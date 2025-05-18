package com.afyaquik.patients;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;


@SpringBootApplication
@ComponentScan(basePackages={"com.afyaquik.patients","com.afyaquik.users"})
@EnableJpaRepositories
public class AfyaquikPatientsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AfyaquikPatientsApplication.class, args);
    }

}
