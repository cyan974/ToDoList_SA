package com.example.todolist_sa;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.todolist_sa.DTO.ToDo;
import com.example.todolist_sa.sqlite.DbHelper;

public class ViewToDoActivity extends Activity {
    DbHelper dbHelper;

    TextView txtTitle;
    TextView txtDate;
    ListView listItem;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewtodo);

        dbHelper = new DbHelper(this);

        // Liens avec les différents TextView et ListView de la vue
        txtTitle = findViewById(R.id.txtTitle);
        txtDate = findViewById(R.id.txtDate);
        listItem = findViewById(R.id.listTodo);

        Intent itn = getIntent();
        Long idTodo = itn.getLongExtra("ID_TODO", -1L);

        ToDo toDo = dbHelper.searchTodoById(idTodo);

        txtTitle.setText(toDo.getTitle());
    }
}
