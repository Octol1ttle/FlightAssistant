package ru.octol1ttle.flightassistant.computers.navigation;

import java.util.ArrayList;

public class FlightPlanner extends ArrayList<Waypoint> {
    public boolean waypointExistsAt(int index) {
        return index < this.size();
    }
}
