package com.kingodogo.buildscape.registry.items;

import com.kingodogo.buildscape.feature.copper.CopperChestItem;
import com.kingodogo.buildscape.item.GlassJarItem;
import com.kingodogo.buildscape.item.HazeBushItem;
import com.kingodogo.buildscape.registry.ModEntities;
import com.kingodogo.buildscape.registry.blocks.CopperBlocks;
import com.kingodogo.buildscape.registry.blocks.ShapedWoodBlocks;
import com.kingodogo.buildscape.registry.blocks.WoolBlocks;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraftforge.registries.RegistryObject;

import static com.kingodogo.buildscape.registry.items.ItemsRegister.ITEMS;
import static com.kingodogo.buildscape.registry.items.ItemsRegister.createBlockItemProperties;
import static com.kingodogo.buildscape.registry.items.ItemsRegister.registerBlockItem;

/**
 * The items PALE_MOSS_BLOCK_SLAB through ORANGE_LAYERED_WOOL of the registry, part 6 of 8, in registration order.
 * Named for its dominant families (copper, foliage, wood); the neighbouring families that share a run of the order
 * with them are registered here too, because the registration order must not change.
 * Load order: {@link ShapedWoodItems#init()} runs first (see the static block), so every holder registers in order whichever is touched first.
 */
public final class CopperItems {

    private CopperItems() {}

    /** Forces this class to load, which registers its items. */
    public static void init() {}

    static {
        ShapedWoodItems.init();
    }

    public static final RegistryObject<Item> PALE_MOSS_BLOCK_SLAB = registerBlockItem("pale_moss_block_slab",
            CopperBlocks.PALE_MOSS_BLOCK_SLAB);
    public static final RegistryObject<Item> PALE_MOSS_BLOCK_STAIRS = registerBlockItem("pale_moss_block_stairs",
            CopperBlocks.PALE_MOSS_BLOCK_STAIRS);
    public static final RegistryObject<Item> PALE_MOSS_BLOCK_VERTICAL_SLAB = registerBlockItem(
            "pale_moss_block_vertical_slab", CopperBlocks.PALE_MOSS_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> PALE_MOSS_CARPET = registerBlockItem("pale_moss_carpet",
            ShapedWoodBlocks.PALE_MOSS_CARPET);
    public static final RegistryObject<Item> PALE_MOSS_LAYERS = registerBlockItem("pale_moss_layers",
            CopperBlocks.PALE_MOSS_LAYERS);
    public static final RegistryObject<Item> PALE_MOSS_OVERLAY = registerBlockItem("pale_moss_overlay",
            CopperBlocks.PALE_MOSS_OVERLAY);
    public static final RegistryObject<Item> PALE_HANGING_MOSS = registerBlockItem("pale_hanging_moss",
            CopperBlocks.PALE_HANGING_MOSS);
    public static final RegistryObject<Item> CREAKING_HEART = registerBlockItem("creaking_heart",
            CopperBlocks.CREAKING_HEART);
    public static final RegistryObject<Item> RESIN_CLUMP = registerBlockItem("resin_clump", CopperBlocks.RESIN_CLUMP);
    public static final RegistryObject<Item> RESIN_BLOCK = registerBlockItem("resin_block", CopperBlocks.RESIN_BLOCK);
    public static final RegistryObject<Item> RESIN_BLOCK_SLAB = registerBlockItem("resin_block_slab",
            CopperBlocks.RESIN_BLOCK_SLAB);
    public static final RegistryObject<Item> RESIN_BLOCK_STAIRS = registerBlockItem("resin_block_stairs",
            CopperBlocks.RESIN_BLOCK_STAIRS);
    public static final RegistryObject<Item> RESIN_BLOCK_VERTICAL_SLAB = registerBlockItem("resin_block_vertical_slab",
            CopperBlocks.RESIN_BLOCK_VERTICAL_SLAB);
    public static final RegistryObject<Item> RESIN_BRICKS = registerBlockItem("resin_bricks",
            CopperBlocks.RESIN_BRICKS);
    public static final RegistryObject<Item> RESIN_BRICK_SLAB = registerBlockItem("resin_brick_slab",
            CopperBlocks.RESIN_BRICK_SLAB);
    public static final RegistryObject<Item> RESIN_BRICK_STAIRS = registerBlockItem("resin_brick_stairs",
            CopperBlocks.RESIN_BRICK_STAIRS);
    public static final RegistryObject<Item> RESIN_BRICK_WALL = registerBlockItem("resin_brick_wall",
            CopperBlocks.RESIN_BRICK_WALL);
    public static final RegistryObject<Item> RESIN_BRICK_VERTICAL_SLAB = registerBlockItem("resin_brick_vertical_slab",
            CopperBlocks.RESIN_BRICK_VERTICAL_SLAB);
    public static final RegistryObject<Item> CHISELED_RESIN_BRICKS = registerBlockItem("chiseled_resin_bricks",
            CopperBlocks.CHISELED_RESIN_BRICKS);
    public static final RegistryObject<Item> CLOSED_EYEBLOSSOM = registerBlockItem("closed_eyeblossom",
            CopperBlocks.CLOSED_EYEBLOSSOM);
    public static final RegistryObject<Item> OPEN_EYEBLOSSOM = registerBlockItem("open_eyeblossom",
            CopperBlocks.OPEN_EYEBLOSSOM);
    public static final RegistryObject<Item> WILDFLOWERS = registerBlockItem("wildflowers", CopperBlocks.WILDFLOWERS);
    public static final RegistryObject<Item> LEAF_LITTER = registerBlockItem("leaf_litter", CopperBlocks.LEAF_LITTER);
    public static final RegistryObject<Item> CACTUS_FLOWER = registerBlockItem("cactus_flower",
            CopperBlocks.CACTUS_FLOWER);
    public static final RegistryObject<Item> BUSH = registerBlockItem("bush", CopperBlocks.BUSH);
    public static final RegistryObject<Item> RED_BUSH = registerBlockItem("red_bush", CopperBlocks.RED_BUSH);
    public static final RegistryObject<Item> WHITE_HAZE_BUSH = ITEMS.register("white_haze_bush",
            () -> new HazeBushItem(CopperBlocks.WHITE_HAZE_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_HAZE_BUSH = ITEMS.register("orange_haze_bush",
            () -> new HazeBushItem(CopperBlocks.ORANGE_HAZE_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_HAZE_BUSH = ITEMS.register("magenta_haze_bush",
            () -> new HazeBushItem(CopperBlocks.MAGENTA_HAZE_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_HAZE_BUSH = ITEMS.register("light_blue_haze_bush",
            () -> new HazeBushItem(CopperBlocks.LIGHT_BLUE_HAZE_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_HAZE_BUSH = ITEMS.register("yellow_haze_bush",
            () -> new HazeBushItem(CopperBlocks.YELLOW_HAZE_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_HAZE_BUSH = ITEMS.register("lime_haze_bush",
            () -> new HazeBushItem(CopperBlocks.LIME_HAZE_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_HAZE_BUSH = ITEMS.register("pink_haze_bush",
            () -> new HazeBushItem(CopperBlocks.PINK_HAZE_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_HAZE_BUSH = ITEMS.register("gray_haze_bush",
            () -> new HazeBushItem(CopperBlocks.GRAY_HAZE_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_HAZE_BUSH = ITEMS.register("light_gray_haze_bush",
            () -> new HazeBushItem(CopperBlocks.LIGHT_GRAY_HAZE_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_HAZE_BUSH = ITEMS.register("cyan_haze_bush",
            () -> new HazeBushItem(CopperBlocks.CYAN_HAZE_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_HAZE_BUSH = ITEMS.register("purple_haze_bush",
            () -> new HazeBushItem(CopperBlocks.PURPLE_HAZE_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_HAZE_BUSH = ITEMS.register("blue_haze_bush",
            () -> new HazeBushItem(CopperBlocks.BLUE_HAZE_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_HAZE_BUSH = ITEMS.register("brown_haze_bush",
            () -> new HazeBushItem(CopperBlocks.BROWN_HAZE_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_HAZE_BUSH = ITEMS.register("green_haze_bush",
            () -> new HazeBushItem(CopperBlocks.GREEN_HAZE_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_HAZE_BUSH = ITEMS.register("red_haze_bush",
            () -> new HazeBushItem(CopperBlocks.RED_HAZE_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_HAZE_BUSH = ITEMS.register("black_haze_bush",
            () -> new HazeBushItem(CopperBlocks.BLACK_HAZE_BUSH.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> FIREFLY_BUSH = registerBlockItem("firefly_bush",
            CopperBlocks.FIREFLY_BUSH);
    public static final RegistryObject<Item> DRY_GRASS = registerBlockItem("dry_grass", CopperBlocks.DRY_GRASS);
    public static final RegistryObject<Item> TALL_DRY_GRASS = registerBlockItem("tall_dry_grass",
            CopperBlocks.TALL_DRY_GRASS);
    public static final RegistryObject<Item> GOLDEN_DANDELION = registerBlockItem("golden_dandelion",
            CopperBlocks.GOLDEN_DANDELION);
    public static final RegistryObject<Item> OCHRE_FROGLIGHT = registerBlockItem("ochre_froglight",
            CopperBlocks.OCHRE_FROGLIGHT);
    public static final RegistryObject<Item> PEARLESCENT_FROGLIGHT = registerBlockItem("pearlescent_froglight",
            CopperBlocks.PEARLESCENT_FROGLIGHT);
    public static final RegistryObject<Item> VERDANT_FROGLIGHT = registerBlockItem("verdant_froglight",
            CopperBlocks.VERDANT_FROGLIGHT);
    public static final RegistryObject<Item> GLEAMING_FROGLIGHT = registerBlockItem("gleaming_froglight",
            CopperBlocks.GLEAMING_FROGLIGHT);
    public static final RegistryObject<Item> IVORY_FROGLIGHT = registerBlockItem("ivory_froglight",
            CopperBlocks.IVORY_FROGLIGHT);
    public static final RegistryObject<Item> ASHEN_FROGLIGHT = registerBlockItem("ashen_froglight",
            CopperBlocks.ASHEN_FROGLIGHT);
    public static final RegistryObject<Item> ONYX_FROGLIGHT = registerBlockItem("onyx_froglight",
            CopperBlocks.ONYX_FROGLIGHT);
    public static final RegistryObject<Item> DUSKY_FROGLIGHT = registerBlockItem("dusky_froglight",
            CopperBlocks.DUSKY_FROGLIGHT);
    public static final RegistryObject<Item> SCARLET_FROGLIGHT = registerBlockItem("scarlet_froglight",
            CopperBlocks.SCARLET_FROGLIGHT);
    public static final RegistryObject<Item> RUSSET_FROGLIGHT = registerBlockItem("russet_froglight",
            CopperBlocks.RUSSET_FROGLIGHT);
    public static final RegistryObject<Item> VERIDIAN_FROGLIGHT = registerBlockItem("veridian_froglight",
            CopperBlocks.VERIDIAN_FROGLIGHT);
    public static final RegistryObject<Item> TIDAL_FROGLIGHT = registerBlockItem("tidal_froglight",
            CopperBlocks.TIDAL_FROGLIGHT);
    public static final RegistryObject<Item> CERULEAN_FROGLIGHT = registerBlockItem("cerulean_froglight",
            CopperBlocks.CERULEAN_FROGLIGHT);
    public static final RegistryObject<Item> AZURE_FROGLIGHT = registerBlockItem("azure_froglight",
            CopperBlocks.AZURE_FROGLIGHT);
    public static final RegistryObject<Item> IRIDESCENT_FROGLIGHT = registerBlockItem("iridescent_froglight",
            CopperBlocks.IRIDESCENT_FROGLIGHT);
    public static final RegistryObject<Item> CORALINE_FROGLIGHT = registerBlockItem("coraline_froglight",
            CopperBlocks.CORALINE_FROGLIGHT);
    public static final RegistryObject<Item> SCULK = registerBlockItem("sculk", CopperBlocks.SCULK);
    public static final RegistryObject<Item> SCULK_SLAB = registerBlockItem("sculk_slab", CopperBlocks.SCULK_SLAB);
    public static final RegistryObject<Item> SCULK_STAIRS = registerBlockItem("sculk_stairs",
            CopperBlocks.SCULK_STAIRS);
    public static final RegistryObject<Item> SCULK_WALL = registerBlockItem("sculk_wall", CopperBlocks.SCULK_WALL);
    public static final RegistryObject<Item> SCULK_VERTICAL_SLAB = registerBlockItem("sculk_vertical_slab",
            CopperBlocks.SCULK_VERTICAL_SLAB);
    public static final RegistryObject<Item> SCULK_VEIN = registerBlockItem("sculk_vein", CopperBlocks.SCULK_VEIN);
    public static final RegistryObject<Item> SCULK_CATALYST = registerBlockItem("sculk_catalyst",
            CopperBlocks.SCULK_CATALYST);
    public static final RegistryObject<Item> SCULK_SHRIEKER = registerBlockItem("sculk_shrieker",
            CopperBlocks.SCULK_SHRIEKER);
    public static final RegistryObject<Item> SCULK_SENSOR = registerBlockItem("sculk_sensor",
            CopperBlocks.SCULK_SENSOR);

    public static final RegistryObject<Item> COPPER_TORCH = ITEMS.register("copper_torch",
            () -> new StandingAndWallBlockItem(CopperBlocks.COPPER_TORCH.get(), CopperBlocks.COPPER_WALL_TORCH.get(),
                    createBlockItemProperties()));
    public static final RegistryObject<Item> COPPER_ROD = registerBlockItem("copper_rod", CopperBlocks.COPPER_ROD);
    public static final RegistryObject<Item> WAXED_COPPER_ROD = registerBlockItem("waxed_copper_rod",
            CopperBlocks.WAXED_COPPER_ROD);
    public static final RegistryObject<Item> EXPOSED_COPPER_ROD = registerBlockItem("exposed_copper_rod",
            CopperBlocks.EXPOSED_COPPER_ROD);
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_ROD = registerBlockItem("waxed_exposed_copper_rod",
            CopperBlocks.WAXED_EXPOSED_COPPER_ROD);
    public static final RegistryObject<Item> WEATHERED_COPPER_ROD = registerBlockItem("weathered_copper_rod",
            CopperBlocks.WEATHERED_COPPER_ROD);
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_ROD = registerBlockItem(
            "waxed_weathered_copper_rod", CopperBlocks.WAXED_WEATHERED_COPPER_ROD);
    public static final RegistryObject<Item> OXIDIZED_COPPER_ROD = registerBlockItem("oxidized_copper_rod",
            CopperBlocks.OXIDIZED_COPPER_ROD);
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_ROD = registerBlockItem("waxed_oxidized_copper_rod",
            CopperBlocks.WAXED_OXIDIZED_COPPER_ROD);
    public static final RegistryObject<Item> COPPER_LANTERN = registerBlockItem("copper_lantern",
            CopperBlocks.COPPER_LANTERN);
    public static final RegistryObject<Item> WAXED_COPPER_LANTERN = registerBlockItem("waxed_copper_lantern",
            CopperBlocks.WAXED_COPPER_LANTERN);
    public static final RegistryObject<Item> EXPOSED_COPPER_LANTERN = registerBlockItem("exposed_copper_lantern",
            CopperBlocks.EXPOSED_COPPER_LANTERN);
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_LANTERN = registerBlockItem(
            "waxed_exposed_copper_lantern", CopperBlocks.WAXED_EXPOSED_COPPER_LANTERN);
    public static final RegistryObject<Item> WEATHERED_COPPER_LANTERN = registerBlockItem("weathered_copper_lantern",
            CopperBlocks.WEATHERED_COPPER_LANTERN);
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_LANTERN = registerBlockItem(
            "waxed_weathered_copper_lantern", CopperBlocks.WAXED_WEATHERED_COPPER_LANTERN);
    public static final RegistryObject<Item> OXIDIZED_COPPER_LANTERN = registerBlockItem("oxidized_copper_lantern",
            CopperBlocks.OXIDIZED_COPPER_LANTERN);
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_LANTERN = registerBlockItem(
            "waxed_oxidized_copper_lantern", CopperBlocks.WAXED_OXIDIZED_COPPER_LANTERN);
    public static final RegistryObject<Item> SLIT_COPPER = registerBlockItem("slit_copper", CopperBlocks.SLIT_COPPER);
    public static final RegistryObject<Item> WAXED_SLIT_COPPER = registerBlockItem("waxed_slit_copper",
            CopperBlocks.WAXED_SLIT_COPPER);
    public static final RegistryObject<Item> EXPOSED_SLIT_COPPER = registerBlockItem("exposed_slit_copper",
            CopperBlocks.EXPOSED_SLIT_COPPER);
    public static final RegistryObject<Item> WAXED_EXPOSED_SLIT_COPPER = registerBlockItem("waxed_exposed_slit_copper",
            CopperBlocks.WAXED_EXPOSED_SLIT_COPPER);
    public static final RegistryObject<Item> WEATHERED_SLIT_COPPER = registerBlockItem("weathered_slit_copper",
            CopperBlocks.WEATHERED_SLIT_COPPER);
    public static final RegistryObject<Item> WAXED_WEATHERED_SLIT_COPPER = registerBlockItem(
            "waxed_weathered_slit_copper", CopperBlocks.WAXED_WEATHERED_SLIT_COPPER);
    public static final RegistryObject<Item> OXIDIZED_SLIT_COPPER = registerBlockItem("oxidized_slit_copper",
            CopperBlocks.OXIDIZED_SLIT_COPPER);
    public static final RegistryObject<Item> WAXED_OXIDIZED_SLIT_COPPER = registerBlockItem(
            "waxed_oxidized_slit_copper", CopperBlocks.WAXED_OXIDIZED_SLIT_COPPER);
    public static final RegistryObject<Item> SLIT_COPPER_STAIRS = registerBlockItem("slit_copper_stairs",
            CopperBlocks.SLIT_COPPER_STAIRS);
    public static final RegistryObject<Item> WAXED_SLIT_COPPER_STAIRS = registerBlockItem("waxed_slit_copper_stairs",
            CopperBlocks.WAXED_SLIT_COPPER_STAIRS);
    public static final RegistryObject<Item> EXPOSED_SLIT_COPPER_STAIRS = registerBlockItem(
            "exposed_slit_copper_stairs", CopperBlocks.EXPOSED_SLIT_COPPER_STAIRS);
    public static final RegistryObject<Item> WAXED_EXPOSED_SLIT_COPPER_STAIRS = registerBlockItem(
            "waxed_exposed_slit_copper_stairs", CopperBlocks.WAXED_EXPOSED_SLIT_COPPER_STAIRS);
    public static final RegistryObject<Item> WEATHERED_SLIT_COPPER_STAIRS = registerBlockItem(
            "weathered_slit_copper_stairs", CopperBlocks.WEATHERED_SLIT_COPPER_STAIRS);
    public static final RegistryObject<Item> WAXED_WEATHERED_SLIT_COPPER_STAIRS = registerBlockItem(
            "waxed_weathered_slit_copper_stairs", CopperBlocks.WAXED_WEATHERED_SLIT_COPPER_STAIRS);
    public static final RegistryObject<Item> OXIDIZED_SLIT_COPPER_STAIRS = registerBlockItem(
            "oxidized_slit_copper_stairs", CopperBlocks.OXIDIZED_SLIT_COPPER_STAIRS);
    public static final RegistryObject<Item> WAXED_OXIDIZED_SLIT_COPPER_STAIRS = registerBlockItem(
            "waxed_oxidized_slit_copper_stairs", CopperBlocks.WAXED_OXIDIZED_SLIT_COPPER_STAIRS);
    public static final RegistryObject<Item> SLIT_COPPER_SLAB = registerBlockItem("slit_copper_slab",
            CopperBlocks.SLIT_COPPER_SLAB);
    public static final RegistryObject<Item> WAXED_SLIT_COPPER_SLAB = registerBlockItem("waxed_slit_copper_slab",
            CopperBlocks.WAXED_SLIT_COPPER_SLAB);
    public static final RegistryObject<Item> EXPOSED_SLIT_COPPER_SLAB = registerBlockItem("exposed_slit_copper_slab",
            CopperBlocks.EXPOSED_SLIT_COPPER_SLAB);
    public static final RegistryObject<Item> WAXED_EXPOSED_SLIT_COPPER_SLAB = registerBlockItem(
            "waxed_exposed_slit_copper_slab", CopperBlocks.WAXED_EXPOSED_SLIT_COPPER_SLAB);
    public static final RegistryObject<Item> WEATHERED_SLIT_COPPER_SLAB = registerBlockItem(
            "weathered_slit_copper_slab", CopperBlocks.WEATHERED_SLIT_COPPER_SLAB);
    public static final RegistryObject<Item> WAXED_WEATHERED_SLIT_COPPER_SLAB = registerBlockItem(
            "waxed_weathered_slit_copper_slab", CopperBlocks.WAXED_WEATHERED_SLIT_COPPER_SLAB);
    public static final RegistryObject<Item> OXIDIZED_SLIT_COPPER_SLAB = registerBlockItem("oxidized_slit_copper_slab",
            CopperBlocks.OXIDIZED_SLIT_COPPER_SLAB);
    public static final RegistryObject<Item> WAXED_OXIDIZED_SLIT_COPPER_SLAB = registerBlockItem(
            "waxed_oxidized_slit_copper_slab", CopperBlocks.WAXED_OXIDIZED_SLIT_COPPER_SLAB);
    public static final RegistryObject<Item> SLIT_COPPER_VERTICAL_SLAB = registerBlockItem("slit_copper_vertical_slab",
            CopperBlocks.SLIT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> WAXED_SLIT_COPPER_VERTICAL_SLAB = registerBlockItem(
            "waxed_slit_copper_vertical_slab", CopperBlocks.WAXED_SLIT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> EXPOSED_SLIT_COPPER_VERTICAL_SLAB = registerBlockItem(
            "exposed_slit_copper_vertical_slab", CopperBlocks.EXPOSED_SLIT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> WAXED_EXPOSED_SLIT_COPPER_VERTICAL_SLAB = registerBlockItem(
            "waxed_exposed_slit_copper_vertical_slab", CopperBlocks.WAXED_EXPOSED_SLIT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> WEATHERED_SLIT_COPPER_VERTICAL_SLAB = registerBlockItem(
            "weathered_slit_copper_vertical_slab", CopperBlocks.WEATHERED_SLIT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> WAXED_WEATHERED_SLIT_COPPER_VERTICAL_SLAB = registerBlockItem(
            "waxed_weathered_slit_copper_vertical_slab", CopperBlocks.WAXED_WEATHERED_SLIT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> OXIDIZED_SLIT_COPPER_VERTICAL_SLAB = registerBlockItem(
            "oxidized_slit_copper_vertical_slab", CopperBlocks.OXIDIZED_SLIT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> WAXED_OXIDIZED_SLIT_COPPER_VERTICAL_SLAB = registerBlockItem(
            "waxed_oxidized_slit_copper_vertical_slab", CopperBlocks.WAXED_OXIDIZED_SLIT_COPPER_VERTICAL_SLAB);
    public static final RegistryObject<Item> CHISELED_COPPER = registerBlockItem("chiseled_copper",
            CopperBlocks.CHISELED_COPPER);
    public static final RegistryObject<Item> WAXED_CHISELED_COPPER = registerBlockItem("waxed_chiseled_copper",
            CopperBlocks.WAXED_CHISELED_COPPER);
    public static final RegistryObject<Item> EXPOSED_CHISELED_COPPER = registerBlockItem("exposed_chiseled_copper",
            CopperBlocks.EXPOSED_CHISELED_COPPER);
    public static final RegistryObject<Item> WAXED_EXPOSED_CHISELED_COPPER = registerBlockItem(
            "waxed_exposed_chiseled_copper", CopperBlocks.WAXED_EXPOSED_CHISELED_COPPER);
    public static final RegistryObject<Item> WEATHERED_CHISELED_COPPER = registerBlockItem("weathered_chiseled_copper",
            CopperBlocks.WEATHERED_CHISELED_COPPER);
    public static final RegistryObject<Item> WAXED_WEATHERED_CHISELED_COPPER = registerBlockItem(
            "waxed_weathered_chiseled_copper", CopperBlocks.WAXED_WEATHERED_CHISELED_COPPER);
    public static final RegistryObject<Item> OXIDIZED_CHISELED_COPPER = registerBlockItem("oxidized_chiseled_copper",
            CopperBlocks.OXIDIZED_CHISELED_COPPER);
    public static final RegistryObject<Item> WAXED_OXIDIZED_CHISELED_COPPER = registerBlockItem(
            "waxed_oxidized_chiseled_copper", CopperBlocks.WAXED_OXIDIZED_CHISELED_COPPER);
    public static final RegistryObject<Item> COPPER_GRATE = registerBlockItem("copper_grate",
            CopperBlocks.COPPER_GRATE);
    public static final RegistryObject<Item> WAXED_COPPER_GRATE = registerBlockItem("waxed_copper_grate",
            CopperBlocks.WAXED_COPPER_GRATE);
    public static final RegistryObject<Item> EXPOSED_COPPER_GRATE = registerBlockItem("exposed_copper_grate",
            CopperBlocks.EXPOSED_COPPER_GRATE);
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_GRATE = registerBlockItem(
            "waxed_exposed_copper_grate", CopperBlocks.WAXED_EXPOSED_COPPER_GRATE);
    public static final RegistryObject<Item> WEATHERED_COPPER_GRATE = registerBlockItem("weathered_copper_grate",
            CopperBlocks.WEATHERED_COPPER_GRATE);
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_GRATE = registerBlockItem(
            "waxed_weathered_copper_grate", CopperBlocks.WAXED_WEATHERED_COPPER_GRATE);
    public static final RegistryObject<Item> OXIDIZED_COPPER_GRATE = registerBlockItem("oxidized_copper_grate",
            CopperBlocks.OXIDIZED_COPPER_GRATE);
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_GRATE = registerBlockItem(
            "waxed_oxidized_copper_grate", CopperBlocks.WAXED_OXIDIZED_COPPER_GRATE);
    public static final RegistryObject<Item> COPPER_BULB = registerBlockItem("copper_bulb", CopperBlocks.COPPER_BULB);
    public static final RegistryObject<Item> WAXED_COPPER_BULB = registerBlockItem("waxed_copper_bulb",
            CopperBlocks.WAXED_COPPER_BULB);
    public static final RegistryObject<Item> EXPOSED_COPPER_BULB = registerBlockItem("exposed_copper_bulb",
            CopperBlocks.EXPOSED_COPPER_BULB);
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_BULB = registerBlockItem("waxed_exposed_copper_bulb",
            CopperBlocks.WAXED_EXPOSED_COPPER_BULB);
    public static final RegistryObject<Item> WEATHERED_COPPER_BULB = registerBlockItem("weathered_copper_bulb",
            CopperBlocks.WEATHERED_COPPER_BULB);
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_BULB = registerBlockItem(
            "waxed_weathered_copper_bulb", CopperBlocks.WAXED_WEATHERED_COPPER_BULB);
    public static final RegistryObject<Item> OXIDIZED_COPPER_BULB = registerBlockItem("oxidized_copper_bulb",
            CopperBlocks.OXIDIZED_COPPER_BULB);
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_BULB = registerBlockItem(
            "waxed_oxidized_copper_bulb", CopperBlocks.WAXED_OXIDIZED_COPPER_BULB);

    public static final RegistryObject<Item> COPPER_DOOR = ITEMS.register("copper_door",
            () -> new DoubleHighBlockItem(CopperBlocks.COPPER_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_COPPER_DOOR = ITEMS.register("waxed_copper_door",
            () -> new DoubleHighBlockItem(CopperBlocks.WAXED_COPPER_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> EXPOSED_COPPER_DOOR = ITEMS.register("exposed_copper_door",
            () -> new DoubleHighBlockItem(CopperBlocks.EXPOSED_COPPER_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_DOOR = ITEMS.register("waxed_exposed_copper_door",
            () -> new DoubleHighBlockItem(CopperBlocks.WAXED_EXPOSED_COPPER_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WEATHERED_COPPER_DOOR = ITEMS.register("weathered_copper_door",
            () -> new DoubleHighBlockItem(CopperBlocks.WEATHERED_COPPER_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_DOOR = ITEMS.register("waxed_weathered_copper_door",
            () -> new DoubleHighBlockItem(CopperBlocks.WAXED_WEATHERED_COPPER_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OXIDIZED_COPPER_DOOR = ITEMS.register("oxidized_copper_door",
            () -> new DoubleHighBlockItem(CopperBlocks.OXIDIZED_COPPER_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_DOOR = ITEMS.register("waxed_oxidized_copper_door",
            () -> new DoubleHighBlockItem(CopperBlocks.WAXED_OXIDIZED_COPPER_DOOR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> COPPER_TRAPDOOR = registerBlockItem("copper_trapdoor",
            CopperBlocks.COPPER_TRAPDOOR);
    public static final RegistryObject<Item> WAXED_COPPER_TRAPDOOR = registerBlockItem("waxed_copper_trapdoor",
            CopperBlocks.WAXED_COPPER_TRAPDOOR);
    public static final RegistryObject<Item> EXPOSED_COPPER_TRAPDOOR = registerBlockItem("exposed_copper_trapdoor",
            CopperBlocks.EXPOSED_COPPER_TRAPDOOR);
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_TRAPDOOR = registerBlockItem(
            "waxed_exposed_copper_trapdoor", CopperBlocks.WAXED_EXPOSED_COPPER_TRAPDOOR);
    public static final RegistryObject<Item> WEATHERED_COPPER_TRAPDOOR = registerBlockItem("weathered_copper_trapdoor",
            CopperBlocks.WEATHERED_COPPER_TRAPDOOR);
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_TRAPDOOR = registerBlockItem(
            "waxed_weathered_copper_trapdoor", CopperBlocks.WAXED_WEATHERED_COPPER_TRAPDOOR);
    public static final RegistryObject<Item> OXIDIZED_COPPER_TRAPDOOR = registerBlockItem("oxidized_copper_trapdoor",
            CopperBlocks.OXIDIZED_COPPER_TRAPDOOR);
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_TRAPDOOR = registerBlockItem(
            "waxed_oxidized_copper_trapdoor", CopperBlocks.WAXED_OXIDIZED_COPPER_TRAPDOOR);
    public static final RegistryObject<Item> COPPER_BARS = registerBlockItem("copper_bars", CopperBlocks.COPPER_BARS);
    public static final RegistryObject<Item> WAXED_COPPER_BARS = registerBlockItem("waxed_copper_bars",
            CopperBlocks.WAXED_COPPER_BARS);
    public static final RegistryObject<Item> EXPOSED_COPPER_BARS = registerBlockItem("exposed_copper_bars",
            CopperBlocks.EXPOSED_COPPER_BARS);
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_BARS = registerBlockItem("waxed_exposed_copper_bars",
            CopperBlocks.WAXED_EXPOSED_COPPER_BARS);
    public static final RegistryObject<Item> WEATHERED_COPPER_BARS = registerBlockItem("weathered_copper_bars",
            CopperBlocks.WEATHERED_COPPER_BARS);
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_BARS = registerBlockItem(
            "waxed_weathered_copper_bars", CopperBlocks.WAXED_WEATHERED_COPPER_BARS);
    public static final RegistryObject<Item> OXIDIZED_COPPER_BARS = registerBlockItem("oxidized_copper_bars",
            CopperBlocks.OXIDIZED_COPPER_BARS);
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_BARS = registerBlockItem(
            "waxed_oxidized_copper_bars", CopperBlocks.WAXED_OXIDIZED_COPPER_BARS);
    public static final RegistryObject<Item> GOLD_BARS = registerBlockItem("gold_bars", CopperBlocks.GOLD_BARS);
    public static final RegistryObject<Item> STEEL_BARS = registerBlockItem("steel_bars", CopperBlocks.STEEL_BARS);
    public static final RegistryObject<Item> IRON_MESH = registerBlockItem("iron_mesh", CopperBlocks.IRON_MESH);
    public static final RegistryObject<Item> GOLD_MESH = registerBlockItem("gold_mesh", CopperBlocks.GOLD_MESH);
    public static final RegistryObject<Item> STEEL_MESH = registerBlockItem("steel_mesh", CopperBlocks.STEEL_MESH);
    public static final RegistryObject<Item> COPPER_MESH = registerBlockItem("copper_mesh", CopperBlocks.COPPER_MESH);
    public static final RegistryObject<Item> WAXED_COPPER_MESH = registerBlockItem("waxed_copper_mesh",
            CopperBlocks.WAXED_COPPER_MESH);
    public static final RegistryObject<Item> EXPOSED_COPPER_MESH = registerBlockItem("exposed_copper_mesh",
            CopperBlocks.EXPOSED_COPPER_MESH);
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_MESH = registerBlockItem("waxed_exposed_copper_mesh",
            CopperBlocks.WAXED_EXPOSED_COPPER_MESH);
    public static final RegistryObject<Item> WEATHERED_COPPER_MESH = registerBlockItem("weathered_copper_mesh",
            CopperBlocks.WEATHERED_COPPER_MESH);
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_MESH = registerBlockItem(
            "waxed_weathered_copper_mesh", CopperBlocks.WAXED_WEATHERED_COPPER_MESH);
    public static final RegistryObject<Item> OXIDIZED_COPPER_MESH = registerBlockItem("oxidized_copper_mesh",
            CopperBlocks.OXIDIZED_COPPER_MESH);
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_MESH = registerBlockItem(
            "waxed_oxidized_copper_mesh", CopperBlocks.WAXED_OXIDIZED_COPPER_MESH);
    public static final RegistryObject<Item> COPPER_BUTTON = registerBlockItem("copper_button",
            CopperBlocks.COPPER_BUTTON);
    public static final RegistryObject<Item> WAXED_COPPER_BUTTON = registerBlockItem("waxed_copper_button",
            CopperBlocks.WAXED_COPPER_BUTTON);
    public static final RegistryObject<Item> EXPOSED_COPPER_BUTTON = registerBlockItem("exposed_copper_button",
            CopperBlocks.EXPOSED_COPPER_BUTTON);
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_BUTTON = registerBlockItem(
            "waxed_exposed_copper_button", CopperBlocks.WAXED_EXPOSED_COPPER_BUTTON);
    public static final RegistryObject<Item> WEATHERED_COPPER_BUTTON = registerBlockItem("weathered_copper_button",
            CopperBlocks.WEATHERED_COPPER_BUTTON);
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_BUTTON = registerBlockItem(
            "waxed_weathered_copper_button", CopperBlocks.WAXED_WEATHERED_COPPER_BUTTON);
    public static final RegistryObject<Item> OXIDIZED_COPPER_BUTTON = registerBlockItem("oxidized_copper_button",
            CopperBlocks.OXIDIZED_COPPER_BUTTON);
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_BUTTON = registerBlockItem(
            "waxed_oxidized_copper_button", CopperBlocks.WAXED_OXIDIZED_COPPER_BUTTON);
    public static final RegistryObject<Item> COPPER_BOLTS = registerBlockItem("copper_bolts",
            CopperBlocks.COPPER_BOLTS);
    public static final RegistryObject<Item> WAXED_COPPER_BOLTS = registerBlockItem("waxed_copper_bolts",
            CopperBlocks.WAXED_COPPER_BOLTS);
    public static final RegistryObject<Item> EXPOSED_COPPER_BOLTS = registerBlockItem("exposed_copper_bolts",
            CopperBlocks.EXPOSED_COPPER_BOLTS);
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_BOLTS = registerBlockItem(
            "waxed_exposed_copper_bolts", CopperBlocks.WAXED_EXPOSED_COPPER_BOLTS);
    public static final RegistryObject<Item> WEATHERED_COPPER_BOLTS = registerBlockItem("weathered_copper_bolts",
            CopperBlocks.WEATHERED_COPPER_BOLTS);
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_BOLTS = registerBlockItem(
            "waxed_weathered_copper_bolts", CopperBlocks.WAXED_WEATHERED_COPPER_BOLTS);
    public static final RegistryObject<Item> OXIDIZED_COPPER_BOLTS = registerBlockItem("oxidized_copper_bolts",
            CopperBlocks.OXIDIZED_COPPER_BOLTS);
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_BOLTS = registerBlockItem(
            "waxed_oxidized_copper_bolts", CopperBlocks.WAXED_OXIDIZED_COPPER_BOLTS);
    public static final RegistryObject<Item> COPPER_PRESSURE_PLATE = registerBlockItem("copper_pressure_plate",
            CopperBlocks.COPPER_PRESSURE_PLATE);
    public static final RegistryObject<Item> WAXED_COPPER_PRESSURE_PLATE = registerBlockItem(
            "waxed_copper_pressure_plate", CopperBlocks.WAXED_COPPER_PRESSURE_PLATE);
    public static final RegistryObject<Item> EXPOSED_COPPER_PRESSURE_PLATE = registerBlockItem(
            "exposed_copper_pressure_plate", CopperBlocks.EXPOSED_COPPER_PRESSURE_PLATE);
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_PRESSURE_PLATE = registerBlockItem(
            "waxed_exposed_copper_pressure_plate", CopperBlocks.WAXED_EXPOSED_COPPER_PRESSURE_PLATE);
    public static final RegistryObject<Item> WEATHERED_COPPER_PRESSURE_PLATE = registerBlockItem(
            "weathered_copper_pressure_plate", CopperBlocks.WEATHERED_COPPER_PRESSURE_PLATE);
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_PRESSURE_PLATE = registerBlockItem(
            "waxed_weathered_copper_pressure_plate", CopperBlocks.WAXED_WEATHERED_COPPER_PRESSURE_PLATE);
    public static final RegistryObject<Item> OXIDIZED_COPPER_PRESSURE_PLATE = registerBlockItem(
            "oxidized_copper_pressure_plate", CopperBlocks.OXIDIZED_COPPER_PRESSURE_PLATE);
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_PRESSURE_PLATE = registerBlockItem(
            "waxed_oxidized_copper_pressure_plate", CopperBlocks.WAXED_OXIDIZED_COPPER_PRESSURE_PLATE);

    public static final RegistryObject<Item> WANDERING_HOMEMAKER_SPAWN_EGG = ITEMS.register(
            "wandering_homemaker_spawn_egg",
            () -> new WanderingHomemakerSpawnEggItem(
                    ModEntities.WANDERING_HOMEMAKER,
                    0x5c3c24,
                    0xe4b484,
                    createBlockItemProperties()));

    public static final RegistryObject<Item> FESTIVE_WANDERING_HOMEMAKER_SPAWN_EGG = ITEMS.register(
            "festive_wandering_homemaker_spawn_egg",
            () -> new FestiveWanderingHomemakerSpawnEggItem(
                    ModEntities.FESTIVE_WANDERING_HOMEMAKER,
                    0x990000,
                    0x009900,
                    createBlockItemProperties()));

    public static final RegistryObject<Item> GLASS_JAR = ITEMS.register("glass_jar",
            () -> new GlassJarItem(CopperBlocks.GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WHITE_GLASS_JAR = ITEMS.register("white_glass_jar",
            () -> new GlassJarItem(CopperBlocks.WHITE_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ORANGE_GLASS_JAR = ITEMS.register("orange_glass_jar",
            () -> new GlassJarItem(CopperBlocks.ORANGE_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> MAGENTA_GLASS_JAR = ITEMS.register("magenta_glass_jar",
            () -> new GlassJarItem(CopperBlocks.MAGENTA_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_BLUE_GLASS_JAR = ITEMS.register("light_blue_glass_jar",
            () -> new GlassJarItem(CopperBlocks.LIGHT_BLUE_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> YELLOW_GLASS_JAR = ITEMS.register("yellow_glass_jar",
            () -> new GlassJarItem(CopperBlocks.YELLOW_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIME_GLASS_JAR = ITEMS.register("lime_glass_jar",
            () -> new GlassJarItem(CopperBlocks.LIME_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PINK_GLASS_JAR = ITEMS.register("pink_glass_jar",
            () -> new GlassJarItem(CopperBlocks.PINK_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GRAY_GLASS_JAR = ITEMS.register("gray_glass_jar",
            () -> new GlassJarItem(CopperBlocks.GRAY_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> LIGHT_GRAY_GLASS_JAR = ITEMS.register("light_gray_glass_jar",
            () -> new GlassJarItem(CopperBlocks.LIGHT_GRAY_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> CYAN_GLASS_JAR = ITEMS.register("cyan_glass_jar",
            () -> new GlassJarItem(CopperBlocks.CYAN_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> PURPLE_GLASS_JAR = ITEMS.register("purple_glass_jar",
            () -> new GlassJarItem(CopperBlocks.PURPLE_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLUE_GLASS_JAR = ITEMS.register("blue_glass_jar",
            () -> new GlassJarItem(CopperBlocks.BLUE_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BROWN_GLASS_JAR = ITEMS.register("brown_glass_jar",
            () -> new GlassJarItem(CopperBlocks.BROWN_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> GREEN_GLASS_JAR = ITEMS.register("green_glass_jar",
            () -> new GlassJarItem(CopperBlocks.GREEN_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> RED_GLASS_JAR = ITEMS.register("red_glass_jar",
            () -> new GlassJarItem(CopperBlocks.RED_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> BLACK_GLASS_JAR = ITEMS.register("black_glass_jar",
            () -> new GlassJarItem(CopperBlocks.BLACK_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> TINTED_GLASS_JAR = ITEMS.register("tinted_glass_jar",
            () -> new GlassJarItem(CopperBlocks.TINTED_GLASS_JAR.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> ACACIA_LADDER = registerBlockItem("acacia_ladder",
            CopperBlocks.ACACIA_LADDER);
    public static final RegistryObject<Item> ASHPEN_BLACK_LADDER = registerBlockItem("ashpen_black_ladder",
            CopperBlocks.ASHPEN_BLACK_LADDER);
    public static final RegistryObject<Item> ASHPEN_BLUE_LADDER = registerBlockItem("ashpen_blue_ladder",
            CopperBlocks.ASHPEN_BLUE_LADDER);
    public static final RegistryObject<Item> ASHPEN_BROWN_LADDER = registerBlockItem("ashpen_brown_ladder",
            CopperBlocks.ASHPEN_BROWN_LADDER);
    public static final RegistryObject<Item> ASHPEN_CYAN_LADDER = registerBlockItem("ashpen_cyan_ladder",
            CopperBlocks.ASHPEN_CYAN_LADDER);
    public static final RegistryObject<Item> ASHPEN_GRAY_LADDER = registerBlockItem("ashpen_gray_ladder",
            CopperBlocks.ASHPEN_GRAY_LADDER);
    public static final RegistryObject<Item> ASHPEN_GREEN_LADDER = registerBlockItem("ashpen_green_ladder",
            CopperBlocks.ASHPEN_GREEN_LADDER);
    public static final RegistryObject<Item> ASHPEN_LIGHT_BLUE_LADDER = registerBlockItem("ashpen_light_blue_ladder",
            CopperBlocks.ASHPEN_LIGHT_BLUE_LADDER);
    public static final RegistryObject<Item> ASHPEN_LIGHT_GRAY_LADDER = registerBlockItem("ashpen_light_gray_ladder",
            CopperBlocks.ASHPEN_LIGHT_GRAY_LADDER);
    public static final RegistryObject<Item> ASHPEN_LIME_LADDER = registerBlockItem("ashpen_lime_ladder",
            CopperBlocks.ASHPEN_LIME_LADDER);
    public static final RegistryObject<Item> ASHPEN_MAGENTA_LADDER = registerBlockItem("ashpen_magenta_ladder",
            CopperBlocks.ASHPEN_MAGENTA_LADDER);
    public static final RegistryObject<Item> ASHPEN_ORANGE_LADDER = registerBlockItem("ashpen_orange_ladder",
            CopperBlocks.ASHPEN_ORANGE_LADDER);
    public static final RegistryObject<Item> ASHPEN_PINK_LADDER = registerBlockItem("ashpen_pink_ladder",
            CopperBlocks.ASHPEN_PINK_LADDER);
    public static final RegistryObject<Item> ASHPEN_PURPLE_LADDER = registerBlockItem("ashpen_purple_ladder",
            CopperBlocks.ASHPEN_PURPLE_LADDER);
    public static final RegistryObject<Item> ASHPEN_RED_LADDER = registerBlockItem("ashpen_red_ladder",
            CopperBlocks.ASHPEN_RED_LADDER);
    public static final RegistryObject<Item> ASHPEN_WHITE_LADDER = registerBlockItem("ashpen_white_ladder",
            CopperBlocks.ASHPEN_WHITE_LADDER);
    public static final RegistryObject<Item> ASHPEN_YELLOW_LADDER = registerBlockItem("ashpen_yellow_ladder",
            CopperBlocks.ASHPEN_YELLOW_LADDER);
    public static final RegistryObject<Item> BAMBOO_LADDER = registerBlockItem("bamboo_ladder",
            CopperBlocks.BAMBOO_LADDER);
    public static final RegistryObject<Item> BIRCH_LADDER = registerBlockItem("birch_ladder",
            CopperBlocks.BIRCH_LADDER);
    public static final RegistryObject<Item> CHERRY_LADDER = registerBlockItem("cherry_ladder",
            CopperBlocks.CHERRY_LADDER);
    public static final RegistryObject<Item> CRIMSON_LADDER = registerBlockItem("crimson_ladder",
            CopperBlocks.CRIMSON_LADDER);
    public static final RegistryObject<Item> DARK_OAK_LADDER = registerBlockItem("dark_oak_ladder",
            CopperBlocks.DARK_OAK_LADDER);
    public static final RegistryObject<Item> JUNGLE_LADDER = registerBlockItem("jungle_ladder",
            CopperBlocks.JUNGLE_LADDER);
    public static final RegistryObject<Item> MANGROVE_LADDER = registerBlockItem("mangrove_ladder",
            CopperBlocks.MANGROVE_LADDER);
    public static final RegistryObject<Item> OAK_LADDER = registerBlockItem("oak_ladder", CopperBlocks.OAK_LADDER);
    public static final RegistryObject<Item> PALE_OAK_LADDER = registerBlockItem("pale_oak_ladder",
            CopperBlocks.PALE_OAK_LADDER);
    public static final RegistryObject<Item> POPLAR_LADDER = registerBlockItem("poplar_ladder",
            CopperBlocks.POPLAR_LADDER);
    public static final RegistryObject<Item> SPRUCE_LADDER = registerBlockItem("spruce_ladder",
            CopperBlocks.SPRUCE_LADDER);
    public static final RegistryObject<Item> STRIPPED_BAMBOO_LADDER = registerBlockItem("stripped_bamboo_ladder",
            CopperBlocks.STRIPPED_BAMBOO_LADDER);
    public static final RegistryObject<Item> WARPED_LADDER = registerBlockItem("warped_ladder",
            CopperBlocks.WARPED_LADDER);

    public static final RegistryObject<Item> COPPER_CHEST = ITEMS.register("copper_chest",
            () -> new CopperChestItem(CopperBlocks.COPPER_CHEST.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> EXPOSED_COPPER_CHEST = ITEMS.register("exposed_copper_chest",
            () -> new CopperChestItem(CopperBlocks.EXPOSED_COPPER_CHEST.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WEATHERED_COPPER_CHEST = ITEMS.register("weathered_copper_chest",
            () -> new CopperChestItem(CopperBlocks.WEATHERED_COPPER_CHEST.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> OXIDIZED_COPPER_CHEST = ITEMS.register("oxidized_copper_chest",
            () -> new CopperChestItem(CopperBlocks.OXIDIZED_COPPER_CHEST.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_COPPER_CHEST = ITEMS.register("waxed_copper_chest",
            () -> new CopperChestItem(CopperBlocks.WAXED_COPPER_CHEST.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_CHEST = ITEMS.register("waxed_exposed_copper_chest",
            () -> new CopperChestItem(CopperBlocks.WAXED_EXPOSED_COPPER_CHEST.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_CHEST = ITEMS.register(
            "waxed_weathered_copper_chest",
            () -> new CopperChestItem(CopperBlocks.WAXED_WEATHERED_COPPER_CHEST.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_CHEST = ITEMS.register("waxed_oxidized_copper_chest",
            () -> new CopperChestItem(CopperBlocks.WAXED_OXIDIZED_COPPER_CHEST.get(), createBlockItemProperties()));
    public static final RegistryObject<Item> WHITE_LAYERED_WOOL = registerBlockItem("white_layered_wool",
            WoolBlocks.WHITE_LAYERED_WOOL);
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL = registerBlockItem("orange_layered_wool",
            WoolBlocks.ORANGE_LAYERED_WOOL);
}
