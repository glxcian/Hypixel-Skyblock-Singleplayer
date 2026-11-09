package com.glxcian.shf.mixin;

import com.glxcian.shf.stat.DefenseStat;
import com.glxcian.shf.stat.DamageStat;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityDefenseMixin {
	@Unique
	private float singleplayerhsfeatures$damageMultiplier = 1.0F;

	@Inject(
		method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
		at = @At("HEAD")
	)
	private void singleplayerhsfeatures$captureDamageSource(
		ServerLevel level,
		DamageSource source,
		float damage,
		CallbackInfoReturnable<Boolean> cir
	) {
		this.singleplayerhsfeatures$damageMultiplier = source.getEntity() instanceof Player player
			? DamageStat.getPlayerDamageMultiplier(
				player.getMainHandItem(),
				player,
				(LivingEntity) (Object) this,
				level,
				source
			)
			: 1.0F;
	}

	@Inject(
		method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
		at = @At("RETURN")
	)
	private void singleplayerhsfeatures$clearDamageSource(
		ServerLevel level,
		DamageSource source,
		float damage,
		CallbackInfoReturnable<Boolean> cir
	) {
		this.singleplayerhsfeatures$damageMultiplier = 1.0F;
	}

	@ModifyVariable(
		method = "actuallyHurt(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)V",
		at = @At("HEAD"),
		argsOnly = true,
		ordinal = 0
	)
	private float singleplayerhsfeatures$scaleDamageFromPlayers(float damage) {
		return damage * this.singleplayerhsfeatures$damageMultiplier;
	}

	@Inject(
		method = "getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F",
		at = @At("HEAD"),
		cancellable = true
	)
	private void singleplayerhsfeatures$applySkyBlockDefense(
		DamageSource source,
		float damage,
		CallbackInfoReturnable<Float> cir
	) {
		LivingEntity entity = (LivingEntity) (Object) this;
		int defense = DefenseStat.get(entity);
		if (!source.is(DamageTypeTags.BYPASSES_ARMOR) && DefenseStat.canApplySkyBlockDefense(entity)) {
			cir.setReturnValue(DefenseStat.applyDamageReduction(damage, defense));
		}
	}
}
