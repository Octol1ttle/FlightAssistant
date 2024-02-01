package ru.octol1ttle.flightassistant;

import java.awt.Color;
import java.util.Map;
import java.util.function.Consumer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.compatibility.ImmediatelyFastBatchingAccessor;
import ru.octol1ttle.flightassistant.config.HudConfig;

public abstract class HudComponent {
    public static HudConfig CONFIG;
    private static final int SINGLE_LINE_DRAWN = 1;
    private static final Map<Integer, Integer> colorHighlightMap = Map.of(
            Color.RED.getRGB(), Color.WHITE.getRGB(),
            Color.YELLOW.getRGB(), Color.BLACK.getRGB()
    );

    public static MutableText asText(String format, Object... args) {
        return Text.literal(String.format(format, args));
    }

    public static void fill(DrawContext context, float x1, float y1, float x2, float y2, int color) {
        context.fill(i(x1), i(y1), i(x2), i(y2), color);
    }

    protected static int i(float f) {
        return (int) f;
    }

    protected static void drawRightAlignedText(TextRenderer textRenderer, DrawContext context, Text text, float x, float y, int color) {
        drawText(textRenderer, context, text, x - textRenderer.getWidth(text), y, color);
    }

    public static void drawText(TextRenderer textRenderer, DrawContext context, Text text, float x, float y, int color) {
        context.drawText(textRenderer, text, i(x), i(y), color, false);
    }

    public static void drawMiddleAlignedText(TextRenderer textRenderer, DrawContext context, Text text, float x, float y, int color) {
        drawText(textRenderer, context, text, x - textRenderer.getWidth(text) * 0.5f, y, color);
    }

    public static int drawHighlightedText(TextRenderer textRenderer, DrawContext context, Text text, float x, float y, int highlightColor, boolean highlight) {
        if (highlight) {
            drawUnbatched(context, ctx -> {
                HudComponent.fill(context, x - 2.0f, y - 1.0f, x + textRenderer.getWidth(text) + 1.0f, y + 8.0f, highlightColor);
                HudComponent.drawText(textRenderer, context, text, x, y, colorHighlightMap.get(highlightColor));
            });
            return SINGLE_LINE_DRAWN;
        }
        HudComponent.drawText(textRenderer, context, text, x, y, highlightColor);

        return 1;
    }

    // that name doe
    public static void drawHighlightedMiddleAlignedText(TextRenderer textRenderer, DrawContext context, Text text, float x, float y, int highlightColor, boolean highlight) {
        drawHighlightedText(textRenderer, context, text, x - textRenderer.getWidth(text) * 0.5f, y, highlightColor, highlight);
    }

    public static void drawUnbatched(DrawContext context, Consumer<DrawContext> c) {
        if (FabricLoader.getInstance().isModLoaded("immediatelyfast")) {
            ImmediatelyFastBatchingAccessor.endHudBatching();
        }
        c.accept(context);
        if (FabricLoader.getInstance().isModLoaded("immediatelyfast")) {
            ImmediatelyFastBatchingAccessor.beginHudBatching();
        }
    }

    public static void drawHorizontalLine(DrawContext context, float x1, float x2, float y, int color) {
        if (x2 < x1) {
            float i = x1;
            x1 = x2;
            x2 = i;
        }
        fill(context, x1 - CONFIG.halfThickness, y - CONFIG.halfThickness, x2 + CONFIG.halfThickness,
                y + CONFIG.halfThickness, color);
    }

    public static void drawVerticalLine(DrawContext context, float x, float y1, float y2, int color) {
        if (y2 < y1) {
            float i = y1;
            y1 = y2;
            y2 = i;
        }

        fill(context, x - CONFIG.halfThickness, y1 + CONFIG.halfThickness, x + CONFIG.halfThickness,
                y2 - CONFIG.halfThickness, color);
    }

    public static void drawBox(DrawContext context, float x, float y, float w, int color) {
        drawHorizontalLine(context, x, x + w, y, color);
        drawHorizontalLine(context, x, x + w, y + 10, color);
        drawVerticalLine(context, x, y, y + 10, color);
        drawVerticalLine(context, x + w, y, y + 10, color);
    }

    protected static void drawHorizontalLineDashed(DrawContext context, float x1, float x2, float y,
                                            int dashCount, int color) {
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
