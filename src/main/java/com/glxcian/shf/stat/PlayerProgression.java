package com.glxcian.shf.stat;

import java.util.Map;
import java.util.Collections;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

public final class PlayerProgression {
	private static final long[] CUMULATIVE_XP = {
		0L, 50L, 175L, 375L, 675L, 1_175L, 1_925L, 2_925L, 4_425L, 6_425L,
		9_925L, 14_925L, 22_425L, 32_425L, 47_425L, 67_425L, 97_425L, 147_425L,
		222_425L, 322_425L, 522_425L, 822_425L, 1_222_425L, 1_722_425L,
		2_322_425L, 3_022_425L, 3_822_425L, 4_722_425L, 5_722_425L, 6_822_425L,
		8_022_425L, 9_322_425L, 10_722_425L, 12_222_425L, 13_822_425L,
		15_522_425L, 17_322_425L, 19_222_425L, 21_222_425L, 23_322_425L,
		25_522_425L, 27_822_425L, 30_222_425L, 32_722_425L, 35_322_425L,
		38_072_425L, 40_972_425L, 44_072_425L, 47_472_425L, 51_172_425L,
		55_172_425L, 59_472_425L, 64_072_425L, 68_972_425L, 74_172_425L,
		79_672_425L, 85_472_425L, 91_572_425L, 97_972_425L, 104_672_425L,
		111_672_425L
	};
	private static final long XP_SCALE = 100L;

	private PlayerProgression() {
	}

	public static double getExperience(Player player, Skill skill) {
		PlayerProgressionAccess access = access(player);
		long scaledExperience = switch (skill) {
			case COMBAT -> access.singleplayerhsfeatures$getCombatExperience();
			case MINING -> access.singleplayerhsfeatures$getMiningExperience();
			case FARMING -> access.singleplayerhsfeatures$getFarmingExperience();
			case FORAGING -> access.singleplayerhsfeatures$getForagingExperience();
			case FISHING -> access.singleplayerhsfeatures$getFishingExperience();
			case ENCHANTING -> access.singleplayerhsfeatures$getEnchantingExperience();
			case ALCHEMY -> access.singleplayerhsfeatures$getAlchemyExperience();
			case TAMING -> access.singleplayerhsfeatures$getTamingExperience();
		};
		return scaledExperience / (double) XP_SCALE;
	}

	public static int getLevel(Player player, Skill skill) {
		return getLevel(getExperience(player, skill));
	}

	public static int getLevel(double experience) {
		int level = 0;
		for (int nextLevel = 1; nextLevel < CUMULATIVE_XP.length; nextLevel++) {
			if (experience < CUMULATIVE_XP[nextLevel]) {
				break;
			}
			level = nextLevel;
		}
		return level;
	}

	public static double getExperienceToNextLevel(Player player, Skill skill) {
		double experience = getExperience(player, skill);
		int level = getLevel(experience);
		if (level >= 60) {
			return 0;
		}
		return CUMULATIVE_XP[level + 1] - experience;
	}

	public static long getExperienceWithinLevel(Player player, Skill skill) {
		int level = getLevel(player, skill);
		double experience = getExperience(player, skill);
		return level >= 60 ? 0L : Math.round(experience - CUMULATIVE_XP[level]);
	}

	public static long getExperienceForNextLevel(Player player, Skill skill) {
		int level = getLevel(player, skill);
		return level >= 60 ? 0 : CUMULATIVE_XP[level + 1] - CUMULATIVE_XP[level];
	}

	public static int getCombatDamageBonus(Player player) {
		int level = getLevel(player, Skill.COMBAT);
		return Math.min(level, 50) * 4 + Math.max(level - 50, 0);
	}

	public static double getCombatCriticalChance(Player player) {
		return getLevel(player, Skill.COMBAT) * 0.5;
	}

	public static int getMiningFortune(Player player) {
		int skillFortune = getLevel(player, Skill.MINING) * 4;
		int collectionFortune = CollectionProgression.getMiningFortune(
			"minecraft:obsidian", getCollectionCount(player, "minecraft:obsidian")
		);
		return skillFortune + collectionFortune;
	}

	public static int getFarmingFortune(Player player) {
		return getLevel(player, Skill.FARMING) * 4;
	}

	public static int getForagingStrength(Player player) {
		return getLevel(player, Skill.FORAGING);
	}

	public static int getMiningDefense(Player player) {
		int level = getLevel(player, Skill.MINING);
		return Math.min(level, 14) + Math.max(level - 14, 0) * 2;
	}

	public static long getSkyBlockExperience(Player player) {
		long experience = 0;
		Map<String, Long> collections = access(player).singleplayerhsfeatures$getCollections();
		for (Skill skill : Skill.values()) {
			int level = getLevel(player, skill);
			for (int reachedLevel = 1; reachedLevel <= level; reachedLevel++) {
				experience = Math.addExact(experience, getSkyBlockExperience(skill, reachedLevel));
			}
		}
		for (Item item : CollectionRegistry.getItems()) {
			String itemId = BuiltInRegistries.ITEM.getKey(item).toString();
			experience = Math.addExact(experience,
				CollectionProgression.getSkyBlockExperience(itemId, getCollectionCount(collections, itemId)));
		}
		return experience;
	}

	private static long getCollectionCount(Map<String, Long> collections, String itemId) {
		CollectionType type = CollectionRegistry.getType(itemId);
		return type == null ? 0L : collections.getOrDefault(type.keyPrefix + itemId, 0L);
	}

	public static int getSkyBlockLevel(Player player) {
		return (int) Math.min(Integer.MAX_VALUE, getSkyBlockExperience(player) / 100L);
	}

	public static int getSkyBlockStrength(Player player) {
		return getSkyBlockLevel(player) / 5 + getForagingStrength(player);
	}

	public static int getSkyBlockExperienceWithinLevel(Player player) {
		return (int) (getSkyBlockExperience(player) % 100L);
	}

	public static long getSkyBlockExperience(Skill skill, int level) {
		if (level <= 0 || level > 60) {
			return 0;
		}
		if (skill == Skill.MINING) {
			if (level <= 10) {
				return 5;
			}
			if (level <= 25) {
				return 10;
			}
			if (level <= 50) {
				return 20;
			}
			return 30;
		}
		return level <= 12 ? 5 : 10;
	}

	public static double getExperienceProgress(Player player, Skill skill) {
		int level = getLevel(player, skill);
		if (level >= 60) {
			return 1.0;
		}

		double experience = getExperience(player, skill);
		double currentLevelStart = CUMULATIVE_XP[level];
		double nextLevelStart = CUMULATIVE_XP[level + 1];
		return (experience - currentLevelStart) / (nextLevelStart - currentLevelStart);
	}

	public static void addExperience(Player player, Skill skill, double amount) {
		requireServerSide(player);
		if (!Double.isFinite(amount) || amount < 0.0) {
			throw new IllegalArgumentException("Skill experience must be finite and non-negative.");
		}

		int currentGlobalLevel = getSkyBlockLevel(player);
		PlayerProgressionAccess access = access(player);
		long currentScaled = Math.round(getExperience(player, skill) * XP_SCALE);
		long addedScaled = Math.round(amount * XP_SCALE);
		long updatedScaled = Math.min(CUMULATIVE_XP[60] * XP_SCALE, Math.addExact(currentScaled, addedScaled));
		switch (skill) {
			case COMBAT -> access.singleplayerhsfeatures$setCombatExperience(updatedScaled);
			case MINING -> access.singleplayerhsfeatures$setMiningExperience(updatedScaled);
			case FARMING -> access.singleplayerhsfeatures$setFarmingExperience(updatedScaled);
			case FORAGING -> access.singleplayerhsfeatures$setForagingExperience(updatedScaled);
			case FISHING -> access.singleplayerhsfeatures$setFishingExperience(updatedScaled);
			case ENCHANTING -> access.singleplayerhsfeatures$setEnchantingExperience(updatedScaled);
			case ALCHEMY -> access.singleplayerhsfeatures$setAlchemyExperience(updatedScaled);
			case TAMING -> access.singleplayerhsfeatures$setTamingExperience(updatedScaled);
		}
		if (player instanceof ServerPlayer serverPlayer
			&& getSkyBlockLevel(serverPlayer) != currentGlobalLevel) {
			HealthStat.updateSkyBlockLevelBonus(serverPlayer);
		}

		if (updatedScaled > currentScaled && player instanceof ServerPlayer serverPlayer) {
			String formattedExperience = amount == Math.rint(amount)
				? String.format(Locale.ROOT, "%,.0f", amount)
				: String.format(Locale.ROOT, "%,.1f", amount);
			long progress = getExperienceWithinLevel(player, skill);
			long nextLevelXp = getExperienceForNextLevel(player, skill);
			serverPlayer.sendSystemMessage(
				Component.literal("+" + formattedExperience).withStyle(ChatFormatting.AQUA)
					.append(Component.literal(" (" + String.format(Locale.ROOT, "%,d", progress) + "/"
						+ String.format(Locale.ROOT, "%,d", nextLevelXp) + ")").withStyle(ChatFormatting.GOLD)),
				true
			);
		}
	}

	public static long getCollectionCount(Player player, String itemId) {
		Map<String, Long> collections = access(player).singleplayerhsfeatures$getCollections();
		long total = 0L;
		for (CollectionType type : CollectionType.values()) {
			total = Math.addExact(total, collections.getOrDefault(type.keyPrefix + itemId, 0L));
		}
		return total;
	}

	public static Map<String, Long> getCollections(Player player) {
		return Collections.unmodifiableMap(access(player).singleplayerhsfeatures$getCollections());
	}

	public static long getCollectionCount(Player player, String itemId, CollectionType type) {
		Map<String, Long> collections = access(player).singleplayerhsfeatures$getCollections();
		return collections.getOrDefault(type.keyPrefix + itemId, 0L);
	}

	public static void addCollectionItems(Player player, ItemStack stack) {
		addCollectionItems(player, stack, null);
	}

	public static void addCollectionItems(Player player, ItemStack stack, CollectionType type) {
		if (stack.isEmpty()) {
			return;
		}
		String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
		CollectionType registeredType = CollectionRegistry.getType(itemId);
		if (registeredType == null || type != null && registeredType != type) {
			return;
		}
		addCollectionItems(player, registeredType.keyPrefix + itemId, stack.getCount());
	}

	public static void addCollectionItems(Player player, String itemId, long count) {
		requireServerSide(player);
		if (count < 0) {
			throw new IllegalArgumentException("Collection count must be non-negative.");
		}
		if (count == 0) {
			return;
		}

		String collectionItemId = itemId;
		CollectionType requestedType = null;
		for (CollectionType type : CollectionType.values()) {
			if (itemId.startsWith(type.keyPrefix)) {
				requestedType = type;
				collectionItemId = itemId.substring(type.keyPrefix.length());
				break;
			}
		}
		CollectionType registeredType = CollectionRegistry.getType(collectionItemId);
		if (registeredType == null || requestedType != null && requestedType != registeredType) {
			return;
		}

		PlayerProgressionAccess access = access(player);
		Map<String, Long> collections = access.singleplayerhsfeatures$getCollections();
		String collectionKey = registeredType.keyPrefix + collectionItemId;
		long oldCount = collections.getOrDefault(collectionKey, 0L);
		long updated = Math.addExact(oldCount, count);
		collections.put(collectionKey, updated);
		access.singleplayerhsfeatures$setCollections(collections);
		if (player instanceof ServerPlayer serverPlayer
			&& CollectionProgression.getMilestonesCrossed(collectionItemId, oldCount, updated) > 0) {
			HealthStat.updateSkyBlockLevelBonus(serverPlayer);
		}
	}

	public static void copy(Player from, Player to) {
		PlayerProgressionAccess source = access(from);
		PlayerProgressionAccess target = access(to);
		target.singleplayerhsfeatures$setCombatExperience(source.singleplayerhsfeatures$getCombatExperience());
		target.singleplayerhsfeatures$setMiningExperience(source.singleplayerhsfeatures$getMiningExperience());
		target.singleplayerhsfeatures$setFarmingExperience(source.singleplayerhsfeatures$getFarmingExperience());
		target.singleplayerhsfeatures$setForagingExperience(source.singleplayerhsfeatures$getForagingExperience());
		target.singleplayerhsfeatures$setFishingExperience(source.singleplayerhsfeatures$getFishingExperience());
		target.singleplayerhsfeatures$setEnchantingExperience(source.singleplayerhsfeatures$getEnchantingExperience());
		target.singleplayerhsfeatures$setAlchemyExperience(source.singleplayerhsfeatures$getAlchemyExperience());
		target.singleplayerhsfeatures$setTamingExperience(source.singleplayerhsfeatures$getTamingExperience());
		target.singleplayerhsfeatures$setCollections(source.singleplayerhsfeatures$getCollections());
	}

	private static void requireServerSide(Player player) {
		if (!(player.level() instanceof ServerLevel)) {
			throw new IllegalStateException("Player progression can only be changed on the server.");
		}
	}

	private static PlayerProgressionAccess access(Player player) {
		if (player instanceof PlayerProgressionAccess access) {
			return access;
		}
		throw new IllegalStateException("Player progression data is unavailable; is the PlayerProgressionMixin loaded?");
	}

	public enum Skill {
		COMBAT("Combat"),
		MINING("Mining"),
		FARMING("Farming"),
		FORAGING("Foraging"),
		FISHING("Fishing"),
		ENCHANTING("Enchanting"),
		ALCHEMY("Alchemy"),
		TAMING("Taming");

		private final String displayName;

		Skill(String displayName) {
			this.displayName = displayName;
		}

		public String displayName() {
			return this.displayName;
		}
	}

	public enum CollectionType {
		MINING("Mining", "mining|"),
		FORAGING("Foraging", "foraging|"),
		COMBAT("Combat", "combat|"),
		FARMING("Farming", "farming|"),
		FISHING("Fishing", "fishing|");

		private final String displayName;
		private final String keyPrefix;

		CollectionType(String displayName, String keyPrefix) {
			this.displayName = displayName;
			this.keyPrefix = keyPrefix;
		}

		public String displayName() {
			return this.displayName;
		}

		public String keyPrefix() {
			return this.keyPrefix;
		}
	}
}
