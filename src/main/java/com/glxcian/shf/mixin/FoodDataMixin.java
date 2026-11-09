package com.glxcian.shf.mixin;

import net.minecraft.world.food.FoodData;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {
	@Inject(method = "tick(Lnet/minecraft/server/level/ServerPlayer;)V", at = @At("HEAD"), cancellable = true)
	private void singleplayerhsfeatures$disableVanillaHungerAndRegeneration(
		ServerPlayer player,
		CallbackInfo ci
	) {
		ci.cancel();
	}
}
