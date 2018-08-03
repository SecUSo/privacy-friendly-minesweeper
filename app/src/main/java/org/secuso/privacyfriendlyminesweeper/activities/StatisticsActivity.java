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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.secuso.privacyfriendlyminesweeper.R;
import org.secuso.privacyfriendlyminesweeper.activities.helper.BaseActivity;
import org.secuso.privacyfriendlyminesweeper.database.DatabaseReader;
import org.secuso.privacyfriendlyminesweeper.database.DatabaseReader.DatabaseReaderReceiver;
import org.secuso.privacyfriendlyminesweeper.database.PFMSQLiteHelper;

import java.util.List;

/**
 * @author I3ananas
 * @version 20180530
 * This class implements an activity with three tabs to show statistics about the three different game modes
 */
public class StatisticsActivity extends BaseActivity implements DatabaseReaderReceiver {

    FragmentManager mFragmentManager;
    StatisticsPagerAdapter mStatisticsPagerAdapter;
    ViewPager mViewPager;

    static int[] nrOfPlayedGames = new int[3];
    static int[] nrOfUncoveredFields = new int[3];
    static int[] winrate = new int[3];
    static int[] averagePlayingTime = new int[3];
    static String[][][] topTimes = new String[3][10][2];
    int index_easy = 0;
    int index_medium = 0;
    int index_difficult = 0;

    @Override
    protected void onCreate(Bundle param) {
        super.onCreate(param);
        setContentView(R.layout.activity_statistics);

        mFragmentManager = getSupportFragmentManager();
        mStatisticsPagerAdapter = new StatisticsPagerAdapter(mFragmentManager);
        mViewPager = (ViewPager) findViewById(R.id.statistics_pager);
        mViewPager.setAdapter(mStatisticsPagerAdapter);

        TabLayout tabLayout_statistics = (TabLayout) findViewById(R.id.tabLayout_statistics);
        tabLayout_statistics.setupWithViewPager((ViewPager) findViewById(R.id.statistics_pager));

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
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 10; j++){
                topTimes[i][j][0] = "";
                topTimes[i][j][1] = "";
            }
        }

        try{
            //get the tables
            JSONArray general_statistics = data.getJSONObject("PF_MINESWEEPER_DB").getJSONArray("GENERAL_STATISTICS");
            JSONArray top_times = data.getJSONObject("PF_MINESWEEPER_DB").getJSONArray("TOP_TIMES");

            //read general statistics
            for(int i = 0; i < general_statistics.length(); i++){
                if(general_statistics.getJSONObject(i).getString("game_mode").equals("easy")){ index = 0; }
                if(general_statistics.getJSONObject(i).getString("game_mode").equals("medium")){ index = 1; }
                if(general_statistics.getJSONObject(i).getString("game_mode").equals("hard")){ index = 2; }

                nrOfPlayedGames[index] = general_statistics.getJSONObject(i).getInt("nr_of_played_games");
                nrOfUncoveredFields[index] = general_statistics.getJSONObject(i).getInt("nr_of_uncovered_fields");
                if(nrOfPlayedGames[index] != 0){
                    winrate[index] = (general_statistics.getJSONObject(i).getInt("nr_of_won_games")*100) / nrOfPlayedGames[index];
                }
                else{
                    winrate[index] = 0;
                }
                if(general_statistics.getJSONObject(i).getInt("nr_of_won_games") != 0){
                    averagePlayingTime[index] = general_statistics.getJSONObject(i).getInt("wins_playing_time") / general_statistics.getJSONObject(i).getInt("nr_of_won_games");
                }
                else {
                    averagePlayingTime[index] = 0;
                }
            }

            //read top times
            for(int i = 0; i < top_times.length(); i++){
                if(top_times.getJSONObject(i).getString("game_mode").equals("easy")){
                    topTimes[0][index_easy][0] = formatPlayingTime(top_times.getJSONObject(i).getInt("playing_time"));
                    topTimes[0][index_easy][1] = top_times.getJSONObject(i).getString("date");
                    index_easy++;
                }
                if(top_times.getJSONObject(i).getString("game_mode").equals("medium")){
                    topTimes[1][index_medium][0] = formatPlayingTime(top_times.getJSONObject(i).getInt("playing_time"));
                    topTimes[1][index_medium][1] = top_times.getJSONObject(i).getString("date");
                    index_medium++;
                }
                if(top_times.getJSONObject(i).getString("game_mode").equals("hard")){
                    topTimes[2][index_difficult][0] = formatPlayingTime(top_times.getJSONObject(i).getInt("playing_time"));
                    topTimes[2][index_difficult][1] = top_times.getJSONObject(i).getString("date");
                    index_difficult++;
                }
            }
        }
        catch(JSONException e){
            Log.d("JSONException", e.getMessage() + "  \n" + e.getCause());
        }

        List<Fragment> fragments = mFragmentManager.getFragments();
        StatisticsFragment fragment;
        for(int i = 0; i < fragments.size(); i++){
            fragment = (StatisticsFragment) fragments.get(i);
            displayStatistics(fragment.getView(), fragment.getArguments().getInt("statistics_number"));
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

        TextView textView_time1 = (TextView) fragmentView.findViewById(R.id.topTime1);
        textView_time1.setText(topTimes[id][0][0]);
        TextView textView_date1 = (TextView) fragmentView.findViewById(R.id.topTimeDate1);
        textView_date1.setText(topTimes[id][0][1]);
        TextView textView_time2 = (TextView) fragmentView.findViewById(R.id.topTime2);
        textView_time2.setText(topTimes[id][1][0]);
        TextView textView_date2 = (TextView) fragmentView.findViewById(R.id.topTimeDate2);
        textView_date2.setText(topTimes[id][1][1]);
        TextView textView_time3 = (TextView) fragmentView.findViewById(R.id.topTime3);
        textView_time3.setText(topTimes[id][2][0]);
        TextView textView_date3 = (TextView) fragmentView.findViewById(R.id.topTimeDate3);
        textView_date3.setText(topTimes[id][2][1]);
        TextView textView_time4 = (TextView) fragmentView.findViewById(R.id.topTime4);
        textView_time4.setText(topTimes[id][3][0]);
        TextView textView_date4 = (TextView) fragmentView.findViewById(R.id.topTimeDate4);
        textView_date4.setText(topTimes[id][3][1]);
        TextView textView_time5 = (TextView) fragmentView.findViewById(R.id.topTime5);
        textView_time5.setText(topTimes[id][4][0]);
        TextView textView_date5 = (TextView) fragmentView.findViewById(R.id.topTimeDate5);
        textView_date5.setText(topTimes[id][4][1]);
        TextView textView_time6 = (TextView) fragmentView.findViewById(R.id.topTime6);
        textView_time6.setText(topTimes[id][5][0]);
        TextView textView_date6 = (TextView) fragmentView.findViewById(R.id.topTimeDate6);
        textView_date6.setText(topTimes[id][5][1]);
        TextView textView_time7 = (TextView) fragmentView.findViewById(R.id.topTime7);
        textView_time7.setText(topTimes[id][6][0]);
        TextView textView_date7 = (TextView) fragmentView.findViewById(R.id.topTimeDate7);
        textView_date7.setText(topTimes[id][6][1]);
        TextView textView_time8 = (TextView) fragmentView.findViewById(R.id.topTime8);
        textView_time8.setText(topTimes[id][7][0]);
        TextView textView_date8 = (TextView) fragmentView.findViewById(R.id.topTimeDate8);
        textView_date8.setText(topTimes[id][7][1]);
        TextView textView_time9 = (TextView) fragmentView.findViewById(R.id.topTime9);
        textView_time9.setText(topTimes[id][8][0]);
        TextView textView_date9 = (TextView) fragmentView.findViewById(R.id.topTimeDate9);
        textView_date9.setText(topTimes[id][8][1]);
        TextView textView_time10 = (TextView) fragmentView.findViewById(R.id.topTime10);
        textView_time10.setText(topTimes[id][9][0]);
        TextView textView_date10 = (TextView) fragmentView.findViewById(R.id.topTimeDate10);
        textView_date10.setText(topTimes[id][9][1]);
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

            View fragmentView = inflater.inflate(R.layout.fragment_statistics, container, false);
            displayStatistics(fragmentView, id);

            return fragmentView;
        }
    }
}
