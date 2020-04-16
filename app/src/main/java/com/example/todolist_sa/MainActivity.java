package com.example.todolist_sa;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
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
    private DbHelper mHelper;
    private ListView lvToDo;
    private AdapterToDo adapter;
    private ArrayList<ToDo> arrayOfTodo;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Mettre avant le onCreate (thème pour la barre de menu)
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Informations de la table sur la bdd
        mHelper = new DbHelper(this);
        //mHelper.insertFakeData();

        // ArrayList des tâches
        arrayOfTodo = new ArrayList<>();

        // ListView pour les ToDos
        lvToDo = findViewById(R.id.list_todo);

        // met à jour la liste des tâches
        updateList();
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void viewTask(View v){
        TextView title = v.findViewById(R.id.txtTitle);
        ToDo todo = mHelper.searchTodoByTitle(title.getText().toString());

        Intent itnViewTask = new Intent (MainActivity.this, ViewToDoActivity.class);
        itnViewTask.putExtra("TODO", todo);
        startActivity(itnViewTask);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateList() {
        if (adapter != null) {
            arrayOfTodo.clear();
            adapter.clear();
        }

        arrayOfTodo = mHelper.getListToDo();
        adapter = new AdapterToDo(this, arrayOfTodo);
        lvToDo.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void deleteTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = parent.findViewById(R.id.txtTitle);

        String task = String.valueOf(taskTextView.getText());
        mHelper.deleteToDoByTitle(task);
        updateList();
    }

}
