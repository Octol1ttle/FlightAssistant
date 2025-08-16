package ru.octol1ttle.flightassistant.mixin.sound;

import com.mojang.blaze3d.audio.Channel;
import java.util.Map;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.octol1ttle.flightassistant.api.util.SoundExtensions;

@Mixin(SoundEngine.class)
abstract class SoundEngineMixin implements SoundExtensions {
    @Shadow
    private boolean loaded;
    @Shadow
    @Final
    private Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel;

    @Override
    public void flightassistant$applyVolume(SoundInstance soundInstance) {
        if (this.loaded) {
            ChannelAccess.ChannelHandle channelHandle = this.instanceToChannel.get(soundInstance);
            if (channelHandle != null) {
                channelHandle.execute(channel -> channel.setVolume(soundInstance.getVolume()));
            }
        }
    }

    @Override
    public void flightassistant$setLooping(SoundInstance soundInstance, boolean looping) {
        if (this.loaded) {
            ChannelAccess.ChannelHandle channelHandle = this.instanceToChannel.get(soundInstance);
            if (channelHandle != null) {
                channelHandle.execute(channel -> channel.setLooping(looping));
            }
        }
    }

    @Override
    public void flightassistant$pause(SoundInstance soundInstance) {
        if (this.loaded) {
            ChannelAccess.ChannelHandle channelHandle = this.instanceToChannel.get(soundInstance);
            if (channelHandle != null) {
                channelHandle.execute(Channel::pause);
            }
        }
    }

    @Override
    public void flightassistant$unpause(SoundInstance soundInstance) {
        if (this.loaded) {
            ChannelAccess.ChannelHandle channelHandle = this.instanceToChannel.get(soundInstance);
            if (channelHandle != null) {
                channelHandle.execute(Channel::unpause);
            }
        }
    }
}
