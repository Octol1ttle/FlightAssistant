package net.torocraft.flighthud.computers;

import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class TimeComputer {
    private static final int HIGHLIGHT_SWITCH_THRESHOLD = 500;
    /**
     * The time between computer ticks *in seconds*
     */
    public float deltaTime;
    public boolean highlight;
    private Long prevMillis;
    private long highlightMillis;

    public void tick() {
        long millis = Util.getMeasuringTimeMs();
        if (prevMillis == null) {
            prevMillis = millis - (1000 / 60);
        }
        deltaTime = MathHelper.clamp((millis - prevMillis) * 0.001f, 0.001f, 1.0f);
        prevMillis = millis;

        if (millis - highlightMillis > HIGHLIGHT_SWITCH_THRESHOLD) {
            highlight = !highlight;
            highlightMillis = millis;
        }
    }
}
