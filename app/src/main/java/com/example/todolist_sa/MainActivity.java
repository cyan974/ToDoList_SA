package com.example.todolist_sa;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.todolist_sa.DTO.ToDo;
import com.example.todolist_sa.DTO.ToDoItem;
import com.example.todolist_sa.sqlite.DbHelper;

import java.util.ArrayList;
import java.util.List;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
//Main
public class MainActivity extends AppCompatActivity {
    private DbHelper mHelper;
    private ListView lvToDo;
    private AdapterToDo adapter;
    private ArrayList<ToDo> arrayOfTodo;

    //Pour la notification d'alarme
    private NotificationManager mNotificationManager;
    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID = "primary_channel_id";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Mettre avant le onCreate (thème pour la barre de menu)
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Informations de la table sur la bdd
        mHelper = new DbHelper(this);
        //mHelper.insertFakeData();

        // ArrayList des tâches
        arrayOfTodo = new ArrayList<>();

        // ListView pour les ToDos
        lvToDo = findViewById(R.id.list_todo);

        // met à jour la liste des tâches
        updateList();

        //notification alarme
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // a rajouter dans la vue listtodo
        ToggleButton toggleButton = findViewById(R.id.toggleButton);
        Intent intent = new Intent(this, AlarmReceiver.class);
        final boolean alarmSet = (PendingIntent.getBroadcast(this, NOTIFICATION_ID, intent, PendingIntent.FLAG_NO_CREATE) != null);
        toggleButton.setChecked(alarmSet);

        final PendingIntent pendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (alarmManager != null){
                        Calendar calendar = Calendar.getInstance();
                        // référence à retravailler avec la date de fin de la liste
                        calendar.set(Calendar.HOUR, 11);
                        calendar.set(Calendar.MINUTE, 11);
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        Toast.makeText(MainActivity.this, R.string.toast_message, Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    if (alarmManager != null){
                        alarmManager.cancel(pendingIntent);
                        mNotificationManager.cancelAll();
                    }
                }
            }
        });
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    // méthode d'alarme
    private void createNotificationChannel() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, getString(R.string.channel_name), NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.WHITE);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription(getString(R.string.channel_desc));
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    // Méthode qui s'exécute à chaque fois qu'on revient sur cette activité
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume(){
        super.onResume();
        updateList();
    }

    // Méthode qui appelle une vue pour créer une tâche
    public void addTask(View v){
        Intent itnAddTask = new Intent(MainActivity.this, AddToDoActivity.class);
        startActivity(itnAddTask);
    }

    // Méthode qui appelle une vue pour visualiser les informations d'une tâche
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void viewTask(View v){
        TextView title = v.findViewById(R.id.txtTitle);
        ToDo todo = mHelper.searchTodoByTitle(title.getText().toString());

        Intent itnViewTask = new Intent (MainActivity.this, ViewToDoActivity.class);
        itnViewTask.putExtra("TODO", todo);
        startActivity(itnViewTask);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateList() {
        if (adapter != null) {
            arrayOfTodo.clear();
            adapter.clear();
        }

        arrayOfTodo = mHelper.getListToDo();
        adapter = new AdapterToDo(this, arrayOfTodo);
        lvToDo.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void deleteTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = parent.findViewById(R.id.txtTitle);

        String task = String.valueOf(taskTextView.getText());
        mHelper.deleteToDoByTitle(task);
        updateList();
    }

}
