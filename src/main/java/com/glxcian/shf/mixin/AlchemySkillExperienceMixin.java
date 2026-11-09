package com.glxcian.shf.mixin;

import com.glxcian.shf.stat.PlayerProgression;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingStandMenu.class)
public abstract class AlchemySkillExperienceMixin {
	@Inject(method = "quickMoveStack(Lnet/minecraft/world/entity/player/Player;I)Lnet/minecraft/world/item/ItemStack;", at = @At("RETURN"))
	private void singleplayerhsfeatures$awardShiftClickPotionExperience(
		Player player,
		int slotIndex,
		CallbackInfoReturnable<ItemStack> cir
	) {
		ItemStack moved = cir.getReturnValue();
		if (slotIndex >= 0 && slotIndex < 3 && !moved.isEmpty() && player instanceof ServerPlayer serverPlayer) {
			PlayerProgression.addExperience(
				serverPlayer, PlayerProgression.Skill.ALCHEMY, moved.getCount() * 5.0
			);
		}
	}
}
