package com.example.todolist_sa.sqlite;

import android.provider.BaseColumns;

public class Const {
    // Pour empêcher quelqu'un d'instancier accidentellement la classe de constante,
    // on rend le constructeur privé.
    private Const() {}

    /* Classe intérieure qui définit le contenu de la table ToDo */
    public class TodoEntry implements BaseColumns {
        public static final String TABLE_NAME = "todo";
        public static final String COL_TITLE = "title";
        public static final String COL_ENDDATE = "endDate";
        public static final String COL_IMG = "imgPath";
        public static final String COL_BGCOLOR = "color";
    }

    /* Classe intérieure qui définit le contenu de la table ToDoItem */
    public class TodoItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "todoitem";
        public static final String COL_FK_TODO = "fk_todo";
        public static final String COL_NAME = "name";
        public static final String COL_ISCOMPLETED = "isCompleted";
    }

    /* Classe intérieure qui définit le contenu de la table Tags */
    public class TagsEntry implements BaseColumns {
        public static final String TABLE_NAME = "tags";
        public static final String COL_LIBELLE = "libelle";
    }

    /* Classe intérieure qui définit le contenu de la table Tag_TODO */
    public class TagTodoEntry implements BaseColumns {
        public static final String TABLE_NAME = "tagtodo";
        public static final String COL_FK_TAG = "fk_tag";
        public static final String COL_FK_TODO = "fk_todo";
    }
}
