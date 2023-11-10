package com.ikksa.customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class Button_Medium extends androidx.appcompat.widget.AppCompatButton{


    public Button_Medium(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public Button_Medium(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Button_Medium(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Euclid Circular A Medium.ttf");
            setTypeface(tf);
        }
    }

}
