package com.kingodogo.buildscape.registry.items;

import com.kingodogo.buildscape.item.BigOrnamentTemplateItem;
import com.kingodogo.buildscape.item.ExperienceBucketItem;
import com.kingodogo.buildscape.item.FestiveGlintShardItem;
import com.kingodogo.buildscape.item.FestiveStarItem;
import com.kingodogo.buildscape.item.GoldenJarItem;
import com.kingodogo.buildscape.item.PatternItem;
import com.kingodogo.buildscape.item.StringlightFrameItem;
import com.kingodogo.buildscape.registry.ModBlocks;
import com.kingodogo.buildscape.registry.ModCreativeModeTab;
import com.kingodogo.buildscape.registry.ModFluids;
import com.kingodogo.buildscape.registry.ModSounds;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.registries.RegistryObject;

import static com.kingodogo.buildscape.registry.items.ItemsRegister.ITEMS;
import static com.kingodogo.buildscape.registry.items.ItemsRegister.registerBlockItem;

/**
 * The items STRIPPED_HOLLOW_SPRUCE_LOG through MUSIC_DISC_BUILDER of the registry, part 8 of 8, in registration order.
 * Named for its dominant families (cardboard, decor, wood); the neighbouring families that share a run of the order
 * with them are registered here too, because the registration order must not change.
 * Load order: {@link SteelItems#init()} runs first (see the static block), so every holder registers in order whichever is touched first.
 */
public final class CardboardItems {

    private CardboardItems() {}

    /** Forces this class to load, which registers its items. */
    public static void init() {}

    static {
        SteelItems.init();
    }

    public static final RegistryObject<Item> STRIPPED_HOLLOW_SPRUCE_LOG = registerBlockItem(
            "stripped_hollow_spruce_log", ModBlocks.STRIPPED_HOLLOW_SPRUCE_LOG);
    public static final RegistryObject<Item> HOLLOW_BIRCH_LOG = registerBlockItem("hollow_birch_log",
            ModBlocks.HOLLOW_BIRCH_LOG);
    public static final RegistryObject<Item> STRIPPED_HOLLOW_BIRCH_LOG = registerBlockItem("stripped_hollow_birch_log",
            ModBlocks.STRIPPED_HOLLOW_BIRCH_LOG);
    public static final RegistryObject<Item> HOLLOW_JUNGLE_LOG = registerBlockItem("hollow_jungle_log",
            ModBlocks.HOLLOW_JUNGLE_LOG);
    public static final RegistryObject<Item> STRIPPED_HOLLOW_JUNGLE_LOG = registerBlockItem(
            "stripped_hollow_jungle_log", ModBlocks.STRIPPED_HOLLOW_JUNGLE_LOG);
    public static final RegistryObject<Item> HOLLOW_ACACIA_LOG = registerBlockItem("hollow_acacia_log",
            ModBlocks.HOLLOW_ACACIA_LOG);
    public static final RegistryObject<Item> STRIPPED_HOLLOW_ACACIA_LOG = registerBlockItem(
            "stripped_hollow_acacia_log", ModBlocks.STRIPPED_HOLLOW_ACACIA_LOG);
    public static final RegistryObject<Item> HOLLOW_DARK_OAK_LOG = registerBlockItem("hollow_dark_oak_log",
            ModBlocks.HOLLOW_DARK_OAK_LOG);
    public static final RegistryObject<Item> STRIPPED_HOLLOW_DARK_OAK_LOG = registerBlockItem(
            "stripped_hollow_dark_oak_log", ModBlocks.STRIPPED_HOLLOW_DARK_OAK_LOG);
    public static final RegistryObject<Item> HOLLOW_CRIMSON_STEM = registerBlockItem("hollow_crimson_stem",
            ModBlocks.HOLLOW_CRIMSON_STEM);
    public static final RegistryObject<Item> STRIPPED_HOLLOW_CRIMSON_STEM = registerBlockItem(
            "stripped_hollow_crimson_stem", ModBlocks.STRIPPED_HOLLOW_CRIMSON_STEM);
    public static final RegistryObject<Item> HOLLOW_WARPED_STEM = registerBlockItem("hollow_warped_stem",
            ModBlocks.HOLLOW_WARPED_STEM);
    public static final RegistryObject<Item> STRIPPED_HOLLOW_WARPED_STEM = registerBlockItem(
            "stripped_hollow_warped_stem", ModBlocks.STRIPPED_HOLLOW_WARPED_STEM);
    public static final RegistryObject<Item> HOLLOW_MANGROVE_LOG = registerBlockItem("hollow_mangrove_log",
            ModBlocks.HOLLOW_MANGROVE_LOG);
    public static final RegistryObject<Item> STRIPPED_HOLLOW_MANGROVE_LOG = registerBlockItem(
            "stripped_hollow_mangrove_log", ModBlocks.STRIPPED_HOLLOW_MANGROVE_LOG);
    public static final RegistryObject<Item> HOLLOW_ASHPEN_LOG = registerBlockItem("hollow_ashpen_log",
            ModBlocks.HOLLOW_ASHPEN_LOG);
    public static final RegistryObject<Item> STRIPPED_HOLLOW_ASHPEN_LOG = registerBlockItem(
            "stripped_hollow_ashpen_log", ModBlocks.STRIPPED_HOLLOW_ASHPEN_LOG);
    public static final RegistryObject<Item> HOLLOW_POPLAR_LOG = registerBlockItem("hollow_poplar_log",
            ModBlocks.HOLLOW_POPLAR_LOG);
    public static final RegistryObject<Item> STRIPPED_HOLLOW_POPLAR_LOG = registerBlockItem(
            "stripped_hollow_poplar_log", ModBlocks.STRIPPED_HOLLOW_POPLAR_LOG);
    public static final RegistryObject<Item> HOLLOW_PALE_OAK_LOG = registerBlockItem("hollow_pale_oak_log",
            ModBlocks.HOLLOW_PALE_OAK_LOG);
    public static final RegistryObject<Item> STRIPPED_HOLLOW_PALE_OAK_LOG = registerBlockItem(
            "stripped_hollow_pale_oak_log", ModBlocks.STRIPPED_HOLLOW_PALE_OAK_LOG);
    public static final RegistryObject<Item> HOLLOW_CHERRY_LOG = registerBlockItem("hollow_cherry_log",
            ModBlocks.HOLLOW_CHERRY_LOG);
    public static final RegistryObject<Item> STRIPPED_HOLLOW_CHERRY_LOG = registerBlockItem(
            "stripped_hollow_cherry_log", ModBlocks.STRIPPED_HOLLOW_CHERRY_LOG);
    public static final RegistryObject<Item> CARDBOARD_BLOCK = registerBlockItem("cardboard_block",
            ModBlocks.CARDBOARD_BLOCK);
    public static final RegistryObject<Item> CARDBOARD_SLAB = registerBlockItem("cardboard_slab",
            ModBlocks.CARDBOARD_SLAB);
    public static final RegistryObject<Item> CARDBOARD_STAIRS = registerBlockItem("cardboard_stairs",
            ModBlocks.CARDBOARD_STAIRS);
    public static final RegistryObject<Item> CARDBOARD_WALL = registerBlockItem("cardboard_wall",
            ModBlocks.CARDBOARD_WALL);
    public static final RegistryObject<Item> CARDBOARD_VERTICAL_SLAB = registerBlockItem("cardboard_vertical_slab",
            ModBlocks.CARDBOARD_VERTICAL_SLAB);
    public static final RegistryObject<Item> CARDBOARD_TRAPDOOR = registerBlockItem("cardboard_trapdoor",
            ModBlocks.CARDBOARD_TRAPDOOR);
    public static final RegistryObject<Item> CARDBOARD_BUTTON = registerBlockItem("cardboard_button",
            ModBlocks.CARDBOARD_BUTTON);
    public static final RegistryObject<Item> CARDBOARD_PRESSURE_PLATE = registerBlockItem("cardboard_pressure_plate",
            ModBlocks.CARDBOARD_PRESSURE_PLATE);
    public static final RegistryObject<Item> SMOOTH_CARDBOARD_BLOCK = registerBlockItem("smooth_cardboard_block",
            ModBlocks.SMOOTH_CARDBOARD_BLOCK);
    public static final RegistryObject<Item> SMOOTH_CARDBOARD_SLAB = registerBlockItem("smooth_cardboard_slab",
            ModBlocks.SMOOTH_CARDBOARD_SLAB);
    public static final RegistryObject<Item> SMOOTH_CARDBOARD_STAIRS = registerBlockItem("smooth_cardboard_stairs",
            ModBlocks.SMOOTH_CARDBOARD_STAIRS);
    public static final RegistryObject<Item> SMOOTH_CARDBOARD_WALL = registerBlockItem("smooth_cardboard_wall",
            ModBlocks.SMOOTH_CARDBOARD_WALL);
    public static final RegistryObject<Item> SMOOTH_CARDBOARD_VERTICAL_SLAB = registerBlockItem(
            "smooth_cardboard_vertical_slab", ModBlocks.SMOOTH_CARDBOARD_VERTICAL_SLAB);
    public static final RegistryObject<Item> BUNDLED_CARDBOARD = registerBlockItem("bundled_cardboard",
            ModBlocks.BUNDLED_CARDBOARD);
    public static final RegistryObject<Item> BUNDLED_CARDBOARD_SLAB = registerBlockItem("bundled_cardboard_slab",
            ModBlocks.BUNDLED_CARDBOARD_SLAB);
    public static final RegistryObject<Item> BUNDLED_CARDBOARD_STAIRS = registerBlockItem("bundled_cardboard_stairs",
            ModBlocks.BUNDLED_CARDBOARD_STAIRS);
    public static final RegistryObject<Item> BUNDLED_CARDBOARD_WALL = registerBlockItem("bundled_cardboard_wall",
            ModBlocks.BUNDLED_CARDBOARD_WALL);
    public static final RegistryObject<Item> BUNDLED_CARDBOARD_VERTICAL_SLAB = registerBlockItem(
            "bundled_cardboard_vertical_slab", ModBlocks.BUNDLED_CARDBOARD_VERTICAL_SLAB);
    public static final RegistryObject<Item> PRESSED_CARDBOARD = registerBlockItem("pressed_cardboard",
            ModBlocks.PRESSED_CARDBOARD);
    public static final RegistryObject<Item> PRESSED_CARDBOARD_SLAB = registerBlockItem("pressed_cardboard_slab",
            ModBlocks.PRESSED_CARDBOARD_SLAB);
    public static final RegistryObject<Item> PRESSED_CARDBOARD_STAIRS = registerBlockItem("pressed_cardboard_stairs",
            ModBlocks.PRESSED_CARDBOARD_STAIRS);
    public static final RegistryObject<Item> PRESSED_CARDBOARD_WALL = registerBlockItem("pressed_cardboard_wall",
            ModBlocks.PRESSED_CARDBOARD_WALL);
    public static final RegistryObject<Item> PRESSED_CARDBOARD_VERTICAL_SLAB = registerBlockItem(
            "pressed_cardboard_vertical_slab", ModBlocks.PRESSED_CARDBOARD_VERTICAL_SLAB);
    public static final RegistryObject<Item> THICK_CARDBOARD_BLOCK = registerBlockItem("thick_cardboard_block",
            ModBlocks.THICK_CARDBOARD_BLOCK);
    public static final RegistryObject<Item> THICK_CARDBOARD_SLAB = registerBlockItem("thick_cardboard_slab",
            ModBlocks.THICK_CARDBOARD_SLAB);
    public static final RegistryObject<Item> THICK_CARDBOARD_STAIRS = registerBlockItem("thick_cardboard_stairs",
            ModBlocks.THICK_CARDBOARD_STAIRS);
    public static final RegistryObject<Item> THICK_CARDBOARD_WALL = registerBlockItem("thick_cardboard_wall",
            ModBlocks.THICK_CARDBOARD_WALL);
    public static final RegistryObject<Item> THICK_CARDBOARD_VERTICAL_SLAB = registerBlockItem(
            "thick_cardboard_vertical_slab", ModBlocks.THICK_CARDBOARD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_CARDBOARD_BLOCK = registerBlockItem("stripped_cardboard_block",
            ModBlocks.STRIPPED_CARDBOARD_BLOCK);
    public static final RegistryObject<Item> STRIPPED_CARDBOARD_SLAB = registerBlockItem("stripped_cardboard_slab",
            ModBlocks.STRIPPED_CARDBOARD_SLAB);
    public static final RegistryObject<Item> STRIPPED_CARDBOARD_STAIRS = registerBlockItem("stripped_cardboard_stairs",
            ModBlocks.STRIPPED_CARDBOARD_STAIRS);
    public static final RegistryObject<Item> STRIPPED_CARDBOARD_WALL = registerBlockItem("stripped_cardboard_wall",
            ModBlocks.STRIPPED_CARDBOARD_WALL);
    public static final RegistryObject<Item> STRIPPED_CARDBOARD_VERTICAL_SLAB = registerBlockItem(
            "stripped_cardboard_vertical_slab", ModBlocks.STRIPPED_CARDBOARD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_CARDBOARD_TRAPDOOR = registerBlockItem(
            "stripped_cardboard_trapdoor", ModBlocks.STRIPPED_CARDBOARD_TRAPDOOR);
    public static final RegistryObject<Item> STRIPPED_CARDBOARD_BUTTON = registerBlockItem("stripped_cardboard_button",
            ModBlocks.STRIPPED_CARDBOARD_BUTTON);
    public static final RegistryObject<Item> STRIPPED_CARDBOARD_PRESSURE_PLATE = registerBlockItem(
            "stripped_cardboard_pressure_plate", ModBlocks.STRIPPED_CARDBOARD_PRESSURE_PLATE);
    public static final RegistryObject<Item> STRIPPED_SMOOTH_CARDBOARD_BLOCK = registerBlockItem(
            "stripped_smooth_cardboard_block", ModBlocks.STRIPPED_SMOOTH_CARDBOARD_BLOCK);
    public static final RegistryObject<Item> STRIPPED_SMOOTH_CARDBOARD_SLAB = registerBlockItem(
            "stripped_smooth_cardboard_slab", ModBlocks.STRIPPED_SMOOTH_CARDBOARD_SLAB);
    public static final RegistryObject<Item> STRIPPED_SMOOTH_CARDBOARD_STAIRS = registerBlockItem(
            "stripped_smooth_cardboard_stairs", ModBlocks.STRIPPED_SMOOTH_CARDBOARD_STAIRS);
    public static final RegistryObject<Item> STRIPPED_SMOOTH_CARDBOARD_WALL = registerBlockItem(
            "stripped_smooth_cardboard_wall", ModBlocks.STRIPPED_SMOOTH_CARDBOARD_WALL);
    public static final RegistryObject<Item> STRIPPED_SMOOTH_CARDBOARD_VERTICAL_SLAB = registerBlockItem(
            "stripped_smooth_cardboard_vertical_slab", ModBlocks.STRIPPED_SMOOTH_CARDBOARD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_BUNDLED_CARDBOARD = registerBlockItem(
            "stripped_bundled_cardboard", ModBlocks.STRIPPED_BUNDLED_CARDBOARD);
    public static final RegistryObject<Item> STRIPPED_BUNDLED_CARDBOARD_SLAB = registerBlockItem(
            "stripped_bundled_cardboard_slab", ModBlocks.STRIPPED_BUNDLED_CARDBOARD_SLAB);
    public static final RegistryObject<Item> STRIPPED_BUNDLED_CARDBOARD_STAIRS = registerBlockItem(
            "stripped_bundled_cardboard_stairs", ModBlocks.STRIPPED_BUNDLED_CARDBOARD_STAIRS);
    public static final RegistryObject<Item> STRIPPED_BUNDLED_CARDBOARD_WALL = registerBlockItem(
            "stripped_bundled_cardboard_wall", ModBlocks.STRIPPED_BUNDLED_CARDBOARD_WALL);
    public static final RegistryObject<Item> STRIPPED_BUNDLED_CARDBOARD_VERTICAL_SLAB = registerBlockItem(
            "stripped_bundled_cardboard_vertical_slab", ModBlocks.STRIPPED_BUNDLED_CARDBOARD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_PRESSED_CARDBOARD = registerBlockItem(
            "stripped_pressed_cardboard", ModBlocks.STRIPPED_PRESSED_CARDBOARD);
    public static final RegistryObject<Item> STRIPPED_PRESSED_CARDBOARD_SLAB = registerBlockItem(
            "stripped_pressed_cardboard_slab", ModBlocks.STRIPPED_PRESSED_CARDBOARD_SLAB);
    public static final RegistryObject<Item> STRIPPED_PRESSED_CARDBOARD_STAIRS = registerBlockItem(
            "stripped_pressed_cardboard_stairs", ModBlocks.STRIPPED_PRESSED_CARDBOARD_STAIRS);
    public static final RegistryObject<Item> STRIPPED_PRESSED_CARDBOARD_WALL = registerBlockItem(
            "stripped_pressed_cardboard_wall", ModBlocks.STRIPPED_PRESSED_CARDBOARD_WALL);
    public static final RegistryObject<Item> STRIPPED_PRESSED_CARDBOARD_VERTICAL_SLAB = registerBlockItem(
            "stripped_pressed_cardboard_vertical_slab", ModBlocks.STRIPPED_PRESSED_CARDBOARD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_THICK_CARDBOARD_BLOCK = registerBlockItem(
            "stripped_thick_cardboard_block", ModBlocks.STRIPPED_THICK_CARDBOARD_BLOCK);
    public static final RegistryObject<Item> STRIPPED_THICK_CARDBOARD_SLAB = registerBlockItem(
            "stripped_thick_cardboard_slab", ModBlocks.STRIPPED_THICK_CARDBOARD_SLAB);
    public static final RegistryObject<Item> STRIPPED_THICK_CARDBOARD_STAIRS = registerBlockItem(
            "stripped_thick_cardboard_stairs", ModBlocks.STRIPPED_THICK_CARDBOARD_STAIRS);
    public static final RegistryObject<Item> STRIPPED_THICK_CARDBOARD_WALL = registerBlockItem(
            "stripped_thick_cardboard_wall", ModBlocks.STRIPPED_THICK_CARDBOARD_WALL);
    public static final RegistryObject<Item> STRIPPED_THICK_CARDBOARD_VERTICAL_SLAB = registerBlockItem(
            "stripped_thick_cardboard_vertical_slab", ModBlocks.STRIPPED_THICK_CARDBOARD_VERTICAL_SLAB);
    public static final RegistryObject<Item> TINTED_CARDBOARD_BLOCK = registerBlockItem("tinted_cardboard_block",
            ModBlocks.TINTED_CARDBOARD_BLOCK);
    public static final RegistryObject<Item> TINTED_CARDBOARD_SLAB = registerBlockItem("tinted_cardboard_slab",
            ModBlocks.TINTED_CARDBOARD_SLAB);
    public static final RegistryObject<Item> TINTED_CARDBOARD_STAIRS = registerBlockItem("tinted_cardboard_stairs",
            ModBlocks.TINTED_CARDBOARD_STAIRS);
    public static final RegistryObject<Item> TINTED_CARDBOARD_WALL = registerBlockItem("tinted_cardboard_wall",
            ModBlocks.TINTED_CARDBOARD_WALL);
    public static final RegistryObject<Item> TINTED_CARDBOARD_VERTICAL_SLAB = registerBlockItem(
            "tinted_cardboard_vertical_slab", ModBlocks.TINTED_CARDBOARD_VERTICAL_SLAB);
    public static final RegistryObject<Item> TINTED_CARDBOARD_TRAPDOOR = registerBlockItem("tinted_cardboard_trapdoor",
            ModBlocks.TINTED_CARDBOARD_TRAPDOOR);
    public static final RegistryObject<Item> TINTED_CARDBOARD_BUTTON = registerBlockItem("tinted_cardboard_button",
            ModBlocks.TINTED_CARDBOARD_BUTTON);
    public static final RegistryObject<Item> TINTED_CARDBOARD_PRESSURE_PLATE = registerBlockItem(
            "tinted_cardboard_pressure_plate", ModBlocks.TINTED_CARDBOARD_PRESSURE_PLATE);
    public static final RegistryObject<Item> TINTED_SMOOTH_CARDBOARD_BLOCK = registerBlockItem(
            "tinted_smooth_cardboard_block", ModBlocks.TINTED_SMOOTH_CARDBOARD_BLOCK);
    public static final RegistryObject<Item> TINTED_SMOOTH_CARDBOARD_SLAB = registerBlockItem(
            "tinted_smooth_cardboard_slab", ModBlocks.TINTED_SMOOTH_CARDBOARD_SLAB);
    public static final RegistryObject<Item> TINTED_SMOOTH_CARDBOARD_STAIRS = registerBlockItem(
            "tinted_smooth_cardboard_stairs", ModBlocks.TINTED_SMOOTH_CARDBOARD_STAIRS);
    public static final RegistryObject<Item> TINTED_SMOOTH_CARDBOARD_WALL = registerBlockItem(
            "tinted_smooth_cardboard_wall", ModBlocks.TINTED_SMOOTH_CARDBOARD_WALL);
    public static final RegistryObject<Item> TINTED_SMOOTH_CARDBOARD_VERTICAL_SLAB = registerBlockItem(
            "tinted_smooth_cardboard_vertical_slab", ModBlocks.TINTED_SMOOTH_CARDBOARD_VERTICAL_SLAB);
    public static final RegistryObject<Item> TINTED_BUNDLED_CARDBOARD = registerBlockItem("tinted_bundled_cardboard",
            ModBlocks.TINTED_BUNDLED_CARDBOARD);
    public static final RegistryObject<Item> TINTED_BUNDLED_CARDBOARD_SLAB = registerBlockItem(
            "tinted_bundled_cardboard_slab", ModBlocks.TINTED_BUNDLED_CARDBOARD_SLAB);
    public static final RegistryObject<Item> TINTED_BUNDLED_CARDBOARD_STAIRS = registerBlockItem(
            "tinted_bundled_cardboard_stairs", ModBlocks.TINTED_BUNDLED_CARDBOARD_STAIRS);
    public static final RegistryObject<Item> TINTED_BUNDLED_CARDBOARD_WALL = registerBlockItem(
            "tinted_bundled_cardboard_wall", ModBlocks.TINTED_BUNDLED_CARDBOARD_WALL);
    public static final RegistryObject<Item> TINTED_BUNDLED_CARDBOARD_VERTICAL_SLAB = registerBlockItem(
            "tinted_bundled_cardboard_vertical_slab", ModBlocks.TINTED_BUNDLED_CARDBOARD_VERTICAL_SLAB);
    public static final RegistryObject<Item> TINTED_PRESSED_CARDBOARD = registerBlockItem("tinted_pressed_cardboard",
            ModBlocks.TINTED_PRESSED_CARDBOARD);
    public static final RegistryObject<Item> TINTED_PRESSED_CARDBOARD_SLAB = registerBlockItem(
            "tinted_pressed_cardboard_slab", ModBlocks.TINTED_PRESSED_CARDBOARD_SLAB);
    public static final RegistryObject<Item> TINTED_PRESSED_CARDBOARD_STAIRS = registerBlockItem(
            "tinted_pressed_cardboard_stairs", ModBlocks.TINTED_PRESSED_CARDBOARD_STAIRS);
    public static final RegistryObject<Item> TINTED_PRESSED_CARDBOARD_WALL = registerBlockItem(
            "tinted_pressed_cardboard_wall", ModBlocks.TINTED_PRESSED_CARDBOARD_WALL);
    public static final RegistryObject<Item> TINTED_PRESSED_CARDBOARD_VERTICAL_SLAB = registerBlockItem(
            "tinted_pressed_cardboard_vertical_slab", ModBlocks.TINTED_PRESSED_CARDBOARD_VERTICAL_SLAB);
    public static final RegistryObject<Item> TINTED_THICK_CARDBOARD_BLOCK = registerBlockItem(
            "tinted_thick_cardboard_block", ModBlocks.TINTED_THICK_CARDBOARD_BLOCK);
    public static final RegistryObject<Item> TINTED_THICK_CARDBOARD_SLAB = registerBlockItem(
            "tinted_thick_cardboard_slab", ModBlocks.TINTED_THICK_CARDBOARD_SLAB);
    public static final RegistryObject<Item> TINTED_THICK_CARDBOARD_STAIRS = registerBlockItem(
            "tinted_thick_cardboard_stairs", ModBlocks.TINTED_THICK_CARDBOARD_STAIRS);
    public static final RegistryObject<Item> TINTED_THICK_CARDBOARD_WALL = registerBlockItem(
            "tinted_thick_cardboard_wall", ModBlocks.TINTED_THICK_CARDBOARD_WALL);
    public static final RegistryObject<Item> TINTED_THICK_CARDBOARD_VERTICAL_SLAB = registerBlockItem(
            "tinted_thick_cardboard_vertical_slab", ModBlocks.TINTED_THICK_CARDBOARD_VERTICAL_SLAB);
    public static final RegistryObject<Item> WASHED_CARDBOARD_BLOCK = registerBlockItem("washed_cardboard_block",
            ModBlocks.WASHED_CARDBOARD_BLOCK);
    public static final RegistryObject<Item> WASHED_CARDBOARD_SLAB = registerBlockItem("washed_cardboard_slab",
            ModBlocks.WASHED_CARDBOARD_SLAB);
    public static final RegistryObject<Item> WASHED_CARDBOARD_STAIRS = registerBlockItem("washed_cardboard_stairs",
            ModBlocks.WASHED_CARDBOARD_STAIRS);
    public static final RegistryObject<Item> WASHED_CARDBOARD_WALL = registerBlockItem("washed_cardboard_wall",
            ModBlocks.WASHED_CARDBOARD_WALL);
    public static final RegistryObject<Item> WASHED_CARDBOARD_VERTICAL_SLAB = registerBlockItem(
            "washed_cardboard_vertical_slab", ModBlocks.WASHED_CARDBOARD_VERTICAL_SLAB);
    public static final RegistryObject<Item> WASHED_CARDBOARD_TRAPDOOR = registerBlockItem("washed_cardboard_trapdoor",
            ModBlocks.WASHED_CARDBOARD_TRAPDOOR);
    public static final RegistryObject<Item> WASHED_CARDBOARD_BUTTON = registerBlockItem("washed_cardboard_button",
            ModBlocks.WASHED_CARDBOARD_BUTTON);
    public static final RegistryObject<Item> WASHED_CARDBOARD_PRESSURE_PLATE = registerBlockItem(
            "washed_cardboard_pressure_plate", ModBlocks.WASHED_CARDBOARD_PRESSURE_PLATE);
    public static final RegistryObject<Item> WASHED_SMOOTH_CARDBOARD_BLOCK = registerBlockItem(
            "washed_smooth_cardboard_block", ModBlocks.WASHED_SMOOTH_CARDBOARD_BLOCK);
    public static final RegistryObject<Item> WASHED_SMOOTH_CARDBOARD_SLAB = registerBlockItem(
            "washed_smooth_cardboard_slab", ModBlocks.WASHED_SMOOTH_CARDBOARD_SLAB);
    public static final RegistryObject<Item> WASHED_SMOOTH_CARDBOARD_STAIRS = registerBlockItem(
            "washed_smooth_cardboard_stairs", ModBlocks.WASHED_SMOOTH_CARDBOARD_STAIRS);
    public static final RegistryObject<Item> WASHED_SMOOTH_CARDBOARD_WALL = registerBlockItem(
            "washed_smooth_cardboard_wall", ModBlocks.WASHED_SMOOTH_CARDBOARD_WALL);
    public static final RegistryObject<Item> WASHED_SMOOTH_CARDBOARD_VERTICAL_SLAB = registerBlockItem(
            "washed_smooth_cardboard_vertical_slab", ModBlocks.WASHED_SMOOTH_CARDBOARD_VERTICAL_SLAB);
    public static final RegistryObject<Item> WASHED_BUNDLED_CARDBOARD = registerBlockItem("washed_bundled_cardboard",
            ModBlocks.WASHED_BUNDLED_CARDBOARD);
    public static final RegistryObject<Item> WASHED_BUNDLED_CARDBOARD_SLAB = registerBlockItem(
            "washed_bundled_cardboard_slab", ModBlocks.WASHED_BUNDLED_CARDBOARD_SLAB);
    public static final RegistryObject<Item> WASHED_BUNDLED_CARDBOARD_STAIRS = registerBlockItem(
            "washed_bundled_cardboard_stairs", ModBlocks.WASHED_BUNDLED_CARDBOARD_STAIRS);
    public static final RegistryObject<Item> WASHED_BUNDLED_CARDBOARD_WALL = registerBlockItem(
            "washed_bundled_cardboard_wall", ModBlocks.WASHED_BUNDLED_CARDBOARD_WALL);
    public static final RegistryObject<Item> WASHED_BUNDLED_CARDBOARD_VERTICAL_SLAB = registerBlockItem(
            "washed_bundled_cardboard_vertical_slab", ModBlocks.WASHED_BUNDLED_CARDBOARD_VERTICAL_SLAB);
    public static final RegistryObject<Item> WASHED_PRESSED_CARDBOARD = registerBlockItem("washed_pressed_cardboard",
            ModBlocks.WASHED_PRESSED_CARDBOARD);
    public static final RegistryObject<Item> WASHED_PRESSED_CARDBOARD_SLAB = registerBlockItem(
            "washed_pressed_cardboard_slab", ModBlocks.WASHED_PRESSED_CARDBOARD_SLAB);
    public static final RegistryObject<Item> WASHED_PRESSED_CARDBOARD_STAIRS = registerBlockItem(
            "washed_pressed_cardboard_stairs", ModBlocks.WASHED_PRESSED_CARDBOARD_STAIRS);
    public static final RegistryObject<Item> WASHED_PRESSED_CARDBOARD_WALL = registerBlockItem(
            "washed_pressed_cardboard_wall", ModBlocks.WASHED_PRESSED_CARDBOARD_WALL);
    public static final RegistryObject<Item> WASHED_PRESSED_CARDBOARD_VERTICAL_SLAB = registerBlockItem(
            "washed_pressed_cardboard_vertical_slab", ModBlocks.WASHED_PRESSED_CARDBOARD_VERTICAL_SLAB);
    public static final RegistryObject<Item> WASHED_THICK_CARDBOARD_BLOCK = registerBlockItem(
            "washed_thick_cardboard_block", ModBlocks.WASHED_THICK_CARDBOARD_BLOCK);
    public static final RegistryObject<Item> WASHED_THICK_CARDBOARD_SLAB = registerBlockItem(
            "washed_thick_cardboard_slab", ModBlocks.WASHED_THICK_CARDBOARD_SLAB);
    public static final RegistryObject<Item> WASHED_THICK_CARDBOARD_STAIRS = registerBlockItem(
            "washed_thick_cardboard_stairs", ModBlocks.WASHED_THICK_CARDBOARD_STAIRS);
    public static final RegistryObject<Item> WASHED_THICK_CARDBOARD_WALL = registerBlockItem(
            "washed_thick_cardboard_wall", ModBlocks.WASHED_THICK_CARDBOARD_WALL);
    public static final RegistryObject<Item> WASHED_THICK_CARDBOARD_VERTICAL_SLAB = registerBlockItem(
            "washed_thick_cardboard_vertical_slab", ModBlocks.WASHED_THICK_CARDBOARD_VERTICAL_SLAB);
    public static final RegistryObject<Item> BURNT_CARDBOARD_BLOCK = registerBlockItem("burnt_cardboard_block",
            ModBlocks.BURNT_CARDBOARD_BLOCK);
    public static final RegistryObject<Item> BURNT_CARDBOARD_SLAB = registerBlockItem("burnt_cardboard_slab",
            ModBlocks.BURNT_CARDBOARD_SLAB);
    public static final RegistryObject<Item> BURNT_CARDBOARD_STAIRS = registerBlockItem("burnt_cardboard_stairs",
            ModBlocks.BURNT_CARDBOARD_STAIRS);
    public static final RegistryObject<Item> BURNT_CARDBOARD_WALL = registerBlockItem("burnt_cardboard_wall",
            ModBlocks.BURNT_CARDBOARD_WALL);
    public static final RegistryObject<Item> BURNT_CARDBOARD_VERTICAL_SLAB = registerBlockItem(
            "burnt_cardboard_vertical_slab", ModBlocks.BURNT_CARDBOARD_VERTICAL_SLAB);
    public static final RegistryObject<Item> BURNT_CARDBOARD_TRAPDOOR = registerBlockItem("burnt_cardboard_trapdoor",
            ModBlocks.BURNT_CARDBOARD_TRAPDOOR);
    public static final RegistryObject<Item> BURNT_CARDBOARD_BUTTON = registerBlockItem("burnt_cardboard_button",
            ModBlocks.BURNT_CARDBOARD_BUTTON);
    public static final RegistryObject<Item> BURNT_CARDBOARD_PRESSURE_PLATE = registerBlockItem(
            "burnt_cardboard_pressure_plate", ModBlocks.BURNT_CARDBOARD_PRESSURE_PLATE);
    public static final RegistryObject<Item> BURNT_SMOOTH_CARDBOARD_BLOCK = registerBlockItem(
            "burnt_smooth_cardboard_block", ModBlocks.BURNT_SMOOTH_CARDBOARD_BLOCK);
    public static final RegistryObject<Item> BURNT_SMOOTH_CARDBOARD_SLAB = registerBlockItem(
            "burnt_smooth_cardboard_slab", ModBlocks.BURNT_SMOOTH_CARDBOARD_SLAB);
    public static final RegistryObject<Item> BURNT_SMOOTH_CARDBOARD_STAIRS = registerBlockItem(
            "burnt_smooth_cardboard_stairs", ModBlocks.BURNT_SMOOTH_CARDBOARD_STAIRS);
    public static final RegistryObject<Item> BURNT_SMOOTH_CARDBOARD_WALL = registerBlockItem(
            "burnt_smooth_cardboard_wall", ModBlocks.BURNT_SMOOTH_CARDBOARD_WALL);
    public static final RegistryObject<Item> BURNT_SMOOTH_CARDBOARD_VERTICAL_SLAB = registerBlockItem(
            "burnt_smooth_cardboard_vertical_slab", ModBlocks.BURNT_SMOOTH_CARDBOARD_VERTICAL_SLAB);
    public static final RegistryObject<Item> BURNT_BUNDLED_CARDBOARD = registerBlockItem("burnt_bundled_cardboard",
            ModBlocks.BURNT_BUNDLED_CARDBOARD);
    public static final RegistryObject<Item> BURNT_BUNDLED_CARDBOARD_SLAB = registerBlockItem(
            "burnt_bundled_cardboard_slab", ModBlocks.BURNT_BUNDLED_CARDBOARD_SLAB);
    public static final RegistryObject<Item> BURNT_BUNDLED_CARDBOARD_STAIRS = registerBlockItem(
            "burnt_bundled_cardboard_stairs", ModBlocks.BURNT_BUNDLED_CARDBOARD_STAIRS);
    public static final RegistryObject<Item> BURNT_BUNDLED_CARDBOARD_WALL = registerBlockItem(
            "burnt_bundled_cardboard_wall", ModBlocks.BURNT_BUNDLED_CARDBOARD_WALL);
    public static final RegistryObject<Item> BURNT_BUNDLED_CARDBOARD_VERTICAL_SLAB = registerBlockItem(
            "burnt_bundled_cardboard_vertical_slab", ModBlocks.BURNT_BUNDLED_CARDBOARD_VERTICAL_SLAB);
    public static final RegistryObject<Item> BURNT_PRESSED_CARDBOARD = registerBlockItem("burnt_pressed_cardboard",
            ModBlocks.BURNT_PRESSED_CARDBOARD);
    public static final RegistryObject<Item> BURNT_PRESSED_CARDBOARD_SLAB = registerBlockItem(
            "burnt_pressed_cardboard_slab", ModBlocks.BURNT_PRESSED_CARDBOARD_SLAB);
    public static final RegistryObject<Item> BURNT_PRESSED_CARDBOARD_STAIRS = registerBlockItem(
            "burnt_pressed_cardboard_stairs", ModBlocks.BURNT_PRESSED_CARDBOARD_STAIRS);
    public static final RegistryObject<Item> BURNT_PRESSED_CARDBOARD_WALL = registerBlockItem(
            "burnt_pressed_cardboard_wall", ModBlocks.BURNT_PRESSED_CARDBOARD_WALL);
    public static final RegistryObject<Item> BURNT_PRESSED_CARDBOARD_VERTICAL_SLAB = registerBlockItem(
            "burnt_pressed_cardboard_vertical_slab", ModBlocks.BURNT_PRESSED_CARDBOARD_VERTICAL_SLAB);
    public static final RegistryObject<Item> BURNT_THICK_CARDBOARD_BLOCK = registerBlockItem(
            "burnt_thick_cardboard_block", ModBlocks.BURNT_THICK_CARDBOARD_BLOCK);
    public static final RegistryObject<Item> BURNT_THICK_CARDBOARD_SLAB = registerBlockItem(
            "burnt_thick_cardboard_slab", ModBlocks.BURNT_THICK_CARDBOARD_SLAB);
    public static final RegistryObject<Item> BURNT_THICK_CARDBOARD_STAIRS = registerBlockItem(
            "burnt_thick_cardboard_stairs", ModBlocks.BURNT_THICK_CARDBOARD_STAIRS);
    public static final RegistryObject<Item> BURNT_THICK_CARDBOARD_WALL = registerBlockItem(
            "burnt_thick_cardboard_wall", ModBlocks.BURNT_THICK_CARDBOARD_WALL);
    public static final RegistryObject<Item> BURNT_THICK_CARDBOARD_VERTICAL_SLAB = registerBlockItem(
            "burnt_thick_cardboard_vertical_slab", ModBlocks.BURNT_THICK_CARDBOARD_VERTICAL_SLAB);
    public static final RegistryObject<Item> OAK_SHELF = registerBlockItem("oak_shelf", ModBlocks.OAK_SHELF);
    public static final RegistryObject<Item> SPRUCE_SHELF = registerBlockItem("spruce_shelf", ModBlocks.SPRUCE_SHELF);
    public static final RegistryObject<Item> BIRCH_SHELF = registerBlockItem("birch_shelf", ModBlocks.BIRCH_SHELF);
    public static final RegistryObject<Item> JUNGLE_SHELF = registerBlockItem("jungle_shelf", ModBlocks.JUNGLE_SHELF);
    public static final RegistryObject<Item> ACACIA_SHELF = registerBlockItem("acacia_shelf", ModBlocks.ACACIA_SHELF);
    public static final RegistryObject<Item> DARK_OAK_SHELF = registerBlockItem("dark_oak_shelf",
            ModBlocks.DARK_OAK_SHELF);
    public static final RegistryObject<Item> MANGROVE_SHELF = registerBlockItem("mangrove_shelf",
            ModBlocks.MANGROVE_SHELF);
    public static final RegistryObject<Item> CHERRY_SHELF = registerBlockItem("cherry_shelf", ModBlocks.CHERRY_SHELF);
    public static final RegistryObject<Item> CRIMSON_SHELF = registerBlockItem("crimson_shelf",
            ModBlocks.CRIMSON_SHELF);
    public static final RegistryObject<Item> WARPED_SHELF = registerBlockItem("warped_shelf", ModBlocks.WARPED_SHELF);
    public static final RegistryObject<Item> BAMBOO_SHELF = registerBlockItem("bamboo_shelf", ModBlocks.BAMBOO_SHELF);
    public static final RegistryObject<Item> STRIPPED_BAMBOO_SHELF = registerBlockItem("stripped_bamboo_shelf",
            ModBlocks.STRIPPED_BAMBOO_SHELF);
    public static final RegistryObject<Item> PALE_OAK_SHELF = registerBlockItem("pale_oak_shelf",
            ModBlocks.PALE_OAK_SHELF);
    public static final RegistryObject<Item> POPLAR_SHELF = registerBlockItem("poplar_shelf", ModBlocks.POPLAR_SHELF);
    public static final RegistryObject<Item> GOLDEN_JAR = ITEMS.register(
            "golden_jar",
            () -> new GoldenJarItem(ModBlocks.GOLDEN_JAR.get(),
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> FESTIVE_STAR = ITEMS.register(
            "festive_star",
            () -> new FestiveStarItem(ModBlocks.FESTIVE_STAR.get(),
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).rarity(Rarity.RARE)));

    public static final RegistryObject<Item> EXPERIENCE_BUCKET = ITEMS.register(
            "experience_bucket",
            () -> new ExperienceBucketItem(
                    () -> ModFluids.EXPERIENCE_STILL.get(),
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(1)
                            .craftRemainder(Items.BUCKET)));

    public static final RegistryObject<Item> FESTIVE_GLINT_SHARD = ITEMS.register(
            "festive_glint_shard",
            () -> new FestiveGlintShardItem(
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).rarity(Rarity.RARE)));

    public static final RegistryObject<Item> GOLDEN_JAR_PATTERN = ITEMS.register(
            "golden_jar_pattern",
            () -> new PatternItem(
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(1).rarity(Rarity.UNCOMMON),
                    "tooltip.buildscape.golden_jar_pattern"));

    public static final RegistryObject<Item> FESTIVE_STAR_PATTERN = ITEMS.register(
            "festive_star_pattern",
            () -> new PatternItem(
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(1).rarity(Rarity.RARE),
                    "tooltip.buildscape.festive_star_pattern"));
    public static final RegistryObject<Item> BIG_WHITE_ORNAMENT = registerBlockItem("big_white_ornament",
            ModBlocks.BIG_WHITE_ORNAMENT);
    public static final RegistryObject<Item> BIG_ORANGE_ORNAMENT = registerBlockItem("big_orange_ornament",
            ModBlocks.BIG_ORANGE_ORNAMENT);
    public static final RegistryObject<Item> BIG_MAGENTA_ORNAMENT = registerBlockItem("big_magenta_ornament",
            ModBlocks.BIG_MAGENTA_ORNAMENT);
    public static final RegistryObject<Item> BIG_LIGHT_BLUE_ORNAMENT = registerBlockItem("big_light_blue_ornament",
            ModBlocks.BIG_LIGHT_BLUE_ORNAMENT);
    public static final RegistryObject<Item> BIG_YELLOW_ORNAMENT = registerBlockItem("big_yellow_ornament",
            ModBlocks.BIG_YELLOW_ORNAMENT);
    public static final RegistryObject<Item> BIG_LIME_ORNAMENT = registerBlockItem("big_lime_ornament",
            ModBlocks.BIG_LIME_ORNAMENT);
    public static final RegistryObject<Item> BIG_PINK_ORNAMENT = registerBlockItem("big_pink_ornament",
            ModBlocks.BIG_PINK_ORNAMENT);
    public static final RegistryObject<Item> BIG_GRAY_ORNAMENT = registerBlockItem("big_gray_ornament",
            ModBlocks.BIG_GRAY_ORNAMENT);
    public static final RegistryObject<Item> BIG_LIGHT_GRAY_ORNAMENT = registerBlockItem("big_light_gray_ornament",
            ModBlocks.BIG_LIGHT_GRAY_ORNAMENT);
    public static final RegistryObject<Item> BIG_CYAN_ORNAMENT = registerBlockItem("big_cyan_ornament",
            ModBlocks.BIG_CYAN_ORNAMENT);
    public static final RegistryObject<Item> BIG_PURPLE_ORNAMENT = registerBlockItem("big_purple_ornament",
            ModBlocks.BIG_PURPLE_ORNAMENT);
    public static final RegistryObject<Item> BIG_BLUE_ORNAMENT = registerBlockItem("big_blue_ornament",
            ModBlocks.BIG_BLUE_ORNAMENT);
    public static final RegistryObject<Item> BIG_BROWN_ORNAMENT = registerBlockItem("big_brown_ornament",
            ModBlocks.BIG_BROWN_ORNAMENT);
    public static final RegistryObject<Item> BIG_GREEN_ORNAMENT = registerBlockItem("big_green_ornament",
            ModBlocks.BIG_GREEN_ORNAMENT);
    public static final RegistryObject<Item> BIG_RED_ORNAMENT = registerBlockItem("big_red_ornament",
            ModBlocks.BIG_RED_ORNAMENT);
    public static final RegistryObject<Item> BIG_BLACK_ORNAMENT = registerBlockItem("big_black_ornament",
            ModBlocks.BIG_BLACK_ORNAMENT);
    public static final RegistryObject<Item> BIG_GLASS_ORNAMENT = registerBlockItem("big_glass_ornament",
            ModBlocks.BIG_GLASS_ORNAMENT);
    public static final RegistryObject<Item> BIG_TINTED_GLASS_ORNAMENT = registerBlockItem("big_tinted_glass_ornament",
            ModBlocks.BIG_TINTED_GLASS_ORNAMENT);

    public static final RegistryObject<Item> BIG_ORNAMENT_TEMPLATE = ITEMS.register(
            "big_ornament_template",
            () -> new BigOrnamentTemplateItem(
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> STRINGLIGHT_FRAME = ITEMS.register(
            "stringlight_frame",
            () -> new StringlightFrameItem(new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB)));

    public static final RegistryObject<Item> STRINGLIGHT_FRAME_PATTERN = ITEMS.register(
            "stringlight_frame_pattern",
            () -> new PatternItem(
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(1).rarity(Rarity.RARE),
                    "tooltip.buildscape.stringlight_frame_pattern"));

    public static final RegistryObject<Item> MUSIC_DISC_CELEBRATION = ITEMS.register(
            "music_disc_celebration",
            () -> new RecordItem(
                    14,
                    ModSounds.MUSIC_DISC_CELEBRATION,
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(1).rarity(Rarity.RARE)));

    public static final RegistryObject<Item> MUSIC_DISC_SNOWFALL = ITEMS.register(
            "music_disc_snowfall",
            () -> new RecordItem(
                    15,
                    ModSounds.MUSIC_DISC_SNOWFALL,
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(1).rarity(Rarity.RARE)));

    public static final RegistryObject<Item> MUSIC_DISC_BUILDER = ITEMS.register(
            "music_disc_builder",
            () -> new RecordItem(
                    13,
                    ModSounds.MUSIC_DISC_BUILDER,
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(1).rarity(Rarity.RARE)));
}
