package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.ModBlocks;
import net.minecraft.world.item.BlockItem;
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
        return new Item.Properties().tab(BuildScape.BUILDSCAPE_TAB);
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

    public static final RegistryObject<Item> QUARTZ_PILLAR_STAIRS = ITEMS.register(
            "quartz_pillar_stairs",
            () ->
                    new BlockItem(ModBlocks.QUARTZ_PILLAR_STAIRS.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> QUARTZ_PILLAR_SLAB = ITEMS.register(
            "quartz_pillar_slab",
            () ->
                    new BlockItem(ModBlocks.QUARTZ_PILLAR_SLAB.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> QUARTZ_PILLAR_WALL = ITEMS.register(
            "quartz_pillar_wall",
            () ->
                    new BlockItem(ModBlocks.QUARTZ_PILLAR_WALL.get(), createBlockItemProperties())
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

    public static final RegistryObject<Item> HAY_BALE_SLAB = ITEMS.register(
            "hay_bale_slab",
            () ->
                    new BlockItem(ModBlocks.HAY_BALE_SLAB.get(), createBlockItemProperties())
    );

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
                    new BlockItem(ModBlocks.SNOWY_LEAVES.get(), createBlockItemProperties())
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
            () -> new Item(new Item.Properties().tab(BuildScape.BUILDSCAPE_TAB))
    );

    public static final RegistryObject<Item> STEEL_INGOT = ITEMS.register(
            "steel_ingot",
            () -> new Item(new Item.Properties().tab(BuildScape.BUILDSCAPE_TAB))
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
                    new Item.Properties().tab(BuildScape.BUILDSCAPE_TAB).stacksTo(16),
                    ModBlocks.MANGROVE_SIGN.get(),
                            ModBlocks.MANGROVE_WALL_SIGN.get()
                    )
    );

    public static final RegistryObject<Item> BAMBOO_SIGN = ITEMS.register(
            "bamboo_sign",
            () ->
                    new net.minecraft.world.item.SignItem(
                    new Item.Properties().tab(BuildScape.BUILDSCAPE_TAB).stacksTo(16),
                    ModBlocks.BAMBOO_SIGN.get(),
                            ModBlocks.BAMBOO_WALL_SIGN.get()
                    )
    );

    public static final RegistryObject<Item> MANGROVE_BOAT = ITEMS.register(
            "mangrove_boat",
            () ->
                    new MangroveBoatItem(
                            new Item.Properties().tab(BuildScape.BUILDSCAPE_TAB).stacksTo(1)
                    )
    );

    public static final RegistryObject<Item> FESTIVE_LAMP = ITEMS.register(
            "festive_lamp",
            () ->
                    new BlockItem(ModBlocks.FESTIVE_LAMP.get(), createBlockItemProperties())
    );

    public static final RegistryObject<Item> FROST_ROSE = ITEMS.register(
            "frost_rose",
            () -> new BlockItem(ModBlocks.FROST_ROSE.get(), createBlockItemProperties())
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
            () -> new ConfettiItem(new Item.Properties().tab(BuildScape.BUILDSCAPE_TAB))
    );

    public static final RegistryObject<Item> STEEL_BLOCK = ITEMS.register(
            "steel_block",
            () -> new BlockItem(ModBlocks.STEEL_BLOCK.get(), createBlockItemProperties())
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

    // Steel Block Variants
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

    // Polished Steel Variants
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

    // Pressed Steel Variants
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

    // Cut Steel Variants
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

    // Caution Blocks
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

    // Caution Slabs
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

    // Caution Stairs
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

    // Factory Glass Blocks
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

    // Factory Glass Panes
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

    // Stained Bricks
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

    // Stained Bricks Slabs
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

    // Stained Bricks Stairs
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

    // Stained Bricks Walls
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

    // Colored Item Frames
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

    // Colored Redstone Lamps
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
            () -> new BottleOfMistItem(new Item.Properties().tab(com.kingodogo.buildscape.BuildScape.BUILDSCAPE_TAB).stacksTo(64))
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
            () -> new Item(new Item.Properties().tab(BuildScape.BUILDSCAPE_TAB).durability(100)));

}
