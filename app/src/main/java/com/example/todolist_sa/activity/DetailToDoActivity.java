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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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

    // Variables pour les couleurs
    private Button btnBgColor;
    private Integer iBgColor;

    private EditText edtElement;

    private Boolean modeEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_todo);

        // Titre de l'activité
        setTitle("Détails de la tâche");

        // Mode édition, va permettre d'afficher différents boutons pour modifier la tâche
        modeEdit = false;

        dbHelper = new DbHelper(this);
        listItems = new ArrayList<>();

        // Récupération des éléments présents dans l'activité
        txtTitle = findViewById(R.id.txtTitle);
        txtDate = findViewById(R.id.txtDate);
        lvItem = findViewById(R.id.listTodo);
        editTitle = findViewById(R.id.editTitle);
        editDate = findViewById(R.id.editDate);
        txtTags = findViewById(R.id.txtTags);
        lblTag = findViewById(R.id.lblTags);
        btnBgColor = findViewById(R.id.btnColor);
        edtElement = findViewById(R.id.edtElement);

        // Récupération de l'objet pour afficher les détails
        Intent itn = getIntent();
        todo = (ToDo)itn.getSerializableExtra("TODO");

        // Mise en place de l'affichage
        setupInfos();

        // cache le mode édition + met à jour les infos
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
            // Permet d'activer ou désactiver le mode édtion (qui permet la modification des éléments)
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

                // Action pour ajouter ou supprimer des tags dans la tâche
            case R.id.action_libelle:
                Intent itnLibelle = new Intent(DetailToDoActivity.this, SelectTagsActivity.class);
                itnLibelle.putExtra("TODO", todo);
                itnLibelle.putExtra("Activity", 1); // Défini un nombre pour différence le comportement dans l'activity des tags
                startActivityForResult(itnLibelle, 0);
                return true;

                // Supprime la tâche de la BDD
            case R.id.action_delete:
                dbHelper.deleteToDoById(todo.getNumID());
                finish();
                return true;

                // Action lorsque qu'on appuie sur la touche retour dans la barre
            case R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Méthode déclenché lorsque l'activity recoit un résultat
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Récupération des informations de l'activité pour les libellés
        if(requestCode == 0){
            if(resultCode == RESULT_OK){
                todo = (ToDo) data.getSerializableExtra("TODO");
                setupInfos();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // Méthode qui met a jour si la tâche dans la liste a été effectué ou non et met à jour l'affichage des CheckBox (checked or not)
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

    // Méthode pour modifier le texte d'un item
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

    // Méthode qui ouvre un AlertDialog pour choisir la couleur
    public void onClickEditColor(View v){
        final View colorLayout = getLayoutInflater().inflate(R.layout.list_color_detail, null);
        AlertDialog alert = new AlertDialog.Builder(DetailToDoActivity.this).create();
        alert.setTitle("Modifer la couleur de fond");
        alert.setIcon(R.drawable.logo);
        alert.setView(colorLayout);
        alert.setButton(Dialog.BUTTON_POSITIVE,"OK",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.setButton(Dialog.BUTTON_NEGATIVE,"Réinitialiser",new DialogInterface.OnClickListener(){

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //btnBgColor.setBackground(getResources().getDrawable(R.drawable.circle_white));
                //todo.setBgColor(R.color.colorWhite);
                iBgColor = R.color.colorWhite;
                dbHelper.updateColorTodo(todo.getNumID(), iBgColor);
                getWindow().getDecorView().setBackgroundColor(getResources().getColor(iBgColor));
            }
        });
        alert.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onClickBtnColor(View v){
        switch (v.getId()) {
            case R.id.btnBlue:
                //btnBgColor.setBackground(getResources().getDrawable(R.drawable.circle_blue));
                iBgColor = R.color.blue;
                break;

            case R.id.btnGrey:
                //btnBgColor.setBackground(getResources().getDrawable(R.drawable.circle_grey));
                iBgColor = R.color.grey;
                break;

            case R.id.btnRed:
                //btnBgColor.setBackground(getResources().getDrawable(R.drawable.circle_red));
                iBgColor = R.color.red;
                break;

            case R.id.btnPurple:
                //btnBgColor.setBackground(getResources().getDrawable(R.drawable.circle_purple));
                iBgColor = R.color.purple;
                break;
        }
        dbHelper.updateColorTodo(todo.getNumID(), iBgColor);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(iBgColor));
    }

    // Méthode pour supprimer un élément dans la liste des tâches (en mode édition)
    public void onClickDeleteItem(View v){
        View parent = (View) v.getParent();
        TextView ele = parent.findViewById(R.id.txtItem);

        dbHelper.deleteItemTodo(ele.getText().toString(), todo.getNumID());

        updateListEdit();
    }

    // Méthode qui ajoute un élément dans la liste et dans la BDD
    public void onClickAddItem(View v){
        if(edtElement.getText().toString().length() > 0){
            dbHelper.addToDoItem(todo.getNumID(), edtElement.getText().toString());
            edtElement.getText().clear();

            if(modeEdit){
                updateListEdit();
            } else {
                updateList();
            }
        }
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

        // Si 0, alors on affiche rien, sinon on affiche la liste des tags
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

            // affiche le texte pour les libellés s'il y en a
            lblTag.setVisibility(View.VISIBLE);
            txtTags.setVisibility(View.VISIBLE);
        } else {
            // cache le texte pour les libellés s'il n'y en a pas
            lblTag.setVisibility(View.INVISIBLE);
            txtTags.setVisibility(View.INVISIBLE);
        }

        // Met en place la couleur de fond
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(todo.getBgColor()));
        //btnBgColor.setBackground(getWindow().getDecorView().getBackground());
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
