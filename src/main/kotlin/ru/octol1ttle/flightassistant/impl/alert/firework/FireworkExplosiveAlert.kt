package ru.octol1ttle.flightassistant.impl.alert.firework

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.ECAMAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.extensions.cautionColor
import ru.octol1ttle.flightassistant.api.util.extensions.drawString
import ru.octol1ttle.flightassistant.config.FAConfig

class FireworkExplosiveAlert(computers: ComputerBus, private val hand: InteractionHand) : Alert(computers), ECAMAlert {
    override val priorityOffset: Int = 5
    override val data: AlertData = AlertData.MASTER_CAUTION

    override fun shouldActivate(): Boolean {
        return FAConfig.safety.fireworkExplosiveAlert && !computers.firework.isEmptyOrSafe(computers.data.player, hand)
    }

    override fun render(guiGraphics: GuiGraphics, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        return guiGraphics.drawString(Component.translatable("alert.flightassistant.firework.explosive.${hand.toString().lowercase()}"), firstLineX, firstLineY, cautionColor)
    }
}
