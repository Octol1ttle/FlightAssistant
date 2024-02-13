package ru.octol1ttle.flightassistant;

import java.awt.Color;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.compatibility.ImmediatelyFastBatchingAccessor;
import ru.octol1ttle.flightassistant.config.FAConfig;

public abstract class HudComponent {
    private static final int SINGLE_LINE_DRAWN = 1;

    public static MutableText asText(String format, Object... args) {
        return Text.literal(String.format(format, args));
    }

    public static void fill(DrawContext context, float x1, float y1, float x2, float y2, Color color) {
        context.fill(i(x1), i(y1), i(x2), i(y2), color.getRGB());
    }

    protected static int i(float f) {
        return MathHelper.floor(f);
    }

    protected static void drawRightAlignedText(TextRenderer textRenderer, DrawContext context, Text text, float x, float y, Color color) {
        drawText(textRenderer, context, text, x - textRenderer.getWidth(text), y, color);
    }

    public static int drawText(TextRenderer textRenderer, DrawContext context, Text text, float x, float y, Color color) {
        context.drawText(textRenderer, text, i(x), i(y), color.getRGB(), false);
        return SINGLE_LINE_DRAWN;
    }

    public static void drawMiddleAlignedText(TextRenderer textRenderer, DrawContext context, Text text, float x, float y, Color color) {
        drawText(textRenderer, context, text, x - textRenderer.getWidth(text) * 0.5f, y, color);
    }

    public static int drawHighlightedText(TextRenderer textRenderer, DrawContext context, Text text, float x, float y, Color color, boolean highlight) {
        drawUnbatched(() -> {
            if (highlight) {
                HudComponent.fill(context, x - 2.0f, y - 1.0f, x + textRenderer.getWidth(text) + 1.0f, y + 8.0f, color);
                HudComponent.drawText(textRenderer, context, text, x, y, getContrasting(color));
            } else {
                HudComponent.drawText(textRenderer, context, text, x, y, color);
            }
        });
        return SINGLE_LINE_DRAWN;
    }

    private static Color getContrasting(Color original) {
        double luma = ((0.299 * original.getRed()) + (0.587 * original.getGreen()) + (0.114 * original.getBlue())) / 255.0d;
        return luma > 0.5d ? Color.BLACK : Color.WHITE;
    }

    // that name doe
    public static void drawHighlightedMiddleAlignedText(TextRenderer textRenderer, DrawContext context, Text text, float x, float y, Color color, boolean highlight) {
        drawHighlightedText(textRenderer, context, text, x - textRenderer.getWidth(text) * 0.5f, y, color, highlight);
    }

    public static void drawUnbatched(Runnable draw) {
        if (FlightAssistant.isHUDBatched()) {
            ImmediatelyFastBatchingAccessor.endHudBatching();
        }
        draw.run();
        if (FlightAssistant.isHUDBatched()) {
            ImmediatelyFastBatchingAccessor.beginHudBatching();
        }
    }

    public static void drawHorizontalLine(DrawContext context, float x1, float x2, float y, Color color) {
        if (x2 < x1) {
            float i = x1;
            x1 = x2;
            x2 = i;
        }
        fill(context, x1 - FAConfig.halfThickness(), y - FAConfig.halfThickness(), x2 + FAConfig.halfThickness(),
                y + FAConfig.halfThickness(), color);
    }

    public static void drawVerticalLine(DrawContext context, float x, float y1, float y2, Color color) {
        if (y2 < y1) {
            float i = y1;
            y1 = y2;
            y2 = i;
        }

        fill(context, x - FAConfig.halfThickness(), y1 + FAConfig.halfThickness(), x + FAConfig.halfThickness(),
                y2 - FAConfig.halfThickness(), color);
    }

    public static void drawBox(DrawContext context, float x, float y, float w, Color color) {
        drawHorizontalLine(context, x, x + w, y, color);
        drawHorizontalLine(context, x, x + w, y + 10, color);
        drawVerticalLine(context, x, y, y + 10, color);
        drawVerticalLine(context, x + w, y, y + 10, color);
    }

    protected static void drawHorizontalLineDashed(DrawContext context, float x1, float x2, float y,
                                                   int dashCount, Color color) {
        float width = x2 - x1;
        int segmentCount = dashCount * 2 - 1;
        float dashSize = width / (float) segmentCount;
        for (int i = 0; i < segmentCount; i++) {
            if (i % 2 != 0) {
                continue;
            }
            float dx1 = i * dashSize + x1;
            float dx2;
            if (i == segmentCount - 1) {
                dx2 = x2;
            } else {
                dx2 = ((i + 1) * dashSize) + x1;
            }
            drawHorizontalLine(context, dx1, dx2, y, color);
        }
    }

    public abstract void render(DrawContext context, TextRenderer textRenderer);

    public abstract void renderFaulted(DrawContext context, TextRenderer textRenderer);

    public abstract String getId();
}
