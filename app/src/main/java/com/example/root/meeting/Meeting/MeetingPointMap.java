package com.example.root.meeting.Meeting;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import com.example.root.meeting.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MeetingPointMap extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private LatLng startLatLng;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_point_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intent = getIntent();
        longitude=intent.getDoubleExtra("long",0);
        latitude=intent.getDoubleExtra ("lat",0);
        startLatLng= new LatLng(latitude,longitude);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions()
                .position(startLatLng)
                .title("MEETING POINT")
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLng(startLatLng));
    }
}
