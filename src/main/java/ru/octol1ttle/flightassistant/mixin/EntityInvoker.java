package ru.octol1ttle.flightassistant.mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public interface EntityInvoker {
//? if >=1.21.2 {
    /*@org.spongepowered.asm.mixin.gen.Invoker("isInvulnerableToBase")
    boolean invokeIsInvulnerableToBase(net.minecraft.world.damagesource.DamageSource damageSource);
*///?}

    @org.spongepowered.asm.mixin.gen.Invoker("isStateClimbable")
    boolean invokeIsStateClimbable(net.minecraft.world.level.block.state.BlockState state);
}
