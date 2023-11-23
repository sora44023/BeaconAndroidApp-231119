package com.example.beaconandroidapp_231119;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BeaconViewModel extends ViewModel {
    private MutableLiveData<Boolean> beaconStatus;
    private MutableLiveData<Double> beaconDistance;

    public MutableLiveData<Boolean> getStatus(){
        if(beaconStatus == null){
            beaconStatus  = new MutableLiveData<>();
        }
        return beaconStatus;
    }

    public MutableLiveData<Double> getDistance(){
        if(beaconDistance == null){
            beaconDistance = new MutableLiveData<>();
        }
        return beaconDistance;
    }
}
