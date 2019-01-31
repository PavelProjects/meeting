package com.example.root.meeting.Meeting;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.root.meeting.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MeetingPointMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_point_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng home = new LatLng(55.704989, 37.746839);
        googleMap.addMarker(new MarkerOptions()
                .position(home)
                .title("marker")
                .draggable(true)
        );
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(home));
    }
}
