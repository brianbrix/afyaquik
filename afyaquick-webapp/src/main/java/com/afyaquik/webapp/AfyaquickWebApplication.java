package com.afyaquik.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.afyaquik")
//@Import(SecurityConfig.class)
public class AfyaquickWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(AfyaquickWebApplication.class, args);
	}

}
