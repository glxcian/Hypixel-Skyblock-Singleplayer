package com.glxcian.shf.stat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.glxcian.shf.SingleplayerHSFeatures;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import net.fabricmc.loader.api.FabricLoader;

public final class ArmorDefenseConfig {
	private static final Pattern RESOURCE_ID = Pattern.compile("[a-z0-9_.-]+:[a-z0-9/._-]+");
	private static final String DEFAULT_CONFIG = """
		{
		  "items": []
		}
		""";

	private static final Path CONFIG_PATH = FabricLoader.getInstance()
		.getConfigDir()
		.resolve(SingleplayerHSFeatures.MOD_ID)
		.resolve("armor_defense.json");

	private ArmorDefenseConfig() {
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
			SingleplayerHSFeatures.LOGGER.info("Loaded custom armor Defense from {}", CONFIG_PATH);
		} catch (IOException | RuntimeException exception) {
			throw new IllegalStateException("Could not load armor Defense from " + CONFIG_PATH, exception);
		}
	}

	private static void loadItems(JsonArray items) {
		for (int index = 0; index < items.size(); index++) {
			JsonElement element = items.get(index);
			if (!element.isJsonObject()) {
				throw new IllegalArgumentException("Armor mapping " + index + " must be a JSON object.");
			}

			JsonObject item = element.getAsJsonObject();
			JsonElement idElement = item.get("item");
			JsonElement defenseElement = item.get("defense");
			if (idElement == null || !idElement.isJsonPrimitive() || !idElement.getAsJsonPrimitive().isString()) {
				throw new IllegalArgumentException("Armor mapping " + index + " requires a string item ID.");
			}
			if (defenseElement == null || !defenseElement.isJsonPrimitive() || !defenseElement.getAsJsonPrimitive().isNumber()) {
				throw new IllegalArgumentException("Armor mapping " + index + " requires a numeric Defense value.");
			}

			String itemId = idElement.getAsString();
			int defense;
			try {
				defense = defenseElement.getAsBigDecimal().intValueExact();
			} catch (NumberFormatException exception) {
				throw new IllegalArgumentException("Armor mapping " + index + " Defense must be an integer.", exception);
			} catch (ArithmeticException exception) {
				throw new IllegalArgumentException("Armor mapping " + index + " Defense must be an integer.", exception);
			}
			if (!RESOURCE_ID.matcher(itemId).matches() || defense < 0) {
				throw new IllegalArgumentException("Armor mapping " + index + " has an invalid item ID or Defense value.");
			}

			DefenseStat.registerCustomDefense(itemId, defense);
		}
	}
}
