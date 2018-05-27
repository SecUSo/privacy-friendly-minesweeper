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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;

import org.secuso.privacyfriendlyminesweeper.R;
import org.secuso.privacyfriendlyminesweeper.activities.helper.BaseActivity;
import org.secuso.privacyfriendlyminesweeper.database.DatabaseWriter;
import org.secuso.privacyfriendlyminesweeper.database.PFMSQLiteHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Christopher Beckmann, I3ananas
 * @version 20180430
 * This class implements the functions that are available in the main menu / on the start screen
 */

public class GameActivity extends BaseActivity implements View.OnClickListener {

    private ViewPager mViewPager;
    private ImageView mArrowLeft;
    private ImageView mArrowRight;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Button button_start_game = (Button) findViewById(R.id.game_button_start);
        button_start_game.setOnClickListener(this);

        final SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.scroller);
        if(mViewPager != null) {
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }

        index = mSharedPreferences.getInt("firstChosenPage", 0);

        mViewPager.setCurrentItem(index);
        mArrowLeft = (ImageView) findViewById(R.id.arrow_left);
        mArrowRight = (ImageView) findViewById(R.id.arrow_right);

        //set initial postiton of the ViewPager
        mArrowLeft.setVisibility((index==0)?View.INVISIBLE:View.VISIBLE);
        mArrowRight.setVisibility((index==mSectionsPagerAdapter.getCount()-1)?View.INVISIBLE:View.VISIBLE);

        //Update ViewPager on change
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mArrowLeft.setVisibility((position==0)?View.INVISIBLE:View.VISIBLE);
                mArrowRight.setVisibility((position==mSectionsPagerAdapter.getCount()-1)?View.INVISIBLE:View.VISIBLE);

                //save position in settings
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putInt("lastChosenPage", position);
                editor.apply();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_game;
    }

    public void onClick(View view) {

        //param will consist of the information needed to build the playing field with the desired
        //width, height and number of mines
        Bundle param;
        Intent intent_with_param;

        if(view == null){
            return;
        }

        switch(view.getId()) {
            case R.id.arrow_left:
                mViewPager.arrowScroll(View.FOCUS_LEFT);
                index--;
                break;
            case R.id.arrow_right:
                mViewPager.arrowScroll(View.FOCUS_RIGHT);
                index++;
                break;
            case R.id.game_button_start:
                switch (index) {
                    case 0:
                        param = new Bundle();
                        //values[0] = width, values[1] = height, values[2] = number of mines
                        //preset values for an easy game are 6,10,7
                        param.putShortArray("info", new short[]{6, 10, 7});
                        intent_with_param = new Intent(this, PlayActivity.class);
                        intent_with_param.putExtras(param);
                        startActivity(intent_with_param);
                        break;
                    case 1:
                        param = new Bundle();
                        //values[0] = width, values[1] = height, values[2] = number of mines
                        //preset values for an medium game are 10,16,24
                        param.putShortArray("info", new short[]{10, 16, 24});
                        intent_with_param = new Intent(this, PlayActivity.class);
                        intent_with_param.putExtras(param);
                        startActivity(intent_with_param);
                        break;
                    case 2:
                        param = new Bundle();
                        //values[0] = width, values[1] = height, values[2] = number of mines
                        //preset values for an hard game are 12,19,46
                        param.putShortArray("info", new short[]{12, 19, 46});
                        intent_with_param = new Intent(this, PlayActivity.class);
                        intent_with_param.putExtras(param);
                        startActivity(intent_with_param);
                        break;
                    default:
                }

                break;
            case R.id.game_button_continue:
                //TODO: navigate to list of saved games (if there are any)
                break;
            default:
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PageFragment (defined as a static inner class below).
            return PageFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

    public static class PageFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PageFragment newInstance(int sectionNumber) {
            PageFragment fragment = new PageFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PageFragment() {

        }


        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int id = 0;
            if(getArguments() != null) {
                id = getArguments().getInt(ARG_SECTION_NUMBER);
            }

            View rootView = inflater.inflate(R.layout.fragment_game_mode, container, false);

            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            switch (id){
                case 0:
                    textView.setText(R.string.game_mode_easy);
                    break;
                case 1:
                    textView.setText(R.string.game_mode_medium);
                    break;
                case 2:
                    textView.setText(R.string.game_mode_difficult);
                    break;
            }
            return rootView;
        }
    }
}
