package com.smartTaskAndResourcePlanner.backendsystem.models;

public class Node {
    //the payload the data this node holds
    private Task task;

    //the  pointer(reference to another node)
    private Node next;

    //constructor
    //when creating new node we pass it the task it should hold
    public Node(Task task){
        this.task = task;
        this.next = null; //by default new node does not point to anything
    }

    //getters and setterss
    public Task getTask(){
        return task;
    }

    public Node getNext(){
        return next;
    }

    public void setNext(Node next){
        this.next = next;
    }
}
