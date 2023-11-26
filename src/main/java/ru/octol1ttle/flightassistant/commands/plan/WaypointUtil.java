package ru.octol1ttle.flightassistant.commands.plan;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import org.joml.Vector2d;
import ru.octol1ttle.flightassistant.computers.navigation.Waypoint;

public class WaypointUtil {
    public static final SimpleCommandExceptionType NO_SUCH_WAYPOINT = new SimpleCommandExceptionType(Text.translatable("commands.flightassistant.no_such_waypoint"));

    public static Waypoint fromCommandContext(CommandContext<FabricClientCommandSource> context) {
        Vector2d targetPosition = new Vector2d(IntegerArgumentType.getInteger(context, "targetX"), IntegerArgumentType.getInteger(context, "targetZ"));
        Integer targetAltitude = null;
        Integer targetSpeed = null;
        try {
            targetAltitude = IntegerArgumentType.getInteger(context, "targetAltitude");
            targetSpeed = IntegerArgumentType.getInteger(context, "targetSpeed");
        } catch (IllegalArgumentException ignored) {
        } // TODO: make proper optional arguments

        return new Waypoint(targetPosition, targetAltitude, targetSpeed);
    }
}
