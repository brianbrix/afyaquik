package com.afyaquik.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class AfyaquikUsersApplication {

    public static void main(String[] args) {
        SpringApplication.run(AfyaquikUsersApplication.class, args);
    }

}
