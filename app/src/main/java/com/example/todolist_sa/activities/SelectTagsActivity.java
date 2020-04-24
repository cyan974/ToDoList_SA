package com.example.todolist_sa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist_sa.DTO.Tag;
import com.example.todolist_sa.DTO.ToDo;
import com.example.todolist_sa.R;
import com.example.todolist_sa.adapters.AdapterTags;
import com.example.todolist_sa.sqlite.DbHelper;

import java.util.ArrayList;

public class SelectTagsActivity extends AppCompatActivity {
    private DbHelper dbHelper;

    private AdapterTags adapter;
    private ListView lvTags;
    private ArrayList<Tag> listTags;

    private ArrayList<Tag> listTagsAdd;
    private ToDo todo;
    private Integer iActivity;

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
        iActivity = itn.getIntExtra("Activity", 0);

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

    // Méhtode qui permet d'initialiser la liste au départ avec les tags qui sont deja présent dans la liste ou non
    private void initializeList(){
        for(Tag tagList:dbHelper.getListTag()){
            for(Tag tagTodo:todo.getListTags()){
                if(tagList.getNumID().equals(tagTodo.getNumID())){
                    tagList.setSelected(true);
                    listTags.add(tagList);
                    break;
                }
            }
            if(!tagList.getSelected()){
                listTags.add(tagList);
            }
        }

        adapter = new AdapterTags(this, listTags);
        lvTags.setAdapter(adapter);
    }

    // Méthode qui se déclenche lors d'un clique sur un checkbox, permet d'ajouter ou retirer un tag de la liste
    public void onClickSelect(View v){
        View parent = (View) v.getParent();
        CheckBox cbxSelect = parent.findViewById(R.id.cbxSelected);
        TextView txtTag = parent.findViewById(R.id.txtTag);

        Tag tag = dbHelper.searchTagByName(txtTag.getText().toString());

        if(cbxSelect.isChecked()){
            // Ajoute le tag dans la liste
            todo.addTag(tag);

            // Ajoute à la BDD lorsque c'est l'activité qui affiche le détail
            if(iActivity == 1){
                dbHelper.addTag_Todo(tag.getNumID(), todo.getNumID());
            }
        } else {
            // Retire le tag qui est présent dans la liste
            for(Tag tagTodo:todo.getListTags()){
                if(tag.getNumID().equals(tagTodo.getNumID())){
                    todo.removeTag(tagTodo);

                    if(iActivity == 1){
                        dbHelper.deleteTag_Todo(tag.getNumID(), todo.getNumID());
                    }
                    break;
                }
            }
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
