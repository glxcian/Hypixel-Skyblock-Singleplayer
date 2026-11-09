package com.glxcian.shf.mixin;

import com.glxcian.shf.stat.PlayerProgression;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.EnchantmentMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantingSkillExperienceMixin {
	@Unique
	private int singleplayerhsfeatures$experienceLevelBeforeEnchant;

	@Inject(method = "clickMenuButton(Lnet/minecraft/world/entity/player/Player;I)Z", at = @At("HEAD"))
	private void singleplayerhsfeatures$captureExperienceLevel(
		Player player,
		int buttonId,
		CallbackInfoReturnable<Boolean> cir
	) {
		this.singleplayerhsfeatures$experienceLevelBeforeEnchant = player.experienceLevel;
	}

	@Inject(method = "clickMenuButton(Lnet/minecraft/world/entity/player/Player;I)Z", at = @At("RETURN"))
	private void singleplayerhsfeatures$awardEnchantingExperience(
		Player player,
		int buttonId,
		CallbackInfoReturnable<Boolean> cir
	) {
		if (!cir.getReturnValue() || !(player instanceof ServerPlayer serverPlayer)) {
			return;
		}
		int spentLevels = Math.max(0, this.singleplayerhsfeatures$experienceLevelBeforeEnchant - player.experienceLevel);
		if (spentLevels > 0) {
			PlayerProgression.addExperience(
				serverPlayer,
				PlayerProgression.Skill.ENCHANTING,
				3.5 * Math.pow(spentLevels, 1.5)
			);
		}
	}
}
