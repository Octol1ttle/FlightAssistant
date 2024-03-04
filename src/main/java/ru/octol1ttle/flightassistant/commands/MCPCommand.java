package ru.octol1ttle.flightassistant.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import ru.octol1ttle.flightassistant.HudRenderer;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class MCPCommand {
    public static void register(LiteralArgumentBuilder<FabricClientCommandSource> builder) {
        var mcp = literal("mcp");
        registerSpeed(mcp);
        registerAltitude(mcp);
        registerHeading(mcp);
        builder.then(mcp);
    }

    private static void registerSpeed(LiteralArgumentBuilder<FabricClientCommandSource> mcp) {
        mcp.then(literal("speed")
                .then(literal("managed")
                        .executes(context -> {
                            HudRenderer.getHost().autoflight.selectedSpeed = null;
                            return 0;
                        })
                )
                .then(argument("target", IntegerArgumentType.integer(0, 30))
                        .executes(context -> {
                            HudRenderer.getHost().autoflight.selectedSpeed = IntegerArgumentType.getInteger(context, "target");
                            return 0;
                        })
                )
        );
    }

    private static void registerAltitude(LiteralArgumentBuilder<FabricClientCommandSource> mcp) {
        mcp.then(literal("altitude")
                .then(literal("managed")
                        .executes(context -> {
                            HudRenderer.getHost().autoflight.selectedAltitude = null;
                            return 0;
                        })
                )
                .then(argument("target", IntegerArgumentType.integer(-120, 1200))
                        .executes(context -> {
                            HudRenderer.getHost().autoflight.selectedAltitude = IntegerArgumentType.getInteger(context, "target");
                            return 0;
                        })
                )
        );
    }

    private static void registerHeading(LiteralArgumentBuilder<FabricClientCommandSource> mcp) {
        mcp.then(literal("heading")
                .then(literal("managed")
                        .executes(context -> {
                            HudRenderer.getHost().autoflight.selectedHeading = null;
                            return 0;
                        })
                )
                .then(argument("target", IntegerArgumentType.integer(0, 360))
                        .executes(context -> {
                            HudRenderer.getHost().autoflight.selectedHeading = IntegerArgumentType.getInteger(context, "target");
                            return 0;
                        })
                )
        );
    }
}
