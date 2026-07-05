package com.smartTaskAndResourcePlanner.backendsystem.controllers;

import com.smartTaskAndResourcePlanner.backendsystem.models.User;
import com.smartTaskAndResourcePlanner.backendsystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    //registration endpoint
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User newUser){
        //clean inputs
        if(newUser.getUsername() == null || newUser.getUsername().trim().isEmpty() ||
                newUser.getPassword() == null || newUser.getPassword().trim().isEmpty()){
            return ResponseEntity.badRequest().body(Map.of("message","Username and password cannot be empty"));
        }

        //checking for username is already exist in supabase
        Optional<User> existingUser = userRepository.findByUsername(newUser.getUsername().trim());
        if(existingUser.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message","Username already taken"));
        }

        //saving the new user
        newUser.setUsername(newUser.getUsername().trim());
        User savedUser = userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Registration successful",
                "userId",savedUser.getId()
        ));
    }

    //login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User loginRequest){
        //look for user by username
        Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername().trim());

        if(userOpt.isPresent()){
            User user = userOpt.get();
            //check for password matching
            if(user.getPassword().equals(loginRequest.getPassword())){
                return ResponseEntity.ok(Map.of(
                        "message", "Login successful",
                        "userId",user.getId(),
                        "username", user.getUsername()
                ));
            }
        }
        //if username do not exist or password do not match return an unauthorized status
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid username or password"));
    }
}
