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

package org.secuso.privacyfriendlyminesweeper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.secuso.privacyfriendlyminesweeper.R;
import org.secuso.privacyfriendlyminesweeper.activities.adapter.SavedGamesRecyclerViewAdapter;
import org.secuso.privacyfriendlyminesweeper.activities.adapter.TopTimesRecyclerViewAdapter;
import org.secuso.privacyfriendlyminesweeper.database.DatabaseSavedGamesReader;
import org.secuso.privacyfriendlyminesweeper.database.PFMSQLiteHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author I3ananas, max-dreger
 * @version 20180606
 * This class implements functions to handle a list of saved games that can be continued
 */
public class SavedGamesActivity extends AppCompatActivity implements DatabaseSavedGamesReader.DatabaseSavedGamesReaderReceiver {

    RecyclerView listRecyclerView;
    RecyclerView.Adapter listAdapter;
    LinearLayoutManager listLayoutManager;
    DividerItemDecoration listDivider;

    ArrayList<ArrayList<String>> savedGamesData = new ArrayList<ArrayList<String>>();
    ArrayList<String> savedGame = new ArrayList<String>();

    protected void onCreate(Bundle param) {
        super.onCreate(param);
        setContentView(R.layout.activity_saved_games);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (getSupportActionBar() == null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        savedGamesData = new ArrayList<ArrayList<String>>();
        savedGame = new ArrayList<String>();
        savedGamesData.add(savedGame);

        listRecyclerView = (RecyclerView) findViewById(R.id.savedGamesList);
        listRecyclerView.setHasFixedSize(true);

        listAdapter = new SavedGamesRecyclerViewAdapter(savedGamesData, this);
        listRecyclerView.setAdapter(listAdapter);

        listLayoutManager = new LinearLayoutManager(this);
        listRecyclerView.setLayoutManager(listLayoutManager);

        listDivider = new DividerItemDecoration(listRecyclerView.getContext(), listLayoutManager.getOrientation());
        listRecyclerView.addItemDecoration(listDivider);

        DatabaseSavedGamesReader reader = new DatabaseSavedGamesReader(new PFMSQLiteHelper(getApplicationContext()), this);
        reader.execute(String.valueOf(getApplicationContext().getDatabasePath("PF_MINESWEEPER_DB")));
    }

    /**
     * This method sets the saved games to display
     * @param data JSONObject that contains all saved game data sets from the database
     */
    public void displaySavedGames(JSONObject data){

        int index = 0;
        String game_mode;
        savedGamesData.clear();

        try{
            //get the tables
            JSONArray savedGames = data.getJSONObject("PF_MINESWEEPER_DB").getJSONArray("SAVED_GAMES");

            //read saved games
            for(int i = 0; i < savedGames.length(); i++) {
                if (savedGames.getJSONObject(i).getString("game_mode").equals("easy")) { index = 0; }
                if (savedGames.getJSONObject(i).getString("game_mode").equals("medium")) { index = 1; }
                if (savedGames.getJSONObject(i).getString("game_mode").equals("hard")) { index = 2; }

                savedGame = new ArrayList<String>();
                savedGame.add(String.valueOf(savedGames.getJSONObject(i).getInt("id")));
                game_mode = savedGames.getJSONObject(i).getString("game_mode");
                if(game_mode.equals("easy")){ savedGame.add(getResources().getString(R.string.game_mode_easy)); }
                if(game_mode.equals("medium")){ savedGame.add(getResources().getString(R.string.game_mode_medium)); }
                if(game_mode.equals("difficult")){ savedGame.add(getResources().getString(R.string.game_mode_difficult)); }
                savedGame.add(formatPlayingTime(Integer.valueOf(savedGames.getJSONObject(i).getString("playing_time"))));
                savedGame.add(savedGames.getJSONObject(i).getString("date"));
                savedGame.add(savedGames.getJSONObject(i).getString("progress"));
                savedGame.add(savedGames.getJSONObject(i).getString("saved_game_content"));
                savedGame.add(savedGames.getJSONObject(i).getString("saved_game_status"));
                savedGamesData.add(savedGame);
            }
        }
        catch(JSONException e){
            Log.d("JSONException", e.getMessage() + "  \n" + e.getCause());
        }

        RecyclerView.Adapter adapterSavedGames = new SavedGamesRecyclerViewAdapter(savedGamesData, this);
        listRecyclerView.swapAdapter(adapterSavedGames, true);
        listAdapter.notifyDataSetChanged();
    }

    /**
     * Formats the given playing time (in seconds) as minutes and seconds
     * @param time The playing time to format
     * @return Formatted playing time as a String
     */
    private static String formatPlayingTime(int time){
        int time_minutes;
        int time_seconds;
        String time_string;

        time_minutes = time/60;
        time_seconds = time%60;
        time_string = (String.valueOf(time_minutes) + ":");

        if(time_seconds < 10){
            time_string = time_string + "0" + time_seconds;
        }
        else{
            time_string = time_string + time_seconds;
        }

        return time_string;
    }


}
