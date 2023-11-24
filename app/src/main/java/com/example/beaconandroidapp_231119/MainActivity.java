package com.example.beaconandroidapp_231119;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.altbeacon.beacon.BeaconManager;

import java.math.BigDecimal;


public class MainActivity extends AppCompatActivity {

    protected static final String TAG = "MainActivity";
    private BeaconManager beaconManager;
    private BeaconViewModel model;
    private ActionBar actionBar;
    private TextView distanceView;
    private TextView nearView;
    private Vibrator vibrator;
    private final VibrationEffect ve = VibrationEffect.createWaveform(new long[]{300, 300, 300, 300}, -1);

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // action bar setup
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title);
        }
        Log.d(TAG, "App started up");

        // vibration
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

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

        // beacon
        beaconManager = BeaconManager.getInstanceForApplication(this);
        model = new ViewModelProvider(this).get(BeaconViewModel.class);
        BeaconHandler beaconHandler = new BeaconHandler(beaconManager, model);

        // view
        distanceView = findViewById(R.id.distanceView);
        nearView = findViewById(R.id.nearView);

        // observation
        final Observer<Boolean> statusObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                updateStatus();
                if (!model.getStatus().getValue()) {
                    notifyOnSignalLoss();
                }
            }
        };
        final Observer<Double> distanceObserver = new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                updateStatus();
                notifyNearby();
            }
        };
        model.getStatus().observe(this, statusObserver);
        model.getDistance().observe(this, distanceObserver);
    }

    private void notifyNearby() {
        if(model.getDistance().getValue() == null){
            return;
        }
        if(model.getDistance().getValue() <= 1.0){
            nearView.setText("非常に近いです");
        }else{
            nearView.setText("");
        }


    }

    private void notifyOnSignalLoss() {
        Log.d(TAG, "notifyOnSignalLoss");
        vibrator.vibrate(ve);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("忘れ物をしています");
        builder.setMessage("Beaconの信号を失いました");
        builder.setPositiveButton(android.R.string.ok, null);
        builder.create();
        builder.show();
    }

    private void updateStatus() {
        Log.d(TAG, "update status!");
        String s = "?";
        if (model.getStatus().getValue() == null || model.getDistance().getValue() == null) {
            return;
        }
        if (model.getStatus().getValue()) {
            s = BigDecimal.valueOf(model.getDistance().getValue()).toPlainString();
        }
        distanceView.setText(s);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.option_menu_about) {
            showAbout();
            return true;
        }
        return false;
    }

    private void showAbout() {
        View messageView = getLayoutInflater().inflate(R.layout.about, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_about);
        builder.setView(messageView);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.create();
        builder.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_COARSE_LOCATION) {
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