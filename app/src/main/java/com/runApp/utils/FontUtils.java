package com.runApp.utils;

import android.graphics.Typeface;
import android.widget.TextView;

/**
 * Created by Rares on 23/09/14.
 */
public class FontUtils {

    private static FontUtils mFontUtils = null;
    private static Typeface robotoLight = null;
    private static Typeface robotoLightItalian = null;
    private static Typeface robotoMedium = null;
    private static Typeface robotoRegular = null;

    public static FontUtils getInst() {
        if (mFontUtils == null) {
            mFontUtils = new FontUtils();
        }
        return mFontUtils;
    }

    public void setRobotoLight(TextView textView) {
        if (robotoLight == null) {
            robotoLight = Typeface.createFromAsset(Utils.getContext().getAssets(), "fonts/Roboto-Light.ttf");
        }
        if (textView == null) {
            return;
        }
        textView.setTypeface(robotoLight);
    }

    public void setRobotoLightItalian(TextView textView) {
        if (robotoLightItalian == null) {
            robotoLightItalian = Typeface.createFromAsset(Utils.getContext().getAssets(), "fonts/Roboto-LightItalic.ttf");
        }
        if (textView == null) {
            return;
        }
        textView.setTypeface(robotoLightItalian);
    }

    public void setRobotoMedium(TextView textView) {
        if (robotoMedium == null) {
            robotoMedium = Typeface.createFromAsset(Utils.getContext().getAssets(), "fonts/Roboto-Medium.ttf");
        }
        if (textView == null) {
            return;
        }
        textView.setTypeface(robotoMedium);
    }

    public void setRobotoRegular(TextView textView) {
        if (robotoRegular == null) {
            robotoRegular = Typeface.createFromAsset(Utils.getContext().getAssets(), "fonts/Roboto-Regular.ttf");
        }
        if (textView == null) {
            return;
        }
        textView.setTypeface(robotoRegular);
    }
}