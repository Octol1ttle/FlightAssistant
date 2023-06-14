package net.torocraft.flighthud.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.torocraft.flighthud.AutoFlightManager;

public class DestinationResetCommand implements Command<FabricClientCommandSource> {
    @Override
    public int run(CommandContext<FabricClientCommandSource> context) {
        AutoFlightManager.destinationX = null;
        AutoFlightManager.destinationZ = null;
        return 0;
    }
}
