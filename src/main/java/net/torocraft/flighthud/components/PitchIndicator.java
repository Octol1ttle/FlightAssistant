package net.torocraft.flighthud.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.torocraft.flighthud.Dimensions;
import net.torocraft.flighthud.FlightComputer;
import net.torocraft.flighthud.HudComponent;

public class PitchIndicator extends HudComponent {
  private final Dimensions dim;
  private final FlightComputer computer;
  private final PitchIndicatorData pitchData = new PitchIndicatorData();

  public PitchIndicator(FlightComputer computer, Dimensions dim) {
    this.computer = computer;
    this.dim = dim;
  }

  @Override
  public void render(MatrixStack m, float partial, MinecraftClient mc) {
    pitchData.update(dim);

    float horizonOffset = computer.pitch * dim.degreesPerPixel;
    float yHorizon = dim.yMid + horizonOffset;

    float a = dim.yMid;
    float b = dim.xMid;

    float roll = computer.roll * (CONFIG.pitchLadder_reverseRoll ? -1 : 1);

    if (CONFIG.pitchLadder_showRoll) {
        m.push();
        m.translate(b, a, 0);
        m.multiply(HudComponent.getDegreesQuaternion(roll));
        m.translate(-b, -a, 0);
    }

      if (CONFIG.pitchLadder_showLadder) {
          drawLadder(mc, m, yHorizon);
      }

      drawReferenceMark(m, yHorizon, CONFIG.pitchLadder_optimumClimbAngle);
      drawReferenceMark(m, yHorizon, CONFIG.pitchLadder_optimumGlideAngle);

      if (CONFIG.pitchLadder_showHorizon) {
          pitchData.l1 -= pitchData.margin;
          pitchData.r2 += pitchData.margin;
          drawDegreeBar(mc, m, 0, yHorizon);
      }

      if (CONFIG.pitchLadder_showRoll) {
          m.pop();
      }
  }

  private void drawLadder(MinecraftClient mc, MatrixStack m, float yHorizon) {
    int degreesPerBar = CONFIG.pitchLadder_degreesPerBar;

    if (degreesPerBar < 1) {
      degreesPerBar = 20;
    }

    for (int i = degreesPerBar; i <= 90; i = i + degreesPerBar) {
      float offset = dim.degreesPerPixel * i;
      drawDegreeBar(mc, m, -i, yHorizon + offset);
      drawDegreeBar(mc, m, i, yHorizon - offset);
    }

  }

    private void drawReferenceMark(MatrixStack m, float yHorizon, float degrees) {
        if (degrees == 0) {
            return;
        }

        float y = (-degrees * dim.degreesPerPixel) + yHorizon;

        if (y < dim.tFrame || y > dim.bFrame) {
            return;
        }

    float width = (pitchData.l2 - pitchData.l1) * 0.45f;
    float l1 = pitchData.l2 - width;
    float r2 = pitchData.r1 + width;

    drawHorizontalLineDashed(m, l1, pitchData.l2, y, 3);
    drawHorizontalLineDashed(m, pitchData.r1, r2, y, 3);
  }

  private void drawDegreeBar(MinecraftClient mc, MatrixStack m, float degree, float y) {

    if (y < dim.tFrame || y > dim.bFrame) {
      return;
    }

    int dashes = degree < 0 ? 4 : 1;

    drawHorizontalLineDashed(m, pitchData.l1, pitchData.l2, y, dashes);
    drawHorizontalLineDashed(m, pitchData.r1, pitchData.r2, y, dashes);

    int sideTickHeight = degree >= 0 ? 5 : -5;
    drawVerticalLine(m, pitchData.l1, y, y + sideTickHeight);
    drawVerticalLine(m, pitchData.r2, y, y + sideTickHeight);

    int fontVerticalOffset = degree >= 0 ? 0 : 6;

      drawFont(mc, m, String.format("%d", i(Math.abs(degree))), pitchData.r2 + 6,
              y - fontVerticalOffset);

      drawFont(mc, m, String.format("%d", i(Math.abs(degree))), pitchData.l1 - 17,
              y - fontVerticalOffset);
  }

  private static class PitchIndicatorData {
    public float width;
    public float mid;
    public float margin;
    public float sideWidth;
    public float l1;
    public float l2;
    public float r1;
    public float r2;

    public void update(Dimensions dim) {
      width = i(dim.wScreen / 3);
      float left = width;

      mid = i((width / 2) + left);
      margin = i(width * 0.3d);
      l1 = left + margin;
      l2 = mid - 7;
      sideWidth = l2 - l1;
      r1 = mid + 8;
      r2 = r1 + sideWidth;
    }

    private int i(double d) {
      return (int) Math.round(d);
    }
  }

}
