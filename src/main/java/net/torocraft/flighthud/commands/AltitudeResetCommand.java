package net.torocraft.flighthud.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.torocraft.flighthud.AutoFlightManager;

import static net.torocraft.flighthud.FlightHud.LOGGER;

public class AltitudeResetCommand implements Command<FabricClientCommandSource> {
    @Override
    public int run(CommandContext<FabricClientCommandSource> context) {
        AutoFlightManager.targetAltitude = null;
        LOGGER.info("Altitude reset");
        return 0;
    }
}
