package com.glxcian.shf.stat;

import java.util.Map;

public interface PlayerProgressionAccess {
	long singleplayerhsfeatures$getCombatExperience();

	void singleplayerhsfeatures$setCombatExperience(long experience);

	void singleplayerhsfeatures$setMiningExperience(long experience);

	long singleplayerhsfeatures$getMiningExperience();

	long singleplayerhsfeatures$getFarmingExperience();

	void singleplayerhsfeatures$setFarmingExperience(long experience);

	long singleplayerhsfeatures$getForagingExperience();

	void singleplayerhsfeatures$setForagingExperience(long experience);

	long singleplayerhsfeatures$getFishingExperience();

	void singleplayerhsfeatures$setFishingExperience(long experience);

	long singleplayerhsfeatures$getEnchantingExperience();

	void singleplayerhsfeatures$setEnchantingExperience(long experience);

	long singleplayerhsfeatures$getAlchemyExperience();

	void singleplayerhsfeatures$setAlchemyExperience(long experience);

	long singleplayerhsfeatures$getTamingExperience();

	void singleplayerhsfeatures$setTamingExperience(long experience);

	Map<String, Long> singleplayerhsfeatures$getCollections();

	void singleplayerhsfeatures$setCollections(Map<String, Long> collections);
}
