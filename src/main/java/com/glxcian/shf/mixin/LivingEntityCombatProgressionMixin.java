package com.glxcian.shf.mixin;

import com.glxcian.shf.stat.MobProfileConfig;
import com.glxcian.shf.stat.PlayerProgression;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityCombatProgressionMixin {
	@Inject(
		method = "die(Lnet/minecraft/world/damagesource/DamageSource;)V",
		at = @At("HEAD")
	)
	private void singleplayerhsfeatures$awardCombatExperience(
		DamageSource source,
		CallbackInfo ci
	) {
		if (!((Object) this instanceof Mob mob)) {
			return;
		}

		ServerPlayer player = source.getEntity() instanceof ServerPlayer directPlayer
			? directPlayer
			: mob.getKillCredit() instanceof ServerPlayer creditedPlayer ? creditedPlayer : null;
		if (player == null) {
			return;
		}

		int experience = MobProfileConfig.combatExperience(mob);
		if (experience > 0) {
			PlayerProgression.addExperience(player, PlayerProgression.Skill.COMBAT, experience);
		}
		if (mob instanceof Animal) {
			PlayerProgression.addExperience(player, PlayerProgression.Skill.FARMING, 1.0);
		}
	}
}
