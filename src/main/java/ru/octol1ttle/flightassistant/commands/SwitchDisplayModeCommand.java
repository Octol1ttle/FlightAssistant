package ru.octol1ttle.flightassistant.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import ru.octol1ttle.flightassistant.FlightAssistant;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class SwitchDisplayModeCommand {
    public static void register(LiteralArgumentBuilder<FabricClientCommandSource> builder) {
        builder.then(literal("toggle").executes(
                context -> {
                    FlightAssistant.CONFIG_SETTINGS.toggleDisplayMode(context.getSource().getClient());
                    return 0;
                }
        ));
    }
}
