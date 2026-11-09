package com.glxcian.shf;

import net.fabricmc.api.ModInitializer;
import com.glxcian.shf.stat.ArmorDefenseConfig;
import com.glxcian.shf.stat.MiningSpeedConfig;
import com.glxcian.shf.stat.MobProfileConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleplayerHSFeatures implements ModInitializer {
	public static final String MOD_ID = "singleplayerhsfeatures";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ArmorDefenseConfig.load();
		MiningSpeedConfig.load();
		MobProfileConfig.load();
		LOGGER.info("Initializing SingleplayerHSFeatures...");
	}
}
