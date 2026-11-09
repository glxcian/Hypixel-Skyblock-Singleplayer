package com.glxcian.shf.mixin;

import com.glxcian.shf.stat.PlayerProgression;
import java.util.function.Consumer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityLootCollectionMixin {
	@ModifyVariable(
		method = "dropFromLootTable(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;ZLnet/minecraft/resources/ResourceKey;Ljava/util/function/Consumer;)V",
		at = @At("HEAD"),
		argsOnly = true,
		ordinal = 0
	)
	private Consumer<ItemStack> singleplayerhsfeatures$recordMobDrops(Consumer<ItemStack> original) {
		LivingEntity entity = (LivingEntity) (Object) this;
		if (!(entity.getLastHurtByPlayer() instanceof ServerPlayer player)) {
			return original;
		}
		return stack -> {
			PlayerProgression.addCollectionItems(player, stack);
			original.accept(stack);
		};
	}
}
