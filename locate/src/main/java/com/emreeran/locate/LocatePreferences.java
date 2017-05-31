package com.emreeran.locate;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.location.LocationRequest;

/**
 * Created by Emre Eran on 31/05/2017.
 */

class LocatePreferences {
    private static final String PREFS_NAME = "locatePreferences";
    private static final String KEY_IS_SAVED = "isSaved";
    private static final String KEY_PRIORITY = "priority";
    private static final String KEY_INTERVAL = "interval";
    private static final String KEY_EXPIRATION_TIME = "expirationTime";
    private static final String KEY_EXPIRATION_DURATION = "expirationDuration";
    private static final String KEY_FASTEST_INTERVAL = "fastestInterval";
    private static final String KEY_MAX_WAIT_TIME = "maxWaitTime";
    private static final String KEY_SMALLEST_DISPLACEMENT = "smallestDisplacement";
    private static final String KEY_NUMBER_OF_UPDATES = "numberOfUpdates";

    static void saveSettings(Context context, Settings settings) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        Settings.Priority priority = settings.getPriority();
        Long interval = settings.getInterval();
        Long expirationTime = settings.getExpirationTime();
        Long expirationDuration = settings.getExpirationDuration();
        Long fastestInterval = settings.getFastestInterval();
        Long maxWaitTime = settings.getMaxWaitTime();
        Integer numberOfUpdates = settings.getNumberOfUpdates();
        Float smallestDisplacement = settings.getSmallestDisplacement();

        if (priority != null) {
            prefs.edit().putInt(KEY_PRIORITY, priority.getValue()).apply();
        }

        if (interval != null) {
            prefs.edit().putLong(KEY_INTERVAL, interval).apply();
        }

        if (expirationTime != null) {
            prefs.edit().putLong(KEY_EXPIRATION_TIME, expirationTime).apply();
        }

        if (expirationDuration != null) {
            prefs.edit().putLong(KEY_EXPIRATION_DURATION, expirationDuration).apply();
        }

        if (fastestInterval != null) {
            prefs.edit().putLong(KEY_FASTEST_INTERVAL, fastestInterval).apply();
        }

        if (maxWaitTime != null) {
            prefs.edit().putLong(KEY_MAX_WAIT_TIME, maxWaitTime).apply();
        }

        if (numberOfUpdates != null) {
            prefs.edit().putInt(KEY_NUMBER_OF_UPDATES, numberOfUpdates).apply();
        }

        if (smallestDisplacement != null) {
            prefs.edit().putFloat(KEY_SMALLEST_DISPLACEMENT, smallestDisplacement).apply();
        }

        prefs.edit().putBoolean(KEY_IS_SAVED, true).apply();
    }

    static Settings getSavedSettings(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (prefs.getBoolean(KEY_IS_SAVED, false)) {
            Settings.Priority priority = Settings.Priority.create(prefs.getInt(KEY_PRIORITY, LocationRequest.PRIORITY_NO_POWER));
            Long interval = prefs.getLong(KEY_INTERVAL, 10000);
            Long expirationTime = prefs.getLong(KEY_EXPIRATION_TIME, -1);
            Long expirationDuration = prefs.getLong(KEY_EXPIRATION_DURATION, -1);
            Long fastestInterval = prefs.getLong(KEY_FASTEST_INTERVAL, -1);
            Long maxWaitTime = prefs.getLong(KEY_MAX_WAIT_TIME, -1);
            Integer numberOfUpdates = prefs.getInt(KEY_NUMBER_OF_UPDATES, -1);
            Float smallestDisplacement = prefs.getFloat(KEY_SMALLEST_DISPLACEMENT, -1);

            return new Settings.Builder()
                    .priority(priority)
                    .interval(interval)
                    .expirationTime(expirationTime)
                    .expirationDuration(expirationDuration)
                    .fastestInterval(fastestInterval)
                    .maxWaitTime(maxWaitTime)
                    .numberOfUpdates(numberOfUpdates)
                    .smallestDisplacement(smallestDisplacement)
                    .build();
        } else {
            return null;
        }
    }
}