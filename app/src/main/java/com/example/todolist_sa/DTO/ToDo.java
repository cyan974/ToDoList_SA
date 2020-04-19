package com.example.todolist_sa.DTO;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.todolist_sa.R;

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
    private Integer bgColor;
    private ArrayList<ToDoItem> listItems;
    private ArrayList<Tag> listTags;

    public ToDo(){
        this.numID = null;
        this.title = null;
        this.tagID = null;
        this.endDate = null;
        this.bgColor = R.color.colorWhite;
        this.listItems = new ArrayList<>();
        this.listTags = new ArrayList<>();
    }

    public ToDo(Long numID, Long tag, String title, LocalDate endDate, Integer bgColor) {
        this.numID = numID;
        this.title = title;
        this.tagID = tag;
        this.endDate = endDate;
        this.bgColor = bgColor;
        this.listItems = new ArrayList<>();
        this.listTags = new ArrayList<>();
    }

    public void addItem(ToDoItem item){
        this.listItems.add(item);
    }

    public void addTag(Tag tag) {
        this.listTags.add(tag);
    }

    public void removeTag(Tag tag) {
        this.listTags.remove(tag);
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

    public void setNumID(Long numID) {
        this.numID = numID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTagID(Long tagID) {
        this.tagID = tagID;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setListItems(ArrayList<ToDoItem> listItems) {
        this.listItems = listItems;
    }

    public void setListTags(ArrayList<Tag> listTags) {
        this.listTags = listTags;
    }

    public Long getTagID() {
        return tagID;
    }

    public Integer getBgColor() {
        return bgColor;
    }

    public void setBgColor(Integer bgColor) {
        this.bgColor = bgColor;
    }
}
