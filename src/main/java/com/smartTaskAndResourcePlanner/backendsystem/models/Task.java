package com.smartTaskAndResourcePlanner.backendsystem.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name ="tasks")//tells mongodb to store this in task
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)// maps this field to mongo id
    private Long id;

    @NotBlank(message = "Task name is required!")
    @Size(min = 3, max = 50, message = "Task name must be between 3 and 50 characters")
    private String title;
    private String status;

    @Min(1)
    @Max(10)
    private int priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // creates user id with foreign key
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User user;

    public Task() {}

    public Task(String title, String status, int priority){
        this.title = title;
        this.status = status;
        this.priority = priority;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){this.id = id;
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

    public void setStatus(String status){
        this.status = status;
    }

    public int getPriority(){
        return priority;
    }

    public void setPriority(int priority){
        this.priority = priority;
    }

    public User getUser(){return user;}
    public void setUser(User user){this.user = user;}
}
