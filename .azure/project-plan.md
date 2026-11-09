# Hypixel SkyBlock Singleplayer Features

## Goal

Extend the existing Fabric mod into a singleplayer port of selected Hypixel SkyBlock features for Minecraft 26.3 and 26.2. Both versions must use the same distributable mod jar. Use the Hypixel SkyBlock Fandom wiki as the primary source for feature behavior, and consult the official Hypixel SkyBlock Wiki where it provides additional or newer details.

## Existing project

- Fabric Loader and Fabric Loom, Java 25, and Minecraft 26.3 are already configured.
- The project contains the Fabric initializer and gameplay mixins.
- There is no existing gameplay implementation or test suite.

## Requirements

1. **One-jar compatibility:** The exact same mod jar must load on Minecraft 26.2 and 26.3. Preserve a shared artifact and use version-aware compatibility code only where necessary. Validate the compatibility path before relying on version-specific Minecraft or Fabric APIs. If a single jar cannot meet a requirement, stop and report the blocker; do not silently create separate jars.
2. **Combat and item behavior:** Remove the attack delay/cooldown for all weapons. Prevent durability loss for all tools and armor in the game.
3. **Health stats:** Add the SkyBlock-style health stat to players and mobs, based on the referenced server behavior.
4. **Mob levels:** Add mob levels following the server's system, using researched and documented rules rather than guessed formulas.
5. **Vanilla armor stats:** Add the relevant SkyBlock stats to vanilla armor after the health and mob-level foundations are in place.
6. **Future armor content:** Keep custom armor and its corresponding custom helmet heads as follow-on scope; use the wiki's item information and verified head textures when that work is planned.

## Delivery sequence

### Phase 0 — Compatibility and reference research

- Confirm Fabric Loader, Fabric API, mappings, metadata, and mixin behavior needed to load one jar on both supported game versions.
- Identify version-sensitive targets before writing mixins, and choose a shared implementation or version-aware selection strategy that keeps one artifact.
- Research the applicable health, mob-level, and armor rules on the Fandom wiki and official wiki; record source pages and unresolved discrepancies in implementation documentation or tests.
- Build and smoke-test the same jar against both versions before feature work depends on the compatibility assumption.

### Phase 1 — Combat cooldown and durability

- Remove vanilla attack cooldown behavior for weapons without changing unrelated combat damage rules.
- Prevent durability consumption for tools and armor; cover normal use and damage paths rather than only one interaction.
- Check behavior with ordinary vanilla items and ensure the implementation is not restricted to a hand-picked item list.

### Phase 2 — Health stats

- Introduce a server-authoritative health-stat model for players and mobs.
- Integrate maximum and current stat health with Minecraft health, damage, healing, death, and entity lifecycle behavior.
- Keep health behavior coherent in singleplayer's integrated server and avoid client/server state divergence.

### Phase 3 — Mob levels

- Implement the researched level assignment and stat behavior for mobs.
- Make level display and any level-dependent health behavior follow the documented SkyBlock rules.
- Keep entity categories or exceptions data-driven if the reference rules require them.

### Phase 4 — Vanilla armor stats

- Map vanilla armor pieces to researched SkyBlock stats.
- Apply and remove armor-derived stats consistently when equipment changes, entities load, or players respawn.
- Verify that the armor stat effects work with the health-stat system and do not duplicate on repeated equipment updates.

### Later scope

- Add SkyBlock armor sets, custom helmet heads, and further features only after the initial phases are complete and separately specified.

## Technical direction

- Continue with the existing Java/Fabric Loom project.
- Keep gameplay state and calculations on the logical server; synchronize only what the client needs to display.
- Prefer Fabric APIs and shared Minecraft behavior where they work on both versions. Isolate unavoidable version-specific mixin targets behind explicit compatibility selection while packaging one jar.
- Avoid introducing an external datastore; persist any required player/entity state using Minecraft's world save mechanisms.

## Verification and acceptance

- Produce one jar and verify that the identical artifact is accepted on Minecraft 26.2 and 26.3.
- Verify that weapons no longer have an attack cooldown and that tools/armor do not lose durability through applicable gameplay paths.
- Verify player and mob health through damage, healing, death, reload, and multiplayer-side synchronization within the integrated server.
- Verify mob level assignment and armor stat mappings against documented wiki examples and exceptions.
- Run the project's Gradle build and focused tests/smoke tests for each supported Minecraft version after implementation.

## Current implementation status

- The same source clean-builds against Minecraft 26.2 and 26.3. The distributable jar is built against 26.3 and its Fabric metadata declares `>=26.2 <26.4`.
- Isolated development clients on both 26.2 and 26.3 loaded the mod and reached a singleplayer world. Comparing both version builds found all 50 non-manifest jar entries identical; only Loom's expected `Fabric-Minecraft-Version` manifest value differs. Retail-launcher testing has not been performed.
- Phase 1: attack-strength cooldown is disabled, and durability loss is cancelled for damageable tool/weapon components and armor equipment slots.
- Phase 2: health is represented in SkyBlock points at a 5:1 ratio to Minecraft health points. Player base health maps from 20 Minecraft points to the wiki's 100 Health. Current/max health is shown in the HUD for the player and targeted mobs. Server-side APIs change current/max/bonus Health and Health Regen; bonus Health and Regen are saved with the player and copied through respawn. The passive Regen formula and one-second interval follow the wiki. Vanilla food/hunger ticking is disabled.
- Phase 3: mobs have a synchronized, saved integer level and show `[LvN]` nameplates. Natural and chunk-generation spawns use profiles from `config/singleplayerhsfeatures/mob_profiles.json`, matched by entity, dimension, optional biome IDs, and optional Y bounds; the first matching profile wins. The generated defaults use the sourced Private Island baseline rows for overworld zombies (Lv1, 100 Health), skeletons (Lv1, 100 Health), and spiders (Lv3, 120 Health). Users can narrow or extend these profiles for their own biome/height regions. A profile can also set optional `attack_damage` points, applied to the mob's vanilla melee attack attribute at the same 5:1 conversion. Other mob types default to level 1 and preserve vanilla health unless configured.
- Phase 4: vanilla leather, golden, chainmail, iron, and diamond armor pieces use the per-piece Defense values documented on their SkyBlock wiki pages. Normal damage reduction follows `damage * 100 / (100 + Defense)`; damage sources tagged to bypass armor also bypass this Defense. Additional item-to-Defense mappings can be added in `config/singleplayerhsfeatures/armor_defense.json`. If any equipped armor piece is unmapped, the full set falls back to vanilla damage reduction.
- Netherite values are estimated by matching its vanilla armor-point profile to diamond (15/40/30/15 Defense). Turtle shell Defense is estimated at 10 using the mapped helmet Defense-per-armor-point ratios; its tooltip marks both estimates. Turtle shell grants 30 Respiration, extending time before air loss by 15 seconds at the documented two-stat-points-per-second rate.
- Item tooltips now show mapped Defense, configured Mining Speed, turtle-shell Respiration, and tracked item collection counts beneath the item name. The HUD shows Health, Defense, Mining Speed, Respiration, Combat level, and Mining level/XP progress. Mob nameplates show level, current/max Health, and recent damage.
- Combat and Mining XP and item collection counts are saved with the player. Mining XP is awarded after a successful survival-mode block break using the wiki resource table; mined drop stacks and player-attributed mob loot increment item collections. Combat XP is awarded once when a mob dies to a player, including player-credited projectiles. Mining and Combat XP gains appear in the action bar. Combat XP is profile-configurable, with a provisional 5 XP fallback for mobs without a positive configured value.
- Weapon/tool tooltips now show a Damage stat derived from main-hand attack attributes. The baseline five SkyBlock points per Minecraft attack-damage point matches the supplied Iron Sword (+30); pickaxes have a +5 adjustment to match Iron Pickaxe (+25). Player-caused hits convert Damage stat points back to Minecraft health units before Defense, preventing the previous extra 5x damage. Post-mitigation mob damage appears as a separate red label above the mob. Mace, Trident, Spear, Bow, and Crossbow values are marked as estimates; this does not add Strength, Crit Damage, or the weapons' specialized attack formulas.
- The mob nameplate is forced through the client renderer and formatted as `[LvN] Name Health/MaxHealth❤`, with level, name, health, and heart colored separately to match the supplied example. Recent damage labels last up to 20 ticks.
- Vanilla pickaxes use the values listed by the Hypixel SkyBlock Wiki: Wooden +70, Stone +110, Iron +160, Golden +250, and Diamond +220 Mining Speed. Netherite is not listed as a SkyBlock item; its +250 estimate scales Diamond's +220 by the Minecraft mining-speed ratio (9/8) and is marked as estimated in its tooltip. Pickaxe break speed scales against Iron's +160 baseline, so Iron retains vanilla mining time and other pickaxes are faster/slower according to their SkyBlock Mining Speed; config entries override defaults.
- A collection catalog is lazily built from registered items when the UI or gameplay first needs it, after item components are bound. It includes modded items and blocks while excluding tools, weapons, armor/equipment, and items with a main-hand attack-damage attribute. Eligible item tooltips say `Collection Item`; mined drops and player-attributed mob drops update saved, separately categorized counts even when no Mining XP mapping exists for the block.
- Right-clicking a Nether Star or entering `/sbmenu` opens an inventory-grid-style menu with Your Skills and Collections pages. Combat and Mining skill pages show level, current XP, XP-to-next, and a progress bar. Collection categories include Mining, Combat, and a paginated catalog of other eligible registered items. Clicking an entry opens its count/progress page. Generic extension milestones currently use 50, 100, 250, 1,000, 5,000, 15,000, 30,000, 50,000, 100,000, 250,000, 500,000, and 1,000,000 items; rewards and per-item Hypixel milestone overrides are not implemented yet.
- The skill XP curve through level 60 follows the community wiki's `Skill/XP Table`. Mining Speed scales Minecraft's ordinary block-destroy speed relative to a 160-Speed Iron Pickaxe baseline. This does not reproduce SkyBlock location exceptions, block-strength calibration, instant mining, or the 4-tick softcap.
- The maximum level of supported vanilla enchantments is raised to the levels listed on the wiki: Efficiency X, Sharpness VII, Protection VII, Looting V, Fortune IV, Smite VI, Bane of Arthropods VII, Power VII, Fire Aspect III, and Feather Falling X. These use vanilla effect calculations, which can differ from Hypixel's custom enchantment effects.
- The project has no automated test suite. Both target builds and client-startup smoke tests pass. Focused gameplay checks of combat XP profile awards, collection counts, tooltip rendering, air drain, and damage labels remain outstanding; external Mojang/Realms authentication warnings occurred in the isolated dev clients but did not prevent world entry.

#

### Promising Pickaxe implementation

- Crafting recipe: 3 Iron Blocks across the top row and 2 Sticks down the center column.
- Base stats: +20 Damage, +3 Breaking Power, and +50 Mining Speed; rarity is Uncommon.
- Stored Potential: +10 Mining Speed per 100 blocks mined with the pickaxe, capped at +250 bonus Mining Speed after 2,500 blocks. The total Blocks Mined counter continues increasing after the bonus cap.
- Tooltip shows the Stored Potential ability, its rate and cap, Blocks Mined, the current Stored Potential Mining Speed bonus, reforging text, and Uncommon rarity. Decorative border is intentionally out of scope for this pass.
- The item uses the vanilla iron pickaxe model/texture until a dedicated SkyBlock texture is explicitly added.

## Local configuration

The mod creates these files on first launch:

- `config/singleplayerhsfeatures/mob_profiles.json`: profiles are checked in order; the first matching entity/dimension/biome/Y rule wins. `biomes: []` matches every biome. Put narrower rules before the built-in wildcard rules. Natural and chunk-generation spawns are matched.
- `config/singleplayerhsfeatures/armor_defense.json`: optional per-item Defense additions use registry IDs and per-piece values. Do not add unsupported values without a source.
- `config/singleplayerhsfeatures/mining_speed.json`: optional per-item Mining Speed overrides use `{"item":"namespace:item","mining_speed":100}` entries. Vanilla pickaxes have distinct defaults; config entries override individual items.

Example regional mob profile:

```json
{
  "entity": "minecraft:zombie",
  "dimension": "minecraft:overworld",
  "biomes": ["minecraft:desert"],
  "min_y": 50,
  "max_y": 120,
  "level": 1,
  "health": 100,
  "combat_xp": 0
}
```

`combat_xp` is an optional non-negative integer awarded when a player kills a mob matched by that profile. Positive configured values override the provisional 5 XP default; omitted or zero values use the default. Mining XP is shown only for blocks with a configured/source-backed mapping.

Armor mappings have the form `{"item":"minecraft:iron_helmet","defense":12}` inside the `items` array. The example value is documented for vanilla Iron Armor. Mining Speed mappings use an analogous `items` array with `mining_speed` values.

## Risks and open implementation details

- Minecraft 26.2 and 26.3 may differ in APIs, mappings, mixin targets, or Fabric API compatibility. The same-jar requirement is a hard constraint, so compatibility must be proven in Phase 0 rather than assumed.
- SkyBlock's mobs have many spawn-specific profiles, and its islands/crypts do not exist in a standard Minecraft world. Built-in profiles cover only wiki-documented Private Island baselines for three common mobs; the JSON profile system makes region mapping configurable but does not recreate SkyBlock islands or implement every mob variant, spawn probability, ranged attack, or spawn location.
- Netherite and turtle shell Defense are estimates, not sourced Hypixel item stats. Other vanilla armor without an implemented Defense mapping continues to use vanilla damage reduction.
- Combat XP uses a provisional 5 XP fallback for mobs without a positive profile value; area-specific values need further research.
- Netherite Pickaxe Mining Speed is estimated because the item is not listed as a SkyBlock pickaxe. The current multiplier does not reproduce SkyBlock island exceptions, block-strength calibration, instant mining, or the 4-tick softcap.
- Collection progression tracks drops from mined blocks and player-attributed mob loot only. It does not yet implement collection milestone rewards/unlocks or minion claims.
- The all-item collection catalog does not mean every acquisition source increments counts: inventory transfers, crafting, trades, and minion claims are not tracked. Generic extension milestones are provisional rather than sourced per-item Hypixel tiers and have no rewards. The Nether Star menus approximate the server layout with vanilla inventory-style slots; a later UI pass is still needed for exact icons, layout, and custom textures.
- The Damage conversion is inferred from the supplied vanilla-item tooltip examples, not a universal Hypixel formula. Bow/Crossbow damage uses a fully charged vanilla arrow estimate; Mace, Trident, and Spear estimates use their vanilla item attack attributes and do not model each weapon's special attack behavior.
- Higher enchantment caps retain vanilla enchantment effects. Enchantments whose SkyBlock effects differ materially from vanilla need dedicated gameplay logic before being treated as exact ports.
- Farming/Fishing/Carpentry/Slayer progression systems are not implemented. Call `HealthStat.setBonusHealth` and `HealthRegenStat.set` from future progression/item features to award their verified stat bonuses.
- The official Hypixel SkyBlock Wiki announced its closure in July 2026; use the community-maintained wiki for current research and treat archived official pages as unavailable unless an accessible archive is found.
- Custom helmet head textures and licensing/attribution requirements should be checked when custom armor enters scope.

## Reference sources

- [Hypixel SkyBlock Fandom Wiki](https://hypixel-skyblock.fandom.com/wiki/SkyBlock)
- [Health (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Health)
- [Health Regen (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Health_Regen)
- [Mob (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Mob)
- [Zombie (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Zombie)
- [Skeleton (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Skeleton)
- [Spider (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Spider)
- [Defense (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Defense)
- [Damage Reduction (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Damage_Reduction)
- [Damage (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Damage)
- [Weapons (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Weapons)
- [Collections (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Collection)
- [Emerald Collection (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Emerald)
- [Hypixel Wiki UI depictions](https://hypixel.fandom.com/wiki/Hypixel_Wiki)
- [Armor (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Armor)
- [Mining (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Mining)
- [Mining Speed (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Mining_Speed)
- [Promising Pickaxe (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Promising_Pickaxe)
- [Wooden Pickaxe (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Wooden_Pickaxe)
- [Stone Pickaxe (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Stone_Pickaxe)
- [Iron Pickaxe (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Iron_Pickaxe)
- [Golden Pickaxe (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Golden_Pickaxe)
- [Diamond Pickaxe (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Diamond_Pickaxe)
- [Respiration (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Respiration)
- [Skills XP Table (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Skill/XP_Table)
- [Combat (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Combat)
- [Enchantments (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Enchantments)
- [Efficiency (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Efficiency)
- [Sharpness (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Sharpness)
- [Protection (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Protection)
- [Looting (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Looting)
- [Fortune (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Fortune)
- [Smite (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Smite)
- [Bane of Arthropods (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Bane_of_Arthropods)
- [Power (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Power)
- [Fire Aspect (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Fire_Aspect)
- [Feather Falling (community-maintained Hypixel SkyBlock Wiki)](https://hypixelskyblock.minecraft.wiki/w/Feather_Falling)
- [Official Hypixel SkyBlock Wiki](https://wiki.hypixel.net/)


## Bugfix pass — November 2026

The following source changes have been made after inspecting the supplied 26.3 workspace and runtime log. They still require a Gradle build and in-game verification:

- **Block drops:** `BlockMiningProgressionMixin` now marks its `getDrops` return injection as cancellable. The supplied `run/logs/latest.log` showed the exact failure (`CancellationException: The call getDrops is not cancellable`) when mining a block; this prevented the normal drop flow.
- **SkyBlock item rarity:** custom-namespace item stacks now retain their configured rarity when enchanted, rather than inheriting vanilla's enchanted-rarity upgrade. This preserves the Promising Pickaxe's UNCOMMON green name and supports the planned rarity behavior for enchanted resources.
- **Efficiency:** mining speed bonuses now follow the wiki table (+30 at I, +50 at II, then +20 per level through +210 at X).
- **Fortune:** Mining Fortune now follows the wiki values (+10/+20/+30/+45 for I–IV).
- **Protection:** Protection enchantments on mapped armor now add the wiki's Defense values (+4/+8/+12/+16/+20/+25/+30 for I–VII) to the SkyBlock Defense calculation.
- **Feather Falling:** the current implementation adds five blocks of safe fall height per level and reduces remaining fall damage by five percent per level, through level X.
- **Promising Pickaxe model investigation:** its item-definition file already references `minecraft:item/iron_pickaxe`, matching vanilla's own iron-pickaxe item definition, and the matching legacy model file also references the vanilla iron-pickaxe texture. No incorrect model path was found in this source archive. The remaining visual issue needs an in-game screenshot or a resource/model loading error to distinguish a stale resource reload from an external override or runtime model-resolution issue.

The above changes are not yet marked complete in the verification section because the Gradle wrapper distribution was not available in the current build environment. Remaining enchantments with materially custom SkyBlock behavior (including Looting and Fire Aspect) need their own gameplay implementation rather than being treated as equivalent to vanilla effects.
