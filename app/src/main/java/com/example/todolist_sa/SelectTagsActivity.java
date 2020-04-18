package com.example.todolist_sa;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist_sa.DTO.Tag;
import com.example.todolist_sa.sqlite.DbHelper;

import java.util.ArrayList;
import java.util.List;

public class SelectTagsActivity extends AppCompatActivity {
    DbHelper dbHelper;

    ListView lvTags;
    List<String> listTags;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tags);

        dbHelper = new DbHelper(this);

        // ActionBar - modifier le titre de la vue
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Sélectionnez des libellés");

        lvTags = findViewById(R.id.listSelectTags);
        initializeList();
    }

    private void initializeList(){
        listTags = new ArrayList<>();
        for(Tag tag:dbHelper.getListTag()){
            listTags.add(tag.getLibelle());
        }

        mAdapter = new ArrayAdapter(this, R.layout.list_select_tags, R.id.txtTag, listTags);
        lvTags.setAdapter(mAdapter);
    }


    public void onClickSelect(View v){
        View parent = (View) v.getParent();
        CheckBox cbxSelect = parent.findViewById(R.id.cbxSelected);
        TextView txtTag = parent.findViewById(R.id.txtTag);

        if(cbxSelect.isChecked()){
            //txtTag.setText("BIEN");
        }
    }
}
