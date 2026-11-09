package com.glxcian.shf.mixin;

import com.glxcian.shf.stat.PlayerProgression;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TamableAnimal.class)
public abstract class TamingSkillExperienceMixin {
	@Inject(method = "tame(Lnet/minecraft/world/entity/player/Player;)V", at = @At("TAIL"))
	private void singleplayerhsfeatures$awardTamingExperience(Player player, CallbackInfo ci) {
		TamableAnimal animal = (TamableAnimal) (Object) this;
		if (player instanceof ServerPlayer serverPlayer && animal.isOwnedBy(player)) {
			PlayerProgression.addExperience(serverPlayer, PlayerProgression.Skill.TAMING, 50.0);
		}
	}
}
