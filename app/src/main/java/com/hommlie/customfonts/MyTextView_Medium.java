package com.hommlie.customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by wolfsoft1 on 31/1/18.
 */

public class MyTextView_Medium extends androidx.appcompat.widget.AppCompatTextView {
    public MyTextView_Medium(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyTextView_Medium(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextView_Medium(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/plussakartasans_medium.ttf");
            setTypeface(tf);
        }
    }
}
