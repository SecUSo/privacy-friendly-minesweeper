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
    Bundle infoForScreen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        infoForScreen = this.getIntent().getExtras();

        setContentView(R.layout.activity_vicotry);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.playingfield);
       // int height = recyclerView.getMeasuredHeight();
      //  int width = recyclerView.getMeasuredWidth();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int height = dm.heightPixels;
        int width = dm.widthPixels;

        getWindow().setLayout((int)(width*0.8),(int)(height*0.5));

        //get time and get it ready for correct display
        int time = infoForScreen.getInt("time");
        int minutes = 0;
        while (time > 59) {
            minutes++;
            time = time - 60;
        }
        boolean padding = false;
        if (time < 10) {
            padding = true;
        }

        //set the text
        TextView text = (TextView) findViewById(R.id.victory_text);
        boolean victory = infoForScreen.getBoolean("victory");
        if (victory) {
            if (padding) {
                text.setText(getString(R.string.victory) + " " + minutes + ":0" + time);
            } else {
                text.setText(getString(R.string.victory) + " " + minutes + ":" + time);
            }
        } else {
            if (padding) {
                text.setText(getString(R.string.defeat1) + " " + minutes + ":0" + time + " " + getString(R.string.defeat2));
            } else {
                text.setText(getString(R.string.defeat1) + " " + minutes + ":" + time + " " + getString(R.string.defeat2));
            }
        }

        //set text about game mode
        TextView textGameMode = (TextView) findViewById(R.id.victory_text_game_mode);
        String textGM = getString(R.string.gameMode);
        String game_mode = infoForScreen.getString("gameMode");
        if(game_mode.equals("easy")){
            textGM = textGM + ": " + getString(R.string.game_mode_easy);
            textGameMode.setText(textGM);
        }
        if(game_mode.equals("medium")){
            textGM = textGM + ": " + getString(R.string.game_mode_medium);
            textGameMode.setText(textGM);
        }
        if(game_mode.equals("hard")){
            textGM = textGM + ": " + getString(R.string.game_mode_difficult);
            textGameMode.setText(textGM);
        }

        //set text if a new best time was achieved
        TextView textNewBestTime = (TextView) findViewById(R.id.victory_text_new_best_time);
        boolean newBestTime = infoForScreen.getBoolean("newBestTime");
        if(newBestTime){
            textNewBestTime.setText(getString(R.string.newBestTime));
        }
        else{
            textNewBestTime.setText("");
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

        //result is important for the correct closing of the playactivity
        setResult(RESULT_OK, null);

    }

    private void toGameActivity() {
        Intent intent = new Intent(this, GameActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        finish();
    }
    private void toStatsActivity() {
        startActivity(new Intent(this, StatisticsActivity.class));

        finish();
    }
    private void replaySameGamemode() {

        Intent intent_for_replay = new Intent(this, PlayActivity.class);
        intent_for_replay.putExtras(infoForScreen);
        startActivity(intent_for_replay);
        finish();
    }
}
