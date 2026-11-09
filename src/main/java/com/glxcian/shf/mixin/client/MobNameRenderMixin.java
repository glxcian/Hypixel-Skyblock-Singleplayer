package com.glxcian.shf.mixin.client;

import com.glxcian.shf.stat.MobDamageStat;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class MobNameRenderMixin<T extends Entity> {
	@Inject(
		method = "shouldShowName(Lnet/minecraft/world/entity/Entity;D)Z",
		at = @At("HEAD"),
		cancellable = true
	)
	private void singleplayerhsfeatures$showMobNameTag(
		Entity entity,
		double distanceToCameraSq,
		CallbackInfoReturnable<Boolean> cir
	) {
		if (entity instanceof Mob) {
			cir.setReturnValue(true);
		}
	}

	@Inject(
		method = "extractNameTags(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;FDD)V",
		at = @At("TAIL")
	)
	private void singleplayerhsfeatures$showRecentDamage(
		Entity entity,
		EntityRenderState state,
		float partialTicks,
		double nameTagDistance,
		double belowNameDistance,
		CallbackInfo ci
	) {
		if (!(entity instanceof Mob mob) || !MobDamageStat.wasRecentlyDamaged(mob)) {
			return;
		}

		MutableComponent damage = Component.literal("-" + Math.round(MobDamageStat.getLastDamage(mob)))
			.withStyle(ChatFormatting.RED);
		if (state.scoreText != null) {
			damage.append(Component.literal(" ").append(state.scoreText));
		}
		state.scoreText = damage;
	}
}
