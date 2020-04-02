package com.example.dronique;

public class Waypoint {
    private double mDroneLat = 0.0;
    private double mDroneLng = 0.0;
    private double mDroneSpeed = 0;

    //Constructeur
    public Waypoint(double droneLat, double droneLng, double droneSpeed){
        mDroneLat = droneLat;
        mDroneLng = droneLng;
        mDroneSpeed = droneSpeed;
    }
    //Getters
    public double getDroneLat(){
        return mDroneLat;
    }
    public double getDroneLng(){
        return mDroneLng;
    }
    public double getDroneSpeed(){
        return mDroneSpeed;
    }

    //TODO : implémenter la création de la Trame à partir des waypoints
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
