package net.torocraft.flighthud.components;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.WorldChunk;
import net.torocraft.flighthud.*;

import java.util.List;

import static net.torocraft.flighthud.FlightHud.CONFIG_SETTINGS;
import static net.torocraft.flighthud.FlightHud.LOGGER;

public class FlightStatusIndicator extends HudComponent {
    public static final SoundEvent MASTER_WARNING = SoundEvent.of(new Identifier("flighthud:master_warning"));
    public static final SoundEvent MASTER_CAUTION = SoundEvent.of(new Identifier("flighthud:master_caution"));
    public static final SoundEvent STICK_SHAKER = SoundEvent.of(new Identifier("flighthud:stick_shaker"));
    public static final SoundEvent SINKRATE = SoundEvent.of(new Identifier("flighthud:sinkrate"));
    public static final SoundEvent TERRAIN = SoundEvent.of(new Identifier("flighthud:terrain"));
    public static final SoundEvent PULL_UP = SoundEvent.of(new Identifier("flighthud:pull_up"));
    public static final SoundEvent AUTOPILOT_DISCONNECT = SoundEvent.of(new Identifier("flighthud:autopilot_disconnect"));

    public static boolean flightDirectorsEnabled = false;
    public static long currentTimeMs;

    private final List<SoundEvent> activeEvents = new ObjectArrayList<>(4);
    private final Dimensions dim;
    private final FlightComputer computer;

    public FlightStatusIndicator(FlightComputer computer, Dimensions dim) {
        this.dim = dim;
        this.computer = computer;
    }

    @Override
    public void render(MatrixStack m, float partial, MinecraftClient mc) {
        if (mc.player == null || !mc.player.isFallFlying()) return;
        FlightSafetyMonitor.deltaTime = Math.min(1, (Util.getMeasuringTimeMs() - currentTimeMs) * 0.001f);
        currentTimeMs = Util.getMeasuringTimeMs();

        float x = dim.lFrame + 5;
        float xRight = dim.rFrame - 5;
        float y = dim.tFrame - 5;
        float yRight = y;

        // Left-side ECAM
        if (!FlightSafetyMonitor.unsafeFireworkHands.isEmpty()) {
            playRepeating(mc, MASTER_WARNING, 0.5f);
            for (Hand hand : FlightSafetyMonitor.unsafeFireworkHands) {
                String handStr = hand.toString().replace('_', ' ');
                drawFont(mc, m, handStr + " FRWKS UNSAFE", x, y += 10, CONFIG.alertColor);
                drawFont(mc, m, " -" + handStr + " FRWKS: REPLACE", x, y += 10, CONFIG.adviceColor);
            }
        }

        if (FlightSafetyMonitor.isElytraLow) {
            playRepeating(mc, MASTER_WARNING, 0.5f);
            drawFont(mc, m, "ELYTRA HEALTH LOW", x, y += 10, CONFIG.alertColor);
            drawFont(mc, m, " -ELYTRA: REPLACE", x, y += 10, CONFIG.adviceColor);
        }

        if (!FlightSafetyMonitor.thrustSet && currentTimeMs - FlightSafetyMonitor.lastFireworkActivationTimeMs > 1000) {
            playRepeating(mc, MASTER_WARNING, 0.5f);
            drawFont(mc, m, "FRWK ACTIVATION FAIL", x, y += 10, CONFIG.alertColor);
            if (!mc.isInSingleplayer()) {
                drawFont(mc, m, " DISCONNECT ASAP", x, y += 10, CONFIG.adviceColor);
                drawFont(mc, m, " -SERVER LIST LATENCY: CHECK", x, y += 10, CONFIG.adviceColor);
                drawFont(mc, m, " MAX LATENCY: 250 MS", x, y += 10, CONFIG.adviceColor);
            }
        }

        if (!FlightSafetyMonitor.flightProtectionsEnabled) {
            playOnce(mc, MASTER_CAUTION, 0.75f);
            drawFont(mc, m, "F/CTL ALTN LAW (PROT LOST)", x, y += 10, CONFIG.amberColor);
            drawFont(mc, m, " MAX PITCH: 40* UP", x, y += 10, CONFIG.adviceColor);
            drawFont(mc, m, " MIN V/S: -8 BPS", x, y += 10, CONFIG.adviceColor);
            drawFont(mc, m, " -FRWK NBT: CHECK", x, y += 10, CONFIG.adviceColor);
            drawFont(mc, m, " MANEUVER WITH CARE", x, y += 10, CONFIG.adviceColor);
        }

        if (FlightSafetyMonitor.radioAltFault) {
            playOnce(mc, MASTER_CAUTION, 0.75f);
            drawFont(mc, m, "NAV GPWS FAULT", x, y += 10, CONFIG.amberColor);
            if (isPosLoaded(mc.player))
                drawFont(mc, m, " MIN ALT: -60", x, y += 10, CONFIG.adviceColor);
            else {
                drawFont(mc, m, " MAX G/S: 15 BPS", x, y += 10, CONFIG.adviceColor);
                drawFont(mc, m, " WAIT FOR CHUNK LOAD", x, y += 10, CONFIG.adviceColor);
                drawFont(mc, m, " -REJOIN: CONSIDER", x, y += 10, CONFIG.adviceColor);
            }
        }

        if (FlightSafetyMonitor.terrainDetectionFault) {
            playOnce(mc, MASTER_CAUTION, 0.75f);
            drawFont(mc, m, "NAV GPWS TERR DET FAULT", x, y += 10, CONFIG.amberColor);
            if (!FlightSafetyMonitor.radioAltFault) {
                drawFont(mc, m, " MAX G/S: 15 BPS", x, y += 10, CONFIG.adviceColor);
                drawFont(mc, m, " WAIT FOR CHUNK LOAD", x, y += 10, CONFIG.adviceColor);
                drawFont(mc, m, " -REJOIN: CONSIDER", x, y += 10, CONFIG.adviceColor);
            }
        }

        if (AutoFlightManager.autoThrustEnabled && FlightSafetyMonitor.usableFireworkHand == null) {
            playOnce(mc, MASTER_CAUTION, 0.75f);
            drawFont(mc, m, "AUTO FLT A/THR LIMITED", x, y += 10, CONFIG.amberColor);
            drawFont(mc, m ," -FRWKS: SELECT", x, y += 10, CONFIG.adviceColor);
        }

        // Right-side ECAM
        if (activeEvents.contains(MASTER_WARNING))
            drawRightAlignedFont(mc, m, "MSTR WARN", xRight, yRight += 10, CONFIG.alertColor);
        if (activeEvents.contains(MASTER_CAUTION))
            drawRightAlignedFont(mc, m, "MSTR CAUT", xRight, yRight += 10, CONFIG.amberColor);
        if (!CONFIG_SETTINGS.gpws || FlightSafetyMonitor.gpwsLampColor != CONFIG.color)
            drawRightAlignedFont(mc, m, "GPWS", xRight, yRight += 10, CONFIG_SETTINGS.gpws ? FlightSafetyMonitor.gpwsLampColor : CONFIG.blankColor);
        if (flightDirectorsEnabled) {
            drawRightAlignedFont(mc, m, "FD", xRight, yRight += 10, CONFIG.color);

            // Flight directors
            if (AutoFlightManager.targetPitch != null) {
                float deltaPitch = computer.pitch + AutoFlightManager.targetPitch;
                float fdY = Math.max(dim.tFrame, Math.min(dim.bFrame, dim.yMid + i(deltaPitch * dim.degreesPerPixel)));
                drawHorizontalLine(m, dim.xMid - dim.wFrame * 0.15f, dim.xMid + dim.wFrame * 0.15f, fdY, CONFIG.adviceColor);
            }

            if (AutoFlightManager.targetHeading != null) {
                float deltaHeading = wrapHeading(AutoFlightManager.targetHeading) - wrapHeading(computer.heading);
                if (deltaHeading < -180) {
                    deltaHeading += 360;
                }

                float fdX = Math.max(dim.lFrame, Math.min(dim.rFrame, dim.xMid + i(deltaHeading * dim.degreesPerPixel)));
                drawVerticalLine(m, fdX, dim.yMid - dim.hFrame * 0.15f, dim.yMid + dim.hFrame * 0.15f, CONFIG.adviceColor);
            }
        }
        if (AutoFlightManager.distanceToTarget != null) {
            double time = AutoFlightManager.distanceToTarget / computer.velocityPerSecond.length();
            drawRightAlignedFont(mc, m, String.format("DISTANCE: %.1f (%.1f SEC)", AutoFlightManager.distanceToTarget, time), xRight, yRight += 10, CONFIG.color);
        }
        drawCenteredFont(mc, m, AutoFlightManager.statusString, dim.wScreen, dim.tFrame + 5, CONFIG.color);

        // Aural alerts
        if (!FlightSafetyMonitor.isElytraLow && FlightSafetyMonitor.unsafeFireworkHands.isEmpty() && (FlightSafetyMonitor.thrustSet || currentTimeMs - FlightSafetyMonitor.lastFireworkActivationTimeMs <= 1000))
            stopEvent(mc, MASTER_WARNING);
        if (FlightSafetyMonitor.flightProtectionsEnabled && !FlightSafetyMonitor.radioAltFault && !FlightSafetyMonitor.terrainDetectionFault && (!AutoFlightManager.autoThrustEnabled || FlightSafetyMonitor.usableFireworkHand != null))
            stopEvent(mc, MASTER_CAUTION);

        if (FlightSafetyMonitor.isStalling && CONFIG_SETTINGS.stickShaker && computer.velocityPerSecond.y <= -10) {
            playRepeating(mc, STICK_SHAKER, 0.75f);
            drawRightAlignedFont(mc, m, "STALL", xRight, yRight += 10, CONFIG.alertColor);
        } else stopEvent(mc, STICK_SHAKER);

        if (FlightSafetyMonitor.secondsUntilGroundImpact <= FlightSafetyMonitor.warningThreshold || FlightSafetyMonitor.secondsUntilTerrainImpact <= FlightSafetyMonitor.warningThreshold)
            playRepeating(mc, PULL_UP, 0.75f);
        else if (FlightSafetyMonitor.secondsUntilTerrainImpact <= FlightSafetyMonitor.cautionThreshold)
            playOnce(mc, TERRAIN, 0.75f);
        else if (FlightSafetyMonitor.secondsUntilGroundImpact <= FlightSafetyMonitor.cautionThreshold)
            playOnce(mc, SINKRATE, 0.75f);
        else
            stopGpwsEvents(mc);
    }

    public static boolean isPosLoaded(PlayerEntity player) {
        int i = ChunkSectionPos.getSectionCoord(player.getX());
        int j = ChunkSectionPos.getSectionCoord(player.getZ());
        WorldChunk worldChunk = player.world.getChunkManager().getWorldChunk(i, j);
        return worldChunk != null && worldChunk.getLevelType() != ChunkHolder.LevelType.INACCESSIBLE;
    }

    public void tryStopEvents(PlayerEntity player, SoundManager manager) {
        if (!activeEvents.isEmpty() && !player.isFallFlying()) {
            for (SoundEvent event : activeEvents)
                manager.stopSounds(event.getId(), SoundCategory.MASTER);
            activeEvents.clear();
        }
    }

    private void playOnce(MinecraftClient mc, SoundEvent event, float volume) {
        if (!activeEvents.contains(event)) {
            mc.getSoundManager().play(new AlertSoundInstance(event, volume, mc.player, false));
            activeEvents.add(event);
        }
    }

    private void playRepeating(MinecraftClient mc, SoundEvent event, float volume) {
        if (!activeEvents.contains(event)) {
            mc.getSoundManager().play(new AlertSoundInstance(event, volume, mc.player, true));
            activeEvents.add(event);
        }
    }

    private void stopGpwsEvents(MinecraftClient mc) {
        stopEvent(mc, TERRAIN);
        stopEvent(mc, SINKRATE);
        stopEvent(mc, PULL_UP);
    }

    private void stopEvent(MinecraftClient mc, SoundEvent event) {
        mc.getSoundManager().stopSounds(event.getId(), SoundCategory.MASTER);
        if (activeEvents.remove(event))
            LOGGER.info("Alert stopped: {}", event.getId().getPath());
    }
}
