package ru.octol1ttle.flightassistant

//? if !fabric {

/*//? if neoforge {
/^import net.minecraft.client.Minecraft
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModLoadingContext
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
import net.neoforged.neoforge.client.gui.VanillaGuiLayers
import nl.enjarai.doabarrelroll.compat.flightassistant.DaBRCompatFA
import ru.octol1ttle.flightassistant.api.util.event.FixedGuiRenderCallback
import ru.octol1ttle.flightassistant.config.FAConfigScreen
import thedarkcolour.kotlinforforge.neoforge.KotlinModLoadingContext

typealias CSF = net.neoforged.neoforge.client.gui.IConfigScreenFactory
^///?} else if forge {
/^import net.minecraftforge.client.event.RegisterGuiOverlaysEvent
import net.minecraftforge.client.event.RegisterKeyMappingsEvent
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import nl.enjarai.doabarrelroll.compat.flightassistant.DaBRCompatFA
import ru.octol1ttle.flightassistant.api.util.event.FixedGuiRenderCallback
import ru.octol1ttle.flightassistant.config.FAConfigScreen
import thedarkcolour.kotlinforforge.KotlinModLoadingContext

typealias CSF = net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory
^///?}

@Mod(FlightAssistant.MOD_ID)
object FlightAssistantForge {
    init {
        FlightAssistant.init()
        DaBRCompatFA.init()
        ModLoadingContext.get().registerExtensionPoint(
            CSF::class.java,
        ) { CSF { _, parent -> FAConfigScreen.generate(parent) } }

        val modEventBus: IEventBus = KotlinModLoadingContext.get().getKEventBus()
        modEventBus.addListener(this::onRegisterKeyMappings)
        modEventBus.addListener(this::onRegisterGuiOverlay)
    }

    fun onRegisterKeyMappings(event: RegisterKeyMappingsEvent) {
        FAKeyMappings.keyMappings.forEach(event::register)
    }

//? if neoforge {
    /^fun onRegisterGuiOverlay(event: RegisterGuiLayersEvent) {
        event.registerBelow(VanillaGuiLayers.HOTBAR, FlightAssistant.id("neoforge_gui")) { guiGraphics, deltaTracker ->
            if (!Minecraft.getInstance().options.hideGui) {
                FixedGuiRenderCallback.EVENT.invoker().onRenderGui(guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(true))
            }
        }
    }
^///?} else {
    fun onRegisterGuiOverlay(event: RegisterGuiOverlaysEvent) {
        event.registerBelow(VanillaGuiOverlay.HOTBAR.id(), "flightassistant") { gui, graphics, partialTick, _, _ ->
            if (!gui.minecraft.options.hideGui) {
                gui.setupOverlayRenderState(true, false)
                FixedGuiRenderCallback.EVENT.invoker().onRenderGui(graphics, partialTick)
            }
        }
    }
//?}
}
*///?}
