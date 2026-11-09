package com.glxcian.shf.stat;

import java.util.Set;

public record MobProfile(
	String entity,
	String dimension,
	Set<String> biomes,
	Integer minY,
	Integer maxY,
	int level,
	float health,
	Float attackDamage,
	Integer combatXp
) {
	public boolean matches(String entityId, String dimensionId, String biomeId, int y) {
		return this.entity.equals(entityId)
			&& (this.dimension == null || this.dimension.equals(dimensionId))
			&& (this.biomes.isEmpty() || this.biomes.contains(biomeId))
			&& (this.minY == null || y >= this.minY)
			&& (this.maxY == null || y <= this.maxY);
	}
}
