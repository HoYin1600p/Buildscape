package com.kingodogo.buildscape;

import com.kingodogo.buildscape.block.ModBlocks;
import com.kingodogo.buildscape.item.ModItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BuildScape.MODID)
public class BuildScape {

        public static final String MODID = "buildscape";
        public static final CreativeModeTab BUILDSCAPE_TAB = new CreativeModeTab(
                        "buildscape") {
                @Override
                public ItemStack makeIcon() {
                        return getModIcon();
                }

                @Override
                public void fillItemList(net.minecraft.core.NonNullList<ItemStack> items) {
                        addHardcodedItems(items);

                        // Dynamic Vertical Slabs insertion
                        // We iterate the list and for each slab, if we have a vertical version, insert it next to it.
                        java.util.List<ItemStack> current = new java.util.ArrayList<>(items);
                        items.clear();
                        for (ItemStack stack : current) {
                                items.add(stack);
                                if (stack.getItem() instanceof net.minecraft.world.item.BlockItem) {
                                        Block block = ((net.minecraft.world.item.BlockItem) stack.getItem()).getBlock();
                                        if (com.kingodogo.buildscape.block.ModVerticalSlabs.VERTICAL_SLABS.containsKey(block)) {
                                                items.add(new ItemStack(com.kingodogo.buildscape.block.ModVerticalSlabs.VERTICAL_SLABS.get(block)));
                                        }
                                }
                        }

                        // Also add any vertical slabs that didn't have their parent in the tab (e.g. vanilla slabs)
                        for (net.minecraft.world.item.Item item : com.kingodogo.buildscape.block.ModVerticalSlabs.DYNAMIC_ITEMS) {
                                if (!containsItem(items, item)) {
                                        items.add(new ItemStack(item));
                                }
                        }
                }

                private boolean containsItem(net.minecraft.core.NonNullList<ItemStack> items, net.minecraft.world.item.Item item) {
                        for (ItemStack stack : items) {
                                if (stack.getItem() == item) return true;
                        }
                        return false;
                }

                private void addHardcodedItems(net.minecraft.core.NonNullList<ItemStack> items) {
                        items.add(new ItemStack(ModItems.BIT_COPPER_BLOCK.get()));
                        items.add(new ItemStack(ModItems.BIT_COPPER_BLOCK_STAIRS.get()));
                        items.add(new ItemStack(ModItems.BIT_COPPER_BLOCK_SLAB.get()));
                        items.add(new ItemStack(ModItems.BIT_COPPER_BLOCK_WALL.get()));
                        items.add(new ItemStack(ModItems.BIT_CUT_COPPER.get()));
                        items.add(new ItemStack(ModItems.BIT_CUT_COPPER_STAIRS.get()));
                        items.add(new ItemStack(ModItems.BIT_CUT_COPPER_SLAB.get()));
                        items.add(new ItemStack(ModItems.BIT_CUT_COPPER_WALL.get()));
                        items.add(new ItemStack(ModItems.BIT_CHISELED_COPPER.get()));
                        items.add(new ItemStack(ModItems.BIT_COPPER_BULB.get()));
                        items.add(new ItemStack(ModItems.BIT_COPPER_GRATE.get()));

                        items.add(new ItemStack(ModItems.BIT_EXPOSED_COPPER_BLOCK.get()));
                        items.add(new ItemStack(ModItems.BIT_EXPOSED_COPPER_BLOCK_STAIRS.get()));
                        items.add(new ItemStack(ModItems.BIT_EXPOSED_COPPER_BLOCK_SLAB.get()));
                        items.add(new ItemStack(ModItems.BIT_EXPOSED_COPPER_BLOCK_WALL.get()));
                        items.add(new ItemStack(ModItems.BIT_EXPOSED_CUT_COPPER.get()));
                        items.add(new ItemStack(ModItems.BIT_EXPOSED_CUT_COPPER_STAIRS.get()));
                        items.add(new ItemStack(ModItems.BIT_EXPOSED_CUT_COPPER_SLAB.get()));
                        items.add(new ItemStack(ModItems.BIT_EXPOSED_CUT_COPPER_WALL.get()));
                        items.add(new ItemStack(ModItems.BIT_EXPOSED_CHISELED_COPPER.get()));
                        items.add(new ItemStack(ModItems.BIT_EXPOSED_COPPER_BULB.get()));
                        items.add(new ItemStack(ModItems.BIT_EXPOSED_COPPER_GRATE.get()));

                        items.add(new ItemStack(ModItems.BIT_WEATHERED_COPPER_BLOCK.get()));
                        items.add(
                                        new ItemStack(ModItems.BIT_WEATHERED_COPPER_BLOCK_STAIRS.get()));
                        items.add(new ItemStack(ModItems.BIT_WEATHERED_COPPER_BLOCK_SLAB.get()));
                        items.add(new ItemStack(ModItems.BIT_WEATHERED_COPPER_BLOCK_WALL.get()));
                        items.add(new ItemStack(ModItems.BIT_WEATHERED_CUT_COPPER.get()));
                        items.add(new ItemStack(ModItems.BIT_WEATHERED_CUT_COPPER_STAIRS.get()));
                        items.add(new ItemStack(ModItems.BIT_WEATHERED_CUT_COPPER_SLAB.get()));
                        items.add(new ItemStack(ModItems.BIT_WEATHERED_CUT_COPPER_WALL.get()));
                        items.add(new ItemStack(ModItems.BIT_WEATHERED_CHISELED_COPPER.get()));
                        items.add(new ItemStack(ModItems.BIT_WEATHERED_COPPER_BULB.get()));
                        items.add(new ItemStack(ModItems.BIT_WEATHERED_COPPER_GRATE.get()));

                        items.add(new ItemStack(ModItems.BIT_OXIDIZED_COPPER_BLOCK.get()));
                        items.add(new ItemStack(ModItems.BIT_OXIDIZED_COPPER_BLOCK_STAIRS.get()));
                        items.add(new ItemStack(ModItems.BIT_OXIDIZED_COPPER_BLOCK_SLAB.get()));
                        items.add(new ItemStack(ModItems.BIT_OXIDIZED_COPPER_BLOCK_WALL.get()));
                        items.add(new ItemStack(ModItems.BIT_OXIDIZED_CUT_COPPER.get()));
                        items.add(new ItemStack(ModItems.BIT_OXIDIZED_CUT_COPPER_STAIRS.get()));
                        items.add(new ItemStack(ModItems.BIT_OXIDIZED_CUT_COPPER_SLAB.get()));
                        items.add(new ItemStack(ModItems.BIT_OXIDIZED_CUT_COPPER_WALL.get()));
                        items.add(new ItemStack(ModItems.BIT_OXIDIZED_CHISELED_COPPER.get()));
                        items.add(new ItemStack(ModItems.BIT_OXIDIZED_COPPER_BULB.get()));
                        items.add(new ItemStack(ModItems.BIT_OXIDIZED_COPPER_GRATE.get()));

                        items.add(new ItemStack(ModItems.COPPER_NUGGET.get()));
                        items.add(new ItemStack(ModItems.COPPER_CHAIN.get()));
                        items.add(new ItemStack(ModItems.EXPOSED_COPPER_CHAIN.get()));
                        items.add(new ItemStack(ModItems.WEATHERED_COPPER_CHAIN.get()));
                        items.add(new ItemStack(ModItems.OXIDIZED_COPPER_CHAIN.get()));

                        items.add(new ItemStack(ModItems.STEEL_INGOT.get()));
                        items.add(new ItemStack(ModItems.STEEL_BLOCK.get()));
                        items.add(new ItemStack(ModItems.STEEL_BLOCK_STAIRS.get()));
                        items.add(new ItemStack(ModItems.STEEL_BLOCK_SLAB.get()));
                        items.add(new ItemStack(ModItems.STEEL_BLOCK_WALL.get()));
                        items.add(new ItemStack(ModItems.POLISHED_STEEL.get()));
                        items.add(new ItemStack(ModItems.POLISHED_STEEL_STAIRS.get()));
                        items.add(new ItemStack(ModItems.POLISHED_STEEL_SLAB.get()));
                        items.add(new ItemStack(ModItems.POLISHED_STEEL_WALL.get()));
                        items.add(new ItemStack(ModItems.PRESSED_STEEL.get()));
                        items.add(new ItemStack(ModItems.PRESSED_STEEL_STAIRS.get()));
                        items.add(new ItemStack(ModItems.PRESSED_STEEL_SLAB.get()));
                        items.add(new ItemStack(ModItems.PRESSED_STEEL_WALL.get()));
                        items.add(new ItemStack(ModItems.CUT_STEEL.get()));
                        items.add(new ItemStack(ModItems.CUT_STEEL_STAIRS.get()));
                        items.add(new ItemStack(ModItems.CUT_STEEL_SLAB.get()));
                        items.add(new ItemStack(ModItems.CUT_STEEL_WALL.get()));
                        items.add(new ItemStack(ModItems.FACTORY_STEEL_PANEL.get()));
                        items.add(new ItemStack(ModItems.STEEL_CASING.get()));
                        items.add(new ItemStack(ModItems.STEEL_TRIM.get()));
                        items.add(new ItemStack(ModItems.STEEL_PILLAR.get()));
                        items.add(new ItemStack(ModItems.BOLTED_STEEL_PILLAR.get()));
                        items.add(new ItemStack(ModItems.STEEL_GRATE.get()));
                        items.add(new ItemStack(ModItems.STEEL_FAN.get()));

                        // Caution Blocks
                        items.add(new ItemStack(ModItems.CAUTION_FACTORY.get()));
                        items.add(new ItemStack(ModItems.CAUTION_FACTORY_STAIRS.get()));
                        items.add(new ItemStack(ModItems.CAUTION_FACTORY_SLAB.get()));

                        items.add(new ItemStack(ModItems.FRAMED_CAUTION.get()));
                        items.add(new ItemStack(ModItems.FRAMED_CAUTION_STAIRS.get()));
                        items.add(new ItemStack(ModItems.FRAMED_CAUTION_SLAB.get()));

                        items.add(new ItemStack(ModItems.CAUTION_BLACK.get()));
                        items.add(new ItemStack(ModItems.CAUTION_BLACK_STAIRS.get()));
                        items.add(new ItemStack(ModItems.CAUTION_BLACK_SLAB.get()));

                        items.add(new ItemStack(ModItems.CAUTION_WHITE.get()));
                        items.add(new ItemStack(ModItems.CAUTION_WHITE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.CAUTION_WHITE_SLAB.get()));

                        items.add(new ItemStack(ModItems.CAUTION_RED.get()));
                        items.add(new ItemStack(ModItems.CAUTION_RED_STAIRS.get()));
                        items.add(new ItemStack(ModItems.CAUTION_RED_SLAB.get()));

                        items.add(new ItemStack(ModItems.CAUTION_BLUE.get()));
                        items.add(new ItemStack(ModItems.CAUTION_BLUE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.CAUTION_BLUE_SLAB.get()));

                        items.add(new ItemStack(ModItems.CAUTION_YELLOW.get()));
                        items.add(new ItemStack(ModItems.CAUTION_YELLOW_STAIRS.get()));
                        items.add(new ItemStack(ModItems.CAUTION_YELLOW_SLAB.get()));

                        items.add(new ItemStack(ModItems.CAUTION_LIME.get()));
                        items.add(new ItemStack(ModItems.CAUTION_LIME_STAIRS.get()));
                        items.add(new ItemStack(ModItems.CAUTION_LIME_SLAB.get()));

                        items.add(new ItemStack(ModItems.CAUTION_PINK.get()));
                        items.add(new ItemStack(ModItems.CAUTION_PINK_STAIRS.get()));
                        items.add(new ItemStack(ModItems.CAUTION_PINK_SLAB.get()));

                        items.add(new ItemStack(ModItems.CAUTION_CANDY.get()));
                        items.add(new ItemStack(ModItems.CAUTION_CANDY_STAIRS.get()));
                        items.add(new ItemStack(ModItems.CAUTION_CANDY_SLAB.get()));

                        items.add(new ItemStack(ModItems.CAUTION_COTTONCANDY.get()));
                        items.add(new ItemStack(ModItems.CAUTION_COTTONCANDY_STAIRS.get()));
                        items.add(new ItemStack(ModItems.CAUTION_COTTONCANDY_SLAB.get()));

                        items.add(new ItemStack(ModItems.CAUTION_MINTCANDY.get()));
                        items.add(new ItemStack(ModItems.CAUTION_MINTCANDY_STAIRS.get()));
                        items.add(new ItemStack(ModItems.CAUTION_MINTCANDY_SLAB.get()));

                        items.add(new ItemStack(ModItems.CAUTION_CITRUSCANDY.get()));
                        items.add(new ItemStack(ModItems.CAUTION_CITRUSCANDY_STAIRS.get()));
                        items.add(new ItemStack(ModItems.CAUTION_CITRUSCANDY_SLAB.get()));

                        items.add(new ItemStack(Items.CHAIN));
                        items.add(new ItemStack(ModItems.LARGE_IRON_CHAIN.get()));
                        items.add(new ItemStack(ModItems.ANCIENT_STEEL_CHAIN.get()));
                        items.add(new ItemStack(ModItems.LARGE_ANCIENT_STEEL_CHAIN.get()));
                        items.add(new ItemStack(ModItems.GOLD_CHAIN.get()));
                        items.add(new ItemStack(ModItems.LARGE_GOLD_CHAIN.get()));
                        items.add(new ItemStack(ModItems.DIAMOND_CHAIN.get()));
                        items.add(new ItemStack(ModItems.LARGE_DIAMOND_CHAIN.get()));
                        items.add(new ItemStack(ModItems.EMERALD_CHAIN.get()));
                        items.add(new ItemStack(ModItems.LARGE_EMERALD_CHAIN.get()));
                        items.add(new ItemStack(ModItems.NETHERITE_CHAIN.get()));
                        items.add(new ItemStack(ModItems.LARGE_NETHERITE_CHAIN.get()));

                        items.add(new ItemStack(ModItems.BIT_CHISELED_TUFF.get()));
                        items.add(new ItemStack(ModItems.BIT_CHISELED_TUFF_BRICKS.get()));
                        items.add(new ItemStack(ModItems.BIT_POLISHED_TUFF.get()));
                        items.add(new ItemStack(ModItems.BIT_POLISHED_TUFF_STAIRS.get()));
                        items.add(new ItemStack(ModItems.BIT_POLISHED_TUFF_SLAB.get()));
                        items.add(new ItemStack(ModItems.BIT_POLISHED_TUFF_WALL.get()));
                        items.add(new ItemStack(ModItems.BIT_TUFF_BRICKS.get()));
                        items.add(new ItemStack(ModItems.BIT_TUFF_BRICKS_STAIRS.get()));
                        items.add(new ItemStack(ModItems.BIT_TUFF_BRICKS_SLAB.get()));
                        items.add(new ItemStack(ModItems.BIT_TUFF_BRICKS_WALL.get()));

                        items.add(new ItemStack(ModItems.WHITE_TILES.get()));
                        items.add(new ItemStack(ModItems.WHITE_TILES_STAIRS.get()));
                        items.add(new ItemStack(ModItems.WHITE_TILES_SLAB.get()));
                        items.add(new ItemStack(ModItems.WHITE_TILES_WALL.get()));

                        items.add(new ItemStack(ModItems.LIGHT_GRAY_TILES.get()));
                        items.add(new ItemStack(ModItems.LIGHT_GRAY_TILES_STAIRS.get()));
                        items.add(new ItemStack(ModItems.LIGHT_GRAY_TILES_SLAB.get()));
                        items.add(new ItemStack(ModItems.LIGHT_GRAY_TILES_WALL.get()));

                        items.add(new ItemStack(ModItems.GRAY_TILES.get()));
                        items.add(new ItemStack(ModItems.GRAY_TILES_STAIRS.get()));
                        items.add(new ItemStack(ModItems.GRAY_TILES_SLAB.get()));
                        items.add(new ItemStack(ModItems.GRAY_TILES_WALL.get()));

                        items.add(new ItemStack(ModItems.BLACK_TILES.get()));
                        items.add(new ItemStack(ModItems.BLACK_TILES_STAIRS.get()));
                        items.add(new ItemStack(ModItems.BLACK_TILES_SLAB.get()));
                        items.add(new ItemStack(ModItems.BLACK_TILES_WALL.get()));

                        items.add(new ItemStack(ModItems.BROWN_TILES.get()));
                        items.add(new ItemStack(ModItems.BROWN_TILES_STAIRS.get()));
                        items.add(new ItemStack(ModItems.BROWN_TILES_SLAB.get()));
                        items.add(new ItemStack(ModItems.BROWN_TILES_WALL.get()));

                        items.add(new ItemStack(ModItems.RED_TILES.get()));
                        items.add(new ItemStack(ModItems.RED_TILES_STAIRS.get()));
                        items.add(new ItemStack(ModItems.RED_TILES_SLAB.get()));
                        items.add(new ItemStack(ModItems.RED_TILES_WALL.get()));

                        items.add(new ItemStack(ModItems.ORANGE_TILES.get()));
                        items.add(new ItemStack(ModItems.ORANGE_TILES_STAIRS.get()));
                        items.add(new ItemStack(ModItems.ORANGE_TILES_SLAB.get()));
                        items.add(new ItemStack(ModItems.ORANGE_TILES_WALL.get()));

                        items.add(new ItemStack(ModItems.YELLOW_TILES.get()));
                        items.add(new ItemStack(ModItems.YELLOW_TILES_STAIRS.get()));
                        items.add(new ItemStack(ModItems.YELLOW_TILES_SLAB.get()));
                        items.add(new ItemStack(ModItems.YELLOW_TILES_WALL.get()));

                        items.add(new ItemStack(ModItems.LIME_TILES.get()));
                        items.add(new ItemStack(ModItems.LIME_TILES_STAIRS.get()));
                        items.add(new ItemStack(ModItems.LIME_TILES_SLAB.get()));
                        items.add(new ItemStack(ModItems.LIME_TILES_WALL.get()));

                        items.add(new ItemStack(ModItems.GREEN_TILES.get()));
                        items.add(new ItemStack(ModItems.GREEN_TILES_STAIRS.get()));
                        items.add(new ItemStack(ModItems.GREEN_TILES_SLAB.get()));
                        items.add(new ItemStack(ModItems.GREEN_TILES_WALL.get()));

                        items.add(new ItemStack(ModItems.CYAN_TILES.get()));
                        items.add(new ItemStack(ModItems.CYAN_TILES_STAIRS.get()));
                        items.add(new ItemStack(ModItems.CYAN_TILES_SLAB.get()));
                        items.add(new ItemStack(ModItems.CYAN_TILES_WALL.get()));

                        items.add(new ItemStack(ModItems.LIGHT_BLUE_TILES.get()));
                        items.add(new ItemStack(ModItems.LIGHT_BLUE_TILES_STAIRS.get()));
                        items.add(new ItemStack(ModItems.LIGHT_BLUE_TILES_SLAB.get()));
                        items.add(new ItemStack(ModItems.LIGHT_BLUE_TILES_WALL.get()));

                        items.add(new ItemStack(ModItems.BLUE_TILES.get()));
                        items.add(new ItemStack(ModItems.BLUE_TILES_STAIRS.get()));
                        items.add(new ItemStack(ModItems.BLUE_TILES_SLAB.get()));
                        items.add(new ItemStack(ModItems.BLUE_TILES_WALL.get()));

                        items.add(new ItemStack(ModItems.PURPLE_TILES.get()));
                        items.add(new ItemStack(ModItems.PURPLE_TILES_STAIRS.get()));
                        items.add(new ItemStack(ModItems.PURPLE_TILES_SLAB.get()));
                        items.add(new ItemStack(ModItems.PURPLE_TILES_WALL.get()));

                        items.add(new ItemStack(ModItems.MAGENTA_TILES.get()));
                        items.add(new ItemStack(ModItems.MAGENTA_TILES_STAIRS.get()));
                        items.add(new ItemStack(ModItems.MAGENTA_TILES_SLAB.get()));
                        items.add(new ItemStack(ModItems.MAGENTA_TILES_WALL.get()));

                        items.add(new ItemStack(ModItems.PINK_TILES.get()));
                        items.add(new ItemStack(ModItems.PINK_TILES_STAIRS.get()));
                        items.add(new ItemStack(ModItems.PINK_TILES_SLAB.get()));
                        items.add(new ItemStack(ModItems.PINK_TILES_WALL.get()));

                        // Stained Bricks
                        items.add(new ItemStack(ModItems.WHITE_STAINED_BRICKS.get()));
                        items.add(new ItemStack(ModItems.WHITE_STAINED_BRICKS_STAIRS.get()));
                        items.add(new ItemStack(ModItems.WHITE_STAINED_BRICKS_SLAB.get()));
                        items.add(new ItemStack(ModItems.WHITE_STAINED_BRICKS_WALL.get()));

                        items.add(new ItemStack(ModItems.LIGHT_GRAY_STAINED_BRICKS.get()));
                        items.add(new ItemStack(ModItems.LIGHT_GRAY_STAINED_BRICKS_STAIRS.get()));
                        items.add(new ItemStack(ModItems.LIGHT_GRAY_STAINED_BRICKS_SLAB.get()));
                        items.add(new ItemStack(ModItems.LIGHT_GRAY_STAINED_BRICKS_WALL.get()));

                        items.add(new ItemStack(ModItems.GRAY_STAINED_BRICKS.get()));
                        items.add(new ItemStack(ModItems.GRAY_STAINED_BRICKS_STAIRS.get()));
                        items.add(new ItemStack(ModItems.GRAY_STAINED_BRICKS_SLAB.get()));
                        items.add(new ItemStack(ModItems.GRAY_STAINED_BRICKS_WALL.get()));

                        items.add(new ItemStack(ModItems.BLACK_STAINED_BRICKS.get()));
                        items.add(new ItemStack(ModItems.BLACK_STAINED_BRICKS_STAIRS.get()));
                        items.add(new ItemStack(ModItems.BLACK_STAINED_BRICKS_SLAB.get()));
                        items.add(new ItemStack(ModItems.BLACK_STAINED_BRICKS_WALL.get()));

                        items.add(new ItemStack(ModItems.BROWN_STAINED_BRICKS.get()));
                        items.add(new ItemStack(ModItems.BROWN_STAINED_BRICKS_STAIRS.get()));
                        items.add(new ItemStack(ModItems.BROWN_STAINED_BRICKS_SLAB.get()));
                        items.add(new ItemStack(ModItems.BROWN_STAINED_BRICKS_WALL.get()));

                        items.add(new ItemStack(ModItems.RED_STAINED_BRICKS.get()));
                        items.add(new ItemStack(ModItems.RED_STAINED_BRICKS_STAIRS.get()));
                        items.add(new ItemStack(ModItems.RED_STAINED_BRICKS_SLAB.get()));
                        items.add(new ItemStack(ModItems.RED_STAINED_BRICKS_WALL.get()));

                        items.add(new ItemStack(ModItems.ORANGE_STAINED_BRICKS.get()));
                        items.add(new ItemStack(ModItems.ORANGE_STAINED_BRICKS_STAIRS.get()));
                        items.add(new ItemStack(ModItems.ORANGE_STAINED_BRICKS_SLAB.get()));
                        items.add(new ItemStack(ModItems.ORANGE_STAINED_BRICKS_WALL.get()));

                        items.add(new ItemStack(ModItems.YELLOW_STAINED_BRICKS.get()));
                        items.add(new ItemStack(ModItems.YELLOW_STAINED_BRICKS_STAIRS.get()));
                        items.add(new ItemStack(ModItems.YELLOW_STAINED_BRICKS_SLAB.get()));
                        items.add(new ItemStack(ModItems.YELLOW_STAINED_BRICKS_WALL.get()));

                        items.add(new ItemStack(ModItems.LIME_STAINED_BRICKS.get()));
                        items.add(new ItemStack(ModItems.LIME_STAINED_BRICKS_STAIRS.get()));
                        items.add(new ItemStack(ModItems.LIME_STAINED_BRICKS_SLAB.get()));
                        items.add(new ItemStack(ModItems.LIME_STAINED_BRICKS_WALL.get()));

                        items.add(new ItemStack(ModItems.GREEN_STAINED_BRICKS.get()));
                        items.add(new ItemStack(ModItems.GREEN_STAINED_BRICKS_STAIRS.get()));
                        items.add(new ItemStack(ModItems.GREEN_STAINED_BRICKS_SLAB.get()));
                        items.add(new ItemStack(ModItems.GREEN_STAINED_BRICKS_WALL.get()));

                        items.add(new ItemStack(ModItems.CYAN_STAINED_BRICKS.get()));
                        items.add(new ItemStack(ModItems.CYAN_STAINED_BRICKS_STAIRS.get()));
                        items.add(new ItemStack(ModItems.CYAN_STAINED_BRICKS_SLAB.get()));
                        items.add(new ItemStack(ModItems.CYAN_STAINED_BRICKS_WALL.get()));

                        items.add(new ItemStack(ModItems.LIGHT_BLUE_STAINED_BRICKS.get()));
                        items.add(new ItemStack(ModItems.LIGHT_BLUE_STAINED_BRICKS_STAIRS.get()));
                        items.add(new ItemStack(ModItems.LIGHT_BLUE_STAINED_BRICKS_SLAB.get()));
                        items.add(new ItemStack(ModItems.LIGHT_BLUE_STAINED_BRICKS_WALL.get()));

                        items.add(new ItemStack(ModItems.BLUE_STAINED_BRICKS.get()));
                        items.add(new ItemStack(ModItems.BLUE_STAINED_BRICKS_STAIRS.get()));
                        items.add(new ItemStack(ModItems.BLUE_STAINED_BRICKS_SLAB.get()));
                        items.add(new ItemStack(ModItems.BLUE_STAINED_BRICKS_WALL.get()));

                        items.add(new ItemStack(ModItems.PURPLE_STAINED_BRICKS.get()));
                        items.add(new ItemStack(ModItems.PURPLE_STAINED_BRICKS_STAIRS.get()));
                        items.add(new ItemStack(ModItems.PURPLE_STAINED_BRICKS_SLAB.get()));
                        items.add(new ItemStack(ModItems.PURPLE_STAINED_BRICKS_WALL.get()));

                        items.add(new ItemStack(ModItems.MAGENTA_STAINED_BRICKS.get()));
                        items.add(new ItemStack(ModItems.MAGENTA_STAINED_BRICKS_STAIRS.get()));
                        items.add(new ItemStack(ModItems.MAGENTA_STAINED_BRICKS_SLAB.get()));
                        items.add(new ItemStack(ModItems.MAGENTA_STAINED_BRICKS_WALL.get()));

                        items.add(new ItemStack(ModItems.PINK_STAINED_BRICKS.get()));
                        items.add(new ItemStack(ModItems.PINK_STAINED_BRICKS_STAIRS.get()));
                        items.add(new ItemStack(ModItems.PINK_STAINED_BRICKS_SLAB.get()));
                        items.add(new ItemStack(ModItems.PINK_STAINED_BRICKS_WALL.get()));

                        items.add(new ItemStack(ModItems.BAMBOO_BLOCK.get()));
                        items.add(new ItemStack(ModItems.BAMBOO_BLOCK_STAIRS.get()));
                        items.add(new ItemStack(ModItems.BAMBOO_BLOCK_SLAB.get()));
                        items.add(new ItemStack(ModItems.BAMBOO_BLOCK_WALL.get()));
                        items.add(new ItemStack(ModItems.BAMBOO_BLOCK_FENCE.get()));
                        items.add(new ItemStack(ModItems.BAMBOO_BLOCK_FENCE_GATE.get()));
                        items.add(new ItemStack(ModItems.BAMBOO_BLOCK_PRESSURE_PLATE.get()));
                        items.add(new ItemStack(ModItems.BAMBOO_BLOCK_BUTTON.get()));
                        items.add(new ItemStack(ModItems.BAMBOO_WOOD.get()));
                        items.add(new ItemStack(ModItems.BAMBOO_DOOR.get()));
                        items.add(new ItemStack(ModItems.BAMBOO_TRAPDOOR.get()));
                        items.add(new ItemStack(ModItems.BAMBOO_SIGN.get()));

                        items.add(new ItemStack(ModItems.STRIPPED_BAMBOO_BLOCK.get()));
                        items.add(new ItemStack(ModItems.STRIPPED_BAMBOO_BLOCK_STAIRS.get()));
                        items.add(new ItemStack(ModItems.STRIPPED_BAMBOO_BLOCK_SLAB.get()));
                        items.add(new ItemStack(ModItems.STRIPPED_BAMBOO_BLOCK_WALL.get()));
                        items.add(new ItemStack(ModItems.STRIPPED_BAMBOO_BLOCK_FENCE.get()));
                        items.add(new ItemStack(ModItems.STRIPPED_BAMBOO_BLOCK_FENCE_GATE.get()));
                        items.add(
                                        new ItemStack(ModItems.STRIPPED_BAMBOO_BLOCK_PRESSURE_PLATE.get()));
                        items.add(new ItemStack(ModItems.STRIPPED_BAMBOO_BLOCK_BUTTON.get()));
                        items.add(new ItemStack(ModItems.STRIPPED_BAMBOO_WOOD.get()));

                        items.add(new ItemStack(ModItems.MANGROVE_LOG.get()));
                        items.add(new ItemStack(ModItems.STRIPPED_MANGROVE_LOG.get()));
                        items.add(new ItemStack(ModItems.MANGROVE_WOOD.get()));
                        items.add(new ItemStack(ModItems.STRIPPED_MANGROVE_WOOD.get()));
                        items.add(new ItemStack(ModItems.MANGROVE_LEAVES.get()));
                        items.add(new ItemStack(ModItems.MANGROVE_ROOTS.get()));
                        items.add(new ItemStack(ModItems.MUDDY_MANGROVE_ROOTS.get()));
                        items.add(new ItemStack(ModItems.MANGROVE_PROPAGULE.get()));
                        items.add(new ItemStack(ModItems.MANGROVE_PLANKS.get()));
                        items.add(new ItemStack(ModItems.MANGROVE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.MANGROVE_SLAB.get()));
                        items.add(new ItemStack(ModItems.MANGROVE_WOOD_WALL.get()));
                        items.add(new ItemStack(ModItems.MANGROVE_FENCE.get()));
                        items.add(new ItemStack(ModItems.MANGROVE_FENCE_GATE.get()));
                        items.add(new ItemStack(ModItems.MANGROVE_PRESSURE_PLATE.get()));
                        items.add(new ItemStack(ModItems.MANGROVE_BUTTON.get()));
                        items.add(new ItemStack(ModItems.MANGROVE_DOOR.get()));
                        items.add(new ItemStack(ModItems.MANGROVE_TRAPDOOR.get()));
                        items.add(new ItemStack(ModItems.MANGROVE_SIGN.get()));

                        items.add(new ItemStack(ModItems.STRIPPED_MANGROVE_WOOD_WALL.get()));
                        items.add(new ItemStack(ModItems.MANGROVE_BOAT.get()));

                        items.add(new ItemStack(ModItems.OAK_WOOD_WALL.get()));
                        items.add(new ItemStack(ModItems.STRIPPED_OAK_WOOD_WALL.get()));
                        items.add(new ItemStack(ModItems.SPRUCE_WOOD_WALL.get()));
                        items.add(new ItemStack(ModItems.STRIPPED_SPRUCE_WOOD_WALL.get()));
                        items.add(new ItemStack(ModItems.BIRCH_WOOD_WALL.get()));
                        items.add(new ItemStack(ModItems.STRIPPED_BIRCH_WOOD_WALL.get()));
                        items.add(new ItemStack(ModItems.DARK_OAK_WOOD_WALL.get()));
                        items.add(new ItemStack(ModItems.STRIPPED_DARK_OAK_WOOD_WALL.get()));
                        items.add(new ItemStack(ModItems.JUNGLE_WOOD_WALL.get()));
                        items.add(new ItemStack(ModItems.STRIPPED_JUNGLE_WOOD_WALL.get()));
                        items.add(new ItemStack(ModItems.ACACIA_WOOD_WALL.get()));
                        items.add(new ItemStack(ModItems.STRIPPED_ACACIA_WOOD_WALL.get()));

                        items.add(new ItemStack(ModItems.ASHPEN_WHITE_PLANKS.get()));
                        items.add(new ItemStack(ModItems.ASHPEN_WHITE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.ASHPEN_WHITE_SLAB.get()));
                        items.add(new ItemStack(ModItems.ASHPEN_WHITE_BUTTON.get()));
                        items.add(new ItemStack(ModItems.ASHPEN_WHITE_PRESSURE_PLATE.get()));
                        items.add(new ItemStack(ModItems.ASHPEN_WHITE_FENCE.get()));
                        items.add(new ItemStack(ModItems.ASHPEN_WHITE_FENCE_GATE.get()));

                        items.add(new ItemStack(ModItems.LIGHT_GRAY_ASHPEN_PLANKS.get()));
                        items.add(new ItemStack(ModItems.LIGHT_GRAY_ASHPEN_STAIRS.get()));
                        items.add(new ItemStack(ModItems.LIGHT_GRAY_ASHPEN_SLAB.get()));
                        items.add(new ItemStack(ModItems.LIGHT_GRAY_ASHPEN_BUTTON.get()));
                        items.add(new ItemStack(ModItems.LIGHT_GRAY_ASHPEN_PRESSURE_PLATE.get()));
                        items.add(new ItemStack(ModItems.LIGHT_GRAY_ASHPEN_FENCE.get()));
                        items.add(new ItemStack(ModItems.LIGHT_GRAY_ASHPEN_FENCE_GATE.get()));

                        items.add(new ItemStack(ModItems.GRAY_ASHPEN_PLANKS.get()));
                        items.add(new ItemStack(ModItems.GRAY_ASHPEN_STAIRS.get()));
                        items.add(new ItemStack(ModItems.GRAY_ASHPEN_SLAB.get()));
                        items.add(new ItemStack(ModItems.GRAY_ASHPEN_BUTTON.get()));
                        items.add(new ItemStack(ModItems.GRAY_ASHPEN_PRESSURE_PLATE.get()));
                        items.add(new ItemStack(ModItems.GRAY_ASHPEN_FENCE.get()));
                        items.add(new ItemStack(ModItems.GRAY_ASHPEN_FENCE_GATE.get()));

                        items.add(new ItemStack(ModItems.BLACK_ASHPEN_PLANKS.get()));
                        items.add(new ItemStack(ModItems.BLACK_ASHPEN_STAIRS.get()));
                        items.add(new ItemStack(ModItems.BLACK_ASHPEN_SLAB.get()));
                        items.add(new ItemStack(ModItems.BLACK_ASHPEN_BUTTON.get()));
                        items.add(new ItemStack(ModItems.BLACK_ASHPEN_PRESSURE_PLATE.get()));
                        items.add(new ItemStack(ModItems.BLACK_ASHPEN_FENCE.get()));
                        items.add(new ItemStack(ModItems.BLACK_ASHPEN_FENCE_GATE.get()));

                        items.add(new ItemStack(ModItems.BROWN_ASHPEN_PLANKS.get()));
                        items.add(new ItemStack(ModItems.BROWN_ASHPEN_STAIRS.get()));
                        items.add(new ItemStack(ModItems.BROWN_ASHPEN_SLAB.get()));
                        items.add(new ItemStack(ModItems.BROWN_ASHPEN_BUTTON.get()));
                        items.add(new ItemStack(ModItems.BROWN_ASHPEN_PRESSURE_PLATE.get()));
                        items.add(new ItemStack(ModItems.BROWN_ASHPEN_FENCE.get()));
                        items.add(new ItemStack(ModItems.BROWN_ASHPEN_FENCE_GATE.get()));

                        items.add(new ItemStack(ModItems.RED_ASHPEN_PLANKS.get()));
                        items.add(new ItemStack(ModItems.RED_ASHPEN_STAIRS.get()));
                        items.add(new ItemStack(ModItems.RED_ASHPEN_SLAB.get()));
                        items.add(new ItemStack(ModItems.RED_ASHPEN_BUTTON.get()));
                        items.add(new ItemStack(ModItems.RED_ASHPEN_PRESSURE_PLATE.get()));
                        items.add(new ItemStack(ModItems.RED_ASHPEN_FENCE.get()));
                        items.add(new ItemStack(ModItems.RED_ASHPEN_FENCE_GATE.get()));

                        items.add(new ItemStack(ModItems.ORANGE_ASHPEN_PLANKS.get()));
                        items.add(new ItemStack(ModItems.ORANGE_ASHPEN_STAIRS.get()));
                        items.add(new ItemStack(ModItems.ORANGE_ASHPEN_SLAB.get()));
                        items.add(new ItemStack(ModItems.ORANGE_ASHPEN_BUTTON.get()));
                        items.add(new ItemStack(ModItems.ORANGE_ASHPEN_PRESSURE_PLATE.get()));
                        items.add(new ItemStack(ModItems.ORANGE_ASHPEN_FENCE.get()));
                        items.add(new ItemStack(ModItems.ORANGE_ASHPEN_FENCE_GATE.get()));

                        items.add(new ItemStack(ModItems.YELLOW_ASHPEN_PLANKS.get()));
                        items.add(new ItemStack(ModItems.YELLOW_ASHPEN_STAIRS.get()));
                        items.add(new ItemStack(ModItems.YELLOW_ASHPEN_SLAB.get()));
                        items.add(new ItemStack(ModItems.YELLOW_ASHPEN_BUTTON.get()));
                        items.add(new ItemStack(ModItems.YELLOW_ASHPEN_PRESSURE_PLATE.get()));
                        items.add(new ItemStack(ModItems.YELLOW_ASHPEN_FENCE.get()));
                        items.add(new ItemStack(ModItems.YELLOW_ASHPEN_FENCE_GATE.get()));

                        items.add(new ItemStack(ModItems.LIME_ASHPEN_PLANKS.get()));
                        items.add(new ItemStack(ModItems.LIME_ASHPEN_STAIRS.get()));
                        items.add(new ItemStack(ModItems.LIME_ASHPEN_SLAB.get()));
                        items.add(new ItemStack(ModItems.LIME_ASHPEN_BUTTON.get()));
                        items.add(new ItemStack(ModItems.LIME_ASHPEN_PRESSURE_PLATE.get()));
                        items.add(new ItemStack(ModItems.LIME_ASHPEN_FENCE.get()));
                        items.add(new ItemStack(ModItems.LIME_ASHPEN_FENCE_GATE.get()));

                        items.add(new ItemStack(ModItems.GREEN_ASHPEN_PLANKS.get()));
                        items.add(new ItemStack(ModItems.GREEN_ASHPEN_STAIRS.get()));
                        items.add(new ItemStack(ModItems.GREEN_ASHPEN_SLAB.get()));
                        items.add(new ItemStack(ModItems.GREEN_ASHPEN_BUTTON.get()));
                        items.add(new ItemStack(ModItems.GREEN_ASHPEN_PRESSURE_PLATE.get()));
                        items.add(new ItemStack(ModItems.GREEN_ASHPEN_FENCE.get()));
                        items.add(new ItemStack(ModItems.GREEN_ASHPEN_FENCE_GATE.get()));

                        items.add(new ItemStack(ModItems.CYAN_ASHPEN_PLANKS.get()));
                        items.add(new ItemStack(ModItems.CYAN_ASHPEN_STAIRS.get()));
                        items.add(new ItemStack(ModItems.CYAN_ASHPEN_SLAB.get()));
                        items.add(new ItemStack(ModItems.CYAN_ASHPEN_BUTTON.get()));
                        items.add(new ItemStack(ModItems.CYAN_ASHPEN_PRESSURE_PLATE.get()));
                        items.add(new ItemStack(ModItems.CYAN_ASHPEN_FENCE.get()));
                        items.add(new ItemStack(ModItems.CYAN_ASHPEN_FENCE_GATE.get()));

                        items.add(new ItemStack(ModItems.LIGHT_BLUE_ASHPEN_PLANKS.get()));
                        items.add(new ItemStack(ModItems.LIGHT_BLUE_ASHPEN_STAIRS.get()));
                        items.add(new ItemStack(ModItems.LIGHT_BLUE_ASHPEN_SLAB.get()));
                        items.add(new ItemStack(ModItems.LIGHT_BLUE_ASHPEN_BUTTON.get()));
                        items.add(new ItemStack(ModItems.LIGHT_BLUE_ASHPEN_PRESSURE_PLATE.get()));
                        items.add(new ItemStack(ModItems.LIGHT_BLUE_ASHPEN_FENCE.get()));
                        items.add(new ItemStack(ModItems.LIGHT_BLUE_ASHPEN_FENCE_GATE.get()));

                        items.add(new ItemStack(ModItems.BLUE_ASHPEN_PLANKS.get()));
                        items.add(new ItemStack(ModItems.BLUE_ASHPEN_STAIRS.get()));
                        items.add(new ItemStack(ModItems.BLUE_ASHPEN_SLAB.get()));
                        items.add(new ItemStack(ModItems.BLUE_ASHPEN_BUTTON.get()));
                        items.add(new ItemStack(ModItems.BLUE_ASHPEN_PRESSURE_PLATE.get()));
                        items.add(new ItemStack(ModItems.BLUE_ASHPEN_FENCE.get()));
                        items.add(new ItemStack(ModItems.BLUE_ASHPEN_FENCE_GATE.get()));

                        items.add(new ItemStack(ModItems.PURPLE_ASHPEN_PLANKS.get()));
                        items.add(new ItemStack(ModItems.PURPLE_ASHPEN_STAIRS.get()));
                        items.add(new ItemStack(ModItems.PURPLE_ASHPEN_SLAB.get()));
                        items.add(new ItemStack(ModItems.PURPLE_ASHPEN_BUTTON.get()));
                        items.add(new ItemStack(ModItems.PURPLE_ASHPEN_PRESSURE_PLATE.get()));
                        items.add(new ItemStack(ModItems.PURPLE_ASHPEN_FENCE.get()));
                        items.add(new ItemStack(ModItems.PURPLE_ASHPEN_FENCE_GATE.get()));

                        items.add(new ItemStack(ModItems.MAGENTA_ASHPEN_PLANKS.get()));
                        items.add(new ItemStack(ModItems.MAGENTA_ASHPEN_STAIRS.get()));
                        items.add(new ItemStack(ModItems.MAGENTA_ASHPEN_SLAB.get()));
                        items.add(new ItemStack(ModItems.MAGENTA_ASHPEN_BUTTON.get()));
                        items.add(new ItemStack(ModItems.MAGENTA_ASHPEN_PRESSURE_PLATE.get()));
                        items.add(new ItemStack(ModItems.MAGENTA_ASHPEN_FENCE.get()));
                        items.add(new ItemStack(ModItems.MAGENTA_ASHPEN_FENCE_GATE.get()));

                        items.add(new ItemStack(ModItems.PINK_ASHPEN_PLANKS.get()));
                        items.add(new ItemStack(ModItems.PINK_ASHPEN_STAIRS.get()));
                        items.add(new ItemStack(ModItems.PINK_ASHPEN_SLAB.get()));
                        items.add(new ItemStack(ModItems.PINK_ASHPEN_BUTTON.get()));
                        items.add(new ItemStack(ModItems.PINK_ASHPEN_PRESSURE_PLATE.get()));
                        items.add(new ItemStack(ModItems.PINK_ASHPEN_FENCE.get()));
                        items.add(new ItemStack(ModItems.PINK_ASHPEN_FENCE_GATE.get()));

                        items.add(new ItemStack(ModItems.WHITE_CONCRETE_SLAB.get()));
                        items.add(new ItemStack(ModItems.WHITE_CONCRETE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.WHITE_CONCRETE_WALL.get()));

                        items.add(new ItemStack(ModItems.LIGHT_GRAY_CONCRETE_SLAB.get()));
                        items.add(new ItemStack(ModItems.LIGHT_GRAY_CONCRETE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.LIGHT_GRAY_CONCRETE_WALL.get()));

                        items.add(new ItemStack(ModItems.GRAY_CONCRETE_SLAB.get()));
                        items.add(new ItemStack(ModItems.GRAY_CONCRETE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.GRAY_CONCRETE_WALL.get()));

                        items.add(new ItemStack(ModItems.BLACK_CONCRETE_SLAB.get()));
                        items.add(new ItemStack(ModItems.BLACK_CONCRETE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.BLACK_CONCRETE_WALL.get()));

                        items.add(new ItemStack(ModItems.BROWN_CONCRETE_SLAB.get()));
                        items.add(new ItemStack(ModItems.BROWN_CONCRETE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.BROWN_CONCRETE_WALL.get()));

                        items.add(new ItemStack(ModItems.RED_CONCRETE_SLAB.get()));
                        items.add(new ItemStack(ModItems.RED_CONCRETE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.RED_CONCRETE_WALL.get()));

                        items.add(new ItemStack(ModItems.ORANGE_CONCRETE_SLAB.get()));
                        items.add(new ItemStack(ModItems.ORANGE_CONCRETE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.ORANGE_CONCRETE_WALL.get()));

                        items.add(new ItemStack(ModItems.YELLOW_CONCRETE_SLAB.get()));
                        items.add(new ItemStack(ModItems.YELLOW_CONCRETE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.YELLOW_CONCRETE_WALL.get()));

                        items.add(new ItemStack(ModItems.LIME_CONCRETE_SLAB.get()));
                        items.add(new ItemStack(ModItems.LIME_CONCRETE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.LIME_CONCRETE_WALL.get()));

                        items.add(new ItemStack(ModItems.GREEN_CONCRETE_SLAB.get()));
                        items.add(new ItemStack(ModItems.GREEN_CONCRETE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.GREEN_CONCRETE_WALL.get()));

                        items.add(new ItemStack(ModItems.CYAN_CONCRETE_SLAB.get()));
                        items.add(new ItemStack(ModItems.CYAN_CONCRETE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.CYAN_CONCRETE_WALL.get()));

                        items.add(new ItemStack(ModItems.LIGHT_BLUE_CONCRETE_SLAB.get()));
                        items.add(new ItemStack(ModItems.LIGHT_BLUE_CONCRETE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.LIGHT_BLUE_CONCRETE_WALL.get()));

                        items.add(new ItemStack(ModItems.BLUE_CONCRETE_SLAB.get()));
                        items.add(new ItemStack(ModItems.BLUE_CONCRETE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.BLUE_CONCRETE_WALL.get()));

                        items.add(new ItemStack(ModItems.PURPLE_CONCRETE_SLAB.get()));
                        items.add(new ItemStack(ModItems.PURPLE_CONCRETE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.PURPLE_CONCRETE_WALL.get()));

                        items.add(new ItemStack(ModItems.MAGENTA_CONCRETE_SLAB.get()));
                        items.add(new ItemStack(ModItems.MAGENTA_CONCRETE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.MAGENTA_CONCRETE_WALL.get()));

                        items.add(new ItemStack(ModItems.PINK_CONCRETE_SLAB.get()));
                        items.add(new ItemStack(ModItems.PINK_CONCRETE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.PINK_CONCRETE_WALL.get()));

                        items.add(new ItemStack(ModItems.POLISHED_WHITE_CONCRETE.get()));
                        items.add(new ItemStack(ModItems.POLISHED_WHITE_CONCRETE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.POLISHED_WHITE_CONCRETE_SLAB.get()));
                        items.add(new ItemStack(ModItems.POLISHED_WHITE_CONCRETE_WALL.get()));

                        items.add(new ItemStack(ModItems.POLISHED_LIGHT_GRAY_CONCRETE.get()));
                        items.add(new ItemStack(ModItems.POLISHED_LIGHT_GRAY_CONCRETE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.POLISHED_LIGHT_GRAY_CONCRETE_SLAB.get()));
                        items.add(new ItemStack(ModItems.POLISHED_LIGHT_GRAY_CONCRETE_WALL.get()));

                        items.add(new ItemStack(ModItems.POLISHED_GRAY_CONCRETE.get()));
                        items.add(new ItemStack(ModItems.POLISHED_GRAY_CONCRETE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.POLISHED_GRAY_CONCRETE_SLAB.get()));
                        items.add(new ItemStack(ModItems.POLISHED_GRAY_CONCRETE_WALL.get()));

                        items.add(new ItemStack(ModItems.POLISHED_BLACK_CONCRETE.get()));
                        items.add(new ItemStack(ModItems.POLISHED_BLACK_CONCRETE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.POLISHED_BLACK_CONCRETE_SLAB.get()));
                        items.add(new ItemStack(ModItems.POLISHED_BLACK_CONCRETE_WALL.get()));

                        items.add(new ItemStack(ModItems.POLISHED_BROWN_CONCRETE.get()));
                        items.add(new ItemStack(ModItems.POLISHED_BROWN_CONCRETE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.POLISHED_BROWN_CONCRETE_SLAB.get()));
                        items.add(new ItemStack(ModItems.POLISHED_BROWN_CONCRETE_WALL.get()));

                        items.add(new ItemStack(ModItems.POLISHED_RED_CONCRETE.get()));
                        items.add(new ItemStack(ModItems.POLISHED_RED_CONCRETE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.POLISHED_RED_CONCRETE_SLAB.get()));
                        items.add(new ItemStack(ModItems.POLISHED_RED_CONCRETE_WALL.get()));

                        items.add(new ItemStack(ModItems.POLISHED_ORANGE_CONCRETE.get()));
                        items.add(new ItemStack(ModItems.POLISHED_ORANGE_CONCRETE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.POLISHED_ORANGE_CONCRETE_SLAB.get()));
                        items.add(new ItemStack(ModItems.POLISHED_ORANGE_CONCRETE_WALL.get()));

                        items.add(new ItemStack(ModItems.POLISHED_YELLOW_CONCRETE.get()));
                        items.add(new ItemStack(ModItems.POLISHED_YELLOW_CONCRETE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.POLISHED_YELLOW_CONCRETE_SLAB.get()));
                        items.add(new ItemStack(ModItems.POLISHED_YELLOW_CONCRETE_WALL.get()));

                        items.add(new ItemStack(ModItems.POLISHED_LIME_CONCRETE.get()));
                        items.add(new ItemStack(ModItems.POLISHED_LIME_CONCRETE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.POLISHED_LIME_CONCRETE_SLAB.get()));
                        items.add(new ItemStack(ModItems.POLISHED_LIME_CONCRETE_WALL.get()));

                        items.add(new ItemStack(ModItems.POLISHED_GREEN_CONCRETE.get()));
                        items.add(new ItemStack(ModItems.POLISHED_GREEN_CONCRETE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.POLISHED_GREEN_CONCRETE_SLAB.get()));
                        items.add(new ItemStack(ModItems.POLISHED_GREEN_CONCRETE_WALL.get()));

                        items.add(new ItemStack(ModItems.POLISHED_CYAN_CONCRETE.get()));
                        items.add(new ItemStack(ModItems.POLISHED_CYAN_CONCRETE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.POLISHED_CYAN_CONCRETE_SLAB.get()));
                        items.add(new ItemStack(ModItems.POLISHED_CYAN_CONCRETE_WALL.get()));

                        items.add(new ItemStack(ModItems.POLISHED_LIGHT_BLUE_CONCRETE.get()));
                        items.add(new ItemStack(ModItems.POLISHED_LIGHT_BLUE_CONCRETE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.POLISHED_LIGHT_BLUE_CONCRETE_SLAB.get()));
                        items.add(new ItemStack(ModItems.POLISHED_LIGHT_BLUE_CONCRETE_WALL.get()));

                        items.add(new ItemStack(ModItems.POLISHED_BLUE_CONCRETE.get()));
                        items.add(new ItemStack(ModItems.POLISHED_BLUE_CONCRETE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.POLISHED_BLUE_CONCRETE_SLAB.get()));
                        items.add(new ItemStack(ModItems.POLISHED_BLUE_CONCRETE_WALL.get()));

                        items.add(new ItemStack(ModItems.POLISHED_PURPLE_CONCRETE.get()));
                        items.add(new ItemStack(ModItems.POLISHED_PURPLE_CONCRETE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.POLISHED_PURPLE_CONCRETE_SLAB.get()));
                        items.add(new ItemStack(ModItems.POLISHED_PURPLE_CONCRETE_WALL.get()));

                        items.add(new ItemStack(ModItems.POLISHED_MAGENTA_CONCRETE.get()));
                        items.add(new ItemStack(ModItems.POLISHED_MAGENTA_CONCRETE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.POLISHED_MAGENTA_CONCRETE_SLAB.get()));
                        items.add(new ItemStack(ModItems.POLISHED_MAGENTA_CONCRETE_WALL.get()));

                        items.add(new ItemStack(ModItems.POLISHED_PINK_CONCRETE.get()));
                        items.add(new ItemStack(ModItems.POLISHED_PINK_CONCRETE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.POLISHED_PINK_CONCRETE_SLAB.get()));
                        items.add(new ItemStack(ModItems.POLISHED_PINK_CONCRETE_WALL.get()));

                        items.add(new ItemStack(ModItems.WHITE_SAND.get()));
                        items.add(new ItemStack(ModItems.WHITE_SANDSTONE.get()));
                        items.add(new ItemStack(ModItems.WHITE_SANDSTONE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.WHITE_SANDSTONE_SLAB.get()));
                        items.add(new ItemStack(ModItems.WHITE_SANDSTONE_WALL.get()));
                        items.add(new ItemStack(ModItems.WHITE_SMOOTH_SANDSTONE.get()));
                        items.add(new ItemStack(ModItems.WHITE_SMOOTH_SANDSTONE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.WHITE_SMOOTH_SANDSTONE_SLAB.get()));
                        items.add(new ItemStack(ModItems.WHITE_SMOOTH_SANDSTONE_WALL.get()));

                        items.add(new ItemStack(ModItems.BLACK_SAND.get()));
                        items.add(new ItemStack(ModItems.BLACK_SANDSTONE.get()));
                        items.add(new ItemStack(ModItems.BLACK_SANDSTONE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.BLACK_SANDSTONE_SLAB.get()));
                        items.add(new ItemStack(ModItems.BLACK_SANDSTONE_WALL.get()));
                        items.add(new ItemStack(ModItems.BLACK_SMOOTH_SANDSTONE.get()));
                        items.add(new ItemStack(ModItems.BLACK_SMOOTH_SANDSTONE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.BLACK_SMOOTH_SANDSTONE_SLAB.get()));
                        items.add(new ItemStack(ModItems.BLACK_SMOOTH_SANDSTONE_WALL.get()));

                        items.add(new ItemStack(ModItems.RED_SAND.get()));
                        items.add(new ItemStack(ModItems.RED_SANDSTONE.get()));
                        items.add(new ItemStack(ModItems.RED_SANDSTONE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.RED_SANDSTONE_SLAB.get()));
                        items.add(new ItemStack(ModItems.RED_SANDSTONE_WALL.get()));
                        items.add(new ItemStack(ModItems.RED_SMOOTH_SANDSTONE.get()));
                        items.add(new ItemStack(ModItems.RED_SMOOTH_SANDSTONE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.RED_SMOOTH_SANDSTONE_SLAB.get()));
                        items.add(new ItemStack(ModItems.RED_SMOOTH_SANDSTONE_WALL.get()));

                        items.add(new ItemStack(ModItems.ORANGE_SAND.get()));
                        items.add(new ItemStack(ModItems.ORANGE_SANDSTONE.get()));
                        items.add(new ItemStack(ModItems.ORANGE_SANDSTONE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.ORANGE_SANDSTONE_SLAB.get()));
                        items.add(new ItemStack(ModItems.ORANGE_SANDSTONE_WALL.get()));
                        items.add(new ItemStack(ModItems.ORANGE_SMOOTH_SANDSTONE.get()));
                        items.add(new ItemStack(ModItems.ORANGE_SMOOTH_SANDSTONE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.ORANGE_SMOOTH_SANDSTONE_SLAB.get()));
                        items.add(new ItemStack(ModItems.ORANGE_SMOOTH_SANDSTONE_WALL.get()));

                        items.add(new ItemStack(ModItems.YELLOW_SAND.get()));
                        items.add(new ItemStack(ModItems.YELLOW_SANDSTONE.get()));
                        items.add(new ItemStack(ModItems.YELLOW_SANDSTONE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.YELLOW_SANDSTONE_SLAB.get()));
                        items.add(new ItemStack(ModItems.YELLOW_SANDSTONE_WALL.get()));
                        items.add(new ItemStack(ModItems.YELLOW_SMOOTH_SANDSTONE.get()));
                        items.add(new ItemStack(ModItems.YELLOW_SMOOTH_SANDSTONE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.YELLOW_SMOOTH_SANDSTONE_SLAB.get()));
                        items.add(new ItemStack(ModItems.YELLOW_SMOOTH_SANDSTONE_WALL.get()));

                        items.add(new ItemStack(ModItems.GREEN_SAND.get()));
                        items.add(new ItemStack(ModItems.GREEN_SANDSTONE.get()));
                        items.add(new ItemStack(ModItems.GREEN_SANDSTONE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.GREEN_SANDSTONE_SLAB.get()));
                        items.add(new ItemStack(ModItems.GREEN_SANDSTONE_WALL.get()));
                        items.add(new ItemStack(ModItems.GREEN_SMOOTH_SANDSTONE.get()));
                        items.add(new ItemStack(ModItems.GREEN_SMOOTH_SANDSTONE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.GREEN_SMOOTH_SANDSTONE_SLAB.get()));
                        items.add(new ItemStack(ModItems.GREEN_SMOOTH_SANDSTONE_WALL.get()));

                        items.add(new ItemStack(ModItems.BLUE_SAND.get()));
                        items.add(new ItemStack(ModItems.BLUE_SANDSTONE.get()));
                        items.add(new ItemStack(ModItems.BLUE_SANDSTONE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.BLUE_SANDSTONE_SLAB.get()));
                        items.add(new ItemStack(ModItems.BLUE_SANDSTONE_WALL.get()));
                        items.add(new ItemStack(ModItems.BLUE_SMOOTH_SANDSTONE.get()));
                        items.add(new ItemStack(ModItems.BLUE_SMOOTH_SANDSTONE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.BLUE_SMOOTH_SANDSTONE_SLAB.get()));
                        items.add(new ItemStack(ModItems.BLUE_SMOOTH_SANDSTONE_WALL.get()));

                        items.add(new ItemStack(ModItems.PINK_SAND.get()));
                        items.add(new ItemStack(ModItems.PINK_SANDSTONE.get()));
                        items.add(new ItemStack(ModItems.PINK_SANDSTONE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.PINK_SANDSTONE_SLAB.get()));
                        items.add(new ItemStack(ModItems.PINK_SANDSTONE_WALL.get()));
                        items.add(new ItemStack(ModItems.PINK_SMOOTH_SANDSTONE.get()));
                        items.add(new ItemStack(ModItems.PINK_SMOOTH_SANDSTONE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.PINK_SMOOTH_SANDSTONE_SLAB.get()));
                        items.add(new ItemStack(ModItems.PINK_SMOOTH_SANDSTONE_WALL.get()));

                        items.add(new ItemStack(ModItems.WHITE_MOSAIC_GLASS.get()));
                        items.add(new ItemStack(ModItems.WHITE_MOSAIC_GLASS_PANE.get()));

                        items.add(new ItemStack(ModItems.LIGHT_GRAY_MOSAIC_GLASS.get()));
                        items.add(new ItemStack(ModItems.LIGHT_GRAY_MOSAIC_GLASS_PANE.get()));

                        items.add(new ItemStack(ModItems.GRAY_MOSAIC_GLASS.get()));
                        items.add(new ItemStack(ModItems.GRAY_MOSAIC_GLASS_PANE.get()));

                        items.add(new ItemStack(ModItems.BLACK_MOSAIC_GLASS.get()));
                        items.add(new ItemStack(ModItems.BLACK_MOSAIC_GLASS_PANE.get()));

                        items.add(new ItemStack(ModItems.BROWN_MOSAIC_GLASS.get()));
                        items.add(new ItemStack(ModItems.BROWN_MOSAIC_GLASS_PANE.get()));

                        items.add(new ItemStack(ModItems.RED_MOSAIC_GLASS.get()));
                        items.add(new ItemStack(ModItems.RED_MOSAIC_GLASS_PANE.get()));

                        items.add(new ItemStack(ModItems.ORANGE_MOSAIC_GLASS.get()));
                        items.add(new ItemStack(ModItems.ORANGE_MOSAIC_GLASS_PANE.get()));

                        items.add(new ItemStack(ModItems.YELLOW_MOSAIC_GLASS.get()));
                        items.add(new ItemStack(ModItems.YELLOW_MOSAIC_GLASS_PANE.get()));

                        items.add(new ItemStack(ModItems.LIME_MOSAIC_GLASS.get()));
                        items.add(new ItemStack(ModItems.LIME_MOSAIC_GLASS_PANE.get()));

                        items.add(new ItemStack(ModItems.GREEN_MOSAIC_GLASS.get()));
                        items.add(new ItemStack(ModItems.GREEN_MOSAIC_GLASS_PANE.get()));

                        items.add(new ItemStack(ModItems.CYAN_MOSAIC_GLASS.get()));
                        items.add(new ItemStack(ModItems.CYAN_MOSAIC_GLASS_PANE.get()));

                        items.add(new ItemStack(ModItems.LIGHT_BLUE_MOSAIC_GLASS.get()));
                        items.add(new ItemStack(ModItems.LIGHT_BLUE_MOSAIC_GLASS_PANE.get()));

                        items.add(new ItemStack(ModItems.BLUE_MOSAIC_GLASS.get()));
                        items.add(new ItemStack(ModItems.BLUE_MOSAIC_GLASS_PANE.get()));

                        items.add(new ItemStack(ModItems.PURPLE_MOSAIC_GLASS.get()));
                        items.add(new ItemStack(ModItems.PURPLE_MOSAIC_GLASS_PANE.get()));

                        items.add(new ItemStack(ModItems.MAGENTA_MOSAIC_GLASS.get()));
                        items.add(new ItemStack(ModItems.MAGENTA_MOSAIC_GLASS_PANE.get()));

                        items.add(new ItemStack(ModItems.PINK_MOSAIC_GLASS.get()));
                        items.add(new ItemStack(ModItems.PINK_MOSAIC_GLASS_PANE.get()));

                        // Factory Glass
                        items.add(new ItemStack(ModItems.FACTORY_WHITE_GLASS.get()));
                        items.add(new ItemStack(ModItems.FACTORY_WHITE_GLASS_PANE.get()));
                        items.add(new ItemStack(ModItems.FACTORY_LIGHT_GRAY_GLASS.get()));
                        items.add(new ItemStack(ModItems.FACTORY_LIGHT_GRAY_GLASS_PANE.get()));
                        items.add(new ItemStack(ModItems.FACTORY_GRAY_GLASS.get()));
                        items.add(new ItemStack(ModItems.FACTORY_GRAY_GLASS_PANE.get()));
                        items.add(new ItemStack(ModItems.FACTORY_BLACK_GLASS.get()));
                        items.add(new ItemStack(ModItems.FACTORY_BLACK_GLASS_PANE.get()));
                        items.add(new ItemStack(ModItems.FACTORY_BROWN_GLASS.get()));
                        items.add(new ItemStack(ModItems.FACTORY_BROWN_GLASS_PANE.get()));
                        items.add(new ItemStack(ModItems.FACTORY_RED_GLASS.get()));
                        items.add(new ItemStack(ModItems.FACTORY_RED_GLASS_PANE.get()));
                        items.add(new ItemStack(ModItems.FACTORY_ORANGE_GLASS.get()));
                        items.add(new ItemStack(ModItems.FACTORY_ORANGE_GLASS_PANE.get()));
                        items.add(new ItemStack(ModItems.FACTORY_YELLOW_GLASS.get()));
                        items.add(new ItemStack(ModItems.FACTORY_YELLOW_GLASS_PANE.get()));
                        items.add(new ItemStack(ModItems.FACTORY_LIME_GLASS.get()));
                        items.add(new ItemStack(ModItems.FACTORY_LIME_GLASS_PANE.get()));
                        items.add(new ItemStack(ModItems.FACTORY_GREEN_GLASS.get()));
                        items.add(new ItemStack(ModItems.FACTORY_GREEN_GLASS_PANE.get()));
                        items.add(new ItemStack(ModItems.FACTORY_CYAN_GLASS.get()));
                        items.add(new ItemStack(ModItems.FACTORY_CYAN_GLASS_PANE.get()));
                        items.add(new ItemStack(ModItems.FACTORY_LIGHT_BLUE_GLASS.get()));
                        items.add(new ItemStack(ModItems.FACTORY_LIGHT_BLUE_GLASS_PANE.get()));
                        items.add(new ItemStack(ModItems.FACTORY_BLUE_GLASS.get()));
                        items.add(new ItemStack(ModItems.FACTORY_BLUE_GLASS_PANE.get()));
                        items.add(new ItemStack(ModItems.FACTORY_PURPLE_GLASS.get()));
                        items.add(new ItemStack(ModItems.FACTORY_PURPLE_GLASS_PANE.get()));
                        items.add(new ItemStack(ModItems.FACTORY_MAGENTA_GLASS.get()));
                        items.add(new ItemStack(ModItems.FACTORY_MAGENTA_GLASS_PANE.get()));
                        items.add(new ItemStack(ModItems.FACTORY_PINK_GLASS.get()));
                        items.add(new ItemStack(ModItems.FACTORY_PINK_GLASS_PANE.get()));

                        items.add(new ItemStack(ModItems.WHITE_GLAZED_GLASS.get()));
                        items.add(new ItemStack(ModItems.WHITE_GLAZED_GLASS_PANE.get()));

                        items.add(new ItemStack(ModItems.LIGHT_GRAY_GLAZED_GLASS.get()));
                        items.add(new ItemStack(ModItems.LIGHT_GRAY_GLAZED_GLASS_PANE.get()));

                        items.add(new ItemStack(ModItems.GRAY_GLAZED_GLASS.get()));
                        items.add(new ItemStack(ModItems.GRAY_GLAZED_GLASS_PANE.get()));

                        items.add(new ItemStack(ModItems.BLACK_GLAZED_GLASS.get()));
                        items.add(new ItemStack(ModItems.BLACK_GLAZED_GLASS_PANE.get()));

                        items.add(new ItemStack(ModItems.BROWN_GLAZED_GLASS.get()));
                        items.add(new ItemStack(ModItems.BROWN_GLAZED_GLASS_PANE.get()));

                        items.add(new ItemStack(ModItems.RED_GLAZED_GLASS.get()));
                        items.add(new ItemStack(ModItems.RED_GLAZED_GLASS_PANE.get()));

                        items.add(new ItemStack(ModItems.ORANGE_GLAZED_GLASS.get()));
                        items.add(new ItemStack(ModItems.ORANGE_GLAZED_GLASS_PANE.get()));

                        items.add(new ItemStack(ModItems.YELLOW_GLAZED_GLASS.get()));
                        items.add(new ItemStack(ModItems.YELLOW_GLAZED_GLASS_PANE.get()));

                        items.add(new ItemStack(ModItems.LIME_GLAZED_GLASS.get()));
                        items.add(new ItemStack(ModItems.LIME_GLAZED_GLASS_PANE.get()));

                        items.add(new ItemStack(ModItems.GREEN_GLAZED_GLASS.get()));
                        items.add(new ItemStack(ModItems.GREEN_GLAZED_GLASS_PANE.get()));

                        items.add(new ItemStack(ModItems.CYAN_GLAZED_GLASS.get()));
                        items.add(new ItemStack(ModItems.CYAN_GLAZED_GLASS_PANE.get()));

                        items.add(new ItemStack(ModItems.LIGHT_BLUE_GLAZED_GLASS.get()));
                        items.add(new ItemStack(ModItems.LIGHT_BLUE_GLAZED_GLASS_PANE.get()));

                        items.add(new ItemStack(ModItems.BLUE_GLAZED_GLASS.get()));
                        items.add(new ItemStack(ModItems.BLUE_GLAZED_GLASS_PANE.get()));

                        items.add(new ItemStack(ModItems.PURPLE_GLAZED_GLASS.get()));
                        items.add(new ItemStack(ModItems.PURPLE_GLAZED_GLASS_PANE.get()));

                        items.add(new ItemStack(ModItems.MAGENTA_GLAZED_GLASS.get()));
                        items.add(new ItemStack(ModItems.MAGENTA_GLAZED_GLASS_PANE.get()));

                        items.add(new ItemStack(ModItems.PINK_GLAZED_GLASS.get()));
                        items.add(new ItemStack(ModItems.PINK_GLAZED_GLASS_PANE.get()));

                        items.add(new ItemStack(ModItems.WHITE_ORNAMENT.get()));
                        items.add(new ItemStack(ModItems.ORANGE_ORNAMENT.get()));
                        items.add(new ItemStack(ModItems.MAGENTA_ORNAMENT.get()));
                        items.add(new ItemStack(ModItems.LIGHT_BLUE_ORNAMENT.get()));
                        items.add(new ItemStack(ModItems.YELLOW_ORNAMENT.get()));
                        items.add(new ItemStack(ModItems.LIME_ORNAMENT.get()));
                        items.add(new ItemStack(ModItems.PINK_ORNAMENT.get()));
                        items.add(new ItemStack(ModItems.GRAY_ORNAMENT.get()));
                        items.add(new ItemStack(ModItems.LIGHT_GRAY_ORNAMENT.get()));
                        items.add(new ItemStack(ModItems.CYAN_ORNAMENT.get()));
                        items.add(new ItemStack(ModItems.PURPLE_ORNAMENT.get()));
                        items.add(new ItemStack(ModItems.BLUE_ORNAMENT.get()));
                        items.add(new ItemStack(ModItems.BROWN_ORNAMENT.get()));
                        items.add(new ItemStack(ModItems.GREEN_ORNAMENT.get()));
                        items.add(new ItemStack(ModItems.RED_ORNAMENT.get()));
                        items.add(new ItemStack(ModItems.BLACK_ORNAMENT.get()));
                        items.add(new ItemStack(ModItems.GLASS_ORNAMENT.get()));
                        items.add(new ItemStack(ModItems.TINTED_GLASS_ORNAMENT.get()));

                        items.add(new ItemStack(ModItems.GLOW_STAR.get()));
                        items.add(new ItemStack(ModItems.WHITE_STAR.get()));
                        items.add(new ItemStack(ModItems.ORANGE_STAR.get()));
                        items.add(new ItemStack(ModItems.MAGENTA_STAR.get()));
                        items.add(new ItemStack(ModItems.LIGHT_BLUE_STAR.get()));
                        items.add(new ItemStack(ModItems.YELLOW_STAR.get()));
                        items.add(new ItemStack(ModItems.LIME_STAR.get()));
                        items.add(new ItemStack(ModItems.PINK_STAR.get()));
                        items.add(new ItemStack(ModItems.GRAY_STAR.get()));
                        items.add(new ItemStack(ModItems.LIGHT_GRAY_STAR.get()));
                        items.add(new ItemStack(ModItems.CYAN_STAR.get()));
                        items.add(new ItemStack(ModItems.PURPLE_STAR.get()));
                        items.add(new ItemStack(ModItems.BLUE_STAR.get()));
                        items.add(new ItemStack(ModItems.BROWN_STAR.get()));
                        items.add(new ItemStack(ModItems.GREEN_STAR.get()));
                        items.add(new ItemStack(ModItems.RED_STAR.get()));
                        items.add(new ItemStack(ModItems.BLACK_STAR.get()));

                        items.add(new ItemStack(ModItems.WHITE_STRING_LIGHT.get()));
                        items.add(new ItemStack(ModItems.ORANGE_STRING_LIGHT.get()));
                        items.add(new ItemStack(ModItems.MAGENTA_STRING_LIGHT.get()));
                        items.add(new ItemStack(ModItems.LIGHT_BLUE_STRING_LIGHT.get()));
                        items.add(new ItemStack(ModItems.YELLOW_STRING_LIGHT.get()));
                        items.add(new ItemStack(ModItems.LIME_STRING_LIGHT.get()));
                        items.add(new ItemStack(ModItems.PINK_STRING_LIGHT.get()));
                        items.add(new ItemStack(ModItems.GRAY_STRING_LIGHT.get()));
                        items.add(new ItemStack(ModItems.LIGHT_GRAY_STRING_LIGHT.get()));
                        items.add(new ItemStack(ModItems.CYAN_STRING_LIGHT.get()));
                        items.add(new ItemStack(ModItems.PURPLE_STRING_LIGHT.get()));
                        items.add(new ItemStack(ModItems.BLUE_STRING_LIGHT.get()));
                        items.add(new ItemStack(ModItems.BROWN_STRING_LIGHT.get()));
                        items.add(new ItemStack(ModItems.GREEN_STRING_LIGHT.get()));
                        items.add(new ItemStack(ModItems.RED_STRING_LIGHT.get()));
                        items.add(new ItemStack(ModItems.BLACK_STRING_LIGHT.get()));
                        items.add(new ItemStack(ModItems.MULTICOLOR_STRING_LIGHT.get()));

                        items.add(new ItemStack(ModItems.GLOW_LIGHTS.get()));
                        items.add(new ItemStack(ModItems.MULTICOLOR_GLOW_LIGHTS.get()));

                        items.add(new ItemStack(ModItems.DECORATED_POT.get()));
                        items.add(new ItemStack(ModItems.WHITE_DECORATED_POT.get()));
                        items.add(new ItemStack(ModItems.LIGHT_GRAY_DECORATED_POT.get()));
                        items.add(new ItemStack(ModItems.GRAY_DECORATED_POT.get()));
                        items.add(new ItemStack(ModItems.BLACK_DECORATED_POT.get()));
                        items.add(new ItemStack(ModItems.BROWN_DECORATED_POT.get()));
                        items.add(new ItemStack(ModItems.RED_DECORATED_POT.get()));
                        items.add(new ItemStack(ModItems.ORANGE_DECORATED_POT.get()));
                        items.add(new ItemStack(ModItems.YELLOW_DECORATED_POT.get()));
                        items.add(new ItemStack(ModItems.LIME_DECORATED_POT.get()));
                        items.add(new ItemStack(ModItems.GREEN_DECORATED_POT.get()));
                        items.add(new ItemStack(ModItems.CYAN_DECORATED_POT.get()));
                        items.add(new ItemStack(ModItems.LIGHT_BLUE_DECORATED_POT.get()));
                        items.add(new ItemStack(ModItems.BLUE_DECORATED_POT.get()));
                        items.add(new ItemStack(ModItems.PURPLE_DECORATED_POT.get()));
                        items.add(new ItemStack(ModItems.MAGENTA_DECORATED_POT.get()));
                        items.add(new ItemStack(ModItems.PINK_DECORATED_POT.get()));

                        items.add(new ItemStack(ModItems.FESTIVE_STOCKING.get()));
                        items.add(new ItemStack(ModItems.WHITE_FESTIVE_STOCKING.get()));
                        items.add(new ItemStack(ModItems.LIGHT_GRAY_FESTIVE_STOCKING.get()));
                        items.add(new ItemStack(ModItems.GRAY_FESTIVE_STOCKING.get()));
                        items.add(new ItemStack(ModItems.BLACK_FESTIVE_STOCKING.get()));
                        items.add(new ItemStack(ModItems.BROWN_FESTIVE_STOCKING.get()));
                        items.add(new ItemStack(ModItems.RED_FESTIVE_STOCKING.get()));
                        items.add(new ItemStack(ModItems.ORANGE_FESTIVE_STOCKING.get()));
                        items.add(new ItemStack(ModItems.YELLOW_FESTIVE_STOCKING.get()));
                        items.add(new ItemStack(ModItems.LIME_FESTIVE_STOCKING.get()));
                        items.add(new ItemStack(ModItems.GREEN_FESTIVE_STOCKING.get()));
                        items.add(new ItemStack(ModItems.CYAN_FESTIVE_STOCKING.get()));
                        items.add(new ItemStack(ModItems.LIGHT_BLUE_FESTIVE_STOCKING.get()));
                        items.add(new ItemStack(ModItems.BLUE_FESTIVE_STOCKING.get()));
                        items.add(new ItemStack(ModItems.PURPLE_FESTIVE_STOCKING.get()));
                        items.add(new ItemStack(ModItems.MAGENTA_FESTIVE_STOCKING.get()));
                        items.add(new ItemStack(ModItems.PINK_FESTIVE_STOCKING.get()));

                        // Colored Item Frames
                        items.add(new ItemStack(ModItems.WHITE_ITEM_FRAME.get()));
                        items.add(new ItemStack(ModItems.LIGHT_GRAY_ITEM_FRAME.get()));
                        items.add(new ItemStack(ModItems.GRAY_ITEM_FRAME.get()));
                        items.add(new ItemStack(ModItems.BLACK_ITEM_FRAME.get()));
                        items.add(new ItemStack(ModItems.BROWN_ITEM_FRAME.get()));
                        items.add(new ItemStack(ModItems.RED_ITEM_FRAME.get()));
                        items.add(new ItemStack(ModItems.ORANGE_ITEM_FRAME.get()));
                        items.add(new ItemStack(ModItems.YELLOW_ITEM_FRAME.get()));
                        items.add(new ItemStack(ModItems.LIME_ITEM_FRAME.get()));
                        items.add(new ItemStack(ModItems.GREEN_ITEM_FRAME.get()));
                        items.add(new ItemStack(ModItems.CYAN_ITEM_FRAME.get()));
                        items.add(new ItemStack(ModItems.LIGHT_BLUE_ITEM_FRAME.get()));
                        items.add(new ItemStack(ModItems.BLUE_ITEM_FRAME.get()));
                        items.add(new ItemStack(ModItems.PURPLE_ITEM_FRAME.get()));
                        items.add(new ItemStack(ModItems.MAGENTA_ITEM_FRAME.get()));
                        items.add(new ItemStack(ModItems.PINK_ITEM_FRAME.get()));
                        items.add(new ItemStack(ModItems.INVISIBLE_ITEM_FRAME.get()));

                        items.add(new ItemStack(ModItems.STONE_PILLAR.get()));
                        items.add(new ItemStack(ModItems.MOSSY_PILLAR.get()));
                        items.add(new ItemStack(ModItems.DEEPSLATE_PILLAR.get()));
                        items.add(new ItemStack(ModItems.QUARTZ_PILLAR.get()));

                        items.add(new ItemStack(ModItems.WHITE_CARPET_LAYERS.get()));
                        items.add(new ItemStack(ModItems.LIGHT_GRAY_CARPET_LAYERS.get()));
                        items.add(new ItemStack(ModItems.GRAY_CARPET_LAYERS.get()));
                        items.add(new ItemStack(ModItems.BLACK_CARPET_LAYERS.get()));
                        items.add(new ItemStack(ModItems.BROWN_CARPET_LAYERS.get()));
                        items.add(new ItemStack(ModItems.RED_CARPET_LAYERS.get()));
                        items.add(new ItemStack(ModItems.ORANGE_CARPET_LAYERS.get()));
                        items.add(new ItemStack(ModItems.YELLOW_CARPET_LAYERS.get()));
                        items.add(new ItemStack(ModItems.LIME_CARPET_LAYERS.get()));
                        items.add(new ItemStack(ModItems.GREEN_CARPET_LAYERS.get()));
                        items.add(new ItemStack(ModItems.CYAN_CARPET_LAYERS.get()));
                        items.add(new ItemStack(ModItems.LIGHT_BLUE_CARPET_LAYERS.get()));
                        items.add(new ItemStack(ModItems.BLUE_CARPET_LAYERS.get()));
                        items.add(new ItemStack(ModItems.PURPLE_CARPET_LAYERS.get()));
                        items.add(new ItemStack(ModItems.MAGENTA_CARPET_LAYERS.get()));
                        items.add(new ItemStack(ModItems.PINK_CARPET_LAYERS.get()));

                        items.add(new ItemStack(ModItems.OAK_LEAF_LAYERS.get()));
                        items.add(new ItemStack(ModItems.SPRUCE_LEAF_LAYERS.get()));
                        items.add(new ItemStack(ModItems.BIRCH_LEAF_LAYERS.get()));
                        items.add(new ItemStack(ModItems.JUNGLE_LEAF_LAYERS.get()));
                        items.add(new ItemStack(ModItems.ACACIA_LEAF_LAYERS.get()));
                        items.add(new ItemStack(ModItems.DARK_OAK_LEAF_LAYERS.get()));
                        items.add(new ItemStack(ModItems.AZALEA_LEAF_LAYERS.get()));
                        items.add(new ItemStack(ModItems.FLOWERING_AZALEA_LEAF_LAYERS.get()));
                        items.add(new ItemStack(ModItems.MANGROVE_LEAF_LAYERS.get()));
                        items.add(new ItemStack(ModItems.SNOWY_OAK_LEAF_LAYERS.get()));
                        items.add(new ItemStack(ModItems.SNOWY_SPRUCE_LEAF_LAYERS.get()));
                        items.add(new ItemStack(ModItems.SNOWY_BIRCH_LEAF_LAYERS.get()));
                        items.add(new ItemStack(ModItems.SNOWY_JUNGLE_LEAF_LAYERS.get()));
                        items.add(new ItemStack(ModItems.SNOWY_ACACIA_LEAF_LAYERS.get()));
                        items.add(new ItemStack(ModItems.SNOWY_DARK_OAK_LEAF_LAYERS.get()));
                        items.add(new ItemStack(ModItems.SNOWY_MANGROVE_LEAF_LAYERS.get()));
                        items.add(new ItemStack(ModItems.SNOWY_AZALEA_LEAF_LAYERS.get()));
                        items.add(
                                        new ItemStack(ModItems.SNOWY_FLOWERING_AZALEA_LEAF_LAYERS.get()));

                        items.add(new ItemStack(ModItems.OAK_LEAF_HEDGE.get()));
                        items.add(new ItemStack(ModItems.SPRUCE_LEAF_HEDGE.get()));
                        items.add(new ItemStack(ModItems.BIRCH_LEAF_HEDGE.get()));
                        items.add(new ItemStack(ModItems.JUNGLE_LEAF_HEDGE.get()));
                        items.add(new ItemStack(ModItems.ACACIA_LEAF_HEDGE.get()));
                        items.add(new ItemStack(ModItems.DARK_OAK_LEAF_HEDGE.get()));
                        items.add(new ItemStack(ModItems.AZALEA_LEAF_HEDGE.get()));
                        items.add(new ItemStack(ModItems.FLOWERING_AZALEA_LEAF_HEDGE.get()));
                        items.add(new ItemStack(ModItems.MANGROVE_LEAF_HEDGE.get()));
                        items.add(new ItemStack(ModItems.SNOWY_OAK_LEAF_HEDGE.get()));
                        items.add(new ItemStack(ModItems.SNOWY_SPRUCE_LEAF_HEDGE.get()));
                        items.add(new ItemStack(ModItems.SNOWY_BIRCH_LEAF_HEDGE.get()));
                        items.add(new ItemStack(ModItems.SNOWY_JUNGLE_LEAF_HEDGE.get()));
                        items.add(new ItemStack(ModItems.SNOWY_ACACIA_LEAF_HEDGE.get()));
                        items.add(new ItemStack(ModItems.SNOWY_DARK_OAK_LEAF_HEDGE.get()));
                        items.add(new ItemStack(ModItems.SNOWY_MANGROVE_LEAF_HEDGE.get()));
                        items.add(new ItemStack(ModItems.SNOWY_AZALEA_LEAF_HEDGE.get()));
                        items.add(
                                        new ItemStack(ModItems.SNOWY_FLOWERING_AZALEA_LEAF_HEDGE.get()));

                        items.add(new ItemStack(ModItems.SNOWY_OAK_LEAVES.get()));
                        items.add(new ItemStack(ModItems.SNOWY_SPRUCE_LEAVES.get()));
                        items.add(new ItemStack(ModItems.SNOWY_BIRCH_LEAVES.get()));
                        items.add(new ItemStack(ModItems.SNOWY_JUNGLE_LEAVES.get()));
                        items.add(new ItemStack(ModItems.SNOWY_ACACIA_LEAVES.get()));
                        items.add(new ItemStack(ModItems.SNOWY_DARK_OAK_LEAVES.get()));
                        items.add(new ItemStack(ModItems.SNOWY_MANGROVE_LEAVES.get()));
                        items.add(new ItemStack(ModItems.SNOWY_AZALEA_LEAVES.get()));
                        items.add(new ItemStack(ModItems.SNOWY_FLOWERING_AZALEA_LEAVES.get()));

                        items.add(new ItemStack(ModItems.RED_ROSE_VINES.get()));
                        items.add(new ItemStack(ModItems.BLACK_ROSE_VINES.get()));
                        items.add(new ItemStack(ModItems.BLUE_ROSE_VINES.get()));
                        items.add(new ItemStack(ModItems.WHITE_ROSE_VINES.get()));

                        items.add(new ItemStack(ModItems.RED_MONETS.get()));
                        items.add(new ItemStack(ModItems.BLUE_MONETS.get()));
                        items.add(new ItemStack(ModItems.PURPLE_MONETS.get()));
                        items.add(new ItemStack(ModItems.LIGHT_BLUE_MONETS.get()));
                        items.add(new ItemStack(ModItems.PINK_MONETS.get()));
                        items.add(new ItemStack(ModItems.YELLOW_MONETS.get()));

                        items.add(new ItemStack(ModItems.RED_PETAL.get()));
                        items.add(new ItemStack(ModItems.BLUE_PETAL.get()));
                        items.add(new ItemStack(ModItems.ORANGE_PETAL.get()));
                        items.add(new ItemStack(ModItems.PINK_PETAL.get()));
                        items.add(new ItemStack(ModItems.PURPLE_PETAL.get()));

                        items.add(new ItemStack(ModItems.CLOVER.get()));

                        items.add(new ItemStack(ModItems.SNOWY_SHORT_GRASS.get()));
                        items.add(new ItemStack(ModItems.SNOWY_TALL_GRASS.get()));
                        items.add(new ItemStack(ModItems.SNOWY_FERN.get()));
                        items.add(new ItemStack(ModItems.SNOWY_LARGE_FERN.get()));
                        items.add(new ItemStack(ModItems.SNOWY_BUSH.get()));
                        items.add(new ItemStack(ModItems.FROST_ROSE.get()));

                        items.add(new ItemStack(ModItems.RED_SPORE_BLOSSOM.get()));
                        items.add(new ItemStack(ModItems.CYAN_SPORE_BLOSSOM.get()));
                        items.add(new ItemStack(ModItems.BLUE_SPORE_BLOSSOM.get()));
                        items.add(new ItemStack(ModItems.PURPLE_SPORE_BLOSSOM.get()));
                        items.add(new ItemStack(ModItems.ORANGE_SPORE_BLOSSOM.get()));

                        items.add(new ItemStack(ModItems.BROWN_MUSHROOM_SHELVES.get()));
                        items.add(new ItemStack(ModItems.RED_MUSHROOM_SHELVES.get()));

                        items.add(new ItemStack(ModItems.CALCITE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.CALCITE_SLAB.get()));
                        items.add(new ItemStack(ModItems.CALCITE_WALL.get()));
                        items.add(new ItemStack(ModItems.MOSSY_CALCITE.get()));
                        items.add(new ItemStack(ModItems.MOSSY_CALCITE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.MOSSY_CALCITE_SLAB.get()));
                        items.add(new ItemStack(ModItems.MOSSY_CALCITE_WALL.get()));

                        items.add(new ItemStack(ModItems.MOSS_BLOCK_SLAB.get()));
                        items.add(new ItemStack(ModItems.MOSS_LAYERS.get()));
                        items.add(new ItemStack(ModItems.MOSS_OVERLAY.get()));
                        items.add(new ItemStack(ModItems.SNOW_STAIRS.get()));
                        items.add(new ItemStack(ModItems.SNOW_SLAB.get()));
                        items.add(new ItemStack(ModItems.SNOW_OVERLAY.get()));
                        items.add(new ItemStack(ModItems.SNOW_BRICKS.get()));
                        items.add(new ItemStack(ModItems.SNOW_BRICKS_STAIRS.get()));
                        items.add(new ItemStack(ModItems.SNOW_BRICKS_SLAB.get()));
                        items.add(new ItemStack(ModItems.SNOW_BRICKS_WALL.get()));
                        items.add(new ItemStack(ModItems.SNOWY_GRASS_BLOCK.get()));
                        items.add(new ItemStack(ModItems.SNOWY_GRASS_BLOCK_STAIRS.get()));
                        items.add(new ItemStack(ModItems.SNOWY_GRASS_BLOCK_SLAB.get()));

                        items.add(new ItemStack(ModItems.ICICLE.get()));
                        items.add(new ItemStack(ModItems.ICICLE_BLOCK.get()));
                        items.add(new ItemStack(ModItems.PACKED_ICICLE_BLOCK.get()));

                        items.add(new ItemStack(ModItems.PODZOL_SLAB.get()));
                        items.add(new ItemStack(ModItems.MYCELIUM_SLAB.get()));
                        items.add(new ItemStack(ModItems.DIRT_SLAB.get()));
                        items.add(new ItemStack(ModItems.MUD.get()));
                        items.add(new ItemStack(ModItems.MUD_SLAB.get()));
                        items.add(new ItemStack(ModItems.AMETHYST_BLOCK_SLAB.get()));
                        items.add(new ItemStack(ModItems.HAY_BALE_SLAB.get()));

                        items.add(new ItemStack(ModItems.DRIPSTONE_BLOCK_STAIRS.get()));
                        items.add(new ItemStack(ModItems.DRIPSTONE_BLOCK_SLAB.get()));
                        items.add(new ItemStack(ModItems.DRIPSTONE_BLOCK_WALL.get()));
                        items.add(new ItemStack(ModItems.POLISHED_BASALT_STAIRS.get()));
                        items.add(new ItemStack(ModItems.POLISHED_BASALT_SLAB.get()));
                        items.add(new ItemStack(ModItems.POLISHED_BASALT_WALL.get()));
                        items.add(new ItemStack(ModItems.SMOOTH_BASALT_STAIRS.get()));
                        items.add(new ItemStack(ModItems.SMOOTH_BASALT_SLAB.get()));
                        items.add(new ItemStack(ModItems.END_STONE_STAIRS.get()));
                        items.add(new ItemStack(ModItems.END_STONE_SLAB.get()));
                        items.add(new ItemStack(ModItems.END_STONE_WALL.get()));
                        items.add(new ItemStack(ModItems.QUARTZ_BLOCK_WALL.get()));
                        items.add(new ItemStack(ModItems.SMOOTH_QUARTZ_WALL.get()));
                        items.add(new ItemStack(ModItems.QUARTZ_PILLAR_STAIRS.get()));
                        items.add(new ItemStack(ModItems.QUARTZ_PILLAR_SLAB.get()));
                        items.add(new ItemStack(ModItems.QUARTZ_PILLAR_WALL.get()));
                        items.add(new ItemStack(ModItems.QUARTZ_BRICKS_STAIRS.get()));
                        items.add(new ItemStack(ModItems.QUARTZ_BRICKS_SLAB.get()));
                        items.add(new ItemStack(ModItems.QUARTZ_BRICKS_WALL.get()));
                        items.add(new ItemStack(ModItems.SMOOTH_SANDSTONE_WALL.get()));
                        items.add(new ItemStack(ModItems.PRISMARINE_BRICKS_WALL.get()));
                        items.add(new ItemStack(ModItems.DARK_PRISMARINE_WALL.get()));
                        items.add(new ItemStack(ModItems.STONE_WALL.get()));
                        items.add(new ItemStack(ModItems.SMOOTH_STONE_STAIRS.get()));

                        items.add(new ItemStack(ModItems.BEDROCK_STAIRS.get()));
                        items.add(new ItemStack(ModItems.BEDROCK_SLAB.get()));
                        items.add(new ItemStack(ModItems.BEDROCK_WALL.get()));
                        items.add(new ItemStack(ModItems.BEDROCK_PANE.get()));

                        items.add(new ItemStack(ModItems.OBSIDIAN_STAIRS.get()));
                        items.add(new ItemStack(ModItems.OBSIDIAN_SLAB.get()));

                        items.add(new ItemStack(ModItems.BIG_CANDLE.get()));
                        items.add(new ItemStack(ModItems.BIG_WHITE_CANDLE.get()));
                        items.add(new ItemStack(ModItems.BIG_ORANGE_CANDLE.get()));
                        items.add(new ItemStack(ModItems.BIG_MAGENTA_CANDLE.get()));
                        items.add(new ItemStack(ModItems.BIG_LIGHT_BLUE_CANDLE.get()));
                        items.add(new ItemStack(ModItems.BIG_YELLOW_CANDLE.get()));
                        items.add(new ItemStack(ModItems.BIG_LIME_CANDLE.get()));
                        items.add(new ItemStack(ModItems.BIG_PINK_CANDLE.get()));
                        items.add(new ItemStack(ModItems.BIG_GRAY_CANDLE.get()));
                        items.add(new ItemStack(ModItems.BIG_LIGHT_GRAY_CANDLE.get()));
                        items.add(new ItemStack(ModItems.BIG_CYAN_CANDLE.get()));
                        items.add(new ItemStack(ModItems.BIG_PURPLE_CANDLE.get()));
                        items.add(new ItemStack(ModItems.BIG_BLUE_CANDLE.get()));
                        items.add(new ItemStack(ModItems.BIG_BROWN_CANDLE.get()));
                        items.add(new ItemStack(ModItems.BIG_GREEN_CANDLE.get()));
                        items.add(new ItemStack(ModItems.BIG_RED_CANDLE.get()));
                        items.add(new ItemStack(ModItems.BIG_BLACK_CANDLE.get()));
                        items.add(new ItemStack(ModItems.BIG_AMETHYST_CANDLE.get()));
                        items.add(new ItemStack(ModItems.BIG_SCULK_CANDLE.get()));

                        items.add(new ItemStack(ModItems.FESTIVE_LAMP.get()));

                        items.add(new ItemStack(ModItems.SMOKE_VENT.get()));
                }
        };
        private static final Logger LOGGER = LogManager.getLogger();
        private static final java.util.concurrent.ExecutorService ASYNC_POOL = java.util.concurrent.Executors
                        .newCachedThreadPool(r -> {
                                Thread t = new Thread(r, "BuildScape-Async");
                                t.setDaemon(true);
                                return t;
                        });
        private static final int WORLD_LOAD_WAIT_TICKS = 40;
        private static final int PILLAR_SYNC_INTERVAL = 100;
        private static final int RECOVERY_DELAY_TICKS = 600;
        private static boolean serverFullyInitialized = false;
        private static boolean pillarDataLoadStarted = false;
        private static int worldLoadWaitTicks = 0;
        private static int pillarSyncTickCounter = 0;
        private static int recoveryDelayTicks = 0;
        private static boolean recoveryAttempted = false;

        public BuildScape() {
                IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
                modEventBus.addListener(this::commonSetup);

                com.kingodogo.buildscape.sound.ModSounds.SOUND_EVENTS.register(modEventBus);
                ModBlocks.BLOCKS.register(modEventBus);
                ModItems.ITEMS.register(modEventBus);
                com.kingodogo.buildscape.particle.ModParticles.PARTICLES.register(
                                modEventBus);
                com.kingodogo.buildscape.block.ModBlockEntities.BLOCK_ENTITIES.register(
                                modEventBus);
                com.kingodogo.buildscape.entity.ModEntities.ENTITIES.register(modEventBus);
                com.kingodogo.buildscape.recipe.ModRecipeSerializers.RECIPE_SERIALIZERS.register(
                                modEventBus);

                com.kingodogo.buildscape.worldgen.ModBlockStateProviderTypes.BLOCK_STATE_PROVIDER_TYPES.register(
                                modEventBus);
                com.kingodogo.buildscape.worldgen.ModTrunkPlacerTypes.TRUNK_PLACER_TYPES.register(
                                modEventBus);
                com.kingodogo.buildscape.worldgen.ModFoliagePlacerTypes.FOLIAGE_PLACER_TYPES.register(
                                modEventBus);
                com.kingodogo.buildscape.worldgen.ModTreeDecoratorTypes.TREE_DECORATOR_TYPES.register(
                                modEventBus);
                com.kingodogo.buildscape.worldgen.ModConfiguredFeatures.CONFIGURED_FEATURES.register(
                                modEventBus);
                com.kingodogo.buildscape.worldgen.ModPlacementModifiers.PLACEMENT_MODIFIERS.register(
                                modEventBus);

                MinecraftForge.EVENT_BUS.register(this);

                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                        net.minecraft.client.Minecraft.getInstance().getResourcePackRepository().addPackFinder(new com.kingodogo.buildscape.client.DynamicVerticalSlabPackFinder());
                });

                LOGGER.info("BuildScape mod initialized!");
        }

        public static Logger getLogger() {
                return LOGGER;
        }

        private static ItemStack getModIcon() {
                return new ItemStack(ModItems.BIT_OXIDIZED_COPPER_BLOCK.get());
        }

        public static boolean isServerFullyInitialized() {
                return serverFullyInitialized;
        }

        public static java.util.concurrent.ExecutorService getAsyncPool() {
                return ASYNC_POOL;
        }

        private void commonSetup(final FMLCommonSetupEvent event) {
                LOGGER.info("Buildscape mod initialized!");

                // Register custom gamerules
                com.kingodogo.buildscape.world.ModGameRules.register();

                event.enqueueWork(() -> {
                        com.kingodogo.buildscape.network.ModMessages.register();
                });

                event.enqueueWork(() -> {
                        com.kingodogo.buildscape.sound.ModSounds.COPPER_GRATE_SOUNDS();
                        com.kingodogo.buildscape.sound.ModSounds.COPPER_BULB_SOUNDS();
                        com.kingodogo.buildscape.sound.ModSounds.MANGROVE_ROOTS_SOUNDS();
                        com.kingodogo.buildscape.sound.ModSounds.MUDDY_MANGROVE_ROOTS_SOUNDS();
                        LOGGER.info("Custom sound types initialized");

                        com.kingodogo.buildscape.config.PillarParticleConfig.get();
                        LOGGER.info("Pillar particle config initialized");

                        com.kingodogo.buildscape.block.ModWoodTypes.MANGROVE.getClass();
                        com.kingodogo.buildscape.block.ModWoodTypes.BAMBOO.getClass();
                        LOGGER.info("Mangrove and Bamboo WoodTypes initialized for signs");

                        LOGGER.info("Mangrove tree configured features registered in code");

                        // Composter recipes
                        // Rose Vines (0.5 - like vanilla vines)
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.RED_ROSE_VINES.get(),
                                        0.5f);
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.BLACK_ROSE_VINES.get(),
                                        0.5f);
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.BLUE_ROSE_VINES.get(),
                                        0.5f);
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.WHITE_ROSE_VINES.get(),
                                        0.5f);

                        // Frost Rose (0.65 - like flowers)
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.FROST_ROSE.get(),
                                        0.65f);

                        // Monets (0.65 - like flowers)
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.RED_MONETS.get(),
                                        0.65f);
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.BLUE_MONETS.get(),
                                        0.65f);
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.PURPLE_MONETS.get(),
                                        0.65f);
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                                        .put(ModItems.LIGHT_BLUE_MONETS.get(), 0.65f);
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.PINK_MONETS.get(),
                                        0.65f);
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.YELLOW_MONETS.get(),
                                        0.65f);

                        // Petals (0.65 - like pink petals)
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.RED_PETAL.get(),
                                        0.65f);
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.BLUE_PETAL.get(),
                                        0.65f);
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.ORANGE_PETAL.get(),
                                        0.65f);
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.PINK_PETAL.get(),
                                        0.65f);
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.PURPLE_PETAL.get(),
                                        0.65f);

                        // Spore Blossoms (0.65 - like vanilla spore blossom)
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                                        .put(ModItems.RED_SPORE_BLOSSOM.get(), 0.65f);
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                                        .put(ModItems.CYAN_SPORE_BLOSSOM.get(), 0.65f);
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                                        .put(ModItems.BLUE_SPORE_BLOSSOM.get(), 0.65f);
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                                        .put(ModItems.PURPLE_SPORE_BLOSSOM.get(), 0.65f);
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                                        .put(ModItems.ORANGE_SPORE_BLOSSOM.get(), 0.65f);

                        // Snowy Foliage (0.3 - like grass/fern)
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                                        .put(ModItems.SNOWY_SHORT_GRASS.get(), 0.3f);
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.SNOWY_TALL_GRASS.get(),
                                        0.3f);
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.SNOWY_FERN.get(),
                                        0.3f);
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.SNOWY_LARGE_FERN.get(),
                                        0.3f);
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.SNOWY_BUSH.get(),
                                        0.3f);

                        // Mangrove Leaves (0.3 - like leaves)
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.MANGROVE_LEAVES.get(),
                                        0.3f);

                        // Snowy Leaves (0.3 - like leaves)
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.SNOWY_LEAVES.get(),
                                        0.3f);
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.SNOWY_OAK_LEAVES.get(),
                                        0.3f);
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                                        .put(ModItems.SNOWY_SPRUCE_LEAVES.get(), 0.3f);
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                                        .put(ModItems.SNOWY_BIRCH_LEAVES.get(), 0.3f);
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                                        .put(ModItems.SNOWY_JUNGLE_LEAVES.get(), 0.3f);
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                                        .put(ModItems.SNOWY_ACACIA_LEAVES.get(), 0.3f);
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                                        .put(ModItems.SNOWY_DARK_OAK_LEAVES.get(), 0.3f);
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                                        .put(ModItems.SNOWY_MANGROVE_LEAVES.get(), 0.3f);
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                                        .put(ModItems.SNOWY_AZALEA_LEAVES.get(), 0.3f);
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                                        .put(ModItems.SNOWY_FLOWERING_AZALEA_LEAVES.get(), 0.3f);

                        // Mushroom Shelves (0.65 - like small mushrooms)
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                                        .put(ModItems.BROWN_MUSHROOM_SHELVES.get(), 0.65f);
                        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES
                                        .put(ModItems.RED_MUSHROOM_SHELVES.get(), 0.65f);

                        LOGGER.info("Composter recipes registered");
                });
        }

        @SubscribeEvent
        public void onServerStarting(ServerStartingEvent event) {
                LOGGER.info("Buildscape mod loaded on server");

                serverFullyInitialized = false;
                pillarDataLoadStarted = false;
                worldLoadWaitTicks = 0;
                recoveryDelayTicks = 0;
                recoveryAttempted = false;

                com.kingodogo.buildscape.config.PillarIdManager.resetWorldCache();
        }

        @SubscribeEvent
        public void onRegisterCommands(net.minecraftforge.event.RegisterCommandsEvent event) {
                // Gamerules are automatically available through /gamerule command once
                // registered
                // This event handler ensures commands are properly initialized
                LOGGER.debug("Commands registered - fastLeafDecay gamerule should be available");

                // Register /buildscape recover PillarData command
                com.mojang.brigadier.builder.LiteralArgumentBuilder<net.minecraft.commands.CommandSourceStack> buildscapeCommand = com.mojang.brigadier.builder.LiteralArgumentBuilder.<net.minecraft.commands.CommandSourceStack>literal(
                                "buildscape")
                                .then(com.mojang.brigadier.builder.LiteralArgumentBuilder.<net.minecraft.commands.CommandSourceStack>literal(
                                                "recover")
                                                .then(com.mojang.brigadier.builder.LiteralArgumentBuilder.<net.minecraft.commands.CommandSourceStack>literal(
                                                                "PillarData")
                                                                .requires(source -> source.hasPermission(2)) // Requires
                                                                // OP level
                                                                // 2
                                                                .executes(context -> {
                                                                        net.minecraft.commands.CommandSourceStack source = context
                                                                                        .getSource();
                                                                        net.minecraft.server.MinecraftServer server = source
                                                                                        .getServer();

                                                                        if (server == null || !server.isRunning()) {
                                                                                source.sendFailure(
                                                                                                new net.minecraft.network.chat.TextComponent(
                                                                                                                "Server is not running"));
                                                                                return 0;
                                                                        }

                                                                        com.kingodogo.buildscape.config.PillarIdManager manager = com.kingodogo.buildscape.config.PillarIdManager
                                                                                        .get();

                                                                        if (manager == null) {
                                                                                source.sendFailure(
                                                                                                new net.minecraft.network.chat.TextComponent(
                                                                                                                "PillarIdManager is not available"));
                                                                                return 0;
                                                                        }

                                                                        source.sendSuccess(
                                                                                        new net.minecraft.network.chat.TextComponent(
                                                                                                        "Starting pillar recovery..."),
                                                                                        true);

                                                                        // Schedule recovery on next server tick (don't
                                                                        // clear colors)
                                                                        server.execute(() -> {
                                                                                try {
                                                                                        manager.recoverPillarsFromWorld(
                                                                                                        server, false); // false
                                                                                        // =
                                                                                        // don't
                                                                                        // clear
                                                                                        // colors
                                                                                        source.sendSuccess(
                                                                                                        new net.minecraft.network.chat.TextComponent(
                                                                                                                        "Pillar recovery completed. Check console for details."),
                                                                                                        true);
                                                                                } catch (Exception e) {
                                                                                        source.sendFailure(
                                                                                                        new net.minecraft.network.chat.TextComponent(
                                                                                                                        "Error during recovery: "
                                                                                                                                        + e.getMessage()));
                                                                                        e.printStackTrace();
                                                                                }
                                                                        });

                                                                        return 1;
                                                                })));

                event.getDispatcher().register(buildscapeCommand);
        }

        @SubscribeEvent
        public void onServerStarted(
                        net.minecraftforge.event.server.ServerStartedEvent event) {
                LOGGER.info(
                                "BuildScape: Server started - will load pillar data after world is fully loaded");

                pillarDataLoadStarted = false;
                worldLoadWaitTicks = 0;
        }

        @SubscribeEvent
        public void onServerStopped(
                        net.minecraftforge.event.server.ServerStoppedEvent event) {
                LOGGER.info("BuildScape: Server stopped - syncing colors and resetting pillar data state");

                // Try to sync and save before resetting (server might still be accessible)
                try {
                        net.minecraft.server.MinecraftServer server = event.getServer();
                        if (server != null) {
                                com.kingodogo.buildscape.config.PillarIdManager manager = com.kingodogo.buildscape.config.PillarIdManager
                                                .get();
                                if (manager != null && manager.hasLoaded()) {
                                        // Sync colors FROM NBT TO manager before resetting
                                        manager.syncColorsFromNBTToManager(server);
                                        // Save main file (pillar-ids.dat)
                                        manager.saveImmediate();
                                        // Save backup file (pillar-ids.bak.dat) - only saved on world save/server close
                                        manager.saveBackupFile();
                                        LOGGER.info("BuildScape: Colors synced and saved (main + backup) on server stop");
                                }
                        }
                } catch (Exception e) {
                        LOGGER.error("BuildScape: Error syncing/saving colors on server stop: " + e.getMessage());
                }

                serverFullyInitialized = false;
                pillarDataLoadStarted = false;
                worldLoadWaitTicks = 0;
                recoveryDelayTicks = 0;
                recoveryAttempted = false;

                // Full reset on server stop - clear all data from memory
                // File is already saved above, so this just clears memory
                com.kingodogo.buildscape.config.PillarIdManager.fullReset();
        }

        @SubscribeEvent
        public void onPlayerJoin(
                        net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event) {
                if (event.getPlayer() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {

                        if (!serverFullyInitialized) {
                                com.kingodogo.buildscape.config.PillarIdManager.resetWorldCache();

                            // Register callback to sync config when file changes
                            com.kingodogo.buildscape.config.PillarParticleConfig.addConfigReloadCallback((isRemote) -> {
                                if (!isRemote) {
                                    net.minecraft.server.MinecraftServer server = net.minecraftforge.server.ServerLifecycleHooks
                                            .getCurrentServer();
                                    if (server != null && server.isRunning()
                                            && server.getPlayerList().getPlayerCount() > 0) {
                                        com.kingodogo.buildscape.config.PillarParticleConfig serverConfig = com.kingodogo.buildscape.config.PillarParticleConfig
                                                .get();
                                        com.kingodogo.buildscape.network.SyncConfigPacket configPacket = new com.kingodogo.buildscape.network.SyncConfigPacket(
                                                serverConfig);
                                        com.kingodogo.buildscape.network.ModMessages.INSTANCE.send(
                                                net.minecraftforge.network.PacketDistributor.ALL
                                                        .noArg(),
                                                configPacket);
                                    }
                                }
                            });
                        }

                        com.kingodogo.buildscape.config.PillarIdManager manager = com.kingodogo.buildscape.config.PillarIdManager
                                        .get();
                        if (!manager.hasLoaded()) {
                                LOGGER.info(
                                                "BuildScape: First player joined - loading pillar data file asynchronously...");
                                manager.load();
                        } else {
                                serverFullyInitialized = true;
                        }

                        com.kingodogo.buildscape.config.PillarParticleConfig serverConfig = com.kingodogo.buildscape.config.PillarParticleConfig
                                        .get();
                        com.kingodogo.buildscape.network.SyncConfigPacket configPacket = new com.kingodogo.buildscape.network.SyncConfigPacket(
                                        serverConfig);
                        com.kingodogo.buildscape.network.ModMessages.INSTANCE.send(
                                        net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> serverPlayer),
                                        configPacket);
                        LOGGER.debug(
                                        "BuildScape: Sent server config to player {}",
                                        serverPlayer.getName().getString());

                        LOGGER.info(
                                        "BuildScape: Player joined - pillars will sync as chunks load");
                }
        }

        @SubscribeEvent
        public void onPlayerLogout(
                        net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent event) {
                LOGGER.info(
                                "BuildScape: Player logged out - resetting state for world switch");

                serverFullyInitialized = false;
                pillarDataLoadStarted = false;
                worldLoadWaitTicks = 0;
                pillarSyncTickCounter = 0;
                recoveryDelayTicks = 0;
                recoveryAttempted = false;
        }

        @SubscribeEvent
        public void onWorldUnload(
                        net.minecraftforge.event.world.WorldEvent.Unload event) {
                if (event.getWorld() instanceof net.minecraft.server.level.ServerLevel) {
                        LOGGER.info("BuildScape: World unloading - saving manager data and syncing colors from NBT");

                        // IMPORTANT: Save manager data FIRST, then sync colors FROM NBT TO manager
                        // This ensures colors are saved even if block entities are already unloaded
                        try {
                                net.minecraft.server.MinecraftServer server = net.minecraftforge.server.ServerLifecycleHooks
                                                .getCurrentServer();
                                if (server != null && server.isRunning()) {
                                        com.kingodogo.buildscape.config.PillarIdManager manager = com.kingodogo.buildscape.config.PillarIdManager
                                                        .get();
                                        if (manager != null && manager.hasLoaded()) {
                                                // Sync colors FROM NBT TO manager (in case NBT has colors not in
                                                // manager)
                                                manager.syncColorsFromNBTToManager(server);

                                                // Save main file (pillar-ids.dat)
                                                manager.saveImmediate();

                                                // Save backup file (pillar-ids.bak.dat) - only saved on world
                                                // save/server close
                                                manager.saveBackupFile();

                                                LOGGER.info("BuildScape: Colors synced and saved (main + backup) before world unload");
                                        }
                                }
                        } catch (Exception e) {
                                LOGGER.error("BuildScape: Error syncing/saving colors before world unload: "
                                                + e.getMessage());
                                e.printStackTrace();
                        }

                        LOGGER.info("BuildScape: World unloading - resetting cached data");
                        com.kingodogo.buildscape.config.PillarIdManager.resetWorldCache();

                        serverFullyInitialized = false;
                        pillarDataLoadStarted = false;
                        worldLoadWaitTicks = 0;
                        pillarSyncTickCounter = 0;
                        recoveryDelayTicks = 0;
                        recoveryAttempted = false;
                }
        }

        @SubscribeEvent
        public void onChunkLoad(
                        net.minecraftforge.event.world.ChunkEvent.Load event) {
                if (event.getWorld() instanceof net.minecraft.server.level.ServerLevel serverLevel) {

                        if (!serverFullyInitialized) {
                                return;
                        }

                        net.minecraft.server.MinecraftServer server = serverLevel.getServer();
                        if (server == null || !server.isRunning()) {
                                return;
                        }

                        if (server.getPlayerList().getPlayerCount() == 0) {
                                return;
                        }

                        if (event.getChunk() instanceof net.minecraft.world.level.chunk.LevelChunk chunk) {

                                if (!chunk
                                                .getStatus()
                                                .isOrAfter(net.minecraft.world.level.chunk.ChunkStatus.FULL)) {
                                }
                        }
                }
        }

        @SubscribeEvent
        public void onServerTick(
                        net.minecraftforge.event.TickEvent.ServerTickEvent event) {
                if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) {
                        return;
                }

                net.minecraft.server.MinecraftServer server = net.minecraftforge.server.ServerLifecycleHooks
                                .getCurrentServer();
                if (server == null
                                || !server.isRunning()
                                || server.getPlayerList().getPlayerCount() == 0) {
                        return;
                }

                if (!serverFullyInitialized) {
                        com.kingodogo.buildscape.config.PillarIdManager manager = com.kingodogo.buildscape.config.PillarIdManager
                                        .get();
                        if (manager != null && manager.hasLoaded()) {
                                serverFullyInitialized = true;
                                LOGGER.info(
                                                "BuildScape: Pillar data loaded - server fully initialized");
                        } else {
                                return;
                        }
                }

                // Check and run scheduled recovery (runs after world load, preserves colors)
                com.kingodogo.buildscape.config.PillarIdManager.checkAndRunScheduledRecovery();

                recoveryDelayTicks++;
                if (recoveryDelayTicks >= RECOVERY_DELAY_TICKS
                                && !recoveryAttempted
                                && serverFullyInitialized) {
                        recoveryAttempted = true;
                        com.kingodogo.buildscape.config.PillarIdManager manager = com.kingodogo.buildscape.config.PillarIdManager
                                        .get();
                        if (manager != null) {
                                try {
                                        java.nio.file.Path worldPath = server.getWorldPath(
                                                        net.minecraft.world.level.storage.LevelResource.ROOT);
                                        java.io.File dataFile = worldPath
                                                        .resolve("buildscape/pillar-ids.dat")
                                                        .toFile();

                                        boolean needsRecovery = false;
                                        if (!dataFile.exists()) {
                                                needsRecovery = true;
                                        } else if (dataFile.length() == 0) {
                                                needsRecovery = true;
                                        } else if (dataFile.length() <= 2) {
                                                try (java.io.FileReader fr = new java.io.FileReader(dataFile)) {
                                                        char[] buffer = new char[10];
                                                        int read = fr.read(buffer);
                                                        String content = new String(buffer, 0, read).trim();
                                                        if (content.equals("{}") || content.isEmpty()) {
                                                                needsRecovery = true;
                                                        }
                                                } catch (Exception e) {
                                                        needsRecovery = true;
                                                }
                                        } else {
                                                if (manager.getPillarCount() == 0) {
                                                }
                                        }

                                        // Automatic recovery disabled - use /buildscape recover PillarData command
                                        // instead
                                        // This prevents colors from being cleared automatically
                                        if (needsRecovery) {
                                                System.out.println(
                                                                "BuildScape: Pillar data file is empty or missing. Use /buildscape recover PillarData to recover pillars.");
                                        }
                                } catch (Exception e) {
                                        System.err.println(
                                                        "BuildScape: Error during delayed recovery: " + e.getMessage());
                                }
                        }
                }

                pillarSyncTickCounter++;
                if (pillarSyncTickCounter >= PILLAR_SYNC_INTERVAL) {
                        pillarSyncTickCounter = 0;

                        try {
                                com.kingodogo.buildscape.config.PillarIdManager manager = com.kingodogo.buildscape.config.PillarIdManager
                                                .get();

                                for (net.minecraft.server.level.ServerLevel level : server.getAllLevels()) {
                                        if (level == null) {
                                                continue;
                                        }

                                        if (!level.getServer().isRunning()) {
                                                continue;
                                        }

                                        String dimensionKey = com.kingodogo.buildscape.config.PillarIdManager
                                                        .getDimensionKey(
                                                                        level);

                                        for (String pillarId : manager.getAllPillarIds()) {
                                                try {
                                                        com.kingodogo.buildscape.config.PillarIdManager.PillarData data = manager
                                                                        .getPillarData(pillarId);
                                                        if (data == null || !data.dimension.equals(dimensionKey)) {
                                                                continue;
                                                        }
                                                        if (!data.hasColors()) {
                                                                continue;
                                                        }

                                                        net.minecraft.core.BlockPos pos = data.getBlockPos();

                                                        if (!level.hasChunkAt(pos)) {
                                                                continue;
                                                        }

                                                        net.minecraft.world.level.chunk.ChunkAccess chunk = level
                                                                        .getChunk(pos);
                                                        if (!(chunk instanceof net.minecraft.world.level.chunk.LevelChunk)) {
                                                                continue;
                                                        }

                                                        if (!chunk
                                                                        .getStatus()
                                                                        .isOrAfter(net.minecraft.world.level.chunk.ChunkStatus.FULL)) {
                                                                continue;
                                                        }

                                                        net.minecraft.world.level.block.entity.BlockEntity be = level
                                                                        .getBlockEntity(pos);
                                                        if (be instanceof com.kingodogo.buildscape.block.PillarBlockEntity pillarBE) {

                                                                if (pillarBE.getPillarId() == null
                                                                                || !pillarBE.getPillarId()
                                                                                                .equals(data.id)) {
                                                                        pillarBE.forceSetColors(data.getColors(),
                                                                                        data.id);
                                                                        LOGGER.debug(
                                                                                        "Force synced pillar {} with {} colors",
                                                                                        data.id,
                                                                                        data.getColorCount());
                                                                }
                                                        }
                                                } catch (Exception e) {
                                                        LOGGER.debug(
                                                                        "Error syncing pillar " + pillarId + ": "
                                                                                        + e.getMessage());
                                                }
                                        }
                                }
                        } catch (Exception e) {
                                LOGGER.debug("Error in periodic pillar sync: " + e.getMessage());
                        }
                }
        }

        @SubscribeEvent
        public void onWandererTrades(
                        net.minecraftforge.event.village.WandererTradesEvent event) {
                event
                                .getGenericTrades()
                                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                                                new net.minecraft.world.item.ItemStack(
                                                                net.minecraft.world.item.Items.EMERALD,
                                                                1),
                                                new net.minecraft.world.item.ItemStack(ModItems.RED_MONETS.get(), 1),
                                                2,
                                                1,
                                                0.05f));

                event
                                .getGenericTrades()
                                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                                                new net.minecraft.world.item.ItemStack(
                                                                net.minecraft.world.item.Items.EMERALD,
                                                                1),
                                                new net.minecraft.world.item.ItemStack(ModItems.BLUE_MONETS.get(), 1),
                                                2,
                                                1,
                                                0.05f));

                event
                                .getGenericTrades()
                                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                                                new net.minecraft.world.item.ItemStack(
                                                                net.minecraft.world.item.Items.EMERALD,
                                                                1),
                                                new net.minecraft.world.item.ItemStack(
                                                                ModItems.PURPLE_MONETS.get(),
                                                                1),
                                                2,
                                                1,
                                                0.05f));

                event
                                .getGenericTrades()
                                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                                                new net.minecraft.world.item.ItemStack(
                                                                net.minecraft.world.item.Items.EMERALD,
                                                                1),
                                                new net.minecraft.world.item.ItemStack(
                                                                ModItems.LIGHT_BLUE_MONETS.get(),
                                                                1),
                                                2,
                                                1,
                                                0.05f));

                event
                                .getGenericTrades()
                                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                                                new net.minecraft.world.item.ItemStack(
                                                                net.minecraft.world.item.Items.EMERALD,
                                                                1),
                                                new net.minecraft.world.item.ItemStack(ModItems.PINK_MONETS.get(), 1),
                                                2,
                                                1,
                                                0.05f));

                event
                                .getGenericTrades()
                                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                                                new net.minecraft.world.item.ItemStack(
                                                                net.minecraft.world.item.Items.EMERALD,
                                                                1),
                                                new net.minecraft.world.item.ItemStack(
                                                                ModItems.YELLOW_MONETS.get(),
                                                                1),
                                                2,
                                                1,
                                                0.05f));

                event
                                .getGenericTrades()
                                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                                                new net.minecraft.world.item.ItemStack(
                                                                net.minecraft.world.item.Items.EMERALD,
                                                                1),
                                                new net.minecraft.world.item.ItemStack(ModItems.CLOVER.get(), 4),
                                                2,
                                                1,
                                                0.05f));

                event
                                .getGenericTrades()
                                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                                                new net.minecraft.world.item.ItemStack(
                                                                net.minecraft.world.item.Items.EMERALD,
                                                                1),
                                                new net.minecraft.world.item.ItemStack(
                                                                ModItems.RED_ROSE_VINES.get(),
                                                                1),
                                                2,
                                                1,
                                                0.05f));

                event
                                .getGenericTrades()
                                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                                                new net.minecraft.world.item.ItemStack(
                                                                net.minecraft.world.item.Items.EMERALD,
                                                                1),
                                                new net.minecraft.world.item.ItemStack(
                                                                ModItems.BLACK_ROSE_VINES.get(),
                                                                1),
                                                2,
                                                1,
                                                0.05f));

                event
                                .getGenericTrades()
                                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                                                new net.minecraft.world.item.ItemStack(
                                                                net.minecraft.world.item.Items.EMERALD,
                                                                1),
                                                new net.minecraft.world.item.ItemStack(
                                                                ModItems.BLUE_ROSE_VINES.get(),
                                                                1),
                                                2,
                                                1,
                                                0.05f));

                event
                                .getGenericTrades()
                                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                                                new net.minecraft.world.item.ItemStack(
                                                                net.minecraft.world.item.Items.EMERALD,
                                                                1),
                                                new net.minecraft.world.item.ItemStack(
                                                                ModItems.WHITE_ROSE_VINES.get(),
                                                                1),
                                                2,
                                                1,
                                                0.05f));

                event
                                .getGenericTrades()
                                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                                                new net.minecraft.world.item.ItemStack(
                                                                net.minecraft.world.item.Items.EMERALD,
                                                                1),
                                                new net.minecraft.world.item.ItemStack(
                                                                ModItems.SNOWY_GRASS_BLOCK.get(),
                                                                1),
                                                2,
                                                1,
                                                0.05f));
                event
                                .getGenericTrades()
                                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                                                new net.minecraft.world.item.ItemStack(
                                                                net.minecraft.world.item.Items.EMERALD,
                                                                1),
                                                new net.minecraft.world.item.ItemStack(
                                                                ModItems.RED_SPORE_BLOSSOM.get(),
                                                                1),
                                                2,
                                                1,
                                                0.05f));

                event
                                .getGenericTrades()
                                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                                                new net.minecraft.world.item.ItemStack(
                                                                net.minecraft.world.item.Items.EMERALD,
                                                                1),
                                                new net.minecraft.world.item.ItemStack(
                                                                ModItems.CYAN_SPORE_BLOSSOM.get(),
                                                                1),
                                                2,
                                                1,
                                                0.05f));

                event
                                .getGenericTrades()
                                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                                                new net.minecraft.world.item.ItemStack(
                                                                net.minecraft.world.item.Items.EMERALD,
                                                                1),
                                                new net.minecraft.world.item.ItemStack(
                                                                ModItems.BLUE_SPORE_BLOSSOM.get(),
                                                                1),
                                                2,
                                                1,
                                                0.05f));

                event
                                .getGenericTrades()
                                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                                                new net.minecraft.world.item.ItemStack(
                                                                net.minecraft.world.item.Items.EMERALD,
                                                                1),
                                                new net.minecraft.world.item.ItemStack(
                                                                ModItems.PURPLE_SPORE_BLOSSOM.get(),
                                                                1),
                                                2,
                                                1,
                                                0.05f));

                event
                                .getGenericTrades()
                                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                                                new net.minecraft.world.item.ItemStack(
                                                                net.minecraft.world.item.Items.EMERALD,
                                                                1),
                                                new net.minecraft.world.item.ItemStack(
                                                                ModItems.ORANGE_SPORE_BLOSSOM.get(),
                                                                1),
                                                2,
                                                1,
                                                0.05f));

                event
                                .getGenericTrades()
                                .add((trader, rand) -> new net.minecraft.world.item.trading.MerchantOffer(
                                                new net.minecraft.world.item.ItemStack(
                                                                net.minecraft.world.item.Items.EMERALD,
                                                                1),
                                                new net.minecraft.world.item.ItemStack(ModItems.ICICLE.get(), 2),
                                                2,
                                                1,
                                                0.05f));
        }

        @SubscribeEvent
        public void onLeftClickBlock(
                        net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock event) {
                net.minecraft.world.level.block.state.BlockState state = event
                                .getWorld()
                                .getBlockState(event.getPos());
                net.minecraft.world.level.block.Block block = state.getBlock();

                if (block instanceof com.kingodogo.buildscape.block.PetalBlock
                                || block instanceof com.kingodogo.buildscape.block.CloverBlock
                                || block instanceof com.kingodogo.buildscape.block.RoseVinesBlock) {
                        if (block.getSoundType(
                                        state) instanceof com.kingodogo.buildscape.block.CustomSoundType customSound) {
                                net.minecraft.core.BlockPos pos = event.getPos();
                                net.minecraft.world.level.Level level = event.getWorld();

                                level.playSound(
                                                null,
                                                pos,
                                                block.getSoundType(state).getHitSound(),
                                                net.minecraft.sounds.SoundSource.BLOCKS,
                                                customSound.getHitVolume(),
                                                customSound.getHitPitch());
                        }
                }
        }

        @SubscribeEvent
        public void onRightClickBlock(
                        net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock event) {
                net.minecraft.world.level.block.state.BlockState state = event
                                .getWorld()
                                .getBlockState(event.getPos());
                net.minecraft.world.item.ItemStack heldItem = event
                                .getPlayer()
                                .getItemInHand(event.getHand());

                if (state.getBlock() == ModBlocks.MANGROVE_LEAVES.get()
                                && heldItem.getItem() instanceof net.minecraft.world.item.BoneMealItem) {
                        if (event.getFace() != net.minecraft.core.Direction.DOWN) {
                                event.setCanceled(true);
                                event.setCancellationResult(net.minecraft.world.InteractionResult.FAIL);
                                return;
                        }
                }

                if (state.getBlock() == ModBlocks.BAMBOO_BLOCK.get()
                                && heldItem.getItem() instanceof net.minecraft.world.item.AxeItem) {
                        net.minecraft.core.BlockPos pos = event.getPos();
                        net.minecraft.world.level.Level level = event.getWorld();
                        net.minecraft.world.entity.player.Player player = event.getPlayer();

                        net.minecraft.core.Direction.Axis axis = state.getValue(
                                        net.minecraft.world.level.block.RotatedPillarBlock.AXIS);

                        level.setBlock(
                                        pos,
                                        ModBlocks.STRIPPED_BAMBOO_BLOCK.get()
                                                        .defaultBlockState()
                                                        .setValue(
                                                                        net.minecraft.world.level.block.RotatedPillarBlock.AXIS,
                                                                        axis),
                                        11);

                        level.playSound(
                                        null,
                                        pos,
                                        net.minecraft.sounds.SoundEvents.AXE_STRIP,
                                        net.minecraft.sounds.SoundSource.BLOCKS,
                                        1.0f,
                                        1.0f);

                        if (!player.getAbilities().instabuild) {
                                heldItem.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(event.getHand()));
                        }

                        event.setCanceled(true);
                        event.setCancellationResult(
                                        net.minecraft.world.InteractionResult.SUCCESS);
                        return;
                }

                if (state.getBlock() == ModBlocks.MANGROVE_LOG.get()
                                && heldItem.getItem() instanceof net.minecraft.world.item.AxeItem) {
                        net.minecraft.core.BlockPos pos = event.getPos();
                        net.minecraft.world.level.Level level = event.getWorld();
                        net.minecraft.world.entity.player.Player player = event.getPlayer();

                        net.minecraft.core.Direction.Axis axis = state.getValue(
                                        net.minecraft.world.level.block.RotatedPillarBlock.AXIS);

                        level.setBlock(
                                        pos,
                                        ModBlocks.STRIPPED_MANGROVE_LOG.get()
                                                        .defaultBlockState()
                                                        .setValue(
                                                                        net.minecraft.world.level.block.RotatedPillarBlock.AXIS,
                                                                        axis),
                                        11);

                        level.playSound(
                                        null,
                                        pos,
                                        net.minecraft.sounds.SoundEvents.AXE_STRIP,
                                        net.minecraft.sounds.SoundSource.BLOCKS,
                                        1.0f,
                                        1.0f);

                        if (!player.getAbilities().instabuild) {
                                heldItem.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(event.getHand()));
                        }

                        event.setCanceled(true);
                        event.setCancellationResult(
                                        net.minecraft.world.InteractionResult.SUCCESS);
                        return;
                }

                if (state.getBlock() == ModBlocks.MANGROVE_WOOD.get()
                                && heldItem.getItem() instanceof net.minecraft.world.item.AxeItem) {
                        net.minecraft.core.BlockPos pos = event.getPos();
                        net.minecraft.world.level.Level level = event.getWorld();
                        net.minecraft.world.entity.player.Player player = event.getPlayer();

                        net.minecraft.core.Direction.Axis axis = state.getValue(
                                        net.minecraft.world.level.block.RotatedPillarBlock.AXIS);

                        level.setBlock(
                                        pos,
                                        ModBlocks.STRIPPED_MANGROVE_WOOD.get()
                                                        .defaultBlockState()
                                                        .setValue(
                                                                        net.minecraft.world.level.block.RotatedPillarBlock.AXIS,
                                                                        axis),
                                        11);

                        level.playSound(
                                        null,
                                        pos,
                                        net.minecraft.sounds.SoundEvents.AXE_STRIP,
                                        net.minecraft.sounds.SoundSource.BLOCKS,
                                        1.0f,
                                        1.0f);

                        if (!player.getAbilities().instabuild) {
                                heldItem.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(event.getHand()));
                        }

                        event.setCanceled(true);
                        event.setCancellationResult(
                                        net.minecraft.world.InteractionResult.SUCCESS);
                        return;
                }

                if (state.getBlock() == net.minecraft.world.level.block.Blocks.DIRT
                                && heldItem.getItem() == net.minecraft.world.item.Items.POTION) {
                        if (net.minecraft.world.item.alchemy.PotionUtils
                                        .getPotion(heldItem) == net.minecraft.world.item.alchemy.Potions.WATER) {
                                net.minecraft.core.BlockPos pos = event.getPos();
                                net.minecraft.world.level.Level level = event.getWorld();
                                net.minecraft.world.entity.player.Player player = event.getPlayer();

                                if (level.isClientSide) {
                                        spawnSplashParticles(level, pos);
                                        return;
                                }

                                level.setBlock(pos, ModBlocks.MUD.get().defaultBlockState(), 3);

                                level.playSound(
                                                null,
                                                pos,
                                                net.minecraft.sounds.SoundEvents.BOTTLE_EMPTY,
                                                net.minecraft.sounds.SoundSource.BLOCKS,
                                                1.0f,
                                                1.0f);

                                if (!player.getAbilities().instabuild) {
                                        heldItem.shrink(1);
                                        net.minecraft.world.item.ItemStack emptyBottle = new net.minecraft.world.item.ItemStack(
                                                        net.minecraft.world.item.Items.GLASS_BOTTLE);
                                        if (heldItem.isEmpty()) {
                                                player.setItemInHand(event.getHand(), emptyBottle);
                                        } else if (!player.getInventory().add(emptyBottle)) {
                                                player.drop(emptyBottle, false);
                                        }
                                }

                                spawnSplashParticles(level, pos);

                                event.setCanceled(true);
                                event.setCancellationResult(
                                                net.minecraft.world.InteractionResult.SUCCESS);
                        }
                }
        }

        @SubscribeEvent
        public void onItemCrafted(
                        net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent event) {
                net.minecraft.world.item.ItemStack crafted = event.getCrafting();

                if (crafted.getItem() == net.minecraft.world.item.Items.SUSPICIOUS_STEW) {
                        boolean hasFrostRose = false;

                        net.minecraft.world.Container inventory = event.getInventory();
                        if (inventory instanceof net.minecraft.world.inventory.CraftingContainer container) {

                                for (int i = 0; i < container.getContainerSize(); i++) {
                                        net.minecraft.world.item.ItemStack stack = container.getItem(i);
                                        if (stack.getItem() == ModItems.FROST_ROSE.get()) {
                                                hasFrostRose = true;
                                                break;
                                        }
                                }
                        }

                        if (hasFrostRose) {
                                net.minecraft.nbt.CompoundTag nbt = crafted.getOrCreateTag();
                                nbt.putInt("FrostRoseStew", 1);
                        }
                }
        }

        @SubscribeEvent
        public void onItemUseFinish(
                        net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Finish event) {
                if (!(event.getEntity() instanceof net.minecraft.world.entity.player.Player player)) {
                        return;
                }

                net.minecraft.world.item.ItemStack itemStack = event.getItem();

                if (itemStack.getItem() == net.minecraft.world.item.Items.SUSPICIOUS_STEW) {
                        net.minecraft.nbt.CompoundTag nbt = itemStack.getTag();
                        if (nbt != null
                                        && nbt.contains("FrostRoseStew")
                                        && nbt.getInt("FrostRoseStew") == 1) {
                                if (!player.level.isClientSide) {
                                        net.minecraft.nbt.CompoundTag playerData = player.getPersistentData();
                                        playerData.putInt("FrostRoseStewDamageTicks", 120);
                                }
                        }
                }
        }

        @SubscribeEvent
        public void onPlayerTick(
                        net.minecraftforge.event.TickEvent.PlayerTickEvent event) {
                if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) {
                        return;
                }

                net.minecraft.world.entity.player.Player player = event.player;
                if (player.level.isClientSide) {
                        return;
                }

                net.minecraft.nbt.CompoundTag playerData = player.getPersistentData();
                if (playerData.contains("FrostRoseStewDamageTicks")) {
                        int ticksRemaining = playerData.getInt("FrostRoseStewDamageTicks");

                        if (ticksRemaining > 0) {
                                if (ticksRemaining % 20 == 0 && player.isAlive()) {
                                        net.minecraft.world.damagesource.DamageSource freezeDamage = net.minecraft.world.damagesource.DamageSource.GENERIC;
                                        player.hurt(freezeDamage, 1.0F);

                                        net.minecraft.world.level.Level level = player.level;
                                        net.minecraft.core.BlockPos playerPos = player.blockPosition();
                                        level.playSound(
                                                        null,
                                                        playerPos,
                                                        net.minecraft.sounds.SoundEvents.POWDER_SNOW_STEP,
                                                        net.minecraft.sounds.SoundSource.PLAYERS,
                                                        0.5f,
                                                        1.0f);

                                        if (player.canFreeze()) {
                                                player.setTicksFrozen(Math.min(player.getTicksFrozen() + 140, 300));
                                        }
                                }

                                ticksRemaining--;
                                if (ticksRemaining > 0) {
                                        playerData.putInt("FrostRoseStewDamageTicks", ticksRemaining);
                                } else {
                                        playerData.remove("FrostRoseStewDamageTicks");
                                }
                        }
                }
        }

        @SubscribeEvent
        public void onEntityJoinWorld(
                        net.minecraftforge.event.entity.EntityJoinWorldEvent event) {
                if (event.getEntity() instanceof net.minecraft.world.entity.vehicle.Boat boat) {
                        net.minecraft.nbt.CompoundTag nbt = boat.getPersistentData();
                        if (nbt.contains("MangroveBoatType")
                                        && nbt.getString("MangroveBoatType").equals("mangrove")) {
                        }
                }
        }

        private void spawnSplashParticles(
                        net.minecraft.world.level.Level level,
                        net.minecraft.core.BlockPos pos) {
                if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        serverLevel.sendParticles(
                                        net.minecraft.core.particles.ParticleTypes.SPLASH,
                                        pos.getX() + 0.5,
                                        pos.getY() + 1.0,
                                        pos.getZ() + 0.5,
                                        8,
                                        0.5,
                                        0.3,
                                        0.5,
                                        0.1);
                } else if (level.isClientSide) {
                        for (int i = 0; i < 8; ++i) {
                                double x = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 0.5;
                                double y = pos.getY() + 1.0 + level.random.nextDouble() * 0.3;
                                double z = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 0.5;
                                double vx = (level.random.nextDouble() - 0.5) * 0.1;
                                double vy = level.random.nextDouble() * 0.1;
                                double vz = (level.random.nextDouble() - 0.5) * 0.1;
                                level.addParticle(
                                                net.minecraft.core.particles.ParticleTypes.SPLASH,
                                                x,
                                                y,
                                                z,
                                                vx,
                                                vy,
                                                vz);
                        }
                }
        }

        @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
        public static class ClientModEvents {

                @SubscribeEvent
                public static void onClientSetup(FMLClientSetupEvent event) {
                        event.enqueueWork(() -> {
                                com.kingodogo.buildscape.block.ModWoodTypes.MANGROVE.getClass();
                                com.kingodogo.buildscape.block.ModWoodTypes.BAMBOO.getClass();
                        });

                        // Note: Model layers for entity models are registered when EntityRenderers are
                        // created
                        // For custom cosmetic models, we'll bake the layer on-demand in the render
                        // method
                        LOGGER.info("Buildscape mod client setup complete");

                        event.enqueueWork(() -> {
                                com.kingodogo.buildscape.client.ModKeyBinds.register();
                        });

                        event.enqueueWork(() -> {
                                com.kingodogo.buildscape.client.ClientEvents.initializeConfigCallback();
                        });


                        event.enqueueWork(() -> {
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BLACK_MOSAIC_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BLUE_MOSAIC_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BROWN_MOSAIC_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.CYAN_MOSAIC_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.GRAY_MOSAIC_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.GREEN_MOSAIC_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LIGHT_BLUE_MOSAIC_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LIGHT_GRAY_MOSAIC_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LIME_MOSAIC_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.MAGENTA_MOSAIC_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.ORANGE_MOSAIC_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.PINK_MOSAIC_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.PURPLE_MOSAIC_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.RED_MOSAIC_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.WHITE_MOSAIC_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.YELLOW_MOSAIC_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BLACK_MOSAIC_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BLUE_MOSAIC_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BROWN_MOSAIC_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.CYAN_MOSAIC_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.GRAY_MOSAIC_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.GREEN_MOSAIC_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LIGHT_BLUE_MOSAIC_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LIGHT_GRAY_MOSAIC_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LIME_MOSAIC_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.MAGENTA_MOSAIC_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.ORANGE_MOSAIC_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.PINK_MOSAIC_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.PURPLE_MOSAIC_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.RED_MOSAIC_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.WHITE_MOSAIC_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.YELLOW_MOSAIC_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());

                                // Factory Glass
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FACTORY_WHITE_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FACTORY_LIGHT_GRAY_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FACTORY_GRAY_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FACTORY_BLACK_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FACTORY_BROWN_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FACTORY_RED_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FACTORY_ORANGE_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FACTORY_YELLOW_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FACTORY_LIME_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FACTORY_GREEN_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FACTORY_CYAN_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FACTORY_LIGHT_BLUE_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FACTORY_BLUE_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FACTORY_PURPLE_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FACTORY_MAGENTA_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FACTORY_PINK_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());

                                // Factory Glass Panes
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FACTORY_WHITE_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FACTORY_LIGHT_GRAY_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FACTORY_GRAY_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FACTORY_BLACK_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FACTORY_BROWN_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FACTORY_RED_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FACTORY_ORANGE_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FACTORY_YELLOW_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FACTORY_LIME_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FACTORY_GREEN_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FACTORY_CYAN_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FACTORY_LIGHT_BLUE_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FACTORY_BLUE_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FACTORY_PURPLE_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FACTORY_MAGENTA_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FACTORY_PINK_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());

                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BLACK_GLAZED_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BLUE_GLAZED_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BROWN_GLAZED_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.CYAN_GLAZED_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.GRAY_GLAZED_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.GREEN_GLAZED_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LIGHT_BLUE_GLAZED_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LIGHT_GRAY_GLAZED_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LIME_GLAZED_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.MAGENTA_GLAZED_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.ORANGE_GLAZED_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.PINK_GLAZED_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.PURPLE_GLAZED_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.RED_GLAZED_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.WHITE_GLAZED_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.YELLOW_GLAZED_GLASS.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());

                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BLACK_GLAZED_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BLUE_GLAZED_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BROWN_GLAZED_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.CYAN_GLAZED_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.GRAY_GLAZED_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.GREEN_GLAZED_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LIGHT_BLUE_GLAZED_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LIGHT_GRAY_GLAZED_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LIME_GLAZED_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.MAGENTA_GLAZED_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.ORANGE_GLAZED_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.PINK_GLAZED_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.PURPLE_GLAZED_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.RED_GLAZED_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.WHITE_GLAZED_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.YELLOW_GLAZED_GLASS_PANE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());

                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.DIAMOND_CHAIN.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.GOLD_CHAIN.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.EMERALD_CHAIN.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.ANCIENT_STEEL_CHAIN.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.NETHERITE_CHAIN.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.COPPER_CHAIN.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.EXPOSED_COPPER_CHAIN.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.WEATHERED_COPPER_CHAIN.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.OXIDIZED_COPPER_CHAIN.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());

                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LARGE_IRON_CHAIN.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LARGE_GOLD_CHAIN.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LARGE_DIAMOND_CHAIN.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LARGE_EMERALD_CHAIN.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LARGE_ANCIENT_STEEL_CHAIN.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LARGE_NETHERITE_CHAIN.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());

                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.WHITE_ORNAMENT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.ORANGE_ORNAMENT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.MAGENTA_ORNAMENT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LIGHT_BLUE_ORNAMENT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.YELLOW_ORNAMENT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LIME_ORNAMENT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.PINK_ORNAMENT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.GRAY_ORNAMENT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LIGHT_GRAY_ORNAMENT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.CYAN_ORNAMENT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.PURPLE_ORNAMENT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BLUE_ORNAMENT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BROWN_ORNAMENT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.GREEN_ORNAMENT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.RED_ORNAMENT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BLACK_ORNAMENT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.GLASS_ORNAMENT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.TINTED_GLASS_ORNAMENT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());

                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.WHITE_STRING_LIGHT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.ORANGE_STRING_LIGHT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.MAGENTA_STRING_LIGHT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LIGHT_BLUE_STRING_LIGHT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.YELLOW_STRING_LIGHT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LIME_STRING_LIGHT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.PINK_STRING_LIGHT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.GRAY_STRING_LIGHT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LIGHT_GRAY_STRING_LIGHT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.CYAN_STRING_LIGHT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.PURPLE_STRING_LIGHT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BLUE_STRING_LIGHT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BROWN_STRING_LIGHT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.GREEN_STRING_LIGHT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.RED_STRING_LIGHT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BLACK_STRING_LIGHT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.MULTICOLOR_STRING_LIGHT.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());

                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_LEAVES.get(),
                                                net.minecraft.client.renderer.RenderType.cutoutMipped());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_OAK_LEAVES.get(),
                                                net.minecraft.client.renderer.RenderType.cutoutMipped());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_SPRUCE_LEAVES.get(),
                                                net.minecraft.client.renderer.RenderType.cutoutMipped());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_BIRCH_LEAVES.get(),
                                                net.minecraft.client.renderer.RenderType.cutoutMipped());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_JUNGLE_LEAVES.get(),
                                                net.minecraft.client.renderer.RenderType.cutoutMipped());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_ACACIA_LEAVES.get(),
                                                net.minecraft.client.renderer.RenderType.cutoutMipped());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_DARK_OAK_LEAVES.get(),
                                                net.minecraft.client.renderer.RenderType.cutoutMipped());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_MANGROVE_LEAVES.get(),
                                                net.minecraft.client.renderer.RenderType.cutoutMipped());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_AZALEA_LEAVES.get(),
                                                net.minecraft.client.renderer.RenderType.cutoutMipped());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_FLOWERING_AZALEA_LEAVES.get(),
                                                net.minecraft.client.renderer.RenderType.cutoutMipped());

                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_SHORT_GRASS.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_TALL_GRASS.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_FERN.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_LARGE_FERN.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_BUSH.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());

                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.WHITE_STAR.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.ORANGE_STAR.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.MAGENTA_STAR.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LIGHT_BLUE_STAR.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.YELLOW_STAR.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LIME_STAR.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.PINK_STAR.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.GRAY_STAR.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LIGHT_GRAY_STAR.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.CYAN_STAR.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.PURPLE_STAR.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BLUE_STAR.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BROWN_STAR.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.GREEN_STAR.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.RED_STAR.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BLACK_STAR.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.GLOW_STAR.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());

                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.GLOW_LIGHTS.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.MULTICOLOR_GLOW_LIGHTS.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());

                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BIT_COPPER_GRATE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BIT_EXPOSED_COPPER_GRATE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BIT_WEATHERED_COPPER_GRATE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BIT_OXIDIZED_COPPER_GRATE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());

                                // Steel Grate
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.STEEL_GRATE.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());

                                net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(
                                                com.kingodogo.buildscape.block.ModBlockEntities.PILLAR_BLOCK_ENTITY
                                                                .get(),
                                                com.kingodogo.buildscape.client.renderer.PillarBlockEntityRenderer::new);
                                net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(
                                                com.kingodogo.buildscape.block.ModBlockEntities.DECORATED_POT_BLOCK_ENTITY
                                                                .get(),
                                                com.kingodogo.buildscape.client.renderer.DecoratedPotBlockEntityRenderer::new);
                                net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(
                                                com.kingodogo.buildscape.block.ModBlockEntities.ICICLE_CAULDRON_BLOCK_ENTITY
                                                                .get(),
                                                com.kingodogo.buildscape.client.renderer.IcicleCauldronBlockEntityRenderer::new);

                                net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(
                                                com.kingodogo.buildscape.block.ModBlockEntities.FESTIVE_STOCKING_BLOCK_ENTITY
                                                                .get(),
                                                com.kingodogo.buildscape.client.renderer.FestiveStockingBlockEntityRenderer::new);

                                net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(
                                                com.kingodogo.buildscape.block.ModBlockEntities.MANGROVE_SIGN_BLOCK_ENTITY
                                                                .get(),
                                                net.minecraft.client.renderer.blockentity.SignRenderer::new);

                                net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(
                                                com.kingodogo.buildscape.block.ModBlockEntities.BAMBOO_SIGN_BLOCK_ENTITY
                                                                .get(),
                                                net.minecraft.client.renderer.blockentity.SignRenderer::new);

                                net.minecraft.client.renderer.entity.EntityRenderers.register(
                                                com.kingodogo.buildscape.entity.ModEntities.FALLING_ICICLE.get(),
                                                com.kingodogo.buildscape.client.renderer.FallingIcicleRenderer::new);

                                net.minecraft.client.renderer.entity.EntityRenderers.register(
                                                com.kingodogo.buildscape.entity.ModEntities.MANGROVE_BOAT.get(),
                                                com.kingodogo.buildscape.client.renderer.MangroveBoatRenderer::new);

                                net.minecraft.client.renderer.entity.EntityRenderers.register(
                                                com.kingodogo.buildscape.entity.ModEntities.COLORED_ITEM_FRAME.get(),
                                                com.kingodogo.buildscape.client.renderer.ColoredItemFrameRenderer::new);

                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.MANGROVE_SIGN.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.MANGROVE_WALL_SIGN.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());

                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BAMBOO_SIGN.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BAMBOO_WALL_SIGN.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());

                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.ICICLE.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());

                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.ICICLE_BLOCK.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.PACKED_ICICLE_BLOCK.get(),
                                                net.minecraft.client.renderer.RenderType.translucent());

                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.DECORATED_POT.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BLACK_DECORATED_POT.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BLUE_DECORATED_POT.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BROWN_DECORATED_POT.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.CYAN_DECORATED_POT.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.GRAY_DECORATED_POT.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.GREEN_DECORATED_POT.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LIGHT_BLUE_DECORATED_POT.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LIGHT_GRAY_DECORATED_POT.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LIME_DECORATED_POT.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.MAGENTA_DECORATED_POT.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.ORANGE_DECORATED_POT.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.PINK_DECORATED_POT.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.PURPLE_DECORATED_POT.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.RED_DECORATED_POT.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.WHITE_DECORATED_POT.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.YELLOW_DECORATED_POT.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());

                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BROWN_MUSHROOM_SHELVES.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.RED_MUSHROOM_SHELVES.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());

                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.RED_ROSE_VINES.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BLACK_ROSE_VINES.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BLUE_ROSE_VINES.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.WHITE_ROSE_VINES.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());

                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FESTIVE_STOCKING.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BLACK_FESTIVE_STOCKING.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BLUE_FESTIVE_STOCKING.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BROWN_FESTIVE_STOCKING.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.CYAN_FESTIVE_STOCKING.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.GRAY_FESTIVE_STOCKING.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.GREEN_FESTIVE_STOCKING.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LIGHT_BLUE_FESTIVE_STOCKING.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LIGHT_GRAY_FESTIVE_STOCKING.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LIME_FESTIVE_STOCKING.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.MAGENTA_FESTIVE_STOCKING.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.ORANGE_FESTIVE_STOCKING.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.PINK_FESTIVE_STOCKING.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.PURPLE_FESTIVE_STOCKING.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.RED_FESTIVE_STOCKING.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.WHITE_FESTIVE_STOCKING.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.YELLOW_FESTIVE_STOCKING.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());

                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BAMBOO_DOOR.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BAMBOO_TRAPDOOR.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());

                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.MANGROVE_DOOR.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.MANGROVE_TRAPDOOR.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.MANGROVE_PROPAGULE.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.MANGROVE_ROOTS.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.MUDDY_MANGROVE_ROOTS.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.MANGROVE_LEAVES.get(),
                                                net.minecraft.client.renderer.RenderType.cutoutMipped());

                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.RED_MONETS.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BLUE_MONETS.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.PURPLE_MONETS.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.LIGHT_BLUE_MONETS.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.PINK_MONETS.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.YELLOW_MONETS.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());

                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.CLOVER.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());

                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.RED_PETAL.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BLUE_PETAL.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.ORANGE_PETAL.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.PINK_PETAL.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.PURPLE_PETAL.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());

                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.RED_SPORE_BLOSSOM.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.CYAN_SPORE_BLOSSOM.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BLUE_SPORE_BLOSSOM.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.PURPLE_SPORE_BLOSSOM.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.ORANGE_SPORE_BLOSSOM.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());

                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.PURPLE_PETAL.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());

                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FROST_ROSE.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());

                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.OAK_LEAF_LAYERS.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SPRUCE_LEAF_LAYERS.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BIRCH_LEAF_LAYERS.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.JUNGLE_LEAF_LAYERS.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.ACACIA_LEAF_LAYERS.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.DARK_OAK_LEAF_LAYERS.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.AZALEA_LEAF_LAYERS.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FLOWERING_AZALEA_LEAF_LAYERS.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_OAK_LEAF_LAYERS.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_SPRUCE_LEAF_LAYERS.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_BIRCH_LEAF_LAYERS.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_JUNGLE_LEAF_LAYERS.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_ACACIA_LEAF_LAYERS.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_DARK_OAK_LEAF_LAYERS.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_MANGROVE_LEAF_LAYERS.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_AZALEA_LEAF_LAYERS.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_FLOWERING_AZALEA_LEAF_LAYERS.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.MANGROVE_LEAF_LAYERS.get(),
                                                net.minecraft.client.renderer.RenderType.cutout());

                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.OAK_LEAF_HEDGE.get(),
                                                net.minecraft.client.renderer.RenderType.cutoutMipped());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SPRUCE_LEAF_HEDGE.get(),
                                                net.minecraft.client.renderer.RenderType.cutoutMipped());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.BIRCH_LEAF_HEDGE.get(),
                                                net.minecraft.client.renderer.RenderType.cutoutMipped());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.JUNGLE_LEAF_HEDGE.get(),
                                                net.minecraft.client.renderer.RenderType.cutoutMipped());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.ACACIA_LEAF_HEDGE.get(),
                                                net.minecraft.client.renderer.RenderType.cutoutMipped());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.DARK_OAK_LEAF_HEDGE.get(),
                                                net.minecraft.client.renderer.RenderType.cutoutMipped());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.AZALEA_LEAF_HEDGE.get(),
                                                net.minecraft.client.renderer.RenderType.cutoutMipped());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.FLOWERING_AZALEA_LEAF_HEDGE.get(),
                                                net.minecraft.client.renderer.RenderType.cutoutMipped());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_OAK_LEAF_HEDGE.get(),
                                                net.minecraft.client.renderer.RenderType.cutoutMipped());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_SPRUCE_LEAF_HEDGE.get(),
                                                net.minecraft.client.renderer.RenderType.cutoutMipped());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_BIRCH_LEAF_HEDGE.get(),
                                                net.minecraft.client.renderer.RenderType.cutoutMipped());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_JUNGLE_LEAF_HEDGE.get(),
                                                net.minecraft.client.renderer.RenderType.cutoutMipped());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_ACACIA_LEAF_HEDGE.get(),
                                                net.minecraft.client.renderer.RenderType.cutoutMipped());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_DARK_OAK_LEAF_HEDGE.get(),
                                                net.minecraft.client.renderer.RenderType.cutoutMipped());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_MANGROVE_LEAF_HEDGE.get(),
                                                net.minecraft.client.renderer.RenderType.cutoutMipped());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_AZALEA_LEAF_HEDGE.get(),
                                                net.minecraft.client.renderer.RenderType.cutoutMipped());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.SNOWY_FLOWERING_AZALEA_LEAF_HEDGE.get(),
                                                net.minecraft.client.renderer.RenderType.cutoutMipped());
                                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                                                ModBlocks.MANGROVE_LEAF_HEDGE.get(),
                                                net.minecraft.client.renderer.RenderType.cutoutMipped());

                                net.minecraft.client.color.block.BlockColors blockColors = net.minecraft.client.Minecraft
                                                .getInstance().getBlockColors();
                                blockColors.register(
                                                (state, reader, pos, tintIndex) -> {
                                                        if (reader == null || pos == null) {
                                                                return 0x7FA832;
                                                        }
                                                        if (tintIndex == 1) {
                                                                if (reader instanceof net.minecraft.world.level.LevelReader levelReader) {
                                                                        return levelReader
                                                                                        .getBiome(pos)
                                                                                        .value()
                                                                                        .getGrassColor(pos.getX(),
                                                                                                        pos.getZ());
                                                                }
                                                                return 0x7FA832;
                                                        }
                                                        return -1;
                                                },
                                                ModBlocks.CLOVER.get());

                                blockColors.register(
                                                (state, reader, pos, tintIndex) -> {
                                                        if (reader == null || pos == null) {
                                                                return 0x7FA832;
                                                        }
                                                        if (tintIndex == 1) {
                                                                if (reader instanceof net.minecraft.world.level.LevelReader levelReader) {
                                                                        return levelReader
                                                                                        .getBiome(pos)
                                                                                        .value()
                                                                                        .getGrassColor(pos.getX(),
                                                                                                        pos.getZ());
                                                                }
                                                                return 0x7FA832;
                                                        }
                                                        return -1;
                                                },
                                                ModBlocks.RED_PETAL.get(),
                                                ModBlocks.BLUE_PETAL.get(),
                                                ModBlocks.ORANGE_PETAL.get(),
                                                ModBlocks.PINK_PETAL.get(),
                                                ModBlocks.PURPLE_PETAL.get());

                                blockColors.register(
                                                (state, reader, pos, tintIndex) -> {
                                                        if (tintIndex != 0) {
                                                                return -1;
                                                        }

                                                        if (state.hasProperty(
                                                                        com.kingodogo.buildscape.block.OrnamentBlock.STRING_COLOR)) {
                                                                com.kingodogo.buildscape.block.OrnamentBlock.StringColor stringColor = state
                                                                                .getValue(
                                                                                                com.kingodogo.buildscape.block.OrnamentBlock.STRING_COLOR);
                                                                return stringColor.getColor();
                                                        }

                                                        return 0xFFFFFF;
                                                },
                                                ModBlocks.WHITE_ORNAMENT.get(),
                                                ModBlocks.ORANGE_ORNAMENT.get(),
                                                ModBlocks.MAGENTA_ORNAMENT.get(),
                                                ModBlocks.LIGHT_BLUE_ORNAMENT.get(),
                                                ModBlocks.YELLOW_ORNAMENT.get(),
                                                ModBlocks.LIME_ORNAMENT.get(),
                                                ModBlocks.PINK_ORNAMENT.get(),
                                                ModBlocks.GRAY_ORNAMENT.get(),
                                                ModBlocks.LIGHT_GRAY_ORNAMENT.get(),
                                                ModBlocks.CYAN_ORNAMENT.get(),
                                                ModBlocks.PURPLE_ORNAMENT.get(),
                                                ModBlocks.BLUE_ORNAMENT.get(),
                                                ModBlocks.BROWN_ORNAMENT.get(),
                                                ModBlocks.GREEN_ORNAMENT.get(),
                                                ModBlocks.RED_ORNAMENT.get(),
                                                ModBlocks.BLACK_ORNAMENT.get(),
                                                ModBlocks.GLASS_ORNAMENT.get(),
                                                ModBlocks.TINTED_GLASS_ORNAMENT.get());

                                blockColors.register(
                                                (state, reader, pos, tintIndex) -> {
                                                        if (tintIndex != 0) {
                                                                return -1;
                                                        }

                                                        if (state.hasProperty(
                                                                        com.kingodogo.buildscape.block.StringLightBlock.STRING_COLOR)) {
                                                                com.kingodogo.buildscape.block.StringLightBlock.StringColor stringColor = state
                                                                                .getValue(
                                                                                                com.kingodogo.buildscape.block.StringLightBlock.STRING_COLOR);
                                                                return stringColor.getColor();
                                                        }

                                                        return 0xFFFFFF;
                                                },
                                                ModBlocks.WHITE_STRING_LIGHT.get(),
                                                ModBlocks.ORANGE_STRING_LIGHT.get(),
                                                ModBlocks.MAGENTA_STRING_LIGHT.get(),
                                                ModBlocks.LIGHT_BLUE_STRING_LIGHT.get(),
                                                ModBlocks.YELLOW_STRING_LIGHT.get(),
                                                ModBlocks.LIME_STRING_LIGHT.get(),
                                                ModBlocks.PINK_STRING_LIGHT.get(),
                                                ModBlocks.GRAY_STRING_LIGHT.get(),
                                                ModBlocks.LIGHT_GRAY_STRING_LIGHT.get(),
                                                ModBlocks.CYAN_STRING_LIGHT.get(),
                                                ModBlocks.PURPLE_STRING_LIGHT.get(),
                                                ModBlocks.BLUE_STRING_LIGHT.get(),
                                                ModBlocks.BROWN_STRING_LIGHT.get(),
                                                ModBlocks.GREEN_STRING_LIGHT.get(),
                                                ModBlocks.RED_STRING_LIGHT.get(),
                                                ModBlocks.BLACK_STRING_LIGHT.get(),
                                                ModBlocks.MULTICOLOR_STRING_LIGHT.get());

                                blockColors.register(
                                                (state, reader, pos, tintIndex) -> {
                                                        if (tintIndex != 0) {
                                                                return -1;
                                                        }

                                                        if (reader != null
                                                                        && pos != null
                                                                        && reader instanceof net.minecraft.world.level.LevelReader levelReader) {
                                                                return levelReader.getBiome(pos).value()
                                                                                .getFoliageColor();
                                                        }

                                                        return 0x48B518;
                                                },
                                                ModBlocks.OAK_LEAF_LAYERS.get(),
                                                ModBlocks.JUNGLE_LEAF_LAYERS.get(),
                                                ModBlocks.ACACIA_LEAF_LAYERS.get(),
                                                ModBlocks.DARK_OAK_LEAF_LAYERS.get(),
                                                ModBlocks.OAK_LEAF_HEDGE.get(),
                                                ModBlocks.JUNGLE_LEAF_HEDGE.get(),
                                                ModBlocks.ACACIA_LEAF_HEDGE.get(),
                                                ModBlocks.DARK_OAK_LEAF_HEDGE.get());

                                blockColors.register(
                                                (state, reader, pos, tintIndex) -> {
                                                        if (tintIndex != 0) {
                                                                return -1;
                                                        }
                                                        return 0x619961;
                                                },
                                                ModBlocks.SPRUCE_LEAF_LAYERS.get(),
                                                ModBlocks.SPRUCE_LEAF_HEDGE.get());

                                blockColors.register(
                                                (state, reader, pos, tintIndex) -> {
                                                        if (tintIndex != 0) {
                                                                return -1;
                                                        }
                                                        return 0x80a755;
                                                },
                                                ModBlocks.BIRCH_LEAF_LAYERS.get(),
                                                ModBlocks.BIRCH_LEAF_HEDGE.get());

                                blockColors.register(
                                                (state, reader, pos, tintIndex) -> {
                                                        return -1;
                                                },
                                                ModBlocks.AZALEA_LEAF_LAYERS.get(),
                                                ModBlocks.FLOWERING_AZALEA_LEAF_LAYERS.get(),
                                                ModBlocks.AZALEA_LEAF_HEDGE.get(),
                                                ModBlocks.FLOWERING_AZALEA_LEAF_HEDGE.get());

                                blockColors.register(
                                                (state, reader, pos, tintIndex) -> {
                                                        return -1;
                                                },
                                                ModBlocks.SNOWY_OAK_LEAF_LAYERS.get(),
                                                ModBlocks.SNOWY_SPRUCE_LEAF_LAYERS.get(),
                                                ModBlocks.SNOWY_BIRCH_LEAF_LAYERS.get(),
                                                ModBlocks.SNOWY_JUNGLE_LEAF_LAYERS.get(),
                                                ModBlocks.SNOWY_ACACIA_LEAF_LAYERS.get(),
                                                ModBlocks.SNOWY_DARK_OAK_LEAF_LAYERS.get(),
                                                ModBlocks.SNOWY_MANGROVE_LEAF_LAYERS.get(),
                                                ModBlocks.SNOWY_AZALEA_LEAF_LAYERS.get(),
                                                ModBlocks.SNOWY_FLOWERING_AZALEA_LEAF_LAYERS.get(),
                                                ModBlocks.SNOWY_OAK_LEAF_HEDGE.get(),
                                                ModBlocks.SNOWY_SPRUCE_LEAF_HEDGE.get(),
                                                ModBlocks.SNOWY_BIRCH_LEAF_HEDGE.get(),
                                                ModBlocks.SNOWY_JUNGLE_LEAF_HEDGE.get(),
                                                ModBlocks.SNOWY_ACACIA_LEAF_HEDGE.get(),
                                                ModBlocks.SNOWY_DARK_OAK_LEAF_HEDGE.get(),
                                                ModBlocks.SNOWY_MANGROVE_LEAF_HEDGE.get(),
                                                ModBlocks.SNOWY_AZALEA_LEAF_HEDGE.get(),
                                                ModBlocks.SNOWY_FLOWERING_AZALEA_LEAF_HEDGE.get());

                                blockColors.register(
                                                (state, reader, pos, tintIndex) -> {
                                                        if (tintIndex != 0) {
                                                                return -1;
                                                        }
                                                        if (reader != null
                                                                        && pos != null
                                                                        && reader instanceof net.minecraft.world.level.LevelReader levelReader) {
                                                                return levelReader.getBiome(pos).value()
                                                                                .getFoliageColor();
                                                        }
                                                        return 0x92c648;
                                                },
                                                ModBlocks.MANGROVE_LEAF_LAYERS.get(),
                                                ModBlocks.MANGROVE_LEAF_HEDGE.get());

                                blockColors.register(
                                                (state, reader, pos, tintIndex) -> {
                                                        if (tintIndex != 0) {
                                                                return -1;
                                                        }
                                                        if (reader != null
                                                                        && pos != null
                                                                        && reader instanceof net.minecraft.world.level.LevelReader levelReader) {
                                                                return levelReader.getBiome(pos).value()
                                                                                .getFoliageColor();
                                                        }
                                                        return 0x92c648;
                                                },
                                                ModBlocks.MANGROVE_LEAVES.get());

                                blockColors.register(
                                                (state, reader, pos, tintIndex) -> {
                                                        if (tintIndex != 0) {
                                                                return -1;
                                                        }

                                                        if (reader != null && pos != null) {
                                                                net.minecraft.world.level.block.entity.BlockEntity be = reader
                                                                                .getBlockEntity(pos);
                                                                if (be instanceof com.kingodogo.buildscape.block.GlowLightsBlockEntity glowBE) {
                                                                        java.util.List<String> dyeColors = glowBE
                                                                                        .getDyeColors();
                                                                        if (dyeColors != null && !dyeColors.isEmpty()) {
                                                                                int hash = pos.hashCode()
                                                                                                ^ state.hashCode();
                                                                                int colorIndex = Math.abs(hash)
                                                                                                % dyeColors.size();
                                                                                String colorCode = dyeColors
                                                                                                .get(colorIndex);

                                                                                try {
                                                                                        if (colorCode != null
                                                                                                        && colorCode.startsWith(
                                                                                                                        "#")
                                                                                                        && colorCode.length() == 7) {
                                                                                                int color = Integer
                                                                                                                .parseInt(colorCode
                                                                                                                                .substring(1),
                                                                                                                                16);
                                                                                                return color | 0xFF000000;
                                                                                        }
                                                                                } catch (NumberFormatException e) {
                                                                                }
                                                                        }
                                                                }
                                                        }

                                                        return -1;
                                                },
                                                ModBlocks.GLOW_LIGHTS.get());

                                net.minecraft.client.color.item.ItemColors itemColors = net.minecraft.client.Minecraft
                                                .getInstance().getItemColors();
                                net.minecraft.client.color.item.ItemColors vanillaItemColors = net.minecraft.client.Minecraft
                                                .getInstance().getItemColors();

                                itemColors.register(
                                                (stack, tintIndex) -> {
                                                        if (tintIndex != 0) {
                                                                return -1;
                                                        }

                                                        net.minecraft.world.item.Item item = stack.getItem();
                                                        if (item instanceof net.minecraft.world.item.BlockItem) {
                                                                net.minecraft.world.level.block.Block block = ((net.minecraft.world.item.BlockItem) item)
                                                                                .getBlock();

                                                                int color = -1;
                                                                if (block == ModBlocks.OAK_LEAF_HEDGE.get()
                                                                                || block == ModBlocks.OAK_LEAF_LAYERS
                                                                                                .get()) {
                                                                        color = vanillaItemColors.getColor(
                                                                                        new net.minecraft.world.item.ItemStack(
                                                                                                        net.minecraft.world.item.Items.OAK_LEAVES),
                                                                                        0);
                                                                } else if (block == ModBlocks.JUNGLE_LEAF_HEDGE.get()
                                                                                || block == ModBlocks.JUNGLE_LEAF_LAYERS
                                                                                                .get()) {
                                                                        color = vanillaItemColors.getColor(
                                                                                        new net.minecraft.world.item.ItemStack(
                                                                                                        net.minecraft.world.item.Items.JUNGLE_LEAVES),
                                                                                        0);
                                                                } else if (block == ModBlocks.ACACIA_LEAF_HEDGE.get()
                                                                                || block == ModBlocks.ACACIA_LEAF_LAYERS
                                                                                                .get()) {
                                                                        color = vanillaItemColors.getColor(
                                                                                        new net.minecraft.world.item.ItemStack(
                                                                                                        net.minecraft.world.item.Items.ACACIA_LEAVES),
                                                                                        0);
                                                                } else if (block == ModBlocks.DARK_OAK_LEAF_HEDGE.get()
                                                                                || block == ModBlocks.DARK_OAK_LEAF_LAYERS
                                                                                                .get()) {
                                                                        color = vanillaItemColors.getColor(
                                                                                        new net.minecraft.world.item.ItemStack(
                                                                                                        net.minecraft.world.item.Items.DARK_OAK_LEAVES),
                                                                                        0);
                                                                } else if (block == ModBlocks.SPRUCE_LEAF_HEDGE.get()
                                                                                || block == ModBlocks.SPRUCE_LEAF_LAYERS
                                                                                                .get()) {
                                                                        color = 0x619961;
                                                                } else if (block == ModBlocks.BIRCH_LEAF_HEDGE.get()
                                                                                || block == ModBlocks.BIRCH_LEAF_LAYERS
                                                                                                .get()) {
                                                                        color = 0x80a755;
                                                                } else if (block == ModBlocks.MANGROVE_LEAVES.get()
                                                                                || block == ModBlocks.MANGROVE_LEAF_LAYERS
                                                                                                .get()
                                                                                ||
                                                                                block == ModBlocks.MANGROVE_LEAF_HEDGE
                                                                                                .get()) {
                                                                        color = 0x92c648;
                                                                }

                                                                return color;
                                                        }
                                                        return -1;
                                                },
                                                ModItems.OAK_LEAF_LAYERS.get(),
                                                ModItems.SPRUCE_LEAF_LAYERS.get(),
                                                ModItems.BIRCH_LEAF_LAYERS.get(),
                                                ModItems.JUNGLE_LEAF_LAYERS.get(),
                                                ModItems.ACACIA_LEAF_LAYERS.get(),
                                                ModItems.DARK_OAK_LEAF_LAYERS.get(),
                                                ModItems.AZALEA_LEAF_LAYERS.get(),
                                                ModItems.FLOWERING_AZALEA_LEAF_LAYERS.get(),
                                                ModItems.SNOWY_OAK_LEAF_LAYERS.get(),
                                                ModItems.SNOWY_SPRUCE_LEAF_LAYERS.get(),
                                                ModItems.SNOWY_BIRCH_LEAF_LAYERS.get(),
                                                ModItems.SNOWY_JUNGLE_LEAF_LAYERS.get(),
                                                ModItems.SNOWY_ACACIA_LEAF_LAYERS.get(),
                                                ModItems.SNOWY_DARK_OAK_LEAF_LAYERS.get(),
                                                ModItems.SNOWY_MANGROVE_LEAF_LAYERS.get(),
                                                ModItems.SNOWY_AZALEA_LEAF_LAYERS.get(),
                                                ModItems.SNOWY_FLOWERING_AZALEA_LEAF_LAYERS.get(),
                                                ModItems.MANGROVE_LEAF_LAYERS.get(),
                                                ModItems.OAK_LEAF_HEDGE.get(),
                                                ModItems.SPRUCE_LEAF_HEDGE.get(),
                                                ModItems.BIRCH_LEAF_HEDGE.get(),
                                                ModItems.JUNGLE_LEAF_HEDGE.get(),
                                                ModItems.ACACIA_LEAF_HEDGE.get(),
                                                ModItems.DARK_OAK_LEAF_HEDGE.get(),
                                                ModItems.AZALEA_LEAF_HEDGE.get(),
                                                ModItems.FLOWERING_AZALEA_LEAF_HEDGE.get(),
                                                ModItems.SNOWY_OAK_LEAF_HEDGE.get(),
                                                ModItems.SNOWY_SPRUCE_LEAF_HEDGE.get(),
                                                ModItems.SNOWY_BIRCH_LEAF_HEDGE.get(),
                                                ModItems.SNOWY_JUNGLE_LEAF_HEDGE.get(),
                                                ModItems.SNOWY_ACACIA_LEAF_HEDGE.get(),
                                                ModItems.SNOWY_DARK_OAK_LEAF_HEDGE.get(),
                                                ModItems.SNOWY_MANGROVE_LEAF_HEDGE.get(),
                                                ModItems.SNOWY_AZALEA_LEAF_HEDGE.get(),
                                                ModItems.SNOWY_FLOWERING_AZALEA_LEAF_HEDGE.get(),
                                                ModItems.MANGROVE_LEAF_HEDGE.get(),
                                                ModItems.MANGROVE_LEAVES.get());
                        });
                }

        }

        @Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
        public static class ClientForgeEvents {

                @SubscribeEvent
                public static void onModelBake(
                                net.minecraftforge.client.event.ModelBakeEvent event) {
                        BuildScape.LOGGER.info("ModelBakeEvent fired - wrapping leaf hedge models");
                        java.util.Set<net.minecraft.resources.ResourceLocation> leafHedgeModels = new java.util.HashSet<>();
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "block/oak_leaf_hedge_post"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "block/oak_leaf_hedge_side"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "block/oak_leaf_hedge_side_tall"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "block/oak_leaf_hedge_inventory"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "block/spruce_leaf_hedge_post"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "block/spruce_leaf_hedge_side"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "block/spruce_leaf_hedge_side_tall"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "block/spruce_leaf_hedge_inventory"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "block/birch_leaf_hedge_post"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "block/birch_leaf_hedge_side"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "block/birch_leaf_hedge_side_tall"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "block/birch_leaf_hedge_inventory"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "block/jungle_leaf_hedge_post"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "block/jungle_leaf_hedge_side"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "block/jungle_leaf_hedge_side_tall"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "block/jungle_leaf_hedge_inventory"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "block/acacia_leaf_hedge_post"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "block/acacia_leaf_hedge_side"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "block/acacia_leaf_hedge_side_tall"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "block/acacia_leaf_hedge_inventory"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "block/dark_oak_leaf_hedge_post"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "block/dark_oak_leaf_hedge_side"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "block/dark_oak_leaf_hedge_side_tall"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "block/dark_oak_leaf_hedge_inventory"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "block/azalea_leaf_hedge_post"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "block/azalea_leaf_hedge_side"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "block/azalea_leaf_hedge_side_tall"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "block/azalea_leaf_hedge_inventory"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "block/flowering_azalea_leaf_hedge_post"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "block/flowering_azalea_leaf_hedge_side"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "block/flowering_azalea_leaf_hedge_side_tall"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "block/flowering_azalea_leaf_hedge_inventory"));

                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "item/oak_leaf_hedge"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "item/spruce_leaf_hedge"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "item/birch_leaf_hedge"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "item/jungle_leaf_hedge"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "item/acacia_leaf_hedge"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "item/dark_oak_leaf_hedge"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "item/azalea_leaf_hedge"));
                        leafHedgeModels.add(
                                        new net.minecraft.resources.ResourceLocation(
                                                        BuildScape.MODID,
                                                        "item/flowering_azalea_leaf_hedge"));

                        int wrappedCount = 0;
                        int notFoundCount = 0;
                        for (net.minecraft.resources.ResourceLocation modelLocation : leafHedgeModels) {
                                net.minecraft.client.resources.model.BakedModel originalModel = event
                                                .getModelRegistry()
                                                .get(modelLocation);
                                if (originalModel != null) {
                                        event
                                                        .getModelRegistry()
                                                        .put(
                                                                        modelLocation,
                                                                        new com.kingodogo.buildscape.client.model.TintedLeafHedgeModel(
                                                                                        originalModel));
                                        wrappedCount++;
                                        BuildScape.LOGGER.debug("Wrapped model: {}", modelLocation);
                                } else {
                                        notFoundCount++;
                                        BuildScape.LOGGER.warn("Model not found: {}", modelLocation);
                                }
                        }
                        BuildScape.LOGGER.info(
                                        "ModelBakeEvent: Wrapped {} leaf hedge models, {} not found",
                                        wrappedCount,
                                        notFoundCount);
                }
        }

        @Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
        public static class ClientModEventsParticles {

                @SubscribeEvent
                public static void registerFactories(
                                net.minecraftforge.client.event.ParticleFactoryRegisterEvent event) {
                        net.minecraft.client.Minecraft.getInstance().particleEngine.register(
                                        com.kingodogo.buildscape.particle.ModParticles.GLOW_LIME_SPARKLE.get(),
                                        sprites -> new com.kingodogo.buildscape.particle.PillarSparkleParticle.Provider(
                                                        sprites));

                        net.minecraft.client.Minecraft.getInstance().particleEngine.register(
                                        com.kingodogo.buildscape.particle.ModParticles.TINTED_DRIP_FALL.get(),
                                        sprites -> new com.kingodogo.buildscape.particle.TintedDripParticle.Provider(
                                                        sprites));

                        net.minecraft.client.Minecraft.getInstance().particleEngine.register(
                                        com.kingodogo.buildscape.particle.ModParticles.TINTED_SPORE.get(),
                                        sprites -> new com.kingodogo.buildscape.particle.TintedSporeParticle.Provider(
                                                        sprites));

                        net.minecraft.client.Minecraft.getInstance().particleEngine.register(
                                        com.kingodogo.buildscape.particle.ModParticles.SNOWFLAKE.get(),
                                        sprites -> new com.kingodogo.buildscape.particle.SnowflakeParticle.Provider(
                                                        sprites));

                        net.minecraft.client.Minecraft.getInstance().particleEngine.register(
                                        com.kingodogo.buildscape.particle.ModParticles.CONFETTI.get(),
                                        sprites -> new com.kingodogo.buildscape.particle.ConfettiParticle.Provider(
                                                        sprites));

                        net.minecraft.client.Minecraft.getInstance().particleEngine.register(
                                        com.kingodogo.buildscape.particle.ModParticles.BUBBLE.get(),
                                        sprites -> new com.kingodogo.buildscape.particle.BubbleParticle.Provider(
                                                        sprites));

                        net.minecraft.client.Minecraft.getInstance().particleEngine.register(
                                        com.kingodogo.buildscape.particle.ModParticles.TRAIL_NOTE.get(),
                                        sprites -> new com.kingodogo.buildscape.particle.TrailNoteParticle.Provider(
                                                        sprites));

                        net.minecraft.client.Minecraft.getInstance().particleEngine.register(
                                        com.kingodogo.buildscape.particle.ModParticles.COLORED_SMOKE.get(),
                                        sprites -> new com.kingodogo.buildscape.particle.ColoredSmokeParticle.Provider(
                                                        sprites));

                        net.minecraft.client.Minecraft.getInstance().particleEngine.register(
                                        com.kingodogo.buildscape.particle.ModParticles.TINTABLE_HEART.get(),
                                        sprites -> new com.kingodogo.buildscape.particle.TintableHeartParticle.Provider(
                                                        sprites));

                        net.minecraft.client.Minecraft.getInstance().particleEngine.register(
                                        com.kingodogo.buildscape.particle.ModParticles.CAKE.get(),
                                        sprites -> new com.kingodogo.buildscape.particle.CakeParticle.Provider(
                                                        sprites));

                        net.minecraft.client.Minecraft.getInstance().particleEngine.register(
                                        com.kingodogo.buildscape.particle.ModParticles.CHERRY.get(),
                                        sprites -> new com.kingodogo.buildscape.particle.CherryParticle.Provider(
                                                        sprites));
                }
        }
}
