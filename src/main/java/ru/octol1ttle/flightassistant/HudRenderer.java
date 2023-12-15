package ru.octol1ttle.flightassistant;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.compatibility.ImmediatelyFastBatchingAccessor;
import ru.octol1ttle.flightassistant.computers.ComputerHost;
import ru.octol1ttle.flightassistant.config.SettingsConfig;
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

public class HudRenderer extends HudComponent {
    private static final String FULL = SettingsConfig.DisplayMode.FULL.toString();
    private static final String MIN = SettingsConfig.DisplayMode.MIN.toString();
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
                new FlightModeIndicator(dim, host.firework, host.time, host.autoflight, host.plan), new StatusIndicator(dim, host.firework),
                new FlightDirectorsIndicator(dim, host.autoflight, host.data)));
        this.faulted = new ArrayList<>(components.size());
    }

    public static ComputerHost getHost() {
        if (INSTANCE == null) {
            return null;
        }
        return INSTANCE.host;
    }

    private void setupConfig() {
        HudComponent.CONFIG = null;
        if (host.data.isFlying) {
            if (FlightAssistant.CONFIG_SETTINGS.displayModeWhenFlying.equals(FULL)) {
                HudComponent.CONFIG = FlightAssistant.CONFIG_FULL;
            } else if (FlightAssistant.CONFIG_SETTINGS.displayModeWhenFlying.equals(MIN)) {
                HudComponent.CONFIG = FlightAssistant.CONFIG_MIN;
            }
        } else {
            if (FlightAssistant.CONFIG_SETTINGS.displayModeWhenNotFlying.equals(FULL)) {
                HudComponent.CONFIG = FlightAssistant.CONFIG_FULL;
            } else if (FlightAssistant.CONFIG_SETTINGS.displayModeWhenNotFlying.equals(MIN)) {
                HudComponent.CONFIG = FlightAssistant.CONFIG_MIN;
            }
        }
    }

    public void render(DrawContext context, MinecraftClient mc) {
        setupConfig();

        if (HudComponent.CONFIG == null) {
            return;
        }

        context.getMatrices().push();

        if (HudComponent.CONFIG.scale != 1d) {
            float scale = 1 / HudComponent.CONFIG.scale;
            context.getMatrices().scale(scale, scale, scale);
        }

        dim.update(mc);

        for (int i = components.size() - 1; i >= 0; i--) {
            HudComponent component = components.get(i);
            if (FabricLoader.getInstance().isModLoaded("immediatelyfast")) {
                ImmediatelyFastBatchingAccessor.beginHudBatching();
            }
            try {
                component.render(context, mc.textRenderer);
            } catch (Exception e) {
                FlightAssistant.LOGGER.error("Exception rendering component", e);
                faulted.add(component);
                components.remove(component);
            }
            if (FabricLoader.getInstance().isModLoaded("immediatelyfast")) {
                ImmediatelyFastBatchingAccessor.endHudBatching();
            }
        }

        for (HudComponent component : faulted) {
            if (FabricLoader.getInstance().isModLoaded("immediatelyfast")) {
                ImmediatelyFastBatchingAccessor.beginHudBatching();
            }
            try {
                component.renderFaulted(context, mc.textRenderer);
            } catch (Exception e) {
                FlightAssistant.LOGGER.error("Exception rendering faulted component", e);
            }
            if (FabricLoader.getInstance().isModLoaded("immediatelyfast")) {
                ImmediatelyFastBatchingAccessor.endHudBatching();
            }
        }

        context.getMatrices().pop();
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
