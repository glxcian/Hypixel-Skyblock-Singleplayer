package com.glxcian.shf.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EnchantedResourceCatalog {
	public static final List<Resource> RESOURCES = List.of(
		resource("enchanted_cobblestone", "Enchanted Cobblestone", "cobblestone", "minecraft:cobblestone", 160),
		resource("enchanted_coal", "Enchanted Coal", "coal", "minecraft:coal", 160),
		resource("enchanted_coal_block", "Enchanted Coal Block", "coal_block", "singleplayerhsfeatures:enchanted_coal", 160),
		resource("enchanted_iron_ingot", "Enchanted Iron Ingot", "iron_ingot", "minecraft:iron_ingot", 160),
		resource("enchanted_iron_block", "Enchanted Iron Block", "iron_block", "singleplayerhsfeatures:enchanted_iron_ingot", 160),
		resource("enchanted_gold_ingot", "Enchanted Gold Ingot", "gold_ingot", "minecraft:gold_ingot", 160),
		resource("enchanted_gold_block", "Enchanted Gold Block", "gold_block", "singleplayerhsfeatures:enchanted_gold_ingot", 160),
		resource("enchanted_diamond", "Enchanted Diamond", "diamond", "minecraft:diamond", 160),
		resource("enchanted_diamond_block", "Enchanted Diamond Block", "diamond_block", "singleplayerhsfeatures:enchanted_diamond", 160),
		resource("enchanted_emerald", "Enchanted Emerald", "emerald", "minecraft:emerald", 160),
		resource("enchanted_emerald_block", "Enchanted Emerald Block", "emerald_block", "singleplayerhsfeatures:enchanted_emerald", 160),
		resource("enchanted_lapis_lazuli", "Enchanted Lapis Lazuli", "lapis_lazuli", "minecraft:lapis_lazuli", 160),
		resource("enchanted_lapis_lazuli_block", "Enchanted Lapis Lazuli Block", "lapis_block", "singleplayerhsfeatures:enchanted_lapis_lazuli", 160),
		resource("enchanted_redstone_dust", "Enchanted Redstone Dust", "redstone", "minecraft:redstone", 160),
		resource("enchanted_redstone_block", "Enchanted Redstone Block", "redstone_block", "singleplayerhsfeatures:enchanted_redstone_dust", 160),
		resource("enchanted_nether_quartz", "Enchanted Nether Quartz", "quartz", "minecraft:quartz", 160),
		resource("enchanted_quartz_block", "Enchanted Quartz Block", "quartz_block", "singleplayerhsfeatures:enchanted_nether_quartz", 160),
		resource("enchanted_obsidian", "Enchanted Obsidian", "obsidian", "minecraft:obsidian", 160),
		resource("enchanted_glowstone_dust", "Enchanted Glowstone Dust", "glowstone_dust", "minecraft:glowstone_dust", 160),
		resource("enchanted_glowstone", "Enchanted Glowstone", "glowstone", "singleplayerhsfeatures:enchanted_glowstone_dust", 160),
		resource("enchanted_flint", "Enchanted Flint", "flint", "minecraft:flint", 160),
		resource("enchanted_ice", "Enchanted Ice", "ice", "minecraft:ice", 160),
		resource("enchanted_packed_ice", "Enchanted Packed Ice", "packed_ice", "singleplayerhsfeatures:enchanted_ice", 160),
		resource("enchanted_netherrack", "Enchanted Netherrack", "netherrack", "minecraft:netherrack", 160),
		resource("enchanted_clay_ball", "Enchanted Clay Ball", "clay_ball", "minecraft:clay_ball", 160),
		resource("enchanted_oak_log", "Enchanted Oak Log", "oak_log", "minecraft:oak_log", 160),
		resource("enchanted_spruce_log", "Enchanted Spruce Log", "spruce_log", "minecraft:spruce_log", 160),
		resource("enchanted_birch_log", "Enchanted Birch Log", "birch_log", "minecraft:birch_log", 160),
		resource("enchanted_jungle_log", "Enchanted Jungle Log", "jungle_log", "minecraft:jungle_log", 160),
		resource("enchanted_acacia_log", "Enchanted Acacia Log", "acacia_log", "minecraft:acacia_log", 160),
		resource("enchanted_dark_oak_log", "Enchanted Dark Oak Log", "dark_oak_log", "minecraft:dark_oak_log", 160),
		resource("enchanted_bone", "Enchanted Bone", "bone", "minecraft:bone", 160),
		resource("enchanted_rotten_flesh", "Enchanted Rotten Flesh", "rotten_flesh", "minecraft:rotten_flesh", 160),
		resource("enchanted_string", "Enchanted String", "string", "minecraft:string", 160),
		resource("enchanted_spider_eye", "Enchanted Spider Eye", "spider_eye", "minecraft:spider_eye", 160),
		resource("enchanted_gunpowder", "Enchanted Gunpowder", "gunpowder", "minecraft:gunpowder", 160),
		resource("enchanted_ender_pearl", "Enchanted Ender Pearl", "ender_pearl", "minecraft:ender_pearl", 20),
		resource("enchanted_slimeball", "Enchanted Slimeball", "slime_ball", "minecraft:slime_ball", 160),
		resource("enchanted_magma_cream", "Enchanted Magma Cream", "magma_cream", "minecraft:magma_cream", 160),
		resource("enchanted_blaze_powder", "Enchanted Blaze Powder", "blaze_powder", "minecraft:blaze_rod", 160),
		resource("enchanted_blaze_rod", "Enchanted Blaze Rod", "blaze_rod", "singleplayerhsfeatures:enchanted_blaze_powder", 160),
		resource("enchanted_ghast_tear", "Enchanted Ghast Tear", "ghast_tear", "minecraft:ghast_tear", 5),
		resource("enchanted_wheat", "Enchanted Wheat", "wheat", "minecraft:wheat", 160),
		resource("enchanted_seeds", "Enchanted Seeds", "wheat_seeds", "minecraft:wheat_seeds", 160),
		resource("enchanted_carrot", "Enchanted Carrot", "carrot", "minecraft:carrot", 160),
		resource("enchanted_potato", "Enchanted Potato", "potato", "minecraft:potato", 160),
		resource("enchanted_pumpkin", "Enchanted Pumpkin", "pumpkin", "minecraft:pumpkin", 160),
		resource("enchanted_melon_slice", "Enchanted Melon Slice", "melon_slice", "minecraft:melon_slice", 160),
		resource("enchanted_melon", "Enchanted Melon", "melon", "singleplayerhsfeatures:enchanted_melon_slice", 160),
		resource("enchanted_sugar", "Enchanted Sugar", "sugar", "minecraft:sugar_cane", 160),
		resource("enchanted_sugar_cane", "Enchanted Sugar Cane", "sugar_cane", "singleplayerhsfeatures:enchanted_sugar", 160),
		resource("enchanted_cactus_green", "Enchanted Cactus Green", "green_dye", "minecraft:cactus", 160),
		resource("enchanted_cactus", "Enchanted Cactus", "cactus", "singleplayerhsfeatures:enchanted_cactus_green", 160),
		resource("enchanted_cocoa_beans", "Enchanted Cocoa Beans", "cocoa_beans", "minecraft:cocoa_beans", 160),
		resource("enchanted_brown_mushroom", "Enchanted Brown Mushroom", "brown_mushroom", "minecraft:brown_mushroom", 160),
		resource("enchanted_red_mushroom", "Enchanted Red Mushroom", "red_mushroom", "minecraft:red_mushroom", 160),
		resource("enchanted_nether_wart", "Enchanted Nether Wart", "nether_wart", "minecraft:nether_wart", 160),
		resource("enchanted_raw_beef", "Enchanted Raw Beef", "beef", "minecraft:beef", 160),
		resource("enchanted_raw_porkchop", "Enchanted Raw Porkchop", "porkchop", "minecraft:porkchop", 160),
		resource("enchanted_raw_chicken", "Enchanted Raw Chicken", "chicken", "minecraft:chicken", 160),
		resource("enchanted_raw_mutton", "Enchanted Raw Mutton", "mutton", "minecraft:mutton", 160),
		resource("enchanted_raw_rabbit", "Enchanted Raw Rabbit", "rabbit", "minecraft:rabbit", 160),
		resource("enchanted_leather", "Enchanted Leather", "leather", "minecraft:leather", 160),
		resource("enchanted_feather", "Enchanted Feather", "feather", "minecraft:feather", 160),
		resource("enchanted_rabbit_hide", "Enchanted Rabbit Hide", "rabbit_hide", "minecraft:rabbit_hide", 160),
		resource("enchanted_rabbit_foot", "Enchanted Rabbit Foot", "rabbit_foot", "minecraft:rabbit_foot", 160),
		resource("enchanted_raw_cod", "Enchanted Raw Cod", "cod", "minecraft:cod", 160),
		resource("enchanted_raw_salmon", "Enchanted Raw Salmon", "salmon", "minecraft:salmon", 160),
		resource("enchanted_tropical_fish", "Enchanted Tropical Fish", "tropical_fish", "minecraft:tropical_fish", 160),
		resource("enchanted_pufferfish", "Enchanted Pufferfish", "pufferfish", "minecraft:pufferfish", 160),
		resource("enchanted_ink_sac", "Enchanted Ink Sac", "ink_sac", "minecraft:ink_sac", 80),
		resource("enchanted_prismarine_shard", "Enchanted Prismarine Shard", "prismarine_shard", "minecraft:prismarine_shard", 80),
		resource("enchanted_prismarine_crystals", "Enchanted Prismarine Crystals", "prismarine_crystals", "minecraft:prismarine_crystals", 80),
		resource("enchanted_sponge", "Enchanted Sponge", "sponge", "minecraft:sponge", 40),
		resource("enchanted_lily_pad", "Enchanted Lily Pad", "lily_pad", "minecraft:lily_pad", 160)
	);

	private static final Map<String, Resource> BY_PATH;
	private static final Map<String, Resource> BY_INPUT_ID;

	static {
		Map<String, Resource> byPath = new HashMap<>();
		Map<String, Resource> byInput = new HashMap<>();
		for (Resource resource : RESOURCES) {
			if (byPath.putIfAbsent(resource.path(), resource) != null) {
				throw new IllegalStateException("Duplicate enchanted resource item: " + resource.path());
			}
			if (byInput.putIfAbsent(resource.inputId(), resource) != null) {
				throw new IllegalStateException("Duplicate enchanted resource input: " + resource.inputId());
			}
		}
		BY_PATH = Map.copyOf(byPath);
		BY_INPUT_ID = Map.copyOf(byInput);
	}

	private EnchantedResourceCatalog() {
	}

	public static Resource getByPath(String path) {
		return BY_PATH.get(path);
	}

	public static Resource getByInputId(String itemId) {
		return BY_INPUT_ID.get(itemId);
	}

	private static Resource resource(String path, String name, String texture, String inputId, int amount) {
		return new Resource(path, name, "minecraft:item/" + texture, inputId, amount);
	}

	public record Resource(String path, String name, String model, String inputId, int amount) {
	}
}
