package ru.octol1ttle.flightassistant.config;

import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import java.util.Locale;
import net.minecraft.text.Text;

public class HUDConfig {
    @SerialEntry
    public BatchedRendering batchedRendering = BatchedRendering.SINGLE_BATCH;
    @SerialEntry
    public float hudScale = 1.0f;
    @SerialEntry
    public float frameWidth = 0.6f;
    @SerialEntry
    public float frameHeight = 0.6f;

    public enum BatchedRendering implements NameableEnum {
        NO_BATCHING,
        PER_COMPONENT,
        SINGLE_BATCH;

        @Override
        public Text getDisplayName() {
            return Text.translatable("config.flightassistant.hud.batching." + name().toLowerCase(Locale.ROOT));
        }
    }
}
