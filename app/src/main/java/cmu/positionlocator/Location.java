package cmu.positionlocator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bpeters on 3/19/16.
 */

//A location has an ID and a list of signals from various nearby access points
public class Location {
    String ID;
    List<Signal> signals = new ArrayList<Signal>();

    public Location(String ID, List<Signal> signals){
        this.ID = ID;
        this.signals = signals;
    }
    public List getSignals(){
        return this.signals;
    }
    public String getID(){
        return this.ID;
    }
}
