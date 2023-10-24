package ru.octol1ttle.flightassistant;

import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public class AlertSoundInstance extends EntityTrackingSoundInstance {
    public AlertSoundInstance(SoundEvent sound, float volume, Entity entity, boolean repeat) {
        super(sound, SoundCategory.MASTER, volume, 1, entity, 0);
        this.attenuationType = AttenuationType.NONE;
        this.repeat = repeat;
    }

    @Override
    public boolean shouldAlwaysPlay() {
        return true;
    }
}
