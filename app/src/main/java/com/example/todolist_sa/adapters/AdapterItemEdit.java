package com.example.todolist_sa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.todolist_sa.DTO.ToDoItem;
import com.example.todolist_sa.R;

import java.util.ArrayList;

public class AdapterItemEdit extends ArrayAdapter<ToDoItem> {
    Context context;

    public AdapterItemEdit(Context c, ArrayList<ToDoItem> itemList){
        super(c, 0, itemList);
        this.context = c;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // On récupère l'objet itemTodo (élément de la liste) par rapport à la position (qui commence de 0)
        ToDoItem itemTodo = getItem(position);

        // Affiche la liste des items en mode édition (avec les boutons d'étidion en plus)
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_detail_edit, parent, false);
        }

        TextView name = convertView.findViewById(R.id.txtItem);
        CheckBox checkBox = convertView.findViewById(R.id.cbxChecked);

        name.setText(itemTodo.getName());

        // Selon si l'élément a été effectué ou pas, met la checkbox a checked ot not
        if(itemTodo.getCompleted()){
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

        return convertView;
    }
}
