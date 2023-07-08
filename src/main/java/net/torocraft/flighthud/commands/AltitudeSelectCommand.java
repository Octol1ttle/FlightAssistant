package net.torocraft.flighthud.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.torocraft.flighthud.AutoFlightManager;

import static net.torocraft.flighthud.FlightHud.LOGGER;

public class AltitudeSelectCommand implements Command<FabricClientCommandSource> {
    @Override
    public int run(CommandContext<FabricClientCommandSource> context) {
        AutoFlightManager.targetAltitude = IntegerArgumentType.getInteger(context, "targetAltitude");
        LOGGER.info("Altitude set to {}", AutoFlightManager.targetAltitude);
        return 0;
    }
}
