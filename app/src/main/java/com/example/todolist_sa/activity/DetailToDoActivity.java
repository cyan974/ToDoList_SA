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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.todolist_sa.DTO.Tag;
import com.example.todolist_sa.DTO.ToDo;
import com.example.todolist_sa.DTO.ToDoItem;
import com.example.todolist_sa.R;
import com.example.todolist_sa.adapter.AdapterItem;
import com.example.todolist_sa.adapter.AdapterItemEdit;
import com.example.todolist_sa.sqlite.DbHelper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

public class DetailToDoActivity extends AppCompatActivity {
    private DbHelper dbHelper;
    private DatePickerDialog datePickerDialog;
    private ToDo todo;

    private TextView txtTitle;
    private TextView txtDate;
    private TextView lblTag;
    private TextView txtTags;

    private AdapterItem adapterItem;
    private AdapterItemEdit adapterItemEdit;
    private ListView lvItem;
    private ArrayList<ToDoItem> listItems;

    private ImageView editTitle;
    private ImageView editDate;

    private Boolean modeEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_todo);

        modeEdit = false;

        // ActionBar - modifier le titre de la vue
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Détails de la tâche");

        dbHelper = new DbHelper(this);
        listItems = new ArrayList<>();

        // Liens avec les différents TextView et ListView de la vue
        txtTitle = findViewById(R.id.txtTitle);
        txtDate = findViewById(R.id.txtDate);
        lvItem = findViewById(R.id.listTodo);
        editTitle = findViewById(R.id.editTitle);
        editDate = findViewById(R.id.editDate);
        txtTags = findViewById(R.id.txtTags);
        lblTag = findViewById(R.id.lblTags);

        // Récupération de l'objet pour afficher les détails
        Intent itn = getIntent();
        todo = (ToDo)itn.getSerializableExtra("TODO");

        // Met en place la couleur
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(todo.getBgColor()));

        // Mise en place de l'affichage
        setupInfos();

        // cache le mode édition + met à jour la liste des tâches
        hideEdit();
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
                if(modeEdit){
                    hideEdit();
                    modeEdit = false;
                    item.setTitle("Modifier");
                } else {
                    showEdit();
                    modeEdit = true;
                    item.setTitle("Valider");
                }
                return true;

            case R.id.action_delete:
                dbHelper.deleteToDoById(todo.getNumID());
                finish();
                return true;

            case R.id.home:
                onBackPressed();
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

        dbHelper.updateCheckedItemTodo(todo.getNumID(), cbx.isChecked(), nameItem.getText().toString());

        updateList();
    }

    // Méthode qui permet la modification du titre
    public void onClickEditTitle(View v){
        final String oldTitle = txtTitle.getText().toString();
        final EditText input = new EditText(DetailToDoActivity.this);
        input.setText(oldTitle);

        AlertDialog alert = new AlertDialog.Builder(DetailToDoActivity.this).create();
        alert.setTitle("Modification du titre");
        alert.setIcon(R.drawable.logo);
        alert.setView(input);
        alert.setButton(Dialog.BUTTON_POSITIVE,"Valider",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newTitle = input.getText().toString();

                if(!oldTitle.equals(newTitle)){
                    dbHelper.updateTitleTodo(oldTitle, newTitle);
                    txtTitle.setText(newTitle);
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

    // Méthode qui permet la modification de la date
    public void onClickEditEndDate(View v){
        // calender class's instance and get current date , month and year from calender
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        // date picker dialog
        datePickerDialog = new DatePickerDialog(DetailToDoActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        // set day of month , month and year value in the edit text
                        LocalDate endDate = LocalDate.of(year,monthOfYear+1,dayOfMonth);

                        if(!txtDate.getText().toString().equals(endDate.toString())){
                            dbHelper.updateEndDateTodo(endDate, todo.getNumID());
                            txtDate.setText(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        datePickerDialog.show();
    }

    public void onClickEditItem(View v){
        View parent = (View) v.getParent();
        TextView ele = parent.findViewById(R.id.txtItem);

        final String oldName = ele.getText().toString();
        final EditText input = new EditText(DetailToDoActivity.this);
        input.setText(oldName);

        AlertDialog alert = new AlertDialog.Builder(DetailToDoActivity.this).create();
        alert.setTitle("Modification d'un élément");
        alert.setIcon(R.drawable.logo);
        alert.setView(input);
        alert.setButton(Dialog.BUTTON_POSITIVE,"Valider",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = input.getText().toString();

                if(!oldName.equals(newName)){
                    dbHelper.updateNameItemTodo(oldName, newName, todo.getNumID());
                    updateListEdit();
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

    // Méthode pour supprimer un élément dans la liste des tâches (en mode édition)
    public void onClickDeleteItem(View v){
        View parent = (View) v.getParent();
        TextView ele = parent.findViewById(R.id.txtItem);

        dbHelper.deleteItemTodo(ele.getText().toString(), todo.getNumID());

        updateListEdit();
    }

    // Méthode qui permet la maj de la liste
    private void updateList(){
        if (adapterItem != null) {
            listItems.clear();
            adapterItem.clear();
        }

        listItems = dbHelper.getListItemByTodo(todo.getNumID());
        adapterItem = new AdapterItem(this, listItems);
        lvItem.setAdapter(adapterItem);
    }

    // Méthode qui permet la maj de la liste
    private void updateListEdit(){
        if (adapterItemEdit != null) {
            listItems.clear();
            adapterItemEdit.clear();
        }

        listItems = dbHelper.getListItemByTodo(todo.getNumID());
        adapterItemEdit = new AdapterItemEdit(this, listItems);
        lvItem.setAdapter(adapterItemEdit);
    }

    // Méthode pour mettre en place l'affichage des informations de départ
    private void setupInfos(){
        // Mise en place de l'affichage
        txtTitle.setText(todo.getTitle());
        txtDate.setText(todo.getEndDate().toString());

        // Si 0, alors on affiche rien, sinon on affiche
        if(todo.getListTags().size() > 0) {
            // Afficher au pluriel ou au singulier
            if(todo.getListTags().size() > 1) {
                lblTag.setText("Libellés :");
            } else {
                lblTag.setText("Libellé :");
            }

            // Afficher la liste des tags
            String str = "";
            for(Tag tag:todo.getListTags()){
                str += "(" + tag.getLibelle() + ") ";
            }
            txtTags.setText(str);
        } else {
            //lblTag.setText("");
            //txtTags.setText("");

            lblTag.setVisibility(View.INVISIBLE);
            txtTags.setVisibility(View.INVISIBLE);
        }
    }

    // Méthode qui permet de cacher le mode édition
    private void hideEdit(){
        // Cache les éléments pour modifier la tâche
        editTitle.setVisibility(View.INVISIBLE);
        editDate.setVisibility(View.INVISIBLE);
        updateList();
    }

    // Méthode qui permet de montrer le mode édition
    private void showEdit(){
        editTitle.setVisibility(View.VISIBLE);
        editDate.setVisibility(View.VISIBLE);
        updateListEdit();
    }
}
