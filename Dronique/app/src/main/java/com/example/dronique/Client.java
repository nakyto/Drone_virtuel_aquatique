package com.example.dronique;

import android.os.AsyncTask;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Client extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... strings) {
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
                    System.out.println("Server: " + responseLine);
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

    protected void onPostExecute() {

    }


}