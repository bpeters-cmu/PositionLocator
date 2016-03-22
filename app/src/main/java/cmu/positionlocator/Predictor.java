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
import java.util.PriorityQueue;
import java.util.Queue;

public class Predictor implements View.OnClickListener {

    private final List<Location> locations;
    private final ListView listView;


    public Predictor(List<Location> locations, ListView view) {
        this.locations = locations;
        this.listView = view;
    }

    @Override
    public void onClick(View v) {
        Location currentLocation = getCurrentLocation();
        List<LocationDistance> distances = new ArrayList<>();
        for (Location location : locations) {
            distances.add(currentLocation.distanceFrom(location));
        }
        Collections.sort(distances, Collections.reverseOrder());
        LocationDistance[] closestLocations = new LocationDistance[5];
        distances.subList(0, 5).toArray(closestLocations);
        listView.setAdapter(new ArrayAdapter<LocationDistance>(v.getContext(), android.R.layout.simple_list_item_1, closestLocations));
    }
    public Location getCurrentLocation(){

        WifiManager wifi;
        String wifis[];
        wifi=(WifiManager)ApplicationContextProvider.getContext().getSystemService(Context.WIFI_SERVICE);
        //wifiReciever = new WifiScanReceiver();
        wifi.startScan();
        //User scans for a list of all available WiFi signals
        List<ScanResult> wifiScanList = wifi.getScanResults();

        //array of detected WiFi networks and signal strengths to display for mapping purposes
        wifis = new String[wifiScanList.size()];



        Location currentLocation = new Location(null);

        for (int i = 0; i < wifiScanList.size(); i++) {


            //signal strength in dBm
            int level = wifiScanList.get(i).level;

            //Network ID
            String ssid = wifiScanList.get(i).SSID;

            //Access point mac address
            String bssid = wifiScanList.get(i).BSSID;

            currentLocation.getSignals().put(bssid,level);


        }

        return currentLocation;
    }
}
