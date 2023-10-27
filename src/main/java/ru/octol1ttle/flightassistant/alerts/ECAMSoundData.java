package ru.octol1ttle.flightassistant.alerts;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ECAMSoundData {
    public static final AlertSoundData MASTER_WARNING = new AlertSoundData(
            SoundEvent.of(new Identifier("flightassistant:warning")),
            3,
            0.5f,
            true
    );

    public static final AlertSoundData MASTER_CAUTION = new AlertSoundData(
            SoundEvent.of(new Identifier("flightassistant:caution")),
            4,
            0.5f,
            false
    );
}
