package ru.octol1ttle.flightassistant.computers.safety;

import net.minecraft.client.MinecraftClient;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.ComputerHost;
import ru.octol1ttle.flightassistant.computers.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.TimeComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;

import java.awt.Color;

public class ChunkStatusComputer implements ITickableComputer {

    private final ComputerHost host;
    private final MinecraftClient mc;
    private final AirDataComputer data;
    private final TimeComputer time;

    public final float recoverPitch = 10f;
    private boolean isInWarning;
    private boolean isLoaded;
    private float lastEncounteredMS = 0f;
    private float lastDiffMS = 0f;
    private float offsetMS = 0f; // for single player pause

    // milliseconds difference
    private static final float WARN_THRESHOLD = 3200f;

    public ChunkStatusComputer(ComputerHost host, MinecraftClient mc, AirDataComputer data, TimeComputer time) {
        this.host = host;
        this.mc = mc;
        this.data = data;
        this.time = time;
    }

    @Override
    public void tick() {
        if (!data.isFlying()) {
            return;
        }

        isLoaded = data.isCurrentChunkLoaded;

        if (isLoaded) {
            if (!host.faulted.contains(time) && time.prevMillis != null) {
                lastEncounteredMS = time.prevMillis;
            }
            isLoaded = false;
            offsetMS = 0f;
        }

        final boolean isSinglePlayerPause = (mc.isInSingleplayer() && mc.isPaused());
        if (isSinglePlayerPause && !isLoaded) {
            offsetMS = ((time.prevMillis - lastEncounteredMS) - lastDiffMS);
        }

        if (time.prevMillis != null && lastEncounteredMS > 0f) {
            lastDiffMS = (time.prevMillis - offsetMS) - lastEncounteredMS;
        }

        isInWarning = shouldWarn();
    }

    @Override
    public String getId() {
        return "chunk_state";
    }

    @Override
    public void reset() {
        isInWarning = false;
        isLoaded = true;
        lastDiffMS = 0f;
        lastEncounteredMS = 0f;
        offsetMS = 0f;
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

    public Color getIndicator() {
        return FAConfig.indicator().warningColor;
    }

    private boolean shouldWarn() {
        if (data.isFlying() && !data.isCurrentChunkLoaded) {
            return lastDiffMS >= WARN_THRESHOLD;
        }
        return false;
    }
}
