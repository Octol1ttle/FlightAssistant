package ru.octol1ttle.flightassistant;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.config.HUDConfig;

public class FlightAssistant implements ClientModInitializer {
    public static final String MODID = "flightassistant";
    public static final Logger LOGGER = LoggerFactory.getLogger("FlightAssistant");

    @Override
    public void onInitializeClient() {
        FAConfig.setup();
        FAKeyBindings.setup();
        FACallbacks.setup();
    }

    public static boolean isHUDBatched() {
        return canUseBatching() && FAConfig.hud().batchedRendering != HUDConfig.BatchedRendering.NO_BATCHING;
    }

    public static boolean canUseBatching() {
        return FabricLoader.getInstance().isModLoaded("immediatelyfast");
    }
}
