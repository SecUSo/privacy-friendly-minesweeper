package org.secuso.privacyfriendlyminesweeper.activities.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.secuso.privacyfriendlyminesweeper.R
import org.secuso.privacyfriendlyminesweeper.activities.helper.CellView
import org.secuso.privacyfriendlyminesweeper.activities.model.grid.MinesweeperCell

/**
 * @author max-dreger, Patrick Schneider
 * @version 20221218
 * This class implements an adapter to handle the views of the cells on the playing field
 */
class PlayRecyclerViewAdapter(
    val context: Context,
    val layoutInflater: LayoutInflater,
    val data: Array<MinesweeperCell>,
    val maxHeight: Int,
    val size: Int,
    val colors: IntArray,
    val backgroundColor: Int,
    val onItemClickListener: ItemClickListener
) : RecyclerView.Adapter<PlayRecyclerViewAdapter.ViewHolder?>() {

    // inflates the cell layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = layoutInflater.inflate(R.layout.playingfield_cell, parent, false)
        view.findViewById<View?>(R.id.playingfield_row).setLayoutParams(LinearLayout.LayoutParams(size, size))
        view.findViewById<View?>(R.id.playingfield_row).requestLayout()

        return PlayRecyclerViewAdapter.ViewHolder(view, maxHeight, onItemClickListener)
    }

    // binds the data to the textview in each cell
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cellBackground = holder.itemView.findViewById<TextView>(R.id.cell_background)
        val cellForeground = holder.itemView.findViewById<TextView>(R.id.cell_decoration)
        val cell = data[position]

        when (cell.state) {
            MinesweeperCell.State.FLAGGED -> cellForeground.setBackgroundResource(R.drawable.flagge)
            MinesweeperCell.State.REVEALED -> if (cell.isBomb) {
                cellForeground.setBackgroundResource(R.drawable.mine_x)
            } else {
                if (cell.bombs == 0) {
                    cellForeground.setText("")
                } else {
                    cellForeground.setText(cell.bombs.toString())
                }
                cellForeground.setTextColor(colors[cell.bombs])
                cellForeground.setBackgroundResource(android.R.color.transparent)
                cellBackground.setBackgroundColor(backgroundColor)
            }

            MinesweeperCell.State.COVERED -> cellForeground.setBackgroundResource(android.R.color.transparent)
        }
    }

    // total number of cells
    override fun getItemCount(): Int {
        return data.size
    }

    // stores and recycles views as they are scrolled off screen
    class ViewHolder internal constructor(itemView: View, val maxHeight: Int, val onItemClickListener: ItemClickListener? = null) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var myTextView: CellView

        init {
            myTextView = itemView.findViewById<View?>(R.id.cell) as CellView
            itemView.setOnClickListener(this)
            myTextView.setTag(maxHeight)
        }

        override fun onClick(view: View?) {
            onItemClickListener?.onItemClick(view, getAdapterPosition())
        }
    }

    // convenience method for getting data at click position
    fun getItem(index: Int): MinesweeperCell? {
        return data[index]
    }

    fun setItem(index: Int, cell: MinesweeperCell?) {
        data[index] = cell!!
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }
}
