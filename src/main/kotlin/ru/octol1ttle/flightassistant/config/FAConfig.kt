package ru.octol1ttle.flightassistant.config

import com.google.gson.GsonBuilder
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder
import dev.isxander.yacl3.platform.YACLPlatform
import net.minecraft.client.player.LocalPlayer
import ru.octol1ttle.flightassistant.FlightAssistant.MOD_ID
import ru.octol1ttle.flightassistant.FlightAssistant.id
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.config.options.DisplayOptions
import ru.octol1ttle.flightassistant.config.options.DisplayOptionsStorage
import ru.octol1ttle.flightassistant.config.options.GlobalOptions
import ru.octol1ttle.flightassistant.config.options.SafetyOptions

object FAConfig {
    private val GLOBAL_HANDLER: ConfigClassHandler<GlobalOptions> =
        ConfigClassHandler.createBuilder(GlobalOptions::class.java)
            .id(id("global"))
            .serializer {
                GsonConfigSerializerBuilder.create(it)
                    .setPath(YACLPlatform.getConfigDir().resolve("$MOD_ID/global.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting) // not needed, pretty print by default
                    .setJson5(true)
                    .build()
            }
            .build()

    private val DISPLAY_HANDLER: ConfigClassHandler<DisplayOptionsStorage> =
        ConfigClassHandler.createBuilder(DisplayOptionsStorage::class.java)
            .id(id("displays"))
            .serializer {
                GsonConfigSerializerBuilder.create(it)
                    .setPath(YACLPlatform.getConfigDir().resolve("$MOD_ID/displays.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting) // not needed, pretty print by default
                    .setJson5(true)
                    .build()
            }
            .build()

    private val SAFETY_HANDLER: ConfigClassHandler<SafetyOptions> =
        ConfigClassHandler.createBuilder(SafetyOptions::class.java)
            .id(id("safety"))
            .serializer {
                GsonConfigSerializerBuilder.create(it)
                    .setPath(YACLPlatform.getConfigDir().resolve("$MOD_ID/safety.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting) // not needed, pretty print by default
                    .setJson5(true)
                    .build()
            }
            .build()

    internal val global: GlobalOptions
        get() = GLOBAL_HANDLER.instance()
    internal val hudEnabled: Boolean
        get() = global.modEnabled && global.hudEnabled
    private val safetyEnabled: Boolean
        get() = global.modEnabled && global.safetyEnabled
    internal val displaysStorage: DisplayOptionsStorage
        get() = DISPLAY_HANDLER.instance()

    val display: DisplayOptions
        get() {
            val player: LocalPlayer = checkNotNull(mc.player)

            if (player.isFallFlying) {
                return displaysStorage.flying
            }

            if (!player.abilities.mayfly) {
//? if >=1.21.2 {
                /*for (slot: net.minecraft.world.entity.EquipmentSlot in net.minecraft.world.entity.EquipmentSlot.VALUES) {
                    if (player.getItemBySlot(slot).has(net.minecraft.core.component.DataComponents.GLIDER)) {
                        return displaysStorage.notFlyingHasElytra
                    }
                }
*///?} else {
                if (player.allSlots.any { stack -> stack.item is net.minecraft.world.item.ElytraItem }) {
                    return displaysStorage.notFlyingHasElytra
                }
//?}
            }

            return displaysStorage.notFlyingNoElytra
        }

    val safety: SafetyOptions
        get() = if (safetyEnabled) SAFETY_HANDLER.instance() else SafetyOptions.DISABLED

    internal val safetyConfig
        get() = SAFETY_HANDLER.instance()

    fun load() {
        GLOBAL_HANDLER.load()
        DISPLAY_HANDLER.load()
        SAFETY_HANDLER.load()
    }

    fun save() {
        GLOBAL_HANDLER.save()
        DISPLAY_HANDLER.save()
        SAFETY_HANDLER.save()
    }
}
