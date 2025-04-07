package com.hommlie.customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyTextView_SemiBold extends TextView {
    public MyTextView_SemiBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyTextView_SemiBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextView_SemiBold(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/plusjakartasans_semi_bold.ttf");
            setTypeface(tf);
        }
    }
}

