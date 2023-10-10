package net.torocraft.flighthud;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.torocraft.flighthud.computers.FlightComputer;
import net.torocraft.flighthud.config.SettingsConfig.DisplayMode;
import net.torocraft.flighthud.indicators.AlertIndicator;
import net.torocraft.flighthud.indicators.AltitudeIndicator;
import net.torocraft.flighthud.indicators.ElytraHealthIndicator;
import net.torocraft.flighthud.indicators.FlightPathIndicator;
import net.torocraft.flighthud.indicators.HeadingIndicator;
import net.torocraft.flighthud.indicators.LocationIndicator;
import net.torocraft.flighthud.indicators.PitchIndicator;
import net.torocraft.flighthud.indicators.SpeedIndicator;
import org.jetbrains.annotations.NotNull;

public class HudRenderer extends HudComponent {
    private static final String FULL = DisplayMode.FULL.toString();
    private static final String MIN = DisplayMode.MIN.toString();
    public static HudRenderer INSTANCE;
    @NotNull
    public final FlightComputer computer;
    private final Dimensions dim = new Dimensions();
    private final HudComponent[] components;

    public HudRenderer(MinecraftClient mc) {
        this.computer = new FlightComputer(mc);
        this.components = new HudComponent[]{
                new FlightPathIndicator(computer, dim), new LocationIndicator(computer, dim),
                new HeadingIndicator(computer, dim), new SpeedIndicator(computer, dim),
                new AltitudeIndicator(computer, dim), new PitchIndicator(computer, dim),
                new ElytraHealthIndicator(computer, dim), new AlertIndicator(computer, dim)
        };
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
            if (FlightHud.CONFIG_SETTINGS.displayModeWhenFlying.equals(FULL)) {
                HudComponent.CONFIG = FlightHud.CONFIG_FULL;
            } else if (FlightHud.CONFIG_SETTINGS.displayModeWhenFlying.equals(MIN)) {
                HudComponent.CONFIG = FlightHud.CONFIG_MIN;
            }
        } else {
            if (FlightHud.CONFIG_SETTINGS.displayModeWhenNotFlying.equals(FULL)) {
                HudComponent.CONFIG = FlightHud.CONFIG_FULL;
            } else if (FlightHud.CONFIG_SETTINGS.displayModeWhenNotFlying.equals(MIN)) {
                HudComponent.CONFIG = FlightHud.CONFIG_MIN;
            }
        }
    }

    public void render(DrawContext context, MinecraftClient mc) {
        setupConfig();

        if (HudComponent.CONFIG == null) {
            return;
        }

        try {
            context.getMatrices().push();

            if (HudComponent.CONFIG.scale != 1d) {
                float scale = 1 / HudComponent.CONFIG.scale;
                context.getMatrices().scale(scale, scale, scale);
            }

            dim.update(mc);

            for (HudComponent component : components) {
                if (FabricLoader.getInstance().isModLoaded("immediatelyfast"))
                    net.torocraft.flighthud.compatibility.ImmediatelyFastBatchingAccessor.beginHudBatching();
                component.render(context, mc.textRenderer);
                if (FabricLoader.getInstance().isModLoaded("immediatelyfast"))
                    net.torocraft.flighthud.compatibility.ImmediatelyFastBatchingAccessor.endHudBatching();
            }
            context.getMatrices().pop();
        } catch (Exception e) {
            // TODO: alert? lmao
            FlightHud.LOGGER.error("Exception rendering components", e);
        }
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        throw new IllegalStateException();
    }
}
