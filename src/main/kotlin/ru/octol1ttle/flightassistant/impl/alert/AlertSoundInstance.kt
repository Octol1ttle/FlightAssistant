package ru.octol1ttle.flightassistant.impl.alert

import kotlin.math.max
import kotlin.math.min
import net.minecraft.client.resources.sounds.AbstractSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.client.sounds.SoundManager
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.player.Player
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.util.extensions.setLooping
import ru.octol1ttle.flightassistant.config.FAConfig

class AlertSoundInstance(val player: Player, val data: AlertData) :
    AbstractSoundInstance(data.soundEvent, SoundSource.MASTER, SoundInstance.createUnseededRandom()) {
    var age: Int = 0
    private var actualVolume: Float = 1.0f
    private var fadingOut: Boolean = false

    init {
        this.relative = true
        this.attenuation = SoundInstance.Attenuation.NONE
        this.looping = data.looping != AlertData.LoopType.NONE
        if (data.looping == AlertData.LoopType.FADE_IN_OUT) {
            this.actualVolume = 0.05f
        }
        this.volume = this.actualVolume * FAConfig.safety.alertVolume
    }

    override fun canStartSilent(): Boolean {
        return true
    }

    fun tick() {
        age++
        if (this.fadingOut) {
            return
        }

        if (age > 100) {
            if (data.looping <= AlertData.LoopType.FADE_OUT) {
                this.actualVolume = max(0.3f, this.actualVolume - 0.007f)
            }
        } else if (data.looping == AlertData.LoopType.FADE_IN_OUT) {
            this.actualVolume = min(1.0f, this.actualVolume + 0.05f)
        }

        this.volume = this.actualVolume * FAConfig.safety.alertVolume
    }

    fun setLooping(looping: Boolean, soundManager: SoundManager) {
        this.looping = looping
        soundManager.setLooping(this, this.looping)
    }

    fun fadeOut(ticksPassed: Int): Boolean {
        this.fadingOut = true
        this.volume = max(0.0f, this.volume - 0.1f * ticksPassed)
        return this.volume <= 0.0f
    }

    fun silence() {
        this.volume = 0.0f
    }
}
