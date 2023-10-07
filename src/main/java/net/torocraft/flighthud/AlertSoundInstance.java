package net.torocraft.flighthud;

import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;

public class AlertSoundInstance extends EntityTrackingSoundInstance {
    public AlertSoundInstance(SoundEvent sound, float volume, Entity entity, boolean repeat) {
        super(sound, SoundCategory.MASTER, volume, 1, entity, 0);
        this.attenuationType = AttenuationType.NONE;
        this.repeat = repeat;
    }

    public void setVolume(float volume) {
        this.volume = MathHelper.clamp(volume, 0.0f, 1.0f);
    }

    @Override
    public boolean shouldAlwaysPlay() {
        return true;
    }
}
