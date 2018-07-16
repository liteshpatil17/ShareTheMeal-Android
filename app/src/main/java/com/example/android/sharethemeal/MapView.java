package com.example.android.sharethemeal;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapView extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public static Context con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        con=this;
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
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13));
        for(int i=0;i<Data_holder.Homeless_list.size();i++)
        {
            LatLng latlong=new LatLng(Data_holder.Homeless_list.get(i).loc_data.latitude,Data_holder.Homeless_list.get(i).loc_data.longitude);
            Marker mymarker=mMap.addMarker(new MarkerOptions().position(latlong).title(Data_holder.Homeless_list.get(i).name));
            mymarker.setTag(i);
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int i= (int) marker.getTag();
                Intent intent=new Intent(MapView.this,ChildViewOnMap.class);
                intent.putExtra("index",i);
                startActivity(intent);

                return false;
            }
        });

    }
}
