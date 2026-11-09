package com.glxcian.shf.mixin;

import com.glxcian.shf.stat.PlayerProgression;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.ContainerInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public abstract class BrewingStandClickExperienceMixin {
	@Unique
	private int singleplayerhsfeatures$potionCountBeforeClick;

	@Inject(
		method = "clicked(IILnet/minecraft/world/inventory/ContainerInput;Lnet/minecraft/world/entity/player/Player;)V",
		at = @At("HEAD")
	)
	private void singleplayerhsfeatures$capturePotionOutput(
		int slotIndex,
		int button,
		ContainerInput input,
		Player player,
		CallbackInfo ci
	) {
		this.singleplayerhsfeatures$potionCountBeforeClick = 0;
		if ((Object) this instanceof BrewingStandMenu && input == ContainerInput.PICKUP
			&& slotIndex >= 0 && slotIndex < 3) {
			this.singleplayerhsfeatures$potionCountBeforeClick = ((AbstractContainerMenu) (Object) this)
				.slots.get(slotIndex).getItem().getCount();
		}
	}

	@Inject(
		method = "clicked(IILnet/minecraft/world/inventory/ContainerInput;Lnet/minecraft/world/entity/player/Player;)V",
		at = @At("RETURN")
	)
	private void singleplayerhsfeatures$awardPotionClickExperience(
		int slotIndex,
		int button,
		ContainerInput input,
		Player player,
		CallbackInfo ci
	) {
		if (!(player instanceof ServerPlayer serverPlayer) || this.singleplayerhsfeatures$potionCountBeforeClick <= 0) {
			return;
		}
		int remaining = ((AbstractContainerMenu) (Object) this).slots.get(slotIndex).getItem().getCount();
		int taken = Math.max(0, this.singleplayerhsfeatures$potionCountBeforeClick - remaining);
		if (taken > 0) {
			PlayerProgression.addExperience(serverPlayer, PlayerProgression.Skill.ALCHEMY, taken * 5.0);
		}
		this.singleplayerhsfeatures$potionCountBeforeClick = 0;
	}
}
