package com.example.todolist_sa;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import androidx.annotation.RequiresApi;

import com.example.todolist_sa.DTO.ToDo;
import com.example.todolist_sa.sqlite.DbHelper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddToDoActivity extends Activity {
    DbHelper dbHelper;
    DatePickerDialog datePickerDialog;

    List<String> listItems = new ArrayList<>();
    ListView listItem;
    private ArrayAdapter<String> mAdapter;

    EditText edtTitre;
    EditText edtElement;
    TextView txtDate;

    LocalDate endDate;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);

        edtTitre = findViewById(R.id.edtTitre);
        txtDate = findViewById(R.id.txtDate);
        edtElement = findViewById(R.id.edtElement);
        listItem = findViewById(R.id.listItem);
        listItem.setItemsCanFocus(true);

        dbHelper = new DbHelper(this);

        mAdapter = new ArrayAdapter(this, R.layout.list_item_todo, R.id.txtElement, listItems);
        listItem.setAdapter(mAdapter);


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


    public void onClickAddElement(View v){
        if(edtElement.getText().toString().length() > 0){
            listItems.add(edtElement.getText().toString());
            mAdapter.notifyDataSetChanged();

            edtElement.getText().clear();
        }
    }

    public void onClickDeleteElement(View v){
        View parent = (View) v.getParent();
        TextView ele = parent.findViewById(R.id.txtElement);
        listItems.remove(ele.getText().toString());
        mAdapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClickAdd(View v){
        if(edtTitre.getText().toString().length() > 0 && txtDate.getText().toString() != ""){
            if(dbHelper.addToDo(edtTitre.getText().toString(), endDate)){
            ToDo toDo = dbHelper.searchTodoByTitle(edtTitre.getText().toString());
                for(String name:listItems){
                    Boolean res = dbHelper.addToDoItem(toDo.getNumID(), name);
                }
            } else {
                // Afficher une erreur lors de l'ajout
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
