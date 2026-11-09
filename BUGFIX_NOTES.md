# Bugfix pass — 26.3 Fabric

This source update includes:

- Fixed the block-drop crash by marking the `Block.getDrops` return injection cancellable.
- Preserved configured rarity colors for enchanted items registered by this mod.
- Corrected Efficiency Mining Speed values through Efficiency X.
- Corrected Mining Fortune values through Fortune IV.
- Added Protection's SkyBlock Defense values to the Defense calculation for mapped armor.
- Reworked Feather Falling to add five blocks of safe fall height per level and replace vanilla's 12%-per-level reduction with SkyBlock's 5%-per-level reduction.

## Promising Pickaxe model

The supplied source already contains the correct 26.3 item definition at
`src/main/resources/assets/singleplayerhsfeatures/items/promising_pickaxe.json`.
It references `minecraft:item/iron_pickaxe`, exactly like Minecraft's own
`assets/minecraft/items/iron_pickaxe.json`. The corresponding legacy model also
references `minecraft:item/iron_pickaxe`. No incorrect model identifier was
found in the uploaded project, so the remaining runtime rendering issue needs
an in-game screenshot or resource/model loading error to diagnose safely.

## Verification status

The JSON resources and mixin configuration were parsed successfully. A Gradle
build could not be run in this environment: the wrapper needed to download
Gradle 9.8.2, network access was unavailable, and the environment only has
Java 21 while this project targets Java 25. Treat this as a source patch, not a
verified compiled release. Run `gradlew build` with Java 25 in the normal
development environment before installing it.
