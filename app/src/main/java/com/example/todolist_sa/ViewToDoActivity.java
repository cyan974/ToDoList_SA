package com.example.todolist_sa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist_sa.DTO.ToDo;
import com.example.todolist_sa.sqlite.DbHelper;

public class ViewToDoActivity extends AppCompatActivity {
    DbHelper dbHelper;

    TextView txtTitle;
    TextView txtDate;
    ListView listItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_todo);

        // ActionBar - modifier le titre de la vue
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Détails de la tâche");

        dbHelper = new DbHelper(this);

        // Liens avec les différents TextView et ListView de la vue
        txtTitle = findViewById(R.id.txtTitle);
        txtDate = findViewById(R.id.txtDate);
        listItem = findViewById(R.id.listTodo);

        // Récupération de l'objet pour afficher les détails
        Intent itn = getIntent();
        ToDo todo = (ToDo)itn.getSerializableExtra("TODO");

        // Mise en place de l'affichage
        txtTitle.setText(todo.getTitle());
        txtDate.setText(todo.getEndDate().toString());
    }
}
