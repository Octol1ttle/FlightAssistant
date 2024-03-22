package ru.octol1ttle.flightassistant.computers.navigation;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;

public class LandingWaypoint extends Waypoint {
    public final @Nullable LandingMinimums minimums;

    public LandingWaypoint(Vector2d targetPosition, @Nullable LandingMinimums minimums) {
        super(targetPosition, null, null);
        this.minimums = minimums;
    }

    public Integer minimums(int ground) {
        if (minimums == null) {
            return null;
        }

        return switch (minimums.type()) {
            case ABSOLUTE -> minimums.altitude();
            case ABOVE_GROUND -> ground + minimums.altitude();
        };
    }

    public Text formatMinimums() {
        if (minimums == null) {
            return Text.translatable("mode.flightassistant.minimums.not_set");
        }

        return switch (minimums.type()) {
            case ABSOLUTE -> Text.translatable("mode.flightassistant.minimums.absolute", minimums.altitude());
            case ABOVE_GROUND -> Text.translatable("mode.flightassistant.minimums.relative", minimums.altitude());
        };
    }

    @Override
    public NbtCompound writeToNbt(NbtCompound compound) {
        compound.putBoolean("IsLanding", true);

        compound.putDouble("TargetX", targetPosition().x);
        compound.putDouble("TargetZ", targetPosition().y);
        if (minimums != null) {
            NbtCompound minimumsNbt = new NbtCompound();
            minimumsNbt.putString("AltitudeType", minimums.type().nbtName);
            minimumsNbt.putInt("Altitude", minimums.altitude());

            compound.put("Minimums", minimumsNbt);
        }

        return compound;
    }

    public static LandingWaypoint readFromNbt(NbtCompound compound) {
        Vector2d targetPosition = new Vector2d(compound.getDouble("TargetX"), compound.getDouble("TargetZ"));
        LandingMinimums minimums = null;
        if (compound.contains("Minimums", NbtElement.COMPOUND_TYPE)) {
            minimums = LandingMinimums.readFromNbt(compound.getCompound("Minimums"));
        }

        return new LandingWaypoint(targetPosition, minimums);
    }
}
