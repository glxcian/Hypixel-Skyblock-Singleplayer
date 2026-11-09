package com.glxcian.shf.stat;

import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class SkillExperience {
	private static final Map<String, Double> FARMING_XP_BY_BLOCK = Map.ofEntries(
		Map.entry("minecraft:pumpkin", 4.0),
		Map.entry("minecraft:melon", 4.0),
		Map.entry("minecraft:sugar_cane", 4.0),
		Map.entry("minecraft:cactus", 4.0),
		Map.entry("minecraft:nether_wart", 4.0),
		Map.entry("minecraft:brown_mushroom", 4.0),
		Map.entry("minecraft:red_mushroom", 4.0),
		Map.entry("minecraft:sweet_berry_bush", 4.0),
		Map.entry("minecraft:cocoa", 4.0)
	);

	private SkillExperience() {
	}

	public static double farming(BlockState state) {
		if (state.getBlock() instanceof CropBlock crop) {
			return crop.isMaxAge(state) ? 4.0 : 0.0;
		}
		if (state.getBlock() instanceof CocoaBlock) {
			return state.getValue(CocoaBlock.AGE) == CocoaBlock.MAX_AGE ? 4.0 : 0.0;
		}
		if (state.getBlock() instanceof SweetBerryBushBlock) {
			return state.getValue(SweetBerryBushBlock.AGE) == SweetBerryBushBlock.MAX_AGE ? 4.0 : 0.0;
		}
		return FARMING_XP_BY_BLOCK.getOrDefault(
			BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString(), 0.0
		);
	}

	public static double foraging(BlockState state) {
		return state.is(BlockTags.LOGS) ? 6.0 : 0.0;
	}
}
