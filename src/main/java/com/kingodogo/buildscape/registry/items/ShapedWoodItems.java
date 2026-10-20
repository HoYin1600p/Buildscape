package com.kingodogo.buildscape.registry.items;

import com.kingodogo.buildscape.item.MuffBlockItem;
import com.kingodogo.buildscape.item.PoplarBoatItem;
import com.kingodogo.buildscape.registry.ModCreativeModeTab;
import com.kingodogo.buildscape.registry.blocks.CopperBlocks;
import com.kingodogo.buildscape.registry.blocks.ShapedStoneBlocks;
import com.kingodogo.buildscape.registry.blocks.ShapedWoodBlocks;
import net.minecraft.world.item.BedItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import static com.kingodogo.buildscape.registry.items.ItemsRegister.ITEMS;
import static com.kingodogo.buildscape.registry.items.ItemsRegister.createBlockItemProperties;
import static com.kingodogo.buildscape.registry.items.ItemsRegister.registerBlockItem;

/**
 * The items SPRUCE_LOG_VERTICAL_SLAB through PALE_MOSS_BLOCK of the registry, part 5 of 8, in registration order.
 * Named for its dominant families (wood, wool, decor); the neighbouring families that share a run of the order
 * with them are registered here too, because the registration order must not change.
 * Load order: {@link GlassItems#init()} runs first (see the static block), so every holder registers in order whichever is touched first.
 */
public final class ShapedWoodItems {

    private ShapedWoodItems() {}

    /** Forces this class to load, which registers its items. */
    public static void init() {}

    static {
        GlassItems.init();
    }

    public static final RegistryObject<Item> SPRUCE_LOG_VERTICAL_SLAB = registerBlockItem("spruce_log_vertical_slab",
            ShapedStoneBlocks.SPRUCE_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_SPRUCE_LOG_VERTICAL_SLAB = registerBlockItem(
            "stripped_spruce_log_vertical_slab", ShapedStoneBlocks.STRIPPED_SPRUCE_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> BIRCH_LOG_VERTICAL_SLAB = registerBlockItem("birch_log_vertical_slab",
            ShapedStoneBlocks.BIRCH_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_BIRCH_LOG_VERTICAL_SLAB = registerBlockItem(
            "stripped_birch_log_vertical_slab", ShapedStoneBlocks.STRIPPED_BIRCH_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> JUNGLE_LOG_VERTICAL_SLAB = registerBlockItem("jungle_log_vertical_slab",
            ShapedStoneBlocks.JUNGLE_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_JUNGLE_LOG_VERTICAL_SLAB = registerBlockItem(
            "stripped_jungle_log_vertical_slab", ShapedStoneBlocks.STRIPPED_JUNGLE_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> ACACIA_LOG_VERTICAL_SLAB = registerBlockItem("acacia_log_vertical_slab",
            ShapedStoneBlocks.ACACIA_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_ACACIA_LOG_VERTICAL_SLAB = registerBlockItem(
            "stripped_acacia_log_vertical_slab", ShapedStoneBlocks.STRIPPED_ACACIA_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> DARK_OAK_LOG_VERTICAL_SLAB = registerBlockItem(
            "dark_oak_log_vertical_slab", ShapedStoneBlocks.DARK_OAK_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_DARK_OAK_LOG_VERTICAL_SLAB = registerBlockItem(
            "stripped_dark_oak_log_vertical_slab", ShapedStoneBlocks.STRIPPED_DARK_OAK_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> CRIMSON_STEM_VERTICAL_SLAB = registerBlockItem(
            "crimson_stem_vertical_slab", ShapedStoneBlocks.CRIMSON_STEM_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_CRIMSON_STEM_VERTICAL_SLAB = registerBlockItem(
            "stripped_crimson_stem_vertical_slab", ShapedStoneBlocks.STRIPPED_CRIMSON_STEM_VERTICAL_SLAB);
    public static final RegistryObject<Item> WARPED_STEM_VERTICAL_SLAB = registerBlockItem("warped_stem_vertical_slab",
            ShapedStoneBlocks.WARPED_STEM_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_WARPED_STEM_VERTICAL_SLAB = registerBlockItem(
            "stripped_warped_stem_vertical_slab", ShapedStoneBlocks.STRIPPED_WARPED_STEM_VERTICAL_SLAB);
    public static final RegistryObject<Item> MANGROVE_LOG_VERTICAL_SLAB = registerBlockItem(
            "mangrove_log_vertical_slab", ShapedStoneBlocks.MANGROVE_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_MANGROVE_LOG_VERTICAL_SLAB = registerBlockItem(
            "stripped_mangrove_log_vertical_slab", ShapedStoneBlocks.STRIPPED_MANGROVE_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> ASHPEN_LOG_VERTICAL_SLAB = registerBlockItem("ashpen_log_vertical_slab",
            ShapedStoneBlocks.ASHPEN_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> ASHPEN_WOOD_VERTICAL_SLAB = registerBlockItem("ashpen_wood_vertical_slab",
            ShapedStoneBlocks.ASHPEN_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_ASHPEN_LOG_VERTICAL_SLAB = registerBlockItem(
            "stripped_ashpen_log_vertical_slab", ShapedWoodBlocks.STRIPPED_ASHPEN_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_ASHPEN_WOOD_VERTICAL_SLAB = registerBlockItem(
            "stripped_ashpen_wood_vertical_slab", ShapedWoodBlocks.STRIPPED_ASHPEN_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> SPOOL = registerBlockItem("spool", ShapedStoneBlocks.SPOOL);
    public static final RegistryObject<Item> GLOWING_SPOOL = registerBlockItem("glowing_spool",
            ShapedStoneBlocks.GLOWING_SPOOL);
    public static final RegistryObject<Item> WHITE_SPOOL = registerBlockItem("white_spool",
            ShapedStoneBlocks.WHITE_SPOOL);
    public static final RegistryObject<Item> LIGHT_GRAY_SPOOL = registerBlockItem("light_gray_spool",
            ShapedStoneBlocks.LIGHT_GRAY_SPOOL);
    public static final RegistryObject<Item> GRAY_SPOOL = registerBlockItem("gray_spool", ShapedStoneBlocks.GRAY_SPOOL);
    public static final RegistryObject<Item> BLACK_SPOOL = registerBlockItem("black_spool",
            ShapedStoneBlocks.BLACK_SPOOL);
    public static final RegistryObject<Item> BROWN_SPOOL = registerBlockItem("brown_spool",
            ShapedStoneBlocks.BROWN_SPOOL);
    public static final RegistryObject<Item> RED_SPOOL = registerBlockItem("red_spool", ShapedStoneBlocks.RED_SPOOL);
    public static final RegistryObject<Item> ORANGE_SPOOL = registerBlockItem("orange_spool",
            ShapedStoneBlocks.ORANGE_SPOOL);
    public static final RegistryObject<Item> YELLOW_SPOOL = registerBlockItem("yellow_spool",
            ShapedStoneBlocks.YELLOW_SPOOL);
    public static final RegistryObject<Item> LIME_SPOOL = registerBlockItem("lime_spool", ShapedStoneBlocks.LIME_SPOOL);
    public static final RegistryObject<Item> GREEN_SPOOL = registerBlockItem("green_spool",
            ShapedStoneBlocks.GREEN_SPOOL);
    public static final RegistryObject<Item> CYAN_SPOOL = registerBlockItem("cyan_spool", ShapedStoneBlocks.CYAN_SPOOL);
    public static final RegistryObject<Item> LIGHT_BLUE_SPOOL = registerBlockItem("light_blue_spool",
            ShapedStoneBlocks.LIGHT_BLUE_SPOOL);
    public static final RegistryObject<Item> BLUE_SPOOL = registerBlockItem("blue_spool", ShapedStoneBlocks.BLUE_SPOOL);
    public static final RegistryObject<Item> PURPLE_SPOOL = registerBlockItem("purple_spool",
            ShapedStoneBlocks.PURPLE_SPOOL);
    public static final RegistryObject<Item> MAGENTA_SPOOL = registerBlockItem("magenta_spool",
            ShapedStoneBlocks.MAGENTA_SPOOL);
    public static final RegistryObject<Item> PINK_SPOOL = registerBlockItem("pink_spool", ShapedStoneBlocks.PINK_SPOOL);
    public static final RegistryObject<Item> WHITE_DYE_SACK = registerBlockItem("white_dye_sack",
            ShapedStoneBlocks.WHITE_DYE_SACK);
    public static final RegistryObject<Item> LIGHT_GRAY_DYE_SACK = registerBlockItem("light_gray_dye_sack",
            ShapedStoneBlocks.LIGHT_GRAY_DYE_SACK);
    public static final RegistryObject<Item> GRAY_DYE_SACK = registerBlockItem("gray_dye_sack",
            ShapedStoneBlocks.GRAY_DYE_SACK);
    public static final RegistryObject<Item> BLACK_DYE_SACK = registerBlockItem("black_dye_sack",
            ShapedStoneBlocks.BLACK_DYE_SACK);
    public static final RegistryObject<Item> BROWN_DYE_SACK = registerBlockItem("brown_dye_sack",
            ShapedStoneBlocks.BROWN_DYE_SACK);
    public static final RegistryObject<Item> RED_DYE_SACK = registerBlockItem("red_dye_sack",
            ShapedStoneBlocks.RED_DYE_SACK);
    public static final RegistryObject<Item> ORANGE_DYE_SACK = registerBlockItem("orange_dye_sack",
            ShapedStoneBlocks.ORANGE_DYE_SACK);
    public static final RegistryObject<Item> YELLOW_DYE_SACK = registerBlockItem("yellow_dye_sack",
            ShapedStoneBlocks.YELLOW_DYE_SACK);
    public static final RegistryObject<Item> LIME_DYE_SACK = registerBlockItem("lime_dye_sack",
            ShapedStoneBlocks.LIME_DYE_SACK);
    public static final RegistryObject<Item> GREEN_DYE_SACK = registerBlockItem("green_dye_sack",
            ShapedStoneBlocks.GREEN_DYE_SACK);
    public static final RegistryObject<Item> CYAN_DYE_SACK = registerBlockItem("cyan_dye_sack",
            ShapedStoneBlocks.CYAN_DYE_SACK);
    public static final RegistryObject<Item> LIGHT_BLUE_DYE_SACK = registerBlockItem("light_blue_dye_sack",
            ShapedStoneBlocks.LIGHT_BLUE_DYE_SACK);
    public static final RegistryObject<Item> BLUE_DYE_SACK = registerBlockItem("blue_dye_sack",
            ShapedStoneBlocks.BLUE_DYE_SACK);
    public static final RegistryObject<Item> PURPLE_DYE_SACK = registerBlockItem("purple_dye_sack",
            ShapedStoneBlocks.PURPLE_DYE_SACK);
    public static final RegistryObject<Item> MAGENTA_DYE_SACK = registerBlockItem("magenta_dye_sack",
            ShapedStoneBlocks.MAGENTA_DYE_SACK);
    public static final RegistryObject<Item> PINK_DYE_SACK = registerBlockItem("pink_dye_sack",
            ShapedStoneBlocks.PINK_DYE_SACK);
    public static final RegistryObject<Item> GLOW_INK_SACK = registerBlockItem("glow_ink_sack",
            ShapedStoneBlocks.GLOW_INK_SACK);
    public static final RegistryObject<Item> RED_MOSS_BLOCK = registerBlockItem("red_moss_block",
            ShapedWoodBlocks.RED_MOSS_BLOCK);
    public static final RegistryObject<Item> RED_MOSS_BLOCK_SLAB = registerBlockItem("red_moss_block_slab",
            ShapedWoodBlocks.RED_MOSS_BLOCK_SLAB);
    public static final RegistryObject<Item> RED_MOSS_BLOCK_STAIRS = registerBlockItem("red_moss_block_stairs",
            ShapedWoodBlocks.RED_MOSS_BLOCK_STAIRS);
    public static final RegistryObject<Item> RED_MOSS_LAYERS = registerBlockItem("red_moss_layers",
            ShapedWoodBlocks.RED_MOSS_LAYERS);
    public static final RegistryObject<Item> RED_MOSS_CARPET = registerBlockItem("red_moss_carpet",
            ShapedWoodBlocks.RED_MOSS_CARPET);
    public static final RegistryObject<Item> RED_MOSS_OVERLAY = registerBlockItem("red_moss_overlay",
            ShapedWoodBlocks.RED_MOSS_OVERLAY);
    public static final RegistryObject<Item> ORANGE_MOSS_BLOCK = registerBlockItem("orange_moss_block",
            ShapedWoodBlocks.ORANGE_MOSS_BLOCK);
    public static final RegistryObject<Item> ORANGE_MOSS_BLOCK_SLAB = registerBlockItem("orange_moss_block_slab",
            ShapedWoodBlocks.ORANGE_MOSS_BLOCK_SLAB);
    public static final RegistryObject<Item> ORANGE_MOSS_BLOCK_STAIRS = registerBlockItem("orange_moss_block_stairs",
            ShapedWoodBlocks.ORANGE_MOSS_BLOCK_STAIRS);
    public static final RegistryObject<Item> ORANGE_MOSS_LAYERS = registerBlockItem("orange_moss_layers",
            ShapedWoodBlocks.ORANGE_MOSS_LAYERS);
    public static final RegistryObject<Item> ORANGE_MOSS_CARPET = registerBlockItem("orange_moss_carpet",
            ShapedWoodBlocks.ORANGE_MOSS_CARPET);
    public static final RegistryObject<Item> ORANGE_MOSS_OVERLAY = registerBlockItem("orange_moss_overlay",
            ShapedWoodBlocks.ORANGE_MOSS_OVERLAY);
    public static final RegistryObject<Item> YELLOW_MOSS_BLOCK = registerBlockItem("yellow_moss_block",
            ShapedWoodBlocks.YELLOW_MOSS_BLOCK);
    public static final RegistryObject<Item> YELLOW_MOSS_BLOCK_SLAB = registerBlockItem("yellow_moss_block_slab",
            ShapedWoodBlocks.YELLOW_MOSS_BLOCK_SLAB);
    public static final RegistryObject<Item> YELLOW_MOSS_BLOCK_STAIRS = registerBlockItem("yellow_moss_block_stairs",
            ShapedWoodBlocks.YELLOW_MOSS_BLOCK_STAIRS);
    public static final RegistryObject<Item> YELLOW_MOSS_LAYERS = registerBlockItem("yellow_moss_layers",
            ShapedWoodBlocks.YELLOW_MOSS_LAYERS);
    public static final RegistryObject<Item> YELLOW_MOSS_CARPET = registerBlockItem("yellow_moss_carpet",
            ShapedWoodBlocks.YELLOW_MOSS_CARPET);
    public static final RegistryObject<Item> YELLOW_MOSS_OVERLAY = registerBlockItem("yellow_moss_overlay",
            ShapedWoodBlocks.YELLOW_MOSS_OVERLAY);
    public static final RegistryObject<Item> TUFF_SLAB = registerBlockItem("tuff_slab", ShapedWoodBlocks.TUFF_SLAB);
    public static final RegistryObject<Item> TUFF_STAIRS = registerBlockItem("tuff_stairs",
            ShapedWoodBlocks.TUFF_STAIRS);
    public static final RegistryObject<Item> TUFF_WALL = registerBlockItem("tuff_wall", ShapedWoodBlocks.TUFF_WALL);
    public static final RegistryObject<Item> POLISHED_TUFF = registerBlockItem("polished_tuff",
            ShapedWoodBlocks.POLISHED_TUFF);
    public static final RegistryObject<Item> POLISHED_TUFF_SLAB = registerBlockItem("polished_tuff_slab",
            ShapedWoodBlocks.POLISHED_TUFF_SLAB);
    public static final RegistryObject<Item> POLISHED_TUFF_STAIRS = registerBlockItem("polished_tuff_stairs",
            ShapedWoodBlocks.POLISHED_TUFF_STAIRS);
    public static final RegistryObject<Item> POLISHED_TUFF_WALL = registerBlockItem("polished_tuff_wall",
            ShapedWoodBlocks.POLISHED_TUFF_WALL);
    public static final RegistryObject<Item> CHISELED_TUFF = registerBlockItem("chiseled_tuff",
            ShapedWoodBlocks.CHISELED_TUFF);
    public static final RegistryObject<Item> TUFF_BRICKS = registerBlockItem("tuff_bricks",
            ShapedWoodBlocks.TUFF_BRICKS);
    public static final RegistryObject<Item> TUFF_BRICK_SLAB = registerBlockItem("tuff_brick_slab",
            ShapedWoodBlocks.TUFF_BRICK_SLAB);
    public static final RegistryObject<Item> TUFF_BRICK_STAIRS = registerBlockItem("tuff_brick_stairs",
            ShapedWoodBlocks.TUFF_BRICK_STAIRS);
    public static final RegistryObject<Item> TUFF_BRICK_WALL = registerBlockItem("tuff_brick_wall",
            ShapedWoodBlocks.TUFF_BRICK_WALL);
    public static final RegistryObject<Item> CHISELED_TUFF_BRICKS = registerBlockItem("chiseled_tuff_bricks",
            ShapedWoodBlocks.CHISELED_TUFF_BRICKS);
    public static final RegistryObject<Item> WHITE_WOOL_SLAB = registerBlockItem("white_wool_slab",
            ShapedWoodBlocks.WHITE_WOOL_SLAB);
    public static final RegistryObject<Item> WHITE_WOOL_STAIRS = registerBlockItem("white_wool_stairs",
            ShapedWoodBlocks.WHITE_WOOL_STAIRS);
    public static final RegistryObject<Item> WHITE_WOOL_WALL = registerBlockItem("white_wool_wall",
            ShapedWoodBlocks.WHITE_WOOL_WALL);
    public static final RegistryObject<Item> ORANGE_WOOL_SLAB = registerBlockItem("orange_wool_slab",
            ShapedWoodBlocks.ORANGE_WOOL_SLAB);
    public static final RegistryObject<Item> ORANGE_WOOL_STAIRS = registerBlockItem("orange_wool_stairs",
            ShapedWoodBlocks.ORANGE_WOOL_STAIRS);
    public static final RegistryObject<Item> ORANGE_WOOL_WALL = registerBlockItem("orange_wool_wall",
            ShapedWoodBlocks.ORANGE_WOOL_WALL);
    public static final RegistryObject<Item> MAGENTA_WOOL_SLAB = registerBlockItem("magenta_wool_slab",
            ShapedWoodBlocks.MAGENTA_WOOL_SLAB);
    public static final RegistryObject<Item> MAGENTA_WOOL_STAIRS = registerBlockItem("magenta_wool_stairs",
            ShapedWoodBlocks.MAGENTA_WOOL_STAIRS);
    public static final RegistryObject<Item> MAGENTA_WOOL_WALL = registerBlockItem("magenta_wool_wall",
            ShapedWoodBlocks.MAGENTA_WOOL_WALL);
    public static final RegistryObject<Item> LIGHT_BLUE_WOOL_SLAB = registerBlockItem("light_blue_wool_slab",
            ShapedWoodBlocks.LIGHT_BLUE_WOOL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_WOOL_STAIRS = registerBlockItem("light_blue_wool_stairs",
            ShapedWoodBlocks.LIGHT_BLUE_WOOL_STAIRS);
    public static final RegistryObject<Item> LIGHT_BLUE_WOOL_WALL = registerBlockItem("light_blue_wool_wall",
            ShapedWoodBlocks.LIGHT_BLUE_WOOL_WALL);
    public static final RegistryObject<Item> YELLOW_WOOL_SLAB = registerBlockItem("yellow_wool_slab",
            ShapedWoodBlocks.YELLOW_WOOL_SLAB);
    public static final RegistryObject<Item> YELLOW_WOOL_STAIRS = registerBlockItem("yellow_wool_stairs",
            ShapedWoodBlocks.YELLOW_WOOL_STAIRS);
    public static final RegistryObject<Item> YELLOW_WOOL_WALL = registerBlockItem("yellow_wool_wall",
            ShapedWoodBlocks.YELLOW_WOOL_WALL);
    public static final RegistryObject<Item> LIME_WOOL_SLAB = registerBlockItem("lime_wool_slab",
            ShapedWoodBlocks.LIME_WOOL_SLAB);
    public static final RegistryObject<Item> LIME_WOOL_STAIRS = registerBlockItem("lime_wool_stairs",
            ShapedWoodBlocks.LIME_WOOL_STAIRS);
    public static final RegistryObject<Item> LIME_WOOL_WALL = registerBlockItem("lime_wool_wall",
            ShapedWoodBlocks.LIME_WOOL_WALL);
    public static final RegistryObject<Item> PINK_WOOL_SLAB = registerBlockItem("pink_wool_slab",
            ShapedWoodBlocks.PINK_WOOL_SLAB);
    public static final RegistryObject<Item> PINK_WOOL_STAIRS = registerBlockItem("pink_wool_stairs",
            ShapedWoodBlocks.PINK_WOOL_STAIRS);
    public static final RegistryObject<Item> PINK_WOOL_WALL = registerBlockItem("pink_wool_wall",
            ShapedWoodBlocks.PINK_WOOL_WALL);
    public static final RegistryObject<Item> GRAY_WOOL_SLAB = registerBlockItem("gray_wool_slab",
            ShapedWoodBlocks.GRAY_WOOL_SLAB);
    public static final RegistryObject<Item> GRAY_WOOL_STAIRS = registerBlockItem("gray_wool_stairs",
            ShapedWoodBlocks.GRAY_WOOL_STAIRS);
    public static final RegistryObject<Item> GRAY_WOOL_WALL = registerBlockItem("gray_wool_wall",
            ShapedWoodBlocks.GRAY_WOOL_WALL);
    public static final RegistryObject<Item> LIGHT_GRAY_WOOL_SLAB = registerBlockItem("light_gray_wool_slab",
            ShapedWoodBlocks.LIGHT_GRAY_WOOL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_WOOL_STAIRS = registerBlockItem("light_gray_wool_stairs",
            ShapedWoodBlocks.LIGHT_GRAY_WOOL_STAIRS);
    public static final RegistryObject<Item> LIGHT_GRAY_WOOL_WALL = registerBlockItem("light_gray_wool_wall",
            ShapedWoodBlocks.LIGHT_GRAY_WOOL_WALL);
    public static final RegistryObject<Item> CYAN_WOOL_SLAB = registerBlockItem("cyan_wool_slab",
            ShapedWoodBlocks.CYAN_WOOL_SLAB);
    public static final RegistryObject<Item> CYAN_WOOL_STAIRS = registerBlockItem("cyan_wool_stairs",
            ShapedWoodBlocks.CYAN_WOOL_STAIRS);
    public static final RegistryObject<Item> CYAN_WOOL_WALL = registerBlockItem("cyan_wool_wall",
            ShapedWoodBlocks.CYAN_WOOL_WALL);
    public static final RegistryObject<Item> PURPLE_WOOL_SLAB = registerBlockItem("purple_wool_slab",
            ShapedWoodBlocks.PURPLE_WOOL_SLAB);
    public static final RegistryObject<Item> PURPLE_WOOL_STAIRS = registerBlockItem("purple_wool_stairs",
            ShapedWoodBlocks.PURPLE_WOOL_STAIRS);
    public static final RegistryObject<Item> PURPLE_WOOL_WALL = registerBlockItem("purple_wool_wall",
            ShapedWoodBlocks.PURPLE_WOOL_WALL);
    public static final RegistryObject<Item> BLUE_WOOL_SLAB = registerBlockItem("blue_wool_slab",
            ShapedWoodBlocks.BLUE_WOOL_SLAB);
    public static final RegistryObject<Item> BLUE_WOOL_STAIRS = registerBlockItem("blue_wool_stairs",
            ShapedWoodBlocks.BLUE_WOOL_STAIRS);
    public static final RegistryObject<Item> BLUE_WOOL_WALL = registerBlockItem("blue_wool_wall",
            ShapedWoodBlocks.BLUE_WOOL_WALL);
    public static final RegistryObject<Item> BROWN_WOOL_SLAB = registerBlockItem("brown_wool_slab",
            ShapedWoodBlocks.BROWN_WOOL_SLAB);
    public static final RegistryObject<Item> BROWN_WOOL_STAIRS = registerBlockItem("brown_wool_stairs",
            ShapedWoodBlocks.BROWN_WOOL_STAIRS);
    public static final RegistryObject<Item> BROWN_WOOL_WALL = registerBlockItem("brown_wool_wall",
            ShapedWoodBlocks.BROWN_WOOL_WALL);
    public static final RegistryObject<Item> GREEN_WOOL_SLAB = registerBlockItem("green_wool_slab",
            ShapedWoodBlocks.GREEN_WOOL_SLAB);
    public static final RegistryObject<Item> GREEN_WOOL_STAIRS = registerBlockItem("green_wool_stairs",
            ShapedWoodBlocks.GREEN_WOOL_STAIRS);
    public static final RegistryObject<Item> GREEN_WOOL_WALL = registerBlockItem("green_wool_wall",
            ShapedWoodBlocks.GREEN_WOOL_WALL);
    public static final RegistryObject<Item> RED_WOOL_SLAB = registerBlockItem("red_wool_slab",
            ShapedWoodBlocks.RED_WOOL_SLAB);
    public static final RegistryObject<Item> RED_WOOL_STAIRS = registerBlockItem("red_wool_stairs",
            ShapedWoodBlocks.RED_WOOL_STAIRS);
    public static final RegistryObject<Item> RED_WOOL_WALL = registerBlockItem("red_wool_wall",
            ShapedWoodBlocks.RED_WOOL_WALL);
    public static final RegistryObject<Item> BLACK_WOOL_SLAB = registerBlockItem("black_wool_slab",
            ShapedWoodBlocks.BLACK_WOOL_SLAB);
    public static final RegistryObject<Item> BLACK_WOOL_STAIRS = registerBlockItem("black_wool_stairs",
            ShapedWoodBlocks.BLACK_WOOL_STAIRS);
    public static final RegistryObject<Item> BLACK_WOOL_WALL = registerBlockItem("black_wool_wall",
            ShapedWoodBlocks.BLACK_WOOL_WALL);
    public static final RegistryObject<Item> POPLAR_LOG = registerBlockItem("poplar_log", ShapedWoodBlocks.POPLAR_LOG);
    public static final RegistryObject<Item> POPLAR_PLANKS = registerBlockItem("poplar_planks",
            ShapedWoodBlocks.POPLAR_PLANKS);
    public static final RegistryObject<Item> POPLAR_SAPLING = registerBlockItem("poplar_sapling",
            ShapedWoodBlocks.POPLAR_SAPLING);
    public static final RegistryObject<Item> POPLAR_BUTTON = registerBlockItem("poplar_button",
            ShapedWoodBlocks.POPLAR_BUTTON);
    public static final RegistryObject<Item> POPLAR_DOOR = registerBlockItem("poplar_door",
            ShapedWoodBlocks.POPLAR_DOOR);
    public static final RegistryObject<Item> POPLAR_FENCE = registerBlockItem("poplar_fence",
            ShapedWoodBlocks.POPLAR_FENCE);
    public static final RegistryObject<Item> POPLAR_FENCE_GATE = registerBlockItem("poplar_fence_gate",
            ShapedWoodBlocks.POPLAR_FENCE_GATE);
    public static final RegistryObject<Item> POPLAR_LOG_SLAB = registerBlockItem("poplar_log_slab",
            ShapedWoodBlocks.POPLAR_LOG_SLAB);
    public static final RegistryObject<Item> POPLAR_LOG_STAIRS = registerBlockItem("poplar_log_stairs",
            ShapedWoodBlocks.POPLAR_LOG_STAIRS);
    public static final RegistryObject<Item> POPLAR_LOG_WALL = registerBlockItem("poplar_log_wall",
            ShapedWoodBlocks.POPLAR_LOG_WALL);
    public static final RegistryObject<Item> POPLAR_PRESSURE_PLATE = registerBlockItem("poplar_pressure_plate",
            ShapedWoodBlocks.POPLAR_PRESSURE_PLATE);
    public static final RegistryObject<Item> POPLAR_SLAB = registerBlockItem("poplar_slab",
            ShapedWoodBlocks.POPLAR_SLAB);
    public static final RegistryObject<Item> POPLAR_STAIRS = registerBlockItem("poplar_stairs",
            ShapedWoodBlocks.POPLAR_STAIRS);
    public static final RegistryObject<Item> POPLAR_TRAPDOOR = registerBlockItem("poplar_trapdoor",
            ShapedWoodBlocks.POPLAR_TRAPDOOR);
    public static final RegistryObject<Item> POPLAR_WOOD = registerBlockItem("poplar_wood",
            ShapedWoodBlocks.POPLAR_WOOD);
    public static final RegistryObject<Item> POPLAR_WOOD_SLAB = registerBlockItem("poplar_wood_slab",
            ShapedWoodBlocks.POPLAR_WOOD_SLAB);
    public static final RegistryObject<Item> POPLAR_WOOD_STAIRS = registerBlockItem("poplar_wood_stairs",
            ShapedWoodBlocks.POPLAR_WOOD_STAIRS);
    public static final RegistryObject<Item> STRIPPED_POPLAR_LOG = registerBlockItem("stripped_poplar_log",
            ShapedWoodBlocks.STRIPPED_POPLAR_LOG);
    public static final RegistryObject<Item> STRIPPED_POPLAR_LOG_SLAB = registerBlockItem("stripped_poplar_log_slab",
            ShapedWoodBlocks.STRIPPED_POPLAR_LOG_SLAB);
    public static final RegistryObject<Item> STRIPPED_POPLAR_LOG_STAIRS = registerBlockItem(
            "stripped_poplar_log_stairs", ShapedWoodBlocks.STRIPPED_POPLAR_LOG_STAIRS);
    public static final RegistryObject<Item> STRIPPED_POPLAR_LOG_WALL = registerBlockItem("stripped_poplar_log_wall",
            ShapedWoodBlocks.STRIPPED_POPLAR_LOG_WALL);
    public static final RegistryObject<Item> STRIPPED_POPLAR_WOOD = registerBlockItem("stripped_poplar_wood",
            ShapedWoodBlocks.STRIPPED_POPLAR_WOOD);
    public static final RegistryObject<Item> STRIPPED_POPLAR_WOOD_SLAB = registerBlockItem("stripped_poplar_wood_slab",
            ShapedWoodBlocks.STRIPPED_POPLAR_WOOD_SLAB);
    public static final RegistryObject<Item> STRIPPED_POPLAR_WOOD_STAIRS = registerBlockItem(
            "stripped_poplar_wood_stairs", ShapedWoodBlocks.STRIPPED_POPLAR_WOOD_STAIRS);
    public static final RegistryObject<Item> POPLAR_BOAT = ITEMS.register("poplar_boat",
            () -> new PoplarBoatItem(new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(1)));
    public static final RegistryObject<Item> ORANGE_POPLAR_LEAVES = registerBlockItem("orange_poplar_leaves",
            ShapedWoodBlocks.ORANGE_POPLAR_LEAVES);
    public static final RegistryObject<Item> RED_POPLAR_LEAVES = registerBlockItem("red_poplar_leaves",
            ShapedWoodBlocks.RED_POPLAR_LEAVES);
    public static final RegistryObject<Item> YELLOW_POPLAR_LEAVES = registerBlockItem("yellow_poplar_leaves",
            ShapedWoodBlocks.YELLOW_POPLAR_LEAVES);
    public static final RegistryObject<Item> CINNABAR = registerBlockItem("cinnabar", ShapedStoneBlocks.CINNABAR);
    public static final RegistryObject<Item> POLISHED_CINNABAR = registerBlockItem("polished_cinnabar",
            ShapedStoneBlocks.POLISHED_CINNABAR);
    public static final RegistryObject<Item> CINNABAR_SLAB = registerBlockItem("cinnabar_slab",
            ShapedStoneBlocks.CINNABAR_SLAB);
    public static final RegistryObject<Item> CINNABAR_STAIRS = registerBlockItem("cinnabar_stairs",
            ShapedStoneBlocks.CINNABAR_STAIRS);
    public static final RegistryObject<Item> CINNABAR_WALL = registerBlockItem("cinnabar_wall",
            ShapedStoneBlocks.CINNABAR_WALL);
    public static final RegistryObject<Item> POLISHED_CINNABAR_SLAB = registerBlockItem("polished_cinnabar_slab",
            ShapedStoneBlocks.POLISHED_CINNABAR_SLAB);
    public static final RegistryObject<Item> POLISHED_CINNABAR_STAIRS = registerBlockItem("polished_cinnabar_stairs",
            ShapedStoneBlocks.POLISHED_CINNABAR_STAIRS);
    public static final RegistryObject<Item> POLISHED_CINNABAR_WALL = registerBlockItem("polished_cinnabar_wall",
            ShapedStoneBlocks.POLISHED_CINNABAR_WALL);
    public static final RegistryObject<Item> CINNABAR_BRICKS = registerBlockItem("cinnabar_bricks",
            ShapedStoneBlocks.CINNABAR_BRICKS);
    public static final RegistryObject<Item> CINNABAR_BRICKS_SLAB = registerBlockItem("cinnabar_bricks_slab",
            ShapedWoodBlocks.CINNABAR_BRICKS_SLAB);
    public static final RegistryObject<Item> CINNABAR_BRICKS_STAIRS = registerBlockItem("cinnabar_bricks_stairs",
            ShapedStoneBlocks.CINNABAR_BRICKS_STAIRS);
    public static final RegistryObject<Item> CINNABAR_BRICKS_WALL = registerBlockItem("cinnabar_bricks_wall",
            ShapedWoodBlocks.CINNABAR_BRICKS_WALL);
    public static final RegistryObject<Item> CHISELED_CINNABAR = registerBlockItem("chiseled_cinnabar",
            ShapedStoneBlocks.CHISELED_CINNABAR);
    public static final RegistryObject<Item> SULFUR = registerBlockItem("sulfur", ShapedStoneBlocks.SULFUR);
    public static final RegistryObject<Item> POLISHED_SULFUR = registerBlockItem("polished_sulfur",
            ShapedStoneBlocks.POLISHED_SULFUR);
    public static final RegistryObject<Item> SULFUR_SLAB = registerBlockItem("sulfur_slab",
            ShapedStoneBlocks.SULFUR_SLAB);
    public static final RegistryObject<Item> SULFUR_STAIRS = registerBlockItem("sulfur_stairs",
            ShapedStoneBlocks.SULFUR_STAIRS);
    public static final RegistryObject<Item> SULFUR_WALL = registerBlockItem("sulfur_wall",
            ShapedStoneBlocks.SULFUR_WALL);
    public static final RegistryObject<Item> POLISHED_SULFUR_SLAB = registerBlockItem("polished_sulfur_slab",
            ShapedStoneBlocks.POLISHED_SULFUR_SLAB);
    public static final RegistryObject<Item> POLISHED_SULFUR_STAIRS = registerBlockItem("polished_sulfur_stairs",
            ShapedStoneBlocks.POLISHED_SULFUR_STAIRS);
    public static final RegistryObject<Item> POLISHED_SULFUR_WALL = registerBlockItem("polished_sulfur_wall",
            ShapedStoneBlocks.POLISHED_SULFUR_WALL);
    public static final RegistryObject<Item> SULFUR_BRICKS = registerBlockItem("sulfur_bricks",
            ShapedStoneBlocks.SULFUR_BRICKS);
    public static final RegistryObject<Item> SULFUR_BRICKS_SLAB = registerBlockItem("sulfur_bricks_slab",
            ShapedStoneBlocks.SULFUR_BRICKS_SLAB);
    public static final RegistryObject<Item> SULFUR_BRICKS_STAIRS = registerBlockItem("sulfur_bricks_stairs",
            ShapedStoneBlocks.SULFUR_BRICKS_STAIRS);
    public static final RegistryObject<Item> SULFUR_BRICKS_WALL = registerBlockItem("sulfur_bricks_wall",
            ShapedStoneBlocks.SULFUR_BRICKS_WALL);
    public static final RegistryObject<Item> CHISELED_SULFUR = registerBlockItem("chiseled_sulfur",
            ShapedStoneBlocks.CHISELED_SULFUR);
    public static final RegistryObject<Item> POTENT_SULFUR = registerBlockItem("potent_sulfur",
            ShapedStoneBlocks.POTENT_SULFUR);
    public static final RegistryObject<Item> SULFUR_SPIKE = registerBlockItem("sulfur_spike",
            ShapedStoneBlocks.SULFUR_SPIKE);
    public static final RegistryObject<Item> WHITE_WOOL_VERTICAL_SLAB = registerBlockItem("white_wool_vertical_slab",
            ShapedWoodBlocks.WHITE_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> ORANGE_WOOL_VERTICAL_SLAB = registerBlockItem("orange_wool_vertical_slab",
            ShapedWoodBlocks.ORANGE_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> MAGENTA_WOOL_VERTICAL_SLAB = registerBlockItem(
            "magenta_wool_vertical_slab", ShapedWoodBlocks.MAGENTA_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_WOOL_VERTICAL_SLAB = registerBlockItem(
            "light_blue_wool_vertical_slab", ShapedWoodBlocks.LIGHT_BLUE_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> YELLOW_WOOL_VERTICAL_SLAB = registerBlockItem("yellow_wool_vertical_slab",
            ShapedWoodBlocks.YELLOW_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIME_WOOL_VERTICAL_SLAB = registerBlockItem("lime_wool_vertical_slab",
            ShapedWoodBlocks.LIME_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> PINK_WOOL_VERTICAL_SLAB = registerBlockItem("pink_wool_vertical_slab",
            ShapedWoodBlocks.PINK_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> GRAY_WOOL_VERTICAL_SLAB = registerBlockItem("gray_wool_vertical_slab",
            ShapedWoodBlocks.GRAY_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_WOOL_VERTICAL_SLAB = registerBlockItem(
            "light_gray_wool_vertical_slab", ShapedWoodBlocks.LIGHT_GRAY_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> CYAN_WOOL_VERTICAL_SLAB = registerBlockItem("cyan_wool_vertical_slab",
            ShapedWoodBlocks.CYAN_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> PURPLE_WOOL_VERTICAL_SLAB = registerBlockItem("purple_wool_vertical_slab",
            ShapedWoodBlocks.PURPLE_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLUE_WOOL_VERTICAL_SLAB = registerBlockItem("blue_wool_vertical_slab",
            ShapedWoodBlocks.BLUE_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BROWN_WOOL_VERTICAL_SLAB = registerBlockItem("brown_wool_vertical_slab",
            ShapedWoodBlocks.BROWN_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> GREEN_WOOL_VERTICAL_SLAB = registerBlockItem("green_wool_vertical_slab",
            ShapedWoodBlocks.GREEN_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_WOOL_VERTICAL_SLAB = registerBlockItem("red_wool_vertical_slab",
            ShapedWoodBlocks.RED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLACK_WOOL_VERTICAL_SLAB = registerBlockItem("black_wool_vertical_slab",
            ShapedWoodBlocks.BLACK_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_MOSS_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "red_moss_block_vertical_slab", ShapedWoodBlocks.RED_MOSS_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> ORANGE_MOSS_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "orange_moss_block_vertical_slab", ShapedWoodBlocks.ORANGE_MOSS_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> YELLOW_MOSS_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "yellow_moss_block_vertical_slab", ShapedWoodBlocks.YELLOW_MOSS_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> TUFF_VERTICAL_SLAB = registerBlockItem("tuff_vertical_slab",
            ShapedWoodBlocks.TUFF_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_TUFF_VERTICAL_SLAB = registerBlockItem(
            "polished_tuff_vertical_slab", ShapedWoodBlocks.POLISHED_TUFF_VERTICAL_SLAB);
    public static final RegistryObject<Item> TUFF_BRICK_VERTICAL_SLAB = registerBlockItem("tuff_brick_vertical_slab",
            ShapedWoodBlocks.TUFF_BRICK_VERTICAL_SLAB);
    public static final RegistryObject<Item> SULFUR_VERTICAL_SLAB = registerBlockItem("sulfur_vertical_slab",
            ShapedWoodBlocks.SULFUR_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_SULFUR_VERTICAL_SLAB = registerBlockItem(
            "polished_sulfur_vertical_slab", ShapedWoodBlocks.POLISHED_SULFUR_VERTICAL_SLAB);
    public static final RegistryObject<Item> SULFUR_BRICKS_VERTICAL_SLAB = registerBlockItem(
            "sulfur_bricks_vertical_slab", ShapedWoodBlocks.SULFUR_BRICKS_VERTICAL_SLAB);
    public static final RegistryObject<Item> CINNABAR_VERTICAL_SLAB = registerBlockItem("cinnabar_vertical_slab",
            ShapedWoodBlocks.CINNABAR_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_CINNABAR_VERTICAL_SLAB = registerBlockItem(
            "polished_cinnabar_vertical_slab", ShapedWoodBlocks.POLISHED_CINNABAR_VERTICAL_SLAB);
    public static final RegistryObject<Item> CINNABAR_BRICKS_VERTICAL_SLAB = registerBlockItem(
            "cinnabar_bricks_vertical_slab", ShapedWoodBlocks.CINNABAR_BRICKS_VERTICAL_SLAB);
    public static final RegistryObject<Item> POPLAR_VERTICAL_SLAB = registerBlockItem("poplar_vertical_slab",
            ShapedWoodBlocks.POPLAR_VERTICAL_SLAB);
    public static final RegistryObject<Item> POPLAR_LOG_VERTICAL_SLAB = registerBlockItem("poplar_log_vertical_slab",
            ShapedWoodBlocks.POPLAR_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_POPLAR_LOG_VERTICAL_SLAB = registerBlockItem(
            "stripped_poplar_log_vertical_slab", ShapedWoodBlocks.STRIPPED_POPLAR_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> POPLAR_WOOD_VERTICAL_SLAB = registerBlockItem("poplar_wood_vertical_slab",
            ShapedWoodBlocks.POPLAR_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_POPLAR_WOOD_VERTICAL_SLAB = registerBlockItem(
            "stripped_poplar_wood_vertical_slab", ShapedWoodBlocks.STRIPPED_POPLAR_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> OAK_VERTICAL_SLAB = registerBlockItem("oak_vertical_slab",
            ShapedWoodBlocks.OAK_VERTICAL_SLAB);
    public static final RegistryObject<Item> SPRUCE_VERTICAL_SLAB = registerBlockItem("spruce_vertical_slab",
            ShapedWoodBlocks.SPRUCE_VERTICAL_SLAB);
    public static final RegistryObject<Item> BIRCH_VERTICAL_SLAB = registerBlockItem("birch_vertical_slab",
            ShapedWoodBlocks.BIRCH_VERTICAL_SLAB);
    public static final RegistryObject<Item> JUNGLE_VERTICAL_SLAB = registerBlockItem("jungle_vertical_slab",
            ShapedWoodBlocks.JUNGLE_VERTICAL_SLAB);
    public static final RegistryObject<Item> ACACIA_VERTICAL_SLAB = registerBlockItem("acacia_vertical_slab",
            ShapedWoodBlocks.ACACIA_VERTICAL_SLAB);
    public static final RegistryObject<Item> DARK_OAK_VERTICAL_SLAB = registerBlockItem("dark_oak_vertical_slab",
            ShapedWoodBlocks.DARK_OAK_VERTICAL_SLAB);
    public static final RegistryObject<Item> CRIMSON_VERTICAL_SLAB = registerBlockItem("crimson_vertical_slab",
            ShapedWoodBlocks.CRIMSON_VERTICAL_SLAB);
    public static final RegistryObject<Item> WARPED_VERTICAL_SLAB = registerBlockItem("warped_vertical_slab",
            ShapedWoodBlocks.WARPED_VERTICAL_SLAB);
    public static final RegistryObject<Item> STONE_VERTICAL_SLAB = registerBlockItem("stone_vertical_slab",
            ShapedWoodBlocks.STONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> SMOOTH_STONE_VERTICAL_SLAB = registerBlockItem(
            "smooth_stone_vertical_slab", ShapedWoodBlocks.SMOOTH_STONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> GRANITE_VERTICAL_SLAB = registerBlockItem("granite_vertical_slab",
            ShapedWoodBlocks.GRANITE_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_GRANITE_VERTICAL_SLAB = registerBlockItem(
            "polished_granite_vertical_slab", ShapedWoodBlocks.POLISHED_GRANITE_VERTICAL_SLAB);
    public static final RegistryObject<Item> DIORITE_VERTICAL_SLAB = registerBlockItem("diorite_vertical_slab",
            ShapedWoodBlocks.DIORITE_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_DIORITE_VERTICAL_SLAB = registerBlockItem(
            "polished_diorite_vertical_slab", ShapedWoodBlocks.POLISHED_DIORITE_VERTICAL_SLAB);
    public static final RegistryObject<Item> ANDESITE_VERTICAL_SLAB = registerBlockItem("andesite_vertical_slab",
            ShapedWoodBlocks.ANDESITE_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_ANDESITE_VERTICAL_SLAB = registerBlockItem(
            "polished_andesite_vertical_slab", ShapedWoodBlocks.POLISHED_ANDESITE_VERTICAL_SLAB);
    public static final RegistryObject<Item> COBBLESTONE_VERTICAL_SLAB = registerBlockItem("cobblestone_vertical_slab",
            ShapedWoodBlocks.COBBLESTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> MOSSY_COBBLESTONE_VERTICAL_SLAB = registerBlockItem(
            "mossy_cobblestone_vertical_slab", ShapedWoodBlocks.MOSSY_COBBLESTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> STONE_BRICK_VERTICAL_SLAB = registerBlockItem("stone_brick_vertical_slab",
            ShapedWoodBlocks.STONE_BRICK_VERTICAL_SLAB);
    public static final RegistryObject<Item> MOSSY_STONE_BRICK_VERTICAL_SLAB = registerBlockItem(
            "mossy_stone_brick_vertical_slab", ShapedWoodBlocks.MOSSY_STONE_BRICK_VERTICAL_SLAB);
    public static final RegistryObject<Item> NETHER_BRICK_VERTICAL_SLAB = registerBlockItem(
            "nether_brick_vertical_slab", ShapedWoodBlocks.NETHER_BRICK_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_NETHER_BRICK_VERTICAL_SLAB = registerBlockItem(
            "red_nether_brick_vertical_slab", ShapedWoodBlocks.RED_NETHER_BRICK_VERTICAL_SLAB);
    public static final RegistryObject<Item> OAK_LOG_STAIRS = registerBlockItem("oak_log_stairs",
            ShapedWoodBlocks.OAK_LOG_STAIRS);
    public static final RegistryObject<Item> CUT_COPPER_VERTICAL_SLAB = registerBlockItem("cut_copper_vertical_slab",
            ShapedWoodBlocks.CUT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> EXPOSED_CUT_COPPER_VERTICAL_SLAB = registerBlockItem(
            "exposed_cut_copper_vertical_slab", ShapedWoodBlocks.EXPOSED_CUT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> WEATHERED_CUT_COPPER_VERTICAL_SLAB = registerBlockItem(
            "weathered_cut_copper_vertical_slab", ShapedWoodBlocks.WEATHERED_CUT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> OXIDIZED_CUT_COPPER_VERTICAL_SLAB = registerBlockItem(
            "oxidized_cut_copper_vertical_slab", ShapedWoodBlocks.OXIDIZED_CUT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> WAXED_CUT_COPPER_VERTICAL_SLAB = registerBlockItem(
            "waxed_cut_copper_vertical_slab", ShapedWoodBlocks.WAXED_CUT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> WAXED_EXPOSED_CUT_COPPER_VERTICAL_SLAB = registerBlockItem(
            "waxed_exposed_cut_copper_vertical_slab", ShapedWoodBlocks.WAXED_EXPOSED_CUT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> WAXED_WEATHERED_CUT_COPPER_VERTICAL_SLAB = registerBlockItem(
            "waxed_weathered_cut_copper_vertical_slab", ShapedWoodBlocks.WAXED_WEATHERED_CUT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> WAXED_OXIDIZED_CUT_COPPER_VERTICAL_SLAB = registerBlockItem(
            "waxed_oxidized_cut_copper_vertical_slab", ShapedWoodBlocks.WAXED_OXIDIZED_CUT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> COBBLED_DEEPSLATE_VERTICAL_SLAB = registerBlockItem(
            "cobbled_deepslate_vertical_slab", ShapedWoodBlocks.COBBLED_DEEPSLATE_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_DEEPSLATE_VERTICAL_SLAB = registerBlockItem(
            "polished_deepslate_vertical_slab", ShapedWoodBlocks.POLISHED_DEEPSLATE_VERTICAL_SLAB);
    public static final RegistryObject<Item> DEEPSLATE_BRICK_VERTICAL_SLAB = registerBlockItem(
            "deepslate_brick_vertical_slab", ShapedWoodBlocks.DEEPSLATE_BRICK_VERTICAL_SLAB);
    public static final RegistryObject<Item> DEEPSLATE_TILE_VERTICAL_SLAB = registerBlockItem(
            "deepslate_tile_vertical_slab", ShapedWoodBlocks.DEEPSLATE_TILE_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLACKSTONE_VERTICAL_SLAB = registerBlockItem("blackstone_vertical_slab",
            ShapedWoodBlocks.BLACKSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_BLACKSTONE_VERTICAL_SLAB = registerBlockItem(
            "polished_blackstone_vertical_slab", ShapedWoodBlocks.POLISHED_BLACKSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_BLACKSTONE_BRICK_VERTICAL_SLAB = registerBlockItem(
            "polished_blackstone_brick_vertical_slab", ShapedWoodBlocks.POLISHED_BLACKSTONE_BRICK_VERTICAL_SLAB);
    public static final RegistryObject<Item> PRISMARINE_VERTICAL_SLAB = registerBlockItem("prismarine_vertical_slab",
            ShapedWoodBlocks.PRISMARINE_VERTICAL_SLAB);
    public static final RegistryObject<Item> PRISMARINE_BRICK_VERTICAL_SLAB = registerBlockItem(
            "prismarine_brick_vertical_slab", ShapedWoodBlocks.PRISMARINE_BRICK_VERTICAL_SLAB);
    public static final RegistryObject<Item> DARK_PRISMARINE_VERTICAL_SLAB = registerBlockItem(
            "dark_prismarine_vertical_slab", ShapedWoodBlocks.DARK_PRISMARINE_VERTICAL_SLAB);
    public static final RegistryObject<Item> BRICK_VERTICAL_SLAB = registerBlockItem("brick_vertical_slab",
            ShapedWoodBlocks.BRICK_VERTICAL_SLAB);
    public static final RegistryObject<Item> END_STONE_BRICK_VERTICAL_SLAB = registerBlockItem(
            "end_stone_brick_vertical_slab", ShapedWoodBlocks.END_STONE_BRICK_VERTICAL_SLAB);
    public static final RegistryObject<Item> PURPUR_VERTICAL_SLAB = registerBlockItem("purpur_vertical_slab",
            ShapedWoodBlocks.PURPUR_VERTICAL_SLAB);

    public static final RegistryObject<Item> QUARTZ_VERTICAL_SLAB = registerBlockItem("quartz_vertical_slab",
            ShapedWoodBlocks.QUARTZ_VERTICAL_SLAB);
    public static final RegistryObject<Item> SMOOTH_QUARTZ_VERTICAL_SLAB = registerBlockItem(
            "smooth_quartz_vertical_slab", ShapedWoodBlocks.SMOOTH_QUARTZ_VERTICAL_SLAB);
    public static final RegistryObject<Item> TERRACOTTA_SLAB = registerBlockItem("terracotta_slab",
            ShapedWoodBlocks.TERRACOTTA_SLAB);
    public static final RegistryObject<Item> TERRACOTTA_STAIRS = registerBlockItem("terracotta_stairs",
            ShapedWoodBlocks.TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> TERRACOTTA_VERTICAL_SLAB = registerBlockItem("terracotta_vertical_slab",
            ShapedWoodBlocks.TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> WHITE_TERRACOTTA_SLAB = registerBlockItem("white_terracotta_slab",
            ShapedWoodBlocks.WHITE_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> WHITE_TERRACOTTA_STAIRS = registerBlockItem("white_terracotta_stairs",
            ShapedWoodBlocks.WHITE_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> WHITE_TERRACOTTA_VERTICAL_SLAB = registerBlockItem(
            "white_terracotta_vertical_slab", ShapedWoodBlocks.WHITE_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> ORANGE_TERRACOTTA_SLAB = registerBlockItem("orange_terracotta_slab",
            ShapedWoodBlocks.ORANGE_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> ORANGE_TERRACOTTA_STAIRS = registerBlockItem("orange_terracotta_stairs",
            ShapedWoodBlocks.ORANGE_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> ORANGE_TERRACOTTA_VERTICAL_SLAB = registerBlockItem(
            "orange_terracotta_vertical_slab", ShapedWoodBlocks.ORANGE_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> MAGENTA_TERRACOTTA_SLAB = registerBlockItem("magenta_terracotta_slab",
            ShapedWoodBlocks.MAGENTA_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> MAGENTA_TERRACOTTA_STAIRS = registerBlockItem("magenta_terracotta_stairs",
            ShapedWoodBlocks.MAGENTA_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> MAGENTA_TERRACOTTA_VERTICAL_SLAB = registerBlockItem(
            "magenta_terracotta_vertical_slab", ShapedWoodBlocks.MAGENTA_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_TERRACOTTA_SLAB = registerBlockItem(
            "light_blue_terracotta_slab", ShapedWoodBlocks.LIGHT_BLUE_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_TERRACOTTA_STAIRS = registerBlockItem(
            "light_blue_terracotta_stairs", ShapedWoodBlocks.LIGHT_BLUE_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> LIGHT_BLUE_TERRACOTTA_VERTICAL_SLAB = registerBlockItem(
            "light_blue_terracotta_vertical_slab", ShapedWoodBlocks.LIGHT_BLUE_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> YELLOW_TERRACOTTA_SLAB = registerBlockItem("yellow_terracotta_slab",
            ShapedWoodBlocks.YELLOW_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> YELLOW_TERRACOTTA_STAIRS = registerBlockItem("yellow_terracotta_stairs",
            ShapedWoodBlocks.YELLOW_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> YELLOW_TERRACOTTA_VERTICAL_SLAB = registerBlockItem(
            "yellow_terracotta_vertical_slab", ShapedWoodBlocks.YELLOW_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIME_TERRACOTTA_SLAB = registerBlockItem("lime_terracotta_slab",
            ShapedWoodBlocks.LIME_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> LIME_TERRACOTTA_STAIRS = registerBlockItem("lime_terracotta_stairs",
            ShapedWoodBlocks.LIME_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> LIME_TERRACOTTA_VERTICAL_SLAB = registerBlockItem(
            "lime_terracotta_vertical_slab", ShapedWoodBlocks.LIME_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> PINK_TERRACOTTA_SLAB = registerBlockItem("pink_terracotta_slab",
            ShapedWoodBlocks.PINK_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> PINK_TERRACOTTA_STAIRS = registerBlockItem("pink_terracotta_stairs",
            ShapedWoodBlocks.PINK_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> PINK_TERRACOTTA_VERTICAL_SLAB = registerBlockItem(
            "pink_terracotta_vertical_slab", ShapedWoodBlocks.PINK_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> GRAY_TERRACOTTA_SLAB = registerBlockItem("gray_terracotta_slab",
            ShapedWoodBlocks.GRAY_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> GRAY_TERRACOTTA_STAIRS = registerBlockItem("gray_terracotta_stairs",
            ShapedWoodBlocks.GRAY_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> GRAY_TERRACOTTA_VERTICAL_SLAB = registerBlockItem(
            "gray_terracotta_vertical_slab", ShapedWoodBlocks.GRAY_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_TERRACOTTA_SLAB = registerBlockItem(
            "light_gray_terracotta_slab", ShapedWoodBlocks.LIGHT_GRAY_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_TERRACOTTA_STAIRS = registerBlockItem(
            "light_gray_terracotta_stairs", ShapedWoodBlocks.LIGHT_GRAY_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> LIGHT_GRAY_TERRACOTTA_VERTICAL_SLAB = registerBlockItem(
            "light_gray_terracotta_vertical_slab", ShapedWoodBlocks.LIGHT_GRAY_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> CYAN_TERRACOTTA_SLAB = registerBlockItem("cyan_terracotta_slab",
            ShapedWoodBlocks.CYAN_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> CYAN_TERRACOTTA_STAIRS = registerBlockItem("cyan_terracotta_stairs",
            ShapedWoodBlocks.CYAN_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> CYAN_TERRACOTTA_VERTICAL_SLAB = registerBlockItem(
            "cyan_terracotta_vertical_slab", ShapedWoodBlocks.CYAN_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> PURPLE_TERRACOTTA_SLAB = registerBlockItem("purple_terracotta_slab",
            ShapedWoodBlocks.PURPLE_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> PURPLE_TERRACOTTA_STAIRS = registerBlockItem("purple_terracotta_stairs",
            ShapedWoodBlocks.PURPLE_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> PURPLE_TERRACOTTA_VERTICAL_SLAB = registerBlockItem(
            "purple_terracotta_vertical_slab", ShapedWoodBlocks.PURPLE_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLUE_TERRACOTTA_SLAB = registerBlockItem("blue_terracotta_slab",
            ShapedWoodBlocks.BLUE_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> BLUE_TERRACOTTA_STAIRS = registerBlockItem("blue_terracotta_stairs",
            ShapedWoodBlocks.BLUE_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> BLUE_TERRACOTTA_VERTICAL_SLAB = registerBlockItem(
            "blue_terracotta_vertical_slab", ShapedWoodBlocks.BLUE_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> BROWN_TERRACOTTA_SLAB = registerBlockItem("brown_terracotta_slab",
            ShapedWoodBlocks.BROWN_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> BROWN_TERRACOTTA_STAIRS = registerBlockItem("brown_terracotta_stairs",
            ShapedWoodBlocks.BROWN_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> BROWN_TERRACOTTA_VERTICAL_SLAB = registerBlockItem(
            "brown_terracotta_vertical_slab", ShapedWoodBlocks.BROWN_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> GREEN_TERRACOTTA_SLAB = registerBlockItem("green_terracotta_slab",
            ShapedWoodBlocks.GREEN_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> GREEN_TERRACOTTA_STAIRS = registerBlockItem("green_terracotta_stairs",
            ShapedWoodBlocks.GREEN_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> GREEN_TERRACOTTA_VERTICAL_SLAB = registerBlockItem(
            "green_terracotta_vertical_slab", ShapedWoodBlocks.GREEN_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_TERRACOTTA_SLAB = registerBlockItem("red_terracotta_slab",
            ShapedWoodBlocks.RED_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> RED_TERRACOTTA_STAIRS = registerBlockItem("red_terracotta_stairs",
            ShapedWoodBlocks.RED_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> RED_TERRACOTTA_VERTICAL_SLAB = registerBlockItem(
            "red_terracotta_vertical_slab", ShapedWoodBlocks.RED_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLACK_TERRACOTTA_SLAB = registerBlockItem("black_terracotta_slab",
            ShapedWoodBlocks.BLACK_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> BLACK_TERRACOTTA_STAIRS = registerBlockItem("black_terracotta_stairs",
            ShapedWoodBlocks.BLACK_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> BLACK_TERRACOTTA_VERTICAL_SLAB = registerBlockItem(
            "black_terracotta_vertical_slab", ShapedWoodBlocks.BLACK_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_OAK_LOG_STAIRS = registerBlockItem("stripped_oak_log_stairs",
            ShapedWoodBlocks.STRIPPED_OAK_LOG_STAIRS);
    public static final RegistryObject<Item> SPRUCE_LOG_STAIRS = registerBlockItem("spruce_log_stairs",
            ShapedWoodBlocks.SPRUCE_LOG_STAIRS);
    public static final RegistryObject<Item> STRIPPED_SPRUCE_LOG_STAIRS = registerBlockItem(
            "stripped_spruce_log_stairs", ShapedWoodBlocks.STRIPPED_SPRUCE_LOG_STAIRS);
    public static final RegistryObject<Item> BIRCH_LOG_STAIRS = registerBlockItem("birch_log_stairs",
            ShapedWoodBlocks.BIRCH_LOG_STAIRS);
    public static final RegistryObject<Item> STRIPPED_BIRCH_LOG_STAIRS = registerBlockItem("stripped_birch_log_stairs",
            ShapedWoodBlocks.STRIPPED_BIRCH_LOG_STAIRS);
    public static final RegistryObject<Item> JUNGLE_LOG_STAIRS = registerBlockItem("jungle_log_stairs",
            ShapedWoodBlocks.JUNGLE_LOG_STAIRS);
    public static final RegistryObject<Item> STRIPPED_JUNGLE_LOG_STAIRS = registerBlockItem(
            "stripped_jungle_log_stairs", ShapedWoodBlocks.STRIPPED_JUNGLE_LOG_STAIRS);
    public static final RegistryObject<Item> ACACIA_LOG_STAIRS = registerBlockItem("acacia_log_stairs",
            ShapedWoodBlocks.ACACIA_LOG_STAIRS);
    public static final RegistryObject<Item> STRIPPED_ACACIA_LOG_STAIRS = registerBlockItem(
            "stripped_acacia_log_stairs", ShapedWoodBlocks.STRIPPED_ACACIA_LOG_STAIRS);
    public static final RegistryObject<Item> DARK_OAK_LOG_STAIRS = registerBlockItem("dark_oak_log_stairs",
            ShapedWoodBlocks.DARK_OAK_LOG_STAIRS);
    public static final RegistryObject<Item> STRIPPED_DARK_OAK_LOG_STAIRS = registerBlockItem(
            "stripped_dark_oak_log_stairs", ShapedWoodBlocks.STRIPPED_DARK_OAK_LOG_STAIRS);
    public static final RegistryObject<Item> CRIMSON_STEM_STAIRS = registerBlockItem("crimson_stem_stairs",
            ShapedWoodBlocks.CRIMSON_STEM_STAIRS);
    public static final RegistryObject<Item> STRIPPED_CRIMSON_STEM_STAIRS = registerBlockItem(
            "stripped_crimson_stem_stairs", ShapedWoodBlocks.STRIPPED_CRIMSON_STEM_STAIRS);
    public static final RegistryObject<Item> WARPED_STEM_STAIRS = registerBlockItem("warped_stem_stairs",
            ShapedWoodBlocks.WARPED_STEM_STAIRS);
    public static final RegistryObject<Item> STRIPPED_WARPED_STEM_STAIRS = registerBlockItem(
            "stripped_warped_stem_stairs", ShapedWoodBlocks.STRIPPED_WARPED_STEM_STAIRS);
    public static final RegistryObject<Item> ACACIA_WOOD_SLAB = registerBlockItem("acacia_wood_slab",
            ShapedWoodBlocks.ACACIA_WOOD_SLAB);
    public static final RegistryObject<Item> ACACIA_WOOD_STAIRS = registerBlockItem("acacia_wood_stairs",
            ShapedWoodBlocks.ACACIA_WOOD_STAIRS);
    public static final RegistryObject<Item> ACACIA_WOOD_VERTICAL_SLAB = registerBlockItem("acacia_wood_vertical_slab",
            ShapedWoodBlocks.ACACIA_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> AMETHYST_BLOCK_STAIRS = registerBlockItem("amethyst_block_stairs",
            ShapedWoodBlocks.AMETHYST_BLOCK_STAIRS);
    public static final RegistryObject<Item> BIRCH_WOOD_SLAB = registerBlockItem("birch_wood_slab",
            ShapedWoodBlocks.BIRCH_WOOD_SLAB);
    public static final RegistryObject<Item> BIRCH_WOOD_STAIRS = registerBlockItem("birch_wood_stairs",
            ShapedWoodBlocks.BIRCH_WOOD_STAIRS);
    public static final RegistryObject<Item> BIRCH_WOOD_VERTICAL_SLAB = registerBlockItem("birch_wood_vertical_slab",
            ShapedWoodBlocks.BIRCH_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> CRIMSON_HYPHAE_SLAB = registerBlockItem("crimson_hyphae_slab",
            ShapedWoodBlocks.CRIMSON_HYPHAE_SLAB);
    public static final RegistryObject<Item> CRIMSON_HYPHAE_STAIRS = registerBlockItem("crimson_hyphae_stairs",
            ShapedWoodBlocks.CRIMSON_HYPHAE_STAIRS);
    public static final RegistryObject<Item> CRIMSON_HYPHAE_VERTICAL_SLAB = registerBlockItem(
            "crimson_hyphae_vertical_slab", ShapedWoodBlocks.CRIMSON_HYPHAE_VERTICAL_SLAB);
    public static final RegistryObject<Item> CRYING_OBSIDIAN_SLAB = registerBlockItem("crying_obsidian_slab",
            ShapedWoodBlocks.CRYING_OBSIDIAN_SLAB);
    public static final RegistryObject<Item> CRYING_OBSIDIAN_STAIRS = registerBlockItem("crying_obsidian_stairs",
            ShapedWoodBlocks.CRYING_OBSIDIAN_STAIRS);
    public static final RegistryObject<Item> CRYING_OBSIDIAN_VERTICAL_SLAB = registerBlockItem(
            "crying_obsidian_vertical_slab", ShapedWoodBlocks.CRYING_OBSIDIAN_VERTICAL_SLAB);
    public static final RegistryObject<Item> CUT_RED_SANDSTONE_STAIRS = registerBlockItem("cut_red_sandstone_stairs",
            ShapedWoodBlocks.CUT_RED_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> CUT_RED_SANDSTONE_VERTICAL_SLAB = registerBlockItem(
            "cut_red_sandstone_vertical_slab", ShapedWoodBlocks.CUT_RED_SANDSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> CUT_SANDSTONE_STAIRS = registerBlockItem("cut_sandstone_stairs",
            ShapedWoodBlocks.CUT_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> CUT_SANDSTONE_VERTICAL_SLAB = registerBlockItem(
            "cut_sandstone_vertical_slab", ShapedWoodBlocks.CUT_SANDSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> DARK_OAK_WOOD_SLAB = registerBlockItem("dark_oak_wood_slab",
            ShapedWoodBlocks.DARK_OAK_WOOD_SLAB);
    public static final RegistryObject<Item> DARK_OAK_WOOD_STAIRS = registerBlockItem("dark_oak_wood_stairs",
            ShapedWoodBlocks.DARK_OAK_WOOD_STAIRS);
    public static final RegistryObject<Item> DARK_OAK_WOOD_VERTICAL_SLAB = registerBlockItem(
            "dark_oak_wood_vertical_slab", ShapedWoodBlocks.DARK_OAK_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> DIRT_STAIRS = registerBlockItem("dirt_stairs",
            ShapedWoodBlocks.DIRT_STAIRS);
    public static final RegistryObject<Item> JUNGLE_WOOD_SLAB = registerBlockItem("jungle_wood_slab",
            ShapedWoodBlocks.JUNGLE_WOOD_SLAB);
    public static final RegistryObject<Item> JUNGLE_WOOD_STAIRS = registerBlockItem("jungle_wood_stairs",
            ShapedWoodBlocks.JUNGLE_WOOD_STAIRS);
    public static final RegistryObject<Item> JUNGLE_WOOD_VERTICAL_SLAB = registerBlockItem("jungle_wood_vertical_slab",
            ShapedWoodBlocks.JUNGLE_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> MOSS_BLOCK_STAIRS = registerBlockItem("moss_block_stairs",
            ShapedWoodBlocks.MOSS_BLOCK_STAIRS);
    public static final RegistryObject<Item> MYCELIUM_STAIRS = registerBlockItem("mycelium_stairs",
            ShapedWoodBlocks.MYCELIUM_STAIRS);
    public static final RegistryObject<Item> OAK_WOOD_SLAB = registerBlockItem("oak_wood_slab",
            ShapedWoodBlocks.OAK_WOOD_SLAB);
    public static final RegistryObject<Item> OAK_WOOD_STAIRS = registerBlockItem("oak_wood_stairs",
            ShapedWoodBlocks.OAK_WOOD_STAIRS);
    public static final RegistryObject<Item> OAK_WOOD_VERTICAL_SLAB = registerBlockItem("oak_wood_vertical_slab",
            ShapedWoodBlocks.OAK_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> PODZOL_STAIRS = registerBlockItem("podzol_stairs",
            ShapedWoodBlocks.PODZOL_STAIRS);
    public static final RegistryObject<Item> SANDSTONE_VERTICAL_SLAB = registerBlockItem("sandstone_vertical_slab",
            ShapedWoodBlocks.SANDSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> SMOOTH_RED_SANDSTONE_VERTICAL_SLAB = registerBlockItem(
            "smooth_red_sandstone_vertical_slab", ShapedWoodBlocks.SMOOTH_RED_SANDSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> SMOOTH_SANDSTONE_VERTICAL_SLAB = registerBlockItem(
            "smooth_sandstone_vertical_slab", ShapedWoodBlocks.SMOOTH_SANDSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> SPRUCE_WOOD_SLAB = registerBlockItem("spruce_wood_slab",
            ShapedWoodBlocks.SPRUCE_WOOD_SLAB);
    public static final RegistryObject<Item> SPRUCE_WOOD_STAIRS = registerBlockItem("spruce_wood_stairs",
            ShapedWoodBlocks.SPRUCE_WOOD_STAIRS);
    public static final RegistryObject<Item> SPRUCE_WOOD_VERTICAL_SLAB = registerBlockItem("spruce_wood_vertical_slab",
            ShapedWoodBlocks.SPRUCE_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_ACACIA_WOOD_SLAB = registerBlockItem("stripped_acacia_wood_slab",
            ShapedWoodBlocks.STRIPPED_ACACIA_WOOD_SLAB);
    public static final RegistryObject<Item> STRIPPED_ACACIA_WOOD_STAIRS = registerBlockItem(
            "stripped_acacia_wood_stairs", ShapedWoodBlocks.STRIPPED_ACACIA_WOOD_STAIRS);
    public static final RegistryObject<Item> STRIPPED_ACACIA_WOOD_VERTICAL_SLAB = registerBlockItem(
            "stripped_acacia_wood_vertical_slab", ShapedWoodBlocks.STRIPPED_ACACIA_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_BIRCH_WOOD_SLAB = registerBlockItem("stripped_birch_wood_slab",
            ShapedWoodBlocks.STRIPPED_BIRCH_WOOD_SLAB);
    public static final RegistryObject<Item> STRIPPED_BIRCH_WOOD_STAIRS = registerBlockItem(
            "stripped_birch_wood_stairs", ShapedWoodBlocks.STRIPPED_BIRCH_WOOD_STAIRS);
    public static final RegistryObject<Item> STRIPPED_BIRCH_WOOD_VERTICAL_SLAB = registerBlockItem(
            "stripped_birch_wood_vertical_slab", ShapedWoodBlocks.STRIPPED_BIRCH_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_CRIMSON_HYPHAE_SLAB = registerBlockItem(
            "stripped_crimson_hyphae_slab", ShapedWoodBlocks.STRIPPED_CRIMSON_HYPHAE_SLAB);
    public static final RegistryObject<Item> STRIPPED_CRIMSON_HYPHAE_STAIRS = registerBlockItem(
            "stripped_crimson_hyphae_stairs", ShapedWoodBlocks.STRIPPED_CRIMSON_HYPHAE_STAIRS);
    public static final RegistryObject<Item> STRIPPED_CRIMSON_HYPHAE_VERTICAL_SLAB = registerBlockItem(
            "stripped_crimson_hyphae_vertical_slab", ShapedWoodBlocks.STRIPPED_CRIMSON_HYPHAE_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_DARK_OAK_WOOD_SLAB = registerBlockItem(
            "stripped_dark_oak_wood_slab", ShapedWoodBlocks.STRIPPED_DARK_OAK_WOOD_SLAB);
    public static final RegistryObject<Item> STRIPPED_DARK_OAK_WOOD_STAIRS = registerBlockItem(
            "stripped_dark_oak_wood_stairs", ShapedWoodBlocks.STRIPPED_DARK_OAK_WOOD_STAIRS);
    public static final RegistryObject<Item> STRIPPED_DARK_OAK_WOOD_VERTICAL_SLAB = registerBlockItem(
            "stripped_dark_oak_wood_vertical_slab", ShapedWoodBlocks.STRIPPED_DARK_OAK_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_JUNGLE_WOOD_SLAB = registerBlockItem("stripped_jungle_wood_slab",
            ShapedWoodBlocks.STRIPPED_JUNGLE_WOOD_SLAB);
    public static final RegistryObject<Item> STRIPPED_JUNGLE_WOOD_STAIRS = registerBlockItem(
            "stripped_jungle_wood_stairs", ShapedWoodBlocks.STRIPPED_JUNGLE_WOOD_STAIRS);
    public static final RegistryObject<Item> STRIPPED_JUNGLE_WOOD_VERTICAL_SLAB = registerBlockItem(
            "stripped_jungle_wood_vertical_slab", ShapedWoodBlocks.STRIPPED_JUNGLE_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_OAK_WOOD_SLAB = registerBlockItem("stripped_oak_wood_slab",
            ShapedWoodBlocks.STRIPPED_OAK_WOOD_SLAB);
    public static final RegistryObject<Item> STRIPPED_OAK_WOOD_STAIRS = registerBlockItem("stripped_oak_wood_stairs",
            ShapedWoodBlocks.STRIPPED_OAK_WOOD_STAIRS);
    public static final RegistryObject<Item> STRIPPED_OAK_WOOD_VERTICAL_SLAB = registerBlockItem(
            "stripped_oak_wood_vertical_slab", ShapedWoodBlocks.STRIPPED_OAK_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_SPRUCE_WOOD_SLAB = registerBlockItem("stripped_spruce_wood_slab",
            ShapedWoodBlocks.STRIPPED_SPRUCE_WOOD_SLAB);
    public static final RegistryObject<Item> STRIPPED_SPRUCE_WOOD_STAIRS = registerBlockItem(
            "stripped_spruce_wood_stairs", ShapedWoodBlocks.STRIPPED_SPRUCE_WOOD_STAIRS);
    public static final RegistryObject<Item> STRIPPED_SPRUCE_WOOD_VERTICAL_SLAB = registerBlockItem(
            "stripped_spruce_wood_vertical_slab", ShapedWoodBlocks.STRIPPED_SPRUCE_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_WARPED_HYPHAE_SLAB = registerBlockItem(
            "stripped_warped_hyphae_slab", ShapedWoodBlocks.STRIPPED_WARPED_HYPHAE_SLAB);
    public static final RegistryObject<Item> STRIPPED_WARPED_HYPHAE_STAIRS = registerBlockItem(
            "stripped_warped_hyphae_stairs", ShapedWoodBlocks.STRIPPED_WARPED_HYPHAE_STAIRS);
    public static final RegistryObject<Item> STRIPPED_WARPED_HYPHAE_VERTICAL_SLAB = registerBlockItem(
            "stripped_warped_hyphae_vertical_slab", ShapedWoodBlocks.STRIPPED_WARPED_HYPHAE_VERTICAL_SLAB);
    public static final RegistryObject<Item> TINTED_GLASS_SLAB = registerBlockItem("tinted_glass_slab",
            ShapedWoodBlocks.TINTED_GLASS_SLAB);
    public static final RegistryObject<Item> TINTED_GLASS_STAIRS = registerBlockItem("tinted_glass_stairs",
            ShapedWoodBlocks.TINTED_GLASS_STAIRS);
    public static final RegistryObject<Item> TINTED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "tinted_glass_vertical_slab", ShapedWoodBlocks.TINTED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> WARPED_HYPHAE_SLAB = registerBlockItem("warped_hyphae_slab",
            ShapedWoodBlocks.WARPED_HYPHAE_SLAB);
    public static final RegistryObject<Item> WARPED_HYPHAE_STAIRS = registerBlockItem("warped_hyphae_stairs",
            ShapedWoodBlocks.WARPED_HYPHAE_STAIRS);
    public static final RegistryObject<Item> WARPED_HYPHAE_VERTICAL_SLAB = registerBlockItem(
            "warped_hyphae_vertical_slab", ShapedWoodBlocks.WARPED_HYPHAE_VERTICAL_SLAB);
    public static final RegistryObject<Item> MUFF_BLOCK = ITEMS.register("muff_block",
            () -> new MuffBlockItem(ShapedWoodBlocks.MUFF_BLOCK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WHITE_WALLPAPER = registerBlockItem("white_wallpaper",
            ShapedWoodBlocks.WHITE_WALLPAPER);
    public static final RegistryObject<Item> ORANGE_WALLPAPER = registerBlockItem("orange_wallpaper",
            ShapedWoodBlocks.ORANGE_WALLPAPER);
    public static final RegistryObject<Item> MAGENTA_WALLPAPER = registerBlockItem("magenta_wallpaper",
            ShapedWoodBlocks.MAGENTA_WALLPAPER);
    public static final RegistryObject<Item> LIGHT_BLUE_WALLPAPER = registerBlockItem("light_blue_wallpaper",
            ShapedWoodBlocks.LIGHT_BLUE_WALLPAPER);
    public static final RegistryObject<Item> YELLOW_WALLPAPER = registerBlockItem("yellow_wallpaper",
            ShapedWoodBlocks.YELLOW_WALLPAPER);
    public static final RegistryObject<Item> LIME_WALLPAPER = registerBlockItem("lime_wallpaper",
            ShapedWoodBlocks.LIME_WALLPAPER);
    public static final RegistryObject<Item> PINK_WALLPAPER = registerBlockItem("pink_wallpaper",
            ShapedWoodBlocks.PINK_WALLPAPER);
    public static final RegistryObject<Item> GRAY_WALLPAPER = registerBlockItem("gray_wallpaper",
            ShapedWoodBlocks.GRAY_WALLPAPER);
    public static final RegistryObject<Item> LIGHT_GRAY_WALLPAPER = registerBlockItem("light_gray_wallpaper",
            ShapedWoodBlocks.LIGHT_GRAY_WALLPAPER);
    public static final RegistryObject<Item> CYAN_WALLPAPER = registerBlockItem("cyan_wallpaper",
            ShapedWoodBlocks.CYAN_WALLPAPER);
    public static final RegistryObject<Item> PURPLE_WALLPAPER = registerBlockItem("purple_wallpaper",
            ShapedWoodBlocks.PURPLE_WALLPAPER);
    public static final RegistryObject<Item> BLUE_WALLPAPER = registerBlockItem("blue_wallpaper",
            ShapedWoodBlocks.BLUE_WALLPAPER);
    public static final RegistryObject<Item> BROWN_WALLPAPER = registerBlockItem("brown_wallpaper",
            ShapedWoodBlocks.BROWN_WALLPAPER);
    public static final RegistryObject<Item> GREEN_WALLPAPER = registerBlockItem("green_wallpaper",
            ShapedWoodBlocks.GREEN_WALLPAPER);
    public static final RegistryObject<Item> RED_WALLPAPER = registerBlockItem("red_wallpaper",
            ShapedWoodBlocks.RED_WALLPAPER);
    public static final RegistryObject<Item> BLACK_WALLPAPER = registerBlockItem("black_wallpaper",
            ShapedWoodBlocks.BLACK_WALLPAPER);
    public static final RegistryObject<Item> WHITE_WALLPAPER_SLAB = registerBlockItem("white_wallpaper_slab",
            ShapedWoodBlocks.WHITE_WALLPAPER_SLAB);
    public static final RegistryObject<Item> WHITE_WALLPAPER_VERTICAL_SLAB = registerBlockItem(
            "white_wallpaper_vertical_slab", ShapedWoodBlocks.WHITE_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> ORANGE_WALLPAPER_SLAB = registerBlockItem("orange_wallpaper_slab",
            ShapedWoodBlocks.ORANGE_WALLPAPER_SLAB);
    public static final RegistryObject<Item> ORANGE_WALLPAPER_VERTICAL_SLAB = registerBlockItem(
            "orange_wallpaper_vertical_slab", ShapedWoodBlocks.ORANGE_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> MAGENTA_WALLPAPER_SLAB = registerBlockItem("magenta_wallpaper_slab",
            ShapedWoodBlocks.MAGENTA_WALLPAPER_SLAB);
    public static final RegistryObject<Item> MAGENTA_WALLPAPER_VERTICAL_SLAB = registerBlockItem(
            "magenta_wallpaper_vertical_slab", ShapedWoodBlocks.MAGENTA_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_WALLPAPER_SLAB = registerBlockItem("light_blue_wallpaper_slab",
            ShapedWoodBlocks.LIGHT_BLUE_WALLPAPER_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_WALLPAPER_VERTICAL_SLAB = registerBlockItem(
            "light_blue_wallpaper_vertical_slab", ShapedWoodBlocks.LIGHT_BLUE_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> YELLOW_WALLPAPER_SLAB = registerBlockItem("yellow_wallpaper_slab",
            ShapedWoodBlocks.YELLOW_WALLPAPER_SLAB);
    public static final RegistryObject<Item> YELLOW_WALLPAPER_VERTICAL_SLAB = registerBlockItem(
            "yellow_wallpaper_vertical_slab", ShapedWoodBlocks.YELLOW_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIME_WALLPAPER_SLAB = registerBlockItem("lime_wallpaper_slab",
            ShapedWoodBlocks.LIME_WALLPAPER_SLAB);
    public static final RegistryObject<Item> LIME_WALLPAPER_VERTICAL_SLAB = registerBlockItem(
            "lime_wallpaper_vertical_slab", ShapedWoodBlocks.LIME_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> PINK_WALLPAPER_SLAB = registerBlockItem("pink_wallpaper_slab",
            ShapedWoodBlocks.PINK_WALLPAPER_SLAB);
    public static final RegistryObject<Item> PINK_WALLPAPER_VERTICAL_SLAB = registerBlockItem(
            "pink_wallpaper_vertical_slab", ShapedWoodBlocks.PINK_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> GRAY_WALLPAPER_SLAB = registerBlockItem("gray_wallpaper_slab",
            ShapedWoodBlocks.GRAY_WALLPAPER_SLAB);
    public static final RegistryObject<Item> GRAY_WALLPAPER_VERTICAL_SLAB = registerBlockItem(
            "gray_wallpaper_vertical_slab", ShapedWoodBlocks.GRAY_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_WALLPAPER_SLAB = registerBlockItem("light_gray_wallpaper_slab",
            ShapedWoodBlocks.LIGHT_GRAY_WALLPAPER_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_WALLPAPER_VERTICAL_SLAB = registerBlockItem(
            "light_gray_wallpaper_vertical_slab", ShapedWoodBlocks.LIGHT_GRAY_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> CYAN_WALLPAPER_SLAB = registerBlockItem("cyan_wallpaper_slab",
            ShapedWoodBlocks.CYAN_WALLPAPER_SLAB);
    public static final RegistryObject<Item> CYAN_WALLPAPER_VERTICAL_SLAB = registerBlockItem(
            "cyan_wallpaper_vertical_slab", ShapedWoodBlocks.CYAN_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> PURPLE_WALLPAPER_SLAB = registerBlockItem("purple_wallpaper_slab",
            ShapedWoodBlocks.PURPLE_WALLPAPER_SLAB);
    public static final RegistryObject<Item> PURPLE_WALLPAPER_VERTICAL_SLAB = registerBlockItem(
            "purple_wallpaper_vertical_slab", ShapedWoodBlocks.PURPLE_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLUE_WALLPAPER_SLAB = registerBlockItem("blue_wallpaper_slab",
            ShapedWoodBlocks.BLUE_WALLPAPER_SLAB);
    public static final RegistryObject<Item> BLUE_WALLPAPER_VERTICAL_SLAB = registerBlockItem(
            "blue_wallpaper_vertical_slab", ShapedWoodBlocks.BLUE_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> BROWN_WALLPAPER_SLAB = registerBlockItem("brown_wallpaper_slab",
            ShapedWoodBlocks.BROWN_WALLPAPER_SLAB);
    public static final RegistryObject<Item> BROWN_WALLPAPER_VERTICAL_SLAB = registerBlockItem(
            "brown_wallpaper_vertical_slab", ShapedWoodBlocks.BROWN_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> GREEN_WALLPAPER_SLAB = registerBlockItem("green_wallpaper_slab",
            ShapedWoodBlocks.GREEN_WALLPAPER_SLAB);
    public static final RegistryObject<Item> GREEN_WALLPAPER_VERTICAL_SLAB = registerBlockItem(
            "green_wallpaper_vertical_slab", ShapedWoodBlocks.GREEN_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_WALLPAPER_SLAB = registerBlockItem("red_wallpaper_slab",
            ShapedWoodBlocks.RED_WALLPAPER_SLAB);
    public static final RegistryObject<Item> RED_WALLPAPER_VERTICAL_SLAB = registerBlockItem(
            "red_wallpaper_vertical_slab", ShapedWoodBlocks.RED_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLACK_WALLPAPER_SLAB = registerBlockItem("black_wallpaper_slab",
            ShapedWoodBlocks.BLACK_WALLPAPER_SLAB);
    public static final RegistryObject<Item> BLACK_WALLPAPER_VERTICAL_SLAB = registerBlockItem(
            "black_wallpaper_vertical_slab", ShapedWoodBlocks.BLACK_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> WHITE_WALLPAPER_FLAT = registerBlockItem("white_wallpaper_flat",
            ShapedWoodBlocks.WHITE_WALLPAPER_FLAT);
    public static final RegistryObject<Item> ORANGE_WALLPAPER_FLAT = registerBlockItem("orange_wallpaper_flat",
            ShapedWoodBlocks.ORANGE_WALLPAPER_FLAT);
    public static final RegistryObject<Item> MAGENTA_WALLPAPER_FLAT = registerBlockItem("magenta_wallpaper_flat",
            ShapedWoodBlocks.MAGENTA_WALLPAPER_FLAT);
    public static final RegistryObject<Item> LIGHT_BLUE_WALLPAPER_FLAT = registerBlockItem("light_blue_wallpaper_flat",
            ShapedWoodBlocks.LIGHT_BLUE_WALLPAPER_FLAT);
    public static final RegistryObject<Item> YELLOW_WALLPAPER_FLAT = registerBlockItem("yellow_wallpaper_flat",
            ShapedWoodBlocks.YELLOW_WALLPAPER_FLAT);
    public static final RegistryObject<Item> LIME_WALLPAPER_FLAT = registerBlockItem("lime_wallpaper_flat",
            ShapedWoodBlocks.LIME_WALLPAPER_FLAT);
    public static final RegistryObject<Item> PINK_WALLPAPER_FLAT = registerBlockItem("pink_wallpaper_flat",
            ShapedWoodBlocks.PINK_WALLPAPER_FLAT);
    public static final RegistryObject<Item> GRAY_WALLPAPER_FLAT = registerBlockItem("gray_wallpaper_flat",
            ShapedWoodBlocks.GRAY_WALLPAPER_FLAT);
    public static final RegistryObject<Item> LIGHT_GRAY_WALLPAPER_FLAT = registerBlockItem("light_gray_wallpaper_flat",
            ShapedWoodBlocks.LIGHT_GRAY_WALLPAPER_FLAT);
    public static final RegistryObject<Item> CYAN_WALLPAPER_FLAT = registerBlockItem("cyan_wallpaper_flat",
            ShapedWoodBlocks.CYAN_WALLPAPER_FLAT);
    public static final RegistryObject<Item> PURPLE_WALLPAPER_FLAT = registerBlockItem("purple_wallpaper_flat",
            ShapedWoodBlocks.PURPLE_WALLPAPER_FLAT);
    public static final RegistryObject<Item> BLUE_WALLPAPER_FLAT = registerBlockItem("blue_wallpaper_flat",
            ShapedWoodBlocks.BLUE_WALLPAPER_FLAT);
    public static final RegistryObject<Item> BROWN_WALLPAPER_FLAT = registerBlockItem("brown_wallpaper_flat",
            ShapedWoodBlocks.BROWN_WALLPAPER_FLAT);
    public static final RegistryObject<Item> GREEN_WALLPAPER_FLAT = registerBlockItem("green_wallpaper_flat",
            ShapedWoodBlocks.GREEN_WALLPAPER_FLAT);
    public static final RegistryObject<Item> RED_WALLPAPER_FLAT = registerBlockItem("red_wallpaper_flat",
            ShapedWoodBlocks.RED_WALLPAPER_FLAT);
    public static final RegistryObject<Item> BLACK_WALLPAPER_FLAT = registerBlockItem("black_wallpaper_flat",
            ShapedWoodBlocks.BLACK_WALLPAPER_FLAT);
    public static final RegistryObject<Item> WHITE_CUSHION = registerBlockItem("white_cushion",
            ShapedWoodBlocks.WHITE_CUSHION);
    public static final RegistryObject<Item> ORANGE_CUSHION = registerBlockItem("orange_cushion",
            ShapedWoodBlocks.ORANGE_CUSHION);
    public static final RegistryObject<Item> MAGENTA_CUSHION = registerBlockItem("magenta_cushion",
            ShapedWoodBlocks.MAGENTA_CUSHION);
    public static final RegistryObject<Item> LIGHT_BLUE_CUSHION = registerBlockItem("light_blue_cushion",
            ShapedWoodBlocks.LIGHT_BLUE_CUSHION);
    public static final RegistryObject<Item> YELLOW_CUSHION = registerBlockItem("yellow_cushion",
            ShapedWoodBlocks.YELLOW_CUSHION);
    public static final RegistryObject<Item> LIME_CUSHION = registerBlockItem("lime_cushion",
            ShapedWoodBlocks.LIME_CUSHION);
    public static final RegistryObject<Item> PINK_CUSHION = registerBlockItem("pink_cushion",
            ShapedWoodBlocks.PINK_CUSHION);
    public static final RegistryObject<Item> GRAY_CUSHION = registerBlockItem("gray_cushion",
            ShapedWoodBlocks.GRAY_CUSHION);
    public static final RegistryObject<Item> LIGHT_GRAY_CUSHION = registerBlockItem("light_gray_cushion",
            ShapedWoodBlocks.LIGHT_GRAY_CUSHION);
    public static final RegistryObject<Item> CYAN_CUSHION = registerBlockItem("cyan_cushion",
            ShapedWoodBlocks.CYAN_CUSHION);
    public static final RegistryObject<Item> PURPLE_CUSHION = registerBlockItem("purple_cushion",
            ShapedWoodBlocks.PURPLE_CUSHION);
    public static final RegistryObject<Item> BLUE_CUSHION = registerBlockItem("blue_cushion",
            ShapedWoodBlocks.BLUE_CUSHION);
    public static final RegistryObject<Item> BROWN_CUSHION = registerBlockItem("brown_cushion",
            ShapedWoodBlocks.BROWN_CUSHION);
    public static final RegistryObject<Item> GREEN_CUSHION = registerBlockItem("green_cushion",
            ShapedWoodBlocks.GREEN_CUSHION);
    public static final RegistryObject<Item> RED_CUSHION = registerBlockItem("red_cushion",
            ShapedWoodBlocks.RED_CUSHION);
    public static final RegistryObject<Item> BLACK_CUSHION = registerBlockItem("black_cushion",
            ShapedWoodBlocks.BLACK_CUSHION);

    public static final RegistryObject<Item> STRAW_BED = ITEMS.register("straw_bed",
            () -> new BedItem(ShapedWoodBlocks.STRAW_BED.get(),
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(16)));
    public static final RegistryObject<Item> BIG_BOOK = registerBlockItem("big_book", ShapedWoodBlocks.BIG_BOOK);
    public static final RegistryObject<Item> GLOWING_BIG_BOOK = registerBlockItem("glowing_big_book",
            ShapedWoodBlocks.GLOWING_BIG_BOOK);
    public static final RegistryObject<Item> WHITE_BIG_BOOK = registerBlockItem("white_big_book",
            ShapedWoodBlocks.WHITE_BIG_BOOK);
    public static final RegistryObject<Item> ORANGE_BIG_BOOK = registerBlockItem("orange_big_book",
            ShapedWoodBlocks.ORANGE_BIG_BOOK);
    public static final RegistryObject<Item> MAGENTA_BIG_BOOK = registerBlockItem("magenta_big_book",
            ShapedWoodBlocks.MAGENTA_BIG_BOOK);
    public static final RegistryObject<Item> LIGHT_BLUE_BIG_BOOK = registerBlockItem("light_blue_big_book",
            ShapedWoodBlocks.LIGHT_BLUE_BIG_BOOK);
    public static final RegistryObject<Item> YELLOW_BIG_BOOK = registerBlockItem("yellow_big_book",
            ShapedWoodBlocks.YELLOW_BIG_BOOK);
    public static final RegistryObject<Item> LIME_BIG_BOOK = registerBlockItem("lime_big_book",
            ShapedWoodBlocks.LIME_BIG_BOOK);
    public static final RegistryObject<Item> PINK_BIG_BOOK = registerBlockItem("pink_big_book",
            ShapedWoodBlocks.PINK_BIG_BOOK);
    public static final RegistryObject<Item> GRAY_BIG_BOOK = registerBlockItem("gray_big_book",
            ShapedWoodBlocks.GRAY_BIG_BOOK);
    public static final RegistryObject<Item> LIGHT_GRAY_BIG_BOOK = registerBlockItem("light_gray_big_book",
            ShapedWoodBlocks.LIGHT_GRAY_BIG_BOOK);
    public static final RegistryObject<Item> CYAN_BIG_BOOK = registerBlockItem("cyan_big_book",
            ShapedWoodBlocks.CYAN_BIG_BOOK);
    public static final RegistryObject<Item> PURPLE_BIG_BOOK = registerBlockItem("purple_big_book",
            ShapedWoodBlocks.PURPLE_BIG_BOOK);
    public static final RegistryObject<Item> BLUE_BIG_BOOK = registerBlockItem("blue_big_book",
            ShapedWoodBlocks.BLUE_BIG_BOOK);
    public static final RegistryObject<Item> BROWN_BIG_BOOK = registerBlockItem("brown_big_book",
            ShapedWoodBlocks.BROWN_BIG_BOOK);
    public static final RegistryObject<Item> GREEN_BIG_BOOK = registerBlockItem("green_big_book",
            ShapedWoodBlocks.GREEN_BIG_BOOK);
    public static final RegistryObject<Item> RED_BIG_BOOK = registerBlockItem("red_big_book",
            ShapedWoodBlocks.RED_BIG_BOOK);
    public static final RegistryObject<Item> BLACK_BIG_BOOK = registerBlockItem("black_big_book",
            ShapedWoodBlocks.BLACK_BIG_BOOK);
    public static final RegistryObject<Item> PALE_OAK_LOG = registerBlockItem("pale_oak_log",
            ShapedWoodBlocks.PALE_OAK_LOG);
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_LOG = registerBlockItem("stripped_pale_oak_log",
            ShapedWoodBlocks.STRIPPED_PALE_OAK_LOG);
    public static final RegistryObject<Item> PALE_OAK_WOOD = registerBlockItem("pale_oak_wood",
            ShapedWoodBlocks.PALE_OAK_WOOD);
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_WOOD = registerBlockItem("stripped_pale_oak_wood",
            ShapedWoodBlocks.STRIPPED_PALE_OAK_WOOD);
    public static final RegistryObject<Item> PALE_OAK_PLANKS = registerBlockItem("pale_oak_planks",
            ShapedWoodBlocks.PALE_OAK_PLANKS);
    public static final RegistryObject<Item> PALE_OAK_STAIRS = registerBlockItem("pale_oak_stairs",
            ShapedWoodBlocks.PALE_OAK_STAIRS);
    public static final RegistryObject<Item> PALE_OAK_SLAB = registerBlockItem("pale_oak_slab",
            ShapedWoodBlocks.PALE_OAK_SLAB);
    public static final RegistryObject<Item> PALE_OAK_VERTICAL_SLAB = registerBlockItem("pale_oak_vertical_slab",
            ShapedWoodBlocks.PALE_OAK_VERTICAL_SLAB);
    public static final RegistryObject<Item> PALE_OAK_FENCE = registerBlockItem("pale_oak_fence",
            ShapedWoodBlocks.PALE_OAK_FENCE);
    public static final RegistryObject<Item> PALE_OAK_FENCE_GATE = registerBlockItem("pale_oak_fence_gate",
            ShapedWoodBlocks.PALE_OAK_FENCE_GATE);
    public static final RegistryObject<Item> PALE_OAK_DOOR = ITEMS.register("pale_oak_door",
            () -> new DoubleHighBlockItem(ShapedWoodBlocks.PALE_OAK_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PALE_OAK_TRAPDOOR = registerBlockItem("pale_oak_trapdoor",
            ShapedWoodBlocks.PALE_OAK_TRAPDOOR);
    public static final RegistryObject<Item> PALE_OAK_BUTTON = registerBlockItem("pale_oak_button",
            ShapedWoodBlocks.PALE_OAK_BUTTON);
    public static final RegistryObject<Item> PALE_OAK_PRESSURE_PLATE = registerBlockItem("pale_oak_pressure_plate",
            ShapedWoodBlocks.PALE_OAK_PRESSURE_PLATE);
    public static final RegistryObject<Item> PALE_OAK_LEAVES = registerBlockItem("pale_oak_leaves",
            ShapedWoodBlocks.PALE_OAK_LEAVES);
    public static final RegistryObject<Item> PALE_OAK_SAPLING = registerBlockItem("pale_oak_sapling",
            ShapedWoodBlocks.PALE_OAK_SAPLING);
    public static final RegistryObject<Item> PALE_OAK_LOG_SLAB = registerBlockItem("pale_oak_log_slab",
            ShapedWoodBlocks.PALE_OAK_LOG_SLAB);
    public static final RegistryObject<Item> PALE_OAK_LOG_VERTICAL_SLAB = registerBlockItem(
            "pale_oak_log_vertical_slab", ShapedWoodBlocks.PALE_OAK_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_LOG_SLAB = registerBlockItem(
            "stripped_pale_oak_log_slab", ShapedWoodBlocks.STRIPPED_PALE_OAK_LOG_SLAB);
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_LOG_VERTICAL_SLAB = registerBlockItem(
            "stripped_pale_oak_log_vertical_slab", ShapedWoodBlocks.STRIPPED_PALE_OAK_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> PALE_OAK_WOOD_SLAB = registerBlockItem("pale_oak_wood_slab",
            ShapedWoodBlocks.PALE_OAK_WOOD_SLAB);
    public static final RegistryObject<Item> PALE_OAK_WOOD_VERTICAL_SLAB = registerBlockItem(
            "pale_oak_wood_vertical_slab", ShapedWoodBlocks.PALE_OAK_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_WOOD_SLAB = registerBlockItem(
            "stripped_pale_oak_wood_slab", ShapedWoodBlocks.STRIPPED_PALE_OAK_WOOD_SLAB);
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_WOOD_VERTICAL_SLAB = registerBlockItem(
            "stripped_pale_oak_wood_vertical_slab", ShapedWoodBlocks.STRIPPED_PALE_OAK_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> CHERRY_LOG = registerBlockItem("cherry_log", ShapedWoodBlocks.CHERRY_LOG);
    public static final RegistryObject<Item> STRIPPED_CHERRY_LOG = registerBlockItem("stripped_cherry_log",
            ShapedWoodBlocks.STRIPPED_CHERRY_LOG);
    public static final RegistryObject<Item> CHERRY_WOOD = registerBlockItem("cherry_wood",
            ShapedWoodBlocks.CHERRY_WOOD);
    public static final RegistryObject<Item> STRIPPED_CHERRY_WOOD = registerBlockItem("stripped_cherry_wood",
            ShapedWoodBlocks.STRIPPED_CHERRY_WOOD);
    public static final RegistryObject<Item> CHERRY_PLANKS = registerBlockItem("cherry_planks",
            ShapedWoodBlocks.CHERRY_PLANKS);
    public static final RegistryObject<Item> CHERRY_STAIRS = registerBlockItem("cherry_stairs",
            ShapedWoodBlocks.CHERRY_STAIRS);
    public static final RegistryObject<Item> CHERRY_SLAB = registerBlockItem("cherry_slab",
            ShapedWoodBlocks.CHERRY_SLAB);
    public static final RegistryObject<Item> CHERRY_VERTICAL_SLAB = registerBlockItem("cherry_vertical_slab",
            ShapedWoodBlocks.CHERRY_VERTICAL_SLAB);
    public static final RegistryObject<Item> CHERRY_FENCE = registerBlockItem("cherry_fence",
            ShapedWoodBlocks.CHERRY_FENCE);
    public static final RegistryObject<Item> CHERRY_FENCE_GATE = registerBlockItem("cherry_fence_gate",
            ShapedWoodBlocks.CHERRY_FENCE_GATE);
    public static final RegistryObject<Item> CHERRY_DOOR = ITEMS.register("cherry_door",
            () -> new DoubleHighBlockItem(ShapedWoodBlocks.CHERRY_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CHERRY_TRAPDOOR = registerBlockItem("cherry_trapdoor",
            ShapedWoodBlocks.CHERRY_TRAPDOOR);
    public static final RegistryObject<Item> CHERRY_BUTTON = registerBlockItem("cherry_button",
            ShapedWoodBlocks.CHERRY_BUTTON);
    public static final RegistryObject<Item> CHERRY_PRESSURE_PLATE = registerBlockItem("cherry_pressure_plate",
            ShapedWoodBlocks.CHERRY_PRESSURE_PLATE);
    public static final RegistryObject<Item> CHERRY_LEAVES = registerBlockItem("cherry_leaves",
            ShapedWoodBlocks.CHERRY_LEAVES);
    public static final RegistryObject<Item> CHERRY_SAPLING = registerBlockItem("cherry_sapling",
            ShapedWoodBlocks.CHERRY_SAPLING);
    public static final RegistryObject<Item> CHERRY_LOG_SLAB = registerBlockItem("cherry_log_slab",
            ShapedWoodBlocks.CHERRY_LOG_SLAB);
    public static final RegistryObject<Item> CHERRY_LOG_VERTICAL_SLAB = registerBlockItem("cherry_log_vertical_slab",
            ShapedWoodBlocks.CHERRY_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_CHERRY_LOG_SLAB = registerBlockItem("stripped_cherry_log_slab",
            ShapedWoodBlocks.STRIPPED_CHERRY_LOG_SLAB);
    public static final RegistryObject<Item> STRIPPED_CHERRY_LOG_VERTICAL_SLAB = registerBlockItem(
            "stripped_cherry_log_vertical_slab", ShapedWoodBlocks.STRIPPED_CHERRY_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> CHERRY_WOOD_SLAB = registerBlockItem("cherry_wood_slab",
            ShapedWoodBlocks.CHERRY_WOOD_SLAB);
    public static final RegistryObject<Item> CHERRY_WOOD_VERTICAL_SLAB = registerBlockItem("cherry_wood_vertical_slab",
            ShapedWoodBlocks.CHERRY_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_CHERRY_WOOD_SLAB = registerBlockItem("stripped_cherry_wood_slab",
            ShapedWoodBlocks.STRIPPED_CHERRY_WOOD_SLAB);
    public static final RegistryObject<Item> STRIPPED_CHERRY_WOOD_VERTICAL_SLAB = registerBlockItem(
            "stripped_cherry_wood_vertical_slab", ShapedWoodBlocks.STRIPPED_CHERRY_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> PALE_MOSS_BLOCK = registerBlockItem("pale_moss_block",
            CopperBlocks.PALE_MOSS_BLOCK);
}
