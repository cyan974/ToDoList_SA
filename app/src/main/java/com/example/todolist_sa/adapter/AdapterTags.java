package com.example.todolist_sa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.todolist_sa.DTO.Tag;
import com.example.todolist_sa.DTO.ToDoItem;
import com.example.todolist_sa.R;

import java.util.ArrayList;

public class AdapterTags extends ArrayAdapter<Tag> {
    Context context;

    public AdapterTags(Context c, ArrayList<Tag> tagList){
        super(c, 0, tagList);
        this.context = c;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // On récupère l'objet tag par rapport à la position (qui commence de 0)
        Tag itemTag = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_tags_select, parent, false);
        }

        TextView name = convertView.findViewById(R.id.txtTag);
        CheckBox checkBox = convertView.findViewById(R.id.cbxSelected);

        // Met en place le texte du tags (libellé)
        name.setText(itemTag.getLibelle());

        // Vérifie si le tag est dans l'état sélectionné, pour mettre la checkbox coché ou non
        if(itemTag.getSelected()){
            checkBox.setChecked(true);
        }

        return convertView;
    }
}
