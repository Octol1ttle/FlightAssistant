package net.torocraft.flighthud.alerts;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.torocraft.flighthud.HudComponent;

import static net.torocraft.flighthud.HudComponent.CONFIG;

public abstract class Alert {
    public boolean hidden;

    public static int drawWarning(MinecraftClient mc, DrawContext context, float x, float y, boolean highlight, String text) {
        HudComponent.drawUnbatched(() -> {
            if (highlight) {
                HudComponent.drawTextHighlight(mc.textRenderer, context, x, y, text, CONFIG.alertColor);
                HudComponent.drawFont(mc, context, text, x, y, CONFIG.white);
            } else {
                HudComponent.drawFont(mc, context, text, x, y, CONFIG.alertColor);
            }
        });
        return 1;
    }

    public static int drawCaution(MinecraftClient mc, DrawContext context, float x, float y, boolean highlight, String text) {
        HudComponent.drawUnbatched(() -> {
            if (highlight) {
                HudComponent.drawTextHighlight(mc.textRenderer, context, x, y, text, CONFIG.amberColor);
                HudComponent.drawFont(mc, context, text, x, y, CONFIG.black);
            } else {
                HudComponent.drawFont(mc, context, text, x, y, CONFIG.amberColor);
            }
        });
        return 1;
    }

    public abstract boolean shouldActivate();

    public abstract int drawText(MinecraftClient mc, DrawContext context, float x, float y, boolean highlight);
}
