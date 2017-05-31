package com.emreeran.locate;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Emre Eran on 31/05/2017.
 */

public abstract class LocateService extends Service
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.setDebug(true);
        Logger.i("Started service");

        if (intent == null) {
            Logger.i("intent is null");
        }

        int providerType = LocatePreferences.getProvider(getApplicationContext());

        // Create location request
        Settings settings = LocatePreferences.getSavedSettings(getApplicationContext());
        if (settings != null) {
            if (providerType == LocatePreferences.PROVIDER_FUSE) {
                // Using Fuse provider
                mLocationRequest = settings.createGooglePlayLocationRequest();

                // Get GoogleApiClient instance.
                mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();

                connect();
            } else if (providerType == LocatePreferences.PROVIDER_LOCATION_MANAGER) {
                // Using LocationManager provider
                // TODO: implement
            } else {
                // Provider type is not set
                stopSelf();
            }

        } else {
            // Settings are not available, stop self.
            stopSelf();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.i("Stopped service");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Logger.i("Connected to Google Api Client");

        // Client is connected, continue with setting up location updates.
        startTracking();
    }

    @Override
    public void onConnectionSuspended(int i) {
        // TODO: handle connection suspension
        Logger.i("Connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Logger.i("Connection failed");
        // Connection failed, check if error is user resolvable. Falls back to LocationManager if not.
        if (mGoogleApiClient != null) {
            Locate.getInstance().resolveGooglePlayServicesConnectionIssue(
                    mGoogleApiClient.getContext(),
                    connectionResult.getErrorCode(),
                    Locate.getInstance().getSettings().isShouldDisplayDialogIfGooglePlayErrorIsResolvable()
            );
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // Implement this
    }

    // Permissions are checked in PermissionValidator
    @SuppressWarnings("MissingPermission")
    private void startTracking() {
        if (mGoogleApiClient != null) {
            if (PermissionValidator.checkLocationPermissions(getApplicationContext())) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                // Permissions are missing somehow, fail with permissions missing.
                // TODO: fail with requiring permissions
            }
        } else {
            // Client is null for some reason, fallback to LocationManager
            // TODO: fallback to LocationManager
        }
    }

    private void connect() {
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                // Reconnect if already connected.
                mGoogleApiClient.reconnect();
            } else {
                // Connect to api if not connected.
                mGoogleApiClient.connect();
            }
        }
    }
}
