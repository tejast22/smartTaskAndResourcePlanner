package com.smartTaskAndResourcePlanner.backendsystem.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name ="tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Task name is required!")
    @Size(min = 3, max = 50, message = "Task name must be between 3 and 50 characters")
    private String title;

    private String status;

    @Min(1)
    @Max(10)
    private int priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User user;

    // FIXED: Changed name to createdAt so it matches all your methods below
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public Task() {}

    public Task(String title, String status, int priority){
        this.title = title;
        this.status = status;
        this.priority = priority;
    }

    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now(); // Works perfectly now!
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getStatus(){
        return status;
    }

    // FIXED: Combined both versions into one clean method that tracks timestamps dynamically!
    public void setStatus(String status) {
        this.status = status;

        if ("completed".equalsIgnoreCase(status)) {
            this.completedAt = LocalDateTime.now();
        } else {
            this.completedAt = null;
        }
    }

    public int getPriority(){
        return priority;
    }

    public void setPriority(int priority){
        this.priority = priority;
    }

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt){
        this.createdAt = createdAt;
    }

    public LocalDateTime getCompletedAt(){
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt){
        this.completedAt = completedAt;
    }
}