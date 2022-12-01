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
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.secuso.privacyfriendlyminesweeper.R;
import org.secuso.privacyfriendlyminesweeper.activities.adapter.PlayRecyclerViewAdapter;
import org.secuso.privacyfriendlyminesweeper.activities.helper.CellView;
import org.secuso.privacyfriendlyminesweeper.database.DatabaseBestTimeReader;
import org.secuso.privacyfriendlyminesweeper.database.DatabaseBestTimeReader.BestTimeReaderReceiver;
import org.secuso.privacyfriendlyminesweeper.database.DatabaseSavedGameProvide;
import org.secuso.privacyfriendlyminesweeper.database.DatabaseSavedGameWriter;
import org.secuso.privacyfriendlyminesweeper.database.DatabaseWriter;
import org.secuso.privacyfriendlyminesweeper.database.PFMSQLiteHelper;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @author I3ananas, max-dreger
 * @version 20180809
 * This class implements all functions required to handle the process of playing:
 * - creation of the playing field and its content
 * - uncovering fields
 * - check for victory and/or defeat
 * - save statistics on victory and/or defeat
 * - save games
 * - load saved games
 */
public class PlayActivity extends AppCompatActivity implements PlayRecyclerViewAdapter.ItemClickListener, BestTimeReaderReceiver {
    PlayRecyclerViewAdapter adapter;
    SharedPreferences sharedPreferences;
    String game_mode;
    int numberOfRows;
    int numberOfColumns;
    int numberOfBombs;
    int numberOfCells;
    RecyclerView recyclerView;
    int maxHeight;
    boolean firstTime;
    boolean marking;
    int[][] data;
    int[][] status;
    TextView mines;
    int bombsLeft;
    int countDownToWin;
    boolean firstClick;
    Bundle parameter;
    Chronometer timer;
    DatabaseBestTimeReader bestTimeReader;
    DatabaseWriter writer;
    int bestTime;
    boolean newBestTime;
    Boolean revealingAround;
    boolean gameEnded;
    boolean savecheck;
    String savedContent;
    String savedStatus;
    int totalSavedSeconds;
    Toolbar toolbar;
    Handler handler;
    int[][] landscape_data;
    int[][] landscape_status;
    int[][] not_in_use_data;
    int[][] not_in_use_status;
    boolean savedinstancestate;
    int desired_width;
    boolean game_saved;
    boolean landscape;

    protected void onCreate(Bundle param){
        super.onCreate(param);
        setContentView(R.layout.activity_play);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        numberOfColumns = 0;
        numberOfRows = 0;
        numberOfBombs = 0;

        landscape = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);

        newBestTime = false;
        gameEnded = false;
        game_saved = false;

        //check if this is loading a saved game
        parameter = this.getIntent().getExtras();
        savecheck = false;
        savecheck = parameter.getBoolean("continue");
        //get the game mode and playingfield size
        if (savecheck){
            ArrayList<String> savedInfo = parameter.getStringArrayList("information");
            int id = Integer.valueOf(savedInfo.get(0));
            String savedGameMode = savedInfo.get(1);
            String time = savedInfo.get(2);
            savedContent = savedInfo.get(5);
            savedStatus = savedInfo.get(6);

            if (savedGameMode.equalsIgnoreCase("easy")) {
                game_mode = "easy";
                parameter.putShortArray("info", new short[]{(short)6, (short)10, (short)7});
                parameter.putBoolean("continue", false);
                numberOfColumns = 6;
                numberOfRows = 10;
                numberOfBombs = 7;
            } else if (savedGameMode.equalsIgnoreCase("medium")) {
                game_mode = "medium";
                parameter.putShortArray("info", new short[]{(short)10, (short)16, (short)24});
                parameter.putBoolean("continue", false);
                numberOfColumns = 10;
                numberOfRows = 16;
                numberOfBombs = 24;
            } else {
                game_mode = "difficult";
                parameter.putShortArray("info", new short[]{(short)12, (short)19, (short)46});
                parameter.putBoolean("continue", false);
                numberOfColumns = 12;
                numberOfRows = 19;
                numberOfBombs = 46;
            }

            //handle the saved time
            String[] units = time.split(":");
            int minutes = Integer.parseInt(units[0]);
            int seconds = Integer.parseInt(units[1]);
            totalSavedSeconds = 60 * minutes + seconds;

            DatabaseSavedGameProvide provider = new DatabaseSavedGameProvide(new PFMSQLiteHelper(getApplicationContext()));
            provider.execute(id);
        }
        //get game mode and PlayingField size if this is not loading a saved game
        else {
            short[] test = parameter.getShortArray("info");
            numberOfColumns = test[0];
            numberOfRows = test[1];
            numberOfBombs = test[2];

            if((numberOfColumns == 6) && (numberOfRows == 10) && (numberOfBombs == 7)){
                game_mode = "easy";
            }
            else if((numberOfColumns == 10) && (numberOfRows == 16) && (numberOfBombs == 24)){
                game_mode = "medium";
            }
            else if((numberOfColumns == 12) && (numberOfRows == 19) && (numberOfBombs == 46)){
                game_mode = "difficult";
            }
            else{
                game_mode = "user-defined";
            }
        }

        //handle the custom toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_play);
        if(getSupportActionBar() == null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            TextView text_game_mode = (TextView) toolbar.findViewById(R.id.game_mode);
            if(game_mode.equals("easy")){
                text_game_mode.setText(getResources().getString(R.string.game_mode_easy));
            }
            if(game_mode.equals("medium")){
                text_game_mode.setText(getResources().getString(R.string.game_mode_medium));
            }
            if(game_mode.equals("difficult")){
                text_game_mode.setText(getResources().getString(R.string.game_mode_difficult));
            }
            if(game_mode.equals("user-defined")){
                text_game_mode.setText(getResources().getString(R.string.game_mode_user_defined_2lines));
            }
        }

        //Creating the right sized the PlayingField
        numberOfCells = numberOfRows * numberOfColumns;
        data = new int[numberOfRows][numberOfColumns];
        countDownToWin = numberOfCells;

        //status saves the state of the cell
        //0 = normal, 1 = revealed, 2 = marked
        status = new int[numberOfRows][numberOfColumns];
        for (int i = 0; i < numberOfRows; i++) {
            for (int j = 0; j < numberOfColumns; ++j) {
                status[i][j] = 0;
            }
        }

        //check if there is a saved instance state
        if (param != null) {
            numberOfRows = param.getInt("rows");
            numberOfColumns = param.getInt("columns");
            data = delinearize_data(param.getIntArray("data"), numberOfRows, numberOfColumns);
            status = delinearize_data(param.getIntArray("status"), numberOfRows, numberOfColumns);
            totalSavedSeconds = param.getInt("time");
            boolean noinfo = param.getBoolean("empty");
            gameEnded = param.getBoolean("gameended");
            if (noinfo) {
                savecheck = false;

            } else {
                savecheck = true;
                savedinstancestate = true;
            }
            if (numberOfRows < numberOfColumns) {
                int save = numberOfColumns;
                numberOfColumns = numberOfRows;
                numberOfRows = save;
            }
        }

        //parce the Content and Status String if this is loading a saved game
        if (savecheck) {
            if (!savedinstancestate) {
                String[] parcedContent = savedContent.split("");
                String[] parcedStatus = savedStatus.split("");

                StringBuilder line = new StringBuilder();
                int[] grid = new int[numberOfCells];
                int[] grid_status = new int[numberOfCells];
                for (int i = 0; i < numberOfCells; i++) {
                    line.append(parcedContent[i + 1]);
                    grid[i] = Integer.parseInt(parcedContent[i + 1]);
                    grid_status[i] = Integer.parseInt(parcedStatus[i + 1]);
                }
                data = delinearize_data(grid, numberOfRows, numberOfColumns);
                status = delinearize_data(grid_status, numberOfRows, numberOfColumns);
            }
            //flip the info if we are in landscape mode
            if(landscape){
                landscape_data = new int[numberOfColumns][numberOfRows];
                for (int row = 0; row < numberOfRows; ++row) {
                    for (int col = 0; col < numberOfColumns; ++col) {
                        landscape_data[numberOfColumns - col - 1][row] = data[row][col];
                    }
                }
                not_in_use_data = data;
                data = landscape_data;

                landscape_status = new int[numberOfColumns][numberOfRows];
                for (int row = 0; row < numberOfRows; ++row) {
                    for (int col = 0; col < numberOfColumns; ++col) {
                        landscape_status[numberOfColumns - col - 1][row] = status[row][col];
                    }
                }
                not_in_use_status = status;
                status = landscape_status;

                int save = numberOfColumns;
                numberOfColumns = numberOfRows;
                numberOfRows = save;
            }
        }

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
                int height;
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                    height = recyclerView.getHeight();
                    desired_width = recyclerView.getWidth() - Math.round(25*(getResources().getDisplayMetrics().xdpi/ DisplayMetrics.DENSITY_DEFAULT));
                } else {
                    height = recyclerView.getHeight()- Math.round(32*(getResources().getDisplayMetrics().xdpi/ DisplayMetrics.DENSITY_DEFAULT));
                    desired_width = recyclerView.getWidth();
                }
                //set height of recyclerView so it does not overlap the play_sidebar
                ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                params.height = height;
                params.width = desired_width;
                maxHeight = height/numberOfRows;
                //cells have a buffer of 2dp, so substract 1dp*2 transformed into pixel value
                maxHeight = maxHeight - Math.round(2*(getResources().getDisplayMetrics().xdpi/ DisplayMetrics.DENSITY_DEFAULT));

                if (firstTime) {
                    firstTime=false;
                    createAdapter(maxHeight);
                    //after heightTest is made invisible the grid is redrawn, this time with the correct maxheight
                    heightTest.setVisibility(View.GONE);
                }

                recyclerView.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(recyclerView.findViewHolderForAdapterPosition(0)!=null )
                        {
                            //loading saved game
                            if (savecheck) {
                                fillSavedGame(savedContent, savedStatus);
                            }
                        }
                    }
                },50);

            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns, LinearLayoutManager.VERTICAL, false));

        createAdapter(maxHeight);

        firstClick = true;

        //handling the Button that toggles between revealing cells and marking them as mines
        marking = false;
        final Button button = (Button) findViewById(R.id.toggle);
        button.setTextColor(getResources().getColor(R.color.white));
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (marking) {
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        Button button2 = (Button) findViewById(R.id.toggle2);
                        button2.callOnClick();
                        button2.setBackground(getDrawable(R.drawable.button_highlighted));
                        button2.setText(getString(R.string.untoggled));
                        button2.setTextColor(getResources().getColor(R.color.white));
                    }
                    button.setBackground(getDrawable(R.drawable.button_highlighted));
                    marking = false;
                    button.setText(getString(R.string.untoggled));
                    button.setTextColor(getResources().getColor(R.color.white));
                } else {
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        Button button2 = (Button) findViewById(R.id.toggle2);
                        button2.setText(getString(R.string.toggled));
                        button2.setTextColor(getResources().getColor(R.color.black));
                        button2.setBackground(getDrawable(R.drawable.button_highlighted_clicked));
                    }
                    view.setBackground(getDrawable(R.drawable.button_highlighted_clicked));
                    marking = true;
                    button.setText(getString(R.string.toggled));
                    button.setTextColor(getResources().getColor(R.color.black));

                }
            }
        });
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            final Button button2 = (Button) findViewById(R.id.toggle2);
            button2.setTextColor(getResources().getColor(R.color.white));
            button2.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if (marking) {
                        button2.setBackground(getDrawable(R.drawable.button_highlighted));
                        button2.setText(getString(R.string.untoggled));
                        button2.setTextColor(getResources().getColor(R.color.white));
                        marking = false;
                        button.setBackground(getDrawable(R.drawable.button_highlighted));
                        button.setText(getString(R.string.untoggled));
                        button.setTextColor(getResources().getColor(R.color.white));
                    } else {
                        view.setBackground(getDrawable(R.drawable.button_highlighted_clicked));
                        button.setBackground(getDrawable(R.drawable.button_highlighted_clicked));
                        marking = true;
                        button.setText(getString(R.string.toggled));
                        button.setTextColor(getResources().getColor(R.color.black));
                        button2.setText(getString(R.string.toggled));
                        button2.setTextColor(getResources().getColor(R.color.black));

                    }
                }
            });
        }

        revealingAround = false;
        bombsLeft = numberOfBombs;
        mines = (TextView) toolbar.findViewById(R.id.mines);
        mines.setText(String.valueOf(bombsLeft));

        ImageView mines_pic = (ImageView) toolbar.findViewById(R.id.mines_pic);
        mines_pic.setImageResource(R.drawable.mine);

        handler = new Handler();

        bestTimeReader = new DatabaseBestTimeReader(new PFMSQLiteHelper(getApplicationContext()), this);
        bestTimeReader.execute(game_mode);
        writer = new DatabaseWriter(new PFMSQLiteHelper(getApplicationContext()));
    }

    private int[][] delinearize_data(int[] data, int numberOfRows, int numberOfColumns) {
        int[][] grid = new int[numberOfRows][numberOfColumns];
        for (int row = 0; row < numberOfRows; ++row) {
            for (int col = 0; col < numberOfColumns; ++col) {
                grid[row][col] = data[linearize_index(row, col)];
            }
        }
        return grid;
    }

    private int[] linearize_data(int[][] data, int numberOfRows, int numberOfColumns) {
        int[] grid = new int[numberOfRows * numberOfColumns];
        for (int row = 0; row < numberOfRows; ++row) {
            for (int col = 0; col < numberOfColumns; ++col) {
                grid[linearize_index(row, col)] = data[row][col];
            }
        }
        return grid;
    }

    private Pair<Integer, Integer> delinearize_index(int index) {
        return new Pair<>(index / numberOfColumns, index % numberOfColumns);
    }

    private int linearize_index(Pair<Integer, Integer> index) {
        return linearize_index(index.first, index.second);
    }

    private int linearize_index(int row, int col) {
        return row * numberOfColumns + col;
    }

    /**
     * This method creates a new PlayRecyclerViewAdapter with the given parameters and connects it to the RecyclerView with the Playing Field
     * @param maximumHeight the maximum height of the singe Cells of the Playing Field
     */
    private void createAdapter(int maximumHeight) {
        adapter = new PlayRecyclerViewAdapter(this, linearize_data(data, numberOfRows, numberOfColumns), maxHeight);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    /**
     * This method fills the playing Field with data. First it puts the needed amount of bombs in random Cells, then Calculates the Number of Neighboring Bomb for each Cell
     * @param click the position of the Cell where the user clicked first. This one can not have a Bomb in it
     */
    private void fillPlayingField(Pair<Integer, Integer> click){

        //put bombs at random positions
        for (int i = 0; i < numberOfBombs; i++) {
            int row, col;
            Random randomGen = new Random();

            // Generate random position until we have a valid bomb position.
            // A position is valid iff:
            //  * it is different from the position the user clicked on
            //  * it is not already occupied by a bomb
            //  * placing a bomb in this position does not create a cluster of bombs of size 4 or greater.
            //
            // A cluster of bombs of size n are n bombs which are horizontally and vertically connected.
            // We want to prevent the following arrangements:
            // 1) XX  2) XX   3) XXXX  4) XXX  5) XXX
            //    XX      XX              X        X
            do {
                row = randomGen.nextInt(numberOfRows);
                col = randomGen.nextInt(numberOfColumns);
            } while (
                    row == click.first && col == click.second
                ||  data[row][col] == 9
                ||  countClusterSize(new Pair<>(row, col)) >= 4
            );
            data[row][col] = 9;
        }

        // We want to look at all neighbours of a given cell
        int[][] neighbourhood = {
            {-1,-1},{0,-1},{1,-1},
            {-1, 0},       {1, 0},
            {-1, 1},{0, 1},{1, 1},
        };

        // Fill the playing field with numbers depending on bomb position
        // As we do not want to handle each case (aka borders of the grid) extra,
        // we loop over all 8 neighbours of every cell and determine if the index is valid.
        for (int row = 0; row < numberOfRows; ++row) {
            for (int col = 0; col < numberOfColumns; ++col) {
                if (data[row][col] == 9) {
                    continue;
                }
                data[row][col] = 0;
                for (int[] neighbour : neighbourhood) {
                    int offx = neighbour[0], offy = neighbour[1];
                    if (    row + offx >= 0 && row + offx < numberOfRows
                        &&  col + offy >= 0 && col + offy < numberOfColumns
                        &&  data[row + offx][col + offy] == 9
                    ) {
                        data[row][col] += 1;
                    }
                }
            }
        }
    }

    /**
     * This method fills the playing Field with the data from the saved game and alters the PlayingField until it is in the same state as the Saved Game and ready to be continued
     * @param savedContent A String coding the Content of each Cell (if there is a Bomb there and how many neighboring Bombs)
     * @param savedStatus A string coding the status of each Cell (if it is untouched, revealed or marked)
     */
    public void fillSavedGame(String savedContent, String savedStatus){

        //Parce the Stings
      //  String[] parcedContent = savedContent.split("");
      //  String[] parcedStatus = savedStatus.split("");
        int[] colors = {R.color.black, R.color.darkblue, R.color.darkgreen, R.color.red, R.color.darkblue, R.color.brown, R.color.cyan, R.color.black, R.color.black};

        //Fill the Playing Field by going through Cell by Cell, filling it with the saved content and setting it to the appropriate status
        for (int row = 0; row < numberOfRows; ++row) {
            for (int col = 0; col < numberOfColumns; ++col) {
                RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(linearize_index(row, col));
                CellView cell = (CellView) holder.itemView.findViewById(R.id.cell);
                if (status[row][col] == 1) {
                    if (data[row][col] == 0) {
                        cell.setText("");
                    } else {
                        cell.setText(String.valueOf(data[row][col]));
                        cell.setTextColor(getResources().getColor(colors[data[row][col]]));
                    }

                    cell.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.middleblue, null));
                    countDownToWin--;
                } else if (status[row][col] == 2) {
                    SpannableStringBuilder builder = new SpannableStringBuilder();
                    Drawable img = getDrawable(R.drawable.flagge);
                    img.setBounds(0, 0, img.getIntrinsicWidth() * cell.getMeasuredHeight() / img.getIntrinsicHeight(), cell.getMeasuredHeight());
                    cell.setCompoundDrawables(img,null,null,null);
                    bombsLeft--;
                    countDownToWin--;
                    mines.setText(String.valueOf(bombsLeft));
                }
            }
        }

        timer = (Chronometer) toolbar.findViewById(R.id.chronometer);
        timer.setBase(SystemClock.elapsedRealtime() - (totalSavedSeconds*1000));

        firstClick = true;

    }

    /**
     * Count the size of the bomb cluster which contains the given cell.
     * A cluster is the biggest set of bombs which are orthogonally connected.
     * @param pos the index of the cell to determine the cluster size.
     * @return int >= 0
     */
    private int countClusterSize(Pair<Integer, Integer> pos) {
        return data[pos.first][pos.second] != 9 ? 0 : countClusterSize(pos, new ArrayList<>());
    }

    /**
     * Recursively counts the size of the bomb cluster which contains the given cell.
     * A cluster is the biggest set of bombs which are orthogonally connected.
     * @param pos the index of the cell to determine the cluster size.
     * @param cluster the current cluster set.
     * @return int >= 0
     */
    private int countClusterSize(Pair<Integer, Integer> pos, List<Pair<Integer, Integer>> cluster) {
        // We want to look at all neighbours of a given cell
        int[][] neighbourhood = {
            {-1,-1},{0,-1},{1,-1},
            {-1, 0},       {1, 0},
            {-1, 1},{0, 1},{1, 1},
        };

        int row = pos.first, col = pos.second;

        // Add all bombs around this bomb, if they weren't already in the cluster.
        // As we do not want to handle each case (aka borders of the grid) extra,
        // we loop over all 8 neighbours of every cell and determine if the index is valid.
        for (int[] neighbour : neighbourhood) {
            int offx = neighbour[0], offy = neighbour[1];
            int x = row + offx, y = col + offy;
            Pair<Integer, Integer> cell = new Pair<>(x,y);
            if (    x >= 0 && x < numberOfRows
                &&  y >= 0 && y < numberOfColumns
                &&  data[x][y] == 9
                &&  !cluster.contains(cell)
            ) {
                cluster.add(cell);
                countClusterSize(cell, cluster);
            }
        }
        return cluster.size();
    }

    /**
     * This method overrides the onItemClick of the Playing Field cells.
     * @param view the View Containing the Cell where the event was triggered
     * @param position the position of the Cell that was clicked
     */
    @Override
    public void onItemClick(View view, int position) {
        //on the first click the timer must be started and the PlayingField must be filled
        Pair<Integer, Integer> index = delinearize_index(position);
        if (firstClick) {
            if (!savecheck) {
                fillPlayingField(index);
                firstClick = false;
                gameEnded = false;

                timer = (Chronometer) toolbar.findViewById(R.id.chronometer);
                timer.setBase(SystemClock.elapsedRealtime());
                timer.start();
            } else {
                // check if the game was initialized -> any cell not empty
                boolean initialized = false;
                for (int[] row : data) {
                    for (int cell : row) {
                        if (cell != 0) {
                            initialized = true;
                            break;
                        }
                    }
                }
                // if game was not initialized -> generate bombs
                if (!initialized) fillPlayingField(index);
                firstClick = false;
                timer = (Chronometer) toolbar.findViewById(R.id.chronometer);
                timer.setBase(SystemClock.elapsedRealtime() - (totalSavedSeconds* 1000L));
                timer.start();
            }
        }

        LinearLayout cellview = (LinearLayout) view;
        CellView cell = (CellView) cellview.getChildAt(0);

        //check if cell is already revealed and has the right amount of mines marked
        int x = index.first, y = index.second;
         if (status[x][y] == 1) {
             revealingAround = true;
             revealCells(index);
             revealingAround = false;
         } else
        //check if we are in marking mode
        if (marking) {
            //only if the cell is not revealed
            if (status[x][y] != 1) {
                //check if already marked
                if (status[x][y] == 2) {
                    countDownToWin++;
                    status[x][y] = 0;
                    cell.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                    bombsLeft++;
                    mines.setText(String.valueOf(bombsLeft));
                } else {
                    status[x][y] = 2;
                    SpannableStringBuilder builder = new SpannableStringBuilder();
                    Drawable img = getDrawable(R.drawable.flagge);
                    img.setBounds(0, 0, img.getIntrinsicWidth() * cell.getMeasuredHeight() / img.getIntrinsicHeight(), cell.getMeasuredHeight());
                    cell.setCompoundDrawables(img,null,null,null);

                    bombsLeft--;
                    countDownToWin--;
                    mines.setText(String.valueOf(bombsLeft));
                    victoryCheck();
                }
            }
        }
        //normal revealing of the cell
        else {
             revealCell(index);
        }
    }

    /**
     * Recursively reveal all cells which are somehow attached to the given cell, if possible.
     * A cell may only be revealed iff count of marked cells neighbouring the current cell is geq than the bomb hint of the cell.
     * @param position
     */
    private void revealCells(Pair<Integer, Integer> position) {
        // We want to look at all neighbours of a given cell
        int[][] neighbourhood = {
                {-1,-1},{0,-1},{1,-1},
                {-1, 0},       {1, 0},
                {-1, 1},{0, 1},{1, 1},
        };

        int row = position.first, col = position.second;

        int tagged = 0;

        // Count all marked neighbours.
        // As we do not want to handle each case (aka borders of the grid) extra,
        // we loop over all 8 neighbours of every cell and determine if the index is valid.
        for (int[] neighbour : neighbourhood) {
            int offx = neighbour[0], offy = neighbour[1];
            int x = row + offx, y = col + offy;
            if (    x >= 0 && x < numberOfRows
                &&  y >= 0 && y < numberOfColumns
                &&  status[x][y] == 2
            ) {
                tagged += 1;
            }
        }

        if (tagged >= data[row][col]) {
            for (int[] neighbour : neighbourhood) {
                int offx = neighbour[0], offy = neighbour[1];
                int x = row + offx, y = col + offy;
                if (    x >= 0 && x < numberOfRows
                    &&  y >= 0 && y < numberOfColumns
                ) {
                    revealCell(new Pair<>(x,y));
                }
            }
        }
    }

    /**
     * This method handles the revealing of a Cell at a specific position
     * @param position position of the cell on the playing field
     */
    private void revealCell(Pair<Integer, Integer> position) {

        //if another cell reveal already lost the game this Method doesnt do anything
        if (gameEnded) {
            return;
        }
        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(linearize_index(position));
        final CellView cell = (CellView) holder.itemView.findViewWithTag(maxHeight);

        int[] colors = {R.color.black, R.color.darkblue, R.color.darkgreen, R.color.red, R.color.darkblue, R.color.brown, R.color.cyan, R.color.black, R.color.black};

        int x = position.first, y = position.second;
        //only reveal if the cell is not marked or revealed
        if (status[x][y] == 0) {
            //check for gameloss
            if (data[x][y] == 9) {

                Drawable img = getDrawable(R.drawable.mine_x);
                img.setBounds(0, 0, img.getIntrinsicWidth() * cell.getMeasuredHeight() / img.getIntrinsicHeight(), cell.getMeasuredHeight());
                cell.setCompoundDrawables(img,null,null,null);

                timer.stop();

                long gametimeInMillis = SystemClock.elapsedRealtime() - timer.getBase();
                long gametime = gametimeInMillis / 1000;
                int time = (int) gametime;

                parameter.putBoolean("victory", false);
                parameter.putInt("time", time);
                parameter.putString("gameMode", game_mode);
                parameter.putBoolean("newBestTime", newBestTime);

                gameEnded = true;

                lockActivityOrientation();
                final Intent tempI = new Intent(this, VictoryScreen.class);
                tempI.putExtras(parameter);
                handler.postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        startActivityForResult(tempI, 0);
                    }
                }, 200);


                //update general statistics (not for user-defined game mode)
                if(!game_mode.equals("user-defined")){
                    //first parameter: game mode
                    //second parameter: 1 as one match was played
                    //third parameter: 1 if game was won, 0 if game was lost
                    //fourth parameter: number of uncovered fields
                    //fifth parameter: playing time in seconds (for won games only)
                    //sixth parameter: playing time in seconds
                    //seventh parameter: actual date and time, here 'lost' to indicate that lost game isn't saved in top times list
                    Object[] result_params = {game_mode, 1, 0, (numberOfCells - countDownToWin), 0, time, "lost"};
                    writer.execute(result_params);
                }

            } else {
                //set cell to revealed
                status[x][y] = 1;

                if (data[x][y] == 0) {
                    cell.setText("");
                } else {
                    cell.setText(String.valueOf(data[x][y]));
                    cell.setTextColor(getResources().getColor(colors[data[x][y]]));
                }

                cell.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.middleblue, null));
                countDownToWin--;
                victoryCheck();

                //check if automatic reveal of surrounding cells is needed
                if (data[x][y] == 0) {
                    revealCells(position);
                }
            }
        }
    }

    /**
     * This method checks if the game is won and
     */
    private void victoryCheck() {
        //if all Cells are revealed or marked and the right Number of Bombs is marked
        if(countDownToWin == 0 && bombsLeft == 0) {
            gameEnded = true;

            long gametimeInMillis = SystemClock.elapsedRealtime() - timer.getBase();
            long gametime = gametimeInMillis / 1000;
            int time = (int) gametime;

            timer.stop();

            if(bestTime > time && !game_mode.equals("user-defined")){
                newBestTime = true;
            }

            parameter.putBoolean("victory", true);
            parameter.putInt("time", time);
            parameter.putString("gameMode", game_mode);
            parameter.putBoolean("newBestTime", newBestTime);

            //start victory screen
            lockActivityOrientation();
            Intent tempI = new Intent(this, VictoryScreen.class);
            tempI.putExtras(parameter);
            startActivityForResult(tempI, 0);

            //update general statistics (not for user-defined game mode)
            if(!game_mode.equals("user-defined")){
                //first parameter: game mode
                //second parameter: 1 as one match was played
                //third parameter: 1 if game was won, 0 if game was lost
                //fourth parameter: number of uncovered fields
                //fifth parameter: playing time in seconds (for won games only)
                //sixth parameter: playing time in seconds
                //seventh parameter: actual date and time
                Object[] result_params = {game_mode, 1, 1, (numberOfCells - countDownToWin), time, time, DateFormat.getDateTimeInstance().format(new Date())};
                writer.execute(result_params);
            }
        }
    }

    /**
     * This method is used to close the PlayActivity when a button on the Victory Screen is pressed
     * @param requestCode the Code for the request, should be 0 if all went well
     * @param resultCode the Code for the result, should be RESULT_OK if nothing broke
     * @param data the Intent of the Activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        }
    }

    /**
     * This method is used to save the game if the PlayActivity is exited without winning or losing
     */
    @Override
    public void onStop(){

            //check if the game has not ended
            if (!gameEnded){
                //no saving of user defined mode
                if (game_mode.equals("user-defined")) {
                    //do nothing
                } else {
                    //ready the save Data
                    int time;
                    if (firstClick) {
                        time = totalSavedSeconds;
                    } else {
                        timer.stop();
                        long gametimeInMillis = SystemClock.elapsedRealtime() - timer.getBase();
                        long gametime = gametimeInMillis / 1000;
                        time = (int) gametime;
                    }

                    //if we are in landscape mode we have to change our data back to normal before saving
                    if(landscape){

                        landscape_data = new int[numberOfColumns][numberOfRows];
                        for (int row = 0; row < numberOfRows; ++row) {
                            for (int col = 0; col < numberOfColumns; ++col) {
                                landscape_data[col][numberOfRows - row - 1] = data[row][col];
                            }
                        }
                        not_in_use_data = data;
                        data = landscape_data;

                        landscape_status = new int[numberOfColumns][numberOfRows];
                        for (int row = 0; row < numberOfRows; ++row) {
                            for (int col = 0; col < numberOfColumns; ++col) {
                                landscape_status[col][numberOfRows - row - 1] = status[row][col];
                            }
                        }
                        not_in_use_status = status;
                        status = landscape_status;

                        //switch back
                        int save = numberOfColumns;
                        numberOfColumns = numberOfRows;
                        numberOfRows = save;
                    }

                    //check if we need to save into database or not
                    if(isChangingConfigurations()) {
                    } else {
                        StringBuilder content = new StringBuilder();
                        StringBuilder states = new StringBuilder();
                        for (int row = 0; row < numberOfRows; ++row) {
                            for (int col = 0; col < numberOfColumns; ++col) {
                                content.append(data[row][col]);
                                states.append(status[row][col]);
                            }
                        }


                        //Save game
                        //first parameter: game mode
                        //second parameter: game time
                        //third parameter: date
                        //fourth parameter: progress
                        //fifth parameter: string coding the content of the playingfield
                        //sixth parameter: string coding the status of the playingfield
                        DatabaseSavedGameWriter writer = new DatabaseSavedGameWriter(new PFMSQLiteHelper(getApplicationContext()), this);
                        Object[] data = {game_mode, time, DateFormat.getDateTimeInstance().format(new Date()), (((double)numberOfCells - countDownToWin)/numberOfCells), content, states};
                        writer.execute(data);

                        //notify that game is saved
                        Toast saveGameInfo = Toast.makeText(getApplicationContext(), getResources().getString(R.string.gameSaved), Toast.LENGTH_SHORT);
                        saveGameInfo.show();
                        finish();
                    }
                }
            }

        super.onStop();
    }

    /**
     * This method is used to set the best time for comparison
     */
    public void setBestTime(int bt){
        bestTime = bt;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        if(isChangingConfigurations()) {

            if(landscape){

                landscape_data = new int[numberOfColumns][numberOfRows];
                for (int row = 0; row < numberOfRows; ++row) {
                    for (int col = 0; col < numberOfColumns; ++col) {
                        landscape_data[col][numberOfRows - row - 1] = data[row][col];
                    }
                }
                not_in_use_data = data;
                data = landscape_data;

                landscape_status = new int[numberOfColumns][numberOfRows];
                for (int row = 0; row < numberOfRows; ++row) {
                    for (int col = 0; col < numberOfColumns; ++col) {
                        landscape_status[col][numberOfRows - row - 1] = status[row][col];
                    }
                }
                not_in_use_status = status;
                status = landscape_status;

                int save = numberOfColumns;
                numberOfColumns = numberOfRows;
                numberOfRows = save;
            }
            int time;
            if (firstClick) {
                time = totalSavedSeconds;
            } else {
                timer.stop();
                long gametimeInMillis = SystemClock.elapsedRealtime() - timer.getBase();
                long gametime = gametimeInMillis / 1000;
                time = (int) gametime;
            }
            // Save the current game state
            savedInstanceState.putInt("columns", numberOfColumns);
            savedInstanceState.putInt("rows", numberOfRows);
            savedInstanceState.putIntArray("data", linearize_data(data, numberOfRows, numberOfColumns));
            savedInstanceState.putIntArray("status", linearize_data(status, numberOfRows, numberOfColumns));
            savedInstanceState.putInt("time", time);
            savedInstanceState.putBoolean("firstclick", firstClick);
            savedInstanceState.putBoolean("gameended", gameEnded);

            Boolean empty;
            if (firstClick && !savecheck) {
                empty  = true;
            } else {
                empty = false;
            }
            savedInstanceState.putBoolean("empty", empty);
        }

            // Always call the superclass so it can save the view hierarchy state
            super.onSaveInstanceState(savedInstanceState);
    }

    private void lockActivityOrientation() {
        Display display = this.getWindowManager().getDefaultDisplay();
        int rotation = display.getRotation();
        int height;
        int width;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
            height = display.getHeight();
            width = display.getWidth();
        } else {
            Point size = new Point();
            display.getSize(size);
            height = size.y;
            width = size.x;
        }
        switch (rotation) {
            case Surface.ROTATION_90:
                if (width > height)
                    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                else
                    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                break;
            case Surface.ROTATION_180:
                if (height > width)
                    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                else
                    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;
            case Surface.ROTATION_270:
                if (width > height)
                    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                else
                    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            default :
                if (height > width)
                    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                else
                    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
}
