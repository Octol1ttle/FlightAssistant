package net.torocraft.flighthud.alerts;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ECAMSoundData {
    public static final AlertSoundData MASTER_WARNING = new AlertSoundData(
            SoundEvent.of(new Identifier("flighthud:warning")),
            3,
            0.75f,
            true
    );

    public static final AlertSoundData MASTER_CAUTION = new AlertSoundData(
            SoundEvent.of(new Identifier("flighthud:caution")),
            4,
            0.75f,
            false
    );
}
