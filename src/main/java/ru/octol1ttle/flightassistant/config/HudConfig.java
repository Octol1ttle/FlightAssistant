package ru.octol1ttle.flightassistant.config;

import dev.isxander.yacl3.config.v2.api.SerialEntry;
import java.awt.Color;

public class HudConfig {
    // TODO START
    @SerialEntry
    public float width = 0.6f;
    @SerialEntry
    public float height = 0.6f;
    @SerialEntry
    public float thickness = 1.0f;
    // TODO END

    @SerialEntry
    public Color frameColor = Color.GREEN;
    @SerialEntry
    public Color statusTextColor = Color.WHITE;
    @SerialEntry
    public Color advisoryColor = Color.CYAN;
    @SerialEntry
    public Color cautionTextColor = Color.YELLOW;
    @SerialEntry
    public Color highlightedCautionTextColor = Color.BLACK;
    @SerialEntry
    public Color warningTextColor = Color.RED;
    @SerialEntry
    public Color highlightedWarningTextColor = Color.WHITE;

    @SerialEntry
    public boolean elytra_showHealth = true;
    @SerialEntry
    public float elytra_x = 0.5f;
    @SerialEntry
    public float elytra_y = 0.8f;

    @SerialEntry
    public boolean location_showReadout = true;
    @SerialEntry
    public float location_x = 0.2f;
    @SerialEntry
    public float location_y = 0.8f;

    @SerialEntry
    public boolean flightPath_show = true;

    @SerialEntry
    public boolean pitchLadder_showLadder = true;

    @SerialEntry
    public boolean speed_showScale = true;
    @SerialEntry
    public boolean speed_showReadout = true;

    @SerialEntry
    public boolean altitude_showScale = true;
    @SerialEntry
    public boolean altitude_showReadout = true;
    @SerialEntry
    public boolean altitude_showGround = true;

    @SerialEntry
    public boolean heading_showScale = true;
    @SerialEntry
    public boolean heading_showReadout = true;

    @SerialEntry
    public boolean alerts_show = true;

    @SerialEntry
    public boolean flightDirectors_show = true;

    @SerialEntry
    public boolean flightMode_showFirework = true;
    @SerialEntry
    public boolean flightMode_showVertical = true;
    @SerialEntry
    public boolean flightMode_showLateral = true;
    @SerialEntry
    public boolean flightMode_showAutomation = true;

    @SerialEntry
    public boolean status_showFireworkCount = true;

    public HudConfig setMinimal() {
        this.flightPath_show = false;
        this.pitchLadder_showLadder = false;
        this.speed_showScale = false;
        this.speed_showReadout = false;
        this.altitude_showScale = false;
        this.altitude_showGround = false;
        this.heading_showScale = false;
        this.flightMode_showFirework = false;
        this.status_showFireworkCount = false;

        return this;
    }

    public HudConfig disableAll() {
        this.elytra_showHealth = false;
        this.location_showReadout = false;
        this.flightPath_show = false;
        this.pitchLadder_showLadder = false;
        this.speed_showScale = false;
        this.speed_showReadout = false;
        this.altitude_showScale = false;
        this.altitude_showReadout = false;
        this.altitude_showGround = false;
        this.heading_showScale = false;
        this.heading_showReadout = false;
        this.alerts_show = false;
        this.flightDirectors_show = false;
        this.flightMode_showFirework = false;
        this.flightMode_showVertical = false;
        this.flightMode_showLateral = false;
        this.flightMode_showAutomation = false;
        this.status_showFireworkCount = false;

        return this;
    }
}
