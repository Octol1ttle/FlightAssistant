package ru.octol1ttle.flightassistant.computers.safety;

import net.minecraft.client.MinecraftClient;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.TimeComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;

public class ChunkStatusComputer implements ITickableComputer {
    private final MinecraftClient mc;
    private final AirDataComputer data;
    private final TimeComputer time;

    private boolean isInWarning;
    private float lastEncounteredMS = 0f;
    private float lastDiffMS = 0f;
    private float offsetMS = 0f; // for single player pause

    // milliseconds difference
    private static final float WARN_THRESHOLD = 3200f;

    public ChunkStatusComputer(MinecraftClient mc, AirDataComputer data, TimeComputer time) {
        this.mc = mc;
        this.data = data;
        this.time = time;
    }

    @Override
    public void tick() {
        if (!data.isFlying()) {
            reset();
            return;
        }

        if (data.isCurrentChunkLoaded) {
            if (time.prevMillis != null) {
                lastEncounteredMS = time.prevMillis;
            }
            offsetMS = 0f;
        }

        final boolean isSinglePlayerPause = (mc.isInSingleplayer() && mc.isPaused());
        if (isSinglePlayerPause && !data.isCurrentChunkLoaded) {
            offsetMS = ((time.prevMillis - lastEncounteredMS) - lastDiffMS);
        }

        if (time.prevMillis != null && lastEncounteredMS > 0f) {
            lastDiffMS = (time.prevMillis - offsetMS) - lastEncounteredMS;
        }

        isInWarning = shouldWarn();
    }

    public boolean shouldCorrectTerrain() {
        return FAConfig.computer().unloadedChunkProtection.recover() && isInWarning();
    }

    public boolean isInWarning() {
        return isInWarning;
    }

    public float getLastDiffMS() {
        return lastDiffMS;
    }

    private boolean shouldWarn() {
        if (data.isFlying() && !data.isCurrentChunkLoaded) {
            return lastDiffMS >= WARN_THRESHOLD;
        }
        return false;
    }

    @Override
    public String getId() {
        return "chunk_state";
    }

    @Override
    public void reset() {
        isInWarning = false;
        lastDiffMS = 0f;
        lastEncounteredMS = 0f;
        offsetMS = 0f;
    }
}
