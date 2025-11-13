package ru.octol1ttle.flightassistant.screen

import dev.isxander.yacl3.platform.YACLPlatform
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Path
import kotlinx.serialization.json.Json
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import org.lwjgl.PointerBuffer
import org.lwjgl.system.MemoryUtil
import org.lwjgl.util.tinyfd.TinyFileDialogs
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.util.extensions.drawMiddleAlignedString
import ru.octol1ttle.flightassistant.config.FAConfigScreen
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer
import ru.octol1ttle.flightassistant.impl.display.HudDisplayHost
import ru.octol1ttle.flightassistant.screen.autoflight.AutoFlightScreen
import ru.octol1ttle.flightassistant.screen.components.SmartStringWidget
import ru.octol1ttle.flightassistant.screen.fms.arrival.ArrivalScreen
import ru.octol1ttle.flightassistant.screen.fms.departure.DepartureScreen
import ru.octol1ttle.flightassistant.screen.fms.enroute.EnrouteScreen
import ru.octol1ttle.flightassistant.screen.system.SystemManagementScreen

class FlightAssistantSetupScreen : FABaseScreen(null, Component.translatable("menu.flightassistant")) {
    private var saveLoadError = false

    override fun init() {
        super.init()

        this.addRenderableWidget(SmartStringWidget(this.centerX, this.centerY - 80, Component.translatable("menu.flightassistant.system")).middleAligned())
        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.system.manage_displays")) {
            this.minecraft!!.setScreen(
                SystemManagementScreen(
                    this,
                Component.translatable("menu.flightassistant.system.manage_displays"), "menu.flightassistant.system.name.hud", HudDisplayHost)
            )
        }.pos(this.centerX - 105, this.centerY - 65).width(100).build())
        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.system.manage_computers")) {
            this.minecraft!!.setScreen(
                SystemManagementScreen(
                    this,
                Component.translatable("menu.flightassistant.system.manage_computers"), "menu.flightassistant.system.name.computer", ComputerHost)
            )
        }.pos(this.centerX + 5, this.centerY - 65).width(100).build())

        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.autoflight")) {
            this.minecraft!!.setScreen(AutoFlightScreen(this))
        }.pos(this.centerX - 80, this.centerY - 30).width(160).build())

        this.addRenderableWidget(SmartStringWidget(this.centerX, this.centerY + 5, Component.translatable("menu.flightassistant.fms")).middleAligned())
        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.fms.departure")) {
            this.minecraft!!.setScreen(DepartureScreen(this))
        }.pos(this.centerX - 130, this.centerY + 20).width(80).build())
        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.fms.enroute")) {
            this.minecraft!!.setScreen(EnrouteScreen(this))
        }.pos(this.centerX - 40, this.centerY + 20).width(80).build())
        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.fms.arrival")) {
            this.minecraft!!.setScreen(ArrivalScreen(this))
        }.pos(this.centerX + 50, this.centerY + 20).width(80).build())

        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.fms.save")) {
            val patterns = PointerBuffer.create(MemoryUtil.memUTF8(".json"))
            try {
                Files.createDirectories(PLANS_PATH)

                val title = "Save flight plan"
                val description = "Flight plans"

                val path = TinyFileDialogs.tinyfd_saveFileDialog(title, PLANS_PATH.resolve("flight_plan.json").toAbsolutePath().toString(), patterns, description)
                if (path != null) {
                    FileWriter(path).use { it.write(Json.encodeToString(FlightPlanComputer.FlightPlan(computers.plan))) }

                    FlightAssistant.logger.info("Saved flight plan to $path")
                }
                saveLoadError = false
            } catch (e: Throwable) {
                FlightAssistant.logger.error("Unable to save flight plan", e)
                saveLoadError = true
            } finally {
                patterns.free()
            }
        }.pos(this.centerX - 90, this.centerY + 50).width(80).build())
        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.fms.load")) {
            val patterns = PointerBuffer.create(MemoryUtil.memUTF8(".json"))
            try {
                Files.createDirectories(PLANS_PATH)

                val title = "Load flight plan"
                val description = "Flight plans"

                val path = TinyFileDialogs.tinyfd_openFileDialog(title, PLANS_PATH.resolve("flight_plan.json").toAbsolutePath().toString(), patterns, description, false)
                if (path != null) {
                    FileReader(path).use { computers.plan.load(Json.decodeFromString(it.readText())) }
                    DepartureScreen.reload(computers.plan.departureData)
                    EnrouteScreen.reload(computers.plan.enrouteData)
                    ArrivalScreen.reload(computers.plan.arrivalData)

                    FlightAssistant.logger.info("Loaded flight plan from $path")
                }
                saveLoadError = false
            } catch (e: Throwable) {
                FlightAssistant.logger.error("Unable to load flight plan", e)
                saveLoadError = true
            } finally {
                patterns.free()
            }
        }.pos(this.centerX + 10, this.centerY + 50).width(80).build())

        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.config")) {
            this.minecraft!!.setScreen(FAConfigScreen.generate(this))
        }.pos(10, this.height - 30).width(120).build())

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE) { _: Button? ->
            this.onClose()
        }.pos(this.width - 90, this.height - 30).width(80).build())
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(guiGraphics, mouseX, mouseY, delta)

        if (saveLoadError) {
            guiGraphics.drawMiddleAlignedString(Component.translatable("menu.flightassistant.fms.error"), this.centerX, this.centerY + 75, ChatFormatting.RED.color!!, true)
        }
    }

    companion object {
        val PLANS_PATH: Path = YACLPlatform.getConfigDir().resolve("${FlightAssistant.MOD_ID}/plans")
    }
}
