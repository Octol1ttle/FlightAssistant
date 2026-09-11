package ru.octol1ttle.flightassistant

import com.mojang.blaze3d.platform.InputConstants
import kotlin.math.roundToInt
import net.minecraft.client.KeyMapping
import org.lwjgl.glfw.GLFW
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.pointsToDirection
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.impl.computer.autoflight.modes.HoldingPatternLateralMode
import ru.octol1ttle.flightassistant.impl.computer.autoflight.modes.SelectedAltitudeVerticalMode
import ru.octol1ttle.flightassistant.screen.FlightAssistantSetupScreen

object FAKeyMappings {
//? if >=1.21.9 {
    /*private val DEFAULT_CATEGORY = KeyMapping.Category.register(FlightAssistant.id("main"))
    private val THRUST_CATEGORY = KeyMapping.Category.register(FlightAssistant.id("thrust"))
*///?} else {
    private const val DEFAULT_CATEGORY = "key.category.flightassistant.main"
    private const val THRUST_CATEGORY = "key.category.flightassistant.thrust"
//?}

    internal val keyMappings: MutableList<KeyMapping> = ArrayList()

    lateinit var toggleEnabled: KeyMapping

    lateinit var openFlightAssistantSetup: KeyMapping

    lateinit var autopilotDisconnect: KeyMapping
    lateinit var toggleAutoThrust: KeyMapping
    lateinit var toggleAllAutopilot: KeyMapping
    lateinit var toggleHoldingPattern: KeyMapping
    lateinit var globalAutomationOverride: KeyMapping

    lateinit var hideCurrentAlert: KeyMapping
    lateinit var showHiddenAlert: KeyMapping

    lateinit var setIdle: KeyMapping
    lateinit var decreaseThrust: KeyMapping
    lateinit var increaseThrust: KeyMapping
    lateinit var setToga: KeyMapping

    fun setup() {
        toggleEnabled = addKeyMapping("toggle_enabled", -1)
        openFlightAssistantSetup = addKeyMapping("open_flightassistant_setup", GLFW.GLFW_KEY_KP_ENTER)

        autopilotDisconnect = addKeyMapping("autopilot_disconnect", GLFW.GLFW_KEY_CAPS_LOCK)
        toggleAutoThrust = addKeyMapping("toggle_auto_thrust", -1)
        toggleAllAutopilot = addKeyMapping("toggle_all_autopilot", -1)
        toggleHoldingPattern = addKeyMapping("toggle_holding_pattern", -1)
        globalAutomationOverride = addKeyMapping("global_automation_override", GLFW.GLFW_KEY_LEFT_ALT)

        hideCurrentAlert = addKeyMapping("hide_current_alert", GLFW.GLFW_KEY_KP_0)
        showHiddenAlert = addKeyMapping("show_hidden_alert", GLFW.GLFW_KEY_KP_DECIMAL)

        setIdle = addKeyMapping("set_idle", GLFW.GLFW_KEY_LEFT, THRUST_CATEGORY)
        decreaseThrust = addKeyMapping("decrease_thrust", GLFW.GLFW_KEY_DOWN, THRUST_CATEGORY)
        increaseThrust = addKeyMapping("increase_thrust", GLFW.GLFW_KEY_UP, THRUST_CATEGORY)
        setToga = addKeyMapping("set_toga", GLFW.GLFW_KEY_RIGHT, THRUST_CATEGORY)
    }

//? if >=1.21.9 {
    /*private fun addKeyMapping(translationKey: String, code: Int, category: KeyMapping.Category = DEFAULT_CATEGORY): KeyMapping {
        val keyBinding = KeyMapping("key.category.flightassistant.${category.id.path}.${translationKey}", InputConstants.Type.KEYSYM, code, category)
*///?} else {
    private fun addKeyMapping(translationKey: String, code: Int, category: String = DEFAULT_CATEGORY): KeyMapping {
        val keyBinding = KeyMapping("${category}.${translationKey}", InputConstants.Type.KEYSYM, code, category)
//?}
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
        while (toggleAutoThrust.consumeClick()) {
            computers.autoflight.setAutoThrust(!computers.autoflight.autoThrust, alert = false)
        }
        while (toggleAllAutopilot.consumeClick()) {
            val enable: Boolean = !computers.autoflight.autopilot
            computers.autoflight.setFlightDirectors(enable)
            computers.autoflight.setAutoThrust(enable, alert = false)
            computers.autoflight.setAutoPilot(enable, alert = false)
        }
        while (toggleHoldingPattern.consumeClick()) {
            if (computers.autoflight.selectedLateralMode is HoldingPatternLateralMode) {
                computers.autoflight.selectedLateralMode = null
                computers.autoflight.selectedVerticalMode = null
            } else {
                // Hold at the waypoint currently being flown to, if there is one; otherwise hold at present position.
                val target = computers.plan.getEnrouteTarget()
                val fixX: Int = target?.coordinatesX ?: computers.data.x.roundToInt()
                val fixZ: Int = target?.coordinatesZ ?: computers.data.z.roundToInt()
                val fixAltitude: Int = target?.altitude ?: computers.data.altitude.roundToInt()
                val inboundCourse: Float = if (target != null) {
                    pointsToDirection(fixX.toDouble(), fixZ.toDouble(), computers.data.x, computers.data.z).toFloat() + 180.0f
                } else {
                    computers.data.heading
                }

                computers.autoflight.selectedLateralMode = HoldingPatternLateralMode(fixX, fixZ, inboundCourse)
                computers.autoflight.selectedVerticalMode = SelectedAltitudeVerticalMode(fixAltitude)
            }
        }

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
