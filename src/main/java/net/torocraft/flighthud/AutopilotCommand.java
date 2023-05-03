package net.torocraft.flighthud;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class AutopilotCommand implements Command<FabricClientCommandSource> {
    public static final SimpleCommandExceptionType CANNOT_USE_AP_IN_NETHER = new SimpleCommandExceptionType(Text.translatable("commands.flighthud.cannotUseApInNether"));
    @Override
    public int run(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        int cruiseAltitude = IntegerArgumentType.getInteger(context, "cruiseAltitude");
        if (context.getSource().getWorld().getDimension().hasCeiling() && (context.getSource().getPosition().y < 128 || cruiseAltitude < 128))
            throw CANNOT_USE_AP_IN_NETHER.create();
        HudRenderer.INSTANCE.automationComponent.setAutopilotSettings(IntegerArgumentType.getInteger(context, "destinationX"), IntegerArgumentType.getInteger(context, "destinationZ"), cruiseAltitude);
        return 0;
    }
}
