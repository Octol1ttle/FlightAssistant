package ru.octol1ttle.flightassistant.computers.navigation;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;

public class Waypoint {
    private final Vector2d targetPosition;
    private final @Nullable Integer targetSpeed;
    protected @Nullable Integer targetAltitude;

    public Waypoint(Vector2d targetPosition, @Nullable Integer targetAltitude, @Nullable Integer targetSpeed) {
        this.targetPosition = targetPosition;
        this.targetAltitude = targetAltitude;
        this.targetSpeed = targetSpeed;
    }

    public Vector2d targetPosition() {
        return targetPosition;
    }

    public Integer targetAltitude() {
        return targetAltitude;
    }

    public Integer targetSpeed() {
        return targetSpeed;
    }
}
