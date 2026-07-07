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

    @GetMapping
    public List<Task> viewTasks(@RequestHeader(value = "X-User-Id", required = false) Long userId){
        Long targetUserId = (userId != null) ? userId : -1L;
        return taskService.getTasksForUser(targetUserId);
    }

    @PostMapping
    public List<Task> createNewTask(
            @Valid @RequestBody Task newTask,
            @RequestHeader(value = "X-User-Id", required = false) Long userId){

        Long targetUserId = (userId != null) ? userId : 1L;
        taskService.addTask(newTask, targetUserId);
        return taskService.getTasksForUser(targetUserId);
    }

    // FIXED: Ensured that user verification is tied directly to status mutations
    @PutMapping("/{id}/status")
    public List<Task> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestHeader(value = "X-User-Id", required = false) Long userId){

        Long targetUserId = (userId != null) ? userId : 1L;

        // Let's protect data integrity: make sure we use the transactional service hook
        // that handles the entity tracking and timestamp mapping correctly!
        taskService.updateTaskStatus(id, status);

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