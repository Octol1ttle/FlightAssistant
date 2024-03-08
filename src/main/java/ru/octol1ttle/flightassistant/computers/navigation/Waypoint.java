package ru.octol1ttle.flightassistant.computers.navigation;

import net.minecraft.nbt.InvalidNbtException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;

public class Waypoint {
    private final Vector2d targetPosition;
    protected @Nullable Integer targetAltitude;
    private final @Nullable Integer targetSpeed;

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

    public NbtCompound writeToNbt(NbtCompound compound) {
        compound.putBoolean("IsLanding", false);

        compound.putDouble("TargetX", targetPosition().x);
        compound.putDouble("TargetZ", targetPosition().y);
        if (targetAltitude() != null) {
            compound.putInt("TargetAltitude", targetAltitude());
        }
        if (targetSpeed() != null) {
            compound.putInt("TargetSpeed", targetSpeed());
        }

        return compound;
    }
    
    public static Waypoint readFromNbt(NbtCompound compound) throws InvalidNbtException {
        if (compound.getBoolean("IsLanding")) {
            return LandingWaypoint.readFromNbt(compound);
        }

        Vector2d targetPosition = new Vector2d(compound.getDouble("TargetX"), compound.getDouble("TargetZ"));
        Integer targetAltitude = null;
        Integer targetSpeed = null;
        if (compound.contains("TargetAltitude", NbtElement.INT_TYPE)) {
            targetAltitude = compound.getInt("TargetAltitude");
        }
        if (compound.contains("TargetSpeed", NbtElement.INT_TYPE)) {
            targetSpeed = compound.getInt("TargetSpeed");
        }

        return new Waypoint(targetPosition, targetAltitude, targetSpeed);
    }
}
