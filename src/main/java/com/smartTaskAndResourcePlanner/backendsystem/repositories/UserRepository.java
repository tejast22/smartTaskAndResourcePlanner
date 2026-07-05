package com.smartTaskAndResourcePlanner.backendsystem.repositories;

import com.smartTaskAndResourcePlanner.backendsystem.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //this helps to search for user by their username when logging in
    Optional<User> findByUsername(String username);
}
