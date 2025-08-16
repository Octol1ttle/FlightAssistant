package ru.octol1ttle.flightassistant.impl.alert.navigation

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.ECAMAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.extensions.drawString
import ru.octol1ttle.flightassistant.api.util.extensions.warningColor
import ru.octol1ttle.flightassistant.impl.computer.safety.ChunkStatusComputer

class NoChunksLoadedAlert(computers: ComputerBus) : Alert(computers), ECAMAlert {
    override val data: AlertData = AlertData.MASTER_WARNING

    override fun shouldActivate(): Boolean {
        return computers.chunk.status == ChunkStatusComputer.Status.ALL_UNLOADED
    }

    override fun render(guiGraphics: GuiGraphics, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        return guiGraphics.drawString(Component.translatable("alert.flightassistant.navigation.no_chunks_loaded"), firstLineX, firstLineY, warningColor)
    }
}
