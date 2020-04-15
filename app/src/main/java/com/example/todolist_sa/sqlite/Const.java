package com.example.todolist_sa.sqlite;

import android.provider.BaseColumns;

public class Const {
    // Pour empêcher quelqu'un d'instancier accidentellement la classe de constante,
    // on rend le constructeur privé.
    private Const() {}

    /* Classe intérieure qui définit le contenu de la table ToDo */
    public class ToDoEntry implements BaseColumns {
        public static final String TABLE_NAME = "tasks";
        public static final String COL_TITLE = "title";
        public static final String COL_ENDDATE = "endDate";
        public static final String COL_IMG = "img";
    }

    /* Classe intérieure qui définit le contenu de la table ToDoItem */
    public class ToDoItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "tags";
        public static final String COL_FK_ToDo = "fk_todo";
        public static final String COL_NAME = "name";
        public static final String COL_ISCOMPLETED = "isCompleted";
    }
}
