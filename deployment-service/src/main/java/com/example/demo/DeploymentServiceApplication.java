package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.example.demo", "com.vit"})
public class DeploymentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeploymentServiceApplication.class, args);
    }
}