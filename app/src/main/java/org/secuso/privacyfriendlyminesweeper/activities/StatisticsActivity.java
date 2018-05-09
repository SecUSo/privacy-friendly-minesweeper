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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.secuso.privacyfriendlyminesweeper.R;
import org.secuso.privacyfriendlyminesweeper.activities.helper.BaseActivity;

/**
 * @author I3ananas
 * @version 20180504
 * This class implements functions to show statistics about played games
 */
public class StatisticsActivity extends BaseActivity {

    StatisticsPagerAdapter mStatisticsPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle param) {
        super.onCreate(param);
        setContentView(R.layout.activity_statistics);

        mStatisticsPagerAdapter = new StatisticsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.statistics_pager);
        mViewPager.setAdapter(mStatisticsPagerAdapter);

        TabLayout tabLayout_statistics = (TabLayout) findViewById(R.id.tabLayout_statistics);
        tabLayout_statistics.setupWithViewPager((ViewPager) findViewById(R.id.statistics_pager));
    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_statistics;
    }

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

            return fragmentView;
        }
    }
}
