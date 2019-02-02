package com.example.remindme;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView
        .Adapter<MyRecyclerViewAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<PlaceDataModel> mDataset;
    private static MyClickListener myClickListener;

    public MyRecyclerViewAdapter(ArrayList<PlaceDataModel> myDataset) {
        this.mDataset = myDataset;
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public void addItem(PlaceDataModel dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_card, parent, false);


        DataObjectHolder dataObjectHolder = new DataObjectHolder(view, mDataset);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.placeName.setText(mDataset.get(position).getName());
        holder.placeCategory.setText(mDataset.get(position).getCategory());
        new AsyncTaskLoadImage(holder.placeIcon).execute(mDataset.get(position).getImageUrl());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView placeName;
        TextView placeCategory;
        ImageView placeIcon;
        private ArrayList<PlaceDataModel> mDataset;

        public DataObjectHolder(View itemView, ArrayList<PlaceDataModel> mDataset) {
            super(itemView);
            placeName = (TextView) itemView.findViewById(R.id.place_name);
            placeCategory = (TextView) itemView.findViewById(R.id.place_category);
            placeIcon = (ImageView) itemView.findViewById(R.id.place_icon);
            this.mDataset = mDataset;
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(mDataset.get(getAdapterPosition()), v);
        }
    }

    public interface MyClickListener {
        public void onItemClick(PlaceDataModel data, View v);
    }
}