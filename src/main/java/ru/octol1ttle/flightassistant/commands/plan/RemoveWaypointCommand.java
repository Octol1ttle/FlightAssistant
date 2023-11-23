package ru.octol1ttle.flightassistant.commands.plan;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.HudRenderer;
import ru.octol1ttle.flightassistant.computers.ComputerHost;

public class RemoveWaypointCommand implements Command<FabricClientCommandSource> {
    @Override
    public int run(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        ComputerHost host = HudRenderer.getHost();
        if (host != null) {
            int waypointIndex = IntegerArgumentType.getInteger(context, "waypointIndex");
            if (!host.plan.waypointExistsAt(waypointIndex)) {
                throw WaypointUtil.NO_SUCH_WAYPOINT.create();
            }
            host.plan.remove(waypointIndex);
            context.getSource().sendFeedback(Text.translatable("commands.flightassistant.waypoint_removed", waypointIndex, host.plan.size()));
        }
        return 0;
    }
}
