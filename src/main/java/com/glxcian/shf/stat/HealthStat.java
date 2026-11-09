package com.glxcian.shf.stat;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class HealthStat {
	private static final float STAT_POINTS_PER_MINECRAFT_HEALTH = 5.0F;

	private HealthStat() {
	}

	public static float getCurrent(LivingEntity entity) {
		return toStatPoints(entity.getHealth());
	}

	public static float getMaximum(LivingEntity entity) {
		return toStatPoints(entity.getMaxHealth());
	}

	public static float getBonusHealth(Player player) {
		return playerHealthAccess(player).singleplayerhsfeatures$getBonusHealth();
	}

	public static void updateSkyBlockLevelBonus(Player player) {
		float levelBonus = PlayerProgression.getSkyBlockLevel(player) * 5.0F;
		if (getBonusHealth(player) != levelBonus) {
			setBonusHealth(player, levelBonus);
		}
	}

	public static void setCurrent(LivingEntity entity, float health) {
		requireServerSide(entity);
		entity.setHealth(toMinecraftHealth(health));
	}

	public static void setMaximum(LivingEntity entity, float health) {
		requireServerSide(entity);
		AttributeInstance maximumHealth = entity.getAttribute(Attributes.MAX_HEALTH);
		if (maximumHealth == null) {
			throw new IllegalStateException("Entity does not have a maximum health attribute.");
		}

		float bonusHealth = entity instanceof Player player
			? playerHealthAccess(player).singleplayerhsfeatures$getBonusHealth()
			: 0.0F;
		maximumHealth.setBaseValue(toMinecraftHealth(health - bonusHealth));
		entity.setHealth(Math.min(entity.getHealth(), entity.getMaxHealth()));
	}

	public static void heal(LivingEntity entity, float amount) {
		requireServerSide(entity);
		entity.heal(toMinecraftHealth(amount));
	}

	public static void setBonusHealth(Player player, float bonusHealth) {
		requireServerSide(player);
		if (!Float.isFinite(bonusHealth) || bonusHealth < 0.0F) {
			throw new IllegalArgumentException("Bonus Health must be finite and non-negative.");
		}

		AttributeInstance maximumHealth = player.getAttribute(Attributes.MAX_HEALTH);
		if (maximumHealth == null) {
			throw new IllegalStateException("Player does not have a maximum health attribute.");
		}

		PlayerHealthAccess access = playerHealthAccess(player);
		maximumHealth.setBaseValue(maximumHealth.getBaseValue()
			+ toMinecraftHealth(bonusHealth - access.singleplayerhsfeatures$getBonusHealth()));
		access.singleplayerhsfeatures$setBonusHealth(bonusHealth);
		player.setHealth(Math.min(player.getHealth(), player.getMaxHealth()));
	}

	public static void regenerate(Player player, float healthRegen) {
		requireServerSide(player);
		if (!Float.isFinite(healthRegen) || healthRegen < 0.0F) {
			throw new IllegalArgumentException("Health Regen must be finite and non-negative.");
		}

		float amount = (getMaximum(player) / 100.0F + 1.5F) * (healthRegen / 100.0F);
		heal(player, amount);
	}

	private static float toStatPoints(float health) {
		return health * STAT_POINTS_PER_MINECRAFT_HEALTH;
	}

	private static float toMinecraftHealth(float health) {
		if (!Float.isFinite(health) || health < 0.0F) {
			throw new IllegalArgumentException("Health must be a finite, non-negative value.");
		}

		return health / STAT_POINTS_PER_MINECRAFT_HEALTH;
	}

	private static void requireServerSide(LivingEntity entity) {
		if (!(entity.level() instanceof ServerLevel)) {
			throw new IllegalStateException("Health stats can only be changed on the server.");
		}
	}

	private static PlayerHealthAccess playerHealthAccess(Player player) {
		if (player instanceof PlayerHealthAccess access) {
			return access;
		}
		throw new IllegalStateException("Player Health data is unavailable; is the PlayerHealthMixin loaded?");
	}
}
