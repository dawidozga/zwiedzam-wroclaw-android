package com.wrocapp.zwiedzamwroclaw;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class Entertainment extends Fragment {

    private static final String  URL_DATA = "http://zwiedzamwroclaw.prv.pl/Entertainment/api.php";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    double myLat;
    double myLng;

    private List<ListItem> listItems;
    private Spinner spinner;
    private Location myLocation;

    public Entertainment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_entertainment, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.entertainment));

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        spinner = (Spinner) view.findViewById(R.id.sorting);

        showMyLocation();

        setupSort();

        return view;
    }

    private void setupSort() {
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.sort_types, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    sortByBest();
                } else if (position == 1) {
                    sortByName();
                } else {
                    if (myLat != 0.0) sortByClosest();
                }
                adapter = new CategoryAdapter(listItems, getActivity());
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void sortByBest() {
        loadRecyclerViewData();
    }

    private void sortByName() {
        Collections.sort(listItems, new Comparator<ListItem>() {
            @Override
            public int compare(ListItem o1, ListItem o2) {
                return o1.getTitle().compareToIgnoreCase(o2.getTitle());
            }
        });
    }

    private void sortByClosest() {
        Collections.sort(listItems, new Comparator<ListItem>() {
            @Override
            public int compare(ListItem o1, ListItem o2) {
                double lat1 = o1.getLatitude();
                double lat2 = o2.getLatitude();
                double lng1 = o1.getLongitude();
                double lng2 = o2.getLongitude();

                Location loc1 = new Location("1");
                Location loc2 = new Location("2");

                loc1.setLatitude(lat1);
                loc1.setLongitude(lng1);

                loc2.setLatitude(lat2);
                loc2.setLongitude(lng2);

                double distance1 = loc1.distanceTo(myLocation);
                double distance2 = loc2.distanceTo(myLocation);

                if (distance1 > distance2) return 1;
                else if (distance1 < distance2) return -1;
                else return 0;
            }
        });
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
        listItems = new ArrayList<>();

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading_data));
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONArray entertainment = new JSONArray(response);

                    for (int i=0; i<entertainment.length(); i++){
                        JSONObject entertainmentObject = entertainment.getJSONObject(i);

                        int id = entertainmentObject.getInt("id");
                        String title = entertainmentObject.getString("title");
                        String shortDesc = entertainmentObject.getString("shortDesc");
                        String smallImageURL = entertainmentObject.getString("smallImageURL");
                        String bigImageURL = entertainmentObject.getString("bigImageURL");
                        String longDesc = entertainmentObject.getString("longDesc");
                        double latitude = entertainmentObject.getDouble("latitude");
                        double longitude = entertainmentObject.getDouble("longitude");

                        ListItem listItem = new ListItem(id, title,shortDesc, smallImageURL, bigImageURL, longDesc, latitude, longitude);

                        listItems.add(listItem);
                    }
                    adapter = new CategoryAdapter(listItems, getActivity());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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