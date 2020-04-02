package com.example.dronique;

import com.google.android.gms.maps.model.LatLng;

import java.util.Observable;

/**
 * Structure de donnée représentant le drone
 */
public class Drone extends Observable {
    private Waypoint mWaypoint;


    /**
     * Met à jour la position du drone
     * @param waypoint
     */
    public void updatePosition(Waypoint waypoint){
        mWaypoint = waypoint;
    }


    /**
     * Retourne la position du drone
     * @return LatLng
     */
    public LatLng getPosition(){
        if(mWaypoint != null)
            return new LatLng(mWaypoint.getDroneLat(), mWaypoint.getDroneLng());
        return null;
    }


    /**
     * Retourne la vitesse du drone
     * @return double
     */
    public Double getSpeed() {
        if(mWaypoint != null)
            return mWaypoint.getDroneSpeed();
        return null;
    }
}
