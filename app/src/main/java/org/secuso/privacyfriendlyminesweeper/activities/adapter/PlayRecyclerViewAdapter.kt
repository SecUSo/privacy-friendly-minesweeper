package org.secuso.privacyfriendlyminesweeper.activities.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.secuso.privacyfriendlyminesweeper.R;
import org.secuso.privacyfriendlyminesweeper.activities.helper.CellView;
import org.secuso.privacyfriendlyminesweeper.activities.model.grid.MinesweeperCell;

import kotlin.Pair;

/**
 * @author max-dreger, Patrick Schneider
 * @version 20221218
 * This class implements an adapter to handle the views of the cells on the playing field
 */
public class PlayRecyclerViewAdapter extends RecyclerView.Adapter<PlayRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private MinesweeperCell[] mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private int maxHeightOfCells;
    private int size;
    private int[] colors;
    private int backgroundColor;

    // data is passed into the constructor
    public PlayRecyclerViewAdapter(Context context, MinesweeperCell[] data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
    }

    // data is passed into the constructor
    public PlayRecyclerViewAdapter(Context context, MinesweeperCell[] data, int maxHeight, int size, int[] colors, int backgroundColor) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        maxHeightOfCells = maxHeight;
        this.colors = colors;
        this.backgroundColor = backgroundColor;
        this.context = context;
        this.size = size;
    }

    // inflates the cell layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.playingfield_cell, parent, false);
        view.findViewById(R.id.playingfield_row).setLayoutParams(new LinearLayout.LayoutParams(size, size));
        view.findViewById(R.id.playingfield_row).requestLayout();

        return new ViewHolder(view);
    }

    // binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
  //      holder.myTextView.setText(String.valueOf(mData[position]));
        TextView cellBackground = holder.itemView.findViewById(R.id.cell_background);
        TextView cellForeground = holder.itemView.findViewById(R.id.cell_decoration);
        MinesweeperCell cell = mData[position];
//        cellView.setWidth(maxHeightOfCells);
//        cellView.setHeight(maxHeightOfCells);

        switch (cell.getState()) {
            case FLAGGED:
                cellForeground.setBackgroundResource(R.drawable.flagge);
                break;
            case REVEALED:
                if (cell.isBomb()) {
                    cellForeground.setBackgroundResource(R.drawable.mine_x);
                } else {
                    if (cell.getBombs() == 0) {
                        cellForeground.setText("");
                    } else {
                        cellForeground.setText(String.valueOf(cell.getBombs()));
                    }
                    cellForeground.setTextColor(colors[cell.getBombs()]);
                    cellForeground.setBackgroundResource(android.R.color.transparent);
                    cellBackground.setBackgroundColor(backgroundColor);
                }
                break;
            case COVERED:
                cellForeground.setBackgroundResource(android.R.color.transparent);
                break;
        }
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
    public MinesweeperCell getItem(int index) {
        return mData[index];
    }

    public void setItem(int index, MinesweeperCell cell) {
        mData[index]= cell;
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
