package ru.octol1ttle.flightassistant.config

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.dsl.*
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.config.options.DisplayOptions
import ru.octol1ttle.flightassistant.config.options.GlobalOptions
import ru.octol1ttle.flightassistant.config.options.SafetyOptions

object FAConfigScreen {
    @Suppress("unused", "UnusedVariable")
    fun generate(parent: Screen?): Screen {
        return YetAnotherConfigLib(FlightAssistant.MOD_ID) {
            val global: ConfigCategory by registerGlobalOptions(
                Component.translatable("config.flightassistant.category.global"),
                FAConfig.global,
                GlobalOptions()
            )

            with(FAConfig.displaysStorage) {
                val notFlyingNoElytra: ConfigCategory by registerDisplayOptions(
                    Component.translatable("config.flightassistant.category.no_elytra"),
                    notFlyingNoElytra,
                    DisplayOptions().setDisabled()
                )
                val notFlyingHasElytra: ConfigCategory by registerDisplayOptions(
                    Component.translatable("config.flightassistant.category.has_elytra"),
                    notFlyingHasElytra,
                    DisplayOptions().setMinimal()
                )
                val flying: ConfigCategory by registerDisplayOptions(
                    Component.translatable("config.flightassistant.category.flying"),
                    flying,
                    DisplayOptions()
                )
            }

            val safety: ConfigCategory by registerSafetyOptions(
                Component.translatable("config.flightassistant.category.safety"),
                FAConfig.safetyConfig,
                SafetyOptions()
            )

            save { FAConfig.save() }
        }.generateScreen(parent)
    }

    private fun RootDsl.registerGlobalOptions(title: Component, current: GlobalOptions, defaults: GlobalOptions) =
        categories.registering {
            name(title)

            rootOptions.register("mod_enabled") {
                setGlobalName()
                binding(current::modEnabled, defaults.modEnabled)
                controller(tickBox())
            }

            rootOptions.register("hud_enabled") {
                setGlobalName()
                binding(current::hudEnabled, defaults.hudEnabled)
                controller(tickBox())
            }

            rootOptions.register("safety_enabled") {
                setGlobalName()
                binding(current::safetyEnabled, defaults.safetyEnabled)
                controller(tickBox())
            }

            rootOptions.register("automations_allowed_in_overlays") {
                setGlobalName()
                binding(current::automationsAllowedInOverlays, defaults.automationsAllowedInOverlays)
                controller(tickBox())
            }
        }

    private fun RootDsl.registerDisplayOptions(
        title: Component,
        current: DisplayOptions,
        defaults: DisplayOptions
    ): RegisterableActionDelegateProvider<CategoryDsl, ConfigCategory> {
        return categories.registering {
            name(title)

            val percentageFormatter: (Float) -> Component = { value: Float -> Component.literal("${(value * 100).toInt()}%") }
            val degreeFormatter: (Int) -> Component = { value: Int -> Component.literal("$value°") }

            rootOptions.registerLabel("frame", Component.translatable("config.flightassistant.option.display.frame"))
            rootOptions.register("frame.width") {
                setDisplayName()
                binding(current::frameWidth, defaults.frameWidth)
                controller(slider(0.2f..0.8f, 0.05f, percentageFormatter))
            }
            rootOptions.register("frame.height") {
                setDisplayName()
                binding(current::frameHeight, defaults.frameHeight)
                controller(slider(0.2f..0.8f, 0.05f, percentageFormatter))
            }

            rootOptions.registerLabel("colors", Component.translatable("config.flightassistant.option.display.colors"))
            rootOptions.register("colors.primary") {
                setDisplayName()
                binding(current::primaryColor, defaults.primaryColor)
                controller(colorPicker())
            }
            rootOptions.register("colors.secondary") {
                setDisplayName()
                binding(current::secondaryColor, defaults.secondaryColor)
                controller(colorPicker())
            }
            rootOptions.register("colors.advisory.primary") {
                setDisplayName()
                binding(current::primaryAdvisoryColor, defaults.primaryAdvisoryColor)
                controller(colorPicker())
            }
            rootOptions.register("colors.advisory.secondary") {
                setDisplayName()
                binding(current::secondaryAdvisoryColor, defaults.secondaryAdvisoryColor)
                controller(colorPicker())
            }
            rootOptions.register("colors.caution") {
                setDisplayName()
                binding(current::cautionColor, defaults.cautionColor)
                controller(colorPicker())
            }
            rootOptions.register("colors.warning") {
                setDisplayName()
                binding(current::warningColor, defaults.warningColor)
                controller(colorPicker())
            }

            rootOptions.registerLabel("attitude", Component.translatable("config.flightassistant.option.display.attitude"))
            rootOptions.register("attitude.show") {
                setDisplayName()
                binding(current::showAttitude, defaults.showAttitude)
                controller(enumSwitch(DisplayOptions.AttitudeDisplayMode::class.java))
            }
            rootOptions.register("attitude.degree_step") {
                setDisplayName()
                binding(current::attitudeDegreeStep, defaults.attitudeDegreeStep)
                controller(slider(5..45, 5, degreeFormatter))
            }
            rootOptions.register("attitude.pitch_outside_frame") {
                setDisplayName()
                binding(current::drawPitchOutsideFrame, defaults.drawPitchOutsideFrame)
                controller(tickBox())
            }

            rootOptions.registerLabel("heading", Component.translatable("config.flightassistant.option.display.heading"))
            rootOptions.register("heading.show_reading") {
                setDisplayName()
                binding(current::showHeadingReading, defaults.showHeadingReading)
                controller(tickBox())
            }
            rootOptions.register("heading.show_scale") {
                setDisplayName()
                binding(current::showHeadingScale, defaults.showHeadingScale)
                controller(tickBox())
            }

            rootOptions.registerLabel("speed", Component.translatable("config.flightassistant.option.display.speed"))
            rootOptions.register("speed.show_reading") {
                setDisplayName()
                binding(current::showSpeedReading, defaults.showSpeedReading)
                controller(tickBox())
            }
            rootOptions.register("speed.show_scale") {
                setDisplayName()
                binding(current::showSpeedScale, defaults.showSpeedScale)
                controller(tickBox())
            }
            rootOptions.register("speed.show_ground") {
                setDisplayName()
                binding(current::showGroundSpeed, defaults.showGroundSpeed)
                controller(tickBox())
            }
            rootOptions.register("speed.show_vertical") {
                setDisplayName()
                binding(current::showVerticalSpeed, defaults.showVerticalSpeed)
                controller(tickBox())
            }

            rootOptions.registerLabel("altitude", Component.translatable("config.flightassistant.option.display.altitude"))
            rootOptions.register("altitude.show_reading") {
                setDisplayName()
                binding(current::showAltitudeReading, defaults.showAltitudeReading)
                controller(tickBox())
            }
            rootOptions.register("altitude.show_scale") {
                setDisplayName()
                binding(current::showAltitudeScale, defaults.showAltitudeScale)
                controller(tickBox())
            }
            rootOptions.register("altitude.show_radar") {
                setDisplayName()
                binding(current::showRadarAltitude, defaults.showRadarAltitude)
                controller(tickBox())
            }

            rootOptions.registerLabel(
                "flight_path_vector",
                Component.translatable("config.flightassistant.option.display.flight_path_vector")
            )
            rootOptions.register("flight_path_vector.show") {
                setDisplayName()
                binding(current::showFlightPathVector, defaults.showFlightPathVector)
                controller(tickBox())
            }
            rootOptions.register("flight_path_vector.size") {
                setDisplayName()
                binding(current::flightPathVectorSize, defaults.flightPathVectorSize)
                controller(slider(0.5f..2.0f, 0.05f, percentageFormatter))
            }

            rootOptions.registerLabel(
                "elytra_durability",
                Component.translatable("config.flightassistant.option.display.elytra_durability")
            )
            rootOptions.register("elytra_durability.show") {
                setDisplayName()
                binding(current::showElytraDurability, defaults.showElytraDurability)
                controller(tickBox())
            }
            rootOptions.register("elytra_durability.units") {
                setDisplayName()
                binding(current::elytraDurabilityUnits, defaults.elytraDurabilityUnits)
                controller(enumSwitch(DisplayOptions.DurabilityUnits::class.java))
            }

            rootOptions.registerLabel("misc", Component.translatable("config.flightassistant.option.display.misc"))
            rootOptions.register("misc.coordinates") {
                setDisplayName()
                binding(current::showCoordinates, defaults.showCoordinates)
                controller(tickBox())
            }
            rootOptions.register("misc.alerts") {
                setDisplayName()
                binding(current::showAlerts, defaults.showAlerts)
                controller(tickBox())
            }
            rootOptions.register("misc.status_messages") {
                setDisplayName()
                binding(current::showStatusMessages, defaults.showStatusMessages)
                controller(tickBox())
            }
            rootOptions.register("misc.automation_modes") {
                setDisplayName()
                binding(current::showAutomationModes, defaults.showAutomationModes)
                controller(tickBox())
            }
        }
    }

    private fun RootDsl.registerSafetyOptions(
        title: Component,
        current: SafetyOptions,
        defaults: SafetyOptions
    ): RegisterableActionDelegateProvider<CategoryDsl, ConfigCategory> {
        return categories.registering {
            name(title)

            val percentageFormatter: (Float) -> Component = { value: Float -> Component.literal("${(value * 100).toInt()}%") }

            rootOptions.register("alert_volume") {
                setSafetyName()
                binding(current::alertVolume, defaults.alertVolume)
                controller(slider(0.0f..1.0f, 0.01f, percentageFormatter))
            }

            rootOptions.register("consider_invulnerability") {
                setSafetyName()
                binding(current::considerInvulnerability, defaults.considerInvulnerability)
                controller(tickBox())
            }

            rootOptions.registerLabel(
                "elytra",
                Component.translatable("config.flightassistant.option.safety.elytra")
            )
            rootOptions.register("elytra.durability_alert_mode") {
                setSafetyName()
                binding(current::elytraDurabilityAlertMode, defaults.elytraDurabilityAlertMode)
                controller(enumSwitch(SafetyOptions.AlertMode::class.java))
            }
            rootOptions.register("elytra.auto_open") {
                setSafetyName()
                binding(current::elytraAutoOpen, defaults.elytraAutoOpen)
                controller(tickBox())
            }
            rootOptions.register("elytra.close_underwater") {
                setSafetyName()
                binding(current::elytraCloseUnderwater, defaults.elytraCloseUnderwater)
                controller(tickBox())
            }

            rootOptions.registerLabel(
                "stall",
                Component.translatable("config.flightassistant.option.safety.stall")
            )
            rootOptions.register("stall.alert_mode") {
                setSafetyName()
                binding(current::stallAlertMode, defaults.stallAlertMode)
                controller(enumSwitch(SafetyOptions.AlertMode::class.java))
            }
            rootOptions.register("stall.alert_method") {
                setSafetyName()
                binding(current::stallAlertMethod, defaults.stallAlertMethod)
                controller(enumSwitch(SafetyOptions.AlertMethod::class.java))
            }
            rootOptions.register("stall.limit_pitch") {
                setSafetyName()
                binding(current::stallLimitPitch, defaults.stallLimitPitch)
                controller(tickBox())
            }
            rootOptions.register("stall.auto_thrust") {
                setSafetyName()
                binding(current::stallAutoThrust, defaults.stallAutoThrust)
                controller(tickBox())
            }

            rootOptions.registerLabel(
                "void",
                Component.translatable("config.flightassistant.option.safety.void")
            )
            rootOptions.register("void.alert_mode") {
                setSafetyName()
                binding(current::voidAlertMode, defaults.voidAlertMode)
                controller(enumSwitch(SafetyOptions.AlertMode::class.java))
            }
            rootOptions.register("void.limit_pitch") {
                setSafetyName()
                binding(current::voidLimitPitch, defaults.voidLimitPitch)
                controller(tickBox())
            }
            rootOptions.register("void.auto_thrust") {
                setSafetyName()
                binding(current::voidAutoThrust, defaults.voidAutoThrust)
                controller(tickBox())
            }
            rootOptions.register("void.auto_pitch") {
                setSafetyName()
                binding(current::voidAutoPitch, defaults.voidAutoPitch)
                controller(tickBox())
            }

            rootOptions.registerLabel(
                "gpws",
                Component.translatable("config.flightassistant.option.safety.gpws")
            )
            rootOptions.register("gpws.sink_rate.alert_mode") {
                setSafetyName()
                binding(current::sinkRateAlertMode, defaults.sinkRateAlertMode)
                controller(enumSwitch(SafetyOptions.AlertMode::class.java))
            }
            rootOptions.register("gpws.sink_rate.alert_method") {
                setSafetyName()
                binding(current::sinkRateAlertMethod, defaults.sinkRateAlertMethod)
                controller(enumSwitch(SafetyOptions.AlertMethod::class.java))
            }
            rootOptions.register("gpws.sink_rate.limit_pitch") {
                setSafetyName()
                binding(current::sinkRateLimitPitch, defaults.sinkRateLimitPitch)
                controller(tickBox())
            }
            rootOptions.register("gpws.sink_rate.auto_thrust") {
                setSafetyName()
                binding(current::sinkRateAutoThrust, defaults.sinkRateAutoThrust)
                controller(tickBox())
            }
            rootOptions.register("gpws.sink_rate.auto_pitch") {
                setSafetyName()
                binding(current::sinkRateAutoPitch, defaults.sinkRateAutoPitch)
                controller(tickBox())
            }
            rootOptions.register("gpws.obstacle.alert_mode") {
                setSafetyName()
                binding(current::obstacleAlertMode, defaults.obstacleAlertMode)
                controller(enumSwitch(SafetyOptions.AlertMode::class.java))
            }
            rootOptions.register("gpws.obstacle.alert_method") {
                setSafetyName()
                binding(current::obstacleAlertMethod, defaults.obstacleAlertMethod)
                controller(enumSwitch(SafetyOptions.AlertMethod::class.java))
            }
            rootOptions.register("gpws.obstacle.limit_pitch") {
                setSafetyName()
                binding(current::obstacleLimitPitch, defaults.obstacleLimitPitch)
                controller(tickBox())
            }
            rootOptions.register("gpws.obstacle.auto_thrust") {
                setSafetyName()
                binding(current::obstacleAutoThrust, defaults.obstacleAutoThrust)
                controller(tickBox())
            }
            rootOptions.register("gpws.obstacle.auto_pitch") {
                setSafetyName()
                binding(current::obstacleAutoPitch, defaults.obstacleAutoPitch)
                controller(tickBox())
            }

            rootOptions.registerLabel(
                "firework",
                Component.translatable("config.flightassistant.option.safety.firework")
            )
            rootOptions.register("firework.explosive_alert") {
                setSafetyName()
                binding(current::fireworkExplosiveAlert, defaults.fireworkExplosiveAlert)
                controller(tickBox())
            }
            rootOptions.register("firework.lock_explosive") {
                setSafetyName()
                binding(current::fireworkLockExplosive, defaults.fireworkLockExplosive)
                controller(tickBox())
            }
        }
    }

    private fun OptionDsl<*>.setGlobalName() {
        name(Component.translatable("config.flightassistant.option.global.${this.optionId}"))
    }

    private fun OptionDsl<*>.setDisplayName() {
        name(Component.translatable("config.flightassistant.option.display.${this.optionId}"))
    }

    private fun OptionDsl<*>.setSafetyName() {
        name(Component.translatable("config.flightassistant.option.safety.${this.optionId}"))
    }
}
