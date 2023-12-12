package ru.octol1ttle.flightassistant.computers.autoflight;

import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.computers.safety.GPWSComputer;

import static ru.octol1ttle.flightassistant.HudComponent.CONFIG;

public class AutoFlightComputer implements ITickableComputer {
    private final AirDataComputer data;
    private final GPWSComputer gpws;
    private final FlightPlanner plan;
    private final FireworkController firework;

    public boolean flightDirectorsEnabled = false;
    public boolean autoThrustEnabled = false;
    public boolean autoPilotEnabled = false;

    public Integer selectedSpeed;
    public Integer selectedAltitude;
    public Integer selectedHeading;

    public AutoFlightComputer(AirDataComputer data, GPWSComputer gpws, FlightPlanner plan, FireworkController firework) {
        this.data = data;
        this.gpws = gpws;
        this.plan = plan;
        this.firework = firework;
    }

    public void tick() {
        if (autoThrustEnabled && gpws.getGPWSLampColor() == CONFIG.color) {
            Integer targetSpeed = getTargetSpeed();
            if (targetSpeed != null && data.speed < targetSpeed) {
                firework.activateFirework(false);
            }
        }
    }

    public @Nullable Integer getTargetSpeed() {
        return selectedSpeed != null ? selectedSpeed : plan.getManagedSpeed();
    }

    public @Nullable Integer getTargetAltitude() {
        return selectedAltitude != null ? selectedAltitude : plan.getManagedAltitude();
    }

    public @Nullable Double getTargetPitch() {
        if (getTargetAltitude() == null) {
            return null;
        }

        double altitudeDelta = getTargetAltitude() - data.altitude;
        double distance;

        Vector2d planPos = plan.getTargetPosition();
        if (planPos != null) {
            distance = Vector2d.distance(planPos.x, planPos.y, data.position.x, data.position.z);
        } else {
            distance = altitudeDelta;
        }

        return -Math.toDegrees(MathHelper.atan2(altitudeDelta, distance));
    }

    public @Nullable Double getTargetHeading() {
        return selectedHeading != null ? Double.valueOf(selectedHeading) : plan.getManagedHeading();
    }

    public void disconnectAutopilot(boolean force) {

    }

    public void disconnectAutoThrust(boolean force) {

    }

    @Override
    public String getId() {
        return "auto_flt";
    }

    @Override
    public void reset() {
        disconnectAutoThrust(true);
        disconnectAutopilot(true);
    }
}
