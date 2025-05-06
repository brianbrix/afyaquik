package com.afyaquik.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.afyaquik.users", "com.afyaquik.web", "com.afyaquik.core"})
public class AfyaquikWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(AfyaquikWebApplication.class, args);
    }

}
