package com.emreeran.locate;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Emre Eran on 30/05/2017.
 */

class FuseProvider implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    /**
     * GoogleApiClient instance to use FusedLocationApi
     */
    private GoogleApiClient mGoogleApiClient;

    FuseProvider(Context context) {
        // Get GoogleApiClient instance.
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        Settings settings = Locate.getInstance().getSettings();
        if (settings.isRunAsService()) {
            // Run as background service
            startAsBackgroundService(context, settings);
        } else {
            // Connect to GoogleApiClient.
            connect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Logger.i("Connected to Google Api Client");

        // Client is connected, continue with setting up location updates.
        Settings settings = Locate.getInstance().getSettings();
        startWithListener(settings);
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
        // TODO: Return location
        Logger.i("Location updated; long:" + location.getLongitude() + " lat: " + location.getLatitude());
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

    // Permissions are checked in PermissionValidator
    @SuppressWarnings("MissingPermission")
    private void startAsBackgroundService(Context context, Settings settings) {
        if (PermissionValidator.checkLocationPermissions(context)) {
            Logger.i("Starting as service");
            // Save settings
            LocatePreferences.saveSettings(context, settings);
            // Start service
            context.startService(new Intent(context, FuseService.class));
        } else {
            // Permissions are missing somehow, fail with permissions missing.
            // TODO: fail with requiring permissions
        }
    }

    // Permissions are checked in PermissionValidator
    @SuppressWarnings("MissingPermission")
    private void startWithListener(Settings settings) {
        if (mGoogleApiClient != null) {
            Context context = mGoogleApiClient.getContext();

            if (PermissionValidator.checkLocationPermissions(context)) {
                LocationRequest locationRequest = settings.createGooglePlayLocationRequest();
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
            } else {
                // Permissions are missing somehow, fail with permissions missing.
                // TODO: fail with requiring permissions
            }
        } else {
            // Client is null for some reason, fallback to LocationManager
            // TODO: fallback to LocationManager
        }
    }
}
