package com.example.dronique;

import java.util.ArrayList;
import java.util.List;

public class Waypoint {
    private List<Waypoint> mWaypointHistory = null;

    private double mDroneLat = 0.0;
    private double mDroneLng = 0.0;
    private String orientation_latitude = "N";
    private String orientation_longitude = "E";
    private double mDroneSpeed = 0;

    public Waypoint(double droneLat, double droneLng, double droneSpeed){
        mDroneLat = droneLat;
        mDroneLng = droneLng;
        mDroneSpeed = droneSpeed;
    }

    public Waypoint(){
        mWaypointHistory = new ArrayList<>();
    }

    public double getDroneLat(){
        return mDroneLat;
    }

    public double getDroneLng(){
        return mDroneLng;
    }

    public double getDroneSpeed(){
        return mDroneSpeed;
    }


    // Gestion de l'historique
    public List<Waypoint> getWaypointHistory() {
        return getWaypointHistory();
    }

    public void addToWaypointHistory(double droneLat, double droneLng, double droneSpeed) {
        this.mWaypointHistory.add(new Waypoint(droneLat, droneLng, droneSpeed));
        mDroneLat = droneLat;
        mDroneLng = droneLng;
        mDroneSpeed = droneSpeed;
    }

    public void removeWaypointHistory(double droneLat, double droneLng) {
        for (Waypoint waypoint: this.mWaypointHistory) {
            if(waypoint.mDroneLat == droneLat && waypoint.mDroneLng == droneLng) {
                mWaypointHistory.remove(waypoint);
            }
        }
    }


    // Gestion des trames
    public String parseToFrame(){

        return "toto";
    }

    public static Waypoint ParseFromFrame(String line){

        String drone_latitude = "0";
        String orientation_latitude = "N";
        String drone_longitude = "0";
        String orientation_longitude = "E";
        float drone_vitesse = 0;
        String identifiant = "";

        String[] gprmc = line.split(",");
        drone_latitude = gprmc[3];
        orientation_latitude = gprmc[4];
        drone_longitude = gprmc[5];
        orientation_longitude = gprmc[6];
        drone_vitesse = Float.parseFloat(gprmc[7]);

        int index_point_lagitude = drone_latitude.indexOf(".");
        float MM_latitude = Float.parseFloat(drone_latitude.substring(index_point_lagitude-2));
        int Degre_latitude = Integer.parseInt(drone_latitude.substring(0, index_point_lagitude-2));
        float latitude = Degre_latitude + (MM_latitude/60) ;

        if(orientation_latitude.contains("S")){
            latitude = latitude * -1;
        }

        int index_point_longitude = drone_longitude.indexOf(".");
        float MM_longitude = Float.parseFloat(drone_longitude.substring(index_point_longitude-2));
        int Degre_longitude = Integer.parseInt(drone_longitude.substring(0, index_point_longitude-2));
        float longitude = Degre_longitude + (MM_longitude/60) ;

        if(orientation_longitude.contains("W")){
            longitude = longitude * -1;
        }

        return new Waypoint(latitude, longitude, drone_vitesse);
    }
}
