package com.example.android.sharethemeal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

public class google_map extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public Intent in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);
        in = getIntent();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        int permissionCheck = ContextCompat.checkSelfPermission(google_map.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (ContextCompat.checkSelfPermission(google_map.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(google_map.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1123);
        }

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }



        mMap.setMyLocationEnabled(true);
        LocationManager locmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location loc = locmanager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        LatLng latlng = new LatLng(loc.getLatitude(), loc.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 10));

        //Add Marker on clicked location
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions().position(latLng).title("Location of Homeless"));
                AlertDialog_to_proceed(latLng);
            }
        });

        //Remove marker on click
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.remove();
                return false;
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1123: {
// If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    LocationManager locmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Location loc = locmanager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    LatLng latlng=new LatLng(loc.getLatitude(),loc.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,18));


                } else {
                    Toast.makeText(this,"Permission Denied", Toast.LENGTH_SHORT);
                    finish();
                }
                return;
            }
        }
    }

    public void AlertDialog_to_proceed(final LatLng latLng)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        Location_Data locdata=new Location_Data();
                        locdata.latitude=latLng.latitude;
                        locdata.longitude=latLng.longitude;
                        Gson jason=new Gson();
                        String loc=jason.toJson(locdata);
                        Intent intent;
                        if(in.getStringExtra("type").equals("homeless"))
                            intent=new Intent(google_map.this,fill_child_details.class);
                        else
                            intent=new Intent(google_map.this,Food_Donation_details.class);

                        intent.putExtra("lat_lon_jason",loc);
                        startActivity(intent);
                        finish();
                        break;
                }
            }
        };
        builder.setMessage("Do you want to save this location ?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(google_map.this,Main_start.class));
        finish();
        super.onBackPressed();
    }
}
