package com.example.remindme;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<TaskDataModel> implements View.OnClickListener, AdapterView.OnItemClickListener {

    private List<TaskDataModel> dataSet;
    Context mContext;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        TaskDataModel dataModel =  getItem(position);
        Log.d("List item onItemClick", "************************"+dataModel.getName());
        Intent intent = new Intent(this.mContext, PlaceResultActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("category", dataModel.getCategory());
        this.mContext.startActivity(intent);
    }

    // View lookup cache
    private static class ViewHolder {
        TextView task_name;
        TextView task_category;
    }

    public CustomAdapter(List<TaskDataModel> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        TaskDataModel dataModel =  getItem(position);
        Log.d("List item clicked", "************************"+dataModel.getName());

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TaskDataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.task_name = (TextView) convertView.findViewById(R.id.task_name);
            viewHolder.task_category = (TextView) convertView.findViewById(R.id.task_category);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.task_name.setText(dataModel.getName());
        viewHolder.task_category.setText(dataModel.getCategory());
        result.setFocusable(false);
        // Return the completed view to render on screen
        return result;
    }
}