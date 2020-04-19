package com.example.todolist_sa.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist_sa.DTO.ToDo;
import com.example.todolist_sa.DTO.ToDoItem;
import com.example.todolist_sa.R;
import com.example.todolist_sa.adapter.AdapterItem;
import com.example.todolist_sa.sqlite.DbHelper;

import java.util.ArrayList;

public class DetailToDoActivity extends AppCompatActivity {
    private DbHelper dbHelper;
    private ToDo todo;

    private TextView txtTitle;
    private TextView txtDate;

    private AdapterItem adapterItem;
    private ListView lvItem;
    private ArrayList<ToDoItem> listItems;

    private ImageView editTitle;
    private ImageView editDate;

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
        lvItem = findViewById(R.id.listTodo);
        editTitle = findViewById(R.id.editTitle);
        editDate = findViewById(R.id.editDate);

        //hideEdit();

        // Récupération de l'objet pour afficher les détails
        Intent itn = getIntent();
        todo = (ToDo)itn.getSerializableExtra("TODO");

        // Mise en place de l'affichage
        txtTitle.setText(todo.getTitle());
        txtDate.setText(todo.getEndDate().toString());

        listItems = new ArrayList<>();

        // Met à jour la liste des tâches à faire pour la liste
        updateList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail_todo_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                return true;

            case R.id.home:
                onBackPressed();
                return true;

            case R.id.action_delete:
                dbHelper.deleteToDoById(todo.getNumID());
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onClickCheckBox(View v){
        View parent = (View) v.getParent();
        CheckBox cbx = parent.findViewById(R.id.cbxChecked);
        TextView nameItem = parent.findViewById(R.id.txtItem);

        dbHelper.updateItemTodo(todo.getNumID(), cbx.isChecked(), nameItem.getText().toString());

        updateList();
    }

    public void onClickEditTitle(View v){
        final EditText input = new EditText(DetailToDoActivity.this);

        AlertDialog alert = new AlertDialog.Builder(DetailToDoActivity.this).create();
        alert.setTitle("Modification du titre");
        alert.setIcon(R.drawable.logo);
        alert.setView(input);
        alert.setButton(Dialog.BUTTON_POSITIVE,"Valider",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.setButton(Dialog.BUTTON_NEGATIVE,"Annuler",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.show();
    }

    private void updateList(){
        if (adapterItem != null) {
            listItems.clear();
            adapterItem.clear();
        }

        listItems = dbHelper.getListItem(todo.getNumID());
        adapterItem = new AdapterItem(this, listItems);
        lvItem.setAdapter(adapterItem);
    }

    private void hideEdit(){
        // Cache les éléments pour modifier la tâche
        editTitle.setVisibility(View.INVISIBLE);
        editDate.setVisibility(View.INVISIBLE);
    }
}
