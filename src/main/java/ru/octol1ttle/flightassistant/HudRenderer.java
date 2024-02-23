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
import ru.octol1ttle.flightassistant.indicators.AlertIndicator;
import ru.octol1ttle.flightassistant.indicators.AltitudeIndicator;
import ru.octol1ttle.flightassistant.indicators.ElytraHealthIndicator;
import ru.octol1ttle.flightassistant.indicators.FlightDirectorsIndicator;
import ru.octol1ttle.flightassistant.indicators.FlightModeIndicator;
import ru.octol1ttle.flightassistant.indicators.FlightPathIndicator;
import ru.octol1ttle.flightassistant.indicators.HeadingIndicator;
import ru.octol1ttle.flightassistant.indicators.LocationIndicator;
import ru.octol1ttle.flightassistant.indicators.PitchIndicator;
import ru.octol1ttle.flightassistant.indicators.SpeedIndicator;
import ru.octol1ttle.flightassistant.indicators.StatusIndicator;
import ru.octol1ttle.flightassistant.mixin.GameRendererInvoker;

public class HudRenderer extends HudComponent {
    public static HudRenderer INSTANCE;
    @NotNull
    public final ComputerHost host;
    private final Dimensions dim = new Dimensions();
    private final List<HudComponent> components;
    public final List<HudComponent> faulted;

    public HudRenderer(MinecraftClient mc) {
        this.host = new ComputerHost(mc, this);
        this.components = new ArrayList<>(List.of(
                new FlightPathIndicator(dim, host.data, host.gpws), new LocationIndicator(dim, host.data),
                new HeadingIndicator(dim, host.data, host.autoflight), new SpeedIndicator(dim, host.data),
                new AltitudeIndicator(dim, host.data, host.autoflight), new PitchIndicator(dim, host.data, host.stall, host.voidLevel),
                new ElytraHealthIndicator(dim, host.data), new AlertIndicator(dim, host, host.alert, host.time),
                new FlightModeIndicator(dim, host.firework, host.time, host.autoflight, host.plan, host.data), new StatusIndicator(dim, host.firework),
                new FlightDirectorsIndicator(dim, host.autoflight, host.data)));
        this.faulted = new ArrayList<>(components.size());
    }

    public static ComputerHost getHost() {
        if (INSTANCE == null) {
            return null;
        }
        return INSTANCE.host;
    }

    public void render(MinecraftClient mc, DrawContext context, float tickDelta) {
        GameRendererInvoker renderer = (GameRendererInvoker) mc.gameRenderer;
        dim.update(context, renderer.getFov(mc.gameRenderer.getCamera(), tickDelta, true));

        float hudScale = FAConfig.get().hudScale;
        boolean batchAll = FlightAssistant.canUseBatching() && FAConfig.get().batchedRendering == FAConfig.BatchedRendering.SINGLE_BATCH;

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
                } catch (Exception e) {
                    FlightAssistant.LOGGER.error("Exception rendering component", e);
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
                } catch (Exception e) {
                    FlightAssistant.LOGGER.error("Exception rendering faulted component", e);
                }
            });
        }

        context.getMatrices().pop();
    }

    public void drawBatchedComponent(Runnable draw) {
        boolean batch = FlightAssistant.canUseBatching() && FAConfig.get().batchedRendering == FAConfig.BatchedRendering.BATCH_PER_COMPONENT;
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
