package ru.octol1ttle.flightassistant.computers.navigation;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;

public record Waypoint(Vector2d targetPosition, @Nullable Integer targetAltitude, @Nullable Integer targetSpeed) {
}
