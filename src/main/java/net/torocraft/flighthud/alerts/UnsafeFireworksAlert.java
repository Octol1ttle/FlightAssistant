package net.torocraft.flighthud.alerts;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Hand;
import net.torocraft.flighthud.FlightSafetyMonitor;
import net.torocraft.flighthud.HudComponent;

import static net.torocraft.flighthud.HudComponent.CONFIG;

public class UnsafeFireworksAlert extends Alert {

    @Override
    public boolean shouldActivate() {
        return !FlightSafetyMonitor.unsafeFireworkHands.isEmpty();
    }

    @Override
    public int drawText(MinecraftClient mc, DrawContext context, float x, float y, boolean highlight) {
        int linesDrawn = 0;
        if (highlight) {
            for (Hand hand : FlightSafetyMonitor.unsafeFireworkHands) {
                String handStr = hand.toString().replace('_', ' ');
                String result = handStr + " FRWKS UNSAFE";
                HudComponent.fill(context, x - 1.5f, y - 1.5f, x + mc.textRenderer.getWidth(result) + CONFIG.halfThickness, y + 9, CONFIG.alertColor);
                HudComponent.drawFont(mc, context, result, x, y, CONFIG.white);
                y += 10;
                linesDrawn++;
            }
            return linesDrawn;
        }

        for (Hand hand : FlightSafetyMonitor.unsafeFireworkHands) {
            String handStr = hand.toString().replace('_', ' ');
            String result = handStr + " FRWKS UNSAFE";
            HudComponent.drawFont(mc, context, result, x, y, CONFIG.alertColor);
            y += 10;
            linesDrawn++;
        }
        return linesDrawn;
    }
}
