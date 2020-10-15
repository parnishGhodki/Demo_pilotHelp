package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public  class getLocation extends Service {



    LocationManager locationManager;
    LocationListener locationListener;
    ArrayList<Location> gps = new ArrayList<Location>();
    ArrayList<Long> speed = new ArrayList<Long>();
    ArrayList<Double> Distance = new ArrayList<Double>();
    ArrayList<Long> Time = new ArrayList<Long>();
    Location previousLocation ;
    long currentTime = 0;
    long lastUpdatedTime = 0;
    double distance = 0;
    long time = 0;
    long Speed = 0;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                gps.add(location);
                currentTime = System.currentTimeMillis();

                if(gps.size() > 1){

                    distance = calculateDistance(previousLocation,location);
                    time = currentTime - lastUpdatedTime;
                    Distance.add(distance);
                    Time.add(time);
                    Speed = (long) (distance*100)/time;
                    speed.add(Speed);
                }
                previousLocation = location;
                lastUpdatedTime = currentTime;

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

            }
        };
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getLocation.this, String.valueOf(gps.size()), Toast.LENGTH_SHORT).show();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);


        }
        xyz();
    }

    public void xyz() {

        Intent i = new Intent(getApplicationContext(), calculate.class);

        Bundle args = new Bundle();
        args.putSerializable("gps", gps);
        args.putSerializable("speed",speed);
        args.putSerializable("distance",Distance);
        args.putSerializable("time",Time);
        i.putExtra("Bundle", args);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        gps.clear();
        speed.clear();
    }

    public double calculateDistance(Location initialLocation,Location finalLocation){

        final int R = 6371;

        double latDistance = Math.toRadians(finalLocation.getLatitude() - initialLocation.getLatitude());
        double lonDistance = Math.toRadians(finalLocation.getLongitude() - initialLocation.getLongitude());
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(initialLocation.getLatitude())) * Math.cos(Math.toRadians(finalLocation.getLatitude()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = (double) (R * c * 1000);


        return distance;
    }




}

