package com.kingodogo.buildscape.registry.items;

import com.kingodogo.buildscape.item.MuffBlockItem;
import com.kingodogo.buildscape.item.PoplarBoatItem;
import com.kingodogo.buildscape.registry.ModBlocks;
import com.kingodogo.buildscape.registry.ModCreativeModeTab;
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
            ModBlocks.SPRUCE_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_SPRUCE_LOG_VERTICAL_SLAB = registerBlockItem(
            "stripped_spruce_log_vertical_slab", ModBlocks.STRIPPED_SPRUCE_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> BIRCH_LOG_VERTICAL_SLAB = registerBlockItem("birch_log_vertical_slab",
            ModBlocks.BIRCH_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_BIRCH_LOG_VERTICAL_SLAB = registerBlockItem(
            "stripped_birch_log_vertical_slab", ModBlocks.STRIPPED_BIRCH_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> JUNGLE_LOG_VERTICAL_SLAB = registerBlockItem("jungle_log_vertical_slab",
            ModBlocks.JUNGLE_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_JUNGLE_LOG_VERTICAL_SLAB = registerBlockItem(
            "stripped_jungle_log_vertical_slab", ModBlocks.STRIPPED_JUNGLE_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> ACACIA_LOG_VERTICAL_SLAB = registerBlockItem("acacia_log_vertical_slab",
            ModBlocks.ACACIA_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_ACACIA_LOG_VERTICAL_SLAB = registerBlockItem(
            "stripped_acacia_log_vertical_slab", ModBlocks.STRIPPED_ACACIA_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> DARK_OAK_LOG_VERTICAL_SLAB = registerBlockItem(
            "dark_oak_log_vertical_slab", ModBlocks.DARK_OAK_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_DARK_OAK_LOG_VERTICAL_SLAB = registerBlockItem(
            "stripped_dark_oak_log_vertical_slab", ModBlocks.STRIPPED_DARK_OAK_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> CRIMSON_STEM_VERTICAL_SLAB = registerBlockItem(
            "crimson_stem_vertical_slab", ModBlocks.CRIMSON_STEM_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_CRIMSON_STEM_VERTICAL_SLAB = registerBlockItem(
            "stripped_crimson_stem_vertical_slab", ModBlocks.STRIPPED_CRIMSON_STEM_VERTICAL_SLAB);
    public static final RegistryObject<Item> WARPED_STEM_VERTICAL_SLAB = registerBlockItem("warped_stem_vertical_slab",
            ModBlocks.WARPED_STEM_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_WARPED_STEM_VERTICAL_SLAB = registerBlockItem(
            "stripped_warped_stem_vertical_slab", ModBlocks.STRIPPED_WARPED_STEM_VERTICAL_SLAB);
    public static final RegistryObject<Item> MANGROVE_LOG_VERTICAL_SLAB = registerBlockItem(
            "mangrove_log_vertical_slab", ModBlocks.MANGROVE_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_MANGROVE_LOG_VERTICAL_SLAB = registerBlockItem(
            "stripped_mangrove_log_vertical_slab", ModBlocks.STRIPPED_MANGROVE_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> ASHPEN_LOG_VERTICAL_SLAB = registerBlockItem("ashpen_log_vertical_slab",
            ModBlocks.ASHPEN_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> ASHPEN_WOOD_VERTICAL_SLAB = registerBlockItem("ashpen_wood_vertical_slab",
            ModBlocks.ASHPEN_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_ASHPEN_LOG_VERTICAL_SLAB = registerBlockItem(
            "stripped_ashpen_log_vertical_slab", ModBlocks.STRIPPED_ASHPEN_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_ASHPEN_WOOD_VERTICAL_SLAB = registerBlockItem(
            "stripped_ashpen_wood_vertical_slab", ModBlocks.STRIPPED_ASHPEN_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> SPOOL = registerBlockItem("spool", ModBlocks.SPOOL);
    public static final RegistryObject<Item> GLOWING_SPOOL = registerBlockItem("glowing_spool",
            ModBlocks.GLOWING_SPOOL);
    public static final RegistryObject<Item> WHITE_SPOOL = registerBlockItem("white_spool", ModBlocks.WHITE_SPOOL);
    public static final RegistryObject<Item> LIGHT_GRAY_SPOOL = registerBlockItem("light_gray_spool",
            ModBlocks.LIGHT_GRAY_SPOOL);
    public static final RegistryObject<Item> GRAY_SPOOL = registerBlockItem("gray_spool", ModBlocks.GRAY_SPOOL);
    public static final RegistryObject<Item> BLACK_SPOOL = registerBlockItem("black_spool", ModBlocks.BLACK_SPOOL);
    public static final RegistryObject<Item> BROWN_SPOOL = registerBlockItem("brown_spool", ModBlocks.BROWN_SPOOL);
    public static final RegistryObject<Item> RED_SPOOL = registerBlockItem("red_spool", ModBlocks.RED_SPOOL);
    public static final RegistryObject<Item> ORANGE_SPOOL = registerBlockItem("orange_spool", ModBlocks.ORANGE_SPOOL);
    public static final RegistryObject<Item> YELLOW_SPOOL = registerBlockItem("yellow_spool", ModBlocks.YELLOW_SPOOL);
    public static final RegistryObject<Item> LIME_SPOOL = registerBlockItem("lime_spool", ModBlocks.LIME_SPOOL);
    public static final RegistryObject<Item> GREEN_SPOOL = registerBlockItem("green_spool", ModBlocks.GREEN_SPOOL);
    public static final RegistryObject<Item> CYAN_SPOOL = registerBlockItem("cyan_spool", ModBlocks.CYAN_SPOOL);
    public static final RegistryObject<Item> LIGHT_BLUE_SPOOL = registerBlockItem("light_blue_spool",
            ModBlocks.LIGHT_BLUE_SPOOL);
    public static final RegistryObject<Item> BLUE_SPOOL = registerBlockItem("blue_spool", ModBlocks.BLUE_SPOOL);
    public static final RegistryObject<Item> PURPLE_SPOOL = registerBlockItem("purple_spool", ModBlocks.PURPLE_SPOOL);
    public static final RegistryObject<Item> MAGENTA_SPOOL = registerBlockItem("magenta_spool",
            ModBlocks.MAGENTA_SPOOL);
    public static final RegistryObject<Item> PINK_SPOOL = registerBlockItem("pink_spool", ModBlocks.PINK_SPOOL);
    public static final RegistryObject<Item> WHITE_DYE_SACK = registerBlockItem("white_dye_sack",
            ModBlocks.WHITE_DYE_SACK);
    public static final RegistryObject<Item> LIGHT_GRAY_DYE_SACK = registerBlockItem("light_gray_dye_sack",
            ModBlocks.LIGHT_GRAY_DYE_SACK);
    public static final RegistryObject<Item> GRAY_DYE_SACK = registerBlockItem("gray_dye_sack",
            ModBlocks.GRAY_DYE_SACK);
    public static final RegistryObject<Item> BLACK_DYE_SACK = registerBlockItem("black_dye_sack",
            ModBlocks.BLACK_DYE_SACK);
    public static final RegistryObject<Item> BROWN_DYE_SACK = registerBlockItem("brown_dye_sack",
            ModBlocks.BROWN_DYE_SACK);
    public static final RegistryObject<Item> RED_DYE_SACK = registerBlockItem("red_dye_sack", ModBlocks.RED_DYE_SACK);
    public static final RegistryObject<Item> ORANGE_DYE_SACK = registerBlockItem("orange_dye_sack",
            ModBlocks.ORANGE_DYE_SACK);
    public static final RegistryObject<Item> YELLOW_DYE_SACK = registerBlockItem("yellow_dye_sack",
            ModBlocks.YELLOW_DYE_SACK);
    public static final RegistryObject<Item> LIME_DYE_SACK = registerBlockItem("lime_dye_sack",
            ModBlocks.LIME_DYE_SACK);
    public static final RegistryObject<Item> GREEN_DYE_SACK = registerBlockItem("green_dye_sack",
            ModBlocks.GREEN_DYE_SACK);
    public static final RegistryObject<Item> CYAN_DYE_SACK = registerBlockItem("cyan_dye_sack",
            ModBlocks.CYAN_DYE_SACK);
    public static final RegistryObject<Item> LIGHT_BLUE_DYE_SACK = registerBlockItem("light_blue_dye_sack",
            ModBlocks.LIGHT_BLUE_DYE_SACK);
    public static final RegistryObject<Item> BLUE_DYE_SACK = registerBlockItem("blue_dye_sack",
            ModBlocks.BLUE_DYE_SACK);
    public static final RegistryObject<Item> PURPLE_DYE_SACK = registerBlockItem("purple_dye_sack",
            ModBlocks.PURPLE_DYE_SACK);
    public static final RegistryObject<Item> MAGENTA_DYE_SACK = registerBlockItem("magenta_dye_sack",
            ModBlocks.MAGENTA_DYE_SACK);
    public static final RegistryObject<Item> PINK_DYE_SACK = registerBlockItem("pink_dye_sack",
            ModBlocks.PINK_DYE_SACK);
    public static final RegistryObject<Item> GLOW_INK_SACK = registerBlockItem("glow_ink_sack",
            ModBlocks.GLOW_INK_SACK);
    public static final RegistryObject<Item> RED_MOSS_BLOCK = registerBlockItem("red_moss_block",
            ModBlocks.RED_MOSS_BLOCK);
    public static final RegistryObject<Item> RED_MOSS_BLOCK_SLAB = registerBlockItem("red_moss_block_slab",
            ModBlocks.RED_MOSS_BLOCK_SLAB);
    public static final RegistryObject<Item> RED_MOSS_BLOCK_STAIRS = registerBlockItem("red_moss_block_stairs",
            ModBlocks.RED_MOSS_BLOCK_STAIRS);
    public static final RegistryObject<Item> RED_MOSS_LAYERS = registerBlockItem("red_moss_layers",
            ModBlocks.RED_MOSS_LAYERS);
    public static final RegistryObject<Item> RED_MOSS_CARPET = registerBlockItem("red_moss_carpet",
            ModBlocks.RED_MOSS_CARPET);
    public static final RegistryObject<Item> RED_MOSS_OVERLAY = registerBlockItem("red_moss_overlay",
            ModBlocks.RED_MOSS_OVERLAY);
    public static final RegistryObject<Item> ORANGE_MOSS_BLOCK = registerBlockItem("orange_moss_block",
            ModBlocks.ORANGE_MOSS_BLOCK);
    public static final RegistryObject<Item> ORANGE_MOSS_BLOCK_SLAB = registerBlockItem("orange_moss_block_slab",
            ModBlocks.ORANGE_MOSS_BLOCK_SLAB);
    public static final RegistryObject<Item> ORANGE_MOSS_BLOCK_STAIRS = registerBlockItem("orange_moss_block_stairs",
            ModBlocks.ORANGE_MOSS_BLOCK_STAIRS);
    public static final RegistryObject<Item> ORANGE_MOSS_LAYERS = registerBlockItem("orange_moss_layers",
            ModBlocks.ORANGE_MOSS_LAYERS);
    public static final RegistryObject<Item> ORANGE_MOSS_CARPET = registerBlockItem("orange_moss_carpet",
            ModBlocks.ORANGE_MOSS_CARPET);
    public static final RegistryObject<Item> ORANGE_MOSS_OVERLAY = registerBlockItem("orange_moss_overlay",
            ModBlocks.ORANGE_MOSS_OVERLAY);
    public static final RegistryObject<Item> YELLOW_MOSS_BLOCK = registerBlockItem("yellow_moss_block",
            ModBlocks.YELLOW_MOSS_BLOCK);
    public static final RegistryObject<Item> YELLOW_MOSS_BLOCK_SLAB = registerBlockItem("yellow_moss_block_slab",
            ModBlocks.YELLOW_MOSS_BLOCK_SLAB);
    public static final RegistryObject<Item> YELLOW_MOSS_BLOCK_STAIRS = registerBlockItem("yellow_moss_block_stairs",
            ModBlocks.YELLOW_MOSS_BLOCK_STAIRS);
    public static final RegistryObject<Item> YELLOW_MOSS_LAYERS = registerBlockItem("yellow_moss_layers",
            ModBlocks.YELLOW_MOSS_LAYERS);
    public static final RegistryObject<Item> YELLOW_MOSS_CARPET = registerBlockItem("yellow_moss_carpet",
            ModBlocks.YELLOW_MOSS_CARPET);
    public static final RegistryObject<Item> YELLOW_MOSS_OVERLAY = registerBlockItem("yellow_moss_overlay",
            ModBlocks.YELLOW_MOSS_OVERLAY);
    public static final RegistryObject<Item> TUFF_SLAB = registerBlockItem("tuff_slab", ModBlocks.TUFF_SLAB);
    public static final RegistryObject<Item> TUFF_STAIRS = registerBlockItem("tuff_stairs", ModBlocks.TUFF_STAIRS);
    public static final RegistryObject<Item> TUFF_WALL = registerBlockItem("tuff_wall", ModBlocks.TUFF_WALL);
    public static final RegistryObject<Item> POLISHED_TUFF = registerBlockItem("polished_tuff",
            ModBlocks.POLISHED_TUFF);
    public static final RegistryObject<Item> POLISHED_TUFF_SLAB = registerBlockItem("polished_tuff_slab",
            ModBlocks.POLISHED_TUFF_SLAB);
    public static final RegistryObject<Item> POLISHED_TUFF_STAIRS = registerBlockItem("polished_tuff_stairs",
            ModBlocks.POLISHED_TUFF_STAIRS);
    public static final RegistryObject<Item> POLISHED_TUFF_WALL = registerBlockItem("polished_tuff_wall",
            ModBlocks.POLISHED_TUFF_WALL);
    public static final RegistryObject<Item> CHISELED_TUFF = registerBlockItem("chiseled_tuff",
            ModBlocks.CHISELED_TUFF);
    public static final RegistryObject<Item> TUFF_BRICKS = registerBlockItem("tuff_bricks", ModBlocks.TUFF_BRICKS);
    public static final RegistryObject<Item> TUFF_BRICK_SLAB = registerBlockItem("tuff_brick_slab",
            ModBlocks.TUFF_BRICK_SLAB);
    public static final RegistryObject<Item> TUFF_BRICK_STAIRS = registerBlockItem("tuff_brick_stairs",
            ModBlocks.TUFF_BRICK_STAIRS);
    public static final RegistryObject<Item> TUFF_BRICK_WALL = registerBlockItem("tuff_brick_wall",
            ModBlocks.TUFF_BRICK_WALL);
    public static final RegistryObject<Item> CHISELED_TUFF_BRICKS = registerBlockItem("chiseled_tuff_bricks",
            ModBlocks.CHISELED_TUFF_BRICKS);
    public static final RegistryObject<Item> WHITE_WOOL_SLAB = registerBlockItem("white_wool_slab",
            ModBlocks.WHITE_WOOL_SLAB);
    public static final RegistryObject<Item> WHITE_WOOL_STAIRS = registerBlockItem("white_wool_stairs",
            ModBlocks.WHITE_WOOL_STAIRS);
    public static final RegistryObject<Item> WHITE_WOOL_WALL = registerBlockItem("white_wool_wall",
            ModBlocks.WHITE_WOOL_WALL);
    public static final RegistryObject<Item> ORANGE_WOOL_SLAB = registerBlockItem("orange_wool_slab",
            ModBlocks.ORANGE_WOOL_SLAB);
    public static final RegistryObject<Item> ORANGE_WOOL_STAIRS = registerBlockItem("orange_wool_stairs",
            ModBlocks.ORANGE_WOOL_STAIRS);
    public static final RegistryObject<Item> ORANGE_WOOL_WALL = registerBlockItem("orange_wool_wall",
            ModBlocks.ORANGE_WOOL_WALL);
    public static final RegistryObject<Item> MAGENTA_WOOL_SLAB = registerBlockItem("magenta_wool_slab",
            ModBlocks.MAGENTA_WOOL_SLAB);
    public static final RegistryObject<Item> MAGENTA_WOOL_STAIRS = registerBlockItem("magenta_wool_stairs",
            ModBlocks.MAGENTA_WOOL_STAIRS);
    public static final RegistryObject<Item> MAGENTA_WOOL_WALL = registerBlockItem("magenta_wool_wall",
            ModBlocks.MAGENTA_WOOL_WALL);
    public static final RegistryObject<Item> LIGHT_BLUE_WOOL_SLAB = registerBlockItem("light_blue_wool_slab",
            ModBlocks.LIGHT_BLUE_WOOL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_WOOL_STAIRS = registerBlockItem("light_blue_wool_stairs",
            ModBlocks.LIGHT_BLUE_WOOL_STAIRS);
    public static final RegistryObject<Item> LIGHT_BLUE_WOOL_WALL = registerBlockItem("light_blue_wool_wall",
            ModBlocks.LIGHT_BLUE_WOOL_WALL);
    public static final RegistryObject<Item> YELLOW_WOOL_SLAB = registerBlockItem("yellow_wool_slab",
            ModBlocks.YELLOW_WOOL_SLAB);
    public static final RegistryObject<Item> YELLOW_WOOL_STAIRS = registerBlockItem("yellow_wool_stairs",
            ModBlocks.YELLOW_WOOL_STAIRS);
    public static final RegistryObject<Item> YELLOW_WOOL_WALL = registerBlockItem("yellow_wool_wall",
            ModBlocks.YELLOW_WOOL_WALL);
    public static final RegistryObject<Item> LIME_WOOL_SLAB = registerBlockItem("lime_wool_slab",
            ModBlocks.LIME_WOOL_SLAB);
    public static final RegistryObject<Item> LIME_WOOL_STAIRS = registerBlockItem("lime_wool_stairs",
            ModBlocks.LIME_WOOL_STAIRS);
    public static final RegistryObject<Item> LIME_WOOL_WALL = registerBlockItem("lime_wool_wall",
            ModBlocks.LIME_WOOL_WALL);
    public static final RegistryObject<Item> PINK_WOOL_SLAB = registerBlockItem("pink_wool_slab",
            ModBlocks.PINK_WOOL_SLAB);
    public static final RegistryObject<Item> PINK_WOOL_STAIRS = registerBlockItem("pink_wool_stairs",
            ModBlocks.PINK_WOOL_STAIRS);
    public static final RegistryObject<Item> PINK_WOOL_WALL = registerBlockItem("pink_wool_wall",
            ModBlocks.PINK_WOOL_WALL);
    public static final RegistryObject<Item> GRAY_WOOL_SLAB = registerBlockItem("gray_wool_slab",
            ModBlocks.GRAY_WOOL_SLAB);
    public static final RegistryObject<Item> GRAY_WOOL_STAIRS = registerBlockItem("gray_wool_stairs",
            ModBlocks.GRAY_WOOL_STAIRS);
    public static final RegistryObject<Item> GRAY_WOOL_WALL = registerBlockItem("gray_wool_wall",
            ModBlocks.GRAY_WOOL_WALL);
    public static final RegistryObject<Item> LIGHT_GRAY_WOOL_SLAB = registerBlockItem("light_gray_wool_slab",
            ModBlocks.LIGHT_GRAY_WOOL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_WOOL_STAIRS = registerBlockItem("light_gray_wool_stairs",
            ModBlocks.LIGHT_GRAY_WOOL_STAIRS);
    public static final RegistryObject<Item> LIGHT_GRAY_WOOL_WALL = registerBlockItem("light_gray_wool_wall",
            ModBlocks.LIGHT_GRAY_WOOL_WALL);
    public static final RegistryObject<Item> CYAN_WOOL_SLAB = registerBlockItem("cyan_wool_slab",
            ModBlocks.CYAN_WOOL_SLAB);
    public static final RegistryObject<Item> CYAN_WOOL_STAIRS = registerBlockItem("cyan_wool_stairs",
            ModBlocks.CYAN_WOOL_STAIRS);
    public static final RegistryObject<Item> CYAN_WOOL_WALL = registerBlockItem("cyan_wool_wall",
            ModBlocks.CYAN_WOOL_WALL);
    public static final RegistryObject<Item> PURPLE_WOOL_SLAB = registerBlockItem("purple_wool_slab",
            ModBlocks.PURPLE_WOOL_SLAB);
    public static final RegistryObject<Item> PURPLE_WOOL_STAIRS = registerBlockItem("purple_wool_stairs",
            ModBlocks.PURPLE_WOOL_STAIRS);
    public static final RegistryObject<Item> PURPLE_WOOL_WALL = registerBlockItem("purple_wool_wall",
            ModBlocks.PURPLE_WOOL_WALL);
    public static final RegistryObject<Item> BLUE_WOOL_SLAB = registerBlockItem("blue_wool_slab",
            ModBlocks.BLUE_WOOL_SLAB);
    public static final RegistryObject<Item> BLUE_WOOL_STAIRS = registerBlockItem("blue_wool_stairs",
            ModBlocks.BLUE_WOOL_STAIRS);
    public static final RegistryObject<Item> BLUE_WOOL_WALL = registerBlockItem("blue_wool_wall",
            ModBlocks.BLUE_WOOL_WALL);
    public static final RegistryObject<Item> BROWN_WOOL_SLAB = registerBlockItem("brown_wool_slab",
            ModBlocks.BROWN_WOOL_SLAB);
    public static final RegistryObject<Item> BROWN_WOOL_STAIRS = registerBlockItem("brown_wool_stairs",
            ModBlocks.BROWN_WOOL_STAIRS);
    public static final RegistryObject<Item> BROWN_WOOL_WALL = registerBlockItem("brown_wool_wall",
            ModBlocks.BROWN_WOOL_WALL);
    public static final RegistryObject<Item> GREEN_WOOL_SLAB = registerBlockItem("green_wool_slab",
            ModBlocks.GREEN_WOOL_SLAB);
    public static final RegistryObject<Item> GREEN_WOOL_STAIRS = registerBlockItem("green_wool_stairs",
            ModBlocks.GREEN_WOOL_STAIRS);
    public static final RegistryObject<Item> GREEN_WOOL_WALL = registerBlockItem("green_wool_wall",
            ModBlocks.GREEN_WOOL_WALL);
    public static final RegistryObject<Item> RED_WOOL_SLAB = registerBlockItem("red_wool_slab",
            ModBlocks.RED_WOOL_SLAB);
    public static final RegistryObject<Item> RED_WOOL_STAIRS = registerBlockItem("red_wool_stairs",
            ModBlocks.RED_WOOL_STAIRS);
    public static final RegistryObject<Item> RED_WOOL_WALL = registerBlockItem("red_wool_wall",
            ModBlocks.RED_WOOL_WALL);
    public static final RegistryObject<Item> BLACK_WOOL_SLAB = registerBlockItem("black_wool_slab",
            ModBlocks.BLACK_WOOL_SLAB);
    public static final RegistryObject<Item> BLACK_WOOL_STAIRS = registerBlockItem("black_wool_stairs",
            ModBlocks.BLACK_WOOL_STAIRS);
    public static final RegistryObject<Item> BLACK_WOOL_WALL = registerBlockItem("black_wool_wall",
            ModBlocks.BLACK_WOOL_WALL);
    public static final RegistryObject<Item> POPLAR_LOG = registerBlockItem("poplar_log", ModBlocks.POPLAR_LOG);
    public static final RegistryObject<Item> POPLAR_PLANKS = registerBlockItem("poplar_planks",
            ModBlocks.POPLAR_PLANKS);
    public static final RegistryObject<Item> POPLAR_SAPLING = registerBlockItem("poplar_sapling",
            ModBlocks.POPLAR_SAPLING);
    public static final RegistryObject<Item> POPLAR_BUTTON = registerBlockItem("poplar_button",
            ModBlocks.POPLAR_BUTTON);
    public static final RegistryObject<Item> POPLAR_DOOR = registerBlockItem("poplar_door", ModBlocks.POPLAR_DOOR);
    public static final RegistryObject<Item> POPLAR_FENCE = registerBlockItem("poplar_fence", ModBlocks.POPLAR_FENCE);
    public static final RegistryObject<Item> POPLAR_FENCE_GATE = registerBlockItem("poplar_fence_gate",
            ModBlocks.POPLAR_FENCE_GATE);
    public static final RegistryObject<Item> POPLAR_LOG_SLAB = registerBlockItem("poplar_log_slab",
            ModBlocks.POPLAR_LOG_SLAB);
    public static final RegistryObject<Item> POPLAR_LOG_STAIRS = registerBlockItem("poplar_log_stairs",
            ModBlocks.POPLAR_LOG_STAIRS);
    public static final RegistryObject<Item> POPLAR_LOG_WALL = registerBlockItem("poplar_log_wall",
            ModBlocks.POPLAR_LOG_WALL);
    public static final RegistryObject<Item> POPLAR_PRESSURE_PLATE = registerBlockItem("poplar_pressure_plate",
            ModBlocks.POPLAR_PRESSURE_PLATE);
    public static final RegistryObject<Item> POPLAR_SLAB = registerBlockItem("poplar_slab", ModBlocks.POPLAR_SLAB);
    public static final RegistryObject<Item> POPLAR_STAIRS = registerBlockItem("poplar_stairs",
            ModBlocks.POPLAR_STAIRS);
    public static final RegistryObject<Item> POPLAR_TRAPDOOR = registerBlockItem("poplar_trapdoor",
            ModBlocks.POPLAR_TRAPDOOR);
    public static final RegistryObject<Item> POPLAR_WOOD = registerBlockItem("poplar_wood", ModBlocks.POPLAR_WOOD);
    public static final RegistryObject<Item> POPLAR_WOOD_SLAB = registerBlockItem("poplar_wood_slab",
            ModBlocks.POPLAR_WOOD_SLAB);
    public static final RegistryObject<Item> POPLAR_WOOD_STAIRS = registerBlockItem("poplar_wood_stairs",
            ModBlocks.POPLAR_WOOD_STAIRS);
    public static final RegistryObject<Item> STRIPPED_POPLAR_LOG = registerBlockItem("stripped_poplar_log",
            ModBlocks.STRIPPED_POPLAR_LOG);
    public static final RegistryObject<Item> STRIPPED_POPLAR_LOG_SLAB = registerBlockItem("stripped_poplar_log_slab",
            ModBlocks.STRIPPED_POPLAR_LOG_SLAB);
    public static final RegistryObject<Item> STRIPPED_POPLAR_LOG_STAIRS = registerBlockItem(
            "stripped_poplar_log_stairs", ModBlocks.STRIPPED_POPLAR_LOG_STAIRS);
    public static final RegistryObject<Item> STRIPPED_POPLAR_LOG_WALL = registerBlockItem("stripped_poplar_log_wall",
            ModBlocks.STRIPPED_POPLAR_LOG_WALL);
    public static final RegistryObject<Item> STRIPPED_POPLAR_WOOD = registerBlockItem("stripped_poplar_wood",
            ModBlocks.STRIPPED_POPLAR_WOOD);
    public static final RegistryObject<Item> STRIPPED_POPLAR_WOOD_SLAB = registerBlockItem("stripped_poplar_wood_slab",
            ModBlocks.STRIPPED_POPLAR_WOOD_SLAB);
    public static final RegistryObject<Item> STRIPPED_POPLAR_WOOD_STAIRS = registerBlockItem(
            "stripped_poplar_wood_stairs", ModBlocks.STRIPPED_POPLAR_WOOD_STAIRS);
    public static final RegistryObject<Item> POPLAR_BOAT = ITEMS.register("poplar_boat",
            () -> new PoplarBoatItem(new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(1)));
    public static final RegistryObject<Item> ORANGE_POPLAR_LEAVES = registerBlockItem("orange_poplar_leaves",
            ModBlocks.ORANGE_POPLAR_LEAVES);
    public static final RegistryObject<Item> RED_POPLAR_LEAVES = registerBlockItem("red_poplar_leaves",
            ModBlocks.RED_POPLAR_LEAVES);
    public static final RegistryObject<Item> YELLOW_POPLAR_LEAVES = registerBlockItem("yellow_poplar_leaves",
            ModBlocks.YELLOW_POPLAR_LEAVES);
    public static final RegistryObject<Item> CINNABAR = registerBlockItem("cinnabar", ModBlocks.CINNABAR);
    public static final RegistryObject<Item> POLISHED_CINNABAR = registerBlockItem("polished_cinnabar",
            ModBlocks.POLISHED_CINNABAR);
    public static final RegistryObject<Item> CINNABAR_SLAB = registerBlockItem("cinnabar_slab",
            ModBlocks.CINNABAR_SLAB);
    public static final RegistryObject<Item> CINNABAR_STAIRS = registerBlockItem("cinnabar_stairs",
            ModBlocks.CINNABAR_STAIRS);
    public static final RegistryObject<Item> CINNABAR_WALL = registerBlockItem("cinnabar_wall",
            ModBlocks.CINNABAR_WALL);
    public static final RegistryObject<Item> POLISHED_CINNABAR_SLAB = registerBlockItem("polished_cinnabar_slab",
            ModBlocks.POLISHED_CINNABAR_SLAB);
    public static final RegistryObject<Item> POLISHED_CINNABAR_STAIRS = registerBlockItem("polished_cinnabar_stairs",
            ModBlocks.POLISHED_CINNABAR_STAIRS);
    public static final RegistryObject<Item> POLISHED_CINNABAR_WALL = registerBlockItem("polished_cinnabar_wall",
            ModBlocks.POLISHED_CINNABAR_WALL);
    public static final RegistryObject<Item> CINNABAR_BRICKS = registerBlockItem("cinnabar_bricks",
            ModBlocks.CINNABAR_BRICKS);
    public static final RegistryObject<Item> CINNABAR_BRICKS_SLAB = registerBlockItem("cinnabar_bricks_slab",
            ModBlocks.CINNABAR_BRICKS_SLAB);
    public static final RegistryObject<Item> CINNABAR_BRICKS_STAIRS = registerBlockItem("cinnabar_bricks_stairs",
            ModBlocks.CINNABAR_BRICKS_STAIRS);
    public static final RegistryObject<Item> CINNABAR_BRICKS_WALL = registerBlockItem("cinnabar_bricks_wall",
            ModBlocks.CINNABAR_BRICKS_WALL);
    public static final RegistryObject<Item> CHISELED_CINNABAR = registerBlockItem("chiseled_cinnabar",
            ModBlocks.CHISELED_CINNABAR);
    public static final RegistryObject<Item> SULFUR = registerBlockItem("sulfur", ModBlocks.SULFUR);
    public static final RegistryObject<Item> POLISHED_SULFUR = registerBlockItem("polished_sulfur",
            ModBlocks.POLISHED_SULFUR);
    public static final RegistryObject<Item> SULFUR_SLAB = registerBlockItem("sulfur_slab", ModBlocks.SULFUR_SLAB);
    public static final RegistryObject<Item> SULFUR_STAIRS = registerBlockItem("sulfur_stairs",
            ModBlocks.SULFUR_STAIRS);
    public static final RegistryObject<Item> SULFUR_WALL = registerBlockItem("sulfur_wall", ModBlocks.SULFUR_WALL);
    public static final RegistryObject<Item> POLISHED_SULFUR_SLAB = registerBlockItem("polished_sulfur_slab",
            ModBlocks.POLISHED_SULFUR_SLAB);
    public static final RegistryObject<Item> POLISHED_SULFUR_STAIRS = registerBlockItem("polished_sulfur_stairs",
            ModBlocks.POLISHED_SULFUR_STAIRS);
    public static final RegistryObject<Item> POLISHED_SULFUR_WALL = registerBlockItem("polished_sulfur_wall",
            ModBlocks.POLISHED_SULFUR_WALL);
    public static final RegistryObject<Item> SULFUR_BRICKS = registerBlockItem("sulfur_bricks",
            ModBlocks.SULFUR_BRICKS);
    public static final RegistryObject<Item> SULFUR_BRICKS_SLAB = registerBlockItem("sulfur_bricks_slab",
            ModBlocks.SULFUR_BRICKS_SLAB);
    public static final RegistryObject<Item> SULFUR_BRICKS_STAIRS = registerBlockItem("sulfur_bricks_stairs",
            ModBlocks.SULFUR_BRICKS_STAIRS);
    public static final RegistryObject<Item> SULFUR_BRICKS_WALL = registerBlockItem("sulfur_bricks_wall",
            ModBlocks.SULFUR_BRICKS_WALL);
    public static final RegistryObject<Item> CHISELED_SULFUR = registerBlockItem("chiseled_sulfur",
            ModBlocks.CHISELED_SULFUR);
    public static final RegistryObject<Item> POTENT_SULFUR = registerBlockItem("potent_sulfur",
            ModBlocks.POTENT_SULFUR);
    public static final RegistryObject<Item> SULFUR_SPIKE = registerBlockItem("sulfur_spike", ModBlocks.SULFUR_SPIKE);
    public static final RegistryObject<Item> WHITE_WOOL_VERTICAL_SLAB = registerBlockItem("white_wool_vertical_slab",
            ModBlocks.WHITE_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> ORANGE_WOOL_VERTICAL_SLAB = registerBlockItem("orange_wool_vertical_slab",
            ModBlocks.ORANGE_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> MAGENTA_WOOL_VERTICAL_SLAB = registerBlockItem(
            "magenta_wool_vertical_slab", ModBlocks.MAGENTA_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_WOOL_VERTICAL_SLAB = registerBlockItem(
            "light_blue_wool_vertical_slab", ModBlocks.LIGHT_BLUE_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> YELLOW_WOOL_VERTICAL_SLAB = registerBlockItem("yellow_wool_vertical_slab",
            ModBlocks.YELLOW_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIME_WOOL_VERTICAL_SLAB = registerBlockItem("lime_wool_vertical_slab",
            ModBlocks.LIME_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> PINK_WOOL_VERTICAL_SLAB = registerBlockItem("pink_wool_vertical_slab",
            ModBlocks.PINK_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> GRAY_WOOL_VERTICAL_SLAB = registerBlockItem("gray_wool_vertical_slab",
            ModBlocks.GRAY_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_WOOL_VERTICAL_SLAB = registerBlockItem(
            "light_gray_wool_vertical_slab", ModBlocks.LIGHT_GRAY_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> CYAN_WOOL_VERTICAL_SLAB = registerBlockItem("cyan_wool_vertical_slab",
            ModBlocks.CYAN_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> PURPLE_WOOL_VERTICAL_SLAB = registerBlockItem("purple_wool_vertical_slab",
            ModBlocks.PURPLE_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLUE_WOOL_VERTICAL_SLAB = registerBlockItem("blue_wool_vertical_slab",
            ModBlocks.BLUE_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BROWN_WOOL_VERTICAL_SLAB = registerBlockItem("brown_wool_vertical_slab",
            ModBlocks.BROWN_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> GREEN_WOOL_VERTICAL_SLAB = registerBlockItem("green_wool_vertical_slab",
            ModBlocks.GREEN_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_WOOL_VERTICAL_SLAB = registerBlockItem("red_wool_vertical_slab",
            ModBlocks.RED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLACK_WOOL_VERTICAL_SLAB = registerBlockItem("black_wool_vertical_slab",
            ModBlocks.BLACK_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_MOSS_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "red_moss_block_vertical_slab", ModBlocks.RED_MOSS_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> ORANGE_MOSS_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "orange_moss_block_vertical_slab", ModBlocks.ORANGE_MOSS_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> YELLOW_MOSS_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "yellow_moss_block_vertical_slab", ModBlocks.YELLOW_MOSS_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> TUFF_VERTICAL_SLAB = registerBlockItem("tuff_vertical_slab",
            ModBlocks.TUFF_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_TUFF_VERTICAL_SLAB = registerBlockItem(
            "polished_tuff_vertical_slab", ModBlocks.POLISHED_TUFF_VERTICAL_SLAB);
    public static final RegistryObject<Item> TUFF_BRICK_VERTICAL_SLAB = registerBlockItem("tuff_brick_vertical_slab",
            ModBlocks.TUFF_BRICK_VERTICAL_SLAB);
    public static final RegistryObject<Item> SULFUR_VERTICAL_SLAB = registerBlockItem("sulfur_vertical_slab",
            ModBlocks.SULFUR_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_SULFUR_VERTICAL_SLAB = registerBlockItem(
            "polished_sulfur_vertical_slab", ModBlocks.POLISHED_SULFUR_VERTICAL_SLAB);
    public static final RegistryObject<Item> SULFUR_BRICKS_VERTICAL_SLAB = registerBlockItem(
            "sulfur_bricks_vertical_slab", ModBlocks.SULFUR_BRICKS_VERTICAL_SLAB);
    public static final RegistryObject<Item> CINNABAR_VERTICAL_SLAB = registerBlockItem("cinnabar_vertical_slab",
            ModBlocks.CINNABAR_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_CINNABAR_VERTICAL_SLAB = registerBlockItem(
            "polished_cinnabar_vertical_slab", ModBlocks.POLISHED_CINNABAR_VERTICAL_SLAB);
    public static final RegistryObject<Item> CINNABAR_BRICKS_VERTICAL_SLAB = registerBlockItem(
            "cinnabar_bricks_vertical_slab", ModBlocks.CINNABAR_BRICKS_VERTICAL_SLAB);
    public static final RegistryObject<Item> POPLAR_VERTICAL_SLAB = registerBlockItem("poplar_vertical_slab",
            ModBlocks.POPLAR_VERTICAL_SLAB);
    public static final RegistryObject<Item> POPLAR_LOG_VERTICAL_SLAB = registerBlockItem("poplar_log_vertical_slab",
            ModBlocks.POPLAR_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_POPLAR_LOG_VERTICAL_SLAB = registerBlockItem(
            "stripped_poplar_log_vertical_slab", ModBlocks.STRIPPED_POPLAR_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> POPLAR_WOOD_VERTICAL_SLAB = registerBlockItem("poplar_wood_vertical_slab",
            ModBlocks.POPLAR_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_POPLAR_WOOD_VERTICAL_SLAB = registerBlockItem(
            "stripped_poplar_wood_vertical_slab", ModBlocks.STRIPPED_POPLAR_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> OAK_VERTICAL_SLAB = registerBlockItem("oak_vertical_slab",
            ModBlocks.OAK_VERTICAL_SLAB);
    public static final RegistryObject<Item> SPRUCE_VERTICAL_SLAB = registerBlockItem("spruce_vertical_slab",
            ModBlocks.SPRUCE_VERTICAL_SLAB);
    public static final RegistryObject<Item> BIRCH_VERTICAL_SLAB = registerBlockItem("birch_vertical_slab",
            ModBlocks.BIRCH_VERTICAL_SLAB);
    public static final RegistryObject<Item> JUNGLE_VERTICAL_SLAB = registerBlockItem("jungle_vertical_slab",
            ModBlocks.JUNGLE_VERTICAL_SLAB);
    public static final RegistryObject<Item> ACACIA_VERTICAL_SLAB = registerBlockItem("acacia_vertical_slab",
            ModBlocks.ACACIA_VERTICAL_SLAB);
    public static final RegistryObject<Item> DARK_OAK_VERTICAL_SLAB = registerBlockItem("dark_oak_vertical_slab",
            ModBlocks.DARK_OAK_VERTICAL_SLAB);
    public static final RegistryObject<Item> CRIMSON_VERTICAL_SLAB = registerBlockItem("crimson_vertical_slab",
            ModBlocks.CRIMSON_VERTICAL_SLAB);
    public static final RegistryObject<Item> WARPED_VERTICAL_SLAB = registerBlockItem("warped_vertical_slab",
            ModBlocks.WARPED_VERTICAL_SLAB);
    public static final RegistryObject<Item> STONE_VERTICAL_SLAB = registerBlockItem("stone_vertical_slab",
            ModBlocks.STONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> SMOOTH_STONE_VERTICAL_SLAB = registerBlockItem(
            "smooth_stone_vertical_slab", ModBlocks.SMOOTH_STONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> GRANITE_VERTICAL_SLAB = registerBlockItem("granite_vertical_slab",
            ModBlocks.GRANITE_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_GRANITE_VERTICAL_SLAB = registerBlockItem(
            "polished_granite_vertical_slab", ModBlocks.POLISHED_GRANITE_VERTICAL_SLAB);
    public static final RegistryObject<Item> DIORITE_VERTICAL_SLAB = registerBlockItem("diorite_vertical_slab",
            ModBlocks.DIORITE_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_DIORITE_VERTICAL_SLAB = registerBlockItem(
            "polished_diorite_vertical_slab", ModBlocks.POLISHED_DIORITE_VERTICAL_SLAB);
    public static final RegistryObject<Item> ANDESITE_VERTICAL_SLAB = registerBlockItem("andesite_vertical_slab",
            ModBlocks.ANDESITE_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_ANDESITE_VERTICAL_SLAB = registerBlockItem(
            "polished_andesite_vertical_slab", ModBlocks.POLISHED_ANDESITE_VERTICAL_SLAB);
    public static final RegistryObject<Item> COBBLESTONE_VERTICAL_SLAB = registerBlockItem("cobblestone_vertical_slab",
            ModBlocks.COBBLESTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> MOSSY_COBBLESTONE_VERTICAL_SLAB = registerBlockItem(
            "mossy_cobblestone_vertical_slab", ModBlocks.MOSSY_COBBLESTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> STONE_BRICK_VERTICAL_SLAB = registerBlockItem("stone_brick_vertical_slab",
            ModBlocks.STONE_BRICK_VERTICAL_SLAB);
    public static final RegistryObject<Item> MOSSY_STONE_BRICK_VERTICAL_SLAB = registerBlockItem(
            "mossy_stone_brick_vertical_slab", ModBlocks.MOSSY_STONE_BRICK_VERTICAL_SLAB);
    public static final RegistryObject<Item> NETHER_BRICK_VERTICAL_SLAB = registerBlockItem(
            "nether_brick_vertical_slab", ModBlocks.NETHER_BRICK_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_NETHER_BRICK_VERTICAL_SLAB = registerBlockItem(
            "red_nether_brick_vertical_slab", ModBlocks.RED_NETHER_BRICK_VERTICAL_SLAB);
    public static final RegistryObject<Item> OAK_LOG_STAIRS = registerBlockItem("oak_log_stairs",
            ModBlocks.OAK_LOG_STAIRS);
    public static final RegistryObject<Item> CUT_COPPER_VERTICAL_SLAB = registerBlockItem("cut_copper_vertical_slab",
            ModBlocks.CUT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> EXPOSED_CUT_COPPER_VERTICAL_SLAB = registerBlockItem(
            "exposed_cut_copper_vertical_slab", ModBlocks.EXPOSED_CUT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> WEATHERED_CUT_COPPER_VERTICAL_SLAB = registerBlockItem(
            "weathered_cut_copper_vertical_slab", ModBlocks.WEATHERED_CUT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> OXIDIZED_CUT_COPPER_VERTICAL_SLAB = registerBlockItem(
            "oxidized_cut_copper_vertical_slab", ModBlocks.OXIDIZED_CUT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> WAXED_CUT_COPPER_VERTICAL_SLAB = registerBlockItem(
            "waxed_cut_copper_vertical_slab", ModBlocks.WAXED_CUT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> WAXED_EXPOSED_CUT_COPPER_VERTICAL_SLAB = registerBlockItem(
            "waxed_exposed_cut_copper_vertical_slab", ModBlocks.WAXED_EXPOSED_CUT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> WAXED_WEATHERED_CUT_COPPER_VERTICAL_SLAB = registerBlockItem(
            "waxed_weathered_cut_copper_vertical_slab", ModBlocks.WAXED_WEATHERED_CUT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> WAXED_OXIDIZED_CUT_COPPER_VERTICAL_SLAB = registerBlockItem(
            "waxed_oxidized_cut_copper_vertical_slab", ModBlocks.WAXED_OXIDIZED_CUT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> COBBLED_DEEPSLATE_VERTICAL_SLAB = registerBlockItem(
            "cobbled_deepslate_vertical_slab", ModBlocks.COBBLED_DEEPSLATE_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_DEEPSLATE_VERTICAL_SLAB = registerBlockItem(
            "polished_deepslate_vertical_slab", ModBlocks.POLISHED_DEEPSLATE_VERTICAL_SLAB);
    public static final RegistryObject<Item> DEEPSLATE_BRICK_VERTICAL_SLAB = registerBlockItem(
            "deepslate_brick_vertical_slab", ModBlocks.DEEPSLATE_BRICK_VERTICAL_SLAB);
    public static final RegistryObject<Item> DEEPSLATE_TILE_VERTICAL_SLAB = registerBlockItem(
            "deepslate_tile_vertical_slab", ModBlocks.DEEPSLATE_TILE_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLACKSTONE_VERTICAL_SLAB = registerBlockItem("blackstone_vertical_slab",
            ModBlocks.BLACKSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_BLACKSTONE_VERTICAL_SLAB = registerBlockItem(
            "polished_blackstone_vertical_slab", ModBlocks.POLISHED_BLACKSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> POLISHED_BLACKSTONE_BRICK_VERTICAL_SLAB = registerBlockItem(
            "polished_blackstone_brick_vertical_slab", ModBlocks.POLISHED_BLACKSTONE_BRICK_VERTICAL_SLAB);
    public static final RegistryObject<Item> PRISMARINE_VERTICAL_SLAB = registerBlockItem("prismarine_vertical_slab",
            ModBlocks.PRISMARINE_VERTICAL_SLAB);
    public static final RegistryObject<Item> PRISMARINE_BRICK_VERTICAL_SLAB = registerBlockItem(
            "prismarine_brick_vertical_slab", ModBlocks.PRISMARINE_BRICK_VERTICAL_SLAB);
    public static final RegistryObject<Item> DARK_PRISMARINE_VERTICAL_SLAB = registerBlockItem(
            "dark_prismarine_vertical_slab", ModBlocks.DARK_PRISMARINE_VERTICAL_SLAB);
    public static final RegistryObject<Item> BRICK_VERTICAL_SLAB = registerBlockItem("brick_vertical_slab",
            ModBlocks.BRICK_VERTICAL_SLAB);
    public static final RegistryObject<Item> END_STONE_BRICK_VERTICAL_SLAB = registerBlockItem(
            "end_stone_brick_vertical_slab", ModBlocks.END_STONE_BRICK_VERTICAL_SLAB);
    public static final RegistryObject<Item> PURPUR_VERTICAL_SLAB = registerBlockItem("purpur_vertical_slab",
            ModBlocks.PURPUR_VERTICAL_SLAB);

    public static final RegistryObject<Item> QUARTZ_VERTICAL_SLAB = registerBlockItem("quartz_vertical_slab",
            ModBlocks.QUARTZ_VERTICAL_SLAB);
    public static final RegistryObject<Item> SMOOTH_QUARTZ_VERTICAL_SLAB = registerBlockItem(
            "smooth_quartz_vertical_slab", ModBlocks.SMOOTH_QUARTZ_VERTICAL_SLAB);
    public static final RegistryObject<Item> TERRACOTTA_SLAB = registerBlockItem("terracotta_slab",
            ModBlocks.TERRACOTTA_SLAB);
    public static final RegistryObject<Item> TERRACOTTA_STAIRS = registerBlockItem("terracotta_stairs",
            ModBlocks.TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> TERRACOTTA_VERTICAL_SLAB = registerBlockItem("terracotta_vertical_slab",
            ModBlocks.TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> WHITE_TERRACOTTA_SLAB = registerBlockItem("white_terracotta_slab",
            ModBlocks.WHITE_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> WHITE_TERRACOTTA_STAIRS = registerBlockItem("white_terracotta_stairs",
            ModBlocks.WHITE_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> WHITE_TERRACOTTA_VERTICAL_SLAB = registerBlockItem(
            "white_terracotta_vertical_slab", ModBlocks.WHITE_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> ORANGE_TERRACOTTA_SLAB = registerBlockItem("orange_terracotta_slab",
            ModBlocks.ORANGE_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> ORANGE_TERRACOTTA_STAIRS = registerBlockItem("orange_terracotta_stairs",
            ModBlocks.ORANGE_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> ORANGE_TERRACOTTA_VERTICAL_SLAB = registerBlockItem(
            "orange_terracotta_vertical_slab", ModBlocks.ORANGE_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> MAGENTA_TERRACOTTA_SLAB = registerBlockItem("magenta_terracotta_slab",
            ModBlocks.MAGENTA_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> MAGENTA_TERRACOTTA_STAIRS = registerBlockItem("magenta_terracotta_stairs",
            ModBlocks.MAGENTA_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> MAGENTA_TERRACOTTA_VERTICAL_SLAB = registerBlockItem(
            "magenta_terracotta_vertical_slab", ModBlocks.MAGENTA_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_TERRACOTTA_SLAB = registerBlockItem(
            "light_blue_terracotta_slab", ModBlocks.LIGHT_BLUE_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_TERRACOTTA_STAIRS = registerBlockItem(
            "light_blue_terracotta_stairs", ModBlocks.LIGHT_BLUE_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> LIGHT_BLUE_TERRACOTTA_VERTICAL_SLAB = registerBlockItem(
            "light_blue_terracotta_vertical_slab", ModBlocks.LIGHT_BLUE_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> YELLOW_TERRACOTTA_SLAB = registerBlockItem("yellow_terracotta_slab",
            ModBlocks.YELLOW_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> YELLOW_TERRACOTTA_STAIRS = registerBlockItem("yellow_terracotta_stairs",
            ModBlocks.YELLOW_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> YELLOW_TERRACOTTA_VERTICAL_SLAB = registerBlockItem(
            "yellow_terracotta_vertical_slab", ModBlocks.YELLOW_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIME_TERRACOTTA_SLAB = registerBlockItem("lime_terracotta_slab",
            ModBlocks.LIME_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> LIME_TERRACOTTA_STAIRS = registerBlockItem("lime_terracotta_stairs",
            ModBlocks.LIME_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> LIME_TERRACOTTA_VERTICAL_SLAB = registerBlockItem(
            "lime_terracotta_vertical_slab", ModBlocks.LIME_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> PINK_TERRACOTTA_SLAB = registerBlockItem("pink_terracotta_slab",
            ModBlocks.PINK_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> PINK_TERRACOTTA_STAIRS = registerBlockItem("pink_terracotta_stairs",
            ModBlocks.PINK_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> PINK_TERRACOTTA_VERTICAL_SLAB = registerBlockItem(
            "pink_terracotta_vertical_slab", ModBlocks.PINK_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> GRAY_TERRACOTTA_SLAB = registerBlockItem("gray_terracotta_slab",
            ModBlocks.GRAY_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> GRAY_TERRACOTTA_STAIRS = registerBlockItem("gray_terracotta_stairs",
            ModBlocks.GRAY_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> GRAY_TERRACOTTA_VERTICAL_SLAB = registerBlockItem(
            "gray_terracotta_vertical_slab", ModBlocks.GRAY_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_TERRACOTTA_SLAB = registerBlockItem(
            "light_gray_terracotta_slab", ModBlocks.LIGHT_GRAY_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_TERRACOTTA_STAIRS = registerBlockItem(
            "light_gray_terracotta_stairs", ModBlocks.LIGHT_GRAY_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> LIGHT_GRAY_TERRACOTTA_VERTICAL_SLAB = registerBlockItem(
            "light_gray_terracotta_vertical_slab", ModBlocks.LIGHT_GRAY_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> CYAN_TERRACOTTA_SLAB = registerBlockItem("cyan_terracotta_slab",
            ModBlocks.CYAN_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> CYAN_TERRACOTTA_STAIRS = registerBlockItem("cyan_terracotta_stairs",
            ModBlocks.CYAN_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> CYAN_TERRACOTTA_VERTICAL_SLAB = registerBlockItem(
            "cyan_terracotta_vertical_slab", ModBlocks.CYAN_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> PURPLE_TERRACOTTA_SLAB = registerBlockItem("purple_terracotta_slab",
            ModBlocks.PURPLE_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> PURPLE_TERRACOTTA_STAIRS = registerBlockItem("purple_terracotta_stairs",
            ModBlocks.PURPLE_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> PURPLE_TERRACOTTA_VERTICAL_SLAB = registerBlockItem(
            "purple_terracotta_vertical_slab", ModBlocks.PURPLE_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLUE_TERRACOTTA_SLAB = registerBlockItem("blue_terracotta_slab",
            ModBlocks.BLUE_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> BLUE_TERRACOTTA_STAIRS = registerBlockItem("blue_terracotta_stairs",
            ModBlocks.BLUE_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> BLUE_TERRACOTTA_VERTICAL_SLAB = registerBlockItem(
            "blue_terracotta_vertical_slab", ModBlocks.BLUE_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> BROWN_TERRACOTTA_SLAB = registerBlockItem("brown_terracotta_slab",
            ModBlocks.BROWN_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> BROWN_TERRACOTTA_STAIRS = registerBlockItem("brown_terracotta_stairs",
            ModBlocks.BROWN_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> BROWN_TERRACOTTA_VERTICAL_SLAB = registerBlockItem(
            "brown_terracotta_vertical_slab", ModBlocks.BROWN_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> GREEN_TERRACOTTA_SLAB = registerBlockItem("green_terracotta_slab",
            ModBlocks.GREEN_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> GREEN_TERRACOTTA_STAIRS = registerBlockItem("green_terracotta_stairs",
            ModBlocks.GREEN_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> GREEN_TERRACOTTA_VERTICAL_SLAB = registerBlockItem(
            "green_terracotta_vertical_slab", ModBlocks.GREEN_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_TERRACOTTA_SLAB = registerBlockItem("red_terracotta_slab",
            ModBlocks.RED_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> RED_TERRACOTTA_STAIRS = registerBlockItem("red_terracotta_stairs",
            ModBlocks.RED_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> RED_TERRACOTTA_VERTICAL_SLAB = registerBlockItem(
            "red_terracotta_vertical_slab", ModBlocks.RED_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLACK_TERRACOTTA_SLAB = registerBlockItem("black_terracotta_slab",
            ModBlocks.BLACK_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> BLACK_TERRACOTTA_STAIRS = registerBlockItem("black_terracotta_stairs",
            ModBlocks.BLACK_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> BLACK_TERRACOTTA_VERTICAL_SLAB = registerBlockItem(
            "black_terracotta_vertical_slab", ModBlocks.BLACK_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_OAK_LOG_STAIRS = registerBlockItem("stripped_oak_log_stairs",
            ModBlocks.STRIPPED_OAK_LOG_STAIRS);
    public static final RegistryObject<Item> SPRUCE_LOG_STAIRS = registerBlockItem("spruce_log_stairs",
            ModBlocks.SPRUCE_LOG_STAIRS);
    public static final RegistryObject<Item> STRIPPED_SPRUCE_LOG_STAIRS = registerBlockItem(
            "stripped_spruce_log_stairs", ModBlocks.STRIPPED_SPRUCE_LOG_STAIRS);
    public static final RegistryObject<Item> BIRCH_LOG_STAIRS = registerBlockItem("birch_log_stairs",
            ModBlocks.BIRCH_LOG_STAIRS);
    public static final RegistryObject<Item> STRIPPED_BIRCH_LOG_STAIRS = registerBlockItem("stripped_birch_log_stairs",
            ModBlocks.STRIPPED_BIRCH_LOG_STAIRS);
    public static final RegistryObject<Item> JUNGLE_LOG_STAIRS = registerBlockItem("jungle_log_stairs",
            ModBlocks.JUNGLE_LOG_STAIRS);
    public static final RegistryObject<Item> STRIPPED_JUNGLE_LOG_STAIRS = registerBlockItem(
            "stripped_jungle_log_stairs", ModBlocks.STRIPPED_JUNGLE_LOG_STAIRS);
    public static final RegistryObject<Item> ACACIA_LOG_STAIRS = registerBlockItem("acacia_log_stairs",
            ModBlocks.ACACIA_LOG_STAIRS);
    public static final RegistryObject<Item> STRIPPED_ACACIA_LOG_STAIRS = registerBlockItem(
            "stripped_acacia_log_stairs", ModBlocks.STRIPPED_ACACIA_LOG_STAIRS);
    public static final RegistryObject<Item> DARK_OAK_LOG_STAIRS = registerBlockItem("dark_oak_log_stairs",
            ModBlocks.DARK_OAK_LOG_STAIRS);
    public static final RegistryObject<Item> STRIPPED_DARK_OAK_LOG_STAIRS = registerBlockItem(
            "stripped_dark_oak_log_stairs", ModBlocks.STRIPPED_DARK_OAK_LOG_STAIRS);
    public static final RegistryObject<Item> CRIMSON_STEM_STAIRS = registerBlockItem("crimson_stem_stairs",
            ModBlocks.CRIMSON_STEM_STAIRS);
    public static final RegistryObject<Item> STRIPPED_CRIMSON_STEM_STAIRS = registerBlockItem(
            "stripped_crimson_stem_stairs", ModBlocks.STRIPPED_CRIMSON_STEM_STAIRS);
    public static final RegistryObject<Item> WARPED_STEM_STAIRS = registerBlockItem("warped_stem_stairs",
            ModBlocks.WARPED_STEM_STAIRS);
    public static final RegistryObject<Item> STRIPPED_WARPED_STEM_STAIRS = registerBlockItem(
            "stripped_warped_stem_stairs", ModBlocks.STRIPPED_WARPED_STEM_STAIRS);
    public static final RegistryObject<Item> ACACIA_WOOD_SLAB = registerBlockItem("acacia_wood_slab",
            ModBlocks.ACACIA_WOOD_SLAB);
    public static final RegistryObject<Item> ACACIA_WOOD_STAIRS = registerBlockItem("acacia_wood_stairs",
            ModBlocks.ACACIA_WOOD_STAIRS);
    public static final RegistryObject<Item> ACACIA_WOOD_VERTICAL_SLAB = registerBlockItem("acacia_wood_vertical_slab",
            ModBlocks.ACACIA_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> AMETHYST_BLOCK_STAIRS = registerBlockItem("amethyst_block_stairs",
            ModBlocks.AMETHYST_BLOCK_STAIRS);
    public static final RegistryObject<Item> BIRCH_WOOD_SLAB = registerBlockItem("birch_wood_slab",
            ModBlocks.BIRCH_WOOD_SLAB);
    public static final RegistryObject<Item> BIRCH_WOOD_STAIRS = registerBlockItem("birch_wood_stairs",
            ModBlocks.BIRCH_WOOD_STAIRS);
    public static final RegistryObject<Item> BIRCH_WOOD_VERTICAL_SLAB = registerBlockItem("birch_wood_vertical_slab",
            ModBlocks.BIRCH_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> CRIMSON_HYPHAE_SLAB = registerBlockItem("crimson_hyphae_slab",
            ModBlocks.CRIMSON_HYPHAE_SLAB);
    public static final RegistryObject<Item> CRIMSON_HYPHAE_STAIRS = registerBlockItem("crimson_hyphae_stairs",
            ModBlocks.CRIMSON_HYPHAE_STAIRS);
    public static final RegistryObject<Item> CRIMSON_HYPHAE_VERTICAL_SLAB = registerBlockItem(
            "crimson_hyphae_vertical_slab", ModBlocks.CRIMSON_HYPHAE_VERTICAL_SLAB);
    public static final RegistryObject<Item> CRYING_OBSIDIAN_SLAB = registerBlockItem("crying_obsidian_slab",
            ModBlocks.CRYING_OBSIDIAN_SLAB);
    public static final RegistryObject<Item> CRYING_OBSIDIAN_STAIRS = registerBlockItem("crying_obsidian_stairs",
            ModBlocks.CRYING_OBSIDIAN_STAIRS);
    public static final RegistryObject<Item> CRYING_OBSIDIAN_VERTICAL_SLAB = registerBlockItem(
            "crying_obsidian_vertical_slab", ModBlocks.CRYING_OBSIDIAN_VERTICAL_SLAB);
    public static final RegistryObject<Item> CUT_RED_SANDSTONE_STAIRS = registerBlockItem("cut_red_sandstone_stairs",
            ModBlocks.CUT_RED_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> CUT_RED_SANDSTONE_VERTICAL_SLAB = registerBlockItem(
            "cut_red_sandstone_vertical_slab", ModBlocks.CUT_RED_SANDSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> CUT_SANDSTONE_STAIRS = registerBlockItem("cut_sandstone_stairs",
            ModBlocks.CUT_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> CUT_SANDSTONE_VERTICAL_SLAB = registerBlockItem(
            "cut_sandstone_vertical_slab", ModBlocks.CUT_SANDSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> DARK_OAK_WOOD_SLAB = registerBlockItem("dark_oak_wood_slab",
            ModBlocks.DARK_OAK_WOOD_SLAB);
    public static final RegistryObject<Item> DARK_OAK_WOOD_STAIRS = registerBlockItem("dark_oak_wood_stairs",
            ModBlocks.DARK_OAK_WOOD_STAIRS);
    public static final RegistryObject<Item> DARK_OAK_WOOD_VERTICAL_SLAB = registerBlockItem(
            "dark_oak_wood_vertical_slab", ModBlocks.DARK_OAK_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> DIRT_STAIRS = registerBlockItem("dirt_stairs", ModBlocks.DIRT_STAIRS);
    public static final RegistryObject<Item> JUNGLE_WOOD_SLAB = registerBlockItem("jungle_wood_slab",
            ModBlocks.JUNGLE_WOOD_SLAB);
    public static final RegistryObject<Item> JUNGLE_WOOD_STAIRS = registerBlockItem("jungle_wood_stairs",
            ModBlocks.JUNGLE_WOOD_STAIRS);
    public static final RegistryObject<Item> JUNGLE_WOOD_VERTICAL_SLAB = registerBlockItem("jungle_wood_vertical_slab",
            ModBlocks.JUNGLE_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> MOSS_BLOCK_STAIRS = registerBlockItem("moss_block_stairs",
            ModBlocks.MOSS_BLOCK_STAIRS);
    public static final RegistryObject<Item> MYCELIUM_STAIRS = registerBlockItem("mycelium_stairs",
            ModBlocks.MYCELIUM_STAIRS);
    public static final RegistryObject<Item> OAK_WOOD_SLAB = registerBlockItem("oak_wood_slab",
            ModBlocks.OAK_WOOD_SLAB);
    public static final RegistryObject<Item> OAK_WOOD_STAIRS = registerBlockItem("oak_wood_stairs",
            ModBlocks.OAK_WOOD_STAIRS);
    public static final RegistryObject<Item> OAK_WOOD_VERTICAL_SLAB = registerBlockItem("oak_wood_vertical_slab",
            ModBlocks.OAK_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> PODZOL_STAIRS = registerBlockItem("podzol_stairs",
            ModBlocks.PODZOL_STAIRS);
    public static final RegistryObject<Item> SANDSTONE_VERTICAL_SLAB = registerBlockItem("sandstone_vertical_slab",
            ModBlocks.SANDSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> SMOOTH_RED_SANDSTONE_VERTICAL_SLAB = registerBlockItem(
            "smooth_red_sandstone_vertical_slab", ModBlocks.SMOOTH_RED_SANDSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> SMOOTH_SANDSTONE_VERTICAL_SLAB = registerBlockItem(
            "smooth_sandstone_vertical_slab", ModBlocks.SMOOTH_SANDSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> SPRUCE_WOOD_SLAB = registerBlockItem("spruce_wood_slab",
            ModBlocks.SPRUCE_WOOD_SLAB);
    public static final RegistryObject<Item> SPRUCE_WOOD_STAIRS = registerBlockItem("spruce_wood_stairs",
            ModBlocks.SPRUCE_WOOD_STAIRS);
    public static final RegistryObject<Item> SPRUCE_WOOD_VERTICAL_SLAB = registerBlockItem("spruce_wood_vertical_slab",
            ModBlocks.SPRUCE_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_ACACIA_WOOD_SLAB = registerBlockItem("stripped_acacia_wood_slab",
            ModBlocks.STRIPPED_ACACIA_WOOD_SLAB);
    public static final RegistryObject<Item> STRIPPED_ACACIA_WOOD_STAIRS = registerBlockItem(
            "stripped_acacia_wood_stairs", ModBlocks.STRIPPED_ACACIA_WOOD_STAIRS);
    public static final RegistryObject<Item> STRIPPED_ACACIA_WOOD_VERTICAL_SLAB = registerBlockItem(
            "stripped_acacia_wood_vertical_slab", ModBlocks.STRIPPED_ACACIA_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_BIRCH_WOOD_SLAB = registerBlockItem("stripped_birch_wood_slab",
            ModBlocks.STRIPPED_BIRCH_WOOD_SLAB);
    public static final RegistryObject<Item> STRIPPED_BIRCH_WOOD_STAIRS = registerBlockItem(
            "stripped_birch_wood_stairs", ModBlocks.STRIPPED_BIRCH_WOOD_STAIRS);
    public static final RegistryObject<Item> STRIPPED_BIRCH_WOOD_VERTICAL_SLAB = registerBlockItem(
            "stripped_birch_wood_vertical_slab", ModBlocks.STRIPPED_BIRCH_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_CRIMSON_HYPHAE_SLAB = registerBlockItem(
            "stripped_crimson_hyphae_slab", ModBlocks.STRIPPED_CRIMSON_HYPHAE_SLAB);
    public static final RegistryObject<Item> STRIPPED_CRIMSON_HYPHAE_STAIRS = registerBlockItem(
            "stripped_crimson_hyphae_stairs", ModBlocks.STRIPPED_CRIMSON_HYPHAE_STAIRS);
    public static final RegistryObject<Item> STRIPPED_CRIMSON_HYPHAE_VERTICAL_SLAB = registerBlockItem(
            "stripped_crimson_hyphae_vertical_slab", ModBlocks.STRIPPED_CRIMSON_HYPHAE_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_DARK_OAK_WOOD_SLAB = registerBlockItem(
            "stripped_dark_oak_wood_slab", ModBlocks.STRIPPED_DARK_OAK_WOOD_SLAB);
    public static final RegistryObject<Item> STRIPPED_DARK_OAK_WOOD_STAIRS = registerBlockItem(
            "stripped_dark_oak_wood_stairs", ModBlocks.STRIPPED_DARK_OAK_WOOD_STAIRS);
    public static final RegistryObject<Item> STRIPPED_DARK_OAK_WOOD_VERTICAL_SLAB = registerBlockItem(
            "stripped_dark_oak_wood_vertical_slab", ModBlocks.STRIPPED_DARK_OAK_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_JUNGLE_WOOD_SLAB = registerBlockItem("stripped_jungle_wood_slab",
            ModBlocks.STRIPPED_JUNGLE_WOOD_SLAB);
    public static final RegistryObject<Item> STRIPPED_JUNGLE_WOOD_STAIRS = registerBlockItem(
            "stripped_jungle_wood_stairs", ModBlocks.STRIPPED_JUNGLE_WOOD_STAIRS);
    public static final RegistryObject<Item> STRIPPED_JUNGLE_WOOD_VERTICAL_SLAB = registerBlockItem(
            "stripped_jungle_wood_vertical_slab", ModBlocks.STRIPPED_JUNGLE_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_OAK_WOOD_SLAB = registerBlockItem("stripped_oak_wood_slab",
            ModBlocks.STRIPPED_OAK_WOOD_SLAB);
    public static final RegistryObject<Item> STRIPPED_OAK_WOOD_STAIRS = registerBlockItem("stripped_oak_wood_stairs",
            ModBlocks.STRIPPED_OAK_WOOD_STAIRS);
    public static final RegistryObject<Item> STRIPPED_OAK_WOOD_VERTICAL_SLAB = registerBlockItem(
            "stripped_oak_wood_vertical_slab", ModBlocks.STRIPPED_OAK_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_SPRUCE_WOOD_SLAB = registerBlockItem("stripped_spruce_wood_slab",
            ModBlocks.STRIPPED_SPRUCE_WOOD_SLAB);
    public static final RegistryObject<Item> STRIPPED_SPRUCE_WOOD_STAIRS = registerBlockItem(
            "stripped_spruce_wood_stairs", ModBlocks.STRIPPED_SPRUCE_WOOD_STAIRS);
    public static final RegistryObject<Item> STRIPPED_SPRUCE_WOOD_VERTICAL_SLAB = registerBlockItem(
            "stripped_spruce_wood_vertical_slab", ModBlocks.STRIPPED_SPRUCE_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_WARPED_HYPHAE_SLAB = registerBlockItem(
            "stripped_warped_hyphae_slab", ModBlocks.STRIPPED_WARPED_HYPHAE_SLAB);
    public static final RegistryObject<Item> STRIPPED_WARPED_HYPHAE_STAIRS = registerBlockItem(
            "stripped_warped_hyphae_stairs", ModBlocks.STRIPPED_WARPED_HYPHAE_STAIRS);
    public static final RegistryObject<Item> STRIPPED_WARPED_HYPHAE_VERTICAL_SLAB = registerBlockItem(
            "stripped_warped_hyphae_vertical_slab", ModBlocks.STRIPPED_WARPED_HYPHAE_VERTICAL_SLAB);
    public static final RegistryObject<Item> TINTED_GLASS_SLAB = registerBlockItem("tinted_glass_slab",
            ModBlocks.TINTED_GLASS_SLAB);
    public static final RegistryObject<Item> TINTED_GLASS_STAIRS = registerBlockItem("tinted_glass_stairs",
            ModBlocks.TINTED_GLASS_STAIRS);
    public static final RegistryObject<Item> TINTED_GLASS_VERTICAL_SLAB = registerBlockItem(
            "tinted_glass_vertical_slab", ModBlocks.TINTED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> WARPED_HYPHAE_SLAB = registerBlockItem("warped_hyphae_slab",
            ModBlocks.WARPED_HYPHAE_SLAB);
    public static final RegistryObject<Item> WARPED_HYPHAE_STAIRS = registerBlockItem("warped_hyphae_stairs",
            ModBlocks.WARPED_HYPHAE_STAIRS);
    public static final RegistryObject<Item> WARPED_HYPHAE_VERTICAL_SLAB = registerBlockItem(
            "warped_hyphae_vertical_slab", ModBlocks.WARPED_HYPHAE_VERTICAL_SLAB);
    public static final RegistryObject<Item> MUFF_BLOCK = ITEMS.register("muff_block",
            () -> new MuffBlockItem(ModBlocks.MUFF_BLOCK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WHITE_WALLPAPER = registerBlockItem("white_wallpaper",
            ModBlocks.WHITE_WALLPAPER);
    public static final RegistryObject<Item> ORANGE_WALLPAPER = registerBlockItem("orange_wallpaper",
            ModBlocks.ORANGE_WALLPAPER);
    public static final RegistryObject<Item> MAGENTA_WALLPAPER = registerBlockItem("magenta_wallpaper",
            ModBlocks.MAGENTA_WALLPAPER);
    public static final RegistryObject<Item> LIGHT_BLUE_WALLPAPER = registerBlockItem("light_blue_wallpaper",
            ModBlocks.LIGHT_BLUE_WALLPAPER);
    public static final RegistryObject<Item> YELLOW_WALLPAPER = registerBlockItem("yellow_wallpaper",
            ModBlocks.YELLOW_WALLPAPER);
    public static final RegistryObject<Item> LIME_WALLPAPER = registerBlockItem("lime_wallpaper",
            ModBlocks.LIME_WALLPAPER);
    public static final RegistryObject<Item> PINK_WALLPAPER = registerBlockItem("pink_wallpaper",
            ModBlocks.PINK_WALLPAPER);
    public static final RegistryObject<Item> GRAY_WALLPAPER = registerBlockItem("gray_wallpaper",
            ModBlocks.GRAY_WALLPAPER);
    public static final RegistryObject<Item> LIGHT_GRAY_WALLPAPER = registerBlockItem("light_gray_wallpaper",
            ModBlocks.LIGHT_GRAY_WALLPAPER);
    public static final RegistryObject<Item> CYAN_WALLPAPER = registerBlockItem("cyan_wallpaper",
            ModBlocks.CYAN_WALLPAPER);
    public static final RegistryObject<Item> PURPLE_WALLPAPER = registerBlockItem("purple_wallpaper",
            ModBlocks.PURPLE_WALLPAPER);
    public static final RegistryObject<Item> BLUE_WALLPAPER = registerBlockItem("blue_wallpaper",
            ModBlocks.BLUE_WALLPAPER);
    public static final RegistryObject<Item> BROWN_WALLPAPER = registerBlockItem("brown_wallpaper",
            ModBlocks.BROWN_WALLPAPER);
    public static final RegistryObject<Item> GREEN_WALLPAPER = registerBlockItem("green_wallpaper",
            ModBlocks.GREEN_WALLPAPER);
    public static final RegistryObject<Item> RED_WALLPAPER = registerBlockItem("red_wallpaper",
            ModBlocks.RED_WALLPAPER);
    public static final RegistryObject<Item> BLACK_WALLPAPER = registerBlockItem("black_wallpaper",
            ModBlocks.BLACK_WALLPAPER);
    public static final RegistryObject<Item> WHITE_WALLPAPER_SLAB = registerBlockItem("white_wallpaper_slab",
            ModBlocks.WHITE_WALLPAPER_SLAB);
    public static final RegistryObject<Item> WHITE_WALLPAPER_VERTICAL_SLAB = registerBlockItem(
            "white_wallpaper_vertical_slab", ModBlocks.WHITE_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> ORANGE_WALLPAPER_SLAB = registerBlockItem("orange_wallpaper_slab",
            ModBlocks.ORANGE_WALLPAPER_SLAB);
    public static final RegistryObject<Item> ORANGE_WALLPAPER_VERTICAL_SLAB = registerBlockItem(
            "orange_wallpaper_vertical_slab", ModBlocks.ORANGE_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> MAGENTA_WALLPAPER_SLAB = registerBlockItem("magenta_wallpaper_slab",
            ModBlocks.MAGENTA_WALLPAPER_SLAB);
    public static final RegistryObject<Item> MAGENTA_WALLPAPER_VERTICAL_SLAB = registerBlockItem(
            "magenta_wallpaper_vertical_slab", ModBlocks.MAGENTA_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_WALLPAPER_SLAB = registerBlockItem("light_blue_wallpaper_slab",
            ModBlocks.LIGHT_BLUE_WALLPAPER_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_WALLPAPER_VERTICAL_SLAB = registerBlockItem(
            "light_blue_wallpaper_vertical_slab", ModBlocks.LIGHT_BLUE_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> YELLOW_WALLPAPER_SLAB = registerBlockItem("yellow_wallpaper_slab",
            ModBlocks.YELLOW_WALLPAPER_SLAB);
    public static final RegistryObject<Item> YELLOW_WALLPAPER_VERTICAL_SLAB = registerBlockItem(
            "yellow_wallpaper_vertical_slab", ModBlocks.YELLOW_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIME_WALLPAPER_SLAB = registerBlockItem("lime_wallpaper_slab",
            ModBlocks.LIME_WALLPAPER_SLAB);
    public static final RegistryObject<Item> LIME_WALLPAPER_VERTICAL_SLAB = registerBlockItem(
            "lime_wallpaper_vertical_slab", ModBlocks.LIME_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> PINK_WALLPAPER_SLAB = registerBlockItem("pink_wallpaper_slab",
            ModBlocks.PINK_WALLPAPER_SLAB);
    public static final RegistryObject<Item> PINK_WALLPAPER_VERTICAL_SLAB = registerBlockItem(
            "pink_wallpaper_vertical_slab", ModBlocks.PINK_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> GRAY_WALLPAPER_SLAB = registerBlockItem("gray_wallpaper_slab",
            ModBlocks.GRAY_WALLPAPER_SLAB);
    public static final RegistryObject<Item> GRAY_WALLPAPER_VERTICAL_SLAB = registerBlockItem(
            "gray_wallpaper_vertical_slab", ModBlocks.GRAY_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_WALLPAPER_SLAB = registerBlockItem("light_gray_wallpaper_slab",
            ModBlocks.LIGHT_GRAY_WALLPAPER_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_WALLPAPER_VERTICAL_SLAB = registerBlockItem(
            "light_gray_wallpaper_vertical_slab", ModBlocks.LIGHT_GRAY_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> CYAN_WALLPAPER_SLAB = registerBlockItem("cyan_wallpaper_slab",
            ModBlocks.CYAN_WALLPAPER_SLAB);
    public static final RegistryObject<Item> CYAN_WALLPAPER_VERTICAL_SLAB = registerBlockItem(
            "cyan_wallpaper_vertical_slab", ModBlocks.CYAN_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> PURPLE_WALLPAPER_SLAB = registerBlockItem("purple_wallpaper_slab",
            ModBlocks.PURPLE_WALLPAPER_SLAB);
    public static final RegistryObject<Item> PURPLE_WALLPAPER_VERTICAL_SLAB = registerBlockItem(
            "purple_wallpaper_vertical_slab", ModBlocks.PURPLE_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLUE_WALLPAPER_SLAB = registerBlockItem("blue_wallpaper_slab",
            ModBlocks.BLUE_WALLPAPER_SLAB);
    public static final RegistryObject<Item> BLUE_WALLPAPER_VERTICAL_SLAB = registerBlockItem(
            "blue_wallpaper_vertical_slab", ModBlocks.BLUE_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> BROWN_WALLPAPER_SLAB = registerBlockItem("brown_wallpaper_slab",
            ModBlocks.BROWN_WALLPAPER_SLAB);
    public static final RegistryObject<Item> BROWN_WALLPAPER_VERTICAL_SLAB = registerBlockItem(
            "brown_wallpaper_vertical_slab", ModBlocks.BROWN_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> GREEN_WALLPAPER_SLAB = registerBlockItem("green_wallpaper_slab",
            ModBlocks.GREEN_WALLPAPER_SLAB);
    public static final RegistryObject<Item> GREEN_WALLPAPER_VERTICAL_SLAB = registerBlockItem(
            "green_wallpaper_vertical_slab", ModBlocks.GREEN_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_WALLPAPER_SLAB = registerBlockItem("red_wallpaper_slab",
            ModBlocks.RED_WALLPAPER_SLAB);
    public static final RegistryObject<Item> RED_WALLPAPER_VERTICAL_SLAB = registerBlockItem(
            "red_wallpaper_vertical_slab", ModBlocks.RED_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLACK_WALLPAPER_SLAB = registerBlockItem("black_wallpaper_slab",
            ModBlocks.BLACK_WALLPAPER_SLAB);
    public static final RegistryObject<Item> BLACK_WALLPAPER_VERTICAL_SLAB = registerBlockItem(
            "black_wallpaper_vertical_slab", ModBlocks.BLACK_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> WHITE_WALLPAPER_FLAT = registerBlockItem("white_wallpaper_flat",
            ModBlocks.WHITE_WALLPAPER_FLAT);
    public static final RegistryObject<Item> ORANGE_WALLPAPER_FLAT = registerBlockItem("orange_wallpaper_flat",
            ModBlocks.ORANGE_WALLPAPER_FLAT);
    public static final RegistryObject<Item> MAGENTA_WALLPAPER_FLAT = registerBlockItem("magenta_wallpaper_flat",
            ModBlocks.MAGENTA_WALLPAPER_FLAT);
    public static final RegistryObject<Item> LIGHT_BLUE_WALLPAPER_FLAT = registerBlockItem("light_blue_wallpaper_flat",
            ModBlocks.LIGHT_BLUE_WALLPAPER_FLAT);
    public static final RegistryObject<Item> YELLOW_WALLPAPER_FLAT = registerBlockItem("yellow_wallpaper_flat",
            ModBlocks.YELLOW_WALLPAPER_FLAT);
    public static final RegistryObject<Item> LIME_WALLPAPER_FLAT = registerBlockItem("lime_wallpaper_flat",
            ModBlocks.LIME_WALLPAPER_FLAT);
    public static final RegistryObject<Item> PINK_WALLPAPER_FLAT = registerBlockItem("pink_wallpaper_flat",
            ModBlocks.PINK_WALLPAPER_FLAT);
    public static final RegistryObject<Item> GRAY_WALLPAPER_FLAT = registerBlockItem("gray_wallpaper_flat",
            ModBlocks.GRAY_WALLPAPER_FLAT);
    public static final RegistryObject<Item> LIGHT_GRAY_WALLPAPER_FLAT = registerBlockItem("light_gray_wallpaper_flat",
            ModBlocks.LIGHT_GRAY_WALLPAPER_FLAT);
    public static final RegistryObject<Item> CYAN_WALLPAPER_FLAT = registerBlockItem("cyan_wallpaper_flat",
            ModBlocks.CYAN_WALLPAPER_FLAT);
    public static final RegistryObject<Item> PURPLE_WALLPAPER_FLAT = registerBlockItem("purple_wallpaper_flat",
            ModBlocks.PURPLE_WALLPAPER_FLAT);
    public static final RegistryObject<Item> BLUE_WALLPAPER_FLAT = registerBlockItem("blue_wallpaper_flat",
            ModBlocks.BLUE_WALLPAPER_FLAT);
    public static final RegistryObject<Item> BROWN_WALLPAPER_FLAT = registerBlockItem("brown_wallpaper_flat",
            ModBlocks.BROWN_WALLPAPER_FLAT);
    public static final RegistryObject<Item> GREEN_WALLPAPER_FLAT = registerBlockItem("green_wallpaper_flat",
            ModBlocks.GREEN_WALLPAPER_FLAT);
    public static final RegistryObject<Item> RED_WALLPAPER_FLAT = registerBlockItem("red_wallpaper_flat",
            ModBlocks.RED_WALLPAPER_FLAT);
    public static final RegistryObject<Item> BLACK_WALLPAPER_FLAT = registerBlockItem("black_wallpaper_flat",
            ModBlocks.BLACK_WALLPAPER_FLAT);
    public static final RegistryObject<Item> WHITE_CUSHION = registerBlockItem("white_cushion",
            ModBlocks.WHITE_CUSHION);
    public static final RegistryObject<Item> ORANGE_CUSHION = registerBlockItem("orange_cushion",
            ModBlocks.ORANGE_CUSHION);
    public static final RegistryObject<Item> MAGENTA_CUSHION = registerBlockItem("magenta_cushion",
            ModBlocks.MAGENTA_CUSHION);
    public static final RegistryObject<Item> LIGHT_BLUE_CUSHION = registerBlockItem("light_blue_cushion",
            ModBlocks.LIGHT_BLUE_CUSHION);
    public static final RegistryObject<Item> YELLOW_CUSHION = registerBlockItem("yellow_cushion",
            ModBlocks.YELLOW_CUSHION);
    public static final RegistryObject<Item> LIME_CUSHION = registerBlockItem("lime_cushion", ModBlocks.LIME_CUSHION);
    public static final RegistryObject<Item> PINK_CUSHION = registerBlockItem("pink_cushion", ModBlocks.PINK_CUSHION);
    public static final RegistryObject<Item> GRAY_CUSHION = registerBlockItem("gray_cushion", ModBlocks.GRAY_CUSHION);
    public static final RegistryObject<Item> LIGHT_GRAY_CUSHION = registerBlockItem("light_gray_cushion",
            ModBlocks.LIGHT_GRAY_CUSHION);
    public static final RegistryObject<Item> CYAN_CUSHION = registerBlockItem("cyan_cushion", ModBlocks.CYAN_CUSHION);
    public static final RegistryObject<Item> PURPLE_CUSHION = registerBlockItem("purple_cushion",
            ModBlocks.PURPLE_CUSHION);
    public static final RegistryObject<Item> BLUE_CUSHION = registerBlockItem("blue_cushion", ModBlocks.BLUE_CUSHION);
    public static final RegistryObject<Item> BROWN_CUSHION = registerBlockItem("brown_cushion",
            ModBlocks.BROWN_CUSHION);
    public static final RegistryObject<Item> GREEN_CUSHION = registerBlockItem("green_cushion",
            ModBlocks.GREEN_CUSHION);
    public static final RegistryObject<Item> RED_CUSHION = registerBlockItem("red_cushion", ModBlocks.RED_CUSHION);
    public static final RegistryObject<Item> BLACK_CUSHION = registerBlockItem("black_cushion",
            ModBlocks.BLACK_CUSHION);

    public static final RegistryObject<Item> STRAW_BED = ITEMS.register("straw_bed",
            () -> new BedItem(ModBlocks.STRAW_BED.get(),
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(16)));
    public static final RegistryObject<Item> BIG_BOOK = registerBlockItem("big_book", ModBlocks.BIG_BOOK);
    public static final RegistryObject<Item> GLOWING_BIG_BOOK = registerBlockItem("glowing_big_book",
            ModBlocks.GLOWING_BIG_BOOK);
    public static final RegistryObject<Item> WHITE_BIG_BOOK = registerBlockItem("white_big_book",
            ModBlocks.WHITE_BIG_BOOK);
    public static final RegistryObject<Item> ORANGE_BIG_BOOK = registerBlockItem("orange_big_book",
            ModBlocks.ORANGE_BIG_BOOK);
    public static final RegistryObject<Item> MAGENTA_BIG_BOOK = registerBlockItem("magenta_big_book",
            ModBlocks.MAGENTA_BIG_BOOK);
    public static final RegistryObject<Item> LIGHT_BLUE_BIG_BOOK = registerBlockItem("light_blue_big_book",
            ModBlocks.LIGHT_BLUE_BIG_BOOK);
    public static final RegistryObject<Item> YELLOW_BIG_BOOK = registerBlockItem("yellow_big_book",
            ModBlocks.YELLOW_BIG_BOOK);
    public static final RegistryObject<Item> LIME_BIG_BOOK = registerBlockItem("lime_big_book",
            ModBlocks.LIME_BIG_BOOK);
    public static final RegistryObject<Item> PINK_BIG_BOOK = registerBlockItem("pink_big_book",
            ModBlocks.PINK_BIG_BOOK);
    public static final RegistryObject<Item> GRAY_BIG_BOOK = registerBlockItem("gray_big_book",
            ModBlocks.GRAY_BIG_BOOK);
    public static final RegistryObject<Item> LIGHT_GRAY_BIG_BOOK = registerBlockItem("light_gray_big_book",
            ModBlocks.LIGHT_GRAY_BIG_BOOK);
    public static final RegistryObject<Item> CYAN_BIG_BOOK = registerBlockItem("cyan_big_book",
            ModBlocks.CYAN_BIG_BOOK);
    public static final RegistryObject<Item> PURPLE_BIG_BOOK = registerBlockItem("purple_big_book",
            ModBlocks.PURPLE_BIG_BOOK);
    public static final RegistryObject<Item> BLUE_BIG_BOOK = registerBlockItem("blue_big_book",
            ModBlocks.BLUE_BIG_BOOK);
    public static final RegistryObject<Item> BROWN_BIG_BOOK = registerBlockItem("brown_big_book",
            ModBlocks.BROWN_BIG_BOOK);
    public static final RegistryObject<Item> GREEN_BIG_BOOK = registerBlockItem("green_big_book",
            ModBlocks.GREEN_BIG_BOOK);
    public static final RegistryObject<Item> RED_BIG_BOOK = registerBlockItem("red_big_book", ModBlocks.RED_BIG_BOOK);
    public static final RegistryObject<Item> BLACK_BIG_BOOK = registerBlockItem("black_big_book",
            ModBlocks.BLACK_BIG_BOOK);
    public static final RegistryObject<Item> PALE_OAK_LOG = registerBlockItem("pale_oak_log", ModBlocks.PALE_OAK_LOG);
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_LOG = registerBlockItem("stripped_pale_oak_log",
            ModBlocks.STRIPPED_PALE_OAK_LOG);
    public static final RegistryObject<Item> PALE_OAK_WOOD = registerBlockItem("pale_oak_wood",
            ModBlocks.PALE_OAK_WOOD);
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_WOOD = registerBlockItem("stripped_pale_oak_wood",
            ModBlocks.STRIPPED_PALE_OAK_WOOD);
    public static final RegistryObject<Item> PALE_OAK_PLANKS = registerBlockItem("pale_oak_planks",
            ModBlocks.PALE_OAK_PLANKS);
    public static final RegistryObject<Item> PALE_OAK_STAIRS = registerBlockItem("pale_oak_stairs",
            ModBlocks.PALE_OAK_STAIRS);
    public static final RegistryObject<Item> PALE_OAK_SLAB = registerBlockItem("pale_oak_slab",
            ModBlocks.PALE_OAK_SLAB);
    public static final RegistryObject<Item> PALE_OAK_VERTICAL_SLAB = registerBlockItem("pale_oak_vertical_slab",
            ModBlocks.PALE_OAK_VERTICAL_SLAB);
    public static final RegistryObject<Item> PALE_OAK_FENCE = registerBlockItem("pale_oak_fence",
            ModBlocks.PALE_OAK_FENCE);
    public static final RegistryObject<Item> PALE_OAK_FENCE_GATE = registerBlockItem("pale_oak_fence_gate",
            ModBlocks.PALE_OAK_FENCE_GATE);
    public static final RegistryObject<Item> PALE_OAK_DOOR = ITEMS.register("pale_oak_door",
            () -> new DoubleHighBlockItem(ModBlocks.PALE_OAK_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PALE_OAK_TRAPDOOR = registerBlockItem("pale_oak_trapdoor",
            ModBlocks.PALE_OAK_TRAPDOOR);
    public static final RegistryObject<Item> PALE_OAK_BUTTON = registerBlockItem("pale_oak_button",
            ModBlocks.PALE_OAK_BUTTON);
    public static final RegistryObject<Item> PALE_OAK_PRESSURE_PLATE = registerBlockItem("pale_oak_pressure_plate",
            ModBlocks.PALE_OAK_PRESSURE_PLATE);
    public static final RegistryObject<Item> PALE_OAK_LEAVES = registerBlockItem("pale_oak_leaves",
            ModBlocks.PALE_OAK_LEAVES);
    public static final RegistryObject<Item> PALE_OAK_SAPLING = registerBlockItem("pale_oak_sapling",
            ModBlocks.PALE_OAK_SAPLING);
    public static final RegistryObject<Item> PALE_OAK_LOG_SLAB = registerBlockItem("pale_oak_log_slab",
            ModBlocks.PALE_OAK_LOG_SLAB);
    public static final RegistryObject<Item> PALE_OAK_LOG_VERTICAL_SLAB = registerBlockItem(
            "pale_oak_log_vertical_slab", ModBlocks.PALE_OAK_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_LOG_SLAB = registerBlockItem(
            "stripped_pale_oak_log_slab", ModBlocks.STRIPPED_PALE_OAK_LOG_SLAB);
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_LOG_VERTICAL_SLAB = registerBlockItem(
            "stripped_pale_oak_log_vertical_slab", ModBlocks.STRIPPED_PALE_OAK_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> PALE_OAK_WOOD_SLAB = registerBlockItem("pale_oak_wood_slab",
            ModBlocks.PALE_OAK_WOOD_SLAB);
    public static final RegistryObject<Item> PALE_OAK_WOOD_VERTICAL_SLAB = registerBlockItem(
            "pale_oak_wood_vertical_slab", ModBlocks.PALE_OAK_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_WOOD_SLAB = registerBlockItem(
            "stripped_pale_oak_wood_slab", ModBlocks.STRIPPED_PALE_OAK_WOOD_SLAB);
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_WOOD_VERTICAL_SLAB = registerBlockItem(
            "stripped_pale_oak_wood_vertical_slab", ModBlocks.STRIPPED_PALE_OAK_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> CHERRY_LOG = registerBlockItem("cherry_log", ModBlocks.CHERRY_LOG);
    public static final RegistryObject<Item> STRIPPED_CHERRY_LOG = registerBlockItem("stripped_cherry_log",
            ModBlocks.STRIPPED_CHERRY_LOG);
    public static final RegistryObject<Item> CHERRY_WOOD = registerBlockItem("cherry_wood", ModBlocks.CHERRY_WOOD);
    public static final RegistryObject<Item> STRIPPED_CHERRY_WOOD = registerBlockItem("stripped_cherry_wood",
            ModBlocks.STRIPPED_CHERRY_WOOD);
    public static final RegistryObject<Item> CHERRY_PLANKS = registerBlockItem("cherry_planks",
            ModBlocks.CHERRY_PLANKS);
    public static final RegistryObject<Item> CHERRY_STAIRS = registerBlockItem("cherry_stairs",
            ModBlocks.CHERRY_STAIRS);
    public static final RegistryObject<Item> CHERRY_SLAB = registerBlockItem("cherry_slab", ModBlocks.CHERRY_SLAB);
    public static final RegistryObject<Item> CHERRY_VERTICAL_SLAB = registerBlockItem("cherry_vertical_slab",
            ModBlocks.CHERRY_VERTICAL_SLAB);
    public static final RegistryObject<Item> CHERRY_FENCE = registerBlockItem("cherry_fence", ModBlocks.CHERRY_FENCE);
    public static final RegistryObject<Item> CHERRY_FENCE_GATE = registerBlockItem("cherry_fence_gate",
            ModBlocks.CHERRY_FENCE_GATE);
    public static final RegistryObject<Item> CHERRY_DOOR = ITEMS.register("cherry_door",
            () -> new DoubleHighBlockItem(ModBlocks.CHERRY_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CHERRY_TRAPDOOR = registerBlockItem("cherry_trapdoor",
            ModBlocks.CHERRY_TRAPDOOR);
    public static final RegistryObject<Item> CHERRY_BUTTON = registerBlockItem("cherry_button",
            ModBlocks.CHERRY_BUTTON);
    public static final RegistryObject<Item> CHERRY_PRESSURE_PLATE = registerBlockItem("cherry_pressure_plate",
            ModBlocks.CHERRY_PRESSURE_PLATE);
    public static final RegistryObject<Item> CHERRY_LEAVES = registerBlockItem("cherry_leaves",
            ModBlocks.CHERRY_LEAVES);
    public static final RegistryObject<Item> CHERRY_SAPLING = registerBlockItem("cherry_sapling",
            ModBlocks.CHERRY_SAPLING);
    public static final RegistryObject<Item> CHERRY_LOG_SLAB = registerBlockItem("cherry_log_slab",
            ModBlocks.CHERRY_LOG_SLAB);
    public static final RegistryObject<Item> CHERRY_LOG_VERTICAL_SLAB = registerBlockItem("cherry_log_vertical_slab",
            ModBlocks.CHERRY_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_CHERRY_LOG_SLAB = registerBlockItem("stripped_cherry_log_slab",
            ModBlocks.STRIPPED_CHERRY_LOG_SLAB);
    public static final RegistryObject<Item> STRIPPED_CHERRY_LOG_VERTICAL_SLAB = registerBlockItem(
            "stripped_cherry_log_vertical_slab", ModBlocks.STRIPPED_CHERRY_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> CHERRY_WOOD_SLAB = registerBlockItem("cherry_wood_slab",
            ModBlocks.CHERRY_WOOD_SLAB);
    public static final RegistryObject<Item> CHERRY_WOOD_VERTICAL_SLAB = registerBlockItem("cherry_wood_vertical_slab",
            ModBlocks.CHERRY_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_CHERRY_WOOD_SLAB = registerBlockItem("stripped_cherry_wood_slab",
            ModBlocks.STRIPPED_CHERRY_WOOD_SLAB);
    public static final RegistryObject<Item> STRIPPED_CHERRY_WOOD_VERTICAL_SLAB = registerBlockItem(
            "stripped_cherry_wood_vertical_slab", ModBlocks.STRIPPED_CHERRY_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> PALE_MOSS_BLOCK = registerBlockItem("pale_moss_block",
            ModBlocks.PALE_MOSS_BLOCK);
}
