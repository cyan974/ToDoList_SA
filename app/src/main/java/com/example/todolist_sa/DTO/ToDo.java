package com.example.todolist_sa.DTO;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ToDo implements Serializable {
    private Long numID;
    private String title;
    private Long tagID;
    private LocalDate endDate;
    private ArrayList<ToDoItem> listItems;

    public ToDo(Long numID, Long tag, String title, LocalDate endDate) {
        this.numID = numID;
        this.title = title;
        this.tagID = tag;
        this.endDate = endDate;
        this.listItems = new ArrayList<>();
    }

    public void addItems(ToDoItem item){
        this.listItems.add(item);
    }

    public Long getNumID() {
        return this.numID;
    }

    public String getTitle() {
        return this.title;
    }

    public Long getTag(){return this.tagID;}

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public List<ToDoItem> getListItems() {
        return this.listItems;
    }
}
