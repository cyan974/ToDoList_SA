package com.example.todolist_sa.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

import com.example.todolist_sa.DTO.Tag;
import com.example.todolist_sa.DTO.ToDo;
import com.example.todolist_sa.DTO.ToDoItem;
import com.example.todolist_sa.R;

import java.util.ArrayList;

public class AdapterToDo extends ArrayAdapter<ToDo> {
    Context context;

    public AdapterToDo(Context c, ArrayList<ToDo> todoList){
        super(c, 0, todoList);
        this.context = c;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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
        TextView tags = convertView.findViewById(R.id.txtTags);
        CardView cardView = convertView.findViewById(R.id.cvTodo);

        // Met en place la couleur de fond
        cardView.setCardBackgroundColor(context.getResources().getColor(toDo.getBgColor()));

        // Met en place le titre
        title.setText(toDo.getTitle());

        // Formatage pour la description (afficher tous les items (liste des tâches) ensemble dans un seul textview)
        Integer cpt = 0;
        String str = "";
        for(ToDoItem item:toDo.getListItems()){
            if(cpt < 5 && !item.getCompleted()) {
                str += "- " + item.getName() +"\n";
                cpt++;
            }
        }
        description.setText(str);

        // Formatage pour les tags (afficher tous les tags ensemble dans un seul textview)
        str = "";
        for(Tag tag:toDo.getListTags()){
            str += "(" + tag.getLibelle() + ") ";
        }
        tags.setText(str);

        // Met en place la date
        date.setText(toDo.getEndDate().toString());
        return convertView;
    }
}
