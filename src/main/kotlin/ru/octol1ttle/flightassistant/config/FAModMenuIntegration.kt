package ru.octol1ttle.flightassistant.config

//? if fabric {
import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import ru.octol1ttle.flightassistant.api.util.extensions.*
import net.minecraft.client.gui.screens.Screen

object FAModMenuIntegration : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> = ConfigScreenFactory { parent: Screen -> FAConfigScreen.generate(parent) }
}
//?}
