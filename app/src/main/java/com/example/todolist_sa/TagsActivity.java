package com.example.todolist_sa;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist_sa.sqlite.DbHelper;

import java.util.ArrayList;
import java.util.List;

public class TagsActivity extends AppCompatActivity {
    DbHelper dbHelper;

    EditText edtTag;
    TextView txtInfo;

    List<String> listTags = new ArrayList<>();
    ListView lvTags;
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
        txtInfo = findViewById(R.id.txtInfo);
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

        dbHelper.deleteTag(ele.getText().toString());
        updateList();
    }

    public void updateList(){
        if (mAdapter != null) {
            listTags.clear();
            mAdapter.clear();
        }

        listTags = dbHelper.getListTag();
        mAdapter = new ArrayAdapter(this, R.layout.list_tags, R.id.txtTag, listTags);
        lvTags.setAdapter(mAdapter);
    }

}
