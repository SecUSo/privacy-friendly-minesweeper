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

import java.util.ArrayList;

/**
 * @author I3ananas
 * @version 20180803
 * This class implements an adapter and its functionality to handle the items/views in a list of top times
 */
public class TopTimesRecyclerViewAdapter extends RecyclerView.Adapter<TopTimesRecyclerViewAdapter.ViewHolder> {

    private ArrayList<ArrayList<String>> topTimes;

    //pass data to constructor
    public TopTimesRecyclerViewAdapter(ArrayList<ArrayList<String>> parameters){
        this.topTimes = parameters;
    }

    //fill text views with data about top times
    @Override
    public void onBindViewHolder(@NonNull TopTimesRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.rank.setText(String.valueOf(position + 1));
        holder.topTime.setText(topTimes.get(position).get(0));
        holder.topTimeDate.setText(topTimes.get(position).get(1));
    }

    //get number of top times
    @Override
    public int getItemCount(){
        return topTimes.size();
    }

    //get list element for a single top time
    @Override
    public TopTimesRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        ViewGroup topTime = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.top_time_list_element, parent, false);
        return new TopTimesRecyclerViewAdapter.ViewHolder(topTime);
    }

    //inner class for a list element representing a single top time
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView rank;
        private TextView topTime;
        private TextView topTimeDate;

        //get text views to display data about top times
        private ViewHolder(ViewGroup top_time_list_element){
            super(top_time_list_element);
            this.rank = (TextView) top_time_list_element.getChildAt(0);
            this.topTime = (TextView) top_time_list_element.getChildAt(1);
            this.topTimeDate = (TextView) top_time_list_element.getChildAt(2);
        }
    }
}
