package Gmail.GmailCsvApplication.schedule;

import Gmail.GmailCsvApplication.service.GmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EmailScheduler {

    @Autowired
    private GmailService gmailService;

    @Scheduled(fixedRate = 60000) // Run every 60 seconds
    public void fetchEmails() {
        gmailService.fetchAndProcessEmails();
    }
}