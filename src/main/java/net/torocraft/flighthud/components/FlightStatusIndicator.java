package net.torocraft.flighthud.components;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
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
import net.torocraft.flighthud.alerts.PassengerDismountedAlert;
import net.torocraft.flighthud.alerts.ThrustLockedAlert;
import net.torocraft.flighthud.alerts.UnsafeFireworksAlert;
import net.torocraft.flighthud.shims.DrawContext;

import static net.torocraft.flighthud.AutoFlightManager.deltaTime;
import static net.torocraft.flighthud.AutoFlightManager.lastUpdateTimeMs;
import static net.torocraft.flighthud.FlightHud.CONFIG_SETTINGS;

public class FlightStatusIndicator extends HudComponent {
    public static final SoundEvent ALERT = SoundEvent.of(new Identifier("flightassistant:alert"));
    public static final SoundEvent STICK_SHAKER = SoundEvent.of(new Identifier("flightassistant:stick_shaker"));
    public static final SoundEvent STALL_WARNING = SoundEvent.of(new Identifier("flightassistant:stall_warning"));
    public static final SoundEvent SINKRATE = SoundEvent.of(new Identifier("flightassistant:sinkrate"));
    public static final SoundEvent TERRAIN = SoundEvent.of(new Identifier("flightassistant:terrain"));
    public static final SoundEvent PULL_UP = SoundEvent.of(new Identifier("flightassistant:pull_up"));
    public static final SoundEvent AUTOPILOT_DISCONNECT = SoundEvent.of(new Identifier("flightassistant:autopilot_disconnect"));
    public static final Alert[] registeredAlerts = {
            new AutoThrustLimitedAlert(), new ThrustLockedAlert(),
            new FlightProtectionsOffAlert(), new GPWSOffAlert(),
            new LowElytraHealthAlert(),
            new LowFireworksAlert(), new NoFireworksAlert(),
            new UnsafeFireworksAlert(), new FireworkActivationFailureAlert(),
            new PassengerDismountedAlert()
    };
    public static final List<Alert> activeAlerts = new ArrayList<>();
    private static long lastHighlightTimeMs = 0L;

    private final List<SoundEvent> activeEvents = new ObjectArrayList<>(2);
    private final Dimensions dim;
    private final FlightComputer computer;
    public boolean highlight = true;
    private boolean lastAutopilotState = false;
    private AlertSoundInstance stickShakerInstance;

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

        int fwobLampColor = FlightSafetyMonitor.fireworkCount > 0
                ? (FlightSafetyMonitor.fireworkCount < 24 ? CONFIG.amberColor : CONFIG.color)
                : CONFIG.alertColor;
        drawRightAlignedFont(mc, context, "FRWK CNT: " + FlightSafetyMonitor.fireworkCount, xRight, yRight += 9, fwobLampColor);
        // Right-side ECAM
        if (AutoFlightManager.flightDirectorsEnabled) {
            drawRightAlignedFont(mc, context, "FD", xRight, yRight += 9, CONFIG.color);

            // Flight directors
            if (AutoFlightManager.targetPitch != null) {
                float deltaPitch = computer.pitch + AutoFlightManager.targetPitch;
                float fdY = Math.max(dim.tFrame, Math.min(dim.bFrame, dim.yMid + i(deltaPitch * dim.degreesPerPixel)));
                drawHorizontalLine(context, dim.xMid - dim.wFrame * 0.1f, dim.xMid + dim.wFrame * 0.1f, fdY, CONFIG.adviceColor);
            }

            if (AutoFlightManager.targetHeading != null) {
                float deltaHeading = wrapHeading(AutoFlightManager.targetHeading) - wrapHeading(computer.heading);
                if (deltaHeading < -180.0f) {
                    deltaHeading += 360.0f;
                }
                if (deltaHeading > 180.0f) {
                    deltaHeading -= 360.0f;
                }

                float fdX = Math.max(dim.lFrame, Math.min(dim.rFrame, dim.xMid + i(deltaHeading * dim.degreesPerPixel)));
                drawVerticalLine(context, fdX, dim.yMid - dim.hFrame * 0.15f, dim.yMid + dim.hFrame * 0.15f, CONFIG.adviceColor);
            }
        }
        if (AutoFlightManager.distanceToTarget != null) {
            drawRightAlignedFont(mc, context, String.format("DIST: %.0f", AutoFlightManager.distanceToTarget), xRight, yRight + 9, CONFIG.color);
        }
        drawCenteredFont(mc, context, AutoFlightManager.statusString, dim.wScreen, dim.tFrame + 15, CONFIG.color);

        if (lastAutopilotState) {
            if (!AutoFlightManager.autoPilotEnabled)
                playOnce(mc, AUTOPILOT_DISCONNECT, 1f, false);
            else stopEvent(mc, AUTOPILOT_DISCONNECT);
        }
        lastAutopilotState = AutoFlightManager.autoPilotEnabled;

        tryPlayStickShaker(mc, context);

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
        HudComponent.drawUnbatched(() -> {
            if (highlight) {
                HudComponent.drawTextHighlight(mc.textRenderer, context, x, y, text, CONFIG.alertColor);
                HudComponent.drawCenteredFont(mc, context, text, width, y, CONFIG.white);
            } else {
                HudComponent.drawCenteredFont(mc, context, text, width, y, CONFIG.alertColor);
            }
        });
    }

    public void tryStopEvents(PlayerEntity player, SoundManager manager) {
        if (!player.isFallFlying()) {
            if (!activeEvents.isEmpty()) {
                for (SoundEvent event : activeEvents)
                    manager.stopSounds(event.getId(), SoundCategory.MASTER);
                activeEvents.clear();
            }
            if (stickShakerInstance != null) {
                manager.stop(stickShakerInstance);
                stickShakerInstance = null;
            }
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

    private void tryPlayStickShaker(MinecraftClient mc, DrawContext context) {
        if (FlightSafetyMonitor.isStalling) {
            if (CONFIG_SETTINGS.stickShaker) {
                if (stickShakerInstance == null) {
                    mc.getSoundManager().play(stickShakerInstance = new AlertSoundInstance(STICK_SHAKER, 0.0f, mc.player, true));
                }

                stickShakerInstance.setVolume(stickShakerInstance.getVolume() + deltaTime * 2.0f);
            }

            if (computer.velocityPerSecond.y <= -10) {
                playOnce(mc, STALL_WARNING, 1.0f, true);
            } else
                stopEvent(mc, STALL_WARNING);
            drawCenteredWarning(mc, context, dim.wScreen, dim.hScreen / 2 + 10, highlight, "STALL");
            return;
        }

        stopEvent(mc, STALL_WARNING);

        if (stickShakerInstance == null) {
            return;
        }

        stickShakerInstance.setVolume(stickShakerInstance.getVolume() - deltaTime * 2.0f);
        if (stickShakerInstance.getVolume() <= 0.0f) {
            mc.getSoundManager().stop(stickShakerInstance);
            stickShakerInstance = null;
        }
    }

    private void stopGpwsEvents(MinecraftClient mc) {
        stopEvent(mc, TERRAIN);
        stopEvent(mc, SINKRATE);
        stopEvent(mc, PULL_UP);
    }

    private void stopEvent(MinecraftClient mc, SoundEvent event) {
        mc.getSoundManager().stopSounds(event.getId(), SoundCategory.MASTER);
        activeEvents.remove(event);
    }
}
