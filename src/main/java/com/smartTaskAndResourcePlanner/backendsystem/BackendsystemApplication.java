package com.smartTaskAndResourcePlanner.backendsystem;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.smartTaskAndResourcePlanner.backendsystem.models.Task;
import com.smartTaskAndResourcePlanner.backendsystem.services.TaskService;

@SpringBootApplication
public class BackendsystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendsystemApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}
}
