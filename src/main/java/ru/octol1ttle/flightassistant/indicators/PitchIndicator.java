package ru.octol1ttle.flightassistant.indicators;

import java.awt.Color;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.autoflight.PitchController;
import ru.octol1ttle.flightassistant.computers.safety.StallComputer;
import ru.octol1ttle.flightassistant.computers.safety.VoidLevelComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;

public class PitchIndicator extends HudComponent {
    public static final int DEGREES_PER_BAR = 20;
    private final Dimensions dim;
    private final AirDataComputer data;
    private final StallComputer stall;
    private final VoidLevelComputer voidLevel;
    private final PitchIndicatorData pitchData = new PitchIndicatorData();

    public PitchIndicator(Dimensions dim, AirDataComputer data, StallComputer stall, VoidLevelComputer voidLevel) {
        this.dim = dim;
        this.data = data;
        this.stall = stall;
        this.voidLevel = voidLevel;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        if (!FAConfig.hud().showPitchLadder) {
            return;
        }

        pitchData.update(dim);

        float horizonOffset = data.pitch * dim.degreesPerPixel;
        float yHorizon = dim.yMid + horizonOffset;

        float xMid = dim.xMid;
        float yMid = dim.yMid;

        context.getMatrices().push();
        context.getMatrices().translate(xMid, yMid, 0);
        context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(data.roll));
        context.getMatrices().translate(-xMid, -yMid, 0);

        drawLadder(textRenderer, context, yHorizon);

        drawReferenceMark(context, yHorizon, stall.maximumSafePitch, FAConfig.hud().warningColor);
        drawReferenceMark(context, yHorizon, PitchController.CLIMB_PITCH, getPitchColor(PitchController.CLIMB_PITCH));
        drawReferenceMark(context, yHorizon, PitchController.GLIDE_PITCH, getPitchColor(PitchController.GLIDE_PITCH));
        drawReferenceMark(context, yHorizon, voidLevel.minimumSafePitch, FAConfig.hud().warningColor);

        pitchData.l1 -= pitchData.margin;
        pitchData.r2 += pitchData.margin;
        drawDegreeBar(textRenderer, context, 0, yHorizon);

        context.getMatrices().pop();
    }

    private Color getPitchColor(float degree) {
        return degree < Math.max(PitchController.DESCEND_PITCH, voidLevel.minimumSafePitch) || degree > stall.maximumSafePitch
                ? FAConfig.hud().warningColor : FAConfig.hud().frameColor;
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        drawMiddleAlignedText(textRenderer, context, Text.translatable("flightassistant.pitch_short"), dim.xMid, dim.yMid - 10, FAConfig.hud().warningColor);
    }

    @Override
    public String getId() {
        return "pitch";
    }

    private void drawLadder(TextRenderer textRenderer, DrawContext context, float yHorizon) {
        for (int i = DEGREES_PER_BAR; i <= 90; i += DEGREES_PER_BAR) {
            float offset = dim.degreesPerPixel * i;
            drawDegreeBar(textRenderer, context, -i, yHorizon + offset);
            drawDegreeBar(textRenderer, context, i, yHorizon - offset);
        }
    }

    private void drawReferenceMark(DrawContext context, float yHorizon, float degrees, Color color) {
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

        drawHorizontalLineDashed(context, l1, pitchData.l2, y, 3, color);
        drawHorizontalLineDashed(context, pitchData.r1, r2, y, 3, color);
    }

    private void drawDegreeBar(TextRenderer textRenderer, DrawContext context, float degree, float y) {
        if (y < dim.tFrame || y > dim.bFrame) {
            return;
        }

        Color color = getPitchColor(degree);
        int dashes = degree < 0 ? 4 : 1;

        drawHorizontalLineDashed(context, pitchData.l1, pitchData.l2, y, dashes, color);
        drawHorizontalLineDashed(context, pitchData.r1, pitchData.r2, y, dashes, color);

        int sideTickHeight = degree >= 0 ? 5 : -5;
        drawVerticalLine(context, pitchData.l1, y, y + sideTickHeight, color);
        drawVerticalLine(context, pitchData.r2, y, y + sideTickHeight, color);

        int fontVerticalOffset = degree >= 0 ? 0 : 6;

        drawText(textRenderer, context, asText("%d", i(Math.abs(degree))), pitchData.r2 + 6,
                y - fontVerticalOffset, color);

        drawText(textRenderer, context, asText("%d", i(Math.abs(degree))), pitchData.l1 - 17,
                y - fontVerticalOffset, color);
    }

    private static class PitchIndicatorData {
        public float width;
        public float margin;
        public float sideWidth;
        public float l1;
        public float l2;
        public float r1;
        public float r2;

        public void update(Dimensions dim) {
            width = dim.wFrame * 0.5f;
            float left = dim.lFrame + (width * 0.5f);

            margin = i(width * 0.3d);
            l1 = left + margin;
            l2 = dim.xMid - 7;
            sideWidth = l2 - l1;
            r1 = dim.xMid + 8;
            r2 = r1 + sideWidth;
        }

        private int i(double d) {
            return MathHelper.floor(d);
        }
    }
}
