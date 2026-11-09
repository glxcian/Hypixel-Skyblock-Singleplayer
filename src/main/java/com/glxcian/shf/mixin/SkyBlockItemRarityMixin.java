package com.glxcian.shf.mixin;

import com.glxcian.shf.SingleplayerHSFeatures;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * SkyBlock item names retain their actual item rarity when enchanted.
 * Vanilla normally upgrades the displayed rarity of enchanted items
 * (for example, Uncommon to Rare), which changes the name to aqua.
 */
@Mixin(ItemStack.class)
public abstract class SkyBlockItemRarityMixin {
	@Inject(method = "getRarity()Lnet/minecraft/world/item/Rarity;", at = @At("HEAD"), cancellable = true)
	private void singleplayerhsfeatures$preserveSkyBlockRarity(CallbackInfoReturnable<Rarity> cir) {
		ItemStack stack = (ItemStack) (Object) this;
		if (BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace()
			.equals(SingleplayerHSFeatures.MOD_ID)) {
			cir.setReturnValue(stack.getOrDefault(DataComponents.RARITY, Rarity.COMMON));
		}
	}
}
