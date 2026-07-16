package com.smartTaskAndResourcePlanner.backendsystem.services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.smartTaskAndResourcePlanner.backendsystem.models.Task;
import com.smartTaskAndResourcePlanner.backendsystem.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

@Component
@EnableScheduling
public class EmailScheduler {

    private final TaskRepository taskRepository;

    @Autowired
    public EmailScheduler(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @PostConstruct
    public void initializeFirebase() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream serviceAccount;

                String firebaseConfigEnv = System.getenv("FIREBASE_CREDENTIALS_JSON");

                if (firebaseConfigEnv != null && !firebaseConfigEnv.trim().isEmpty()) {
                    serviceAccount = new java.io.ByteArrayInputStream(
                            firebaseConfigEnv.getBytes(java.nio.charset.StandardCharsets.UTF_8)
                    );
                    System.out.println("☁️ PRODUCTION: Loading Firebase credentials securely from environment variable.");
                } else {
                    serviceAccount = new ClassPathResource("firebase-service-account.json").getInputStream();
                    System.out.println("💻 LOCAL: Loading Firebase credentials from local resources file.");
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                System.out.println("🚀 SYSTEM INITIALIZATION: Firebase Admin SDK initialized successfully!");
            }
        } catch (Exception e) {
            System.err.println("❌ CRITICAL ERROR: Failed to initialize Firebase SDK: " + e.getMessage());
        }
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkAndSendReminders() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("\n==================================================");
        System.out.println("⏰ SCHEDULER BEAT: Checking alarms at: " + now);

        List<Task> allTasks = taskRepository.findAll();
        System.out.println("📊 DATABASE CHECK: Found total tasks: " + allTasks.size());

        for (Task t : allTasks) {
            System.out.println("👉 Task: [" + t.getTitle() + "] | Status: [" + t.getStatus() + "] | Due: [" + t.getDueDate() + "] | Sent: [" + t.isReminderSent() + "]");
        }

        List<Task> tasksToAlert = allTasks.stream()
                .filter(task -> "pending".equalsIgnoreCase(task.getStatus()) || "working".equalsIgnoreCase(task.getStatus()))
                .filter(task -> !task.isReminderSent())
                .filter(task -> task.getDueDate() != null && !now.isBefore(task.getDueDate()))
                .toList();

        System.out.println("🎯 FILTERS APPLIED: Tasks matching criteria to send right now: " + tasksToAlert.size());
        System.out.println("==================================================\n");

        for (Task task : tasksToAlert) {
            if (task.getUser() != null) {
                String username = task.getUser().getUsername();

                // Read the whole set collection of active device addresses
                java.util.Set<String> tokens = task.getUser().getFcmTokens();

                System.out.println(">>> MATCH FOUND: Preparing multi-device push dispatch for: " + task.getTitle());

                if (tokens == null || tokens.isEmpty()) {
                    System.out.println("⚠️ NOTICE: User '" + username + "' has no registered device tokens active.");
                    continue;
                }

                // DYNAMIC MULTI-CAST: Fire the notification engine to every screen concurrently!
                for (String token : tokens) {
                    try {
                        com.google.firebase.messaging.Message message = com.google.firebase.messaging.Message.builder()
                                .setToken(token)
                                .setNotification(com.google.firebase.messaging.Notification.builder()
                                        .setTitle("⏰ Task Reminder: " + task.getTitle())
                                        .setBody("Your task requires active progress attention.")
                                        .build())
                                .build();

                        String response = com.google.firebase.messaging.FirebaseMessaging.getInstance().send(message);
                        System.out.println("🎉 DISPATCH SUCCESS: Sent to device token segment. Msg ID: " + response);

                    } catch (Exception e) {
                        System.err.println("❌ DEVICE DELIVERY FAILURE: Token signature invalid or expired: " + e.getMessage());
                    }
                }

                // Mark the task reminder loop flag as complete after hitting all devices
                task.setReminderSent(true);
                taskRepository.save(task);
            }
        }
    }
}