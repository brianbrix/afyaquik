package com.afyaquik;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication
//@EntityScan(basePackages = {"com.afyaquik.users.entity", "com.afyaquik.patients.entity"})
//@EnableJpaRepositories(basePackages = {"com.afyaquik.users.repository","com.afyaquik.patients.repository"})
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
@ComponentScan(basePackages = {"com.afyaquik.users","com.afyaquik.patients", "com.afyaquik.web", "com.afyaquik.utils","com.afyaquik.appointments"})
@EnableCaching
public class AfyaquikApplication {

    public static void main(String[] args) {
        SpringApplication.run(AfyaquikApplication.class, args);
    }

}
