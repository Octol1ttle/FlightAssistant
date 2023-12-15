package ru.octol1ttle.flightassistant.alerts.nav;

import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.alerts.AbstractAlert;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.navigation.FlightPlanner;

public class AltitudeDeviationAlert extends AbstractAlert {
    private final AirDataComputer data;
    private final FlightPlanner plan;

    public AltitudeDeviationAlert(AirDataComputer data, FlightPlanner plan) {
        this.data = data;
        this.plan = plan;
    }

    @Override
    public boolean isTriggered() {
        return data.isFlying && plan.minAltitudeDeviation != null && plan.altitudeDeviation - plan.minAltitudeDeviation > 5.0f;
    }

    @Override
    public @NotNull AlertSoundData getAlertSoundData() {
        return AlertSoundData.ALTITUDE_ALERT;
    }

    @Override
    public boolean canBeHidden() {
        return false;
    }
}
