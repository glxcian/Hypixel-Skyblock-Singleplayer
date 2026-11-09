package com.glxcian.shf.stat;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

public final class HealthRegenStat {
	private HealthRegenStat() {
	}

	public static float get(Player player) {
		return access(player).singleplayerhsfeatures$getHealthRegen();
	}

	public static void set(Player player, float healthRegen) {
		if (!(player.level() instanceof ServerLevel)) {
			throw new IllegalStateException("Health Regen can only be changed on the server.");
		}
		if (!Float.isFinite(healthRegen) || healthRegen < 0.0F) {
			throw new IllegalArgumentException("Health Regen must be finite and non-negative.");
		}
		access(player).singleplayerhsfeatures$setHealthRegen(healthRegen);
	}

	private static PlayerHealthAccess access(Player player) {
		if (player instanceof PlayerHealthAccess access) {
			return access;
		}
		throw new IllegalStateException("Player Health Regen data is unavailable; is the PlayerHealthMixin loaded?");
	}
}
