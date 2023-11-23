package ru.octol1ttle.flightassistant.commands.plan;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.HudRenderer;
import ru.octol1ttle.flightassistant.computers.ComputerHost;

public class InsertWaypointCommand implements Command<FabricClientCommandSource> {
    @Override
    public int run(CommandContext<FabricClientCommandSource> context) {
        ComputerHost host = HudRenderer.getHost();
        if (host != null) {
            int waypointIndex = IntegerArgumentType.getInteger(context, "insertAt");
            host.plan.add(waypointIndex, WaypointUtil.fromCommandContext(context));
            context.getSource().sendFeedback(Text.translatable("commands.flightassistant.waypoint_inserted", waypointIndex, host.plan.size()));
        }
        return 0;
    }
}
