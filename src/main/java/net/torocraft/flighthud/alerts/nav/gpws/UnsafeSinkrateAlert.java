package net.torocraft.flighthud.alerts.nav.gpws;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.torocraft.flighthud.alerts.IAlert;
import net.torocraft.flighthud.computers.FlightComputer;

public class UnsafeSinkrateAlert implements IAlert {
    private final FlightComputer computer;

    public UnsafeSinkrateAlert(FlightComputer computer) {
        this.computer = computer;
    }

    @Override
    public boolean isTriggered() {
        return computer.gpws.getImpactTime() >= 0;
    }

    @Override
    public void tick() {
        boolean shouldLevelOff = computer.gpws.getImpactTime() <= 2.5f;
        if (shouldLevelOff) {
            // Looks like it's trying to kill us.
            computer.autoflight.disconnectAutopilot(true);
        }
        computer.pitchController.forceLevelOff = shouldLevelOff;
    }

    @Override
    public int drawText(MinecraftClient mc, DrawContext context, float x, float y, boolean highlight) {
        return 0;
    }
}
