package ru.octol1ttle.flightassistant.alerts;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.octol1ttle.flightassistant.AlertSoundInstance;

public abstract class BaseAlert {
    @Nullable
    public AlertSoundInstance soundInstance;
    public boolean played = false;
    public boolean hidden = false;

    public abstract boolean isTriggered();

    @NotNull
    public abstract AlertSoundData getSoundData();
}
