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
        // Get the data item for this position
        Tag itemTag = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_tags_select, parent, false);
        }

        TextView name = convertView.findViewById(R.id.txtTag);
        CheckBox checkBox = convertView.findViewById(R.id.cbxSelected);

        name.setText(itemTag.getLibelle());

        return convertView;
    }
}
