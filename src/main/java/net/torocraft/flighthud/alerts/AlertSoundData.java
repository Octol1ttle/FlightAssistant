package net.torocraft.flighthud.alerts;

import net.minecraft.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;

public record AlertSoundData(@Nullable SoundEvent sound, int priority, float volume, boolean repeat) {
    public static final AlertSoundData EMPTY = new AlertSoundData(
            null,
            Integer.MAX_VALUE,
            0.0f,
            false
    );
}
