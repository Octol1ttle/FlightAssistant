package ru.octol1ttle.flightassistant.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Minecraft.class)
public interface ClientLevelRunningNormallyInvoker {
//? if >=1.21 {
/*@org.spongepowered.asm.mixin.gen.Invoker("isLevelRunningNormally")
boolean invokeIsLevelRunningNormally();
    *///?}
}
