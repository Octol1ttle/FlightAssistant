package ru.octol1ttle.flightassistant.computers.autoflight;

import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.safety.GPWSComputer;

import static ru.octol1ttle.flightassistant.HudComponent.CONFIG;

public class AutoFlightComputer implements ITickableComputer {
    private final AirDataComputer data;
    private final GPWSComputer gpws;
    private final FireworkController firework;

    public boolean autoThrustEnabled = false;

    public Integer targetSpeed;
    public float targetPitch;

    public AutoFlightComputer(AirDataComputer data, GPWSComputer gpws, FireworkController firework) {
        this.data = data;
        this.gpws = gpws;
        this.firework = firework;
    }

    public void tick() {
        if (autoThrustEnabled && targetSpeed != null && data.speed < targetSpeed && gpws.getGPWSLampColor() == CONFIG.color) {
            firework.activateFirework(false);
        }
    }

    public void disconnectAutopilot(boolean force) {

    }

    public void disconnectAutoThrust(boolean force) {

    }

    @Override
    public String getId() {
        return "auto_flt";
    }

    @Override
    public void reset() {
        autoThrustEnabled = false;
    }
}
