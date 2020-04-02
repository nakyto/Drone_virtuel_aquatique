package com.example.dronique;

import android.os.AsyncTask;

import com.example.dronique.ui.main.Tab1Fragment;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import static com.example.dronique.Waypoint.ParseFromFrame;

/**
 * Client tcp pour se connecter au NMEA Simulator
 */
public class Client extends AsyncTask<Void, String, Void> {

    private Drone mDrone;
    private Tab1Fragment mTab1;

    /**
     * Constructeur du  Client
     * @param drone
     * @param tab1
     */
    public Client(Drone drone, Tab1Fragment tab1){
        mDrone = drone;
        mTab1 = tab1;
    }

    /**
     * Réception de la progession du doInBackground
     * @param responseLine
     */
    @Override
    protected void onProgressUpdate(String... responseLine){
        if(mDrone != null) {
            mDrone.updatePosition(ParseFromFrame(responseLine[0]));
            mTab1.update();
        }
    }

    /**
     * Tâche asynchrone du client
     * @param voids
     * @return Void
     */
    @Override
    protected Void doInBackground(Void... voids) {
        Socket sock = null;
        DataOutputStream os = null;
        DataInputStream is = null;

        try{
            sock = new Socket("192.168.1.16", 55555);
            System.out.println("le socket est créé");
            os = new DataOutputStream(sock.getOutputStream());
            is = new DataInputStream(sock.getInputStream());
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        if(sock != null && os != null && is != null){
            try{
                String responseLine = null;
                while(((responseLine = is.readLine()) != null) && !isCancelled()){
                    String id = "";
                    id += responseLine.charAt(1);
                    id += responseLine.charAt(2);
                    id += responseLine.charAt(3);
                    id += responseLine.charAt(4);
                    id += responseLine.charAt(5);

                    if (id.contains("GPRMC")){
                        publishProgress(responseLine);
                    }
                }
                os.close();
                is.close();
                sock.close();
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        }
        return null;
    }
}