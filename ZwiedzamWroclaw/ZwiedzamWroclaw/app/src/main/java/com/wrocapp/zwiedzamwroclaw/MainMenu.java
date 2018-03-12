package com.wrocapp.zwiedzamwroclaw;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainMenu extends Fragment {

    private Location myLocation;
    double myLat;
    double myLng;

    private static final String  URL_DATA = "http://zwiedzamwroclaw.prv.pl/MainMenu/api.php";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private List<ListItem> listItems;

    public MainMenu() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.main_menu));

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        listItems = new ArrayList<>();

        loadRecyclerViewData();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1234);
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1234);
            }
        }
        showMyLocation();

        return view;
    }

    private void showMyLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1234);
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1234);
            return;
        }
        myLocation = locationManager.getLastKnownLocation(provider);
        if (myLocation != null) {
            myLat = myLocation.getLatitude();
            myLng = myLocation.getLongitude();
        }
    }

    private void loadRecyclerViewData() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading_data));
        progressDialog.show();

        recyclerView.setNestedScrollingEnabled(false);
        //recyclerView.stopScroll();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONArray menuGlowne = new JSONArray(response);
                    Random generator = new Random();

                    for (int i=0; i<3; i++){

                        int e = generator.nextInt(menuGlowne.length());

                        JSONObject menuGlowneObject = menuGlowne.getJSONObject(e);

                        int id = menuGlowneObject.getInt("id");
                        String title = menuGlowneObject.getString("title");
                        String shortDesc = menuGlowneObject.getString("shortDesc");
                        String smallImageURL = menuGlowneObject.getString("smallImageURL");
                        String bigImageURL = menuGlowneObject.getString("bigImageURL");
                        String longDesc = menuGlowneObject.getString("longDesc");
                        double latitude = menuGlowneObject.getDouble("latitude");
                        double longitude = menuGlowneObject.getDouble("longitude");

                        if (i == 0)
                            title = "HIT DNIA: " + title;
                        else
                            title = "Polecane: " + title;

                        ListItem listItem = new ListItem(id, title,shortDesc, smallImageURL, bigImageURL, longDesc, latitude, longitude);

                        listItems.add(listItem);
                    }
                    adapter = new MainMenuAdapter(listItems, getActivity());
                    recyclerView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), getString(R.string.error_toast), Toast.LENGTH_LONG).show();
            }
        });

        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

}
