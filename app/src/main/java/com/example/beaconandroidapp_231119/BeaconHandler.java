package com.example.beaconandroidapp_231119;

import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

public class BeaconHandler implements RangeNotifier, MonitorNotifier {
    protected static final String TAG = "BeaconHandler";
    private final Identifier uuid = Identifier.parse("00000000-9BD0-1001-B000-001C4D778244");
    private final Identifier major = Identifier.parse("1");
    private final Identifier minor = Identifier.parse("2");
    private final Region mRegion = new Region("B02", uuid, major, minor);

    BeaconHandler(BeaconManager bm){
        bm.addMonitorNotifier(this);
        bm.addRangeNotifier(this);
        bm.startMonitoring(mRegion);
        Log.i(TAG, "start monitoring");
        bm.startRangingBeacons(mRegion);
        Log.i(TAG, "start ranging");
        bm.getBeaconParsers().clear();
        bm.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        bm.setDebug(true);
    }

    @Override
    public void didEnterRegion(Region region) {
        Log.d(TAG, "didEnterRegion");
    }

    @Override
    public void didExitRegion(Region region) {
        Log.d(TAG, "didExitRegion");
    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {
        Log.d(TAG, "didDetermineStateForRegion: "+ state);
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        Log.d(TAG, "didRangeBeaconsInRegion");
        Log.i(TAG, "The detected beacon count: " + beacons.size());
        if (beacons.size() > 0) {
            for (Beacon beacon : beacons) {
                Log.i(TAG, "UUID: " + beacon.getId1() + ", major: " + beacon.getId2() + ", minor: " + beacon.getId3() + ", RSSI: " + beacon.getRssi() + ", TxPower: " + beacon.getTxPower() + ", Distance: " + beacon.getDistance());
            }
        }
    }
}
