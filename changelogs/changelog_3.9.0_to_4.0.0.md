# Buildscape - 3.9.0 --> 4.0.0

**Full Changelog**: https://github.com/HoYin1600p/Buildscape/compare/3.9.0...4.0.0

## What's Changed

- Add dedicated Buildscape Advancement Tab (`buildscape:root`) and 44 achievements across Normal, Display, and Seasonal categories (including `Support System`, `Put It On Display`, `Columnist`, `Art Collector`, `Buildscape Museum`, `One More Block`, `Okay One More`, `Actually One Last`, `Reach For The Sky`, `Fixer Upper`, `Hammer Time`, `Jar-ring Display`, `Touch Grass`, `Metalhead`, `That's One Hot Block`, `Think Outside the Box`, `Bookworm`, `Rainbow Mood Light`, `Christmas Every Day`, `Ornamental`, `Light 'em Up`, `Santa's Little Helper`, `A White Christmas`, `A Very Buildscape Christmas`, `The Homemaker Cometh`, `Finally You Can Walk on Lava`, `No More Walking on Lava`, `I Feel Like Jesus`) by @KyroXova
- Add custom statistics tracking (`buildscape:interact_with_pillar`, `buildscape:hammer_used`, `buildscape:blocks_placed`, `buildscape:hollow_logs_placed`, `buildscape:icicles_placed`, `buildscape:ornaments_placed`, `buildscape:string_lights_placed`, `buildscape:stars_placed`, `buildscape:snowy_leaves_placed`, `buildscape:jars_crafted`, `buildscape:stockings_crafted`) by @KyroXova
- Add `TEST_TROPHY` reward item (`buildscape:test_trophy`) with diamond texture for milestone achievements by @KyroXova
- Fix item insertion requirement for Pillar achievements (`put_it_on_display`, `columnist`, `art_collector`, `buildscape_museum`) to trigger only upon successful item placement by @KyroXova
- Fix `Support System` advancement logic to track cumulative same-type pillar placement and 4-block vertical columns by @KyroXova
- Add cycling block icons for block placement advancements (`one_more_block`, `okay_one_more`, `actually_one_last`) across all Buildscape block types by @KyroXova
- Add 14 wooden Display Shelf block variants (`oak_shelf` through `poplar_shelf`) and 6x output crafting recipes using stripped wood, stems, and bamboo blocks by @KyroXova
- Add Layered Wool blocks and Vertical Slabs in all 16 dyable colors with block drop loot tables and shears/mining support by @KyroXova
- Add 26 Hollow Log block variants (`hollow_oak_log`, `hollow_spruce_log`, `hollow_birch_log`, `hollow_jungle_log`, `hollow_acacia_log`, `hollow_dark_oak_log`, `hollow_mangrove_log`, `hollow_crimson_stem`, `hollow_warped_stem`, `hollow_cherry_log`, `hollow_pale_oak_log`, `hollow_ashpen_log`, `hollow_poplar_log`, Stripped variants) with bare-hand and axe mineable support by @KyroXova
- Add dynamic 6-way Hollow Steel Pipe networking with internal 1x1 hollow passage handshake, automatic rim removal on seamless connected joints, rim retention on exposed endpoints, and strut-frame junction models by @KyroXova
- Add ladder climbing mechanics (`isLadder`) to vertical Hollow Steel Pipes, vertical Hollow Logs, and vertical junctions, allowing upright climbing and sneak suspension without forced crawling by @KyroXova
- Improve player movement inside hollow passages with flexible intersection navigation, allowing players to freely climb up, descend down, or turn sideways at multi-way junctions by @KyroXova
- Add universal modded fluid logging to Hollow Steel Pipes and Hollow Logs with full compatibility for all Forge fluid containers, bucket deposit/pickup, fluid absorption when placed in-world over any fluid source, and dynamic interior rendering for any modded fluid by @KyroXova
- Prevent fluid replacement and enforce strict single-fluid occupancy per pipe/log blockspace by @KyroXova
- Prevent external ambient fluids from seeping through solid unhollow pipe/log sides by @KyroXova
- Add fluid release on destruction for fluidlogged Hollow Steel Pipes and Hollow Logs, dropping the block and placing flowing fluid in-world by @KyroXova
- Add directional fluid outflow from open pipe endpoints pouring flowing fluid into the surrounding world by @KyroXova
- Add multi-fluid logging for Hollow Logs (Water, Lava, Experience Fluid, mod fluids) with temporary lava-logging lifecycle (5-15s timer when placed directly via bucket) and combustion behavior by @KyroXova
- Fix Hollow Log fluid rendering levels so all fluids (Water, Lava, Experience) use identical orientation-aware recessed cavity geometry 3 pixels below the top surface by @KyroXova
- Add automatic player crawling handler (`HollowLogCrawlHandler`) setting swimming pose when crouching/sneaking near or inside horizontal hollow log tunnels by @KyroXova
- Add horizontal Hollow Log flower pot and potted plant decoration support with centered flower pot rendering by @KyroXova
- Add vertical Hollow Log flower pot support when bottom face has a glass cover providing a supporting floor by @KyroXova
- Add non-BlockEntity 1x1x1 decorative block placement inside Hollow Logs with 1-pixel recessed block rendering by @KyroXova
- Enforce strict fluid and decoration placement rules: fluids reject interior decorations; interior decorations block fluid logging by @KyroXova
- Add Glass Cover placement against open faces of Hollow Logs using full glass blocks providing solid walking collision, fluid logging protection, and glass item drops upon lava burn expiry by @KyroXova
- Add achievements `Finally You Can Walk on Lava` (`finally_you_can_walk_on_lava`), `No More Walking on Lava` (`no_more_walking_on_lava`), and `I Feel Like Jesus` (`i_feel_like_jesus`) by @KyroXova
- Update Experience Fluid particle suppression to prevent underwater bubble particles by @KyroXova
- Add 22 new Pillar block variants (`amethyst_pillar`, `andesite_pillar`, `basalt_pillar`, `blackstone_pillar`, `blue_ice_pillar`, `calcite_pillar`, `cinnabar_pillar`, `copper_pillar`, `exposed_copper_pillar`, `weathered_copper_pillar`, `oxidized_copper_pillar`, `dark_prismarine_pillar`, `diorite_pillar`, `dripstone_pillar`, `granite_pillar`, `netherrack_pillar`, `obsidian_pillar`, `packed_mud_pillar`, `prismarine_pillar`, `sculk_pillar`, `sulfur_pillar`, `tuff_pillar`) and fix break particles by @KyroXova
- Create BDRE (Buildscape Data & Recipe Engine) for centralized recipe management, binary caching, and compacted JSON storage by @KyroXova
- Add 1,869 Buildscape Stonecutter interchangeability recipes for building block families by @KyroXova
- Add Cardboard family blocks and variants (`cardboard_block`, `smooth`, `bundled`, `pressed`, `thick`, `stripped`, `tinted`, `washed`, `burnt`, Slabs, Stairs, Vertical Slabs, Walls, Trapdoors, Buttons, Pressure Plates) by @KyroXova
- Add Slit Copper family (`slit_copper`, `exposed`, `weathered`, `oxidized`, Waxed variants, Stairs, Slabs, Vertical Slabs) by @KyroXova
- Add Flaming Steel set (`flaming_steel_ingot`, `nugget`, `block`, `cut_steel`, `pressed_steel`, `polished_steel`, `casing`, dyed color variants) by @KyroXova
- Add Sulfur Geysers with player-launch mechanics and sulfur bubble particle effects by @KyroXova
- Overhaul Builder's Workbench UI, add ghost filter slots, result persistence, pouch reset, and Builder's Pouch integration by @KyroXova
- Add Biome Brushes (`copper_biome_brush`, `diamond_biome_brush`, `netherite_biome_brush`) by @KyroXova
- Added Copper Chest repairs and content integration by @KyroXova
- Added Wrench and Iron/Diamond/Netherite Hammers by @KyroXova
- Added Cauldron icicle growth mechanic and rebalanced Icicle & Sulfur Spike growth rates by @KyroXova
- Added Jars with visible stored item rendering by @KyroXova
- Add Wandering Homemaker entity, custom model, backpack, 2x2 structure summoning logic, and Festive seasonal variant by @KyroXova
- Allow waxing Eyeblossoms with honeycomb to lock day/night bloom state and fix open eyeblossom item model texture namespace by @KyroXova
- Add Poplar, Cherry, and Pale Oak sapling trades to Wandering Trader by @KyroXova
- Add Copper Buttons and Heavy Weighted Copper Pressure Plates by @KyroXova
- Make water bottles and potions stack up to 16 by @KyroXova
- Add Shulker Box filter slot mechanics, progressive workbench filter application, survival drop filter retention, and graphical 9x3 tooltip container grid with dynamic Shulker Box colors and 25% ghost filter opacity by @KyroXova
