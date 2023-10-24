package ru.octol1ttle.flightassistant;

import net.minecraft.client.MinecraftClient;
import ru.octol1ttle.flightassistant.config.HudConfig;

public class Dimensions {

    public float hScreen;
    public float wScreen;
    public float degreesPerPixel;
    public float xMid;
    public float yMid;

    public float wFrame;
    public float hFrame;
    public float lFrame;
    public float rFrame;
    public float tFrame;
    public float bFrame;

    public void update(MinecraftClient client) {
        if (HudComponent.CONFIG == null) {
            return;
        }
        HudConfig c = HudComponent.CONFIG;
        hScreen = client.getWindow().getScaledHeight();
        wScreen = client.getWindow().getScaledWidth();

        if (c.scale != 1d && c.scale > 0) {
            hScreen = hScreen * c.scale;
            wScreen = wScreen * c.scale;
        }

        degreesPerPixel = hScreen / (float) client.options.getFov().getValue();
        xMid = wScreen * 0.5f;
        yMid = hScreen * 0.5f;

        wFrame = wScreen * c.width;
        hFrame = hScreen * c.height;

        lFrame = ((wScreen - wFrame) * 0.5f) + c.xOffset;
        rFrame = lFrame + wFrame;

        tFrame = ((hScreen - hFrame) * 0.5f) + c.yOffset;
        bFrame = tFrame + hFrame;
    }

}