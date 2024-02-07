package ru.octol1ttle.flightassistant.config;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ru.octol1ttle.flightassistant.FlightAssistant;

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
    public HudConfig flying = new HudConfig();
    @SerialEntry
    public HudConfig notFlyingHasElytra = new HudConfig().setMinimal();
    @SerialEntry
    public HudConfig notFlyingNoElytra = new HudConfig().disableAll();

    @SerialEntry
    public BatchedRendering batchedRendering = BatchedRendering.SINGLE_DRAW_CALL;
    @SerialEntry
    public float hudScale = 1.0f;

    public static void setup() {
        HANDLER.load();
    }

    public static FAConfig get() {
        return HANDLER.instance();
    }

    public static HudConfig hud() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            throw new IllegalStateException("Attempted to retrieve HUD settings when there is no player");
        }

        if (client.player.isFallFlying()) {
            return HANDLER.instance().flying;
        }

        for (ItemStack stack : client.player.getItemsEquipped()) {
            if (Items.ELYTRA.equals(stack.getItem())) {
                return HANDLER.instance().notFlyingHasElytra;
            }
        }

        return HANDLER.instance().notFlyingNoElytra;
    }

    public enum BatchedRendering implements NameableEnum {
        SINGLE_DRAW_CALL,
        DRAW_CALL_PER_COMPONENT,
        NO_BATCHING;

        @Override
        public Text getDisplayName() {
            return Text.literal(name());
        }
    }
}
