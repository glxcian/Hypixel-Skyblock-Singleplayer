package com.glxcian.shf.stat;

import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.state.BlockState;

public final class MiningExperience {
	private static final Map<String, Double> XP_BY_BLOCK = Map.ofEntries(
		Map.entry("minecraft:stone", 1.0),
		Map.entry("minecraft:cobblestone", 1.0),
		Map.entry("minecraft:ice", 0.2),
		Map.entry("minecraft:packed_ice", 0.2),
		Map.entry("minecraft:blue_ice", 0.2),
		Map.entry("minecraft:netherrack", 0.5),
		Map.entry("minecraft:sand", 3.0),
		Map.entry("minecraft:red_sand", 3.0),
		Map.entry("minecraft:gravel", 4.0),
		Map.entry("minecraft:nether_quartz_ore", 5.0),
		Map.entry("minecraft:nether_gold_ore", 6.0),
		Map.entry("minecraft:end_stone", 3.0),
		Map.entry("minecraft:coal_ore", 5.0),
		Map.entry("minecraft:deepslate_coal_ore", 5.0),
		Map.entry("minecraft:iron_ore", 5.0),
		Map.entry("minecraft:deepslate_iron_ore", 5.0),
		Map.entry("minecraft:gold_ore", 6.0),
		Map.entry("minecraft:deepslate_gold_ore", 6.0),
		Map.entry("minecraft:lapis_ore", 7.0),
		Map.entry("minecraft:deepslate_lapis_ore", 7.0),
		Map.entry("minecraft:redstone_ore", 7.0),
		Map.entry("minecraft:deepslate_redstone_ore", 7.0),
		Map.entry("minecraft:emerald_ore", 9.0),
		Map.entry("minecraft:deepslate_emerald_ore", 9.0),
		Map.entry("minecraft:diamond_ore", 10.0),
		Map.entry("minecraft:deepslate_diamond_ore", 10.0),
		Map.entry("minecraft:glowstone", 7.0),
		Map.entry("minecraft:obsidian", 20.0)
	);

	private MiningExperience() {
	}

	public static double get(BlockState state) {
		String blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
		return XP_BY_BLOCK.getOrDefault(blockId, 0.0);
	}

	public static boolean isFortuneAffected(BlockState state) {
		String path = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
		return path.endsWith("_ore") || path.equals("glowstone") || path.equals("gravel");
	}
}
