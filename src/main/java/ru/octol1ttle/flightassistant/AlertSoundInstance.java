package ru.octol1ttle.flightassistant;

import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;

public class AlertSoundInstance extends AbstractSoundInstance {
    public AlertSoundInstance(SoundEvent sound) {
        super(sound, SoundCategory.MASTER, Random.create(0L));
        this.volume = 0.5f;
        this.attenuationType = AttenuationType.NONE;
        this.relative = true;
    }

    @Override
    public boolean shouldAlwaysPlay() {
        return true;
    }
}
