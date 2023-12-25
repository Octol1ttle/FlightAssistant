package ru.octol1ttle.flightassistant.computers.autoflight;

import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
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

    public void tick() {
        if (autoFireworkEnabled && gpws.getGPWSLampColor() == CONFIG.color) {
            Integer targetSpeed = getTargetSpeed();
            // TODO: support A/FRWK when targetSpeed is null
            if (targetSpeed != null && data.speed < targetSpeed) {
                firework.activateFirework(false);
            }
        }

        pitch.targetPitch = autoPilotEnabled ? getTargetPitch() : null;
        yaw.targetHeading = autoPilotEnabled ? getTargetHeading() : null;

        // TODO: disconnect A/P on player input
    }

    public @Nullable Integer getTargetSpeed() {
        return selectedSpeed != null ? selectedSpeed : plan.getManagedSpeed();
    }

    public @Nullable Integer getTargetAltitude() {
        return selectedAltitude != null ? selectedAltitude : plan.getManagedAltitude();
    }

    public @Nullable Float getTargetPitch() {
        if (selectedAltitude == null) {
            return plan.getManagedPitch();
        }

        return (float) (-Math.toDegrees(MathHelper.atan2(selectedAltitude - data.altitude, 35.0f * 3.0f)));
    }

    public @Nullable Float getTargetHeading() {
        return selectedHeading != null ? Float.valueOf(selectedHeading) : plan.getManagedHeading();
    }

    public void disconnectAutopilot(boolean force) {
        autoPilotEnabled = false;
        apDisconnectionForced = force;
    }

    public void disconnectAutoFirework(boolean force) {
        autoFireworkEnabled = false;
        afrwkDisconnectionForced = force;
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
