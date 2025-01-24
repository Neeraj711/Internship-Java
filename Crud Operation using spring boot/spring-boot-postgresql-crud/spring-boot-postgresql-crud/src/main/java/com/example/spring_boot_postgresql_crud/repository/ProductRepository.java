package com.example.spring_boot_postgresql_crud.repository;
import com.example.spring_boot_postgresql_crud.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductRepository extends JpaRepository <Product,Long>{

}
