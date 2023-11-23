package com.example.beaconandroidapp_231119;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BeaconViewModel extends ViewModel {
    private MutableLiveData<Boolean> beaconStatus;

    public MutableLiveData<Boolean> getStatus(){
        if(beaconStatus == null){
            beaconStatus  = new MutableLiveData<>();
        }
        return beaconStatus;
    }
}
