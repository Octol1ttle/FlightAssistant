package net.torocraft.flighthud;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.torocraft.flighthud.alerts.Alert;
import net.torocraft.flighthud.commands.AltitudeResetCommand;
import net.torocraft.flighthud.commands.AltitudeSelectCommand;
import net.torocraft.flighthud.commands.DestinationResetCommand;
import net.torocraft.flighthud.commands.DestinationSelectCommand;
import net.torocraft.flighthud.commands.SwitchDisplayModeCommand;
import net.torocraft.flighthud.components.FlightStatusIndicator;
import net.torocraft.flighthud.config.HudConfig;
import net.torocraft.flighthud.config.SettingsConfig;
import net.torocraft.flighthud.config.loader.ConfigLoader;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class FlightHud implements ClientModInitializer {
    public static final String MODID = "flighthud";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

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
    private static KeyBinding hideAlertSwitch;
    private static KeyBinding showAlertSwitch;
    private static KeyBinding flightLawSwitch;
    private static KeyBinding gpwsSwitch;
    private static KeyBinding fdSwitch;
    private static KeyBinding aThrSwitch;
    private static KeyBinding apSwitch;

    private static void setupKeycCode() {
        keyBinding = new KeyBinding("key.flighthud.toggleDisplayMode", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_GRAVE_ACCENT, "category.flighthud.toggleDisplayMode");

        hideAlertSwitch = new KeyBinding("key.flighthud.hideAlertSwitch", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_0, "category.flighthud.toggleDisplayMode");

        showAlertSwitch = new KeyBinding("key.flighthud.showAlertSwitch", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_DECIMAL, "category.flighthud.toggleDisplayMode");

        flightLawSwitch = new KeyBinding("key.flighthud.flightLawSwitch", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_ENTER, "category.flighthud.toggleDisplayMode");

        gpwsSwitch = new KeyBinding("key.flighthud.gpwsSwitch", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_ADD, "category.flighthud.toggleDisplayMode");

        fdSwitch = new KeyBinding("key.flighthud.fdSwitch", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_1, "category.flighthud.toggleDisplayMode");

        aThrSwitch = new KeyBinding("key.flighthud.aThrSwitch", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_2, "category.flighthud.toggleDisplayMode");

        apSwitch = new KeyBinding("key.flighthud.apSwitch", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_3, "category.flighthud.toggleDisplayMode");

        KeyBindingHelper.registerKeyBinding(keyBinding);
        KeyBindingHelper.registerKeyBinding(hideAlertSwitch);
        KeyBindingHelper.registerKeyBinding(showAlertSwitch);
        KeyBindingHelper.registerKeyBinding(flightLawSwitch);
        KeyBindingHelper.registerKeyBinding(gpwsSwitch);
        KeyBindingHelper.registerKeyBinding(fdSwitch);
        KeyBindingHelper.registerKeyBinding(aThrSwitch);
        KeyBindingHelper.registerKeyBinding(apSwitch);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                CONFIG_SETTINGS.toggleDisplayMode();
            }

            while (hideAlertSwitch.wasPressed()) {
                for (Alert alert : FlightStatusIndicator.activeAlerts) {
                    if (alert.hidden) continue;
                    alert.hidden = true;
                    break;
                }
            }

            while (showAlertSwitch.wasPressed()) {
                for (int i = FlightStatusIndicator.activeAlerts.size() - 1; i >= 0; i--) {
                    Alert alert = FlightStatusIndicator.activeAlerts.get(i);
                    if (!alert.hidden) continue;
                    alert.hidden = false;
                    break;
                }
            }

            while (flightLawSwitch.wasPressed()) {
                FlightSafetyMonitor.flightProtectionsEnabled = !FlightSafetyMonitor.flightProtectionsEnabled;
                LOGGER.warn("Flight protections turned {}", FlightSafetyMonitor.flightProtectionsEnabled ? "on" : "off");
            }

            while (gpwsSwitch.wasPressed()) {
                CONFIG_SETTINGS.gpws = !CONFIG_SETTINGS.gpws;
                LOGGER.warn("GPWS turned {}", CONFIG_SETTINGS.gpws ? "on" : "off");
            }

            while (fdSwitch.wasPressed()) {
                AutoFlightManager.flightDirectorsEnabled = !AutoFlightManager.flightDirectorsEnabled;
                LOGGER.info("Flight directors turned {}", AutoFlightManager.flightDirectorsEnabled ? "on" : "off");
            }

            while (aThrSwitch.wasPressed()) {
                AutoFlightManager.autoThrustEnabled = !AutoFlightManager.autoThrustEnabled;
                if (!AutoFlightManager.autoThrustEnabled)
                    FlightSafetyMonitor.thrustLocked = false;
                LOGGER.info("Auto thrust turned {}", AutoFlightManager.autoThrustEnabled ? "on" : "off");
            }

            while (apSwitch.wasPressed()) {
                AutoFlightManager.autoPilotEnabled = !AutoFlightManager.autoPilotEnabled;
                LOGGER.info("Auto pilot turned {}", AutoFlightManager.autoPilotEnabled ? "on" : "off");
            }
        });
    }

    private static void setupCommand() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(literal("flighthud")
                .then(literal("toggle").executes(new SwitchDisplayModeCommand()))
                .then(literal("nav")
                        .then(argument("destinationX", IntegerArgumentType.integer(-30_000_000, 30_000_000))
                                .then(argument("destinationZ", IntegerArgumentType.integer(-30_000_000, 30_000_000))
                                        .executes(new DestinationSelectCommand())))
                        .then(literal("reset")
                                .executes(new DestinationResetCommand())))
                .then(literal("alt")
                        .then(argument("targetAltitude", IntegerArgumentType.integer(0, 640))
                                .executes(new AltitudeSelectCommand()))
                        .then(literal("reset")
                                .executes(new AltitudeResetCommand())))));
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
