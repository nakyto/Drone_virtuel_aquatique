package com.example.dronique;

import com.example.dronique.Frame;
import com.example.dronique.ui.main.Tab1Fragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.Observable;

public class Drone extends Observable {
    private Frame mFrame;

    public void updatePosition(Frame frame){
        mFrame = frame;
    }

    public LatLng getPosition(){
        if(mFrame != null)
            return new LatLng(mFrame.getDroneLat(), mFrame.getDroneLng());
        return null;
    }

    public Double getSpeed() {
        if(mFrame != null)
            return mFrame.getDroneSpeed();
        return null;
    }
}
