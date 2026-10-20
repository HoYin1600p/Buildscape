package com.kingodogo.buildscape.registry.items;

import com.kingodogo.buildscape.feature.copper.CopperChestItem;
import com.kingodogo.buildscape.item.GlassJarItem;
import com.kingodogo.buildscape.item.HazeBushItem;
import com.kingodogo.buildscape.item.MuffBlockItem;
import com.kingodogo.buildscape.registry.ModBlocks;
import com.kingodogo.buildscape.registry.ModCreativeModeTab;
import com.kingodogo.buildscape.registry.ModEntities;
import com.kingodogo.buildscape.registry.ModItems.FestiveWanderingHomemakerSpawnEggItem;
import com.kingodogo.buildscape.registry.ModItems.WanderingHomemakerSpawnEggItem;
import net.minecraft.world.item.BedItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraftforge.registries.RegistryObject;

import static com.kingodogo.buildscape.registry.ModItems.ITEMS;
import static com.kingodogo.buildscape.registry.ModItems.createBlockItemProperties;
import static com.kingodogo.buildscape.registry.ModItems.registerBlockItem;

/** Part of the items registrations; see {@code ModItems}. */
public final class ItemsPart04 {

    private ItemsPart04() {
    }

    /** Forces this class to load so its registrations run in the order {@code ModItems} calls it. */
    public static void init() {
    }

    public static final RegistryObject<Item> QUARTZ_VERTICAL_SLAB =
            registerBlockItem("quartz_vertical_slab", ModBlocks.QUARTZ_VERTICAL_SLAB);
    public static final RegistryObject<Item> SMOOTH_QUARTZ_VERTICAL_SLAB =
            registerBlockItem("smooth_quartz_vertical_slab", ModBlocks.SMOOTH_QUARTZ_VERTICAL_SLAB);
    public static final RegistryObject<Item> TERRACOTTA_SLAB =
            registerBlockItem("terracotta_slab", ModBlocks.TERRACOTTA_SLAB);
    public static final RegistryObject<Item> TERRACOTTA_STAIRS =
            registerBlockItem("terracotta_stairs", ModBlocks.TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> TERRACOTTA_VERTICAL_SLAB =
            registerBlockItem("terracotta_vertical_slab", ModBlocks.TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> WHITE_TERRACOTTA_SLAB =
            registerBlockItem("white_terracotta_slab", ModBlocks.WHITE_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> WHITE_TERRACOTTA_STAIRS =
            registerBlockItem("white_terracotta_stairs", ModBlocks.WHITE_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> WHITE_TERRACOTTA_VERTICAL_SLAB =
            registerBlockItem("white_terracotta_vertical_slab", ModBlocks.WHITE_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> ORANGE_TERRACOTTA_SLAB =
            registerBlockItem("orange_terracotta_slab", ModBlocks.ORANGE_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> ORANGE_TERRACOTTA_STAIRS =
            registerBlockItem("orange_terracotta_stairs", ModBlocks.ORANGE_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> ORANGE_TERRACOTTA_VERTICAL_SLAB =
            registerBlockItem("orange_terracotta_vertical_slab", ModBlocks.ORANGE_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> MAGENTA_TERRACOTTA_SLAB =
            registerBlockItem("magenta_terracotta_slab", ModBlocks.MAGENTA_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> MAGENTA_TERRACOTTA_STAIRS =
            registerBlockItem("magenta_terracotta_stairs", ModBlocks.MAGENTA_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> MAGENTA_TERRACOTTA_VERTICAL_SLAB =
            registerBlockItem("magenta_terracotta_vertical_slab", ModBlocks.MAGENTA_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_TERRACOTTA_SLAB =
            registerBlockItem("light_blue_terracotta_slab", ModBlocks.LIGHT_BLUE_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_TERRACOTTA_STAIRS =
            registerBlockItem("light_blue_terracotta_stairs", ModBlocks.LIGHT_BLUE_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> LIGHT_BLUE_TERRACOTTA_VERTICAL_SLAB =
            registerBlockItem("light_blue_terracotta_vertical_slab", ModBlocks.LIGHT_BLUE_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> YELLOW_TERRACOTTA_SLAB =
            registerBlockItem("yellow_terracotta_slab", ModBlocks.YELLOW_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> YELLOW_TERRACOTTA_STAIRS =
            registerBlockItem("yellow_terracotta_stairs", ModBlocks.YELLOW_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> YELLOW_TERRACOTTA_VERTICAL_SLAB =
            registerBlockItem("yellow_terracotta_vertical_slab", ModBlocks.YELLOW_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIME_TERRACOTTA_SLAB =
            registerBlockItem("lime_terracotta_slab", ModBlocks.LIME_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> LIME_TERRACOTTA_STAIRS =
            registerBlockItem("lime_terracotta_stairs", ModBlocks.LIME_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> LIME_TERRACOTTA_VERTICAL_SLAB =
            registerBlockItem("lime_terracotta_vertical_slab", ModBlocks.LIME_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> PINK_TERRACOTTA_SLAB =
            registerBlockItem("pink_terracotta_slab", ModBlocks.PINK_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> PINK_TERRACOTTA_STAIRS =
            registerBlockItem("pink_terracotta_stairs", ModBlocks.PINK_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> PINK_TERRACOTTA_VERTICAL_SLAB =
            registerBlockItem("pink_terracotta_vertical_slab", ModBlocks.PINK_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> GRAY_TERRACOTTA_SLAB =
            registerBlockItem("gray_terracotta_slab", ModBlocks.GRAY_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> GRAY_TERRACOTTA_STAIRS =
            registerBlockItem("gray_terracotta_stairs", ModBlocks.GRAY_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> GRAY_TERRACOTTA_VERTICAL_SLAB =
            registerBlockItem("gray_terracotta_vertical_slab", ModBlocks.GRAY_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_TERRACOTTA_SLAB =
            registerBlockItem("light_gray_terracotta_slab", ModBlocks.LIGHT_GRAY_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_TERRACOTTA_STAIRS =
            registerBlockItem("light_gray_terracotta_stairs", ModBlocks.LIGHT_GRAY_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> LIGHT_GRAY_TERRACOTTA_VERTICAL_SLAB =
            registerBlockItem("light_gray_terracotta_vertical_slab", ModBlocks.LIGHT_GRAY_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> CYAN_TERRACOTTA_SLAB =
            registerBlockItem("cyan_terracotta_slab", ModBlocks.CYAN_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> CYAN_TERRACOTTA_STAIRS =
            registerBlockItem("cyan_terracotta_stairs", ModBlocks.CYAN_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> CYAN_TERRACOTTA_VERTICAL_SLAB =
            registerBlockItem("cyan_terracotta_vertical_slab", ModBlocks.CYAN_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> PURPLE_TERRACOTTA_SLAB =
            registerBlockItem("purple_terracotta_slab", ModBlocks.PURPLE_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> PURPLE_TERRACOTTA_STAIRS =
            registerBlockItem("purple_terracotta_stairs", ModBlocks.PURPLE_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> PURPLE_TERRACOTTA_VERTICAL_SLAB =
            registerBlockItem("purple_terracotta_vertical_slab", ModBlocks.PURPLE_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLUE_TERRACOTTA_SLAB =
            registerBlockItem("blue_terracotta_slab", ModBlocks.BLUE_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> BLUE_TERRACOTTA_STAIRS =
            registerBlockItem("blue_terracotta_stairs", ModBlocks.BLUE_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> BLUE_TERRACOTTA_VERTICAL_SLAB =
            registerBlockItem("blue_terracotta_vertical_slab", ModBlocks.BLUE_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> BROWN_TERRACOTTA_SLAB =
            registerBlockItem("brown_terracotta_slab", ModBlocks.BROWN_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> BROWN_TERRACOTTA_STAIRS =
            registerBlockItem("brown_terracotta_stairs", ModBlocks.BROWN_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> BROWN_TERRACOTTA_VERTICAL_SLAB =
            registerBlockItem("brown_terracotta_vertical_slab", ModBlocks.BROWN_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> GREEN_TERRACOTTA_SLAB =
            registerBlockItem("green_terracotta_slab", ModBlocks.GREEN_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> GREEN_TERRACOTTA_STAIRS =
            registerBlockItem("green_terracotta_stairs", ModBlocks.GREEN_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> GREEN_TERRACOTTA_VERTICAL_SLAB =
            registerBlockItem("green_terracotta_vertical_slab", ModBlocks.GREEN_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_TERRACOTTA_SLAB =
            registerBlockItem("red_terracotta_slab", ModBlocks.RED_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> RED_TERRACOTTA_STAIRS =
            registerBlockItem("red_terracotta_stairs", ModBlocks.RED_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> RED_TERRACOTTA_VERTICAL_SLAB =
            registerBlockItem("red_terracotta_vertical_slab", ModBlocks.RED_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLACK_TERRACOTTA_SLAB =
            registerBlockItem("black_terracotta_slab", ModBlocks.BLACK_TERRACOTTA_SLAB);
    public static final RegistryObject<Item> BLACK_TERRACOTTA_STAIRS =
            registerBlockItem("black_terracotta_stairs", ModBlocks.BLACK_TERRACOTTA_STAIRS);
    public static final RegistryObject<Item> BLACK_TERRACOTTA_VERTICAL_SLAB =
            registerBlockItem("black_terracotta_vertical_slab", ModBlocks.BLACK_TERRACOTTA_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_OAK_LOG_STAIRS =
            registerBlockItem("stripped_oak_log_stairs", ModBlocks.STRIPPED_OAK_LOG_STAIRS);
    public static final RegistryObject<Item> SPRUCE_LOG_STAIRS =
            registerBlockItem("spruce_log_stairs", ModBlocks.SPRUCE_LOG_STAIRS);
    public static final RegistryObject<Item> STRIPPED_SPRUCE_LOG_STAIRS =
            registerBlockItem("stripped_spruce_log_stairs", ModBlocks.STRIPPED_SPRUCE_LOG_STAIRS);
    public static final RegistryObject<Item> BIRCH_LOG_STAIRS =
            registerBlockItem("birch_log_stairs", ModBlocks.BIRCH_LOG_STAIRS);
    public static final RegistryObject<Item> STRIPPED_BIRCH_LOG_STAIRS =
            registerBlockItem("stripped_birch_log_stairs", ModBlocks.STRIPPED_BIRCH_LOG_STAIRS);
    public static final RegistryObject<Item> JUNGLE_LOG_STAIRS =
            registerBlockItem("jungle_log_stairs", ModBlocks.JUNGLE_LOG_STAIRS);
    public static final RegistryObject<Item> STRIPPED_JUNGLE_LOG_STAIRS =
            registerBlockItem("stripped_jungle_log_stairs", ModBlocks.STRIPPED_JUNGLE_LOG_STAIRS);
    public static final RegistryObject<Item> ACACIA_LOG_STAIRS =
            registerBlockItem("acacia_log_stairs", ModBlocks.ACACIA_LOG_STAIRS);
    public static final RegistryObject<Item> STRIPPED_ACACIA_LOG_STAIRS =
            registerBlockItem("stripped_acacia_log_stairs", ModBlocks.STRIPPED_ACACIA_LOG_STAIRS);
    public static final RegistryObject<Item> DARK_OAK_LOG_STAIRS =
            registerBlockItem("dark_oak_log_stairs", ModBlocks.DARK_OAK_LOG_STAIRS);
    public static final RegistryObject<Item> STRIPPED_DARK_OAK_LOG_STAIRS =
            registerBlockItem("stripped_dark_oak_log_stairs", ModBlocks.STRIPPED_DARK_OAK_LOG_STAIRS);
    public static final RegistryObject<Item> CRIMSON_STEM_STAIRS =
            registerBlockItem("crimson_stem_stairs", ModBlocks.CRIMSON_STEM_STAIRS);
    public static final RegistryObject<Item> STRIPPED_CRIMSON_STEM_STAIRS =
            registerBlockItem("stripped_crimson_stem_stairs", ModBlocks.STRIPPED_CRIMSON_STEM_STAIRS);
    public static final RegistryObject<Item> WARPED_STEM_STAIRS =
            registerBlockItem("warped_stem_stairs", ModBlocks.WARPED_STEM_STAIRS);
    public static final RegistryObject<Item> STRIPPED_WARPED_STEM_STAIRS =
            registerBlockItem("stripped_warped_stem_stairs", ModBlocks.STRIPPED_WARPED_STEM_STAIRS);
    public static final RegistryObject<Item> ACACIA_WOOD_SLAB =
            registerBlockItem("acacia_wood_slab", ModBlocks.ACACIA_WOOD_SLAB);
    public static final RegistryObject<Item> ACACIA_WOOD_STAIRS =
            registerBlockItem("acacia_wood_stairs", ModBlocks.ACACIA_WOOD_STAIRS);
    public static final RegistryObject<Item> ACACIA_WOOD_VERTICAL_SLAB =
            registerBlockItem("acacia_wood_vertical_slab", ModBlocks.ACACIA_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> AMETHYST_BLOCK_STAIRS =
            registerBlockItem("amethyst_block_stairs", ModBlocks.AMETHYST_BLOCK_STAIRS);
    public static final RegistryObject<Item> BIRCH_WOOD_SLAB =
            registerBlockItem("birch_wood_slab", ModBlocks.BIRCH_WOOD_SLAB);
    public static final RegistryObject<Item> BIRCH_WOOD_STAIRS =
            registerBlockItem("birch_wood_stairs", ModBlocks.BIRCH_WOOD_STAIRS);
    public static final RegistryObject<Item> BIRCH_WOOD_VERTICAL_SLAB =
            registerBlockItem("birch_wood_vertical_slab", ModBlocks.BIRCH_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> CRIMSON_HYPHAE_SLAB =
            registerBlockItem("crimson_hyphae_slab", ModBlocks.CRIMSON_HYPHAE_SLAB);
    public static final RegistryObject<Item> CRIMSON_HYPHAE_STAIRS =
            registerBlockItem("crimson_hyphae_stairs", ModBlocks.CRIMSON_HYPHAE_STAIRS);
    public static final RegistryObject<Item> CRIMSON_HYPHAE_VERTICAL_SLAB =
            registerBlockItem("crimson_hyphae_vertical_slab", ModBlocks.CRIMSON_HYPHAE_VERTICAL_SLAB);
    public static final RegistryObject<Item> CRYING_OBSIDIAN_SLAB =
            registerBlockItem("crying_obsidian_slab", ModBlocks.CRYING_OBSIDIAN_SLAB);
    public static final RegistryObject<Item> CRYING_OBSIDIAN_STAIRS =
            registerBlockItem("crying_obsidian_stairs", ModBlocks.CRYING_OBSIDIAN_STAIRS);
    public static final RegistryObject<Item> CRYING_OBSIDIAN_VERTICAL_SLAB =
            registerBlockItem("crying_obsidian_vertical_slab", ModBlocks.CRYING_OBSIDIAN_VERTICAL_SLAB);
    public static final RegistryObject<Item> CUT_RED_SANDSTONE_STAIRS =
            registerBlockItem("cut_red_sandstone_stairs", ModBlocks.CUT_RED_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> CUT_RED_SANDSTONE_VERTICAL_SLAB =
            registerBlockItem("cut_red_sandstone_vertical_slab", ModBlocks.CUT_RED_SANDSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> CUT_SANDSTONE_STAIRS =
            registerBlockItem("cut_sandstone_stairs", ModBlocks.CUT_SANDSTONE_STAIRS);
    public static final RegistryObject<Item> CUT_SANDSTONE_VERTICAL_SLAB =
            registerBlockItem("cut_sandstone_vertical_slab", ModBlocks.CUT_SANDSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> DARK_OAK_WOOD_SLAB =
            registerBlockItem("dark_oak_wood_slab", ModBlocks.DARK_OAK_WOOD_SLAB);
    public static final RegistryObject<Item> DARK_OAK_WOOD_STAIRS =
            registerBlockItem("dark_oak_wood_stairs", ModBlocks.DARK_OAK_WOOD_STAIRS);
    public static final RegistryObject<Item> DARK_OAK_WOOD_VERTICAL_SLAB =
            registerBlockItem("dark_oak_wood_vertical_slab", ModBlocks.DARK_OAK_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> DIRT_STAIRS = registerBlockItem("dirt_stairs", ModBlocks.DIRT_STAIRS);
    public static final RegistryObject<Item> JUNGLE_WOOD_SLAB =
            registerBlockItem("jungle_wood_slab", ModBlocks.JUNGLE_WOOD_SLAB);
    public static final RegistryObject<Item> JUNGLE_WOOD_STAIRS =
            registerBlockItem("jungle_wood_stairs", ModBlocks.JUNGLE_WOOD_STAIRS);
    public static final RegistryObject<Item> JUNGLE_WOOD_VERTICAL_SLAB =
            registerBlockItem("jungle_wood_vertical_slab", ModBlocks.JUNGLE_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> MOSS_BLOCK_STAIRS =
            registerBlockItem("moss_block_stairs", ModBlocks.MOSS_BLOCK_STAIRS);
    public static final RegistryObject<Item> MYCELIUM_STAIRS =
            registerBlockItem("mycelium_stairs", ModBlocks.MYCELIUM_STAIRS);
    public static final RegistryObject<Item> OAK_WOOD_SLAB =
            registerBlockItem("oak_wood_slab", ModBlocks.OAK_WOOD_SLAB);
    public static final RegistryObject<Item> OAK_WOOD_STAIRS =
            registerBlockItem("oak_wood_stairs", ModBlocks.OAK_WOOD_STAIRS);
    public static final RegistryObject<Item> OAK_WOOD_VERTICAL_SLAB =
            registerBlockItem("oak_wood_vertical_slab", ModBlocks.OAK_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> PODZOL_STAIRS =
            registerBlockItem("podzol_stairs", ModBlocks.PODZOL_STAIRS);
    public static final RegistryObject<Item> SANDSTONE_VERTICAL_SLAB =
            registerBlockItem("sandstone_vertical_slab", ModBlocks.SANDSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> SMOOTH_RED_SANDSTONE_VERTICAL_SLAB =
            registerBlockItem("smooth_red_sandstone_vertical_slab", ModBlocks.SMOOTH_RED_SANDSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> SMOOTH_SANDSTONE_VERTICAL_SLAB =
            registerBlockItem("smooth_sandstone_vertical_slab", ModBlocks.SMOOTH_SANDSTONE_VERTICAL_SLAB);
    public static final RegistryObject<Item> SPRUCE_WOOD_SLAB =
            registerBlockItem("spruce_wood_slab", ModBlocks.SPRUCE_WOOD_SLAB);
    public static final RegistryObject<Item> SPRUCE_WOOD_STAIRS =
            registerBlockItem("spruce_wood_stairs", ModBlocks.SPRUCE_WOOD_STAIRS);
    public static final RegistryObject<Item> SPRUCE_WOOD_VERTICAL_SLAB =
            registerBlockItem("spruce_wood_vertical_slab", ModBlocks.SPRUCE_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_ACACIA_WOOD_SLAB =
            registerBlockItem("stripped_acacia_wood_slab", ModBlocks.STRIPPED_ACACIA_WOOD_SLAB);
    public static final RegistryObject<Item> STRIPPED_ACACIA_WOOD_STAIRS =
            registerBlockItem("stripped_acacia_wood_stairs", ModBlocks.STRIPPED_ACACIA_WOOD_STAIRS);
    public static final RegistryObject<Item> STRIPPED_ACACIA_WOOD_VERTICAL_SLAB =
            registerBlockItem("stripped_acacia_wood_vertical_slab", ModBlocks.STRIPPED_ACACIA_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_BIRCH_WOOD_SLAB =
            registerBlockItem("stripped_birch_wood_slab", ModBlocks.STRIPPED_BIRCH_WOOD_SLAB);
    public static final RegistryObject<Item> STRIPPED_BIRCH_WOOD_STAIRS =
            registerBlockItem("stripped_birch_wood_stairs", ModBlocks.STRIPPED_BIRCH_WOOD_STAIRS);
    public static final RegistryObject<Item> STRIPPED_BIRCH_WOOD_VERTICAL_SLAB =
            registerBlockItem("stripped_birch_wood_vertical_slab", ModBlocks.STRIPPED_BIRCH_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_CRIMSON_HYPHAE_SLAB =
            registerBlockItem("stripped_crimson_hyphae_slab", ModBlocks.STRIPPED_CRIMSON_HYPHAE_SLAB);
    public static final RegistryObject<Item> STRIPPED_CRIMSON_HYPHAE_STAIRS =
            registerBlockItem("stripped_crimson_hyphae_stairs", ModBlocks.STRIPPED_CRIMSON_HYPHAE_STAIRS);
    public static final RegistryObject<Item> STRIPPED_CRIMSON_HYPHAE_VERTICAL_SLAB =
            registerBlockItem("stripped_crimson_hyphae_vertical_slab", ModBlocks.STRIPPED_CRIMSON_HYPHAE_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_DARK_OAK_WOOD_SLAB =
            registerBlockItem("stripped_dark_oak_wood_slab", ModBlocks.STRIPPED_DARK_OAK_WOOD_SLAB);
    public static final RegistryObject<Item> STRIPPED_DARK_OAK_WOOD_STAIRS =
            registerBlockItem("stripped_dark_oak_wood_stairs", ModBlocks.STRIPPED_DARK_OAK_WOOD_STAIRS);
    public static final RegistryObject<Item> STRIPPED_DARK_OAK_WOOD_VERTICAL_SLAB =
            registerBlockItem("stripped_dark_oak_wood_vertical_slab", ModBlocks.STRIPPED_DARK_OAK_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_JUNGLE_WOOD_SLAB =
            registerBlockItem("stripped_jungle_wood_slab", ModBlocks.STRIPPED_JUNGLE_WOOD_SLAB);
    public static final RegistryObject<Item> STRIPPED_JUNGLE_WOOD_STAIRS =
            registerBlockItem("stripped_jungle_wood_stairs", ModBlocks.STRIPPED_JUNGLE_WOOD_STAIRS);
    public static final RegistryObject<Item> STRIPPED_JUNGLE_WOOD_VERTICAL_SLAB =
            registerBlockItem("stripped_jungle_wood_vertical_slab", ModBlocks.STRIPPED_JUNGLE_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_OAK_WOOD_SLAB =
            registerBlockItem("stripped_oak_wood_slab", ModBlocks.STRIPPED_OAK_WOOD_SLAB);
    public static final RegistryObject<Item> STRIPPED_OAK_WOOD_STAIRS =
            registerBlockItem("stripped_oak_wood_stairs", ModBlocks.STRIPPED_OAK_WOOD_STAIRS);
    public static final RegistryObject<Item> STRIPPED_OAK_WOOD_VERTICAL_SLAB =
            registerBlockItem("stripped_oak_wood_vertical_slab", ModBlocks.STRIPPED_OAK_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_SPRUCE_WOOD_SLAB =
            registerBlockItem("stripped_spruce_wood_slab", ModBlocks.STRIPPED_SPRUCE_WOOD_SLAB);
    public static final RegistryObject<Item> STRIPPED_SPRUCE_WOOD_STAIRS =
            registerBlockItem("stripped_spruce_wood_stairs", ModBlocks.STRIPPED_SPRUCE_WOOD_STAIRS);
    public static final RegistryObject<Item> STRIPPED_SPRUCE_WOOD_VERTICAL_SLAB =
            registerBlockItem("stripped_spruce_wood_vertical_slab", ModBlocks.STRIPPED_SPRUCE_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_WARPED_HYPHAE_SLAB =
            registerBlockItem("stripped_warped_hyphae_slab", ModBlocks.STRIPPED_WARPED_HYPHAE_SLAB);
    public static final RegistryObject<Item> STRIPPED_WARPED_HYPHAE_STAIRS =
            registerBlockItem("stripped_warped_hyphae_stairs", ModBlocks.STRIPPED_WARPED_HYPHAE_STAIRS);
    public static final RegistryObject<Item> STRIPPED_WARPED_HYPHAE_VERTICAL_SLAB =
            registerBlockItem("stripped_warped_hyphae_vertical_slab", ModBlocks.STRIPPED_WARPED_HYPHAE_VERTICAL_SLAB);
    public static final RegistryObject<Item> TINTED_GLASS_SLAB =
            registerBlockItem("tinted_glass_slab", ModBlocks.TINTED_GLASS_SLAB);
    public static final RegistryObject<Item> TINTED_GLASS_STAIRS =
            registerBlockItem("tinted_glass_stairs", ModBlocks.TINTED_GLASS_STAIRS);
    public static final RegistryObject<Item> TINTED_GLASS_VERTICAL_SLAB =
            registerBlockItem("tinted_glass_vertical_slab", ModBlocks.TINTED_GLASS_VERTICAL_SLAB);
    public static final RegistryObject<Item> WARPED_HYPHAE_SLAB =
            registerBlockItem("warped_hyphae_slab", ModBlocks.WARPED_HYPHAE_SLAB);
    public static final RegistryObject<Item> WARPED_HYPHAE_STAIRS =
            registerBlockItem("warped_hyphae_stairs", ModBlocks.WARPED_HYPHAE_STAIRS);
    public static final RegistryObject<Item> WARPED_HYPHAE_VERTICAL_SLAB =
            registerBlockItem("warped_hyphae_vertical_slab", ModBlocks.WARPED_HYPHAE_VERTICAL_SLAB);
    public static final RegistryObject<Item> MUFF_BLOCK = ITEMS.register("muff_block",
            () -> new MuffBlockItem(ModBlocks.MUFF_BLOCK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WHITE_WALLPAPER =
            registerBlockItem("white_wallpaper", ModBlocks.WHITE_WALLPAPER);
    public static final RegistryObject<Item> ORANGE_WALLPAPER =
            registerBlockItem("orange_wallpaper", ModBlocks.ORANGE_WALLPAPER);
    public static final RegistryObject<Item> MAGENTA_WALLPAPER =
            registerBlockItem("magenta_wallpaper", ModBlocks.MAGENTA_WALLPAPER);
    public static final RegistryObject<Item> LIGHT_BLUE_WALLPAPER =
            registerBlockItem("light_blue_wallpaper", ModBlocks.LIGHT_BLUE_WALLPAPER);
    public static final RegistryObject<Item> YELLOW_WALLPAPER =
            registerBlockItem("yellow_wallpaper", ModBlocks.YELLOW_WALLPAPER);
    public static final RegistryObject<Item> LIME_WALLPAPER =
            registerBlockItem("lime_wallpaper", ModBlocks.LIME_WALLPAPER);
    public static final RegistryObject<Item> PINK_WALLPAPER =
            registerBlockItem("pink_wallpaper", ModBlocks.PINK_WALLPAPER);
    public static final RegistryObject<Item> GRAY_WALLPAPER =
            registerBlockItem("gray_wallpaper", ModBlocks.GRAY_WALLPAPER);
    public static final RegistryObject<Item> LIGHT_GRAY_WALLPAPER =
            registerBlockItem("light_gray_wallpaper", ModBlocks.LIGHT_GRAY_WALLPAPER);
    public static final RegistryObject<Item> CYAN_WALLPAPER =
            registerBlockItem("cyan_wallpaper", ModBlocks.CYAN_WALLPAPER);
    public static final RegistryObject<Item> PURPLE_WALLPAPER =
            registerBlockItem("purple_wallpaper", ModBlocks.PURPLE_WALLPAPER);
    public static final RegistryObject<Item> BLUE_WALLPAPER =
            registerBlockItem("blue_wallpaper", ModBlocks.BLUE_WALLPAPER);
    public static final RegistryObject<Item> BROWN_WALLPAPER =
            registerBlockItem("brown_wallpaper", ModBlocks.BROWN_WALLPAPER);
    public static final RegistryObject<Item> GREEN_WALLPAPER =
            registerBlockItem("green_wallpaper", ModBlocks.GREEN_WALLPAPER);
    public static final RegistryObject<Item> RED_WALLPAPER =
            registerBlockItem("red_wallpaper", ModBlocks.RED_WALLPAPER);
    public static final RegistryObject<Item> BLACK_WALLPAPER =
            registerBlockItem("black_wallpaper", ModBlocks.BLACK_WALLPAPER);
    public static final RegistryObject<Item> WHITE_WALLPAPER_SLAB =
            registerBlockItem("white_wallpaper_slab", ModBlocks.WHITE_WALLPAPER_SLAB);
    public static final RegistryObject<Item> WHITE_WALLPAPER_VERTICAL_SLAB =
            registerBlockItem("white_wallpaper_vertical_slab", ModBlocks.WHITE_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> ORANGE_WALLPAPER_SLAB =
            registerBlockItem("orange_wallpaper_slab", ModBlocks.ORANGE_WALLPAPER_SLAB);
    public static final RegistryObject<Item> ORANGE_WALLPAPER_VERTICAL_SLAB =
            registerBlockItem("orange_wallpaper_vertical_slab", ModBlocks.ORANGE_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> MAGENTA_WALLPAPER_SLAB =
            registerBlockItem("magenta_wallpaper_slab", ModBlocks.MAGENTA_WALLPAPER_SLAB);
    public static final RegistryObject<Item> MAGENTA_WALLPAPER_VERTICAL_SLAB =
            registerBlockItem("magenta_wallpaper_vertical_slab", ModBlocks.MAGENTA_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_WALLPAPER_SLAB =
            registerBlockItem("light_blue_wallpaper_slab", ModBlocks.LIGHT_BLUE_WALLPAPER_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_WALLPAPER_VERTICAL_SLAB =
            registerBlockItem("light_blue_wallpaper_vertical_slab", ModBlocks.LIGHT_BLUE_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> YELLOW_WALLPAPER_SLAB =
            registerBlockItem("yellow_wallpaper_slab", ModBlocks.YELLOW_WALLPAPER_SLAB);
    public static final RegistryObject<Item> YELLOW_WALLPAPER_VERTICAL_SLAB =
            registerBlockItem("yellow_wallpaper_vertical_slab", ModBlocks.YELLOW_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIME_WALLPAPER_SLAB =
            registerBlockItem("lime_wallpaper_slab", ModBlocks.LIME_WALLPAPER_SLAB);
    public static final RegistryObject<Item> LIME_WALLPAPER_VERTICAL_SLAB =
            registerBlockItem("lime_wallpaper_vertical_slab", ModBlocks.LIME_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> PINK_WALLPAPER_SLAB =
            registerBlockItem("pink_wallpaper_slab", ModBlocks.PINK_WALLPAPER_SLAB);
    public static final RegistryObject<Item> PINK_WALLPAPER_VERTICAL_SLAB =
            registerBlockItem("pink_wallpaper_vertical_slab", ModBlocks.PINK_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> GRAY_WALLPAPER_SLAB =
            registerBlockItem("gray_wallpaper_slab", ModBlocks.GRAY_WALLPAPER_SLAB);
    public static final RegistryObject<Item> GRAY_WALLPAPER_VERTICAL_SLAB =
            registerBlockItem("gray_wallpaper_vertical_slab", ModBlocks.GRAY_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_WALLPAPER_SLAB =
            registerBlockItem("light_gray_wallpaper_slab", ModBlocks.LIGHT_GRAY_WALLPAPER_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_WALLPAPER_VERTICAL_SLAB =
            registerBlockItem("light_gray_wallpaper_vertical_slab", ModBlocks.LIGHT_GRAY_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> CYAN_WALLPAPER_SLAB =
            registerBlockItem("cyan_wallpaper_slab", ModBlocks.CYAN_WALLPAPER_SLAB);
    public static final RegistryObject<Item> CYAN_WALLPAPER_VERTICAL_SLAB =
            registerBlockItem("cyan_wallpaper_vertical_slab", ModBlocks.CYAN_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> PURPLE_WALLPAPER_SLAB =
            registerBlockItem("purple_wallpaper_slab", ModBlocks.PURPLE_WALLPAPER_SLAB);
    public static final RegistryObject<Item> PURPLE_WALLPAPER_VERTICAL_SLAB =
            registerBlockItem("purple_wallpaper_vertical_slab", ModBlocks.PURPLE_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLUE_WALLPAPER_SLAB =
            registerBlockItem("blue_wallpaper_slab", ModBlocks.BLUE_WALLPAPER_SLAB);
    public static final RegistryObject<Item> BLUE_WALLPAPER_VERTICAL_SLAB =
            registerBlockItem("blue_wallpaper_vertical_slab", ModBlocks.BLUE_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> BROWN_WALLPAPER_SLAB =
            registerBlockItem("brown_wallpaper_slab", ModBlocks.BROWN_WALLPAPER_SLAB);
    public static final RegistryObject<Item> BROWN_WALLPAPER_VERTICAL_SLAB =
            registerBlockItem("brown_wallpaper_vertical_slab", ModBlocks.BROWN_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> GREEN_WALLPAPER_SLAB =
            registerBlockItem("green_wallpaper_slab", ModBlocks.GREEN_WALLPAPER_SLAB);
    public static final RegistryObject<Item> GREEN_WALLPAPER_VERTICAL_SLAB =
            registerBlockItem("green_wallpaper_vertical_slab", ModBlocks.GREEN_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_WALLPAPER_SLAB =
            registerBlockItem("red_wallpaper_slab", ModBlocks.RED_WALLPAPER_SLAB);
    public static final RegistryObject<Item> RED_WALLPAPER_VERTICAL_SLAB =
            registerBlockItem("red_wallpaper_vertical_slab", ModBlocks.RED_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLACK_WALLPAPER_SLAB =
            registerBlockItem("black_wallpaper_slab", ModBlocks.BLACK_WALLPAPER_SLAB);
    public static final RegistryObject<Item> BLACK_WALLPAPER_VERTICAL_SLAB =
            registerBlockItem("black_wallpaper_vertical_slab", ModBlocks.BLACK_WALLPAPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> WHITE_WALLPAPER_FLAT =
            registerBlockItem("white_wallpaper_flat", ModBlocks.WHITE_WALLPAPER_FLAT);
    public static final RegistryObject<Item> ORANGE_WALLPAPER_FLAT =
            registerBlockItem("orange_wallpaper_flat", ModBlocks.ORANGE_WALLPAPER_FLAT);
    public static final RegistryObject<Item> MAGENTA_WALLPAPER_FLAT =
            registerBlockItem("magenta_wallpaper_flat", ModBlocks.MAGENTA_WALLPAPER_FLAT);
    public static final RegistryObject<Item> LIGHT_BLUE_WALLPAPER_FLAT =
            registerBlockItem("light_blue_wallpaper_flat", ModBlocks.LIGHT_BLUE_WALLPAPER_FLAT);
    public static final RegistryObject<Item> YELLOW_WALLPAPER_FLAT =
            registerBlockItem("yellow_wallpaper_flat", ModBlocks.YELLOW_WALLPAPER_FLAT);
    public static final RegistryObject<Item> LIME_WALLPAPER_FLAT =
            registerBlockItem("lime_wallpaper_flat", ModBlocks.LIME_WALLPAPER_FLAT);
    public static final RegistryObject<Item> PINK_WALLPAPER_FLAT =
            registerBlockItem("pink_wallpaper_flat", ModBlocks.PINK_WALLPAPER_FLAT);
    public static final RegistryObject<Item> GRAY_WALLPAPER_FLAT =
            registerBlockItem("gray_wallpaper_flat", ModBlocks.GRAY_WALLPAPER_FLAT);
    public static final RegistryObject<Item> LIGHT_GRAY_WALLPAPER_FLAT =
            registerBlockItem("light_gray_wallpaper_flat", ModBlocks.LIGHT_GRAY_WALLPAPER_FLAT);
    public static final RegistryObject<Item> CYAN_WALLPAPER_FLAT =
            registerBlockItem("cyan_wallpaper_flat", ModBlocks.CYAN_WALLPAPER_FLAT);
    public static final RegistryObject<Item> PURPLE_WALLPAPER_FLAT =
            registerBlockItem("purple_wallpaper_flat", ModBlocks.PURPLE_WALLPAPER_FLAT);
    public static final RegistryObject<Item> BLUE_WALLPAPER_FLAT =
            registerBlockItem("blue_wallpaper_flat", ModBlocks.BLUE_WALLPAPER_FLAT);
    public static final RegistryObject<Item> BROWN_WALLPAPER_FLAT =
            registerBlockItem("brown_wallpaper_flat", ModBlocks.BROWN_WALLPAPER_FLAT);
    public static final RegistryObject<Item> GREEN_WALLPAPER_FLAT =
            registerBlockItem("green_wallpaper_flat", ModBlocks.GREEN_WALLPAPER_FLAT);
    public static final RegistryObject<Item> RED_WALLPAPER_FLAT =
            registerBlockItem("red_wallpaper_flat", ModBlocks.RED_WALLPAPER_FLAT);
    public static final RegistryObject<Item> BLACK_WALLPAPER_FLAT =
            registerBlockItem("black_wallpaper_flat", ModBlocks.BLACK_WALLPAPER_FLAT);
    public static final RegistryObject<Item> WHITE_CUSHION =
            registerBlockItem("white_cushion", ModBlocks.WHITE_CUSHION);
    public static final RegistryObject<Item> ORANGE_CUSHION =
            registerBlockItem("orange_cushion", ModBlocks.ORANGE_CUSHION);
    public static final RegistryObject<Item> MAGENTA_CUSHION =
            registerBlockItem("magenta_cushion", ModBlocks.MAGENTA_CUSHION);
    public static final RegistryObject<Item> LIGHT_BLUE_CUSHION =
            registerBlockItem("light_blue_cushion", ModBlocks.LIGHT_BLUE_CUSHION);
    public static final RegistryObject<Item> YELLOW_CUSHION =
            registerBlockItem("yellow_cushion", ModBlocks.YELLOW_CUSHION);
    public static final RegistryObject<Item> LIME_CUSHION = registerBlockItem("lime_cushion", ModBlocks.LIME_CUSHION);
    public static final RegistryObject<Item> PINK_CUSHION = registerBlockItem("pink_cushion", ModBlocks.PINK_CUSHION);
    public static final RegistryObject<Item> GRAY_CUSHION = registerBlockItem("gray_cushion", ModBlocks.GRAY_CUSHION);
    public static final RegistryObject<Item> LIGHT_GRAY_CUSHION =
            registerBlockItem("light_gray_cushion", ModBlocks.LIGHT_GRAY_CUSHION);
    public static final RegistryObject<Item> CYAN_CUSHION = registerBlockItem("cyan_cushion", ModBlocks.CYAN_CUSHION);
    public static final RegistryObject<Item> PURPLE_CUSHION =
            registerBlockItem("purple_cushion", ModBlocks.PURPLE_CUSHION);
    public static final RegistryObject<Item> BLUE_CUSHION = registerBlockItem("blue_cushion", ModBlocks.BLUE_CUSHION);
    public static final RegistryObject<Item> BROWN_CUSHION =
            registerBlockItem("brown_cushion", ModBlocks.BROWN_CUSHION);
    public static final RegistryObject<Item> GREEN_CUSHION =
            registerBlockItem("green_cushion", ModBlocks.GREEN_CUSHION);
    public static final RegistryObject<Item> RED_CUSHION = registerBlockItem("red_cushion", ModBlocks.RED_CUSHION);
    public static final RegistryObject<Item> BLACK_CUSHION =
            registerBlockItem("black_cushion", ModBlocks.BLACK_CUSHION);

    public static final RegistryObject<Item> STRAW_BED = ITEMS.register("straw_bed",
            () -> new BedItem(ModBlocks.STRAW_BED.get(), new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(16)));
    public static final RegistryObject<Item> BIG_BOOK = registerBlockItem("big_book", ModBlocks.BIG_BOOK);
    public static final RegistryObject<Item> GLOWING_BIG_BOOK =
            registerBlockItem("glowing_big_book", ModBlocks.GLOWING_BIG_BOOK);
    public static final RegistryObject<Item> WHITE_BIG_BOOK =
            registerBlockItem("white_big_book", ModBlocks.WHITE_BIG_BOOK);
    public static final RegistryObject<Item> ORANGE_BIG_BOOK =
            registerBlockItem("orange_big_book", ModBlocks.ORANGE_BIG_BOOK);
    public static final RegistryObject<Item> MAGENTA_BIG_BOOK =
            registerBlockItem("magenta_big_book", ModBlocks.MAGENTA_BIG_BOOK);
    public static final RegistryObject<Item> LIGHT_BLUE_BIG_BOOK =
            registerBlockItem("light_blue_big_book", ModBlocks.LIGHT_BLUE_BIG_BOOK);
    public static final RegistryObject<Item> YELLOW_BIG_BOOK =
            registerBlockItem("yellow_big_book", ModBlocks.YELLOW_BIG_BOOK);
    public static final RegistryObject<Item> LIME_BIG_BOOK =
            registerBlockItem("lime_big_book", ModBlocks.LIME_BIG_BOOK);
    public static final RegistryObject<Item> PINK_BIG_BOOK =
            registerBlockItem("pink_big_book", ModBlocks.PINK_BIG_BOOK);
    public static final RegistryObject<Item> GRAY_BIG_BOOK =
            registerBlockItem("gray_big_book", ModBlocks.GRAY_BIG_BOOK);
    public static final RegistryObject<Item> LIGHT_GRAY_BIG_BOOK =
            registerBlockItem("light_gray_big_book", ModBlocks.LIGHT_GRAY_BIG_BOOK);
    public static final RegistryObject<Item> CYAN_BIG_BOOK =
            registerBlockItem("cyan_big_book", ModBlocks.CYAN_BIG_BOOK);
    public static final RegistryObject<Item> PURPLE_BIG_BOOK =
            registerBlockItem("purple_big_book", ModBlocks.PURPLE_BIG_BOOK);
    public static final RegistryObject<Item> BLUE_BIG_BOOK =
            registerBlockItem("blue_big_book", ModBlocks.BLUE_BIG_BOOK);
    public static final RegistryObject<Item> BROWN_BIG_BOOK =
            registerBlockItem("brown_big_book", ModBlocks.BROWN_BIG_BOOK);
    public static final RegistryObject<Item> GREEN_BIG_BOOK =
            registerBlockItem("green_big_book", ModBlocks.GREEN_BIG_BOOK);
    public static final RegistryObject<Item> RED_BIG_BOOK = registerBlockItem("red_big_book", ModBlocks.RED_BIG_BOOK);
    public static final RegistryObject<Item> BLACK_BIG_BOOK =
            registerBlockItem("black_big_book", ModBlocks.BLACK_BIG_BOOK);
    public static final RegistryObject<Item> PALE_OAK_LOG = registerBlockItem("pale_oak_log", ModBlocks.PALE_OAK_LOG);
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_LOG =
            registerBlockItem("stripped_pale_oak_log", ModBlocks.STRIPPED_PALE_OAK_LOG);
    public static final RegistryObject<Item> PALE_OAK_WOOD =
            registerBlockItem("pale_oak_wood", ModBlocks.PALE_OAK_WOOD);
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_WOOD =
            registerBlockItem("stripped_pale_oak_wood", ModBlocks.STRIPPED_PALE_OAK_WOOD);
    public static final RegistryObject<Item> PALE_OAK_PLANKS =
            registerBlockItem("pale_oak_planks", ModBlocks.PALE_OAK_PLANKS);
    public static final RegistryObject<Item> PALE_OAK_STAIRS =
            registerBlockItem("pale_oak_stairs", ModBlocks.PALE_OAK_STAIRS);
    public static final RegistryObject<Item> PALE_OAK_SLAB =
            registerBlockItem("pale_oak_slab", ModBlocks.PALE_OAK_SLAB);
    public static final RegistryObject<Item> PALE_OAK_VERTICAL_SLAB =
            registerBlockItem("pale_oak_vertical_slab", ModBlocks.PALE_OAK_VERTICAL_SLAB);
    public static final RegistryObject<Item> PALE_OAK_FENCE =
            registerBlockItem("pale_oak_fence", ModBlocks.PALE_OAK_FENCE);
    public static final RegistryObject<Item> PALE_OAK_FENCE_GATE =
            registerBlockItem("pale_oak_fence_gate", ModBlocks.PALE_OAK_FENCE_GATE);
    public static final RegistryObject<Item> PALE_OAK_DOOR = ITEMS.register("pale_oak_door", () -> new DoubleHighBlockItem(ModBlocks.PALE_OAK_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PALE_OAK_TRAPDOOR =
            registerBlockItem("pale_oak_trapdoor", ModBlocks.PALE_OAK_TRAPDOOR);
    public static final RegistryObject<Item> PALE_OAK_BUTTON =
            registerBlockItem("pale_oak_button", ModBlocks.PALE_OAK_BUTTON);
    public static final RegistryObject<Item> PALE_OAK_PRESSURE_PLATE =
            registerBlockItem("pale_oak_pressure_plate", ModBlocks.PALE_OAK_PRESSURE_PLATE);
    public static final RegistryObject<Item> PALE_OAK_LEAVES =
            registerBlockItem("pale_oak_leaves", ModBlocks.PALE_OAK_LEAVES);
    public static final RegistryObject<Item> PALE_OAK_SAPLING =
            registerBlockItem("pale_oak_sapling", ModBlocks.PALE_OAK_SAPLING);
    public static final RegistryObject<Item> PALE_OAK_LOG_SLAB =
            registerBlockItem("pale_oak_log_slab", ModBlocks.PALE_OAK_LOG_SLAB);
    public static final RegistryObject<Item> PALE_OAK_LOG_VERTICAL_SLAB =
            registerBlockItem("pale_oak_log_vertical_slab", ModBlocks.PALE_OAK_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_LOG_SLAB =
            registerBlockItem("stripped_pale_oak_log_slab", ModBlocks.STRIPPED_PALE_OAK_LOG_SLAB);
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_LOG_VERTICAL_SLAB =
            registerBlockItem("stripped_pale_oak_log_vertical_slab", ModBlocks.STRIPPED_PALE_OAK_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> PALE_OAK_WOOD_SLAB =
            registerBlockItem("pale_oak_wood_slab", ModBlocks.PALE_OAK_WOOD_SLAB);
    public static final RegistryObject<Item> PALE_OAK_WOOD_VERTICAL_SLAB =
            registerBlockItem("pale_oak_wood_vertical_slab", ModBlocks.PALE_OAK_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_WOOD_SLAB =
            registerBlockItem("stripped_pale_oak_wood_slab", ModBlocks.STRIPPED_PALE_OAK_WOOD_SLAB);
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_WOOD_VERTICAL_SLAB =
            registerBlockItem("stripped_pale_oak_wood_vertical_slab", ModBlocks.STRIPPED_PALE_OAK_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> CHERRY_LOG = registerBlockItem("cherry_log", ModBlocks.CHERRY_LOG);
    public static final RegistryObject<Item> STRIPPED_CHERRY_LOG =
            registerBlockItem("stripped_cherry_log", ModBlocks.STRIPPED_CHERRY_LOG);
    public static final RegistryObject<Item> CHERRY_WOOD = registerBlockItem("cherry_wood", ModBlocks.CHERRY_WOOD);
    public static final RegistryObject<Item> STRIPPED_CHERRY_WOOD =
            registerBlockItem("stripped_cherry_wood", ModBlocks.STRIPPED_CHERRY_WOOD);
    public static final RegistryObject<Item> CHERRY_PLANKS =
            registerBlockItem("cherry_planks", ModBlocks.CHERRY_PLANKS);
    public static final RegistryObject<Item> CHERRY_STAIRS =
            registerBlockItem("cherry_stairs", ModBlocks.CHERRY_STAIRS);
    public static final RegistryObject<Item> CHERRY_SLAB = registerBlockItem("cherry_slab", ModBlocks.CHERRY_SLAB);
    public static final RegistryObject<Item> CHERRY_VERTICAL_SLAB =
            registerBlockItem("cherry_vertical_slab", ModBlocks.CHERRY_VERTICAL_SLAB);
    public static final RegistryObject<Item> CHERRY_FENCE = registerBlockItem("cherry_fence", ModBlocks.CHERRY_FENCE);
    public static final RegistryObject<Item> CHERRY_FENCE_GATE =
            registerBlockItem("cherry_fence_gate", ModBlocks.CHERRY_FENCE_GATE);
    public static final RegistryObject<Item> CHERRY_DOOR = ITEMS.register("cherry_door", () -> new DoubleHighBlockItem(ModBlocks.CHERRY_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CHERRY_TRAPDOOR =
            registerBlockItem("cherry_trapdoor", ModBlocks.CHERRY_TRAPDOOR);
    public static final RegistryObject<Item> CHERRY_BUTTON =
            registerBlockItem("cherry_button", ModBlocks.CHERRY_BUTTON);
    public static final RegistryObject<Item> CHERRY_PRESSURE_PLATE =
            registerBlockItem("cherry_pressure_plate", ModBlocks.CHERRY_PRESSURE_PLATE);
    public static final RegistryObject<Item> CHERRY_LEAVES =
            registerBlockItem("cherry_leaves", ModBlocks.CHERRY_LEAVES);
    public static final RegistryObject<Item> CHERRY_SAPLING =
            registerBlockItem("cherry_sapling", ModBlocks.CHERRY_SAPLING);
    public static final RegistryObject<Item> CHERRY_LOG_SLAB =
            registerBlockItem("cherry_log_slab", ModBlocks.CHERRY_LOG_SLAB);
    public static final RegistryObject<Item> CHERRY_LOG_VERTICAL_SLAB =
            registerBlockItem("cherry_log_vertical_slab", ModBlocks.CHERRY_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_CHERRY_LOG_SLAB =
            registerBlockItem("stripped_cherry_log_slab", ModBlocks.STRIPPED_CHERRY_LOG_SLAB);
    public static final RegistryObject<Item> STRIPPED_CHERRY_LOG_VERTICAL_SLAB =
            registerBlockItem("stripped_cherry_log_vertical_slab", ModBlocks.STRIPPED_CHERRY_LOG_VERTICAL_SLAB);
    public static final RegistryObject<Item> CHERRY_WOOD_SLAB =
            registerBlockItem("cherry_wood_slab", ModBlocks.CHERRY_WOOD_SLAB);
    public static final RegistryObject<Item> CHERRY_WOOD_VERTICAL_SLAB =
            registerBlockItem("cherry_wood_vertical_slab", ModBlocks.CHERRY_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> STRIPPED_CHERRY_WOOD_SLAB =
            registerBlockItem("stripped_cherry_wood_slab", ModBlocks.STRIPPED_CHERRY_WOOD_SLAB);
    public static final RegistryObject<Item> STRIPPED_CHERRY_WOOD_VERTICAL_SLAB =
            registerBlockItem("stripped_cherry_wood_vertical_slab", ModBlocks.STRIPPED_CHERRY_WOOD_VERTICAL_SLAB);
    public static final RegistryObject<Item> PALE_MOSS_BLOCK =
            registerBlockItem("pale_moss_block", ModBlocks.PALE_MOSS_BLOCK);
    public static final RegistryObject<Item> PALE_MOSS_BLOCK_SLAB =
            registerBlockItem("pale_moss_block_slab", ModBlocks.PALE_MOSS_BLOCK_SLAB);
    public static final RegistryObject<Item> PALE_MOSS_BLOCK_STAIRS =
            registerBlockItem("pale_moss_block_stairs", ModBlocks.PALE_MOSS_BLOCK_STAIRS);
    public static final RegistryObject<Item> PALE_MOSS_BLOCK_VERTICAL_SLAB =
            registerBlockItem("pale_moss_block_vertical_slab", ModBlocks.PALE_MOSS_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> PALE_MOSS_CARPET =
            registerBlockItem("pale_moss_carpet", ModBlocks.PALE_MOSS_CARPET);
    public static final RegistryObject<Item> PALE_MOSS_LAYERS =
            registerBlockItem("pale_moss_layers", ModBlocks.PALE_MOSS_LAYERS);
    public static final RegistryObject<Item> PALE_MOSS_OVERLAY =
            registerBlockItem("pale_moss_overlay", ModBlocks.PALE_MOSS_OVERLAY);
    public static final RegistryObject<Item> PALE_HANGING_MOSS =
            registerBlockItem("pale_hanging_moss", ModBlocks.PALE_HANGING_MOSS);
    public static final RegistryObject<Item> CREAKING_HEART =
            registerBlockItem("creaking_heart", ModBlocks.CREAKING_HEART);
    public static final RegistryObject<Item> RESIN_CLUMP = registerBlockItem("resin_clump", ModBlocks.RESIN_CLUMP);
    public static final RegistryObject<Item> RESIN_BLOCK = registerBlockItem("resin_block", ModBlocks.RESIN_BLOCK);
    public static final RegistryObject<Item> RESIN_BLOCK_SLAB =
            registerBlockItem("resin_block_slab", ModBlocks.RESIN_BLOCK_SLAB);
    public static final RegistryObject<Item> RESIN_BLOCK_STAIRS =
            registerBlockItem("resin_block_stairs", ModBlocks.RESIN_BLOCK_STAIRS);
    public static final RegistryObject<Item> RESIN_BLOCK_VERTICAL_SLAB =
            registerBlockItem("resin_block_vertical_slab", ModBlocks.RESIN_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> RESIN_BRICKS = registerBlockItem("resin_bricks", ModBlocks.RESIN_BRICKS);
    public static final RegistryObject<Item> RESIN_BRICK_SLAB =
            registerBlockItem("resin_brick_slab", ModBlocks.RESIN_BRICK_SLAB);
    public static final RegistryObject<Item> RESIN_BRICK_STAIRS =
            registerBlockItem("resin_brick_stairs", ModBlocks.RESIN_BRICK_STAIRS);
    public static final RegistryObject<Item> RESIN_BRICK_WALL =
            registerBlockItem("resin_brick_wall", ModBlocks.RESIN_BRICK_WALL);
    public static final RegistryObject<Item> RESIN_BRICK_VERTICAL_SLAB =
            registerBlockItem("resin_brick_vertical_slab", ModBlocks.RESIN_BRICK_VERTICAL_SLAB);
    public static final RegistryObject<Item> CHISELED_RESIN_BRICKS =
            registerBlockItem("chiseled_resin_bricks", ModBlocks.CHISELED_RESIN_BRICKS);
    public static final RegistryObject<Item> CLOSED_EYEBLOSSOM =
            registerBlockItem("closed_eyeblossom", ModBlocks.CLOSED_EYEBLOSSOM);
    public static final RegistryObject<Item> OPEN_EYEBLOSSOM =
            registerBlockItem("open_eyeblossom", ModBlocks.OPEN_EYEBLOSSOM);
    public static final RegistryObject<Item> WILDFLOWERS = registerBlockItem("wildflowers", ModBlocks.WILDFLOWERS);
    public static final RegistryObject<Item> LEAF_LITTER = registerBlockItem("leaf_litter", ModBlocks.LEAF_LITTER);
    public static final RegistryObject<Item> CACTUS_FLOWER =
            registerBlockItem("cactus_flower", ModBlocks.CACTUS_FLOWER);
    public static final RegistryObject<Item> BUSH = registerBlockItem("bush", ModBlocks.BUSH);
    public static final RegistryObject<Item> RED_BUSH = registerBlockItem("red_bush", ModBlocks.RED_BUSH);
    public static final RegistryObject<Item> WHITE_HAZE_BUSH = ITEMS.register("white_haze_bush", () -> new HazeBushItem(ModBlocks.WHITE_HAZE_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_HAZE_BUSH = ITEMS.register("orange_haze_bush", () -> new HazeBushItem(ModBlocks.ORANGE_HAZE_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_HAZE_BUSH = ITEMS.register("magenta_haze_bush", () -> new HazeBushItem(ModBlocks.MAGENTA_HAZE_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_HAZE_BUSH = ITEMS.register("light_blue_haze_bush", () -> new HazeBushItem(ModBlocks.LIGHT_BLUE_HAZE_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_HAZE_BUSH = ITEMS.register("yellow_haze_bush", () -> new HazeBushItem(ModBlocks.YELLOW_HAZE_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_HAZE_BUSH = ITEMS.register("lime_haze_bush", () -> new HazeBushItem(ModBlocks.LIME_HAZE_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_HAZE_BUSH = ITEMS.register("pink_haze_bush", () -> new HazeBushItem(ModBlocks.PINK_HAZE_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_HAZE_BUSH = ITEMS.register("gray_haze_bush", () -> new HazeBushItem(ModBlocks.GRAY_HAZE_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_HAZE_BUSH = ITEMS.register("light_gray_haze_bush", () -> new HazeBushItem(ModBlocks.LIGHT_GRAY_HAZE_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_HAZE_BUSH = ITEMS.register("cyan_haze_bush", () -> new HazeBushItem(ModBlocks.CYAN_HAZE_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_HAZE_BUSH = ITEMS.register("purple_haze_bush", () -> new HazeBushItem(ModBlocks.PURPLE_HAZE_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_HAZE_BUSH = ITEMS.register("blue_haze_bush", () -> new HazeBushItem(ModBlocks.BLUE_HAZE_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_HAZE_BUSH = ITEMS.register("brown_haze_bush", () -> new HazeBushItem(ModBlocks.BROWN_HAZE_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_HAZE_BUSH = ITEMS.register("green_haze_bush", () -> new HazeBushItem(ModBlocks.GREEN_HAZE_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_HAZE_BUSH = ITEMS.register("red_haze_bush", () -> new HazeBushItem(ModBlocks.RED_HAZE_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_HAZE_BUSH = ITEMS.register("black_haze_bush", () -> new HazeBushItem(ModBlocks.BLACK_HAZE_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FIREFLY_BUSH = registerBlockItem("firefly_bush", ModBlocks.FIREFLY_BUSH);
    public static final RegistryObject<Item> DRY_GRASS = registerBlockItem("dry_grass", ModBlocks.DRY_GRASS);
    public static final RegistryObject<Item> TALL_DRY_GRASS =
            registerBlockItem("tall_dry_grass", ModBlocks.TALL_DRY_GRASS);
    public static final RegistryObject<Item> GOLDEN_DANDELION =
            registerBlockItem("golden_dandelion", ModBlocks.GOLDEN_DANDELION);
    public static final RegistryObject<Item> OCHRE_FROGLIGHT =
            registerBlockItem("ochre_froglight", ModBlocks.OCHRE_FROGLIGHT);
    public static final RegistryObject<Item> PEARLESCENT_FROGLIGHT =
            registerBlockItem("pearlescent_froglight", ModBlocks.PEARLESCENT_FROGLIGHT);
    public static final RegistryObject<Item> VERDANT_FROGLIGHT =
            registerBlockItem("verdant_froglight", ModBlocks.VERDANT_FROGLIGHT);
    public static final RegistryObject<Item> GLEAMING_FROGLIGHT =
            registerBlockItem("gleaming_froglight", ModBlocks.GLEAMING_FROGLIGHT);
    public static final RegistryObject<Item> IVORY_FROGLIGHT =
            registerBlockItem("ivory_froglight", ModBlocks.IVORY_FROGLIGHT);
    public static final RegistryObject<Item> ASHEN_FROGLIGHT =
            registerBlockItem("ashen_froglight", ModBlocks.ASHEN_FROGLIGHT);
    public static final RegistryObject<Item> ONYX_FROGLIGHT =
            registerBlockItem("onyx_froglight", ModBlocks.ONYX_FROGLIGHT);
    public static final RegistryObject<Item> DUSKY_FROGLIGHT =
            registerBlockItem("dusky_froglight", ModBlocks.DUSKY_FROGLIGHT);
    public static final RegistryObject<Item> SCARLET_FROGLIGHT =
            registerBlockItem("scarlet_froglight", ModBlocks.SCARLET_FROGLIGHT);
    public static final RegistryObject<Item> RUSSET_FROGLIGHT =
            registerBlockItem("russet_froglight", ModBlocks.RUSSET_FROGLIGHT);
    public static final RegistryObject<Item> VERIDIAN_FROGLIGHT =
            registerBlockItem("veridian_froglight", ModBlocks.VERIDIAN_FROGLIGHT);
    public static final RegistryObject<Item> TIDAL_FROGLIGHT =
            registerBlockItem("tidal_froglight", ModBlocks.TIDAL_FROGLIGHT);
    public static final RegistryObject<Item> CERULEAN_FROGLIGHT =
            registerBlockItem("cerulean_froglight", ModBlocks.CERULEAN_FROGLIGHT);
    public static final RegistryObject<Item> AZURE_FROGLIGHT =
            registerBlockItem("azure_froglight", ModBlocks.AZURE_FROGLIGHT);
    public static final RegistryObject<Item> IRIDESCENT_FROGLIGHT =
            registerBlockItem("iridescent_froglight", ModBlocks.IRIDESCENT_FROGLIGHT);
    public static final RegistryObject<Item> CORALINE_FROGLIGHT =
            registerBlockItem("coraline_froglight", ModBlocks.CORALINE_FROGLIGHT);
    public static final RegistryObject<Item> SCULK = registerBlockItem("sculk", ModBlocks.SCULK);
    public static final RegistryObject<Item> SCULK_SLAB = registerBlockItem("sculk_slab", ModBlocks.SCULK_SLAB);
    public static final RegistryObject<Item> SCULK_STAIRS = registerBlockItem("sculk_stairs", ModBlocks.SCULK_STAIRS);
    public static final RegistryObject<Item> SCULK_WALL = registerBlockItem("sculk_wall", ModBlocks.SCULK_WALL);
    public static final RegistryObject<Item> SCULK_VERTICAL_SLAB =
            registerBlockItem("sculk_vertical_slab", ModBlocks.SCULK_VERTICAL_SLAB);
    public static final RegistryObject<Item> SCULK_VEIN = registerBlockItem("sculk_vein", ModBlocks.SCULK_VEIN);
    public static final RegistryObject<Item> SCULK_CATALYST =
            registerBlockItem("sculk_catalyst", ModBlocks.SCULK_CATALYST);
    public static final RegistryObject<Item> SCULK_SHRIEKER =
            registerBlockItem("sculk_shrieker", ModBlocks.SCULK_SHRIEKER);
    public static final RegistryObject<Item> SCULK_SENSOR = registerBlockItem("sculk_sensor", ModBlocks.SCULK_SENSOR);

    public static final RegistryObject<Item> COPPER_TORCH = ITEMS.register("copper_torch", () -> new StandingAndWallBlockItem(ModBlocks.COPPER_TORCH.get(), ModBlocks.COPPER_WALL_TORCH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> COPPER_ROD = registerBlockItem("copper_rod", ModBlocks.COPPER_ROD);
    public static final RegistryObject<Item> WAXED_COPPER_ROD =
            registerBlockItem("waxed_copper_rod", ModBlocks.WAXED_COPPER_ROD);
    public static final RegistryObject<Item> EXPOSED_COPPER_ROD =
            registerBlockItem("exposed_copper_rod", ModBlocks.EXPOSED_COPPER_ROD);
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_ROD =
            registerBlockItem("waxed_exposed_copper_rod", ModBlocks.WAXED_EXPOSED_COPPER_ROD);
    public static final RegistryObject<Item> WEATHERED_COPPER_ROD =
            registerBlockItem("weathered_copper_rod", ModBlocks.WEATHERED_COPPER_ROD);
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_ROD =
            registerBlockItem("waxed_weathered_copper_rod", ModBlocks.WAXED_WEATHERED_COPPER_ROD);
    public static final RegistryObject<Item> OXIDIZED_COPPER_ROD =
            registerBlockItem("oxidized_copper_rod", ModBlocks.OXIDIZED_COPPER_ROD);
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_ROD =
            registerBlockItem("waxed_oxidized_copper_rod", ModBlocks.WAXED_OXIDIZED_COPPER_ROD);
    public static final RegistryObject<Item> COPPER_LANTERN =
            registerBlockItem("copper_lantern", ModBlocks.COPPER_LANTERN);
    public static final RegistryObject<Item> WAXED_COPPER_LANTERN =
            registerBlockItem("waxed_copper_lantern", ModBlocks.WAXED_COPPER_LANTERN);
    public static final RegistryObject<Item> EXPOSED_COPPER_LANTERN =
            registerBlockItem("exposed_copper_lantern", ModBlocks.EXPOSED_COPPER_LANTERN);
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_LANTERN =
            registerBlockItem("waxed_exposed_copper_lantern", ModBlocks.WAXED_EXPOSED_COPPER_LANTERN);
    public static final RegistryObject<Item> WEATHERED_COPPER_LANTERN =
            registerBlockItem("weathered_copper_lantern", ModBlocks.WEATHERED_COPPER_LANTERN);
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_LANTERN =
            registerBlockItem("waxed_weathered_copper_lantern", ModBlocks.WAXED_WEATHERED_COPPER_LANTERN);
    public static final RegistryObject<Item> OXIDIZED_COPPER_LANTERN =
            registerBlockItem("oxidized_copper_lantern", ModBlocks.OXIDIZED_COPPER_LANTERN);
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_LANTERN =
            registerBlockItem("waxed_oxidized_copper_lantern", ModBlocks.WAXED_OXIDIZED_COPPER_LANTERN);
    public static final RegistryObject<Item> SLIT_COPPER = registerBlockItem("slit_copper", ModBlocks.SLIT_COPPER);
    public static final RegistryObject<Item> WAXED_SLIT_COPPER =
            registerBlockItem("waxed_slit_copper", ModBlocks.WAXED_SLIT_COPPER);
    public static final RegistryObject<Item> EXPOSED_SLIT_COPPER =
            registerBlockItem("exposed_slit_copper", ModBlocks.EXPOSED_SLIT_COPPER);
    public static final RegistryObject<Item> WAXED_EXPOSED_SLIT_COPPER =
            registerBlockItem("waxed_exposed_slit_copper", ModBlocks.WAXED_EXPOSED_SLIT_COPPER);
    public static final RegistryObject<Item> WEATHERED_SLIT_COPPER =
            registerBlockItem("weathered_slit_copper", ModBlocks.WEATHERED_SLIT_COPPER);
    public static final RegistryObject<Item> WAXED_WEATHERED_SLIT_COPPER =
            registerBlockItem("waxed_weathered_slit_copper", ModBlocks.WAXED_WEATHERED_SLIT_COPPER);
    public static final RegistryObject<Item> OXIDIZED_SLIT_COPPER =
            registerBlockItem("oxidized_slit_copper", ModBlocks.OXIDIZED_SLIT_COPPER);
    public static final RegistryObject<Item> WAXED_OXIDIZED_SLIT_COPPER =
            registerBlockItem("waxed_oxidized_slit_copper", ModBlocks.WAXED_OXIDIZED_SLIT_COPPER);
    public static final RegistryObject<Item> SLIT_COPPER_STAIRS =
            registerBlockItem("slit_copper_stairs", ModBlocks.SLIT_COPPER_STAIRS);
    public static final RegistryObject<Item> WAXED_SLIT_COPPER_STAIRS =
            registerBlockItem("waxed_slit_copper_stairs", ModBlocks.WAXED_SLIT_COPPER_STAIRS);
    public static final RegistryObject<Item> EXPOSED_SLIT_COPPER_STAIRS =
            registerBlockItem("exposed_slit_copper_stairs", ModBlocks.EXPOSED_SLIT_COPPER_STAIRS);
    public static final RegistryObject<Item> WAXED_EXPOSED_SLIT_COPPER_STAIRS =
            registerBlockItem("waxed_exposed_slit_copper_stairs", ModBlocks.WAXED_EXPOSED_SLIT_COPPER_STAIRS);
    public static final RegistryObject<Item> WEATHERED_SLIT_COPPER_STAIRS =
            registerBlockItem("weathered_slit_copper_stairs", ModBlocks.WEATHERED_SLIT_COPPER_STAIRS);
    public static final RegistryObject<Item> WAXED_WEATHERED_SLIT_COPPER_STAIRS =
            registerBlockItem("waxed_weathered_slit_copper_stairs", ModBlocks.WAXED_WEATHERED_SLIT_COPPER_STAIRS);
    public static final RegistryObject<Item> OXIDIZED_SLIT_COPPER_STAIRS =
            registerBlockItem("oxidized_slit_copper_stairs", ModBlocks.OXIDIZED_SLIT_COPPER_STAIRS);
    public static final RegistryObject<Item> WAXED_OXIDIZED_SLIT_COPPER_STAIRS =
            registerBlockItem("waxed_oxidized_slit_copper_stairs", ModBlocks.WAXED_OXIDIZED_SLIT_COPPER_STAIRS);
    public static final RegistryObject<Item> SLIT_COPPER_SLAB =
            registerBlockItem("slit_copper_slab", ModBlocks.SLIT_COPPER_SLAB);
    public static final RegistryObject<Item> WAXED_SLIT_COPPER_SLAB =
            registerBlockItem("waxed_slit_copper_slab", ModBlocks.WAXED_SLIT_COPPER_SLAB);
    public static final RegistryObject<Item> EXPOSED_SLIT_COPPER_SLAB =
            registerBlockItem("exposed_slit_copper_slab", ModBlocks.EXPOSED_SLIT_COPPER_SLAB);
    public static final RegistryObject<Item> WAXED_EXPOSED_SLIT_COPPER_SLAB =
            registerBlockItem("waxed_exposed_slit_copper_slab", ModBlocks.WAXED_EXPOSED_SLIT_COPPER_SLAB);
    public static final RegistryObject<Item> WEATHERED_SLIT_COPPER_SLAB =
            registerBlockItem("weathered_slit_copper_slab", ModBlocks.WEATHERED_SLIT_COPPER_SLAB);
    public static final RegistryObject<Item> WAXED_WEATHERED_SLIT_COPPER_SLAB =
            registerBlockItem("waxed_weathered_slit_copper_slab", ModBlocks.WAXED_WEATHERED_SLIT_COPPER_SLAB);
    public static final RegistryObject<Item> OXIDIZED_SLIT_COPPER_SLAB =
            registerBlockItem("oxidized_slit_copper_slab", ModBlocks.OXIDIZED_SLIT_COPPER_SLAB);
    public static final RegistryObject<Item> WAXED_OXIDIZED_SLIT_COPPER_SLAB =
            registerBlockItem("waxed_oxidized_slit_copper_slab", ModBlocks.WAXED_OXIDIZED_SLIT_COPPER_SLAB);
    public static final RegistryObject<Item> SLIT_COPPER_VERTICAL_SLAB =
            registerBlockItem("slit_copper_vertical_slab", ModBlocks.SLIT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> WAXED_SLIT_COPPER_VERTICAL_SLAB =
            registerBlockItem("waxed_slit_copper_vertical_slab", ModBlocks.WAXED_SLIT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> EXPOSED_SLIT_COPPER_VERTICAL_SLAB =
            registerBlockItem("exposed_slit_copper_vertical_slab", ModBlocks.EXPOSED_SLIT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> WAXED_EXPOSED_SLIT_COPPER_VERTICAL_SLAB =
            registerBlockItem("waxed_exposed_slit_copper_vertical_slab", ModBlocks.WAXED_EXPOSED_SLIT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> WEATHERED_SLIT_COPPER_VERTICAL_SLAB =
            registerBlockItem("weathered_slit_copper_vertical_slab", ModBlocks.WEATHERED_SLIT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> WAXED_WEATHERED_SLIT_COPPER_VERTICAL_SLAB =
            registerBlockItem("waxed_weathered_slit_copper_vertical_slab", ModBlocks.WAXED_WEATHERED_SLIT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> OXIDIZED_SLIT_COPPER_VERTICAL_SLAB =
            registerBlockItem("oxidized_slit_copper_vertical_slab", ModBlocks.OXIDIZED_SLIT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> WAXED_OXIDIZED_SLIT_COPPER_VERTICAL_SLAB =
            registerBlockItem("waxed_oxidized_slit_copper_vertical_slab", ModBlocks.WAXED_OXIDIZED_SLIT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> CHISELED_COPPER =
            registerBlockItem("chiseled_copper", ModBlocks.CHISELED_COPPER);
    public static final RegistryObject<Item> WAXED_CHISELED_COPPER =
            registerBlockItem("waxed_chiseled_copper", ModBlocks.WAXED_CHISELED_COPPER);
    public static final RegistryObject<Item> EXPOSED_CHISELED_COPPER =
            registerBlockItem("exposed_chiseled_copper", ModBlocks.EXPOSED_CHISELED_COPPER);
    public static final RegistryObject<Item> WAXED_EXPOSED_CHISELED_COPPER =
            registerBlockItem("waxed_exposed_chiseled_copper", ModBlocks.WAXED_EXPOSED_CHISELED_COPPER);
    public static final RegistryObject<Item> WEATHERED_CHISELED_COPPER =
            registerBlockItem("weathered_chiseled_copper", ModBlocks.WEATHERED_CHISELED_COPPER);
    public static final RegistryObject<Item> WAXED_WEATHERED_CHISELED_COPPER =
            registerBlockItem("waxed_weathered_chiseled_copper", ModBlocks.WAXED_WEATHERED_CHISELED_COPPER);
    public static final RegistryObject<Item> OXIDIZED_CHISELED_COPPER =
            registerBlockItem("oxidized_chiseled_copper", ModBlocks.OXIDIZED_CHISELED_COPPER);
    public static final RegistryObject<Item> WAXED_OXIDIZED_CHISELED_COPPER =
            registerBlockItem("waxed_oxidized_chiseled_copper", ModBlocks.WAXED_OXIDIZED_CHISELED_COPPER);
    public static final RegistryObject<Item> COPPER_GRATE = registerBlockItem("copper_grate", ModBlocks.COPPER_GRATE);
    public static final RegistryObject<Item> WAXED_COPPER_GRATE =
            registerBlockItem("waxed_copper_grate", ModBlocks.WAXED_COPPER_GRATE);
    public static final RegistryObject<Item> EXPOSED_COPPER_GRATE =
            registerBlockItem("exposed_copper_grate", ModBlocks.EXPOSED_COPPER_GRATE);
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_GRATE =
            registerBlockItem("waxed_exposed_copper_grate", ModBlocks.WAXED_EXPOSED_COPPER_GRATE);
    public static final RegistryObject<Item> WEATHERED_COPPER_GRATE =
            registerBlockItem("weathered_copper_grate", ModBlocks.WEATHERED_COPPER_GRATE);
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_GRATE =
            registerBlockItem("waxed_weathered_copper_grate", ModBlocks.WAXED_WEATHERED_COPPER_GRATE);
    public static final RegistryObject<Item> OXIDIZED_COPPER_GRATE =
            registerBlockItem("oxidized_copper_grate", ModBlocks.OXIDIZED_COPPER_GRATE);
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_GRATE =
            registerBlockItem("waxed_oxidized_copper_grate", ModBlocks.WAXED_OXIDIZED_COPPER_GRATE);
    public static final RegistryObject<Item> COPPER_BULB = registerBlockItem("copper_bulb", ModBlocks.COPPER_BULB);
    public static final RegistryObject<Item> WAXED_COPPER_BULB =
            registerBlockItem("waxed_copper_bulb", ModBlocks.WAXED_COPPER_BULB);
    public static final RegistryObject<Item> EXPOSED_COPPER_BULB =
            registerBlockItem("exposed_copper_bulb", ModBlocks.EXPOSED_COPPER_BULB);
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_BULB =
            registerBlockItem("waxed_exposed_copper_bulb", ModBlocks.WAXED_EXPOSED_COPPER_BULB);
    public static final RegistryObject<Item> WEATHERED_COPPER_BULB =
            registerBlockItem("weathered_copper_bulb", ModBlocks.WEATHERED_COPPER_BULB);
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_BULB =
            registerBlockItem("waxed_weathered_copper_bulb", ModBlocks.WAXED_WEATHERED_COPPER_BULB);
    public static final RegistryObject<Item> OXIDIZED_COPPER_BULB =
            registerBlockItem("oxidized_copper_bulb", ModBlocks.OXIDIZED_COPPER_BULB);
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_BULB =
            registerBlockItem("waxed_oxidized_copper_bulb", ModBlocks.WAXED_OXIDIZED_COPPER_BULB);

    public static final RegistryObject<Item> COPPER_DOOR = ITEMS.register("copper_door", () -> new DoubleHighBlockItem(ModBlocks.COPPER_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_COPPER_DOOR = ITEMS.register("waxed_copper_door", () -> new DoubleHighBlockItem(ModBlocks.WAXED_COPPER_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> EXPOSED_COPPER_DOOR = ITEMS.register("exposed_copper_door", () -> new DoubleHighBlockItem(ModBlocks.EXPOSED_COPPER_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_DOOR = ITEMS.register("waxed_exposed_copper_door", () -> new DoubleHighBlockItem(ModBlocks.WAXED_EXPOSED_COPPER_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WEATHERED_COPPER_DOOR = ITEMS.register("weathered_copper_door", () -> new DoubleHighBlockItem(ModBlocks.WEATHERED_COPPER_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_DOOR = ITEMS.register("waxed_weathered_copper_door", () -> new DoubleHighBlockItem(ModBlocks.WAXED_WEATHERED_COPPER_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OXIDIZED_COPPER_DOOR = ITEMS.register("oxidized_copper_door", () -> new DoubleHighBlockItem(ModBlocks.OXIDIZED_COPPER_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_DOOR = ITEMS.register("waxed_oxidized_copper_door", () -> new DoubleHighBlockItem(ModBlocks.WAXED_OXIDIZED_COPPER_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> COPPER_TRAPDOOR =
            registerBlockItem("copper_trapdoor", ModBlocks.COPPER_TRAPDOOR);
    public static final RegistryObject<Item> WAXED_COPPER_TRAPDOOR =
            registerBlockItem("waxed_copper_trapdoor", ModBlocks.WAXED_COPPER_TRAPDOOR);
    public static final RegistryObject<Item> EXPOSED_COPPER_TRAPDOOR =
            registerBlockItem("exposed_copper_trapdoor", ModBlocks.EXPOSED_COPPER_TRAPDOOR);
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_TRAPDOOR =
            registerBlockItem("waxed_exposed_copper_trapdoor", ModBlocks.WAXED_EXPOSED_COPPER_TRAPDOOR);
    public static final RegistryObject<Item> WEATHERED_COPPER_TRAPDOOR =
            registerBlockItem("weathered_copper_trapdoor", ModBlocks.WEATHERED_COPPER_TRAPDOOR);
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_TRAPDOOR =
            registerBlockItem("waxed_weathered_copper_trapdoor", ModBlocks.WAXED_WEATHERED_COPPER_TRAPDOOR);
    public static final RegistryObject<Item> OXIDIZED_COPPER_TRAPDOOR =
            registerBlockItem("oxidized_copper_trapdoor", ModBlocks.OXIDIZED_COPPER_TRAPDOOR);
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_TRAPDOOR =
            registerBlockItem("waxed_oxidized_copper_trapdoor", ModBlocks.WAXED_OXIDIZED_COPPER_TRAPDOOR);
    public static final RegistryObject<Item> COPPER_BARS = registerBlockItem("copper_bars", ModBlocks.COPPER_BARS);
    public static final RegistryObject<Item> WAXED_COPPER_BARS =
            registerBlockItem("waxed_copper_bars", ModBlocks.WAXED_COPPER_BARS);
    public static final RegistryObject<Item> EXPOSED_COPPER_BARS =
            registerBlockItem("exposed_copper_bars", ModBlocks.EXPOSED_COPPER_BARS);
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_BARS =
            registerBlockItem("waxed_exposed_copper_bars", ModBlocks.WAXED_EXPOSED_COPPER_BARS);
    public static final RegistryObject<Item> WEATHERED_COPPER_BARS =
            registerBlockItem("weathered_copper_bars", ModBlocks.WEATHERED_COPPER_BARS);
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_BARS =
            registerBlockItem("waxed_weathered_copper_bars", ModBlocks.WAXED_WEATHERED_COPPER_BARS);
    public static final RegistryObject<Item> OXIDIZED_COPPER_BARS =
            registerBlockItem("oxidized_copper_bars", ModBlocks.OXIDIZED_COPPER_BARS);
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_BARS =
            registerBlockItem("waxed_oxidized_copper_bars", ModBlocks.WAXED_OXIDIZED_COPPER_BARS);
    public static final RegistryObject<Item> GOLD_BARS = registerBlockItem("gold_bars", ModBlocks.GOLD_BARS);
    public static final RegistryObject<Item> STEEL_BARS = registerBlockItem("steel_bars", ModBlocks.STEEL_BARS);
    public static final RegistryObject<Item> IRON_MESH = registerBlockItem("iron_mesh", ModBlocks.IRON_MESH);
    public static final RegistryObject<Item> GOLD_MESH = registerBlockItem("gold_mesh", ModBlocks.GOLD_MESH);
    public static final RegistryObject<Item> STEEL_MESH = registerBlockItem("steel_mesh", ModBlocks.STEEL_MESH);
    public static final RegistryObject<Item> COPPER_MESH = registerBlockItem("copper_mesh", ModBlocks.COPPER_MESH);
    public static final RegistryObject<Item> WAXED_COPPER_MESH =
            registerBlockItem("waxed_copper_mesh", ModBlocks.WAXED_COPPER_MESH);
    public static final RegistryObject<Item> EXPOSED_COPPER_MESH =
            registerBlockItem("exposed_copper_mesh", ModBlocks.EXPOSED_COPPER_MESH);
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_MESH =
            registerBlockItem("waxed_exposed_copper_mesh", ModBlocks.WAXED_EXPOSED_COPPER_MESH);
    public static final RegistryObject<Item> WEATHERED_COPPER_MESH =
            registerBlockItem("weathered_copper_mesh", ModBlocks.WEATHERED_COPPER_MESH);
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_MESH =
            registerBlockItem("waxed_weathered_copper_mesh", ModBlocks.WAXED_WEATHERED_COPPER_MESH);
    public static final RegistryObject<Item> OXIDIZED_COPPER_MESH =
            registerBlockItem("oxidized_copper_mesh", ModBlocks.OXIDIZED_COPPER_MESH);
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_MESH =
            registerBlockItem("waxed_oxidized_copper_mesh", ModBlocks.WAXED_OXIDIZED_COPPER_MESH);
    public static final RegistryObject<Item> COPPER_BUTTON =
            registerBlockItem("copper_button", ModBlocks.COPPER_BUTTON);
    public static final RegistryObject<Item> WAXED_COPPER_BUTTON =
            registerBlockItem("waxed_copper_button", ModBlocks.WAXED_COPPER_BUTTON);
    public static final RegistryObject<Item> EXPOSED_COPPER_BUTTON =
            registerBlockItem("exposed_copper_button", ModBlocks.EXPOSED_COPPER_BUTTON);
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_BUTTON =
            registerBlockItem("waxed_exposed_copper_button", ModBlocks.WAXED_EXPOSED_COPPER_BUTTON);
    public static final RegistryObject<Item> WEATHERED_COPPER_BUTTON =
            registerBlockItem("weathered_copper_button", ModBlocks.WEATHERED_COPPER_BUTTON);
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_BUTTON =
            registerBlockItem("waxed_weathered_copper_button", ModBlocks.WAXED_WEATHERED_COPPER_BUTTON);
    public static final RegistryObject<Item> OXIDIZED_COPPER_BUTTON =
            registerBlockItem("oxidized_copper_button", ModBlocks.OXIDIZED_COPPER_BUTTON);
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_BUTTON =
            registerBlockItem("waxed_oxidized_copper_button", ModBlocks.WAXED_OXIDIZED_COPPER_BUTTON);
    public static final RegistryObject<Item> COPPER_BOLTS = registerBlockItem("copper_bolts", ModBlocks.COPPER_BOLTS);
    public static final RegistryObject<Item> WAXED_COPPER_BOLTS =
            registerBlockItem("waxed_copper_bolts", ModBlocks.WAXED_COPPER_BOLTS);
    public static final RegistryObject<Item> EXPOSED_COPPER_BOLTS =
            registerBlockItem("exposed_copper_bolts", ModBlocks.EXPOSED_COPPER_BOLTS);
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_BOLTS =
            registerBlockItem("waxed_exposed_copper_bolts", ModBlocks.WAXED_EXPOSED_COPPER_BOLTS);
    public static final RegistryObject<Item> WEATHERED_COPPER_BOLTS =
            registerBlockItem("weathered_copper_bolts", ModBlocks.WEATHERED_COPPER_BOLTS);
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_BOLTS =
            registerBlockItem("waxed_weathered_copper_bolts", ModBlocks.WAXED_WEATHERED_COPPER_BOLTS);
    public static final RegistryObject<Item> OXIDIZED_COPPER_BOLTS =
            registerBlockItem("oxidized_copper_bolts", ModBlocks.OXIDIZED_COPPER_BOLTS);
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_BOLTS =
            registerBlockItem("waxed_oxidized_copper_bolts", ModBlocks.WAXED_OXIDIZED_COPPER_BOLTS);
    public static final RegistryObject<Item> COPPER_PRESSURE_PLATE =
            registerBlockItem("copper_pressure_plate", ModBlocks.COPPER_PRESSURE_PLATE);
    public static final RegistryObject<Item> WAXED_COPPER_PRESSURE_PLATE =
            registerBlockItem("waxed_copper_pressure_plate", ModBlocks.WAXED_COPPER_PRESSURE_PLATE);
    public static final RegistryObject<Item> EXPOSED_COPPER_PRESSURE_PLATE =
            registerBlockItem("exposed_copper_pressure_plate", ModBlocks.EXPOSED_COPPER_PRESSURE_PLATE);
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_PRESSURE_PLATE =
            registerBlockItem("waxed_exposed_copper_pressure_plate", ModBlocks.WAXED_EXPOSED_COPPER_PRESSURE_PLATE);
    public static final RegistryObject<Item> WEATHERED_COPPER_PRESSURE_PLATE =
            registerBlockItem("weathered_copper_pressure_plate", ModBlocks.WEATHERED_COPPER_PRESSURE_PLATE);
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_PRESSURE_PLATE =
            registerBlockItem("waxed_weathered_copper_pressure_plate", ModBlocks.WAXED_WEATHERED_COPPER_PRESSURE_PLATE);
    public static final RegistryObject<Item> OXIDIZED_COPPER_PRESSURE_PLATE =
            registerBlockItem("oxidized_copper_pressure_plate", ModBlocks.OXIDIZED_COPPER_PRESSURE_PLATE);
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_PRESSURE_PLATE =
            registerBlockItem("waxed_oxidized_copper_pressure_plate", ModBlocks.WAXED_OXIDIZED_COPPER_PRESSURE_PLATE);

    public static final RegistryObject<Item> WANDERING_HOMEMAKER_SPAWN_EGG = ITEMS.register(
            "wandering_homemaker_spawn_egg",
            () -> new WanderingHomemakerSpawnEggItem(
                    ModEntities.WANDERING_HOMEMAKER,
                    0x5c3c24,
                    0xe4b484,
                    createBlockItemProperties()
            )
    );

    public static final RegistryObject<Item> FESTIVE_WANDERING_HOMEMAKER_SPAWN_EGG = ITEMS.register(
            "festive_wandering_homemaker_spawn_egg",
            () -> new FestiveWanderingHomemakerSpawnEggItem(
                    ModEntities.FESTIVE_WANDERING_HOMEMAKER,
                    0x990000,
                    0x009900,
                    createBlockItemProperties()
            )
    );

    public static final RegistryObject<Item> GLASS_JAR = ITEMS.register("glass_jar",
            () -> new GlassJarItem(ModBlocks.GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WHITE_GLASS_JAR = ITEMS.register("white_glass_jar",
            () -> new GlassJarItem(ModBlocks.WHITE_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_GLASS_JAR = ITEMS.register("orange_glass_jar",
            () -> new GlassJarItem(ModBlocks.ORANGE_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_GLASS_JAR = ITEMS.register("magenta_glass_jar",
            () -> new GlassJarItem(ModBlocks.MAGENTA_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_GLASS_JAR = ITEMS.register("light_blue_glass_jar",
            () -> new GlassJarItem(ModBlocks.LIGHT_BLUE_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_GLASS_JAR = ITEMS.register("yellow_glass_jar",
            () -> new GlassJarItem(ModBlocks.YELLOW_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_GLASS_JAR = ITEMS.register("lime_glass_jar",
            () -> new GlassJarItem(ModBlocks.LIME_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_GLASS_JAR = ITEMS.register("pink_glass_jar",
            () -> new GlassJarItem(ModBlocks.PINK_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_GLASS_JAR = ITEMS.register("gray_glass_jar",
            () -> new GlassJarItem(ModBlocks.GRAY_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_GLASS_JAR = ITEMS.register("light_gray_glass_jar",
            () -> new GlassJarItem(ModBlocks.LIGHT_GRAY_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_GLASS_JAR = ITEMS.register("cyan_glass_jar",
            () -> new GlassJarItem(ModBlocks.CYAN_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_GLASS_JAR = ITEMS.register("purple_glass_jar",
            () -> new GlassJarItem(ModBlocks.PURPLE_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_GLASS_JAR = ITEMS.register("blue_glass_jar",
            () -> new GlassJarItem(ModBlocks.BLUE_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_GLASS_JAR = ITEMS.register("brown_glass_jar",
            () -> new GlassJarItem(ModBlocks.BROWN_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_GLASS_JAR = ITEMS.register("green_glass_jar",
            () -> new GlassJarItem(ModBlocks.GREEN_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_GLASS_JAR = ITEMS.register("red_glass_jar",
            () -> new GlassJarItem(ModBlocks.RED_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_GLASS_JAR = ITEMS.register("black_glass_jar",
            () -> new GlassJarItem(ModBlocks.BLACK_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> TINTED_GLASS_JAR = ITEMS.register("tinted_glass_jar",
            () -> new GlassJarItem(ModBlocks.TINTED_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ACACIA_LADDER =
            registerBlockItem("acacia_ladder", ModBlocks.ACACIA_LADDER);
    public static final RegistryObject<Item> ASHPEN_BLACK_LADDER =
            registerBlockItem("ashpen_black_ladder", ModBlocks.ASHPEN_BLACK_LADDER);
    public static final RegistryObject<Item> ASHPEN_BLUE_LADDER =
            registerBlockItem("ashpen_blue_ladder", ModBlocks.ASHPEN_BLUE_LADDER);
    public static final RegistryObject<Item> ASHPEN_BROWN_LADDER =
            registerBlockItem("ashpen_brown_ladder", ModBlocks.ASHPEN_BROWN_LADDER);
    public static final RegistryObject<Item> ASHPEN_CYAN_LADDER =
            registerBlockItem("ashpen_cyan_ladder", ModBlocks.ASHPEN_CYAN_LADDER);
    public static final RegistryObject<Item> ASHPEN_GRAY_LADDER =
            registerBlockItem("ashpen_gray_ladder", ModBlocks.ASHPEN_GRAY_LADDER);
    public static final RegistryObject<Item> ASHPEN_GREEN_LADDER =
            registerBlockItem("ashpen_green_ladder", ModBlocks.ASHPEN_GREEN_LADDER);
    public static final RegistryObject<Item> ASHPEN_LIGHT_BLUE_LADDER =
            registerBlockItem("ashpen_light_blue_ladder", ModBlocks.ASHPEN_LIGHT_BLUE_LADDER);
    public static final RegistryObject<Item> ASHPEN_LIGHT_GRAY_LADDER =
            registerBlockItem("ashpen_light_gray_ladder", ModBlocks.ASHPEN_LIGHT_GRAY_LADDER);
    public static final RegistryObject<Item> ASHPEN_LIME_LADDER =
            registerBlockItem("ashpen_lime_ladder", ModBlocks.ASHPEN_LIME_LADDER);
    public static final RegistryObject<Item> ASHPEN_MAGENTA_LADDER =
            registerBlockItem("ashpen_magenta_ladder", ModBlocks.ASHPEN_MAGENTA_LADDER);
    public static final RegistryObject<Item> ASHPEN_ORANGE_LADDER =
            registerBlockItem("ashpen_orange_ladder", ModBlocks.ASHPEN_ORANGE_LADDER);
    public static final RegistryObject<Item> ASHPEN_PINK_LADDER =
            registerBlockItem("ashpen_pink_ladder", ModBlocks.ASHPEN_PINK_LADDER);
    public static final RegistryObject<Item> ASHPEN_PURPLE_LADDER =
            registerBlockItem("ashpen_purple_ladder", ModBlocks.ASHPEN_PURPLE_LADDER);
    public static final RegistryObject<Item> ASHPEN_RED_LADDER =
            registerBlockItem("ashpen_red_ladder", ModBlocks.ASHPEN_RED_LADDER);
    public static final RegistryObject<Item> ASHPEN_WHITE_LADDER =
            registerBlockItem("ashpen_white_ladder", ModBlocks.ASHPEN_WHITE_LADDER);
    public static final RegistryObject<Item> ASHPEN_YELLOW_LADDER =
            registerBlockItem("ashpen_yellow_ladder", ModBlocks.ASHPEN_YELLOW_LADDER);
    public static final RegistryObject<Item> BAMBOO_LADDER =
            registerBlockItem("bamboo_ladder", ModBlocks.BAMBOO_LADDER);
    public static final RegistryObject<Item> BIRCH_LADDER = registerBlockItem("birch_ladder", ModBlocks.BIRCH_LADDER);
    public static final RegistryObject<Item> CHERRY_LADDER =
            registerBlockItem("cherry_ladder", ModBlocks.CHERRY_LADDER);
    public static final RegistryObject<Item> CRIMSON_LADDER =
            registerBlockItem("crimson_ladder", ModBlocks.CRIMSON_LADDER);
    public static final RegistryObject<Item> DARK_OAK_LADDER =
            registerBlockItem("dark_oak_ladder", ModBlocks.DARK_OAK_LADDER);
    public static final RegistryObject<Item> JUNGLE_LADDER =
            registerBlockItem("jungle_ladder", ModBlocks.JUNGLE_LADDER);
    public static final RegistryObject<Item> MANGROVE_LADDER =
            registerBlockItem("mangrove_ladder", ModBlocks.MANGROVE_LADDER);
    public static final RegistryObject<Item> OAK_LADDER = registerBlockItem("oak_ladder", ModBlocks.OAK_LADDER);
    public static final RegistryObject<Item> PALE_OAK_LADDER =
            registerBlockItem("pale_oak_ladder", ModBlocks.PALE_OAK_LADDER);
    public static final RegistryObject<Item> POPLAR_LADDER =
            registerBlockItem("poplar_ladder", ModBlocks.POPLAR_LADDER);
    public static final RegistryObject<Item> SPRUCE_LADDER =
            registerBlockItem("spruce_ladder", ModBlocks.SPRUCE_LADDER);
    public static final RegistryObject<Item> STRIPPED_BAMBOO_LADDER =
            registerBlockItem("stripped_bamboo_ladder", ModBlocks.STRIPPED_BAMBOO_LADDER);
    public static final RegistryObject<Item> WARPED_LADDER =
            registerBlockItem("warped_ladder", ModBlocks.WARPED_LADDER);

    public static final RegistryObject<Item> COPPER_CHEST = ITEMS.register("copper_chest", () -> new CopperChestItem(ModBlocks.COPPER_CHEST.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> EXPOSED_COPPER_CHEST = ITEMS.register("exposed_copper_chest", () -> new CopperChestItem(ModBlocks.EXPOSED_COPPER_CHEST.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WEATHERED_COPPER_CHEST = ITEMS.register("weathered_copper_chest", () -> new CopperChestItem(ModBlocks.WEATHERED_COPPER_CHEST.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OXIDIZED_COPPER_CHEST = ITEMS.register("oxidized_copper_chest", () -> new CopperChestItem(ModBlocks.OXIDIZED_COPPER_CHEST.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_COPPER_CHEST = ITEMS.register("waxed_copper_chest", () -> new CopperChestItem(ModBlocks.WAXED_COPPER_CHEST.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_CHEST = ITEMS.register("waxed_exposed_copper_chest", () -> new CopperChestItem(ModBlocks.WAXED_EXPOSED_COPPER_CHEST.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_CHEST = ITEMS.register("waxed_weathered_copper_chest", () -> new CopperChestItem(ModBlocks.WAXED_WEATHERED_COPPER_CHEST.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_CHEST = ITEMS.register("waxed_oxidized_copper_chest", () -> new CopperChestItem(ModBlocks.WAXED_OXIDIZED_COPPER_CHEST.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WHITE_LAYERED_WOOL =
            registerBlockItem("white_layered_wool", ModBlocks.WHITE_LAYERED_WOOL);
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL =
            registerBlockItem("orange_layered_wool", ModBlocks.ORANGE_LAYERED_WOOL);
    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL =
            registerBlockItem("magenta_layered_wool", ModBlocks.MAGENTA_LAYERED_WOOL);
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL =
            registerBlockItem("light_blue_layered_wool", ModBlocks.LIGHT_BLUE_LAYERED_WOOL);
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL =
            registerBlockItem("yellow_layered_wool", ModBlocks.YELLOW_LAYERED_WOOL);
    public static final RegistryObject<Item> LIME_LAYERED_WOOL =
            registerBlockItem("lime_layered_wool", ModBlocks.LIME_LAYERED_WOOL);
    public static final RegistryObject<Item> PINK_LAYERED_WOOL =
            registerBlockItem("pink_layered_wool", ModBlocks.PINK_LAYERED_WOOL);
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL =
            registerBlockItem("gray_layered_wool", ModBlocks.GRAY_LAYERED_WOOL);
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL =
            registerBlockItem("light_gray_layered_wool", ModBlocks.LIGHT_GRAY_LAYERED_WOOL);
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL =
            registerBlockItem("cyan_layered_wool", ModBlocks.CYAN_LAYERED_WOOL);
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL =
            registerBlockItem("purple_layered_wool", ModBlocks.PURPLE_LAYERED_WOOL);
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL =
            registerBlockItem("blue_layered_wool", ModBlocks.BLUE_LAYERED_WOOL);
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL =
            registerBlockItem("brown_layered_wool", ModBlocks.BROWN_LAYERED_WOOL);
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL =
            registerBlockItem("green_layered_wool", ModBlocks.GREEN_LAYERED_WOOL);
    public static final RegistryObject<Item> RED_LAYERED_WOOL =
            registerBlockItem("red_layered_wool", ModBlocks.RED_LAYERED_WOOL);
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL =
            registerBlockItem("black_layered_wool", ModBlocks.BLACK_LAYERED_WOOL);
    public static final RegistryObject<Item> WHITE_LAYERED_WOOL_SLAB =
            registerBlockItem("white_layered_wool_slab", ModBlocks.WHITE_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> WHITE_LAYERED_WOOL_STAIRS =
            registerBlockItem("white_layered_wool_stairs", ModBlocks.WHITE_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> WHITE_LAYERED_WOOL_WALL =
            registerBlockItem("white_layered_wool_wall", ModBlocks.WHITE_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> WHITE_LAYERED_WOOL_CARPET =
            registerBlockItem("white_layered_wool_carpet", ModBlocks.WHITE_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> WHITE_LAYERED_WOOL_LAYERS =
            registerBlockItem("white_layered_wool_layers", ModBlocks.WHITE_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> WHITE_LAYERED_WOOL_VERTICAL_SLAB =
            registerBlockItem("white_layered_wool_vertical_slab", ModBlocks.WHITE_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL_SLAB =
            registerBlockItem("orange_layered_wool_slab", ModBlocks.ORANGE_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL_STAIRS =
            registerBlockItem("orange_layered_wool_stairs", ModBlocks.ORANGE_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL_WALL =
            registerBlockItem("orange_layered_wool_wall", ModBlocks.ORANGE_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL_CARPET =
            registerBlockItem("orange_layered_wool_carpet", ModBlocks.ORANGE_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL_LAYERS =
            registerBlockItem("orange_layered_wool_layers", ModBlocks.ORANGE_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL_VERTICAL_SLAB =
            registerBlockItem("orange_layered_wool_vertical_slab", ModBlocks.ORANGE_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL_SLAB =
            registerBlockItem("magenta_layered_wool_slab", ModBlocks.MAGENTA_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL_STAIRS =
            registerBlockItem("magenta_layered_wool_stairs", ModBlocks.MAGENTA_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL_WALL =
            registerBlockItem("magenta_layered_wool_wall", ModBlocks.MAGENTA_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL_CARPET =
            registerBlockItem("magenta_layered_wool_carpet", ModBlocks.MAGENTA_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL_LAYERS =
            registerBlockItem("magenta_layered_wool_layers", ModBlocks.MAGENTA_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL_VERTICAL_SLAB =
            registerBlockItem("magenta_layered_wool_vertical_slab", ModBlocks.MAGENTA_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL_SLAB =
            registerBlockItem("light_blue_layered_wool_slab", ModBlocks.LIGHT_BLUE_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL_STAIRS =
            registerBlockItem("light_blue_layered_wool_stairs", ModBlocks.LIGHT_BLUE_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL_WALL =
            registerBlockItem("light_blue_layered_wool_wall", ModBlocks.LIGHT_BLUE_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL_CARPET =
            registerBlockItem("light_blue_layered_wool_carpet", ModBlocks.LIGHT_BLUE_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL_LAYERS =
            registerBlockItem("light_blue_layered_wool_layers", ModBlocks.LIGHT_BLUE_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL_VERTICAL_SLAB =
            registerBlockItem("light_blue_layered_wool_vertical_slab", ModBlocks.LIGHT_BLUE_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL_SLAB =
            registerBlockItem("yellow_layered_wool_slab", ModBlocks.YELLOW_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL_STAIRS =
            registerBlockItem("yellow_layered_wool_stairs", ModBlocks.YELLOW_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL_WALL =
            registerBlockItem("yellow_layered_wool_wall", ModBlocks.YELLOW_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL_CARPET =
            registerBlockItem("yellow_layered_wool_carpet", ModBlocks.YELLOW_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL_LAYERS =
            registerBlockItem("yellow_layered_wool_layers", ModBlocks.YELLOW_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL_VERTICAL_SLAB =
            registerBlockItem("yellow_layered_wool_vertical_slab", ModBlocks.YELLOW_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIME_LAYERED_WOOL_SLAB =
            registerBlockItem("lime_layered_wool_slab", ModBlocks.LIME_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> LIME_LAYERED_WOOL_STAIRS =
            registerBlockItem("lime_layered_wool_stairs", ModBlocks.LIME_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> LIME_LAYERED_WOOL_WALL =
            registerBlockItem("lime_layered_wool_wall", ModBlocks.LIME_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> LIME_LAYERED_WOOL_CARPET =
            registerBlockItem("lime_layered_wool_carpet", ModBlocks.LIME_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> LIME_LAYERED_WOOL_LAYERS =
            registerBlockItem("lime_layered_wool_layers", ModBlocks.LIME_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> LIME_LAYERED_WOOL_VERTICAL_SLAB =
            registerBlockItem("lime_layered_wool_vertical_slab", ModBlocks.LIME_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> PINK_LAYERED_WOOL_SLAB =
            registerBlockItem("pink_layered_wool_slab", ModBlocks.PINK_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> PINK_LAYERED_WOOL_STAIRS =
            registerBlockItem("pink_layered_wool_stairs", ModBlocks.PINK_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> PINK_LAYERED_WOOL_WALL =
            registerBlockItem("pink_layered_wool_wall", ModBlocks.PINK_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> PINK_LAYERED_WOOL_CARPET =
            registerBlockItem("pink_layered_wool_carpet", ModBlocks.PINK_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> PINK_LAYERED_WOOL_LAYERS =
            registerBlockItem("pink_layered_wool_layers", ModBlocks.PINK_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> PINK_LAYERED_WOOL_VERTICAL_SLAB =
            registerBlockItem("pink_layered_wool_vertical_slab", ModBlocks.PINK_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL_SLAB =
            registerBlockItem("gray_layered_wool_slab", ModBlocks.GRAY_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL_STAIRS =
            registerBlockItem("gray_layered_wool_stairs", ModBlocks.GRAY_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL_WALL =
            registerBlockItem("gray_layered_wool_wall", ModBlocks.GRAY_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL_CARPET =
            registerBlockItem("gray_layered_wool_carpet", ModBlocks.GRAY_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL_LAYERS =
            registerBlockItem("gray_layered_wool_layers", ModBlocks.GRAY_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL_VERTICAL_SLAB =
            registerBlockItem("gray_layered_wool_vertical_slab", ModBlocks.GRAY_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL_SLAB =
            registerBlockItem("light_gray_layered_wool_slab", ModBlocks.LIGHT_GRAY_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL_STAIRS =
            registerBlockItem("light_gray_layered_wool_stairs", ModBlocks.LIGHT_GRAY_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL_WALL =
            registerBlockItem("light_gray_layered_wool_wall", ModBlocks.LIGHT_GRAY_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL_CARPET =
            registerBlockItem("light_gray_layered_wool_carpet", ModBlocks.LIGHT_GRAY_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL_LAYERS =
            registerBlockItem("light_gray_layered_wool_layers", ModBlocks.LIGHT_GRAY_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL_VERTICAL_SLAB =
            registerBlockItem("light_gray_layered_wool_vertical_slab", ModBlocks.LIGHT_GRAY_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL_SLAB =
            registerBlockItem("cyan_layered_wool_slab", ModBlocks.CYAN_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL_STAIRS =
            registerBlockItem("cyan_layered_wool_stairs", ModBlocks.CYAN_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL_WALL =
            registerBlockItem("cyan_layered_wool_wall", ModBlocks.CYAN_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL_CARPET =
            registerBlockItem("cyan_layered_wool_carpet", ModBlocks.CYAN_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL_LAYERS =
            registerBlockItem("cyan_layered_wool_layers", ModBlocks.CYAN_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL_VERTICAL_SLAB =
            registerBlockItem("cyan_layered_wool_vertical_slab", ModBlocks.CYAN_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL_SLAB =
            registerBlockItem("purple_layered_wool_slab", ModBlocks.PURPLE_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL_STAIRS =
            registerBlockItem("purple_layered_wool_stairs", ModBlocks.PURPLE_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL_WALL =
            registerBlockItem("purple_layered_wool_wall", ModBlocks.PURPLE_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL_CARPET =
            registerBlockItem("purple_layered_wool_carpet", ModBlocks.PURPLE_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL_LAYERS =
            registerBlockItem("purple_layered_wool_layers", ModBlocks.PURPLE_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL_VERTICAL_SLAB =
            registerBlockItem("purple_layered_wool_vertical_slab", ModBlocks.PURPLE_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL_SLAB =
            registerBlockItem("blue_layered_wool_slab", ModBlocks.BLUE_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL_STAIRS =
            registerBlockItem("blue_layered_wool_stairs", ModBlocks.BLUE_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL_WALL =
            registerBlockItem("blue_layered_wool_wall", ModBlocks.BLUE_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL_CARPET =
            registerBlockItem("blue_layered_wool_carpet", ModBlocks.BLUE_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL_LAYERS =
            registerBlockItem("blue_layered_wool_layers", ModBlocks.BLUE_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL_VERTICAL_SLAB =
            registerBlockItem("blue_layered_wool_vertical_slab", ModBlocks.BLUE_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL_SLAB =
            registerBlockItem("brown_layered_wool_slab", ModBlocks.BROWN_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL_STAIRS =
            registerBlockItem("brown_layered_wool_stairs", ModBlocks.BROWN_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL_WALL =
            registerBlockItem("brown_layered_wool_wall", ModBlocks.BROWN_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL_CARPET =
            registerBlockItem("brown_layered_wool_carpet", ModBlocks.BROWN_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL_LAYERS =
            registerBlockItem("brown_layered_wool_layers", ModBlocks.BROWN_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL_VERTICAL_SLAB =
            registerBlockItem("brown_layered_wool_vertical_slab", ModBlocks.BROWN_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL_SLAB =
            registerBlockItem("green_layered_wool_slab", ModBlocks.GREEN_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL_STAIRS =
            registerBlockItem("green_layered_wool_stairs", ModBlocks.GREEN_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL_WALL =
            registerBlockItem("green_layered_wool_wall", ModBlocks.GREEN_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL_CARPET =
            registerBlockItem("green_layered_wool_carpet", ModBlocks.GREEN_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL_LAYERS =
            registerBlockItem("green_layered_wool_layers", ModBlocks.GREEN_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL_VERTICAL_SLAB =
            registerBlockItem("green_layered_wool_vertical_slab", ModBlocks.GREEN_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_LAYERED_WOOL_SLAB =
            registerBlockItem("red_layered_wool_slab", ModBlocks.RED_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> RED_LAYERED_WOOL_STAIRS =
            registerBlockItem("red_layered_wool_stairs", ModBlocks.RED_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> RED_LAYERED_WOOL_WALL =
            registerBlockItem("red_layered_wool_wall", ModBlocks.RED_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> RED_LAYERED_WOOL_CARPET =
            registerBlockItem("red_layered_wool_carpet", ModBlocks.RED_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> RED_LAYERED_WOOL_LAYERS =
            registerBlockItem("red_layered_wool_layers", ModBlocks.RED_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> RED_LAYERED_WOOL_VERTICAL_SLAB =
            registerBlockItem("red_layered_wool_vertical_slab", ModBlocks.RED_LAYERED_WOOL_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL_SLAB =
            registerBlockItem("black_layered_wool_slab", ModBlocks.BLACK_LAYERED_WOOL_SLAB);
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL_STAIRS =
            registerBlockItem("black_layered_wool_stairs", ModBlocks.BLACK_LAYERED_WOOL_STAIRS);
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL_WALL =
            registerBlockItem("black_layered_wool_wall", ModBlocks.BLACK_LAYERED_WOOL_WALL);
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL_CARPET =
            registerBlockItem("black_layered_wool_carpet", ModBlocks.BLACK_LAYERED_WOOL_CARPET);
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL_LAYERS =
            registerBlockItem("black_layered_wool_layers", ModBlocks.BLACK_LAYERED_WOOL_LAYERS);
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL_VERTICAL_SLAB =
            registerBlockItem("black_layered_wool_vertical_slab", ModBlocks.BLACK_LAYERED_WOOL_VERTICAL_SLAB);


public static final RegistryObject<Item> FLAMING_STEEL_INGOT = ITEMS.register("flaming_steel_ingot",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB)));
    public static final RegistryObject<Item> FLAMING_STEEL_NUGGET = ITEMS.register("flaming_steel_nugget",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB)));
    public static final RegistryObject<Item> FLAMING_STEEL_BLOCK =
            registerBlockItem("flaming_steel_block", ModBlocks.FLAMING_STEEL_BLOCK);
    public static final RegistryObject<Item> FLAMING_STEEL_BLOCK_SLAB =
            registerBlockItem("flaming_steel_block_slab", ModBlocks.FLAMING_STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> FLAMING_STEEL_BLOCK_VERTICAL_SLAB =
            registerBlockItem("flaming_steel_block_vertical_slab", ModBlocks.FLAMING_STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> FLAMING_STEEL_BLOCK_STAIRS =
            registerBlockItem("flaming_steel_block_stairs", ModBlocks.FLAMING_STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> FLAMING_STEEL_BLOCK_WALL =
            registerBlockItem("flaming_steel_block_wall", ModBlocks.FLAMING_STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> FLAMING_CUT_STEEL =
            registerBlockItem("flaming_cut_steel", ModBlocks.FLAMING_CUT_STEEL);
    public static final RegistryObject<Item> FLAMING_CUT_STEEL_SLAB =
            registerBlockItem("flaming_cut_steel_slab", ModBlocks.FLAMING_CUT_STEEL_SLAB);
    public static final RegistryObject<Item> FLAMING_CUT_STEEL_VERTICAL_SLAB =
            registerBlockItem("flaming_cut_steel_vertical_slab", ModBlocks.FLAMING_CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> FLAMING_CUT_STEEL_STAIRS =
            registerBlockItem("flaming_cut_steel_stairs", ModBlocks.FLAMING_CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> FLAMING_CUT_STEEL_WALL =
            registerBlockItem("flaming_cut_steel_wall", ModBlocks.FLAMING_CUT_STEEL_WALL);
    public static final RegistryObject<Item> FLAMING_POLISHED_STEEL =
            registerBlockItem("flaming_polished_steel", ModBlocks.FLAMING_POLISHED_STEEL);
    public static final RegistryObject<Item> FLAMING_POLISHED_STEEL_SLAB =
            registerBlockItem("flaming_polished_steel_slab", ModBlocks.FLAMING_POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> FLAMING_POLISHED_STEEL_VERTICAL_SLAB =
            registerBlockItem("flaming_polished_steel_vertical_slab", ModBlocks.FLAMING_POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> FLAMING_POLISHED_STEEL_STAIRS =
            registerBlockItem("flaming_polished_steel_stairs", ModBlocks.FLAMING_POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> FLAMING_POLISHED_STEEL_WALL =
            registerBlockItem("flaming_polished_steel_wall", ModBlocks.FLAMING_POLISHED_STEEL_WALL);
    public static final RegistryObject<Item> FLAMING_PRESSED_STEEL =
            registerBlockItem("flaming_pressed_steel", ModBlocks.FLAMING_PRESSED_STEEL);
    public static final RegistryObject<Item> FLAMING_PRESSED_STEEL_SLAB =
            registerBlockItem("flaming_pressed_steel_slab", ModBlocks.FLAMING_PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> FLAMING_PRESSED_STEEL_VERTICAL_SLAB =
            registerBlockItem("flaming_pressed_steel_vertical_slab", ModBlocks.FLAMING_PRESSED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> FLAMING_PRESSED_STEEL_STAIRS =
            registerBlockItem("flaming_pressed_steel_stairs", ModBlocks.FLAMING_PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> FLAMING_PRESSED_STEEL_WALL =
            registerBlockItem("flaming_pressed_steel_wall", ModBlocks.FLAMING_PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> FLAMING_STEEL_CASING =
            registerBlockItem("flaming_steel_casing", ModBlocks.FLAMING_STEEL_CASING);
    public static final RegistryObject<Item> FLAMING_STEEL_GRATE =
            registerBlockItem("flaming_steel_grate", ModBlocks.FLAMING_STEEL_GRATE);
    public static final RegistryObject<Item> FLAMING_STEEL_PILLAR =
            registerBlockItem("flaming_steel_pillar", ModBlocks.FLAMING_STEEL_PILLAR);
    public static final RegistryObject<Item> FLAMING_BOLTED_STEEL_PILLAR =
            registerBlockItem("flaming_bolted_steel_pillar", ModBlocks.FLAMING_BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> FLAMING_STEEL_BOLTS =
            registerBlockItem("flaming_steel_bolts", ModBlocks.FLAMING_STEEL_BOLTS);
    public static final RegistryObject<Item> FLAMING_STEEL_DOOR = ITEMS.register("flaming_steel_door",
            () -> new DoubleHighBlockItem(ModBlocks.FLAMING_STEEL_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FLAMING_STEEL_TRAPDOOR =
            registerBlockItem("flaming_steel_trapdoor", ModBlocks.FLAMING_STEEL_TRAPDOOR);
    public static final RegistryObject<Item> FLAMING_STEEL_BUTTON =
            registerBlockItem("flaming_steel_button", ModBlocks.FLAMING_STEEL_BUTTON);
    public static final RegistryObject<Item> FLAMING_STEEL_PRESSURE_PLATE =
            registerBlockItem("flaming_steel_pressure_plate", ModBlocks.FLAMING_STEEL_PRESSURE_PLATE);
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_BLOCK =
            registerBlockItem("magenta_flaming_steel_block", ModBlocks.MAGENTA_FLAMING_STEEL_BLOCK);
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_BLOCK_SLAB =
            registerBlockItem("magenta_flaming_steel_block_slab", ModBlocks.MAGENTA_FLAMING_STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_BLOCK_VERTICAL_SLAB =
            registerBlockItem("magenta_flaming_steel_block_vertical_slab", ModBlocks.MAGENTA_FLAMING_STEEL_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_BLOCK_STAIRS =
            registerBlockItem("magenta_flaming_steel_block_stairs", ModBlocks.MAGENTA_FLAMING_STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_BLOCK_WALL =
            registerBlockItem("magenta_flaming_steel_block_wall", ModBlocks.MAGENTA_FLAMING_STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> MAGENTA_FLAMING_CUT_STEEL =
            registerBlockItem("magenta_flaming_cut_steel", ModBlocks.MAGENTA_FLAMING_CUT_STEEL);
    public static final RegistryObject<Item> MAGENTA_FLAMING_CUT_STEEL_SLAB =
            registerBlockItem("magenta_flaming_cut_steel_slab", ModBlocks.MAGENTA_FLAMING_CUT_STEEL_SLAB);
    public static final RegistryObject<Item> MAGENTA_FLAMING_CUT_STEEL_VERTICAL_SLAB =
            registerBlockItem("magenta_flaming_cut_steel_vertical_slab", ModBlocks.MAGENTA_FLAMING_CUT_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> MAGENTA_FLAMING_CUT_STEEL_STAIRS =
            registerBlockItem("magenta_flaming_cut_steel_stairs", ModBlocks.MAGENTA_FLAMING_CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> MAGENTA_FLAMING_CUT_STEEL_WALL =
            registerBlockItem("magenta_flaming_cut_steel_wall", ModBlocks.MAGENTA_FLAMING_CUT_STEEL_WALL);
    public static final RegistryObject<Item> MAGENTA_FLAMING_POLISHED_STEEL =
            registerBlockItem("magenta_flaming_polished_steel", ModBlocks.MAGENTA_FLAMING_POLISHED_STEEL);
    public static final RegistryObject<Item> MAGENTA_FLAMING_POLISHED_STEEL_SLAB =
            registerBlockItem("magenta_flaming_polished_steel_slab", ModBlocks.MAGENTA_FLAMING_POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> MAGENTA_FLAMING_POLISHED_STEEL_VERTICAL_SLAB =
            registerBlockItem("magenta_flaming_polished_steel_vertical_slab", ModBlocks.MAGENTA_FLAMING_POLISHED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> MAGENTA_FLAMING_POLISHED_STEEL_STAIRS =
            registerBlockItem("magenta_flaming_polished_steel_stairs", ModBlocks.MAGENTA_FLAMING_POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> MAGENTA_FLAMING_POLISHED_STEEL_WALL =
            registerBlockItem("magenta_flaming_polished_steel_wall", ModBlocks.MAGENTA_FLAMING_POLISHED_STEEL_WALL);
}
