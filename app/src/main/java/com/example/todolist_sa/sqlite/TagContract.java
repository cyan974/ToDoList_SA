package com.example.todolist_sa.sqlite;

import android.provider.BaseColumns;

public class TagContract {
    // Pour empêcher quelqu'un d'instancier accidentellement la classe de contrat,
    // rendez le constructeur privé.
    private TagContract() {}

    /* Classe intérieure qui définit le contenu de la table */
    public class TagEntry implements BaseColumns {
        public static final String TABLE_NAME = "tags";
        public static final String COL_TAG_LIBELLE = "libelle";
        public static final String COL_FK_TASK = "fk_task";
    }
}
