package com.wrocapp.zwiedzamwroclaw;


import android.Manifest;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class Navigation extends Fragment implements OnMapReadyCallback, DirectionFinderListener {

    private static final String GET_TITLE = "GET_TITLE";
    private static final String GET_LATITUDE = "GET_SZEROKOSC";
    private static final String GET_LONGITUDE = "GET_DLUGOSC";

    LatLng selectedItemLocation;
    LatLng myLocation;

    double myLat;
    double myLng;

    String myLatString;
    String myLngString;
    String myLocationString;

    String selectedItemLatString;
    String selectedItemLngString;
    String selectedItemLocationString;

    private Double longitude;
    private Double latitude;

    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;

    private View view;

    GoogleMap mGoogleMap;
    MapView mMapView;
    private String nazwa;

    LocationListener locationListener;


    public Navigation() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_navigation, container, false);

        showMyLocation();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = (MapView) view.findViewById(R.id.mapanavi);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getActivity());

        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        Bundle bundle = this.getArguments();
        nazwa = bundle.getString(GET_TITLE);
        latitude = bundle.getDouble(GET_LATITUDE);
        longitude = bundle.getDouble(GET_LONGITUDE);

        selectedItemLocation = new LatLng(latitude, longitude);

        if (myLat == 0.0) {
            Toast.makeText(getActivity(), getString(R.string.error_toast) + getString(R.string.no_gps), Toast.LENGTH_LONG).show();
        }

        myLocation = new LatLng(myLat, myLng);

        CameraPosition cameraPosition = CameraPosition.builder().target(new LatLng(latitude, longitude)).zoom(16).bearing(0).tilt(45).build();

        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1234);
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1234);
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);

        directionFinder();

    }

    private void directionFinder() {
        myLatString = String.valueOf(myLat);
        myLngString = String.valueOf(myLng);
        myLocationString = (myLatString + "," + myLngString);

        selectedItemLatString = String.valueOf(latitude);
        selectedItemLngString = String.valueOf(longitude);
        selectedItemLocationString = (selectedItemLatString + "," + selectedItemLngString);

        try {
            new DirectionFinder(this, myLocationString, selectedItemLocationString).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void showMyLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        if (lastKnownLocation == null) {
            locationProvider = LocationManager.GPS_PROVIDER;
            lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        }
        if (lastKnownLocation != null) {
            makeUseOfNewLocation(lastKnownLocation);
        }
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: 23.11.2017
                return;
            }
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    }

    private void makeUseOfNewLocation(Location location) {
        if (location != null) {
            myLat = location.getLatitude();
            myLng = location.getLongitude();
        }
    }


    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(getActivity(), getString(R.string.please_wait), getString(R.string.finding_direction), true);

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {

        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) view.findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) view.findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(mGoogleMap.addMarker(new MarkerOptions()
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mGoogleMap.addMarker(new MarkerOptions()
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(nazwa)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.parseColor("#03A9F4")).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mGoogleMap.addPolyline(polylineOptions));
        }

    }
}
