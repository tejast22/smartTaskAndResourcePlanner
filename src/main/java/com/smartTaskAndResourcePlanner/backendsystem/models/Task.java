package com.smartTaskAndResourcePlanner.backendsystem.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
}
