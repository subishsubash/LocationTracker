package com.example.subash.locationtracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;
import android.widget.Toast;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import java.io.IOException;
import java.util.List;
import java.util.Locale;



/**
 * Created by Subash on 1/8/2017.
 */
public class start_tracker extends Service   {

    public String phoneNo="0123456789";//provide your corresponding number
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 60000;//mention your updating intervals in millisecond
    private static final float LOCATION_DISTANCE = 0f;

    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;

        public LocationListener(String p)
        {
            mLastLocation = new Location(p);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            mLastLocation.set(location);
            sendActualLocation(mLastLocation);
        }

        @Override
        public void onProviderDisabled(String p) {}

        @Override
        public void onProviderEnabled(String p){}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }
    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };
    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Tracking Started", Toast.LENGTH_LONG).show();
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }
    @Override
    public void onCreate()
    {
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        }
        catch (java.lang.SecurityException ex) {}
        catch (IllegalArgumentException ex) {}
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {}
         catch (IllegalArgumentException ex) {}
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Toast.makeText(this, "Switching OFF Tracker", Toast.LENGTH_LONG).show();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    if(checkPermission()){
                    mLocationManager.removeUpdates(mLocationListeners[i]);}
                } catch (Exception ex) {}
            }
        }
    }

    private void initializeLocationManager() {

        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private boolean checkPermission(){
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED );
    }
    private void sendActualLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
        String m=" ";
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null) {
                Address fetchedAddress = addresses.get(0);
                StringBuilder strAddress = new StringBuilder();
                for (int i = 0; i < fetchedAddress.getMaxAddressLineIndex(); i++) {
                    strAddress.append(fetchedAddress.getAddressLine(i)).append("\n");
                }
                m=strAddress.toString();
            }
            else {
                m=" ";
            }

        }catch (IOException e) {
            e.printStackTrace();}
        String msg="Coordinates are :"+location.getLatitude() + " and " +location.getLongitude()+"   "+m;
        if(checkPermission()) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),
                    "SMS faild, please try again.", Toast.LENGTH_LONG).show();
        }
    }
}

