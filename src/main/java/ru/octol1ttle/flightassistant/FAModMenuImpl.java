package ru.octol1ttle.flightassistant;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import java.awt.Color;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.config.HudConfig;

public class FAModMenuImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> YetAnotherConfigLib.create(FAConfig.HANDLER, (defaults, config, builder) -> builder
                .title(Text.translatable("mod.flightassistant"))
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
                        .option(Option.<Float>createBuilder()
                                .name(Text.translatable("config.flightassistant.settings.frame_width"))
                                .binding(0.6f, () -> config.frameWidth, o -> config.frameWidth = o)
                                .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                        .range(0.3f, 0.9f)
                                        .step(0.05f)
                                        .formatValue(value -> Text.literal(MathHelper.floor(value * 100.0f) + "%"))
                                )
                                .build()
                        )
                        .option(Option.<Float>createBuilder()
                                .name(Text.translatable("config.flightassistant.settings.frame_height"))
                                .binding(0.6f, () -> config.frameHeight, o -> config.frameHeight = o)
                                .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                        .range(0.3f, 0.9f)
                                        .step(0.05f)
                                        .formatValue(value -> Text.literal(MathHelper.floor(value * 100.0f) + "%"))
                                )
                                .build()
                        )
                        .build()
                )
                .category(hud(Text.translatable("config.flightassistant.category.not_flying_no_elytra"), config.notFlyingNoElytra))
                .category(hud(Text.translatable("config.flightassistant.category.not_flying_has_elytra"), config.notFlyingHasElytra))
                .category(hud(Text.translatable("config.flightassistant.category.flying"), config.flying))
        ).generateScreen(parent);
    }

    private ConfigCategory hud(Text name, HudConfig config) {
        return ConfigCategory.createBuilder()
                .name(name)
                .option(Option.<Color>createBuilder()
                        .name(Text.translatable("config.flightassistant.hud.frame_color"))
                        .binding(Color.GREEN, () -> config.frameColor, o -> config.frameColor = o)
                        .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(false))
                        .build())
                .option(Option.<Color>createBuilder()
                        .name(Text.translatable("config.flightassistant.hud.status_color"))
                        .binding(Color.WHITE, () -> config.statusColor, o -> config.statusColor = o)
                        .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(false))
                        .build())
                .option(Option.<Color>createBuilder()
                        .name(Text.translatable("config.flightassistant.hud.advisory_color"))
                        .binding(Color.CYAN, () -> config.advisoryColor, o -> config.advisoryColor = o)
                        .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(false))
                        .build())
                .option(Option.<Color>createBuilder()
                        .name(Text.translatable("config.flightassistant.hud.caution_color"))
                        .binding(Color.YELLOW, () -> config.cautionColor, o -> config.cautionColor = o)
                        .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(false))
                        .build())
                .option(Option.<Color>createBuilder()
                        .name(Text.translatable("config.flightassistant.hud.warning_color"))
                        .binding(Color.RED, () -> config.warningColor, o -> config.warningColor = o)
                        .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(false))
                        .build())
                .build();
    }
}
