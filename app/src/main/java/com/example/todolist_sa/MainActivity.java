package com.example.todolist_sa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.todolist_sa.sqlite.Const;
import com.example.todolist_sa.sqlite.DbHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private DbHelper mHelper;
    private ListView mTaskListView;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Mettre avant le onCreate
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Informations de la table sur la bdd
        mHelper = new DbHelper(this);
        SQLiteDatabase db = mHelper.getReadableDatabase();

        // Définir une projection qui précise quelles colonnes de la base de données
        // que vous utiliserez effectivement après cette requête.
        String[] projection = {
                BaseColumns._ID,
                Const.ToDoEntry.COL_TITLE,
                Const.ToDoEntry.COL_ENDDATE
                //Const.ToDoEntry.COL_TASK_IMG
        };

        // Filtrer les résultats WHERE "title" = "My Title" (mon titre)
        String selection = Const.ToDoEntry.COL_TITLE;
        //String[] selectionArgs = { "My Title" };

        // Comment vous voulez que les résultats soient triés dans le Curseur résultant
        String sortOrder =
                Const.ToDoEntry.COL_ENDDATE + " DESC";

        Cursor cursor = db.query(
                Const.ToDoEntry.TABLE_NAME,
                projection,
                selection,
                null,
                null,
                null,
                sortOrder,
                null
        );

        mTaskListView = findViewById(R.id.list_todo);
        List itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(Const.ToDoEntry.COL_TITLE);
            Log.d(TAG, "Tâche: " + cursor.getString(idx));

            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(Const.ToDoEntry._ID));
            itemIds.add(itemId);
        }
        cursor.close();
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task:
                //addTask();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addTask(View view){
        Intent itnAddTask = new Intent(MainActivity.this, AddToDoActivity.class);
        startActivity(itnAddTask);

        /*final EditText taskEditText = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Ajouter une nouvelle tâche")
                .setMessage("Que veux-tu faire après?")
                .setView(taskEditText)
                .setPositiveButton("Ajouter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(taskEditText.getText());
                        SQLiteDatabase db = mHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put(TaskContract.TaskEntry.COL_TASK_TITLE, task);
                        db.insertWithOnConflict(TaskContract.TaskEntry.TABLE_NAME,
                                null,
                                values,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        db.close();
                        updateUI();
                    }
                })
                .setNegativeButton("Annuler", null)
                .create();
        dialog.show();*/
    }

    private void updateUI() {
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(Const.ToDoEntry.TABLE_NAME,
                new String[]{Const.ToDoEntry._ID, Const.ToDoEntry.COL_TITLE},
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(Const.ToDoEntry.COL_TITLE);
            taskList.add(cursor.getString(idx));
        }

        if (mAdapter == null) {
            mAdapter = new ArrayAdapter<>(this,
                    R.layout.item_todo,
                    R.id.task_title,
                    taskList);
            mTaskListView.setAdapter(mAdapter);
        } else {
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }

        cursor.close();
        db.close();
    }

    public void deleteTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(Const.ToDoEntry.TABLE_NAME,
                Const.ToDoEntry.COL_TITLE + " = ?",
                new String[]{task});
        db.close();
        updateUI();
    }

}
