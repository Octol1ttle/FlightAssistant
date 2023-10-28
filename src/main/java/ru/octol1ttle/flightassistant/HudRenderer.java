package ru.octol1ttle.flightassistant;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.compatibility.ImmediatelyFastBatchingAccessor;
import ru.octol1ttle.flightassistant.computers.FlightComputer;
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

public class HudRenderer extends HudComponent {
    private static final String FULL = SettingsConfig.DisplayMode.FULL.toString();
    private static final String MIN = SettingsConfig.DisplayMode.MIN.toString();
    public static HudRenderer INSTANCE;
    @NotNull
    public final FlightComputer computer;
    private final Dimensions dim = new Dimensions();
    private final List<HudComponent> components;
    private final List<HudComponent> toDelete;

    public HudRenderer(MinecraftClient mc) {
        this.computer = new FlightComputer(mc);
        this.components = new ArrayList<>(List.of(
                new FlightPathIndicator(computer, dim), new LocationIndicator(computer, dim),
                new HeadingIndicator(computer, dim), new SpeedIndicator(computer, dim),
                new AltitudeIndicator(computer, dim), new PitchIndicator(computer, dim),
                new ElytraHealthIndicator(computer, dim), new AlertIndicator(computer, dim),
                new FlightModeIndicator(computer, dim)));
        this.toDelete = new ArrayList<>(components.size());
    }

    public static FlightComputer getComputer() {
        if (INSTANCE == null) {
            return null;
        }
        return INSTANCE.computer;
    }

    private void setupConfig() {
        HudComponent.CONFIG = null;
        if (computer.player.isFallFlying()) {
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

        for (HudComponent component : components) {
            if (FabricLoader.getInstance().isModLoaded("immediatelyfast")) {
                ImmediatelyFastBatchingAccessor.beginHudBatching();
            }
            try {
                component.render(context, mc.textRenderer);
            } catch (Exception e) {
                FlightAssistant.LOGGER.error("Exception rendering component", e);
                toDelete.add(component);
                if (getComputer() != null) {
                    getComputer().internalError = true;
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
