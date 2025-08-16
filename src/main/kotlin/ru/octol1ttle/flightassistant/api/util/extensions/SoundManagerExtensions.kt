package ru.octol1ttle.flightassistant.api.util.extensions

import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.client.sounds.SoundManager
import ru.octol1ttle.flightassistant.api.util.SoundExtensions

fun SoundManager.applyVolume(soundInstance: SoundInstance) {
    (this as SoundExtensions).`flightassistant$applyVolume`(soundInstance)
}

fun SoundManager.setLooping(soundInstance: SoundInstance, looping: Boolean) {
    (this as SoundExtensions).`flightassistant$setLooping`(soundInstance, looping)
}

fun SoundManager.pause(soundInstance: SoundInstance) {
    (this as SoundExtensions).`flightassistant$pause`(soundInstance)
}

fun SoundManager.unpause(soundInstance: SoundInstance) {
    (this as SoundExtensions).`flightassistant$unpause`(soundInstance)
}
