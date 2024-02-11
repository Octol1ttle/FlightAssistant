package ru.octol1ttle.flightassistant.config;

import dev.isxander.yacl3.config.v2.api.SerialEntry;
import java.awt.Color;

public class HudConfig {
    @SerialEntry
    public Color frameColor = Color.GREEN;
    @SerialEntry
    public Color statusColor = Color.WHITE;
    @SerialEntry
    public Color advisoryColor = Color.CYAN;
    @SerialEntry
    public Color cautionColor = Color.YELLOW;
    @SerialEntry
    public Color warningColor = Color.RED;

    @SerialEntry
    public boolean showElytraHealth = true;
    @SerialEntry
    public boolean showCoordinates = true;
    @SerialEntry
    public boolean showFlightPath = true;

    @SerialEntry
    public boolean showPitchLadder = true;

    @SerialEntry
    public boolean showSpeedScale = true;
    @SerialEntry
    public boolean showSpeedReadout = true;

    @SerialEntry
    public boolean showAltitudeScale = true;
    @SerialEntry
    public boolean showAltitudeReadout = true;
    @SerialEntry
    public boolean showGroundAltitude = true;

    @SerialEntry
    public boolean showHeadingScale = true;
    @SerialEntry
    public boolean showHeadingReadout = true;

    @SerialEntry
    public boolean showAlerts = true;

    @SerialEntry
    public boolean showFlightDirectors = true;

    @SerialEntry
    public boolean showFireworkMode = true;
    @SerialEntry
    public boolean showVerticalMode = true;
    @SerialEntry
    public boolean showLateralMode = true;
    @SerialEntry
    public boolean showAutomationMode = true;

    @SerialEntry
    public boolean showFireworkCount = true;

    public HudConfig setMinimal() {
        this.showFlightPath = false;
        this.showPitchLadder = false;
        this.showSpeedScale = false;
        this.showSpeedReadout = false;
        this.showAltitudeScale = false;
        this.showGroundAltitude = false;
        this.showHeadingScale = false;
        this.showFireworkMode = false;
        this.showFireworkCount = false;

        return this;
    }

    public HudConfig disableAll() {
        this.showElytraHealth = false;
        this.showCoordinates = false;
        this.showFlightPath = false;
        this.showPitchLadder = false;
        this.showSpeedScale = false;
        this.showSpeedReadout = false;
        this.showAltitudeScale = false;
        this.showAltitudeReadout = false;
        this.showGroundAltitude = false;
        this.showHeadingScale = false;
        this.showHeadingReadout = false;
        this.showAlerts = false;
        this.showFlightDirectors = false;
        this.showFireworkMode = false;
        this.showVerticalMode = false;
        this.showLateralMode = false;
        this.showAutomationMode = false;
        this.showFireworkCount = false;

        return this;
    }
}
