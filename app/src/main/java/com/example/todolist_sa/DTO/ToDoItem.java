package com.example.todolist_sa.DTO;

import java.io.Serializable;

public class ToDoItem implements Serializable {
    private Long numID;
    private Long idToDo;
    private String name;
    private Boolean isCompleted;

    public ToDoItem(Long numID, Long idToDo, String name, Boolean isCompleted){
        this.numID = numID;
        this.idToDo = idToDo;
        this.name = name;
        this.isCompleted = isCompleted;
    }

    public ToDoItem(String name){
        this.numID = null;
        this.idToDo = null;
        this.name = name;
        this.isCompleted = null;
    }

    public Long getNumID() {
        return numID;
    }

    public Long getIdToDo() {
        return idToDo;
    }

    public String getName() {
        return name;
    }

    public Boolean getCompleted() {
        return isCompleted;
    }

    public void setCompleted(Boolean completed) {
        isCompleted = completed;
    }

    public void setNumID(Long numID) {
        this.numID = numID;
    }

    public void setIdToDo(Long idToDo) {
        this.idToDo = idToDo;
    }

    public void setName(String name) {
        this.name = name;
    }
}
