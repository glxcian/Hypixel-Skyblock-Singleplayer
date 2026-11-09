package com.glxcian.shf.mixin;

import java.util.function.Consumer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemDurabilityMixin {
	@Inject(
		method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/server/level/ServerPlayer;Ljava/util/function/Consumer;)V",
		at = @At("HEAD"),
		cancellable = true
	)
	private void singleplayerhsfeatures$preventToolAndArmorDurabilityLoss(
		int amount,
		ServerLevel level,
		ServerPlayer player,
		Consumer<ItemStack> onBreak,
		CallbackInfo ci
	) {
		ItemStack stack = (ItemStack) (Object) this;
		if (isToolOrArmor(stack)) {
			ci.cancel();
		}
	}

	private static boolean isToolOrArmor(ItemStack stack) {
		if (!stack.isDamageableItem()) {
			return false;
		}

		if (stack.has(DataComponents.TOOL) || stack.has(DataComponents.WEAPON)) {
			return true;
		}

		Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
		if (equippable == null) {
			return false;
		}

		return switch (equippable.slot()) {
			case HEAD, CHEST, LEGS, FEET -> true;
			default -> false;
		};
	}
}
