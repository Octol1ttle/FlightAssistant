package ru.octol1ttle.flightassistant.indicators;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.joml.Vector2d;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.TimeComputer;
import ru.octol1ttle.flightassistant.computers.autoflight.AutoFlightComputer;
import ru.octol1ttle.flightassistant.computers.autoflight.FireworkController;
import ru.octol1ttle.flightassistant.computers.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.computers.safety.WallCollisionComputer;

public class FlightModeIndicator extends HudComponent {
    private final Dimensions dim;
    private final FireworkController firework;
    private final TimeComputer time;
    private final AutoFlightComputer autoflight;
    private final FlightPlanner plan;
    private final AirDataComputer data;
    private final WallCollisionComputer collision;

    private final FlightMode fireworkMode;
    private final FlightMode verticalMode;
    private final FlightMode lateralMode;
    private final FlightMode automationMode;

    public FlightModeIndicator(Dimensions dim, FireworkController firework, TimeComputer time, AutoFlightComputer autoflight, FlightPlanner plan, AirDataComputer data, WallCollisionComputer collision) {
        this.dim = dim;
        this.firework = firework;
        this.time = time;
        this.autoflight = autoflight;
        this.plan = plan;
        this.data = data;
        this.collision = collision;

        this.fireworkMode = new FlightMode(this.time);
        this.verticalMode = new FlightMode(this.time);
        this.lateralMode = new FlightMode(this.time);
        this.automationMode = new FlightMode(this.time);
    }

    // TODO: consider delegating mode update tasks to a computer
    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        if (time.prevMillis == null) {
            renderFaulted(context, textRenderer);
            return;
        }
        renderFireworkMode(context, textRenderer);
        renderVerticalMode(context, textRenderer);
        renderLateralMode(context, textRenderer);
        renderAutomationMode(context, textRenderer);
    }

    private void renderFireworkMode(DrawContext context, TextRenderer textRenderer) {
        Integer targetSpeed = autoflight.getTargetSpeed();
        if (firework.noFireworks) {
            fireworkMode.update(Text.translatable("flightassistant.mode.firework.none_in_hotbar"), autoflight.autoFireworkEnabled);
        } else if (collision.collided) {
            fireworkMode.update(Text.translatable("flightassistant.mode.firework.locked"), true);
        } else if (firework.lastProtTrigger != null && time.prevMillis - firework.lastProtTrigger < 2000) {
            fireworkMode.update(Text.translatable("flightassistant.mode.firework.protection"), true);
        } else if (autoflight.autoFireworkEnabled && targetSpeed != null) {
            String type = autoflight.selectedSpeed != null ? ".selected" : ".managed";
            fireworkMode.update(Text.translatable("flightassistant.mode.firework.speed" + type, targetSpeed));
        } else {
            fireworkMode.update(Text.translatable("flightassistant.mode.firework.manual"), autoflight.autoPilotEnabled);
        }

        float x = dim.lFrame + dim.wFrame * (1 / 5.0f);
        float y = dim.bFrame + 10;
        fireworkMode.render(context, textRenderer, x, y);
    }

    private void renderVerticalMode(DrawContext context, TextRenderer textRenderer) {
        Integer targetAltitude = autoflight.getTargetAltitude();
        if (targetAltitude == null) {
            return;
        }

        float diff = Math.abs(targetAltitude - data.altitude);
        String type = autoflight.selectedAltitude != null ? ".selected" : ".managed";

        if (!autoflight.autoPilotEnabled || diff <= 5) {
            verticalMode.update(Text.translatable("flightassistant.mode.vert.alt_hold" + type, targetAltitude));
        } else if (diff <= 10) {
            verticalMode.update(Text.translatable("flightassistant.mode.vert.alt_approaching" + type, targetAltitude));
        } else if (targetAltitude > data.altitude) {
            verticalMode.update(Text.translatable("flightassistant.mode.vert.climb" + type, targetAltitude));
        } else {
            verticalMode.update(Text.translatable("flightassistant.mode.vert.descend" + type, targetAltitude));
        }

        float x = dim.lFrame + dim.wFrame * (2 / 5.0f);
        float y = dim.bFrame + 10;
        verticalMode.render(context, textRenderer, x, y);
    }

    private void renderLateralMode(DrawContext context, TextRenderer textRenderer) {
        if (autoflight.getTargetHeading() == null) {
            return;
        }

        if (autoflight.selectedHeading != null) {
            lateralMode.update(Text.translatable("flightassistant.mode.lat.heading", autoflight.selectedHeading));
        } else if (plan.getTargetPosition() != null) {
            Vector2d target = plan.getTargetPosition();
            lateralMode.update(Text.translatable("flightassistant.mode.lat.position", target.x, target.y));
        }

        float x = dim.lFrame + dim.wFrame * (3 / 5.0f);
        float y = dim.bFrame + 10;
        lateralMode.render(context, textRenderer, x, y);
    }

    private void renderAutomationMode(DrawContext context, TextRenderer textRenderer) {
        MutableText automationStatus = Text.literal("");
        if (autoflight.flightDirectorsEnabled) {
            appendWithSeparation(automationStatus, Text.translatable("flightassistant.flight_directors_enabled"));
        }
        if (autoflight.autoFireworkEnabled) {
            appendWithSeparation(automationStatus, Text.translatable("flightassistant.auto_firework_enabled"));
        }
        if (autoflight.autoPilotEnabled) {
            appendWithSeparation(automationStatus, Text.translatable("flightassistant.auto_pilot_enabled"));
        }
        if (automationStatus.getSiblings().isEmpty()) {
            return;
        }

        automationMode.update(automationStatus);

        float x = dim.lFrame + dim.wFrame * (4 / 5.0f);
        float y = dim.bFrame + 10;
        automationMode.render(context, textRenderer, x, y);
    }

    private void appendWithSeparation(MutableText text, Text append) {
        if (!text.getSiblings().isEmpty()) {
            text.append(" ");
        }
        text.append(append);
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        drawFont(textRenderer, context, Text.translatable("flightassistant.flight_mode_short"),
                dim.lFrame + dim.wFrame * 0.2f, dim.bFrame + 10,
                CONFIG.alertColor);
    }

    @Override
    public String getId() {
        return "flight_mode";
    }
}