package com.smartTaskAndResourcePlanner.backendsystem.models;

import jakarta.persistence.*;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    // MULTI-DEVICE UPGRADE: Automatically maps a hidden child collection table in Supabase
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_fcm_tokens", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "token", length = 512)
    private Set<String> fcmTokens = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> tasks;

    public User() {}

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    public Long getId(){ return id;}
    public void setId(Long id){ this.id = id;}
    public String getUsername(){return username;}
    public void setUsername(String username){this.username = username;}
    public String getPassword(){return password;}
    public void setPassword(String password){this.password = password;}

    // Updated Getters and Setters for Collection Token Set
    public Set<String> getFcmTokens(){return fcmTokens;}
    public void setFcmTokens(Set<String> fcmTokens){this.fcmTokens = fcmTokens;}

    public List<Task> getTasks(){return tasks;}
    public void setTasks(List<Task> tasks){this.tasks = tasks;}
}