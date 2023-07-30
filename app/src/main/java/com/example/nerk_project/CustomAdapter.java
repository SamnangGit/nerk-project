package com.example.nerk_project;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.nerk_project.model.ToDoModel;


public class CustomAdapter  extends ArrayAdapter<ToDoModel> {
    private ArrayList<ToDoModel> todoModelList;
    Context context;

    public CustomAdapter(ArrayList<ToDoModel> data, Context context){
        super(context, R.layout.todo_item_layout, data);
        this.todoModelList = data;
        this.context = context;
    }

    // View Lookup Cache
    private static class ViewHolder{
        TextView tvTime;
        TextView tvTitle;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        ToDoModel dataModel = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;
        final View result;

        if (convertView == null){
            viewHolder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());

            convertView = inflater.inflate(
                    R.layout.todo_item_layout,
                    parent,
                    false
            );

            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);

            result = convertView;
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        // Get the data from the model class
        viewHolder.tvTime.setText(dataModel.getTime());
        viewHolder.tvTitle.setText(dataModel.getTitle());

        return convertView;
    }
}
