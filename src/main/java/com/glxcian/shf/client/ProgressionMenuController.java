package com.glxcian.shf.client;

import com.glxcian.shf.stat.CollectionRegistry;
import com.glxcian.shf.stat.PlayerProgression;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.Item;

public final class ProgressionMenuController {
	private ProgressionMenuController() {
	}

	public static void open(ServerPlayer player) {
		open(player, ProgressionMenu.Page.HOME);
	}

	public static void open(ServerPlayer player, ProgressionMenu.Page page) {
		player.openMenu(new SimpleMenuProvider(
			(containerId, inventory, menuPlayer) ->
				new ProgressionMenu(containerId, player, page, 0, null, null),
			Component.literal(page.title())
		));
	}

	public static void openCollectionItems(
		ServerPlayer player,
		PlayerProgression.CollectionType type,
		int page
	) {
		int safePage = Math.max(0, page);
		player.openMenu(new SimpleMenuProvider(
			(containerId, inventory, menuPlayer) ->
				new ProgressionMenu(containerId, player, ProgressionMenu.Page.COLLECTION_ITEMS, safePage, null, type),
			Component.literal(type == null ? "Collections" : type.displayName() + " Collections")
		));
	}

	public static void openCollectionDetail(
		ServerPlayer player,
		PlayerProgression.CollectionType type,
		int page,
		String itemId
	) {
		player.openMenu(new SimpleMenuProvider(
			(containerId, inventory, menuPlayer) ->
				new ProgressionMenu(containerId, player, ProgressionMenu.Page.COLLECTION_DETAIL, page, itemId, type),
			Component.literal("Collection")
		));
	}

	public static void openSkillLevels(ServerPlayer player, PlayerProgression.Skill skill, int page) {
		int safePage = Math.max(0, page);
		player.openMenu(new SimpleMenuProvider(
			(containerId, inventory, menuPlayer) ->
				new ProgressionMenu(containerId, player, ProgressionMenu.Page.SKILL_LEVELS, safePage, skill.name(), null),
			Component.literal(skill.displayName() + " Skill")
		));
	}

	public static void openSkillLevelDetail(
		ServerPlayer player,
		PlayerProgression.Skill skill,
		int level,
		int page
	) {
		int safeLevel = Math.max(1, Math.min(60, level));
		player.openMenu(new SimpleMenuProvider(
			(containerId, inventory, menuPlayer) ->
				new ProgressionMenu(
					containerId, player, ProgressionMenu.Page.SKILL_LEVEL_DETAIL, safeLevel,
					skill.name() + ":" + Math.max(0, page), null
				),
			Component.literal(skill.displayName() + " Level " + com.glxcian.shf.stat.SkillProgression.roman(safeLevel))
		));
	}

	public static void openGlobalLevel(ServerPlayer player) {
		player.openMenu(new SimpleMenuProvider(
			(containerId, inventory, menuPlayer) ->
				new ProgressionMenu(containerId, player, ProgressionMenu.Page.GLOBAL_LEVEL, 0, null, null),
			Component.literal("SkyBlock Leveling")
		));
	}

	public static void openCollectionLevels(
		ServerPlayer player,
		PlayerProgression.CollectionType type,
		int page,
		String itemId
	) {
		player.openMenu(new SimpleMenuProvider(
			(containerId, inventory, menuPlayer) ->
				new ProgressionMenu(containerId, player, ProgressionMenu.Page.COLLECTION_LEVELS, page, itemId, type),
			Component.literal("Collection Milestones")
		));
	}

	public static void openCollectionReward(
		ServerPlayer player,
		PlayerProgression.CollectionType type,
		int page,
		String itemId,
		int level
	) {
		player.openMenu(new SimpleMenuProvider(
			(containerId, inventory, menuPlayer) ->
				new ProgressionMenu(containerId, player, ProgressionMenu.Page.COLLECTION_REWARD, level, itemId, type),
			Component.literal("Collection Level " + com.glxcian.shf.stat.SkillProgression.roman(level))
		));
	}

	static List<Item> getCollectionItems(ServerPlayer player, PlayerProgression.CollectionType type) {
		return CollectionRegistry.getItems(type);
	}
}
