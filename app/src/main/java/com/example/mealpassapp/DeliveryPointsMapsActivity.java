package com.example.mealpassapp;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class DeliveryPointsMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double custlati , custLongi;
    double userLati , userLongi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_points_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        custlati = CheckDeliveryPointsActivity.custLati;
        custLongi = CheckDeliveryPointsActivity.custLongi;
        userLati = FoodSellerActivity.lati;
        userLongi = FoodSellerActivity.longi;

        Location custLocation = new Location("custLocation");
        custLocation.setLatitude(custlati);
        custLocation.setLongitude(custLongi);

        Location userLocation = new Location("userLoc");
        userLocation.setLatitude(userLati);
        userLocation.setLongitude(userLongi);

        double diistance = 0;

        diistance = userLocation.distanceTo(custLocation)/1000;
        Toast.makeText(getApplicationContext(),"Distance :  "+diistance,Toast.LENGTH_LONG).show();

        LatLng customer = new LatLng(custlati , custLongi);
        LatLng seller = new LatLng(userLati , userLongi);

        mMap.addPolyline(
                new PolylineOptions()
                        .add(seller)
                        .add(customer)
                        .width(5f)
                        .color(Color.RED)
        );

        LatLng custLoc = new LatLng(custlati, custLongi);
        mMap.addMarker(new MarkerOptions().position(custLoc).title("User location (destination)").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(custLoc, 5));

        LatLng userLoc = new LatLng(userLati, userLongi);
        mMap.addMarker(new MarkerOptions().position(userLoc).title("Your location (Origin)").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 5));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), CheckDeliveryPointsActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
        finish();
    }
}