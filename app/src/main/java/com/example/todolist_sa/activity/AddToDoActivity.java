package com.example.todolist_sa.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.todolist_sa.DTO.Tag;
import com.example.todolist_sa.DTO.ToDo;
import com.example.todolist_sa.DTO.ToDoItem;
import com.example.todolist_sa.R;
import com.example.todolist_sa.sqlite.DbHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddToDoActivity extends AppCompatActivity {
    // Constantes
    private static final Integer RES_CHOOSE_TAG = 1;
    private static final Integer RES_TAKE_PICTURE = 2;

    // Propriétés
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
    private ImageView imgTodo;

    private LocalDate endDate;

    private ToDo todo;

    private String photoPath = null;

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
        imgTodo = findViewById(R.id.imgTodo);

        lblLibelle.setVisibility(View.INVISIBLE);

        // Gestion de l'affichage pour la ListView
        lvItem = findViewById(R.id.listItem);
        listItems = new ArrayList<>();
        mAdapter = new ArrayAdapter(this, R.layout.list_item_add_todo, R.id.txtElement, listItems);
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

                startActivityForResult(itnLibelle, RES_CHOOSE_TAG);
                return true;

            case R.id.action_photo:
                onClickTakePicture();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Méthode déclenché lorsque l'activity recoit un résultat
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Retour de l'appel de l'activity pour les libellés
        if(requestCode == RES_CHOOSE_TAG && resultCode == RESULT_OK){
            // Récupérer notre objet todo
            todo = (ToDo) data.getSerializableExtra("TODO");
            restoreInfo();
        }

        // Retour de l'appel de l'appareil photo
        if(requestCode == RES_TAKE_PICTURE && resultCode == RESULT_OK){
            // Récupérer l'image
            /*Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //Bitmap image = BitmapFactory.decodeFile(photoPath);
            imgTodo.setImageBitmap(imageBitmap);*/
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

    // Méthode pour accéder à l'appareil photo et prendre une photo et mémorise dans un fichier temporaire
    public void onClickTakePicture() {
        /*Intent itnPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // test pour contrôler que l'intent peut être créé
        if(itnPicture.resolveActivity(getPackageManager()) != null){
            startActivityForResult(itnPicture, RES_TAKE_PICTURE);

            // Crée un nom de fichier unique
            String time = new SimpleDateFormat("yyyyMMdd HHmmss").format(new Date());
            File photoDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

            try {
                File photoFile = File.createTempFile("photo"+time,".jpg", photoDir);

                // Enregistre le chemin complet
                photoPath = photoFile.getAbsolutePath();

                // Créer l'URI
                Uri photoUri = FileProvider.getUriForFile(AddToDoActivity.this,
                        AddToDoActivity.this.getApplicationContext().getPackageName()+".provider",
                        photoFile);

                // Transfert URI vers l'intent pour enregistrer la photo dans le fichier temporaire
                itnPicture.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                // ouvrir l'activity par rapport à l'intent
                startActivityForResult(itnPicture, RES_TAKE_PICTURE);
            } catch (IOException e){
                e.printStackTrace();
            }

        }*/

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

    public void onClickEditElement(View v){
        /*View parent = (View) v.getParent();
        TextView ele = parent.findViewById(R.id.txtItem);

        final String oldName = ele.getText().toString();
        final EditText input = new EditText(AddToDoActivity.this);
        input.setText(oldName);

        AlertDialog alert = new AlertDialog.Builder(AddToDoActivity.this).create();
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
        alert.show();*/
    }

    // Méthode qui ouvre un AlertDialog pour afficher une vue qui contient un choix de couleur pour le background
    public void onClickChooseColor(View v){
        final View customLayout = getLayoutInflater().inflate(R.layout.list_color_add, null);

        AlertDialog alert = new AlertDialog.Builder(AddToDoActivity.this).create();
        alert.setTitle("Choisir une couleur de fond");
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
