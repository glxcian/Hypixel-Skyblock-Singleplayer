package com.glxcian.shf.stat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import net.minecraft.world.entity.player.Player;

public final class SkillProgression {
	private static final long[] COMBAT_COINS = {
		0, 100, 250, 500, 750, 1_000, 2_000, 3_000, 4_000, 5_000, 7_500, 10_000, 15_000
	};
	private static final Map<Integer, Long> ADDITIONAL_COMBAT_COINS = Map.of(22, 150_000L);
	private static final long[] MINING_COINS = {
		0, 100, 250, 500, 750, 1_000, 2_000, 3_000, 4_000, 5_000, 7_500,
		10_000, 15_000, 20_000, 25_000, 30_000, 40_000, 50_000, 65_000, 80_000,
		100_000, 125_000, 150_000, 175_000, 200_000, 225_000, 250_000, 275_000,
		300_000, 325_000, 350_000, 375_000, 400_000, 425_000, 450_000, 475_000,
		500_000, 550_000, 600_000, 650_000, 700_000, 750_000, 800_000, 850_000,
		900_000, 1_000_000
	};
	private static final Map<Integer, String> MINING_REFORGES = Map.ofEntries(
		Map.entry(1, "Jerry Stone"),
		Map.entry(2, "Black Diamond"),
		Map.entry(3, "Overflowing Trash Can"),
		Map.entry(5, "Lapis Crystal; Moil Log"),
		Map.entry(8, "Rare Diamond"),
		Map.entry(9, "SkyMart Brochure"),
		Map.entry(10, "Dirt Bottle"),
		Map.entry(11, "Sunstone; Moonstone"),
		Map.entry(12, "Moonglade Jewel; Hardened Wood; Large Walnut"),
		Map.entry(13, "Full-Jaw Fanging Kit; Flowering Bouquet"),
		Map.entry(14, "Dwarven Treasure; End Stone Geode"),
		Map.entry(15, "Beady Eyes; Diamonite; Clipped Wings; Kaleidoscopic Mineral; Terry's Snowglobe"),
		Map.entry(16, "Premium Flesh"),
		Map.entry(17, "Red Nose"),
		Map.entry(18, "Pitchin' Koi; Pure Mithril"),
		Map.entry(19, "Necromancer's Brooch"),
		Map.entry(20, "Entropy Suppressor; Titanium Tesseract; Dragon Scale; Molten Cube; Frozen Bauble"),
		Map.entry(21, "Red Scarf; Squeaky Toy; Dwarven Geode"),
		Map.entry(22, "Dragon Claw; Optical Lens; Mantid Claw; Diamond Atom"),
		Map.entry(23, "Overgrown Grass; Suspicious Vial"),
		Map.entry(24, "Gleaming Crystal; Presumed Gallon of Red Paint; Searing Stone; Blaze Wax; Lucky Dice; Burrowing Spores; Shiny Prism; Meteor Chunk; Spirit Stone"),
		Map.entry(25, "Giant Tooth; Andesite Whetstone; Bulky Stone; Midas Jewel; Sadan's Brooch; Dragon Horn; Toil Log; Blessed Fruit"),
		Map.entry(26, "Warped Stone; Shriveled Cornea; Petrified Starfall"),
		Map.entry(27, "Blazen Sphere"),
		Map.entry(30, "Amber Material; Wither Blood; Precursor Gear; Mangrove Gem; Golden Ball; Frigid Husk; Jaderald; Daedalus' Notes"),
		Map.entry(33, "Kuudra Mandible")
	);
	private static final Map<Integer, String> COMBAT_UNLOCKS = Map.of(
		1, "Access to Spider's Den",
		4, "Artisanal Shortbow; Celeste Wand; Celeste Armor; Mercenary Sword; Reinforced Iron Arrow; Squire Sword; Squire Armor",
		8, "Gold-tipped Arrow; Starlight Wand; Starlight Armor; Mercenary Armor",
		10, "Displaced Leech",
		12, "Access to The End; Ender Armor; Ender Necklace; Ender Cloak; Ender Belt; Ender Gauntlet",
		22, "Access to Crimson Isle"
	);

	private SkillProgression() {
	}

	public static List<String> rewards(Player player, PlayerProgression.Skill skill, int level) {
		List<String> lines = new ArrayList<>();
		if (skill == PlayerProgression.Skill.COMBAT) {
			String unlock = COMBAT_UNLOCKS.get(level);
			if (unlock != null && unlock.startsWith("Access to")) {
				lines.add("+" + unlock);
			}
			int damageBonus = Math.min(level, 50) * 4 + Math.max(level - 50, 0);
			int previousDamageBonus = Math.min(Math.max(level - 1, 0), 50) * 4 + Math.max(level - 51, 0);
			lines.add("Warrior " + roman(level));
			lines.add(level == 1
				? "Deal " + damageBonus + "% more damage to mobs"
				: "Deal " + previousDamageBonus + " \u279c " + damageBonus + "% more damage to mobs");
			lines.add("+0.5% Crit Chance");
			if (level < COMBAT_COINS.length) {
				lines.add("+" + format(COMBAT_COINS[level]) + " Coins");
			} else if (ADDITIONAL_COMBAT_COINS.containsKey(level)) {
				lines.add("+" + format(ADDITIONAL_COMBAT_COINS.get(level)) + " Coins");
			}
			if (unlock != null && !unlock.startsWith("Access to")) {
				lines.add("Unlocks: " + unlock);
			}
		} else if (skill == PlayerProgression.Skill.MINING) {
			int defense = level <= 14 ? 1 : 2;
			lines.add("+" + defense + " Defense");
			int fortune = level * 4;
			lines.add("Spelunker " + roman(level) + ": +" + (level == 1 ? fortune : fortune - 4 + " -> +" + fortune)
				+ " Mining Fortune");
			if (level < MINING_COINS.length) {
				lines.add("+" + format(MINING_COINS[level]) + " Coins");
			} else {
				lines.add("+1,000,000 Coins");
			}
			if (level == 1) {
				lines.add("Access to Gold Mine");
			} else if (level == 5) {
				lines.add("Access to Deep Caverns");
			} else if (level == 12) {
				lines.add("Access to Dwarven Mines and Crystal Hollows");
			}
			if (level == 26) {
				lines.add("+1,000 Mining Experience");
			}
			String reforges = MINING_REFORGES.get(level);
			if (reforges != null) {
				lines.add("Reforge Stones: " + reforges);
			}
		} else {
			switch (skill) {
				case FARMING -> {
					lines.add("Farmer " + roman(level));
					lines.add("+" + (level * 4) + " Farming Fortune");
				}
				case FORAGING -> {
					lines.add("Logger " + roman(level));
					lines.add("+" + level + " Strength");
					lines.add("+" + (level * 4) + " Foraging Fortune");
				}
				case FISHING -> {
					lines.add("Angler " + roman(level));
					lines.add(String.format(Locale.ROOT, "+%.1f%% Sea Creature Chance", level * 0.2));
				}
				case ENCHANTING -> {
					lines.add("Conjurer " + roman(level));
					lines.add("+" + level + " Intelligence");
					lines.add("+" + (level * 5) + "% Experience from all sources");
				}
				case ALCHEMY -> {
					lines.add("Mixologist " + roman(level));
					lines.add("+" + level + " Intelligence");
				}
				case TAMING -> {
					lines.add("Beastmaster " + roman(level));
					lines.add("+" + level + " Pet Luck");
					lines.add("+" + level + "% Pet XP");
				}
				case COMBAT, MINING -> throw new IllegalStateException("Handled skill reward branch unexpectedly.");
			}
		}
		lines.add("+" + PlayerProgression.getSkyBlockExperience(skill, level) + " SkyBlock XP");
		return lines;
	}

	public static String roman(int number) {
		if (number <= 0) {
			return "0";
		}
		int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
		String[] numerals = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
		StringBuilder result = new StringBuilder();
		for (int index = 0; index < values.length; index++) {
			while (number >= values[index]) {
				result.append(numerals[index]);
				number -= values[index];
			}
		}
		return result.toString();
	}

	private static String format(long amount) {
		return String.format(Locale.ROOT, "%,d", amount);
	}
}
