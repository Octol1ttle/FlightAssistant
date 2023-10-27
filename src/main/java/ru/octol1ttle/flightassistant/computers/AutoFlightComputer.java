package ru.octol1ttle.flightassistant.computers;

import static ru.octol1ttle.flightassistant.HudComponent.CONFIG;

public class AutoFlightComputer {
    private final FlightComputer computer;

    public boolean autoThrustEnabled = false;

    public Integer targetSpeed;
    public float targetPitch;

    public AutoFlightComputer(FlightComputer computer) {
        this.computer = computer;
    }

    public void tick() {
        if (autoThrustEnabled && targetSpeed != null && computer.speed < targetSpeed && computer.gpws.getGPWSLampColor() != CONFIG.alertColor) {
            computer.firework.activateFirework(false);
        }
    }

    public void disconnectAutopilot(boolean force) {

    }

    public void disconnectAutoThrust(boolean force) {

    }

}
