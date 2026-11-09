package com.glxcian.shf.mixin;

import com.glxcian.shf.stat.EnchantmentStatEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class FeatherFallingSkyBlockMixin {
	@ModifyVariable(
		method = "calculateFallDamage(DF)I",
		at = @At("HEAD"),
		argsOnly = true,
		ordinal = 0
	)
	private double singleplayerhsfeatures$increaseSafeFallHeight(double fallDistance) {
		int level = EnchantmentStatEffects.getFeatherFallingLevel((LivingEntity) (Object) this);
		return Math.max(0.0, fallDistance - level * 5.0);
	}
}
