package cmu.positionlocator;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class Predictor implements View.OnClickListener {

    private final List<Location> locations;
    private final ListView listView;

    public Predictor(List<Location> locations, ListView view) {
        this.locations = locations;
        this.listView = view;
    }

    @Override
    public void onClick(View v) {
        Location currentLocation = null;
        List<LocationDistance> distances = new ArrayList<>();
        for (Location location : locations) {
            distances.add(currentLocation.distanceFrom(location));
        }
        Collections.sort(distances, Collections.reverseOrder());
        LocationDistance[] closestLocations = new LocationDistance[5];
        distances.subList(0, 5).toArray(closestLocations);
        listView.setAdapter(new ArrayAdapter<LocationDistance>(v.getContext(), android.R.layout.simple_list_item_1, closestLocations));
    }
}
