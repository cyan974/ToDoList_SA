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
        // Get the data item for this position
        ToDoItem itemTodo = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_detail_edit, parent, false);
        }

        TextView name = convertView.findViewById(R.id.txtItem);
        CheckBox checkBox = convertView.findViewById(R.id.cbxChecked);
        ImageView editItem = convertView.findViewById(R.id.editItem);

        name.setText(itemTodo.getName());

        if(itemTodo.getCompleted()){
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

        return convertView;
    }
}
