package com.glxcian.shf.stat;

import net.minecraft.world.entity.Mob;

public final class MobDamageStat {
	private MobDamageStat() {
	}

	public static float getLastDamage(Mob mob) {
		return access(mob).singleplayerhsfeatures$getLastDamage();
	}

	public static boolean wasRecentlyDamaged(Mob mob) {
		int elapsedTicks = mob.tickCount - access(mob).singleplayerhsfeatures$getLastDamageTick();
		return getLastDamage(mob) > 0.0F && elapsedTicks >= 0 && elapsedTicks <= 20;
	}

	public static void record(Mob mob, float damage) {
		if (!Float.isFinite(damage) || damage <= 0.0F) {
			return;
		}
		access(mob).singleplayerhsfeatures$setLastDamage(damage, mob.tickCount);
	}

	private static MobDamageAccess access(Mob mob) {
		if (mob instanceof MobDamageAccess access) {
			return access;
		}
		throw new IllegalStateException("Mob damage data is unavailable; is the MobLevelMixin loaded?");
	}
}
