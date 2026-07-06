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

    // 1. FIXED: Accepts the specific user ID now
    public Task addTask(Task task, Long userId){
        User testUser = userRepository.findById(userId).orElseGet(() -> {
            User defaultUser = new User("testuser", "password123");
            return userRepository.save(defaultUser);
        });

        task.setUser(testUser);
        return taskRepository.save(task);
    }

    // 2. FIXED: Filters rows by the logged-in user before sorting
    public List<Task> getTasksForUser(Long userId){
        List<Task> allTasks = taskRepository.findByUserId(userId);

        return allTasks.stream()
                .sorted(java.util.Comparator
                        // Rule 1: Sinks "Completed" tasks to the bottom (Weight 1 vs Weight 0)
                        .comparing((Task task) -> task.getStatus().equalsIgnoreCase("Completed") ? 1 : 0)
                        // Rule 2: Sorts the remaining active tasks by Priority (10 down to 1)
                        .thenComparing(java.util.Comparator.comparingInt(Task::getPriority).reversed())
                        // Rule 3: If priorities tie, keep them in creation order (by ID)
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
    }
}