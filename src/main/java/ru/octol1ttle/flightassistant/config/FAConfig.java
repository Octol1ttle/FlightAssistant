package ru.octol1ttle.flightassistant.config;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import ru.octol1ttle.flightassistant.FlightAssistant;

public class FAConfig {
    private static final ConfigClassHandler<HUDConfig> HUD_HANDLER = ConfigClassHandler.createBuilder(HUDConfig.class)
            .id(new Identifier(FlightAssistant.MODID, "hud"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve("%s/hud.json5".formatted(FlightAssistant.MODID)))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting) // not needed, pretty print by default
                    .setJson5(true)
                    .build())
            .build();

    private static final ConfigClassHandler<IndicatorConfigStorage> INDICATORS_STORAGE_HANDLER = ConfigClassHandler.createBuilder(IndicatorConfigStorage.class)
            .id(new Identifier(FlightAssistant.MODID, "indicators"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve("%s/indicators.json5".formatted(FlightAssistant.MODID)))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting) // not needed, pretty print by default
                    .setJson5(true)
                    .build())
            .build();

    private static final ConfigClassHandler<ComputerConfig> COMPUTER_HANDLER = ConfigClassHandler.createBuilder(ComputerConfig.class)
            .id(new Identifier(FlightAssistant.MODID, "computers"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve("%s/computers.json5".formatted(FlightAssistant.MODID)))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting) // not needed, pretty print by default
                    .setJson5(true)
                    .build())
            .build();

    public static void setup() {
        HUD_HANDLER.load();
        INDICATORS_STORAGE_HANDLER.load();
        COMPUTER_HANDLER.load();
    }

    public static void save() {
        HUD_HANDLER.save();
        INDICATORS_STORAGE_HANDLER.save();
        COMPUTER_HANDLER.save();
    }

    public static HUDConfig hud() {
        return HUD_HANDLER.instance();
    }

    public static IndicatorConfigStorage.IndicatorConfig indicator() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            throw new IllegalStateException("Attempted to retrieve indicator settings when there is no player");
        }

        if (client.player.isFallFlying()) {
            return INDICATORS_STORAGE_HANDLER.instance().flying;
        }

        for (ItemStack stack : client.player.getItemsEquipped()) {
            if (Items.ELYTRA.equals(stack.getItem())) {
                return INDICATORS_STORAGE_HANDLER.instance().notFlyingHasElytra;
            }
        }

        return INDICATORS_STORAGE_HANDLER.instance().notFlyingNoElytra;
    }

    public static ComputerConfig computer() {
        return COMPUTER_HANDLER.instance();
    }

    public static IndicatorConfigStorage getIndicatorConfigStorage() {
        return INDICATORS_STORAGE_HANDLER.instance();
    }
}
