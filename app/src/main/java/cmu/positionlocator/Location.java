package cmu.positionlocator;

import java.util.HashMap;
import java.util.HashSet;

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

        Set<String> BSSIDs = new HashSet<>(signals.keySet());

        BSSIDs.addAll(other.signals.keySet());


        for (String BSSID : BSSIDs) {
            int level1 = signals.containsKey(BSSID) ? Math.max(0, signals.get(BSSID) + 80) : 0;
            int level2 = other.signals.containsKey(BSSID) ? Math.max(0, other.signals.get(BSSID) + 80) : 0;
            int diff = level1 - level2;
            //System.out.println(level1 + " " + level2);
            distance += diff * diff;
        }
        distance = Math.sqrt(distance);
        //System.out.println(other.getID() + " distance: "+ distance);
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
