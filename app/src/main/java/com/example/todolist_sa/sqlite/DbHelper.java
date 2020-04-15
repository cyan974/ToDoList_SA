package com.example.todolist_sa.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.todolist_sa.DTO.ToDo;
import com.example.todolist_sa.DTO.ToDoItem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {
    SQLiteDatabase db;

    // Si vous changez le schéma de la base de données, vous devez incrémenter la version de la base de données.
    public static final String DB_NAME = "ToDoList.db";
    public static final int DB_VERSION = 1;

    // Création de la requête SQL pour créer la table ToDo
    private static final String CREATE_ToDo =
            "CREATE TABLE " + Const.ToDoEntry.TABLE_NAME + " (" +
                    Const.ToDoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Const.ToDoEntry.COL_TITLE + " TEXT NOT NULL UNIQUE," +
                    Const.ToDoEntry.COL_FK_TAG + " INTEGER," +
                    Const.ToDoEntry.COL_ENDDATE + " DATE," +
                    Const.ToDoEntry.COL_IMG + " TEXT " + ")";

    // Création de la requête SQL pour créer la table ToDoItem
    private static final String CREATE_ToDoItem =
            "CREATE TABLE " + Const.ToDoItemEntry.TABLE_NAME + " (" +
                    Const.ToDoItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Const.ToDoItemEntry.COL_NAME + " TEXT," +
                    Const.ToDoItemEntry.COL_ISCOMPLETED + " BOOLEAN," +
                    Const.ToDoItemEntry.COL_FK_ToDo + " INTEGER NOT NULL CONSTRAINT fk_nn_task REFERENCES " +
                    Const.ToDoEntry.TABLE_NAME + "(" + Const.ToDoEntry._ID + ")" +")";

    // Création de la requête SQL pour créer la table ToDoItem
    private static final String CREATE_TAGS =
            "CREATE TABLE " + Const.Tags.TABLE_NAME + " (" +
                    Const.Tags._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Const.Tags.COL_LIBELLE + " TEXT NOT NULL UNIQUE" + ")";

    // Requête SQL pour supprimer la table ToDo
    private static final String DELETE_ToDo =
            "DROP TABLE IF EXISTS " + Const.ToDoEntry.TABLE_NAME;

    // Requête SQL pour supprimer la table ToDoItem
    private static final String DELETE_ToDoItem =
            "DROP TABLE IF EXISTS " + Const.ToDoItemEntry.TABLE_NAME;

    // Requête SQL pour supprimer la table Tags
    private static final String DELETE_TAGS =
            "DROP TABLE IF EXISTS " + Const.Tags.TABLE_NAME;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Exécution des requêtes SQL pour la création des deux tables
        db.execSQL(CREATE_ToDo);
        db.execSQL(CREATE_ToDoItem);
        db.execSQL(CREATE_TAGS);
    }

    // Cette base de données n'est qu'un cache pour les données en ligne, sa politique de mise à jour est donc
    // pour simplement se débarrasser des données et recommencer à zéro
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_ToDo);
        db.execSQL(DELETE_ToDoItem);
        db.execSQL(DELETE_TAGS);
        onCreate(db);
    }

    // Méthode pour ajouter des tâches dans la table ToDo
    public Boolean addToDo(String title, LocalDate endDate){
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Const.ToDoEntry.COL_TITLE, title);
        values.put(Const.ToDoEntry.COL_FK_TAG, 0);
        values.put(Const.ToDoEntry.COL_ENDDATE, endDate.toString());

        Long res = db.insert(Const.ToDoEntry.TABLE_NAME, null, values);

        db.close();
        return res!=-1L;
    }

    // Méthode pour ajouter des sous-tâches dans la table ToDoItem
    public Boolean addToDoItem(Long toDoId, String name){
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Const.ToDoItemEntry.COL_FK_ToDo, toDoId);
        values.put(Const.ToDoItemEntry.COL_NAME, name);
        values.put(Const.ToDoItemEntry.COL_ISCOMPLETED, false);

        Long res = db.insert(Const.ToDoItemEntry.TABLE_NAME, null, values);

        db.close();
        return res!=-1L;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public ToDo searchTodoByTitle(String title){
        db = this.getReadableDatabase();

        Cursor queryRes = db.rawQuery("SELECT * FROM " + Const.ToDoEntry.TABLE_NAME + " WHERE " + Const.ToDoEntry.COL_TITLE + " =?", new String[] { title });

        if(queryRes.moveToFirst()){
            db.close();
            return new ToDo(
                    queryRes.getLong(queryRes.getColumnIndex(Const.ToDoEntry._ID)),
                    queryRes.getLong(queryRes.getColumnIndex(Const.ToDoEntry.COL_FK_TAG)),
                    queryRes.getString(queryRes.getColumnIndex(Const.ToDoEntry.COL_TITLE)),
                    LocalDate.parse(queryRes.getString(queryRes.getColumnIndex(Const.ToDoEntry.COL_ENDDATE))));

        } else {
            db.close();
            queryRes.close();
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ToDo searchTodoById(Long id){
        db = this.getReadableDatabase();

        Cursor queryRes = db.rawQuery("SELECT * FROM " + Const.ToDoEntry.TABLE_NAME + " WHERE " + Const.ToDoEntry._ID + " =?", new String[] { id.toString() });

        if(queryRes.moveToFirst()){
            db.close();
            return new ToDo(
                    queryRes.getLong(queryRes.getColumnIndex(Const.ToDoEntry._ID)),
                    queryRes.getLong(queryRes.getColumnIndex(Const.ToDoEntry.COL_FK_TAG)),
                    queryRes.getString(queryRes.getColumnIndex(Const.ToDoEntry.COL_TITLE)),
                    LocalDate.parse(queryRes.getString(queryRes.getColumnIndex(Const.ToDoEntry.COL_ENDDATE))));
        } else {
            queryRes.close();
            db.close();
            return null;
        }
    }

    public void deleteToDoByTitle(String title){
        db = this.getWritableDatabase();
        db.delete(Const.ToDoEntry.TABLE_NAME,
                Const.ToDoEntry.COL_TITLE + " = ?",
                new String[]{title});
        db.close();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<ToDo> getListToDo(){
        List<ToDo> listRes = new ArrayList();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor queryRes = db.rawQuery("SELECT * FROM " + Const.ToDoEntry.TABLE_NAME, null);

        if(queryRes.moveToFirst()){
            do{
                ToDo toDo = new ToDo(
                        queryRes.getLong(queryRes.getColumnIndex(Const.ToDoEntry._ID)),
                        queryRes.getString(queryRes.getColumnIndex(Const.ToDoEntry.COL_TITLE)),
                        LocalDate.parse(queryRes.getString(queryRes.getColumnIndex(Const.ToDoEntry.COL_ENDDATE))));

                //Requête pour remplir la liste des tâches à faire dans la tâche principale
                Cursor queryResItem = db.rawQuery("SELECT * FROM " + Const.ToDoItemEntry.TABLE_NAME + " WHERE " + Const.ToDoItemEntry.COL_FK_ToDo + " = ?", new String[]{toDo.getNumID().toString()});

                if(queryResItem.moveToFirst()){
                    do{
                        ToDoItem toDoItem = new ToDoItem(
                                queryResItem.getLong(queryResItem.getColumnIndex(Const.ToDoItemEntry._ID)),
                                queryResItem.getLong(queryResItem.getColumnIndex(Const.ToDoItemEntry.COL_FK_ToDo)),
                                queryResItem.getString(queryResItem.getColumnIndex(Const.ToDoItemEntry.COL_NAME))
                        );

                        toDo.addItems(toDoItem);
                    } while(queryResItem.moveToNext());
                }

                listRes.add(toDo);
            } while(queryRes.moveToNext());
        }

        queryRes.close();
        db.close();

        return listRes;
    }
}
