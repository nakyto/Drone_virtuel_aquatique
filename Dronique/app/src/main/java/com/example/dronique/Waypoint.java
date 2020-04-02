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
        //Initialisation des variables
        String drone_latitude = "0"; //latitude en format DDM
        String orientation_latitude = "N"; //Si N (North) alors latitude positive, si S (South) alors latitude négative
        String drone_longitude = "0"; //longitude en format DDM
        String orientation_longitude = "E"; //Si E (East) alors longitude positive, si W (West) alors longitude négative
        float drone_vitesse = 0; //vitesse

        String[] gprmc = line.split(","); //On transforme la trame NMEA en un tableau de String
        //On affecte les valeurs du tableau aux variables
        drone_latitude = gprmc[3]; 
        orientation_latitude = gprmc[4];
        drone_longitude = gprmc[5];
        orientation_longitude = gprmc[6];
        drone_vitesse = Float.parseFloat(gprmc[7]);

        
        int index_point_latitude = drone_latitude.indexOf("."); //On repère où se trouve le point dans la latitude
        float DM_latitude = Float.parseFloat(drone_latitude.substring(index_point_latitude-2)); //Tout se qui se trouve après le point correspond aux Decimal Minutes de la latitude
        int Degre_latitude = Integer.parseInt(drone_latitude.substring(0, index_point_latitude-2)); //Tout se qui se trouve avant le point correspond au Degree de la latitude
        float latitude = Degre_latitude + (DM_latitude/60) ; //Conversion du format DDM en format DD

        if(orientation_latitude.contains("S")){ //Si N (North) alors on laisse la latitude positive, si S (South) alors latitude négative
            latitude = latitude * -1;
        }

        int index_point_longitude = drone_longitude.indexOf("."); //On repère où se trouve le point dans la longitude
        float DM_longitude = Float.parseFloat(drone_longitude.substring(index_point_longitude-2)); //Tout se qui se trouve après le point correspond aux Decimal Minutes de la longitude
        int Degre_longitude = Integer.parseInt(drone_longitude.substring(0, index_point_longitude-2)); //Tout se qui se trouve avant le point correspond au Degree de la longitude
        float longitude = Degre_longitude + (DM_longitude/60) ; //Conversion du format DDM en format DD

        if(orientation_longitude.contains("W")){ //Si E (East) alors on laisse la longitude positive, si W (West) alors longitude négative
            longitude = longitude * -1;
        }

        return new Waypoint(latitude, longitude, drone_vitesse); //On retourne le Waypoint
    }
}
