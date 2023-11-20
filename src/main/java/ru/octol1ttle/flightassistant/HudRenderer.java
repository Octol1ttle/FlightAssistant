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
    private final List<HudComponent> toDelete;

    public HudRenderer(MinecraftClient mc) {
        this.host = new ComputerHost(mc);
        this.components = new ArrayList<>(List.of(
                new FlightPathIndicator(dim, host.data, host.gpws), new LocationIndicator(dim, host.data),
                new HeadingIndicator(dim, host.data), new SpeedIndicator(dim, host.data),
                new AltitudeIndicator(dim, host.data), new PitchIndicator(dim, host.data, host.stall, host.voidLevel),
                new ElytraHealthIndicator(dim, host.data), new AlertIndicator(dim, host.alert, host.time),
                new FlightModeIndicator(dim, host.firework, host.time, host.autoflight), new StatusIndicator(dim, host.firework)));
        this.toDelete = new ArrayList<>(components.size());
    }

    public static ComputerHost getHost() {
        if (INSTANCE == null) {
            return null;
        }
        return INSTANCE.host;
    }

    private void setupConfig() {
        HudComponent.CONFIG = null;
        if (host.data.player.isFallFlying()) {
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

        if (HudComponent.CONFIG == null || !host.ready) {
            return;
        }

        context.getMatrices().push();

        if (HudComponent.CONFIG.scale != 1d) {
            float scale = 1 / HudComponent.CONFIG.scale;
            context.getMatrices().scale(scale, scale, scale);
        }

        dim.update(mc);

        for (HudComponent component : components) {
            if (FabricLoader.getInstance().isModLoaded("immediatelyfast")) {
                ImmediatelyFastBatchingAccessor.beginHudBatching();
            }
            try {
                component.render(context, mc.textRenderer);
            } catch (Exception e) {
                FlightAssistant.LOGGER.error("Exception rendering component", e);
                toDelete.add(component);
                if (getHost() != null) {
                    // TODO: indicator fail alert
                }
            }
            if (FabricLoader.getInstance().isModLoaded("immediatelyfast")) {
                ImmediatelyFastBatchingAccessor.endHudBatching();
            }
        }

        if (components.removeAll(toDelete)) {
            toDelete.clear();
        }

        context.getMatrices().pop();
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        throw new IllegalStateException();
    }
}
