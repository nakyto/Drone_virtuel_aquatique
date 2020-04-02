package com.example.dronique;

import android.os.AsyncTask;

import com.example.dronique.ui.main.Tab1Fragment;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import static com.example.dronique.Waypoint.Parse;

public class Client extends AsyncTask<Void, String, Void> {

    private Drone mDrone;
    private Tab1Fragment mTab1;

    Socket mSock = null;
    DataOutputStream mOS = null;
    DataInputStream mIS = null;

    public Client(Drone drone, Tab1Fragment tab1){
        mDrone = drone;
        mTab1 = tab1;
    }

    @Override
    protected void onProgressUpdate(String... responseLine){
        if(mDrone != null) {
            mDrone.updatePosition(Parse(responseLine[0]));
            mTab1.update();
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try{
            mSock = new Socket("192.168.1.16", 55555);
            System.out.println("le socket est créé");
            mOS = new DataOutputStream(mSock.getOutputStream());
            mIS = new DataInputStream(mSock.getInputStream());
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        if(mSock != null && mOS != null && mIS != null){
            try{
                String responseLine = null;
                while(((responseLine = mIS.readLine()) != null) && !isCancelled()){
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
                mOS.close();
                mIS.close();
                mSock.close();
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

}