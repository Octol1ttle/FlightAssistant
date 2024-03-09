package ru.octol1ttle.flightassistant.commands.plan;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.computers.navigation.FlightPlanner;

public class WaypointUtil {
    public static final SimpleCommandExceptionType FLIGHT_PLAN_EMPTY = new SimpleCommandExceptionType(Text.translatable("commands.flightassistant.flight_plan_empty"));
    public static final SimpleCommandExceptionType NO_SUCH_WAYPOINT = new SimpleCommandExceptionType(Text.translatable("commands.flightassistant.no_such_waypoint"));

    public static void throwOnNotFound(FlightPlanner plan, int index) throws CommandSyntaxException {
        if (plan.isEmpty()) {
            throw FLIGHT_PLAN_EMPTY.create();
        }
        if (!plan.waypointExistsAt(index)) {
            throw NO_SUCH_WAYPOINT.create();
        }
    }
}
