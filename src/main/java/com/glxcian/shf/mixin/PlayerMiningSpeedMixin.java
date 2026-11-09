package com.glxcian.shf.mixin;

import com.glxcian.shf.stat.MiningSpeedStat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMiningSpeedMixin {
	@Inject(method = "getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;)F", at = @At("RETURN"), cancellable = true)
	private void singleplayerhsfeatures$applyMiningSpeed(BlockState state, CallbackInfoReturnable<Float> cir) {
		Player player = (Player) (Object) this;
		float speed = MiningSpeedStat.applyToBlockSpeed(player, cir.getReturnValue());
		cir.setReturnValue(speed);
	}
}
