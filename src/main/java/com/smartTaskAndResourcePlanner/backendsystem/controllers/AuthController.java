package com.smartTaskAndResourcePlanner.backendsystem.controllers;

import com.smartTaskAndResourcePlanner.backendsystem.models.User;
import com.smartTaskAndResourcePlanner.backendsystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User newUser){
        if(newUser.getUsername() == null || newUser.getUsername().trim().isEmpty() ||
                newUser.getPassword() == null || newUser.getPassword().trim().isEmpty()){
            return ResponseEntity.badRequest().body(Map.of("message","Username and password cannot be empty"));
        }

        Optional<User> existingUser = userRepository.findByUsername(newUser.getUsername().trim());
        if(existingUser.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message","Username already taken"));
        }

        newUser.setUsername(newUser.getUsername().trim());
        String hashedPassword = passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(hashedPassword);

        User savedUser = userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Registration successful",
                "userId", savedUser.getId()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User loginRequest){
        Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername().trim());

        if(userOpt.isPresent()){
            User user = userOpt.get();
            if(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
                return ResponseEntity.ok(Map.of(
                        "message", "Login successful",
                        "userId", user.getId(),
                        "username", user.getUsername()
                ));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid username or password"));
    }

    // Single unified rolling 3-device maximum rotation stack channel
    @PutMapping("/update-fcm-token")
    public ResponseEntity<?> updateFcmToken(@RequestParam Long userId, @RequestParam String token) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isPresent()) {
            User currentUser = userOpt.get();
            List<String> tokens = currentUser.getFcmTokens();

            if (tokens == null) {
                tokens = new ArrayList<>();
            }

            // STACK MANAGEMENT ENGINE: Cleans duplicate rows and kicks out the 4th oldest device
            if (tokens.contains(token)) {
                tokens.remove(token);
            } else {
                if (tokens.size() >= 3) {
                    tokens.remove(0); // Evicts index 0 (the oldest logged-in device session)
                    System.out.println("⚠️ FIFO MAX DEVICE LIMIT reached. Evicted oldest session record.");
                }
            }

            tokens.add(token);
            currentUser.setFcmTokens(tokens);
            userRepository.save(currentUser);

            System.out.println("💾 ROLLING STACK CONFIG: Updated active device tokens array for: " + currentUser.getUsername());
            return ResponseEntity.ok(Map.of("message", "Device token successfully updated within 3-device max limit."));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User tracking ID not found."));
    }
}