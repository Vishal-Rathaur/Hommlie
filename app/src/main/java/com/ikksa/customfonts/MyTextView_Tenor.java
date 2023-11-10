package com.ikksa.customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class MyTextView_Tenor extends androidx.appcompat.widget.AppCompatTextView {
    public MyTextView_Tenor(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyTextView_Tenor(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextView_Tenor(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/NanumMyeongjoBold.ttf");
            setTypeface(tf);
        }
    }
}
