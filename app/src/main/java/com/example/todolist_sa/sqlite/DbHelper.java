package com.example.todolist_sa.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.todolist_sa.DTO.Tag;
import com.example.todolist_sa.DTO.ToDo;
import com.example.todolist_sa.DTO.ToDoItem;

import java.time.LocalDate;
import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {
    SQLiteDatabase db;

    // Si vous changez le schéma de la base de données, vous devez incrémenter la version de la base de données.
    public static final String DB_NAME = "ToDoList.db";
    public static final int DB_VERSION = 1;

    // Création de la requête SQL pour créer la table ToDo
    private static final String CREATE_TODO =
            "CREATE TABLE " + Const.TodoEntry.TABLE_NAME + " (" +
                    Const.TodoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Const.TodoEntry.COL_TITLE + " TEXT NOT NULL UNIQUE," +
                    Const.TodoEntry.COL_ENDDATE + " DATE," +
                    Const.TodoEntry.COL_IMG + " TEXT," +
                    Const.TodoEntry.COL_BGCOLOR + " INTEGER " +
                    ")";

    // Création de la requête SQL pour créer la table ToDoItem
    private static final String CREATE_TODO_ITEM =
            "CREATE TABLE " + Const.TodoItemEntry.TABLE_NAME + " (" +
                    Const.TodoItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Const.TodoItemEntry.COL_NAME + " TEXT," +
                    Const.TodoItemEntry.COL_ISCOMPLETED + " INTEGER," +
                    Const.TodoItemEntry.COL_FK_TODO + " INTEGER NOT NULL CONSTRAINT fk_nn_task REFERENCES " +
                    Const.TodoEntry.TABLE_NAME + "(" + Const.TodoEntry._ID + ")," +
                    "UNIQUE(" + Const.TodoItemEntry.COL_FK_TODO + ", " + Const.TodoItemEntry.COL_NAME + ")" +
                    ")";

    // Création de la requête SQL pour créer la table ToDoItem
    private static final String CREATE_TAGS =
            "CREATE TABLE " + Const.TagsEntry.TABLE_NAME + " (" +
                    Const.TagsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Const.TagsEntry.COL_LIBELLE + " TEXT NOT NULL UNIQUE" + ")";

    // Création de la requête SQL pour créer la table TagTodo (pour les liens entre les deux tables)
    private static final String CREATE_TAG_TODO =
            "CREATE TABLE " + Const.TagTodoEntry.TABLE_NAME + " (" +
                    Const.TagTodoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Const.TagTodoEntry.COL_FK_TAG + " INTEGER NOT NULL," +
                    Const.TagTodoEntry.COL_FK_TODO + " INTEGER NOT NULL," +
                    "UNIQUE(" + Const.TagTodoEntry.COL_FK_TAG + ", " + Const.TagTodoEntry.COL_FK_TODO + ")" +
                    ")";

    // Requête SQL pour supprimer la table ToDo
    private static final String DELETE_TODO =
            "DROP TABLE IF EXISTS " + Const.TodoEntry.TABLE_NAME;

    // Requête SQL pour supprimer la table ToDoItem
    private static final String DELETE_TODO_ITEM =
            "DROP TABLE IF EXISTS " + Const.TodoItemEntry.TABLE_NAME;

    // Requête SQL pour supprimer la table Tags
    private static final String DELETE_TAGS =
            "DROP TABLE IF EXISTS " + Const.TagsEntry.TABLE_NAME;

    // Requête SQL pour supprimer la table Tags
    private static final String DELETE_TAG_TODO =
            "DROP TABLE IF EXISTS " + Const.TagTodoEntry.TABLE_NAME;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Exécution des requêtes SQL pour la création des deux tables
        db.execSQL(CREATE_TODO);
        db.execSQL(CREATE_TODO_ITEM);
        db.execSQL(CREATE_TAGS);
        db.execSQL(CREATE_TAG_TODO);
    }

    // Cette base de données n'est qu'un cache pour les données en ligne, sa politique de mise à jour est donc
    // pour simplement se débarrasser des données et recommencer à zéro
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_TODO);
        db.execSQL(DELETE_TODO_ITEM);
        db.execSQL(DELETE_TAGS);
        db.execSQL(DELETE_TAG_TODO);
        onCreate(db);
    }

    // Ajoute une tâche dans la table ToDo
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Boolean addToDo(ToDo todo){
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Const.TodoEntry.COL_TITLE, todo.getTitle());
        values.put(Const.TodoEntry.COL_ENDDATE, todo.getEndDate().toString());
        values.put(Const.TodoEntry.COL_BGCOLOR, todo.getBgColor());
        values.put(Const.TodoEntry.COL_IMG, todo.getImgPath());

        // insert la tâche dans la BDD
        Long res = db.insert(Const.TodoEntry.TABLE_NAME, null, values);

        // Récupère la tâche créée pour avoir l'ID
        ToDo toDo = searchTodoByTitle(todo.getTitle());

        // Insère les items (la liste des tâches à faire) dans la BDD
        if(todo.getListItems().size() > 0){
            for(ToDoItem item:todo.getListItems()){
                addToDoItem(toDo.getNumID(), item.getName());
            }
        }

        // Lie les tags (libellés) avec la liste de tâche
        for(Tag tag:todo.getListTags()){
            addTag_Todo(tag.getNumID(), toDo.getNumID());
        }

        db.close();
        return res!=-1L;
    }

    // Ajoute des sous-tâches (élément de la liste) dans la table ToDoItem
    public Boolean addToDoItem(Long toDoId, String name){
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Const.TodoItemEntry.COL_NAME, name);
        values.put(Const.TodoItemEntry.COL_ISCOMPLETED, 0);
        values.put(Const.TodoItemEntry.COL_FK_TODO, toDoId);

        Long res = db.insert(Const.TodoItemEntry.TABLE_NAME, null, values);

        db.close();
        return res!=-1L;
    }

    // Ajoute un tag
    public Boolean addTag(String tag){
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Const.TagsEntry.COL_LIBELLE, tag);

        Long res = db.insert(Const.TagsEntry.TABLE_NAME, null, values);

        db.close();
        return res!=-1L;
    }

    // Ajout un lien entre un tag et une tâche
    public Boolean addTag_Todo(Long idTag, Long idTodo){
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Const.TagTodoEntry.COL_FK_TODO, idTodo);
        values.put(Const.TagTodoEntry.COL_FK_TAG, idTag);

        Long res = db.insert(Const.TagTodoEntry.TABLE_NAME, null, values);

        db.close();
        return res!=-1L;
    }

    // Recherche et retourne un tag par rapport à son nom
    public Tag searchTagByName(String name){
        db = this.getReadableDatabase();

        Cursor queryRes = db.rawQuery("SELECT * FROM " + Const.TagsEntry.TABLE_NAME + " WHERE " + Const.TagsEntry.COL_LIBELLE + " =?", new String[] { name });

        if(queryRes.moveToFirst()){
            db.close();
            return new Tag(
                    queryRes.getLong(queryRes.getColumnIndex(Const.TagsEntry._ID)),
                    queryRes.getString(queryRes.getColumnIndex(Const.TagsEntry.COL_LIBELLE))
            );
        } else {
            db.close();
            queryRes.close();
            return null;
        }
    }

    // Recherche et retourne un tag par rapport à son ID
    public Tag searchTagById(Long idTag){
        db = this.getReadableDatabase();

        Cursor queryRes = db.rawQuery("SELECT * FROM " + Const.TagsEntry.TABLE_NAME + " WHERE " + Const.TagsEntry._ID + " =?", new String[] { idTag.toString() });

        if(queryRes.moveToFirst()){
            db.close();
            return new Tag(
                    queryRes.getLong(queryRes.getColumnIndex(Const.TagsEntry._ID)),
                    queryRes.getString(queryRes.getColumnIndex(Const.TagsEntry.COL_LIBELLE))
            );
        } else {
            db.close();
            queryRes.close();
            return null;
        }
    }

    // Recherche et retourne une tâche par rapport à son titre
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ToDo searchTodoByTitle(String title){
        db = this.getReadableDatabase();

        Cursor queryRes = db.rawQuery("SELECT * FROM " + Const.TodoEntry.TABLE_NAME + " WHERE " + Const.TodoEntry.COL_TITLE + " =?", new String[] { title });

        if(queryRes.moveToFirst()){
            db.close();
            ToDo toDo = new ToDo(
                    queryRes.getLong(queryRes.getColumnIndex(Const.TodoEntry._ID)),
                    queryRes.getString(queryRes.getColumnIndex(Const.TodoEntry.COL_TITLE)),
                    LocalDate.parse(queryRes.getString(queryRes.getColumnIndex(Const.TodoEntry.COL_ENDDATE))),
                    queryRes.getString(queryRes.getColumnIndex(Const.TodoEntry.COL_IMG)),
                    queryRes.getInt(queryRes.getColumnIndex(Const.TodoEntry.COL_BGCOLOR))
                    );

            toDo.setListItems(getListItemByTodo(toDo.getNumID()));
            toDo.setListTags(getListTagByTodo(toDo.getNumID()));

            return toDo;

        } else {
            db.close();
            queryRes.close();
            return null;
        }
    }

    // Recherche et retourne l'ID d'une tâche par rapport à son titre
    public Long searchTodoIDbyTitle(String title){
        db = this.getReadableDatabase();

        Cursor queryRes = db.rawQuery("SELECT * FROM " + Const.TodoEntry.TABLE_NAME + " WHERE " + Const.TodoEntry.COL_TITLE + " =?", new String[] { title });

        if(queryRes.moveToFirst()){
            db.close();
            return queryRes.getLong(queryRes.getColumnIndex(Const.TodoEntry._ID));
        } else {
            db.close();
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ToDo searchTodoById(Long id){
        db = this.getReadableDatabase();

        Cursor queryRes = db.rawQuery("SELECT * FROM " + Const.TodoEntry.TABLE_NAME + " WHERE " + Const.TodoEntry._ID + " =?", new String[] { id.toString() });

        if(queryRes.moveToFirst()){
            db.close();
            return new ToDo(
                    queryRes.getLong(queryRes.getColumnIndex(Const.TodoEntry._ID)),
                    queryRes.getString(queryRes.getColumnIndex(Const.TodoEntry.COL_TITLE)),
                    LocalDate.parse(queryRes.getString(queryRes.getColumnIndex(Const.TodoEntry.COL_ENDDATE))),
                    queryRes.getString(queryRes.getColumnIndex(Const.TodoEntry.COL_IMG)),
                    queryRes.getInt(queryRes.getColumnIndex(Const.TodoEntry.COL_BGCOLOR))
            );
        } else {
            queryRes.close();
            db.close();
            return null;
        }
    }

    public void deleteToDoByTitle(String title){
        db = this.getWritableDatabase();
        db.delete(Const.TodoEntry.TABLE_NAME,
                Const.TodoEntry.COL_TITLE + " = ?",
                new String[]{title});
        db.close();
    }

    // Supprime une tâche
    public void deleteToDoById(Long id){
        db = this.getWritableDatabase();

        // Suppression des éléments liés à la tâche
        db.delete(Const.TodoItemEntry.TABLE_NAME,
                Const.TodoItemEntry.COL_FK_TODO + " = ?",
                new String[]{id.toString()});

        // Suppression de la tâche
        db.delete(Const.TodoEntry.TABLE_NAME,
                Const.TodoEntry._ID + " = ?",
                new String[]{id.toString()});
        db.close();
    }

    // Supprime un élément (présent dans la liste de tâche)
    public void deleteItemTodo(String itemName, Long idTodo){
        db = this.getWritableDatabase();

        db.delete(Const.TodoItemEntry.TABLE_NAME,
                Const.TodoItemEntry.COL_FK_TODO + " = ? AND " + Const.TodoItemEntry.COL_NAME + " = ?",
                new String[]{idTodo.toString(), itemName});

        db.close();
    }

    // Supprime le lien entre un tag et une tâche
    public void deleteTag_Todo(Long idTag, Long idTodo){
        db = this.getWritableDatabase();

        db.delete(Const.TagTodoEntry.TABLE_NAME,
                Const.TagTodoEntry.COL_FK_TODO + " = ? AND " + Const.TagTodoEntry.COL_FK_TAG + " = ?",
                new String[]{idTodo.toString(), idTag.toString()});

        db.close();
    }

    // Supprime un tag
    public void deleteTag(Tag tag){
        db = this.getWritableDatabase();

        //Tag tag = searchTagByName(tagName);

        db.delete(Const.TagTodoEntry.TABLE_NAME,
                Const.TagTodoEntry.COL_FK_TAG + " = ?",
                new String[]{tag.getNumID().toString()});

        db.delete(Const.TagsEntry.TABLE_NAME,
                Const.TagsEntry._ID + " = ?",
                new String[]{tag.getNumID().toString()});
        db.close();
    }

    // Retourne toutes les tâches
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<ToDo> getListToDo(){
        ArrayList<ToDo> listRes = new ArrayList();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor queryRes = db.rawQuery("SELECT * FROM " + Const.TodoEntry.TABLE_NAME, null);

        if(queryRes.moveToFirst()){
            do{
                ToDo toDo = new ToDo(
                        queryRes.getLong(queryRes.getColumnIndex(Const.TodoEntry._ID)),
                        queryRes.getString(queryRes.getColumnIndex(Const.TodoEntry.COL_TITLE)),
                        LocalDate.parse(queryRes.getString(queryRes.getColumnIndex(Const.TodoEntry.COL_ENDDATE))),
                        queryRes.getString(queryRes.getColumnIndex(Const.TodoEntry.COL_IMG)),
                        queryRes.getInt(queryRes.getColumnIndex(Const.TodoEntry.COL_BGCOLOR))
                );

                toDo.setListItems(getListItemByTodo(toDo.getNumID()));

                toDo.setListTags(getListTagByTodo(toDo.getNumID()));

                listRes.add(toDo);
            } while(queryRes.moveToNext());
        }

        queryRes.close();
        db.close();

        return listRes;
    }

    // Retourne la liste des éléments de la liste de tâche
    public ArrayList<ToDoItem> getListItemByTodo(Long idTodo){
        ArrayList<ToDoItem> listItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor queryResItem = db.rawQuery("SELECT * FROM " + Const.TodoItemEntry.TABLE_NAME + " WHERE " + Const.TodoItemEntry.COL_FK_TODO + " = ?", new String[]{ idTodo.toString() });

        if(queryResItem.moveToFirst()){
            do{
                ToDoItem toDoItem = new ToDoItem(
                        queryResItem.getLong(queryResItem.getColumnIndex(Const.TodoItemEntry._ID)),
                        queryResItem.getLong(queryResItem.getColumnIndex(Const.TodoItemEntry.COL_FK_TODO)),
                        queryResItem.getString(queryResItem.getColumnIndex(Const.TodoItemEntry.COL_NAME)),
                        queryResItem.getInt(queryResItem.getColumnIndex(Const.TodoItemEntry.COL_ISCOMPLETED)) > 0
                );

                listItems.add(toDoItem);
            } while(queryResItem.moveToNext());
        }

        queryResItem.close();
        db.close();

        return listItems;
    }

    // Retoune la liste complète des tags
    public ArrayList<Tag> getListTag(){
        ArrayList<Tag> listTags = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor queryRes = db.rawQuery("SELECT * FROM " + Const.TagsEntry.TABLE_NAME, null);

        if(queryRes.moveToFirst()){
            do{
                Tag tag = new Tag(queryRes.getLong(queryRes.getColumnIndex(Const.TagsEntry._ID)),
                        queryRes.getString(queryRes.getColumnIndex(Const.TagsEntry.COL_LIBELLE)));
                listTags.add(tag);
            } while(queryRes.moveToNext());
        }

        queryRes.close();
        db.close();

        return listTags;
    }

    // Retourne une liste des tags pour une tâche
    public ArrayList<Tag> getListTagByTodo(Long idTodo){
        ArrayList<Tag> listTags = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor queryRes = db.rawQuery("SELECT * FROM " + Const.TagTodoEntry.TABLE_NAME + " WHERE " + Const.TagTodoEntry.COL_FK_TODO + " = ?", new String[]{ idTodo.toString() });

        if(queryRes.moveToFirst()){
            do{
                Tag tag = searchTagById(queryRes.getLong(queryRes.getColumnIndex(Const.TagTodoEntry.COL_FK_TAG)));
                listTags.add(tag);
            } while(queryRes.moveToNext());
        }

        queryRes.close();
        db.close();

        return listTags;
    }

    // Met à jour l'état d'un élément de la list des tâches (si il a été effectué ou pas)
    public void updateCheckedItemTodo(Long id, Boolean checked, String nameItem){
        SQLiteDatabase db = this.getWritableDatabase();

        Integer isChecked;
        if(checked)
            isChecked = 1;
        else
            isChecked = 0;

        ContentValues contentValues = new ContentValues();
        contentValues.put(Const.TodoItemEntry.COL_ISCOMPLETED, isChecked);

        db.update(Const.TodoItemEntry.TABLE_NAME, contentValues,
                Const.TodoItemEntry.COL_FK_TODO + " = ? AND " + Const.TodoItemEntry.COL_NAME + " = ?",
                new String[] {id.toString(), nameItem});

        db.close();
    }

    // Met à jour le nom d'un tag (libellé)
    public void updateNameTag(String oldName, String newName){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Const.TagsEntry.COL_LIBELLE, newName);

        db.update(Const.TagsEntry.TABLE_NAME, values,
                Const.TagsEntry.COL_LIBELLE + " = ?",
                new String[] {oldName});

        db.close();
    }

    // Met à jour le titre de la tâche
    public void updateTitleTodo(String oldTitle, String newTitle){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Const.TodoEntry.COL_TITLE, newTitle);

        db.update(Const.TodoEntry.TABLE_NAME, values,
                Const.TodoEntry.COL_TITLE + " = ?",
                new String[] {oldTitle});

        db.close();
    }

    // Met à jour la date de fin prévue pour la tâche
    public void updateEndDateTodo(LocalDate endDate, Long idTodo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Const.TodoEntry.COL_ENDDATE, endDate.toString());

        db.update(Const.TodoEntry.TABLE_NAME, values,
                Const.TodoEntry._ID + " = ?",
                new String[] {idTodo.toString()});

        db.close();
    }

    // Met à jour le nom d'un élément de la liste de tâche
    public void updateNameItemTodo(String oldName, String newName, Long idTodo){
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Const.TodoItemEntry.COL_NAME, newName);

        db.update(Const.TodoItemEntry.TABLE_NAME, values,
                Const.TodoItemEntry.COL_FK_TODO + " = ? AND " + Const.TodoItemEntry.COL_NAME + " = ?",
                new String[]{idTodo.toString(), oldName});

        db.close();
    }

    // Met à jour la couleur de fond pour une tâche
    public void updateColorTodo(Long idTodo, Integer color){
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Const.TodoEntry.COL_BGCOLOR, color);

        db.update(Const.TodoEntry.TABLE_NAME, values,
                Const.TodoEntry._ID + " = ?",
                new String[]{idTodo.toString()});

        db.close();
    }

    // Met a jour le lien qui permet d'accéder à l'image pour une tâche
    public void updateImageTodo(Long idTodo, String pathImg){
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Const.TodoEntry.COL_IMG, pathImg);

        db.update(Const.TodoEntry.TABLE_NAME, values,
                Const.TodoEntry._ID + " = ?",
                new String[]{idTodo.toString()});

        db.close();
    }
}
