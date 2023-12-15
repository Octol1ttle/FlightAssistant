package ru.octol1ttle.flightassistant.computers.autoflight;

import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.IRenderTickableComputer;
import ru.octol1ttle.flightassistant.computers.TimeComputer;

public class YawController implements IRenderTickableComputer {
    private final TimeComputer time;
    private final AirDataComputer data;

    public Float targetHeading;

    public YawController(TimeComputer time, AirDataComputer data) {
        this.time = time;
        this.data = data;
    }

    @Override
    public void tick() {
        if (!data.canAutomationsActivate()) {
            return;
        }

        smoothSetHeading(targetHeading, time.deltaTime);
    }

    private void smoothSetHeading(Float heading, float delta) {
        if (heading == null) {
            return;
        }

        float difference = heading - data.heading;

        float newYaw;
        if (Math.abs(difference) < 0.05f) {
            newYaw = heading - 180.0f;
        } else {
            newYaw = data.player.getYaw() + difference * delta;
        }

        data.player.setYaw(newYaw);
    }

    @Override
    public String getId() {
        return "yaw_ctl";
    }

    @Override
    public void reset() {
        targetHeading = null;
    }
}
