package ru.octol1ttle.flightassistant.commands.plan;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.HudRenderer;
import ru.octol1ttle.flightassistant.computers.ComputerHost;
import ru.octol1ttle.flightassistant.computers.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.computers.navigation.Waypoint;

public class ListWaypointsCommand {
    public static int execute(CommandContext<FabricClientCommandSource> context) {
        ComputerHost host = HudRenderer.getHost();
        if (host != null) {
            context.getSource().sendFeedback(Text.translatable("commands.flightassistant.total_waypoints", host.plan.size()));
            FlightPlanner plan = host.plan;
            for (int i = 0; i < plan.size(); i++) {
                Waypoint waypoint = plan.get(i);
                context.getSource().sendFeedback(Text.translatable("commands.flightassistant.waypoint_info",
                        i,
                        waypoint.targetPosition().x,
                        waypoint.targetPosition().y,
                        waypoint.targetAltitude() != null ? waypoint.targetAltitude() : Text.translatable("commands.flightassistant.waypoint_info_not_set"),
                        waypoint.targetSpeed() != null ? waypoint.targetSpeed() : Text.translatable("commands.flightassistant.waypoint_info_not_set")));
            }
        }
        return 0;
    }
}
