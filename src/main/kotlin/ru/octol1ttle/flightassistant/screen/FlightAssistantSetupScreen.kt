package ru.octol1ttle.flightassistant.screen

import dev.isxander.yacl3.platform.YACLPlatform
import kotlinx.serialization.json.Json
import net.minecraft.ChatFormatting
import net.minecraft.Util
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
import ru.octol1ttle.flightassistant.impl.display.HudDisplayHost
import ru.octol1ttle.flightassistant.screen.autoflight.AutoFlightScreen
import ru.octol1ttle.flightassistant.screen.components.SmartStringWidget
import ru.octol1ttle.flightassistant.screen.system.SystemManagementScreen
import ru.octol1ttle.flightassistant.screen.waypoints.WaypointsScreen
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Path

class FlightAssistantSetupScreen : FABaseScreen(null, Component.translatable("menu.flightassistant")) {
    private lateinit var dismissAlertButton: Button
    private lateinit var recallAlertButton: Button

    private var saveLoadError = false

    private val hasShiftDown
//? if >=1.21.9 {
        /*get() = dev.isxander.yacl3.gui.utils.KeyUtils.hasShiftDown()
*///?} else
        get() = net.minecraft.client.gui.screens.Screen.hasShiftDown()

    override fun init() {
        super.init()

        val alertsY = this.centerY - 90
        this.addRenderableWidget(SmartStringWidget(this.centerX, alertsY, Component.translatable("menu.flightassistant.alerts")).middleAligned())
        dismissAlertButton = this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.alerts.dismiss_one")) {
            computers.alert.dismiss(hasShiftDown)
        }.pos(this.centerX - 105, alertsY + 15).width(100).build())
        recallAlertButton = this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.alerts.recall_one")) {
            computers.alert.recall(hasShiftDown)
        }.pos(this.centerX + 5, alertsY + 15).width(100).build())

        val systemY = this.centerY - 40
        this.addRenderableWidget(SmartStringWidget(this.centerX, systemY, Component.translatable("menu.flightassistant.system")).middleAligned())
        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.system.displays")) {
            this.minecraft!!.setScreen(
                SystemManagementScreen(
                    this,
                Component.translatable("menu.flightassistant.system.displays"), "menu.flightassistant.system.name.hud", HudDisplayHost)
            )
        }.pos(this.centerX - 105, systemY + 15).width(100).build())
        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.system.computers")) {
            this.minecraft!!.setScreen(
                SystemManagementScreen(
                    this,
                Component.translatable("menu.flightassistant.system.computers"), "menu.flightassistant.system.name.computer", ComputerHost)
            )
        }.pos(this.centerX + 5, systemY + 15).width(100).build())

        val autoflightY = this.centerY + 10
        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.autoflight")) {
            this.minecraft!!.setScreen(AutoFlightScreen(this))
        }.pos(this.centerX - 80, autoflightY).width(160).build())

        val fmsY = this.centerY + 40
        this.addRenderableWidget(SmartStringWidget(this.centerX, fmsY, Component.translatable("menu.flightassistant.fms")).middleAligned())
        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.fms.waypoints")) {
            this.minecraft!!.setScreen(WaypointsScreen(this))
        }.pos(this.centerX - 40, fmsY + 15).width(80).build())

        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.fms.save")) {
            val patterns = PointerBuffer.create(MemoryUtil.memUTF8(".json"))
            try {
                Files.createDirectories(PLANS_PATH)

                val title = "Save flight plan"
                val description = "Flight plans"

                val path = TinyFileDialogs.tinyfd_saveFileDialog(title, PLANS_PATH.resolve("flight_plan.json").toAbsolutePath().toString(), patterns, description)
                if (path != null) {
                    FileWriter(path).use { it.write(Json.encodeToString(computers.plan.waypoints)) }

                    FlightAssistant.logger.info("Saved flight plan to $path")
                }
                saveLoadError = false
            } catch (e: Throwable) {
                FlightAssistant.logger.error("Unable to save flight plan", e)
                saveLoadError = true
            } finally {
                patterns.free()
            }
        }.pos(this.centerX - 130, fmsY + 15).width(80).build())
        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.fms.load")) {
            val patterns = PointerBuffer.create(MemoryUtil.memUTF8(".json"))
            try {
                Files.createDirectories(PLANS_PATH)

                val title = "Load flight plan"
                val description = "Flight plans"

                val path = TinyFileDialogs.tinyfd_openFileDialog(title, PLANS_PATH.resolve("flight_plan.json").toAbsolutePath().toString(), patterns, description, false)
                if (path != null) {
                    FileReader(path).use { computers.plan.load(Json.decodeFromString(it.readText())) }
                    WaypointsScreen.reload(computers.plan)

                    FlightAssistant.logger.info("Loaded flight plan from $path")
                }
                saveLoadError = false
            } catch (e: Throwable) {
                FlightAssistant.logger.error("Unable to load flight plan", e)
                saveLoadError = true
            } finally {
                patterns.free()
            }
        }.pos(this.centerX + 50, fmsY + 15).width(80).build())

        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.config")) {
            this.minecraft!!.setScreen(FAConfigScreen.generate(this))
        }.pos(10, this.height - 30).width(120).build())

        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.wiki")) {
            Util.getPlatform().openUri("https://github.com/Octol1ttle/FlightAssistant/wiki")
        }.pos(140, this.height - 30).width(80).build())

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE) { _: Button? ->
            this.onClose()
        }.pos(this.width - 90, this.height - 30).width(80).build())
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        if (hasShiftDown) {
            dismissAlertButton.message = Component.translatable("menu.flightassistant.alerts.dismiss_all")
            recallAlertButton.message = Component.translatable("menu.flightassistant.alerts.recall_all")
        } else {
            dismissAlertButton.message = Component.translatable("menu.flightassistant.alerts.dismiss_one")
            recallAlertButton.message = Component.translatable("menu.flightassistant.alerts.recall_one")
        }

        super.render(guiGraphics, mouseX, mouseY, delta)

        if (saveLoadError) {
            guiGraphics.drawMiddleAlignedString(Component.translatable("menu.flightassistant.fms.error"), this.centerX, this.centerY + 75, ChatFormatting.RED.color!!, true)
        }
    }

    companion object {
        val PLANS_PATH: Path = YACLPlatform.getConfigDir().resolve("${FlightAssistant.MOD_ID}/plans")
    }
}
