# Buildscape - 4.0.3 --> 4.0.4

**Full Changelog**: https://github.com/HoYin1600p/Buildscape/compare/4.0.3...4.0.4

## Highlights

- Fixed an intermittent dedicated-server freeze during startup. Occasionally the server would hang during the initial datapack reload and never finish loading. This was a race condition in vanilla 1.18.2's reload system; Buildscape now makes that part thread-safe, so servers (including large modpack servers) start reliably. Single-player is unaffected apart from the same safety improvement by @HoYin1600p
- Added Haze Bushes in all 16 dye colors, plus potted versions. Start from a White Haze Bush and combine it with a dye to change its color. Haze Bushes release haze particles, and Bottles of Mist are now collected by right-clicking a haze bush with an empty glass bottle (instead of right-clicking in the open air). Once drained, a bush stops giving haze until it recovers by @ItzDGA
- Added Ice Crystals, a new decorative block crafted from an Icicle and Amethyst Shards, and reworked Icicles with new models, textures and placement behavior by @ItzDGA
- Copper Rods now act like Lightning Rods: they attract lightning strikes, and a strike cleans the oxidation off nearby copper by @ItzDGA

## Fixes

- Fixed a crash that could happen when Eyeblossoms switched between their day and night states
- Fixed Copper Rod lightning handling: the redstone pulse is kept, doors and chests are cleaned safely, rods that already exist in the world are recognized correctly, freshly placed copper counts as copper, and Copper Rods can now be mined with a stone pickaxe
- Fixed several Icicle problems: waterlogging, placement and breaking checks in Adventure mode and while sneaking, the falling icicle shell, repair of icicles that lost their attachment, and growth near the bottom of the world
- Fixed the icicle outer shell being drawn underneath dripstone and slabs; it is now only drawn under a full block face
- Fixed Haze Bush issues: the particle effect no longer scans far outside what you can see (better performance), Haze Bushes stack properly when picked up from a pot, they drop the right items, they can be replaced less accidentally (they are no longer treated as replaceable plants), bone meal now plays its sound, and you can sneak to place into a pot
- Fixed the Pillar mob states being left out of clean builds and releases
- Fixed duplicate Haze Bush dye recipes and a duplicate Ice Crystal recipe
- Fixed duplicate Icicle entries in the language file
- Fixed incorrect ingredients in the Packed Mud Pillar and Sculk Pillar recipes, which pointed at the vanilla blocks instead of the Buildscape ones
- Fixed Embeddium failing to download on fresh builds because of a wrong CurseMaven address

## Changes

- Bottles of Mist are obtained from Haze Bushes (see Highlights). Their tooltip now says to right-click a haze bush with an empty glass bottle
- Icicles and Ice Crystals appear in the Buildscape creative tab; the creative tab order was rearranged
- Wandering Homemakers and Hollow Logs received small adjustments related to Haze Bushes
- Recipes are now loaded more strictly: invalid recipes are dropped when the recipe cache loads instead of causing problems

## Textures/Visuals

- Refreshed the Steel Door textures (door, item and Flaming Steel Door) by @ItzDGA
- Updated the hollow metal pipe corner model by @ItzDGA
- New textures for Icicles and Ice Crystals, with a more consistent icicle look
- New textures and a haze particle effect for the Haze Bushes

## Internal

- The recipe cache is now rebuilt automatically whenever recipe files are added or removed, and the bundled cache is verified byte for byte against the recipes at build time, so what ships always matches the recipes by @KyroXova & @HoYin1600p
- Recipe pack files are formatted consistently and normalised to the same line endings, which keeps the bundled cache stable across machines
- Added automated tests covering the recipe cache, Copper Rod logic and Haze Bush resources
