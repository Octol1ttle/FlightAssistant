package ru.octol1ttle.flightassistant.impl.alert.autoflight

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.ECAMAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.extensions.drawString
import ru.octol1ttle.flightassistant.api.util.extensions.primaryAdvisoryColor
import ru.octol1ttle.flightassistant.api.util.extensions.warningColor

class AutopilotOffAlert(computers: ComputerBus) : Alert(computers), ECAMAlert {
    override val data: AlertData
        get() = if (computers.autoflight.autopilotAlert) AlertData.FORCE_AUTOPILOT_OFF else AlertData.PLAYER_AUTOPILOT_OFF
    private var age: Int = 0
    private var wasAutopilot: Boolean = false

    override fun shouldActivate(): Boolean {
        if (computers.autoflight.autopilotAlert || age > 80) {
            wasAutopilot = false
            age = 0
            return computers.autoflight.autopilotAlert
        }

        if (computers.autoflight.autopilot) {
            wasAutopilot = true
        }
        val autopilotOff: Boolean = wasAutopilot && !computers.autoflight.autopilot
        if (autopilotOff) {
            age += FATickCounter.ticksPassed
        }

        return autopilotOff
    }

    override fun onHide() {
        computers.autoflight.autopilotAlert = false
    }

    override fun render(guiGraphics: GuiGraphics, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        var i: Int = guiGraphics.drawString(Component.translatable("alert.flightassistant.autoflight.autopilot_off"), firstLineX, firstLineY, warningColor)
        if (computers.autoflight.autopilotAlert) {
            i += guiGraphics.drawString(Component.translatable("alert.flightassistant.autoflight.autopilot_off.push_to_silence"), otherLinesX, firstLineY + 11, primaryAdvisoryColor)
        }

        return i
    }
}
