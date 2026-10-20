package com.kingodogo.buildscape.registry.blocks;

import com.kingodogo.buildscape.block.ClimbableChainBlock;
import com.kingodogo.buildscape.block.HayBaleSlabBlock;
import com.kingodogo.buildscape.block.LeafHedgeBlock;
import com.kingodogo.buildscape.block.LeafLayersBlock;
import com.kingodogo.buildscape.block.LogSlabBlock;
import com.kingodogo.buildscape.block.ModBambooStandingSignBlock;
import com.kingodogo.buildscape.block.ModBambooWallSignBlock;
import com.kingodogo.buildscape.block.ModBlock;
import com.kingodogo.buildscape.block.ModDoorBlock;
import com.kingodogo.buildscape.block.ModStairBlock;
import com.kingodogo.buildscape.block.ModTrapdoorBlock;
import com.kingodogo.buildscape.block.ModWoodTypes;
import com.kingodogo.buildscape.registry.ModItems;
import com.kingodogo.buildscape.registry.ModSounds;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WoodButtonBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.RegistryObject;

import static com.kingodogo.buildscape.registry.ModBlocks.BLOCKS;

/** Part of the blocks registrations; see {@code ModBlocks}. */
public final class BlocksPart03 {

    private BlocksPart03() {}

    /** Forces this class to load so its registrations run in the order {@code ModBlocks} calls it. */
    public static void init() {}

    public static final RegistryObject<Block> BIRCH_LEAF_LAYERS = BLOCKS.register(
            "birch_leaf_layers",
            () -> new LeafLayersBlock(
                    BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.SAND)
                            .strength(0.2f)
                            .sound(SoundType.GRASS)
                            .noOcclusion()
                            .isRedstoneConductor((state, reader, pos) -> false)
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    "birch"));

    public static final RegistryObject<Block> JUNGLE_LEAF_LAYERS = BLOCKS.register("jungle_leaf_layers",
            () -> new LeafLayersBlock(
                    BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.DIRT)
                            .strength(0.2f)
                            .sound(SoundType.GRASS)
                            .noOcclusion()
                            .isRedstoneConductor((state, reader, pos) -> false)
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    "jungle"));

    public static final RegistryObject<Block> ACACIA_LEAF_LAYERS = BLOCKS.register("acacia_leaf_layers",
            () -> new LeafLayersBlock(
                    BlockBehaviour.Properties.of(
                            Material.LEAVES,
                            MaterialColor.COLOR_ORANGE)
                            .strength(0.2f)
                            .sound(SoundType.GRASS)
                            .noOcclusion()
                            .isRedstoneConductor((state, reader, pos) -> false)
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    "acacia"));

    public static final RegistryObject<Block> DARK_OAK_LEAF_LAYERS = BLOCKS.register("dark_oak_leaf_layers",
            () -> new LeafLayersBlock(
                    BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.COLOR_BROWN)
                            .strength(0.2f)
                            .sound(SoundType.GRASS)
                            .noOcclusion()
                            .isRedstoneConductor((state, reader, pos) -> false)
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    "dark_oak"));

    public static final RegistryObject<Block> AZALEA_LEAF_LAYERS = BLOCKS.register("azalea_leaf_layers",
            () -> new LeafLayersBlock(
                    BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.PLANT)
                            .strength(0.2f)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isRedstoneConductor((state, reader, pos) -> false)
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    "azalea"));

    public static final RegistryObject<Block> FLOWERING_AZALEA_LEAF_LAYERS = BLOCKS
            .register("flowering_azalea_leaf_layers", () -> new LeafLayersBlock(
                    BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.PLANT)
                            .strength(0.2f)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isRedstoneConductor((state, reader, pos) -> false)
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    "flowering_azalea"));

    public static final RegistryObject<Block> SNOWY_OAK_LEAF_LAYERS = BLOCKS.register("snowy_oak_leaf_layers",
            () -> new LeafLayersBlock(
                    BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.SNOW)
                            .strength(0.2f)
                            .sound(SoundType.GRASS)
                            .noOcclusion()
                            .isRedstoneConductor((state, reader, pos) -> false)
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    "snowy_oak"));

    public static final RegistryObject<Block> SNOWY_SPRUCE_LEAF_LAYERS = BLOCKS.register("snowy_spruce_leaf_layers",
            () -> new LeafLayersBlock(
                    BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.PODZOL)
                            .strength(0.2f)
                            .sound(SoundType.GRASS)
                            .noOcclusion()
                            .isRedstoneConductor((state, reader, pos) -> false)
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    "snowy_spruce"));

    public static final RegistryObject<Block> SNOWY_BIRCH_LEAF_LAYERS = BLOCKS.register("snowy_birch_leaf_layers",
            () -> new LeafLayersBlock(
                    BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.SAND)
                            .strength(0.2f)
                            .sound(SoundType.GRASS)
                            .noOcclusion()
                            .isRedstoneConductor((state, reader, pos) -> false)
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    "snowy_birch"));

    public static final RegistryObject<Block> SNOWY_JUNGLE_LEAF_LAYERS = BLOCKS.register("snowy_jungle_leaf_layers",
            () -> new LeafLayersBlock(
                    BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.DIRT)
                            .strength(0.2f)
                            .sound(SoundType.GRASS)
                            .noOcclusion()
                            .isRedstoneConductor((state, reader, pos) -> false)
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    "snowy_jungle"));

    public static final RegistryObject<Block> SNOWY_ACACIA_LEAF_LAYERS = BLOCKS.register("snowy_acacia_leaf_layers",
            () -> new LeafLayersBlock(
                    BlockBehaviour.Properties.of(
                            Material.LEAVES,
                            MaterialColor.COLOR_ORANGE)
                            .strength(0.2f)
                            .sound(SoundType.GRASS)
                            .noOcclusion()
                            .isRedstoneConductor((state, reader, pos) -> false)
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    "snowy_acacia"));

    public static final RegistryObject<Block> SNOWY_DARK_OAK_LEAF_LAYERS = BLOCKS
            .register("snowy_dark_oak_leaf_layers", () -> new LeafLayersBlock(
                    BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.COLOR_BROWN)
                            .strength(0.2f)
                            .sound(SoundType.GRASS)
                            .noOcclusion()
                            .isRedstoneConductor((state, reader, pos) -> false)
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    "snowy_dark_oak"));

    public static final RegistryObject<Block> SNOWY_MANGROVE_LEAF_LAYERS = BLOCKS
            .register("snowy_mangrove_leaf_layers", () -> new LeafLayersBlock(
                    BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.PLANT)
                            .strength(0.2f)
                            .sound(SoundType.GRASS)
                            .noOcclusion()
                            .isRedstoneConductor((state, reader, pos) -> false)
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    "snowy_mangrove"));

    public static final RegistryObject<Block> SNOWY_AZALEA_LEAF_LAYERS = BLOCKS.register("snowy_azalea_leaf_layers",
            () -> new LeafLayersBlock(
                    BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.PLANT)
                            .strength(0.2f)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isRedstoneConductor((state, reader, pos) -> false)
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    "snowy_azalea"));

    public static final RegistryObject<Block> SNOWY_FLOWERING_AZALEA_LEAF_LAYERS = BLOCKS
            .register("snowy_flowering_azalea_leaf_layers", () -> new LeafLayersBlock(
                    BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.PLANT)
                            .strength(0.2f)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isRedstoneConductor((state, reader, pos) -> false)
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    "snowy_flowering_azalea"));

    public static final RegistryObject<Block> MANGROVE_LEAF_LAYERS = BLOCKS.register("mangrove_leaf_layers",
            () -> new LeafLayersBlock(
                    BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.PLANT)
                            .strength(0.2f)
                            .sound(SoundType.GRASS)
                            .noOcclusion()
                            .isRedstoneConductor((state, reader, pos) -> false)
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    "mangrove"));

    public static final RegistryObject<Block> ORANGE_POPLAR_LEAF_LAYERS = BLOCKS.register("orange_poplar_leaf_layers",
            () -> new LeafLayersBlock(
                    BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.COLOR_ORANGE)
                            .strength(0.2f)
                            .sound(SoundType.GRASS)
                            .noOcclusion()
                            .isRedstoneConductor((state, reader, pos) -> false)
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    "orange_poplar"));

    public static final RegistryObject<Block> RED_POPLAR_LEAF_LAYERS = BLOCKS.register("red_poplar_leaf_layers",
            () -> new LeafLayersBlock(
                    BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.COLOR_RED)
                            .strength(0.2f)
                            .sound(SoundType.GRASS)
                            .noOcclusion()
                            .isRedstoneConductor((state, reader, pos) -> false)
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    "red_poplar"));

    public static final RegistryObject<Block> YELLOW_POPLAR_LEAF_LAYERS = BLOCKS.register("yellow_poplar_leaf_layers",
            () -> new LeafLayersBlock(
                    BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.COLOR_YELLOW)
                            .strength(0.2f)
                            .sound(SoundType.GRASS)
                            .noOcclusion()
                            .isRedstoneConductor((state, reader, pos) -> false)
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    "yellow_poplar"));

    public static final RegistryObject<Block> CHERRY_LEAF_LAYERS = BLOCKS.register("cherry_leaf_layers",
            () -> new LeafLayersBlock(
                    BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.COLOR_PINK)
                            .strength(0.2f)
                            .sound(SoundType.GRASS)
                            .noOcclusion()
                            .isRedstoneConductor((state, reader, pos) -> false)
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    "cherry"));

    public static final RegistryObject<Block> PALE_OAK_LEAF_LAYERS = BLOCKS.register("pale_oak_leaf_layers",
            () -> new LeafLayersBlock(
                    BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.PLANT)
                            .strength(0.2f)
                            .sound(SoundType.GRASS)
                            .noOcclusion()
                            .isRedstoneConductor((state, reader, pos) -> false)
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    "pale_oak"));

    public static final RegistryObject<Block> SNOWY_CHERRY_LEAF_LAYERS = BLOCKS.register("snowy_cherry_leaf_layers",
            () -> new LeafLayersBlock(
                    BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.SNOW)
                            .strength(0.2f)
                            .sound(SoundType.GRASS)
                            .noOcclusion()
                            .isRedstoneConductor((state, reader, pos) -> false)
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    "snowy_cherry"));

    public static final RegistryObject<Block> SNOWY_PALE_OAK_LEAF_LAYERS = BLOCKS.register("snowy_pale_oak_leaf_layers",
            () -> new LeafLayersBlock(
                    BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.SNOW)
                            .strength(0.2f)
                            .sound(SoundType.GRASS)
                            .noOcclusion()
                            .isRedstoneConductor((state, reader, pos) -> false)
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    "snowy_pale_oak"));

    public static final RegistryObject<Block> OAK_LEAF_HEDGE = BLOCKS.register(
            "oak_leaf_hedge",
            () -> new LeafHedgeBlock(
                    BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.PLANT)
                            .strength(0.2F)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    ModItems.OAK_LEAF_HEDGE));

    public static final RegistryObject<Block> SPRUCE_LEAF_HEDGE = BLOCKS.register(
            "spruce_leaf_hedge",
            () -> new LeafHedgeBlock(
                    BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.PODZOL)
                            .strength(0.2F)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    ModItems.SPRUCE_LEAF_HEDGE));

    public static final RegistryObject<Block> BIRCH_LEAF_HEDGE = BLOCKS.register(
            "birch_leaf_hedge",
            () -> new LeafHedgeBlock(
                    BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.SAND)
                            .strength(0.2F)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    ModItems.BIRCH_LEAF_HEDGE));

    public static final RegistryObject<Block> JUNGLE_LEAF_HEDGE = BLOCKS.register(
            "jungle_leaf_hedge",
            () -> new LeafHedgeBlock(
                    BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.DIRT)
                            .strength(0.2F)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    ModItems.JUNGLE_LEAF_HEDGE));

    public static final RegistryObject<Block> ACACIA_LEAF_HEDGE = BLOCKS.register(
            "acacia_leaf_hedge",
            () -> new LeafHedgeBlock(
                    BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_ORANGE)
                            .strength(0.2F)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    ModItems.ACACIA_LEAF_HEDGE));

    public static final RegistryObject<Block> DARK_OAK_LEAF_HEDGE = BLOCKS.register("dark_oak_leaf_hedge",
            () -> new LeafHedgeBlock(
                    BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_BROWN)
                            .strength(0.2F)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    ModItems.DARK_OAK_LEAF_HEDGE));

    public static final RegistryObject<Block> AZALEA_LEAF_HEDGE = BLOCKS.register(
            "azalea_leaf_hedge",
            () -> new LeafHedgeBlock(
                    BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.PLANT)
                            .strength(0.2F)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    ModItems.AZALEA_LEAF_HEDGE));

    public static final RegistryObject<Block> FLOWERING_AZALEA_LEAF_HEDGE = BLOCKS
            .register("flowering_azalea_leaf_hedge", () -> new LeafHedgeBlock(
                    BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.PLANT)
                            .strength(0.2F)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    ModItems.FLOWERING_AZALEA_LEAF_HEDGE));

    public static final RegistryObject<Block> SNOWY_OAK_LEAF_HEDGE = BLOCKS.register("snowy_oak_leaf_hedge",
            () -> new LeafHedgeBlock(
                    BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.SNOW)
                            .strength(0.2F)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    ModItems.SNOWY_OAK_LEAF_HEDGE));

    public static final RegistryObject<Block> SNOWY_SPRUCE_LEAF_HEDGE = BLOCKS.register("snowy_spruce_leaf_hedge",
            () -> new LeafHedgeBlock(
                    BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.PODZOL)
                            .strength(0.2F)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    ModItems.SNOWY_SPRUCE_LEAF_HEDGE));

    public static final RegistryObject<Block> SNOWY_BIRCH_LEAF_HEDGE = BLOCKS.register("snowy_birch_leaf_hedge",
            () -> new LeafHedgeBlock(
                    BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.SAND)
                            .strength(0.2F)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    ModItems.SNOWY_BIRCH_LEAF_HEDGE));

    public static final RegistryObject<Block> SNOWY_JUNGLE_LEAF_HEDGE = BLOCKS.register("snowy_jungle_leaf_hedge",
            () -> new LeafHedgeBlock(
                    BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.DIRT)
                            .strength(0.2F)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    ModItems.SNOWY_JUNGLE_LEAF_HEDGE));

    public static final RegistryObject<Block> SNOWY_ACACIA_LEAF_HEDGE = BLOCKS.register("snowy_acacia_leaf_hedge",
            () -> new LeafHedgeBlock(
                    BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_ORANGE)
                            .strength(0.2F)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    ModItems.SNOWY_ACACIA_LEAF_HEDGE));

    public static final RegistryObject<Block> SNOWY_DARK_OAK_LEAF_HEDGE = BLOCKS
            .register("snowy_dark_oak_leaf_hedge", () -> new LeafHedgeBlock(
                    BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_BROWN)
                            .strength(0.2F)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    ModItems.SNOWY_DARK_OAK_LEAF_HEDGE));

    public static final RegistryObject<Block> SNOWY_MANGROVE_LEAF_HEDGE = BLOCKS
            .register("snowy_mangrove_leaf_hedge", () -> new LeafHedgeBlock(
                    BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.PLANT)
                            .strength(0.2F)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    ModItems.SNOWY_MANGROVE_LEAF_HEDGE));

    public static final RegistryObject<Block> SNOWY_AZALEA_LEAF_HEDGE = BLOCKS.register("snowy_azalea_leaf_hedge",
            () -> new LeafHedgeBlock(
                    BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.PLANT)
                            .strength(0.2F)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    ModItems.SNOWY_AZALEA_LEAF_HEDGE));

    public static final RegistryObject<Block> SNOWY_FLOWERING_AZALEA_LEAF_HEDGE = BLOCKS
            .register("snowy_flowering_azalea_leaf_hedge", () -> new LeafHedgeBlock(
                    BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.PLANT)
                            .strength(0.2F)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    ModItems.SNOWY_FLOWERING_AZALEA_LEAF_HEDGE));

    public static final RegistryObject<Block> MANGROVE_LEAF_HEDGE = BLOCKS.register("mangrove_leaf_hedge",
            () -> new LeafHedgeBlock(
                    BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.PLANT)
                            .strength(0.2F)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    ModItems.MANGROVE_LEAF_HEDGE));

    public static final RegistryObject<Block> ORANGE_POPLAR_LEAF_HEDGE = BLOCKS.register("orange_poplar_leaf_hedge",
            () -> new LeafHedgeBlock(
                    BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_ORANGE)
                            .strength(0.2F)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    ModItems.ORANGE_POPLAR_LEAF_HEDGE));

    public static final RegistryObject<Block> RED_POPLAR_LEAF_HEDGE = BLOCKS.register("red_poplar_leaf_hedge",
            () -> new LeafHedgeBlock(
                    BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_RED)
                            .strength(0.2F)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    ModItems.RED_POPLAR_LEAF_HEDGE));

    public static final RegistryObject<Block> YELLOW_POPLAR_LEAF_HEDGE = BLOCKS.register("yellow_poplar_leaf_hedge",
            () -> new LeafHedgeBlock(
                    BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_YELLOW)
                            .strength(0.2F)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    ModItems.YELLOW_POPLAR_LEAF_HEDGE));

    public static final RegistryObject<Block> CHERRY_LEAF_HEDGE = BLOCKS.register("cherry_leaf_hedge",
            () -> new LeafHedgeBlock(
                    BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_PINK)
                            .strength(0.2F)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    ModItems.CHERRY_LEAF_HEDGE));

    public static final RegistryObject<Block> PALE_OAK_LEAF_HEDGE = BLOCKS.register("pale_oak_leaf_hedge",
            () -> new LeafHedgeBlock(
                    BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.PLANT)
                            .strength(0.2F)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    ModItems.PALE_OAK_LEAF_HEDGE));

    public static final RegistryObject<Block> SNOWY_CHERRY_LEAF_HEDGE = BLOCKS.register("snowy_cherry_leaf_hedge",
            () -> new LeafHedgeBlock(
                    BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.SNOW)
                            .strength(0.2F)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    ModItems.SNOWY_CHERRY_LEAF_HEDGE));

    public static final RegistryObject<Block> SNOWY_PALE_OAK_LEAF_HEDGE = BLOCKS.register("snowy_pale_oak_leaf_hedge",
            () -> new LeafHedgeBlock(
                    BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.SNOW)
                            .strength(0.2F)
                            .sound(ModSounds.AZALEA_SOUNDS())
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false),
                    ModItems.SNOWY_PALE_OAK_LEAF_HEDGE));

    public static final RegistryObject<Block> HAY_BALE_SLAB = BLOCKS.register(
            "hay_bale_slab",
            () -> new HayBaleSlabBlock(
                    BlockBehaviour.Properties.of(Material.GRASS, MaterialColor.COLOR_YELLOW)
                            .strength(0.5f)
                            .sound(SoundType.GRASS)));
    public static final RegistryObject<Block> HAY_BALE_STAIRS = BLOCKS.register("hay_bale_stairs",
            () -> new ModStairBlock(Blocks.HAY_BLOCK.defaultBlockState(),
                    BlockBehaviour.Properties.copy(HAY_BALE_SLAB.get())));

    public static final RegistryObject<Block> BAMBOO_BLOCK = BLOCKS.register(
            "bamboo_block",
            () -> new RotatedPillarBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                            .strength(2.0f)
                            .sound(SoundType.BAMBOO)));
    public static final RegistryObject<Block> BAMBOO_BLOCK_STAIRS = BLOCKS.register("bamboo_block_stairs",
            () -> new ModStairBlock(
                    BAMBOO_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                            .strength(2.0f)
                            .sound(SoundType.BAMBOO)));
    public static final RegistryObject<Block> STRIPPED_BAMBOO_BLOCK = BLOCKS.register("stripped_bamboo_block",
            () -> new RotatedPillarBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                            .strength(2.0f)
                            .sound(SoundType.BAMBOO)));
    public static final RegistryObject<Block> STRIPPED_BAMBOO_BLOCK_STAIRS = BLOCKS.register(
            "stripped_bamboo_block_stairs",
            () -> new StairBlock(
                    () -> STRIPPED_BAMBOO_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.PLANT)
                            .strength(2.0f)
                            .sound(SoundType.BAMBOO)));
    public static final RegistryObject<Block> BAMBOO_WOOD = BLOCKS.register(
            "bamboo_wood",
            () -> new Block(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                            .strength(2.0f)
                            .sound(SoundType.BAMBOO)));
    public static final RegistryObject<Block> STRIPPED_BAMBOO_WOOD = BLOCKS.register(
            "stripped_bamboo_wood",
            () -> new Block(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                            .strength(2.0f)
                            .sound(SoundType.BAMBOO)));
    public static final RegistryObject<Block> BAMBOO_BLOCK_SLAB = BLOCKS.register(
            "bamboo_block_slab",
            () -> new LogSlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                            .strength(2.0f)
                            .sound(SoundType.BAMBOO)));
    public static final RegistryObject<Block> BAMBOO_BLOCK_FENCE = BLOCKS.register("bamboo_block_fence",
            () -> new FenceBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                            .strength(2.0f)
                            .sound(SoundType.BAMBOO)));
    public static final RegistryObject<Block> BAMBOO_BLOCK_FENCE_GATE = BLOCKS.register("bamboo_block_fence_gate",
            () -> new FenceGateBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                            .strength(2.0f)
                            .sound(SoundType.BAMBOO)));
    public static final RegistryObject<Block> BAMBOO_BLOCK_PRESSURE_PLATE = BLOCKS
            .register("bamboo_block_pressure_plate", () -> new PressurePlateBlock(
                    PressurePlateBlock.Sensitivity.EVERYTHING,
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                            .strength(0.5f)
                            .sound(SoundType.BAMBOO)
                            .noCollission()));
    public static final RegistryObject<Block> BAMBOO_BLOCK_BUTTON = BLOCKS.register("bamboo_block_button",
            () -> new WoodButtonBlock(
                    BlockBehaviour.Properties.of(
                            Material.DECORATION,
                            MaterialColor.COLOR_YELLOW)
                            .strength(0.5f)
                            .sound(SoundType.BAMBOO)
                            .noCollission()));
    public static final RegistryObject<Block> STRIPPED_BAMBOO_BLOCK_SLAB = BLOCKS
            .register("stripped_bamboo_block_slab", () -> new LogSlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                            .strength(2.0f)
                            .sound(SoundType.BAMBOO)));
    public static final RegistryObject<Block> STRIPPED_BAMBOO_BLOCK_FENCE = BLOCKS
            .register("stripped_bamboo_block_fence", () -> new FenceBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                            .strength(2.0f)
                            .sound(SoundType.BAMBOO)));
    public static final RegistryObject<Block> STRIPPED_BAMBOO_BLOCK_FENCE_GATE = BLOCKS
            .register("stripped_bamboo_block_fence_gate", () -> new FenceGateBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                            .strength(2.0f)
                            .sound(SoundType.BAMBOO)));
    public static final RegistryObject<Block> STRIPPED_BAMBOO_BLOCK_PRESSURE_PLATE = BLOCKS.register(
            "stripped_bamboo_block_pressure_plate",
            () -> new PressurePlateBlock(
                    PressurePlateBlock.Sensitivity.EVERYTHING,
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                            .strength(0.5f)
                            .sound(SoundType.BAMBOO)
                            .noCollission()));
    public static final RegistryObject<Block> STRIPPED_BAMBOO_BLOCK_BUTTON = BLOCKS
            .register("stripped_bamboo_block_button", () -> new WoodButtonBlock(
                    BlockBehaviour.Properties.of(
                            Material.DECORATION,
                            MaterialColor.COLOR_YELLOW)
                            .strength(0.5f)
                            .sound(SoundType.BAMBOO)
                            .noCollission()));
    public static final RegistryObject<Block> BAMBOO_DOOR = BLOCKS.register(
            "bamboo_door",
            () -> new ModDoorBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                            .strength(3.0f)
                            .sound(SoundType.BAMBOO)
                            .noOcclusion()));
    public static final RegistryObject<Block> BAMBOO_TRAPDOOR = BLOCKS.register(
            "bamboo_trapdoor",
            () -> new ModTrapdoorBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                            .strength(3.0f)
                            .sound(SoundType.BAMBOO)
                            .noOcclusion()
                            .isValidSpawn((state, reader, pos, entityType) -> false)));
    public static final RegistryObject<Block> BAMBOO_SIGN = BLOCKS.register(
            "bamboo_sign",
            () -> new ModBambooStandingSignBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                            .strength(1.0f)
                            .sound(SoundType.BAMBOO)
                            .noCollission(),
                    ModWoodTypes.BAMBOO));
    public static final RegistryObject<Block> BAMBOO_WALL_SIGN = BLOCKS.register(
            "bamboo_wall_sign",
            () -> new ModBambooWallSignBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                            .strength(1.0f)
                            .sound(SoundType.BAMBOO)
                            .noCollission()
                            .dropsLike(BAMBOO_SIGN.get()),
                    ModWoodTypes.BAMBOO));
    public static final RegistryObject<Block> OAK_WOOD_WALL = BLOCKS.register(
            "oak_wood_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                            .strength(2.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> SPRUCE_WOOD_WALL = BLOCKS.register(
            "spruce_wood_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.PODZOL)
                            .strength(2.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BIRCH_WOOD_WALL = BLOCKS.register(
            "birch_wood_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                            .strength(2.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> DARK_OAK_WOOD_WALL = BLOCKS.register("dark_oak_wood_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                            .strength(2.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> JUNGLE_WOOD_WALL = BLOCKS.register(
            "jungle_wood_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.DIRT)
                            .strength(2.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> ACACIA_WOOD_WALL = BLOCKS.register(
            "acacia_wood_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_ORANGE)
                            .strength(2.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BAMBOO_BLOCK_WALL = BLOCKS.register(
            "bamboo_block_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                            .strength(2.0f)
                            .sound(SoundType.BAMBOO)));
    public static final RegistryObject<Block> MANGROVE_WOOD_WALL = BLOCKS.register("mangrove_wood_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                            .strength(2.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_OAK_WOOD_WALL = BLOCKS.register("stripped_oak_wood_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                            .strength(2.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_SPRUCE_WOOD_WALL = BLOCKS
            .register("stripped_spruce_wood_wall", () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.PODZOL)
                            .strength(2.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_BIRCH_WOOD_WALL = BLOCKS.register("stripped_birch_wood_wall",
            () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
                            .strength(2.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_DARK_OAK_WOOD_WALL = BLOCKS
            .register("stripped_dark_oak_wood_wall", () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                            .strength(2.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_JUNGLE_WOOD_WALL = BLOCKS
            .register("stripped_jungle_wood_wall", () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.DIRT)
                            .strength(2.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_ACACIA_WOOD_WALL = BLOCKS
            .register("stripped_acacia_wood_wall", () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_ORANGE)
                            .strength(2.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_BAMBOO_BLOCK_WALL = BLOCKS
            .register("stripped_bamboo_block_wall", () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                            .strength(2.0f)
                            .sound(SoundType.BAMBOO)));
    public static final RegistryObject<Block> STRIPPED_MANGROVE_WOOD_WALL = BLOCKS
            .register("stripped_mangrove_wood_wall", () -> new WallBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                            .strength(2.0f)
                            .sound(SoundType.WOOD)));

    public static final RegistryObject<Block> CHERRY_WOOD_WALL = BLOCKS.register(
            "cherry_wood_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.TERRACOTTA_WHITE)
                    .strength(2.0f)
                    .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_CHERRY_WOOD_WALL = BLOCKS.register(
            "stripped_cherry_wood_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.TERRACOTTA_WHITE)
                    .strength(2.0f)
                    .sound(SoundType.WOOD)));

    public static final RegistryObject<Block> PALE_OAK_WOOD_WALL = BLOCKS.register(
            "pale_oak_wood_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.TERRACOTTA_WHITE)
                    .strength(2.0f)
                    .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_PALE_OAK_WOOD_WALL = BLOCKS.register(
            "stripped_pale_oak_wood_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.TERRACOTTA_WHITE)
                    .strength(2.0f)
                    .sound(SoundType.WOOD)));

    public static final RegistryObject<Block> ASHPEN_WOOD_WALL = BLOCKS.register(
            "ashpen_wood_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SAND)
                    .strength(2.0f)
                    .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> STRIPPED_ASHPEN_WOOD_WALL = BLOCKS.register(
            "stripped_ashpen_wood_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SAND)
                    .strength(2.0f)
                    .sound(SoundType.WOOD)));

    public static final RegistryObject<Block> ASHPEN_WHITE_PLANKS = BLOCKS.register("ashpen_white_planks",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SNOW)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> ASHPEN_WHITE_STAIRS = BLOCKS.register("ashpen_white_stairs",
            () -> new ModStairBlock(
                    ASHPEN_WHITE_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SNOW)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> ASHPEN_WHITE_SLAB = BLOCKS.register(
            "ashpen_white_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SNOW)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> ASHPEN_WHITE_FENCE = BLOCKS.register("ashpen_white_fence",
            () -> new FenceBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SNOW)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> ASHPEN_WHITE_FENCE_GATE = BLOCKS.register("ashpen_white_fence_gate",
            () -> new FenceGateBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SNOW)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> ASHPEN_WHITE_PRESSURE_PLATE = BLOCKS
            .register("ashpen_white_pressure_plate", () -> new PressurePlateBlock(
                    PressurePlateBlock.Sensitivity.EVERYTHING,
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.SNOW)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> ASHPEN_WHITE_BUTTON = BLOCKS.register("ashpen_white_button",
            () -> new WoodButtonBlock(
                    BlockBehaviour.Properties.of(Material.DECORATION, MaterialColor.SNOW)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> BLACK_ASHPEN_PLANKS = BLOCKS.register("ashpen_black_planks",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BLACK_ASHPEN_STAIRS = BLOCKS.register("ashpen_black_stairs",
            () -> new ModStairBlock(
                    BLACK_ASHPEN_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BLACK_ASHPEN_SLAB = BLOCKS.register(
            "ashpen_black_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BLACK_ASHPEN_FENCE = BLOCKS.register("ashpen_black_fence",
            () -> new FenceBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BLACK_ASHPEN_FENCE_GATE = BLOCKS.register("ashpen_black_fence_gate",
            () -> new FenceGateBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BLACK_ASHPEN_PRESSURE_PLATE = BLOCKS
            .register("ashpen_black_pressure_plate", () -> new PressurePlateBlock(
                    PressurePlateBlock.Sensitivity.EVERYTHING,
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLACK)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> BLACK_ASHPEN_BUTTON = BLOCKS.register("ashpen_black_button",
            () -> new WoodButtonBlock(
                    BlockBehaviour.Properties.of(
                            Material.DECORATION,
                            MaterialColor.COLOR_BLACK)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> BLUE_ASHPEN_PLANKS = BLOCKS.register("ashpen_blue_planks",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLUE)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BLUE_ASHPEN_STAIRS = BLOCKS.register("ashpen_blue_stairs",
            () -> new ModStairBlock(
                    BLUE_ASHPEN_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLUE)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BLUE_ASHPEN_SLAB = BLOCKS.register(
            "ashpen_blue_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLUE)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BLUE_ASHPEN_FENCE = BLOCKS.register(
            "ashpen_blue_fence",
            () -> new FenceBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLUE)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BLUE_ASHPEN_FENCE_GATE = BLOCKS.register("ashpen_blue_fence_gate",
            () -> new FenceGateBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLUE)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BLUE_ASHPEN_PRESSURE_PLATE = BLOCKS
            .register("ashpen_blue_pressure_plate", () -> new PressurePlateBlock(
                    PressurePlateBlock.Sensitivity.EVERYTHING,
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BLUE)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> BLUE_ASHPEN_BUTTON = BLOCKS.register("ashpen_blue_button",
            () -> new WoodButtonBlock(
                    BlockBehaviour.Properties.of(
                            Material.DECORATION,
                            MaterialColor.COLOR_BLUE)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> BROWN_ASHPEN_PLANKS = BLOCKS.register("ashpen_brown_planks",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BROWN_ASHPEN_STAIRS = BLOCKS.register("ashpen_brown_stairs",
            () -> new ModStairBlock(
                    BROWN_ASHPEN_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BROWN_ASHPEN_SLAB = BLOCKS.register(
            "ashpen_brown_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BROWN_ASHPEN_FENCE = BLOCKS.register("ashpen_brown_fence",
            () -> new FenceBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BROWN_ASHPEN_FENCE_GATE = BLOCKS.register("ashpen_brown_fence_gate",
            () -> new FenceGateBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BROWN_ASHPEN_PRESSURE_PLATE = BLOCKS
            .register("ashpen_brown_pressure_plate", () -> new PressurePlateBlock(
                    PressurePlateBlock.Sensitivity.EVERYTHING,
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> BROWN_ASHPEN_BUTTON = BLOCKS.register("ashpen_brown_button",
            () -> new WoodButtonBlock(
                    BlockBehaviour.Properties.of(
                            Material.DECORATION,
                            MaterialColor.COLOR_BROWN)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> CYAN_ASHPEN_PLANKS = BLOCKS.register("ashpen_cyan_planks",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_CYAN)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> CYAN_ASHPEN_STAIRS = BLOCKS.register("ashpen_cyan_stairs",
            () -> new ModStairBlock(
                    CYAN_ASHPEN_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_CYAN)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> CYAN_ASHPEN_SLAB = BLOCKS.register(
            "ashpen_cyan_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_CYAN)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> CYAN_ASHPEN_FENCE = BLOCKS.register(
            "ashpen_cyan_fence",
            () -> new FenceBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_CYAN)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> CYAN_ASHPEN_FENCE_GATE = BLOCKS.register("ashpen_cyan_fence_gate",
            () -> new FenceGateBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_CYAN)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> CYAN_ASHPEN_PRESSURE_PLATE = BLOCKS
            .register("ashpen_cyan_pressure_plate", () -> new PressurePlateBlock(
                    PressurePlateBlock.Sensitivity.EVERYTHING,
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_CYAN)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> CYAN_ASHPEN_BUTTON = BLOCKS.register("ashpen_cyan_button",
            () -> new WoodButtonBlock(
                    BlockBehaviour.Properties.of(
                            Material.DECORATION,
                            MaterialColor.COLOR_CYAN)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> GRAY_ASHPEN_PLANKS = BLOCKS.register("ashpen_gray_planks",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_GRAY)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> GRAY_ASHPEN_STAIRS = BLOCKS.register("ashpen_gray_stairs",
            () -> new ModStairBlock(
                    GRAY_ASHPEN_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_GRAY)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> GRAY_ASHPEN_SLAB = BLOCKS.register(
            "ashpen_gray_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_GRAY)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> GRAY_ASHPEN_FENCE = BLOCKS.register(
            "ashpen_gray_fence",
            () -> new FenceBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_GRAY)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> GRAY_ASHPEN_FENCE_GATE = BLOCKS.register("ashpen_gray_fence_gate",
            () -> new FenceGateBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_GRAY)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> GRAY_ASHPEN_PRESSURE_PLATE = BLOCKS
            .register("ashpen_gray_pressure_plate", () -> new PressurePlateBlock(
                    PressurePlateBlock.Sensitivity.EVERYTHING,
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_GRAY)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> GRAY_ASHPEN_BUTTON = BLOCKS.register("ashpen_gray_button",
            () -> new WoodButtonBlock(
                    BlockBehaviour.Properties.of(
                            Material.DECORATION,
                            MaterialColor.COLOR_GRAY)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> GREEN_ASHPEN_PLANKS = BLOCKS.register("ashpen_green_planks",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_GREEN)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> GREEN_ASHPEN_STAIRS = BLOCKS.register("ashpen_green_stairs",
            () -> new ModStairBlock(
                    GREEN_ASHPEN_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_GREEN)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> GREEN_ASHPEN_SLAB = BLOCKS.register(
            "ashpen_green_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_GREEN)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> GREEN_ASHPEN_FENCE = BLOCKS.register("ashpen_green_fence",
            () -> new FenceBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_GREEN)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> GREEN_ASHPEN_FENCE_GATE = BLOCKS.register("ashpen_green_fence_gate",
            () -> new FenceGateBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_GREEN)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> GREEN_ASHPEN_PRESSURE_PLATE = BLOCKS
            .register("ashpen_green_pressure_plate", () -> new PressurePlateBlock(
                    PressurePlateBlock.Sensitivity.EVERYTHING,
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_GREEN)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> GREEN_ASHPEN_BUTTON = BLOCKS.register("ashpen_green_button",
            () -> new WoodButtonBlock(
                    BlockBehaviour.Properties.of(
                            Material.DECORATION,
                            MaterialColor.COLOR_GREEN)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> LIGHT_BLUE_ASHPEN_PLANKS = BLOCKS.register("ashpen_light_blue_planks",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(
                            Material.WOOD,
                            MaterialColor.COLOR_LIGHT_BLUE)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> LIGHT_BLUE_ASHPEN_STAIRS = BLOCKS.register("ashpen_light_blue_stairs",
            () -> new ModStairBlock(
                    LIGHT_BLUE_ASHPEN_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(
                            Material.WOOD,
                            MaterialColor.COLOR_LIGHT_BLUE)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> LIGHT_BLUE_ASHPEN_SLAB = BLOCKS.register("ashpen_light_blue_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(
                            Material.WOOD,
                            MaterialColor.COLOR_LIGHT_BLUE)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> LIGHT_BLUE_ASHPEN_FENCE = BLOCKS.register("ashpen_light_blue_fence",
            () -> new FenceBlock(
                    BlockBehaviour.Properties.of(
                            Material.WOOD,
                            MaterialColor.COLOR_LIGHT_BLUE)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> LIGHT_BLUE_ASHPEN_FENCE_GATE = BLOCKS
            .register("ashpen_light_blue_fence_gate", () -> new FenceGateBlock(
                    BlockBehaviour.Properties.of(
                            Material.WOOD,
                            MaterialColor.COLOR_LIGHT_BLUE)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> LIGHT_BLUE_ASHPEN_PRESSURE_PLATE = BLOCKS
            .register("ashpen_light_blue_pressure_plate", () -> new PressurePlateBlock(
                    PressurePlateBlock.Sensitivity.EVERYTHING,
                    BlockBehaviour.Properties.of(
                            Material.WOOD,
                            MaterialColor.COLOR_LIGHT_BLUE)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> LIGHT_BLUE_ASHPEN_BUTTON = BLOCKS.register("ashpen_light_blue_button",
            () -> new WoodButtonBlock(
                    BlockBehaviour.Properties.of(
                            Material.DECORATION,
                            MaterialColor.COLOR_LIGHT_BLUE)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> LIGHT_GRAY_ASHPEN_PLANKS = BLOCKS.register("ashpen_light_gray_planks",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(
                            Material.WOOD,
                            MaterialColor.COLOR_LIGHT_GRAY)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> LIGHT_GRAY_ASHPEN_STAIRS = BLOCKS.register("ashpen_light_gray_stairs",
            () -> new ModStairBlock(
                    LIGHT_GRAY_ASHPEN_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(
                            Material.WOOD,
                            MaterialColor.COLOR_LIGHT_GRAY)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> LIGHT_GRAY_ASHPEN_SLAB = BLOCKS.register("ashpen_light_gray_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(
                            Material.WOOD,
                            MaterialColor.COLOR_LIGHT_GRAY)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> LIGHT_GRAY_ASHPEN_FENCE = BLOCKS.register("ashpen_light_gray_fence",
            () -> new FenceBlock(
                    BlockBehaviour.Properties.of(
                            Material.WOOD,
                            MaterialColor.COLOR_LIGHT_GRAY)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> LIGHT_GRAY_ASHPEN_FENCE_GATE = BLOCKS
            .register("ashpen_light_gray_fence_gate", () -> new FenceGateBlock(
                    BlockBehaviour.Properties.of(
                            Material.WOOD,
                            MaterialColor.COLOR_LIGHT_GRAY)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> LIGHT_GRAY_ASHPEN_PRESSURE_PLATE = BLOCKS
            .register("ashpen_light_gray_pressure_plate", () -> new PressurePlateBlock(
                    PressurePlateBlock.Sensitivity.EVERYTHING,
                    BlockBehaviour.Properties.of(
                            Material.WOOD,
                            MaterialColor.COLOR_LIGHT_GRAY)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> LIGHT_GRAY_ASHPEN_BUTTON = BLOCKS.register("ashpen_light_gray_button",
            () -> new WoodButtonBlock(
                    BlockBehaviour.Properties.of(
                            Material.DECORATION,
                            MaterialColor.COLOR_LIGHT_GRAY)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> LIME_ASHPEN_PLANKS = BLOCKS.register("ashpen_lime_planks",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(
                            Material.WOOD,
                            MaterialColor.COLOR_LIGHT_GREEN)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> LIME_ASHPEN_STAIRS = BLOCKS.register("ashpen_lime_stairs",
            () -> new ModStairBlock(
                    LIME_ASHPEN_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(
                            Material.WOOD,
                            MaterialColor.COLOR_LIGHT_GREEN)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> LIME_ASHPEN_SLAB = BLOCKS.register(
            "ashpen_lime_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(
                            Material.WOOD,
                            MaterialColor.COLOR_LIGHT_GREEN)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> LIME_ASHPEN_FENCE = BLOCKS.register(
            "ashpen_lime_fence",
            () -> new FenceBlock(
                    BlockBehaviour.Properties.of(
                            Material.WOOD,
                            MaterialColor.COLOR_LIGHT_GREEN)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> LIME_ASHPEN_FENCE_GATE = BLOCKS.register("ashpen_lime_fence_gate",
            () -> new FenceGateBlock(
                    BlockBehaviour.Properties.of(
                            Material.WOOD,
                            MaterialColor.COLOR_LIGHT_GREEN)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> LIME_ASHPEN_PRESSURE_PLATE = BLOCKS
            .register("ashpen_lime_pressure_plate", () -> new PressurePlateBlock(
                    PressurePlateBlock.Sensitivity.EVERYTHING,
                    BlockBehaviour.Properties.of(
                            Material.WOOD,
                            MaterialColor.COLOR_LIGHT_GREEN)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> LIME_ASHPEN_BUTTON = BLOCKS.register("ashpen_lime_button",
            () -> new WoodButtonBlock(
                    BlockBehaviour.Properties.of(
                            Material.DECORATION,
                            MaterialColor.COLOR_LIGHT_GREEN)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> MAGENTA_ASHPEN_PLANKS = BLOCKS.register("ashpen_magenta_planks",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_MAGENTA)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> MAGENTA_ASHPEN_STAIRS = BLOCKS.register("ashpen_magenta_stairs",
            () -> new ModStairBlock(
                    MAGENTA_ASHPEN_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_MAGENTA)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> MAGENTA_ASHPEN_SLAB = BLOCKS.register("ashpen_magenta_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_MAGENTA)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> MAGENTA_ASHPEN_FENCE = BLOCKS.register("ashpen_magenta_fence",
            () -> new FenceBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_MAGENTA)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> MAGENTA_ASHPEN_FENCE_GATE = BLOCKS
            .register("ashpen_magenta_fence_gate", () -> new FenceGateBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_MAGENTA)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> MAGENTA_ASHPEN_PRESSURE_PLATE = BLOCKS
            .register("ashpen_magenta_pressure_plate", () -> new PressurePlateBlock(
                    PressurePlateBlock.Sensitivity.EVERYTHING,
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_MAGENTA)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> MAGENTA_ASHPEN_BUTTON = BLOCKS.register("ashpen_magenta_button",
            () -> new WoodButtonBlock(
                    BlockBehaviour.Properties.of(
                            Material.DECORATION,
                            MaterialColor.COLOR_MAGENTA)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> ORANGE_ASHPEN_PLANKS = BLOCKS.register("ashpen_orange_planks",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_ORANGE)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> ORANGE_ASHPEN_STAIRS = BLOCKS.register("ashpen_orange_stairs",
            () -> new ModStairBlock(
                    ORANGE_ASHPEN_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_ORANGE)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> ORANGE_ASHPEN_SLAB = BLOCKS.register("ashpen_orange_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_ORANGE)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> ORANGE_ASHPEN_FENCE = BLOCKS.register("ashpen_orange_fence",
            () -> new FenceBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_ORANGE)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> ORANGE_ASHPEN_FENCE_GATE = BLOCKS.register("ashpen_orange_fence_gate",
            () -> new FenceGateBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_ORANGE)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> ORANGE_ASHPEN_PRESSURE_PLATE = BLOCKS
            .register("ashpen_orange_pressure_plate", () -> new PressurePlateBlock(
                    PressurePlateBlock.Sensitivity.EVERYTHING,
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_ORANGE)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> ORANGE_ASHPEN_BUTTON = BLOCKS.register("ashpen_orange_button",
            () -> new WoodButtonBlock(
                    BlockBehaviour.Properties.of(
                            Material.DECORATION,
                            MaterialColor.COLOR_ORANGE)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> PINK_ASHPEN_PLANKS = BLOCKS.register("ashpen_pink_planks",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_PINK)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> PINK_ASHPEN_STAIRS = BLOCKS.register("ashpen_pink_stairs",
            () -> new ModStairBlock(
                    PINK_ASHPEN_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_PINK)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> PINK_ASHPEN_SLAB = BLOCKS.register(
            "ashpen_pink_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_PINK)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> PINK_ASHPEN_FENCE = BLOCKS.register(
            "ashpen_pink_fence",
            () -> new FenceBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_PINK)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> PINK_ASHPEN_FENCE_GATE = BLOCKS.register("ashpen_pink_fence_gate",
            () -> new FenceGateBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_PINK)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> PINK_ASHPEN_PRESSURE_PLATE = BLOCKS
            .register("ashpen_pink_pressure_plate", () -> new PressurePlateBlock(
                    PressurePlateBlock.Sensitivity.EVERYTHING,
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_PINK)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> PINK_ASHPEN_BUTTON = BLOCKS.register("ashpen_pink_button",
            () -> new WoodButtonBlock(
                    BlockBehaviour.Properties.of(
                            Material.DECORATION,
                            MaterialColor.COLOR_PINK)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> PURPLE_ASHPEN_PLANKS = BLOCKS.register("ashpen_purple_planks",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_PURPLE)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> PURPLE_ASHPEN_STAIRS = BLOCKS.register("ashpen_purple_stairs",
            () -> new ModStairBlock(
                    PURPLE_ASHPEN_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_PURPLE)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> PURPLE_ASHPEN_SLAB = BLOCKS.register("ashpen_purple_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_PURPLE)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> PURPLE_ASHPEN_FENCE = BLOCKS.register("ashpen_purple_fence",
            () -> new FenceBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_PURPLE)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> PURPLE_ASHPEN_FENCE_GATE = BLOCKS.register("ashpen_purple_fence_gate",
            () -> new FenceGateBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_PURPLE)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> PURPLE_ASHPEN_PRESSURE_PLATE = BLOCKS
            .register("ashpen_purple_pressure_plate", () -> new PressurePlateBlock(
                    PressurePlateBlock.Sensitivity.EVERYTHING,
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_PURPLE)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> PURPLE_ASHPEN_BUTTON = BLOCKS.register("ashpen_purple_button",
            () -> new WoodButtonBlock(
                    BlockBehaviour.Properties.of(
                            Material.DECORATION,
                            MaterialColor.COLOR_PURPLE)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> RED_ASHPEN_PLANKS = BLOCKS.register(
            "ashpen_red_planks",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> RED_ASHPEN_STAIRS = BLOCKS.register(
            "ashpen_red_stairs",
            () -> new ModStairBlock(
                    RED_ASHPEN_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> RED_ASHPEN_SLAB = BLOCKS.register(
            "ashpen_red_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> RED_ASHPEN_FENCE = BLOCKS.register(
            "ashpen_red_fence",
            () -> new FenceBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> RED_ASHPEN_FENCE_GATE = BLOCKS.register("ashpen_red_fence_gate",
            () -> new FenceGateBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> RED_ASHPEN_PRESSURE_PLATE = BLOCKS
            .register("ashpen_red_pressure_plate", () -> new PressurePlateBlock(
                    PressurePlateBlock.Sensitivity.EVERYTHING,
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> RED_ASHPEN_BUTTON = BLOCKS.register(
            "ashpen_red_button",
            () -> new WoodButtonBlock(
                    BlockBehaviour.Properties.of(
                            Material.DECORATION,
                            MaterialColor.COLOR_RED)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> YELLOW_ASHPEN_PLANKS = BLOCKS.register("ashpen_yellow_planks",
            () -> new ModBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> YELLOW_ASHPEN_STAIRS = BLOCKS.register("ashpen_yellow_stairs",
            () -> new ModStairBlock(
                    YELLOW_ASHPEN_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> YELLOW_ASHPEN_SLAB = BLOCKS.register("ashpen_yellow_slab",
            () -> new SlabBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> YELLOW_ASHPEN_FENCE = BLOCKS.register("ashpen_yellow_fence",
            () -> new FenceBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> YELLOW_ASHPEN_FENCE_GATE = BLOCKS.register("ashpen_yellow_fence_gate",
            () -> new FenceGateBlock(
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                            .strength(2.0f, 3.0f)
                            .sound(SoundType.WOOD)));
    public static final RegistryObject<Block> YELLOW_ASHPEN_PRESSURE_PLATE = BLOCKS
            .register("ashpen_yellow_pressure_plate", () -> new PressurePlateBlock(
                    PressurePlateBlock.Sensitivity.EVERYTHING,
                    BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> YELLOW_ASHPEN_BUTTON = BLOCKS.register("ashpen_yellow_button",
            () -> new WoodButtonBlock(
                    BlockBehaviour.Properties.of(
                            Material.DECORATION,
                            MaterialColor.COLOR_YELLOW)
                            .strength(0.5f)
                            .sound(SoundType.WOOD)
                            .noCollission()));
    public static final RegistryObject<Block> DIAMOND_CHAIN = BLOCKS.register(
            "diamond_chain",
            () -> new ClimbableChainBlock(
                    BlockBehaviour.Properties.of(Material.METAL, MaterialColor.DIAMOND)
                            .strength(5.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CHAIN)
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false)));
    public static final RegistryObject<Block> GOLD_CHAIN = BLOCKS.register(
            "gold_chain",
            () -> new ClimbableChainBlock(
                    BlockBehaviour.Properties.of(Material.METAL, MaterialColor.GOLD)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.CHAIN)
                            .noOcclusion()
                            .isSuffocating((state, reader, pos) -> false)
                            .isViewBlocking((state, reader, pos) -> false)));
}
