package com.example.android.sharethemeal;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.sharethemeal.Utility.nunito_bold;
import static com.example.android.sharethemeal.Utility.nunito_reg;

public class ChildViewOnMap extends FragmentActivity implements OnMapReadyCallback, RoutingListener {

    private GoogleMap mMap;
    int index;
    Homeless curr_homeless;
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.route};
    TextView distance;
    TextView duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_details_map);
        polylines = new ArrayList<>();
        Utility.setStatusBar(getWindow(), getApplicationContext());
        TextView name = (TextView) findViewById(R.id.nameofhomeless);
        TextView age = (TextView) findViewById(R.id.ageofhomeless);
        TextView sex = (TextView) findViewById(R.id.sexofhomeless);
        distance = (TextView) findViewById(R.id.distanceFromSource);
        duration = (TextView) findViewById(R.id.durationToReach);
        Intent prev_intent = getIntent();
        index = prev_intent.getIntExtra("index", 0);

        curr_homeless = Data_holder.Homeless_list.get(index);
        String sname=curr_homeless.name;
        name.setText(sname.substring(0, 1).toUpperCase() + sname.substring(1).toLowerCase());
        name.setTypeface(nunito_bold);
        age.setTypeface(nunito_reg);
        sex.setTypeface(nunito_reg);
        age.setText(curr_homeless.age+" years");
        sex.setText(curr_homeless.gender);
        StorageReference storage = FirebaseStorage.getInstance().getReference();
        StorageReference s1 = storage.child("images/" + name.getText().toString().toLowerCase() + ".jpg");
        ImageView imageView = (ImageView) findViewById(R.id.pictureofhomeless);
        if (curr_homeless.pic_data_url == null || curr_homeless.pic_data_url.equals("nil") == false) {

            Glide.with(this).using(new FirebaseImageLoader()).load(s1).into(imageView);
        } else {
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        int permissionCheck = ContextCompat.checkSelfPermission(ChildViewOnMap.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (ContextCompat.checkSelfPermission(ChildViewOnMap.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ChildViewOnMap.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1123);
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        mMap.setMyLocationEnabled(true);
        LocationManager locmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location loc = locmanager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        LatLng currentLoc = new LatLng(loc.getLatitude(), loc.getLongitude());


        LatLng latlong = new LatLng(curr_homeless.loc_data.latitude, curr_homeless.loc_data.longitude);
        String sname=curr_homeless.name;
        sname=sname.substring(0, 1).toUpperCase() + sname.substring(1).toLowerCase();
        Marker mymarker = mMap.addMarker(new MarkerOptions().position(latlong).title(sname));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong, 13));
        mymarker.showInfoWindow();
        mymarker.setTag(index);

        getRouteToMarker(currentLoc, latlong);


    }

    private void getRouteToMarker(LatLng currentLoc, LatLng latlong) {

        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(currentLoc, latlong)
                .build();
        routing.execute();
    }


    @Override
    public void onRoutingFailure(RouteException e) {
        if (e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        //In case of more than 5 alternative routes
        int colorIndex = 0 % COLORS.length;

        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(getResources().getColor(COLORS[colorIndex]));
        polyOptions.width(10 + 0 * 3);
        polyOptions.addAll(route.get(0).getPoints());
        Polyline polyline = mMap.addPolyline(polyOptions);
        polylines.add(polyline);
        double dis = (route.get(0).getDistanceValue()) / 1000;
        int min = (route.get(0).getDurationValue()) / 60;
        int sec = (route.get(0).getDurationValue()) / 360;
        distance.setText(dis + " Km");
        duration.setText(min + " Min " + sec + " Sec");
        Toast.makeText(getApplicationContext(), "Route " + (0 + 1) + ": distance - " + route.get(0).getDistanceValue() + ": duration - " + route.get(0).getDurationValue(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRoutingCancelled() {
    }

    public void erasePolyLines() {
        for (Polyline line : polylines)
            line.remove();
        polylines.clear();
    }
}

