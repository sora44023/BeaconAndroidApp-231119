package com.example.beaconandroidapp_231119;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;

import org.altbeacon.beacon.BeaconManager;

import java.math.BigDecimal;

import javax.xml.validation.Validator;

public class MainActivity extends AppCompatActivity {

    protected static final String TAG = "MainActivity";
    private BeaconManager beaconManager;
    private BeaconViewModel model;
    private TextView distanceView;
    private Vibrator vibrator;

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        Log.d(TAG, "App started up");

        // vibration
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        VibrationEffect ve2 = VibrationEffect.createWaveform(new long[]{300, 300,300, 300}, -1);

        // Android M Permission check
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect beacons.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
            builder.show();
        }
        beaconManager = BeaconManager.getInstanceForApplication(this);
        model = new ViewModelProvider(this).get(BeaconViewModel.class);
        BeaconHandler beaconHandler = new BeaconHandler(beaconManager, model);
        distanceView = findViewById(R.id.distanceView);
        final Observer<Boolean> statusObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                updateStatus();
                if(!model.getStatus().getValue()){
                    vibrator.vibrate(ve2);
                }
            }
        };
        final Observer<Double> distanceObserver = new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                updateStatus();
            }
        };
        model.getStatus().observe(this,statusObserver);
        model.getDistance().observe(this,distanceObserver);

    }

    private void updateStatus(){
        Log.d(TAG, "update status!");
        String s = "?";
        if(model.getStatus().getValue() != null && model.getDistance().getValue() != null) {
            if (model.getStatus().getValue()) {
                s = BigDecimal.valueOf(model.getDistance().getValue()).toPlainString();
            } else {
                s = "?";
            }
        }
        distanceView.setText(s);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    Log.d(TAG, "coarse location permission denied");
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }
                    });
                    builder.show();
                }
            }
        }
    }
}