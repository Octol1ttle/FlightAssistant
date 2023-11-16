package ru.octol1ttle.flightassistant.indicators;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.computers.autoflight.FireworkController;

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

        int fireworkColor = CONFIG.white;
        if (firework.safeFireworkCount > 0) {
            if (firework.safeFireworkCount <= 24) {
                fireworkColor = CONFIG.amberColor;
            }
        } else {
            fireworkColor = CONFIG.alertColor;
        }
        drawRightAlignedFont(textRenderer, context,
                Text.translatable("status.flightassistant.firework_count", firework.safeFireworkCount),
                x, y += 10, fireworkColor);
    }
}
