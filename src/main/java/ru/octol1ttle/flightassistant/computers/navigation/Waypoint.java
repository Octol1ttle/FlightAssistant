package ru.octol1ttle.flightassistant.computers.navigation;

import org.joml.Vector2d;

public record Waypoint(Vector2d targetPosition, Integer targetAltitude, Integer targetSpeed) {
}
