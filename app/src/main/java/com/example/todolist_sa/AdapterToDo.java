package com.example.todolist_sa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.todolist_sa.DTO.ToDo;
import com.example.todolist_sa.DTO.ToDoItem;

import java.util.ArrayList;

public class AdapterToDo extends ArrayAdapter<ToDo> {
    Context context;

    AdapterToDo(Context c, ArrayList<ToDo> todoList){
        super(c, 0, todoList);
        this.context = c;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        ToDo toDo = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_todo, parent, false);
        }

        TextView title = convertView.findViewById(R.id.txtTitle);
        TextView description = convertView.findViewById(R.id.txtDescription);
        TextView date = convertView.findViewById(R.id.txtDate);

        title.setText(toDo.getTitle());

        Integer cpt = 0;
        String str = "";
        for(ToDoItem item:toDo.getListItems()){
            if(cpt < 5 && !item.getCompleted()) {
                str += "- " + item.getName() +"\n";
                cpt++;
            }
        }
        description.setText(str);

        date.setText(toDo.getEndDate().toString());
        return convertView;
    }
}
