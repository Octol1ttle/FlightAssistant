package ru.octol1ttle.flightassistant;

import net.minecraft.client.MinecraftClient;
import ru.octol1ttle.flightassistant.config.FAConfig;

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
        float scale = 1.0f / FAConfig.get().hudScale;

        hScreen = client.getWindow().getScaledHeight() * scale;
        wScreen = client.getWindow().getScaledWidth() * scale;

        degreesPerPixel = hScreen / (float) client.options.getFov().getValue();
        xMid = wScreen * 0.5f;
        yMid = hScreen * 0.5f;

        wFrame = wScreen * FAConfig.get().frameWidth;
        hFrame = hScreen * FAConfig.get().frameHeight;

        lFrame = ((wScreen - wFrame) * 0.5f);
        rFrame = lFrame + wFrame;

        tFrame = ((hScreen - hFrame) * 0.5f);
        bFrame = tFrame + hFrame;
    }
}