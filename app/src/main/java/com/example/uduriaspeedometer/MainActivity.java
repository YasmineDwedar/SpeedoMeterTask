package com.example.uduriaspeedometer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {
    private int PERMISSION_ID = 1;
    private double LONGITUDE;
    private double LATITUDE;
    private TextView speed, TenThirty, ThirtyTen;
    private FusedLocationProviderClient mFusedLocationClient;
    private float changedSpeed;
    private long firstTime = 0;
    private long secondTime = 0;
    private boolean flag10Inc = true;
    private boolean flag30Inc = true;
    private boolean flag30Dec = true;
    private boolean Flag10Dec = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        speed = findViewById(R.id.id_speed);
        TenThirty = findViewById(R.id.id_10_30);
        ThirtyTen = findViewById(R.id.id_30_10);
        //This  will tell us whether or not the user grant us to access ACCESS_COARSE_LOCATION and ACCESS_FINE_LOCATION.
        if (checkPermissions()) {
            requestNewLocationData();
            toast("Permissions Are Granted");

        } else {
            toast("Permissions Are Not Granted ");
            requestPermissions();
        }
    }

    /**
     * This method will tell us whether or not the user grant us to access ACCESS_COARSE_LOCATION and ACCESS_FINE_LOCATION.
     */
    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    /**
     * This method will request our necessary permissions to the user if they are not already granted.
     */
    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    /**
     * This method is called when a user Allow or Deny our requested permissions. So it will help us to move forward if the permissions are granted.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {


            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Granted. Start getting the location information
            }
        }
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );
        toast("Long  =   " + LONGITUDE + "  Lat = " + LATITUDE);


    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();

            LONGITUDE = mLastLocation.getLongitude();
            LATITUDE = mLastLocation.getLatitude();
            speed.setText(0.0 + " km/h");
            changedSpeed = mLastLocation.getSpeed() * 3.6f;
            speed.setText(String.format("%.2f", changedSpeed) + " km/h");

            if (changedSpeed >= 10 && flag10Inc) {
                firstTime = mLastLocation.getTime() / 1000;
                flag10Inc = false;
            }
            if (changedSpeed >= 30 && flag30Inc) {
                secondTime = mLastLocation.getTime() / 1000;
                flag30Inc = false;
                float subtractedTime = secondTime - firstTime;
                TenThirty.setText((subtractedTime) + " s");
                flag30Dec = true;
                Flag10Dec = true;
            }
            if (changedSpeed <= 30 && flag30Dec) {
                firstTime = mLastLocation.getTime() / 1000;
                flag30Dec = false;
            }
            if (changedSpeed <= 10 && Flag10Dec) {
                secondTime = mLastLocation.getTime() / 1000;
                Flag10Dec = false;
                float subtractedTime = secondTime - firstTime;
                ThirtyTen.setText((subtractedTime) + " s");
                flag30Inc = true;
                flag10Inc = true;
            }


            toast("Long =   " + String.format("%.5f", LONGITUDE) + " , Lat = " + String.format("%.5f", LATITUDE));
        }
    };

    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
