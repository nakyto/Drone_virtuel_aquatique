package com.example.dronique;

import com.google.android.gms.maps.model.LatLng;

import java.util.Observable;

public class Drone extends Observable {
    private Waypoint mWaypoint;

    public void updatePosition(Waypoint waypoint){
        mWaypoint = waypoint;
    }

    public LatLng getPosition(){
        if(mWaypoint != null)
            return new LatLng(mWaypoint.getDroneLat(), mWaypoint.getDroneLng());
        return null;
    }

    public Double getSpeed() {
        if(mWaypoint != null)
            return mWaypoint.getDroneSpeed();
        return null;
    }
}
