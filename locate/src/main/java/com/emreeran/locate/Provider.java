package com.emreeran.locate;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by Emre Eran on 31/05/2017.
 */

abstract class Provider {
    abstract void requestLocationUpdates(@NonNull Context context, @NonNull Settings settings, OnLocationChangedListener listener);
    abstract void startService(Context context, Settings settings, Class<? extends LocateService> serviceClass);
}
