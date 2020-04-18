package com.example.todolist_sa.DTO;

public class Tag {
    private Long numID;
    private String libelle;

    public Tag(Long numID, String libelle) {
        this.numID = numID;
        this.libelle = libelle;
    }

    public Long getNumID() {
        return numID;
    }

    public void setNumID(Long numID) {
        this.numID = numID;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
}
