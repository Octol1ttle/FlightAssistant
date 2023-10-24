package ru.octol1ttle.flightassistant.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import ru.octol1ttle.flightassistant.FlightAssistant;

public class SwitchDisplayModeCommand implements Command<FabricClientCommandSource> {

    @Override
    public int run(CommandContext<FabricClientCommandSource> context) {
        FlightAssistant.CONFIG_SETTINGS.toggleDisplayMode(context.getSource().getClient());
        return 0;
    }

}
