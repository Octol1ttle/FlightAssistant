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

public class AttitudeIndicator extends HudComponent {
    public static final int DEGREES_PER_BAR = 20;
    private final Dimensions dim;
    private final AirDataComputer data;
    private final StallComputer stall;
    private final VoidLevelComputer voidLevel;
    private final AttitudeIndicatorData pitchData = new AttitudeIndicatorData();

    public AttitudeIndicator(Dimensions dim, AirDataComputer data, StallComputer stall, VoidLevelComputer voidLevel) {
        this.dim = dim;
        this.data = data;
        this.stall = stall;
        this.voidLevel = voidLevel;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        if (!FAConfig.indicator().showAttitudeIndicator) {
            return;
        }

        pitchData.update(dim);

        int yHorizon = MathHelper.floor(dim.yMid + data.pitch() * dim.degreesPerPixel);

        int xMid = dim.xMid;
        int yMid = dim.yMid;

        context.getMatrices().push();
        context.getMatrices().translate(xMid, yMid, 0);
        context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(data.roll));
        context.getMatrices().translate(-xMid, -yMid, 0);

        drawLadder(textRenderer, context, yHorizon);

        drawPushArrows(textRenderer, context, stall.maximumSafePitch, yHorizon, FAConfig.indicator().warningColor);
        drawReferenceMark(context, PitchController.CLIMB_PITCH, yHorizon, getPitchColor(PitchController.CLIMB_PITCH));
        drawReferenceMark(context, PitchController.GLIDE_PITCH, yHorizon, getPitchColor(PitchController.GLIDE_PITCH));
        drawPullArrows(textRenderer, context, Math.max(PitchController.DESCEND_PITCH, voidLevel.minimumSafePitch), yHorizon, FAConfig.indicator().warningColor);

        pitchData.l1 -= pitchData.margin;
        pitchData.r2 += pitchData.margin;
        drawDegreeBar(textRenderer, context, 0, yHorizon);

        context.getMatrices().pop();
    }

    private Color getPitchColor(float degree) {
        return degree < Math.max(PitchController.DESCEND_PITCH, voidLevel.minimumSafePitch) || degree > stall.maximumSafePitch
                ? FAConfig.indicator().warningColor : FAConfig.indicator().frameColor;
    }

    private void drawLadder(TextRenderer textRenderer, DrawContext context, int yHorizon) {
        for (int i = DEGREES_PER_BAR; i <= 90; i += DEGREES_PER_BAR) {
            int offset = dim.degreesPerPixel * i;
            drawDegreeBar(textRenderer, context, -i, yHorizon + offset);
            drawDegreeBar(textRenderer, context, i, yHorizon - offset);
        }
    }

    private void drawReferenceMark(DrawContext context, float degrees, int yHorizon, Color color) {
        if (degrees == 0) {
            return;
        }

        int y = MathHelper.floor((-degrees * dim.degreesPerPixel) + yHorizon);

        if (outOfFrame(y)) {
            return;
        }

        int width = (pitchData.l2 - pitchData.l1) / 2;
        int l1 = pitchData.l2 - width;
        int r2 = pitchData.r1 + width;

        drawHorizontalLineDashed(context, l1, pitchData.l2, y, 3, color);
        drawHorizontalLineDashed(context, pitchData.r1, r2, y, 3, color);
    }

    private void drawDegreeBar(TextRenderer textRenderer, DrawContext context, float degree, int y) {
        if (outOfFrame(y)) {
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

        drawText(textRenderer, context, asText("%d", Math.round(Math.abs(degree))), pitchData.r2 + 6,
                y - fontVerticalOffset, color);

        drawText(textRenderer, context, asText("%d", Math.round(Math.abs(degree))), pitchData.l1 - 17,
                y - fontVerticalOffset, color);
    }

    private void drawPushArrows(TextRenderer textRenderer, DrawContext context, float degrees, int yHorizon, Color color) {
        Text text = asText("^");
        for (float f = degrees; f <= 90; f += 10) {
            int y = MathHelper.floor((-f * dim.degreesPerPixel) + yHorizon);

            if (outOfFrame(y)) {
                continue;
            }
            context.getMatrices().push();
            context.getMatrices().translate(dim.xMid, y, 0); // Rotate around the middle of the arrow
            context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f)); // Flip upside down
            context.getMatrices().translate(-dim.xMid, -y, 0);

            drawMiddleAlignedText(textRenderer, context, text, dim.xMid, y, color);

            context.getMatrices().pop();
        }
    }

    private void drawPullArrows(TextRenderer textRenderer, DrawContext context, float degrees, int yHorizon, Color color) {
        Text text = asText("^");
        for (float f = degrees; f >= -90; f -= 10) {
            int y = MathHelper.floor((-f * dim.degreesPerPixel) + yHorizon);

            if (outOfFrame(y)) {
                continue;
            }
            drawMiddleAlignedText(textRenderer, context, text, dim.xMid, y, color);
        }
    }

    private boolean outOfFrame(int y) {
        return y < dim.tFrame || y > dim.bFrame - 20;
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        drawMiddleAlignedText(textRenderer, context, Text.translatable("flightassistant.attitude_short"), dim.xMid, dim.yMid - 10, FAConfig.indicator().warningColor);
    }

    @Override
    public String getId() {
        return "attitude";
    }

    private static class AttitudeIndicatorData {
        public int width;
        public int margin;
        public int sideWidth;
        public int l1;
        public int l2;
        public int r1;
        public int r2;

        public void update(Dimensions dim) {
            width = dim.wFrame / 2;
            int left = dim.lFrame + (width / 2);

            margin = width / 3;
            l1 = left + margin;
            l2 = dim.xMid - 7;
            sideWidth = l2 - l1;
            r1 = dim.xMid + 8;
            r2 = r1 + sideWidth;
        }
    }
}
