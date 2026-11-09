package com.glxcian.shf.stat;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public final class MobLevel {
	private MobLevel() {
	}

	public static int get(LivingEntity entity) {
		return entity instanceof MobLevelAccess levelAccess
			? levelAccess.singleplayerhsfeatures$getMobLevel()
			: 0;
	}

	public static void set(LivingEntity entity, int level) {
		if (level < 1) {
			throw new IllegalArgumentException("Mob level must be at least 1.");
		}
		if (!(entity.level() instanceof ServerLevel)) {
			throw new IllegalStateException("Mob levels can only be changed on the server.");
		}
		if (!(entity instanceof MobLevelAccess levelAccess)) {
			throw new IllegalArgumentException("Only mobs can have a mob level.");
		}

		levelAccess.singleplayerhsfeatures$setMobLevel(level);
	}
}
