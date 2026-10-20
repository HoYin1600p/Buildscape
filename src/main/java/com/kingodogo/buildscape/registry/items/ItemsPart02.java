package com.kingodogo.buildscape.registry.items;

import com.kingodogo.buildscape.feature.biome.BiomeBrushItem;
import com.kingodogo.buildscape.feature.firework.InfinitePhoenixFireworkStarItem;
import com.kingodogo.buildscape.feature.pipe.WrenchItem;
import com.kingodogo.buildscape.item.BuildersPouchItem;
import com.kingodogo.buildscape.item.ColoredItemFrameItem;
import com.kingodogo.buildscape.item.ConfettiItem;
import com.kingodogo.buildscape.item.HammerItem;
import com.kingodogo.buildscape.item.MangroveBoatItem;
import com.kingodogo.buildscape.item.MistBlockItem;
import com.kingodogo.buildscape.registry.ModBlocks;
import com.kingodogo.buildscape.registry.ModCreativeModeTab;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;

import static com.kingodogo.buildscape.registry.ModItems.ITEMS;
import static com.kingodogo.buildscape.registry.ModItems.createBlockItemProperties;
import static com.kingodogo.buildscape.registry.ModItems.registerBlockItem;

/** Part of the items registrations; see {@code ModItems}. */
public final class ItemsPart02 {

    private ItemsPart02() {
    }

    /** Forces this class to load so its registrations run in the order {@code ModItems} calls it. */
    public static void init() {
    }

    public static final RegistryObject<Item> LIME_ASHPEN_PLANKS = registerBlockItem("ashpen_lime_planks", ModBlocks.LIME_ASHPEN_PLANKS);
    public static final RegistryObject<Item> LIME_ASHPEN_STAIRS = registerBlockItem("ashpen_lime_stairs", ModBlocks.LIME_ASHPEN_STAIRS);
    public static final RegistryObject<Item> LIME_ASHPEN_SLAB = registerBlockItem("ashpen_lime_slab", ModBlocks.LIME_ASHPEN_SLAB);
    public static final RegistryObject<Item> LIME_ASHPEN_FENCE = registerBlockItem("ashpen_lime_fence", ModBlocks.LIME_ASHPEN_FENCE);
    public static final RegistryObject<Item> LIME_ASHPEN_FENCE_GATE = registerBlockItem("ashpen_lime_fence_gate", ModBlocks.LIME_ASHPEN_FENCE_GATE);
    public static final RegistryObject<Item> LIME_ASHPEN_PRESSURE_PLATE = registerBlockItem("ashpen_lime_pressure_plate", ModBlocks.LIME_ASHPEN_PRESSURE_PLATE);
    public static final RegistryObject<Item> LIME_ASHPEN_BUTTON = registerBlockItem("ashpen_lime_button", ModBlocks.LIME_ASHPEN_BUTTON);
    public static final RegistryObject<Item> MAGENTA_ASHPEN_PLANKS = registerBlockItem("ashpen_magenta_planks", ModBlocks.MAGENTA_ASHPEN_PLANKS);
    public static final RegistryObject<Item> MAGENTA_ASHPEN_STAIRS = registerBlockItem("ashpen_magenta_stairs", ModBlocks.MAGENTA_ASHPEN_STAIRS);
    public static final RegistryObject<Item> MAGENTA_ASHPEN_SLAB = registerBlockItem("ashpen_magenta_slab", ModBlocks.MAGENTA_ASHPEN_SLAB);
    public static final RegistryObject<Item> MAGENTA_ASHPEN_FENCE = registerBlockItem("ashpen_magenta_fence", ModBlocks.MAGENTA_ASHPEN_FENCE);
    public static final RegistryObject<Item> MAGENTA_ASHPEN_FENCE_GATE = registerBlockItem("ashpen_magenta_fence_gate", ModBlocks.MAGENTA_ASHPEN_FENCE_GATE);
    public static final RegistryObject<Item> MAGENTA_ASHPEN_PRESSURE_PLATE = registerBlockItem("ashpen_magenta_pressure_plate", ModBlocks.MAGENTA_ASHPEN_PRESSURE_PLATE);
    public static final RegistryObject<Item> MAGENTA_ASHPEN_BUTTON = registerBlockItem("ashpen_magenta_button", ModBlocks.MAGENTA_ASHPEN_BUTTON);
    public static final RegistryObject<Item> ORANGE_ASHPEN_PLANKS = registerBlockItem("ashpen_orange_planks", ModBlocks.ORANGE_ASHPEN_PLANKS);
    public static final RegistryObject<Item> ORANGE_ASHPEN_STAIRS = registerBlockItem("ashpen_orange_stairs", ModBlocks.ORANGE_ASHPEN_STAIRS);
    public static final RegistryObject<Item> ORANGE_ASHPEN_SLAB = registerBlockItem("ashpen_orange_slab", ModBlocks.ORANGE_ASHPEN_SLAB);
    public static final RegistryObject<Item> ORANGE_ASHPEN_FENCE = registerBlockItem("ashpen_orange_fence", ModBlocks.ORANGE_ASHPEN_FENCE);
    public static final RegistryObject<Item> ORANGE_ASHPEN_FENCE_GATE = registerBlockItem("ashpen_orange_fence_gate", ModBlocks.ORANGE_ASHPEN_FENCE_GATE);
    public static final RegistryObject<Item> ORANGE_ASHPEN_PRESSURE_PLATE = registerBlockItem("ashpen_orange_pressure_plate", ModBlocks.ORANGE_ASHPEN_PRESSURE_PLATE);
    public static final RegistryObject<Item> ORANGE_ASHPEN_BUTTON = registerBlockItem("ashpen_orange_button", ModBlocks.ORANGE_ASHPEN_BUTTON);
    public static final RegistryObject<Item> PINK_ASHPEN_PLANKS = registerBlockItem("ashpen_pink_planks", ModBlocks.PINK_ASHPEN_PLANKS);
    public static final RegistryObject<Item> PINK_ASHPEN_STAIRS = registerBlockItem("ashpen_pink_stairs", ModBlocks.PINK_ASHPEN_STAIRS);
    public static final RegistryObject<Item> PINK_ASHPEN_SLAB = registerBlockItem("ashpen_pink_slab", ModBlocks.PINK_ASHPEN_SLAB);
    public static final RegistryObject<Item> PINK_ASHPEN_FENCE = registerBlockItem("ashpen_pink_fence", ModBlocks.PINK_ASHPEN_FENCE);
    public static final RegistryObject<Item> PINK_ASHPEN_FENCE_GATE = registerBlockItem("ashpen_pink_fence_gate", ModBlocks.PINK_ASHPEN_FENCE_GATE);
    public static final RegistryObject<Item> PINK_ASHPEN_PRESSURE_PLATE = registerBlockItem("ashpen_pink_pressure_plate", ModBlocks.PINK_ASHPEN_PRESSURE_PLATE);
    public static final RegistryObject<Item> PINK_ASHPEN_BUTTON = registerBlockItem("ashpen_pink_button", ModBlocks.PINK_ASHPEN_BUTTON);
    public static final RegistryObject<Item> PURPLE_ASHPEN_PLANKS = registerBlockItem("ashpen_purple_planks", ModBlocks.PURPLE_ASHPEN_PLANKS);
    public static final RegistryObject<Item> PURPLE_ASHPEN_STAIRS = registerBlockItem("ashpen_purple_stairs", ModBlocks.PURPLE_ASHPEN_STAIRS);
    public static final RegistryObject<Item> PURPLE_ASHPEN_SLAB = registerBlockItem("ashpen_purple_slab", ModBlocks.PURPLE_ASHPEN_SLAB);
    public static final RegistryObject<Item> PURPLE_ASHPEN_FENCE = registerBlockItem("ashpen_purple_fence", ModBlocks.PURPLE_ASHPEN_FENCE);
    public static final RegistryObject<Item> PURPLE_ASHPEN_FENCE_GATE = registerBlockItem("ashpen_purple_fence_gate", ModBlocks.PURPLE_ASHPEN_FENCE_GATE);
    public static final RegistryObject<Item> PURPLE_ASHPEN_PRESSURE_PLATE = registerBlockItem("ashpen_purple_pressure_plate", ModBlocks.PURPLE_ASHPEN_PRESSURE_PLATE);
    public static final RegistryObject<Item> PURPLE_ASHPEN_BUTTON = registerBlockItem("ashpen_purple_button", ModBlocks.PURPLE_ASHPEN_BUTTON);
    public static final RegistryObject<Item> RED_ASHPEN_PLANKS = registerBlockItem("ashpen_red_planks", ModBlocks.RED_ASHPEN_PLANKS);
    public static final RegistryObject<Item> RED_ASHPEN_STAIRS = registerBlockItem("ashpen_red_stairs", ModBlocks.RED_ASHPEN_STAIRS);
    public static final RegistryObject<Item> RED_ASHPEN_SLAB = registerBlockItem("ashpen_red_slab", ModBlocks.RED_ASHPEN_SLAB);
    public static final RegistryObject<Item> RED_ASHPEN_FENCE = registerBlockItem("ashpen_red_fence", ModBlocks.RED_ASHPEN_FENCE);
    public static final RegistryObject<Item> RED_ASHPEN_FENCE_GATE = registerBlockItem("ashpen_red_fence_gate", ModBlocks.RED_ASHPEN_FENCE_GATE);
    public static final RegistryObject<Item> RED_ASHPEN_PRESSURE_PLATE = registerBlockItem("ashpen_red_pressure_plate", ModBlocks.RED_ASHPEN_PRESSURE_PLATE);
    public static final RegistryObject<Item> RED_ASHPEN_BUTTON = registerBlockItem("ashpen_red_button", ModBlocks.RED_ASHPEN_BUTTON);
    public static final RegistryObject<Item> YELLOW_ASHPEN_PLANKS = registerBlockItem("ashpen_yellow_planks", ModBlocks.YELLOW_ASHPEN_PLANKS);
    public static final RegistryObject<Item> YELLOW_ASHPEN_STAIRS = registerBlockItem("ashpen_yellow_stairs", ModBlocks.YELLOW_ASHPEN_STAIRS);
    public static final RegistryObject<Item> YELLOW_ASHPEN_SLAB = registerBlockItem("ashpen_yellow_slab", ModBlocks.YELLOW_ASHPEN_SLAB);
    public static final RegistryObject<Item> YELLOW_ASHPEN_FENCE = registerBlockItem("ashpen_yellow_fence", ModBlocks.YELLOW_ASHPEN_FENCE);
    public static final RegistryObject<Item> YELLOW_ASHPEN_FENCE_GATE = registerBlockItem("ashpen_yellow_fence_gate", ModBlocks.YELLOW_ASHPEN_FENCE_GATE);
    public static final RegistryObject<Item> YELLOW_ASHPEN_PRESSURE_PLATE = registerBlockItem("ashpen_yellow_pressure_plate", ModBlocks.YELLOW_ASHPEN_PRESSURE_PLATE);
    public static final RegistryObject<Item> YELLOW_ASHPEN_BUTTON = registerBlockItem("ashpen_yellow_button", ModBlocks.YELLOW_ASHPEN_BUTTON);
    public static final RegistryObject<Item> RED_ROSE_VINES = registerBlockItem("red_rose_vines", ModBlocks.RED_ROSE_VINES);
    public static final RegistryObject<Item> BLACK_ROSE_VINES = registerBlockItem("black_rose_vines", ModBlocks.BLACK_ROSE_VINES);
    public static final RegistryObject<Item> BLUE_ROSE_VINES = registerBlockItem("blue_rose_vines", ModBlocks.BLUE_ROSE_VINES);
    public static final RegistryObject<Item> WHITE_ROSE_VINES = registerBlockItem("white_rose_vines", ModBlocks.WHITE_ROSE_VINES);
    public static final RegistryObject<Item> RED_MONETS = registerBlockItem("red_monets", ModBlocks.RED_MONETS);
    public static final RegistryObject<Item> BLUE_MONETS = registerBlockItem("blue_monets", ModBlocks.BLUE_MONETS);
    public static final RegistryObject<Item> PURPLE_MONETS = registerBlockItem("purple_monets", ModBlocks.PURPLE_MONETS);
    public static final RegistryObject<Item> LIGHT_BLUE_MONETS = registerBlockItem("light_blue_monets", ModBlocks.LIGHT_BLUE_MONETS);
    public static final RegistryObject<Item> PINK_MONETS = registerBlockItem("pink_monets", ModBlocks.PINK_MONETS);
    public static final RegistryObject<Item> YELLOW_MONETS = registerBlockItem("yellow_monets", ModBlocks.YELLOW_MONETS);
    public static final RegistryObject<Item> CLOVER = registerBlockItem("clover", ModBlocks.CLOVER);
    public static final RegistryObject<Item> RED_PETAL = registerBlockItem("red_petal", ModBlocks.RED_PETAL);
    public static final RegistryObject<Item> BLUE_PETAL = registerBlockItem("blue_petal", ModBlocks.BLUE_PETAL);
    public static final RegistryObject<Item> ORANGE_PETAL = registerBlockItem("orange_petal", ModBlocks.ORANGE_PETAL);
    public static final RegistryObject<Item> PINK_PETAL = registerBlockItem("pink_petal", ModBlocks.PINK_PETAL);
    public static final RegistryObject<Item> PURPLE_PETAL = registerBlockItem("purple_petal", ModBlocks.PURPLE_PETAL);
    public static final RegistryObject<Item> RED_SPORE_BLOSSOM = registerBlockItem("red_spore_blossom", ModBlocks.RED_SPORE_BLOSSOM);
    public static final RegistryObject<Item> CYAN_SPORE_BLOSSOM = registerBlockItem("cyan_spore_blossom", ModBlocks.CYAN_SPORE_BLOSSOM);
    public static final RegistryObject<Item> BLUE_SPORE_BLOSSOM = registerBlockItem("blue_spore_blossom", ModBlocks.BLUE_SPORE_BLOSSOM);
    public static final RegistryObject<Item> PURPLE_SPORE_BLOSSOM = registerBlockItem("purple_spore_blossom", ModBlocks.PURPLE_SPORE_BLOSSOM);
    public static final RegistryObject<Item> ORANGE_SPORE_BLOSSOM = registerBlockItem("orange_spore_blossom", ModBlocks.ORANGE_SPORE_BLOSSOM);
    public static final RegistryObject<Item> BIG_CANDLE = registerBlockItem("big_candle", ModBlocks.BIG_CANDLE);
    public static final RegistryObject<Item> BIG_WHITE_CANDLE = registerBlockItem("big_white_candle", ModBlocks.BIG_WHITE_CANDLE);
    public static final RegistryObject<Item> BIG_ORANGE_CANDLE = registerBlockItem("big_orange_candle", ModBlocks.BIG_ORANGE_CANDLE);
    public static final RegistryObject<Item> BIG_MAGENTA_CANDLE = registerBlockItem("big_magenta_candle", ModBlocks.BIG_MAGENTA_CANDLE);
    public static final RegistryObject<Item> BIG_LIGHT_BLUE_CANDLE = registerBlockItem("big_light_blue_candle", ModBlocks.BIG_LIGHT_BLUE_CANDLE);
    public static final RegistryObject<Item> BIG_YELLOW_CANDLE = registerBlockItem("big_yellow_candle", ModBlocks.BIG_YELLOW_CANDLE);
    public static final RegistryObject<Item> BIG_LIME_CANDLE = registerBlockItem("big_lime_candle", ModBlocks.BIG_LIME_CANDLE);
    public static final RegistryObject<Item> BIG_PINK_CANDLE = registerBlockItem("big_pink_candle", ModBlocks.BIG_PINK_CANDLE);
    public static final RegistryObject<Item> BIG_GRAY_CANDLE = registerBlockItem("big_gray_candle", ModBlocks.BIG_GRAY_CANDLE);
    public static final RegistryObject<Item> BIG_LIGHT_GRAY_CANDLE = registerBlockItem("big_light_gray_candle", ModBlocks.BIG_LIGHT_GRAY_CANDLE);
    public static final RegistryObject<Item> BIG_CYAN_CANDLE = registerBlockItem("big_cyan_candle", ModBlocks.BIG_CYAN_CANDLE);
    public static final RegistryObject<Item> BIG_PURPLE_CANDLE = registerBlockItem("big_purple_candle", ModBlocks.BIG_PURPLE_CANDLE);
    public static final RegistryObject<Item> BIG_BLUE_CANDLE = registerBlockItem("big_blue_candle", ModBlocks.BIG_BLUE_CANDLE);
    public static final RegistryObject<Item> BIG_BROWN_CANDLE = registerBlockItem("big_brown_candle", ModBlocks.BIG_BROWN_CANDLE);
    public static final RegistryObject<Item> BIG_GREEN_CANDLE = registerBlockItem("big_green_candle", ModBlocks.BIG_GREEN_CANDLE);
    public static final RegistryObject<Item> BIG_RED_CANDLE = registerBlockItem("big_red_candle", ModBlocks.BIG_RED_CANDLE);
    public static final RegistryObject<Item> BIG_BLACK_CANDLE = registerBlockItem("big_black_candle", ModBlocks.BIG_BLACK_CANDLE);
    public static final RegistryObject<Item> BIG_AMETHYST_CANDLE = registerBlockItem("big_amethyst_candle", ModBlocks.BIG_AMETHYST_CANDLE);
    public static final RegistryObject<Item> BIG_SCULK_CANDLE = registerBlockItem("big_sculk_candle", ModBlocks.BIG_SCULK_CANDLE);
    public static final RegistryObject<Item> WHITE_ORNAMENT = registerBlockItem("white_ornament", ModBlocks.WHITE_ORNAMENT);
    public static final RegistryObject<Item> ORANGE_ORNAMENT = registerBlockItem("orange_ornament", ModBlocks.ORANGE_ORNAMENT);
    public static final RegistryObject<Item> MAGENTA_ORNAMENT = registerBlockItem("magenta_ornament", ModBlocks.MAGENTA_ORNAMENT);
    public static final RegistryObject<Item> LIGHT_BLUE_ORNAMENT = registerBlockItem("light_blue_ornament", ModBlocks.LIGHT_BLUE_ORNAMENT);
    public static final RegistryObject<Item> YELLOW_ORNAMENT = registerBlockItem("yellow_ornament", ModBlocks.YELLOW_ORNAMENT);
    public static final RegistryObject<Item> LIME_ORNAMENT = registerBlockItem("lime_ornament", ModBlocks.LIME_ORNAMENT);
    public static final RegistryObject<Item> PINK_ORNAMENT = registerBlockItem("pink_ornament", ModBlocks.PINK_ORNAMENT);
    public static final RegistryObject<Item> GRAY_ORNAMENT = registerBlockItem("gray_ornament", ModBlocks.GRAY_ORNAMENT);
    public static final RegistryObject<Item> LIGHT_GRAY_ORNAMENT = registerBlockItem("light_gray_ornament", ModBlocks.LIGHT_GRAY_ORNAMENT);
    public static final RegistryObject<Item> CYAN_ORNAMENT = registerBlockItem("cyan_ornament", ModBlocks.CYAN_ORNAMENT);
    public static final RegistryObject<Item> PURPLE_ORNAMENT = registerBlockItem("purple_ornament", ModBlocks.PURPLE_ORNAMENT);
    public static final RegistryObject<Item> BLUE_ORNAMENT = registerBlockItem("blue_ornament", ModBlocks.BLUE_ORNAMENT);
    public static final RegistryObject<Item> BROWN_ORNAMENT = registerBlockItem("brown_ornament", ModBlocks.BROWN_ORNAMENT);
    public static final RegistryObject<Item> GREEN_ORNAMENT = registerBlockItem("green_ornament", ModBlocks.GREEN_ORNAMENT);
    public static final RegistryObject<Item> RED_ORNAMENT = registerBlockItem("red_ornament", ModBlocks.RED_ORNAMENT);
    public static final RegistryObject<Item> BLACK_ORNAMENT = registerBlockItem("black_ornament", ModBlocks.BLACK_ORNAMENT);
    public static final RegistryObject<Item> GLASS_ORNAMENT = registerBlockItem("glass_ornament", ModBlocks.GLASS_ORNAMENT);
    public static final RegistryObject<Item> TINTED_GLASS_ORNAMENT = registerBlockItem("tinted_glass_ornament", ModBlocks.TINTED_GLASS_ORNAMENT);
    public static final RegistryObject<Item> WHITE_STRING_LIGHT = registerBlockItem("white_string_light", ModBlocks.WHITE_STRING_LIGHT);
    public static final RegistryObject<Item> ORANGE_STRING_LIGHT = registerBlockItem("orange_string_light", ModBlocks.ORANGE_STRING_LIGHT);
    public static final RegistryObject<Item> MAGENTA_STRING_LIGHT = registerBlockItem("magenta_string_light", ModBlocks.MAGENTA_STRING_LIGHT);
    public static final RegistryObject<Item> LIGHT_BLUE_STRING_LIGHT = registerBlockItem("light_blue_string_light", ModBlocks.LIGHT_BLUE_STRING_LIGHT);
    public static final RegistryObject<Item> YELLOW_STRING_LIGHT = registerBlockItem("yellow_string_light", ModBlocks.YELLOW_STRING_LIGHT);
    public static final RegistryObject<Item> LIME_STRING_LIGHT = registerBlockItem("lime_string_light", ModBlocks.LIME_STRING_LIGHT);
    public static final RegistryObject<Item> PINK_STRING_LIGHT = registerBlockItem("pink_string_light", ModBlocks.PINK_STRING_LIGHT);
    public static final RegistryObject<Item> GRAY_STRING_LIGHT = registerBlockItem("gray_string_light", ModBlocks.GRAY_STRING_LIGHT);
    public static final RegistryObject<Item> LIGHT_GRAY_STRING_LIGHT = registerBlockItem("light_gray_string_light", ModBlocks.LIGHT_GRAY_STRING_LIGHT);
    public static final RegistryObject<Item> CYAN_STRING_LIGHT = registerBlockItem("cyan_string_light", ModBlocks.CYAN_STRING_LIGHT);
    public static final RegistryObject<Item> PURPLE_STRING_LIGHT = registerBlockItem("purple_string_light", ModBlocks.PURPLE_STRING_LIGHT);
    public static final RegistryObject<Item> BLUE_STRING_LIGHT = registerBlockItem("blue_string_light", ModBlocks.BLUE_STRING_LIGHT);
    public static final RegistryObject<Item> BROWN_STRING_LIGHT = registerBlockItem("brown_string_light", ModBlocks.BROWN_STRING_LIGHT);
    public static final RegistryObject<Item> GREEN_STRING_LIGHT = registerBlockItem("green_string_light", ModBlocks.GREEN_STRING_LIGHT);
    public static final RegistryObject<Item> RED_STRING_LIGHT = registerBlockItem("red_string_light", ModBlocks.RED_STRING_LIGHT);
    public static final RegistryObject<Item> BLACK_STRING_LIGHT = registerBlockItem("black_string_light", ModBlocks.BLACK_STRING_LIGHT);
    public static final RegistryObject<Item> MULTICOLOR_STRING_LIGHT = registerBlockItem("multicolor_string_light", ModBlocks.MULTICOLOR_STRING_LIGHT);
    public static final RegistryObject<Item> WHITE_STAR = registerBlockItem("white_star", ModBlocks.WHITE_STAR);
    public static final RegistryObject<Item> ORANGE_STAR = registerBlockItem("orange_star", ModBlocks.ORANGE_STAR);
    public static final RegistryObject<Item> MAGENTA_STAR = registerBlockItem("magenta_star", ModBlocks.MAGENTA_STAR);
    public static final RegistryObject<Item> LIGHT_BLUE_STAR = registerBlockItem("light_blue_star", ModBlocks.LIGHT_BLUE_STAR);
    public static final RegistryObject<Item> YELLOW_STAR = registerBlockItem("yellow_star", ModBlocks.YELLOW_STAR);
    public static final RegistryObject<Item> LIME_STAR = registerBlockItem("lime_star", ModBlocks.LIME_STAR);
    public static final RegistryObject<Item> PINK_STAR = registerBlockItem("pink_star", ModBlocks.PINK_STAR);
    public static final RegistryObject<Item> GRAY_STAR = registerBlockItem("gray_star", ModBlocks.GRAY_STAR);
    public static final RegistryObject<Item> LIGHT_GRAY_STAR = registerBlockItem("light_gray_star", ModBlocks.LIGHT_GRAY_STAR);
    public static final RegistryObject<Item> CYAN_STAR = registerBlockItem("cyan_star", ModBlocks.CYAN_STAR);
    public static final RegistryObject<Item> PURPLE_STAR = registerBlockItem("purple_star", ModBlocks.PURPLE_STAR);
    public static final RegistryObject<Item> BLUE_STAR = registerBlockItem("blue_star", ModBlocks.BLUE_STAR);
    public static final RegistryObject<Item> BROWN_STAR = registerBlockItem("brown_star", ModBlocks.BROWN_STAR);
    public static final RegistryObject<Item> GREEN_STAR = registerBlockItem("green_star", ModBlocks.GREEN_STAR);
    public static final RegistryObject<Item> RED_STAR = registerBlockItem("red_star", ModBlocks.RED_STAR);
    public static final RegistryObject<Item> BLACK_STAR = registerBlockItem("black_star", ModBlocks.BLACK_STAR);
    public static final RegistryObject<Item> GLOW_STAR = registerBlockItem("glow_star", ModBlocks.GLOW_STAR);

    public static final RegistryObject<Item> SNOWY_LEAVES = ITEMS.register(
            "snowy_leaves",
            () ->
                    new BlockItem(ModBlocks.SNOWY_LEAVES.get(), new Item.Properties())
    );
    public static final RegistryObject<Item> SNOWY_OAK_LEAVES = registerBlockItem("snowy_oak_leaves", ModBlocks.SNOWY_OAK_LEAVES);
    public static final RegistryObject<Item> SNOWY_SPRUCE_LEAVES = registerBlockItem("snowy_spruce_leaves", ModBlocks.SNOWY_SPRUCE_LEAVES);
    public static final RegistryObject<Item> SNOWY_BIRCH_LEAVES = registerBlockItem("snowy_birch_leaves", ModBlocks.SNOWY_BIRCH_LEAVES);
    public static final RegistryObject<Item> SNOWY_JUNGLE_LEAVES = registerBlockItem("snowy_jungle_leaves", ModBlocks.SNOWY_JUNGLE_LEAVES);
    public static final RegistryObject<Item> SNOWY_ACACIA_LEAVES = registerBlockItem("snowy_acacia_leaves", ModBlocks.SNOWY_ACACIA_LEAVES);
    public static final RegistryObject<Item> SNOWY_DARK_OAK_LEAVES = registerBlockItem("snowy_dark_oak_leaves", ModBlocks.SNOWY_DARK_OAK_LEAVES);
    public static final RegistryObject<Item> SNOWY_MANGROVE_LEAVES = registerBlockItem("snowy_mangrove_leaves", ModBlocks.SNOWY_MANGROVE_LEAVES);
    public static final RegistryObject<Item> SNOWY_AZALEA_LEAVES = registerBlockItem("snowy_azalea_leaves", ModBlocks.SNOWY_AZALEA_LEAVES);
    public static final RegistryObject<Item> SNOWY_FLOWERING_AZALEA_LEAVES = registerBlockItem("snowy_flowering_azalea_leaves", ModBlocks.SNOWY_FLOWERING_AZALEA_LEAVES);
    public static final RegistryObject<Item> SNOWY_CHERRY_LEAVES = registerBlockItem("snowy_cherry_leaves", ModBlocks.SNOWY_CHERRY_LEAVES);
    public static final RegistryObject<Item> SNOWY_PALE_OAK_LEAVES = registerBlockItem("snowy_pale_oak_leaves", ModBlocks.SNOWY_PALE_OAK_LEAVES);
    public static final RegistryObject<Item> SNOWY_ORANGE_POPLAR_LEAVES = registerBlockItem("snowy_orange_poplar_leaves", ModBlocks.SNOWY_ORANGE_POPLAR_LEAVES);
    public static final RegistryObject<Item> SNOWY_RED_POPLAR_LEAVES = registerBlockItem("snowy_red_poplar_leaves", ModBlocks.SNOWY_RED_POPLAR_LEAVES);
    public static final RegistryObject<Item> SNOWY_YELLOW_POPLAR_LEAVES = registerBlockItem("snowy_yellow_poplar_leaves", ModBlocks.SNOWY_YELLOW_POPLAR_LEAVES);
    public static final RegistryObject<Item> SNOWY_SHORT_GRASS = registerBlockItem("snowy_short_grass", ModBlocks.SNOWY_SHORT_GRASS);
    public static final RegistryObject<Item> SNOWY_TALL_GRASS = registerBlockItem("snowy_tall_grass", ModBlocks.SNOWY_TALL_GRASS);
    public static final RegistryObject<Item> SNOWY_FERN = registerBlockItem("snowy_fern", ModBlocks.SNOWY_FERN);
    public static final RegistryObject<Item> SNOWY_LARGE_FERN = registerBlockItem("snowy_large_fern", ModBlocks.SNOWY_LARGE_FERN);
    public static final RegistryObject<Item> SNOWY_BUSH = registerBlockItem("snowy_bush", ModBlocks.SNOWY_BUSH);
    public static final RegistryObject<Item> SNOW_BRICKS = registerBlockItem("snow_bricks", ModBlocks.SNOW_BRICKS);
    public static final RegistryObject<Item> SNOW_BRICKS_STAIRS = registerBlockItem("snow_bricks_stairs", ModBlocks.SNOW_BRICKS_STAIRS);
    public static final RegistryObject<Item> SNOW_BRICKS_SLAB = registerBlockItem("snow_bricks_slab", ModBlocks.SNOW_BRICKS_SLAB);
    public static final RegistryObject<Item> SNOW_BRICKS_WALL = registerBlockItem("snow_bricks_wall", ModBlocks.SNOW_BRICKS_WALL);
    public static final RegistryObject<Item> SNOWY_GRASS_BLOCK = registerBlockItem("snowy_grass_block", ModBlocks.SNOWY_GRASS_BLOCK);
    public static final RegistryObject<Item> SNOWY_GRASS_BLOCK_STAIRS = registerBlockItem("snowy_grass_block_stairs", ModBlocks.SNOWY_GRASS_BLOCK_STAIRS);
    public static final RegistryObject<Item> SNOWY_GRASS_BLOCK_SLAB = registerBlockItem("snowy_grass_block_slab", ModBlocks.SNOWY_GRASS_BLOCK_SLAB);
    public static final RegistryObject<Item> ICICLE = registerBlockItem("icicle", ModBlocks.ICICLE);
    public static final RegistryObject<Item> ICICLE_BLOCK = registerBlockItem("icicle_block", ModBlocks.ICICLE_BLOCK);
    public static final RegistryObject<Item> PACKED_ICICLE_BLOCK = registerBlockItem("packed_icicle_block", ModBlocks.PACKED_ICICLE_BLOCK);
    public static final RegistryObject<Item> ICE_CRYSTAL = registerBlockItem("ice_crystal", ModBlocks.ICE_CRYSTAL);

    public static final RegistryObject<Item> COPPER_NUGGET = ITEMS.register(
            "copper_nugget",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB))
    );

    public static final RegistryObject<Item> STEEL_INGOT = ITEMS.register(
            "steel_ingot",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB))
    );
    public static final RegistryObject<Item> STEEL_NUGGET = ITEMS.register(
            "steel_nugget",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB))
    );

    public static final RegistryObject<Item> WRENCH = ITEMS.register(
            "wrench",
            () -> new WrenchItem(new Item.Properties().stacksTo(1).tab(ModCreativeModeTab.BUILDSCAPE_TAB))
    );

    public static final RegistryObject<Item> IRON_HAMMER = ITEMS.register(
            "iron_hammer",
            () -> new HammerItem(HammerItem.HammerTier.IRON, new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB))
    );

    public static final RegistryObject<Item> DIAMOND_HAMMER = ITEMS.register(
            "diamond_hammer",
            () -> new HammerItem(HammerItem.HammerTier.DIAMOND, new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB))
    );

    public static final RegistryObject<Item> NETHERITE_HAMMER = ITEMS.register(
            "netherite_hammer",
            () -> new HammerItem(HammerItem.HammerTier.NETHERITE, new Item.Properties().fireResistant().tab(ModCreativeModeTab.BUILDSCAPE_TAB))
    );

    public static final RegistryObject<Item> COPPER_BIOME_BRUSH = ITEMS.register(
            "copper_biome_brush",
            () -> new BiomeBrushItem(BiomeBrushItem.BiomeBrushTier.COPPER, new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB))
    );

    public static final RegistryObject<Item> DIAMOND_BIOME_BRUSH = ITEMS.register(
            "diamond_biome_brush",
            () -> new BiomeBrushItem(BiomeBrushItem.BiomeBrushTier.DIAMOND, new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB))
    );

    public static final RegistryObject<Item> NETHERITE_BIOME_BRUSH = ITEMS.register(
            "netherite_biome_brush",
            () -> new BiomeBrushItem(BiomeBrushItem.BiomeBrushTier.NETHERITE, new Item.Properties().fireResistant().tab(ModCreativeModeTab.BUILDSCAPE_TAB))
    );

    public static final RegistryObject<Item> BUILDERS_WORKBENCH = ITEMS.register(
            "builders_workbench",
            () -> new BlockItem(
                    ModBlocks.BUILDERS_WORKBENCH.get(),
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(64)
            )
    );

    public static final RegistryObject<Item> BUILDERS_POUCH = ITEMS.register(
            "builders_pouch",
            () -> new BuildersPouchItem(new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB))
    );
    public static final RegistryObject<Item> SNOW_STAIRS = registerBlockItem("snow_stairs", ModBlocks.SNOW_STAIRS);
    public static final RegistryObject<Item> SNOW_SLAB = registerBlockItem("snow_slab", ModBlocks.SNOW_SLAB);
    public static final RegistryObject<Item> MANGROVE_LOG = registerBlockItem("mangrove_log", ModBlocks.MANGROVE_LOG);
    public static final RegistryObject<Item> STRIPPED_MANGROVE_LOG = registerBlockItem("stripped_mangrove_log", ModBlocks.STRIPPED_MANGROVE_LOG);
    public static final RegistryObject<Item> MANGROVE_WOOD = registerBlockItem("mangrove_wood", ModBlocks.MANGROVE_WOOD);
    public static final RegistryObject<Item> STRIPPED_MANGROVE_WOOD = registerBlockItem("stripped_mangrove_wood", ModBlocks.STRIPPED_MANGROVE_WOOD);
    public static final RegistryObject<Item> MANGROVE_LEAVES = registerBlockItem("mangrove_leaves", ModBlocks.MANGROVE_LEAVES);
    public static final RegistryObject<Item> MANGROVE_ROOTS = registerBlockItem("mangrove_roots", ModBlocks.MANGROVE_ROOTS);
    public static final RegistryObject<Item> MUDDY_MANGROVE_ROOTS = registerBlockItem("muddy_mangrove_roots", ModBlocks.MUDDY_MANGROVE_ROOTS);
    public static final RegistryObject<Item> MANGROVE_PROPAGULE = registerBlockItem("mangrove_propagule", ModBlocks.MANGROVE_PROPAGULE);
    public static final RegistryObject<Item> MANGROVE_PLANKS = registerBlockItem("mangrove_planks", ModBlocks.MANGROVE_PLANKS);
    public static final RegistryObject<Item> MANGROVE_SLAB = registerBlockItem("mangrove_slab", ModBlocks.MANGROVE_SLAB);
    public static final RegistryObject<Item> MANGROVE_STAIRS = registerBlockItem("mangrove_stairs", ModBlocks.MANGROVE_STAIRS);
    public static final RegistryObject<Item> MANGROVE_FENCE = registerBlockItem("mangrove_fence", ModBlocks.MANGROVE_FENCE);
    public static final RegistryObject<Item> MANGROVE_FENCE_GATE = registerBlockItem("mangrove_fence_gate", ModBlocks.MANGROVE_FENCE_GATE);
    public static final RegistryObject<Item> MANGROVE_DOOR = registerBlockItem("mangrove_door", ModBlocks.MANGROVE_DOOR);
    public static final RegistryObject<Item> MANGROVE_TRAPDOOR = registerBlockItem("mangrove_trapdoor", ModBlocks.MANGROVE_TRAPDOOR);
    public static final RegistryObject<Item> MANGROVE_BUTTON = registerBlockItem("mangrove_button", ModBlocks.MANGROVE_BUTTON);
    public static final RegistryObject<Item> MANGROVE_PRESSURE_PLATE = registerBlockItem("mangrove_pressure_plate", ModBlocks.MANGROVE_PRESSURE_PLATE);

    public static final RegistryObject<Item> MANGROVE_SIGN = ITEMS.register(
            "mangrove_sign",
            () ->
                    new SignItem(
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(16),
                    ModBlocks.MANGROVE_SIGN.get(),
                            ModBlocks.MANGROVE_WALL_SIGN.get()
                    )
    );

    public static final RegistryObject<Item> BAMBOO_SIGN = ITEMS.register(
            "bamboo_sign",
            () ->
                    new SignItem(
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(16),
                    ModBlocks.BAMBOO_SIGN.get(),
                            ModBlocks.BAMBOO_WALL_SIGN.get()
                    )
    );

    public static final RegistryObject<Item> MANGROVE_BOAT = ITEMS.register(
            "mangrove_boat",
            () ->
                    new MangroveBoatItem(
                            new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(1)
                    )
    );
    public static final RegistryObject<Item> FESTIVE_LAMP = registerBlockItem("festive_lamp", ModBlocks.FESTIVE_LAMP);

    public static final RegistryObject<Item> FROST_ROSE = ITEMS.register(
            "frost_rose",
            () -> new BlockItem(ModBlocks.FROST_ROSE.get(), createBlockItemProperties()) {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                    tooltip.add(new TextComponent("Obtained when a Wither kills a Snow Golem")
                            .withStyle(ChatFormatting.DARK_AQUA));
                }
            }
    );
    public static final RegistryObject<Item> GLOW_LIGHTS = registerBlockItem("glow_lights", ModBlocks.GLOW_LIGHTS);
    public static final RegistryObject<Item> MULTICOLOR_GLOW_LIGHTS = registerBlockItem("multicolor_glow_lights", ModBlocks.MULTICOLOR_GLOW_LIGHTS);

    public static final RegistryObject<Item> CONFETTI_ITEM = ITEMS.register(
            "confetti",
            () -> new ConfettiItem(new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(64))
    );

    public static final RegistryObject<Item> INFINITE_PHOENIX_FIREWORK_STAR = ITEMS.register(
            "infinite_phoenix_firework_star",
            () -> new InfinitePhoenixFireworkStarItem(new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(1))
    );
    public static final RegistryObject<Item> STEEL_BLOCK = registerBlockItem("steel_block", ModBlocks.STEEL_BLOCK);
    public static final RegistryObject<Item> STEEL_PIPE = registerBlockItem("steel_pipe", ModBlocks.STEEL_PIPE);
    public static final RegistryObject<Item> HOLLOW_STEEL_PIPE = registerBlockItem("hollow_steel_pipe", ModBlocks.HOLLOW_STEEL_PIPE);
    public static final RegistryObject<Item> SCRAPED_STEEL = registerBlockItem("scraped_steel", ModBlocks.SCRAPED_STEEL);
    public static final RegistryObject<Item> RUSTIC_SCRAPED_STEEL = registerBlockItem("rustic_scraped_steel", ModBlocks.RUSTIC_SCRAPED_STEEL);
    public static final RegistryObject<Item> STACKED_STEEL = registerBlockItem("stacked_steel", ModBlocks.STACKED_STEEL);
    public static final RegistryObject<Item> STEEL_PANELS = registerBlockItem("steel_panels", ModBlocks.STEEL_PANELS);
    public static final RegistryObject<Item> CROSSED_STEEL_PANELS = registerBlockItem("crossed_steel_panels", ModBlocks.CROSSED_STEEL_PANELS);
    public static final RegistryObject<Item> STEEL_MESH_BLOCK = registerBlockItem("steel_mesh_block", ModBlocks.STEEL_MESH_BLOCK);
    public static final RegistryObject<Item> STEEL_BOLTS = registerBlockItem("steel_bolts", ModBlocks.STEEL_BOLTS);

    public static final RegistryObject<Item> STEEL_DOOR = ITEMS.register(
            "steel_door",
            () -> new DoubleHighBlockItem(ModBlocks.STEEL_DOOR.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> STEEL_TRAPDOOR = registerBlockItem("steel_trapdoor", ModBlocks.STEEL_TRAPDOOR);
    public static final RegistryObject<Item> STEEL_BUTTON = registerBlockItem("steel_button", ModBlocks.STEEL_BUTTON);
    public static final RegistryObject<Item> STEEL_PRESSURE_PLATE = registerBlockItem("steel_pressure_plate", ModBlocks.STEEL_PRESSURE_PLATE);
    public static final RegistryObject<Item> SCRAPED_STEEL_SLAB = registerBlockItem("scraped_steel_slab", ModBlocks.SCRAPED_STEEL_SLAB);
    public static final RegistryObject<Item> SCRAPED_STEEL_VERTICAL_SLAB = registerBlockItem("scraped_steel_vertical_slab", ModBlocks.SCRAPED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> RUSTIC_SCRAPED_STEEL_SLAB = registerBlockItem("rustic_scraped_steel_slab", ModBlocks.RUSTIC_SCRAPED_STEEL_SLAB);
    public static final RegistryObject<Item> RUSTIC_SCRAPED_STEEL_VERTICAL_SLAB = registerBlockItem("rustic_scraped_steel_vertical_slab", ModBlocks.RUSTIC_SCRAPED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> STACKED_STEEL_SLAB = registerBlockItem("stacked_steel_slab", ModBlocks.STACKED_STEEL_SLAB);
    public static final RegistryObject<Item> STACKED_STEEL_VERTICAL_SLAB = registerBlockItem("stacked_steel_vertical_slab", ModBlocks.STACKED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> STACKED_STEEL_STAIRS = registerBlockItem("stacked_steel_stairs", ModBlocks.STACKED_STEEL_STAIRS);
    public static final RegistryObject<Item> STEEL_PANELS_SLAB = registerBlockItem("steel_panels_slab", ModBlocks.STEEL_PANELS_SLAB);
    public static final RegistryObject<Item> STEEL_PANELS_VERTICAL_SLAB = registerBlockItem("steel_panels_vertical_slab", ModBlocks.STEEL_PANELS_VERTICAL_SLAB);
    public static final RegistryObject<Item> STEEL_PANELS_STAIRS = registerBlockItem("steel_panels_stairs", ModBlocks.STEEL_PANELS_STAIRS);
    public static final RegistryObject<Item> CROSSED_STEEL_PANELS_SLAB = registerBlockItem("crossed_steel_panels_slab", ModBlocks.CROSSED_STEEL_PANELS_SLAB);
    public static final RegistryObject<Item> CROSSED_STEEL_PANELS_VERTICAL_SLAB = registerBlockItem("crossed_steel_panels_vertical_slab", ModBlocks.CROSSED_STEEL_PANELS_VERTICAL_SLAB);
    public static final RegistryObject<Item> STEEL_MESH_BLOCK_SLAB = registerBlockItem("steel_mesh_block_slab", ModBlocks.STEEL_MESH_BLOCK_SLAB);
    public static final RegistryObject<Item> STEEL_MESH_BLOCK_VERTICAL_SLAB = registerBlockItem("steel_mesh_block_vertical_slab", ModBlocks.STEEL_MESH_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> STEEL_MESH_BLOCK_STAIRS = registerBlockItem("steel_mesh_block_stairs", ModBlocks.STEEL_MESH_BLOCK_STAIRS);
    public static final RegistryObject<Item> PRESSED_STEEL = registerBlockItem("pressed_steel", ModBlocks.PRESSED_STEEL);
    public static final RegistryObject<Item> CUT_STEEL = registerBlockItem("cut_steel", ModBlocks.CUT_STEEL);
    public static final RegistryObject<Item> POLISHED_STEEL = registerBlockItem("polished_steel", ModBlocks.POLISHED_STEEL);
    public static final RegistryObject<Item> FACTORY_STEEL_PANEL = registerBlockItem("factory_steel_panel", ModBlocks.FACTORY_STEEL_PANEL);
    public static final RegistryObject<Item> STEEL_CASING = registerBlockItem("steel_casing", ModBlocks.STEEL_CASING);
    public static final RegistryObject<Item> STEEL_TRIM = registerBlockItem("steel_trim", ModBlocks.STEEL_TRIM);
    public static final RegistryObject<Item> STEEL_PILLAR = registerBlockItem("steel_pillar", ModBlocks.STEEL_PILLAR);
    public static final RegistryObject<Item> BOLTED_STEEL_PILLAR = registerBlockItem("bolted_steel_pillar", ModBlocks.BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> STEEL_GRATE = registerBlockItem("steel_grate", ModBlocks.STEEL_GRATE);
    public static final RegistryObject<Item> STEEL_FAN = registerBlockItem("steel_fan", ModBlocks.STEEL_FAN);
    public static final RegistryObject<Item> STEEL_BLOCK_STAIRS = registerBlockItem("steel_block_stairs", ModBlocks.STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> STEEL_BLOCK_SLAB = registerBlockItem("steel_block_slab", ModBlocks.STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> STEEL_BLOCK_WALL = registerBlockItem("steel_block_wall", ModBlocks.STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> POLISHED_STEEL_STAIRS = registerBlockItem("polished_steel_stairs", ModBlocks.POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> POLISHED_STEEL_SLAB = registerBlockItem("polished_steel_slab", ModBlocks.POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> POLISHED_STEEL_WALL = registerBlockItem("polished_steel_wall", ModBlocks.POLISHED_STEEL_WALL);
    public static final RegistryObject<Item> PRESSED_STEEL_STAIRS = registerBlockItem("pressed_steel_stairs", ModBlocks.PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> PRESSED_STEEL_SLAB = registerBlockItem("pressed_steel_slab", ModBlocks.PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> PRESSED_STEEL_WALL = registerBlockItem("pressed_steel_wall", ModBlocks.PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> CUT_STEEL_STAIRS = registerBlockItem("cut_steel_stairs", ModBlocks.CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> CUT_STEEL_SLAB = registerBlockItem("cut_steel_slab", ModBlocks.CUT_STEEL_SLAB);
    public static final RegistryObject<Item> CUT_STEEL_WALL = registerBlockItem("cut_steel_wall", ModBlocks.CUT_STEEL_WALL);
    public static final RegistryObject<Item> CAUTION_BLACK = registerBlockItem("caution_black", ModBlocks.CAUTION_BLACK);
    public static final RegistryObject<Item> CAUTION_BLUE = registerBlockItem("caution_blue", ModBlocks.CAUTION_BLUE);
    public static final RegistryObject<Item> CAUTION_FACTORY = registerBlockItem("caution_factory", ModBlocks.CAUTION_FACTORY);
    public static final RegistryObject<Item> CAUTION_RED = registerBlockItem("caution_red", ModBlocks.CAUTION_RED);
    public static final RegistryObject<Item> FRAMED_CAUTION = registerBlockItem("framed_caution", ModBlocks.FRAMED_CAUTION);
    public static final RegistryObject<Item> CAUTION_WHITE = registerBlockItem("caution_white", ModBlocks.CAUTION_WHITE);
    public static final RegistryObject<Item> CAUTION_LIME = registerBlockItem("caution_lime", ModBlocks.CAUTION_LIME);
    public static final RegistryObject<Item> CAUTION_PINK = registerBlockItem("caution_pink", ModBlocks.CAUTION_PINK);
    public static final RegistryObject<Item> CAUTION_YELLOW = registerBlockItem("caution_yellow", ModBlocks.CAUTION_YELLOW);
    public static final RegistryObject<Item> CAUTION_CANDY = registerBlockItem("caution_candy", ModBlocks.CAUTION_CANDY);
    public static final RegistryObject<Item> CAUTION_COTTONCANDY = registerBlockItem("caution_cottoncandy", ModBlocks.CAUTION_COTTONCANDY);
    public static final RegistryObject<Item> CAUTION_MINTCANDY = registerBlockItem("caution_mintcandy", ModBlocks.CAUTION_MINTCANDY);
    public static final RegistryObject<Item> CAUTION_CITRUSCANDY = registerBlockItem("caution_citruscandy", ModBlocks.CAUTION_CITRUSCANDY);
    public static final RegistryObject<Item> CAUTION_BLACK_SLAB = registerBlockItem("caution_black_slab", ModBlocks.CAUTION_BLACK_SLAB);
    public static final RegistryObject<Item> CAUTION_BLUE_SLAB = registerBlockItem("caution_blue_slab", ModBlocks.CAUTION_BLUE_SLAB);
    public static final RegistryObject<Item> CAUTION_FACTORY_SLAB = registerBlockItem("caution_factory_slab", ModBlocks.CAUTION_FACTORY_SLAB);
    public static final RegistryObject<Item> CAUTION_RED_SLAB = registerBlockItem("caution_red_slab", ModBlocks.CAUTION_RED_SLAB);
    public static final RegistryObject<Item> FRAMED_CAUTION_SLAB = registerBlockItem("framed_caution_slab", ModBlocks.FRAMED_CAUTION_SLAB);
    public static final RegistryObject<Item> CAUTION_WHITE_SLAB = registerBlockItem("caution_white_slab", ModBlocks.CAUTION_WHITE_SLAB);
    public static final RegistryObject<Item> CAUTION_LIME_SLAB = registerBlockItem("caution_lime_slab", ModBlocks.CAUTION_LIME_SLAB);
    public static final RegistryObject<Item> CAUTION_PINK_SLAB = registerBlockItem("caution_pink_slab", ModBlocks.CAUTION_PINK_SLAB);
    public static final RegistryObject<Item> CAUTION_YELLOW_SLAB = registerBlockItem("caution_yellow_slab", ModBlocks.CAUTION_YELLOW_SLAB);
    public static final RegistryObject<Item> CAUTION_CANDY_SLAB = registerBlockItem("caution_candy_slab", ModBlocks.CAUTION_CANDY_SLAB);
    public static final RegistryObject<Item> CAUTION_COTTONCANDY_SLAB = registerBlockItem("caution_cottoncandy_slab", ModBlocks.CAUTION_COTTONCANDY_SLAB);
    public static final RegistryObject<Item> CAUTION_MINTCANDY_SLAB = registerBlockItem("caution_mintcandy_slab", ModBlocks.CAUTION_MINTCANDY_SLAB);
    public static final RegistryObject<Item> CAUTION_CITRUSCANDY_SLAB = registerBlockItem("caution_citruscandy_slab", ModBlocks.CAUTION_CITRUSCANDY_SLAB);
    public static final RegistryObject<Item> CAUTION_BLACK_STAIRS = registerBlockItem("caution_black_stairs", ModBlocks.CAUTION_BLACK_STAIRS);
    public static final RegistryObject<Item> CAUTION_BLUE_STAIRS = registerBlockItem("caution_blue_stairs", ModBlocks.CAUTION_BLUE_STAIRS);
    public static final RegistryObject<Item> CAUTION_FACTORY_STAIRS = registerBlockItem("caution_factory_stairs", ModBlocks.CAUTION_FACTORY_STAIRS);
    public static final RegistryObject<Item> CAUTION_RED_STAIRS = registerBlockItem("caution_red_stairs", ModBlocks.CAUTION_RED_STAIRS);
    public static final RegistryObject<Item> FRAMED_CAUTION_STAIRS = registerBlockItem("framed_caution_stairs", ModBlocks.FRAMED_CAUTION_STAIRS);
    public static final RegistryObject<Item> CAUTION_WHITE_STAIRS = registerBlockItem("caution_white_stairs", ModBlocks.CAUTION_WHITE_STAIRS);
    public static final RegistryObject<Item> CAUTION_LIME_STAIRS = registerBlockItem("caution_lime_stairs", ModBlocks.CAUTION_LIME_STAIRS);
    public static final RegistryObject<Item> CAUTION_PINK_STAIRS = registerBlockItem("caution_pink_stairs", ModBlocks.CAUTION_PINK_STAIRS);
    public static final RegistryObject<Item> CAUTION_YELLOW_STAIRS = registerBlockItem("caution_yellow_stairs", ModBlocks.CAUTION_YELLOW_STAIRS);
    public static final RegistryObject<Item> CAUTION_CANDY_STAIRS = registerBlockItem("caution_candy_stairs", ModBlocks.CAUTION_CANDY_STAIRS);
    public static final RegistryObject<Item> CAUTION_COTTONCANDY_STAIRS = registerBlockItem("caution_cottoncandy_stairs", ModBlocks.CAUTION_COTTONCANDY_STAIRS);
    public static final RegistryObject<Item> CAUTION_MINTCANDY_STAIRS = registerBlockItem("caution_mintcandy_stairs", ModBlocks.CAUTION_MINTCANDY_STAIRS);
    public static final RegistryObject<Item> CAUTION_CITRUSCANDY_STAIRS = registerBlockItem("caution_citruscandy_stairs", ModBlocks.CAUTION_CITRUSCANDY_STAIRS);
    public static final RegistryObject<Item> FACTORY_WHITE_GLASS = registerBlockItem("factory_white_glass", ModBlocks.FACTORY_WHITE_GLASS);
    public static final RegistryObject<Item> FACTORY_LIGHT_GRAY_GLASS = registerBlockItem("factory_light_gray_glass", ModBlocks.FACTORY_LIGHT_GRAY_GLASS);
    public static final RegistryObject<Item> FACTORY_GRAY_GLASS = registerBlockItem("factory_gray_glass", ModBlocks.FACTORY_GRAY_GLASS);
    public static final RegistryObject<Item> FACTORY_BLACK_GLASS = registerBlockItem("factory_black_glass", ModBlocks.FACTORY_BLACK_GLASS);
    public static final RegistryObject<Item> FACTORY_BROWN_GLASS = registerBlockItem("factory_brown_glass", ModBlocks.FACTORY_BROWN_GLASS);
    public static final RegistryObject<Item> FACTORY_RED_GLASS = registerBlockItem("factory_red_glass", ModBlocks.FACTORY_RED_GLASS);
    public static final RegistryObject<Item> FACTORY_ORANGE_GLASS = registerBlockItem("factory_orange_glass", ModBlocks.FACTORY_ORANGE_GLASS);
    public static final RegistryObject<Item> FACTORY_YELLOW_GLASS = registerBlockItem("factory_yellow_glass", ModBlocks.FACTORY_YELLOW_GLASS);
    public static final RegistryObject<Item> FACTORY_LIME_GLASS = registerBlockItem("factory_lime_glass", ModBlocks.FACTORY_LIME_GLASS);
    public static final RegistryObject<Item> FACTORY_GREEN_GLASS = registerBlockItem("factory_green_glass", ModBlocks.FACTORY_GREEN_GLASS);
    public static final RegistryObject<Item> FACTORY_CYAN_GLASS = registerBlockItem("factory_cyan_glass", ModBlocks.FACTORY_CYAN_GLASS);
    public static final RegistryObject<Item> FACTORY_LIGHT_BLUE_GLASS = registerBlockItem("factory_light_blue_glass", ModBlocks.FACTORY_LIGHT_BLUE_GLASS);
    public static final RegistryObject<Item> FACTORY_BLUE_GLASS = registerBlockItem("factory_blue_glass", ModBlocks.FACTORY_BLUE_GLASS);
    public static final RegistryObject<Item> FACTORY_PURPLE_GLASS = registerBlockItem("factory_purple_glass", ModBlocks.FACTORY_PURPLE_GLASS);
    public static final RegistryObject<Item> FACTORY_MAGENTA_GLASS = registerBlockItem("factory_magenta_glass", ModBlocks.FACTORY_MAGENTA_GLASS);
    public static final RegistryObject<Item> FACTORY_PINK_GLASS = registerBlockItem("factory_pink_glass", ModBlocks.FACTORY_PINK_GLASS);
    public static final RegistryObject<Item> FACTORY_WHITE_GLASS_PANE = registerBlockItem("factory_white_glass_pane", ModBlocks.FACTORY_WHITE_GLASS_PANE);
    public static final RegistryObject<Item> FACTORY_LIGHT_GRAY_GLASS_PANE = registerBlockItem("factory_light_gray_glass_pane", ModBlocks.FACTORY_LIGHT_GRAY_GLASS_PANE);
    public static final RegistryObject<Item> FACTORY_GRAY_GLASS_PANE = registerBlockItem("factory_gray_glass_pane", ModBlocks.FACTORY_GRAY_GLASS_PANE);
    public static final RegistryObject<Item> FACTORY_BLACK_GLASS_PANE = registerBlockItem("factory_black_glass_pane", ModBlocks.FACTORY_BLACK_GLASS_PANE);
    public static final RegistryObject<Item> FACTORY_BROWN_GLASS_PANE = registerBlockItem("factory_brown_glass_pane", ModBlocks.FACTORY_BROWN_GLASS_PANE);
    public static final RegistryObject<Item> FACTORY_RED_GLASS_PANE = registerBlockItem("factory_red_glass_pane", ModBlocks.FACTORY_RED_GLASS_PANE);
    public static final RegistryObject<Item> FACTORY_ORANGE_GLASS_PANE = registerBlockItem("factory_orange_glass_pane", ModBlocks.FACTORY_ORANGE_GLASS_PANE);
    public static final RegistryObject<Item> FACTORY_YELLOW_GLASS_PANE = registerBlockItem("factory_yellow_glass_pane", ModBlocks.FACTORY_YELLOW_GLASS_PANE);
    public static final RegistryObject<Item> FACTORY_LIME_GLASS_PANE = registerBlockItem("factory_lime_glass_pane", ModBlocks.FACTORY_LIME_GLASS_PANE);
    public static final RegistryObject<Item> FACTORY_GREEN_GLASS_PANE = registerBlockItem("factory_green_glass_pane", ModBlocks.FACTORY_GREEN_GLASS_PANE);
    public static final RegistryObject<Item> FACTORY_CYAN_GLASS_PANE = registerBlockItem("factory_cyan_glass_pane", ModBlocks.FACTORY_CYAN_GLASS_PANE);
    public static final RegistryObject<Item> FACTORY_LIGHT_BLUE_GLASS_PANE = registerBlockItem("factory_light_blue_glass_pane", ModBlocks.FACTORY_LIGHT_BLUE_GLASS_PANE);
    public static final RegistryObject<Item> FACTORY_BLUE_GLASS_PANE = registerBlockItem("factory_blue_glass_pane", ModBlocks.FACTORY_BLUE_GLASS_PANE);
    public static final RegistryObject<Item> FACTORY_PURPLE_GLASS_PANE = registerBlockItem("factory_purple_glass_pane", ModBlocks.FACTORY_PURPLE_GLASS_PANE);
    public static final RegistryObject<Item> FACTORY_MAGENTA_GLASS_PANE = registerBlockItem("factory_magenta_glass_pane", ModBlocks.FACTORY_MAGENTA_GLASS_PANE);
    public static final RegistryObject<Item> FACTORY_PINK_GLASS_PANE = registerBlockItem("factory_pink_glass_pane", ModBlocks.FACTORY_PINK_GLASS_PANE);
    public static final RegistryObject<Item> WHITE_STAINED_BRICKS = registerBlockItem("white_stained_bricks", ModBlocks.WHITE_STAINED_BRICKS);
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICKS = registerBlockItem("light_gray_stained_bricks", ModBlocks.LIGHT_GRAY_STAINED_BRICKS);
    public static final RegistryObject<Item> GRAY_STAINED_BRICKS = registerBlockItem("gray_stained_bricks", ModBlocks.GRAY_STAINED_BRICKS);
    public static final RegistryObject<Item> BLACK_STAINED_BRICKS = registerBlockItem("black_stained_bricks", ModBlocks.BLACK_STAINED_BRICKS);
    public static final RegistryObject<Item> BROWN_STAINED_BRICKS = registerBlockItem("brown_stained_bricks", ModBlocks.BROWN_STAINED_BRICKS);
    public static final RegistryObject<Item> RED_STAINED_BRICKS = registerBlockItem("red_stained_bricks", ModBlocks.RED_STAINED_BRICKS);
    public static final RegistryObject<Item> ORANGE_STAINED_BRICKS = registerBlockItem("orange_stained_bricks", ModBlocks.ORANGE_STAINED_BRICKS);
    public static final RegistryObject<Item> YELLOW_STAINED_BRICKS = registerBlockItem("yellow_stained_bricks", ModBlocks.YELLOW_STAINED_BRICKS);
    public static final RegistryObject<Item> LIME_STAINED_BRICKS = registerBlockItem("lime_stained_bricks", ModBlocks.LIME_STAINED_BRICKS);
    public static final RegistryObject<Item> GREEN_STAINED_BRICKS = registerBlockItem("green_stained_bricks", ModBlocks.GREEN_STAINED_BRICKS);
    public static final RegistryObject<Item> CYAN_STAINED_BRICKS = registerBlockItem("cyan_stained_bricks", ModBlocks.CYAN_STAINED_BRICKS);
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICKS = registerBlockItem("light_blue_stained_bricks", ModBlocks.LIGHT_BLUE_STAINED_BRICKS);
    public static final RegistryObject<Item> BLUE_STAINED_BRICKS = registerBlockItem("blue_stained_bricks", ModBlocks.BLUE_STAINED_BRICKS);
    public static final RegistryObject<Item> PURPLE_STAINED_BRICKS = registerBlockItem("purple_stained_bricks", ModBlocks.PURPLE_STAINED_BRICKS);
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICKS = registerBlockItem("magenta_stained_bricks", ModBlocks.MAGENTA_STAINED_BRICKS);
    public static final RegistryObject<Item> PINK_STAINED_BRICKS = registerBlockItem("pink_stained_bricks", ModBlocks.PINK_STAINED_BRICKS);
    public static final RegistryObject<Item> WHITE_STAINED_BRICK_TILES = registerBlockItem("white_stained_brick_tiles", ModBlocks.WHITE_STAINED_BRICK_TILES);
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICK_TILES = registerBlockItem("light_gray_stained_brick_tiles", ModBlocks.LIGHT_GRAY_STAINED_BRICK_TILES);
    public static final RegistryObject<Item> GRAY_STAINED_BRICK_TILES = registerBlockItem("gray_stained_brick_tiles", ModBlocks.GRAY_STAINED_BRICK_TILES);
    public static final RegistryObject<Item> BLACK_STAINED_BRICK_TILES = registerBlockItem("black_stained_brick_tiles", ModBlocks.BLACK_STAINED_BRICK_TILES);
    public static final RegistryObject<Item> BROWN_STAINED_BRICK_TILES = registerBlockItem("brown_stained_brick_tiles", ModBlocks.BROWN_STAINED_BRICK_TILES);
    public static final RegistryObject<Item> RED_STAINED_BRICK_TILES = registerBlockItem("red_stained_brick_tiles", ModBlocks.RED_STAINED_BRICK_TILES);
    public static final RegistryObject<Item> ORANGE_STAINED_BRICK_TILES = registerBlockItem("orange_stained_brick_tiles", ModBlocks.ORANGE_STAINED_BRICK_TILES);
    public static final RegistryObject<Item> YELLOW_STAINED_BRICK_TILES = registerBlockItem("yellow_stained_brick_tiles", ModBlocks.YELLOW_STAINED_BRICK_TILES);
    public static final RegistryObject<Item> LIME_STAINED_BRICK_TILES = registerBlockItem("lime_stained_brick_tiles", ModBlocks.LIME_STAINED_BRICK_TILES);
    public static final RegistryObject<Item> GREEN_STAINED_BRICK_TILES = registerBlockItem("green_stained_brick_tiles", ModBlocks.GREEN_STAINED_BRICK_TILES);
    public static final RegistryObject<Item> CYAN_STAINED_BRICK_TILES = registerBlockItem("cyan_stained_brick_tiles", ModBlocks.CYAN_STAINED_BRICK_TILES);
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICK_TILES = registerBlockItem("light_blue_stained_brick_tiles", ModBlocks.LIGHT_BLUE_STAINED_BRICK_TILES);
    public static final RegistryObject<Item> BLUE_STAINED_BRICK_TILES = registerBlockItem("blue_stained_brick_tiles", ModBlocks.BLUE_STAINED_BRICK_TILES);
    public static final RegistryObject<Item> PURPLE_STAINED_BRICK_TILES = registerBlockItem("purple_stained_brick_tiles", ModBlocks.PURPLE_STAINED_BRICK_TILES);
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICK_TILES = registerBlockItem("magenta_stained_brick_tiles", ModBlocks.MAGENTA_STAINED_BRICK_TILES);
    public static final RegistryObject<Item> PINK_STAINED_BRICK_TILES = registerBlockItem("pink_stained_brick_tiles", ModBlocks.PINK_STAINED_BRICK_TILES);
    public static final RegistryObject<Item> WHITE_STAINED_BRICK_TILES_SLAB = registerBlockItem("white_stained_brick_tiles_slab", ModBlocks.WHITE_STAINED_BRICK_TILES_SLAB);
    public static final RegistryObject<Item> WHITE_STAINED_BRICK_TILES_STAIRS = registerBlockItem("white_stained_brick_tiles_stairs", ModBlocks.WHITE_STAINED_BRICK_TILES_STAIRS);
    public static final RegistryObject<Item> WHITE_STAINED_BRICK_TILES_WALL = registerBlockItem("white_stained_brick_tiles_wall", ModBlocks.WHITE_STAINED_BRICK_TILES_WALL);
    public static final RegistryObject<Item> WHITE_STAINED_BRICK_TILES_VERTICAL_SLAB = registerBlockItem("white_stained_brick_tiles_vertical_slab", ModBlocks.WHITE_STAINED_BRICK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICK_TILES_SLAB = registerBlockItem("light_gray_stained_brick_tiles_slab", ModBlocks.LIGHT_GRAY_STAINED_BRICK_TILES_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICK_TILES_STAIRS = registerBlockItem("light_gray_stained_brick_tiles_stairs", ModBlocks.LIGHT_GRAY_STAINED_BRICK_TILES_STAIRS);
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICK_TILES_WALL = registerBlockItem("light_gray_stained_brick_tiles_wall", ModBlocks.LIGHT_GRAY_STAINED_BRICK_TILES_WALL);
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICK_TILES_VERTICAL_SLAB = registerBlockItem("light_gray_stained_brick_tiles_vertical_slab", ModBlocks.LIGHT_GRAY_STAINED_BRICK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> GRAY_STAINED_BRICK_TILES_SLAB = registerBlockItem("gray_stained_brick_tiles_slab", ModBlocks.GRAY_STAINED_BRICK_TILES_SLAB);
    public static final RegistryObject<Item> GRAY_STAINED_BRICK_TILES_STAIRS = registerBlockItem("gray_stained_brick_tiles_stairs", ModBlocks.GRAY_STAINED_BRICK_TILES_STAIRS);
    public static final RegistryObject<Item> GRAY_STAINED_BRICK_TILES_WALL = registerBlockItem("gray_stained_brick_tiles_wall", ModBlocks.GRAY_STAINED_BRICK_TILES_WALL);
    public static final RegistryObject<Item> GRAY_STAINED_BRICK_TILES_VERTICAL_SLAB = registerBlockItem("gray_stained_brick_tiles_vertical_slab", ModBlocks.GRAY_STAINED_BRICK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLACK_STAINED_BRICK_TILES_SLAB = registerBlockItem("black_stained_brick_tiles_slab", ModBlocks.BLACK_STAINED_BRICK_TILES_SLAB);
    public static final RegistryObject<Item> BLACK_STAINED_BRICK_TILES_STAIRS = registerBlockItem("black_stained_brick_tiles_stairs", ModBlocks.BLACK_STAINED_BRICK_TILES_STAIRS);
    public static final RegistryObject<Item> BLACK_STAINED_BRICK_TILES_WALL = registerBlockItem("black_stained_brick_tiles_wall", ModBlocks.BLACK_STAINED_BRICK_TILES_WALL);
    public static final RegistryObject<Item> BLACK_STAINED_BRICK_TILES_VERTICAL_SLAB = registerBlockItem("black_stained_brick_tiles_vertical_slab", ModBlocks.BLACK_STAINED_BRICK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> BROWN_STAINED_BRICK_TILES_SLAB = registerBlockItem("brown_stained_brick_tiles_slab", ModBlocks.BROWN_STAINED_BRICK_TILES_SLAB);
    public static final RegistryObject<Item> BROWN_STAINED_BRICK_TILES_STAIRS = registerBlockItem("brown_stained_brick_tiles_stairs", ModBlocks.BROWN_STAINED_BRICK_TILES_STAIRS);
    public static final RegistryObject<Item> BROWN_STAINED_BRICK_TILES_WALL = registerBlockItem("brown_stained_brick_tiles_wall", ModBlocks.BROWN_STAINED_BRICK_TILES_WALL);
    public static final RegistryObject<Item> BROWN_STAINED_BRICK_TILES_VERTICAL_SLAB = registerBlockItem("brown_stained_brick_tiles_vertical_slab", ModBlocks.BROWN_STAINED_BRICK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_STAINED_BRICK_TILES_SLAB = registerBlockItem("red_stained_brick_tiles_slab", ModBlocks.RED_STAINED_BRICK_TILES_SLAB);
    public static final RegistryObject<Item> RED_STAINED_BRICK_TILES_STAIRS = registerBlockItem("red_stained_brick_tiles_stairs", ModBlocks.RED_STAINED_BRICK_TILES_STAIRS);
    public static final RegistryObject<Item> RED_STAINED_BRICK_TILES_WALL = registerBlockItem("red_stained_brick_tiles_wall", ModBlocks.RED_STAINED_BRICK_TILES_WALL);
    public static final RegistryObject<Item> RED_STAINED_BRICK_TILES_VERTICAL_SLAB = registerBlockItem("red_stained_brick_tiles_vertical_slab", ModBlocks.RED_STAINED_BRICK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> ORANGE_STAINED_BRICK_TILES_SLAB = registerBlockItem("orange_stained_brick_tiles_slab", ModBlocks.ORANGE_STAINED_BRICK_TILES_SLAB);
    public static final RegistryObject<Item> ORANGE_STAINED_BRICK_TILES_STAIRS = registerBlockItem("orange_stained_brick_tiles_stairs", ModBlocks.ORANGE_STAINED_BRICK_TILES_STAIRS);
    public static final RegistryObject<Item> ORANGE_STAINED_BRICK_TILES_WALL = registerBlockItem("orange_stained_brick_tiles_wall", ModBlocks.ORANGE_STAINED_BRICK_TILES_WALL);
    public static final RegistryObject<Item> ORANGE_STAINED_BRICK_TILES_VERTICAL_SLAB = registerBlockItem("orange_stained_brick_tiles_vertical_slab", ModBlocks.ORANGE_STAINED_BRICK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> YELLOW_STAINED_BRICK_TILES_SLAB = registerBlockItem("yellow_stained_brick_tiles_slab", ModBlocks.YELLOW_STAINED_BRICK_TILES_SLAB);
    public static final RegistryObject<Item> YELLOW_STAINED_BRICK_TILES_STAIRS = registerBlockItem("yellow_stained_brick_tiles_stairs", ModBlocks.YELLOW_STAINED_BRICK_TILES_STAIRS);
    public static final RegistryObject<Item> YELLOW_STAINED_BRICK_TILES_WALL = registerBlockItem("yellow_stained_brick_tiles_wall", ModBlocks.YELLOW_STAINED_BRICK_TILES_WALL);
    public static final RegistryObject<Item> YELLOW_STAINED_BRICK_TILES_VERTICAL_SLAB = registerBlockItem("yellow_stained_brick_tiles_vertical_slab", ModBlocks.YELLOW_STAINED_BRICK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIME_STAINED_BRICK_TILES_SLAB = registerBlockItem("lime_stained_brick_tiles_slab", ModBlocks.LIME_STAINED_BRICK_TILES_SLAB);
    public static final RegistryObject<Item> LIME_STAINED_BRICK_TILES_STAIRS = registerBlockItem("lime_stained_brick_tiles_stairs", ModBlocks.LIME_STAINED_BRICK_TILES_STAIRS);
    public static final RegistryObject<Item> LIME_STAINED_BRICK_TILES_WALL = registerBlockItem("lime_stained_brick_tiles_wall", ModBlocks.LIME_STAINED_BRICK_TILES_WALL);
    public static final RegistryObject<Item> LIME_STAINED_BRICK_TILES_VERTICAL_SLAB = registerBlockItem("lime_stained_brick_tiles_vertical_slab", ModBlocks.LIME_STAINED_BRICK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> GREEN_STAINED_BRICK_TILES_SLAB = registerBlockItem("green_stained_brick_tiles_slab", ModBlocks.GREEN_STAINED_BRICK_TILES_SLAB);
    public static final RegistryObject<Item> GREEN_STAINED_BRICK_TILES_STAIRS = registerBlockItem("green_stained_brick_tiles_stairs", ModBlocks.GREEN_STAINED_BRICK_TILES_STAIRS);
    public static final RegistryObject<Item> GREEN_STAINED_BRICK_TILES_WALL = registerBlockItem("green_stained_brick_tiles_wall", ModBlocks.GREEN_STAINED_BRICK_TILES_WALL);
    public static final RegistryObject<Item> GREEN_STAINED_BRICK_TILES_VERTICAL_SLAB = registerBlockItem("green_stained_brick_tiles_vertical_slab", ModBlocks.GREEN_STAINED_BRICK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> CYAN_STAINED_BRICK_TILES_SLAB = registerBlockItem("cyan_stained_brick_tiles_slab", ModBlocks.CYAN_STAINED_BRICK_TILES_SLAB);
    public static final RegistryObject<Item> CYAN_STAINED_BRICK_TILES_STAIRS = registerBlockItem("cyan_stained_brick_tiles_stairs", ModBlocks.CYAN_STAINED_BRICK_TILES_STAIRS);
    public static final RegistryObject<Item> CYAN_STAINED_BRICK_TILES_WALL = registerBlockItem("cyan_stained_brick_tiles_wall", ModBlocks.CYAN_STAINED_BRICK_TILES_WALL);
    public static final RegistryObject<Item> CYAN_STAINED_BRICK_TILES_VERTICAL_SLAB = registerBlockItem("cyan_stained_brick_tiles_vertical_slab", ModBlocks.CYAN_STAINED_BRICK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICK_TILES_SLAB = registerBlockItem("light_blue_stained_brick_tiles_slab", ModBlocks.LIGHT_BLUE_STAINED_BRICK_TILES_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICK_TILES_STAIRS = registerBlockItem("light_blue_stained_brick_tiles_stairs", ModBlocks.LIGHT_BLUE_STAINED_BRICK_TILES_STAIRS);
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICK_TILES_WALL = registerBlockItem("light_blue_stained_brick_tiles_wall", ModBlocks.LIGHT_BLUE_STAINED_BRICK_TILES_WALL);
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICK_TILES_VERTICAL_SLAB = registerBlockItem("light_blue_stained_brick_tiles_vertical_slab", ModBlocks.LIGHT_BLUE_STAINED_BRICK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLUE_STAINED_BRICK_TILES_SLAB = registerBlockItem("blue_stained_brick_tiles_slab", ModBlocks.BLUE_STAINED_BRICK_TILES_SLAB);
    public static final RegistryObject<Item> BLUE_STAINED_BRICK_TILES_STAIRS = registerBlockItem("blue_stained_brick_tiles_stairs", ModBlocks.BLUE_STAINED_BRICK_TILES_STAIRS);
    public static final RegistryObject<Item> BLUE_STAINED_BRICK_TILES_WALL = registerBlockItem("blue_stained_brick_tiles_wall", ModBlocks.BLUE_STAINED_BRICK_TILES_WALL);
    public static final RegistryObject<Item> BLUE_STAINED_BRICK_TILES_VERTICAL_SLAB = registerBlockItem("blue_stained_brick_tiles_vertical_slab", ModBlocks.BLUE_STAINED_BRICK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> PURPLE_STAINED_BRICK_TILES_SLAB = registerBlockItem("purple_stained_brick_tiles_slab", ModBlocks.PURPLE_STAINED_BRICK_TILES_SLAB);
    public static final RegistryObject<Item> PURPLE_STAINED_BRICK_TILES_STAIRS = registerBlockItem("purple_stained_brick_tiles_stairs", ModBlocks.PURPLE_STAINED_BRICK_TILES_STAIRS);
    public static final RegistryObject<Item> PURPLE_STAINED_BRICK_TILES_WALL = registerBlockItem("purple_stained_brick_tiles_wall", ModBlocks.PURPLE_STAINED_BRICK_TILES_WALL);
    public static final RegistryObject<Item> PURPLE_STAINED_BRICK_TILES_VERTICAL_SLAB = registerBlockItem("purple_stained_brick_tiles_vertical_slab", ModBlocks.PURPLE_STAINED_BRICK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICK_TILES_SLAB = registerBlockItem("magenta_stained_brick_tiles_slab", ModBlocks.MAGENTA_STAINED_BRICK_TILES_SLAB);
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICK_TILES_STAIRS = registerBlockItem("magenta_stained_brick_tiles_stairs", ModBlocks.MAGENTA_STAINED_BRICK_TILES_STAIRS);
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICK_TILES_WALL = registerBlockItem("magenta_stained_brick_tiles_wall", ModBlocks.MAGENTA_STAINED_BRICK_TILES_WALL);
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICK_TILES_VERTICAL_SLAB = registerBlockItem("magenta_stained_brick_tiles_vertical_slab", ModBlocks.MAGENTA_STAINED_BRICK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> PINK_STAINED_BRICK_TILES_SLAB = registerBlockItem("pink_stained_brick_tiles_slab", ModBlocks.PINK_STAINED_BRICK_TILES_SLAB);
    public static final RegistryObject<Item> PINK_STAINED_BRICK_TILES_STAIRS = registerBlockItem("pink_stained_brick_tiles_stairs", ModBlocks.PINK_STAINED_BRICK_TILES_STAIRS);
    public static final RegistryObject<Item> PINK_STAINED_BRICK_TILES_WALL = registerBlockItem("pink_stained_brick_tiles_wall", ModBlocks.PINK_STAINED_BRICK_TILES_WALL);
    public static final RegistryObject<Item> PINK_STAINED_BRICK_TILES_VERTICAL_SLAB = registerBlockItem("pink_stained_brick_tiles_vertical_slab", ModBlocks.PINK_STAINED_BRICK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> WHITE_STAINED_BRICKS_SLAB = registerBlockItem("white_stained_bricks_slab", ModBlocks.WHITE_STAINED_BRICKS_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICKS_SLAB = registerBlockItem("light_gray_stained_bricks_slab", ModBlocks.LIGHT_GRAY_STAINED_BRICKS_SLAB);
    public static final RegistryObject<Item> GRAY_STAINED_BRICKS_SLAB = registerBlockItem("gray_stained_bricks_slab", ModBlocks.GRAY_STAINED_BRICKS_SLAB);
    public static final RegistryObject<Item> BLACK_STAINED_BRICKS_SLAB = registerBlockItem("black_stained_bricks_slab", ModBlocks.BLACK_STAINED_BRICKS_SLAB);
    public static final RegistryObject<Item> BROWN_STAINED_BRICKS_SLAB = registerBlockItem("brown_stained_bricks_slab", ModBlocks.BROWN_STAINED_BRICKS_SLAB);
    public static final RegistryObject<Item> RED_STAINED_BRICKS_SLAB = registerBlockItem("red_stained_bricks_slab", ModBlocks.RED_STAINED_BRICKS_SLAB);
    public static final RegistryObject<Item> ORANGE_STAINED_BRICKS_SLAB = registerBlockItem("orange_stained_bricks_slab", ModBlocks.ORANGE_STAINED_BRICKS_SLAB);
    public static final RegistryObject<Item> YELLOW_STAINED_BRICKS_SLAB = registerBlockItem("yellow_stained_bricks_slab", ModBlocks.YELLOW_STAINED_BRICKS_SLAB);
    public static final RegistryObject<Item> LIME_STAINED_BRICKS_SLAB = registerBlockItem("lime_stained_bricks_slab", ModBlocks.LIME_STAINED_BRICKS_SLAB);
    public static final RegistryObject<Item> GREEN_STAINED_BRICKS_SLAB = registerBlockItem("green_stained_bricks_slab", ModBlocks.GREEN_STAINED_BRICKS_SLAB);
    public static final RegistryObject<Item> CYAN_STAINED_BRICKS_SLAB = registerBlockItem("cyan_stained_bricks_slab", ModBlocks.CYAN_STAINED_BRICKS_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICKS_SLAB = registerBlockItem("light_blue_stained_bricks_slab", ModBlocks.LIGHT_BLUE_STAINED_BRICKS_SLAB);
    public static final RegistryObject<Item> BLUE_STAINED_BRICKS_SLAB = registerBlockItem("blue_stained_bricks_slab", ModBlocks.BLUE_STAINED_BRICKS_SLAB);
    public static final RegistryObject<Item> PURPLE_STAINED_BRICKS_SLAB = registerBlockItem("purple_stained_bricks_slab", ModBlocks.PURPLE_STAINED_BRICKS_SLAB);
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICKS_SLAB = registerBlockItem("magenta_stained_bricks_slab", ModBlocks.MAGENTA_STAINED_BRICKS_SLAB);
    public static final RegistryObject<Item> PINK_STAINED_BRICKS_SLAB = registerBlockItem("pink_stained_bricks_slab", ModBlocks.PINK_STAINED_BRICKS_SLAB);
    public static final RegistryObject<Item> WHITE_STAINED_BRICKS_STAIRS = registerBlockItem("white_stained_bricks_stairs", ModBlocks.WHITE_STAINED_BRICKS_STAIRS);
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICKS_STAIRS = registerBlockItem("light_gray_stained_bricks_stairs", ModBlocks.LIGHT_GRAY_STAINED_BRICKS_STAIRS);
    public static final RegistryObject<Item> GRAY_STAINED_BRICKS_STAIRS = registerBlockItem("gray_stained_bricks_stairs", ModBlocks.GRAY_STAINED_BRICKS_STAIRS);
    public static final RegistryObject<Item> BLACK_STAINED_BRICKS_STAIRS = registerBlockItem("black_stained_bricks_stairs", ModBlocks.BLACK_STAINED_BRICKS_STAIRS);
    public static final RegistryObject<Item> BROWN_STAINED_BRICKS_STAIRS = registerBlockItem("brown_stained_bricks_stairs", ModBlocks.BROWN_STAINED_BRICKS_STAIRS);
    public static final RegistryObject<Item> RED_STAINED_BRICKS_STAIRS = registerBlockItem("red_stained_bricks_stairs", ModBlocks.RED_STAINED_BRICKS_STAIRS);
    public static final RegistryObject<Item> ORANGE_STAINED_BRICKS_STAIRS = registerBlockItem("orange_stained_bricks_stairs", ModBlocks.ORANGE_STAINED_BRICKS_STAIRS);
    public static final RegistryObject<Item> YELLOW_STAINED_BRICKS_STAIRS = registerBlockItem("yellow_stained_bricks_stairs", ModBlocks.YELLOW_STAINED_BRICKS_STAIRS);
    public static final RegistryObject<Item> LIME_STAINED_BRICKS_STAIRS = registerBlockItem("lime_stained_bricks_stairs", ModBlocks.LIME_STAINED_BRICKS_STAIRS);
    public static final RegistryObject<Item> GREEN_STAINED_BRICKS_STAIRS = registerBlockItem("green_stained_bricks_stairs", ModBlocks.GREEN_STAINED_BRICKS_STAIRS);
    public static final RegistryObject<Item> CYAN_STAINED_BRICKS_STAIRS = registerBlockItem("cyan_stained_bricks_stairs", ModBlocks.CYAN_STAINED_BRICKS_STAIRS);
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICKS_STAIRS = registerBlockItem("light_blue_stained_bricks_stairs", ModBlocks.LIGHT_BLUE_STAINED_BRICKS_STAIRS);
    public static final RegistryObject<Item> BLUE_STAINED_BRICKS_STAIRS = registerBlockItem("blue_stained_bricks_stairs", ModBlocks.BLUE_STAINED_BRICKS_STAIRS);
    public static final RegistryObject<Item> PURPLE_STAINED_BRICKS_STAIRS = registerBlockItem("purple_stained_bricks_stairs", ModBlocks.PURPLE_STAINED_BRICKS_STAIRS);
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICKS_STAIRS = registerBlockItem("magenta_stained_bricks_stairs", ModBlocks.MAGENTA_STAINED_BRICKS_STAIRS);
    public static final RegistryObject<Item> PINK_STAINED_BRICKS_STAIRS = registerBlockItem("pink_stained_bricks_stairs", ModBlocks.PINK_STAINED_BRICKS_STAIRS);
    public static final RegistryObject<Item> WHITE_STAINED_BRICKS_WALL = registerBlockItem("white_stained_bricks_wall", ModBlocks.WHITE_STAINED_BRICKS_WALL);
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICKS_WALL = registerBlockItem("light_gray_stained_bricks_wall", ModBlocks.LIGHT_GRAY_STAINED_BRICKS_WALL);
    public static final RegistryObject<Item> GRAY_STAINED_BRICKS_WALL = registerBlockItem("gray_stained_bricks_wall", ModBlocks.GRAY_STAINED_BRICKS_WALL);
    public static final RegistryObject<Item> BLACK_STAINED_BRICKS_WALL = registerBlockItem("black_stained_bricks_wall", ModBlocks.BLACK_STAINED_BRICKS_WALL);
    public static final RegistryObject<Item> BROWN_STAINED_BRICKS_WALL = registerBlockItem("brown_stained_bricks_wall", ModBlocks.BROWN_STAINED_BRICKS_WALL);
    public static final RegistryObject<Item> RED_STAINED_BRICKS_WALL = registerBlockItem("red_stained_bricks_wall", ModBlocks.RED_STAINED_BRICKS_WALL);
    public static final RegistryObject<Item> ORANGE_STAINED_BRICKS_WALL = registerBlockItem("orange_stained_bricks_wall", ModBlocks.ORANGE_STAINED_BRICKS_WALL);
    public static final RegistryObject<Item> YELLOW_STAINED_BRICKS_WALL = registerBlockItem("yellow_stained_bricks_wall", ModBlocks.YELLOW_STAINED_BRICKS_WALL);
    public static final RegistryObject<Item> LIME_STAINED_BRICKS_WALL = registerBlockItem("lime_stained_bricks_wall", ModBlocks.LIME_STAINED_BRICKS_WALL);
    public static final RegistryObject<Item> GREEN_STAINED_BRICKS_WALL = registerBlockItem("green_stained_bricks_wall", ModBlocks.GREEN_STAINED_BRICKS_WALL);
    public static final RegistryObject<Item> CYAN_STAINED_BRICKS_WALL = registerBlockItem("cyan_stained_bricks_wall", ModBlocks.CYAN_STAINED_BRICKS_WALL);
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICKS_WALL = registerBlockItem("light_blue_stained_bricks_wall", ModBlocks.LIGHT_BLUE_STAINED_BRICKS_WALL);
    public static final RegistryObject<Item> BLUE_STAINED_BRICKS_WALL = registerBlockItem("blue_stained_bricks_wall", ModBlocks.BLUE_STAINED_BRICKS_WALL);
    public static final RegistryObject<Item> PURPLE_STAINED_BRICKS_WALL = registerBlockItem("purple_stained_bricks_wall", ModBlocks.PURPLE_STAINED_BRICKS_WALL);
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICKS_WALL = registerBlockItem("magenta_stained_bricks_wall", ModBlocks.MAGENTA_STAINED_BRICKS_WALL);
    public static final RegistryObject<Item> PINK_STAINED_BRICKS_WALL = registerBlockItem("pink_stained_bricks_wall", ModBlocks.PINK_STAINED_BRICKS_WALL);

    public static final RegistryObject<Item> WHITE_ITEM_FRAME = ITEMS.register(
            "white_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "white")
    );

    public static final RegistryObject<Item> LIGHT_GRAY_ITEM_FRAME = ITEMS.register(
            "light_gray_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "light_gray")
    );

    public static final RegistryObject<Item> GRAY_ITEM_FRAME = ITEMS.register(
            "gray_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "gray")
    );

    public static final RegistryObject<Item> BLACK_ITEM_FRAME = ITEMS.register(
            "black_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "black")
    );

    public static final RegistryObject<Item> BROWN_ITEM_FRAME = ITEMS.register(
            "brown_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "brown")
    );

    public static final RegistryObject<Item> RED_ITEM_FRAME = ITEMS.register(
            "red_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "red")
    );

    public static final RegistryObject<Item> ORANGE_ITEM_FRAME = ITEMS.register(
            "orange_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "orange")
    );

    public static final RegistryObject<Item> YELLOW_ITEM_FRAME = ITEMS.register(
            "yellow_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "yellow")
    );

    public static final RegistryObject<Item> LIME_ITEM_FRAME = ITEMS.register(
            "lime_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "lime")
    );

    public static final RegistryObject<Item> GREEN_ITEM_FRAME = ITEMS.register(
            "green_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "green")
    );

    public static final RegistryObject<Item> CYAN_ITEM_FRAME = ITEMS.register(
            "cyan_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "cyan")
    );

    public static final RegistryObject<Item> LIGHT_BLUE_ITEM_FRAME = ITEMS.register(
            "light_blue_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "light_blue")
    );

    public static final RegistryObject<Item> BLUE_ITEM_FRAME = ITEMS.register(
            "blue_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "blue")
    );

    public static final RegistryObject<Item> PURPLE_ITEM_FRAME = ITEMS.register(
            "purple_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "purple")
    );

    public static final RegistryObject<Item> MAGENTA_ITEM_FRAME = ITEMS.register(
            "magenta_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "magenta")
    );

    public static final RegistryObject<Item> PINK_ITEM_FRAME = ITEMS.register(
            "pink_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "pink")
    );

    public static final RegistryObject<Item> INVISIBLE_ITEM_FRAME = ITEMS.register(
            "invisible_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "invisible")
    );
    public static final RegistryObject<Item> SMOOTH_STONE_STAIRS = registerBlockItem("smooth_stone_stairs", ModBlocks.SMOOTH_STONE_STAIRS);
    public static final RegistryObject<Item> POLISHED_WHITE_CONCRETE = registerBlockItem("polished_white_concrete", ModBlocks.POLISHED_WHITE_CONCRETE);
    public static final RegistryObject<Item> POLISHED_ORANGE_CONCRETE = registerBlockItem("polished_orange_concrete", ModBlocks.POLISHED_ORANGE_CONCRETE);
    public static final RegistryObject<Item> POLISHED_MAGENTA_CONCRETE = registerBlockItem("polished_magenta_concrete", ModBlocks.POLISHED_MAGENTA_CONCRETE);
    public static final RegistryObject<Item> POLISHED_LIGHT_BLUE_CONCRETE = registerBlockItem("polished_light_blue_concrete", ModBlocks.POLISHED_LIGHT_BLUE_CONCRETE);
    public static final RegistryObject<Item> POLISHED_YELLOW_CONCRETE = registerBlockItem("polished_yellow_concrete", ModBlocks.POLISHED_YELLOW_CONCRETE);
    public static final RegistryObject<Item> POLISHED_LIME_CONCRETE = registerBlockItem("polished_lime_concrete", ModBlocks.POLISHED_LIME_CONCRETE);
    public static final RegistryObject<Item> POLISHED_PINK_CONCRETE = registerBlockItem("polished_pink_concrete", ModBlocks.POLISHED_PINK_CONCRETE);
    public static final RegistryObject<Item> POLISHED_GRAY_CONCRETE = registerBlockItem("polished_gray_concrete", ModBlocks.POLISHED_GRAY_CONCRETE);
    public static final RegistryObject<Item> POLISHED_LIGHT_GRAY_CONCRETE = registerBlockItem("polished_light_gray_concrete", ModBlocks.POLISHED_LIGHT_GRAY_CONCRETE);
    public static final RegistryObject<Item> POLISHED_CYAN_CONCRETE = registerBlockItem("polished_cyan_concrete", ModBlocks.POLISHED_CYAN_CONCRETE);
    public static final RegistryObject<Item> POLISHED_PURPLE_CONCRETE = registerBlockItem("polished_purple_concrete", ModBlocks.POLISHED_PURPLE_CONCRETE);
    public static final RegistryObject<Item> POLISHED_BLUE_CONCRETE = registerBlockItem("polished_blue_concrete", ModBlocks.POLISHED_BLUE_CONCRETE);
    public static final RegistryObject<Item> POLISHED_BROWN_CONCRETE = registerBlockItem("polished_brown_concrete", ModBlocks.POLISHED_BROWN_CONCRETE);
    public static final RegistryObject<Item> POLISHED_GREEN_CONCRETE = registerBlockItem("polished_green_concrete", ModBlocks.POLISHED_GREEN_CONCRETE);
    public static final RegistryObject<Item> POLISHED_RED_CONCRETE = registerBlockItem("polished_red_concrete", ModBlocks.POLISHED_RED_CONCRETE);
    public static final RegistryObject<Item> POLISHED_BLACK_CONCRETE = registerBlockItem("polished_black_concrete", ModBlocks.POLISHED_BLACK_CONCRETE);
    public static final RegistryObject<Item> POLISHED_WHITE_CONCRETE_STAIRS = registerBlockItem("polished_white_concrete_stairs", ModBlocks.POLISHED_WHITE_CONCRETE_STAIRS);
    public static final RegistryObject<Item> POLISHED_WHITE_CONCRETE_SLAB = registerBlockItem("polished_white_concrete_slab", ModBlocks.POLISHED_WHITE_CONCRETE_SLAB);
    public static final RegistryObject<Item> POLISHED_WHITE_CONCRETE_WALL = registerBlockItem("polished_white_concrete_wall", ModBlocks.POLISHED_WHITE_CONCRETE_WALL);
    public static final RegistryObject<Item> POLISHED_ORANGE_CONCRETE_STAIRS = registerBlockItem("polished_orange_concrete_stairs", ModBlocks.POLISHED_ORANGE_CONCRETE_STAIRS);
    public static final RegistryObject<Item> POLISHED_ORANGE_CONCRETE_SLAB = registerBlockItem("polished_orange_concrete_slab", ModBlocks.POLISHED_ORANGE_CONCRETE_SLAB);
    public static final RegistryObject<Item> POLISHED_ORANGE_CONCRETE_WALL = registerBlockItem("polished_orange_concrete_wall", ModBlocks.POLISHED_ORANGE_CONCRETE_WALL);
    public static final RegistryObject<Item> POLISHED_MAGENTA_CONCRETE_STAIRS = registerBlockItem("polished_magenta_concrete_stairs", ModBlocks.POLISHED_MAGENTA_CONCRETE_STAIRS);
    public static final RegistryObject<Item> POLISHED_MAGENTA_CONCRETE_SLAB = registerBlockItem("polished_magenta_concrete_slab", ModBlocks.POLISHED_MAGENTA_CONCRETE_SLAB);
    public static final RegistryObject<Item> POLISHED_MAGENTA_CONCRETE_WALL = registerBlockItem("polished_magenta_concrete_wall", ModBlocks.POLISHED_MAGENTA_CONCRETE_WALL);
    public static final RegistryObject<Item> POLISHED_LIGHT_BLUE_CONCRETE_STAIRS = registerBlockItem("polished_light_blue_concrete_stairs", ModBlocks.POLISHED_LIGHT_BLUE_CONCRETE_STAIRS);
    public static final RegistryObject<Item> POLISHED_LIGHT_BLUE_CONCRETE_SLAB = registerBlockItem("polished_light_blue_concrete_slab", ModBlocks.POLISHED_LIGHT_BLUE_CONCRETE_SLAB);
    public static final RegistryObject<Item> POLISHED_LIGHT_BLUE_CONCRETE_WALL = registerBlockItem("polished_light_blue_concrete_wall", ModBlocks.POLISHED_LIGHT_BLUE_CONCRETE_WALL);
    public static final RegistryObject<Item> POLISHED_YELLOW_CONCRETE_STAIRS = registerBlockItem("polished_yellow_concrete_stairs", ModBlocks.POLISHED_YELLOW_CONCRETE_STAIRS);
    public static final RegistryObject<Item> POLISHED_YELLOW_CONCRETE_SLAB = registerBlockItem("polished_yellow_concrete_slab", ModBlocks.POLISHED_YELLOW_CONCRETE_SLAB);
    public static final RegistryObject<Item> POLISHED_YELLOW_CONCRETE_WALL = registerBlockItem("polished_yellow_concrete_wall", ModBlocks.POLISHED_YELLOW_CONCRETE_WALL);
    public static final RegistryObject<Item> POLISHED_LIME_CONCRETE_STAIRS = registerBlockItem("polished_lime_concrete_stairs", ModBlocks.POLISHED_LIME_CONCRETE_STAIRS);
    public static final RegistryObject<Item> POLISHED_LIME_CONCRETE_SLAB = registerBlockItem("polished_lime_concrete_slab", ModBlocks.POLISHED_LIME_CONCRETE_SLAB);
    public static final RegistryObject<Item> POLISHED_LIME_CONCRETE_WALL = registerBlockItem("polished_lime_concrete_wall", ModBlocks.POLISHED_LIME_CONCRETE_WALL);
    public static final RegistryObject<Item> POLISHED_PINK_CONCRETE_STAIRS = registerBlockItem("polished_pink_concrete_stairs", ModBlocks.POLISHED_PINK_CONCRETE_STAIRS);
    public static final RegistryObject<Item> POLISHED_PINK_CONCRETE_SLAB = registerBlockItem("polished_pink_concrete_slab", ModBlocks.POLISHED_PINK_CONCRETE_SLAB);
    public static final RegistryObject<Item> POLISHED_PINK_CONCRETE_WALL = registerBlockItem("polished_pink_concrete_wall", ModBlocks.POLISHED_PINK_CONCRETE_WALL);
    public static final RegistryObject<Item> POLISHED_GRAY_CONCRETE_STAIRS = registerBlockItem("polished_gray_concrete_stairs", ModBlocks.POLISHED_GRAY_CONCRETE_STAIRS);
    public static final RegistryObject<Item> POLISHED_GRAY_CONCRETE_SLAB = registerBlockItem("polished_gray_concrete_slab", ModBlocks.POLISHED_GRAY_CONCRETE_SLAB);
    public static final RegistryObject<Item> POLISHED_GRAY_CONCRETE_WALL = registerBlockItem("polished_gray_concrete_wall", ModBlocks.POLISHED_GRAY_CONCRETE_WALL);
    public static final RegistryObject<Item> POLISHED_LIGHT_GRAY_CONCRETE_STAIRS = registerBlockItem("polished_light_gray_concrete_stairs", ModBlocks.POLISHED_LIGHT_GRAY_CONCRETE_STAIRS);
    public static final RegistryObject<Item> POLISHED_LIGHT_GRAY_CONCRETE_SLAB = registerBlockItem("polished_light_gray_concrete_slab", ModBlocks.POLISHED_LIGHT_GRAY_CONCRETE_SLAB);
    public static final RegistryObject<Item> POLISHED_LIGHT_GRAY_CONCRETE_WALL = registerBlockItem("polished_light_gray_concrete_wall", ModBlocks.POLISHED_LIGHT_GRAY_CONCRETE_WALL);
    public static final RegistryObject<Item> POLISHED_CYAN_CONCRETE_STAIRS = registerBlockItem("polished_cyan_concrete_stairs", ModBlocks.POLISHED_CYAN_CONCRETE_STAIRS);
    public static final RegistryObject<Item> POLISHED_CYAN_CONCRETE_SLAB = registerBlockItem("polished_cyan_concrete_slab", ModBlocks.POLISHED_CYAN_CONCRETE_SLAB);
    public static final RegistryObject<Item> POLISHED_CYAN_CONCRETE_WALL = registerBlockItem("polished_cyan_concrete_wall", ModBlocks.POLISHED_CYAN_CONCRETE_WALL);
    public static final RegistryObject<Item> POLISHED_PURPLE_CONCRETE_STAIRS = registerBlockItem("polished_purple_concrete_stairs", ModBlocks.POLISHED_PURPLE_CONCRETE_STAIRS);
    public static final RegistryObject<Item> POLISHED_PURPLE_CONCRETE_SLAB = registerBlockItem("polished_purple_concrete_slab", ModBlocks.POLISHED_PURPLE_CONCRETE_SLAB);
    public static final RegistryObject<Item> POLISHED_PURPLE_CONCRETE_WALL = registerBlockItem("polished_purple_concrete_wall", ModBlocks.POLISHED_PURPLE_CONCRETE_WALL);
    public static final RegistryObject<Item> POLISHED_BLUE_CONCRETE_STAIRS = registerBlockItem("polished_blue_concrete_stairs", ModBlocks.POLISHED_BLUE_CONCRETE_STAIRS);
    public static final RegistryObject<Item> POLISHED_BLUE_CONCRETE_SLAB = registerBlockItem("polished_blue_concrete_slab", ModBlocks.POLISHED_BLUE_CONCRETE_SLAB);
    public static final RegistryObject<Item> POLISHED_BLUE_CONCRETE_WALL = registerBlockItem("polished_blue_concrete_wall", ModBlocks.POLISHED_BLUE_CONCRETE_WALL);
    public static final RegistryObject<Item> POLISHED_BROWN_CONCRETE_STAIRS = registerBlockItem("polished_brown_concrete_stairs", ModBlocks.POLISHED_BROWN_CONCRETE_STAIRS);
    public static final RegistryObject<Item> POLISHED_BROWN_CONCRETE_SLAB = registerBlockItem("polished_brown_concrete_slab", ModBlocks.POLISHED_BROWN_CONCRETE_SLAB);
    public static final RegistryObject<Item> POLISHED_BROWN_CONCRETE_WALL = registerBlockItem("polished_brown_concrete_wall", ModBlocks.POLISHED_BROWN_CONCRETE_WALL);
    public static final RegistryObject<Item> POLISHED_GREEN_CONCRETE_STAIRS = registerBlockItem("polished_green_concrete_stairs", ModBlocks.POLISHED_GREEN_CONCRETE_STAIRS);
    public static final RegistryObject<Item> POLISHED_GREEN_CONCRETE_SLAB = registerBlockItem("polished_green_concrete_slab", ModBlocks.POLISHED_GREEN_CONCRETE_SLAB);
    public static final RegistryObject<Item> POLISHED_GREEN_CONCRETE_WALL = registerBlockItem("polished_green_concrete_wall", ModBlocks.POLISHED_GREEN_CONCRETE_WALL);
    public static final RegistryObject<Item> POLISHED_RED_CONCRETE_STAIRS = registerBlockItem("polished_red_concrete_stairs", ModBlocks.POLISHED_RED_CONCRETE_STAIRS);
    public static final RegistryObject<Item> POLISHED_RED_CONCRETE_SLAB = registerBlockItem("polished_red_concrete_slab", ModBlocks.POLISHED_RED_CONCRETE_SLAB);
    public static final RegistryObject<Item> POLISHED_RED_CONCRETE_WALL = registerBlockItem("polished_red_concrete_wall", ModBlocks.POLISHED_RED_CONCRETE_WALL);
    public static final RegistryObject<Item> POLISHED_BLACK_CONCRETE_STAIRS = registerBlockItem("polished_black_concrete_stairs", ModBlocks.POLISHED_BLACK_CONCRETE_STAIRS);
    public static final RegistryObject<Item> POLISHED_BLACK_CONCRETE_SLAB = registerBlockItem("polished_black_concrete_slab", ModBlocks.POLISHED_BLACK_CONCRETE_SLAB);
    public static final RegistryObject<Item> POLISHED_BLACK_CONCRETE_WALL = registerBlockItem("polished_black_concrete_wall", ModBlocks.POLISHED_BLACK_CONCRETE_WALL);
    public static final RegistryObject<Item> SMOKE_VENT = registerBlockItem("smoke_vent", ModBlocks.SMOKE_VENT);
    public static final RegistryObject<Item> WHITE_REDSTONE_LAMP = registerBlockItem("white_redstone_lamp", ModBlocks.WHITE_REDSTONE_LAMP);
    public static final RegistryObject<Item> ORANGE_REDSTONE_LAMP = registerBlockItem("orange_redstone_lamp", ModBlocks.ORANGE_REDSTONE_LAMP);
    public static final RegistryObject<Item> MAGENTA_REDSTONE_LAMP = registerBlockItem("magenta_redstone_lamp", ModBlocks.MAGENTA_REDSTONE_LAMP);
    public static final RegistryObject<Item> LIGHT_BLUE_REDSTONE_LAMP = registerBlockItem("light_blue_redstone_lamp", ModBlocks.LIGHT_BLUE_REDSTONE_LAMP);
    public static final RegistryObject<Item> YELLOW_REDSTONE_LAMP = registerBlockItem("yellow_redstone_lamp", ModBlocks.YELLOW_REDSTONE_LAMP);
    public static final RegistryObject<Item> LIME_REDSTONE_LAMP = registerBlockItem("lime_redstone_lamp", ModBlocks.LIME_REDSTONE_LAMP);
    public static final RegistryObject<Item> PINK_REDSTONE_LAMP = registerBlockItem("pink_redstone_lamp", ModBlocks.PINK_REDSTONE_LAMP);
    public static final RegistryObject<Item> GRAY_REDSTONE_LAMP = registerBlockItem("gray_redstone_lamp", ModBlocks.GRAY_REDSTONE_LAMP);
    public static final RegistryObject<Item> LIGHT_GRAY_REDSTONE_LAMP = registerBlockItem("light_gray_redstone_lamp", ModBlocks.LIGHT_GRAY_REDSTONE_LAMP);
    public static final RegistryObject<Item> CYAN_REDSTONE_LAMP = registerBlockItem("cyan_redstone_lamp", ModBlocks.CYAN_REDSTONE_LAMP);
    public static final RegistryObject<Item> PURPLE_REDSTONE_LAMP = registerBlockItem("purple_redstone_lamp", ModBlocks.PURPLE_REDSTONE_LAMP);
    public static final RegistryObject<Item> BLUE_REDSTONE_LAMP = registerBlockItem("blue_redstone_lamp", ModBlocks.BLUE_REDSTONE_LAMP);
    public static final RegistryObject<Item> BROWN_REDSTONE_LAMP = registerBlockItem("brown_redstone_lamp", ModBlocks.BROWN_REDSTONE_LAMP);
    public static final RegistryObject<Item> GREEN_REDSTONE_LAMP = registerBlockItem("green_redstone_lamp", ModBlocks.GREEN_REDSTONE_LAMP);
    public static final RegistryObject<Item> RED_REDSTONE_LAMP = registerBlockItem("red_redstone_lamp", ModBlocks.RED_REDSTONE_LAMP);
    public static final RegistryObject<Item> BLACK_REDSTONE_LAMP = registerBlockItem("black_redstone_lamp", ModBlocks.BLACK_REDSTONE_LAMP);

    public static final RegistryObject<Item> CASCADE_BLOCK = ITEMS.register(
            "cascade_block",
            () -> new MistBlockItem(ModBlocks.CASCADE_BLOCK.get(), createBlockItemProperties())
    );
}
