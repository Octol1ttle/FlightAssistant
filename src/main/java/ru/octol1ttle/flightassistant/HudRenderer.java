package ru.octol1ttle.flightassistant;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.compatibility.ImmediatelyFastBatchingAccessor;
import ru.octol1ttle.flightassistant.computers.ComputerHost;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.config.HUDConfig;
import ru.octol1ttle.flightassistant.indicators.AlertIndicator;
import ru.octol1ttle.flightassistant.indicators.AltitudeIndicator;
import ru.octol1ttle.flightassistant.indicators.ElytraHealthIndicator;
import ru.octol1ttle.flightassistant.indicators.FlightDirectorsIndicator;
import ru.octol1ttle.flightassistant.indicators.FlightModeIndicator;
import ru.octol1ttle.flightassistant.indicators.FlightPathIndicator;
import ru.octol1ttle.flightassistant.indicators.GroundSpeedIndicator;
import ru.octol1ttle.flightassistant.indicators.HeadingIndicator;
import ru.octol1ttle.flightassistant.indicators.RadarAltitudeIndicator;
import ru.octol1ttle.flightassistant.indicators.LocationIndicator;
import ru.octol1ttle.flightassistant.indicators.AttitudeIndicator;
import ru.octol1ttle.flightassistant.indicators.SpeedIndicator;
import ru.octol1ttle.flightassistant.indicators.StatusIndicator;
import ru.octol1ttle.flightassistant.indicators.VerticalSpeedIndicator;
import ru.octol1ttle.flightassistant.mixin.GameRendererInvoker;

public class HudRenderer extends HudComponent {
    public static final HudRenderer INSTANCE = new HudRenderer(MinecraftClient.getInstance());
    @NotNull
    public final ComputerHost host;
    private final Dimensions dim = new Dimensions();
    private final List<HudComponent> components;
    public final List<HudComponent> faulted;

    public HudRenderer(MinecraftClient mc) {
        this.host = new ComputerHost(mc, this);
        this.components = new ArrayList<>(List.of(
                new AlertIndicator(dim, host, host.alert, host.time),
                new AltitudeIndicator(dim, host.data, host.autoflight, host.plan),
                new AttitudeIndicator(dim, host.data, host.stall, host.voidLevel),
                new ElytraHealthIndicator(dim, host.data),
                new FlightDirectorsIndicator(dim, host.data, host.autoflight),
                new FlightModeIndicator(dim, host.data, host.time, host.firework, host.autoflight, host.plan),
                new FlightPathIndicator(dim, host.data, host.gpws),
                new GroundSpeedIndicator(dim, host.data),
                new HeadingIndicator(dim, host.data, host.autoflight),
                new RadarAltitudeIndicator(dim, host.data, host.plan),
                new LocationIndicator(dim, host.data),
                new SpeedIndicator(dim, host.data),
                new StatusIndicator(dim, host.firework, host.plan),
                new VerticalSpeedIndicator(dim, host.data)
        ));
        this.faulted = new ArrayList<>(components.size());
    }

    public static @NotNull ComputerHost getHost() {
        return INSTANCE.host;
    }

    public void render(MinecraftClient mc, DrawContext context, float tickDelta) {
        GameRendererInvoker renderer = (GameRendererInvoker) mc.gameRenderer;
        dim.update(context, renderer.invokeGetFov(mc.gameRenderer.getCamera(), tickDelta, true));

        float hudScale = FAConfig.hud().hudScale;
        boolean batchAll = FlightAssistant.canUseBatching() && FAConfig.hud().batchedRendering == HUDConfig.BatchedRendering.SINGLE_BATCH;

        context.getMatrices().push();
        context.getMatrices().scale(hudScale, hudScale, hudScale);

        if (batchAll) {
            ImmediatelyFastBatchingAccessor.beginHudBatching();
        }
        for (int i = components.size() - 1; i >= 0; i--) {
            HudComponent component = components.get(i);
            drawBatchedComponent(() -> {
                try {
                    component.render(context, mc.textRenderer);
                } catch (Throwable t) {
                    FlightAssistant.LOGGER.error("Exception rendering component", t);
                    faulted.add(component);
                    components.remove(component);
                }
            });
        }
        if (batchAll) {
            ImmediatelyFastBatchingAccessor.endHudBatching();
        }

        for (HudComponent component : faulted) {
            drawBatchedComponent(() -> {
                try {
                    component.renderFaulted(context, mc.textRenderer);
                } catch (Throwable t) {
                    FlightAssistant.LOGGER.error("Exception rendering faulted component", t);
                }
            });
        }

        context.getMatrices().pop();
    }

    public void drawBatchedComponent(Runnable draw) {
        boolean batch = FlightAssistant.canUseBatching() && FAConfig.hud().batchedRendering == HUDConfig.BatchedRendering.PER_COMPONENT;
        if (batch) {
            ImmediatelyFastBatchingAccessor.beginHudBatching();
        }
        draw.run();
        if (batch) {
            ImmediatelyFastBatchingAccessor.endHudBatching();
        }
    }

    public void resetFaulted() {
        for (int i = faulted.size() - 1; i >= 0; i--) {
            HudComponent component = faulted.get(i);
            faulted.remove(component);
            components.add(component);
        }
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        throw new IllegalStateException();
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        throw new IllegalStateException();
    }

    @Override
    public String getId() {
        throw new IllegalStateException();
    }
}
