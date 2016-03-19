package cmu.positionlocator;

/**
 * Created by bpeters on 3/19/16.
 */
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