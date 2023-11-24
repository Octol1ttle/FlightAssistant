package ru.octol1ttle.flightassistant.computers.navigation;

import java.util.ArrayList;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.ITickableComputer;

public class FlightPlanner extends ArrayList<Waypoint> implements ITickableComputer {
    private final AirDataComputer data;
    private @Nullable Waypoint currentWaypoint;

    public FlightPlanner(AirDataComputer data) {
        this.data = data;
    }

    public boolean waypointExistsAt(int index) {
        return index < this.size();
    }

    @Override
    public void tick() {
        if (currentWaypoint == null) {
            return;
        }

        Vector2d target = new Vector2d(currentWaypoint.targetPosition());
        if (target.sub(data.position.x, data.position.z).length() <= 5.0f) {
            int nextIndex = this.indexOf(currentWaypoint) + 1;
            currentWaypoint = waypointExistsAt(nextIndex) ? this.get(nextIndex) : null;
        }
    }

    @Override
    public void reset() {
    }

    @Override
    public String getId() {
        return "flt_plan";
    }
}
