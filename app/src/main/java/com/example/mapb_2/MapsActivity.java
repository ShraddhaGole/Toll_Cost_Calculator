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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {
    public GoogleMap mMap;
    private MarkerOptions place1, place2;
    Button getDirection;
    private Polyline currentPolyline;
    EditText et1, et2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getDirection = findViewById(R.id.btn);
        et1 = findViewById(R.id.et1);
        et2 = findViewById(R.id.et2);
        TextView txtView = (TextView) findViewById(R.id.text);
        //TextView tv=(TextView)findViewById(R.id.txt);

        getDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et1.getText().toString().matches("") || et2.getText().toString().matches("")) {
                    Toast.makeText(getApplicationContext(), "please enter the locations", Toast.LENGTH_LONG).show();
                } else {
                    mMap.clear();
                    place1 = new MarkerOptions().position(searchLocation(et1)).title("Location 1");
                    place2 = new MarkerOptions().position(searchLocation(et2)).title("Location 2");
                    new FetchURL(MapsActivity.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
                }
            }
        });
        //27.658143,85.3199503
        //27.667491,85.3208583
//        place1 = new MarkerOptions().position(searchLocation(et1)).title("Location 1");
//        place2 = new MarkerOptions().position(searchLocation(et2)).title("Location 2");
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        Toast.makeText(getApplicationContext(), "in Ontask done", Toast.LENGTH_LONG).show();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
//        TextView txtView = (TextView)findViewById(R.id.text);
//        txtView.setText((Integer) values[1]);
    }


    public LatLng searchLocation(EditText locationSearch) {
        // EditText locationSearch = (EditText) findViewById(R.id.editText);

        String location = locationSearch.getText().toString();
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