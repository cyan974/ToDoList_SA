package com.example.todolist_sa.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist_sa.DTO.Tag;
import com.example.todolist_sa.DTO.ToDo;
import com.example.todolist_sa.DTO.ToDoItem;
import com.example.todolist_sa.R;
import com.example.todolist_sa.sqlite.DbHelper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

public class AddToDoActivity extends AppCompatActivity {
    private DbHelper dbHelper;
    private DatePickerDialog datePickerDialog;

    private ArrayList<String> listItems;
    private ListView lvItem;
    private ArrayAdapter<String> mAdapter;

    private EditText edtTitre;
    private EditText edtElement;
    private TextView txtDate;
    private TextView txtTags;

    private LocalDate endDate;

    private ToDo todo;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);

        setTitle("Ajout d'une tâche");

        dbHelper = new DbHelper(this);

        todo = new ToDo();

        edtTitre = findViewById(R.id.edtTitre);
        txtDate = findViewById(R.id.txtDate);
        edtElement = findViewById(R.id.edtElement);
        txtTags = findViewById(R.id.txtTags);

        // Gestion de l'affichage pour la ListView
        lvItem = findViewById(R.id.listItem);
        listItems = new ArrayList<>();
        mAdapter = new ArrayAdapter(this, R.layout.list_item_todo, R.id.txtElement, listItems);
        lvItem.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_todo_menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_libelle:
                saveInfo();
                Intent itnLibelle = new Intent(AddToDoActivity.this, SelectTagsActivity.class);
                itnLibelle.putExtra("TODO", todo);

                startActivityForResult(itnLibelle,1);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Récupération des informations de l'activité pour les libellés
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                todo = (ToDo) data.getSerializableExtra("TODO");
                restoreInfo();
            }
        }
    }

    // Méthode pour sauvegarder les informations dans un objet
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveInfo(){
        if(edtTitre.getText().toString().length() > 0)
            todo.setTitle(edtTitre.getText().toString());

        if(txtDate.getText().toString().length() > 0)
            todo.setEndDate(endDate);

        if(listItems.size() > 0) {
            for(String item : listItems){
                todo.addItem(new ToDoItem(item));
            }
        }

        if(txtTags.getText().toString().length() > 0){

        }
    }

    // Méthode pour réafficher les infos dans l'activity
    public void restoreInfo(){
        if(todo.getTitle() != null){
            edtTitre.setText(todo.getTitle());
        }

        if(todo.getEndDate() != null){
            txtDate.setText(todo.getEndDate().toString());
        }

        if(todo.getListItems().size() > 0){
            listItems.clear();
            for(ToDoItem item:todo.getListItems()){
                listItems.add(item.getName());
            }
            mAdapter.notifyDataSetChanged();
        }

        if(todo.getListTags().size() > 0){
            String strTags ="";
            for(Tag tag:todo.getListTags()){
                strTags += tag.getLibelle() + " / ";
            }

            txtTags.setText(strTags);
        }
    }

    // Méthode onClick pour l'ajout d'une date via l'interface d'un calendrier
    public void onClickDate(View v){
        // calender class's instance and get current date , month and year from calender
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        // date picker dialog
        datePickerDialog = new DatePickerDialog(AddToDoActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        TextView txtDate = findViewById(R.id.txtDate);
                        // set day of month , month and year value in the edit text

                        endDate = LocalDate.of(year,monthOfYear+1,dayOfMonth);

                        txtDate.setText(dayOfMonth + "/"
                                + (monthOfYear + 1) + "/" + year);

                        //cDate.set(year,monthOfYear,dayOfMonth);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    // Méthode pour ajouter une élément pour la liste des tâches
    public void onClickAddElement(View v){
        if(edtElement.getText().toString().length() > 0){
            listItems.add(edtElement.getText().toString());
            mAdapter.notifyDataSetChanged();

            edtElement.getText().clear();
        }
    }

    // Méthode pour supprimer l'élément de la liste
    public void onClickDeleteElement(View v){
        View parent = (View) v.getParent();
        TextView ele = parent.findViewById(R.id.txtElement);
        listItems.remove(ele.getText().toString());
        mAdapter.notifyDataSetChanged();
    }

    // Action du bouton flottant qui ajoute la liste de tâche avec ses différents éléments
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClickAdd(View v){
        saveInfo();
        if(todo.getTitle() != null && todo.getEndDate() != null){
            if(dbHelper.addToDo(todo.getTitle(), todo.getEndDate())){

                // Récupère la tâche créée pour avoir l'ID
                ToDo toDo = dbHelper.searchTodoByTitle(todo.getTitle());

                // Insère les items (la liste des tâches à faire) dans la BDD
                if(todo.getListItems().size() > 0){
                    for(ToDoItem item:todo.getListItems()){
                        Boolean res = dbHelper.addToDoItem(toDo.getNumID(), item.getName());
                    }
                }

                // Lie les tags (libellés) avec la liste de tâche
                for(Tag tag:todo.getListTags()){
                    Boolean res = dbHelper.addTag_Todo(tag.getNumID(), toDo.getNumID());
                }
            } else {
                // Afficher une erreur lors de l'ajout si nécessaire
            }

            finish();
        } else {
            AlertDialog alert = new AlertDialog.Builder(AddToDoActivity.this).create();
            alert.setTitle("Erreur");
            alert.setMessage("Vous ne pouvez pas laisser les champs vides");
            alert.setButton(Dialog.BUTTON_POSITIVE,"OK",new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alert.show();
        }
    }
}
