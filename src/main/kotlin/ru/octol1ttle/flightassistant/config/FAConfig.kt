package ru.octol1ttle.flightassistant.config

import com.google.gson.GsonBuilder
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder
import dev.isxander.yacl3.platform.YACLPlatform
import ru.octol1ttle.flightassistant.FlightAssistant.MOD_ID
import ru.octol1ttle.flightassistant.FlightAssistant.id
import ru.octol1ttle.flightassistant.config.options.AlertOptions
import ru.octol1ttle.flightassistant.config.options.GlobalOptions
import ru.octol1ttle.flightassistant.config.options.HudOptions
import ru.octol1ttle.flightassistant.config.options.ProtectionOptions

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
    private val HUD_HANDLER: ConfigClassHandler<HudOptions> =
        ConfigClassHandler.createBuilder(HudOptions::class.java)
            .id(id("hud"))
            .serializer {
                GsonConfigSerializerBuilder.create(it)
                    .setPath(YACLPlatform.getConfigDir().resolve("$MOD_ID/hud.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting) // not needed, pretty print by default
                    .setJson5(true)
                    .build()
            }
            .build()
    private val ALERTS_HANDLER: ConfigClassHandler<AlertOptions> =
        ConfigClassHandler.createBuilder(AlertOptions::class.java)
            .id(id("alerts"))
            .serializer {
                GsonConfigSerializerBuilder.create(it)
                    .setPath(YACLPlatform.getConfigDir().resolve("$MOD_ID/alerts.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting) // not needed, pretty print by default
                    .setJson5(true)
                    .build()
            }
            .build()
    private val PROTECTIONS_HANDLER: ConfigClassHandler<ProtectionOptions> =
        ConfigClassHandler.createBuilder(ProtectionOptions::class.java)
            .id(id("protections"))
            .serializer {
                GsonConfigSerializerBuilder.create(it)
                    .setPath(YACLPlatform.getConfigDir().resolve("$MOD_ID/protections.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting) // not needed, pretty print by default
                    .setJson5(true)
                    .build()
            }
            .build()

    internal val global: GlobalOptions
        get() = GLOBAL_HANDLER.instance()
    internal val hud: HudOptions
        get() = HUD_HANDLER.instance()
    internal val alerts: AlertOptions
        get() = ALERTS_HANDLER.instance()

    internal val protectionsConfig: ProtectionOptions
        get() = PROTECTIONS_HANDLER.instance()
    internal val protections: ProtectionOptions
        get() = if (global.flightProtections) PROTECTIONS_HANDLER.instance() else ProtectionOptions.DISABLED

    fun load() {
        GLOBAL_HANDLER.load()
        HUD_HANDLER.load()
        ALERTS_HANDLER.load()
        PROTECTIONS_HANDLER.load()
    }

    fun save() {
        GLOBAL_HANDLER.save()
        HUD_HANDLER.save()
        ALERTS_HANDLER.save()
        PROTECTIONS_HANDLER.save()
    }
}
