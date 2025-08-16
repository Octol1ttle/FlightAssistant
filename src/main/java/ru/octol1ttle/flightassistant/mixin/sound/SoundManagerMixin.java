package ru.octol1ttle.flightassistant.mixin.sound;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.octol1ttle.flightassistant.api.util.SoundExtensions;

@Mixin(SoundManager.class)
abstract class SoundManagerMixin implements SoundExtensions {
    @Shadow
    @Final
    private SoundEngine soundEngine;

    @Override
    public void flightassistant$applyVolume(SoundInstance soundInstance) {
        ((SoundExtensions) soundEngine).flightassistant$applyVolume(soundInstance);
    }

    @Override
    public void flightassistant$setLooping(SoundInstance soundInstance, boolean looping) {
        ((SoundExtensions) soundEngine).flightassistant$setLooping(soundInstance, looping);
    }

    @Override
    public void flightassistant$pause(SoundInstance soundInstance) {
        ((SoundExtensions) soundEngine).flightassistant$pause(soundInstance);
    }

    @Override
    public void flightassistant$unpause(SoundInstance soundInstance) {
        ((SoundExtensions) soundEngine).flightassistant$unpause(soundInstance);
    }
}
