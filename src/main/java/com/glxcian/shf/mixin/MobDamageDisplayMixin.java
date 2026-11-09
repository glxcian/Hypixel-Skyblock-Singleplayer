package com.glxcian.shf.mixin;

import com.glxcian.shf.stat.MobDamageStat;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MobDamageDisplayMixin {
	@Unique
	private float singleplayerhsfeatures$healthBeforeDamage;

	@Inject(
		method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
		at = @At("HEAD")
	)
	private void singleplayerhsfeatures$captureHealth(
		ServerLevel level,
		DamageSource source,
		float amount,
		CallbackInfoReturnable<Boolean> cir
	) {
		this.singleplayerhsfeatures$healthBeforeDamage = ((LivingEntity) (Object) this).getHealth();
	}

	@Inject(
		method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
		at = @At("RETURN")
	)
	private void singleplayerhsfeatures$recordDamage(
		ServerLevel level,
		DamageSource source,
		float amount,
		CallbackInfoReturnable<Boolean> cir
	) {
		if (!cir.getReturnValue() || !((Object) this instanceof Mob mob)) {
			return;
		}

		float damage = this.singleplayerhsfeatures$healthBeforeDamage - mob.getHealth();
		MobDamageStat.record(mob, damage * 5.0F);
	}
}
