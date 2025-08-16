package ru.octol1ttle.flightassistant.impl.computer.safety

import kotlin.math.abs
import net.minecraft.client.multiplayer.ClientChunkCache
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.ChunkPos
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerBus

class ChunkStatusComputer(computers: ComputerBus) : Computer(computers) {
    var status: Status = Status.LOADED
        private set

    override fun tick() {
        val chunkPos: ChunkPos = computers.data.player.chunkPosition()
        val world: ClientChunkCache = computers.data.level.chunkSource

        var unloadedClose = 0
        var unloadedFar = false
        for (x: Int in -3..3) {
            for (z: Int in -3..3) {
                if (!world.hasChunk(chunkPos.x + x, chunkPos.z + z)) {
                    if (abs(x) <= 1 && abs(z) <= 1) {
                        unloadedClose++
                    } else {
                        unloadedFar = true
                    }
                }
            }
        }

        status =
            if (unloadedFar || unloadedClose > 0) {
                if (status == Status.ALL_UNLOADED || (unloadedFar && unloadedClose == 9)) Status.ALL_UNLOADED else Status.SOME_UNLOADED
            } else {
                Status.LOADED
            }
    }

    override fun reset() {
        status = Status.LOADED
    }

    enum class Status {
        ALL_UNLOADED,
        SOME_UNLOADED,
        LOADED
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("chunk_status")
    }
}
