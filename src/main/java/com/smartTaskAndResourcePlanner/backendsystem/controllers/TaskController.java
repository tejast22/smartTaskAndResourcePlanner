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
    public List<Task> viewTasks(){
        return taskService.getTheList();
    }

    @PostMapping
    public List<Task> createNewTask(@Valid @RequestBody Task newTask){
        taskService.addTask(newTask);
        return taskService.getTheList();
    }

    /* * TEMPORARILY DISABLED: Migrating away from in-memory TaskHistoryList.
     * Will rebuild using a database-backed Audit Log in the future.
     *
    @PostMapping("/undo")
    public List<Task> undoLastAction(){
        // Logic to be rebuilt
        return taskService.getTheList();
    }
    */

//    @GetMapping("/sort")
//    public List<Task> getSortedTasks(){
//        // Notice we changed this to call the new service method directly
//        return taskService.getSortedTasks();
//    }

    // FIX: Changed @PathVariable String id to @PathVariable Long id
    @PutMapping("/{id}/status")
    public List<Task> updateStatus(@PathVariable Long id, @RequestParam String status){
        taskService.updateTaskStatus(id, status);
        return taskService.getTheList();
    }

    //to delete the row or task
    @DeleteMapping("/{id}")
    public List<Task> deleteTask(@PathVariable Long id){
        taskService.deleteId(id);
        return taskService.getTheList();
    }
}