package com.hommlie.customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class EditText_Regular extends androidx.appcompat.widget.AppCompatEditText {

    public EditText_Regular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public EditText_Regular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditText_Regular(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/plusjkartasans_regular.ttf");
            setTypeface(tf);
        }
    }
}
