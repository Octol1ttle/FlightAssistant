package ru.octol1ttle.flightassistant.indicators;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.RotationAxis;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.autoflight.PitchController;
import ru.octol1ttle.flightassistant.computers.safety.StallComputer;
import ru.octol1ttle.flightassistant.computers.safety.VoidLevelComputer;

public class PitchIndicator extends HudComponent {
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
        pitchData.update(dim);

        float horizonOffset = data.pitch * dim.degreesPerPixel;
        float yHorizon = dim.yMid + horizonOffset;

        float a = dim.yMid;
        float b = dim.xMid;

        float roll = data.roll * (CONFIG.pitchLadder_reverseRoll ? -1 : 1);

        if (CONFIG.pitchLadder_showRoll) {
            context.getMatrices().push();
            context.getMatrices().translate(b, a, 0);
            context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(roll));
            context.getMatrices().translate(-b, -a, 0);
        }

        if (CONFIG.pitchLadder_showLadder) {
            drawLadder(textRenderer, context, yHorizon);
        }

        float climbAngle = CONFIG.pitchLadder_optimumClimbAngle;
        float glideAngle = CONFIG.pitchLadder_optimumGlideAngle;

        drawReferenceMark(context, yHorizon, stall.maximumSafePitch, CONFIG.alertColor);
        drawReferenceMark(context, yHorizon, climbAngle, getPitchColor(climbAngle));
        drawReferenceMark(context, yHorizon, glideAngle, getPitchColor(glideAngle));
        drawReferenceMark(context, yHorizon, voidLevel.minimumSafePitch, CONFIG.alertColor);

        if (CONFIG.pitchLadder_showHorizon) {
            pitchData.l1 -= pitchData.margin;
            pitchData.r2 += pitchData.margin;
            drawDegreeBar(textRenderer, context, 0, yHorizon);
        }

        if (CONFIG.pitchLadder_showRoll) {
            context.getMatrices().pop();
        }
    }

    private int getPitchColor(float degree) {
        return degree < Math.max(-PitchController.DESCEND_PITCH, voidLevel.minimumSafePitch) || degree > stall.maximumSafePitch
                ? CONFIG.alertColor : CONFIG.color;
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        drawMiddleAlignedText(textRenderer, context, Text.translatable("flightassistant.pitch_short"), dim.xMid, dim.yMid - 10, CONFIG.alertColor);
    }

    @Override
    public String getId() {
        return "pitch";
    }

    private void drawLadder(TextRenderer textRenderer, DrawContext context, float yHorizon) {
        int degreesPerBar = CONFIG.pitchLadder_degreesPerBar;

        if (degreesPerBar < 1) {
            degreesPerBar = 20;
        }

        for (int i = degreesPerBar; i <= 90; i += degreesPerBar) {
            float offset = dim.degreesPerPixel * i;
            drawDegreeBar(textRenderer, context, -i, yHorizon + offset);
            drawDegreeBar(textRenderer, context, i, yHorizon - offset);
        }
    }

    private void drawReferenceMark(DrawContext context, float yHorizon, float degrees, int color) {
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

        int color = getPitchColor(degree);
        int dashes = degree < 0 ? 4 : 1;

        drawHorizontalLineDashed(context, pitchData.l1, pitchData.l2, y, dashes, color);
        drawHorizontalLineDashed(context, pitchData.r1, pitchData.r2, y, dashes, color);

        int sideTickHeight = degree >= 0 ? 5 : -5;
        drawVerticalLine(context, pitchData.l1, y, y + sideTickHeight, color);
        drawVerticalLine(context, pitchData.r2, y, y + sideTickHeight, color);

        int fontVerticalOffset = degree >= 0 ? 0 : 6;

        drawString(textRenderer, context, String.format("%d", i(Math.abs(degree))), pitchData.r2 + 6,
                y - fontVerticalOffset, color);

        drawString(textRenderer, context, String.format("%d", i(Math.abs(degree))), pitchData.l1 - 17,
                y - fontVerticalOffset, color);
    }

    // TODO: this is absolutely terrible. the amount of imprecision this causes is absurd. (but that can be said about all rendering code here)
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
            width = i(dim.wScreen / 3.0f);
            float left = width;

            mid = i((width * 0.5f) + left);
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
