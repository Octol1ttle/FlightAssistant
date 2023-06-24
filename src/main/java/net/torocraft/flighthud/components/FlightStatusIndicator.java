package net.torocraft.flighthud.components;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkSectionPos;
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

    private final List<SoundEvent> activeEvents = new ObjectArrayList<>(4);
    private final Dimensions dim;
    private final FlightComputer computer;

    private boolean lastAutopilotState = false;

    public FlightStatusIndicator(FlightComputer computer, Dimensions dim) {
        this.dim = dim;
        this.computer = computer;
    }

    public static boolean isPosLoaded(ClientChunkManager manager, PlayerEntity player) {
        int i = ChunkSectionPos.getSectionCoord(player.getX());
        int j = ChunkSectionPos.getSectionCoord(player.getZ());
        return manager.isChunkLoaded(i, j);
    }

    @Override
    public void render(DrawContext context, MinecraftClient mc) {
        if (mc.world == null || mc.player == null || !mc.player.isFallFlying()) return;

        float x = dim.lFrame + 5;
        float xRight = dim.rFrame - 5;
        float y = dim.tFrame - 5;
        float yRight = y;

        // Left-side ECAM - warnings
        if (!FlightSafetyMonitor.unsafeFireworkHands.isEmpty()) {
            playRepeating(mc, MASTER_WARNING, 0.5f);
            for (Hand hand : FlightSafetyMonitor.unsafeFireworkHands) {
                String handStr = hand.toString().replace('_', ' ');
                drawFont(mc, context, handStr + " FRWKS UNSAFE", x, y += 10, CONFIG.alertColor);
                drawFont(mc, context, " -" + handStr + " FRWKS: REPLACE", x, y += 10, CONFIG.adviceColor);
            }
        }

        if (FlightSafetyMonitor.isElytraLow) {
            playRepeating(mc, MASTER_WARNING, 0.5f);
            drawFont(mc, context, "ELYTRA HEALTH LOW", x, y += 10, CONFIG.alertColor);
            drawFont(mc, context, " -ELYTRA: REPLACE", x, y += 10, CONFIG.adviceColor);
        }

        if (!FlightSafetyMonitor.thrustSet && AutoFlightManager.lastUpdateTimeMs - FlightSafetyMonitor.lastFireworkActivationTimeMs > 1000) {
            playRepeating(mc, MASTER_WARNING, 0.5f);
            drawFont(mc, context, "FRWK ACTIVATION FAIL", x, y += 10, CONFIG.alertColor);
            if (AutoFlightManager.autoPilotEnabled || AutoFlightManager.flightDirectorsEnabled)
                drawFont(mc, context, " -AP+FD: OFF", x, y += 10, CONFIG.adviceColor);
            if (AutoFlightManager.autoThrustEnabled)
                drawFont(mc, context, " -ATHR: OFF", x, y += 10, CONFIG.adviceColor);
            drawFont(mc, context, " -PITCH: MAX SAFE UP", x, y += 10, CONFIG.adviceColor);
            drawFont(mc, context, " -FWRKS: DO NOT USE", x, y += 10, CONFIG.adviceColor);
            drawFont(mc, context, " SPD MAY BE UNREL", x, y += 10, CONFIG.adviceColor);
        }

        if (FlightSafetyMonitor.fireworkCount <= 0) {
            playRepeating(mc, MASTER_WARNING, 0.5f);
            drawFont(mc, context, "FWOB AT ZERO", x, y += 10, CONFIG.alertColor);
            if (AutoFlightManager.targetAltitude != null)
                drawFont(mc, context, " -AP ALT: RESET", x, y += 10, CONFIG.adviceColor);
            drawFont(mc, context, " -DIVERSION: INITIATE", x, y += 10, CONFIG.adviceColor);
            drawFont(mc, context, " OPT GLD PITCH: -2* DOWN", x, y += 10, CONFIG.adviceColor);
            drawFont(mc, context, " GLDG DIST: 100 BLKS/10 GND ALT", x, y += 10, CONFIG.adviceColor);
        }

        // Left-side ECAM - cautions
        if (!FlightSafetyMonitor.flightProtectionsEnabled) {
            playOnce(mc, MASTER_CAUTION, 0.75f);
            drawFont(mc, context, "F/CTL ALTN LAW (PROT LOST)", x, y += 10, CONFIG.amberColor);
            drawFont(mc, context, " MAX PITCH: 40* UP", x, y += 10, CONFIG.adviceColor);
            drawFont(mc, context, " MIN V/S: -8 BPS", x, y += 10, CONFIG.adviceColor);
            drawFont(mc, context, " -FRWK NBT: CHECK", x, y += 10, CONFIG.adviceColor);
            drawFont(mc, context, " MANEUVER WITH CARE", x, y += 10, CONFIG.adviceColor);
        }

        if (FlightSafetyMonitor.fireworkCount > 0 && FlightSafetyMonitor.fireworkCount < 24) {
            playOnce(mc, MASTER_CAUTION, 0.75f);
            drawFont(mc, context, "FWOB BELOW 24", x, y += 10, CONFIG.amberColor);
            if (AutoFlightManager.autoThrustEnabled)
                drawFont(mc, context, " -ATHR: OFF", x, y += 10, CONFIG.adviceColor);
            drawFont(mc, context, " -CLIMB: INITIATE", x, y += 10, CONFIG.adviceColor);
            drawFont(mc, context, " OPT CLB PITCH: 55* UP", x, y += 10, CONFIG.adviceColor);
        }

        if (FlightSafetyMonitor.radioAltFault) {
            playOnce(mc, MASTER_CAUTION, 0.75f);
            drawFont(mc, context, "NAV GPWS FAULT", x, y += 10, CONFIG.amberColor);
            if (isPosLoaded(mc.world.getChunkManager(), mc.player))
                drawFont(mc, context, " MIN ALT: -60", x, y += 10, CONFIG.adviceColor);
            else {
                drawFont(mc, context, " MAX G/S: 15 BPS", x, y += 10, CONFIG.adviceColor);
                drawFont(mc, context, " WAIT FOR CHUNK LOAD", x, y += 10, CONFIG.adviceColor);
                drawFont(mc, context, " -REJOIN: CONSIDER", x, y += 10, CONFIG.adviceColor);
            }
        }

        if (FlightSafetyMonitor.terrainDetectionFault) {
            playOnce(mc, MASTER_CAUTION, 0.75f);
            drawFont(mc, context, "NAV GPWS TERR DET FAULT", x, y += 10, CONFIG.amberColor);
            if (!FlightSafetyMonitor.radioAltFault) {
                drawFont(mc, context, " MAX G/S: 15 BPS", x, y += 10, CONFIG.adviceColor);
                drawFont(mc, context, " WAIT FOR CHUNK LOAD", x, y += 10, CONFIG.adviceColor);
                drawFont(mc, context, " -REJOIN: CONSIDER", x, y += 10, CONFIG.adviceColor);
            }
        }

        if (AutoFlightManager.autoThrustEnabled && FlightSafetyMonitor.usableFireworkHand == null) {
            playOnce(mc, MASTER_CAUTION, 0.75f);
            drawFont(mc, context, "AUTO FLT A/THR LIMITED", x, y += 10, CONFIG.amberColor);
            drawFont(mc, context, " -FRWKS: SELECT", x, y += 10, CONFIG.adviceColor);
        }

        // Right-side ECAM
        int fwobLampColor = FlightSafetyMonitor.fireworkCount > 0
                ? (FlightSafetyMonitor.fireworkCount < 24 ? CONFIG.amberColor : CONFIG.color)
                : CONFIG.alertColor;
        drawRightAlignedFont(mc, context, "FWOB: " + FlightSafetyMonitor.fireworkCount, xRight, yRight += 10, fwobLampColor);
        if (activeEvents.contains(MASTER_WARNING))
            drawRightAlignedFont(mc, context, "MSTR WARN", xRight, yRight += 10, CONFIG.alertColor);
        if (activeEvents.contains(MASTER_CAUTION))
            drawRightAlignedFont(mc, context, "MSTR CAUT", xRight, yRight += 10, CONFIG.amberColor);
        if (!CONFIG_SETTINGS.gpws || FlightSafetyMonitor.gpwsLampColor != CONFIG.color)
            drawRightAlignedFont(mc, context, "GPWS", xRight, yRight += 10, CONFIG_SETTINGS.gpws ? FlightSafetyMonitor.gpwsLampColor : CONFIG.blankColor);
        if (AutoFlightManager.flightDirectorsEnabled) {
            drawRightAlignedFont(mc, context, "FD", xRight, yRight += 10, CONFIG.color);

            // Flight directors
            if (AutoFlightManager.targetPitch != null) {
                float deltaPitch = computer.pitch + AutoFlightManager.targetPitch;
                float fdY = Math.max(dim.tFrame, Math.min(dim.bFrame, dim.yMid + i(deltaPitch * dim.degreesPerPixel)));
                drawHorizontalLine(context, dim.xMid - dim.wFrame * 0.15f, dim.xMid + dim.wFrame * 0.15f, fdY, CONFIG.adviceColor);
            }

            if (AutoFlightManager.targetHeading != null) {
                float deltaHeading = wrapHeading(AutoFlightManager.targetHeading) - wrapHeading(computer.heading);
                if (deltaHeading < -180) {
                    deltaHeading += 360;
                }

                float fdX = Math.max(dim.lFrame, Math.min(dim.rFrame, dim.xMid + i(deltaHeading * dim.degreesPerPixel)));
                drawVerticalLine(context, fdX, dim.yMid - dim.hFrame * 0.15f, dim.yMid + dim.hFrame * 0.15f, CONFIG.adviceColor);
            }
        }
        if (AutoFlightManager.distanceToTarget != null) {
            double time = AutoFlightManager.distanceToTarget / Math.max(1, computer.velocityPerSecond.horizontalLength());
            drawRightAlignedFont(mc, context, String.format("DIST: %.1f (%.1f SEC)", AutoFlightManager.distanceToTarget, time), xRight, yRight += 10, CONFIG.color);
        }
        drawCenteredFont(mc, context, AutoFlightManager.statusString, dim.wScreen, dim.tFrame + 5, CONFIG.color);

        // Aural alerts
        if (!FlightSafetyMonitor.isElytraLow && FlightSafetyMonitor.unsafeFireworkHands.isEmpty()
                && (FlightSafetyMonitor.thrustSet || AutoFlightManager.lastUpdateTimeMs - FlightSafetyMonitor.lastFireworkActivationTimeMs <= 1000)
                && FlightSafetyMonitor.fireworkCount > 0)
            stopEvent(mc, MASTER_WARNING);
        if (FlightSafetyMonitor.flightProtectionsEnabled && !FlightSafetyMonitor.radioAltFault
                && !FlightSafetyMonitor.terrainDetectionFault
                && (!AutoFlightManager.autoThrustEnabled || FlightSafetyMonitor.usableFireworkHand != null)
                && (FlightSafetyMonitor.fireworkCount <= 0 || FlightSafetyMonitor.fireworkCount > 24))
            stopEvent(mc, MASTER_CAUTION);

        if (lastAutopilotState) {
            if (!AutoFlightManager.autoPilotEnabled)
                playOnce(mc, AUTOPILOT_DISCONNECT, 1f);
            else stopEvent(mc, AUTOPILOT_DISCONNECT);
        }
        lastAutopilotState = AutoFlightManager.autoPilotEnabled;

        if (FlightSafetyMonitor.isStalling && CONFIG_SETTINGS.stickShaker && computer.velocityPerSecond.y <= -10) {
            playRepeating(mc, STICK_SHAKER, 0.75f);
            drawRightAlignedFont(mc, context, "STALL", xRight, yRight += 10, CONFIG.alertColor);
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
