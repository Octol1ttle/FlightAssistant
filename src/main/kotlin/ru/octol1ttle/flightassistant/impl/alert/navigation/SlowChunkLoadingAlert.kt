package ru.octol1ttle.flightassistant.impl.alert.navigation

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.ECAMAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.extensions.cautionColor
import ru.octol1ttle.flightassistant.api.util.extensions.drawString
import ru.octol1ttle.flightassistant.impl.computer.safety.ChunkStatusComputer

class SlowChunkLoadingAlert(computers: ComputerBus) : Alert(computers), ECAMAlert {
    override val priorityOffset: Int = 55
    override val data: AlertData = AlertData.MASTER_CAUTION
    private var alertDuration = 0

    override fun shouldActivate(): Boolean {
        val status: ChunkStatusComputer.Status = computers.chunk.status
        if (status != ChunkStatusComputer.Status.ALL_UNLOADED && alertDuration > 0) {
            alertDuration -= FATickCounter.ticksPassed
            return true
        }

        val shouldActivate: Boolean = status == ChunkStatusComputer.Status.SOME_UNLOADED
        if (shouldActivate) {
            alertDuration = 100
        }
        return shouldActivate
    }

    override fun render(guiGraphics: GuiGraphics, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        return guiGraphics.drawString(Component.translatable("alert.flightassistant.navigation.slow_chunk_loading"), firstLineX, firstLineY, cautionColor)
    }
}
