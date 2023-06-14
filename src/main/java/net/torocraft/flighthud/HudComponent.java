package net.torocraft.flighthud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.torocraft.flighthud.config.HudConfig;

public abstract class HudComponent extends DrawableHelper {

  public abstract void render(MatrixStack m, float partial, MinecraftClient client);

  public static HudConfig CONFIG;

  protected static int i(float f) {
    return Math.round(f);
  }

  public static float wrapHeading(float degrees) {
    degrees = degrees % 360;
    while (degrees < 0) {
      degrees += 360;
    }
    return degrees;
  }

  protected void drawFont(MinecraftClient mc, MatrixStack m, String s, float x, float y) {
    drawFont(mc, m, s, x, y, CONFIG.color);
  }

  protected void drawPointer(MatrixStack m, float x, float y, float rot) {
    m.push();
    m.translate(x, y, 0);
    m.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rot + 45));
    drawVerticalLine(m, 0, 0, 5, CONFIG.color);
    drawHorizontalLine(m, 0, 5, 0, CONFIG.color);
    m.pop();
  }

  protected void drawFont(MinecraftClient mc, MatrixStack m, String s, float x, float y,
                          int color) {
    mc.textRenderer.draw(m, s, x, y, color);
  }

  protected void drawRightAlignedFont(MinecraftClient mc, MatrixStack m, String s, float x,
                                      float y, int color) {
    int w = mc.textRenderer.getWidth(s);
    drawFont(mc, m, s, x - w, y, color);
  }

  protected void drawCenteredFont(MinecraftClient mc, MatrixStack m, String s, float width, float y,
                                  int color) {
    mc.textRenderer.draw(m, s, (width - mc.textRenderer.getWidth(s)) / 2, y, color);
  }

  protected void drawHorizontalLineDashed(MatrixStack m, float x1, float x2, float y,
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
      drawHorizontalLine(m, dx1, dx2, y, color);
    }
  }

  protected void drawHorizontalLine(MatrixStack matrices, float x1, float x2, float y, int color) {
    if (x2 < x1) {
      float i = x1;
      x1 = x2;
      x2 = i;
    }
    fill(matrices, x1 - CONFIG.halfThickness, y - CONFIG.halfThickness, x2 + CONFIG.halfThickness,
        y + CONFIG.halfThickness, color);
  }

  protected void drawVerticalLine(MatrixStack matrices, float x, float y1, float y2, int color) {
    if (y2 < y1) {
      float i = y1;
      y1 = y2;
      y2 = i;
    }

    fill(matrices, x - CONFIG.halfThickness, y1 + CONFIG.halfThickness, x + CONFIG.halfThickness,
            y2 - CONFIG.halfThickness, color);
  }

  public static void fill(MatrixStack matrices, float x1, float y1, float x2, float y2, int color) {
    DrawableHelper.fill(matrices, i(x1), i(y1), i(x2), i(y2), color);
  }

  protected void drawBox(MatrixStack m, float x, float y, float w) {
    drawHorizontalLine(m, x, x + w, y, CONFIG.color);
    drawHorizontalLine(m, x, x + w, y + 10, CONFIG.color);
    drawVerticalLine(m, x, y, y + 10, CONFIG.color);
    drawVerticalLine(m, x + w, y, y + 10, CONFIG.color);
  }
}
