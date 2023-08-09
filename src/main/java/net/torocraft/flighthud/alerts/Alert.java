package net.torocraft.flighthud.alerts;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.torocraft.flighthud.HudComponent;

import static net.torocraft.flighthud.HudComponent.CONFIG;

public abstract class Alert {
    public boolean hidden;

    public static int drawWarning(MinecraftClient mc, DrawContext context, float x, float y, boolean highlight, String text) {
        if (highlight) {
            HudComponent.fill(context, x - 1.5f, y - 1.5f, x + mc.textRenderer.getWidth(text) + CONFIG.halfThickness, y + 9, CONFIG.alertColor);
            HudComponent.drawFont(mc, context, text, x, y, CONFIG.white);
            return 1;
        }
        HudComponent.drawFont(mc, context, text, x, y, CONFIG.alertColor);
        return 1;
    }

    public static int drawCaution(MinecraftClient mc, DrawContext context, float x, float y, boolean highlight, String text) {
        if (highlight) {
            HudComponent.fill(context, x - 1.5f, y - 1.5f, x + mc.textRenderer.getWidth(text) + CONFIG.halfThickness, y + 9, CONFIG.amberColor);
            HudComponent.drawFont(mc, context, text, x, y, CONFIG.black);
            return 1;
        }
        HudComponent.drawFont(mc, context, text, x, y, CONFIG.amberColor);
        return 1;
    }

    public abstract boolean shouldActivate();

    public abstract int drawText(MinecraftClient mc, DrawContext context, float x, float y, boolean highlight);
}
