package com.example.dronique;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.example.dronique.ui.main.Tab1Fragment;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import static com.example.dronique.Frame.Parse;

public class Client extends AsyncTask<Void, String, Void> {

    private Drone mDrone;
    private Tab1Fragment mTab1;

    public Client(Drone drone, Tab1Fragment tab1){
        mDrone = drone;
        mTab1 = tab1;
    }

    @Override
    protected void onProgressUpdate(String... responseLine){
        mDrone.updatePosition(Parse(responseLine[0]));
        mTab1.update();
    }

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
                while((responseLine = is.readLine()) != null){
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