package com.runApp.utils;

/**
 * Created by Rares 10/12/14.
 */

import android.util.Log;

/**
 * Helper methods that make logging more consistent throughout the app.
 */
public class LogUtils {
    private static final String LOG_PREFIX = "currency_";
    private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
    private static final int MAX_LOG_TAG_LENGTH = 23;

    public static String makeLogTag(String str) {
        if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            return LOG_PREFIX
                    + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH
                    - 1);
        }

        return LOG_PREFIX + str;
    }

    /**
     * WARNING: Don't use this when obfuscating class names with Proguard!
     */
    public static String makeLogTag(@SuppressWarnings("rawtypes") Class cls) {
        return makeLogTag(cls.getSimpleName());
    }

    public static void LOGD(final String tag, String message) {
        if (!Constants.DEV) {
            return;
        }
        Log.d(tag, message);
    }

    public static void LOGD(final String tag, String message, Throwable cause) {
        if (!Constants.DEV) {
            return;
        }
        if (Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, message, cause);
        }
    }

    public static void LOGI(final String tag, String message) {
        if (!Constants.DEV) {
            return;
        }
        Log.i(tag, message);
    }

    public static void LOGI(final String tag, String message, Throwable cause) {
        if (!Constants.DEV) {
            return;
        }
        Log.i(tag, message, cause);
    }

    public static void LOGW(final String tag, String message) {
        if (!Constants.DEV) {
            return;
        }
        Log.w(tag, message);
    }

    public static void LOGW(final String tag, String message, Throwable cause) {
        if (!Constants.DEV) {
            return;
        }
        Log.w(tag, message, cause);
    }

    public static void LOGE(final String tag, String message) {
        if (!Constants.DEV) {
            return;
        }
        Log.e(tag, message);
    }

    public static void LOGE(final String tag, String message, Throwable cause) {
        if (!Constants.DEV) {
            return;
        }
        Log.e(tag, message, cause);
    }

    private LogUtils() {
    }
}