package ru.octol1ttle.flightassistant.computers;

import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class TimeComputer implements IRenderTickableComputer {
    private static final int HIGHLIGHT_SWITCH_THRESHOLD = 500;
    /**
     * The time between computer ticks *in seconds*
     */
    public float deltaTime;
    public boolean highlight;
    public Float prevMillis;
    private float highlightMillis;

    @Override
    public void tick() {
        float millis = Util.getMeasuringTimeNano() / 1000000.0f;
        if (prevMillis == null) {
            prevMillis = millis - (1000.0f / 60.0f);
        }
        deltaTime = MathHelper.clamp((millis - prevMillis) * 0.001f, 0.001f, 1.0f);
        prevMillis = millis;

        if (millis - highlightMillis > HIGHLIGHT_SWITCH_THRESHOLD) {
            highlight = !highlight;
            highlightMillis = millis;
        }
    }

    @Override
    public String getId() {
        return "time_prvd";
    }

    @Override
    public void reset() {
        deltaTime = 0.0f;
        highlight = false;
        prevMillis = null;
        highlightMillis = 0.0f;
    }
}
