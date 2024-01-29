package ru.octol1ttle.flightassistant.computers.autoflight;

import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.computers.safety.GPWSComputer;
import ru.octol1ttle.flightassistant.computers.safety.WallCollisionComputer;

import static ru.octol1ttle.flightassistant.HudComponent.CONFIG;

public class AutoFlightComputer implements ITickableComputer {
    private final AirDataComputer data;
    private final GPWSComputer gpws;
    private final FlightPlanner plan;
    private final FireworkController firework;
    private final WallCollisionComputer collision;
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

    public AutoFlightComputer(AirDataComputer data, GPWSComputer gpws, FlightPlanner plan, FireworkController firework, WallCollisionComputer collision, PitchController pitch, YawController yaw) {
        this.data = data;
        this.gpws = gpws;
        this.plan = plan;
        this.firework = firework;
        this.collision = collision;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    @Override
    public void tick() {
        if (autoFireworkEnabled && !collision.collided && gpws.getGPWSLampColor() == CONFIG.color) {
            Integer targetSpeed = getTargetSpeed();
            // TODO: support A/FRWK when targetSpeed is null
            if (targetSpeed != null && data.speed < targetSpeed) {
                firework.activateFirework(false);
            }
        }

        if (autoPilotEnabled && gpws.isInDanger()) {
            disconnectAutopilot(true);
        }

        // TODO: the only question I want to ask is what the FUCK is wrong with this
        // TODO: passing a waypoint with A/P is like rolling a dice because this fucker can't actually follow the needed path
        // TODO: if you aren't lucky enough, it won't meet the requirements for passing a waypoint and it will NEVER recover itself
        // TODO: if an autopilot requires constant monitoring and intervention, who is gonna use it?
        // TODO: well tbf who would even use an autopilot in a block game where there are objectively better ways of travelling...
        // TODO: can't believe some people actually want to use this fork.
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
        if (autoPilotEnabled) {
            autoPilotEnabled = false;
            apDisconnectionForced = force;
        }
    }

    public void disconnectAutoFirework(boolean force) {
        if (autoFireworkEnabled) {
            autoFireworkEnabled = false;
            afrwkDisconnectionForced = force;
            collision.collided = false;
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
