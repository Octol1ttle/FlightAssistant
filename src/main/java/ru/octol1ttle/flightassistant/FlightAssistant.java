package ru.octol1ttle.flightassistant;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: try to remove all abbreviations/shortenings and see what comes of it
// TODO: block certain alerts in certain phases of flight
public class FlightAssistant implements ClientModInitializer {
    public static final String MODID = "flightassistant";
    public static final Logger LOGGER = LoggerFactory.getLogger("FlightAssistant");

    @Override
    public void onInitializeClient() {
        FAConfig.setup();
        FAKeyBindings.setup();
        FACallbacks.setup();
    }
}
