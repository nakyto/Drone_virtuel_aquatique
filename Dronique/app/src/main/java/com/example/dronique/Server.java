package com.example.dronique;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends AsyncTask<String, Void, Void> {

    private Drone mDrone;

    public Server(Drone drone)
    {
        mDrone = drone;
    }

    @Override
    protected Void doInBackground(String... strings) {

        ServerSocket serverSock = null;
        Socket sock = null;
        OutputStream os = null;
        InputStream is = null;

        try{
            serverSock = new ServerSocket(8080);
            sock = serverSock.accept();
            os = sock.getOutputStream();
            is = sock.getInputStream();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

        if(serverSock != null && os != null && is != null){
            try{
                PrintWriter writer = new PrintWriter(os, true);
                for (Waypoint waypoint : mDrone.getWaypoint().getWaypointHistory())
                {
                    writer.println("$GPGLL " +
                            waypoint.getDroneLng() + " " +
                            waypoint.getDroneLat()  + " " +
                            waypoint.getDroneSpeed() + "\n");
                }
                /*
                writer.close();
                os.close();
                is.close();
                sock.close();
                serverSock.close();
                */
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    protected void onPostExecute() {

    }


}