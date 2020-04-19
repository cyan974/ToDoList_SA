package com.example.todolist_sa.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist_sa.DTO.Tag;
import com.example.todolist_sa.DTO.ToDo;
import com.example.todolist_sa.R;
import com.example.todolist_sa.adapter.AdapterTags;
import com.example.todolist_sa.sqlite.DbHelper;

import java.util.ArrayList;

public class SelectTagsActivity extends AppCompatActivity {
    private DbHelper dbHelper;

    private AdapterTags adapter;
    private ListView lvTags;
    private ArrayList<Tag> listTags;

    private ArrayList<Tag> listTagsAdd;
    private ToDo todo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tags);

        setTitle("Sélectionnez des libellés");

        dbHelper = new DbHelper(this);

        Intent itn = getIntent();
        listTagsAdd = (ArrayList<Tag>) itn.getSerializableExtra("LIST_TAGS");
        todo = (ToDo) itn.getSerializableExtra("TODO");

        listTags = new ArrayList<>();

        lvTags = findViewById(R.id.listSelectTags);
        initializeList();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initializeList(){
        listTags = dbHelper.getListTag();

        adapter = new AdapterTags(this, listTags);
        lvTags.setAdapter(adapter);
    }


    public void onClickSelect(View v){
        View parent = (View) v.getParent();
        CheckBox cbxSelect = parent.findViewById(R.id.cbxSelected);
        TextView txtTag = parent.findViewById(R.id.txtTag);

        Tag tag = dbHelper.searchTagByName(txtTag.getText().toString());

        if(cbxSelect.isChecked()){
            //listTagsAdd.add(tag);
            todo.addTag(tag);
        } else {
            //listTagsAdd.remove(tag);
            todo.removeTag(tag);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("TODO", todo);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}
