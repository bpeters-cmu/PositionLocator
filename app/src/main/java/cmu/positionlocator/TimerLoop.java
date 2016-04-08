package cmu.positionlocator;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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

                }

                List<LocationDistance> distances = new ArrayList<>();
                for (Location location : locations) {
                    distances.add(currentLocation.distanceFrom(location));
                }
                Collections.sort(distances);
                int size = Math.min(7, distances.size());
                LocationDistance[] closestLocations = new LocationDistance[size];
                distances.subList(0, size).toArray(closestLocations);
                listView.setAdapter(new ArrayAdapter<LocationDistance>(v.getContext(), android.R.layout.simple_list_item_1, closestLocations));



                FileOutputStream outputStream, scanOutputStream;
                try {
                    outputStream = v.getContext().openFileOutput("results.txt", Context.MODE_APPEND);
                    scanOutputStream = v.getContext().openFileOutput("scans.txt", Context.MODE_APPEND);

                    String timeStamp = new SimpleDateFormat("MM-dd HH:mm:ss").format(new Date())+"\n";

                    outputStream.write(timeStamp.getBytes());
                    scanOutputStream.write(timeStamp.getBytes());

                    for(LocationDistance loc:closestLocations){
                        String location = loc.toString() + "\n";
                        outputStream.write(location.getBytes());
                    }

                    for (ScanResult result : wifiScanList) {
                        String line = result.SSID + '\t' + result.BSSID + '\t' + result.level + '\n';
                        scanOutputStream.write(line.getBytes());
                    }

                    outputStream.close();
                    scanOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
                });


                }
                }





