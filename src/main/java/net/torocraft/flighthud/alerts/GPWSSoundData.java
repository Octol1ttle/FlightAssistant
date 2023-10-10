package net.torocraft.flighthud.alerts;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class GPWSSoundData {
    public static final AlertSoundData PULL_UP = new AlertSoundData(
            SoundEvent.of(new Identifier("flighthud:pull_up")),
            1,
            0.75f,
            false
    );
}
