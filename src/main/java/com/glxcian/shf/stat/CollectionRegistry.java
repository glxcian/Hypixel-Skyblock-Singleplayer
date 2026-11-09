package com.glxcian.shf.stat;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import com.glxcian.shf.item.CustomItems;

public final class CollectionRegistry {
	private static final Map<PlayerProgression.CollectionType, List<String>> COLLECTION_IDS = Map.of(
		PlayerProgression.CollectionType.MINING, List.of(
			"minecraft:cobblestone", "minecraft:coal", "minecraft:raw_iron", "minecraft:iron_ingot",
			"minecraft:raw_copper", "minecraft:copper_ingot", "minecraft:raw_gold", "minecraft:gold_ingot",
			"minecraft:diamond", "minecraft:emerald", "minecraft:lapis_lazuli", "minecraft:redstone",
			"minecraft:quartz", "minecraft:amethyst_shard", "minecraft:obsidian", "minecraft:glowstone_dust",
			"minecraft:gravel", "minecraft:flint", "minecraft:ice", "minecraft:packed_ice",
			"minecraft:blue_ice", "minecraft:netherrack", "minecraft:ancient_debris",
			"singleplayerhsfeatures:glacite", "singleplayerhsfeatures:tungsten", "singleplayerhsfeatures:umber",
			"minecraft:netherite_scrap", "minecraft:clay_ball"
		),
		PlayerProgression.CollectionType.FORAGING, List.of(
			"minecraft:oak_log", "minecraft:spruce_log", "minecraft:birch_log", "minecraft:jungle_log",
			"minecraft:acacia_log", "minecraft:dark_oak_log", "minecraft:mangrove_log", "minecraft:cherry_log",
			"minecraft:crimson_stem", "minecraft:warped_stem"
		),
		PlayerProgression.CollectionType.COMBAT, List.of(
			"minecraft:bone", "minecraft:rotten_flesh", "minecraft:string", "minecraft:spider_eye",
			"minecraft:gunpowder", "minecraft:ender_pearl", "minecraft:slime_ball", "minecraft:magma_cream",
			"minecraft:blaze_rod", "minecraft:ghast_tear", "minecraft:phantom_membrane",
			"minecraft:shulker_shell", "minecraft:wither_skeleton_skull"
		),
		PlayerProgression.CollectionType.FARMING, List.of(
			"minecraft:wheat", "minecraft:wheat_seeds", "minecraft:carrot", "minecraft:potato",
			"minecraft:beetroot", "minecraft:beetroot_seeds", "minecraft:pumpkin", "minecraft:melon_slice",
			"minecraft:sugar_cane", "minecraft:cactus", "minecraft:cocoa_beans",
			"minecraft:brown_mushroom", "minecraft:red_mushroom", "minecraft:nether_wart",
			"minecraft:apple", "minecraft:sweet_berries", "minecraft:glow_berries",
			"minecraft:beef", "minecraft:porkchop", "minecraft:chicken", "minecraft:mutton",
			"minecraft:rabbit", "minecraft:leather", "minecraft:feather", "minecraft:rabbit_hide",
			"minecraft:rabbit_foot", "minecraft:white_wool"
		),
		PlayerProgression.CollectionType.FISHING, List.of(
			"minecraft:cod", "minecraft:salmon", "minecraft:tropical_fish", "minecraft:pufferfish",
			"minecraft:ink_sac", "minecraft:glow_ink_sac", "minecraft:prismarine_shard",
			"minecraft:prismarine_crystals", "minecraft:nautilus_shell", "minecraft:sponge",
			"minecraft:lily_pad", "minecraft:sea_pickle"
		)
	);

	private static Map<String, PlayerProgression.CollectionType> typeById = Map.of();
	private static Map<PlayerProgression.CollectionType, List<Item>> itemsByType = Map.of();
	private static List<Item> allItems = List.of();
	private static Map<Item, String> itemIdsByItem = Map.of();
	private static boolean initialized;

	private CollectionRegistry() {
	}

	public static synchronized void initialize() {
		if (initialized) {
			return;
		}

		Map<String, PlayerProgression.CollectionType> resolvedTypes = new LinkedHashMap<>();
		Map<PlayerProgression.CollectionType, List<Item>> resolvedByType =
			new EnumMap<>(PlayerProgression.CollectionType.class);
		Map<Item, String> resolvedItemIds = new IdentityHashMap<>();
		List<Item> resolvedAll = new ArrayList<>();
		for (PlayerProgression.CollectionType type : PlayerProgression.CollectionType.values()) {
			List<Item> categoryItems = new ArrayList<>();
			for (String itemId : COLLECTION_IDS.get(type)) {
				Item item = resolveItem(itemId);
				if (resolvedTypes.putIfAbsent(itemId, type) != null) {
					throw new IllegalStateException("Collection item is assigned to multiple categories: " + itemId);
				}
				String previousId = resolvedItemIds.putIfAbsent(item, itemId);
				if (previousId != null) {
					throw new IllegalStateException("Collection IDs " + previousId + " and " + itemId
						+ " use the same vanilla base item.");
				}
				categoryItems.add(item);
				resolvedAll.add(item);
			}
			resolvedByType.put(type, List.copyOf(categoryItems));
		}

		typeById = Map.copyOf(resolvedTypes);
		itemsByType = Map.copyOf(resolvedByType);
		allItems = List.copyOf(resolvedAll);
		itemIdsByItem = Map.copyOf(resolvedItemIds);
		initialized = true;
	}

	public static List<Item> getItems() {
		initialize();
		return allItems;
	}

	public static List<Item> getItems(PlayerProgression.CollectionType type) {
		initialize();
		return type == null ? allItems : itemsByType.get(type);
	}

	public static PlayerProgression.CollectionType getType(Item item) {
		initialize();
		return typeById.get(getItemId(item));
	}

	public static PlayerProgression.CollectionType getType(ItemStack stack) {
		return !stack.isEmpty() ? getType(getItemId(stack)) : null;
	}

	public static PlayerProgression.CollectionType getType(String itemId) {
		initialize();
		return typeById.get(itemId);
	}

	public static String getItemId(Item item) {
		initialize();
		return itemIdsByItem.getOrDefault(item, BuiltInRegistries.ITEM.getKey(item).toString());
	}

	public static String getItemId(ItemStack stack) {
		if (stack.isEmpty()) {
			return "";
		}
		String customId = CustomItems.getCustomItemId(stack);
		return customId != null && typeById.containsKey(customId)
			? customId
			: getItemId(stack.getItem());
	}

	public static ItemStack createStack(Item item) {
		return createStack(getItemId(item));
	}

	public static ItemStack createStack(String itemId) {
		if (itemId.startsWith("singleplayerhsfeatures:")) {
			return CustomItems.createMiningResource(itemId.substring("singleplayerhsfeatures:".length()));
		}
		Identifier identifier = Identifier.parse(itemId);
		Item item = BuiltInRegistries.ITEM.get(identifier)
			.orElseThrow(() -> new IllegalStateException("Unknown collection item: " + itemId)).value();
		return item.getDefaultInstance();
	}

	public static boolean isCollectionItem(ItemStack stack) {
		return !stack.isEmpty() && getType(stack) != null;
	}

	private static Item resolveItem(String itemId) {
		if (itemId.startsWith("singleplayerhsfeatures:")) {
			Item item = CustomItems.getMiningResourceBaseItem(itemId);
			if (item == null) {
				throw new IllegalStateException("Unknown custom collection item: " + itemId);
			}
			return item;
		}
		Identifier identifier = Identifier.parse(itemId);
		return BuiltInRegistries.ITEM.get(identifier)
			.orElseThrow(() -> new IllegalStateException("Unknown collection item: " + itemId)).value();
	}

	public static int getAvailableCount(PlayerProgression.CollectionType type) {
		return getItems(type).size();
	}

	public static int getUnlockedCount(Player player, PlayerProgression.CollectionType type) {
		int unlocked = 0;
		for (Item item : getItems(type)) {
			String itemId = getItemId(item);
			PlayerProgression.CollectionType itemType = type == null ? getType(item) : type;
			if (CollectionProgression.getLevel(
				itemId, PlayerProgression.getCollectionCount(player, itemId, itemType)
			) > 0) {
				unlocked++;
			}
		}
		return unlocked;
	}
}
