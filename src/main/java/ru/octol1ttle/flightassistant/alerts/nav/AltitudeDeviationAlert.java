package ru.octol1ttle.flightassistant.alerts.nav;

import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.alerts.AbstractAlert;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.computers.navigation.FlightPlanner;

public class AltitudeDeviationAlert extends AbstractAlert {
    private final FlightPlanner plan;

    public AltitudeDeviationAlert(FlightPlanner plan) {
        this.plan = plan;
    }

    @Override
    public boolean isTriggered() {
        return plan.minAltitudeDeviation != null && plan.altitudeDeviation - plan.minAltitudeDeviation > 5;
    }

    @Override
    public @NotNull AlertSoundData getAlertSoundData() {
        return AlertSoundData.ALTITUDE_ALERT;
    }
}
