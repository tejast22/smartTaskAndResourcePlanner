package com.smartTaskAndResourcePlanner.backendsystem.services;

import com.smartTaskAndResourcePlanner.backendsystem.models.Node;
import com.smartTaskAndResourcePlanner.backendsystem.models.Task;
import org.springframework.stereotype.Service;

@Service
public class TaskHistoryList {
    //tracking top node
    private Node head;

    //constructor when application starts the stack is completely empty
    public TaskHistoryList(){
        this.head = null; //no nodes exist yet so head points null
    }

    //entering the data
    public void push(Task newTask){
        // wrap your task payload inside a brand new train car
        Node newNode = new Node(newTask);

        //connect the new car to the current top of the stack
        newNode.setNext(head);

        //move the anchor the new car is now the official top of the stack
        head = newNode;
    }

    //poping the data
    public Task pop(){
        //checking list is empty
        if(head == null){
            return null;
        }

        //put the task payload somewhere safe before moving references
        Task removedTask = head.getTask();

        //move the head pointer to the next node in line
        head = head.getNext();

        //return the task so the controller can process the undo action
        return removedTask;
    }
}
