package com.glxcian.shf.stat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.glxcian.shf.SingleplayerHSFeatures;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class MobProfileConfig {
	private static final Pattern RESOURCE_ID = Pattern.compile("[a-z0-9_.-]+:[a-z0-9/._-]+");
	private static final int DEFAULT_COMBAT_XP = 5;
	private static final String DEFAULT_CONFIG = """
		{
		  "profiles": [
		    {
		      "entity": "minecraft:zombie",
		      "dimension": "minecraft:overworld",
		      "biomes": [],
		      "level": 1,
		      "health": 100,
		      "combat_xp": 5,
		      "source": "https://hypixelskyblock.minecraft.wiki/w/Zombie"
		    },
		    {
		      "entity": "minecraft:skeleton",
		      "dimension": "minecraft:overworld",
		      "biomes": [],
		      "level": 1,
		      "health": 100,
		      "combat_xp": 5,
		      "source": "https://hypixelskyblock.minecraft.wiki/w/Skeleton"
		    },
		    {
		      "entity": "minecraft:spider",
		      "dimension": "minecraft:overworld",
		      "biomes": [],
		      "level": 3,
		      "health": 120,
		      "combat_xp": 5,
		      "source": "https://hypixelskyblock.minecraft.wiki/w/Spider"
		    }
		  ]
		}
		""";

	private static final Path CONFIG_PATH = FabricLoader.getInstance()
		.getConfigDir()
		.resolve(SingleplayerHSFeatures.MOD_ID)
		.resolve("mob_profiles.json");
	private static final Method RESOURCE_KEY_IDENTIFIER = findResourceKeyIdentifier();
	private static List<MobProfile> profiles = List.of();

	private MobProfileConfig() {
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

			JsonElement profilesElement = rootElement.getAsJsonObject().get("profiles");
			if (profilesElement == null || !profilesElement.isJsonArray()) {
				throw new IllegalArgumentException("The 'profiles' property must be a JSON array.");
			}

			profiles = parseProfiles(profilesElement.getAsJsonArray());
			SingleplayerHSFeatures.LOGGER.info("Loaded {} mob profiles from {}", profiles.size(), CONFIG_PATH);
		} catch (IOException | RuntimeException exception) {
			throw new IllegalStateException("Could not load mob profiles from " + CONFIG_PATH, exception);
		}
	}

	public static void applyTo(Mob mob) {
		if (!(mob.level() instanceof ServerLevel level)) {
			return;
		}

		BlockPos position = mob.blockPosition();
		String entityId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
		String dimensionId = getResourceKeyId(level.dimension());
		String biomeId = level.getBiome(position)
			.unwrapKey()
			.map(MobProfileConfig::getResourceKeyId)
			.orElse("");

		MobProfile profile = findProfile(entityId, dimensionId, biomeId, position.getY());
		if (profile == null) {
			return;
		}

		MobLevel.set(mob, profile.level());
		HealthStat.setMaximum(mob, profile.health());
		HealthStat.setCurrent(mob, profile.health());
		applyAttackDamage(mob, profile.attackDamage());
	}

	public static int combatExperience(Mob mob) {
		if (!(mob.level() instanceof ServerLevel level)) {
			return 0;
		}

		BlockPos position = mob.blockPosition();
		String entityId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
		String dimensionId = getResourceKeyId(level.dimension());
		String biomeId = level.getBiome(position)
			.unwrapKey()
			.map(MobProfileConfig::getResourceKeyId)
			.orElse("");
		MobProfile profile = findProfile(entityId, dimensionId, biomeId, position.getY());
		return profile == null || profile.combatXp() == null || profile.combatXp() == 0
			? DEFAULT_COMBAT_XP
			: profile.combatXp();
	}

	private static MobProfile findProfile(String entityId, String dimensionId, String biomeId, int y) {
		for (MobProfile profile : profiles) {
			if (profile.matches(entityId, dimensionId, biomeId, y)) {
				return profile;
			}
		}
		return null;
	}

	private static String getResourceKeyId(ResourceKey<?> key) {
		try {
			return RESOURCE_KEY_IDENTIFIER.invoke(key).toString();
		} catch (IllegalAccessException | InvocationTargetException exception) {
			throw new IllegalStateException("Could not read the identifier for " + key, exception);
		}
	}

	private static Method findResourceKeyIdentifier() {
		for (String methodName : List.of("identifier", "location")) {
			try {
				return ResourceKey.class.getMethod(methodName);
			} catch (NoSuchMethodException ignored) {
				continue;
			}
		}
		throw new IllegalStateException("No compatible resource-key identifier method exists for this Minecraft version.");
	}

	private static void applyAttackDamage(Mob mob, Float attackDamage) {
		if (attackDamage == null) {
			return;
		}

		AttributeInstance attribute = mob.getAttribute(Attributes.ATTACK_DAMAGE);
		if (attribute != null) {
			attribute.setBaseValue(attackDamage / 5.0F);
		}
	}

	private static List<MobProfile> parseProfiles(JsonArray profileArray) {
		List<MobProfile> parsed = new ArrayList<>();
		for (int index = 0; index < profileArray.size(); index++) {
			JsonElement element = profileArray.get(index);
			if (!element.isJsonObject()) {
				throw new IllegalArgumentException("Profile " + index + " must be a JSON object.");
			}

			JsonObject object = element.getAsJsonObject();
			String entity = requiredResourceId(object, "entity", index);
			String dimension = optionalResourceId(object, "dimension", index);
			Set<String> biomes = parseResourceIdSet(object, "biomes", index);
			Integer minY = optionalInteger(object, "min_y", index);
			Integer maxY = optionalInteger(object, "max_y", index);
			int level = requiredInteger(object, "level", index);
			float health = requiredFloat(object, "health", index);
			Float attackDamage = optionalFloat(object, "attack_damage", index);
			Integer combatXp = optionalInteger(object, "combat_xp", index);

			if (level < 1) {
				throw new IllegalArgumentException("Profile " + index + " has a level below 1.");
			}
			if (!Float.isFinite(health) || health <= 0.0F) {
				throw new IllegalArgumentException("Profile " + index + " health must be finite and positive.");
			}
			if (attackDamage != null && (!Float.isFinite(attackDamage) || attackDamage < 0.0F)) {
				throw new IllegalArgumentException("Profile " + index + " attack_damage must be finite and non-negative.");
			}
			if (combatXp != null && combatXp < 0) {
				throw new IllegalArgumentException("Profile " + index + " combat_xp must be non-negative.");
			}
			if (minY != null && maxY != null && minY > maxY) {
				throw new IllegalArgumentException("Profile " + index + " min_y must not exceed max_y.");
			}

			parsed.add(new MobProfile(entity, dimension, biomes, minY, maxY, level, health, attackDamage, combatXp));
		}

		return List.copyOf(parsed);
	}

	private static String requiredResourceId(JsonObject object, String property, int profileIndex) {
		String value = requiredString(object, property, profileIndex);
		if (!RESOURCE_ID.matcher(value).matches()) {
			throw new IllegalArgumentException("Profile " + profileIndex + " has an invalid " + property + " resource ID.");
		}
		return value;
	}

	private static String optionalResourceId(JsonObject object, String property, int profileIndex) {
		if (!object.has(property) || object.get(property).isJsonNull()) {
			return null;
		}
		return requiredResourceId(object, property, profileIndex);
	}

	private static Set<String> parseResourceIdSet(JsonObject object, String property, int profileIndex) {
		Set<String> values = new HashSet<>();
		if (!object.has(property)) {
			return Set.of();
		}

		JsonElement element = object.get(property);
		if (!element.isJsonArray()) {
			throw new IllegalArgumentException("Profile " + profileIndex + " property " + property + " must be an array.");
		}
		for (JsonElement value : element.getAsJsonArray()) {
			if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()) {
				throw new IllegalArgumentException("Profile " + profileIndex + " property " + property + " must contain strings.");
			}

			String resourceId = value.getAsString();
			if (!RESOURCE_ID.matcher(resourceId).matches()) {
				throw new IllegalArgumentException("Profile " + profileIndex + " has an invalid " + property + " resource ID.");
			}
			values.add(resourceId);
		}
		return Set.copyOf(values);
	}

	private static String requiredString(JsonObject object, String property, int profileIndex) {
		JsonElement element = object.get(property);
		if (element == null || !element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
			throw new IllegalArgumentException("Profile " + profileIndex + " requires a string property '" + property + "'.");
		}
		return element.getAsString();
	}

	private static int requiredInteger(JsonObject object, String property, int profileIndex) {
		Integer value = optionalInteger(object, property, profileIndex);
		if (value == null) {
			throw new IllegalArgumentException("Profile " + profileIndex + " requires integer property '" + property + "'.");
		}
		return value;
	}

	private static Integer optionalInteger(JsonObject object, String property, int profileIndex) {
		if (!object.has(property) || object.get(property).isJsonNull()) {
			return null;
		}
		JsonElement element = object.get(property);
		try {
			if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) {
				throw new NumberFormatException();
			}
			return element.getAsBigDecimal().intValueExact();
		} catch (NumberFormatException exception) {
			throw new IllegalArgumentException("Profile " + profileIndex + " property '" + property + "' must be an integer.", exception);
		} catch (ArithmeticException exception) {
			throw new IllegalArgumentException("Profile " + profileIndex + " property '" + property + "' must be an integer.", exception);
		}
	}

	private static float requiredFloat(JsonObject object, String property, int profileIndex) {
		Float value = optionalFloat(object, property, profileIndex);
		if (value == null) {
			throw new IllegalArgumentException("Profile " + profileIndex + " requires numeric property '" + property + "'.");
		}
		return value;
	}

	private static Float optionalFloat(JsonObject object, String property, int profileIndex) {
		if (!object.has(property) || object.get(property).isJsonNull()) {
			return null;
		}
		JsonElement element = object.get(property);
		try {
			if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) {
				throw new NumberFormatException();
			}
			return element.getAsFloat();
		} catch (NumberFormatException exception) {
			throw new IllegalArgumentException("Profile " + profileIndex + " property '" + property + "' must be numeric.", exception);
		}
	}
}
