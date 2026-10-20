package com.kingodogo.buildscape.registry.items;

import com.kingodogo.buildscape.registry.blocks.SandstoneBlocks;
import com.kingodogo.buildscape.registry.blocks.StoneBlocks;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import static com.kingodogo.buildscape.registry.items.ItemsRegister.registerBlockItem;

/**
 * The items BLACK_SAND through DECORATED_POT of the registry, part 1 of 8, in registration order.
 * Named for its dominant families (stone, glass, copper); the neighbouring families that share a run of the order
 * with them are registered here too, because the registration order must not change.
 * Load order: this is the first holder; the registry itself is {@code ItemsRegister}.
 */
public final class StoneItems {

    private StoneItems() {}

    /** Forces this class to load, which registers its items. */
    public static void init() {}

    public static final RegistryObject<Item> BLACK_SAND = registerBlockItem("black_sand", SandstoneBlocks.BLACK_SAND);
    public static final RegistryObject<Item> BLUE_SAND = registerBlockItem("blue_sand", SandstoneBlocks.BLUE_SAND);
    public static final RegistryObject<Item> GREEN_SAND = registerBlockItem("green_sand", SandstoneBlocks.GREEN_SAND);
    public static final RegistryObject<Item> ORANGE_SAND = registerBlockItem("orange_sand",
            SandstoneBlocks.ORANGE_SAND);
    public static final RegistryObject<Item> PINK_SAND = registerBlockItem("pink_sand", SandstoneBlocks.PINK_SAND);
    public static final RegistryObject<Item> RED_SAND = registerBlockItem("red_sand", SandstoneBlocks.RED_SAND);
    public static final RegistryObject<Item> WHITE_SAND = registerBlockItem("white_sand", SandstoneBlocks.WHITE_SAND);
    public static final RegistryObject<Item> YELLOW_SAND = registerBlockItem("yellow_sand",
            SandstoneBlocks.YELLOW_SAND);
    public static final RegistryObject<Item> BLACK_SANDSTONE = registerBlockItem("black_sandstone",
            SandstoneBlocks.BLACK_SANDSTONE);
    public static final RegistryObject<Item> BLUE_SANDSTONE = registerBlockItem("blue_sandstone",
            SandstoneBlocks.BLUE_SANDSTONE);
    public static final RegistryObject<Item> GREEN_SANDSTONE = registerBlockItem("green_sandstone",
            SandstoneBlocks.GREEN_SANDSTONE);
    public static final RegistryObject<Item> ORANGE_SANDSTONE = registerBlockItem("orange_sandstone",
            SandstoneBlocks.ORANGE_SANDSTONE);
    public static final RegistryObject<Item> PINK_SANDSTONE = registerBlockItem("pink_sandstone",
            SandstoneBlocks.PINK_SANDSTONE);
    public static final RegistryObject<Item> RED_SANDSTONE = registerBlockItem("red_sandstone",
            SandstoneBlocks.RED_SANDSTONE);
    public static final RegistryObject<Item> WHITE_SANDSTONE = registerBlockItem("white_sandstone",
            SandstoneBlocks.WHITE_SANDSTONE);
    public static final RegistryObject<Item> YELLOW_SANDSTONE = registerBlockItem("yellow_sandstone",
            SandstoneBlocks.YELLOW_SANDSTONE);
    public static final RegistryObject<Item> BLACK_SMOOTH_SANDSTONE = registerBlockItem("black_smooth_sandstone",
            StoneBlocks.BLACK_SMOOTH_SANDSTONE);
    public static final RegistryObject<Item> BLUE_SMOOTH_SANDSTONE = registerBlockItem("blue_smooth_sandstone",
            StoneBlocks.BLUE_SMOOTH_SANDSTONE);
    public static final RegistryObject<Item> GREEN_SMOOTH_SANDSTONE = registerBlockItem("green_smooth_sandstone",
            StoneBlocks.GREEN_SMOOTH_SANDSTONE);
    public static final RegistryObject<Item> ORANGE_SMOOTH_SANDSTONE = registerBlockItem("orange_smooth_sandstone",
            StoneBlocks.ORANGE_SMOOTH_SANDSTONE);
    public static final RegistryObject<Item> PINK_SMOOTH_SANDSTONE = registerBlockItem("pink_smooth_sandstone",
            StoneBlocks.PINK_SMOOTH_SANDSTONE);
    public static final RegistryObject<Item> RED_SMOOTH_SANDSTONE = registerBlockItem("red_smooth_sandstone",
            StoneBlocks.RED_SMOOTH_SANDSTONE);
    public static final RegistryObject<Item> WHITE_SMOOTH_SANDSTONE = registerBlockItem("white_smooth_sandstone",
            StoneBlocks.WHITE_SMOOTH_SANDSTONE);
    public static final RegistryObject<Item> YELLOW_SMOOTH_SANDSTONE = registerBlockItem("yellow_smooth_sandstone",
            StoneBlocks.YELLOW_SMOOTH_SANDSTONE);
    public static final RegistryObject<Item> BLACK_TILES = registerBlockItem("black_tiles",
            SandstoneBlocks.BLACK_TILES);
    public static final RegistryObject<Item> BLUE_TILES = registerBlockItem("blue_tiles", SandstoneBlocks.BLUE_TILES);
    public static final RegistryObject<Item> BROWN_TILES = registerBlockItem("brown_tiles",
            SandstoneBlocks.BROWN_TILES);
    public static final RegistryObject<Item> CYAN_TILES = registerBlockItem("cyan_tiles", SandstoneBlocks.CYAN_TILES);
    public static final RegistryObject<Item> GRAY_TILES = registerBlockItem("gray_tiles", SandstoneBlocks.GRAY_TILES);
    public static final RegistryObject<Item> GREEN_TILES = registerBlockItem("green_tiles",
            SandstoneBlocks.GREEN_TILES);
    public static final RegistryObject<Item> LIGHT_BLUE_TILES = registerBlockItem("light_blue_tiles",
            SandstoneBlocks.LIGHT_BLUE_TILES);
    public static final RegistryObject<Item> LIGHT_GRAY_TILES = registerBlockItem("light_gray_tiles",
            SandstoneBlocks.LIGHT_GRAY_TILES);
    public static final RegistryObject<Item> LIME_TILES = registerBlockItem("lime_tiles", SandstoneBlocks.LIME_TILES);
    public static final RegistryObject<Item> MAGENTA_TILES = registerBlockItem("magenta_tiles",
            SandstoneBlocks.MAGENTA_TILES);
    public static final RegistryObject<Item> ORANGE_TILES = registerBlockItem("orange_tiles",
            SandstoneBlocks.ORANGE_TILES);
    public static final RegistryObject<Item> PINK_TILES = registerBlockItem("pink_tiles", SandstoneBlocks.PINK_TILES);
    public static final RegistryObject<Item> PURPLE_TILES = registerBlockItem("purple_tiles",
            SandstoneBlocks.PURPLE_TILES);
    public static final RegistryObject<Item> RED_TILES = registerBlockItem("red_tiles", SandstoneBlocks.RED_TILES);
    public static final RegistryObject<Item> WHITE_TILES = registerBlockItem("white_tiles",
            SandstoneBlocks.WHITE_TILES);
    public static final RegistryObject<Item> YELLOW_TILES = registerBlockItem("yellow_tiles",
            SandstoneBlocks.YELLOW_TILES);
    public static final RegistryObject<Item> BLACK_MOSAIC_GLASS = registerBlockItem("black_mosaic_glass",
            SandstoneBlocks.BLACK_MOSAIC_GLASS);
    public static final RegistryObject<Item> BLUE_MOSAIC_GLASS = registerBlockItem("blue_mosaic_glass",
            SandstoneBlocks.BLUE_MOSAIC_GLASS);
    public static final RegistryObject<Item> BROWN_MOSAIC_GLASS = registerBlockItem("brown_mosaic_glass",
            SandstoneBlocks.BROWN_MOSAIC_GLASS);
    public static final RegistryObject<Item> CYAN_MOSAIC_GLASS = registerBlockItem("cyan_mosaic_glass",
            SandstoneBlocks.CYAN_MOSAIC_GLASS);
    public static final RegistryObject<Item> GRAY_MOSAIC_GLASS = registerBlockItem("gray_mosaic_glass",
            SandstoneBlocks.GRAY_MOSAIC_GLASS);
    public static final RegistryObject<Item> GREEN_MOSAIC_GLASS = registerBlockItem("green_mosaic_glass",
            SandstoneBlocks.GREEN_MOSAIC_GLASS);
    public static final RegistryObject<Item> LIGHT_BLUE_MOSAIC_GLASS = registerBlockItem("light_blue_mosaic_glass",
            SandstoneBlocks.LIGHT_BLUE_MOSAIC_GLASS);
    public static final RegistryObject<Item> LIGHT_GRAY_MOSAIC_GLASS = registerBlockItem("light_gray_mosaic_glass",
            SandstoneBlocks.LIGHT_GRAY_MOSAIC_GLASS);
    public static final RegistryObject<Item> LIME_MOSAIC_GLASS = registerBlockItem("lime_mosaic_glass",
            SandstoneBlocks.LIME_MOSAIC_GLASS);
    public static final RegistryObject<Item> MAGENTA_MOSAIC_GLASS = registerBlockItem("magenta_mosaic_glass",
            SandstoneBlocks.MAGENTA_MOSAIC_GLASS);
    public static final RegistryObject<Item> ORANGE_MOSAIC_GLASS = registerBlockItem("orange_mosaic_glass",
            SandstoneBlocks.ORANGE_MOSAIC_GLASS);
    public static final RegistryObject<Item> PINK_MOSAIC_GLASS = registerBlockItem("pink_mosaic_glass",
            SandstoneBlocks.PINK_MOSAIC_GLASS);
    public static final RegistryObject<Item> PURPLE_MOSAIC_GLASS = registerBlockItem("purple_mosaic_glass",
            SandstoneBlocks.PURPLE_MOSAIC_GLASS);
    public static final RegistryObject<Item> RED_MOSAIC_GLASS = registerBlockItem("red_mosaic_glass",
            SandstoneBlocks.RED_MOSAIC_GLASS);
    public static final RegistryObject<Item> WHITE_MOSAIC_GLASS = registerBlockItem("white_mosaic_glass",
            SandstoneBlocks.WHITE_MOSAIC_GLASS);
    public static final RegistryObject<Item> YELLOW_MOSAIC_GLASS = registerBlockItem("yellow_mosaic_glass",
            SandstoneBlocks.YELLOW_MOSAIC_GLASS);
    public static final RegistryObject<Item> BLACK_MOSAIC_GLASS_PANE = registerBlockItem("black_mosaic_glass_pane",
            StoneBlocks.BLACK_MOSAIC_GLASS_PANE);
    public static final RegistryObject<Item> BLUE_MOSAIC_GLASS_PANE = registerBlockItem("blue_mosaic_glass_pane",
            StoneBlocks.BLUE_MOSAIC_GLASS_PANE);
    public static final RegistryObject<Item> BROWN_MOSAIC_GLASS_PANE = registerBlockItem("brown_mosaic_glass_pane",
            StoneBlocks.BROWN_MOSAIC_GLASS_PANE);
    public static final RegistryObject<Item> CYAN_MOSAIC_GLASS_PANE = registerBlockItem("cyan_mosaic_glass_pane",
            StoneBlocks.CYAN_MOSAIC_GLASS_PANE);
    public static final RegistryObject<Item> GRAY_MOSAIC_GLASS_PANE = registerBlockItem("gray_mosaic_glass_pane",
            StoneBlocks.GRAY_MOSAIC_GLASS_PANE);
    public static final RegistryObject<Item> GREEN_MOSAIC_GLASS_PANE = registerBlockItem("green_mosaic_glass_pane",
            StoneBlocks.GREEN_MOSAIC_GLASS_PANE);
    public static final RegistryObject<Item> LIGHT_BLUE_MOSAIC_GLASS_PANE = registerBlockItem(
            "light_blue_mosaic_glass_pane", StoneBlocks.LIGHT_BLUE_MOSAIC_GLASS_PANE);
    public static final RegistryObject<Item> LIGHT_GRAY_MOSAIC_GLASS_PANE = registerBlockItem(
            "light_gray_mosaic_glass_pane", StoneBlocks.LIGHT_GRAY_MOSAIC_GLASS_PANE);
    public static final RegistryObject<Item> LIME_MOSAIC_GLASS_PANE = registerBlockItem("lime_mosaic_glass_pane",
            StoneBlocks.LIME_MOSAIC_GLASS_PANE);
    public static final RegistryObject<Item> MAGENTA_MOSAIC_GLASS_PANE = registerBlockItem("magenta_mosaic_glass_pane",
            StoneBlocks.MAGENTA_MOSAIC_GLASS_PANE);
    public static final RegistryObject<Item> ORANGE_MOSAIC_GLASS_PANE = registerBlockItem("orange_mosaic_glass_pane",
            StoneBlocks.ORANGE_MOSAIC_GLASS_PANE);
    public static final RegistryObject<Item> PINK_MOSAIC_GLASS_PANE = registerBlockItem("pink_mosaic_glass_pane",
            StoneBlocks.PINK_MOSAIC_GLASS_PANE);
    public static final RegistryObject<Item> PURPLE_MOSAIC_GLASS_PANE = registerBlockItem("purple_mosaic_glass_pane",
            StoneBlocks.PURPLE_MOSAIC_GLASS_PANE);
    public static final RegistryObject<Item> RED_MOSAIC_GLASS_PANE = registerBlockItem("red_mosaic_glass_pane",
            StoneBlocks.RED_MOSAIC_GLASS_PANE);
    public static final RegistryObject<Item> WHITE_MOSAIC_GLASS_PANE = registerBlockItem("white_mosaic_glass_pane",
            StoneBlocks.WHITE_MOSAIC_GLASS_PANE);
    public static final RegistryObject<Item> YELLOW_MOSAIC_GLASS_PANE = registerBlockItem("yellow_mosaic_glass_pane",
            StoneBlocks.YELLOW_MOSAIC_GLASS_PANE);
    public static final RegistryObject<Item> BLACK_GLAZED_GLASS = registerBlockItem("black_glazed_glass",
            StoneBlocks.BLACK_GLAZED_GLASS);
    public static final RegistryObject<Item> BLUE_GLAZED_GLASS = registerBlockItem("blue_glazed_glass",
            StoneBlocks.BLUE_GLAZED_GLASS);
    public static final RegistryObject<Item> BROWN_GLAZED_GLASS = registerBlockItem("brown_glazed_glass",
            StoneBlocks.BROWN_GLAZED_GLASS);
    public static final RegistryObject<Item> CYAN_GLAZED_GLASS = registerBlockItem("cyan_glazed_glass",
            StoneBlocks.CYAN_GLAZED_GLASS);
    public static final RegistryObject<Item> GRAY_GLAZED_GLASS = registerBlockItem("gray_glazed_glass",
            StoneBlocks.GRAY_GLAZED_GLASS);
    public static final RegistryObject<Item> GREEN_GLAZED_GLASS = registerBlockItem("green_glazed_glass",
            StoneBlocks.GREEN_GLAZED_GLASS);
    public static final RegistryObject<Item> LIGHT_BLUE_GLAZED_GLASS = registerBlockItem("light_blue_glazed_glass",
            StoneBlocks.LIGHT_BLUE_GLAZED_GLASS);
    public static final RegistryObject<Item> LIGHT_GRAY_GLAZED_GLASS = registerBlockItem("light_gray_glazed_glass",
            StoneBlocks.LIGHT_GRAY_GLAZED_GLASS);
    public static final RegistryObject<Item> LIME_GLAZED_GLASS = registerBlockItem("lime_glazed_glass",
            StoneBlocks.LIME_GLAZED_GLASS);
    public static final RegistryObject<Item> MAGENTA_GLAZED_GLASS = registerBlockItem("magenta_glazed_glass",
            StoneBlocks.MAGENTA_GLAZED_GLASS);
    public static final RegistryObject<Item> ORANGE_GLAZED_GLASS = registerBlockItem("orange_glazed_glass",
            StoneBlocks.ORANGE_GLAZED_GLASS);
    public static final RegistryObject<Item> PINK_GLAZED_GLASS = registerBlockItem("pink_glazed_glass",
            StoneBlocks.PINK_GLAZED_GLASS);
    public static final RegistryObject<Item> PURPLE_GLAZED_GLASS = registerBlockItem("purple_glazed_glass",
            StoneBlocks.PURPLE_GLAZED_GLASS);
    public static final RegistryObject<Item> RED_GLAZED_GLASS = registerBlockItem("red_glazed_glass",
            StoneBlocks.RED_GLAZED_GLASS);
    public static final RegistryObject<Item> WHITE_GLAZED_GLASS = registerBlockItem("white_glazed_glass",
            StoneBlocks.WHITE_GLAZED_GLASS);
    public static final RegistryObject<Item> YELLOW_GLAZED_GLASS = registerBlockItem("yellow_glazed_glass",
            StoneBlocks.YELLOW_GLAZED_GLASS);
    public static final RegistryObject<Item> BLACK_GLAZED_GLASS_PANE = registerBlockItem("black_glazed_glass_pane",
            StoneBlocks.BLACK_GLAZED_GLASS_PANE);
    public static final RegistryObject<Item> BLUE_GLAZED_GLASS_PANE = registerBlockItem("blue_glazed_glass_pane",
            StoneBlocks.BLUE_GLAZED_GLASS_PANE);
    public static final RegistryObject<Item> BROWN_GLAZED_GLASS_PANE = registerBlockItem("brown_glazed_glass_pane",
            StoneBlocks.BROWN_GLAZED_GLASS_PANE);
    public static final RegistryObject<Item> CYAN_GLAZED_GLASS_PANE = registerBlockItem("cyan_glazed_glass_pane",
            StoneBlocks.CYAN_GLAZED_GLASS_PANE);
    public static final RegistryObject<Item> GRAY_GLAZED_GLASS_PANE = registerBlockItem("gray_glazed_glass_pane",
            StoneBlocks.GRAY_GLAZED_GLASS_PANE);
    public static final RegistryObject<Item> GREEN_GLAZED_GLASS_PANE = registerBlockItem("green_glazed_glass_pane",
            StoneBlocks.GREEN_GLAZED_GLASS_PANE);
    public static final RegistryObject<Item> LIGHT_BLUE_GLAZED_GLASS_PANE = registerBlockItem(
            "light_blue_glazed_glass_pane", StoneBlocks.LIGHT_BLUE_GLAZED_GLASS_PANE);
    public static final RegistryObject<Item> LIGHT_GRAY_GLAZED_GLASS_PANE = registerBlockItem(
            "light_gray_glazed_glass_pane", StoneBlocks.LIGHT_GRAY_GLAZED_GLASS_PANE);
    public static final RegistryObject<Item> LIME_GLAZED_GLASS_PANE = registerBlockItem("lime_glazed_glass_pane",
            StoneBlocks.LIME_GLAZED_GLASS_PANE);
    public static final RegistryObject<Item> MAGENTA_GLAZED_GLASS_PANE = registerBlockItem("magenta_glazed_glass_pane",
            StoneBlocks.MAGENTA_GLAZED_GLASS_PANE);
    public static final RegistryObject<Item> ORANGE_GLAZED_GLASS_PANE = registerBlockItem("orange_glazed_glass_pane",
            StoneBlocks.ORANGE_GLAZED_GLASS_PANE);
    public static final RegistryObject<Item> PINK_GLAZED_GLASS_PANE = registerBlockItem("pink_glazed_glass_pane",
            StoneBlocks.PINK_GLAZED_GLASS_PANE);
    public static final RegistryObject<Item> PURPLE_GLAZED_GLASS_PANE = registerBlockItem("purple_glazed_glass_pane",
            StoneBlocks.PURPLE_GLAZED_GLASS_PANE);
    public static final RegistryObject<Item> RED_GLAZED_GLASS_PANE = registerBlockItem("red_glazed_glass_pane",
            StoneBlocks.RED_GLAZED_GLASS_PANE);
    public static final RegistryObject<Item> WHITE_GLAZED_GLASS_PANE = registerBlockItem("white_glazed_glass_pane",
            StoneBlocks.WHITE_GLAZED_GLASS_PANE);
    public static final RegistryObject<Item> YELLOW_GLAZED_GLASS_PANE = registerBlockItem("yellow_glazed_glass_pane",
            StoneBlocks.YELLOW_GLAZED_GLASS_PANE);
    public static final RegistryObject<Item> BIT_CHISELED_COPPER = registerBlockItem("bit_chiseled_copper",
            SandstoneBlocks.BIT_CHISELED_COPPER);
    public static final RegistryObject<Item> BIT_COPPER_BLOCK = registerBlockItem("bit_copper_block",
            SandstoneBlocks.BIT_COPPER_BLOCK);
    public static final RegistryObject<Item> BIT_COPPER_BULB = registerBlockItem("bit_copper_bulb",
            SandstoneBlocks.BIT_COPPER_BULB);
    public static final RegistryObject<Item> BIT_COPPER_GRATE = registerBlockItem("bit_copper_grate",
            SandstoneBlocks.BIT_COPPER_GRATE);
    public static final RegistryObject<Item> BIT_CUT_COPPER = registerBlockItem("bit_cut_copper",
            SandstoneBlocks.BIT_CUT_COPPER);
    public static final RegistryObject<Item> BIT_EXPOSED_CHISELED_COPPER = registerBlockItem(
            "bit_exposed_chiseled_copper", SandstoneBlocks.BIT_EXPOSED_CHISELED_COPPER);
    public static final RegistryObject<Item> BIT_EXPOSED_COPPER_BLOCK = registerBlockItem("bit_exposed_copper_block",
            SandstoneBlocks.BIT_EXPOSED_COPPER_BLOCK);
    public static final RegistryObject<Item> BIT_EXPOSED_COPPER_BULB = registerBlockItem("bit_exposed_copper_bulb",
            SandstoneBlocks.BIT_EXPOSED_COPPER_BULB);
    public static final RegistryObject<Item> BIT_EXPOSED_COPPER_GRATE = registerBlockItem("bit_exposed_copper_grate",
            SandstoneBlocks.BIT_EXPOSED_COPPER_GRATE);
    public static final RegistryObject<Item> BIT_EXPOSED_CUT_COPPER = registerBlockItem("bit_exposed_cut_copper",
            SandstoneBlocks.BIT_EXPOSED_CUT_COPPER);
    public static final RegistryObject<Item> BIT_WEATHERED_CHISELED_COPPER = registerBlockItem(
            "bit_weathered_chiseled_copper", SandstoneBlocks.BIT_WEATHERED_CHISELED_COPPER);
    public static final RegistryObject<Item> BIT_WEATHERED_COPPER_BLOCK = registerBlockItem(
            "bit_weathered_copper_block", SandstoneBlocks.BIT_WEATHERED_COPPER_BLOCK);
    public static final RegistryObject<Item> BIT_WEATHERED_COPPER_BULB = registerBlockItem("bit_weathered_copper_bulb",
            SandstoneBlocks.BIT_WEATHERED_COPPER_BULB);
    public static final RegistryObject<Item> BIT_WEATHERED_COPPER_GRATE = registerBlockItem(
            "bit_weathered_copper_grate", SandstoneBlocks.BIT_WEATHERED_COPPER_GRATE);
    public static final RegistryObject<Item> BIT_WEATHERED_CUT_COPPER = registerBlockItem("bit_weathered_cut_copper",
            SandstoneBlocks.BIT_WEATHERED_CUT_COPPER);
    public static final RegistryObject<Item> BIT_OXIDIZED_CHISELED_COPPER = registerBlockItem(
            "bit_oxidized_chiseled_copper", SandstoneBlocks.BIT_OXIDIZED_CHISELED_COPPER);
    public static final RegistryObject<Item> BIT_OXIDIZED_COPPER_BLOCK = registerBlockItem("bit_oxidized_copper_block",
            SandstoneBlocks.BIT_OXIDIZED_COPPER_BLOCK);
    public static final RegistryObject<Item> BIT_OXIDIZED_COPPER_BULB = registerBlockItem("bit_oxidized_copper_bulb",
            SandstoneBlocks.BIT_OXIDIZED_COPPER_BULB);
    public static final RegistryObject<Item> BIT_OXIDIZED_COPPER_GRATE = registerBlockItem("bit_oxidized_copper_grate",
            SandstoneBlocks.BIT_OXIDIZED_COPPER_GRATE);
    public static final RegistryObject<Item> BIT_OXIDIZED_CUT_COPPER = registerBlockItem("bit_oxidized_cut_copper",
            SandstoneBlocks.BIT_OXIDIZED_CUT_COPPER);
    public static final RegistryObject<Item> BIT_CHISELED_TUFF = registerBlockItem("bit_chiseled_tuff",
            SandstoneBlocks.BIT_CHISELED_TUFF);
    public static final RegistryObject<Item> BIT_CHISELED_TUFF_BRICKS = registerBlockItem("bit_chiseled_tuff_bricks",
            SandstoneBlocks.BIT_CHISELED_TUFF_BRICKS);
    public static final RegistryObject<Item> BIT_POLISHED_TUFF = registerBlockItem("bit_polished_tuff",
            SandstoneBlocks.BIT_POLISHED_TUFF);
    public static final RegistryObject<Item> BIT_POLISHED_TUFF_STAIRS = registerBlockItem("bit_polished_tuff_stairs",
            SandstoneBlocks.BIT_POLISHED_TUFF_STAIRS);
    public static final RegistryObject<Item> BIT_POLISHED_TUFF_SLAB = registerBlockItem("bit_polished_tuff_slab",
            SandstoneBlocks.BIT_POLISHED_TUFF_SLAB);
    public static final RegistryObject<Item> BIT_POLISHED_TUFF_WALL = registerBlockItem("bit_polished_tuff_wall",
            SandstoneBlocks.BIT_POLISHED_TUFF_WALL);
    public static final RegistryObject<Item> BIT_TUFF_BRICKS = registerBlockItem("bit_tuff_bricks",
            SandstoneBlocks.BIT_TUFF_BRICKS);
    public static final RegistryObject<Item> BIT_TUFF_BRICKS_STAIRS = registerBlockItem("bit_tuff_bricks_stairs",
            SandstoneBlocks.BIT_TUFF_BRICKS_STAIRS);
    public static final RegistryObject<Item> BIT_TUFF_BRICKS_SLAB = registerBlockItem("bit_tuff_bricks_slab",
            SandstoneBlocks.BIT_TUFF_BRICKS_SLAB);
    public static final RegistryObject<Item> BIT_TUFF_BRICKS_WALL = registerBlockItem("bit_tuff_bricks_wall",
            SandstoneBlocks.BIT_TUFF_BRICKS_WALL);
    public static final RegistryObject<Item> BLACK_SANDSTONE_STAIRS = registerBlockItem("black_sandstone_stairs",
            SandstoneBlocks.BLACK_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> BLUE_SANDSTONE_STAIRS = registerBlockItem("blue_sandstone_stairs",
            SandstoneBlocks.BLUE_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> GREEN_SANDSTONE_STAIRS = registerBlockItem("green_sandstone_stairs",
            SandstoneBlocks.GREEN_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> ORANGE_SANDSTONE_STAIRS = registerBlockItem("orange_sandstone_stairs",
            SandstoneBlocks.ORANGE_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> PINK_SANDSTONE_STAIRS = registerBlockItem("pink_sandstone_stairs",
            SandstoneBlocks.PINK_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> RED_SANDSTONE_STAIRS = registerBlockItem("red_sandstone_stairs",
            SandstoneBlocks.RED_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> WHITE_SANDSTONE_STAIRS = registerBlockItem("white_sandstone_stairs",
            SandstoneBlocks.WHITE_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> YELLOW_SANDSTONE_STAIRS = registerBlockItem("yellow_sandstone_stairs",
            SandstoneBlocks.YELLOW_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> BLACK_SANDSTONE_SLAB = registerBlockItem("black_sandstone_slab",
            StoneBlocks.BLACK_SANDSTONE_SLAB);
    public static final RegistryObject<Item> BLUE_SANDSTONE_SLAB = registerBlockItem("blue_sandstone_slab",
            StoneBlocks.BLUE_SANDSTONE_SLAB);
    public static final RegistryObject<Item> GREEN_SANDSTONE_SLAB = registerBlockItem("green_sandstone_slab",
            StoneBlocks.GREEN_SANDSTONE_SLAB);
    public static final RegistryObject<Item> ORANGE_SANDSTONE_SLAB = registerBlockItem("orange_sandstone_slab",
            StoneBlocks.ORANGE_SANDSTONE_SLAB);
    public static final RegistryObject<Item> PINK_SANDSTONE_SLAB = registerBlockItem("pink_sandstone_slab",
            StoneBlocks.PINK_SANDSTONE_SLAB);
    public static final RegistryObject<Item> RED_SANDSTONE_SLAB = registerBlockItem("red_sandstone_slab",
            StoneBlocks.RED_SANDSTONE_SLAB);
    public static final RegistryObject<Item> WHITE_SANDSTONE_SLAB = registerBlockItem("white_sandstone_slab",
            StoneBlocks.WHITE_SANDSTONE_SLAB);
    public static final RegistryObject<Item> YELLOW_SANDSTONE_SLAB = registerBlockItem("yellow_sandstone_slab",
            StoneBlocks.YELLOW_SANDSTONE_SLAB);
    public static final RegistryObject<Item> BLACK_SANDSTONE_WALL = registerBlockItem("black_sandstone_wall",
            StoneBlocks.BLACK_SANDSTONE_WALL);
    public static final RegistryObject<Item> BLUE_SANDSTONE_WALL = registerBlockItem("blue_sandstone_wall",
            StoneBlocks.BLUE_SANDSTONE_WALL);
    public static final RegistryObject<Item> GREEN_SANDSTONE_WALL = registerBlockItem("green_sandstone_wall",
            StoneBlocks.GREEN_SANDSTONE_WALL);
    public static final RegistryObject<Item> ORANGE_SANDSTONE_WALL = registerBlockItem("orange_sandstone_wall",
            StoneBlocks.ORANGE_SANDSTONE_WALL);
    public static final RegistryObject<Item> PINK_SANDSTONE_WALL = registerBlockItem("pink_sandstone_wall",
            StoneBlocks.PINK_SANDSTONE_WALL);
    public static final RegistryObject<Item> RED_SANDSTONE_WALL = registerBlockItem("red_sandstone_wall",
            StoneBlocks.RED_SANDSTONE_WALL);
    public static final RegistryObject<Item> WHITE_SANDSTONE_WALL = registerBlockItem("white_sandstone_wall",
            StoneBlocks.WHITE_SANDSTONE_WALL);
    public static final RegistryObject<Item> YELLOW_SANDSTONE_WALL = registerBlockItem("yellow_sandstone_wall",
            StoneBlocks.YELLOW_SANDSTONE_WALL);
    public static final RegistryObject<Item> WHITE_SMOOTH_SANDSTONE_WALL = registerBlockItem(
            "white_smooth_sandstone_wall", StoneBlocks.WHITE_SMOOTH_SANDSTONE_WALL);
    public static final RegistryObject<Item> BLACK_SMOOTH_SANDSTONE_WALL = registerBlockItem(
            "black_smooth_sandstone_wall", StoneBlocks.BLACK_SMOOTH_SANDSTONE_WALL);
    public static final RegistryObject<Item> RED_SMOOTH_SANDSTONE_WALL = registerBlockItem("red_smooth_sandstone_wall",
            StoneBlocks.RED_SMOOTH_SANDSTONE_WALL);
    public static final RegistryObject<Item> ORANGE_SMOOTH_SANDSTONE_WALL = registerBlockItem(
            "orange_smooth_sandstone_wall", StoneBlocks.ORANGE_SMOOTH_SANDSTONE_WALL);
    public static final RegistryObject<Item> YELLOW_SMOOTH_SANDSTONE_WALL = registerBlockItem(
            "yellow_smooth_sandstone_wall", StoneBlocks.YELLOW_SMOOTH_SANDSTONE_WALL);
    public static final RegistryObject<Item> GREEN_SMOOTH_SANDSTONE_WALL = registerBlockItem(
            "green_smooth_sandstone_wall", StoneBlocks.GREEN_SMOOTH_SANDSTONE_WALL);
    public static final RegistryObject<Item> BLUE_SMOOTH_SANDSTONE_WALL = registerBlockItem(
            "blue_smooth_sandstone_wall", StoneBlocks.BLUE_SMOOTH_SANDSTONE_WALL);
    public static final RegistryObject<Item> PINK_SMOOTH_SANDSTONE_WALL = registerBlockItem(
            "pink_smooth_sandstone_wall", StoneBlocks.PINK_SMOOTH_SANDSTONE_WALL);
    public static final RegistryObject<Item> SMOOTH_SANDSTONE_WALL = registerBlockItem("smooth_sandstone_wall",
            StoneBlocks.SMOOTH_SANDSTONE_WALL);
    public static final RegistryObject<Item> BLACK_SMOOTH_SANDSTONE_SLAB = registerBlockItem(
            "black_smooth_sandstone_slab", StoneBlocks.BLACK_SMOOTH_SANDSTONE_SLAB);
    public static final RegistryObject<Item> BLUE_SMOOTH_SANDSTONE_SLAB = registerBlockItem(
            "blue_smooth_sandstone_slab", StoneBlocks.BLUE_SMOOTH_SANDSTONE_SLAB);
    public static final RegistryObject<Item> GREEN_SMOOTH_SANDSTONE_SLAB = registerBlockItem(
            "green_smooth_sandstone_slab", StoneBlocks.GREEN_SMOOTH_SANDSTONE_SLAB);
    public static final RegistryObject<Item> ORANGE_SMOOTH_SANDSTONE_SLAB = registerBlockItem(
            "orange_smooth_sandstone_slab", StoneBlocks.ORANGE_SMOOTH_SANDSTONE_SLAB);
    public static final RegistryObject<Item> PINK_SMOOTH_SANDSTONE_SLAB = registerBlockItem(
            "pink_smooth_sandstone_slab", StoneBlocks.PINK_SMOOTH_SANDSTONE_SLAB);
    public static final RegistryObject<Item> RED_SMOOTH_SANDSTONE_SLAB = registerBlockItem("red_smooth_sandstone_slab",
            StoneBlocks.RED_SMOOTH_SANDSTONE_SLAB);
    public static final RegistryObject<Item> WHITE_SMOOTH_SANDSTONE_SLAB = registerBlockItem(
            "white_smooth_sandstone_slab", StoneBlocks.WHITE_SMOOTH_SANDSTONE_SLAB);
    public static final RegistryObject<Item> YELLOW_SMOOTH_SANDSTONE_SLAB = registerBlockItem(
            "yellow_smooth_sandstone_slab", StoneBlocks.YELLOW_SMOOTH_SANDSTONE_SLAB);
    public static final RegistryObject<Item> BLACK_SMOOTH_SANDSTONE_STAIRS = registerBlockItem(
            "black_smooth_sandstone_stairs", StoneBlocks.BLACK_SMOOTH_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> BLUE_SMOOTH_SANDSTONE_STAIRS = registerBlockItem(
            "blue_smooth_sandstone_stairs", StoneBlocks.BLUE_SMOOTH_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> GREEN_SMOOTH_SANDSTONE_STAIRS = registerBlockItem(
            "green_smooth_sandstone_stairs", StoneBlocks.GREEN_SMOOTH_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> ORANGE_SMOOTH_SANDSTONE_STAIRS = registerBlockItem(
            "orange_smooth_sandstone_stairs", StoneBlocks.ORANGE_SMOOTH_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> PINK_SMOOTH_SANDSTONE_STAIRS = registerBlockItem(
            "pink_smooth_sandstone_stairs", StoneBlocks.PINK_SMOOTH_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> RED_SMOOTH_SANDSTONE_STAIRS = registerBlockItem(
            "red_smooth_sandstone_stairs", StoneBlocks.RED_SMOOTH_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> WHITE_SMOOTH_SANDSTONE_STAIRS = registerBlockItem(
            "white_smooth_sandstone_stairs", StoneBlocks.WHITE_SMOOTH_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> YELLOW_SMOOTH_SANDSTONE_STAIRS = registerBlockItem(
            "yellow_smooth_sandstone_stairs", StoneBlocks.YELLOW_SMOOTH_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> BLACK_TILES_STAIRS = registerBlockItem("black_tiles_stairs",
            SandstoneBlocks.BLACK_TILES_STAIRS);
    public static final RegistryObject<Item> BLUE_TILES_STAIRS = registerBlockItem("blue_tiles_stairs",
            SandstoneBlocks.BLUE_TILES_STAIRS);
    public static final RegistryObject<Item> BROWN_TILES_STAIRS = registerBlockItem("brown_tiles_stairs",
            SandstoneBlocks.BROWN_TILES_STAIRS);
    public static final RegistryObject<Item> CYAN_TILES_STAIRS = registerBlockItem("cyan_tiles_stairs",
            SandstoneBlocks.CYAN_TILES_STAIRS);
    public static final RegistryObject<Item> GRAY_TILES_STAIRS = registerBlockItem("gray_tiles_stairs",
            SandstoneBlocks.GRAY_TILES_STAIRS);
    public static final RegistryObject<Item> GREEN_TILES_STAIRS = registerBlockItem("green_tiles_stairs",
            SandstoneBlocks.GREEN_TILES_STAIRS);
    public static final RegistryObject<Item> LIGHT_BLUE_TILES_STAIRS = registerBlockItem("light_blue_tiles_stairs",
            SandstoneBlocks.LIGHT_BLUE_TILES_STAIRS);
    public static final RegistryObject<Item> LIGHT_GRAY_TILES_STAIRS = registerBlockItem("light_gray_tiles_stairs",
            SandstoneBlocks.LIGHT_GRAY_TILES_STAIRS);
    public static final RegistryObject<Item> LIME_TILES_STAIRS = registerBlockItem("lime_tiles_stairs",
            SandstoneBlocks.LIME_TILES_STAIRS);
    public static final RegistryObject<Item> MAGENTA_TILES_STAIRS = registerBlockItem("magenta_tiles_stairs",
            SandstoneBlocks.MAGENTA_TILES_STAIRS);
    public static final RegistryObject<Item> ORANGE_TILES_STAIRS = registerBlockItem("orange_tiles_stairs",
            SandstoneBlocks.ORANGE_TILES_STAIRS);
    public static final RegistryObject<Item> PINK_TILES_STAIRS = registerBlockItem("pink_tiles_stairs",
            SandstoneBlocks.PINK_TILES_STAIRS);
    public static final RegistryObject<Item> PURPLE_TILES_STAIRS = registerBlockItem("purple_tiles_stairs",
            SandstoneBlocks.PURPLE_TILES_STAIRS);
    public static final RegistryObject<Item> RED_TILES_STAIRS = registerBlockItem("red_tiles_stairs",
            SandstoneBlocks.RED_TILES_STAIRS);
    public static final RegistryObject<Item> WHITE_TILES_STAIRS = registerBlockItem("white_tiles_stairs",
            SandstoneBlocks.WHITE_TILES_STAIRS);
    public static final RegistryObject<Item> YELLOW_TILES_STAIRS = registerBlockItem("yellow_tiles_stairs",
            SandstoneBlocks.YELLOW_TILES_STAIRS);
    public static final RegistryObject<Item> BLACK_TILES_SLAB = registerBlockItem("black_tiles_slab",
            StoneBlocks.BLACK_TILES_SLAB);
    public static final RegistryObject<Item> BLUE_TILES_SLAB = registerBlockItem("blue_tiles_slab",
            StoneBlocks.BLUE_TILES_SLAB);
    public static final RegistryObject<Item> BROWN_TILES_SLAB = registerBlockItem("brown_tiles_slab",
            StoneBlocks.BROWN_TILES_SLAB);
    public static final RegistryObject<Item> CYAN_TILES_SLAB = registerBlockItem("cyan_tiles_slab",
            StoneBlocks.CYAN_TILES_SLAB);
    public static final RegistryObject<Item> GRAY_TILES_SLAB = registerBlockItem("gray_tiles_slab",
            StoneBlocks.GRAY_TILES_SLAB);
    public static final RegistryObject<Item> GREEN_TILES_SLAB = registerBlockItem("green_tiles_slab",
            StoneBlocks.GREEN_TILES_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_TILES_SLAB = registerBlockItem("light_blue_tiles_slab",
            StoneBlocks.LIGHT_BLUE_TILES_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_TILES_SLAB = registerBlockItem("light_gray_tiles_slab",
            StoneBlocks.LIGHT_GRAY_TILES_SLAB);
    public static final RegistryObject<Item> LIME_TILES_SLAB = registerBlockItem("lime_tiles_slab",
            StoneBlocks.LIME_TILES_SLAB);
    public static final RegistryObject<Item> MAGENTA_TILES_SLAB = registerBlockItem("magenta_tiles_slab",
            StoneBlocks.MAGENTA_TILES_SLAB);
    public static final RegistryObject<Item> ORANGE_TILES_SLAB = registerBlockItem("orange_tiles_slab",
            StoneBlocks.ORANGE_TILES_SLAB);
    public static final RegistryObject<Item> PINK_TILES_SLAB = registerBlockItem("pink_tiles_slab",
            StoneBlocks.PINK_TILES_SLAB);
    public static final RegistryObject<Item> PURPLE_TILES_SLAB = registerBlockItem("purple_tiles_slab",
            StoneBlocks.PURPLE_TILES_SLAB);
    public static final RegistryObject<Item> RED_TILES_SLAB = registerBlockItem("red_tiles_slab",
            StoneBlocks.RED_TILES_SLAB);
    public static final RegistryObject<Item> WHITE_TILES_SLAB = registerBlockItem("white_tiles_slab",
            StoneBlocks.WHITE_TILES_SLAB);
    public static final RegistryObject<Item> YELLOW_TILES_SLAB = registerBlockItem("yellow_tiles_slab",
            StoneBlocks.YELLOW_TILES_SLAB);
    public static final RegistryObject<Item> BLACK_TILES_WALL = registerBlockItem("black_tiles_wall",
            StoneBlocks.BLACK_TILES_WALL);
    public static final RegistryObject<Item> BLUE_TILES_WALL = registerBlockItem("blue_tiles_wall",
            StoneBlocks.BLUE_TILES_WALL);
    public static final RegistryObject<Item> BROWN_TILES_WALL = registerBlockItem("brown_tiles_wall",
            StoneBlocks.BROWN_TILES_WALL);
    public static final RegistryObject<Item> CYAN_TILES_WALL = registerBlockItem("cyan_tiles_wall",
            StoneBlocks.CYAN_TILES_WALL);
    public static final RegistryObject<Item> GRAY_TILES_WALL = registerBlockItem("gray_tiles_wall",
            StoneBlocks.GRAY_TILES_WALL);
    public static final RegistryObject<Item> GREEN_TILES_WALL = registerBlockItem("green_tiles_wall",
            StoneBlocks.GREEN_TILES_WALL);
    public static final RegistryObject<Item> LIGHT_BLUE_TILES_WALL = registerBlockItem("light_blue_tiles_wall",
            StoneBlocks.LIGHT_BLUE_TILES_WALL);
    public static final RegistryObject<Item> LIGHT_GRAY_TILES_WALL = registerBlockItem("light_gray_tiles_wall",
            StoneBlocks.LIGHT_GRAY_TILES_WALL);
    public static final RegistryObject<Item> LIME_TILES_WALL = registerBlockItem("lime_tiles_wall",
            StoneBlocks.LIME_TILES_WALL);
    public static final RegistryObject<Item> MAGENTA_TILES_WALL = registerBlockItem("magenta_tiles_wall",
            StoneBlocks.MAGENTA_TILES_WALL);
    public static final RegistryObject<Item> ORANGE_TILES_WALL = registerBlockItem("orange_tiles_wall",
            StoneBlocks.ORANGE_TILES_WALL);
    public static final RegistryObject<Item> PINK_TILES_WALL = registerBlockItem("pink_tiles_wall",
            StoneBlocks.PINK_TILES_WALL);
    public static final RegistryObject<Item> PURPLE_TILES_WALL = registerBlockItem("purple_tiles_wall",
            StoneBlocks.PURPLE_TILES_WALL);
    public static final RegistryObject<Item> RED_TILES_WALL = registerBlockItem("red_tiles_wall",
            StoneBlocks.RED_TILES_WALL);
    public static final RegistryObject<Item> WHITE_TILES_WALL = registerBlockItem("white_tiles_wall",
            StoneBlocks.WHITE_TILES_WALL);
    public static final RegistryObject<Item> YELLOW_TILES_WALL = registerBlockItem("yellow_tiles_wall",
            StoneBlocks.YELLOW_TILES_WALL);
    public static final RegistryObject<Item> POLISHED_BASALT_STAIRS = registerBlockItem("polished_basalt_stairs",
            StoneBlocks.POLISHED_BASALT_STAIRS);
    public static final RegistryObject<Item> POLISHED_BASALT_SLAB = registerBlockItem("polished_basalt_slab",
            StoneBlocks.POLISHED_BASALT_SLAB);
    public static final RegistryObject<Item> POLISHED_BASALT_WALL = registerBlockItem("polished_basalt_wall",
            StoneBlocks.POLISHED_BASALT_WALL);
    public static final RegistryObject<Item> DRIPSTONE_BLOCK_STAIRS = registerBlockItem("dripstone_block_stairs",
            StoneBlocks.DRIPSTONE_BLOCK_STAIRS);
    public static final RegistryObject<Item> DRIPSTONE_BLOCK_SLAB = registerBlockItem("dripstone_block_slab",
            StoneBlocks.DRIPSTONE_BLOCK_SLAB);
    public static final RegistryObject<Item> DRIPSTONE_BLOCK_WALL = registerBlockItem("dripstone_block_wall",
            StoneBlocks.DRIPSTONE_BLOCK_WALL);
    public static final RegistryObject<Item> END_STONE_STAIRS = registerBlockItem("end_stone_stairs",
            StoneBlocks.END_STONE_STAIRS);
    public static final RegistryObject<Item> END_STONE_SLAB = registerBlockItem("end_stone_slab",
            StoneBlocks.END_STONE_SLAB);
    public static final RegistryObject<Item> END_STONE_WALL = registerBlockItem("end_stone_wall",
            StoneBlocks.END_STONE_WALL);
    public static final RegistryObject<Item> STONE_WALL = registerBlockItem("stone_wall", StoneBlocks.STONE_WALL);
    public static final RegistryObject<Item> QUARTZ_BRICKS_STAIRS = registerBlockItem("quartz_bricks_stairs",
            StoneBlocks.QUARTZ_BRICKS_STAIRS);
    public static final RegistryObject<Item> QUARTZ_BRICKS_SLAB = registerBlockItem("quartz_bricks_slab",
            StoneBlocks.QUARTZ_BRICKS_SLAB);
    public static final RegistryObject<Item> QUARTZ_BRICKS_WALL = registerBlockItem("quartz_bricks_wall",
            StoneBlocks.QUARTZ_BRICKS_WALL);
    public static final RegistryObject<Item> QUARTZ_PILLAR_STAIRS = registerBlockItem("quartz_pillar_stairs",
            StoneBlocks.QUARTZ_PILLAR_STAIRS);
    public static final RegistryObject<Item> QUARTZ_PILLAR_SLAB = registerBlockItem("quartz_pillar_slab",
            StoneBlocks.QUARTZ_PILLAR_SLAB);
    public static final RegistryObject<Item> QUARTZ_PILLAR_WALL = registerBlockItem("quartz_pillar_wall",
            StoneBlocks.QUARTZ_PILLAR_WALL);
    public static final RegistryObject<Item> CALCITE_STAIRS = registerBlockItem("calcite_stairs",
            StoneBlocks.CALCITE_STAIRS);
    public static final RegistryObject<Item> CALCITE_SLAB = registerBlockItem("calcite_slab", StoneBlocks.CALCITE_SLAB);
    public static final RegistryObject<Item> CALCITE_WALL = registerBlockItem("calcite_wall", StoneBlocks.CALCITE_WALL);
    public static final RegistryObject<Item> BONE_BLOCK_STAIRS = registerBlockItem("bone_block_stairs",
            StoneBlocks.BONE_BLOCK_STAIRS);
    public static final RegistryObject<Item> BONE_BLOCK_SLAB = registerBlockItem("bone_block_slab",
            StoneBlocks.BONE_BLOCK_SLAB);
    public static final RegistryObject<Item> BONE_BLOCK_WALL = registerBlockItem("bone_block_wall",
            StoneBlocks.BONE_BLOCK_WALL);
    public static final RegistryObject<Item> STACKED_BONE_BLOCK = registerBlockItem("stacked_bone_block",
            StoneBlocks.STACKED_BONE_BLOCK);
    public static final RegistryObject<Item> STACKED_BONE_STAIRS = registerBlockItem("stacked_bone_stairs",
            StoneBlocks.STACKED_BONE_STAIRS);
    public static final RegistryObject<Item> STACKED_BONE_SLAB = registerBlockItem("stacked_bone_slab",
            StoneBlocks.STACKED_BONE_SLAB);
    public static final RegistryObject<Item> STACKED_BONE_WALL = registerBlockItem("stacked_bone_wall",
            StoneBlocks.STACKED_BONE_WALL);
    public static final RegistryObject<Item> POLISHED_BONE_BLOCK = registerBlockItem("polished_bone_block",
            StoneBlocks.POLISHED_BONE_BLOCK);
    public static final RegistryObject<Item> POLISHED_BONE_STAIRS = registerBlockItem("polished_bone_stairs",
            StoneBlocks.POLISHED_BONE_STAIRS);
    public static final RegistryObject<Item> POLISHED_BONE_SLAB = registerBlockItem("polished_bone_slab",
            StoneBlocks.POLISHED_BONE_SLAB);
    public static final RegistryObject<Item> POLISHED_BONE_WALL = registerBlockItem("polished_bone_wall",
            StoneBlocks.POLISHED_BONE_WALL);
    public static final RegistryObject<Item> BONE_BRICKS = registerBlockItem("bone_bricks", StoneBlocks.BONE_BRICKS);
    public static final RegistryObject<Item> BONE_BRICK_STAIRS = registerBlockItem("bone_brick_stairs",
            StoneBlocks.BONE_BRICK_STAIRS);
    public static final RegistryObject<Item> BONE_BRICK_SLAB = registerBlockItem("bone_brick_slab",
            StoneBlocks.BONE_BRICK_SLAB);
    public static final RegistryObject<Item> BONE_BRICK_WALL = registerBlockItem("bone_brick_wall",
            StoneBlocks.BONE_BRICK_WALL);
    public static final RegistryObject<Item> CHISELED_BONE_BLOCK = registerBlockItem("chiseled_bone_block",
            StoneBlocks.CHISELED_BONE_BLOCK);
    public static final RegistryObject<Item> BONE_PILLAR = registerBlockItem("bone_pillar", StoneBlocks.BONE_PILLAR);
    public static final RegistryObject<Item> BONE_DICE = registerBlockItem("bone_dice", StoneBlocks.BONE_DICE);
    public static final RegistryObject<Item> BEDROCK_SLAB = registerBlockItem("bedrock_slab", StoneBlocks.BEDROCK_SLAB);
    public static final RegistryObject<Item> BEDROCK_STAIRS = registerBlockItem("bedrock_stairs",
            StoneBlocks.BEDROCK_STAIRS);
    public static final RegistryObject<Item> BEDROCK_WALL = registerBlockItem("bedrock_wall", StoneBlocks.BEDROCK_WALL);
    public static final RegistryObject<Item> BEDROCK_PANE = registerBlockItem("bedrock_pane", StoneBlocks.BEDROCK_PANE);
    public static final RegistryObject<Item> OBSIDIAN_STAIRS = registerBlockItem("obsidian_stairs",
            StoneBlocks.OBSIDIAN_STAIRS);
    public static final RegistryObject<Item> OBSIDIAN_SLAB = registerBlockItem("obsidian_slab",
            StoneBlocks.OBSIDIAN_SLAB);
    public static final RegistryObject<Item> PRISMARINE_BRICKS_WALL = registerBlockItem("prismarine_bricks_wall",
            StoneBlocks.PRISMARINE_BRICKS_WALL);
    public static final RegistryObject<Item> DARK_PRISMARINE_WALL = registerBlockItem("dark_prismarine_wall",
            StoneBlocks.DARK_PRISMARINE_WALL);
    public static final RegistryObject<Item> QUARTZ_BLOCK_WALL = registerBlockItem("quartz_block_wall",
            StoneBlocks.QUARTZ_BLOCK_WALL);
    public static final RegistryObject<Item> SMOOTH_QUARTZ_WALL = registerBlockItem("smooth_quartz_wall",
            StoneBlocks.SMOOTH_QUARTZ_WALL);
    public static final RegistryObject<Item> SMOOTH_BASALT_STAIRS = registerBlockItem("smooth_basalt_stairs",
            StoneBlocks.SMOOTH_BASALT_STAIRS);
    public static final RegistryObject<Item> SMOOTH_BASALT_SLAB = registerBlockItem("smooth_basalt_slab",
            StoneBlocks.SMOOTH_BASALT_SLAB);
    public static final RegistryObject<Item> MOSS_BLOCK_SLAB = registerBlockItem("moss_block_slab",
            StoneBlocks.MOSS_BLOCK_SLAB);
    public static final RegistryObject<Item> AMETHYST_BLOCK_SLAB = registerBlockItem("amethyst_block_slab",
            StoneBlocks.AMETHYST_BLOCK_SLAB);
    public static final RegistryObject<Item> BIT_COPPER_BLOCK_STAIRS = registerBlockItem("bit_copper_block_stairs",
            SandstoneBlocks.BIT_COPPER_BLOCK_STAIRS);
    public static final RegistryObject<Item> BIT_COPPER_BLOCK_SLAB = registerBlockItem("bit_copper_block_slab",
            StoneBlocks.BIT_COPPER_BLOCK_SLAB);
    public static final RegistryObject<Item> BIT_COPPER_BLOCK_WALL = registerBlockItem("bit_copper_block_wall",
            StoneBlocks.BIT_COPPER_BLOCK_WALL);
    public static final RegistryObject<Item> BIT_EXPOSED_COPPER_BLOCK_STAIRS = registerBlockItem(
            "bit_exposed_copper_block_stairs", SandstoneBlocks.BIT_EXPOSED_COPPER_BLOCK_STAIRS);
    public static final RegistryObject<Item> BIT_EXPOSED_COPPER_BLOCK_SLAB = registerBlockItem(
            "bit_exposed_copper_block_slab", StoneBlocks.BIT_EXPOSED_COPPER_BLOCK_SLAB);
    public static final RegistryObject<Item> BIT_EXPOSED_COPPER_BLOCK_WALL = registerBlockItem(
            "bit_exposed_copper_block_wall", StoneBlocks.BIT_EXPOSED_COPPER_BLOCK_WALL);
    public static final RegistryObject<Item> BIT_WEATHERED_COPPER_BLOCK_STAIRS = registerBlockItem(
            "bit_weathered_copper_block_stairs", SandstoneBlocks.BIT_WEATHERED_COPPER_BLOCK_STAIRS);
    public static final RegistryObject<Item> BIT_WEATHERED_COPPER_BLOCK_SLAB = registerBlockItem(
            "bit_weathered_copper_block_slab", StoneBlocks.BIT_WEATHERED_COPPER_BLOCK_SLAB);
    public static final RegistryObject<Item> BIT_WEATHERED_COPPER_BLOCK_WALL = registerBlockItem(
            "bit_weathered_copper_block_wall", StoneBlocks.BIT_WEATHERED_COPPER_BLOCK_WALL);
    public static final RegistryObject<Item> BIT_OXIDIZED_COPPER_BLOCK_STAIRS = registerBlockItem(
            "bit_oxidized_copper_block_stairs", SandstoneBlocks.BIT_OXIDIZED_COPPER_BLOCK_STAIRS);
    public static final RegistryObject<Item> BIT_OXIDIZED_COPPER_BLOCK_SLAB = registerBlockItem(
            "bit_oxidized_copper_block_slab", StoneBlocks.BIT_OXIDIZED_COPPER_BLOCK_SLAB);
    public static final RegistryObject<Item> BIT_OXIDIZED_COPPER_BLOCK_WALL = registerBlockItem(
            "bit_oxidized_copper_block_wall", StoneBlocks.BIT_OXIDIZED_COPPER_BLOCK_WALL);
    public static final RegistryObject<Item> BIT_CUT_COPPER_STAIRS = registerBlockItem("bit_cut_copper_stairs",
            SandstoneBlocks.BIT_CUT_COPPER_STAIRS);
    public static final RegistryObject<Item> BIT_CUT_COPPER_SLAB = registerBlockItem("bit_cut_copper_slab",
            StoneBlocks.BIT_CUT_COPPER_SLAB);
    public static final RegistryObject<Item> BIT_CUT_COPPER_WALL = registerBlockItem("bit_cut_copper_wall",
            StoneBlocks.BIT_CUT_COPPER_WALL);
    public static final RegistryObject<Item> BIT_EXPOSED_CUT_COPPER_STAIRS = registerBlockItem(
            "bit_exposed_cut_copper_stairs", SandstoneBlocks.BIT_EXPOSED_CUT_COPPER_STAIRS);
    public static final RegistryObject<Item> BIT_EXPOSED_CUT_COPPER_SLAB = registerBlockItem(
            "bit_exposed_cut_copper_slab", StoneBlocks.BIT_EXPOSED_CUT_COPPER_SLAB);
    public static final RegistryObject<Item> BIT_EXPOSED_CUT_COPPER_WALL = registerBlockItem(
            "bit_exposed_cut_copper_wall", StoneBlocks.BIT_EXPOSED_CUT_COPPER_WALL);
    public static final RegistryObject<Item> BIT_WEATHERED_CUT_COPPER_STAIRS = registerBlockItem(
            "bit_weathered_cut_copper_stairs", SandstoneBlocks.BIT_WEATHERED_CUT_COPPER_STAIRS);
    public static final RegistryObject<Item> BIT_WEATHERED_CUT_COPPER_SLAB = registerBlockItem(
            "bit_weathered_cut_copper_slab", StoneBlocks.BIT_WEATHERED_CUT_COPPER_SLAB);
    public static final RegistryObject<Item> BIT_WEATHERED_CUT_COPPER_WALL = registerBlockItem(
            "bit_weathered_cut_copper_wall", StoneBlocks.BIT_WEATHERED_CUT_COPPER_WALL);
    public static final RegistryObject<Item> BIT_OXIDIZED_CUT_COPPER_STAIRS = registerBlockItem(
            "bit_oxidized_cut_copper_stairs", SandstoneBlocks.BIT_OXIDIZED_CUT_COPPER_STAIRS);
    public static final RegistryObject<Item> BIT_OXIDIZED_CUT_COPPER_SLAB = registerBlockItem(
            "bit_oxidized_cut_copper_slab", StoneBlocks.BIT_OXIDIZED_CUT_COPPER_SLAB);
    public static final RegistryObject<Item> BIT_OXIDIZED_CUT_COPPER_WALL = registerBlockItem(
            "bit_oxidized_cut_copper_wall", StoneBlocks.BIT_OXIDIZED_CUT_COPPER_WALL);
    public static final RegistryObject<Item> MOSSY_CALCITE = registerBlockItem("mossy_calcite",
            StoneBlocks.MOSSY_CALCITE);
    public static final RegistryObject<Item> MOSSY_CALCITE_STAIRS = registerBlockItem("mossy_calcite_stairs",
            StoneBlocks.MOSSY_CALCITE_STAIRS);
    public static final RegistryObject<Item> MOSSY_CALCITE_SLAB = registerBlockItem("mossy_calcite_slab",
            StoneBlocks.MOSSY_CALCITE_SLAB);
    public static final RegistryObject<Item> MOSSY_CALCITE_WALL = registerBlockItem("mossy_calcite_wall",
            StoneBlocks.MOSSY_CALCITE_WALL);
    public static final RegistryObject<Item> PODZOL_SLAB = registerBlockItem("podzol_slab",
            SandstoneBlocks.PODZOL_SLAB);
    public static final RegistryObject<Item> DIRT_SLAB = registerBlockItem("dirt_slab", SandstoneBlocks.DIRT_SLAB);
    public static final RegistryObject<Item> MUD = registerBlockItem("mud", SandstoneBlocks.MUD);
    public static final RegistryObject<Item> MUD_STAIRS = registerBlockItem("mud_stairs", StoneBlocks.MUD_STAIRS);
    public static final RegistryObject<Item> MUD_SLAB = registerBlockItem("mud_slab", StoneBlocks.MUD_SLAB);
    public static final RegistryObject<Item> MYCELIUM_SLAB = registerBlockItem("mycelium_slab",
            SandstoneBlocks.MYCELIUM_SLAB);
    public static final RegistryObject<Item> MOSS_LAYERS = registerBlockItem("moss_layers", StoneBlocks.MOSS_LAYERS);
    public static final RegistryObject<Item> MOSS_OVERLAY = registerBlockItem("moss_overlay", StoneBlocks.MOSS_OVERLAY);
    public static final RegistryObject<Item> SNOW_OVERLAY = registerBlockItem("snow_overlay", StoneBlocks.SNOW_OVERLAY);
    public static final RegistryObject<Item> BLACK_CONCRETE_STAIRS = registerBlockItem("black_concrete_stairs",
            StoneBlocks.BLACK_CONCRETE_STAIRS);
    public static final RegistryObject<Item> BLUE_CONCRETE_STAIRS = registerBlockItem("blue_concrete_stairs",
            StoneBlocks.BLUE_CONCRETE_STAIRS);
    public static final RegistryObject<Item> BROWN_CONCRETE_STAIRS = registerBlockItem("brown_concrete_stairs",
            StoneBlocks.BROWN_CONCRETE_STAIRS);
    public static final RegistryObject<Item> CYAN_CONCRETE_STAIRS = registerBlockItem("cyan_concrete_stairs",
            StoneBlocks.CYAN_CONCRETE_STAIRS);
    public static final RegistryObject<Item> GRAY_CONCRETE_STAIRS = registerBlockItem("gray_concrete_stairs",
            StoneBlocks.GRAY_CONCRETE_STAIRS);
    public static final RegistryObject<Item> GREEN_CONCRETE_STAIRS = registerBlockItem("green_concrete_stairs",
            StoneBlocks.GREEN_CONCRETE_STAIRS);
    public static final RegistryObject<Item> LIGHT_BLUE_CONCRETE_STAIRS = registerBlockItem(
            "light_blue_concrete_stairs", StoneBlocks.LIGHT_BLUE_CONCRETE_STAIRS);
    public static final RegistryObject<Item> LIGHT_GRAY_CONCRETE_STAIRS = registerBlockItem(
            "light_gray_concrete_stairs", StoneBlocks.LIGHT_GRAY_CONCRETE_STAIRS);
    public static final RegistryObject<Item> LIME_CONCRETE_STAIRS = registerBlockItem("lime_concrete_stairs",
            StoneBlocks.LIME_CONCRETE_STAIRS);
    public static final RegistryObject<Item> MAGENTA_CONCRETE_STAIRS = registerBlockItem("magenta_concrete_stairs",
            StoneBlocks.MAGENTA_CONCRETE_STAIRS);
    public static final RegistryObject<Item> ORANGE_CONCRETE_STAIRS = registerBlockItem("orange_concrete_stairs",
            StoneBlocks.ORANGE_CONCRETE_STAIRS);
    public static final RegistryObject<Item> PINK_CONCRETE_STAIRS = registerBlockItem("pink_concrete_stairs",
            StoneBlocks.PINK_CONCRETE_STAIRS);
    public static final RegistryObject<Item> PURPLE_CONCRETE_STAIRS = registerBlockItem("purple_concrete_stairs",
            StoneBlocks.PURPLE_CONCRETE_STAIRS);
    public static final RegistryObject<Item> RED_CONCRETE_STAIRS = registerBlockItem("red_concrete_stairs",
            StoneBlocks.RED_CONCRETE_STAIRS);
    public static final RegistryObject<Item> WHITE_CONCRETE_STAIRS = registerBlockItem("white_concrete_stairs",
            StoneBlocks.WHITE_CONCRETE_STAIRS);
    public static final RegistryObject<Item> YELLOW_CONCRETE_STAIRS = registerBlockItem("yellow_concrete_stairs",
            StoneBlocks.YELLOW_CONCRETE_STAIRS);
    public static final RegistryObject<Item> WHITE_CONCRETE_WALL = registerBlockItem("white_concrete_wall",
            StoneBlocks.WHITE_CONCRETE_WALL);
    public static final RegistryObject<Item> LIGHT_GRAY_CONCRETE_WALL = registerBlockItem("light_gray_concrete_wall",
            StoneBlocks.LIGHT_GRAY_CONCRETE_WALL);
    public static final RegistryObject<Item> GRAY_CONCRETE_WALL = registerBlockItem("gray_concrete_wall",
            StoneBlocks.GRAY_CONCRETE_WALL);
    public static final RegistryObject<Item> BLACK_CONCRETE_WALL = registerBlockItem("black_concrete_wall",
            StoneBlocks.BLACK_CONCRETE_WALL);
    public static final RegistryObject<Item> BROWN_CONCRETE_WALL = registerBlockItem("brown_concrete_wall",
            StoneBlocks.BROWN_CONCRETE_WALL);
    public static final RegistryObject<Item> RED_CONCRETE_WALL = registerBlockItem("red_concrete_wall",
            StoneBlocks.RED_CONCRETE_WALL);
    public static final RegistryObject<Item> ORANGE_CONCRETE_WALL = registerBlockItem("orange_concrete_wall",
            StoneBlocks.ORANGE_CONCRETE_WALL);
    public static final RegistryObject<Item> YELLOW_CONCRETE_WALL = registerBlockItem("yellow_concrete_wall",
            StoneBlocks.YELLOW_CONCRETE_WALL);
    public static final RegistryObject<Item> LIME_CONCRETE_WALL = registerBlockItem("lime_concrete_wall",
            StoneBlocks.LIME_CONCRETE_WALL);
    public static final RegistryObject<Item> GREEN_CONCRETE_WALL = registerBlockItem("green_concrete_wall",
            StoneBlocks.GREEN_CONCRETE_WALL);
    public static final RegistryObject<Item> CYAN_CONCRETE_WALL = registerBlockItem("cyan_concrete_wall",
            StoneBlocks.CYAN_CONCRETE_WALL);
    public static final RegistryObject<Item> LIGHT_BLUE_CONCRETE_WALL = registerBlockItem("light_blue_concrete_wall",
            StoneBlocks.LIGHT_BLUE_CONCRETE_WALL);
    public static final RegistryObject<Item> BLUE_CONCRETE_WALL = registerBlockItem("blue_concrete_wall",
            StoneBlocks.BLUE_CONCRETE_WALL);
    public static final RegistryObject<Item> PURPLE_CONCRETE_WALL = registerBlockItem("purple_concrete_wall",
            StoneBlocks.PURPLE_CONCRETE_WALL);
    public static final RegistryObject<Item> MAGENTA_CONCRETE_WALL = registerBlockItem("magenta_concrete_wall",
            StoneBlocks.MAGENTA_CONCRETE_WALL);
    public static final RegistryObject<Item> PINK_CONCRETE_WALL = registerBlockItem("pink_concrete_wall",
            StoneBlocks.PINK_CONCRETE_WALL);
    public static final RegistryObject<Item> BLACK_CONCRETE_SLAB = registerBlockItem("black_concrete_slab",
            StoneBlocks.BLACK_CONCRETE_SLAB);
    public static final RegistryObject<Item> BLUE_CONCRETE_SLAB = registerBlockItem("blue_concrete_slab",
            StoneBlocks.BLUE_CONCRETE_SLAB);
    public static final RegistryObject<Item> BROWN_CONCRETE_SLAB = registerBlockItem("brown_concrete_slab",
            StoneBlocks.BROWN_CONCRETE_SLAB);
    public static final RegistryObject<Item> CYAN_CONCRETE_SLAB = registerBlockItem("cyan_concrete_slab",
            StoneBlocks.CYAN_CONCRETE_SLAB);
    public static final RegistryObject<Item> GRAY_CONCRETE_SLAB = registerBlockItem("gray_concrete_slab",
            StoneBlocks.GRAY_CONCRETE_SLAB);
    public static final RegistryObject<Item> GREEN_CONCRETE_SLAB = registerBlockItem("green_concrete_slab",
            StoneBlocks.GREEN_CONCRETE_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_CONCRETE_SLAB = registerBlockItem("light_blue_concrete_slab",
            StoneBlocks.LIGHT_BLUE_CONCRETE_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_CONCRETE_SLAB = registerBlockItem("light_gray_concrete_slab",
            StoneBlocks.LIGHT_GRAY_CONCRETE_SLAB);
    public static final RegistryObject<Item> LIME_CONCRETE_SLAB = registerBlockItem("lime_concrete_slab",
            StoneBlocks.LIME_CONCRETE_SLAB);
    public static final RegistryObject<Item> MAGENTA_CONCRETE_SLAB = registerBlockItem("magenta_concrete_slab",
            StoneBlocks.MAGENTA_CONCRETE_SLAB);
    public static final RegistryObject<Item> ORANGE_CONCRETE_SLAB = registerBlockItem("orange_concrete_slab",
            StoneBlocks.ORANGE_CONCRETE_SLAB);
    public static final RegistryObject<Item> PINK_CONCRETE_SLAB = registerBlockItem("pink_concrete_slab",
            StoneBlocks.PINK_CONCRETE_SLAB);
    public static final RegistryObject<Item> PURPLE_CONCRETE_SLAB = registerBlockItem("purple_concrete_slab",
            StoneBlocks.PURPLE_CONCRETE_SLAB);
    public static final RegistryObject<Item> RED_CONCRETE_SLAB = registerBlockItem("red_concrete_slab",
            StoneBlocks.RED_CONCRETE_SLAB);
    public static final RegistryObject<Item> WHITE_CONCRETE_SLAB = registerBlockItem("white_concrete_slab",
            StoneBlocks.WHITE_CONCRETE_SLAB);
    public static final RegistryObject<Item> YELLOW_CONCRETE_SLAB = registerBlockItem("yellow_concrete_slab",
            StoneBlocks.YELLOW_CONCRETE_SLAB);
    public static final RegistryObject<Item> BROWN_MUSHROOM_SHELVES = registerBlockItem("brown_mushroom_shelves",
            StoneBlocks.BROWN_MUSHROOM_SHELVES);
    public static final RegistryObject<Item> RED_MUSHROOM_SHELVES = registerBlockItem("red_mushroom_shelves",
            StoneBlocks.RED_MUSHROOM_SHELVES);
    public static final RegistryObject<Item> QUARTZ_PILLAR = registerBlockItem("quartz_pillar",
            StoneBlocks.QUARTZ_PILLAR);
    public static final RegistryObject<Item> STONE_PILLAR = registerBlockItem("stone_pillar", StoneBlocks.STONE_PILLAR);
    public static final RegistryObject<Item> DEEPSLATE_PILLAR = registerBlockItem("deepslate_pillar",
            StoneBlocks.DEEPSLATE_PILLAR);
    public static final RegistryObject<Item> MOSSY_PILLAR = registerBlockItem("mossy_pillar", StoneBlocks.MOSSY_PILLAR);
    public static final RegistryObject<Item> AMETHYST_PILLAR = registerBlockItem("amethyst_pillar",
            StoneBlocks.AMETHYST_PILLAR);
    public static final RegistryObject<Item> ANDESITE_PILLAR = registerBlockItem("andesite_pillar",
            StoneBlocks.ANDESITE_PILLAR);
    public static final RegistryObject<Item> BASALT_PILLAR = registerBlockItem("basalt_pillar",
            StoneBlocks.BASALT_PILLAR);
    public static final RegistryObject<Item> BLACKSTONE_PILLAR = registerBlockItem("blackstone_pillar",
            StoneBlocks.BLACKSTONE_PILLAR);
    public static final RegistryObject<Item> BLUE_ICE_PILLAR = registerBlockItem("blue_ice_pillar",
            StoneBlocks.BLUE_ICE_PILLAR);
    public static final RegistryObject<Item> CALCITE_PILLAR = registerBlockItem("calcite_pillar",
            StoneBlocks.CALCITE_PILLAR);
    public static final RegistryObject<Item> CINNABAR_PILLAR = registerBlockItem("cinnabar_pillar",
            StoneBlocks.CINNABAR_PILLAR);
    public static final RegistryObject<Item> COPPER_PILLAR = registerBlockItem("copper_pillar",
            StoneBlocks.COPPER_PILLAR);
    public static final RegistryObject<Item> DARK_PRISMARINE_PILLAR = registerBlockItem("dark_prismarine_pillar",
            StoneBlocks.DARK_PRISMARINE_PILLAR);
    public static final RegistryObject<Item> DIORITE_PILLAR = registerBlockItem("diorite_pillar",
            StoneBlocks.DIORITE_PILLAR);
    public static final RegistryObject<Item> DRIPSTONE_PILLAR = registerBlockItem("dripstone_pillar",
            StoneBlocks.DRIPSTONE_PILLAR);
    public static final RegistryObject<Item> EXPOSED_COPPER_PILLAR = registerBlockItem("exposed_copper_pillar",
            StoneBlocks.EXPOSED_COPPER_PILLAR);
    public static final RegistryObject<Item> GRANITE_PILLAR = registerBlockItem("granite_pillar",
            StoneBlocks.GRANITE_PILLAR);
    public static final RegistryObject<Item> NETHERRACK_PILLAR = registerBlockItem("netherrack_pillar",
            StoneBlocks.NETHERRACK_PILLAR);
    public static final RegistryObject<Item> OBSIDIAN_PILLAR = registerBlockItem("obsidian_pillar",
            StoneBlocks.OBSIDIAN_PILLAR);
    public static final RegistryObject<Item> OXIDIZED_COPPER_PILLAR = registerBlockItem("oxidized_copper_pillar",
            StoneBlocks.OXIDIZED_COPPER_PILLAR);
    public static final RegistryObject<Item> PACKED_MUD_PILLAR = registerBlockItem("packed_mud_pillar",
            StoneBlocks.PACKED_MUD_PILLAR);
    public static final RegistryObject<Item> PRISMARINE_PILLAR = registerBlockItem("prismarine_pillar",
            StoneBlocks.PRISMARINE_PILLAR);
    public static final RegistryObject<Item> SCULK_PILLAR = registerBlockItem("sculk_pillar", StoneBlocks.SCULK_PILLAR);
    public static final RegistryObject<Item> SULFUR_PILLAR = registerBlockItem("sulfur_pillar",
            StoneBlocks.SULFUR_PILLAR);
    public static final RegistryObject<Item> TUFF_PILLAR = registerBlockItem("tuff_pillar", StoneBlocks.TUFF_PILLAR);
    public static final RegistryObject<Item> WEATHERED_COPPER_PILLAR = registerBlockItem("weathered_copper_pillar",
            StoneBlocks.WEATHERED_COPPER_PILLAR);
    public static final RegistryObject<Item> ASHENKING_DIAMOND_PILLAR = registerBlockItem("ashenking_diamond_pillar",
            StoneBlocks.ASHENKING_DIAMOND_PILLAR);
    public static final RegistryObject<Item> ASHENKING_GOLD_PILLAR = registerBlockItem("ashenking_gold_pillar",
            StoneBlocks.ASHENKING_GOLD_PILLAR);
    public static final RegistryObject<Item> ASHENKING_EMERALD_PILLAR = registerBlockItem("ashenking_emerald_pillar",
            StoneBlocks.ASHENKING_EMERALD_PILLAR);
    public static final RegistryObject<Item> ASHENKING_NETHERITE_PILLAR = registerBlockItem(
            "ashenking_netherite_pillar", StoneBlocks.ASHENKING_NETHERITE_PILLAR);
    public static final RegistryObject<Item> DECORATED_POT = registerBlockItem("decorated_pot",
            StoneBlocks.DECORATED_POT);
}
