package com.example.jzhou.serendlpity;

import android.Manifest;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Bel on 24.02.2016.
 */
public class Map extends Fragment implements OnMapReadyCallback {
    private GoogleMap googleMap;
    View root;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        root = inflater.inflate(R.layout.fragment_map, container, false);
        //googleMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMapAsync();
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
            Toast.makeText(getContext(), "permission is not given", Toast.LENGTH_LONG).show();
        }

        // Add a marker in Oulu Yliopisto
        LatLng oulu = new LatLng(65.065534, 25.488174);
        //LatLng oulu = new LatLng(65, 25);


        googleMap.addMarker(new MarkerOptions()
                .position(oulu)
                .title("Lovely place on the lake"));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(oulu, 15));

        //CameraUpdate zoomTo = CameraUpdateFactory.zoomTo(15);
        //googleMap.moveCamera(zoomTo);

    }
}
