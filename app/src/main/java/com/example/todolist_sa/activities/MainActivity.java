package com.example.todolist_sa.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.todolist_sa.DTO.ToDo;
import com.example.todolist_sa.R;
import com.example.todolist_sa.adapters.AdapterToDo;
import com.example.todolist_sa.sqlite.DbHelper;

import java.util.ArrayList;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Cela ajoute des éléments à la barre d'action si elle est présente.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Permet d'ouvrir une activité pour gérer tous les libellés
            case R.id.action_libelle:
                Intent itnLibelle = new Intent(MainActivity.this, TagsActivity.class);
                startActivity(itnLibelle);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Méthode qui appelle une activité pour créer une tâche
    public void onClickAddTask(View v){
        Intent itnAddTask = new Intent(MainActivity.this, AddToDoActivity.class);
        startActivity(itnAddTask);
    }

    // Méthode qui appelle une activité pour visualiser les informations d'une tâche
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClickDetailTask(View v){
        TextView title = v.findViewById(R.id.txtTitle);
        ToDo todo = mHelper.searchTodoByTitle(title.getText().toString());

        Intent itnViewTask = new Intent (MainActivity.this, DetailToDoActivity.class);
        itnViewTask.putExtra("TODO", todo);
        startActivity(itnViewTask);
    }

    // Méthode qui met à jour la vue de la liste afin d'avoir la liste complète des tâches toujours à jour
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
}
