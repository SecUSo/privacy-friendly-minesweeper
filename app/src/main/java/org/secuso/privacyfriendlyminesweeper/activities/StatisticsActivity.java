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

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.secuso.privacyfriendlyminesweeper.R;
import org.secuso.privacyfriendlyminesweeper.activities.adapter.TopTimesRecyclerViewAdapter;
import org.secuso.privacyfriendlyminesweeper.activities.helper.BaseActivity;
import org.secuso.privacyfriendlyminesweeper.database.DatabaseReader;
import org.secuso.privacyfriendlyminesweeper.database.DatabaseReader.DatabaseReaderReceiver;
import org.secuso.privacyfriendlyminesweeper.database.DatabaseReset;
import org.secuso.privacyfriendlyminesweeper.database.PFMSQLiteHelper;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

/**
 * @author I3ananas
 * @version 20180809
 * This class implements an activity with three tabs to show statistics about the three different game modes
 */
public class StatisticsActivity extends BaseActivity implements DatabaseReaderReceiver, DatabaseReset.DatabaseResetReceiver {

    FragmentManager fragmentManager;
    StatisticsPagerAdapter statisticsPagerAdapter;
    ViewPager viewPager;

    static int[] nrOfPlayedGames = new int[3];
    static int[] nrOfUncoveredFields = new int[3];
    static int[] winrate = new int[3];
    static int[] averagePlayingTime = new int[3];
    static ArrayList<ArrayList<ArrayList<String>>> topTimes;
    static ArrayList<ArrayList<String>> topTimesEasy;
    static ArrayList<ArrayList<String>> topTimesMedium;
    static ArrayList<ArrayList<String>> topTimesDifficult;

    @Override
    protected void onCreate(Bundle param) {
        super.onCreate(param);
        setContentView(R.layout.activity_statistics);

        fragmentManager = getSupportFragmentManager();
        statisticsPagerAdapter = new StatisticsPagerAdapter(fragmentManager);
        viewPager = (ViewPager) findViewById(R.id.statistics_pager);
        viewPager.setAdapter(statisticsPagerAdapter);

        TabLayout tabLayout_statistics = (TabLayout) findViewById(R.id.tabLayout_statistics);
        tabLayout_statistics.setupWithViewPager((ViewPager) findViewById(R.id.statistics_pager));

        topTimes = new ArrayList<ArrayList<ArrayList<String>>>();
        topTimesEasy = new ArrayList<ArrayList<String>>();
        topTimesMedium = new ArrayList<ArrayList<String>>();
        topTimesDifficult = new ArrayList<ArrayList<String>>();
        topTimes.add(topTimesEasy);
        topTimes.add(topTimesMedium);
        topTimes.add(topTimesDifficult);

        DatabaseReader reader = new DatabaseReader(new PFMSQLiteHelper(getApplicationContext()), this);
        reader.execute(String.valueOf(getApplicationContext().getDatabasePath("PF_MINESWEEPER_DB")));
    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_statistics;
    }

    /**
     * Extracts all statistics data from a JSONObject
     * @param data JSONObject that contains all data sets from the database
     */
    public void setStatistics(JSONObject data){

        int index = 0;

        try{
            //get the tables
            JSONArray general_statistics = data.getJSONObject("PF_MINESWEEPER_DB").getJSONArray("GENERAL_STATISTICS");
            JSONArray top_times = data.getJSONObject("PF_MINESWEEPER_DB").getJSONArray("TOP_TIMES");

            //read general statistics
            if(general_statistics.length() > 0) {
                for (int i = 0; i < general_statistics.length(); i++) {
                    if (general_statistics.getJSONObject(i).getString("game_mode").equals("easy")) {
                        index = 0;
                    }
                    if (general_statistics.getJSONObject(i).getString("game_mode").equals("medium")) {
                        index = 1;
                    }
                    if (general_statistics.getJSONObject(i).getString("game_mode").equals("difficult")) {
                        index = 2;
                    }

                    nrOfPlayedGames[index] = general_statistics.getJSONObject(i).getInt("nr_of_played_games");
                    nrOfUncoveredFields[index] = general_statistics.getJSONObject(i).getInt("nr_of_uncovered_fields");
                    if (nrOfPlayedGames[index] != 0) {
                        winrate[index] = (general_statistics.getJSONObject(i).getInt("nr_of_won_games") * 100) / nrOfPlayedGames[index];
                    } else {
                        winrate[index] = 0;
                    }
                    if (general_statistics.getJSONObject(i).getInt("nr_of_won_games") != 0) {
                        averagePlayingTime[index] = general_statistics.getJSONObject(i).getInt("wins_playing_time") / general_statistics.getJSONObject(i).getInt("nr_of_won_games");
                    } else {
                        averagePlayingTime[index] = 0;
                    }
                }
            }
            else{
                //reset general statistics if there are no data sets
                for(int i = 0; i < 3; i++){
                    nrOfPlayedGames[i] = 0;
                    nrOfUncoveredFields[i] = 0;
                    winrate[i] = 0;
                    averagePlayingTime[i] = 0;
                }
            }

            topTimesEasy = new ArrayList<ArrayList<String>>();
            topTimesMedium = new ArrayList<ArrayList<String>>();
            topTimesDifficult = new ArrayList<ArrayList<String>>();
            for(int i = 0; i < top_times.length(); i++){

                ArrayList<String> topTime = new ArrayList<String>();
                topTime.add(formatPlayingTime(top_times.getJSONObject(i).getInt("playing_time")));
                topTime.add(top_times.getJSONObject(i).getString("date"));

                if(top_times.getJSONObject(i).getString("game_mode").equals("easy")){
                    topTimesEasy.add(topTime);
                }
                if(top_times.getJSONObject(i).getString("game_mode").equals("medium")){
                    topTimesMedium.add(topTime);
                }
                if(top_times.getJSONObject(i).getString("game_mode").equals("difficult")){
                    topTimesDifficult.add(topTime);
                }
            }
            topTimes.clear();
            topTimes.add(topTimesEasy);
            topTimes.add(topTimesMedium);
            topTimes.add(topTimesDifficult);
        }
        catch(JSONException e){
            Log.d("JSONException", e.getMessage() + "  \n" + e.getCause());
        }

        List<Fragment> fragments = fragmentManager.getFragments();
        StatisticsFragment fragment;
        for(int i = 0; i < fragments.size(); i++){
            fragment = (StatisticsFragment) fragments.get(i);
            RecyclerView.Adapter adapterTopTimes = new TopTimesRecyclerViewAdapter(topTimes.get(i));
            fragment.recyclerViewTopTimes.swapAdapter(adapterTopTimes, true);
            displayStatistics(fragment.getView(), fragment.getArguments().getInt("statistics_number"));
            fragment.adapterTopTimes.notifyDataSetChanged();
        }
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

    /**
     * Fills the view of a fragment with statistics data
     * @param fragmentView The view to display statistics
     * @param id ID of the fragment
     */
    private static void displayStatistics(View fragmentView, int id){

        TextView textView_nrOfPlayedGames = (TextView) fragmentView.findViewById(R.id.value_numberOfPlayedGames);
        textView_nrOfPlayedGames.setText(String.valueOf(nrOfPlayedGames[id]));
        TextView textView_winrate = (TextView) fragmentView.findViewById(R.id.value_winrate);
        textView_winrate.setText(String.valueOf(winrate[id]) + " %");
        TextView textView_nrOfUncoveredFields = (TextView) fragmentView.findViewById(R.id.value_uncoveredFields);
        textView_nrOfUncoveredFields.setText(String.valueOf(nrOfUncoveredFields[id]));
        TextView textView_averageTime = (TextView) fragmentView.findViewById(R.id.value_averageTime);
        textView_averageTime.setText(String.valueOf(formatPlayingTime(averagePlayingTime[id])));

    }

    /**
     * Read and display statistics from database after reset operation
     */
    public void resetStatistics(){
        DatabaseReader reader = new DatabaseReader(new PFMSQLiteHelper(getApplicationContext()), this);
        reader.execute(String.valueOf(getApplicationContext().getDatabasePath("PF_MINESWEEPER_DB")));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.statistics_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.resetAllStatistics:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.resetAllStatisticsDialogText);
                builder.setPositiveButton(R.string.resetAllStatisticsDialogYes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        DatabaseReset reset = new DatabaseReset(new PFMSQLiteHelper(getApplicationContext()), StatisticsActivity.this);
                        reset.execute();
                    }
                });
                builder.setNegativeButton(R.string.resetAllStatisticsDialogNo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //do nothing
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Adapter for fragments that show statistics about played games
     */
    public class StatisticsPagerAdapter extends FragmentPagerAdapter {

        public StatisticsPagerAdapter (FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position){
            return StatisticsFragment.newInstance(position);
        }

        @Override
        public int getCount(){
            //There will be 3 statistics pages (one for each game mode)
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position){
            CharSequence title = null;
            switch(position){
                case 0:
                    title = getText(R.string.game_mode_easy);
                    break;
                case 1:
                    title = getText(R.string.game_mode_medium);
                    break;
                case 2:
                    title = getText(R.string.game_mode_difficult);
                    break;
            }
            return title;
        }
    }

    /**
     * Fragment that shows statistics about played games
     */
    public static class StatisticsFragment extends Fragment {

        private static final String ARG_STATISTICS_NUMBER = "statistics_number";

        View fragmentView;
        RecyclerView recyclerViewTopTimes;
        RecyclerView.Adapter adapterTopTimes;
        LinearLayoutManager layoutManagerTopTimes;
        DividerItemDecoration listDividerTopTimes;

        public static StatisticsFragment newInstance(int statisticsNumber){
            StatisticsFragment statistic_fragment = new StatisticsFragment();
            Bundle arguments = new Bundle();
            arguments.putInt(ARG_STATISTICS_NUMBER, statisticsNumber);
            statistic_fragment.setArguments(arguments);
            return statistic_fragment;
        }

        public StatisticsFragment(){

        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            int id = 0;
            if(getArguments() != null){
                id = getArguments().getInt(ARG_STATISTICS_NUMBER);
            }

            fragmentView = inflater.inflate(R.layout.fragment_statistics, container, false);
            displayStatistics(fragmentView, id);

            recyclerViewTopTimes = (RecyclerView) fragmentView.findViewById(R.id.topTimesList);
            recyclerViewTopTimes.setHasFixedSize(true);

            adapterTopTimes = new TopTimesRecyclerViewAdapter(topTimes.get(id));
            recyclerViewTopTimes.setAdapter(adapterTopTimes);

            layoutManagerTopTimes = new LinearLayoutManager(getContext());
            recyclerViewTopTimes.setLayoutManager(layoutManagerTopTimes);

            listDividerTopTimes = new DividerItemDecoration(recyclerViewTopTimes.getContext(), layoutManagerTopTimes.getOrientation());
            recyclerViewTopTimes.addItemDecoration(listDividerTopTimes);

            return fragmentView;
        }
    }
}
