package com.smartTaskAndResourcePlanner.backendsystem.repositories;

import com.smartTaskAndResourcePlanner.backendsystem.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>{
    //spring boot automatically converts this method to sql query
    //select * from tasks where userid = ?
    List<Task> findByUserId(Long userId);
    List<Task> findByStatusInAndReminderSentFalseAndDueDateLessThanEqual(List<String> statuses, LocalDateTime now);
}
