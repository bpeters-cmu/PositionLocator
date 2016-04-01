package cmu.positionlocator;


import android.app.Activity;


import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Predictor implements View.OnClickListener {

    private final List<Location> locations;
    private final ListView listView;


    public Predictor(List<Location> locations, ListView view) {
        this.locations = locations;
        this.listView = view;
    }

    @Override
    public void onClick(View v) {

        Location currentLocation = new Location(null);

        WifiManager wifi = (WifiManager) v.getContext().getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> wifiScanList = wifi.getScanResults();

        for (ScanResult result : wifiScanList) {
            System.out.println(result.BSSID + " - " + result.level);
            currentLocation.getSignals().put(result.BSSID, result.level);
        }

        List<LocationDistance> distances = new ArrayList<>();
        for (Location location : locations) {
            distances.add(currentLocation.distanceFrom(location));
        }
        Collections.sort(distances);
        int size = Math.min(5, distances.size());
        LocationDistance[] closestLocations = new LocationDistance[size];
        distances.subList(0, size).toArray(closestLocations);
        listView.setAdapter(new ArrayAdapter<LocationDistance>(v.getContext(), android.R.layout.simple_list_item_1, closestLocations));
    }

}
