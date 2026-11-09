package com.glxcian.shf.mixin;

import com.glxcian.shf.item.CompactorAutomation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class CompactorAutomationMixin {
	@Inject(method = "tick()V", at = @At("TAIL"))
	private void singleplayerhsfeatures$compactInventory(CallbackInfo ci) {
		Player player = (Player) (Object) this;
		if (player instanceof ServerPlayer serverPlayer && player.tickCount % 10 == 0) {
			CompactorAutomation.tick(serverPlayer);
		}
	}
}
