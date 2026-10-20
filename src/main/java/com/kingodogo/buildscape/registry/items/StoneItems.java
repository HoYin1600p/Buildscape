package com.kingodogo.buildscape.registry.items;

import com.kingodogo.buildscape.registry.ModBlocks;
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

    public static final RegistryObject<Item> BLACK_SAND = registerBlockItem("black_sand", ModBlocks.BLACK_SAND);
    public static final RegistryObject<Item> BLUE_SAND = registerBlockItem("blue_sand", ModBlocks.BLUE_SAND);
    public static final RegistryObject<Item> GREEN_SAND = registerBlockItem("green_sand", ModBlocks.GREEN_SAND);
    public static final RegistryObject<Item> ORANGE_SAND = registerBlockItem("orange_sand", ModBlocks.ORANGE_SAND);
    public static final RegistryObject<Item> PINK_SAND = registerBlockItem("pink_sand", ModBlocks.PINK_SAND);
    public static final RegistryObject<Item> RED_SAND = registerBlockItem("red_sand", ModBlocks.RED_SAND);
    public static final RegistryObject<Item> WHITE_SAND = registerBlockItem("white_sand", ModBlocks.WHITE_SAND);
    public static final RegistryObject<Item> YELLOW_SAND = registerBlockItem("yellow_sand", ModBlocks.YELLOW_SAND);
    public static final RegistryObject<Item> BLACK_SANDSTONE = registerBlockItem("black_sandstone",
            ModBlocks.BLACK_SANDSTONE);
    public static final RegistryObject<Item> BLUE_SANDSTONE = registerBlockItem("blue_sandstone",
            ModBlocks.BLUE_SANDSTONE);
    public static final RegistryObject<Item> GREEN_SANDSTONE = registerBlockItem("green_sandstone",
            ModBlocks.GREEN_SANDSTONE);
    public static final RegistryObject<Item> ORANGE_SANDSTONE = registerBlockItem("orange_sandstone",
            ModBlocks.ORANGE_SANDSTONE);
    public static final RegistryObject<Item> PINK_SANDSTONE = registerBlockItem("pink_sandstone",
            ModBlocks.PINK_SANDSTONE);
    public static final RegistryObject<Item> RED_SANDSTONE = registerBlockItem("red_sandstone",
            ModBlocks.RED_SANDSTONE);
    public static final RegistryObject<Item> WHITE_SANDSTONE = registerBlockItem("white_sandstone",
            ModBlocks.WHITE_SANDSTONE);
    public static final RegistryObject<Item> YELLOW_SANDSTONE = registerBlockItem("yellow_sandstone",
            ModBlocks.YELLOW_SANDSTONE);
    public static final RegistryObject<Item> BLACK_SMOOTH_SANDSTONE = registerBlockItem("black_smooth_sandstone",
            ModBlocks.BLACK_SMOOTH_SANDSTONE);
    public static final RegistryObject<Item> BLUE_SMOOTH_SANDSTONE = registerBlockItem("blue_smooth_sandstone",
            ModBlocks.BLUE_SMOOTH_SANDSTONE);
    public static final RegistryObject<Item> GREEN_SMOOTH_SANDSTONE = registerBlockItem("green_smooth_sandstone",
            ModBlocks.GREEN_SMOOTH_SANDSTONE);
    public static final RegistryObject<Item> ORANGE_SMOOTH_SANDSTONE = registerBlockItem("orange_smooth_sandstone",
            ModBlocks.ORANGE_SMOOTH_SANDSTONE);
    public static final RegistryObject<Item> PINK_SMOOTH_SANDSTONE = registerBlockItem("pink_smooth_sandstone",
            ModBlocks.PINK_SMOOTH_SANDSTONE);
    public static final RegistryObject<Item> RED_SMOOTH_SANDSTONE = registerBlockItem("red_smooth_sandstone",
            ModBlocks.RED_SMOOTH_SANDSTONE);
    public static final RegistryObject<Item> WHITE_SMOOTH_SANDSTONE = registerBlockItem("white_smooth_sandstone",
            ModBlocks.WHITE_SMOOTH_SANDSTONE);
    public static final RegistryObject<Item> YELLOW_SMOOTH_SANDSTONE = registerBlockItem("yellow_smooth_sandstone",
            ModBlocks.YELLOW_SMOOTH_SANDSTONE);
    public static final RegistryObject<Item> BLACK_TILES = registerBlockItem("black_tiles", ModBlocks.BLACK_TILES);
    public static final RegistryObject<Item> BLUE_TILES = registerBlockItem("blue_tiles", ModBlocks.BLUE_TILES);
    public static final RegistryObject<Item> BROWN_TILES = registerBlockItem("brown_tiles", ModBlocks.BROWN_TILES);
    public static final RegistryObject<Item> CYAN_TILES = registerBlockItem("cyan_tiles", ModBlocks.CYAN_TILES);
    public static final RegistryObject<Item> GRAY_TILES = registerBlockItem("gray_tiles", ModBlocks.GRAY_TILES);
    public static final RegistryObject<Item> GREEN_TILES = registerBlockItem("green_tiles", ModBlocks.GREEN_TILES);
    public static final RegistryObject<Item> LIGHT_BLUE_TILES = registerBlockItem("light_blue_tiles",
            ModBlocks.LIGHT_BLUE_TILES);
    public static final RegistryObject<Item> LIGHT_GRAY_TILES = registerBlockItem("light_gray_tiles",
            ModBlocks.LIGHT_GRAY_TILES);
    public static final RegistryObject<Item> LIME_TILES = registerBlockItem("lime_tiles", ModBlocks.LIME_TILES);
    public static final RegistryObject<Item> MAGENTA_TILES = registerBlockItem("magenta_tiles",
            ModBlocks.MAGENTA_TILES);
    public static final RegistryObject<Item> ORANGE_TILES = registerBlockItem("orange_tiles", ModBlocks.ORANGE_TILES);
    public static final RegistryObject<Item> PINK_TILES = registerBlockItem("pink_tiles", ModBlocks.PINK_TILES);
    public static final RegistryObject<Item> PURPLE_TILES = registerBlockItem("purple_tiles", ModBlocks.PURPLE_TILES);
    public static final RegistryObject<Item> RED_TILES = registerBlockItem("red_tiles", ModBlocks.RED_TILES);
    public static final RegistryObject<Item> WHITE_TILES = registerBlockItem("white_tiles", ModBlocks.WHITE_TILES);
    public static final RegistryObject<Item> YELLOW_TILES = registerBlockItem("yellow_tiles", ModBlocks.YELLOW_TILES);
    public static final RegistryObject<Item> BLACK_MOSAIC_GLASS = registerBlockItem("black_mosaic_glass",
            ModBlocks.BLACK_MOSAIC_GLASS);
    public static final RegistryObject<Item> BLUE_MOSAIC_GLASS = registerBlockItem("blue_mosaic_glass",
            ModBlocks.BLUE_MOSAIC_GLASS);
    public static final RegistryObject<Item> BROWN_MOSAIC_GLASS = registerBlockItem("brown_mosaic_glass",
            ModBlocks.BROWN_MOSAIC_GLASS);
    public static final RegistryObject<Item> CYAN_MOSAIC_GLASS = registerBlockItem("cyan_mosaic_glass",
            ModBlocks.CYAN_MOSAIC_GLASS);
    public static final RegistryObject<Item> GRAY_MOSAIC_GLASS = registerBlockItem("gray_mosaic_glass",
            ModBlocks.GRAY_MOSAIC_GLASS);
    public static final RegistryObject<Item> GREEN_MOSAIC_GLASS = registerBlockItem("green_mosaic_glass",
            ModBlocks.GREEN_MOSAIC_GLASS);
    public static final RegistryObject<Item> LIGHT_BLUE_MOSAIC_GLASS = registerBlockItem("light_blue_mosaic_glass",
            ModBlocks.LIGHT_BLUE_MOSAIC_GLASS);
    public static final RegistryObject<Item> LIGHT_GRAY_MOSAIC_GLASS = registerBlockItem("light_gray_mosaic_glass",
            ModBlocks.LIGHT_GRAY_MOSAIC_GLASS);
    public static final RegistryObject<Item> LIME_MOSAIC_GLASS = registerBlockItem("lime_mosaic_glass",
            ModBlocks.LIME_MOSAIC_GLASS);
    public static final RegistryObject<Item> MAGENTA_MOSAIC_GLASS = registerBlockItem("magenta_mosaic_glass",
            ModBlocks.MAGENTA_MOSAIC_GLASS);
    public static final RegistryObject<Item> ORANGE_MOSAIC_GLASS = registerBlockItem("orange_mosaic_glass",
            ModBlocks.ORANGE_MOSAIC_GLASS);
    public static final RegistryObject<Item> PINK_MOSAIC_GLASS = registerBlockItem("pink_mosaic_glass",
            ModBlocks.PINK_MOSAIC_GLASS);
    public static final RegistryObject<Item> PURPLE_MOSAIC_GLASS = registerBlockItem("purple_mosaic_glass",
            ModBlocks.PURPLE_MOSAIC_GLASS);
    public static final RegistryObject<Item> RED_MOSAIC_GLASS = registerBlockItem("red_mosaic_glass",
            ModBlocks.RED_MOSAIC_GLASS);
    public static final RegistryObject<Item> WHITE_MOSAIC_GLASS = registerBlockItem("white_mosaic_glass",
            ModBlocks.WHITE_MOSAIC_GLASS);
    public static final RegistryObject<Item> YELLOW_MOSAIC_GLASS = registerBlockItem("yellow_mosaic_glass",
            ModBlocks.YELLOW_MOSAIC_GLASS);
    public static final RegistryObject<Item> BLACK_MOSAIC_GLASS_PANE = registerBlockItem("black_mosaic_glass_pane",
            ModBlocks.BLACK_MOSAIC_GLASS_PANE);
    public static final RegistryObject<Item> BLUE_MOSAIC_GLASS_PANE = registerBlockItem("blue_mosaic_glass_pane",
            ModBlocks.BLUE_MOSAIC_GLASS_PANE);
    public static final RegistryObject<Item> BROWN_MOSAIC_GLASS_PANE = registerBlockItem("brown_mosaic_glass_pane",
            ModBlocks.BROWN_MOSAIC_GLASS_PANE);
    public static final RegistryObject<Item> CYAN_MOSAIC_GLASS_PANE = registerBlockItem("cyan_mosaic_glass_pane",
            ModBlocks.CYAN_MOSAIC_GLASS_PANE);
    public static final RegistryObject<Item> GRAY_MOSAIC_GLASS_PANE = registerBlockItem("gray_mosaic_glass_pane",
            ModBlocks.GRAY_MOSAIC_GLASS_PANE);
    public static final RegistryObject<Item> GREEN_MOSAIC_GLASS_PANE = registerBlockItem("green_mosaic_glass_pane",
            ModBlocks.GREEN_MOSAIC_GLASS_PANE);
    public static final RegistryObject<Item> LIGHT_BLUE_MOSAIC_GLASS_PANE = registerBlockItem(
            "light_blue_mosaic_glass_pane", ModBlocks.LIGHT_BLUE_MOSAIC_GLASS_PANE);
    public static final RegistryObject<Item> LIGHT_GRAY_MOSAIC_GLASS_PANE = registerBlockItem(
            "light_gray_mosaic_glass_pane", ModBlocks.LIGHT_GRAY_MOSAIC_GLASS_PANE);
    public static final RegistryObject<Item> LIME_MOSAIC_GLASS_PANE = registerBlockItem("lime_mosaic_glass_pane",
            ModBlocks.LIME_MOSAIC_GLASS_PANE);
    public static final RegistryObject<Item> MAGENTA_MOSAIC_GLASS_PANE = registerBlockItem("magenta_mosaic_glass_pane",
            ModBlocks.MAGENTA_MOSAIC_GLASS_PANE);
    public static final RegistryObject<Item> ORANGE_MOSAIC_GLASS_PANE = registerBlockItem("orange_mosaic_glass_pane",
            ModBlocks.ORANGE_MOSAIC_GLASS_PANE);
    public static final RegistryObject<Item> PINK_MOSAIC_GLASS_PANE = registerBlockItem("pink_mosaic_glass_pane",
            ModBlocks.PINK_MOSAIC_GLASS_PANE);
    public static final RegistryObject<Item> PURPLE_MOSAIC_GLASS_PANE = registerBlockItem("purple_mosaic_glass_pane",
            ModBlocks.PURPLE_MOSAIC_GLASS_PANE);
    public static final RegistryObject<Item> RED_MOSAIC_GLASS_PANE = registerBlockItem("red_mosaic_glass_pane",
            ModBlocks.RED_MOSAIC_GLASS_PANE);
    public static final RegistryObject<Item> WHITE_MOSAIC_GLASS_PANE = registerBlockItem("white_mosaic_glass_pane",
            ModBlocks.WHITE_MOSAIC_GLASS_PANE);
    public static final RegistryObject<Item> YELLOW_MOSAIC_GLASS_PANE = registerBlockItem("yellow_mosaic_glass_pane",
            ModBlocks.YELLOW_MOSAIC_GLASS_PANE);
    public static final RegistryObject<Item> BLACK_GLAZED_GLASS = registerBlockItem("black_glazed_glass",
            ModBlocks.BLACK_GLAZED_GLASS);
    public static final RegistryObject<Item> BLUE_GLAZED_GLASS = registerBlockItem("blue_glazed_glass",
            ModBlocks.BLUE_GLAZED_GLASS);
    public static final RegistryObject<Item> BROWN_GLAZED_GLASS = registerBlockItem("brown_glazed_glass",
            ModBlocks.BROWN_GLAZED_GLASS);
    public static final RegistryObject<Item> CYAN_GLAZED_GLASS = registerBlockItem("cyan_glazed_glass",
            ModBlocks.CYAN_GLAZED_GLASS);
    public static final RegistryObject<Item> GRAY_GLAZED_GLASS = registerBlockItem("gray_glazed_glass",
            ModBlocks.GRAY_GLAZED_GLASS);
    public static final RegistryObject<Item> GREEN_GLAZED_GLASS = registerBlockItem("green_glazed_glass",
            ModBlocks.GREEN_GLAZED_GLASS);
    public static final RegistryObject<Item> LIGHT_BLUE_GLAZED_GLASS = registerBlockItem("light_blue_glazed_glass",
            ModBlocks.LIGHT_BLUE_GLAZED_GLASS);
    public static final RegistryObject<Item> LIGHT_GRAY_GLAZED_GLASS = registerBlockItem("light_gray_glazed_glass",
            ModBlocks.LIGHT_GRAY_GLAZED_GLASS);
    public static final RegistryObject<Item> LIME_GLAZED_GLASS = registerBlockItem("lime_glazed_glass",
            ModBlocks.LIME_GLAZED_GLASS);
    public static final RegistryObject<Item> MAGENTA_GLAZED_GLASS = registerBlockItem("magenta_glazed_glass",
            ModBlocks.MAGENTA_GLAZED_GLASS);
    public static final RegistryObject<Item> ORANGE_GLAZED_GLASS = registerBlockItem("orange_glazed_glass",
            ModBlocks.ORANGE_GLAZED_GLASS);
    public static final RegistryObject<Item> PINK_GLAZED_GLASS = registerBlockItem("pink_glazed_glass",
            ModBlocks.PINK_GLAZED_GLASS);
    public static final RegistryObject<Item> PURPLE_GLAZED_GLASS = registerBlockItem("purple_glazed_glass",
            ModBlocks.PURPLE_GLAZED_GLASS);
    public static final RegistryObject<Item> RED_GLAZED_GLASS = registerBlockItem("red_glazed_glass",
            ModBlocks.RED_GLAZED_GLASS);
    public static final RegistryObject<Item> WHITE_GLAZED_GLASS = registerBlockItem("white_glazed_glass",
            ModBlocks.WHITE_GLAZED_GLASS);
    public static final RegistryObject<Item> YELLOW_GLAZED_GLASS = registerBlockItem("yellow_glazed_glass",
            ModBlocks.YELLOW_GLAZED_GLASS);
    public static final RegistryObject<Item> BLACK_GLAZED_GLASS_PANE = registerBlockItem("black_glazed_glass_pane",
            ModBlocks.BLACK_GLAZED_GLASS_PANE);
    public static final RegistryObject<Item> BLUE_GLAZED_GLASS_PANE = registerBlockItem("blue_glazed_glass_pane",
            ModBlocks.BLUE_GLAZED_GLASS_PANE);
    public static final RegistryObject<Item> BROWN_GLAZED_GLASS_PANE = registerBlockItem("brown_glazed_glass_pane",
            ModBlocks.BROWN_GLAZED_GLASS_PANE);
    public static final RegistryObject<Item> CYAN_GLAZED_GLASS_PANE = registerBlockItem("cyan_glazed_glass_pane",
            ModBlocks.CYAN_GLAZED_GLASS_PANE);
    public static final RegistryObject<Item> GRAY_GLAZED_GLASS_PANE = registerBlockItem("gray_glazed_glass_pane",
            ModBlocks.GRAY_GLAZED_GLASS_PANE);
    public static final RegistryObject<Item> GREEN_GLAZED_GLASS_PANE = registerBlockItem("green_glazed_glass_pane",
            ModBlocks.GREEN_GLAZED_GLASS_PANE);
    public static final RegistryObject<Item> LIGHT_BLUE_GLAZED_GLASS_PANE = registerBlockItem(
            "light_blue_glazed_glass_pane", ModBlocks.LIGHT_BLUE_GLAZED_GLASS_PANE);
    public static final RegistryObject<Item> LIGHT_GRAY_GLAZED_GLASS_PANE = registerBlockItem(
            "light_gray_glazed_glass_pane", ModBlocks.LIGHT_GRAY_GLAZED_GLASS_PANE);
    public static final RegistryObject<Item> LIME_GLAZED_GLASS_PANE = registerBlockItem("lime_glazed_glass_pane",
            ModBlocks.LIME_GLAZED_GLASS_PANE);
    public static final RegistryObject<Item> MAGENTA_GLAZED_GLASS_PANE = registerBlockItem("magenta_glazed_glass_pane",
            ModBlocks.MAGENTA_GLAZED_GLASS_PANE);
    public static final RegistryObject<Item> ORANGE_GLAZED_GLASS_PANE = registerBlockItem("orange_glazed_glass_pane",
            ModBlocks.ORANGE_GLAZED_GLASS_PANE);
    public static final RegistryObject<Item> PINK_GLAZED_GLASS_PANE = registerBlockItem("pink_glazed_glass_pane",
            ModBlocks.PINK_GLAZED_GLASS_PANE);
    public static final RegistryObject<Item> PURPLE_GLAZED_GLASS_PANE = registerBlockItem("purple_glazed_glass_pane",
            ModBlocks.PURPLE_GLAZED_GLASS_PANE);
    public static final RegistryObject<Item> RED_GLAZED_GLASS_PANE = registerBlockItem("red_glazed_glass_pane",
            ModBlocks.RED_GLAZED_GLASS_PANE);
    public static final RegistryObject<Item> WHITE_GLAZED_GLASS_PANE = registerBlockItem("white_glazed_glass_pane",
            ModBlocks.WHITE_GLAZED_GLASS_PANE);
    public static final RegistryObject<Item> YELLOW_GLAZED_GLASS_PANE = registerBlockItem("yellow_glazed_glass_pane",
            ModBlocks.YELLOW_GLAZED_GLASS_PANE);
    public static final RegistryObject<Item> BIT_CHISELED_COPPER = registerBlockItem("bit_chiseled_copper",
            ModBlocks.BIT_CHISELED_COPPER);
    public static final RegistryObject<Item> BIT_COPPER_BLOCK = registerBlockItem("bit_copper_block",
            ModBlocks.BIT_COPPER_BLOCK);
    public static final RegistryObject<Item> BIT_COPPER_BULB = registerBlockItem("bit_copper_bulb",
            ModBlocks.BIT_COPPER_BULB);
    public static final RegistryObject<Item> BIT_COPPER_GRATE = registerBlockItem("bit_copper_grate",
            ModBlocks.BIT_COPPER_GRATE);
    public static final RegistryObject<Item> BIT_CUT_COPPER = registerBlockItem("bit_cut_copper",
            ModBlocks.BIT_CUT_COPPER);
    public static final RegistryObject<Item> BIT_EXPOSED_CHISELED_COPPER = registerBlockItem(
            "bit_exposed_chiseled_copper", ModBlocks.BIT_EXPOSED_CHISELED_COPPER);
    public static final RegistryObject<Item> BIT_EXPOSED_COPPER_BLOCK = registerBlockItem("bit_exposed_copper_block",
            ModBlocks.BIT_EXPOSED_COPPER_BLOCK);
    public static final RegistryObject<Item> BIT_EXPOSED_COPPER_BULB = registerBlockItem("bit_exposed_copper_bulb",
            ModBlocks.BIT_EXPOSED_COPPER_BULB);
    public static final RegistryObject<Item> BIT_EXPOSED_COPPER_GRATE = registerBlockItem("bit_exposed_copper_grate",
            ModBlocks.BIT_EXPOSED_COPPER_GRATE);
    public static final RegistryObject<Item> BIT_EXPOSED_CUT_COPPER = registerBlockItem("bit_exposed_cut_copper",
            ModBlocks.BIT_EXPOSED_CUT_COPPER);
    public static final RegistryObject<Item> BIT_WEATHERED_CHISELED_COPPER = registerBlockItem(
            "bit_weathered_chiseled_copper", ModBlocks.BIT_WEATHERED_CHISELED_COPPER);
    public static final RegistryObject<Item> BIT_WEATHERED_COPPER_BLOCK = registerBlockItem(
            "bit_weathered_copper_block", ModBlocks.BIT_WEATHERED_COPPER_BLOCK);
    public static final RegistryObject<Item> BIT_WEATHERED_COPPER_BULB = registerBlockItem("bit_weathered_copper_bulb",
            ModBlocks.BIT_WEATHERED_COPPER_BULB);
    public static final RegistryObject<Item> BIT_WEATHERED_COPPER_GRATE = registerBlockItem(
            "bit_weathered_copper_grate", ModBlocks.BIT_WEATHERED_COPPER_GRATE);
    public static final RegistryObject<Item> BIT_WEATHERED_CUT_COPPER = registerBlockItem("bit_weathered_cut_copper",
            ModBlocks.BIT_WEATHERED_CUT_COPPER);
    public static final RegistryObject<Item> BIT_OXIDIZED_CHISELED_COPPER = registerBlockItem(
            "bit_oxidized_chiseled_copper", ModBlocks.BIT_OXIDIZED_CHISELED_COPPER);
    public static final RegistryObject<Item> BIT_OXIDIZED_COPPER_BLOCK = registerBlockItem("bit_oxidized_copper_block",
            ModBlocks.BIT_OXIDIZED_COPPER_BLOCK);
    public static final RegistryObject<Item> BIT_OXIDIZED_COPPER_BULB = registerBlockItem("bit_oxidized_copper_bulb",
            ModBlocks.BIT_OXIDIZED_COPPER_BULB);
    public static final RegistryObject<Item> BIT_OXIDIZED_COPPER_GRATE = registerBlockItem("bit_oxidized_copper_grate",
            ModBlocks.BIT_OXIDIZED_COPPER_GRATE);
    public static final RegistryObject<Item> BIT_OXIDIZED_CUT_COPPER = registerBlockItem("bit_oxidized_cut_copper",
            ModBlocks.BIT_OXIDIZED_CUT_COPPER);
    public static final RegistryObject<Item> BIT_CHISELED_TUFF = registerBlockItem("bit_chiseled_tuff",
            ModBlocks.BIT_CHISELED_TUFF);
    public static final RegistryObject<Item> BIT_CHISELED_TUFF_BRICKS = registerBlockItem("bit_chiseled_tuff_bricks",
            ModBlocks.BIT_CHISELED_TUFF_BRICKS);
    public static final RegistryObject<Item> BIT_POLISHED_TUFF = registerBlockItem("bit_polished_tuff",
            ModBlocks.BIT_POLISHED_TUFF);
    public static final RegistryObject<Item> BIT_POLISHED_TUFF_STAIRS = registerBlockItem("bit_polished_tuff_stairs",
            ModBlocks.BIT_POLISHED_TUFF_STAIRS);
    public static final RegistryObject<Item> BIT_POLISHED_TUFF_SLAB = registerBlockItem("bit_polished_tuff_slab",
            ModBlocks.BIT_POLISHED_TUFF_SLAB);
    public static final RegistryObject<Item> BIT_POLISHED_TUFF_WALL = registerBlockItem("bit_polished_tuff_wall",
            ModBlocks.BIT_POLISHED_TUFF_WALL);
    public static final RegistryObject<Item> BIT_TUFF_BRICKS = registerBlockItem("bit_tuff_bricks",
            ModBlocks.BIT_TUFF_BRICKS);
    public static final RegistryObject<Item> BIT_TUFF_BRICKS_STAIRS = registerBlockItem("bit_tuff_bricks_stairs",
            ModBlocks.BIT_TUFF_BRICKS_STAIRS);
    public static final RegistryObject<Item> BIT_TUFF_BRICKS_SLAB = registerBlockItem("bit_tuff_bricks_slab",
            ModBlocks.BIT_TUFF_BRICKS_SLAB);
    public static final RegistryObject<Item> BIT_TUFF_BRICKS_WALL = registerBlockItem("bit_tuff_bricks_wall",
            ModBlocks.BIT_TUFF_BRICKS_WALL);
    public static final RegistryObject<Item> BLACK_SANDSTONE_STAIRS = registerBlockItem("black_sandstone_stairs",
            ModBlocks.BLACK_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> BLUE_SANDSTONE_STAIRS = registerBlockItem("blue_sandstone_stairs",
            ModBlocks.BLUE_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> GREEN_SANDSTONE_STAIRS = registerBlockItem("green_sandstone_stairs",
            ModBlocks.GREEN_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> ORANGE_SANDSTONE_STAIRS = registerBlockItem("orange_sandstone_stairs",
            ModBlocks.ORANGE_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> PINK_SANDSTONE_STAIRS = registerBlockItem("pink_sandstone_stairs",
            ModBlocks.PINK_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> RED_SANDSTONE_STAIRS = registerBlockItem("red_sandstone_stairs",
            ModBlocks.RED_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> WHITE_SANDSTONE_STAIRS = registerBlockItem("white_sandstone_stairs",
            ModBlocks.WHITE_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> YELLOW_SANDSTONE_STAIRS = registerBlockItem("yellow_sandstone_stairs",
            ModBlocks.YELLOW_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> BLACK_SANDSTONE_SLAB = registerBlockItem("black_sandstone_slab",
            ModBlocks.BLACK_SANDSTONE_SLAB);
    public static final RegistryObject<Item> BLUE_SANDSTONE_SLAB = registerBlockItem("blue_sandstone_slab",
            ModBlocks.BLUE_SANDSTONE_SLAB);
    public static final RegistryObject<Item> GREEN_SANDSTONE_SLAB = registerBlockItem("green_sandstone_slab",
            ModBlocks.GREEN_SANDSTONE_SLAB);
    public static final RegistryObject<Item> ORANGE_SANDSTONE_SLAB = registerBlockItem("orange_sandstone_slab",
            ModBlocks.ORANGE_SANDSTONE_SLAB);
    public static final RegistryObject<Item> PINK_SANDSTONE_SLAB = registerBlockItem("pink_sandstone_slab",
            ModBlocks.PINK_SANDSTONE_SLAB);
    public static final RegistryObject<Item> RED_SANDSTONE_SLAB = registerBlockItem("red_sandstone_slab",
            ModBlocks.RED_SANDSTONE_SLAB);
    public static final RegistryObject<Item> WHITE_SANDSTONE_SLAB = registerBlockItem("white_sandstone_slab",
            ModBlocks.WHITE_SANDSTONE_SLAB);
    public static final RegistryObject<Item> YELLOW_SANDSTONE_SLAB = registerBlockItem("yellow_sandstone_slab",
            ModBlocks.YELLOW_SANDSTONE_SLAB);
    public static final RegistryObject<Item> BLACK_SANDSTONE_WALL = registerBlockItem("black_sandstone_wall",
            ModBlocks.BLACK_SANDSTONE_WALL);
    public static final RegistryObject<Item> BLUE_SANDSTONE_WALL = registerBlockItem("blue_sandstone_wall",
            ModBlocks.BLUE_SANDSTONE_WALL);
    public static final RegistryObject<Item> GREEN_SANDSTONE_WALL = registerBlockItem("green_sandstone_wall",
            ModBlocks.GREEN_SANDSTONE_WALL);
    public static final RegistryObject<Item> ORANGE_SANDSTONE_WALL = registerBlockItem("orange_sandstone_wall",
            ModBlocks.ORANGE_SANDSTONE_WALL);
    public static final RegistryObject<Item> PINK_SANDSTONE_WALL = registerBlockItem("pink_sandstone_wall",
            ModBlocks.PINK_SANDSTONE_WALL);
    public static final RegistryObject<Item> RED_SANDSTONE_WALL = registerBlockItem("red_sandstone_wall",
            ModBlocks.RED_SANDSTONE_WALL);
    public static final RegistryObject<Item> WHITE_SANDSTONE_WALL = registerBlockItem("white_sandstone_wall",
            ModBlocks.WHITE_SANDSTONE_WALL);
    public static final RegistryObject<Item> YELLOW_SANDSTONE_WALL = registerBlockItem("yellow_sandstone_wall",
            ModBlocks.YELLOW_SANDSTONE_WALL);
    public static final RegistryObject<Item> WHITE_SMOOTH_SANDSTONE_WALL = registerBlockItem(
            "white_smooth_sandstone_wall", ModBlocks.WHITE_SMOOTH_SANDSTONE_WALL);
    public static final RegistryObject<Item> BLACK_SMOOTH_SANDSTONE_WALL = registerBlockItem(
            "black_smooth_sandstone_wall", ModBlocks.BLACK_SMOOTH_SANDSTONE_WALL);
    public static final RegistryObject<Item> RED_SMOOTH_SANDSTONE_WALL = registerBlockItem("red_smooth_sandstone_wall",
            ModBlocks.RED_SMOOTH_SANDSTONE_WALL);
    public static final RegistryObject<Item> ORANGE_SMOOTH_SANDSTONE_WALL = registerBlockItem(
            "orange_smooth_sandstone_wall", ModBlocks.ORANGE_SMOOTH_SANDSTONE_WALL);
    public static final RegistryObject<Item> YELLOW_SMOOTH_SANDSTONE_WALL = registerBlockItem(
            "yellow_smooth_sandstone_wall", ModBlocks.YELLOW_SMOOTH_SANDSTONE_WALL);
    public static final RegistryObject<Item> GREEN_SMOOTH_SANDSTONE_WALL = registerBlockItem(
            "green_smooth_sandstone_wall", ModBlocks.GREEN_SMOOTH_SANDSTONE_WALL);
    public static final RegistryObject<Item> BLUE_SMOOTH_SANDSTONE_WALL = registerBlockItem(
            "blue_smooth_sandstone_wall", ModBlocks.BLUE_SMOOTH_SANDSTONE_WALL);
    public static final RegistryObject<Item> PINK_SMOOTH_SANDSTONE_WALL = registerBlockItem(
            "pink_smooth_sandstone_wall", ModBlocks.PINK_SMOOTH_SANDSTONE_WALL);
    public static final RegistryObject<Item> SMOOTH_SANDSTONE_WALL = registerBlockItem("smooth_sandstone_wall",
            ModBlocks.SMOOTH_SANDSTONE_WALL);
    public static final RegistryObject<Item> BLACK_SMOOTH_SANDSTONE_SLAB = registerBlockItem(
            "black_smooth_sandstone_slab", ModBlocks.BLACK_SMOOTH_SANDSTONE_SLAB);
    public static final RegistryObject<Item> BLUE_SMOOTH_SANDSTONE_SLAB = registerBlockItem(
            "blue_smooth_sandstone_slab", ModBlocks.BLUE_SMOOTH_SANDSTONE_SLAB);
    public static final RegistryObject<Item> GREEN_SMOOTH_SANDSTONE_SLAB = registerBlockItem(
            "green_smooth_sandstone_slab", ModBlocks.GREEN_SMOOTH_SANDSTONE_SLAB);
    public static final RegistryObject<Item> ORANGE_SMOOTH_SANDSTONE_SLAB = registerBlockItem(
            "orange_smooth_sandstone_slab", ModBlocks.ORANGE_SMOOTH_SANDSTONE_SLAB);
    public static final RegistryObject<Item> PINK_SMOOTH_SANDSTONE_SLAB = registerBlockItem(
            "pink_smooth_sandstone_slab", ModBlocks.PINK_SMOOTH_SANDSTONE_SLAB);
    public static final RegistryObject<Item> RED_SMOOTH_SANDSTONE_SLAB = registerBlockItem("red_smooth_sandstone_slab",
            ModBlocks.RED_SMOOTH_SANDSTONE_SLAB);
    public static final RegistryObject<Item> WHITE_SMOOTH_SANDSTONE_SLAB = registerBlockItem(
            "white_smooth_sandstone_slab", ModBlocks.WHITE_SMOOTH_SANDSTONE_SLAB);
    public static final RegistryObject<Item> YELLOW_SMOOTH_SANDSTONE_SLAB = registerBlockItem(
            "yellow_smooth_sandstone_slab", ModBlocks.YELLOW_SMOOTH_SANDSTONE_SLAB);
    public static final RegistryObject<Item> BLACK_SMOOTH_SANDSTONE_STAIRS = registerBlockItem(
            "black_smooth_sandstone_stairs", ModBlocks.BLACK_SMOOTH_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> BLUE_SMOOTH_SANDSTONE_STAIRS = registerBlockItem(
            "blue_smooth_sandstone_stairs", ModBlocks.BLUE_SMOOTH_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> GREEN_SMOOTH_SANDSTONE_STAIRS = registerBlockItem(
            "green_smooth_sandstone_stairs", ModBlocks.GREEN_SMOOTH_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> ORANGE_SMOOTH_SANDSTONE_STAIRS = registerBlockItem(
            "orange_smooth_sandstone_stairs", ModBlocks.ORANGE_SMOOTH_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> PINK_SMOOTH_SANDSTONE_STAIRS = registerBlockItem(
            "pink_smooth_sandstone_stairs", ModBlocks.PINK_SMOOTH_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> RED_SMOOTH_SANDSTONE_STAIRS = registerBlockItem(
            "red_smooth_sandstone_stairs", ModBlocks.RED_SMOOTH_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> WHITE_SMOOTH_SANDSTONE_STAIRS = registerBlockItem(
            "white_smooth_sandstone_stairs", ModBlocks.WHITE_SMOOTH_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> YELLOW_SMOOTH_SANDSTONE_STAIRS = registerBlockItem(
            "yellow_smooth_sandstone_stairs", ModBlocks.YELLOW_SMOOTH_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> BLACK_TILES_STAIRS = registerBlockItem("black_tiles_stairs",
            ModBlocks.BLACK_TILES_STAIRS);
    public static final RegistryObject<Item> BLUE_TILES_STAIRS = registerBlockItem("blue_tiles_stairs",
            ModBlocks.BLUE_TILES_STAIRS);
    public static final RegistryObject<Item> BROWN_TILES_STAIRS = registerBlockItem("brown_tiles_stairs",
            ModBlocks.BROWN_TILES_STAIRS);
    public static final RegistryObject<Item> CYAN_TILES_STAIRS = registerBlockItem("cyan_tiles_stairs",
            ModBlocks.CYAN_TILES_STAIRS);
    public static final RegistryObject<Item> GRAY_TILES_STAIRS = registerBlockItem("gray_tiles_stairs",
            ModBlocks.GRAY_TILES_STAIRS);
    public static final RegistryObject<Item> GREEN_TILES_STAIRS = registerBlockItem("green_tiles_stairs",
            ModBlocks.GREEN_TILES_STAIRS);
    public static final RegistryObject<Item> LIGHT_BLUE_TILES_STAIRS = registerBlockItem("light_blue_tiles_stairs",
            ModBlocks.LIGHT_BLUE_TILES_STAIRS);
    public static final RegistryObject<Item> LIGHT_GRAY_TILES_STAIRS = registerBlockItem("light_gray_tiles_stairs",
            ModBlocks.LIGHT_GRAY_TILES_STAIRS);
    public static final RegistryObject<Item> LIME_TILES_STAIRS = registerBlockItem("lime_tiles_stairs",
            ModBlocks.LIME_TILES_STAIRS);
    public static final RegistryObject<Item> MAGENTA_TILES_STAIRS = registerBlockItem("magenta_tiles_stairs",
            ModBlocks.MAGENTA_TILES_STAIRS);
    public static final RegistryObject<Item> ORANGE_TILES_STAIRS = registerBlockItem("orange_tiles_stairs",
            ModBlocks.ORANGE_TILES_STAIRS);
    public static final RegistryObject<Item> PINK_TILES_STAIRS = registerBlockItem("pink_tiles_stairs",
            ModBlocks.PINK_TILES_STAIRS);
    public static final RegistryObject<Item> PURPLE_TILES_STAIRS = registerBlockItem("purple_tiles_stairs",
            ModBlocks.PURPLE_TILES_STAIRS);
    public static final RegistryObject<Item> RED_TILES_STAIRS = registerBlockItem("red_tiles_stairs",
            ModBlocks.RED_TILES_STAIRS);
    public static final RegistryObject<Item> WHITE_TILES_STAIRS = registerBlockItem("white_tiles_stairs",
            ModBlocks.WHITE_TILES_STAIRS);
    public static final RegistryObject<Item> YELLOW_TILES_STAIRS = registerBlockItem("yellow_tiles_stairs",
            ModBlocks.YELLOW_TILES_STAIRS);
    public static final RegistryObject<Item> BLACK_TILES_SLAB = registerBlockItem("black_tiles_slab",
            ModBlocks.BLACK_TILES_SLAB);
    public static final RegistryObject<Item> BLUE_TILES_SLAB = registerBlockItem("blue_tiles_slab",
            ModBlocks.BLUE_TILES_SLAB);
    public static final RegistryObject<Item> BROWN_TILES_SLAB = registerBlockItem("brown_tiles_slab",
            ModBlocks.BROWN_TILES_SLAB);
    public static final RegistryObject<Item> CYAN_TILES_SLAB = registerBlockItem("cyan_tiles_slab",
            ModBlocks.CYAN_TILES_SLAB);
    public static final RegistryObject<Item> GRAY_TILES_SLAB = registerBlockItem("gray_tiles_slab",
            ModBlocks.GRAY_TILES_SLAB);
    public static final RegistryObject<Item> GREEN_TILES_SLAB = registerBlockItem("green_tiles_slab",
            ModBlocks.GREEN_TILES_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_TILES_SLAB = registerBlockItem("light_blue_tiles_slab",
            ModBlocks.LIGHT_BLUE_TILES_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_TILES_SLAB = registerBlockItem("light_gray_tiles_slab",
            ModBlocks.LIGHT_GRAY_TILES_SLAB);
    public static final RegistryObject<Item> LIME_TILES_SLAB = registerBlockItem("lime_tiles_slab",
            ModBlocks.LIME_TILES_SLAB);
    public static final RegistryObject<Item> MAGENTA_TILES_SLAB = registerBlockItem("magenta_tiles_slab",
            ModBlocks.MAGENTA_TILES_SLAB);
    public static final RegistryObject<Item> ORANGE_TILES_SLAB = registerBlockItem("orange_tiles_slab",
            ModBlocks.ORANGE_TILES_SLAB);
    public static final RegistryObject<Item> PINK_TILES_SLAB = registerBlockItem("pink_tiles_slab",
            ModBlocks.PINK_TILES_SLAB);
    public static final RegistryObject<Item> PURPLE_TILES_SLAB = registerBlockItem("purple_tiles_slab",
            ModBlocks.PURPLE_TILES_SLAB);
    public static final RegistryObject<Item> RED_TILES_SLAB = registerBlockItem("red_tiles_slab",
            ModBlocks.RED_TILES_SLAB);
    public static final RegistryObject<Item> WHITE_TILES_SLAB = registerBlockItem("white_tiles_slab",
            ModBlocks.WHITE_TILES_SLAB);
    public static final RegistryObject<Item> YELLOW_TILES_SLAB = registerBlockItem("yellow_tiles_slab",
            ModBlocks.YELLOW_TILES_SLAB);
    public static final RegistryObject<Item> BLACK_TILES_WALL = registerBlockItem("black_tiles_wall",
            ModBlocks.BLACK_TILES_WALL);
    public static final RegistryObject<Item> BLUE_TILES_WALL = registerBlockItem("blue_tiles_wall",
            ModBlocks.BLUE_TILES_WALL);
    public static final RegistryObject<Item> BROWN_TILES_WALL = registerBlockItem("brown_tiles_wall",
            ModBlocks.BROWN_TILES_WALL);
    public static final RegistryObject<Item> CYAN_TILES_WALL = registerBlockItem("cyan_tiles_wall",
            ModBlocks.CYAN_TILES_WALL);
    public static final RegistryObject<Item> GRAY_TILES_WALL = registerBlockItem("gray_tiles_wall",
            ModBlocks.GRAY_TILES_WALL);
    public static final RegistryObject<Item> GREEN_TILES_WALL = registerBlockItem("green_tiles_wall",
            ModBlocks.GREEN_TILES_WALL);
    public static final RegistryObject<Item> LIGHT_BLUE_TILES_WALL = registerBlockItem("light_blue_tiles_wall",
            ModBlocks.LIGHT_BLUE_TILES_WALL);
    public static final RegistryObject<Item> LIGHT_GRAY_TILES_WALL = registerBlockItem("light_gray_tiles_wall",
            ModBlocks.LIGHT_GRAY_TILES_WALL);
    public static final RegistryObject<Item> LIME_TILES_WALL = registerBlockItem("lime_tiles_wall",
            ModBlocks.LIME_TILES_WALL);
    public static final RegistryObject<Item> MAGENTA_TILES_WALL = registerBlockItem("magenta_tiles_wall",
            ModBlocks.MAGENTA_TILES_WALL);
    public static final RegistryObject<Item> ORANGE_TILES_WALL = registerBlockItem("orange_tiles_wall",
            ModBlocks.ORANGE_TILES_WALL);
    public static final RegistryObject<Item> PINK_TILES_WALL = registerBlockItem("pink_tiles_wall",
            ModBlocks.PINK_TILES_WALL);
    public static final RegistryObject<Item> PURPLE_TILES_WALL = registerBlockItem("purple_tiles_wall",
            ModBlocks.PURPLE_TILES_WALL);
    public static final RegistryObject<Item> RED_TILES_WALL = registerBlockItem("red_tiles_wall",
            ModBlocks.RED_TILES_WALL);
    public static final RegistryObject<Item> WHITE_TILES_WALL = registerBlockItem("white_tiles_wall",
            ModBlocks.WHITE_TILES_WALL);
    public static final RegistryObject<Item> YELLOW_TILES_WALL = registerBlockItem("yellow_tiles_wall",
            ModBlocks.YELLOW_TILES_WALL);
    public static final RegistryObject<Item> POLISHED_BASALT_STAIRS = registerBlockItem("polished_basalt_stairs",
            ModBlocks.POLISHED_BASALT_STAIRS);
    public static final RegistryObject<Item> POLISHED_BASALT_SLAB = registerBlockItem("polished_basalt_slab",
            ModBlocks.POLISHED_BASALT_SLAB);
    public static final RegistryObject<Item> POLISHED_BASALT_WALL = registerBlockItem("polished_basalt_wall",
            ModBlocks.POLISHED_BASALT_WALL);
    public static final RegistryObject<Item> DRIPSTONE_BLOCK_STAIRS = registerBlockItem("dripstone_block_stairs",
            ModBlocks.DRIPSTONE_BLOCK_STAIRS);
    public static final RegistryObject<Item> DRIPSTONE_BLOCK_SLAB = registerBlockItem("dripstone_block_slab",
            ModBlocks.DRIPSTONE_BLOCK_SLAB);
    public static final RegistryObject<Item> DRIPSTONE_BLOCK_WALL = registerBlockItem("dripstone_block_wall",
            ModBlocks.DRIPSTONE_BLOCK_WALL);
    public static final RegistryObject<Item> END_STONE_STAIRS = registerBlockItem("end_stone_stairs",
            ModBlocks.END_STONE_STAIRS);
    public static final RegistryObject<Item> END_STONE_SLAB = registerBlockItem("end_stone_slab",
            ModBlocks.END_STONE_SLAB);
    public static final RegistryObject<Item> END_STONE_WALL = registerBlockItem("end_stone_wall",
            ModBlocks.END_STONE_WALL);
    public static final RegistryObject<Item> STONE_WALL = registerBlockItem("stone_wall", ModBlocks.STONE_WALL);
    public static final RegistryObject<Item> QUARTZ_BRICKS_STAIRS = registerBlockItem("quartz_bricks_stairs",
            ModBlocks.QUARTZ_BRICKS_STAIRS);
    public static final RegistryObject<Item> QUARTZ_BRICKS_SLAB = registerBlockItem("quartz_bricks_slab",
            ModBlocks.QUARTZ_BRICKS_SLAB);
    public static final RegistryObject<Item> QUARTZ_BRICKS_WALL = registerBlockItem("quartz_bricks_wall",
            ModBlocks.QUARTZ_BRICKS_WALL);
    public static final RegistryObject<Item> QUARTZ_PILLAR_STAIRS = registerBlockItem("quartz_pillar_stairs",
            ModBlocks.QUARTZ_PILLAR_STAIRS);
    public static final RegistryObject<Item> QUARTZ_PILLAR_SLAB = registerBlockItem("quartz_pillar_slab",
            ModBlocks.QUARTZ_PILLAR_SLAB);
    public static final RegistryObject<Item> QUARTZ_PILLAR_WALL = registerBlockItem("quartz_pillar_wall",
            ModBlocks.QUARTZ_PILLAR_WALL);
    public static final RegistryObject<Item> CALCITE_STAIRS = registerBlockItem("calcite_stairs",
            ModBlocks.CALCITE_STAIRS);
    public static final RegistryObject<Item> CALCITE_SLAB = registerBlockItem("calcite_slab", ModBlocks.CALCITE_SLAB);
    public static final RegistryObject<Item> CALCITE_WALL = registerBlockItem("calcite_wall", ModBlocks.CALCITE_WALL);
    public static final RegistryObject<Item> BONE_BLOCK_STAIRS = registerBlockItem("bone_block_stairs",
            ModBlocks.BONE_BLOCK_STAIRS);
    public static final RegistryObject<Item> BONE_BLOCK_SLAB = registerBlockItem("bone_block_slab",
            ModBlocks.BONE_BLOCK_SLAB);
    public static final RegistryObject<Item> BONE_BLOCK_WALL = registerBlockItem("bone_block_wall",
            ModBlocks.BONE_BLOCK_WALL);
    public static final RegistryObject<Item> STACKED_BONE_BLOCK = registerBlockItem("stacked_bone_block",
            ModBlocks.STACKED_BONE_BLOCK);
    public static final RegistryObject<Item> STACKED_BONE_STAIRS = registerBlockItem("stacked_bone_stairs",
            ModBlocks.STACKED_BONE_STAIRS);
    public static final RegistryObject<Item> STACKED_BONE_SLAB = registerBlockItem("stacked_bone_slab",
            ModBlocks.STACKED_BONE_SLAB);
    public static final RegistryObject<Item> STACKED_BONE_WALL = registerBlockItem("stacked_bone_wall",
            ModBlocks.STACKED_BONE_WALL);
    public static final RegistryObject<Item> POLISHED_BONE_BLOCK = registerBlockItem("polished_bone_block",
            ModBlocks.POLISHED_BONE_BLOCK);
    public static final RegistryObject<Item> POLISHED_BONE_STAIRS = registerBlockItem("polished_bone_stairs",
            ModBlocks.POLISHED_BONE_STAIRS);
    public static final RegistryObject<Item> POLISHED_BONE_SLAB = registerBlockItem("polished_bone_slab",
            ModBlocks.POLISHED_BONE_SLAB);
    public static final RegistryObject<Item> POLISHED_BONE_WALL = registerBlockItem("polished_bone_wall",
            ModBlocks.POLISHED_BONE_WALL);
    public static final RegistryObject<Item> BONE_BRICKS = registerBlockItem("bone_bricks", ModBlocks.BONE_BRICKS);
    public static final RegistryObject<Item> BONE_BRICK_STAIRS = registerBlockItem("bone_brick_stairs",
            ModBlocks.BONE_BRICK_STAIRS);
    public static final RegistryObject<Item> BONE_BRICK_SLAB = registerBlockItem("bone_brick_slab",
            ModBlocks.BONE_BRICK_SLAB);
    public static final RegistryObject<Item> BONE_BRICK_WALL = registerBlockItem("bone_brick_wall",
            ModBlocks.BONE_BRICK_WALL);
    public static final RegistryObject<Item> CHISELED_BONE_BLOCK = registerBlockItem("chiseled_bone_block",
            ModBlocks.CHISELED_BONE_BLOCK);
    public static final RegistryObject<Item> BONE_PILLAR = registerBlockItem("bone_pillar", ModBlocks.BONE_PILLAR);
    public static final RegistryObject<Item> BONE_DICE = registerBlockItem("bone_dice", ModBlocks.BONE_DICE);
    public static final RegistryObject<Item> BEDROCK_SLAB = registerBlockItem("bedrock_slab", ModBlocks.BEDROCK_SLAB);
    public static final RegistryObject<Item> BEDROCK_STAIRS = registerBlockItem("bedrock_stairs",
            ModBlocks.BEDROCK_STAIRS);
    public static final RegistryObject<Item> BEDROCK_WALL = registerBlockItem("bedrock_wall", ModBlocks.BEDROCK_WALL);
    public static final RegistryObject<Item> BEDROCK_PANE = registerBlockItem("bedrock_pane", ModBlocks.BEDROCK_PANE);
    public static final RegistryObject<Item> OBSIDIAN_STAIRS = registerBlockItem("obsidian_stairs",
            ModBlocks.OBSIDIAN_STAIRS);
    public static final RegistryObject<Item> OBSIDIAN_SLAB = registerBlockItem("obsidian_slab",
            ModBlocks.OBSIDIAN_SLAB);
    public static final RegistryObject<Item> PRISMARINE_BRICKS_WALL = registerBlockItem("prismarine_bricks_wall",
            ModBlocks.PRISMARINE_BRICKS_WALL);
    public static final RegistryObject<Item> DARK_PRISMARINE_WALL = registerBlockItem("dark_prismarine_wall",
            ModBlocks.DARK_PRISMARINE_WALL);
    public static final RegistryObject<Item> QUARTZ_BLOCK_WALL = registerBlockItem("quartz_block_wall",
            ModBlocks.QUARTZ_BLOCK_WALL);
    public static final RegistryObject<Item> SMOOTH_QUARTZ_WALL = registerBlockItem("smooth_quartz_wall",
            ModBlocks.SMOOTH_QUARTZ_WALL);
    public static final RegistryObject<Item> SMOOTH_BASALT_STAIRS = registerBlockItem("smooth_basalt_stairs",
            ModBlocks.SMOOTH_BASALT_STAIRS);
    public static final RegistryObject<Item> SMOOTH_BASALT_SLAB = registerBlockItem("smooth_basalt_slab",
            ModBlocks.SMOOTH_BASALT_SLAB);
    public static final RegistryObject<Item> MOSS_BLOCK_SLAB = registerBlockItem("moss_block_slab",
            ModBlocks.MOSS_BLOCK_SLAB);
    public static final RegistryObject<Item> AMETHYST_BLOCK_SLAB = registerBlockItem("amethyst_block_slab",
            ModBlocks.AMETHYST_BLOCK_SLAB);
    public static final RegistryObject<Item> BIT_COPPER_BLOCK_STAIRS = registerBlockItem("bit_copper_block_stairs",
            ModBlocks.BIT_COPPER_BLOCK_STAIRS);
    public static final RegistryObject<Item> BIT_COPPER_BLOCK_SLAB = registerBlockItem("bit_copper_block_slab",
            ModBlocks.BIT_COPPER_BLOCK_SLAB);
    public static final RegistryObject<Item> BIT_COPPER_BLOCK_WALL = registerBlockItem("bit_copper_block_wall",
            ModBlocks.BIT_COPPER_BLOCK_WALL);
    public static final RegistryObject<Item> BIT_EXPOSED_COPPER_BLOCK_STAIRS = registerBlockItem(
            "bit_exposed_copper_block_stairs", ModBlocks.BIT_EXPOSED_COPPER_BLOCK_STAIRS);
    public static final RegistryObject<Item> BIT_EXPOSED_COPPER_BLOCK_SLAB = registerBlockItem(
            "bit_exposed_copper_block_slab", ModBlocks.BIT_EXPOSED_COPPER_BLOCK_SLAB);
    public static final RegistryObject<Item> BIT_EXPOSED_COPPER_BLOCK_WALL = registerBlockItem(
            "bit_exposed_copper_block_wall", ModBlocks.BIT_EXPOSED_COPPER_BLOCK_WALL);
    public static final RegistryObject<Item> BIT_WEATHERED_COPPER_BLOCK_STAIRS = registerBlockItem(
            "bit_weathered_copper_block_stairs", ModBlocks.BIT_WEATHERED_COPPER_BLOCK_STAIRS);
    public static final RegistryObject<Item> BIT_WEATHERED_COPPER_BLOCK_SLAB = registerBlockItem(
            "bit_weathered_copper_block_slab", ModBlocks.BIT_WEATHERED_COPPER_BLOCK_SLAB);
    public static final RegistryObject<Item> BIT_WEATHERED_COPPER_BLOCK_WALL = registerBlockItem(
            "bit_weathered_copper_block_wall", ModBlocks.BIT_WEATHERED_COPPER_BLOCK_WALL);
    public static final RegistryObject<Item> BIT_OXIDIZED_COPPER_BLOCK_STAIRS = registerBlockItem(
            "bit_oxidized_copper_block_stairs", ModBlocks.BIT_OXIDIZED_COPPER_BLOCK_STAIRS);
    public static final RegistryObject<Item> BIT_OXIDIZED_COPPER_BLOCK_SLAB = registerBlockItem(
            "bit_oxidized_copper_block_slab", ModBlocks.BIT_OXIDIZED_COPPER_BLOCK_SLAB);
    public static final RegistryObject<Item> BIT_OXIDIZED_COPPER_BLOCK_WALL = registerBlockItem(
            "bit_oxidized_copper_block_wall", ModBlocks.BIT_OXIDIZED_COPPER_BLOCK_WALL);
    public static final RegistryObject<Item> BIT_CUT_COPPER_STAIRS = registerBlockItem("bit_cut_copper_stairs",
            ModBlocks.BIT_CUT_COPPER_STAIRS);
    public static final RegistryObject<Item> BIT_CUT_COPPER_SLAB = registerBlockItem("bit_cut_copper_slab",
            ModBlocks.BIT_CUT_COPPER_SLAB);
    public static final RegistryObject<Item> BIT_CUT_COPPER_WALL = registerBlockItem("bit_cut_copper_wall",
            ModBlocks.BIT_CUT_COPPER_WALL);
    public static final RegistryObject<Item> BIT_EXPOSED_CUT_COPPER_STAIRS = registerBlockItem(
            "bit_exposed_cut_copper_stairs", ModBlocks.BIT_EXPOSED_CUT_COPPER_STAIRS);
    public static final RegistryObject<Item> BIT_EXPOSED_CUT_COPPER_SLAB = registerBlockItem(
            "bit_exposed_cut_copper_slab", ModBlocks.BIT_EXPOSED_CUT_COPPER_SLAB);
    public static final RegistryObject<Item> BIT_EXPOSED_CUT_COPPER_WALL = registerBlockItem(
            "bit_exposed_cut_copper_wall", ModBlocks.BIT_EXPOSED_CUT_COPPER_WALL);
    public static final RegistryObject<Item> BIT_WEATHERED_CUT_COPPER_STAIRS = registerBlockItem(
            "bit_weathered_cut_copper_stairs", ModBlocks.BIT_WEATHERED_CUT_COPPER_STAIRS);
    public static final RegistryObject<Item> BIT_WEATHERED_CUT_COPPER_SLAB = registerBlockItem(
            "bit_weathered_cut_copper_slab", ModBlocks.BIT_WEATHERED_CUT_COPPER_SLAB);
    public static final RegistryObject<Item> BIT_WEATHERED_CUT_COPPER_WALL = registerBlockItem(
            "bit_weathered_cut_copper_wall", ModBlocks.BIT_WEATHERED_CUT_COPPER_WALL);
    public static final RegistryObject<Item> BIT_OXIDIZED_CUT_COPPER_STAIRS = registerBlockItem(
            "bit_oxidized_cut_copper_stairs", ModBlocks.BIT_OXIDIZED_CUT_COPPER_STAIRS);
    public static final RegistryObject<Item> BIT_OXIDIZED_CUT_COPPER_SLAB = registerBlockItem(
            "bit_oxidized_cut_copper_slab", ModBlocks.BIT_OXIDIZED_CUT_COPPER_SLAB);
    public static final RegistryObject<Item> BIT_OXIDIZED_CUT_COPPER_WALL = registerBlockItem(
            "bit_oxidized_cut_copper_wall", ModBlocks.BIT_OXIDIZED_CUT_COPPER_WALL);
    public static final RegistryObject<Item> MOSSY_CALCITE = registerBlockItem("mossy_calcite",
            ModBlocks.MOSSY_CALCITE);
    public static final RegistryObject<Item> MOSSY_CALCITE_STAIRS = registerBlockItem("mossy_calcite_stairs",
            ModBlocks.MOSSY_CALCITE_STAIRS);
    public static final RegistryObject<Item> MOSSY_CALCITE_SLAB = registerBlockItem("mossy_calcite_slab",
            ModBlocks.MOSSY_CALCITE_SLAB);
    public static final RegistryObject<Item> MOSSY_CALCITE_WALL = registerBlockItem("mossy_calcite_wall",
            ModBlocks.MOSSY_CALCITE_WALL);
    public static final RegistryObject<Item> PODZOL_SLAB = registerBlockItem("podzol_slab", ModBlocks.PODZOL_SLAB);
    public static final RegistryObject<Item> DIRT_SLAB = registerBlockItem("dirt_slab", ModBlocks.DIRT_SLAB);
    public static final RegistryObject<Item> MUD = registerBlockItem("mud", ModBlocks.MUD);
    public static final RegistryObject<Item> MUD_STAIRS = registerBlockItem("mud_stairs", ModBlocks.MUD_STAIRS);
    public static final RegistryObject<Item> MUD_SLAB = registerBlockItem("mud_slab", ModBlocks.MUD_SLAB);
    public static final RegistryObject<Item> MYCELIUM_SLAB = registerBlockItem("mycelium_slab",
            ModBlocks.MYCELIUM_SLAB);
    public static final RegistryObject<Item> MOSS_LAYERS = registerBlockItem("moss_layers", ModBlocks.MOSS_LAYERS);
    public static final RegistryObject<Item> MOSS_OVERLAY = registerBlockItem("moss_overlay", ModBlocks.MOSS_OVERLAY);
    public static final RegistryObject<Item> SNOW_OVERLAY = registerBlockItem("snow_overlay", ModBlocks.SNOW_OVERLAY);
    public static final RegistryObject<Item> BLACK_CONCRETE_STAIRS = registerBlockItem("black_concrete_stairs",
            ModBlocks.BLACK_CONCRETE_STAIRS);
    public static final RegistryObject<Item> BLUE_CONCRETE_STAIRS = registerBlockItem("blue_concrete_stairs",
            ModBlocks.BLUE_CONCRETE_STAIRS);
    public static final RegistryObject<Item> BROWN_CONCRETE_STAIRS = registerBlockItem("brown_concrete_stairs",
            ModBlocks.BROWN_CONCRETE_STAIRS);
    public static final RegistryObject<Item> CYAN_CONCRETE_STAIRS = registerBlockItem("cyan_concrete_stairs",
            ModBlocks.CYAN_CONCRETE_STAIRS);
    public static final RegistryObject<Item> GRAY_CONCRETE_STAIRS = registerBlockItem("gray_concrete_stairs",
            ModBlocks.GRAY_CONCRETE_STAIRS);
    public static final RegistryObject<Item> GREEN_CONCRETE_STAIRS = registerBlockItem("green_concrete_stairs",
            ModBlocks.GREEN_CONCRETE_STAIRS);
    public static final RegistryObject<Item> LIGHT_BLUE_CONCRETE_STAIRS = registerBlockItem(
            "light_blue_concrete_stairs", ModBlocks.LIGHT_BLUE_CONCRETE_STAIRS);
    public static final RegistryObject<Item> LIGHT_GRAY_CONCRETE_STAIRS = registerBlockItem(
            "light_gray_concrete_stairs", ModBlocks.LIGHT_GRAY_CONCRETE_STAIRS);
    public static final RegistryObject<Item> LIME_CONCRETE_STAIRS = registerBlockItem("lime_concrete_stairs",
            ModBlocks.LIME_CONCRETE_STAIRS);
    public static final RegistryObject<Item> MAGENTA_CONCRETE_STAIRS = registerBlockItem("magenta_concrete_stairs",
            ModBlocks.MAGENTA_CONCRETE_STAIRS);
    public static final RegistryObject<Item> ORANGE_CONCRETE_STAIRS = registerBlockItem("orange_concrete_stairs",
            ModBlocks.ORANGE_CONCRETE_STAIRS);
    public static final RegistryObject<Item> PINK_CONCRETE_STAIRS = registerBlockItem("pink_concrete_stairs",
            ModBlocks.PINK_CONCRETE_STAIRS);
    public static final RegistryObject<Item> PURPLE_CONCRETE_STAIRS = registerBlockItem("purple_concrete_stairs",
            ModBlocks.PURPLE_CONCRETE_STAIRS);
    public static final RegistryObject<Item> RED_CONCRETE_STAIRS = registerBlockItem("red_concrete_stairs",
            ModBlocks.RED_CONCRETE_STAIRS);
    public static final RegistryObject<Item> WHITE_CONCRETE_STAIRS = registerBlockItem("white_concrete_stairs",
            ModBlocks.WHITE_CONCRETE_STAIRS);
    public static final RegistryObject<Item> YELLOW_CONCRETE_STAIRS = registerBlockItem("yellow_concrete_stairs",
            ModBlocks.YELLOW_CONCRETE_STAIRS);
    public static final RegistryObject<Item> WHITE_CONCRETE_WALL = registerBlockItem("white_concrete_wall",
            ModBlocks.WHITE_CONCRETE_WALL);
    public static final RegistryObject<Item> LIGHT_GRAY_CONCRETE_WALL = registerBlockItem("light_gray_concrete_wall",
            ModBlocks.LIGHT_GRAY_CONCRETE_WALL);
    public static final RegistryObject<Item> GRAY_CONCRETE_WALL = registerBlockItem("gray_concrete_wall",
            ModBlocks.GRAY_CONCRETE_WALL);
    public static final RegistryObject<Item> BLACK_CONCRETE_WALL = registerBlockItem("black_concrete_wall",
            ModBlocks.BLACK_CONCRETE_WALL);
    public static final RegistryObject<Item> BROWN_CONCRETE_WALL = registerBlockItem("brown_concrete_wall",
            ModBlocks.BROWN_CONCRETE_WALL);
    public static final RegistryObject<Item> RED_CONCRETE_WALL = registerBlockItem("red_concrete_wall",
            ModBlocks.RED_CONCRETE_WALL);
    public static final RegistryObject<Item> ORANGE_CONCRETE_WALL = registerBlockItem("orange_concrete_wall",
            ModBlocks.ORANGE_CONCRETE_WALL);
    public static final RegistryObject<Item> YELLOW_CONCRETE_WALL = registerBlockItem("yellow_concrete_wall",
            ModBlocks.YELLOW_CONCRETE_WALL);
    public static final RegistryObject<Item> LIME_CONCRETE_WALL = registerBlockItem("lime_concrete_wall",
            ModBlocks.LIME_CONCRETE_WALL);
    public static final RegistryObject<Item> GREEN_CONCRETE_WALL = registerBlockItem("green_concrete_wall",
            ModBlocks.GREEN_CONCRETE_WALL);
    public static final RegistryObject<Item> CYAN_CONCRETE_WALL = registerBlockItem("cyan_concrete_wall",
            ModBlocks.CYAN_CONCRETE_WALL);
    public static final RegistryObject<Item> LIGHT_BLUE_CONCRETE_WALL = registerBlockItem("light_blue_concrete_wall",
            ModBlocks.LIGHT_BLUE_CONCRETE_WALL);
    public static final RegistryObject<Item> BLUE_CONCRETE_WALL = registerBlockItem("blue_concrete_wall",
            ModBlocks.BLUE_CONCRETE_WALL);
    public static final RegistryObject<Item> PURPLE_CONCRETE_WALL = registerBlockItem("purple_concrete_wall",
            ModBlocks.PURPLE_CONCRETE_WALL);
    public static final RegistryObject<Item> MAGENTA_CONCRETE_WALL = registerBlockItem("magenta_concrete_wall",
            ModBlocks.MAGENTA_CONCRETE_WALL);
    public static final RegistryObject<Item> PINK_CONCRETE_WALL = registerBlockItem("pink_concrete_wall",
            ModBlocks.PINK_CONCRETE_WALL);
    public static final RegistryObject<Item> BLACK_CONCRETE_SLAB = registerBlockItem("black_concrete_slab",
            ModBlocks.BLACK_CONCRETE_SLAB);
    public static final RegistryObject<Item> BLUE_CONCRETE_SLAB = registerBlockItem("blue_concrete_slab",
            ModBlocks.BLUE_CONCRETE_SLAB);
    public static final RegistryObject<Item> BROWN_CONCRETE_SLAB = registerBlockItem("brown_concrete_slab",
            ModBlocks.BROWN_CONCRETE_SLAB);
    public static final RegistryObject<Item> CYAN_CONCRETE_SLAB = registerBlockItem("cyan_concrete_slab",
            ModBlocks.CYAN_CONCRETE_SLAB);
    public static final RegistryObject<Item> GRAY_CONCRETE_SLAB = registerBlockItem("gray_concrete_slab",
            ModBlocks.GRAY_CONCRETE_SLAB);
    public static final RegistryObject<Item> GREEN_CONCRETE_SLAB = registerBlockItem("green_concrete_slab",
            ModBlocks.GREEN_CONCRETE_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_CONCRETE_SLAB = registerBlockItem("light_blue_concrete_slab",
            ModBlocks.LIGHT_BLUE_CONCRETE_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_CONCRETE_SLAB = registerBlockItem("light_gray_concrete_slab",
            ModBlocks.LIGHT_GRAY_CONCRETE_SLAB);
    public static final RegistryObject<Item> LIME_CONCRETE_SLAB = registerBlockItem("lime_concrete_slab",
            ModBlocks.LIME_CONCRETE_SLAB);
    public static final RegistryObject<Item> MAGENTA_CONCRETE_SLAB = registerBlockItem("magenta_concrete_slab",
            ModBlocks.MAGENTA_CONCRETE_SLAB);
    public static final RegistryObject<Item> ORANGE_CONCRETE_SLAB = registerBlockItem("orange_concrete_slab",
            ModBlocks.ORANGE_CONCRETE_SLAB);
    public static final RegistryObject<Item> PINK_CONCRETE_SLAB = registerBlockItem("pink_concrete_slab",
            ModBlocks.PINK_CONCRETE_SLAB);
    public static final RegistryObject<Item> PURPLE_CONCRETE_SLAB = registerBlockItem("purple_concrete_slab",
            ModBlocks.PURPLE_CONCRETE_SLAB);
    public static final RegistryObject<Item> RED_CONCRETE_SLAB = registerBlockItem("red_concrete_slab",
            ModBlocks.RED_CONCRETE_SLAB);
    public static final RegistryObject<Item> WHITE_CONCRETE_SLAB = registerBlockItem("white_concrete_slab",
            ModBlocks.WHITE_CONCRETE_SLAB);
    public static final RegistryObject<Item> YELLOW_CONCRETE_SLAB = registerBlockItem("yellow_concrete_slab",
            ModBlocks.YELLOW_CONCRETE_SLAB);
    public static final RegistryObject<Item> BROWN_MUSHROOM_SHELVES = registerBlockItem("brown_mushroom_shelves",
            ModBlocks.BROWN_MUSHROOM_SHELVES);
    public static final RegistryObject<Item> RED_MUSHROOM_SHELVES = registerBlockItem("red_mushroom_shelves",
            ModBlocks.RED_MUSHROOM_SHELVES);
    public static final RegistryObject<Item> QUARTZ_PILLAR = registerBlockItem("quartz_pillar",
            ModBlocks.QUARTZ_PILLAR);
    public static final RegistryObject<Item> STONE_PILLAR = registerBlockItem("stone_pillar", ModBlocks.STONE_PILLAR);
    public static final RegistryObject<Item> DEEPSLATE_PILLAR = registerBlockItem("deepslate_pillar",
            ModBlocks.DEEPSLATE_PILLAR);
    public static final RegistryObject<Item> MOSSY_PILLAR = registerBlockItem("mossy_pillar", ModBlocks.MOSSY_PILLAR);
    public static final RegistryObject<Item> AMETHYST_PILLAR = registerBlockItem("amethyst_pillar",
            ModBlocks.AMETHYST_PILLAR);
    public static final RegistryObject<Item> ANDESITE_PILLAR = registerBlockItem("andesite_pillar",
            ModBlocks.ANDESITE_PILLAR);
    public static final RegistryObject<Item> BASALT_PILLAR = registerBlockItem("basalt_pillar",
            ModBlocks.BASALT_PILLAR);
    public static final RegistryObject<Item> BLACKSTONE_PILLAR = registerBlockItem("blackstone_pillar",
            ModBlocks.BLACKSTONE_PILLAR);
    public static final RegistryObject<Item> BLUE_ICE_PILLAR = registerBlockItem("blue_ice_pillar",
            ModBlocks.BLUE_ICE_PILLAR);
    public static final RegistryObject<Item> CALCITE_PILLAR = registerBlockItem("calcite_pillar",
            ModBlocks.CALCITE_PILLAR);
    public static final RegistryObject<Item> CINNABAR_PILLAR = registerBlockItem("cinnabar_pillar",
            ModBlocks.CINNABAR_PILLAR);
    public static final RegistryObject<Item> COPPER_PILLAR = registerBlockItem("copper_pillar",
            ModBlocks.COPPER_PILLAR);
    public static final RegistryObject<Item> DARK_PRISMARINE_PILLAR = registerBlockItem("dark_prismarine_pillar",
            ModBlocks.DARK_PRISMARINE_PILLAR);
    public static final RegistryObject<Item> DIORITE_PILLAR = registerBlockItem("diorite_pillar",
            ModBlocks.DIORITE_PILLAR);
    public static final RegistryObject<Item> DRIPSTONE_PILLAR = registerBlockItem("dripstone_pillar",
            ModBlocks.DRIPSTONE_PILLAR);
    public static final RegistryObject<Item> EXPOSED_COPPER_PILLAR = registerBlockItem("exposed_copper_pillar",
            ModBlocks.EXPOSED_COPPER_PILLAR);
    public static final RegistryObject<Item> GRANITE_PILLAR = registerBlockItem("granite_pillar",
            ModBlocks.GRANITE_PILLAR);
    public static final RegistryObject<Item> NETHERRACK_PILLAR = registerBlockItem("netherrack_pillar",
            ModBlocks.NETHERRACK_PILLAR);
    public static final RegistryObject<Item> OBSIDIAN_PILLAR = registerBlockItem("obsidian_pillar",
            ModBlocks.OBSIDIAN_PILLAR);
    public static final RegistryObject<Item> OXIDIZED_COPPER_PILLAR = registerBlockItem("oxidized_copper_pillar",
            ModBlocks.OXIDIZED_COPPER_PILLAR);
    public static final RegistryObject<Item> PACKED_MUD_PILLAR = registerBlockItem("packed_mud_pillar",
            ModBlocks.PACKED_MUD_PILLAR);
    public static final RegistryObject<Item> PRISMARINE_PILLAR = registerBlockItem("prismarine_pillar",
            ModBlocks.PRISMARINE_PILLAR);
    public static final RegistryObject<Item> SCULK_PILLAR = registerBlockItem("sculk_pillar", ModBlocks.SCULK_PILLAR);
    public static final RegistryObject<Item> SULFUR_PILLAR = registerBlockItem("sulfur_pillar",
            ModBlocks.SULFUR_PILLAR);
    public static final RegistryObject<Item> TUFF_PILLAR = registerBlockItem("tuff_pillar", ModBlocks.TUFF_PILLAR);
    public static final RegistryObject<Item> WEATHERED_COPPER_PILLAR = registerBlockItem("weathered_copper_pillar",
            ModBlocks.WEATHERED_COPPER_PILLAR);
    public static final RegistryObject<Item> ASHENKING_DIAMOND_PILLAR = registerBlockItem("ashenking_diamond_pillar",
            ModBlocks.ASHENKING_DIAMOND_PILLAR);
    public static final RegistryObject<Item> ASHENKING_GOLD_PILLAR = registerBlockItem("ashenking_gold_pillar",
            ModBlocks.ASHENKING_GOLD_PILLAR);
    public static final RegistryObject<Item> ASHENKING_EMERALD_PILLAR = registerBlockItem("ashenking_emerald_pillar",
            ModBlocks.ASHENKING_EMERALD_PILLAR);
    public static final RegistryObject<Item> ASHENKING_NETHERITE_PILLAR = registerBlockItem(
            "ashenking_netherite_pillar", ModBlocks.ASHENKING_NETHERITE_PILLAR);
    public static final RegistryObject<Item> DECORATED_POT = registerBlockItem("decorated_pot",
            ModBlocks.DECORATED_POT);
}
