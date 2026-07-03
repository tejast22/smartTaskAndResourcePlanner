package com.smartTaskAndResourcePlanner.backendsystem.services;

import com.smartTaskAndResourcePlanner.backendsystem.models.Task;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {
    private List<Task> taskList = new ArrayList<>();

    public TaskService(){
        taskList.add(new Task("T1","Java Project Staring","Pending",1));
        taskList.add(new Task("T2","Day 2 work Started","working",2));
    }

    public void addTask(Task task){
        taskList.add(task);
    }

    public List<Task> getTheList(){
        return taskList;
    }

    public Task searchId(String id){
        for(Task task:taskList){
            if(id.equals(task.getId())){
                return task;
            }
        }
        return null;
    }

    public void deleteId(String id){
        for(int i=0;i<taskList.size();i++){
            if(taskList.get(i).getId().equals(id)){
                taskList.remove(i);
                break;
            }
        }
    }

    public void sortTasksByPriority(List<Task> tasks){
        int n = tasks.size();
        for(int i=0;i<n-1;i++){
            for(int j=0;j<n-i-1;j++){
                if(tasks.get(j).getPriority() < tasks.get(j+1).getPriority()){
                    Task temp = tasks.get(j);
                    tasks.set(j, tasks.get(j+1));
                    tasks.set(j+1,temp);
                }
            }
        }
    }

    //button for making changes in working pending completed
    public void updateTaskStatus(String id, String newStatus){
        Task task = searchId(id);
        if(task != null){
            task.setStatus(newStatus);
        }
    }
}
