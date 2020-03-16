package com.example.mapb_2;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {
    public GoogleMap mMap;
    private MarkerOptions place1, place2;
    Button getDirection;
    private Polyline currentPolyline;
    String source, destination;
    PlacesClient placesClient;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        source = "";
        destination = "";
        getDirection = findViewById(R.id.btn);

        getDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (source.matches("") && destination.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter both the locations", Toast.LENGTH_LONG).show();
                } else if (source.matches("") && !destination.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter the source location", Toast.LENGTH_LONG).show();
                } else if (!source.matches("") && destination.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter the destination location", Toast.LENGTH_LONG).show();
                } else {
                    mMap.clear();
                    place1 = new MarkerOptions().position(searchLocation(source)).title("Source");
                    place2 = new MarkerOptions().position(searchLocation(destination)).title("Destination");
                    new FetchURL(MapsActivity.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
                }
            }
        });


        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String apiKey = "AIzaSyAgO7JJG2V4YduaZ9vC_TjEaUtE1tyuzQ8";

        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(MapsActivity.this, apiKey);
        }
        // Retrieve a PlacesClient (previously initialized - see MainActivity)
        placesClient = Places.createClient(this);

        final AutocompleteSupportFragment autocompleteSupportFragment_1 =
                (AutocompleteSupportFragment)
                        getSupportFragmentManager().findFragmentById(R.id.et1);

        final AutocompleteSupportFragment autocompleteSupportFragment_2 =
                (AutocompleteSupportFragment)
                        getSupportFragmentManager().findFragmentById(R.id.et2);

        autocompleteSupportFragment_1.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));
        autocompleteSupportFragment_2.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));

        autocompleteSupportFragment_1.setOnPlaceSelectedListener(
                new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {
                        final LatLng latLng = place.getLatLng();
                        System.out.println("Source: " + place.getName());
                        source = place.getName();
                        Toast.makeText(MapsActivity.this, place.getAddress(), Toast.LENGTH_SHORT).show();
                        Log.d("auto suggestions 1", "" + latLng.latitude + "\n" + latLng.longitude);
                        System.out.println("auto suggestions 1: " + latLng.latitude + "\n" + latLng.longitude);
                    }

                    @Override
                    public void onError(Status status) {
                        Toast.makeText(MapsActivity.this, "" + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        autocompleteSupportFragment_2.setOnPlaceSelectedListener(
                new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {
                        final LatLng latLng = place.getLatLng();
                        System.out.println("Destination: " + place.getName());
                        destination = place.getName();
                        Toast.makeText(MapsActivity.this, "" + place.getAddress(), Toast.LENGTH_SHORT).show();
                        Log.d("auto suggestions 2", "" + latLng.latitude + "\n" + latLng.longitude);
                        System.out.println("auto suggestions 2: " + latLng.latitude + "\n" + latLng.longitude);
                    }

                    @Override
                    public void onError(Status status) {
                        Toast.makeText(MapsActivity.this, "" + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
//        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompatf)
        //mMap.setMyLocationEnabled(true);
        Log.d("mylog", "Added Markers");

        //mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//        Toast.makeText(getApplicationContext(),"i am in maps",Toast.LENGTH_LONG).show();
//        mMap.addMarker(place1);
//        mMap.addMarker(place2);

    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        Log.d("shrad", "latlang called");
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null) {
            currentPolyline.remove();
        }
        Log.d("shrad", "yes printed");
//        Toast.makeText(getApplicationContext(), "in Ontask done", Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), "Route", Toast.LENGTH_SHORT).show();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
//        TextView txtView = (TextView)findViewById(R.id.text);
//        txtView.setText((Integer) values[1]);
    }


    public LatLng searchLocation(String location) {
        // EditText locationSearch = (EditText) findViewById(R.id.editText);

        List<Address> addressList = null;
        Log.d("_hela", location);
        Log.d("_hela", "entered search location");
        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            Log.d("_hela", "just above marker statement");
            Log.d("_hela", String.valueOf(address.getLatitude()));
            mMap.addMarker(new MarkerOptions().position(latLng).title(location));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            // Toast.makeText(getApplicationContext(),address.getLatitude()+" "+address.getLongitude(),Toast.LENGTH_LONG).show();
            Log.d("_hela", "about to return latLang ");
            return latLng;

        }
        Log.d("_hela", "about to return null");
        return null;
    }
}