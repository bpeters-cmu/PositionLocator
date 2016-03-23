package cmu.positionlocator;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;

import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import java.util.List;

import java.util.Scanner;

public class MainActivity extends Activity  {
    ListView lv;
    WifiManager wifi;
    String wifis[];
    //WifiScanReceiver wifiReciever;
    TextView tv;
    private Spinner spinner1;
    private Button button;
    private Button button2;
    private Button btnPredict;

    List<Location> locations;

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect Wi-Fi signals.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }

        //lv=(ListView)findViewById(R.id.listView);
        tv = (TextView)findViewById(R.id.textView2);


        //initiate list of mapped locations
        locations = initiate();


        wifi=(WifiManager)getSystemService(Context.WIFI_SERVICE);
        //wifiReciever = new WifiScanReceiver();
        wifi.startScan();
        spinner1 = (Spinner) findViewById(R.id.spinner);


        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        btnPredict = (Button) findViewById(R.id.btnPredict);

        btnPredict.setOnClickListener(new Predictor(locations, (ListView)findViewById(R.id.listView)));

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //User scans for a list of all available WiFi signals
                List<ScanResult> wifiScanList = wifi.getScanResults();

                //array of detected WiFi networks and signal strengths to display for mapping purposes
                wifis = new String[wifiScanList.size()];


                String filename = "locations.txt";

                //Toast.makeText(getApplicationContext(), filename, Toast.LENGTH_LONG).show();
                FileOutputStream outputStream;

                try {

                    outputStream = openFileOutput(filename, Context.MODE_APPEND);


                    //iterate through list of scanned access points
                    final String location = "#" + '\t' + spinner1.getSelectedItem().toString() + '\n';


                    outputStream.write(location.getBytes());

                    int count = 0;

                    for (int i = 0; i < wifiScanList.size(); i++) {

                        //System.out.println("test" + i);

                        //wifis[i] = ((wifiScanList.get(i)).toString());


                        //signal strength in dBm
                        int level = wifiScanList.get(i).level;

                        //Network ID
                        String ssid = wifiScanList.get(i).SSID;

                        //Access point mac address
                        String bssid = wifiScanList.get(i).BSSID;


                        if(ssid.equals("CMU-SECURE")) {

                            wifis[count] = bssid + "\t" + String.valueOf(level) + "\n";

                            outputStream.write(wifis[count].getBytes());
                            count ++;

                            System.out.println(ssid.toString());

                        }

                    }
                    outputStream.close();

                    try {
                        BufferedReader inputReader = new BufferedReader(new InputStreamReader(
                                openFileInput("locations.txt")));
                        String inputString;
                        StringBuffer stringBuffer = new StringBuffer();
                        while ((inputString = inputReader.readLine()) != null) {
                            stringBuffer.append(inputString + "\n");
                        }
                        System.out.println(stringBuffer.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });

        button2.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                locations = initiate();
                for(int i = 0; i<locations.size(); i++){
                    locations.get(i).toString();
                }
            }

        });




    }

    /*protected void onPause() {
        unregisterReceiver(wifiReciever);
        super.onPause();
    }*/

    /*protected void onResume() {
        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }*/



            @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




    /*private class WifiScanReceiver extends BroadcastReceiver{


        public void onReceive(Context c, Intent intent) {


            //This is a list of access point ID's used for localization
            List<String> accessPointIDs = new ArrayList<String>();
            accessPointIDs.add("00:1a:1e:8a:f7:41");
            accessPointIDs.add("00:1a:1e:8a:f9:21");
            accessPointIDs.add("00:1a:1e:8a:5d:61");
            accessPointIDs.add("00:1a:1e:8a:f7:41");


            //User scans for a list of all available WiFi signals
            List<ScanResult> wifiScanList = wifi.getScanResults();

            //array of detected WiFi networks and signal strengths to display for mapping purposes
            wifis = new String[wifiScanList.size()];



            int[][] multi = new int[5][5];



            //iterate through list of scanned access points
            for(int i = 0; i < wifiScanList.size(); i++) {

                //wifis[i] = ((wifiScanList.get(i)).toString());


                //signal strength in dBm
                int level = wifiScanList.get(i).level;

                //Network ID
                String ssid = wifiScanList.get(i).SSID;

                //Access point mac address
                String bssid = wifiScanList.get(i).BSSID;


                int count = 0;

                //if this AP has been mapped
                if (accessPointIDs.contains(bssid)) {


                    //returns an array with the
                    multi[count] = findClosestMatch(bssid, level);

                    count++;
                }


                //
                wifis[i] = ssid + " - " + bssid + " : " + String.valueOf(level);


            }


            int[] compare = new int[5];
            for (int i = 0; i < multi.length; i++) {
                int sum = 0;
                for (int j = 0; j < multi[i].length; j++) {
                    sum += multi[j][i];
                }
                compare[i] = sum;
            }

            //find the location with the smallest difference in signal level
            int closestLocation = getMinIndex(compare) + 1;






            //displays list of AP's and signal levels recorded by the user
            //lv.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, wifis));

            //tv.setText(compare[0] +":"+ compare[1] +":"+ compare[2]+ ":"+compare[3]+":"+compare[4] );

            tv.setText("Closest location: " + closestLocation);
        }

    }*/


    //this method compares the users recorded signal level for a given AP and compares it with the list of pre-recorded locations
    //an array is returned containing the difference between the user's recorded signal level and the mapped level for each location
    //the index with the smallest difference should indicate which mapped location the user is closest to
    /*public int[] findClosestMatch(String bssid, int level){

        int[] difference = new int[5];

        for (int j = 0; j < locations.size(); j++) {



            for (int k = 0; k < locations.get(j).getSignals().size(); k++) {

                Signal s = (Signal) locations.get(j).getSignals().get(k);

                if (s.getBSSID().equals(bssid)) {

                    int dif = Math.abs(level - s.getLevel());
                    System.out.println("dif = " + dif);
                    difference[j] = dif;
                }

            }

        }


        return difference;
    }*/



    //We will record each mapped location within this method. We will record the mac address and signal level
    //of several access points here so we can compare with the user's signal strength for these access points
    public List<Location> initiate(){

        List<Location> locationList = new ArrayList<Location>();



        // The name of the file to open.
        //File fileName = new File("locations.txt");



        try {
            AssetManager am = getAssets();
            InputStream is = am.open("locations.txt");
            Scanner scanner = new Scanner(is);

            scanner.useDelimiter("\t|\n");
            int count = 0;

            while (scanner.hasNext()) {


                String current = scanner.next();

                if(current.equals("#")){

                    String loc = scanner.next();
                    loc = loc.trim();
                    System.out.println(loc);
                    Location l = new Location(loc);
                    locationList.add(l);
                    count++;



                }else{

                    String mac = current;

                    String level = scanner.next();
                    level = level.trim();
                    locationList.get(count - 1).getSignals().put(mac, Integer.parseInt(level));
                    //Signal s = new Signal(mac,Integer.parseInt(level));
                    //locationList.get(count-1).getSignals().add(s);
                    System.out.println("------");

                }


            }
        }catch (Exception e) {
            e.printStackTrace();
        }




        return locationList;
    }
    public static int getMinIndex(int[] numbers){
        int minValue = numbers[0];
        int minIndex = 0;
        for(int i=1;i<numbers.length;i++){
            if(numbers[i] < minValue){
                minIndex = i;
            }
        }
        return minIndex;
    }
}
