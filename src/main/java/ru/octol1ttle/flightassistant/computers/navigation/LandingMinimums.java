package ru.octol1ttle.flightassistant.computers.navigation;

public record LandingMinimums(AltitudeType type, int altitude) {
    public enum AltitudeType {
        ABSOLUTE("Absolute"),
        ABOVE_GROUND("AboveGround");

        public final String serialisedName;
        AltitudeType(String serialisedName) {
            this.serialisedName = serialisedName;
        }

        public static AltitudeType fromSerialisedName(String nbtName) {
            for (AltitudeType type : AltitudeType.values()) {
                if (type.serialisedName.equals(nbtName)) {
                    return type;
                }
            }

            throw new IllegalStateException("Unknown altitude type: %s".formatted(nbtName));
        }
    }
}