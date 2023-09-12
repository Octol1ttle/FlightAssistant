package net.torocraft.flighthud.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.torocraft.flighthud.AutoFlightManager;

public class DestinationSelectCommand implements Command<FabricClientCommandSource> {
    @Override
    public int run(CommandContext<FabricClientCommandSource> context) {
        AutoFlightManager.destinationX = IntegerArgumentType.getInteger(context, "destinationX");
        AutoFlightManager.destinationZ = IntegerArgumentType.getInteger(context, "destinationZ");
        return 0;
    }
}
