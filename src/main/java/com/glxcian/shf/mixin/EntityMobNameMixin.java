package com.glxcian.shf.mixin;

import com.glxcian.shf.stat.MobLevel;
import com.glxcian.shf.stat.HealthStat;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMobNameMixin {
	@Inject(method = "getDisplayName()Lnet/minecraft/network/chat/Component;", at = @At("RETURN"), cancellable = true)
	private void singleplayerhsfeatures$addMobLevelToName(CallbackInfoReturnable<Component> cir) {
		if ((Object) this instanceof Mob mob) {
			MutableComponent level = Component.literal("[Lv" + MobLevel.get(mob) + "] ").withStyle(ChatFormatting.GRAY);
			level.append(cir.getReturnValue().copy().withStyle(ChatFormatting.RED));
			float maximumHealth = HealthStat.getMaximum(mob);
			float currentHealth = HealthStat.getCurrent(mob);
			level.append(Component.literal(String.format(
				java.util.Locale.ROOT,
				" %.0f/%.0f",
				currentHealth,
				maximumHealth
			)).withStyle(ChatFormatting.GREEN));
			level.append(Component.literal("❤").withStyle(ChatFormatting.RED));
			cir.setReturnValue(level);
		}
	}

	@Inject(method = "shouldShowName()Z", at = @At("HEAD"), cancellable = true)
	private void singleplayerhsfeatures$showMobLevel(CallbackInfoReturnable<Boolean> cir) {
		if ((Object) this instanceof Mob) {
			cir.setReturnValue(true);
		}
	}
}
