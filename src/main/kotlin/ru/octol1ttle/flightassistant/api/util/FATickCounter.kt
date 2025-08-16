package ru.octol1ttle.flightassistant.api.util

import kotlin.random.Random
import kotlin.random.nextInt
import net.minecraft.Util
import net.minecraft.client.player.LocalPlayer

object FATickCounter {
    val worldLoadWaitTime = Random.Default.nextInt(10..60) // TODO: wait for chunk loading instead
    private var lastPlayerTickCount: Int = 0
    private var lastMillis: Long = 0

    var totalTicks: Int = 0
        private set
    var ticksSinceWorldLoad: Int = 0
        private set
    var ticksPassed: Int = 0
        private set
    var timePassed: Float = 0.0f
        private set
    var partialTick: Float = 0.0f
        private set

    fun tick(player: LocalPlayer, partialTick: Float, paused: Boolean) {
        if (!paused) {
            if (player.tickCount < lastPlayerTickCount) {
                ticksSinceWorldLoad = player.tickCount
            }
            ticksPassed = if (player.tickCount >= lastPlayerTickCount) player.tickCount - lastPlayerTickCount else player.tickCount
            lastPlayerTickCount = player.tickCount
            totalTicks += ticksPassed
            ticksSinceWorldLoad += ticksPassed
            this.partialTick = partialTick
        }

        val millis: Long = Util.getMillis()
        timePassed = (millis - lastMillis) / 1000.0f
        lastMillis = millis
    }
}
