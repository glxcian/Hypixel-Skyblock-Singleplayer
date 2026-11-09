package com.glxcian.shf.client;

import com.glxcian.shf.stat.CollectionRegistry;
import com.glxcian.shf.stat.CollectionProgression;
import com.glxcian.shf.stat.PlayerProgression;
import com.glxcian.shf.stat.SkillProgression;
import java.util.List;
import java.util.ArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;

public final class ProgressionMenu extends ChestMenu {
	private static final int PAGE_SIZE = 45;
	private static final int COLLECTION_ITEMS_PER_PAGE = 28;
	private static final int[] COLLECTION_SLOTS = {
		10, 11, 12, 13, 14, 15, 16,
		19, 20, 21, 22, 23, 24, 25,
		28, 29, 30, 31, 32, 33, 34,
		37, 38, 39, 40, 41, 42, 43
	};
	private static final int LEVELS_PER_PAGE = 20;
	private static final int[] LEVEL_SLOTS = {
		27, 28, 20, 11, 2, 3, 4, 13, 22, 31,
		32, 33, 24, 15, 6, 7, 8, 17, 26, 35
	};
	private static final int[] SKILL_MENU_SLOTS = {10, 12, 14, 16, 19, 21, 23, 25};
	private static final Item[] SKILL_MENU_ICONS = {
		Items.IRON_SWORD, Items.IRON_PICKAXE, Items.GOLDEN_HOE, Items.IRON_AXE,
		Items.FISHING_ROD, Items.ENCHANTING_TABLE, Items.BREWING_STAND, Items.BONE
	};
	private final Page page;
	private final int collectionPage;
	private final String detailItemId;
	private final PlayerProgression.CollectionType collectionType;

	ProgressionMenu(
		int containerId,
		ServerPlayer player,
		Page page,
		int collectionPage,
		String detailItemId,
		PlayerProgression.CollectionType collectionType
	) {
		this(containerId, player, page, collectionPage, detailItemId, collectionType,
			createContainer(player, page, collectionPage, detailItemId, collectionType));
	}

	private static void setGlobalLevelSelector(SimpleContainer container, int slot, Player player) {
		ItemStack stack = menuIcon(Items.NETHER_STAR);
		stack.set(DataComponents.CUSTOM_NAME, noItalic(Component.literal("SkyBlock Leveling")
			.withStyle(ChatFormatting.GREEN)));
		stack.set(DataComponents.LORE, skyBlockLevelTooltip(player));
		container.setItem(slot, stack);
	}

	private ProgressionMenu(
		int containerId,
		ServerPlayer player,
		Page page,
		int collectionPage,
		String detailItemId,
		PlayerProgression.CollectionType collectionType,
		SimpleContainer container
	) {
		super(MenuType.GENERIC_9x6, containerId, player.getInventory(), container, 6);
		this.page = page;
		this.collectionPage = collectionPage;
		this.detailItemId = detailItemId;
		this.collectionType = collectionType;
	}

	@Override
	public void clicked(int slotIndex, int button, ContainerInput input, Player player) {
		if (slotIndex >= 54) {
			super.clicked(slotIndex, button, input, player);
			return;
		}
		if (!(player instanceof ServerPlayer serverPlayer) || slotIndex < 0) {
			return;
		}

		switch (this.page) {
			case HOME -> {
				if (slotIndex == 20) {
					ProgressionMenuController.open(serverPlayer, Page.SKILLS);
				} else if (slotIndex == 22) {
					ProgressionMenuController.openGlobalLevel(serverPlayer);
				} else if (slotIndex == 24) {
					ProgressionMenuController.open(serverPlayer, Page.COLLECTIONS);
				} else if (slotIndex == 49) {
					serverPlayer.closeContainer();
				}
			}
			case SKILLS -> {
				int skillIndex = indexOf(SKILL_MENU_SLOTS, slotIndex);
				if (skillIndex >= 0) {
					ProgressionMenuController.openSkillLevels(
						serverPlayer, PlayerProgression.Skill.values()[skillIndex], 0
					);
				} else if (slotIndex == 45) {
					ProgressionMenuController.open(serverPlayer, Page.HOME);
				}
			}
			case COLLECTIONS -> this.handleNavigation(serverPlayer, slotIndex);
			case SKILL_LEVELS -> this.handleSkillLevelClick(serverPlayer, slotIndex);
			case COMBAT_LEVELS, MINING_LEVELS -> this.handleSkillLevelClick(serverPlayer, slotIndex);
			case SKILL_LEVEL_DETAIL -> {
				if (slotIndex == 49) {
					this.openParentSkillLevels(serverPlayer);
				}
			}
			case COMBAT_LEVEL_DETAIL, MINING_LEVEL_DETAIL -> {
				if (slotIndex == 49) {
					PlayerProgression.Skill skill = this.page == Page.COMBAT_LEVEL_DETAIL
						? PlayerProgression.Skill.COMBAT : PlayerProgression.Skill.MINING;
					ProgressionMenuController.openSkillLevels(
						serverPlayer,
						skill,
						this.detailItemId == null ? 0 : Integer.parseInt(this.detailItemId)
					);
				}
			}
			case COLLECTION_ITEMS -> this.handleCollectionClick(serverPlayer, slotIndex);
			case COLLECTION_DETAIL -> {
				if (slotIndex == 49) {
					ProgressionMenuController.openCollectionItems(serverPlayer, this.collectionType, this.collectionPage);
				} else if (slotIndex == 13) {
					ProgressionMenuController.openCollectionLevels(
						serverPlayer,
						this.collectionType,
						this.collectionPage,
						this.detailItemId
					);
				}
			}
			case COLLECTION_LEVELS -> this.handleCollectionLevelClick(serverPlayer, slotIndex);
			case COLLECTION_REWARD -> {
				if (slotIndex == 49) {
					ProgressionMenuController.openCollectionLevels(
						serverPlayer,
						this.collectionType,
						0,
						this.detailItemId
					);
				}
			}
			case GLOBAL_LEVEL -> {
				if (slotIndex == 49) {
					ProgressionMenuController.open(serverPlayer, Page.HOME);
				}
			}
			case BESTIARY -> {
				if (slotIndex == 49) {
					ProgressionMenuController.openSkillLevels(serverPlayer, PlayerProgression.Skill.COMBAT, 0);
				}
			}
			case HEART_OF_THE_MOUNTAIN -> {
				if (slotIndex == 49) {
					ProgressionMenuController.openSkillLevels(serverPlayer, PlayerProgression.Skill.MINING, 0);
				}
			}
		}
	}

	private void handleNavigation(ServerPlayer player, int slotIndex) {
		if (slotIndex == 45) {
			ProgressionMenuController.open(player, Page.HOME);
		} else if (this.page == Page.COLLECTIONS) {
			int categoryIndex = slotIndex - 20;
			PlayerProgression.CollectionType[] categories = PlayerProgression.CollectionType.values();
			if (categoryIndex >= 0 && categoryIndex < categories.length) {
				ProgressionMenuController.openCollectionItems(player, categories[categoryIndex], 0);
			}
		}
	}

	private void handleSkillLevelClick(ServerPlayer player, int slotIndex) {
		PlayerProgression.Skill skill = this.page == Page.SKILL_LEVELS
			? PlayerProgression.Skill.valueOf(this.detailItemId)
			: this.page == Page.COMBAT_LEVELS ? PlayerProgression.Skill.COMBAT : PlayerProgression.Skill.MINING;
		if (slotIndex == 45) {
			ProgressionMenuController.open(player, Page.SKILLS);
			return;
		}
		if (slotIndex == 48 && this.collectionPage > 0) {
			ProgressionMenuController.openSkillLevels(player, skill, this.collectionPage - 1);
		} else if (slotIndex == 53 && (this.collectionPage + 1) * LEVELS_PER_PAGE < 60) {
			ProgressionMenuController.openSkillLevels(player, skill, this.collectionPage + 1);
		} else if (slotIndex == 49) {
			player.closeContainer();
		} else if (slotIndex == 50) {
			ProgressionMenuController.openGlobalLevel(player);
		} else if (slotIndex == 39 && skill == PlayerProgression.Skill.COMBAT) {
			ProgressionMenuController.open(player, Page.BESTIARY);
		} else if (slotIndex == 39 && skill == PlayerProgression.Skill.MINING) {
			ProgressionMenuController.open(player, Page.HEART_OF_THE_MOUNTAIN);
		} else {
			for (int index = 0; index < LEVEL_SLOTS.length; index++) {
				if (slotIndex == LEVEL_SLOTS[index]) {
					int level = this.collectionPage * LEVELS_PER_PAGE + index + 1;
					if (level <= 60) {
						ProgressionMenuController.openSkillLevelDetail(player, skill, level, this.collectionPage);
					}
					return;
				}
			}
		}
	}

	private void openParentSkillLevels(ServerPlayer player) {
		String[] details = this.detailItemId.split(":", 2);
		PlayerProgression.Skill skill = PlayerProgression.Skill.valueOf(details[0]);
		int page = details.length == 2 ? Integer.parseInt(details[1]) : 0;
		ProgressionMenuController.openSkillLevels(player, skill, page);
	}

	private static int indexOf(int[] values, int value) {
		for (int index = 0; index < values.length; index++) {
			if (values[index] == value) {
				return index;
			}
		}
		return -1;
	}

	private void handleCollectionLevelClick(ServerPlayer player, int slotIndex) {
		if (slotIndex == 45) {
			ProgressionMenuController.openCollectionDetail(
				player, this.collectionType, 0, this.detailItemId
			);
			return;
		}
		if (slotIndex >= 0 && slotIndex < CollectionProgression.milestones(this.detailItemId).size()) {
			ProgressionMenuController.openCollectionReward(
				player, this.collectionType, 0, this.detailItemId, slotIndex + 1
			);
		}
	}

	private void handleCollectionClick(ServerPlayer player, int slotIndex) {
		if (slotIndex == 45) {
			ProgressionMenuController.open(player, Page.COLLECTIONS);
			return;
		}
		if (slotIndex == 49) {
			player.closeContainer();
			return;
		}
		if (slotIndex == 48 && this.collectionPage > 0) {
			ProgressionMenuController.openCollectionItems(player, this.collectionType, this.collectionPage - 1);
		} else if (slotIndex == 50) {
			int itemCount = ProgressionMenuController.getCollectionItems(player, this.collectionType).size();
			if ((this.collectionPage + 1) * COLLECTION_ITEMS_PER_PAGE < itemCount) {
				ProgressionMenuController.openCollectionItems(player, this.collectionType, this.collectionPage + 1);
			}
		} else if (slotIndex >= 0 && slotIndex < 54) {
			int itemSlot = -1;
			for (int index = 0; index < COLLECTION_SLOTS.length; index++) {
				if (COLLECTION_SLOTS[index] == slotIndex) {
					itemSlot = index;
					break;
				}
			}
			if (itemSlot < 0) {
				return;
			}
			List<Item> items = ProgressionMenuController.getCollectionItems(player, this.collectionType);
			int index = this.collectionPage * COLLECTION_ITEMS_PER_PAGE + itemSlot;
			if (index < items.size()) {
				String itemId = CollectionRegistry.getItemId(items.get(index));
				ProgressionMenuController.openCollectionDetail(player, this.collectionType, this.collectionPage, itemId);
			}
		}
	}

	@Override
	public ItemStack quickMoveStack(Player player, int slotIndex) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	private static SimpleContainer createContainer(
		ServerPlayer player,
		Page page,
		int collectionPage,
		String detailItemId,
		PlayerProgression.CollectionType collectionType
	) {
		SimpleContainer container = new SimpleContainer(54);
		for (int slot = 0; slot < container.getContainerSize(); slot++) {
			set(container, slot, Items.STAINED_GLASS_PANE.gray(), " ", ChatFormatting.GRAY);
		}
		switch (page) {
			case HOME -> {
				set(container, 20, Items.BOOK, "Your Skills", ChatFormatting.GREEN);
				setGlobalLevelSelector(container, 22, player);
				set(container, 24, Items.CHEST, "Collections", ChatFormatting.GREEN);
				set(container, 49, Items.BARRIER, "Close menu", ChatFormatting.RED);
			}
			case SKILLS -> {
				PlayerProgression.Skill[] skills = PlayerProgression.Skill.values();
				for (int index = 0; index < skills.length; index++) {
					setSkillSelector(container, SKILL_MENU_SLOTS[index], SKILL_MENU_ICONS[index], player, skills[index]);
				}
				set(container, 45, Items.ARROW, "Back", ChatFormatting.YELLOW);
			}
			case COLLECTIONS -> {
				PlayerProgression.CollectionType[] categories = PlayerProgression.CollectionType.values();
				Item[] icons = {Items.IRON_PICKAXE, Items.IRON_AXE, Items.IRON_SWORD, Items.WHEAT, Items.FISHING_ROD};
				ChatFormatting[] colors = {
					ChatFormatting.GOLD, ChatFormatting.DARK_GREEN, ChatFormatting.RED,
					ChatFormatting.YELLOW, ChatFormatting.AQUA
				};
				int unlocked = CollectionRegistry.getUnlockedCount(player, null);
				int available = CollectionRegistry.getAvailableCount(null);
				set(container, 4, Items.CHEST, "Collections", ChatFormatting.GREEN,
					"View all your item Collections.",
					"Unlocked: " + unlocked + "/" + available);
				for (int index = 0; index < categories.length; index++) {
					int count = CollectionRegistry.getAvailableCount(categories[index]);
					int unlockedInCategory = CollectionRegistry.getUnlockedCount(player, categories[index]);
					set(container, 20 + index, icons[index],
						categories[index].displayName() + " Collections", colors[index],
						"View your " + categories[index].displayName() + " Collections!",
						"Unlocked: " + unlockedInCategory + "/" + count,
						"Click to view!");
				}
				set(container, 45, Items.ARROW, "Back", ChatFormatting.YELLOW);
			}
			case COLLECTION_ITEMS -> {
				List<Item> items = ProgressionMenuController.getCollectionItems(player, collectionType);
				int start = Math.min(collectionPage * COLLECTION_ITEMS_PER_PAGE, items.size());
				int end = Math.min(start + COLLECTION_ITEMS_PER_PAGE, items.size());
				set(container, 4, collectionCategoryIcon(collectionType),
					collectionCategoryName(collectionType) + " Collections", ChatFormatting.GREEN,
					"View your " + collectionCategoryName(collectionType) + " Collections!");
				for (int index = start; index < end; index++) {
					Item item = items.get(index);
					String itemId = CollectionRegistry.getItemId(item);
					ItemStack stack = CollectionRegistry.createStack(itemId);
					long count = collectionType == null
						? PlayerProgression.getCollectionCount(player, itemId)
						: PlayerProgression.getCollectionCount(player, itemId, collectionType);
					if (count > 0) {
						stack.setCount((int) Math.min(item.getDefaultMaxStackSize(), count));
					}
					stack.set(DataComponents.LORE, collectionItemTooltip(player, itemId, count));
					container.setItem(COLLECTION_SLOTS[index - start], stack);
				}
				set(container, 45, Items.ARROW, "Back", ChatFormatting.YELLOW);
				set(container, 49, Items.BARRIER, "Close menu", ChatFormatting.RED);
				if (collectionPage > 0) {
					set(container, 48, Items.ARROW, "Previous page", ChatFormatting.YELLOW);
				}
				if (end < items.size()) {
					set(container, 50, Items.ARROW, "Next page", ChatFormatting.YELLOW);
				}
				set(container, 53, Items.PAPER, "Page " + (collectionPage + 1), ChatFormatting.GRAY);
			}
			case COLLECTION_DETAIL -> {
				ItemStack stack = CollectionRegistry.createStack(detailItemId);
				String itemId = detailItemId;
				long count = collectionType == null
					? PlayerProgression.getCollectionCount(player, itemId)
					: PlayerProgression.getCollectionCount(player, itemId, collectionType);
				stack.set(DataComponents.CUSTOM_NAME, Component.literal("Collected: " + count).withStyle(ChatFormatting.GREEN));
				int level = CollectionProgression.getLevel(itemId, count);
				List<CollectionProgression.Milestone> milestones = CollectionProgression.milestones(itemId);
				List<String> lore = new ArrayList<>();
				lore.add("Collection Level " + SkillProgression.roman(level));
				if (level < milestones.size()) {
					CollectionProgression.Milestone next = milestones.get(level);
					lore.add("Next: " + next.required() + " items");
					lore.add(next.reward());
				} else {
					lore.add(milestones.isEmpty()
						? "Collection rewards are not available yet."
						: "Maximum collection level reached");
				}
				stack.set(DataComponents.LORE, lore(lore));
				container.setItem(22, stack);
				set(container, 13, Items.PAPER, "Collection Milestones", ChatFormatting.GREEN,
					"View all levels and rewards");
				set(container, 49, Items.ARROW, "Back to Collections", ChatFormatting.YELLOW);
			}
			case SKILL_LEVELS, COMBAT_LEVELS, MINING_LEVELS -> {
				PlayerProgression.Skill skill = page == Page.SKILL_LEVELS
					? PlayerProgression.Skill.valueOf(detailItemId)
					: page == Page.COMBAT_LEVELS ? PlayerProgression.Skill.COMBAT : PlayerProgression.Skill.MINING;
				int currentLevel = PlayerProgression.getLevel(player, skill);
				for (int slot = 0; slot < 45; slot++) {
					set(container, slot, Items.STAINED_GLASS_PANE.gray(), " ", ChatFormatting.GRAY);
				}
				int firstLevel = collectionPage * LEVELS_PER_PAGE + 1;
				for (int index = 0; index < LEVEL_SLOTS.length; index++) {
					int level = firstLevel + index;
					if (level > 60) {
						break;
					}
					boolean unlocked = level <= currentLevel;
					Item icon = level == 1
						? skillIcon(skill)
						: level % 5 == 0
							? (skill == PlayerProgression.Skill.COMBAT ? Items.DIAMOND_HELMET
								: skill == PlayerProgression.Skill.MINING ? Items.IRON_BLOCK : SKILL_MENU_ICONS[skill.ordinal()])
							: unlocked ? Items.STAINED_GLASS_PANE.lime() : Items.STAINED_GLASS_PANE.gray();
					ItemStack levelStack = menuIcon(icon);
					levelStack.setCount(level);
					levelStack.set(DataComponents.CUSTOM_NAME, noItalic(Component.literal(
						skill.displayName() + " Level " + SkillProgression.roman(level))
						.withStyle(unlocked ? ChatFormatting.GREEN : ChatFormatting.GRAY)));
					levelStack.set(DataComponents.LORE, levelTooltip(player, skill, level, unlocked));
					container.setItem(LEVEL_SLOTS[index], levelStack);
				}
				if (skill == PlayerProgression.Skill.COMBAT) {
					set(container, 39, Items.BOOK, "Bestiary", ChatFormatting.AQUA,
						"The Bestiary is a compendium of mobs in SkyBlock.",
						"It contains detailed information on loot drops, your mob stats, and more!",
						"Kill mobs within Families to progress and earn rewards,",
						"including Magic Find bonuses towards mobs in the Family.",
						"Click to view!");
				} else if (skill == PlayerProgression.Skill.MINING) {
					setHeartOfTheMountainSelector(container, 39);
				}
				set(container, 45, Items.ARROW, "Back to Skills", ChatFormatting.YELLOW);
				if (collectionPage > 0) {
					set(container, 48, Items.ARROW, "Previous page", ChatFormatting.YELLOW);
				}
				set(container, 49, Items.BARRIER, "Close menu", ChatFormatting.RED);
				setGlobalLevelSelector(container, 50, player);
				if ((collectionPage + 1) * LEVELS_PER_PAGE < 60) {
					set(container, 53, Items.ARROW, "Next page", ChatFormatting.YELLOW);
				}
			}
			case SKILL_LEVEL_DETAIL, COMBAT_LEVEL_DETAIL, MINING_LEVEL_DETAIL -> {
				PlayerProgression.Skill skill = page == Page.SKILL_LEVEL_DETAIL
					? PlayerProgression.Skill.valueOf(detailItemId.split(":", 2)[0])
					: page == Page.COMBAT_LEVEL_DETAIL ? PlayerProgression.Skill.COMBAT : PlayerProgression.Skill.MINING;
				int level = collectionPage;
				ItemStack levelStack = menuIcon(skillIcon(skill));
				levelStack.set(DataComponents.CUSTOM_NAME, Component.literal(skill.displayName() + " "
					+ SkillProgression.roman(level)).withStyle(ChatFormatting.GREEN)
					.withStyle(style -> style.withItalic(false)));
				levelStack.set(DataComponents.LORE, levelTooltip(
					player, skill, level, level <= PlayerProgression.getLevel(player, skill)));
				container.setItem(22, levelStack);
				set(container, 49, Items.ARROW, "Back to levels", ChatFormatting.YELLOW);
			}
			case BESTIARY -> {
				set(container, 22, Items.BOOK, "Bestiary", ChatFormatting.AQUA,
					"The Bestiary is a compendium of mobs in SkyBlock.",
					"It contains detailed information on loot drops, your mob stats, and more!",
					"Kill mobs within Families to progress and earn rewards,",
					"including Magic Find bonuses towards mobs in the Family.");
				set(container, 49, Items.ARROW, "Back to Combat Skill", ChatFormatting.YELLOW);
			}
			case HEART_OF_THE_MOUNTAIN -> {
				setHeartOfTheMountainSelector(container, 22);
				set(container, 49, Items.ARROW, "Back to Mining Skill", ChatFormatting.YELLOW);
			}
			case COLLECTION_LEVELS -> {
				long count = collectionType == null
					? PlayerProgression.getCollectionCount(player, detailItemId)
					: PlayerProgression.getCollectionCount(player, detailItemId, collectionType);
				List<CollectionProgression.Milestone> milestones = CollectionProgression.milestones(detailItemId);
				int currentLevel = CollectionProgression.getLevel(detailItemId, count);
				Item item = getCollectionItem(detailItemId);
				if (milestones.isEmpty()) {
					set(container, 22, item, "Collection Milestones", ChatFormatting.GREEN,
						"Milestone and reward data has not been verified from the Wiki yet.",
						"Collected: " + count);
				}
				for (int index = 0; index < milestones.size() && index < PAGE_SIZE; index++) {
					CollectionProgression.Milestone milestone = milestones.get(index);
					boolean unlocked = currentLevel >= index + 1;
					set(container, index,
						unlocked ? Items.STAINED_GLASS_PANE.lime() : Items.STAINED_GLASS_PANE.gray(),
						"Collection " + SkillProgression.roman(index + 1),
						unlocked ? ChatFormatting.GREEN : ChatFormatting.GRAY,
						"Requires " + milestone.required() + " items",
						milestone.reward(),
						"Your progress: " + count);
				}
				set(container, 50, item, "Collection Milestones", ChatFormatting.GREEN,
					"Current level: " + SkillProgression.roman(currentLevel),
					"Total collected: " + count);
				set(container, 45, Items.ARROW, "Back to collection", ChatFormatting.YELLOW);
			}
			case COLLECTION_REWARD -> {
				int level = collectionPage;
				CollectionProgression.Milestone milestone = CollectionProgression.milestones(detailItemId)
					.get(level - 1);
				ItemStack rewardStack = getCollectionItem(detailItemId).getDefaultInstance();
				rewardStack.set(DataComponents.CUSTOM_NAME, Component.literal(
					"Collection " + SkillProgression.roman(level)).withStyle(ChatFormatting.GREEN));
				rewardStack.set(DataComponents.LORE, lore(
					"Required items: " + milestone.required(),
					milestone.reward()));
				container.setItem(22, rewardStack);
				set(container, 31, Items.PAPER, "Current progress: "
					+ (collectionType == null
						? PlayerProgression.getCollectionCount(player, detailItemId)
						: PlayerProgression.getCollectionCount(player, detailItemId, collectionType)), ChatFormatting.AQUA);
				set(container, 49, Items.ARROW, "Back to milestones", ChatFormatting.YELLOW);
			}
			case GLOBAL_LEVEL -> {
				int level = PlayerProgression.getSkyBlockLevel(player);
				setGlobalLevelSelector(container, 4, player);
				set(container, 16, Items.REDSTONE_TORCH, "SkyBlock Leveling Rewards", ChatFormatting.GREEN,
					"View your SkyBlock Level rewards.",
					"Click to view!");
				set(container, 20, Items.EMERALD, "Ways to Gain SkyBlock XP", ChatFormatting.GREEN,
					"View the ways you can earn SkyBlock XP.",
					"Click to view!");
				set(container, 22, Items.BOOK, "SkyBlock XP Guide", ChatFormatting.GREEN,
					"Learn how to earn SkyBlock XP.",
					"Click to view!");
				set(container, 24, Items.NETHER_STAR, "SkyBlock Level Rewards", ChatFormatting.GREEN,
					"Your current level: " + level,
					"\u2665 Health bonus: +" + level * 5,
					"\u2694 Strength bonus: +" + PlayerProgression.getSkyBlockStrength(player));
				set(container, 49, Items.ARROW, "Back to Main Menu", ChatFormatting.YELLOW);
			}
		}
		return container;
	}

	private static Item getCollectionItem(String itemId) {
		return BuiltInRegistries.ITEM.get(net.minecraft.resources.Identifier.parse(itemId))
			.orElseThrow(() -> new IllegalStateException("Unknown collection item: " + itemId)).value();
	}

	private static ItemLore lore(List<String> lines) {
		return new ItemLore(lines.stream()
			.<Component>map(line -> noItalic(Component.literal(line)))
			.toList());
	}

	private static ItemLore lore(String... lines) {
		return lore(List.of(lines));
	}

	private static ItemLore skillTooltip(Player player, PlayerProgression.Skill skill) {
		int currentLevel = PlayerProgression.getLevel(player, skill);
		int rewardLevel = Math.min(currentLevel + 1, 60);
		long progress = PlayerProgression.getExperienceWithinLevel(player, skill);
		long required = PlayerProgression.getExperienceForNextLevel(player, skill);
		double percentage = required == 0 ? 100.0 : Math.min(100.0, progress * 100.0 / required);
		String description = switch (skill) {
			case COMBAT -> "Fight mobs and special bosses to earn Combat EXP!";
			case MINING -> "Mine ores and valuable materials to earn Mining EXP!";
			case FARMING -> "Harvest crops and farm animals to earn Farming EXP!";
			case FORAGING -> "Chop trees to earn Foraging EXP!";
			case FISHING -> "Fish and catch sea creatures to earn Fishing EXP!";
			case ENCHANTING -> "Enchant items to earn Enchanting EXP!";
			case ALCHEMY -> "Brew potions to earn Alchemy EXP!";
			case TAMING -> "Tame animals to earn Taming EXP!";
		};
		List<Component> lines = new ArrayList<>();
		lines.add(noItalic(Component.literal(description).withStyle(ChatFormatting.GRAY)));
		lines.add(Component.empty());
		lines.add(noItalic(Component.literal("Progress to Level " + SkillProgression.roman(rewardLevel) + ": ")
			.withStyle(ChatFormatting.GRAY)
			.append(Component.literal(String.format(java.util.Locale.ROOT, "%.1f%%", percentage))
				.withStyle(ChatFormatting.YELLOW))));
		if (required > 0) {
			int filled = (int) Math.round(percentage * 30.0 / 100.0);
			lines.add(noItalic(Component.literal("=".repeat(filled)).withStyle(ChatFormatting.GREEN)
				.append(Component.literal("-".repeat(30 - filled)).withStyle(ChatFormatting.WHITE))));
			lines.add(noItalic(Component.literal(formatNumber(progress) + "/" + formatRequired(required) + " XP")
				.withStyle(ChatFormatting.YELLOW)));
		} else {
			lines.add(noItalic(Component.literal("MAX LEVEL").withStyle(ChatFormatting.GOLD)));
		}
		lines.add(Component.empty());
			if (currentLevel > 0) {
				if (skill == PlayerProgression.Skill.COMBAT) {
					lines.add(noItalic(Component.literal("Warrior " + SkillProgression.roman(currentLevel))
						.withStyle(ChatFormatting.YELLOW)));
					lines.add(statLine("\u2694", ChatFormatting.RED,
						Component.literal("Deal " + PlayerProgression.getCombatDamageBonus(player)
							+ "% more damage to mobs").withStyle(ChatFormatting.GRAY)));
					lines.add(statLine("\u26a1", ChatFormatting.BLUE,
						Component.literal(String.format(java.util.Locale.ROOT, "+%.1f%% Crit Chance",
							PlayerProgression.getCombatCriticalChance(player))).withStyle(ChatFormatting.BLUE)));
				} else if (skill == PlayerProgression.Skill.MINING) {
					lines.add(noItalic(Component.literal("Spelunker " + SkillProgression.roman(currentLevel))
						.withStyle(ChatFormatting.YELLOW)));
					lines.add(statLine("\u26cf", ChatFormatting.GOLD,
						Component.literal("+" + PlayerProgression.getMiningFortune(player)
							+ " Mining Fortune").withStyle(ChatFormatting.GREEN)));
					lines.add(statLine("\u26e8", ChatFormatting.GREEN,
						Component.literal("+" + PlayerProgression.getMiningDefense(player)
							+ " Defense").withStyle(ChatFormatting.GREEN)));
				} else {
					for (String reward : SkillProgression.rewards(player, skill, currentLevel)) {
						if (reward.endsWith("SkyBlock XP") || reward.contains("Coins")) {
							continue;
						}
						lines.add(skillRewardLine(reward));
					}
				}
			} else {
				lines.add(noItalic(Component.literal("Reach Level I to unlock skill bonuses.")
					.withStyle(ChatFormatting.GRAY)));
			}
			lines.add(Component.empty());
			lines.add(noItalic(Component.literal("Increase your " + skill.displayName()
				+ " Level to unlock perks, stat bonuses, and more!").withStyle(ChatFormatting.DARK_GRAY)));
			lines.add(Component.empty());
			lines.add(noItalic(Component.literal("Click to view!").withStyle(ChatFormatting.YELLOW)));
			return new ItemLore(lines);
	}

	private static ItemLore levelTooltip(
		Player player,
		PlayerProgression.Skill skill,
		int level,
		boolean unlocked
	) {
		List<Component> lines = new ArrayList<>();
		lines.add(noItalic(Component.literal("Rewards:").withStyle(ChatFormatting.GRAY)));
		for (String reward : SkillProgression.rewards(player, skill, level)) {
			lines.add(skillRewardLine(reward));
		}
		lines.add(Component.empty());
		int currentLevel = PlayerProgression.getLevel(player, skill);
		if (level == currentLevel + 1 && level <= 60) {
			long progress = PlayerProgression.getExperienceWithinLevel(player, skill);
			long required = PlayerProgression.getExperienceForNextLevel(player, skill);
			double percentage = required == 0 ? 100.0 : Math.min(100.0, progress * 100.0 / required);
			lines.add(noItalic(Component.literal(String.format(java.util.Locale.ROOT,
				"Progress: %.1f%%", percentage)).withStyle(ChatFormatting.GRAY)));
			lines.add(progressBar(percentage, ChatFormatting.GREEN));
			lines.add(noItalic(Component.literal(formatNumber(progress) + "/"
				+ formatRequired(required) + " XP").withStyle(ChatFormatting.YELLOW)));
			lines.add(Component.empty());
		}
		lines.add(noItalic(Component.literal(unlocked ? "UNLOCKED" : "LOCKED")
			.withStyle(unlocked ? ChatFormatting.GREEN : ChatFormatting.RED)));
		return new ItemLore(lines);
	}

	private static ItemLore skyBlockLevelTooltip(Player player) {
		int level = PlayerProgression.getSkyBlockLevel(player);
		int progress = PlayerProgression.getSkyBlockExperienceWithinLevel(player);
		List<Component> lines = new ArrayList<>();
		lines.add(noItalic(Component.literal("Your SkyBlock Level: [" + level + "]")
			.withStyle(ChatFormatting.GRAY)));
		lines.add(Component.empty());
		lines.add(noItalic(Component.literal("Determine how far you've")
			.withStyle(ChatFormatting.GRAY)));
		lines.add(noItalic(Component.literal("progressed in SkyBlock and earn")
			.withStyle(ChatFormatting.GRAY)));
		lines.add(noItalic(Component.literal("rewards from completing unique tasks.")
			.withStyle(ChatFormatting.GRAY)));
		lines.add(Component.empty());
		lines.add(noItalic(Component.literal("Progress to Level " + (level + 1) + ":")
			.withStyle(ChatFormatting.GRAY)));
		int filled = progress * 30 / 100;
		lines.add(noItalic(Component.literal("=".repeat(filled)).withStyle(ChatFormatting.DARK_AQUA)
			.append(Component.literal("-".repeat(30 - filled)).withStyle(ChatFormatting.WHITE))
			.append(Component.literal(" " + progress + "/100 XP").withStyle(ChatFormatting.AQUA))));
		lines.add(Component.empty());
		lines.add(noItalic(Component.literal("Also accessible via /levels")
			.withStyle(ChatFormatting.DARK_GRAY)));
		lines.add(Component.empty());
		lines.add(noItalic(Component.literal("Click to view!")
			.withStyle(ChatFormatting.YELLOW)));
		return new ItemLore(lines);
	}

	private static ItemLore collectionItemTooltip(Player player, String itemId, long count) {
		List<CollectionProgression.Milestone> milestones = CollectionProgression.milestones(itemId);
		List<Component> lines = new ArrayList<>();
		int level = CollectionProgression.getLevel(itemId, count);
		lines.add(noItalic(Component.literal("Collection Level " + SkillProgression.roman(level))
			.withStyle(ChatFormatting.GRAY)));
		lines.add(Component.empty());
		if (milestones.isEmpty()) {
			lines.add(noItalic(Component.literal("Milestone and reward data has not")
				.withStyle(ChatFormatting.DARK_GRAY)));
			lines.add(noItalic(Component.literal("been verified from the Wiki yet.")
				.withStyle(ChatFormatting.DARK_GRAY)));
			lines.add(noItalic(Component.literal("Collected: " + formatNumber(count))
				.withStyle(ChatFormatting.YELLOW)));
		} else if (level < milestones.size()) {
			CollectionProgression.Milestone next = milestones.get(level);
			long previousRequirement = level == 0 ? 0 : milestones.get(level - 1).required();
			double percentage = Math.min(100.0, (count - previousRequirement) * 100.0
				/ (next.required() - previousRequirement));
			Item item = BuiltInRegistries.ITEM.get(
				net.minecraft.resources.Identifier.parse(itemId)
			).orElseThrow(() -> new IllegalStateException("Unknown collection item: " + itemId))
				.value();
			String itemName = item.getName(item.getDefaultInstance()).getString();
			lines.add(noItalic(Component.literal("Progress to " + itemName + " "
				+ SkillProgression.roman(level + 1) + ": "
				+ String.format(java.util.Locale.ROOT, "%.1f%%", percentage))
				.withStyle(ChatFormatting.GRAY)));
			lines.add(progressBar(percentage, ChatFormatting.DARK_AQUA));
			lines.add(noItalic(Component.literal(formatNumber(count) + "/" + formatRequired(next.required()))
				.withStyle(ChatFormatting.YELLOW)));
			lines.add(Component.empty());
			lines.add(noItalic(Component.literal(itemName + " "
				+ SkillProgression.roman(level + 1) + " Rewards:").withStyle(ChatFormatting.GRAY)));
			for (String reward : next.reward().split("; ")) {
				lines.add(noItalic(Component.literal(reward).withStyle(ChatFormatting.GREEN)));
			}
		} else {
			lines.add(noItalic(Component.literal("MAX COLLECTION LEVEL")
				.withStyle(ChatFormatting.GOLD)));
			lines.add(noItalic(Component.literal("Collected: " + formatNumber(count))
				.withStyle(ChatFormatting.YELLOW)));
		}
		lines.add(Component.empty());
		lines.add(noItalic(Component.literal("Click to view!").withStyle(ChatFormatting.YELLOW)));
		return new ItemLore(lines);
	}

	private static ItemLore heartOfTheMountainTooltip() {
		return new ItemLore(List.of(
			noItalic(Component.literal("Your Heart of the Mountain contains your")
				.withStyle(ChatFormatting.GRAY)),
			noItalic(Component.literal("progression for the deeper layers of")
				.withStyle(ChatFormatting.GRAY)),
			noItalic(Component.literal("the Deep Caverns!").withStyle(ChatFormatting.GRAY)),
			Component.empty(),
			noItalic(Component.literal("Unlock Tiers by completing Commissions")
				.withStyle(ChatFormatting.GRAY)),
			noItalic(Component.literal("and upgrade perks using Powder.")
				.withStyle(ChatFormatting.GRAY)),
			Component.empty(),
			noItalic(Component.literal("Mithril Powder: 0").withStyle(ChatFormatting.DARK_AQUA)),
			noItalic(Component.literal("Gemstone Powder: 0").withStyle(ChatFormatting.LIGHT_PURPLE)),
			noItalic(Component.literal("Glacite Powder: 0").withStyle(ChatFormatting.BLUE)),
			Component.empty(),
			noItalic(Component.literal("Click to view!").withStyle(ChatFormatting.YELLOW))
		));
	}

	private static Component progressBar(double percentage, ChatFormatting filledColor) {
		int filled = Math.max(0, Math.min(30, (int) Math.round(percentage * 30.0 / 100.0)));
		return noItalic(Component.literal("=".repeat(filled)).withStyle(filledColor)
			.append(Component.literal("-".repeat(30 - filled)).withStyle(ChatFormatting.WHITE)));
	}

	private static Component noItalic(Component component) {
		return component.copy().withStyle(style -> style.withItalic(false));
	}

	private static Component statLine(String icon, ChatFormatting iconColor, Component text) {
		return noItalic(Component.literal(icon + " ").withStyle(iconColor).append(text));
	}

	private static Component skillRewardLine(String reward) {
		if (reward.startsWith("Deal ")) {
			return statLine("\u2694", ChatFormatting.RED,
				Component.literal(reward).withStyle(rewardColor(reward)));
		}
		if (reward.contains("Crit Chance")) {
			return statLine("\u26a1", ChatFormatting.BLUE,
				Component.literal(reward).withStyle(rewardColor(reward)));
		}
		if (reward.contains(" Defense")) {
			return statLine("\u26e8", ChatFormatting.GREEN,
				Component.literal(reward).withStyle(rewardColor(reward)));
		}
		if (reward.contains("Mining Fortune")) {
			return statLine("\u26cf", ChatFormatting.GOLD,
				Component.literal(reward).withStyle(rewardColor(reward)));
		}
		return noItalic(Component.literal(reward).withStyle(rewardColor(reward)));
	}

	private static String formatNumber(long value) {
		return String.format(java.util.Locale.ROOT, "%,d", value);
	}

	private static String formatRequired(long value) {
		if (value >= 1_000_000L) {
			return String.format(java.util.Locale.ROOT, "%.1fm", value / 1_000_000.0);
		}
		if (value >= 100_000L) {
			return (value / 1_000L) + "k";
		}
		return formatNumber(value);
	}

	private static ChatFormatting rewardColor(String reward) {
		if (reward.contains("Coin")) {
			return ChatFormatting.GOLD;
		}
		if (reward.contains("SkyBlock XP")) {
			return ChatFormatting.AQUA;
		}
		if (reward.startsWith("Warrior")) {
			return ChatFormatting.YELLOW;
		}
		if (reward.contains("Crit Chance")) {
			return ChatFormatting.BLUE;
		}
		if (reward.startsWith("Deal ")) {
			return ChatFormatting.WHITE;
		}
		if (reward.startsWith("+Access to")) {
			return ChatFormatting.GREEN;
		}
		if (reward.contains("Defense") || reward.contains("damage") || reward.contains("Fortune")) {
			return ChatFormatting.GREEN;
		}
		if (reward.startsWith("Unlocks:") || reward.startsWith("Access to")) {
			return ChatFormatting.RED;
		}
		return ChatFormatting.GRAY;
	}

	private static ItemStack menuIcon(Item item) {
		ItemStack stack = item.getDefaultInstance();
		stack.remove(DataComponents.ATTRIBUTE_MODIFIERS);
		stack.remove(DataComponents.TOOL);
		return stack;
	}

	private static void setSkillSelector(
		SimpleContainer container,
		int slot,
		Item icon,
		Player player,
		PlayerProgression.Skill skill
	) {
		ItemStack stack = menuIcon(icon);
		stack.set(DataComponents.CUSTOM_NAME, Component.literal(skill.displayName() + " Skill").withStyle(
				skillColor(skill))
			.withStyle(style -> style.withItalic(false)));
		stack.set(DataComponents.LORE, skillTooltip(player, skill));
		container.setItem(slot, stack);
	}

	private static Item skillIcon(PlayerProgression.Skill skill) {
		return switch (skill) {
			case COMBAT -> Items.IRON_SWORD;
			case MINING -> Items.IRON_PICKAXE;
			case FARMING -> Items.GOLDEN_HOE;
			case FORAGING -> Items.IRON_AXE;
			case FISHING -> Items.FISHING_ROD;
			case ENCHANTING -> Items.ENCHANTING_TABLE;
			case ALCHEMY -> Items.BREWING_STAND;
			case TAMING -> Items.BONE;
		};
	}

	private static ChatFormatting skillColor(PlayerProgression.Skill skill) {
		return switch (skill) {
			case COMBAT -> ChatFormatting.RED;
			case MINING -> ChatFormatting.GOLD;
			case FARMING -> ChatFormatting.GREEN;
			case FORAGING -> ChatFormatting.DARK_GREEN;
			case FISHING -> ChatFormatting.AQUA;
			case ENCHANTING -> ChatFormatting.LIGHT_PURPLE;
			case ALCHEMY -> ChatFormatting.BLUE;
			case TAMING -> ChatFormatting.YELLOW;
		};
	}

	private static void setHeartOfTheMountainSelector(SimpleContainer container, int slot) {
		ItemStack stack = menuIcon(Items.AMETHYST_SHARD);
		stack.set(DataComponents.CUSTOM_NAME, noItalic(Component.literal("Heart of the Mountain")
			.withStyle(ChatFormatting.LIGHT_PURPLE)));
		stack.set(DataComponents.LORE, heartOfTheMountainTooltip());
		container.setItem(slot, stack);
	}

	private static Item collectionCategoryIcon(PlayerProgression.CollectionType type) {
		if (type == null) {
			return Items.CHEST;
		}
		return switch (type) {
			case MINING -> Items.IRON_PICKAXE;
			case FORAGING -> Items.IRON_AXE;
			case COMBAT -> Items.IRON_SWORD;
			case FARMING -> Items.WHEAT;
			case FISHING -> Items.FISHING_ROD;
		};
	}

	private static String collectionCategoryName(PlayerProgression.CollectionType type) {
		return type == null ? "All" : type.displayName();
	}

	private static void set(SimpleContainer container, int slot, Item item, String name, ChatFormatting color) {
		ItemStack stack = menuIcon(item);
		stack.set(DataComponents.CUSTOM_NAME, noItalic(Component.literal(name).withStyle(color)));
		container.setItem(slot, stack);
	}

	private static void set(
		SimpleContainer container,
		int slot,
		Item item,
		String name,
		ChatFormatting color,
		String... loreLines
	) {
		ItemStack stack = menuIcon(item);
		stack.set(DataComponents.CUSTOM_NAME, noItalic(Component.literal(name).withStyle(color)));
		if (loreLines.length > 0) {
			stack.set(DataComponents.LORE, lore(loreLines));
		}
		container.setItem(slot, stack);
	}

	private static void set(
		SimpleContainer container,
		int slot,
		Item item,
		String name,
		ChatFormatting color,
		List<String> loreLines
	) {
		ItemStack stack = menuIcon(item);
		stack.set(DataComponents.CUSTOM_NAME, noItalic(Component.literal(name).withStyle(color)));
		if (!loreLines.isEmpty()) {
			stack.set(DataComponents.LORE, lore(loreLines));
		}
		container.setItem(slot, stack);
	}

	public enum Page {
		HOME("SkyBlock Menu"),
		SKILLS("Your Skills"),
		COLLECTIONS("Collections"),
		COLLECTION_ITEMS("Collection Items"),
		COLLECTION_DETAIL("Collection"),
		SKILL_LEVELS("Skill Levels"),
		SKILL_LEVEL_DETAIL("Skill Level"),
		COMBAT_LEVELS("Combat Levels"),
		MINING_LEVELS("Mining Levels"),
		COMBAT_LEVEL_DETAIL("Combat Level"),
		MINING_LEVEL_DETAIL("Mining Level"),
		COLLECTION_LEVELS("Collection Milestones"),
		COLLECTION_REWARD("Collection Reward"),
		GLOBAL_LEVEL("SkyBlock Leveling"),
		BESTIARY("Bestiary"),
		HEART_OF_THE_MOUNTAIN("Heart of the Mountain");

		private final String title;

		Page(String title) {
			this.title = title;
		}

		String title() {
			return this.title;
		}
	}
}
