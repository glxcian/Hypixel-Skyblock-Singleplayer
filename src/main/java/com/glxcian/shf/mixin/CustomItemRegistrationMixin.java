package com.glxcian.shf.mixin;

import com.glxcian.shf.item.CustomItems;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Registers legacy IDs solely to migrate old saved stacks to vanilla item stacks. */
@Mixin(BuiltInRegistries.class)
public abstract class CustomItemRegistrationMixin {
	@Inject(
		method = "bootStrap()V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/registries/BuiltInRegistries;freeze()V",
			shift = At.Shift.BEFORE
		)
	)
	private static void singleplayerhsfeatures$registerLegacyItems(CallbackInfo ci) {
		CustomItems.initializeLegacyItems();
	}
}
