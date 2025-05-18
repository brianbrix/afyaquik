package com.afyaquik.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
@ComponentScan(basePackages = {"com.afyaquik.users","com.afyaquik.patients", "com.afyaquik.web", "com.afyaquik.core","com.afyaquik.appointments"})
public class AfyaquikWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(AfyaquikWebApplication.class, args);
    }

}
