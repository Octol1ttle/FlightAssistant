package ru.octol1ttle.flightassistant.computers.navigation;

public record LandingMinimums(AltitudeType type, int altitude) {
    public enum AltitudeType {
        ABSOLUTE("Absolute"),
        ABOVE_GROUND("AboveGround");

        public final String nbtName;
        AltitudeType(String nbtName) {
            this.nbtName = nbtName;
        }
    }
}