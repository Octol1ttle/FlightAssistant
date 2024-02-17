package net.torocraft.flighthud.shims;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

// Shim class for 1.20.x+
public class DrawContext extends DrawableHelper {

    private final MatrixStack matrixStack;
    private final float tickDelta;

    public DrawContext(MatrixStack m, float tickDelta) {
        this.matrixStack = m;
        this.tickDelta = tickDelta;
    }

    public MatrixStack getMatrices() {
        return matrixStack;
    }

    public float getTickDelta() {
        return tickDelta;
    }

    public void drawText(TextRenderer textRenderer, String text, int x, int y, int color) {
        textRenderer.draw(matrixStack, text, x, y, color);
    }

    public void drawText(TextRenderer textRenderer, String text, int x, int y, int color, boolean shadow) {
        if (shadow) {
            textRenderer.drawWithShadow(matrixStack, text, x, y, color);
        } else {
            textRenderer.draw(matrixStack, text, x, y, color);
        }
    }

    public void fill(int x1, int y1, int x2, int y2, int color) {
        fill(matrixStack, x1, y1, x2, y2, color);
    }

    public static DrawContext from(MatrixStack matrixStack, float tickDelta) {
        return new DrawContext(matrixStack, tickDelta);
    }
}
