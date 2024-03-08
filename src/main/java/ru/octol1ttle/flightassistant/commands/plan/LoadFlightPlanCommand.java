package ru.octol1ttle.flightassistant.commands.plan;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.List;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.HudRenderer;
import ru.octol1ttle.flightassistant.computers.ComputerHost;
import ru.octol1ttle.flightassistant.computers.navigation.Waypoint;
import ru.octol1ttle.flightassistant.config.FlightPlanNbt;

public class LoadFlightPlanCommand {
    public static final SimpleCommandExceptionType NO_SUCH_PLAN = new SimpleCommandExceptionType(Text.translatable("commands.flightassistant.no_such_plan"));

    public static int execute(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        ComputerHost host = HudRenderer.getHost();
        String name = StringArgumentType.getString(context, "name");
        List<Waypoint> loaded = FlightPlanNbt.read(name);
        if (loaded == null) {
            throw NO_SUCH_PLAN.create();
        }
        host.plan.clear();
        host.plan.addAll(loaded);
        context.getSource().sendFeedback(Text.translatable("commands.flightassistant.flight_plan_loaded", host.plan.size(), name));
        return 0;
    }
}
