package com.smartTaskAndResourcePlanner.backendsystem.services;

import com.smartTaskAndResourcePlanner.backendsystem.models.Task;
import com.smartTaskAndResourcePlanner.backendsystem.repositories.TaskRepository;
import com.smartTaskAndResourcePlanner.backendsystem.models.User;
import com.smartTaskAndResourcePlanner.backendsystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Task addTask(Task task, Long userId){
        User testUser = userRepository.findById(userId).orElseGet(() -> {
            User defaultUser = new User("testuser", "password123");
            return userRepository.save(defaultUser);
        });

        task.setUser(testUser);
        return taskRepository.save(task);
    }

    public List<Task> getTasksForUser(Long userId){
        List<Task> allTasks = taskRepository.findByUserId(userId);

        return allTasks.stream()
                .sorted(java.util.Comparator
                        .comparing((Task task) -> task.getStatus().equalsIgnoreCase("Completed") ? 1 : 0)
                        .thenComparing(java.util.Comparator.comparingInt(Task::getPriority).reversed())
                        .thenComparing(Task::getId))
                .toList();
    }

    public Task searchId(Long id){
        return taskRepository.findById(id).orElse(null);
    }

    public void deleteId(Long id){
        taskRepository.deleteById(id);
    }

    public List<Task> getSortedTasks(){
        return taskRepository.findAll(Sort.by(Sort.Direction.DESC, "priority"));
    }

    public Task updateTaskStatus(Long id, String newStatus){
        Task task = searchId(id);
        if(task != null){
            task.setStatus(newStatus);
            return taskRepository.save(task);
        }
        return null;
    } // FIXED: Closed this brace properly!

    public Task updateTaskDueDate(Long id, String newDateStr) {
        Task task = searchId(id);
        if (task != null) {
            if (newDateStr != null && !newDateStr.trim().isEmpty()) {
                task.setDueDate(java.time.LocalDateTime.parse(newDateStr));
                task.setReminderSent(false);
                return taskRepository.save(task);
            }
        }
        return null;
    }
}