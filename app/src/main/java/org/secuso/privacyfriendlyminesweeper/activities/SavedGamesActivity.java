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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import org.secuso.privacyfriendlyminesweeper.R;
import org.secuso.privacyfriendlyminesweeper.activities.adapter.SavedGamesRecyclerViewAdapter;

/**
 * @author I3ananas
 * @version 20180606
 * This class implements functions to handle a list of saved games that can be continued
 */
public class SavedGamesActivity extends AppCompatActivity {

    RecyclerView listRecyclerView;
    RecyclerView.Adapter listAdapter;
    LinearLayoutManager listLayoutManager;
    DividerItemDecoration listDivider;

    //test data set
    String[][] testParameters = {{"easy", "57/100", "2.8.2018", "00:01:03"}, {"medium", "45/100", "1.8.2018", "00:02:15"}, {"easy", "80/100", "2.8.2018", "00:00:25"}, {"easy", "75/100", "2.8.2018", "00:00:46"}};

    protected void onCreate(Bundle param) {
        super.onCreate(param);
        setContentView(R.layout.activity_saved_games);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (getSupportActionBar() == null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        listRecyclerView = (RecyclerView) findViewById(R.id.savedGamesList);
        listRecyclerView.setHasFixedSize(true);

        listAdapter = new SavedGamesRecyclerViewAdapter(testParameters);
        listRecyclerView.setAdapter(listAdapter);

        listLayoutManager = new LinearLayoutManager(this);
        listRecyclerView.setLayoutManager(listLayoutManager);

        listDivider = new DividerItemDecoration(listRecyclerView.getContext(), listLayoutManager.getOrientation());
        listRecyclerView.addItemDecoration(listDivider);
    }

}
