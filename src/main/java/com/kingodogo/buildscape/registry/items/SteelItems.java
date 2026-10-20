package com.kingodogo.buildscape.registry.items;

import com.kingodogo.buildscape.registry.ModBlocks;
import com.kingodogo.buildscape.registry.ModCreativeModeTab;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import static com.kingodogo.buildscape.registry.items.ItemsRegister.ITEMS;
import static com.kingodogo.buildscape.registry.items.ItemsRegister.createBlockItemProperties;
import static com.kingodogo.buildscape.registry.items.ItemsRegister.registerBlockItem;

/**
 * The items MAGENTA_LAYERED_WOOL through HOLLOW_SPRUCE_LOG of the registry, part 7 of 8, in registration order.
 * Named for its dominant families (steel, wool, light); the neighbouring families that share a run of the order
 * with them are registered here too, because the registration order must not change.
 * Load order: {@link CopperItems#init()} runs first (see the static block), so every holder registers in order whichever is touched first.
 */
public final class SteelItems {

    private SteelItems() {}

    /** Forces this class to load, which registers its items. */
    public static void init() {}

    static {
        CopperItems.init();
    }

    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL = registerBlockItem("magenta_layered_wool",
            ModBlocks.MAGENTA_LAYERED_WOOL);
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL = registerBlockItem("light_blue_layered_wool",
            ModBlocks.LIGHT_BLUE_LAYERED_WOOL);
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL = registerBlockItem("yellow_layered_wool",
            ModBlocks.YELLOW_LAYERED_WOOL);
    public static final RegistryObject<Item> LIME_LAYERED_WOOL = registerBlockItem("lime_layered_wool",
            ModBlocks.LIME_LAYERED_WOOL);
    public static final RegistryObject<Item> PINK_LAYERED_WOOL = registerBlockItem("pink_layered_wool",
            ModBlocks.PINK_LAYERED_WOOL);
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL = registerBlockItem("gray_layered_wool",
            ModBlocks.GRAY_LAYERED_WOOL);
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL = registerBlockItem("light_gray_layered_wool",
            ModBlocks.LIGHT_GRAY_LAYERED_WOOL);
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL = registerBlockItem("cyan_layered_wool",
            ModBlocks.CYAN_LAYERED_WOOL);
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL = registerBlockItem("purple_layered_wool",
            ModBlocks.PURPLE_LAYERED_WOOL);
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL = registerBlockItem("blue_layered_wool",
            ModBlocks.BLUE_LAYERED_WOOL);
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL = registerBlockItem("brown_layered_wool",
            ModBlocks.BROWN_LAYERED_WOOL);
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL = registerBlockItem("green_layered_wool",
            ModBlocks.GREEN_LAYERED_WOOL);
    public static final RegistryObject<Item> RED_LAYERED_WOOL = registerBlockItem("red_layered_wool",
            ModBlocks.RED_LAYERED_WOOL);
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL = registerBlockItem("black_layered_wool",
            ModBlocks.BLACK_LAYERED_WOOL);
    public static final RegistryObject<Item> WHITE_LAYERED_WOOL_SLAB = registerBlockItem("white_layered_wool_slab",
            ModBlocks.WHITE_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> WHITE_LAYERED_WOOL_STAIRS = registerBlockItem("white_layered_wool_stairs",
            ModBlocks.WHITE_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> WHITE_LAYERED_WOOL_WALL = registerBlockItem("white_layered_wool_wall",
            ModBlocks.WHITE_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> WHITE_LAYERED_WOOL_CARPET = registerBlockItem("white_layered_wool_carpet",
            ModBlocks.WHITE_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> WHITE_LAYERED_WOOL_LAYERS = registerBlockItem("white_layered_wool_layers",
            ModBlocks.WHITE_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> WHITE_LAYERED_WOOL_VERTICAL_SLAB = registerBlockItem(
            "white_layered_wool_vertical_slab", ModBlocks.WHITE_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL_SLAB = registerBlockItem("orange_layered_wool_slab",
            ModBlocks.ORANGE_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL_STAIRS = registerBlockItem(
            "orange_layered_wool_stairs", ModBlocks.ORANGE_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL_WALL = registerBlockItem("orange_layered_wool_wall",
            ModBlocks.ORANGE_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL_CARPET = registerBlockItem(
            "orange_layered_wool_carpet", ModBlocks.ORANGE_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL_LAYERS = registerBlockItem(
            "orange_layered_wool_layers", ModBlocks.ORANGE_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL_VERTICAL_SLAB = registerBlockItem(
            "orange_layered_wool_vertical_slab", ModBlocks.ORANGE_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL_SLAB = registerBlockItem("magenta_layered_wool_slab",
            ModBlocks.MAGENTA_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL_STAIRS = registerBlockItem(
            "magenta_layered_wool_stairs", ModBlocks.MAGENTA_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL_WALL = registerBlockItem("magenta_layered_wool_wall",
            ModBlocks.MAGENTA_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL_CARPET = registerBlockItem(
            "magenta_layered_wool_carpet", ModBlocks.MAGENTA_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL_LAYERS = registerBlockItem(
            "magenta_layered_wool_layers", ModBlocks.MAGENTA_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL_VERTICAL_SLAB = registerBlockItem(
            "magenta_layered_wool_vertical_slab", ModBlocks.MAGENTA_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL_SLAB = registerBlockItem(
            "light_blue_layered_wool_slab", ModBlocks.LIGHT_BLUE_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL_STAIRS = registerBlockItem(
            "light_blue_layered_wool_stairs", ModBlocks.LIGHT_BLUE_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL_WALL = registerBlockItem(
            "light_blue_layered_wool_wall", ModBlocks.LIGHT_BLUE_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL_CARPET = registerBlockItem(
            "light_blue_layered_wool_carpet", ModBlocks.LIGHT_BLUE_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL_LAYERS = registerBlockItem(
            "light_blue_layered_wool_layers", ModBlocks.LIGHT_BLUE_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL_VERTICAL_SLAB = registerBlockItem(
            "light_blue_layered_wool_vertical_slab", ModBlocks.LIGHT_BLUE_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL_SLAB = registerBlockItem("yellow_layered_wool_slab",
            ModBlocks.YELLOW_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL_STAIRS = registerBlockItem(
            "yellow_layered_wool_stairs", ModBlocks.YELLOW_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL_WALL = registerBlockItem("yellow_layered_wool_wall",
            ModBlocks.YELLOW_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL_CARPET = registerBlockItem(
            "yellow_layered_wool_carpet", ModBlocks.YELLOW_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL_LAYERS = registerBlockItem(
            "yellow_layered_wool_layers", ModBlocks.YELLOW_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL_VERTICAL_SLAB = registerBlockItem(
            "yellow_layered_wool_vertical_slab", ModBlocks.YELLOW_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIME_LAYERED_WOOL_SLAB = registerBlockItem("lime_layered_wool_slab",
            ModBlocks.LIME_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> LIME_LAYERED_WOOL_STAIRS = registerBlockItem("lime_layered_wool_stairs",
            ModBlocks.LIME_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> LIME_LAYERED_WOOL_WALL = registerBlockItem("lime_layered_wool_wall",
            ModBlocks.LIME_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> LIME_LAYERED_WOOL_CARPET = registerBlockItem("lime_layered_wool_carpet",
            ModBlocks.LIME_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> LIME_LAYERED_WOOL_LAYERS = registerBlockItem("lime_layered_wool_layers",
            ModBlocks.LIME_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> LIME_LAYERED_WOOL_VERTICAL_SLAB = registerBlockItem(
            "lime_layered_wool_vertical_slab", ModBlocks.LIME_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> PINK_LAYERED_WOOL_SLAB = registerBlockItem("pink_layered_wool_slab",
            ModBlocks.PINK_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> PINK_LAYERED_WOOL_STAIRS = registerBlockItem("pink_layered_wool_stairs",
            ModBlocks.PINK_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> PINK_LAYERED_WOOL_WALL = registerBlockItem("pink_layered_wool_wall",
            ModBlocks.PINK_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> PINK_LAYERED_WOOL_CARPET = registerBlockItem("pink_layered_wool_carpet",
            ModBlocks.PINK_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> PINK_LAYERED_WOOL_LAYERS = registerBlockItem("pink_layered_wool_layers",
            ModBlocks.PINK_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> PINK_LAYERED_WOOL_VERTICAL_SLAB = registerBlockItem(
            "pink_layered_wool_vertical_slab", ModBlocks.PINK_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL_SLAB = registerBlockItem("gray_layered_wool_slab",
            ModBlocks.GRAY_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL_STAIRS = registerBlockItem("gray_layered_wool_stairs",
            ModBlocks.GRAY_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL_WALL = registerBlockItem("gray_layered_wool_wall",
            ModBlocks.GRAY_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL_CARPET = registerBlockItem("gray_layered_wool_carpet",
            ModBlocks.GRAY_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL_LAYERS = registerBlockItem("gray_layered_wool_layers",
            ModBlocks.GRAY_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL_VERTICAL_SLAB = registerBlockItem(
            "gray_layered_wool_vertical_slab", ModBlocks.GRAY_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL_SLAB = registerBlockItem(
            "light_gray_layered_wool_slab", ModBlocks.LIGHT_GRAY_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL_STAIRS = registerBlockItem(
            "light_gray_layered_wool_stairs", ModBlocks.LIGHT_GRAY_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL_WALL = registerBlockItem(
            "light_gray_layered_wool_wall", ModBlocks.LIGHT_GRAY_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL_CARPET = registerBlockItem(
            "light_gray_layered_wool_carpet", ModBlocks.LIGHT_GRAY_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL_LAYERS = registerBlockItem(
            "light_gray_layered_wool_layers", ModBlocks.LIGHT_GRAY_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL_VERTICAL_SLAB = registerBlockItem(
            "light_gray_layered_wool_vertical_slab", ModBlocks.LIGHT_GRAY_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL_SLAB = registerBlockItem("cyan_layered_wool_slab",
            ModBlocks.CYAN_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL_STAIRS = registerBlockItem("cyan_layered_wool_stairs",
            ModBlocks.CYAN_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL_WALL = registerBlockItem("cyan_layered_wool_wall",
            ModBlocks.CYAN_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL_CARPET = registerBlockItem("cyan_layered_wool_carpet",
            ModBlocks.CYAN_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL_LAYERS = registerBlockItem("cyan_layered_wool_layers",
            ModBlocks.CYAN_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL_VERTICAL_SLAB = registerBlockItem(
            "cyan_layered_wool_vertical_slab", ModBlocks.CYAN_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL_SLAB = registerBlockItem("purple_layered_wool_slab",
            ModBlocks.PURPLE_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL_STAIRS = registerBlockItem(
            "purple_layered_wool_stairs", ModBlocks.PURPLE_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL_WALL = registerBlockItem("purple_layered_wool_wall",
            ModBlocks.PURPLE_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL_CARPET = registerBlockItem(
            "purple_layered_wool_carpet", ModBlocks.PURPLE_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL_LAYERS = registerBlockItem(
            "purple_layered_wool_layers", ModBlocks.PURPLE_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL_VERTICAL_SLAB = registerBlockItem(
            "purple_layered_wool_vertical_slab", ModBlocks.PURPLE_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL_SLAB = registerBlockItem("blue_layered_wool_slab",
            ModBlocks.BLUE_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL_STAIRS = registerBlockItem("blue_layered_wool_stairs",
            ModBlocks.BLUE_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL_WALL = registerBlockItem("blue_layered_wool_wall",
            ModBlocks.BLUE_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL_CARPET = registerBlockItem("blue_layered_wool_carpet",
            ModBlocks.BLUE_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL_LAYERS = registerBlockItem("blue_layered_wool_layers",
            ModBlocks.BLUE_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL_VERTICAL_SLAB = registerBlockItem(
            "blue_layered_wool_vertical_slab", ModBlocks.BLUE_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL_SLAB = registerBlockItem("brown_layered_wool_slab",
            ModBlocks.BROWN_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL_STAIRS = registerBlockItem("brown_layered_wool_stairs",
            ModBlocks.BROWN_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL_WALL = registerBlockItem("brown_layered_wool_wall",
            ModBlocks.BROWN_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL_CARPET = registerBlockItem("brown_layered_wool_carpet",
            ModBlocks.BROWN_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL_LAYERS = registerBlockItem("brown_layered_wool_layers",
            ModBlocks.BROWN_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL_VERTICAL_SLAB = registerBlockItem(
            "brown_layered_wool_vertical_slab", ModBlocks.BROWN_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL_SLAB = registerBlockItem("green_layered_wool_slab",
            ModBlocks.GREEN_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL_STAIRS = registerBlockItem("green_layered_wool_stairs",
            ModBlocks.GREEN_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL_WALL = registerBlockItem("green_layered_wool_wall",
            ModBlocks.GREEN_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL_CARPET = registerBlockItem("green_layered_wool_carpet",
            ModBlocks.GREEN_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL_LAYERS = registerBlockItem("green_layered_wool_layers",
            ModBlocks.GREEN_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL_VERTICAL_SLAB = registerBlockItem(
            "green_layered_wool_vertical_slab", ModBlocks.GREEN_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_LAYERED_WOOL_SLAB = registerBlockItem("red_layered_wool_slab",
            ModBlocks.RED_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> RED_LAYERED_WOOL_STAIRS = registerBlockItem("red_layered_wool_stairs",
            ModBlocks.RED_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> RED_LAYERED_WOOL_WALL = registerBlockItem("red_layered_wool_wall",
            ModBlocks.RED_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> RED_LAYERED_WOOL_CARPET = registerBlockItem("red_layered_wool_carpet",
            ModBlocks.RED_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> RED_LAYERED_WOOL_LAYERS = registerBlockItem("red_layered_wool_layers",
            ModBlocks.RED_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> RED_LAYERED_WOOL_VERTICAL_SLAB = registerBlockItem(
            "red_layered_wool_vertical_slab", ModBlocks.RED_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL_SLAB = registerBlockItem("black_layered_wool_slab",
            ModBlocks.BLACK_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL_STAIRS = registerBlockItem("black_layered_wool_stairs",
            ModBlocks.BLACK_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL_WALL = registerBlockItem("black_layered_wool_wall",
            ModBlocks.BLACK_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL_CARPET = registerBlockItem("black_layered_wool_carpet",
            ModBlocks.BLACK_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL_LAYERS = registerBlockItem("black_layered_wool_layers",
            ModBlocks.BLACK_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL_VERTICAL_SLAB = registerBlockItem(
            "black_layered_wool_vertical_slab", ModBlocks.BLACK_LAYERED_WOOL_VERTICAL_SLAB);

    public static final RegistryObject<Item> FLAMING_STEEL_INGOT = ITEMS.register("flaming_steel_ingot",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB)));
    public static final RegistryObject<Item> FLAMING_STEEL_NUGGET = ITEMS.register("flaming_steel_nugget",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB)));
    public static final RegistryObject<Item> FLAMING_STEEL_BLOCK = registerBlockItem("flaming_steel_block",
            ModBlocks.FLAMING_STEEL_BLOCK);
    public static final RegistryObject<Item> FLAMING_STEEL_BLOCK_SLAB = registerBlockItem("flaming_steel_block_slab",
            ModBlocks.FLAMING_STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> FLAMING_STEEL_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "flaming_steel_block_vertical_slab", ModBlocks.FLAMING_STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> FLAMING_STEEL_BLOCK_STAIRS = registerBlockItem(
            "flaming_steel_block_stairs", ModBlocks.FLAMING_STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> FLAMING_STEEL_BLOCK_WALL = registerBlockItem("flaming_steel_block_wall",
            ModBlocks.FLAMING_STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> FLAMING_CUT_STEEL = registerBlockItem("flaming_cut_steel",
            ModBlocks.FLAMING_CUT_STEEL);
    public static final RegistryObject<Item> FLAMING_CUT_STEEL_SLAB = registerBlockItem("flaming_cut_steel_slab",
            ModBlocks.FLAMING_CUT_STEEL_SLAB);
    public static final RegistryObject<Item> FLAMING_CUT_STEEL_VERTICAL_SLAB = registerBlockItem(
            "flaming_cut_steel_vertical_slab", ModBlocks.FLAMING_CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> FLAMING_CUT_STEEL_STAIRS = registerBlockItem("flaming_cut_steel_stairs",
            ModBlocks.FLAMING_CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> FLAMING_CUT_STEEL_WALL = registerBlockItem("flaming_cut_steel_wall",
            ModBlocks.FLAMING_CUT_STEEL_WALL);
    public static final RegistryObject<Item> FLAMING_POLISHED_STEEL = registerBlockItem("flaming_polished_steel",
            ModBlocks.FLAMING_POLISHED_STEEL);
    public static final RegistryObject<Item> FLAMING_POLISHED_STEEL_SLAB = registerBlockItem(
            "flaming_polished_steel_slab", ModBlocks.FLAMING_POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> FLAMING_POLISHED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "flaming_polished_steel_vertical_slab", ModBlocks.FLAMING_POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> FLAMING_POLISHED_STEEL_STAIRS = registerBlockItem(
            "flaming_polished_steel_stairs", ModBlocks.FLAMING_POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> FLAMING_POLISHED_STEEL_WALL = registerBlockItem(
            "flaming_polished_steel_wall", ModBlocks.FLAMING_POLISHED_STEEL_WALL);
    public static final RegistryObject<Item> FLAMING_PRESSED_STEEL = registerBlockItem("flaming_pressed_steel",
            ModBlocks.FLAMING_PRESSED_STEEL);
    public static final RegistryObject<Item> FLAMING_PRESSED_STEEL_SLAB = registerBlockItem(
            "flaming_pressed_steel_slab", ModBlocks.FLAMING_PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> FLAMING_PRESSED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "flaming_pressed_steel_vertical_slab", ModBlocks.FLAMING_PRESSED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> FLAMING_PRESSED_STEEL_STAIRS = registerBlockItem(
            "flaming_pressed_steel_stairs", ModBlocks.FLAMING_PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> FLAMING_PRESSED_STEEL_WALL = registerBlockItem(
            "flaming_pressed_steel_wall", ModBlocks.FLAMING_PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> FLAMING_STEEL_CASING = registerBlockItem("flaming_steel_casing",
            ModBlocks.FLAMING_STEEL_CASING);
    public static final RegistryObject<Item> FLAMING_STEEL_GRATE = registerBlockItem("flaming_steel_grate",
            ModBlocks.FLAMING_STEEL_GRATE);
    public static final RegistryObject<Item> FLAMING_STEEL_PILLAR = registerBlockItem("flaming_steel_pillar",
            ModBlocks.FLAMING_STEEL_PILLAR);
    public static final RegistryObject<Item> FLAMING_BOLTED_STEEL_PILLAR = registerBlockItem(
            "flaming_bolted_steel_pillar", ModBlocks.FLAMING_BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> FLAMING_STEEL_BOLTS = registerBlockItem("flaming_steel_bolts",
            ModBlocks.FLAMING_STEEL_BOLTS);
    public static final RegistryObject<Item> FLAMING_STEEL_DOOR = ITEMS.register("flaming_steel_door",
            () -> new DoubleHighBlockItem(ModBlocks.FLAMING_STEEL_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FLAMING_STEEL_TRAPDOOR = registerBlockItem("flaming_steel_trapdoor",
            ModBlocks.FLAMING_STEEL_TRAPDOOR);
    public static final RegistryObject<Item> FLAMING_STEEL_BUTTON = registerBlockItem("flaming_steel_button",
            ModBlocks.FLAMING_STEEL_BUTTON);
    public static final RegistryObject<Item> FLAMING_STEEL_PRESSURE_PLATE = registerBlockItem(
            "flaming_steel_pressure_plate", ModBlocks.FLAMING_STEEL_PRESSURE_PLATE);
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_BLOCK = registerBlockItem(
            "magenta_flaming_steel_block", ModBlocks.MAGENTA_FLAMING_STEEL_BLOCK);
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_BLOCK_SLAB = registerBlockItem(
            "magenta_flaming_steel_block_slab", ModBlocks.MAGENTA_FLAMING_STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "magenta_flaming_steel_block_vertical_slab", ModBlocks.MAGENTA_FLAMING_STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_BLOCK_STAIRS = registerBlockItem(
            "magenta_flaming_steel_block_stairs", ModBlocks.MAGENTA_FLAMING_STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_BLOCK_WALL = registerBlockItem(
            "magenta_flaming_steel_block_wall", ModBlocks.MAGENTA_FLAMING_STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> MAGENTA_FLAMING_CUT_STEEL = registerBlockItem("magenta_flaming_cut_steel",
            ModBlocks.MAGENTA_FLAMING_CUT_STEEL);
    public static final RegistryObject<Item> MAGENTA_FLAMING_CUT_STEEL_SLAB = registerBlockItem(
            "magenta_flaming_cut_steel_slab", ModBlocks.MAGENTA_FLAMING_CUT_STEEL_SLAB);
    public static final RegistryObject<Item> MAGENTA_FLAMING_CUT_STEEL_VERTICAL_SLAB = registerBlockItem(
            "magenta_flaming_cut_steel_vertical_slab", ModBlocks.MAGENTA_FLAMING_CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> MAGENTA_FLAMING_CUT_STEEL_STAIRS = registerBlockItem(
            "magenta_flaming_cut_steel_stairs", ModBlocks.MAGENTA_FLAMING_CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> MAGENTA_FLAMING_CUT_STEEL_WALL = registerBlockItem(
            "magenta_flaming_cut_steel_wall", ModBlocks.MAGENTA_FLAMING_CUT_STEEL_WALL);
    public static final RegistryObject<Item> MAGENTA_FLAMING_POLISHED_STEEL = registerBlockItem(
            "magenta_flaming_polished_steel", ModBlocks.MAGENTA_FLAMING_POLISHED_STEEL);
    public static final RegistryObject<Item> MAGENTA_FLAMING_POLISHED_STEEL_SLAB = registerBlockItem(
            "magenta_flaming_polished_steel_slab", ModBlocks.MAGENTA_FLAMING_POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> MAGENTA_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "magenta_flaming_polished_steel_vertical_slab", ModBlocks.MAGENTA_FLAMING_POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> MAGENTA_FLAMING_POLISHED_STEEL_STAIRS = registerBlockItem(
            "magenta_flaming_polished_steel_stairs", ModBlocks.MAGENTA_FLAMING_POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> MAGENTA_FLAMING_POLISHED_STEEL_WALL = registerBlockItem(
            "magenta_flaming_polished_steel_wall", ModBlocks.MAGENTA_FLAMING_POLISHED_STEEL_WALL);

    public static final RegistryObject<Item> MAGENTA_FLAMING_PRESSED_STEEL = registerBlockItem(
            "magenta_flaming_pressed_steel", ModBlocks.MAGENTA_FLAMING_PRESSED_STEEL);
    public static final RegistryObject<Item> MAGENTA_FLAMING_PRESSED_STEEL_SLAB = registerBlockItem(
            "magenta_flaming_pressed_steel_slab", ModBlocks.MAGENTA_FLAMING_PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> MAGENTA_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "magenta_flaming_pressed_steel_vertical_slab", ModBlocks.MAGENTA_FLAMING_PRESSED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> MAGENTA_FLAMING_PRESSED_STEEL_STAIRS = registerBlockItem(
            "magenta_flaming_pressed_steel_stairs", ModBlocks.MAGENTA_FLAMING_PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> MAGENTA_FLAMING_PRESSED_STEEL_WALL = registerBlockItem(
            "magenta_flaming_pressed_steel_wall", ModBlocks.MAGENTA_FLAMING_PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_CASING = registerBlockItem(
            "magenta_flaming_steel_casing", ModBlocks.MAGENTA_FLAMING_STEEL_CASING);
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_GRATE = registerBlockItem(
            "magenta_flaming_steel_grate", ModBlocks.MAGENTA_FLAMING_STEEL_GRATE);
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_PILLAR = registerBlockItem(
            "magenta_flaming_steel_pillar", ModBlocks.MAGENTA_FLAMING_STEEL_PILLAR);
    public static final RegistryObject<Item> MAGENTA_FLAMING_BOLTED_STEEL_PILLAR = registerBlockItem(
            "magenta_flaming_bolted_steel_pillar", ModBlocks.MAGENTA_FLAMING_BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_STEEL_BLOCK = registerBlockItem(
            "light_blue_flaming_steel_block", ModBlocks.LIGHT_BLUE_FLAMING_STEEL_BLOCK);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_STEEL_BLOCK_SLAB = registerBlockItem(
            "light_blue_flaming_steel_block_slab", ModBlocks.LIGHT_BLUE_FLAMING_STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "light_blue_flaming_steel_block_vertical_slab", ModBlocks.LIGHT_BLUE_FLAMING_STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_STEEL_BLOCK_STAIRS = registerBlockItem(
            "light_blue_flaming_steel_block_stairs", ModBlocks.LIGHT_BLUE_FLAMING_STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_STEEL_BLOCK_WALL = registerBlockItem(
            "light_blue_flaming_steel_block_wall", ModBlocks.LIGHT_BLUE_FLAMING_STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_CUT_STEEL = registerBlockItem(
            "light_blue_flaming_cut_steel", ModBlocks.LIGHT_BLUE_FLAMING_CUT_STEEL);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_CUT_STEEL_SLAB = registerBlockItem(
            "light_blue_flaming_cut_steel_slab", ModBlocks.LIGHT_BLUE_FLAMING_CUT_STEEL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_CUT_STEEL_VERTICAL_SLAB = registerBlockItem(
            "light_blue_flaming_cut_steel_vertical_slab", ModBlocks.LIGHT_BLUE_FLAMING_CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_CUT_STEEL_STAIRS = registerBlockItem(
            "light_blue_flaming_cut_steel_stairs", ModBlocks.LIGHT_BLUE_FLAMING_CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_CUT_STEEL_WALL = registerBlockItem(
            "light_blue_flaming_cut_steel_wall", ModBlocks.LIGHT_BLUE_FLAMING_CUT_STEEL_WALL);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_POLISHED_STEEL = registerBlockItem(
            "light_blue_flaming_polished_steel", ModBlocks.LIGHT_BLUE_FLAMING_POLISHED_STEEL);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_POLISHED_STEEL_SLAB = registerBlockItem(
            "light_blue_flaming_polished_steel_slab", ModBlocks.LIGHT_BLUE_FLAMING_POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "light_blue_flaming_polished_steel_vertical_slab",
            ModBlocks.LIGHT_BLUE_FLAMING_POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_POLISHED_STEEL_STAIRS = registerBlockItem(
            "light_blue_flaming_polished_steel_stairs", ModBlocks.LIGHT_BLUE_FLAMING_POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_POLISHED_STEEL_WALL = registerBlockItem(
            "light_blue_flaming_polished_steel_wall", ModBlocks.LIGHT_BLUE_FLAMING_POLISHED_STEEL_WALL);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_PRESSED_STEEL = registerBlockItem(
            "light_blue_flaming_pressed_steel", ModBlocks.LIGHT_BLUE_FLAMING_PRESSED_STEEL);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_PRESSED_STEEL_SLAB = registerBlockItem(
            "light_blue_flaming_pressed_steel_slab", ModBlocks.LIGHT_BLUE_FLAMING_PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "light_blue_flaming_pressed_steel_vertical_slab", ModBlocks.LIGHT_BLUE_FLAMING_PRESSED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_PRESSED_STEEL_STAIRS = registerBlockItem(
            "light_blue_flaming_pressed_steel_stairs", ModBlocks.LIGHT_BLUE_FLAMING_PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_PRESSED_STEEL_WALL = registerBlockItem(
            "light_blue_flaming_pressed_steel_wall", ModBlocks.LIGHT_BLUE_FLAMING_PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_STEEL_CASING = registerBlockItem(
            "light_blue_flaming_steel_casing", ModBlocks.LIGHT_BLUE_FLAMING_STEEL_CASING);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_STEEL_GRATE = registerBlockItem(
            "light_blue_flaming_steel_grate", ModBlocks.LIGHT_BLUE_FLAMING_STEEL_GRATE);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_STEEL_PILLAR = registerBlockItem(
            "light_blue_flaming_steel_pillar", ModBlocks.LIGHT_BLUE_FLAMING_STEEL_PILLAR);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_BOLTED_STEEL_PILLAR = registerBlockItem(
            "light_blue_flaming_bolted_steel_pillar", ModBlocks.LIGHT_BLUE_FLAMING_BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> YELLOW_FLAMING_STEEL_BLOCK = registerBlockItem(
            "yellow_flaming_steel_block", ModBlocks.YELLOW_FLAMING_STEEL_BLOCK);
    public static final RegistryObject<Item> YELLOW_FLAMING_STEEL_BLOCK_SLAB = registerBlockItem(
            "yellow_flaming_steel_block_slab", ModBlocks.YELLOW_FLAMING_STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> YELLOW_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "yellow_flaming_steel_block_vertical_slab", ModBlocks.YELLOW_FLAMING_STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> YELLOW_FLAMING_STEEL_BLOCK_STAIRS = registerBlockItem(
            "yellow_flaming_steel_block_stairs", ModBlocks.YELLOW_FLAMING_STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> YELLOW_FLAMING_STEEL_BLOCK_WALL = registerBlockItem(
            "yellow_flaming_steel_block_wall", ModBlocks.YELLOW_FLAMING_STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> YELLOW_FLAMING_CUT_STEEL = registerBlockItem("yellow_flaming_cut_steel",
            ModBlocks.YELLOW_FLAMING_CUT_STEEL);
    public static final RegistryObject<Item> YELLOW_FLAMING_CUT_STEEL_SLAB = registerBlockItem(
            "yellow_flaming_cut_steel_slab", ModBlocks.YELLOW_FLAMING_CUT_STEEL_SLAB);
    public static final RegistryObject<Item> YELLOW_FLAMING_CUT_STEEL_VERTICAL_SLAB = registerBlockItem(
            "yellow_flaming_cut_steel_vertical_slab", ModBlocks.YELLOW_FLAMING_CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> YELLOW_FLAMING_CUT_STEEL_STAIRS = registerBlockItem(
            "yellow_flaming_cut_steel_stairs", ModBlocks.YELLOW_FLAMING_CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> YELLOW_FLAMING_CUT_STEEL_WALL = registerBlockItem(
            "yellow_flaming_cut_steel_wall", ModBlocks.YELLOW_FLAMING_CUT_STEEL_WALL);
    public static final RegistryObject<Item> YELLOW_FLAMING_POLISHED_STEEL = registerBlockItem(
            "yellow_flaming_polished_steel", ModBlocks.YELLOW_FLAMING_POLISHED_STEEL);
    public static final RegistryObject<Item> YELLOW_FLAMING_POLISHED_STEEL_SLAB = registerBlockItem(
            "yellow_flaming_polished_steel_slab", ModBlocks.YELLOW_FLAMING_POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> YELLOW_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "yellow_flaming_polished_steel_vertical_slab", ModBlocks.YELLOW_FLAMING_POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> YELLOW_FLAMING_POLISHED_STEEL_STAIRS = registerBlockItem(
            "yellow_flaming_polished_steel_stairs", ModBlocks.YELLOW_FLAMING_POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> YELLOW_FLAMING_POLISHED_STEEL_WALL = registerBlockItem(
            "yellow_flaming_polished_steel_wall", ModBlocks.YELLOW_FLAMING_POLISHED_STEEL_WALL);
    public static final RegistryObject<Item> YELLOW_FLAMING_PRESSED_STEEL = registerBlockItem(
            "yellow_flaming_pressed_steel", ModBlocks.YELLOW_FLAMING_PRESSED_STEEL);
    public static final RegistryObject<Item> YELLOW_FLAMING_PRESSED_STEEL_SLAB = registerBlockItem(
            "yellow_flaming_pressed_steel_slab", ModBlocks.YELLOW_FLAMING_PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> YELLOW_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "yellow_flaming_pressed_steel_vertical_slab", ModBlocks.YELLOW_FLAMING_PRESSED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> YELLOW_FLAMING_PRESSED_STEEL_STAIRS = registerBlockItem(
            "yellow_flaming_pressed_steel_stairs", ModBlocks.YELLOW_FLAMING_PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> YELLOW_FLAMING_PRESSED_STEEL_WALL = registerBlockItem(
            "yellow_flaming_pressed_steel_wall", ModBlocks.YELLOW_FLAMING_PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> YELLOW_FLAMING_STEEL_CASING = registerBlockItem(
            "yellow_flaming_steel_casing", ModBlocks.YELLOW_FLAMING_STEEL_CASING);
    public static final RegistryObject<Item> YELLOW_FLAMING_STEEL_GRATE = registerBlockItem(
            "yellow_flaming_steel_grate", ModBlocks.YELLOW_FLAMING_STEEL_GRATE);
    public static final RegistryObject<Item> YELLOW_FLAMING_STEEL_PILLAR = registerBlockItem(
            "yellow_flaming_steel_pillar", ModBlocks.YELLOW_FLAMING_STEEL_PILLAR);
    public static final RegistryObject<Item> YELLOW_FLAMING_BOLTED_STEEL_PILLAR = registerBlockItem(
            "yellow_flaming_bolted_steel_pillar", ModBlocks.YELLOW_FLAMING_BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> LIME_FLAMING_STEEL_BLOCK = registerBlockItem("lime_flaming_steel_block",
            ModBlocks.LIME_FLAMING_STEEL_BLOCK);
    public static final RegistryObject<Item> LIME_FLAMING_STEEL_BLOCK_SLAB = registerBlockItem(
            "lime_flaming_steel_block_slab", ModBlocks.LIME_FLAMING_STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> LIME_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "lime_flaming_steel_block_vertical_slab", ModBlocks.LIME_FLAMING_STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIME_FLAMING_STEEL_BLOCK_STAIRS = registerBlockItem(
            "lime_flaming_steel_block_stairs", ModBlocks.LIME_FLAMING_STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> LIME_FLAMING_STEEL_BLOCK_WALL = registerBlockItem(
            "lime_flaming_steel_block_wall", ModBlocks.LIME_FLAMING_STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> LIME_FLAMING_CUT_STEEL = registerBlockItem("lime_flaming_cut_steel",
            ModBlocks.LIME_FLAMING_CUT_STEEL);
    public static final RegistryObject<Item> LIME_FLAMING_CUT_STEEL_SLAB = registerBlockItem(
            "lime_flaming_cut_steel_slab", ModBlocks.LIME_FLAMING_CUT_STEEL_SLAB);
    public static final RegistryObject<Item> LIME_FLAMING_CUT_STEEL_VERTICAL_SLAB = registerBlockItem(
            "lime_flaming_cut_steel_vertical_slab", ModBlocks.LIME_FLAMING_CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIME_FLAMING_CUT_STEEL_STAIRS = registerBlockItem(
            "lime_flaming_cut_steel_stairs", ModBlocks.LIME_FLAMING_CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> LIME_FLAMING_CUT_STEEL_WALL = registerBlockItem(
            "lime_flaming_cut_steel_wall", ModBlocks.LIME_FLAMING_CUT_STEEL_WALL);
    public static final RegistryObject<Item> LIME_FLAMING_POLISHED_STEEL = registerBlockItem(
            "lime_flaming_polished_steel", ModBlocks.LIME_FLAMING_POLISHED_STEEL);
    public static final RegistryObject<Item> LIME_FLAMING_POLISHED_STEEL_SLAB = registerBlockItem(
            "lime_flaming_polished_steel_slab", ModBlocks.LIME_FLAMING_POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> LIME_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "lime_flaming_polished_steel_vertical_slab", ModBlocks.LIME_FLAMING_POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIME_FLAMING_POLISHED_STEEL_STAIRS = registerBlockItem(
            "lime_flaming_polished_steel_stairs", ModBlocks.LIME_FLAMING_POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> LIME_FLAMING_POLISHED_STEEL_WALL = registerBlockItem(
            "lime_flaming_polished_steel_wall", ModBlocks.LIME_FLAMING_POLISHED_STEEL_WALL);
    public static final RegistryObject<Item> LIME_FLAMING_PRESSED_STEEL = registerBlockItem(
            "lime_flaming_pressed_steel", ModBlocks.LIME_FLAMING_PRESSED_STEEL);
    public static final RegistryObject<Item> LIME_FLAMING_PRESSED_STEEL_SLAB = registerBlockItem(
            "lime_flaming_pressed_steel_slab", ModBlocks.LIME_FLAMING_PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> LIME_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "lime_flaming_pressed_steel_vertical_slab", ModBlocks.LIME_FLAMING_PRESSED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIME_FLAMING_PRESSED_STEEL_STAIRS = registerBlockItem(
            "lime_flaming_pressed_steel_stairs", ModBlocks.LIME_FLAMING_PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> LIME_FLAMING_PRESSED_STEEL_WALL = registerBlockItem(
            "lime_flaming_pressed_steel_wall", ModBlocks.LIME_FLAMING_PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> LIME_FLAMING_STEEL_CASING = registerBlockItem("lime_flaming_steel_casing",
            ModBlocks.LIME_FLAMING_STEEL_CASING);
    public static final RegistryObject<Item> LIME_FLAMING_STEEL_GRATE = registerBlockItem("lime_flaming_steel_grate",
            ModBlocks.LIME_FLAMING_STEEL_GRATE);
    public static final RegistryObject<Item> LIME_FLAMING_STEEL_PILLAR = registerBlockItem("lime_flaming_steel_pillar",
            ModBlocks.LIME_FLAMING_STEEL_PILLAR);
    public static final RegistryObject<Item> LIME_FLAMING_BOLTED_STEEL_PILLAR = registerBlockItem(
            "lime_flaming_bolted_steel_pillar", ModBlocks.LIME_FLAMING_BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> PINK_FLAMING_STEEL_BLOCK = registerBlockItem("pink_flaming_steel_block",
            ModBlocks.PINK_FLAMING_STEEL_BLOCK);
    public static final RegistryObject<Item> PINK_FLAMING_STEEL_BLOCK_SLAB = registerBlockItem(
            "pink_flaming_steel_block_slab", ModBlocks.PINK_FLAMING_STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> PINK_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "pink_flaming_steel_block_vertical_slab", ModBlocks.PINK_FLAMING_STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> PINK_FLAMING_STEEL_BLOCK_STAIRS = registerBlockItem(
            "pink_flaming_steel_block_stairs", ModBlocks.PINK_FLAMING_STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> PINK_FLAMING_STEEL_BLOCK_WALL = registerBlockItem(
            "pink_flaming_steel_block_wall", ModBlocks.PINK_FLAMING_STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> PINK_FLAMING_CUT_STEEL = registerBlockItem("pink_flaming_cut_steel",
            ModBlocks.PINK_FLAMING_CUT_STEEL);
    public static final RegistryObject<Item> PINK_FLAMING_CUT_STEEL_SLAB = registerBlockItem(
            "pink_flaming_cut_steel_slab", ModBlocks.PINK_FLAMING_CUT_STEEL_SLAB);
    public static final RegistryObject<Item> PINK_FLAMING_CUT_STEEL_VERTICAL_SLAB = registerBlockItem(
            "pink_flaming_cut_steel_vertical_slab", ModBlocks.PINK_FLAMING_CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> PINK_FLAMING_CUT_STEEL_STAIRS = registerBlockItem(
            "pink_flaming_cut_steel_stairs", ModBlocks.PINK_FLAMING_CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> PINK_FLAMING_CUT_STEEL_WALL = registerBlockItem(
            "pink_flaming_cut_steel_wall", ModBlocks.PINK_FLAMING_CUT_STEEL_WALL);
    public static final RegistryObject<Item> PINK_FLAMING_POLISHED_STEEL = registerBlockItem(
            "pink_flaming_polished_steel", ModBlocks.PINK_FLAMING_POLISHED_STEEL);
    public static final RegistryObject<Item> PINK_FLAMING_POLISHED_STEEL_SLAB = registerBlockItem(
            "pink_flaming_polished_steel_slab", ModBlocks.PINK_FLAMING_POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> PINK_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "pink_flaming_polished_steel_vertical_slab", ModBlocks.PINK_FLAMING_POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> PINK_FLAMING_POLISHED_STEEL_STAIRS = registerBlockItem(
            "pink_flaming_polished_steel_stairs", ModBlocks.PINK_FLAMING_POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> PINK_FLAMING_POLISHED_STEEL_WALL = registerBlockItem(
            "pink_flaming_polished_steel_wall", ModBlocks.PINK_FLAMING_POLISHED_STEEL_WALL);
    public static final RegistryObject<Item> PINK_FLAMING_PRESSED_STEEL = registerBlockItem(
            "pink_flaming_pressed_steel", ModBlocks.PINK_FLAMING_PRESSED_STEEL);
    public static final RegistryObject<Item> PINK_FLAMING_PRESSED_STEEL_SLAB = registerBlockItem(
            "pink_flaming_pressed_steel_slab", ModBlocks.PINK_FLAMING_PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> PINK_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "pink_flaming_pressed_steel_vertical_slab", ModBlocks.PINK_FLAMING_PRESSED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> PINK_FLAMING_PRESSED_STEEL_STAIRS = registerBlockItem(
            "pink_flaming_pressed_steel_stairs", ModBlocks.PINK_FLAMING_PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> PINK_FLAMING_PRESSED_STEEL_WALL = registerBlockItem(
            "pink_flaming_pressed_steel_wall", ModBlocks.PINK_FLAMING_PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> PINK_FLAMING_STEEL_CASING = registerBlockItem("pink_flaming_steel_casing",
            ModBlocks.PINK_FLAMING_STEEL_CASING);
    public static final RegistryObject<Item> PINK_FLAMING_STEEL_GRATE = registerBlockItem("pink_flaming_steel_grate",
            ModBlocks.PINK_FLAMING_STEEL_GRATE);
    public static final RegistryObject<Item> PINK_FLAMING_STEEL_PILLAR = registerBlockItem("pink_flaming_steel_pillar",
            ModBlocks.PINK_FLAMING_STEEL_PILLAR);
    public static final RegistryObject<Item> PINK_FLAMING_BOLTED_STEEL_PILLAR = registerBlockItem(
            "pink_flaming_bolted_steel_pillar", ModBlocks.PINK_FLAMING_BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> GRAY_FLAMING_STEEL_BLOCK = registerBlockItem("gray_flaming_steel_block",
            ModBlocks.GRAY_FLAMING_STEEL_BLOCK);
    public static final RegistryObject<Item> GRAY_FLAMING_STEEL_BLOCK_SLAB = registerBlockItem(
            "gray_flaming_steel_block_slab", ModBlocks.GRAY_FLAMING_STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> GRAY_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "gray_flaming_steel_block_vertical_slab", ModBlocks.GRAY_FLAMING_STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> GRAY_FLAMING_STEEL_BLOCK_STAIRS = registerBlockItem(
            "gray_flaming_steel_block_stairs", ModBlocks.GRAY_FLAMING_STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> GRAY_FLAMING_STEEL_BLOCK_WALL = registerBlockItem(
            "gray_flaming_steel_block_wall", ModBlocks.GRAY_FLAMING_STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> GRAY_FLAMING_CUT_STEEL = registerBlockItem("gray_flaming_cut_steel",
            ModBlocks.GRAY_FLAMING_CUT_STEEL);
    public static final RegistryObject<Item> GRAY_FLAMING_CUT_STEEL_SLAB = registerBlockItem(
            "gray_flaming_cut_steel_slab", ModBlocks.GRAY_FLAMING_CUT_STEEL_SLAB);
    public static final RegistryObject<Item> GRAY_FLAMING_CUT_STEEL_VERTICAL_SLAB = registerBlockItem(
            "gray_flaming_cut_steel_vertical_slab", ModBlocks.GRAY_FLAMING_CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> GRAY_FLAMING_CUT_STEEL_STAIRS = registerBlockItem(
            "gray_flaming_cut_steel_stairs", ModBlocks.GRAY_FLAMING_CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> GRAY_FLAMING_CUT_STEEL_WALL = registerBlockItem(
            "gray_flaming_cut_steel_wall", ModBlocks.GRAY_FLAMING_CUT_STEEL_WALL);
    public static final RegistryObject<Item> GRAY_FLAMING_POLISHED_STEEL = registerBlockItem(
            "gray_flaming_polished_steel", ModBlocks.GRAY_FLAMING_POLISHED_STEEL);
    public static final RegistryObject<Item> GRAY_FLAMING_POLISHED_STEEL_SLAB = registerBlockItem(
            "gray_flaming_polished_steel_slab", ModBlocks.GRAY_FLAMING_POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> GRAY_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "gray_flaming_polished_steel_vertical_slab", ModBlocks.GRAY_FLAMING_POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> GRAY_FLAMING_POLISHED_STEEL_STAIRS = registerBlockItem(
            "gray_flaming_polished_steel_stairs", ModBlocks.GRAY_FLAMING_POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> GRAY_FLAMING_POLISHED_STEEL_WALL = registerBlockItem(
            "gray_flaming_polished_steel_wall", ModBlocks.GRAY_FLAMING_POLISHED_STEEL_WALL);
    public static final RegistryObject<Item> GRAY_FLAMING_PRESSED_STEEL = registerBlockItem(
            "gray_flaming_pressed_steel", ModBlocks.GRAY_FLAMING_PRESSED_STEEL);
    public static final RegistryObject<Item> GRAY_FLAMING_PRESSED_STEEL_SLAB = registerBlockItem(
            "gray_flaming_pressed_steel_slab", ModBlocks.GRAY_FLAMING_PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> GRAY_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "gray_flaming_pressed_steel_vertical_slab", ModBlocks.GRAY_FLAMING_PRESSED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> GRAY_FLAMING_PRESSED_STEEL_STAIRS = registerBlockItem(
            "gray_flaming_pressed_steel_stairs", ModBlocks.GRAY_FLAMING_PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> GRAY_FLAMING_PRESSED_STEEL_WALL = registerBlockItem(
            "gray_flaming_pressed_steel_wall", ModBlocks.GRAY_FLAMING_PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> GRAY_FLAMING_STEEL_CASING = registerBlockItem("gray_flaming_steel_casing",
            ModBlocks.GRAY_FLAMING_STEEL_CASING);
    public static final RegistryObject<Item> GRAY_FLAMING_STEEL_GRATE = registerBlockItem("gray_flaming_steel_grate",
            ModBlocks.GRAY_FLAMING_STEEL_GRATE);
    public static final RegistryObject<Item> GRAY_FLAMING_STEEL_PILLAR = registerBlockItem("gray_flaming_steel_pillar",
            ModBlocks.GRAY_FLAMING_STEEL_PILLAR);
    public static final RegistryObject<Item> GRAY_FLAMING_BOLTED_STEEL_PILLAR = registerBlockItem(
            "gray_flaming_bolted_steel_pillar", ModBlocks.GRAY_FLAMING_BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_STEEL_BLOCK = registerBlockItem(
            "light_gray_flaming_steel_block", ModBlocks.LIGHT_GRAY_FLAMING_STEEL_BLOCK);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_STEEL_BLOCK_SLAB = registerBlockItem(
            "light_gray_flaming_steel_block_slab", ModBlocks.LIGHT_GRAY_FLAMING_STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "light_gray_flaming_steel_block_vertical_slab", ModBlocks.LIGHT_GRAY_FLAMING_STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_STEEL_BLOCK_STAIRS = registerBlockItem(
            "light_gray_flaming_steel_block_stairs", ModBlocks.LIGHT_GRAY_FLAMING_STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_STEEL_BLOCK_WALL = registerBlockItem(
            "light_gray_flaming_steel_block_wall", ModBlocks.LIGHT_GRAY_FLAMING_STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_CUT_STEEL = registerBlockItem(
            "light_gray_flaming_cut_steel", ModBlocks.LIGHT_GRAY_FLAMING_CUT_STEEL);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_CUT_STEEL_SLAB = registerBlockItem(
            "light_gray_flaming_cut_steel_slab", ModBlocks.LIGHT_GRAY_FLAMING_CUT_STEEL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_CUT_STEEL_VERTICAL_SLAB = registerBlockItem(
            "light_gray_flaming_cut_steel_vertical_slab", ModBlocks.LIGHT_GRAY_FLAMING_CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_CUT_STEEL_STAIRS = registerBlockItem(
            "light_gray_flaming_cut_steel_stairs", ModBlocks.LIGHT_GRAY_FLAMING_CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_CUT_STEEL_WALL = registerBlockItem(
            "light_gray_flaming_cut_steel_wall", ModBlocks.LIGHT_GRAY_FLAMING_CUT_STEEL_WALL);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_POLISHED_STEEL = registerBlockItem(
            "light_gray_flaming_polished_steel", ModBlocks.LIGHT_GRAY_FLAMING_POLISHED_STEEL);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_POLISHED_STEEL_SLAB = registerBlockItem(
            "light_gray_flaming_polished_steel_slab", ModBlocks.LIGHT_GRAY_FLAMING_POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "light_gray_flaming_polished_steel_vertical_slab",
            ModBlocks.LIGHT_GRAY_FLAMING_POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_POLISHED_STEEL_STAIRS = registerBlockItem(
            "light_gray_flaming_polished_steel_stairs", ModBlocks.LIGHT_GRAY_FLAMING_POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_POLISHED_STEEL_WALL = registerBlockItem(
            "light_gray_flaming_polished_steel_wall", ModBlocks.LIGHT_GRAY_FLAMING_POLISHED_STEEL_WALL);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_PRESSED_STEEL = registerBlockItem(
            "light_gray_flaming_pressed_steel", ModBlocks.LIGHT_GRAY_FLAMING_PRESSED_STEEL);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_PRESSED_STEEL_SLAB = registerBlockItem(
            "light_gray_flaming_pressed_steel_slab", ModBlocks.LIGHT_GRAY_FLAMING_PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "light_gray_flaming_pressed_steel_vertical_slab", ModBlocks.LIGHT_GRAY_FLAMING_PRESSED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_PRESSED_STEEL_STAIRS = registerBlockItem(
            "light_gray_flaming_pressed_steel_stairs", ModBlocks.LIGHT_GRAY_FLAMING_PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_PRESSED_STEEL_WALL = registerBlockItem(
            "light_gray_flaming_pressed_steel_wall", ModBlocks.LIGHT_GRAY_FLAMING_PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_STEEL_CASING = registerBlockItem(
            "light_gray_flaming_steel_casing", ModBlocks.LIGHT_GRAY_FLAMING_STEEL_CASING);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_STEEL_GRATE = registerBlockItem(
            "light_gray_flaming_steel_grate", ModBlocks.LIGHT_GRAY_FLAMING_STEEL_GRATE);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_STEEL_PILLAR = registerBlockItem(
            "light_gray_flaming_steel_pillar", ModBlocks.LIGHT_GRAY_FLAMING_STEEL_PILLAR);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_BOLTED_STEEL_PILLAR = registerBlockItem(
            "light_gray_flaming_bolted_steel_pillar", ModBlocks.LIGHT_GRAY_FLAMING_BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> CYAN_FLAMING_STEEL_BLOCK = registerBlockItem("cyan_flaming_steel_block",
            ModBlocks.CYAN_FLAMING_STEEL_BLOCK);
    public static final RegistryObject<Item> CYAN_FLAMING_STEEL_BLOCK_SLAB = registerBlockItem(
            "cyan_flaming_steel_block_slab", ModBlocks.CYAN_FLAMING_STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> CYAN_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "cyan_flaming_steel_block_vertical_slab", ModBlocks.CYAN_FLAMING_STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> CYAN_FLAMING_STEEL_BLOCK_STAIRS = registerBlockItem(
            "cyan_flaming_steel_block_stairs", ModBlocks.CYAN_FLAMING_STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> CYAN_FLAMING_STEEL_BLOCK_WALL = registerBlockItem(
            "cyan_flaming_steel_block_wall", ModBlocks.CYAN_FLAMING_STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> CYAN_FLAMING_CUT_STEEL = registerBlockItem("cyan_flaming_cut_steel",
            ModBlocks.CYAN_FLAMING_CUT_STEEL);
    public static final RegistryObject<Item> CYAN_FLAMING_CUT_STEEL_SLAB = registerBlockItem(
            "cyan_flaming_cut_steel_slab", ModBlocks.CYAN_FLAMING_CUT_STEEL_SLAB);
    public static final RegistryObject<Item> CYAN_FLAMING_CUT_STEEL_VERTICAL_SLAB = registerBlockItem(
            "cyan_flaming_cut_steel_vertical_slab", ModBlocks.CYAN_FLAMING_CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> CYAN_FLAMING_CUT_STEEL_STAIRS = registerBlockItem(
            "cyan_flaming_cut_steel_stairs", ModBlocks.CYAN_FLAMING_CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> CYAN_FLAMING_CUT_STEEL_WALL = registerBlockItem(
            "cyan_flaming_cut_steel_wall", ModBlocks.CYAN_FLAMING_CUT_STEEL_WALL);
    public static final RegistryObject<Item> CYAN_FLAMING_POLISHED_STEEL = registerBlockItem(
            "cyan_flaming_polished_steel", ModBlocks.CYAN_FLAMING_POLISHED_STEEL);
    public static final RegistryObject<Item> CYAN_FLAMING_POLISHED_STEEL_SLAB = registerBlockItem(
            "cyan_flaming_polished_steel_slab", ModBlocks.CYAN_FLAMING_POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> CYAN_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "cyan_flaming_polished_steel_vertical_slab", ModBlocks.CYAN_FLAMING_POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> CYAN_FLAMING_POLISHED_STEEL_STAIRS = registerBlockItem(
            "cyan_flaming_polished_steel_stairs", ModBlocks.CYAN_FLAMING_POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> CYAN_FLAMING_POLISHED_STEEL_WALL = registerBlockItem(
            "cyan_flaming_polished_steel_wall", ModBlocks.CYAN_FLAMING_POLISHED_STEEL_WALL);
    public static final RegistryObject<Item> CYAN_FLAMING_PRESSED_STEEL = registerBlockItem(
            "cyan_flaming_pressed_steel", ModBlocks.CYAN_FLAMING_PRESSED_STEEL);
    public static final RegistryObject<Item> CYAN_FLAMING_PRESSED_STEEL_SLAB = registerBlockItem(
            "cyan_flaming_pressed_steel_slab", ModBlocks.CYAN_FLAMING_PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> CYAN_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "cyan_flaming_pressed_steel_vertical_slab", ModBlocks.CYAN_FLAMING_PRESSED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> CYAN_FLAMING_PRESSED_STEEL_STAIRS = registerBlockItem(
            "cyan_flaming_pressed_steel_stairs", ModBlocks.CYAN_FLAMING_PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> CYAN_FLAMING_PRESSED_STEEL_WALL = registerBlockItem(
            "cyan_flaming_pressed_steel_wall", ModBlocks.CYAN_FLAMING_PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> CYAN_FLAMING_STEEL_CASING = registerBlockItem("cyan_flaming_steel_casing",
            ModBlocks.CYAN_FLAMING_STEEL_CASING);
    public static final RegistryObject<Item> CYAN_FLAMING_STEEL_GRATE = registerBlockItem("cyan_flaming_steel_grate",
            ModBlocks.CYAN_FLAMING_STEEL_GRATE);
    public static final RegistryObject<Item> CYAN_FLAMING_STEEL_PILLAR = registerBlockItem("cyan_flaming_steel_pillar",
            ModBlocks.CYAN_FLAMING_STEEL_PILLAR);
    public static final RegistryObject<Item> CYAN_FLAMING_BOLTED_STEEL_PILLAR = registerBlockItem(
            "cyan_flaming_bolted_steel_pillar", ModBlocks.CYAN_FLAMING_BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> PURPLE_FLAMING_STEEL_BLOCK = registerBlockItem(
            "purple_flaming_steel_block", ModBlocks.PURPLE_FLAMING_STEEL_BLOCK);
    public static final RegistryObject<Item> PURPLE_FLAMING_STEEL_BLOCK_SLAB = registerBlockItem(
            "purple_flaming_steel_block_slab", ModBlocks.PURPLE_FLAMING_STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> PURPLE_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "purple_flaming_steel_block_vertical_slab", ModBlocks.PURPLE_FLAMING_STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> PURPLE_FLAMING_STEEL_BLOCK_STAIRS = registerBlockItem(
            "purple_flaming_steel_block_stairs", ModBlocks.PURPLE_FLAMING_STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> PURPLE_FLAMING_STEEL_BLOCK_WALL = registerBlockItem(
            "purple_flaming_steel_block_wall", ModBlocks.PURPLE_FLAMING_STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> PURPLE_FLAMING_CUT_STEEL = registerBlockItem("purple_flaming_cut_steel",
            ModBlocks.PURPLE_FLAMING_CUT_STEEL);
    public static final RegistryObject<Item> PURPLE_FLAMING_CUT_STEEL_SLAB = registerBlockItem(
            "purple_flaming_cut_steel_slab", ModBlocks.PURPLE_FLAMING_CUT_STEEL_SLAB);
    public static final RegistryObject<Item> PURPLE_FLAMING_CUT_STEEL_VERTICAL_SLAB = registerBlockItem(
            "purple_flaming_cut_steel_vertical_slab", ModBlocks.PURPLE_FLAMING_CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> PURPLE_FLAMING_CUT_STEEL_STAIRS = registerBlockItem(
            "purple_flaming_cut_steel_stairs", ModBlocks.PURPLE_FLAMING_CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> PURPLE_FLAMING_CUT_STEEL_WALL = registerBlockItem(
            "purple_flaming_cut_steel_wall", ModBlocks.PURPLE_FLAMING_CUT_STEEL_WALL);
    public static final RegistryObject<Item> PURPLE_FLAMING_POLISHED_STEEL = registerBlockItem(
            "purple_flaming_polished_steel", ModBlocks.PURPLE_FLAMING_POLISHED_STEEL);
    public static final RegistryObject<Item> PURPLE_FLAMING_POLISHED_STEEL_SLAB = registerBlockItem(
            "purple_flaming_polished_steel_slab", ModBlocks.PURPLE_FLAMING_POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> PURPLE_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "purple_flaming_polished_steel_vertical_slab", ModBlocks.PURPLE_FLAMING_POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> PURPLE_FLAMING_POLISHED_STEEL_STAIRS = registerBlockItem(
            "purple_flaming_polished_steel_stairs", ModBlocks.PURPLE_FLAMING_POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> PURPLE_FLAMING_POLISHED_STEEL_WALL = registerBlockItem(
            "purple_flaming_polished_steel_wall", ModBlocks.PURPLE_FLAMING_POLISHED_STEEL_WALL);
    public static final RegistryObject<Item> PURPLE_FLAMING_PRESSED_STEEL = registerBlockItem(
            "purple_flaming_pressed_steel", ModBlocks.PURPLE_FLAMING_PRESSED_STEEL);
    public static final RegistryObject<Item> PURPLE_FLAMING_PRESSED_STEEL_SLAB = registerBlockItem(
            "purple_flaming_pressed_steel_slab", ModBlocks.PURPLE_FLAMING_PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> PURPLE_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "purple_flaming_pressed_steel_vertical_slab", ModBlocks.PURPLE_FLAMING_PRESSED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> PURPLE_FLAMING_PRESSED_STEEL_STAIRS = registerBlockItem(
            "purple_flaming_pressed_steel_stairs", ModBlocks.PURPLE_FLAMING_PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> PURPLE_FLAMING_PRESSED_STEEL_WALL = registerBlockItem(
            "purple_flaming_pressed_steel_wall", ModBlocks.PURPLE_FLAMING_PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> PURPLE_FLAMING_STEEL_CASING = registerBlockItem(
            "purple_flaming_steel_casing", ModBlocks.PURPLE_FLAMING_STEEL_CASING);
    public static final RegistryObject<Item> PURPLE_FLAMING_STEEL_GRATE = registerBlockItem(
            "purple_flaming_steel_grate", ModBlocks.PURPLE_FLAMING_STEEL_GRATE);
    public static final RegistryObject<Item> PURPLE_FLAMING_STEEL_PILLAR = registerBlockItem(
            "purple_flaming_steel_pillar", ModBlocks.PURPLE_FLAMING_STEEL_PILLAR);
    public static final RegistryObject<Item> PURPLE_FLAMING_BOLTED_STEEL_PILLAR = registerBlockItem(
            "purple_flaming_bolted_steel_pillar", ModBlocks.PURPLE_FLAMING_BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> BLUE_FLAMING_STEEL_BLOCK = registerBlockItem("blue_flaming_steel_block",
            ModBlocks.BLUE_FLAMING_STEEL_BLOCK);
    public static final RegistryObject<Item> BLUE_FLAMING_STEEL_BLOCK_SLAB = registerBlockItem(
            "blue_flaming_steel_block_slab", ModBlocks.BLUE_FLAMING_STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> BLUE_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "blue_flaming_steel_block_vertical_slab", ModBlocks.BLUE_FLAMING_STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLUE_FLAMING_STEEL_BLOCK_STAIRS = registerBlockItem(
            "blue_flaming_steel_block_stairs", ModBlocks.BLUE_FLAMING_STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> BLUE_FLAMING_STEEL_BLOCK_WALL = registerBlockItem(
            "blue_flaming_steel_block_wall", ModBlocks.BLUE_FLAMING_STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> BLUE_FLAMING_CUT_STEEL = registerBlockItem("blue_flaming_cut_steel",
            ModBlocks.BLUE_FLAMING_CUT_STEEL);
    public static final RegistryObject<Item> BLUE_FLAMING_CUT_STEEL_SLAB = registerBlockItem(
            "blue_flaming_cut_steel_slab", ModBlocks.BLUE_FLAMING_CUT_STEEL_SLAB);
    public static final RegistryObject<Item> BLUE_FLAMING_CUT_STEEL_VERTICAL_SLAB = registerBlockItem(
            "blue_flaming_cut_steel_vertical_slab", ModBlocks.BLUE_FLAMING_CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLUE_FLAMING_CUT_STEEL_STAIRS = registerBlockItem(
            "blue_flaming_cut_steel_stairs", ModBlocks.BLUE_FLAMING_CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> BLUE_FLAMING_CUT_STEEL_WALL = registerBlockItem(
            "blue_flaming_cut_steel_wall", ModBlocks.BLUE_FLAMING_CUT_STEEL_WALL);
    public static final RegistryObject<Item> BLUE_FLAMING_POLISHED_STEEL = registerBlockItem(
            "blue_flaming_polished_steel", ModBlocks.BLUE_FLAMING_POLISHED_STEEL);
    public static final RegistryObject<Item> BLUE_FLAMING_POLISHED_STEEL_SLAB = registerBlockItem(
            "blue_flaming_polished_steel_slab", ModBlocks.BLUE_FLAMING_POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> BLUE_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "blue_flaming_polished_steel_vertical_slab", ModBlocks.BLUE_FLAMING_POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLUE_FLAMING_POLISHED_STEEL_STAIRS = registerBlockItem(
            "blue_flaming_polished_steel_stairs", ModBlocks.BLUE_FLAMING_POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> BLUE_FLAMING_POLISHED_STEEL_WALL = registerBlockItem(
            "blue_flaming_polished_steel_wall", ModBlocks.BLUE_FLAMING_POLISHED_STEEL_WALL);
    public static final RegistryObject<Item> BLUE_FLAMING_PRESSED_STEEL = registerBlockItem(
            "blue_flaming_pressed_steel", ModBlocks.BLUE_FLAMING_PRESSED_STEEL);
    public static final RegistryObject<Item> BLUE_FLAMING_PRESSED_STEEL_SLAB = registerBlockItem(
            "blue_flaming_pressed_steel_slab", ModBlocks.BLUE_FLAMING_PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> BLUE_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "blue_flaming_pressed_steel_vertical_slab", ModBlocks.BLUE_FLAMING_PRESSED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLUE_FLAMING_PRESSED_STEEL_STAIRS = registerBlockItem(
            "blue_flaming_pressed_steel_stairs", ModBlocks.BLUE_FLAMING_PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> BLUE_FLAMING_PRESSED_STEEL_WALL = registerBlockItem(
            "blue_flaming_pressed_steel_wall", ModBlocks.BLUE_FLAMING_PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> BLUE_FLAMING_STEEL_CASING = registerBlockItem("blue_flaming_steel_casing",
            ModBlocks.BLUE_FLAMING_STEEL_CASING);
    public static final RegistryObject<Item> BLUE_FLAMING_STEEL_GRATE = registerBlockItem("blue_flaming_steel_grate",
            ModBlocks.BLUE_FLAMING_STEEL_GRATE);
    public static final RegistryObject<Item> BLUE_FLAMING_STEEL_PILLAR = registerBlockItem("blue_flaming_steel_pillar",
            ModBlocks.BLUE_FLAMING_STEEL_PILLAR);
    public static final RegistryObject<Item> BLUE_FLAMING_BOLTED_STEEL_PILLAR = registerBlockItem(
            "blue_flaming_bolted_steel_pillar", ModBlocks.BLUE_FLAMING_BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> BROWN_FLAMING_STEEL_BLOCK = registerBlockItem("brown_flaming_steel_block",
            ModBlocks.BROWN_FLAMING_STEEL_BLOCK);
    public static final RegistryObject<Item> BROWN_FLAMING_STEEL_BLOCK_SLAB = registerBlockItem(
            "brown_flaming_steel_block_slab", ModBlocks.BROWN_FLAMING_STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> BROWN_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "brown_flaming_steel_block_vertical_slab", ModBlocks.BROWN_FLAMING_STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> BROWN_FLAMING_STEEL_BLOCK_STAIRS = registerBlockItem(
            "brown_flaming_steel_block_stairs", ModBlocks.BROWN_FLAMING_STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> BROWN_FLAMING_STEEL_BLOCK_WALL = registerBlockItem(
            "brown_flaming_steel_block_wall", ModBlocks.BROWN_FLAMING_STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> BROWN_FLAMING_CUT_STEEL = registerBlockItem("brown_flaming_cut_steel",
            ModBlocks.BROWN_FLAMING_CUT_STEEL);
    public static final RegistryObject<Item> BROWN_FLAMING_CUT_STEEL_SLAB = registerBlockItem(
            "brown_flaming_cut_steel_slab", ModBlocks.BROWN_FLAMING_CUT_STEEL_SLAB);
    public static final RegistryObject<Item> BROWN_FLAMING_CUT_STEEL_VERTICAL_SLAB = registerBlockItem(
            "brown_flaming_cut_steel_vertical_slab", ModBlocks.BROWN_FLAMING_CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BROWN_FLAMING_CUT_STEEL_STAIRS = registerBlockItem(
            "brown_flaming_cut_steel_stairs", ModBlocks.BROWN_FLAMING_CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> BROWN_FLAMING_CUT_STEEL_WALL = registerBlockItem(
            "brown_flaming_cut_steel_wall", ModBlocks.BROWN_FLAMING_CUT_STEEL_WALL);
    public static final RegistryObject<Item> BROWN_FLAMING_POLISHED_STEEL = registerBlockItem(
            "brown_flaming_polished_steel", ModBlocks.BROWN_FLAMING_POLISHED_STEEL);
    public static final RegistryObject<Item> BROWN_FLAMING_POLISHED_STEEL_SLAB = registerBlockItem(
            "brown_flaming_polished_steel_slab", ModBlocks.BROWN_FLAMING_POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> BROWN_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "brown_flaming_polished_steel_vertical_slab", ModBlocks.BROWN_FLAMING_POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BROWN_FLAMING_POLISHED_STEEL_STAIRS = registerBlockItem(
            "brown_flaming_polished_steel_stairs", ModBlocks.BROWN_FLAMING_POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> BROWN_FLAMING_POLISHED_STEEL_WALL = registerBlockItem(
            "brown_flaming_polished_steel_wall", ModBlocks.BROWN_FLAMING_POLISHED_STEEL_WALL);
    public static final RegistryObject<Item> BROWN_FLAMING_PRESSED_STEEL = registerBlockItem(
            "brown_flaming_pressed_steel", ModBlocks.BROWN_FLAMING_PRESSED_STEEL);
    public static final RegistryObject<Item> BROWN_FLAMING_PRESSED_STEEL_SLAB = registerBlockItem(
            "brown_flaming_pressed_steel_slab", ModBlocks.BROWN_FLAMING_PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> BROWN_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "brown_flaming_pressed_steel_vertical_slab", ModBlocks.BROWN_FLAMING_PRESSED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BROWN_FLAMING_PRESSED_STEEL_STAIRS = registerBlockItem(
            "brown_flaming_pressed_steel_stairs", ModBlocks.BROWN_FLAMING_PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> BROWN_FLAMING_PRESSED_STEEL_WALL = registerBlockItem(
            "brown_flaming_pressed_steel_wall", ModBlocks.BROWN_FLAMING_PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> BROWN_FLAMING_STEEL_CASING = registerBlockItem(
            "brown_flaming_steel_casing", ModBlocks.BROWN_FLAMING_STEEL_CASING);
    public static final RegistryObject<Item> BROWN_FLAMING_STEEL_GRATE = registerBlockItem("brown_flaming_steel_grate",
            ModBlocks.BROWN_FLAMING_STEEL_GRATE);
    public static final RegistryObject<Item> BROWN_FLAMING_STEEL_PILLAR = registerBlockItem(
            "brown_flaming_steel_pillar", ModBlocks.BROWN_FLAMING_STEEL_PILLAR);
    public static final RegistryObject<Item> BROWN_FLAMING_BOLTED_STEEL_PILLAR = registerBlockItem(
            "brown_flaming_bolted_steel_pillar", ModBlocks.BROWN_FLAMING_BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> GREEN_FLAMING_STEEL_BLOCK = registerBlockItem("green_flaming_steel_block",
            ModBlocks.GREEN_FLAMING_STEEL_BLOCK);
    public static final RegistryObject<Item> GREEN_FLAMING_STEEL_BLOCK_SLAB = registerBlockItem(
            "green_flaming_steel_block_slab", ModBlocks.GREEN_FLAMING_STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> GREEN_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "green_flaming_steel_block_vertical_slab", ModBlocks.GREEN_FLAMING_STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> GREEN_FLAMING_STEEL_BLOCK_STAIRS = registerBlockItem(
            "green_flaming_steel_block_stairs", ModBlocks.GREEN_FLAMING_STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> GREEN_FLAMING_STEEL_BLOCK_WALL = registerBlockItem(
            "green_flaming_steel_block_wall", ModBlocks.GREEN_FLAMING_STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> GREEN_FLAMING_CUT_STEEL = registerBlockItem("green_flaming_cut_steel",
            ModBlocks.GREEN_FLAMING_CUT_STEEL);
    public static final RegistryObject<Item> GREEN_FLAMING_CUT_STEEL_SLAB = registerBlockItem(
            "green_flaming_cut_steel_slab", ModBlocks.GREEN_FLAMING_CUT_STEEL_SLAB);
    public static final RegistryObject<Item> GREEN_FLAMING_CUT_STEEL_VERTICAL_SLAB = registerBlockItem(
            "green_flaming_cut_steel_vertical_slab", ModBlocks.GREEN_FLAMING_CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> GREEN_FLAMING_CUT_STEEL_STAIRS = registerBlockItem(
            "green_flaming_cut_steel_stairs", ModBlocks.GREEN_FLAMING_CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> GREEN_FLAMING_CUT_STEEL_WALL = registerBlockItem(
            "green_flaming_cut_steel_wall", ModBlocks.GREEN_FLAMING_CUT_STEEL_WALL);
    public static final RegistryObject<Item> GREEN_FLAMING_POLISHED_STEEL = registerBlockItem(
            "green_flaming_polished_steel", ModBlocks.GREEN_FLAMING_POLISHED_STEEL);
    public static final RegistryObject<Item> GREEN_FLAMING_POLISHED_STEEL_SLAB = registerBlockItem(
            "green_flaming_polished_steel_slab", ModBlocks.GREEN_FLAMING_POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> GREEN_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "green_flaming_polished_steel_vertical_slab", ModBlocks.GREEN_FLAMING_POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> GREEN_FLAMING_POLISHED_STEEL_STAIRS = registerBlockItem(
            "green_flaming_polished_steel_stairs", ModBlocks.GREEN_FLAMING_POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> GREEN_FLAMING_POLISHED_STEEL_WALL = registerBlockItem(
            "green_flaming_polished_steel_wall", ModBlocks.GREEN_FLAMING_POLISHED_STEEL_WALL);
    public static final RegistryObject<Item> GREEN_FLAMING_PRESSED_STEEL = registerBlockItem(
            "green_flaming_pressed_steel", ModBlocks.GREEN_FLAMING_PRESSED_STEEL);
    public static final RegistryObject<Item> GREEN_FLAMING_PRESSED_STEEL_SLAB = registerBlockItem(
            "green_flaming_pressed_steel_slab", ModBlocks.GREEN_FLAMING_PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> GREEN_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "green_flaming_pressed_steel_vertical_slab", ModBlocks.GREEN_FLAMING_PRESSED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> GREEN_FLAMING_PRESSED_STEEL_STAIRS = registerBlockItem(
            "green_flaming_pressed_steel_stairs", ModBlocks.GREEN_FLAMING_PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> GREEN_FLAMING_PRESSED_STEEL_WALL = registerBlockItem(
            "green_flaming_pressed_steel_wall", ModBlocks.GREEN_FLAMING_PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> GREEN_FLAMING_STEEL_CASING = registerBlockItem(
            "green_flaming_steel_casing", ModBlocks.GREEN_FLAMING_STEEL_CASING);
    public static final RegistryObject<Item> GREEN_FLAMING_STEEL_GRATE = registerBlockItem("green_flaming_steel_grate",
            ModBlocks.GREEN_FLAMING_STEEL_GRATE);
    public static final RegistryObject<Item> GREEN_FLAMING_STEEL_PILLAR = registerBlockItem(
            "green_flaming_steel_pillar", ModBlocks.GREEN_FLAMING_STEEL_PILLAR);
    public static final RegistryObject<Item> GREEN_FLAMING_BOLTED_STEEL_PILLAR = registerBlockItem(
            "green_flaming_bolted_steel_pillar", ModBlocks.GREEN_FLAMING_BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> RED_FLAMING_STEEL_BLOCK = registerBlockItem("red_flaming_steel_block",
            ModBlocks.RED_FLAMING_STEEL_BLOCK);
    public static final RegistryObject<Item> RED_FLAMING_STEEL_BLOCK_SLAB = registerBlockItem(
            "red_flaming_steel_block_slab", ModBlocks.RED_FLAMING_STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> RED_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "red_flaming_steel_block_vertical_slab", ModBlocks.RED_FLAMING_STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_FLAMING_STEEL_BLOCK_STAIRS = registerBlockItem(
            "red_flaming_steel_block_stairs", ModBlocks.RED_FLAMING_STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> RED_FLAMING_STEEL_BLOCK_WALL = registerBlockItem(
            "red_flaming_steel_block_wall", ModBlocks.RED_FLAMING_STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> RED_FLAMING_CUT_STEEL = registerBlockItem("red_flaming_cut_steel",
            ModBlocks.RED_FLAMING_CUT_STEEL);
    public static final RegistryObject<Item> RED_FLAMING_CUT_STEEL_SLAB = registerBlockItem(
            "red_flaming_cut_steel_slab", ModBlocks.RED_FLAMING_CUT_STEEL_SLAB);
    public static final RegistryObject<Item> RED_FLAMING_CUT_STEEL_VERTICAL_SLAB = registerBlockItem(
            "red_flaming_cut_steel_vertical_slab", ModBlocks.RED_FLAMING_CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_FLAMING_CUT_STEEL_STAIRS = registerBlockItem(
            "red_flaming_cut_steel_stairs", ModBlocks.RED_FLAMING_CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> RED_FLAMING_CUT_STEEL_WALL = registerBlockItem(
            "red_flaming_cut_steel_wall", ModBlocks.RED_FLAMING_CUT_STEEL_WALL);
    public static final RegistryObject<Item> RED_FLAMING_POLISHED_STEEL = registerBlockItem(
            "red_flaming_polished_steel", ModBlocks.RED_FLAMING_POLISHED_STEEL);
    public static final RegistryObject<Item> RED_FLAMING_POLISHED_STEEL_SLAB = registerBlockItem(
            "red_flaming_polished_steel_slab", ModBlocks.RED_FLAMING_POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> RED_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "red_flaming_polished_steel_vertical_slab", ModBlocks.RED_FLAMING_POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_FLAMING_POLISHED_STEEL_STAIRS = registerBlockItem(
            "red_flaming_polished_steel_stairs", ModBlocks.RED_FLAMING_POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> RED_FLAMING_POLISHED_STEEL_WALL = registerBlockItem(
            "red_flaming_polished_steel_wall", ModBlocks.RED_FLAMING_POLISHED_STEEL_WALL);
    public static final RegistryObject<Item> RED_FLAMING_PRESSED_STEEL = registerBlockItem("red_flaming_pressed_steel",
            ModBlocks.RED_FLAMING_PRESSED_STEEL);
    public static final RegistryObject<Item> RED_FLAMING_PRESSED_STEEL_SLAB = registerBlockItem(
            "red_flaming_pressed_steel_slab", ModBlocks.RED_FLAMING_PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> RED_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "red_flaming_pressed_steel_vertical_slab", ModBlocks.RED_FLAMING_PRESSED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_FLAMING_PRESSED_STEEL_STAIRS = registerBlockItem(
            "red_flaming_pressed_steel_stairs", ModBlocks.RED_FLAMING_PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> RED_FLAMING_PRESSED_STEEL_WALL = registerBlockItem(
            "red_flaming_pressed_steel_wall", ModBlocks.RED_FLAMING_PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> RED_FLAMING_STEEL_CASING = registerBlockItem("red_flaming_steel_casing",
            ModBlocks.RED_FLAMING_STEEL_CASING);
    public static final RegistryObject<Item> RED_FLAMING_STEEL_GRATE = registerBlockItem("red_flaming_steel_grate",
            ModBlocks.RED_FLAMING_STEEL_GRATE);
    public static final RegistryObject<Item> RED_FLAMING_STEEL_PILLAR = registerBlockItem("red_flaming_steel_pillar",
            ModBlocks.RED_FLAMING_STEEL_PILLAR);
    public static final RegistryObject<Item> RED_FLAMING_BOLTED_STEEL_PILLAR = registerBlockItem(
            "red_flaming_bolted_steel_pillar", ModBlocks.RED_FLAMING_BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> BLACK_FLAMING_STEEL_BLOCK = registerBlockItem("black_flaming_steel_block",
            ModBlocks.BLACK_FLAMING_STEEL_BLOCK);
    public static final RegistryObject<Item> BLACK_FLAMING_STEEL_BLOCK_SLAB = registerBlockItem(
            "black_flaming_steel_block_slab", ModBlocks.BLACK_FLAMING_STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> BLACK_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "black_flaming_steel_block_vertical_slab", ModBlocks.BLACK_FLAMING_STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLACK_FLAMING_STEEL_BLOCK_STAIRS = registerBlockItem(
            "black_flaming_steel_block_stairs", ModBlocks.BLACK_FLAMING_STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> BLACK_FLAMING_STEEL_BLOCK_WALL = registerBlockItem(
            "black_flaming_steel_block_wall", ModBlocks.BLACK_FLAMING_STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> BLACK_FLAMING_CUT_STEEL = registerBlockItem("black_flaming_cut_steel",
            ModBlocks.BLACK_FLAMING_CUT_STEEL);
    public static final RegistryObject<Item> BLACK_FLAMING_CUT_STEEL_SLAB = registerBlockItem(
            "black_flaming_cut_steel_slab", ModBlocks.BLACK_FLAMING_CUT_STEEL_SLAB);
    public static final RegistryObject<Item> BLACK_FLAMING_CUT_STEEL_VERTICAL_SLAB = registerBlockItem(
            "black_flaming_cut_steel_vertical_slab", ModBlocks.BLACK_FLAMING_CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLACK_FLAMING_CUT_STEEL_STAIRS = registerBlockItem(
            "black_flaming_cut_steel_stairs", ModBlocks.BLACK_FLAMING_CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> BLACK_FLAMING_CUT_STEEL_WALL = registerBlockItem(
            "black_flaming_cut_steel_wall", ModBlocks.BLACK_FLAMING_CUT_STEEL_WALL);
    public static final RegistryObject<Item> BLACK_FLAMING_POLISHED_STEEL = registerBlockItem(
            "black_flaming_polished_steel", ModBlocks.BLACK_FLAMING_POLISHED_STEEL);
    public static final RegistryObject<Item> BLACK_FLAMING_POLISHED_STEEL_SLAB = registerBlockItem(
            "black_flaming_polished_steel_slab", ModBlocks.BLACK_FLAMING_POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> BLACK_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "black_flaming_polished_steel_vertical_slab", ModBlocks.BLACK_FLAMING_POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLACK_FLAMING_POLISHED_STEEL_STAIRS = registerBlockItem(
            "black_flaming_polished_steel_stairs", ModBlocks.BLACK_FLAMING_POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> BLACK_FLAMING_POLISHED_STEEL_WALL = registerBlockItem(
            "black_flaming_polished_steel_wall", ModBlocks.BLACK_FLAMING_POLISHED_STEEL_WALL);
    public static final RegistryObject<Item> BLACK_FLAMING_PRESSED_STEEL = registerBlockItem(
            "black_flaming_pressed_steel", ModBlocks.BLACK_FLAMING_PRESSED_STEEL);
    public static final RegistryObject<Item> BLACK_FLAMING_PRESSED_STEEL_SLAB = registerBlockItem(
            "black_flaming_pressed_steel_slab", ModBlocks.BLACK_FLAMING_PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> BLACK_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "black_flaming_pressed_steel_vertical_slab", ModBlocks.BLACK_FLAMING_PRESSED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLACK_FLAMING_PRESSED_STEEL_STAIRS = registerBlockItem(
            "black_flaming_pressed_steel_stairs", ModBlocks.BLACK_FLAMING_PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> BLACK_FLAMING_PRESSED_STEEL_WALL = registerBlockItem(
            "black_flaming_pressed_steel_wall", ModBlocks.BLACK_FLAMING_PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> BLACK_FLAMING_STEEL_CASING = registerBlockItem(
            "black_flaming_steel_casing", ModBlocks.BLACK_FLAMING_STEEL_CASING);
    public static final RegistryObject<Item> BLACK_FLAMING_STEEL_GRATE = registerBlockItem("black_flaming_steel_grate",
            ModBlocks.BLACK_FLAMING_STEEL_GRATE);
    public static final RegistryObject<Item> BLACK_FLAMING_STEEL_PILLAR = registerBlockItem(
            "black_flaming_steel_pillar", ModBlocks.BLACK_FLAMING_STEEL_PILLAR);
    public static final RegistryObject<Item> BLACK_FLAMING_BOLTED_STEEL_PILLAR = registerBlockItem(
            "black_flaming_bolted_steel_pillar", ModBlocks.BLACK_FLAMING_BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> HOLLOW_OAK_LOG = registerBlockItem("hollow_oak_log",
            ModBlocks.HOLLOW_OAK_LOG);
    public static final RegistryObject<Item> STRIPPED_HOLLOW_OAK_LOG = registerBlockItem("stripped_hollow_oak_log",
            ModBlocks.STRIPPED_HOLLOW_OAK_LOG);
    public static final RegistryObject<Item> HOLLOW_SPRUCE_LOG = registerBlockItem("hollow_spruce_log",
            ModBlocks.HOLLOW_SPRUCE_LOG);
}
