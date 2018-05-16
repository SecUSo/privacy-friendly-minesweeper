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
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.secuso.privacyfriendlyminesweeper.R;
import org.secuso.privacyfriendlyminesweeper.activities.adapter.PlayRecyclerViewAdapter;
import org.secuso.privacyfriendlyminesweeper.activities.helper.BaseActivity;
import org.secuso.privacyfriendlyminesweeper.activities.helper.CellView;

import java.util.Random;

/**
 * @author I3ananas
 * @version 20180430
 * This class implements functions required to handle the process of playing
 */
public class PlayActivity extends BaseActivity implements PlayRecyclerViewAdapter.ItemClickListener{
    PlayRecyclerViewAdapter adapter;
    int numberOfRows;
    int numberOfColumns;
    int numberOfBombs;
    int numberOfCells;
    RecyclerView recyclerView;
    int maxHeight;
    int maxWidth;
    boolean firstTime;
    boolean marking;
    int[] data;
    int[] status;
    TextView mines;
    int bombsLeft;
    int countDownToWin;

    protected void onCreate(Bundle param){
        super.onCreate(param);
        setContentView(R.layout.activity_play);

        numberOfColumns = 0;
        numberOfRows = 0;
        numberOfBombs = 0;

        //TODO: fix continuing a game (for now if/else clause)
        Bundle b = this.getIntent().getExtras();
        if (b==null){

        }else {
            short[] test = b.getShortArray("info");
            numberOfColumns = test[0];
            numberOfRows = test[1];
            numberOfBombs = test[2];
            switch (numberOfColumns) {
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

        //TODO: create playing field of buttons from parameters
        //TODO: fix positions when screen is being flipped
        //Filling the PlayingField
        numberOfCells = numberOfRows * numberOfColumns;
        data = new int[numberOfCells];
        countDownToWin = numberOfCells;

        //put bombs at random positions
        for (int i = 0; i < numberOfBombs; i++) {
            int position;
            Random randomGen = new Random();
            position = randomGen.nextInt(numberOfCells);
            //10 equals a bomb
            //redo random position if there is a bomb already
            if (data[position] == 10) {
                i--;
            }
            data[position] = 10;
        }

        //Fill the Playingfield with numbers depending on bomb position
        for (int pos= 0; pos < numberOfCells; pos++) {

            if (data[pos] != 10) {
                data[pos] = 0;
                //check if position is in one corner of the Field
                //bottom left
                if (pos == 0) {
                    if (data[pos + 1] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                    if (data[pos + numberOfColumns] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                    if (data[pos + numberOfColumns + 1] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                }
                //bottom right
                else if (pos == (numberOfColumns - 1)) {
                    if (data[pos - 1] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                    if (data[pos + numberOfColumns] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                    if (data[pos + numberOfColumns - 1] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                }
                //top left
                else if (pos == (numberOfCells - numberOfColumns)) {
                    if (data[pos + 1] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                    if (data[pos - numberOfColumns] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                    if (data[pos - numberOfColumns + 1] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                }
                //top right
                else if (pos == numberOfCells - 1) {
                    if (data[pos - 1] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                    if (data[pos - numberOfColumns] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                    if (data[pos - numberOfColumns - 1] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                }
                //bottom row
                else if (pos < numberOfColumns) {
                    if (data[pos - 1] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                    if (data[pos + 1] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                    if (data[pos + numberOfColumns - 1] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                    if (data[pos + numberOfColumns] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                    if (data[pos + numberOfColumns + 1] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                }
                //top row
                else if (pos > numberOfCells - numberOfColumns) {
                    if (data[pos - 1] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                    if (data[pos + 1] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                    if (data[pos - numberOfColumns - 1] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                    if (data[pos - numberOfColumns] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                    if (data[pos - numberOfColumns + 1] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                }
                //left column
                else if (pos % numberOfColumns == 0) {
                    if (data[pos + 1] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                    if (data[pos + numberOfColumns] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                    if (data[pos + numberOfColumns + 1] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                    if (data[pos - numberOfColumns] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                    if (data[pos - numberOfColumns + 1] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                }
                //right column
                else if (pos % numberOfColumns == (numberOfColumns - 1)) {
                    if (data[pos - 1] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                    if (data[pos + numberOfColumns] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                    if (data[pos + numberOfColumns - 1] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                    if (data[pos - numberOfColumns] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                    if (data[pos - numberOfColumns - 1] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                }
                //the rest (inner cells)
                else {
                    if (data[pos - 1] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                    if (data[pos + 1] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                    if (data[pos + numberOfColumns + 1] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                    if (data[pos + numberOfColumns] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                    if (data[pos + numberOfColumns - 1] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                    if (data[pos - numberOfColumns + 1] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                    if (data[pos - numberOfColumns] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                    if (data[pos - numberOfColumns - 1] == 10) {
                        data[pos] = data[pos] + 1;
                    }
                }
            }
        }

        //status saves the state of the cell
        //0 = normal, 1 = revealed, 2 = marked
        status = new int[numberOfCells];
        for (int i = 0; i < numberOfCells; i++) {
            status[i] = 0;
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
                int height = recyclerView.getHeight()- Math.round(32*(getResources().getDisplayMetrics().xdpi/ DisplayMetrics.DENSITY_DEFAULT));
                //set height of recyclerView so it does not overlap the play_sidebar
                ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                params.height = height;
                maxHeight = height/numberOfRows;
                //cells have a buffer of 2dp, so substract 1dp*2 transformed into pixel value
                maxHeight = maxHeight - Math.round(3*(getResources().getDisplayMetrics().xdpi/ DisplayMetrics.DENSITY_DEFAULT));

   //             int width = recyclerView.getWidth();
   //             maxWidth = width/numberOfColumns;
  //              maxWidth = maxWidth - Math.round(2*(getResources().getDisplayMetrics().xdpi/ DisplayMetrics.DENSITY_DEFAULT));

                if (firstTime) {
                    firstTime=false;
                    createAdapter(maxHeight);
   //                 createAdapter(maxHeight);
                    //after heightTest is made invisible the grid is redrawn, this time with the correct maxheight
                    heightTest.setVisibility(View.GONE);
                }
            }
        });

        //fistLaunch
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns, LinearLayoutManager.VERTICAL, true));
        createAdapter(maxHeight);

        //handling the Button that toggles between revealing cells and marking them as mines
        marking = false;
        final Button button = (Button) findViewById(R.id.toggle);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (marking) {
                    button.setBackground(getDrawable(R.drawable.button_highlighted));
                    marking = false;
                    button.setText("MARK");
                } else {
                    view.setBackground(getDrawable(R.drawable.button_highlighted_clicked));
                    marking = true;
                    button.setText("MARKING...");

                }
            }
        });

        bombsLeft = numberOfBombs;
        mines = (TextView) findViewById(R.id.mines);
        mines.setText(String.valueOf(bombsLeft));

        ImageView mines = (ImageView) findViewById(R.id.mines_pic);
        mines.setImageResource(R.drawable.mine);

    }
    private void createAdapter(int maximumHeight) {
        adapter = new PlayRecyclerViewAdapter(this, data, maxHeight);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        LinearLayout cellview = (LinearLayout) view;
        RecyclerView.ViewHolder testviewww = recyclerView.findViewHolderForAdapterPosition(position);
        CellView testcell = (CellView) testviewww.itemView.findViewWithTag(maxHeight);

        Log.i("TAG", "You clicked number " + adapter.getItem(position) + ", which is at cell position " + position);

        CellView cell = (CellView) cellview.getChildAt(0);

        //check if cell is already revealed and has the right amount of mines marked
         if (status[position] == 1) {
             revealAroundCell(position, true);
         } else
        //check if we are in marking mode
        if (marking) {
            //only if the cell is not revealed
            if (status[position] != 1) {
                //check if already marked
                if (status[position] == 2) {
                    status[position] = 0;
                    cell.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    countDownToWin++;
                 //   cell.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
                //    cell.setBackgroundResource(R.color.colorAccent);

              //      cell.setText("");
                    bombsLeft++;
                    mines.setText(String.valueOf(bombsLeft));
                } else {
                    status[position] = 2;
                    SpannableStringBuilder builder = new SpannableStringBuilder();
                    countDownToWin--;
                    victoryCheck();
                 //   builder.append(" ");
                  //  builder.setSpan(new ImageSpan(this, R.drawable.flag),builder.length() - 1, builder.length(), 0);
                   // builder.append(" Cree by Dexode");

              //      cell.setText(builder);
              //      cell.setCompoundDrawablesWithIntrinsicBounds(R.drawable.flag2, 0, 0, 0);
                    Drawable img = getDrawable(R.drawable.flag);
                    img.setBounds(0, 0, img.getIntrinsicWidth() * cell.getMeasuredHeight() / img.getIntrinsicHeight(), cell.getMeasuredHeight());
                   // img.setColorFilter(new PorterDuffColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null), PorterDuff.Mode.SRC_IN));
                   // img.setAlpha(0);
                    cell.setCompoundDrawables(img,null,null,null);
                   // cell.setText("F");
                    bombsLeft--;
                    mines.setText(String.valueOf(bombsLeft));
                }
            }
        }
        //normal revealing of the cell
        else {
             revealCell(position);
        }
    }

    private void revealAroundCell(int position, boolean revealed) {

        //if revealed is true then
        if (revealed) {
            //check if the right amount of mines is tagged
            int taggedCells = 0;

            //check if position is in one corner of the Field
            //bottom left
            if (position == 0) {
                if (status[position + 1] == 2) {
                    taggedCells++;
                }
                if (status[position + numberOfColumns] == 2) {
                    taggedCells++;
                }
                if (status[position + numberOfColumns + 1] == 2) {
                    taggedCells++;
                }
            }
            //bottom right
            else if (position == (numberOfColumns - 1)) {
                if (status[position - 1] == 2) {
                    taggedCells++;
                }
                if (status[position + numberOfColumns] == 2) {
                    taggedCells++;
                }
                if (status[position + numberOfColumns - 1] == 2) {
                    taggedCells++;
                }
            }
            //top left
            else if (position == (numberOfCells - numberOfColumns)) {
                if (status[position + 1] == 2) {
                    taggedCells++;
                }
                if (status[position - numberOfColumns] == 2) {
                    taggedCells++;
                }
                if (status[position - numberOfColumns + 1] == 2) {
                    taggedCells++;
                }
            }
            //top right
            else if (position == numberOfCells - 1) {
                if (status[position - 1] == 2) {
                    taggedCells++;
                }
                if (status[position - numberOfColumns] == 2) {
                    taggedCells++;
                }
                if (status[position - numberOfColumns - 1] == 2) {
                    taggedCells++;
                }
            }
            //bottom row
            else if (position < numberOfColumns) {
                if (status[position + 1] == 2) {
                    taggedCells++;
                }
                if (status[position - 1] == 2) {
                    taggedCells++;
                }
                if (status[position + numberOfColumns] == 2) {
                    taggedCells++;
                }
                if (status[position + numberOfColumns - 1] == 2) {
                    taggedCells++;
                }
                if (status[position + numberOfColumns + 1] == 2) {
                    taggedCells++;
                }
            }
            //top row
            else if (position > numberOfCells - numberOfColumns) {
                if (status[position + 1] == 2) {
                    taggedCells++;
                }
                if (status[position - 1] == 2) {
                    taggedCells++;
                }
                if (status[position - numberOfColumns] == 2) {
                    taggedCells++;
                }
                if (status[position - numberOfColumns - 1] == 2) {
                    taggedCells++;
                }
                if (status[position - numberOfColumns + 1] == 2) {
                    taggedCells++;
                }
            }
            //left column
            else if (position % numberOfColumns == 0) {
                if (status[position + 1] == 2) {
                    taggedCells++;
                }
                if (status[position + numberOfColumns] == 2) {
                    taggedCells++;
                }
                if (status[position + numberOfColumns + 1] == 2) {
                    taggedCells++;
                }
                if (status[position - numberOfColumns] == 2) {
                    taggedCells++;
                }
                if (status[position - numberOfColumns + 1] == 2) {
                    taggedCells++;
                }
            }
            //right column
            else if (position % numberOfColumns == (numberOfColumns - 1)) {
                if (status[position - 1] == 2) {
                    taggedCells++;
                }
                if (status[position + numberOfColumns] == 2) {
                    taggedCells++;
                }
                if (status[position + numberOfColumns - 1] == 2) {
                    taggedCells++;
                }
                if (status[position - numberOfColumns] == 2) {
                    taggedCells++;
                }
                if (status[position - numberOfColumns - 1] == 2) {
                    taggedCells++;
                }
            }
            //the rest (inner cells)
            else {
                if (status[position - 1] == 2) {
                    taggedCells++;
                }
                if (status[position + 1] == 2) {
                    taggedCells++;
                }
                if (status[position + numberOfColumns] == 2) {
                    taggedCells++;
                }
                if (status[position + numberOfColumns - 1] == 2) {
                    taggedCells++;
                }
                if (status[position + numberOfColumns + 1] == 2) {
                    taggedCells++;
                }
                if (status[position - numberOfColumns] == 2) {
                    taggedCells++;
                }
                if (status[position - numberOfColumns - 1] == 2) {
                    taggedCells++;
                }
                if (status[position - numberOfColumns + 1] == 2) {
                    taggedCells++;
                }
            }
            if (taggedCells == data[position]) {
                revealAroundCell(position, false);
            }
        }
        //revealing around the cell because the cell has 0 mines adjacent (or the right amount of marks)
        else {
            //check position of the cell
            //bottom left
            if (position == 0) {
                revealCell(position + 1);
                revealCell(position + numberOfColumns);
                revealCell(position + numberOfColumns + 1);
            }
            //bottom right
            else if (position == (numberOfColumns - 1)) {
                revealCell(position - 1);
                revealCell(position + numberOfColumns);
                revealCell(position + numberOfColumns - 1);
            }
            //top left
            else if (position == (numberOfCells - numberOfColumns)) {
                revealCell(position + 1);
                revealCell(position - numberOfColumns);
                revealCell(position - numberOfColumns + 1);
            }
            //top right
            else if (position == numberOfCells - 1) {
                revealCell(position - 1);
                revealCell(position - numberOfColumns);
                revealCell(position - numberOfColumns - 1);
            }
            //bottom row
            else if (position < numberOfColumns) {
                revealCell(position + 1);
                revealCell(position - 1);
                revealCell(position + numberOfColumns);
                revealCell(position + numberOfColumns + 1);
                revealCell(position + numberOfColumns - 1);
            }
            //top row
            else if (position > numberOfCells - numberOfColumns) {
                revealCell(position + 1);
                revealCell(position - 1);
                revealCell(position - numberOfColumns);
                revealCell(position - numberOfColumns + 1);
                revealCell(position - numberOfColumns - 1);
            }
            //left column
            else if (position % numberOfColumns == 0) {
                revealCell(position + 1);
                revealCell(position + numberOfColumns);
                revealCell(position + numberOfColumns + 1);
                revealCell(position - numberOfColumns);
                revealCell(position - numberOfColumns + 1);
            }
            //right column
            else if (position % numberOfColumns == (numberOfColumns - 1)) {
                revealCell(position - 1);
                revealCell(position + numberOfColumns);
                revealCell(position + numberOfColumns - 1);
                revealCell(position - numberOfColumns);
                revealCell(position - numberOfColumns - 1);
            }
            //the rest (inner cells)
            else {
                revealCell(position + 1);
                revealCell(position - 1);
                revealCell(position + numberOfColumns);
                revealCell(position + numberOfColumns + 1);
                revealCell(position + numberOfColumns - 1);
                revealCell(position - numberOfColumns);
                revealCell(position - numberOfColumns + 1);
                revealCell(position - numberOfColumns - 1);
            }
        }
    }

    private void revealCell(int position) {

        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(position);
        CellView cell = (CellView) holder.itemView.findViewWithTag(maxHeight);

        //only reveal if the cell is not marked or revealed
        if (status[position] == 0) {
            //check for gameloss
            if (data[position] == 10) {
                cell.setText("M");
                //todo: lose game
                System.out.println("__________YOU LOSE__________");
            } else {
                //set cell to revealed
                status[position] = 1;

                cell.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.middleblue, null));
                cell.setText(String.valueOf(data[position]));

                countDownToWin--;
                victoryCheck();

                //check if automatic reveal of surrounding cells is needed
                if (data[position] == 0) {
                    revealAroundCell(position, false);
                }
            }
        }
    }

    private void victoryCheck() {
        if(countDownToWin == 0) {
            //todo: implement winning
            System.out.println("!__________YOU Win__________!");
        }
    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_play;
    }

    public int getNumberOfRows() { return numberOfRows; }
    public int getMaxHeight() { return maxHeight; }
}
