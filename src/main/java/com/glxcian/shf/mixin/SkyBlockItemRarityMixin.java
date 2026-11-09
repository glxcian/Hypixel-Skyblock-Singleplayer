package com.glxcian.shf.mixin;

import com.glxcian.shf.item.SkyBlockRarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Prevents vanilla enchantment glint from changing the item's base rarity.
 * SkyBlock's full rarity palette is rendered by the tooltip mixin.
 */
@Mixin(ItemStack.class)
public abstract class SkyBlockItemRarityMixin {
	@Inject(method = "getRarity()Lnet/minecraft/world/item/Rarity;", at = @At("HEAD"), cancellable = true)
	private void singleplayerhsfeatures$preserveSkyBlockRarity(CallbackInfoReturnable<Rarity> cir) {
		ItemStack stack = (ItemStack) (Object) this;
		cir.setReturnValue(SkyBlockRarity.get(stack).vanillaRarity());
	}
}
