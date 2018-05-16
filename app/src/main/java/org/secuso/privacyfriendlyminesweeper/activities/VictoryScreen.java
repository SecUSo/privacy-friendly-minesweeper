package org.secuso.privacyfriendlyminesweeper.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import org.secuso.privacyfriendlyminesweeper.R;

/**
 * @author max-dreger
 * @version 20180430
 * This class implements the popup when winning
 */
public class VictoryScreen extends Activity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vicotry);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.playingfield);
       // int height = recyclerView.getMeasuredHeight();
      //  int width = recyclerView.getMeasuredWidth();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int height = dm.heightPixels;
        int width = dm.widthPixels;

        getWindow().setLayout((int)(width*0.8),(int)(height*0.5));


        final Button button = (Button) findViewById(R.id.victory_continue);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                toGameActivity();
            }
        });

      //  height = (int) (height*0.5);
       // width = (int) (width*0.8);

    }

    public void toGameActivity() {
        startActivity(new Intent(this, GameActivity.class));
    }
}
