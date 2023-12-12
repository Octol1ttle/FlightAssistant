package ru.octol1ttle.flightassistant.computers.autoflight;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.IRenderTickableComputer;
import ru.octol1ttle.flightassistant.computers.TimeComputer;
import ru.octol1ttle.flightassistant.computers.safety.GPWSComputer;
import ru.octol1ttle.flightassistant.computers.safety.StallComputer;
import ru.octol1ttle.flightassistant.computers.safety.VoidLevelComputer;
import ru.octol1ttle.flightassistant.indicators.PitchIndicator;

public class PitchController implements IRenderTickableComputer {
    public static final float CLIMB_PITCH = -55.0f;
    private final AirDataComputer data;
    private final StallComputer stall;
    private final TimeComputer time;
    private final VoidLevelComputer voidLevel;
    private final GPWSComputer gpws;
    /**
     * USE MINECRAFT PITCH (minus is up and plus is down)
     **/
    public Float targetPitch = null;
    public boolean upsetRecover = false;

    public PitchController(AirDataComputer data, StallComputer stall, TimeComputer time, VoidLevelComputer voidLevel, GPWSComputer gpws) {
        this.data = data;
        this.stall = stall;
        this.time = time;
        this.voidLevel = voidLevel;
        this.gpws = gpws;
    }

    public void tick() {
        if (!data.canAutomationsActivate()) {
            return;
        }
        if (data.pitch > stall.maximumSafePitch) {
            smoothSetPitch(-stall.maximumSafePitch, time.deltaTime);
            return;
        }
        if (data.pitch < voidLevel.minimumSafePitch) {
            smoothSetPitch(-voidLevel.minimumSafePitch, time.deltaTime);
            return;
        }
        if (upsetRecover) {
            smoothSetPitch(CLIMB_PITCH, MathHelper.clamp(time.deltaTime / positiveMin(gpws.descentImpactTime, gpws.terrainImpactTime), 0.001f, 1.0f));
            return;
        }

        smoothSetPitch(targetPitch, time.deltaTime);
    }

    /**
     * Smoothly changes the player's pitch to the specified pitch using the delta
     *
     * @param pitch Target MINECRAFT pitch (- is up, + is down)
     * @param delta Delta time, in seconds
     */
    public void smoothSetPitch(Float pitch, float delta) {
        if (pitch == null) {
            return;
        }

        PlayerEntity player = data.player;
        float difference = pitch - player.getPitch();

        if (difference < 0) { // going UP
            pitch = MathHelper.clamp(pitch, -stall.maximumSafePitch, 90.0f);
        }
        if (difference > 0) { // going DOWN
            pitch = MathHelper.clamp(pitch, -90.0f, -PitchIndicator.DANGEROUS_DOWN_PITCH);
        }

        float newPitch = player.getPitch() + (pitch - player.getPitch()) * delta;

        player.setPitch(newPitch);
    }

    /**
     * Returns the lesser of the two provided numbers. If one of the numbers is less than zero, the other is returned instead.
     **/
    private float positiveMin(float a, float b) {
        if (a < 0.0f) {
            return b;
        }
        if (b < 0.0f) {
            return a;
        }

        return Math.min(a, b);
    }

    @Override
    public String getId() {
        return "pitch_ctl";
    }

    @Override
    public void reset() {
        targetPitch = null;
        upsetRecover = false;
    }
}
