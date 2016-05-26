package com.example.giovanny.choferburra;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by giovanny on 26/05/16.
 */
public class Localization implements LocationListener {
    private LocationManager locationManager;
    Context ctx;
    String posicion;
    public Localization(Context ctx) {
        this.ctx = ctx;
        locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }

        if(locationManager==null){
            Log.d("localizacion","ERROR DE NULL");
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000,   // 1 sec
                5, this);


        posicion="0:-12.04:-77.03";
        Log.d("localizacion","Recien lanzo! ");
    }


    @Override
    public void onLocationChanged(Location location) {
        posicion=location.getLatitude()+":"+location.getLongitude();
        Log.d("localizacion",posicion);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude",status+":");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(ctx, "Gps turned on ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(ctx, "Gps turned off", Toast.LENGTH_LONG).show();
    }
}
