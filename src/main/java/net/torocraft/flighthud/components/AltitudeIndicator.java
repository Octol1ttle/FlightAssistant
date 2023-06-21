package net.torocraft.flighthud.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.torocraft.flighthud.Dimensions;
import net.torocraft.flighthud.FlightComputer;
import net.torocraft.flighthud.HudComponent;

public class AltitudeIndicator extends HudComponent {
  private final Dimensions dim;
  private final FlightComputer computer;

  public AltitudeIndicator(FlightComputer computer, Dimensions dim) {
    this.computer = computer;
    this.dim = dim;
  }

  @Override
  public void render(DrawContext context, MinecraftClient mc) {
    float top = dim.tFrame;
    float bottom = dim.bFrame;

    float right = dim.rFrame + 2;
    float left = dim.rFrame;

    float blocksPerPixel = 1;

    float floorOffset = i(computer.altitude * blocksPerPixel);
    float yFloor = dim.yMid - floorOffset;
    float xAltText = right + 5;

    if (CONFIG.altitude_showGroundInfo) {
      drawHeightIndicator(mc, context, left - 1, dim.yMid, bottom - dim.yMid);
    }

    if (CONFIG.altitude_showReadout) {
      drawFont(mc, context, String.format("%.0f", computer.altitude), xAltText, dim.yMid - 3);
      drawBox(context, xAltText - 2, dim.yMid - 4.5f, 28);
    }

    if (CONFIG.altitude_showHeight) {
      drawFont(mc, context, "G", xAltText - 10, bottom + 3);
      String heightText = computer.distanceFromGround == null ? "??"
              : String.format("%d", i(computer.distanceFromGround));
      drawFont(mc, context, heightText, xAltText, bottom + 3);
      drawBox(context, xAltText - 2, bottom + 1.5f, 28);
    }

    if (CONFIG.altitude_showScale) {
      for (int i = -100; i < 1000; i = i + 10) {

        float y = (dim.hScreen - i * blocksPerPixel) - yFloor;
        if (y < top || y > (bottom - 5))
          continue;

        if (i % 50 == 0) {
          drawHorizontalLine(context, left, right + 2, y, CONFIG.color);
          if (!CONFIG.altitude_showReadout || y > dim.yMid + 7 || y < dim.yMid - 7) {
            drawFont(mc, context, String.format("%d", i), xAltText, y - 3);
          }
        }
        drawHorizontalLine(context, left, right, y, CONFIG.color);
      }
    }
  }

  private void drawHeightIndicator(MinecraftClient client, DrawContext context, float x, float top, float h) {
    float bottom = top + h;
    float blocksPerPixel = h / (client.world.getHeight() + 64f);
    float yAlt = bottom - i((computer.altitude + 64) * blocksPerPixel);
    float yFloor = bottom - i(64 * blocksPerPixel);

    drawVerticalLine(context, x, top - 1, bottom + 1, CONFIG.color);

    if (computer.groundLevel != null) {
      float yGroundLevel = bottom - (computer.groundLevel + 64f) * blocksPerPixel;
      fill(context, x - 3, yGroundLevel + 2, x, yFloor, CONFIG.color);
    }

    drawHorizontalLine(context, x - 6, x - 1, top, CONFIG.color);
    drawHorizontalLine(context, x - 6, x - 1, yFloor, CONFIG.color);
    drawHorizontalLine(context, x - 6, x - 1, bottom, CONFIG.color);

    drawPointer(context, x, yAlt, 90);
  }

}
