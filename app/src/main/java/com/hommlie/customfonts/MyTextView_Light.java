package com.hommlie.customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by wolfsoft1 on 31/1/18.
 */

public class MyTextView_Light extends TextView {
    public MyTextView_Light(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyTextView_Light(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextView_Light(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/plussakartasans_light.ttf");
            setTypeface(tf);
        }
    }
}
