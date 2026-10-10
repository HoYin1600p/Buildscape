package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.BuildscapeCommon;

public class BlockDefinition {
    private final String id;
    private final String blockType;
    private final CommonBlockProperties properties;
    private final String parentBlockId;

    public BlockDefinition(String id) {
        this(id, "Block", CommonBlockProperties.of(), null);
    }

    public BlockDefinition(String id, String blockType, CommonBlockProperties properties) {
        this(id, blockType, properties, null);
    }

    public BlockDefinition(String id, String blockType, CommonBlockProperties properties, String parentBlockId) {
        this.id = id;
        this.blockType = blockType != null ? blockType : "Block";
        this.properties = properties != null ? properties : CommonBlockProperties.of();
        this.parentBlockId = parentBlockId;
    }

    private net.minecraft.world.level.block.Block block;

    public net.minecraft.world.level.block.Block getBlock() {
        return block;
    }

    public net.minecraft.world.level.block.state.BlockState defaultBlockState() {
        return block != null ? block.defaultBlockState() : net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
    }

    public void setBlock(net.minecraft.world.level.block.Block block) {
        this.block = block;
    }

    public String getId() {
        return id;
    }

    public String getBlockType() {
        return blockType;
    }

    public CommonBlockProperties getProperties() {
        return properties;
    }

    public String getParentBlockId() {
        return parentBlockId;
    }

    public String getNamespacedId() {
        return BuildscapeCommon.MOD_ID + ":" + id;
    }

    public boolean isStair() { return blockType.contains("Stair"); }
    public boolean isVerticalSlab() { return blockType.contains("VerticalSlab"); }
    public boolean isGrassSlab() { return "GrassSlabBlock".equals(blockType) || "snowy_grass_block_slab".equals(id); }
    public boolean isHayBaleSlab() { return "HayBaleSlabBlock".equals(blockType) || id.contains("hay_bale_slab"); }
    public boolean isLogSlab() { return "LogSlabBlock".equals(blockType) || id.endsWith("_log_slab") || id.endsWith("_wood_slab") || id.endsWith("_stem_slab") || "bamboo_block_slab".equals(id) || "stripped_bamboo_block_slab".equals(id); }
    public boolean isMudSlab() { return "MudSlabBlock".equals(blockType) || "mud_slab".equals(id); }
    public boolean isMud() { return "MudBlock".equals(blockType) || "mud".equals(id); }
    public boolean isSlab() { return !isVerticalSlab() && !isGrassSlab() && !isHayBaleSlab() && !isLogSlab() && !isMudSlab() && blockType.contains("Slab"); }
    public boolean isWall() { return blockType.contains("Wall") && !blockType.contains("WallTorch") && !blockType.contains("Wallpaper"); }
    public boolean isFalling() { return blockType.contains("Falling"); }
    public boolean isCopperDoor() { return !isWeatheringDoor() && ("ModCopperDoorBlock".equals(blockType) || id.contains("copper_door") || "steel_door".equals(id) || "flaming_steel_door".equals(id)); }
    public boolean isDoor() { return !isWeatheringDoor() && !isCopperDoor() && blockType.contains("Door"); }
    public boolean isCopperTrapdoor() { return !isWeatheringTrapdoor() && ("ModCopperTrapdoorBlock".equals(blockType) || id.contains("copper_trapdoor") || "steel_trapdoor".equals(id) || "flaming_steel_trapdoor".equals(id)); }
    public boolean isTrapdoor() { return !isWeatheringTrapdoor() && !isCopperTrapdoor() && blockType.contains("Trapdoor"); }
    public boolean isButton() { return blockType.contains("Button"); }
    public boolean isPressurePlate() { return blockType.contains("PressurePlate"); }
    public boolean isWeatheringGrate() { return "WeatheringGrateBlock".equals(blockType); }
    public boolean isWeatheringBars() { return "WeatheringBarsBlock".equals(blockType); }
    public boolean isWeatheringLantern() { return "WeatheringLanternBlock".equals(blockType); }
    public boolean isWeatheringBolt() { return "WeatheringBoltBlock".equals(blockType); }
    public boolean isWeatheringLargeChain() { return "WeatheringLargeChainBlock".equals(blockType); }
    public boolean isWeatheringClimbableChain() { return "WeatheringClimbableChainBlock".equals(blockType); }
    public boolean isClimbableChain() { return "ClimbableChainBlock".equals(blockType); }
    public boolean isLargeChain() { return "LargeChainBlock".equals(blockType); }
    public boolean isWeatheringBlock() { return "WeatheringBlock".equals(blockType); }
    public boolean isWeatheringButton() { return "WeatheringButtonBlock".equals(blockType); }
    public boolean isWeatheringDoor() { return "WeatheringDoorBlock".equals(blockType); }
    public boolean isWeatheringPressurePlate() { return "WeatheringPressurePlateBlock".equals(blockType); }
    public boolean isWeatheringRod() { return "WeatheringRodBlock".equals(blockType); }
    public boolean isWeatheringSlab() { return "WeatheringSlabBlock".equals(blockType); }
    public boolean isWeatheringStair() { return "WeatheringStairBlock".equals(blockType); }
    public boolean isWeatheringTrapdoor() { return "WeatheringTrapdoorBlock".equals(blockType); }
    public boolean isWeatheringVerticalSlab() { return "WeatheringVerticalSlabBlock".equals(blockType); }
    public boolean isMushroomShelves() { return "MushroomShelvesBlock".equals(blockType) || id.contains("mushroom_shelves"); }
    public boolean isShelf() { return !isMushroomShelves() && blockType.contains("Shelf"); }
    public boolean isWorkbench() { return "BuildersWorkbenchBlock".equals(blockType) || id.contains("builders_workbench"); }
    public boolean isGlassJar() { return blockType.contains("GlassJar") || id.contains("glass_jar") || id.contains("golden_jar"); }
    public boolean isSmokeVent() { return blockType.contains("SmokeVent") || id.contains("smoke_vent"); }
    public boolean isMuff() { return blockType.contains("Muff") || id.contains("muff"); }
    public boolean isChest() { return blockType.contains("Chest") || id.contains("chest"); }
    public boolean isBambooStandingSign() { return "ModBambooStandingSignBlock".equals(blockType) || "bamboo_sign".equals(id); }
    public boolean isBambooWallSign() { return "ModBambooWallSignBlock".equals(blockType) || "bamboo_wall_sign".equals(id); }
    public boolean isMangroveStandingSign() { return ("ModStandingSignBlock".equals(blockType) || "mangrove_sign".equals(id)) && !"bamboo_sign".equals(id); }
    public boolean isMangroveWallSign() { return ("ModWallSignBlock".equals(blockType) || "mangrove_wall_sign".equals(id)) && !"bamboo_wall_sign".equals(id); }
    public boolean isStandingSign() { return isBambooStandingSign() || isMangroveStandingSign() || blockType.contains("StandingSign"); }
    public boolean isWallSign() { return isBambooWallSign() || isMangroveWallSign() || blockType.contains("WallSign"); }
    public boolean isFenceGate() { return blockType.contains("FenceGate"); }
    public boolean isFence() { return !isFenceGate() && blockType.contains("Fence"); }
    public boolean isWaterloggableGrate() { return "WaterloggableGrateBlock".equals(blockType); }
    public boolean isIronBars() { return !isWaterloggableGrate() && (blockType.contains("Bars") || blockType.contains("Grate")); }
    public boolean isLadder() { return blockType.contains("Ladder"); }
    public boolean isCopperWallTorch() { return "CopperWallTorchBlock".equals(blockType) || id.contains("copper_wall_torch"); }
    public boolean isCopperTorch() { return !isCopperWallTorch() && ("CopperTorchBlock".equals(blockType) || id.contains("copper_torch")); }
    public boolean isLantern() { return !isCopperTorch() && !isCopperWallTorch() && (blockType.contains("Lantern") || blockType.contains("Torch")); }
    public boolean isLeafHedge() { return "LeafHedgeBlock".equals(blockType) || id.endsWith("_leaf_hedge"); }
    public boolean isMangroveLeaves() { return "MangroveLeavesBlock".equals(blockType) || id.equals("mangrove_leaves"); }
    public boolean isLeaves() { return !isLeafHedge() && !isMangroveLeaves() && !isSnowyLeaves() && blockType.contains("Leaves"); }
    public boolean isSnowyLeaves() { return "SnowyLeavesBlock".equals(blockType) || (id.contains("snowy") && id.contains("leaves")); }
    public boolean isSnowOverlay() { return "SnowOverlayBlock".equals(blockType) || "snow_overlay".equals(id); }
    public boolean isMangrovePropagule() { return "MangrovePropaguleBlock".equals(blockType) || id.equals("mangrove_propagule"); }
    public boolean isMangroveRoots() { return "MangroveRootsBlock".equals(blockType) || id.contains("mangrove_roots"); }
    public boolean isColoredMossLayers() { return "ColoredMossLayersBlock".equals(blockType) || (id.endsWith("_moss_layers") && !"moss_layers".equals(id)); }
    public boolean isMossLayers() { return "MossLayersBlock".equals(blockType) || "moss_layers".equals(id); }
    public boolean isLeafLayers() { return "LeafLayersBlock".equals(blockType) || id.endsWith("_leaf_layers"); }
    public boolean isLeafLitter() { return "LeafLitterBlock".equals(blockType) || "leaf_litter".equals(id); }
    public boolean isMossOverlay() { return "MossOverlayBlock".equals(blockType) || id.endsWith("_moss_overlay") || "moss_overlay".equals(id); }
    public boolean isWoolLayers() { return "WoolLayersBlock".equals(blockType); }
    public boolean isLayer() { return !isWoolLayers() && !isLeafLayers() && !isColoredMossLayers() && !isMossLayers() && !isMossOverlay() && (blockType.contains("Layers") || blockType.contains("Overlay") || blockType.contains("Carpet")); }
    public boolean isAshenKingPillar() { return id.contains("ashen_king_pillar") || blockType.contains("AshenKingPillar"); }
    public boolean isPillar() { return !isAshenKingPillar() && "PillarBlock".equals(blockType); }
    public boolean isHollowLog() { return blockType.contains("HollowLog") || id.startsWith("hollow_") || id.contains("_hollow_"); }
    public boolean isHollowPipe() { return blockType.contains("HollowPipe") || id.contains("hollow_pipe"); }
    public boolean isPipe() { return !isHollowPipe() && (blockType.contains("Pipe") || id.endsWith("_pipe")); }
    public boolean isFroglight() { return blockType.contains("Froglight") || id.contains("froglight"); }
    public boolean isBambooBlock() { return !isSlab() && !isVerticalSlab() && !isWall() && !isStair() && ("bamboo_block".equals(id) || "stripped_bamboo_block".equals(id)); }
    public boolean isCautionBlock() { return !isSlab() && !isVerticalSlab() && !isStair() && ("framed_caution".equals(id) || (id.startsWith("caution_") && !id.endsWith("_slab") && !id.endsWith("_vertical_slab") && !id.endsWith("_stairs"))); }
    public boolean isLog() { return !isHollowLog() && !isSlab() && !isVerticalSlab() && !isStair() && !isWall() && (id.endsWith("_log") || "LogBlock".equals(blockType)); }
    public boolean isWood() { return !isSlab() && !isVerticalSlab() && !isStair() && !isWall() && !id.contains("bamboo_wood") && (id.endsWith("_wood") || "WoodBlock".equals(blockType)); }
    public boolean isRotatedPillar() { return !isHollowLog() && !isHollowPipe() && !isPipe() && !isPillar() && !isAshenKingPillar() && !isFroglight() && !isSlab() && !isVerticalSlab() && !isStair() && !isWall() && ("RotatedPillarBlock".equals(blockType) || isLog() || isWood() || isCautionBlock() || isBambooBlock()); }
    public boolean isTrappedDecoratedPot() { return blockType.contains("TrappedDecoratedPot") || id.contains("trapped_decorated_pot"); }
    public boolean isDecoratedPot() { return !isTrappedDecoratedPot() && (blockType.contains("DecoratedPot") || id.contains("decorated_pot")); }
    public boolean isFestiveStocking() { return blockType.contains("FestiveStocking") || id.contains("festive_stocking"); }
    public boolean isCascadeNoMist() { return "CascadeBlockNoMist".equals(blockType) || "cascade_block_no_mist".equals(id); }
    public boolean isCascade() { return !isCascadeNoMist() && ("CascadeBlock".equals(blockType) || "cascade_block".equals(id)); }
    public boolean isGlazedGlass() { return "GlazedGlassBlock".equals(blockType); }
    public boolean isIcicleCauldron() { return blockType.contains("IcicleCauldron") || id.contains("icicle_cauldron"); }
    public boolean isPackedIcicleBlock() { return "PackedIcicleBlock".equals(blockType) || "packed_icicle_block".equals(id); }
    public boolean isIcicleBlock() { return !isPackedIcicleBlock() && ("IcicleBlock".equals(blockType) || "icicle_block".equals(id)); }
    public boolean isPotentSulfur() { return blockType.contains("PotentSulfur") || id.contains("potent_sulfur"); }
    public boolean isSulfurSpike() { return blockType.contains("SulfurSpike") || id.contains("sulfur_spike"); }
    public boolean isBoneDice() { return blockType.contains("BoneDice") || id.contains("bone_dice"); }
    public boolean isCopperBulb() { return blockType.endsWith("CopperBulbBlock"); }
    public int copperBulbLightLevel() {
        if (blockType.startsWith("Exposed")) return 12;
        if (blockType.startsWith("Weathered")) return 8;
        if (blockType.startsWith("Oxidized")) return 4;
        return 15;
    }
    public boolean isStar() { return blockType.contains("Star") || id.contains("star"); }
    public boolean isCushion() { return blockType.contains("Cushion") || id.contains("cushion"); }
    public boolean isBigBook() { return blockType.contains("BigBook") || id.contains("big_book"); }
    public boolean isBigCandle() { return "BigCandleBlock".equals(blockType) || id.contains("big_candle"); }
    public boolean isSoftFabric() { return "SoftFabricBlock".equals(blockType) || id.endsWith("_dye_sack") || "glow_ink_sack".equals(id); }
    public boolean isSpool() { return blockType.contains("Spool") || id.endsWith("_spool") || "spool".equals(id); }
    public boolean isSteelBolt() { return "SteelBoltBlock".equals(blockType) || (!isWeatheringBolt() && (id.endsWith("_bolts") || id.contains("bolt"))); }
    public boolean isFestiveLamp() { return blockType.contains("FestiveLamp") || id.contains("festive_lamp"); }
    public boolean isBigOrnament() { return blockType.contains("BigOrnament") || id.contains("big_ornament"); }
    public boolean isBigTintedOrnament() { return "big_tinted_glass_ornament".equals(id); }
    public boolean isOrnament() { return !isBigOrnament() && (blockType.contains("Ornament") || id.contains("ornament")); }
    public boolean isTintedOrnament() { return "tinted_glass_ornament".equals(id); }
    public boolean isStringLight() { return blockType.contains("StringLight") || id.contains("string_light"); }
    public boolean isExperienceCauldron() { return blockType.contains("ExperienceCauldron") || id.contains("experience_cauldron"); }
    public boolean isExperienceFluid() { return "ExperienceFluidBlock".equals(blockType) || id.contains("experience_liquid"); }
    public boolean isMulticolorGlowLights() { return blockType.contains("MulticolorGlowLights") || id.contains("multicolor_glow_lights"); }
    public boolean isGlowLights() { return !isMulticolorGlowLights() && (blockType.contains("GlowLights") || id.contains("glow_lights")); }
    public boolean isEyeblossom() { return blockType.contains("Eyeblossom") || id.contains("eyeblossom"); }
    public boolean isFrostRose() { return blockType.contains("FrostRose") || id.contains("frost_rose"); }
    public boolean isStrawBed() { return blockType.contains("StrawBed") || id.contains("straw_bed"); }
    public boolean isCactusFlower() { return "CactusFlowerBlock".equals(blockType) || id.contains("cactus_flower"); }
    public boolean isClover() { return "CloverBlock".equals(blockType) || (id.contains("clover") && !id.contains("potted_clover")); }
    public boolean isColoredMoss() { return "ColoredMossBlock".equals(blockType) || (id.endsWith("_moss_block") && !"moss_block".equals(id)); }
    public boolean isColoredSporeBlossom() { return "ColoredSporeBlossomBlock".equals(blockType) || id.contains("spore_blossom"); }
    public boolean isCreakingHeart() { return "CreakingHeartBlock".equals(blockType) || id.contains("creaking_heart"); }
    public boolean isDryGrass() { return "DryGrassBlock".equals(blockType) || "dry_grass".equals(id); }
    public boolean isFireflyBush() { return "FireflyBushBlock".equals(blockType) || "firefly_bush".equals(id); }
    public boolean isGoldenDandelion() { return "GoldenDandelionBlock".equals(blockType) || ("golden_dandelion".equals(id) && !"potted_golden_dandelion".equals(id)); }
    public boolean isHangingMoss() { return "HangingMossBlock".equals(blockType) || id.contains("hanging_moss"); }
    public boolean isMonetFlower() { return "MonetFlowerBlock".equals(blockType) || id.endsWith("_monets"); }
    public boolean isPetal() { return "PetalBlock".equals(blockType) || ((id.contains("petal") || "wildflowers".equals(id) || "leaf_litter".equals(id)) && !id.contains("potted")); }
    public boolean isRoseVines() { return "RoseVinesBlock".equals(blockType) || id.contains("rose_vines"); }
    public boolean isTallDryGrass() { return "TallDryGrassBlock".equals(blockType) || "tall_dry_grass".equals(id); }
    public boolean isWallpaperFlat() { return "WallpaperFlatBlock".equals(blockType) || id.contains("wallpaper_flat"); }
    public boolean isWildflowers() { return "WildflowersBlock".equals(blockType); }
    public boolean isPlant() { return !isStar() && !isDecoratedPot() && !isTrappedDecoratedPot() && !isEyeblossom() && !isFrostRose() && !isCactusFlower() && !isClover() && !isColoredMoss() && !isColoredSporeBlossom() && !isCreakingHeart() && !isDryGrass() && !isTallDryGrass() && !isFireflyBush() && !isGoldenDandelion() && !isHangingMoss() && !isMonetFlower() && !isPetal() && !isWildflowers() && !isRoseVines() && !isSnowyBush() && !isSnowyFern() && !isSnowyLargeFern() && !isSnowyGrass() && !isSnowyShortGrass() && !isSnowyTallGrass() && (blockType.contains("Bush") || blockType.contains("Flower") || blockType.contains("Grass") || blockType.contains("Fern") || blockType.contains("Petal") || blockType.contains("Sapling") || blockType.contains("Clover") || blockType.contains("Pot") || blockType.contains("Blossom") || blockType.contains("Rose")); }
    public boolean isSnowyBush() { return "SnowyBushBlock".equals(blockType) || "snowy_bush".equals(id); }
    public boolean isSnowyFern() { return "SnowyFernBlock".equals(blockType) || "snowy_fern".equals(id); }
    public boolean isSnowyLargeFern() { return "SnowyLargeFernBlock".equals(blockType) || "snowy_large_fern".equals(id); }
    public boolean isSnowyGrass() { return "SnowyGrassBlock".equals(blockType) || "snowy_grass_block".equals(id); }
    public boolean isSnowyShortGrass() { return "SnowyShortGrassBlock".equals(blockType) || "snowy_short_grass".equals(id); }
    public boolean isSnowyTallGrass() { return "SnowyTallGrassBlock".equals(blockType) || "snowy_tall_grass".equals(id); }
    public boolean isSilkTouchOnlyGlass() { return "SilkTouchOnlyGlassBlock".equals(blockType); }
    public boolean isSilkTouchOnlyPane() { return "SilkTouchOnlyPaneBlock".equals(blockType); }
    public boolean isResinClump() { return "ResinClumpBlock".equals(blockType) || id.contains("resin_clump"); }
    public boolean isSculkVein() { return "SculkVeinBlock".equals(blockType) || id.contains("sculk_vein"); }
    public boolean isSculkCatalyst() { return "SculkCatalystBlock".equals(blockType) || id.contains("sculk_catalyst"); }
    public boolean isPointedIcicle() { return "PointedIcicleBlock".equals(blockType) || id.contains("pointed_icicle"); }
    public boolean isLamp() { return !isFestiveLamp() && !isStringLight() && !isOrnament() && !isBigOrnament() && !isGlowLights() && !isMulticolorGlowLights() && (blockType.contains("Lamp") || blockType.contains("Bulb") || blockType.contains("Light")); }

    @Override
    public String toString() {
        return getNamespacedId() + " [" + blockType + "]";
    }
}
