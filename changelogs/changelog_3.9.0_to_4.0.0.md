# Buildscape - 3.9.0 --> 4.0.0

**Full Changelog**: https://github.com/HoYin1600p/Buildscape/compare/3.9.0...4.0.0

## What's Changed

- Added dedicated Buildscape Advancement Tab (`buildscape:root`) and 44 achievements across Normal, Display, and Seasonal categories (including `Support System`, `Put It On Display`, `Columnist`, `Art Collector`, `Buildscape Museum`, `One More Block`, `Okay One More`, `Actually One Last`, `Reach For The Sky`, `Fixer Upper`, `Hammer Time`, `Jar-ring Display`, `Touch Grass`, `Metalhead`, `That's One Hot Block`, `Think Outside the Box`, `Bookworm`, `Rainbow Mood Light`, `Christmas Every Day`, `Ornamental`, `Light 'em Up`, `Santa's Little Helper`, `A White Christmas`, `A Very Buildscape Christmas`, `The Homemaker Cometh`, `Finally You Can Walk on Lava`, `No More Walking on Lava`, `I Feel Like Jesus`, `Can You Hear Me Now?`, `Colorful Smoke`, `Let It Cascade`, `Let It Out`, `Let It Snow`) by @KyroXova
- Added custom statistics tracking (`buildscape:interact_with_pillar`, `buildscape:hammer_used`, `buildscape:blocks_placed`, `buildscape:hollow_logs_placed`, `buildscape:icicles_placed`, `buildscape:ornaments_placed`, `buildscape:string_lights_placed`, `buildscape:stars_placed`, `buildscape:snowy_leaves_placed`, `buildscape:jars_crafted`, `buildscape:stockings_crafted`) by @KyroXova
- Added Milestone Trophy System framework (`TrophyDefinition`, `TrophyTier`, `TrophyBlock`, `TrophyBlockEntity`) featuring 12 collectible milestone trophies (`Stone Pillar`, `Gold Pillar`, `Diamond Pillar`, `Emerald Pillar`, `Netherite Pillar`, `Gold Ornament`, `Firelight`, `Golden Jar`, `Emerald Stocking`, `Star`, `String Light`), Festive Star (`festive_star`), and Golden Jar (`golden_jar`) by @KyroXova & @HoYin1600p
- Added Festive Enchantment Glint system (`FestiveRenderTypes`, `FestiveGlintAnvilHandler`) and Festive Glint Shard item (`festive_glint_shard`), allowing multi-color festive glint customization via Anvil on weapons, tools, armor, elytra, and tridents by @ItzDGA
- Added Custom Fireworks system (custom firework star shapes, color handling, rocket distance calculation) and custom firework advancement rewards by @ItzDGA
- Configured item insertion requirements and progression logic for Pillar achievements (`put_it_on_display`, `columnist`, `art_collector`, `buildscape_museum`, `support_system`) by @KyroXova
- Added cycling block icons for block placement advancements (`one_more_block`, `okay_one_more`, `actually_one_last`) across all Buildscape block types by @KyroXova
- Added 14 wooden Display Shelf block variants (`oak_shelf` through `poplar_shelf`), Bamboo Shelf, and Stripped Bamboo Shelf with 6x output crafting recipes by @KyroXova & @ItzDGA
- Added Layered Wool blocks and Vertical Slabs in all 16 dyable colors with block drop loot tables and shears/mining support by @KyroXova
- Added 26 Hollow Log block variants (`hollow_oak_log`, `hollow_spruce_log`, `hollow_birch_log`, `hollow_jungle_log`, `hollow_acacia_log`, `hollow_dark_oak_log`, `hollow_mangrove_log`, `hollow_crimson_stem`, `hollow_warped_stem`, `hollow_cherry_log`, `hollow_pale_oak_log`, `hollow_ashpen_log`, `hollow_poplar_log`, Stripped variants) with bare-hand and axe mineable support by @KyroXova
- Added dynamic 6-way Hollow Steel Pipe networking with internal 1x1 hollow passage handshake, automatic rim removal on seamless connected joints, rim retention on exposed endpoints, and strut-frame junction models by @KyroXova
- Added ladder climbing mechanics (`isLadder`) to vertical Hollow Steel Pipes, vertical Hollow Logs, and vertical junctions, allowing upright climbing and sneak suspension without forced crawling by @KyroXova
- Improved player movement inside hollow passages with flexible intersection navigation, allowing players to freely climb up, descend down, or turn sideways at multi-way junctions by @KyroXova
- Added universal modded fluid logging to Hollow Steel Pipes and Hollow Logs with full compatibility for all Forge fluid containers, bucket deposit/pickup, fluid absorption when placed in-world over any fluid source, and dynamic interior rendering for any modded fluid by @KyroXova
- Added standard Forge fluid-system capability (`FluidHandlerItemStack`) to Experience Bucket item stacks, enabling full automation, storage, and transfer support with Applied Energistics 2 (AE2) and Refined Storage by @HoYin1600p
- Prevented fluid replacement and enforced strict single-fluid occupancy per pipe/log blockspace by @KyroXova
- Prevented external ambient fluids from seeping through solid unhollow pipe/log sides by @KyroXova
- Added fluid release on destruction for fluidlogged Hollow Steel Pipes and Hollow Logs, dropping the block and placing flowing fluid in-world by @KyroXova
- Added directional fluid outflow from open pipe endpoints pouring flowing fluid into the surrounding world by @KyroXova
- Added multi-fluid logging for Hollow Logs (Water, Lava, Experience Fluid, mod fluids) with temporary lava-logging lifecycle (5-15s timer when placed directly via bucket) and combustion behavior by @KyroXova
- Configured Hollow Log fluid rendering levels so all fluids (Water, Lava, Experience) use orientation-aware recessed cavity geometry 3 pixels below the top surface by @KyroXova
- Added automatic player crawling handler (`HollowLogCrawlHandler`) setting swimming pose when crouching/sneaking near or inside horizontal hollow log tunnels by @KyroXova
- Added horizontal Hollow Log flower pot and potted plant decoration support with centered flower pot rendering by @KyroXova
- Added vertical Hollow Log flower pot support when bottom face has a glass cover providing a supporting floor by @KyroXova
- Added non-BlockEntity 1x1x1 decorative block placement inside Hollow Logs with 1-pixel recessed block rendering by @KyroXova
- Enforced strict fluid and decoration placement rules: fluids reject interior decorations; interior decorations block fluid logging by @KyroXova
- Added Glass Cover placement against open faces of Hollow Logs using full glass blocks providing solid walking collision, fluid logging protection, and glass item drops upon lava burn expiry by @KyroXova
- Added achievements `Finally You Can Walk on Lava` (`finally_you_can_walk_on_lava`), `No More Walking on Lava` (`no_more_walking_on_lava`), and `I Feel Like Jesus` (`i_feel_like_jesus`) by @KyroXova
- Updated Experience Fluid particle suppression to prevent underwater bubble particles by @KyroXova
- Added 22 new Pillar block variants (`amethyst_pillar`, `andesite_pillar`, `basalt_pillar`, `blackstone_pillar`, `blue_ice_pillar`, `calcite_pillar`, `cinnabar_pillar`, `copper_pillar`, `exposed_copper_pillar`, `weathered_copper_pillar`, `oxidized_copper_pillar`, `dark_prismarine_pillar`, `diorite_pillar`, `dripstone_pillar`, `granite_pillar`, `netherrack_pillar`, `obsidian_pillar`, `packed_mud_pillar`, `prismarine_pillar`, `sculk_pillar`, `sulfur_pillar`, `tuff_pillar`) and break particles by @KyroXova
- Created BDRE (Buildscape Data & Recipe Engine) for centralized recipe management, binary caching, and compacted JSON storage by @KyroXova & @HoYin1600p
- Added 1,869 Buildscape Stonecutter interchangeability recipes for building block families by @KyroXova
- Added Cardboard family blocks and variants (`cardboard_block`, `smooth`, `bundled`, `pressed`, `thick`, `stripped`, `tinted`, `washed`, `burnt`, Slabs, Stairs, Vertical Slabs, Walls, Trapdoors, Buttons, Pressure Plates) by @KyroXova
- Added Slit Copper family (`slit_copper`, `exposed`, `weathered`, `oxidized`, Waxed variants, Stairs, Slabs, Vertical Slabs) by @KyroXova
- Added Flaming Steel set (`flaming_steel_ingot`, `nugget`, `block`, `cut_steel`, `pressed_steel`, `polished_steel`, `casing`, dyed color variants) by @KyroXova
- Added Steel building variants (Steel Panels, Crossed Steel Panels, Steel Mesh Block, Steel Grate, Steel Fan, Steel Bolts, Door, Trapdoor, Button, Pressure Plate) by @ItzDGA
- Added XP Liquid (Experience Fluid), Experience Bucket, Glass Jar, and Cauldron experience fluid integration by @ItzDGA & @KyroXova
- Added Sulfur Geysers with player-launch mechanics and sulfur bubble particle effects by @KyroXova
- Overhauled Builder's Workbench UI, added ghost filter slots, result persistence, pouch reset, and Builder's Pouch integration by @KyroXova
- Added Biome Brushes (`copper_biome_brush`, `diamond_biome_brush`, `netherite_biome_brush`) by @KyroXova
- Added Copper Chest repairs and content integration by @KyroXova
- Added Wrench and Iron/Diamond/Netherite Hammers by @KyroXova
- Added Cauldron icicle growth mechanic and rebalanced Icicle & Sulfur Spike growth rates by @KyroXova
- Added Jars with visible stored item rendering by @KyroXova
- Added Wandering Homemaker entity, custom model, backpack, 2x2 structure summoning logic, and Festive seasonal variant by @KyroXova
- Allowed waxing Eyeblossoms with honeycomb to lock day/night bloom state and updated open eyeblossom item model texture namespace by @KyroXova
- Added Poplar, Cherry, and Pale Oak sapling trades to Wandering Trader by @KyroXova
- Added Copper Buttons and Heavy Weighted Copper Pressure Plates by @KyroXova
- Made water bottles and potions stack up to 16 by @KyroXova
- Added Shulker Box filter slot mechanics, progressive workbench filter application, survival drop filter retention, and graphical 9x3 tooltip container grid with dynamic Shulker Box colors and 25% ghost filter opacity by @KyroXova
- Reorganized Creative Mode Tab in `ModCreativeModeTab.java`, standardizing block/item family sequencing across all sets (`BLOCK -> STAIRS -> SLAB -> VERTICAL_SLAB -> WALL`), placing woodset ladders at the end after `FENCE_GATE`, integrating Cherry/Pale Oak/Ashpen wood walls, and organizing Sculk, Cardboard, Pale Moss, Concrete, Tuff, Flaming Steel, and Wool sets by @KyroXova
