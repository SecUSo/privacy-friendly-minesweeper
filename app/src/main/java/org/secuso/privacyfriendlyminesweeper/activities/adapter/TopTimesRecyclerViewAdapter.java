/*
 This file is part of Privacy Friendly Minesweeper.

 Privacy Friendly Minesweeper is free software:
 you can redistribute it and/or modify it under the terms of the
 GNU General Public License as published by the Free Software Foundation,
 either version 3 of the License, or any later version.

 Privacy Friendly Minesweeper is distributed in the hope
 that it will be useful, but WITHOUT ANY WARRANTY; without even
 the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Privacy Friendly Minesweeper. If not, see <http://www.gnu.org/licenses/>.
 */

package org.secuso.privacyfriendlyminesweeper.activities.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import org.secuso.privacyfriendlyminesweeper.R;

/**
 * @author I3ananas
 * @version 20180803
 * This class implements an adapter to handle the items/views in a list of top times
 */
public class TopTimesRecyclerViewAdapter extends RecyclerView.Adapter<TopTimesRecyclerViewAdapter.ViewHolder> {

    private String[][] toptimes;

    //pass data to constructor
    public TopTimesRecyclerViewAdapter(String[][] parameters){
        this.toptimes = parameters;
    }

    //fill text views with data about top times
    @Override
    public void onBindViewHolder(@NonNull TopTimesRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.topTime.setText(toptimes[position][0]);
        holder.topTimeDate.setText(toptimes[position][1]);
    }

    //get number of top times
    @Override
    public int getItemCount(){
        return toptimes.length;
    }

    //get list element for a single top time
    public TopTimesRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        ViewGroup topTime = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.top_time_list_element, parent, false);
        TopTimesRecyclerViewAdapter.ViewHolder vh = new TopTimesRecyclerViewAdapter.ViewHolder(topTime);
        return vh;
    }

    //inner class for a list element representing a single top time
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView topTime;
        public TextView topTimeDate;

        //get text views to display data about top times
        public ViewHolder(ViewGroup top_time_list_element){
            super(top_time_list_element);
            this.topTime = (TextView) top_time_list_element.getChildAt(0);
            this.topTimeDate = (TextView) top_time_list_element.getChildAt(1);
        }
    }
}
