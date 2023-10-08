package net.torocraft.flighthud;

import java.util.function.Consumer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.RotationAxis;
import net.torocraft.flighthud.config.HudConfig;

public abstract class HudComponent {
    public static HudConfig CONFIG;

    public static int drawFont(TextRenderer textRenderer, DrawContext context, Text text, float x, float y,
                               int color) {
        context.drawText(textRenderer, text, i(x), i(y), color, false);
        return 1;
    }

    public static void fill(DrawContext context, float x1, float y1, float x2, float y2, int color) {
        context.fill(i(x1), i(y1), i(x2), i(y2), color);
    }

    protected static int i(float f) {
        return Math.round(f);
    }

    public static int drawFont(TextRenderer textRenderer, DrawContext context, String text, float x, float y,
                               int color) {
        context.drawText(textRenderer, text, i(x), i(y), color, false);
        return 1;
    }

    public static void drawHighlightedFont(TextRenderer textRenderer, DrawContext context, float x, float y, Text text, int highlightColor, boolean highlight) {
        if (highlight) {
            HudRenderer.drawUnbatched(context, ctx -> {
                HudComponent.fill(context, x - 1.5f, y - 1.5f, x + textRenderer.getWidth(text), y + 8.0f, highlightColor);
                HudComponent.drawFont(textRenderer, context, text, x, y, CONFIG.white);
            });
            return;
        }
        HudComponent.drawFont(textRenderer, context, text, x, y, highlightColor);
    }

    public static void drawUnbatched(DrawContext context, Consumer<DrawContext> c) {
        if (FabricLoader.getInstance().isModLoaded("immediatelyfast"))
            net.torocraft.flighthud.compatibility.ImmediatelyFastBatchingAccessor.endHudBatching();
        c.accept(context);
        if (FabricLoader.getInstance().isModLoaded("immediatelyfast"))
            net.torocraft.flighthud.compatibility.ImmediatelyFastBatchingAccessor.beginHudBatching();
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

    protected static void drawPointer(DrawContext context, float x, float y, float rot) {
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rot + 45));
        drawVerticalLine(context, 0, 0, 5, CONFIG.color);
        drawHorizontalLine(context, 0, 5, 0, CONFIG.color);
        context.getMatrices().pop();
    }

    protected static void drawRightAlignedFont(TextRenderer textRenderer, DrawContext context, String s, float x,
                                        float y, int color) {
        int w = textRenderer.getWidth(s);
        drawFont(textRenderer, context, s, x - w, y, color);
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
}
