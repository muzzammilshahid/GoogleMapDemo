package com.example.googlemapdemo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    double latitude;
    double longitude;


    FusedLocationProviderClient mFusedLocationClient;


    // flag for GPS Status
    private boolean gps_enable = false;
    // flag for network status
    private boolean network_enable = false;

    public LocationManager locationManager;

    Geocoder geocoder;
    GoogleMap map;

    Button pinLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pinLocation = findViewById(R.id.pin_location);

        pinLocation.setOnClickListener(v -> addMarker());

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        int location1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int location2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        List<String> listPermission = new ArrayList<>();

        if (location1 != PackageManager.PERMISSION_GRANTED) {
            listPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (location2 != PackageManager.PERMISSION_GRANTED) {
            listPermission.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (!listPermission.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermission.toArray(new String[listPermission.size()]),
                    1);
        } else {
            try {
                // This will check that the GPS is enable or not
                gps_enable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            } catch (Exception e) {
                System.out.println("This is first catch");
                e.printStackTrace();
            }

            try {
                // This will check that the network is enable or not
                network_enable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            } catch (Exception e) {
                System.out.println("This is last catch");
                e.printStackTrace();
            }

            //if gps and network is not available then show the dialog box
            if (!gps_enable && !network_enable) {
                showSettingsAlert();
            } else {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, location -> {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().
                                        findFragmentById(R.id.map);
                                mapFragment.getMapAsync(MainActivity.this);
                            }
                        });
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng latLng = new LatLng(latitude, longitude);
        try {
            List<Address> markerAddress = geocoder.getFromLocation(latitude, longitude, 1);
            map.addMarker(new MarkerOptions().position(latLng).title(markerAddress.get(0).getAddressLine(0)));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == 1) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted.
                try {
                    // This will check that the GPS is enable or not
                    gps_enable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                } catch (Exception e) {
                    System.out.println("This is first catch");
                    e.printStackTrace();
                }

                try {
                    // This will check that the network is enable or not
                    network_enable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                } catch (Exception e) {
                    System.out.println("This is last catch");
                    e.printStackTrace();
                }

                //if gps and network is not available then show the dialog box
                if (!gps_enable && !network_enable) {
                    showSettingsAlert();
                } else {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mFusedLocationClient.getLastLocation()
                            .addOnSuccessListener(this, location -> {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().
                                            findFragmentById(R.id.map);
                                    mapFragment.getMapAsync(MainActivity.this);
                                }
                            });
                }


            } else {

                // permission denied
                Toast.makeText(this, "The permission denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        //Setting Dialog Title
        alertDialog.setTitle("Location");

        //Setting Dialog Message
        alertDialog.setMessage("Please Enable the location");

        //On Pressing Setting button
        alertDialog.setPositiveButton("Enable Location", (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        });

        //On pressing cancel button
        alertDialog.setNegativeButton("cancel", (dialog, which) -> dialog.cancel());

        alertDialog.show();
    }

    public void addMarker() {
        try {
            List<Address> markerAddress = geocoder.getFromLocation(latitude, longitude, 1);
            LatLng latLng1 = new LatLng(latitude, longitude);
            map.addMarker(new MarkerOptions().position(latLng1).title(markerAddress.get(0).getAddressLine(0)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}