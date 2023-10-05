package net.torocraft.flighthud.computers;

public class AutoFlightComputer {
    private final FlightComputer computer;
    private float targetPitch;

    public AutoFlightComputer(FlightComputer computer) {
        this.computer = computer;
    }

    public void setTargetPitch(float pitch) {
        this.targetPitch = pitch;
    }

    public void disconnectAutopilot(boolean force) {

    }

    public void disconnectAutoThrust(boolean force) {

    }

    public void tick() {

    }
}
