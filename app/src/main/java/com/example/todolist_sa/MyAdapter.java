package com.example.todolist_sa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyAdapter extends ArrayAdapter<String> {
    Context context;
    String[] titles;
    String[] descriptions;

    MyAdapter(Context c, String[] titles, String[] descriptions){
        super(c, R.layout.list_todo, R.id.txtTitle, titles);
        this.context = c;
        this.titles = titles;
        this.descriptions = descriptions;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View toDoView = layoutInflater.inflate(R.layout.list_todo, parent, false);
        TextView title = toDoView.findViewById(R.id.txtTitle);
        TextView description = toDoView.findViewById(R.id.txtDescription);
        title.setText(titles[position]);
        description.setText(descriptions[position]);
        return toDoView;
    }
}
