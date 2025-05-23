package com.afyaquik.afyaquik;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"com.afyaquik.users.entity", "com.afyaquik.patients.entity"})
@EnableJpaRepositories(basePackages = {"com.afyaquik.users.repository","com.afyaquik.patients.repository"})
public class AfyaquikApplication {

    public static void main(String[] args) {
        SpringApplication.run(AfyaquikApplication.class, args);
    }

}
