package ru.octol1ttle.flightassistant.api.util;

import net.minecraft.client.resources.sounds.SoundInstance;

public interface SoundExtensions {
    void flightassistant$applyVolume(SoundInstance soundInstance);
    void flightassistant$setLooping(SoundInstance soundInstance, boolean looping);
    void flightassistant$pause(SoundInstance soundInstance);

    void flightassistant$unpause(SoundInstance soundInstance);
}
