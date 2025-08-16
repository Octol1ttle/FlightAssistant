package ru.octol1ttle.flightassistant.impl.alert.flight_controls

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.ECAMAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.extensions.cautionColor
import ru.octol1ttle.flightassistant.api.util.extensions.drawString
import ru.octol1ttle.flightassistant.api.util.extensions.primaryAdvisoryColor

class ProtectionsLostAlert(computers: ComputerBus) : Alert(computers), ECAMAlert {
    override val priorityOffset: Int = 10
    override val data: AlertData = AlertData.MASTER_CAUTION

    override fun shouldActivate(): Boolean {
        return computers.protections.isDisabledOrFaulted()
    }

    override fun render(guiGraphics: GuiGraphics, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        var i = 0
        i += guiGraphics.drawString(Component.translatable("alert.flightassistant.flight_controls.protections_lost"), firstLineX, firstLineY, cautionColor)

        var y: Int = firstLineY + 1
        if (computers.data.isDisabledOrFaulted() && computers.data.faultCount <= 1) {
            y += 10
            i += guiGraphics.drawString(Component.translatable("alert.flightassistant.flight_controls.protections_lost.enable_air_data"), otherLinesX, y, primaryAdvisoryColor)
        }
        if (computers.pitch.isDisabledOrFaulted() && computers.pitch.faultCount <= 1) {
            y += 10
            i += guiGraphics.drawString(Component.translatable("alert.flightassistant.flight_controls.protections_lost.enable_pitch"), otherLinesX, y, primaryAdvisoryColor)
        }
        if (computers.protections.faultCount <= 1) {
            y += 10
            i += guiGraphics.drawString(Component.translatable("alert.flightassistant.flight_controls.protections_lost.enable_prot_sys"), otherLinesX, y, primaryAdvisoryColor)
        }
        y += 10
        i += guiGraphics.drawString(Component.translatable("alert.flightassistant.flight_controls.protections_lost.max_pitch"), otherLinesX, y, primaryAdvisoryColor)
        y += 10
        i += guiGraphics.drawString(Component.translatable("alert.flightassistant.flight_controls.protections_lost.min_pitch"), otherLinesX, y, primaryAdvisoryColor)
        y += 10
        i += guiGraphics.drawString(Component.translatable("alert.flightassistant.flight_controls.protections_lost.maneuver_with_care"), otherLinesX, y, primaryAdvisoryColor)

        return i
    }
}
