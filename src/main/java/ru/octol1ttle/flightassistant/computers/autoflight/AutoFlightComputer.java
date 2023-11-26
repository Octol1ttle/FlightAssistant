package ru.octol1ttle.flightassistant.computers.autoflight;

import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.computers.safety.GPWSComputer;

import static ru.octol1ttle.flightassistant.HudComponent.CONFIG;

public class AutoFlightComputer implements ITickableComputer {
    private final AirDataComputer data;
    private final GPWSComputer gpws;
    private final FlightPlanner plan;
    private final FireworkController firework;

    public boolean autoThrustEnabled = false;

    public Integer selectedSpeed;
    public float targetPitch;

    public AutoFlightComputer(AirDataComputer data, GPWSComputer gpws, FlightPlanner plan, FireworkController firework) {
        this.data = data;
        this.gpws = gpws;
        this.plan = plan;
        this.firework = firework;
    }

    public void tick() {
        if (autoThrustEnabled && gpws.getGPWSLampColor() == CONFIG.color) {
            Integer targetSpeed = selectedSpeed != null ? selectedSpeed : plan.getManagedSpeed();
            if (targetSpeed != null && data.speed < targetSpeed) {
                firework.activateFirework(false);
            }
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
