package ru.octol1ttle.flightassistant;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;
import java.awt.Color;
import net.minecraft.util.Identifier;

public class FAConfig {
    public static ConfigClassHandler<FAConfig> HANDLER = ConfigClassHandler.createBuilder(FAConfig.class)
            .id(new Identifier(FlightAssistant.MODID, "main"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve("flightassistant.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting) // not needed, pretty print by default
                    .setJson5(true)
                    .build())
            .build();

    @SerialEntry
    public float hudScale = 1.0f;
    @SerialEntry
    @Deprecated
    public float halfThickness = 0.5f;

    @SerialEntry
    public Color primaryColor = Color.GREEN;
    @SerialEntry
    public Color statusColor = Color.WHITE;
    @SerialEntry
    public Color adviceColor = Color.CYAN;
    @SerialEntry
    public Color amberColor = Color.YELLOW;
    @SerialEntry
    public Color alertColor = Color.RED;

    public static void setup() {
        HANDLER.load();
    }

    public static FAConfig get() {
        return HANDLER.instance();
    }
}
