package com.example.todolist_sa.DTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ToDo {
    private Long numID;
    private String title;
    private Date endDate;
    private List<ToDoItem> listItems;

    public ToDo(Long numID, String title, Date endDate) {
        this.numID = numID;
        this.title = title;
        this.endDate = endDate;
        this.listItems = new ArrayList<>();
    }

    public ToDo(String title, Date endDate) {
        this.numID = -1L;
        this.title = title;
        this.endDate = endDate;
        this.listItems = new ArrayList<>();
    }

    public void addItems(ToDoItem item){
        this.listItems.add(item);
    }

    public Long getNumID() {
        return numID;
    }

    public String getTitle() {
        return title;
    }

    /*public Date getEndDate() {
        return endDate;
    }*/

    public List<ToDoItem> getListItems() {
        return listItems;
    }


}
