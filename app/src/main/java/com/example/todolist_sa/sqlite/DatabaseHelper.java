package com.example.todolist_sa.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Si vous changez le schéma de la base de données, vous devez incrémenter la version de la base de données.
    public static final String DB_NAME = "ToDoList.db";
    public static final int DB_VERSION = 1;

    // Création de la requête SQL pour créer la table TASKS
    private static final String SQL_CREATE_TASKS =
            "CREATE TABLE " + TaskContract.TaskEntry.TABLE_NAME + " (" +
                    TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY," +
                    TaskContract.TaskEntry.COL_TASK_TITLE + " TEXT," +
                    TaskContract.TaskEntry.COL_TASK_DATEFIN + " TEXT," +
                    TaskContract.TaskEntry.COL_TASK_IMG + " TEXT)";

    // Création de la requête SQL pour créer la table TAGS
    private static final String SQL_CREATE_TAGS =
            "CREATE TABLE " + TagContract.TagEntry.TABLE_NAME + " (" +
                    TagContract.TagEntry._ID + " INTEGER PRIMARY KEY," +
                    TagContract.TagEntry.COL_TAG_LIBELLE + " TEXT," +
                    TagContract.TagEntry.COL_FK_TASK + " INTEGER NOT NULL CONSTRAINT fk_nn_task REFERENCES " +
                    TaskContract.TaskEntry.TABLE_NAME + "(" + TaskContract.TaskEntry._ID + ")" +")";

    // Requête SQL pour supprimer la table TASKS
    private static final String SQL_DELETE_TASKS =
            "DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE_NAME;

    // Requête SQL pour supprimer la table TAGS
    private static final String SQL_DELETE_TAGS =
            "DROP TABLE IF EXISTS " + TagContract.TagEntry.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Exécution des requêtes SQL pour la création des deux tables
        db.execSQL(SQL_CREATE_TASKS);
        db.execSQL(SQL_CREATE_TAGS);
    }


    // Cette base de données n'est qu'un cache pour les données en ligne, sa politique de mise à jour est donc
    // pour simplement se débarrasser des données et recommencer à zéro
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TASKS);
        db.execSQL(SQL_DELETE_TAGS);
        onCreate(db);
    }
}
