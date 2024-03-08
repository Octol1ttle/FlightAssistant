package ru.octol1ttle.flightassistant.computers.navigation;

import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;
import ru.octol1ttle.flightassistant.HudComponent;

public class LandingWaypoint extends Waypoint {
    public final LandingMinimums minimums;

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
            return Text.translatable("commands.flightassistant.waypoint_info_not_set");
        }

        return switch (minimums.type()) {
            case ABSOLUTE -> HudComponent.asText("%s", minimums.altitude());
            case ABOVE_GROUND -> HudComponent.asText("+%s", minimums.altitude());
        };
    }

    public void setTargetAltitude(Integer targetAltitude) {
        this.targetAltitude = targetAltitude;
    }
}
