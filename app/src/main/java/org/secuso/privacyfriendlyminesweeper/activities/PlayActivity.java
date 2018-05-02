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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

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

    protected void onCreate(Bundle param){
        super.onCreate(param);
        setContentView(R.layout.activity_play);


        //TODO: fix continuing a game (for now if/else clause)
        Bundle b = this.getIntent().getExtras();
        if (b==null){

        }else {
            short[] test = b.getShortArray("info");
            int typ = test[0];
            switch (typ) {
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

        //TODO: create playing field of buttons from parameters (so far only testing from this point onwards)
        //TODO: button for changing between revealing and marking field
        //TODO: fix positions when screen is being flipped, make fields change size depending on amount in column/row
        // data to populate the RecyclerView with
        String[] data = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48"};

        // set up the RecyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvNumbers);
        int numberOfColumns = 6;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        adapter = new PlayRecyclerViewAdapter(this, data);
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
}
