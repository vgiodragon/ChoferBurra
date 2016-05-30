package com.example.giovanny.choferburra;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by giovanny on 30/05/16.
 */
public class GLocalization implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    GoogleApiClient mGoogleApiClient;

    LocationRequest mLocationRequest;
    int Interval=3000;
    int FastInterval=2000;

    Context ctx;
    String mTAG = "glocalization";
    double lati=0.1, longi=-12.04;

    public GLocalization(Context ctx) {
        this.ctx = ctx;
        setGoogleApliClient();
        Log.d(mTAG, "contructor!");
    }

    public void setGoogleApliClient() {
        if (mGoogleApiClient == null) {

            mGoogleApiClient = new GoogleApiClient.Builder(ctx)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            createLocationRequest();
        }
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Interval);
        mLocationRequest.setFastestInterval(FastInterval);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void onStart() {
        mGoogleApiClient.connect();

    }

    public void onStop() {
        mGoogleApiClient.disconnect();
    }

    public synchronized void setLL(double lati, double longi) {
        this.lati = lati;
        this.longi = longi;
        Log.d(mTAG, "set_" + lati + ":" + longi);
    }

    public synchronized String getLL() {
        return lati + ":" + longi;
    }

    public void starLocation() {
        if (ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        setLL(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        starLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(mTAG,"Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(mTAG,connectionResult.toString());
        Log.d(mTAG,"Connection Failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        setLL(location.getLatitude(),location.getLongitude());
        Log.d(mTAG,"LocationChanged! "+location.getLatitude()+"_"+location.getLongitude());
    }

}
