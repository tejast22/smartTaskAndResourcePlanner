package com.smartTaskAndResourcePlanner.backendsystem.models;

import jakarta.persistence.*;
import java.util.List;

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

    //it links users to its tasks
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> tasks;

    //constructors
    public User() {}

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    //getters and setters
    public Long getId(){ return id;}
    public void setId(Long id){ this.id = id;}
    public String getUsername(){return username;}
    public void setUsername(String username){this.username = username;}
    public String getPassword(){return password;}
    public void setPassword(String password){this.password = password;}
    public List<Task> getTasks(){return tasks;}
    public void setTasks(List<Task> tasks){this.tasks = tasks;}

}
