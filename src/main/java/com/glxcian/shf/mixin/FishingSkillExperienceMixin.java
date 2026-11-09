package com.glxcian.shf.mixin;

import com.glxcian.shf.stat.PlayerProgression;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingHook.class)
public abstract class FishingSkillExperienceMixin {
	@Inject(method = "retrieve(Lnet/minecraft/world/item/ItemStack;)I", at = @At("RETURN"))
	private void singleplayerhsfeatures$awardFishingExperience(
		ItemStack rod,
		CallbackInfoReturnable<Integer> cir
	) {
		FishingHook hook = (FishingHook) (Object) this;
		if (hook.getPlayerOwner() instanceof ServerPlayer player
			&& (cir.getReturnValue() == 1 || cir.getReturnValue() == 3)) {
			PlayerProgression.addExperience(player, PlayerProgression.Skill.FISHING, 5.0);
		}
	}
}
