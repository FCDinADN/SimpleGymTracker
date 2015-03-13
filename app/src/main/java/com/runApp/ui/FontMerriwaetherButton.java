package com.runApp.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by vadimemrich on 14.03.14.
 */
public class FontMerriwaetherButton extends Button {

    public FontMerriwaetherButton(Context context) {
        super(context);
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(context.getAssets(),
                    "Merriweather-Light.ttf");
            setTypeface(tf);
        }
    }

    public FontMerriwaetherButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(context.getAssets(),
                    "Merriweather-Light.ttf");
            setTypeface(tf);
        }
    }

    public FontMerriwaetherButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(context.getAssets(),
                    "Merriweather-Light.ttf");
            setTypeface(tf);
        }
    }
}
