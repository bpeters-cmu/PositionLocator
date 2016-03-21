package cmu.positionlocator;

/**
 * Created by donkike on 3/21/16.
 */
public class LocationDistance implements Comparable<LocationDistance> {

    private final String ID;
    private final double distance;

    public LocationDistance(String ID, double distance) {
        this.ID = ID;
        this.distance = distance;
    }

    public String getID() {
        return ID;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public int compareTo(LocationDistance another) {
        return (int)(distance - another.distance);
    }

    @Override
    public String toString() {
        return ID + ": " + distance;
    }
}
