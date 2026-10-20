package com.kingodogo.buildscape.registry.items;

import com.kingodogo.buildscape.registry.blocks.GlassBlocks;
import com.kingodogo.buildscape.registry.blocks.ShapedStoneBlocks;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import static com.kingodogo.buildscape.registry.items.ItemsRegister.registerBlockItem;

/**
 * The items GLASS_SLAB through STRIPPED_OAK_LOG_VERTICAL_SLAB of the registry, part 4 of 8, in registration order.
 * Named for its dominant families (glass, stone, wood); the neighbouring families that share a run of the order
 * with them are registered here too, because the registration order must not change.
 * Load order: {@link MasonryItems#init()} runs first (see the static block), so every holder registers in order whichever is touched first.
 */
public final class GlassItems {

    private GlassItems() {}

    /** Forces this class to load, which registers its items. */
    public static void init() {}

    static {
        MasonryItems.init();
    }

    public static final RegistryObject<Item> GLASS_SLAB = registerBlockItem("glass_slab", GlassBlocks.GLASS_SLAB);
    public static final RegistryObject<Item> WHITE_STAINED_GLASS_SLAB = registerBlockItem("white_stained_glass_slab",
            GlassBlocks.WHITE_STAINED_GLASS_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_GLASS_SLAB = registerBlockItem(
            "light_gray_stained_glass_slab", GlassBlocks.LIGHT_GRAY_STAINED_GLASS_SLAB);
    public static final RegistryObject<Item> GRAY_STAINED_GLASS_SLAB = registerBlockItem("gray_stained_glass_slab",
            GlassBlocks.GRAY_STAINED_GLASS_SLAB);
    public static final RegistryObject<Item> BLACK_STAINED_GLASS_SLAB = registerBlockItem("black_stained_glass_slab",
            GlassBlocks.BLACK_STAINED_GLASS_SLAB);
    public static final RegistryObject<Item> BROWN_STAINED_GLASS_SLAB = registerBlockItem("brown_stained_glass_slab",
            GlassBlocks.BROWN_STAINED_GLASS_SLAB);
    public static final RegistryObject<Item> RED_STAINED_GLASS_SLAB = registerBlockItem("red_stained_glass_slab",
            GlassBlocks.RED_STAINED_GLASS_SLAB);
    public static final RegistryObject<Item> ORANGE_STAINED_GLASS_SLAB = registerBlockItem("orange_stained_glass_slab",
            GlassBlocks.ORANGE_STAINED_GLASS_SLAB);
    public static final RegistryObject<Item> YELLOW_STAINED_GLASS_SLAB = registerBlockItem("yellow_stained_glass_slab",
            GlassBlocks.YELLOW_STAINED_GLASS_SLAB);
    public static final RegistryObject<Item> LIME_STAINED_GLASS_SLAB = registerBlockItem("lime_stained_glass_slab",
            GlassBlocks.LIME_STAINED_GLASS_SLAB);
    public static final RegistryObject<Item> GREEN_STAINED_GLASS_SLAB = registerBlockItem("green_stained_glass_slab",
            GlassBlocks.GREEN_STAINED_GLASS_SLAB);
    public static final RegistryObject<Item> CYAN_STAINED_GLASS_SLAB = registerBlockItem("cyan_stained_glass_slab",
            GlassBlocks.CYAN_STAINED_GLASS_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_GLASS_SLAB = registerBlockItem(
            "light_blue_stained_glass_slab", GlassBlocks.LIGHT_BLUE_STAINED_GLASS_SLAB);
    public static final RegistryObject<Item> BLUE_STAINED_GLASS_SLAB = registerBlockItem("blue_stained_glass_slab",
            GlassBlocks.BLUE_STAINED_GLASS_SLAB);
    public static final RegistryObject<Item> PURPLE_STAINED_GLASS_SLAB = registerBlockItem("purple_stained_glass_slab",
            GlassBlocks.PURPLE_STAINED_GLASS_SLAB);
    public static final RegistryObject<Item> MAGENTA_STAINED_GLASS_SLAB = registerBlockItem(
            "magenta_stained_glass_slab", GlassBlocks.MAGENTA_STAINED_GLASS_SLAB);
    public static final RegistryObject<Item> PINK_STAINED_GLASS_SLAB = registerBlockItem("pink_stained_glass_slab",
            GlassBlocks.PINK_STAINED_GLASS_SLAB);
    public static final RegistryObject<Item> GLASS_STAIRS = registerBlockItem("glass_stairs", GlassBlocks.GLASS_STAIRS);
    public static final RegistryObject<Item> WHITE_STAINED_GLASS_STAIRS = registerBlockItem(
            "white_stained_glass_stairs", GlassBlocks.WHITE_STAINED_GLASS_STAIRS);
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_GLASS_STAIRS = registerBlockItem(
            "light_gray_stained_glass_stairs", GlassBlocks.LIGHT_GRAY_STAINED_GLASS_STAIRS);
    public static final RegistryObject<Item> GRAY_STAINED_GLASS_STAIRS = registerBlockItem("gray_stained_glass_stairs",
            GlassBlocks.GRAY_STAINED_GLASS_STAIRS);
    public static final RegistryObject<Item> BLACK_STAINED_GLASS_STAIRS = registerBlockItem(
            "black_stained_glass_stairs", GlassBlocks.BLACK_STAINED_GLASS_STAIRS);
    public static final RegistryObject<Item> BROWN_STAINED_GLASS_STAIRS = registerBlockItem(
            "brown_stained_glass_stairs", GlassBlocks.BROWN_STAINED_GLASS_STAIRS);
    public static final RegistryObject<Item> RED_STAINED_GLASS_STAIRS = registerBlockItem("red_stained_glass_stairs",
            GlassBlocks.RED_STAINED_GLASS_STAIRS);
    public static final RegistryObject<Item> ORANGE_STAINED_GLASS_STAIRS = registerBlockItem(
            "orange_stained_glass_stairs", GlassBlocks.ORANGE_STAINED_GLASS_STAIRS);
    public static final RegistryObject<Item> YELLOW_STAINED_GLASS_STAIRS = registerBlockItem(
            "yellow_stained_glass_stairs", GlassBlocks.YELLOW_STAINED_GLASS_STAIRS);
    public static final RegistryObject<Item> LIME_STAINED_GLASS_STAIRS = registerBlockItem("lime_stained_glass_stairs",
            GlassBlocks.LIME_STAINED_GLASS_STAIRS);
    public static final RegistryObject<Item> GREEN_STAINED_GLASS_STAIRS = registerBlockItem(
            "green_stained_glass_stairs", GlassBlocks.GREEN_STAINED_GLASS_STAIRS);
    public static final RegistryObject<Item> CYAN_STAINED_GLASS_STAIRS = registerBlockItem("cyan_stained_glass_stairs",
            GlassBlocks.CYAN_STAINED_GLASS_STAIRS);
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_GLASS_STAIRS = registerBlockItem(
            "light_blue_stained_glass_stairs", GlassBlocks.LIGHT_BLUE_STAINED_GLASS_STAIRS);
    public static final RegistryObject<Item> BLUE_STAINED_GLASS_STAIRS = registerBlockItem("blue_stained_glass_stairs",
            GlassBlocks.BLUE_STAINED_GLASS_STAIRS);
    public static final RegistryObject<Item> PURPLE_STAINED_GLASS_STAIRS = registerBlockItem(
            "purple_stained_glass_stairs", GlassBlocks.PURPLE_STAINED_GLASS_STAIRS);
    public static final RegistryObject<Item> MAGENTA_STAINED_GLASS_STAIRS = registerBlockItem(
            "magenta_stained_glass_stairs", GlassBlocks.MAGENTA_STAINED_GLASS_STAIRS);
    public static final RegistryObject<Item> PINK_STAINED_GLASS_STAIRS = registerBlockItem("pink_stained_glass_stairs",
            GlassBlocks.PINK_STAINED_GLASS_STAIRS);
    public static final RegistryObject<Item> WHITE_MOSAIC_GLASS_SLAB = registerBlockItem("white_mosaic_glass_slab",
            GlassBlocks.WHITE_MOSAIC_GLASS_SLAB);
    public static final RegistryObject<Item> WHITE_MOSAIC_GLASS_STAIRS = registerBlockItem("white_mosaic_glass_stairs",
            GlassBlocks.WHITE_MOSAIC_GLASS_STAIRS);
    public static final RegistryObject<Item> LIGHT_GRAY_MOSAIC_GLASS_SLAB = registerBlockItem(
            "light_gray_mosaic_glass_slab", GlassBlocks.LIGHT_GRAY_MOSAIC_GLASS_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_MOSAIC_GLASS_STAIRS = registerBlockItem(
            "light_gray_mosaic_glass_stairs", GlassBlocks.LIGHT_GRAY_MOSAIC_GLASS_STAIRS);
    public static final RegistryObject<Item> GRAY_MOSAIC_GLASS_SLAB = registerBlockItem("gray_mosaic_glass_slab",
            GlassBlocks.GRAY_MOSAIC_GLASS_SLAB);
    public static final RegistryObject<Item> GRAY_MOSAIC_GLASS_STAIRS = registerBlockItem("gray_mosaic_glass_stairs",
            GlassBlocks.GRAY_MOSAIC_GLASS_STAIRS);
    public static final RegistryObject<Item> BLACK_MOSAIC_GLASS_SLAB = registerBlockItem("black_mosaic_glass_slab",
            GlassBlocks.BLACK_MOSAIC_GLASS_SLAB);
    public static final RegistryObject<Item> BLACK_MOSAIC_GLASS_STAIRS = registerBlockItem("black_mosaic_glass_stairs",
            GlassBlocks.BLACK_MOSAIC_GLASS_STAIRS);
    public static final RegistryObject<Item> BROWN_MOSAIC_GLASS_SLAB = registerBlockItem("brown_mosaic_glass_slab",
            GlassBlocks.BROWN_MOSAIC_GLASS_SLAB);
    public static final RegistryObject<Item> BROWN_MOSAIC_GLASS_STAIRS = registerBlockItem("brown_mosaic_glass_stairs",
            GlassBlocks.BROWN_MOSAIC_GLASS_STAIRS);
    public static final RegistryObject<Item> RED_MOSAIC_GLASS_SLAB = registerBlockItem("red_mosaic_glass_slab",
            GlassBlocks.RED_MOSAIC_GLASS_SLAB);
    public static final RegistryObject<Item> RED_MOSAIC_GLASS_STAIRS = registerBlockItem("red_mosaic_glass_stairs",
            GlassBlocks.RED_MOSAIC_GLASS_STAIRS);
    public static final RegistryObject<Item> ORANGE_MOSAIC_GLASS_SLAB = registerBlockItem("orange_mosaic_glass_slab",
            GlassBlocks.ORANGE_MOSAIC_GLASS_SLAB);
    public static final RegistryObject<Item> ORANGE_MOSAIC_GLASS_STAIRS = registerBlockItem(
            "orange_mosaic_glass_stairs", GlassBlocks.ORANGE_MOSAIC_GLASS_STAIRS);
    public static final RegistryObject<Item> YELLOW_MOSAIC_GLASS_SLAB = registerBlockItem("yellow_mosaic_glass_slab",
            GlassBlocks.YELLOW_MOSAIC_GLASS_SLAB);
    public static final RegistryObject<Item> YELLOW_MOSAIC_GLASS_STAIRS = registerBlockItem(
            "yellow_mosaic_glass_stairs", GlassBlocks.YELLOW_MOSAIC_GLASS_STAIRS);
    public static final RegistryObject<Item> LIME_MOSAIC_GLASS_SLAB = registerBlockItem("lime_mosaic_glass_slab",
            GlassBlocks.LIME_MOSAIC_GLASS_SLAB);
    public static final RegistryObject<Item> LIME_MOSAIC_GLASS_STAIRS = registerBlockItem("lime_mosaic_glass_stairs",
            GlassBlocks.LIME_MOSAIC_GLASS_STAIRS);
    public static final RegistryObject<Item> GREEN_MOSAIC_GLASS_SLAB = registerBlockItem("green_mosaic_glass_slab",
            GlassBlocks.GREEN_MOSAIC_GLASS_SLAB);
    public static final RegistryObject<Item> GREEN_MOSAIC_GLASS_STAIRS = registerBlockItem("green_mosaic_glass_stairs",
            GlassBlocks.GREEN_MOSAIC_GLASS_STAIRS);
    public static final RegistryObject<Item> CYAN_MOSAIC_GLASS_SLAB = registerBlockItem("cyan_mosaic_glass_slab",
            GlassBlocks.CYAN_MOSAIC_GLASS_SLAB);
    public static final RegistryObject<Item> CYAN_MOSAIC_GLASS_STAIRS = registerBlockItem("cyan_mosaic_glass_stairs",
            GlassBlocks.CYAN_MOSAIC_GLASS_STAIRS);
    public static final RegistryObject<Item> LIGHT_BLUE_MOSAIC_GLASS_SLAB = registerBlockItem(
            "light_blue_mosaic_glass_slab", GlassBlocks.LIGHT_BLUE_MOSAIC_GLASS_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_MOSAIC_GLASS_STAIRS = registerBlockItem(
            "light_blue_mosaic_glass_stairs", GlassBlocks.LIGHT_BLUE_MOSAIC_GLASS_STAIRS);
    public static final RegistryObject<Item> BLUE_MOSAIC_GLASS_SLAB = registerBlockItem("blue_mosaic_glass_slab",
            GlassBlocks.BLUE_MOSAIC_GLASS_SLAB);
    public static final RegistryObject<Item> BLUE_MOSAIC_GLASS_STAIRS = registerBlockItem("blue_mosaic_glass_stairs",
            GlassBlocks.BLUE_MOSAIC_GLASS_STAIRS);
    public static final RegistryObject<Item> PURPLE_MOSAIC_GLASS_SLAB = registerBlockItem("purple_mosaic_glass_slab",
            GlassBlocks.PURPLE_MOSAIC_GLASS_SLAB);
    public static final RegistryObject<Item> PURPLE_MOSAIC_GLASS_STAIRS = registerBlockItem(
            "purple_mosaic_glass_stairs", GlassBlocks.PURPLE_MOSAIC_GLASS_STAIRS);
    public static final RegistryObject<Item> MAGENTA_MOSAIC_GLASS_SLAB = registerBlockItem("magenta_mosaic_glass_slab",
            GlassBlocks.MAGENTA_MOSAIC_GLASS_SLAB);
    public static final RegistryObject<Item> MAGENTA_MOSAIC_GLASS_STAIRS = registerBlockItem(
            "magenta_mosaic_glass_stairs", GlassBlocks.MAGENTA_MOSAIC_GLASS_STAIRS);
    public static final RegistryObject<Item> PINK_MOSAIC_GLASS_SLAB = registerBlockItem("pink_mosaic_glass_slab",
            GlassBlocks.PINK_MOSAIC_GLASS_SLAB);
    public static final RegistryObject<Item> PINK_MOSAIC_GLASS_STAIRS = registerBlockItem("pink_mosaic_glass_stairs",
            GlassBlocks.PINK_MOSAIC_GLASS_STAIRS);
    public static final RegistryObject<Item> FACTORY_WHITE_GLASS_SLAB = registerBlockItem("factory_white_glass_slab",
            GlassBlocks.FACTORY_WHITE_GLASS_SLAB);
    public static final RegistryObject<Item> FACTORY_WHITE_GLASS_STAIRS = registerBlockItem(
            "factory_white_glass_stairs", GlassBlocks.FACTORY_WHITE_GLASS_STAIRS);
    public static final RegistryObject<Item> FACTORY_LIGHT_GRAY_GLASS_SLAB = registerBlockItem(
            "factory_light_gray_glass_slab", GlassBlocks.FACTORY_LIGHT_GRAY_GLASS_SLAB);
    public static final RegistryObject<Item> FACTORY_LIGHT_GRAY_GLASS_STAIRS = registerBlockItem(
            "factory_light_gray_glass_stairs", GlassBlocks.FACTORY_LIGHT_GRAY_GLASS_STAIRS);
    public static final RegistryObject<Item> FACTORY_GRAY_GLASS_SLAB = registerBlockItem("factory_gray_glass_slab",
            GlassBlocks.FACTORY_GRAY_GLASS_SLAB);
    public static final RegistryObject<Item> FACTORY_GRAY_GLASS_STAIRS = registerBlockItem("factory_gray_glass_stairs",
            GlassBlocks.FACTORY_GRAY_GLASS_STAIRS);
    public static final RegistryObject<Item> FACTORY_BLACK_GLASS_SLAB = registerBlockItem("factory_black_glass_slab",
            GlassBlocks.FACTORY_BLACK_GLASS_SLAB);
    public static final RegistryObject<Item> FACTORY_BLACK_GLASS_STAIRS = registerBlockItem(
            "factory_black_glass_stairs", GlassBlocks.FACTORY_BLACK_GLASS_STAIRS);
    public static final RegistryObject<Item> FACTORY_BROWN_GLASS_SLAB = registerBlockItem("factory_brown_glass_slab",
            GlassBlocks.FACTORY_BROWN_GLASS_SLAB);
    public static final RegistryObject<Item> FACTORY_BROWN_GLASS_STAIRS = registerBlockItem(
            "factory_brown_glass_stairs", GlassBlocks.FACTORY_BROWN_GLASS_STAIRS);
    public static final RegistryObject<Item> FACTORY_RED_GLASS_SLAB = registerBlockItem("factory_red_glass_slab",
            GlassBlocks.FACTORY_RED_GLASS_SLAB);
    public static final RegistryObject<Item> FACTORY_RED_GLASS_STAIRS = registerBlockItem("factory_red_glass_stairs",
            GlassBlocks.FACTORY_RED_GLASS_STAIRS);
    public static final RegistryObject<Item> FACTORY_ORANGE_GLASS_SLAB = registerBlockItem("factory_orange_glass_slab",
            GlassBlocks.FACTORY_ORANGE_GLASS_SLAB);
    public static final RegistryObject<Item> FACTORY_ORANGE_GLASS_STAIRS = registerBlockItem(
            "factory_orange_glass_stairs", GlassBlocks.FACTORY_ORANGE_GLASS_STAIRS);
    public static final RegistryObject<Item> FACTORY_YELLOW_GLASS_SLAB = registerBlockItem("factory_yellow_glass_slab",
            GlassBlocks.FACTORY_YELLOW_GLASS_SLAB);
    public static final RegistryObject<Item> FACTORY_YELLOW_GLASS_STAIRS = registerBlockItem(
            "factory_yellow_glass_stairs", GlassBlocks.FACTORY_YELLOW_GLASS_STAIRS);
    public static final RegistryObject<Item> FACTORY_LIME_GLASS_SLAB = registerBlockItem("factory_lime_glass_slab",
            GlassBlocks.FACTORY_LIME_GLASS_SLAB);
    public static final RegistryObject<Item> FACTORY_LIME_GLASS_STAIRS = registerBlockItem("factory_lime_glass_stairs",
            GlassBlocks.FACTORY_LIME_GLASS_STAIRS);
    public static final RegistryObject<Item> FACTORY_GREEN_GLASS_SLAB = registerBlockItem("factory_green_glass_slab",
            GlassBlocks.FACTORY_GREEN_GLASS_SLAB);
    public static final RegistryObject<Item> FACTORY_GREEN_GLASS_STAIRS = registerBlockItem(
            "factory_green_glass_stairs", GlassBlocks.FACTORY_GREEN_GLASS_STAIRS);
    public static final RegistryObject<Item> FACTORY_CYAN_GLASS_SLAB = registerBlockItem("factory_cyan_glass_slab",
            GlassBlocks.FACTORY_CYAN_GLASS_SLAB);
    public static final RegistryObject<Item> FACTORY_CYAN_GLASS_STAIRS = registerBlockItem("factory_cyan_glass_stairs",
            GlassBlocks.FACTORY_CYAN_GLASS_STAIRS);
    public static final RegistryObject<Item> FACTORY_LIGHT_BLUE_GLASS_SLAB = registerBlockItem(
            "factory_light_blue_glass_slab", GlassBlocks.FACTORY_LIGHT_BLUE_GLASS_SLAB);
    public static final RegistryObject<Item> FACTORY_LIGHT_BLUE_GLASS_STAIRS = registerBlockItem(
            "factory_light_blue_glass_stairs", GlassBlocks.FACTORY_LIGHT_BLUE_GLASS_STAIRS);
    public static final RegistryObject<Item> FACTORY_BLUE_GLASS_SLAB = registerBlockItem("factory_blue_glass_slab",
            GlassBlocks.FACTORY_BLUE_GLASS_SLAB);
    public static final RegistryObject<Item> FACTORY_BLUE_GLASS_STAIRS = registerBlockItem("factory_blue_glass_stairs",
            GlassBlocks.FACTORY_BLUE_GLASS_STAIRS);
    public static final RegistryObject<Item> FACTORY_PURPLE_GLASS_SLAB = registerBlockItem("factory_purple_glass_slab",
            GlassBlocks.FACTORY_PURPLE_GLASS_SLAB);
    public static final RegistryObject<Item> FACTORY_PURPLE_GLASS_STAIRS = registerBlockItem(
            "factory_purple_glass_stairs", GlassBlocks.FACTORY_PURPLE_GLASS_STAIRS);
    public static final RegistryObject<Item> FACTORY_MAGENTA_GLASS_SLAB = registerBlockItem(
            "factory_magenta_glass_slab", GlassBlocks.FACTORY_MAGENTA_GLASS_SLAB);
    public static final RegistryObject<Item> FACTORY_MAGENTA_GLASS_STAIRS = registerBlockItem(
            "factory_magenta_glass_stairs", GlassBlocks.FACTORY_MAGENTA_GLASS_STAIRS);
    public static final RegistryObject<Item> FACTORY_PINK_GLASS_SLAB = registerBlockItem("factory_pink_glass_slab",
            GlassBlocks.FACTORY_PINK_GLASS_SLAB);
    public static final RegistryObject<Item> FACTORY_PINK_GLASS_STAIRS = registerBlockItem("factory_pink_glass_stairs",
            GlassBlocks.FACTORY_PINK_GLASS_STAIRS);
    public static final RegistryObject<Item> WHITE_GLAZED_GLASS_SLAB = registerBlockItem("white_glazed_glass_slab",
            GlassBlocks.WHITE_GLAZED_GLASS_SLAB);
    public static final RegistryObject<Item> WHITE_GLAZED_GLASS_STAIRS = registerBlockItem("white_glazed_glass_stairs",
            GlassBlocks.WHITE_GLAZED_GLASS_STAIRS);
    public static final RegistryObject<Item> LIGHT_GRAY_GLAZED_GLASS_SLAB = registerBlockItem(
            "light_gray_glazed_glass_slab", GlassBlocks.LIGHT_GRAY_GLAZED_GLASS_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_GLAZED_GLASS_STAIRS = registerBlockItem(
            "light_gray_glazed_glass_stairs", GlassBlocks.LIGHT_GRAY_GLAZED_GLASS_STAIRS);
    public static final RegistryObject<Item> GRAY_GLAZED_GLASS_SLAB = registerBlockItem("gray_glazed_glass_slab",
            GlassBlocks.GRAY_GLAZED_GLASS_SLAB);
    public static final RegistryObject<Item> GRAY_GLAZED_GLASS_STAIRS = registerBlockItem("gray_glazed_glass_stairs",
            GlassBlocks.GRAY_GLAZED_GLASS_STAIRS);
    public static final RegistryObject<Item> BLACK_GLAZED_GLASS_SLAB = registerBlockItem("black_glazed_glass_slab",
            GlassBlocks.BLACK_GLAZED_GLASS_SLAB);
    public static final RegistryObject<Item> BLACK_GLAZED_GLASS_STAIRS = registerBlockItem("black_glazed_glass_stairs",
            GlassBlocks.BLACK_GLAZED_GLASS_STAIRS);
    public static final RegistryObject<Item> BROWN_GLAZED_GLASS_SLAB = registerBlockItem("brown_glazed_glass_slab",
            GlassBlocks.BROWN_GLAZED_GLASS_SLAB);
    public static final RegistryObject<Item> BROWN_GLAZED_GLASS_STAIRS = registerBlockItem("brown_glazed_glass_stairs",
            GlassBlocks.BROWN_GLAZED_GLASS_STAIRS);
    public static final RegistryObject<Item> RED_GLAZED_GLASS_SLAB = registerBlockItem("red_glazed_glass_slab",
            GlassBlocks.RED_GLAZED_GLASS_SLAB);
    public static final RegistryObject<Item> RED_GLAZED_GLASS_STAIRS = registerBlockItem("red_glazed_glass_stairs",
            GlassBlocks.RED_GLAZED_GLASS_STAIRS);
    public static final RegistryObject<Item> ORANGE_GLAZED_GLASS_SLAB = registerBlockItem("orange_glazed_glass_slab",
            GlassBlocks.ORANGE_GLAZED_GLASS_SLAB);
    public static final RegistryObject<Item> ORANGE_GLAZED_GLASS_STAIRS = registerBlockItem(
            "orange_glazed_glass_stairs", GlassBlocks.ORANGE_GLAZED_GLASS_STAIRS);
    public static final RegistryObject<Item> YELLOW_GLAZED_GLASS_SLAB = registerBlockItem("yellow_glazed_glass_slab",
            GlassBlocks.YELLOW_GLAZED_GLASS_SLAB);
    public static final RegistryObject<Item> YELLOW_GLAZED_GLASS_STAIRS = registerBlockItem(
            "yellow_glazed_glass_stairs", GlassBlocks.YELLOW_GLAZED_GLASS_STAIRS);
    public static final RegistryObject<Item> LIME_GLAZED_GLASS_SLAB = registerBlockItem("lime_glazed_glass_slab",
            GlassBlocks.LIME_GLAZED_GLASS_SLAB);
    public static final RegistryObject<Item> LIME_GLAZED_GLASS_STAIRS = registerBlockItem("lime_glazed_glass_stairs",
            GlassBlocks.LIME_GLAZED_GLASS_STAIRS);
    public static final RegistryObject<Item> GREEN_GLAZED_GLASS_SLAB = registerBlockItem("green_glazed_glass_slab",
            GlassBlocks.GREEN_GLAZED_GLASS_SLAB);
    public static final RegistryObject<Item> GREEN_GLAZED_GLASS_STAIRS = registerBlockItem("green_glazed_glass_stairs",
            GlassBlocks.GREEN_GLAZED_GLASS_STAIRS);
    public static final RegistryObject<Item> CYAN_GLAZED_GLASS_SLAB = registerBlockItem("cyan_glazed_glass_slab",
            GlassBlocks.CYAN_GLAZED_GLASS_SLAB);
    public static final RegistryObject<Item> CYAN_GLAZED_GLASS_STAIRS = registerBlockItem("cyan_glazed_glass_stairs",
            GlassBlocks.CYAN_GLAZED_GLASS_STAIRS);
    public static final RegistryObject<Item> LIGHT_BLUE_GLAZED_GLASS_SLAB = registerBlockItem(
            "light_blue_glazed_glass_slab", GlassBlocks.LIGHT_BLUE_GLAZED_GLASS_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_GLAZED_GLASS_STAIRS = registerBlockItem(
            "light_blue_glazed_glass_stairs", GlassBlocks.LIGHT_BLUE_GLAZED_GLASS_STAIRS);
    public static final RegistryObject<Item> BLUE_GLAZED_GLASS_SLAB = registerBlockItem("blue_glazed_glass_slab",
            GlassBlocks.BLUE_GLAZED_GLASS_SLAB);
    public static final RegistryObject<Item> BLUE_GLAZED_GLASS_STAIRS = registerBlockItem("blue_glazed_glass_stairs",
            GlassBlocks.BLUE_GLAZED_GLASS_STAIRS);
    public static final RegistryObject<Item> PURPLE_GLAZED_GLASS_SLAB = registerBlockItem("purple_glazed_glass_slab",
            GlassBlocks.PURPLE_GLAZED_GLASS_SLAB);
    public static final RegistryObject<Item> PURPLE_GLAZED_GLASS_STAIRS = registerBlockItem(
            "purple_glazed_glass_stairs", GlassBlocks.PURPLE_GLAZED_GLASS_STAIRS);
    public static final RegistryObject<Item> MAGENTA_GLAZED_GLASS_SLAB = registerBlockItem("magenta_glazed_glass_slab",
            GlassBlocks.MAGENTA_GLAZED_GLASS_SLAB);
    public static final RegistryObject<Item> MAGENTA_GLAZED_GLASS_STAIRS = registerBlockItem(
            "magenta_glazed_glass_stairs", GlassBlocks.MAGENTA_GLAZED_GLASS_STAIRS);
    public static final RegistryObject<Item> PINK_GLAZED_GLASS_SLAB = registerBlockItem("pink_glazed_glass_slab",
            GlassBlocks.PINK_GLAZED_GLASS_SLAB);
    public static final RegistryObject<Item> PINK_GLAZED_GLASS_STAIRS = registerBlockItem("pink_glazed_glass_stairs",
            GlassBlocks.PINK_GLAZED_GLASS_STAIRS);
    public static final RegistryObject<Item> OAK_LOG_SLAB = registerBlockItem("oak_log_slab",
            ShapedStoneBlocks.OAK_LOG_SLAB);
    public static final RegistryObject<Item> STRIPPED_OAK_LOG_SLAB = registerBlockItem("stripped_oak_log_slab",
            ShapedStoneBlocks.STRIPPED_OAK_LOG_SLAB);
    public static final RegistryObject<Item> SPRUCE_LOG_SLAB = registerBlockItem("spruce_log_slab",
            ShapedStoneBlocks.SPRUCE_LOG_SLAB);
    public static final RegistryObject<Item> STRIPPED_SPRUCE_LOG_SLAB = registerBlockItem("stripped_spruce_log_slab",
            ShapedStoneBlocks.STRIPPED_SPRUCE_LOG_SLAB);
    public static final RegistryObject<Item> BIRCH_LOG_SLAB = registerBlockItem("birch_log_slab",
            ShapedStoneBlocks.BIRCH_LOG_SLAB);
    public static final RegistryObject<Item> STRIPPED_BIRCH_LOG_SLAB = registerBlockItem("stripped_birch_log_slab",
            ShapedStoneBlocks.STRIPPED_BIRCH_LOG_SLAB);
    public static final RegistryObject<Item> JUNGLE_LOG_SLAB = registerBlockItem("jungle_log_slab",
            ShapedStoneBlocks.JUNGLE_LOG_SLAB);
    public static final RegistryObject<Item> STRIPPED_JUNGLE_LOG_SLAB = registerBlockItem("stripped_jungle_log_slab",
            ShapedStoneBlocks.STRIPPED_JUNGLE_LOG_SLAB);
    public static final RegistryObject<Item> ACACIA_LOG_SLAB = registerBlockItem("acacia_log_slab",
            ShapedStoneBlocks.ACACIA_LOG_SLAB);
    public static final RegistryObject<Item> STRIPPED_ACACIA_LOG_SLAB = registerBlockItem("stripped_acacia_log_slab",
            ShapedStoneBlocks.STRIPPED_ACACIA_LOG_SLAB);
    public static final RegistryObject<Item> DARK_OAK_LOG_SLAB = registerBlockItem("dark_oak_log_slab",
            ShapedStoneBlocks.DARK_OAK_LOG_SLAB);
    public static final RegistryObject<Item> STRIPPED_DARK_OAK_LOG_SLAB = registerBlockItem(
            "stripped_dark_oak_log_slab", ShapedStoneBlocks.STRIPPED_DARK_OAK_LOG_SLAB);
    public static final RegistryObject<Item> CRIMSON_STEM_SLAB = registerBlockItem("crimson_stem_slab",
            ShapedStoneBlocks.CRIMSON_STEM_SLAB);
    public static final RegistryObject<Item> STRIPPED_CRIMSON_STEM_SLAB = registerBlockItem(
            "stripped_crimson_stem_slab", ShapedStoneBlocks.STRIPPED_CRIMSON_STEM_SLAB);
    public static final RegistryObject<Item> WARPED_STEM_SLAB = registerBlockItem("warped_stem_slab",
            ShapedStoneBlocks.WARPED_STEM_SLAB);
    public static final RegistryObject<Item> STRIPPED_WARPED_STEM_SLAB = registerBlockItem("stripped_warped_stem_slab",
            ShapedStoneBlocks.STRIPPED_WARPED_STEM_SLAB);
    public static final RegistryObject<Item> MANGROVE_LOG_SLAB = registerBlockItem("mangrove_log_slab",
            ShapedStoneBlocks.MANGROVE_LOG_SLAB);
    public static final RegistryObject<Item> MANGROVE_LOG_STAIRS = registerBlockItem("mangrove_log_stairs",
            ShapedStoneBlocks.MANGROVE_LOG_STAIRS);
    public static final RegistryObject<Item> STRIPPED_MANGROVE_LOG_SLAB = registerBlockItem(
            "stripped_mangrove_log_slab", ShapedStoneBlocks.STRIPPED_MANGROVE_LOG_SLAB);
    public static final RegistryObject<Item> STRIPPED_MANGROVE_LOG_STAIRS = registerBlockItem(
            "stripped_mangrove_log_stairs", ShapedStoneBlocks.STRIPPED_MANGROVE_LOG_STAIRS);
    public static final RegistryObject<Item> ASHPEN_LOG = registerBlockItem("ashpen_log", ShapedStoneBlocks.ASHPEN_LOG);
    public static final RegistryObject<Item> ASHPEN_WOOD = registerBlockItem("ashpen_wood",
            ShapedStoneBlocks.ASHPEN_WOOD);
    public static final RegistryObject<Item> ASHPEN_LOG_SLAB = registerBlockItem("ashpen_log_slab",
            ShapedStoneBlocks.ASHPEN_LOG_SLAB);
    public static final RegistryObject<Item> ASHPEN_LOG_STAIRS = registerBlockItem("ashpen_log_stairs",
            ShapedStoneBlocks.ASHPEN_LOG_STAIRS);
    public static final RegistryObject<Item> ASHPEN_WOOD_SLAB = registerBlockItem("ashpen_wood_slab",
            ShapedStoneBlocks.ASHPEN_WOOD_SLAB);
    public static final RegistryObject<Item> ASHPEN_WOOD_STAIRS = registerBlockItem("ashpen_wood_stairs",
            ShapedStoneBlocks.ASHPEN_WOOD_STAIRS);
    public static final RegistryObject<Item> STRIPPED_ASHPEN_LOG = registerBlockItem("stripped_ashpen_log",
            ShapedStoneBlocks.STRIPPED_ASHPEN_LOG);
    public static final RegistryObject<Item> STRIPPED_ASHPEN_WOOD = registerBlockItem("stripped_ashpen_wood",
            ShapedStoneBlocks.STRIPPED_ASHPEN_WOOD);
    public static final RegistryObject<Item> STRIPPED_ASHPEN_LOG_SLAB = registerBlockItem("stripped_ashpen_log_slab",
            ShapedStoneBlocks.STRIPPED_ASHPEN_LOG_SLAB);
    public static final RegistryObject<Item> STRIPPED_ASHPEN_LOG_STAIRS = registerBlockItem(
            "stripped_ashpen_log_stairs", ShapedStoneBlocks.STRIPPED_ASHPEN_LOG_STAIRS);
    public static final RegistryObject<Item> STRIPPED_ASHPEN_WOOD_SLAB = registerBlockItem("stripped_ashpen_wood_slab",
            ShapedStoneBlocks.STRIPPED_ASHPEN_WOOD_SLAB);
    public static final RegistryObject<Item> STRIPPED_ASHPEN_WOOD_STAIRS = registerBlockItem(
            "stripped_ashpen_wood_stairs", ShapedStoneBlocks.STRIPPED_ASHPEN_WOOD_STAIRS);
    public static final RegistryObject<Item> BIT_POLISHED_TUFF_VERTICAL_SLAB = registerBlockItem(
            "bit_polished_tuff_vertical_slab", ShapedStoneBlocks.BIT_POLISHED_TUFF_VERTICAL_SLAB);
    public static final RegistryObject<Item> BIT_TUFF_BRICKS_VERTICAL_SLAB = registerBlockItem(
            "bit_tuff_bricks_vertical_slab", ShapedStoneBlocks.BIT_TUFF_BRICKS_VERTICAL_SLAB);
    public static final RegistryObject<Item> PODZOL_VERTICAL_SLAB = registerBlockItem("podzol_vertical_slab",
            ShapedStoneBlocks.PODZOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> DIRT_VERTICAL_SLAB = registerBlockItem("dirt_vertical_slab",
            ShapedStoneBlocks.DIRT_VERTICAL_SLAB);
    public static final RegistryObject<Item> MYCELIUM_VERTICAL_SLAB = registerBlockItem("mycelium_vertical_slab",
            ShapedStoneBlocks.MYCELIUM_VERTICAL_SLAB);
    public static final RegistryObject<Item> MUD_VERTICAL_SLAB = registerBlockItem("mud_vertical_slab",
            ShapedStoneBlocks.MUD_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLACK_SANDSTONE_VERTICAL_SLAB = registerBlockItem(
            "black_sandstone_vertical_slab", ShapedStoneBlocks.BLACK_SANDSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLUE_SANDSTONE_VERTICAL_SLAB = registerBlockItem(
            "blue_sandstone_vertical_slab", ShapedStoneBlocks.BLUE_SANDSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> GREEN_SANDSTONE_VERTICAL_SLAB = registerBlockItem(
            "green_sandstone_vertical_slab", ShapedStoneBlocks.GREEN_SANDSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> ORANGE_SANDSTONE_VERTICAL_SLAB = registerBlockItem(
            "orange_sandstone_vertical_slab", ShapedStoneBlocks.ORANGE_SANDSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> PINK_SANDSTONE_VERTICAL_SLAB = registerBlockItem(
            "pink_sandstone_vertical_slab", ShapedStoneBlocks.PINK_SANDSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_SANDSTONE_VERTICAL_SLAB = registerBlockItem(
            "red_sandstone_vertical_slab", ShapedStoneBlocks.RED_SANDSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> WHITE_SANDSTONE_VERTICAL_SLAB = registerBlockItem(
            "white_sandstone_vertical_slab", ShapedStoneBlocks.WHITE_SANDSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> YELLOW_SANDSTONE_VERTICAL_SLAB = registerBlockItem(
            "yellow_sandstone_vertical_slab", ShapedStoneBlocks.YELLOW_SANDSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLACK_SMOOTH_SANDSTONE_VERTICAL_SLAB = registerBlockItem(
            "black_smooth_sandstone_vertical_slab", ShapedStoneBlocks.BLACK_SMOOTH_SANDSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLUE_SMOOTH_SANDSTONE_VERTICAL_SLAB = registerBlockItem(
            "blue_smooth_sandstone_vertical_slab", ShapedStoneBlocks.BLUE_SMOOTH_SANDSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> GREEN_SMOOTH_SANDSTONE_VERTICAL_SLAB = registerBlockItem(
            "green_smooth_sandstone_vertical_slab", ShapedStoneBlocks.GREEN_SMOOTH_SANDSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> ORANGE_SMOOTH_SANDSTONE_VERTICAL_SLAB = registerBlockItem(
            "orange_smooth_sandstone_vertical_slab", ShapedStoneBlocks.ORANGE_SMOOTH_SANDSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> PINK_SMOOTH_SANDSTONE_VERTICAL_SLAB = registerBlockItem(
            "pink_smooth_sandstone_vertical_slab", ShapedStoneBlocks.PINK_SMOOTH_SANDSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_SMOOTH_SANDSTONE_VERTICAL_SLAB = registerBlockItem(
            "red_smooth_sandstone_vertical_slab", ShapedStoneBlocks.RED_SMOOTH_SANDSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> WHITE_SMOOTH_SANDSTONE_VERTICAL_SLAB = registerBlockItem(
            "white_smooth_sandstone_vertical_slab", ShapedStoneBlocks.WHITE_SMOOTH_SANDSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> YELLOW_SMOOTH_SANDSTONE_VERTICAL_SLAB = registerBlockItem(
            "yellow_smooth_sandstone_vertical_slab", ShapedStoneBlocks.YELLOW_SMOOTH_SANDSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLACK_TILES_VERTICAL_SLAB = registerBlockItem("black_tiles_vertical_slab",
            ShapedStoneBlocks.BLACK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLUE_TILES_VERTICAL_SLAB = registerBlockItem("blue_tiles_vertical_slab",
            ShapedStoneBlocks.BLUE_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> BROWN_TILES_VERTICAL_SLAB = registerBlockItem("brown_tiles_vertical_slab",
            ShapedStoneBlocks.BROWN_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> CYAN_TILES_VERTICAL_SLAB = registerBlockItem("cyan_tiles_vertical_slab",
            ShapedStoneBlocks.CYAN_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> GRAY_TILES_VERTICAL_SLAB = registerBlockItem("gray_tiles_vertical_slab",
            ShapedStoneBlocks.GRAY_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> GREEN_TILES_VERTICAL_SLAB = registerBlockItem("green_tiles_vertical_slab",
            ShapedStoneBlocks.GREEN_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_TILES_VERTICAL_SLAB = registerBlockItem(
            "light_blue_tiles_vertical_slab", ShapedStoneBlocks.LIGHT_BLUE_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_TILES_VERTICAL_SLAB = registerBlockItem(
            "light_gray_tiles_vertical_slab", ShapedStoneBlocks.LIGHT_GRAY_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIME_TILES_VERTICAL_SLAB = registerBlockItem("lime_tiles_vertical_slab",
            ShapedStoneBlocks.LIME_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> MAGENTA_TILES_VERTICAL_SLAB = registerBlockItem(
            "magenta_tiles_vertical_slab", ShapedStoneBlocks.MAGENTA_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> ORANGE_TILES_VERTICAL_SLAB = registerBlockItem(
            "orange_tiles_vertical_slab", ShapedStoneBlocks.ORANGE_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> PINK_TILES_VERTICAL_SLAB = registerBlockItem("pink_tiles_vertical_slab",
            ShapedStoneBlocks.PINK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> PURPLE_TILES_VERTICAL_SLAB = registerBlockItem(
            "purple_tiles_vertical_slab", ShapedStoneBlocks.PURPLE_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_TILES_VERTICAL_SLAB = registerBlockItem("red_tiles_vertical_slab",
            ShapedStoneBlocks.RED_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> WHITE_TILES_VERTICAL_SLAB = registerBlockItem("white_tiles_vertical_slab",
            ShapedStoneBlocks.WHITE_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> YELLOW_TILES_VERTICAL_SLAB = registerBlockItem(
            "yellow_tiles_vertical_slab", ShapedStoneBlocks.YELLOW_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_BASALT_VERTICAL_SLAB = registerBlockItem(
            "polished_basalt_vertical_slab", ShapedStoneBlocks.POLISHED_BASALT_VERTICAL_SLAB);
    public static final RegistryObject<Item> DRIPSTONE_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "dripstone_block_vertical_slab", ShapedStoneBlocks.DRIPSTONE_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> END_STONE_VERTICAL_SLAB = registerBlockItem("end_stone_vertical_slab",
            ShapedStoneBlocks.END_STONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> QUARTZ_BRICKS_VERTICAL_SLAB = registerBlockItem(
            "quartz_bricks_vertical_slab", ShapedStoneBlocks.QUARTZ_BRICKS_VERTICAL_SLAB);
    public static final RegistryObject<Item> QUARTZ_PILLAR_VERTICAL_SLAB = registerBlockItem(
            "quartz_pillar_vertical_slab", ShapedStoneBlocks.QUARTZ_PILLAR_VERTICAL_SLAB);
    public static final RegistryObject<Item> CALCITE_VERTICAL_SLAB = registerBlockItem("calcite_vertical_slab",
            ShapedStoneBlocks.CALCITE_VERTICAL_SLAB);
    public static final RegistryObject<Item> BONE_BLOCK_VERTICAL_SLAB = registerBlockItem("bone_block_vertical_slab",
            ShapedStoneBlocks.BONE_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> STACKED_BONE_VERTICAL_SLAB = registerBlockItem(
            "stacked_bone_vertical_slab", ShapedStoneBlocks.STACKED_BONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_BONE_VERTICAL_SLAB = registerBlockItem(
            "polished_bone_vertical_slab", ShapedStoneBlocks.POLISHED_BONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> BONE_BRICK_VERTICAL_SLAB = registerBlockItem("bone_brick_vertical_slab",
            ShapedStoneBlocks.BONE_BRICK_VERTICAL_SLAB);
    public static final RegistryObject<Item> OBSIDIAN_VERTICAL_SLAB = registerBlockItem("obsidian_vertical_slab",
            ShapedStoneBlocks.OBSIDIAN_VERTICAL_SLAB);
    public static final RegistryObject<Item> SMOOTH_BASALT_VERTICAL_SLAB = registerBlockItem(
            "smooth_basalt_vertical_slab", ShapedStoneBlocks.SMOOTH_BASALT_VERTICAL_SLAB);
    public static final RegistryObject<Item> MOSS_BLOCK_VERTICAL_SLAB = registerBlockItem("moss_block_vertical_slab",
            ShapedStoneBlocks.MOSS_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> AMETHYST_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "amethyst_block_vertical_slab", ShapedStoneBlocks.AMETHYST_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> MOSSY_CALCITE_VERTICAL_SLAB = registerBlockItem(
            "mossy_calcite_vertical_slab", ShapedStoneBlocks.MOSSY_CALCITE_VERTICAL_SLAB);
    public static final RegistryObject<Item> BIT_COPPER_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "bit_copper_block_vertical_slab", ShapedStoneBlocks.BIT_COPPER_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> BIT_EXPOSED_COPPER_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "bit_exposed_copper_block_vertical_slab", ShapedStoneBlocks.BIT_EXPOSED_COPPER_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> BIT_WEATHERED_COPPER_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "bit_weathered_copper_block_vertical_slab", ShapedStoneBlocks.BIT_WEATHERED_COPPER_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> BIT_OXIDIZED_COPPER_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "bit_oxidized_copper_block_vertical_slab", ShapedStoneBlocks.BIT_OXIDIZED_COPPER_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> BIT_CUT_COPPER_VERTICAL_SLAB = registerBlockItem(
            "bit_cut_copper_vertical_slab", ShapedStoneBlocks.BIT_CUT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> BIT_EXPOSED_CUT_COPPER_VERTICAL_SLAB = registerBlockItem(
            "bit_exposed_cut_copper_vertical_slab", ShapedStoneBlocks.BIT_EXPOSED_CUT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> BIT_WEATHERED_CUT_COPPER_VERTICAL_SLAB = registerBlockItem(
            "bit_weathered_cut_copper_vertical_slab", ShapedStoneBlocks.BIT_WEATHERED_CUT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> BIT_OXIDIZED_CUT_COPPER_VERTICAL_SLAB = registerBlockItem(
            "bit_oxidized_cut_copper_vertical_slab", ShapedStoneBlocks.BIT_OXIDIZED_CUT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLACK_CONCRETE_VERTICAL_SLAB = registerBlockItem(
            "black_concrete_vertical_slab", ShapedStoneBlocks.BLACK_CONCRETE_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLUE_CONCRETE_VERTICAL_SLAB = registerBlockItem(
            "blue_concrete_vertical_slab", ShapedStoneBlocks.BLUE_CONCRETE_VERTICAL_SLAB);
    public static final RegistryObject<Item> BROWN_CONCRETE_VERTICAL_SLAB = registerBlockItem(
            "brown_concrete_vertical_slab", ShapedStoneBlocks.BROWN_CONCRETE_VERTICAL_SLAB);
    public static final RegistryObject<Item> CYAN_CONCRETE_VERTICAL_SLAB = registerBlockItem(
            "cyan_concrete_vertical_slab", ShapedStoneBlocks.CYAN_CONCRETE_VERTICAL_SLAB);
    public static final RegistryObject<Item> GRAY_CONCRETE_VERTICAL_SLAB = registerBlockItem(
            "gray_concrete_vertical_slab", ShapedStoneBlocks.GRAY_CONCRETE_VERTICAL_SLAB);
    public static final RegistryObject<Item> GREEN_CONCRETE_VERTICAL_SLAB = registerBlockItem(
            "green_concrete_vertical_slab", ShapedStoneBlocks.GREEN_CONCRETE_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_CONCRETE_VERTICAL_SLAB = registerBlockItem(
            "light_blue_concrete_vertical_slab", ShapedStoneBlocks.LIGHT_BLUE_CONCRETE_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_CONCRETE_VERTICAL_SLAB = registerBlockItem(
            "light_gray_concrete_vertical_slab", ShapedStoneBlocks.LIGHT_GRAY_CONCRETE_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIME_CONCRETE_VERTICAL_SLAB = registerBlockItem(
            "lime_concrete_vertical_slab", ShapedStoneBlocks.LIME_CONCRETE_VERTICAL_SLAB);
    public static final RegistryObject<Item> MAGENTA_CONCRETE_VERTICAL_SLAB = registerBlockItem(
            "magenta_concrete_vertical_slab", ShapedStoneBlocks.MAGENTA_CONCRETE_VERTICAL_SLAB);
    public static final RegistryObject<Item> ORANGE_CONCRETE_VERTICAL_SLAB = registerBlockItem(
            "orange_concrete_vertical_slab", ShapedStoneBlocks.ORANGE_CONCRETE_VERTICAL_SLAB);
    public static final RegistryObject<Item> PINK_CONCRETE_VERTICAL_SLAB = registerBlockItem(
            "pink_concrete_vertical_slab", ShapedStoneBlocks.PINK_CONCRETE_VERTICAL_SLAB);
    public static final RegistryObject<Item> PURPLE_CONCRETE_VERTICAL_SLAB = registerBlockItem(
            "purple_concrete_vertical_slab", ShapedStoneBlocks.PURPLE_CONCRETE_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_CONCRETE_VERTICAL_SLAB = registerBlockItem(
            "red_concrete_vertical_slab", ShapedStoneBlocks.RED_CONCRETE_VERTICAL_SLAB);
    public static final RegistryObject<Item> WHITE_CONCRETE_VERTICAL_SLAB = registerBlockItem(
            "white_concrete_vertical_slab", ShapedStoneBlocks.WHITE_CONCRETE_VERTICAL_SLAB);
    public static final RegistryObject<Item> YELLOW_CONCRETE_VERTICAL_SLAB = registerBlockItem(
            "yellow_concrete_vertical_slab", ShapedStoneBlocks.YELLOW_CONCRETE_VERTICAL_SLAB);
    public static final RegistryObject<Item> HAY_BALE_VERTICAL_SLAB = registerBlockItem("hay_bale_vertical_slab",
            ShapedStoneBlocks.HAY_BALE_VERTICAL_SLAB);
    public static final RegistryObject<Item> BAMBOO_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "bamboo_block_vertical_slab", ShapedStoneBlocks.BAMBOO_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_BAMBOO_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "stripped_bamboo_block_vertical_slab", ShapedStoneBlocks.STRIPPED_BAMBOO_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> ASHPEN_WHITE_VERTICAL_SLAB = registerBlockItem(
            "ashpen_white_vertical_slab", ShapedStoneBlocks.ASHPEN_WHITE_VERTICAL_SLAB);
    public static final RegistryObject<Item> ASHPEN_BLACK_VERTICAL_SLAB = registerBlockItem(
            "ashpen_black_vertical_slab", ShapedStoneBlocks.ASHPEN_BLACK_VERTICAL_SLAB);
    public static final RegistryObject<Item> ASHPEN_BLUE_VERTICAL_SLAB = registerBlockItem("ashpen_blue_vertical_slab",
            ShapedStoneBlocks.ASHPEN_BLUE_VERTICAL_SLAB);
    public static final RegistryObject<Item> ASHPEN_BROWN_VERTICAL_SLAB = registerBlockItem(
            "ashpen_brown_vertical_slab", ShapedStoneBlocks.ASHPEN_BROWN_VERTICAL_SLAB);
    public static final RegistryObject<Item> ASHPEN_CYAN_VERTICAL_SLAB = registerBlockItem("ashpen_cyan_vertical_slab",
            ShapedStoneBlocks.ASHPEN_CYAN_VERTICAL_SLAB);
    public static final RegistryObject<Item> ASHPEN_GRAY_VERTICAL_SLAB = registerBlockItem("ashpen_gray_vertical_slab",
            ShapedStoneBlocks.ASHPEN_GRAY_VERTICAL_SLAB);
    public static final RegistryObject<Item> ASHPEN_GREEN_VERTICAL_SLAB = registerBlockItem(
            "ashpen_green_vertical_slab", ShapedStoneBlocks.ASHPEN_GREEN_VERTICAL_SLAB);
    public static final RegistryObject<Item> ASHPEN_LIGHT_BLUE_VERTICAL_SLAB = registerBlockItem(
            "ashpen_light_blue_vertical_slab", ShapedStoneBlocks.ASHPEN_LIGHT_BLUE_VERTICAL_SLAB);
    public static final RegistryObject<Item> ASHPEN_LIGHT_GRAY_VERTICAL_SLAB = registerBlockItem(
            "ashpen_light_gray_vertical_slab", ShapedStoneBlocks.ASHPEN_LIGHT_GRAY_VERTICAL_SLAB);
    public static final RegistryObject<Item> ASHPEN_LIME_VERTICAL_SLAB = registerBlockItem("ashpen_lime_vertical_slab",
            ShapedStoneBlocks.ASHPEN_LIME_VERTICAL_SLAB);
    public static final RegistryObject<Item> ASHPEN_MAGENTA_VERTICAL_SLAB = registerBlockItem(
            "ashpen_magenta_vertical_slab", ShapedStoneBlocks.ASHPEN_MAGENTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> ASHPEN_ORANGE_VERTICAL_SLAB = registerBlockItem(
            "ashpen_orange_vertical_slab", ShapedStoneBlocks.ASHPEN_ORANGE_VERTICAL_SLAB);
    public static final RegistryObject<Item> ASHPEN_PINK_VERTICAL_SLAB = registerBlockItem("ashpen_pink_vertical_slab",
            ShapedStoneBlocks.ASHPEN_PINK_VERTICAL_SLAB);
    public static final RegistryObject<Item> ASHPEN_PURPLE_VERTICAL_SLAB = registerBlockItem(
            "ashpen_purple_vertical_slab", ShapedStoneBlocks.ASHPEN_PURPLE_VERTICAL_SLAB);
    public static final RegistryObject<Item> ASHPEN_RED_VERTICAL_SLAB = registerBlockItem("ashpen_red_vertical_slab",
            ShapedStoneBlocks.ASHPEN_RED_VERTICAL_SLAB);
    public static final RegistryObject<Item> ASHPEN_YELLOW_VERTICAL_SLAB = registerBlockItem(
            "ashpen_yellow_vertical_slab", ShapedStoneBlocks.ASHPEN_YELLOW_VERTICAL_SLAB);
    public static final RegistryObject<Item> SNOW_BRICKS_VERTICAL_SLAB = registerBlockItem("snow_bricks_vertical_slab",
            ShapedStoneBlocks.SNOW_BRICKS_VERTICAL_SLAB);
    public static final RegistryObject<Item> SNOW_VERTICAL_SLAB = registerBlockItem("snow_vertical_slab",
            ShapedStoneBlocks.SNOW_VERTICAL_SLAB);
    public static final RegistryObject<Item> SNOWY_GRASS_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "snowy_grass_block_vertical_slab", ShapedStoneBlocks.SNOWY_GRASS_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> MANGROVE_VERTICAL_SLAB = registerBlockItem("mangrove_vertical_slab",
            ShapedStoneBlocks.MANGROVE_VERTICAL_SLAB);
    public static final RegistryObject<Item> STEEL_BLOCK_VERTICAL_SLAB = registerBlockItem("steel_block_vertical_slab",
            ShapedStoneBlocks.STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "polished_steel_vertical_slab", ShapedStoneBlocks.POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> PRESSED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "pressed_steel_vertical_slab", ShapedStoneBlocks.PRESSED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> CUT_STEEL_VERTICAL_SLAB = registerBlockItem("cut_steel_vertical_slab",
            ShapedStoneBlocks.CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> CAUTION_BLACK_VERTICAL_SLAB = registerBlockItem(
            "caution_black_vertical_slab", ShapedStoneBlocks.CAUTION_BLACK_VERTICAL_SLAB);
    public static final RegistryObject<Item> CAUTION_BLUE_VERTICAL_SLAB = registerBlockItem(
            "caution_blue_vertical_slab", ShapedStoneBlocks.CAUTION_BLUE_VERTICAL_SLAB);
    public static final RegistryObject<Item> CAUTION_FACTORY_VERTICAL_SLAB = registerBlockItem(
            "caution_factory_vertical_slab", ShapedStoneBlocks.CAUTION_FACTORY_VERTICAL_SLAB);
    public static final RegistryObject<Item> CAUTION_RED_VERTICAL_SLAB = registerBlockItem("caution_red_vertical_slab",
            ShapedStoneBlocks.CAUTION_RED_VERTICAL_SLAB);
    public static final RegistryObject<Item> FRAMED_CAUTION_VERTICAL_SLAB = registerBlockItem(
            "framed_caution_vertical_slab", ShapedStoneBlocks.FRAMED_CAUTION_VERTICAL_SLAB);
    public static final RegistryObject<Item> CAUTION_WHITE_VERTICAL_SLAB = registerBlockItem(
            "caution_white_vertical_slab", ShapedStoneBlocks.CAUTION_WHITE_VERTICAL_SLAB);
    public static final RegistryObject<Item> CAUTION_LIME_VERTICAL_SLAB = registerBlockItem(
            "caution_lime_vertical_slab", ShapedStoneBlocks.CAUTION_LIME_VERTICAL_SLAB);
    public static final RegistryObject<Item> CAUTION_PINK_VERTICAL_SLAB = registerBlockItem(
            "caution_pink_vertical_slab", ShapedStoneBlocks.CAUTION_PINK_VERTICAL_SLAB);
    public static final RegistryObject<Item> CAUTION_YELLOW_VERTICAL_SLAB = registerBlockItem(
            "caution_yellow_vertical_slab", ShapedStoneBlocks.CAUTION_YELLOW_VERTICAL_SLAB);
    public static final RegistryObject<Item> CAUTION_CANDY_VERTICAL_SLAB = registerBlockItem(
            "caution_candy_vertical_slab", ShapedStoneBlocks.CAUTION_CANDY_VERTICAL_SLAB);
    public static final RegistryObject<Item> CAUTION_COTTONCANDY_VERTICAL_SLAB = registerBlockItem(
            "caution_cottoncandy_vertical_slab", ShapedStoneBlocks.CAUTION_COTTONCANDY_VERTICAL_SLAB);
    public static final RegistryObject<Item> CAUTION_MINTCANDY_VERTICAL_SLAB = registerBlockItem(
            "caution_mintcandy_vertical_slab", ShapedStoneBlocks.CAUTION_MINTCANDY_VERTICAL_SLAB);
    public static final RegistryObject<Item> CAUTION_CITRUSCANDY_VERTICAL_SLAB = registerBlockItem(
            "caution_citruscandy_vertical_slab", ShapedStoneBlocks.CAUTION_CITRUSCANDY_VERTICAL_SLAB);
    public static final RegistryObject<Item> WHITE_STAINED_BRICKS_VERTICAL_SLAB = registerBlockItem(
            "white_stained_bricks_vertical_slab", ShapedStoneBlocks.WHITE_STAINED_BRICKS_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICKS_VERTICAL_SLAB = registerBlockItem(
            "light_gray_stained_bricks_vertical_slab", ShapedStoneBlocks.LIGHT_GRAY_STAINED_BRICKS_VERTICAL_SLAB);
    public static final RegistryObject<Item> GRAY_STAINED_BRICKS_VERTICAL_SLAB = registerBlockItem(
            "gray_stained_bricks_vertical_slab", ShapedStoneBlocks.GRAY_STAINED_BRICKS_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLACK_STAINED_BRICKS_VERTICAL_SLAB = registerBlockItem(
            "black_stained_bricks_vertical_slab", ShapedStoneBlocks.BLACK_STAINED_BRICKS_VERTICAL_SLAB);
    public static final RegistryObject<Item> BROWN_STAINED_BRICKS_VERTICAL_SLAB = registerBlockItem(
            "brown_stained_bricks_vertical_slab", ShapedStoneBlocks.BROWN_STAINED_BRICKS_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_STAINED_BRICKS_VERTICAL_SLAB = registerBlockItem(
            "red_stained_bricks_vertical_slab", ShapedStoneBlocks.RED_STAINED_BRICKS_VERTICAL_SLAB);
    public static final RegistryObject<Item> ORANGE_STAINED_BRICKS_VERTICAL_SLAB = registerBlockItem(
            "orange_stained_bricks_vertical_slab", ShapedStoneBlocks.ORANGE_STAINED_BRICKS_VERTICAL_SLAB);
    public static final RegistryObject<Item> YELLOW_STAINED_BRICKS_VERTICAL_SLAB = registerBlockItem(
            "yellow_stained_bricks_vertical_slab", ShapedStoneBlocks.YELLOW_STAINED_BRICKS_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIME_STAINED_BRICKS_VERTICAL_SLAB = registerBlockItem(
            "lime_stained_bricks_vertical_slab", ShapedStoneBlocks.LIME_STAINED_BRICKS_VERTICAL_SLAB);
    public static final RegistryObject<Item> GREEN_STAINED_BRICKS_VERTICAL_SLAB = registerBlockItem(
            "green_stained_bricks_vertical_slab", ShapedStoneBlocks.GREEN_STAINED_BRICKS_VERTICAL_SLAB);
    public static final RegistryObject<Item> CYAN_STAINED_BRICKS_VERTICAL_SLAB = registerBlockItem(
            "cyan_stained_bricks_vertical_slab", ShapedStoneBlocks.CYAN_STAINED_BRICKS_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICKS_VERTICAL_SLAB = registerBlockItem(
            "light_blue_stained_bricks_vertical_slab", ShapedStoneBlocks.LIGHT_BLUE_STAINED_BRICKS_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLUE_STAINED_BRICKS_VERTICAL_SLAB = registerBlockItem(
            "blue_stained_bricks_vertical_slab", ShapedStoneBlocks.BLUE_STAINED_BRICKS_VERTICAL_SLAB);
    public static final RegistryObject<Item> PURPLE_STAINED_BRICKS_VERTICAL_SLAB = registerBlockItem(
            "purple_stained_bricks_vertical_slab", ShapedStoneBlocks.PURPLE_STAINED_BRICKS_VERTICAL_SLAB);
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICKS_VERTICAL_SLAB = registerBlockItem(
            "magenta_stained_bricks_vertical_slab", ShapedStoneBlocks.MAGENTA_STAINED_BRICKS_VERTICAL_SLAB);
    public static final RegistryObject<Item> PINK_STAINED_BRICKS_VERTICAL_SLAB = registerBlockItem(
            "pink_stained_bricks_vertical_slab", ShapedStoneBlocks.PINK_STAINED_BRICKS_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_WHITE_CONCRETE_VERTICAL_SLAB = registerBlockItem(
            "polished_white_concrete_vertical_slab", ShapedStoneBlocks.POLISHED_WHITE_CONCRETE_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_ORANGE_CONCRETE_VERTICAL_SLAB = registerBlockItem(
            "polished_orange_concrete_vertical_slab", ShapedStoneBlocks.POLISHED_ORANGE_CONCRETE_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_MAGENTA_CONCRETE_VERTICAL_SLAB = registerBlockItem(
            "polished_magenta_concrete_vertical_slab", ShapedStoneBlocks.POLISHED_MAGENTA_CONCRETE_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_LIGHT_BLUE_CONCRETE_VERTICAL_SLAB = registerBlockItem(
            "polished_light_blue_concrete_vertical_slab", ShapedStoneBlocks.POLISHED_LIGHT_BLUE_CONCRETE_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_YELLOW_CONCRETE_VERTICAL_SLAB = registerBlockItem(
            "polished_yellow_concrete_vertical_slab", ShapedStoneBlocks.POLISHED_YELLOW_CONCRETE_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_LIME_CONCRETE_VERTICAL_SLAB = registerBlockItem(
            "polished_lime_concrete_vertical_slab", ShapedStoneBlocks.POLISHED_LIME_CONCRETE_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_PINK_CONCRETE_VERTICAL_SLAB = registerBlockItem(
            "polished_pink_concrete_vertical_slab", ShapedStoneBlocks.POLISHED_PINK_CONCRETE_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_GRAY_CONCRETE_VERTICAL_SLAB = registerBlockItem(
            "polished_gray_concrete_vertical_slab", ShapedStoneBlocks.POLISHED_GRAY_CONCRETE_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_LIGHT_GRAY_CONCRETE_VERTICAL_SLAB = registerBlockItem(
            "polished_light_gray_concrete_vertical_slab", ShapedStoneBlocks.POLISHED_LIGHT_GRAY_CONCRETE_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_CYAN_CONCRETE_VERTICAL_SLAB = registerBlockItem(
            "polished_cyan_concrete_vertical_slab", ShapedStoneBlocks.POLISHED_CYAN_CONCRETE_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_PURPLE_CONCRETE_VERTICAL_SLAB = registerBlockItem(
            "polished_purple_concrete_vertical_slab", ShapedStoneBlocks.POLISHED_PURPLE_CONCRETE_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_BLUE_CONCRETE_VERTICAL_SLAB = registerBlockItem(
            "polished_blue_concrete_vertical_slab", ShapedStoneBlocks.POLISHED_BLUE_CONCRETE_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_BROWN_CONCRETE_VERTICAL_SLAB = registerBlockItem(
            "polished_brown_concrete_vertical_slab", ShapedStoneBlocks.POLISHED_BROWN_CONCRETE_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_GREEN_CONCRETE_VERTICAL_SLAB = registerBlockItem(
            "polished_green_concrete_vertical_slab", ShapedStoneBlocks.POLISHED_GREEN_CONCRETE_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_RED_CONCRETE_VERTICAL_SLAB = registerBlockItem(
            "polished_red_concrete_vertical_slab", ShapedStoneBlocks.POLISHED_RED_CONCRETE_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_BLACK_CONCRETE_VERTICAL_SLAB = registerBlockItem(
            "polished_black_concrete_vertical_slab", ShapedStoneBlocks.POLISHED_BLACK_CONCRETE_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLACK_FACTORY_MESH_VERTICAL_SLAB = registerBlockItem(
            "black_factory_mesh_vertical_slab", ShapedStoneBlocks.BLACK_FACTORY_MESH_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLUE_FACTORY_MESH_VERTICAL_SLAB = registerBlockItem(
            "blue_factory_mesh_vertical_slab", ShapedStoneBlocks.BLUE_FACTORY_MESH_VERTICAL_SLAB);
    public static final RegistryObject<Item> BROWN_FACTORY_MESH_VERTICAL_SLAB = registerBlockItem(
            "brown_factory_mesh_vertical_slab", ShapedStoneBlocks.BROWN_FACTORY_MESH_VERTICAL_SLAB);
    public static final RegistryObject<Item> GRAY_FACTORY_MESH_VERTICAL_SLAB = registerBlockItem(
            "gray_factory_mesh_vertical_slab", ShapedStoneBlocks.GRAY_FACTORY_MESH_VERTICAL_SLAB);
    public static final RegistryObject<Item> GREEN_FACTORY_MESH_VERTICAL_SLAB = registerBlockItem(
            "green_factory_mesh_vertical_slab", ShapedStoneBlocks.GREEN_FACTORY_MESH_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_FACTORY_MESH_VERTICAL_SLAB = registerBlockItem(
            "light_gray_factory_mesh_vertical_slab", ShapedStoneBlocks.LIGHT_GRAY_FACTORY_MESH_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIME_FACTORY_MESH_VERTICAL_SLAB = registerBlockItem(
            "lime_factory_mesh_vertical_slab", ShapedStoneBlocks.LIME_FACTORY_MESH_VERTICAL_SLAB);
    public static final RegistryObject<Item> ORANGE_FACTORY_MESH_VERTICAL_SLAB = registerBlockItem(
            "orange_factory_mesh_vertical_slab", ShapedStoneBlocks.ORANGE_FACTORY_MESH_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_FACTORY_MESH_VERTICAL_SLAB = registerBlockItem(
            "red_factory_mesh_vertical_slab", ShapedStoneBlocks.RED_FACTORY_MESH_VERTICAL_SLAB);
    public static final RegistryObject<Item> YELLOW_FACTORY_MESH_VERTICAL_SLAB = registerBlockItem(
            "yellow_factory_mesh_vertical_slab", ShapedStoneBlocks.YELLOW_FACTORY_MESH_VERTICAL_SLAB);
    public static final RegistryObject<Item> PACKED_MUD_VERTICAL_SLAB = registerBlockItem("packed_mud_vertical_slab",
            ShapedStoneBlocks.PACKED_MUD_VERTICAL_SLAB);
    public static final RegistryObject<Item> MUD_BRICK_VERTICAL_SLAB = registerBlockItem("mud_brick_vertical_slab",
            ShapedStoneBlocks.MUD_BRICK_VERTICAL_SLAB);
    public static final RegistryObject<Item> GLASS_VERTICAL_SLAB = registerBlockItem("glass_vertical_slab",
            ShapedStoneBlocks.GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> WHITE_STAINED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "white_stained_glass_vertical_slab", ShapedStoneBlocks.WHITE_STAINED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "light_gray_stained_glass_vertical_slab", ShapedStoneBlocks.LIGHT_GRAY_STAINED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> GRAY_STAINED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "gray_stained_glass_vertical_slab", ShapedStoneBlocks.GRAY_STAINED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLACK_STAINED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "black_stained_glass_vertical_slab", ShapedStoneBlocks.BLACK_STAINED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> BROWN_STAINED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "brown_stained_glass_vertical_slab", ShapedStoneBlocks.BROWN_STAINED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_STAINED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "red_stained_glass_vertical_slab", ShapedStoneBlocks.RED_STAINED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> ORANGE_STAINED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "orange_stained_glass_vertical_slab", ShapedStoneBlocks.ORANGE_STAINED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> YELLOW_STAINED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "yellow_stained_glass_vertical_slab", ShapedStoneBlocks.YELLOW_STAINED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIME_STAINED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "lime_stained_glass_vertical_slab", ShapedStoneBlocks.LIME_STAINED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> GREEN_STAINED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "green_stained_glass_vertical_slab", ShapedStoneBlocks.GREEN_STAINED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> CYAN_STAINED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "cyan_stained_glass_vertical_slab", ShapedStoneBlocks.CYAN_STAINED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "light_blue_stained_glass_vertical_slab", ShapedStoneBlocks.LIGHT_BLUE_STAINED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLUE_STAINED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "blue_stained_glass_vertical_slab", ShapedStoneBlocks.BLUE_STAINED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> PURPLE_STAINED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "purple_stained_glass_vertical_slab", ShapedStoneBlocks.PURPLE_STAINED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> MAGENTA_STAINED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "magenta_stained_glass_vertical_slab", ShapedStoneBlocks.MAGENTA_STAINED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> PINK_STAINED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "pink_stained_glass_vertical_slab", ShapedStoneBlocks.PINK_STAINED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> WHITE_MOSAIC_GLASS_VERTICAL_SLAB = registerBlockItem(
            "white_mosaic_glass_vertical_slab", ShapedStoneBlocks.WHITE_MOSAIC_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_MOSAIC_GLASS_VERTICAL_SLAB = registerBlockItem(
            "light_gray_mosaic_glass_vertical_slab", ShapedStoneBlocks.LIGHT_GRAY_MOSAIC_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> GRAY_MOSAIC_GLASS_VERTICAL_SLAB = registerBlockItem(
            "gray_mosaic_glass_vertical_slab", ShapedStoneBlocks.GRAY_MOSAIC_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLACK_MOSAIC_GLASS_VERTICAL_SLAB = registerBlockItem(
            "black_mosaic_glass_vertical_slab", ShapedStoneBlocks.BLACK_MOSAIC_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> BROWN_MOSAIC_GLASS_VERTICAL_SLAB = registerBlockItem(
            "brown_mosaic_glass_vertical_slab", ShapedStoneBlocks.BROWN_MOSAIC_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_MOSAIC_GLASS_VERTICAL_SLAB = registerBlockItem(
            "red_mosaic_glass_vertical_slab", ShapedStoneBlocks.RED_MOSAIC_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> ORANGE_MOSAIC_GLASS_VERTICAL_SLAB = registerBlockItem(
            "orange_mosaic_glass_vertical_slab", ShapedStoneBlocks.ORANGE_MOSAIC_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> YELLOW_MOSAIC_GLASS_VERTICAL_SLAB = registerBlockItem(
            "yellow_mosaic_glass_vertical_slab", ShapedStoneBlocks.YELLOW_MOSAIC_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIME_MOSAIC_GLASS_VERTICAL_SLAB = registerBlockItem(
            "lime_mosaic_glass_vertical_slab", ShapedStoneBlocks.LIME_MOSAIC_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> GREEN_MOSAIC_GLASS_VERTICAL_SLAB = registerBlockItem(
            "green_mosaic_glass_vertical_slab", ShapedStoneBlocks.GREEN_MOSAIC_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> CYAN_MOSAIC_GLASS_VERTICAL_SLAB = registerBlockItem(
            "cyan_mosaic_glass_vertical_slab", ShapedStoneBlocks.CYAN_MOSAIC_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_MOSAIC_GLASS_VERTICAL_SLAB = registerBlockItem(
            "light_blue_mosaic_glass_vertical_slab", ShapedStoneBlocks.LIGHT_BLUE_MOSAIC_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLUE_MOSAIC_GLASS_VERTICAL_SLAB = registerBlockItem(
            "blue_mosaic_glass_vertical_slab", ShapedStoneBlocks.BLUE_MOSAIC_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> PURPLE_MOSAIC_GLASS_VERTICAL_SLAB = registerBlockItem(
            "purple_mosaic_glass_vertical_slab", ShapedStoneBlocks.PURPLE_MOSAIC_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> MAGENTA_MOSAIC_GLASS_VERTICAL_SLAB = registerBlockItem(
            "magenta_mosaic_glass_vertical_slab", ShapedStoneBlocks.MAGENTA_MOSAIC_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> PINK_MOSAIC_GLASS_VERTICAL_SLAB = registerBlockItem(
            "pink_mosaic_glass_vertical_slab", ShapedStoneBlocks.PINK_MOSAIC_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> FACTORY_WHITE_GLASS_VERTICAL_SLAB = registerBlockItem(
            "factory_white_glass_vertical_slab", ShapedStoneBlocks.FACTORY_WHITE_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> FACTORY_LIGHT_GRAY_GLASS_VERTICAL_SLAB = registerBlockItem(
            "factory_light_gray_glass_vertical_slab", ShapedStoneBlocks.FACTORY_LIGHT_GRAY_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> FACTORY_GRAY_GLASS_VERTICAL_SLAB = registerBlockItem(
            "factory_gray_glass_vertical_slab", ShapedStoneBlocks.FACTORY_GRAY_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> FACTORY_BLACK_GLASS_VERTICAL_SLAB = registerBlockItem(
            "factory_black_glass_vertical_slab", ShapedStoneBlocks.FACTORY_BLACK_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> FACTORY_BROWN_GLASS_VERTICAL_SLAB = registerBlockItem(
            "factory_brown_glass_vertical_slab", ShapedStoneBlocks.FACTORY_BROWN_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> FACTORY_RED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "factory_red_glass_vertical_slab", ShapedStoneBlocks.FACTORY_RED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> FACTORY_ORANGE_GLASS_VERTICAL_SLAB = registerBlockItem(
            "factory_orange_glass_vertical_slab", ShapedStoneBlocks.FACTORY_ORANGE_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> FACTORY_YELLOW_GLASS_VERTICAL_SLAB = registerBlockItem(
            "factory_yellow_glass_vertical_slab", ShapedStoneBlocks.FACTORY_YELLOW_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> FACTORY_LIME_GLASS_VERTICAL_SLAB = registerBlockItem(
            "factory_lime_glass_vertical_slab", ShapedStoneBlocks.FACTORY_LIME_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> FACTORY_GREEN_GLASS_VERTICAL_SLAB = registerBlockItem(
            "factory_green_glass_vertical_slab", ShapedStoneBlocks.FACTORY_GREEN_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> FACTORY_CYAN_GLASS_VERTICAL_SLAB = registerBlockItem(
            "factory_cyan_glass_vertical_slab", ShapedStoneBlocks.FACTORY_CYAN_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> FACTORY_LIGHT_BLUE_GLASS_VERTICAL_SLAB = registerBlockItem(
            "factory_light_blue_glass_vertical_slab", ShapedStoneBlocks.FACTORY_LIGHT_BLUE_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> FACTORY_BLUE_GLASS_VERTICAL_SLAB = registerBlockItem(
            "factory_blue_glass_vertical_slab", ShapedStoneBlocks.FACTORY_BLUE_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> FACTORY_PURPLE_GLASS_VERTICAL_SLAB = registerBlockItem(
            "factory_purple_glass_vertical_slab", ShapedStoneBlocks.FACTORY_PURPLE_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> FACTORY_MAGENTA_GLASS_VERTICAL_SLAB = registerBlockItem(
            "factory_magenta_glass_vertical_slab", ShapedStoneBlocks.FACTORY_MAGENTA_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> FACTORY_PINK_GLASS_VERTICAL_SLAB = registerBlockItem(
            "factory_pink_glass_vertical_slab", ShapedStoneBlocks.FACTORY_PINK_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> WHITE_GLAZED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "white_glazed_glass_vertical_slab", ShapedStoneBlocks.WHITE_GLAZED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_GLAZED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "light_gray_glazed_glass_vertical_slab", ShapedStoneBlocks.LIGHT_GRAY_GLAZED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> GRAY_GLAZED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "gray_glazed_glass_vertical_slab", ShapedStoneBlocks.GRAY_GLAZED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLACK_GLAZED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "black_glazed_glass_vertical_slab", ShapedStoneBlocks.BLACK_GLAZED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> BROWN_GLAZED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "brown_glazed_glass_vertical_slab", ShapedStoneBlocks.BROWN_GLAZED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_GLAZED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "red_glazed_glass_vertical_slab", ShapedStoneBlocks.RED_GLAZED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> ORANGE_GLAZED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "orange_glazed_glass_vertical_slab", ShapedStoneBlocks.ORANGE_GLAZED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> YELLOW_GLAZED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "yellow_glazed_glass_vertical_slab", ShapedStoneBlocks.YELLOW_GLAZED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIME_GLAZED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "lime_glazed_glass_vertical_slab", ShapedStoneBlocks.LIME_GLAZED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> GREEN_GLAZED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "green_glazed_glass_vertical_slab", ShapedStoneBlocks.GREEN_GLAZED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> CYAN_GLAZED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "cyan_glazed_glass_vertical_slab", ShapedStoneBlocks.CYAN_GLAZED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_GLAZED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "light_blue_glazed_glass_vertical_slab", ShapedStoneBlocks.LIGHT_BLUE_GLAZED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLUE_GLAZED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "blue_glazed_glass_vertical_slab", ShapedStoneBlocks.BLUE_GLAZED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> PURPLE_GLAZED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "purple_glazed_glass_vertical_slab", ShapedStoneBlocks.PURPLE_GLAZED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> MAGENTA_GLAZED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "magenta_glazed_glass_vertical_slab", ShapedStoneBlocks.MAGENTA_GLAZED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> PINK_GLAZED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "pink_glazed_glass_vertical_slab", ShapedStoneBlocks.PINK_GLAZED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> OAK_LOG_VERTICAL_SLAB = registerBlockItem("oak_log_vertical_slab",
            ShapedStoneBlocks.OAK_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_OAK_LOG_VERTICAL_SLAB = registerBlockItem(
            "stripped_oak_log_vertical_slab", ShapedStoneBlocks.STRIPPED_OAK_LOG_VERTICAL_SLAB);
}
