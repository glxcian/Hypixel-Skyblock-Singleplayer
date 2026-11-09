package com.glxcian.shf.mixin;

import java.util.Map;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class EnchantmentMaxLevelMixin {
	private static final Map<String, Integer> SINGLEPLAYERHSFEATURES_MAX_LEVELS = Map.ofEntries(
		Map.entry("enchantment.minecraft.efficiency", 10),
		Map.entry("enchantment.minecraft.sharpness", 7),
		Map.entry("enchantment.minecraft.protection", 7),
		Map.entry("enchantment.minecraft.looting", 5),
		Map.entry("enchantment.minecraft.fortune", 4),
		Map.entry("enchantment.minecraft.smite", 6),
		Map.entry("enchantment.minecraft.bane_of_arthropods", 7),
		Map.entry("enchantment.minecraft.power", 7),
		Map.entry("enchantment.minecraft.fire_aspect", 3),
		Map.entry("enchantment.minecraft.feather_falling", 10)
	);

	@Inject(method = "getMaxLevel()I", at = @At("RETURN"), cancellable = true)
	private void singleplayerhsfeatures$extendMaximumLevel(CallbackInfoReturnable<Integer> cir) {
		ComponentContents contents = ((Enchantment) (Object) this).description().getContents();
		if (!(contents instanceof TranslatableContents translated)) {
			return;
		}

		Integer level = SINGLEPLAYERHSFEATURES_MAX_LEVELS.get(translated.getKey());
		if (level != null && level > cir.getReturnValue()) {
			cir.setReturnValue(level);
		}
	}
}
