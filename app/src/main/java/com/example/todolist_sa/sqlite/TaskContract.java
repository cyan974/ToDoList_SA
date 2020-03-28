package com.example.todolist_sa.sqlite;

import android.provider.BaseColumns;

public final class TaskContract {
    // Pour empêcher quelqu'un d'instancier accidentellement la classe de contrat,
    // rendez le constructeur privé.
    private TaskContract(){}

    /* Classe intérieure qui définit le contenu de la table */
    public class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "tasks";
        public static final String COL_TASK_TITLE = "title";
        public static final String COL_TASK_DATEFIN = "date_fin";
        public static final String COL_TASK_IMG = "img";
    }
}
