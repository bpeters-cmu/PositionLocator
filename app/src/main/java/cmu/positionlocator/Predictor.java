package cmu.positionlocator;


import android.app.Activity;


import android.app.Application;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Predictor implements View.OnClickListener {

    private final List<Location> locations;
    private final ListView listView;


    public Predictor(List<Location> locations, ListView view) {
        this.locations = locations;
        this.listView = view;
    }

    @Override
    public void onClick(View v) {

        Timer timer = new Timer();


        //execute predictor every minute
        timer.scheduleAtFixedRate(new TimerLoop(v,locations,listView), new Date(), 60000);




    }
}





