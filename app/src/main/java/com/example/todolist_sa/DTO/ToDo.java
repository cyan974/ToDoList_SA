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
    private ArrayList<Tag> listTags;

    public ToDo(Long numID, Long tag, String title, LocalDate endDate) {
        this.numID = numID;
        this.title = title;
        this.tagID = tag;
        this.endDate = endDate;
        this.listItems = new ArrayList<>();
        this.listTags = new ArrayList<>();
    }

    public ToDo(String title, LocalDate endDate, ArrayList<ToDoItem> listItems){
        this.numID = null;
        this.title = title;
        this.tagID = null;
        this.endDate = endDate;
        this.listItems = listItems;
        this.listTags = new ArrayList<>();
    }

    public void addItem(ToDoItem item){
        this.listItems.add(item);
    }

    public void addTag(Tag tag) {
        this.listTags.add(tag);
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

    public List<Tag> getListTags(){
        return this.listTags;
    }
}
