package com.melodicloud.util;

import android.util.Log;

import com.melodicloud.common.Constants;

/**
 * Use this log utility to quickly switch on/off application logs
 */
public class LogUtil {

    public static void d(String tag, String msg) {
        if (Constants.DEBUG && msg != null) {
            Log.d(tag, msg);

        }
    }

    public static void i(String tag, String msg) {
        if (Constants.DEBUG && msg != null) {
            Log.i(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (Constants.DEBUG && msg != null)
            Log.v(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (Constants.DEBUG && msg != null)
            Log.e(tag, msg);
    }

    public static void logLongString(final String tag, final String log) {
        if (Constants.DEBUG && log != null) {
            i(tag,
                    "==================================================================");
            int remainingChars = 0;
            final int MAX_LENGTH = 3070;
            final char[] c = log.toCharArray();
            remainingChars = c.length;
            for (int i = 0; i < c.length; i += MAX_LENGTH) {
                String s;
                if (remainingChars >= MAX_LENGTH) {
                    s = new String(c, i, MAX_LENGTH);
                } else {
                    s = new String(c, i, remainingChars);
                }

                remainingChars -= MAX_LENGTH;
                d(tag, s);
            }

            i(tag,
                    "==================================================================");
        }
    }

    public static void e(String tag, String msg, Throwable throwable) {
        if (Constants.DEBUG && msg != null)
            Log.e(tag, msg, throwable);
    }

    public static void w(String tag, String msg) {

        if (Constants.DEBUG && msg != null)
            Log.w(tag, msg);

    }
}
