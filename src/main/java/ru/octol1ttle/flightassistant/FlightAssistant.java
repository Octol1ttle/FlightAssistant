package ru.octol1ttle.flightassistant;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.commands.SwitchDisplayModeCommand;
import ru.octol1ttle.flightassistant.commands.autoflight.SetAutoFireworkSpeedCommand;
import ru.octol1ttle.flightassistant.commands.plan.AddWaypointCommand;
import ru.octol1ttle.flightassistant.commands.plan.ExecutePlanCommand;
import ru.octol1ttle.flightassistant.commands.plan.InsertWaypointCommand;
import ru.octol1ttle.flightassistant.commands.plan.RemoveWaypointCommand;
import ru.octol1ttle.flightassistant.commands.plan.ReplaceWaypointCommand;
import ru.octol1ttle.flightassistant.commands.reset.ResetAllComputersCommand;
import ru.octol1ttle.flightassistant.commands.reset.ResetFaultedComputersCommand;
import ru.octol1ttle.flightassistant.commands.reset.ResetFaultedIndicatorsCommand;
import ru.octol1ttle.flightassistant.computers.ComputerHost;
import ru.octol1ttle.flightassistant.config.HudConfig;
import ru.octol1ttle.flightassistant.config.SettingsConfig;
import ru.octol1ttle.flightassistant.config.loader.ConfigLoader;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

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

    private static KeyBinding toggleDisplayMode;

    private static KeyBinding toggleFlightDirectors;
    private static KeyBinding toggleAutoFirework;
    private static KeyBinding toggleAutoPilot;

    private static KeyBinding dismissMasterWarning;
    private static KeyBinding dismissMasterCaution;

    private static void setupKeycCode() {
        toggleDisplayMode = new KeyBinding("key.flightassistant.toggle_display_mode", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_GRAVE_ACCENT, "category.flightassistant");

        toggleFlightDirectors = new KeyBinding("key.flightassistant.toggle_flight_directors", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_1, "category.flightassistant");
        toggleAutoFirework = new KeyBinding("key.flightassistant.toggle_auto_firework", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_2, "category.flightassistant");
        toggleAutoPilot = new KeyBinding("key.flightassistant.toggle_auto_pilot", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_3, "category.flightassistant");

        dismissMasterWarning = new KeyBinding("key.flightassistant.dismiss_master_warning", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_0, "category.flightassistant");
        dismissMasterCaution = new KeyBinding("key.flightassistant.dismiss_master_caution", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_DECIMAL, "category.flightassistant");

        KeyBindingHelper.registerKeyBinding(toggleDisplayMode);

        KeyBindingHelper.registerKeyBinding(toggleFlightDirectors);
        KeyBindingHelper.registerKeyBinding(toggleAutoFirework);
        KeyBindingHelper.registerKeyBinding(toggleAutoPilot);

        KeyBindingHelper.registerKeyBinding(dismissMasterWarning);
        KeyBindingHelper.registerKeyBinding(dismissMasterCaution);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleDisplayMode.wasPressed()) {
                CONFIG_SETTINGS.toggleDisplayMode(client);
            }

            ComputerHost host = HudRenderer.getHost();
            if (host != null) {
                while (toggleFlightDirectors.wasPressed()) {
                    host.autoflight.flightDirectorsEnabled = !host.autoflight.flightDirectorsEnabled;
                }

                while (toggleAutoFirework.wasPressed()) {
                    if (!host.autoflight.autoFireworkEnabled) {
                        host.autoflight.autoFireworkEnabled = true;
                    } else {
                        host.autoflight.disconnectAutoFirework(false);
                    }
                }

                while (toggleAutoPilot.wasPressed()) {
                    if (!host.autoflight.autoPilotEnabled) {
                        host.autoflight.autoPilotEnabled = true;
                    } else {
                        host.autoflight.disconnectAutopilot(false);
                    }
                }

                while (dismissMasterWarning.wasPressed()) {
                    if (!host.alert.dismiss(AlertSoundData.AUTOPILOT_FORCED_OFF) && !host.alert.dismiss(AlertSoundData.AUTOPILOT_DISCONNECTED_BY_PLAYER)) {
                        host.alert.dismiss(AlertSoundData.MASTER_WARNING);
                    }
                }

                while (dismissMasterCaution.wasPressed()) {
                    if (!host.alert.dismiss(AlertSoundData.ALTITUDE_ALERT)) {
                        host.alert.dismiss(AlertSoundData.MASTER_CAUTION);
                    }
                }
            }
        });
    }

    private static void setupCommand() {
        // TODO: registers should be in command classes, not here
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            LiteralCommandNode<FabricClientCommandSource> node = dispatcher.register(literal("flightassistant")
                    .then(literal("toggle").executes(
                            new SwitchDisplayModeCommand())
                    )
                    .then(literal("reset")
                            .then(literal("computers")
                                    .then(literal("all")
                                            .executes(new ResetAllComputersCommand()))
                                    .then(literal("faulted")
                                            .executes(new ResetFaultedComputersCommand()))
                            )
                            .then(literal("indicators")
                                    .executes(new ResetFaultedIndicatorsCommand())
                            )
                    )
                    .then(literal("speed")
                            .then(argument("targetSpeed", IntegerArgumentType.integer(10, 30))
                                    .executes(new SetAutoFireworkSpeedCommand())))
                    .then(literal("plan")
                            .then(literal("add")
                                    .then(argument("targetX", IntegerArgumentType.integer()).then(argument("targetZ", IntegerArgumentType.integer()).then(argument("targetAltitude", IntegerArgumentType.integer(-80)).then(argument("targetSpeed", IntegerArgumentType.integer(10, 30))
                                            .executes(new AddWaypointCommand())
                                    ))))
                            )
                            .then(literal("remove")
                                    .then(argument("waypointIndex", IntegerArgumentType.integer(0))
                                            .executes(new RemoveWaypointCommand())
                                    )
                            )
                            .then(literal("insert")
                                    .then(argument("insertAt", IntegerArgumentType.integer(0))
                                            .then(argument("targetX", IntegerArgumentType.integer()).then(argument("targetZ", IntegerArgumentType.integer()).then(argument("targetAltitude", IntegerArgumentType.integer(-80)).then(argument("targetSpeed", IntegerArgumentType.integer(10, 30))
                                                            .executes(new InsertWaypointCommand())
                                                    )))
                                            )
                                    )
                            )
                            .then(literal("replace")
                                    .then(argument("replaceAt", IntegerArgumentType.integer(0))
                                            .then(argument("targetX", IntegerArgumentType.integer()).then(argument("targetZ", IntegerArgumentType.integer()).then(argument("targetAltitude", IntegerArgumentType.integer(-80)).then(argument("targetSpeed", IntegerArgumentType.integer(10, 30))
                                                            .executes(new ReplaceWaypointCommand())
                                                    )))
                                            )
                                    )
                            )
                            .then(literal("execute")
                                    .executes(new ExecutePlanCommand())
                            )
                    )
            );
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
        setupKeycCode();
        setupCommand();
    }
}
