package com.glxcian.shf.mixin;

import com.glxcian.shf.stat.HealthStat;
import com.glxcian.shf.stat.PlayerProgression;
import com.glxcian.shf.stat.PlayerHealthAccess;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerHealthMixin implements PlayerHealthAccess {
	@Unique
	private float singleplayerhsfeatures$bonusHealth;

	@Unique
	private float singleplayerhsfeatures$healthRegen = 100.0F;

	@Inject(method = "addAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueOutput;)V", at = @At("TAIL"))
	private void singleplayerhsfeatures$saveHealthRegen(ValueOutput output, CallbackInfo ci) {
		output.putFloat("SingleplayerHSFeaturesBonusHealth", this.singleplayerhsfeatures$bonusHealth);
		output.putFloat("SingleplayerHSFeaturesHealthRegen", this.singleplayerhsfeatures$healthRegen);
	}

	@Inject(method = "readAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueInput;)V", at = @At("TAIL"))
	private void singleplayerhsfeatures$loadHealthRegen(ValueInput input, CallbackInfo ci) {
		float bonusHealth = input.getFloatOr("SingleplayerHSFeaturesBonusHealth", 0.0F);
		float healthRegen = input.getFloatOr("SingleplayerHSFeaturesHealthRegen", 100.0F);
		if (!Float.isFinite(bonusHealth) || bonusHealth < 0.0F) {
			throw new IllegalArgumentException("Saved Bonus Health must be finite and non-negative.");
		}
		if (!Float.isFinite(healthRegen) || healthRegen < 0.0F) {
			throw new IllegalArgumentException("Saved Health Regen must be finite and non-negative.");
		}
		this.singleplayerhsfeatures$bonusHealth = bonusHealth;
		this.singleplayerhsfeatures$healthRegen = healthRegen;
	}

	@Inject(method = "tick()V", at = @At("TAIL"))
	private void singleplayerhsfeatures$regenerateHealth(CallbackInfo ci) {
		if ((Object) this instanceof ServerPlayer player && player.tickCount % 20 == 0) {
			HealthStat.updateSkyBlockLevelBonus(player);
			HealthStat.regenerate(player, this.singleplayerhsfeatures$healthRegen);
		}
	}

	@Override
	public float singleplayerhsfeatures$getBonusHealth() {
		return this.singleplayerhsfeatures$bonusHealth;
	}

	@Override
	public void singleplayerhsfeatures$setBonusHealth(float bonusHealth) {
		this.singleplayerhsfeatures$bonusHealth = bonusHealth;
	}

	@Override
	public float singleplayerhsfeatures$getHealthRegen() {
		return this.singleplayerhsfeatures$healthRegen;
	}

	@Override
	public void singleplayerhsfeatures$setHealthRegen(float healthRegen) {
		this.singleplayerhsfeatures$healthRegen = healthRegen;
	}
}
