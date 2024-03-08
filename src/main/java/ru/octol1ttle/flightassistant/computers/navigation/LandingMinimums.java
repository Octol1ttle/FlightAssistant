package ru.octol1ttle.flightassistant.computers.navigation;

import net.minecraft.nbt.InvalidNbtException;
import net.minecraft.nbt.NbtCompound;

public record LandingMinimums(AltitudeType type, int altitude) {
    public enum AltitudeType {
        ABSOLUTE("Absolute"),
        ABOVE_GROUND("AboveGround");

        public final String nbtName;
        AltitudeType(String nbtName) {
            this.nbtName = nbtName;
        }

        static AltitudeType fromNbtName(String nbtName) throws InvalidNbtException {
            for (AltitudeType type : AltitudeType.values()) {
                if (type.nbtName.equals(nbtName)) {
                    return type;
                }
            }

            throw new InvalidNbtException("Unknown altitude type: %s".formatted(nbtName));
        }
    }

    public static LandingMinimums readFromNbt(NbtCompound compound) {
        return new LandingMinimums(
                AltitudeType.fromNbtName(compound.getString("AltitudeType")),
                compound.getInt("Altitude")
        );
    }
}