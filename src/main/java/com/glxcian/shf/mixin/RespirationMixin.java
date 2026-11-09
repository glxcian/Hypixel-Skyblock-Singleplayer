package com.glxcian.shf.mixin;

import com.glxcian.shf.stat.RespirationStat;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class RespirationMixin {
	@Inject(method = "decreaseAirSupply(I)I", at = @At("RETURN"), cancellable = true)
	private void singleplayerhsfeatures$extendAirSupply(int air, CallbackInfoReturnable<Integer> cir) {
		if (RespirationStat.shouldSkipAirDrain((LivingEntity) (Object) this)) {
			cir.setReturnValue(cir.getReturnValue() + 1);
		}
	}
}
