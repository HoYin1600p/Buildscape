package com.kingodogo.buildscape.registry.items;

import com.kingodogo.buildscape.feature.firework.InfinitePhoenixFireworkStarItem;
import com.kingodogo.buildscape.item.BottleOfMistItem;
import com.kingodogo.buildscape.item.ColoredItemFrameItem;
import com.kingodogo.buildscape.item.ConfettiItem;
import com.kingodogo.buildscape.item.MistBlockItem;
import com.kingodogo.buildscape.registry.ModCreativeModeTab;
import com.kingodogo.buildscape.registry.blocks.DecorBlocks;
import com.kingodogo.buildscape.registry.blocks.GlassBlocks;
import com.kingodogo.buildscape.registry.blocks.MasonryBlocks;
import com.kingodogo.buildscape.registry.blocks.ShapedStoneBlocks;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;

import static com.kingodogo.buildscape.registry.items.ItemsRegister.ITEMS;
import static com.kingodogo.buildscape.registry.items.ItemsRegister.createBlockItemProperties;
import static com.kingodogo.buildscape.registry.items.ItemsRegister.registerBlockItem;

/**
 * The items MULTICOLOR_GLOW_LIGHTS through ANCIENT_ASHEN_SCROLL of the registry, part 3 of 8, in registration order.
 * Named for its dominant families (stone, decor, steel); the neighbouring families that share a run of the order
 * with them are registered here too, because the registration order must not change.
 * Load order: {@link WoodItems#init()} runs first (see the static block), so every holder registers in order whichever is touched first.
 */
public final class MasonryItems {

    private MasonryItems() {}

    /** Forces this class to load, which registers its items. */
    public static void init() {}

    static {
        WoodItems.init();
    }

    public static final RegistryObject<Item> MULTICOLOR_GLOW_LIGHTS = registerBlockItem("multicolor_glow_lights",
            DecorBlocks.MULTICOLOR_GLOW_LIGHTS);

    public static final RegistryObject<Item> CONFETTI_ITEM = ITEMS.register(
            "confetti",
            () -> new ConfettiItem(new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(64)));

    public static final RegistryObject<Item> INFINITE_PHOENIX_FIREWORK_STAR = ITEMS.register(
            "infinite_phoenix_firework_star",
            () -> new InfinitePhoenixFireworkStarItem(
                    new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(1)));
    public static final RegistryObject<Item> STEEL_BLOCK = registerBlockItem("steel_block", DecorBlocks.STEEL_BLOCK);
    public static final RegistryObject<Item> STEEL_PIPE = registerBlockItem("steel_pipe", DecorBlocks.STEEL_PIPE);
    public static final RegistryObject<Item> HOLLOW_STEEL_PIPE = registerBlockItem("hollow_steel_pipe",
            DecorBlocks.HOLLOW_STEEL_PIPE);
    public static final RegistryObject<Item> SCRAPED_STEEL = registerBlockItem("scraped_steel",
            DecorBlocks.SCRAPED_STEEL);
    public static final RegistryObject<Item> RUSTIC_SCRAPED_STEEL = registerBlockItem("rustic_scraped_steel",
            DecorBlocks.RUSTIC_SCRAPED_STEEL);
    public static final RegistryObject<Item> STACKED_STEEL = registerBlockItem("stacked_steel",
            DecorBlocks.STACKED_STEEL);
    public static final RegistryObject<Item> STEEL_PANELS = registerBlockItem("steel_panels", DecorBlocks.STEEL_PANELS);
    public static final RegistryObject<Item> CROSSED_STEEL_PANELS = registerBlockItem("crossed_steel_panels",
            DecorBlocks.CROSSED_STEEL_PANELS);
    public static final RegistryObject<Item> STEEL_MESH_BLOCK = registerBlockItem("steel_mesh_block",
            DecorBlocks.STEEL_MESH_BLOCK);
    public static final RegistryObject<Item> STEEL_BOLTS = registerBlockItem("steel_bolts", DecorBlocks.STEEL_BOLTS);

    public static final RegistryObject<Item> STEEL_DOOR = ITEMS.register(
            "steel_door",
            () -> new DoubleHighBlockItem(DecorBlocks.STEEL_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> STEEL_TRAPDOOR = registerBlockItem("steel_trapdoor",
            DecorBlocks.STEEL_TRAPDOOR);
    public static final RegistryObject<Item> STEEL_BUTTON = registerBlockItem("steel_button", DecorBlocks.STEEL_BUTTON);
    public static final RegistryObject<Item> STEEL_PRESSURE_PLATE = registerBlockItem("steel_pressure_plate",
            DecorBlocks.STEEL_PRESSURE_PLATE);
    public static final RegistryObject<Item> SCRAPED_STEEL_SLAB = registerBlockItem("scraped_steel_slab",
            DecorBlocks.SCRAPED_STEEL_SLAB);
    public static final RegistryObject<Item> SCRAPED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "scraped_steel_vertical_slab", DecorBlocks.SCRAPED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> RUSTIC_SCRAPED_STEEL_SLAB = registerBlockItem("rustic_scraped_steel_slab",
            DecorBlocks.RUSTIC_SCRAPED_STEEL_SLAB);
    public static final RegistryObject<Item> RUSTIC_SCRAPED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "rustic_scraped_steel_vertical_slab", DecorBlocks.RUSTIC_SCRAPED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> STACKED_STEEL_SLAB = registerBlockItem("stacked_steel_slab",
            DecorBlocks.STACKED_STEEL_SLAB);
    public static final RegistryObject<Item> STACKED_STEEL_VERTICAL_SLAB = registerBlockItem(
            "stacked_steel_vertical_slab", DecorBlocks.STACKED_STEEL_VERTICAL_SLAB);
    public static final RegistryObject<Item> STACKED_STEEL_STAIRS = registerBlockItem("stacked_steel_stairs",
            DecorBlocks.STACKED_STEEL_STAIRS);
    public static final RegistryObject<Item> STEEL_PANELS_SLAB = registerBlockItem("steel_panels_slab",
            DecorBlocks.STEEL_PANELS_SLAB);
    public static final RegistryObject<Item> STEEL_PANELS_VERTICAL_SLAB = registerBlockItem(
            "steel_panels_vertical_slab", DecorBlocks.STEEL_PANELS_VERTICAL_SLAB);
    public static final RegistryObject<Item> STEEL_PANELS_STAIRS = registerBlockItem("steel_panels_stairs",
            DecorBlocks.STEEL_PANELS_STAIRS);
    public static final RegistryObject<Item> CROSSED_STEEL_PANELS_SLAB = registerBlockItem("crossed_steel_panels_slab",
            DecorBlocks.CROSSED_STEEL_PANELS_SLAB);
    public static final RegistryObject<Item> CROSSED_STEEL_PANELS_VERTICAL_SLAB = registerBlockItem(
            "crossed_steel_panels_vertical_slab", DecorBlocks.CROSSED_STEEL_PANELS_VERTICAL_SLAB);
    public static final RegistryObject<Item> STEEL_MESH_BLOCK_SLAB = registerBlockItem("steel_mesh_block_slab",
            DecorBlocks.STEEL_MESH_BLOCK_SLAB);
    public static final RegistryObject<Item> STEEL_MESH_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "steel_mesh_block_vertical_slab", DecorBlocks.STEEL_MESH_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> STEEL_MESH_BLOCK_STAIRS = registerBlockItem("steel_mesh_block_stairs",
            DecorBlocks.STEEL_MESH_BLOCK_STAIRS);
    public static final RegistryObject<Item> PRESSED_STEEL = registerBlockItem("pressed_steel",
            DecorBlocks.PRESSED_STEEL);
    public static final RegistryObject<Item> CUT_STEEL = registerBlockItem("cut_steel", DecorBlocks.CUT_STEEL);
    public static final RegistryObject<Item> POLISHED_STEEL = registerBlockItem("polished_steel",
            DecorBlocks.POLISHED_STEEL);
    public static final RegistryObject<Item> FACTORY_STEEL_PANEL = registerBlockItem("factory_steel_panel",
            DecorBlocks.FACTORY_STEEL_PANEL);
    public static final RegistryObject<Item> STEEL_CASING = registerBlockItem("steel_casing", DecorBlocks.STEEL_CASING);
    public static final RegistryObject<Item> STEEL_TRIM = registerBlockItem("steel_trim", DecorBlocks.STEEL_TRIM);
    public static final RegistryObject<Item> STEEL_PILLAR = registerBlockItem("steel_pillar", DecorBlocks.STEEL_PILLAR);
    public static final RegistryObject<Item> BOLTED_STEEL_PILLAR = registerBlockItem("bolted_steel_pillar",
            DecorBlocks.BOLTED_STEEL_PILLAR);
    public static final RegistryObject<Item> STEEL_GRATE = registerBlockItem("steel_grate", DecorBlocks.STEEL_GRATE);
    public static final RegistryObject<Item> STEEL_FAN = registerBlockItem("steel_fan", DecorBlocks.STEEL_FAN);
    public static final RegistryObject<Item> STEEL_BLOCK_STAIRS = registerBlockItem("steel_block_stairs",
            DecorBlocks.STEEL_BLOCK_STAIRS);
    public static final RegistryObject<Item> STEEL_BLOCK_SLAB = registerBlockItem("steel_block_slab",
            DecorBlocks.STEEL_BLOCK_SLAB);
    public static final RegistryObject<Item> STEEL_BLOCK_WALL = registerBlockItem("steel_block_wall",
            DecorBlocks.STEEL_BLOCK_WALL);
    public static final RegistryObject<Item> POLISHED_STEEL_STAIRS = registerBlockItem("polished_steel_stairs",
            DecorBlocks.POLISHED_STEEL_STAIRS);
    public static final RegistryObject<Item> POLISHED_STEEL_SLAB = registerBlockItem("polished_steel_slab",
            DecorBlocks.POLISHED_STEEL_SLAB);
    public static final RegistryObject<Item> POLISHED_STEEL_WALL = registerBlockItem("polished_steel_wall",
            DecorBlocks.POLISHED_STEEL_WALL);
    public static final RegistryObject<Item> PRESSED_STEEL_STAIRS = registerBlockItem("pressed_steel_stairs",
            DecorBlocks.PRESSED_STEEL_STAIRS);
    public static final RegistryObject<Item> PRESSED_STEEL_SLAB = registerBlockItem("pressed_steel_slab",
            DecorBlocks.PRESSED_STEEL_SLAB);
    public static final RegistryObject<Item> PRESSED_STEEL_WALL = registerBlockItem("pressed_steel_wall",
            DecorBlocks.PRESSED_STEEL_WALL);
    public static final RegistryObject<Item> CUT_STEEL_STAIRS = registerBlockItem("cut_steel_stairs",
            DecorBlocks.CUT_STEEL_STAIRS);
    public static final RegistryObject<Item> CUT_STEEL_SLAB = registerBlockItem("cut_steel_slab",
            DecorBlocks.CUT_STEEL_SLAB);
    public static final RegistryObject<Item> CUT_STEEL_WALL = registerBlockItem("cut_steel_wall",
            DecorBlocks.CUT_STEEL_WALL);
    public static final RegistryObject<Item> CAUTION_BLACK = registerBlockItem("caution_black",
            DecorBlocks.CAUTION_BLACK);
    public static final RegistryObject<Item> CAUTION_BLUE = registerBlockItem("caution_blue", DecorBlocks.CAUTION_BLUE);
    public static final RegistryObject<Item> CAUTION_FACTORY = registerBlockItem("caution_factory",
            DecorBlocks.CAUTION_FACTORY);
    public static final RegistryObject<Item> CAUTION_RED = registerBlockItem("caution_red", DecorBlocks.CAUTION_RED);
    public static final RegistryObject<Item> FRAMED_CAUTION = registerBlockItem("framed_caution",
            DecorBlocks.FRAMED_CAUTION);
    public static final RegistryObject<Item> CAUTION_WHITE = registerBlockItem("caution_white",
            DecorBlocks.CAUTION_WHITE);
    public static final RegistryObject<Item> CAUTION_LIME = registerBlockItem("caution_lime", DecorBlocks.CAUTION_LIME);
    public static final RegistryObject<Item> CAUTION_PINK = registerBlockItem("caution_pink", DecorBlocks.CAUTION_PINK);
    public static final RegistryObject<Item> CAUTION_YELLOW = registerBlockItem("caution_yellow",
            DecorBlocks.CAUTION_YELLOW);
    public static final RegistryObject<Item> CAUTION_CANDY = registerBlockItem("caution_candy",
            DecorBlocks.CAUTION_CANDY);
    public static final RegistryObject<Item> CAUTION_COTTONCANDY = registerBlockItem("caution_cottoncandy",
            DecorBlocks.CAUTION_COTTONCANDY);
    public static final RegistryObject<Item> CAUTION_MINTCANDY = registerBlockItem("caution_mintcandy",
            DecorBlocks.CAUTION_MINTCANDY);
    public static final RegistryObject<Item> CAUTION_CITRUSCANDY = registerBlockItem("caution_citruscandy",
            DecorBlocks.CAUTION_CITRUSCANDY);
    public static final RegistryObject<Item> CAUTION_BLACK_SLAB = registerBlockItem("caution_black_slab",
            DecorBlocks.CAUTION_BLACK_SLAB);
    public static final RegistryObject<Item> CAUTION_BLUE_SLAB = registerBlockItem("caution_blue_slab",
            DecorBlocks.CAUTION_BLUE_SLAB);
    public static final RegistryObject<Item> CAUTION_FACTORY_SLAB = registerBlockItem("caution_factory_slab",
            DecorBlocks.CAUTION_FACTORY_SLAB);
    public static final RegistryObject<Item> CAUTION_RED_SLAB = registerBlockItem("caution_red_slab",
            DecorBlocks.CAUTION_RED_SLAB);
    public static final RegistryObject<Item> FRAMED_CAUTION_SLAB = registerBlockItem("framed_caution_slab",
            DecorBlocks.FRAMED_CAUTION_SLAB);
    public static final RegistryObject<Item> CAUTION_WHITE_SLAB = registerBlockItem("caution_white_slab",
            DecorBlocks.CAUTION_WHITE_SLAB);
    public static final RegistryObject<Item> CAUTION_LIME_SLAB = registerBlockItem("caution_lime_slab",
            DecorBlocks.CAUTION_LIME_SLAB);
    public static final RegistryObject<Item> CAUTION_PINK_SLAB = registerBlockItem("caution_pink_slab",
            DecorBlocks.CAUTION_PINK_SLAB);
    public static final RegistryObject<Item> CAUTION_YELLOW_SLAB = registerBlockItem("caution_yellow_slab",
            DecorBlocks.CAUTION_YELLOW_SLAB);
    public static final RegistryObject<Item> CAUTION_CANDY_SLAB = registerBlockItem("caution_candy_slab",
            DecorBlocks.CAUTION_CANDY_SLAB);
    public static final RegistryObject<Item> CAUTION_COTTONCANDY_SLAB = registerBlockItem("caution_cottoncandy_slab",
            DecorBlocks.CAUTION_COTTONCANDY_SLAB);
    public static final RegistryObject<Item> CAUTION_MINTCANDY_SLAB = registerBlockItem("caution_mintcandy_slab",
            DecorBlocks.CAUTION_MINTCANDY_SLAB);
    public static final RegistryObject<Item> CAUTION_CITRUSCANDY_SLAB = registerBlockItem("caution_citruscandy_slab",
            DecorBlocks.CAUTION_CITRUSCANDY_SLAB);
    public static final RegistryObject<Item> CAUTION_BLACK_STAIRS = registerBlockItem("caution_black_stairs",
            DecorBlocks.CAUTION_BLACK_STAIRS);
    public static final RegistryObject<Item> CAUTION_BLUE_STAIRS = registerBlockItem("caution_blue_stairs",
            DecorBlocks.CAUTION_BLUE_STAIRS);
    public static final RegistryObject<Item> CAUTION_FACTORY_STAIRS = registerBlockItem("caution_factory_stairs",
            DecorBlocks.CAUTION_FACTORY_STAIRS);
    public static final RegistryObject<Item> CAUTION_RED_STAIRS = registerBlockItem("caution_red_stairs",
            DecorBlocks.CAUTION_RED_STAIRS);
    public static final RegistryObject<Item> FRAMED_CAUTION_STAIRS = registerBlockItem("framed_caution_stairs",
            DecorBlocks.FRAMED_CAUTION_STAIRS);
    public static final RegistryObject<Item> CAUTION_WHITE_STAIRS = registerBlockItem("caution_white_stairs",
            DecorBlocks.CAUTION_WHITE_STAIRS);
    public static final RegistryObject<Item> CAUTION_LIME_STAIRS = registerBlockItem("caution_lime_stairs",
            DecorBlocks.CAUTION_LIME_STAIRS);
    public static final RegistryObject<Item> CAUTION_PINK_STAIRS = registerBlockItem("caution_pink_stairs",
            DecorBlocks.CAUTION_PINK_STAIRS);
    public static final RegistryObject<Item> CAUTION_YELLOW_STAIRS = registerBlockItem("caution_yellow_stairs",
            DecorBlocks.CAUTION_YELLOW_STAIRS);
    public static final RegistryObject<Item> CAUTION_CANDY_STAIRS = registerBlockItem("caution_candy_stairs",
            DecorBlocks.CAUTION_CANDY_STAIRS);
    public static final RegistryObject<Item> CAUTION_COTTONCANDY_STAIRS = registerBlockItem(
            "caution_cottoncandy_stairs", DecorBlocks.CAUTION_COTTONCANDY_STAIRS);
    public static final RegistryObject<Item> CAUTION_MINTCANDY_STAIRS = registerBlockItem("caution_mintcandy_stairs",
            DecorBlocks.CAUTION_MINTCANDY_STAIRS);
    public static final RegistryObject<Item> CAUTION_CITRUSCANDY_STAIRS = registerBlockItem(
            "caution_citruscandy_stairs", DecorBlocks.CAUTION_CITRUSCANDY_STAIRS);
    public static final RegistryObject<Item> FACTORY_WHITE_GLASS = registerBlockItem("factory_white_glass",
            DecorBlocks.FACTORY_WHITE_GLASS);
    public static final RegistryObject<Item> FACTORY_LIGHT_GRAY_GLASS = registerBlockItem("factory_light_gray_glass",
            DecorBlocks.FACTORY_LIGHT_GRAY_GLASS);
    public static final RegistryObject<Item> FACTORY_GRAY_GLASS = registerBlockItem("factory_gray_glass",
            MasonryBlocks.FACTORY_GRAY_GLASS);
    public static final RegistryObject<Item> FACTORY_BLACK_GLASS = registerBlockItem("factory_black_glass",
            MasonryBlocks.FACTORY_BLACK_GLASS);
    public static final RegistryObject<Item> FACTORY_BROWN_GLASS = registerBlockItem("factory_brown_glass",
            MasonryBlocks.FACTORY_BROWN_GLASS);
    public static final RegistryObject<Item> FACTORY_RED_GLASS = registerBlockItem("factory_red_glass",
            MasonryBlocks.FACTORY_RED_GLASS);
    public static final RegistryObject<Item> FACTORY_ORANGE_GLASS = registerBlockItem("factory_orange_glass",
            MasonryBlocks.FACTORY_ORANGE_GLASS);
    public static final RegistryObject<Item> FACTORY_YELLOW_GLASS = registerBlockItem("factory_yellow_glass",
            MasonryBlocks.FACTORY_YELLOW_GLASS);
    public static final RegistryObject<Item> FACTORY_LIME_GLASS = registerBlockItem("factory_lime_glass",
            MasonryBlocks.FACTORY_LIME_GLASS);
    public static final RegistryObject<Item> FACTORY_GREEN_GLASS = registerBlockItem("factory_green_glass",
            MasonryBlocks.FACTORY_GREEN_GLASS);
    public static final RegistryObject<Item> FACTORY_CYAN_GLASS = registerBlockItem("factory_cyan_glass",
            MasonryBlocks.FACTORY_CYAN_GLASS);
    public static final RegistryObject<Item> FACTORY_LIGHT_BLUE_GLASS = registerBlockItem("factory_light_blue_glass",
            MasonryBlocks.FACTORY_LIGHT_BLUE_GLASS);
    public static final RegistryObject<Item> FACTORY_BLUE_GLASS = registerBlockItem("factory_blue_glass",
            MasonryBlocks.FACTORY_BLUE_GLASS);
    public static final RegistryObject<Item> FACTORY_PURPLE_GLASS = registerBlockItem("factory_purple_glass",
            MasonryBlocks.FACTORY_PURPLE_GLASS);
    public static final RegistryObject<Item> FACTORY_MAGENTA_GLASS = registerBlockItem("factory_magenta_glass",
            MasonryBlocks.FACTORY_MAGENTA_GLASS);
    public static final RegistryObject<Item> FACTORY_PINK_GLASS = registerBlockItem("factory_pink_glass",
            MasonryBlocks.FACTORY_PINK_GLASS);
    public static final RegistryObject<Item> FACTORY_WHITE_GLASS_PANE = registerBlockItem("factory_white_glass_pane",
            MasonryBlocks.FACTORY_WHITE_GLASS_PANE);
    public static final RegistryObject<Item> FACTORY_LIGHT_GRAY_GLASS_PANE = registerBlockItem(
            "factory_light_gray_glass_pane", MasonryBlocks.FACTORY_LIGHT_GRAY_GLASS_PANE);
    public static final RegistryObject<Item> FACTORY_GRAY_GLASS_PANE = registerBlockItem("factory_gray_glass_pane",
            MasonryBlocks.FACTORY_GRAY_GLASS_PANE);
    public static final RegistryObject<Item> FACTORY_BLACK_GLASS_PANE = registerBlockItem("factory_black_glass_pane",
            MasonryBlocks.FACTORY_BLACK_GLASS_PANE);
    public static final RegistryObject<Item> FACTORY_BROWN_GLASS_PANE = registerBlockItem("factory_brown_glass_pane",
            MasonryBlocks.FACTORY_BROWN_GLASS_PANE);
    public static final RegistryObject<Item> FACTORY_RED_GLASS_PANE = registerBlockItem("factory_red_glass_pane",
            MasonryBlocks.FACTORY_RED_GLASS_PANE);
    public static final RegistryObject<Item> FACTORY_ORANGE_GLASS_PANE = registerBlockItem("factory_orange_glass_pane",
            MasonryBlocks.FACTORY_ORANGE_GLASS_PANE);
    public static final RegistryObject<Item> FACTORY_YELLOW_GLASS_PANE = registerBlockItem("factory_yellow_glass_pane",
            MasonryBlocks.FACTORY_YELLOW_GLASS_PANE);
    public static final RegistryObject<Item> FACTORY_LIME_GLASS_PANE = registerBlockItem("factory_lime_glass_pane",
            MasonryBlocks.FACTORY_LIME_GLASS_PANE);
    public static final RegistryObject<Item> FACTORY_GREEN_GLASS_PANE = registerBlockItem("factory_green_glass_pane",
            MasonryBlocks.FACTORY_GREEN_GLASS_PANE);
    public static final RegistryObject<Item> FACTORY_CYAN_GLASS_PANE = registerBlockItem("factory_cyan_glass_pane",
            MasonryBlocks.FACTORY_CYAN_GLASS_PANE);
    public static final RegistryObject<Item> FACTORY_LIGHT_BLUE_GLASS_PANE = registerBlockItem(
            "factory_light_blue_glass_pane", MasonryBlocks.FACTORY_LIGHT_BLUE_GLASS_PANE);
    public static final RegistryObject<Item> FACTORY_BLUE_GLASS_PANE = registerBlockItem("factory_blue_glass_pane",
            MasonryBlocks.FACTORY_BLUE_GLASS_PANE);
    public static final RegistryObject<Item> FACTORY_PURPLE_GLASS_PANE = registerBlockItem("factory_purple_glass_pane",
            MasonryBlocks.FACTORY_PURPLE_GLASS_PANE);
    public static final RegistryObject<Item> FACTORY_MAGENTA_GLASS_PANE = registerBlockItem(
            "factory_magenta_glass_pane", MasonryBlocks.FACTORY_MAGENTA_GLASS_PANE);
    public static final RegistryObject<Item> FACTORY_PINK_GLASS_PANE = registerBlockItem("factory_pink_glass_pane",
            MasonryBlocks.FACTORY_PINK_GLASS_PANE);
    public static final RegistryObject<Item> WHITE_STAINED_BRICKS = registerBlockItem("white_stained_bricks",
            MasonryBlocks.WHITE_STAINED_BRICKS);
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICKS = registerBlockItem("light_gray_stained_bricks",
            MasonryBlocks.LIGHT_GRAY_STAINED_BRICKS);
    public static final RegistryObject<Item> GRAY_STAINED_BRICKS = registerBlockItem("gray_stained_bricks",
            MasonryBlocks.GRAY_STAINED_BRICKS);
    public static final RegistryObject<Item> BLACK_STAINED_BRICKS = registerBlockItem("black_stained_bricks",
            MasonryBlocks.BLACK_STAINED_BRICKS);
    public static final RegistryObject<Item> BROWN_STAINED_BRICKS = registerBlockItem("brown_stained_bricks",
            MasonryBlocks.BROWN_STAINED_BRICKS);
    public static final RegistryObject<Item> RED_STAINED_BRICKS = registerBlockItem("red_stained_bricks",
            MasonryBlocks.RED_STAINED_BRICKS);
    public static final RegistryObject<Item> ORANGE_STAINED_BRICKS = registerBlockItem("orange_stained_bricks",
            MasonryBlocks.ORANGE_STAINED_BRICKS);
    public static final RegistryObject<Item> YELLOW_STAINED_BRICKS = registerBlockItem("yellow_stained_bricks",
            MasonryBlocks.YELLOW_STAINED_BRICKS);
    public static final RegistryObject<Item> LIME_STAINED_BRICKS = registerBlockItem("lime_stained_bricks",
            MasonryBlocks.LIME_STAINED_BRICKS);
    public static final RegistryObject<Item> GREEN_STAINED_BRICKS = registerBlockItem("green_stained_bricks",
            MasonryBlocks.GREEN_STAINED_BRICKS);
    public static final RegistryObject<Item> CYAN_STAINED_BRICKS = registerBlockItem("cyan_stained_bricks",
            MasonryBlocks.CYAN_STAINED_BRICKS);
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICKS = registerBlockItem("light_blue_stained_bricks",
            MasonryBlocks.LIGHT_BLUE_STAINED_BRICKS);
    public static final RegistryObject<Item> BLUE_STAINED_BRICKS = registerBlockItem("blue_stained_bricks",
            MasonryBlocks.BLUE_STAINED_BRICKS);
    public static final RegistryObject<Item> PURPLE_STAINED_BRICKS = registerBlockItem("purple_stained_bricks",
            MasonryBlocks.PURPLE_STAINED_BRICKS);
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICKS = registerBlockItem("magenta_stained_bricks",
            MasonryBlocks.MAGENTA_STAINED_BRICKS);
    public static final RegistryObject<Item> PINK_STAINED_BRICKS = registerBlockItem("pink_stained_bricks",
            MasonryBlocks.PINK_STAINED_BRICKS);
    public static final RegistryObject<Item> WHITE_STAINED_BRICK_TILES = registerBlockItem("white_stained_brick_tiles",
            MasonryBlocks.WHITE_STAINED_BRICK_TILES);
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICK_TILES = registerBlockItem(
            "light_gray_stained_brick_tiles", MasonryBlocks.LIGHT_GRAY_STAINED_BRICK_TILES);
    public static final RegistryObject<Item> GRAY_STAINED_BRICK_TILES = registerBlockItem("gray_stained_brick_tiles",
            MasonryBlocks.GRAY_STAINED_BRICK_TILES);
    public static final RegistryObject<Item> BLACK_STAINED_BRICK_TILES = registerBlockItem("black_stained_brick_tiles",
            MasonryBlocks.BLACK_STAINED_BRICK_TILES);
    public static final RegistryObject<Item> BROWN_STAINED_BRICK_TILES = registerBlockItem("brown_stained_brick_tiles",
            MasonryBlocks.BROWN_STAINED_BRICK_TILES);
    public static final RegistryObject<Item> RED_STAINED_BRICK_TILES = registerBlockItem("red_stained_brick_tiles",
            MasonryBlocks.RED_STAINED_BRICK_TILES);
    public static final RegistryObject<Item> ORANGE_STAINED_BRICK_TILES = registerBlockItem(
            "orange_stained_brick_tiles", MasonryBlocks.ORANGE_STAINED_BRICK_TILES);
    public static final RegistryObject<Item> YELLOW_STAINED_BRICK_TILES = registerBlockItem(
            "yellow_stained_brick_tiles", MasonryBlocks.YELLOW_STAINED_BRICK_TILES);
    public static final RegistryObject<Item> LIME_STAINED_BRICK_TILES = registerBlockItem("lime_stained_brick_tiles",
            MasonryBlocks.LIME_STAINED_BRICK_TILES);
    public static final RegistryObject<Item> GREEN_STAINED_BRICK_TILES = registerBlockItem("green_stained_brick_tiles",
            MasonryBlocks.GREEN_STAINED_BRICK_TILES);
    public static final RegistryObject<Item> CYAN_STAINED_BRICK_TILES = registerBlockItem("cyan_stained_brick_tiles",
            MasonryBlocks.CYAN_STAINED_BRICK_TILES);
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICK_TILES = registerBlockItem(
            "light_blue_stained_brick_tiles", MasonryBlocks.LIGHT_BLUE_STAINED_BRICK_TILES);
    public static final RegistryObject<Item> BLUE_STAINED_BRICK_TILES = registerBlockItem("blue_stained_brick_tiles",
            MasonryBlocks.BLUE_STAINED_BRICK_TILES);
    public static final RegistryObject<Item> PURPLE_STAINED_BRICK_TILES = registerBlockItem(
            "purple_stained_brick_tiles", MasonryBlocks.PURPLE_STAINED_BRICK_TILES);
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICK_TILES = registerBlockItem(
            "magenta_stained_brick_tiles", MasonryBlocks.MAGENTA_STAINED_BRICK_TILES);
    public static final RegistryObject<Item> PINK_STAINED_BRICK_TILES = registerBlockItem("pink_stained_brick_tiles",
            MasonryBlocks.PINK_STAINED_BRICK_TILES);
    public static final RegistryObject<Item> WHITE_STAINED_BRICK_TILES_SLAB = registerBlockItem(
            "white_stained_brick_tiles_slab", MasonryBlocks.WHITE_STAINED_BRICK_TILES_SLAB);
    public static final RegistryObject<Item> WHITE_STAINED_BRICK_TILES_STAIRS = registerBlockItem(
            "white_stained_brick_tiles_stairs", MasonryBlocks.WHITE_STAINED_BRICK_TILES_STAIRS);
    public static final RegistryObject<Item> WHITE_STAINED_BRICK_TILES_WALL = registerBlockItem(
            "white_stained_brick_tiles_wall", MasonryBlocks.WHITE_STAINED_BRICK_TILES_WALL);
    public static final RegistryObject<Item> WHITE_STAINED_BRICK_TILES_VERTICAL_SLAB = registerBlockItem(
            "white_stained_brick_tiles_vertical_slab", ShapedStoneBlocks.WHITE_STAINED_BRICK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICK_TILES_SLAB = registerBlockItem(
            "light_gray_stained_brick_tiles_slab", MasonryBlocks.LIGHT_GRAY_STAINED_BRICK_TILES_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICK_TILES_STAIRS = registerBlockItem(
            "light_gray_stained_brick_tiles_stairs", MasonryBlocks.LIGHT_GRAY_STAINED_BRICK_TILES_STAIRS);
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICK_TILES_WALL = registerBlockItem(
            "light_gray_stained_brick_tiles_wall", MasonryBlocks.LIGHT_GRAY_STAINED_BRICK_TILES_WALL);
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICK_TILES_VERTICAL_SLAB = registerBlockItem(
            "light_gray_stained_brick_tiles_vertical_slab",
            ShapedStoneBlocks.LIGHT_GRAY_STAINED_BRICK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> GRAY_STAINED_BRICK_TILES_SLAB = registerBlockItem(
            "gray_stained_brick_tiles_slab", MasonryBlocks.GRAY_STAINED_BRICK_TILES_SLAB);
    public static final RegistryObject<Item> GRAY_STAINED_BRICK_TILES_STAIRS = registerBlockItem(
            "gray_stained_brick_tiles_stairs", MasonryBlocks.GRAY_STAINED_BRICK_TILES_STAIRS);
    public static final RegistryObject<Item> GRAY_STAINED_BRICK_TILES_WALL = registerBlockItem(
            "gray_stained_brick_tiles_wall", MasonryBlocks.GRAY_STAINED_BRICK_TILES_WALL);
    public static final RegistryObject<Item> GRAY_STAINED_BRICK_TILES_VERTICAL_SLAB = registerBlockItem(
            "gray_stained_brick_tiles_vertical_slab", ShapedStoneBlocks.GRAY_STAINED_BRICK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLACK_STAINED_BRICK_TILES_SLAB = registerBlockItem(
            "black_stained_brick_tiles_slab", MasonryBlocks.BLACK_STAINED_BRICK_TILES_SLAB);
    public static final RegistryObject<Item> BLACK_STAINED_BRICK_TILES_STAIRS = registerBlockItem(
            "black_stained_brick_tiles_stairs", MasonryBlocks.BLACK_STAINED_BRICK_TILES_STAIRS);
    public static final RegistryObject<Item> BLACK_STAINED_BRICK_TILES_WALL = registerBlockItem(
            "black_stained_brick_tiles_wall", MasonryBlocks.BLACK_STAINED_BRICK_TILES_WALL);
    public static final RegistryObject<Item> BLACK_STAINED_BRICK_TILES_VERTICAL_SLAB = registerBlockItem(
            "black_stained_brick_tiles_vertical_slab", ShapedStoneBlocks.BLACK_STAINED_BRICK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> BROWN_STAINED_BRICK_TILES_SLAB = registerBlockItem(
            "brown_stained_brick_tiles_slab", MasonryBlocks.BROWN_STAINED_BRICK_TILES_SLAB);
    public static final RegistryObject<Item> BROWN_STAINED_BRICK_TILES_STAIRS = registerBlockItem(
            "brown_stained_brick_tiles_stairs", MasonryBlocks.BROWN_STAINED_BRICK_TILES_STAIRS);
    public static final RegistryObject<Item> BROWN_STAINED_BRICK_TILES_WALL = registerBlockItem(
            "brown_stained_brick_tiles_wall", MasonryBlocks.BROWN_STAINED_BRICK_TILES_WALL);
    public static final RegistryObject<Item> BROWN_STAINED_BRICK_TILES_VERTICAL_SLAB = registerBlockItem(
            "brown_stained_brick_tiles_vertical_slab", ShapedStoneBlocks.BROWN_STAINED_BRICK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> RED_STAINED_BRICK_TILES_SLAB = registerBlockItem(
            "red_stained_brick_tiles_slab", MasonryBlocks.RED_STAINED_BRICK_TILES_SLAB);
    public static final RegistryObject<Item> RED_STAINED_BRICK_TILES_STAIRS = registerBlockItem(
            "red_stained_brick_tiles_stairs", MasonryBlocks.RED_STAINED_BRICK_TILES_STAIRS);
    public static final RegistryObject<Item> RED_STAINED_BRICK_TILES_WALL = registerBlockItem(
            "red_stained_brick_tiles_wall", MasonryBlocks.RED_STAINED_BRICK_TILES_WALL);
    public static final RegistryObject<Item> RED_STAINED_BRICK_TILES_VERTICAL_SLAB = registerBlockItem(
            "red_stained_brick_tiles_vertical_slab", ShapedStoneBlocks.RED_STAINED_BRICK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> ORANGE_STAINED_BRICK_TILES_SLAB = registerBlockItem(
            "orange_stained_brick_tiles_slab", MasonryBlocks.ORANGE_STAINED_BRICK_TILES_SLAB);
    public static final RegistryObject<Item> ORANGE_STAINED_BRICK_TILES_STAIRS = registerBlockItem(
            "orange_stained_brick_tiles_stairs", MasonryBlocks.ORANGE_STAINED_BRICK_TILES_STAIRS);
    public static final RegistryObject<Item> ORANGE_STAINED_BRICK_TILES_WALL = registerBlockItem(
            "orange_stained_brick_tiles_wall", MasonryBlocks.ORANGE_STAINED_BRICK_TILES_WALL);
    public static final RegistryObject<Item> ORANGE_STAINED_BRICK_TILES_VERTICAL_SLAB = registerBlockItem(
            "orange_stained_brick_tiles_vertical_slab", ShapedStoneBlocks.ORANGE_STAINED_BRICK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> YELLOW_STAINED_BRICK_TILES_SLAB = registerBlockItem(
            "yellow_stained_brick_tiles_slab", MasonryBlocks.YELLOW_STAINED_BRICK_TILES_SLAB);
    public static final RegistryObject<Item> YELLOW_STAINED_BRICK_TILES_STAIRS = registerBlockItem(
            "yellow_stained_brick_tiles_stairs", MasonryBlocks.YELLOW_STAINED_BRICK_TILES_STAIRS);
    public static final RegistryObject<Item> YELLOW_STAINED_BRICK_TILES_WALL = registerBlockItem(
            "yellow_stained_brick_tiles_wall", MasonryBlocks.YELLOW_STAINED_BRICK_TILES_WALL);
    public static final RegistryObject<Item> YELLOW_STAINED_BRICK_TILES_VERTICAL_SLAB = registerBlockItem(
            "yellow_stained_brick_tiles_vertical_slab", ShapedStoneBlocks.YELLOW_STAINED_BRICK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIME_STAINED_BRICK_TILES_SLAB = registerBlockItem(
            "lime_stained_brick_tiles_slab", MasonryBlocks.LIME_STAINED_BRICK_TILES_SLAB);
    public static final RegistryObject<Item> LIME_STAINED_BRICK_TILES_STAIRS = registerBlockItem(
            "lime_stained_brick_tiles_stairs", MasonryBlocks.LIME_STAINED_BRICK_TILES_STAIRS);
    public static final RegistryObject<Item> LIME_STAINED_BRICK_TILES_WALL = registerBlockItem(
            "lime_stained_brick_tiles_wall", MasonryBlocks.LIME_STAINED_BRICK_TILES_WALL);
    public static final RegistryObject<Item> LIME_STAINED_BRICK_TILES_VERTICAL_SLAB = registerBlockItem(
            "lime_stained_brick_tiles_vertical_slab", ShapedStoneBlocks.LIME_STAINED_BRICK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> GREEN_STAINED_BRICK_TILES_SLAB = registerBlockItem(
            "green_stained_brick_tiles_slab", MasonryBlocks.GREEN_STAINED_BRICK_TILES_SLAB);
    public static final RegistryObject<Item> GREEN_STAINED_BRICK_TILES_STAIRS = registerBlockItem(
            "green_stained_brick_tiles_stairs", MasonryBlocks.GREEN_STAINED_BRICK_TILES_STAIRS);
    public static final RegistryObject<Item> GREEN_STAINED_BRICK_TILES_WALL = registerBlockItem(
            "green_stained_brick_tiles_wall", MasonryBlocks.GREEN_STAINED_BRICK_TILES_WALL);
    public static final RegistryObject<Item> GREEN_STAINED_BRICK_TILES_VERTICAL_SLAB = registerBlockItem(
            "green_stained_brick_tiles_vertical_slab", ShapedStoneBlocks.GREEN_STAINED_BRICK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> CYAN_STAINED_BRICK_TILES_SLAB = registerBlockItem(
            "cyan_stained_brick_tiles_slab", MasonryBlocks.CYAN_STAINED_BRICK_TILES_SLAB);
    public static final RegistryObject<Item> CYAN_STAINED_BRICK_TILES_STAIRS = registerBlockItem(
            "cyan_stained_brick_tiles_stairs", MasonryBlocks.CYAN_STAINED_BRICK_TILES_STAIRS);
    public static final RegistryObject<Item> CYAN_STAINED_BRICK_TILES_WALL = registerBlockItem(
            "cyan_stained_brick_tiles_wall", MasonryBlocks.CYAN_STAINED_BRICK_TILES_WALL);
    public static final RegistryObject<Item> CYAN_STAINED_BRICK_TILES_VERTICAL_SLAB = registerBlockItem(
            "cyan_stained_brick_tiles_vertical_slab", ShapedStoneBlocks.CYAN_STAINED_BRICK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICK_TILES_SLAB = registerBlockItem(
            "light_blue_stained_brick_tiles_slab", MasonryBlocks.LIGHT_BLUE_STAINED_BRICK_TILES_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICK_TILES_STAIRS = registerBlockItem(
            "light_blue_stained_brick_tiles_stairs", MasonryBlocks.LIGHT_BLUE_STAINED_BRICK_TILES_STAIRS);
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICK_TILES_WALL = registerBlockItem(
            "light_blue_stained_brick_tiles_wall", MasonryBlocks.LIGHT_BLUE_STAINED_BRICK_TILES_WALL);
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICK_TILES_VERTICAL_SLAB = registerBlockItem(
            "light_blue_stained_brick_tiles_vertical_slab",
            ShapedStoneBlocks.LIGHT_BLUE_STAINED_BRICK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> BLUE_STAINED_BRICK_TILES_SLAB = registerBlockItem(
            "blue_stained_brick_tiles_slab", MasonryBlocks.BLUE_STAINED_BRICK_TILES_SLAB);
    public static final RegistryObject<Item> BLUE_STAINED_BRICK_TILES_STAIRS = registerBlockItem(
            "blue_stained_brick_tiles_stairs", MasonryBlocks.BLUE_STAINED_BRICK_TILES_STAIRS);
    public static final RegistryObject<Item> BLUE_STAINED_BRICK_TILES_WALL = registerBlockItem(
            "blue_stained_brick_tiles_wall", MasonryBlocks.BLUE_STAINED_BRICK_TILES_WALL);
    public static final RegistryObject<Item> BLUE_STAINED_BRICK_TILES_VERTICAL_SLAB = registerBlockItem(
            "blue_stained_brick_tiles_vertical_slab", ShapedStoneBlocks.BLUE_STAINED_BRICK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> PURPLE_STAINED_BRICK_TILES_SLAB = registerBlockItem(
            "purple_stained_brick_tiles_slab", MasonryBlocks.PURPLE_STAINED_BRICK_TILES_SLAB);
    public static final RegistryObject<Item> PURPLE_STAINED_BRICK_TILES_STAIRS = registerBlockItem(
            "purple_stained_brick_tiles_stairs", MasonryBlocks.PURPLE_STAINED_BRICK_TILES_STAIRS);
    public static final RegistryObject<Item> PURPLE_STAINED_BRICK_TILES_WALL = registerBlockItem(
            "purple_stained_brick_tiles_wall", MasonryBlocks.PURPLE_STAINED_BRICK_TILES_WALL);
    public static final RegistryObject<Item> PURPLE_STAINED_BRICK_TILES_VERTICAL_SLAB = registerBlockItem(
            "purple_stained_brick_tiles_vertical_slab", ShapedStoneBlocks.PURPLE_STAINED_BRICK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICK_TILES_SLAB = registerBlockItem(
            "magenta_stained_brick_tiles_slab", MasonryBlocks.MAGENTA_STAINED_BRICK_TILES_SLAB);
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICK_TILES_STAIRS = registerBlockItem(
            "magenta_stained_brick_tiles_stairs", MasonryBlocks.MAGENTA_STAINED_BRICK_TILES_STAIRS);
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICK_TILES_WALL = registerBlockItem(
            "magenta_stained_brick_tiles_wall", MasonryBlocks.MAGENTA_STAINED_BRICK_TILES_WALL);
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICK_TILES_VERTICAL_SLAB = registerBlockItem(
            "magenta_stained_brick_tiles_vertical_slab", ShapedStoneBlocks.MAGENTA_STAINED_BRICK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> PINK_STAINED_BRICK_TILES_SLAB = registerBlockItem(
            "pink_stained_brick_tiles_slab", MasonryBlocks.PINK_STAINED_BRICK_TILES_SLAB);
    public static final RegistryObject<Item> PINK_STAINED_BRICK_TILES_STAIRS = registerBlockItem(
            "pink_stained_brick_tiles_stairs", MasonryBlocks.PINK_STAINED_BRICK_TILES_STAIRS);
    public static final RegistryObject<Item> PINK_STAINED_BRICK_TILES_WALL = registerBlockItem(
            "pink_stained_brick_tiles_wall", MasonryBlocks.PINK_STAINED_BRICK_TILES_WALL);
    public static final RegistryObject<Item> PINK_STAINED_BRICK_TILES_VERTICAL_SLAB = registerBlockItem(
            "pink_stained_brick_tiles_vertical_slab", ShapedStoneBlocks.PINK_STAINED_BRICK_TILES_VERTICAL_SLAB);
    public static final RegistryObject<Item> WHITE_STAINED_BRICKS_SLAB = registerBlockItem("white_stained_bricks_slab",
            MasonryBlocks.WHITE_STAINED_BRICKS_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICKS_SLAB = registerBlockItem(
            "light_gray_stained_bricks_slab", MasonryBlocks.LIGHT_GRAY_STAINED_BRICKS_SLAB);
    public static final RegistryObject<Item> GRAY_STAINED_BRICKS_SLAB = registerBlockItem("gray_stained_bricks_slab",
            MasonryBlocks.GRAY_STAINED_BRICKS_SLAB);
    public static final RegistryObject<Item> BLACK_STAINED_BRICKS_SLAB = registerBlockItem("black_stained_bricks_slab",
            MasonryBlocks.BLACK_STAINED_BRICKS_SLAB);
    public static final RegistryObject<Item> BROWN_STAINED_BRICKS_SLAB = registerBlockItem("brown_stained_bricks_slab",
            MasonryBlocks.BROWN_STAINED_BRICKS_SLAB);
    public static final RegistryObject<Item> RED_STAINED_BRICKS_SLAB = registerBlockItem("red_stained_bricks_slab",
            MasonryBlocks.RED_STAINED_BRICKS_SLAB);
    public static final RegistryObject<Item> ORANGE_STAINED_BRICKS_SLAB = registerBlockItem(
            "orange_stained_bricks_slab", MasonryBlocks.ORANGE_STAINED_BRICKS_SLAB);
    public static final RegistryObject<Item> YELLOW_STAINED_BRICKS_SLAB = registerBlockItem(
            "yellow_stained_bricks_slab", MasonryBlocks.YELLOW_STAINED_BRICKS_SLAB);
    public static final RegistryObject<Item> LIME_STAINED_BRICKS_SLAB = registerBlockItem("lime_stained_bricks_slab",
            MasonryBlocks.LIME_STAINED_BRICKS_SLAB);
    public static final RegistryObject<Item> GREEN_STAINED_BRICKS_SLAB = registerBlockItem("green_stained_bricks_slab",
            MasonryBlocks.GREEN_STAINED_BRICKS_SLAB);
    public static final RegistryObject<Item> CYAN_STAINED_BRICKS_SLAB = registerBlockItem("cyan_stained_bricks_slab",
            MasonryBlocks.CYAN_STAINED_BRICKS_SLAB);
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICKS_SLAB = registerBlockItem(
            "light_blue_stained_bricks_slab", MasonryBlocks.LIGHT_BLUE_STAINED_BRICKS_SLAB);
    public static final RegistryObject<Item> BLUE_STAINED_BRICKS_SLAB = registerBlockItem("blue_stained_bricks_slab",
            MasonryBlocks.BLUE_STAINED_BRICKS_SLAB);
    public static final RegistryObject<Item> PURPLE_STAINED_BRICKS_SLAB = registerBlockItem(
            "purple_stained_bricks_slab", MasonryBlocks.PURPLE_STAINED_BRICKS_SLAB);
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICKS_SLAB = registerBlockItem(
            "magenta_stained_bricks_slab", MasonryBlocks.MAGENTA_STAINED_BRICKS_SLAB);
    public static final RegistryObject<Item> PINK_STAINED_BRICKS_SLAB = registerBlockItem("pink_stained_bricks_slab",
            MasonryBlocks.PINK_STAINED_BRICKS_SLAB);
    public static final RegistryObject<Item> WHITE_STAINED_BRICKS_STAIRS = registerBlockItem(
            "white_stained_bricks_stairs", MasonryBlocks.WHITE_STAINED_BRICKS_STAIRS);
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICKS_STAIRS = registerBlockItem(
            "light_gray_stained_bricks_stairs", MasonryBlocks.LIGHT_GRAY_STAINED_BRICKS_STAIRS);
    public static final RegistryObject<Item> GRAY_STAINED_BRICKS_STAIRS = registerBlockItem(
            "gray_stained_bricks_stairs", MasonryBlocks.GRAY_STAINED_BRICKS_STAIRS);
    public static final RegistryObject<Item> BLACK_STAINED_BRICKS_STAIRS = registerBlockItem(
            "black_stained_bricks_stairs", MasonryBlocks.BLACK_STAINED_BRICKS_STAIRS);
    public static final RegistryObject<Item> BROWN_STAINED_BRICKS_STAIRS = registerBlockItem(
            "brown_stained_bricks_stairs", MasonryBlocks.BROWN_STAINED_BRICKS_STAIRS);
    public static final RegistryObject<Item> RED_STAINED_BRICKS_STAIRS = registerBlockItem("red_stained_bricks_stairs",
            MasonryBlocks.RED_STAINED_BRICKS_STAIRS);
    public static final RegistryObject<Item> ORANGE_STAINED_BRICKS_STAIRS = registerBlockItem(
            "orange_stained_bricks_stairs", MasonryBlocks.ORANGE_STAINED_BRICKS_STAIRS);
    public static final RegistryObject<Item> YELLOW_STAINED_BRICKS_STAIRS = registerBlockItem(
            "yellow_stained_bricks_stairs", MasonryBlocks.YELLOW_STAINED_BRICKS_STAIRS);
    public static final RegistryObject<Item> LIME_STAINED_BRICKS_STAIRS = registerBlockItem(
            "lime_stained_bricks_stairs", MasonryBlocks.LIME_STAINED_BRICKS_STAIRS);
    public static final RegistryObject<Item> GREEN_STAINED_BRICKS_STAIRS = registerBlockItem(
            "green_stained_bricks_stairs", MasonryBlocks.GREEN_STAINED_BRICKS_STAIRS);
    public static final RegistryObject<Item> CYAN_STAINED_BRICKS_STAIRS = registerBlockItem(
            "cyan_stained_bricks_stairs", MasonryBlocks.CYAN_STAINED_BRICKS_STAIRS);
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICKS_STAIRS = registerBlockItem(
            "light_blue_stained_bricks_stairs", MasonryBlocks.LIGHT_BLUE_STAINED_BRICKS_STAIRS);
    public static final RegistryObject<Item> BLUE_STAINED_BRICKS_STAIRS = registerBlockItem(
            "blue_stained_bricks_stairs", MasonryBlocks.BLUE_STAINED_BRICKS_STAIRS);
    public static final RegistryObject<Item> PURPLE_STAINED_BRICKS_STAIRS = registerBlockItem(
            "purple_stained_bricks_stairs", MasonryBlocks.PURPLE_STAINED_BRICKS_STAIRS);
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICKS_STAIRS = registerBlockItem(
            "magenta_stained_bricks_stairs", MasonryBlocks.MAGENTA_STAINED_BRICKS_STAIRS);
    public static final RegistryObject<Item> PINK_STAINED_BRICKS_STAIRS = registerBlockItem(
            "pink_stained_bricks_stairs", MasonryBlocks.PINK_STAINED_BRICKS_STAIRS);
    public static final RegistryObject<Item> WHITE_STAINED_BRICKS_WALL = registerBlockItem("white_stained_bricks_wall",
            MasonryBlocks.WHITE_STAINED_BRICKS_WALL);
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICKS_WALL = registerBlockItem(
            "light_gray_stained_bricks_wall", MasonryBlocks.LIGHT_GRAY_STAINED_BRICKS_WALL);
    public static final RegistryObject<Item> GRAY_STAINED_BRICKS_WALL = registerBlockItem("gray_stained_bricks_wall",
            MasonryBlocks.GRAY_STAINED_BRICKS_WALL);
    public static final RegistryObject<Item> BLACK_STAINED_BRICKS_WALL = registerBlockItem("black_stained_bricks_wall",
            MasonryBlocks.BLACK_STAINED_BRICKS_WALL);
    public static final RegistryObject<Item> BROWN_STAINED_BRICKS_WALL = registerBlockItem("brown_stained_bricks_wall",
            MasonryBlocks.BROWN_STAINED_BRICKS_WALL);
    public static final RegistryObject<Item> RED_STAINED_BRICKS_WALL = registerBlockItem("red_stained_bricks_wall",
            MasonryBlocks.RED_STAINED_BRICKS_WALL);
    public static final RegistryObject<Item> ORANGE_STAINED_BRICKS_WALL = registerBlockItem(
            "orange_stained_bricks_wall", MasonryBlocks.ORANGE_STAINED_BRICKS_WALL);
    public static final RegistryObject<Item> YELLOW_STAINED_BRICKS_WALL = registerBlockItem(
            "yellow_stained_bricks_wall", MasonryBlocks.YELLOW_STAINED_BRICKS_WALL);
    public static final RegistryObject<Item> LIME_STAINED_BRICKS_WALL = registerBlockItem("lime_stained_bricks_wall",
            MasonryBlocks.LIME_STAINED_BRICKS_WALL);
    public static final RegistryObject<Item> GREEN_STAINED_BRICKS_WALL = registerBlockItem("green_stained_bricks_wall",
            MasonryBlocks.GREEN_STAINED_BRICKS_WALL);
    public static final RegistryObject<Item> CYAN_STAINED_BRICKS_WALL = registerBlockItem("cyan_stained_bricks_wall",
            MasonryBlocks.CYAN_STAINED_BRICKS_WALL);
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICKS_WALL = registerBlockItem(
            "light_blue_stained_bricks_wall", MasonryBlocks.LIGHT_BLUE_STAINED_BRICKS_WALL);
    public static final RegistryObject<Item> BLUE_STAINED_BRICKS_WALL = registerBlockItem("blue_stained_bricks_wall",
            MasonryBlocks.BLUE_STAINED_BRICKS_WALL);
    public static final RegistryObject<Item> PURPLE_STAINED_BRICKS_WALL = registerBlockItem(
            "purple_stained_bricks_wall", MasonryBlocks.PURPLE_STAINED_BRICKS_WALL);
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICKS_WALL = registerBlockItem(
            "magenta_stained_bricks_wall", MasonryBlocks.MAGENTA_STAINED_BRICKS_WALL);
    public static final RegistryObject<Item> PINK_STAINED_BRICKS_WALL = registerBlockItem("pink_stained_bricks_wall",
            MasonryBlocks.PINK_STAINED_BRICKS_WALL);

    public static final RegistryObject<Item> WHITE_ITEM_FRAME = ITEMS.register(
            "white_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "white"));

    public static final RegistryObject<Item> LIGHT_GRAY_ITEM_FRAME = ITEMS.register(
            "light_gray_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "light_gray"));

    public static final RegistryObject<Item> GRAY_ITEM_FRAME = ITEMS.register(
            "gray_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "gray"));

    public static final RegistryObject<Item> BLACK_ITEM_FRAME = ITEMS.register(
            "black_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "black"));

    public static final RegistryObject<Item> BROWN_ITEM_FRAME = ITEMS.register(
            "brown_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "brown"));

    public static final RegistryObject<Item> RED_ITEM_FRAME = ITEMS.register(
            "red_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "red"));

    public static final RegistryObject<Item> ORANGE_ITEM_FRAME = ITEMS.register(
            "orange_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "orange"));

    public static final RegistryObject<Item> YELLOW_ITEM_FRAME = ITEMS.register(
            "yellow_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "yellow"));

    public static final RegistryObject<Item> LIME_ITEM_FRAME = ITEMS.register(
            "lime_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "lime"));

    public static final RegistryObject<Item> GREEN_ITEM_FRAME = ITEMS.register(
            "green_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "green"));

    public static final RegistryObject<Item> CYAN_ITEM_FRAME = ITEMS.register(
            "cyan_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "cyan"));

    public static final RegistryObject<Item> LIGHT_BLUE_ITEM_FRAME = ITEMS.register(
            "light_blue_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "light_blue"));

    public static final RegistryObject<Item> BLUE_ITEM_FRAME = ITEMS.register(
            "blue_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "blue"));

    public static final RegistryObject<Item> PURPLE_ITEM_FRAME = ITEMS.register(
            "purple_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "purple"));

    public static final RegistryObject<Item> MAGENTA_ITEM_FRAME = ITEMS.register(
            "magenta_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "magenta"));

    public static final RegistryObject<Item> PINK_ITEM_FRAME = ITEMS.register(
            "pink_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "pink"));

    public static final RegistryObject<Item> INVISIBLE_ITEM_FRAME = ITEMS.register(
            "invisible_item_frame",
            () -> new ColoredItemFrameItem(createBlockItemProperties(), "invisible"));
    public static final RegistryObject<Item> SMOOTH_STONE_STAIRS = registerBlockItem("smooth_stone_stairs",
            MasonryBlocks.SMOOTH_STONE_STAIRS);
    public static final RegistryObject<Item> POLISHED_WHITE_CONCRETE = registerBlockItem("polished_white_concrete",
            MasonryBlocks.POLISHED_WHITE_CONCRETE);
    public static final RegistryObject<Item> POLISHED_ORANGE_CONCRETE = registerBlockItem("polished_orange_concrete",
            MasonryBlocks.POLISHED_ORANGE_CONCRETE);
    public static final RegistryObject<Item> POLISHED_MAGENTA_CONCRETE = registerBlockItem("polished_magenta_concrete",
            MasonryBlocks.POLISHED_MAGENTA_CONCRETE);
    public static final RegistryObject<Item> POLISHED_LIGHT_BLUE_CONCRETE = registerBlockItem(
            "polished_light_blue_concrete", MasonryBlocks.POLISHED_LIGHT_BLUE_CONCRETE);
    public static final RegistryObject<Item> POLISHED_YELLOW_CONCRETE = registerBlockItem("polished_yellow_concrete",
            MasonryBlocks.POLISHED_YELLOW_CONCRETE);
    public static final RegistryObject<Item> POLISHED_LIME_CONCRETE = registerBlockItem("polished_lime_concrete",
            MasonryBlocks.POLISHED_LIME_CONCRETE);
    public static final RegistryObject<Item> POLISHED_PINK_CONCRETE = registerBlockItem("polished_pink_concrete",
            MasonryBlocks.POLISHED_PINK_CONCRETE);
    public static final RegistryObject<Item> POLISHED_GRAY_CONCRETE = registerBlockItem("polished_gray_concrete",
            MasonryBlocks.POLISHED_GRAY_CONCRETE);
    public static final RegistryObject<Item> POLISHED_LIGHT_GRAY_CONCRETE = registerBlockItem(
            "polished_light_gray_concrete", MasonryBlocks.POLISHED_LIGHT_GRAY_CONCRETE);
    public static final RegistryObject<Item> POLISHED_CYAN_CONCRETE = registerBlockItem("polished_cyan_concrete",
            MasonryBlocks.POLISHED_CYAN_CONCRETE);
    public static final RegistryObject<Item> POLISHED_PURPLE_CONCRETE = registerBlockItem("polished_purple_concrete",
            MasonryBlocks.POLISHED_PURPLE_CONCRETE);
    public static final RegistryObject<Item> POLISHED_BLUE_CONCRETE = registerBlockItem("polished_blue_concrete",
            MasonryBlocks.POLISHED_BLUE_CONCRETE);
    public static final RegistryObject<Item> POLISHED_BROWN_CONCRETE = registerBlockItem("polished_brown_concrete",
            MasonryBlocks.POLISHED_BROWN_CONCRETE);
    public static final RegistryObject<Item> POLISHED_GREEN_CONCRETE = registerBlockItem("polished_green_concrete",
            MasonryBlocks.POLISHED_GREEN_CONCRETE);
    public static final RegistryObject<Item> POLISHED_RED_CONCRETE = registerBlockItem("polished_red_concrete",
            MasonryBlocks.POLISHED_RED_CONCRETE);
    public static final RegistryObject<Item> POLISHED_BLACK_CONCRETE = registerBlockItem("polished_black_concrete",
            MasonryBlocks.POLISHED_BLACK_CONCRETE);
    public static final RegistryObject<Item> POLISHED_WHITE_CONCRETE_STAIRS = registerBlockItem(
            "polished_white_concrete_stairs", MasonryBlocks.POLISHED_WHITE_CONCRETE_STAIRS);
    public static final RegistryObject<Item> POLISHED_WHITE_CONCRETE_SLAB = registerBlockItem(
            "polished_white_concrete_slab", MasonryBlocks.POLISHED_WHITE_CONCRETE_SLAB);
    public static final RegistryObject<Item> POLISHED_WHITE_CONCRETE_WALL = registerBlockItem(
            "polished_white_concrete_wall", MasonryBlocks.POLISHED_WHITE_CONCRETE_WALL);
    public static final RegistryObject<Item> POLISHED_ORANGE_CONCRETE_STAIRS = registerBlockItem(
            "polished_orange_concrete_stairs", MasonryBlocks.POLISHED_ORANGE_CONCRETE_STAIRS);
    public static final RegistryObject<Item> POLISHED_ORANGE_CONCRETE_SLAB = registerBlockItem(
            "polished_orange_concrete_slab", MasonryBlocks.POLISHED_ORANGE_CONCRETE_SLAB);
    public static final RegistryObject<Item> POLISHED_ORANGE_CONCRETE_WALL = registerBlockItem(
            "polished_orange_concrete_wall", MasonryBlocks.POLISHED_ORANGE_CONCRETE_WALL);
    public static final RegistryObject<Item> POLISHED_MAGENTA_CONCRETE_STAIRS = registerBlockItem(
            "polished_magenta_concrete_stairs", MasonryBlocks.POLISHED_MAGENTA_CONCRETE_STAIRS);
    public static final RegistryObject<Item> POLISHED_MAGENTA_CONCRETE_SLAB = registerBlockItem(
            "polished_magenta_concrete_slab", MasonryBlocks.POLISHED_MAGENTA_CONCRETE_SLAB);
    public static final RegistryObject<Item> POLISHED_MAGENTA_CONCRETE_WALL = registerBlockItem(
            "polished_magenta_concrete_wall", MasonryBlocks.POLISHED_MAGENTA_CONCRETE_WALL);
    public static final RegistryObject<Item> POLISHED_LIGHT_BLUE_CONCRETE_STAIRS = registerBlockItem(
            "polished_light_blue_concrete_stairs", MasonryBlocks.POLISHED_LIGHT_BLUE_CONCRETE_STAIRS);
    public static final RegistryObject<Item> POLISHED_LIGHT_BLUE_CONCRETE_SLAB = registerBlockItem(
            "polished_light_blue_concrete_slab", MasonryBlocks.POLISHED_LIGHT_BLUE_CONCRETE_SLAB);
    public static final RegistryObject<Item> POLISHED_LIGHT_BLUE_CONCRETE_WALL = registerBlockItem(
            "polished_light_blue_concrete_wall", MasonryBlocks.POLISHED_LIGHT_BLUE_CONCRETE_WALL);
    public static final RegistryObject<Item> POLISHED_YELLOW_CONCRETE_STAIRS = registerBlockItem(
            "polished_yellow_concrete_stairs", MasonryBlocks.POLISHED_YELLOW_CONCRETE_STAIRS);
    public static final RegistryObject<Item> POLISHED_YELLOW_CONCRETE_SLAB = registerBlockItem(
            "polished_yellow_concrete_slab", MasonryBlocks.POLISHED_YELLOW_CONCRETE_SLAB);
    public static final RegistryObject<Item> POLISHED_YELLOW_CONCRETE_WALL = registerBlockItem(
            "polished_yellow_concrete_wall", MasonryBlocks.POLISHED_YELLOW_CONCRETE_WALL);
    public static final RegistryObject<Item> POLISHED_LIME_CONCRETE_STAIRS = registerBlockItem(
            "polished_lime_concrete_stairs", MasonryBlocks.POLISHED_LIME_CONCRETE_STAIRS);
    public static final RegistryObject<Item> POLISHED_LIME_CONCRETE_SLAB = registerBlockItem(
            "polished_lime_concrete_slab", MasonryBlocks.POLISHED_LIME_CONCRETE_SLAB);
    public static final RegistryObject<Item> POLISHED_LIME_CONCRETE_WALL = registerBlockItem(
            "polished_lime_concrete_wall", MasonryBlocks.POLISHED_LIME_CONCRETE_WALL);
    public static final RegistryObject<Item> POLISHED_PINK_CONCRETE_STAIRS = registerBlockItem(
            "polished_pink_concrete_stairs", MasonryBlocks.POLISHED_PINK_CONCRETE_STAIRS);
    public static final RegistryObject<Item> POLISHED_PINK_CONCRETE_SLAB = registerBlockItem(
            "polished_pink_concrete_slab", MasonryBlocks.POLISHED_PINK_CONCRETE_SLAB);
    public static final RegistryObject<Item> POLISHED_PINK_CONCRETE_WALL = registerBlockItem(
            "polished_pink_concrete_wall", MasonryBlocks.POLISHED_PINK_CONCRETE_WALL);
    public static final RegistryObject<Item> POLISHED_GRAY_CONCRETE_STAIRS = registerBlockItem(
            "polished_gray_concrete_stairs", MasonryBlocks.POLISHED_GRAY_CONCRETE_STAIRS);
    public static final RegistryObject<Item> POLISHED_GRAY_CONCRETE_SLAB = registerBlockItem(
            "polished_gray_concrete_slab", MasonryBlocks.POLISHED_GRAY_CONCRETE_SLAB);
    public static final RegistryObject<Item> POLISHED_GRAY_CONCRETE_WALL = registerBlockItem(
            "polished_gray_concrete_wall", MasonryBlocks.POLISHED_GRAY_CONCRETE_WALL);
    public static final RegistryObject<Item> POLISHED_LIGHT_GRAY_CONCRETE_STAIRS = registerBlockItem(
            "polished_light_gray_concrete_stairs", MasonryBlocks.POLISHED_LIGHT_GRAY_CONCRETE_STAIRS);
    public static final RegistryObject<Item> POLISHED_LIGHT_GRAY_CONCRETE_SLAB = registerBlockItem(
            "polished_light_gray_concrete_slab", MasonryBlocks.POLISHED_LIGHT_GRAY_CONCRETE_SLAB);
    public static final RegistryObject<Item> POLISHED_LIGHT_GRAY_CONCRETE_WALL = registerBlockItem(
            "polished_light_gray_concrete_wall", MasonryBlocks.POLISHED_LIGHT_GRAY_CONCRETE_WALL);
    public static final RegistryObject<Item> POLISHED_CYAN_CONCRETE_STAIRS = registerBlockItem(
            "polished_cyan_concrete_stairs", MasonryBlocks.POLISHED_CYAN_CONCRETE_STAIRS);
    public static final RegistryObject<Item> POLISHED_CYAN_CONCRETE_SLAB = registerBlockItem(
            "polished_cyan_concrete_slab", MasonryBlocks.POLISHED_CYAN_CONCRETE_SLAB);
    public static final RegistryObject<Item> POLISHED_CYAN_CONCRETE_WALL = registerBlockItem(
            "polished_cyan_concrete_wall", MasonryBlocks.POLISHED_CYAN_CONCRETE_WALL);
    public static final RegistryObject<Item> POLISHED_PURPLE_CONCRETE_STAIRS = registerBlockItem(
            "polished_purple_concrete_stairs", MasonryBlocks.POLISHED_PURPLE_CONCRETE_STAIRS);
    public static final RegistryObject<Item> POLISHED_PURPLE_CONCRETE_SLAB = registerBlockItem(
            "polished_purple_concrete_slab", MasonryBlocks.POLISHED_PURPLE_CONCRETE_SLAB);
    public static final RegistryObject<Item> POLISHED_PURPLE_CONCRETE_WALL = registerBlockItem(
            "polished_purple_concrete_wall", MasonryBlocks.POLISHED_PURPLE_CONCRETE_WALL);
    public static final RegistryObject<Item> POLISHED_BLUE_CONCRETE_STAIRS = registerBlockItem(
            "polished_blue_concrete_stairs", MasonryBlocks.POLISHED_BLUE_CONCRETE_STAIRS);
    public static final RegistryObject<Item> POLISHED_BLUE_CONCRETE_SLAB = registerBlockItem(
            "polished_blue_concrete_slab", MasonryBlocks.POLISHED_BLUE_CONCRETE_SLAB);
    public static final RegistryObject<Item> POLISHED_BLUE_CONCRETE_WALL = registerBlockItem(
            "polished_blue_concrete_wall", MasonryBlocks.POLISHED_BLUE_CONCRETE_WALL);
    public static final RegistryObject<Item> POLISHED_BROWN_CONCRETE_STAIRS = registerBlockItem(
            "polished_brown_concrete_stairs", MasonryBlocks.POLISHED_BROWN_CONCRETE_STAIRS);
    public static final RegistryObject<Item> POLISHED_BROWN_CONCRETE_SLAB = registerBlockItem(
            "polished_brown_concrete_slab", MasonryBlocks.POLISHED_BROWN_CONCRETE_SLAB);
    public static final RegistryObject<Item> POLISHED_BROWN_CONCRETE_WALL = registerBlockItem(
            "polished_brown_concrete_wall", MasonryBlocks.POLISHED_BROWN_CONCRETE_WALL);
    public static final RegistryObject<Item> POLISHED_GREEN_CONCRETE_STAIRS = registerBlockItem(
            "polished_green_concrete_stairs", MasonryBlocks.POLISHED_GREEN_CONCRETE_STAIRS);
    public static final RegistryObject<Item> POLISHED_GREEN_CONCRETE_SLAB = registerBlockItem(
            "polished_green_concrete_slab", MasonryBlocks.POLISHED_GREEN_CONCRETE_SLAB);
    public static final RegistryObject<Item> POLISHED_GREEN_CONCRETE_WALL = registerBlockItem(
            "polished_green_concrete_wall", MasonryBlocks.POLISHED_GREEN_CONCRETE_WALL);
    public static final RegistryObject<Item> POLISHED_RED_CONCRETE_STAIRS = registerBlockItem(
            "polished_red_concrete_stairs", MasonryBlocks.POLISHED_RED_CONCRETE_STAIRS);
    public static final RegistryObject<Item> POLISHED_RED_CONCRETE_SLAB = registerBlockItem(
            "polished_red_concrete_slab", MasonryBlocks.POLISHED_RED_CONCRETE_SLAB);
    public static final RegistryObject<Item> POLISHED_RED_CONCRETE_WALL = registerBlockItem(
            "polished_red_concrete_wall", MasonryBlocks.POLISHED_RED_CONCRETE_WALL);
    public static final RegistryObject<Item> POLISHED_BLACK_CONCRETE_STAIRS = registerBlockItem(
            "polished_black_concrete_stairs", MasonryBlocks.POLISHED_BLACK_CONCRETE_STAIRS);
    public static final RegistryObject<Item> POLISHED_BLACK_CONCRETE_SLAB = registerBlockItem(
            "polished_black_concrete_slab", MasonryBlocks.POLISHED_BLACK_CONCRETE_SLAB);
    public static final RegistryObject<Item> POLISHED_BLACK_CONCRETE_WALL = registerBlockItem(
            "polished_black_concrete_wall", MasonryBlocks.POLISHED_BLACK_CONCRETE_WALL);
    public static final RegistryObject<Item> SMOKE_VENT = registerBlockItem("smoke_vent", MasonryBlocks.SMOKE_VENT);
    public static final RegistryObject<Item> WHITE_REDSTONE_LAMP = registerBlockItem("white_redstone_lamp",
            MasonryBlocks.WHITE_REDSTONE_LAMP);
    public static final RegistryObject<Item> ORANGE_REDSTONE_LAMP = registerBlockItem("orange_redstone_lamp",
            MasonryBlocks.ORANGE_REDSTONE_LAMP);
    public static final RegistryObject<Item> MAGENTA_REDSTONE_LAMP = registerBlockItem("magenta_redstone_lamp",
            MasonryBlocks.MAGENTA_REDSTONE_LAMP);
    public static final RegistryObject<Item> LIGHT_BLUE_REDSTONE_LAMP = registerBlockItem("light_blue_redstone_lamp",
            MasonryBlocks.LIGHT_BLUE_REDSTONE_LAMP);
    public static final RegistryObject<Item> YELLOW_REDSTONE_LAMP = registerBlockItem("yellow_redstone_lamp",
            MasonryBlocks.YELLOW_REDSTONE_LAMP);
    public static final RegistryObject<Item> LIME_REDSTONE_LAMP = registerBlockItem("lime_redstone_lamp",
            MasonryBlocks.LIME_REDSTONE_LAMP);
    public static final RegistryObject<Item> PINK_REDSTONE_LAMP = registerBlockItem("pink_redstone_lamp",
            MasonryBlocks.PINK_REDSTONE_LAMP);
    public static final RegistryObject<Item> GRAY_REDSTONE_LAMP = registerBlockItem("gray_redstone_lamp",
            MasonryBlocks.GRAY_REDSTONE_LAMP);
    public static final RegistryObject<Item> LIGHT_GRAY_REDSTONE_LAMP = registerBlockItem("light_gray_redstone_lamp",
            MasonryBlocks.LIGHT_GRAY_REDSTONE_LAMP);
    public static final RegistryObject<Item> CYAN_REDSTONE_LAMP = registerBlockItem("cyan_redstone_lamp",
            MasonryBlocks.CYAN_REDSTONE_LAMP);
    public static final RegistryObject<Item> PURPLE_REDSTONE_LAMP = registerBlockItem("purple_redstone_lamp",
            MasonryBlocks.PURPLE_REDSTONE_LAMP);
    public static final RegistryObject<Item> BLUE_REDSTONE_LAMP = registerBlockItem("blue_redstone_lamp",
            MasonryBlocks.BLUE_REDSTONE_LAMP);
    public static final RegistryObject<Item> BROWN_REDSTONE_LAMP = registerBlockItem("brown_redstone_lamp",
            MasonryBlocks.BROWN_REDSTONE_LAMP);
    public static final RegistryObject<Item> GREEN_REDSTONE_LAMP = registerBlockItem("green_redstone_lamp",
            MasonryBlocks.GREEN_REDSTONE_LAMP);
    public static final RegistryObject<Item> RED_REDSTONE_LAMP = registerBlockItem("red_redstone_lamp",
            MasonryBlocks.RED_REDSTONE_LAMP);
    public static final RegistryObject<Item> BLACK_REDSTONE_LAMP = registerBlockItem("black_redstone_lamp",
            MasonryBlocks.BLACK_REDSTONE_LAMP);

    public static final RegistryObject<Item> CASCADE_BLOCK = ITEMS.register(
            "cascade_block",
            () -> new MistBlockItem(MasonryBlocks.CASCADE_BLOCK.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> CASCADE_BLOCK_NO_MIST = ITEMS.register(
            "cascade_block_no_mist",
            () -> new MistBlockItem(MasonryBlocks.CASCADE_BLOCK_NO_MIST.get(), createBlockItemProperties()));

    public static final RegistryObject<Item> BOTTLE_OF_MIST = ITEMS.register(
            "bottle_of_mist",
            () -> new BottleOfMistItem(new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).stacksTo(64)));
    public static final RegistryObject<Item> BLACK_FACTORY_MESH = registerBlockItem("black_factory_mesh",
            MasonryBlocks.BLACK_FACTORY_MESH);
    public static final RegistryObject<Item> BLACK_FACTORY_MESH_STAIRS = registerBlockItem("black_factory_mesh_stairs",
            MasonryBlocks.BLACK_FACTORY_MESH_STAIRS);
    public static final RegistryObject<Item> BLACK_FACTORY_MESH_SLAB = registerBlockItem("black_factory_mesh_slab",
            MasonryBlocks.BLACK_FACTORY_MESH_SLAB);
    public static final RegistryObject<Item> BLACK_FACTORY_MESH_WALL = registerBlockItem("black_factory_mesh_wall",
            MasonryBlocks.BLACK_FACTORY_MESH_WALL);
    public static final RegistryObject<Item> BLUE_FACTORY_MESH = registerBlockItem("blue_factory_mesh",
            MasonryBlocks.BLUE_FACTORY_MESH);
    public static final RegistryObject<Item> BLUE_FACTORY_MESH_STAIRS = registerBlockItem("blue_factory_mesh_stairs",
            MasonryBlocks.BLUE_FACTORY_MESH_STAIRS);
    public static final RegistryObject<Item> BLUE_FACTORY_MESH_SLAB = registerBlockItem("blue_factory_mesh_slab",
            MasonryBlocks.BLUE_FACTORY_MESH_SLAB);
    public static final RegistryObject<Item> BLUE_FACTORY_MESH_WALL = registerBlockItem("blue_factory_mesh_wall",
            MasonryBlocks.BLUE_FACTORY_MESH_WALL);
    public static final RegistryObject<Item> BROWN_FACTORY_MESH = registerBlockItem("brown_factory_mesh",
            MasonryBlocks.BROWN_FACTORY_MESH);
    public static final RegistryObject<Item> BROWN_FACTORY_MESH_STAIRS = registerBlockItem("brown_factory_mesh_stairs",
            MasonryBlocks.BROWN_FACTORY_MESH_STAIRS);
    public static final RegistryObject<Item> BROWN_FACTORY_MESH_SLAB = registerBlockItem("brown_factory_mesh_slab",
            MasonryBlocks.BROWN_FACTORY_MESH_SLAB);
    public static final RegistryObject<Item> BROWN_FACTORY_MESH_WALL = registerBlockItem("brown_factory_mesh_wall",
            MasonryBlocks.BROWN_FACTORY_MESH_WALL);
    public static final RegistryObject<Item> GRAY_FACTORY_MESH = registerBlockItem("gray_factory_mesh",
            MasonryBlocks.GRAY_FACTORY_MESH);
    public static final RegistryObject<Item> GRAY_FACTORY_MESH_STAIRS = registerBlockItem("gray_factory_mesh_stairs",
            MasonryBlocks.GRAY_FACTORY_MESH_STAIRS);
    public static final RegistryObject<Item> GRAY_FACTORY_MESH_SLAB = registerBlockItem("gray_factory_mesh_slab",
            MasonryBlocks.GRAY_FACTORY_MESH_SLAB);
    public static final RegistryObject<Item> GRAY_FACTORY_MESH_WALL = registerBlockItem("gray_factory_mesh_wall",
            MasonryBlocks.GRAY_FACTORY_MESH_WALL);
    public static final RegistryObject<Item> GREEN_FACTORY_MESH = registerBlockItem("green_factory_mesh",
            MasonryBlocks.GREEN_FACTORY_MESH);
    public static final RegistryObject<Item> GREEN_FACTORY_MESH_STAIRS = registerBlockItem("green_factory_mesh_stairs",
            MasonryBlocks.GREEN_FACTORY_MESH_STAIRS);
    public static final RegistryObject<Item> GREEN_FACTORY_MESH_SLAB = registerBlockItem("green_factory_mesh_slab",
            MasonryBlocks.GREEN_FACTORY_MESH_SLAB);
    public static final RegistryObject<Item> GREEN_FACTORY_MESH_WALL = registerBlockItem("green_factory_mesh_wall",
            MasonryBlocks.GREEN_FACTORY_MESH_WALL);
    public static final RegistryObject<Item> LIGHT_GRAY_FACTORY_MESH = registerBlockItem("light_gray_factory_mesh",
            MasonryBlocks.LIGHT_GRAY_FACTORY_MESH);
    public static final RegistryObject<Item> LIGHT_GRAY_FACTORY_MESH_STAIRS = registerBlockItem(
            "light_gray_factory_mesh_stairs", MasonryBlocks.LIGHT_GRAY_FACTORY_MESH_STAIRS);
    public static final RegistryObject<Item> LIGHT_GRAY_FACTORY_MESH_SLAB = registerBlockItem(
            "light_gray_factory_mesh_slab", MasonryBlocks.LIGHT_GRAY_FACTORY_MESH_SLAB);
    public static final RegistryObject<Item> LIGHT_GRAY_FACTORY_MESH_WALL = registerBlockItem(
            "light_gray_factory_mesh_wall", MasonryBlocks.LIGHT_GRAY_FACTORY_MESH_WALL);
    public static final RegistryObject<Item> LIME_FACTORY_MESH = registerBlockItem("lime_factory_mesh",
            MasonryBlocks.LIME_FACTORY_MESH);
    public static final RegistryObject<Item> LIME_FACTORY_MESH_STAIRS = registerBlockItem("lime_factory_mesh_stairs",
            MasonryBlocks.LIME_FACTORY_MESH_STAIRS);
    public static final RegistryObject<Item> LIME_FACTORY_MESH_SLAB = registerBlockItem("lime_factory_mesh_slab",
            MasonryBlocks.LIME_FACTORY_MESH_SLAB);
    public static final RegistryObject<Item> LIME_FACTORY_MESH_WALL = registerBlockItem("lime_factory_mesh_wall",
            MasonryBlocks.LIME_FACTORY_MESH_WALL);
    public static final RegistryObject<Item> ORANGE_FACTORY_MESH = registerBlockItem("orange_factory_mesh",
            MasonryBlocks.ORANGE_FACTORY_MESH);
    public static final RegistryObject<Item> ORANGE_FACTORY_MESH_STAIRS = registerBlockItem(
            "orange_factory_mesh_stairs", MasonryBlocks.ORANGE_FACTORY_MESH_STAIRS);
    public static final RegistryObject<Item> ORANGE_FACTORY_MESH_SLAB = registerBlockItem("orange_factory_mesh_slab",
            MasonryBlocks.ORANGE_FACTORY_MESH_SLAB);
    public static final RegistryObject<Item> ORANGE_FACTORY_MESH_WALL = registerBlockItem("orange_factory_mesh_wall",
            MasonryBlocks.ORANGE_FACTORY_MESH_WALL);
    public static final RegistryObject<Item> RED_FACTORY_MESH = registerBlockItem("red_factory_mesh",
            MasonryBlocks.RED_FACTORY_MESH);
    public static final RegistryObject<Item> RED_FACTORY_MESH_STAIRS = registerBlockItem("red_factory_mesh_stairs",
            MasonryBlocks.RED_FACTORY_MESH_STAIRS);
    public static final RegistryObject<Item> RED_FACTORY_MESH_SLAB = registerBlockItem("red_factory_mesh_slab",
            MasonryBlocks.RED_FACTORY_MESH_SLAB);
    public static final RegistryObject<Item> RED_FACTORY_MESH_WALL = registerBlockItem("red_factory_mesh_wall",
            MasonryBlocks.RED_FACTORY_MESH_WALL);
    public static final RegistryObject<Item> YELLOW_FACTORY_MESH = registerBlockItem("yellow_factory_mesh",
            MasonryBlocks.YELLOW_FACTORY_MESH);
    public static final RegistryObject<Item> YELLOW_FACTORY_MESH_STAIRS = registerBlockItem(
            "yellow_factory_mesh_stairs", MasonryBlocks.YELLOW_FACTORY_MESH_STAIRS);
    public static final RegistryObject<Item> YELLOW_FACTORY_MESH_SLAB = registerBlockItem("yellow_factory_mesh_slab",
            GlassBlocks.YELLOW_FACTORY_MESH_SLAB);
    public static final RegistryObject<Item> YELLOW_FACTORY_MESH_WALL = registerBlockItem("yellow_factory_mesh_wall",
            GlassBlocks.YELLOW_FACTORY_MESH_WALL);
    public static final RegistryObject<Item> PACKED_MUD = registerBlockItem("packed_mud", GlassBlocks.PACKED_MUD);
    public static final RegistryObject<Item> PACKED_MUD_STAIRS = registerBlockItem("packed_mud_stairs",
            GlassBlocks.PACKED_MUD_STAIRS);
    public static final RegistryObject<Item> PACKED_MUD_SLAB = registerBlockItem("packed_mud_slab",
            GlassBlocks.PACKED_MUD_SLAB);
    public static final RegistryObject<Item> PACKED_MUD_WALL = registerBlockItem("packed_mud_wall",
            GlassBlocks.PACKED_MUD_WALL);
    public static final RegistryObject<Item> MUD_BRICKS = registerBlockItem("mud_bricks", GlassBlocks.MUD_BRICKS);
    public static final RegistryObject<Item> MUD_BRICK_STAIRS = registerBlockItem("mud_brick_stairs",
            GlassBlocks.MUD_BRICK_STAIRS);
    public static final RegistryObject<Item> MUD_BRICK_SLAB = registerBlockItem("mud_brick_slab",
            GlassBlocks.MUD_BRICK_SLAB);
    public static final RegistryObject<Item> MUD_BRICK_WALL = registerBlockItem("mud_brick_wall",
            GlassBlocks.MUD_BRICK_WALL);

    public static final RegistryObject<Item> ANCIENT_ASHEN_SCROLL = ITEMS.register("ancient_ashen_scroll",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB).durability(28)) {
                @Override
                public boolean isEnchantable(ItemStack stack) {
                    return false;
                }

                @Override
                public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
                    return false;
                }

                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip,
                        TooltipFlag flag) {
                    tooltip.add(new TextComponent("Found rarely in ")
                            .withStyle(ChatFormatting.GRAY)
                            .append(new TextComponent("End Cities")
                                    .withStyle(ChatFormatting.AQUA))
                            .append(new TextComponent(" & ")
                                    .withStyle(ChatFormatting.GRAY))
                            .append(new TextComponent("Woodland Mansions")
                                    .withStyle(ChatFormatting.AQUA))
                            .append(new TextComponent(", also if")
                                    .withStyle(ChatFormatting.GRAY)));
                    tooltip.add(new TextComponent("you are lucky a ")
                            .withStyle(ChatFormatting.GRAY)
                            .append(new TextComponent("Wandering Trader")
                                    .withStyle(ChatFormatting.AQUA))
                            .append(new TextComponent(" might trade")
                                    .withStyle(ChatFormatting.GRAY)));
                    tooltip.add(new TextComponent("it for a sweet price.")
                            .withStyle(ChatFormatting.GRAY));
                }
            });
}
