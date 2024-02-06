package ru.octol1ttle.flightassistant;

import net.minecraft.client.MinecraftClient;

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
        hScreen = client.getWindow().getScaledHeight() * FAConfig.get().hudScale;
        wScreen = client.getWindow().getScaledWidth() * FAConfig.get().hudScale;

        degreesPerPixel = hScreen / (float) client.options.getFov().getValue();
        xMid = wScreen * 0.5f;
        yMid = hScreen * 0.5f;

        wFrame = wScreen;
        hFrame = hScreen;

        lFrame = ((wScreen - wFrame) * 0.5f);
        rFrame = lFrame + wFrame;

        tFrame = ((hScreen - hFrame) * 0.5f);
        bFrame = tFrame + hFrame;
    }

}