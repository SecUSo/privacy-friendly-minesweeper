package org.secuso.privacyfriendlyminesweeper.activities.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.os.Bundle;
import org.secuso.privacyfriendlyminesweeper.R;
import org.secuso.privacyfriendlyminesweeper.activities.helper.BaseActivity;
import org.secuso.privacyfriendlyminesweeper.activities.helper.CellView;

public class PlayRecyclerViewAdapter extends RecyclerView.Adapter<PlayRecyclerViewAdapter.ViewHolder> {

    private int[] mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private int maxHeightOfCells;
    private int maxWidthOfCells;

    // data is passed into the constructor
    public PlayRecyclerViewAdapter(Context context, int[] data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // data is passed into the constructor
    public PlayRecyclerViewAdapter(Context context, int[] data, int maxHeight) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        maxHeightOfCells = maxHeight;
    }

    // inflates the cell layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.playingfield_cell, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
  //      holder.myTextView.setText(String.valueOf(mData[position]));
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return mData.length;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CellView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = (CellView) itemView.findViewById(R.id.cell);
            itemView.setOnClickListener(this);
            myTextView.setTag(maxHeightOfCells);
   //         myTextView.setTag(R.id.width ,maxWidthOfCells);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public int getItem(int id) {
        return mData[id];
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
