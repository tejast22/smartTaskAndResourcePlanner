package com.smartTaskAndResourcePlanner.backendsystem.models;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public class Task {
    private String id;
    private String title;
    private String status;

    @Min(1)
    @Max(10)
    private int priority;

    public Task() {}

    public Task(String id, String title, String status, int priority){
        this.id = id;
        this.title = title;
        this.status = status;

        this.priority = priority;
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
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
