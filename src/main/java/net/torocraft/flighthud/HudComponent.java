package net.torocraft.flighthud;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import net.torocraft.flighthud.config.HudConfig;
import net.torocraft.flighthud.shims.DrawContext;

public abstract class HudComponent {
    public static HudConfig CONFIG;

    public static void fill(DrawContext context, float x1, float y1, float x2, float y2, int color) {
        context.fill(i(x1), i(y1), i(x2), i(y2), color);
    }

    protected static int i(float f) {
        return MathHelper.floor(f);
    }

    public static float wrapHeading(float degrees) {
        degrees = degrees % 360;
        while (degrees < 0) {
            degrees += 360;
        }
        return degrees;
    }

    public static int drawFont(MinecraftClient mc, DrawContext context, String s, float x, float y,
                               int color) {
        context.drawText(mc.textRenderer, s, i(x), i(y), color, false);
        return 1;
    }

    public static void drawCenteredFont(MinecraftClient mc, DrawContext context, String s, float width, float y,
                                        int color) {
        context.drawText(mc.textRenderer, s, i(width - mc.textRenderer.getWidth(s)) / 2, i(y), color, false);
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

    public static void drawTextHighlight(TextRenderer renderer, DrawContext context, float x, float y, String text, int color) {
        HudComponent.fill(context, x - 2.0f, y - 1.0f, x + renderer.getWidth(text) + 1.0f, y + 8.0f, color);
    }

    public static void drawUnbatched(Runnable draw) {
        if (FabricLoader.getInstance().isModLoaded("immediatelyfast"))
            net.torocraft.flighthud.compat.ImmediatelyFastBatchingAccessor.endHudBatching();
        draw.run();
        if (FabricLoader.getInstance().isModLoaded("immediatelyfast"))
            net.torocraft.flighthud.compat.ImmediatelyFastBatchingAccessor.beginHudBatching();
    }

    public abstract void render(DrawContext context, MinecraftClient client);

    protected void drawFont(MinecraftClient mc, DrawContext context, String s, float x, float y) {
        drawFont(mc, context, s, x, y, CONFIG.color);
    }

    protected void drawPointer(DrawContext context, float x, float y, float rot) {
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        context.getMatrices().multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rot + 45));
        drawVerticalLine(context, 0, 0, 5, CONFIG.color);
        drawHorizontalLine(context, 0, 5, 0, CONFIG.color);
        context.getMatrices().pop();
    }

    protected void drawRightAlignedFont(MinecraftClient mc, DrawContext context, String s, float x,
                                        float y, int color) {
        int w = mc.textRenderer.getWidth(s);
        drawFont(mc, context, s, x - w, y, color);
    }

    protected void drawHorizontalLineDashed(DrawContext context, float x1, float x2, float y,
                                            int dashCount, int color) {
        float width = x2 - x1;
        int segmentCount = dashCount * 2 - 1;
        float dashSize = width / segmentCount;
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
}
