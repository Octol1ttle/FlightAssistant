package net.torocraft.flighthud.components;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.torocraft.flighthud.AlertSoundInstance;
import net.torocraft.flighthud.AutoFlightManager;
import net.torocraft.flighthud.Dimensions;
import net.torocraft.flighthud.FlightComputer;
import net.torocraft.flighthud.FlightSafetyMonitor;
import net.torocraft.flighthud.HudComponent;
import net.torocraft.flighthud.alerts.Alert;
import net.torocraft.flighthud.alerts.AutoThrustLimitedAlert;
import net.torocraft.flighthud.alerts.FireworkActivationFailureAlert;
import net.torocraft.flighthud.alerts.FlightProtectionsOffAlert;
import net.torocraft.flighthud.alerts.GPWSOffAlert;
import net.torocraft.flighthud.alerts.LowElytraHealthAlert;
import net.torocraft.flighthud.alerts.LowFireworksAlert;
import net.torocraft.flighthud.alerts.NoFireworksAlert;
import net.torocraft.flighthud.alerts.ThrustLockedAlert;
import net.torocraft.flighthud.alerts.UnsafeFireworksAlert;

import java.util.ArrayList;
import java.util.List;

import static net.torocraft.flighthud.AutoFlightManager.lastUpdateTimeMs;
import static net.torocraft.flighthud.FlightHud.CONFIG_SETTINGS;
import static net.torocraft.flighthud.FlightHud.LOGGER;

public class FlightStatusIndicator extends HudComponent {
    public static final SoundEvent ALERT = SoundEvent.of(new Identifier("flighthud:alert"));
    public static final SoundEvent STICK_SHAKER = SoundEvent.of(new Identifier("flighthud:stick_shaker"));
    public static final SoundEvent SINKRATE = SoundEvent.of(new Identifier("flighthud:sinkrate"));
    public static final SoundEvent TERRAIN = SoundEvent.of(new Identifier("flighthud:terrain"));
    public static final SoundEvent PULL_UP = SoundEvent.of(new Identifier("flighthud:pull_up"));
    public static final SoundEvent AUTOPILOT_DISCONNECT = SoundEvent.of(new Identifier("flighthud:autopilot_disconnect"));
    public static final Alert[] registeredAlerts = {
            new AutoThrustLimitedAlert(), new ThrustLockedAlert(),
            new FlightProtectionsOffAlert(), new GPWSOffAlert(),
            new LowElytraHealthAlert(),
            new LowFireworksAlert(), new NoFireworksAlert(),
            new UnsafeFireworksAlert(), new FireworkActivationFailureAlert(),
    };
    public static final List<Alert> activeAlerts = new ArrayList<>();
    private static long lastHighlightTimeMs = 0L;
    @Deprecated
    private final List<SoundEvent> activeEvents = new ObjectArrayList<>(2);
    public boolean highlight = true;

    private final Dimensions dim;
    private final FlightComputer computer;

    private boolean lastAutopilotState = false;

    public FlightStatusIndicator(FlightComputer computer, Dimensions dim) {
        this.dim = dim;
        this.computer = computer;
    }

    @Override
    public void render(DrawContext context, MinecraftClient mc) {
        if (mc.world == null || mc.player == null || !mc.player.isFallFlying()) return;
        if (lastUpdateTimeMs - lastHighlightTimeMs >= 500) {
            highlight = !highlight;
            lastHighlightTimeMs = lastUpdateTimeMs;
        }

        float x = dim.lFrame + 5;
        float xRight = dim.rFrame - 5;
        float y = dim.tFrame + 15;
        float yRight = y - 10;

        for (Alert alert : registeredAlerts) {
            if (!activeAlerts.contains(alert) && alert.shouldActivate()) {
                playOnce(mc, ALERT, 0.75f, false);
                alert.hidden = false;
                activeAlerts.add(alert);
            }
        }

        for (Alert alert : activeAlerts) {
            if (alert.hidden) continue;
            y += 9 * alert.drawText(mc, context, x, y, highlight);
        }

        // Right-side ECAM
        if (AutoFlightManager.flightDirectorsEnabled) {
            drawRightAlignedFont(mc, context, "FD", xRight, yRight += 9, CONFIG.color);

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
            drawRightAlignedFont(mc, context, String.format("DIST: %.1f", AutoFlightManager.distanceToTarget), xRight, yRight += 9, CONFIG.color);
        }
        drawCenteredFont(mc, context, AutoFlightManager.statusString, dim.wScreen, dim.tFrame + 15, CONFIG.color);

        if (lastAutopilotState) {
            if (!AutoFlightManager.autoPilotEnabled)
                playOnce(mc, AUTOPILOT_DISCONNECT, 1f, false);
            else stopEvent(mc, AUTOPILOT_DISCONNECT);
        }
        lastAutopilotState = AutoFlightManager.autoPilotEnabled;

        if (FlightSafetyMonitor.isStalling && CONFIG_SETTINGS.stickShaker && computer.velocityPerSecond.y <= -10) {
            playRepeating(mc, STICK_SHAKER, 0.75f);
            drawCenteredWarning(mc, context, dim.wScreen, dim.hScreen / 2 + 10, highlight, "STALL");
        } else stopEvent(mc, STICK_SHAKER);

        if (FlightSafetyMonitor.secondsUntilGroundImpact <= FlightSafetyMonitor.warningThreshold || FlightSafetyMonitor.secondsUntilTerrainImpact <= FlightSafetyMonitor.warningThreshold) {
            playOnce(mc, PULL_UP, 0.75f, true);
            drawCenteredWarning(mc, context, dim.wScreen, dim.hScreen / 2 + 10, highlight, "PULL UP");
        } else if (FlightSafetyMonitor.secondsUntilTerrainImpact <= FlightSafetyMonitor.cautionThreshold)
            playOnce(mc, TERRAIN, 0.5f, true);
        else if (FlightSafetyMonitor.secondsUntilGroundImpact <= FlightSafetyMonitor.cautionThreshold)
            playOnce(mc, SINKRATE, 0.5f, true);
        else
            stopGpwsEvents(mc);
    }

    public void drawCenteredWarning(MinecraftClient mc, DrawContext context, float width, float y, boolean highlight, String text) {
        float x = (width - mc.textRenderer.getWidth(text)) / 2;
        if (highlight) {
            HudComponent.drawUnbatched(drawContext -> {
                HudComponent.drawTextHighlight(mc.textRenderer, context, x, y, text, CONFIG.alertColor);
                HudComponent.drawCenteredFont(mc, context, text, width, y, CONFIG.white);
            }, context);
            return;
        }
        HudComponent.drawCenteredFont(mc, context, text, width, y, CONFIG.alertColor);
    }

    public void tryStopEvents(PlayerEntity player, SoundManager manager) {
        if (!activeEvents.isEmpty() && !player.isFallFlying()) {
            for (SoundEvent event : activeEvents)
                manager.stopSounds(event.getId(), SoundCategory.MASTER);
            activeEvents.clear();
        }
        activeAlerts.removeIf(alert -> !alert.shouldActivate());
    }

    private void playOnce(MinecraftClient mc, SoundEvent event, float volume, boolean limit) {
        if (!limit || !activeEvents.contains(event)) {
            mc.getSoundManager().play(new AlertSoundInstance(event, volume, mc.player, false));
            if (limit)
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
