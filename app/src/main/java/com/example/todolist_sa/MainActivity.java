package com.example.todolist_sa;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.todolist_sa.DTO.ToDo;
import com.example.todolist_sa.DTO.ToDoItem;
import com.example.todolist_sa.sqlite.DbHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private DbHelper mHelper;
    private ListView lvToDo;
    private ArrayAdapter<String> mAdapter;
    private AdapterToDo adapter;


    List<ToDo> todoList;
    String[] listTitles;
    String[] listDescriptions;
    String[] listTags;

    ArrayList<ToDo> arrayOfTodo;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Mettre avant le onCreate (thème pour la barre de menu)
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Informations de la table sur la bdd
        mHelper = new DbHelper(this);
        mHelper.insertFakeData();

        arrayOfTodo = new ArrayList<>();
        arrayOfTodo = mHelper.getListToDo();
        adapter = new AdapterToDo(this, arrayOfTodo);

        // ListView pour les ToDos
        lvToDo = findViewById(R.id.list_todo);
        lvToDo.setAdapter(adapter);

        // met à jour la liste des tâches
        //updateList();
    }

    // Méthode qui s'exécute à chaque fois qu'on revient sur cette activité
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume(){
        super.onResume();
        updateList();
    }

    // Méthode qui appelle une vue pour créer une tâche
    public void addTask(View v){
        Intent itnAddTask = new Intent(MainActivity.this, AddToDoActivity.class);
        startActivity(itnAddTask);
    }

    // Méthode qui appelle une vue pour visualiser les informations d'une tâche
    public void viewTask(View v){
        Intent itnViewTask = new Intent (MainActivity.this, ViewToDoActivity.class);
        //itnViewTask.putExtra("ID_TODO", id);
        startActivity(itnViewTask);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateList() {
        //refreshCollection();

        /*if(adapter == null){
            adapter = new AdapterToDo(this, listTitles, listDescriptions);
            lvToDo.setAdapter(adapter);
            lvToDo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    viewTask(id);
                }
            });
        } else {
            refreshCollection();
            adapter.notifyDataSetChanged();
        }*/


        /*
        if (mAdapter == null) {
            mAdapter = new ArrayAdapter<>(this,
                    R.layout.list_todo,
                    R.id.txtTitle,
                    listTitles);
            lvToDo.setAdapter(mAdapter);
            lvToDo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    viewTask(id);
                }
            });
        } else {
            mAdapter.clear();
            mAdapter.addAll(listTitles);
            mAdapter.notifyDataSetChanged();
        }*/
    }

    /*@RequiresApi(api = Build.VERSION_CODES.
            O)
    private void refreshCollection(){
        todoList = mHelper.getListToDo();

        listTitles = new String[todoList.size()];
        listDescriptions = new String[todoList.size()];
        listTags = new String[todoList.size()];

        int i = 0;
        for(ToDo todo:todoList){
            listTitles[i] = todo.getTitle();
            List<ToDoItem> listItem = todo.getListItems();

            String str = "";
            for(ToDoItem item:listItem){
                str += "- " + item.getName() +"\n";
            }
            listDescriptions[i] = str;
            i++;
        }
    }*/

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void deleteTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = parent.findViewById(R.id.txtTitle);
        String task = String.valueOf(taskTextView.getText());

        mHelper.deleteToDoByTitle(task);

        updateList();
    }

}
