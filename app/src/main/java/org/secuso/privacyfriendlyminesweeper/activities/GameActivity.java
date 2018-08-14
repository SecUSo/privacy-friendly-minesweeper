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
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import org.secuso.privacyfriendlyminesweeper.R;
import org.secuso.privacyfriendlyminesweeper.activities.helper.BaseActivity;
import org.secuso.privacyfriendlyminesweeper.database.DatabaseSavedGamesCheck;
import org.secuso.privacyfriendlyminesweeper.database.PFMSQLiteHelper;

import java.util.concurrent.ExecutionException;

/**
 * @author Christopher Beckmann, I3ananas
 * @version 20180606
 * This class implements the functions that are available in the main menu / on the start screen
 */

public class GameActivity extends BaseActivity implements View.OnClickListener, DatabaseSavedGamesCheck.DatabaseSavedGamesCheckReceiver {

    private ViewPager mViewPager;
    private ImageView mArrowLeft;
    private ImageView mArrowRight;
    private int index;
    private Button continueButton;
    private float screen_width_dp;
    private float screen_height_dp;
    private float min_dp_per_field;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Button button_start_game = (Button) findViewById(R.id.game_button_start);
        button_start_game.setOnClickListener(this);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screen_width_dp = dm.widthPixels/getResources().getDisplayMetrics().density;
        screen_height_dp = dm.heightPixels/getResources().getDisplayMetrics().density;
        min_dp_per_field = 30;

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

                index = position;

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

        if(view == null){
            return;
        }

        switch(view.getId()) {
            case R.id.arrow_left:
                mViewPager.arrowScroll(View.FOCUS_LEFT);
                //index--;      //satisfied by onPageSelected-method
                break;
            case R.id.arrow_right:
                mViewPager.arrowScroll(View.FOCUS_RIGHT);
                //index++;      //satisfied by onPageSelected-method
                break;
            case R.id.game_button_start:
                switch (index) {
                    case 0:
                        //values[0] = width, values[1] = height, values[2] = number of mines
                        //preset values for an easy game are 6,10,7
                        if(!checkIfScreenLargeEnough(6, 10)){
                            showDialogIfScreenTooSmall(6,10,7);
                        }
                        else{
                            startGame(6,10,7);
                        }
                        break;
                    case 1:
                        //values[0] = width, values[1] = height, values[2] = number of mines
                        //preset values for an medium game are 10,16,24
                        if(!checkIfScreenLargeEnough(10, 16)){
                            showDialogIfScreenTooSmall(10, 16,24);
                        }
                        else {
                            startGame(10,16,24);
                        }
                        break;
                    case 2:
                        //values[0] = width, values[1] = height, values[2] = number of mines
                        //preset values for an hard game are 12,19,46
                        if(!checkIfScreenLargeEnough(12, 19)){
                            showDialogIfScreenTooSmall(12, 19, 46);
                        }
                        else{
                            startGame(12,19,46);
                        }
                        break;
                    default:
                }
                break;
            case R.id.game_button_continue:
                Intent intent = new Intent(this, SavedGamesActivity.class);
                startActivity(intent);
                break;
            case R.id.game_button_user_defined:
                showDialogForUserDefinedGameMode();
                break;
            default:
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        DatabaseSavedGamesCheck check = new DatabaseSavedGamesCheck(new PFMSQLiteHelper(getApplicationContext()), this);
        check.execute();
    }

    @Override
    public void updateContinueButton(boolean savedGamesExist) {
        continueButton = (Button) findViewById(R.id.game_button_continue);
        if(savedGamesExist){
            continueButton.setEnabled(true);
            continueButton.setBackground(getResources().getDrawable(R.drawable.button_fullwidth));
        }
        else{
            continueButton.setEnabled(false);
            continueButton.setBackground(getResources().getDrawable(R.drawable.button_disabled));
        }
    }

    /**
     * This method checks if the screen of the device is large enough to play the selected game mode appropriate
     * @param nrOfColumns number of columns of the playing field
     * @param nrOfRows number of rows of the playing field
     * @return true, if screen is large enough for selected game mode, false otherwise
     */
    private boolean checkIfScreenLargeEnough(int nrOfColumns, int nrOfRows){
        boolean screenLargeEnough = false;
        if(((screen_width_dp / nrOfColumns) >= min_dp_per_field) && ((screen_height_dp / nrOfRows) >= min_dp_per_field)){
            screenLargeEnough = true;
        }
        return screenLargeEnough;
    }

    /**
     * This method shows a dialog if the screen is too small to play the selected game mode appropriate
     * If the user chooses to continue, the game is started
     * @param columns Number of columns of the playing field
     * @param rows Number of rows of the playing field
     * @param nrOfBombs Number of bombs on the playing field
     */
    private void showDialogIfScreenTooSmall(final int columns, final int rows, final int nrOfBombs){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.screenTooSmall_title);
        builder.setMessage(R.string.screenTooSmall);
        builder.setPositiveButton(R.string.startGame, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                startGame(columns, rows, nrOfBombs);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //do nothing
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * This method shows a dialog, where the user can set up a user-defined game
     */
    private void showDialogForUserDefinedGameMode(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = this.getLayoutInflater().inflate(R.layout.dialog_user_defined_game_mode, null);
        builder.setView(view);

        builder.setPositiveButton(R.string.startGame, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                int nrOfColumns = 0;
                int nrOfRows = 0;
                int nrOfBombs = 0;

                EditText nrOfColumns_editText = (EditText) view.findViewById(R.id.editTextNrOfColumns);
                EditText nrOfRows_editText = (EditText) view.findViewById(R.id.editTextNrOfRows);
                SeekBar seekbar = (SeekBar) view.findViewById(R.id.degreeOfDifficulty);

                if(!nrOfColumns_editText.getText().toString().equals("")){
                    nrOfColumns = Integer.valueOf(nrOfColumns_editText.getText().toString());
                }
                else{
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.userDefinedInvalid_column), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!nrOfRows_editText.getText().toString().equals("")){
                    nrOfRows = Integer.valueOf(nrOfRows_editText.getText().toString());
                }
                else{
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.userDefinedInvalid_row), Toast.LENGTH_SHORT).show();
                    return;
                }

                int nrOfCells = nrOfColumns * nrOfRows;

                if(seekbar.getProgress() == 0){
                    //game mode easy requires at least 9 cells, otherwise there would be no mine
                    if(nrOfCells < 9){
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.userDefinedInvalid_easy), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {
                        nrOfBombs = (int) Math.round((double) nrOfCells * 0.12);
                    }
                }
                if(seekbar.getProgress() == 1){
                    //game mode medium requires at least 7 cells, otherwise there would be no mine
                    if(nrOfCells < 7){
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.userDefinedInvalid_medium), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {
                        nrOfBombs = (int) Math.round((double) nrOfCells * 0.15);
                    }
                }
                if(seekbar.getProgress() == 2){
                    //game mode difficult requires at least 5 cells, otherwise there would be no mine
                    if(nrOfCells < 5){
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.userDefinedInvalid_difficult), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {
                        nrOfBombs = (int) Math.round((double) nrOfCells * 0.20);
                    }
                }

                if(!checkIfScreenLargeEnough(nrOfColumns, nrOfRows)){
                    showDialogIfScreenTooSmall(nrOfColumns, nrOfRows, nrOfBombs);
                }
                else{
                    startGame(nrOfColumns, nrOfRows, nrOfBombs);
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //do nothing
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * This method starts a game with the passed parameters
     * @param columns Number of columns of the playing field
     * @param rows Number of rows of the playing field
     * @param nrOfBombs Number of bombs on the playing field
     */
    private void startGame(int columns, int rows, int nrOfBombs){
        //param will consist of the information needed to build the playing field with the desired width, height and number of mines
        Bundle param = new Bundle();
        Intent intent_with_param;
        param.putShortArray("info", new short[]{(short)columns, (short)rows, (short)nrOfBombs});
        intent_with_param = new Intent(this, PlayActivity.class);
        intent_with_param.putExtras(param);
        startActivity(intent_with_param);
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
            ImageView image_mine1 = (ImageView) rootView.findViewById(R.id.mine1);
            ImageView image_mine2 = (ImageView) rootView.findViewById(R.id.mine2);
            ImageView image_mine3 = (ImageView) rootView.findViewById(R.id.mine3);
            switch (id){
                case 0:
                    textView.setText(R.string.game_mode_easy);
                    image_mine1.setImageAlpha(255);
                    image_mine2.setImageAlpha(100);
                    image_mine3.setImageAlpha(100);
                    break;
                case 1:
                    textView.setText(R.string.game_mode_medium);
                    image_mine1.setImageAlpha(255);
                    image_mine2.setImageAlpha(255);
                    image_mine3.setImageAlpha(100);
                    break;
                case 2:
                    textView.setText(R.string.game_mode_difficult);
                    image_mine1.setImageAlpha(255);
                    image_mine2.setImageAlpha(255);
                    image_mine3.setImageAlpha(255);
                    break;
            }
            return rootView;
        }
    }
}
