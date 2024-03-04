package ru.octol1ttle.flightassistant.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import ru.octol1ttle.flightassistant.HudRenderer;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ResetCommand {
    public static void register(LiteralArgumentBuilder<FabricClientCommandSource> builder) {
        builder
                .then(literal("reset")
                        .then(literal("computers")
                                .then(literal("all")
                                        .executes(context -> {
                                            HudRenderer.getHost().resetComputers(true);
                                            return 0;
                                        }))
                                .then(literal("faulted")
                                        .executes(context -> {
                                            HudRenderer.getHost().resetComputers(false);
                                            return 0;
                                        })
                                ))
                        .then(literal("indicators")
                                .executes(context -> {
                                    HudRenderer.INSTANCE.resetFaulted();
                                    return 0;
                                })
                        )
                );
    }
}
