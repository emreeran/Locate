package com.emreeran.locate;

import android.os.Bundle;
import android.util.Log;

import java.util.Set;

/**
 * Created by Emre Eran on 27/05/2017.
 */

final class Logger {

    private static boolean isDebug = false;

    private Logger() {
    }

    static void setDebug(boolean isDebug) {
        Logger.isDebug = isDebug;
    }

    static void i(String message) {
        if (isDebug) {
            Log.i(getClassName(), message);
        }
    }

    static void d(String message) {
        if (isDebug) {
            Log.d(getClassName(), message);
        }
    }

    static void e(String message) {
        if (isDebug) {
            Log.e(getClassName(), message);
        }
    }

    static void e(Throwable e) {
        if (isDebug) {
            Log.e(getClassName(), e.getLocalizedMessage(), e);
        }
    }

    static void e(String message, Throwable e) {
        if (isDebug) {
            Log.e(getClassName(), message, e);
        }
    }

    static String parseBundleToString(Bundle bundle) {
        String bundleString = "";
        try {
            if (null == bundle) {
                return bundleString;
            }
            Set<String> ketSet = bundle.keySet();
            if (null == ketSet || ketSet.isEmpty()) return bundleString;

            for (String key : ketSet) {
                Object obj = bundle.get(key);
                if (null != obj) {
                    bundleString += "[ " + key + " = " + obj.toString() + " ]";
                }
            }

        } catch (Exception e) {
            Log.e(getClassName(), e.getMessage(), e);
        }

        return bundleString;
    }

    private static String getClassName() {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        StackTraceElement relevantTrace = trace[4];
        String className = relevantTrace.getClassName();
        int lastIndex = className.lastIndexOf('.');
        return className.substring(lastIndex + 1);
    }
}
