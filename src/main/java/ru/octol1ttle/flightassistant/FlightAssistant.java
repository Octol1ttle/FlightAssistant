package ru.octol1ttle.flightassistant;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.octol1ttle.flightassistant.commands.FlightPlanCommand;
import ru.octol1ttle.flightassistant.commands.MCPCommand;
import ru.octol1ttle.flightassistant.commands.ResetCommand;
import ru.octol1ttle.flightassistant.commands.SwitchDisplayModeCommand;
import ru.octol1ttle.flightassistant.config.HudConfig;
import ru.octol1ttle.flightassistant.config.SettingsConfig;
import ru.octol1ttle.flightassistant.config.loader.ConfigLoader;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

// TODO: try to remove all abbreviations/shortenings and see what comes of it
public class FlightAssistant implements ClientModInitializer {
    public static final String MODID = "flightassistant";
    public static final Logger LOGGER = LoggerFactory.getLogger("FlightAssistant");

    public static SettingsConfig CONFIG_SETTINGS = new SettingsConfig();
    public static HudConfig CONFIG_MIN = new HudConfig();
    public static HudConfig CONFIG_FULL = new HudConfig();

    public static ConfigLoader<SettingsConfig> CONFIG_LOADER_SETTINGS = new ConfigLoader<>(
            new SettingsConfig(),
            FlightAssistant.MODID + ".settings.json",
            config -> FlightAssistant.CONFIG_SETTINGS = config);


    public static ConfigLoader<HudConfig> CONFIG_LOADER_FULL = new ConfigLoader<>(
            new HudConfig(),
            FlightAssistant.MODID + ".full.json",
            config -> FlightAssistant.CONFIG_FULL = config);


    public static ConfigLoader<HudConfig> CONFIG_LOADER_MIN = new ConfigLoader<>(
            HudConfig.getDefaultMinSettings(),
            FlightAssistant.MODID + ".min.json",
            config -> FlightAssistant.CONFIG_MIN = config);

    private static void setupCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            LiteralArgumentBuilder<FabricClientCommandSource> builder = literal("flightassistant");
            SwitchDisplayModeCommand.register(builder);
            ResetCommand.register(builder);
            MCPCommand.register(builder);
            FlightPlanCommand.register(builder);

            LiteralCommandNode<FabricClientCommandSource> node = dispatcher.register(builder);
            dispatcher.register(literal("flas").redirect(node));
            dispatcher.register(literal("fhud").redirect(node));
            dispatcher.register(literal("fh").redirect(node));
        });
    }

    @Override
    public void onInitializeClient() {
        CONFIG_LOADER_SETTINGS.load();
        CONFIG_LOADER_FULL.load();
        CONFIG_LOADER_MIN.load();

        FAKeyBindings.setup();
        FACallbacks.setup();
        setupCommands();
    }
}
