package com.glxcian.shf.stat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public final class EnchantmentStatEffects {
	private static final int[] DAMAGE_PERCENT_BY_LEVEL = {0, 5, 10, 15, 20, 30, 40, 50};
	private static final int[] PROTECTION_DEFENSE_BY_LEVEL = {0, 4, 8, 12, 16, 20, 25, 30};

	private EnchantmentStatEffects() {
	}

	public static List<DamageBonus> getDamageBonuses(ItemStack stack) {
		List<DamageBonus> bonuses = new ArrayList<>();
		forEachEnchantment(stack, (holder, level, enchantment) -> {
			int percent = getDamagePercent(level);
			switch (enchantment) {
				case "sharpness" -> bonuses.add(new DamageBonus("Sharpness", level, percent, "Damage"));
				case "smite" -> bonuses.add(new DamageBonus("Smite", level, percent, "Damage vs Undead"));
				case "bane_of_arthropods" -> bonuses.add(
					new DamageBonus("Bane of Arthropods", level, percent, "Damage vs Arthropods")
				);
				default -> {
				}
			}
		});
		return List.copyOf(bonuses);
	}

	public static List<TooltipEnchantment> getTooltipEnchantments(ItemStack stack) {
		List<TooltipEnchantment> enchantments = new ArrayList<>();
		Set<String> seen = new HashSet<>();
		forEachEnchantment(stack, (holder, level, enchantment) -> {
			MutableComponent name = Component.literal("").append(holder.value().description());
			if (level > 1) {
				name.append(" ").append(Component.literal(SkillProgression.roman(level)));
			}
			if (!seen.add(name.getString())) {
				return;
			}
			String effect = getDamageEffect(enchantment, level);
			enchantments.add(new TooltipEnchantment(name, effect, enchantment));
		});
		return List.copyOf(enchantments);
	}

	public static int getUniversalDamageBonus(ItemStack stack) {
		int percent = 0;
		for (DamageBonus bonus : getDamageBonuses(stack)) {
			if (bonus.name().equals("Sharpness")) {
				percent += bonus.percent();
			}
		}
		return percent;
	}

	public static int getMiningSpeedBonus(ItemStack stack) {
		if (!isPickaxe(stack)) {
			return 0;
		}
		int level = Math.min(10, getEnchantmentLevel(stack, "efficiency"));
		int[] bonusByLevel = {0, 30, 50, 70, 90, 110, 130, 150, 170, 190, 210};
		return bonusByLevel[level];
	}

	public static int getMiningFortuneBonus(ItemStack stack) {
		if (!isPickaxe(stack)) {
			return 0;
		}
		int level = Math.min(4, getEnchantmentLevel(stack, "fortune"));
		return switch (level) {
			case 1 -> 10;
			case 2 -> 20;
			case 3 -> 30;
			case 4 -> 45;
			default -> 0;
		};
	}

	public static int getProtectionDefenseBonus(ItemStack stack) {
		return getProtectionDefenseForLevel(getEnchantmentLevel(stack, "protection"));
	}

	private static int getProtectionDefenseForLevel(int level) {
		return PROTECTION_DEFENSE_BY_LEVEL[Math.max(0, Math.min(level, PROTECTION_DEFENSE_BY_LEVEL.length - 1))];
	}

	public static int getFeatherFallingLevel(LivingEntity entity) {
		return Math.min(10, getEnchantmentLevel(entity.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET),
			"feather_falling"));
	}

	public static int getFarmingFortuneBonus(ItemStack stack) {
		return isFarmingTool(stack) ? getEnchantmentLevel(stack, "fortune") * 10 : 0;
	}

	public static int getFortuneBonus(ItemStack stack, boolean farming) {
		if (farming) {
			return getFarmingFortuneBonus(stack);
		}
		return hasSmeltingTouch(stack) ? 0 : getMiningFortuneBonus(stack);
	}

	public static boolean hasSmeltingTouch(ItemStack stack) {
		return getEnchantmentLevel(stack, "smelting_touch") > 0;
	}

	public static int getSkyBlockDamagePercent(ItemStack stack, LivingEntity target) {
		int percent = 0;
		for (DamageBonus bonus : getDamageBonuses(stack)) {
			if (bonus.name().equals("Sharpness")
				|| bonus.name().equals("Smite") && target.typeHolder().is(EntityTypeTags.UNDEAD)
				|| bonus.name().equals("Bane of Arthropods")
					&& target.typeHolder().is(EntityTypeTags.ARTHROPOD)) {
				percent += bonus.percent();
			}
		}
		return percent;
	}

	private static int getEnchantmentLevel(ItemStack stack, String enchantmentId) {
		int[] level = {0};
		forEachEnchantment(stack, (holder, currentLevel, enchantment) -> {
			if (enchantment.equals(enchantmentId)) {
				level[0] = Math.max(level[0], currentLevel);
			}
		});
		return level[0];
	}

	private static String getDamageEffect(String enchantment, int level) {
		int percent = getDamagePercent(level);
		if (percent == 0) {
			return "";
		}
		return switch (enchantment) {
			case "sharpness" -> "";
			case "smite" -> ": +" + percent + "% Damage vs Undead";
			case "bane_of_arthropods" -> ": +" + percent + "% Damage vs Arthropods";
			case "efficiency", "fortune" -> "";
			case "protection" -> ": +" + getProtectionDefenseForLevel(level) + " Defense";
			case "feather_falling" -> ": +" + (Math.min(level, 10) * 5) + " Fall Height, -"
				+ (Math.min(level, 10) * 5) + "% Fall Damage";
			default -> "";
		};
	}

	private static boolean isPickaxe(ItemStack stack) {
		return net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().endsWith("_pickaxe");
	}

	private static boolean isFarmingTool(ItemStack stack) {
		return net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().endsWith("_hoe");
	}

	private static void forEachEnchantment(ItemStack stack, EnchantmentConsumer consumer) {
		ItemEnchantments regular = stack.get(DataComponents.ENCHANTMENTS);
		if (regular != null) {
			regular.entrySet().forEach(entry ->
				consumer.accept(
					entry.getKey(), entry.getIntValue(), entry.getKey().unwrapKey().orElseThrow().identifier().getPath()
				)
			);
		}
		ItemEnchantments stored = stack.get(DataComponents.STORED_ENCHANTMENTS);
		if (stored != null) {
			stored.entrySet().forEach(entry ->
				consumer.accept(
					entry.getKey(), entry.getIntValue(), entry.getKey().unwrapKey().orElseThrow().identifier().getPath()
				)
			);
		}
	}

	private static int getDamagePercent(int level) {
		return DAMAGE_PERCENT_BY_LEVEL[Math.max(0, Math.min(level, DAMAGE_PERCENT_BY_LEVEL.length - 1))];
	}

	@FunctionalInterface
	private interface EnchantmentConsumer {
		void accept(Holder<Enchantment> holder, int level, String enchantment);
	}

	public record DamageBonus(String name, int level, int percent, String description) {
	}

	public record TooltipEnchantment(Component name, String effect, String id) {
	}
}
