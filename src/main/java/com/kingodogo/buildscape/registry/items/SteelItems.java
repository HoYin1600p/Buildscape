package com.kingodogo.buildscape.registry.items;

import com.kingodogo.buildscape.registry.ModCreativeModeTab;
import com.kingodogo.buildscape.registry.blocks.CardboardBlocks;
import com.kingodogo.buildscape.registry.blocks.SteelBlocks;
import com.kingodogo.buildscape.registry.blocks.WoolBlocks;
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
            WoolBlocks.MAGENTA_LAYERED_WOOL);
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL = registerBlockItem("light_blue_layered_wool",
            WoolBlocks.LIGHT_BLUE_LAYERED_WOOL);
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL = registerBlockItem("yellow_layered_wool",
            WoolBlocks.YELLOW_LAYERED_WOOL);
    public static final RegistryObject<Item> LIME_LAYERED_WOOL = registerBlockItem("lime_layered_wool",
            WoolBlocks.LIME_LAYERED_WOOL);
    public static final RegistryObject<Item> PINK_LAYERED_WOOL = registerBlockItem("pink_layered_wool",
            WoolBlocks.PINK_LAYERED_WOOL);
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL = registerBlockItem("gray_layered_wool",
            WoolBlocks.GRAY_LAYERED_WOOL);
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL = registerBlockItem("light_gray_layered_wool",
            WoolBlocks.LIGHT_GRAY_LAYERED_WOOL);
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL = registerBlockItem("cyan_layered_wool",
            WoolBlocks.CYAN_LAYERED_WOOL);
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL = registerBlockItem("purple_layered_wool",
            WoolBlocks.PURPLE_LAYERED_WOOL);
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL = registerBlockItem("blue_layered_wool",
            WoolBlocks.BLUE_LAYERED_WOOL);
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL = registerBlockItem("brown_layered_wool",
            WoolBlocks.BROWN_LAYERED_WOOL);
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL = registerBlockItem("green_layered_wool",
            WoolBlocks.GREEN_LAYERED_WOOL);
    public static final RegistryObject<Item> RED_LAYERED_WOOL = registerBlockItem("red_layered_wool",
            WoolBlocks.RED_LAYERED_WOOL);
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL = registerBlockItem("black_layered_wool",
            WoolBlocks.BLACK_LAYERED_WOOL);
    public static final RegistryObject<Item> WHITE_LAYERED_WOOL_SLAB = registerBlockItem("white_layered_wool_slab",
            WoolBlocks.WHITE_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> WHITE_LAYERED_WOOL_STAIRS = registerBlockItem("white_layered_wool_stairs",
            WoolBlocks.WHITE_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> WHITE_LAYERED_WOOL_WALL = registerBlockItem("white_layered_wool_wall",
            WoolBlocks.WHITE_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> WHITE_LAYERED_WOOL_CARPET = registerBlockItem("white_layered_wool_carpet",
            WoolBlocks.WHITE_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> WHITE_LAYERED_WOOL_LAYERS = registerBlockItem("white_layered_wool_layers",
            WoolBlocks.WHITE_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> WHITE_LAYERED_WOOL_VERTICAL_SLAB = registerBlockItem(
            "white_layered_wool_vertical_slab", WoolBlocks.WHITE_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL_SLAB = registerBlockItem("orange_layered_wool_slab",
            WoolBlocks.ORANGE_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL_STAIRS = registerBlockItem(
            "orange_layered_wool_stairs", WoolBlocks.ORANGE_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL_WALL = registerBlockItem("orange_layered_wool_wall",
            WoolBlocks.ORANGE_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL_CARPET = registerBlockItem(
            "orange_layered_wool_carpet", WoolBlocks.ORANGE_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL_LAYERS = registerBlockItem(
            "orange_layered_wool_layers", WoolBlocks.ORANGE_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL_VERTICAL_SLAB = registerBlockItem(
            "orange_layered_wool_vertical_slab", WoolBlocks.ORANGE_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL_SLAB = registerBlockItem("magenta_layered_wool_slab",
            WoolBlocks.MAGENTA_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL_STAIRS = registerBlockItem(
            "magenta_layered_wool_stairs", WoolBlocks.MAGENTA_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL_WALL = registerBlockItem("magenta_layered_wool_wall",
            WoolBlocks.MAGENTA_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL_CARPET = registerBlockItem(
            "magenta_layered_wool_carpet", WoolBlocks.MAGENTA_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL_LAYERS = registerBlockItem(
            "magenta_layered_wool_layers", WoolBlocks.MAGENTA_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL_VERTICAL_SLAB = registerBlockItem(
            "magenta_layered_wool_vertical_slab", WoolBlocks.MAGENTA_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL_SLAB = registerBlockItem(
            "light_blue_layered_wool_slab", WoolBlocks.LIGHT_BLUE_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL_STAIRS = registerBlockItem(
            "light_blue_layered_wool_stairs", WoolBlocks.LIGHT_BLUE_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL_WALL = registerBlockItem(
            "light_blue_layered_wool_wall", WoolBlocks.LIGHT_BLUE_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL_CARPET = registerBlockItem(
            "light_blue_layered_wool_carpet", WoolBlocks.LIGHT_BLUE_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL_LAYERS = registerBlockItem(
            "light_blue_layered_wool_layers", WoolBlocks.LIGHT_BLUE_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL_VERTICAL_SLAB = registerBlockItem(
            "light_blue_layered_wool_vertical_slab", WoolBlocks.LIGHT_BLUE_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL_SLAB = registerBlockItem("yellow_layered_wool_slab",
            WoolBlocks.YELLOW_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL_STAIRS = registerBlockItem(
            "yellow_layered_wool_stairs", WoolBlocks.YELLOW_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL_WALL = registerBlockItem("yellow_layered_wool_wall",
            WoolBlocks.YELLOW_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL_CARPET = registerBlockItem(
            "yellow_layered_wool_carpet", WoolBlocks.YELLOW_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL_LAYERS = registerBlockItem(
            "yellow_layered_wool_layers", WoolBlocks.YELLOW_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL_VERTICAL_SLAB = registerBlockItem(
            "yellow_layered_wool_vertical_slab", WoolBlocks.YELLOW_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIME_LAYERED_WOOL_SLAB = registerBlockItem("lime_layered_wool_slab",
            WoolBlocks.LIME_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> LIME_LAYERED_WOOL_STAIRS = registerBlockItem("lime_layered_wool_stairs",
            WoolBlocks.LIME_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> LIME_LAYERED_WOOL_WALL = registerBlockItem("lime_layered_wool_wall",
            WoolBlocks.LIME_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> LIME_LAYERED_WOOL_CARPET = registerBlockItem("lime_layered_wool_carpet",
            WoolBlocks.LIME_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> LIME_LAYERED_WOOL_LAYERS = registerBlockItem("lime_layered_wool_layers",
            WoolBlocks.LIME_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> LIME_LAYERED_WOOL_VERTICAL_SLAB = registerBlockItem(
            "lime_layered_wool_vertical_slab", WoolBlocks.LIME_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> PINK_LAYERED_WOOL_SLAB = registerBlockItem("pink_layered_wool_slab",
            WoolBlocks.PINK_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> PINK_LAYERED_WOOL_STAIRS = registerBlockItem("pink_layered_wool_stairs",
            WoolBlocks.PINK_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> PINK_LAYERED_WOOL_WALL = registerBlockItem("pink_layered_wool_wall",
            WoolBlocks.PINK_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> PINK_LAYERED_WOOL_CARPET = registerBlockItem("pink_layered_wool_carpet",
            WoolBlocks.PINK_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> PINK_LAYERED_WOOL_LAYERS = registerBlockItem("pink_layered_wool_layers",
            WoolBlocks.PINK_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> PINK_LAYERED_WOOL_VERTICAL_SLAB = registerBlockItem(
            "pink_layered_wool_vertical_slab", WoolBlocks.PINK_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL_SLAB = registerBlockItem("gray_layered_wool_slab",
            WoolBlocks.GRAY_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL_STAIRS = registerBlockItem("gray_layered_wool_stairs",
            WoolBlocks.GRAY_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL_WALL = registerBlockItem("gray_layered_wool_wall",
            WoolBlocks.GRAY_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL_CARPET = registerBlockItem("gray_layered_wool_carpet",
            WoolBlocks.GRAY_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL_LAYERS = registerBlockItem("gray_layered_wool_layers",
            WoolBlocks.GRAY_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL_VERTICAL_SLAB = registerBlockItem(
            "gray_layered_wool_vertical_slab", WoolBlocks.GRAY_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL_SLAB = registerBlockItem(
            "light_gray_layered_wool_slab", WoolBlocks.LIGHT_GRAY_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL_STAIRS = registerBlockItem(
            "light_gray_layered_wool_stairs", WoolBlocks.LIGHT_GRAY_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL_WALL = registerBlockItem(
            "light_gray_layered_wool_wall", WoolBlocks.LIGHT_GRAY_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL_CARPET = registerBlockItem(
            "light_gray_layered_wool_carpet", WoolBlocks.LIGHT_GRAY_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL_LAYERS = registerBlockItem(
            "light_gray_layered_wool_layers", WoolBlocks.LIGHT_GRAY_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL_VERTICAL_SLAB = registerBlockItem(
            "light_gray_layered_wool_vertical_slab", WoolBlocks.LIGHT_GRAY_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL_SLAB = registerBlockItem("cyan_layered_wool_slab",
            WoolBlocks.CYAN_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL_STAIRS = registerBlockItem("cyan_layered_wool_stairs",
            WoolBlocks.CYAN_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL_WALL = registerBlockItem("cyan_layered_wool_wall",
            WoolBlocks.CYAN_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL_CARPET = registerBlockItem("cyan_layered_wool_carpet",
            WoolBlocks.CYAN_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL_LAYERS = registerBlockItem("cyan_layered_wool_layers",
            WoolBlocks.CYAN_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL_VERTICAL_SLAB = registerBlockItem(
            "cyan_layered_wool_vertical_slab", WoolBlocks.CYAN_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL_SLAB = registerBlockItem("purple_layered_wool_slab",
            WoolBlocks.PURPLE_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL_STAIRS = registerBlockItem(
            "purple_layered_wool_stairs", WoolBlocks.PURPLE_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL_WALL = registerBlockItem("purple_layered_wool_wall",
            WoolBlocks.PURPLE_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL_CARPET = registerBlockItem(
            "purple_layered_wool_carpet", WoolBlocks.PURPLE_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL_LAYERS = registerBlockItem(
            "purple_layered_wool_layers", WoolBlocks.PURPLE_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL_VERTICAL_SLAB = registerBlockItem(
            "purple_layered_wool_vertical_slab", WoolBlocks.PURPLE_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL_SLAB = registerBlockItem("blue_layered_wool_slab",
            WoolBlocks.BLUE_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL_STAIRS = registerBlockItem("blue_layered_wool_stairs",
            WoolBlocks.BLUE_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL_WALL = registerBlockItem("blue_layered_wool_wall",
            WoolBlocks.BLUE_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL_CARPET = registerBlockItem("blue_layered_wool_carpet",
            WoolBlocks.BLUE_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL_LAYERS = registerBlockItem("blue_layered_wool_layers",
            WoolBlocks.BLUE_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL_VERTICAL_SLAB = registerBlockItem(
            "blue_layered_wool_vertical_slab", WoolBlocks.BLUE_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL_SLAB = registerBlockItem("brown_layered_wool_slab",
            WoolBlocks.BROWN_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL_STAIRS = registerBlockItem("brown_layered_wool_stairs",
            WoolBlocks.BROWN_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL_WALL = registerBlockItem("brown_layered_wool_wall",
            WoolBlocks.BROWN_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL_CARPET = registerBlockItem("brown_layered_wool_carpet",
            WoolBlocks.BROWN_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL_LAYERS = registerBlockItem("brown_layered_wool_layers",
            WoolBlocks.BROWN_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL_VERTICAL_SLAB = registerBlockItem(
            "brown_layered_wool_vertical_slab", WoolBlocks.BROWN_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL_SLAB = registerBlockItem("green_layered_wool_slab",
            WoolBlocks.GREEN_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL_STAIRS = registerBlockItem("green_layered_wool_stairs",
            WoolBlocks.GREEN_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL_WALL = registerBlockItem("green_layered_wool_wall",
            WoolBlocks.GREEN_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL_CARPET = registerBlockItem("green_layered_wool_carpet",
            WoolBlocks.GREEN_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL_LAYERS = registerBlockItem("green_layered_wool_layers",
            WoolBlocks.GREEN_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL_VERTICAL_SLAB = registerBlockItem(
            "green_layered_wool_vertical_slab", WoolBlocks.GREEN_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_LAYERED_WOOL_SLAB = registerBlockItem("red_layered_wool_slab",
            WoolBlocks.RED_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> RED_LAYERED_WOOL_STAIRS = registerBlockItem("red_layered_wool_stairs",
            WoolBlocks.RED_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> RED_LAYERED_WOOL_WALL = registerBlockItem("red_layered_wool_wall",
            WoolBlocks.RED_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> RED_LAYERED_WOOL_CARPET = registerBlockItem("red_layered_wool_carpet",
            WoolBlocks.RED_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> RED_LAYERED_WOOL_LAYERS = registerBlockItem("red_layered_wool_layers",
            WoolBlocks.RED_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> RED_LAYERED_WOOL_VERTICAL_SLAB = registerBlockItem(
            "red_layered_wool_vertical_slab", WoolBlocks.RED_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL_SLAB = registerBlockItem("black_layered_wool_slab",
            WoolBlocks.BLACK_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL_STAIRS = registerBlockItem("black_layered_wool_stairs",
            WoolBlocks.BLACK_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL_WALL = registerBlockItem("black_layered_wool_wall",
            WoolBlocks.BLACK_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL_CARPET = registerBlockItem("black_layered_wool_carpet",
            WoolBlocks.BLACK_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL_LAYERS = registerBlockItem("black_layered_wool_layers",
            WoolBlocks.BLACK_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL_VERTICAL_SLAB = registerBlockItem(
            "black_layered_wool_vertical_slab", WoolBlocks.BLACK_LAYERED_WOOL_VERTICAL_SLAB);

    public static final RegistryObject<Item> FLAMING_STEEL_INGOT = ITEMS.register("flaming_steel_ingot",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB)));
    public static final RegistryObject<Item> FLAMING_STEEL_NUGGET = ITEMS.register("flaming_steel_nugget",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB)));
    public static final RegistryObject<Item> FLAMING_STEEL_BLOCK = registerBlockItem("flaming_steel_block",
            WoolBlocks.FLAMING_STEEL_BLOCK);
    public static final RegistryObject<Item> FLAMING_STEEL_BLOCK_SLAB = registerBlockItem("flaming_steel_block_slab",
            SteelBlocks.FLAMING_STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> FLAMING_STEEL_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "flaming_steel_block_vertical_slab", SteelBlocks.FLAMING_STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> FLAMING_STEEL_BLOCK_STAIRS = registerBlockItem(
            "flaming_steel_block_stairs", SteelBlocks.FLAMING_STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> FLAMING_STEEL_BLOCK_WALL = registerBlockItem("flaming_steel_block_wall",
            SteelBlocks.FLAMING_STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> FLAMING_CUT_STEEL = registerBlockItem("flaming_cut_steel",
            SteelBlocks.FLAMING_CUT_STEEL);
    public static final RegistryObject<Item> FLAMING_CUT_STEEL_SLAB = registerBlockItem("flaming_cut_steel_slab",
            SteelBlocks.FLAMING_CUT_STEEL_SLAB);
    public static final RegistryObject<Item> FLAMING_CUT_STEEL_VERTICAL_SLAB = registerBlockItem(
            "flaming_cut_steel_vertical_slab", SteelBlocks.FLAMING_CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> FLAMING_CUT_STEEL_STAIRS = registerBlockItem("flaming_cut_steel_stairs",
            SteelBlocks.FLAMING_CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> FLAMING_CUT_STEEL_WALL = registerBlockItem("flaming_cut_steel_wall",
            SteelBlocks.FLAMING_CUT_STEEL_WALL);
    public static final RegistryObject<Item> FLAMING_POLISHED_STEEL = registerBlockItem("flaming_polished_steel",
            SteelBlocks.FLAMING_POLISHED_STEEL);
    public static final RegistryObject<Item> FLAMING_POLISHED_STEEL_SLAB = registerBlockItem(
            "flaming_polished_steel_slab", SteelBlocks.FLAMING_POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> FLAMING_POLISHED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "flaming_polished_steel_vertical_slab", SteelBlocks.FLAMING_POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> FLAMING_POLISHED_STEEL_STAIRS = registerBlockItem(
            "flaming_polished_steel_stairs", SteelBlocks.FLAMING_POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> FLAMING_POLISHED_STEEL_WALL = registerBlockItem(
            "flaming_polished_steel_wall", SteelBlocks.FLAMING_POLISHED_STEEL_WALL);
    public static final RegistryObject<Item> FLAMING_PRESSED_STEEL = registerBlockItem("flaming_pressed_steel",
            SteelBlocks.FLAMING_PRESSED_STEEL);
    public static final RegistryObject<Item> FLAMING_PRESSED_STEEL_SLAB = registerBlockItem(
            "flaming_pressed_steel_slab", SteelBlocks.FLAMING_PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> FLAMING_PRESSED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "flaming_pressed_steel_vertical_slab", SteelBlocks.FLAMING_PRESSED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> FLAMING_PRESSED_STEEL_STAIRS = registerBlockItem(
            "flaming_pressed_steel_stairs", SteelBlocks.FLAMING_PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> FLAMING_PRESSED_STEEL_WALL = registerBlockItem(
            "flaming_pressed_steel_wall", SteelBlocks.FLAMING_PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> FLAMING_STEEL_CASING = registerBlockItem("flaming_steel_casing",
            SteelBlocks.FLAMING_STEEL_CASING);
    public static final RegistryObject<Item> FLAMING_STEEL_GRATE = registerBlockItem("flaming_steel_grate",
            SteelBlocks.FLAMING_STEEL_GRATE);
    public static final RegistryObject<Item> FLAMING_STEEL_PILLAR = registerBlockItem("flaming_steel_pillar",
            SteelBlocks.FLAMING_STEEL_PILLAR);
    public static final RegistryObject<Item> FLAMING_BOLTED_STEEL_PILLAR = registerBlockItem(
            "flaming_bolted_steel_pillar", SteelBlocks.FLAMING_BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> FLAMING_STEEL_BOLTS = registerBlockItem("flaming_steel_bolts",
            SteelBlocks.FLAMING_STEEL_BOLTS);
    public static final RegistryObject<Item> FLAMING_STEEL_DOOR = ITEMS.register("flaming_steel_door",
            () -> new DoubleHighBlockItem(SteelBlocks.FLAMING_STEEL_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FLAMING_STEEL_TRAPDOOR = registerBlockItem("flaming_steel_trapdoor",
            SteelBlocks.FLAMING_STEEL_TRAPDOOR);
    public static final RegistryObject<Item> FLAMING_STEEL_BUTTON = registerBlockItem("flaming_steel_button",
            SteelBlocks.FLAMING_STEEL_BUTTON);
    public static final RegistryObject<Item> FLAMING_STEEL_PRESSURE_PLATE = registerBlockItem(
            "flaming_steel_pressure_plate", SteelBlocks.FLAMING_STEEL_PRESSURE_PLATE);
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_BLOCK = registerBlockItem(
            "magenta_flaming_steel_block", SteelBlocks.MAGENTA_FLAMING_STEEL_BLOCK);
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_BLOCK_SLAB = registerBlockItem(
            "magenta_flaming_steel_block_slab", SteelBlocks.MAGENTA_FLAMING_STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "magenta_flaming_steel_block_vertical_slab", SteelBlocks.MAGENTA_FLAMING_STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_BLOCK_STAIRS = registerBlockItem(
            "magenta_flaming_steel_block_stairs", SteelBlocks.MAGENTA_FLAMING_STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_BLOCK_WALL = registerBlockItem(
            "magenta_flaming_steel_block_wall", SteelBlocks.MAGENTA_FLAMING_STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> MAGENTA_FLAMING_CUT_STEEL = registerBlockItem("magenta_flaming_cut_steel",
            SteelBlocks.MAGENTA_FLAMING_CUT_STEEL);
    public static final RegistryObject<Item> MAGENTA_FLAMING_CUT_STEEL_SLAB = registerBlockItem(
            "magenta_flaming_cut_steel_slab", SteelBlocks.MAGENTA_FLAMING_CUT_STEEL_SLAB);
    public static final RegistryObject<Item> MAGENTA_FLAMING_CUT_STEEL_VERTICAL_SLAB = registerBlockItem(
            "magenta_flaming_cut_steel_vertical_slab", SteelBlocks.MAGENTA_FLAMING_CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> MAGENTA_FLAMING_CUT_STEEL_STAIRS = registerBlockItem(
            "magenta_flaming_cut_steel_stairs", SteelBlocks.MAGENTA_FLAMING_CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> MAGENTA_FLAMING_CUT_STEEL_WALL = registerBlockItem(
            "magenta_flaming_cut_steel_wall", SteelBlocks.MAGENTA_FLAMING_CUT_STEEL_WALL);
    public static final RegistryObject<Item> MAGENTA_FLAMING_POLISHED_STEEL = registerBlockItem(
            "magenta_flaming_polished_steel", SteelBlocks.MAGENTA_FLAMING_POLISHED_STEEL);
    public static final RegistryObject<Item> MAGENTA_FLAMING_POLISHED_STEEL_SLAB = registerBlockItem(
            "magenta_flaming_polished_steel_slab", SteelBlocks.MAGENTA_FLAMING_POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> MAGENTA_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "magenta_flaming_polished_steel_vertical_slab", SteelBlocks.MAGENTA_FLAMING_POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> MAGENTA_FLAMING_POLISHED_STEEL_STAIRS = registerBlockItem(
            "magenta_flaming_polished_steel_stairs", SteelBlocks.MAGENTA_FLAMING_POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> MAGENTA_FLAMING_POLISHED_STEEL_WALL = registerBlockItem(
            "magenta_flaming_polished_steel_wall", SteelBlocks.MAGENTA_FLAMING_POLISHED_STEEL_WALL);

    public static final RegistryObject<Item> MAGENTA_FLAMING_PRESSED_STEEL = registerBlockItem(
            "magenta_flaming_pressed_steel", SteelBlocks.MAGENTA_FLAMING_PRESSED_STEEL);
    public static final RegistryObject<Item> MAGENTA_FLAMING_PRESSED_STEEL_SLAB = registerBlockItem(
            "magenta_flaming_pressed_steel_slab", SteelBlocks.MAGENTA_FLAMING_PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> MAGENTA_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "magenta_flaming_pressed_steel_vertical_slab", SteelBlocks.MAGENTA_FLAMING_PRESSED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> MAGENTA_FLAMING_PRESSED_STEEL_STAIRS = registerBlockItem(
            "magenta_flaming_pressed_steel_stairs", SteelBlocks.MAGENTA_FLAMING_PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> MAGENTA_FLAMING_PRESSED_STEEL_WALL = registerBlockItem(
            "magenta_flaming_pressed_steel_wall", SteelBlocks.MAGENTA_FLAMING_PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_CASING = registerBlockItem(
            "magenta_flaming_steel_casing", SteelBlocks.MAGENTA_FLAMING_STEEL_CASING);
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_GRATE = registerBlockItem(
            "magenta_flaming_steel_grate", SteelBlocks.MAGENTA_FLAMING_STEEL_GRATE);
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_PILLAR = registerBlockItem(
            "magenta_flaming_steel_pillar", SteelBlocks.MAGENTA_FLAMING_STEEL_PILLAR);
    public static final RegistryObject<Item> MAGENTA_FLAMING_BOLTED_STEEL_PILLAR = registerBlockItem(
            "magenta_flaming_bolted_steel_pillar", SteelBlocks.MAGENTA_FLAMING_BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_STEEL_BLOCK = registerBlockItem(
            "light_blue_flaming_steel_block", SteelBlocks.LIGHT_BLUE_FLAMING_STEEL_BLOCK);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_STEEL_BLOCK_SLAB = registerBlockItem(
            "light_blue_flaming_steel_block_slab", SteelBlocks.LIGHT_BLUE_FLAMING_STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "light_blue_flaming_steel_block_vertical_slab", SteelBlocks.LIGHT_BLUE_FLAMING_STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_STEEL_BLOCK_STAIRS = registerBlockItem(
            "light_blue_flaming_steel_block_stairs", SteelBlocks.LIGHT_BLUE_FLAMING_STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_STEEL_BLOCK_WALL = registerBlockItem(
            "light_blue_flaming_steel_block_wall", SteelBlocks.LIGHT_BLUE_FLAMING_STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_CUT_STEEL = registerBlockItem(
            "light_blue_flaming_cut_steel", SteelBlocks.LIGHT_BLUE_FLAMING_CUT_STEEL);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_CUT_STEEL_SLAB = registerBlockItem(
            "light_blue_flaming_cut_steel_slab", SteelBlocks.LIGHT_BLUE_FLAMING_CUT_STEEL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_CUT_STEEL_VERTICAL_SLAB = registerBlockItem(
            "light_blue_flaming_cut_steel_vertical_slab", SteelBlocks.LIGHT_BLUE_FLAMING_CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_CUT_STEEL_STAIRS = registerBlockItem(
            "light_blue_flaming_cut_steel_stairs", SteelBlocks.LIGHT_BLUE_FLAMING_CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_CUT_STEEL_WALL = registerBlockItem(
            "light_blue_flaming_cut_steel_wall", SteelBlocks.LIGHT_BLUE_FLAMING_CUT_STEEL_WALL);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_POLISHED_STEEL = registerBlockItem(
            "light_blue_flaming_polished_steel", SteelBlocks.LIGHT_BLUE_FLAMING_POLISHED_STEEL);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_POLISHED_STEEL_SLAB = registerBlockItem(
            "light_blue_flaming_polished_steel_slab", SteelBlocks.LIGHT_BLUE_FLAMING_POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "light_blue_flaming_polished_steel_vertical_slab",
            SteelBlocks.LIGHT_BLUE_FLAMING_POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_POLISHED_STEEL_STAIRS = registerBlockItem(
            "light_blue_flaming_polished_steel_stairs", SteelBlocks.LIGHT_BLUE_FLAMING_POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_POLISHED_STEEL_WALL = registerBlockItem(
            "light_blue_flaming_polished_steel_wall", SteelBlocks.LIGHT_BLUE_FLAMING_POLISHED_STEEL_WALL);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_PRESSED_STEEL = registerBlockItem(
            "light_blue_flaming_pressed_steel", SteelBlocks.LIGHT_BLUE_FLAMING_PRESSED_STEEL);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_PRESSED_STEEL_SLAB = registerBlockItem(
            "light_blue_flaming_pressed_steel_slab", SteelBlocks.LIGHT_BLUE_FLAMING_PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "light_blue_flaming_pressed_steel_vertical_slab",
            SteelBlocks.LIGHT_BLUE_FLAMING_PRESSED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_PRESSED_STEEL_STAIRS = registerBlockItem(
            "light_blue_flaming_pressed_steel_stairs", SteelBlocks.LIGHT_BLUE_FLAMING_PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_PRESSED_STEEL_WALL = registerBlockItem(
            "light_blue_flaming_pressed_steel_wall", SteelBlocks.LIGHT_BLUE_FLAMING_PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_STEEL_CASING = registerBlockItem(
            "light_blue_flaming_steel_casing", SteelBlocks.LIGHT_BLUE_FLAMING_STEEL_CASING);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_STEEL_GRATE = registerBlockItem(
            "light_blue_flaming_steel_grate", SteelBlocks.LIGHT_BLUE_FLAMING_STEEL_GRATE);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_STEEL_PILLAR = registerBlockItem(
            "light_blue_flaming_steel_pillar", SteelBlocks.LIGHT_BLUE_FLAMING_STEEL_PILLAR);
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_BOLTED_STEEL_PILLAR = registerBlockItem(
            "light_blue_flaming_bolted_steel_pillar", SteelBlocks.LIGHT_BLUE_FLAMING_BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> YELLOW_FLAMING_STEEL_BLOCK = registerBlockItem(
            "yellow_flaming_steel_block", SteelBlocks.YELLOW_FLAMING_STEEL_BLOCK);
    public static final RegistryObject<Item> YELLOW_FLAMING_STEEL_BLOCK_SLAB = registerBlockItem(
            "yellow_flaming_steel_block_slab", SteelBlocks.YELLOW_FLAMING_STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> YELLOW_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "yellow_flaming_steel_block_vertical_slab", SteelBlocks.YELLOW_FLAMING_STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> YELLOW_FLAMING_STEEL_BLOCK_STAIRS = registerBlockItem(
            "yellow_flaming_steel_block_stairs", SteelBlocks.YELLOW_FLAMING_STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> YELLOW_FLAMING_STEEL_BLOCK_WALL = registerBlockItem(
            "yellow_flaming_steel_block_wall", SteelBlocks.YELLOW_FLAMING_STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> YELLOW_FLAMING_CUT_STEEL = registerBlockItem("yellow_flaming_cut_steel",
            SteelBlocks.YELLOW_FLAMING_CUT_STEEL);
    public static final RegistryObject<Item> YELLOW_FLAMING_CUT_STEEL_SLAB = registerBlockItem(
            "yellow_flaming_cut_steel_slab", SteelBlocks.YELLOW_FLAMING_CUT_STEEL_SLAB);
    public static final RegistryObject<Item> YELLOW_FLAMING_CUT_STEEL_VERTICAL_SLAB = registerBlockItem(
            "yellow_flaming_cut_steel_vertical_slab", SteelBlocks.YELLOW_FLAMING_CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> YELLOW_FLAMING_CUT_STEEL_STAIRS = registerBlockItem(
            "yellow_flaming_cut_steel_stairs", SteelBlocks.YELLOW_FLAMING_CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> YELLOW_FLAMING_CUT_STEEL_WALL = registerBlockItem(
            "yellow_flaming_cut_steel_wall", SteelBlocks.YELLOW_FLAMING_CUT_STEEL_WALL);
    public static final RegistryObject<Item> YELLOW_FLAMING_POLISHED_STEEL = registerBlockItem(
            "yellow_flaming_polished_steel", SteelBlocks.YELLOW_FLAMING_POLISHED_STEEL);
    public static final RegistryObject<Item> YELLOW_FLAMING_POLISHED_STEEL_SLAB = registerBlockItem(
            "yellow_flaming_polished_steel_slab", SteelBlocks.YELLOW_FLAMING_POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> YELLOW_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "yellow_flaming_polished_steel_vertical_slab", SteelBlocks.YELLOW_FLAMING_POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> YELLOW_FLAMING_POLISHED_STEEL_STAIRS = registerBlockItem(
            "yellow_flaming_polished_steel_stairs", SteelBlocks.YELLOW_FLAMING_POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> YELLOW_FLAMING_POLISHED_STEEL_WALL = registerBlockItem(
            "yellow_flaming_polished_steel_wall", SteelBlocks.YELLOW_FLAMING_POLISHED_STEEL_WALL);
    public static final RegistryObject<Item> YELLOW_FLAMING_PRESSED_STEEL = registerBlockItem(
            "yellow_flaming_pressed_steel", SteelBlocks.YELLOW_FLAMING_PRESSED_STEEL);
    public static final RegistryObject<Item> YELLOW_FLAMING_PRESSED_STEEL_SLAB = registerBlockItem(
            "yellow_flaming_pressed_steel_slab", SteelBlocks.YELLOW_FLAMING_PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> YELLOW_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "yellow_flaming_pressed_steel_vertical_slab", SteelBlocks.YELLOW_FLAMING_PRESSED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> YELLOW_FLAMING_PRESSED_STEEL_STAIRS = registerBlockItem(
            "yellow_flaming_pressed_steel_stairs", SteelBlocks.YELLOW_FLAMING_PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> YELLOW_FLAMING_PRESSED_STEEL_WALL = registerBlockItem(
            "yellow_flaming_pressed_steel_wall", SteelBlocks.YELLOW_FLAMING_PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> YELLOW_FLAMING_STEEL_CASING = registerBlockItem(
            "yellow_flaming_steel_casing", SteelBlocks.YELLOW_FLAMING_STEEL_CASING);
    public static final RegistryObject<Item> YELLOW_FLAMING_STEEL_GRATE = registerBlockItem(
            "yellow_flaming_steel_grate", SteelBlocks.YELLOW_FLAMING_STEEL_GRATE);
    public static final RegistryObject<Item> YELLOW_FLAMING_STEEL_PILLAR = registerBlockItem(
            "yellow_flaming_steel_pillar", SteelBlocks.YELLOW_FLAMING_STEEL_PILLAR);
    public static final RegistryObject<Item> YELLOW_FLAMING_BOLTED_STEEL_PILLAR = registerBlockItem(
            "yellow_flaming_bolted_steel_pillar", SteelBlocks.YELLOW_FLAMING_BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> LIME_FLAMING_STEEL_BLOCK = registerBlockItem("lime_flaming_steel_block",
            SteelBlocks.LIME_FLAMING_STEEL_BLOCK);
    public static final RegistryObject<Item> LIME_FLAMING_STEEL_BLOCK_SLAB = registerBlockItem(
            "lime_flaming_steel_block_slab", SteelBlocks.LIME_FLAMING_STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> LIME_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "lime_flaming_steel_block_vertical_slab", SteelBlocks.LIME_FLAMING_STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIME_FLAMING_STEEL_BLOCK_STAIRS = registerBlockItem(
            "lime_flaming_steel_block_stairs", SteelBlocks.LIME_FLAMING_STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> LIME_FLAMING_STEEL_BLOCK_WALL = registerBlockItem(
            "lime_flaming_steel_block_wall", SteelBlocks.LIME_FLAMING_STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> LIME_FLAMING_CUT_STEEL = registerBlockItem("lime_flaming_cut_steel",
            SteelBlocks.LIME_FLAMING_CUT_STEEL);
    public static final RegistryObject<Item> LIME_FLAMING_CUT_STEEL_SLAB = registerBlockItem(
            "lime_flaming_cut_steel_slab", SteelBlocks.LIME_FLAMING_CUT_STEEL_SLAB);
    public static final RegistryObject<Item> LIME_FLAMING_CUT_STEEL_VERTICAL_SLAB = registerBlockItem(
            "lime_flaming_cut_steel_vertical_slab", SteelBlocks.LIME_FLAMING_CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIME_FLAMING_CUT_STEEL_STAIRS = registerBlockItem(
            "lime_flaming_cut_steel_stairs", SteelBlocks.LIME_FLAMING_CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> LIME_FLAMING_CUT_STEEL_WALL = registerBlockItem(
            "lime_flaming_cut_steel_wall", SteelBlocks.LIME_FLAMING_CUT_STEEL_WALL);
    public static final RegistryObject<Item> LIME_FLAMING_POLISHED_STEEL = registerBlockItem(
            "lime_flaming_polished_steel", SteelBlocks.LIME_FLAMING_POLISHED_STEEL);
    public static final RegistryObject<Item> LIME_FLAMING_POLISHED_STEEL_SLAB = registerBlockItem(
            "lime_flaming_polished_steel_slab", SteelBlocks.LIME_FLAMING_POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> LIME_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "lime_flaming_polished_steel_vertical_slab", SteelBlocks.LIME_FLAMING_POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIME_FLAMING_POLISHED_STEEL_STAIRS = registerBlockItem(
            "lime_flaming_polished_steel_stairs", SteelBlocks.LIME_FLAMING_POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> LIME_FLAMING_POLISHED_STEEL_WALL = registerBlockItem(
            "lime_flaming_polished_steel_wall", SteelBlocks.LIME_FLAMING_POLISHED_STEEL_WALL);
    public static final RegistryObject<Item> LIME_FLAMING_PRESSED_STEEL = registerBlockItem(
            "lime_flaming_pressed_steel", SteelBlocks.LIME_FLAMING_PRESSED_STEEL);
    public static final RegistryObject<Item> LIME_FLAMING_PRESSED_STEEL_SLAB = registerBlockItem(
            "lime_flaming_pressed_steel_slab", SteelBlocks.LIME_FLAMING_PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> LIME_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "lime_flaming_pressed_steel_vertical_slab", SteelBlocks.LIME_FLAMING_PRESSED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIME_FLAMING_PRESSED_STEEL_STAIRS = registerBlockItem(
            "lime_flaming_pressed_steel_stairs", SteelBlocks.LIME_FLAMING_PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> LIME_FLAMING_PRESSED_STEEL_WALL = registerBlockItem(
            "lime_flaming_pressed_steel_wall", SteelBlocks.LIME_FLAMING_PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> LIME_FLAMING_STEEL_CASING = registerBlockItem("lime_flaming_steel_casing",
            SteelBlocks.LIME_FLAMING_STEEL_CASING);
    public static final RegistryObject<Item> LIME_FLAMING_STEEL_GRATE = registerBlockItem("lime_flaming_steel_grate",
            SteelBlocks.LIME_FLAMING_STEEL_GRATE);
    public static final RegistryObject<Item> LIME_FLAMING_STEEL_PILLAR = registerBlockItem("lime_flaming_steel_pillar",
            SteelBlocks.LIME_FLAMING_STEEL_PILLAR);
    public static final RegistryObject<Item> LIME_FLAMING_BOLTED_STEEL_PILLAR = registerBlockItem(
            "lime_flaming_bolted_steel_pillar", SteelBlocks.LIME_FLAMING_BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> PINK_FLAMING_STEEL_BLOCK = registerBlockItem("pink_flaming_steel_block",
            SteelBlocks.PINK_FLAMING_STEEL_BLOCK);
    public static final RegistryObject<Item> PINK_FLAMING_STEEL_BLOCK_SLAB = registerBlockItem(
            "pink_flaming_steel_block_slab", SteelBlocks.PINK_FLAMING_STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> PINK_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "pink_flaming_steel_block_vertical_slab", SteelBlocks.PINK_FLAMING_STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> PINK_FLAMING_STEEL_BLOCK_STAIRS = registerBlockItem(
            "pink_flaming_steel_block_stairs", SteelBlocks.PINK_FLAMING_STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> PINK_FLAMING_STEEL_BLOCK_WALL = registerBlockItem(
            "pink_flaming_steel_block_wall", SteelBlocks.PINK_FLAMING_STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> PINK_FLAMING_CUT_STEEL = registerBlockItem("pink_flaming_cut_steel",
            SteelBlocks.PINK_FLAMING_CUT_STEEL);
    public static final RegistryObject<Item> PINK_FLAMING_CUT_STEEL_SLAB = registerBlockItem(
            "pink_flaming_cut_steel_slab", SteelBlocks.PINK_FLAMING_CUT_STEEL_SLAB);
    public static final RegistryObject<Item> PINK_FLAMING_CUT_STEEL_VERTICAL_SLAB = registerBlockItem(
            "pink_flaming_cut_steel_vertical_slab", SteelBlocks.PINK_FLAMING_CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> PINK_FLAMING_CUT_STEEL_STAIRS = registerBlockItem(
            "pink_flaming_cut_steel_stairs", SteelBlocks.PINK_FLAMING_CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> PINK_FLAMING_CUT_STEEL_WALL = registerBlockItem(
            "pink_flaming_cut_steel_wall", SteelBlocks.PINK_FLAMING_CUT_STEEL_WALL);
    public static final RegistryObject<Item> PINK_FLAMING_POLISHED_STEEL = registerBlockItem(
            "pink_flaming_polished_steel", SteelBlocks.PINK_FLAMING_POLISHED_STEEL);
    public static final RegistryObject<Item> PINK_FLAMING_POLISHED_STEEL_SLAB = registerBlockItem(
            "pink_flaming_polished_steel_slab", SteelBlocks.PINK_FLAMING_POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> PINK_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "pink_flaming_polished_steel_vertical_slab", SteelBlocks.PINK_FLAMING_POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> PINK_FLAMING_POLISHED_STEEL_STAIRS = registerBlockItem(
            "pink_flaming_polished_steel_stairs", SteelBlocks.PINK_FLAMING_POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> PINK_FLAMING_POLISHED_STEEL_WALL = registerBlockItem(
            "pink_flaming_polished_steel_wall", SteelBlocks.PINK_FLAMING_POLISHED_STEEL_WALL);
    public static final RegistryObject<Item> PINK_FLAMING_PRESSED_STEEL = registerBlockItem(
            "pink_flaming_pressed_steel", SteelBlocks.PINK_FLAMING_PRESSED_STEEL);
    public static final RegistryObject<Item> PINK_FLAMING_PRESSED_STEEL_SLAB = registerBlockItem(
            "pink_flaming_pressed_steel_slab", SteelBlocks.PINK_FLAMING_PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> PINK_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "pink_flaming_pressed_steel_vertical_slab", SteelBlocks.PINK_FLAMING_PRESSED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> PINK_FLAMING_PRESSED_STEEL_STAIRS = registerBlockItem(
            "pink_flaming_pressed_steel_stairs", SteelBlocks.PINK_FLAMING_PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> PINK_FLAMING_PRESSED_STEEL_WALL = registerBlockItem(
            "pink_flaming_pressed_steel_wall", SteelBlocks.PINK_FLAMING_PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> PINK_FLAMING_STEEL_CASING = registerBlockItem("pink_flaming_steel_casing",
            SteelBlocks.PINK_FLAMING_STEEL_CASING);
    public static final RegistryObject<Item> PINK_FLAMING_STEEL_GRATE = registerBlockItem("pink_flaming_steel_grate",
            SteelBlocks.PINK_FLAMING_STEEL_GRATE);
    public static final RegistryObject<Item> PINK_FLAMING_STEEL_PILLAR = registerBlockItem("pink_flaming_steel_pillar",
            SteelBlocks.PINK_FLAMING_STEEL_PILLAR);
    public static final RegistryObject<Item> PINK_FLAMING_BOLTED_STEEL_PILLAR = registerBlockItem(
            "pink_flaming_bolted_steel_pillar", SteelBlocks.PINK_FLAMING_BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> GRAY_FLAMING_STEEL_BLOCK = registerBlockItem("gray_flaming_steel_block",
            SteelBlocks.GRAY_FLAMING_STEEL_BLOCK);
    public static final RegistryObject<Item> GRAY_FLAMING_STEEL_BLOCK_SLAB = registerBlockItem(
            "gray_flaming_steel_block_slab", SteelBlocks.GRAY_FLAMING_STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> GRAY_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "gray_flaming_steel_block_vertical_slab", SteelBlocks.GRAY_FLAMING_STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> GRAY_FLAMING_STEEL_BLOCK_STAIRS = registerBlockItem(
            "gray_flaming_steel_block_stairs", SteelBlocks.GRAY_FLAMING_STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> GRAY_FLAMING_STEEL_BLOCK_WALL = registerBlockItem(
            "gray_flaming_steel_block_wall", SteelBlocks.GRAY_FLAMING_STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> GRAY_FLAMING_CUT_STEEL = registerBlockItem("gray_flaming_cut_steel",
            SteelBlocks.GRAY_FLAMING_CUT_STEEL);
    public static final RegistryObject<Item> GRAY_FLAMING_CUT_STEEL_SLAB = registerBlockItem(
            "gray_flaming_cut_steel_slab", SteelBlocks.GRAY_FLAMING_CUT_STEEL_SLAB);
    public static final RegistryObject<Item> GRAY_FLAMING_CUT_STEEL_VERTICAL_SLAB = registerBlockItem(
            "gray_flaming_cut_steel_vertical_slab", SteelBlocks.GRAY_FLAMING_CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> GRAY_FLAMING_CUT_STEEL_STAIRS = registerBlockItem(
            "gray_flaming_cut_steel_stairs", SteelBlocks.GRAY_FLAMING_CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> GRAY_FLAMING_CUT_STEEL_WALL = registerBlockItem(
            "gray_flaming_cut_steel_wall", SteelBlocks.GRAY_FLAMING_CUT_STEEL_WALL);
    public static final RegistryObject<Item> GRAY_FLAMING_POLISHED_STEEL = registerBlockItem(
            "gray_flaming_polished_steel", SteelBlocks.GRAY_FLAMING_POLISHED_STEEL);
    public static final RegistryObject<Item> GRAY_FLAMING_POLISHED_STEEL_SLAB = registerBlockItem(
            "gray_flaming_polished_steel_slab", SteelBlocks.GRAY_FLAMING_POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> GRAY_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "gray_flaming_polished_steel_vertical_slab", SteelBlocks.GRAY_FLAMING_POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> GRAY_FLAMING_POLISHED_STEEL_STAIRS = registerBlockItem(
            "gray_flaming_polished_steel_stairs", SteelBlocks.GRAY_FLAMING_POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> GRAY_FLAMING_POLISHED_STEEL_WALL = registerBlockItem(
            "gray_flaming_polished_steel_wall", SteelBlocks.GRAY_FLAMING_POLISHED_STEEL_WALL);
    public static final RegistryObject<Item> GRAY_FLAMING_PRESSED_STEEL = registerBlockItem(
            "gray_flaming_pressed_steel", SteelBlocks.GRAY_FLAMING_PRESSED_STEEL);
    public static final RegistryObject<Item> GRAY_FLAMING_PRESSED_STEEL_SLAB = registerBlockItem(
            "gray_flaming_pressed_steel_slab", SteelBlocks.GRAY_FLAMING_PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> GRAY_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "gray_flaming_pressed_steel_vertical_slab", SteelBlocks.GRAY_FLAMING_PRESSED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> GRAY_FLAMING_PRESSED_STEEL_STAIRS = registerBlockItem(
            "gray_flaming_pressed_steel_stairs", SteelBlocks.GRAY_FLAMING_PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> GRAY_FLAMING_PRESSED_STEEL_WALL = registerBlockItem(
            "gray_flaming_pressed_steel_wall", SteelBlocks.GRAY_FLAMING_PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> GRAY_FLAMING_STEEL_CASING = registerBlockItem("gray_flaming_steel_casing",
            SteelBlocks.GRAY_FLAMING_STEEL_CASING);
    public static final RegistryObject<Item> GRAY_FLAMING_STEEL_GRATE = registerBlockItem("gray_flaming_steel_grate",
            SteelBlocks.GRAY_FLAMING_STEEL_GRATE);
    public static final RegistryObject<Item> GRAY_FLAMING_STEEL_PILLAR = registerBlockItem("gray_flaming_steel_pillar",
            SteelBlocks.GRAY_FLAMING_STEEL_PILLAR);
    public static final RegistryObject<Item> GRAY_FLAMING_BOLTED_STEEL_PILLAR = registerBlockItem(
            "gray_flaming_bolted_steel_pillar", SteelBlocks.GRAY_FLAMING_BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_STEEL_BLOCK = registerBlockItem(
            "light_gray_flaming_steel_block", SteelBlocks.LIGHT_GRAY_FLAMING_STEEL_BLOCK);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_STEEL_BLOCK_SLAB = registerBlockItem(
            "light_gray_flaming_steel_block_slab", SteelBlocks.LIGHT_GRAY_FLAMING_STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "light_gray_flaming_steel_block_vertical_slab", SteelBlocks.LIGHT_GRAY_FLAMING_STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_STEEL_BLOCK_STAIRS = registerBlockItem(
            "light_gray_flaming_steel_block_stairs", SteelBlocks.LIGHT_GRAY_FLAMING_STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_STEEL_BLOCK_WALL = registerBlockItem(
            "light_gray_flaming_steel_block_wall", SteelBlocks.LIGHT_GRAY_FLAMING_STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_CUT_STEEL = registerBlockItem(
            "light_gray_flaming_cut_steel", SteelBlocks.LIGHT_GRAY_FLAMING_CUT_STEEL);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_CUT_STEEL_SLAB = registerBlockItem(
            "light_gray_flaming_cut_steel_slab", SteelBlocks.LIGHT_GRAY_FLAMING_CUT_STEEL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_CUT_STEEL_VERTICAL_SLAB = registerBlockItem(
            "light_gray_flaming_cut_steel_vertical_slab", SteelBlocks.LIGHT_GRAY_FLAMING_CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_CUT_STEEL_STAIRS = registerBlockItem(
            "light_gray_flaming_cut_steel_stairs", SteelBlocks.LIGHT_GRAY_FLAMING_CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_CUT_STEEL_WALL = registerBlockItem(
            "light_gray_flaming_cut_steel_wall", SteelBlocks.LIGHT_GRAY_FLAMING_CUT_STEEL_WALL);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_POLISHED_STEEL = registerBlockItem(
            "light_gray_flaming_polished_steel", SteelBlocks.LIGHT_GRAY_FLAMING_POLISHED_STEEL);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_POLISHED_STEEL_SLAB = registerBlockItem(
            "light_gray_flaming_polished_steel_slab", SteelBlocks.LIGHT_GRAY_FLAMING_POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "light_gray_flaming_polished_steel_vertical_slab",
            SteelBlocks.LIGHT_GRAY_FLAMING_POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_POLISHED_STEEL_STAIRS = registerBlockItem(
            "light_gray_flaming_polished_steel_stairs", SteelBlocks.LIGHT_GRAY_FLAMING_POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_POLISHED_STEEL_WALL = registerBlockItem(
            "light_gray_flaming_polished_steel_wall", SteelBlocks.LIGHT_GRAY_FLAMING_POLISHED_STEEL_WALL);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_PRESSED_STEEL = registerBlockItem(
            "light_gray_flaming_pressed_steel", SteelBlocks.LIGHT_GRAY_FLAMING_PRESSED_STEEL);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_PRESSED_STEEL_SLAB = registerBlockItem(
            "light_gray_flaming_pressed_steel_slab", SteelBlocks.LIGHT_GRAY_FLAMING_PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "light_gray_flaming_pressed_steel_vertical_slab",
            SteelBlocks.LIGHT_GRAY_FLAMING_PRESSED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_PRESSED_STEEL_STAIRS = registerBlockItem(
            "light_gray_flaming_pressed_steel_stairs", SteelBlocks.LIGHT_GRAY_FLAMING_PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_PRESSED_STEEL_WALL = registerBlockItem(
            "light_gray_flaming_pressed_steel_wall", SteelBlocks.LIGHT_GRAY_FLAMING_PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_STEEL_CASING = registerBlockItem(
            "light_gray_flaming_steel_casing", SteelBlocks.LIGHT_GRAY_FLAMING_STEEL_CASING);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_STEEL_GRATE = registerBlockItem(
            "light_gray_flaming_steel_grate", SteelBlocks.LIGHT_GRAY_FLAMING_STEEL_GRATE);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_STEEL_PILLAR = registerBlockItem(
            "light_gray_flaming_steel_pillar", SteelBlocks.LIGHT_GRAY_FLAMING_STEEL_PILLAR);
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_BOLTED_STEEL_PILLAR = registerBlockItem(
            "light_gray_flaming_bolted_steel_pillar", SteelBlocks.LIGHT_GRAY_FLAMING_BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> CYAN_FLAMING_STEEL_BLOCK = registerBlockItem("cyan_flaming_steel_block",
            SteelBlocks.CYAN_FLAMING_STEEL_BLOCK);
    public static final RegistryObject<Item> CYAN_FLAMING_STEEL_BLOCK_SLAB = registerBlockItem(
            "cyan_flaming_steel_block_slab", SteelBlocks.CYAN_FLAMING_STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> CYAN_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "cyan_flaming_steel_block_vertical_slab", SteelBlocks.CYAN_FLAMING_STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> CYAN_FLAMING_STEEL_BLOCK_STAIRS = registerBlockItem(
            "cyan_flaming_steel_block_stairs", SteelBlocks.CYAN_FLAMING_STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> CYAN_FLAMING_STEEL_BLOCK_WALL = registerBlockItem(
            "cyan_flaming_steel_block_wall", SteelBlocks.CYAN_FLAMING_STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> CYAN_FLAMING_CUT_STEEL = registerBlockItem("cyan_flaming_cut_steel",
            SteelBlocks.CYAN_FLAMING_CUT_STEEL);
    public static final RegistryObject<Item> CYAN_FLAMING_CUT_STEEL_SLAB = registerBlockItem(
            "cyan_flaming_cut_steel_slab", SteelBlocks.CYAN_FLAMING_CUT_STEEL_SLAB);
    public static final RegistryObject<Item> CYAN_FLAMING_CUT_STEEL_VERTICAL_SLAB = registerBlockItem(
            "cyan_flaming_cut_steel_vertical_slab", SteelBlocks.CYAN_FLAMING_CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> CYAN_FLAMING_CUT_STEEL_STAIRS = registerBlockItem(
            "cyan_flaming_cut_steel_stairs", SteelBlocks.CYAN_FLAMING_CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> CYAN_FLAMING_CUT_STEEL_WALL = registerBlockItem(
            "cyan_flaming_cut_steel_wall", SteelBlocks.CYAN_FLAMING_CUT_STEEL_WALL);
    public static final RegistryObject<Item> CYAN_FLAMING_POLISHED_STEEL = registerBlockItem(
            "cyan_flaming_polished_steel", SteelBlocks.CYAN_FLAMING_POLISHED_STEEL);
    public static final RegistryObject<Item> CYAN_FLAMING_POLISHED_STEEL_SLAB = registerBlockItem(
            "cyan_flaming_polished_steel_slab", SteelBlocks.CYAN_FLAMING_POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> CYAN_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "cyan_flaming_polished_steel_vertical_slab", SteelBlocks.CYAN_FLAMING_POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> CYAN_FLAMING_POLISHED_STEEL_STAIRS = registerBlockItem(
            "cyan_flaming_polished_steel_stairs", SteelBlocks.CYAN_FLAMING_POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> CYAN_FLAMING_POLISHED_STEEL_WALL = registerBlockItem(
            "cyan_flaming_polished_steel_wall", SteelBlocks.CYAN_FLAMING_POLISHED_STEEL_WALL);
    public static final RegistryObject<Item> CYAN_FLAMING_PRESSED_STEEL = registerBlockItem(
            "cyan_flaming_pressed_steel", SteelBlocks.CYAN_FLAMING_PRESSED_STEEL);
    public static final RegistryObject<Item> CYAN_FLAMING_PRESSED_STEEL_SLAB = registerBlockItem(
            "cyan_flaming_pressed_steel_slab", SteelBlocks.CYAN_FLAMING_PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> CYAN_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "cyan_flaming_pressed_steel_vertical_slab", SteelBlocks.CYAN_FLAMING_PRESSED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> CYAN_FLAMING_PRESSED_STEEL_STAIRS = registerBlockItem(
            "cyan_flaming_pressed_steel_stairs", SteelBlocks.CYAN_FLAMING_PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> CYAN_FLAMING_PRESSED_STEEL_WALL = registerBlockItem(
            "cyan_flaming_pressed_steel_wall", SteelBlocks.CYAN_FLAMING_PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> CYAN_FLAMING_STEEL_CASING = registerBlockItem("cyan_flaming_steel_casing",
            SteelBlocks.CYAN_FLAMING_STEEL_CASING);
    public static final RegistryObject<Item> CYAN_FLAMING_STEEL_GRATE = registerBlockItem("cyan_flaming_steel_grate",
            SteelBlocks.CYAN_FLAMING_STEEL_GRATE);
    public static final RegistryObject<Item> CYAN_FLAMING_STEEL_PILLAR = registerBlockItem("cyan_flaming_steel_pillar",
            SteelBlocks.CYAN_FLAMING_STEEL_PILLAR);
    public static final RegistryObject<Item> CYAN_FLAMING_BOLTED_STEEL_PILLAR = registerBlockItem(
            "cyan_flaming_bolted_steel_pillar", SteelBlocks.CYAN_FLAMING_BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> PURPLE_FLAMING_STEEL_BLOCK = registerBlockItem(
            "purple_flaming_steel_block", SteelBlocks.PURPLE_FLAMING_STEEL_BLOCK);
    public static final RegistryObject<Item> PURPLE_FLAMING_STEEL_BLOCK_SLAB = registerBlockItem(
            "purple_flaming_steel_block_slab", SteelBlocks.PURPLE_FLAMING_STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> PURPLE_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "purple_flaming_steel_block_vertical_slab", SteelBlocks.PURPLE_FLAMING_STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> PURPLE_FLAMING_STEEL_BLOCK_STAIRS = registerBlockItem(
            "purple_flaming_steel_block_stairs", SteelBlocks.PURPLE_FLAMING_STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> PURPLE_FLAMING_STEEL_BLOCK_WALL = registerBlockItem(
            "purple_flaming_steel_block_wall", SteelBlocks.PURPLE_FLAMING_STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> PURPLE_FLAMING_CUT_STEEL = registerBlockItem("purple_flaming_cut_steel",
            SteelBlocks.PURPLE_FLAMING_CUT_STEEL);
    public static final RegistryObject<Item> PURPLE_FLAMING_CUT_STEEL_SLAB = registerBlockItem(
            "purple_flaming_cut_steel_slab", SteelBlocks.PURPLE_FLAMING_CUT_STEEL_SLAB);
    public static final RegistryObject<Item> PURPLE_FLAMING_CUT_STEEL_VERTICAL_SLAB = registerBlockItem(
            "purple_flaming_cut_steel_vertical_slab", SteelBlocks.PURPLE_FLAMING_CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> PURPLE_FLAMING_CUT_STEEL_STAIRS = registerBlockItem(
            "purple_flaming_cut_steel_stairs", SteelBlocks.PURPLE_FLAMING_CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> PURPLE_FLAMING_CUT_STEEL_WALL = registerBlockItem(
            "purple_flaming_cut_steel_wall", SteelBlocks.PURPLE_FLAMING_CUT_STEEL_WALL);
    public static final RegistryObject<Item> PURPLE_FLAMING_POLISHED_STEEL = registerBlockItem(
            "purple_flaming_polished_steel", SteelBlocks.PURPLE_FLAMING_POLISHED_STEEL);
    public static final RegistryObject<Item> PURPLE_FLAMING_POLISHED_STEEL_SLAB = registerBlockItem(
            "purple_flaming_polished_steel_slab", SteelBlocks.PURPLE_FLAMING_POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> PURPLE_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "purple_flaming_polished_steel_vertical_slab", SteelBlocks.PURPLE_FLAMING_POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> PURPLE_FLAMING_POLISHED_STEEL_STAIRS = registerBlockItem(
            "purple_flaming_polished_steel_stairs", SteelBlocks.PURPLE_FLAMING_POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> PURPLE_FLAMING_POLISHED_STEEL_WALL = registerBlockItem(
            "purple_flaming_polished_steel_wall", SteelBlocks.PURPLE_FLAMING_POLISHED_STEEL_WALL);
    public static final RegistryObject<Item> PURPLE_FLAMING_PRESSED_STEEL = registerBlockItem(
            "purple_flaming_pressed_steel", SteelBlocks.PURPLE_FLAMING_PRESSED_STEEL);
    public static final RegistryObject<Item> PURPLE_FLAMING_PRESSED_STEEL_SLAB = registerBlockItem(
            "purple_flaming_pressed_steel_slab", SteelBlocks.PURPLE_FLAMING_PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> PURPLE_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "purple_flaming_pressed_steel_vertical_slab", SteelBlocks.PURPLE_FLAMING_PRESSED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> PURPLE_FLAMING_PRESSED_STEEL_STAIRS = registerBlockItem(
            "purple_flaming_pressed_steel_stairs", SteelBlocks.PURPLE_FLAMING_PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> PURPLE_FLAMING_PRESSED_STEEL_WALL = registerBlockItem(
            "purple_flaming_pressed_steel_wall", SteelBlocks.PURPLE_FLAMING_PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> PURPLE_FLAMING_STEEL_CASING = registerBlockItem(
            "purple_flaming_steel_casing", SteelBlocks.PURPLE_FLAMING_STEEL_CASING);
    public static final RegistryObject<Item> PURPLE_FLAMING_STEEL_GRATE = registerBlockItem(
            "purple_flaming_steel_grate", SteelBlocks.PURPLE_FLAMING_STEEL_GRATE);
    public static final RegistryObject<Item> PURPLE_FLAMING_STEEL_PILLAR = registerBlockItem(
            "purple_flaming_steel_pillar", SteelBlocks.PURPLE_FLAMING_STEEL_PILLAR);
    public static final RegistryObject<Item> PURPLE_FLAMING_BOLTED_STEEL_PILLAR = registerBlockItem(
            "purple_flaming_bolted_steel_pillar", SteelBlocks.PURPLE_FLAMING_BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> BLUE_FLAMING_STEEL_BLOCK = registerBlockItem("blue_flaming_steel_block",
            SteelBlocks.BLUE_FLAMING_STEEL_BLOCK);
    public static final RegistryObject<Item> BLUE_FLAMING_STEEL_BLOCK_SLAB = registerBlockItem(
            "blue_flaming_steel_block_slab", SteelBlocks.BLUE_FLAMING_STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> BLUE_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "blue_flaming_steel_block_vertical_slab", SteelBlocks.BLUE_FLAMING_STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLUE_FLAMING_STEEL_BLOCK_STAIRS = registerBlockItem(
            "blue_flaming_steel_block_stairs", SteelBlocks.BLUE_FLAMING_STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> BLUE_FLAMING_STEEL_BLOCK_WALL = registerBlockItem(
            "blue_flaming_steel_block_wall", SteelBlocks.BLUE_FLAMING_STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> BLUE_FLAMING_CUT_STEEL = registerBlockItem("blue_flaming_cut_steel",
            SteelBlocks.BLUE_FLAMING_CUT_STEEL);
    public static final RegistryObject<Item> BLUE_FLAMING_CUT_STEEL_SLAB = registerBlockItem(
            "blue_flaming_cut_steel_slab", SteelBlocks.BLUE_FLAMING_CUT_STEEL_SLAB);
    public static final RegistryObject<Item> BLUE_FLAMING_CUT_STEEL_VERTICAL_SLAB = registerBlockItem(
            "blue_flaming_cut_steel_vertical_slab", SteelBlocks.BLUE_FLAMING_CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLUE_FLAMING_CUT_STEEL_STAIRS = registerBlockItem(
            "blue_flaming_cut_steel_stairs", SteelBlocks.BLUE_FLAMING_CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> BLUE_FLAMING_CUT_STEEL_WALL = registerBlockItem(
            "blue_flaming_cut_steel_wall", SteelBlocks.BLUE_FLAMING_CUT_STEEL_WALL);
    public static final RegistryObject<Item> BLUE_FLAMING_POLISHED_STEEL = registerBlockItem(
            "blue_flaming_polished_steel", SteelBlocks.BLUE_FLAMING_POLISHED_STEEL);
    public static final RegistryObject<Item> BLUE_FLAMING_POLISHED_STEEL_SLAB = registerBlockItem(
            "blue_flaming_polished_steel_slab", SteelBlocks.BLUE_FLAMING_POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> BLUE_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "blue_flaming_polished_steel_vertical_slab", SteelBlocks.BLUE_FLAMING_POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLUE_FLAMING_POLISHED_STEEL_STAIRS = registerBlockItem(
            "blue_flaming_polished_steel_stairs", SteelBlocks.BLUE_FLAMING_POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> BLUE_FLAMING_POLISHED_STEEL_WALL = registerBlockItem(
            "blue_flaming_polished_steel_wall", SteelBlocks.BLUE_FLAMING_POLISHED_STEEL_WALL);
    public static final RegistryObject<Item> BLUE_FLAMING_PRESSED_STEEL = registerBlockItem(
            "blue_flaming_pressed_steel", SteelBlocks.BLUE_FLAMING_PRESSED_STEEL);
    public static final RegistryObject<Item> BLUE_FLAMING_PRESSED_STEEL_SLAB = registerBlockItem(
            "blue_flaming_pressed_steel_slab", SteelBlocks.BLUE_FLAMING_PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> BLUE_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "blue_flaming_pressed_steel_vertical_slab", SteelBlocks.BLUE_FLAMING_PRESSED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLUE_FLAMING_PRESSED_STEEL_STAIRS = registerBlockItem(
            "blue_flaming_pressed_steel_stairs", SteelBlocks.BLUE_FLAMING_PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> BLUE_FLAMING_PRESSED_STEEL_WALL = registerBlockItem(
            "blue_flaming_pressed_steel_wall", SteelBlocks.BLUE_FLAMING_PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> BLUE_FLAMING_STEEL_CASING = registerBlockItem("blue_flaming_steel_casing",
            SteelBlocks.BLUE_FLAMING_STEEL_CASING);
    public static final RegistryObject<Item> BLUE_FLAMING_STEEL_GRATE = registerBlockItem("blue_flaming_steel_grate",
            SteelBlocks.BLUE_FLAMING_STEEL_GRATE);
    public static final RegistryObject<Item> BLUE_FLAMING_STEEL_PILLAR = registerBlockItem("blue_flaming_steel_pillar",
            SteelBlocks.BLUE_FLAMING_STEEL_PILLAR);
    public static final RegistryObject<Item> BLUE_FLAMING_BOLTED_STEEL_PILLAR = registerBlockItem(
            "blue_flaming_bolted_steel_pillar", SteelBlocks.BLUE_FLAMING_BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> BROWN_FLAMING_STEEL_BLOCK = registerBlockItem("brown_flaming_steel_block",
            SteelBlocks.BROWN_FLAMING_STEEL_BLOCK);
    public static final RegistryObject<Item> BROWN_FLAMING_STEEL_BLOCK_SLAB = registerBlockItem(
            "brown_flaming_steel_block_slab", SteelBlocks.BROWN_FLAMING_STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> BROWN_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "brown_flaming_steel_block_vertical_slab", SteelBlocks.BROWN_FLAMING_STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> BROWN_FLAMING_STEEL_BLOCK_STAIRS = registerBlockItem(
            "brown_flaming_steel_block_stairs", SteelBlocks.BROWN_FLAMING_STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> BROWN_FLAMING_STEEL_BLOCK_WALL = registerBlockItem(
            "brown_flaming_steel_block_wall", SteelBlocks.BROWN_FLAMING_STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> BROWN_FLAMING_CUT_STEEL = registerBlockItem("brown_flaming_cut_steel",
            SteelBlocks.BROWN_FLAMING_CUT_STEEL);
    public static final RegistryObject<Item> BROWN_FLAMING_CUT_STEEL_SLAB = registerBlockItem(
            "brown_flaming_cut_steel_slab", SteelBlocks.BROWN_FLAMING_CUT_STEEL_SLAB);
    public static final RegistryObject<Item> BROWN_FLAMING_CUT_STEEL_VERTICAL_SLAB = registerBlockItem(
            "brown_flaming_cut_steel_vertical_slab", SteelBlocks.BROWN_FLAMING_CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BROWN_FLAMING_CUT_STEEL_STAIRS = registerBlockItem(
            "brown_flaming_cut_steel_stairs", SteelBlocks.BROWN_FLAMING_CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> BROWN_FLAMING_CUT_STEEL_WALL = registerBlockItem(
            "brown_flaming_cut_steel_wall", SteelBlocks.BROWN_FLAMING_CUT_STEEL_WALL);
    public static final RegistryObject<Item> BROWN_FLAMING_POLISHED_STEEL = registerBlockItem(
            "brown_flaming_polished_steel", SteelBlocks.BROWN_FLAMING_POLISHED_STEEL);
    public static final RegistryObject<Item> BROWN_FLAMING_POLISHED_STEEL_SLAB = registerBlockItem(
            "brown_flaming_polished_steel_slab", SteelBlocks.BROWN_FLAMING_POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> BROWN_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "brown_flaming_polished_steel_vertical_slab", SteelBlocks.BROWN_FLAMING_POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BROWN_FLAMING_POLISHED_STEEL_STAIRS = registerBlockItem(
            "brown_flaming_polished_steel_stairs", SteelBlocks.BROWN_FLAMING_POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> BROWN_FLAMING_POLISHED_STEEL_WALL = registerBlockItem(
            "brown_flaming_polished_steel_wall", SteelBlocks.BROWN_FLAMING_POLISHED_STEEL_WALL);
    public static final RegistryObject<Item> BROWN_FLAMING_PRESSED_STEEL = registerBlockItem(
            "brown_flaming_pressed_steel", SteelBlocks.BROWN_FLAMING_PRESSED_STEEL);
    public static final RegistryObject<Item> BROWN_FLAMING_PRESSED_STEEL_SLAB = registerBlockItem(
            "brown_flaming_pressed_steel_slab", SteelBlocks.BROWN_FLAMING_PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> BROWN_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "brown_flaming_pressed_steel_vertical_slab", SteelBlocks.BROWN_FLAMING_PRESSED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BROWN_FLAMING_PRESSED_STEEL_STAIRS = registerBlockItem(
            "brown_flaming_pressed_steel_stairs", SteelBlocks.BROWN_FLAMING_PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> BROWN_FLAMING_PRESSED_STEEL_WALL = registerBlockItem(
            "brown_flaming_pressed_steel_wall", SteelBlocks.BROWN_FLAMING_PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> BROWN_FLAMING_STEEL_CASING = registerBlockItem(
            "brown_flaming_steel_casing", SteelBlocks.BROWN_FLAMING_STEEL_CASING);
    public static final RegistryObject<Item> BROWN_FLAMING_STEEL_GRATE = registerBlockItem("brown_flaming_steel_grate",
            SteelBlocks.BROWN_FLAMING_STEEL_GRATE);
    public static final RegistryObject<Item> BROWN_FLAMING_STEEL_PILLAR = registerBlockItem(
            "brown_flaming_steel_pillar", SteelBlocks.BROWN_FLAMING_STEEL_PILLAR);
    public static final RegistryObject<Item> BROWN_FLAMING_BOLTED_STEEL_PILLAR = registerBlockItem(
            "brown_flaming_bolted_steel_pillar", SteelBlocks.BROWN_FLAMING_BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> GREEN_FLAMING_STEEL_BLOCK = registerBlockItem("green_flaming_steel_block",
            SteelBlocks.GREEN_FLAMING_STEEL_BLOCK);
    public static final RegistryObject<Item> GREEN_FLAMING_STEEL_BLOCK_SLAB = registerBlockItem(
            "green_flaming_steel_block_slab", SteelBlocks.GREEN_FLAMING_STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> GREEN_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "green_flaming_steel_block_vertical_slab", SteelBlocks.GREEN_FLAMING_STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> GREEN_FLAMING_STEEL_BLOCK_STAIRS = registerBlockItem(
            "green_flaming_steel_block_stairs", SteelBlocks.GREEN_FLAMING_STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> GREEN_FLAMING_STEEL_BLOCK_WALL = registerBlockItem(
            "green_flaming_steel_block_wall", SteelBlocks.GREEN_FLAMING_STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> GREEN_FLAMING_CUT_STEEL = registerBlockItem("green_flaming_cut_steel",
            SteelBlocks.GREEN_FLAMING_CUT_STEEL);
    public static final RegistryObject<Item> GREEN_FLAMING_CUT_STEEL_SLAB = registerBlockItem(
            "green_flaming_cut_steel_slab", SteelBlocks.GREEN_FLAMING_CUT_STEEL_SLAB);
    public static final RegistryObject<Item> GREEN_FLAMING_CUT_STEEL_VERTICAL_SLAB = registerBlockItem(
            "green_flaming_cut_steel_vertical_slab", SteelBlocks.GREEN_FLAMING_CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> GREEN_FLAMING_CUT_STEEL_STAIRS = registerBlockItem(
            "green_flaming_cut_steel_stairs", SteelBlocks.GREEN_FLAMING_CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> GREEN_FLAMING_CUT_STEEL_WALL = registerBlockItem(
            "green_flaming_cut_steel_wall", SteelBlocks.GREEN_FLAMING_CUT_STEEL_WALL);
    public static final RegistryObject<Item> GREEN_FLAMING_POLISHED_STEEL = registerBlockItem(
            "green_flaming_polished_steel", SteelBlocks.GREEN_FLAMING_POLISHED_STEEL);
    public static final RegistryObject<Item> GREEN_FLAMING_POLISHED_STEEL_SLAB = registerBlockItem(
            "green_flaming_polished_steel_slab", SteelBlocks.GREEN_FLAMING_POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> GREEN_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "green_flaming_polished_steel_vertical_slab", SteelBlocks.GREEN_FLAMING_POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> GREEN_FLAMING_POLISHED_STEEL_STAIRS = registerBlockItem(
            "green_flaming_polished_steel_stairs", SteelBlocks.GREEN_FLAMING_POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> GREEN_FLAMING_POLISHED_STEEL_WALL = registerBlockItem(
            "green_flaming_polished_steel_wall", SteelBlocks.GREEN_FLAMING_POLISHED_STEEL_WALL);
    public static final RegistryObject<Item> GREEN_FLAMING_PRESSED_STEEL = registerBlockItem(
            "green_flaming_pressed_steel", SteelBlocks.GREEN_FLAMING_PRESSED_STEEL);
    public static final RegistryObject<Item> GREEN_FLAMING_PRESSED_STEEL_SLAB = registerBlockItem(
            "green_flaming_pressed_steel_slab", SteelBlocks.GREEN_FLAMING_PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> GREEN_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "green_flaming_pressed_steel_vertical_slab", SteelBlocks.GREEN_FLAMING_PRESSED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> GREEN_FLAMING_PRESSED_STEEL_STAIRS = registerBlockItem(
            "green_flaming_pressed_steel_stairs", SteelBlocks.GREEN_FLAMING_PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> GREEN_FLAMING_PRESSED_STEEL_WALL = registerBlockItem(
            "green_flaming_pressed_steel_wall", SteelBlocks.GREEN_FLAMING_PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> GREEN_FLAMING_STEEL_CASING = registerBlockItem(
            "green_flaming_steel_casing", SteelBlocks.GREEN_FLAMING_STEEL_CASING);
    public static final RegistryObject<Item> GREEN_FLAMING_STEEL_GRATE = registerBlockItem("green_flaming_steel_grate",
            SteelBlocks.GREEN_FLAMING_STEEL_GRATE);
    public static final RegistryObject<Item> GREEN_FLAMING_STEEL_PILLAR = registerBlockItem(
            "green_flaming_steel_pillar", SteelBlocks.GREEN_FLAMING_STEEL_PILLAR);
    public static final RegistryObject<Item> GREEN_FLAMING_BOLTED_STEEL_PILLAR = registerBlockItem(
            "green_flaming_bolted_steel_pillar", SteelBlocks.GREEN_FLAMING_BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> RED_FLAMING_STEEL_BLOCK = registerBlockItem("red_flaming_steel_block",
            SteelBlocks.RED_FLAMING_STEEL_BLOCK);
    public static final RegistryObject<Item> RED_FLAMING_STEEL_BLOCK_SLAB = registerBlockItem(
            "red_flaming_steel_block_slab", SteelBlocks.RED_FLAMING_STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> RED_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "red_flaming_steel_block_vertical_slab", SteelBlocks.RED_FLAMING_STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_FLAMING_STEEL_BLOCK_STAIRS = registerBlockItem(
            "red_flaming_steel_block_stairs", SteelBlocks.RED_FLAMING_STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> RED_FLAMING_STEEL_BLOCK_WALL = registerBlockItem(
            "red_flaming_steel_block_wall", SteelBlocks.RED_FLAMING_STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> RED_FLAMING_CUT_STEEL = registerBlockItem("red_flaming_cut_steel",
            SteelBlocks.RED_FLAMING_CUT_STEEL);
    public static final RegistryObject<Item> RED_FLAMING_CUT_STEEL_SLAB = registerBlockItem(
            "red_flaming_cut_steel_slab", SteelBlocks.RED_FLAMING_CUT_STEEL_SLAB);
    public static final RegistryObject<Item> RED_FLAMING_CUT_STEEL_VERTICAL_SLAB = registerBlockItem(
            "red_flaming_cut_steel_vertical_slab", SteelBlocks.RED_FLAMING_CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_FLAMING_CUT_STEEL_STAIRS = registerBlockItem(
            "red_flaming_cut_steel_stairs", SteelBlocks.RED_FLAMING_CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> RED_FLAMING_CUT_STEEL_WALL = registerBlockItem(
            "red_flaming_cut_steel_wall", SteelBlocks.RED_FLAMING_CUT_STEEL_WALL);
    public static final RegistryObject<Item> RED_FLAMING_POLISHED_STEEL = registerBlockItem(
            "red_flaming_polished_steel", SteelBlocks.RED_FLAMING_POLISHED_STEEL);
    public static final RegistryObject<Item> RED_FLAMING_POLISHED_STEEL_SLAB = registerBlockItem(
            "red_flaming_polished_steel_slab", SteelBlocks.RED_FLAMING_POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> RED_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "red_flaming_polished_steel_vertical_slab", SteelBlocks.RED_FLAMING_POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_FLAMING_POLISHED_STEEL_STAIRS = registerBlockItem(
            "red_flaming_polished_steel_stairs", SteelBlocks.RED_FLAMING_POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> RED_FLAMING_POLISHED_STEEL_WALL = registerBlockItem(
            "red_flaming_polished_steel_wall", SteelBlocks.RED_FLAMING_POLISHED_STEEL_WALL);
    public static final RegistryObject<Item> RED_FLAMING_PRESSED_STEEL = registerBlockItem("red_flaming_pressed_steel",
            SteelBlocks.RED_FLAMING_PRESSED_STEEL);
    public static final RegistryObject<Item> RED_FLAMING_PRESSED_STEEL_SLAB = registerBlockItem(
            "red_flaming_pressed_steel_slab", SteelBlocks.RED_FLAMING_PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> RED_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "red_flaming_pressed_steel_vertical_slab", SteelBlocks.RED_FLAMING_PRESSED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_FLAMING_PRESSED_STEEL_STAIRS = registerBlockItem(
            "red_flaming_pressed_steel_stairs", SteelBlocks.RED_FLAMING_PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> RED_FLAMING_PRESSED_STEEL_WALL = registerBlockItem(
            "red_flaming_pressed_steel_wall", SteelBlocks.RED_FLAMING_PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> RED_FLAMING_STEEL_CASING = registerBlockItem("red_flaming_steel_casing",
            SteelBlocks.RED_FLAMING_STEEL_CASING);
    public static final RegistryObject<Item> RED_FLAMING_STEEL_GRATE = registerBlockItem("red_flaming_steel_grate",
            SteelBlocks.RED_FLAMING_STEEL_GRATE);
    public static final RegistryObject<Item> RED_FLAMING_STEEL_PILLAR = registerBlockItem("red_flaming_steel_pillar",
            SteelBlocks.RED_FLAMING_STEEL_PILLAR);
    public static final RegistryObject<Item> RED_FLAMING_BOLTED_STEEL_PILLAR = registerBlockItem(
            "red_flaming_bolted_steel_pillar", SteelBlocks.RED_FLAMING_BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> BLACK_FLAMING_STEEL_BLOCK = registerBlockItem("black_flaming_steel_block",
            SteelBlocks.BLACK_FLAMING_STEEL_BLOCK);
    public static final RegistryObject<Item> BLACK_FLAMING_STEEL_BLOCK_SLAB = registerBlockItem(
            "black_flaming_steel_block_slab", SteelBlocks.BLACK_FLAMING_STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> BLACK_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "black_flaming_steel_block_vertical_slab", SteelBlocks.BLACK_FLAMING_STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLACK_FLAMING_STEEL_BLOCK_STAIRS = registerBlockItem(
            "black_flaming_steel_block_stairs", SteelBlocks.BLACK_FLAMING_STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> BLACK_FLAMING_STEEL_BLOCK_WALL = registerBlockItem(
            "black_flaming_steel_block_wall", SteelBlocks.BLACK_FLAMING_STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> BLACK_FLAMING_CUT_STEEL = registerBlockItem("black_flaming_cut_steel",
            SteelBlocks.BLACK_FLAMING_CUT_STEEL);
    public static final RegistryObject<Item> BLACK_FLAMING_CUT_STEEL_SLAB = registerBlockItem(
            "black_flaming_cut_steel_slab", SteelBlocks.BLACK_FLAMING_CUT_STEEL_SLAB);
    public static final RegistryObject<Item> BLACK_FLAMING_CUT_STEEL_VERTICAL_SLAB = registerBlockItem(
            "black_flaming_cut_steel_vertical_slab", SteelBlocks.BLACK_FLAMING_CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLACK_FLAMING_CUT_STEEL_STAIRS = registerBlockItem(
            "black_flaming_cut_steel_stairs", SteelBlocks.BLACK_FLAMING_CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> BLACK_FLAMING_CUT_STEEL_WALL = registerBlockItem(
            "black_flaming_cut_steel_wall", SteelBlocks.BLACK_FLAMING_CUT_STEEL_WALL);
    public static final RegistryObject<Item> BLACK_FLAMING_POLISHED_STEEL = registerBlockItem(
            "black_flaming_polished_steel", SteelBlocks.BLACK_FLAMING_POLISHED_STEEL);
    public static final RegistryObject<Item> BLACK_FLAMING_POLISHED_STEEL_SLAB = registerBlockItem(
            "black_flaming_polished_steel_slab", SteelBlocks.BLACK_FLAMING_POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> BLACK_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "black_flaming_polished_steel_vertical_slab", SteelBlocks.BLACK_FLAMING_POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLACK_FLAMING_POLISHED_STEEL_STAIRS = registerBlockItem(
            "black_flaming_polished_steel_stairs", SteelBlocks.BLACK_FLAMING_POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> BLACK_FLAMING_POLISHED_STEEL_WALL = registerBlockItem(
            "black_flaming_polished_steel_wall", SteelBlocks.BLACK_FLAMING_POLISHED_STEEL_WALL);
    public static final RegistryObject<Item> BLACK_FLAMING_PRESSED_STEEL = registerBlockItem(
            "black_flaming_pressed_steel", SteelBlocks.BLACK_FLAMING_PRESSED_STEEL);
    public static final RegistryObject<Item> BLACK_FLAMING_PRESSED_STEEL_SLAB = registerBlockItem(
            "black_flaming_pressed_steel_slab", SteelBlocks.BLACK_FLAMING_PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> BLACK_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "black_flaming_pressed_steel_vertical_slab", SteelBlocks.BLACK_FLAMING_PRESSED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLACK_FLAMING_PRESSED_STEEL_STAIRS = registerBlockItem(
            "black_flaming_pressed_steel_stairs", SteelBlocks.BLACK_FLAMING_PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> BLACK_FLAMING_PRESSED_STEEL_WALL = registerBlockItem(
            "black_flaming_pressed_steel_wall", SteelBlocks.BLACK_FLAMING_PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> BLACK_FLAMING_STEEL_CASING = registerBlockItem(
            "black_flaming_steel_casing", SteelBlocks.BLACK_FLAMING_STEEL_CASING);
    public static final RegistryObject<Item> BLACK_FLAMING_STEEL_GRATE = registerBlockItem("black_flaming_steel_grate",
            SteelBlocks.BLACK_FLAMING_STEEL_GRATE);
    public static final RegistryObject<Item> BLACK_FLAMING_STEEL_PILLAR = registerBlockItem(
            "black_flaming_steel_pillar", SteelBlocks.BLACK_FLAMING_STEEL_PILLAR);
    public static final RegistryObject<Item> BLACK_FLAMING_BOLTED_STEEL_PILLAR = registerBlockItem(
            "black_flaming_bolted_steel_pillar", SteelBlocks.BLACK_FLAMING_BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> HOLLOW_OAK_LOG = registerBlockItem("hollow_oak_log",
            SteelBlocks.HOLLOW_OAK_LOG);
    public static final RegistryObject<Item> STRIPPED_HOLLOW_OAK_LOG = registerBlockItem("stripped_hollow_oak_log",
            CardboardBlocks.STRIPPED_HOLLOW_OAK_LOG);
    public static final RegistryObject<Item> HOLLOW_SPRUCE_LOG = registerBlockItem("hollow_spruce_log",
            CardboardBlocks.HOLLOW_SPRUCE_LOG);
}
