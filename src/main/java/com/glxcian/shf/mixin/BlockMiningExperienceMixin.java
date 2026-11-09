package com.glxcian.shf.mixin;

import com.glxcian.shf.stat.MiningExperience;
import com.glxcian.shf.stat.PlayerProgression;
import com.glxcian.shf.stat.PromisingPickaxeProgress;
import com.glxcian.shf.stat.SkillExperience;
import com.glxcian.shf.item.CustomItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class BlockMiningExperienceMixin {
	@Inject(
		method = "playerDestroy(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/item/ItemStack;)V",
		at = @At("TAIL")
	)
	private void singleplayerhsfeatures$awardMiningExperience(
		ServerLevel level,
		ServerPlayer player,
		BlockPos position,
		BlockState state,
		BlockEntity blockEntity,
		ItemStack tool,
		CallbackInfo ci
	) {
		if (player.getAbilities().instabuild) {
			return;
		}

		double experience = MiningExperience.get(state);
		if (experience > 0.0) {
			PlayerProgression.addExperience(player, PlayerProgression.Skill.MINING, experience);
		}
		double farmingExperience = SkillExperience.farming(state);
		if (farmingExperience > 0.0) {
			PlayerProgression.addExperience(player, PlayerProgression.Skill.FARMING, farmingExperience);
		}
		double foragingExperience = SkillExperience.foraging(state);
		if (foragingExperience > 0.0) {
			PlayerProgression.addExperience(player, PlayerProgression.Skill.FORAGING, foragingExperience);
		}
		if (tool.is(CustomItems.PROMISING_PICKAXE)) {
			PromisingPickaxeProgress.recordMinedBlock(player, player.getMainHandItem());
		}
	}
}
