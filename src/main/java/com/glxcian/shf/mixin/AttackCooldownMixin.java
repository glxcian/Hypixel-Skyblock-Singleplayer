package com.glxcian.shf.mixin;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class AttackCooldownMixin {
	@Inject(method = "getAttackStrengthScale(F)F", at = @At("HEAD"), cancellable = true)
	private void singleplayerhsfeatures$removeAttackCooldown(float baseTime, CallbackInfoReturnable<Float> cir) {
		cir.setReturnValue(1.0F);
	}
}
