package com.glxcian.shf.mixin;

import com.glxcian.shf.stat.EnchantmentStatEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public abstract class FeatherFallingProtectionMixin {
	@Inject(
		method = "getDamageProtection(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/damagesource/DamageSource;)F",
		at = @At("RETURN"),
		cancellable = true
	)
	private static void singleplayerhsfeatures$useSkyBlockFallReduction(
		ServerLevel level,
		LivingEntity entity,
		DamageSource source,
		CallbackInfoReturnable<Float> cir
	) {
		if (!source.is(DamageTypeTags.IS_FALL)) {
			return;
		}
		int featherFallingLevel = EnchantmentStatEffects.getFeatherFallingLevel(entity);
		if (featherFallingLevel > 0) {
			// Vanilla Feather Falling contributes 3 protection points per level (12%).
			// SkyBlock uses 5% per level, or 1.25 protection points.
			cir.setReturnValue(cir.getReturnValue() - featherFallingLevel * 3.0F
				+ featherFallingLevel * 1.25F);
		}
	}
}
