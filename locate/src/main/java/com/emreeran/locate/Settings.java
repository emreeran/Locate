package com.emreeran.locate;

import android.location.Criteria;

import com.google.android.gms.location.LocationRequest;

/**
 * Created by Emre Eran on 30/05/2017.
 */

public class Settings {
    /**
     * Ask permissions on initialize if not permitted, defaults to true.
     */
    private boolean mShouldAskPermissions = true;

    /**
     * Use Google Fuse API, defaults to true.
     */
    private boolean mShouldUseFuseProvider = true;

    /**
     * Display issue resolve dialog to user if Google Fuse API is requested and an error was received, defaults to true.
     */
    private boolean mShouldDisplayDialogIfGooglePlayErrorIsResolvable = true;

    /**
     * Is gps available.
     */
    private boolean mCanUseGps;

    /**
     * Run as a background service, defaults to false.
     */
    private boolean mRunAsService = false;

    // FusedApi settings
    private Priority mPriority;
    private Long mInterval;
    private Long mExpirationTime;
    private Long mExpirationDuration;
    private Long mFastestInterval;
    private Long mMaxWaitTime;
    private Integer mNumberOfUpdates;
    private Float mSmallestDisplacement;

    // Constructor

    private Settings(Builder builder) {
        Priority prioritySetting = builder.mPriority;
        if (prioritySetting == null) {
            prioritySetting = Priority.BALANCED;
        }

        Long interval = builder.mInterval;
        if (interval == null) {
            interval = 5000L; // TODO: move to defaults as constant
        }

        Float smallestDisplacement = builder.mSmallestDisplacement;
        if (smallestDisplacement == null) {
            smallestDisplacement = 500F; // TODO: move to defaults as constant
        }

        // Location settings
        mPriority = prioritySetting;
        mInterval = interval;
        mSmallestDisplacement = smallestDisplacement;
        mExpirationTime = builder.mExpirationTime;
        mExpirationDuration = builder.mExpirationDuration;
        mFastestInterval = builder.mFastestInterval;
        mMaxWaitTime = builder.mMaxWaitTime;
        mNumberOfUpdates = builder.mNumberOfUpdates;

        // General settings
        Boolean runAsService = builder.mRunAsService;
        if (runAsService != null ){
            mRunAsService = runAsService;
        }
    }

    // Package private methods

    LocationRequest createGooglePlayLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(mPriority.getValue());

        if (mInterval != null) {
            locationRequest.setInterval(mInterval);
        }

        if (mExpirationTime != null) {
            locationRequest.setExpirationTime(mExpirationTime);
        }

        if (mExpirationDuration != null) {
            locationRequest.setExpirationDuration(mExpirationDuration);
        }

        if (mFastestInterval != null) {
            locationRequest.setFastestInterval(mFastestInterval);
        }

        if (mMaxWaitTime != null) {
            locationRequest.setMaxWaitTime(mMaxWaitTime);
        }

        if (mNumberOfUpdates != null) {
            locationRequest.setNumUpdates(mNumberOfUpdates);
        }

        if (mSmallestDisplacement != null) {
            locationRequest.setSmallestDisplacement(mSmallestDisplacement);
        }

        return locationRequest;
    }

    Criteria createCriteria() {
        Criteria criteria = new Criteria();
        switch (mPriority) {
            case HIGH:
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
                criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
                criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);
                criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
                criteria.setPowerRequirement(Criteria.POWER_HIGH);
                break;
            case BALANCED:
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                criteria.setHorizontalAccuracy(Criteria.ACCURACY_MEDIUM);
                criteria.setVerticalAccuracy(Criteria.ACCURACY_MEDIUM);
                criteria.setBearingAccuracy(Criteria.ACCURACY_MEDIUM);
                criteria.setSpeedAccuracy(Criteria.ACCURACY_MEDIUM);
                criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
                break;
            case LOW:
            case NO_POWER:
            default:
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                criteria.setHorizontalAccuracy(Criteria.ACCURACY_LOW);
                criteria.setVerticalAccuracy(Criteria.ACCURACY_LOW);
                criteria.setBearingAccuracy(Criteria.ACCURACY_LOW);
                criteria.setSpeedAccuracy(Criteria.ACCURACY_LOW);
                criteria.setPowerRequirement(Criteria.POWER_LOW);
                break;
        }
        return criteria;
    }

    // Getters

    Long getInterval() {
        return mInterval;
    }

    Priority getPriority() {
        return mPriority;
    }

    Long getExpirationTime() {
        return mExpirationTime;
    }

    Long getExpirationDuration() {
        return mExpirationDuration;
    }

    Long getFastestInterval() {
        return mFastestInterval;
    }

    Long getMaxWaitTime() {
        return mMaxWaitTime;
    }

    Integer getNumberOfUpdates() {
        return mNumberOfUpdates;
    }

    Float getSmallestDisplacement() {
        return mSmallestDisplacement;
    }

    void setCanUseGps(boolean canUseGps) {
        mCanUseGps = canUseGps;
    }

    boolean isRunAsService() {
        return mRunAsService;
    }

    boolean isShouldAskPermissions() {
        return mShouldAskPermissions;
    }

    boolean isShouldUseFuseProvider() {
        return mShouldUseFuseProvider;
    }

    boolean isShouldDisplayDialogIfGooglePlayErrorIsResolvable() {
        return mShouldDisplayDialogIfGooglePlayErrorIsResolvable;
    }

    boolean isCanUseGps() {
        return mCanUseGps;
    }

    public static class Builder {
        private Priority mPriority;
        private Long mInterval;
        private Long mExpirationTime;
        private Long mExpirationDuration;
        private Long mFastestInterval;
        private Long mMaxWaitTime;
        private Integer mNumberOfUpdates;
        private Float mSmallestDisplacement;
        private Boolean mRunAsService;

        public Settings build() {
            return new Settings(this);
        }

        public Builder priority(Priority priority) {
            mPriority = priority;
            return this;
        }

        public Builder interval(long interval) {
            mInterval = interval;
            return this;
        }

        public Builder expirationTime(long expirationTime) {
            if (expirationTime > 0) {
                mExpirationTime = expirationTime;
            }
            return this;
        }

        public Builder expirationDuration(long expirationDuration) {
            if (expirationDuration > 0) {
                this.mExpirationDuration = expirationDuration;
            }
            return this;
        }

        public Builder fastestInterval(long fastestInterval) {
            if (fastestInterval >= 0) {
                mFastestInterval = fastestInterval;
            }
            return this;
        }

        public Builder maxWaitTime(long maxWaitTime) {
            if (maxWaitTime >= 0) {
                mMaxWaitTime = maxWaitTime;
            }
            return this;
        }

        public Builder numberOfUpdates(int numberOfUpdates) {
            if (numberOfUpdates > 0) {
                mNumberOfUpdates = numberOfUpdates;
            }
            return this;
        }

        public Builder smallestDisplacement(float smallestDisplacement) {
            if (smallestDisplacement >= 0) {
                mSmallestDisplacement = smallestDisplacement;
            }
            return this;
        }

        public Builder runAsService(boolean runAsService) {
            mRunAsService = runAsService;
            return this;
        }
    }

    /**
     * FusedApi request priority
     */
    public enum Priority {
        HIGH(LocationRequest.PRIORITY_HIGH_ACCURACY),
        BALANCED(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY),
        LOW(LocationRequest.PRIORITY_LOW_POWER),
        NO_POWER(LocationRequest.PRIORITY_NO_POWER);

        private final int mValue;

        Priority(int value) {
            mValue = value;
        }

        public int getValue() {
            return mValue;
        }

        public static Priority create(int value) {
            switch (value) {
                case LocationRequest.PRIORITY_HIGH_ACCURACY:
                    return HIGH;
                case LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY:
                    return BALANCED;
                case LocationRequest.PRIORITY_LOW_POWER:
                    return LOW;
                case LocationRequest.PRIORITY_NO_POWER:
                default:
                    return NO_POWER;
            }
        }
    }
}
