package com.glxcian.shf.item;

import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.CustomData;

/**
 * SkyBlock items are vanilla item stacks identified by custom data.
 * Legacy registry entries exist only long enough to migrate older world saves;
 * all newly created items use vanilla base items and vanilla textures.
 */
public final class CustomItems {
	private static final String ITEM_ID_KEY = "singleplayerhsfeatures:item_id";
	private static final String PROMISING_PICKAXE_MARKER = "singleplayerhsfeatures:promising_pickaxe";

	/** The Personal Compactor uses a vanilla dropper as its base item. */
	public static final Item COMPACTOR = Items.DROPPER;

	/** Unique vanilla base items for the mining collection resources. */
	public static final Item GLACITE = Items.SNOW_BLOCK;
	public static final Item TUNGSTEN = Items.IRON_NUGGET;
	public static final Item UMBER = Items.TUFF;

	/** Compatibility alias. Promising Pickaxes are marked vanilla iron pickaxe stacks. */
	public static final Item PROMISING_PICKAXE = Items.IRON_PICKAXE;

	private static final Map<String, MiningResource> MINING_RESOURCES = Map.of(
		"glacite", new MiningResource(GLACITE, "Glacite"),
		"tungsten", new MiningResource(TUNGSTEN, "Tungsten"),
		"umber", new MiningResource(UMBER, "Umber")
	);

	private CustomItems() {
	}

	/**
	 * Registers old item IDs only so existing worlds can deserialize them.
	 * New items are always created as vanilla stacks with custom data.
	 */
	public static void initializeLegacyItems() {
		registerLegacyItem("compactor", "Compactor", Rarity.UNCOMMON, 1);
		for (Map.Entry<String, MiningResource> entry : MINING_RESOURCES.entrySet()) {
			registerLegacyItem(entry.getKey(), entry.getValue().displayName(), Rarity.COMMON, 64);
		}
		for (EnchantedResourceCatalog.Resource resource : EnchantedResourceCatalog.RESOURCES) {
			registerLegacyItem(resource.path(), resource.name(), resource.rarity().vanillaRarity(), 64);
		}
	}

	/** Converts old registered custom items in the player's inventory to vanilla-backed stacks. */
	public static void migrateLegacyItems(ServerPlayer player) {
		Inventory inventory = player.getInventory();
		for (int slot = 0; slot < 36; slot++) {
			ItemStack oldStack = inventory.getItem(slot);
			if (oldStack.isEmpty()) {
				continue;
			}
			Identifier key = BuiltInRegistries.ITEM.getKey(oldStack.getItem());
			if (!key.getNamespace().equals("singleplayerhsfeatures")) {
				continue;
			}
			String path = key.getPath();
			ItemStack replacement = switch (path) {
				case "compactor" -> createPersonalCompactor();
				case "glacite", "tungsten", "umber" -> createMiningResource(path);
			default -> EnchantedResourceCatalog.getByPath(path) == null
				? ItemStack.EMPTY
				: createEnchantedResource(path);
			};
			if (replacement.isEmpty()) {
				continue;
			}
			replacement.setCount(oldStack.getCount());
			replacement.set(DataComponents.MAX_STACK_SIZE,
				Math.max(oldStack.getMaxStackSize(), replacement.getMaxStackSize()));
			inventory.setItem(slot, replacement);
		}
	}

	private static void registerLegacyItem(String path, String name, Rarity rarity, int maxStackSize) {
		ResourceKey<Item> key = ResourceKey.create(
			Registries.ITEM,
			Identifier.fromNamespaceAndPath("singleplayerhsfeatures", path)
		);
		Registry.register(
			BuiltInRegistries.ITEM,
			key,
			new Item(new Item.Properties()
				.setId(key)
				.stacksTo(maxStackSize)
				.rarity(rarity)
				.component(DataComponents.ITEM_NAME, Component.literal(name)))
		);
	}

	public static ItemStack createPromisingPickaxe() {
		ItemStack stack = new ItemStack(Items.IRON_PICKAXE);
		CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
			tag.putBoolean(PROMISING_PICKAXE_MARKER, true);
			tag.putString(ITEM_ID_KEY, "singleplayerhsfeatures:promising_pickaxe");
		});
		stack.set(DataComponents.CUSTOM_NAME, Component.literal("Promising Pickaxe"));
		stack.set(DataComponents.RARITY, Rarity.UNCOMMON);
		return stack;
	}

	public static boolean isPromisingPickaxe(ItemStack stack) {
		if (stack.isEmpty() || !stack.is(Items.IRON_PICKAXE)) {
			return false;
		}
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data != null && data.copyTag().getBoolean(PROMISING_PICKAXE_MARKER).orElse(false);
	}

	public static ItemStack createPersonalCompactor() {
		ItemStack stack = createCustomStack(
			"singleplayerhsfeatures:personal_compactor",
			Items.DROPPER,
			"Personal Compactor",
			SkyBlockRarity.RARE,
			true
		);
		stack.set(DataComponents.MAX_STACK_SIZE, 1);
		return stack;
	}

	public static boolean isPersonalCompactor(ItemStack stack) {
		return isCustomItem(stack, "singleplayerhsfeatures:personal_compactor");
	}

	public static ItemStack createMiningResource(String path) {
		MiningResource resource = MINING_RESOURCES.get(path);
		if (resource == null) {
			throw new IllegalArgumentException("Unknown mining resource: " + path);
		}
		return createCustomStack(
			"singleplayerhsfeatures:" + path,
			resource.baseItem(),
			resource.displayName(),
			SkyBlockRarity.COMMON,
			false
		);
	}

	public static ItemStack createEnchantedResource(String path) {
		EnchantedResourceCatalog.Resource resource = EnchantedResourceCatalog.getByPath(path);
		if (resource == null) {
			throw new IllegalArgumentException("Unknown enchanted resource: " + path);
		}
		String model = resource.model();
		String itemPath = model.substring("minecraft:item/".length());
		Item baseItem = BuiltInRegistries.ITEM.get(
			Identifier.fromNamespaceAndPath("minecraft", itemPath)
		).orElseThrow(() -> new IllegalStateException("Missing vanilla base item for " + path)).value();
		return createCustomStack(
			"singleplayerhsfeatures:" + path,
			baseItem,
			resource.name(),
			resource.rarity(),
			true
		);
	}

	/** Returns the vanilla item used to render a mining collection resource. */
	public static Item getMiningResourceBaseItem(String itemId) {
		String path = itemId.substring(itemId.indexOf(':') + 1);
		MiningResource resource = MINING_RESOURCES.get(path);
		return resource == null ? null : resource.baseItem();
	}

	public static String getCustomItemId(ItemStack stack) {
		if (stack.isEmpty()) {
			return null;
		}
		if (isPromisingPickaxe(stack)) {
			return "singleplayerhsfeatures:promising_pickaxe";
		}
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data == null ? null : data.copyTag().getString(ITEM_ID_KEY).orElse(null);
	}

	public static boolean isCustomItem(ItemStack stack, String itemId) {
		return itemId.equals(getCustomItemId(stack));
	}

	private static ItemStack createCustomStack(
		String itemId,
		Item baseItem,
		String displayName,
		SkyBlockRarity rarity,
		boolean glint
	) {
		ItemStack stack = new ItemStack(baseItem);
		CustomData.update(DataComponents.CUSTOM_DATA, stack,
			tag -> tag.putString(ITEM_ID_KEY, itemId));
		stack.set(DataComponents.CUSTOM_NAME, Component.literal(displayName));
		stack.set(DataComponents.RARITY, rarity.vanillaRarity());
		if (glint) {
			stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
		}
		return stack;
	}

	private record MiningResource(Item baseItem, String displayName) {
	}
}
