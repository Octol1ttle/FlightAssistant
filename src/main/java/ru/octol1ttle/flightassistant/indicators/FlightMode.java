package ru.octol1ttle.flightassistant.indicators;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.computers.TimeComputer;

import static ru.octol1ttle.flightassistant.HudComponent.CONFIG;

public class FlightMode {
    private static final int UPDATE_FLASH_TIME = 2000;
    private final TimeComputer time;
    @Nullable
    private Float lastUpdateTime;
    @Nullable
    private Text lastText;

    public FlightMode(TimeComputer time) {
        this.time = time;
    }

    public void update(Text newText) {
        if (newText.equals(lastText)) {
            return;
        }

        lastUpdateTime = time.prevMillis;
        lastText = newText;
    }

    public void render(DrawContext context, TextRenderer textRenderer, float x, float y) {
        if (lastUpdateTime == null) {
            throw new IllegalStateException("Called render before updating");
        }
        if (time.prevMillis - lastUpdateTime < UPDATE_FLASH_TIME) {
            HudComponent.drawHighlightedFont(textRenderer, context,
                    lastText,
                    x - textRenderer.getWidth(lastText) * 0.5f, y,
                    CONFIG.amberColor, time.highlight);
            return;
        }

        HudComponent.drawFont(textRenderer, context, lastText, x - textRenderer.getWidth(lastText) * 0.5f, y, CONFIG.white);
    }
}
