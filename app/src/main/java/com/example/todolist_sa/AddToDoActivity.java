package com.example.todolist_sa;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.todolist_sa.sqlite.Const;
import com.example.todolist_sa.sqlite.DbHelper;

public class AddToDoActivity extends Activity {
    private DbHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        mHelper = new DbHelper(this);
    }

    public void onClick_Add(View view){
        EditText edtxt_title = findViewById(R.id.editTxt_Titre);
        DbHelper dbHelper = new DbHelper(this);


        AddToDoActivity.this.finish();
    }
}
