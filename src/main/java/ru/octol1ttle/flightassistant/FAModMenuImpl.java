package ru.octol1ttle.flightassistant;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.config.FAConfig;

public class FAModMenuImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> YetAnotherConfigLib.create(FAConfig.HANDLER, (defaults, config, builder) ->
                builder.title(Text.translatable("mod.flightassistant"))
                        .category(ConfigCategory.createBuilder()
                                .name(Text.translatable("config.flightassistant.category.hud_settings"))
                                .option(Option.<FAConfig.BatchedRendering>createBuilder()
                                        .name(Text.translatable("config.flightassistant.settings.batching"))
                                        .available(FlightAssistant.canUseBatching())
                                        .binding(FAConfig.BatchedRendering.SINGLE_BATCH, () -> config.batchedRendering, o -> config.batchedRendering = o)
                                        .controller(opt -> EnumControllerBuilder.create(opt).enumClass(FAConfig.BatchedRendering.class))
                                        .build()
                                )
                                .option(Option.<Float>createBuilder()
                                        .name(Text.translatable("config.flightassistant.settings.hud_scale"))
                                        .binding(1.0f, () -> config.hudScale, o -> config.hudScale = o)
                                        .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                                .range(0.1f, 5.0f)
                                                .step(0.05f)
                                                .formatValue(value -> Text.literal(MathHelper.floor(value * 100.0f) + "%"))
                                        )
                                        .build()
                                )
                                .build()
                        )
        ).generateScreen(parent);
    }
}
