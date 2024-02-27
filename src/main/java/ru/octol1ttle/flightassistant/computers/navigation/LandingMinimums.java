package ru.octol1ttle.flightassistant.computers.navigation;

public record LandingMinimums(AltitudeType type, int altitude) {
    public enum AltitudeType {
        ABSOLUTE,
        ABOVE_GROUND
    }
}