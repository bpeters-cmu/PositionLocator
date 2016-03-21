package cmu.positionlocator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by bpeters on 3/19/16.
 */

//A location has an ID and a list of signals from various nearby access points
public class Location {
    String ID;
    Map<String, Integer> signals;

    public Location(String ID){
        this.ID = ID;
        this.signals = new HashMap<>();
    }
    public Map<String, Integer> getSignals(){
        return this.signals;
    }

    public String getID(){
        return this.ID;
    }

    public LocationDistance distanceFrom(Location other) {
        double distance = 0;
        Set<String> BSSIDs = signals.keySet();
        BSSIDs.addAll(other.signals.keySet());
        for (String BSSID : BSSIDs) {
            int level1 = signals.containsKey(BSSID) ? signals.get(BSSID) : Integer.MIN_VALUE;
            int level2 = other.signals.containsKey(BSSID) ? other.signals.get(BSSID) : Integer.MIN_VALUE;
            int diff = level1 - level2;
            distance += diff * diff;
        }
        distance = Math.sqrt(distance);
        return new LocationDistance(other.ID, distance);
    }

    @Override
    public String toString(){
        String result = null;

        System.out.println(this.getID());
        System.out.println(this.getSignals().size());



        return result;
    }
}
