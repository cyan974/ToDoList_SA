package com.example.todolist_sa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.todolist_sa.DTO.ToDoItem;
import com.example.todolist_sa.R;

import java.util.ArrayList;

public class AdapterItem extends ArrayAdapter<ToDoItem> {
    Context context;

    public AdapterItem(Context c, ArrayList<ToDoItem> itemList){
        super(c, 0, itemList);
        this.context = c;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        ToDoItem itemTodo = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_detail, parent, false);
        }

        TextView name = convertView.findViewById(R.id.txtItem);
        CheckBox checkBox = convertView.findViewById(R.id.cbxChecked);

        name.setText(itemTodo.getName());

        if(itemTodo.getCompleted()){
            checkBox.setChecked(true);
            //name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            checkBox.setChecked(false);
        }

        return convertView;
    }
}
