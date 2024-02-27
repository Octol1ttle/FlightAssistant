package ru.octol1ttle.flightassistant.computers.autoflight;

import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;
import ru.octol1ttle.flightassistant.FAMathHelper;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.computers.safety.GPWSComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;



public class AutoFlightComputer implements ITickableComputer {
    private final AirDataComputer data;
    private final GPWSComputer gpws;
    private final FlightPlanner plan;
    private final FireworkController firework;
    private final PitchController pitch;
    private final YawController yaw;

    public boolean flightDirectorsEnabled = false;
    public boolean autoFireworkEnabled = false;
    public boolean autoPilotEnabled = false;

    public boolean afrwkDisconnectionForced = false;
    public boolean apDisconnectionForced = false;

    public Integer selectedSpeed;
    public Integer selectedAltitude;
    public Integer selectedHeading;

    public AutoFlightComputer(AirDataComputer data, GPWSComputer gpws, FlightPlanner plan, FireworkController firework, PitchController pitch, YawController yaw) {
        this.data = data;
        this.gpws = gpws;
        this.plan = plan;
        this.firework = firework;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    @Override
    public void tick() {
        if (autoFireworkEnabled && gpws.getGPWSLampColor() == FAConfig.hud().frameColor) {
            Integer targetSpeed = getTargetSpeed();
            Integer targetAltitude = getTargetAltitude();
            if (targetSpeed != null) {
                if (data.speed < targetSpeed) {
                    firework.activateFirework(false);
                }
            } else if (targetAltitude != null && targetAltitude > data.altitude && data.pitch > 0 && data.velocity.y < 0.0f) {
                firework.activateFirework(false);
            }
        }

        pitch.targetPitch = autoPilotEnabled ? getTargetPitch() : null;
        yaw.targetHeading = autoPilotEnabled ? getTargetHeading() : null;
    }

    public @Nullable Integer getTargetSpeed() {
        return selectedSpeed != null ? selectedSpeed : plan.getManagedSpeed();
    }

    public @Nullable Integer getTargetAltitude() {
        return selectedAltitude != null ? selectedAltitude : plan.getManagedAltitude();
    }

    public @Nullable Float getTargetPitch() {
        if (getTargetAltitude() == null) {
            return null;
        }

        double altitudeDelta = getTargetAltitude() - data.altitude;

        double distance;
        Vector2d planPos = plan.getTargetPosition();
        if (planPos != null && selectedAltitude == null) {
            distance = Vector2d.distance(planPos.x, planPos.y, data.position.x, data.position.z);
        } else {
            distance = Math.max(10.0f, data.speed);
        }

        return FAMathHelper.toDegrees(MathHelper.atan2(altitudeDelta, distance));
    }

    public @Nullable Float getTargetHeading() {
        return selectedHeading != null ? Float.valueOf(selectedHeading) : plan.getManagedHeading();
    }

    public void disconnectAutopilot(boolean force) {
        if (autoPilotEnabled) {
            autoPilotEnabled = false;
            apDisconnectionForced = force;
        }
    }

    public void disconnectAutoFirework(boolean force) {
        if (autoFireworkEnabled) {
            autoFireworkEnabled = false;
            afrwkDisconnectionForced = force;
        }
    }

    @Override
    public String getId() {
        return "auto_flt";
    }

    @Override
    public void reset() {
        flightDirectorsEnabled = false;
        disconnectAutoFirework(true);
        disconnectAutopilot(true);

        pitch.targetPitch = null;
        yaw.targetHeading = null;
    }
}
