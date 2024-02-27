package ru.octol1ttle.flightassistant.alerts;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record AlertSoundData(@Nullable SoundEvent sound, int priority) {
    public static final AlertSoundData STALL = new AlertSoundData(
            SoundEvent.of(new Identifier("flightassistant:stall")),
            0
    );
    public static final AlertSoundData PULL_UP = new AlertSoundData(
            SoundEvent.of(new Identifier("flightassistant:pull_up")),
            1
    );
    public static final AlertSoundData SINK_RATE = new AlertSoundData(
            SoundEvent.of(new Identifier("flightassistant:sink_rate")),
            2
    );
    public static final AlertSoundData TERRAIN = new AlertSoundData(
            SoundEvent.of(new Identifier("flightassistant:terrain")),
            2
    );
    public static final AlertSoundData AUTOPILOT_DISCONNECT = new AlertSoundData(
            SoundEvent.of(new Identifier("flightassistant:autopilot_disconnect")),
            3
    );
    public static final AlertSoundData MASTER_WARNING = new AlertSoundData(
            SoundEvent.of(new Identifier("flightassistant:warning")),
            4
    );
    public static final AlertSoundData MINIMUMS = new AlertSoundData(
            SoundEvent.of(new Identifier("flightassistant:minimums")),
            5
    );
    public static final AlertSoundData MASTER_CAUTION = new AlertSoundData(
            SoundEvent.of(new Identifier("flightassistant:caution")),
            6
    );
    public static final AlertSoundData EMPTY = new AlertSoundData(
            null,
            Integer.MAX_VALUE
    );
}
