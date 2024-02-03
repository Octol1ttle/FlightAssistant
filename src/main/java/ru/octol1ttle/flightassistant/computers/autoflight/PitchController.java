package ru.octol1ttle.flightassistant.computers.autoflight;

import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.FAMathHelper;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.TimeComputer;
import ru.octol1ttle.flightassistant.computers.safety.GPWSComputer;
import ru.octol1ttle.flightassistant.computers.safety.StallComputer;
import ru.octol1ttle.flightassistant.computers.safety.VoidLevelComputer;

public class PitchController implements ITickableComputer {
    public static final float CLIMB_PITCH = 55.0f;
    public static final float DESCEND_PITCH = -35.0f;
    private final AirDataComputer data;
    private final StallComputer stall;
    private final TimeComputer time;
    private final VoidLevelComputer voidLevel;
    private final GPWSComputer gpws;
    public Float targetPitch = null;

    public PitchController(AirDataComputer data, StallComputer stall, TimeComputer time, VoidLevelComputer voidLevel, GPWSComputer gpws) {
        this.data = data;
        this.stall = stall;
        this.time = time;
        this.voidLevel = voidLevel;
        this.gpws = gpws;
    }

    @Override
    public void tick() {
        if (!data.canAutomationsActivate()) {
            return;
        }

        if (data.pitch > stall.maximumSafePitch) {
            smoothSetPitch(stall.maximumSafePitch, time.deltaTime);
            return;
        }
        if (data.pitch < voidLevel.minimumSafePitch) {
            smoothSetPitch(voidLevel.minimumSafePitch, time.deltaTime);
            return;
        }

        if (gpws.shouldCorrectSinkrate()) {
            smoothSetPitch(90.0f, MathHelper.clamp(time.deltaTime / gpws.descentImpactTime, 0.001f, 1.0f));
        } else if (gpws.shouldCorrectTerrain()) {
            smoothSetPitch(FAMathHelper.toDegrees(MathHelper.atan2(gpws.terrainAvoidVector.y, gpws.terrainAvoidVector.x)), MathHelper.clamp(time.deltaTime / gpws.terrainImpactTime, 0.001f, 1.0f));
        } else {
            smoothSetPitch(targetPitch, time.deltaTime);
        }
    }

    /**
     * Smoothly changes the player's pitch to the specified pitch using the delta
     *
     * @param pitch Target pitch
     * @param delta Delta time, in seconds
     */
    public void smoothSetPitch(Float pitch, float delta) {
        if (pitch == null) {
            return;
        }

        float difference = pitch - data.pitch;

        float newPitch;
        if (Math.abs(difference) < 0.05f) {
            newPitch = pitch;
        } else {
            if (difference > 0) { // going UP
                pitch = MathHelper.clamp(pitch, -90.0f, Math.min(CLIMB_PITCH, stall.maximumSafePitch));
            }
            if (difference < 0) { // going DOWN
                pitch = MathHelper.clamp(pitch, Math.max(DESCEND_PITCH, voidLevel.minimumSafePitch), 90.0f);
            }

            newPitch = data.pitch + (pitch - data.pitch) * delta;
        }

        data.player.setPitch(-newPitch);
    }

    @Override
    public String getId() {
        return "pitch_ctl";
    }

    @Override
    public void reset() {
        targetPitch = null;
    }
}
