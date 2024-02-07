package ru.octol1ttle.flightassistant;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.config.FAConfig;

public class FAModMenuImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        // TODO: Text.translatable everywhere AH FUCKKKKKKKKKKKKKKKKKKKKkk
        return parent -> YetAnotherConfigLib.create(FAConfig.HANDLER, (defaults, config, builder) ->
                builder.title(Text.translatable("category.flightassistant"))
                        .category(ConfigCategory.createBuilder()
                                .name(Text.literal("HUD settings"))
                                .option(Option.<FAConfig.BatchedRendering>createBuilder()
                                        .name(Text.literal("Use Batched HUD Rendering"))
                                        .available(FlightAssistant.canUseBatching())
                                        .binding(FAConfig.BatchedRendering.DRAW_CALL_PER_COMPONENT, () -> config.batchedRendering, o -> config.batchedRendering = o)
                                        .controller(opt -> EnumControllerBuilder.create(opt).enumClass(FAConfig.BatchedRendering.class))
                                        .build())
                                .build())
        ).generateScreen(parent);
    }
}
