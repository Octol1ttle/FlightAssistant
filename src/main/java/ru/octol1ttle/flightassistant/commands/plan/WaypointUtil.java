package ru.octol1ttle.flightassistant.commands.plan;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.text.Text;

public class WaypointUtil {
    public static final SimpleCommandExceptionType NO_SUCH_WAYPOINT = new SimpleCommandExceptionType(Text.translatable("commands.flightassistant.no_such_waypoint"));
}
