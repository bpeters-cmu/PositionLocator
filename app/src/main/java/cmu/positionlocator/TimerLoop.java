package cmu.positionlocator;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;

/**
 * Created by bpeters on 4/5/16.
 */
public class TimerLoop extends TimerTask {

    private final View v;
    private final List<Location> locations;
    private final ListView listView;


    TimerLoop ( View v, List<Location> locations, ListView listView ) {
        this.v = v;
        this.listView = listView;
        this.locations = locations;
    }

    public void run() {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                //Here your code that runs on UI Threads
                //Do stuff
                System.out.println("here's test");

                Location currentLocation = new Location(null);


                WifiManager wifi = (WifiManager) v.getContext().getSystemService(Context.WIFI_SERVICE);


                List<ScanResult> wifiScanList = wifi.getScanResults();

                for (ScanResult result : wifiScanList) {


                    currentLocation.getSignals().put(result.BSSID, result.level);
//                    System.out.println("test");
//                    System.out.println(result.SSID.toString() + " " + result.BSSID + " " + result.level);
//
//
//                    System.out.println(result.BSSID + " - " + result.level);
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

        });


}
}




