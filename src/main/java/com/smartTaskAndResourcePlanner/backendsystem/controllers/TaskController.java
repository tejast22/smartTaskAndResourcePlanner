package com.smartTaskAndResourcePlanner.backendsystem.controllers;

import com.smartTaskAndResourcePlanner.backendsystem.models.Task;
import com.smartTaskAndResourcePlanner.backendsystem.services.TaskService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService){
        this.taskService = taskService;
    }

    // 1. UPDATED: Accepts the user ID header to filter the task list
    @GetMapping
    public List<Task> viewTasks(@RequestHeader(value = "X-User-Id", required = false) Long userId){
        Long targetUserId = (userId != null) ? userId :-1L; // Fallback to test user if guest
        return taskService.getTasksForUser(targetUserId);
    }

    // 2. UPDATED: Accepts the user ID header to save the task under the correct owner
    @PostMapping
    public List<Task> createNewTask(
            @Valid @RequestBody Task newTask,
            @RequestHeader(value = "X-User-Id", required = false) Long userId){

        Long targetUserId = (userId != null) ? userId : 1L;
        taskService.addTask(newTask, targetUserId);
        return taskService.getTasksForUser(targetUserId); // Return only this user's updated list!
    }

    // 3. UPDATED: Passing the header isn't strictly needed for status changes or deletes yet,
    // but we change the return statement so they also return just the filtered list!
    @PutMapping("/{id}/status")
    public List<Task> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestHeader(value = "X-User-Id", required = false) Long userId){

        taskService.updateTaskStatus(id, status);
        Long targetUserId = (userId != null) ? userId : 1L;
        return taskService.getTasksForUser(targetUserId);
    }

    @DeleteMapping("/{id}")
    public List<Task> deleteTask(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId){

        taskService.deleteId(id);
        Long targetUserId = (userId != null) ? userId : 1L;
        return taskService.getTasksForUser(targetUserId);
    }
}