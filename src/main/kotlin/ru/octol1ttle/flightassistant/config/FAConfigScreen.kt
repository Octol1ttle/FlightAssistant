package ru.octol1ttle.flightassistant.config

import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.dsl.*
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.config.options.AlertOptions
import ru.octol1ttle.flightassistant.config.options.GlobalOptions
import ru.octol1ttle.flightassistant.config.options.HudOptions
import ru.octol1ttle.flightassistant.config.options.ProtectionOptions

object FAConfigScreen {
    fun generate(parent: Screen?): Screen {
        return YetAnotherConfigLib(FlightAssistant.MOD_ID) {
            registerGeneralOptions(
                Component.translatable("config.flightassistant.general"),
                FAConfig.global,
                GlobalOptions()
            )

            registerHudOptions(
                Component.translatable("config.flightassistant.hud"),
                FAConfig.hud,
                HudOptions()
            )

            registerAlertOptions(
                Component.translatable("config.flightassistant.alerts"),
                FAConfig.alerts,
                AlertOptions()
            )

            registerProtectionOptions(
                Component.translatable("config.flightassistant.protections"),
                FAConfig.protectionsConfig,
                ProtectionOptions()
            )

            save { FAConfig.save() }
        }.generateScreen(parent)
    }

    private fun RootDsl.registerGeneralOptions(title: Component, current: GlobalOptions, defaults: GlobalOptions) =
        categories.register("general") {
            name(title)

            rootOptions.register("mod_enabled") {
                makeGeneralOption()
                binding(current::modEnabled, defaults.modEnabled)
                controller(tickBox())
            }

            rootOptions.register("show_hud") {
                makeGeneralOption()
                binding(current::showHud, defaults.showHud)
                controller(tickBox())
            }

            rootOptions.register("alerts") {
                makeGeneralOption()
                binding(current::alerts, defaults.alerts)
                controller(enumSwitch())
            }

            rootOptions.register("allow_automations") {
                makeGeneralOption()
                binding(current::allowAutomations, defaults.allowAutomations)
                controller(tickBox())
            }

            rootOptions.register("allow_automations_in_gui") {
                makeGeneralOption()
                binding(current::allowAutomationsInGUI, defaults.allowAutomationsInGUI)
                controller(tickBox())
                available(current::allowAutomations)
            }

            rootOptions.register("flight_protections") {
                makeGeneralOption()
                binding(current::flightProtections, defaults.flightProtections)
                controller(tickBox())
                available(current::allowAutomations)
            }

            rootOptions.register("autopilot_resistance_threshold") {
                makeGeneralOption()
                binding(current::autopilotResistanceThreshold, defaults.autopilotResistanceThreshold)
                controller(slider(0.1f..2.0f, 0.05f))
                available(current::allowAutomations)
            }
        }

    private fun RootDsl.registerHudOptions(title: Component, current: HudOptions, defaults: HudOptions) =
        categories.register("hud") {
            name(title)

            groups.register("frame") {
                makeHudGroup()

                val percentageFormatter: (Float) -> Component =
                    { value: Float -> Component.literal("${(value * 100).toInt()}%") }

                options.register("frame.width") {
                    makeHudOption()
                    binding(current::frameWidth, defaults.frameWidth)
                    controller(slider(0.2f..0.8f, 0.05f, percentageFormatter))
                }
                options.register("frame.height") {
                    makeHudOption()
                    binding(current::frameHeight, defaults.frameHeight)
                    controller(slider(0.2f..0.8f, 0.05f, percentageFormatter))
                }
                options.register("frame.allow_drawing_outside_frame") {
                    makeHudOption()
                    binding(current::allowDrawingOutsideFrame, defaults.allowDrawingOutsideFrame)
                    controller(tickBox())
                }
            }

            groups.register("colors") {
                makeHudGroup()

                options.register("colors.main") {
                    makeHudOption()
                    binding(current::mainColor, defaults.mainColor)
                    controller(colorPicker())
                }
                options.register("colors.secondary") {
                    makeHudOption()
                    binding(current::secondaryColor, defaults.secondaryColor)
                    controller(colorPicker())
                }
                options.register("colors.advisory.primary") {
                    makeHudOption()
                    binding(current::primaryAdvisoryColor, defaults.primaryAdvisoryColor)
                    controller(colorPicker())
                }
                options.register("colors.advisory.secondary") {
                    makeHudOption()
                    binding(current::secondaryAdvisoryColor, defaults.secondaryAdvisoryColor)
                    controller(colorPicker())
                }
                options.register("colors.warning") {
                    makeHudOption()
                    binding(current::warningColor, defaults.warningColor)
                    controller(colorPicker())
                }
                options.register("colors.danger") {
                    makeHudOption()
                    binding(current::dangerColor, defaults.dangerColor)
                    controller(colorPicker())
                }
            }

            groups.register("attitude") {
                makeHudGroup()

                options.register("attitude.show") {
                    makeHudOption()
                    binding(current::showAttitude, defaults.showAttitude)
                    controller(enumSwitch())
                }
                options.register("attitude.elements") {
                    makeHudOption()
                    binding(current::attitudeElements, defaults.attitudeElements)
                    controller(enumSwitch())
                }
                options.register("attitude.ladder_degree_interval") {
                    makeHudOption()
                    val degreeFormatter: (Int) -> Component = { value: Int -> Component.literal("$value°") }
                    binding(current::attitudeLadderDegreeInterval, defaults.attitudeLadderDegreeInterval)
                    controller(slider(5..45, 5, degreeFormatter))
                    available(FAConfig.global.showHud && current.attitudeElements == HudOptions.AttitudeElements.HORIZON_AND_LADDER)
                }
            }

            groups.register("speed") {
                makeHudGroup()

                options.register("speed.show_reading") {
                    makeHudOption()
                    binding(current::showSpeedReading, defaults.showSpeedReading)
                    controller(enumSwitch())
                }
                options.register("speed.show_scale") {
                    makeHudOption()
                    binding(current::showSpeedScale, defaults.showSpeedScale)
                    controller(enumSwitch())
                }
                options.register("speed.show_ground_speed") {
                    makeHudOption()
                    binding(current::showGroundSpeed, defaults.showGroundSpeed)
                    controller(enumSwitch())
                }
                options.register("speed.show_vertical_speed") {
                    makeHudOption()
                    binding(current::showVerticalSpeed, defaults.showVerticalSpeed)
                    controller(enumSwitch())
                }
            }

            groups.register("altitude") {
                makeHudGroup()

                options.register("altitude.show_reading") {
                    makeHudOption()
                    binding(current::showAltitudeReading, defaults.showAltitudeReading)
                    controller(enumSwitch())
                }
                options.register("altitude.show_scale") {
                    makeHudOption()
                    binding(current::showAltitudeScale, defaults.showAltitudeScale)
                    controller(enumSwitch())
                }
                options.register("altitude.show_height_above_ground") {
                    makeHudOption()
                    binding(current::showHeightAboveGround, defaults.showHeightAboveGround)
                    controller(enumSwitch())
                }
            }

            groups.register("heading") {
                makeHudGroup()

                options.register("heading.show_reading") {
                    makeHudOption()
                    binding(current::showHeadingReading, defaults.showHeadingReading)
                    controller(enumSwitch())
                }
                options.register("heading.show_scale") {
                    makeHudOption()
                    binding(current::showHeadingScale, defaults.showHeadingScale)
                    controller(enumSwitch())
                }
            }

            groups.register("elytra_durability") {
                makeHudGroup()

                options.register("elytra_durability.show") {
                    makeHudOption()
                    binding(current::showElytraDurability, defaults.showElytraDurability)
                    controller(enumSwitch())
                }
                options.register("elytra_durability.units") {
                    makeHudOption()
                    binding(current::elytraDurabilityUnits, defaults.elytraDurabilityUnits)
                    controller(enumSwitch())
                }
            }

            groups.register("automation") {
                makeHudGroup()

                options.register("automation.show_automation_modes") {
                    makeHudOption()
                    binding(current::showAutomationModes, defaults.showAutomationModes)
                    controller(enumSwitch())
                }
                options.register("automation.show_flight_directors") {
                    makeHudOption()
                    binding(current::showFlightDirectors, defaults.showFlightDirectors)
                    controller(enumSwitch())
                }
                options.register("automation.show_course_deviation") {
                    makeHudOption()
                    binding(current::showCourseDeviation, defaults.showCourseDeviation)
                    controller(enumSwitch())
                }
            }

            groups.register("misc") {
                makeHudGroup()

                options.register("misc.show_flight_path_vector") {
                    makeHudOption()
                    binding(current::showFlightPathVector, defaults.showFlightPathVector)
                    controller(tickBox())
                }
                options.register("misc.show_coordinates") {
                    makeHudOption()
                    binding(current::showCoordinates, defaults.showCoordinates)
                    controller(enumSwitch())
                }
                options.register("misc.show_alerts") {
                    makeHudOption()
                    binding(current::showAlerts, defaults.showAlerts)
                    controller(enumSwitch())
                    available(FAConfig.global.showHud && FAConfig.global.alerts.hud())
                }
                options.register("misc.show_status_messages") {
                    makeHudOption()
                    binding(current::showStatusMessages, defaults.showStatusMessages)
                    controller(enumSwitch())
                }
            }
        }

    private fun RootDsl.registerAlertOptions(title: Component, current: AlertOptions, defaults: AlertOptions) =
        categories.register("alerts") {
            name(title)

            groups.register("sound") {
                makeAlertGroup()

                options.register("sound.volume") {
                    makeAlertOption()
                    binding(current::soundVolume, defaults.soundVolume)
                    val percentageFormatter: (Float) -> Component =
                        { value: Float -> Component.literal("${(value * 100).toInt()}%") }
                    controller(slider(0.0f..1.0f, 0.01f, percentageFormatter))
                    available(FAConfig.global.alerts.sound())
                }
                options.register("sound.allow_looping") {
                    makeAlertOption()
                    binding(current::allowSoundLooping, defaults.allowSoundLooping)
                    controller(tickBox())
                    available(FAConfig.global.alerts.sound())
                }
            }

            groups.register("elytra_durability") {
                makeAlertGroup()

                options.register("elytra_durability.severity") {
                    makeAlertOption()
                    binding(current::elytraDurabilityAlertSeverity, defaults.elytraDurabilityAlertSeverity)
                    controller(enumSwitch())
                }
            }

            groups.register("stall") {
                makeAlertGroup()

                options.register("stall.severity") {
                    makeAlertOption()
                    binding(current::stallAlertSeverity, defaults.stallAlertSeverity)
                    controller(enumSwitch())
                }
            }

            groups.register("void_damage") {
                makeAlertGroup()

                options.register("void_damage.severity") {
                    makeAlertOption()
                    binding(current::voidDamageAlertSeverity, defaults.voidDamageAlertSeverity)
                    controller(enumSwitch())
                }
            }

            groups.register("ground_proximity") {
                makeAlertGroup()

                options.register("ground_proximity.sink_rate") {
                    makeAlertOption()
                    binding(current::sinkRateAlertSeverity, defaults.sinkRateAlertSeverity)
                    controller(enumSwitch())
                }
                options.register("ground_proximity.obstacle") {
                    makeAlertOption()
                    binding(current::obstacleAlertSeverity, defaults.obstacleAlertSeverity)
                    controller(enumSwitch())
                }
                options.register("ground_proximity.below_glide_slope") {
                    makeAlertOption()
                    binding(current::belowGlideSlopeAlertSeverity, defaults.belowGlideSlopeAlertSeverity)
                    controller(enumSwitch())
                }
                options.register("ground_proximity.altitude_loss") {
                    makeAlertOption()
                    binding(current::altitudeLoss, defaults.altitudeLoss)
                    controller(tickBox())
                }
            }

            groups.register("fireworks") {
                makeAlertGroup()

                options.register("fireworks.explosive") {
                    makeAlertOption()
                    binding(current::fireworkExplosive, defaults.fireworkExplosive)
                    controller(tickBox())
                }
                options.register("fireworks.response") {
                    makeAlertOption()
                    binding(current::fireworkResponseAlertSeverity, defaults.fireworkResponseAlertSeverity)
                    controller(enumSwitch())
                }
            }

            groups.register("chunk_loading") {
                makeAlertGroup()

                options.register("chunk_loading.severity") {
                    makeAlertOption()
                    binding(current::chunkLoadingAlertSeverity, defaults.chunkLoadingAlertSeverity)
                    controller(enumSwitch())
                }
            }
        }

    private fun RootDsl.registerProtectionOptions(
        title: Component,
        current: ProtectionOptions,
        defaults: ProtectionOptions
    ) =
        categories.register("protections") {
            name(title)

            groups.register("general") {
                makeProtectionGroup()

                options.register("general.consider_invulnerability") {
                    makeProtectionOption()
                    binding(current::considerInvulnerability, defaults.considerInvulnerability)
                    controller(tickBox())
                }
            }

            groups.register("elytra") {
                makeProtectionGroup()

                options.register("elytra.auto_open") {
                    makeProtectionOption()
                    binding(current::elytraAutoOpen, defaults.elytraAutoOpen)
                    controller(tickBox())
                }
            }

            groups.register("stall") {
                makeProtectionGroup()

                options.register("stall.limit_pitch") {
                    makeProtectionOption()
                    binding(current::stallLimitPitch, defaults.stallLimitPitch)
                    controller(tickBox())
                }
                options.register("stall.auto_thrust") {
                    makeProtectionOption()
                    binding(current::stallAutoThrust, defaults.stallAutoThrust)
                    controller(tickBox())
                }
            }

            groups.register("void_damage") {
                makeProtectionGroup()

                options.register("void_damage.limit_pitch") {
                    makeProtectionOption()
                    binding(current::voidLimitPitch, defaults.voidLimitPitch)
                    controller(tickBox())
                }
                options.register("void_damage.auto_recover") {
                    makeProtectionOption()
                    binding(current::voidAutoRecover, defaults.voidAutoRecover)
                    controller(tickBox())
                }
            }

            groups.register("ground_proximity") {
                makeProtectionGroup()

                options.registerLabel(
                    "ground_proximity.sink_rate",
                    Component.translatable("config.flightassistant.protections.ground_proximity.sink_rate")
                )
                options.register("ground_proximity.sink_rate.limit_pitch") {
                    makeProtectionOption()
                    binding(current::sinkRateLimitPitch, defaults.sinkRateLimitPitch)
                    controller(tickBox())
                }
                options.register("ground_proximity.sink_rate.limit_thrust") {
                    makeProtectionOption()
                    binding(current::sinkRateLimitThrust, defaults.sinkRateLimitThrust)
                    controller(tickBox())
                }
                options.register("ground_proximity.sink_rate.auto_pitch") {
                    makeProtectionOption()
                    binding(current::sinkRateAutoPitch, defaults.sinkRateAutoPitch)
                    controller(tickBox())
                }

                options.registerLabel(
                    "ground_proximity.obstacle",
                    Component.translatable("config.flightassistant.protections.ground_proximity.obstacle")
                )
                options.register("ground_proximity.obstacle.limit_thrust") {
                    makeProtectionOption()
                    binding(current::obstacleLimitThrust, defaults.obstacleLimitThrust)
                    controller(tickBox())
                }
                options.register("ground_proximity.obstacle.auto_pitch") {
                    makeProtectionOption()
                    binding(current::obstacleAutoPitch, defaults.obstacleAutoPitch)
                    controller(tickBox())
                }
            }

            groups.register("fireworks") {
                makeProtectionGroup()

                options.register("fireworks.lock_explosive") {
                    makeProtectionOption()
                    binding(current::fireworkLockExplosive, defaults.fireworkLockExplosive)
                    controller(tickBox())
                }
                options.register("fireworks.lock_obstacle") {
                    makeProtectionOption()
                    binding(current::fireworkLockObstacle, defaults.fireworkLockObstacle)
                    controller(tickBox())
                }
            }
        }

    private fun OptionDsl<*>.makeGeneralOption() {
        name(Component.translatable("config.flightassistant.general.${this.optionId}"))
        description(OptionDescription.of(Component.translatable("config.flightassistant.general.${this.optionId}.description")))
    }

    private fun GroupDsl.makeHudGroup() {
        name(Component.translatable("config.flightassistant.hud.${this.groupId}"))
    }

    private fun OptionDsl<*>.makeHudOption() {
        name(Component.translatable("config.flightassistant.hud.${this.optionId}"))
        description(OptionDescription.of(Component.translatable("config.flightassistant.hud.${this.optionId}.description")))
        available(FAConfig.global.showHud)
    }

    private fun GroupDsl.makeAlertGroup() {
        name(Component.translatable("config.flightassistant.alerts.${this.groupId}"))
    }

    private fun OptionDsl<*>.makeAlertOption() {
        name(Component.translatable("config.flightassistant.alerts.${this.optionId}"))
        description(OptionDescription.of(Component.translatable("config.flightassistant.alerts.${this.optionId}.description")))
        available(FAConfig.global.alerts != GlobalOptions.AlertMode.DISABLED)
    }

    private fun GroupDsl.makeProtectionGroup() {
        name(Component.translatable("config.flightassistant.protections.${this.groupId}"))
    }

    private fun OptionDsl<*>.makeProtectionOption() {
        name(Component.translatable("config.flightassistant.protections.${this.optionId}"))
        description(OptionDescription.of(Component.translatable("config.flightassistant.protections.${this.optionId}.description")))
        available(FAConfig.global.flightProtections)
    }
}
