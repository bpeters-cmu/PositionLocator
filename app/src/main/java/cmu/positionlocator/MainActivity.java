package cmu.positionlocator;

import android.app.Activity;
import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

import java.util.List;

import java.math.*;

public class MainActivity extends Activity  {
    ListView lv;
    WifiManager wifi;
    String wifis[];
    WifiScanReceiver wifiReciever;
    TextView tv;
    private Spinner spinner1;
    private Button button;

    List<Location> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //lv=(ListView)findViewById(R.id.listView);
        tv = (TextView)findViewById(R.id.textView2);


        //initiate list of mapped locations
        locations = initiate();


        wifi=(WifiManager)getSystemService(Context.WIFI_SERVICE);
        wifiReciever = new WifiScanReceiver();
        wifi.startScan();
    }

    protected void onPause() {
        unregisterReceiver(wifiReciever);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }
    public void addListenerOnSpinnerItemSelection() {
        spinner1 = (Spinner) findViewById(R.id.spinner);

    }

    // get the selected dropdown list value
    public void addListenerOnButton() {

        spinner1 = (Spinner) findViewById(R.id.spinner);

        String location = spinner1.getSelectedItem().toString();

        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //User scans for a list of all available WiFi signals
                List<ScanResult> wifiScanList = wifi.getScanResults();

                //array of detected WiFi networks and signal strengths to display for mapping purposes
                wifis = new String[wifiScanList.size()];




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



                    //
                    wifis[i] = ssid + " - " + bssid + " : " + String.valueOf(level);


                }
                



            }


        });

    }
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




    private class WifiScanReceiver extends BroadcastReceiver{


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

    }


    //this method compares the users recorded signal level for a given AP and compares it with the list of pre-recorded locations
    //an array is returned containing the difference between the user's recorded signal level and the mapped level for each location
    //the index with the smallest difference should indicate which mapped location the user is closest to
    public int[] findClosestMatch(String bssid, int level){

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
    }

    //A location has an ID and a list of signals from various nearby access points
    public class Location{

        int ID;
        List<Signal> signals = new ArrayList<Signal>();

        public Location(int ID, List<Signal> signals){
            this.ID = ID;
            this.signals = signals;
        }
        public List getSignals(){
            return this.signals;
        }
        public int getID(){
            return this.ID;
        }

    }

    //A signal has a BSSID(mac address) and a signal level for that AP
    public class Signal{
        String BSSID;
        int level;

        public Signal(String BSSID, int level){
            this.BSSID = BSSID;
            this.level = level;
        }
        public String getBSSID(){
            return this.BSSID;
        }
        public int getLevel(){
            return this.level;
        }
    }

    //We will record each mapped location within this method. We will record the mac address and signal level
    //of several access points here so we can compare with the user's signal strength for these access points
    public List<Location> initiate(){

        List<Location> locations = new ArrayList<Location>();

        //-------------- location 1 ------------------//

        List<Signal> lsOne = new ArrayList<Signal>();
        Signal s1one = new Signal("00:1a:1e:8a:f7:41",-79);
        Signal s2one = new Signal("00:1a:1e:8a:f9:21",-64);
        Signal s3one = new Signal("00:1a:1e:8a:5d:61",-68);


        lsOne.add(s1one);
        lsOne.add(s2one);
        lsOne.add(s3one);
        Location lOne = new Location(1,lsOne);

        //-------------- location 2 ------------------//

        List<Signal> lsTwo = new ArrayList<Signal>();
        Signal s1two = new Signal("00:1a:1e:8a:f7:41",-54);
        Signal s2two = new Signal("00:1a:1e:8a:f9:21",-64);
        Signal s3two = new Signal("00:1a:1e:8a:5d:61",-49);

        lsTwo.add(s1two);
        lsTwo.add(s2two);
        lsTwo.add(s3two);
        Location lTwo = new Location(2,lsTwo);

        //-------------- location 3 ------------------//

        List<Signal> lsThree = new ArrayList<Signal>();
        Signal s1three = new Signal("00:1a:1e:8a:f7:41",-62);
        Signal s2three = new Signal("00:1a:1e:8a:f9:21",-74);
        Signal s3three = new Signal("00:1a:1e:8a:5d:61",-100);

        lsThree.add(s1three);
        lsThree.add(s2three);
        lsThree.add(s3three);
        Location lThree = new Location(3,lsThree);

        //-------------- location 4 ------------------//

        List<Signal> lsFour = new ArrayList<Signal>();
        Signal s1four = new Signal("00:1a:1e:8a:f7:41",-73);
        Signal s2four = new Signal("00:1a:1e:8a:f9:21",-64);
        Signal s3four = new Signal("00:1a:1e:8a:5d:61",-54);

        lsFour.add(s1four);
        lsFour.add(s2four);
        lsFour.add(s3four);
        Location lFour = new Location(4,lsFour);

        //-------------- location 5 ------------------//

        List<Signal> lsFive = new ArrayList<Signal>();
        Signal s1five = new Signal("00:1a:1e:8a:f7:41",-59);
        Signal s2five = new Signal("00:1a:1e:8a:f9:21",-64);
        Signal s3five = new Signal("00:1a:1e:8a:5d:61",-54);

        lsFive.add(s1five);
        lsFive.add(s2five);
        lsFive.add(s3five);
        Location lFive = new Location(5,lsFive);


        //add all recorded locations to the list so we can compare with the data the user provides
        locations.add(lOne);
        locations.add(lTwo);
        locations.add(lThree);
        locations.add(lFour);
        locations.add(lFive);


        return locations;
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
