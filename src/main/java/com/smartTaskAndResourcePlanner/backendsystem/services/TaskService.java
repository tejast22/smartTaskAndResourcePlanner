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
        // Data now lives permanently in PostgreSQL (Supabase)!

        this.userRepository = userRepository;
    }

    public Task addTask(Task task){
        //search for user id if they don't exist create a default user automatically
        User testUser = userRepository.findById(1L).orElseGet(() -> {
            User defaultUser = new User("testuser", "password123");
            return userRepository.save(defaultUser);
        });

        //attaching user to incoming task
        task.setUser(testUser);

        //saving the task and returing it back to the controller
        return taskRepository.save(task);
    }

    public List<Task> getTheList(){
            List<Task> allTasks = taskRepository.findAll();

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

    // FIX 1: Changed String to Long
    public Task searchId(Long id){
        // JpaRepository returns an Optional to prevent NullPointerExceptions
        return taskRepository.findById(id).orElse(null);
    }

    // FIX 2: Changed String to Long
    public void deleteId(Long id){
        taskRepository.deleteById(id);
    }

    public List<Task> getSortedTasks(){
        // The database handles the sorting natively and efficiently!
        return taskRepository.findAll(Sort.by(Sort.Direction.DESC, "priority"));
    }

    // FIX 3: Changed String to Long
    public Task updateTaskStatus(Long id, String newStatus){
        Task task = searchId(id);
        if(task != null){
            task.setStatus(newStatus);
            return taskRepository.save(task); // We must save the changes back to the database
        }
        return null;
    }
}