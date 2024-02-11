package ru.octol1ttle.flightassistant.indicators;

import java.awt.Color;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.computers.autoflight.FireworkController;
import ru.octol1ttle.flightassistant.config.FAConfig;

public class StatusIndicator extends HudComponent {
    private final Dimensions dim;
    private final FireworkController firework;

    public StatusIndicator(Dimensions dim, FireworkController firework) {
        this.dim = dim;
        this.firework = firework;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        float x = dim.rFrame - 5;
        float y = dim.tFrame + 5;

        if (FAConfig.hud().showFireworkCount) {
            Color fireworkColor = FAConfig.hud().statusColor;
            if (firework.safeFireworkCount > 0) {
                if (firework.safeFireworkCount <= 24) {
                    fireworkColor = FAConfig.hud().cautionColor;
                }
            } else {
                fireworkColor = FAConfig.hud().warningColor;
            }
            drawRightAlignedText(textRenderer, context,
                    Text.translatable("status.flightassistant.firework_count", firework.safeFireworkCount),
                    x, y += 10, fireworkColor);
        }
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        drawRightAlignedText(textRenderer, context,
                Text.translatable("flightassistant.status_short"),
                dim.rFrame - 5, dim.tFrame + 15, FAConfig.hud().warningColor);
    }

    @Override
    public String getId() {
        return "status";
    }
}
