package com.example.subash.locationtracker;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;import android.widget.Button;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity  {

    Button b_track,b_untrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b_track=(Button)findViewById(R.id.b_track);
        b_untrack=(Button)findViewById(R.id.b_untrack);
        check_Request_Permissions();

    }


    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onStop() {
        super.onStop();

    }
    public void start_track(View v)
    {

        if(check_Request_Permissions()){
            startService(new Intent(getBaseContext(), start_tracker.class));
        }
        else{
            permissionsDenied();
        }
    }
    public void stop_track(View v)
    {
        stopService(new Intent(getBaseContext(), start_tracker.class));

    }
    public  void permissionsDenied(){
        Toast.makeText(getApplicationContext(), "We need those permission to track locations",
                Toast.LENGTH_LONG).show();
    }

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 99;
    private  boolean check_Request_Permissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.SEND_SMS);
        int locationPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.SEND_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

}
