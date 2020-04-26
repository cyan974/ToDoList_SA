package com.example.todolist_sa.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.todolist_sa.DTO.Tag;
import com.example.todolist_sa.DTO.ToDo;
import com.example.todolist_sa.DTO.ToDoItem;
import com.example.todolist_sa.R;
import com.example.todolist_sa.adapters.AdapterItem;
import com.example.todolist_sa.adapters.AdapterItemEdit;
import com.example.todolist_sa.sqlite.DbHelper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

public class DetailToDoActivity extends AppCompatActivity {
    // Constantes
    private static final Integer RES_GALLERY = 1;
    private static final Integer RES_PERMISSION = 2;

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
    private ImageView editImg;
    private ImageView imgDetail;

    // Variables pour les couleurs
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

        // Liens avec les différents objets graphiques
        txtTitle = findViewById(R.id.txtTitle);
        txtDate = findViewById(R.id.txtDate);
        lvItem = findViewById(R.id.listTodo);
        editTitle = findViewById(R.id.editTitle);
        editDate = findViewById(R.id.editDate);
        txtTags = findViewById(R.id.txtTags);
        lblTag = findViewById(R.id.lblTags);
        edtElement = findViewById(R.id.edtElement);
        imgDetail = findViewById(R.id.imgDetail);
        editImg = findViewById(R.id.editImg);

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
        // Cela ajoute des éléments à la barre d'action si elle est présente.
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
        }
        return super.onOptionsItemSelected(item);
    }

    // Méthode déclenché lorsque l'activity recoit un résultat
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Récupération des informations de l'activité pour les libellés
        if (requestCode == 0 && resultCode == RESULT_OK) {
            todo = (ToDo) data.getSerializableExtra("TODO");
            setupInfos();
        }

        // Retour de l'appel de la gallerie
        if (requestCode == RES_GALLERY && resultCode == RESULT_OK) {
            // Accès à l'image à partir de data
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            // Curseur d'accès au chemin de l'image
            Cursor cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);

            // Position sur la première ligne (normalement une seule)
            if (cursor.moveToFirst()) {
                // Récupération du chemin précis de l'image
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String pathImg = cursor.getString(columnIndex);
                dbHelper.updateImageTodo(todo.getNumID(), pathImg);
                cursor.close();

                // Récupération image + affichage
                Bitmap image = BitmapFactory.decodeFile(pathImg);
                imgDetail.setImageBitmap(image);
            }
        }
    }

    // Méthode déclenché au retour de l'appel des demandes de persmissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Si on a la permission pour accéder à la gallerie est donné, alors on ouvre la gallerie pour récupérer une image
        if(requestCode == RES_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            openGallery();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Méthode qui met a jour si la tâche dans la liste a été effectué ou non et met à jour l'affichage des CheckBox (checked or not)
    public void onClickCheckBox(View v){
        View parent = (View) v.getParent();
        CheckBox cbx = parent.findViewById(R.id.cbxChecked);
        TextView nameItem = parent.findViewById(R.id.txtItem);

        dbHelper.updateCheckedItemTodo(todo.getNumID(), cbx.isChecked(), nameItem.getText().toString());

        // Selon le mode, met à jour la liste de manière différente
        if(modeEdit){
            updateListEdit();
        } else {
            updateList();
        }
    }

    // Méthode qui permet la modification du titre
    public void onClickEditTitle(View v){
        final String oldTitle = txtTitle.getText().toString();
        final EditText input = new EditText(DetailToDoActivity.this);
        input.setText(oldTitle);

        // Affiche un AlertDialog qui permet de modifier le titre
        AlertDialog alert = new AlertDialog.Builder(DetailToDoActivity.this).create();
        alert.setTitle("Modification du titre");
        alert.setIcon(R.drawable.logo);
        alert.setView(input);
        alert.setButton(Dialog.BUTTON_POSITIVE,"Valider",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newTitle = input.getText().toString();

                // Modifie le titre seulement si le nouveau titre est différent de l'ancien
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
        // Obitens la date, le mois et l'année actuels du calendrier
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // année actuelle
        int mMonth = c.get(Calendar.MONTH); // mois actuel
        int mDay = c.get(Calendar.DAY_OF_MONTH); // jour actuel

        // Date picker dialog, qui permet de choisir une date
        datePickerDialog = new DatePickerDialog(DetailToDoActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        // Récupère la date dans une variable
                        LocalDate endDate = LocalDate.of(year,monthOfYear+1,dayOfMonth);

                        // Modifie la date seulement si la nouvelle date est différente de l'ancienne
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

        // Affiche un AlertDialog qui permet de modifier le nom d'un élément
        AlertDialog alert = new AlertDialog.Builder(DetailToDoActivity.this).create();
        alert.setTitle("Modification d'un élément");
        alert.setIcon(R.drawable.logo);
        alert.setView(input);
        alert.setButton(Dialog.BUTTON_POSITIVE,"Valider",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = input.getText().toString();

                // modifie seulement si le nouveau nom est différent de l'ancien
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

        // Affiche AlertDialog qui permet de choisir une couleur grâce à une vue
        AlertDialog alert = new AlertDialog.Builder(DetailToDoActivity.this).create();
        alert.setTitle("Modifer la couleur de fond");
        alert.setIcon(R.drawable.logo);
        alert.setView(colorLayout);
        alert.setButton(Dialog.BUTTON_POSITIVE,"OK",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        // Permet de remettre la couleur par défaut pour le fond (le blanc)
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

    // Méthode lorsqu'on clique pour ouvre la gallerie
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onClickEditImage(){
        // Si la persmission pour accéder à la gallerie n'est pas donné, effectue une demande de permission pour y accéder
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, RES_PERMISSION);
        } else {
            // Ouvre la gallerie
            openGallery();
        }
    }

    // Méthode pour accéder à la gallerie du téléphone
    public void openGallery(){
        Intent itnGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(itnGallery, RES_GALLERY);
    }

    // Méthode qui choisit la couleur du background en fonction du bouton surlequel on a cliqué
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

            // Selon le mode, met à jour la liste des éléments
            if(modeEdit){
                updateListEdit();
            } else {
                updateList();
            }
        }
    }

    // Méthode qui permet la maj de la liste (en mode normal)
    private void updateList(){
        if (adapterItem != null) {
            listItems.clear();
            adapterItem.clear();
        }

        listItems = dbHelper.getListItemByTodo(todo.getNumID());
        adapterItem = new AdapterItem(this, listItems);
        lvItem.setAdapter(adapterItem);
    }

    // Méthode qui permet la maj de la liste (en mode édition)
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

        // Met en place l'image lié à la tâche
        Bitmap image = BitmapFactory.decodeFile(todo.getImgPath());
        imgDetail.setImageBitmap(image);
    }

    // Méthode qui permet de cacher le mode édition
    private void hideEdit(){
        // Cache les éléments pour modifier la tâche
        editTitle.setVisibility(View.INVISIBLE);
        editDate.setVisibility(View.INVISIBLE);
        editImg.setVisibility(View.INVISIBLE);
        updateList();
    }

    // Méthode qui permet de montrer le mode édition
    private void showEdit(){
        editTitle.setVisibility(View.VISIBLE);
        editDate.setVisibility(View.VISIBLE);
        editImg.setVisibility(View.VISIBLE);
        updateListEdit();
    }
}
