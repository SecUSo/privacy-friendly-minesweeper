package org.secuso.privacyfriendlyminesweeper.activities.helper;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import org.secuso.privacyfriendlyminesweeper.activities.PlayActivity;

public class CellView extends android.support.v7.widget.AppCompatButton {
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
        if (getTag() == null) {
            maxHeight = getMeasuredHeight();
        } else {
            maxHeight = (int) getTag();
        }
        //turns cell into squares.
        int size = Math.min(getMeasuredWidth(), maxHeight);
        setMeasuredDimension(size, size);
    }
}
