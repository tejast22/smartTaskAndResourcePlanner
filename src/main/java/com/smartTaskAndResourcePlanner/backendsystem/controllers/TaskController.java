package com.smartTaskAndResourcePlanner.backendsystem.controllers;

import com.smartTaskAndResourcePlanner.backendsystem.models.Task;
import com.smartTaskAndResourcePlanner.backendsystem.services.TaskService;
import com.smartTaskAndResourcePlanner.backendsystem.services.TaskHistoryList;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    private final TaskService taskService;
    private final TaskHistoryList taskHistoryList;

    @Autowired
    public TaskController(TaskService taskService, TaskHistoryList taskHistoryList){

        this.taskService = taskService;
        this.taskHistoryList = taskHistoryList;
    }

    @CrossOrigin(origins="*")
    @GetMapping
    public List<Task> viewTasks(){
        return taskService.getTheList();
    }

    @PostMapping
    public List<Task> createNewTask(@Valid @RequestBody Task newTask){
        //record the action into our linked list history stack first
        taskHistoryList.push(newTask);

        //add it to the active system arrayList
        taskService.addTask(newTask);

        //return the updated list back to the browser dashboard
        return taskService.getTheList();
    }

    //undo endpoint
    @PostMapping("/undo")
    public List<Task> undoLastAction(){
        //pop the most recent task from the history stack
        Task lastActionTask = taskHistoryList.pop();

        //check if there was actually a task to undo
        if(lastActionTask != null){
            //since the last action was creating this task, undion it means
            //deleting it
            taskService.deleteId(lastActionTask.getId());
        }

        //return the modified list back to the frontend to refresh the dashboard
        return taskService.getTheList();
    }

    @GetMapping("/sort")
    public List<Task> getSortedTasks(){
        //get the current list
        List<Task> currentList = taskService.getTheList();

        //perform manual sorting
        taskService.sortTasksByPriority(currentList);

        //return the newly sorted list
        return currentList;
    }

    //endpoint for task changing from pending working completed
    @PutMapping("/{id}/status")
    public List<Task> updateStatus(@PathVariable String id, @RequestParam String status){
        taskService.updateTaskStatus(id, status);
        return taskService.getTheList();
    }
}
