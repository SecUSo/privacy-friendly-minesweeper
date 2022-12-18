package org.secuso.privacyfriendlyminesweeper.activities.helper;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * @author max-dreger
 * @version 20180430
 * This class implements one cell on the Playing Field
 */
public class CellView extends FrameLayout {

    public CellView(Context context) {
        super(context);
    }

    public CellView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //Turns the Cell View into squares.
    //width is the maximum width so that the squares fill up the whole width oif the screen.
    //maxHeight is the calculated maximum height so that the squares fill up the whole height of the screen.
    @Override
    public void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        int maxHeight;
        int maxWidth;
        if (getTag() == null) {
            maxHeight = getMeasuredHeight();
        } else {
            maxHeight = (int) getTag();
        }
        //turns cell into squares.
        int size = Math.min(getMeasuredWidth(), maxHeight);
 //       int size = Math.min(maxWidth, maxHeight);
        setMeasuredDimension(size, size);
        //LinearLayout parent = (LinearLayout) findViewById(R.id.playingfield_row);
        //parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }
}
