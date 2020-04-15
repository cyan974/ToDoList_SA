package com.example.todolist_sa.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.todolist_sa.DTO.ToDo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {

    // Si vous changez le schéma de la base de données, vous devez incrémenter la version de la base de données.
    public static final String DB_NAME = "ToDoList.db";
    public static final int DB_VERSION = 1;

    // Création de la requête SQL pour créer la table TASKS
    private static final String CREATE_ToDo =
            "CREATE TABLE " + Const.ToDoEntry.TABLE_NAME + " (" +
                    Const.ToDoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Const.ToDoEntry.COL_TITLE + " TEXT," +
                    Const.ToDoEntry.COL_ENDDATE + " DATE," +
                    Const.ToDoEntry.COL_IMG + " TEXT " + ")";

    // Création de la requête SQL pour créer la table TAGS
    private static final String CREATE_ToDoItem =
            "CREATE TABLE " + Const.ToDoItemEntry.TABLE_NAME + " (" +
                    Const.ToDoItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Const.ToDoItemEntry.COL_NAME + " TEXT," +
                    Const.ToDoItemEntry.COL_ISCOMPLETED + " BOOLEAN," +
                    Const.ToDoItemEntry.COL_FK_ToDo + " INTEGER NOT NULL CONSTRAINT fk_nn_task REFERENCES " +
                    Const.ToDoEntry.TABLE_NAME + "(" + Const.ToDoEntry._ID + ")" +")";

    // Requête SQL pour supprimer la table TASKS
    private static final String DELETE_ToDo =
            "DROP TABLE IF EXISTS " + Const.ToDoEntry.TABLE_NAME;

    // Requête SQL pour supprimer la table TAGS
    private static final String DELETE_ToDoItem =
            "DROP TABLE IF EXISTS " + Const.ToDoItemEntry.TABLE_NAME;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Exécution des requêtes SQL pour la création des deux tables
        db.execSQL(CREATE_ToDo);
        db.execSQL(CREATE_ToDoItem);
    }

    // Cette base de données n'est qu'un cache pour les données en ligne, sa politique de mise à jour est donc
    // pour simplement se débarrasser des données et recommencer à zéro
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_ToDo);
        db.execSQL(DELETE_ToDoItem);
        onCreate(db);
    }

    // Méthode pour ajouter des tâches dans la table ToDO
    public Boolean addToDo(String title, LocalDate endDate){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Const.ToDoEntry.COL_TITLE, title);
        values.put(Const.ToDoEntry.COL_ENDDATE, endDate.toString());

        Long res = db.insert(Const.ToDoEntry.TABLE_NAME, null, values);

        db.close();
        return res!=-1L;
    }


    public List<ToDo> getListToDo(){
        List<ToDo> listRes = new ArrayList();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor queryRes = db.rawQuery("SELECT * FROM " + Const.ToDoEntry.TABLE_NAME, null);

        if(queryRes.moveToFirst()){
            do{
                ToDo toDo = new ToDo(
                        queryRes.getLong(queryRes.getColumnIndex(Const.ToDoEntry._ID)),
                        queryRes.getString(queryRes.getColumnIndex(Const.ToDoEntry.COL_TITLE)),
                        new Date(Calendar.DATE));
                listRes.add(toDo);

            } while(queryRes.moveToNext());
        }

        queryRes.close();
        db.close();

        return listRes;
    }
}
