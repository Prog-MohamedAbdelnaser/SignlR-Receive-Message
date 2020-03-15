package com.tabeesgl;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.tabeesgl.background.service.SignalRService;

public class SplashScreen extends AppCompatActivity {
    public static final int MY_PERMISSIONS_REQUEST_READ_FINE_LOCATION = 100;
    private static final int MY_PERMISSIONS_REQUEST_REBOOT = 1010;
    boolean  ACCESS_REBOOT=false;
    boolean ACCESS_FINE=false;
    boolean ACCESS_CROSE=false;
    private boolean isConnectedGps;
    ComponentName mAdminComponentName;
    DevicePolicyManager mDevicePolicyManager;
    LocationManager locationManager;
    Handler handler=new Handler(Looper.myLooper());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_splash_screen );
        Intent intent=new Intent(this,SignalRService.class);
        startService(intent);
    }




}
