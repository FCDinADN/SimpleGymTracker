package com.runApp.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by vadimemrich on 14.03.14.
 */
public class FontLatoTextView extends TextView {
    public FontLatoTextView(Context context) {
        super(context);
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(context.getAssets(),
                    "Lato-Light.ttf");
            setTypeface(tf);
        }
    }

    public FontLatoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(context.getAssets(),
                    "Lato-Light.ttf");
            setTypeface(tf);
        }
    }

    public FontLatoTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(context.getAssets(),
                    "Lato-Light.ttf");
            setTypeface(tf);
        }
    }
}
