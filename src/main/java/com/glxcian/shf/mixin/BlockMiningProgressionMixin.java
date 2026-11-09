package com.glxcian.shf.mixin;

import com.glxcian.shf.stat.PlayerProgression;
import com.glxcian.shf.stat.EnchantmentStatEffects;
import com.glxcian.shf.stat.MiningExperience;
import com.glxcian.shf.stat.SkillExperience;
import java.util.List;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlockMiningProgressionMixin {
	@Inject(
		method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemInstance;)Ljava/util/List;",
		at = @At("RETURN"),
		cancellable = true
	)
	private static void singleplayerhsfeatures$recordMinedResources(
		BlockState state,
		ServerLevel level,
		BlockPos position,
		BlockEntity blockEntity,
		Entity breaker,
		ItemInstance tool,
		CallbackInfoReturnable<List<ItemStack>> cir
	) {
		if (!(breaker instanceof ServerPlayer player) || player.getAbilities().instabuild) {
			return;
		}

		List<ItemStack> drops = new ArrayList<>(cir.getReturnValue());
		boolean farming = SkillExperience.farming(state) > 0.0;
		if (farming || MiningExperience.isFortuneAffected(state)) {
			int fortune = (farming ? PlayerProgression.getFarmingFortune(player) : PlayerProgression.getMiningFortune(player))
				+ EnchantmentStatEffects.getFortuneBonus(player.getMainHandItem(), farming);
			applyFortune(drops, fortune, level);
			cir.setReturnValue(drops);
		}

		for (ItemStack drop : drops) {
			PlayerProgression.addCollectionItems(player, drop);
		}
	}

	private static void applyFortune(List<ItemStack> drops, int fortune, ServerLevel level) {
		if (fortune <= 0) {
			return;
		}

		List<ItemStack> bonusDrops = new ArrayList<>();
		for (ItemStack drop : drops) {
			int bonusCount = drop.getCount() * fortune / 100;
			if (level.getRandom().nextInt(100) < drop.getCount() * fortune % 100) {
				bonusCount++;
			}
			while (bonusCount > 0) {
				ItemStack bonus = drop.copy();
				int count = Math.min(bonusCount, bonus.getMaxStackSize());
				bonus.setCount(count);
				bonusDrops.add(bonus);
				bonusCount -= count;
			}
		}
		drops.addAll(bonusDrops);
	}
}
