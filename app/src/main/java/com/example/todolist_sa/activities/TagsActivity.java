package com.example.todolist_sa.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist_sa.DTO.Tag;
import com.example.todolist_sa.R;
import com.example.todolist_sa.sqlite.DbHelper;

import java.util.ArrayList;
import java.util.List;

public class TagsActivity extends AppCompatActivity {
    private DbHelper dbHelper;

    private EditText edtTag;

    private List<String> listTags = new ArrayList<>();
    private ListView lvTags;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);

        dbHelper = new DbHelper(this);

        // ActionBar - modifier le titre de la vue
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Gestion des libellés");

        edtTag = findViewById(R.id.edtTag);
        lvTags = findViewById(R.id.listTags);

        // Met à jour l'affichage de la liste par rapport a la base de données
        updateList();
    }

    public void onClickAddTag(View v){
        String strTag = edtTag.getText().toString();
        if(strTag.length() > 0){
            // Ajout dans la base de données
            dbHelper.addTag(strTag);

            edtTag.getText().clear();
            updateList();
        }
    }

    public void onClickDeleteTag(View v){
        View parent = (View) v.getParent();
        TextView ele = parent.findViewById(R.id.txtTag);

        dbHelper.deleteTag(dbHelper.searchTagByName(ele.getText().toString()));
        updateList();
    }

    public void onClickEditTag(View v){
        View parent = (View) v.getParent();
        TextView ele = parent.findViewById(R.id.txtTag);
        final String oldTag = ele.getText().toString();

        final EditText input = new EditText(TagsActivity.this);
        input.setText(oldTag);

        AlertDialog alert = new AlertDialog.Builder(TagsActivity.this).create();
        alert.setTitle("Modification du libellé");
        alert.setIcon(R.drawable.logo);
        alert.setView(input);
        alert.setButton(Dialog.BUTTON_POSITIVE,"Valider",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newTag = input.getText().toString();

                if(!oldTag.equals(newTag)){
                    dbHelper.updateTag(oldTag, newTag);
                    updateList();
                }
            }
        });

        alert.setButton(Dialog.BUTTON_NEGATIVE,"Annuler",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.show();
    }

    public void updateList(){
        if (mAdapter != null) {
            listTags.clear();
            mAdapter.clear();
        }

        for(Tag tag:dbHelper.getListTag()){
            listTags.add(tag.getLibelle());
        }

        mAdapter = new ArrayAdapter(this, R.layout.list_tags, R.id.txtTag, listTags);
        lvTags.setAdapter(mAdapter);
    }

}
