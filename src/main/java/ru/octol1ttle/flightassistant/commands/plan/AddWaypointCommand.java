package ru.octol1ttle.flightassistant.commands.plan;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.HudRenderer;
import ru.octol1ttle.flightassistant.computers.navigation.Waypoint;

public class AddWaypointCommand {
    public static int execute(CommandContext<FabricClientCommandSource> context, Waypoint waypoint) {
        HudRenderer.getHost().plan.add(waypoint);
        context.getSource().sendFeedback(Text.translatable("commands.flightassistant.waypoint_created", HudRenderer.getHost().plan.size() - 1, HudRenderer.getHost().plan.size()));
        return 0;
    }
}
