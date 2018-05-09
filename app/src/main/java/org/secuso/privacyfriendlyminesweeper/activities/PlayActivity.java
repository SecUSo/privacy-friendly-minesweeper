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

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridView;
import android.widget.ImageView;

import org.secuso.privacyfriendlyminesweeper.R;
import org.secuso.privacyfriendlyminesweeper.activities.adapter.PlayRecyclerViewAdapter;
import org.secuso.privacyfriendlyminesweeper.activities.helper.BaseActivity;

/**
 * @author I3ananas
 * @version 20180430
 * This class implements functions required to handle the process of playing
 */
public class PlayActivity extends BaseActivity implements PlayRecyclerViewAdapter.ItemClickListener{
    short[][] playingField;
    PlayRecyclerViewAdapter adapter;
    int numberOfRows;
    RecyclerView recyclerView;
    int maxHeight;
    boolean firstTime;
    String[] empty;

    protected void onCreate(Bundle param){
        super.onCreate(param);
        setContentView(R.layout.activity_play);

        int columns = 0;
        int rows = 0;
        int numberOfBombs = 0;

        //TODO: fix continuing a game (for now if/else clause)
        Bundle b = this.getIntent().getExtras();
        if (b==null){

        }else {
            short[] test = b.getShortArray("info");
            columns = test[0];
            rows = test[1];
            numberOfBombs = test[2];
            switch (columns) {
                case 6:
                    System.out.println("easy");
                    break;
                case 10:
                    System.out.println("medium");
                    break;
                case 12:
                    System.out.println("hard");
                    break;
                default:
            }
        }
        numberOfRows = rows;

        //TODO: create playing field of buttons from parameters (so far only testing from this point onwards)
        //TODO: fix positions when screen is being flipped
        // data to populate the RecyclerView with
        String[] data = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48"};

        int numberOfCells = rows * columns;
        empty = new String[numberOfCells];

        // set up the RecyclerView
        final View heightTest = findViewById(R.id.height_test);
        recyclerView = (RecyclerView) findViewById(R.id.playingfield);

        //uses heightTest to measure the height of the usable screen when it is first drawn
        heightTest.setVisibility(View.VISIBLE);
        firstTime = true;
        //after the first drawing we use the measured height to calculate the maximum height of every cell in the grid
        heightTest.post(new Runnable() {
            @Override
            public void run() {
                //subtract the height of the play_sidebar
                int height = recyclerView.getHeight()- Math.round(32*(getResources().getDisplayMetrics().xdpi/ DisplayMetrics.DENSITY_DEFAULT));
                //set height of recyclerView so it does not overlap the play_sidebar
                ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                params.height = height;
                maxHeight = height/numberOfRows;
                //cells have a buffer of 2dp, so substract 2dp*2 transformed into pixel value
                maxHeight = maxHeight - Math.round(4*(getResources().getDisplayMetrics().xdpi/ DisplayMetrics.DENSITY_DEFAULT));
                if (firstTime) {
                    firstTime=false;
                    createAdapter(maxHeight);
                    //after heightTest is made invisible the grid is redrawn, this time with the correct maxheight
                    heightTest.setVisibility(View.GONE);
                }
            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(this, columns, LinearLayoutManager.VERTICAL, true));
        createAdapter(maxHeight);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);


    }
    private void createAdapter(int maximumHeight) {
        adapter = new PlayRecyclerViewAdapter(this, empty, maxHeight);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.i("TAG", "You clicked number " + adapter.getItem(position) + ", which is at cell position " + position);
    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_play;
    }

    public int getNumberOfRows() { return numberOfRows; }
    public int getMaxHeight() { return maxHeight; }
}
