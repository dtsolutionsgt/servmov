package com.dts.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import com.dts.fbase.fbLocItem;
import com.dts.base.clsClasses;

import org.jetbrains.annotations.Nullable;

import java.util.Timer;
import java.util.TimerTask;


public class GPSService extends Service implements LocationListener{

    LocationManager locationManager;
    Location location;

    private Handler mHandler = new Handler();
    private Timer mTimer = null;

    // Read from SharedPreferences
    SharedPreferences sharedPreferences;

    Intent intent;

    fbLocItem fbl;

    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double  latitude,longitude;
    int cnt;

    long notify_interval = 5000;
    public static String str_receiver = "serviciomovil.gpsservice.receiver";

    public GPSService() {}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            fbl= new fbLocItem("");
        } catch (Exception e) {}

        try {
            mTimer = new Timer();
            mTimer.schedule(new TimerTaskToGetLocation(),5,notify_interval);
            intent = new Intent(str_receiver);
        } catch (Exception e) {}

        try {
            sharedPreferences = getSharedPreferences("GPSService", Context.MODE_PRIVATE);

        } catch (Exception e) {}

        // Read from SharedPreferences


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    @SuppressLint("MissingPermission")
    private void fn_getlocation(){
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isGPSEnable | isNetworkEnable){

            if (isNetworkEnable){
                location = null;
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,0,this);
                if (locationManager!=null){
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location!=null){
                        latitude = location.getLatitude();longitude = location.getLongitude();
                        fn_update(location);
                    }
                }
            }

            if (isGPSEnable) {
                location = null;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,this);
                if (locationManager!=null){
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location!=null){
                        latitude = location.getLatitude();longitude = location.getLongitude();
                        fn_update(location);
                    }
                }
            }

        }

    }

    private class TimerTaskToGetLocation extends TimerTask{
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    fn_getlocation();
                }
            });
        }
    }

    private void fn_update(Location location){
        try {
            cnt++;
            String root = sharedPreferences.getString("root", "locusr/1/121/");

            clsClasses.clsLocItem litem=new clsClasses.clsLocItem(100,1000000L,
                         location.getLongitude(),location.getLatitude(),cnt);
            fbl.setItem(root,litem);

            //fbl.setItem("locusr/1/10/",litem);
        } catch (Exception e) {}

        try {
            intent.putExtra("latutide",location.getLatitude()+"");
            intent.putExtra("longitude",location.getLongitude()+"");
            sendBroadcast(intent);
        } catch (Exception e) {}

    }


}

