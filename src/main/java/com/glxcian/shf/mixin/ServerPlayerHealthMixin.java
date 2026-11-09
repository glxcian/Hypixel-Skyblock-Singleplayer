package com.glxcian.shf.mixin;

import com.glxcian.shf.stat.HealthRegenStat;
import com.glxcian.shf.stat.HealthStat;
import com.glxcian.shf.stat.PlayerHealthAccess;
import com.glxcian.shf.stat.PlayerProgression;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerHealthMixin {
	@Inject(method = "restoreFrom(Lnet/minecraft/server/level/ServerPlayer;Z)V", at = @At("TAIL"))
	private void singleplayerhsfeatures$copyHealthStats(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
		ServerPlayer player = (ServerPlayer) (Object) this;
		((PlayerHealthAccess) player).singleplayerhsfeatures$setBonusHealth(HealthStat.getBonusHealth(oldPlayer));
		HealthRegenStat.set(player, HealthRegenStat.get(oldPlayer));
		PlayerProgression.copy(oldPlayer, player);
	}
}
