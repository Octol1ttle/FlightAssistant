package net.torocraft.flighthud.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.torocraft.flighthud.FlightHud;

public class SwitchDisplayModeCommand implements Command<FabricClientCommandSource> {

    @Override
    public int run(CommandContext<FabricClientCommandSource> context) {
        FlightHud.CONFIG_SETTINGS.toggleDisplayMode(context.getSource().getClient());
        return 0;
    }

}
