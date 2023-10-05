package net.torocraft.flighthud;

import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.torocraft.flighthud.commands.SwitchDisplayModeCommand;
import net.torocraft.flighthud.config.HudConfig;
import net.torocraft.flighthud.config.SettingsConfig;
import net.torocraft.flighthud.config.loader.ConfigLoader;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class FlightHud implements ClientModInitializer {
    public static final String MODID = "flighthud";
    public static final Logger LOGGER = LoggerFactory.getLogger("FlightHud");

    public static SettingsConfig CONFIG_SETTINGS = new SettingsConfig();
    public static HudConfig CONFIG_MIN = new HudConfig();
    public static HudConfig CONFIG_FULL = new HudConfig();

    public static ConfigLoader<SettingsConfig> CONFIG_LOADER_SETTINGS = new ConfigLoader<>(
            new SettingsConfig(),
            FlightHud.MODID + ".settings.json",
            config -> FlightHud.CONFIG_SETTINGS = config);


    public static ConfigLoader<HudConfig> CONFIG_LOADER_FULL = new ConfigLoader<>(
            new HudConfig(),
            FlightHud.MODID + ".full.json",
            config -> FlightHud.CONFIG_FULL = config);


    public static ConfigLoader<HudConfig> CONFIG_LOADER_MIN = new ConfigLoader<>(
            HudConfig.getDefaultMinSettings(),
            FlightHud.MODID + ".min.json",
            config -> FlightHud.CONFIG_MIN = config);

    private static KeyBinding keyBinding;

    private static void setupKeycCode() {
        keyBinding = new KeyBinding("key.flighthud.toggleDisplayMode", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_GRAVE_ACCENT, "category.flighthud.toggleDisplayMode");


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                CONFIG_SETTINGS.toggleDisplayMode();
            }
        });
    }

    private static void setupCommand() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {

            LiteralCommandNode<FabricClientCommandSource> node = dispatcher.register(literal("flighthud")
                    .then(literal("toggle").executes(new SwitchDisplayModeCommand()))
            );
            dispatcher.register(literal("fhud").redirect(node));
            dispatcher.register(literal("fh").redirect(node));
        });
    }

    @Override
    public void onInitializeClient() {
        CONFIG_LOADER_SETTINGS.load();
        CONFIG_LOADER_FULL.load();
        CONFIG_LOADER_MIN.load();
        setupKeycCode();
        setupCommand();
    }
}
