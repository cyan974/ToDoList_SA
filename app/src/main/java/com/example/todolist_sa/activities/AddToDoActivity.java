package com.example.todolist_sa.activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.todolist_sa.DTO.Tag;
import com.example.todolist_sa.DTO.ToDo;
import com.example.todolist_sa.DTO.ToDoItem;
import com.example.todolist_sa.R;
import com.example.todolist_sa.sqlite.DbHelper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

public class AddToDoActivity extends AppCompatActivity {
    // Constantes
    private static final Integer RES_CHOOSE_TAG = 1;
    private static final Integer RES_GALLERY = 3;
    private static final Integer RES_PERMISSION = 4;

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

    private ToDo todo;

    // Date
    private int iYear;
    private int iMonth;
    private int iDay;

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

        // Liens avec les objets graphiques
        edtTitre = findViewById(R.id.edtTitre);
        txtDate = findViewById(R.id.txtDate);
        edtElement = findViewById(R.id.edtElement);
        txtTags = findViewById(R.id.txtTags);
        lblLibelle = findViewById(R.id.lblLibelle);
        btnColor = findViewById(R.id.btnColor);
        imgTodo = findViewById(R.id.imgTodo);
        lvItem = findViewById(R.id.listItem);

        // Cache le label, car à la création au début il n'y a pas de libéllés selectionnés (s'affiche dès qu'il y en a)
        lblLibelle.setVisibility(View.INVISIBLE);

        // Gestion de l'affichage pour la ListView
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

            case R.id.action_gallery:
                onClickGallery();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Méthode déclenché lorsque l'activity recoit un résultat
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Retour de l'appel de l'activity pour la sélection des libellés
        if(requestCode == RES_CHOOSE_TAG && resultCode == RESULT_OK){
            // Récupérer notre objet todo
            todo = (ToDo) data.getSerializableExtra("TODO");
            restoreInfo();
        }

        // Retour de l'appel de la gallerie | Vérifie si une image est récupéré
        if(requestCode == RES_GALLERY && resultCode == RESULT_OK){
            // Accès à l'image à partir de data
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            // Curseur d'accès au chemin de l'image
            Cursor cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);

            // Position sur la première ligne (normalement une seule)
            if(cursor.moveToFirst()){
                // Récupération du chemin précis de l'image
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                todo.setImgPath(cursor.getString(columnIndex));
                cursor.close();

                // Récupération image + affichage
                Bitmap image = BitmapFactory.decodeFile(todo.getImgPath());
                imgTodo.setImageBitmap(image);
            }

        } else {
            Toast.makeText(this, "Aucune image sélectionnée", Toast.LENGTH_LONG).show();
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

    // Méthode pour sauvegarder les informations dans un objet
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveInfo(){
        // Sauvegarder le titre dans l'objet ToDo
        if(edtTitre.getText().toString().length() > 0)
            todo.setTitle(edtTitre.getText().toString());

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
        // Affiche le titre
        if(todo.getTitle() != null){
            edtTitre.setText(todo.getTitle());
        }

        // Affiche la date
        if(todo.getEndDate() != null){
            txtDate.setText(todo.getEndDate().toString());
        }

        // Affiche les différents items (liste des choses à faire)
        if(todo.getListItems().size() > 0){
            listItems.clear();
            for(ToDoItem item:todo.getListItems()){
                listItems.add(item.getName());
            }
            mAdapter.notifyDataSetChanged();
        }

        // Affiche les différents tags (libellés) pour la tâche
        if(todo.getListTags().size() > 0){
            String strTags ="";
            Integer cpt = 0;
            lblLibelle.setVisibility(View.VISIBLE);
            for(Tag tag:todo.getListTags()){
                strTags += "(" + tag.getLibelle() + ") ";
                cpt++;
            }

            // Met au pluriel ou au singulier selon le nombre de tags (libellés) présents
            if (cpt > 1) {
                lblLibelle.setText("Libellés :");
            } else {
                lblLibelle.setText("Libellé :");
            }
            txtTags.setText(strTags);
        }
    }

    // Méthode lorsqu'on clique pour ouvre la gallerie
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onClickGallery(){
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

    // Méthode onClick pour l'ajout d'une date via l'interface d'un calendrier
    public void onClickDate(View v){
        // Obitens la date, le mois et l'année actuels du calendrier
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // année actuelle
        int mMonth = c.get(Calendar.MONTH); // mois actuel
        int mDay = c.get(Calendar.DAY_OF_MONTH); // jour actuel

        // Date picker dialog, qui permet de choisir une date
        datePickerDialog = new DatePickerDialog(AddToDoActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        // Saisie la date choisi dans l'objet todo
                        todo.setEndDate(LocalDate.of(year,monthOfYear+1,dayOfMonth));

                        // Affiche la date dans le TextView
                        txtDate.setText(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);

                        // Récupère l'année, le mois et le jour pour gérer la notification plus tard
                        iYear = year;
                        iMonth = monthOfYear;
                        iDay = dayOfMonth;
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        datePickerDialog.show();
    }

    // Méthode pour ajouter un élément dans la liste des tâches
    public void onClickAddElement(View v){
        // Test pour savoir si l'EditText est vide ou non
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

    // Méthode pour modifier l'élément de la liste (modifie le nom)
    public void onClickEditElement(View v){
        View parent = (View) v.getParent();
        final TextView ele = parent.findViewById(R.id.txtElement);

        final String oldName = ele.getText().toString();
        final EditText input = new EditText(AddToDoActivity.this);
        input.setText(oldName);

        // Affiche un alertDialog avec un edittext pour saisir un nouveau nom
        AlertDialog alert = new AlertDialog.Builder(AddToDoActivity.this).create();
        alert.setTitle("Modification d'un élément");
        alert.setIcon(R.drawable.logo);
        alert.setView(input);
        alert.setButton(Dialog.BUTTON_POSITIVE,"Valider",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = input.getText().toString();

                // Effectue le changement de nom seulement si le nouveau nom est différent de l'ancien nom
                if(!oldName.equals(newName)){
                    listItems.set(listItems.indexOf(oldName), newName);
                    mAdapter.notifyDataSetChanged();
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

    // Méthode qui ouvre un AlertDialog pour afficher une vue qui contient un choix de couleur pour le background
    public void onClickChooseColor(View v){
        final View customLayout = getLayoutInflater().inflate(R.layout.list_color_add, null);

        // Ouvre un AlertDialog qui affiche notre vue de sélection de couleur
        AlertDialog alert = new AlertDialog.Builder(AddToDoActivity.this).create();
        alert.setTitle("Choisir une couleur de fond");
        alert.setIcon(R.drawable.logo);
        alert.setView(customLayout);
        alert.setButton(Dialog.BUTTON_POSITIVE,"OK",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        // Remet la couleur blanche en tant que couleur principale
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
        // Sauvegarder toutes les infos présente sur la vue dans un objet ToDo
        saveInfo();

        // Vérifie que le titre et la date ont été saisie, sinon ne permet pas d'enregistrer une tâche
        if(todo.getTitle() != null && todo.getEndDate() != null){
            dbHelper.addToDo(todo);

            // Ajout une notification à la tâche
            addNotif();

            // Ferme l'activité
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

    // Méthode qui crée une notification pour la tâche
    public void addNotif(){
        // Met en place la notification
        long idTodo = dbHelper.searchTodoIDbyTitle(todo.getTitle());
        int notifID = (int) idTodo;
        String message = "Votre tâche " + '"' + todo.getTitle() + '"' + " est terminée !";

        Intent itnNotif = new Intent(AddToDoActivity.this, AlarmReceiver.class);

        // Passe l'id de la notification et le message en paramètre
        itnNotif.putExtra("notifID", notifID);
        itnNotif.putExtra("message", message);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(AddToDoActivity.this, 0, itnNotif, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Défini l'heure à laquelle la notification va s'afficher
        int hour = 12;
        int minute = 00;
        int second = 00;

        // On set les valeurs qu'on a défini pour le déclenchement de la notification
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.YEAR, iYear);
        startTime.set(Calendar.MONTH, iMonth);
        startTime.set(Calendar.DAY_OF_MONTH, iDay);
        startTime.set(Calendar.HOUR_OF_DAY, hour);
        startTime.set(Calendar.MINUTE, minute);
        startTime.set(Calendar.SECOND, second);

        Long alarmStartTime = startTime.getTimeInMillis();

        alarm.set(AlarmManager.RTC_WAKEUP, alarmStartTime, alarmIntent);

        createNotificationChannel();
    }

    // méthode d'alarme qui crée un channel pour la notification
    private void createNotificationChannel() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("primary_channel_id","channel_name" , NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(R.color.colorPrimary);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("DESCRIPTION");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
