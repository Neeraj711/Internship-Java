package Gmail.GmailCsvApplication.service;

import Gmail.GmailCsvApplication.entity.User;
import Gmail.GmailCsvApplication.repository.UserRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
//import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import javax.mail.search.SubjectTerm;
@Service
public class GmailService {

    @Autowired
    private UserRepository userRepository;

    public void fetchAndProcessEmails() {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imaps.host", "imap.gmail.com");
        properties.put("mail.imaps.port", "993");

        try {
            Session session = Session.getDefaultInstance(properties, null);
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", "neerajmalviya7746@gmail.com", "gnsd rtks wqza rvmt");

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            // Fetch emails with subject "xyz"
            Message[] messages = inbox.search(new SubjectTerm("CSV"));
            for (Message message : messages) {
                processMessage(message);
                moveMessageToFolder(message, inbox, "Processed");
            }

            inbox.close(false);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processMessage(Message message) throws Exception {
        if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            for (int i = 0; i < mimeMultipart.getCount(); i++) {
                BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                if (bodyPart.getFileName() != null && bodyPart.getFileName().endsWith(".csv")) {
                    InputStream inputStream = bodyPart.getInputStream();
                    parseAndSaveCSV(inputStream);
                }
            }
        }
    }

    private void parseAndSaveCSV(InputStream inputStream) throws IOException {
        CSVParser csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(new InputStreamReader(inputStream));
        for (CSVRecord record : csvParser) {
            String email = record.get("email");
            String name = record.get("name");

            if (!userRepository.existsByEmail(email)) {
                User user = new User();
                user.setEmail(email);
                user.setName(name);
                userRepository.save(user);
            }
        }
    }

    private void moveMessageToFolder(Message message, Folder sourceFolder, String folderName) throws Exception {
        Folder destinationFolder = sourceFolder.getStore().getFolder(folderName);
        if (!destinationFolder.exists()) {
            destinationFolder.create(Folder.HOLDS_MESSAGES);
        }
        sourceFolder.copyMessages(new Message[]{message}, destinationFolder);
        message.setFlag(Flags.Flag.DELETED, true);
    }
}