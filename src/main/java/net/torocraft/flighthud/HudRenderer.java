package net.torocraft.flighthud;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.torocraft.flighthud.components.AltitudeIndicator;
import net.torocraft.flighthud.components.ElytraHealthIndicator;
import net.torocraft.flighthud.components.FlightPathIndicator;
import net.torocraft.flighthud.components.FlightStatusIndicator;
import net.torocraft.flighthud.components.HeadingIndicator;
import net.torocraft.flighthud.components.LocationIndicator;
import net.torocraft.flighthud.components.PitchIndicator;
import net.torocraft.flighthud.components.SpeedIndicator;
import net.torocraft.flighthud.config.SettingsConfig.DisplayMode;

public class HudRenderer extends HudComponent {
    public static final HudRenderer INSTANCE = new HudRenderer();
    private static final String FULL = DisplayMode.FULL.toString();
    private static final String MIN = DisplayMode.MIN.toString();
    public final FlightComputer computer = new FlightComputer();
    private final Dimensions dim = new Dimensions();
    private final HudComponent[] components =
            new HudComponent[]{new FlightPathIndicator(computer, dim), new LocationIndicator(dim),
                    new HeadingIndicator(computer, dim), new SpeedIndicator(computer, dim),
                    new AltitudeIndicator(computer, dim), new PitchIndicator(computer, dim),
                    new ElytraHealthIndicator(computer, dim), new FlightStatusIndicator(computer, dim)};

    private void setupConfig(MinecraftClient client) {
        HudComponent.CONFIG = null;
        if (client.player.isFallFlying()) {
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

    @Override
    public void render(DrawContext context, MinecraftClient client) {
        setupConfig(client);
        ((FlightStatusIndicator) components[components.length - 1]).tryStopEvents(client.player, client.getSoundManager());

        if (HudComponent.CONFIG == null) {
            return;
        }

        try {
            context.getMatrices().push();

            if (HudComponent.CONFIG.scale != 1d) {
                float scale = 1 / HudComponent.CONFIG.scale;
                context.getMatrices().scale(scale, scale, scale);
            }

            dim.update(client);

            for (HudComponent component : components) {
                if (FabricLoader.getInstance().isModLoaded("immediatelyfast"))
                    net.torocraft.flighthud.compat.ImmediatelyFastBatchingAccessor.beginHudBatching();
                component.render(context, client);
                if (FabricLoader.getInstance().isModLoaded("immediatelyfast"))
                    net.torocraft.flighthud.compat.ImmediatelyFastBatchingAccessor.endHudBatching();
            }
            context.getMatrices().pop();
        } catch (Exception e) {
            FlightHud.LOGGER.error("Exception rendering components", e);
        }
    }
}
