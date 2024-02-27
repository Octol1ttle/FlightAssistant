package ru.octol1ttle.flightassistant.computers.navigation;

import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;
import ru.octol1ttle.flightassistant.HudComponent;

public class LandingWaypoint extends Waypoint {
    private final LandingMinimums landingMinimums;

    public LandingWaypoint(Vector2d targetPosition, @Nullable LandingMinimums landingMinimums) {
        super(targetPosition, null, null);
        this.landingMinimums = landingMinimums;
    }

    public Integer minimums(int ground) {
        if (landingMinimums == null) {
            return null;
        }

        return switch (landingMinimums.type()) {
            case ABSOLUTE -> landingMinimums.altitude();
            case ABOVE_GROUND -> ground + landingMinimums.altitude();
        };
    }

    public Text formatMinimums() {
        if (landingMinimums == null) {
            return Text.translatable("commands.flightassistant.waypoint_info_not_set");
        }

        return switch (landingMinimums.type()) {
            case ABSOLUTE -> HudComponent.asText("%s", landingMinimums.altitude());
            case ABOVE_GROUND -> HudComponent.asText("+%s", landingMinimums.altitude());
        };
    }

    @Override
    public Waypoint setTargetAltitude(Integer targetAltitude) {
        this.targetAltitude = targetAltitude;
        return this;
    }
}
