package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.ModBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BedItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(
            ForgeRegistries.ITEMS,
            BuildScape.MODID
    );

    private static Item.Properties createBlockItemProperties() {
        return new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB);
    }


    public static final RegistryObject<Item> BLACK_SAND = ITEMS.register(
            "black_sand",
            () -> new BlockItem(ModBlocks.BLACK_SAND.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BLUE_SAND = ITEMS.register(
            "blue_sand",
            () -> new BlockItem(ModBlocks.BLUE_SAND.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> GREEN_SAND = ITEMS.register(
            "green_sand",
            () -> new BlockItem(ModBlocks.GREEN_SAND.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> ORANGE_SAND = ITEMS.register(
            "orange_sand",
            () ->
                    new BlockItem(ModBlocks.ORANGE_SAND.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> PINK_SAND = ITEMS.register(
            "pink_sand",
            () -> new BlockItem(ModBlocks.PINK_SAND.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> RED_SAND = ITEMS.register(
            "red_sand",
            () -> new BlockItem(ModBlocks.RED_SAND.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> WHITE_SAND = ITEMS.register(
            "white_sand",
            () -> new BlockItem(ModBlocks.WHITE_SAND.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> YELLOW_SAND = ITEMS.register(
            "yellow_sand",
            () ->
                    new BlockItem(ModBlocks.YELLOW_SAND.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BLACK_SANDSTONE = ITEMS.register(
            "black_sandstone",
            () ->
                    new BlockItem(
                    ModBlocks.BLACK_SANDSTONE.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BLUE_SANDSTONE = ITEMS.register(
            "blue_sandstone",
            () ->
                    new BlockItem(ModBlocks.BLUE_SANDSTONE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> GREEN_SANDSTONE = ITEMS.register(
            "green_sandstone",
            () ->
                    new BlockItem(
                    ModBlocks.GREEN_SANDSTONE.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> ORANGE_SANDSTONE = ITEMS.register(
            "orange_sandstone",
            () ->
                    new BlockItem(
                    ModBlocks.ORANGE_SANDSTONE.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> PINK_SANDSTONE = ITEMS.register(
            "pink_sandstone",
            () ->
                    new BlockItem(ModBlocks.PINK_SANDSTONE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> RED_SANDSTONE = ITEMS.register(
            "red_sandstone",
            () ->
                    new BlockItem(ModBlocks.RED_SANDSTONE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> WHITE_SANDSTONE = ITEMS.register(
            "white_sandstone",
            () ->
                    new BlockItem(
                    ModBlocks.WHITE_SANDSTONE.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> YELLOW_SANDSTONE = ITEMS.register(
            "yellow_sandstone",
            () ->
                    new BlockItem(
                    ModBlocks.YELLOW_SANDSTONE.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BLACK_SMOOTH_SANDSTONE =
            ITEMS.register("black_smooth_sandstone", () ->
                    new BlockItem(
                    ModBlocks.BLACK_SMOOTH_SANDSTONE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BLUE_SMOOTH_SANDSTONE =
            ITEMS.register("blue_smooth_sandstone", () ->
                    new BlockItem(
                    ModBlocks.BLUE_SMOOTH_SANDSTONE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> GREEN_SMOOTH_SANDSTONE =
            ITEMS.register("green_smooth_sandstone", () ->
                    new BlockItem(
                    ModBlocks.GREEN_SMOOTH_SANDSTONE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> ORANGE_SMOOTH_SANDSTONE =
            ITEMS.register("orange_smooth_sandstone", () ->
                    new BlockItem(
                    ModBlocks.ORANGE_SMOOTH_SANDSTONE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> PINK_SMOOTH_SANDSTONE =
            ITEMS.register("pink_smooth_sandstone", () ->
                    new BlockItem(
                    ModBlocks.PINK_SMOOTH_SANDSTONE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> RED_SMOOTH_SANDSTONE =
            ITEMS.register("red_smooth_sandstone", () ->
                    new BlockItem(
                    ModBlocks.RED_SMOOTH_SANDSTONE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> WHITE_SMOOTH_SANDSTONE =
            ITEMS.register("white_smooth_sandstone", () ->
                    new BlockItem(
                    ModBlocks.WHITE_SMOOTH_SANDSTONE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> YELLOW_SMOOTH_SANDSTONE =
            ITEMS.register("yellow_smooth_sandstone", () ->
                    new BlockItem(
                    ModBlocks.YELLOW_SMOOTH_SANDSTONE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BLACK_TILES = ITEMS.register(
            "black_tiles",
            () ->
                    new BlockItem(ModBlocks.BLACK_TILES.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BLUE_TILES = ITEMS.register(
            "blue_tiles",
            () -> new BlockItem(ModBlocks.BLUE_TILES.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BROWN_TILES = ITEMS.register(
            "brown_tiles",
            () ->
                    new BlockItem(ModBlocks.BROWN_TILES.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CYAN_TILES = ITEMS.register(
            "cyan_tiles",
            () -> new BlockItem(ModBlocks.CYAN_TILES.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> GRAY_TILES = ITEMS.register(
            "gray_tiles",
            () -> new BlockItem(ModBlocks.GRAY_TILES.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> GREEN_TILES = ITEMS.register(
            "green_tiles",
            () ->
                    new BlockItem(ModBlocks.GREEN_TILES.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> LIGHT_BLUE_TILES = ITEMS.register(
            "light_blue_tiles",
            () ->
                    new BlockItem(
                    ModBlocks.LIGHT_BLUE_TILES.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> LIGHT_GRAY_TILES = ITEMS.register(
            "light_gray_tiles",
            () ->
                    new BlockItem(
                    ModBlocks.LIGHT_GRAY_TILES.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> LIME_TILES = ITEMS.register(
            "lime_tiles",
            () -> new BlockItem(ModBlocks.LIME_TILES.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> MAGENTA_TILES = ITEMS.register(
            "magenta_tiles",
            () ->
                    new BlockItem(ModBlocks.MAGENTA_TILES.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> ORANGE_TILES = ITEMS.register(
            "orange_tiles",
            () ->
                    new BlockItem(ModBlocks.ORANGE_TILES.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> PINK_TILES = ITEMS.register(
            "pink_tiles",
            () -> new BlockItem(ModBlocks.PINK_TILES.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> PURPLE_TILES = ITEMS.register(
            "purple_tiles",
            () ->
                    new BlockItem(ModBlocks.PURPLE_TILES.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> RED_TILES = ITEMS.register(
            "red_tiles",
            () -> new BlockItem(ModBlocks.RED_TILES.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> WHITE_TILES = ITEMS.register(
            "white_tiles",
            () ->
                    new BlockItem(ModBlocks.WHITE_TILES.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> YELLOW_TILES = ITEMS.register(
            "yellow_tiles",
            () ->
                    new BlockItem(ModBlocks.YELLOW_TILES.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BLACK_MOSAIC_GLASS = ITEMS.register(
            "black_mosaic_glass",
            () ->
                    new BlockItem(
                    ModBlocks.BLACK_MOSAIC_GLASS.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BLUE_MOSAIC_GLASS = ITEMS.register(
            "blue_mosaic_glass",
            () ->
                    new BlockItem(
                    ModBlocks.BLUE_MOSAIC_GLASS.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BROWN_MOSAIC_GLASS = ITEMS.register(
            "brown_mosaic_glass",
            () ->
                    new BlockItem(
                    ModBlocks.BROWN_MOSAIC_GLASS.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> CYAN_MOSAIC_GLASS = ITEMS.register(
            "cyan_mosaic_glass",
            () ->
                    new BlockItem(
                    ModBlocks.CYAN_MOSAIC_GLASS.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> GRAY_MOSAIC_GLASS = ITEMS.register(
            "gray_mosaic_glass",
            () ->
                    new BlockItem(
                    ModBlocks.GRAY_MOSAIC_GLASS.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> GREEN_MOSAIC_GLASS = ITEMS.register(
            "green_mosaic_glass",
            () ->
                    new BlockItem(
                    ModBlocks.GREEN_MOSAIC_GLASS.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> LIGHT_BLUE_MOSAIC_GLASS =
            ITEMS.register("light_blue_mosaic_glass", () ->
                    new BlockItem(
                    ModBlocks.LIGHT_BLUE_MOSAIC_GLASS.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> LIGHT_GRAY_MOSAIC_GLASS =
            ITEMS.register("light_gray_mosaic_glass", () ->
                    new BlockItem(
                    ModBlocks.LIGHT_GRAY_MOSAIC_GLASS.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> LIME_MOSAIC_GLASS = ITEMS.register(
            "lime_mosaic_glass",
            () ->
                    new BlockItem(
                    ModBlocks.LIME_MOSAIC_GLASS.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> MAGENTA_MOSAIC_GLASS =
            ITEMS.register("magenta_mosaic_glass", () ->
                    new BlockItem(
                    ModBlocks.MAGENTA_MOSAIC_GLASS.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> ORANGE_MOSAIC_GLASS = ITEMS.register(
            "orange_mosaic_glass",
            () ->
                    new BlockItem(
                    ModBlocks.ORANGE_MOSAIC_GLASS.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> PINK_MOSAIC_GLASS = ITEMS.register(
            "pink_mosaic_glass",
            () ->
                    new BlockItem(
                    ModBlocks.PINK_MOSAIC_GLASS.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> PURPLE_MOSAIC_GLASS = ITEMS.register(
            "purple_mosaic_glass",
            () ->
                    new BlockItem(
                    ModBlocks.PURPLE_MOSAIC_GLASS.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> RED_MOSAIC_GLASS = ITEMS.register(
            "red_mosaic_glass",
            () ->
                    new BlockItem(
                    ModBlocks.RED_MOSAIC_GLASS.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> WHITE_MOSAIC_GLASS = ITEMS.register(
            "white_mosaic_glass",
            () ->
                    new BlockItem(
                    ModBlocks.WHITE_MOSAIC_GLASS.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> YELLOW_MOSAIC_GLASS = ITEMS.register(
            "yellow_mosaic_glass",
            () ->
                    new BlockItem(
                    ModBlocks.YELLOW_MOSAIC_GLASS.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BLACK_MOSAIC_GLASS_PANE =
            ITEMS.register("black_mosaic_glass_pane", () ->
                    new BlockItem(
                    ModBlocks.BLACK_MOSAIC_GLASS_PANE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BLUE_MOSAIC_GLASS_PANE =
            ITEMS.register("blue_mosaic_glass_pane", () ->
                    new BlockItem(
                    ModBlocks.BLUE_MOSAIC_GLASS_PANE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BROWN_MOSAIC_GLASS_PANE =
            ITEMS.register("brown_mosaic_glass_pane", () ->
                    new BlockItem(
                    ModBlocks.BROWN_MOSAIC_GLASS_PANE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> CYAN_MOSAIC_GLASS_PANE =
            ITEMS.register("cyan_mosaic_glass_pane", () ->
                    new BlockItem(
                    ModBlocks.CYAN_MOSAIC_GLASS_PANE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> GRAY_MOSAIC_GLASS_PANE =
            ITEMS.register("gray_mosaic_glass_pane", () ->
                    new BlockItem(
                    ModBlocks.GRAY_MOSAIC_GLASS_PANE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> GREEN_MOSAIC_GLASS_PANE =
            ITEMS.register("green_mosaic_glass_pane", () ->
                    new BlockItem(
                    ModBlocks.GREEN_MOSAIC_GLASS_PANE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> LIGHT_BLUE_MOSAIC_GLASS_PANE =
            ITEMS.register("light_blue_mosaic_glass_pane", () ->
                            new BlockItem(
            ModBlocks.LIGHT_BLUE_MOSAIC_GLASS_PANE.get(),
                                    createBlockItemProperties()
                            )
            );

    public static final RegistryObject<Item> LIGHT_GRAY_MOSAIC_GLASS_PANE =
            ITEMS.register("light_gray_mosaic_glass_pane", () ->
                            new BlockItem(
            ModBlocks.LIGHT_GRAY_MOSAIC_GLASS_PANE.get(),
                                    createBlockItemProperties()
                            )
            );

    public static final RegistryObject<Item> LIME_MOSAIC_GLASS_PANE =
            ITEMS.register("lime_mosaic_glass_pane", () ->
                    new BlockItem(
                    ModBlocks.LIME_MOSAIC_GLASS_PANE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> MAGENTA_MOSAIC_GLASS_PANE =
            ITEMS.register("magenta_mosaic_glass_pane", () ->
                    new BlockItem(
                    ModBlocks.MAGENTA_MOSAIC_GLASS_PANE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> ORANGE_MOSAIC_GLASS_PANE =
            ITEMS.register("orange_mosaic_glass_pane", () ->
                    new BlockItem(
                    ModBlocks.ORANGE_MOSAIC_GLASS_PANE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> PINK_MOSAIC_GLASS_PANE =
            ITEMS.register("pink_mosaic_glass_pane", () ->
                    new BlockItem(
                    ModBlocks.PINK_MOSAIC_GLASS_PANE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> PURPLE_MOSAIC_GLASS_PANE =
            ITEMS.register("purple_mosaic_glass_pane", () ->
                    new BlockItem(
                    ModBlocks.PURPLE_MOSAIC_GLASS_PANE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> RED_MOSAIC_GLASS_PANE =
            ITEMS.register("red_mosaic_glass_pane", () ->
                    new BlockItem(
                    ModBlocks.RED_MOSAIC_GLASS_PANE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> WHITE_MOSAIC_GLASS_PANE =
            ITEMS.register("white_mosaic_glass_pane", () ->
                    new BlockItem(
                    ModBlocks.WHITE_MOSAIC_GLASS_PANE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> YELLOW_MOSAIC_GLASS_PANE =
            ITEMS.register("yellow_mosaic_glass_pane", () ->
                    new BlockItem(
                    ModBlocks.YELLOW_MOSAIC_GLASS_PANE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BLACK_GLAZED_GLASS = ITEMS.register(
            "black_glazed_glass",
            () ->
                    new BlockItem(
                    ModBlocks.BLACK_GLAZED_GLASS.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BLUE_GLAZED_GLASS = ITEMS.register(
            "blue_glazed_glass",
            () ->
                    new BlockItem(
                    ModBlocks.BLUE_GLAZED_GLASS.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BROWN_GLAZED_GLASS = ITEMS.register(
            "brown_glazed_glass",
            () ->
                    new BlockItem(
                    ModBlocks.BROWN_GLAZED_GLASS.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> CYAN_GLAZED_GLASS = ITEMS.register(
            "cyan_glazed_glass",
            () ->
                    new BlockItem(
                    ModBlocks.CYAN_GLAZED_GLASS.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> GRAY_GLAZED_GLASS = ITEMS.register(
            "gray_glazed_glass",
            () ->
                    new BlockItem(
                    ModBlocks.GRAY_GLAZED_GLASS.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> GREEN_GLAZED_GLASS = ITEMS.register(
            "green_glazed_glass",
            () ->
                    new BlockItem(
                    ModBlocks.GREEN_GLAZED_GLASS.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> LIGHT_BLUE_GLAZED_GLASS =
            ITEMS.register("light_blue_glazed_glass", () ->
                    new BlockItem(
                    ModBlocks.LIGHT_BLUE_GLAZED_GLASS.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> LIGHT_GRAY_GLAZED_GLASS =
            ITEMS.register("light_gray_glazed_glass", () ->
                    new BlockItem(
                    ModBlocks.LIGHT_GRAY_GLAZED_GLASS.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> LIME_GLAZED_GLASS = ITEMS.register(
            "lime_glazed_glass",
            () ->
                    new BlockItem(
                    ModBlocks.LIME_GLAZED_GLASS.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> MAGENTA_GLAZED_GLASS =
            ITEMS.register("magenta_glazed_glass", () ->
                    new BlockItem(
                    ModBlocks.MAGENTA_GLAZED_GLASS.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> ORANGE_GLAZED_GLASS = ITEMS.register(
            "orange_glazed_glass",
            () ->
                    new BlockItem(
                    ModBlocks.ORANGE_GLAZED_GLASS.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> PINK_GLAZED_GLASS = ITEMS.register(
            "pink_glazed_glass",
            () ->
                    new BlockItem(
                    ModBlocks.PINK_GLAZED_GLASS.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> PURPLE_GLAZED_GLASS = ITEMS.register(
            "purple_glazed_glass",
            () ->
                    new BlockItem(
                    ModBlocks.PURPLE_GLAZED_GLASS.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> RED_GLAZED_GLASS = ITEMS.register(
            "red_glazed_glass",
            () ->
                    new BlockItem(
                    ModBlocks.RED_GLAZED_GLASS.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> WHITE_GLAZED_GLASS = ITEMS.register(
            "white_glazed_glass",
            () ->
                    new BlockItem(
                    ModBlocks.WHITE_GLAZED_GLASS.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> YELLOW_GLAZED_GLASS = ITEMS.register(
            "yellow_glazed_glass",
            () ->
                    new BlockItem(
                    ModBlocks.YELLOW_GLAZED_GLASS.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BLACK_GLAZED_GLASS_PANE =
            ITEMS.register("black_glazed_glass_pane", () ->
                    new BlockItem(
                    ModBlocks.BLACK_GLAZED_GLASS_PANE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BLUE_GLAZED_GLASS_PANE =
            ITEMS.register("blue_glazed_glass_pane", () ->
                    new BlockItem(
                    ModBlocks.BLUE_GLAZED_GLASS_PANE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BROWN_GLAZED_GLASS_PANE =
            ITEMS.register("brown_glazed_glass_pane", () ->
                    new BlockItem(
                    ModBlocks.BROWN_GLAZED_GLASS_PANE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> CYAN_GLAZED_GLASS_PANE =
            ITEMS.register("cyan_glazed_glass_pane", () ->
                    new BlockItem(
                    ModBlocks.CYAN_GLAZED_GLASS_PANE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> GRAY_GLAZED_GLASS_PANE =
            ITEMS.register("gray_glazed_glass_pane", () ->
                    new BlockItem(
                    ModBlocks.GRAY_GLAZED_GLASS_PANE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> GREEN_GLAZED_GLASS_PANE =
            ITEMS.register("green_glazed_glass_pane", () ->
                    new BlockItem(
                    ModBlocks.GREEN_GLAZED_GLASS_PANE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> LIGHT_BLUE_GLAZED_GLASS_PANE =
            ITEMS.register("light_blue_glazed_glass_pane", () ->
                            new BlockItem(
            ModBlocks.LIGHT_BLUE_GLAZED_GLASS_PANE.get(),
                                    createBlockItemProperties()
                            )
            );

    public static final RegistryObject<Item> LIGHT_GRAY_GLAZED_GLASS_PANE =
            ITEMS.register("light_gray_glazed_glass_pane", () ->
                            new BlockItem(
            ModBlocks.LIGHT_GRAY_GLAZED_GLASS_PANE.get(),
                                    createBlockItemProperties()
                            )
            );

    public static final RegistryObject<Item> LIME_GLAZED_GLASS_PANE =
            ITEMS.register("lime_glazed_glass_pane", () ->
                    new BlockItem(
                    ModBlocks.LIME_GLAZED_GLASS_PANE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> MAGENTA_GLAZED_GLASS_PANE =
            ITEMS.register("magenta_glazed_glass_pane", () ->
                    new BlockItem(
                    ModBlocks.MAGENTA_GLAZED_GLASS_PANE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> ORANGE_GLAZED_GLASS_PANE =
            ITEMS.register("orange_glazed_glass_pane", () ->
                    new BlockItem(
                    ModBlocks.ORANGE_GLAZED_GLASS_PANE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> PINK_GLAZED_GLASS_PANE =
            ITEMS.register("pink_glazed_glass_pane", () ->
                    new BlockItem(
                    ModBlocks.PINK_GLAZED_GLASS_PANE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> PURPLE_GLAZED_GLASS_PANE =
            ITEMS.register("purple_glazed_glass_pane", () ->
                    new BlockItem(
                    ModBlocks.PURPLE_GLAZED_GLASS_PANE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> RED_GLAZED_GLASS_PANE =
            ITEMS.register("red_glazed_glass_pane", () ->
                    new BlockItem(
                    ModBlocks.RED_GLAZED_GLASS_PANE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> WHITE_GLAZED_GLASS_PANE =
            ITEMS.register("white_glazed_glass_pane", () ->
                    new BlockItem(
                    ModBlocks.WHITE_GLAZED_GLASS_PANE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> YELLOW_GLAZED_GLASS_PANE =
            ITEMS.register("yellow_glazed_glass_pane", () ->
                    new BlockItem(
                    ModBlocks.YELLOW_GLAZED_GLASS_PANE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BIT_CHISELED_COPPER = ITEMS.register(
            "bit_chiseled_copper",
            () ->
                    new BlockItem(
                    ModBlocks.BIT_CHISELED_COPPER.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BIT_COPPER_BLOCK = ITEMS.register(
            "bit_copper_block",
            () ->
                    new BlockItem(
                    ModBlocks.BIT_COPPER_BLOCK.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BIT_COPPER_BULB = ITEMS.register(
            "bit_copper_bulb",
            () ->
                    new BlockItem(
                    ModBlocks.BIT_COPPER_BULB.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BIT_COPPER_GRATE = ITEMS.register(
            "bit_copper_grate",
            () ->
                    new BlockItem(
                    ModBlocks.BIT_COPPER_GRATE.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BIT_CUT_COPPER = ITEMS.register(
            "bit_cut_copper",
            () ->
                    new BlockItem(ModBlocks.BIT_CUT_COPPER.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BIT_EXPOSED_CHISELED_COPPER =
            ITEMS.register("bit_exposed_chiseled_copper", () ->
                            new BlockItem(
            ModBlocks.BIT_EXPOSED_CHISELED_COPPER.get(),
                                    createBlockItemProperties()
                            )
            );

    public static final RegistryObject<Item> BIT_EXPOSED_COPPER_BLOCK =
            ITEMS.register("bit_exposed_copper_block", () ->
                    new BlockItem(
                    ModBlocks.BIT_EXPOSED_COPPER_BLOCK.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BIT_EXPOSED_COPPER_BULB =
            ITEMS.register("bit_exposed_copper_bulb", () ->
                    new BlockItem(
                    ModBlocks.BIT_EXPOSED_COPPER_BULB.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BIT_EXPOSED_COPPER_GRATE =
            ITEMS.register("bit_exposed_copper_grate", () ->
                    new BlockItem(
                    ModBlocks.BIT_EXPOSED_COPPER_GRATE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BIT_EXPOSED_CUT_COPPER =
            ITEMS.register("bit_exposed_cut_copper", () ->
                    new BlockItem(
                    ModBlocks.BIT_EXPOSED_CUT_COPPER.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BIT_WEATHERED_CHISELED_COPPER =
            ITEMS.register("bit_weathered_chiseled_copper", () ->
                            new BlockItem(
            ModBlocks.BIT_WEATHERED_CHISELED_COPPER.get(),
                                    createBlockItemProperties()
                            )
            );

    public static final RegistryObject<Item> BIT_WEATHERED_COPPER_BLOCK =
            ITEMS.register("bit_weathered_copper_block", () ->
                            new BlockItem(
            ModBlocks.BIT_WEATHERED_COPPER_BLOCK.get(),
                                    createBlockItemProperties()
                            )
            );

    public static final RegistryObject<Item> BIT_WEATHERED_COPPER_BULB =
            ITEMS.register("bit_weathered_copper_bulb", () ->
                    new BlockItem(
                    ModBlocks.BIT_WEATHERED_COPPER_BULB.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BIT_WEATHERED_COPPER_GRATE =
            ITEMS.register("bit_weathered_copper_grate", () ->
                            new BlockItem(
            ModBlocks.BIT_WEATHERED_COPPER_GRATE.get(),
                                    createBlockItemProperties()
                            )
            );

    public static final RegistryObject<Item> BIT_WEATHERED_CUT_COPPER =
            ITEMS.register("bit_weathered_cut_copper", () ->
                    new BlockItem(
                    ModBlocks.BIT_WEATHERED_CUT_COPPER.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BIT_OXIDIZED_CHISELED_COPPER =
            ITEMS.register("bit_oxidized_chiseled_copper", () ->
                            new BlockItem(
            ModBlocks.BIT_OXIDIZED_CHISELED_COPPER.get(),
                                    createBlockItemProperties()
                            )
            );

    public static final RegistryObject<Item> BIT_OXIDIZED_COPPER_BLOCK =
            ITEMS.register("bit_oxidized_copper_block", () ->
                    new BlockItem(
                    ModBlocks.BIT_OXIDIZED_COPPER_BLOCK.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BIT_OXIDIZED_COPPER_BULB =
            ITEMS.register("bit_oxidized_copper_bulb", () ->
                    new BlockItem(
                    ModBlocks.BIT_OXIDIZED_COPPER_BULB.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BIT_OXIDIZED_COPPER_GRATE =
            ITEMS.register("bit_oxidized_copper_grate", () ->
                    new BlockItem(
                    ModBlocks.BIT_OXIDIZED_COPPER_GRATE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BIT_OXIDIZED_CUT_COPPER =
            ITEMS.register("bit_oxidized_cut_copper", () ->
                    new BlockItem(
                    ModBlocks.BIT_OXIDIZED_CUT_COPPER.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BIT_CHISELED_TUFF = ITEMS.register(
            "bit_chiseled_tuff",
            () ->
                    new BlockItem(
                    ModBlocks.BIT_CHISELED_TUFF.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BIT_CHISELED_TUFF_BRICKS =
            ITEMS.register("bit_chiseled_tuff_bricks", () ->
                    new BlockItem(
                    ModBlocks.BIT_CHISELED_TUFF_BRICKS.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BIT_POLISHED_TUFF = ITEMS.register(
            "bit_polished_tuff",
            () ->
                    new BlockItem(
                    ModBlocks.BIT_POLISHED_TUFF.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BIT_POLISHED_TUFF_STAIRS =
            ITEMS.register("bit_polished_tuff_stairs", () ->
                    new BlockItem(
                    ModBlocks.BIT_POLISHED_TUFF_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BIT_POLISHED_TUFF_SLAB =
            ITEMS.register("bit_polished_tuff_slab", () ->
                    new BlockItem(
                    ModBlocks.BIT_POLISHED_TUFF_SLAB.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BIT_POLISHED_TUFF_WALL =
            ITEMS.register("bit_polished_tuff_wall", () ->
                    new BlockItem(
                    ModBlocks.BIT_POLISHED_TUFF_WALL.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BIT_TUFF_BRICKS = ITEMS.register(
            "bit_tuff_bricks",
            () ->
                    new BlockItem(
                    ModBlocks.BIT_TUFF_BRICKS.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BIT_TUFF_BRICKS_STAIRS =
            ITEMS.register("bit_tuff_bricks_stairs", () ->
                    new BlockItem(
                    ModBlocks.BIT_TUFF_BRICKS_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BIT_TUFF_BRICKS_SLAB =
            ITEMS.register("bit_tuff_bricks_slab", () ->
                    new BlockItem(
                    ModBlocks.BIT_TUFF_BRICKS_SLAB.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BIT_TUFF_BRICKS_WALL =
            ITEMS.register("bit_tuff_bricks_wall", () ->
                    new BlockItem(
                    ModBlocks.BIT_TUFF_BRICKS_WALL.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BLACK_SANDSTONE_STAIRS =
            ITEMS.register("black_sandstone_stairs", () ->
                    new BlockItem(
                    ModBlocks.BLACK_SANDSTONE_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> BLUE_SANDSTONE_STAIRS =
            ITEMS.register("blue_sandstone_stairs", () ->
                    new BlockItem(
                    ModBlocks.BLUE_SANDSTONE_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> GREEN_SANDSTONE_STAIRS =
            ITEMS.register("green_sandstone_stairs", () ->
                    new BlockItem(
                    ModBlocks.GREEN_SANDSTONE_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> ORANGE_SANDSTONE_STAIRS =
            ITEMS.register("orange_sandstone_stairs", () ->
                    new BlockItem(
                    ModBlocks.ORANGE_SANDSTONE_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> PINK_SANDSTONE_STAIRS =
            ITEMS.register("pink_sandstone_stairs", () ->
                    new BlockItem(
                    ModBlocks.PINK_SANDSTONE_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> RED_SANDSTONE_STAIRS =
            ITEMS.register("red_sandstone_stairs", () ->
                    new BlockItem(
                    ModBlocks.RED_SANDSTONE_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> WHITE_SANDSTONE_STAIRS =
            ITEMS.register("white_sandstone_stairs", () ->
                    new BlockItem(
                    ModBlocks.WHITE_SANDSTONE_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> YELLOW_SANDSTONE_STAIRS =
            ITEMS.register("yellow_sandstone_stairs", () ->
                    new BlockItem(
                    ModBlocks.YELLOW_SANDSTONE_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BLACK_SANDSTONE_SLAB =
            ITEMS.register("black_sandstone_slab", () ->
                    new BlockItem(
                    ModBlocks.BLACK_SANDSTONE_SLAB.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> BLUE_SANDSTONE_SLAB = ITEMS.register(
            "blue_sandstone_slab",
            () ->
                    new BlockItem(
                    ModBlocks.BLUE_SANDSTONE_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> GREEN_SANDSTONE_SLAB =
            ITEMS.register("green_sandstone_slab", () ->
                    new BlockItem(
                    ModBlocks.GREEN_SANDSTONE_SLAB.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> ORANGE_SANDSTONE_SLAB =
            ITEMS.register("orange_sandstone_slab", () ->
                    new BlockItem(
                    ModBlocks.ORANGE_SANDSTONE_SLAB.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> PINK_SANDSTONE_SLAB = ITEMS.register(
            "pink_sandstone_slab",
            () ->
                    new BlockItem(
                    ModBlocks.PINK_SANDSTONE_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> RED_SANDSTONE_SLAB = ITEMS.register(
            "red_sandstone_slab",
            () ->
                    new BlockItem(
                    ModBlocks.RED_SANDSTONE_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> WHITE_SANDSTONE_SLAB =
            ITEMS.register("white_sandstone_slab", () ->
                    new BlockItem(
                    ModBlocks.WHITE_SANDSTONE_SLAB.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> YELLOW_SANDSTONE_SLAB =
            ITEMS.register("yellow_sandstone_slab", () ->
                    new BlockItem(
                    ModBlocks.YELLOW_SANDSTONE_SLAB.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BLACK_SANDSTONE_WALL =
            ITEMS.register("black_sandstone_wall", () ->
                    new BlockItem(
                    ModBlocks.BLACK_SANDSTONE_WALL.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> BLUE_SANDSTONE_WALL = ITEMS.register(
            "blue_sandstone_wall",
            () ->
                    new BlockItem(
                    ModBlocks.BLUE_SANDSTONE_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> GREEN_SANDSTONE_WALL =
            ITEMS.register("green_sandstone_wall", () ->
                    new BlockItem(
                    ModBlocks.GREEN_SANDSTONE_WALL.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> ORANGE_SANDSTONE_WALL =
            ITEMS.register("orange_sandstone_wall", () ->
                    new BlockItem(
                    ModBlocks.ORANGE_SANDSTONE_WALL.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> PINK_SANDSTONE_WALL = ITEMS.register(
            "pink_sandstone_wall",
            () ->
                    new BlockItem(
                    ModBlocks.PINK_SANDSTONE_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> RED_SANDSTONE_WALL = ITEMS.register(
            "red_sandstone_wall",
            () ->
                    new BlockItem(
                    ModBlocks.RED_SANDSTONE_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> WHITE_SANDSTONE_WALL =
            ITEMS.register("white_sandstone_wall", () ->
                    new BlockItem(
                    ModBlocks.WHITE_SANDSTONE_WALL.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> YELLOW_SANDSTONE_WALL =
            ITEMS.register("yellow_sandstone_wall", () ->
                    new BlockItem(
                    ModBlocks.YELLOW_SANDSTONE_WALL.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> WHITE_SMOOTH_SANDSTONE_WALL =
            ITEMS.register("white_smooth_sandstone_wall", () ->
                            new BlockItem(
            ModBlocks.WHITE_SMOOTH_SANDSTONE_WALL.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> BLACK_SMOOTH_SANDSTONE_WALL =
            ITEMS.register("black_smooth_sandstone_wall", () ->
                            new BlockItem(
            ModBlocks.BLACK_SMOOTH_SANDSTONE_WALL.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> RED_SMOOTH_SANDSTONE_WALL =
            ITEMS.register("red_smooth_sandstone_wall", () ->
                    new BlockItem(
                    ModBlocks.RED_SMOOTH_SANDSTONE_WALL.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> ORANGE_SMOOTH_SANDSTONE_WALL =
            ITEMS.register("orange_smooth_sandstone_wall", () ->
                            new BlockItem(
            ModBlocks.ORANGE_SMOOTH_SANDSTONE_WALL.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> YELLOW_SMOOTH_SANDSTONE_WALL =
            ITEMS.register("yellow_smooth_sandstone_wall", () ->
                            new BlockItem(
            ModBlocks.YELLOW_SMOOTH_SANDSTONE_WALL.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> GREEN_SMOOTH_SANDSTONE_WALL =
            ITEMS.register("green_smooth_sandstone_wall", () ->
                            new BlockItem(
            ModBlocks.GREEN_SMOOTH_SANDSTONE_WALL.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> BLUE_SMOOTH_SANDSTONE_WALL =
            ITEMS.register("blue_smooth_sandstone_wall", () ->
                            new BlockItem(
            ModBlocks.BLUE_SMOOTH_SANDSTONE_WALL.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> PINK_SMOOTH_SANDSTONE_WALL =
            ITEMS.register("pink_smooth_sandstone_wall", () ->
                            new BlockItem(
            ModBlocks.PINK_SMOOTH_SANDSTONE_WALL.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> SMOOTH_SANDSTONE_WALL =
            ITEMS.register("smooth_sandstone_wall", () ->
                    new BlockItem(
                    ModBlocks.SMOOTH_SANDSTONE_WALL.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BLACK_SMOOTH_SANDSTONE_SLAB =
            ITEMS.register("black_smooth_sandstone_slab", () ->
                            new BlockItem(
            ModBlocks.BLACK_SMOOTH_SANDSTONE_SLAB.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> BLUE_SMOOTH_SANDSTONE_SLAB =
            ITEMS.register("blue_smooth_sandstone_slab", () ->
                            new BlockItem(
            ModBlocks.BLUE_SMOOTH_SANDSTONE_SLAB.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> GREEN_SMOOTH_SANDSTONE_SLAB =
            ITEMS.register("green_smooth_sandstone_slab", () ->
                            new BlockItem(
            ModBlocks.GREEN_SMOOTH_SANDSTONE_SLAB.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> ORANGE_SMOOTH_SANDSTONE_SLAB =
            ITEMS.register("orange_smooth_sandstone_slab", () ->
                            new BlockItem(
            ModBlocks.ORANGE_SMOOTH_SANDSTONE_SLAB.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> PINK_SMOOTH_SANDSTONE_SLAB =
            ITEMS.register("pink_smooth_sandstone_slab", () ->
                            new BlockItem(
            ModBlocks.PINK_SMOOTH_SANDSTONE_SLAB.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> RED_SMOOTH_SANDSTONE_SLAB =
            ITEMS.register("red_smooth_sandstone_slab", () ->
                    new BlockItem(
                    ModBlocks.RED_SMOOTH_SANDSTONE_SLAB.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> WHITE_SMOOTH_SANDSTONE_SLAB =
            ITEMS.register("white_smooth_sandstone_slab", () ->
                            new BlockItem(
            ModBlocks.WHITE_SMOOTH_SANDSTONE_SLAB.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> YELLOW_SMOOTH_SANDSTONE_SLAB =
            ITEMS.register("yellow_smooth_sandstone_slab", () ->
                            new BlockItem(
            ModBlocks.YELLOW_SMOOTH_SANDSTONE_SLAB.get(),
                                    createBlockItemProperties()
                            )
            );

    public static final RegistryObject<Item> BLACK_SMOOTH_SANDSTONE_STAIRS =
            ITEMS.register("black_smooth_sandstone_stairs", () ->
                            new BlockItem(
            ModBlocks.BLACK_SMOOTH_SANDSTONE_STAIRS.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> BLUE_SMOOTH_SANDSTONE_STAIRS =
            ITEMS.register("blue_smooth_sandstone_stairs", () ->
                            new BlockItem(
            ModBlocks.BLUE_SMOOTH_SANDSTONE_STAIRS.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> GREEN_SMOOTH_SANDSTONE_STAIRS =
            ITEMS.register("green_smooth_sandstone_stairs", () ->
                            new BlockItem(
            ModBlocks.GREEN_SMOOTH_SANDSTONE_STAIRS.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> ORANGE_SMOOTH_SANDSTONE_STAIRS =
            ITEMS.register("orange_smooth_sandstone_stairs", () ->
                            new BlockItem(
            ModBlocks.ORANGE_SMOOTH_SANDSTONE_STAIRS.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> PINK_SMOOTH_SANDSTONE_STAIRS =
            ITEMS.register("pink_smooth_sandstone_stairs", () ->
                            new BlockItem(
            ModBlocks.PINK_SMOOTH_SANDSTONE_STAIRS.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> RED_SMOOTH_SANDSTONE_STAIRS =
            ITEMS.register("red_smooth_sandstone_stairs", () ->
                            new BlockItem(
            ModBlocks.RED_SMOOTH_SANDSTONE_STAIRS.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> WHITE_SMOOTH_SANDSTONE_STAIRS =
            ITEMS.register("white_smooth_sandstone_stairs", () ->
                            new BlockItem(
            ModBlocks.WHITE_SMOOTH_SANDSTONE_STAIRS.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> YELLOW_SMOOTH_SANDSTONE_STAIRS =
            ITEMS.register("yellow_smooth_sandstone_stairs", () ->
                            new BlockItem(
            ModBlocks.YELLOW_SMOOTH_SANDSTONE_STAIRS.get(),
                                    createBlockItemProperties()
                            )
            );

    public static final RegistryObject<Item> BLACK_TILES_STAIRS = ITEMS.register(
            "black_tiles_stairs",
            () ->
                    new BlockItem(
                    ModBlocks.BLACK_TILES_STAIRS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BLUE_TILES_STAIRS = ITEMS.register(
            "blue_tiles_stairs",
            () ->
                    new BlockItem(
                    ModBlocks.BLUE_TILES_STAIRS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BROWN_TILES_STAIRS = ITEMS.register(
            "brown_tiles_stairs",
            () ->
                    new BlockItem(
                    ModBlocks.BROWN_TILES_STAIRS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> CYAN_TILES_STAIRS = ITEMS.register(
            "cyan_tiles_stairs",
            () ->
                    new BlockItem(
                    ModBlocks.CYAN_TILES_STAIRS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> GRAY_TILES_STAIRS = ITEMS.register(
            "gray_tiles_stairs",
            () ->
                    new BlockItem(
                    ModBlocks.GRAY_TILES_STAIRS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> GREEN_TILES_STAIRS = ITEMS.register(
            "green_tiles_stairs",
            () ->
                    new BlockItem(
                    ModBlocks.GREEN_TILES_STAIRS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> LIGHT_BLUE_TILES_STAIRS =
            ITEMS.register("light_blue_tiles_stairs", () ->
                    new BlockItem(
                    ModBlocks.LIGHT_BLUE_TILES_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> LIGHT_GRAY_TILES_STAIRS =
            ITEMS.register("light_gray_tiles_stairs", () ->
                    new BlockItem(
                    ModBlocks.LIGHT_GRAY_TILES_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> LIME_TILES_STAIRS = ITEMS.register(
            "lime_tiles_stairs",
            () ->
                    new BlockItem(
                    ModBlocks.LIME_TILES_STAIRS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> MAGENTA_TILES_STAIRS =
            ITEMS.register("magenta_tiles_stairs", () ->
                    new BlockItem(
                    ModBlocks.MAGENTA_TILES_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> ORANGE_TILES_STAIRS = ITEMS.register(
            "orange_tiles_stairs",
            () ->
                    new BlockItem(
                    ModBlocks.ORANGE_TILES_STAIRS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> PINK_TILES_STAIRS = ITEMS.register(
            "pink_tiles_stairs",
            () ->
                    new BlockItem(
                    ModBlocks.PINK_TILES_STAIRS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> PURPLE_TILES_STAIRS = ITEMS.register(
            "purple_tiles_stairs",
            () ->
                    new BlockItem(
                    ModBlocks.PURPLE_TILES_STAIRS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> RED_TILES_STAIRS = ITEMS.register(
            "red_tiles_stairs",
            () ->
                    new BlockItem(
                    ModBlocks.RED_TILES_STAIRS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> WHITE_TILES_STAIRS = ITEMS.register(
            "white_tiles_stairs",
            () ->
                    new BlockItem(
                    ModBlocks.WHITE_TILES_STAIRS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> YELLOW_TILES_STAIRS = ITEMS.register(
            "yellow_tiles_stairs",
            () ->
                    new BlockItem(
                    ModBlocks.YELLOW_TILES_STAIRS.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BLACK_TILES_SLAB = ITEMS.register(
            "black_tiles_slab",
            () ->
                    new BlockItem(
                    ModBlocks.BLACK_TILES_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BLUE_TILES_SLAB = ITEMS.register(
            "blue_tiles_slab",
            () ->
                    new BlockItem(
                    ModBlocks.BLUE_TILES_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BROWN_TILES_SLAB = ITEMS.register(
            "brown_tiles_slab",
            () ->
                    new BlockItem(
                    ModBlocks.BROWN_TILES_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> CYAN_TILES_SLAB = ITEMS.register(
            "cyan_tiles_slab",
            () ->
                    new BlockItem(
                    ModBlocks.CYAN_TILES_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> GRAY_TILES_SLAB = ITEMS.register(
            "gray_tiles_slab",
            () ->
                    new BlockItem(
                    ModBlocks.GRAY_TILES_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> GREEN_TILES_SLAB = ITEMS.register(
            "green_tiles_slab",
            () ->
                    new BlockItem(
                    ModBlocks.GREEN_TILES_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> LIGHT_BLUE_TILES_SLAB =
            ITEMS.register("light_blue_tiles_slab", () ->
                    new BlockItem(
                    ModBlocks.LIGHT_BLUE_TILES_SLAB.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> LIGHT_GRAY_TILES_SLAB =
            ITEMS.register("light_gray_tiles_slab", () ->
                    new BlockItem(
                    ModBlocks.LIGHT_GRAY_TILES_SLAB.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> LIME_TILES_SLAB = ITEMS.register(
            "lime_tiles_slab",
            () ->
                    new BlockItem(
                    ModBlocks.LIME_TILES_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> MAGENTA_TILES_SLAB = ITEMS.register(
            "magenta_tiles_slab",
            () ->
                    new BlockItem(
                    ModBlocks.MAGENTA_TILES_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> ORANGE_TILES_SLAB = ITEMS.register(
            "orange_tiles_slab",
            () ->
                    new BlockItem(
                    ModBlocks.ORANGE_TILES_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> PINK_TILES_SLAB = ITEMS.register(
            "pink_tiles_slab",
            () ->
                    new BlockItem(
                    ModBlocks.PINK_TILES_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> PURPLE_TILES_SLAB = ITEMS.register(
            "purple_tiles_slab",
            () ->
                    new BlockItem(
                    ModBlocks.PURPLE_TILES_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> RED_TILES_SLAB = ITEMS.register(
            "red_tiles_slab",
            () ->
                    new BlockItem(ModBlocks.RED_TILES_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WHITE_TILES_SLAB = ITEMS.register(
            "white_tiles_slab",
            () ->
                    new BlockItem(
                    ModBlocks.WHITE_TILES_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> YELLOW_TILES_SLAB = ITEMS.register(
            "yellow_tiles_slab",
            () ->
                    new BlockItem(
                    ModBlocks.YELLOW_TILES_SLAB.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BLACK_TILES_WALL = ITEMS.register(
            "black_tiles_wall",
            () ->
                    new BlockItem(
                    ModBlocks.BLACK_TILES_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BLUE_TILES_WALL = ITEMS.register(
            "blue_tiles_wall",
            () ->
                    new BlockItem(
                    ModBlocks.BLUE_TILES_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BROWN_TILES_WALL = ITEMS.register(
            "brown_tiles_wall",
            () ->
                    new BlockItem(
                    ModBlocks.BROWN_TILES_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> CYAN_TILES_WALL = ITEMS.register(
            "cyan_tiles_wall",
            () ->
                    new BlockItem(
                    ModBlocks.CYAN_TILES_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> GRAY_TILES_WALL = ITEMS.register(
            "gray_tiles_wall",
            () ->
                    new BlockItem(
                    ModBlocks.GRAY_TILES_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> GREEN_TILES_WALL = ITEMS.register(
            "green_tiles_wall",
            () ->
                    new BlockItem(
                    ModBlocks.GREEN_TILES_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> LIGHT_BLUE_TILES_WALL =
            ITEMS.register("light_blue_tiles_wall", () ->
                    new BlockItem(
                    ModBlocks.LIGHT_BLUE_TILES_WALL.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> LIGHT_GRAY_TILES_WALL =
            ITEMS.register("light_gray_tiles_wall", () ->
                    new BlockItem(
                    ModBlocks.LIGHT_GRAY_TILES_WALL.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> LIME_TILES_WALL = ITEMS.register(
            "lime_tiles_wall",
            () ->
                    new BlockItem(
                    ModBlocks.LIME_TILES_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> MAGENTA_TILES_WALL = ITEMS.register(
            "magenta_tiles_wall",
            () ->
                    new BlockItem(
                    ModBlocks.MAGENTA_TILES_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> ORANGE_TILES_WALL = ITEMS.register(
            "orange_tiles_wall",
            () ->
                    new BlockItem(
                    ModBlocks.ORANGE_TILES_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> PINK_TILES_WALL = ITEMS.register(
            "pink_tiles_wall",
            () ->
                    new BlockItem(
                    ModBlocks.PINK_TILES_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> PURPLE_TILES_WALL = ITEMS.register(
            "purple_tiles_wall",
            () ->
                    new BlockItem(
                    ModBlocks.PURPLE_TILES_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> RED_TILES_WALL = ITEMS.register(
            "red_tiles_wall",
            () ->
                    new BlockItem(ModBlocks.RED_TILES_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WHITE_TILES_WALL = ITEMS.register(
            "white_tiles_wall",
            () ->
                    new BlockItem(
                    ModBlocks.WHITE_TILES_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> YELLOW_TILES_WALL = ITEMS.register(
            "yellow_tiles_wall",
            () ->
                    new BlockItem(
                    ModBlocks.YELLOW_TILES_WALL.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> POLISHED_BASALT_STAIRS =
            ITEMS.register("polished_basalt_stairs", () ->
                    new BlockItem(
                    ModBlocks.POLISHED_BASALT_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> POLISHED_BASALT_SLAB =
            ITEMS.register("polished_basalt_slab", () ->
                    new BlockItem(
                    ModBlocks.POLISHED_BASALT_SLAB.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> POLISHED_BASALT_WALL =
            ITEMS.register("polished_basalt_wall", () ->
                    new BlockItem(
                    ModBlocks.POLISHED_BASALT_WALL.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> DRIPSTONE_BLOCK_STAIRS =
            ITEMS.register("dripstone_block_stairs", () ->
                    new BlockItem(
                    ModBlocks.DRIPSTONE_BLOCK_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> DRIPSTONE_BLOCK_SLAB =
            ITEMS.register("dripstone_block_slab", () ->
                    new BlockItem(
                    ModBlocks.DRIPSTONE_BLOCK_SLAB.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> DRIPSTONE_BLOCK_WALL =
            ITEMS.register("dripstone_block_wall", () ->
                    new BlockItem(
                    ModBlocks.DRIPSTONE_BLOCK_WALL.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> END_STONE_STAIRS = ITEMS.register(
            "end_stone_stairs",
            () ->
                    new BlockItem(
                    ModBlocks.END_STONE_STAIRS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> END_STONE_SLAB = ITEMS.register(
            "end_stone_slab",
            () ->
                    new BlockItem(ModBlocks.END_STONE_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> END_STONE_WALL = ITEMS.register(
            "end_stone_wall",
            () ->
                    new BlockItem(ModBlocks.END_STONE_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> STONE_WALL = ITEMS.register(
            "stone_wall",
            () -> new BlockItem(ModBlocks.STONE_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> QUARTZ_BRICKS_STAIRS =
            ITEMS.register("quartz_bricks_stairs", () ->
                    new BlockItem(
                    ModBlocks.QUARTZ_BRICKS_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> QUARTZ_BRICKS_SLAB = ITEMS.register(
            "quartz_bricks_slab",
            () ->
                    new BlockItem(
                    ModBlocks.QUARTZ_BRICKS_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> QUARTZ_BRICKS_WALL = ITEMS.register(
            "quartz_bricks_wall",
            () ->
                    new BlockItem(
                    ModBlocks.QUARTZ_BRICKS_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> QUARTZ_PILLAR_STAIRS =
            ITEMS.register("quartz_pillar_stairs", () ->
                    new BlockItem(
                    ModBlocks.QUARTZ_PILLAR_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> QUARTZ_PILLAR_SLAB = ITEMS.register(
            "quartz_pillar_slab",
            () ->
                    new BlockItem(
                    ModBlocks.QUARTZ_PILLAR_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> QUARTZ_PILLAR_WALL = ITEMS.register(
            "quartz_pillar_wall",
            () ->
                    new BlockItem(
                    ModBlocks.QUARTZ_PILLAR_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> CALCITE_STAIRS = ITEMS.register(
            "calcite_stairs",
            () ->
                    new BlockItem(ModBlocks.CALCITE_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> CALCITE_SLAB = ITEMS.register(
            "calcite_slab",
            () ->
                    new BlockItem(ModBlocks.CALCITE_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> CALCITE_WALL = ITEMS.register(
            "calcite_wall",
            () ->
                    new BlockItem(ModBlocks.CALCITE_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BEDROCK_SLAB = ITEMS.register(
            "bedrock_slab",
            () ->
                    new BlockItem(ModBlocks.BEDROCK_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BEDROCK_STAIRS = ITEMS.register(
            "bedrock_stairs",
            () ->
                    new BlockItem(ModBlocks.BEDROCK_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BEDROCK_WALL = ITEMS.register(
            "bedrock_wall",
            () ->
                    new BlockItem(ModBlocks.BEDROCK_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BEDROCK_PANE = ITEMS.register(
            "bedrock_pane",
            () ->
                    new BlockItem(ModBlocks.BEDROCK_PANE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> OBSIDIAN_STAIRS = ITEMS.register(
            "obsidian_stairs",
            () ->
                    new BlockItem(
                    ModBlocks.OBSIDIAN_STAIRS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> OBSIDIAN_SLAB = ITEMS.register(
            "obsidian_slab",
            () ->
                    new BlockItem(ModBlocks.OBSIDIAN_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> PRISMARINE_BRICKS_WALL =
            ITEMS.register("prismarine_bricks_wall", () ->
                    new BlockItem(
                    ModBlocks.PRISMARINE_BRICKS_WALL.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> DARK_PRISMARINE_WALL =
            ITEMS.register("dark_prismarine_wall", () ->
                    new BlockItem(
                    ModBlocks.DARK_PRISMARINE_WALL.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> QUARTZ_BLOCK_WALL = ITEMS.register(
            "quartz_block_wall",
            () ->
                    new BlockItem(
                    ModBlocks.QUARTZ_BLOCK_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> SMOOTH_QUARTZ_WALL = ITEMS.register(
            "smooth_quartz_wall",
            () ->
                    new BlockItem(
                    ModBlocks.SMOOTH_QUARTZ_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> SMOOTH_BASALT_STAIRS =
            ITEMS.register("smooth_basalt_stairs", () ->
                    new BlockItem(
                    ModBlocks.SMOOTH_BASALT_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> SMOOTH_BASALT_SLAB = ITEMS.register(
            "smooth_basalt_slab",
            () ->
                    new BlockItem(
                    ModBlocks.SMOOTH_BASALT_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> MOSS_BLOCK_SLAB = ITEMS.register(
            "moss_block_slab",
            () ->
                    new BlockItem(
                    ModBlocks.MOSS_BLOCK_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> AMETHYST_BLOCK_SLAB = ITEMS.register(
            "amethyst_block_slab",
            () ->
                    new BlockItem(
                    ModBlocks.AMETHYST_BLOCK_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BIT_COPPER_BLOCK_STAIRS =
            ITEMS.register("bit_copper_block_stairs", () ->
                    new BlockItem(
                    ModBlocks.BIT_COPPER_BLOCK_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> BIT_COPPER_BLOCK_SLAB =
            ITEMS.register("bit_copper_block_slab", () ->
                    new BlockItem(
                    ModBlocks.BIT_COPPER_BLOCK_SLAB.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> BIT_COPPER_BLOCK_WALL =
            ITEMS.register("bit_copper_block_wall", () ->
                    new BlockItem(
                    ModBlocks.BIT_COPPER_BLOCK_WALL.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> BIT_EXPOSED_COPPER_BLOCK_STAIRS =
            ITEMS.register("bit_exposed_copper_block_stairs", () ->
                            new BlockItem(
            ModBlocks.BIT_EXPOSED_COPPER_BLOCK_STAIRS.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> BIT_EXPOSED_COPPER_BLOCK_SLAB =
            ITEMS.register("bit_exposed_copper_block_slab", () ->
                            new BlockItem(
            ModBlocks.BIT_EXPOSED_COPPER_BLOCK_SLAB.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> BIT_EXPOSED_COPPER_BLOCK_WALL =
            ITEMS.register("bit_exposed_copper_block_wall", () ->
                            new BlockItem(
            ModBlocks.BIT_EXPOSED_COPPER_BLOCK_WALL.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> BIT_WEATHERED_COPPER_BLOCK_STAIRS =
            ITEMS.register("bit_weathered_copper_block_stairs", () ->
                            new BlockItem(
            ModBlocks.BIT_WEATHERED_COPPER_BLOCK_STAIRS.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> BIT_WEATHERED_COPPER_BLOCK_SLAB =
            ITEMS.register("bit_weathered_copper_block_slab", () ->
                            new BlockItem(
            ModBlocks.BIT_WEATHERED_COPPER_BLOCK_SLAB.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> BIT_WEATHERED_COPPER_BLOCK_WALL =
            ITEMS.register("bit_weathered_copper_block_wall", () ->
                            new BlockItem(
            ModBlocks.BIT_WEATHERED_COPPER_BLOCK_WALL.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> BIT_OXIDIZED_COPPER_BLOCK_STAIRS =
            ITEMS.register("bit_oxidized_copper_block_stairs", () ->
                            new BlockItem(
            ModBlocks.BIT_OXIDIZED_COPPER_BLOCK_STAIRS.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> BIT_OXIDIZED_COPPER_BLOCK_SLAB =
            ITEMS.register("bit_oxidized_copper_block_slab", () ->
                            new BlockItem(
            ModBlocks.BIT_OXIDIZED_COPPER_BLOCK_SLAB.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> BIT_OXIDIZED_COPPER_BLOCK_WALL =
            ITEMS.register("bit_oxidized_copper_block_wall", () ->
                            new BlockItem(
            ModBlocks.BIT_OXIDIZED_COPPER_BLOCK_WALL.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> BIT_CUT_COPPER_STAIRS =
            ITEMS.register("bit_cut_copper_stairs", () ->
                    new BlockItem(
                    ModBlocks.BIT_CUT_COPPER_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> BIT_CUT_COPPER_SLAB = ITEMS.register(
            "bit_cut_copper_slab",
            () ->
                    new BlockItem(
                    ModBlocks.BIT_CUT_COPPER_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BIT_CUT_COPPER_WALL = ITEMS.register(
            "bit_cut_copper_wall",
            () ->
                    new BlockItem(
                    ModBlocks.BIT_CUT_COPPER_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BIT_EXPOSED_CUT_COPPER_STAIRS =
            ITEMS.register("bit_exposed_cut_copper_stairs", () ->
                            new BlockItem(
            ModBlocks.BIT_EXPOSED_CUT_COPPER_STAIRS.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> BIT_EXPOSED_CUT_COPPER_SLAB =
            ITEMS.register("bit_exposed_cut_copper_slab", () ->
                            new BlockItem(
            ModBlocks.BIT_EXPOSED_CUT_COPPER_SLAB.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> BIT_EXPOSED_CUT_COPPER_WALL =
            ITEMS.register("bit_exposed_cut_copper_wall", () ->
                            new BlockItem(
            ModBlocks.BIT_EXPOSED_CUT_COPPER_WALL.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> BIT_WEATHERED_CUT_COPPER_STAIRS =
            ITEMS.register("bit_weathered_cut_copper_stairs", () ->
                            new BlockItem(
            ModBlocks.BIT_WEATHERED_CUT_COPPER_STAIRS.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> BIT_WEATHERED_CUT_COPPER_SLAB =
            ITEMS.register("bit_weathered_cut_copper_slab", () ->
                            new BlockItem(
            ModBlocks.BIT_WEATHERED_CUT_COPPER_SLAB.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> BIT_WEATHERED_CUT_COPPER_WALL =
            ITEMS.register("bit_weathered_cut_copper_wall", () ->
                            new BlockItem(
            ModBlocks.BIT_WEATHERED_CUT_COPPER_WALL.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> BIT_OXIDIZED_CUT_COPPER_STAIRS =
            ITEMS.register("bit_oxidized_cut_copper_stairs", () ->
                            new BlockItem(
            ModBlocks.BIT_OXIDIZED_CUT_COPPER_STAIRS.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> BIT_OXIDIZED_CUT_COPPER_SLAB =
            ITEMS.register("bit_oxidized_cut_copper_slab", () ->
                            new BlockItem(
            ModBlocks.BIT_OXIDIZED_CUT_COPPER_SLAB.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> BIT_OXIDIZED_CUT_COPPER_WALL =
            ITEMS.register("bit_oxidized_cut_copper_wall", () ->
                            new BlockItem(
            ModBlocks.BIT_OXIDIZED_CUT_COPPER_WALL.get(),
                                    createBlockItemProperties()
                            )
            );

    public static final RegistryObject<Item> MOSSY_CALCITE = ITEMS.register(
            "mossy_calcite",
            () ->
                    new BlockItem(ModBlocks.MOSSY_CALCITE.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> MOSSY_CALCITE_STAIRS =
            ITEMS.register("mossy_calcite_stairs", () ->
                    new BlockItem(
                    ModBlocks.MOSSY_CALCITE_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> MOSSY_CALCITE_SLAB = ITEMS.register(
            "mossy_calcite_slab",
            () ->
                    new BlockItem(
                    ModBlocks.MOSSY_CALCITE_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> MOSSY_CALCITE_WALL = ITEMS.register(
            "mossy_calcite_wall",
            () ->
                    new BlockItem(
                    ModBlocks.MOSSY_CALCITE_WALL.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> PODZOL_SLAB = ITEMS.register(
            "podzol_slab",
            () ->
                    new BlockItem(ModBlocks.PODZOL_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> DIRT_SLAB = ITEMS.register(
            "dirt_slab",
            () -> new BlockItem(ModBlocks.DIRT_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> MUD = ITEMS.register("mud", () ->
            new BlockItem(ModBlocks.MUD.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> MUD_STAIRS = ITEMS.register("mud_stairs", () ->
            new BlockItem(ModBlocks.MUD_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> MUD_SLAB = ITEMS.register(
            "mud_slab",
            () -> new BlockItem(ModBlocks.MUD_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> MYCELIUM_SLAB = ITEMS.register(
            "mycelium_slab",
            () ->
                    new BlockItem(ModBlocks.MYCELIUM_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> MOSS_LAYERS = ITEMS.register(
            "moss_layers",
            () ->
                    new BlockItem(ModBlocks.MOSS_LAYERS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> MOSS_OVERLAY = ITEMS.register(
            "moss_overlay",
            () ->
                    new BlockItem(ModBlocks.MOSS_OVERLAY.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> SNOW_OVERLAY = ITEMS.register(
            "snow_overlay",
            () ->
                    new BlockItem(ModBlocks.SNOW_OVERLAY.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BLACK_CONCRETE_STAIRS =
            ITEMS.register("black_concrete_stairs", () ->
                    new BlockItem(
                    ModBlocks.BLACK_CONCRETE_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> BLUE_CONCRETE_STAIRS =
            ITEMS.register("blue_concrete_stairs", () ->
                    new BlockItem(
                    ModBlocks.BLUE_CONCRETE_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> BROWN_CONCRETE_STAIRS =
            ITEMS.register("brown_concrete_stairs", () ->
                    new BlockItem(
                    ModBlocks.BROWN_CONCRETE_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> CYAN_CONCRETE_STAIRS =
            ITEMS.register("cyan_concrete_stairs", () ->
                    new BlockItem(
                    ModBlocks.CYAN_CONCRETE_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> GRAY_CONCRETE_STAIRS =
            ITEMS.register("gray_concrete_stairs", () ->
                    new BlockItem(
                    ModBlocks.GRAY_CONCRETE_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> GREEN_CONCRETE_STAIRS =
            ITEMS.register("green_concrete_stairs", () ->
                    new BlockItem(
                    ModBlocks.GREEN_CONCRETE_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> LIGHT_BLUE_CONCRETE_STAIRS =
            ITEMS.register("light_blue_concrete_stairs", () ->
                            new BlockItem(
            ModBlocks.LIGHT_BLUE_CONCRETE_STAIRS.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> LIGHT_GRAY_CONCRETE_STAIRS =
            ITEMS.register("light_gray_concrete_stairs", () ->
                            new BlockItem(
            ModBlocks.LIGHT_GRAY_CONCRETE_STAIRS.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> LIME_CONCRETE_STAIRS =
            ITEMS.register("lime_concrete_stairs", () ->
                    new BlockItem(
                    ModBlocks.LIME_CONCRETE_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> MAGENTA_CONCRETE_STAIRS =
            ITEMS.register("magenta_concrete_stairs", () ->
                    new BlockItem(
                    ModBlocks.MAGENTA_CONCRETE_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> ORANGE_CONCRETE_STAIRS =
            ITEMS.register("orange_concrete_stairs", () ->
                    new BlockItem(
                    ModBlocks.ORANGE_CONCRETE_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> PINK_CONCRETE_STAIRS =
            ITEMS.register("pink_concrete_stairs", () ->
                    new BlockItem(
                    ModBlocks.PINK_CONCRETE_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> PURPLE_CONCRETE_STAIRS =
            ITEMS.register("purple_concrete_stairs", () ->
                    new BlockItem(
                    ModBlocks.PURPLE_CONCRETE_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> RED_CONCRETE_STAIRS = ITEMS.register(
            "red_concrete_stairs",
            () ->
                    new BlockItem(
                    ModBlocks.RED_CONCRETE_STAIRS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> WHITE_CONCRETE_STAIRS =
            ITEMS.register("white_concrete_stairs", () ->
                    new BlockItem(
                    ModBlocks.WHITE_CONCRETE_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> YELLOW_CONCRETE_STAIRS =
            ITEMS.register("yellow_concrete_stairs", () ->
                    new BlockItem(
                    ModBlocks.YELLOW_CONCRETE_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> WHITE_CONCRETE_WALL = ITEMS.register(
            "white_concrete_wall",
            () ->
                    new BlockItem(
                    ModBlocks.WHITE_CONCRETE_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> LIGHT_GRAY_CONCRETE_WALL =
            ITEMS.register("light_gray_concrete_wall", () ->
                    new BlockItem(
                    ModBlocks.LIGHT_GRAY_CONCRETE_WALL.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> GRAY_CONCRETE_WALL = ITEMS.register(
            "gray_concrete_wall",
            () ->
                    new BlockItem(
                    ModBlocks.GRAY_CONCRETE_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BLACK_CONCRETE_WALL = ITEMS.register(
            "black_concrete_wall",
            () ->
                    new BlockItem(
                    ModBlocks.BLACK_CONCRETE_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BROWN_CONCRETE_WALL = ITEMS.register(
            "brown_concrete_wall",
            () ->
                    new BlockItem(
                    ModBlocks.BROWN_CONCRETE_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> RED_CONCRETE_WALL = ITEMS.register(
            "red_concrete_wall",
            () ->
                    new BlockItem(
                    ModBlocks.RED_CONCRETE_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> ORANGE_CONCRETE_WALL =
            ITEMS.register("orange_concrete_wall", () ->
                    new BlockItem(
                    ModBlocks.ORANGE_CONCRETE_WALL.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> YELLOW_CONCRETE_WALL =
            ITEMS.register("yellow_concrete_wall", () ->
                    new BlockItem(
                    ModBlocks.YELLOW_CONCRETE_WALL.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> LIME_CONCRETE_WALL = ITEMS.register(
            "lime_concrete_wall",
            () ->
                    new BlockItem(
                    ModBlocks.LIME_CONCRETE_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> GREEN_CONCRETE_WALL = ITEMS.register(
            "green_concrete_wall",
            () ->
                    new BlockItem(
                    ModBlocks.GREEN_CONCRETE_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> CYAN_CONCRETE_WALL = ITEMS.register(
            "cyan_concrete_wall",
            () ->
                    new BlockItem(
                    ModBlocks.CYAN_CONCRETE_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> LIGHT_BLUE_CONCRETE_WALL =
            ITEMS.register("light_blue_concrete_wall", () ->
                    new BlockItem(
                    ModBlocks.LIGHT_BLUE_CONCRETE_WALL.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> BLUE_CONCRETE_WALL = ITEMS.register(
            "blue_concrete_wall",
            () ->
                    new BlockItem(
                    ModBlocks.BLUE_CONCRETE_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> PURPLE_CONCRETE_WALL =
            ITEMS.register("purple_concrete_wall", () ->
                    new BlockItem(
                    ModBlocks.PURPLE_CONCRETE_WALL.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> MAGENTA_CONCRETE_WALL =
            ITEMS.register("magenta_concrete_wall", () ->
                    new BlockItem(
                    ModBlocks.MAGENTA_CONCRETE_WALL.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> PINK_CONCRETE_WALL = ITEMS.register(
            "pink_concrete_wall",
            () ->
                    new BlockItem(
                    ModBlocks.PINK_CONCRETE_WALL.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BLACK_CONCRETE_SLAB = ITEMS.register(
            "black_concrete_slab",
            () ->
                    new BlockItem(
                    ModBlocks.BLACK_CONCRETE_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BLUE_CONCRETE_SLAB = ITEMS.register(
            "blue_concrete_slab",
            () ->
                    new BlockItem(
                    ModBlocks.BLUE_CONCRETE_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BROWN_CONCRETE_SLAB = ITEMS.register(
            "brown_concrete_slab",
            () ->
                    new BlockItem(
                    ModBlocks.BROWN_CONCRETE_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> CYAN_CONCRETE_SLAB = ITEMS.register(
            "cyan_concrete_slab",
            () ->
                    new BlockItem(
                    ModBlocks.CYAN_CONCRETE_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> GRAY_CONCRETE_SLAB = ITEMS.register(
            "gray_concrete_slab",
            () ->
                    new BlockItem(
                    ModBlocks.GRAY_CONCRETE_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> GREEN_CONCRETE_SLAB = ITEMS.register(
            "green_concrete_slab",
            () ->
                    new BlockItem(
                    ModBlocks.GREEN_CONCRETE_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> LIGHT_BLUE_CONCRETE_SLAB =
            ITEMS.register("light_blue_concrete_slab", () ->
                    new BlockItem(
                    ModBlocks.LIGHT_BLUE_CONCRETE_SLAB.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> LIGHT_GRAY_CONCRETE_SLAB =
            ITEMS.register("light_gray_concrete_slab", () ->
                    new BlockItem(
                    ModBlocks.LIGHT_GRAY_CONCRETE_SLAB.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> LIME_CONCRETE_SLAB = ITEMS.register(
            "lime_concrete_slab",
            () ->
                    new BlockItem(
                    ModBlocks.LIME_CONCRETE_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> MAGENTA_CONCRETE_SLAB =
            ITEMS.register("magenta_concrete_slab", () ->
                    new BlockItem(
                    ModBlocks.MAGENTA_CONCRETE_SLAB.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> ORANGE_CONCRETE_SLAB =
            ITEMS.register("orange_concrete_slab", () ->
                    new BlockItem(
                    ModBlocks.ORANGE_CONCRETE_SLAB.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> PINK_CONCRETE_SLAB = ITEMS.register(
            "pink_concrete_slab",
            () ->
                    new BlockItem(
                    ModBlocks.PINK_CONCRETE_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> PURPLE_CONCRETE_SLAB =
            ITEMS.register("purple_concrete_slab", () ->
                    new BlockItem(
                    ModBlocks.PURPLE_CONCRETE_SLAB.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> RED_CONCRETE_SLAB = ITEMS.register(
            "red_concrete_slab",
            () ->
                    new BlockItem(
                    ModBlocks.RED_CONCRETE_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> WHITE_CONCRETE_SLAB = ITEMS.register(
            "white_concrete_slab",
            () ->
                    new BlockItem(
                    ModBlocks.WHITE_CONCRETE_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> YELLOW_CONCRETE_SLAB =
            ITEMS.register("yellow_concrete_slab", () ->
                    new BlockItem(
                    ModBlocks.YELLOW_CONCRETE_SLAB.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BROWN_MUSHROOM_SHELVES =
            ITEMS.register("brown_mushroom_shelves", () ->
                    new BlockItem(
                    ModBlocks.BROWN_MUSHROOM_SHELVES.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> RED_MUSHROOM_SHELVES =
            ITEMS.register("red_mushroom_shelves", () ->
                    new BlockItem(
                    ModBlocks.RED_MUSHROOM_SHELVES.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> QUARTZ_PILLAR = ITEMS.register(
            "quartz_pillar",
            () ->
                    new BlockItem(ModBlocks.QUARTZ_PILLAR.get(), createBlockItemProperties())
    );



    public static final RegistryObject<Item> STONE_PILLAR = ITEMS.register(
            "stone_pillar",
            () ->
                    new BlockItem(ModBlocks.STONE_PILLAR.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> DEEPSLATE_PILLAR = ITEMS.register(
            "deepslate_pillar",
            () ->
                    new BlockItem(
                    ModBlocks.DEEPSLATE_PILLAR.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> MOSSY_PILLAR = ITEMS.register(
            "mossy_pillar",
            () ->
                    new BlockItem(ModBlocks.MOSSY_PILLAR.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> AMETHYST_PILLAR = ITEMS.register("amethyst_pillar", () -> new BlockItem(ModBlocks.AMETHYST_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ANDESITE_PILLAR = ITEMS.register("andesite_pillar", () -> new BlockItem(ModBlocks.ANDESITE_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BASALT_PILLAR = ITEMS.register("basalt_pillar", () -> new BlockItem(ModBlocks.BASALT_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACKSTONE_PILLAR = ITEMS.register("blackstone_pillar", () -> new BlockItem(ModBlocks.BLACKSTONE_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_ICE_PILLAR = ITEMS.register("blue_ice_pillar", () -> new BlockItem(ModBlocks.BLUE_ICE_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CALCITE_PILLAR = ITEMS.register("calcite_pillar", () -> new BlockItem(ModBlocks.CALCITE_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CINNABAR_PILLAR = ITEMS.register("cinnabar_pillar", () -> new BlockItem(ModBlocks.CINNABAR_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> COPPER_PILLAR = ITEMS.register("copper_pillar", () -> new BlockItem(ModBlocks.COPPER_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> DARK_PRISMARINE_PILLAR = ITEMS.register("dark_prismarine_pillar", () -> new BlockItem(ModBlocks.DARK_PRISMARINE_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> DIORITE_PILLAR = ITEMS.register("diorite_pillar", () -> new BlockItem(ModBlocks.DIORITE_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> DRIPSTONE_PILLAR = ITEMS.register("dripstone_pillar", () -> new BlockItem(ModBlocks.DRIPSTONE_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> EXPOSED_COPPER_PILLAR = ITEMS.register("exposed_copper_pillar", () -> new BlockItem(ModBlocks.EXPOSED_COPPER_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRANITE_PILLAR = ITEMS.register("granite_pillar", () -> new BlockItem(ModBlocks.GRANITE_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> NETHERRACK_PILLAR = ITEMS.register("netherrack_pillar", () -> new BlockItem(ModBlocks.NETHERRACK_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OBSIDIAN_PILLAR = ITEMS.register("obsidian_pillar", () -> new BlockItem(ModBlocks.OBSIDIAN_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OXIDIZED_COPPER_PILLAR = ITEMS.register("oxidized_copper_pillar", () -> new BlockItem(ModBlocks.OXIDIZED_COPPER_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PACKED_MUD_PILLAR = ITEMS.register("packed_mud_pillar", () -> new BlockItem(ModBlocks.PACKED_MUD_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PRISMARINE_PILLAR = ITEMS.register("prismarine_pillar", () -> new BlockItem(ModBlocks.PRISMARINE_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SCULK_PILLAR = ITEMS.register("sculk_pillar", () -> new BlockItem(ModBlocks.SCULK_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SULFUR_PILLAR = ITEMS.register("sulfur_pillar", () -> new BlockItem(ModBlocks.SULFUR_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> TUFF_PILLAR = ITEMS.register("tuff_pillar", () -> new BlockItem(ModBlocks.TUFF_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WEATHERED_COPPER_PILLAR = ITEMS.register("weathered_copper_pillar", () -> new BlockItem(ModBlocks.WEATHERED_COPPER_PILLAR.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> ASHENKING_DIAMOND_PILLAR = ITEMS.register(
            "ashenking_diamond_pillar",
            () ->
                    new BlockItem(ModBlocks.ASHENKING_DIAMOND_PILLAR.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> ASHENKING_GOLD_PILLAR = ITEMS.register(
            "ashenking_gold_pillar",
            () ->
                    new BlockItem(ModBlocks.ASHENKING_GOLD_PILLAR.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> ASHENKING_EMERALD_PILLAR = ITEMS.register(
            "ashenking_emerald_pillar",
            () ->
                    new BlockItem(ModBlocks.ASHENKING_EMERALD_PILLAR.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> ASHENKING_NETHERITE_PILLAR = ITEMS.register(
            "ashenking_netherite_pillar",
            () ->
                    new BlockItem(ModBlocks.ASHENKING_NETHERITE_PILLAR.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> DECORATED_POT = ITEMS.register(
            "decorated_pot",
            () ->
                    new BlockItem(ModBlocks.DECORATED_POT.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BLACK_DECORATED_POT = ITEMS.register(
            "black_decorated_pot",
            () ->
                    new BlockItem(
                    ModBlocks.BLACK_DECORATED_POT.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BLUE_DECORATED_POT = ITEMS.register(
            "blue_decorated_pot",
            () ->
                    new BlockItem(
                    ModBlocks.BLUE_DECORATED_POT.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BROWN_DECORATED_POT = ITEMS.register(
            "brown_decorated_pot",
            () ->
                    new BlockItem(
                    ModBlocks.BROWN_DECORATED_POT.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> CYAN_DECORATED_POT = ITEMS.register(
            "cyan_decorated_pot",
            () ->
                    new BlockItem(
                    ModBlocks.CYAN_DECORATED_POT.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> GRAY_DECORATED_POT = ITEMS.register(
            "gray_decorated_pot",
            () ->
                    new BlockItem(
                    ModBlocks.GRAY_DECORATED_POT.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> GREEN_DECORATED_POT = ITEMS.register(
            "green_decorated_pot",
            () ->
                    new BlockItem(
                    ModBlocks.GREEN_DECORATED_POT.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> LIGHT_BLUE_DECORATED_POT =
            ITEMS.register("light_blue_decorated_pot", () ->
                    new BlockItem(
                    ModBlocks.LIGHT_BLUE_DECORATED_POT.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> LIGHT_GRAY_DECORATED_POT =
            ITEMS.register("light_gray_decorated_pot", () ->
                    new BlockItem(
                    ModBlocks.LIGHT_GRAY_DECORATED_POT.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> LIME_DECORATED_POT = ITEMS.register(
            "lime_decorated_pot",
            () ->
                    new BlockItem(
                    ModBlocks.LIME_DECORATED_POT.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> MAGENTA_DECORATED_POT =
            ITEMS.register("magenta_decorated_pot", () ->
                    new BlockItem(
                    ModBlocks.MAGENTA_DECORATED_POT.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> ORANGE_DECORATED_POT =
            ITEMS.register("orange_decorated_pot", () ->
                    new BlockItem(
                    ModBlocks.ORANGE_DECORATED_POT.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> PINK_DECORATED_POT = ITEMS.register(
            "pink_decorated_pot",
            () ->
                    new BlockItem(
                    ModBlocks.PINK_DECORATED_POT.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> PURPLE_DECORATED_POT =
            ITEMS.register("purple_decorated_pot", () ->
                    new BlockItem(
                    ModBlocks.PURPLE_DECORATED_POT.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> RED_DECORATED_POT = ITEMS.register(
            "red_decorated_pot",
            () ->
                    new BlockItem(
                    ModBlocks.RED_DECORATED_POT.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> WHITE_DECORATED_POT = ITEMS.register(
            "white_decorated_pot",
            () ->
                    new BlockItem(
                    ModBlocks.WHITE_DECORATED_POT.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> YELLOW_DECORATED_POT =
            ITEMS.register("yellow_decorated_pot", () ->
                    new BlockItem(
                    ModBlocks.YELLOW_DECORATED_POT.get(),
                            createBlockItemProperties()
                    )
            );


    public static final RegistryObject<Item> TRAPPED_DECORATED_POT = ITEMS.register(
            "trapped_decorated_pot",
            () -> new BlockItem(ModBlocks.TRAPPED_DECORATED_POT.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BLACK_TRAPPED_DECORATED_POT = ITEMS.register(
            "black_trapped_decorated_pot",
            () -> new BlockItem(ModBlocks.BLACK_TRAPPED_DECORATED_POT.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BLUE_TRAPPED_DECORATED_POT = ITEMS.register(
            "blue_trapped_decorated_pot",
            () -> new BlockItem(ModBlocks.BLUE_TRAPPED_DECORATED_POT.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BROWN_TRAPPED_DECORATED_POT = ITEMS.register(
            "brown_trapped_decorated_pot",
            () -> new BlockItem(ModBlocks.BROWN_TRAPPED_DECORATED_POT.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> CYAN_TRAPPED_DECORATED_POT = ITEMS.register(
            "cyan_trapped_decorated_pot",
            () -> new BlockItem(ModBlocks.CYAN_TRAPPED_DECORATED_POT.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> GRAY_TRAPPED_DECORATED_POT = ITEMS.register(
            "gray_trapped_decorated_pot",
            () -> new BlockItem(ModBlocks.GRAY_TRAPPED_DECORATED_POT.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> GREEN_TRAPPED_DECORATED_POT = ITEMS.register(
            "green_trapped_decorated_pot",
            () -> new BlockItem(ModBlocks.GREEN_TRAPPED_DECORATED_POT.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIGHT_BLUE_TRAPPED_DECORATED_POT = ITEMS.register(
            "light_blue_trapped_decorated_pot",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_TRAPPED_DECORATED_POT.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIGHT_GRAY_TRAPPED_DECORATED_POT = ITEMS.register(
            "light_gray_trapped_decorated_pot",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_TRAPPED_DECORATED_POT.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIME_TRAPPED_DECORATED_POT = ITEMS.register(
            "lime_trapped_decorated_pot",
            () -> new BlockItem(ModBlocks.LIME_TRAPPED_DECORATED_POT.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> MAGENTA_TRAPPED_DECORATED_POT = ITEMS.register(
            "magenta_trapped_decorated_pot",
            () -> new BlockItem(ModBlocks.MAGENTA_TRAPPED_DECORATED_POT.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> ORANGE_TRAPPED_DECORATED_POT = ITEMS.register(
            "orange_trapped_decorated_pot",
            () -> new BlockItem(ModBlocks.ORANGE_TRAPPED_DECORATED_POT.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PINK_TRAPPED_DECORATED_POT = ITEMS.register(
            "pink_trapped_decorated_pot",
            () -> new BlockItem(ModBlocks.PINK_TRAPPED_DECORATED_POT.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PURPLE_TRAPPED_DECORATED_POT = ITEMS.register(
            "purple_trapped_decorated_pot",
            () -> new BlockItem(ModBlocks.PURPLE_TRAPPED_DECORATED_POT.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> RED_TRAPPED_DECORATED_POT = ITEMS.register(
            "red_trapped_decorated_pot",
            () -> new BlockItem(ModBlocks.RED_TRAPPED_DECORATED_POT.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WHITE_TRAPPED_DECORATED_POT = ITEMS.register(
            "white_trapped_decorated_pot",
            () -> new BlockItem(ModBlocks.WHITE_TRAPPED_DECORATED_POT.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> YELLOW_TRAPPED_DECORATED_POT = ITEMS.register(
            "yellow_trapped_decorated_pot",
            () -> new BlockItem(ModBlocks.YELLOW_TRAPPED_DECORATED_POT.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FESTIVE_STOCKING = ITEMS.register(
            "festive_stocking",
            () ->
                    new com.kingodogo.buildscape.item.FestiveStockingItem(
                    createBlockItemProperties(),
                            "festive"
                    )
    );
    public static final RegistryObject<Item> BLACK_FESTIVE_STOCKING =
            ITEMS.register("black_festive_stocking", () ->
                    new com.kingodogo.buildscape.item.FestiveStockingItem(
                    createBlockItemProperties(),
                            "black"
                    )
            );
    public static final RegistryObject<Item> BLUE_FESTIVE_STOCKING =
            ITEMS.register("blue_festive_stocking", () ->
                    new com.kingodogo.buildscape.item.FestiveStockingItem(
                    createBlockItemProperties(),
                            "blue"
                    )
            );
    public static final RegistryObject<Item> BROWN_FESTIVE_STOCKING =
            ITEMS.register("brown_festive_stocking", () ->
                    new com.kingodogo.buildscape.item.FestiveStockingItem(
                    createBlockItemProperties(),
                            "brown"
                    )
            );
    public static final RegistryObject<Item> CYAN_FESTIVE_STOCKING =
            ITEMS.register("cyan_festive_stocking", () ->
                    new com.kingodogo.buildscape.item.FestiveStockingItem(
                    createBlockItemProperties(),
                            "cyan"
                    )
            );
    public static final RegistryObject<Item> GRAY_FESTIVE_STOCKING =
            ITEMS.register("gray_festive_stocking", () ->
                    new com.kingodogo.buildscape.item.FestiveStockingItem(
                    createBlockItemProperties(),
                            "gray"
                    )
            );
    public static final RegistryObject<Item> GREEN_FESTIVE_STOCKING =
            ITEMS.register("green_festive_stocking", () ->
                    new com.kingodogo.buildscape.item.FestiveStockingItem(
                    createBlockItemProperties(),
                            "green"
                    )
            );
    public static final RegistryObject<Item> LIGHT_BLUE_FESTIVE_STOCKING =
            ITEMS.register("light_blue_festive_stocking", () ->
                    new com.kingodogo.buildscape.item.FestiveStockingItem(
                            createBlockItemProperties(),
                            "light_blue"
                    )
            );
    public static final RegistryObject<Item> LIGHT_GRAY_FESTIVE_STOCKING =
            ITEMS.register("light_gray_festive_stocking", () ->
                    new com.kingodogo.buildscape.item.FestiveStockingItem(
                            createBlockItemProperties(),
                            "light_gray"
                    )
            );
    public static final RegistryObject<Item> LIME_FESTIVE_STOCKING =
            ITEMS.register("lime_festive_stocking", () ->
                    new com.kingodogo.buildscape.item.FestiveStockingItem(
                    createBlockItemProperties(),
                            "lime"
                    )
            );
    public static final RegistryObject<Item> MAGENTA_FESTIVE_STOCKING =
            ITEMS.register("magenta_festive_stocking", () ->
                    new com.kingodogo.buildscape.item.FestiveStockingItem(
                    createBlockItemProperties(),
                            "magenta"
                    )
            );
    public static final RegistryObject<Item> ORANGE_FESTIVE_STOCKING =
            ITEMS.register("orange_festive_stocking", () ->
                    new com.kingodogo.buildscape.item.FestiveStockingItem(
                    createBlockItemProperties(),
                            "orange"
                    )
            );
    public static final RegistryObject<Item> PINK_FESTIVE_STOCKING =
            ITEMS.register("pink_festive_stocking", () ->
                    new com.kingodogo.buildscape.item.FestiveStockingItem(
                    createBlockItemProperties(),
                            "pink"
                    )
            );
    public static final RegistryObject<Item> PURPLE_FESTIVE_STOCKING =
            ITEMS.register("purple_festive_stocking", () ->
                    new com.kingodogo.buildscape.item.FestiveStockingItem(
                    createBlockItemProperties(),
                            "purple"
                    )
            );
    public static final RegistryObject<Item> RED_FESTIVE_STOCKING =
            ITEMS.register("red_festive_stocking", () ->
                    new com.kingodogo.buildscape.item.FestiveStockingItem(
                    createBlockItemProperties(),
                            "red"
                    )
            );
    public static final RegistryObject<Item> WHITE_FESTIVE_STOCKING =
            ITEMS.register("white_festive_stocking", () ->
                    new com.kingodogo.buildscape.item.FestiveStockingItem(
                    createBlockItemProperties(),
                            "white"
                    )
            );
    public static final RegistryObject<Item> YELLOW_FESTIVE_STOCKING =
            ITEMS.register("yellow_festive_stocking", () ->
                    new com.kingodogo.buildscape.item.FestiveStockingItem(
                    createBlockItemProperties(),
                            "yellow"
                    )
            );

    public static final RegistryObject<Item> BLACK_CARPET_LAYERS = ITEMS.register(
            "black_carpet_layers",
            () ->
                    new BlockItem(
                    ModBlocks.BLACK_CARPET_LAYERS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BLUE_CARPET_LAYERS = ITEMS.register(
            "blue_carpet_layers",
            () ->
                    new BlockItem(
                    ModBlocks.BLUE_CARPET_LAYERS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BROWN_CARPET_LAYERS = ITEMS.register(
            "brown_carpet_layers",
            () ->
                    new BlockItem(
                    ModBlocks.BROWN_CARPET_LAYERS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> CYAN_CARPET_LAYERS = ITEMS.register(
            "cyan_carpet_layers",
            () ->
                    new BlockItem(
                    ModBlocks.CYAN_CARPET_LAYERS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> GRAY_CARPET_LAYERS = ITEMS.register(
            "gray_carpet_layers",
            () ->
                    new BlockItem(
                    ModBlocks.GRAY_CARPET_LAYERS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> GREEN_CARPET_LAYERS = ITEMS.register(
            "green_carpet_layers",
            () ->
                    new BlockItem(
                    ModBlocks.GREEN_CARPET_LAYERS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> LIGHT_BLUE_CARPET_LAYERS =
            ITEMS.register("light_blue_carpet_layers", () ->
                    new BlockItem(
                    ModBlocks.LIGHT_BLUE_CARPET_LAYERS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> LIGHT_GRAY_CARPET_LAYERS =
            ITEMS.register("light_gray_carpet_layers", () ->
                    new BlockItem(
                    ModBlocks.LIGHT_GRAY_CARPET_LAYERS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> LIME_CARPET_LAYERS = ITEMS.register(
            "lime_carpet_layers",
            () ->
                    new BlockItem(
                    ModBlocks.LIME_CARPET_LAYERS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> MAGENTA_CARPET_LAYERS =
            ITEMS.register("magenta_carpet_layers", () ->
                    new BlockItem(
                    ModBlocks.MAGENTA_CARPET_LAYERS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> ORANGE_CARPET_LAYERS =
            ITEMS.register("orange_carpet_layers", () ->
                    new BlockItem(
                    ModBlocks.ORANGE_CARPET_LAYERS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> PINK_CARPET_LAYERS = ITEMS.register(
            "pink_carpet_layers",
            () ->
                    new BlockItem(
                    ModBlocks.PINK_CARPET_LAYERS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> PURPLE_CARPET_LAYERS =
            ITEMS.register("purple_carpet_layers", () ->
                    new BlockItem(
                    ModBlocks.PURPLE_CARPET_LAYERS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> RED_CARPET_LAYERS = ITEMS.register(
            "red_carpet_layers",
            () ->
                    new BlockItem(
                    ModBlocks.RED_CARPET_LAYERS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> WHITE_CARPET_LAYERS = ITEMS.register(
            "white_carpet_layers",
            () ->
                    new BlockItem(
                    ModBlocks.WHITE_CARPET_LAYERS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> YELLOW_CARPET_LAYERS =
            ITEMS.register("yellow_carpet_layers", () ->
                    new BlockItem(
                    ModBlocks.YELLOW_CARPET_LAYERS.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> OAK_LEAF_LAYERS = ITEMS.register(
            "oak_leaf_layers",
            () ->
                    new BlockItem(
                    ModBlocks.OAK_LEAF_LAYERS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> SPRUCE_LEAF_LAYERS = ITEMS.register(
            "spruce_leaf_layers",
            () ->
                    new BlockItem(
                    ModBlocks.SPRUCE_LEAF_LAYERS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BIRCH_LEAF_LAYERS = ITEMS.register(
            "birch_leaf_layers",
            () ->
                    new BlockItem(
                    ModBlocks.BIRCH_LEAF_LAYERS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> JUNGLE_LEAF_LAYERS = ITEMS.register(
            "jungle_leaf_layers",
            () ->
                    new BlockItem(
                    ModBlocks.JUNGLE_LEAF_LAYERS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> ACACIA_LEAF_LAYERS = ITEMS.register(
            "acacia_leaf_layers",
            () ->
                    new BlockItem(
                    ModBlocks.ACACIA_LEAF_LAYERS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> DARK_OAK_LEAF_LAYERS =
            ITEMS.register("dark_oak_leaf_layers", () ->
                    new BlockItem(
                    ModBlocks.DARK_OAK_LEAF_LAYERS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> AZALEA_LEAF_LAYERS = ITEMS.register(
            "azalea_leaf_layers",
            () ->
                    new BlockItem(
                    ModBlocks.AZALEA_LEAF_LAYERS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> FLOWERING_AZALEA_LEAF_LAYERS =
            ITEMS.register("flowering_azalea_leaf_layers", () ->
                            new BlockItem(
            ModBlocks.FLOWERING_AZALEA_LEAF_LAYERS.get(),
                                    createBlockItemProperties()
                            )
            );

    public static final RegistryObject<Item> SNOWY_OAK_LEAF_LAYERS =
            ITEMS.register("snowy_oak_leaf_layers", () ->
                    new BlockItem(
                    ModBlocks.SNOWY_OAK_LEAF_LAYERS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> SNOWY_SPRUCE_LEAF_LAYERS =
            ITEMS.register("snowy_spruce_leaf_layers", () ->
                    new BlockItem(
                    ModBlocks.SNOWY_SPRUCE_LEAF_LAYERS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> SNOWY_BIRCH_LEAF_LAYERS =
            ITEMS.register("snowy_birch_leaf_layers", () ->
                    new BlockItem(
                    ModBlocks.SNOWY_BIRCH_LEAF_LAYERS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> SNOWY_JUNGLE_LEAF_LAYERS =
            ITEMS.register("snowy_jungle_leaf_layers", () ->
                    new BlockItem(
                    ModBlocks.SNOWY_JUNGLE_LEAF_LAYERS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> SNOWY_ACACIA_LEAF_LAYERS =
            ITEMS.register("snowy_acacia_leaf_layers", () ->
                    new BlockItem(
                    ModBlocks.SNOWY_ACACIA_LEAF_LAYERS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> SNOWY_DARK_OAK_LEAF_LAYERS =
            ITEMS.register("snowy_dark_oak_leaf_layers", () ->
                            new BlockItem(
            ModBlocks.SNOWY_DARK_OAK_LEAF_LAYERS.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> SNOWY_MANGROVE_LEAF_LAYERS =
            ITEMS.register("snowy_mangrove_leaf_layers", () ->
                            new BlockItem(
            ModBlocks.SNOWY_MANGROVE_LEAF_LAYERS.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> SNOWY_AZALEA_LEAF_LAYERS =
            ITEMS.register("snowy_azalea_leaf_layers", () ->
                    new BlockItem(
                    ModBlocks.SNOWY_AZALEA_LEAF_LAYERS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> SNOWY_FLOWERING_AZALEA_LEAF_LAYERS =
            ITEMS.register("snowy_flowering_azalea_leaf_layers", () ->
                            new BlockItem(
            ModBlocks.SNOWY_FLOWERING_AZALEA_LEAF_LAYERS.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> MANGROVE_LEAF_LAYERS =
            ITEMS.register("mangrove_leaf_layers", () ->
                    new BlockItem(
                    ModBlocks.MANGROVE_LEAF_LAYERS.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> ORANGE_POPLAR_LEAF_LAYERS =
            ITEMS.register("orange_poplar_leaf_layers", () ->
                    new BlockItem(
                    ModBlocks.ORANGE_POPLAR_LEAF_LAYERS.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> RED_POPLAR_LEAF_LAYERS =
            ITEMS.register("red_poplar_leaf_layers", () ->
                    new BlockItem(
                    ModBlocks.RED_POPLAR_LEAF_LAYERS.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> YELLOW_POPLAR_LEAF_LAYERS =
            ITEMS.register("yellow_poplar_leaf_layers", () ->
                    new BlockItem(
                    ModBlocks.YELLOW_POPLAR_LEAF_LAYERS.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> CHERRY_LEAF_LAYERS =
            ITEMS.register("cherry_leaf_layers", () ->
                    new BlockItem(
                    ModBlocks.CHERRY_LEAF_LAYERS.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> PALE_OAK_LEAF_LAYERS =
            ITEMS.register("pale_oak_leaf_layers", () ->
                    new BlockItem(
                    ModBlocks.PALE_OAK_LEAF_LAYERS.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> SNOWY_CHERRY_LEAF_LAYERS =
            ITEMS.register("snowy_cherry_leaf_layers", () ->
                    new BlockItem(
                    ModBlocks.SNOWY_CHERRY_LEAF_LAYERS.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> SNOWY_PALE_OAK_LEAF_LAYERS =
            ITEMS.register("snowy_pale_oak_leaf_layers", () ->
                    new BlockItem(
                    ModBlocks.SNOWY_PALE_OAK_LEAF_LAYERS.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> SNOWY_ORANGE_POPLAR_LEAF_LAYERS =
            ITEMS.register("snowy_orange_poplar_leaf_layers", () ->
                    new BlockItem(
                    ModBlocks.SNOWY_ORANGE_POPLAR_LEAF_LAYERS.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> SNOWY_RED_POPLAR_LEAF_LAYERS =
            ITEMS.register("snowy_red_poplar_leaf_layers", () ->
                    new BlockItem(
                    ModBlocks.SNOWY_RED_POPLAR_LEAF_LAYERS.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> SNOWY_YELLOW_POPLAR_LEAF_LAYERS =
            ITEMS.register("snowy_yellow_poplar_leaf_layers", () ->
                    new BlockItem(
                    ModBlocks.SNOWY_YELLOW_POPLAR_LEAF_LAYERS.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> OAK_LEAF_HEDGE = ITEMS.register(
            "oak_leaf_hedge",
            () ->
                    new BlockItem(ModBlocks.OAK_LEAF_HEDGE.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> SPRUCE_LEAF_HEDGE = ITEMS.register(
            "spruce_leaf_hedge",
            () ->
                    new BlockItem(
                    ModBlocks.SPRUCE_LEAF_HEDGE.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BIRCH_LEAF_HEDGE = ITEMS.register(
            "birch_leaf_hedge",
            () ->
                    new BlockItem(
                    ModBlocks.BIRCH_LEAF_HEDGE.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> JUNGLE_LEAF_HEDGE = ITEMS.register(
            "jungle_leaf_hedge",
            () ->
                    new BlockItem(
                    ModBlocks.JUNGLE_LEAF_HEDGE.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> ACACIA_LEAF_HEDGE = ITEMS.register(
            "acacia_leaf_hedge",
            () ->
                    new BlockItem(
                    ModBlocks.ACACIA_LEAF_HEDGE.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> DARK_OAK_LEAF_HEDGE = ITEMS.register(
            "dark_oak_leaf_hedge",
            () ->
                    new BlockItem(
                    ModBlocks.DARK_OAK_LEAF_HEDGE.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> AZALEA_LEAF_HEDGE = ITEMS.register(
            "azalea_leaf_hedge",
            () ->
                    new BlockItem(
                    ModBlocks.AZALEA_LEAF_HEDGE.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> FLOWERING_AZALEA_LEAF_HEDGE =
            ITEMS.register("flowering_azalea_leaf_hedge", () ->
                            new BlockItem(
            ModBlocks.FLOWERING_AZALEA_LEAF_HEDGE.get(),
                                    createBlockItemProperties()
                            )
            );

    public static final RegistryObject<Item> SNOWY_OAK_LEAF_HEDGE =
            ITEMS.register("snowy_oak_leaf_hedge", () ->
                    new BlockItem(
                    ModBlocks.SNOWY_OAK_LEAF_HEDGE.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> SNOWY_SPRUCE_LEAF_HEDGE =
            ITEMS.register("snowy_spruce_leaf_hedge", () ->
                    new BlockItem(
                    ModBlocks.SNOWY_SPRUCE_LEAF_HEDGE.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> SNOWY_BIRCH_LEAF_HEDGE =
            ITEMS.register("snowy_birch_leaf_hedge", () ->
                    new BlockItem(
                    ModBlocks.SNOWY_BIRCH_LEAF_HEDGE.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> SNOWY_JUNGLE_LEAF_HEDGE =
            ITEMS.register("snowy_jungle_leaf_hedge", () ->
                    new BlockItem(
                    ModBlocks.SNOWY_JUNGLE_LEAF_HEDGE.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> SNOWY_ACACIA_LEAF_HEDGE =
            ITEMS.register("snowy_acacia_leaf_hedge", () ->
                    new BlockItem(
                    ModBlocks.SNOWY_ACACIA_LEAF_HEDGE.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> SNOWY_DARK_OAK_LEAF_HEDGE =
            ITEMS.register("snowy_dark_oak_leaf_hedge", () ->
                    new BlockItem(
                    ModBlocks.SNOWY_DARK_OAK_LEAF_HEDGE.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> SNOWY_MANGROVE_LEAF_HEDGE =
            ITEMS.register("snowy_mangrove_leaf_hedge", () ->
                    new BlockItem(
                    ModBlocks.SNOWY_MANGROVE_LEAF_HEDGE.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> SNOWY_AZALEA_LEAF_HEDGE =
            ITEMS.register("snowy_azalea_leaf_hedge", () ->
                    new BlockItem(
                    ModBlocks.SNOWY_AZALEA_LEAF_HEDGE.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> SNOWY_FLOWERING_AZALEA_LEAF_HEDGE =
            ITEMS.register("snowy_flowering_azalea_leaf_hedge", () ->
                            new BlockItem(
            ModBlocks.SNOWY_FLOWERING_AZALEA_LEAF_HEDGE.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> MANGROVE_LEAF_HEDGE = ITEMS.register(
            "mangrove_leaf_hedge",
            () ->
                    new BlockItem(
                    ModBlocks.MANGROVE_LEAF_HEDGE.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> ORANGE_POPLAR_LEAF_HEDGE =
            ITEMS.register("orange_poplar_leaf_hedge", () ->
                    new BlockItem(
                    ModBlocks.ORANGE_POPLAR_LEAF_HEDGE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> RED_POPLAR_LEAF_HEDGE =
            ITEMS.register("red_poplar_leaf_hedge", () ->
                    new BlockItem(
                    ModBlocks.RED_POPLAR_LEAF_HEDGE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> YELLOW_POPLAR_LEAF_HEDGE =
            ITEMS.register("yellow_poplar_leaf_hedge", () ->
                    new BlockItem(
                    ModBlocks.YELLOW_POPLAR_LEAF_HEDGE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> CHERRY_LEAF_HEDGE =
            ITEMS.register("cherry_leaf_hedge", () ->
                    new BlockItem(
                    ModBlocks.CHERRY_LEAF_HEDGE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> PALE_OAK_LEAF_HEDGE =
            ITEMS.register("pale_oak_leaf_hedge", () ->
                    new BlockItem(
                    ModBlocks.PALE_OAK_LEAF_HEDGE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> SNOWY_CHERRY_LEAF_HEDGE =
            ITEMS.register("snowy_cherry_leaf_hedge", () ->
                    new BlockItem(
                    ModBlocks.SNOWY_CHERRY_LEAF_HEDGE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> SNOWY_PALE_OAK_LEAF_HEDGE =
            ITEMS.register("snowy_pale_oak_leaf_hedge", () ->
                    new BlockItem(
                    ModBlocks.SNOWY_PALE_OAK_LEAF_HEDGE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> SNOWY_ORANGE_POPLAR_LEAF_HEDGE =
            ITEMS.register("snowy_orange_poplar_leaf_hedge", () ->
                    new BlockItem(
                    ModBlocks.SNOWY_ORANGE_POPLAR_LEAF_HEDGE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> SNOWY_RED_POPLAR_LEAF_HEDGE =
            ITEMS.register("snowy_red_poplar_leaf_hedge", () ->
                    new BlockItem(
                    ModBlocks.SNOWY_RED_POPLAR_LEAF_HEDGE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> SNOWY_YELLOW_POPLAR_LEAF_HEDGE =
            ITEMS.register("snowy_yellow_poplar_leaf_hedge", () ->
                    new BlockItem(
                    ModBlocks.SNOWY_YELLOW_POPLAR_LEAF_HEDGE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> HAY_BALE_SLAB = ITEMS.register(
            "hay_bale_slab",
            () ->
                    new BlockItem(ModBlocks.HAY_BALE_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> HAY_BALE_STAIRS = ITEMS.register("hay_bale_stairs",
            () -> new BlockItem(ModBlocks.HAY_BALE_STAIRS.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> BAMBOO_BLOCK = ITEMS.register(
            "bamboo_block",
            () ->
                    new BlockItem(ModBlocks.BAMBOO_BLOCK.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> STRIPPED_BAMBOO_BLOCK =
            ITEMS.register("stripped_bamboo_block", () ->
                    new BlockItem(
                    ModBlocks.STRIPPED_BAMBOO_BLOCK.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BAMBOO_WOOD = ITEMS.register(
            "bamboo_wood",
            () ->
                    new BlockItem(ModBlocks.BAMBOO_WOOD.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> STRIPPED_BAMBOO_WOOD = ITEMS.register(
            "stripped_bamboo_wood",
            () ->
                    new BlockItem(
                            ModBlocks.STRIPPED_BAMBOO_WOOD.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BAMBOO_BLOCK_SLAB = ITEMS.register(
            "bamboo_block_slab",
            () ->
                    new BlockItem(
                    ModBlocks.BAMBOO_BLOCK_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BAMBOO_BLOCK_STAIRS = ITEMS.register(
            "bamboo_block_stairs",
            () ->
                    new BlockItem(
                    ModBlocks.BAMBOO_BLOCK_STAIRS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BAMBOO_BLOCK_FENCE = ITEMS.register(
            "bamboo_block_fence",
            () ->
                    new BlockItem(
                    ModBlocks.BAMBOO_BLOCK_FENCE.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BAMBOO_BLOCK_FENCE_GATE =
            ITEMS.register("bamboo_block_fence_gate", () ->
                    new BlockItem(
                    ModBlocks.BAMBOO_BLOCK_FENCE_GATE.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> BAMBOO_BLOCK_PRESSURE_PLATE =
            ITEMS.register("bamboo_block_pressure_plate", () ->
                            new BlockItem(
            ModBlocks.BAMBOO_BLOCK_PRESSURE_PLATE.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> BAMBOO_BLOCK_BUTTON = ITEMS.register(
            "bamboo_block_button",
            () ->
                    new BlockItem(
                    ModBlocks.BAMBOO_BLOCK_BUTTON.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> STRIPPED_BAMBOO_BLOCK_SLAB =
            ITEMS.register("stripped_bamboo_block_slab", () ->
                            new BlockItem(
            ModBlocks.STRIPPED_BAMBOO_BLOCK_SLAB.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> STRIPPED_BAMBOO_BLOCK_STAIRS =
            ITEMS.register("stripped_bamboo_block_stairs", () ->
                            new BlockItem(
            ModBlocks.STRIPPED_BAMBOO_BLOCK_STAIRS.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> STRIPPED_BAMBOO_BLOCK_FENCE =
            ITEMS.register("stripped_bamboo_block_fence", () ->
                            new BlockItem(
            ModBlocks.STRIPPED_BAMBOO_BLOCK_FENCE.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> STRIPPED_BAMBOO_BLOCK_FENCE_GATE =
            ITEMS.register("stripped_bamboo_block_fence_gate", () ->
                            new BlockItem(
            ModBlocks.STRIPPED_BAMBOO_BLOCK_FENCE_GATE.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<
            Item
            > STRIPPED_BAMBOO_BLOCK_PRESSURE_PLATE = ITEMS.register(
            "stripped_bamboo_block_pressure_plate",
            () ->
                    new BlockItem(
                    ModBlocks.STRIPPED_BAMBOO_BLOCK_PRESSURE_PLATE.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> STRIPPED_BAMBOO_BLOCK_BUTTON =
            ITEMS.register("stripped_bamboo_block_button", () ->
                            new BlockItem(
            ModBlocks.STRIPPED_BAMBOO_BLOCK_BUTTON.get(),
                                    createBlockItemProperties()
                            )
            );

    public static final RegistryObject<Item> BAMBOO_DOOR = ITEMS.register(
            "bamboo_door",
            () ->
                    new BlockItem(ModBlocks.BAMBOO_DOOR.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BAMBOO_TRAPDOOR = ITEMS.register(
            "bamboo_trapdoor",
            () ->
                    new BlockItem(
                    ModBlocks.BAMBOO_TRAPDOOR.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> DIAMOND_CHAIN = ITEMS.register(
            "diamond_chain",
            () ->
                    new BlockItem(ModBlocks.DIAMOND_CHAIN.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> GOLD_CHAIN = ITEMS.register(
            "gold_chain",
            () -> new BlockItem(ModBlocks.GOLD_CHAIN.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> EMERALD_CHAIN = ITEMS.register(
            "emerald_chain",
            () ->
                    new BlockItem(ModBlocks.EMERALD_CHAIN.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> ANCIENT_STEEL_CHAIN = ITEMS.register(
            "ancient_steel_chain",
            () ->
                    new BlockItem(
                    ModBlocks.ANCIENT_STEEL_CHAIN.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> NETHERITE_CHAIN = ITEMS.register(
            "netherite_chain",
            () ->
                    new BlockItem(
                    ModBlocks.NETHERITE_CHAIN.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> COPPER_CHAIN = ITEMS.register(
            "copper_chain",
            () ->
                    new BlockItem(ModBlocks.COPPER_CHAIN.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> EXPOSED_COPPER_CHAIN =
            ITEMS.register("exposed_copper_chain", () ->
                    new BlockItem(
                    ModBlocks.EXPOSED_COPPER_CHAIN.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> WEATHERED_COPPER_CHAIN =
            ITEMS.register("weathered_copper_chain", () ->
                    new BlockItem(
                    ModBlocks.WEATHERED_COPPER_CHAIN.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> OXIDIZED_COPPER_CHAIN =
            ITEMS.register("oxidized_copper_chain", () ->
                    new BlockItem(
                    ModBlocks.OXIDIZED_COPPER_CHAIN.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> LARGE_IRON_CHAIN = ITEMS.register(
            "large_iron_chain",
            () ->
                    new BlockItem(
                    ModBlocks.LARGE_IRON_CHAIN.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> LARGE_COPPER_CHAIN = ITEMS.register(
            "large_copper_chain",
            () ->
                    new BlockItem(
                    ModBlocks.LARGE_COPPER_CHAIN.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> LARGE_EXPOSED_COPPER_CHAIN = ITEMS.register(
            "large_exposed_copper_chain",
            () ->
                    new BlockItem(
                    ModBlocks.LARGE_EXPOSED_COPPER_CHAIN.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> LARGE_WEATHERED_COPPER_CHAIN = ITEMS.register(
            "large_weathered_copper_chain",
            () ->
                    new BlockItem(
                    ModBlocks.LARGE_WEATHERED_COPPER_CHAIN.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> LARGE_OXIDIZED_COPPER_CHAIN = ITEMS.register(
            "large_oxidized_copper_chain",
            () ->
                    new BlockItem(
                    ModBlocks.LARGE_OXIDIZED_COPPER_CHAIN.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> LARGE_GOLD_CHAIN = ITEMS.register(
            "large_gold_chain",
            () ->
                    new BlockItem(
                    ModBlocks.LARGE_GOLD_CHAIN.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> LARGE_DIAMOND_CHAIN = ITEMS.register(
            "large_diamond_chain",
            () ->
                    new BlockItem(
                    ModBlocks.LARGE_DIAMOND_CHAIN.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> LARGE_EMERALD_CHAIN = ITEMS.register(
            "large_emerald_chain",
            () ->
                    new BlockItem(
                    ModBlocks.LARGE_EMERALD_CHAIN.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> LARGE_ANCIENT_STEEL_CHAIN =
            ITEMS.register("large_ancient_steel_chain", () ->
                    new BlockItem(
                    ModBlocks.LARGE_ANCIENT_STEEL_CHAIN.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> LARGE_NETHERITE_CHAIN =
            ITEMS.register("large_netherite_chain", () ->
                    new BlockItem(
                    ModBlocks.LARGE_NETHERITE_CHAIN.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> OAK_WOOD_WALL = ITEMS.register(
            "oak_wood_wall",
            () ->
                    new BlockItem(ModBlocks.OAK_WOOD_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> SPRUCE_WOOD_WALL = ITEMS.register(
            "spruce_wood_wall",
            () ->
                    new BlockItem(
                    ModBlocks.SPRUCE_WOOD_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BIRCH_WOOD_WALL = ITEMS.register(
            "birch_wood_wall",
            () ->
                    new BlockItem(
                    ModBlocks.BIRCH_WOOD_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> DARK_OAK_WOOD_WALL = ITEMS.register(
            "dark_oak_wood_wall",
            () ->
                    new BlockItem(
                    ModBlocks.DARK_OAK_WOOD_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> JUNGLE_WOOD_WALL = ITEMS.register(
            "jungle_wood_wall",
            () ->
                    new BlockItem(
                    ModBlocks.JUNGLE_WOOD_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> ACACIA_WOOD_WALL = ITEMS.register(
            "acacia_wood_wall",
            () ->
                    new BlockItem(
                    ModBlocks.ACACIA_WOOD_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BAMBOO_BLOCK_WALL = ITEMS.register(
            "bamboo_block_wall",
            () ->
                    new BlockItem(
                    ModBlocks.BAMBOO_BLOCK_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> MANGROVE_WOOD_WALL = ITEMS.register(
            "mangrove_wood_wall",
            () ->
                    new BlockItem(
                    ModBlocks.MANGROVE_WOOD_WALL.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> STRIPPED_OAK_WOOD_WALL =
            ITEMS.register("stripped_oak_wood_wall", () ->
                    new BlockItem(
                    ModBlocks.STRIPPED_OAK_WOOD_WALL.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> STRIPPED_SPRUCE_WOOD_WALL =
            ITEMS.register("stripped_spruce_wood_wall", () ->
                    new BlockItem(
                    ModBlocks.STRIPPED_SPRUCE_WOOD_WALL.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> STRIPPED_BIRCH_WOOD_WALL =
            ITEMS.register("stripped_birch_wood_wall", () ->
                    new BlockItem(
                    ModBlocks.STRIPPED_BIRCH_WOOD_WALL.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> STRIPPED_DARK_OAK_WOOD_WALL =
            ITEMS.register("stripped_dark_oak_wood_wall", () ->
                            new BlockItem(
            ModBlocks.STRIPPED_DARK_OAK_WOOD_WALL.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> STRIPPED_JUNGLE_WOOD_WALL =
            ITEMS.register("stripped_jungle_wood_wall", () ->
                    new BlockItem(
                    ModBlocks.STRIPPED_JUNGLE_WOOD_WALL.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> STRIPPED_ACACIA_WOOD_WALL =
            ITEMS.register("stripped_acacia_wood_wall", () ->
                    new BlockItem(
                    ModBlocks.STRIPPED_ACACIA_WOOD_WALL.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> STRIPPED_BAMBOO_BLOCK_WALL =
            ITEMS.register("stripped_bamboo_block_wall", () ->
                            new BlockItem(
            ModBlocks.STRIPPED_BAMBOO_BLOCK_WALL.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> STRIPPED_MANGROVE_WOOD_WALL =
            ITEMS.register("stripped_mangrove_wood_wall", () ->
                            new BlockItem(
            ModBlocks.STRIPPED_MANGROVE_WOOD_WALL.get(),
                                    createBlockItemProperties()
                            )
            );

    public static final RegistryObject<Item> CHERRY_WOOD_WALL =
            ITEMS.register("cherry_wood_wall", () ->
                            new BlockItem(
            ModBlocks.CHERRY_WOOD_WALL.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> STRIPPED_CHERRY_WOOD_WALL =
            ITEMS.register("stripped_cherry_wood_wall", () ->
                            new BlockItem(
            ModBlocks.STRIPPED_CHERRY_WOOD_WALL.get(),
                                    createBlockItemProperties()
                            )
            );

    public static final RegistryObject<Item> PALE_OAK_WOOD_WALL =
            ITEMS.register("pale_oak_wood_wall", () ->
                            new BlockItem(
            ModBlocks.PALE_OAK_WOOD_WALL.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_WOOD_WALL =
            ITEMS.register("stripped_pale_oak_wood_wall", () ->
                            new BlockItem(
            ModBlocks.STRIPPED_PALE_OAK_WOOD_WALL.get(),
                                    createBlockItemProperties()
                            )
            );

    public static final RegistryObject<Item> ASHPEN_WOOD_WALL =
            ITEMS.register("ashpen_wood_wall", () ->
                            new BlockItem(
            ModBlocks.ASHPEN_WOOD_WALL.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> STRIPPED_ASHPEN_WOOD_WALL =
            ITEMS.register("stripped_ashpen_wood_wall", () ->
                            new BlockItem(
            ModBlocks.STRIPPED_ASHPEN_WOOD_WALL.get(),
                                    createBlockItemProperties()
                            )
            );

    public static final RegistryObject<Item> ASHPEN_WHITE_PLANKS = ITEMS.register(
            "ashpen_white_planks",
            () ->
                    new BlockItem(
                    ModBlocks.ASHPEN_WHITE_PLANKS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> ASHPEN_WHITE_STAIRS = ITEMS.register(
            "ashpen_white_stairs",
            () ->
                    new BlockItem(
                    ModBlocks.ASHPEN_WHITE_STAIRS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> ASHPEN_WHITE_SLAB = ITEMS.register(
            "ashpen_white_slab",
            () ->
                    new BlockItem(
                    ModBlocks.ASHPEN_WHITE_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> ASHPEN_WHITE_FENCE = ITEMS.register(
            "ashpen_white_fence",
            () ->
                    new BlockItem(
                    ModBlocks.ASHPEN_WHITE_FENCE.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> ASHPEN_WHITE_FENCE_GATE =
            ITEMS.register("ashpen_white_fence_gate", () ->
                    new BlockItem(
                    ModBlocks.ASHPEN_WHITE_FENCE_GATE.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> ASHPEN_WHITE_PRESSURE_PLATE =
            ITEMS.register("ashpen_white_pressure_plate", () ->
                            new BlockItem(
            ModBlocks.ASHPEN_WHITE_PRESSURE_PLATE.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> ASHPEN_WHITE_BUTTON = ITEMS.register(
            "ashpen_white_button",
            () ->
                    new BlockItem(
                    ModBlocks.ASHPEN_WHITE_BUTTON.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BLACK_ASHPEN_PLANKS = ITEMS.register(
            "ashpen_black_planks",
            () ->
                    new BlockItem(
                    ModBlocks.BLACK_ASHPEN_PLANKS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BLACK_ASHPEN_STAIRS = ITEMS.register(
            "ashpen_black_stairs",
            () ->
                    new BlockItem(
                    ModBlocks.BLACK_ASHPEN_STAIRS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BLACK_ASHPEN_SLAB = ITEMS.register(
            "ashpen_black_slab",
            () ->
                    new BlockItem(
                    ModBlocks.BLACK_ASHPEN_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BLACK_ASHPEN_FENCE = ITEMS.register(
            "ashpen_black_fence",
            () ->
                    new BlockItem(
                    ModBlocks.BLACK_ASHPEN_FENCE.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BLACK_ASHPEN_FENCE_GATE =
            ITEMS.register("ashpen_black_fence_gate", () ->
                    new BlockItem(
                    ModBlocks.BLACK_ASHPEN_FENCE_GATE.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> BLACK_ASHPEN_PRESSURE_PLATE =
            ITEMS.register("ashpen_black_pressure_plate", () ->
                            new BlockItem(
            ModBlocks.BLACK_ASHPEN_PRESSURE_PLATE.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> BLACK_ASHPEN_BUTTON = ITEMS.register(
            "ashpen_black_button",
            () ->
                    new BlockItem(
                    ModBlocks.BLACK_ASHPEN_BUTTON.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BLUE_ASHPEN_PLANKS = ITEMS.register(
            "ashpen_blue_planks",
            () ->
                    new BlockItem(
                    ModBlocks.BLUE_ASHPEN_PLANKS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BLUE_ASHPEN_STAIRS = ITEMS.register(
            "ashpen_blue_stairs",
            () ->
                    new BlockItem(
                    ModBlocks.BLUE_ASHPEN_STAIRS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BLUE_ASHPEN_SLAB = ITEMS.register(
            "ashpen_blue_slab",
            () ->
                    new BlockItem(
                    ModBlocks.BLUE_ASHPEN_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BLUE_ASHPEN_FENCE = ITEMS.register(
            "ashpen_blue_fence",
            () ->
                    new BlockItem(
                    ModBlocks.BLUE_ASHPEN_FENCE.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BLUE_ASHPEN_FENCE_GATE =
            ITEMS.register("ashpen_blue_fence_gate", () ->
                    new BlockItem(
                    ModBlocks.BLUE_ASHPEN_FENCE_GATE.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> BLUE_ASHPEN_PRESSURE_PLATE =
            ITEMS.register("ashpen_blue_pressure_plate", () ->
                            new BlockItem(
            ModBlocks.BLUE_ASHPEN_PRESSURE_PLATE.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> BLUE_ASHPEN_BUTTON = ITEMS.register(
            "ashpen_blue_button",
            () ->
                    new BlockItem(
                    ModBlocks.BLUE_ASHPEN_BUTTON.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BROWN_ASHPEN_PLANKS = ITEMS.register(
            "ashpen_brown_planks",
            () ->
                    new BlockItem(
                    ModBlocks.BROWN_ASHPEN_PLANKS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BROWN_ASHPEN_STAIRS = ITEMS.register(
            "ashpen_brown_stairs",
            () ->
                    new BlockItem(
                    ModBlocks.BROWN_ASHPEN_STAIRS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BROWN_ASHPEN_SLAB = ITEMS.register(
            "ashpen_brown_slab",
            () ->
                    new BlockItem(
                    ModBlocks.BROWN_ASHPEN_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BROWN_ASHPEN_FENCE = ITEMS.register(
            "ashpen_brown_fence",
            () ->
                    new BlockItem(
                    ModBlocks.BROWN_ASHPEN_FENCE.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> BROWN_ASHPEN_FENCE_GATE =
            ITEMS.register("ashpen_brown_fence_gate", () ->
                    new BlockItem(
                    ModBlocks.BROWN_ASHPEN_FENCE_GATE.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> BROWN_ASHPEN_PRESSURE_PLATE =
            ITEMS.register("ashpen_brown_pressure_plate", () ->
                            new BlockItem(
            ModBlocks.BROWN_ASHPEN_PRESSURE_PLATE.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> BROWN_ASHPEN_BUTTON = ITEMS.register(
            "ashpen_brown_button",
            () ->
                    new BlockItem(
                    ModBlocks.BROWN_ASHPEN_BUTTON.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> CYAN_ASHPEN_PLANKS = ITEMS.register(
            "ashpen_cyan_planks",
            () ->
                    new BlockItem(
                    ModBlocks.CYAN_ASHPEN_PLANKS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> CYAN_ASHPEN_STAIRS = ITEMS.register(
            "ashpen_cyan_stairs",
            () ->
                    new BlockItem(
                    ModBlocks.CYAN_ASHPEN_STAIRS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> CYAN_ASHPEN_SLAB = ITEMS.register(
            "ashpen_cyan_slab",
            () ->
                    new BlockItem(
                    ModBlocks.CYAN_ASHPEN_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> CYAN_ASHPEN_FENCE = ITEMS.register(
            "ashpen_cyan_fence",
            () ->
                    new BlockItem(
                    ModBlocks.CYAN_ASHPEN_FENCE.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> CYAN_ASHPEN_FENCE_GATE =
            ITEMS.register("ashpen_cyan_fence_gate", () ->
                    new BlockItem(
                    ModBlocks.CYAN_ASHPEN_FENCE_GATE.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> CYAN_ASHPEN_PRESSURE_PLATE =
            ITEMS.register("ashpen_cyan_pressure_plate", () ->
                            new BlockItem(
            ModBlocks.CYAN_ASHPEN_PRESSURE_PLATE.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> CYAN_ASHPEN_BUTTON = ITEMS.register(
            "ashpen_cyan_button",
            () ->
                    new BlockItem(
                    ModBlocks.CYAN_ASHPEN_BUTTON.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> GRAY_ASHPEN_PLANKS = ITEMS.register(
            "ashpen_gray_planks",
            () ->
                    new BlockItem(
                    ModBlocks.GRAY_ASHPEN_PLANKS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> GRAY_ASHPEN_STAIRS = ITEMS.register(
            "ashpen_gray_stairs",
            () ->
                    new BlockItem(
                    ModBlocks.GRAY_ASHPEN_STAIRS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> GRAY_ASHPEN_SLAB = ITEMS.register(
            "ashpen_gray_slab",
            () ->
                    new BlockItem(
                    ModBlocks.GRAY_ASHPEN_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> GRAY_ASHPEN_FENCE = ITEMS.register(
            "ashpen_gray_fence",
            () ->
                    new BlockItem(
                    ModBlocks.GRAY_ASHPEN_FENCE.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> GRAY_ASHPEN_FENCE_GATE =
            ITEMS.register("ashpen_gray_fence_gate", () ->
                    new BlockItem(
                    ModBlocks.GRAY_ASHPEN_FENCE_GATE.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> GRAY_ASHPEN_PRESSURE_PLATE =
            ITEMS.register("ashpen_gray_pressure_plate", () ->
                            new BlockItem(
            ModBlocks.GRAY_ASHPEN_PRESSURE_PLATE.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> GRAY_ASHPEN_BUTTON = ITEMS.register(
            "ashpen_gray_button",
            () ->
                    new BlockItem(
                    ModBlocks.GRAY_ASHPEN_BUTTON.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> GREEN_ASHPEN_PLANKS = ITEMS.register(
            "ashpen_green_planks",
            () ->
                    new BlockItem(
                    ModBlocks.GREEN_ASHPEN_PLANKS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> GREEN_ASHPEN_STAIRS = ITEMS.register(
            "ashpen_green_stairs",
            () ->
                    new BlockItem(
                    ModBlocks.GREEN_ASHPEN_STAIRS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> GREEN_ASHPEN_SLAB = ITEMS.register(
            "ashpen_green_slab",
            () ->
                    new BlockItem(
                    ModBlocks.GREEN_ASHPEN_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> GREEN_ASHPEN_FENCE = ITEMS.register(
            "ashpen_green_fence",
            () ->
                    new BlockItem(
                    ModBlocks.GREEN_ASHPEN_FENCE.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> GREEN_ASHPEN_FENCE_GATE =
            ITEMS.register("ashpen_green_fence_gate", () ->
                    new BlockItem(
                    ModBlocks.GREEN_ASHPEN_FENCE_GATE.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> GREEN_ASHPEN_PRESSURE_PLATE =
            ITEMS.register("ashpen_green_pressure_plate", () ->
                            new BlockItem(
            ModBlocks.GREEN_ASHPEN_PRESSURE_PLATE.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> GREEN_ASHPEN_BUTTON = ITEMS.register(
            "ashpen_green_button",
            () ->
                    new BlockItem(
                    ModBlocks.GREEN_ASHPEN_BUTTON.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> LIGHT_BLUE_ASHPEN_PLANKS =
            ITEMS.register("ashpen_light_blue_planks", () ->
                    new BlockItem(
                    ModBlocks.LIGHT_BLUE_ASHPEN_PLANKS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> LIGHT_BLUE_ASHPEN_STAIRS =
            ITEMS.register("ashpen_light_blue_stairs", () ->
                    new BlockItem(
                    ModBlocks.LIGHT_BLUE_ASHPEN_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> LIGHT_BLUE_ASHPEN_SLAB =
            ITEMS.register("ashpen_light_blue_slab", () ->
                    new BlockItem(
                    ModBlocks.LIGHT_BLUE_ASHPEN_SLAB.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> LIGHT_BLUE_ASHPEN_FENCE =
            ITEMS.register("ashpen_light_blue_fence", () ->
                    new BlockItem(
                    ModBlocks.LIGHT_BLUE_ASHPEN_FENCE.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> LIGHT_BLUE_ASHPEN_FENCE_GATE =
            ITEMS.register("ashpen_light_blue_fence_gate", () ->
                            new BlockItem(
            ModBlocks.LIGHT_BLUE_ASHPEN_FENCE_GATE.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> LIGHT_BLUE_ASHPEN_PRESSURE_PLATE =
            ITEMS.register("ashpen_light_blue_pressure_plate", () ->
                            new BlockItem(
            ModBlocks.LIGHT_BLUE_ASHPEN_PRESSURE_PLATE.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> LIGHT_BLUE_ASHPEN_BUTTON =
            ITEMS.register("ashpen_light_blue_button", () ->
                    new BlockItem(
                    ModBlocks.LIGHT_BLUE_ASHPEN_BUTTON.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> LIGHT_GRAY_ASHPEN_PLANKS =
            ITEMS.register("ashpen_light_gray_planks", () ->
                    new BlockItem(
                    ModBlocks.LIGHT_GRAY_ASHPEN_PLANKS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> LIGHT_GRAY_ASHPEN_STAIRS =
            ITEMS.register("ashpen_light_gray_stairs", () ->
                    new BlockItem(
                    ModBlocks.LIGHT_GRAY_ASHPEN_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> LIGHT_GRAY_ASHPEN_SLAB =
            ITEMS.register("ashpen_light_gray_slab", () ->
                    new BlockItem(
                    ModBlocks.LIGHT_GRAY_ASHPEN_SLAB.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> LIGHT_GRAY_ASHPEN_FENCE =
            ITEMS.register("ashpen_light_gray_fence", () ->
                    new BlockItem(
                    ModBlocks.LIGHT_GRAY_ASHPEN_FENCE.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> LIGHT_GRAY_ASHPEN_FENCE_GATE =
            ITEMS.register("ashpen_light_gray_fence_gate", () ->
                            new BlockItem(
            ModBlocks.LIGHT_GRAY_ASHPEN_FENCE_GATE.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> LIGHT_GRAY_ASHPEN_PRESSURE_PLATE =
            ITEMS.register("ashpen_light_gray_pressure_plate", () ->
                            new BlockItem(
            ModBlocks.LIGHT_GRAY_ASHPEN_PRESSURE_PLATE.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> LIGHT_GRAY_ASHPEN_BUTTON =
            ITEMS.register("ashpen_light_gray_button", () ->
                    new BlockItem(
                    ModBlocks.LIGHT_GRAY_ASHPEN_BUTTON.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> LIME_ASHPEN_PLANKS = ITEMS.register(
            "ashpen_lime_planks",
            () ->
                    new BlockItem(
                    ModBlocks.LIME_ASHPEN_PLANKS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> LIME_ASHPEN_STAIRS = ITEMS.register(
            "ashpen_lime_stairs",
            () ->
                    new BlockItem(
                    ModBlocks.LIME_ASHPEN_STAIRS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> LIME_ASHPEN_SLAB = ITEMS.register(
            "ashpen_lime_slab",
            () ->
                    new BlockItem(
                    ModBlocks.LIME_ASHPEN_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> LIME_ASHPEN_FENCE = ITEMS.register(
            "ashpen_lime_fence",
            () ->
                    new BlockItem(
                    ModBlocks.LIME_ASHPEN_FENCE.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> LIME_ASHPEN_FENCE_GATE =
            ITEMS.register("ashpen_lime_fence_gate", () ->
                    new BlockItem(
                    ModBlocks.LIME_ASHPEN_FENCE_GATE.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> LIME_ASHPEN_PRESSURE_PLATE =
            ITEMS.register("ashpen_lime_pressure_plate", () ->
                            new BlockItem(
            ModBlocks.LIME_ASHPEN_PRESSURE_PLATE.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> LIME_ASHPEN_BUTTON = ITEMS.register(
            "ashpen_lime_button",
            () ->
                    new BlockItem(
                    ModBlocks.LIME_ASHPEN_BUTTON.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> MAGENTA_ASHPEN_PLANKS =
            ITEMS.register("ashpen_magenta_planks", () ->
                    new BlockItem(
                    ModBlocks.MAGENTA_ASHPEN_PLANKS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> MAGENTA_ASHPEN_STAIRS =
            ITEMS.register("ashpen_magenta_stairs", () ->
                    new BlockItem(
                    ModBlocks.MAGENTA_ASHPEN_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> MAGENTA_ASHPEN_SLAB = ITEMS.register(
            "ashpen_magenta_slab",
            () ->
                    new BlockItem(
                    ModBlocks.MAGENTA_ASHPEN_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> MAGENTA_ASHPEN_FENCE =
            ITEMS.register("ashpen_magenta_fence", () ->
                    new BlockItem(
                    ModBlocks.MAGENTA_ASHPEN_FENCE.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> MAGENTA_ASHPEN_FENCE_GATE =
            ITEMS.register("ashpen_magenta_fence_gate", () ->
                    new BlockItem(
                    ModBlocks.MAGENTA_ASHPEN_FENCE_GATE.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> MAGENTA_ASHPEN_PRESSURE_PLATE =
            ITEMS.register("ashpen_magenta_pressure_plate", () ->
                            new BlockItem(
            ModBlocks.MAGENTA_ASHPEN_PRESSURE_PLATE.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> MAGENTA_ASHPEN_BUTTON =
            ITEMS.register("ashpen_magenta_button", () ->
                    new BlockItem(
                    ModBlocks.MAGENTA_ASHPEN_BUTTON.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> ORANGE_ASHPEN_PLANKS =
            ITEMS.register("ashpen_orange_planks", () ->
                    new BlockItem(
                    ModBlocks.ORANGE_ASHPEN_PLANKS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> ORANGE_ASHPEN_STAIRS =
            ITEMS.register("ashpen_orange_stairs", () ->
                    new BlockItem(
                    ModBlocks.ORANGE_ASHPEN_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> ORANGE_ASHPEN_SLAB = ITEMS.register(
            "ashpen_orange_slab",
            () ->
                    new BlockItem(
                    ModBlocks.ORANGE_ASHPEN_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> ORANGE_ASHPEN_FENCE = ITEMS.register(
            "ashpen_orange_fence",
            () ->
                    new BlockItem(
                    ModBlocks.ORANGE_ASHPEN_FENCE.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> ORANGE_ASHPEN_FENCE_GATE =
            ITEMS.register("ashpen_orange_fence_gate", () ->
                    new BlockItem(
                    ModBlocks.ORANGE_ASHPEN_FENCE_GATE.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> ORANGE_ASHPEN_PRESSURE_PLATE =
            ITEMS.register("ashpen_orange_pressure_plate", () ->
                            new BlockItem(
            ModBlocks.ORANGE_ASHPEN_PRESSURE_PLATE.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> ORANGE_ASHPEN_BUTTON =
            ITEMS.register("ashpen_orange_button", () ->
                    new BlockItem(
                    ModBlocks.ORANGE_ASHPEN_BUTTON.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> PINK_ASHPEN_PLANKS = ITEMS.register(
            "ashpen_pink_planks",
            () ->
                    new BlockItem(
                    ModBlocks.PINK_ASHPEN_PLANKS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> PINK_ASHPEN_STAIRS = ITEMS.register(
            "ashpen_pink_stairs",
            () ->
                    new BlockItem(
                    ModBlocks.PINK_ASHPEN_STAIRS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> PINK_ASHPEN_SLAB = ITEMS.register(
            "ashpen_pink_slab",
            () ->
                    new BlockItem(
                    ModBlocks.PINK_ASHPEN_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> PINK_ASHPEN_FENCE = ITEMS.register(
            "ashpen_pink_fence",
            () ->
                    new BlockItem(
                    ModBlocks.PINK_ASHPEN_FENCE.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> PINK_ASHPEN_FENCE_GATE =
            ITEMS.register("ashpen_pink_fence_gate", () ->
                    new BlockItem(
                    ModBlocks.PINK_ASHPEN_FENCE_GATE.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> PINK_ASHPEN_PRESSURE_PLATE =
            ITEMS.register("ashpen_pink_pressure_plate", () ->
                            new BlockItem(
            ModBlocks.PINK_ASHPEN_PRESSURE_PLATE.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> PINK_ASHPEN_BUTTON = ITEMS.register(
            "ashpen_pink_button",
            () ->
                    new BlockItem(
                    ModBlocks.PINK_ASHPEN_BUTTON.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> PURPLE_ASHPEN_PLANKS =
            ITEMS.register("ashpen_purple_planks", () ->
                    new BlockItem(
                    ModBlocks.PURPLE_ASHPEN_PLANKS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> PURPLE_ASHPEN_STAIRS =
            ITEMS.register("ashpen_purple_stairs", () ->
                    new BlockItem(
                    ModBlocks.PURPLE_ASHPEN_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> PURPLE_ASHPEN_SLAB = ITEMS.register(
            "ashpen_purple_slab",
            () ->
                    new BlockItem(
                    ModBlocks.PURPLE_ASHPEN_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> PURPLE_ASHPEN_FENCE = ITEMS.register(
            "ashpen_purple_fence",
            () ->
                    new BlockItem(
                    ModBlocks.PURPLE_ASHPEN_FENCE.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> PURPLE_ASHPEN_FENCE_GATE =
            ITEMS.register("ashpen_purple_fence_gate", () ->
                    new BlockItem(
                    ModBlocks.PURPLE_ASHPEN_FENCE_GATE.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> PURPLE_ASHPEN_PRESSURE_PLATE =
            ITEMS.register("ashpen_purple_pressure_plate", () ->
                            new BlockItem(
            ModBlocks.PURPLE_ASHPEN_PRESSURE_PLATE.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> PURPLE_ASHPEN_BUTTON =
            ITEMS.register("ashpen_purple_button", () ->
                    new BlockItem(
                    ModBlocks.PURPLE_ASHPEN_BUTTON.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> RED_ASHPEN_PLANKS = ITEMS.register(
            "ashpen_red_planks",
            () ->
                    new BlockItem(
                    ModBlocks.RED_ASHPEN_PLANKS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> RED_ASHPEN_STAIRS = ITEMS.register(
            "ashpen_red_stairs",
            () ->
                    new BlockItem(
                    ModBlocks.RED_ASHPEN_STAIRS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> RED_ASHPEN_SLAB = ITEMS.register(
            "ashpen_red_slab",
            () ->
                    new BlockItem(
                    ModBlocks.RED_ASHPEN_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> RED_ASHPEN_FENCE = ITEMS.register(
            "ashpen_red_fence",
            () ->
                    new BlockItem(
                    ModBlocks.RED_ASHPEN_FENCE.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> RED_ASHPEN_FENCE_GATE =
            ITEMS.register("ashpen_red_fence_gate", () ->
                    new BlockItem(
                    ModBlocks.RED_ASHPEN_FENCE_GATE.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> RED_ASHPEN_PRESSURE_PLATE =
            ITEMS.register("ashpen_red_pressure_plate", () ->
                    new BlockItem(
                    ModBlocks.RED_ASHPEN_PRESSURE_PLATE.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> RED_ASHPEN_BUTTON = ITEMS.register(
            "ashpen_red_button",
            () ->
                    new BlockItem(
                    ModBlocks.RED_ASHPEN_BUTTON.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> YELLOW_ASHPEN_PLANKS =
            ITEMS.register("ashpen_yellow_planks", () ->
                    new BlockItem(
                    ModBlocks.YELLOW_ASHPEN_PLANKS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> YELLOW_ASHPEN_STAIRS =
            ITEMS.register("ashpen_yellow_stairs", () ->
                    new BlockItem(
                    ModBlocks.YELLOW_ASHPEN_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> YELLOW_ASHPEN_SLAB = ITEMS.register(
            "ashpen_yellow_slab",
            () ->
                    new BlockItem(
                    ModBlocks.YELLOW_ASHPEN_SLAB.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> YELLOW_ASHPEN_FENCE = ITEMS.register(
            "ashpen_yellow_fence",
            () ->
                    new BlockItem(
                    ModBlocks.YELLOW_ASHPEN_FENCE.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> YELLOW_ASHPEN_FENCE_GATE =
            ITEMS.register("ashpen_yellow_fence_gate", () ->
                    new BlockItem(
                    ModBlocks.YELLOW_ASHPEN_FENCE_GATE.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> YELLOW_ASHPEN_PRESSURE_PLATE =
            ITEMS.register("ashpen_yellow_pressure_plate", () ->
                            new BlockItem(
            ModBlocks.YELLOW_ASHPEN_PRESSURE_PLATE.get(),
                                    createBlockItemProperties()
                            )
            );
    public static final RegistryObject<Item> YELLOW_ASHPEN_BUTTON =
            ITEMS.register("ashpen_yellow_button", () ->
                    new BlockItem(
                    ModBlocks.YELLOW_ASHPEN_BUTTON.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> RED_ROSE_VINES = ITEMS.register(
            "red_rose_vines",
            () ->
                    new BlockItem(ModBlocks.RED_ROSE_VINES.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BLACK_ROSE_VINES = ITEMS.register(
            "black_rose_vines",
            () ->
                    new BlockItem(
                    ModBlocks.BLACK_ROSE_VINES.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BLUE_ROSE_VINES = ITEMS.register(
            "blue_rose_vines",
            () ->
                    new BlockItem(
                    ModBlocks.BLUE_ROSE_VINES.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> WHITE_ROSE_VINES = ITEMS.register(
            "white_rose_vines",
            () ->
                    new BlockItem(
                    ModBlocks.WHITE_ROSE_VINES.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> RED_MONETS = ITEMS.register(
            "red_monets",
            () -> new BlockItem(ModBlocks.RED_MONETS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BLUE_MONETS = ITEMS.register(
            "blue_monets",
            () ->
                    new BlockItem(ModBlocks.BLUE_MONETS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> PURPLE_MONETS = ITEMS.register(
            "purple_monets",
            () ->
                    new BlockItem(ModBlocks.PURPLE_MONETS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> LIGHT_BLUE_MONETS = ITEMS.register(
            "light_blue_monets",
            () ->
                    new BlockItem(
                    ModBlocks.LIGHT_BLUE_MONETS.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> PINK_MONETS = ITEMS.register(
            "pink_monets",
            () ->
                    new BlockItem(ModBlocks.PINK_MONETS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> YELLOW_MONETS = ITEMS.register(
            "yellow_monets",
            () ->
                    new BlockItem(ModBlocks.YELLOW_MONETS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CLOVER = ITEMS.register(
            "clover",
            () -> new BlockItem(ModBlocks.CLOVER.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> RED_PETAL = ITEMS.register(
            "red_petal",
            () -> new BlockItem(ModBlocks.RED_PETAL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BLUE_PETAL = ITEMS.register(
            "blue_petal",
            () -> new BlockItem(ModBlocks.BLUE_PETAL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> ORANGE_PETAL = ITEMS.register(
            "orange_petal",
            () ->
                    new BlockItem(ModBlocks.ORANGE_PETAL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> PINK_PETAL = ITEMS.register(
            "pink_petal",
            () -> new BlockItem(ModBlocks.PINK_PETAL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> PURPLE_PETAL = ITEMS.register(
            "purple_petal",
            () ->
                    new BlockItem(ModBlocks.PURPLE_PETAL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> RED_SPORE_BLOSSOM = ITEMS.register(
            "red_spore_blossom",
            () ->
                    new BlockItem(
                    ModBlocks.RED_SPORE_BLOSSOM.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> CYAN_SPORE_BLOSSOM = ITEMS.register(
            "cyan_spore_blossom",
            () ->
                    new BlockItem(
                    ModBlocks.CYAN_SPORE_BLOSSOM.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BLUE_SPORE_BLOSSOM = ITEMS.register(
            "blue_spore_blossom",
            () ->
                    new BlockItem(
                    ModBlocks.BLUE_SPORE_BLOSSOM.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> PURPLE_SPORE_BLOSSOM =
            ITEMS.register("purple_spore_blossom", () ->
                    new BlockItem(
                    ModBlocks.PURPLE_SPORE_BLOSSOM.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> ORANGE_SPORE_BLOSSOM =
            ITEMS.register("orange_spore_blossom", () ->
                    new BlockItem(
                    ModBlocks.ORANGE_SPORE_BLOSSOM.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BIG_CANDLE = ITEMS.register(
            "big_candle",
            () -> new BlockItem(ModBlocks.BIG_CANDLE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BIG_WHITE_CANDLE = ITEMS.register(
            "big_white_candle",
            () ->
                    new BlockItem(
                    ModBlocks.BIG_WHITE_CANDLE.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BIG_ORANGE_CANDLE = ITEMS.register(
            "big_orange_candle",
            () ->
                    new BlockItem(
                    ModBlocks.BIG_ORANGE_CANDLE.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BIG_MAGENTA_CANDLE = ITEMS.register(
            "big_magenta_candle",
            () ->
                    new BlockItem(
                    ModBlocks.BIG_MAGENTA_CANDLE.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BIG_LIGHT_BLUE_CANDLE =
            ITEMS.register("big_light_blue_candle", () ->
                    new BlockItem(
                    ModBlocks.BIG_LIGHT_BLUE_CANDLE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BIG_YELLOW_CANDLE = ITEMS.register(
            "big_yellow_candle",
            () ->
                    new BlockItem(
                    ModBlocks.BIG_YELLOW_CANDLE.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BIG_LIME_CANDLE = ITEMS.register(
            "big_lime_candle",
            () ->
                    new BlockItem(
                    ModBlocks.BIG_LIME_CANDLE.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BIG_PINK_CANDLE = ITEMS.register(
            "big_pink_candle",
            () ->
                    new BlockItem(
                    ModBlocks.BIG_PINK_CANDLE.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BIG_GRAY_CANDLE = ITEMS.register(
            "big_gray_candle",
            () ->
                    new BlockItem(
                    ModBlocks.BIG_GRAY_CANDLE.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BIG_LIGHT_GRAY_CANDLE =
            ITEMS.register("big_light_gray_candle", () ->
                    new BlockItem(
                    ModBlocks.BIG_LIGHT_GRAY_CANDLE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> BIG_CYAN_CANDLE = ITEMS.register(
            "big_cyan_candle",
            () ->
                    new BlockItem(
                    ModBlocks.BIG_CYAN_CANDLE.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BIG_PURPLE_CANDLE = ITEMS.register(
            "big_purple_candle",
            () ->
                    new BlockItem(
                    ModBlocks.BIG_PURPLE_CANDLE.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BIG_BLUE_CANDLE = ITEMS.register(
            "big_blue_candle",
            () ->
                    new BlockItem(
                    ModBlocks.BIG_BLUE_CANDLE.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BIG_BROWN_CANDLE = ITEMS.register(
            "big_brown_candle",
            () ->
                    new BlockItem(
                    ModBlocks.BIG_BROWN_CANDLE.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BIG_GREEN_CANDLE = ITEMS.register(
            "big_green_candle",
            () ->
                    new BlockItem(
                    ModBlocks.BIG_GREEN_CANDLE.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BIG_RED_CANDLE = ITEMS.register(
            "big_red_candle",
            () ->
                    new BlockItem(ModBlocks.BIG_RED_CANDLE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BIG_BLACK_CANDLE = ITEMS.register(
            "big_black_candle",
            () ->
                    new BlockItem(
                    ModBlocks.BIG_BLACK_CANDLE.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BIG_AMETHYST_CANDLE = ITEMS.register(
            "big_amethyst_candle",
            () ->
                    new BlockItem(
                    ModBlocks.BIG_AMETHYST_CANDLE.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BIG_SCULK_CANDLE = ITEMS.register(
            "big_sculk_candle",
            () ->
                    new BlockItem(
                    ModBlocks.BIG_SCULK_CANDLE.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> WHITE_ORNAMENT = ITEMS.register(
            "white_ornament",
            () ->
                    new BlockItem(ModBlocks.WHITE_ORNAMENT.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> ORANGE_ORNAMENT = ITEMS.register(
            "orange_ornament",
            () ->
                    new BlockItem(
                    ModBlocks.ORANGE_ORNAMENT.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> MAGENTA_ORNAMENT = ITEMS.register(
            "magenta_ornament",
            () ->
                    new BlockItem(
                    ModBlocks.MAGENTA_ORNAMENT.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> LIGHT_BLUE_ORNAMENT = ITEMS.register(
            "light_blue_ornament",
            () ->
                    new BlockItem(
                    ModBlocks.LIGHT_BLUE_ORNAMENT.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> YELLOW_ORNAMENT = ITEMS.register(
            "yellow_ornament",
            () ->
                    new BlockItem(
                    ModBlocks.YELLOW_ORNAMENT.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> LIME_ORNAMENT = ITEMS.register(
            "lime_ornament",
            () ->
                    new BlockItem(ModBlocks.LIME_ORNAMENT.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> PINK_ORNAMENT = ITEMS.register(
            "pink_ornament",
            () ->
                    new BlockItem(ModBlocks.PINK_ORNAMENT.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> GRAY_ORNAMENT = ITEMS.register(
            "gray_ornament",
            () ->
                    new BlockItem(ModBlocks.GRAY_ORNAMENT.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> LIGHT_GRAY_ORNAMENT = ITEMS.register(
            "light_gray_ornament",
            () ->
                    new BlockItem(
                    ModBlocks.LIGHT_GRAY_ORNAMENT.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> CYAN_ORNAMENT = ITEMS.register(
            "cyan_ornament",
            () ->
                    new BlockItem(ModBlocks.CYAN_ORNAMENT.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> PURPLE_ORNAMENT = ITEMS.register(
            "purple_ornament",
            () ->
                    new BlockItem(
                    ModBlocks.PURPLE_ORNAMENT.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BLUE_ORNAMENT = ITEMS.register(
            "blue_ornament",
            () ->
                    new BlockItem(ModBlocks.BLUE_ORNAMENT.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BROWN_ORNAMENT = ITEMS.register(
            "brown_ornament",
            () ->
                    new BlockItem(ModBlocks.BROWN_ORNAMENT.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> GREEN_ORNAMENT = ITEMS.register(
            "green_ornament",
            () ->
                    new BlockItem(ModBlocks.GREEN_ORNAMENT.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> RED_ORNAMENT = ITEMS.register(
            "red_ornament",
            () ->
                    new BlockItem(ModBlocks.RED_ORNAMENT.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BLACK_ORNAMENT = ITEMS.register(
            "black_ornament",
            () ->
                    new BlockItem(ModBlocks.BLACK_ORNAMENT.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> GLASS_ORNAMENT = ITEMS.register(
            "glass_ornament",
            () ->
                    new BlockItem(ModBlocks.GLASS_ORNAMENT.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> TINTED_GLASS_ORNAMENT =
            ITEMS.register("tinted_glass_ornament", () ->
                    new BlockItem(
                    ModBlocks.TINTED_GLASS_ORNAMENT.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> WHITE_STRING_LIGHT = ITEMS.register(
            "white_string_light",
            () ->
                    new BlockItem(
                    ModBlocks.WHITE_STRING_LIGHT.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> ORANGE_STRING_LIGHT = ITEMS.register(
            "orange_string_light",
            () ->
                    new BlockItem(
                    ModBlocks.ORANGE_STRING_LIGHT.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> MAGENTA_STRING_LIGHT =
            ITEMS.register("magenta_string_light", () ->
                    new BlockItem(
                    ModBlocks.MAGENTA_STRING_LIGHT.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> LIGHT_BLUE_STRING_LIGHT =
            ITEMS.register("light_blue_string_light", () ->
                    new BlockItem(
                    ModBlocks.LIGHT_BLUE_STRING_LIGHT.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> YELLOW_STRING_LIGHT = ITEMS.register(
            "yellow_string_light",
            () ->
                    new BlockItem(
                    ModBlocks.YELLOW_STRING_LIGHT.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> LIME_STRING_LIGHT = ITEMS.register(
            "lime_string_light",
            () ->
                    new BlockItem(
                    ModBlocks.LIME_STRING_LIGHT.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> PINK_STRING_LIGHT = ITEMS.register(
            "pink_string_light",
            () ->
                    new BlockItem(
                    ModBlocks.PINK_STRING_LIGHT.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> GRAY_STRING_LIGHT = ITEMS.register(
            "gray_string_light",
            () ->
                    new BlockItem(
                    ModBlocks.GRAY_STRING_LIGHT.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> LIGHT_GRAY_STRING_LIGHT =
            ITEMS.register("light_gray_string_light", () ->
                    new BlockItem(
                    ModBlocks.LIGHT_GRAY_STRING_LIGHT.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> CYAN_STRING_LIGHT = ITEMS.register(
            "cyan_string_light",
            () ->
                    new BlockItem(
                    ModBlocks.CYAN_STRING_LIGHT.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> PURPLE_STRING_LIGHT = ITEMS.register(
            "purple_string_light",
            () ->
                    new BlockItem(
                    ModBlocks.PURPLE_STRING_LIGHT.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BLUE_STRING_LIGHT = ITEMS.register(
            "blue_string_light",
            () ->
                    new BlockItem(
                    ModBlocks.BLUE_STRING_LIGHT.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BROWN_STRING_LIGHT = ITEMS.register(
            "brown_string_light",
            () ->
                    new BlockItem(
                    ModBlocks.BROWN_STRING_LIGHT.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> GREEN_STRING_LIGHT = ITEMS.register(
            "green_string_light",
            () ->
                    new BlockItem(
                    ModBlocks.GREEN_STRING_LIGHT.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> RED_STRING_LIGHT = ITEMS.register(
            "red_string_light",
            () ->
                    new BlockItem(
                    ModBlocks.RED_STRING_LIGHT.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> BLACK_STRING_LIGHT = ITEMS.register(
            "black_string_light",
            () ->
                    new BlockItem(
                    ModBlocks.BLACK_STRING_LIGHT.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> MULTICOLOR_STRING_LIGHT =
            ITEMS.register("multicolor_string_light", () ->
                    new BlockItem(
                    ModBlocks.MULTICOLOR_STRING_LIGHT.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> WHITE_STAR = ITEMS.register(
            "white_star",
            () -> new BlockItem(ModBlocks.WHITE_STAR.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> ORANGE_STAR = ITEMS.register(
            "orange_star",
            () ->
                    new BlockItem(ModBlocks.ORANGE_STAR.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> MAGENTA_STAR = ITEMS.register(
            "magenta_star",
            () ->
                    new BlockItem(ModBlocks.MAGENTA_STAR.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> LIGHT_BLUE_STAR = ITEMS.register(
            "light_blue_star",
            () ->
                    new BlockItem(
                    ModBlocks.LIGHT_BLUE_STAR.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> YELLOW_STAR = ITEMS.register(
            "yellow_star",
            () ->
                    new BlockItem(ModBlocks.YELLOW_STAR.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> LIME_STAR = ITEMS.register(
            "lime_star",
            () -> new BlockItem(ModBlocks.LIME_STAR.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> PINK_STAR = ITEMS.register(
            "pink_star",
            () -> new BlockItem(ModBlocks.PINK_STAR.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> GRAY_STAR = ITEMS.register(
            "gray_star",
            () -> new BlockItem(ModBlocks.GRAY_STAR.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> LIGHT_GRAY_STAR = ITEMS.register(
            "light_gray_star",
            () ->
                    new BlockItem(
                    ModBlocks.LIGHT_GRAY_STAR.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> CYAN_STAR = ITEMS.register(
            "cyan_star",
            () -> new BlockItem(ModBlocks.CYAN_STAR.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> PURPLE_STAR = ITEMS.register(
            "purple_star",
            () ->
                    new BlockItem(ModBlocks.PURPLE_STAR.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BLUE_STAR = ITEMS.register(
            "blue_star",
            () -> new BlockItem(ModBlocks.BLUE_STAR.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BROWN_STAR = ITEMS.register(
            "brown_star",
            () -> new BlockItem(ModBlocks.BROWN_STAR.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> GREEN_STAR = ITEMS.register(
            "green_star",
            () -> new BlockItem(ModBlocks.GREEN_STAR.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> RED_STAR = ITEMS.register(
            "red_star",
            () -> new BlockItem(ModBlocks.RED_STAR.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BLACK_STAR = ITEMS.register(
            "black_star",
            () -> new BlockItem(ModBlocks.BLACK_STAR.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> GLOW_STAR = ITEMS.register(
            "glow_star",
            () -> new BlockItem(ModBlocks.GLOW_STAR.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> SNOWY_LEAVES = ITEMS.register(
            "snowy_leaves",
            () ->
                    new BlockItem(ModBlocks.SNOWY_LEAVES.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> SNOWY_OAK_LEAVES = ITEMS.register(
            "snowy_oak_leaves",
            () ->
                    new BlockItem(
                    ModBlocks.SNOWY_OAK_LEAVES.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> SNOWY_SPRUCE_LEAVES = ITEMS.register(
            "snowy_spruce_leaves",
            () ->
                    new BlockItem(
                    ModBlocks.SNOWY_SPRUCE_LEAVES.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> SNOWY_BIRCH_LEAVES = ITEMS.register(
            "snowy_birch_leaves",
            () ->
                    new BlockItem(
                    ModBlocks.SNOWY_BIRCH_LEAVES.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> SNOWY_JUNGLE_LEAVES = ITEMS.register(
            "snowy_jungle_leaves",
            () ->
                    new BlockItem(
                    ModBlocks.SNOWY_JUNGLE_LEAVES.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> SNOWY_ACACIA_LEAVES = ITEMS.register(
            "snowy_acacia_leaves",
            () ->
                    new BlockItem(
                    ModBlocks.SNOWY_ACACIA_LEAVES.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> SNOWY_DARK_OAK_LEAVES =
            ITEMS.register("snowy_dark_oak_leaves", () ->
                    new BlockItem(
                    ModBlocks.SNOWY_DARK_OAK_LEAVES.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> SNOWY_MANGROVE_LEAVES =
            ITEMS.register("snowy_mangrove_leaves", () ->
                    new BlockItem(
                    ModBlocks.SNOWY_MANGROVE_LEAVES.get(),
                            createBlockItemProperties()
                    )
            );
    public static final RegistryObject<Item> SNOWY_AZALEA_LEAVES = ITEMS.register(
            "snowy_azalea_leaves",
            () ->
                    new BlockItem(
                    ModBlocks.SNOWY_AZALEA_LEAVES.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> SNOWY_FLOWERING_AZALEA_LEAVES =
            ITEMS.register("snowy_flowering_azalea_leaves", () ->
                            new BlockItem(
            ModBlocks.SNOWY_FLOWERING_AZALEA_LEAVES.get(),
                                    createBlockItemProperties()
                            )
            );

    public static final RegistryObject<Item> SNOWY_CHERRY_LEAVES =
            ITEMS.register("snowy_cherry_leaves", () ->
                    new BlockItem(
                            ModBlocks.SNOWY_CHERRY_LEAVES.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> SNOWY_PALE_OAK_LEAVES =
            ITEMS.register("snowy_pale_oak_leaves", () ->
                    new BlockItem(
                            ModBlocks.SNOWY_PALE_OAK_LEAVES.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> SNOWY_ORANGE_POPLAR_LEAVES =
            ITEMS.register("snowy_orange_poplar_leaves", () ->
                    new BlockItem(
                            ModBlocks.SNOWY_ORANGE_POPLAR_LEAVES.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> SNOWY_RED_POPLAR_LEAVES =
            ITEMS.register("snowy_red_poplar_leaves", () ->
                    new BlockItem(
                            ModBlocks.SNOWY_RED_POPLAR_LEAVES.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> SNOWY_YELLOW_POPLAR_LEAVES =
            ITEMS.register("snowy_yellow_poplar_leaves", () ->
                    new BlockItem(
                            ModBlocks.SNOWY_YELLOW_POPLAR_LEAVES.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> SNOWY_SHORT_GRASS = ITEMS.register(
            "snowy_short_grass",
            () ->
                    new BlockItem(
                    ModBlocks.SNOWY_SHORT_GRASS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> SNOWY_TALL_GRASS = ITEMS.register(
            "snowy_tall_grass",
            () ->
                    new BlockItem(
                    ModBlocks.SNOWY_TALL_GRASS.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> SNOWY_FERN = ITEMS.register(
            "snowy_fern",
            () -> new BlockItem(ModBlocks.SNOWY_FERN.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> SNOWY_LARGE_FERN = ITEMS.register(
            "snowy_large_fern",
            () ->
                    new BlockItem(
                    ModBlocks.SNOWY_LARGE_FERN.get(),
                            createBlockItemProperties()
                    )
    );
    public static final RegistryObject<Item> SNOWY_BUSH = ITEMS.register(
            "snowy_bush",
            () -> new BlockItem(ModBlocks.SNOWY_BUSH.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> SNOW_BRICKS = ITEMS.register(
            "snow_bricks",
            () ->
                    new BlockItem(ModBlocks.SNOW_BRICKS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> SNOW_BRICKS_STAIRS = ITEMS.register(
            "snow_bricks_stairs",
            () ->
                    new BlockItem(
                    ModBlocks.SNOW_BRICKS_STAIRS.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> SNOW_BRICKS_SLAB = ITEMS.register(
            "snow_bricks_slab",
            () ->
                    new BlockItem(
                    ModBlocks.SNOW_BRICKS_SLAB.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> SNOW_BRICKS_WALL = ITEMS.register(
            "snow_bricks_wall",
            () ->
                    new BlockItem(
                    ModBlocks.SNOW_BRICKS_WALL.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> SNOWY_GRASS_BLOCK = ITEMS.register(
            "snowy_grass_block",
            () ->
                    new BlockItem(
                    ModBlocks.SNOWY_GRASS_BLOCK.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> SNOWY_GRASS_BLOCK_STAIRS =
            ITEMS.register("snowy_grass_block_stairs", () ->
                    new BlockItem(
                    ModBlocks.SNOWY_GRASS_BLOCK_STAIRS.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> SNOWY_GRASS_BLOCK_SLAB =
            ITEMS.register("snowy_grass_block_slab", () ->
                    new BlockItem(
                    ModBlocks.SNOWY_GRASS_BLOCK_SLAB.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> ICICLE = ITEMS.register(
            "icicle",
            () -> new BlockItem(ModBlocks.ICICLE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> ICICLE_BLOCK = ITEMS.register(
            "icicle_block",
            () ->
                    new BlockItem(ModBlocks.ICICLE_BLOCK.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> PACKED_ICICLE_BLOCK = ITEMS.register(
            "packed_icicle_block",
            () ->
                    new BlockItem(
                    ModBlocks.PACKED_ICICLE_BLOCK.get(),
                            createBlockItemProperties()
                    )
    );

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

    public static final RegistryObject<Item> SNOW_STAIRS = ITEMS.register(
            "snow_stairs",
            () ->
                    new BlockItem(ModBlocks.SNOW_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> SNOW_SLAB = ITEMS.register(
            "snow_slab",
            () -> new BlockItem(ModBlocks.SNOW_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> MANGROVE_LOG = ITEMS.register(
            "mangrove_log",
            () ->
                    new BlockItem(ModBlocks.MANGROVE_LOG.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> STRIPPED_MANGROVE_LOG =
            ITEMS.register("stripped_mangrove_log", () ->
                    new BlockItem(
                    ModBlocks.STRIPPED_MANGROVE_LOG.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> MANGROVE_WOOD = ITEMS.register(
            "mangrove_wood",
            () ->
                    new BlockItem(ModBlocks.MANGROVE_WOOD.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> STRIPPED_MANGROVE_WOOD =
            ITEMS.register("stripped_mangrove_wood", () ->
                    new BlockItem(
                    ModBlocks.STRIPPED_MANGROVE_WOOD.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> MANGROVE_LEAVES = ITEMS.register(
            "mangrove_leaves",
            () ->
                    new BlockItem(
                    ModBlocks.MANGROVE_LEAVES.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> MANGROVE_ROOTS = ITEMS.register(
            "mangrove_roots",
            () ->
                    new BlockItem(ModBlocks.MANGROVE_ROOTS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> MUDDY_MANGROVE_ROOTS =
            ITEMS.register("muddy_mangrove_roots", () ->
                    new BlockItem(
                    ModBlocks.MUDDY_MANGROVE_ROOTS.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> MANGROVE_PROPAGULE = ITEMS.register(
            "mangrove_propagule",
            () ->
                    new BlockItem(
                    ModBlocks.MANGROVE_PROPAGULE.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> MANGROVE_PLANKS = ITEMS.register(
            "mangrove_planks",
            () ->
                    new BlockItem(
                    ModBlocks.MANGROVE_PLANKS.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> MANGROVE_SLAB = ITEMS.register(
            "mangrove_slab",
            () ->
                    new BlockItem(ModBlocks.MANGROVE_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> MANGROVE_STAIRS = ITEMS.register(
            "mangrove_stairs",
            () ->
                    new BlockItem(
                    ModBlocks.MANGROVE_STAIRS.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> MANGROVE_FENCE = ITEMS.register(
            "mangrove_fence",
            () ->
                    new BlockItem(ModBlocks.MANGROVE_FENCE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> MANGROVE_FENCE_GATE = ITEMS.register(
            "mangrove_fence_gate",
            () ->
                    new BlockItem(
                    ModBlocks.MANGROVE_FENCE_GATE.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> MANGROVE_DOOR = ITEMS.register(
            "mangrove_door",
            () ->
                    new BlockItem(ModBlocks.MANGROVE_DOOR.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> MANGROVE_TRAPDOOR = ITEMS.register(
            "mangrove_trapdoor",
            () ->
                    new BlockItem(
                    ModBlocks.MANGROVE_TRAPDOOR.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> MANGROVE_BUTTON = ITEMS.register(
            "mangrove_button",
            () ->
                    new BlockItem(
                    ModBlocks.MANGROVE_BUTTON.get(),
                            createBlockItemProperties()
                    )
    );

    public static final RegistryObject<Item> MANGROVE_PRESSURE_PLATE =
            ITEMS.register("mangrove_pressure_plate", () ->
                    new BlockItem(
                    ModBlocks.MANGROVE_PRESSURE_PLATE.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> MANGROVE_SIGN = ITEMS.register(
            "mangrove_sign",
            () ->
                    new net.minecraft.world.item.SignItem(
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(16),
                    ModBlocks.MANGROVE_SIGN.get(),
                            ModBlocks.MANGROVE_WALL_SIGN.get()
                    )
    );

    public static final RegistryObject<Item> BAMBOO_SIGN = ITEMS.register(
            "bamboo_sign",
            () ->
                    new net.minecraft.world.item.SignItem(
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

    public static final RegistryObject<Item> FESTIVE_LAMP = ITEMS.register(
            "festive_lamp",
            () ->
                    new BlockItem(ModBlocks.FESTIVE_LAMP.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FROST_ROSE = ITEMS.register(
            "frost_rose",
            () -> new BlockItem(ModBlocks.FROST_ROSE.get(), createBlockItemProperties()) {
                @Override
                public void appendHoverText(net.minecraft.world.item.ItemStack stack, @javax.annotation.Nullable net.minecraft.world.level.Level level, java.util.List<net.minecraft.network.chat.Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
                    tooltip.add(new net.minecraft.network.chat.TextComponent("Obtained when a Wither kills a Snow Golem")
                            .withStyle(net.minecraft.ChatFormatting.DARK_AQUA));
                }
            }
    );

    public static final RegistryObject<Item> GLOW_LIGHTS = ITEMS.register(
            "glow_lights",
            () ->
                    new BlockItem(ModBlocks.GLOW_LIGHTS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> MULTICOLOR_GLOW_LIGHTS =
            ITEMS.register("multicolor_glow_lights", () ->
                    new BlockItem(
                    ModBlocks.MULTICOLOR_GLOW_LIGHTS.get(),
                            createBlockItemProperties()
                    )
            );

    public static final RegistryObject<Item> CONFETTI_ITEM = ITEMS.register(
            "confetti",
            () -> new ConfettiItem(new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(64))
    );

    public static final RegistryObject<Item> INFINITE_PHOENIX_FIREWORK_STAR = ITEMS.register(
            "infinite_phoenix_firework_star",
            () -> new InfinitePhoenixFireworkStarItem(new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(1))
    );

    public static final RegistryObject<Item> STEEL_BLOCK = ITEMS.register(
            "steel_block",
            () -> new BlockItem(ModBlocks.STEEL_BLOCK.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> STEEL_PIPE = ITEMS.register(
            "steel_pipe",
            () -> new BlockItem(ModBlocks.STEEL_PIPE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> HOLLOW_STEEL_PIPE = ITEMS.register(
            "hollow_steel_pipe",
            () -> new BlockItem(ModBlocks.HOLLOW_STEEL_PIPE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> SCRAPED_STEEL = ITEMS.register(
            "scraped_steel",
            () -> new BlockItem(ModBlocks.SCRAPED_STEEL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> RUSTIC_SCRAPED_STEEL = ITEMS.register(
            "rustic_scraped_steel",
            () -> new BlockItem(ModBlocks.RUSTIC_SCRAPED_STEEL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> STACKED_STEEL = ITEMS.register(
            "stacked_steel",
            () -> new BlockItem(ModBlocks.STACKED_STEEL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> STEEL_PANELS = ITEMS.register(
            "steel_panels",
            () -> new BlockItem(ModBlocks.STEEL_PANELS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CROSSED_STEEL_PANELS = ITEMS.register(
            "crossed_steel_panels",
            () -> new BlockItem(ModBlocks.CROSSED_STEEL_PANELS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> STEEL_MESH_BLOCK = ITEMS.register(
            "steel_mesh_block",
            () -> new BlockItem(ModBlocks.STEEL_MESH_BLOCK.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> STEEL_BOLTS = ITEMS.register(
            "steel_bolts",
            () -> new BlockItem(ModBlocks.STEEL_BOLTS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> STEEL_DOOR = ITEMS.register(
            "steel_door",
            () -> new DoubleHighBlockItem(ModBlocks.STEEL_DOOR.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> STEEL_TRAPDOOR = ITEMS.register(
            "steel_trapdoor",
            () -> new BlockItem(ModBlocks.STEEL_TRAPDOOR.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> STEEL_BUTTON = ITEMS.register(
            "steel_button",
            () -> new BlockItem(ModBlocks.STEEL_BUTTON.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> STEEL_PRESSURE_PLATE = ITEMS.register(
            "steel_pressure_plate",
            () -> new BlockItem(ModBlocks.STEEL_PRESSURE_PLATE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> SCRAPED_STEEL_SLAB = ITEMS.register(
            "scraped_steel_slab",
            () -> new BlockItem(ModBlocks.SCRAPED_STEEL_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> SCRAPED_STEEL_VERTICAL_SLAB = ITEMS.register(
            "scraped_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.SCRAPED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> RUSTIC_SCRAPED_STEEL_SLAB = ITEMS.register(
            "rustic_scraped_steel_slab",
            () -> new BlockItem(ModBlocks.RUSTIC_SCRAPED_STEEL_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> RUSTIC_SCRAPED_STEEL_VERTICAL_SLAB = ITEMS.register(
            "rustic_scraped_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.RUSTIC_SCRAPED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> STACKED_STEEL_SLAB = ITEMS.register(
            "stacked_steel_slab",
            () -> new BlockItem(ModBlocks.STACKED_STEEL_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> STACKED_STEEL_VERTICAL_SLAB = ITEMS.register(
            "stacked_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.STACKED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> STACKED_STEEL_STAIRS = ITEMS.register(
            "stacked_steel_stairs",
            () -> new BlockItem(ModBlocks.STACKED_STEEL_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> STEEL_PANELS_SLAB = ITEMS.register(
            "steel_panels_slab",
            () -> new BlockItem(ModBlocks.STEEL_PANELS_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> STEEL_PANELS_VERTICAL_SLAB = ITEMS.register(
            "steel_panels_vertical_slab",
            () -> new BlockItem(ModBlocks.STEEL_PANELS_VERTICAL_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> STEEL_PANELS_STAIRS = ITEMS.register(
            "steel_panels_stairs",
            () -> new BlockItem(ModBlocks.STEEL_PANELS_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CROSSED_STEEL_PANELS_SLAB = ITEMS.register(
            "crossed_steel_panels_slab",
            () -> new BlockItem(ModBlocks.CROSSED_STEEL_PANELS_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CROSSED_STEEL_PANELS_VERTICAL_SLAB = ITEMS.register(
            "crossed_steel_panels_vertical_slab",
            () -> new BlockItem(ModBlocks.CROSSED_STEEL_PANELS_VERTICAL_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> STEEL_MESH_BLOCK_SLAB = ITEMS.register(
            "steel_mesh_block_slab",
            () -> new BlockItem(ModBlocks.STEEL_MESH_BLOCK_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> STEEL_MESH_BLOCK_VERTICAL_SLAB = ITEMS.register(
            "steel_mesh_block_vertical_slab",
            () -> new BlockItem(ModBlocks.STEEL_MESH_BLOCK_VERTICAL_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> STEEL_MESH_BLOCK_STAIRS = ITEMS.register(
            "steel_mesh_block_stairs",
            () -> new BlockItem(ModBlocks.STEEL_MESH_BLOCK_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> PRESSED_STEEL = ITEMS.register(
            "pressed_steel",
            () -> new BlockItem(ModBlocks.PRESSED_STEEL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CUT_STEEL = ITEMS.register(
            "cut_steel",
            () -> new BlockItem(ModBlocks.CUT_STEEL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_STEEL = ITEMS.register(
            "polished_steel",
            () -> new BlockItem(ModBlocks.POLISHED_STEEL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_STEEL_PANEL = ITEMS.register(
            "factory_steel_panel",
            () -> new BlockItem(ModBlocks.FACTORY_STEEL_PANEL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> STEEL_CASING = ITEMS.register(
            "steel_casing",
            () -> new BlockItem(ModBlocks.STEEL_CASING.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> STEEL_TRIM = ITEMS.register(
            "steel_trim",
            () -> new BlockItem(ModBlocks.STEEL_TRIM.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> STEEL_PILLAR = ITEMS.register(
            "steel_pillar",
            () -> new BlockItem(ModBlocks.STEEL_PILLAR.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BOLTED_STEEL_PILLAR = ITEMS.register(
            "bolted_steel_pillar",
            () -> new BlockItem(ModBlocks.BOLTED_STEEL_PILLAR.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> STEEL_GRATE = ITEMS.register(
            "steel_grate",
            () -> new BlockItem(ModBlocks.STEEL_GRATE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> STEEL_FAN = ITEMS.register(
            "steel_fan",
            () -> new BlockItem(ModBlocks.STEEL_FAN.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> STEEL_BLOCK_STAIRS = ITEMS.register(
            "steel_block_stairs",
            () -> new BlockItem(ModBlocks.STEEL_BLOCK_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> STEEL_BLOCK_SLAB = ITEMS.register(
            "steel_block_slab",
            () -> new BlockItem(ModBlocks.STEEL_BLOCK_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> STEEL_BLOCK_WALL = ITEMS.register(
            "steel_block_wall",
            () -> new BlockItem(ModBlocks.STEEL_BLOCK_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_STEEL_STAIRS = ITEMS.register(
            "polished_steel_stairs",
            () -> new BlockItem(ModBlocks.POLISHED_STEEL_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_STEEL_SLAB = ITEMS.register(
            "polished_steel_slab",
            () -> new BlockItem(ModBlocks.POLISHED_STEEL_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_STEEL_WALL = ITEMS.register(
            "polished_steel_wall",
            () -> new BlockItem(ModBlocks.POLISHED_STEEL_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> PRESSED_STEEL_STAIRS = ITEMS.register(
            "pressed_steel_stairs",
            () -> new BlockItem(ModBlocks.PRESSED_STEEL_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> PRESSED_STEEL_SLAB = ITEMS.register(
            "pressed_steel_slab",
            () -> new BlockItem(ModBlocks.PRESSED_STEEL_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> PRESSED_STEEL_WALL = ITEMS.register(
            "pressed_steel_wall",
            () -> new BlockItem(ModBlocks.PRESSED_STEEL_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CUT_STEEL_STAIRS = ITEMS.register(
            "cut_steel_stairs",
            () -> new BlockItem(ModBlocks.CUT_STEEL_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CUT_STEEL_SLAB = ITEMS.register(
            "cut_steel_slab",
            () -> new BlockItem(ModBlocks.CUT_STEEL_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CUT_STEEL_WALL = ITEMS.register(
            "cut_steel_wall",
            () -> new BlockItem(ModBlocks.CUT_STEEL_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_BLACK = ITEMS.register(
            "caution_black",
            () -> new BlockItem(ModBlocks.CAUTION_BLACK.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_BLUE = ITEMS.register(
            "caution_blue",
            () -> new BlockItem(ModBlocks.CAUTION_BLUE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_FACTORY = ITEMS.register(
            "caution_factory",
            () -> new BlockItem(ModBlocks.CAUTION_FACTORY.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_RED = ITEMS.register(
            "caution_red",
            () -> new BlockItem(ModBlocks.CAUTION_RED.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FRAMED_CAUTION = ITEMS.register(
            "framed_caution",
            () -> new BlockItem(ModBlocks.FRAMED_CAUTION.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_WHITE = ITEMS.register(
            "caution_white",
            () -> new BlockItem(ModBlocks.CAUTION_WHITE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_LIME = ITEMS.register(
            "caution_lime",
            () -> new BlockItem(ModBlocks.CAUTION_LIME.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_PINK = ITEMS.register(
            "caution_pink",
            () -> new BlockItem(ModBlocks.CAUTION_PINK.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_YELLOW = ITEMS.register(
            "caution_yellow",
            () -> new BlockItem(ModBlocks.CAUTION_YELLOW.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_CANDY = ITEMS.register(
            "caution_candy",
            () -> new BlockItem(ModBlocks.CAUTION_CANDY.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_COTTONCANDY = ITEMS.register(
            "caution_cottoncandy",
            () -> new BlockItem(ModBlocks.CAUTION_COTTONCANDY.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_MINTCANDY = ITEMS.register(
            "caution_mintcandy",
            () -> new BlockItem(ModBlocks.CAUTION_MINTCANDY.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_CITRUSCANDY = ITEMS.register(
            "caution_citruscandy",
            () -> new BlockItem(ModBlocks.CAUTION_CITRUSCANDY.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_BLACK_SLAB = ITEMS.register(
            "caution_black_slab",
            () -> new BlockItem(ModBlocks.CAUTION_BLACK_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_BLUE_SLAB = ITEMS.register(
            "caution_blue_slab",
            () -> new BlockItem(ModBlocks.CAUTION_BLUE_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_FACTORY_SLAB = ITEMS.register(
            "caution_factory_slab",
            () -> new BlockItem(ModBlocks.CAUTION_FACTORY_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_RED_SLAB = ITEMS.register(
            "caution_red_slab",
            () -> new BlockItem(ModBlocks.CAUTION_RED_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FRAMED_CAUTION_SLAB = ITEMS.register(
            "framed_caution_slab",
            () -> new BlockItem(ModBlocks.FRAMED_CAUTION_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_WHITE_SLAB = ITEMS.register(
            "caution_white_slab",
            () -> new BlockItem(ModBlocks.CAUTION_WHITE_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_LIME_SLAB = ITEMS.register(
            "caution_lime_slab",
            () -> new BlockItem(ModBlocks.CAUTION_LIME_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_PINK_SLAB = ITEMS.register(
            "caution_pink_slab",
            () -> new BlockItem(ModBlocks.CAUTION_PINK_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_YELLOW_SLAB = ITEMS.register(
            "caution_yellow_slab",
            () -> new BlockItem(ModBlocks.CAUTION_YELLOW_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_CANDY_SLAB = ITEMS.register(
            "caution_candy_slab",
            () -> new BlockItem(ModBlocks.CAUTION_CANDY_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_COTTONCANDY_SLAB = ITEMS.register(
            "caution_cottoncandy_slab",
            () -> new BlockItem(ModBlocks.CAUTION_COTTONCANDY_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_MINTCANDY_SLAB = ITEMS.register(
            "caution_mintcandy_slab",
            () -> new BlockItem(ModBlocks.CAUTION_MINTCANDY_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_CITRUSCANDY_SLAB = ITEMS.register(
            "caution_citruscandy_slab",
            () -> new BlockItem(ModBlocks.CAUTION_CITRUSCANDY_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_BLACK_STAIRS = ITEMS.register(
            "caution_black_stairs",
            () -> new BlockItem(ModBlocks.CAUTION_BLACK_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_BLUE_STAIRS = ITEMS.register(
            "caution_blue_stairs",
            () -> new BlockItem(ModBlocks.CAUTION_BLUE_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_FACTORY_STAIRS = ITEMS.register(
            "caution_factory_stairs",
            () -> new BlockItem(ModBlocks.CAUTION_FACTORY_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_RED_STAIRS = ITEMS.register(
            "caution_red_stairs",
            () -> new BlockItem(ModBlocks.CAUTION_RED_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FRAMED_CAUTION_STAIRS = ITEMS.register(
            "framed_caution_stairs",
            () -> new BlockItem(ModBlocks.FRAMED_CAUTION_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_WHITE_STAIRS = ITEMS.register(
            "caution_white_stairs",
            () -> new BlockItem(ModBlocks.CAUTION_WHITE_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_LIME_STAIRS = ITEMS.register(
            "caution_lime_stairs",
            () -> new BlockItem(ModBlocks.CAUTION_LIME_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_PINK_STAIRS = ITEMS.register(
            "caution_pink_stairs",
            () -> new BlockItem(ModBlocks.CAUTION_PINK_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_YELLOW_STAIRS = ITEMS.register(
            "caution_yellow_stairs",
            () -> new BlockItem(ModBlocks.CAUTION_YELLOW_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_CANDY_STAIRS = ITEMS.register(
            "caution_candy_stairs",
            () -> new BlockItem(ModBlocks.CAUTION_CANDY_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_COTTONCANDY_STAIRS = ITEMS.register(
            "caution_cottoncandy_stairs",
            () -> new BlockItem(ModBlocks.CAUTION_COTTONCANDY_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_MINTCANDY_STAIRS = ITEMS.register(
            "caution_mintcandy_stairs",
            () -> new BlockItem(ModBlocks.CAUTION_MINTCANDY_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CAUTION_CITRUSCANDY_STAIRS = ITEMS.register(
            "caution_citruscandy_stairs",
            () -> new BlockItem(ModBlocks.CAUTION_CITRUSCANDY_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_WHITE_GLASS = ITEMS.register(
            "factory_white_glass",
            () -> new BlockItem(ModBlocks.FACTORY_WHITE_GLASS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_LIGHT_GRAY_GLASS = ITEMS.register(
            "factory_light_gray_glass",
            () -> new BlockItem(ModBlocks.FACTORY_LIGHT_GRAY_GLASS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_GRAY_GLASS = ITEMS.register(
            "factory_gray_glass",
            () -> new BlockItem(ModBlocks.FACTORY_GRAY_GLASS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_BLACK_GLASS = ITEMS.register(
            "factory_black_glass",
            () -> new BlockItem(ModBlocks.FACTORY_BLACK_GLASS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_BROWN_GLASS = ITEMS.register(
            "factory_brown_glass",
            () -> new BlockItem(ModBlocks.FACTORY_BROWN_GLASS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_RED_GLASS = ITEMS.register(
            "factory_red_glass",
            () -> new BlockItem(ModBlocks.FACTORY_RED_GLASS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_ORANGE_GLASS = ITEMS.register(
            "factory_orange_glass",
            () -> new BlockItem(ModBlocks.FACTORY_ORANGE_GLASS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_YELLOW_GLASS = ITEMS.register(
            "factory_yellow_glass",
            () -> new BlockItem(ModBlocks.FACTORY_YELLOW_GLASS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_LIME_GLASS = ITEMS.register(
            "factory_lime_glass",
            () -> new BlockItem(ModBlocks.FACTORY_LIME_GLASS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_GREEN_GLASS = ITEMS.register(
            "factory_green_glass",
            () -> new BlockItem(ModBlocks.FACTORY_GREEN_GLASS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_CYAN_GLASS = ITEMS.register(
            "factory_cyan_glass",
            () -> new BlockItem(ModBlocks.FACTORY_CYAN_GLASS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_LIGHT_BLUE_GLASS = ITEMS.register(
            "factory_light_blue_glass",
            () -> new BlockItem(ModBlocks.FACTORY_LIGHT_BLUE_GLASS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_BLUE_GLASS = ITEMS.register(
            "factory_blue_glass",
            () -> new BlockItem(ModBlocks.FACTORY_BLUE_GLASS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_PURPLE_GLASS = ITEMS.register(
            "factory_purple_glass",
            () -> new BlockItem(ModBlocks.FACTORY_PURPLE_GLASS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_MAGENTA_GLASS = ITEMS.register(
            "factory_magenta_glass",
            () -> new BlockItem(ModBlocks.FACTORY_MAGENTA_GLASS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_PINK_GLASS = ITEMS.register(
            "factory_pink_glass",
            () -> new BlockItem(ModBlocks.FACTORY_PINK_GLASS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_WHITE_GLASS_PANE = ITEMS.register(
            "factory_white_glass_pane",
            () -> new BlockItem(ModBlocks.FACTORY_WHITE_GLASS_PANE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_LIGHT_GRAY_GLASS_PANE = ITEMS.register(
            "factory_light_gray_glass_pane",
            () -> new BlockItem(ModBlocks.FACTORY_LIGHT_GRAY_GLASS_PANE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_GRAY_GLASS_PANE = ITEMS.register(
            "factory_gray_glass_pane",
            () -> new BlockItem(ModBlocks.FACTORY_GRAY_GLASS_PANE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_BLACK_GLASS_PANE = ITEMS.register(
            "factory_black_glass_pane",
            () -> new BlockItem(ModBlocks.FACTORY_BLACK_GLASS_PANE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_BROWN_GLASS_PANE = ITEMS.register(
            "factory_brown_glass_pane",
            () -> new BlockItem(ModBlocks.FACTORY_BROWN_GLASS_PANE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_RED_GLASS_PANE = ITEMS.register(
            "factory_red_glass_pane",
            () -> new BlockItem(ModBlocks.FACTORY_RED_GLASS_PANE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_ORANGE_GLASS_PANE = ITEMS.register(
            "factory_orange_glass_pane",
            () -> new BlockItem(ModBlocks.FACTORY_ORANGE_GLASS_PANE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_YELLOW_GLASS_PANE = ITEMS.register(
            "factory_yellow_glass_pane",
            () -> new BlockItem(ModBlocks.FACTORY_YELLOW_GLASS_PANE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_LIME_GLASS_PANE = ITEMS.register(
            "factory_lime_glass_pane",
            () -> new BlockItem(ModBlocks.FACTORY_LIME_GLASS_PANE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_GREEN_GLASS_PANE = ITEMS.register(
            "factory_green_glass_pane",
            () -> new BlockItem(ModBlocks.FACTORY_GREEN_GLASS_PANE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_CYAN_GLASS_PANE = ITEMS.register(
            "factory_cyan_glass_pane",
            () -> new BlockItem(ModBlocks.FACTORY_CYAN_GLASS_PANE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_LIGHT_BLUE_GLASS_PANE = ITEMS.register(
            "factory_light_blue_glass_pane",
            () -> new BlockItem(ModBlocks.FACTORY_LIGHT_BLUE_GLASS_PANE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_BLUE_GLASS_PANE = ITEMS.register(
            "factory_blue_glass_pane",
            () -> new BlockItem(ModBlocks.FACTORY_BLUE_GLASS_PANE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_PURPLE_GLASS_PANE = ITEMS.register(
            "factory_purple_glass_pane",
            () -> new BlockItem(ModBlocks.FACTORY_PURPLE_GLASS_PANE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_MAGENTA_GLASS_PANE = ITEMS.register(
            "factory_magenta_glass_pane",
            () -> new BlockItem(ModBlocks.FACTORY_MAGENTA_GLASS_PANE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FACTORY_PINK_GLASS_PANE = ITEMS.register(
            "factory_pink_glass_pane",
            () -> new BlockItem(ModBlocks.FACTORY_PINK_GLASS_PANE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> WHITE_STAINED_BRICKS = ITEMS.register(
            "white_stained_bricks",
            () -> new BlockItem(ModBlocks.WHITE_STAINED_BRICKS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICKS = ITEMS.register(
            "light_gray_stained_bricks",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_STAINED_BRICKS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> GRAY_STAINED_BRICKS = ITEMS.register(
            "gray_stained_bricks",
            () -> new BlockItem(ModBlocks.GRAY_STAINED_BRICKS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BLACK_STAINED_BRICKS = ITEMS.register(
            "black_stained_bricks",
            () -> new BlockItem(ModBlocks.BLACK_STAINED_BRICKS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BROWN_STAINED_BRICKS = ITEMS.register(
            "brown_stained_bricks",
            () -> new BlockItem(ModBlocks.BROWN_STAINED_BRICKS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> RED_STAINED_BRICKS = ITEMS.register(
            "red_stained_bricks",
            () -> new BlockItem(ModBlocks.RED_STAINED_BRICKS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> ORANGE_STAINED_BRICKS = ITEMS.register(
            "orange_stained_bricks",
            () -> new BlockItem(ModBlocks.ORANGE_STAINED_BRICKS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> YELLOW_STAINED_BRICKS = ITEMS.register(
            "yellow_stained_bricks",
            () -> new BlockItem(ModBlocks.YELLOW_STAINED_BRICKS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> LIME_STAINED_BRICKS = ITEMS.register(
            "lime_stained_bricks",
            () -> new BlockItem(ModBlocks.LIME_STAINED_BRICKS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> GREEN_STAINED_BRICKS = ITEMS.register(
            "green_stained_bricks",
            () -> new BlockItem(ModBlocks.GREEN_STAINED_BRICKS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CYAN_STAINED_BRICKS = ITEMS.register(
            "cyan_stained_bricks",
            () -> new BlockItem(ModBlocks.CYAN_STAINED_BRICKS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICKS = ITEMS.register(
            "light_blue_stained_bricks",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_STAINED_BRICKS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BLUE_STAINED_BRICKS = ITEMS.register(
            "blue_stained_bricks",
            () -> new BlockItem(ModBlocks.BLUE_STAINED_BRICKS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> PURPLE_STAINED_BRICKS = ITEMS.register(
            "purple_stained_bricks",
            () -> new BlockItem(ModBlocks.PURPLE_STAINED_BRICKS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> MAGENTA_STAINED_BRICKS = ITEMS.register(
            "magenta_stained_bricks",
            () -> new BlockItem(ModBlocks.MAGENTA_STAINED_BRICKS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> PINK_STAINED_BRICKS = ITEMS.register(
            "pink_stained_bricks",
            () -> new BlockItem(ModBlocks.PINK_STAINED_BRICKS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> WHITE_STAINED_BRICK_TILES = ITEMS.register(
            "white_stained_brick_tiles",
            () -> new BlockItem(ModBlocks.WHITE_STAINED_BRICK_TILES.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICK_TILES = ITEMS.register(
            "light_gray_stained_brick_tiles",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_STAINED_BRICK_TILES.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> GRAY_STAINED_BRICK_TILES = ITEMS.register(
            "gray_stained_brick_tiles",
            () -> new BlockItem(ModBlocks.GRAY_STAINED_BRICK_TILES.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BLACK_STAINED_BRICK_TILES = ITEMS.register(
            "black_stained_brick_tiles",
            () -> new BlockItem(ModBlocks.BLACK_STAINED_BRICK_TILES.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BROWN_STAINED_BRICK_TILES = ITEMS.register(
            "brown_stained_brick_tiles",
            () -> new BlockItem(ModBlocks.BROWN_STAINED_BRICK_TILES.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> RED_STAINED_BRICK_TILES = ITEMS.register(
            "red_stained_brick_tiles",
            () -> new BlockItem(ModBlocks.RED_STAINED_BRICK_TILES.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> ORANGE_STAINED_BRICK_TILES = ITEMS.register(
            "orange_stained_brick_tiles",
            () -> new BlockItem(ModBlocks.ORANGE_STAINED_BRICK_TILES.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> YELLOW_STAINED_BRICK_TILES = ITEMS.register(
            "yellow_stained_brick_tiles",
            () -> new BlockItem(ModBlocks.YELLOW_STAINED_BRICK_TILES.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIME_STAINED_BRICK_TILES = ITEMS.register(
            "lime_stained_brick_tiles",
            () -> new BlockItem(ModBlocks.LIME_STAINED_BRICK_TILES.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> GREEN_STAINED_BRICK_TILES = ITEMS.register(
            "green_stained_brick_tiles",
            () -> new BlockItem(ModBlocks.GREEN_STAINED_BRICK_TILES.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> CYAN_STAINED_BRICK_TILES = ITEMS.register(
            "cyan_stained_brick_tiles",
            () -> new BlockItem(ModBlocks.CYAN_STAINED_BRICK_TILES.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICK_TILES = ITEMS.register(
            "light_blue_stained_brick_tiles",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_STAINED_BRICK_TILES.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BLUE_STAINED_BRICK_TILES = ITEMS.register(
            "blue_stained_brick_tiles",
            () -> new BlockItem(ModBlocks.BLUE_STAINED_BRICK_TILES.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PURPLE_STAINED_BRICK_TILES = ITEMS.register(
            "purple_stained_brick_tiles",
            () -> new BlockItem(ModBlocks.PURPLE_STAINED_BRICK_TILES.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICK_TILES = ITEMS.register(
            "magenta_stained_brick_tiles",
            () -> new BlockItem(ModBlocks.MAGENTA_STAINED_BRICK_TILES.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PINK_STAINED_BRICK_TILES = ITEMS.register(
            "pink_stained_brick_tiles",
            () -> new BlockItem(ModBlocks.PINK_STAINED_BRICK_TILES.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WHITE_STAINED_BRICK_TILES_SLAB = ITEMS.register(
            "white_stained_brick_tiles_slab",
            () -> new BlockItem(ModBlocks.WHITE_STAINED_BRICK_TILES_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WHITE_STAINED_BRICK_TILES_STAIRS = ITEMS.register(
            "white_stained_brick_tiles_stairs",
            () -> new BlockItem(ModBlocks.WHITE_STAINED_BRICK_TILES_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WHITE_STAINED_BRICK_TILES_WALL = ITEMS.register(
            "white_stained_brick_tiles_wall",
            () -> new BlockItem(ModBlocks.WHITE_STAINED_BRICK_TILES_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WHITE_STAINED_BRICK_TILES_VERTICAL_SLAB = ITEMS.register(
            "white_stained_brick_tiles_vertical_slab",
            () -> new BlockItem(ModBlocks.WHITE_STAINED_BRICK_TILES_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICK_TILES_SLAB = ITEMS.register(
            "light_gray_stained_brick_tiles_slab",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_STAINED_BRICK_TILES_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICK_TILES_STAIRS = ITEMS.register(
            "light_gray_stained_brick_tiles_stairs",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_STAINED_BRICK_TILES_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICK_TILES_WALL = ITEMS.register(
            "light_gray_stained_brick_tiles_wall",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_STAINED_BRICK_TILES_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICK_TILES_VERTICAL_SLAB = ITEMS.register(
            "light_gray_stained_brick_tiles_vertical_slab",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_STAINED_BRICK_TILES_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> GRAY_STAINED_BRICK_TILES_SLAB = ITEMS.register(
            "gray_stained_brick_tiles_slab",
            () -> new BlockItem(ModBlocks.GRAY_STAINED_BRICK_TILES_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> GRAY_STAINED_BRICK_TILES_STAIRS = ITEMS.register(
            "gray_stained_brick_tiles_stairs",
            () -> new BlockItem(ModBlocks.GRAY_STAINED_BRICK_TILES_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> GRAY_STAINED_BRICK_TILES_WALL = ITEMS.register(
            "gray_stained_brick_tiles_wall",
            () -> new BlockItem(ModBlocks.GRAY_STAINED_BRICK_TILES_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> GRAY_STAINED_BRICK_TILES_VERTICAL_SLAB = ITEMS.register(
            "gray_stained_brick_tiles_vertical_slab",
            () -> new BlockItem(ModBlocks.GRAY_STAINED_BRICK_TILES_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BLACK_STAINED_BRICK_TILES_SLAB = ITEMS.register(
            "black_stained_brick_tiles_slab",
            () -> new BlockItem(ModBlocks.BLACK_STAINED_BRICK_TILES_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BLACK_STAINED_BRICK_TILES_STAIRS = ITEMS.register(
            "black_stained_brick_tiles_stairs",
            () -> new BlockItem(ModBlocks.BLACK_STAINED_BRICK_TILES_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BLACK_STAINED_BRICK_TILES_WALL = ITEMS.register(
            "black_stained_brick_tiles_wall",
            () -> new BlockItem(ModBlocks.BLACK_STAINED_BRICK_TILES_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BLACK_STAINED_BRICK_TILES_VERTICAL_SLAB = ITEMS.register(
            "black_stained_brick_tiles_vertical_slab",
            () -> new BlockItem(ModBlocks.BLACK_STAINED_BRICK_TILES_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BROWN_STAINED_BRICK_TILES_SLAB = ITEMS.register(
            "brown_stained_brick_tiles_slab",
            () -> new BlockItem(ModBlocks.BROWN_STAINED_BRICK_TILES_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BROWN_STAINED_BRICK_TILES_STAIRS = ITEMS.register(
            "brown_stained_brick_tiles_stairs",
            () -> new BlockItem(ModBlocks.BROWN_STAINED_BRICK_TILES_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BROWN_STAINED_BRICK_TILES_WALL = ITEMS.register(
            "brown_stained_brick_tiles_wall",
            () -> new BlockItem(ModBlocks.BROWN_STAINED_BRICK_TILES_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BROWN_STAINED_BRICK_TILES_VERTICAL_SLAB = ITEMS.register(
            "brown_stained_brick_tiles_vertical_slab",
            () -> new BlockItem(ModBlocks.BROWN_STAINED_BRICK_TILES_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> RED_STAINED_BRICK_TILES_SLAB = ITEMS.register(
            "red_stained_brick_tiles_slab",
            () -> new BlockItem(ModBlocks.RED_STAINED_BRICK_TILES_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> RED_STAINED_BRICK_TILES_STAIRS = ITEMS.register(
            "red_stained_brick_tiles_stairs",
            () -> new BlockItem(ModBlocks.RED_STAINED_BRICK_TILES_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> RED_STAINED_BRICK_TILES_WALL = ITEMS.register(
            "red_stained_brick_tiles_wall",
            () -> new BlockItem(ModBlocks.RED_STAINED_BRICK_TILES_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> RED_STAINED_BRICK_TILES_VERTICAL_SLAB = ITEMS.register(
            "red_stained_brick_tiles_vertical_slab",
            () -> new BlockItem(ModBlocks.RED_STAINED_BRICK_TILES_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> ORANGE_STAINED_BRICK_TILES_SLAB = ITEMS.register(
            "orange_stained_brick_tiles_slab",
            () -> new BlockItem(ModBlocks.ORANGE_STAINED_BRICK_TILES_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> ORANGE_STAINED_BRICK_TILES_STAIRS = ITEMS.register(
            "orange_stained_brick_tiles_stairs",
            () -> new BlockItem(ModBlocks.ORANGE_STAINED_BRICK_TILES_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> ORANGE_STAINED_BRICK_TILES_WALL = ITEMS.register(
            "orange_stained_brick_tiles_wall",
            () -> new BlockItem(ModBlocks.ORANGE_STAINED_BRICK_TILES_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> ORANGE_STAINED_BRICK_TILES_VERTICAL_SLAB = ITEMS.register(
            "orange_stained_brick_tiles_vertical_slab",
            () -> new BlockItem(ModBlocks.ORANGE_STAINED_BRICK_TILES_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> YELLOW_STAINED_BRICK_TILES_SLAB = ITEMS.register(
            "yellow_stained_brick_tiles_slab",
            () -> new BlockItem(ModBlocks.YELLOW_STAINED_BRICK_TILES_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> YELLOW_STAINED_BRICK_TILES_STAIRS = ITEMS.register(
            "yellow_stained_brick_tiles_stairs",
            () -> new BlockItem(ModBlocks.YELLOW_STAINED_BRICK_TILES_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> YELLOW_STAINED_BRICK_TILES_WALL = ITEMS.register(
            "yellow_stained_brick_tiles_wall",
            () -> new BlockItem(ModBlocks.YELLOW_STAINED_BRICK_TILES_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> YELLOW_STAINED_BRICK_TILES_VERTICAL_SLAB = ITEMS.register(
            "yellow_stained_brick_tiles_vertical_slab",
            () -> new BlockItem(ModBlocks.YELLOW_STAINED_BRICK_TILES_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIME_STAINED_BRICK_TILES_SLAB = ITEMS.register(
            "lime_stained_brick_tiles_slab",
            () -> new BlockItem(ModBlocks.LIME_STAINED_BRICK_TILES_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIME_STAINED_BRICK_TILES_STAIRS = ITEMS.register(
            "lime_stained_brick_tiles_stairs",
            () -> new BlockItem(ModBlocks.LIME_STAINED_BRICK_TILES_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIME_STAINED_BRICK_TILES_WALL = ITEMS.register(
            "lime_stained_brick_tiles_wall",
            () -> new BlockItem(ModBlocks.LIME_STAINED_BRICK_TILES_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIME_STAINED_BRICK_TILES_VERTICAL_SLAB = ITEMS.register(
            "lime_stained_brick_tiles_vertical_slab",
            () -> new BlockItem(ModBlocks.LIME_STAINED_BRICK_TILES_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> GREEN_STAINED_BRICK_TILES_SLAB = ITEMS.register(
            "green_stained_brick_tiles_slab",
            () -> new BlockItem(ModBlocks.GREEN_STAINED_BRICK_TILES_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> GREEN_STAINED_BRICK_TILES_STAIRS = ITEMS.register(
            "green_stained_brick_tiles_stairs",
            () -> new BlockItem(ModBlocks.GREEN_STAINED_BRICK_TILES_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> GREEN_STAINED_BRICK_TILES_WALL = ITEMS.register(
            "green_stained_brick_tiles_wall",
            () -> new BlockItem(ModBlocks.GREEN_STAINED_BRICK_TILES_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> GREEN_STAINED_BRICK_TILES_VERTICAL_SLAB = ITEMS.register(
            "green_stained_brick_tiles_vertical_slab",
            () -> new BlockItem(ModBlocks.GREEN_STAINED_BRICK_TILES_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> CYAN_STAINED_BRICK_TILES_SLAB = ITEMS.register(
            "cyan_stained_brick_tiles_slab",
            () -> new BlockItem(ModBlocks.CYAN_STAINED_BRICK_TILES_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> CYAN_STAINED_BRICK_TILES_STAIRS = ITEMS.register(
            "cyan_stained_brick_tiles_stairs",
            () -> new BlockItem(ModBlocks.CYAN_STAINED_BRICK_TILES_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> CYAN_STAINED_BRICK_TILES_WALL = ITEMS.register(
            "cyan_stained_brick_tiles_wall",
            () -> new BlockItem(ModBlocks.CYAN_STAINED_BRICK_TILES_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> CYAN_STAINED_BRICK_TILES_VERTICAL_SLAB = ITEMS.register(
            "cyan_stained_brick_tiles_vertical_slab",
            () -> new BlockItem(ModBlocks.CYAN_STAINED_BRICK_TILES_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICK_TILES_SLAB = ITEMS.register(
            "light_blue_stained_brick_tiles_slab",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_STAINED_BRICK_TILES_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICK_TILES_STAIRS = ITEMS.register(
            "light_blue_stained_brick_tiles_stairs",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_STAINED_BRICK_TILES_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICK_TILES_WALL = ITEMS.register(
            "light_blue_stained_brick_tiles_wall",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_STAINED_BRICK_TILES_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICK_TILES_VERTICAL_SLAB = ITEMS.register(
            "light_blue_stained_brick_tiles_vertical_slab",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_STAINED_BRICK_TILES_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BLUE_STAINED_BRICK_TILES_SLAB = ITEMS.register(
            "blue_stained_brick_tiles_slab",
            () -> new BlockItem(ModBlocks.BLUE_STAINED_BRICK_TILES_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BLUE_STAINED_BRICK_TILES_STAIRS = ITEMS.register(
            "blue_stained_brick_tiles_stairs",
            () -> new BlockItem(ModBlocks.BLUE_STAINED_BRICK_TILES_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BLUE_STAINED_BRICK_TILES_WALL = ITEMS.register(
            "blue_stained_brick_tiles_wall",
            () -> new BlockItem(ModBlocks.BLUE_STAINED_BRICK_TILES_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BLUE_STAINED_BRICK_TILES_VERTICAL_SLAB = ITEMS.register(
            "blue_stained_brick_tiles_vertical_slab",
            () -> new BlockItem(ModBlocks.BLUE_STAINED_BRICK_TILES_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PURPLE_STAINED_BRICK_TILES_SLAB = ITEMS.register(
            "purple_stained_brick_tiles_slab",
            () -> new BlockItem(ModBlocks.PURPLE_STAINED_BRICK_TILES_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PURPLE_STAINED_BRICK_TILES_STAIRS = ITEMS.register(
            "purple_stained_brick_tiles_stairs",
            () -> new BlockItem(ModBlocks.PURPLE_STAINED_BRICK_TILES_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PURPLE_STAINED_BRICK_TILES_WALL = ITEMS.register(
            "purple_stained_brick_tiles_wall",
            () -> new BlockItem(ModBlocks.PURPLE_STAINED_BRICK_TILES_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PURPLE_STAINED_BRICK_TILES_VERTICAL_SLAB = ITEMS.register(
            "purple_stained_brick_tiles_vertical_slab",
            () -> new BlockItem(ModBlocks.PURPLE_STAINED_BRICK_TILES_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICK_TILES_SLAB = ITEMS.register(
            "magenta_stained_brick_tiles_slab",
            () -> new BlockItem(ModBlocks.MAGENTA_STAINED_BRICK_TILES_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICK_TILES_STAIRS = ITEMS.register(
            "magenta_stained_brick_tiles_stairs",
            () -> new BlockItem(ModBlocks.MAGENTA_STAINED_BRICK_TILES_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICK_TILES_WALL = ITEMS.register(
            "magenta_stained_brick_tiles_wall",
            () -> new BlockItem(ModBlocks.MAGENTA_STAINED_BRICK_TILES_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICK_TILES_VERTICAL_SLAB = ITEMS.register(
            "magenta_stained_brick_tiles_vertical_slab",
            () -> new BlockItem(ModBlocks.MAGENTA_STAINED_BRICK_TILES_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PINK_STAINED_BRICK_TILES_SLAB = ITEMS.register(
            "pink_stained_brick_tiles_slab",
            () -> new BlockItem(ModBlocks.PINK_STAINED_BRICK_TILES_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PINK_STAINED_BRICK_TILES_STAIRS = ITEMS.register(
            "pink_stained_brick_tiles_stairs",
            () -> new BlockItem(ModBlocks.PINK_STAINED_BRICK_TILES_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PINK_STAINED_BRICK_TILES_WALL = ITEMS.register(
            "pink_stained_brick_tiles_wall",
            () -> new BlockItem(ModBlocks.PINK_STAINED_BRICK_TILES_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PINK_STAINED_BRICK_TILES_VERTICAL_SLAB = ITEMS.register(
            "pink_stained_brick_tiles_vertical_slab",
            () -> new BlockItem(ModBlocks.PINK_STAINED_BRICK_TILES_VERTICAL_SLAB.get(), createBlockItemProperties())
    );


    public static final RegistryObject<Item> WHITE_STAINED_BRICKS_SLAB = ITEMS.register(
            "white_stained_bricks_slab",
            () -> new BlockItem(ModBlocks.WHITE_STAINED_BRICKS_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICKS_SLAB = ITEMS.register(
            "light_gray_stained_bricks_slab",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_STAINED_BRICKS_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> GRAY_STAINED_BRICKS_SLAB = ITEMS.register(
            "gray_stained_bricks_slab",
            () -> new BlockItem(ModBlocks.GRAY_STAINED_BRICKS_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BLACK_STAINED_BRICKS_SLAB = ITEMS.register(
            "black_stained_bricks_slab",
            () -> new BlockItem(ModBlocks.BLACK_STAINED_BRICKS_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BROWN_STAINED_BRICKS_SLAB = ITEMS.register(
            "brown_stained_bricks_slab",
            () -> new BlockItem(ModBlocks.BROWN_STAINED_BRICKS_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> RED_STAINED_BRICKS_SLAB = ITEMS.register(
            "red_stained_bricks_slab",
            () -> new BlockItem(ModBlocks.RED_STAINED_BRICKS_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> ORANGE_STAINED_BRICKS_SLAB = ITEMS.register(
            "orange_stained_bricks_slab",
            () -> new BlockItem(ModBlocks.ORANGE_STAINED_BRICKS_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> YELLOW_STAINED_BRICKS_SLAB = ITEMS.register(
            "yellow_stained_bricks_slab",
            () -> new BlockItem(ModBlocks.YELLOW_STAINED_BRICKS_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> LIME_STAINED_BRICKS_SLAB = ITEMS.register(
            "lime_stained_bricks_slab",
            () -> new BlockItem(ModBlocks.LIME_STAINED_BRICKS_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> GREEN_STAINED_BRICKS_SLAB = ITEMS.register(
            "green_stained_bricks_slab",
            () -> new BlockItem(ModBlocks.GREEN_STAINED_BRICKS_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CYAN_STAINED_BRICKS_SLAB = ITEMS.register(
            "cyan_stained_bricks_slab",
            () -> new BlockItem(ModBlocks.CYAN_STAINED_BRICKS_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICKS_SLAB = ITEMS.register(
            "light_blue_stained_bricks_slab",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_STAINED_BRICKS_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BLUE_STAINED_BRICKS_SLAB = ITEMS.register(
            "blue_stained_bricks_slab",
            () -> new BlockItem(ModBlocks.BLUE_STAINED_BRICKS_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> PURPLE_STAINED_BRICKS_SLAB = ITEMS.register(
            "purple_stained_bricks_slab",
            () -> new BlockItem(ModBlocks.PURPLE_STAINED_BRICKS_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> MAGENTA_STAINED_BRICKS_SLAB = ITEMS.register(
            "magenta_stained_bricks_slab",
            () -> new BlockItem(ModBlocks.MAGENTA_STAINED_BRICKS_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> PINK_STAINED_BRICKS_SLAB = ITEMS.register(
            "pink_stained_bricks_slab",
            () -> new BlockItem(ModBlocks.PINK_STAINED_BRICKS_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> WHITE_STAINED_BRICKS_STAIRS = ITEMS.register(
            "white_stained_bricks_stairs",
            () -> new BlockItem(ModBlocks.WHITE_STAINED_BRICKS_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICKS_STAIRS = ITEMS.register(
            "light_gray_stained_bricks_stairs",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_STAINED_BRICKS_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> GRAY_STAINED_BRICKS_STAIRS = ITEMS.register(
            "gray_stained_bricks_stairs",
            () -> new BlockItem(ModBlocks.GRAY_STAINED_BRICKS_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BLACK_STAINED_BRICKS_STAIRS = ITEMS.register(
            "black_stained_bricks_stairs",
            () -> new BlockItem(ModBlocks.BLACK_STAINED_BRICKS_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BROWN_STAINED_BRICKS_STAIRS = ITEMS.register(
            "brown_stained_bricks_stairs",
            () -> new BlockItem(ModBlocks.BROWN_STAINED_BRICKS_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> RED_STAINED_BRICKS_STAIRS = ITEMS.register(
            "red_stained_bricks_stairs",
            () -> new BlockItem(ModBlocks.RED_STAINED_BRICKS_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> ORANGE_STAINED_BRICKS_STAIRS = ITEMS.register(
            "orange_stained_bricks_stairs",
            () -> new BlockItem(ModBlocks.ORANGE_STAINED_BRICKS_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> YELLOW_STAINED_BRICKS_STAIRS = ITEMS.register(
            "yellow_stained_bricks_stairs",
            () -> new BlockItem(ModBlocks.YELLOW_STAINED_BRICKS_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> LIME_STAINED_BRICKS_STAIRS = ITEMS.register(
            "lime_stained_bricks_stairs",
            () -> new BlockItem(ModBlocks.LIME_STAINED_BRICKS_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> GREEN_STAINED_BRICKS_STAIRS = ITEMS.register(
            "green_stained_bricks_stairs",
            () -> new BlockItem(ModBlocks.GREEN_STAINED_BRICKS_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CYAN_STAINED_BRICKS_STAIRS = ITEMS.register(
            "cyan_stained_bricks_stairs",
            () -> new BlockItem(ModBlocks.CYAN_STAINED_BRICKS_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICKS_STAIRS = ITEMS.register(
            "light_blue_stained_bricks_stairs",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_STAINED_BRICKS_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BLUE_STAINED_BRICKS_STAIRS = ITEMS.register(
            "blue_stained_bricks_stairs",
            () -> new BlockItem(ModBlocks.BLUE_STAINED_BRICKS_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> PURPLE_STAINED_BRICKS_STAIRS = ITEMS.register(
            "purple_stained_bricks_stairs",
            () -> new BlockItem(ModBlocks.PURPLE_STAINED_BRICKS_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> MAGENTA_STAINED_BRICKS_STAIRS = ITEMS.register(
            "magenta_stained_bricks_stairs",
            () -> new BlockItem(ModBlocks.MAGENTA_STAINED_BRICKS_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> PINK_STAINED_BRICKS_STAIRS = ITEMS.register(
            "pink_stained_bricks_stairs",
            () -> new BlockItem(ModBlocks.PINK_STAINED_BRICKS_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> WHITE_STAINED_BRICKS_WALL = ITEMS.register(
            "white_stained_bricks_wall",
            () -> new BlockItem(ModBlocks.WHITE_STAINED_BRICKS_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICKS_WALL = ITEMS.register(
            "light_gray_stained_bricks_wall",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_STAINED_BRICKS_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> GRAY_STAINED_BRICKS_WALL = ITEMS.register(
            "gray_stained_bricks_wall",
            () -> new BlockItem(ModBlocks.GRAY_STAINED_BRICKS_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BLACK_STAINED_BRICKS_WALL = ITEMS.register(
            "black_stained_bricks_wall",
            () -> new BlockItem(ModBlocks.BLACK_STAINED_BRICKS_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BROWN_STAINED_BRICKS_WALL = ITEMS.register(
            "brown_stained_bricks_wall",
            () -> new BlockItem(ModBlocks.BROWN_STAINED_BRICKS_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> RED_STAINED_BRICKS_WALL = ITEMS.register(
            "red_stained_bricks_wall",
            () -> new BlockItem(ModBlocks.RED_STAINED_BRICKS_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> ORANGE_STAINED_BRICKS_WALL = ITEMS.register(
            "orange_stained_bricks_wall",
            () -> new BlockItem(ModBlocks.ORANGE_STAINED_BRICKS_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> YELLOW_STAINED_BRICKS_WALL = ITEMS.register(
            "yellow_stained_bricks_wall",
            () -> new BlockItem(ModBlocks.YELLOW_STAINED_BRICKS_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> LIME_STAINED_BRICKS_WALL = ITEMS.register(
            "lime_stained_bricks_wall",
            () -> new BlockItem(ModBlocks.LIME_STAINED_BRICKS_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> GREEN_STAINED_BRICKS_WALL = ITEMS.register(
            "green_stained_bricks_wall",
            () -> new BlockItem(ModBlocks.GREEN_STAINED_BRICKS_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CYAN_STAINED_BRICKS_WALL = ITEMS.register(
            "cyan_stained_bricks_wall",
            () -> new BlockItem(ModBlocks.CYAN_STAINED_BRICKS_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICKS_WALL = ITEMS.register(
            "light_blue_stained_bricks_wall",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_STAINED_BRICKS_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BLUE_STAINED_BRICKS_WALL = ITEMS.register(
            "blue_stained_bricks_wall",
            () -> new BlockItem(ModBlocks.BLUE_STAINED_BRICKS_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> PURPLE_STAINED_BRICKS_WALL = ITEMS.register(
            "purple_stained_bricks_wall",
            () -> new BlockItem(ModBlocks.PURPLE_STAINED_BRICKS_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> MAGENTA_STAINED_BRICKS_WALL = ITEMS.register(
            "magenta_stained_bricks_wall",
            () -> new BlockItem(ModBlocks.MAGENTA_STAINED_BRICKS_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> PINK_STAINED_BRICKS_WALL = ITEMS.register(
            "pink_stained_bricks_wall",
            () -> new BlockItem(ModBlocks.PINK_STAINED_BRICKS_WALL.get(), createBlockItemProperties())
    );

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

    public static final RegistryObject<Item> SMOOTH_STONE_STAIRS = ITEMS.register(
            "smooth_stone_stairs",
            () ->
                    new BlockItem(ModBlocks.SMOOTH_STONE_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_WHITE_CONCRETE = ITEMS.register(
            "polished_white_concrete",
            () -> new BlockItem(ModBlocks.POLISHED_WHITE_CONCRETE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_ORANGE_CONCRETE = ITEMS.register(
            "polished_orange_concrete",
            () -> new BlockItem(ModBlocks.POLISHED_ORANGE_CONCRETE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_MAGENTA_CONCRETE = ITEMS.register(
            "polished_magenta_concrete",
            () -> new BlockItem(ModBlocks.POLISHED_MAGENTA_CONCRETE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_LIGHT_BLUE_CONCRETE = ITEMS.register(
            "polished_light_blue_concrete",
            () -> new BlockItem(ModBlocks.POLISHED_LIGHT_BLUE_CONCRETE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_YELLOW_CONCRETE = ITEMS.register(
            "polished_yellow_concrete",
            () -> new BlockItem(ModBlocks.POLISHED_YELLOW_CONCRETE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_LIME_CONCRETE = ITEMS.register(
            "polished_lime_concrete",
            () -> new BlockItem(ModBlocks.POLISHED_LIME_CONCRETE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_PINK_CONCRETE = ITEMS.register(
            "polished_pink_concrete",
            () -> new BlockItem(ModBlocks.POLISHED_PINK_CONCRETE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_GRAY_CONCRETE = ITEMS.register(
            "polished_gray_concrete",
            () -> new BlockItem(ModBlocks.POLISHED_GRAY_CONCRETE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_LIGHT_GRAY_CONCRETE = ITEMS.register(
            "polished_light_gray_concrete",
            () -> new BlockItem(ModBlocks.POLISHED_LIGHT_GRAY_CONCRETE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_CYAN_CONCRETE = ITEMS.register(
            "polished_cyan_concrete",
            () -> new BlockItem(ModBlocks.POLISHED_CYAN_CONCRETE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_PURPLE_CONCRETE = ITEMS.register(
            "polished_purple_concrete",
            () -> new BlockItem(ModBlocks.POLISHED_PURPLE_CONCRETE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_BLUE_CONCRETE = ITEMS.register(
            "polished_blue_concrete",
            () -> new BlockItem(ModBlocks.POLISHED_BLUE_CONCRETE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_BROWN_CONCRETE = ITEMS.register(
            "polished_brown_concrete",
            () -> new BlockItem(ModBlocks.POLISHED_BROWN_CONCRETE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_GREEN_CONCRETE = ITEMS.register(
            "polished_green_concrete",
            () -> new BlockItem(ModBlocks.POLISHED_GREEN_CONCRETE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_RED_CONCRETE = ITEMS.register(
            "polished_red_concrete",
            () -> new BlockItem(ModBlocks.POLISHED_RED_CONCRETE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_BLACK_CONCRETE = ITEMS.register(
            "polished_black_concrete",
            () -> new BlockItem(ModBlocks.POLISHED_BLACK_CONCRETE.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_WHITE_CONCRETE_STAIRS = ITEMS.register(
            "polished_white_concrete_stairs",
            () -> new BlockItem(ModBlocks.POLISHED_WHITE_CONCRETE_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_WHITE_CONCRETE_SLAB = ITEMS.register(
            "polished_white_concrete_slab",
            () -> new BlockItem(ModBlocks.POLISHED_WHITE_CONCRETE_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_WHITE_CONCRETE_WALL = ITEMS.register(
            "polished_white_concrete_wall",
            () -> new BlockItem(ModBlocks.POLISHED_WHITE_CONCRETE_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_ORANGE_CONCRETE_STAIRS = ITEMS.register(
            "polished_orange_concrete_stairs",
            () -> new BlockItem(ModBlocks.POLISHED_ORANGE_CONCRETE_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_ORANGE_CONCRETE_SLAB = ITEMS.register(
            "polished_orange_concrete_slab",
            () -> new BlockItem(ModBlocks.POLISHED_ORANGE_CONCRETE_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_ORANGE_CONCRETE_WALL = ITEMS.register(
            "polished_orange_concrete_wall",
            () -> new BlockItem(ModBlocks.POLISHED_ORANGE_CONCRETE_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_MAGENTA_CONCRETE_STAIRS = ITEMS.register(
            "polished_magenta_concrete_stairs",
            () -> new BlockItem(ModBlocks.POLISHED_MAGENTA_CONCRETE_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_MAGENTA_CONCRETE_SLAB = ITEMS.register(
            "polished_magenta_concrete_slab",
            () -> new BlockItem(ModBlocks.POLISHED_MAGENTA_CONCRETE_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_MAGENTA_CONCRETE_WALL = ITEMS.register(
            "polished_magenta_concrete_wall",
            () -> new BlockItem(ModBlocks.POLISHED_MAGENTA_CONCRETE_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_LIGHT_BLUE_CONCRETE_STAIRS = ITEMS.register(
            "polished_light_blue_concrete_stairs",
            () -> new BlockItem(ModBlocks.POLISHED_LIGHT_BLUE_CONCRETE_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_LIGHT_BLUE_CONCRETE_SLAB = ITEMS.register(
            "polished_light_blue_concrete_slab",
            () -> new BlockItem(ModBlocks.POLISHED_LIGHT_BLUE_CONCRETE_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_LIGHT_BLUE_CONCRETE_WALL = ITEMS.register(
            "polished_light_blue_concrete_wall",
            () -> new BlockItem(ModBlocks.POLISHED_LIGHT_BLUE_CONCRETE_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_YELLOW_CONCRETE_STAIRS = ITEMS.register(
            "polished_yellow_concrete_stairs",
            () -> new BlockItem(ModBlocks.POLISHED_YELLOW_CONCRETE_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_YELLOW_CONCRETE_SLAB = ITEMS.register(
            "polished_yellow_concrete_slab",
            () -> new BlockItem(ModBlocks.POLISHED_YELLOW_CONCRETE_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_YELLOW_CONCRETE_WALL = ITEMS.register(
            "polished_yellow_concrete_wall",
            () -> new BlockItem(ModBlocks.POLISHED_YELLOW_CONCRETE_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_LIME_CONCRETE_STAIRS = ITEMS.register(
            "polished_lime_concrete_stairs",
            () -> new BlockItem(ModBlocks.POLISHED_LIME_CONCRETE_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_LIME_CONCRETE_SLAB = ITEMS.register(
            "polished_lime_concrete_slab",
            () -> new BlockItem(ModBlocks.POLISHED_LIME_CONCRETE_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_LIME_CONCRETE_WALL = ITEMS.register(
            "polished_lime_concrete_wall",
            () -> new BlockItem(ModBlocks.POLISHED_LIME_CONCRETE_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_PINK_CONCRETE_STAIRS = ITEMS.register(
            "polished_pink_concrete_stairs",
            () -> new BlockItem(ModBlocks.POLISHED_PINK_CONCRETE_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_PINK_CONCRETE_SLAB = ITEMS.register(
            "polished_pink_concrete_slab",
            () -> new BlockItem(ModBlocks.POLISHED_PINK_CONCRETE_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_PINK_CONCRETE_WALL = ITEMS.register(
            "polished_pink_concrete_wall",
            () -> new BlockItem(ModBlocks.POLISHED_PINK_CONCRETE_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_GRAY_CONCRETE_STAIRS = ITEMS.register(
            "polished_gray_concrete_stairs",
            () -> new BlockItem(ModBlocks.POLISHED_GRAY_CONCRETE_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_GRAY_CONCRETE_SLAB = ITEMS.register(
            "polished_gray_concrete_slab",
            () -> new BlockItem(ModBlocks.POLISHED_GRAY_CONCRETE_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_GRAY_CONCRETE_WALL = ITEMS.register(
            "polished_gray_concrete_wall",
            () -> new BlockItem(ModBlocks.POLISHED_GRAY_CONCRETE_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_LIGHT_GRAY_CONCRETE_STAIRS = ITEMS.register(
            "polished_light_gray_concrete_stairs",
            () -> new BlockItem(ModBlocks.POLISHED_LIGHT_GRAY_CONCRETE_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_LIGHT_GRAY_CONCRETE_SLAB = ITEMS.register(
            "polished_light_gray_concrete_slab",
            () -> new BlockItem(ModBlocks.POLISHED_LIGHT_GRAY_CONCRETE_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_LIGHT_GRAY_CONCRETE_WALL = ITEMS.register(
            "polished_light_gray_concrete_wall",
            () -> new BlockItem(ModBlocks.POLISHED_LIGHT_GRAY_CONCRETE_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_CYAN_CONCRETE_STAIRS = ITEMS.register(
            "polished_cyan_concrete_stairs",
            () -> new BlockItem(ModBlocks.POLISHED_CYAN_CONCRETE_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_CYAN_CONCRETE_SLAB = ITEMS.register(
            "polished_cyan_concrete_slab",
            () -> new BlockItem(ModBlocks.POLISHED_CYAN_CONCRETE_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_CYAN_CONCRETE_WALL = ITEMS.register(
            "polished_cyan_concrete_wall",
            () -> new BlockItem(ModBlocks.POLISHED_CYAN_CONCRETE_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_PURPLE_CONCRETE_STAIRS = ITEMS.register(
            "polished_purple_concrete_stairs",
            () -> new BlockItem(ModBlocks.POLISHED_PURPLE_CONCRETE_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_PURPLE_CONCRETE_SLAB = ITEMS.register(
            "polished_purple_concrete_slab",
            () -> new BlockItem(ModBlocks.POLISHED_PURPLE_CONCRETE_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_PURPLE_CONCRETE_WALL = ITEMS.register(
            "polished_purple_concrete_wall",
            () -> new BlockItem(ModBlocks.POLISHED_PURPLE_CONCRETE_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_BLUE_CONCRETE_STAIRS = ITEMS.register(
            "polished_blue_concrete_stairs",
            () -> new BlockItem(ModBlocks.POLISHED_BLUE_CONCRETE_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_BLUE_CONCRETE_SLAB = ITEMS.register(
            "polished_blue_concrete_slab",
            () -> new BlockItem(ModBlocks.POLISHED_BLUE_CONCRETE_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_BLUE_CONCRETE_WALL = ITEMS.register(
            "polished_blue_concrete_wall",
            () -> new BlockItem(ModBlocks.POLISHED_BLUE_CONCRETE_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_BROWN_CONCRETE_STAIRS = ITEMS.register(
            "polished_brown_concrete_stairs",
            () -> new BlockItem(ModBlocks.POLISHED_BROWN_CONCRETE_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_BROWN_CONCRETE_SLAB = ITEMS.register(
            "polished_brown_concrete_slab",
            () -> new BlockItem(ModBlocks.POLISHED_BROWN_CONCRETE_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_BROWN_CONCRETE_WALL = ITEMS.register(
            "polished_brown_concrete_wall",
            () -> new BlockItem(ModBlocks.POLISHED_BROWN_CONCRETE_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_GREEN_CONCRETE_STAIRS = ITEMS.register(
            "polished_green_concrete_stairs",
            () -> new BlockItem(ModBlocks.POLISHED_GREEN_CONCRETE_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_GREEN_CONCRETE_SLAB = ITEMS.register(
            "polished_green_concrete_slab",
            () -> new BlockItem(ModBlocks.POLISHED_GREEN_CONCRETE_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_GREEN_CONCRETE_WALL = ITEMS.register(
            "polished_green_concrete_wall",
            () -> new BlockItem(ModBlocks.POLISHED_GREEN_CONCRETE_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_RED_CONCRETE_STAIRS = ITEMS.register(
            "polished_red_concrete_stairs",
            () -> new BlockItem(ModBlocks.POLISHED_RED_CONCRETE_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_RED_CONCRETE_SLAB = ITEMS.register(
            "polished_red_concrete_slab",
            () -> new BlockItem(ModBlocks.POLISHED_RED_CONCRETE_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_RED_CONCRETE_WALL = ITEMS.register(
            "polished_red_concrete_wall",
            () -> new BlockItem(ModBlocks.POLISHED_RED_CONCRETE_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_BLACK_CONCRETE_STAIRS = ITEMS.register(
            "polished_black_concrete_stairs",
            () -> new BlockItem(ModBlocks.POLISHED_BLACK_CONCRETE_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_BLACK_CONCRETE_SLAB = ITEMS.register(
            "polished_black_concrete_slab",
            () -> new BlockItem(ModBlocks.POLISHED_BLACK_CONCRETE_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> POLISHED_BLACK_CONCRETE_WALL = ITEMS.register(
            "polished_black_concrete_wall",
            () -> new BlockItem(ModBlocks.POLISHED_BLACK_CONCRETE_WALL.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> SMOKE_VENT = ITEMS.register(
            "smoke_vent",
            () -> new BlockItem(ModBlocks.SMOKE_VENT.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> WHITE_REDSTONE_LAMP = ITEMS.register(
            "white_redstone_lamp",
            () -> new BlockItem(ModBlocks.WHITE_REDSTONE_LAMP.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> ORANGE_REDSTONE_LAMP = ITEMS.register(
            "orange_redstone_lamp",
            () -> new BlockItem(ModBlocks.ORANGE_REDSTONE_LAMP.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> MAGENTA_REDSTONE_LAMP = ITEMS.register(
            "magenta_redstone_lamp",
            () -> new BlockItem(ModBlocks.MAGENTA_REDSTONE_LAMP.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> LIGHT_BLUE_REDSTONE_LAMP = ITEMS.register(
            "light_blue_redstone_lamp",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_REDSTONE_LAMP.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> YELLOW_REDSTONE_LAMP = ITEMS.register(
            "yellow_redstone_lamp",
            () -> new BlockItem(ModBlocks.YELLOW_REDSTONE_LAMP.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> LIME_REDSTONE_LAMP = ITEMS.register(
            "lime_redstone_lamp",
            () -> new BlockItem(ModBlocks.LIME_REDSTONE_LAMP.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> PINK_REDSTONE_LAMP = ITEMS.register(
            "pink_redstone_lamp",
            () -> new BlockItem(ModBlocks.PINK_REDSTONE_LAMP.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> GRAY_REDSTONE_LAMP = ITEMS.register(
            "gray_redstone_lamp",
            () -> new BlockItem(ModBlocks.GRAY_REDSTONE_LAMP.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> LIGHT_GRAY_REDSTONE_LAMP = ITEMS.register(
            "light_gray_redstone_lamp",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_REDSTONE_LAMP.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CYAN_REDSTONE_LAMP = ITEMS.register(
            "cyan_redstone_lamp",
            () -> new BlockItem(ModBlocks.CYAN_REDSTONE_LAMP.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> PURPLE_REDSTONE_LAMP = ITEMS.register(
            "purple_redstone_lamp",
            () -> new BlockItem(ModBlocks.PURPLE_REDSTONE_LAMP.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BLUE_REDSTONE_LAMP = ITEMS.register(
            "blue_redstone_lamp",
            () -> new BlockItem(ModBlocks.BLUE_REDSTONE_LAMP.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BROWN_REDSTONE_LAMP = ITEMS.register(
            "brown_redstone_lamp",
            () -> new BlockItem(ModBlocks.BROWN_REDSTONE_LAMP.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> GREEN_REDSTONE_LAMP = ITEMS.register(
            "green_redstone_lamp",
            () -> new BlockItem(ModBlocks.GREEN_REDSTONE_LAMP.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> RED_REDSTONE_LAMP = ITEMS.register(
            "red_redstone_lamp",
            () -> new BlockItem(ModBlocks.RED_REDSTONE_LAMP.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BLACK_REDSTONE_LAMP = ITEMS.register(
            "black_redstone_lamp",
            () -> new BlockItem(ModBlocks.BLACK_REDSTONE_LAMP.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CASCADE_BLOCK = ITEMS.register(
            "cascade_block",
            () -> new MistBlockItem(ModBlocks.CASCADE_BLOCK.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> CASCADE_BLOCK_NO_MIST = ITEMS.register(
            "cascade_block_no_mist",
            () -> new MistBlockItem(ModBlocks.CASCADE_BLOCK_NO_MIST.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> BOTTLE_OF_MIST = ITEMS.register(
            "bottle_of_mist",
            () -> new BottleOfMistItem(new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(64))
    );

    public static final RegistryObject<Item> BLACK_FACTORY_MESH = ITEMS.register("black_factory_mesh",
            () -> new BlockItem(ModBlocks.BLACK_FACTORY_MESH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_FACTORY_MESH_STAIRS = ITEMS.register("black_factory_mesh_stairs",
            () -> new BlockItem(ModBlocks.BLACK_FACTORY_MESH_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_FACTORY_MESH_SLAB = ITEMS.register("black_factory_mesh_slab",
            () -> new BlockItem(ModBlocks.BLACK_FACTORY_MESH_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_FACTORY_MESH_WALL = ITEMS.register("black_factory_mesh_wall",
            () -> new BlockItem(ModBlocks.BLACK_FACTORY_MESH_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_FACTORY_MESH = ITEMS.register("blue_factory_mesh",
            () -> new BlockItem(ModBlocks.BLUE_FACTORY_MESH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_FACTORY_MESH_STAIRS = ITEMS.register("blue_factory_mesh_stairs",
            () -> new BlockItem(ModBlocks.BLUE_FACTORY_MESH_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_FACTORY_MESH_SLAB = ITEMS.register("blue_factory_mesh_slab",
            () -> new BlockItem(ModBlocks.BLUE_FACTORY_MESH_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_FACTORY_MESH_WALL = ITEMS.register("blue_factory_mesh_wall",
            () -> new BlockItem(ModBlocks.BLUE_FACTORY_MESH_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_FACTORY_MESH = ITEMS.register("brown_factory_mesh",
            () -> new BlockItem(ModBlocks.BROWN_FACTORY_MESH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_FACTORY_MESH_STAIRS = ITEMS.register("brown_factory_mesh_stairs",
            () -> new BlockItem(ModBlocks.BROWN_FACTORY_MESH_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_FACTORY_MESH_SLAB = ITEMS.register("brown_factory_mesh_slab",
            () -> new BlockItem(ModBlocks.BROWN_FACTORY_MESH_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_FACTORY_MESH_WALL = ITEMS.register("brown_factory_mesh_wall",
            () -> new BlockItem(ModBlocks.BROWN_FACTORY_MESH_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_FACTORY_MESH = ITEMS.register("gray_factory_mesh",
            () -> new BlockItem(ModBlocks.GRAY_FACTORY_MESH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_FACTORY_MESH_STAIRS = ITEMS.register("gray_factory_mesh_stairs",
            () -> new BlockItem(ModBlocks.GRAY_FACTORY_MESH_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_FACTORY_MESH_SLAB = ITEMS.register("gray_factory_mesh_slab",
            () -> new BlockItem(ModBlocks.GRAY_FACTORY_MESH_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_FACTORY_MESH_WALL = ITEMS.register("gray_factory_mesh_wall",
            () -> new BlockItem(ModBlocks.GRAY_FACTORY_MESH_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_FACTORY_MESH = ITEMS.register("green_factory_mesh",
            () -> new BlockItem(ModBlocks.GREEN_FACTORY_MESH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_FACTORY_MESH_STAIRS = ITEMS.register("green_factory_mesh_stairs",
            () -> new BlockItem(ModBlocks.GREEN_FACTORY_MESH_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_FACTORY_MESH_SLAB = ITEMS.register("green_factory_mesh_slab",
            () -> new BlockItem(ModBlocks.GREEN_FACTORY_MESH_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_FACTORY_MESH_WALL = ITEMS.register("green_factory_mesh_wall",
            () -> new BlockItem(ModBlocks.GREEN_FACTORY_MESH_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_FACTORY_MESH = ITEMS.register("light_gray_factory_mesh",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_FACTORY_MESH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_FACTORY_MESH_STAIRS = ITEMS.register("light_gray_factory_mesh_stairs",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_FACTORY_MESH_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_FACTORY_MESH_SLAB = ITEMS.register("light_gray_factory_mesh_slab",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_FACTORY_MESH_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_FACTORY_MESH_WALL = ITEMS.register("light_gray_factory_mesh_wall",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_FACTORY_MESH_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_FACTORY_MESH = ITEMS.register("lime_factory_mesh",
            () -> new BlockItem(ModBlocks.LIME_FACTORY_MESH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_FACTORY_MESH_STAIRS = ITEMS.register("lime_factory_mesh_stairs",
            () -> new BlockItem(ModBlocks.LIME_FACTORY_MESH_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_FACTORY_MESH_SLAB = ITEMS.register("lime_factory_mesh_slab",
            () -> new BlockItem(ModBlocks.LIME_FACTORY_MESH_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_FACTORY_MESH_WALL = ITEMS.register("lime_factory_mesh_wall",
            () -> new BlockItem(ModBlocks.LIME_FACTORY_MESH_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_FACTORY_MESH = ITEMS.register("orange_factory_mesh",
            () -> new BlockItem(ModBlocks.ORANGE_FACTORY_MESH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_FACTORY_MESH_STAIRS = ITEMS.register("orange_factory_mesh_stairs",
            () -> new BlockItem(ModBlocks.ORANGE_FACTORY_MESH_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_FACTORY_MESH_SLAB = ITEMS.register("orange_factory_mesh_slab",
            () -> new BlockItem(ModBlocks.ORANGE_FACTORY_MESH_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_FACTORY_MESH_WALL = ITEMS.register("orange_factory_mesh_wall",
            () -> new BlockItem(ModBlocks.ORANGE_FACTORY_MESH_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_FACTORY_MESH = ITEMS.register("red_factory_mesh",
            () -> new BlockItem(ModBlocks.RED_FACTORY_MESH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_FACTORY_MESH_STAIRS = ITEMS.register("red_factory_mesh_stairs",
            () -> new BlockItem(ModBlocks.RED_FACTORY_MESH_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_FACTORY_MESH_SLAB = ITEMS.register("red_factory_mesh_slab",
            () -> new BlockItem(ModBlocks.RED_FACTORY_MESH_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_FACTORY_MESH_WALL = ITEMS.register("red_factory_mesh_wall",
            () -> new BlockItem(ModBlocks.RED_FACTORY_MESH_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_FACTORY_MESH = ITEMS.register("yellow_factory_mesh",
            () -> new BlockItem(ModBlocks.YELLOW_FACTORY_MESH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_FACTORY_MESH_STAIRS = ITEMS.register("yellow_factory_mesh_stairs",
            () -> new BlockItem(ModBlocks.YELLOW_FACTORY_MESH_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_FACTORY_MESH_SLAB = ITEMS.register("yellow_factory_mesh_slab",
            () -> new BlockItem(ModBlocks.YELLOW_FACTORY_MESH_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_FACTORY_MESH_WALL = ITEMS.register("yellow_factory_mesh_wall",
            () -> new BlockItem(ModBlocks.YELLOW_FACTORY_MESH_WALL.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> PACKED_MUD = ITEMS.register("packed_mud",
            () -> new BlockItem(ModBlocks.PACKED_MUD.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PACKED_MUD_STAIRS = ITEMS.register("packed_mud_stairs",
            () -> new BlockItem(ModBlocks.PACKED_MUD_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PACKED_MUD_SLAB = ITEMS.register("packed_mud_slab",
            () -> new BlockItem(ModBlocks.PACKED_MUD_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PACKED_MUD_WALL = ITEMS.register("packed_mud_wall",
            () -> new BlockItem(ModBlocks.PACKED_MUD_WALL.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> MUD_BRICKS = ITEMS.register("mud_bricks",
            () -> new BlockItem(ModBlocks.MUD_BRICKS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MUD_BRICK_STAIRS = ITEMS.register("mud_brick_stairs",
            () -> new BlockItem(ModBlocks.MUD_BRICK_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MUD_BRICK_SLAB = ITEMS.register("mud_brick_slab",
            () -> new BlockItem(ModBlocks.MUD_BRICK_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MUD_BRICK_WALL = ITEMS.register("mud_brick_wall",
            () -> new BlockItem(ModBlocks.MUD_BRICK_WALL.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> ANCIENT_ASHEN_SCROLL = ITEMS.register("ancient_ashen_scroll",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).durability(28)) {
                @Override
                public boolean isEnchantable(net.minecraft.world.item.ItemStack stack) {
                    return false;
                }

                @Override
                public boolean isBookEnchantable(net.minecraft.world.item.ItemStack stack, net.minecraft.world.item.ItemStack book) {
                    return false;
                }

                @Override
                public void appendHoverText(net.minecraft.world.item.ItemStack stack, @javax.annotation.Nullable net.minecraft.world.level.Level level, java.util.List<net.minecraft.network.chat.Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
                    tooltip.add(new net.minecraft.network.chat.TextComponent("Found rarely in ")
                            .withStyle(net.minecraft.ChatFormatting.GRAY)
                            .append(new net.minecraft.network.chat.TextComponent("End Cities")
                                    .withStyle(net.minecraft.ChatFormatting.AQUA))
                            .append(new net.minecraft.network.chat.TextComponent(" & ")
                                    .withStyle(net.minecraft.ChatFormatting.GRAY))
                            .append(new net.minecraft.network.chat.TextComponent("Woodland Mansions")
                                    .withStyle(net.minecraft.ChatFormatting.AQUA))
                            .append(new net.minecraft.network.chat.TextComponent(", also if")
                                    .withStyle(net.minecraft.ChatFormatting.GRAY)));
                    tooltip.add(new net.minecraft.network.chat.TextComponent("you are lucky a ")
                            .withStyle(net.minecraft.ChatFormatting.GRAY)
                            .append(new net.minecraft.network.chat.TextComponent("Wandering Trader")
                                    .withStyle(net.minecraft.ChatFormatting.AQUA))
                            .append(new net.minecraft.network.chat.TextComponent(" might trade")
                                    .withStyle(net.minecraft.ChatFormatting.GRAY)));
                    tooltip.add(new net.minecraft.network.chat.TextComponent("it for a sweet price.")
                            .withStyle(net.minecraft.ChatFormatting.GRAY));
                }
            });



    public static final RegistryObject<Item> GLASS_SLAB = ITEMS.register(
            "glass_slab",
            () -> new BlockItem(ModBlocks.GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WHITE_STAINED_GLASS_SLAB = ITEMS.register(
            "white_stained_glass_slab",
            () -> new BlockItem(ModBlocks.WHITE_STAINED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_GLASS_SLAB = ITEMS.register(
            "light_gray_stained_glass_slab",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_STAINED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> GRAY_STAINED_GLASS_SLAB = ITEMS.register(
            "gray_stained_glass_slab",
            () -> new BlockItem(ModBlocks.GRAY_STAINED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BLACK_STAINED_GLASS_SLAB = ITEMS.register(
            "black_stained_glass_slab",
            () -> new BlockItem(ModBlocks.BLACK_STAINED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BROWN_STAINED_GLASS_SLAB = ITEMS.register(
            "brown_stained_glass_slab",
            () -> new BlockItem(ModBlocks.BROWN_STAINED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> RED_STAINED_GLASS_SLAB = ITEMS.register(
            "red_stained_glass_slab",
            () -> new BlockItem(ModBlocks.RED_STAINED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> ORANGE_STAINED_GLASS_SLAB = ITEMS.register(
            "orange_stained_glass_slab",
            () -> new BlockItem(ModBlocks.ORANGE_STAINED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> YELLOW_STAINED_GLASS_SLAB = ITEMS.register(
            "yellow_stained_glass_slab",
            () -> new BlockItem(ModBlocks.YELLOW_STAINED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIME_STAINED_GLASS_SLAB = ITEMS.register(
            "lime_stained_glass_slab",
            () -> new BlockItem(ModBlocks.LIME_STAINED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> GREEN_STAINED_GLASS_SLAB = ITEMS.register(
            "green_stained_glass_slab",
            () -> new BlockItem(ModBlocks.GREEN_STAINED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> CYAN_STAINED_GLASS_SLAB = ITEMS.register(
            "cyan_stained_glass_slab",
            () -> new BlockItem(ModBlocks.CYAN_STAINED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_GLASS_SLAB = ITEMS.register(
            "light_blue_stained_glass_slab",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_STAINED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BLUE_STAINED_GLASS_SLAB = ITEMS.register(
            "blue_stained_glass_slab",
            () -> new BlockItem(ModBlocks.BLUE_STAINED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PURPLE_STAINED_GLASS_SLAB = ITEMS.register(
            "purple_stained_glass_slab",
            () -> new BlockItem(ModBlocks.PURPLE_STAINED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> MAGENTA_STAINED_GLASS_SLAB = ITEMS.register(
            "magenta_stained_glass_slab",
            () -> new BlockItem(ModBlocks.MAGENTA_STAINED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PINK_STAINED_GLASS_SLAB = ITEMS.register(
            "pink_stained_glass_slab",
            () -> new BlockItem(ModBlocks.PINK_STAINED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> GLASS_STAIRS = ITEMS.register(
            "glass_stairs",
            () -> new BlockItem(ModBlocks.GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WHITE_STAINED_GLASS_STAIRS = ITEMS.register(
            "white_stained_glass_stairs",
            () -> new BlockItem(ModBlocks.WHITE_STAINED_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_GLASS_STAIRS = ITEMS.register(
            "light_gray_stained_glass_stairs",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_STAINED_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> GRAY_STAINED_GLASS_STAIRS = ITEMS.register(
            "gray_stained_glass_stairs",
            () -> new BlockItem(ModBlocks.GRAY_STAINED_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BLACK_STAINED_GLASS_STAIRS = ITEMS.register(
            "black_stained_glass_stairs",
            () -> new BlockItem(ModBlocks.BLACK_STAINED_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BROWN_STAINED_GLASS_STAIRS = ITEMS.register(
            "brown_stained_glass_stairs",
            () -> new BlockItem(ModBlocks.BROWN_STAINED_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> RED_STAINED_GLASS_STAIRS = ITEMS.register(
            "red_stained_glass_stairs",
            () -> new BlockItem(ModBlocks.RED_STAINED_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> ORANGE_STAINED_GLASS_STAIRS = ITEMS.register(
            "orange_stained_glass_stairs",
            () -> new BlockItem(ModBlocks.ORANGE_STAINED_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> YELLOW_STAINED_GLASS_STAIRS = ITEMS.register(
            "yellow_stained_glass_stairs",
            () -> new BlockItem(ModBlocks.YELLOW_STAINED_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIME_STAINED_GLASS_STAIRS = ITEMS.register(
            "lime_stained_glass_stairs",
            () -> new BlockItem(ModBlocks.LIME_STAINED_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> GREEN_STAINED_GLASS_STAIRS = ITEMS.register(
            "green_stained_glass_stairs",
            () -> new BlockItem(ModBlocks.GREEN_STAINED_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> CYAN_STAINED_GLASS_STAIRS = ITEMS.register(
            "cyan_stained_glass_stairs",
            () -> new BlockItem(ModBlocks.CYAN_STAINED_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_GLASS_STAIRS = ITEMS.register(
            "light_blue_stained_glass_stairs",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_STAINED_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BLUE_STAINED_GLASS_STAIRS = ITEMS.register(
            "blue_stained_glass_stairs",
            () -> new BlockItem(ModBlocks.BLUE_STAINED_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PURPLE_STAINED_GLASS_STAIRS = ITEMS.register(
            "purple_stained_glass_stairs",
            () -> new BlockItem(ModBlocks.PURPLE_STAINED_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> MAGENTA_STAINED_GLASS_STAIRS = ITEMS.register(
            "magenta_stained_glass_stairs",
            () -> new BlockItem(ModBlocks.MAGENTA_STAINED_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PINK_STAINED_GLASS_STAIRS = ITEMS.register(
            "pink_stained_glass_stairs",
            () -> new BlockItem(ModBlocks.PINK_STAINED_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WHITE_MOSAIC_GLASS_SLAB = ITEMS.register(
            "white_mosaic_glass_slab",
            () -> new BlockItem(ModBlocks.WHITE_MOSAIC_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WHITE_MOSAIC_GLASS_STAIRS = ITEMS.register(
            "white_mosaic_glass_stairs",
            () -> new BlockItem(ModBlocks.WHITE_MOSAIC_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIGHT_GRAY_MOSAIC_GLASS_SLAB = ITEMS.register(
            "light_gray_mosaic_glass_slab",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_MOSAIC_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIGHT_GRAY_MOSAIC_GLASS_STAIRS = ITEMS.register(
            "light_gray_mosaic_glass_stairs",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_MOSAIC_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> GRAY_MOSAIC_GLASS_SLAB = ITEMS.register(
            "gray_mosaic_glass_slab",
            () -> new BlockItem(ModBlocks.GRAY_MOSAIC_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> GRAY_MOSAIC_GLASS_STAIRS = ITEMS.register(
            "gray_mosaic_glass_stairs",
            () -> new BlockItem(ModBlocks.GRAY_MOSAIC_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BLACK_MOSAIC_GLASS_SLAB = ITEMS.register(
            "black_mosaic_glass_slab",
            () -> new BlockItem(ModBlocks.BLACK_MOSAIC_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BLACK_MOSAIC_GLASS_STAIRS = ITEMS.register(
            "black_mosaic_glass_stairs",
            () -> new BlockItem(ModBlocks.BLACK_MOSAIC_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BROWN_MOSAIC_GLASS_SLAB = ITEMS.register(
            "brown_mosaic_glass_slab",
            () -> new BlockItem(ModBlocks.BROWN_MOSAIC_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BROWN_MOSAIC_GLASS_STAIRS = ITEMS.register(
            "brown_mosaic_glass_stairs",
            () -> new BlockItem(ModBlocks.BROWN_MOSAIC_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> RED_MOSAIC_GLASS_SLAB = ITEMS.register(
            "red_mosaic_glass_slab",
            () -> new BlockItem(ModBlocks.RED_MOSAIC_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> RED_MOSAIC_GLASS_STAIRS = ITEMS.register(
            "red_mosaic_glass_stairs",
            () -> new BlockItem(ModBlocks.RED_MOSAIC_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> ORANGE_MOSAIC_GLASS_SLAB = ITEMS.register(
            "orange_mosaic_glass_slab",
            () -> new BlockItem(ModBlocks.ORANGE_MOSAIC_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> ORANGE_MOSAIC_GLASS_STAIRS = ITEMS.register(
            "orange_mosaic_glass_stairs",
            () -> new BlockItem(ModBlocks.ORANGE_MOSAIC_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> YELLOW_MOSAIC_GLASS_SLAB = ITEMS.register(
            "yellow_mosaic_glass_slab",
            () -> new BlockItem(ModBlocks.YELLOW_MOSAIC_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> YELLOW_MOSAIC_GLASS_STAIRS = ITEMS.register(
            "yellow_mosaic_glass_stairs",
            () -> new BlockItem(ModBlocks.YELLOW_MOSAIC_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIME_MOSAIC_GLASS_SLAB = ITEMS.register(
            "lime_mosaic_glass_slab",
            () -> new BlockItem(ModBlocks.LIME_MOSAIC_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIME_MOSAIC_GLASS_STAIRS = ITEMS.register(
            "lime_mosaic_glass_stairs",
            () -> new BlockItem(ModBlocks.LIME_MOSAIC_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> GREEN_MOSAIC_GLASS_SLAB = ITEMS.register(
            "green_mosaic_glass_slab",
            () -> new BlockItem(ModBlocks.GREEN_MOSAIC_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> GREEN_MOSAIC_GLASS_STAIRS = ITEMS.register(
            "green_mosaic_glass_stairs",
            () -> new BlockItem(ModBlocks.GREEN_MOSAIC_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> CYAN_MOSAIC_GLASS_SLAB = ITEMS.register(
            "cyan_mosaic_glass_slab",
            () -> new BlockItem(ModBlocks.CYAN_MOSAIC_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> CYAN_MOSAIC_GLASS_STAIRS = ITEMS.register(
            "cyan_mosaic_glass_stairs",
            () -> new BlockItem(ModBlocks.CYAN_MOSAIC_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIGHT_BLUE_MOSAIC_GLASS_SLAB = ITEMS.register(
            "light_blue_mosaic_glass_slab",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_MOSAIC_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIGHT_BLUE_MOSAIC_GLASS_STAIRS = ITEMS.register(
            "light_blue_mosaic_glass_stairs",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_MOSAIC_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BLUE_MOSAIC_GLASS_SLAB = ITEMS.register(
            "blue_mosaic_glass_slab",
            () -> new BlockItem(ModBlocks.BLUE_MOSAIC_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BLUE_MOSAIC_GLASS_STAIRS = ITEMS.register(
            "blue_mosaic_glass_stairs",
            () -> new BlockItem(ModBlocks.BLUE_MOSAIC_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PURPLE_MOSAIC_GLASS_SLAB = ITEMS.register(
            "purple_mosaic_glass_slab",
            () -> new BlockItem(ModBlocks.PURPLE_MOSAIC_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PURPLE_MOSAIC_GLASS_STAIRS = ITEMS.register(
            "purple_mosaic_glass_stairs",
            () -> new BlockItem(ModBlocks.PURPLE_MOSAIC_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> MAGENTA_MOSAIC_GLASS_SLAB = ITEMS.register(
            "magenta_mosaic_glass_slab",
            () -> new BlockItem(ModBlocks.MAGENTA_MOSAIC_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> MAGENTA_MOSAIC_GLASS_STAIRS = ITEMS.register(
            "magenta_mosaic_glass_stairs",
            () -> new BlockItem(ModBlocks.MAGENTA_MOSAIC_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PINK_MOSAIC_GLASS_SLAB = ITEMS.register(
            "pink_mosaic_glass_slab",
            () -> new BlockItem(ModBlocks.PINK_MOSAIC_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PINK_MOSAIC_GLASS_STAIRS = ITEMS.register(
            "pink_mosaic_glass_stairs",
            () -> new BlockItem(ModBlocks.PINK_MOSAIC_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> FACTORY_WHITE_GLASS_SLAB = ITEMS.register(
            "factory_white_glass_slab",
            () -> new BlockItem(ModBlocks.FACTORY_WHITE_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> FACTORY_WHITE_GLASS_STAIRS = ITEMS.register(
            "factory_white_glass_stairs",
            () -> new BlockItem(ModBlocks.FACTORY_WHITE_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> FACTORY_LIGHT_GRAY_GLASS_SLAB = ITEMS.register(
            "factory_light_gray_glass_slab",
            () -> new BlockItem(ModBlocks.FACTORY_LIGHT_GRAY_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> FACTORY_LIGHT_GRAY_GLASS_STAIRS = ITEMS.register(
            "factory_light_gray_glass_stairs",
            () -> new BlockItem(ModBlocks.FACTORY_LIGHT_GRAY_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> FACTORY_GRAY_GLASS_SLAB = ITEMS.register(
            "factory_gray_glass_slab",
            () -> new BlockItem(ModBlocks.FACTORY_GRAY_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> FACTORY_GRAY_GLASS_STAIRS = ITEMS.register(
            "factory_gray_glass_stairs",
            () -> new BlockItem(ModBlocks.FACTORY_GRAY_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> FACTORY_BLACK_GLASS_SLAB = ITEMS.register(
            "factory_black_glass_slab",
            () -> new BlockItem(ModBlocks.FACTORY_BLACK_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> FACTORY_BLACK_GLASS_STAIRS = ITEMS.register(
            "factory_black_glass_stairs",
            () -> new BlockItem(ModBlocks.FACTORY_BLACK_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> FACTORY_BROWN_GLASS_SLAB = ITEMS.register(
            "factory_brown_glass_slab",
            () -> new BlockItem(ModBlocks.FACTORY_BROWN_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> FACTORY_BROWN_GLASS_STAIRS = ITEMS.register(
            "factory_brown_glass_stairs",
            () -> new BlockItem(ModBlocks.FACTORY_BROWN_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> FACTORY_RED_GLASS_SLAB = ITEMS.register(
            "factory_red_glass_slab",
            () -> new BlockItem(ModBlocks.FACTORY_RED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> FACTORY_RED_GLASS_STAIRS = ITEMS.register(
            "factory_red_glass_stairs",
            () -> new BlockItem(ModBlocks.FACTORY_RED_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> FACTORY_ORANGE_GLASS_SLAB = ITEMS.register(
            "factory_orange_glass_slab",
            () -> new BlockItem(ModBlocks.FACTORY_ORANGE_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> FACTORY_ORANGE_GLASS_STAIRS = ITEMS.register(
            "factory_orange_glass_stairs",
            () -> new BlockItem(ModBlocks.FACTORY_ORANGE_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> FACTORY_YELLOW_GLASS_SLAB = ITEMS.register(
            "factory_yellow_glass_slab",
            () -> new BlockItem(ModBlocks.FACTORY_YELLOW_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> FACTORY_YELLOW_GLASS_STAIRS = ITEMS.register(
            "factory_yellow_glass_stairs",
            () -> new BlockItem(ModBlocks.FACTORY_YELLOW_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> FACTORY_LIME_GLASS_SLAB = ITEMS.register(
            "factory_lime_glass_slab",
            () -> new BlockItem(ModBlocks.FACTORY_LIME_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> FACTORY_LIME_GLASS_STAIRS = ITEMS.register(
            "factory_lime_glass_stairs",
            () -> new BlockItem(ModBlocks.FACTORY_LIME_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> FACTORY_GREEN_GLASS_SLAB = ITEMS.register(
            "factory_green_glass_slab",
            () -> new BlockItem(ModBlocks.FACTORY_GREEN_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> FACTORY_GREEN_GLASS_STAIRS = ITEMS.register(
            "factory_green_glass_stairs",
            () -> new BlockItem(ModBlocks.FACTORY_GREEN_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> FACTORY_CYAN_GLASS_SLAB = ITEMS.register(
            "factory_cyan_glass_slab",
            () -> new BlockItem(ModBlocks.FACTORY_CYAN_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> FACTORY_CYAN_GLASS_STAIRS = ITEMS.register(
            "factory_cyan_glass_stairs",
            () -> new BlockItem(ModBlocks.FACTORY_CYAN_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> FACTORY_LIGHT_BLUE_GLASS_SLAB = ITEMS.register(
            "factory_light_blue_glass_slab",
            () -> new BlockItem(ModBlocks.FACTORY_LIGHT_BLUE_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> FACTORY_LIGHT_BLUE_GLASS_STAIRS = ITEMS.register(
            "factory_light_blue_glass_stairs",
            () -> new BlockItem(ModBlocks.FACTORY_LIGHT_BLUE_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> FACTORY_BLUE_GLASS_SLAB = ITEMS.register(
            "factory_blue_glass_slab",
            () -> new BlockItem(ModBlocks.FACTORY_BLUE_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> FACTORY_BLUE_GLASS_STAIRS = ITEMS.register(
            "factory_blue_glass_stairs",
            () -> new BlockItem(ModBlocks.FACTORY_BLUE_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> FACTORY_PURPLE_GLASS_SLAB = ITEMS.register(
            "factory_purple_glass_slab",
            () -> new BlockItem(ModBlocks.FACTORY_PURPLE_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> FACTORY_PURPLE_GLASS_STAIRS = ITEMS.register(
            "factory_purple_glass_stairs",
            () -> new BlockItem(ModBlocks.FACTORY_PURPLE_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> FACTORY_MAGENTA_GLASS_SLAB = ITEMS.register(
            "factory_magenta_glass_slab",
            () -> new BlockItem(ModBlocks.FACTORY_MAGENTA_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> FACTORY_MAGENTA_GLASS_STAIRS = ITEMS.register(
            "factory_magenta_glass_stairs",
            () -> new BlockItem(ModBlocks.FACTORY_MAGENTA_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> FACTORY_PINK_GLASS_SLAB = ITEMS.register(
            "factory_pink_glass_slab",
            () -> new BlockItem(ModBlocks.FACTORY_PINK_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> FACTORY_PINK_GLASS_STAIRS = ITEMS.register(
            "factory_pink_glass_stairs",
            () -> new BlockItem(ModBlocks.FACTORY_PINK_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WHITE_GLAZED_GLASS_SLAB = ITEMS.register(
            "white_glazed_glass_slab",
            () -> new BlockItem(ModBlocks.WHITE_GLAZED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WHITE_GLAZED_GLASS_STAIRS = ITEMS.register(
            "white_glazed_glass_stairs",
            () -> new BlockItem(ModBlocks.WHITE_GLAZED_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIGHT_GRAY_GLAZED_GLASS_SLAB = ITEMS.register(
            "light_gray_glazed_glass_slab",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_GLAZED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIGHT_GRAY_GLAZED_GLASS_STAIRS = ITEMS.register(
            "light_gray_glazed_glass_stairs",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_GLAZED_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> GRAY_GLAZED_GLASS_SLAB = ITEMS.register(
            "gray_glazed_glass_slab",
            () -> new BlockItem(ModBlocks.GRAY_GLAZED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> GRAY_GLAZED_GLASS_STAIRS = ITEMS.register(
            "gray_glazed_glass_stairs",
            () -> new BlockItem(ModBlocks.GRAY_GLAZED_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BLACK_GLAZED_GLASS_SLAB = ITEMS.register(
            "black_glazed_glass_slab",
            () -> new BlockItem(ModBlocks.BLACK_GLAZED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BLACK_GLAZED_GLASS_STAIRS = ITEMS.register(
            "black_glazed_glass_stairs",
            () -> new BlockItem(ModBlocks.BLACK_GLAZED_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BROWN_GLAZED_GLASS_SLAB = ITEMS.register(
            "brown_glazed_glass_slab",
            () -> new BlockItem(ModBlocks.BROWN_GLAZED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BROWN_GLAZED_GLASS_STAIRS = ITEMS.register(
            "brown_glazed_glass_stairs",
            () -> new BlockItem(ModBlocks.BROWN_GLAZED_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> RED_GLAZED_GLASS_SLAB = ITEMS.register(
            "red_glazed_glass_slab",
            () -> new BlockItem(ModBlocks.RED_GLAZED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> RED_GLAZED_GLASS_STAIRS = ITEMS.register(
            "red_glazed_glass_stairs",
            () -> new BlockItem(ModBlocks.RED_GLAZED_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> ORANGE_GLAZED_GLASS_SLAB = ITEMS.register(
            "orange_glazed_glass_slab",
            () -> new BlockItem(ModBlocks.ORANGE_GLAZED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> ORANGE_GLAZED_GLASS_STAIRS = ITEMS.register(
            "orange_glazed_glass_stairs",
            () -> new BlockItem(ModBlocks.ORANGE_GLAZED_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> YELLOW_GLAZED_GLASS_SLAB = ITEMS.register(
            "yellow_glazed_glass_slab",
            () -> new BlockItem(ModBlocks.YELLOW_GLAZED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> YELLOW_GLAZED_GLASS_STAIRS = ITEMS.register(
            "yellow_glazed_glass_stairs",
            () -> new BlockItem(ModBlocks.YELLOW_GLAZED_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIME_GLAZED_GLASS_SLAB = ITEMS.register(
            "lime_glazed_glass_slab",
            () -> new BlockItem(ModBlocks.LIME_GLAZED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIME_GLAZED_GLASS_STAIRS = ITEMS.register(
            "lime_glazed_glass_stairs",
            () -> new BlockItem(ModBlocks.LIME_GLAZED_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> GREEN_GLAZED_GLASS_SLAB = ITEMS.register(
            "green_glazed_glass_slab",
            () -> new BlockItem(ModBlocks.GREEN_GLAZED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> GREEN_GLAZED_GLASS_STAIRS = ITEMS.register(
            "green_glazed_glass_stairs",
            () -> new BlockItem(ModBlocks.GREEN_GLAZED_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> CYAN_GLAZED_GLASS_SLAB = ITEMS.register(
            "cyan_glazed_glass_slab",
            () -> new BlockItem(ModBlocks.CYAN_GLAZED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> CYAN_GLAZED_GLASS_STAIRS = ITEMS.register(
            "cyan_glazed_glass_stairs",
            () -> new BlockItem(ModBlocks.CYAN_GLAZED_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIGHT_BLUE_GLAZED_GLASS_SLAB = ITEMS.register(
            "light_blue_glazed_glass_slab",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_GLAZED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIGHT_BLUE_GLAZED_GLASS_STAIRS = ITEMS.register(
            "light_blue_glazed_glass_stairs",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_GLAZED_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BLUE_GLAZED_GLASS_SLAB = ITEMS.register(
            "blue_glazed_glass_slab",
            () -> new BlockItem(ModBlocks.BLUE_GLAZED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BLUE_GLAZED_GLASS_STAIRS = ITEMS.register(
            "blue_glazed_glass_stairs",
            () -> new BlockItem(ModBlocks.BLUE_GLAZED_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PURPLE_GLAZED_GLASS_SLAB = ITEMS.register(
            "purple_glazed_glass_slab",
            () -> new BlockItem(ModBlocks.PURPLE_GLAZED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PURPLE_GLAZED_GLASS_STAIRS = ITEMS.register(
            "purple_glazed_glass_stairs",
            () -> new BlockItem(ModBlocks.PURPLE_GLAZED_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> MAGENTA_GLAZED_GLASS_SLAB = ITEMS.register(
            "magenta_glazed_glass_slab",
            () -> new BlockItem(ModBlocks.MAGENTA_GLAZED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> MAGENTA_GLAZED_GLASS_STAIRS = ITEMS.register(
            "magenta_glazed_glass_stairs",
            () -> new BlockItem(ModBlocks.MAGENTA_GLAZED_GLASS_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PINK_GLAZED_GLASS_SLAB = ITEMS.register(
            "pink_glazed_glass_slab",
            () -> new BlockItem(ModBlocks.PINK_GLAZED_GLASS_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PINK_GLAZED_GLASS_STAIRS = ITEMS.register(
            "pink_glazed_glass_stairs",
            () -> new BlockItem(ModBlocks.PINK_GLAZED_GLASS_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> OAK_LOG_SLAB = ITEMS.register("oak_log_slab",
            () -> new BlockItem(ModBlocks.OAK_LOG_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_OAK_LOG_SLAB = ITEMS.register("stripped_oak_log_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_OAK_LOG_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SPRUCE_LOG_SLAB = ITEMS.register("spruce_log_slab",
            () -> new BlockItem(ModBlocks.SPRUCE_LOG_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_SPRUCE_LOG_SLAB = ITEMS.register("stripped_spruce_log_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_SPRUCE_LOG_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIRCH_LOG_SLAB = ITEMS.register("birch_log_slab",
            () -> new BlockItem(ModBlocks.BIRCH_LOG_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_BIRCH_LOG_SLAB = ITEMS.register("stripped_birch_log_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_BIRCH_LOG_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> JUNGLE_LOG_SLAB = ITEMS.register("jungle_log_slab",
            () -> new BlockItem(ModBlocks.JUNGLE_LOG_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_JUNGLE_LOG_SLAB = ITEMS.register("stripped_jungle_log_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_JUNGLE_LOG_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ACACIA_LOG_SLAB = ITEMS.register("acacia_log_slab",
            () -> new BlockItem(ModBlocks.ACACIA_LOG_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_ACACIA_LOG_SLAB = ITEMS.register("stripped_acacia_log_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_ACACIA_LOG_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> DARK_OAK_LOG_SLAB = ITEMS.register("dark_oak_log_slab",
            () -> new BlockItem(ModBlocks.DARK_OAK_LOG_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_DARK_OAK_LOG_SLAB = ITEMS.register("stripped_dark_oak_log_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_DARK_OAK_LOG_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CRIMSON_STEM_SLAB = ITEMS.register("crimson_stem_slab",
            () -> new BlockItem(ModBlocks.CRIMSON_STEM_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_CRIMSON_STEM_SLAB = ITEMS.register("stripped_crimson_stem_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_CRIMSON_STEM_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WARPED_STEM_SLAB = ITEMS.register("warped_stem_slab",
            () -> new BlockItem(ModBlocks.WARPED_STEM_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_WARPED_STEM_SLAB = ITEMS.register("stripped_warped_stem_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_WARPED_STEM_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MANGROVE_LOG_SLAB = ITEMS.register("mangrove_log_slab",
            () -> new BlockItem(ModBlocks.MANGROVE_LOG_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MANGROVE_LOG_STAIRS = ITEMS.register("mangrove_log_stairs",
            () -> new BlockItem(ModBlocks.MANGROVE_LOG_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_MANGROVE_LOG_SLAB = ITEMS.register("stripped_mangrove_log_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_MANGROVE_LOG_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_MANGROVE_LOG_STAIRS = ITEMS.register("stripped_mangrove_log_stairs",
            () -> new BlockItem(ModBlocks.STRIPPED_MANGROVE_LOG_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_LOG = ITEMS.register("ashpen_log",
            () -> new BlockItem(ModBlocks.ASHPEN_LOG.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_WOOD = ITEMS.register("ashpen_wood",
            () -> new BlockItem(ModBlocks.ASHPEN_WOOD.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_LOG_SLAB = ITEMS.register("ashpen_log_slab",
            () -> new BlockItem(ModBlocks.ASHPEN_LOG_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_LOG_STAIRS = ITEMS.register("ashpen_log_stairs",
            () -> new BlockItem(ModBlocks.ASHPEN_LOG_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_WOOD_SLAB = ITEMS.register("ashpen_wood_slab",
            () -> new BlockItem(ModBlocks.ASHPEN_WOOD_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_WOOD_STAIRS = ITEMS.register("ashpen_wood_stairs",
            () -> new BlockItem(ModBlocks.ASHPEN_WOOD_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_ASHPEN_LOG = ITEMS.register("stripped_ashpen_log",
            () -> new BlockItem(ModBlocks.STRIPPED_ASHPEN_LOG.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_ASHPEN_WOOD = ITEMS.register("stripped_ashpen_wood",
            () -> new BlockItem(ModBlocks.STRIPPED_ASHPEN_WOOD.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_ASHPEN_LOG_SLAB = ITEMS.register("stripped_ashpen_log_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_ASHPEN_LOG_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_ASHPEN_LOG_STAIRS = ITEMS.register("stripped_ashpen_log_stairs",
            () -> new BlockItem(ModBlocks.STRIPPED_ASHPEN_LOG_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_ASHPEN_WOOD_SLAB = ITEMS.register("stripped_ashpen_wood_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_ASHPEN_WOOD_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_ASHPEN_WOOD_STAIRS = ITEMS.register("stripped_ashpen_wood_stairs",
            () -> new BlockItem(ModBlocks.STRIPPED_ASHPEN_WOOD_STAIRS.get(), createBlockItemProperties()));


    public static final RegistryObject<Item> BIT_POLISHED_TUFF_VERTICAL_SLAB = ITEMS.register("bit_polished_tuff_vertical_slab",
            () -> new BlockItem(ModBlocks.BIT_POLISHED_TUFF_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIT_TUFF_BRICKS_VERTICAL_SLAB = ITEMS.register("bit_tuff_bricks_vertical_slab",
            () -> new BlockItem(ModBlocks.BIT_TUFF_BRICKS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PODZOL_VERTICAL_SLAB = ITEMS.register("podzol_vertical_slab",
            () -> new BlockItem(ModBlocks.PODZOL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> DIRT_VERTICAL_SLAB = ITEMS.register("dirt_vertical_slab",
            () -> new BlockItem(ModBlocks.DIRT_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MYCELIUM_VERTICAL_SLAB = ITEMS.register("mycelium_vertical_slab",
            () -> new BlockItem(ModBlocks.MYCELIUM_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MUD_VERTICAL_SLAB = ITEMS.register("mud_vertical_slab",
            () -> new BlockItem(ModBlocks.MUD_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_SANDSTONE_VERTICAL_SLAB = ITEMS.register("black_sandstone_vertical_slab",
            () -> new BlockItem(ModBlocks.BLACK_SANDSTONE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_SANDSTONE_VERTICAL_SLAB = ITEMS.register("blue_sandstone_vertical_slab",
            () -> new BlockItem(ModBlocks.BLUE_SANDSTONE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_SANDSTONE_VERTICAL_SLAB = ITEMS.register("green_sandstone_vertical_slab",
            () -> new BlockItem(ModBlocks.GREEN_SANDSTONE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_SANDSTONE_VERTICAL_SLAB = ITEMS.register("orange_sandstone_vertical_slab",
            () -> new BlockItem(ModBlocks.ORANGE_SANDSTONE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_SANDSTONE_VERTICAL_SLAB = ITEMS.register("pink_sandstone_vertical_slab",
            () -> new BlockItem(ModBlocks.PINK_SANDSTONE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_SANDSTONE_VERTICAL_SLAB = ITEMS.register("red_sandstone_vertical_slab",
            () -> new BlockItem(ModBlocks.RED_SANDSTONE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WHITE_SANDSTONE_VERTICAL_SLAB = ITEMS.register("white_sandstone_vertical_slab",
            () -> new BlockItem(ModBlocks.WHITE_SANDSTONE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_SANDSTONE_VERTICAL_SLAB = ITEMS.register("yellow_sandstone_vertical_slab",
            () -> new BlockItem(ModBlocks.YELLOW_SANDSTONE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_SMOOTH_SANDSTONE_VERTICAL_SLAB = ITEMS.register("black_smooth_sandstone_vertical_slab",
            () -> new BlockItem(ModBlocks.BLACK_SMOOTH_SANDSTONE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_SMOOTH_SANDSTONE_VERTICAL_SLAB = ITEMS.register("blue_smooth_sandstone_vertical_slab",
            () -> new BlockItem(ModBlocks.BLUE_SMOOTH_SANDSTONE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_SMOOTH_SANDSTONE_VERTICAL_SLAB = ITEMS.register("green_smooth_sandstone_vertical_slab",
            () -> new BlockItem(ModBlocks.GREEN_SMOOTH_SANDSTONE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_SMOOTH_SANDSTONE_VERTICAL_SLAB = ITEMS.register("orange_smooth_sandstone_vertical_slab",
            () -> new BlockItem(ModBlocks.ORANGE_SMOOTH_SANDSTONE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_SMOOTH_SANDSTONE_VERTICAL_SLAB = ITEMS.register("pink_smooth_sandstone_vertical_slab",
            () -> new BlockItem(ModBlocks.PINK_SMOOTH_SANDSTONE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_SMOOTH_SANDSTONE_VERTICAL_SLAB = ITEMS.register("red_smooth_sandstone_vertical_slab",
            () -> new BlockItem(ModBlocks.RED_SMOOTH_SANDSTONE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WHITE_SMOOTH_SANDSTONE_VERTICAL_SLAB = ITEMS.register("white_smooth_sandstone_vertical_slab",
            () -> new BlockItem(ModBlocks.WHITE_SMOOTH_SANDSTONE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_SMOOTH_SANDSTONE_VERTICAL_SLAB = ITEMS.register("yellow_smooth_sandstone_vertical_slab",
            () -> new BlockItem(ModBlocks.YELLOW_SMOOTH_SANDSTONE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_TILES_VERTICAL_SLAB = ITEMS.register("black_tiles_vertical_slab",
            () -> new BlockItem(ModBlocks.BLACK_TILES_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_TILES_VERTICAL_SLAB = ITEMS.register("blue_tiles_vertical_slab",
            () -> new BlockItem(ModBlocks.BLUE_TILES_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_TILES_VERTICAL_SLAB = ITEMS.register("brown_tiles_vertical_slab",
            () -> new BlockItem(ModBlocks.BROWN_TILES_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_TILES_VERTICAL_SLAB = ITEMS.register("cyan_tiles_vertical_slab",
            () -> new BlockItem(ModBlocks.CYAN_TILES_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_TILES_VERTICAL_SLAB = ITEMS.register("gray_tiles_vertical_slab",
            () -> new BlockItem(ModBlocks.GRAY_TILES_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_TILES_VERTICAL_SLAB = ITEMS.register("green_tiles_vertical_slab",
            () -> new BlockItem(ModBlocks.GREEN_TILES_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_TILES_VERTICAL_SLAB = ITEMS.register("light_blue_tiles_vertical_slab",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_TILES_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_TILES_VERTICAL_SLAB = ITEMS.register("light_gray_tiles_vertical_slab",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_TILES_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_TILES_VERTICAL_SLAB = ITEMS.register("lime_tiles_vertical_slab",
            () -> new BlockItem(ModBlocks.LIME_TILES_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_TILES_VERTICAL_SLAB = ITEMS.register("magenta_tiles_vertical_slab",
            () -> new BlockItem(ModBlocks.MAGENTA_TILES_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_TILES_VERTICAL_SLAB = ITEMS.register("orange_tiles_vertical_slab",
            () -> new BlockItem(ModBlocks.ORANGE_TILES_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_TILES_VERTICAL_SLAB = ITEMS.register("pink_tiles_vertical_slab",
            () -> new BlockItem(ModBlocks.PINK_TILES_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_TILES_VERTICAL_SLAB = ITEMS.register("purple_tiles_vertical_slab",
            () -> new BlockItem(ModBlocks.PURPLE_TILES_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_TILES_VERTICAL_SLAB = ITEMS.register("red_tiles_vertical_slab",
            () -> new BlockItem(ModBlocks.RED_TILES_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WHITE_TILES_VERTICAL_SLAB = ITEMS.register("white_tiles_vertical_slab",
            () -> new BlockItem(ModBlocks.WHITE_TILES_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_TILES_VERTICAL_SLAB = ITEMS.register("yellow_tiles_vertical_slab",
            () -> new BlockItem(ModBlocks.YELLOW_TILES_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_BASALT_VERTICAL_SLAB = ITEMS.register("polished_basalt_vertical_slab",
            () -> new BlockItem(ModBlocks.POLISHED_BASALT_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> DRIPSTONE_BLOCK_VERTICAL_SLAB = ITEMS.register("dripstone_block_vertical_slab",
            () -> new BlockItem(ModBlocks.DRIPSTONE_BLOCK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> END_STONE_VERTICAL_SLAB = ITEMS.register("end_stone_vertical_slab",
            () -> new BlockItem(ModBlocks.END_STONE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> QUARTZ_BRICKS_VERTICAL_SLAB = ITEMS.register("quartz_bricks_vertical_slab",
            () -> new BlockItem(ModBlocks.QUARTZ_BRICKS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> QUARTZ_PILLAR_VERTICAL_SLAB = ITEMS.register("quartz_pillar_vertical_slab",
            () -> new BlockItem(ModBlocks.QUARTZ_PILLAR_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CALCITE_VERTICAL_SLAB = ITEMS.register("calcite_vertical_slab",
            () -> new BlockItem(ModBlocks.CALCITE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OBSIDIAN_VERTICAL_SLAB = ITEMS.register("obsidian_vertical_slab",
            () -> new BlockItem(ModBlocks.OBSIDIAN_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SMOOTH_BASALT_VERTICAL_SLAB = ITEMS.register("smooth_basalt_vertical_slab",
            () -> new BlockItem(ModBlocks.SMOOTH_BASALT_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MOSS_BLOCK_VERTICAL_SLAB = ITEMS.register("moss_block_vertical_slab",
            () -> new BlockItem(ModBlocks.MOSS_BLOCK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> AMETHYST_BLOCK_VERTICAL_SLAB = ITEMS.register("amethyst_block_vertical_slab",
            () -> new BlockItem(ModBlocks.AMETHYST_BLOCK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MOSSY_CALCITE_VERTICAL_SLAB = ITEMS.register("mossy_calcite_vertical_slab",
            () -> new BlockItem(ModBlocks.MOSSY_CALCITE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIT_COPPER_BLOCK_VERTICAL_SLAB = ITEMS.register("bit_copper_block_vertical_slab",
            () -> new BlockItem(ModBlocks.BIT_COPPER_BLOCK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIT_EXPOSED_COPPER_BLOCK_VERTICAL_SLAB = ITEMS.register("bit_exposed_copper_block_vertical_slab",
            () -> new BlockItem(ModBlocks.BIT_EXPOSED_COPPER_BLOCK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIT_WEATHERED_COPPER_BLOCK_VERTICAL_SLAB = ITEMS.register("bit_weathered_copper_block_vertical_slab",
            () -> new BlockItem(ModBlocks.BIT_WEATHERED_COPPER_BLOCK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIT_OXIDIZED_COPPER_BLOCK_VERTICAL_SLAB = ITEMS.register("bit_oxidized_copper_block_vertical_slab",
            () -> new BlockItem(ModBlocks.BIT_OXIDIZED_COPPER_BLOCK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIT_CUT_COPPER_VERTICAL_SLAB = ITEMS.register("bit_cut_copper_vertical_slab",
            () -> new BlockItem(ModBlocks.BIT_CUT_COPPER_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIT_EXPOSED_CUT_COPPER_VERTICAL_SLAB = ITEMS.register("bit_exposed_cut_copper_vertical_slab",
            () -> new BlockItem(ModBlocks.BIT_EXPOSED_CUT_COPPER_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIT_WEATHERED_CUT_COPPER_VERTICAL_SLAB = ITEMS.register("bit_weathered_cut_copper_vertical_slab",
            () -> new BlockItem(ModBlocks.BIT_WEATHERED_CUT_COPPER_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIT_OXIDIZED_CUT_COPPER_VERTICAL_SLAB = ITEMS.register("bit_oxidized_cut_copper_vertical_slab",
            () -> new BlockItem(ModBlocks.BIT_OXIDIZED_CUT_COPPER_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_CONCRETE_VERTICAL_SLAB = ITEMS.register("black_concrete_vertical_slab",
            () -> new BlockItem(ModBlocks.BLACK_CONCRETE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_CONCRETE_VERTICAL_SLAB = ITEMS.register("blue_concrete_vertical_slab",
            () -> new BlockItem(ModBlocks.BLUE_CONCRETE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_CONCRETE_VERTICAL_SLAB = ITEMS.register("brown_concrete_vertical_slab",
            () -> new BlockItem(ModBlocks.BROWN_CONCRETE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_CONCRETE_VERTICAL_SLAB = ITEMS.register("cyan_concrete_vertical_slab",
            () -> new BlockItem(ModBlocks.CYAN_CONCRETE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_CONCRETE_VERTICAL_SLAB = ITEMS.register("gray_concrete_vertical_slab",
            () -> new BlockItem(ModBlocks.GRAY_CONCRETE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_CONCRETE_VERTICAL_SLAB = ITEMS.register("green_concrete_vertical_slab",
            () -> new BlockItem(ModBlocks.GREEN_CONCRETE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_CONCRETE_VERTICAL_SLAB = ITEMS.register("light_blue_concrete_vertical_slab",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_CONCRETE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_CONCRETE_VERTICAL_SLAB = ITEMS.register("light_gray_concrete_vertical_slab",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_CONCRETE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_CONCRETE_VERTICAL_SLAB = ITEMS.register("lime_concrete_vertical_slab",
            () -> new BlockItem(ModBlocks.LIME_CONCRETE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_CONCRETE_VERTICAL_SLAB = ITEMS.register("magenta_concrete_vertical_slab",
            () -> new BlockItem(ModBlocks.MAGENTA_CONCRETE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_CONCRETE_VERTICAL_SLAB = ITEMS.register("orange_concrete_vertical_slab",
            () -> new BlockItem(ModBlocks.ORANGE_CONCRETE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_CONCRETE_VERTICAL_SLAB = ITEMS.register("pink_concrete_vertical_slab",
            () -> new BlockItem(ModBlocks.PINK_CONCRETE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_CONCRETE_VERTICAL_SLAB = ITEMS.register("purple_concrete_vertical_slab",
            () -> new BlockItem(ModBlocks.PURPLE_CONCRETE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_CONCRETE_VERTICAL_SLAB = ITEMS.register("red_concrete_vertical_slab",
            () -> new BlockItem(ModBlocks.RED_CONCRETE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WHITE_CONCRETE_VERTICAL_SLAB = ITEMS.register("white_concrete_vertical_slab",
            () -> new BlockItem(ModBlocks.WHITE_CONCRETE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_CONCRETE_VERTICAL_SLAB = ITEMS.register("yellow_concrete_vertical_slab",
            () -> new BlockItem(ModBlocks.YELLOW_CONCRETE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> HAY_BALE_VERTICAL_SLAB = ITEMS.register("hay_bale_vertical_slab",
            () -> new BlockItem(ModBlocks.HAY_BALE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BAMBOO_BLOCK_VERTICAL_SLAB = ITEMS.register("bamboo_block_vertical_slab",
            () -> new BlockItem(ModBlocks.BAMBOO_BLOCK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_BAMBOO_BLOCK_VERTICAL_SLAB = ITEMS.register("stripped_bamboo_block_vertical_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_BAMBOO_BLOCK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_WHITE_VERTICAL_SLAB = ITEMS.register("ashpen_white_vertical_slab",
            () -> new BlockItem(ModBlocks.ASHPEN_WHITE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_BLACK_VERTICAL_SLAB = ITEMS.register("ashpen_black_vertical_slab",
            () -> new BlockItem(ModBlocks.ASHPEN_BLACK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_BLUE_VERTICAL_SLAB = ITEMS.register("ashpen_blue_vertical_slab",
            () -> new BlockItem(ModBlocks.ASHPEN_BLUE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_BROWN_VERTICAL_SLAB = ITEMS.register("ashpen_brown_vertical_slab",
            () -> new BlockItem(ModBlocks.ASHPEN_BROWN_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_CYAN_VERTICAL_SLAB = ITEMS.register("ashpen_cyan_vertical_slab",
            () -> new BlockItem(ModBlocks.ASHPEN_CYAN_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_GRAY_VERTICAL_SLAB = ITEMS.register("ashpen_gray_vertical_slab",
            () -> new BlockItem(ModBlocks.ASHPEN_GRAY_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_GREEN_VERTICAL_SLAB = ITEMS.register("ashpen_green_vertical_slab",
            () -> new BlockItem(ModBlocks.ASHPEN_GREEN_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_LIGHT_BLUE_VERTICAL_SLAB = ITEMS.register("ashpen_light_blue_vertical_slab",
            () -> new BlockItem(ModBlocks.ASHPEN_LIGHT_BLUE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_LIGHT_GRAY_VERTICAL_SLAB = ITEMS.register("ashpen_light_gray_vertical_slab",
            () -> new BlockItem(ModBlocks.ASHPEN_LIGHT_GRAY_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_LIME_VERTICAL_SLAB = ITEMS.register("ashpen_lime_vertical_slab",
            () -> new BlockItem(ModBlocks.ASHPEN_LIME_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_MAGENTA_VERTICAL_SLAB = ITEMS.register("ashpen_magenta_vertical_slab",
            () -> new BlockItem(ModBlocks.ASHPEN_MAGENTA_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_ORANGE_VERTICAL_SLAB = ITEMS.register("ashpen_orange_vertical_slab",
            () -> new BlockItem(ModBlocks.ASHPEN_ORANGE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_PINK_VERTICAL_SLAB = ITEMS.register("ashpen_pink_vertical_slab",
            () -> new BlockItem(ModBlocks.ASHPEN_PINK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_PURPLE_VERTICAL_SLAB = ITEMS.register("ashpen_purple_vertical_slab",
            () -> new BlockItem(ModBlocks.ASHPEN_PURPLE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_RED_VERTICAL_SLAB = ITEMS.register("ashpen_red_vertical_slab",
            () -> new BlockItem(ModBlocks.ASHPEN_RED_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_YELLOW_VERTICAL_SLAB = ITEMS.register("ashpen_yellow_vertical_slab",
            () -> new BlockItem(ModBlocks.ASHPEN_YELLOW_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SNOW_BRICKS_VERTICAL_SLAB = ITEMS.register("snow_bricks_vertical_slab",
            () -> new BlockItem(ModBlocks.SNOW_BRICKS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SNOW_VERTICAL_SLAB = ITEMS.register("snow_vertical_slab",
            () -> new BlockItem(ModBlocks.SNOW_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SNOWY_GRASS_BLOCK_VERTICAL_SLAB = ITEMS.register("snowy_grass_block_vertical_slab",
            () -> new BlockItem(ModBlocks.SNOWY_GRASS_BLOCK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MANGROVE_VERTICAL_SLAB = ITEMS.register("mangrove_vertical_slab",
            () -> new BlockItem(ModBlocks.MANGROVE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STEEL_BLOCK_VERTICAL_SLAB = ITEMS.register("steel_block_vertical_slab",
            () -> new BlockItem(ModBlocks.STEEL_BLOCK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_STEEL_VERTICAL_SLAB = ITEMS.register("polished_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.POLISHED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PRESSED_STEEL_VERTICAL_SLAB = ITEMS.register("pressed_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.PRESSED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CUT_STEEL_VERTICAL_SLAB = ITEMS.register("cut_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.CUT_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CAUTION_BLACK_VERTICAL_SLAB = ITEMS.register("caution_black_vertical_slab",
            () -> new BlockItem(ModBlocks.CAUTION_BLACK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CAUTION_BLUE_VERTICAL_SLAB = ITEMS.register("caution_blue_vertical_slab",
            () -> new BlockItem(ModBlocks.CAUTION_BLUE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CAUTION_FACTORY_VERTICAL_SLAB = ITEMS.register("caution_factory_vertical_slab",
            () -> new BlockItem(ModBlocks.CAUTION_FACTORY_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CAUTION_RED_VERTICAL_SLAB = ITEMS.register("caution_red_vertical_slab",
            () -> new BlockItem(ModBlocks.CAUTION_RED_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FRAMED_CAUTION_VERTICAL_SLAB = ITEMS.register("framed_caution_vertical_slab",
            () -> new BlockItem(ModBlocks.FRAMED_CAUTION_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CAUTION_WHITE_VERTICAL_SLAB = ITEMS.register("caution_white_vertical_slab",
            () -> new BlockItem(ModBlocks.CAUTION_WHITE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CAUTION_LIME_VERTICAL_SLAB = ITEMS.register("caution_lime_vertical_slab",
            () -> new BlockItem(ModBlocks.CAUTION_LIME_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CAUTION_PINK_VERTICAL_SLAB = ITEMS.register("caution_pink_vertical_slab",
            () -> new BlockItem(ModBlocks.CAUTION_PINK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CAUTION_YELLOW_VERTICAL_SLAB = ITEMS.register("caution_yellow_vertical_slab",
            () -> new BlockItem(ModBlocks.CAUTION_YELLOW_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CAUTION_CANDY_VERTICAL_SLAB = ITEMS.register("caution_candy_vertical_slab",
            () -> new BlockItem(ModBlocks.CAUTION_CANDY_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CAUTION_COTTONCANDY_VERTICAL_SLAB = ITEMS.register("caution_cottoncandy_vertical_slab",
            () -> new BlockItem(ModBlocks.CAUTION_COTTONCANDY_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CAUTION_MINTCANDY_VERTICAL_SLAB = ITEMS.register("caution_mintcandy_vertical_slab",
            () -> new BlockItem(ModBlocks.CAUTION_MINTCANDY_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CAUTION_CITRUSCANDY_VERTICAL_SLAB = ITEMS.register("caution_citruscandy_vertical_slab",
            () -> new BlockItem(ModBlocks.CAUTION_CITRUSCANDY_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WHITE_STAINED_BRICKS_VERTICAL_SLAB = ITEMS.register("white_stained_bricks_vertical_slab",
            () -> new BlockItem(ModBlocks.WHITE_STAINED_BRICKS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICKS_VERTICAL_SLAB = ITEMS.register("light_gray_stained_bricks_vertical_slab",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_STAINED_BRICKS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_STAINED_BRICKS_VERTICAL_SLAB = ITEMS.register("gray_stained_bricks_vertical_slab",
            () -> new BlockItem(ModBlocks.GRAY_STAINED_BRICKS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_STAINED_BRICKS_VERTICAL_SLAB = ITEMS.register("black_stained_bricks_vertical_slab",
            () -> new BlockItem(ModBlocks.BLACK_STAINED_BRICKS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_STAINED_BRICKS_VERTICAL_SLAB = ITEMS.register("brown_stained_bricks_vertical_slab",
            () -> new BlockItem(ModBlocks.BROWN_STAINED_BRICKS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_STAINED_BRICKS_VERTICAL_SLAB = ITEMS.register("red_stained_bricks_vertical_slab",
            () -> new BlockItem(ModBlocks.RED_STAINED_BRICKS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_STAINED_BRICKS_VERTICAL_SLAB = ITEMS.register("orange_stained_bricks_vertical_slab",
            () -> new BlockItem(ModBlocks.ORANGE_STAINED_BRICKS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_STAINED_BRICKS_VERTICAL_SLAB = ITEMS.register("yellow_stained_bricks_vertical_slab",
            () -> new BlockItem(ModBlocks.YELLOW_STAINED_BRICKS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_STAINED_BRICKS_VERTICAL_SLAB = ITEMS.register("lime_stained_bricks_vertical_slab",
            () -> new BlockItem(ModBlocks.LIME_STAINED_BRICKS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_STAINED_BRICKS_VERTICAL_SLAB = ITEMS.register("green_stained_bricks_vertical_slab",
            () -> new BlockItem(ModBlocks.GREEN_STAINED_BRICKS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_STAINED_BRICKS_VERTICAL_SLAB = ITEMS.register("cyan_stained_bricks_vertical_slab",
            () -> new BlockItem(ModBlocks.CYAN_STAINED_BRICKS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICKS_VERTICAL_SLAB = ITEMS.register("light_blue_stained_bricks_vertical_slab",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_STAINED_BRICKS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_STAINED_BRICKS_VERTICAL_SLAB = ITEMS.register("blue_stained_bricks_vertical_slab",
            () -> new BlockItem(ModBlocks.BLUE_STAINED_BRICKS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_STAINED_BRICKS_VERTICAL_SLAB = ITEMS.register("purple_stained_bricks_vertical_slab",
            () -> new BlockItem(ModBlocks.PURPLE_STAINED_BRICKS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICKS_VERTICAL_SLAB = ITEMS.register("magenta_stained_bricks_vertical_slab",
            () -> new BlockItem(ModBlocks.MAGENTA_STAINED_BRICKS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_STAINED_BRICKS_VERTICAL_SLAB = ITEMS.register("pink_stained_bricks_vertical_slab",
            () -> new BlockItem(ModBlocks.PINK_STAINED_BRICKS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_WHITE_CONCRETE_VERTICAL_SLAB = ITEMS.register("polished_white_concrete_vertical_slab",
            () -> new BlockItem(ModBlocks.POLISHED_WHITE_CONCRETE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_ORANGE_CONCRETE_VERTICAL_SLAB = ITEMS.register("polished_orange_concrete_vertical_slab",
            () -> new BlockItem(ModBlocks.POLISHED_ORANGE_CONCRETE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_MAGENTA_CONCRETE_VERTICAL_SLAB = ITEMS.register("polished_magenta_concrete_vertical_slab",
            () -> new BlockItem(ModBlocks.POLISHED_MAGENTA_CONCRETE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_LIGHT_BLUE_CONCRETE_VERTICAL_SLAB = ITEMS.register("polished_light_blue_concrete_vertical_slab",
            () -> new BlockItem(ModBlocks.POLISHED_LIGHT_BLUE_CONCRETE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_YELLOW_CONCRETE_VERTICAL_SLAB = ITEMS.register("polished_yellow_concrete_vertical_slab",
            () -> new BlockItem(ModBlocks.POLISHED_YELLOW_CONCRETE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_LIME_CONCRETE_VERTICAL_SLAB = ITEMS.register("polished_lime_concrete_vertical_slab",
            () -> new BlockItem(ModBlocks.POLISHED_LIME_CONCRETE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_PINK_CONCRETE_VERTICAL_SLAB = ITEMS.register("polished_pink_concrete_vertical_slab",
            () -> new BlockItem(ModBlocks.POLISHED_PINK_CONCRETE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_GRAY_CONCRETE_VERTICAL_SLAB = ITEMS.register("polished_gray_concrete_vertical_slab",
            () -> new BlockItem(ModBlocks.POLISHED_GRAY_CONCRETE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_LIGHT_GRAY_CONCRETE_VERTICAL_SLAB = ITEMS.register("polished_light_gray_concrete_vertical_slab",
            () -> new BlockItem(ModBlocks.POLISHED_LIGHT_GRAY_CONCRETE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_CYAN_CONCRETE_VERTICAL_SLAB = ITEMS.register("polished_cyan_concrete_vertical_slab",
            () -> new BlockItem(ModBlocks.POLISHED_CYAN_CONCRETE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_PURPLE_CONCRETE_VERTICAL_SLAB = ITEMS.register("polished_purple_concrete_vertical_slab",
            () -> new BlockItem(ModBlocks.POLISHED_PURPLE_CONCRETE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_BLUE_CONCRETE_VERTICAL_SLAB = ITEMS.register("polished_blue_concrete_vertical_slab",
            () -> new BlockItem(ModBlocks.POLISHED_BLUE_CONCRETE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_BROWN_CONCRETE_VERTICAL_SLAB = ITEMS.register("polished_brown_concrete_vertical_slab",
            () -> new BlockItem(ModBlocks.POLISHED_BROWN_CONCRETE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_GREEN_CONCRETE_VERTICAL_SLAB = ITEMS.register("polished_green_concrete_vertical_slab",
            () -> new BlockItem(ModBlocks.POLISHED_GREEN_CONCRETE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_RED_CONCRETE_VERTICAL_SLAB = ITEMS.register("polished_red_concrete_vertical_slab",
            () -> new BlockItem(ModBlocks.POLISHED_RED_CONCRETE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_BLACK_CONCRETE_VERTICAL_SLAB = ITEMS.register("polished_black_concrete_vertical_slab",
            () -> new BlockItem(ModBlocks.POLISHED_BLACK_CONCRETE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_FACTORY_MESH_VERTICAL_SLAB = ITEMS.register("black_factory_mesh_vertical_slab",
            () -> new BlockItem(ModBlocks.BLACK_FACTORY_MESH_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_FACTORY_MESH_VERTICAL_SLAB = ITEMS.register("blue_factory_mesh_vertical_slab",
            () -> new BlockItem(ModBlocks.BLUE_FACTORY_MESH_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_FACTORY_MESH_VERTICAL_SLAB = ITEMS.register("brown_factory_mesh_vertical_slab",
            () -> new BlockItem(ModBlocks.BROWN_FACTORY_MESH_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_FACTORY_MESH_VERTICAL_SLAB = ITEMS.register("gray_factory_mesh_vertical_slab",
            () -> new BlockItem(ModBlocks.GRAY_FACTORY_MESH_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_FACTORY_MESH_VERTICAL_SLAB = ITEMS.register("green_factory_mesh_vertical_slab",
            () -> new BlockItem(ModBlocks.GREEN_FACTORY_MESH_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_FACTORY_MESH_VERTICAL_SLAB = ITEMS.register("light_gray_factory_mesh_vertical_slab",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_FACTORY_MESH_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_FACTORY_MESH_VERTICAL_SLAB = ITEMS.register("lime_factory_mesh_vertical_slab",
            () -> new BlockItem(ModBlocks.LIME_FACTORY_MESH_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_FACTORY_MESH_VERTICAL_SLAB = ITEMS.register("orange_factory_mesh_vertical_slab",
            () -> new BlockItem(ModBlocks.ORANGE_FACTORY_MESH_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_FACTORY_MESH_VERTICAL_SLAB = ITEMS.register("red_factory_mesh_vertical_slab",
            () -> new BlockItem(ModBlocks.RED_FACTORY_MESH_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_FACTORY_MESH_VERTICAL_SLAB = ITEMS.register("yellow_factory_mesh_vertical_slab",
            () -> new BlockItem(ModBlocks.YELLOW_FACTORY_MESH_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PACKED_MUD_VERTICAL_SLAB = ITEMS.register("packed_mud_vertical_slab",
            () -> new BlockItem(ModBlocks.PACKED_MUD_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MUD_BRICK_VERTICAL_SLAB = ITEMS.register("mud_brick_vertical_slab",
            () -> new BlockItem(ModBlocks.MUD_BRICK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GLASS_VERTICAL_SLAB = ITEMS.register("glass_vertical_slab",
            () -> new BlockItem(ModBlocks.GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WHITE_STAINED_GLASS_VERTICAL_SLAB = ITEMS.register("white_stained_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.WHITE_STAINED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_GLASS_VERTICAL_SLAB = ITEMS.register("light_gray_stained_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_STAINED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_STAINED_GLASS_VERTICAL_SLAB = ITEMS.register("gray_stained_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.GRAY_STAINED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_STAINED_GLASS_VERTICAL_SLAB = ITEMS.register("black_stained_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.BLACK_STAINED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_STAINED_GLASS_VERTICAL_SLAB = ITEMS.register("brown_stained_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.BROWN_STAINED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_STAINED_GLASS_VERTICAL_SLAB = ITEMS.register("red_stained_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.RED_STAINED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_STAINED_GLASS_VERTICAL_SLAB = ITEMS.register("orange_stained_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.ORANGE_STAINED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_STAINED_GLASS_VERTICAL_SLAB = ITEMS.register("yellow_stained_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.YELLOW_STAINED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_STAINED_GLASS_VERTICAL_SLAB = ITEMS.register("lime_stained_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.LIME_STAINED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_STAINED_GLASS_VERTICAL_SLAB = ITEMS.register("green_stained_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.GREEN_STAINED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_STAINED_GLASS_VERTICAL_SLAB = ITEMS.register("cyan_stained_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.CYAN_STAINED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_GLASS_VERTICAL_SLAB = ITEMS.register("light_blue_stained_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_STAINED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_STAINED_GLASS_VERTICAL_SLAB = ITEMS.register("blue_stained_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.BLUE_STAINED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_STAINED_GLASS_VERTICAL_SLAB = ITEMS.register("purple_stained_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.PURPLE_STAINED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_STAINED_GLASS_VERTICAL_SLAB = ITEMS.register("magenta_stained_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.MAGENTA_STAINED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_STAINED_GLASS_VERTICAL_SLAB = ITEMS.register("pink_stained_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.PINK_STAINED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WHITE_MOSAIC_GLASS_VERTICAL_SLAB = ITEMS.register("white_mosaic_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.WHITE_MOSAIC_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_MOSAIC_GLASS_VERTICAL_SLAB = ITEMS.register("light_gray_mosaic_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_MOSAIC_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_MOSAIC_GLASS_VERTICAL_SLAB = ITEMS.register("gray_mosaic_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.GRAY_MOSAIC_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_MOSAIC_GLASS_VERTICAL_SLAB = ITEMS.register("black_mosaic_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.BLACK_MOSAIC_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_MOSAIC_GLASS_VERTICAL_SLAB = ITEMS.register("brown_mosaic_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.BROWN_MOSAIC_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_MOSAIC_GLASS_VERTICAL_SLAB = ITEMS.register("red_mosaic_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.RED_MOSAIC_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_MOSAIC_GLASS_VERTICAL_SLAB = ITEMS.register("orange_mosaic_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.ORANGE_MOSAIC_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_MOSAIC_GLASS_VERTICAL_SLAB = ITEMS.register("yellow_mosaic_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.YELLOW_MOSAIC_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_MOSAIC_GLASS_VERTICAL_SLAB = ITEMS.register("lime_mosaic_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.LIME_MOSAIC_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_MOSAIC_GLASS_VERTICAL_SLAB = ITEMS.register("green_mosaic_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.GREEN_MOSAIC_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_MOSAIC_GLASS_VERTICAL_SLAB = ITEMS.register("cyan_mosaic_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.CYAN_MOSAIC_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_MOSAIC_GLASS_VERTICAL_SLAB = ITEMS.register("light_blue_mosaic_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_MOSAIC_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_MOSAIC_GLASS_VERTICAL_SLAB = ITEMS.register("blue_mosaic_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.BLUE_MOSAIC_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_MOSAIC_GLASS_VERTICAL_SLAB = ITEMS.register("purple_mosaic_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.PURPLE_MOSAIC_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_MOSAIC_GLASS_VERTICAL_SLAB = ITEMS.register("magenta_mosaic_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.MAGENTA_MOSAIC_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_MOSAIC_GLASS_VERTICAL_SLAB = ITEMS.register("pink_mosaic_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.PINK_MOSAIC_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FACTORY_WHITE_GLASS_VERTICAL_SLAB = ITEMS.register("factory_white_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.FACTORY_WHITE_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FACTORY_LIGHT_GRAY_GLASS_VERTICAL_SLAB = ITEMS.register("factory_light_gray_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.FACTORY_LIGHT_GRAY_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FACTORY_GRAY_GLASS_VERTICAL_SLAB = ITEMS.register("factory_gray_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.FACTORY_GRAY_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FACTORY_BLACK_GLASS_VERTICAL_SLAB = ITEMS.register("factory_black_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.FACTORY_BLACK_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FACTORY_BROWN_GLASS_VERTICAL_SLAB = ITEMS.register("factory_brown_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.FACTORY_BROWN_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FACTORY_RED_GLASS_VERTICAL_SLAB = ITEMS.register("factory_red_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.FACTORY_RED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FACTORY_ORANGE_GLASS_VERTICAL_SLAB = ITEMS.register("factory_orange_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.FACTORY_ORANGE_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FACTORY_YELLOW_GLASS_VERTICAL_SLAB = ITEMS.register("factory_yellow_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.FACTORY_YELLOW_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FACTORY_LIME_GLASS_VERTICAL_SLAB = ITEMS.register("factory_lime_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.FACTORY_LIME_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FACTORY_GREEN_GLASS_VERTICAL_SLAB = ITEMS.register("factory_green_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.FACTORY_GREEN_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FACTORY_CYAN_GLASS_VERTICAL_SLAB = ITEMS.register("factory_cyan_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.FACTORY_CYAN_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FACTORY_LIGHT_BLUE_GLASS_VERTICAL_SLAB = ITEMS.register("factory_light_blue_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.FACTORY_LIGHT_BLUE_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FACTORY_BLUE_GLASS_VERTICAL_SLAB = ITEMS.register("factory_blue_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.FACTORY_BLUE_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FACTORY_PURPLE_GLASS_VERTICAL_SLAB = ITEMS.register("factory_purple_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.FACTORY_PURPLE_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FACTORY_MAGENTA_GLASS_VERTICAL_SLAB = ITEMS.register("factory_magenta_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.FACTORY_MAGENTA_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FACTORY_PINK_GLASS_VERTICAL_SLAB = ITEMS.register("factory_pink_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.FACTORY_PINK_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WHITE_GLAZED_GLASS_VERTICAL_SLAB = ITEMS.register("white_glazed_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.WHITE_GLAZED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_GLAZED_GLASS_VERTICAL_SLAB = ITEMS.register("light_gray_glazed_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_GLAZED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_GLAZED_GLASS_VERTICAL_SLAB = ITEMS.register("gray_glazed_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.GRAY_GLAZED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_GLAZED_GLASS_VERTICAL_SLAB = ITEMS.register("black_glazed_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.BLACK_GLAZED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_GLAZED_GLASS_VERTICAL_SLAB = ITEMS.register("brown_glazed_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.BROWN_GLAZED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_GLAZED_GLASS_VERTICAL_SLAB = ITEMS.register("red_glazed_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.RED_GLAZED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_GLAZED_GLASS_VERTICAL_SLAB = ITEMS.register("orange_glazed_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.ORANGE_GLAZED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_GLAZED_GLASS_VERTICAL_SLAB = ITEMS.register("yellow_glazed_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.YELLOW_GLAZED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_GLAZED_GLASS_VERTICAL_SLAB = ITEMS.register("lime_glazed_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.LIME_GLAZED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_GLAZED_GLASS_VERTICAL_SLAB = ITEMS.register("green_glazed_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.GREEN_GLAZED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_GLAZED_GLASS_VERTICAL_SLAB = ITEMS.register("cyan_glazed_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.CYAN_GLAZED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_GLAZED_GLASS_VERTICAL_SLAB = ITEMS.register("light_blue_glazed_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_GLAZED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_GLAZED_GLASS_VERTICAL_SLAB = ITEMS.register("blue_glazed_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.BLUE_GLAZED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_GLAZED_GLASS_VERTICAL_SLAB = ITEMS.register("purple_glazed_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.PURPLE_GLAZED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_GLAZED_GLASS_VERTICAL_SLAB = ITEMS.register("magenta_glazed_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.MAGENTA_GLAZED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_GLAZED_GLASS_VERTICAL_SLAB = ITEMS.register("pink_glazed_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.PINK_GLAZED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OAK_LOG_VERTICAL_SLAB = ITEMS.register("oak_log_vertical_slab",
            () -> new BlockItem(ModBlocks.OAK_LOG_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_OAK_LOG_VERTICAL_SLAB = ITEMS.register("stripped_oak_log_vertical_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_OAK_LOG_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SPRUCE_LOG_VERTICAL_SLAB = ITEMS.register("spruce_log_vertical_slab",
            () -> new BlockItem(ModBlocks.SPRUCE_LOG_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_SPRUCE_LOG_VERTICAL_SLAB = ITEMS.register("stripped_spruce_log_vertical_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_SPRUCE_LOG_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIRCH_LOG_VERTICAL_SLAB = ITEMS.register("birch_log_vertical_slab",
            () -> new BlockItem(ModBlocks.BIRCH_LOG_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_BIRCH_LOG_VERTICAL_SLAB = ITEMS.register("stripped_birch_log_vertical_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_BIRCH_LOG_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> JUNGLE_LOG_VERTICAL_SLAB = ITEMS.register("jungle_log_vertical_slab",
            () -> new BlockItem(ModBlocks.JUNGLE_LOG_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_JUNGLE_LOG_VERTICAL_SLAB = ITEMS.register("stripped_jungle_log_vertical_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_JUNGLE_LOG_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ACACIA_LOG_VERTICAL_SLAB = ITEMS.register("acacia_log_vertical_slab",
            () -> new BlockItem(ModBlocks.ACACIA_LOG_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_ACACIA_LOG_VERTICAL_SLAB = ITEMS.register("stripped_acacia_log_vertical_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_ACACIA_LOG_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> DARK_OAK_LOG_VERTICAL_SLAB = ITEMS.register("dark_oak_log_vertical_slab",
            () -> new BlockItem(ModBlocks.DARK_OAK_LOG_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_DARK_OAK_LOG_VERTICAL_SLAB = ITEMS.register("stripped_dark_oak_log_vertical_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_DARK_OAK_LOG_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CRIMSON_STEM_VERTICAL_SLAB = ITEMS.register("crimson_stem_vertical_slab",
            () -> new BlockItem(ModBlocks.CRIMSON_STEM_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_CRIMSON_STEM_VERTICAL_SLAB = ITEMS.register("stripped_crimson_stem_vertical_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_CRIMSON_STEM_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WARPED_STEM_VERTICAL_SLAB = ITEMS.register("warped_stem_vertical_slab",
            () -> new BlockItem(ModBlocks.WARPED_STEM_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_WARPED_STEM_VERTICAL_SLAB = ITEMS.register("stripped_warped_stem_vertical_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_WARPED_STEM_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MANGROVE_LOG_VERTICAL_SLAB = ITEMS.register("mangrove_log_vertical_slab",
            () -> new BlockItem(ModBlocks.MANGROVE_LOG_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_MANGROVE_LOG_VERTICAL_SLAB = ITEMS.register("stripped_mangrove_log_vertical_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_MANGROVE_LOG_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_LOG_VERTICAL_SLAB = ITEMS.register("ashpen_log_vertical_slab",
            () -> new BlockItem(ModBlocks.ASHPEN_LOG_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_WOOD_VERTICAL_SLAB = ITEMS.register("ashpen_wood_vertical_slab",
            () -> new BlockItem(ModBlocks.ASHPEN_WOOD_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_ASHPEN_LOG_VERTICAL_SLAB = ITEMS.register("stripped_ashpen_log_vertical_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_ASHPEN_LOG_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_ASHPEN_WOOD_VERTICAL_SLAB = ITEMS.register("stripped_ashpen_wood_vertical_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_ASHPEN_WOOD_VERTICAL_SLAB.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> SPOOL = ITEMS.register("spool", () -> new BlockItem(ModBlocks.SPOOL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GLOWING_SPOOL = ITEMS.register("glowing_spool", () -> new BlockItem(ModBlocks.GLOWING_SPOOL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WHITE_SPOOL = ITEMS.register("white_spool", () -> new BlockItem(ModBlocks.WHITE_SPOOL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_SPOOL = ITEMS.register("light_gray_spool", () -> new BlockItem(ModBlocks.LIGHT_GRAY_SPOOL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_SPOOL = ITEMS.register("gray_spool", () -> new BlockItem(ModBlocks.GRAY_SPOOL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_SPOOL = ITEMS.register("black_spool", () -> new BlockItem(ModBlocks.BLACK_SPOOL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_SPOOL = ITEMS.register("brown_spool", () -> new BlockItem(ModBlocks.BROWN_SPOOL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_SPOOL = ITEMS.register("red_spool", () -> new BlockItem(ModBlocks.RED_SPOOL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_SPOOL = ITEMS.register("orange_spool", () -> new BlockItem(ModBlocks.ORANGE_SPOOL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_SPOOL = ITEMS.register("yellow_spool", () -> new BlockItem(ModBlocks.YELLOW_SPOOL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_SPOOL = ITEMS.register("lime_spool", () -> new BlockItem(ModBlocks.LIME_SPOOL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_SPOOL = ITEMS.register("green_spool", () -> new BlockItem(ModBlocks.GREEN_SPOOL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_SPOOL = ITEMS.register("cyan_spool", () -> new BlockItem(ModBlocks.CYAN_SPOOL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_SPOOL = ITEMS.register("light_blue_spool", () -> new BlockItem(ModBlocks.LIGHT_BLUE_SPOOL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_SPOOL = ITEMS.register("blue_spool", () -> new BlockItem(ModBlocks.BLUE_SPOOL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_SPOOL = ITEMS.register("purple_spool", () -> new BlockItem(ModBlocks.PURPLE_SPOOL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_SPOOL = ITEMS.register("magenta_spool", () -> new BlockItem(ModBlocks.MAGENTA_SPOOL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_SPOOL = ITEMS.register("pink_spool", () -> new BlockItem(ModBlocks.PINK_SPOOL.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> WHITE_DYE_SACK = ITEMS.register("white_dye_sack", () -> new BlockItem(ModBlocks.WHITE_DYE_SACK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_DYE_SACK = ITEMS.register("light_gray_dye_sack", () -> new BlockItem(ModBlocks.LIGHT_GRAY_DYE_SACK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_DYE_SACK = ITEMS.register("gray_dye_sack", () -> new BlockItem(ModBlocks.GRAY_DYE_SACK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_DYE_SACK = ITEMS.register("black_dye_sack", () -> new BlockItem(ModBlocks.BLACK_DYE_SACK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_DYE_SACK = ITEMS.register("brown_dye_sack", () -> new BlockItem(ModBlocks.BROWN_DYE_SACK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_DYE_SACK = ITEMS.register("red_dye_sack", () -> new BlockItem(ModBlocks.RED_DYE_SACK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_DYE_SACK = ITEMS.register("orange_dye_sack", () -> new BlockItem(ModBlocks.ORANGE_DYE_SACK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_DYE_SACK = ITEMS.register("yellow_dye_sack", () -> new BlockItem(ModBlocks.YELLOW_DYE_SACK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_DYE_SACK = ITEMS.register("lime_dye_sack", () -> new BlockItem(ModBlocks.LIME_DYE_SACK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_DYE_SACK = ITEMS.register("green_dye_sack", () -> new BlockItem(ModBlocks.GREEN_DYE_SACK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_DYE_SACK = ITEMS.register("cyan_dye_sack", () -> new BlockItem(ModBlocks.CYAN_DYE_SACK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_DYE_SACK = ITEMS.register("light_blue_dye_sack", () -> new BlockItem(ModBlocks.LIGHT_BLUE_DYE_SACK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_DYE_SACK = ITEMS.register("blue_dye_sack", () -> new BlockItem(ModBlocks.BLUE_DYE_SACK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_DYE_SACK = ITEMS.register("purple_dye_sack", () -> new BlockItem(ModBlocks.PURPLE_DYE_SACK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_DYE_SACK = ITEMS.register("magenta_dye_sack", () -> new BlockItem(ModBlocks.MAGENTA_DYE_SACK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_DYE_SACK = ITEMS.register("pink_dye_sack", () -> new BlockItem(ModBlocks.PINK_DYE_SACK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GLOW_INK_SACK = ITEMS.register("glow_ink_sack", () -> new BlockItem(ModBlocks.GLOW_INK_SACK.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> RED_MOSS_BLOCK = ITEMS.register("red_moss_block", () -> new BlockItem(ModBlocks.RED_MOSS_BLOCK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_MOSS_BLOCK_SLAB = ITEMS.register("red_moss_block_slab", () -> new BlockItem(ModBlocks.RED_MOSS_BLOCK_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_MOSS_BLOCK_STAIRS = ITEMS.register("red_moss_block_stairs", () -> new BlockItem(ModBlocks.RED_MOSS_BLOCK_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_MOSS_LAYERS = ITEMS.register("red_moss_layers", () -> new BlockItem(ModBlocks.RED_MOSS_LAYERS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_MOSS_CARPET = ITEMS.register("red_moss_carpet", () -> new BlockItem(ModBlocks.RED_MOSS_CARPET.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_MOSS_OVERLAY = ITEMS.register("red_moss_overlay", () -> new BlockItem(ModBlocks.RED_MOSS_OVERLAY.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_MOSS_BLOCK = ITEMS.register("orange_moss_block", () -> new BlockItem(ModBlocks.ORANGE_MOSS_BLOCK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_MOSS_BLOCK_SLAB = ITEMS.register("orange_moss_block_slab", () -> new BlockItem(ModBlocks.ORANGE_MOSS_BLOCK_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_MOSS_BLOCK_STAIRS = ITEMS.register("orange_moss_block_stairs", () -> new BlockItem(ModBlocks.ORANGE_MOSS_BLOCK_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_MOSS_LAYERS = ITEMS.register("orange_moss_layers", () -> new BlockItem(ModBlocks.ORANGE_MOSS_LAYERS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_MOSS_CARPET = ITEMS.register("orange_moss_carpet", () -> new BlockItem(ModBlocks.ORANGE_MOSS_CARPET.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_MOSS_OVERLAY = ITEMS.register("orange_moss_overlay", () -> new BlockItem(ModBlocks.ORANGE_MOSS_OVERLAY.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_MOSS_BLOCK = ITEMS.register("yellow_moss_block", () -> new BlockItem(ModBlocks.YELLOW_MOSS_BLOCK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_MOSS_BLOCK_SLAB = ITEMS.register("yellow_moss_block_slab", () -> new BlockItem(ModBlocks.YELLOW_MOSS_BLOCK_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_MOSS_BLOCK_STAIRS = ITEMS.register("yellow_moss_block_stairs", () -> new BlockItem(ModBlocks.YELLOW_MOSS_BLOCK_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_MOSS_LAYERS = ITEMS.register("yellow_moss_layers", () -> new BlockItem(ModBlocks.YELLOW_MOSS_LAYERS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_MOSS_CARPET = ITEMS.register("yellow_moss_carpet", () -> new BlockItem(ModBlocks.YELLOW_MOSS_CARPET.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_MOSS_OVERLAY = ITEMS.register("yellow_moss_overlay", () -> new BlockItem(ModBlocks.YELLOW_MOSS_OVERLAY.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> TUFF_SLAB = ITEMS.register("tuff_slab", () -> new BlockItem(ModBlocks.TUFF_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> TUFF_STAIRS = ITEMS.register("tuff_stairs", () -> new BlockItem(ModBlocks.TUFF_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> TUFF_WALL = ITEMS.register("tuff_wall", () -> new BlockItem(ModBlocks.TUFF_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_TUFF = ITEMS.register("polished_tuff", () -> new BlockItem(ModBlocks.POLISHED_TUFF.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_TUFF_SLAB = ITEMS.register("polished_tuff_slab", () -> new BlockItem(ModBlocks.POLISHED_TUFF_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_TUFF_STAIRS = ITEMS.register("polished_tuff_stairs", () -> new BlockItem(ModBlocks.POLISHED_TUFF_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_TUFF_WALL = ITEMS.register("polished_tuff_wall", () -> new BlockItem(ModBlocks.POLISHED_TUFF_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CHISELED_TUFF = ITEMS.register("chiseled_tuff", () -> new BlockItem(ModBlocks.CHISELED_TUFF.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> TUFF_BRICKS = ITEMS.register("tuff_bricks", () -> new BlockItem(ModBlocks.TUFF_BRICKS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> TUFF_BRICK_SLAB = ITEMS.register("tuff_brick_slab", () -> new BlockItem(ModBlocks.TUFF_BRICK_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> TUFF_BRICK_STAIRS = ITEMS.register("tuff_brick_stairs", () -> new BlockItem(ModBlocks.TUFF_BRICK_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> TUFF_BRICK_WALL = ITEMS.register("tuff_brick_wall", () -> new BlockItem(ModBlocks.TUFF_BRICK_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CHISELED_TUFF_BRICKS = ITEMS.register("chiseled_tuff_bricks", () -> new BlockItem(ModBlocks.CHISELED_TUFF_BRICKS.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> WHITE_WOOL_SLAB = ITEMS.register("white_wool_slab", () -> new BlockItem(ModBlocks.WHITE_WOOL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WHITE_WOOL_STAIRS = ITEMS.register("white_wool_stairs", () -> new BlockItem(ModBlocks.WHITE_WOOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WHITE_WOOL_WALL = ITEMS.register("white_wool_wall", () -> new BlockItem(ModBlocks.WHITE_WOOL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_WOOL_SLAB = ITEMS.register("orange_wool_slab", () -> new BlockItem(ModBlocks.ORANGE_WOOL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_WOOL_STAIRS = ITEMS.register("orange_wool_stairs", () -> new BlockItem(ModBlocks.ORANGE_WOOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_WOOL_WALL = ITEMS.register("orange_wool_wall", () -> new BlockItem(ModBlocks.ORANGE_WOOL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_WOOL_SLAB = ITEMS.register("magenta_wool_slab", () -> new BlockItem(ModBlocks.MAGENTA_WOOL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_WOOL_STAIRS = ITEMS.register("magenta_wool_stairs", () -> new BlockItem(ModBlocks.MAGENTA_WOOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_WOOL_WALL = ITEMS.register("magenta_wool_wall", () -> new BlockItem(ModBlocks.MAGENTA_WOOL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_WOOL_SLAB = ITEMS.register("light_blue_wool_slab", () -> new BlockItem(ModBlocks.LIGHT_BLUE_WOOL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_WOOL_STAIRS = ITEMS.register("light_blue_wool_stairs", () -> new BlockItem(ModBlocks.LIGHT_BLUE_WOOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_WOOL_WALL = ITEMS.register("light_blue_wool_wall", () -> new BlockItem(ModBlocks.LIGHT_BLUE_WOOL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_WOOL_SLAB = ITEMS.register("yellow_wool_slab", () -> new BlockItem(ModBlocks.YELLOW_WOOL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_WOOL_STAIRS = ITEMS.register("yellow_wool_stairs", () -> new BlockItem(ModBlocks.YELLOW_WOOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_WOOL_WALL = ITEMS.register("yellow_wool_wall", () -> new BlockItem(ModBlocks.YELLOW_WOOL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_WOOL_SLAB = ITEMS.register("lime_wool_slab", () -> new BlockItem(ModBlocks.LIME_WOOL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_WOOL_STAIRS = ITEMS.register("lime_wool_stairs", () -> new BlockItem(ModBlocks.LIME_WOOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_WOOL_WALL = ITEMS.register("lime_wool_wall", () -> new BlockItem(ModBlocks.LIME_WOOL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_WOOL_SLAB = ITEMS.register("pink_wool_slab", () -> new BlockItem(ModBlocks.PINK_WOOL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_WOOL_STAIRS = ITEMS.register("pink_wool_stairs", () -> new BlockItem(ModBlocks.PINK_WOOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_WOOL_WALL = ITEMS.register("pink_wool_wall", () -> new BlockItem(ModBlocks.PINK_WOOL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_WOOL_SLAB = ITEMS.register("gray_wool_slab", () -> new BlockItem(ModBlocks.GRAY_WOOL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_WOOL_STAIRS = ITEMS.register("gray_wool_stairs", () -> new BlockItem(ModBlocks.GRAY_WOOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_WOOL_WALL = ITEMS.register("gray_wool_wall", () -> new BlockItem(ModBlocks.GRAY_WOOL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_WOOL_SLAB = ITEMS.register("light_gray_wool_slab", () -> new BlockItem(ModBlocks.LIGHT_GRAY_WOOL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_WOOL_STAIRS = ITEMS.register("light_gray_wool_stairs", () -> new BlockItem(ModBlocks.LIGHT_GRAY_WOOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_WOOL_WALL = ITEMS.register("light_gray_wool_wall", () -> new BlockItem(ModBlocks.LIGHT_GRAY_WOOL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_WOOL_SLAB = ITEMS.register("cyan_wool_slab", () -> new BlockItem(ModBlocks.CYAN_WOOL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_WOOL_STAIRS = ITEMS.register("cyan_wool_stairs", () -> new BlockItem(ModBlocks.CYAN_WOOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_WOOL_WALL = ITEMS.register("cyan_wool_wall", () -> new BlockItem(ModBlocks.CYAN_WOOL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_WOOL_SLAB = ITEMS.register("purple_wool_slab", () -> new BlockItem(ModBlocks.PURPLE_WOOL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_WOOL_STAIRS = ITEMS.register("purple_wool_stairs", () -> new BlockItem(ModBlocks.PURPLE_WOOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_WOOL_WALL = ITEMS.register("purple_wool_wall", () -> new BlockItem(ModBlocks.PURPLE_WOOL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_WOOL_SLAB = ITEMS.register("blue_wool_slab", () -> new BlockItem(ModBlocks.BLUE_WOOL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_WOOL_STAIRS = ITEMS.register("blue_wool_stairs", () -> new BlockItem(ModBlocks.BLUE_WOOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_WOOL_WALL = ITEMS.register("blue_wool_wall", () -> new BlockItem(ModBlocks.BLUE_WOOL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_WOOL_SLAB = ITEMS.register("brown_wool_slab", () -> new BlockItem(ModBlocks.BROWN_WOOL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_WOOL_STAIRS = ITEMS.register("brown_wool_stairs", () -> new BlockItem(ModBlocks.BROWN_WOOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_WOOL_WALL = ITEMS.register("brown_wool_wall", () -> new BlockItem(ModBlocks.BROWN_WOOL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_WOOL_SLAB = ITEMS.register("green_wool_slab", () -> new BlockItem(ModBlocks.GREEN_WOOL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_WOOL_STAIRS = ITEMS.register("green_wool_stairs", () -> new BlockItem(ModBlocks.GREEN_WOOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_WOOL_WALL = ITEMS.register("green_wool_wall", () -> new BlockItem(ModBlocks.GREEN_WOOL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_WOOL_SLAB = ITEMS.register("red_wool_slab", () -> new BlockItem(ModBlocks.RED_WOOL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_WOOL_STAIRS = ITEMS.register("red_wool_stairs", () -> new BlockItem(ModBlocks.RED_WOOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_WOOL_WALL = ITEMS.register("red_wool_wall", () -> new BlockItem(ModBlocks.RED_WOOL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_WOOL_SLAB = ITEMS.register("black_wool_slab", () -> new BlockItem(ModBlocks.BLACK_WOOL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_WOOL_STAIRS = ITEMS.register("black_wool_stairs", () -> new BlockItem(ModBlocks.BLACK_WOOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_WOOL_WALL = ITEMS.register("black_wool_wall", () -> new BlockItem(ModBlocks.BLACK_WOOL_WALL.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> POPLAR_LOG = ITEMS.register("poplar_log", () -> new BlockItem(ModBlocks.POPLAR_LOG.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POPLAR_PLANKS = ITEMS.register("poplar_planks", () -> new BlockItem(ModBlocks.POPLAR_PLANKS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POPLAR_SAPLING = ITEMS.register("poplar_sapling", () -> new BlockItem(ModBlocks.POPLAR_SAPLING.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POPLAR_BUTTON = ITEMS.register("poplar_button", () -> new BlockItem(ModBlocks.POPLAR_BUTTON.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POPLAR_DOOR = ITEMS.register("poplar_door", () -> new BlockItem(ModBlocks.POPLAR_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POPLAR_FENCE = ITEMS.register("poplar_fence", () -> new BlockItem(ModBlocks.POPLAR_FENCE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POPLAR_FENCE_GATE = ITEMS.register("poplar_fence_gate", () -> new BlockItem(ModBlocks.POPLAR_FENCE_GATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POPLAR_LOG_SLAB = ITEMS.register("poplar_log_slab", () -> new BlockItem(ModBlocks.POPLAR_LOG_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POPLAR_LOG_STAIRS = ITEMS.register("poplar_log_stairs", () -> new BlockItem(ModBlocks.POPLAR_LOG_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POPLAR_LOG_WALL = ITEMS.register("poplar_log_wall", () -> new BlockItem(ModBlocks.POPLAR_LOG_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POPLAR_PRESSURE_PLATE = ITEMS.register("poplar_pressure_plate", () -> new BlockItem(ModBlocks.POPLAR_PRESSURE_PLATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POPLAR_SLAB = ITEMS.register("poplar_slab", () -> new BlockItem(ModBlocks.POPLAR_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POPLAR_STAIRS = ITEMS.register("poplar_stairs", () -> new BlockItem(ModBlocks.POPLAR_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POPLAR_TRAPDOOR = ITEMS.register("poplar_trapdoor", () -> new BlockItem(ModBlocks.POPLAR_TRAPDOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POPLAR_WOOD = ITEMS.register("poplar_wood", () -> new BlockItem(ModBlocks.POPLAR_WOOD.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POPLAR_WOOD_SLAB = ITEMS.register("poplar_wood_slab", () -> new BlockItem(ModBlocks.POPLAR_WOOD_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POPLAR_WOOD_STAIRS = ITEMS.register("poplar_wood_stairs", () -> new BlockItem(ModBlocks.POPLAR_WOOD_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_POPLAR_LOG = ITEMS.register("stripped_poplar_log", () -> new BlockItem(ModBlocks.STRIPPED_POPLAR_LOG.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_POPLAR_LOG_SLAB = ITEMS.register("stripped_poplar_log_slab", () -> new BlockItem(ModBlocks.STRIPPED_POPLAR_LOG_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_POPLAR_LOG_STAIRS = ITEMS.register("stripped_poplar_log_stairs", () -> new BlockItem(ModBlocks.STRIPPED_POPLAR_LOG_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_POPLAR_LOG_WALL = ITEMS.register("stripped_poplar_log_wall", () -> new BlockItem(ModBlocks.STRIPPED_POPLAR_LOG_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_POPLAR_WOOD = ITEMS.register("stripped_poplar_wood", () -> new BlockItem(ModBlocks.STRIPPED_POPLAR_WOOD.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_POPLAR_WOOD_SLAB = ITEMS.register("stripped_poplar_wood_slab", () -> new BlockItem(ModBlocks.STRIPPED_POPLAR_WOOD_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_POPLAR_WOOD_STAIRS = ITEMS.register("stripped_poplar_wood_stairs", () -> new BlockItem(ModBlocks.STRIPPED_POPLAR_WOOD_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POPLAR_BOAT = ITEMS.register("poplar_boat", () -> new PoplarBoatItem(new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(1)));

    public static final RegistryObject<Item> ORANGE_POPLAR_LEAVES = ITEMS.register("orange_poplar_leaves", () -> new BlockItem(ModBlocks.ORANGE_POPLAR_LEAVES.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_POPLAR_LEAVES = ITEMS.register("red_poplar_leaves", () -> new BlockItem(ModBlocks.RED_POPLAR_LEAVES.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_POPLAR_LEAVES = ITEMS.register("yellow_poplar_leaves", () -> new BlockItem(ModBlocks.YELLOW_POPLAR_LEAVES.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> CINNABAR = ITEMS.register("cinnabar", () -> new BlockItem(ModBlocks.CINNABAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_CINNABAR = ITEMS.register("polished_cinnabar", () -> new BlockItem(ModBlocks.POLISHED_CINNABAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CINNABAR_SLAB = ITEMS.register("cinnabar_slab", () -> new BlockItem(ModBlocks.CINNABAR_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CINNABAR_STAIRS = ITEMS.register("cinnabar_stairs", () -> new BlockItem(ModBlocks.CINNABAR_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CINNABAR_WALL = ITEMS.register("cinnabar_wall", () -> new BlockItem(ModBlocks.CINNABAR_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_CINNABAR_SLAB = ITEMS.register("polished_cinnabar_slab", () -> new BlockItem(ModBlocks.POLISHED_CINNABAR_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_CINNABAR_STAIRS = ITEMS.register("polished_cinnabar_stairs", () -> new BlockItem(ModBlocks.POLISHED_CINNABAR_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_CINNABAR_WALL = ITEMS.register("polished_cinnabar_wall", () -> new BlockItem(ModBlocks.POLISHED_CINNABAR_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CINNABAR_BRICKS = ITEMS.register("cinnabar_bricks", () -> new BlockItem(ModBlocks.CINNABAR_BRICKS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CINNABAR_BRICKS_SLAB = ITEMS.register("cinnabar_bricks_slab", () -> new BlockItem(ModBlocks.CINNABAR_BRICKS_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CINNABAR_BRICKS_STAIRS = ITEMS.register("cinnabar_bricks_stairs", () -> new BlockItem(ModBlocks.CINNABAR_BRICKS_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CINNABAR_BRICKS_WALL = ITEMS.register("cinnabar_bricks_wall", () -> new BlockItem(ModBlocks.CINNABAR_BRICKS_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CHISELED_CINNABAR = ITEMS.register("chiseled_cinnabar", () -> new BlockItem(ModBlocks.CHISELED_CINNABAR.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> SULFUR = ITEMS.register("sulfur", () -> new BlockItem(ModBlocks.SULFUR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_SULFUR = ITEMS.register("polished_sulfur", () -> new BlockItem(ModBlocks.POLISHED_SULFUR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SULFUR_SLAB = ITEMS.register("sulfur_slab", () -> new BlockItem(ModBlocks.SULFUR_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SULFUR_STAIRS = ITEMS.register("sulfur_stairs", () -> new BlockItem(ModBlocks.SULFUR_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SULFUR_WALL = ITEMS.register("sulfur_wall", () -> new BlockItem(ModBlocks.SULFUR_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_SULFUR_SLAB = ITEMS.register("polished_sulfur_slab", () -> new BlockItem(ModBlocks.POLISHED_SULFUR_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_SULFUR_STAIRS = ITEMS.register("polished_sulfur_stairs", () -> new BlockItem(ModBlocks.POLISHED_SULFUR_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_SULFUR_WALL = ITEMS.register("polished_sulfur_wall", () -> new BlockItem(ModBlocks.POLISHED_SULFUR_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SULFUR_BRICKS = ITEMS.register("sulfur_bricks", () -> new BlockItem(ModBlocks.SULFUR_BRICKS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SULFUR_BRICKS_SLAB = ITEMS.register("sulfur_bricks_slab", () -> new BlockItem(ModBlocks.SULFUR_BRICKS_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SULFUR_BRICKS_STAIRS = ITEMS.register("sulfur_bricks_stairs", () -> new BlockItem(ModBlocks.SULFUR_BRICKS_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SULFUR_BRICKS_WALL = ITEMS.register("sulfur_bricks_wall", () -> new BlockItem(ModBlocks.SULFUR_BRICKS_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CHISELED_SULFUR = ITEMS.register("chiseled_sulfur", () -> new BlockItem(ModBlocks.CHISELED_SULFUR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POTENT_SULFUR = ITEMS.register("potent_sulfur", () -> new BlockItem(ModBlocks.POTENT_SULFUR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SULFUR_SPIKE = ITEMS.register("sulfur_spike", () -> new BlockItem(ModBlocks.SULFUR_SPIKE.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> WHITE_WOOL_VERTICAL_SLAB = ITEMS.register("white_wool_vertical_slab",
            () -> new BlockItem(ModBlocks.WHITE_WOOL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_WOOL_VERTICAL_SLAB = ITEMS.register("orange_wool_vertical_slab",
            () -> new BlockItem(ModBlocks.ORANGE_WOOL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_WOOL_VERTICAL_SLAB = ITEMS.register("magenta_wool_vertical_slab",
            () -> new BlockItem(ModBlocks.MAGENTA_WOOL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_WOOL_VERTICAL_SLAB = ITEMS.register("light_blue_wool_vertical_slab",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_WOOL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_WOOL_VERTICAL_SLAB = ITEMS.register("yellow_wool_vertical_slab",
            () -> new BlockItem(ModBlocks.YELLOW_WOOL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_WOOL_VERTICAL_SLAB = ITEMS.register("lime_wool_vertical_slab",
            () -> new BlockItem(ModBlocks.LIME_WOOL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_WOOL_VERTICAL_SLAB = ITEMS.register("pink_wool_vertical_slab",
            () -> new BlockItem(ModBlocks.PINK_WOOL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_WOOL_VERTICAL_SLAB = ITEMS.register("gray_wool_vertical_slab",
            () -> new BlockItem(ModBlocks.GRAY_WOOL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_WOOL_VERTICAL_SLAB = ITEMS.register("light_gray_wool_vertical_slab",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_WOOL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_WOOL_VERTICAL_SLAB = ITEMS.register("cyan_wool_vertical_slab",
            () -> new BlockItem(ModBlocks.CYAN_WOOL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_WOOL_VERTICAL_SLAB = ITEMS.register("purple_wool_vertical_slab",
            () -> new BlockItem(ModBlocks.PURPLE_WOOL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_WOOL_VERTICAL_SLAB = ITEMS.register("blue_wool_vertical_slab",
            () -> new BlockItem(ModBlocks.BLUE_WOOL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_WOOL_VERTICAL_SLAB = ITEMS.register("brown_wool_vertical_slab",
            () -> new BlockItem(ModBlocks.BROWN_WOOL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_WOOL_VERTICAL_SLAB = ITEMS.register("green_wool_vertical_slab",
            () -> new BlockItem(ModBlocks.GREEN_WOOL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_WOOL_VERTICAL_SLAB = ITEMS.register("red_wool_vertical_slab",
            () -> new BlockItem(ModBlocks.RED_WOOL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_WOOL_VERTICAL_SLAB = ITEMS.register("black_wool_vertical_slab",
            () -> new BlockItem(ModBlocks.BLACK_WOOL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_MOSS_BLOCK_VERTICAL_SLAB = ITEMS.register("red_moss_block_vertical_slab",
            () -> new BlockItem(ModBlocks.RED_MOSS_BLOCK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_MOSS_BLOCK_VERTICAL_SLAB = ITEMS.register("orange_moss_block_vertical_slab",
            () -> new BlockItem(ModBlocks.ORANGE_MOSS_BLOCK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_MOSS_BLOCK_VERTICAL_SLAB = ITEMS.register("yellow_moss_block_vertical_slab",
            () -> new BlockItem(ModBlocks.YELLOW_MOSS_BLOCK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> TUFF_VERTICAL_SLAB = ITEMS.register("tuff_vertical_slab",
            () -> new BlockItem(ModBlocks.TUFF_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_TUFF_VERTICAL_SLAB = ITEMS.register("polished_tuff_vertical_slab",
            () -> new BlockItem(ModBlocks.POLISHED_TUFF_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> TUFF_BRICK_VERTICAL_SLAB = ITEMS.register("tuff_brick_vertical_slab",
            () -> new BlockItem(ModBlocks.TUFF_BRICK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SULFUR_VERTICAL_SLAB = ITEMS.register("sulfur_vertical_slab",
            () -> new BlockItem(ModBlocks.SULFUR_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_SULFUR_VERTICAL_SLAB = ITEMS.register("polished_sulfur_vertical_slab",
            () -> new BlockItem(ModBlocks.POLISHED_SULFUR_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SULFUR_BRICKS_VERTICAL_SLAB = ITEMS.register("sulfur_bricks_vertical_slab",
            () -> new BlockItem(ModBlocks.SULFUR_BRICKS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CINNABAR_VERTICAL_SLAB = ITEMS.register("cinnabar_vertical_slab",
            () -> new BlockItem(ModBlocks.CINNABAR_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_CINNABAR_VERTICAL_SLAB = ITEMS.register("polished_cinnabar_vertical_slab",
            () -> new BlockItem(ModBlocks.POLISHED_CINNABAR_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CINNABAR_BRICKS_VERTICAL_SLAB = ITEMS.register("cinnabar_bricks_vertical_slab",
            () -> new BlockItem(ModBlocks.CINNABAR_BRICKS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POPLAR_VERTICAL_SLAB = ITEMS.register("poplar_vertical_slab",
            () -> new BlockItem(ModBlocks.POPLAR_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POPLAR_LOG_VERTICAL_SLAB = ITEMS.register("poplar_log_vertical_slab",
            () -> new BlockItem(ModBlocks.POPLAR_LOG_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_POPLAR_LOG_VERTICAL_SLAB = ITEMS.register("stripped_poplar_log_vertical_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_POPLAR_LOG_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POPLAR_WOOD_VERTICAL_SLAB = ITEMS.register("poplar_wood_vertical_slab",
            () -> new BlockItem(ModBlocks.POPLAR_WOOD_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_POPLAR_WOOD_VERTICAL_SLAB = ITEMS.register("stripped_poplar_wood_vertical_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_POPLAR_WOOD_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OAK_VERTICAL_SLAB = ITEMS.register("oak_vertical_slab",
            () -> new BlockItem(ModBlocks.OAK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SPRUCE_VERTICAL_SLAB = ITEMS.register("spruce_vertical_slab",
            () -> new BlockItem(ModBlocks.SPRUCE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIRCH_VERTICAL_SLAB = ITEMS.register("birch_vertical_slab",
            () -> new BlockItem(ModBlocks.BIRCH_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> JUNGLE_VERTICAL_SLAB = ITEMS.register("jungle_vertical_slab",
            () -> new BlockItem(ModBlocks.JUNGLE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ACACIA_VERTICAL_SLAB = ITEMS.register("acacia_vertical_slab",
            () -> new BlockItem(ModBlocks.ACACIA_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> DARK_OAK_VERTICAL_SLAB = ITEMS.register("dark_oak_vertical_slab",
            () -> new BlockItem(ModBlocks.DARK_OAK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CRIMSON_VERTICAL_SLAB = ITEMS.register("crimson_vertical_slab",
            () -> new BlockItem(ModBlocks.CRIMSON_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WARPED_VERTICAL_SLAB = ITEMS.register("warped_vertical_slab",
            () -> new BlockItem(ModBlocks.WARPED_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STONE_VERTICAL_SLAB = ITEMS.register("stone_vertical_slab",
            () -> new BlockItem(ModBlocks.STONE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SMOOTH_STONE_VERTICAL_SLAB = ITEMS.register("smooth_stone_vertical_slab",
            () -> new BlockItem(ModBlocks.SMOOTH_STONE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRANITE_VERTICAL_SLAB = ITEMS.register("granite_vertical_slab",
            () -> new BlockItem(ModBlocks.GRANITE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_GRANITE_VERTICAL_SLAB = ITEMS.register("polished_granite_vertical_slab",
            () -> new BlockItem(ModBlocks.POLISHED_GRANITE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> DIORITE_VERTICAL_SLAB = ITEMS.register("diorite_vertical_slab",
            () -> new BlockItem(ModBlocks.DIORITE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_DIORITE_VERTICAL_SLAB = ITEMS.register("polished_diorite_vertical_slab",
            () -> new BlockItem(ModBlocks.POLISHED_DIORITE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ANDESITE_VERTICAL_SLAB = ITEMS.register("andesite_vertical_slab",
            () -> new BlockItem(ModBlocks.ANDESITE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_ANDESITE_VERTICAL_SLAB = ITEMS.register("polished_andesite_vertical_slab",
            () -> new BlockItem(ModBlocks.POLISHED_ANDESITE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> COBBLESTONE_VERTICAL_SLAB = ITEMS.register("cobblestone_vertical_slab",
            () -> new BlockItem(ModBlocks.COBBLESTONE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MOSSY_COBBLESTONE_VERTICAL_SLAB = ITEMS.register("mossy_cobblestone_vertical_slab",
            () -> new BlockItem(ModBlocks.MOSSY_COBBLESTONE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STONE_BRICK_VERTICAL_SLAB = ITEMS.register("stone_brick_vertical_slab",
            () -> new BlockItem(ModBlocks.STONE_BRICK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MOSSY_STONE_BRICK_VERTICAL_SLAB = ITEMS.register("mossy_stone_brick_vertical_slab",
            () -> new BlockItem(ModBlocks.MOSSY_STONE_BRICK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> NETHER_BRICK_VERTICAL_SLAB = ITEMS.register("nether_brick_vertical_slab",
            () -> new BlockItem(ModBlocks.NETHER_BRICK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_NETHER_BRICK_VERTICAL_SLAB = ITEMS.register("red_nether_brick_vertical_slab",
            () -> new BlockItem(ModBlocks.RED_NETHER_BRICK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OAK_LOG_STAIRS = ITEMS.register("oak_log_stairs",
            () -> new BlockItem(ModBlocks.OAK_LOG_STAIRS.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> CUT_COPPER_VERTICAL_SLAB = ITEMS.register("cut_copper_vertical_slab",
            () -> new BlockItem(ModBlocks.CUT_COPPER_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> EXPOSED_CUT_COPPER_VERTICAL_SLAB = ITEMS.register("exposed_cut_copper_vertical_slab",
            () -> new BlockItem(ModBlocks.EXPOSED_CUT_COPPER_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WEATHERED_CUT_COPPER_VERTICAL_SLAB = ITEMS.register("weathered_cut_copper_vertical_slab",
            () -> new BlockItem(ModBlocks.WEATHERED_CUT_COPPER_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OXIDIZED_CUT_COPPER_VERTICAL_SLAB = ITEMS.register("oxidized_cut_copper_vertical_slab",
            () -> new BlockItem(ModBlocks.OXIDIZED_CUT_COPPER_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_CUT_COPPER_VERTICAL_SLAB = ITEMS.register("waxed_cut_copper_vertical_slab",
            () -> new BlockItem(ModBlocks.WAXED_CUT_COPPER_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_EXPOSED_CUT_COPPER_VERTICAL_SLAB = ITEMS.register("waxed_exposed_cut_copper_vertical_slab",
            () -> new BlockItem(ModBlocks.WAXED_EXPOSED_CUT_COPPER_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_WEATHERED_CUT_COPPER_VERTICAL_SLAB = ITEMS.register("waxed_weathered_cut_copper_vertical_slab",
            () -> new BlockItem(ModBlocks.WAXED_WEATHERED_CUT_COPPER_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_OXIDIZED_CUT_COPPER_VERTICAL_SLAB = ITEMS.register("waxed_oxidized_cut_copper_vertical_slab",
            () -> new BlockItem(ModBlocks.WAXED_OXIDIZED_CUT_COPPER_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> COBBLED_DEEPSLATE_VERTICAL_SLAB = ITEMS.register("cobbled_deepslate_vertical_slab",
            () -> new BlockItem(ModBlocks.COBBLED_DEEPSLATE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_DEEPSLATE_VERTICAL_SLAB = ITEMS.register("polished_deepslate_vertical_slab",
            () -> new BlockItem(ModBlocks.POLISHED_DEEPSLATE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> DEEPSLATE_BRICK_VERTICAL_SLAB = ITEMS.register("deepslate_brick_vertical_slab",
            () -> new BlockItem(ModBlocks.DEEPSLATE_BRICK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> DEEPSLATE_TILE_VERTICAL_SLAB = ITEMS.register("deepslate_tile_vertical_slab",
            () -> new BlockItem(ModBlocks.DEEPSLATE_TILE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACKSTONE_VERTICAL_SLAB = ITEMS.register("blackstone_vertical_slab",
            () -> new BlockItem(ModBlocks.BLACKSTONE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_BLACKSTONE_VERTICAL_SLAB = ITEMS.register("polished_blackstone_vertical_slab",
            () -> new BlockItem(ModBlocks.POLISHED_BLACKSTONE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POLISHED_BLACKSTONE_BRICK_VERTICAL_SLAB = ITEMS.register("polished_blackstone_brick_vertical_slab",
            () -> new BlockItem(ModBlocks.POLISHED_BLACKSTONE_BRICK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PRISMARINE_VERTICAL_SLAB = ITEMS.register("prismarine_vertical_slab",
            () -> new BlockItem(ModBlocks.PRISMARINE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PRISMARINE_BRICK_VERTICAL_SLAB = ITEMS.register("prismarine_brick_vertical_slab",
            () -> new BlockItem(ModBlocks.PRISMARINE_BRICK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> DARK_PRISMARINE_VERTICAL_SLAB = ITEMS.register("dark_prismarine_vertical_slab",
            () -> new BlockItem(ModBlocks.DARK_PRISMARINE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BRICK_VERTICAL_SLAB = ITEMS.register("brick_vertical_slab",
            () -> new BlockItem(ModBlocks.BRICK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> END_STONE_BRICK_VERTICAL_SLAB = ITEMS.register("end_stone_brick_vertical_slab",
            () -> new BlockItem(ModBlocks.END_STONE_BRICK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPUR_VERTICAL_SLAB = ITEMS.register("purpur_vertical_slab",
            () -> new BlockItem(ModBlocks.PURPUR_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> QUARTZ_VERTICAL_SLAB = ITEMS.register("quartz_vertical_slab",
            () -> new BlockItem(ModBlocks.QUARTZ_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SMOOTH_QUARTZ_VERTICAL_SLAB = ITEMS.register("smooth_quartz_vertical_slab",
            () -> new BlockItem(ModBlocks.SMOOTH_QUARTZ_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> TERRACOTTA_SLAB = ITEMS.register("terracotta_slab",
            () -> new BlockItem(ModBlocks.TERRACOTTA_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> TERRACOTTA_STAIRS = ITEMS.register("terracotta_stairs",
            () -> new BlockItem(ModBlocks.TERRACOTTA_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> TERRACOTTA_VERTICAL_SLAB = ITEMS.register("terracotta_vertical_slab",
            () -> new BlockItem(ModBlocks.TERRACOTTA_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WHITE_TERRACOTTA_SLAB = ITEMS.register("white_terracotta_slab",
            () -> new BlockItem(ModBlocks.WHITE_TERRACOTTA_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WHITE_TERRACOTTA_STAIRS = ITEMS.register("white_terracotta_stairs",
            () -> new BlockItem(ModBlocks.WHITE_TERRACOTTA_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WHITE_TERRACOTTA_VERTICAL_SLAB = ITEMS.register("white_terracotta_vertical_slab",
            () -> new BlockItem(ModBlocks.WHITE_TERRACOTTA_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_TERRACOTTA_SLAB = ITEMS.register("orange_terracotta_slab",
            () -> new BlockItem(ModBlocks.ORANGE_TERRACOTTA_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_TERRACOTTA_STAIRS = ITEMS.register("orange_terracotta_stairs",
            () -> new BlockItem(ModBlocks.ORANGE_TERRACOTTA_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_TERRACOTTA_VERTICAL_SLAB = ITEMS.register("orange_terracotta_vertical_slab",
            () -> new BlockItem(ModBlocks.ORANGE_TERRACOTTA_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_TERRACOTTA_SLAB = ITEMS.register("magenta_terracotta_slab",
            () -> new BlockItem(ModBlocks.MAGENTA_TERRACOTTA_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_TERRACOTTA_STAIRS = ITEMS.register("magenta_terracotta_stairs",
            () -> new BlockItem(ModBlocks.MAGENTA_TERRACOTTA_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_TERRACOTTA_VERTICAL_SLAB = ITEMS.register("magenta_terracotta_vertical_slab",
            () -> new BlockItem(ModBlocks.MAGENTA_TERRACOTTA_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_TERRACOTTA_SLAB = ITEMS.register("light_blue_terracotta_slab",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_TERRACOTTA_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_TERRACOTTA_STAIRS = ITEMS.register("light_blue_terracotta_stairs",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_TERRACOTTA_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_TERRACOTTA_VERTICAL_SLAB = ITEMS.register("light_blue_terracotta_vertical_slab",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_TERRACOTTA_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_TERRACOTTA_SLAB = ITEMS.register("yellow_terracotta_slab",
            () -> new BlockItem(ModBlocks.YELLOW_TERRACOTTA_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_TERRACOTTA_STAIRS = ITEMS.register("yellow_terracotta_stairs",
            () -> new BlockItem(ModBlocks.YELLOW_TERRACOTTA_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_TERRACOTTA_VERTICAL_SLAB = ITEMS.register("yellow_terracotta_vertical_slab",
            () -> new BlockItem(ModBlocks.YELLOW_TERRACOTTA_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_TERRACOTTA_SLAB = ITEMS.register("lime_terracotta_slab",
            () -> new BlockItem(ModBlocks.LIME_TERRACOTTA_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_TERRACOTTA_STAIRS = ITEMS.register("lime_terracotta_stairs",
            () -> new BlockItem(ModBlocks.LIME_TERRACOTTA_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_TERRACOTTA_VERTICAL_SLAB = ITEMS.register("lime_terracotta_vertical_slab",
            () -> new BlockItem(ModBlocks.LIME_TERRACOTTA_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_TERRACOTTA_SLAB = ITEMS.register("pink_terracotta_slab",
            () -> new BlockItem(ModBlocks.PINK_TERRACOTTA_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_TERRACOTTA_STAIRS = ITEMS.register("pink_terracotta_stairs",
            () -> new BlockItem(ModBlocks.PINK_TERRACOTTA_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_TERRACOTTA_VERTICAL_SLAB = ITEMS.register("pink_terracotta_vertical_slab",
            () -> new BlockItem(ModBlocks.PINK_TERRACOTTA_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_TERRACOTTA_SLAB = ITEMS.register("gray_terracotta_slab",
            () -> new BlockItem(ModBlocks.GRAY_TERRACOTTA_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_TERRACOTTA_STAIRS = ITEMS.register("gray_terracotta_stairs",
            () -> new BlockItem(ModBlocks.GRAY_TERRACOTTA_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_TERRACOTTA_VERTICAL_SLAB = ITEMS.register("gray_terracotta_vertical_slab",
            () -> new BlockItem(ModBlocks.GRAY_TERRACOTTA_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_TERRACOTTA_SLAB = ITEMS.register("light_gray_terracotta_slab",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_TERRACOTTA_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_TERRACOTTA_STAIRS = ITEMS.register("light_gray_terracotta_stairs",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_TERRACOTTA_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_TERRACOTTA_VERTICAL_SLAB = ITEMS.register("light_gray_terracotta_vertical_slab",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_TERRACOTTA_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_TERRACOTTA_SLAB = ITEMS.register("cyan_terracotta_slab",
            () -> new BlockItem(ModBlocks.CYAN_TERRACOTTA_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_TERRACOTTA_STAIRS = ITEMS.register("cyan_terracotta_stairs",
            () -> new BlockItem(ModBlocks.CYAN_TERRACOTTA_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_TERRACOTTA_VERTICAL_SLAB = ITEMS.register("cyan_terracotta_vertical_slab",
            () -> new BlockItem(ModBlocks.CYAN_TERRACOTTA_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_TERRACOTTA_SLAB = ITEMS.register("purple_terracotta_slab",
            () -> new BlockItem(ModBlocks.PURPLE_TERRACOTTA_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_TERRACOTTA_STAIRS = ITEMS.register("purple_terracotta_stairs",
            () -> new BlockItem(ModBlocks.PURPLE_TERRACOTTA_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_TERRACOTTA_VERTICAL_SLAB = ITEMS.register("purple_terracotta_vertical_slab",
            () -> new BlockItem(ModBlocks.PURPLE_TERRACOTTA_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_TERRACOTTA_SLAB = ITEMS.register("blue_terracotta_slab",
            () -> new BlockItem(ModBlocks.BLUE_TERRACOTTA_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_TERRACOTTA_STAIRS = ITEMS.register("blue_terracotta_stairs",
            () -> new BlockItem(ModBlocks.BLUE_TERRACOTTA_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_TERRACOTTA_VERTICAL_SLAB = ITEMS.register("blue_terracotta_vertical_slab",
            () -> new BlockItem(ModBlocks.BLUE_TERRACOTTA_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_TERRACOTTA_SLAB = ITEMS.register("brown_terracotta_slab",
            () -> new BlockItem(ModBlocks.BROWN_TERRACOTTA_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_TERRACOTTA_STAIRS = ITEMS.register("brown_terracotta_stairs",
            () -> new BlockItem(ModBlocks.BROWN_TERRACOTTA_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_TERRACOTTA_VERTICAL_SLAB = ITEMS.register("brown_terracotta_vertical_slab",
            () -> new BlockItem(ModBlocks.BROWN_TERRACOTTA_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_TERRACOTTA_SLAB = ITEMS.register("green_terracotta_slab",
            () -> new BlockItem(ModBlocks.GREEN_TERRACOTTA_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_TERRACOTTA_STAIRS = ITEMS.register("green_terracotta_stairs",
            () -> new BlockItem(ModBlocks.GREEN_TERRACOTTA_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_TERRACOTTA_VERTICAL_SLAB = ITEMS.register("green_terracotta_vertical_slab",
            () -> new BlockItem(ModBlocks.GREEN_TERRACOTTA_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_TERRACOTTA_SLAB = ITEMS.register("red_terracotta_slab",
            () -> new BlockItem(ModBlocks.RED_TERRACOTTA_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_TERRACOTTA_STAIRS = ITEMS.register("red_terracotta_stairs",
            () -> new BlockItem(ModBlocks.RED_TERRACOTTA_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_TERRACOTTA_VERTICAL_SLAB = ITEMS.register("red_terracotta_vertical_slab",
            () -> new BlockItem(ModBlocks.RED_TERRACOTTA_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_TERRACOTTA_SLAB = ITEMS.register("black_terracotta_slab",
            () -> new BlockItem(ModBlocks.BLACK_TERRACOTTA_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_TERRACOTTA_STAIRS = ITEMS.register("black_terracotta_stairs",
            () -> new BlockItem(ModBlocks.BLACK_TERRACOTTA_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_TERRACOTTA_VERTICAL_SLAB = ITEMS.register("black_terracotta_vertical_slab",
            () -> new BlockItem(ModBlocks.BLACK_TERRACOTTA_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_OAK_LOG_STAIRS = ITEMS.register("stripped_oak_log_stairs",
            () -> new BlockItem(ModBlocks.STRIPPED_OAK_LOG_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SPRUCE_LOG_STAIRS = ITEMS.register("spruce_log_stairs",
            () -> new BlockItem(ModBlocks.SPRUCE_LOG_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_SPRUCE_LOG_STAIRS = ITEMS.register("stripped_spruce_log_stairs",
            () -> new BlockItem(ModBlocks.STRIPPED_SPRUCE_LOG_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIRCH_LOG_STAIRS = ITEMS.register("birch_log_stairs",
            () -> new BlockItem(ModBlocks.BIRCH_LOG_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_BIRCH_LOG_STAIRS = ITEMS.register("stripped_birch_log_stairs",
            () -> new BlockItem(ModBlocks.STRIPPED_BIRCH_LOG_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> JUNGLE_LOG_STAIRS = ITEMS.register("jungle_log_stairs",
            () -> new BlockItem(ModBlocks.JUNGLE_LOG_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_JUNGLE_LOG_STAIRS = ITEMS.register("stripped_jungle_log_stairs",
            () -> new BlockItem(ModBlocks.STRIPPED_JUNGLE_LOG_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ACACIA_LOG_STAIRS = ITEMS.register("acacia_log_stairs",
            () -> new BlockItem(ModBlocks.ACACIA_LOG_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_ACACIA_LOG_STAIRS = ITEMS.register("stripped_acacia_log_stairs",
            () -> new BlockItem(ModBlocks.STRIPPED_ACACIA_LOG_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> DARK_OAK_LOG_STAIRS = ITEMS.register("dark_oak_log_stairs",
            () -> new BlockItem(ModBlocks.DARK_OAK_LOG_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_DARK_OAK_LOG_STAIRS = ITEMS.register("stripped_dark_oak_log_stairs",
            () -> new BlockItem(ModBlocks.STRIPPED_DARK_OAK_LOG_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CRIMSON_STEM_STAIRS = ITEMS.register("crimson_stem_stairs",
            () -> new BlockItem(ModBlocks.CRIMSON_STEM_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_CRIMSON_STEM_STAIRS = ITEMS.register("stripped_crimson_stem_stairs",
            () -> new BlockItem(ModBlocks.STRIPPED_CRIMSON_STEM_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WARPED_STEM_STAIRS = ITEMS.register("warped_stem_stairs",
            () -> new BlockItem(ModBlocks.WARPED_STEM_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_WARPED_STEM_STAIRS = ITEMS.register("stripped_warped_stem_stairs",
            () -> new BlockItem(ModBlocks.STRIPPED_WARPED_STEM_STAIRS.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> ACACIA_WOOD_SLAB = ITEMS.register("acacia_wood_slab",
            () -> new BlockItem(ModBlocks.ACACIA_WOOD_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ACACIA_WOOD_STAIRS = ITEMS.register("acacia_wood_stairs",
            () -> new BlockItem(ModBlocks.ACACIA_WOOD_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ACACIA_WOOD_VERTICAL_SLAB = ITEMS.register("acacia_wood_vertical_slab",
            () -> new BlockItem(ModBlocks.ACACIA_WOOD_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> AMETHYST_BLOCK_STAIRS = ITEMS.register("amethyst_block_stairs",
            () -> new BlockItem(ModBlocks.AMETHYST_BLOCK_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIRCH_WOOD_SLAB = ITEMS.register("birch_wood_slab",
            () -> new BlockItem(ModBlocks.BIRCH_WOOD_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIRCH_WOOD_STAIRS = ITEMS.register("birch_wood_stairs",
            () -> new BlockItem(ModBlocks.BIRCH_WOOD_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIRCH_WOOD_VERTICAL_SLAB = ITEMS.register("birch_wood_vertical_slab",
            () -> new BlockItem(ModBlocks.BIRCH_WOOD_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CRIMSON_HYPHAE_SLAB = ITEMS.register("crimson_hyphae_slab",
            () -> new BlockItem(ModBlocks.CRIMSON_HYPHAE_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CRIMSON_HYPHAE_STAIRS = ITEMS.register("crimson_hyphae_stairs",
            () -> new BlockItem(ModBlocks.CRIMSON_HYPHAE_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CRIMSON_HYPHAE_VERTICAL_SLAB = ITEMS.register("crimson_hyphae_vertical_slab",
            () -> new BlockItem(ModBlocks.CRIMSON_HYPHAE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CRYING_OBSIDIAN_SLAB = ITEMS.register("crying_obsidian_slab",
            () -> new BlockItem(ModBlocks.CRYING_OBSIDIAN_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CRYING_OBSIDIAN_STAIRS = ITEMS.register("crying_obsidian_stairs",
            () -> new BlockItem(ModBlocks.CRYING_OBSIDIAN_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CRYING_OBSIDIAN_VERTICAL_SLAB = ITEMS.register("crying_obsidian_vertical_slab",
            () -> new BlockItem(ModBlocks.CRYING_OBSIDIAN_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CUT_RED_SANDSTONE_STAIRS = ITEMS.register("cut_red_sandstone_stairs",
            () -> new BlockItem(ModBlocks.CUT_RED_SANDSTONE_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CUT_RED_SANDSTONE_VERTICAL_SLAB = ITEMS.register("cut_red_sandstone_vertical_slab",
            () -> new BlockItem(ModBlocks.CUT_RED_SANDSTONE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CUT_SANDSTONE_STAIRS = ITEMS.register("cut_sandstone_stairs",
            () -> new BlockItem(ModBlocks.CUT_SANDSTONE_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CUT_SANDSTONE_VERTICAL_SLAB = ITEMS.register("cut_sandstone_vertical_slab",
            () -> new BlockItem(ModBlocks.CUT_SANDSTONE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> DARK_OAK_WOOD_SLAB = ITEMS.register("dark_oak_wood_slab",
            () -> new BlockItem(ModBlocks.DARK_OAK_WOOD_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> DARK_OAK_WOOD_STAIRS = ITEMS.register("dark_oak_wood_stairs",
            () -> new BlockItem(ModBlocks.DARK_OAK_WOOD_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> DARK_OAK_WOOD_VERTICAL_SLAB = ITEMS.register("dark_oak_wood_vertical_slab",
            () -> new BlockItem(ModBlocks.DARK_OAK_WOOD_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> DIRT_STAIRS = ITEMS.register("dirt_stairs",
            () -> new BlockItem(ModBlocks.DIRT_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> JUNGLE_WOOD_SLAB = ITEMS.register("jungle_wood_slab",
            () -> new BlockItem(ModBlocks.JUNGLE_WOOD_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> JUNGLE_WOOD_STAIRS = ITEMS.register("jungle_wood_stairs",
            () -> new BlockItem(ModBlocks.JUNGLE_WOOD_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> JUNGLE_WOOD_VERTICAL_SLAB = ITEMS.register("jungle_wood_vertical_slab",
            () -> new BlockItem(ModBlocks.JUNGLE_WOOD_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MOSS_BLOCK_STAIRS = ITEMS.register("moss_block_stairs",
            () -> new BlockItem(ModBlocks.MOSS_BLOCK_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MYCELIUM_STAIRS = ITEMS.register("mycelium_stairs",
            () -> new BlockItem(ModBlocks.MYCELIUM_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OAK_WOOD_SLAB = ITEMS.register("oak_wood_slab",
            () -> new BlockItem(ModBlocks.OAK_WOOD_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OAK_WOOD_STAIRS = ITEMS.register("oak_wood_stairs",
            () -> new BlockItem(ModBlocks.OAK_WOOD_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OAK_WOOD_VERTICAL_SLAB = ITEMS.register("oak_wood_vertical_slab",
            () -> new BlockItem(ModBlocks.OAK_WOOD_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PODZOL_STAIRS = ITEMS.register("podzol_stairs",
            () -> new BlockItem(ModBlocks.PODZOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SANDSTONE_VERTICAL_SLAB = ITEMS.register("sandstone_vertical_slab",
            () -> new BlockItem(ModBlocks.SANDSTONE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SMOOTH_RED_SANDSTONE_VERTICAL_SLAB = ITEMS.register("smooth_red_sandstone_vertical_slab",
            () -> new BlockItem(ModBlocks.SMOOTH_RED_SANDSTONE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SMOOTH_SANDSTONE_VERTICAL_SLAB = ITEMS.register("smooth_sandstone_vertical_slab",
            () -> new BlockItem(ModBlocks.SMOOTH_SANDSTONE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SPRUCE_WOOD_SLAB = ITEMS.register("spruce_wood_slab",
            () -> new BlockItem(ModBlocks.SPRUCE_WOOD_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SPRUCE_WOOD_STAIRS = ITEMS.register("spruce_wood_stairs",
            () -> new BlockItem(ModBlocks.SPRUCE_WOOD_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SPRUCE_WOOD_VERTICAL_SLAB = ITEMS.register("spruce_wood_vertical_slab",
            () -> new BlockItem(ModBlocks.SPRUCE_WOOD_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_ACACIA_WOOD_SLAB = ITEMS.register("stripped_acacia_wood_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_ACACIA_WOOD_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_ACACIA_WOOD_STAIRS = ITEMS.register("stripped_acacia_wood_stairs",
            () -> new BlockItem(ModBlocks.STRIPPED_ACACIA_WOOD_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_ACACIA_WOOD_VERTICAL_SLAB = ITEMS.register("stripped_acacia_wood_vertical_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_ACACIA_WOOD_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_BIRCH_WOOD_SLAB = ITEMS.register("stripped_birch_wood_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_BIRCH_WOOD_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_BIRCH_WOOD_STAIRS = ITEMS.register("stripped_birch_wood_stairs",
            () -> new BlockItem(ModBlocks.STRIPPED_BIRCH_WOOD_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_BIRCH_WOOD_VERTICAL_SLAB = ITEMS.register("stripped_birch_wood_vertical_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_BIRCH_WOOD_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_CRIMSON_HYPHAE_SLAB = ITEMS.register("stripped_crimson_hyphae_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_CRIMSON_HYPHAE_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_CRIMSON_HYPHAE_STAIRS = ITEMS.register("stripped_crimson_hyphae_stairs",
            () -> new BlockItem(ModBlocks.STRIPPED_CRIMSON_HYPHAE_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_CRIMSON_HYPHAE_VERTICAL_SLAB = ITEMS.register("stripped_crimson_hyphae_vertical_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_CRIMSON_HYPHAE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_DARK_OAK_WOOD_SLAB = ITEMS.register("stripped_dark_oak_wood_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_DARK_OAK_WOOD_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_DARK_OAK_WOOD_STAIRS = ITEMS.register("stripped_dark_oak_wood_stairs",
            () -> new BlockItem(ModBlocks.STRIPPED_DARK_OAK_WOOD_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_DARK_OAK_WOOD_VERTICAL_SLAB = ITEMS.register("stripped_dark_oak_wood_vertical_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_DARK_OAK_WOOD_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_JUNGLE_WOOD_SLAB = ITEMS.register("stripped_jungle_wood_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_JUNGLE_WOOD_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_JUNGLE_WOOD_STAIRS = ITEMS.register("stripped_jungle_wood_stairs",
            () -> new BlockItem(ModBlocks.STRIPPED_JUNGLE_WOOD_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_JUNGLE_WOOD_VERTICAL_SLAB = ITEMS.register("stripped_jungle_wood_vertical_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_JUNGLE_WOOD_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_OAK_WOOD_SLAB = ITEMS.register("stripped_oak_wood_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_OAK_WOOD_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_OAK_WOOD_STAIRS = ITEMS.register("stripped_oak_wood_stairs",
            () -> new BlockItem(ModBlocks.STRIPPED_OAK_WOOD_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_OAK_WOOD_VERTICAL_SLAB = ITEMS.register("stripped_oak_wood_vertical_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_OAK_WOOD_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_SPRUCE_WOOD_SLAB = ITEMS.register("stripped_spruce_wood_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_SPRUCE_WOOD_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_SPRUCE_WOOD_STAIRS = ITEMS.register("stripped_spruce_wood_stairs",
            () -> new BlockItem(ModBlocks.STRIPPED_SPRUCE_WOOD_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_SPRUCE_WOOD_VERTICAL_SLAB = ITEMS.register("stripped_spruce_wood_vertical_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_SPRUCE_WOOD_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_WARPED_HYPHAE_SLAB = ITEMS.register("stripped_warped_hyphae_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_WARPED_HYPHAE_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_WARPED_HYPHAE_STAIRS = ITEMS.register("stripped_warped_hyphae_stairs",
            () -> new BlockItem(ModBlocks.STRIPPED_WARPED_HYPHAE_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_WARPED_HYPHAE_VERTICAL_SLAB = ITEMS.register("stripped_warped_hyphae_vertical_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_WARPED_HYPHAE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> TINTED_GLASS_SLAB = ITEMS.register("tinted_glass_slab",
            () -> new BlockItem(ModBlocks.TINTED_GLASS_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> TINTED_GLASS_STAIRS = ITEMS.register("tinted_glass_stairs",
            () -> new BlockItem(ModBlocks.TINTED_GLASS_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> TINTED_GLASS_VERTICAL_SLAB = ITEMS.register("tinted_glass_vertical_slab",
            () -> new BlockItem(ModBlocks.TINTED_GLASS_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WARPED_HYPHAE_SLAB = ITEMS.register("warped_hyphae_slab",
            () -> new BlockItem(ModBlocks.WARPED_HYPHAE_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WARPED_HYPHAE_STAIRS = ITEMS.register("warped_hyphae_stairs",
            () -> new BlockItem(ModBlocks.WARPED_HYPHAE_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WARPED_HYPHAE_VERTICAL_SLAB = ITEMS.register("warped_hyphae_vertical_slab",
            () -> new BlockItem(ModBlocks.WARPED_HYPHAE_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MUFF_BLOCK = ITEMS.register("muff_block",
            () -> new MuffBlockItem(ModBlocks.MUFF_BLOCK.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> WHITE_WALLPAPER = ITEMS.register("white_wallpaper",
            () -> new BlockItem(ModBlocks.WHITE_WALLPAPER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_WALLPAPER = ITEMS.register("orange_wallpaper",
            () -> new BlockItem(ModBlocks.ORANGE_WALLPAPER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_WALLPAPER = ITEMS.register("magenta_wallpaper",
            () -> new BlockItem(ModBlocks.MAGENTA_WALLPAPER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_WALLPAPER = ITEMS.register("light_blue_wallpaper",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_WALLPAPER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_WALLPAPER = ITEMS.register("yellow_wallpaper",
            () -> new BlockItem(ModBlocks.YELLOW_WALLPAPER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_WALLPAPER = ITEMS.register("lime_wallpaper",
            () -> new BlockItem(ModBlocks.LIME_WALLPAPER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_WALLPAPER = ITEMS.register("pink_wallpaper",
            () -> new BlockItem(ModBlocks.PINK_WALLPAPER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_WALLPAPER = ITEMS.register("gray_wallpaper",
            () -> new BlockItem(ModBlocks.GRAY_WALLPAPER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_WALLPAPER = ITEMS.register("light_gray_wallpaper",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_WALLPAPER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_WALLPAPER = ITEMS.register("cyan_wallpaper",
            () -> new BlockItem(ModBlocks.CYAN_WALLPAPER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_WALLPAPER = ITEMS.register("purple_wallpaper",
            () -> new BlockItem(ModBlocks.PURPLE_WALLPAPER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_WALLPAPER = ITEMS.register("blue_wallpaper",
            () -> new BlockItem(ModBlocks.BLUE_WALLPAPER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_WALLPAPER = ITEMS.register("brown_wallpaper",
            () -> new BlockItem(ModBlocks.BROWN_WALLPAPER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_WALLPAPER = ITEMS.register("green_wallpaper",
            () -> new BlockItem(ModBlocks.GREEN_WALLPAPER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_WALLPAPER = ITEMS.register("red_wallpaper",
            () -> new BlockItem(ModBlocks.RED_WALLPAPER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_WALLPAPER = ITEMS.register("black_wallpaper",
            () -> new BlockItem(ModBlocks.BLACK_WALLPAPER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WHITE_WALLPAPER_SLAB = ITEMS.register(
            "white_wallpaper_slab",
            () -> new BlockItem(ModBlocks.WHITE_WALLPAPER_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WHITE_WALLPAPER_VERTICAL_SLAB = ITEMS.register(
            "white_wallpaper_vertical_slab",
            () -> new BlockItem(ModBlocks.WHITE_WALLPAPER_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> ORANGE_WALLPAPER_SLAB = ITEMS.register(
            "orange_wallpaper_slab",
            () -> new BlockItem(ModBlocks.ORANGE_WALLPAPER_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> ORANGE_WALLPAPER_VERTICAL_SLAB = ITEMS.register(
            "orange_wallpaper_vertical_slab",
            () -> new BlockItem(ModBlocks.ORANGE_WALLPAPER_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> MAGENTA_WALLPAPER_SLAB = ITEMS.register(
            "magenta_wallpaper_slab",
            () -> new BlockItem(ModBlocks.MAGENTA_WALLPAPER_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> MAGENTA_WALLPAPER_VERTICAL_SLAB = ITEMS.register(
            "magenta_wallpaper_vertical_slab",
            () -> new BlockItem(ModBlocks.MAGENTA_WALLPAPER_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIGHT_BLUE_WALLPAPER_SLAB = ITEMS.register(
            "light_blue_wallpaper_slab",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_WALLPAPER_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIGHT_BLUE_WALLPAPER_VERTICAL_SLAB = ITEMS.register(
            "light_blue_wallpaper_vertical_slab",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_WALLPAPER_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> YELLOW_WALLPAPER_SLAB = ITEMS.register(
            "yellow_wallpaper_slab",
            () -> new BlockItem(ModBlocks.YELLOW_WALLPAPER_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> YELLOW_WALLPAPER_VERTICAL_SLAB = ITEMS.register(
            "yellow_wallpaper_vertical_slab",
            () -> new BlockItem(ModBlocks.YELLOW_WALLPAPER_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIME_WALLPAPER_SLAB = ITEMS.register(
            "lime_wallpaper_slab",
            () -> new BlockItem(ModBlocks.LIME_WALLPAPER_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIME_WALLPAPER_VERTICAL_SLAB = ITEMS.register(
            "lime_wallpaper_vertical_slab",
            () -> new BlockItem(ModBlocks.LIME_WALLPAPER_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PINK_WALLPAPER_SLAB = ITEMS.register(
            "pink_wallpaper_slab",
            () -> new BlockItem(ModBlocks.PINK_WALLPAPER_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PINK_WALLPAPER_VERTICAL_SLAB = ITEMS.register(
            "pink_wallpaper_vertical_slab",
            () -> new BlockItem(ModBlocks.PINK_WALLPAPER_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> GRAY_WALLPAPER_SLAB = ITEMS.register(
            "gray_wallpaper_slab",
            () -> new BlockItem(ModBlocks.GRAY_WALLPAPER_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> GRAY_WALLPAPER_VERTICAL_SLAB = ITEMS.register(
            "gray_wallpaper_vertical_slab",
            () -> new BlockItem(ModBlocks.GRAY_WALLPAPER_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIGHT_GRAY_WALLPAPER_SLAB = ITEMS.register(
            "light_gray_wallpaper_slab",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_WALLPAPER_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> LIGHT_GRAY_WALLPAPER_VERTICAL_SLAB = ITEMS.register(
            "light_gray_wallpaper_vertical_slab",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_WALLPAPER_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> CYAN_WALLPAPER_SLAB = ITEMS.register(
            "cyan_wallpaper_slab",
            () -> new BlockItem(ModBlocks.CYAN_WALLPAPER_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> CYAN_WALLPAPER_VERTICAL_SLAB = ITEMS.register(
            "cyan_wallpaper_vertical_slab",
            () -> new BlockItem(ModBlocks.CYAN_WALLPAPER_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PURPLE_WALLPAPER_SLAB = ITEMS.register(
            "purple_wallpaper_slab",
            () -> new BlockItem(ModBlocks.PURPLE_WALLPAPER_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PURPLE_WALLPAPER_VERTICAL_SLAB = ITEMS.register(
            "purple_wallpaper_vertical_slab",
            () -> new BlockItem(ModBlocks.PURPLE_WALLPAPER_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BLUE_WALLPAPER_SLAB = ITEMS.register(
            "blue_wallpaper_slab",
            () -> new BlockItem(ModBlocks.BLUE_WALLPAPER_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BLUE_WALLPAPER_VERTICAL_SLAB = ITEMS.register(
            "blue_wallpaper_vertical_slab",
            () -> new BlockItem(ModBlocks.BLUE_WALLPAPER_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BROWN_WALLPAPER_SLAB = ITEMS.register(
            "brown_wallpaper_slab",
            () -> new BlockItem(ModBlocks.BROWN_WALLPAPER_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BROWN_WALLPAPER_VERTICAL_SLAB = ITEMS.register(
            "brown_wallpaper_vertical_slab",
            () -> new BlockItem(ModBlocks.BROWN_WALLPAPER_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> GREEN_WALLPAPER_SLAB = ITEMS.register(
            "green_wallpaper_slab",
            () -> new BlockItem(ModBlocks.GREEN_WALLPAPER_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> GREEN_WALLPAPER_VERTICAL_SLAB = ITEMS.register(
            "green_wallpaper_vertical_slab",
            () -> new BlockItem(ModBlocks.GREEN_WALLPAPER_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> RED_WALLPAPER_SLAB = ITEMS.register(
            "red_wallpaper_slab",
            () -> new BlockItem(ModBlocks.RED_WALLPAPER_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> RED_WALLPAPER_VERTICAL_SLAB = ITEMS.register(
            "red_wallpaper_vertical_slab",
            () -> new BlockItem(ModBlocks.RED_WALLPAPER_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BLACK_WALLPAPER_SLAB = ITEMS.register(
            "black_wallpaper_slab",
            () -> new BlockItem(ModBlocks.BLACK_WALLPAPER_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BLACK_WALLPAPER_VERTICAL_SLAB = ITEMS.register(
            "black_wallpaper_vertical_slab",
            () -> new BlockItem(ModBlocks.BLACK_WALLPAPER_VERTICAL_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> WHITE_WALLPAPER_FLAT = ITEMS.register("white_wallpaper_flat",
            () -> new BlockItem(ModBlocks.WHITE_WALLPAPER_FLAT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_WALLPAPER_FLAT = ITEMS.register("orange_wallpaper_flat",
            () -> new BlockItem(ModBlocks.ORANGE_WALLPAPER_FLAT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_WALLPAPER_FLAT = ITEMS.register("magenta_wallpaper_flat",
            () -> new BlockItem(ModBlocks.MAGENTA_WALLPAPER_FLAT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_WALLPAPER_FLAT = ITEMS.register("light_blue_wallpaper_flat",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_WALLPAPER_FLAT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_WALLPAPER_FLAT = ITEMS.register("yellow_wallpaper_flat",
            () -> new BlockItem(ModBlocks.YELLOW_WALLPAPER_FLAT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_WALLPAPER_FLAT = ITEMS.register("lime_wallpaper_flat",
            () -> new BlockItem(ModBlocks.LIME_WALLPAPER_FLAT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_WALLPAPER_FLAT = ITEMS.register("pink_wallpaper_flat",
            () -> new BlockItem(ModBlocks.PINK_WALLPAPER_FLAT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_WALLPAPER_FLAT = ITEMS.register("gray_wallpaper_flat",
            () -> new BlockItem(ModBlocks.GRAY_WALLPAPER_FLAT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_WALLPAPER_FLAT = ITEMS.register("light_gray_wallpaper_flat",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_WALLPAPER_FLAT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_WALLPAPER_FLAT = ITEMS.register("cyan_wallpaper_flat",
            () -> new BlockItem(ModBlocks.CYAN_WALLPAPER_FLAT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_WALLPAPER_FLAT = ITEMS.register("purple_wallpaper_flat",
            () -> new BlockItem(ModBlocks.PURPLE_WALLPAPER_FLAT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_WALLPAPER_FLAT = ITEMS.register("blue_wallpaper_flat",
            () -> new BlockItem(ModBlocks.BLUE_WALLPAPER_FLAT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_WALLPAPER_FLAT = ITEMS.register("brown_wallpaper_flat",
            () -> new BlockItem(ModBlocks.BROWN_WALLPAPER_FLAT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_WALLPAPER_FLAT = ITEMS.register("green_wallpaper_flat",
            () -> new BlockItem(ModBlocks.GREEN_WALLPAPER_FLAT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_WALLPAPER_FLAT = ITEMS.register("red_wallpaper_flat",
            () -> new BlockItem(ModBlocks.RED_WALLPAPER_FLAT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_WALLPAPER_FLAT = ITEMS.register("black_wallpaper_flat",
            () -> new BlockItem(ModBlocks.BLACK_WALLPAPER_FLAT.get(), createBlockItemProperties()));



    public static final RegistryObject<Item> WHITE_CUSHION = ITEMS.register("white_cushion",
            () -> new BlockItem(ModBlocks.WHITE_CUSHION.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_CUSHION = ITEMS.register("orange_cushion",
            () -> new BlockItem(ModBlocks.ORANGE_CUSHION.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_CUSHION = ITEMS.register("magenta_cushion",
            () -> new BlockItem(ModBlocks.MAGENTA_CUSHION.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_CUSHION = ITEMS.register("light_blue_cushion",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_CUSHION.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_CUSHION = ITEMS.register("yellow_cushion",
            () -> new BlockItem(ModBlocks.YELLOW_CUSHION.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_CUSHION = ITEMS.register("lime_cushion",
            () -> new BlockItem(ModBlocks.LIME_CUSHION.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_CUSHION = ITEMS.register("pink_cushion",
            () -> new BlockItem(ModBlocks.PINK_CUSHION.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_CUSHION = ITEMS.register("gray_cushion",
            () -> new BlockItem(ModBlocks.GRAY_CUSHION.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_CUSHION = ITEMS.register("light_gray_cushion",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_CUSHION.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_CUSHION = ITEMS.register("cyan_cushion",
            () -> new BlockItem(ModBlocks.CYAN_CUSHION.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_CUSHION = ITEMS.register("purple_cushion",
            () -> new BlockItem(ModBlocks.PURPLE_CUSHION.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_CUSHION = ITEMS.register("blue_cushion",
            () -> new BlockItem(ModBlocks.BLUE_CUSHION.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_CUSHION = ITEMS.register("brown_cushion",
            () -> new BlockItem(ModBlocks.BROWN_CUSHION.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_CUSHION = ITEMS.register("green_cushion",
            () -> new BlockItem(ModBlocks.GREEN_CUSHION.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_CUSHION = ITEMS.register("red_cushion",
            () -> new BlockItem(ModBlocks.RED_CUSHION.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_CUSHION = ITEMS.register("black_cushion",
            () -> new BlockItem(ModBlocks.BLACK_CUSHION.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> STRAW_BED = ITEMS.register("straw_bed",
            () -> new BedItem(ModBlocks.STRAW_BED.get(), new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(16)));

    public static final RegistryObject<Item> BIG_BOOK = ITEMS.register("big_book",
            () -> new BlockItem(ModBlocks.BIG_BOOK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GLOWING_BIG_BOOK = ITEMS.register("glowing_big_book",
            () -> new BlockItem(ModBlocks.GLOWING_BIG_BOOK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WHITE_BIG_BOOK = ITEMS.register("white_big_book",
            () -> new BlockItem(ModBlocks.WHITE_BIG_BOOK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_BIG_BOOK = ITEMS.register("orange_big_book",
            () -> new BlockItem(ModBlocks.ORANGE_BIG_BOOK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_BIG_BOOK = ITEMS.register("magenta_big_book",
            () -> new BlockItem(ModBlocks.MAGENTA_BIG_BOOK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_BIG_BOOK = ITEMS.register("light_blue_big_book",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_BIG_BOOK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_BIG_BOOK = ITEMS.register("yellow_big_book",
            () -> new BlockItem(ModBlocks.YELLOW_BIG_BOOK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_BIG_BOOK = ITEMS.register("lime_big_book",
            () -> new BlockItem(ModBlocks.LIME_BIG_BOOK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_BIG_BOOK = ITEMS.register("pink_big_book",
            () -> new BlockItem(ModBlocks.PINK_BIG_BOOK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_BIG_BOOK = ITEMS.register("gray_big_book",
            () -> new BlockItem(ModBlocks.GRAY_BIG_BOOK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_BIG_BOOK = ITEMS.register("light_gray_big_book",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_BIG_BOOK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_BIG_BOOK = ITEMS.register("cyan_big_book",
            () -> new BlockItem(ModBlocks.CYAN_BIG_BOOK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_BIG_BOOK = ITEMS.register("purple_big_book",
            () -> new BlockItem(ModBlocks.PURPLE_BIG_BOOK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_BIG_BOOK = ITEMS.register("blue_big_book",
            () -> new BlockItem(ModBlocks.BLUE_BIG_BOOK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_BIG_BOOK = ITEMS.register("brown_big_book",
            () -> new BlockItem(ModBlocks.BROWN_BIG_BOOK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_BIG_BOOK = ITEMS.register("green_big_book",
            () -> new BlockItem(ModBlocks.GREEN_BIG_BOOK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_BIG_BOOK = ITEMS.register("red_big_book",
            () -> new BlockItem(ModBlocks.RED_BIG_BOOK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_BIG_BOOK = ITEMS.register("black_big_book",
            () -> new BlockItem(ModBlocks.BLACK_BIG_BOOK.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> PALE_OAK_LOG = ITEMS.register("pale_oak_log", () -> new BlockItem(ModBlocks.PALE_OAK_LOG.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_LOG = ITEMS.register("stripped_pale_oak_log", () -> new BlockItem(ModBlocks.STRIPPED_PALE_OAK_LOG.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PALE_OAK_WOOD = ITEMS.register("pale_oak_wood", () -> new BlockItem(ModBlocks.PALE_OAK_WOOD.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_WOOD = ITEMS.register("stripped_pale_oak_wood", () -> new BlockItem(ModBlocks.STRIPPED_PALE_OAK_WOOD.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PALE_OAK_PLANKS = ITEMS.register("pale_oak_planks", () -> new BlockItem(ModBlocks.PALE_OAK_PLANKS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PALE_OAK_STAIRS = ITEMS.register("pale_oak_stairs", () -> new BlockItem(ModBlocks.PALE_OAK_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PALE_OAK_SLAB = ITEMS.register("pale_oak_slab", () -> new BlockItem(ModBlocks.PALE_OAK_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PALE_OAK_VERTICAL_SLAB = ITEMS.register("pale_oak_vertical_slab", () -> new BlockItem(ModBlocks.PALE_OAK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PALE_OAK_FENCE = ITEMS.register("pale_oak_fence", () -> new BlockItem(ModBlocks.PALE_OAK_FENCE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PALE_OAK_FENCE_GATE = ITEMS.register("pale_oak_fence_gate", () -> new BlockItem(ModBlocks.PALE_OAK_FENCE_GATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PALE_OAK_DOOR = ITEMS.register("pale_oak_door", () -> new DoubleHighBlockItem(ModBlocks.PALE_OAK_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PALE_OAK_TRAPDOOR = ITEMS.register("pale_oak_trapdoor", () -> new BlockItem(ModBlocks.PALE_OAK_TRAPDOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PALE_OAK_BUTTON = ITEMS.register("pale_oak_button", () -> new BlockItem(ModBlocks.PALE_OAK_BUTTON.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PALE_OAK_PRESSURE_PLATE = ITEMS.register("pale_oak_pressure_plate", () -> new BlockItem(ModBlocks.PALE_OAK_PRESSURE_PLATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PALE_OAK_LEAVES = ITEMS.register("pale_oak_leaves", () -> new BlockItem(ModBlocks.PALE_OAK_LEAVES.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PALE_OAK_SAPLING = ITEMS.register("pale_oak_sapling", () -> new BlockItem(ModBlocks.PALE_OAK_SAPLING.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> PALE_OAK_LOG_SLAB = ITEMS.register("pale_oak_log_slab", () -> new BlockItem(ModBlocks.PALE_OAK_LOG_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PALE_OAK_LOG_VERTICAL_SLAB = ITEMS.register("pale_oak_log_vertical_slab", () -> new BlockItem(ModBlocks.PALE_OAK_LOG_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_LOG_SLAB = ITEMS.register("stripped_pale_oak_log_slab", () -> new BlockItem(ModBlocks.STRIPPED_PALE_OAK_LOG_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_LOG_VERTICAL_SLAB = ITEMS.register("stripped_pale_oak_log_vertical_slab", () -> new BlockItem(ModBlocks.STRIPPED_PALE_OAK_LOG_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PALE_OAK_WOOD_SLAB = ITEMS.register("pale_oak_wood_slab", () -> new BlockItem(ModBlocks.PALE_OAK_WOOD_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PALE_OAK_WOOD_VERTICAL_SLAB = ITEMS.register("pale_oak_wood_vertical_slab", () -> new BlockItem(ModBlocks.PALE_OAK_WOOD_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_WOOD_SLAB = ITEMS.register("stripped_pale_oak_wood_slab", () -> new BlockItem(ModBlocks.STRIPPED_PALE_OAK_WOOD_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_WOOD_VERTICAL_SLAB = ITEMS.register("stripped_pale_oak_wood_vertical_slab", () -> new BlockItem(ModBlocks.STRIPPED_PALE_OAK_WOOD_VERTICAL_SLAB.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> CHERRY_LOG = ITEMS.register("cherry_log", () -> new BlockItem(ModBlocks.CHERRY_LOG.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_CHERRY_LOG = ITEMS.register("stripped_cherry_log", () -> new BlockItem(ModBlocks.STRIPPED_CHERRY_LOG.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CHERRY_WOOD = ITEMS.register("cherry_wood", () -> new BlockItem(ModBlocks.CHERRY_WOOD.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_CHERRY_WOOD = ITEMS.register("stripped_cherry_wood", () -> new BlockItem(ModBlocks.STRIPPED_CHERRY_WOOD.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CHERRY_PLANKS = ITEMS.register("cherry_planks", () -> new BlockItem(ModBlocks.CHERRY_PLANKS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CHERRY_STAIRS = ITEMS.register("cherry_stairs", () -> new BlockItem(ModBlocks.CHERRY_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CHERRY_SLAB = ITEMS.register("cherry_slab", () -> new BlockItem(ModBlocks.CHERRY_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CHERRY_VERTICAL_SLAB = ITEMS.register("cherry_vertical_slab", () -> new BlockItem(ModBlocks.CHERRY_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CHERRY_FENCE = ITEMS.register("cherry_fence", () -> new BlockItem(ModBlocks.CHERRY_FENCE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CHERRY_FENCE_GATE = ITEMS.register("cherry_fence_gate", () -> new BlockItem(ModBlocks.CHERRY_FENCE_GATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CHERRY_DOOR = ITEMS.register("cherry_door", () -> new DoubleHighBlockItem(ModBlocks.CHERRY_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CHERRY_TRAPDOOR = ITEMS.register("cherry_trapdoor", () -> new BlockItem(ModBlocks.CHERRY_TRAPDOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CHERRY_BUTTON = ITEMS.register("cherry_button", () -> new BlockItem(ModBlocks.CHERRY_BUTTON.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CHERRY_PRESSURE_PLATE = ITEMS.register("cherry_pressure_plate", () -> new BlockItem(ModBlocks.CHERRY_PRESSURE_PLATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CHERRY_LEAVES = ITEMS.register("cherry_leaves", () -> new BlockItem(ModBlocks.CHERRY_LEAVES.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CHERRY_SAPLING = ITEMS.register("cherry_sapling", () -> new BlockItem(ModBlocks.CHERRY_SAPLING.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> CHERRY_LOG_SLAB = ITEMS.register("cherry_log_slab", () -> new BlockItem(ModBlocks.CHERRY_LOG_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CHERRY_LOG_VERTICAL_SLAB = ITEMS.register("cherry_log_vertical_slab", () -> new BlockItem(ModBlocks.CHERRY_LOG_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_CHERRY_LOG_SLAB = ITEMS.register("stripped_cherry_log_slab", () -> new BlockItem(ModBlocks.STRIPPED_CHERRY_LOG_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_CHERRY_LOG_VERTICAL_SLAB = ITEMS.register("stripped_cherry_log_vertical_slab", () -> new BlockItem(ModBlocks.STRIPPED_CHERRY_LOG_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CHERRY_WOOD_SLAB = ITEMS.register("cherry_wood_slab", () -> new BlockItem(ModBlocks.CHERRY_WOOD_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CHERRY_WOOD_VERTICAL_SLAB = ITEMS.register("cherry_wood_vertical_slab", () -> new BlockItem(ModBlocks.CHERRY_WOOD_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_CHERRY_WOOD_SLAB = ITEMS.register("stripped_cherry_wood_slab", () -> new BlockItem(ModBlocks.STRIPPED_CHERRY_WOOD_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_CHERRY_WOOD_VERTICAL_SLAB = ITEMS.register("stripped_cherry_wood_vertical_slab", () -> new BlockItem(ModBlocks.STRIPPED_CHERRY_WOOD_VERTICAL_SLAB.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> PALE_MOSS_BLOCK = ITEMS.register("pale_moss_block", () -> new BlockItem(ModBlocks.PALE_MOSS_BLOCK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PALE_MOSS_BLOCK_SLAB = ITEMS.register("pale_moss_block_slab", () -> new BlockItem(ModBlocks.PALE_MOSS_BLOCK_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PALE_MOSS_BLOCK_STAIRS = ITEMS.register("pale_moss_block_stairs", () -> new BlockItem(ModBlocks.PALE_MOSS_BLOCK_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PALE_MOSS_BLOCK_VERTICAL_SLAB = ITEMS.register("pale_moss_block_vertical_slab", () -> new BlockItem(ModBlocks.PALE_MOSS_BLOCK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PALE_MOSS_CARPET = ITEMS.register("pale_moss_carpet", () -> new BlockItem(ModBlocks.PALE_MOSS_CARPET.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PALE_MOSS_LAYERS = ITEMS.register("pale_moss_layers", () -> new BlockItem(ModBlocks.PALE_MOSS_LAYERS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PALE_MOSS_OVERLAY = ITEMS.register("pale_moss_overlay", () -> new BlockItem(ModBlocks.PALE_MOSS_OVERLAY.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PALE_HANGING_MOSS = ITEMS.register("pale_hanging_moss", () -> new BlockItem(ModBlocks.PALE_HANGING_MOSS.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> CREAKING_HEART = ITEMS.register("creaking_heart", () -> new BlockItem(ModBlocks.CREAKING_HEART.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RESIN_CLUMP = ITEMS.register("resin_clump", () -> new BlockItem(ModBlocks.RESIN_CLUMP.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RESIN_BLOCK = ITEMS.register("resin_block", () -> new BlockItem(ModBlocks.RESIN_BLOCK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RESIN_BLOCK_SLAB = ITEMS.register("resin_block_slab", () -> new BlockItem(ModBlocks.RESIN_BLOCK_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RESIN_BLOCK_STAIRS = ITEMS.register("resin_block_stairs", () -> new BlockItem(ModBlocks.RESIN_BLOCK_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RESIN_BLOCK_VERTICAL_SLAB = ITEMS.register("resin_block_vertical_slab", () -> new BlockItem(ModBlocks.RESIN_BLOCK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RESIN_BRICKS = ITEMS.register("resin_bricks", () -> new BlockItem(ModBlocks.RESIN_BRICKS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RESIN_BRICK_SLAB = ITEMS.register("resin_brick_slab", () -> new BlockItem(ModBlocks.RESIN_BRICK_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RESIN_BRICK_STAIRS = ITEMS.register("resin_brick_stairs", () -> new BlockItem(ModBlocks.RESIN_BRICK_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RESIN_BRICK_WALL = ITEMS.register("resin_brick_wall", () -> new BlockItem(ModBlocks.RESIN_BRICK_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RESIN_BRICK_VERTICAL_SLAB = ITEMS.register("resin_brick_vertical_slab", () -> new BlockItem(ModBlocks.RESIN_BRICK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CHISELED_RESIN_BRICKS = ITEMS.register("chiseled_resin_bricks", () -> new BlockItem(ModBlocks.CHISELED_RESIN_BRICKS.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> CLOSED_EYEBLOSSOM = ITEMS.register("closed_eyeblossom", () -> new BlockItem(ModBlocks.CLOSED_EYEBLOSSOM.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OPEN_EYEBLOSSOM = ITEMS.register("open_eyeblossom", () -> new BlockItem(ModBlocks.OPEN_EYEBLOSSOM.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WILDFLOWERS = ITEMS.register("wildflowers", () -> new BlockItem(ModBlocks.WILDFLOWERS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LEAF_LITTER = ITEMS.register("leaf_litter", () -> new BlockItem(ModBlocks.LEAF_LITTER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CACTUS_FLOWER = ITEMS.register("cactus_flower", () -> new BlockItem(ModBlocks.CACTUS_FLOWER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BUSH = ITEMS.register("bush", () -> new BlockItem(ModBlocks.BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_BUSH = ITEMS.register("red_bush", () -> new BlockItem(ModBlocks.RED_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FIREFLY_BUSH = ITEMS.register("firefly_bush", () -> new BlockItem(ModBlocks.FIREFLY_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> DRY_GRASS = ITEMS.register("dry_grass", () -> new BlockItem(ModBlocks.DRY_GRASS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> TALL_DRY_GRASS = ITEMS.register("tall_dry_grass", () -> new BlockItem(ModBlocks.TALL_DRY_GRASS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GOLDEN_DANDELION = ITEMS.register("golden_dandelion", () -> new BlockItem(ModBlocks.GOLDEN_DANDELION.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> OCHRE_FROGLIGHT = ITEMS.register("ochre_froglight", () -> new BlockItem(ModBlocks.OCHRE_FROGLIGHT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PEARLESCENT_FROGLIGHT = ITEMS.register("pearlescent_froglight", () -> new BlockItem(ModBlocks.PEARLESCENT_FROGLIGHT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> VERDANT_FROGLIGHT = ITEMS.register("verdant_froglight", () -> new BlockItem(ModBlocks.VERDANT_FROGLIGHT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GLEAMING_FROGLIGHT = ITEMS.register("gleaming_froglight", () -> new BlockItem(ModBlocks.GLEAMING_FROGLIGHT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> IVORY_FROGLIGHT = ITEMS.register("ivory_froglight", () -> new BlockItem(ModBlocks.IVORY_FROGLIGHT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHEN_FROGLIGHT = ITEMS.register("ashen_froglight", () -> new BlockItem(ModBlocks.ASHEN_FROGLIGHT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ONYX_FROGLIGHT = ITEMS.register("onyx_froglight", () -> new BlockItem(ModBlocks.ONYX_FROGLIGHT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> DUSKY_FROGLIGHT = ITEMS.register("dusky_froglight", () -> new BlockItem(ModBlocks.DUSKY_FROGLIGHT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SCARLET_FROGLIGHT = ITEMS.register("scarlet_froglight", () -> new BlockItem(ModBlocks.SCARLET_FROGLIGHT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RUSSET_FROGLIGHT = ITEMS.register("russet_froglight", () -> new BlockItem(ModBlocks.RUSSET_FROGLIGHT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> VERIDIAN_FROGLIGHT = ITEMS.register("veridian_froglight", () -> new BlockItem(ModBlocks.VERIDIAN_FROGLIGHT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> TIDAL_FROGLIGHT = ITEMS.register("tidal_froglight", () -> new BlockItem(ModBlocks.TIDAL_FROGLIGHT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CERULEAN_FROGLIGHT = ITEMS.register("cerulean_froglight", () -> new BlockItem(ModBlocks.CERULEAN_FROGLIGHT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> AZURE_FROGLIGHT = ITEMS.register("azure_froglight", () -> new BlockItem(ModBlocks.AZURE_FROGLIGHT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> IRIDESCENT_FROGLIGHT = ITEMS.register("iridescent_froglight", () -> new BlockItem(ModBlocks.IRIDESCENT_FROGLIGHT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CORALINE_FROGLIGHT = ITEMS.register("coraline_froglight", () -> new BlockItem(ModBlocks.CORALINE_FROGLIGHT.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> SCULK = ITEMS.register("sculk", () -> new BlockItem(ModBlocks.SCULK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SCULK_SLAB = ITEMS.register("sculk_slab", () -> new BlockItem(ModBlocks.SCULK_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SCULK_STAIRS = ITEMS.register("sculk_stairs", () -> new BlockItem(ModBlocks.SCULK_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SCULK_WALL = ITEMS.register("sculk_wall", () -> new BlockItem(ModBlocks.SCULK_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SCULK_VERTICAL_SLAB = ITEMS.register("sculk_vertical_slab", () -> new BlockItem(ModBlocks.SCULK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SCULK_VEIN = ITEMS.register("sculk_vein", () -> new BlockItem(ModBlocks.SCULK_VEIN.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SCULK_CATALYST = ITEMS.register("sculk_catalyst", () -> new BlockItem(ModBlocks.SCULK_CATALYST.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SCULK_SHRIEKER = ITEMS.register("sculk_shrieker", () -> new BlockItem(ModBlocks.SCULK_SHRIEKER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SCULK_SENSOR = ITEMS.register("sculk_sensor", () -> new BlockItem(ModBlocks.SCULK_SENSOR.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> COPPER_TORCH = ITEMS.register("copper_torch", () -> new StandingAndWallBlockItem(ModBlocks.COPPER_TORCH.get(), ModBlocks.COPPER_WALL_TORCH.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> COPPER_ROD = ITEMS.register("copper_rod", () -> new BlockItem(ModBlocks.COPPER_ROD.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_COPPER_ROD = ITEMS.register("waxed_copper_rod", () -> new BlockItem(ModBlocks.WAXED_COPPER_ROD.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> EXPOSED_COPPER_ROD = ITEMS.register("exposed_copper_rod", () -> new BlockItem(ModBlocks.EXPOSED_COPPER_ROD.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_ROD = ITEMS.register("waxed_exposed_copper_rod", () -> new BlockItem(ModBlocks.WAXED_EXPOSED_COPPER_ROD.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WEATHERED_COPPER_ROD = ITEMS.register("weathered_copper_rod", () -> new BlockItem(ModBlocks.WEATHERED_COPPER_ROD.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_ROD = ITEMS.register("waxed_weathered_copper_rod", () -> new BlockItem(ModBlocks.WAXED_WEATHERED_COPPER_ROD.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OXIDIZED_COPPER_ROD = ITEMS.register("oxidized_copper_rod", () -> new BlockItem(ModBlocks.OXIDIZED_COPPER_ROD.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_ROD = ITEMS.register("waxed_oxidized_copper_rod", () -> new BlockItem(ModBlocks.WAXED_OXIDIZED_COPPER_ROD.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> COPPER_LANTERN = ITEMS.register("copper_lantern", () -> new BlockItem(ModBlocks.COPPER_LANTERN.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_COPPER_LANTERN = ITEMS.register("waxed_copper_lantern", () -> new BlockItem(ModBlocks.WAXED_COPPER_LANTERN.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> EXPOSED_COPPER_LANTERN = ITEMS.register("exposed_copper_lantern", () -> new BlockItem(ModBlocks.EXPOSED_COPPER_LANTERN.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_LANTERN = ITEMS.register("waxed_exposed_copper_lantern", () -> new BlockItem(ModBlocks.WAXED_EXPOSED_COPPER_LANTERN.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WEATHERED_COPPER_LANTERN = ITEMS.register("weathered_copper_lantern", () -> new BlockItem(ModBlocks.WEATHERED_COPPER_LANTERN.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_LANTERN = ITEMS.register("waxed_weathered_copper_lantern", () -> new BlockItem(ModBlocks.WAXED_WEATHERED_COPPER_LANTERN.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OXIDIZED_COPPER_LANTERN = ITEMS.register("oxidized_copper_lantern", () -> new BlockItem(ModBlocks.OXIDIZED_COPPER_LANTERN.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_LANTERN = ITEMS.register("waxed_oxidized_copper_lantern", () -> new BlockItem(ModBlocks.WAXED_OXIDIZED_COPPER_LANTERN.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> SLIT_COPPER = ITEMS.register("slit_copper", () -> new BlockItem(ModBlocks.SLIT_COPPER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_SLIT_COPPER = ITEMS.register("waxed_slit_copper", () -> new BlockItem(ModBlocks.WAXED_SLIT_COPPER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> EXPOSED_SLIT_COPPER = ITEMS.register("exposed_slit_copper", () -> new BlockItem(ModBlocks.EXPOSED_SLIT_COPPER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_EXPOSED_SLIT_COPPER = ITEMS.register("waxed_exposed_slit_copper", () -> new BlockItem(ModBlocks.WAXED_EXPOSED_SLIT_COPPER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WEATHERED_SLIT_COPPER = ITEMS.register("weathered_slit_copper", () -> new BlockItem(ModBlocks.WEATHERED_SLIT_COPPER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_WEATHERED_SLIT_COPPER = ITEMS.register("waxed_weathered_slit_copper", () -> new BlockItem(ModBlocks.WAXED_WEATHERED_SLIT_COPPER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OXIDIZED_SLIT_COPPER = ITEMS.register("oxidized_slit_copper", () -> new BlockItem(ModBlocks.OXIDIZED_SLIT_COPPER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_OXIDIZED_SLIT_COPPER = ITEMS.register("waxed_oxidized_slit_copper", () -> new BlockItem(ModBlocks.WAXED_OXIDIZED_SLIT_COPPER.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> SLIT_COPPER_STAIRS = ITEMS.register("slit_copper_stairs", () -> new BlockItem(ModBlocks.SLIT_COPPER_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_SLIT_COPPER_STAIRS = ITEMS.register("waxed_slit_copper_stairs", () -> new BlockItem(ModBlocks.WAXED_SLIT_COPPER_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> EXPOSED_SLIT_COPPER_STAIRS = ITEMS.register("exposed_slit_copper_stairs", () -> new BlockItem(ModBlocks.EXPOSED_SLIT_COPPER_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_EXPOSED_SLIT_COPPER_STAIRS = ITEMS.register("waxed_exposed_slit_copper_stairs", () -> new BlockItem(ModBlocks.WAXED_EXPOSED_SLIT_COPPER_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WEATHERED_SLIT_COPPER_STAIRS = ITEMS.register("weathered_slit_copper_stairs", () -> new BlockItem(ModBlocks.WEATHERED_SLIT_COPPER_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_WEATHERED_SLIT_COPPER_STAIRS = ITEMS.register("waxed_weathered_slit_copper_stairs", () -> new BlockItem(ModBlocks.WAXED_WEATHERED_SLIT_COPPER_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OXIDIZED_SLIT_COPPER_STAIRS = ITEMS.register("oxidized_slit_copper_stairs", () -> new BlockItem(ModBlocks.OXIDIZED_SLIT_COPPER_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_OXIDIZED_SLIT_COPPER_STAIRS = ITEMS.register("waxed_oxidized_slit_copper_stairs", () -> new BlockItem(ModBlocks.WAXED_OXIDIZED_SLIT_COPPER_STAIRS.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> SLIT_COPPER_SLAB = ITEMS.register("slit_copper_slab", () -> new BlockItem(ModBlocks.SLIT_COPPER_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_SLIT_COPPER_SLAB = ITEMS.register("waxed_slit_copper_slab", () -> new BlockItem(ModBlocks.WAXED_SLIT_COPPER_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> EXPOSED_SLIT_COPPER_SLAB = ITEMS.register("exposed_slit_copper_slab", () -> new BlockItem(ModBlocks.EXPOSED_SLIT_COPPER_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_EXPOSED_SLIT_COPPER_SLAB = ITEMS.register("waxed_exposed_slit_copper_slab", () -> new BlockItem(ModBlocks.WAXED_EXPOSED_SLIT_COPPER_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WEATHERED_SLIT_COPPER_SLAB = ITEMS.register("weathered_slit_copper_slab", () -> new BlockItem(ModBlocks.WEATHERED_SLIT_COPPER_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_WEATHERED_SLIT_COPPER_SLAB = ITEMS.register("waxed_weathered_slit_copper_slab", () -> new BlockItem(ModBlocks.WAXED_WEATHERED_SLIT_COPPER_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OXIDIZED_SLIT_COPPER_SLAB = ITEMS.register("oxidized_slit_copper_slab", () -> new BlockItem(ModBlocks.OXIDIZED_SLIT_COPPER_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_OXIDIZED_SLIT_COPPER_SLAB = ITEMS.register("waxed_oxidized_slit_copper_slab", () -> new BlockItem(ModBlocks.WAXED_OXIDIZED_SLIT_COPPER_SLAB.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> SLIT_COPPER_VERTICAL_SLAB = ITEMS.register("slit_copper_vertical_slab", () -> new BlockItem(ModBlocks.SLIT_COPPER_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_SLIT_COPPER_VERTICAL_SLAB = ITEMS.register("waxed_slit_copper_vertical_slab", () -> new BlockItem(ModBlocks.WAXED_SLIT_COPPER_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> EXPOSED_SLIT_COPPER_VERTICAL_SLAB = ITEMS.register("exposed_slit_copper_vertical_slab", () -> new BlockItem(ModBlocks.EXPOSED_SLIT_COPPER_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_EXPOSED_SLIT_COPPER_VERTICAL_SLAB = ITEMS.register("waxed_exposed_slit_copper_vertical_slab", () -> new BlockItem(ModBlocks.WAXED_EXPOSED_SLIT_COPPER_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WEATHERED_SLIT_COPPER_VERTICAL_SLAB = ITEMS.register("weathered_slit_copper_vertical_slab", () -> new BlockItem(ModBlocks.WEATHERED_SLIT_COPPER_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_WEATHERED_SLIT_COPPER_VERTICAL_SLAB = ITEMS.register("waxed_weathered_slit_copper_vertical_slab", () -> new BlockItem(ModBlocks.WAXED_WEATHERED_SLIT_COPPER_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OXIDIZED_SLIT_COPPER_VERTICAL_SLAB = ITEMS.register("oxidized_slit_copper_vertical_slab", () -> new BlockItem(ModBlocks.OXIDIZED_SLIT_COPPER_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_OXIDIZED_SLIT_COPPER_VERTICAL_SLAB = ITEMS.register("waxed_oxidized_slit_copper_vertical_slab", () -> new BlockItem(ModBlocks.WAXED_OXIDIZED_SLIT_COPPER_VERTICAL_SLAB.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> CHISELED_COPPER = ITEMS.register("chiseled_copper", () -> new BlockItem(ModBlocks.CHISELED_COPPER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_CHISELED_COPPER = ITEMS.register("waxed_chiseled_copper", () -> new BlockItem(ModBlocks.WAXED_CHISELED_COPPER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> EXPOSED_CHISELED_COPPER = ITEMS.register("exposed_chiseled_copper", () -> new BlockItem(ModBlocks.EXPOSED_CHISELED_COPPER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_EXPOSED_CHISELED_COPPER = ITEMS.register("waxed_exposed_chiseled_copper", () -> new BlockItem(ModBlocks.WAXED_EXPOSED_CHISELED_COPPER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WEATHERED_CHISELED_COPPER = ITEMS.register("weathered_chiseled_copper", () -> new BlockItem(ModBlocks.WEATHERED_CHISELED_COPPER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_WEATHERED_CHISELED_COPPER = ITEMS.register("waxed_weathered_chiseled_copper", () -> new BlockItem(ModBlocks.WAXED_WEATHERED_CHISELED_COPPER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OXIDIZED_CHISELED_COPPER = ITEMS.register("oxidized_chiseled_copper", () -> new BlockItem(ModBlocks.OXIDIZED_CHISELED_COPPER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_OXIDIZED_CHISELED_COPPER = ITEMS.register("waxed_oxidized_chiseled_copper", () -> new BlockItem(ModBlocks.WAXED_OXIDIZED_CHISELED_COPPER.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> COPPER_GRATE = ITEMS.register("copper_grate", () -> new BlockItem(ModBlocks.COPPER_GRATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_COPPER_GRATE = ITEMS.register("waxed_copper_grate", () -> new BlockItem(ModBlocks.WAXED_COPPER_GRATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> EXPOSED_COPPER_GRATE = ITEMS.register("exposed_copper_grate", () -> new BlockItem(ModBlocks.EXPOSED_COPPER_GRATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_GRATE = ITEMS.register("waxed_exposed_copper_grate", () -> new BlockItem(ModBlocks.WAXED_EXPOSED_COPPER_GRATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WEATHERED_COPPER_GRATE = ITEMS.register("weathered_copper_grate", () -> new BlockItem(ModBlocks.WEATHERED_COPPER_GRATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_GRATE = ITEMS.register("waxed_weathered_copper_grate", () -> new BlockItem(ModBlocks.WAXED_WEATHERED_COPPER_GRATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OXIDIZED_COPPER_GRATE = ITEMS.register("oxidized_copper_grate", () -> new BlockItem(ModBlocks.OXIDIZED_COPPER_GRATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_GRATE = ITEMS.register("waxed_oxidized_copper_grate", () -> new BlockItem(ModBlocks.WAXED_OXIDIZED_COPPER_GRATE.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> COPPER_BULB = ITEMS.register("copper_bulb", () -> new BlockItem(ModBlocks.COPPER_BULB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_COPPER_BULB = ITEMS.register("waxed_copper_bulb", () -> new BlockItem(ModBlocks.WAXED_COPPER_BULB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> EXPOSED_COPPER_BULB = ITEMS.register("exposed_copper_bulb", () -> new BlockItem(ModBlocks.EXPOSED_COPPER_BULB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_BULB = ITEMS.register("waxed_exposed_copper_bulb", () -> new BlockItem(ModBlocks.WAXED_EXPOSED_COPPER_BULB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WEATHERED_COPPER_BULB = ITEMS.register("weathered_copper_bulb", () -> new BlockItem(ModBlocks.WEATHERED_COPPER_BULB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_BULB = ITEMS.register("waxed_weathered_copper_bulb", () -> new BlockItem(ModBlocks.WAXED_WEATHERED_COPPER_BULB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OXIDIZED_COPPER_BULB = ITEMS.register("oxidized_copper_bulb", () -> new BlockItem(ModBlocks.OXIDIZED_COPPER_BULB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_BULB = ITEMS.register("waxed_oxidized_copper_bulb", () -> new BlockItem(ModBlocks.WAXED_OXIDIZED_COPPER_BULB.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> COPPER_DOOR = ITEMS.register("copper_door", () -> new DoubleHighBlockItem(ModBlocks.COPPER_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_COPPER_DOOR = ITEMS.register("waxed_copper_door", () -> new DoubleHighBlockItem(ModBlocks.WAXED_COPPER_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> EXPOSED_COPPER_DOOR = ITEMS.register("exposed_copper_door", () -> new DoubleHighBlockItem(ModBlocks.EXPOSED_COPPER_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_DOOR = ITEMS.register("waxed_exposed_copper_door", () -> new DoubleHighBlockItem(ModBlocks.WAXED_EXPOSED_COPPER_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WEATHERED_COPPER_DOOR = ITEMS.register("weathered_copper_door", () -> new DoubleHighBlockItem(ModBlocks.WEATHERED_COPPER_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_DOOR = ITEMS.register("waxed_weathered_copper_door", () -> new DoubleHighBlockItem(ModBlocks.WAXED_WEATHERED_COPPER_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OXIDIZED_COPPER_DOOR = ITEMS.register("oxidized_copper_door", () -> new DoubleHighBlockItem(ModBlocks.OXIDIZED_COPPER_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_DOOR = ITEMS.register("waxed_oxidized_copper_door", () -> new DoubleHighBlockItem(ModBlocks.WAXED_OXIDIZED_COPPER_DOOR.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> COPPER_TRAPDOOR = ITEMS.register("copper_trapdoor", () -> new BlockItem(ModBlocks.COPPER_TRAPDOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_COPPER_TRAPDOOR = ITEMS.register("waxed_copper_trapdoor", () -> new BlockItem(ModBlocks.WAXED_COPPER_TRAPDOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> EXPOSED_COPPER_TRAPDOOR = ITEMS.register("exposed_copper_trapdoor", () -> new BlockItem(ModBlocks.EXPOSED_COPPER_TRAPDOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_TRAPDOOR = ITEMS.register("waxed_exposed_copper_trapdoor", () -> new BlockItem(ModBlocks.WAXED_EXPOSED_COPPER_TRAPDOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WEATHERED_COPPER_TRAPDOOR = ITEMS.register("weathered_copper_trapdoor", () -> new BlockItem(ModBlocks.WEATHERED_COPPER_TRAPDOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_TRAPDOOR = ITEMS.register("waxed_weathered_copper_trapdoor", () -> new BlockItem(ModBlocks.WAXED_WEATHERED_COPPER_TRAPDOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OXIDIZED_COPPER_TRAPDOOR = ITEMS.register("oxidized_copper_trapdoor", () -> new BlockItem(ModBlocks.OXIDIZED_COPPER_TRAPDOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_TRAPDOOR = ITEMS.register("waxed_oxidized_copper_trapdoor", () -> new BlockItem(ModBlocks.WAXED_OXIDIZED_COPPER_TRAPDOOR.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> COPPER_BARS = ITEMS.register("copper_bars", () -> new BlockItem(ModBlocks.COPPER_BARS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_COPPER_BARS = ITEMS.register("waxed_copper_bars", () -> new BlockItem(ModBlocks.WAXED_COPPER_BARS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> EXPOSED_COPPER_BARS = ITEMS.register("exposed_copper_bars", () -> new BlockItem(ModBlocks.EXPOSED_COPPER_BARS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_BARS = ITEMS.register("waxed_exposed_copper_bars", () -> new BlockItem(ModBlocks.WAXED_EXPOSED_COPPER_BARS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WEATHERED_COPPER_BARS = ITEMS.register("weathered_copper_bars", () -> new BlockItem(ModBlocks.WEATHERED_COPPER_BARS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_BARS = ITEMS.register("waxed_weathered_copper_bars", () -> new BlockItem(ModBlocks.WAXED_WEATHERED_COPPER_BARS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OXIDIZED_COPPER_BARS = ITEMS.register("oxidized_copper_bars", () -> new BlockItem(ModBlocks.OXIDIZED_COPPER_BARS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_BARS = ITEMS.register("waxed_oxidized_copper_bars", () -> new BlockItem(ModBlocks.WAXED_OXIDIZED_COPPER_BARS.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> GOLD_BARS = ITEMS.register("gold_bars", () -> new BlockItem(ModBlocks.GOLD_BARS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STEEL_BARS = ITEMS.register("steel_bars", () -> new BlockItem(ModBlocks.STEEL_BARS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> IRON_MESH = ITEMS.register("iron_mesh", () -> new BlockItem(ModBlocks.IRON_MESH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GOLD_MESH = ITEMS.register("gold_mesh", () -> new BlockItem(ModBlocks.GOLD_MESH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STEEL_MESH = ITEMS.register("steel_mesh", () -> new BlockItem(ModBlocks.STEEL_MESH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> COPPER_MESH = ITEMS.register("copper_mesh", () -> new BlockItem(ModBlocks.COPPER_MESH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_COPPER_MESH = ITEMS.register("waxed_copper_mesh", () -> new BlockItem(ModBlocks.WAXED_COPPER_MESH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> EXPOSED_COPPER_MESH = ITEMS.register("exposed_copper_mesh", () -> new BlockItem(ModBlocks.EXPOSED_COPPER_MESH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_MESH = ITEMS.register("waxed_exposed_copper_mesh", () -> new BlockItem(ModBlocks.WAXED_EXPOSED_COPPER_MESH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WEATHERED_COPPER_MESH = ITEMS.register("weathered_copper_mesh", () -> new BlockItem(ModBlocks.WEATHERED_COPPER_MESH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_MESH = ITEMS.register("waxed_weathered_copper_mesh", () -> new BlockItem(ModBlocks.WAXED_WEATHERED_COPPER_MESH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OXIDIZED_COPPER_MESH = ITEMS.register("oxidized_copper_mesh", () -> new BlockItem(ModBlocks.OXIDIZED_COPPER_MESH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_MESH = ITEMS.register("waxed_oxidized_copper_mesh", () -> new BlockItem(ModBlocks.WAXED_OXIDIZED_COPPER_MESH.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> COPPER_BUTTON = ITEMS.register("copper_button", () -> new BlockItem(ModBlocks.COPPER_BUTTON.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_COPPER_BUTTON = ITEMS.register("waxed_copper_button", () -> new BlockItem(ModBlocks.WAXED_COPPER_BUTTON.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> EXPOSED_COPPER_BUTTON = ITEMS.register("exposed_copper_button", () -> new BlockItem(ModBlocks.EXPOSED_COPPER_BUTTON.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_BUTTON = ITEMS.register("waxed_exposed_copper_button", () -> new BlockItem(ModBlocks.WAXED_EXPOSED_COPPER_BUTTON.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WEATHERED_COPPER_BUTTON = ITEMS.register("weathered_copper_button", () -> new BlockItem(ModBlocks.WEATHERED_COPPER_BUTTON.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_BUTTON = ITEMS.register("waxed_weathered_copper_button", () -> new BlockItem(ModBlocks.WAXED_WEATHERED_COPPER_BUTTON.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OXIDIZED_COPPER_BUTTON = ITEMS.register("oxidized_copper_button", () -> new BlockItem(ModBlocks.OXIDIZED_COPPER_BUTTON.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_BUTTON = ITEMS.register("waxed_oxidized_copper_button", () -> new BlockItem(ModBlocks.WAXED_OXIDIZED_COPPER_BUTTON.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> COPPER_BOLTS = ITEMS.register("copper_bolts", () -> new BlockItem(ModBlocks.COPPER_BOLTS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_COPPER_BOLTS = ITEMS.register("waxed_copper_bolts", () -> new BlockItem(ModBlocks.WAXED_COPPER_BOLTS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> EXPOSED_COPPER_BOLTS = ITEMS.register("exposed_copper_bolts", () -> new BlockItem(ModBlocks.EXPOSED_COPPER_BOLTS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_BOLTS = ITEMS.register("waxed_exposed_copper_bolts", () -> new BlockItem(ModBlocks.WAXED_EXPOSED_COPPER_BOLTS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WEATHERED_COPPER_BOLTS = ITEMS.register("weathered_copper_bolts", () -> new BlockItem(ModBlocks.WEATHERED_COPPER_BOLTS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_BOLTS = ITEMS.register("waxed_weathered_copper_bolts", () -> new BlockItem(ModBlocks.WAXED_WEATHERED_COPPER_BOLTS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OXIDIZED_COPPER_BOLTS = ITEMS.register("oxidized_copper_bolts", () -> new BlockItem(ModBlocks.OXIDIZED_COPPER_BOLTS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_BOLTS = ITEMS.register("waxed_oxidized_copper_bolts", () -> new BlockItem(ModBlocks.WAXED_OXIDIZED_COPPER_BOLTS.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> COPPER_PRESSURE_PLATE = ITEMS.register("copper_pressure_plate", () -> new BlockItem(ModBlocks.COPPER_PRESSURE_PLATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_COPPER_PRESSURE_PLATE = ITEMS.register("waxed_copper_pressure_plate", () -> new BlockItem(ModBlocks.WAXED_COPPER_PRESSURE_PLATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> EXPOSED_COPPER_PRESSURE_PLATE = ITEMS.register("exposed_copper_pressure_plate", () -> new BlockItem(ModBlocks.EXPOSED_COPPER_PRESSURE_PLATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_PRESSURE_PLATE = ITEMS.register("waxed_exposed_copper_pressure_plate", () -> new BlockItem(ModBlocks.WAXED_EXPOSED_COPPER_PRESSURE_PLATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WEATHERED_COPPER_PRESSURE_PLATE = ITEMS.register("weathered_copper_pressure_plate", () -> new BlockItem(ModBlocks.WEATHERED_COPPER_PRESSURE_PLATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_PRESSURE_PLATE = ITEMS.register("waxed_weathered_copper_pressure_plate", () -> new BlockItem(ModBlocks.WAXED_WEATHERED_COPPER_PRESSURE_PLATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OXIDIZED_COPPER_PRESSURE_PLATE = ITEMS.register("oxidized_copper_pressure_plate", () -> new BlockItem(ModBlocks.OXIDIZED_COPPER_PRESSURE_PLATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_PRESSURE_PLATE = ITEMS.register("waxed_oxidized_copper_pressure_plate", () -> new BlockItem(ModBlocks.WAXED_OXIDIZED_COPPER_PRESSURE_PLATE.get(), createBlockItemProperties()));

    public static class WanderingHomemakerSpawnEggItem extends net.minecraftforge.common.ForgeSpawnEggItem {
        public WanderingHomemakerSpawnEggItem(java.util.function.Supplier<? extends net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.Mob>> type, int backgroundColor, int highlightColor, Item.Properties properties) {
            super(type, backgroundColor, highlightColor, properties);
        }

        public int getColor(int tintIndex) {
            return -1;
        }

        @Override
        public void appendHoverText(net.minecraft.world.item.ItemStack stack, @javax.annotation.Nullable net.minecraft.world.level.Level level, java.util.List<net.minecraft.network.chat.Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltip, flag);
            tooltip.add(new net.minecraft.network.chat.TranslatableComponent("tooltip.buildscape.wandering_homemaker_spawn_egg.spawn_info"));
            if (level != null) {
                long currentTime = System.currentTimeMillis();
                long endTime = com.kingodogo.buildscape.client.HomemakerCooldownTracker.cooldownEndTime;
                if (endTime > currentTime) {
                    long remainingMs = endTime - currentTime;
                    long totalSeconds = remainingMs / 1000;
                    long minutes = totalSeconds / 60;
                    long seconds = totalSeconds % 60;
                    tooltip.add(new net.minecraft.network.chat.TranslatableComponent("tooltip.buildscape.wandering_homemaker_spawn_egg.cooldown_active", minutes, seconds)
                            .withStyle(net.minecraft.ChatFormatting.RED));
                } else {
                    tooltip.add(new net.minecraft.network.chat.TranslatableComponent("tooltip.buildscape.wandering_homemaker_spawn_egg.cooldown_ready")
                            .withStyle(net.minecraft.ChatFormatting.GREEN));
                }
            }
        }
    }

    public static class FestiveWanderingHomemakerSpawnEggItem extends net.minecraftforge.common.ForgeSpawnEggItem {
        public FestiveWanderingHomemakerSpawnEggItem(java.util.function.Supplier<? extends net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.Mob>> type, int backgroundColor, int highlightColor, Item.Properties properties) {
            super(type, backgroundColor, highlightColor, properties);
        }

        public int getColor(int tintIndex) {
            return -1;
        }

        @Override
        public void appendHoverText(net.minecraft.world.item.ItemStack stack, @javax.annotation.Nullable net.minecraft.world.level.Level level, java.util.List<net.minecraft.network.chat.Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltip, flag);
            tooltip.add(new net.minecraft.network.chat.TranslatableComponent("tooltip.buildscape.festive_wandering_homemaker_spawn_egg.spawn_info"));
            if (level != null) {
                long currentTime = System.currentTimeMillis();
                long endTime = com.kingodogo.buildscape.client.HomemakerCooldownTracker.cooldownEndTime;
                if (endTime > currentTime) {
                    long remainingMs = endTime - currentTime;
                    long totalSeconds = remainingMs / 1000;
                    long minutes = totalSeconds / 60;
                    long seconds = totalSeconds % 60;
                    tooltip.add(new net.minecraft.network.chat.TranslatableComponent("tooltip.buildscape.wandering_homemaker_spawn_egg.cooldown_active", minutes, seconds)
                            .withStyle(net.minecraft.ChatFormatting.RED));
                } else {
                    tooltip.add(new net.minecraft.network.chat.TranslatableComponent("tooltip.buildscape.wandering_homemaker_spawn_egg.cooldown_ready")
                            .withStyle(net.minecraft.ChatFormatting.GREEN));
                }
            }
        }
    }

    public static final RegistryObject<Item> WANDERING_HOMEMAKER_SPAWN_EGG = ITEMS.register(
            "wandering_homemaker_spawn_egg",
            () -> new WanderingHomemakerSpawnEggItem(
                    com.kingodogo.buildscape.entity.ModEntities.WANDERING_HOMEMAKER,
                    0x5c3c24,
                    0xe4b484,
                    createBlockItemProperties()
            )
    );

    public static final RegistryObject<Item> FESTIVE_WANDERING_HOMEMAKER_SPAWN_EGG = ITEMS.register(
            "festive_wandering_homemaker_spawn_egg",
            () -> new FestiveWanderingHomemakerSpawnEggItem(
                    com.kingodogo.buildscape.entity.ModEntities.FESTIVE_WANDERING_HOMEMAKER,
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

    public static final RegistryObject<Item> ACACIA_LADDER = ITEMS.register("acacia_ladder", () -> new BlockItem(ModBlocks.ACACIA_LADDER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_BLACK_LADDER = ITEMS.register("ashpen_black_ladder", () -> new BlockItem(ModBlocks.ASHPEN_BLACK_LADDER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_BLUE_LADDER = ITEMS.register("ashpen_blue_ladder", () -> new BlockItem(ModBlocks.ASHPEN_BLUE_LADDER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_BROWN_LADDER = ITEMS.register("ashpen_brown_ladder", () -> new BlockItem(ModBlocks.ASHPEN_BROWN_LADDER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_CYAN_LADDER = ITEMS.register("ashpen_cyan_ladder", () -> new BlockItem(ModBlocks.ASHPEN_CYAN_LADDER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_GRAY_LADDER = ITEMS.register("ashpen_gray_ladder", () -> new BlockItem(ModBlocks.ASHPEN_GRAY_LADDER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_GREEN_LADDER = ITEMS.register("ashpen_green_ladder", () -> new BlockItem(ModBlocks.ASHPEN_GREEN_LADDER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_LIGHT_BLUE_LADDER = ITEMS.register("ashpen_light_blue_ladder", () -> new BlockItem(ModBlocks.ASHPEN_LIGHT_BLUE_LADDER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_LIGHT_GRAY_LADDER = ITEMS.register("ashpen_light_gray_ladder", () -> new BlockItem(ModBlocks.ASHPEN_LIGHT_GRAY_LADDER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_LIME_LADDER = ITEMS.register("ashpen_lime_ladder", () -> new BlockItem(ModBlocks.ASHPEN_LIME_LADDER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_MAGENTA_LADDER = ITEMS.register("ashpen_magenta_ladder", () -> new BlockItem(ModBlocks.ASHPEN_MAGENTA_LADDER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_ORANGE_LADDER = ITEMS.register("ashpen_orange_ladder", () -> new BlockItem(ModBlocks.ASHPEN_ORANGE_LADDER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_PINK_LADDER = ITEMS.register("ashpen_pink_ladder", () -> new BlockItem(ModBlocks.ASHPEN_PINK_LADDER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_PURPLE_LADDER = ITEMS.register("ashpen_purple_ladder", () -> new BlockItem(ModBlocks.ASHPEN_PURPLE_LADDER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_RED_LADDER = ITEMS.register("ashpen_red_ladder", () -> new BlockItem(ModBlocks.ASHPEN_RED_LADDER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_WHITE_LADDER = ITEMS.register("ashpen_white_ladder", () -> new BlockItem(ModBlocks.ASHPEN_WHITE_LADDER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ASHPEN_YELLOW_LADDER = ITEMS.register("ashpen_yellow_ladder", () -> new BlockItem(ModBlocks.ASHPEN_YELLOW_LADDER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BAMBOO_LADDER = ITEMS.register("bamboo_ladder", () -> new BlockItem(ModBlocks.BAMBOO_LADDER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIRCH_LADDER = ITEMS.register("birch_ladder", () -> new BlockItem(ModBlocks.BIRCH_LADDER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CHERRY_LADDER = ITEMS.register("cherry_ladder", () -> new BlockItem(ModBlocks.CHERRY_LADDER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CRIMSON_LADDER = ITEMS.register("crimson_ladder", () -> new BlockItem(ModBlocks.CRIMSON_LADDER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> DARK_OAK_LADDER = ITEMS.register("dark_oak_ladder", () -> new BlockItem(ModBlocks.DARK_OAK_LADDER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> JUNGLE_LADDER = ITEMS.register("jungle_ladder", () -> new BlockItem(ModBlocks.JUNGLE_LADDER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MANGROVE_LADDER = ITEMS.register("mangrove_ladder", () -> new BlockItem(ModBlocks.MANGROVE_LADDER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OAK_LADDER = ITEMS.register("oak_ladder", () -> new BlockItem(ModBlocks.OAK_LADDER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PALE_OAK_LADDER = ITEMS.register("pale_oak_ladder", () -> new BlockItem(ModBlocks.PALE_OAK_LADDER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> POPLAR_LADDER = ITEMS.register("poplar_ladder", () -> new BlockItem(ModBlocks.POPLAR_LADDER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> SPRUCE_LADDER = ITEMS.register("spruce_ladder", () -> new BlockItem(ModBlocks.SPRUCE_LADDER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_BAMBOO_LADDER = ITEMS.register("stripped_bamboo_ladder", () -> new BlockItem(ModBlocks.STRIPPED_BAMBOO_LADDER.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WARPED_LADDER = ITEMS.register("warped_ladder", () -> new BlockItem(ModBlocks.WARPED_LADDER.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> COPPER_CHEST = ITEMS.register("copper_chest", () -> new CopperChestItem(ModBlocks.COPPER_CHEST.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> EXPOSED_COPPER_CHEST = ITEMS.register("exposed_copper_chest", () -> new CopperChestItem(ModBlocks.EXPOSED_COPPER_CHEST.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WEATHERED_COPPER_CHEST = ITEMS.register("weathered_copper_chest", () -> new CopperChestItem(ModBlocks.WEATHERED_COPPER_CHEST.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OXIDIZED_COPPER_CHEST = ITEMS.register("oxidized_copper_chest", () -> new CopperChestItem(ModBlocks.OXIDIZED_COPPER_CHEST.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_COPPER_CHEST = ITEMS.register("waxed_copper_chest", () -> new CopperChestItem(ModBlocks.WAXED_COPPER_CHEST.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_CHEST = ITEMS.register("waxed_exposed_copper_chest", () -> new CopperChestItem(ModBlocks.WAXED_EXPOSED_COPPER_CHEST.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_CHEST = ITEMS.register("waxed_weathered_copper_chest", () -> new CopperChestItem(ModBlocks.WAXED_WEATHERED_COPPER_CHEST.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_CHEST = ITEMS.register("waxed_oxidized_copper_chest", () -> new CopperChestItem(ModBlocks.WAXED_OXIDIZED_COPPER_CHEST.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> WHITE_LAYERED_WOOL = ITEMS.register(
            "white_layered_wool",
            () -> new BlockItem(ModBlocks.WHITE_LAYERED_WOOL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL = ITEMS.register(
            "orange_layered_wool",
            () -> new BlockItem(ModBlocks.ORANGE_LAYERED_WOOL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL = ITEMS.register(
            "magenta_layered_wool",
            () -> new BlockItem(ModBlocks.MAGENTA_LAYERED_WOOL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL = ITEMS.register(
            "light_blue_layered_wool",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_LAYERED_WOOL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL = ITEMS.register(
            "yellow_layered_wool",
            () -> new BlockItem(ModBlocks.YELLOW_LAYERED_WOOL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_LAYERED_WOOL = ITEMS.register(
            "lime_layered_wool",
            () -> new BlockItem(ModBlocks.LIME_LAYERED_WOOL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_LAYERED_WOOL = ITEMS.register(
            "pink_layered_wool",
            () -> new BlockItem(ModBlocks.PINK_LAYERED_WOOL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL = ITEMS.register(
            "gray_layered_wool",
            () -> new BlockItem(ModBlocks.GRAY_LAYERED_WOOL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL = ITEMS.register(
            "light_gray_layered_wool",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_LAYERED_WOOL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL = ITEMS.register(
            "cyan_layered_wool",
            () -> new BlockItem(ModBlocks.CYAN_LAYERED_WOOL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL = ITEMS.register(
            "purple_layered_wool",
            () -> new BlockItem(ModBlocks.PURPLE_LAYERED_WOOL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL = ITEMS.register(
            "blue_layered_wool",
            () -> new BlockItem(ModBlocks.BLUE_LAYERED_WOOL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL = ITEMS.register(
            "brown_layered_wool",
            () -> new BlockItem(ModBlocks.BROWN_LAYERED_WOOL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL = ITEMS.register(
            "green_layered_wool",
            () -> new BlockItem(ModBlocks.GREEN_LAYERED_WOOL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_LAYERED_WOOL = ITEMS.register(
            "red_layered_wool",
            () -> new BlockItem(ModBlocks.RED_LAYERED_WOOL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL = ITEMS.register(
            "black_layered_wool",
            () -> new BlockItem(ModBlocks.BLACK_LAYERED_WOOL.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> WHITE_LAYERED_WOOL_SLAB = ITEMS.register(
            "white_layered_wool_slab",
            () -> new BlockItem(ModBlocks.WHITE_LAYERED_WOOL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WHITE_LAYERED_WOOL_STAIRS = ITEMS.register(
            "white_layered_wool_stairs",
            () -> new BlockItem(ModBlocks.WHITE_LAYERED_WOOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WHITE_LAYERED_WOOL_WALL = ITEMS.register(
            "white_layered_wool_wall",
            () -> new BlockItem(ModBlocks.WHITE_LAYERED_WOOL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WHITE_LAYERED_WOOL_CARPET = ITEMS.register(
            "white_layered_wool_carpet",
            () -> new BlockItem(ModBlocks.WHITE_LAYERED_WOOL_CARPET.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WHITE_LAYERED_WOOL_LAYERS = ITEMS.register(
            "white_layered_wool_layers",
            () -> new BlockItem(ModBlocks.WHITE_LAYERED_WOOL_LAYERS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WHITE_LAYERED_WOOL_VERTICAL_SLAB = ITEMS.register(
            "white_layered_wool_vertical_slab",
            () -> new BlockItem(ModBlocks.WHITE_LAYERED_WOOL_VERTICAL_SLAB.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL_SLAB = ITEMS.register(
            "orange_layered_wool_slab",
            () -> new BlockItem(ModBlocks.ORANGE_LAYERED_WOOL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL_STAIRS = ITEMS.register(
            "orange_layered_wool_stairs",
            () -> new BlockItem(ModBlocks.ORANGE_LAYERED_WOOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL_WALL = ITEMS.register(
            "orange_layered_wool_wall",
            () -> new BlockItem(ModBlocks.ORANGE_LAYERED_WOOL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL_CARPET = ITEMS.register(
            "orange_layered_wool_carpet",
            () -> new BlockItem(ModBlocks.ORANGE_LAYERED_WOOL_CARPET.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL_LAYERS = ITEMS.register(
            "orange_layered_wool_layers",
            () -> new BlockItem(ModBlocks.ORANGE_LAYERED_WOOL_LAYERS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL_VERTICAL_SLAB = ITEMS.register(
            "orange_layered_wool_vertical_slab",
            () -> new BlockItem(ModBlocks.ORANGE_LAYERED_WOOL_VERTICAL_SLAB.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL_SLAB = ITEMS.register(
            "magenta_layered_wool_slab",
            () -> new BlockItem(ModBlocks.MAGENTA_LAYERED_WOOL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL_STAIRS = ITEMS.register(
            "magenta_layered_wool_stairs",
            () -> new BlockItem(ModBlocks.MAGENTA_LAYERED_WOOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL_WALL = ITEMS.register(
            "magenta_layered_wool_wall",
            () -> new BlockItem(ModBlocks.MAGENTA_LAYERED_WOOL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL_CARPET = ITEMS.register(
            "magenta_layered_wool_carpet",
            () -> new BlockItem(ModBlocks.MAGENTA_LAYERED_WOOL_CARPET.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL_LAYERS = ITEMS.register(
            "magenta_layered_wool_layers",
            () -> new BlockItem(ModBlocks.MAGENTA_LAYERED_WOOL_LAYERS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL_VERTICAL_SLAB = ITEMS.register(
            "magenta_layered_wool_vertical_slab",
            () -> new BlockItem(ModBlocks.MAGENTA_LAYERED_WOOL_VERTICAL_SLAB.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL_SLAB = ITEMS.register(
            "light_blue_layered_wool_slab",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_LAYERED_WOOL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL_STAIRS = ITEMS.register(
            "light_blue_layered_wool_stairs",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_LAYERED_WOOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL_WALL = ITEMS.register(
            "light_blue_layered_wool_wall",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_LAYERED_WOOL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL_CARPET = ITEMS.register(
            "light_blue_layered_wool_carpet",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_LAYERED_WOOL_CARPET.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL_LAYERS = ITEMS.register(
            "light_blue_layered_wool_layers",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_LAYERED_WOOL_LAYERS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL_VERTICAL_SLAB = ITEMS.register(
            "light_blue_layered_wool_vertical_slab",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_LAYERED_WOOL_VERTICAL_SLAB.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL_SLAB = ITEMS.register(
            "yellow_layered_wool_slab",
            () -> new BlockItem(ModBlocks.YELLOW_LAYERED_WOOL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL_STAIRS = ITEMS.register(
            "yellow_layered_wool_stairs",
            () -> new BlockItem(ModBlocks.YELLOW_LAYERED_WOOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL_WALL = ITEMS.register(
            "yellow_layered_wool_wall",
            () -> new BlockItem(ModBlocks.YELLOW_LAYERED_WOOL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL_CARPET = ITEMS.register(
            "yellow_layered_wool_carpet",
            () -> new BlockItem(ModBlocks.YELLOW_LAYERED_WOOL_CARPET.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL_LAYERS = ITEMS.register(
            "yellow_layered_wool_layers",
            () -> new BlockItem(ModBlocks.YELLOW_LAYERED_WOOL_LAYERS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL_VERTICAL_SLAB = ITEMS.register(
            "yellow_layered_wool_vertical_slab",
            () -> new BlockItem(ModBlocks.YELLOW_LAYERED_WOOL_VERTICAL_SLAB.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> LIME_LAYERED_WOOL_SLAB = ITEMS.register(
            "lime_layered_wool_slab",
            () -> new BlockItem(ModBlocks.LIME_LAYERED_WOOL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_LAYERED_WOOL_STAIRS = ITEMS.register(
            "lime_layered_wool_stairs",
            () -> new BlockItem(ModBlocks.LIME_LAYERED_WOOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_LAYERED_WOOL_WALL = ITEMS.register(
            "lime_layered_wool_wall",
            () -> new BlockItem(ModBlocks.LIME_LAYERED_WOOL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_LAYERED_WOOL_CARPET = ITEMS.register(
            "lime_layered_wool_carpet",
            () -> new BlockItem(ModBlocks.LIME_LAYERED_WOOL_CARPET.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_LAYERED_WOOL_LAYERS = ITEMS.register(
            "lime_layered_wool_layers",
            () -> new BlockItem(ModBlocks.LIME_LAYERED_WOOL_LAYERS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_LAYERED_WOOL_VERTICAL_SLAB = ITEMS.register(
            "lime_layered_wool_vertical_slab",
            () -> new BlockItem(ModBlocks.LIME_LAYERED_WOOL_VERTICAL_SLAB.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> PINK_LAYERED_WOOL_SLAB = ITEMS.register(
            "pink_layered_wool_slab",
            () -> new BlockItem(ModBlocks.PINK_LAYERED_WOOL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_LAYERED_WOOL_STAIRS = ITEMS.register(
            "pink_layered_wool_stairs",
            () -> new BlockItem(ModBlocks.PINK_LAYERED_WOOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_LAYERED_WOOL_WALL = ITEMS.register(
            "pink_layered_wool_wall",
            () -> new BlockItem(ModBlocks.PINK_LAYERED_WOOL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_LAYERED_WOOL_CARPET = ITEMS.register(
            "pink_layered_wool_carpet",
            () -> new BlockItem(ModBlocks.PINK_LAYERED_WOOL_CARPET.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_LAYERED_WOOL_LAYERS = ITEMS.register(
            "pink_layered_wool_layers",
            () -> new BlockItem(ModBlocks.PINK_LAYERED_WOOL_LAYERS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_LAYERED_WOOL_VERTICAL_SLAB = ITEMS.register(
            "pink_layered_wool_vertical_slab",
            () -> new BlockItem(ModBlocks.PINK_LAYERED_WOOL_VERTICAL_SLAB.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> GRAY_LAYERED_WOOL_SLAB = ITEMS.register(
            "gray_layered_wool_slab",
            () -> new BlockItem(ModBlocks.GRAY_LAYERED_WOOL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL_STAIRS = ITEMS.register(
            "gray_layered_wool_stairs",
            () -> new BlockItem(ModBlocks.GRAY_LAYERED_WOOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL_WALL = ITEMS.register(
            "gray_layered_wool_wall",
            () -> new BlockItem(ModBlocks.GRAY_LAYERED_WOOL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL_CARPET = ITEMS.register(
            "gray_layered_wool_carpet",
            () -> new BlockItem(ModBlocks.GRAY_LAYERED_WOOL_CARPET.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL_LAYERS = ITEMS.register(
            "gray_layered_wool_layers",
            () -> new BlockItem(ModBlocks.GRAY_LAYERED_WOOL_LAYERS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL_VERTICAL_SLAB = ITEMS.register(
            "gray_layered_wool_vertical_slab",
            () -> new BlockItem(ModBlocks.GRAY_LAYERED_WOOL_VERTICAL_SLAB.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL_SLAB = ITEMS.register(
            "light_gray_layered_wool_slab",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_LAYERED_WOOL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL_STAIRS = ITEMS.register(
            "light_gray_layered_wool_stairs",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_LAYERED_WOOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL_WALL = ITEMS.register(
            "light_gray_layered_wool_wall",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_LAYERED_WOOL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL_CARPET = ITEMS.register(
            "light_gray_layered_wool_carpet",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_LAYERED_WOOL_CARPET.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL_LAYERS = ITEMS.register(
            "light_gray_layered_wool_layers",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_LAYERED_WOOL_LAYERS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL_VERTICAL_SLAB = ITEMS.register(
            "light_gray_layered_wool_vertical_slab",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_LAYERED_WOOL_VERTICAL_SLAB.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> CYAN_LAYERED_WOOL_SLAB = ITEMS.register(
            "cyan_layered_wool_slab",
            () -> new BlockItem(ModBlocks.CYAN_LAYERED_WOOL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL_STAIRS = ITEMS.register(
            "cyan_layered_wool_stairs",
            () -> new BlockItem(ModBlocks.CYAN_LAYERED_WOOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL_WALL = ITEMS.register(
            "cyan_layered_wool_wall",
            () -> new BlockItem(ModBlocks.CYAN_LAYERED_WOOL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL_CARPET = ITEMS.register(
            "cyan_layered_wool_carpet",
            () -> new BlockItem(ModBlocks.CYAN_LAYERED_WOOL_CARPET.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL_LAYERS = ITEMS.register(
            "cyan_layered_wool_layers",
            () -> new BlockItem(ModBlocks.CYAN_LAYERED_WOOL_LAYERS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL_VERTICAL_SLAB = ITEMS.register(
            "cyan_layered_wool_vertical_slab",
            () -> new BlockItem(ModBlocks.CYAN_LAYERED_WOOL_VERTICAL_SLAB.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL_SLAB = ITEMS.register(
            "purple_layered_wool_slab",
            () -> new BlockItem(ModBlocks.PURPLE_LAYERED_WOOL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL_STAIRS = ITEMS.register(
            "purple_layered_wool_stairs",
            () -> new BlockItem(ModBlocks.PURPLE_LAYERED_WOOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL_WALL = ITEMS.register(
            "purple_layered_wool_wall",
            () -> new BlockItem(ModBlocks.PURPLE_LAYERED_WOOL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL_CARPET = ITEMS.register(
            "purple_layered_wool_carpet",
            () -> new BlockItem(ModBlocks.PURPLE_LAYERED_WOOL_CARPET.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL_LAYERS = ITEMS.register(
            "purple_layered_wool_layers",
            () -> new BlockItem(ModBlocks.PURPLE_LAYERED_WOOL_LAYERS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL_VERTICAL_SLAB = ITEMS.register(
            "purple_layered_wool_vertical_slab",
            () -> new BlockItem(ModBlocks.PURPLE_LAYERED_WOOL_VERTICAL_SLAB.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> BLUE_LAYERED_WOOL_SLAB = ITEMS.register(
            "blue_layered_wool_slab",
            () -> new BlockItem(ModBlocks.BLUE_LAYERED_WOOL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL_STAIRS = ITEMS.register(
            "blue_layered_wool_stairs",
            () -> new BlockItem(ModBlocks.BLUE_LAYERED_WOOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL_WALL = ITEMS.register(
            "blue_layered_wool_wall",
            () -> new BlockItem(ModBlocks.BLUE_LAYERED_WOOL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL_CARPET = ITEMS.register(
            "blue_layered_wool_carpet",
            () -> new BlockItem(ModBlocks.BLUE_LAYERED_WOOL_CARPET.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL_LAYERS = ITEMS.register(
            "blue_layered_wool_layers",
            () -> new BlockItem(ModBlocks.BLUE_LAYERED_WOOL_LAYERS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL_VERTICAL_SLAB = ITEMS.register(
            "blue_layered_wool_vertical_slab",
            () -> new BlockItem(ModBlocks.BLUE_LAYERED_WOOL_VERTICAL_SLAB.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> BROWN_LAYERED_WOOL_SLAB = ITEMS.register(
            "brown_layered_wool_slab",
            () -> new BlockItem(ModBlocks.BROWN_LAYERED_WOOL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL_STAIRS = ITEMS.register(
            "brown_layered_wool_stairs",
            () -> new BlockItem(ModBlocks.BROWN_LAYERED_WOOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL_WALL = ITEMS.register(
            "brown_layered_wool_wall",
            () -> new BlockItem(ModBlocks.BROWN_LAYERED_WOOL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL_CARPET = ITEMS.register(
            "brown_layered_wool_carpet",
            () -> new BlockItem(ModBlocks.BROWN_LAYERED_WOOL_CARPET.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL_LAYERS = ITEMS.register(
            "brown_layered_wool_layers",
            () -> new BlockItem(ModBlocks.BROWN_LAYERED_WOOL_LAYERS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL_VERTICAL_SLAB = ITEMS.register(
            "brown_layered_wool_vertical_slab",
            () -> new BlockItem(ModBlocks.BROWN_LAYERED_WOOL_VERTICAL_SLAB.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> GREEN_LAYERED_WOOL_SLAB = ITEMS.register(
            "green_layered_wool_slab",
            () -> new BlockItem(ModBlocks.GREEN_LAYERED_WOOL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL_STAIRS = ITEMS.register(
            "green_layered_wool_stairs",
            () -> new BlockItem(ModBlocks.GREEN_LAYERED_WOOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL_WALL = ITEMS.register(
            "green_layered_wool_wall",
            () -> new BlockItem(ModBlocks.GREEN_LAYERED_WOOL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL_CARPET = ITEMS.register(
            "green_layered_wool_carpet",
            () -> new BlockItem(ModBlocks.GREEN_LAYERED_WOOL_CARPET.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL_LAYERS = ITEMS.register(
            "green_layered_wool_layers",
            () -> new BlockItem(ModBlocks.GREEN_LAYERED_WOOL_LAYERS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL_VERTICAL_SLAB = ITEMS.register(
            "green_layered_wool_vertical_slab",
            () -> new BlockItem(ModBlocks.GREEN_LAYERED_WOOL_VERTICAL_SLAB.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> RED_LAYERED_WOOL_SLAB = ITEMS.register(
            "red_layered_wool_slab",
            () -> new BlockItem(ModBlocks.RED_LAYERED_WOOL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_LAYERED_WOOL_STAIRS = ITEMS.register(
            "red_layered_wool_stairs",
            () -> new BlockItem(ModBlocks.RED_LAYERED_WOOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_LAYERED_WOOL_WALL = ITEMS.register(
            "red_layered_wool_wall",
            () -> new BlockItem(ModBlocks.RED_LAYERED_WOOL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_LAYERED_WOOL_CARPET = ITEMS.register(
            "red_layered_wool_carpet",
            () -> new BlockItem(ModBlocks.RED_LAYERED_WOOL_CARPET.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_LAYERED_WOOL_LAYERS = ITEMS.register(
            "red_layered_wool_layers",
            () -> new BlockItem(ModBlocks.RED_LAYERED_WOOL_LAYERS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_LAYERED_WOOL_VERTICAL_SLAB = ITEMS.register(
            "red_layered_wool_vertical_slab",
            () -> new BlockItem(ModBlocks.RED_LAYERED_WOOL_VERTICAL_SLAB.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> BLACK_LAYERED_WOOL_SLAB = ITEMS.register(
            "black_layered_wool_slab",
            () -> new BlockItem(ModBlocks.BLACK_LAYERED_WOOL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL_STAIRS = ITEMS.register(
            "black_layered_wool_stairs",
            () -> new BlockItem(ModBlocks.BLACK_LAYERED_WOOL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL_WALL = ITEMS.register(
            "black_layered_wool_wall",
            () -> new BlockItem(ModBlocks.BLACK_LAYERED_WOOL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL_CARPET = ITEMS.register(
            "black_layered_wool_carpet",
            () -> new BlockItem(ModBlocks.BLACK_LAYERED_WOOL_CARPET.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL_LAYERS = ITEMS.register(
            "black_layered_wool_layers",
            () -> new BlockItem(ModBlocks.BLACK_LAYERED_WOOL_LAYERS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL_VERTICAL_SLAB = ITEMS.register(
            "black_layered_wool_vertical_slab",
            () -> new BlockItem(ModBlocks.BLACK_LAYERED_WOOL_VERTICAL_SLAB.get(), createBlockItemProperties()));


public static final RegistryObject<Item> FLAMING_STEEL_INGOT = ITEMS.register("flaming_steel_ingot",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB)));
    public static final RegistryObject<Item> FLAMING_STEEL_NUGGET = ITEMS.register("flaming_steel_nugget",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB)));
    public static final RegistryObject<Item> FLAMING_STEEL_BLOCK = ITEMS.register("flaming_steel_block",
            () -> new BlockItem(ModBlocks.FLAMING_STEEL_BLOCK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FLAMING_STEEL_BLOCK_SLAB = ITEMS.register("flaming_steel_block_slab",
            () -> new BlockItem(ModBlocks.FLAMING_STEEL_BLOCK_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FLAMING_STEEL_BLOCK_VERTICAL_SLAB = ITEMS.register("flaming_steel_block_vertical_slab",
            () -> new BlockItem(ModBlocks.FLAMING_STEEL_BLOCK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FLAMING_STEEL_BLOCK_STAIRS = ITEMS.register("flaming_steel_block_stairs",
            () -> new BlockItem(ModBlocks.FLAMING_STEEL_BLOCK_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FLAMING_STEEL_BLOCK_WALL = ITEMS.register("flaming_steel_block_wall",
            () -> new BlockItem(ModBlocks.FLAMING_STEEL_BLOCK_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FLAMING_CUT_STEEL = ITEMS.register("flaming_cut_steel",
            () -> new BlockItem(ModBlocks.FLAMING_CUT_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FLAMING_CUT_STEEL_SLAB = ITEMS.register("flaming_cut_steel_slab",
            () -> new BlockItem(ModBlocks.FLAMING_CUT_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FLAMING_CUT_STEEL_VERTICAL_SLAB = ITEMS.register("flaming_cut_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.FLAMING_CUT_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FLAMING_CUT_STEEL_STAIRS = ITEMS.register("flaming_cut_steel_stairs",
            () -> new BlockItem(ModBlocks.FLAMING_CUT_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FLAMING_CUT_STEEL_WALL = ITEMS.register("flaming_cut_steel_wall",
            () -> new BlockItem(ModBlocks.FLAMING_CUT_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FLAMING_POLISHED_STEEL = ITEMS.register("flaming_polished_steel",
            () -> new BlockItem(ModBlocks.FLAMING_POLISHED_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FLAMING_POLISHED_STEEL_SLAB = ITEMS.register("flaming_polished_steel_slab",
            () -> new BlockItem(ModBlocks.FLAMING_POLISHED_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FLAMING_POLISHED_STEEL_VERTICAL_SLAB = ITEMS.register("flaming_polished_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.FLAMING_POLISHED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FLAMING_POLISHED_STEEL_STAIRS = ITEMS.register("flaming_polished_steel_stairs",
            () -> new BlockItem(ModBlocks.FLAMING_POLISHED_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FLAMING_POLISHED_STEEL_WALL = ITEMS.register("flaming_polished_steel_wall",
            () -> new BlockItem(ModBlocks.FLAMING_POLISHED_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FLAMING_PRESSED_STEEL = ITEMS.register("flaming_pressed_steel",
            () -> new BlockItem(ModBlocks.FLAMING_PRESSED_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FLAMING_PRESSED_STEEL_SLAB = ITEMS.register("flaming_pressed_steel_slab",
            () -> new BlockItem(ModBlocks.FLAMING_PRESSED_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FLAMING_PRESSED_STEEL_VERTICAL_SLAB = ITEMS.register("flaming_pressed_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.FLAMING_PRESSED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FLAMING_PRESSED_STEEL_STAIRS = ITEMS.register("flaming_pressed_steel_stairs",
            () -> new BlockItem(ModBlocks.FLAMING_PRESSED_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FLAMING_PRESSED_STEEL_WALL = ITEMS.register("flaming_pressed_steel_wall",
            () -> new BlockItem(ModBlocks.FLAMING_PRESSED_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FLAMING_STEEL_CASING = ITEMS.register("flaming_steel_casing",
            () -> new BlockItem(ModBlocks.FLAMING_STEEL_CASING.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FLAMING_STEEL_GRATE = ITEMS.register("flaming_steel_grate",
            () -> new BlockItem(ModBlocks.FLAMING_STEEL_GRATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FLAMING_STEEL_PILLAR = ITEMS.register("flaming_steel_pillar",
            () -> new BlockItem(ModBlocks.FLAMING_STEEL_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FLAMING_BOLTED_STEEL_PILLAR = ITEMS.register("flaming_bolted_steel_pillar",
            () -> new BlockItem(ModBlocks.FLAMING_BOLTED_STEEL_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FLAMING_STEEL_BOLTS = ITEMS.register("flaming_steel_bolts",
            () -> new BlockItem(ModBlocks.FLAMING_STEEL_BOLTS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FLAMING_STEEL_DOOR = ITEMS.register("flaming_steel_door",
            () -> new DoubleHighBlockItem(ModBlocks.FLAMING_STEEL_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FLAMING_STEEL_TRAPDOOR = ITEMS.register("flaming_steel_trapdoor",
            () -> new BlockItem(ModBlocks.FLAMING_STEEL_TRAPDOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FLAMING_STEEL_BUTTON = ITEMS.register("flaming_steel_button",
            () -> new BlockItem(ModBlocks.FLAMING_STEEL_BUTTON.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FLAMING_STEEL_PRESSURE_PLATE = ITEMS.register("flaming_steel_pressure_plate",
            () -> new BlockItem(ModBlocks.FLAMING_STEEL_PRESSURE_PLATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_BLOCK = ITEMS.register("magenta_flaming_steel_block",
            () -> new BlockItem(ModBlocks.MAGENTA_FLAMING_STEEL_BLOCK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_BLOCK_SLAB = ITEMS.register("magenta_flaming_steel_block_slab",
            () -> new BlockItem(ModBlocks.MAGENTA_FLAMING_STEEL_BLOCK_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = ITEMS.register("magenta_flaming_steel_block_vertical_slab",
            () -> new BlockItem(ModBlocks.MAGENTA_FLAMING_STEEL_BLOCK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_BLOCK_STAIRS = ITEMS.register("magenta_flaming_steel_block_stairs",
            () -> new BlockItem(ModBlocks.MAGENTA_FLAMING_STEEL_BLOCK_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_BLOCK_WALL = ITEMS.register("magenta_flaming_steel_block_wall",
            () -> new BlockItem(ModBlocks.MAGENTA_FLAMING_STEEL_BLOCK_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_FLAMING_CUT_STEEL = ITEMS.register("magenta_flaming_cut_steel",
            () -> new BlockItem(ModBlocks.MAGENTA_FLAMING_CUT_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_FLAMING_CUT_STEEL_SLAB = ITEMS.register("magenta_flaming_cut_steel_slab",
            () -> new BlockItem(ModBlocks.MAGENTA_FLAMING_CUT_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_FLAMING_CUT_STEEL_VERTICAL_SLAB = ITEMS.register("magenta_flaming_cut_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.MAGENTA_FLAMING_CUT_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_FLAMING_CUT_STEEL_STAIRS = ITEMS.register("magenta_flaming_cut_steel_stairs",
            () -> new BlockItem(ModBlocks.MAGENTA_FLAMING_CUT_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_FLAMING_CUT_STEEL_WALL = ITEMS.register("magenta_flaming_cut_steel_wall",
            () -> new BlockItem(ModBlocks.MAGENTA_FLAMING_CUT_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_FLAMING_POLISHED_STEEL = ITEMS.register("magenta_flaming_polished_steel",
            () -> new BlockItem(ModBlocks.MAGENTA_FLAMING_POLISHED_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_FLAMING_POLISHED_STEEL_SLAB = ITEMS.register("magenta_flaming_polished_steel_slab",
            () -> new BlockItem(ModBlocks.MAGENTA_FLAMING_POLISHED_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = ITEMS.register("magenta_flaming_polished_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.MAGENTA_FLAMING_POLISHED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_FLAMING_POLISHED_STEEL_STAIRS = ITEMS.register("magenta_flaming_polished_steel_stairs",
            () -> new BlockItem(ModBlocks.MAGENTA_FLAMING_POLISHED_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_FLAMING_POLISHED_STEEL_WALL = ITEMS.register("magenta_flaming_polished_steel_wall",
            () -> new BlockItem(ModBlocks.MAGENTA_FLAMING_POLISHED_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_FLAMING_PRESSED_STEEL = ITEMS.register("magenta_flaming_pressed_steel",
            () -> new BlockItem(ModBlocks.MAGENTA_FLAMING_PRESSED_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_FLAMING_PRESSED_STEEL_SLAB = ITEMS.register("magenta_flaming_pressed_steel_slab",
            () -> new BlockItem(ModBlocks.MAGENTA_FLAMING_PRESSED_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = ITEMS.register("magenta_flaming_pressed_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.MAGENTA_FLAMING_PRESSED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_FLAMING_PRESSED_STEEL_STAIRS = ITEMS.register("magenta_flaming_pressed_steel_stairs",
            () -> new BlockItem(ModBlocks.MAGENTA_FLAMING_PRESSED_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_FLAMING_PRESSED_STEEL_WALL = ITEMS.register("magenta_flaming_pressed_steel_wall",
            () -> new BlockItem(ModBlocks.MAGENTA_FLAMING_PRESSED_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_CASING = ITEMS.register("magenta_flaming_steel_casing",
            () -> new BlockItem(ModBlocks.MAGENTA_FLAMING_STEEL_CASING.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_GRATE = ITEMS.register("magenta_flaming_steel_grate",
            () -> new BlockItem(ModBlocks.MAGENTA_FLAMING_STEEL_GRATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_PILLAR = ITEMS.register("magenta_flaming_steel_pillar",
            () -> new BlockItem(ModBlocks.MAGENTA_FLAMING_STEEL_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_FLAMING_BOLTED_STEEL_PILLAR = ITEMS.register("magenta_flaming_bolted_steel_pillar",
            () -> new BlockItem(ModBlocks.MAGENTA_FLAMING_BOLTED_STEEL_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_STEEL_BLOCK = ITEMS.register("light_blue_flaming_steel_block",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_FLAMING_STEEL_BLOCK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_STEEL_BLOCK_SLAB = ITEMS.register("light_blue_flaming_steel_block_slab",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_FLAMING_STEEL_BLOCK_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = ITEMS.register("light_blue_flaming_steel_block_vertical_slab",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_FLAMING_STEEL_BLOCK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_STEEL_BLOCK_STAIRS = ITEMS.register("light_blue_flaming_steel_block_stairs",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_FLAMING_STEEL_BLOCK_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_STEEL_BLOCK_WALL = ITEMS.register("light_blue_flaming_steel_block_wall",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_FLAMING_STEEL_BLOCK_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_CUT_STEEL = ITEMS.register("light_blue_flaming_cut_steel",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_FLAMING_CUT_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_CUT_STEEL_SLAB = ITEMS.register("light_blue_flaming_cut_steel_slab",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_FLAMING_CUT_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_CUT_STEEL_VERTICAL_SLAB = ITEMS.register("light_blue_flaming_cut_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_FLAMING_CUT_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_CUT_STEEL_STAIRS = ITEMS.register("light_blue_flaming_cut_steel_stairs",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_FLAMING_CUT_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_CUT_STEEL_WALL = ITEMS.register("light_blue_flaming_cut_steel_wall",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_FLAMING_CUT_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_POLISHED_STEEL = ITEMS.register("light_blue_flaming_polished_steel",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_FLAMING_POLISHED_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_POLISHED_STEEL_SLAB = ITEMS.register("light_blue_flaming_polished_steel_slab",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_FLAMING_POLISHED_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = ITEMS.register("light_blue_flaming_polished_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_FLAMING_POLISHED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_POLISHED_STEEL_STAIRS = ITEMS.register("light_blue_flaming_polished_steel_stairs",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_FLAMING_POLISHED_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_POLISHED_STEEL_WALL = ITEMS.register("light_blue_flaming_polished_steel_wall",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_FLAMING_POLISHED_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_PRESSED_STEEL = ITEMS.register("light_blue_flaming_pressed_steel",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_FLAMING_PRESSED_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_PRESSED_STEEL_SLAB = ITEMS.register("light_blue_flaming_pressed_steel_slab",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_FLAMING_PRESSED_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = ITEMS.register("light_blue_flaming_pressed_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_FLAMING_PRESSED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_PRESSED_STEEL_STAIRS = ITEMS.register("light_blue_flaming_pressed_steel_stairs",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_FLAMING_PRESSED_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_PRESSED_STEEL_WALL = ITEMS.register("light_blue_flaming_pressed_steel_wall",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_FLAMING_PRESSED_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_STEEL_CASING = ITEMS.register("light_blue_flaming_steel_casing",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_FLAMING_STEEL_CASING.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_STEEL_GRATE = ITEMS.register("light_blue_flaming_steel_grate",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_FLAMING_STEEL_GRATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_STEEL_PILLAR = ITEMS.register("light_blue_flaming_steel_pillar",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_FLAMING_STEEL_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_BOLTED_STEEL_PILLAR = ITEMS.register("light_blue_flaming_bolted_steel_pillar",
            () -> new BlockItem(ModBlocks.LIGHT_BLUE_FLAMING_BOLTED_STEEL_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_FLAMING_STEEL_BLOCK = ITEMS.register("yellow_flaming_steel_block",
            () -> new BlockItem(ModBlocks.YELLOW_FLAMING_STEEL_BLOCK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_FLAMING_STEEL_BLOCK_SLAB = ITEMS.register("yellow_flaming_steel_block_slab",
            () -> new BlockItem(ModBlocks.YELLOW_FLAMING_STEEL_BLOCK_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = ITEMS.register("yellow_flaming_steel_block_vertical_slab",
            () -> new BlockItem(ModBlocks.YELLOW_FLAMING_STEEL_BLOCK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_FLAMING_STEEL_BLOCK_STAIRS = ITEMS.register("yellow_flaming_steel_block_stairs",
            () -> new BlockItem(ModBlocks.YELLOW_FLAMING_STEEL_BLOCK_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_FLAMING_STEEL_BLOCK_WALL = ITEMS.register("yellow_flaming_steel_block_wall",
            () -> new BlockItem(ModBlocks.YELLOW_FLAMING_STEEL_BLOCK_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_FLAMING_CUT_STEEL = ITEMS.register("yellow_flaming_cut_steel",
            () -> new BlockItem(ModBlocks.YELLOW_FLAMING_CUT_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_FLAMING_CUT_STEEL_SLAB = ITEMS.register("yellow_flaming_cut_steel_slab",
            () -> new BlockItem(ModBlocks.YELLOW_FLAMING_CUT_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_FLAMING_CUT_STEEL_VERTICAL_SLAB = ITEMS.register("yellow_flaming_cut_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.YELLOW_FLAMING_CUT_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_FLAMING_CUT_STEEL_STAIRS = ITEMS.register("yellow_flaming_cut_steel_stairs",
            () -> new BlockItem(ModBlocks.YELLOW_FLAMING_CUT_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_FLAMING_CUT_STEEL_WALL = ITEMS.register("yellow_flaming_cut_steel_wall",
            () -> new BlockItem(ModBlocks.YELLOW_FLAMING_CUT_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_FLAMING_POLISHED_STEEL = ITEMS.register("yellow_flaming_polished_steel",
            () -> new BlockItem(ModBlocks.YELLOW_FLAMING_POLISHED_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_FLAMING_POLISHED_STEEL_SLAB = ITEMS.register("yellow_flaming_polished_steel_slab",
            () -> new BlockItem(ModBlocks.YELLOW_FLAMING_POLISHED_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = ITEMS.register("yellow_flaming_polished_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.YELLOW_FLAMING_POLISHED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_FLAMING_POLISHED_STEEL_STAIRS = ITEMS.register("yellow_flaming_polished_steel_stairs",
            () -> new BlockItem(ModBlocks.YELLOW_FLAMING_POLISHED_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_FLAMING_POLISHED_STEEL_WALL = ITEMS.register("yellow_flaming_polished_steel_wall",
            () -> new BlockItem(ModBlocks.YELLOW_FLAMING_POLISHED_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_FLAMING_PRESSED_STEEL = ITEMS.register("yellow_flaming_pressed_steel",
            () -> new BlockItem(ModBlocks.YELLOW_FLAMING_PRESSED_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_FLAMING_PRESSED_STEEL_SLAB = ITEMS.register("yellow_flaming_pressed_steel_slab",
            () -> new BlockItem(ModBlocks.YELLOW_FLAMING_PRESSED_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = ITEMS.register("yellow_flaming_pressed_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.YELLOW_FLAMING_PRESSED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_FLAMING_PRESSED_STEEL_STAIRS = ITEMS.register("yellow_flaming_pressed_steel_stairs",
            () -> new BlockItem(ModBlocks.YELLOW_FLAMING_PRESSED_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_FLAMING_PRESSED_STEEL_WALL = ITEMS.register("yellow_flaming_pressed_steel_wall",
            () -> new BlockItem(ModBlocks.YELLOW_FLAMING_PRESSED_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_FLAMING_STEEL_CASING = ITEMS.register("yellow_flaming_steel_casing",
            () -> new BlockItem(ModBlocks.YELLOW_FLAMING_STEEL_CASING.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_FLAMING_STEEL_GRATE = ITEMS.register("yellow_flaming_steel_grate",
            () -> new BlockItem(ModBlocks.YELLOW_FLAMING_STEEL_GRATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_FLAMING_STEEL_PILLAR = ITEMS.register("yellow_flaming_steel_pillar",
            () -> new BlockItem(ModBlocks.YELLOW_FLAMING_STEEL_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_FLAMING_BOLTED_STEEL_PILLAR = ITEMS.register("yellow_flaming_bolted_steel_pillar",
            () -> new BlockItem(ModBlocks.YELLOW_FLAMING_BOLTED_STEEL_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_FLAMING_STEEL_BLOCK = ITEMS.register("lime_flaming_steel_block",
            () -> new BlockItem(ModBlocks.LIME_FLAMING_STEEL_BLOCK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_FLAMING_STEEL_BLOCK_SLAB = ITEMS.register("lime_flaming_steel_block_slab",
            () -> new BlockItem(ModBlocks.LIME_FLAMING_STEEL_BLOCK_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = ITEMS.register("lime_flaming_steel_block_vertical_slab",
            () -> new BlockItem(ModBlocks.LIME_FLAMING_STEEL_BLOCK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_FLAMING_STEEL_BLOCK_STAIRS = ITEMS.register("lime_flaming_steel_block_stairs",
            () -> new BlockItem(ModBlocks.LIME_FLAMING_STEEL_BLOCK_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_FLAMING_STEEL_BLOCK_WALL = ITEMS.register("lime_flaming_steel_block_wall",
            () -> new BlockItem(ModBlocks.LIME_FLAMING_STEEL_BLOCK_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_FLAMING_CUT_STEEL = ITEMS.register("lime_flaming_cut_steel",
            () -> new BlockItem(ModBlocks.LIME_FLAMING_CUT_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_FLAMING_CUT_STEEL_SLAB = ITEMS.register("lime_flaming_cut_steel_slab",
            () -> new BlockItem(ModBlocks.LIME_FLAMING_CUT_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_FLAMING_CUT_STEEL_VERTICAL_SLAB = ITEMS.register("lime_flaming_cut_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.LIME_FLAMING_CUT_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_FLAMING_CUT_STEEL_STAIRS = ITEMS.register("lime_flaming_cut_steel_stairs",
            () -> new BlockItem(ModBlocks.LIME_FLAMING_CUT_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_FLAMING_CUT_STEEL_WALL = ITEMS.register("lime_flaming_cut_steel_wall",
            () -> new BlockItem(ModBlocks.LIME_FLAMING_CUT_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_FLAMING_POLISHED_STEEL = ITEMS.register("lime_flaming_polished_steel",
            () -> new BlockItem(ModBlocks.LIME_FLAMING_POLISHED_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_FLAMING_POLISHED_STEEL_SLAB = ITEMS.register("lime_flaming_polished_steel_slab",
            () -> new BlockItem(ModBlocks.LIME_FLAMING_POLISHED_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = ITEMS.register("lime_flaming_polished_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.LIME_FLAMING_POLISHED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_FLAMING_POLISHED_STEEL_STAIRS = ITEMS.register("lime_flaming_polished_steel_stairs",
            () -> new BlockItem(ModBlocks.LIME_FLAMING_POLISHED_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_FLAMING_POLISHED_STEEL_WALL = ITEMS.register("lime_flaming_polished_steel_wall",
            () -> new BlockItem(ModBlocks.LIME_FLAMING_POLISHED_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_FLAMING_PRESSED_STEEL = ITEMS.register("lime_flaming_pressed_steel",
            () -> new BlockItem(ModBlocks.LIME_FLAMING_PRESSED_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_FLAMING_PRESSED_STEEL_SLAB = ITEMS.register("lime_flaming_pressed_steel_slab",
            () -> new BlockItem(ModBlocks.LIME_FLAMING_PRESSED_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = ITEMS.register("lime_flaming_pressed_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.LIME_FLAMING_PRESSED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_FLAMING_PRESSED_STEEL_STAIRS = ITEMS.register("lime_flaming_pressed_steel_stairs",
            () -> new BlockItem(ModBlocks.LIME_FLAMING_PRESSED_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_FLAMING_PRESSED_STEEL_WALL = ITEMS.register("lime_flaming_pressed_steel_wall",
            () -> new BlockItem(ModBlocks.LIME_FLAMING_PRESSED_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_FLAMING_STEEL_CASING = ITEMS.register("lime_flaming_steel_casing",
            () -> new BlockItem(ModBlocks.LIME_FLAMING_STEEL_CASING.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_FLAMING_STEEL_GRATE = ITEMS.register("lime_flaming_steel_grate",
            () -> new BlockItem(ModBlocks.LIME_FLAMING_STEEL_GRATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_FLAMING_STEEL_PILLAR = ITEMS.register("lime_flaming_steel_pillar",
            () -> new BlockItem(ModBlocks.LIME_FLAMING_STEEL_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_FLAMING_BOLTED_STEEL_PILLAR = ITEMS.register("lime_flaming_bolted_steel_pillar",
            () -> new BlockItem(ModBlocks.LIME_FLAMING_BOLTED_STEEL_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_FLAMING_STEEL_BLOCK = ITEMS.register("pink_flaming_steel_block",
            () -> new BlockItem(ModBlocks.PINK_FLAMING_STEEL_BLOCK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_FLAMING_STEEL_BLOCK_SLAB = ITEMS.register("pink_flaming_steel_block_slab",
            () -> new BlockItem(ModBlocks.PINK_FLAMING_STEEL_BLOCK_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = ITEMS.register("pink_flaming_steel_block_vertical_slab",
            () -> new BlockItem(ModBlocks.PINK_FLAMING_STEEL_BLOCK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_FLAMING_STEEL_BLOCK_STAIRS = ITEMS.register("pink_flaming_steel_block_stairs",
            () -> new BlockItem(ModBlocks.PINK_FLAMING_STEEL_BLOCK_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_FLAMING_STEEL_BLOCK_WALL = ITEMS.register("pink_flaming_steel_block_wall",
            () -> new BlockItem(ModBlocks.PINK_FLAMING_STEEL_BLOCK_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_FLAMING_CUT_STEEL = ITEMS.register("pink_flaming_cut_steel",
            () -> new BlockItem(ModBlocks.PINK_FLAMING_CUT_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_FLAMING_CUT_STEEL_SLAB = ITEMS.register("pink_flaming_cut_steel_slab",
            () -> new BlockItem(ModBlocks.PINK_FLAMING_CUT_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_FLAMING_CUT_STEEL_VERTICAL_SLAB = ITEMS.register("pink_flaming_cut_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.PINK_FLAMING_CUT_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_FLAMING_CUT_STEEL_STAIRS = ITEMS.register("pink_flaming_cut_steel_stairs",
            () -> new BlockItem(ModBlocks.PINK_FLAMING_CUT_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_FLAMING_CUT_STEEL_WALL = ITEMS.register("pink_flaming_cut_steel_wall",
            () -> new BlockItem(ModBlocks.PINK_FLAMING_CUT_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_FLAMING_POLISHED_STEEL = ITEMS.register("pink_flaming_polished_steel",
            () -> new BlockItem(ModBlocks.PINK_FLAMING_POLISHED_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_FLAMING_POLISHED_STEEL_SLAB = ITEMS.register("pink_flaming_polished_steel_slab",
            () -> new BlockItem(ModBlocks.PINK_FLAMING_POLISHED_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = ITEMS.register("pink_flaming_polished_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.PINK_FLAMING_POLISHED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_FLAMING_POLISHED_STEEL_STAIRS = ITEMS.register("pink_flaming_polished_steel_stairs",
            () -> new BlockItem(ModBlocks.PINK_FLAMING_POLISHED_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_FLAMING_POLISHED_STEEL_WALL = ITEMS.register("pink_flaming_polished_steel_wall",
            () -> new BlockItem(ModBlocks.PINK_FLAMING_POLISHED_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_FLAMING_PRESSED_STEEL = ITEMS.register("pink_flaming_pressed_steel",
            () -> new BlockItem(ModBlocks.PINK_FLAMING_PRESSED_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_FLAMING_PRESSED_STEEL_SLAB = ITEMS.register("pink_flaming_pressed_steel_slab",
            () -> new BlockItem(ModBlocks.PINK_FLAMING_PRESSED_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = ITEMS.register("pink_flaming_pressed_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.PINK_FLAMING_PRESSED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_FLAMING_PRESSED_STEEL_STAIRS = ITEMS.register("pink_flaming_pressed_steel_stairs",
            () -> new BlockItem(ModBlocks.PINK_FLAMING_PRESSED_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_FLAMING_PRESSED_STEEL_WALL = ITEMS.register("pink_flaming_pressed_steel_wall",
            () -> new BlockItem(ModBlocks.PINK_FLAMING_PRESSED_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_FLAMING_STEEL_CASING = ITEMS.register("pink_flaming_steel_casing",
            () -> new BlockItem(ModBlocks.PINK_FLAMING_STEEL_CASING.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_FLAMING_STEEL_GRATE = ITEMS.register("pink_flaming_steel_grate",
            () -> new BlockItem(ModBlocks.PINK_FLAMING_STEEL_GRATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_FLAMING_STEEL_PILLAR = ITEMS.register("pink_flaming_steel_pillar",
            () -> new BlockItem(ModBlocks.PINK_FLAMING_STEEL_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_FLAMING_BOLTED_STEEL_PILLAR = ITEMS.register("pink_flaming_bolted_steel_pillar",
            () -> new BlockItem(ModBlocks.PINK_FLAMING_BOLTED_STEEL_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_FLAMING_STEEL_BLOCK = ITEMS.register("gray_flaming_steel_block",
            () -> new BlockItem(ModBlocks.GRAY_FLAMING_STEEL_BLOCK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_FLAMING_STEEL_BLOCK_SLAB = ITEMS.register("gray_flaming_steel_block_slab",
            () -> new BlockItem(ModBlocks.GRAY_FLAMING_STEEL_BLOCK_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = ITEMS.register("gray_flaming_steel_block_vertical_slab",
            () -> new BlockItem(ModBlocks.GRAY_FLAMING_STEEL_BLOCK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_FLAMING_STEEL_BLOCK_STAIRS = ITEMS.register("gray_flaming_steel_block_stairs",
            () -> new BlockItem(ModBlocks.GRAY_FLAMING_STEEL_BLOCK_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_FLAMING_STEEL_BLOCK_WALL = ITEMS.register("gray_flaming_steel_block_wall",
            () -> new BlockItem(ModBlocks.GRAY_FLAMING_STEEL_BLOCK_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_FLAMING_CUT_STEEL = ITEMS.register("gray_flaming_cut_steel",
            () -> new BlockItem(ModBlocks.GRAY_FLAMING_CUT_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_FLAMING_CUT_STEEL_SLAB = ITEMS.register("gray_flaming_cut_steel_slab",
            () -> new BlockItem(ModBlocks.GRAY_FLAMING_CUT_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_FLAMING_CUT_STEEL_VERTICAL_SLAB = ITEMS.register("gray_flaming_cut_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.GRAY_FLAMING_CUT_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_FLAMING_CUT_STEEL_STAIRS = ITEMS.register("gray_flaming_cut_steel_stairs",
            () -> new BlockItem(ModBlocks.GRAY_FLAMING_CUT_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_FLAMING_CUT_STEEL_WALL = ITEMS.register("gray_flaming_cut_steel_wall",
            () -> new BlockItem(ModBlocks.GRAY_FLAMING_CUT_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_FLAMING_POLISHED_STEEL = ITEMS.register("gray_flaming_polished_steel",
            () -> new BlockItem(ModBlocks.GRAY_FLAMING_POLISHED_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_FLAMING_POLISHED_STEEL_SLAB = ITEMS.register("gray_flaming_polished_steel_slab",
            () -> new BlockItem(ModBlocks.GRAY_FLAMING_POLISHED_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = ITEMS.register("gray_flaming_polished_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.GRAY_FLAMING_POLISHED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_FLAMING_POLISHED_STEEL_STAIRS = ITEMS.register("gray_flaming_polished_steel_stairs",
            () -> new BlockItem(ModBlocks.GRAY_FLAMING_POLISHED_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_FLAMING_POLISHED_STEEL_WALL = ITEMS.register("gray_flaming_polished_steel_wall",
            () -> new BlockItem(ModBlocks.GRAY_FLAMING_POLISHED_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_FLAMING_PRESSED_STEEL = ITEMS.register("gray_flaming_pressed_steel",
            () -> new BlockItem(ModBlocks.GRAY_FLAMING_PRESSED_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_FLAMING_PRESSED_STEEL_SLAB = ITEMS.register("gray_flaming_pressed_steel_slab",
            () -> new BlockItem(ModBlocks.GRAY_FLAMING_PRESSED_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = ITEMS.register("gray_flaming_pressed_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.GRAY_FLAMING_PRESSED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_FLAMING_PRESSED_STEEL_STAIRS = ITEMS.register("gray_flaming_pressed_steel_stairs",
            () -> new BlockItem(ModBlocks.GRAY_FLAMING_PRESSED_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_FLAMING_PRESSED_STEEL_WALL = ITEMS.register("gray_flaming_pressed_steel_wall",
            () -> new BlockItem(ModBlocks.GRAY_FLAMING_PRESSED_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_FLAMING_STEEL_CASING = ITEMS.register("gray_flaming_steel_casing",
            () -> new BlockItem(ModBlocks.GRAY_FLAMING_STEEL_CASING.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_FLAMING_STEEL_GRATE = ITEMS.register("gray_flaming_steel_grate",
            () -> new BlockItem(ModBlocks.GRAY_FLAMING_STEEL_GRATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_FLAMING_STEEL_PILLAR = ITEMS.register("gray_flaming_steel_pillar",
            () -> new BlockItem(ModBlocks.GRAY_FLAMING_STEEL_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_FLAMING_BOLTED_STEEL_PILLAR = ITEMS.register("gray_flaming_bolted_steel_pillar",
            () -> new BlockItem(ModBlocks.GRAY_FLAMING_BOLTED_STEEL_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_STEEL_BLOCK = ITEMS.register("light_gray_flaming_steel_block",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_FLAMING_STEEL_BLOCK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_STEEL_BLOCK_SLAB = ITEMS.register("light_gray_flaming_steel_block_slab",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_FLAMING_STEEL_BLOCK_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = ITEMS.register("light_gray_flaming_steel_block_vertical_slab",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_FLAMING_STEEL_BLOCK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_STEEL_BLOCK_STAIRS = ITEMS.register("light_gray_flaming_steel_block_stairs",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_FLAMING_STEEL_BLOCK_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_STEEL_BLOCK_WALL = ITEMS.register("light_gray_flaming_steel_block_wall",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_FLAMING_STEEL_BLOCK_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_CUT_STEEL = ITEMS.register("light_gray_flaming_cut_steel",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_FLAMING_CUT_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_CUT_STEEL_SLAB = ITEMS.register("light_gray_flaming_cut_steel_slab",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_FLAMING_CUT_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_CUT_STEEL_VERTICAL_SLAB = ITEMS.register("light_gray_flaming_cut_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_FLAMING_CUT_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_CUT_STEEL_STAIRS = ITEMS.register("light_gray_flaming_cut_steel_stairs",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_FLAMING_CUT_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_CUT_STEEL_WALL = ITEMS.register("light_gray_flaming_cut_steel_wall",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_FLAMING_CUT_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_POLISHED_STEEL = ITEMS.register("light_gray_flaming_polished_steel",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_FLAMING_POLISHED_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_POLISHED_STEEL_SLAB = ITEMS.register("light_gray_flaming_polished_steel_slab",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_FLAMING_POLISHED_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = ITEMS.register("light_gray_flaming_polished_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_FLAMING_POLISHED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_POLISHED_STEEL_STAIRS = ITEMS.register("light_gray_flaming_polished_steel_stairs",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_FLAMING_POLISHED_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_POLISHED_STEEL_WALL = ITEMS.register("light_gray_flaming_polished_steel_wall",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_FLAMING_POLISHED_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_PRESSED_STEEL = ITEMS.register("light_gray_flaming_pressed_steel",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_FLAMING_PRESSED_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_PRESSED_STEEL_SLAB = ITEMS.register("light_gray_flaming_pressed_steel_slab",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_FLAMING_PRESSED_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = ITEMS.register("light_gray_flaming_pressed_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_FLAMING_PRESSED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_PRESSED_STEEL_STAIRS = ITEMS.register("light_gray_flaming_pressed_steel_stairs",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_FLAMING_PRESSED_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_PRESSED_STEEL_WALL = ITEMS.register("light_gray_flaming_pressed_steel_wall",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_FLAMING_PRESSED_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_STEEL_CASING = ITEMS.register("light_gray_flaming_steel_casing",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_FLAMING_STEEL_CASING.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_STEEL_GRATE = ITEMS.register("light_gray_flaming_steel_grate",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_FLAMING_STEEL_GRATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_STEEL_PILLAR = ITEMS.register("light_gray_flaming_steel_pillar",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_FLAMING_STEEL_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_BOLTED_STEEL_PILLAR = ITEMS.register("light_gray_flaming_bolted_steel_pillar",
            () -> new BlockItem(ModBlocks.LIGHT_GRAY_FLAMING_BOLTED_STEEL_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_FLAMING_STEEL_BLOCK = ITEMS.register("cyan_flaming_steel_block",
            () -> new BlockItem(ModBlocks.CYAN_FLAMING_STEEL_BLOCK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_FLAMING_STEEL_BLOCK_SLAB = ITEMS.register("cyan_flaming_steel_block_slab",
            () -> new BlockItem(ModBlocks.CYAN_FLAMING_STEEL_BLOCK_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = ITEMS.register("cyan_flaming_steel_block_vertical_slab",
            () -> new BlockItem(ModBlocks.CYAN_FLAMING_STEEL_BLOCK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_FLAMING_STEEL_BLOCK_STAIRS = ITEMS.register("cyan_flaming_steel_block_stairs",
            () -> new BlockItem(ModBlocks.CYAN_FLAMING_STEEL_BLOCK_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_FLAMING_STEEL_BLOCK_WALL = ITEMS.register("cyan_flaming_steel_block_wall",
            () -> new BlockItem(ModBlocks.CYAN_FLAMING_STEEL_BLOCK_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_FLAMING_CUT_STEEL = ITEMS.register("cyan_flaming_cut_steel",
            () -> new BlockItem(ModBlocks.CYAN_FLAMING_CUT_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_FLAMING_CUT_STEEL_SLAB = ITEMS.register("cyan_flaming_cut_steel_slab",
            () -> new BlockItem(ModBlocks.CYAN_FLAMING_CUT_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_FLAMING_CUT_STEEL_VERTICAL_SLAB = ITEMS.register("cyan_flaming_cut_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.CYAN_FLAMING_CUT_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_FLAMING_CUT_STEEL_STAIRS = ITEMS.register("cyan_flaming_cut_steel_stairs",
            () -> new BlockItem(ModBlocks.CYAN_FLAMING_CUT_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_FLAMING_CUT_STEEL_WALL = ITEMS.register("cyan_flaming_cut_steel_wall",
            () -> new BlockItem(ModBlocks.CYAN_FLAMING_CUT_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_FLAMING_POLISHED_STEEL = ITEMS.register("cyan_flaming_polished_steel",
            () -> new BlockItem(ModBlocks.CYAN_FLAMING_POLISHED_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_FLAMING_POLISHED_STEEL_SLAB = ITEMS.register("cyan_flaming_polished_steel_slab",
            () -> new BlockItem(ModBlocks.CYAN_FLAMING_POLISHED_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = ITEMS.register("cyan_flaming_polished_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.CYAN_FLAMING_POLISHED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_FLAMING_POLISHED_STEEL_STAIRS = ITEMS.register("cyan_flaming_polished_steel_stairs",
            () -> new BlockItem(ModBlocks.CYAN_FLAMING_POLISHED_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_FLAMING_POLISHED_STEEL_WALL = ITEMS.register("cyan_flaming_polished_steel_wall",
            () -> new BlockItem(ModBlocks.CYAN_FLAMING_POLISHED_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_FLAMING_PRESSED_STEEL = ITEMS.register("cyan_flaming_pressed_steel",
            () -> new BlockItem(ModBlocks.CYAN_FLAMING_PRESSED_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_FLAMING_PRESSED_STEEL_SLAB = ITEMS.register("cyan_flaming_pressed_steel_slab",
            () -> new BlockItem(ModBlocks.CYAN_FLAMING_PRESSED_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = ITEMS.register("cyan_flaming_pressed_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.CYAN_FLAMING_PRESSED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_FLAMING_PRESSED_STEEL_STAIRS = ITEMS.register("cyan_flaming_pressed_steel_stairs",
            () -> new BlockItem(ModBlocks.CYAN_FLAMING_PRESSED_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_FLAMING_PRESSED_STEEL_WALL = ITEMS.register("cyan_flaming_pressed_steel_wall",
            () -> new BlockItem(ModBlocks.CYAN_FLAMING_PRESSED_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_FLAMING_STEEL_CASING = ITEMS.register("cyan_flaming_steel_casing",
            () -> new BlockItem(ModBlocks.CYAN_FLAMING_STEEL_CASING.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_FLAMING_STEEL_GRATE = ITEMS.register("cyan_flaming_steel_grate",
            () -> new BlockItem(ModBlocks.CYAN_FLAMING_STEEL_GRATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_FLAMING_STEEL_PILLAR = ITEMS.register("cyan_flaming_steel_pillar",
            () -> new BlockItem(ModBlocks.CYAN_FLAMING_STEEL_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_FLAMING_BOLTED_STEEL_PILLAR = ITEMS.register("cyan_flaming_bolted_steel_pillar",
            () -> new BlockItem(ModBlocks.CYAN_FLAMING_BOLTED_STEEL_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_FLAMING_STEEL_BLOCK = ITEMS.register("purple_flaming_steel_block",
            () -> new BlockItem(ModBlocks.PURPLE_FLAMING_STEEL_BLOCK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_FLAMING_STEEL_BLOCK_SLAB = ITEMS.register("purple_flaming_steel_block_slab",
            () -> new BlockItem(ModBlocks.PURPLE_FLAMING_STEEL_BLOCK_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = ITEMS.register("purple_flaming_steel_block_vertical_slab",
            () -> new BlockItem(ModBlocks.PURPLE_FLAMING_STEEL_BLOCK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_FLAMING_STEEL_BLOCK_STAIRS = ITEMS.register("purple_flaming_steel_block_stairs",
            () -> new BlockItem(ModBlocks.PURPLE_FLAMING_STEEL_BLOCK_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_FLAMING_STEEL_BLOCK_WALL = ITEMS.register("purple_flaming_steel_block_wall",
            () -> new BlockItem(ModBlocks.PURPLE_FLAMING_STEEL_BLOCK_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_FLAMING_CUT_STEEL = ITEMS.register("purple_flaming_cut_steel",
            () -> new BlockItem(ModBlocks.PURPLE_FLAMING_CUT_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_FLAMING_CUT_STEEL_SLAB = ITEMS.register("purple_flaming_cut_steel_slab",
            () -> new BlockItem(ModBlocks.PURPLE_FLAMING_CUT_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_FLAMING_CUT_STEEL_VERTICAL_SLAB = ITEMS.register("purple_flaming_cut_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.PURPLE_FLAMING_CUT_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_FLAMING_CUT_STEEL_STAIRS = ITEMS.register("purple_flaming_cut_steel_stairs",
            () -> new BlockItem(ModBlocks.PURPLE_FLAMING_CUT_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_FLAMING_CUT_STEEL_WALL = ITEMS.register("purple_flaming_cut_steel_wall",
            () -> new BlockItem(ModBlocks.PURPLE_FLAMING_CUT_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_FLAMING_POLISHED_STEEL = ITEMS.register("purple_flaming_polished_steel",
            () -> new BlockItem(ModBlocks.PURPLE_FLAMING_POLISHED_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_FLAMING_POLISHED_STEEL_SLAB = ITEMS.register("purple_flaming_polished_steel_slab",
            () -> new BlockItem(ModBlocks.PURPLE_FLAMING_POLISHED_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = ITEMS.register("purple_flaming_polished_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.PURPLE_FLAMING_POLISHED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_FLAMING_POLISHED_STEEL_STAIRS = ITEMS.register("purple_flaming_polished_steel_stairs",
            () -> new BlockItem(ModBlocks.PURPLE_FLAMING_POLISHED_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_FLAMING_POLISHED_STEEL_WALL = ITEMS.register("purple_flaming_polished_steel_wall",
            () -> new BlockItem(ModBlocks.PURPLE_FLAMING_POLISHED_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_FLAMING_PRESSED_STEEL = ITEMS.register("purple_flaming_pressed_steel",
            () -> new BlockItem(ModBlocks.PURPLE_FLAMING_PRESSED_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_FLAMING_PRESSED_STEEL_SLAB = ITEMS.register("purple_flaming_pressed_steel_slab",
            () -> new BlockItem(ModBlocks.PURPLE_FLAMING_PRESSED_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = ITEMS.register("purple_flaming_pressed_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.PURPLE_FLAMING_PRESSED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_FLAMING_PRESSED_STEEL_STAIRS = ITEMS.register("purple_flaming_pressed_steel_stairs",
            () -> new BlockItem(ModBlocks.PURPLE_FLAMING_PRESSED_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_FLAMING_PRESSED_STEEL_WALL = ITEMS.register("purple_flaming_pressed_steel_wall",
            () -> new BlockItem(ModBlocks.PURPLE_FLAMING_PRESSED_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_FLAMING_STEEL_CASING = ITEMS.register("purple_flaming_steel_casing",
            () -> new BlockItem(ModBlocks.PURPLE_FLAMING_STEEL_CASING.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_FLAMING_STEEL_GRATE = ITEMS.register("purple_flaming_steel_grate",
            () -> new BlockItem(ModBlocks.PURPLE_FLAMING_STEEL_GRATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_FLAMING_STEEL_PILLAR = ITEMS.register("purple_flaming_steel_pillar",
            () -> new BlockItem(ModBlocks.PURPLE_FLAMING_STEEL_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_FLAMING_BOLTED_STEEL_PILLAR = ITEMS.register("purple_flaming_bolted_steel_pillar",
            () -> new BlockItem(ModBlocks.PURPLE_FLAMING_BOLTED_STEEL_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_FLAMING_STEEL_BLOCK = ITEMS.register("blue_flaming_steel_block",
            () -> new BlockItem(ModBlocks.BLUE_FLAMING_STEEL_BLOCK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_FLAMING_STEEL_BLOCK_SLAB = ITEMS.register("blue_flaming_steel_block_slab",
            () -> new BlockItem(ModBlocks.BLUE_FLAMING_STEEL_BLOCK_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = ITEMS.register("blue_flaming_steel_block_vertical_slab",
            () -> new BlockItem(ModBlocks.BLUE_FLAMING_STEEL_BLOCK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_FLAMING_STEEL_BLOCK_STAIRS = ITEMS.register("blue_flaming_steel_block_stairs",
            () -> new BlockItem(ModBlocks.BLUE_FLAMING_STEEL_BLOCK_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_FLAMING_STEEL_BLOCK_WALL = ITEMS.register("blue_flaming_steel_block_wall",
            () -> new BlockItem(ModBlocks.BLUE_FLAMING_STEEL_BLOCK_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_FLAMING_CUT_STEEL = ITEMS.register("blue_flaming_cut_steel",
            () -> new BlockItem(ModBlocks.BLUE_FLAMING_CUT_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_FLAMING_CUT_STEEL_SLAB = ITEMS.register("blue_flaming_cut_steel_slab",
            () -> new BlockItem(ModBlocks.BLUE_FLAMING_CUT_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_FLAMING_CUT_STEEL_VERTICAL_SLAB = ITEMS.register("blue_flaming_cut_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.BLUE_FLAMING_CUT_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_FLAMING_CUT_STEEL_STAIRS = ITEMS.register("blue_flaming_cut_steel_stairs",
            () -> new BlockItem(ModBlocks.BLUE_FLAMING_CUT_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_FLAMING_CUT_STEEL_WALL = ITEMS.register("blue_flaming_cut_steel_wall",
            () -> new BlockItem(ModBlocks.BLUE_FLAMING_CUT_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_FLAMING_POLISHED_STEEL = ITEMS.register("blue_flaming_polished_steel",
            () -> new BlockItem(ModBlocks.BLUE_FLAMING_POLISHED_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_FLAMING_POLISHED_STEEL_SLAB = ITEMS.register("blue_flaming_polished_steel_slab",
            () -> new BlockItem(ModBlocks.BLUE_FLAMING_POLISHED_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = ITEMS.register("blue_flaming_polished_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.BLUE_FLAMING_POLISHED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_FLAMING_POLISHED_STEEL_STAIRS = ITEMS.register("blue_flaming_polished_steel_stairs",
            () -> new BlockItem(ModBlocks.BLUE_FLAMING_POLISHED_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_FLAMING_POLISHED_STEEL_WALL = ITEMS.register("blue_flaming_polished_steel_wall",
            () -> new BlockItem(ModBlocks.BLUE_FLAMING_POLISHED_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_FLAMING_PRESSED_STEEL = ITEMS.register("blue_flaming_pressed_steel",
            () -> new BlockItem(ModBlocks.BLUE_FLAMING_PRESSED_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_FLAMING_PRESSED_STEEL_SLAB = ITEMS.register("blue_flaming_pressed_steel_slab",
            () -> new BlockItem(ModBlocks.BLUE_FLAMING_PRESSED_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = ITEMS.register("blue_flaming_pressed_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.BLUE_FLAMING_PRESSED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_FLAMING_PRESSED_STEEL_STAIRS = ITEMS.register("blue_flaming_pressed_steel_stairs",
            () -> new BlockItem(ModBlocks.BLUE_FLAMING_PRESSED_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_FLAMING_PRESSED_STEEL_WALL = ITEMS.register("blue_flaming_pressed_steel_wall",
            () -> new BlockItem(ModBlocks.BLUE_FLAMING_PRESSED_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_FLAMING_STEEL_CASING = ITEMS.register("blue_flaming_steel_casing",
            () -> new BlockItem(ModBlocks.BLUE_FLAMING_STEEL_CASING.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_FLAMING_STEEL_GRATE = ITEMS.register("blue_flaming_steel_grate",
            () -> new BlockItem(ModBlocks.BLUE_FLAMING_STEEL_GRATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_FLAMING_STEEL_PILLAR = ITEMS.register("blue_flaming_steel_pillar",
            () -> new BlockItem(ModBlocks.BLUE_FLAMING_STEEL_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_FLAMING_BOLTED_STEEL_PILLAR = ITEMS.register("blue_flaming_bolted_steel_pillar",
            () -> new BlockItem(ModBlocks.BLUE_FLAMING_BOLTED_STEEL_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_FLAMING_STEEL_BLOCK = ITEMS.register("brown_flaming_steel_block",
            () -> new BlockItem(ModBlocks.BROWN_FLAMING_STEEL_BLOCK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_FLAMING_STEEL_BLOCK_SLAB = ITEMS.register("brown_flaming_steel_block_slab",
            () -> new BlockItem(ModBlocks.BROWN_FLAMING_STEEL_BLOCK_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = ITEMS.register("brown_flaming_steel_block_vertical_slab",
            () -> new BlockItem(ModBlocks.BROWN_FLAMING_STEEL_BLOCK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_FLAMING_STEEL_BLOCK_STAIRS = ITEMS.register("brown_flaming_steel_block_stairs",
            () -> new BlockItem(ModBlocks.BROWN_FLAMING_STEEL_BLOCK_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_FLAMING_STEEL_BLOCK_WALL = ITEMS.register("brown_flaming_steel_block_wall",
            () -> new BlockItem(ModBlocks.BROWN_FLAMING_STEEL_BLOCK_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_FLAMING_CUT_STEEL = ITEMS.register("brown_flaming_cut_steel",
            () -> new BlockItem(ModBlocks.BROWN_FLAMING_CUT_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_FLAMING_CUT_STEEL_SLAB = ITEMS.register("brown_flaming_cut_steel_slab",
            () -> new BlockItem(ModBlocks.BROWN_FLAMING_CUT_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_FLAMING_CUT_STEEL_VERTICAL_SLAB = ITEMS.register("brown_flaming_cut_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.BROWN_FLAMING_CUT_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_FLAMING_CUT_STEEL_STAIRS = ITEMS.register("brown_flaming_cut_steel_stairs",
            () -> new BlockItem(ModBlocks.BROWN_FLAMING_CUT_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_FLAMING_CUT_STEEL_WALL = ITEMS.register("brown_flaming_cut_steel_wall",
            () -> new BlockItem(ModBlocks.BROWN_FLAMING_CUT_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_FLAMING_POLISHED_STEEL = ITEMS.register("brown_flaming_polished_steel",
            () -> new BlockItem(ModBlocks.BROWN_FLAMING_POLISHED_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_FLAMING_POLISHED_STEEL_SLAB = ITEMS.register("brown_flaming_polished_steel_slab",
            () -> new BlockItem(ModBlocks.BROWN_FLAMING_POLISHED_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = ITEMS.register("brown_flaming_polished_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.BROWN_FLAMING_POLISHED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_FLAMING_POLISHED_STEEL_STAIRS = ITEMS.register("brown_flaming_polished_steel_stairs",
            () -> new BlockItem(ModBlocks.BROWN_FLAMING_POLISHED_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_FLAMING_POLISHED_STEEL_WALL = ITEMS.register("brown_flaming_polished_steel_wall",
            () -> new BlockItem(ModBlocks.BROWN_FLAMING_POLISHED_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_FLAMING_PRESSED_STEEL = ITEMS.register("brown_flaming_pressed_steel",
            () -> new BlockItem(ModBlocks.BROWN_FLAMING_PRESSED_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_FLAMING_PRESSED_STEEL_SLAB = ITEMS.register("brown_flaming_pressed_steel_slab",
            () -> new BlockItem(ModBlocks.BROWN_FLAMING_PRESSED_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = ITEMS.register("brown_flaming_pressed_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.BROWN_FLAMING_PRESSED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_FLAMING_PRESSED_STEEL_STAIRS = ITEMS.register("brown_flaming_pressed_steel_stairs",
            () -> new BlockItem(ModBlocks.BROWN_FLAMING_PRESSED_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_FLAMING_PRESSED_STEEL_WALL = ITEMS.register("brown_flaming_pressed_steel_wall",
            () -> new BlockItem(ModBlocks.BROWN_FLAMING_PRESSED_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_FLAMING_STEEL_CASING = ITEMS.register("brown_flaming_steel_casing",
            () -> new BlockItem(ModBlocks.BROWN_FLAMING_STEEL_CASING.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_FLAMING_STEEL_GRATE = ITEMS.register("brown_flaming_steel_grate",
            () -> new BlockItem(ModBlocks.BROWN_FLAMING_STEEL_GRATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_FLAMING_STEEL_PILLAR = ITEMS.register("brown_flaming_steel_pillar",
            () -> new BlockItem(ModBlocks.BROWN_FLAMING_STEEL_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_FLAMING_BOLTED_STEEL_PILLAR = ITEMS.register("brown_flaming_bolted_steel_pillar",
            () -> new BlockItem(ModBlocks.BROWN_FLAMING_BOLTED_STEEL_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_FLAMING_STEEL_BLOCK = ITEMS.register("green_flaming_steel_block",
            () -> new BlockItem(ModBlocks.GREEN_FLAMING_STEEL_BLOCK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_FLAMING_STEEL_BLOCK_SLAB = ITEMS.register("green_flaming_steel_block_slab",
            () -> new BlockItem(ModBlocks.GREEN_FLAMING_STEEL_BLOCK_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = ITEMS.register("green_flaming_steel_block_vertical_slab",
            () -> new BlockItem(ModBlocks.GREEN_FLAMING_STEEL_BLOCK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_FLAMING_STEEL_BLOCK_STAIRS = ITEMS.register("green_flaming_steel_block_stairs",
            () -> new BlockItem(ModBlocks.GREEN_FLAMING_STEEL_BLOCK_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_FLAMING_STEEL_BLOCK_WALL = ITEMS.register("green_flaming_steel_block_wall",
            () -> new BlockItem(ModBlocks.GREEN_FLAMING_STEEL_BLOCK_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_FLAMING_CUT_STEEL = ITEMS.register("green_flaming_cut_steel",
            () -> new BlockItem(ModBlocks.GREEN_FLAMING_CUT_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_FLAMING_CUT_STEEL_SLAB = ITEMS.register("green_flaming_cut_steel_slab",
            () -> new BlockItem(ModBlocks.GREEN_FLAMING_CUT_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_FLAMING_CUT_STEEL_VERTICAL_SLAB = ITEMS.register("green_flaming_cut_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.GREEN_FLAMING_CUT_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_FLAMING_CUT_STEEL_STAIRS = ITEMS.register("green_flaming_cut_steel_stairs",
            () -> new BlockItem(ModBlocks.GREEN_FLAMING_CUT_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_FLAMING_CUT_STEEL_WALL = ITEMS.register("green_flaming_cut_steel_wall",
            () -> new BlockItem(ModBlocks.GREEN_FLAMING_CUT_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_FLAMING_POLISHED_STEEL = ITEMS.register("green_flaming_polished_steel",
            () -> new BlockItem(ModBlocks.GREEN_FLAMING_POLISHED_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_FLAMING_POLISHED_STEEL_SLAB = ITEMS.register("green_flaming_polished_steel_slab",
            () -> new BlockItem(ModBlocks.GREEN_FLAMING_POLISHED_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = ITEMS.register("green_flaming_polished_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.GREEN_FLAMING_POLISHED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_FLAMING_POLISHED_STEEL_STAIRS = ITEMS.register("green_flaming_polished_steel_stairs",
            () -> new BlockItem(ModBlocks.GREEN_FLAMING_POLISHED_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_FLAMING_POLISHED_STEEL_WALL = ITEMS.register("green_flaming_polished_steel_wall",
            () -> new BlockItem(ModBlocks.GREEN_FLAMING_POLISHED_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_FLAMING_PRESSED_STEEL = ITEMS.register("green_flaming_pressed_steel",
            () -> new BlockItem(ModBlocks.GREEN_FLAMING_PRESSED_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_FLAMING_PRESSED_STEEL_SLAB = ITEMS.register("green_flaming_pressed_steel_slab",
            () -> new BlockItem(ModBlocks.GREEN_FLAMING_PRESSED_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = ITEMS.register("green_flaming_pressed_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.GREEN_FLAMING_PRESSED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_FLAMING_PRESSED_STEEL_STAIRS = ITEMS.register("green_flaming_pressed_steel_stairs",
            () -> new BlockItem(ModBlocks.GREEN_FLAMING_PRESSED_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_FLAMING_PRESSED_STEEL_WALL = ITEMS.register("green_flaming_pressed_steel_wall",
            () -> new BlockItem(ModBlocks.GREEN_FLAMING_PRESSED_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_FLAMING_STEEL_CASING = ITEMS.register("green_flaming_steel_casing",
            () -> new BlockItem(ModBlocks.GREEN_FLAMING_STEEL_CASING.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_FLAMING_STEEL_GRATE = ITEMS.register("green_flaming_steel_grate",
            () -> new BlockItem(ModBlocks.GREEN_FLAMING_STEEL_GRATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_FLAMING_STEEL_PILLAR = ITEMS.register("green_flaming_steel_pillar",
            () -> new BlockItem(ModBlocks.GREEN_FLAMING_STEEL_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_FLAMING_BOLTED_STEEL_PILLAR = ITEMS.register("green_flaming_bolted_steel_pillar",
            () -> new BlockItem(ModBlocks.GREEN_FLAMING_BOLTED_STEEL_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_FLAMING_STEEL_BLOCK = ITEMS.register("red_flaming_steel_block",
            () -> new BlockItem(ModBlocks.RED_FLAMING_STEEL_BLOCK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_FLAMING_STEEL_BLOCK_SLAB = ITEMS.register("red_flaming_steel_block_slab",
            () -> new BlockItem(ModBlocks.RED_FLAMING_STEEL_BLOCK_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = ITEMS.register("red_flaming_steel_block_vertical_slab",
            () -> new BlockItem(ModBlocks.RED_FLAMING_STEEL_BLOCK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_FLAMING_STEEL_BLOCK_STAIRS = ITEMS.register("red_flaming_steel_block_stairs",
            () -> new BlockItem(ModBlocks.RED_FLAMING_STEEL_BLOCK_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_FLAMING_STEEL_BLOCK_WALL = ITEMS.register("red_flaming_steel_block_wall",
            () -> new BlockItem(ModBlocks.RED_FLAMING_STEEL_BLOCK_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_FLAMING_CUT_STEEL = ITEMS.register("red_flaming_cut_steel",
            () -> new BlockItem(ModBlocks.RED_FLAMING_CUT_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_FLAMING_CUT_STEEL_SLAB = ITEMS.register("red_flaming_cut_steel_slab",
            () -> new BlockItem(ModBlocks.RED_FLAMING_CUT_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_FLAMING_CUT_STEEL_VERTICAL_SLAB = ITEMS.register("red_flaming_cut_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.RED_FLAMING_CUT_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_FLAMING_CUT_STEEL_STAIRS = ITEMS.register("red_flaming_cut_steel_stairs",
            () -> new BlockItem(ModBlocks.RED_FLAMING_CUT_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_FLAMING_CUT_STEEL_WALL = ITEMS.register("red_flaming_cut_steel_wall",
            () -> new BlockItem(ModBlocks.RED_FLAMING_CUT_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_FLAMING_POLISHED_STEEL = ITEMS.register("red_flaming_polished_steel",
            () -> new BlockItem(ModBlocks.RED_FLAMING_POLISHED_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_FLAMING_POLISHED_STEEL_SLAB = ITEMS.register("red_flaming_polished_steel_slab",
            () -> new BlockItem(ModBlocks.RED_FLAMING_POLISHED_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = ITEMS.register("red_flaming_polished_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.RED_FLAMING_POLISHED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_FLAMING_POLISHED_STEEL_STAIRS = ITEMS.register("red_flaming_polished_steel_stairs",
            () -> new BlockItem(ModBlocks.RED_FLAMING_POLISHED_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_FLAMING_POLISHED_STEEL_WALL = ITEMS.register("red_flaming_polished_steel_wall",
            () -> new BlockItem(ModBlocks.RED_FLAMING_POLISHED_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_FLAMING_PRESSED_STEEL = ITEMS.register("red_flaming_pressed_steel",
            () -> new BlockItem(ModBlocks.RED_FLAMING_PRESSED_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_FLAMING_PRESSED_STEEL_SLAB = ITEMS.register("red_flaming_pressed_steel_slab",
            () -> new BlockItem(ModBlocks.RED_FLAMING_PRESSED_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = ITEMS.register("red_flaming_pressed_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.RED_FLAMING_PRESSED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_FLAMING_PRESSED_STEEL_STAIRS = ITEMS.register("red_flaming_pressed_steel_stairs",
            () -> new BlockItem(ModBlocks.RED_FLAMING_PRESSED_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_FLAMING_PRESSED_STEEL_WALL = ITEMS.register("red_flaming_pressed_steel_wall",
            () -> new BlockItem(ModBlocks.RED_FLAMING_PRESSED_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_FLAMING_STEEL_CASING = ITEMS.register("red_flaming_steel_casing",
            () -> new BlockItem(ModBlocks.RED_FLAMING_STEEL_CASING.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_FLAMING_STEEL_GRATE = ITEMS.register("red_flaming_steel_grate",
            () -> new BlockItem(ModBlocks.RED_FLAMING_STEEL_GRATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_FLAMING_STEEL_PILLAR = ITEMS.register("red_flaming_steel_pillar",
            () -> new BlockItem(ModBlocks.RED_FLAMING_STEEL_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_FLAMING_BOLTED_STEEL_PILLAR = ITEMS.register("red_flaming_bolted_steel_pillar",
            () -> new BlockItem(ModBlocks.RED_FLAMING_BOLTED_STEEL_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_FLAMING_STEEL_BLOCK = ITEMS.register("black_flaming_steel_block",
            () -> new BlockItem(ModBlocks.BLACK_FLAMING_STEEL_BLOCK.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_FLAMING_STEEL_BLOCK_SLAB = ITEMS.register("black_flaming_steel_block_slab",
            () -> new BlockItem(ModBlocks.BLACK_FLAMING_STEEL_BLOCK_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_FLAMING_STEEL_BLOCK_VERTICAL_SLAB = ITEMS.register("black_flaming_steel_block_vertical_slab",
            () -> new BlockItem(ModBlocks.BLACK_FLAMING_STEEL_BLOCK_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_FLAMING_STEEL_BLOCK_STAIRS = ITEMS.register("black_flaming_steel_block_stairs",
            () -> new BlockItem(ModBlocks.BLACK_FLAMING_STEEL_BLOCK_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_FLAMING_STEEL_BLOCK_WALL = ITEMS.register("black_flaming_steel_block_wall",
            () -> new BlockItem(ModBlocks.BLACK_FLAMING_STEEL_BLOCK_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_FLAMING_CUT_STEEL = ITEMS.register("black_flaming_cut_steel",
            () -> new BlockItem(ModBlocks.BLACK_FLAMING_CUT_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_FLAMING_CUT_STEEL_SLAB = ITEMS.register("black_flaming_cut_steel_slab",
            () -> new BlockItem(ModBlocks.BLACK_FLAMING_CUT_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_FLAMING_CUT_STEEL_VERTICAL_SLAB = ITEMS.register("black_flaming_cut_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.BLACK_FLAMING_CUT_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_FLAMING_CUT_STEEL_STAIRS = ITEMS.register("black_flaming_cut_steel_stairs",
            () -> new BlockItem(ModBlocks.BLACK_FLAMING_CUT_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_FLAMING_CUT_STEEL_WALL = ITEMS.register("black_flaming_cut_steel_wall",
            () -> new BlockItem(ModBlocks.BLACK_FLAMING_CUT_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_FLAMING_POLISHED_STEEL = ITEMS.register("black_flaming_polished_steel",
            () -> new BlockItem(ModBlocks.BLACK_FLAMING_POLISHED_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_FLAMING_POLISHED_STEEL_SLAB = ITEMS.register("black_flaming_polished_steel_slab",
            () -> new BlockItem(ModBlocks.BLACK_FLAMING_POLISHED_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_FLAMING_POLISHED_STEEL_VERTICAL_SLAB = ITEMS.register("black_flaming_polished_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.BLACK_FLAMING_POLISHED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_FLAMING_POLISHED_STEEL_STAIRS = ITEMS.register("black_flaming_polished_steel_stairs",
            () -> new BlockItem(ModBlocks.BLACK_FLAMING_POLISHED_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_FLAMING_POLISHED_STEEL_WALL = ITEMS.register("black_flaming_polished_steel_wall",
            () -> new BlockItem(ModBlocks.BLACK_FLAMING_POLISHED_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_FLAMING_PRESSED_STEEL = ITEMS.register("black_flaming_pressed_steel",
            () -> new BlockItem(ModBlocks.BLACK_FLAMING_PRESSED_STEEL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_FLAMING_PRESSED_STEEL_SLAB = ITEMS.register("black_flaming_pressed_steel_slab",
            () -> new BlockItem(ModBlocks.BLACK_FLAMING_PRESSED_STEEL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_FLAMING_PRESSED_STEEL_VERTICAL_SLAB = ITEMS.register("black_flaming_pressed_steel_vertical_slab",
            () -> new BlockItem(ModBlocks.BLACK_FLAMING_PRESSED_STEEL_VERTICAL_SLAB.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_FLAMING_PRESSED_STEEL_STAIRS = ITEMS.register("black_flaming_pressed_steel_stairs",
            () -> new BlockItem(ModBlocks.BLACK_FLAMING_PRESSED_STEEL_STAIRS.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_FLAMING_PRESSED_STEEL_WALL = ITEMS.register("black_flaming_pressed_steel_wall",
            () -> new BlockItem(ModBlocks.BLACK_FLAMING_PRESSED_STEEL_WALL.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_FLAMING_STEEL_CASING = ITEMS.register("black_flaming_steel_casing",
            () -> new BlockItem(ModBlocks.BLACK_FLAMING_STEEL_CASING.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_FLAMING_STEEL_GRATE = ITEMS.register("black_flaming_steel_grate",
            () -> new BlockItem(ModBlocks.BLACK_FLAMING_STEEL_GRATE.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_FLAMING_STEEL_PILLAR = ITEMS.register("black_flaming_steel_pillar",
            () -> new BlockItem(ModBlocks.BLACK_FLAMING_STEEL_PILLAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_FLAMING_BOLTED_STEEL_PILLAR = ITEMS.register("black_flaming_bolted_steel_pillar",
            () -> new BlockItem(ModBlocks.BLACK_FLAMING_BOLTED_STEEL_PILLAR.get(), createBlockItemProperties()));


    public static final RegistryObject<Item> HOLLOW_OAK_LOG = ITEMS.register("hollow_oak_log",
            () -> new BlockItem(ModBlocks.HOLLOW_OAK_LOG.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_HOLLOW_OAK_LOG = ITEMS.register("stripped_hollow_oak_log",
            () -> new BlockItem(ModBlocks.STRIPPED_HOLLOW_OAK_LOG.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> HOLLOW_SPRUCE_LOG = ITEMS.register("hollow_spruce_log",
            () -> new BlockItem(ModBlocks.HOLLOW_SPRUCE_LOG.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_HOLLOW_SPRUCE_LOG = ITEMS.register("stripped_hollow_spruce_log",
            () -> new BlockItem(ModBlocks.STRIPPED_HOLLOW_SPRUCE_LOG.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> HOLLOW_BIRCH_LOG = ITEMS.register("hollow_birch_log",
            () -> new BlockItem(ModBlocks.HOLLOW_BIRCH_LOG.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_HOLLOW_BIRCH_LOG = ITEMS.register("stripped_hollow_birch_log",
            () -> new BlockItem(ModBlocks.STRIPPED_HOLLOW_BIRCH_LOG.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> HOLLOW_JUNGLE_LOG = ITEMS.register("hollow_jungle_log",
            () -> new BlockItem(ModBlocks.HOLLOW_JUNGLE_LOG.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_HOLLOW_JUNGLE_LOG = ITEMS.register("stripped_hollow_jungle_log",
            () -> new BlockItem(ModBlocks.STRIPPED_HOLLOW_JUNGLE_LOG.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> HOLLOW_ACACIA_LOG = ITEMS.register("hollow_acacia_log",
            () -> new BlockItem(ModBlocks.HOLLOW_ACACIA_LOG.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_HOLLOW_ACACIA_LOG = ITEMS.register("stripped_hollow_acacia_log",
            () -> new BlockItem(ModBlocks.STRIPPED_HOLLOW_ACACIA_LOG.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> HOLLOW_DARK_OAK_LOG = ITEMS.register("hollow_dark_oak_log",
            () -> new BlockItem(ModBlocks.HOLLOW_DARK_OAK_LOG.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_HOLLOW_DARK_OAK_LOG = ITEMS.register("stripped_hollow_dark_oak_log",
            () -> new BlockItem(ModBlocks.STRIPPED_HOLLOW_DARK_OAK_LOG.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> HOLLOW_CRIMSON_STEM = ITEMS.register("hollow_crimson_stem",
            () -> new BlockItem(ModBlocks.HOLLOW_CRIMSON_STEM.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_HOLLOW_CRIMSON_STEM = ITEMS.register("stripped_hollow_crimson_stem",
            () -> new BlockItem(ModBlocks.STRIPPED_HOLLOW_CRIMSON_STEM.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> HOLLOW_WARPED_STEM = ITEMS.register("hollow_warped_stem",
            () -> new BlockItem(ModBlocks.HOLLOW_WARPED_STEM.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_HOLLOW_WARPED_STEM = ITEMS.register("stripped_hollow_warped_stem",
            () -> new BlockItem(ModBlocks.STRIPPED_HOLLOW_WARPED_STEM.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> HOLLOW_MANGROVE_LOG = ITEMS.register("hollow_mangrove_log",
            () -> new BlockItem(ModBlocks.HOLLOW_MANGROVE_LOG.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_HOLLOW_MANGROVE_LOG = ITEMS.register("stripped_hollow_mangrove_log",
            () -> new BlockItem(ModBlocks.STRIPPED_HOLLOW_MANGROVE_LOG.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> HOLLOW_ASHPEN_LOG = ITEMS.register("hollow_ashpen_log",
            () -> new BlockItem(ModBlocks.HOLLOW_ASHPEN_LOG.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_HOLLOW_ASHPEN_LOG = ITEMS.register("stripped_hollow_ashpen_log",
            () -> new BlockItem(ModBlocks.STRIPPED_HOLLOW_ASHPEN_LOG.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> HOLLOW_POPLAR_LOG = ITEMS.register("hollow_poplar_log",
            () -> new BlockItem(ModBlocks.HOLLOW_POPLAR_LOG.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_HOLLOW_POPLAR_LOG = ITEMS.register("stripped_hollow_poplar_log",
            () -> new BlockItem(ModBlocks.STRIPPED_HOLLOW_POPLAR_LOG.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> HOLLOW_PALE_OAK_LOG = ITEMS.register("hollow_pale_oak_log",
            () -> new BlockItem(ModBlocks.HOLLOW_PALE_OAK_LOG.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_HOLLOW_PALE_OAK_LOG = ITEMS.register("stripped_hollow_pale_oak_log",
            () -> new BlockItem(ModBlocks.STRIPPED_HOLLOW_PALE_OAK_LOG.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> HOLLOW_CHERRY_LOG = ITEMS.register("hollow_cherry_log",
            () -> new BlockItem(ModBlocks.HOLLOW_CHERRY_LOG.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STRIPPED_HOLLOW_CHERRY_LOG = ITEMS.register("stripped_hollow_cherry_log",
            () -> new BlockItem(ModBlocks.STRIPPED_HOLLOW_CHERRY_LOG.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> CARDBOARD_BLOCK = ITEMS.register(
            "cardboard_block",
            () -> new BlockItem(ModBlocks.CARDBOARD_BLOCK.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> CARDBOARD_SLAB = ITEMS.register(
            "cardboard_slab",
            () -> new BlockItem(ModBlocks.CARDBOARD_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> CARDBOARD_STAIRS = ITEMS.register(
            "cardboard_stairs",
            () -> new BlockItem(ModBlocks.CARDBOARD_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> CARDBOARD_WALL = ITEMS.register(
            "cardboard_wall",
            () -> new BlockItem(ModBlocks.CARDBOARD_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> CARDBOARD_VERTICAL_SLAB = ITEMS.register(
            "cardboard_vertical_slab",
            () -> new BlockItem(ModBlocks.CARDBOARD_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> CARDBOARD_TRAPDOOR = ITEMS.register(
            "cardboard_trapdoor",
            () -> new BlockItem(ModBlocks.CARDBOARD_TRAPDOOR.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> CARDBOARD_BUTTON = ITEMS.register(
            "cardboard_button",
            () -> new BlockItem(ModBlocks.CARDBOARD_BUTTON.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> CARDBOARD_PRESSURE_PLATE = ITEMS.register(
            "cardboard_pressure_plate",
            () -> new BlockItem(ModBlocks.CARDBOARD_PRESSURE_PLATE.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> SMOOTH_CARDBOARD_BLOCK = ITEMS.register(
            "smooth_cardboard_block",
            () -> new BlockItem(ModBlocks.SMOOTH_CARDBOARD_BLOCK.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> SMOOTH_CARDBOARD_SLAB = ITEMS.register(
            "smooth_cardboard_slab",
            () -> new BlockItem(ModBlocks.SMOOTH_CARDBOARD_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> SMOOTH_CARDBOARD_STAIRS = ITEMS.register(
            "smooth_cardboard_stairs",
            () -> new BlockItem(ModBlocks.SMOOTH_CARDBOARD_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> SMOOTH_CARDBOARD_WALL = ITEMS.register(
            "smooth_cardboard_wall",
            () -> new BlockItem(ModBlocks.SMOOTH_CARDBOARD_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> SMOOTH_CARDBOARD_VERTICAL_SLAB = ITEMS.register(
            "smooth_cardboard_vertical_slab",
            () -> new BlockItem(ModBlocks.SMOOTH_CARDBOARD_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BUNDLED_CARDBOARD = ITEMS.register(
            "bundled_cardboard",
            () -> new BlockItem(ModBlocks.BUNDLED_CARDBOARD.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BUNDLED_CARDBOARD_SLAB = ITEMS.register(
            "bundled_cardboard_slab",
            () -> new BlockItem(ModBlocks.BUNDLED_CARDBOARD_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BUNDLED_CARDBOARD_STAIRS = ITEMS.register(
            "bundled_cardboard_stairs",
            () -> new BlockItem(ModBlocks.BUNDLED_CARDBOARD_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BUNDLED_CARDBOARD_WALL = ITEMS.register(
            "bundled_cardboard_wall",
            () -> new BlockItem(ModBlocks.BUNDLED_CARDBOARD_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BUNDLED_CARDBOARD_VERTICAL_SLAB = ITEMS.register(
            "bundled_cardboard_vertical_slab",
            () -> new BlockItem(ModBlocks.BUNDLED_CARDBOARD_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PRESSED_CARDBOARD = ITEMS.register(
            "pressed_cardboard",
            () -> new BlockItem(ModBlocks.PRESSED_CARDBOARD.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PRESSED_CARDBOARD_SLAB = ITEMS.register(
            "pressed_cardboard_slab",
            () -> new BlockItem(ModBlocks.PRESSED_CARDBOARD_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PRESSED_CARDBOARD_STAIRS = ITEMS.register(
            "pressed_cardboard_stairs",
            () -> new BlockItem(ModBlocks.PRESSED_CARDBOARD_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PRESSED_CARDBOARD_WALL = ITEMS.register(
            "pressed_cardboard_wall",
            () -> new BlockItem(ModBlocks.PRESSED_CARDBOARD_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PRESSED_CARDBOARD_VERTICAL_SLAB = ITEMS.register(
            "pressed_cardboard_vertical_slab",
            () -> new BlockItem(ModBlocks.PRESSED_CARDBOARD_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> THICK_CARDBOARD_BLOCK = ITEMS.register(
            "thick_cardboard_block",
            () -> new BlockItem(ModBlocks.THICK_CARDBOARD_BLOCK.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> THICK_CARDBOARD_SLAB = ITEMS.register(
            "thick_cardboard_slab",
            () -> new BlockItem(ModBlocks.THICK_CARDBOARD_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> THICK_CARDBOARD_STAIRS = ITEMS.register(
            "thick_cardboard_stairs",
            () -> new BlockItem(ModBlocks.THICK_CARDBOARD_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> THICK_CARDBOARD_WALL = ITEMS.register(
            "thick_cardboard_wall",
            () -> new BlockItem(ModBlocks.THICK_CARDBOARD_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> THICK_CARDBOARD_VERTICAL_SLAB = ITEMS.register(
            "thick_cardboard_vertical_slab",
            () -> new BlockItem(ModBlocks.THICK_CARDBOARD_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> STRIPPED_CARDBOARD_BLOCK = ITEMS.register(
            "stripped_cardboard_block",
            () -> new BlockItem(ModBlocks.STRIPPED_CARDBOARD_BLOCK.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> STRIPPED_CARDBOARD_SLAB = ITEMS.register(
            "stripped_cardboard_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_CARDBOARD_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> STRIPPED_CARDBOARD_STAIRS = ITEMS.register(
            "stripped_cardboard_stairs",
            () -> new BlockItem(ModBlocks.STRIPPED_CARDBOARD_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> STRIPPED_CARDBOARD_WALL = ITEMS.register(
            "stripped_cardboard_wall",
            () -> new BlockItem(ModBlocks.STRIPPED_CARDBOARD_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> STRIPPED_CARDBOARD_VERTICAL_SLAB = ITEMS.register(
            "stripped_cardboard_vertical_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_CARDBOARD_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> STRIPPED_CARDBOARD_TRAPDOOR = ITEMS.register(
            "stripped_cardboard_trapdoor",
            () -> new BlockItem(ModBlocks.STRIPPED_CARDBOARD_TRAPDOOR.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> STRIPPED_CARDBOARD_BUTTON = ITEMS.register(
            "stripped_cardboard_button",
            () -> new BlockItem(ModBlocks.STRIPPED_CARDBOARD_BUTTON.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> STRIPPED_CARDBOARD_PRESSURE_PLATE = ITEMS.register(
            "stripped_cardboard_pressure_plate",
            () -> new BlockItem(ModBlocks.STRIPPED_CARDBOARD_PRESSURE_PLATE.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> STRIPPED_SMOOTH_CARDBOARD_BLOCK = ITEMS.register(
            "stripped_smooth_cardboard_block",
            () -> new BlockItem(ModBlocks.STRIPPED_SMOOTH_CARDBOARD_BLOCK.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> STRIPPED_SMOOTH_CARDBOARD_SLAB = ITEMS.register(
            "stripped_smooth_cardboard_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_SMOOTH_CARDBOARD_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> STRIPPED_SMOOTH_CARDBOARD_STAIRS = ITEMS.register(
            "stripped_smooth_cardboard_stairs",
            () -> new BlockItem(ModBlocks.STRIPPED_SMOOTH_CARDBOARD_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> STRIPPED_SMOOTH_CARDBOARD_WALL = ITEMS.register(
            "stripped_smooth_cardboard_wall",
            () -> new BlockItem(ModBlocks.STRIPPED_SMOOTH_CARDBOARD_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> STRIPPED_SMOOTH_CARDBOARD_VERTICAL_SLAB = ITEMS.register(
            "stripped_smooth_cardboard_vertical_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_SMOOTH_CARDBOARD_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> STRIPPED_BUNDLED_CARDBOARD = ITEMS.register(
            "stripped_bundled_cardboard",
            () -> new BlockItem(ModBlocks.STRIPPED_BUNDLED_CARDBOARD.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> STRIPPED_BUNDLED_CARDBOARD_SLAB = ITEMS.register(
            "stripped_bundled_cardboard_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_BUNDLED_CARDBOARD_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> STRIPPED_BUNDLED_CARDBOARD_STAIRS = ITEMS.register(
            "stripped_bundled_cardboard_stairs",
            () -> new BlockItem(ModBlocks.STRIPPED_BUNDLED_CARDBOARD_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> STRIPPED_BUNDLED_CARDBOARD_WALL = ITEMS.register(
            "stripped_bundled_cardboard_wall",
            () -> new BlockItem(ModBlocks.STRIPPED_BUNDLED_CARDBOARD_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> STRIPPED_BUNDLED_CARDBOARD_VERTICAL_SLAB = ITEMS.register(
            "stripped_bundled_cardboard_vertical_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_BUNDLED_CARDBOARD_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> STRIPPED_PRESSED_CARDBOARD = ITEMS.register(
            "stripped_pressed_cardboard",
            () -> new BlockItem(ModBlocks.STRIPPED_PRESSED_CARDBOARD.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> STRIPPED_PRESSED_CARDBOARD_SLAB = ITEMS.register(
            "stripped_pressed_cardboard_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_PRESSED_CARDBOARD_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> STRIPPED_PRESSED_CARDBOARD_STAIRS = ITEMS.register(
            "stripped_pressed_cardboard_stairs",
            () -> new BlockItem(ModBlocks.STRIPPED_PRESSED_CARDBOARD_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> STRIPPED_PRESSED_CARDBOARD_WALL = ITEMS.register(
            "stripped_pressed_cardboard_wall",
            () -> new BlockItem(ModBlocks.STRIPPED_PRESSED_CARDBOARD_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> STRIPPED_PRESSED_CARDBOARD_VERTICAL_SLAB = ITEMS.register(
            "stripped_pressed_cardboard_vertical_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_PRESSED_CARDBOARD_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> STRIPPED_THICK_CARDBOARD_BLOCK = ITEMS.register(
            "stripped_thick_cardboard_block",
            () -> new BlockItem(ModBlocks.STRIPPED_THICK_CARDBOARD_BLOCK.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> STRIPPED_THICK_CARDBOARD_SLAB = ITEMS.register(
            "stripped_thick_cardboard_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_THICK_CARDBOARD_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> STRIPPED_THICK_CARDBOARD_STAIRS = ITEMS.register(
            "stripped_thick_cardboard_stairs",
            () -> new BlockItem(ModBlocks.STRIPPED_THICK_CARDBOARD_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> STRIPPED_THICK_CARDBOARD_WALL = ITEMS.register(
            "stripped_thick_cardboard_wall",
            () -> new BlockItem(ModBlocks.STRIPPED_THICK_CARDBOARD_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> STRIPPED_THICK_CARDBOARD_VERTICAL_SLAB = ITEMS.register(
            "stripped_thick_cardboard_vertical_slab",
            () -> new BlockItem(ModBlocks.STRIPPED_THICK_CARDBOARD_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> TINTED_CARDBOARD_BLOCK = ITEMS.register(
            "tinted_cardboard_block",
            () -> new BlockItem(ModBlocks.TINTED_CARDBOARD_BLOCK.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> TINTED_CARDBOARD_SLAB = ITEMS.register(
            "tinted_cardboard_slab",
            () -> new BlockItem(ModBlocks.TINTED_CARDBOARD_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> TINTED_CARDBOARD_STAIRS = ITEMS.register(
            "tinted_cardboard_stairs",
            () -> new BlockItem(ModBlocks.TINTED_CARDBOARD_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> TINTED_CARDBOARD_WALL = ITEMS.register(
            "tinted_cardboard_wall",
            () -> new BlockItem(ModBlocks.TINTED_CARDBOARD_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> TINTED_CARDBOARD_VERTICAL_SLAB = ITEMS.register(
            "tinted_cardboard_vertical_slab",
            () -> new BlockItem(ModBlocks.TINTED_CARDBOARD_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> TINTED_CARDBOARD_TRAPDOOR = ITEMS.register(
            "tinted_cardboard_trapdoor",
            () -> new BlockItem(ModBlocks.TINTED_CARDBOARD_TRAPDOOR.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> TINTED_CARDBOARD_BUTTON = ITEMS.register(
            "tinted_cardboard_button",
            () -> new BlockItem(ModBlocks.TINTED_CARDBOARD_BUTTON.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> TINTED_CARDBOARD_PRESSURE_PLATE = ITEMS.register(
            "tinted_cardboard_pressure_plate",
            () -> new BlockItem(ModBlocks.TINTED_CARDBOARD_PRESSURE_PLATE.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> TINTED_SMOOTH_CARDBOARD_BLOCK = ITEMS.register(
            "tinted_smooth_cardboard_block",
            () -> new BlockItem(ModBlocks.TINTED_SMOOTH_CARDBOARD_BLOCK.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> TINTED_SMOOTH_CARDBOARD_SLAB = ITEMS.register(
            "tinted_smooth_cardboard_slab",
            () -> new BlockItem(ModBlocks.TINTED_SMOOTH_CARDBOARD_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> TINTED_SMOOTH_CARDBOARD_STAIRS = ITEMS.register(
            "tinted_smooth_cardboard_stairs",
            () -> new BlockItem(ModBlocks.TINTED_SMOOTH_CARDBOARD_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> TINTED_SMOOTH_CARDBOARD_WALL = ITEMS.register(
            "tinted_smooth_cardboard_wall",
            () -> new BlockItem(ModBlocks.TINTED_SMOOTH_CARDBOARD_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> TINTED_SMOOTH_CARDBOARD_VERTICAL_SLAB = ITEMS.register(
            "tinted_smooth_cardboard_vertical_slab",
            () -> new BlockItem(ModBlocks.TINTED_SMOOTH_CARDBOARD_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> TINTED_BUNDLED_CARDBOARD = ITEMS.register(
            "tinted_bundled_cardboard",
            () -> new BlockItem(ModBlocks.TINTED_BUNDLED_CARDBOARD.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> TINTED_BUNDLED_CARDBOARD_SLAB = ITEMS.register(
            "tinted_bundled_cardboard_slab",
            () -> new BlockItem(ModBlocks.TINTED_BUNDLED_CARDBOARD_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> TINTED_BUNDLED_CARDBOARD_STAIRS = ITEMS.register(
            "tinted_bundled_cardboard_stairs",
            () -> new BlockItem(ModBlocks.TINTED_BUNDLED_CARDBOARD_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> TINTED_BUNDLED_CARDBOARD_WALL = ITEMS.register(
            "tinted_bundled_cardboard_wall",
            () -> new BlockItem(ModBlocks.TINTED_BUNDLED_CARDBOARD_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> TINTED_BUNDLED_CARDBOARD_VERTICAL_SLAB = ITEMS.register(
            "tinted_bundled_cardboard_vertical_slab",
            () -> new BlockItem(ModBlocks.TINTED_BUNDLED_CARDBOARD_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> TINTED_PRESSED_CARDBOARD = ITEMS.register(
            "tinted_pressed_cardboard",
            () -> new BlockItem(ModBlocks.TINTED_PRESSED_CARDBOARD.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> TINTED_PRESSED_CARDBOARD_SLAB = ITEMS.register(
            "tinted_pressed_cardboard_slab",
            () -> new BlockItem(ModBlocks.TINTED_PRESSED_CARDBOARD_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> TINTED_PRESSED_CARDBOARD_STAIRS = ITEMS.register(
            "tinted_pressed_cardboard_stairs",
            () -> new BlockItem(ModBlocks.TINTED_PRESSED_CARDBOARD_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> TINTED_PRESSED_CARDBOARD_WALL = ITEMS.register(
            "tinted_pressed_cardboard_wall",
            () -> new BlockItem(ModBlocks.TINTED_PRESSED_CARDBOARD_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> TINTED_PRESSED_CARDBOARD_VERTICAL_SLAB = ITEMS.register(
            "tinted_pressed_cardboard_vertical_slab",
            () -> new BlockItem(ModBlocks.TINTED_PRESSED_CARDBOARD_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> TINTED_THICK_CARDBOARD_BLOCK = ITEMS.register(
            "tinted_thick_cardboard_block",
            () -> new BlockItem(ModBlocks.TINTED_THICK_CARDBOARD_BLOCK.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> TINTED_THICK_CARDBOARD_SLAB = ITEMS.register(
            "tinted_thick_cardboard_slab",
            () -> new BlockItem(ModBlocks.TINTED_THICK_CARDBOARD_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> TINTED_THICK_CARDBOARD_STAIRS = ITEMS.register(
            "tinted_thick_cardboard_stairs",
            () -> new BlockItem(ModBlocks.TINTED_THICK_CARDBOARD_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> TINTED_THICK_CARDBOARD_WALL = ITEMS.register(
            "tinted_thick_cardboard_wall",
            () -> new BlockItem(ModBlocks.TINTED_THICK_CARDBOARD_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> TINTED_THICK_CARDBOARD_VERTICAL_SLAB = ITEMS.register(
            "tinted_thick_cardboard_vertical_slab",
            () -> new BlockItem(ModBlocks.TINTED_THICK_CARDBOARD_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WASHED_CARDBOARD_BLOCK = ITEMS.register(
            "washed_cardboard_block",
            () -> new BlockItem(ModBlocks.WASHED_CARDBOARD_BLOCK.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WASHED_CARDBOARD_SLAB = ITEMS.register(
            "washed_cardboard_slab",
            () -> new BlockItem(ModBlocks.WASHED_CARDBOARD_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WASHED_CARDBOARD_STAIRS = ITEMS.register(
            "washed_cardboard_stairs",
            () -> new BlockItem(ModBlocks.WASHED_CARDBOARD_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WASHED_CARDBOARD_WALL = ITEMS.register(
            "washed_cardboard_wall",
            () -> new BlockItem(ModBlocks.WASHED_CARDBOARD_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WASHED_CARDBOARD_VERTICAL_SLAB = ITEMS.register(
            "washed_cardboard_vertical_slab",
            () -> new BlockItem(ModBlocks.WASHED_CARDBOARD_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WASHED_CARDBOARD_TRAPDOOR = ITEMS.register(
            "washed_cardboard_trapdoor",
            () -> new BlockItem(ModBlocks.WASHED_CARDBOARD_TRAPDOOR.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WASHED_CARDBOARD_BUTTON = ITEMS.register(
            "washed_cardboard_button",
            () -> new BlockItem(ModBlocks.WASHED_CARDBOARD_BUTTON.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WASHED_CARDBOARD_PRESSURE_PLATE = ITEMS.register(
            "washed_cardboard_pressure_plate",
            () -> new BlockItem(ModBlocks.WASHED_CARDBOARD_PRESSURE_PLATE.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WASHED_SMOOTH_CARDBOARD_BLOCK = ITEMS.register(
            "washed_smooth_cardboard_block",
            () -> new BlockItem(ModBlocks.WASHED_SMOOTH_CARDBOARD_BLOCK.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WASHED_SMOOTH_CARDBOARD_SLAB = ITEMS.register(
            "washed_smooth_cardboard_slab",
            () -> new BlockItem(ModBlocks.WASHED_SMOOTH_CARDBOARD_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WASHED_SMOOTH_CARDBOARD_STAIRS = ITEMS.register(
            "washed_smooth_cardboard_stairs",
            () -> new BlockItem(ModBlocks.WASHED_SMOOTH_CARDBOARD_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WASHED_SMOOTH_CARDBOARD_WALL = ITEMS.register(
            "washed_smooth_cardboard_wall",
            () -> new BlockItem(ModBlocks.WASHED_SMOOTH_CARDBOARD_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WASHED_SMOOTH_CARDBOARD_VERTICAL_SLAB = ITEMS.register(
            "washed_smooth_cardboard_vertical_slab",
            () -> new BlockItem(ModBlocks.WASHED_SMOOTH_CARDBOARD_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WASHED_BUNDLED_CARDBOARD = ITEMS.register(
            "washed_bundled_cardboard",
            () -> new BlockItem(ModBlocks.WASHED_BUNDLED_CARDBOARD.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WASHED_BUNDLED_CARDBOARD_SLAB = ITEMS.register(
            "washed_bundled_cardboard_slab",
            () -> new BlockItem(ModBlocks.WASHED_BUNDLED_CARDBOARD_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WASHED_BUNDLED_CARDBOARD_STAIRS = ITEMS.register(
            "washed_bundled_cardboard_stairs",
            () -> new BlockItem(ModBlocks.WASHED_BUNDLED_CARDBOARD_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WASHED_BUNDLED_CARDBOARD_WALL = ITEMS.register(
            "washed_bundled_cardboard_wall",
            () -> new BlockItem(ModBlocks.WASHED_BUNDLED_CARDBOARD_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WASHED_BUNDLED_CARDBOARD_VERTICAL_SLAB = ITEMS.register(
            "washed_bundled_cardboard_vertical_slab",
            () -> new BlockItem(ModBlocks.WASHED_BUNDLED_CARDBOARD_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WASHED_PRESSED_CARDBOARD = ITEMS.register(
            "washed_pressed_cardboard",
            () -> new BlockItem(ModBlocks.WASHED_PRESSED_CARDBOARD.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WASHED_PRESSED_CARDBOARD_SLAB = ITEMS.register(
            "washed_pressed_cardboard_slab",
            () -> new BlockItem(ModBlocks.WASHED_PRESSED_CARDBOARD_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WASHED_PRESSED_CARDBOARD_STAIRS = ITEMS.register(
            "washed_pressed_cardboard_stairs",
            () -> new BlockItem(ModBlocks.WASHED_PRESSED_CARDBOARD_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WASHED_PRESSED_CARDBOARD_WALL = ITEMS.register(
            "washed_pressed_cardboard_wall",
            () -> new BlockItem(ModBlocks.WASHED_PRESSED_CARDBOARD_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WASHED_PRESSED_CARDBOARD_VERTICAL_SLAB = ITEMS.register(
            "washed_pressed_cardboard_vertical_slab",
            () -> new BlockItem(ModBlocks.WASHED_PRESSED_CARDBOARD_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WASHED_THICK_CARDBOARD_BLOCK = ITEMS.register(
            "washed_thick_cardboard_block",
            () -> new BlockItem(ModBlocks.WASHED_THICK_CARDBOARD_BLOCK.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WASHED_THICK_CARDBOARD_SLAB = ITEMS.register(
            "washed_thick_cardboard_slab",
            () -> new BlockItem(ModBlocks.WASHED_THICK_CARDBOARD_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WASHED_THICK_CARDBOARD_STAIRS = ITEMS.register(
            "washed_thick_cardboard_stairs",
            () -> new BlockItem(ModBlocks.WASHED_THICK_CARDBOARD_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WASHED_THICK_CARDBOARD_WALL = ITEMS.register(
            "washed_thick_cardboard_wall",
            () -> new BlockItem(ModBlocks.WASHED_THICK_CARDBOARD_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WASHED_THICK_CARDBOARD_VERTICAL_SLAB = ITEMS.register(
            "washed_thick_cardboard_vertical_slab",
            () -> new BlockItem(ModBlocks.WASHED_THICK_CARDBOARD_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BURNT_CARDBOARD_BLOCK = ITEMS.register(
            "burnt_cardboard_block",
            () -> new BlockItem(ModBlocks.BURNT_CARDBOARD_BLOCK.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BURNT_CARDBOARD_SLAB = ITEMS.register(
            "burnt_cardboard_slab",
            () -> new BlockItem(ModBlocks.BURNT_CARDBOARD_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BURNT_CARDBOARD_STAIRS = ITEMS.register(
            "burnt_cardboard_stairs",
            () -> new BlockItem(ModBlocks.BURNT_CARDBOARD_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BURNT_CARDBOARD_WALL = ITEMS.register(
            "burnt_cardboard_wall",
            () -> new BlockItem(ModBlocks.BURNT_CARDBOARD_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BURNT_CARDBOARD_VERTICAL_SLAB = ITEMS.register(
            "burnt_cardboard_vertical_slab",
            () -> new BlockItem(ModBlocks.BURNT_CARDBOARD_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BURNT_CARDBOARD_TRAPDOOR = ITEMS.register(
            "burnt_cardboard_trapdoor",
            () -> new BlockItem(ModBlocks.BURNT_CARDBOARD_TRAPDOOR.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BURNT_CARDBOARD_BUTTON = ITEMS.register(
            "burnt_cardboard_button",
            () -> new BlockItem(ModBlocks.BURNT_CARDBOARD_BUTTON.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BURNT_CARDBOARD_PRESSURE_PLATE = ITEMS.register(
            "burnt_cardboard_pressure_plate",
            () -> new BlockItem(ModBlocks.BURNT_CARDBOARD_PRESSURE_PLATE.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BURNT_SMOOTH_CARDBOARD_BLOCK = ITEMS.register(
            "burnt_smooth_cardboard_block",
            () -> new BlockItem(ModBlocks.BURNT_SMOOTH_CARDBOARD_BLOCK.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BURNT_SMOOTH_CARDBOARD_SLAB = ITEMS.register(
            "burnt_smooth_cardboard_slab",
            () -> new BlockItem(ModBlocks.BURNT_SMOOTH_CARDBOARD_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BURNT_SMOOTH_CARDBOARD_STAIRS = ITEMS.register(
            "burnt_smooth_cardboard_stairs",
            () -> new BlockItem(ModBlocks.BURNT_SMOOTH_CARDBOARD_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BURNT_SMOOTH_CARDBOARD_WALL = ITEMS.register(
            "burnt_smooth_cardboard_wall",
            () -> new BlockItem(ModBlocks.BURNT_SMOOTH_CARDBOARD_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BURNT_SMOOTH_CARDBOARD_VERTICAL_SLAB = ITEMS.register(
            "burnt_smooth_cardboard_vertical_slab",
            () -> new BlockItem(ModBlocks.BURNT_SMOOTH_CARDBOARD_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BURNT_BUNDLED_CARDBOARD = ITEMS.register(
            "burnt_bundled_cardboard",
            () -> new BlockItem(ModBlocks.BURNT_BUNDLED_CARDBOARD.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BURNT_BUNDLED_CARDBOARD_SLAB = ITEMS.register(
            "burnt_bundled_cardboard_slab",
            () -> new BlockItem(ModBlocks.BURNT_BUNDLED_CARDBOARD_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BURNT_BUNDLED_CARDBOARD_STAIRS = ITEMS.register(
            "burnt_bundled_cardboard_stairs",
            () -> new BlockItem(ModBlocks.BURNT_BUNDLED_CARDBOARD_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BURNT_BUNDLED_CARDBOARD_WALL = ITEMS.register(
            "burnt_bundled_cardboard_wall",
            () -> new BlockItem(ModBlocks.BURNT_BUNDLED_CARDBOARD_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BURNT_BUNDLED_CARDBOARD_VERTICAL_SLAB = ITEMS.register(
            "burnt_bundled_cardboard_vertical_slab",
            () -> new BlockItem(ModBlocks.BURNT_BUNDLED_CARDBOARD_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BURNT_PRESSED_CARDBOARD = ITEMS.register(
            "burnt_pressed_cardboard",
            () -> new BlockItem(ModBlocks.BURNT_PRESSED_CARDBOARD.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BURNT_PRESSED_CARDBOARD_SLAB = ITEMS.register(
            "burnt_pressed_cardboard_slab",
            () -> new BlockItem(ModBlocks.BURNT_PRESSED_CARDBOARD_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BURNT_PRESSED_CARDBOARD_STAIRS = ITEMS.register(
            "burnt_pressed_cardboard_stairs",
            () -> new BlockItem(ModBlocks.BURNT_PRESSED_CARDBOARD_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BURNT_PRESSED_CARDBOARD_WALL = ITEMS.register(
            "burnt_pressed_cardboard_wall",
            () -> new BlockItem(ModBlocks.BURNT_PRESSED_CARDBOARD_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BURNT_PRESSED_CARDBOARD_VERTICAL_SLAB = ITEMS.register(
            "burnt_pressed_cardboard_vertical_slab",
            () -> new BlockItem(ModBlocks.BURNT_PRESSED_CARDBOARD_VERTICAL_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BURNT_THICK_CARDBOARD_BLOCK = ITEMS.register(
            "burnt_thick_cardboard_block",
            () -> new BlockItem(ModBlocks.BURNT_THICK_CARDBOARD_BLOCK.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BURNT_THICK_CARDBOARD_SLAB = ITEMS.register(
            "burnt_thick_cardboard_slab",
            () -> new BlockItem(ModBlocks.BURNT_THICK_CARDBOARD_SLAB.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BURNT_THICK_CARDBOARD_STAIRS = ITEMS.register(
            "burnt_thick_cardboard_stairs",
            () -> new BlockItem(ModBlocks.BURNT_THICK_CARDBOARD_STAIRS.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BURNT_THICK_CARDBOARD_WALL = ITEMS.register(
            "burnt_thick_cardboard_wall",
            () -> new BlockItem(ModBlocks.BURNT_THICK_CARDBOARD_WALL.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BURNT_THICK_CARDBOARD_VERTICAL_SLAB = ITEMS.register(
            "burnt_thick_cardboard_vertical_slab",
            () -> new BlockItem(ModBlocks.BURNT_THICK_CARDBOARD_VERTICAL_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> OAK_SHELF = ITEMS.register(
            "oak_shelf",
            () -> new BlockItem(ModBlocks.OAK_SHELF.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> SPRUCE_SHELF = ITEMS.register(
            "spruce_shelf",
            () -> new BlockItem(ModBlocks.SPRUCE_SHELF.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BIRCH_SHELF = ITEMS.register(
            "birch_shelf",
            () -> new BlockItem(ModBlocks.BIRCH_SHELF.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> JUNGLE_SHELF = ITEMS.register(
            "jungle_shelf",
            () -> new BlockItem(ModBlocks.JUNGLE_SHELF.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> ACACIA_SHELF = ITEMS.register(
            "acacia_shelf",
            () -> new BlockItem(ModBlocks.ACACIA_SHELF.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> DARK_OAK_SHELF = ITEMS.register(
            "dark_oak_shelf",
            () -> new BlockItem(ModBlocks.DARK_OAK_SHELF.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> MANGROVE_SHELF = ITEMS.register(
            "mangrove_shelf",
            () -> new BlockItem(ModBlocks.MANGROVE_SHELF.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> CHERRY_SHELF = ITEMS.register(
            "cherry_shelf",
            () -> new BlockItem(ModBlocks.CHERRY_SHELF.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> CRIMSON_SHELF = ITEMS.register(
            "crimson_shelf",
            () -> new BlockItem(ModBlocks.CRIMSON_SHELF.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> WARPED_SHELF = ITEMS.register(
            "warped_shelf",
            () -> new BlockItem(ModBlocks.WARPED_SHELF.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> BAMBOO_SHELF = ITEMS.register(
            "bamboo_shelf",
            () -> new BlockItem(ModBlocks.BAMBOO_SHELF.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> STRIPPED_BAMBOO_SHELF = ITEMS.register(
            "stripped_bamboo_shelf",
            () -> new BlockItem(ModBlocks.STRIPPED_BAMBOO_SHELF.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> PALE_OAK_SHELF = ITEMS.register(
            "pale_oak_shelf",
            () -> new BlockItem(ModBlocks.PALE_OAK_SHELF.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> POPLAR_SHELF = ITEMS.register(
            "poplar_shelf",
            () -> new BlockItem(ModBlocks.POPLAR_SHELF.get(), createBlockItemProperties())
    );
    public static final RegistryObject<Item> GOLDEN_JAR = ITEMS.register(
            "golden_jar",
            () -> new GoldenJarItem(ModBlocks.GOLDEN_JAR.get(), new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).rarity(net.minecraft.world.item.Rarity.UNCOMMON)));

    public static final RegistryObject<Item> FESTIVE_STAR = ITEMS.register(
            "festive_star",
            () -> new FestiveStarItem(ModBlocks.FESTIVE_STAR.get(), new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).rarity(net.minecraft.world.item.Rarity.RARE)));


    public static final RegistryObject<Item> EXPERIENCE_BUCKET = ITEMS.register(
            "experience_bucket",
            () -> new ExperienceBucketItem(
                    () -> com.kingodogo.buildscape.fluid.ModFluids.EXPERIENCE_STILL.get(),
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(1).craftRemainder(net.minecraft.world.item.Items.BUCKET)
            )
    );

    public static final RegistryObject<Item> FESTIVE_GLINT_SHARD = ITEMS.register(
            "festive_glint_shard",
            () -> new FestiveGlintShardItem(
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).rarity(net.minecraft.world.item.Rarity.RARE)
            )
    );

    public static final RegistryObject<Item> GOLDEN_JAR_PATTERN = ITEMS.register(
            "golden_jar_pattern",
            () -> new PatternItem(
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(1).rarity(net.minecraft.world.item.Rarity.UNCOMMON),
                    "tooltip.buildscape.golden_jar_pattern"
            )
    );

    public static final RegistryObject<Item> FESTIVE_STAR_PATTERN = ITEMS.register(
            "festive_star_pattern",
            () -> new PatternItem(
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(1).rarity(net.minecraft.world.item.Rarity.RARE),
                    "tooltip.buildscape.festive_star_pattern"
            )
    );

    public static final RegistryObject<Item> BIG_WHITE_ORNAMENT = ITEMS.register("big_white_ornament", () -> new BlockItem(ModBlocks.BIG_WHITE_ORNAMENT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIG_ORANGE_ORNAMENT = ITEMS.register("big_orange_ornament", () -> new BlockItem(ModBlocks.BIG_ORANGE_ORNAMENT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIG_MAGENTA_ORNAMENT = ITEMS.register("big_magenta_ornament", () -> new BlockItem(ModBlocks.BIG_MAGENTA_ORNAMENT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIG_LIGHT_BLUE_ORNAMENT = ITEMS.register("big_light_blue_ornament", () -> new BlockItem(ModBlocks.BIG_LIGHT_BLUE_ORNAMENT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIG_YELLOW_ORNAMENT = ITEMS.register("big_yellow_ornament", () -> new BlockItem(ModBlocks.BIG_YELLOW_ORNAMENT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIG_LIME_ORNAMENT = ITEMS.register("big_lime_ornament", () -> new BlockItem(ModBlocks.BIG_LIME_ORNAMENT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIG_PINK_ORNAMENT = ITEMS.register("big_pink_ornament", () -> new BlockItem(ModBlocks.BIG_PINK_ORNAMENT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIG_GRAY_ORNAMENT = ITEMS.register("big_gray_ornament", () -> new BlockItem(ModBlocks.BIG_GRAY_ORNAMENT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIG_LIGHT_GRAY_ORNAMENT = ITEMS.register("big_light_gray_ornament", () -> new BlockItem(ModBlocks.BIG_LIGHT_GRAY_ORNAMENT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIG_CYAN_ORNAMENT = ITEMS.register("big_cyan_ornament", () -> new BlockItem(ModBlocks.BIG_CYAN_ORNAMENT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIG_PURPLE_ORNAMENT = ITEMS.register("big_purple_ornament", () -> new BlockItem(ModBlocks.BIG_PURPLE_ORNAMENT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIG_BLUE_ORNAMENT = ITEMS.register("big_blue_ornament", () -> new BlockItem(ModBlocks.BIG_BLUE_ORNAMENT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIG_BROWN_ORNAMENT = ITEMS.register("big_brown_ornament", () -> new BlockItem(ModBlocks.BIG_BROWN_ORNAMENT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIG_GREEN_ORNAMENT = ITEMS.register("big_green_ornament", () -> new BlockItem(ModBlocks.BIG_GREEN_ORNAMENT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIG_RED_ORNAMENT = ITEMS.register("big_red_ornament", () -> new BlockItem(ModBlocks.BIG_RED_ORNAMENT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIG_BLACK_ORNAMENT = ITEMS.register("big_black_ornament", () -> new BlockItem(ModBlocks.BIG_BLACK_ORNAMENT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIG_GLASS_ORNAMENT = ITEMS.register("big_glass_ornament", () -> new BlockItem(ModBlocks.BIG_GLASS_ORNAMENT.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BIG_TINTED_GLASS_ORNAMENT = ITEMS.register("big_tinted_glass_ornament", () -> new BlockItem(ModBlocks.BIG_TINTED_GLASS_ORNAMENT.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> BIG_ORNAMENT_TEMPLATE = ITEMS.register(
            "big_ornament_template",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).rarity(net.minecraft.world.item.Rarity.UNCOMMON))
    );

    public static final RegistryObject<Item> STRINGLIGHT_FRAME = ITEMS.register(
            "stringlight_frame",
            () -> new StringlightFrameItem(new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB))
    );

    public static final RegistryObject<Item> STRINGLIGHT_FRAME_PATTERN = ITEMS.register(
            "stringlight_frame_pattern",
            () -> new PatternItem(
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(1).rarity(net.minecraft.world.item.Rarity.RARE),
                    "tooltip.buildscape.stringlight_frame_pattern"
            )
    );

    public static final RegistryObject<Item> MUSIC_DISC_CELEBRATION = ITEMS.register(
            "music_disc_celebration",
            () -> new net.minecraft.world.item.RecordItem(
                    14,
                    com.kingodogo.buildscape.sound.ModSounds.MUSIC_DISC_CELEBRATION,
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(1).rarity(net.minecraft.world.item.Rarity.RARE)
            )
    );

    public static final RegistryObject<Item> MUSIC_DISC_SNOWFALL = ITEMS.register(
            "music_disc_snowfall",
            () -> new net.minecraft.world.item.RecordItem(
                    15,
                    com.kingodogo.buildscape.sound.ModSounds.MUSIC_DISC_SNOWFALL,
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(1).rarity(net.minecraft.world.item.Rarity.RARE)
            )
    );
}

