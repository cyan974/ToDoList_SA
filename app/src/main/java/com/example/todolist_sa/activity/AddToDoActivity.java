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
import android.widget.Button;
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
    private TextView lblLibelle;
    private Button btnColor;

    private LocalDate endDate;

    private ToDo todo;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);

        // Titre de l'activité
        setTitle("Ajout d'une tâche");

        dbHelper = new DbHelper(this);

        // Objet qui va permettre de garder toutes les informations avant d'etre enregistré dans la BDD
        todo = new ToDo();

        // Récupération des éléments présents dans l'activité
        edtTitre = findViewById(R.id.edtTitre);
        txtDate = findViewById(R.id.txtDate);
        edtElement = findViewById(R.id.edtElement);
        txtTags = findViewById(R.id.txtTags);
        lblLibelle = findViewById(R.id.lblLibelle);
        btnColor = findViewById(R.id.btnColor);

        lblLibelle.setVisibility(View.INVISIBLE);

        // Gestion de l'affichage pour la ListView
        lvItem = findViewById(R.id.listItem);
        listItems = new ArrayList<>();
        mAdapter = new ArrayAdapter(this, R.layout.list_item_todo, R.id.txtElement, listItems);
        lvItem.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Cela ajoute des éléments à la barre d'action si elle est présente.
        getMenuInflater().inflate(R.menu.add_todo_menu, menu);
        return true;
    }

    // Options présent dans l'ActionBar
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

    // Méthode déclenché lorsque l'activity recoit un résultat
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
        // Sauvegarder le titre dans l'objet ToDo
        if(edtTitre.getText().toString().length() > 0)
            todo.setTitle(edtTitre.getText().toString());

        // Sauvegarde la date dans l'objet ToDo
        if(txtDate.getText().toString().length() > 0)
            todo.setEndDate(endDate);

        // Sauvegarde tous les items (liste des choses à faire) dans la liste de l'objet ToDo
        if(listItems.size() > 0) {
            todo.getListItems().clear();
            for(String item : listItems){
                todo.addItem(new ToDoItem(item));
            }
        }
    }

    // Méthode pour réafficher les infos sauvegardés dans l'activity
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
            Integer cpt = 0;
            lblLibelle.setVisibility(View.VISIBLE);
            for(Tag tag:todo.getListTags()){
                strTags += "(" + tag.getLibelle() + ") ";
                cpt++;
            }

            if (cpt > 1) {
                lblLibelle.setText("Libellés :");
            } else {
                lblLibelle.setText("Libellé :");
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

                        txtDate.setText(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);

                        //cDate.set(year,monthOfYear,dayOfMonth);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        datePickerDialog.show();
    }

    // Méthode pour ajouter un élément dans la liste des tâches
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

    // Méthode qui ouvre un AlertDialog pour afficher une vue qui contient un choix de couleur pour le background
    public void onClickChooseColor(View v){
        final View customLayout = getLayoutInflater().inflate(R.layout.list_color, null);

        AlertDialog alert = new AlertDialog.Builder(AddToDoActivity.this).create();
        alert.setTitle("Choisir une couleur");
        alert.setIcon(R.drawable.logo);
        alert.setView(customLayout);
        alert.setButton(Dialog.BUTTON_POSITIVE,"OK",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.setButton(Dialog.BUTTON_NEGATIVE,"Réinitialiser",new DialogInterface.OnClickListener(){

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                btnColor.setBackground(getResources().getDrawable(R.drawable.circle_white));
                todo.setBgColor(R.color.colorWhite);
            }
        });
        alert.show();
    }

    // Méthode qui choisit la couleur du background en fonction du bouton surlequel on a cliqué
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onClickColor(View v){
        switch (v.getId()) {
            case R.id.btnBlue:
                btnColor.setBackground(getResources().getDrawable(R.drawable.circle_blue));
                todo.setBgColor(R.color.blue);
                break;

            case R.id.btnGrey:
                btnColor.setBackground(getResources().getDrawable(R.drawable.circle_grey));
                todo.setBgColor(R.color.grey);
                break;

            case R.id.btnRed:
                btnColor.setBackground(getResources().getDrawable(R.drawable.circle_red));
                todo.setBgColor(R.color.red);
                break;

            case R.id.btnPurple:
                btnColor.setBackground(getResources().getDrawable(R.drawable.circle_purple));
                todo.setBgColor(R.color.purple);
                break;
        }

    }

    // Action du bouton flottant qui ajoute la liste de tâche avec ses différents éléments (qui crée l'objet ToDo dans la BDD)
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClickAdd(View v){
        saveInfo();
        if(todo.getTitle() != null && todo.getEndDate() != null){
            if(dbHelper.addToDo(todo.getTitle(), todo.getEndDate(), todo.getBgColor())){

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
