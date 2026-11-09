package com.glxcian.shf.stat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.glxcian.shf.SingleplayerHSFeatures;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

public final class MiningSpeedConfig {
	private static final Pattern RESOURCE_ID = Pattern.compile("[a-z0-9_.-]+:[a-z0-9/._-]+");
	public static final int BASE_PICKAXE_MINING_SPEED = 160;
	private static final Map<String, Integer> BASE_SPEED_BY_ITEM = Map.ofEntries(
		Map.entry("minecraft:wooden_pickaxe", 70),
		Map.entry("minecraft:stone_pickaxe", 110),
		Map.entry("minecraft:iron_pickaxe", 160),
		Map.entry("minecraft:golden_pickaxe", 250),
		Map.entry("minecraft:diamond_pickaxe", 220),
		Map.entry("minecraft:netherite_pickaxe", 250),
	);
	private static final Map<String, Integer> SPEED_BY_ITEM = new ConcurrentHashMap<>(BASE_SPEED_BY_ITEM);
	private static final String DEFAULT_CONFIG = """
		{
		  "items": []
		}
		""";

	private static final Path CONFIG_PATH = FabricLoader.getInstance()
		.getConfigDir()
		.resolve(SingleplayerHSFeatures.MOD_ID)
		.resolve("mining_speed.json");

	private MiningSpeedConfig() {
	}

	public static void load() {
		try {
			Files.createDirectories(CONFIG_PATH.getParent());
			if (Files.notExists(CONFIG_PATH)) {
				Files.writeString(CONFIG_PATH, DEFAULT_CONFIG);
			}

			JsonElement rootElement = JsonParser.parseString(Files.readString(CONFIG_PATH));
			if (!rootElement.isJsonObject()) {
				throw new IllegalArgumentException("The root value must be a JSON object.");
			}
			JsonElement itemsElement = rootElement.getAsJsonObject().get("items");
			if (itemsElement == null || !itemsElement.isJsonArray()) {
				throw new IllegalArgumentException("The 'items' property must be a JSON array.");
			}

			loadItems(itemsElement.getAsJsonArray());
			SingleplayerHSFeatures.LOGGER.info("Loaded custom Mining Speed from {}", CONFIG_PATH);
		} catch (IOException | RuntimeException exception) {
			throw new IllegalStateException("Could not load Mining Speed from " + CONFIG_PATH, exception);
		}
	}

	public static int get(ItemStack stack) {
		if (com.glxcian.shf.item.CustomItems.isPromisingPickaxe(stack)) {
			return 50;
		}
		String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
		return SPEED_BY_ITEM.getOrDefault(itemId, getDefault(stack));
	}

	public static int getDefault(ItemStack stack) {
		if (com.glxcian.shf.item.CustomItems.isPromisingPickaxe(stack)) {
			return 50;
		}
		String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
		return BASE_SPEED_BY_ITEM.getOrDefault(
			itemId,
			itemId.endsWith("_pickaxe") ? BASE_PICKAXE_MINING_SPEED : 0
		);
	}

	public static boolean isEstimated(ItemStack stack) {
		return false;
	}

	private static void loadItems(JsonArray items) {
		for (int index = 0; index < items.size(); index++) {
			JsonElement element = items.get(index);
			if (!element.isJsonObject()) {
				throw new IllegalArgumentException("Mining Speed mapping " + index + " must be a JSON object.");
			}

			JsonObject item = element.getAsJsonObject();
			JsonElement idElement = item.get("item");
			JsonElement speedElement = item.get("mining_speed");
			if (idElement == null || !idElement.isJsonPrimitive() || !idElement.getAsJsonPrimitive().isString()) {
				throw new IllegalArgumentException("Mining Speed mapping " + index + " requires a string item ID.");
			}
			if (speedElement == null || !speedElement.isJsonPrimitive() || !speedElement.getAsJsonPrimitive().isNumber()) {
				throw new IllegalArgumentException("Mining Speed mapping " + index + " requires a numeric value.");
			}

			String itemId = idElement.getAsString();
			int speed;
			try {
				speed = speedElement.getAsBigDecimal().intValueExact();
			} catch (NumberFormatException | ArithmeticException exception) {
				throw new IllegalArgumentException("Mining Speed mapping " + index + " must be an integer.", exception);
			}
			if (!RESOURCE_ID.matcher(itemId).matches() || speed < 0) {
				throw new IllegalArgumentException("Mining Speed mapping " + index + " has an invalid item ID or value.");
			}

			SPEED_BY_ITEM.put(itemId, speed);
		}
	}
}
