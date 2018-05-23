package org.secuso.privacyfriendlyminesweeper.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.secuso.privacyfriendlyminesweeper.R;

/**
 * @author max-dreger
 * @version 20180430
 * This class implements the popup when winning
 */
public class VictoryScreen extends Activity{
    Bundle infoForSameGamemode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        infoForSameGamemode = this.getIntent().getExtras();

        setContentView(R.layout.activity_vicotry);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.playingfield);
       // int height = recyclerView.getMeasuredHeight();
      //  int width = recyclerView.getMeasuredWidth();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int height = dm.heightPixels;
        int width = dm.widthPixels;

        getWindow().setLayout((int)(width*0.8),(int)(height*0.5));

        TextView text = (TextView) findViewById(R.id.victory_text);
        boolean victory = infoForSameGamemode.getBoolean("victory");
        if (victory) {
        } else {
            text.setText("You Suck!");
        }

        final Button ok = (Button) findViewById(R.id.victory_continue);
        final Button stats = (Button) findViewById(R.id.victory_statistics);
        final Button redo = (Button) findViewById(R.id.victory_redo);
        ok.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                toGameActivity();
            }
        });

        stats.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                toStatsActivity();
            }
        });

        redo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                replaySameGamemode();
            }
        });



      //  height = (int) (height*0.5);
       // width = (int) (width*0.8);

    }

    private void toGameActivity() {
        startActivity(new Intent(this, GameActivity.class));
    }
    private void toStatsActivity() {
        startActivity(new Intent(this, StatisticsActivity.class));
    }
    private void replaySameGamemode() {

        Intent intent_for_replay = new Intent(this, PlayActivity.class);
        intent_for_replay.putExtras(infoForSameGamemode);

        startActivity(intent_for_replay);
    }
}
