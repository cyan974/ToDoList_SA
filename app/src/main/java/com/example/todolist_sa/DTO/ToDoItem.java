package com.example.todolist_sa.DTO;

public class ToDoItem {
    private Long numID;
    private Long idToDo;
    private String name;
    private Boolean isCompleted;

    public ToDoItem(Long numID, Long idToDo, String name){
        this.numID = numID;
        this.idToDo = idToDo;
        this.name = name;
        this.isCompleted = false;
    }

    public ToDoItem(Long idToDo, String name){
        this.numID = -1L;
        this.idToDo = idToDo;
        this.name = name;
        this.isCompleted = false;
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
}
