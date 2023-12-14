package ru.octol1ttle.flightassistant.computers.navigation;

import java.util.ArrayList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.ITickableComputer;

public class FlightPlanner extends ArrayList<Waypoint> implements ITickableComputer {
    private final AirDataComputer data;
    private @Nullable Waypoint currentWaypoint;
    public float altitudeDeviation;
    public @Nullable Float minAltitudeDeviation;

    public FlightPlanner(AirDataComputer data) {
        this.data = data;
    }

    @Override
    public void tick() {
        if (!this.contains(currentWaypoint)) {
            currentWaypoint = null;
        }

        if (currentWaypoint == null) {
            minAltitudeDeviation = null;
            return;
        }

        if (currentWaypoint.targetAltitude() != null) {
            altitudeDeviation = Math.abs(data.altitude - currentWaypoint.targetAltitude());
            if (minAltitudeDeviation == null) {
                minAltitudeDeviation = altitudeDeviation;
            } else {
                minAltitudeDeviation = Math.min(minAltitudeDeviation, altitudeDeviation);
            }
        }

        Vector2d target = new Vector2d(currentWaypoint.targetPosition());
        int altitude = currentWaypoint.targetAltitude() != null ? currentWaypoint.targetAltitude() : (int) data.altitude;
        if (target.sub(data.position.x, data.position.z).length() <= 5.0f && Math.abs(altitude - data.altitude) <= 5.0f) {
            int nextIndex = this.indexOf(currentWaypoint) + 1;
            currentWaypoint = waypointExistsAt(nextIndex) ? this.get(nextIndex) : null;
            minAltitudeDeviation = null;
        }
    }

    public boolean waypointExistsAt(int index) {
        return index < this.size();
    }

    public @Nullable Integer getManagedSpeed() {
        if (currentWaypoint == null) {
            return null;
        }
        return currentWaypoint.targetSpeed();
    }

    public @Nullable Integer getManagedAltitude() {
        if (currentWaypoint == null) {
            return null;
        }
        return currentWaypoint.targetAltitude();
    }

    public @Nullable Float getManagedHeading() {
        if (currentWaypoint == null) {
            return null;
        }

        Vec3d current = data.position;
        Vector2d target = currentWaypoint.targetPosition();
        return AirDataComputer.toHeading((float) Math.toDegrees(MathHelper.atan2(-(target.x - current.x), target.y - current.z)));
    }

    public @Nullable Vector2d getTargetPosition() {
        if (currentWaypoint == null) {
            return null;
        }

        return currentWaypoint.targetPosition();
    }

    public void execute(int waypointIndex) {
        // TODO: throw exception if waypoint doesn't exist
        if (waypointExistsAt(waypointIndex)) {
            currentWaypoint = this.get(waypointIndex);
        }
    }

    @Override
    public void reset() {
        currentWaypoint = null;
        altitudeDeviation = 0.0f;
        minAltitudeDeviation = null;
    }

    @Override
    public String getId() {
        return "flt_plan";
    }
}
