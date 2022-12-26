package org.secuso.privacyfriendlyminesweeper.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.secuso.privacyfriendlyminesweeper.R;

/**
 * @author max-dreger
 * @version 20180430
 * This class implements the popup activity when a game is finished (won/lost)
 * It shows information about the recently played game (won/lost, played time, game mode)
 * It implements options to play the same game mode again, to return to the game activity and to go to the statistics activity (options are accessible over buttons)
 */
public class VictoryScreen extends Activity{
    Bundle infoForScreen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        infoForScreen = this.getIntent().getExtras();

        setContentView(R.layout.activity_victory);


        DisplayMetrics dm = new DisplayMetrics();
       getWindowManager().getDefaultDisplay().getMetrics(dm);

       int height = dm.heightPixels;
       int width = dm.widthPixels;

       // int height = 1794;
       // int width = 1080;


        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            getWindow().setLayout((int)(width*0.5),(int)(height*0.8));
        } else {
            getWindow().setLayout((int)(width*0.8),(int)(height*0.5));
        }


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
        if(game_mode.equals("difficult")){
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
    /**
     * This method just closes the Victory screen so the user gets put back into the game activity
     */
    private void toGameActivity() {
        finish();
    }
    /**
     * This method starts the stat activity
     */
    private void toStatsActivity() {
        startActivity(new Intent(this, StatisticsActivity.class));

        finish();
    }

    /**
     * This method starts a new game with the same parameter as before
     */
    private void replaySameGamemode() {

        Intent intent_for_replay = new Intent(this, PlayActivity.class);
        intent_for_replay.putExtras(infoForScreen);
        startActivity(intent_for_replay);
        finish();
    }

    @Override
    protected void onPause() {
        if(isChangingConfigurations()) {
            setResult(RESULT_OK, null);
            finish();
        }
        super.onPause();
    }
}
