package ru.octol1ttle.flightassistant.computers;

public class AutoFlightComputer {
    private final FlightComputer computer;

    public boolean autoThrustEnabled = false;

    public Integer targetSpeed;
    public float targetPitch;

    public AutoFlightComputer(FlightComputer computer) {
        this.computer = computer;
    }

    public void tick() {
        if (targetSpeed != null && computer.speed < targetSpeed) {
            tryActivateFireworks();
        }
    }

    private void tryActivateFireworks() {

    }

    public void toggleAutoThrust() {
        autoThrustEnabled = !autoThrustEnabled;
    }

    public void disconnectAutopilot(boolean force) {

    }

    public void disconnectAutoThrust(boolean force) {

    }

}
