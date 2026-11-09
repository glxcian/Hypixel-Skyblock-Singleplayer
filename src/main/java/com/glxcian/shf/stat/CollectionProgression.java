package com.glxcian.shf.stat;

import java.util.List;
import java.util.Map;

public final class CollectionProgression {
	private static final Map<String, List<Milestone>> MILESTONES_BY_ITEM = Map.of(
		"minecraft:cobblestone", List.of(
			new Milestone(50, "Cobblestone Minion Recipe"),
			new Milestone(100, "Stone Platform Recipe"),
			new Milestone(250, "Silverfish Pet Recipe; Auto Smelter Recipe"),
			new Milestone(1_000, "Enchanted Cobblestone Recipe"),
			new Milestone(2_500, "Compactor Recipe"),
			new Milestone(5_000, "+1,000 Mining Experience"),
			new Milestone(10_000, "Haste Ring Recipe"),
			new Milestone(25_000, "Hyper Furnace Recipe"),
			new Milestone(40_000, "Haste Artifact Recipe"),
			new Milestone(70_000, "Super Compactor 3000 Recipe")
		),
		"minecraft:iron_ingot", List.of(
			new Milestone(50, "Iron Minion Recipe"),
			new Milestone(100, "Golem Hat Recipe; Prospecting Armor Recipe"),
			new Milestone(250, "Protection Experience Discount (-25%)"),
			new Milestone(1_000, "Enchanted Iron Ingot Recipe"),
			new Milestone(2_500, "Budget Hopper Recipe"),
			new Milestone(5_000, "Golem Armor Recipe"),
			new Milestone(10_000, "Enchanted Iron Block Recipe"),
			new Milestone(25_000, "Golem Sword Recipe"),
			new Milestone(50_000, "Enchanted Hopper Recipe; Personal Deletor 4000 Recipe"),
			new Milestone(100_000, "Personal Deletor 5000 Recipe"),
			new Milestone(200_000, "Personal Deletor 6000 Recipe"),
			new Milestone(400_000, "Personal Deletor 7000 Recipe")
		),
		"minecraft:rotten_flesh", List.of(
			new Milestone(50, "Zombie Minion Recipe"),
			new Milestone(100, "Zombie Pickaxe Recipe"),
			new Milestone(250, "Smite Experience Discount (-25%); Zombie Pet Recipe"),
			new Milestone(1_000, "Enchanted Rotten Flesh Recipe"),
			new Milestone(2_500, "Zombie Hat Recipe; Small Combat Sack Recipe"),
			new Milestone(5_000, "Zombie's Heart Recipe"),
			new Milestone(10_000, "Zombie Sword Recipe; Medium Combat Sack Recipe"),
			new Milestone(25_000, "Zombie Armor Recipe"),
			new Milestone(50_000, "Large Combat Sack Recipe"),
			new Milestone(100_000, "Large Enchanted Combat Sack Recipe")
		),
		"minecraft:obsidian", List.of(
			new Milestone(50, "Obsidian Minion Recipe"),
			new Milestone(100, "Lethality Experience Discount (-25%)"),
			new Milestone(250, "Gravity Talisman Recipe"),
			new Milestone(1_000, "Enchanted Obsidian Recipe"),
			new Milestone(2_500, "+1 Mining Fortune"),
			new Milestone(5_000, "Stun Potion Recipe"),
			new Milestone(10_000, "+1 Mining Fortune"),
			new Milestone(25_000, "+1 Mining Fortune"),
			new Milestone(50_000, "Obsidian Tablet Recipe"),
			new Milestone(100_000, "+1 Mining Fortune; +50,000 Mining Experience")
		),
		"singleplayerhsfeatures:glacite", List.of(
			new Milestone(100, "+1 Mining Fortune"),
			new Milestone(1_000, "Enchanted Glacite Recipe; Cold Resistance Potion Recipe"),
			new Milestone(5_000, "Glacite Amalgamation Dwarven Forge Recipe"),
			new Milestone(10_000, "Glacite Golem Pet Recipe"),
			new Milestone(25_000, "Frigid Husk Dwarven Forge Recipe"),
			new Milestone(50_000, "+1 Mining Fortune"),
			new Milestone(100_000, "+1 Mining Fortune"),
			new Milestone(250_000, "+1 Mining Fortune"),
			new Milestone(500_000, "+10,000 Mining Experience")
		),
		"singleplayerhsfeatures:tungsten", List.of(
			new Milestone(100, "+1 Mining Fortune"),
			new Milestone(1_000, "Enchanted Tungsten Recipe"),
			new Milestone(5_000, "Refined Tungsten Dwarven Forge Recipe"),
			new Milestone(10_000, "Tungsten Key Dwarven Forge Recipe"),
			new Milestone(25_000, "+1 Mining Fortune"),
			new Milestone(50_000, "Tungsten Plate Dwarven Forge Recipe"),
			new Milestone(100_000, "+1 Mining Fortune"),
			new Milestone(250_000, "+1 Mining Fortune"),
			new Milestone(500_000, "+1 Mining Fortune")
		),
		"singleplayerhsfeatures:umber", List.of(
			new Milestone(100, "+1 Mining Fortune"),
			new Milestone(1_000, "Enchanted Umber Recipe"),
			new Milestone(5_000, "Refined Umber Dwarven Forge Recipe"),
			new Milestone(10_000, "Umber Key Dwarven Forge Recipe"),
			new Milestone(25_000, "+1 Mining Fortune"),
			new Milestone(50_000, "Umber Plate Dwarven Forge Recipe"),
			new Milestone(100_000, "+1 Mining Fortune"),
			new Milestone(250_000, "+1 Mining Fortune"),
			new Milestone(500_000, "Umberella Recipe")
		)
	);

	private CollectionProgression() {
	}

	public static List<Milestone> milestones(String itemId) {
		return MILESTONES_BY_ITEM.getOrDefault(itemId, List.of());
	}

	public static int getLevel(String itemId, long count) {
		List<Milestone> milestones = milestones(itemId);
		int level = 0;
		for (Milestone milestone : milestones) {
			if (count < milestone.required()) {
				break;
			}
			level++;
		}
		return level;
	}

	public static int getMiningFortune(String itemId, long count) {
		int fortune = 0;
		for (Milestone milestone : milestones(itemId)) {
			if (count < milestone.required()) {
				break;
			}
			if (milestone.reward().contains("Mining Fortune")) {
				fortune++;
			}
		}
		return fortune;
	}

	public static int getNextRequirement(String itemId, long count) {
		List<Milestone> milestones = milestones(itemId);
		int level = getLevel(itemId, count);
		return level >= milestones.size() ? 0 : milestones.get(level).required();
	}

	public static long getSkyBlockExperience(String itemId, long count) {
		return (long) getLevel(itemId, count) * 4L;
	}

	public static int getMilestonesCrossed(String itemId, long oldCount, long newCount) {
		return getLevel(itemId, newCount) - getLevel(itemId, oldCount);
	}

	public record Milestone(int required, String reward) {
		public Milestone {
			if (required <= 0 || reward == null || reward.isBlank()) {
				throw new IllegalArgumentException("Collection milestone must have a positive threshold and reward.");
			}
		}
	}
}
