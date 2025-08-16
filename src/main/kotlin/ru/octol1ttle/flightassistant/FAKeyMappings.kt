package ru.octol1ttle.flightassistant

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import org.lwjgl.glfw.GLFW
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.screen.FlightAssistantSetupScreen

object FAKeyMappings {
    internal val keyMappings: MutableList<KeyMapping> = ArrayList()

    private lateinit var toggleEnabled: KeyMapping

    private lateinit var openFlightAssistantSetup: KeyMapping

    private lateinit var autopilotDisconnect: KeyMapping
    private lateinit var manualPitchOverride: KeyMapping

    private lateinit var hideCurrentAlert: KeyMapping
    private lateinit var showHiddenAlert: KeyMapping

    private lateinit var setIdle: KeyMapping
    private lateinit var decreaseThrust: KeyMapping
    private lateinit var increaseThrust: KeyMapping
    private lateinit var setToga: KeyMapping

    fun setup() {
        toggleEnabled = addKeyMapping("toggle_enabled", -1)
        openFlightAssistantSetup = addKeyMapping("open_flightassistant_setup", GLFW.GLFW_KEY_KP_ENTER)

        autopilotDisconnect = addKeyMapping("autopilot_disconnect", GLFW.GLFW_KEY_CAPS_LOCK) // TODO: replace with "Toggle FD", "Toggle A/T", "Toggle AP"
        manualPitchOverride = addKeyMapping("manual_pitch_override", GLFW.GLFW_KEY_RIGHT_ALT)

        hideCurrentAlert = addKeyMapping("hide_current_alert", GLFW.GLFW_KEY_KP_0)
        showHiddenAlert = addKeyMapping("show_hidden_alert", GLFW.GLFW_KEY_KP_DECIMAL)

        setIdle = addKeyMapping("set_idle", GLFW.GLFW_KEY_LEFT, "key.flightassistant.thrust")
        decreaseThrust = addKeyMapping("decrease_thrust", GLFW.GLFW_KEY_DOWN, "key.flightassistant.thrust")
        increaseThrust = addKeyMapping("increase_thrust", GLFW.GLFW_KEY_UP, "key.flightassistant.thrust")
        setToga = addKeyMapping("set_toga", GLFW.GLFW_KEY_RIGHT, "key.flightassistant.thrust")
    }

    private fun addKeyMapping(translationKey: String, code: Int, category: String = "key.flightassistant"): KeyMapping {
        val keyBinding = KeyMapping("${category}.${translationKey}", InputConstants.Type.KEYSYM, code, category)
        keyMappings.add(keyBinding)
        return keyBinding
    }

    fun checkPressed(computers: ComputerBus) {
        while (toggleEnabled.consumeClick()) {
            FAConfig.global.modEnabled = !FAConfig.global.modEnabled
        }

        while (openFlightAssistantSetup.consumeClick()) {
            mc.execute {
                mc.setScreen(FlightAssistantSetupScreen())
            }
        }

        while (autopilotDisconnect.consumeClick()) {
            if (!computers.autoflight.autopilot && !computers.autoflight.autopilotAlert) {
                computers.autoflight.setFlightDirectors(false)
            }
            computers.autoflight.setAutoPilot(false, alert = false)
        }
        computers.pitch.manualOverride = manualPitchOverride.isDown

        while (hideCurrentAlert.consumeClick()) {
            computers.alert.hideCurrentAlert()
        }
        while (showHiddenAlert.consumeClick()) {
            computers.alert.showHiddenAlert()
        }

        while (setIdle.consumeClick()) {
            computers.thrust.setTarget(0.0f)
        }
        while (setToga.consumeClick()) {
            computers.thrust.setTarget(1.0f)
        }
        while (decreaseThrust.consumeClick()) {
            computers.thrust.tickTarget(-1.0f)
        }
        while (increaseThrust.consumeClick()) {
            computers.thrust.tickTarget(1.0f)
        }
    }

    fun isHoldingThrust(): Boolean {
        return setIdle.isDown || setToga.isDown
    }
}
