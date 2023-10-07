package net.torocraft.flighthud.alerts;

import net.minecraft.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;

public class AlertSoundData {
    public static final AlertSoundData EMPTY = new AlertSoundData(
            null,
            Integer.MAX_VALUE,
            0.0f,
            false
    );
    public final @Nullable SoundEvent sound;
    public final float volume;
    public final boolean repeat;
    private final int priority;

    public AlertSoundData(@Nullable SoundEvent sound, int priority, float volume, boolean repeat) {
        this.sound = sound;
        this.priority = priority;
        this.volume = volume;
        this.repeat = repeat;
    }

    /**
     * @return the alert's priority, where 0 is highest priority. The priority will go down by 0.5 if the sound is repeating
     */
    public double getPriority() {
        return this.priority - (this.repeat ? 0.5 : 0.0);
    }
}
