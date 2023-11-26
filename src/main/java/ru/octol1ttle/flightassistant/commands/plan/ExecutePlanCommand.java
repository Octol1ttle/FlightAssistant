package ru.octol1ttle.flightassistant.commands.plan;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import ru.octol1ttle.flightassistant.HudRenderer;
import ru.octol1ttle.flightassistant.computers.ComputerHost;

public class ExecutePlanCommand implements Command<FabricClientCommandSource> {
    @Override
    public int run(CommandContext<FabricClientCommandSource> context) {
        ComputerHost host = HudRenderer.getHost();
        if (host != null) {
            host.plan.execute(0);
        }
        return 0;
    }
}