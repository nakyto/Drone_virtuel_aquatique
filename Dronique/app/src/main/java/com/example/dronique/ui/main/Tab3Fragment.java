package com.example.dronique.ui.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.dronique.Drone;
import com.example.dronique.MainActivity;
import com.example.dronique.R;
import com.example.dronique.Server;
import com.example.dronique.Waypoint;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Tab3Fragment extends Fragment {

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private Drone mDrone;
    private SeekBar mSeekBar;
    private Button mButton;
    private Polyline mPolyline = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_three, container, false);

        // Gestion du drone
        mDrone = new Drone();

        // Gestion de la MapView
        mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try{
            MapsInitializer.initialize(getActivity().getApplicationContext());
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                //Écouteur de la carte
                mGoogleMap.setOnMapClickListener(
                        new GoogleMap.OnMapClickListener(){
                            //Ajoute un Marker après un click à un endroit de la carte
                            @Override
                            public void onMapClick(LatLng pos){
                                TextView textView = view.findViewById(R.id.speedView);

                                //Définition des coordoonées et de la vitesse du point
                                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                                                            .position(pos)
                                                            .title((String) textView.getText() + " noeuds")
                                                            .snippet("Cliquez pour supprimer"));
                                double speed = Double.parseDouble(textView.getText().toString());
                                mDrone.getWaypoint().addToWaypointHistory(pos.latitude, pos.longitude, speed); //Sauvegarde du point
                                marker.showInfoWindow();
                            }
                        }
                );
                //Écouteur de l'infobulle
                mGoogleMap.setOnInfoWindowClickListener(
                        new GoogleMap.OnInfoWindowClickListener() {
                            // Suppression d'un waypoint
                            @Override
                            public void onInfoWindowClick(Marker marker) {
                                marker.remove();
                                mDrone.getWaypoint().removeFromWaypointHistory(marker.getPosition().latitude, marker.getPosition().longitude); //Suppression des données stockées
                            }
                        }
                );

                //Définition de la position de la caméra au lancement de la vue
                LatLng posLaRochelle = new LatLng(46.1558,-1.1532);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(posLaRochelle).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        //Gestion de la SeekBar pour définir la vitesse
        mSeekBar = (SeekBar) view.findViewById(R.id.speedBar);
        mSeekBar.setMax(60);
        mSeekBar.setProgress(15);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            // Modifie la vitesse affichée dans le textView
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    TextView textView = view.findViewById(R.id.speedView);
                    textView.setText(String.valueOf(progress)); //Affichage de la vitesse
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //Envoi de la trame et tracer du chemin
        mButton=view.findViewById(R.id.button_envoyer);
        mButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                //récupération des points sous la forme LatLng
                ArrayList<LatLng> arrayList = new ArrayList<>();
                List<Waypoint> list = mDrone.getWaypoint().getWaypointHistory();
                for(int i=0; i<list.size();i++){
                    arrayList.add(list.get(i).getLatLng());
                }

                //suppression du tracé précédent (s'il existe)
                if (mPolyline!=null){
                    mPolyline.remove();
                }

                //tracé de la trajectoire suivant les waypoints 
                mPolyline=mGoogleMap.addPolyline(new PolylineOptions()
                        .addAll(arrayList)
                        .width(7)
                        .color(Color.RED));

                // Envoi des trames
                Server serv = new Server(mDrone);
                serv.execute();

                //Création du fichier de test JSON
                JSONObject fichier = new JSONObject();

                try {
                    fichier.put("name","Trajectoire");
                    fichier.put("desc","Destiné à être utilisé pour vérifier que tout fonctionne");
                    JSONArray waypointsListArray = new JSONArray();
                    for (Waypoint waypoint : mDrone.getWaypoint().getWaypointHistory()){
                        JSONObject waypointJson = new JSONObject();
                        waypointJson.put("lat",waypoint.getDroneLat());
                        waypointJson.put("lng",waypoint.getDroneLng());
                        waypointJson.put("speed",waypoint.getDroneSpeed());
                        waypointsListArray.put(waypointJson);
                    }
                    fichier.put("waypoints",waypointsListArray);

                    File file = getContext().getFilesDir();
                    FileWriter fileWriter = new FileWriter(file.getPath() + "/trajectoire.json");
                    fileWriter.write(fichier.toString(2));
                    fileWriter.flush();
                    fileWriter.close();
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

            }
        });

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
