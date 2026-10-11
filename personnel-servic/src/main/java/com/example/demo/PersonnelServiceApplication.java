package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.demo", "com.vit"})
public class PersonnelServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PersonnelServiceApplication.class, args);
    }
}