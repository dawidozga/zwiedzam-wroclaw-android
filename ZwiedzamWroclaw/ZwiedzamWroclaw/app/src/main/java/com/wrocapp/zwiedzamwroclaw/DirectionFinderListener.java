package com.wrocapp.zwiedzamwroclaw;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import com.wrocapp.zwiedzamwroclaw.Route;

/**
 * Created by Dawid on 20.11.2017.
 */

public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
