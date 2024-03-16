package ru.octol1ttle.flightassistant.computers.autoflight;

import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.FAMathHelper;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.TimeComputer;
import ru.octol1ttle.flightassistant.computers.safety.ChunkStatusComputer;
import ru.octol1ttle.flightassistant.computers.safety.GPWSComputer;
import ru.octol1ttle.flightassistant.computers.safety.StallComputer;
import ru.octol1ttle.flightassistant.computers.safety.VoidLevelComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;

public class PitchController implements ITickableComputer {
    public static final float CLIMB_PITCH = 55.0f;
    public static final float GLIDE_PITCH = -2.2f;
    public static final float DESCEND_PITCH = -35.0f;
    private final AirDataComputer data;
    private final StallComputer stall;
    private final TimeComputer time;
    private final VoidLevelComputer voidLevel;
    private final GPWSComputer gpws;
    private final ChunkStatusComputer chunkStatus;
    public Float targetPitch = null;

    public PitchController(AirDataComputer data, StallComputer stall, TimeComputer time, VoidLevelComputer voidLevel, GPWSComputer gpws, ChunkStatusComputer chunkStatus) {
        this.data = data;
        this.stall = stall;
        this.time = time;
        this.voidLevel = voidLevel;
        this.gpws = gpws;
        this.chunkStatus = chunkStatus;
    }

    @Override
    public void tick() {
        if (!data.canAutomationsActivate()) {
            return;
        }

        if (FAConfig.computer().stallProtection.recover() && data.pitch() > 0.0f && data.pitch() > stall.maximumSafePitch) {
            smoothSetPitch(stall.maximumSafePitch, time.deltaTime);
        } else if (FAConfig.computer().voidProtection.recover() && data.pitch() < voidLevel.minimumSafePitch) {
            smoothSetPitch(voidLevel.minimumSafePitch, time.deltaTime);
        }

        if (gpws.shouldCorrectSinkrate()) {
            smoothSetPitch(90.0f, MathHelper.clamp(time.deltaTime / gpws.descentImpactTime, 0.001f, 1.0f));
        } else if (gpws.shouldCorrectTerrain()) {
            smoothSetPitch(FAMathHelper.toDegrees(MathHelper.atan2(gpws.terrainAvoidVector.y, gpws.terrainAvoidVector.x)), time.deltaTime);
        } else if (chunkStatus.shouldCorrectTerrain()) {
            smoothSetPitch(chunkStatus.recoverPitch, time.deltaTime);
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

        float difference = pitch - data.pitch();

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

            newPitch = data.pitch() + (pitch - data.pitch()) * delta;
        }

        data.player().setPitch(-newPitch);
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
