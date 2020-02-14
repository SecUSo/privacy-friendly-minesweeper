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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.secuso.privacyfriendlyminesweeper.R;
import org.secuso.privacyfriendlyminesweeper.activities.PlayActivity;
import org.secuso.privacyfriendlyminesweeper.activities.SavedGamesActivity;
import org.secuso.privacyfriendlyminesweeper.database.DatabaseSavedGameProvide;
import org.secuso.privacyfriendlyminesweeper.database.PFMSQLiteHelper;

import java.util.ArrayList;

/**
 * @author I3ananas, max-dreger
 * @version 20180802
 * This class implements an adapter and its functionality to handle the items/views in a list of saved games
 * Saved games can be restarted from this list
 */
public class SavedGamesRecyclerViewAdapter extends RecyclerView.Adapter<SavedGamesRecyclerViewAdapter.ViewHolder> {

    private ArrayList<ArrayList<String>> savedGameParameters;
    private SavedGamesActivity savedGamesActivity;

    //pass data to constructor
    public SavedGamesRecyclerViewAdapter(ArrayList<ArrayList<String>> parameters, Context pSavedGamesActivity){
        this.savedGameParameters = parameters;
        savedGamesActivity = (SavedGamesActivity) pSavedGamesActivity;
    }

    //fill views with information corresponding to saved games
    @Override
    public void onBindViewHolder(@NonNull SavedGamesRecyclerViewAdapter.ViewHolder holder, int position) {
        if(savedGameParameters.get(position).size() > 0) {
            holder.id = Integer.valueOf(savedGameParameters.get(position).get(0));
            holder.savedGameMode.setText(savedGameParameters.get(position).get(1));
            holder.savedGamePlayingTime.setText(savedGameParameters.get(position).get(2));
            holder.savedGameDate.setText(savedGameParameters.get(position).get(3));
            if(savedGameParameters.get(position).get(1).equals(savedGamesActivity.getResources().getString(R.string.game_mode_easy))){
                holder.mine1.setImageAlpha(255);
                holder.mine2.setImageAlpha(100);
                holder.mine3.setImageAlpha(100);
            }
            if(savedGameParameters.get(position).get(1).equals(savedGamesActivity.getResources().getString(R.string.game_mode_medium))){
                holder.mine1.setImageAlpha(255);
                holder.mine2.setImageAlpha(255);
                holder.mine3.setImageAlpha(100);
            }
            if(savedGameParameters.get(position).get(1).equals(savedGamesActivity.getResources().getString(R.string.game_mode_difficult))){
                holder.mine1.setImageAlpha(255);
                holder.mine2.setImageAlpha(255);
                holder.mine3.setImageAlpha(255);
            }
            if(savedGameParameters.get(position).get(1).equals(savedGamesActivity.getResources().getString(R.string.game_mode_user_defined))){
                holder.mine1.setImageAlpha(0);
                holder.mine2.setImageAlpha(0);
                holder.mine3.setImageAlpha(0);
            }

            holder.savedGameProgress.setProgress((int) (Math.round(Double.parseDouble(savedGameParameters.get(position).get(4)) * 100)));
            holder.information = savedGameParameters.get(position);

            int gameId = Integer.valueOf(holder.information.get(0));
            holder.deleteButton.setOnClickListener(new SaveDeleteOnClickListener(gameId, position));
        }
    }

    //get number of saved games
    @Override
    public int getItemCount(){
        return savedGameParameters.size();
    }

    //get list element for a single saved game
    public SavedGamesRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        ViewGroup savedGame = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_game_list_element, parent, false);
        return new ViewHolder(savedGame, savedGamesActivity);
    }

    // delete saved game with speficied id and remove it from the adapter on a click event
    private class SaveDeleteOnClickListener implements View.OnClickListener {
        private final int gameId;
        private final int adapterPosition;

        public SaveDeleteOnClickListener(int gameId, int adapterPosition){
            this.gameId = gameId;
            this.adapterPosition = adapterPosition;
        }

        @Override
        public void onClick(View view) {
            DatabaseSavedGameProvide provide = new DatabaseSavedGameProvide(new PFMSQLiteHelper(view.getContext()));
            provide.execute(gameId);
            savedGameParameters.remove(adapterPosition);
            SavedGamesRecyclerViewAdapter.this.notifyItemRemoved(adapterPosition);

            Toast saveDeletedInfo = Toast.makeText(view.getContext(), view.getResources().getString(R.string.saveDeleted), Toast.LENGTH_SHORT);
            saveDeletedInfo.show();
        }
    }

    //inner class for a list element representing a single saved game
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public int id;
        private TextView savedGameMode;
        private TextView savedGameDate;
        private TextView savedGamePlayingTime;
        private ImageView mine1;
        private ImageView mine2;
        private ImageView mine3;
        private ProgressBar savedGameProgress;
        private ImageButton deleteButton;
        private ArrayList<String> information;
        private SavedGamesActivity activity;

        //get text views to display information about saved games
        public ViewHolder(ViewGroup saved_game_list_element, SavedGamesActivity activity){
            super(saved_game_list_element);
            saved_game_list_element.setOnClickListener(this);

            this.savedGameMode = (TextView) saved_game_list_element.findViewById(R.id.saved_game_mode);
            this.savedGamePlayingTime = (TextView) saved_game_list_element.findViewById(R.id.saved_game_time);
            this.savedGameDate = (TextView) saved_game_list_element.findViewById(R.id.saved_game_date);
            this.savedGameProgress = (ProgressBar) saved_game_list_element.findViewById(R.id.saved_game_progress_bar);
            this.mine1 = (ImageView) saved_game_list_element.findViewById(R.id.saved_game_mine1);
            this.mine2 = (ImageView) saved_game_list_element.findViewById(R.id.saved_game_mine2);
            this.mine3 = (ImageView) saved_game_list_element.findViewById(R.id.saved_game_mine3);
            this.deleteButton = (ImageButton)  saved_game_list_element.findViewById(R.id.saved_game_delete_button);

            this.activity = activity;
        }

        public void onClick(View view){
            Bundle param = new Bundle();
            param.putBoolean("continue", true);

            //language-specific game mode has to be translated into english (database standard)
            if(information.get(1).equals(view.getContext().getResources().getString(R.string.game_mode_easy))){
                information.set(1, "easy");
            }
            if(information.get(1).equals(view.getContext().getResources().getString(R.string.game_mode_medium))){
                information.set(1, "medium");
            }
            if(information.get(1).equals(view.getContext().getResources().getString(R.string.game_mode_difficult))) {
                information.set(1, "difficult");
            }
            if(information.get(1).equals(view.getContext().getResources().getString(R.string.game_mode_user_defined))){
                information.set(1, "user-defined");
            }

            param.putStringArrayList("information", information);

            Intent intent_with_param = new Intent(view.getContext(), PlayActivity.class);
            intent_with_param.putExtras(param);
            view.getContext().startActivity(intent_with_param);

            activity.finish();

        }
    }

}
