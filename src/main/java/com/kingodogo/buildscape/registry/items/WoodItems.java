package com.kingodogo.buildscape.registry.items;

import com.kingodogo.buildscape.block.decor.FestiveStockingItem;
import com.kingodogo.buildscape.feature.biome.BiomeBrushItem;
import com.kingodogo.buildscape.feature.pipe.WrenchItem;
import com.kingodogo.buildscape.item.BuildersPouchItem;
import com.kingodogo.buildscape.item.HammerItem;
import com.kingodogo.buildscape.item.MangroveBoatItem;
import com.kingodogo.buildscape.registry.ModCreativeModeTab;
import com.kingodogo.buildscape.registry.blocks.CopperBlocks;
import com.kingodogo.buildscape.registry.blocks.DecorBlocks;
import com.kingodogo.buildscape.registry.blocks.SandstoneBlocks;
import com.kingodogo.buildscape.registry.blocks.StoneBlocks;
import com.kingodogo.buildscape.registry.blocks.WoodBlocks;
import com.kingodogo.buildscape.registry.blocks.WoolBlocks;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;

import static com.kingodogo.buildscape.registry.items.ItemsRegister.ITEMS;
import static com.kingodogo.buildscape.registry.items.ItemsRegister.createBlockItemProperties;
import static com.kingodogo.buildscape.registry.items.ItemsRegister.registerBlockItem;

/**
 * The items BLACK_DECORATED_POT through GLOW_LIGHTS of the registry, part 2 of 8, in registration order.
 * Named for its dominant families (wood, decor, foliage); the neighbouring families that share a run of the order
 * with them are registered here too, because the registration order must not change.
 * Load order: {@link StoneItems#init()} runs first (see the static block), so every holder registers in order whichever is touched first.
 */
public final class WoodItems {

    private WoodItems() {}

    /** Forces this class to load, which registers its items. */
    public static void init() {}

    static {
        StoneItems.init();
    }

    public static final RegistryObject<Item> BLACK_DECORATED_POT = registerBlockItem("black_decorated_pot",
            StoneBlocks.BLACK_DECORATED_POT);
    public static final RegistryObject<Item> BLUE_DECORATED_POT = registerBlockItem("blue_decorated_pot",
            StoneBlocks.BLUE_DECORATED_POT);
    public static final RegistryObject<Item> BROWN_DECORATED_POT = registerBlockItem("brown_decorated_pot",
            StoneBlocks.BROWN_DECORATED_POT);
    public static final RegistryObject<Item> CYAN_DECORATED_POT = registerBlockItem("cyan_decorated_pot",
            StoneBlocks.CYAN_DECORATED_POT);
    public static final RegistryObject<Item> GRAY_DECORATED_POT = registerBlockItem("gray_decorated_pot",
            WoodBlocks.GRAY_DECORATED_POT);
    public static final RegistryObject<Item> GREEN_DECORATED_POT = registerBlockItem("green_decorated_pot",
            WoodBlocks.GREEN_DECORATED_POT);
    public static final RegistryObject<Item> LIGHT_BLUE_DECORATED_POT = registerBlockItem("light_blue_decorated_pot",
            WoodBlocks.LIGHT_BLUE_DECORATED_POT);
    public static final RegistryObject<Item> LIGHT_GRAY_DECORATED_POT = registerBlockItem("light_gray_decorated_pot",
            WoodBlocks.LIGHT_GRAY_DECORATED_POT);
    public static final RegistryObject<Item> LIME_DECORATED_POT = registerBlockItem("lime_decorated_pot",
            WoodBlocks.LIME_DECORATED_POT);
    public static final RegistryObject<Item> MAGENTA_DECORATED_POT = registerBlockItem("magenta_decorated_pot",
            WoodBlocks.MAGENTA_DECORATED_POT);
    public static final RegistryObject<Item> ORANGE_DECORATED_POT = registerBlockItem("orange_decorated_pot",
            WoodBlocks.ORANGE_DECORATED_POT);
    public static final RegistryObject<Item> PINK_DECORATED_POT = registerBlockItem("pink_decorated_pot",
            WoodBlocks.PINK_DECORATED_POT);
    public static final RegistryObject<Item> PURPLE_DECORATED_POT = registerBlockItem("purple_decorated_pot",
            WoodBlocks.PURPLE_DECORATED_POT);
    public static final RegistryObject<Item> RED_DECORATED_POT = registerBlockItem("red_decorated_pot",
            WoodBlocks.RED_DECORATED_POT);
    public static final RegistryObject<Item> WHITE_DECORATED_POT = registerBlockItem("white_decorated_pot",
            WoodBlocks.WHITE_DECORATED_POT);
    public static final RegistryObject<Item> YELLOW_DECORATED_POT = registerBlockItem("yellow_decorated_pot",
            WoodBlocks.YELLOW_DECORATED_POT);
    public static final RegistryObject<Item> TRAPPED_DECORATED_POT = registerBlockItem("trapped_decorated_pot",
            WoodBlocks.TRAPPED_DECORATED_POT);
    public static final RegistryObject<Item> BLACK_TRAPPED_DECORATED_POT = registerBlockItem(
            "black_trapped_decorated_pot", WoodBlocks.BLACK_TRAPPED_DECORATED_POT);
    public static final RegistryObject<Item> BLUE_TRAPPED_DECORATED_POT = registerBlockItem(
            "blue_trapped_decorated_pot", WoodBlocks.BLUE_TRAPPED_DECORATED_POT);
    public static final RegistryObject<Item> BROWN_TRAPPED_DECORATED_POT = registerBlockItem(
            "brown_trapped_decorated_pot", WoodBlocks.BROWN_TRAPPED_DECORATED_POT);
    public static final RegistryObject<Item> CYAN_TRAPPED_DECORATED_POT = registerBlockItem(
            "cyan_trapped_decorated_pot", WoodBlocks.CYAN_TRAPPED_DECORATED_POT);
    public static final RegistryObject<Item> GRAY_TRAPPED_DECORATED_POT = registerBlockItem(
            "gray_trapped_decorated_pot", WoodBlocks.GRAY_TRAPPED_DECORATED_POT);
    public static final RegistryObject<Item> GREEN_TRAPPED_DECORATED_POT = registerBlockItem(
            "green_trapped_decorated_pot", WoodBlocks.GREEN_TRAPPED_DECORATED_POT);
    public static final RegistryObject<Item> LIGHT_BLUE_TRAPPED_DECORATED_POT = registerBlockItem(
            "light_blue_trapped_decorated_pot", WoodBlocks.LIGHT_BLUE_TRAPPED_DECORATED_POT);
    public static final RegistryObject<Item> LIGHT_GRAY_TRAPPED_DECORATED_POT = registerBlockItem(
            "light_gray_trapped_decorated_pot", WoodBlocks.LIGHT_GRAY_TRAPPED_DECORATED_POT);
    public static final RegistryObject<Item> LIME_TRAPPED_DECORATED_POT = registerBlockItem(
            "lime_trapped_decorated_pot", WoodBlocks.LIME_TRAPPED_DECORATED_POT);
    public static final RegistryObject<Item> MAGENTA_TRAPPED_DECORATED_POT = registerBlockItem(
            "magenta_trapped_decorated_pot", WoodBlocks.MAGENTA_TRAPPED_DECORATED_POT);
    public static final RegistryObject<Item> ORANGE_TRAPPED_DECORATED_POT = registerBlockItem(
            "orange_trapped_decorated_pot", WoodBlocks.ORANGE_TRAPPED_DECORATED_POT);
    public static final RegistryObject<Item> PINK_TRAPPED_DECORATED_POT = registerBlockItem(
            "pink_trapped_decorated_pot", WoodBlocks.PINK_TRAPPED_DECORATED_POT);
    public static final RegistryObject<Item> PURPLE_TRAPPED_DECORATED_POT = registerBlockItem(
            "purple_trapped_decorated_pot", WoodBlocks.PURPLE_TRAPPED_DECORATED_POT);
    public static final RegistryObject<Item> RED_TRAPPED_DECORATED_POT = registerBlockItem("red_trapped_decorated_pot",
            WoodBlocks.RED_TRAPPED_DECORATED_POT);
    public static final RegistryObject<Item> WHITE_TRAPPED_DECORATED_POT = registerBlockItem(
            "white_trapped_decorated_pot", WoodBlocks.WHITE_TRAPPED_DECORATED_POT);
    public static final RegistryObject<Item> YELLOW_TRAPPED_DECORATED_POT = registerBlockItem(
            "yellow_trapped_decorated_pot", WoodBlocks.YELLOW_TRAPPED_DECORATED_POT);

    public static final RegistryObject<Item> FESTIVE_STOCKING = ITEMS.register(
            "festive_stocking",
            () -> new FestiveStockingItem(
                    createBlockItemProperties(),
                    "festive"));
    public static final RegistryObject<Item> BLACK_FESTIVE_STOCKING = ITEMS.register("black_festive_stocking",
            () -> new FestiveStockingItem(
                    createBlockItemProperties(),
                    "black"));
    public static final RegistryObject<Item> BLUE_FESTIVE_STOCKING = ITEMS.register("blue_festive_stocking",
            () -> new FestiveStockingItem(
                    createBlockItemProperties(),
                    "blue"));
    public static final RegistryObject<Item> BROWN_FESTIVE_STOCKING = ITEMS.register("brown_festive_stocking",
            () -> new FestiveStockingItem(
                    createBlockItemProperties(),
                    "brown"));
    public static final RegistryObject<Item> CYAN_FESTIVE_STOCKING = ITEMS.register("cyan_festive_stocking",
            () -> new FestiveStockingItem(
                    createBlockItemProperties(),
                    "cyan"));
    public static final RegistryObject<Item> GRAY_FESTIVE_STOCKING = ITEMS.register("gray_festive_stocking",
            () -> new FestiveStockingItem(
                    createBlockItemProperties(),
                    "gray"));
    public static final RegistryObject<Item> GREEN_FESTIVE_STOCKING = ITEMS.register("green_festive_stocking",
            () -> new FestiveStockingItem(
                    createBlockItemProperties(),
                    "green"));
    public static final RegistryObject<Item> LIGHT_BLUE_FESTIVE_STOCKING = ITEMS.register("light_blue_festive_stocking",
            () -> new FestiveStockingItem(
                    createBlockItemProperties(),
                    "light_blue"));
    public static final RegistryObject<Item> LIGHT_GRAY_FESTIVE_STOCKING = ITEMS.register("light_gray_festive_stocking",
            () -> new FestiveStockingItem(
                    createBlockItemProperties(),
                    "light_gray"));
    public static final RegistryObject<Item> LIME_FESTIVE_STOCKING = ITEMS.register("lime_festive_stocking",
            () -> new FestiveStockingItem(
                    createBlockItemProperties(),
                    "lime"));
    public static final RegistryObject<Item> MAGENTA_FESTIVE_STOCKING = ITEMS.register("magenta_festive_stocking",
            () -> new FestiveStockingItem(
                    createBlockItemProperties(),
                    "magenta"));
    public static final RegistryObject<Item> ORANGE_FESTIVE_STOCKING = ITEMS.register("orange_festive_stocking",
            () -> new FestiveStockingItem(
                    createBlockItemProperties(),
                    "orange"));
    public static final RegistryObject<Item> PINK_FESTIVE_STOCKING = ITEMS.register("pink_festive_stocking",
            () -> new FestiveStockingItem(
                    createBlockItemProperties(),
                    "pink"));
    public static final RegistryObject<Item> PURPLE_FESTIVE_STOCKING = ITEMS.register("purple_festive_stocking",
            () -> new FestiveStockingItem(
                    createBlockItemProperties(),
                    "purple"));
    public static final RegistryObject<Item> RED_FESTIVE_STOCKING = ITEMS.register("red_festive_stocking",
            () -> new FestiveStockingItem(
                    createBlockItemProperties(),
                    "red"));
    public static final RegistryObject<Item> WHITE_FESTIVE_STOCKING = ITEMS.register("white_festive_stocking",
            () -> new FestiveStockingItem(
                    createBlockItemProperties(),
                    "white"));
    public static final RegistryObject<Item> YELLOW_FESTIVE_STOCKING = ITEMS.register("yellow_festive_stocking",
            () -> new FestiveStockingItem(
                    createBlockItemProperties(),
                    "yellow"));
    public static final RegistryObject<Item> BLACK_CARPET_LAYERS = registerBlockItem("black_carpet_layers",
            WoodBlocks.BLACK_CARPET_LAYERS);
    public static final RegistryObject<Item> BLUE_CARPET_LAYERS = registerBlockItem("blue_carpet_layers",
            WoodBlocks.BLUE_CARPET_LAYERS);
    public static final RegistryObject<Item> BROWN_CARPET_LAYERS = registerBlockItem("brown_carpet_layers",
            WoodBlocks.BROWN_CARPET_LAYERS);
    public static final RegistryObject<Item> CYAN_CARPET_LAYERS = registerBlockItem("cyan_carpet_layers",
            WoodBlocks.CYAN_CARPET_LAYERS);
    public static final RegistryObject<Item> GRAY_CARPET_LAYERS = registerBlockItem("gray_carpet_layers",
            WoodBlocks.GRAY_CARPET_LAYERS);
    public static final RegistryObject<Item> GREEN_CARPET_LAYERS = registerBlockItem("green_carpet_layers",
            WoodBlocks.GREEN_CARPET_LAYERS);
    public static final RegistryObject<Item> LIGHT_BLUE_CARPET_LAYERS = registerBlockItem("light_blue_carpet_layers",
            WoodBlocks.LIGHT_BLUE_CARPET_LAYERS);
    public static final RegistryObject<Item> LIGHT_GRAY_CARPET_LAYERS = registerBlockItem("light_gray_carpet_layers",
            WoodBlocks.LIGHT_GRAY_CARPET_LAYERS);
    public static final RegistryObject<Item> LIME_CARPET_LAYERS = registerBlockItem("lime_carpet_layers",
            WoodBlocks.LIME_CARPET_LAYERS);
    public static final RegistryObject<Item> MAGENTA_CARPET_LAYERS = registerBlockItem("magenta_carpet_layers",
            WoodBlocks.MAGENTA_CARPET_LAYERS);
    public static final RegistryObject<Item> ORANGE_CARPET_LAYERS = registerBlockItem("orange_carpet_layers",
            WoodBlocks.ORANGE_CARPET_LAYERS);
    public static final RegistryObject<Item> PINK_CARPET_LAYERS = registerBlockItem("pink_carpet_layers",
            WoodBlocks.PINK_CARPET_LAYERS);
    public static final RegistryObject<Item> PURPLE_CARPET_LAYERS = registerBlockItem("purple_carpet_layers",
            WoodBlocks.PURPLE_CARPET_LAYERS);
    public static final RegistryObject<Item> RED_CARPET_LAYERS = registerBlockItem("red_carpet_layers",
            WoodBlocks.RED_CARPET_LAYERS);
    public static final RegistryObject<Item> WHITE_CARPET_LAYERS = registerBlockItem("white_carpet_layers",
            WoodBlocks.WHITE_CARPET_LAYERS);
    public static final RegistryObject<Item> YELLOW_CARPET_LAYERS = registerBlockItem("yellow_carpet_layers",
            WoodBlocks.YELLOW_CARPET_LAYERS);
    public static final RegistryObject<Item> OAK_LEAF_LAYERS = registerBlockItem("oak_leaf_layers",
            WoodBlocks.OAK_LEAF_LAYERS);
    public static final RegistryObject<Item> SPRUCE_LEAF_LAYERS = registerBlockItem("spruce_leaf_layers",
            WoodBlocks.SPRUCE_LEAF_LAYERS);
    public static final RegistryObject<Item> BIRCH_LEAF_LAYERS = registerBlockItem("birch_leaf_layers",
            WoodBlocks.BIRCH_LEAF_LAYERS);
    public static final RegistryObject<Item> JUNGLE_LEAF_LAYERS = registerBlockItem("jungle_leaf_layers",
            WoodBlocks.JUNGLE_LEAF_LAYERS);
    public static final RegistryObject<Item> ACACIA_LEAF_LAYERS = registerBlockItem("acacia_leaf_layers",
            WoodBlocks.ACACIA_LEAF_LAYERS);
    public static final RegistryObject<Item> DARK_OAK_LEAF_LAYERS = registerBlockItem("dark_oak_leaf_layers",
            WoodBlocks.DARK_OAK_LEAF_LAYERS);
    public static final RegistryObject<Item> AZALEA_LEAF_LAYERS = registerBlockItem("azalea_leaf_layers",
            WoodBlocks.AZALEA_LEAF_LAYERS);
    public static final RegistryObject<Item> FLOWERING_AZALEA_LEAF_LAYERS = registerBlockItem(
            "flowering_azalea_leaf_layers", WoodBlocks.FLOWERING_AZALEA_LEAF_LAYERS);
    public static final RegistryObject<Item> SNOWY_OAK_LEAF_LAYERS = registerBlockItem("snowy_oak_leaf_layers",
            WoodBlocks.SNOWY_OAK_LEAF_LAYERS);
    public static final RegistryObject<Item> SNOWY_SPRUCE_LEAF_LAYERS = registerBlockItem("snowy_spruce_leaf_layers",
            WoodBlocks.SNOWY_SPRUCE_LEAF_LAYERS);
    public static final RegistryObject<Item> SNOWY_BIRCH_LEAF_LAYERS = registerBlockItem("snowy_birch_leaf_layers",
            WoodBlocks.SNOWY_BIRCH_LEAF_LAYERS);
    public static final RegistryObject<Item> SNOWY_JUNGLE_LEAF_LAYERS = registerBlockItem("snowy_jungle_leaf_layers",
            WoodBlocks.SNOWY_JUNGLE_LEAF_LAYERS);
    public static final RegistryObject<Item> SNOWY_ACACIA_LEAF_LAYERS = registerBlockItem("snowy_acacia_leaf_layers",
            WoodBlocks.SNOWY_ACACIA_LEAF_LAYERS);
    public static final RegistryObject<Item> SNOWY_DARK_OAK_LEAF_LAYERS = registerBlockItem(
            "snowy_dark_oak_leaf_layers", WoodBlocks.SNOWY_DARK_OAK_LEAF_LAYERS);
    public static final RegistryObject<Item> SNOWY_MANGROVE_LEAF_LAYERS = registerBlockItem(
            "snowy_mangrove_leaf_layers", WoodBlocks.SNOWY_MANGROVE_LEAF_LAYERS);
    public static final RegistryObject<Item> SNOWY_AZALEA_LEAF_LAYERS = registerBlockItem("snowy_azalea_leaf_layers",
            WoodBlocks.SNOWY_AZALEA_LEAF_LAYERS);
    public static final RegistryObject<Item> SNOWY_FLOWERING_AZALEA_LEAF_LAYERS = registerBlockItem(
            "snowy_flowering_azalea_leaf_layers", WoodBlocks.SNOWY_FLOWERING_AZALEA_LEAF_LAYERS);
    public static final RegistryObject<Item> MANGROVE_LEAF_LAYERS = registerBlockItem("mangrove_leaf_layers",
            WoodBlocks.MANGROVE_LEAF_LAYERS);
    public static final RegistryObject<Item> ORANGE_POPLAR_LEAF_LAYERS = registerBlockItem("orange_poplar_leaf_layers",
            WoodBlocks.ORANGE_POPLAR_LEAF_LAYERS);
    public static final RegistryObject<Item> RED_POPLAR_LEAF_LAYERS = registerBlockItem("red_poplar_leaf_layers",
            WoodBlocks.RED_POPLAR_LEAF_LAYERS);
    public static final RegistryObject<Item> YELLOW_POPLAR_LEAF_LAYERS = registerBlockItem("yellow_poplar_leaf_layers",
            WoodBlocks.YELLOW_POPLAR_LEAF_LAYERS);
    public static final RegistryObject<Item> CHERRY_LEAF_LAYERS = registerBlockItem("cherry_leaf_layers",
            WoodBlocks.CHERRY_LEAF_LAYERS);
    public static final RegistryObject<Item> PALE_OAK_LEAF_LAYERS = registerBlockItem("pale_oak_leaf_layers",
            WoodBlocks.PALE_OAK_LEAF_LAYERS);
    public static final RegistryObject<Item> SNOWY_CHERRY_LEAF_LAYERS = registerBlockItem("snowy_cherry_leaf_layers",
            WoodBlocks.SNOWY_CHERRY_LEAF_LAYERS);
    public static final RegistryObject<Item> SNOWY_PALE_OAK_LEAF_LAYERS = registerBlockItem(
            "snowy_pale_oak_leaf_layers", WoodBlocks.SNOWY_PALE_OAK_LEAF_LAYERS);
    public static final RegistryObject<Item> SNOWY_ORANGE_POPLAR_LEAF_LAYERS = registerBlockItem(
            "snowy_orange_poplar_leaf_layers", CopperBlocks.SNOWY_ORANGE_POPLAR_LEAF_LAYERS);
    public static final RegistryObject<Item> SNOWY_RED_POPLAR_LEAF_LAYERS = registerBlockItem(
            "snowy_red_poplar_leaf_layers", CopperBlocks.SNOWY_RED_POPLAR_LEAF_LAYERS);
    public static final RegistryObject<Item> SNOWY_YELLOW_POPLAR_LEAF_LAYERS = registerBlockItem(
            "snowy_yellow_poplar_leaf_layers", CopperBlocks.SNOWY_YELLOW_POPLAR_LEAF_LAYERS);
    public static final RegistryObject<Item> OAK_LEAF_HEDGE = registerBlockItem("oak_leaf_hedge",
            WoodBlocks.OAK_LEAF_HEDGE);
    public static final RegistryObject<Item> SPRUCE_LEAF_HEDGE = registerBlockItem("spruce_leaf_hedge",
            WoodBlocks.SPRUCE_LEAF_HEDGE);
    public static final RegistryObject<Item> BIRCH_LEAF_HEDGE = registerBlockItem("birch_leaf_hedge",
            WoodBlocks.BIRCH_LEAF_HEDGE);
    public static final RegistryObject<Item> JUNGLE_LEAF_HEDGE = registerBlockItem("jungle_leaf_hedge",
            WoodBlocks.JUNGLE_LEAF_HEDGE);
    public static final RegistryObject<Item> ACACIA_LEAF_HEDGE = registerBlockItem("acacia_leaf_hedge",
            WoodBlocks.ACACIA_LEAF_HEDGE);
    public static final RegistryObject<Item> DARK_OAK_LEAF_HEDGE = registerBlockItem("dark_oak_leaf_hedge",
            WoodBlocks.DARK_OAK_LEAF_HEDGE);
    public static final RegistryObject<Item> AZALEA_LEAF_HEDGE = registerBlockItem("azalea_leaf_hedge",
            WoodBlocks.AZALEA_LEAF_HEDGE);
    public static final RegistryObject<Item> FLOWERING_AZALEA_LEAF_HEDGE = registerBlockItem(
            "flowering_azalea_leaf_hedge", WoodBlocks.FLOWERING_AZALEA_LEAF_HEDGE);
    public static final RegistryObject<Item> SNOWY_OAK_LEAF_HEDGE = registerBlockItem("snowy_oak_leaf_hedge",
            WoodBlocks.SNOWY_OAK_LEAF_HEDGE);
    public static final RegistryObject<Item> SNOWY_SPRUCE_LEAF_HEDGE = registerBlockItem("snowy_spruce_leaf_hedge",
            WoodBlocks.SNOWY_SPRUCE_LEAF_HEDGE);
    public static final RegistryObject<Item> SNOWY_BIRCH_LEAF_HEDGE = registerBlockItem("snowy_birch_leaf_hedge",
            WoodBlocks.SNOWY_BIRCH_LEAF_HEDGE);
    public static final RegistryObject<Item> SNOWY_JUNGLE_LEAF_HEDGE = registerBlockItem("snowy_jungle_leaf_hedge",
            WoodBlocks.SNOWY_JUNGLE_LEAF_HEDGE);
    public static final RegistryObject<Item> SNOWY_ACACIA_LEAF_HEDGE = registerBlockItem("snowy_acacia_leaf_hedge",
            WoodBlocks.SNOWY_ACACIA_LEAF_HEDGE);
    public static final RegistryObject<Item> SNOWY_DARK_OAK_LEAF_HEDGE = registerBlockItem("snowy_dark_oak_leaf_hedge",
            WoodBlocks.SNOWY_DARK_OAK_LEAF_HEDGE);
    public static final RegistryObject<Item> SNOWY_MANGROVE_LEAF_HEDGE = registerBlockItem("snowy_mangrove_leaf_hedge",
            WoodBlocks.SNOWY_MANGROVE_LEAF_HEDGE);
    public static final RegistryObject<Item> SNOWY_AZALEA_LEAF_HEDGE = registerBlockItem("snowy_azalea_leaf_hedge",
            WoodBlocks.SNOWY_AZALEA_LEAF_HEDGE);
    public static final RegistryObject<Item> SNOWY_FLOWERING_AZALEA_LEAF_HEDGE = registerBlockItem(
            "snowy_flowering_azalea_leaf_hedge", WoodBlocks.SNOWY_FLOWERING_AZALEA_LEAF_HEDGE);
    public static final RegistryObject<Item> MANGROVE_LEAF_HEDGE = registerBlockItem("mangrove_leaf_hedge",
            WoodBlocks.MANGROVE_LEAF_HEDGE);
    public static final RegistryObject<Item> ORANGE_POPLAR_LEAF_HEDGE = registerBlockItem("orange_poplar_leaf_hedge",
            WoodBlocks.ORANGE_POPLAR_LEAF_HEDGE);
    public static final RegistryObject<Item> RED_POPLAR_LEAF_HEDGE = registerBlockItem("red_poplar_leaf_hedge",
            WoodBlocks.RED_POPLAR_LEAF_HEDGE);
    public static final RegistryObject<Item> YELLOW_POPLAR_LEAF_HEDGE = registerBlockItem("yellow_poplar_leaf_hedge",
            WoodBlocks.YELLOW_POPLAR_LEAF_HEDGE);
    public static final RegistryObject<Item> CHERRY_LEAF_HEDGE = registerBlockItem("cherry_leaf_hedge",
            WoodBlocks.CHERRY_LEAF_HEDGE);
    public static final RegistryObject<Item> PALE_OAK_LEAF_HEDGE = registerBlockItem("pale_oak_leaf_hedge",
            WoodBlocks.PALE_OAK_LEAF_HEDGE);
    public static final RegistryObject<Item> SNOWY_CHERRY_LEAF_HEDGE = registerBlockItem("snowy_cherry_leaf_hedge",
            WoodBlocks.SNOWY_CHERRY_LEAF_HEDGE);
    public static final RegistryObject<Item> SNOWY_PALE_OAK_LEAF_HEDGE = registerBlockItem("snowy_pale_oak_leaf_hedge",
            WoodBlocks.SNOWY_PALE_OAK_LEAF_HEDGE);
    public static final RegistryObject<Item> SNOWY_ORANGE_POPLAR_LEAF_HEDGE = registerBlockItem(
            "snowy_orange_poplar_leaf_hedge", CopperBlocks.SNOWY_ORANGE_POPLAR_LEAF_HEDGE);
    public static final RegistryObject<Item> SNOWY_RED_POPLAR_LEAF_HEDGE = registerBlockItem(
            "snowy_red_poplar_leaf_hedge", CopperBlocks.SNOWY_RED_POPLAR_LEAF_HEDGE);
    public static final RegistryObject<Item> SNOWY_YELLOW_POPLAR_LEAF_HEDGE = registerBlockItem(
            "snowy_yellow_poplar_leaf_hedge", CopperBlocks.SNOWY_YELLOW_POPLAR_LEAF_HEDGE);
    public static final RegistryObject<Item> HAY_BALE_SLAB = registerBlockItem("hay_bale_slab",
            WoodBlocks.HAY_BALE_SLAB);
    public static final RegistryObject<Item> HAY_BALE_STAIRS = registerBlockItem("hay_bale_stairs",
            WoodBlocks.HAY_BALE_STAIRS);
    public static final RegistryObject<Item> BAMBOO_BLOCK = registerBlockItem("bamboo_block", WoodBlocks.BAMBOO_BLOCK);
    public static final RegistryObject<Item> STRIPPED_BAMBOO_BLOCK = registerBlockItem("stripped_bamboo_block",
            WoodBlocks.STRIPPED_BAMBOO_BLOCK);
    public static final RegistryObject<Item> BAMBOO_WOOD = registerBlockItem("bamboo_wood", WoodBlocks.BAMBOO_WOOD);
    public static final RegistryObject<Item> STRIPPED_BAMBOO_WOOD = registerBlockItem("stripped_bamboo_wood",
            WoodBlocks.STRIPPED_BAMBOO_WOOD);
    public static final RegistryObject<Item> BAMBOO_BLOCK_SLAB = registerBlockItem("bamboo_block_slab",
            WoodBlocks.BAMBOO_BLOCK_SLAB);
    public static final RegistryObject<Item> BAMBOO_BLOCK_STAIRS = registerBlockItem("bamboo_block_stairs",
            WoodBlocks.BAMBOO_BLOCK_STAIRS);
    public static final RegistryObject<Item> BAMBOO_BLOCK_FENCE = registerBlockItem("bamboo_block_fence",
            WoodBlocks.BAMBOO_BLOCK_FENCE);
    public static final RegistryObject<Item> BAMBOO_BLOCK_FENCE_GATE = registerBlockItem("bamboo_block_fence_gate",
            WoodBlocks.BAMBOO_BLOCK_FENCE_GATE);
    public static final RegistryObject<Item> BAMBOO_BLOCK_PRESSURE_PLATE = registerBlockItem(
            "bamboo_block_pressure_plate", WoodBlocks.BAMBOO_BLOCK_PRESSURE_PLATE);
    public static final RegistryObject<Item> BAMBOO_BLOCK_BUTTON = registerBlockItem("bamboo_block_button",
            WoodBlocks.BAMBOO_BLOCK_BUTTON);
    public static final RegistryObject<Item> STRIPPED_BAMBOO_BLOCK_SLAB = registerBlockItem(
            "stripped_bamboo_block_slab", WoodBlocks.STRIPPED_BAMBOO_BLOCK_SLAB);
    public static final RegistryObject<Item> STRIPPED_BAMBOO_BLOCK_STAIRS = registerBlockItem(
            "stripped_bamboo_block_stairs", WoodBlocks.STRIPPED_BAMBOO_BLOCK_STAIRS);
    public static final RegistryObject<Item> STRIPPED_BAMBOO_BLOCK_FENCE = registerBlockItem(
            "stripped_bamboo_block_fence", WoodBlocks.STRIPPED_BAMBOO_BLOCK_FENCE);
    public static final RegistryObject<Item> STRIPPED_BAMBOO_BLOCK_FENCE_GATE = registerBlockItem(
            "stripped_bamboo_block_fence_gate", WoodBlocks.STRIPPED_BAMBOO_BLOCK_FENCE_GATE);
    public static final RegistryObject<Item> STRIPPED_BAMBOO_BLOCK_PRESSURE_PLATE = registerBlockItem(
            "stripped_bamboo_block_pressure_plate", WoodBlocks.STRIPPED_BAMBOO_BLOCK_PRESSURE_PLATE);
    public static final RegistryObject<Item> STRIPPED_BAMBOO_BLOCK_BUTTON = registerBlockItem(
            "stripped_bamboo_block_button", WoodBlocks.STRIPPED_BAMBOO_BLOCK_BUTTON);
    public static final RegistryObject<Item> BAMBOO_DOOR = registerBlockItem("bamboo_door", WoodBlocks.BAMBOO_DOOR);
    public static final RegistryObject<Item> BAMBOO_TRAPDOOR = registerBlockItem("bamboo_trapdoor",
            WoodBlocks.BAMBOO_TRAPDOOR);
    public static final RegistryObject<Item> DIAMOND_CHAIN = registerBlockItem("diamond_chain",
            WoodBlocks.DIAMOND_CHAIN);
    public static final RegistryObject<Item> GOLD_CHAIN = registerBlockItem("gold_chain", WoodBlocks.GOLD_CHAIN);
    public static final RegistryObject<Item> EMERALD_CHAIN = registerBlockItem("emerald_chain",
            WoodBlocks.EMERALD_CHAIN);
    public static final RegistryObject<Item> ANCIENT_STEEL_CHAIN = registerBlockItem("ancient_steel_chain",
            WoodBlocks.ANCIENT_STEEL_CHAIN);
    public static final RegistryObject<Item> NETHERITE_CHAIN = registerBlockItem("netherite_chain",
            WoodBlocks.NETHERITE_CHAIN);
    public static final RegistryObject<Item> COPPER_CHAIN = registerBlockItem("copper_chain", WoodBlocks.COPPER_CHAIN);
    public static final RegistryObject<Item> EXPOSED_COPPER_CHAIN = registerBlockItem("exposed_copper_chain",
            WoodBlocks.EXPOSED_COPPER_CHAIN);
    public static final RegistryObject<Item> WEATHERED_COPPER_CHAIN = registerBlockItem("weathered_copper_chain",
            WoodBlocks.WEATHERED_COPPER_CHAIN);
    public static final RegistryObject<Item> OXIDIZED_COPPER_CHAIN = registerBlockItem("oxidized_copper_chain",
            WoodBlocks.OXIDIZED_COPPER_CHAIN);
    public static final RegistryObject<Item> LARGE_IRON_CHAIN = registerBlockItem("large_iron_chain",
            WoodBlocks.LARGE_IRON_CHAIN);
    public static final RegistryObject<Item> LARGE_COPPER_CHAIN = registerBlockItem("large_copper_chain",
            CopperBlocks.LARGE_COPPER_CHAIN);
    public static final RegistryObject<Item> LARGE_EXPOSED_COPPER_CHAIN = registerBlockItem(
            "large_exposed_copper_chain", CopperBlocks.LARGE_EXPOSED_COPPER_CHAIN);
    public static final RegistryObject<Item> LARGE_WEATHERED_COPPER_CHAIN = registerBlockItem(
            "large_weathered_copper_chain", WoolBlocks.LARGE_WEATHERED_COPPER_CHAIN);
    public static final RegistryObject<Item> LARGE_OXIDIZED_COPPER_CHAIN = registerBlockItem(
            "large_oxidized_copper_chain", WoolBlocks.LARGE_OXIDIZED_COPPER_CHAIN);
    public static final RegistryObject<Item> LARGE_GOLD_CHAIN = registerBlockItem("large_gold_chain",
            WoodBlocks.LARGE_GOLD_CHAIN);
    public static final RegistryObject<Item> LARGE_DIAMOND_CHAIN = registerBlockItem("large_diamond_chain",
            WoodBlocks.LARGE_DIAMOND_CHAIN);
    public static final RegistryObject<Item> LARGE_EMERALD_CHAIN = registerBlockItem("large_emerald_chain",
            WoodBlocks.LARGE_EMERALD_CHAIN);
    public static final RegistryObject<Item> LARGE_ANCIENT_STEEL_CHAIN = registerBlockItem("large_ancient_steel_chain",
            WoodBlocks.LARGE_ANCIENT_STEEL_CHAIN);
    public static final RegistryObject<Item> LARGE_NETHERITE_CHAIN = registerBlockItem("large_netherite_chain",
            WoodBlocks.LARGE_NETHERITE_CHAIN);
    public static final RegistryObject<Item> OAK_WOOD_WALL = registerBlockItem("oak_wood_wall",
            WoodBlocks.OAK_WOOD_WALL);
    public static final RegistryObject<Item> SPRUCE_WOOD_WALL = registerBlockItem("spruce_wood_wall",
            WoodBlocks.SPRUCE_WOOD_WALL);
    public static final RegistryObject<Item> BIRCH_WOOD_WALL = registerBlockItem("birch_wood_wall",
            WoodBlocks.BIRCH_WOOD_WALL);
    public static final RegistryObject<Item> DARK_OAK_WOOD_WALL = registerBlockItem("dark_oak_wood_wall",
            WoodBlocks.DARK_OAK_WOOD_WALL);
    public static final RegistryObject<Item> JUNGLE_WOOD_WALL = registerBlockItem("jungle_wood_wall",
            WoodBlocks.JUNGLE_WOOD_WALL);
    public static final RegistryObject<Item> ACACIA_WOOD_WALL = registerBlockItem("acacia_wood_wall",
            WoodBlocks.ACACIA_WOOD_WALL);
    public static final RegistryObject<Item> BAMBOO_BLOCK_WALL = registerBlockItem("bamboo_block_wall",
            WoodBlocks.BAMBOO_BLOCK_WALL);
    public static final RegistryObject<Item> MANGROVE_WOOD_WALL = registerBlockItem("mangrove_wood_wall",
            WoodBlocks.MANGROVE_WOOD_WALL);
    public static final RegistryObject<Item> STRIPPED_OAK_WOOD_WALL = registerBlockItem("stripped_oak_wood_wall",
            WoodBlocks.STRIPPED_OAK_WOOD_WALL);
    public static final RegistryObject<Item> STRIPPED_SPRUCE_WOOD_WALL = registerBlockItem("stripped_spruce_wood_wall",
            WoodBlocks.STRIPPED_SPRUCE_WOOD_WALL);
    public static final RegistryObject<Item> STRIPPED_BIRCH_WOOD_WALL = registerBlockItem("stripped_birch_wood_wall",
            WoodBlocks.STRIPPED_BIRCH_WOOD_WALL);
    public static final RegistryObject<Item> STRIPPED_DARK_OAK_WOOD_WALL = registerBlockItem(
            "stripped_dark_oak_wood_wall", WoodBlocks.STRIPPED_DARK_OAK_WOOD_WALL);
    public static final RegistryObject<Item> STRIPPED_JUNGLE_WOOD_WALL = registerBlockItem("stripped_jungle_wood_wall",
            WoodBlocks.STRIPPED_JUNGLE_WOOD_WALL);
    public static final RegistryObject<Item> STRIPPED_ACACIA_WOOD_WALL = registerBlockItem("stripped_acacia_wood_wall",
            WoodBlocks.STRIPPED_ACACIA_WOOD_WALL);
    public static final RegistryObject<Item> STRIPPED_BAMBOO_BLOCK_WALL = registerBlockItem(
            "stripped_bamboo_block_wall", WoodBlocks.STRIPPED_BAMBOO_BLOCK_WALL);
    public static final RegistryObject<Item> STRIPPED_MANGROVE_WOOD_WALL = registerBlockItem(
            "stripped_mangrove_wood_wall", WoodBlocks.STRIPPED_MANGROVE_WOOD_WALL);
    public static final RegistryObject<Item> CHERRY_WOOD_WALL = registerBlockItem("cherry_wood_wall",
            WoodBlocks.CHERRY_WOOD_WALL);
    public static final RegistryObject<Item> STRIPPED_CHERRY_WOOD_WALL = registerBlockItem("stripped_cherry_wood_wall",
            WoodBlocks.STRIPPED_CHERRY_WOOD_WALL);
    public static final RegistryObject<Item> PALE_OAK_WOOD_WALL = registerBlockItem("pale_oak_wood_wall",
            WoodBlocks.PALE_OAK_WOOD_WALL);
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_WOOD_WALL = registerBlockItem(
            "stripped_pale_oak_wood_wall", WoodBlocks.STRIPPED_PALE_OAK_WOOD_WALL);
    public static final RegistryObject<Item> ASHPEN_WOOD_WALL = registerBlockItem("ashpen_wood_wall",
            WoodBlocks.ASHPEN_WOOD_WALL);
    public static final RegistryObject<Item> STRIPPED_ASHPEN_WOOD_WALL = registerBlockItem("stripped_ashpen_wood_wall",
            WoodBlocks.STRIPPED_ASHPEN_WOOD_WALL);
    public static final RegistryObject<Item> ASHPEN_WHITE_PLANKS = registerBlockItem("ashpen_white_planks",
            WoodBlocks.ASHPEN_WHITE_PLANKS);
    public static final RegistryObject<Item> ASHPEN_WHITE_STAIRS = registerBlockItem("ashpen_white_stairs",
            WoodBlocks.ASHPEN_WHITE_STAIRS);
    public static final RegistryObject<Item> ASHPEN_WHITE_SLAB = registerBlockItem("ashpen_white_slab",
            WoodBlocks.ASHPEN_WHITE_SLAB);
    public static final RegistryObject<Item> ASHPEN_WHITE_FENCE = registerBlockItem("ashpen_white_fence",
            WoodBlocks.ASHPEN_WHITE_FENCE);
    public static final RegistryObject<Item> ASHPEN_WHITE_FENCE_GATE = registerBlockItem("ashpen_white_fence_gate",
            WoodBlocks.ASHPEN_WHITE_FENCE_GATE);
    public static final RegistryObject<Item> ASHPEN_WHITE_PRESSURE_PLATE = registerBlockItem(
            "ashpen_white_pressure_plate", WoodBlocks.ASHPEN_WHITE_PRESSURE_PLATE);
    public static final RegistryObject<Item> ASHPEN_WHITE_BUTTON = registerBlockItem("ashpen_white_button",
            WoodBlocks.ASHPEN_WHITE_BUTTON);
    public static final RegistryObject<Item> BLACK_ASHPEN_PLANKS = registerBlockItem("ashpen_black_planks",
            WoodBlocks.BLACK_ASHPEN_PLANKS);
    public static final RegistryObject<Item> BLACK_ASHPEN_STAIRS = registerBlockItem("ashpen_black_stairs",
            WoodBlocks.BLACK_ASHPEN_STAIRS);
    public static final RegistryObject<Item> BLACK_ASHPEN_SLAB = registerBlockItem("ashpen_black_slab",
            WoodBlocks.BLACK_ASHPEN_SLAB);
    public static final RegistryObject<Item> BLACK_ASHPEN_FENCE = registerBlockItem("ashpen_black_fence",
            WoodBlocks.BLACK_ASHPEN_FENCE);
    public static final RegistryObject<Item> BLACK_ASHPEN_FENCE_GATE = registerBlockItem("ashpen_black_fence_gate",
            WoodBlocks.BLACK_ASHPEN_FENCE_GATE);
    public static final RegistryObject<Item> BLACK_ASHPEN_PRESSURE_PLATE = registerBlockItem(
            "ashpen_black_pressure_plate", WoodBlocks.BLACK_ASHPEN_PRESSURE_PLATE);
    public static final RegistryObject<Item> BLACK_ASHPEN_BUTTON = registerBlockItem("ashpen_black_button",
            WoodBlocks.BLACK_ASHPEN_BUTTON);
    public static final RegistryObject<Item> BLUE_ASHPEN_PLANKS = registerBlockItem("ashpen_blue_planks",
            WoodBlocks.BLUE_ASHPEN_PLANKS);
    public static final RegistryObject<Item> BLUE_ASHPEN_STAIRS = registerBlockItem("ashpen_blue_stairs",
            WoodBlocks.BLUE_ASHPEN_STAIRS);
    public static final RegistryObject<Item> BLUE_ASHPEN_SLAB = registerBlockItem("ashpen_blue_slab",
            WoodBlocks.BLUE_ASHPEN_SLAB);
    public static final RegistryObject<Item> BLUE_ASHPEN_FENCE = registerBlockItem("ashpen_blue_fence",
            WoodBlocks.BLUE_ASHPEN_FENCE);
    public static final RegistryObject<Item> BLUE_ASHPEN_FENCE_GATE = registerBlockItem("ashpen_blue_fence_gate",
            WoodBlocks.BLUE_ASHPEN_FENCE_GATE);
    public static final RegistryObject<Item> BLUE_ASHPEN_PRESSURE_PLATE = registerBlockItem(
            "ashpen_blue_pressure_plate", WoodBlocks.BLUE_ASHPEN_PRESSURE_PLATE);
    public static final RegistryObject<Item> BLUE_ASHPEN_BUTTON = registerBlockItem("ashpen_blue_button",
            WoodBlocks.BLUE_ASHPEN_BUTTON);
    public static final RegistryObject<Item> BROWN_ASHPEN_PLANKS = registerBlockItem("ashpen_brown_planks",
            WoodBlocks.BROWN_ASHPEN_PLANKS);
    public static final RegistryObject<Item> BROWN_ASHPEN_STAIRS = registerBlockItem("ashpen_brown_stairs",
            WoodBlocks.BROWN_ASHPEN_STAIRS);
    public static final RegistryObject<Item> BROWN_ASHPEN_SLAB = registerBlockItem("ashpen_brown_slab",
            WoodBlocks.BROWN_ASHPEN_SLAB);
    public static final RegistryObject<Item> BROWN_ASHPEN_FENCE = registerBlockItem("ashpen_brown_fence",
            WoodBlocks.BROWN_ASHPEN_FENCE);
    public static final RegistryObject<Item> BROWN_ASHPEN_FENCE_GATE = registerBlockItem("ashpen_brown_fence_gate",
            WoodBlocks.BROWN_ASHPEN_FENCE_GATE);
    public static final RegistryObject<Item> BROWN_ASHPEN_PRESSURE_PLATE = registerBlockItem(
            "ashpen_brown_pressure_plate", WoodBlocks.BROWN_ASHPEN_PRESSURE_PLATE);
    public static final RegistryObject<Item> BROWN_ASHPEN_BUTTON = registerBlockItem("ashpen_brown_button",
            WoodBlocks.BROWN_ASHPEN_BUTTON);
    public static final RegistryObject<Item> CYAN_ASHPEN_PLANKS = registerBlockItem("ashpen_cyan_planks",
            WoodBlocks.CYAN_ASHPEN_PLANKS);
    public static final RegistryObject<Item> CYAN_ASHPEN_STAIRS = registerBlockItem("ashpen_cyan_stairs",
            WoodBlocks.CYAN_ASHPEN_STAIRS);
    public static final RegistryObject<Item> CYAN_ASHPEN_SLAB = registerBlockItem("ashpen_cyan_slab",
            WoodBlocks.CYAN_ASHPEN_SLAB);
    public static final RegistryObject<Item> CYAN_ASHPEN_FENCE = registerBlockItem("ashpen_cyan_fence",
            WoodBlocks.CYAN_ASHPEN_FENCE);
    public static final RegistryObject<Item> CYAN_ASHPEN_FENCE_GATE = registerBlockItem("ashpen_cyan_fence_gate",
            WoodBlocks.CYAN_ASHPEN_FENCE_GATE);
    public static final RegistryObject<Item> CYAN_ASHPEN_PRESSURE_PLATE = registerBlockItem(
            "ashpen_cyan_pressure_plate", WoodBlocks.CYAN_ASHPEN_PRESSURE_PLATE);
    public static final RegistryObject<Item> CYAN_ASHPEN_BUTTON = registerBlockItem("ashpen_cyan_button",
            WoodBlocks.CYAN_ASHPEN_BUTTON);
    public static final RegistryObject<Item> GRAY_ASHPEN_PLANKS = registerBlockItem("ashpen_gray_planks",
            WoodBlocks.GRAY_ASHPEN_PLANKS);
    public static final RegistryObject<Item> GRAY_ASHPEN_STAIRS = registerBlockItem("ashpen_gray_stairs",
            WoodBlocks.GRAY_ASHPEN_STAIRS);
    public static final RegistryObject<Item> GRAY_ASHPEN_SLAB = registerBlockItem("ashpen_gray_slab",
            WoodBlocks.GRAY_ASHPEN_SLAB);
    public static final RegistryObject<Item> GRAY_ASHPEN_FENCE = registerBlockItem("ashpen_gray_fence",
            WoodBlocks.GRAY_ASHPEN_FENCE);
    public static final RegistryObject<Item> GRAY_ASHPEN_FENCE_GATE = registerBlockItem("ashpen_gray_fence_gate",
            WoodBlocks.GRAY_ASHPEN_FENCE_GATE);
    public static final RegistryObject<Item> GRAY_ASHPEN_PRESSURE_PLATE = registerBlockItem(
            "ashpen_gray_pressure_plate", WoodBlocks.GRAY_ASHPEN_PRESSURE_PLATE);
    public static final RegistryObject<Item> GRAY_ASHPEN_BUTTON = registerBlockItem("ashpen_gray_button",
            WoodBlocks.GRAY_ASHPEN_BUTTON);
    public static final RegistryObject<Item> GREEN_ASHPEN_PLANKS = registerBlockItem("ashpen_green_planks",
            WoodBlocks.GREEN_ASHPEN_PLANKS);
    public static final RegistryObject<Item> GREEN_ASHPEN_STAIRS = registerBlockItem("ashpen_green_stairs",
            WoodBlocks.GREEN_ASHPEN_STAIRS);
    public static final RegistryObject<Item> GREEN_ASHPEN_SLAB = registerBlockItem("ashpen_green_slab",
            WoodBlocks.GREEN_ASHPEN_SLAB);
    public static final RegistryObject<Item> GREEN_ASHPEN_FENCE = registerBlockItem("ashpen_green_fence",
            WoodBlocks.GREEN_ASHPEN_FENCE);
    public static final RegistryObject<Item> GREEN_ASHPEN_FENCE_GATE = registerBlockItem("ashpen_green_fence_gate",
            WoodBlocks.GREEN_ASHPEN_FENCE_GATE);
    public static final RegistryObject<Item> GREEN_ASHPEN_PRESSURE_PLATE = registerBlockItem(
            "ashpen_green_pressure_plate", WoodBlocks.GREEN_ASHPEN_PRESSURE_PLATE);
    public static final RegistryObject<Item> GREEN_ASHPEN_BUTTON = registerBlockItem("ashpen_green_button",
            WoodBlocks.GREEN_ASHPEN_BUTTON);
    public static final RegistryObject<Item> LIGHT_BLUE_ASHPEN_PLANKS = registerBlockItem("ashpen_light_blue_planks",
            WoodBlocks.LIGHT_BLUE_ASHPEN_PLANKS);
    public static final RegistryObject<Item> LIGHT_BLUE_ASHPEN_STAIRS = registerBlockItem("ashpen_light_blue_stairs",
            WoodBlocks.LIGHT_BLUE_ASHPEN_STAIRS);
    public static final RegistryObject<Item> LIGHT_BLUE_ASHPEN_SLAB = registerBlockItem("ashpen_light_blue_slab",
            WoodBlocks.LIGHT_BLUE_ASHPEN_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_ASHPEN_FENCE = registerBlockItem("ashpen_light_blue_fence",
            WoodBlocks.LIGHT_BLUE_ASHPEN_FENCE);
    public static final RegistryObject<Item> LIGHT_BLUE_ASHPEN_FENCE_GATE = registerBlockItem(
            "ashpen_light_blue_fence_gate", WoodBlocks.LIGHT_BLUE_ASHPEN_FENCE_GATE);
    public static final RegistryObject<Item> LIGHT_BLUE_ASHPEN_PRESSURE_PLATE = registerBlockItem(
            "ashpen_light_blue_pressure_plate", WoodBlocks.LIGHT_BLUE_ASHPEN_PRESSURE_PLATE);
    public static final RegistryObject<Item> LIGHT_BLUE_ASHPEN_BUTTON = registerBlockItem("ashpen_light_blue_button",
            WoodBlocks.LIGHT_BLUE_ASHPEN_BUTTON);
    public static final RegistryObject<Item> LIGHT_GRAY_ASHPEN_PLANKS = registerBlockItem("ashpen_light_gray_planks",
            WoodBlocks.LIGHT_GRAY_ASHPEN_PLANKS);
    public static final RegistryObject<Item> LIGHT_GRAY_ASHPEN_STAIRS = registerBlockItem("ashpen_light_gray_stairs",
            WoodBlocks.LIGHT_GRAY_ASHPEN_STAIRS);
    public static final RegistryObject<Item> LIGHT_GRAY_ASHPEN_SLAB = registerBlockItem("ashpen_light_gray_slab",
            WoodBlocks.LIGHT_GRAY_ASHPEN_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_ASHPEN_FENCE = registerBlockItem("ashpen_light_gray_fence",
            WoodBlocks.LIGHT_GRAY_ASHPEN_FENCE);
    public static final RegistryObject<Item> LIGHT_GRAY_ASHPEN_FENCE_GATE = registerBlockItem(
            "ashpen_light_gray_fence_gate", WoodBlocks.LIGHT_GRAY_ASHPEN_FENCE_GATE);
    public static final RegistryObject<Item> LIGHT_GRAY_ASHPEN_PRESSURE_PLATE = registerBlockItem(
            "ashpen_light_gray_pressure_plate", WoodBlocks.LIGHT_GRAY_ASHPEN_PRESSURE_PLATE);
    public static final RegistryObject<Item> LIGHT_GRAY_ASHPEN_BUTTON = registerBlockItem("ashpen_light_gray_button",
            WoodBlocks.LIGHT_GRAY_ASHPEN_BUTTON);

    public static final RegistryObject<Item> LIME_ASHPEN_PLANKS = registerBlockItem("ashpen_lime_planks",
            WoodBlocks.LIME_ASHPEN_PLANKS);
    public static final RegistryObject<Item> LIME_ASHPEN_STAIRS = registerBlockItem("ashpen_lime_stairs",
            WoodBlocks.LIME_ASHPEN_STAIRS);
    public static final RegistryObject<Item> LIME_ASHPEN_SLAB = registerBlockItem("ashpen_lime_slab",
            WoodBlocks.LIME_ASHPEN_SLAB);
    public static final RegistryObject<Item> LIME_ASHPEN_FENCE = registerBlockItem("ashpen_lime_fence",
            WoodBlocks.LIME_ASHPEN_FENCE);
    public static final RegistryObject<Item> LIME_ASHPEN_FENCE_GATE = registerBlockItem("ashpen_lime_fence_gate",
            WoodBlocks.LIME_ASHPEN_FENCE_GATE);
    public static final RegistryObject<Item> LIME_ASHPEN_PRESSURE_PLATE = registerBlockItem(
            "ashpen_lime_pressure_plate", WoodBlocks.LIME_ASHPEN_PRESSURE_PLATE);
    public static final RegistryObject<Item> LIME_ASHPEN_BUTTON = registerBlockItem("ashpen_lime_button",
            WoodBlocks.LIME_ASHPEN_BUTTON);
    public static final RegistryObject<Item> MAGENTA_ASHPEN_PLANKS = registerBlockItem("ashpen_magenta_planks",
            WoodBlocks.MAGENTA_ASHPEN_PLANKS);
    public static final RegistryObject<Item> MAGENTA_ASHPEN_STAIRS = registerBlockItem("ashpen_magenta_stairs",
            WoodBlocks.MAGENTA_ASHPEN_STAIRS);
    public static final RegistryObject<Item> MAGENTA_ASHPEN_SLAB = registerBlockItem("ashpen_magenta_slab",
            WoodBlocks.MAGENTA_ASHPEN_SLAB);
    public static final RegistryObject<Item> MAGENTA_ASHPEN_FENCE = registerBlockItem("ashpen_magenta_fence",
            WoodBlocks.MAGENTA_ASHPEN_FENCE);
    public static final RegistryObject<Item> MAGENTA_ASHPEN_FENCE_GATE = registerBlockItem("ashpen_magenta_fence_gate",
            WoodBlocks.MAGENTA_ASHPEN_FENCE_GATE);
    public static final RegistryObject<Item> MAGENTA_ASHPEN_PRESSURE_PLATE = registerBlockItem(
            "ashpen_magenta_pressure_plate", WoodBlocks.MAGENTA_ASHPEN_PRESSURE_PLATE);
    public static final RegistryObject<Item> MAGENTA_ASHPEN_BUTTON = registerBlockItem("ashpen_magenta_button",
            WoodBlocks.MAGENTA_ASHPEN_BUTTON);
    public static final RegistryObject<Item> ORANGE_ASHPEN_PLANKS = registerBlockItem("ashpen_orange_planks",
            WoodBlocks.ORANGE_ASHPEN_PLANKS);
    public static final RegistryObject<Item> ORANGE_ASHPEN_STAIRS = registerBlockItem("ashpen_orange_stairs",
            WoodBlocks.ORANGE_ASHPEN_STAIRS);
    public static final RegistryObject<Item> ORANGE_ASHPEN_SLAB = registerBlockItem("ashpen_orange_slab",
            WoodBlocks.ORANGE_ASHPEN_SLAB);
    public static final RegistryObject<Item> ORANGE_ASHPEN_FENCE = registerBlockItem("ashpen_orange_fence",
            WoodBlocks.ORANGE_ASHPEN_FENCE);
    public static final RegistryObject<Item> ORANGE_ASHPEN_FENCE_GATE = registerBlockItem("ashpen_orange_fence_gate",
            WoodBlocks.ORANGE_ASHPEN_FENCE_GATE);
    public static final RegistryObject<Item> ORANGE_ASHPEN_PRESSURE_PLATE = registerBlockItem(
            "ashpen_orange_pressure_plate", WoodBlocks.ORANGE_ASHPEN_PRESSURE_PLATE);
    public static final RegistryObject<Item> ORANGE_ASHPEN_BUTTON = registerBlockItem("ashpen_orange_button",
            WoodBlocks.ORANGE_ASHPEN_BUTTON);
    public static final RegistryObject<Item> PINK_ASHPEN_PLANKS = registerBlockItem("ashpen_pink_planks",
            WoodBlocks.PINK_ASHPEN_PLANKS);
    public static final RegistryObject<Item> PINK_ASHPEN_STAIRS = registerBlockItem("ashpen_pink_stairs",
            WoodBlocks.PINK_ASHPEN_STAIRS);
    public static final RegistryObject<Item> PINK_ASHPEN_SLAB = registerBlockItem("ashpen_pink_slab",
            WoodBlocks.PINK_ASHPEN_SLAB);
    public static final RegistryObject<Item> PINK_ASHPEN_FENCE = registerBlockItem("ashpen_pink_fence",
            WoodBlocks.PINK_ASHPEN_FENCE);
    public static final RegistryObject<Item> PINK_ASHPEN_FENCE_GATE = registerBlockItem("ashpen_pink_fence_gate",
            WoodBlocks.PINK_ASHPEN_FENCE_GATE);
    public static final RegistryObject<Item> PINK_ASHPEN_PRESSURE_PLATE = registerBlockItem(
            "ashpen_pink_pressure_plate", WoodBlocks.PINK_ASHPEN_PRESSURE_PLATE);
    public static final RegistryObject<Item> PINK_ASHPEN_BUTTON = registerBlockItem("ashpen_pink_button",
            WoodBlocks.PINK_ASHPEN_BUTTON);
    public static final RegistryObject<Item> PURPLE_ASHPEN_PLANKS = registerBlockItem("ashpen_purple_planks",
            WoodBlocks.PURPLE_ASHPEN_PLANKS);
    public static final RegistryObject<Item> PURPLE_ASHPEN_STAIRS = registerBlockItem("ashpen_purple_stairs",
            WoodBlocks.PURPLE_ASHPEN_STAIRS);
    public static final RegistryObject<Item> PURPLE_ASHPEN_SLAB = registerBlockItem("ashpen_purple_slab",
            WoodBlocks.PURPLE_ASHPEN_SLAB);
    public static final RegistryObject<Item> PURPLE_ASHPEN_FENCE = registerBlockItem("ashpen_purple_fence",
            WoodBlocks.PURPLE_ASHPEN_FENCE);
    public static final RegistryObject<Item> PURPLE_ASHPEN_FENCE_GATE = registerBlockItem("ashpen_purple_fence_gate",
            WoodBlocks.PURPLE_ASHPEN_FENCE_GATE);
    public static final RegistryObject<Item> PURPLE_ASHPEN_PRESSURE_PLATE = registerBlockItem(
            "ashpen_purple_pressure_plate", WoodBlocks.PURPLE_ASHPEN_PRESSURE_PLATE);
    public static final RegistryObject<Item> PURPLE_ASHPEN_BUTTON = registerBlockItem("ashpen_purple_button",
            WoodBlocks.PURPLE_ASHPEN_BUTTON);
    public static final RegistryObject<Item> RED_ASHPEN_PLANKS = registerBlockItem("ashpen_red_planks",
            WoodBlocks.RED_ASHPEN_PLANKS);
    public static final RegistryObject<Item> RED_ASHPEN_STAIRS = registerBlockItem("ashpen_red_stairs",
            WoodBlocks.RED_ASHPEN_STAIRS);
    public static final RegistryObject<Item> RED_ASHPEN_SLAB = registerBlockItem("ashpen_red_slab",
            WoodBlocks.RED_ASHPEN_SLAB);
    public static final RegistryObject<Item> RED_ASHPEN_FENCE = registerBlockItem("ashpen_red_fence",
            WoodBlocks.RED_ASHPEN_FENCE);
    public static final RegistryObject<Item> RED_ASHPEN_FENCE_GATE = registerBlockItem("ashpen_red_fence_gate",
            WoodBlocks.RED_ASHPEN_FENCE_GATE);
    public static final RegistryObject<Item> RED_ASHPEN_PRESSURE_PLATE = registerBlockItem("ashpen_red_pressure_plate",
            WoodBlocks.RED_ASHPEN_PRESSURE_PLATE);
    public static final RegistryObject<Item> RED_ASHPEN_BUTTON = registerBlockItem("ashpen_red_button",
            WoodBlocks.RED_ASHPEN_BUTTON);
    public static final RegistryObject<Item> YELLOW_ASHPEN_PLANKS = registerBlockItem("ashpen_yellow_planks",
            WoodBlocks.YELLOW_ASHPEN_PLANKS);
    public static final RegistryObject<Item> YELLOW_ASHPEN_STAIRS = registerBlockItem("ashpen_yellow_stairs",
            WoodBlocks.YELLOW_ASHPEN_STAIRS);
    public static final RegistryObject<Item> YELLOW_ASHPEN_SLAB = registerBlockItem("ashpen_yellow_slab",
            WoodBlocks.YELLOW_ASHPEN_SLAB);
    public static final RegistryObject<Item> YELLOW_ASHPEN_FENCE = registerBlockItem("ashpen_yellow_fence",
            WoodBlocks.YELLOW_ASHPEN_FENCE);
    public static final RegistryObject<Item> YELLOW_ASHPEN_FENCE_GATE = registerBlockItem("ashpen_yellow_fence_gate",
            WoodBlocks.YELLOW_ASHPEN_FENCE_GATE);
    public static final RegistryObject<Item> YELLOW_ASHPEN_PRESSURE_PLATE = registerBlockItem(
            "ashpen_yellow_pressure_plate", WoodBlocks.YELLOW_ASHPEN_PRESSURE_PLATE);
    public static final RegistryObject<Item> YELLOW_ASHPEN_BUTTON = registerBlockItem("ashpen_yellow_button",
            WoodBlocks.YELLOW_ASHPEN_BUTTON);
    public static final RegistryObject<Item> RED_ROSE_VINES = registerBlockItem("red_rose_vines",
            WoodBlocks.RED_ROSE_VINES);
    public static final RegistryObject<Item> BLACK_ROSE_VINES = registerBlockItem("black_rose_vines",
            WoodBlocks.BLACK_ROSE_VINES);
    public static final RegistryObject<Item> BLUE_ROSE_VINES = registerBlockItem("blue_rose_vines",
            WoodBlocks.BLUE_ROSE_VINES);
    public static final RegistryObject<Item> WHITE_ROSE_VINES = registerBlockItem("white_rose_vines",
            WoodBlocks.WHITE_ROSE_VINES);
    public static final RegistryObject<Item> RED_MONETS = registerBlockItem("red_monets", WoodBlocks.RED_MONETS);
    public static final RegistryObject<Item> BLUE_MONETS = registerBlockItem("blue_monets", WoodBlocks.BLUE_MONETS);
    public static final RegistryObject<Item> PURPLE_MONETS = registerBlockItem("purple_monets",
            WoodBlocks.PURPLE_MONETS);
    public static final RegistryObject<Item> LIGHT_BLUE_MONETS = registerBlockItem("light_blue_monets",
            WoodBlocks.LIGHT_BLUE_MONETS);
    public static final RegistryObject<Item> PINK_MONETS = registerBlockItem("pink_monets", WoodBlocks.PINK_MONETS);
    public static final RegistryObject<Item> YELLOW_MONETS = registerBlockItem("yellow_monets",
            DecorBlocks.YELLOW_MONETS);
    public static final RegistryObject<Item> CLOVER = registerBlockItem("clover", DecorBlocks.CLOVER);
    public static final RegistryObject<Item> RED_PETAL = registerBlockItem("red_petal", DecorBlocks.RED_PETAL);
    public static final RegistryObject<Item> BLUE_PETAL = registerBlockItem("blue_petal", DecorBlocks.BLUE_PETAL);
    public static final RegistryObject<Item> ORANGE_PETAL = registerBlockItem("orange_petal", DecorBlocks.ORANGE_PETAL);
    public static final RegistryObject<Item> PINK_PETAL = registerBlockItem("pink_petal", DecorBlocks.PINK_PETAL);
    public static final RegistryObject<Item> PURPLE_PETAL = registerBlockItem("purple_petal", DecorBlocks.PURPLE_PETAL);
    public static final RegistryObject<Item> RED_SPORE_BLOSSOM = registerBlockItem("red_spore_blossom",
            DecorBlocks.RED_SPORE_BLOSSOM);
    public static final RegistryObject<Item> CYAN_SPORE_BLOSSOM = registerBlockItem("cyan_spore_blossom",
            DecorBlocks.CYAN_SPORE_BLOSSOM);
    public static final RegistryObject<Item> BLUE_SPORE_BLOSSOM = registerBlockItem("blue_spore_blossom",
            DecorBlocks.BLUE_SPORE_BLOSSOM);
    public static final RegistryObject<Item> PURPLE_SPORE_BLOSSOM = registerBlockItem("purple_spore_blossom",
            DecorBlocks.PURPLE_SPORE_BLOSSOM);
    public static final RegistryObject<Item> ORANGE_SPORE_BLOSSOM = registerBlockItem("orange_spore_blossom",
            DecorBlocks.ORANGE_SPORE_BLOSSOM);
    public static final RegistryObject<Item> BIG_CANDLE = registerBlockItem("big_candle", DecorBlocks.BIG_CANDLE);
    public static final RegistryObject<Item> BIG_WHITE_CANDLE = registerBlockItem("big_white_candle",
            DecorBlocks.BIG_WHITE_CANDLE);
    public static final RegistryObject<Item> BIG_ORANGE_CANDLE = registerBlockItem("big_orange_candle",
            DecorBlocks.BIG_ORANGE_CANDLE);
    public static final RegistryObject<Item> BIG_MAGENTA_CANDLE = registerBlockItem("big_magenta_candle",
            DecorBlocks.BIG_MAGENTA_CANDLE);
    public static final RegistryObject<Item> BIG_LIGHT_BLUE_CANDLE = registerBlockItem("big_light_blue_candle",
            DecorBlocks.BIG_LIGHT_BLUE_CANDLE);
    public static final RegistryObject<Item> BIG_YELLOW_CANDLE = registerBlockItem("big_yellow_candle",
            DecorBlocks.BIG_YELLOW_CANDLE);
    public static final RegistryObject<Item> BIG_LIME_CANDLE = registerBlockItem("big_lime_candle",
            DecorBlocks.BIG_LIME_CANDLE);
    public static final RegistryObject<Item> BIG_PINK_CANDLE = registerBlockItem("big_pink_candle",
            DecorBlocks.BIG_PINK_CANDLE);
    public static final RegistryObject<Item> BIG_GRAY_CANDLE = registerBlockItem("big_gray_candle",
            DecorBlocks.BIG_GRAY_CANDLE);
    public static final RegistryObject<Item> BIG_LIGHT_GRAY_CANDLE = registerBlockItem("big_light_gray_candle",
            DecorBlocks.BIG_LIGHT_GRAY_CANDLE);
    public static final RegistryObject<Item> BIG_CYAN_CANDLE = registerBlockItem("big_cyan_candle",
            DecorBlocks.BIG_CYAN_CANDLE);
    public static final RegistryObject<Item> BIG_PURPLE_CANDLE = registerBlockItem("big_purple_candle",
            DecorBlocks.BIG_PURPLE_CANDLE);
    public static final RegistryObject<Item> BIG_BLUE_CANDLE = registerBlockItem("big_blue_candle",
            DecorBlocks.BIG_BLUE_CANDLE);
    public static final RegistryObject<Item> BIG_BROWN_CANDLE = registerBlockItem("big_brown_candle",
            DecorBlocks.BIG_BROWN_CANDLE);
    public static final RegistryObject<Item> BIG_GREEN_CANDLE = registerBlockItem("big_green_candle",
            DecorBlocks.BIG_GREEN_CANDLE);
    public static final RegistryObject<Item> BIG_RED_CANDLE = registerBlockItem("big_red_candle",
            DecorBlocks.BIG_RED_CANDLE);
    public static final RegistryObject<Item> BIG_BLACK_CANDLE = registerBlockItem("big_black_candle",
            DecorBlocks.BIG_BLACK_CANDLE);
    public static final RegistryObject<Item> BIG_AMETHYST_CANDLE = registerBlockItem("big_amethyst_candle",
            DecorBlocks.BIG_AMETHYST_CANDLE);
    public static final RegistryObject<Item> BIG_SCULK_CANDLE = registerBlockItem("big_sculk_candle",
            DecorBlocks.BIG_SCULK_CANDLE);
    public static final RegistryObject<Item> WHITE_ORNAMENT = registerBlockItem("white_ornament",
            DecorBlocks.WHITE_ORNAMENT);
    public static final RegistryObject<Item> ORANGE_ORNAMENT = registerBlockItem("orange_ornament",
            DecorBlocks.ORANGE_ORNAMENT);
    public static final RegistryObject<Item> MAGENTA_ORNAMENT = registerBlockItem("magenta_ornament",
            DecorBlocks.MAGENTA_ORNAMENT);
    public static final RegistryObject<Item> LIGHT_BLUE_ORNAMENT = registerBlockItem("light_blue_ornament",
            DecorBlocks.LIGHT_BLUE_ORNAMENT);
    public static final RegistryObject<Item> YELLOW_ORNAMENT = registerBlockItem("yellow_ornament",
            DecorBlocks.YELLOW_ORNAMENT);
    public static final RegistryObject<Item> LIME_ORNAMENT = registerBlockItem("lime_ornament",
            DecorBlocks.LIME_ORNAMENT);
    public static final RegistryObject<Item> PINK_ORNAMENT = registerBlockItem("pink_ornament",
            DecorBlocks.PINK_ORNAMENT);
    public static final RegistryObject<Item> GRAY_ORNAMENT = registerBlockItem("gray_ornament",
            DecorBlocks.GRAY_ORNAMENT);
    public static final RegistryObject<Item> LIGHT_GRAY_ORNAMENT = registerBlockItem("light_gray_ornament",
            DecorBlocks.LIGHT_GRAY_ORNAMENT);
    public static final RegistryObject<Item> CYAN_ORNAMENT = registerBlockItem("cyan_ornament",
            DecorBlocks.CYAN_ORNAMENT);
    public static final RegistryObject<Item> PURPLE_ORNAMENT = registerBlockItem("purple_ornament",
            DecorBlocks.PURPLE_ORNAMENT);
    public static final RegistryObject<Item> BLUE_ORNAMENT = registerBlockItem("blue_ornament",
            DecorBlocks.BLUE_ORNAMENT);
    public static final RegistryObject<Item> BROWN_ORNAMENT = registerBlockItem("brown_ornament",
            DecorBlocks.BROWN_ORNAMENT);
    public static final RegistryObject<Item> GREEN_ORNAMENT = registerBlockItem("green_ornament",
            DecorBlocks.GREEN_ORNAMENT);
    public static final RegistryObject<Item> RED_ORNAMENT = registerBlockItem("red_ornament", DecorBlocks.RED_ORNAMENT);
    public static final RegistryObject<Item> BLACK_ORNAMENT = registerBlockItem("black_ornament",
            DecorBlocks.BLACK_ORNAMENT);
    public static final RegistryObject<Item> GLASS_ORNAMENT = registerBlockItem("glass_ornament",
            DecorBlocks.GLASS_ORNAMENT);
    public static final RegistryObject<Item> TINTED_GLASS_ORNAMENT = registerBlockItem("tinted_glass_ornament",
            DecorBlocks.TINTED_GLASS_ORNAMENT);
    public static final RegistryObject<Item> WHITE_STRING_LIGHT = registerBlockItem("white_string_light",
            DecorBlocks.WHITE_STRING_LIGHT);
    public static final RegistryObject<Item> ORANGE_STRING_LIGHT = registerBlockItem("orange_string_light",
            DecorBlocks.ORANGE_STRING_LIGHT);
    public static final RegistryObject<Item> MAGENTA_STRING_LIGHT = registerBlockItem("magenta_string_light",
            DecorBlocks.MAGENTA_STRING_LIGHT);
    public static final RegistryObject<Item> LIGHT_BLUE_STRING_LIGHT = registerBlockItem("light_blue_string_light",
            DecorBlocks.LIGHT_BLUE_STRING_LIGHT);
    public static final RegistryObject<Item> YELLOW_STRING_LIGHT = registerBlockItem("yellow_string_light",
            DecorBlocks.YELLOW_STRING_LIGHT);
    public static final RegistryObject<Item> LIME_STRING_LIGHT = registerBlockItem("lime_string_light",
            DecorBlocks.LIME_STRING_LIGHT);
    public static final RegistryObject<Item> PINK_STRING_LIGHT = registerBlockItem("pink_string_light",
            DecorBlocks.PINK_STRING_LIGHT);
    public static final RegistryObject<Item> GRAY_STRING_LIGHT = registerBlockItem("gray_string_light",
            DecorBlocks.GRAY_STRING_LIGHT);
    public static final RegistryObject<Item> LIGHT_GRAY_STRING_LIGHT = registerBlockItem("light_gray_string_light",
            DecorBlocks.LIGHT_GRAY_STRING_LIGHT);
    public static final RegistryObject<Item> CYAN_STRING_LIGHT = registerBlockItem("cyan_string_light",
            DecorBlocks.CYAN_STRING_LIGHT);
    public static final RegistryObject<Item> PURPLE_STRING_LIGHT = registerBlockItem("purple_string_light",
            DecorBlocks.PURPLE_STRING_LIGHT);
    public static final RegistryObject<Item> BLUE_STRING_LIGHT = registerBlockItem("blue_string_light",
            DecorBlocks.BLUE_STRING_LIGHT);
    public static final RegistryObject<Item> BROWN_STRING_LIGHT = registerBlockItem("brown_string_light",
            DecorBlocks.BROWN_STRING_LIGHT);
    public static final RegistryObject<Item> GREEN_STRING_LIGHT = registerBlockItem("green_string_light",
            DecorBlocks.GREEN_STRING_LIGHT);
    public static final RegistryObject<Item> RED_STRING_LIGHT = registerBlockItem("red_string_light",
            DecorBlocks.RED_STRING_LIGHT);
    public static final RegistryObject<Item> BLACK_STRING_LIGHT = registerBlockItem("black_string_light",
            DecorBlocks.BLACK_STRING_LIGHT);
    public static final RegistryObject<Item> MULTICOLOR_STRING_LIGHT = registerBlockItem("multicolor_string_light",
            DecorBlocks.MULTICOLOR_STRING_LIGHT);
    public static final RegistryObject<Item> WHITE_STAR = registerBlockItem("white_star", DecorBlocks.WHITE_STAR);
    public static final RegistryObject<Item> ORANGE_STAR = registerBlockItem("orange_star", DecorBlocks.ORANGE_STAR);
    public static final RegistryObject<Item> MAGENTA_STAR = registerBlockItem("magenta_star", DecorBlocks.MAGENTA_STAR);
    public static final RegistryObject<Item> LIGHT_BLUE_STAR = registerBlockItem("light_blue_star",
            DecorBlocks.LIGHT_BLUE_STAR);
    public static final RegistryObject<Item> YELLOW_STAR = registerBlockItem("yellow_star", DecorBlocks.YELLOW_STAR);
    public static final RegistryObject<Item> LIME_STAR = registerBlockItem("lime_star", DecorBlocks.LIME_STAR);
    public static final RegistryObject<Item> PINK_STAR = registerBlockItem("pink_star", DecorBlocks.PINK_STAR);
    public static final RegistryObject<Item> GRAY_STAR = registerBlockItem("gray_star", DecorBlocks.GRAY_STAR);
    public static final RegistryObject<Item> LIGHT_GRAY_STAR = registerBlockItem("light_gray_star",
            DecorBlocks.LIGHT_GRAY_STAR);
    public static final RegistryObject<Item> CYAN_STAR = registerBlockItem("cyan_star", DecorBlocks.CYAN_STAR);
    public static final RegistryObject<Item> PURPLE_STAR = registerBlockItem("purple_star", DecorBlocks.PURPLE_STAR);
    public static final RegistryObject<Item> BLUE_STAR = registerBlockItem("blue_star", DecorBlocks.BLUE_STAR);
    public static final RegistryObject<Item> BROWN_STAR = registerBlockItem("brown_star", DecorBlocks.BROWN_STAR);
    public static final RegistryObject<Item> GREEN_STAR = registerBlockItem("green_star", DecorBlocks.GREEN_STAR);
    public static final RegistryObject<Item> RED_STAR = registerBlockItem("red_star", DecorBlocks.RED_STAR);
    public static final RegistryObject<Item> BLACK_STAR = registerBlockItem("black_star", DecorBlocks.BLACK_STAR);
    public static final RegistryObject<Item> GLOW_STAR = registerBlockItem("glow_star", DecorBlocks.GLOW_STAR);

    public static final RegistryObject<Item> SNOWY_LEAVES = ITEMS.register(
            "snowy_leaves",
            () -> new BlockItem(DecorBlocks.SNOWY_LEAVES.get(), new Item.Properties()));
    public static final RegistryObject<Item> SNOWY_OAK_LEAVES = registerBlockItem("snowy_oak_leaves",
            DecorBlocks.SNOWY_OAK_LEAVES);
    public static final RegistryObject<Item> SNOWY_SPRUCE_LEAVES = registerBlockItem("snowy_spruce_leaves",
            DecorBlocks.SNOWY_SPRUCE_LEAVES);
    public static final RegistryObject<Item> SNOWY_BIRCH_LEAVES = registerBlockItem("snowy_birch_leaves",
            DecorBlocks.SNOWY_BIRCH_LEAVES);
    public static final RegistryObject<Item> SNOWY_JUNGLE_LEAVES = registerBlockItem("snowy_jungle_leaves",
            DecorBlocks.SNOWY_JUNGLE_LEAVES);
    public static final RegistryObject<Item> SNOWY_ACACIA_LEAVES = registerBlockItem("snowy_acacia_leaves",
            DecorBlocks.SNOWY_ACACIA_LEAVES);
    public static final RegistryObject<Item> SNOWY_DARK_OAK_LEAVES = registerBlockItem("snowy_dark_oak_leaves",
            DecorBlocks.SNOWY_DARK_OAK_LEAVES);
    public static final RegistryObject<Item> SNOWY_MANGROVE_LEAVES = registerBlockItem("snowy_mangrove_leaves",
            DecorBlocks.SNOWY_MANGROVE_LEAVES);
    public static final RegistryObject<Item> SNOWY_AZALEA_LEAVES = registerBlockItem("snowy_azalea_leaves",
            DecorBlocks.SNOWY_AZALEA_LEAVES);
    public static final RegistryObject<Item> SNOWY_FLOWERING_AZALEA_LEAVES = registerBlockItem(
            "snowy_flowering_azalea_leaves", DecorBlocks.SNOWY_FLOWERING_AZALEA_LEAVES);
    public static final RegistryObject<Item> SNOWY_CHERRY_LEAVES = registerBlockItem("snowy_cherry_leaves",
            DecorBlocks.SNOWY_CHERRY_LEAVES);
    public static final RegistryObject<Item> SNOWY_PALE_OAK_LEAVES = registerBlockItem("snowy_pale_oak_leaves",
            DecorBlocks.SNOWY_PALE_OAK_LEAVES);
    public static final RegistryObject<Item> SNOWY_ORANGE_POPLAR_LEAVES = registerBlockItem(
            "snowy_orange_poplar_leaves", CopperBlocks.SNOWY_ORANGE_POPLAR_LEAVES);
    public static final RegistryObject<Item> SNOWY_RED_POPLAR_LEAVES = registerBlockItem("snowy_red_poplar_leaves",
            CopperBlocks.SNOWY_RED_POPLAR_LEAVES);
    public static final RegistryObject<Item> SNOWY_YELLOW_POPLAR_LEAVES = registerBlockItem(
            "snowy_yellow_poplar_leaves", CopperBlocks.SNOWY_YELLOW_POPLAR_LEAVES);
    public static final RegistryObject<Item> SNOWY_SHORT_GRASS = registerBlockItem("snowy_short_grass",
            DecorBlocks.SNOWY_SHORT_GRASS);
    public static final RegistryObject<Item> SNOWY_TALL_GRASS = registerBlockItem("snowy_tall_grass",
            DecorBlocks.SNOWY_TALL_GRASS);
    public static final RegistryObject<Item> SNOWY_FERN = registerBlockItem("snowy_fern", DecorBlocks.SNOWY_FERN);
    public static final RegistryObject<Item> SNOWY_LARGE_FERN = registerBlockItem("snowy_large_fern",
            DecorBlocks.SNOWY_LARGE_FERN);
    public static final RegistryObject<Item> SNOWY_BUSH = registerBlockItem("snowy_bush", DecorBlocks.SNOWY_BUSH);
    public static final RegistryObject<Item> SNOW_BRICKS = registerBlockItem("snow_bricks", DecorBlocks.SNOW_BRICKS);
    public static final RegistryObject<Item> SNOW_BRICKS_STAIRS = registerBlockItem("snow_bricks_stairs",
            DecorBlocks.SNOW_BRICKS_STAIRS);
    public static final RegistryObject<Item> SNOW_BRICKS_SLAB = registerBlockItem("snow_bricks_slab",
            DecorBlocks.SNOW_BRICKS_SLAB);
    public static final RegistryObject<Item> SNOW_BRICKS_WALL = registerBlockItem("snow_bricks_wall",
            DecorBlocks.SNOW_BRICKS_WALL);
    public static final RegistryObject<Item> SNOWY_GRASS_BLOCK = registerBlockItem("snowy_grass_block",
            DecorBlocks.SNOWY_GRASS_BLOCK);
    public static final RegistryObject<Item> SNOWY_GRASS_BLOCK_STAIRS = registerBlockItem("snowy_grass_block_stairs",
            DecorBlocks.SNOWY_GRASS_BLOCK_STAIRS);
    public static final RegistryObject<Item> SNOWY_GRASS_BLOCK_SLAB = registerBlockItem("snowy_grass_block_slab",
            DecorBlocks.SNOWY_GRASS_BLOCK_SLAB);
    public static final RegistryObject<Item> ICICLE = registerBlockItem("icicle", DecorBlocks.ICICLE);
    public static final RegistryObject<Item> ICICLE_BLOCK = registerBlockItem("icicle_block", DecorBlocks.ICICLE_BLOCK);
    public static final RegistryObject<Item> PACKED_ICICLE_BLOCK = registerBlockItem("packed_icicle_block",
            DecorBlocks.PACKED_ICICLE_BLOCK);
    public static final RegistryObject<Item> ICE_CRYSTAL = registerBlockItem("ice_crystal", DecorBlocks.ICE_CRYSTAL);

    public static final RegistryObject<Item> COPPER_NUGGET = ITEMS.register(
            "copper_nugget",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB)));

    public static final RegistryObject<Item> STEEL_INGOT = ITEMS.register(
            "steel_ingot",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB)));
    public static final RegistryObject<Item> STEEL_NUGGET = ITEMS.register(
            "steel_nugget",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB)));

    public static final RegistryObject<Item> WRENCH = ITEMS.register(
            "wrench",
            () -> new WrenchItem(new Item.Properties().stacksTo(1).tab(ModCreativeModeTab.BUILDSCAPE_TAB)));

    public static final RegistryObject<Item> IRON_HAMMER = ITEMS.register(
            "iron_hammer",
            () -> new HammerItem(HammerItem.HammerTier.IRON,
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB)));

    public static final RegistryObject<Item> DIAMOND_HAMMER = ITEMS.register(
            "diamond_hammer",
            () -> new HammerItem(HammerItem.HammerTier.DIAMOND,
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB)));

    public static final RegistryObject<Item> NETHERITE_HAMMER = ITEMS.register(
            "netherite_hammer",
            () -> new HammerItem(HammerItem.HammerTier.NETHERITE,
                    new Item.Properties().fireResistant().tab(ModCreativeModeTab.BUILDSCAPE_TAB)));

    public static final RegistryObject<Item> COPPER_BIOME_BRUSH = ITEMS.register(
            "copper_biome_brush",
            () -> new BiomeBrushItem(BiomeBrushItem.BiomeBrushTier.COPPER,
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB)));

    public static final RegistryObject<Item> DIAMOND_BIOME_BRUSH = ITEMS.register(
            "diamond_biome_brush",
            () -> new BiomeBrushItem(BiomeBrushItem.BiomeBrushTier.DIAMOND,
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB)));

    public static final RegistryObject<Item> NETHERITE_BIOME_BRUSH = ITEMS.register(
            "netherite_biome_brush",
            () -> new BiomeBrushItem(BiomeBrushItem.BiomeBrushTier.NETHERITE,
                    new Item.Properties().fireResistant().tab(ModCreativeModeTab.BUILDSCAPE_TAB)));

    public static final RegistryObject<Item> BUILDERS_WORKBENCH = ITEMS.register(
            "builders_workbench",
            () -> new BlockItem(
                    SandstoneBlocks.BUILDERS_WORKBENCH.get(),
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(64)));

    public static final RegistryObject<Item> BUILDERS_POUCH = ITEMS.register(
            "builders_pouch",
            () -> new BuildersPouchItem(new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB)));
    public static final RegistryObject<Item> SNOW_STAIRS = registerBlockItem("snow_stairs", DecorBlocks.SNOW_STAIRS);
    public static final RegistryObject<Item> SNOW_SLAB = registerBlockItem("snow_slab", DecorBlocks.SNOW_SLAB);
    public static final RegistryObject<Item> MANGROVE_LOG = registerBlockItem("mangrove_log", DecorBlocks.MANGROVE_LOG);
    public static final RegistryObject<Item> STRIPPED_MANGROVE_LOG = registerBlockItem("stripped_mangrove_log",
            DecorBlocks.STRIPPED_MANGROVE_LOG);
    public static final RegistryObject<Item> MANGROVE_WOOD = registerBlockItem("mangrove_wood",
            DecorBlocks.MANGROVE_WOOD);
    public static final RegistryObject<Item> STRIPPED_MANGROVE_WOOD = registerBlockItem("stripped_mangrove_wood",
            DecorBlocks.STRIPPED_MANGROVE_WOOD);
    public static final RegistryObject<Item> MANGROVE_LEAVES = registerBlockItem("mangrove_leaves",
            DecorBlocks.MANGROVE_LEAVES);
    public static final RegistryObject<Item> MANGROVE_ROOTS = registerBlockItem("mangrove_roots",
            DecorBlocks.MANGROVE_ROOTS);
    public static final RegistryObject<Item> MUDDY_MANGROVE_ROOTS = registerBlockItem("muddy_mangrove_roots",
            DecorBlocks.MUDDY_MANGROVE_ROOTS);
    public static final RegistryObject<Item> MANGROVE_PROPAGULE = registerBlockItem("mangrove_propagule",
            DecorBlocks.MANGROVE_PROPAGULE);
    public static final RegistryObject<Item> MANGROVE_PLANKS = registerBlockItem("mangrove_planks",
            DecorBlocks.MANGROVE_PLANKS);
    public static final RegistryObject<Item> MANGROVE_SLAB = registerBlockItem("mangrove_slab",
            DecorBlocks.MANGROVE_SLAB);
    public static final RegistryObject<Item> MANGROVE_STAIRS = registerBlockItem("mangrove_stairs",
            DecorBlocks.MANGROVE_STAIRS);
    public static final RegistryObject<Item> MANGROVE_FENCE = registerBlockItem("mangrove_fence",
            DecorBlocks.MANGROVE_FENCE);
    public static final RegistryObject<Item> MANGROVE_FENCE_GATE = registerBlockItem("mangrove_fence_gate",
            DecorBlocks.MANGROVE_FENCE_GATE);
    public static final RegistryObject<Item> MANGROVE_DOOR = registerBlockItem("mangrove_door",
            DecorBlocks.MANGROVE_DOOR);
    public static final RegistryObject<Item> MANGROVE_TRAPDOOR = registerBlockItem("mangrove_trapdoor",
            DecorBlocks.MANGROVE_TRAPDOOR);
    public static final RegistryObject<Item> MANGROVE_BUTTON = registerBlockItem("mangrove_button",
            DecorBlocks.MANGROVE_BUTTON);
    public static final RegistryObject<Item> MANGROVE_PRESSURE_PLATE = registerBlockItem("mangrove_pressure_plate",
            DecorBlocks.MANGROVE_PRESSURE_PLATE);

    public static final RegistryObject<Item> MANGROVE_SIGN = ITEMS.register(
            "mangrove_sign",
            () -> new SignItem(
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(16),
                    DecorBlocks.MANGROVE_SIGN.get(),
                    DecorBlocks.MANGROVE_WALL_SIGN.get()));

    public static final RegistryObject<Item> BAMBOO_SIGN = ITEMS.register(
            "bamboo_sign",
            () -> new SignItem(
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(16),
                    WoodBlocks.BAMBOO_SIGN.get(),
                    WoodBlocks.BAMBOO_WALL_SIGN.get()));

    public static final RegistryObject<Item> MANGROVE_BOAT = ITEMS.register(
            "mangrove_boat",
            () -> new MangroveBoatItem(
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(1)));
    public static final RegistryObject<Item> FESTIVE_LAMP = registerBlockItem("festive_lamp", DecorBlocks.FESTIVE_LAMP);

    public static final RegistryObject<Item> FROST_ROSE = ITEMS.register(
            "frost_rose",
            () -> new BlockItem(DecorBlocks.FROST_ROSE.get(), createBlockItemProperties()) {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip,
                        TooltipFlag flag) {
                    tooltip.add(new TextComponent("Obtained when a Wither kills a Snow Golem")
                            .withStyle(ChatFormatting.DARK_AQUA));
                }
            });
    public static final RegistryObject<Item> GLOW_LIGHTS = registerBlockItem("glow_lights", DecorBlocks.GLOW_LIGHTS);
}
