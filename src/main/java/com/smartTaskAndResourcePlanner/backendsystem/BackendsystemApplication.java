package com.smartTaskAndResourcePlanner.backendsystem;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.smartTaskAndResourcePlanner.backendsystem.models.Task;
import com.smartTaskAndResourcePlanner.backendsystem.services.TaskService;

@SpringBootApplication
public class BackendsystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendsystemApplication.class, args);
	}
	@Bean
	public CommandLineRunner runTest(TaskService taskService) {
		return args -> {
			System.out.println("========== MEMORY BANK TEST START ==========");

			// 1. Print the dummy tasks from your constructor
			System.out.println("Initial Tasks in Memory:");
			for (Task t : taskService.getTheList()) {
				System.out.println(t.getId() + " - " + t.getTitle() + " (" + t.getStatus() + ")");
			}

			// 2. Test the Create algorithm
			System.out.println("\nAdding a new task...");
			taskService.addTask(new Task("T3", "Build Web Controller", "Pending",2));

			// 3. Test the Read algorithm again to prove it saved
			System.out.println("\nUpdated Tasks in Memory:");
			for (Task t : taskService.getTheList()) {
				System.out.println(t.getId() + " - " + t.getTitle() + " (" + t.getStatus() + ")");
			}

			System.out.println("========== MEMORY BANK TEST COMPLETE ==========");
		};

	}
}
