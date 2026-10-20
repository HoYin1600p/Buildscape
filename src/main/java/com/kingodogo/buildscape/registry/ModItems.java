package com.kingodogo.buildscape.registry;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.feature.homemaker.HomemakerCooldownTracker;
import com.kingodogo.buildscape.registry.items.ItemsPart01;
import com.kingodogo.buildscape.registry.items.ItemsPart02;
import com.kingodogo.buildscape.registry.items.ItemsPart03;
import com.kingodogo.buildscape.registry.items.ItemsPart04;
import com.kingodogo.buildscape.registry.items.ItemsPart05;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(
            ForgeRegistries.ITEMS,
            BuildScape.MODID
    );

    public static Item.Properties createBlockItemProperties() {
        return new Item.Properties().tab(ModCreativeModeTab.BUILDSCAPE_TAB);
    }

    public static class WanderingHomemakerSpawnEggItem extends ForgeSpawnEggItem {
        public WanderingHomemakerSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, int highlightColor, Item.Properties properties) {
            super(type, backgroundColor, highlightColor, properties);
        }

        public int getColor(int tintIndex) {
            return -1;
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltip, flag);
            tooltip.add(new TranslatableComponent("tooltip.buildscape.wandering_homemaker_spawn_egg.spawn_info"));
            if (level != null) {
                long currentTime = System.currentTimeMillis();
                long endTime = HomemakerCooldownTracker.cooldownEndTime;
                if (endTime > currentTime) {
                    long remainingMs = endTime - currentTime;
                    long totalSeconds = remainingMs / 1000;
                    long minutes = totalSeconds / 60;
                    long seconds = totalSeconds % 60;
                    tooltip.add(new TranslatableComponent("tooltip.buildscape.wandering_homemaker_spawn_egg.cooldown_active", minutes, seconds)
                            .withStyle(ChatFormatting.RED));
                } else {
                    tooltip.add(new TranslatableComponent("tooltip.buildscape.wandering_homemaker_spawn_egg.cooldown_ready")
                            .withStyle(ChatFormatting.GREEN));
                }
            }
        }
    }

    public static class FestiveWanderingHomemakerSpawnEggItem extends ForgeSpawnEggItem {
        public FestiveWanderingHomemakerSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, int highlightColor, Item.Properties properties) {
            super(type, backgroundColor, highlightColor, properties);
        }

        public int getColor(int tintIndex) {
            return -1;
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltip, flag);
            tooltip.add(new TranslatableComponent("tooltip.buildscape.festive_wandering_homemaker_spawn_egg.spawn_info"));
            if (level != null) {
                long currentTime = System.currentTimeMillis();
                long endTime = HomemakerCooldownTracker.cooldownEndTime;
                if (endTime > currentTime) {
                    long remainingMs = endTime - currentTime;
                    long totalSeconds = remainingMs / 1000;
                    long minutes = totalSeconds / 60;
                    long seconds = totalSeconds % 60;
                    tooltip.add(new TranslatableComponent("tooltip.buildscape.wandering_homemaker_spawn_egg.cooldown_active", minutes, seconds)
                            .withStyle(ChatFormatting.RED));
                } else {
                    tooltip.add(new TranslatableComponent("tooltip.buildscape.wandering_homemaker_spawn_egg.cooldown_ready")
                            .withStyle(ChatFormatting.GREEN));
                }
            }
        }
    }

    public static RegistryObject<Item> registerBlockItem(String name, Supplier<? extends Block> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), createBlockItemProperties()));
    }

    static {
        ItemsPart01.init();
        ItemsPart02.init();
        ItemsPart03.init();
        ItemsPart04.init();
        ItemsPart05.init();
    }

    public static final RegistryObject<Item> BLACK_SAND = ItemsPart01.BLACK_SAND;
    public static final RegistryObject<Item> BLUE_SAND = ItemsPart01.BLUE_SAND;
    public static final RegistryObject<Item> GREEN_SAND = ItemsPart01.GREEN_SAND;
    public static final RegistryObject<Item> ORANGE_SAND = ItemsPart01.ORANGE_SAND;
    public static final RegistryObject<Item> PINK_SAND = ItemsPart01.PINK_SAND;
    public static final RegistryObject<Item> RED_SAND = ItemsPart01.RED_SAND;
    public static final RegistryObject<Item> WHITE_SAND = ItemsPart01.WHITE_SAND;
    public static final RegistryObject<Item> YELLOW_SAND = ItemsPart01.YELLOW_SAND;
    public static final RegistryObject<Item> BLACK_SANDSTONE = ItemsPart01.BLACK_SANDSTONE;
    public static final RegistryObject<Item> BLUE_SANDSTONE = ItemsPart01.BLUE_SANDSTONE;
    public static final RegistryObject<Item> GREEN_SANDSTONE = ItemsPart01.GREEN_SANDSTONE;
    public static final RegistryObject<Item> ORANGE_SANDSTONE = ItemsPart01.ORANGE_SANDSTONE;
    public static final RegistryObject<Item> PINK_SANDSTONE = ItemsPart01.PINK_SANDSTONE;
    public static final RegistryObject<Item> RED_SANDSTONE = ItemsPart01.RED_SANDSTONE;
    public static final RegistryObject<Item> WHITE_SANDSTONE = ItemsPart01.WHITE_SANDSTONE;
    public static final RegistryObject<Item> YELLOW_SANDSTONE = ItemsPart01.YELLOW_SANDSTONE;
    public static final RegistryObject<Item> BLACK_SMOOTH_SANDSTONE = ItemsPart01.BLACK_SMOOTH_SANDSTONE;
    public static final RegistryObject<Item> BLUE_SMOOTH_SANDSTONE = ItemsPart01.BLUE_SMOOTH_SANDSTONE;
    public static final RegistryObject<Item> GREEN_SMOOTH_SANDSTONE = ItemsPart01.GREEN_SMOOTH_SANDSTONE;
    public static final RegistryObject<Item> ORANGE_SMOOTH_SANDSTONE = ItemsPart01.ORANGE_SMOOTH_SANDSTONE;
    public static final RegistryObject<Item> PINK_SMOOTH_SANDSTONE = ItemsPart01.PINK_SMOOTH_SANDSTONE;
    public static final RegistryObject<Item> RED_SMOOTH_SANDSTONE = ItemsPart01.RED_SMOOTH_SANDSTONE;
    public static final RegistryObject<Item> WHITE_SMOOTH_SANDSTONE = ItemsPart01.WHITE_SMOOTH_SANDSTONE;
    public static final RegistryObject<Item> YELLOW_SMOOTH_SANDSTONE = ItemsPart01.YELLOW_SMOOTH_SANDSTONE;
    public static final RegistryObject<Item> BLACK_TILES = ItemsPart01.BLACK_TILES;
    public static final RegistryObject<Item> BLUE_TILES = ItemsPart01.BLUE_TILES;
    public static final RegistryObject<Item> BROWN_TILES = ItemsPart01.BROWN_TILES;
    public static final RegistryObject<Item> CYAN_TILES = ItemsPart01.CYAN_TILES;
    public static final RegistryObject<Item> GRAY_TILES = ItemsPart01.GRAY_TILES;
    public static final RegistryObject<Item> GREEN_TILES = ItemsPart01.GREEN_TILES;
    public static final RegistryObject<Item> LIGHT_BLUE_TILES = ItemsPart01.LIGHT_BLUE_TILES;
    public static final RegistryObject<Item> LIGHT_GRAY_TILES = ItemsPart01.LIGHT_GRAY_TILES;
    public static final RegistryObject<Item> LIME_TILES = ItemsPart01.LIME_TILES;
    public static final RegistryObject<Item> MAGENTA_TILES = ItemsPart01.MAGENTA_TILES;
    public static final RegistryObject<Item> ORANGE_TILES = ItemsPart01.ORANGE_TILES;
    public static final RegistryObject<Item> PINK_TILES = ItemsPart01.PINK_TILES;
    public static final RegistryObject<Item> PURPLE_TILES = ItemsPart01.PURPLE_TILES;
    public static final RegistryObject<Item> RED_TILES = ItemsPart01.RED_TILES;
    public static final RegistryObject<Item> WHITE_TILES = ItemsPart01.WHITE_TILES;
    public static final RegistryObject<Item> YELLOW_TILES = ItemsPart01.YELLOW_TILES;
    public static final RegistryObject<Item> BLACK_MOSAIC_GLASS = ItemsPart01.BLACK_MOSAIC_GLASS;
    public static final RegistryObject<Item> BLUE_MOSAIC_GLASS = ItemsPart01.BLUE_MOSAIC_GLASS;
    public static final RegistryObject<Item> BROWN_MOSAIC_GLASS = ItemsPart01.BROWN_MOSAIC_GLASS;
    public static final RegistryObject<Item> CYAN_MOSAIC_GLASS = ItemsPart01.CYAN_MOSAIC_GLASS;
    public static final RegistryObject<Item> GRAY_MOSAIC_GLASS = ItemsPart01.GRAY_MOSAIC_GLASS;
    public static final RegistryObject<Item> GREEN_MOSAIC_GLASS = ItemsPart01.GREEN_MOSAIC_GLASS;
    public static final RegistryObject<Item> LIGHT_BLUE_MOSAIC_GLASS = ItemsPart01.LIGHT_BLUE_MOSAIC_GLASS;
    public static final RegistryObject<Item> LIGHT_GRAY_MOSAIC_GLASS = ItemsPart01.LIGHT_GRAY_MOSAIC_GLASS;
    public static final RegistryObject<Item> LIME_MOSAIC_GLASS = ItemsPart01.LIME_MOSAIC_GLASS;
    public static final RegistryObject<Item> MAGENTA_MOSAIC_GLASS = ItemsPart01.MAGENTA_MOSAIC_GLASS;
    public static final RegistryObject<Item> ORANGE_MOSAIC_GLASS = ItemsPart01.ORANGE_MOSAIC_GLASS;
    public static final RegistryObject<Item> PINK_MOSAIC_GLASS = ItemsPart01.PINK_MOSAIC_GLASS;
    public static final RegistryObject<Item> PURPLE_MOSAIC_GLASS = ItemsPart01.PURPLE_MOSAIC_GLASS;
    public static final RegistryObject<Item> RED_MOSAIC_GLASS = ItemsPart01.RED_MOSAIC_GLASS;
    public static final RegistryObject<Item> WHITE_MOSAIC_GLASS = ItemsPart01.WHITE_MOSAIC_GLASS;
    public static final RegistryObject<Item> YELLOW_MOSAIC_GLASS = ItemsPart01.YELLOW_MOSAIC_GLASS;
    public static final RegistryObject<Item> BLACK_MOSAIC_GLASS_PANE = ItemsPart01.BLACK_MOSAIC_GLASS_PANE;
    public static final RegistryObject<Item> BLUE_MOSAIC_GLASS_PANE = ItemsPart01.BLUE_MOSAIC_GLASS_PANE;
    public static final RegistryObject<Item> BROWN_MOSAIC_GLASS_PANE = ItemsPart01.BROWN_MOSAIC_GLASS_PANE;
    public static final RegistryObject<Item> CYAN_MOSAIC_GLASS_PANE = ItemsPart01.CYAN_MOSAIC_GLASS_PANE;
    public static final RegistryObject<Item> GRAY_MOSAIC_GLASS_PANE = ItemsPart01.GRAY_MOSAIC_GLASS_PANE;
    public static final RegistryObject<Item> GREEN_MOSAIC_GLASS_PANE = ItemsPart01.GREEN_MOSAIC_GLASS_PANE;
    public static final RegistryObject<Item> LIGHT_BLUE_MOSAIC_GLASS_PANE = ItemsPart01.LIGHT_BLUE_MOSAIC_GLASS_PANE;
    public static final RegistryObject<Item> LIGHT_GRAY_MOSAIC_GLASS_PANE = ItemsPart01.LIGHT_GRAY_MOSAIC_GLASS_PANE;
    public static final RegistryObject<Item> LIME_MOSAIC_GLASS_PANE = ItemsPart01.LIME_MOSAIC_GLASS_PANE;
    public static final RegistryObject<Item> MAGENTA_MOSAIC_GLASS_PANE = ItemsPart01.MAGENTA_MOSAIC_GLASS_PANE;
    public static final RegistryObject<Item> ORANGE_MOSAIC_GLASS_PANE = ItemsPart01.ORANGE_MOSAIC_GLASS_PANE;
    public static final RegistryObject<Item> PINK_MOSAIC_GLASS_PANE = ItemsPart01.PINK_MOSAIC_GLASS_PANE;
    public static final RegistryObject<Item> PURPLE_MOSAIC_GLASS_PANE = ItemsPart01.PURPLE_MOSAIC_GLASS_PANE;
    public static final RegistryObject<Item> RED_MOSAIC_GLASS_PANE = ItemsPart01.RED_MOSAIC_GLASS_PANE;
    public static final RegistryObject<Item> WHITE_MOSAIC_GLASS_PANE = ItemsPart01.WHITE_MOSAIC_GLASS_PANE;
    public static final RegistryObject<Item> YELLOW_MOSAIC_GLASS_PANE = ItemsPart01.YELLOW_MOSAIC_GLASS_PANE;
    public static final RegistryObject<Item> BLACK_GLAZED_GLASS = ItemsPart01.BLACK_GLAZED_GLASS;
    public static final RegistryObject<Item> BLUE_GLAZED_GLASS = ItemsPart01.BLUE_GLAZED_GLASS;
    public static final RegistryObject<Item> BROWN_GLAZED_GLASS = ItemsPart01.BROWN_GLAZED_GLASS;
    public static final RegistryObject<Item> CYAN_GLAZED_GLASS = ItemsPart01.CYAN_GLAZED_GLASS;
    public static final RegistryObject<Item> GRAY_GLAZED_GLASS = ItemsPart01.GRAY_GLAZED_GLASS;
    public static final RegistryObject<Item> GREEN_GLAZED_GLASS = ItemsPart01.GREEN_GLAZED_GLASS;
    public static final RegistryObject<Item> LIGHT_BLUE_GLAZED_GLASS = ItemsPart01.LIGHT_BLUE_GLAZED_GLASS;
    public static final RegistryObject<Item> LIGHT_GRAY_GLAZED_GLASS = ItemsPart01.LIGHT_GRAY_GLAZED_GLASS;
    public static final RegistryObject<Item> LIME_GLAZED_GLASS = ItemsPart01.LIME_GLAZED_GLASS;
    public static final RegistryObject<Item> MAGENTA_GLAZED_GLASS = ItemsPart01.MAGENTA_GLAZED_GLASS;
    public static final RegistryObject<Item> ORANGE_GLAZED_GLASS = ItemsPart01.ORANGE_GLAZED_GLASS;
    public static final RegistryObject<Item> PINK_GLAZED_GLASS = ItemsPart01.PINK_GLAZED_GLASS;
    public static final RegistryObject<Item> PURPLE_GLAZED_GLASS = ItemsPart01.PURPLE_GLAZED_GLASS;
    public static final RegistryObject<Item> RED_GLAZED_GLASS = ItemsPart01.RED_GLAZED_GLASS;
    public static final RegistryObject<Item> WHITE_GLAZED_GLASS = ItemsPart01.WHITE_GLAZED_GLASS;
    public static final RegistryObject<Item> YELLOW_GLAZED_GLASS = ItemsPart01.YELLOW_GLAZED_GLASS;
    public static final RegistryObject<Item> BLACK_GLAZED_GLASS_PANE = ItemsPart01.BLACK_GLAZED_GLASS_PANE;
    public static final RegistryObject<Item> BLUE_GLAZED_GLASS_PANE = ItemsPart01.BLUE_GLAZED_GLASS_PANE;
    public static final RegistryObject<Item> BROWN_GLAZED_GLASS_PANE = ItemsPart01.BROWN_GLAZED_GLASS_PANE;
    public static final RegistryObject<Item> CYAN_GLAZED_GLASS_PANE = ItemsPart01.CYAN_GLAZED_GLASS_PANE;
    public static final RegistryObject<Item> GRAY_GLAZED_GLASS_PANE = ItemsPart01.GRAY_GLAZED_GLASS_PANE;
    public static final RegistryObject<Item> GREEN_GLAZED_GLASS_PANE = ItemsPart01.GREEN_GLAZED_GLASS_PANE;
    public static final RegistryObject<Item> LIGHT_BLUE_GLAZED_GLASS_PANE = ItemsPart01.LIGHT_BLUE_GLAZED_GLASS_PANE;
    public static final RegistryObject<Item> LIGHT_GRAY_GLAZED_GLASS_PANE = ItemsPart01.LIGHT_GRAY_GLAZED_GLASS_PANE;
    public static final RegistryObject<Item> LIME_GLAZED_GLASS_PANE = ItemsPart01.LIME_GLAZED_GLASS_PANE;
    public static final RegistryObject<Item> MAGENTA_GLAZED_GLASS_PANE = ItemsPart01.MAGENTA_GLAZED_GLASS_PANE;
    public static final RegistryObject<Item> ORANGE_GLAZED_GLASS_PANE = ItemsPart01.ORANGE_GLAZED_GLASS_PANE;
    public static final RegistryObject<Item> PINK_GLAZED_GLASS_PANE = ItemsPart01.PINK_GLAZED_GLASS_PANE;
    public static final RegistryObject<Item> PURPLE_GLAZED_GLASS_PANE = ItemsPart01.PURPLE_GLAZED_GLASS_PANE;
    public static final RegistryObject<Item> RED_GLAZED_GLASS_PANE = ItemsPart01.RED_GLAZED_GLASS_PANE;
    public static final RegistryObject<Item> WHITE_GLAZED_GLASS_PANE = ItemsPart01.WHITE_GLAZED_GLASS_PANE;
    public static final RegistryObject<Item> YELLOW_GLAZED_GLASS_PANE = ItemsPart01.YELLOW_GLAZED_GLASS_PANE;
    public static final RegistryObject<Item> BIT_CHISELED_COPPER = ItemsPart01.BIT_CHISELED_COPPER;
    public static final RegistryObject<Item> BIT_COPPER_BLOCK = ItemsPart01.BIT_COPPER_BLOCK;
    public static final RegistryObject<Item> BIT_COPPER_BULB = ItemsPart01.BIT_COPPER_BULB;
    public static final RegistryObject<Item> BIT_COPPER_GRATE = ItemsPart01.BIT_COPPER_GRATE;
    public static final RegistryObject<Item> BIT_CUT_COPPER = ItemsPart01.BIT_CUT_COPPER;
    public static final RegistryObject<Item> BIT_EXPOSED_CHISELED_COPPER = ItemsPart01.BIT_EXPOSED_CHISELED_COPPER;
    public static final RegistryObject<Item> BIT_EXPOSED_COPPER_BLOCK = ItemsPart01.BIT_EXPOSED_COPPER_BLOCK;
    public static final RegistryObject<Item> BIT_EXPOSED_COPPER_BULB = ItemsPart01.BIT_EXPOSED_COPPER_BULB;
    public static final RegistryObject<Item> BIT_EXPOSED_COPPER_GRATE = ItemsPart01.BIT_EXPOSED_COPPER_GRATE;
    public static final RegistryObject<Item> BIT_EXPOSED_CUT_COPPER = ItemsPart01.BIT_EXPOSED_CUT_COPPER;
    public static final RegistryObject<Item> BIT_WEATHERED_CHISELED_COPPER = ItemsPart01.BIT_WEATHERED_CHISELED_COPPER;
    public static final RegistryObject<Item> BIT_WEATHERED_COPPER_BLOCK = ItemsPart01.BIT_WEATHERED_COPPER_BLOCK;
    public static final RegistryObject<Item> BIT_WEATHERED_COPPER_BULB = ItemsPart01.BIT_WEATHERED_COPPER_BULB;
    public static final RegistryObject<Item> BIT_WEATHERED_COPPER_GRATE = ItemsPart01.BIT_WEATHERED_COPPER_GRATE;
    public static final RegistryObject<Item> BIT_WEATHERED_CUT_COPPER = ItemsPart01.BIT_WEATHERED_CUT_COPPER;
    public static final RegistryObject<Item> BIT_OXIDIZED_CHISELED_COPPER = ItemsPart01.BIT_OXIDIZED_CHISELED_COPPER;
    public static final RegistryObject<Item> BIT_OXIDIZED_COPPER_BLOCK = ItemsPart01.BIT_OXIDIZED_COPPER_BLOCK;
    public static final RegistryObject<Item> BIT_OXIDIZED_COPPER_BULB = ItemsPart01.BIT_OXIDIZED_COPPER_BULB;
    public static final RegistryObject<Item> BIT_OXIDIZED_COPPER_GRATE = ItemsPart01.BIT_OXIDIZED_COPPER_GRATE;
    public static final RegistryObject<Item> BIT_OXIDIZED_CUT_COPPER = ItemsPart01.BIT_OXIDIZED_CUT_COPPER;
    public static final RegistryObject<Item> BIT_CHISELED_TUFF = ItemsPart01.BIT_CHISELED_TUFF;
    public static final RegistryObject<Item> BIT_CHISELED_TUFF_BRICKS = ItemsPart01.BIT_CHISELED_TUFF_BRICKS;
    public static final RegistryObject<Item> BIT_POLISHED_TUFF = ItemsPart01.BIT_POLISHED_TUFF;
    public static final RegistryObject<Item> BIT_POLISHED_TUFF_STAIRS = ItemsPart01.BIT_POLISHED_TUFF_STAIRS;
    public static final RegistryObject<Item> BIT_POLISHED_TUFF_SLAB = ItemsPart01.BIT_POLISHED_TUFF_SLAB;
    public static final RegistryObject<Item> BIT_POLISHED_TUFF_WALL = ItemsPart01.BIT_POLISHED_TUFF_WALL;
    public static final RegistryObject<Item> BIT_TUFF_BRICKS = ItemsPart01.BIT_TUFF_BRICKS;
    public static final RegistryObject<Item> BIT_TUFF_BRICKS_STAIRS = ItemsPart01.BIT_TUFF_BRICKS_STAIRS;
    public static final RegistryObject<Item> BIT_TUFF_BRICKS_SLAB = ItemsPart01.BIT_TUFF_BRICKS_SLAB;
    public static final RegistryObject<Item> BIT_TUFF_BRICKS_WALL = ItemsPart01.BIT_TUFF_BRICKS_WALL;
    public static final RegistryObject<Item> BLACK_SANDSTONE_STAIRS = ItemsPart01.BLACK_SANDSTONE_STAIRS;
    public static final RegistryObject<Item> BLUE_SANDSTONE_STAIRS = ItemsPart01.BLUE_SANDSTONE_STAIRS;
    public static final RegistryObject<Item> GREEN_SANDSTONE_STAIRS = ItemsPart01.GREEN_SANDSTONE_STAIRS;
    public static final RegistryObject<Item> ORANGE_SANDSTONE_STAIRS = ItemsPart01.ORANGE_SANDSTONE_STAIRS;
    public static final RegistryObject<Item> PINK_SANDSTONE_STAIRS = ItemsPart01.PINK_SANDSTONE_STAIRS;
    public static final RegistryObject<Item> RED_SANDSTONE_STAIRS = ItemsPart01.RED_SANDSTONE_STAIRS;
    public static final RegistryObject<Item> WHITE_SANDSTONE_STAIRS = ItemsPart01.WHITE_SANDSTONE_STAIRS;
    public static final RegistryObject<Item> YELLOW_SANDSTONE_STAIRS = ItemsPart01.YELLOW_SANDSTONE_STAIRS;
    public static final RegistryObject<Item> BLACK_SANDSTONE_SLAB = ItemsPart01.BLACK_SANDSTONE_SLAB;
    public static final RegistryObject<Item> BLUE_SANDSTONE_SLAB = ItemsPart01.BLUE_SANDSTONE_SLAB;
    public static final RegistryObject<Item> GREEN_SANDSTONE_SLAB = ItemsPart01.GREEN_SANDSTONE_SLAB;
    public static final RegistryObject<Item> ORANGE_SANDSTONE_SLAB = ItemsPart01.ORANGE_SANDSTONE_SLAB;
    public static final RegistryObject<Item> PINK_SANDSTONE_SLAB = ItemsPart01.PINK_SANDSTONE_SLAB;
    public static final RegistryObject<Item> RED_SANDSTONE_SLAB = ItemsPart01.RED_SANDSTONE_SLAB;
    public static final RegistryObject<Item> WHITE_SANDSTONE_SLAB = ItemsPart01.WHITE_SANDSTONE_SLAB;
    public static final RegistryObject<Item> YELLOW_SANDSTONE_SLAB = ItemsPart01.YELLOW_SANDSTONE_SLAB;
    public static final RegistryObject<Item> BLACK_SANDSTONE_WALL = ItemsPart01.BLACK_SANDSTONE_WALL;
    public static final RegistryObject<Item> BLUE_SANDSTONE_WALL = ItemsPart01.BLUE_SANDSTONE_WALL;
    public static final RegistryObject<Item> GREEN_SANDSTONE_WALL = ItemsPart01.GREEN_SANDSTONE_WALL;
    public static final RegistryObject<Item> ORANGE_SANDSTONE_WALL = ItemsPart01.ORANGE_SANDSTONE_WALL;
    public static final RegistryObject<Item> PINK_SANDSTONE_WALL = ItemsPart01.PINK_SANDSTONE_WALL;
    public static final RegistryObject<Item> RED_SANDSTONE_WALL = ItemsPart01.RED_SANDSTONE_WALL;
    public static final RegistryObject<Item> WHITE_SANDSTONE_WALL = ItemsPart01.WHITE_SANDSTONE_WALL;
    public static final RegistryObject<Item> YELLOW_SANDSTONE_WALL = ItemsPart01.YELLOW_SANDSTONE_WALL;
    public static final RegistryObject<Item> WHITE_SMOOTH_SANDSTONE_WALL = ItemsPart01.WHITE_SMOOTH_SANDSTONE_WALL;
    public static final RegistryObject<Item> BLACK_SMOOTH_SANDSTONE_WALL = ItemsPart01.BLACK_SMOOTH_SANDSTONE_WALL;
    public static final RegistryObject<Item> RED_SMOOTH_SANDSTONE_WALL = ItemsPart01.RED_SMOOTH_SANDSTONE_WALL;
    public static final RegistryObject<Item> ORANGE_SMOOTH_SANDSTONE_WALL = ItemsPart01.ORANGE_SMOOTH_SANDSTONE_WALL;
    public static final RegistryObject<Item> YELLOW_SMOOTH_SANDSTONE_WALL = ItemsPart01.YELLOW_SMOOTH_SANDSTONE_WALL;
    public static final RegistryObject<Item> GREEN_SMOOTH_SANDSTONE_WALL = ItemsPart01.GREEN_SMOOTH_SANDSTONE_WALL;
    public static final RegistryObject<Item> BLUE_SMOOTH_SANDSTONE_WALL = ItemsPart01.BLUE_SMOOTH_SANDSTONE_WALL;
    public static final RegistryObject<Item> PINK_SMOOTH_SANDSTONE_WALL = ItemsPart01.PINK_SMOOTH_SANDSTONE_WALL;
    public static final RegistryObject<Item> SMOOTH_SANDSTONE_WALL = ItemsPart01.SMOOTH_SANDSTONE_WALL;
    public static final RegistryObject<Item> BLACK_SMOOTH_SANDSTONE_SLAB = ItemsPart01.BLACK_SMOOTH_SANDSTONE_SLAB;
    public static final RegistryObject<Item> BLUE_SMOOTH_SANDSTONE_SLAB = ItemsPart01.BLUE_SMOOTH_SANDSTONE_SLAB;
    public static final RegistryObject<Item> GREEN_SMOOTH_SANDSTONE_SLAB = ItemsPart01.GREEN_SMOOTH_SANDSTONE_SLAB;
    public static final RegistryObject<Item> ORANGE_SMOOTH_SANDSTONE_SLAB = ItemsPart01.ORANGE_SMOOTH_SANDSTONE_SLAB;
    public static final RegistryObject<Item> PINK_SMOOTH_SANDSTONE_SLAB = ItemsPart01.PINK_SMOOTH_SANDSTONE_SLAB;
    public static final RegistryObject<Item> RED_SMOOTH_SANDSTONE_SLAB = ItemsPart01.RED_SMOOTH_SANDSTONE_SLAB;
    public static final RegistryObject<Item> WHITE_SMOOTH_SANDSTONE_SLAB = ItemsPart01.WHITE_SMOOTH_SANDSTONE_SLAB;
    public static final RegistryObject<Item> YELLOW_SMOOTH_SANDSTONE_SLAB = ItemsPart01.YELLOW_SMOOTH_SANDSTONE_SLAB;
    public static final RegistryObject<Item> BLACK_SMOOTH_SANDSTONE_STAIRS = ItemsPart01.BLACK_SMOOTH_SANDSTONE_STAIRS;
    public static final RegistryObject<Item> BLUE_SMOOTH_SANDSTONE_STAIRS = ItemsPart01.BLUE_SMOOTH_SANDSTONE_STAIRS;
    public static final RegistryObject<Item> GREEN_SMOOTH_SANDSTONE_STAIRS = ItemsPart01.GREEN_SMOOTH_SANDSTONE_STAIRS;
    public static final RegistryObject<Item> ORANGE_SMOOTH_SANDSTONE_STAIRS =
            ItemsPart01.ORANGE_SMOOTH_SANDSTONE_STAIRS;
    public static final RegistryObject<Item> PINK_SMOOTH_SANDSTONE_STAIRS = ItemsPart01.PINK_SMOOTH_SANDSTONE_STAIRS;
    public static final RegistryObject<Item> RED_SMOOTH_SANDSTONE_STAIRS = ItemsPart01.RED_SMOOTH_SANDSTONE_STAIRS;
    public static final RegistryObject<Item> WHITE_SMOOTH_SANDSTONE_STAIRS = ItemsPart01.WHITE_SMOOTH_SANDSTONE_STAIRS;
    public static final RegistryObject<Item> YELLOW_SMOOTH_SANDSTONE_STAIRS =
            ItemsPart01.YELLOW_SMOOTH_SANDSTONE_STAIRS;
    public static final RegistryObject<Item> BLACK_TILES_STAIRS = ItemsPart01.BLACK_TILES_STAIRS;
    public static final RegistryObject<Item> BLUE_TILES_STAIRS = ItemsPart01.BLUE_TILES_STAIRS;
    public static final RegistryObject<Item> BROWN_TILES_STAIRS = ItemsPart01.BROWN_TILES_STAIRS;
    public static final RegistryObject<Item> CYAN_TILES_STAIRS = ItemsPart01.CYAN_TILES_STAIRS;
    public static final RegistryObject<Item> GRAY_TILES_STAIRS = ItemsPart01.GRAY_TILES_STAIRS;
    public static final RegistryObject<Item> GREEN_TILES_STAIRS = ItemsPart01.GREEN_TILES_STAIRS;
    public static final RegistryObject<Item> LIGHT_BLUE_TILES_STAIRS = ItemsPart01.LIGHT_BLUE_TILES_STAIRS;
    public static final RegistryObject<Item> LIGHT_GRAY_TILES_STAIRS = ItemsPart01.LIGHT_GRAY_TILES_STAIRS;
    public static final RegistryObject<Item> LIME_TILES_STAIRS = ItemsPart01.LIME_TILES_STAIRS;
    public static final RegistryObject<Item> MAGENTA_TILES_STAIRS = ItemsPart01.MAGENTA_TILES_STAIRS;
    public static final RegistryObject<Item> ORANGE_TILES_STAIRS = ItemsPart01.ORANGE_TILES_STAIRS;
    public static final RegistryObject<Item> PINK_TILES_STAIRS = ItemsPart01.PINK_TILES_STAIRS;
    public static final RegistryObject<Item> PURPLE_TILES_STAIRS = ItemsPart01.PURPLE_TILES_STAIRS;
    public static final RegistryObject<Item> RED_TILES_STAIRS = ItemsPart01.RED_TILES_STAIRS;
    public static final RegistryObject<Item> WHITE_TILES_STAIRS = ItemsPart01.WHITE_TILES_STAIRS;
    public static final RegistryObject<Item> YELLOW_TILES_STAIRS = ItemsPart01.YELLOW_TILES_STAIRS;
    public static final RegistryObject<Item> BLACK_TILES_SLAB = ItemsPart01.BLACK_TILES_SLAB;
    public static final RegistryObject<Item> BLUE_TILES_SLAB = ItemsPart01.BLUE_TILES_SLAB;
    public static final RegistryObject<Item> BROWN_TILES_SLAB = ItemsPart01.BROWN_TILES_SLAB;
    public static final RegistryObject<Item> CYAN_TILES_SLAB = ItemsPart01.CYAN_TILES_SLAB;
    public static final RegistryObject<Item> GRAY_TILES_SLAB = ItemsPart01.GRAY_TILES_SLAB;
    public static final RegistryObject<Item> GREEN_TILES_SLAB = ItemsPart01.GREEN_TILES_SLAB;
    public static final RegistryObject<Item> LIGHT_BLUE_TILES_SLAB = ItemsPart01.LIGHT_BLUE_TILES_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_TILES_SLAB = ItemsPart01.LIGHT_GRAY_TILES_SLAB;
    public static final RegistryObject<Item> LIME_TILES_SLAB = ItemsPart01.LIME_TILES_SLAB;
    public static final RegistryObject<Item> MAGENTA_TILES_SLAB = ItemsPart01.MAGENTA_TILES_SLAB;
    public static final RegistryObject<Item> ORANGE_TILES_SLAB = ItemsPart01.ORANGE_TILES_SLAB;
    public static final RegistryObject<Item> PINK_TILES_SLAB = ItemsPart01.PINK_TILES_SLAB;
    public static final RegistryObject<Item> PURPLE_TILES_SLAB = ItemsPart01.PURPLE_TILES_SLAB;
    public static final RegistryObject<Item> RED_TILES_SLAB = ItemsPart01.RED_TILES_SLAB;
    public static final RegistryObject<Item> WHITE_TILES_SLAB = ItemsPart01.WHITE_TILES_SLAB;
    public static final RegistryObject<Item> YELLOW_TILES_SLAB = ItemsPart01.YELLOW_TILES_SLAB;
    public static final RegistryObject<Item> BLACK_TILES_WALL = ItemsPart01.BLACK_TILES_WALL;
    public static final RegistryObject<Item> BLUE_TILES_WALL = ItemsPart01.BLUE_TILES_WALL;
    public static final RegistryObject<Item> BROWN_TILES_WALL = ItemsPart01.BROWN_TILES_WALL;
    public static final RegistryObject<Item> CYAN_TILES_WALL = ItemsPart01.CYAN_TILES_WALL;
    public static final RegistryObject<Item> GRAY_TILES_WALL = ItemsPart01.GRAY_TILES_WALL;
    public static final RegistryObject<Item> GREEN_TILES_WALL = ItemsPart01.GREEN_TILES_WALL;
    public static final RegistryObject<Item> LIGHT_BLUE_TILES_WALL = ItemsPart01.LIGHT_BLUE_TILES_WALL;
    public static final RegistryObject<Item> LIGHT_GRAY_TILES_WALL = ItemsPart01.LIGHT_GRAY_TILES_WALL;
    public static final RegistryObject<Item> LIME_TILES_WALL = ItemsPart01.LIME_TILES_WALL;
    public static final RegistryObject<Item> MAGENTA_TILES_WALL = ItemsPart01.MAGENTA_TILES_WALL;
    public static final RegistryObject<Item> ORANGE_TILES_WALL = ItemsPart01.ORANGE_TILES_WALL;
    public static final RegistryObject<Item> PINK_TILES_WALL = ItemsPart01.PINK_TILES_WALL;
    public static final RegistryObject<Item> PURPLE_TILES_WALL = ItemsPart01.PURPLE_TILES_WALL;
    public static final RegistryObject<Item> RED_TILES_WALL = ItemsPart01.RED_TILES_WALL;
    public static final RegistryObject<Item> WHITE_TILES_WALL = ItemsPart01.WHITE_TILES_WALL;
    public static final RegistryObject<Item> YELLOW_TILES_WALL = ItemsPart01.YELLOW_TILES_WALL;
    public static final RegistryObject<Item> POLISHED_BASALT_STAIRS = ItemsPart01.POLISHED_BASALT_STAIRS;
    public static final RegistryObject<Item> POLISHED_BASALT_SLAB = ItemsPart01.POLISHED_BASALT_SLAB;
    public static final RegistryObject<Item> POLISHED_BASALT_WALL = ItemsPart01.POLISHED_BASALT_WALL;
    public static final RegistryObject<Item> DRIPSTONE_BLOCK_STAIRS = ItemsPart01.DRIPSTONE_BLOCK_STAIRS;
    public static final RegistryObject<Item> DRIPSTONE_BLOCK_SLAB = ItemsPart01.DRIPSTONE_BLOCK_SLAB;
    public static final RegistryObject<Item> DRIPSTONE_BLOCK_WALL = ItemsPart01.DRIPSTONE_BLOCK_WALL;
    public static final RegistryObject<Item> END_STONE_STAIRS = ItemsPart01.END_STONE_STAIRS;
    public static final RegistryObject<Item> END_STONE_SLAB = ItemsPart01.END_STONE_SLAB;
    public static final RegistryObject<Item> END_STONE_WALL = ItemsPart01.END_STONE_WALL;
    public static final RegistryObject<Item> STONE_WALL = ItemsPart01.STONE_WALL;
    public static final RegistryObject<Item> QUARTZ_BRICKS_STAIRS = ItemsPart01.QUARTZ_BRICKS_STAIRS;
    public static final RegistryObject<Item> QUARTZ_BRICKS_SLAB = ItemsPart01.QUARTZ_BRICKS_SLAB;
    public static final RegistryObject<Item> QUARTZ_BRICKS_WALL = ItemsPart01.QUARTZ_BRICKS_WALL;
    public static final RegistryObject<Item> QUARTZ_PILLAR_STAIRS = ItemsPart01.QUARTZ_PILLAR_STAIRS;
    public static final RegistryObject<Item> QUARTZ_PILLAR_SLAB = ItemsPart01.QUARTZ_PILLAR_SLAB;
    public static final RegistryObject<Item> QUARTZ_PILLAR_WALL = ItemsPart01.QUARTZ_PILLAR_WALL;
    public static final RegistryObject<Item> CALCITE_STAIRS = ItemsPart01.CALCITE_STAIRS;
    public static final RegistryObject<Item> CALCITE_SLAB = ItemsPart01.CALCITE_SLAB;
    public static final RegistryObject<Item> CALCITE_WALL = ItemsPart01.CALCITE_WALL;
    public static final RegistryObject<Item> BONE_BLOCK_STAIRS = ItemsPart01.BONE_BLOCK_STAIRS;
    public static final RegistryObject<Item> BONE_BLOCK_SLAB = ItemsPart01.BONE_BLOCK_SLAB;
    public static final RegistryObject<Item> BONE_BLOCK_WALL = ItemsPart01.BONE_BLOCK_WALL;
    public static final RegistryObject<Item> STACKED_BONE_BLOCK = ItemsPart01.STACKED_BONE_BLOCK;
    public static final RegistryObject<Item> STACKED_BONE_STAIRS = ItemsPart01.STACKED_BONE_STAIRS;
    public static final RegistryObject<Item> STACKED_BONE_SLAB = ItemsPart01.STACKED_BONE_SLAB;
    public static final RegistryObject<Item> STACKED_BONE_WALL = ItemsPart01.STACKED_BONE_WALL;
    public static final RegistryObject<Item> POLISHED_BONE_BLOCK = ItemsPart01.POLISHED_BONE_BLOCK;
    public static final RegistryObject<Item> POLISHED_BONE_STAIRS = ItemsPart01.POLISHED_BONE_STAIRS;
    public static final RegistryObject<Item> POLISHED_BONE_SLAB = ItemsPart01.POLISHED_BONE_SLAB;
    public static final RegistryObject<Item> POLISHED_BONE_WALL = ItemsPart01.POLISHED_BONE_WALL;
    public static final RegistryObject<Item> BONE_BRICKS = ItemsPart01.BONE_BRICKS;
    public static final RegistryObject<Item> BONE_BRICK_STAIRS = ItemsPart01.BONE_BRICK_STAIRS;
    public static final RegistryObject<Item> BONE_BRICK_SLAB = ItemsPart01.BONE_BRICK_SLAB;
    public static final RegistryObject<Item> BONE_BRICK_WALL = ItemsPart01.BONE_BRICK_WALL;
    public static final RegistryObject<Item> CHISELED_BONE_BLOCK = ItemsPart01.CHISELED_BONE_BLOCK;
    public static final RegistryObject<Item> BONE_PILLAR = ItemsPart01.BONE_PILLAR;
    public static final RegistryObject<Item> BONE_DICE = ItemsPart01.BONE_DICE;
    public static final RegistryObject<Item> BEDROCK_SLAB = ItemsPart01.BEDROCK_SLAB;
    public static final RegistryObject<Item> BEDROCK_STAIRS = ItemsPart01.BEDROCK_STAIRS;
    public static final RegistryObject<Item> BEDROCK_WALL = ItemsPart01.BEDROCK_WALL;
    public static final RegistryObject<Item> BEDROCK_PANE = ItemsPart01.BEDROCK_PANE;
    public static final RegistryObject<Item> OBSIDIAN_STAIRS = ItemsPart01.OBSIDIAN_STAIRS;
    public static final RegistryObject<Item> OBSIDIAN_SLAB = ItemsPart01.OBSIDIAN_SLAB;
    public static final RegistryObject<Item> PRISMARINE_BRICKS_WALL = ItemsPart01.PRISMARINE_BRICKS_WALL;
    public static final RegistryObject<Item> DARK_PRISMARINE_WALL = ItemsPart01.DARK_PRISMARINE_WALL;
    public static final RegistryObject<Item> QUARTZ_BLOCK_WALL = ItemsPart01.QUARTZ_BLOCK_WALL;
    public static final RegistryObject<Item> SMOOTH_QUARTZ_WALL = ItemsPart01.SMOOTH_QUARTZ_WALL;
    public static final RegistryObject<Item> SMOOTH_BASALT_STAIRS = ItemsPart01.SMOOTH_BASALT_STAIRS;
    public static final RegistryObject<Item> SMOOTH_BASALT_SLAB = ItemsPart01.SMOOTH_BASALT_SLAB;
    public static final RegistryObject<Item> MOSS_BLOCK_SLAB = ItemsPart01.MOSS_BLOCK_SLAB;
    public static final RegistryObject<Item> AMETHYST_BLOCK_SLAB = ItemsPart01.AMETHYST_BLOCK_SLAB;
    public static final RegistryObject<Item> BIT_COPPER_BLOCK_STAIRS = ItemsPart01.BIT_COPPER_BLOCK_STAIRS;
    public static final RegistryObject<Item> BIT_COPPER_BLOCK_SLAB = ItemsPart01.BIT_COPPER_BLOCK_SLAB;
    public static final RegistryObject<Item> BIT_COPPER_BLOCK_WALL = ItemsPart01.BIT_COPPER_BLOCK_WALL;
    public static final RegistryObject<Item> BIT_EXPOSED_COPPER_BLOCK_STAIRS =
            ItemsPart01.BIT_EXPOSED_COPPER_BLOCK_STAIRS;
    public static final RegistryObject<Item> BIT_EXPOSED_COPPER_BLOCK_SLAB = ItemsPart01.BIT_EXPOSED_COPPER_BLOCK_SLAB;
    public static final RegistryObject<Item> BIT_EXPOSED_COPPER_BLOCK_WALL = ItemsPart01.BIT_EXPOSED_COPPER_BLOCK_WALL;
    public static final RegistryObject<Item> BIT_WEATHERED_COPPER_BLOCK_STAIRS =
            ItemsPart01.BIT_WEATHERED_COPPER_BLOCK_STAIRS;
    public static final RegistryObject<Item> BIT_WEATHERED_COPPER_BLOCK_SLAB =
            ItemsPart01.BIT_WEATHERED_COPPER_BLOCK_SLAB;
    public static final RegistryObject<Item> BIT_WEATHERED_COPPER_BLOCK_WALL =
            ItemsPart01.BIT_WEATHERED_COPPER_BLOCK_WALL;
    public static final RegistryObject<Item> BIT_OXIDIZED_COPPER_BLOCK_STAIRS =
            ItemsPart01.BIT_OXIDIZED_COPPER_BLOCK_STAIRS;
    public static final RegistryObject<Item> BIT_OXIDIZED_COPPER_BLOCK_SLAB =
            ItemsPart01.BIT_OXIDIZED_COPPER_BLOCK_SLAB;
    public static final RegistryObject<Item> BIT_OXIDIZED_COPPER_BLOCK_WALL =
            ItemsPart01.BIT_OXIDIZED_COPPER_BLOCK_WALL;
    public static final RegistryObject<Item> BIT_CUT_COPPER_STAIRS = ItemsPart01.BIT_CUT_COPPER_STAIRS;
    public static final RegistryObject<Item> BIT_CUT_COPPER_SLAB = ItemsPart01.BIT_CUT_COPPER_SLAB;
    public static final RegistryObject<Item> BIT_CUT_COPPER_WALL = ItemsPart01.BIT_CUT_COPPER_WALL;
    public static final RegistryObject<Item> BIT_EXPOSED_CUT_COPPER_STAIRS = ItemsPart01.BIT_EXPOSED_CUT_COPPER_STAIRS;
    public static final RegistryObject<Item> BIT_EXPOSED_CUT_COPPER_SLAB = ItemsPart01.BIT_EXPOSED_CUT_COPPER_SLAB;
    public static final RegistryObject<Item> BIT_EXPOSED_CUT_COPPER_WALL = ItemsPart01.BIT_EXPOSED_CUT_COPPER_WALL;
    public static final RegistryObject<Item> BIT_WEATHERED_CUT_COPPER_STAIRS =
            ItemsPart01.BIT_WEATHERED_CUT_COPPER_STAIRS;
    public static final RegistryObject<Item> BIT_WEATHERED_CUT_COPPER_SLAB = ItemsPart01.BIT_WEATHERED_CUT_COPPER_SLAB;
    public static final RegistryObject<Item> BIT_WEATHERED_CUT_COPPER_WALL = ItemsPart01.BIT_WEATHERED_CUT_COPPER_WALL;
    public static final RegistryObject<Item> BIT_OXIDIZED_CUT_COPPER_STAIRS =
            ItemsPart01.BIT_OXIDIZED_CUT_COPPER_STAIRS;
    public static final RegistryObject<Item> BIT_OXIDIZED_CUT_COPPER_SLAB = ItemsPart01.BIT_OXIDIZED_CUT_COPPER_SLAB;
    public static final RegistryObject<Item> BIT_OXIDIZED_CUT_COPPER_WALL = ItemsPart01.BIT_OXIDIZED_CUT_COPPER_WALL;
    public static final RegistryObject<Item> MOSSY_CALCITE = ItemsPart01.MOSSY_CALCITE;
    public static final RegistryObject<Item> MOSSY_CALCITE_STAIRS = ItemsPart01.MOSSY_CALCITE_STAIRS;
    public static final RegistryObject<Item> MOSSY_CALCITE_SLAB = ItemsPart01.MOSSY_CALCITE_SLAB;
    public static final RegistryObject<Item> MOSSY_CALCITE_WALL = ItemsPart01.MOSSY_CALCITE_WALL;
    public static final RegistryObject<Item> PODZOL_SLAB = ItemsPart01.PODZOL_SLAB;
    public static final RegistryObject<Item> DIRT_SLAB = ItemsPart01.DIRT_SLAB;
    public static final RegistryObject<Item> MUD = ItemsPart01.MUD;
    public static final RegistryObject<Item> MUD_STAIRS = ItemsPart01.MUD_STAIRS;
    public static final RegistryObject<Item> MUD_SLAB = ItemsPart01.MUD_SLAB;
    public static final RegistryObject<Item> MYCELIUM_SLAB = ItemsPart01.MYCELIUM_SLAB;
    public static final RegistryObject<Item> MOSS_LAYERS = ItemsPart01.MOSS_LAYERS;
    public static final RegistryObject<Item> MOSS_OVERLAY = ItemsPart01.MOSS_OVERLAY;
    public static final RegistryObject<Item> SNOW_OVERLAY = ItemsPart01.SNOW_OVERLAY;
    public static final RegistryObject<Item> BLACK_CONCRETE_STAIRS = ItemsPart01.BLACK_CONCRETE_STAIRS;
    public static final RegistryObject<Item> BLUE_CONCRETE_STAIRS = ItemsPart01.BLUE_CONCRETE_STAIRS;
    public static final RegistryObject<Item> BROWN_CONCRETE_STAIRS = ItemsPart01.BROWN_CONCRETE_STAIRS;
    public static final RegistryObject<Item> CYAN_CONCRETE_STAIRS = ItemsPart01.CYAN_CONCRETE_STAIRS;
    public static final RegistryObject<Item> GRAY_CONCRETE_STAIRS = ItemsPart01.GRAY_CONCRETE_STAIRS;
    public static final RegistryObject<Item> GREEN_CONCRETE_STAIRS = ItemsPart01.GREEN_CONCRETE_STAIRS;
    public static final RegistryObject<Item> LIGHT_BLUE_CONCRETE_STAIRS = ItemsPart01.LIGHT_BLUE_CONCRETE_STAIRS;
    public static final RegistryObject<Item> LIGHT_GRAY_CONCRETE_STAIRS = ItemsPart01.LIGHT_GRAY_CONCRETE_STAIRS;
    public static final RegistryObject<Item> LIME_CONCRETE_STAIRS = ItemsPart01.LIME_CONCRETE_STAIRS;
    public static final RegistryObject<Item> MAGENTA_CONCRETE_STAIRS = ItemsPart01.MAGENTA_CONCRETE_STAIRS;
    public static final RegistryObject<Item> ORANGE_CONCRETE_STAIRS = ItemsPart01.ORANGE_CONCRETE_STAIRS;
    public static final RegistryObject<Item> PINK_CONCRETE_STAIRS = ItemsPart01.PINK_CONCRETE_STAIRS;
    public static final RegistryObject<Item> PURPLE_CONCRETE_STAIRS = ItemsPart01.PURPLE_CONCRETE_STAIRS;
    public static final RegistryObject<Item> RED_CONCRETE_STAIRS = ItemsPart01.RED_CONCRETE_STAIRS;
    public static final RegistryObject<Item> WHITE_CONCRETE_STAIRS = ItemsPart01.WHITE_CONCRETE_STAIRS;
    public static final RegistryObject<Item> YELLOW_CONCRETE_STAIRS = ItemsPart01.YELLOW_CONCRETE_STAIRS;
    public static final RegistryObject<Item> WHITE_CONCRETE_WALL = ItemsPart01.WHITE_CONCRETE_WALL;
    public static final RegistryObject<Item> LIGHT_GRAY_CONCRETE_WALL = ItemsPart01.LIGHT_GRAY_CONCRETE_WALL;
    public static final RegistryObject<Item> GRAY_CONCRETE_WALL = ItemsPart01.GRAY_CONCRETE_WALL;
    public static final RegistryObject<Item> BLACK_CONCRETE_WALL = ItemsPart01.BLACK_CONCRETE_WALL;
    public static final RegistryObject<Item> BROWN_CONCRETE_WALL = ItemsPart01.BROWN_CONCRETE_WALL;
    public static final RegistryObject<Item> RED_CONCRETE_WALL = ItemsPart01.RED_CONCRETE_WALL;
    public static final RegistryObject<Item> ORANGE_CONCRETE_WALL = ItemsPart01.ORANGE_CONCRETE_WALL;
    public static final RegistryObject<Item> YELLOW_CONCRETE_WALL = ItemsPart01.YELLOW_CONCRETE_WALL;
    public static final RegistryObject<Item> LIME_CONCRETE_WALL = ItemsPart01.LIME_CONCRETE_WALL;
    public static final RegistryObject<Item> GREEN_CONCRETE_WALL = ItemsPart01.GREEN_CONCRETE_WALL;
    public static final RegistryObject<Item> CYAN_CONCRETE_WALL = ItemsPart01.CYAN_CONCRETE_WALL;
    public static final RegistryObject<Item> LIGHT_BLUE_CONCRETE_WALL = ItemsPart01.LIGHT_BLUE_CONCRETE_WALL;
    public static final RegistryObject<Item> BLUE_CONCRETE_WALL = ItemsPart01.BLUE_CONCRETE_WALL;
    public static final RegistryObject<Item> PURPLE_CONCRETE_WALL = ItemsPart01.PURPLE_CONCRETE_WALL;
    public static final RegistryObject<Item> MAGENTA_CONCRETE_WALL = ItemsPart01.MAGENTA_CONCRETE_WALL;
    public static final RegistryObject<Item> PINK_CONCRETE_WALL = ItemsPart01.PINK_CONCRETE_WALL;
    public static final RegistryObject<Item> BLACK_CONCRETE_SLAB = ItemsPart01.BLACK_CONCRETE_SLAB;
    public static final RegistryObject<Item> BLUE_CONCRETE_SLAB = ItemsPart01.BLUE_CONCRETE_SLAB;
    public static final RegistryObject<Item> BROWN_CONCRETE_SLAB = ItemsPart01.BROWN_CONCRETE_SLAB;
    public static final RegistryObject<Item> CYAN_CONCRETE_SLAB = ItemsPart01.CYAN_CONCRETE_SLAB;
    public static final RegistryObject<Item> GRAY_CONCRETE_SLAB = ItemsPart01.GRAY_CONCRETE_SLAB;
    public static final RegistryObject<Item> GREEN_CONCRETE_SLAB = ItemsPart01.GREEN_CONCRETE_SLAB;
    public static final RegistryObject<Item> LIGHT_BLUE_CONCRETE_SLAB = ItemsPart01.LIGHT_BLUE_CONCRETE_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_CONCRETE_SLAB = ItemsPart01.LIGHT_GRAY_CONCRETE_SLAB;
    public static final RegistryObject<Item> LIME_CONCRETE_SLAB = ItemsPart01.LIME_CONCRETE_SLAB;
    public static final RegistryObject<Item> MAGENTA_CONCRETE_SLAB = ItemsPart01.MAGENTA_CONCRETE_SLAB;
    public static final RegistryObject<Item> ORANGE_CONCRETE_SLAB = ItemsPart01.ORANGE_CONCRETE_SLAB;
    public static final RegistryObject<Item> PINK_CONCRETE_SLAB = ItemsPart01.PINK_CONCRETE_SLAB;
    public static final RegistryObject<Item> PURPLE_CONCRETE_SLAB = ItemsPart01.PURPLE_CONCRETE_SLAB;
    public static final RegistryObject<Item> RED_CONCRETE_SLAB = ItemsPart01.RED_CONCRETE_SLAB;
    public static final RegistryObject<Item> WHITE_CONCRETE_SLAB = ItemsPart01.WHITE_CONCRETE_SLAB;
    public static final RegistryObject<Item> YELLOW_CONCRETE_SLAB = ItemsPart01.YELLOW_CONCRETE_SLAB;
    public static final RegistryObject<Item> BROWN_MUSHROOM_SHELVES = ItemsPart01.BROWN_MUSHROOM_SHELVES;
    public static final RegistryObject<Item> RED_MUSHROOM_SHELVES = ItemsPart01.RED_MUSHROOM_SHELVES;
    public static final RegistryObject<Item> QUARTZ_PILLAR = ItemsPart01.QUARTZ_PILLAR;
    public static final RegistryObject<Item> STONE_PILLAR = ItemsPart01.STONE_PILLAR;
    public static final RegistryObject<Item> DEEPSLATE_PILLAR = ItemsPart01.DEEPSLATE_PILLAR;
    public static final RegistryObject<Item> MOSSY_PILLAR = ItemsPart01.MOSSY_PILLAR;
    public static final RegistryObject<Item> AMETHYST_PILLAR = ItemsPart01.AMETHYST_PILLAR;
    public static final RegistryObject<Item> ANDESITE_PILLAR = ItemsPart01.ANDESITE_PILLAR;
    public static final RegistryObject<Item> BASALT_PILLAR = ItemsPart01.BASALT_PILLAR;
    public static final RegistryObject<Item> BLACKSTONE_PILLAR = ItemsPart01.BLACKSTONE_PILLAR;
    public static final RegistryObject<Item> BLUE_ICE_PILLAR = ItemsPart01.BLUE_ICE_PILLAR;
    public static final RegistryObject<Item> CALCITE_PILLAR = ItemsPart01.CALCITE_PILLAR;
    public static final RegistryObject<Item> CINNABAR_PILLAR = ItemsPart01.CINNABAR_PILLAR;
    public static final RegistryObject<Item> COPPER_PILLAR = ItemsPart01.COPPER_PILLAR;
    public static final RegistryObject<Item> DARK_PRISMARINE_PILLAR = ItemsPart01.DARK_PRISMARINE_PILLAR;
    public static final RegistryObject<Item> DIORITE_PILLAR = ItemsPart01.DIORITE_PILLAR;
    public static final RegistryObject<Item> DRIPSTONE_PILLAR = ItemsPart01.DRIPSTONE_PILLAR;
    public static final RegistryObject<Item> EXPOSED_COPPER_PILLAR = ItemsPart01.EXPOSED_COPPER_PILLAR;
    public static final RegistryObject<Item> GRANITE_PILLAR = ItemsPart01.GRANITE_PILLAR;
    public static final RegistryObject<Item> NETHERRACK_PILLAR = ItemsPart01.NETHERRACK_PILLAR;
    public static final RegistryObject<Item> OBSIDIAN_PILLAR = ItemsPart01.OBSIDIAN_PILLAR;
    public static final RegistryObject<Item> OXIDIZED_COPPER_PILLAR = ItemsPart01.OXIDIZED_COPPER_PILLAR;
    public static final RegistryObject<Item> PACKED_MUD_PILLAR = ItemsPart01.PACKED_MUD_PILLAR;
    public static final RegistryObject<Item> PRISMARINE_PILLAR = ItemsPart01.PRISMARINE_PILLAR;
    public static final RegistryObject<Item> SCULK_PILLAR = ItemsPart01.SCULK_PILLAR;
    public static final RegistryObject<Item> SULFUR_PILLAR = ItemsPart01.SULFUR_PILLAR;
    public static final RegistryObject<Item> TUFF_PILLAR = ItemsPart01.TUFF_PILLAR;
    public static final RegistryObject<Item> WEATHERED_COPPER_PILLAR = ItemsPart01.WEATHERED_COPPER_PILLAR;
    public static final RegistryObject<Item> ASHENKING_DIAMOND_PILLAR = ItemsPart01.ASHENKING_DIAMOND_PILLAR;
    public static final RegistryObject<Item> ASHENKING_GOLD_PILLAR = ItemsPart01.ASHENKING_GOLD_PILLAR;
    public static final RegistryObject<Item> ASHENKING_EMERALD_PILLAR = ItemsPart01.ASHENKING_EMERALD_PILLAR;
    public static final RegistryObject<Item> ASHENKING_NETHERITE_PILLAR = ItemsPart01.ASHENKING_NETHERITE_PILLAR;
    public static final RegistryObject<Item> DECORATED_POT = ItemsPart01.DECORATED_POT;
    public static final RegistryObject<Item> BLACK_DECORATED_POT = ItemsPart01.BLACK_DECORATED_POT;
    public static final RegistryObject<Item> BLUE_DECORATED_POT = ItemsPart01.BLUE_DECORATED_POT;
    public static final RegistryObject<Item> BROWN_DECORATED_POT = ItemsPart01.BROWN_DECORATED_POT;
    public static final RegistryObject<Item> CYAN_DECORATED_POT = ItemsPart01.CYAN_DECORATED_POT;
    public static final RegistryObject<Item> GRAY_DECORATED_POT = ItemsPart01.GRAY_DECORATED_POT;
    public static final RegistryObject<Item> GREEN_DECORATED_POT = ItemsPart01.GREEN_DECORATED_POT;
    public static final RegistryObject<Item> LIGHT_BLUE_DECORATED_POT = ItemsPart01.LIGHT_BLUE_DECORATED_POT;
    public static final RegistryObject<Item> LIGHT_GRAY_DECORATED_POT = ItemsPart01.LIGHT_GRAY_DECORATED_POT;
    public static final RegistryObject<Item> LIME_DECORATED_POT = ItemsPart01.LIME_DECORATED_POT;
    public static final RegistryObject<Item> MAGENTA_DECORATED_POT = ItemsPart01.MAGENTA_DECORATED_POT;
    public static final RegistryObject<Item> ORANGE_DECORATED_POT = ItemsPart01.ORANGE_DECORATED_POT;
    public static final RegistryObject<Item> PINK_DECORATED_POT = ItemsPart01.PINK_DECORATED_POT;
    public static final RegistryObject<Item> PURPLE_DECORATED_POT = ItemsPart01.PURPLE_DECORATED_POT;
    public static final RegistryObject<Item> RED_DECORATED_POT = ItemsPart01.RED_DECORATED_POT;
    public static final RegistryObject<Item> WHITE_DECORATED_POT = ItemsPart01.WHITE_DECORATED_POT;
    public static final RegistryObject<Item> YELLOW_DECORATED_POT = ItemsPart01.YELLOW_DECORATED_POT;
    public static final RegistryObject<Item> TRAPPED_DECORATED_POT = ItemsPart01.TRAPPED_DECORATED_POT;
    public static final RegistryObject<Item> BLACK_TRAPPED_DECORATED_POT = ItemsPart01.BLACK_TRAPPED_DECORATED_POT;
    public static final RegistryObject<Item> BLUE_TRAPPED_DECORATED_POT = ItemsPart01.BLUE_TRAPPED_DECORATED_POT;
    public static final RegistryObject<Item> BROWN_TRAPPED_DECORATED_POT = ItemsPart01.BROWN_TRAPPED_DECORATED_POT;
    public static final RegistryObject<Item> CYAN_TRAPPED_DECORATED_POT = ItemsPart01.CYAN_TRAPPED_DECORATED_POT;
    public static final RegistryObject<Item> GRAY_TRAPPED_DECORATED_POT = ItemsPart01.GRAY_TRAPPED_DECORATED_POT;
    public static final RegistryObject<Item> GREEN_TRAPPED_DECORATED_POT = ItemsPart01.GREEN_TRAPPED_DECORATED_POT;
    public static final RegistryObject<Item> LIGHT_BLUE_TRAPPED_DECORATED_POT =
            ItemsPart01.LIGHT_BLUE_TRAPPED_DECORATED_POT;
    public static final RegistryObject<Item> LIGHT_GRAY_TRAPPED_DECORATED_POT =
            ItemsPart01.LIGHT_GRAY_TRAPPED_DECORATED_POT;
    public static final RegistryObject<Item> LIME_TRAPPED_DECORATED_POT = ItemsPart01.LIME_TRAPPED_DECORATED_POT;
    public static final RegistryObject<Item> MAGENTA_TRAPPED_DECORATED_POT = ItemsPart01.MAGENTA_TRAPPED_DECORATED_POT;
    public static final RegistryObject<Item> ORANGE_TRAPPED_DECORATED_POT = ItemsPart01.ORANGE_TRAPPED_DECORATED_POT;
    public static final RegistryObject<Item> PINK_TRAPPED_DECORATED_POT = ItemsPart01.PINK_TRAPPED_DECORATED_POT;
    public static final RegistryObject<Item> PURPLE_TRAPPED_DECORATED_POT = ItemsPart01.PURPLE_TRAPPED_DECORATED_POT;
    public static final RegistryObject<Item> RED_TRAPPED_DECORATED_POT = ItemsPart01.RED_TRAPPED_DECORATED_POT;
    public static final RegistryObject<Item> WHITE_TRAPPED_DECORATED_POT = ItemsPart01.WHITE_TRAPPED_DECORATED_POT;
    public static final RegistryObject<Item> YELLOW_TRAPPED_DECORATED_POT = ItemsPart01.YELLOW_TRAPPED_DECORATED_POT;
    public static final RegistryObject<Item> FESTIVE_STOCKING = ItemsPart01.FESTIVE_STOCKING;
    public static final RegistryObject<Item> BLACK_FESTIVE_STOCKING = ItemsPart01.BLACK_FESTIVE_STOCKING;
    public static final RegistryObject<Item> BLUE_FESTIVE_STOCKING = ItemsPart01.BLUE_FESTIVE_STOCKING;
    public static final RegistryObject<Item> BROWN_FESTIVE_STOCKING = ItemsPart01.BROWN_FESTIVE_STOCKING;
    public static final RegistryObject<Item> CYAN_FESTIVE_STOCKING = ItemsPart01.CYAN_FESTIVE_STOCKING;
    public static final RegistryObject<Item> GRAY_FESTIVE_STOCKING = ItemsPart01.GRAY_FESTIVE_STOCKING;
    public static final RegistryObject<Item> GREEN_FESTIVE_STOCKING = ItemsPart01.GREEN_FESTIVE_STOCKING;
    public static final RegistryObject<Item> LIGHT_BLUE_FESTIVE_STOCKING = ItemsPart01.LIGHT_BLUE_FESTIVE_STOCKING;
    public static final RegistryObject<Item> LIGHT_GRAY_FESTIVE_STOCKING = ItemsPart01.LIGHT_GRAY_FESTIVE_STOCKING;
    public static final RegistryObject<Item> LIME_FESTIVE_STOCKING = ItemsPart01.LIME_FESTIVE_STOCKING;
    public static final RegistryObject<Item> MAGENTA_FESTIVE_STOCKING = ItemsPart01.MAGENTA_FESTIVE_STOCKING;
    public static final RegistryObject<Item> ORANGE_FESTIVE_STOCKING = ItemsPart01.ORANGE_FESTIVE_STOCKING;
    public static final RegistryObject<Item> PINK_FESTIVE_STOCKING = ItemsPart01.PINK_FESTIVE_STOCKING;
    public static final RegistryObject<Item> PURPLE_FESTIVE_STOCKING = ItemsPart01.PURPLE_FESTIVE_STOCKING;
    public static final RegistryObject<Item> RED_FESTIVE_STOCKING = ItemsPart01.RED_FESTIVE_STOCKING;
    public static final RegistryObject<Item> WHITE_FESTIVE_STOCKING = ItemsPart01.WHITE_FESTIVE_STOCKING;
    public static final RegistryObject<Item> YELLOW_FESTIVE_STOCKING = ItemsPart01.YELLOW_FESTIVE_STOCKING;
    public static final RegistryObject<Item> BLACK_CARPET_LAYERS = ItemsPart01.BLACK_CARPET_LAYERS;
    public static final RegistryObject<Item> BLUE_CARPET_LAYERS = ItemsPart01.BLUE_CARPET_LAYERS;
    public static final RegistryObject<Item> BROWN_CARPET_LAYERS = ItemsPart01.BROWN_CARPET_LAYERS;
    public static final RegistryObject<Item> CYAN_CARPET_LAYERS = ItemsPart01.CYAN_CARPET_LAYERS;
    public static final RegistryObject<Item> GRAY_CARPET_LAYERS = ItemsPart01.GRAY_CARPET_LAYERS;
    public static final RegistryObject<Item> GREEN_CARPET_LAYERS = ItemsPart01.GREEN_CARPET_LAYERS;
    public static final RegistryObject<Item> LIGHT_BLUE_CARPET_LAYERS = ItemsPart01.LIGHT_BLUE_CARPET_LAYERS;
    public static final RegistryObject<Item> LIGHT_GRAY_CARPET_LAYERS = ItemsPart01.LIGHT_GRAY_CARPET_LAYERS;
    public static final RegistryObject<Item> LIME_CARPET_LAYERS = ItemsPart01.LIME_CARPET_LAYERS;
    public static final RegistryObject<Item> MAGENTA_CARPET_LAYERS = ItemsPart01.MAGENTA_CARPET_LAYERS;
    public static final RegistryObject<Item> ORANGE_CARPET_LAYERS = ItemsPart01.ORANGE_CARPET_LAYERS;
    public static final RegistryObject<Item> PINK_CARPET_LAYERS = ItemsPart01.PINK_CARPET_LAYERS;
    public static final RegistryObject<Item> PURPLE_CARPET_LAYERS = ItemsPart01.PURPLE_CARPET_LAYERS;
    public static final RegistryObject<Item> RED_CARPET_LAYERS = ItemsPart01.RED_CARPET_LAYERS;
    public static final RegistryObject<Item> WHITE_CARPET_LAYERS = ItemsPart01.WHITE_CARPET_LAYERS;
    public static final RegistryObject<Item> YELLOW_CARPET_LAYERS = ItemsPart01.YELLOW_CARPET_LAYERS;
    public static final RegistryObject<Item> OAK_LEAF_LAYERS = ItemsPart01.OAK_LEAF_LAYERS;
    public static final RegistryObject<Item> SPRUCE_LEAF_LAYERS = ItemsPart01.SPRUCE_LEAF_LAYERS;
    public static final RegistryObject<Item> BIRCH_LEAF_LAYERS = ItemsPart01.BIRCH_LEAF_LAYERS;
    public static final RegistryObject<Item> JUNGLE_LEAF_LAYERS = ItemsPart01.JUNGLE_LEAF_LAYERS;
    public static final RegistryObject<Item> ACACIA_LEAF_LAYERS = ItemsPart01.ACACIA_LEAF_LAYERS;
    public static final RegistryObject<Item> DARK_OAK_LEAF_LAYERS = ItemsPart01.DARK_OAK_LEAF_LAYERS;
    public static final RegistryObject<Item> AZALEA_LEAF_LAYERS = ItemsPart01.AZALEA_LEAF_LAYERS;
    public static final RegistryObject<Item> FLOWERING_AZALEA_LEAF_LAYERS = ItemsPart01.FLOWERING_AZALEA_LEAF_LAYERS;
    public static final RegistryObject<Item> SNOWY_OAK_LEAF_LAYERS = ItemsPart01.SNOWY_OAK_LEAF_LAYERS;
    public static final RegistryObject<Item> SNOWY_SPRUCE_LEAF_LAYERS = ItemsPart01.SNOWY_SPRUCE_LEAF_LAYERS;
    public static final RegistryObject<Item> SNOWY_BIRCH_LEAF_LAYERS = ItemsPart01.SNOWY_BIRCH_LEAF_LAYERS;
    public static final RegistryObject<Item> SNOWY_JUNGLE_LEAF_LAYERS = ItemsPart01.SNOWY_JUNGLE_LEAF_LAYERS;
    public static final RegistryObject<Item> SNOWY_ACACIA_LEAF_LAYERS = ItemsPart01.SNOWY_ACACIA_LEAF_LAYERS;
    public static final RegistryObject<Item> SNOWY_DARK_OAK_LEAF_LAYERS = ItemsPart01.SNOWY_DARK_OAK_LEAF_LAYERS;
    public static final RegistryObject<Item> SNOWY_MANGROVE_LEAF_LAYERS = ItemsPart01.SNOWY_MANGROVE_LEAF_LAYERS;
    public static final RegistryObject<Item> SNOWY_AZALEA_LEAF_LAYERS = ItemsPart01.SNOWY_AZALEA_LEAF_LAYERS;
    public static final RegistryObject<Item> SNOWY_FLOWERING_AZALEA_LEAF_LAYERS =
            ItemsPart01.SNOWY_FLOWERING_AZALEA_LEAF_LAYERS;
    public static final RegistryObject<Item> MANGROVE_LEAF_LAYERS = ItemsPart01.MANGROVE_LEAF_LAYERS;
    public static final RegistryObject<Item> ORANGE_POPLAR_LEAF_LAYERS = ItemsPart01.ORANGE_POPLAR_LEAF_LAYERS;
    public static final RegistryObject<Item> RED_POPLAR_LEAF_LAYERS = ItemsPart01.RED_POPLAR_LEAF_LAYERS;
    public static final RegistryObject<Item> YELLOW_POPLAR_LEAF_LAYERS = ItemsPart01.YELLOW_POPLAR_LEAF_LAYERS;
    public static final RegistryObject<Item> CHERRY_LEAF_LAYERS = ItemsPart01.CHERRY_LEAF_LAYERS;
    public static final RegistryObject<Item> PALE_OAK_LEAF_LAYERS = ItemsPart01.PALE_OAK_LEAF_LAYERS;
    public static final RegistryObject<Item> SNOWY_CHERRY_LEAF_LAYERS = ItemsPart01.SNOWY_CHERRY_LEAF_LAYERS;
    public static final RegistryObject<Item> SNOWY_PALE_OAK_LEAF_LAYERS = ItemsPart01.SNOWY_PALE_OAK_LEAF_LAYERS;
    public static final RegistryObject<Item> SNOWY_ORANGE_POPLAR_LEAF_LAYERS =
            ItemsPart01.SNOWY_ORANGE_POPLAR_LEAF_LAYERS;
    public static final RegistryObject<Item> SNOWY_RED_POPLAR_LEAF_LAYERS = ItemsPart01.SNOWY_RED_POPLAR_LEAF_LAYERS;
    public static final RegistryObject<Item> SNOWY_YELLOW_POPLAR_LEAF_LAYERS =
            ItemsPart01.SNOWY_YELLOW_POPLAR_LEAF_LAYERS;
    public static final RegistryObject<Item> OAK_LEAF_HEDGE = ItemsPart01.OAK_LEAF_HEDGE;
    public static final RegistryObject<Item> SPRUCE_LEAF_HEDGE = ItemsPart01.SPRUCE_LEAF_HEDGE;
    public static final RegistryObject<Item> BIRCH_LEAF_HEDGE = ItemsPart01.BIRCH_LEAF_HEDGE;
    public static final RegistryObject<Item> JUNGLE_LEAF_HEDGE = ItemsPart01.JUNGLE_LEAF_HEDGE;
    public static final RegistryObject<Item> ACACIA_LEAF_HEDGE = ItemsPart01.ACACIA_LEAF_HEDGE;
    public static final RegistryObject<Item> DARK_OAK_LEAF_HEDGE = ItemsPart01.DARK_OAK_LEAF_HEDGE;
    public static final RegistryObject<Item> AZALEA_LEAF_HEDGE = ItemsPart01.AZALEA_LEAF_HEDGE;
    public static final RegistryObject<Item> FLOWERING_AZALEA_LEAF_HEDGE = ItemsPart01.FLOWERING_AZALEA_LEAF_HEDGE;
    public static final RegistryObject<Item> SNOWY_OAK_LEAF_HEDGE = ItemsPart01.SNOWY_OAK_LEAF_HEDGE;
    public static final RegistryObject<Item> SNOWY_SPRUCE_LEAF_HEDGE = ItemsPart01.SNOWY_SPRUCE_LEAF_HEDGE;
    public static final RegistryObject<Item> SNOWY_BIRCH_LEAF_HEDGE = ItemsPart01.SNOWY_BIRCH_LEAF_HEDGE;
    public static final RegistryObject<Item> SNOWY_JUNGLE_LEAF_HEDGE = ItemsPart01.SNOWY_JUNGLE_LEAF_HEDGE;
    public static final RegistryObject<Item> SNOWY_ACACIA_LEAF_HEDGE = ItemsPart01.SNOWY_ACACIA_LEAF_HEDGE;
    public static final RegistryObject<Item> SNOWY_DARK_OAK_LEAF_HEDGE = ItemsPart01.SNOWY_DARK_OAK_LEAF_HEDGE;
    public static final RegistryObject<Item> SNOWY_MANGROVE_LEAF_HEDGE = ItemsPart01.SNOWY_MANGROVE_LEAF_HEDGE;
    public static final RegistryObject<Item> SNOWY_AZALEA_LEAF_HEDGE = ItemsPart01.SNOWY_AZALEA_LEAF_HEDGE;
    public static final RegistryObject<Item> SNOWY_FLOWERING_AZALEA_LEAF_HEDGE =
            ItemsPart01.SNOWY_FLOWERING_AZALEA_LEAF_HEDGE;
    public static final RegistryObject<Item> MANGROVE_LEAF_HEDGE = ItemsPart01.MANGROVE_LEAF_HEDGE;
    public static final RegistryObject<Item> ORANGE_POPLAR_LEAF_HEDGE = ItemsPart01.ORANGE_POPLAR_LEAF_HEDGE;
    public static final RegistryObject<Item> RED_POPLAR_LEAF_HEDGE = ItemsPart01.RED_POPLAR_LEAF_HEDGE;
    public static final RegistryObject<Item> YELLOW_POPLAR_LEAF_HEDGE = ItemsPart01.YELLOW_POPLAR_LEAF_HEDGE;
    public static final RegistryObject<Item> CHERRY_LEAF_HEDGE = ItemsPart01.CHERRY_LEAF_HEDGE;
    public static final RegistryObject<Item> PALE_OAK_LEAF_HEDGE = ItemsPart01.PALE_OAK_LEAF_HEDGE;
    public static final RegistryObject<Item> SNOWY_CHERRY_LEAF_HEDGE = ItemsPart01.SNOWY_CHERRY_LEAF_HEDGE;
    public static final RegistryObject<Item> SNOWY_PALE_OAK_LEAF_HEDGE = ItemsPart01.SNOWY_PALE_OAK_LEAF_HEDGE;
    public static final RegistryObject<Item> SNOWY_ORANGE_POPLAR_LEAF_HEDGE =
            ItemsPart01.SNOWY_ORANGE_POPLAR_LEAF_HEDGE;
    public static final RegistryObject<Item> SNOWY_RED_POPLAR_LEAF_HEDGE = ItemsPart01.SNOWY_RED_POPLAR_LEAF_HEDGE;
    public static final RegistryObject<Item> SNOWY_YELLOW_POPLAR_LEAF_HEDGE =
            ItemsPart01.SNOWY_YELLOW_POPLAR_LEAF_HEDGE;
    public static final RegistryObject<Item> HAY_BALE_SLAB = ItemsPart01.HAY_BALE_SLAB;
    public static final RegistryObject<Item> HAY_BALE_STAIRS = ItemsPart01.HAY_BALE_STAIRS;
    public static final RegistryObject<Item> BAMBOO_BLOCK = ItemsPart01.BAMBOO_BLOCK;
    public static final RegistryObject<Item> STRIPPED_BAMBOO_BLOCK = ItemsPart01.STRIPPED_BAMBOO_BLOCK;
    public static final RegistryObject<Item> BAMBOO_WOOD = ItemsPart01.BAMBOO_WOOD;
    public static final RegistryObject<Item> STRIPPED_BAMBOO_WOOD = ItemsPart01.STRIPPED_BAMBOO_WOOD;
    public static final RegistryObject<Item> BAMBOO_BLOCK_SLAB = ItemsPart01.BAMBOO_BLOCK_SLAB;
    public static final RegistryObject<Item> BAMBOO_BLOCK_STAIRS = ItemsPart01.BAMBOO_BLOCK_STAIRS;
    public static final RegistryObject<Item> BAMBOO_BLOCK_FENCE = ItemsPart01.BAMBOO_BLOCK_FENCE;
    public static final RegistryObject<Item> BAMBOO_BLOCK_FENCE_GATE = ItemsPart01.BAMBOO_BLOCK_FENCE_GATE;
    public static final RegistryObject<Item> BAMBOO_BLOCK_PRESSURE_PLATE = ItemsPart01.BAMBOO_BLOCK_PRESSURE_PLATE;
    public static final RegistryObject<Item> BAMBOO_BLOCK_BUTTON = ItemsPart01.BAMBOO_BLOCK_BUTTON;
    public static final RegistryObject<Item> STRIPPED_BAMBOO_BLOCK_SLAB = ItemsPart01.STRIPPED_BAMBOO_BLOCK_SLAB;
    public static final RegistryObject<Item> STRIPPED_BAMBOO_BLOCK_STAIRS = ItemsPart01.STRIPPED_BAMBOO_BLOCK_STAIRS;
    public static final RegistryObject<Item> STRIPPED_BAMBOO_BLOCK_FENCE = ItemsPart01.STRIPPED_BAMBOO_BLOCK_FENCE;
    public static final RegistryObject<Item> STRIPPED_BAMBOO_BLOCK_FENCE_GATE =
            ItemsPart01.STRIPPED_BAMBOO_BLOCK_FENCE_GATE;
    public static final RegistryObject<
            Item
            > STRIPPED_BAMBOO_BLOCK_PRESSURE_PLATE = ItemsPart01.STRIPPED_BAMBOO_BLOCK_PRESSURE_PLATE;
    public static final RegistryObject<Item> STRIPPED_BAMBOO_BLOCK_BUTTON = ItemsPart01.STRIPPED_BAMBOO_BLOCK_BUTTON;
    public static final RegistryObject<Item> BAMBOO_DOOR = ItemsPart01.BAMBOO_DOOR;
    public static final RegistryObject<Item> BAMBOO_TRAPDOOR = ItemsPart01.BAMBOO_TRAPDOOR;
    public static final RegistryObject<Item> DIAMOND_CHAIN = ItemsPart01.DIAMOND_CHAIN;
    public static final RegistryObject<Item> GOLD_CHAIN = ItemsPart01.GOLD_CHAIN;
    public static final RegistryObject<Item> EMERALD_CHAIN = ItemsPart01.EMERALD_CHAIN;
    public static final RegistryObject<Item> ANCIENT_STEEL_CHAIN = ItemsPart01.ANCIENT_STEEL_CHAIN;
    public static final RegistryObject<Item> NETHERITE_CHAIN = ItemsPart01.NETHERITE_CHAIN;
    public static final RegistryObject<Item> COPPER_CHAIN = ItemsPart01.COPPER_CHAIN;
    public static final RegistryObject<Item> EXPOSED_COPPER_CHAIN = ItemsPart01.EXPOSED_COPPER_CHAIN;
    public static final RegistryObject<Item> WEATHERED_COPPER_CHAIN = ItemsPart01.WEATHERED_COPPER_CHAIN;
    public static final RegistryObject<Item> OXIDIZED_COPPER_CHAIN = ItemsPart01.OXIDIZED_COPPER_CHAIN;
    public static final RegistryObject<Item> LARGE_IRON_CHAIN = ItemsPart01.LARGE_IRON_CHAIN;
    public static final RegistryObject<Item> LARGE_COPPER_CHAIN = ItemsPart01.LARGE_COPPER_CHAIN;
    public static final RegistryObject<Item> LARGE_EXPOSED_COPPER_CHAIN = ItemsPart01.LARGE_EXPOSED_COPPER_CHAIN;
    public static final RegistryObject<Item> LARGE_WEATHERED_COPPER_CHAIN = ItemsPart01.LARGE_WEATHERED_COPPER_CHAIN;
    public static final RegistryObject<Item> LARGE_OXIDIZED_COPPER_CHAIN = ItemsPart01.LARGE_OXIDIZED_COPPER_CHAIN;
    public static final RegistryObject<Item> LARGE_GOLD_CHAIN = ItemsPart01.LARGE_GOLD_CHAIN;
    public static final RegistryObject<Item> LARGE_DIAMOND_CHAIN = ItemsPart01.LARGE_DIAMOND_CHAIN;
    public static final RegistryObject<Item> LARGE_EMERALD_CHAIN = ItemsPart01.LARGE_EMERALD_CHAIN;
    public static final RegistryObject<Item> LARGE_ANCIENT_STEEL_CHAIN = ItemsPart01.LARGE_ANCIENT_STEEL_CHAIN;
    public static final RegistryObject<Item> LARGE_NETHERITE_CHAIN = ItemsPart01.LARGE_NETHERITE_CHAIN;
    public static final RegistryObject<Item> OAK_WOOD_WALL = ItemsPart01.OAK_WOOD_WALL;
    public static final RegistryObject<Item> SPRUCE_WOOD_WALL = ItemsPart01.SPRUCE_WOOD_WALL;
    public static final RegistryObject<Item> BIRCH_WOOD_WALL = ItemsPart01.BIRCH_WOOD_WALL;
    public static final RegistryObject<Item> DARK_OAK_WOOD_WALL = ItemsPart01.DARK_OAK_WOOD_WALL;
    public static final RegistryObject<Item> JUNGLE_WOOD_WALL = ItemsPart01.JUNGLE_WOOD_WALL;
    public static final RegistryObject<Item> ACACIA_WOOD_WALL = ItemsPart01.ACACIA_WOOD_WALL;
    public static final RegistryObject<Item> BAMBOO_BLOCK_WALL = ItemsPart01.BAMBOO_BLOCK_WALL;
    public static final RegistryObject<Item> MANGROVE_WOOD_WALL = ItemsPart01.MANGROVE_WOOD_WALL;
    public static final RegistryObject<Item> STRIPPED_OAK_WOOD_WALL = ItemsPart01.STRIPPED_OAK_WOOD_WALL;
    public static final RegistryObject<Item> STRIPPED_SPRUCE_WOOD_WALL = ItemsPart01.STRIPPED_SPRUCE_WOOD_WALL;
    public static final RegistryObject<Item> STRIPPED_BIRCH_WOOD_WALL = ItemsPart01.STRIPPED_BIRCH_WOOD_WALL;
    public static final RegistryObject<Item> STRIPPED_DARK_OAK_WOOD_WALL = ItemsPart01.STRIPPED_DARK_OAK_WOOD_WALL;
    public static final RegistryObject<Item> STRIPPED_JUNGLE_WOOD_WALL = ItemsPart01.STRIPPED_JUNGLE_WOOD_WALL;
    public static final RegistryObject<Item> STRIPPED_ACACIA_WOOD_WALL = ItemsPart01.STRIPPED_ACACIA_WOOD_WALL;
    public static final RegistryObject<Item> STRIPPED_BAMBOO_BLOCK_WALL = ItemsPart01.STRIPPED_BAMBOO_BLOCK_WALL;
    public static final RegistryObject<Item> STRIPPED_MANGROVE_WOOD_WALL = ItemsPart01.STRIPPED_MANGROVE_WOOD_WALL;
    public static final RegistryObject<Item> CHERRY_WOOD_WALL = ItemsPart01.CHERRY_WOOD_WALL;
    public static final RegistryObject<Item> STRIPPED_CHERRY_WOOD_WALL = ItemsPart01.STRIPPED_CHERRY_WOOD_WALL;
    public static final RegistryObject<Item> PALE_OAK_WOOD_WALL = ItemsPart01.PALE_OAK_WOOD_WALL;
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_WOOD_WALL = ItemsPart01.STRIPPED_PALE_OAK_WOOD_WALL;
    public static final RegistryObject<Item> ASHPEN_WOOD_WALL = ItemsPart01.ASHPEN_WOOD_WALL;
    public static final RegistryObject<Item> STRIPPED_ASHPEN_WOOD_WALL = ItemsPart01.STRIPPED_ASHPEN_WOOD_WALL;
    public static final RegistryObject<Item> ASHPEN_WHITE_PLANKS = ItemsPart01.ASHPEN_WHITE_PLANKS;
    public static final RegistryObject<Item> ASHPEN_WHITE_STAIRS = ItemsPart01.ASHPEN_WHITE_STAIRS;
    public static final RegistryObject<Item> ASHPEN_WHITE_SLAB = ItemsPart01.ASHPEN_WHITE_SLAB;
    public static final RegistryObject<Item> ASHPEN_WHITE_FENCE = ItemsPart01.ASHPEN_WHITE_FENCE;
    public static final RegistryObject<Item> ASHPEN_WHITE_FENCE_GATE = ItemsPart01.ASHPEN_WHITE_FENCE_GATE;
    public static final RegistryObject<Item> ASHPEN_WHITE_PRESSURE_PLATE = ItemsPart01.ASHPEN_WHITE_PRESSURE_PLATE;
    public static final RegistryObject<Item> ASHPEN_WHITE_BUTTON = ItemsPart01.ASHPEN_WHITE_BUTTON;
    public static final RegistryObject<Item> BLACK_ASHPEN_PLANKS = ItemsPart01.BLACK_ASHPEN_PLANKS;
    public static final RegistryObject<Item> BLACK_ASHPEN_STAIRS = ItemsPart01.BLACK_ASHPEN_STAIRS;
    public static final RegistryObject<Item> BLACK_ASHPEN_SLAB = ItemsPart01.BLACK_ASHPEN_SLAB;
    public static final RegistryObject<Item> BLACK_ASHPEN_FENCE = ItemsPart01.BLACK_ASHPEN_FENCE;
    public static final RegistryObject<Item> BLACK_ASHPEN_FENCE_GATE = ItemsPart01.BLACK_ASHPEN_FENCE_GATE;
    public static final RegistryObject<Item> BLACK_ASHPEN_PRESSURE_PLATE = ItemsPart01.BLACK_ASHPEN_PRESSURE_PLATE;
    public static final RegistryObject<Item> BLACK_ASHPEN_BUTTON = ItemsPart01.BLACK_ASHPEN_BUTTON;
    public static final RegistryObject<Item> BLUE_ASHPEN_PLANKS = ItemsPart01.BLUE_ASHPEN_PLANKS;
    public static final RegistryObject<Item> BLUE_ASHPEN_STAIRS = ItemsPart01.BLUE_ASHPEN_STAIRS;
    public static final RegistryObject<Item> BLUE_ASHPEN_SLAB = ItemsPart01.BLUE_ASHPEN_SLAB;
    public static final RegistryObject<Item> BLUE_ASHPEN_FENCE = ItemsPart01.BLUE_ASHPEN_FENCE;
    public static final RegistryObject<Item> BLUE_ASHPEN_FENCE_GATE = ItemsPart01.BLUE_ASHPEN_FENCE_GATE;
    public static final RegistryObject<Item> BLUE_ASHPEN_PRESSURE_PLATE = ItemsPart01.BLUE_ASHPEN_PRESSURE_PLATE;
    public static final RegistryObject<Item> BLUE_ASHPEN_BUTTON = ItemsPart01.BLUE_ASHPEN_BUTTON;
    public static final RegistryObject<Item> BROWN_ASHPEN_PLANKS = ItemsPart01.BROWN_ASHPEN_PLANKS;
    public static final RegistryObject<Item> BROWN_ASHPEN_STAIRS = ItemsPart01.BROWN_ASHPEN_STAIRS;
    public static final RegistryObject<Item> BROWN_ASHPEN_SLAB = ItemsPart01.BROWN_ASHPEN_SLAB;
    public static final RegistryObject<Item> BROWN_ASHPEN_FENCE = ItemsPart01.BROWN_ASHPEN_FENCE;
    public static final RegistryObject<Item> BROWN_ASHPEN_FENCE_GATE = ItemsPart01.BROWN_ASHPEN_FENCE_GATE;
    public static final RegistryObject<Item> BROWN_ASHPEN_PRESSURE_PLATE = ItemsPart01.BROWN_ASHPEN_PRESSURE_PLATE;
    public static final RegistryObject<Item> BROWN_ASHPEN_BUTTON = ItemsPart01.BROWN_ASHPEN_BUTTON;
    public static final RegistryObject<Item> CYAN_ASHPEN_PLANKS = ItemsPart01.CYAN_ASHPEN_PLANKS;
    public static final RegistryObject<Item> CYAN_ASHPEN_STAIRS = ItemsPart01.CYAN_ASHPEN_STAIRS;
    public static final RegistryObject<Item> CYAN_ASHPEN_SLAB = ItemsPart01.CYAN_ASHPEN_SLAB;
    public static final RegistryObject<Item> CYAN_ASHPEN_FENCE = ItemsPart01.CYAN_ASHPEN_FENCE;
    public static final RegistryObject<Item> CYAN_ASHPEN_FENCE_GATE = ItemsPart01.CYAN_ASHPEN_FENCE_GATE;
    public static final RegistryObject<Item> CYAN_ASHPEN_PRESSURE_PLATE = ItemsPart01.CYAN_ASHPEN_PRESSURE_PLATE;
    public static final RegistryObject<Item> CYAN_ASHPEN_BUTTON = ItemsPart01.CYAN_ASHPEN_BUTTON;
    public static final RegistryObject<Item> GRAY_ASHPEN_PLANKS = ItemsPart01.GRAY_ASHPEN_PLANKS;
    public static final RegistryObject<Item> GRAY_ASHPEN_STAIRS = ItemsPart01.GRAY_ASHPEN_STAIRS;
    public static final RegistryObject<Item> GRAY_ASHPEN_SLAB = ItemsPart01.GRAY_ASHPEN_SLAB;
    public static final RegistryObject<Item> GRAY_ASHPEN_FENCE = ItemsPart01.GRAY_ASHPEN_FENCE;
    public static final RegistryObject<Item> GRAY_ASHPEN_FENCE_GATE = ItemsPart01.GRAY_ASHPEN_FENCE_GATE;
    public static final RegistryObject<Item> GRAY_ASHPEN_PRESSURE_PLATE = ItemsPart01.GRAY_ASHPEN_PRESSURE_PLATE;
    public static final RegistryObject<Item> GRAY_ASHPEN_BUTTON = ItemsPart01.GRAY_ASHPEN_BUTTON;
    public static final RegistryObject<Item> GREEN_ASHPEN_PLANKS = ItemsPart01.GREEN_ASHPEN_PLANKS;
    public static final RegistryObject<Item> GREEN_ASHPEN_STAIRS = ItemsPart01.GREEN_ASHPEN_STAIRS;
    public static final RegistryObject<Item> GREEN_ASHPEN_SLAB = ItemsPart01.GREEN_ASHPEN_SLAB;
    public static final RegistryObject<Item> GREEN_ASHPEN_FENCE = ItemsPart01.GREEN_ASHPEN_FENCE;
    public static final RegistryObject<Item> GREEN_ASHPEN_FENCE_GATE = ItemsPart01.GREEN_ASHPEN_FENCE_GATE;
    public static final RegistryObject<Item> GREEN_ASHPEN_PRESSURE_PLATE = ItemsPart01.GREEN_ASHPEN_PRESSURE_PLATE;
    public static final RegistryObject<Item> GREEN_ASHPEN_BUTTON = ItemsPart01.GREEN_ASHPEN_BUTTON;
    public static final RegistryObject<Item> LIGHT_BLUE_ASHPEN_PLANKS = ItemsPart01.LIGHT_BLUE_ASHPEN_PLANKS;
    public static final RegistryObject<Item> LIGHT_BLUE_ASHPEN_STAIRS = ItemsPart01.LIGHT_BLUE_ASHPEN_STAIRS;
    public static final RegistryObject<Item> LIGHT_BLUE_ASHPEN_SLAB = ItemsPart01.LIGHT_BLUE_ASHPEN_SLAB;
    public static final RegistryObject<Item> LIGHT_BLUE_ASHPEN_FENCE = ItemsPart01.LIGHT_BLUE_ASHPEN_FENCE;
    public static final RegistryObject<Item> LIGHT_BLUE_ASHPEN_FENCE_GATE = ItemsPart01.LIGHT_BLUE_ASHPEN_FENCE_GATE;
    public static final RegistryObject<Item> LIGHT_BLUE_ASHPEN_PRESSURE_PLATE =
            ItemsPart01.LIGHT_BLUE_ASHPEN_PRESSURE_PLATE;
    public static final RegistryObject<Item> LIGHT_BLUE_ASHPEN_BUTTON = ItemsPart01.LIGHT_BLUE_ASHPEN_BUTTON;
    public static final RegistryObject<Item> LIGHT_GRAY_ASHPEN_PLANKS = ItemsPart01.LIGHT_GRAY_ASHPEN_PLANKS;
    public static final RegistryObject<Item> LIGHT_GRAY_ASHPEN_STAIRS = ItemsPart01.LIGHT_GRAY_ASHPEN_STAIRS;
    public static final RegistryObject<Item> LIGHT_GRAY_ASHPEN_SLAB = ItemsPart01.LIGHT_GRAY_ASHPEN_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_ASHPEN_FENCE = ItemsPart01.LIGHT_GRAY_ASHPEN_FENCE;
    public static final RegistryObject<Item> LIGHT_GRAY_ASHPEN_FENCE_GATE = ItemsPart01.LIGHT_GRAY_ASHPEN_FENCE_GATE;
    public static final RegistryObject<Item> LIGHT_GRAY_ASHPEN_PRESSURE_PLATE =
            ItemsPart01.LIGHT_GRAY_ASHPEN_PRESSURE_PLATE;
    public static final RegistryObject<Item> LIGHT_GRAY_ASHPEN_BUTTON = ItemsPart01.LIGHT_GRAY_ASHPEN_BUTTON;
    public static final RegistryObject<Item> LIME_ASHPEN_PLANKS = ItemsPart02.LIME_ASHPEN_PLANKS;
    public static final RegistryObject<Item> LIME_ASHPEN_STAIRS = ItemsPart02.LIME_ASHPEN_STAIRS;
    public static final RegistryObject<Item> LIME_ASHPEN_SLAB = ItemsPart02.LIME_ASHPEN_SLAB;
    public static final RegistryObject<Item> LIME_ASHPEN_FENCE = ItemsPart02.LIME_ASHPEN_FENCE;
    public static final RegistryObject<Item> LIME_ASHPEN_FENCE_GATE = ItemsPart02.LIME_ASHPEN_FENCE_GATE;
    public static final RegistryObject<Item> LIME_ASHPEN_PRESSURE_PLATE = ItemsPart02.LIME_ASHPEN_PRESSURE_PLATE;
    public static final RegistryObject<Item> LIME_ASHPEN_BUTTON = ItemsPart02.LIME_ASHPEN_BUTTON;
    public static final RegistryObject<Item> MAGENTA_ASHPEN_PLANKS = ItemsPart02.MAGENTA_ASHPEN_PLANKS;
    public static final RegistryObject<Item> MAGENTA_ASHPEN_STAIRS = ItemsPart02.MAGENTA_ASHPEN_STAIRS;
    public static final RegistryObject<Item> MAGENTA_ASHPEN_SLAB = ItemsPart02.MAGENTA_ASHPEN_SLAB;
    public static final RegistryObject<Item> MAGENTA_ASHPEN_FENCE = ItemsPart02.MAGENTA_ASHPEN_FENCE;
    public static final RegistryObject<Item> MAGENTA_ASHPEN_FENCE_GATE = ItemsPart02.MAGENTA_ASHPEN_FENCE_GATE;
    public static final RegistryObject<Item> MAGENTA_ASHPEN_PRESSURE_PLATE = ItemsPart02.MAGENTA_ASHPEN_PRESSURE_PLATE;
    public static final RegistryObject<Item> MAGENTA_ASHPEN_BUTTON = ItemsPart02.MAGENTA_ASHPEN_BUTTON;
    public static final RegistryObject<Item> ORANGE_ASHPEN_PLANKS = ItemsPart02.ORANGE_ASHPEN_PLANKS;
    public static final RegistryObject<Item> ORANGE_ASHPEN_STAIRS = ItemsPart02.ORANGE_ASHPEN_STAIRS;
    public static final RegistryObject<Item> ORANGE_ASHPEN_SLAB = ItemsPart02.ORANGE_ASHPEN_SLAB;
    public static final RegistryObject<Item> ORANGE_ASHPEN_FENCE = ItemsPart02.ORANGE_ASHPEN_FENCE;
    public static final RegistryObject<Item> ORANGE_ASHPEN_FENCE_GATE = ItemsPart02.ORANGE_ASHPEN_FENCE_GATE;
    public static final RegistryObject<Item> ORANGE_ASHPEN_PRESSURE_PLATE = ItemsPart02.ORANGE_ASHPEN_PRESSURE_PLATE;
    public static final RegistryObject<Item> ORANGE_ASHPEN_BUTTON = ItemsPart02.ORANGE_ASHPEN_BUTTON;
    public static final RegistryObject<Item> PINK_ASHPEN_PLANKS = ItemsPart02.PINK_ASHPEN_PLANKS;
    public static final RegistryObject<Item> PINK_ASHPEN_STAIRS = ItemsPart02.PINK_ASHPEN_STAIRS;
    public static final RegistryObject<Item> PINK_ASHPEN_SLAB = ItemsPart02.PINK_ASHPEN_SLAB;
    public static final RegistryObject<Item> PINK_ASHPEN_FENCE = ItemsPart02.PINK_ASHPEN_FENCE;
    public static final RegistryObject<Item> PINK_ASHPEN_FENCE_GATE = ItemsPart02.PINK_ASHPEN_FENCE_GATE;
    public static final RegistryObject<Item> PINK_ASHPEN_PRESSURE_PLATE = ItemsPart02.PINK_ASHPEN_PRESSURE_PLATE;
    public static final RegistryObject<Item> PINK_ASHPEN_BUTTON = ItemsPart02.PINK_ASHPEN_BUTTON;
    public static final RegistryObject<Item> PURPLE_ASHPEN_PLANKS = ItemsPart02.PURPLE_ASHPEN_PLANKS;
    public static final RegistryObject<Item> PURPLE_ASHPEN_STAIRS = ItemsPart02.PURPLE_ASHPEN_STAIRS;
    public static final RegistryObject<Item> PURPLE_ASHPEN_SLAB = ItemsPart02.PURPLE_ASHPEN_SLAB;
    public static final RegistryObject<Item> PURPLE_ASHPEN_FENCE = ItemsPart02.PURPLE_ASHPEN_FENCE;
    public static final RegistryObject<Item> PURPLE_ASHPEN_FENCE_GATE = ItemsPart02.PURPLE_ASHPEN_FENCE_GATE;
    public static final RegistryObject<Item> PURPLE_ASHPEN_PRESSURE_PLATE = ItemsPart02.PURPLE_ASHPEN_PRESSURE_PLATE;
    public static final RegistryObject<Item> PURPLE_ASHPEN_BUTTON = ItemsPart02.PURPLE_ASHPEN_BUTTON;
    public static final RegistryObject<Item> RED_ASHPEN_PLANKS = ItemsPart02.RED_ASHPEN_PLANKS;
    public static final RegistryObject<Item> RED_ASHPEN_STAIRS = ItemsPart02.RED_ASHPEN_STAIRS;
    public static final RegistryObject<Item> RED_ASHPEN_SLAB = ItemsPart02.RED_ASHPEN_SLAB;
    public static final RegistryObject<Item> RED_ASHPEN_FENCE = ItemsPart02.RED_ASHPEN_FENCE;
    public static final RegistryObject<Item> RED_ASHPEN_FENCE_GATE = ItemsPart02.RED_ASHPEN_FENCE_GATE;
    public static final RegistryObject<Item> RED_ASHPEN_PRESSURE_PLATE = ItemsPart02.RED_ASHPEN_PRESSURE_PLATE;
    public static final RegistryObject<Item> RED_ASHPEN_BUTTON = ItemsPart02.RED_ASHPEN_BUTTON;
    public static final RegistryObject<Item> YELLOW_ASHPEN_PLANKS = ItemsPart02.YELLOW_ASHPEN_PLANKS;
    public static final RegistryObject<Item> YELLOW_ASHPEN_STAIRS = ItemsPart02.YELLOW_ASHPEN_STAIRS;
    public static final RegistryObject<Item> YELLOW_ASHPEN_SLAB = ItemsPart02.YELLOW_ASHPEN_SLAB;
    public static final RegistryObject<Item> YELLOW_ASHPEN_FENCE = ItemsPart02.YELLOW_ASHPEN_FENCE;
    public static final RegistryObject<Item> YELLOW_ASHPEN_FENCE_GATE = ItemsPart02.YELLOW_ASHPEN_FENCE_GATE;
    public static final RegistryObject<Item> YELLOW_ASHPEN_PRESSURE_PLATE = ItemsPart02.YELLOW_ASHPEN_PRESSURE_PLATE;
    public static final RegistryObject<Item> YELLOW_ASHPEN_BUTTON = ItemsPart02.YELLOW_ASHPEN_BUTTON;
    public static final RegistryObject<Item> RED_ROSE_VINES = ItemsPart02.RED_ROSE_VINES;
    public static final RegistryObject<Item> BLACK_ROSE_VINES = ItemsPart02.BLACK_ROSE_VINES;
    public static final RegistryObject<Item> BLUE_ROSE_VINES = ItemsPart02.BLUE_ROSE_VINES;
    public static final RegistryObject<Item> WHITE_ROSE_VINES = ItemsPart02.WHITE_ROSE_VINES;
    public static final RegistryObject<Item> RED_MONETS = ItemsPart02.RED_MONETS;
    public static final RegistryObject<Item> BLUE_MONETS = ItemsPart02.BLUE_MONETS;
    public static final RegistryObject<Item> PURPLE_MONETS = ItemsPart02.PURPLE_MONETS;
    public static final RegistryObject<Item> LIGHT_BLUE_MONETS = ItemsPart02.LIGHT_BLUE_MONETS;
    public static final RegistryObject<Item> PINK_MONETS = ItemsPart02.PINK_MONETS;
    public static final RegistryObject<Item> YELLOW_MONETS = ItemsPart02.YELLOW_MONETS;
    public static final RegistryObject<Item> CLOVER = ItemsPart02.CLOVER;
    public static final RegistryObject<Item> RED_PETAL = ItemsPart02.RED_PETAL;
    public static final RegistryObject<Item> BLUE_PETAL = ItemsPart02.BLUE_PETAL;
    public static final RegistryObject<Item> ORANGE_PETAL = ItemsPart02.ORANGE_PETAL;
    public static final RegistryObject<Item> PINK_PETAL = ItemsPart02.PINK_PETAL;
    public static final RegistryObject<Item> PURPLE_PETAL = ItemsPart02.PURPLE_PETAL;
    public static final RegistryObject<Item> RED_SPORE_BLOSSOM = ItemsPart02.RED_SPORE_BLOSSOM;
    public static final RegistryObject<Item> CYAN_SPORE_BLOSSOM = ItemsPart02.CYAN_SPORE_BLOSSOM;
    public static final RegistryObject<Item> BLUE_SPORE_BLOSSOM = ItemsPart02.BLUE_SPORE_BLOSSOM;
    public static final RegistryObject<Item> PURPLE_SPORE_BLOSSOM = ItemsPart02.PURPLE_SPORE_BLOSSOM;
    public static final RegistryObject<Item> ORANGE_SPORE_BLOSSOM = ItemsPart02.ORANGE_SPORE_BLOSSOM;
    public static final RegistryObject<Item> BIG_CANDLE = ItemsPart02.BIG_CANDLE;
    public static final RegistryObject<Item> BIG_WHITE_CANDLE = ItemsPart02.BIG_WHITE_CANDLE;
    public static final RegistryObject<Item> BIG_ORANGE_CANDLE = ItemsPart02.BIG_ORANGE_CANDLE;
    public static final RegistryObject<Item> BIG_MAGENTA_CANDLE = ItemsPart02.BIG_MAGENTA_CANDLE;
    public static final RegistryObject<Item> BIG_LIGHT_BLUE_CANDLE = ItemsPart02.BIG_LIGHT_BLUE_CANDLE;
    public static final RegistryObject<Item> BIG_YELLOW_CANDLE = ItemsPart02.BIG_YELLOW_CANDLE;
    public static final RegistryObject<Item> BIG_LIME_CANDLE = ItemsPart02.BIG_LIME_CANDLE;
    public static final RegistryObject<Item> BIG_PINK_CANDLE = ItemsPart02.BIG_PINK_CANDLE;
    public static final RegistryObject<Item> BIG_GRAY_CANDLE = ItemsPart02.BIG_GRAY_CANDLE;
    public static final RegistryObject<Item> BIG_LIGHT_GRAY_CANDLE = ItemsPart02.BIG_LIGHT_GRAY_CANDLE;
    public static final RegistryObject<Item> BIG_CYAN_CANDLE = ItemsPart02.BIG_CYAN_CANDLE;
    public static final RegistryObject<Item> BIG_PURPLE_CANDLE = ItemsPart02.BIG_PURPLE_CANDLE;
    public static final RegistryObject<Item> BIG_BLUE_CANDLE = ItemsPart02.BIG_BLUE_CANDLE;
    public static final RegistryObject<Item> BIG_BROWN_CANDLE = ItemsPart02.BIG_BROWN_CANDLE;
    public static final RegistryObject<Item> BIG_GREEN_CANDLE = ItemsPart02.BIG_GREEN_CANDLE;
    public static final RegistryObject<Item> BIG_RED_CANDLE = ItemsPart02.BIG_RED_CANDLE;
    public static final RegistryObject<Item> BIG_BLACK_CANDLE = ItemsPart02.BIG_BLACK_CANDLE;
    public static final RegistryObject<Item> BIG_AMETHYST_CANDLE = ItemsPart02.BIG_AMETHYST_CANDLE;
    public static final RegistryObject<Item> BIG_SCULK_CANDLE = ItemsPart02.BIG_SCULK_CANDLE;
    public static final RegistryObject<Item> WHITE_ORNAMENT = ItemsPart02.WHITE_ORNAMENT;
    public static final RegistryObject<Item> ORANGE_ORNAMENT = ItemsPart02.ORANGE_ORNAMENT;
    public static final RegistryObject<Item> MAGENTA_ORNAMENT = ItemsPart02.MAGENTA_ORNAMENT;
    public static final RegistryObject<Item> LIGHT_BLUE_ORNAMENT = ItemsPart02.LIGHT_BLUE_ORNAMENT;
    public static final RegistryObject<Item> YELLOW_ORNAMENT = ItemsPart02.YELLOW_ORNAMENT;
    public static final RegistryObject<Item> LIME_ORNAMENT = ItemsPart02.LIME_ORNAMENT;
    public static final RegistryObject<Item> PINK_ORNAMENT = ItemsPart02.PINK_ORNAMENT;
    public static final RegistryObject<Item> GRAY_ORNAMENT = ItemsPart02.GRAY_ORNAMENT;
    public static final RegistryObject<Item> LIGHT_GRAY_ORNAMENT = ItemsPart02.LIGHT_GRAY_ORNAMENT;
    public static final RegistryObject<Item> CYAN_ORNAMENT = ItemsPart02.CYAN_ORNAMENT;
    public static final RegistryObject<Item> PURPLE_ORNAMENT = ItemsPart02.PURPLE_ORNAMENT;
    public static final RegistryObject<Item> BLUE_ORNAMENT = ItemsPart02.BLUE_ORNAMENT;
    public static final RegistryObject<Item> BROWN_ORNAMENT = ItemsPart02.BROWN_ORNAMENT;
    public static final RegistryObject<Item> GREEN_ORNAMENT = ItemsPart02.GREEN_ORNAMENT;
    public static final RegistryObject<Item> RED_ORNAMENT = ItemsPart02.RED_ORNAMENT;
    public static final RegistryObject<Item> BLACK_ORNAMENT = ItemsPart02.BLACK_ORNAMENT;
    public static final RegistryObject<Item> GLASS_ORNAMENT = ItemsPart02.GLASS_ORNAMENT;
    public static final RegistryObject<Item> TINTED_GLASS_ORNAMENT = ItemsPart02.TINTED_GLASS_ORNAMENT;
    public static final RegistryObject<Item> WHITE_STRING_LIGHT = ItemsPart02.WHITE_STRING_LIGHT;
    public static final RegistryObject<Item> ORANGE_STRING_LIGHT = ItemsPart02.ORANGE_STRING_LIGHT;
    public static final RegistryObject<Item> MAGENTA_STRING_LIGHT = ItemsPart02.MAGENTA_STRING_LIGHT;
    public static final RegistryObject<Item> LIGHT_BLUE_STRING_LIGHT = ItemsPart02.LIGHT_BLUE_STRING_LIGHT;
    public static final RegistryObject<Item> YELLOW_STRING_LIGHT = ItemsPart02.YELLOW_STRING_LIGHT;
    public static final RegistryObject<Item> LIME_STRING_LIGHT = ItemsPart02.LIME_STRING_LIGHT;
    public static final RegistryObject<Item> PINK_STRING_LIGHT = ItemsPart02.PINK_STRING_LIGHT;
    public static final RegistryObject<Item> GRAY_STRING_LIGHT = ItemsPart02.GRAY_STRING_LIGHT;
    public static final RegistryObject<Item> LIGHT_GRAY_STRING_LIGHT = ItemsPart02.LIGHT_GRAY_STRING_LIGHT;
    public static final RegistryObject<Item> CYAN_STRING_LIGHT = ItemsPart02.CYAN_STRING_LIGHT;
    public static final RegistryObject<Item> PURPLE_STRING_LIGHT = ItemsPart02.PURPLE_STRING_LIGHT;
    public static final RegistryObject<Item> BLUE_STRING_LIGHT = ItemsPart02.BLUE_STRING_LIGHT;
    public static final RegistryObject<Item> BROWN_STRING_LIGHT = ItemsPart02.BROWN_STRING_LIGHT;
    public static final RegistryObject<Item> GREEN_STRING_LIGHT = ItemsPart02.GREEN_STRING_LIGHT;
    public static final RegistryObject<Item> RED_STRING_LIGHT = ItemsPart02.RED_STRING_LIGHT;
    public static final RegistryObject<Item> BLACK_STRING_LIGHT = ItemsPart02.BLACK_STRING_LIGHT;
    public static final RegistryObject<Item> MULTICOLOR_STRING_LIGHT = ItemsPart02.MULTICOLOR_STRING_LIGHT;
    public static final RegistryObject<Item> WHITE_STAR = ItemsPart02.WHITE_STAR;
    public static final RegistryObject<Item> ORANGE_STAR = ItemsPart02.ORANGE_STAR;
    public static final RegistryObject<Item> MAGENTA_STAR = ItemsPart02.MAGENTA_STAR;
    public static final RegistryObject<Item> LIGHT_BLUE_STAR = ItemsPart02.LIGHT_BLUE_STAR;
    public static final RegistryObject<Item> YELLOW_STAR = ItemsPart02.YELLOW_STAR;
    public static final RegistryObject<Item> LIME_STAR = ItemsPart02.LIME_STAR;
    public static final RegistryObject<Item> PINK_STAR = ItemsPart02.PINK_STAR;
    public static final RegistryObject<Item> GRAY_STAR = ItemsPart02.GRAY_STAR;
    public static final RegistryObject<Item> LIGHT_GRAY_STAR = ItemsPart02.LIGHT_GRAY_STAR;
    public static final RegistryObject<Item> CYAN_STAR = ItemsPart02.CYAN_STAR;
    public static final RegistryObject<Item> PURPLE_STAR = ItemsPart02.PURPLE_STAR;
    public static final RegistryObject<Item> BLUE_STAR = ItemsPart02.BLUE_STAR;
    public static final RegistryObject<Item> BROWN_STAR = ItemsPart02.BROWN_STAR;
    public static final RegistryObject<Item> GREEN_STAR = ItemsPart02.GREEN_STAR;
    public static final RegistryObject<Item> RED_STAR = ItemsPart02.RED_STAR;
    public static final RegistryObject<Item> BLACK_STAR = ItemsPart02.BLACK_STAR;
    public static final RegistryObject<Item> GLOW_STAR = ItemsPart02.GLOW_STAR;
    public static final RegistryObject<Item> SNOWY_LEAVES = ItemsPart02.SNOWY_LEAVES;
    public static final RegistryObject<Item> SNOWY_OAK_LEAVES = ItemsPart02.SNOWY_OAK_LEAVES;
    public static final RegistryObject<Item> SNOWY_SPRUCE_LEAVES = ItemsPart02.SNOWY_SPRUCE_LEAVES;
    public static final RegistryObject<Item> SNOWY_BIRCH_LEAVES = ItemsPart02.SNOWY_BIRCH_LEAVES;
    public static final RegistryObject<Item> SNOWY_JUNGLE_LEAVES = ItemsPart02.SNOWY_JUNGLE_LEAVES;
    public static final RegistryObject<Item> SNOWY_ACACIA_LEAVES = ItemsPart02.SNOWY_ACACIA_LEAVES;
    public static final RegistryObject<Item> SNOWY_DARK_OAK_LEAVES = ItemsPart02.SNOWY_DARK_OAK_LEAVES;
    public static final RegistryObject<Item> SNOWY_MANGROVE_LEAVES = ItemsPart02.SNOWY_MANGROVE_LEAVES;
    public static final RegistryObject<Item> SNOWY_AZALEA_LEAVES = ItemsPart02.SNOWY_AZALEA_LEAVES;
    public static final RegistryObject<Item> SNOWY_FLOWERING_AZALEA_LEAVES = ItemsPart02.SNOWY_FLOWERING_AZALEA_LEAVES;
    public static final RegistryObject<Item> SNOWY_CHERRY_LEAVES = ItemsPart02.SNOWY_CHERRY_LEAVES;
    public static final RegistryObject<Item> SNOWY_PALE_OAK_LEAVES = ItemsPart02.SNOWY_PALE_OAK_LEAVES;
    public static final RegistryObject<Item> SNOWY_ORANGE_POPLAR_LEAVES = ItemsPart02.SNOWY_ORANGE_POPLAR_LEAVES;
    public static final RegistryObject<Item> SNOWY_RED_POPLAR_LEAVES = ItemsPart02.SNOWY_RED_POPLAR_LEAVES;
    public static final RegistryObject<Item> SNOWY_YELLOW_POPLAR_LEAVES = ItemsPart02.SNOWY_YELLOW_POPLAR_LEAVES;
    public static final RegistryObject<Item> SNOWY_SHORT_GRASS = ItemsPart02.SNOWY_SHORT_GRASS;
    public static final RegistryObject<Item> SNOWY_TALL_GRASS = ItemsPart02.SNOWY_TALL_GRASS;
    public static final RegistryObject<Item> SNOWY_FERN = ItemsPart02.SNOWY_FERN;
    public static final RegistryObject<Item> SNOWY_LARGE_FERN = ItemsPart02.SNOWY_LARGE_FERN;
    public static final RegistryObject<Item> SNOWY_BUSH = ItemsPart02.SNOWY_BUSH;
    public static final RegistryObject<Item> SNOW_BRICKS = ItemsPart02.SNOW_BRICKS;
    public static final RegistryObject<Item> SNOW_BRICKS_STAIRS = ItemsPart02.SNOW_BRICKS_STAIRS;
    public static final RegistryObject<Item> SNOW_BRICKS_SLAB = ItemsPart02.SNOW_BRICKS_SLAB;
    public static final RegistryObject<Item> SNOW_BRICKS_WALL = ItemsPart02.SNOW_BRICKS_WALL;
    public static final RegistryObject<Item> SNOWY_GRASS_BLOCK = ItemsPart02.SNOWY_GRASS_BLOCK;
    public static final RegistryObject<Item> SNOWY_GRASS_BLOCK_STAIRS = ItemsPart02.SNOWY_GRASS_BLOCK_STAIRS;
    public static final RegistryObject<Item> SNOWY_GRASS_BLOCK_SLAB = ItemsPart02.SNOWY_GRASS_BLOCK_SLAB;
    public static final RegistryObject<Item> ICICLE = ItemsPart02.ICICLE;
    public static final RegistryObject<Item> ICICLE_BLOCK = ItemsPart02.ICICLE_BLOCK;
    public static final RegistryObject<Item> PACKED_ICICLE_BLOCK = ItemsPart02.PACKED_ICICLE_BLOCK;
    public static final RegistryObject<Item> ICE_CRYSTAL = ItemsPart02.ICE_CRYSTAL;
    public static final RegistryObject<Item> COPPER_NUGGET = ItemsPart02.COPPER_NUGGET;
    public static final RegistryObject<Item> STEEL_INGOT = ItemsPart02.STEEL_INGOT;
    public static final RegistryObject<Item> STEEL_NUGGET = ItemsPart02.STEEL_NUGGET;
    public static final RegistryObject<Item> WRENCH = ItemsPart02.WRENCH;
    public static final RegistryObject<Item> IRON_HAMMER = ItemsPart02.IRON_HAMMER;
    public static final RegistryObject<Item> DIAMOND_HAMMER = ItemsPart02.DIAMOND_HAMMER;
    public static final RegistryObject<Item> NETHERITE_HAMMER = ItemsPart02.NETHERITE_HAMMER;
    public static final RegistryObject<Item> COPPER_BIOME_BRUSH = ItemsPart02.COPPER_BIOME_BRUSH;
    public static final RegistryObject<Item> DIAMOND_BIOME_BRUSH = ItemsPart02.DIAMOND_BIOME_BRUSH;
    public static final RegistryObject<Item> NETHERITE_BIOME_BRUSH = ItemsPart02.NETHERITE_BIOME_BRUSH;
    public static final RegistryObject<Item> BUILDERS_WORKBENCH = ItemsPart02.BUILDERS_WORKBENCH;
    public static final RegistryObject<Item> BUILDERS_POUCH = ItemsPart02.BUILDERS_POUCH;
    public static final RegistryObject<Item> SNOW_STAIRS = ItemsPart02.SNOW_STAIRS;
    public static final RegistryObject<Item> SNOW_SLAB = ItemsPart02.SNOW_SLAB;
    public static final RegistryObject<Item> MANGROVE_LOG = ItemsPart02.MANGROVE_LOG;
    public static final RegistryObject<Item> STRIPPED_MANGROVE_LOG = ItemsPart02.STRIPPED_MANGROVE_LOG;
    public static final RegistryObject<Item> MANGROVE_WOOD = ItemsPart02.MANGROVE_WOOD;
    public static final RegistryObject<Item> STRIPPED_MANGROVE_WOOD = ItemsPart02.STRIPPED_MANGROVE_WOOD;
    public static final RegistryObject<Item> MANGROVE_LEAVES = ItemsPart02.MANGROVE_LEAVES;
    public static final RegistryObject<Item> MANGROVE_ROOTS = ItemsPart02.MANGROVE_ROOTS;
    public static final RegistryObject<Item> MUDDY_MANGROVE_ROOTS = ItemsPart02.MUDDY_MANGROVE_ROOTS;
    public static final RegistryObject<Item> MANGROVE_PROPAGULE = ItemsPart02.MANGROVE_PROPAGULE;
    public static final RegistryObject<Item> MANGROVE_PLANKS = ItemsPart02.MANGROVE_PLANKS;
    public static final RegistryObject<Item> MANGROVE_SLAB = ItemsPart02.MANGROVE_SLAB;
    public static final RegistryObject<Item> MANGROVE_STAIRS = ItemsPart02.MANGROVE_STAIRS;
    public static final RegistryObject<Item> MANGROVE_FENCE = ItemsPart02.MANGROVE_FENCE;
    public static final RegistryObject<Item> MANGROVE_FENCE_GATE = ItemsPart02.MANGROVE_FENCE_GATE;
    public static final RegistryObject<Item> MANGROVE_DOOR = ItemsPart02.MANGROVE_DOOR;
    public static final RegistryObject<Item> MANGROVE_TRAPDOOR = ItemsPart02.MANGROVE_TRAPDOOR;
    public static final RegistryObject<Item> MANGROVE_BUTTON = ItemsPart02.MANGROVE_BUTTON;
    public static final RegistryObject<Item> MANGROVE_PRESSURE_PLATE = ItemsPart02.MANGROVE_PRESSURE_PLATE;
    public static final RegistryObject<Item> MANGROVE_SIGN = ItemsPart02.MANGROVE_SIGN;
    public static final RegistryObject<Item> BAMBOO_SIGN = ItemsPart02.BAMBOO_SIGN;
    public static final RegistryObject<Item> MANGROVE_BOAT = ItemsPart02.MANGROVE_BOAT;
    public static final RegistryObject<Item> FESTIVE_LAMP = ItemsPart02.FESTIVE_LAMP;
    public static final RegistryObject<Item> FROST_ROSE = ItemsPart02.FROST_ROSE;
    public static final RegistryObject<Item> GLOW_LIGHTS = ItemsPart02.GLOW_LIGHTS;
    public static final RegistryObject<Item> MULTICOLOR_GLOW_LIGHTS = ItemsPart02.MULTICOLOR_GLOW_LIGHTS;
    public static final RegistryObject<Item> CONFETTI_ITEM = ItemsPart02.CONFETTI_ITEM;
    public static final RegistryObject<Item> INFINITE_PHOENIX_FIREWORK_STAR =
            ItemsPart02.INFINITE_PHOENIX_FIREWORK_STAR;
    public static final RegistryObject<Item> STEEL_BLOCK = ItemsPart02.STEEL_BLOCK;
    public static final RegistryObject<Item> STEEL_PIPE = ItemsPart02.STEEL_PIPE;
    public static final RegistryObject<Item> HOLLOW_STEEL_PIPE = ItemsPart02.HOLLOW_STEEL_PIPE;
    public static final RegistryObject<Item> SCRAPED_STEEL = ItemsPart02.SCRAPED_STEEL;
    public static final RegistryObject<Item> RUSTIC_SCRAPED_STEEL = ItemsPart02.RUSTIC_SCRAPED_STEEL;
    public static final RegistryObject<Item> STACKED_STEEL = ItemsPart02.STACKED_STEEL;
    public static final RegistryObject<Item> STEEL_PANELS = ItemsPart02.STEEL_PANELS;
    public static final RegistryObject<Item> CROSSED_STEEL_PANELS = ItemsPart02.CROSSED_STEEL_PANELS;
    public static final RegistryObject<Item> STEEL_MESH_BLOCK = ItemsPart02.STEEL_MESH_BLOCK;
    public static final RegistryObject<Item> STEEL_BOLTS = ItemsPart02.STEEL_BOLTS;
    public static final RegistryObject<Item> STEEL_DOOR = ItemsPart02.STEEL_DOOR;
    public static final RegistryObject<Item> STEEL_TRAPDOOR = ItemsPart02.STEEL_TRAPDOOR;
    public static final RegistryObject<Item> STEEL_BUTTON = ItemsPart02.STEEL_BUTTON;
    public static final RegistryObject<Item> STEEL_PRESSURE_PLATE = ItemsPart02.STEEL_PRESSURE_PLATE;
    public static final RegistryObject<Item> SCRAPED_STEEL_SLAB = ItemsPart02.SCRAPED_STEEL_SLAB;
    public static final RegistryObject<Item> SCRAPED_STEEL_VERTICAL_SLAB = ItemsPart02.SCRAPED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> RUSTIC_SCRAPED_STEEL_SLAB = ItemsPart02.RUSTIC_SCRAPED_STEEL_SLAB;
    public static final RegistryObject<Item> RUSTIC_SCRAPED_STEEL_VERTICAL_SLAB =
            ItemsPart02.RUSTIC_SCRAPED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> STACKED_STEEL_SLAB = ItemsPart02.STACKED_STEEL_SLAB;
    public static final RegistryObject<Item> STACKED_STEEL_VERTICAL_SLAB = ItemsPart02.STACKED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> STACKED_STEEL_STAIRS = ItemsPart02.STACKED_STEEL_STAIRS;
    public static final RegistryObject<Item> STEEL_PANELS_SLAB = ItemsPart02.STEEL_PANELS_SLAB;
    public static final RegistryObject<Item> STEEL_PANELS_VERTICAL_SLAB = ItemsPart02.STEEL_PANELS_VERTICAL_SLAB;
    public static final RegistryObject<Item> STEEL_PANELS_STAIRS = ItemsPart02.STEEL_PANELS_STAIRS;
    public static final RegistryObject<Item> CROSSED_STEEL_PANELS_SLAB = ItemsPart02.CROSSED_STEEL_PANELS_SLAB;
    public static final RegistryObject<Item> CROSSED_STEEL_PANELS_VERTICAL_SLAB =
            ItemsPart02.CROSSED_STEEL_PANELS_VERTICAL_SLAB;
    public static final RegistryObject<Item> STEEL_MESH_BLOCK_SLAB = ItemsPart02.STEEL_MESH_BLOCK_SLAB;
    public static final RegistryObject<Item> STEEL_MESH_BLOCK_VERTICAL_SLAB =
            ItemsPart02.STEEL_MESH_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> STEEL_MESH_BLOCK_STAIRS = ItemsPart02.STEEL_MESH_BLOCK_STAIRS;
    public static final RegistryObject<Item> PRESSED_STEEL = ItemsPart02.PRESSED_STEEL;
    public static final RegistryObject<Item> CUT_STEEL = ItemsPart02.CUT_STEEL;
    public static final RegistryObject<Item> POLISHED_STEEL = ItemsPart02.POLISHED_STEEL;
    public static final RegistryObject<Item> FACTORY_STEEL_PANEL = ItemsPart02.FACTORY_STEEL_PANEL;
    public static final RegistryObject<Item> STEEL_CASING = ItemsPart02.STEEL_CASING;
    public static final RegistryObject<Item> STEEL_TRIM = ItemsPart02.STEEL_TRIM;
    public static final RegistryObject<Item> STEEL_PILLAR = ItemsPart02.STEEL_PILLAR;
    public static final RegistryObject<Item> BOLTED_STEEL_PILLAR = ItemsPart02.BOLTED_STEEL_PILLAR;
    public static final RegistryObject<Item> STEEL_GRATE = ItemsPart02.STEEL_GRATE;
    public static final RegistryObject<Item> STEEL_FAN = ItemsPart02.STEEL_FAN;
    public static final RegistryObject<Item> STEEL_BLOCK_STAIRS = ItemsPart02.STEEL_BLOCK_STAIRS;
    public static final RegistryObject<Item> STEEL_BLOCK_SLAB = ItemsPart02.STEEL_BLOCK_SLAB;
    public static final RegistryObject<Item> STEEL_BLOCK_WALL = ItemsPart02.STEEL_BLOCK_WALL;
    public static final RegistryObject<Item> POLISHED_STEEL_STAIRS = ItemsPart02.POLISHED_STEEL_STAIRS;
    public static final RegistryObject<Item> POLISHED_STEEL_SLAB = ItemsPart02.POLISHED_STEEL_SLAB;
    public static final RegistryObject<Item> POLISHED_STEEL_WALL = ItemsPart02.POLISHED_STEEL_WALL;
    public static final RegistryObject<Item> PRESSED_STEEL_STAIRS = ItemsPart02.PRESSED_STEEL_STAIRS;
    public static final RegistryObject<Item> PRESSED_STEEL_SLAB = ItemsPart02.PRESSED_STEEL_SLAB;
    public static final RegistryObject<Item> PRESSED_STEEL_WALL = ItemsPart02.PRESSED_STEEL_WALL;
    public static final RegistryObject<Item> CUT_STEEL_STAIRS = ItemsPart02.CUT_STEEL_STAIRS;
    public static final RegistryObject<Item> CUT_STEEL_SLAB = ItemsPart02.CUT_STEEL_SLAB;
    public static final RegistryObject<Item> CUT_STEEL_WALL = ItemsPart02.CUT_STEEL_WALL;
    public static final RegistryObject<Item> CAUTION_BLACK = ItemsPart02.CAUTION_BLACK;
    public static final RegistryObject<Item> CAUTION_BLUE = ItemsPart02.CAUTION_BLUE;
    public static final RegistryObject<Item> CAUTION_FACTORY = ItemsPart02.CAUTION_FACTORY;
    public static final RegistryObject<Item> CAUTION_RED = ItemsPart02.CAUTION_RED;
    public static final RegistryObject<Item> FRAMED_CAUTION = ItemsPart02.FRAMED_CAUTION;
    public static final RegistryObject<Item> CAUTION_WHITE = ItemsPart02.CAUTION_WHITE;
    public static final RegistryObject<Item> CAUTION_LIME = ItemsPart02.CAUTION_LIME;
    public static final RegistryObject<Item> CAUTION_PINK = ItemsPart02.CAUTION_PINK;
    public static final RegistryObject<Item> CAUTION_YELLOW = ItemsPart02.CAUTION_YELLOW;
    public static final RegistryObject<Item> CAUTION_CANDY = ItemsPart02.CAUTION_CANDY;
    public static final RegistryObject<Item> CAUTION_COTTONCANDY = ItemsPart02.CAUTION_COTTONCANDY;
    public static final RegistryObject<Item> CAUTION_MINTCANDY = ItemsPart02.CAUTION_MINTCANDY;
    public static final RegistryObject<Item> CAUTION_CITRUSCANDY = ItemsPart02.CAUTION_CITRUSCANDY;
    public static final RegistryObject<Item> CAUTION_BLACK_SLAB = ItemsPart02.CAUTION_BLACK_SLAB;
    public static final RegistryObject<Item> CAUTION_BLUE_SLAB = ItemsPart02.CAUTION_BLUE_SLAB;
    public static final RegistryObject<Item> CAUTION_FACTORY_SLAB = ItemsPart02.CAUTION_FACTORY_SLAB;
    public static final RegistryObject<Item> CAUTION_RED_SLAB = ItemsPart02.CAUTION_RED_SLAB;
    public static final RegistryObject<Item> FRAMED_CAUTION_SLAB = ItemsPart02.FRAMED_CAUTION_SLAB;
    public static final RegistryObject<Item> CAUTION_WHITE_SLAB = ItemsPart02.CAUTION_WHITE_SLAB;
    public static final RegistryObject<Item> CAUTION_LIME_SLAB = ItemsPart02.CAUTION_LIME_SLAB;
    public static final RegistryObject<Item> CAUTION_PINK_SLAB = ItemsPart02.CAUTION_PINK_SLAB;
    public static final RegistryObject<Item> CAUTION_YELLOW_SLAB = ItemsPart02.CAUTION_YELLOW_SLAB;
    public static final RegistryObject<Item> CAUTION_CANDY_SLAB = ItemsPart02.CAUTION_CANDY_SLAB;
    public static final RegistryObject<Item> CAUTION_COTTONCANDY_SLAB = ItemsPart02.CAUTION_COTTONCANDY_SLAB;
    public static final RegistryObject<Item> CAUTION_MINTCANDY_SLAB = ItemsPart02.CAUTION_MINTCANDY_SLAB;
    public static final RegistryObject<Item> CAUTION_CITRUSCANDY_SLAB = ItemsPart02.CAUTION_CITRUSCANDY_SLAB;
    public static final RegistryObject<Item> CAUTION_BLACK_STAIRS = ItemsPart02.CAUTION_BLACK_STAIRS;
    public static final RegistryObject<Item> CAUTION_BLUE_STAIRS = ItemsPart02.CAUTION_BLUE_STAIRS;
    public static final RegistryObject<Item> CAUTION_FACTORY_STAIRS = ItemsPart02.CAUTION_FACTORY_STAIRS;
    public static final RegistryObject<Item> CAUTION_RED_STAIRS = ItemsPart02.CAUTION_RED_STAIRS;
    public static final RegistryObject<Item> FRAMED_CAUTION_STAIRS = ItemsPart02.FRAMED_CAUTION_STAIRS;
    public static final RegistryObject<Item> CAUTION_WHITE_STAIRS = ItemsPart02.CAUTION_WHITE_STAIRS;
    public static final RegistryObject<Item> CAUTION_LIME_STAIRS = ItemsPart02.CAUTION_LIME_STAIRS;
    public static final RegistryObject<Item> CAUTION_PINK_STAIRS = ItemsPart02.CAUTION_PINK_STAIRS;
    public static final RegistryObject<Item> CAUTION_YELLOW_STAIRS = ItemsPart02.CAUTION_YELLOW_STAIRS;
    public static final RegistryObject<Item> CAUTION_CANDY_STAIRS = ItemsPart02.CAUTION_CANDY_STAIRS;
    public static final RegistryObject<Item> CAUTION_COTTONCANDY_STAIRS = ItemsPart02.CAUTION_COTTONCANDY_STAIRS;
    public static final RegistryObject<Item> CAUTION_MINTCANDY_STAIRS = ItemsPart02.CAUTION_MINTCANDY_STAIRS;
    public static final RegistryObject<Item> CAUTION_CITRUSCANDY_STAIRS = ItemsPart02.CAUTION_CITRUSCANDY_STAIRS;
    public static final RegistryObject<Item> FACTORY_WHITE_GLASS = ItemsPart02.FACTORY_WHITE_GLASS;
    public static final RegistryObject<Item> FACTORY_LIGHT_GRAY_GLASS = ItemsPart02.FACTORY_LIGHT_GRAY_GLASS;
    public static final RegistryObject<Item> FACTORY_GRAY_GLASS = ItemsPart02.FACTORY_GRAY_GLASS;
    public static final RegistryObject<Item> FACTORY_BLACK_GLASS = ItemsPart02.FACTORY_BLACK_GLASS;
    public static final RegistryObject<Item> FACTORY_BROWN_GLASS = ItemsPart02.FACTORY_BROWN_GLASS;
    public static final RegistryObject<Item> FACTORY_RED_GLASS = ItemsPart02.FACTORY_RED_GLASS;
    public static final RegistryObject<Item> FACTORY_ORANGE_GLASS = ItemsPart02.FACTORY_ORANGE_GLASS;
    public static final RegistryObject<Item> FACTORY_YELLOW_GLASS = ItemsPart02.FACTORY_YELLOW_GLASS;
    public static final RegistryObject<Item> FACTORY_LIME_GLASS = ItemsPart02.FACTORY_LIME_GLASS;
    public static final RegistryObject<Item> FACTORY_GREEN_GLASS = ItemsPart02.FACTORY_GREEN_GLASS;
    public static final RegistryObject<Item> FACTORY_CYAN_GLASS = ItemsPart02.FACTORY_CYAN_GLASS;
    public static final RegistryObject<Item> FACTORY_LIGHT_BLUE_GLASS = ItemsPart02.FACTORY_LIGHT_BLUE_GLASS;
    public static final RegistryObject<Item> FACTORY_BLUE_GLASS = ItemsPart02.FACTORY_BLUE_GLASS;
    public static final RegistryObject<Item> FACTORY_PURPLE_GLASS = ItemsPart02.FACTORY_PURPLE_GLASS;
    public static final RegistryObject<Item> FACTORY_MAGENTA_GLASS = ItemsPart02.FACTORY_MAGENTA_GLASS;
    public static final RegistryObject<Item> FACTORY_PINK_GLASS = ItemsPart02.FACTORY_PINK_GLASS;
    public static final RegistryObject<Item> FACTORY_WHITE_GLASS_PANE = ItemsPart02.FACTORY_WHITE_GLASS_PANE;
    public static final RegistryObject<Item> FACTORY_LIGHT_GRAY_GLASS_PANE = ItemsPart02.FACTORY_LIGHT_GRAY_GLASS_PANE;
    public static final RegistryObject<Item> FACTORY_GRAY_GLASS_PANE = ItemsPart02.FACTORY_GRAY_GLASS_PANE;
    public static final RegistryObject<Item> FACTORY_BLACK_GLASS_PANE = ItemsPart02.FACTORY_BLACK_GLASS_PANE;
    public static final RegistryObject<Item> FACTORY_BROWN_GLASS_PANE = ItemsPart02.FACTORY_BROWN_GLASS_PANE;
    public static final RegistryObject<Item> FACTORY_RED_GLASS_PANE = ItemsPart02.FACTORY_RED_GLASS_PANE;
    public static final RegistryObject<Item> FACTORY_ORANGE_GLASS_PANE = ItemsPart02.FACTORY_ORANGE_GLASS_PANE;
    public static final RegistryObject<Item> FACTORY_YELLOW_GLASS_PANE = ItemsPart02.FACTORY_YELLOW_GLASS_PANE;
    public static final RegistryObject<Item> FACTORY_LIME_GLASS_PANE = ItemsPart02.FACTORY_LIME_GLASS_PANE;
    public static final RegistryObject<Item> FACTORY_GREEN_GLASS_PANE = ItemsPart02.FACTORY_GREEN_GLASS_PANE;
    public static final RegistryObject<Item> FACTORY_CYAN_GLASS_PANE = ItemsPart02.FACTORY_CYAN_GLASS_PANE;
    public static final RegistryObject<Item> FACTORY_LIGHT_BLUE_GLASS_PANE = ItemsPart02.FACTORY_LIGHT_BLUE_GLASS_PANE;
    public static final RegistryObject<Item> FACTORY_BLUE_GLASS_PANE = ItemsPart02.FACTORY_BLUE_GLASS_PANE;
    public static final RegistryObject<Item> FACTORY_PURPLE_GLASS_PANE = ItemsPart02.FACTORY_PURPLE_GLASS_PANE;
    public static final RegistryObject<Item> FACTORY_MAGENTA_GLASS_PANE = ItemsPart02.FACTORY_MAGENTA_GLASS_PANE;
    public static final RegistryObject<Item> FACTORY_PINK_GLASS_PANE = ItemsPart02.FACTORY_PINK_GLASS_PANE;
    public static final RegistryObject<Item> WHITE_STAINED_BRICKS = ItemsPart02.WHITE_STAINED_BRICKS;
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICKS = ItemsPart02.LIGHT_GRAY_STAINED_BRICKS;
    public static final RegistryObject<Item> GRAY_STAINED_BRICKS = ItemsPart02.GRAY_STAINED_BRICKS;
    public static final RegistryObject<Item> BLACK_STAINED_BRICKS = ItemsPart02.BLACK_STAINED_BRICKS;
    public static final RegistryObject<Item> BROWN_STAINED_BRICKS = ItemsPart02.BROWN_STAINED_BRICKS;
    public static final RegistryObject<Item> RED_STAINED_BRICKS = ItemsPart02.RED_STAINED_BRICKS;
    public static final RegistryObject<Item> ORANGE_STAINED_BRICKS = ItemsPart02.ORANGE_STAINED_BRICKS;
    public static final RegistryObject<Item> YELLOW_STAINED_BRICKS = ItemsPart02.YELLOW_STAINED_BRICKS;
    public static final RegistryObject<Item> LIME_STAINED_BRICKS = ItemsPart02.LIME_STAINED_BRICKS;
    public static final RegistryObject<Item> GREEN_STAINED_BRICKS = ItemsPart02.GREEN_STAINED_BRICKS;
    public static final RegistryObject<Item> CYAN_STAINED_BRICKS = ItemsPart02.CYAN_STAINED_BRICKS;
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICKS = ItemsPart02.LIGHT_BLUE_STAINED_BRICKS;
    public static final RegistryObject<Item> BLUE_STAINED_BRICKS = ItemsPart02.BLUE_STAINED_BRICKS;
    public static final RegistryObject<Item> PURPLE_STAINED_BRICKS = ItemsPart02.PURPLE_STAINED_BRICKS;
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICKS = ItemsPart02.MAGENTA_STAINED_BRICKS;
    public static final RegistryObject<Item> PINK_STAINED_BRICKS = ItemsPart02.PINK_STAINED_BRICKS;
    public static final RegistryObject<Item> WHITE_STAINED_BRICK_TILES = ItemsPart02.WHITE_STAINED_BRICK_TILES;
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICK_TILES =
            ItemsPart02.LIGHT_GRAY_STAINED_BRICK_TILES;
    public static final RegistryObject<Item> GRAY_STAINED_BRICK_TILES = ItemsPart02.GRAY_STAINED_BRICK_TILES;
    public static final RegistryObject<Item> BLACK_STAINED_BRICK_TILES = ItemsPart02.BLACK_STAINED_BRICK_TILES;
    public static final RegistryObject<Item> BROWN_STAINED_BRICK_TILES = ItemsPart02.BROWN_STAINED_BRICK_TILES;
    public static final RegistryObject<Item> RED_STAINED_BRICK_TILES = ItemsPart02.RED_STAINED_BRICK_TILES;
    public static final RegistryObject<Item> ORANGE_STAINED_BRICK_TILES = ItemsPart02.ORANGE_STAINED_BRICK_TILES;
    public static final RegistryObject<Item> YELLOW_STAINED_BRICK_TILES = ItemsPart02.YELLOW_STAINED_BRICK_TILES;
    public static final RegistryObject<Item> LIME_STAINED_BRICK_TILES = ItemsPart02.LIME_STAINED_BRICK_TILES;
    public static final RegistryObject<Item> GREEN_STAINED_BRICK_TILES = ItemsPart02.GREEN_STAINED_BRICK_TILES;
    public static final RegistryObject<Item> CYAN_STAINED_BRICK_TILES = ItemsPart02.CYAN_STAINED_BRICK_TILES;
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICK_TILES =
            ItemsPart02.LIGHT_BLUE_STAINED_BRICK_TILES;
    public static final RegistryObject<Item> BLUE_STAINED_BRICK_TILES = ItemsPart02.BLUE_STAINED_BRICK_TILES;
    public static final RegistryObject<Item> PURPLE_STAINED_BRICK_TILES = ItemsPart02.PURPLE_STAINED_BRICK_TILES;
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICK_TILES = ItemsPart02.MAGENTA_STAINED_BRICK_TILES;
    public static final RegistryObject<Item> PINK_STAINED_BRICK_TILES = ItemsPart02.PINK_STAINED_BRICK_TILES;
    public static final RegistryObject<Item> WHITE_STAINED_BRICK_TILES_SLAB =
            ItemsPart02.WHITE_STAINED_BRICK_TILES_SLAB;
    public static final RegistryObject<Item> WHITE_STAINED_BRICK_TILES_STAIRS =
            ItemsPart02.WHITE_STAINED_BRICK_TILES_STAIRS;
    public static final RegistryObject<Item> WHITE_STAINED_BRICK_TILES_WALL =
            ItemsPart02.WHITE_STAINED_BRICK_TILES_WALL;
    public static final RegistryObject<Item> WHITE_STAINED_BRICK_TILES_VERTICAL_SLAB =
            ItemsPart02.WHITE_STAINED_BRICK_TILES_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICK_TILES_SLAB =
            ItemsPart02.LIGHT_GRAY_STAINED_BRICK_TILES_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICK_TILES_STAIRS =
            ItemsPart02.LIGHT_GRAY_STAINED_BRICK_TILES_STAIRS;
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICK_TILES_WALL =
            ItemsPart02.LIGHT_GRAY_STAINED_BRICK_TILES_WALL;
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICK_TILES_VERTICAL_SLAB =
            ItemsPart02.LIGHT_GRAY_STAINED_BRICK_TILES_VERTICAL_SLAB;
    public static final RegistryObject<Item> GRAY_STAINED_BRICK_TILES_SLAB = ItemsPart02.GRAY_STAINED_BRICK_TILES_SLAB;
    public static final RegistryObject<Item> GRAY_STAINED_BRICK_TILES_STAIRS =
            ItemsPart02.GRAY_STAINED_BRICK_TILES_STAIRS;
    public static final RegistryObject<Item> GRAY_STAINED_BRICK_TILES_WALL = ItemsPart02.GRAY_STAINED_BRICK_TILES_WALL;
    public static final RegistryObject<Item> GRAY_STAINED_BRICK_TILES_VERTICAL_SLAB =
            ItemsPart02.GRAY_STAINED_BRICK_TILES_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLACK_STAINED_BRICK_TILES_SLAB =
            ItemsPart02.BLACK_STAINED_BRICK_TILES_SLAB;
    public static final RegistryObject<Item> BLACK_STAINED_BRICK_TILES_STAIRS =
            ItemsPart02.BLACK_STAINED_BRICK_TILES_STAIRS;
    public static final RegistryObject<Item> BLACK_STAINED_BRICK_TILES_WALL =
            ItemsPart02.BLACK_STAINED_BRICK_TILES_WALL;
    public static final RegistryObject<Item> BLACK_STAINED_BRICK_TILES_VERTICAL_SLAB =
            ItemsPart02.BLACK_STAINED_BRICK_TILES_VERTICAL_SLAB;
    public static final RegistryObject<Item> BROWN_STAINED_BRICK_TILES_SLAB =
            ItemsPart02.BROWN_STAINED_BRICK_TILES_SLAB;
    public static final RegistryObject<Item> BROWN_STAINED_BRICK_TILES_STAIRS =
            ItemsPart02.BROWN_STAINED_BRICK_TILES_STAIRS;
    public static final RegistryObject<Item> BROWN_STAINED_BRICK_TILES_WALL =
            ItemsPart02.BROWN_STAINED_BRICK_TILES_WALL;
    public static final RegistryObject<Item> BROWN_STAINED_BRICK_TILES_VERTICAL_SLAB =
            ItemsPart02.BROWN_STAINED_BRICK_TILES_VERTICAL_SLAB;
    public static final RegistryObject<Item> RED_STAINED_BRICK_TILES_SLAB = ItemsPart02.RED_STAINED_BRICK_TILES_SLAB;
    public static final RegistryObject<Item> RED_STAINED_BRICK_TILES_STAIRS =
            ItemsPart02.RED_STAINED_BRICK_TILES_STAIRS;
    public static final RegistryObject<Item> RED_STAINED_BRICK_TILES_WALL = ItemsPart02.RED_STAINED_BRICK_TILES_WALL;
    public static final RegistryObject<Item> RED_STAINED_BRICK_TILES_VERTICAL_SLAB =
            ItemsPart02.RED_STAINED_BRICK_TILES_VERTICAL_SLAB;
    public static final RegistryObject<Item> ORANGE_STAINED_BRICK_TILES_SLAB =
            ItemsPart02.ORANGE_STAINED_BRICK_TILES_SLAB;
    public static final RegistryObject<Item> ORANGE_STAINED_BRICK_TILES_STAIRS =
            ItemsPart02.ORANGE_STAINED_BRICK_TILES_STAIRS;
    public static final RegistryObject<Item> ORANGE_STAINED_BRICK_TILES_WALL =
            ItemsPart02.ORANGE_STAINED_BRICK_TILES_WALL;
    public static final RegistryObject<Item> ORANGE_STAINED_BRICK_TILES_VERTICAL_SLAB =
            ItemsPart02.ORANGE_STAINED_BRICK_TILES_VERTICAL_SLAB;
    public static final RegistryObject<Item> YELLOW_STAINED_BRICK_TILES_SLAB =
            ItemsPart02.YELLOW_STAINED_BRICK_TILES_SLAB;
    public static final RegistryObject<Item> YELLOW_STAINED_BRICK_TILES_STAIRS =
            ItemsPart02.YELLOW_STAINED_BRICK_TILES_STAIRS;
    public static final RegistryObject<Item> YELLOW_STAINED_BRICK_TILES_WALL =
            ItemsPart02.YELLOW_STAINED_BRICK_TILES_WALL;
    public static final RegistryObject<Item> YELLOW_STAINED_BRICK_TILES_VERTICAL_SLAB =
            ItemsPart02.YELLOW_STAINED_BRICK_TILES_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIME_STAINED_BRICK_TILES_SLAB = ItemsPart02.LIME_STAINED_BRICK_TILES_SLAB;
    public static final RegistryObject<Item> LIME_STAINED_BRICK_TILES_STAIRS =
            ItemsPart02.LIME_STAINED_BRICK_TILES_STAIRS;
    public static final RegistryObject<Item> LIME_STAINED_BRICK_TILES_WALL = ItemsPart02.LIME_STAINED_BRICK_TILES_WALL;
    public static final RegistryObject<Item> LIME_STAINED_BRICK_TILES_VERTICAL_SLAB =
            ItemsPart02.LIME_STAINED_BRICK_TILES_VERTICAL_SLAB;
    public static final RegistryObject<Item> GREEN_STAINED_BRICK_TILES_SLAB =
            ItemsPart02.GREEN_STAINED_BRICK_TILES_SLAB;
    public static final RegistryObject<Item> GREEN_STAINED_BRICK_TILES_STAIRS =
            ItemsPart02.GREEN_STAINED_BRICK_TILES_STAIRS;
    public static final RegistryObject<Item> GREEN_STAINED_BRICK_TILES_WALL =
            ItemsPart02.GREEN_STAINED_BRICK_TILES_WALL;
    public static final RegistryObject<Item> GREEN_STAINED_BRICK_TILES_VERTICAL_SLAB =
            ItemsPart02.GREEN_STAINED_BRICK_TILES_VERTICAL_SLAB;
    public static final RegistryObject<Item> CYAN_STAINED_BRICK_TILES_SLAB = ItemsPart02.CYAN_STAINED_BRICK_TILES_SLAB;
    public static final RegistryObject<Item> CYAN_STAINED_BRICK_TILES_STAIRS =
            ItemsPart02.CYAN_STAINED_BRICK_TILES_STAIRS;
    public static final RegistryObject<Item> CYAN_STAINED_BRICK_TILES_WALL = ItemsPart02.CYAN_STAINED_BRICK_TILES_WALL;
    public static final RegistryObject<Item> CYAN_STAINED_BRICK_TILES_VERTICAL_SLAB =
            ItemsPart02.CYAN_STAINED_BRICK_TILES_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICK_TILES_SLAB =
            ItemsPart02.LIGHT_BLUE_STAINED_BRICK_TILES_SLAB;
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICK_TILES_STAIRS =
            ItemsPart02.LIGHT_BLUE_STAINED_BRICK_TILES_STAIRS;
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICK_TILES_WALL =
            ItemsPart02.LIGHT_BLUE_STAINED_BRICK_TILES_WALL;
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICK_TILES_VERTICAL_SLAB =
            ItemsPart02.LIGHT_BLUE_STAINED_BRICK_TILES_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLUE_STAINED_BRICK_TILES_SLAB = ItemsPart02.BLUE_STAINED_BRICK_TILES_SLAB;
    public static final RegistryObject<Item> BLUE_STAINED_BRICK_TILES_STAIRS =
            ItemsPart02.BLUE_STAINED_BRICK_TILES_STAIRS;
    public static final RegistryObject<Item> BLUE_STAINED_BRICK_TILES_WALL = ItemsPart02.BLUE_STAINED_BRICK_TILES_WALL;
    public static final RegistryObject<Item> BLUE_STAINED_BRICK_TILES_VERTICAL_SLAB =
            ItemsPart02.BLUE_STAINED_BRICK_TILES_VERTICAL_SLAB;
    public static final RegistryObject<Item> PURPLE_STAINED_BRICK_TILES_SLAB =
            ItemsPart02.PURPLE_STAINED_BRICK_TILES_SLAB;
    public static final RegistryObject<Item> PURPLE_STAINED_BRICK_TILES_STAIRS =
            ItemsPart02.PURPLE_STAINED_BRICK_TILES_STAIRS;
    public static final RegistryObject<Item> PURPLE_STAINED_BRICK_TILES_WALL =
            ItemsPart02.PURPLE_STAINED_BRICK_TILES_WALL;
    public static final RegistryObject<Item> PURPLE_STAINED_BRICK_TILES_VERTICAL_SLAB =
            ItemsPart02.PURPLE_STAINED_BRICK_TILES_VERTICAL_SLAB;
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICK_TILES_SLAB =
            ItemsPart02.MAGENTA_STAINED_BRICK_TILES_SLAB;
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICK_TILES_STAIRS =
            ItemsPart02.MAGENTA_STAINED_BRICK_TILES_STAIRS;
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICK_TILES_WALL =
            ItemsPart02.MAGENTA_STAINED_BRICK_TILES_WALL;
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICK_TILES_VERTICAL_SLAB =
            ItemsPart02.MAGENTA_STAINED_BRICK_TILES_VERTICAL_SLAB;
    public static final RegistryObject<Item> PINK_STAINED_BRICK_TILES_SLAB = ItemsPart02.PINK_STAINED_BRICK_TILES_SLAB;
    public static final RegistryObject<Item> PINK_STAINED_BRICK_TILES_STAIRS =
            ItemsPart02.PINK_STAINED_BRICK_TILES_STAIRS;
    public static final RegistryObject<Item> PINK_STAINED_BRICK_TILES_WALL = ItemsPart02.PINK_STAINED_BRICK_TILES_WALL;
    public static final RegistryObject<Item> PINK_STAINED_BRICK_TILES_VERTICAL_SLAB =
            ItemsPart02.PINK_STAINED_BRICK_TILES_VERTICAL_SLAB;
    public static final RegistryObject<Item> WHITE_STAINED_BRICKS_SLAB = ItemsPart02.WHITE_STAINED_BRICKS_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICKS_SLAB =
            ItemsPart02.LIGHT_GRAY_STAINED_BRICKS_SLAB;
    public static final RegistryObject<Item> GRAY_STAINED_BRICKS_SLAB = ItemsPart02.GRAY_STAINED_BRICKS_SLAB;
    public static final RegistryObject<Item> BLACK_STAINED_BRICKS_SLAB = ItemsPart02.BLACK_STAINED_BRICKS_SLAB;
    public static final RegistryObject<Item> BROWN_STAINED_BRICKS_SLAB = ItemsPart02.BROWN_STAINED_BRICKS_SLAB;
    public static final RegistryObject<Item> RED_STAINED_BRICKS_SLAB = ItemsPart02.RED_STAINED_BRICKS_SLAB;
    public static final RegistryObject<Item> ORANGE_STAINED_BRICKS_SLAB = ItemsPart02.ORANGE_STAINED_BRICKS_SLAB;
    public static final RegistryObject<Item> YELLOW_STAINED_BRICKS_SLAB = ItemsPart02.YELLOW_STAINED_BRICKS_SLAB;
    public static final RegistryObject<Item> LIME_STAINED_BRICKS_SLAB = ItemsPart02.LIME_STAINED_BRICKS_SLAB;
    public static final RegistryObject<Item> GREEN_STAINED_BRICKS_SLAB = ItemsPart02.GREEN_STAINED_BRICKS_SLAB;
    public static final RegistryObject<Item> CYAN_STAINED_BRICKS_SLAB = ItemsPart02.CYAN_STAINED_BRICKS_SLAB;
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICKS_SLAB =
            ItemsPart02.LIGHT_BLUE_STAINED_BRICKS_SLAB;
    public static final RegistryObject<Item> BLUE_STAINED_BRICKS_SLAB = ItemsPart02.BLUE_STAINED_BRICKS_SLAB;
    public static final RegistryObject<Item> PURPLE_STAINED_BRICKS_SLAB = ItemsPart02.PURPLE_STAINED_BRICKS_SLAB;
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICKS_SLAB = ItemsPart02.MAGENTA_STAINED_BRICKS_SLAB;
    public static final RegistryObject<Item> PINK_STAINED_BRICKS_SLAB = ItemsPart02.PINK_STAINED_BRICKS_SLAB;
    public static final RegistryObject<Item> WHITE_STAINED_BRICKS_STAIRS = ItemsPart02.WHITE_STAINED_BRICKS_STAIRS;
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICKS_STAIRS =
            ItemsPart02.LIGHT_GRAY_STAINED_BRICKS_STAIRS;
    public static final RegistryObject<Item> GRAY_STAINED_BRICKS_STAIRS = ItemsPart02.GRAY_STAINED_BRICKS_STAIRS;
    public static final RegistryObject<Item> BLACK_STAINED_BRICKS_STAIRS = ItemsPart02.BLACK_STAINED_BRICKS_STAIRS;
    public static final RegistryObject<Item> BROWN_STAINED_BRICKS_STAIRS = ItemsPart02.BROWN_STAINED_BRICKS_STAIRS;
    public static final RegistryObject<Item> RED_STAINED_BRICKS_STAIRS = ItemsPart02.RED_STAINED_BRICKS_STAIRS;
    public static final RegistryObject<Item> ORANGE_STAINED_BRICKS_STAIRS = ItemsPart02.ORANGE_STAINED_BRICKS_STAIRS;
    public static final RegistryObject<Item> YELLOW_STAINED_BRICKS_STAIRS = ItemsPart02.YELLOW_STAINED_BRICKS_STAIRS;
    public static final RegistryObject<Item> LIME_STAINED_BRICKS_STAIRS = ItemsPart02.LIME_STAINED_BRICKS_STAIRS;
    public static final RegistryObject<Item> GREEN_STAINED_BRICKS_STAIRS = ItemsPart02.GREEN_STAINED_BRICKS_STAIRS;
    public static final RegistryObject<Item> CYAN_STAINED_BRICKS_STAIRS = ItemsPart02.CYAN_STAINED_BRICKS_STAIRS;
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICKS_STAIRS =
            ItemsPart02.LIGHT_BLUE_STAINED_BRICKS_STAIRS;
    public static final RegistryObject<Item> BLUE_STAINED_BRICKS_STAIRS = ItemsPart02.BLUE_STAINED_BRICKS_STAIRS;
    public static final RegistryObject<Item> PURPLE_STAINED_BRICKS_STAIRS = ItemsPart02.PURPLE_STAINED_BRICKS_STAIRS;
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICKS_STAIRS = ItemsPart02.MAGENTA_STAINED_BRICKS_STAIRS;
    public static final RegistryObject<Item> PINK_STAINED_BRICKS_STAIRS = ItemsPart02.PINK_STAINED_BRICKS_STAIRS;
    public static final RegistryObject<Item> WHITE_STAINED_BRICKS_WALL = ItemsPart02.WHITE_STAINED_BRICKS_WALL;
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICKS_WALL =
            ItemsPart02.LIGHT_GRAY_STAINED_BRICKS_WALL;
    public static final RegistryObject<Item> GRAY_STAINED_BRICKS_WALL = ItemsPart02.GRAY_STAINED_BRICKS_WALL;
    public static final RegistryObject<Item> BLACK_STAINED_BRICKS_WALL = ItemsPart02.BLACK_STAINED_BRICKS_WALL;
    public static final RegistryObject<Item> BROWN_STAINED_BRICKS_WALL = ItemsPart02.BROWN_STAINED_BRICKS_WALL;
    public static final RegistryObject<Item> RED_STAINED_BRICKS_WALL = ItemsPart02.RED_STAINED_BRICKS_WALL;
    public static final RegistryObject<Item> ORANGE_STAINED_BRICKS_WALL = ItemsPart02.ORANGE_STAINED_BRICKS_WALL;
    public static final RegistryObject<Item> YELLOW_STAINED_BRICKS_WALL = ItemsPart02.YELLOW_STAINED_BRICKS_WALL;
    public static final RegistryObject<Item> LIME_STAINED_BRICKS_WALL = ItemsPart02.LIME_STAINED_BRICKS_WALL;
    public static final RegistryObject<Item> GREEN_STAINED_BRICKS_WALL = ItemsPart02.GREEN_STAINED_BRICKS_WALL;
    public static final RegistryObject<Item> CYAN_STAINED_BRICKS_WALL = ItemsPart02.CYAN_STAINED_BRICKS_WALL;
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICKS_WALL =
            ItemsPart02.LIGHT_BLUE_STAINED_BRICKS_WALL;
    public static final RegistryObject<Item> BLUE_STAINED_BRICKS_WALL = ItemsPart02.BLUE_STAINED_BRICKS_WALL;
    public static final RegistryObject<Item> PURPLE_STAINED_BRICKS_WALL = ItemsPart02.PURPLE_STAINED_BRICKS_WALL;
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICKS_WALL = ItemsPart02.MAGENTA_STAINED_BRICKS_WALL;
    public static final RegistryObject<Item> PINK_STAINED_BRICKS_WALL = ItemsPart02.PINK_STAINED_BRICKS_WALL;
    public static final RegistryObject<Item> WHITE_ITEM_FRAME = ItemsPart02.WHITE_ITEM_FRAME;
    public static final RegistryObject<Item> LIGHT_GRAY_ITEM_FRAME = ItemsPart02.LIGHT_GRAY_ITEM_FRAME;
    public static final RegistryObject<Item> GRAY_ITEM_FRAME = ItemsPart02.GRAY_ITEM_FRAME;
    public static final RegistryObject<Item> BLACK_ITEM_FRAME = ItemsPart02.BLACK_ITEM_FRAME;
    public static final RegistryObject<Item> BROWN_ITEM_FRAME = ItemsPart02.BROWN_ITEM_FRAME;
    public static final RegistryObject<Item> RED_ITEM_FRAME = ItemsPart02.RED_ITEM_FRAME;
    public static final RegistryObject<Item> ORANGE_ITEM_FRAME = ItemsPart02.ORANGE_ITEM_FRAME;
    public static final RegistryObject<Item> YELLOW_ITEM_FRAME = ItemsPart02.YELLOW_ITEM_FRAME;
    public static final RegistryObject<Item> LIME_ITEM_FRAME = ItemsPart02.LIME_ITEM_FRAME;
    public static final RegistryObject<Item> GREEN_ITEM_FRAME = ItemsPart02.GREEN_ITEM_FRAME;
    public static final RegistryObject<Item> CYAN_ITEM_FRAME = ItemsPart02.CYAN_ITEM_FRAME;
    public static final RegistryObject<Item> LIGHT_BLUE_ITEM_FRAME = ItemsPart02.LIGHT_BLUE_ITEM_FRAME;
    public static final RegistryObject<Item> BLUE_ITEM_FRAME = ItemsPart02.BLUE_ITEM_FRAME;
    public static final RegistryObject<Item> PURPLE_ITEM_FRAME = ItemsPart02.PURPLE_ITEM_FRAME;
    public static final RegistryObject<Item> MAGENTA_ITEM_FRAME = ItemsPart02.MAGENTA_ITEM_FRAME;
    public static final RegistryObject<Item> PINK_ITEM_FRAME = ItemsPart02.PINK_ITEM_FRAME;
    public static final RegistryObject<Item> INVISIBLE_ITEM_FRAME = ItemsPart02.INVISIBLE_ITEM_FRAME;
    public static final RegistryObject<Item> SMOOTH_STONE_STAIRS = ItemsPart02.SMOOTH_STONE_STAIRS;
    public static final RegistryObject<Item> POLISHED_WHITE_CONCRETE = ItemsPart02.POLISHED_WHITE_CONCRETE;
    public static final RegistryObject<Item> POLISHED_ORANGE_CONCRETE = ItemsPart02.POLISHED_ORANGE_CONCRETE;
    public static final RegistryObject<Item> POLISHED_MAGENTA_CONCRETE = ItemsPart02.POLISHED_MAGENTA_CONCRETE;
    public static final RegistryObject<Item> POLISHED_LIGHT_BLUE_CONCRETE = ItemsPart02.POLISHED_LIGHT_BLUE_CONCRETE;
    public static final RegistryObject<Item> POLISHED_YELLOW_CONCRETE = ItemsPart02.POLISHED_YELLOW_CONCRETE;
    public static final RegistryObject<Item> POLISHED_LIME_CONCRETE = ItemsPart02.POLISHED_LIME_CONCRETE;
    public static final RegistryObject<Item> POLISHED_PINK_CONCRETE = ItemsPart02.POLISHED_PINK_CONCRETE;
    public static final RegistryObject<Item> POLISHED_GRAY_CONCRETE = ItemsPart02.POLISHED_GRAY_CONCRETE;
    public static final RegistryObject<Item> POLISHED_LIGHT_GRAY_CONCRETE = ItemsPart02.POLISHED_LIGHT_GRAY_CONCRETE;
    public static final RegistryObject<Item> POLISHED_CYAN_CONCRETE = ItemsPart02.POLISHED_CYAN_CONCRETE;
    public static final RegistryObject<Item> POLISHED_PURPLE_CONCRETE = ItemsPart02.POLISHED_PURPLE_CONCRETE;
    public static final RegistryObject<Item> POLISHED_BLUE_CONCRETE = ItemsPart02.POLISHED_BLUE_CONCRETE;
    public static final RegistryObject<Item> POLISHED_BROWN_CONCRETE = ItemsPart02.POLISHED_BROWN_CONCRETE;
    public static final RegistryObject<Item> POLISHED_GREEN_CONCRETE = ItemsPart02.POLISHED_GREEN_CONCRETE;
    public static final RegistryObject<Item> POLISHED_RED_CONCRETE = ItemsPart02.POLISHED_RED_CONCRETE;
    public static final RegistryObject<Item> POLISHED_BLACK_CONCRETE = ItemsPart02.POLISHED_BLACK_CONCRETE;
    public static final RegistryObject<Item> POLISHED_WHITE_CONCRETE_STAIRS =
            ItemsPart02.POLISHED_WHITE_CONCRETE_STAIRS;
    public static final RegistryObject<Item> POLISHED_WHITE_CONCRETE_SLAB = ItemsPart02.POLISHED_WHITE_CONCRETE_SLAB;
    public static final RegistryObject<Item> POLISHED_WHITE_CONCRETE_WALL = ItemsPart02.POLISHED_WHITE_CONCRETE_WALL;
    public static final RegistryObject<Item> POLISHED_ORANGE_CONCRETE_STAIRS =
            ItemsPart02.POLISHED_ORANGE_CONCRETE_STAIRS;
    public static final RegistryObject<Item> POLISHED_ORANGE_CONCRETE_SLAB = ItemsPart02.POLISHED_ORANGE_CONCRETE_SLAB;
    public static final RegistryObject<Item> POLISHED_ORANGE_CONCRETE_WALL = ItemsPart02.POLISHED_ORANGE_CONCRETE_WALL;
    public static final RegistryObject<Item> POLISHED_MAGENTA_CONCRETE_STAIRS =
            ItemsPart02.POLISHED_MAGENTA_CONCRETE_STAIRS;
    public static final RegistryObject<Item> POLISHED_MAGENTA_CONCRETE_SLAB =
            ItemsPart02.POLISHED_MAGENTA_CONCRETE_SLAB;
    public static final RegistryObject<Item> POLISHED_MAGENTA_CONCRETE_WALL =
            ItemsPart02.POLISHED_MAGENTA_CONCRETE_WALL;
    public static final RegistryObject<Item> POLISHED_LIGHT_BLUE_CONCRETE_STAIRS =
            ItemsPart02.POLISHED_LIGHT_BLUE_CONCRETE_STAIRS;
    public static final RegistryObject<Item> POLISHED_LIGHT_BLUE_CONCRETE_SLAB =
            ItemsPart02.POLISHED_LIGHT_BLUE_CONCRETE_SLAB;
    public static final RegistryObject<Item> POLISHED_LIGHT_BLUE_CONCRETE_WALL =
            ItemsPart02.POLISHED_LIGHT_BLUE_CONCRETE_WALL;
    public static final RegistryObject<Item> POLISHED_YELLOW_CONCRETE_STAIRS =
            ItemsPart02.POLISHED_YELLOW_CONCRETE_STAIRS;
    public static final RegistryObject<Item> POLISHED_YELLOW_CONCRETE_SLAB = ItemsPart02.POLISHED_YELLOW_CONCRETE_SLAB;
    public static final RegistryObject<Item> POLISHED_YELLOW_CONCRETE_WALL = ItemsPart02.POLISHED_YELLOW_CONCRETE_WALL;
    public static final RegistryObject<Item> POLISHED_LIME_CONCRETE_STAIRS = ItemsPart02.POLISHED_LIME_CONCRETE_STAIRS;
    public static final RegistryObject<Item> POLISHED_LIME_CONCRETE_SLAB = ItemsPart02.POLISHED_LIME_CONCRETE_SLAB;
    public static final RegistryObject<Item> POLISHED_LIME_CONCRETE_WALL = ItemsPart02.POLISHED_LIME_CONCRETE_WALL;
    public static final RegistryObject<Item> POLISHED_PINK_CONCRETE_STAIRS = ItemsPart02.POLISHED_PINK_CONCRETE_STAIRS;
    public static final RegistryObject<Item> POLISHED_PINK_CONCRETE_SLAB = ItemsPart02.POLISHED_PINK_CONCRETE_SLAB;
    public static final RegistryObject<Item> POLISHED_PINK_CONCRETE_WALL = ItemsPart02.POLISHED_PINK_CONCRETE_WALL;
    public static final RegistryObject<Item> POLISHED_GRAY_CONCRETE_STAIRS = ItemsPart02.POLISHED_GRAY_CONCRETE_STAIRS;
    public static final RegistryObject<Item> POLISHED_GRAY_CONCRETE_SLAB = ItemsPart02.POLISHED_GRAY_CONCRETE_SLAB;
    public static final RegistryObject<Item> POLISHED_GRAY_CONCRETE_WALL = ItemsPart02.POLISHED_GRAY_CONCRETE_WALL;
    public static final RegistryObject<Item> POLISHED_LIGHT_GRAY_CONCRETE_STAIRS =
            ItemsPart02.POLISHED_LIGHT_GRAY_CONCRETE_STAIRS;
    public static final RegistryObject<Item> POLISHED_LIGHT_GRAY_CONCRETE_SLAB =
            ItemsPart02.POLISHED_LIGHT_GRAY_CONCRETE_SLAB;
    public static final RegistryObject<Item> POLISHED_LIGHT_GRAY_CONCRETE_WALL =
            ItemsPart02.POLISHED_LIGHT_GRAY_CONCRETE_WALL;
    public static final RegistryObject<Item> POLISHED_CYAN_CONCRETE_STAIRS = ItemsPart02.POLISHED_CYAN_CONCRETE_STAIRS;
    public static final RegistryObject<Item> POLISHED_CYAN_CONCRETE_SLAB = ItemsPart02.POLISHED_CYAN_CONCRETE_SLAB;
    public static final RegistryObject<Item> POLISHED_CYAN_CONCRETE_WALL = ItemsPart02.POLISHED_CYAN_CONCRETE_WALL;
    public static final RegistryObject<Item> POLISHED_PURPLE_CONCRETE_STAIRS =
            ItemsPart02.POLISHED_PURPLE_CONCRETE_STAIRS;
    public static final RegistryObject<Item> POLISHED_PURPLE_CONCRETE_SLAB = ItemsPart02.POLISHED_PURPLE_CONCRETE_SLAB;
    public static final RegistryObject<Item> POLISHED_PURPLE_CONCRETE_WALL = ItemsPart02.POLISHED_PURPLE_CONCRETE_WALL;
    public static final RegistryObject<Item> POLISHED_BLUE_CONCRETE_STAIRS = ItemsPart02.POLISHED_BLUE_CONCRETE_STAIRS;
    public static final RegistryObject<Item> POLISHED_BLUE_CONCRETE_SLAB = ItemsPart02.POLISHED_BLUE_CONCRETE_SLAB;
    public static final RegistryObject<Item> POLISHED_BLUE_CONCRETE_WALL = ItemsPart02.POLISHED_BLUE_CONCRETE_WALL;
    public static final RegistryObject<Item> POLISHED_BROWN_CONCRETE_STAIRS =
            ItemsPart02.POLISHED_BROWN_CONCRETE_STAIRS;
    public static final RegistryObject<Item> POLISHED_BROWN_CONCRETE_SLAB = ItemsPart02.POLISHED_BROWN_CONCRETE_SLAB;
    public static final RegistryObject<Item> POLISHED_BROWN_CONCRETE_WALL = ItemsPart02.POLISHED_BROWN_CONCRETE_WALL;
    public static final RegistryObject<Item> POLISHED_GREEN_CONCRETE_STAIRS =
            ItemsPart02.POLISHED_GREEN_CONCRETE_STAIRS;
    public static final RegistryObject<Item> POLISHED_GREEN_CONCRETE_SLAB = ItemsPart02.POLISHED_GREEN_CONCRETE_SLAB;
    public static final RegistryObject<Item> POLISHED_GREEN_CONCRETE_WALL = ItemsPart02.POLISHED_GREEN_CONCRETE_WALL;
    public static final RegistryObject<Item> POLISHED_RED_CONCRETE_STAIRS = ItemsPart02.POLISHED_RED_CONCRETE_STAIRS;
    public static final RegistryObject<Item> POLISHED_RED_CONCRETE_SLAB = ItemsPart02.POLISHED_RED_CONCRETE_SLAB;
    public static final RegistryObject<Item> POLISHED_RED_CONCRETE_WALL = ItemsPart02.POLISHED_RED_CONCRETE_WALL;
    public static final RegistryObject<Item> POLISHED_BLACK_CONCRETE_STAIRS =
            ItemsPart02.POLISHED_BLACK_CONCRETE_STAIRS;
    public static final RegistryObject<Item> POLISHED_BLACK_CONCRETE_SLAB = ItemsPart02.POLISHED_BLACK_CONCRETE_SLAB;
    public static final RegistryObject<Item> POLISHED_BLACK_CONCRETE_WALL = ItemsPart02.POLISHED_BLACK_CONCRETE_WALL;
    public static final RegistryObject<Item> SMOKE_VENT = ItemsPart02.SMOKE_VENT;
    public static final RegistryObject<Item> WHITE_REDSTONE_LAMP = ItemsPart02.WHITE_REDSTONE_LAMP;
    public static final RegistryObject<Item> ORANGE_REDSTONE_LAMP = ItemsPart02.ORANGE_REDSTONE_LAMP;
    public static final RegistryObject<Item> MAGENTA_REDSTONE_LAMP = ItemsPart02.MAGENTA_REDSTONE_LAMP;
    public static final RegistryObject<Item> LIGHT_BLUE_REDSTONE_LAMP = ItemsPart02.LIGHT_BLUE_REDSTONE_LAMP;
    public static final RegistryObject<Item> YELLOW_REDSTONE_LAMP = ItemsPart02.YELLOW_REDSTONE_LAMP;
    public static final RegistryObject<Item> LIME_REDSTONE_LAMP = ItemsPart02.LIME_REDSTONE_LAMP;
    public static final RegistryObject<Item> PINK_REDSTONE_LAMP = ItemsPart02.PINK_REDSTONE_LAMP;
    public static final RegistryObject<Item> GRAY_REDSTONE_LAMP = ItemsPart02.GRAY_REDSTONE_LAMP;
    public static final RegistryObject<Item> LIGHT_GRAY_REDSTONE_LAMP = ItemsPart02.LIGHT_GRAY_REDSTONE_LAMP;
    public static final RegistryObject<Item> CYAN_REDSTONE_LAMP = ItemsPart02.CYAN_REDSTONE_LAMP;
    public static final RegistryObject<Item> PURPLE_REDSTONE_LAMP = ItemsPart02.PURPLE_REDSTONE_LAMP;
    public static final RegistryObject<Item> BLUE_REDSTONE_LAMP = ItemsPart02.BLUE_REDSTONE_LAMP;
    public static final RegistryObject<Item> BROWN_REDSTONE_LAMP = ItemsPart02.BROWN_REDSTONE_LAMP;
    public static final RegistryObject<Item> GREEN_REDSTONE_LAMP = ItemsPart02.GREEN_REDSTONE_LAMP;
    public static final RegistryObject<Item> RED_REDSTONE_LAMP = ItemsPart02.RED_REDSTONE_LAMP;
    public static final RegistryObject<Item> BLACK_REDSTONE_LAMP = ItemsPart02.BLACK_REDSTONE_LAMP;
    public static final RegistryObject<Item> CASCADE_BLOCK = ItemsPart02.CASCADE_BLOCK;
    public static final RegistryObject<Item> CASCADE_BLOCK_NO_MIST = ItemsPart03.CASCADE_BLOCK_NO_MIST;
    public static final RegistryObject<Item> BOTTLE_OF_MIST = ItemsPart03.BOTTLE_OF_MIST;
    public static final RegistryObject<Item> BLACK_FACTORY_MESH = ItemsPart03.BLACK_FACTORY_MESH;
    public static final RegistryObject<Item> BLACK_FACTORY_MESH_STAIRS = ItemsPart03.BLACK_FACTORY_MESH_STAIRS;
    public static final RegistryObject<Item> BLACK_FACTORY_MESH_SLAB = ItemsPart03.BLACK_FACTORY_MESH_SLAB;
    public static final RegistryObject<Item> BLACK_FACTORY_MESH_WALL = ItemsPart03.BLACK_FACTORY_MESH_WALL;
    public static final RegistryObject<Item> BLUE_FACTORY_MESH = ItemsPart03.BLUE_FACTORY_MESH;
    public static final RegistryObject<Item> BLUE_FACTORY_MESH_STAIRS = ItemsPart03.BLUE_FACTORY_MESH_STAIRS;
    public static final RegistryObject<Item> BLUE_FACTORY_MESH_SLAB = ItemsPart03.BLUE_FACTORY_MESH_SLAB;
    public static final RegistryObject<Item> BLUE_FACTORY_MESH_WALL = ItemsPart03.BLUE_FACTORY_MESH_WALL;
    public static final RegistryObject<Item> BROWN_FACTORY_MESH = ItemsPart03.BROWN_FACTORY_MESH;
    public static final RegistryObject<Item> BROWN_FACTORY_MESH_STAIRS = ItemsPart03.BROWN_FACTORY_MESH_STAIRS;
    public static final RegistryObject<Item> BROWN_FACTORY_MESH_SLAB = ItemsPart03.BROWN_FACTORY_MESH_SLAB;
    public static final RegistryObject<Item> BROWN_FACTORY_MESH_WALL = ItemsPart03.BROWN_FACTORY_MESH_WALL;
    public static final RegistryObject<Item> GRAY_FACTORY_MESH = ItemsPart03.GRAY_FACTORY_MESH;
    public static final RegistryObject<Item> GRAY_FACTORY_MESH_STAIRS = ItemsPart03.GRAY_FACTORY_MESH_STAIRS;
    public static final RegistryObject<Item> GRAY_FACTORY_MESH_SLAB = ItemsPart03.GRAY_FACTORY_MESH_SLAB;
    public static final RegistryObject<Item> GRAY_FACTORY_MESH_WALL = ItemsPart03.GRAY_FACTORY_MESH_WALL;
    public static final RegistryObject<Item> GREEN_FACTORY_MESH = ItemsPart03.GREEN_FACTORY_MESH;
    public static final RegistryObject<Item> GREEN_FACTORY_MESH_STAIRS = ItemsPart03.GREEN_FACTORY_MESH_STAIRS;
    public static final RegistryObject<Item> GREEN_FACTORY_MESH_SLAB = ItemsPart03.GREEN_FACTORY_MESH_SLAB;
    public static final RegistryObject<Item> GREEN_FACTORY_MESH_WALL = ItemsPart03.GREEN_FACTORY_MESH_WALL;
    public static final RegistryObject<Item> LIGHT_GRAY_FACTORY_MESH = ItemsPart03.LIGHT_GRAY_FACTORY_MESH;
    public static final RegistryObject<Item> LIGHT_GRAY_FACTORY_MESH_STAIRS =
            ItemsPart03.LIGHT_GRAY_FACTORY_MESH_STAIRS;
    public static final RegistryObject<Item> LIGHT_GRAY_FACTORY_MESH_SLAB = ItemsPart03.LIGHT_GRAY_FACTORY_MESH_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_FACTORY_MESH_WALL = ItemsPart03.LIGHT_GRAY_FACTORY_MESH_WALL;
    public static final RegistryObject<Item> LIME_FACTORY_MESH = ItemsPart03.LIME_FACTORY_MESH;
    public static final RegistryObject<Item> LIME_FACTORY_MESH_STAIRS = ItemsPart03.LIME_FACTORY_MESH_STAIRS;
    public static final RegistryObject<Item> LIME_FACTORY_MESH_SLAB = ItemsPart03.LIME_FACTORY_MESH_SLAB;
    public static final RegistryObject<Item> LIME_FACTORY_MESH_WALL = ItemsPart03.LIME_FACTORY_MESH_WALL;
    public static final RegistryObject<Item> ORANGE_FACTORY_MESH = ItemsPart03.ORANGE_FACTORY_MESH;
    public static final RegistryObject<Item> ORANGE_FACTORY_MESH_STAIRS = ItemsPart03.ORANGE_FACTORY_MESH_STAIRS;
    public static final RegistryObject<Item> ORANGE_FACTORY_MESH_SLAB = ItemsPart03.ORANGE_FACTORY_MESH_SLAB;
    public static final RegistryObject<Item> ORANGE_FACTORY_MESH_WALL = ItemsPart03.ORANGE_FACTORY_MESH_WALL;
    public static final RegistryObject<Item> RED_FACTORY_MESH = ItemsPart03.RED_FACTORY_MESH;
    public static final RegistryObject<Item> RED_FACTORY_MESH_STAIRS = ItemsPart03.RED_FACTORY_MESH_STAIRS;
    public static final RegistryObject<Item> RED_FACTORY_MESH_SLAB = ItemsPart03.RED_FACTORY_MESH_SLAB;
    public static final RegistryObject<Item> RED_FACTORY_MESH_WALL = ItemsPart03.RED_FACTORY_MESH_WALL;
    public static final RegistryObject<Item> YELLOW_FACTORY_MESH = ItemsPart03.YELLOW_FACTORY_MESH;
    public static final RegistryObject<Item> YELLOW_FACTORY_MESH_STAIRS = ItemsPart03.YELLOW_FACTORY_MESH_STAIRS;
    public static final RegistryObject<Item> YELLOW_FACTORY_MESH_SLAB = ItemsPart03.YELLOW_FACTORY_MESH_SLAB;
    public static final RegistryObject<Item> YELLOW_FACTORY_MESH_WALL = ItemsPart03.YELLOW_FACTORY_MESH_WALL;
    public static final RegistryObject<Item> PACKED_MUD = ItemsPart03.PACKED_MUD;
    public static final RegistryObject<Item> PACKED_MUD_STAIRS = ItemsPart03.PACKED_MUD_STAIRS;
    public static final RegistryObject<Item> PACKED_MUD_SLAB = ItemsPart03.PACKED_MUD_SLAB;
    public static final RegistryObject<Item> PACKED_MUD_WALL = ItemsPart03.PACKED_MUD_WALL;
    public static final RegistryObject<Item> MUD_BRICKS = ItemsPart03.MUD_BRICKS;
    public static final RegistryObject<Item> MUD_BRICK_STAIRS = ItemsPart03.MUD_BRICK_STAIRS;
    public static final RegistryObject<Item> MUD_BRICK_SLAB = ItemsPart03.MUD_BRICK_SLAB;
    public static final RegistryObject<Item> MUD_BRICK_WALL = ItemsPart03.MUD_BRICK_WALL;
    public static final RegistryObject<Item> ANCIENT_ASHEN_SCROLL = ItemsPart03.ANCIENT_ASHEN_SCROLL;
    public static final RegistryObject<Item> GLASS_SLAB = ItemsPart03.GLASS_SLAB;
    public static final RegistryObject<Item> WHITE_STAINED_GLASS_SLAB = ItemsPart03.WHITE_STAINED_GLASS_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_GLASS_SLAB = ItemsPart03.LIGHT_GRAY_STAINED_GLASS_SLAB;
    public static final RegistryObject<Item> GRAY_STAINED_GLASS_SLAB = ItemsPart03.GRAY_STAINED_GLASS_SLAB;
    public static final RegistryObject<Item> BLACK_STAINED_GLASS_SLAB = ItemsPart03.BLACK_STAINED_GLASS_SLAB;
    public static final RegistryObject<Item> BROWN_STAINED_GLASS_SLAB = ItemsPart03.BROWN_STAINED_GLASS_SLAB;
    public static final RegistryObject<Item> RED_STAINED_GLASS_SLAB = ItemsPart03.RED_STAINED_GLASS_SLAB;
    public static final RegistryObject<Item> ORANGE_STAINED_GLASS_SLAB = ItemsPart03.ORANGE_STAINED_GLASS_SLAB;
    public static final RegistryObject<Item> YELLOW_STAINED_GLASS_SLAB = ItemsPart03.YELLOW_STAINED_GLASS_SLAB;
    public static final RegistryObject<Item> LIME_STAINED_GLASS_SLAB = ItemsPart03.LIME_STAINED_GLASS_SLAB;
    public static final RegistryObject<Item> GREEN_STAINED_GLASS_SLAB = ItemsPart03.GREEN_STAINED_GLASS_SLAB;
    public static final RegistryObject<Item> CYAN_STAINED_GLASS_SLAB = ItemsPart03.CYAN_STAINED_GLASS_SLAB;
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_GLASS_SLAB = ItemsPart03.LIGHT_BLUE_STAINED_GLASS_SLAB;
    public static final RegistryObject<Item> BLUE_STAINED_GLASS_SLAB = ItemsPart03.BLUE_STAINED_GLASS_SLAB;
    public static final RegistryObject<Item> PURPLE_STAINED_GLASS_SLAB = ItemsPart03.PURPLE_STAINED_GLASS_SLAB;
    public static final RegistryObject<Item> MAGENTA_STAINED_GLASS_SLAB = ItemsPart03.MAGENTA_STAINED_GLASS_SLAB;
    public static final RegistryObject<Item> PINK_STAINED_GLASS_SLAB = ItemsPart03.PINK_STAINED_GLASS_SLAB;
    public static final RegistryObject<Item> GLASS_STAIRS = ItemsPart03.GLASS_STAIRS;
    public static final RegistryObject<Item> WHITE_STAINED_GLASS_STAIRS = ItemsPart03.WHITE_STAINED_GLASS_STAIRS;
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_GLASS_STAIRS =
            ItemsPart03.LIGHT_GRAY_STAINED_GLASS_STAIRS;
    public static final RegistryObject<Item> GRAY_STAINED_GLASS_STAIRS = ItemsPart03.GRAY_STAINED_GLASS_STAIRS;
    public static final RegistryObject<Item> BLACK_STAINED_GLASS_STAIRS = ItemsPart03.BLACK_STAINED_GLASS_STAIRS;
    public static final RegistryObject<Item> BROWN_STAINED_GLASS_STAIRS = ItemsPart03.BROWN_STAINED_GLASS_STAIRS;
    public static final RegistryObject<Item> RED_STAINED_GLASS_STAIRS = ItemsPart03.RED_STAINED_GLASS_STAIRS;
    public static final RegistryObject<Item> ORANGE_STAINED_GLASS_STAIRS = ItemsPart03.ORANGE_STAINED_GLASS_STAIRS;
    public static final RegistryObject<Item> YELLOW_STAINED_GLASS_STAIRS = ItemsPart03.YELLOW_STAINED_GLASS_STAIRS;
    public static final RegistryObject<Item> LIME_STAINED_GLASS_STAIRS = ItemsPart03.LIME_STAINED_GLASS_STAIRS;
    public static final RegistryObject<Item> GREEN_STAINED_GLASS_STAIRS = ItemsPart03.GREEN_STAINED_GLASS_STAIRS;
    public static final RegistryObject<Item> CYAN_STAINED_GLASS_STAIRS = ItemsPart03.CYAN_STAINED_GLASS_STAIRS;
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_GLASS_STAIRS =
            ItemsPart03.LIGHT_BLUE_STAINED_GLASS_STAIRS;
    public static final RegistryObject<Item> BLUE_STAINED_GLASS_STAIRS = ItemsPart03.BLUE_STAINED_GLASS_STAIRS;
    public static final RegistryObject<Item> PURPLE_STAINED_GLASS_STAIRS = ItemsPart03.PURPLE_STAINED_GLASS_STAIRS;
    public static final RegistryObject<Item> MAGENTA_STAINED_GLASS_STAIRS = ItemsPart03.MAGENTA_STAINED_GLASS_STAIRS;
    public static final RegistryObject<Item> PINK_STAINED_GLASS_STAIRS = ItemsPart03.PINK_STAINED_GLASS_STAIRS;
    public static final RegistryObject<Item> WHITE_MOSAIC_GLASS_SLAB = ItemsPart03.WHITE_MOSAIC_GLASS_SLAB;
    public static final RegistryObject<Item> WHITE_MOSAIC_GLASS_STAIRS = ItemsPart03.WHITE_MOSAIC_GLASS_STAIRS;
    public static final RegistryObject<Item> LIGHT_GRAY_MOSAIC_GLASS_SLAB = ItemsPart03.LIGHT_GRAY_MOSAIC_GLASS_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_MOSAIC_GLASS_STAIRS =
            ItemsPart03.LIGHT_GRAY_MOSAIC_GLASS_STAIRS;
    public static final RegistryObject<Item> GRAY_MOSAIC_GLASS_SLAB = ItemsPart03.GRAY_MOSAIC_GLASS_SLAB;
    public static final RegistryObject<Item> GRAY_MOSAIC_GLASS_STAIRS = ItemsPart03.GRAY_MOSAIC_GLASS_STAIRS;
    public static final RegistryObject<Item> BLACK_MOSAIC_GLASS_SLAB = ItemsPart03.BLACK_MOSAIC_GLASS_SLAB;
    public static final RegistryObject<Item> BLACK_MOSAIC_GLASS_STAIRS = ItemsPart03.BLACK_MOSAIC_GLASS_STAIRS;
    public static final RegistryObject<Item> BROWN_MOSAIC_GLASS_SLAB = ItemsPart03.BROWN_MOSAIC_GLASS_SLAB;
    public static final RegistryObject<Item> BROWN_MOSAIC_GLASS_STAIRS = ItemsPart03.BROWN_MOSAIC_GLASS_STAIRS;
    public static final RegistryObject<Item> RED_MOSAIC_GLASS_SLAB = ItemsPart03.RED_MOSAIC_GLASS_SLAB;
    public static final RegistryObject<Item> RED_MOSAIC_GLASS_STAIRS = ItemsPart03.RED_MOSAIC_GLASS_STAIRS;
    public static final RegistryObject<Item> ORANGE_MOSAIC_GLASS_SLAB = ItemsPart03.ORANGE_MOSAIC_GLASS_SLAB;
    public static final RegistryObject<Item> ORANGE_MOSAIC_GLASS_STAIRS = ItemsPart03.ORANGE_MOSAIC_GLASS_STAIRS;
    public static final RegistryObject<Item> YELLOW_MOSAIC_GLASS_SLAB = ItemsPart03.YELLOW_MOSAIC_GLASS_SLAB;
    public static final RegistryObject<Item> YELLOW_MOSAIC_GLASS_STAIRS = ItemsPart03.YELLOW_MOSAIC_GLASS_STAIRS;
    public static final RegistryObject<Item> LIME_MOSAIC_GLASS_SLAB = ItemsPart03.LIME_MOSAIC_GLASS_SLAB;
    public static final RegistryObject<Item> LIME_MOSAIC_GLASS_STAIRS = ItemsPart03.LIME_MOSAIC_GLASS_STAIRS;
    public static final RegistryObject<Item> GREEN_MOSAIC_GLASS_SLAB = ItemsPart03.GREEN_MOSAIC_GLASS_SLAB;
    public static final RegistryObject<Item> GREEN_MOSAIC_GLASS_STAIRS = ItemsPart03.GREEN_MOSAIC_GLASS_STAIRS;
    public static final RegistryObject<Item> CYAN_MOSAIC_GLASS_SLAB = ItemsPart03.CYAN_MOSAIC_GLASS_SLAB;
    public static final RegistryObject<Item> CYAN_MOSAIC_GLASS_STAIRS = ItemsPart03.CYAN_MOSAIC_GLASS_STAIRS;
    public static final RegistryObject<Item> LIGHT_BLUE_MOSAIC_GLASS_SLAB = ItemsPart03.LIGHT_BLUE_MOSAIC_GLASS_SLAB;
    public static final RegistryObject<Item> LIGHT_BLUE_MOSAIC_GLASS_STAIRS =
            ItemsPart03.LIGHT_BLUE_MOSAIC_GLASS_STAIRS;
    public static final RegistryObject<Item> BLUE_MOSAIC_GLASS_SLAB = ItemsPart03.BLUE_MOSAIC_GLASS_SLAB;
    public static final RegistryObject<Item> BLUE_MOSAIC_GLASS_STAIRS = ItemsPart03.BLUE_MOSAIC_GLASS_STAIRS;
    public static final RegistryObject<Item> PURPLE_MOSAIC_GLASS_SLAB = ItemsPart03.PURPLE_MOSAIC_GLASS_SLAB;
    public static final RegistryObject<Item> PURPLE_MOSAIC_GLASS_STAIRS = ItemsPart03.PURPLE_MOSAIC_GLASS_STAIRS;
    public static final RegistryObject<Item> MAGENTA_MOSAIC_GLASS_SLAB = ItemsPart03.MAGENTA_MOSAIC_GLASS_SLAB;
    public static final RegistryObject<Item> MAGENTA_MOSAIC_GLASS_STAIRS = ItemsPart03.MAGENTA_MOSAIC_GLASS_STAIRS;
    public static final RegistryObject<Item> PINK_MOSAIC_GLASS_SLAB = ItemsPart03.PINK_MOSAIC_GLASS_SLAB;
    public static final RegistryObject<Item> PINK_MOSAIC_GLASS_STAIRS = ItemsPart03.PINK_MOSAIC_GLASS_STAIRS;
    public static final RegistryObject<Item> FACTORY_WHITE_GLASS_SLAB = ItemsPart03.FACTORY_WHITE_GLASS_SLAB;
    public static final RegistryObject<Item> FACTORY_WHITE_GLASS_STAIRS = ItemsPart03.FACTORY_WHITE_GLASS_STAIRS;
    public static final RegistryObject<Item> FACTORY_LIGHT_GRAY_GLASS_SLAB = ItemsPart03.FACTORY_LIGHT_GRAY_GLASS_SLAB;
    public static final RegistryObject<Item> FACTORY_LIGHT_GRAY_GLASS_STAIRS =
            ItemsPart03.FACTORY_LIGHT_GRAY_GLASS_STAIRS;
    public static final RegistryObject<Item> FACTORY_GRAY_GLASS_SLAB = ItemsPart03.FACTORY_GRAY_GLASS_SLAB;
    public static final RegistryObject<Item> FACTORY_GRAY_GLASS_STAIRS = ItemsPart03.FACTORY_GRAY_GLASS_STAIRS;
    public static final RegistryObject<Item> FACTORY_BLACK_GLASS_SLAB = ItemsPart03.FACTORY_BLACK_GLASS_SLAB;
    public static final RegistryObject<Item> FACTORY_BLACK_GLASS_STAIRS = ItemsPart03.FACTORY_BLACK_GLASS_STAIRS;
    public static final RegistryObject<Item> FACTORY_BROWN_GLASS_SLAB = ItemsPart03.FACTORY_BROWN_GLASS_SLAB;
    public static final RegistryObject<Item> FACTORY_BROWN_GLASS_STAIRS = ItemsPart03.FACTORY_BROWN_GLASS_STAIRS;
    public static final RegistryObject<Item> FACTORY_RED_GLASS_SLAB = ItemsPart03.FACTORY_RED_GLASS_SLAB;
    public static final RegistryObject<Item> FACTORY_RED_GLASS_STAIRS = ItemsPart03.FACTORY_RED_GLASS_STAIRS;
    public static final RegistryObject<Item> FACTORY_ORANGE_GLASS_SLAB = ItemsPart03.FACTORY_ORANGE_GLASS_SLAB;
    public static final RegistryObject<Item> FACTORY_ORANGE_GLASS_STAIRS = ItemsPart03.FACTORY_ORANGE_GLASS_STAIRS;
    public static final RegistryObject<Item> FACTORY_YELLOW_GLASS_SLAB = ItemsPart03.FACTORY_YELLOW_GLASS_SLAB;
    public static final RegistryObject<Item> FACTORY_YELLOW_GLASS_STAIRS = ItemsPart03.FACTORY_YELLOW_GLASS_STAIRS;
    public static final RegistryObject<Item> FACTORY_LIME_GLASS_SLAB = ItemsPart03.FACTORY_LIME_GLASS_SLAB;
    public static final RegistryObject<Item> FACTORY_LIME_GLASS_STAIRS = ItemsPart03.FACTORY_LIME_GLASS_STAIRS;
    public static final RegistryObject<Item> FACTORY_GREEN_GLASS_SLAB = ItemsPart03.FACTORY_GREEN_GLASS_SLAB;
    public static final RegistryObject<Item> FACTORY_GREEN_GLASS_STAIRS = ItemsPart03.FACTORY_GREEN_GLASS_STAIRS;
    public static final RegistryObject<Item> FACTORY_CYAN_GLASS_SLAB = ItemsPart03.FACTORY_CYAN_GLASS_SLAB;
    public static final RegistryObject<Item> FACTORY_CYAN_GLASS_STAIRS = ItemsPart03.FACTORY_CYAN_GLASS_STAIRS;
    public static final RegistryObject<Item> FACTORY_LIGHT_BLUE_GLASS_SLAB = ItemsPart03.FACTORY_LIGHT_BLUE_GLASS_SLAB;
    public static final RegistryObject<Item> FACTORY_LIGHT_BLUE_GLASS_STAIRS =
            ItemsPart03.FACTORY_LIGHT_BLUE_GLASS_STAIRS;
    public static final RegistryObject<Item> FACTORY_BLUE_GLASS_SLAB = ItemsPart03.FACTORY_BLUE_GLASS_SLAB;
    public static final RegistryObject<Item> FACTORY_BLUE_GLASS_STAIRS = ItemsPart03.FACTORY_BLUE_GLASS_STAIRS;
    public static final RegistryObject<Item> FACTORY_PURPLE_GLASS_SLAB = ItemsPart03.FACTORY_PURPLE_GLASS_SLAB;
    public static final RegistryObject<Item> FACTORY_PURPLE_GLASS_STAIRS = ItemsPart03.FACTORY_PURPLE_GLASS_STAIRS;
    public static final RegistryObject<Item> FACTORY_MAGENTA_GLASS_SLAB = ItemsPart03.FACTORY_MAGENTA_GLASS_SLAB;
    public static final RegistryObject<Item> FACTORY_MAGENTA_GLASS_STAIRS = ItemsPart03.FACTORY_MAGENTA_GLASS_STAIRS;
    public static final RegistryObject<Item> FACTORY_PINK_GLASS_SLAB = ItemsPart03.FACTORY_PINK_GLASS_SLAB;
    public static final RegistryObject<Item> FACTORY_PINK_GLASS_STAIRS = ItemsPart03.FACTORY_PINK_GLASS_STAIRS;
    public static final RegistryObject<Item> WHITE_GLAZED_GLASS_SLAB = ItemsPart03.WHITE_GLAZED_GLASS_SLAB;
    public static final RegistryObject<Item> WHITE_GLAZED_GLASS_STAIRS = ItemsPart03.WHITE_GLAZED_GLASS_STAIRS;
    public static final RegistryObject<Item> LIGHT_GRAY_GLAZED_GLASS_SLAB = ItemsPart03.LIGHT_GRAY_GLAZED_GLASS_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_GLAZED_GLASS_STAIRS =
            ItemsPart03.LIGHT_GRAY_GLAZED_GLASS_STAIRS;
    public static final RegistryObject<Item> GRAY_GLAZED_GLASS_SLAB = ItemsPart03.GRAY_GLAZED_GLASS_SLAB;
    public static final RegistryObject<Item> GRAY_GLAZED_GLASS_STAIRS = ItemsPart03.GRAY_GLAZED_GLASS_STAIRS;
    public static final RegistryObject<Item> BLACK_GLAZED_GLASS_SLAB = ItemsPart03.BLACK_GLAZED_GLASS_SLAB;
    public static final RegistryObject<Item> BLACK_GLAZED_GLASS_STAIRS = ItemsPart03.BLACK_GLAZED_GLASS_STAIRS;
    public static final RegistryObject<Item> BROWN_GLAZED_GLASS_SLAB = ItemsPart03.BROWN_GLAZED_GLASS_SLAB;
    public static final RegistryObject<Item> BROWN_GLAZED_GLASS_STAIRS = ItemsPart03.BROWN_GLAZED_GLASS_STAIRS;
    public static final RegistryObject<Item> RED_GLAZED_GLASS_SLAB = ItemsPart03.RED_GLAZED_GLASS_SLAB;
    public static final RegistryObject<Item> RED_GLAZED_GLASS_STAIRS = ItemsPart03.RED_GLAZED_GLASS_STAIRS;
    public static final RegistryObject<Item> ORANGE_GLAZED_GLASS_SLAB = ItemsPart03.ORANGE_GLAZED_GLASS_SLAB;
    public static final RegistryObject<Item> ORANGE_GLAZED_GLASS_STAIRS = ItemsPart03.ORANGE_GLAZED_GLASS_STAIRS;
    public static final RegistryObject<Item> YELLOW_GLAZED_GLASS_SLAB = ItemsPart03.YELLOW_GLAZED_GLASS_SLAB;
    public static final RegistryObject<Item> YELLOW_GLAZED_GLASS_STAIRS = ItemsPart03.YELLOW_GLAZED_GLASS_STAIRS;
    public static final RegistryObject<Item> LIME_GLAZED_GLASS_SLAB = ItemsPart03.LIME_GLAZED_GLASS_SLAB;
    public static final RegistryObject<Item> LIME_GLAZED_GLASS_STAIRS = ItemsPart03.LIME_GLAZED_GLASS_STAIRS;
    public static final RegistryObject<Item> GREEN_GLAZED_GLASS_SLAB = ItemsPart03.GREEN_GLAZED_GLASS_SLAB;
    public static final RegistryObject<Item> GREEN_GLAZED_GLASS_STAIRS = ItemsPart03.GREEN_GLAZED_GLASS_STAIRS;
    public static final RegistryObject<Item> CYAN_GLAZED_GLASS_SLAB = ItemsPart03.CYAN_GLAZED_GLASS_SLAB;
    public static final RegistryObject<Item> CYAN_GLAZED_GLASS_STAIRS = ItemsPart03.CYAN_GLAZED_GLASS_STAIRS;
    public static final RegistryObject<Item> LIGHT_BLUE_GLAZED_GLASS_SLAB = ItemsPart03.LIGHT_BLUE_GLAZED_GLASS_SLAB;
    public static final RegistryObject<Item> LIGHT_BLUE_GLAZED_GLASS_STAIRS =
            ItemsPart03.LIGHT_BLUE_GLAZED_GLASS_STAIRS;
    public static final RegistryObject<Item> BLUE_GLAZED_GLASS_SLAB = ItemsPart03.BLUE_GLAZED_GLASS_SLAB;
    public static final RegistryObject<Item> BLUE_GLAZED_GLASS_STAIRS = ItemsPart03.BLUE_GLAZED_GLASS_STAIRS;
    public static final RegistryObject<Item> PURPLE_GLAZED_GLASS_SLAB = ItemsPart03.PURPLE_GLAZED_GLASS_SLAB;
    public static final RegistryObject<Item> PURPLE_GLAZED_GLASS_STAIRS = ItemsPart03.PURPLE_GLAZED_GLASS_STAIRS;
    public static final RegistryObject<Item> MAGENTA_GLAZED_GLASS_SLAB = ItemsPart03.MAGENTA_GLAZED_GLASS_SLAB;
    public static final RegistryObject<Item> MAGENTA_GLAZED_GLASS_STAIRS = ItemsPart03.MAGENTA_GLAZED_GLASS_STAIRS;
    public static final RegistryObject<Item> PINK_GLAZED_GLASS_SLAB = ItemsPart03.PINK_GLAZED_GLASS_SLAB;
    public static final RegistryObject<Item> PINK_GLAZED_GLASS_STAIRS = ItemsPart03.PINK_GLAZED_GLASS_STAIRS;
    public static final RegistryObject<Item> OAK_LOG_SLAB = ItemsPart03.OAK_LOG_SLAB;
    public static final RegistryObject<Item> STRIPPED_OAK_LOG_SLAB = ItemsPart03.STRIPPED_OAK_LOG_SLAB;
    public static final RegistryObject<Item> SPRUCE_LOG_SLAB = ItemsPart03.SPRUCE_LOG_SLAB;
    public static final RegistryObject<Item> STRIPPED_SPRUCE_LOG_SLAB = ItemsPart03.STRIPPED_SPRUCE_LOG_SLAB;
    public static final RegistryObject<Item> BIRCH_LOG_SLAB = ItemsPart03.BIRCH_LOG_SLAB;
    public static final RegistryObject<Item> STRIPPED_BIRCH_LOG_SLAB = ItemsPart03.STRIPPED_BIRCH_LOG_SLAB;
    public static final RegistryObject<Item> JUNGLE_LOG_SLAB = ItemsPart03.JUNGLE_LOG_SLAB;
    public static final RegistryObject<Item> STRIPPED_JUNGLE_LOG_SLAB = ItemsPart03.STRIPPED_JUNGLE_LOG_SLAB;
    public static final RegistryObject<Item> ACACIA_LOG_SLAB = ItemsPart03.ACACIA_LOG_SLAB;
    public static final RegistryObject<Item> STRIPPED_ACACIA_LOG_SLAB = ItemsPart03.STRIPPED_ACACIA_LOG_SLAB;
    public static final RegistryObject<Item> DARK_OAK_LOG_SLAB = ItemsPart03.DARK_OAK_LOG_SLAB;
    public static final RegistryObject<Item> STRIPPED_DARK_OAK_LOG_SLAB = ItemsPart03.STRIPPED_DARK_OAK_LOG_SLAB;
    public static final RegistryObject<Item> CRIMSON_STEM_SLAB = ItemsPart03.CRIMSON_STEM_SLAB;
    public static final RegistryObject<Item> STRIPPED_CRIMSON_STEM_SLAB = ItemsPart03.STRIPPED_CRIMSON_STEM_SLAB;
    public static final RegistryObject<Item> WARPED_STEM_SLAB = ItemsPart03.WARPED_STEM_SLAB;
    public static final RegistryObject<Item> STRIPPED_WARPED_STEM_SLAB = ItemsPart03.STRIPPED_WARPED_STEM_SLAB;
    public static final RegistryObject<Item> MANGROVE_LOG_SLAB = ItemsPart03.MANGROVE_LOG_SLAB;
    public static final RegistryObject<Item> MANGROVE_LOG_STAIRS = ItemsPart03.MANGROVE_LOG_STAIRS;
    public static final RegistryObject<Item> STRIPPED_MANGROVE_LOG_SLAB = ItemsPart03.STRIPPED_MANGROVE_LOG_SLAB;
    public static final RegistryObject<Item> STRIPPED_MANGROVE_LOG_STAIRS = ItemsPart03.STRIPPED_MANGROVE_LOG_STAIRS;
    public static final RegistryObject<Item> ASHPEN_LOG = ItemsPart03.ASHPEN_LOG;
    public static final RegistryObject<Item> ASHPEN_WOOD = ItemsPart03.ASHPEN_WOOD;
    public static final RegistryObject<Item> ASHPEN_LOG_SLAB = ItemsPart03.ASHPEN_LOG_SLAB;
    public static final RegistryObject<Item> ASHPEN_LOG_STAIRS = ItemsPart03.ASHPEN_LOG_STAIRS;
    public static final RegistryObject<Item> ASHPEN_WOOD_SLAB = ItemsPart03.ASHPEN_WOOD_SLAB;
    public static final RegistryObject<Item> ASHPEN_WOOD_STAIRS = ItemsPart03.ASHPEN_WOOD_STAIRS;
    public static final RegistryObject<Item> STRIPPED_ASHPEN_LOG = ItemsPart03.STRIPPED_ASHPEN_LOG;
    public static final RegistryObject<Item> STRIPPED_ASHPEN_WOOD = ItemsPart03.STRIPPED_ASHPEN_WOOD;
    public static final RegistryObject<Item> STRIPPED_ASHPEN_LOG_SLAB = ItemsPart03.STRIPPED_ASHPEN_LOG_SLAB;
    public static final RegistryObject<Item> STRIPPED_ASHPEN_LOG_STAIRS = ItemsPart03.STRIPPED_ASHPEN_LOG_STAIRS;
    public static final RegistryObject<Item> STRIPPED_ASHPEN_WOOD_SLAB = ItemsPart03.STRIPPED_ASHPEN_WOOD_SLAB;
    public static final RegistryObject<Item> STRIPPED_ASHPEN_WOOD_STAIRS = ItemsPart03.STRIPPED_ASHPEN_WOOD_STAIRS;
    public static final RegistryObject<Item> BIT_POLISHED_TUFF_VERTICAL_SLAB =
            ItemsPart03.BIT_POLISHED_TUFF_VERTICAL_SLAB;
    public static final RegistryObject<Item> BIT_TUFF_BRICKS_VERTICAL_SLAB = ItemsPart03.BIT_TUFF_BRICKS_VERTICAL_SLAB;
    public static final RegistryObject<Item> PODZOL_VERTICAL_SLAB = ItemsPart03.PODZOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> DIRT_VERTICAL_SLAB = ItemsPart03.DIRT_VERTICAL_SLAB;
    public static final RegistryObject<Item> MYCELIUM_VERTICAL_SLAB = ItemsPart03.MYCELIUM_VERTICAL_SLAB;
    public static final RegistryObject<Item> MUD_VERTICAL_SLAB = ItemsPart03.MUD_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLACK_SANDSTONE_VERTICAL_SLAB = ItemsPart03.BLACK_SANDSTONE_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLUE_SANDSTONE_VERTICAL_SLAB = ItemsPart03.BLUE_SANDSTONE_VERTICAL_SLAB;
    public static final RegistryObject<Item> GREEN_SANDSTONE_VERTICAL_SLAB = ItemsPart03.GREEN_SANDSTONE_VERTICAL_SLAB;
    public static final RegistryObject<Item> ORANGE_SANDSTONE_VERTICAL_SLAB =
            ItemsPart03.ORANGE_SANDSTONE_VERTICAL_SLAB;
    public static final RegistryObject<Item> PINK_SANDSTONE_VERTICAL_SLAB = ItemsPart03.PINK_SANDSTONE_VERTICAL_SLAB;
    public static final RegistryObject<Item> RED_SANDSTONE_VERTICAL_SLAB = ItemsPart03.RED_SANDSTONE_VERTICAL_SLAB;
    public static final RegistryObject<Item> WHITE_SANDSTONE_VERTICAL_SLAB = ItemsPart03.WHITE_SANDSTONE_VERTICAL_SLAB;
    public static final RegistryObject<Item> YELLOW_SANDSTONE_VERTICAL_SLAB =
            ItemsPart03.YELLOW_SANDSTONE_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLACK_SMOOTH_SANDSTONE_VERTICAL_SLAB =
            ItemsPart03.BLACK_SMOOTH_SANDSTONE_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLUE_SMOOTH_SANDSTONE_VERTICAL_SLAB =
            ItemsPart03.BLUE_SMOOTH_SANDSTONE_VERTICAL_SLAB;
    public static final RegistryObject<Item> GREEN_SMOOTH_SANDSTONE_VERTICAL_SLAB =
            ItemsPart03.GREEN_SMOOTH_SANDSTONE_VERTICAL_SLAB;
    public static final RegistryObject<Item> ORANGE_SMOOTH_SANDSTONE_VERTICAL_SLAB =
            ItemsPart03.ORANGE_SMOOTH_SANDSTONE_VERTICAL_SLAB;
    public static final RegistryObject<Item> PINK_SMOOTH_SANDSTONE_VERTICAL_SLAB =
            ItemsPart03.PINK_SMOOTH_SANDSTONE_VERTICAL_SLAB;
    public static final RegistryObject<Item> RED_SMOOTH_SANDSTONE_VERTICAL_SLAB =
            ItemsPart03.RED_SMOOTH_SANDSTONE_VERTICAL_SLAB;
    public static final RegistryObject<Item> WHITE_SMOOTH_SANDSTONE_VERTICAL_SLAB =
            ItemsPart03.WHITE_SMOOTH_SANDSTONE_VERTICAL_SLAB;
    public static final RegistryObject<Item> YELLOW_SMOOTH_SANDSTONE_VERTICAL_SLAB =
            ItemsPart03.YELLOW_SMOOTH_SANDSTONE_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLACK_TILES_VERTICAL_SLAB = ItemsPart03.BLACK_TILES_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLUE_TILES_VERTICAL_SLAB = ItemsPart03.BLUE_TILES_VERTICAL_SLAB;
    public static final RegistryObject<Item> BROWN_TILES_VERTICAL_SLAB = ItemsPart03.BROWN_TILES_VERTICAL_SLAB;
    public static final RegistryObject<Item> CYAN_TILES_VERTICAL_SLAB = ItemsPart03.CYAN_TILES_VERTICAL_SLAB;
    public static final RegistryObject<Item> GRAY_TILES_VERTICAL_SLAB = ItemsPart03.GRAY_TILES_VERTICAL_SLAB;
    public static final RegistryObject<Item> GREEN_TILES_VERTICAL_SLAB = ItemsPart03.GREEN_TILES_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIGHT_BLUE_TILES_VERTICAL_SLAB =
            ItemsPart03.LIGHT_BLUE_TILES_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_TILES_VERTICAL_SLAB =
            ItemsPart03.LIGHT_GRAY_TILES_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIME_TILES_VERTICAL_SLAB = ItemsPart03.LIME_TILES_VERTICAL_SLAB;
    public static final RegistryObject<Item> MAGENTA_TILES_VERTICAL_SLAB = ItemsPart03.MAGENTA_TILES_VERTICAL_SLAB;
    public static final RegistryObject<Item> ORANGE_TILES_VERTICAL_SLAB = ItemsPart03.ORANGE_TILES_VERTICAL_SLAB;
    public static final RegistryObject<Item> PINK_TILES_VERTICAL_SLAB = ItemsPart03.PINK_TILES_VERTICAL_SLAB;
    public static final RegistryObject<Item> PURPLE_TILES_VERTICAL_SLAB = ItemsPart03.PURPLE_TILES_VERTICAL_SLAB;
    public static final RegistryObject<Item> RED_TILES_VERTICAL_SLAB = ItemsPart03.RED_TILES_VERTICAL_SLAB;
    public static final RegistryObject<Item> WHITE_TILES_VERTICAL_SLAB = ItemsPart03.WHITE_TILES_VERTICAL_SLAB;
    public static final RegistryObject<Item> YELLOW_TILES_VERTICAL_SLAB = ItemsPart03.YELLOW_TILES_VERTICAL_SLAB;
    public static final RegistryObject<Item> POLISHED_BASALT_VERTICAL_SLAB = ItemsPart03.POLISHED_BASALT_VERTICAL_SLAB;
    public static final RegistryObject<Item> DRIPSTONE_BLOCK_VERTICAL_SLAB = ItemsPart03.DRIPSTONE_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> END_STONE_VERTICAL_SLAB = ItemsPart03.END_STONE_VERTICAL_SLAB;
    public static final RegistryObject<Item> QUARTZ_BRICKS_VERTICAL_SLAB = ItemsPart03.QUARTZ_BRICKS_VERTICAL_SLAB;
    public static final RegistryObject<Item> QUARTZ_PILLAR_VERTICAL_SLAB = ItemsPart03.QUARTZ_PILLAR_VERTICAL_SLAB;
    public static final RegistryObject<Item> CALCITE_VERTICAL_SLAB = ItemsPart03.CALCITE_VERTICAL_SLAB;
    public static final RegistryObject<Item> BONE_BLOCK_VERTICAL_SLAB = ItemsPart03.BONE_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> STACKED_BONE_VERTICAL_SLAB = ItemsPart03.STACKED_BONE_VERTICAL_SLAB;
    public static final RegistryObject<Item> POLISHED_BONE_VERTICAL_SLAB = ItemsPart03.POLISHED_BONE_VERTICAL_SLAB;
    public static final RegistryObject<Item> BONE_BRICK_VERTICAL_SLAB = ItemsPart03.BONE_BRICK_VERTICAL_SLAB;
    public static final RegistryObject<Item> OBSIDIAN_VERTICAL_SLAB = ItemsPart03.OBSIDIAN_VERTICAL_SLAB;
    public static final RegistryObject<Item> SMOOTH_BASALT_VERTICAL_SLAB = ItemsPart03.SMOOTH_BASALT_VERTICAL_SLAB;
    public static final RegistryObject<Item> MOSS_BLOCK_VERTICAL_SLAB = ItemsPart03.MOSS_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> AMETHYST_BLOCK_VERTICAL_SLAB = ItemsPart03.AMETHYST_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> MOSSY_CALCITE_VERTICAL_SLAB = ItemsPart03.MOSSY_CALCITE_VERTICAL_SLAB;
    public static final RegistryObject<Item> BIT_COPPER_BLOCK_VERTICAL_SLAB =
            ItemsPart03.BIT_COPPER_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> BIT_EXPOSED_COPPER_BLOCK_VERTICAL_SLAB =
            ItemsPart03.BIT_EXPOSED_COPPER_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> BIT_WEATHERED_COPPER_BLOCK_VERTICAL_SLAB =
            ItemsPart03.BIT_WEATHERED_COPPER_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> BIT_OXIDIZED_COPPER_BLOCK_VERTICAL_SLAB =
            ItemsPart03.BIT_OXIDIZED_COPPER_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> BIT_CUT_COPPER_VERTICAL_SLAB = ItemsPart03.BIT_CUT_COPPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> BIT_EXPOSED_CUT_COPPER_VERTICAL_SLAB =
            ItemsPart03.BIT_EXPOSED_CUT_COPPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> BIT_WEATHERED_CUT_COPPER_VERTICAL_SLAB =
            ItemsPart03.BIT_WEATHERED_CUT_COPPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> BIT_OXIDIZED_CUT_COPPER_VERTICAL_SLAB =
            ItemsPart03.BIT_OXIDIZED_CUT_COPPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLACK_CONCRETE_VERTICAL_SLAB = ItemsPart03.BLACK_CONCRETE_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLUE_CONCRETE_VERTICAL_SLAB = ItemsPart03.BLUE_CONCRETE_VERTICAL_SLAB;
    public static final RegistryObject<Item> BROWN_CONCRETE_VERTICAL_SLAB = ItemsPart03.BROWN_CONCRETE_VERTICAL_SLAB;
    public static final RegistryObject<Item> CYAN_CONCRETE_VERTICAL_SLAB = ItemsPart03.CYAN_CONCRETE_VERTICAL_SLAB;
    public static final RegistryObject<Item> GRAY_CONCRETE_VERTICAL_SLAB = ItemsPart03.GRAY_CONCRETE_VERTICAL_SLAB;
    public static final RegistryObject<Item> GREEN_CONCRETE_VERTICAL_SLAB = ItemsPart03.GREEN_CONCRETE_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIGHT_BLUE_CONCRETE_VERTICAL_SLAB =
            ItemsPart03.LIGHT_BLUE_CONCRETE_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_CONCRETE_VERTICAL_SLAB =
            ItemsPart03.LIGHT_GRAY_CONCRETE_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIME_CONCRETE_VERTICAL_SLAB = ItemsPart03.LIME_CONCRETE_VERTICAL_SLAB;
    public static final RegistryObject<Item> MAGENTA_CONCRETE_VERTICAL_SLAB =
            ItemsPart03.MAGENTA_CONCRETE_VERTICAL_SLAB;
    public static final RegistryObject<Item> ORANGE_CONCRETE_VERTICAL_SLAB = ItemsPart03.ORANGE_CONCRETE_VERTICAL_SLAB;
    public static final RegistryObject<Item> PINK_CONCRETE_VERTICAL_SLAB = ItemsPart03.PINK_CONCRETE_VERTICAL_SLAB;
    public static final RegistryObject<Item> PURPLE_CONCRETE_VERTICAL_SLAB = ItemsPart03.PURPLE_CONCRETE_VERTICAL_SLAB;
    public static final RegistryObject<Item> RED_CONCRETE_VERTICAL_SLAB = ItemsPart03.RED_CONCRETE_VERTICAL_SLAB;
    public static final RegistryObject<Item> WHITE_CONCRETE_VERTICAL_SLAB = ItemsPart03.WHITE_CONCRETE_VERTICAL_SLAB;
    public static final RegistryObject<Item> YELLOW_CONCRETE_VERTICAL_SLAB = ItemsPart03.YELLOW_CONCRETE_VERTICAL_SLAB;
    public static final RegistryObject<Item> HAY_BALE_VERTICAL_SLAB = ItemsPart03.HAY_BALE_VERTICAL_SLAB;
    public static final RegistryObject<Item> BAMBOO_BLOCK_VERTICAL_SLAB = ItemsPart03.BAMBOO_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> STRIPPED_BAMBOO_BLOCK_VERTICAL_SLAB =
            ItemsPart03.STRIPPED_BAMBOO_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> ASHPEN_WHITE_VERTICAL_SLAB = ItemsPart03.ASHPEN_WHITE_VERTICAL_SLAB;
    public static final RegistryObject<Item> ASHPEN_BLACK_VERTICAL_SLAB = ItemsPart03.ASHPEN_BLACK_VERTICAL_SLAB;
    public static final RegistryObject<Item> ASHPEN_BLUE_VERTICAL_SLAB = ItemsPart03.ASHPEN_BLUE_VERTICAL_SLAB;
    public static final RegistryObject<Item> ASHPEN_BROWN_VERTICAL_SLAB = ItemsPart03.ASHPEN_BROWN_VERTICAL_SLAB;
    public static final RegistryObject<Item> ASHPEN_CYAN_VERTICAL_SLAB = ItemsPart03.ASHPEN_CYAN_VERTICAL_SLAB;
    public static final RegistryObject<Item> ASHPEN_GRAY_VERTICAL_SLAB = ItemsPart03.ASHPEN_GRAY_VERTICAL_SLAB;
    public static final RegistryObject<Item> ASHPEN_GREEN_VERTICAL_SLAB = ItemsPart03.ASHPEN_GREEN_VERTICAL_SLAB;
    public static final RegistryObject<Item> ASHPEN_LIGHT_BLUE_VERTICAL_SLAB =
            ItemsPart03.ASHPEN_LIGHT_BLUE_VERTICAL_SLAB;
    public static final RegistryObject<Item> ASHPEN_LIGHT_GRAY_VERTICAL_SLAB =
            ItemsPart03.ASHPEN_LIGHT_GRAY_VERTICAL_SLAB;
    public static final RegistryObject<Item> ASHPEN_LIME_VERTICAL_SLAB = ItemsPart03.ASHPEN_LIME_VERTICAL_SLAB;
    public static final RegistryObject<Item> ASHPEN_MAGENTA_VERTICAL_SLAB = ItemsPart03.ASHPEN_MAGENTA_VERTICAL_SLAB;
    public static final RegistryObject<Item> ASHPEN_ORANGE_VERTICAL_SLAB = ItemsPart03.ASHPEN_ORANGE_VERTICAL_SLAB;
    public static final RegistryObject<Item> ASHPEN_PINK_VERTICAL_SLAB = ItemsPart03.ASHPEN_PINK_VERTICAL_SLAB;
    public static final RegistryObject<Item> ASHPEN_PURPLE_VERTICAL_SLAB = ItemsPart03.ASHPEN_PURPLE_VERTICAL_SLAB;
    public static final RegistryObject<Item> ASHPEN_RED_VERTICAL_SLAB = ItemsPart03.ASHPEN_RED_VERTICAL_SLAB;
    public static final RegistryObject<Item> ASHPEN_YELLOW_VERTICAL_SLAB = ItemsPart03.ASHPEN_YELLOW_VERTICAL_SLAB;
    public static final RegistryObject<Item> SNOW_BRICKS_VERTICAL_SLAB = ItemsPart03.SNOW_BRICKS_VERTICAL_SLAB;
    public static final RegistryObject<Item> SNOW_VERTICAL_SLAB = ItemsPart03.SNOW_VERTICAL_SLAB;
    public static final RegistryObject<Item> SNOWY_GRASS_BLOCK_VERTICAL_SLAB =
            ItemsPart03.SNOWY_GRASS_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> MANGROVE_VERTICAL_SLAB = ItemsPart03.MANGROVE_VERTICAL_SLAB;
    public static final RegistryObject<Item> STEEL_BLOCK_VERTICAL_SLAB = ItemsPart03.STEEL_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> POLISHED_STEEL_VERTICAL_SLAB = ItemsPart03.POLISHED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> PRESSED_STEEL_VERTICAL_SLAB = ItemsPart03.PRESSED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> CUT_STEEL_VERTICAL_SLAB = ItemsPart03.CUT_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> CAUTION_BLACK_VERTICAL_SLAB = ItemsPart03.CAUTION_BLACK_VERTICAL_SLAB;
    public static final RegistryObject<Item> CAUTION_BLUE_VERTICAL_SLAB = ItemsPart03.CAUTION_BLUE_VERTICAL_SLAB;
    public static final RegistryObject<Item> CAUTION_FACTORY_VERTICAL_SLAB = ItemsPart03.CAUTION_FACTORY_VERTICAL_SLAB;
    public static final RegistryObject<Item> CAUTION_RED_VERTICAL_SLAB = ItemsPart03.CAUTION_RED_VERTICAL_SLAB;
    public static final RegistryObject<Item> FRAMED_CAUTION_VERTICAL_SLAB = ItemsPart03.FRAMED_CAUTION_VERTICAL_SLAB;
    public static final RegistryObject<Item> CAUTION_WHITE_VERTICAL_SLAB = ItemsPart03.CAUTION_WHITE_VERTICAL_SLAB;
    public static final RegistryObject<Item> CAUTION_LIME_VERTICAL_SLAB = ItemsPart03.CAUTION_LIME_VERTICAL_SLAB;
    public static final RegistryObject<Item> CAUTION_PINK_VERTICAL_SLAB = ItemsPart03.CAUTION_PINK_VERTICAL_SLAB;
    public static final RegistryObject<Item> CAUTION_YELLOW_VERTICAL_SLAB = ItemsPart03.CAUTION_YELLOW_VERTICAL_SLAB;
    public static final RegistryObject<Item> CAUTION_CANDY_VERTICAL_SLAB = ItemsPart03.CAUTION_CANDY_VERTICAL_SLAB;
    public static final RegistryObject<Item> CAUTION_COTTONCANDY_VERTICAL_SLAB =
            ItemsPart03.CAUTION_COTTONCANDY_VERTICAL_SLAB;
    public static final RegistryObject<Item> CAUTION_MINTCANDY_VERTICAL_SLAB =
            ItemsPart03.CAUTION_MINTCANDY_VERTICAL_SLAB;
    public static final RegistryObject<Item> CAUTION_CITRUSCANDY_VERTICAL_SLAB =
            ItemsPart03.CAUTION_CITRUSCANDY_VERTICAL_SLAB;
    public static final RegistryObject<Item> WHITE_STAINED_BRICKS_VERTICAL_SLAB =
            ItemsPart03.WHITE_STAINED_BRICKS_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_BRICKS_VERTICAL_SLAB =
            ItemsPart03.LIGHT_GRAY_STAINED_BRICKS_VERTICAL_SLAB;
    public static final RegistryObject<Item> GRAY_STAINED_BRICKS_VERTICAL_SLAB =
            ItemsPart03.GRAY_STAINED_BRICKS_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLACK_STAINED_BRICKS_VERTICAL_SLAB =
            ItemsPart03.BLACK_STAINED_BRICKS_VERTICAL_SLAB;
    public static final RegistryObject<Item> BROWN_STAINED_BRICKS_VERTICAL_SLAB =
            ItemsPart03.BROWN_STAINED_BRICKS_VERTICAL_SLAB;
    public static final RegistryObject<Item> RED_STAINED_BRICKS_VERTICAL_SLAB =
            ItemsPart03.RED_STAINED_BRICKS_VERTICAL_SLAB;
    public static final RegistryObject<Item> ORANGE_STAINED_BRICKS_VERTICAL_SLAB =
            ItemsPart03.ORANGE_STAINED_BRICKS_VERTICAL_SLAB;
    public static final RegistryObject<Item> YELLOW_STAINED_BRICKS_VERTICAL_SLAB =
            ItemsPart03.YELLOW_STAINED_BRICKS_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIME_STAINED_BRICKS_VERTICAL_SLAB =
            ItemsPart03.LIME_STAINED_BRICKS_VERTICAL_SLAB;
    public static final RegistryObject<Item> GREEN_STAINED_BRICKS_VERTICAL_SLAB =
            ItemsPart03.GREEN_STAINED_BRICKS_VERTICAL_SLAB;
    public static final RegistryObject<Item> CYAN_STAINED_BRICKS_VERTICAL_SLAB =
            ItemsPart03.CYAN_STAINED_BRICKS_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_BRICKS_VERTICAL_SLAB =
            ItemsPart03.LIGHT_BLUE_STAINED_BRICKS_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLUE_STAINED_BRICKS_VERTICAL_SLAB =
            ItemsPart03.BLUE_STAINED_BRICKS_VERTICAL_SLAB;
    public static final RegistryObject<Item> PURPLE_STAINED_BRICKS_VERTICAL_SLAB =
            ItemsPart03.PURPLE_STAINED_BRICKS_VERTICAL_SLAB;
    public static final RegistryObject<Item> MAGENTA_STAINED_BRICKS_VERTICAL_SLAB =
            ItemsPart03.MAGENTA_STAINED_BRICKS_VERTICAL_SLAB;
    public static final RegistryObject<Item> PINK_STAINED_BRICKS_VERTICAL_SLAB =
            ItemsPart03.PINK_STAINED_BRICKS_VERTICAL_SLAB;
    public static final RegistryObject<Item> POLISHED_WHITE_CONCRETE_VERTICAL_SLAB =
            ItemsPart03.POLISHED_WHITE_CONCRETE_VERTICAL_SLAB;
    public static final RegistryObject<Item> POLISHED_ORANGE_CONCRETE_VERTICAL_SLAB =
            ItemsPart03.POLISHED_ORANGE_CONCRETE_VERTICAL_SLAB;
    public static final RegistryObject<Item> POLISHED_MAGENTA_CONCRETE_VERTICAL_SLAB =
            ItemsPart03.POLISHED_MAGENTA_CONCRETE_VERTICAL_SLAB;
    public static final RegistryObject<Item> POLISHED_LIGHT_BLUE_CONCRETE_VERTICAL_SLAB =
            ItemsPart03.POLISHED_LIGHT_BLUE_CONCRETE_VERTICAL_SLAB;
    public static final RegistryObject<Item> POLISHED_YELLOW_CONCRETE_VERTICAL_SLAB =
            ItemsPart03.POLISHED_YELLOW_CONCRETE_VERTICAL_SLAB;
    public static final RegistryObject<Item> POLISHED_LIME_CONCRETE_VERTICAL_SLAB =
            ItemsPart03.POLISHED_LIME_CONCRETE_VERTICAL_SLAB;
    public static final RegistryObject<Item> POLISHED_PINK_CONCRETE_VERTICAL_SLAB =
            ItemsPart03.POLISHED_PINK_CONCRETE_VERTICAL_SLAB;
    public static final RegistryObject<Item> POLISHED_GRAY_CONCRETE_VERTICAL_SLAB =
            ItemsPart03.POLISHED_GRAY_CONCRETE_VERTICAL_SLAB;
    public static final RegistryObject<Item> POLISHED_LIGHT_GRAY_CONCRETE_VERTICAL_SLAB =
            ItemsPart03.POLISHED_LIGHT_GRAY_CONCRETE_VERTICAL_SLAB;
    public static final RegistryObject<Item> POLISHED_CYAN_CONCRETE_VERTICAL_SLAB =
            ItemsPart03.POLISHED_CYAN_CONCRETE_VERTICAL_SLAB;
    public static final RegistryObject<Item> POLISHED_PURPLE_CONCRETE_VERTICAL_SLAB =
            ItemsPart03.POLISHED_PURPLE_CONCRETE_VERTICAL_SLAB;
    public static final RegistryObject<Item> POLISHED_BLUE_CONCRETE_VERTICAL_SLAB =
            ItemsPart03.POLISHED_BLUE_CONCRETE_VERTICAL_SLAB;
    public static final RegistryObject<Item> POLISHED_BROWN_CONCRETE_VERTICAL_SLAB =
            ItemsPart03.POLISHED_BROWN_CONCRETE_VERTICAL_SLAB;
    public static final RegistryObject<Item> POLISHED_GREEN_CONCRETE_VERTICAL_SLAB =
            ItemsPart03.POLISHED_GREEN_CONCRETE_VERTICAL_SLAB;
    public static final RegistryObject<Item> POLISHED_RED_CONCRETE_VERTICAL_SLAB =
            ItemsPart03.POLISHED_RED_CONCRETE_VERTICAL_SLAB;
    public static final RegistryObject<Item> POLISHED_BLACK_CONCRETE_VERTICAL_SLAB =
            ItemsPart03.POLISHED_BLACK_CONCRETE_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLACK_FACTORY_MESH_VERTICAL_SLAB =
            ItemsPart03.BLACK_FACTORY_MESH_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLUE_FACTORY_MESH_VERTICAL_SLAB =
            ItemsPart03.BLUE_FACTORY_MESH_VERTICAL_SLAB;
    public static final RegistryObject<Item> BROWN_FACTORY_MESH_VERTICAL_SLAB =
            ItemsPart03.BROWN_FACTORY_MESH_VERTICAL_SLAB;
    public static final RegistryObject<Item> GRAY_FACTORY_MESH_VERTICAL_SLAB =
            ItemsPart03.GRAY_FACTORY_MESH_VERTICAL_SLAB;
    public static final RegistryObject<Item> GREEN_FACTORY_MESH_VERTICAL_SLAB =
            ItemsPart03.GREEN_FACTORY_MESH_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_FACTORY_MESH_VERTICAL_SLAB =
            ItemsPart03.LIGHT_GRAY_FACTORY_MESH_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIME_FACTORY_MESH_VERTICAL_SLAB =
            ItemsPart03.LIME_FACTORY_MESH_VERTICAL_SLAB;
    public static final RegistryObject<Item> ORANGE_FACTORY_MESH_VERTICAL_SLAB =
            ItemsPart03.ORANGE_FACTORY_MESH_VERTICAL_SLAB;
    public static final RegistryObject<Item> RED_FACTORY_MESH_VERTICAL_SLAB =
            ItemsPart03.RED_FACTORY_MESH_VERTICAL_SLAB;
    public static final RegistryObject<Item> YELLOW_FACTORY_MESH_VERTICAL_SLAB =
            ItemsPart03.YELLOW_FACTORY_MESH_VERTICAL_SLAB;
    public static final RegistryObject<Item> PACKED_MUD_VERTICAL_SLAB = ItemsPart03.PACKED_MUD_VERTICAL_SLAB;
    public static final RegistryObject<Item> MUD_BRICK_VERTICAL_SLAB = ItemsPart03.MUD_BRICK_VERTICAL_SLAB;
    public static final RegistryObject<Item> GLASS_VERTICAL_SLAB = ItemsPart03.GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> WHITE_STAINED_GLASS_VERTICAL_SLAB =
            ItemsPart03.WHITE_STAINED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_STAINED_GLASS_VERTICAL_SLAB =
            ItemsPart03.LIGHT_GRAY_STAINED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> GRAY_STAINED_GLASS_VERTICAL_SLAB =
            ItemsPart03.GRAY_STAINED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLACK_STAINED_GLASS_VERTICAL_SLAB =
            ItemsPart03.BLACK_STAINED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> BROWN_STAINED_GLASS_VERTICAL_SLAB =
            ItemsPart03.BROWN_STAINED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> RED_STAINED_GLASS_VERTICAL_SLAB =
            ItemsPart03.RED_STAINED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> ORANGE_STAINED_GLASS_VERTICAL_SLAB =
            ItemsPart03.ORANGE_STAINED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> YELLOW_STAINED_GLASS_VERTICAL_SLAB =
            ItemsPart03.YELLOW_STAINED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIME_STAINED_GLASS_VERTICAL_SLAB =
            ItemsPart03.LIME_STAINED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> GREEN_STAINED_GLASS_VERTICAL_SLAB =
            ItemsPart03.GREEN_STAINED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> CYAN_STAINED_GLASS_VERTICAL_SLAB =
            ItemsPart03.CYAN_STAINED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIGHT_BLUE_STAINED_GLASS_VERTICAL_SLAB =
            ItemsPart03.LIGHT_BLUE_STAINED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLUE_STAINED_GLASS_VERTICAL_SLAB =
            ItemsPart03.BLUE_STAINED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> PURPLE_STAINED_GLASS_VERTICAL_SLAB =
            ItemsPart03.PURPLE_STAINED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> MAGENTA_STAINED_GLASS_VERTICAL_SLAB =
            ItemsPart03.MAGENTA_STAINED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> PINK_STAINED_GLASS_VERTICAL_SLAB =
            ItemsPart03.PINK_STAINED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> WHITE_MOSAIC_GLASS_VERTICAL_SLAB =
            ItemsPart03.WHITE_MOSAIC_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_MOSAIC_GLASS_VERTICAL_SLAB =
            ItemsPart03.LIGHT_GRAY_MOSAIC_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> GRAY_MOSAIC_GLASS_VERTICAL_SLAB =
            ItemsPart03.GRAY_MOSAIC_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLACK_MOSAIC_GLASS_VERTICAL_SLAB =
            ItemsPart03.BLACK_MOSAIC_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> BROWN_MOSAIC_GLASS_VERTICAL_SLAB =
            ItemsPart03.BROWN_MOSAIC_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> RED_MOSAIC_GLASS_VERTICAL_SLAB =
            ItemsPart03.RED_MOSAIC_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> ORANGE_MOSAIC_GLASS_VERTICAL_SLAB =
            ItemsPart03.ORANGE_MOSAIC_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> YELLOW_MOSAIC_GLASS_VERTICAL_SLAB =
            ItemsPart03.YELLOW_MOSAIC_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIME_MOSAIC_GLASS_VERTICAL_SLAB =
            ItemsPart03.LIME_MOSAIC_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> GREEN_MOSAIC_GLASS_VERTICAL_SLAB =
            ItemsPart03.GREEN_MOSAIC_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> CYAN_MOSAIC_GLASS_VERTICAL_SLAB =
            ItemsPart03.CYAN_MOSAIC_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIGHT_BLUE_MOSAIC_GLASS_VERTICAL_SLAB =
            ItemsPart03.LIGHT_BLUE_MOSAIC_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLUE_MOSAIC_GLASS_VERTICAL_SLAB =
            ItemsPart03.BLUE_MOSAIC_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> PURPLE_MOSAIC_GLASS_VERTICAL_SLAB =
            ItemsPart03.PURPLE_MOSAIC_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> MAGENTA_MOSAIC_GLASS_VERTICAL_SLAB =
            ItemsPart03.MAGENTA_MOSAIC_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> PINK_MOSAIC_GLASS_VERTICAL_SLAB =
            ItemsPart03.PINK_MOSAIC_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> FACTORY_WHITE_GLASS_VERTICAL_SLAB =
            ItemsPart03.FACTORY_WHITE_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> FACTORY_LIGHT_GRAY_GLASS_VERTICAL_SLAB =
            ItemsPart03.FACTORY_LIGHT_GRAY_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> FACTORY_GRAY_GLASS_VERTICAL_SLAB =
            ItemsPart03.FACTORY_GRAY_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> FACTORY_BLACK_GLASS_VERTICAL_SLAB =
            ItemsPart03.FACTORY_BLACK_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> FACTORY_BROWN_GLASS_VERTICAL_SLAB =
            ItemsPart03.FACTORY_BROWN_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> FACTORY_RED_GLASS_VERTICAL_SLAB =
            ItemsPart03.FACTORY_RED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> FACTORY_ORANGE_GLASS_VERTICAL_SLAB =
            ItemsPart03.FACTORY_ORANGE_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> FACTORY_YELLOW_GLASS_VERTICAL_SLAB =
            ItemsPart03.FACTORY_YELLOW_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> FACTORY_LIME_GLASS_VERTICAL_SLAB =
            ItemsPart03.FACTORY_LIME_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> FACTORY_GREEN_GLASS_VERTICAL_SLAB =
            ItemsPart03.FACTORY_GREEN_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> FACTORY_CYAN_GLASS_VERTICAL_SLAB =
            ItemsPart03.FACTORY_CYAN_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> FACTORY_LIGHT_BLUE_GLASS_VERTICAL_SLAB =
            ItemsPart03.FACTORY_LIGHT_BLUE_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> FACTORY_BLUE_GLASS_VERTICAL_SLAB =
            ItemsPart03.FACTORY_BLUE_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> FACTORY_PURPLE_GLASS_VERTICAL_SLAB =
            ItemsPart03.FACTORY_PURPLE_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> FACTORY_MAGENTA_GLASS_VERTICAL_SLAB =
            ItemsPart03.FACTORY_MAGENTA_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> FACTORY_PINK_GLASS_VERTICAL_SLAB =
            ItemsPart03.FACTORY_PINK_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> WHITE_GLAZED_GLASS_VERTICAL_SLAB =
            ItemsPart03.WHITE_GLAZED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_GLAZED_GLASS_VERTICAL_SLAB =
            ItemsPart03.LIGHT_GRAY_GLAZED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> GRAY_GLAZED_GLASS_VERTICAL_SLAB =
            ItemsPart03.GRAY_GLAZED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLACK_GLAZED_GLASS_VERTICAL_SLAB =
            ItemsPart03.BLACK_GLAZED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> BROWN_GLAZED_GLASS_VERTICAL_SLAB =
            ItemsPart03.BROWN_GLAZED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> RED_GLAZED_GLASS_VERTICAL_SLAB =
            ItemsPart03.RED_GLAZED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> ORANGE_GLAZED_GLASS_VERTICAL_SLAB =
            ItemsPart03.ORANGE_GLAZED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> YELLOW_GLAZED_GLASS_VERTICAL_SLAB =
            ItemsPart03.YELLOW_GLAZED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIME_GLAZED_GLASS_VERTICAL_SLAB =
            ItemsPart03.LIME_GLAZED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> GREEN_GLAZED_GLASS_VERTICAL_SLAB =
            ItemsPart03.GREEN_GLAZED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> CYAN_GLAZED_GLASS_VERTICAL_SLAB =
            ItemsPart03.CYAN_GLAZED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIGHT_BLUE_GLAZED_GLASS_VERTICAL_SLAB =
            ItemsPart03.LIGHT_BLUE_GLAZED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLUE_GLAZED_GLASS_VERTICAL_SLAB =
            ItemsPart03.BLUE_GLAZED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> PURPLE_GLAZED_GLASS_VERTICAL_SLAB =
            ItemsPart03.PURPLE_GLAZED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> MAGENTA_GLAZED_GLASS_VERTICAL_SLAB =
            ItemsPart03.MAGENTA_GLAZED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> PINK_GLAZED_GLASS_VERTICAL_SLAB =
            ItemsPart03.PINK_GLAZED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> OAK_LOG_VERTICAL_SLAB = ItemsPart03.OAK_LOG_VERTICAL_SLAB;
    public static final RegistryObject<Item> STRIPPED_OAK_LOG_VERTICAL_SLAB =
            ItemsPart03.STRIPPED_OAK_LOG_VERTICAL_SLAB;
    public static final RegistryObject<Item> SPRUCE_LOG_VERTICAL_SLAB = ItemsPart03.SPRUCE_LOG_VERTICAL_SLAB;
    public static final RegistryObject<Item> STRIPPED_SPRUCE_LOG_VERTICAL_SLAB =
            ItemsPart03.STRIPPED_SPRUCE_LOG_VERTICAL_SLAB;
    public static final RegistryObject<Item> BIRCH_LOG_VERTICAL_SLAB = ItemsPart03.BIRCH_LOG_VERTICAL_SLAB;
    public static final RegistryObject<Item> STRIPPED_BIRCH_LOG_VERTICAL_SLAB =
            ItemsPart03.STRIPPED_BIRCH_LOG_VERTICAL_SLAB;
    public static final RegistryObject<Item> JUNGLE_LOG_VERTICAL_SLAB = ItemsPart03.JUNGLE_LOG_VERTICAL_SLAB;
    public static final RegistryObject<Item> STRIPPED_JUNGLE_LOG_VERTICAL_SLAB =
            ItemsPart03.STRIPPED_JUNGLE_LOG_VERTICAL_SLAB;
    public static final RegistryObject<Item> ACACIA_LOG_VERTICAL_SLAB = ItemsPart03.ACACIA_LOG_VERTICAL_SLAB;
    public static final RegistryObject<Item> STRIPPED_ACACIA_LOG_VERTICAL_SLAB =
            ItemsPart03.STRIPPED_ACACIA_LOG_VERTICAL_SLAB;
    public static final RegistryObject<Item> DARK_OAK_LOG_VERTICAL_SLAB = ItemsPart03.DARK_OAK_LOG_VERTICAL_SLAB;
    public static final RegistryObject<Item> STRIPPED_DARK_OAK_LOG_VERTICAL_SLAB =
            ItemsPart03.STRIPPED_DARK_OAK_LOG_VERTICAL_SLAB;
    public static final RegistryObject<Item> CRIMSON_STEM_VERTICAL_SLAB = ItemsPart03.CRIMSON_STEM_VERTICAL_SLAB;
    public static final RegistryObject<Item> STRIPPED_CRIMSON_STEM_VERTICAL_SLAB =
            ItemsPart03.STRIPPED_CRIMSON_STEM_VERTICAL_SLAB;
    public static final RegistryObject<Item> WARPED_STEM_VERTICAL_SLAB = ItemsPart03.WARPED_STEM_VERTICAL_SLAB;
    public static final RegistryObject<Item> STRIPPED_WARPED_STEM_VERTICAL_SLAB =
            ItemsPart03.STRIPPED_WARPED_STEM_VERTICAL_SLAB;
    public static final RegistryObject<Item> MANGROVE_LOG_VERTICAL_SLAB = ItemsPart03.MANGROVE_LOG_VERTICAL_SLAB;
    public static final RegistryObject<Item> STRIPPED_MANGROVE_LOG_VERTICAL_SLAB =
            ItemsPart03.STRIPPED_MANGROVE_LOG_VERTICAL_SLAB;
    public static final RegistryObject<Item> ASHPEN_LOG_VERTICAL_SLAB = ItemsPart03.ASHPEN_LOG_VERTICAL_SLAB;
    public static final RegistryObject<Item> ASHPEN_WOOD_VERTICAL_SLAB = ItemsPart03.ASHPEN_WOOD_VERTICAL_SLAB;
    public static final RegistryObject<Item> STRIPPED_ASHPEN_LOG_VERTICAL_SLAB =
            ItemsPart03.STRIPPED_ASHPEN_LOG_VERTICAL_SLAB;
    public static final RegistryObject<Item> STRIPPED_ASHPEN_WOOD_VERTICAL_SLAB =
            ItemsPart03.STRIPPED_ASHPEN_WOOD_VERTICAL_SLAB;
    public static final RegistryObject<Item> SPOOL = ItemsPart03.SPOOL;
    public static final RegistryObject<Item> GLOWING_SPOOL = ItemsPart03.GLOWING_SPOOL;
    public static final RegistryObject<Item> WHITE_SPOOL = ItemsPart03.WHITE_SPOOL;
    public static final RegistryObject<Item> LIGHT_GRAY_SPOOL = ItemsPart03.LIGHT_GRAY_SPOOL;
    public static final RegistryObject<Item> GRAY_SPOOL = ItemsPart03.GRAY_SPOOL;
    public static final RegistryObject<Item> BLACK_SPOOL = ItemsPart03.BLACK_SPOOL;
    public static final RegistryObject<Item> BROWN_SPOOL = ItemsPart03.BROWN_SPOOL;
    public static final RegistryObject<Item> RED_SPOOL = ItemsPart03.RED_SPOOL;
    public static final RegistryObject<Item> ORANGE_SPOOL = ItemsPart03.ORANGE_SPOOL;
    public static final RegistryObject<Item> YELLOW_SPOOL = ItemsPart03.YELLOW_SPOOL;
    public static final RegistryObject<Item> LIME_SPOOL = ItemsPart03.LIME_SPOOL;
    public static final RegistryObject<Item> GREEN_SPOOL = ItemsPart03.GREEN_SPOOL;
    public static final RegistryObject<Item> CYAN_SPOOL = ItemsPart03.CYAN_SPOOL;
    public static final RegistryObject<Item> LIGHT_BLUE_SPOOL = ItemsPart03.LIGHT_BLUE_SPOOL;
    public static final RegistryObject<Item> BLUE_SPOOL = ItemsPart03.BLUE_SPOOL;
    public static final RegistryObject<Item> PURPLE_SPOOL = ItemsPart03.PURPLE_SPOOL;
    public static final RegistryObject<Item> MAGENTA_SPOOL = ItemsPart03.MAGENTA_SPOOL;
    public static final RegistryObject<Item> PINK_SPOOL = ItemsPart03.PINK_SPOOL;
    public static final RegistryObject<Item> WHITE_DYE_SACK = ItemsPart03.WHITE_DYE_SACK;
    public static final RegistryObject<Item> LIGHT_GRAY_DYE_SACK = ItemsPart03.LIGHT_GRAY_DYE_SACK;
    public static final RegistryObject<Item> GRAY_DYE_SACK = ItemsPart03.GRAY_DYE_SACK;
    public static final RegistryObject<Item> BLACK_DYE_SACK = ItemsPart03.BLACK_DYE_SACK;
    public static final RegistryObject<Item> BROWN_DYE_SACK = ItemsPart03.BROWN_DYE_SACK;
    public static final RegistryObject<Item> RED_DYE_SACK = ItemsPart03.RED_DYE_SACK;
    public static final RegistryObject<Item> ORANGE_DYE_SACK = ItemsPart03.ORANGE_DYE_SACK;
    public static final RegistryObject<Item> YELLOW_DYE_SACK = ItemsPart03.YELLOW_DYE_SACK;
    public static final RegistryObject<Item> LIME_DYE_SACK = ItemsPart03.LIME_DYE_SACK;
    public static final RegistryObject<Item> GREEN_DYE_SACK = ItemsPart03.GREEN_DYE_SACK;
    public static final RegistryObject<Item> CYAN_DYE_SACK = ItemsPart03.CYAN_DYE_SACK;
    public static final RegistryObject<Item> LIGHT_BLUE_DYE_SACK = ItemsPart03.LIGHT_BLUE_DYE_SACK;
    public static final RegistryObject<Item> BLUE_DYE_SACK = ItemsPart03.BLUE_DYE_SACK;
    public static final RegistryObject<Item> PURPLE_DYE_SACK = ItemsPart03.PURPLE_DYE_SACK;
    public static final RegistryObject<Item> MAGENTA_DYE_SACK = ItemsPart03.MAGENTA_DYE_SACK;
    public static final RegistryObject<Item> PINK_DYE_SACK = ItemsPart03.PINK_DYE_SACK;
    public static final RegistryObject<Item> GLOW_INK_SACK = ItemsPart03.GLOW_INK_SACK;
    public static final RegistryObject<Item> RED_MOSS_BLOCK = ItemsPart03.RED_MOSS_BLOCK;
    public static final RegistryObject<Item> RED_MOSS_BLOCK_SLAB = ItemsPart03.RED_MOSS_BLOCK_SLAB;
    public static final RegistryObject<Item> RED_MOSS_BLOCK_STAIRS = ItemsPart03.RED_MOSS_BLOCK_STAIRS;
    public static final RegistryObject<Item> RED_MOSS_LAYERS = ItemsPart03.RED_MOSS_LAYERS;
    public static final RegistryObject<Item> RED_MOSS_CARPET = ItemsPart03.RED_MOSS_CARPET;
    public static final RegistryObject<Item> RED_MOSS_OVERLAY = ItemsPart03.RED_MOSS_OVERLAY;
    public static final RegistryObject<Item> ORANGE_MOSS_BLOCK = ItemsPart03.ORANGE_MOSS_BLOCK;
    public static final RegistryObject<Item> ORANGE_MOSS_BLOCK_SLAB = ItemsPart03.ORANGE_MOSS_BLOCK_SLAB;
    public static final RegistryObject<Item> ORANGE_MOSS_BLOCK_STAIRS = ItemsPart03.ORANGE_MOSS_BLOCK_STAIRS;
    public static final RegistryObject<Item> ORANGE_MOSS_LAYERS = ItemsPart03.ORANGE_MOSS_LAYERS;
    public static final RegistryObject<Item> ORANGE_MOSS_CARPET = ItemsPart03.ORANGE_MOSS_CARPET;
    public static final RegistryObject<Item> ORANGE_MOSS_OVERLAY = ItemsPart03.ORANGE_MOSS_OVERLAY;
    public static final RegistryObject<Item> YELLOW_MOSS_BLOCK = ItemsPart03.YELLOW_MOSS_BLOCK;
    public static final RegistryObject<Item> YELLOW_MOSS_BLOCK_SLAB = ItemsPart03.YELLOW_MOSS_BLOCK_SLAB;
    public static final RegistryObject<Item> YELLOW_MOSS_BLOCK_STAIRS = ItemsPart03.YELLOW_MOSS_BLOCK_STAIRS;
    public static final RegistryObject<Item> YELLOW_MOSS_LAYERS = ItemsPart03.YELLOW_MOSS_LAYERS;
    public static final RegistryObject<Item> YELLOW_MOSS_CARPET = ItemsPart03.YELLOW_MOSS_CARPET;
    public static final RegistryObject<Item> YELLOW_MOSS_OVERLAY = ItemsPart03.YELLOW_MOSS_OVERLAY;
    public static final RegistryObject<Item> TUFF_SLAB = ItemsPart03.TUFF_SLAB;
    public static final RegistryObject<Item> TUFF_STAIRS = ItemsPart03.TUFF_STAIRS;
    public static final RegistryObject<Item> TUFF_WALL = ItemsPart03.TUFF_WALL;
    public static final RegistryObject<Item> POLISHED_TUFF = ItemsPart03.POLISHED_TUFF;
    public static final RegistryObject<Item> POLISHED_TUFF_SLAB = ItemsPart03.POLISHED_TUFF_SLAB;
    public static final RegistryObject<Item> POLISHED_TUFF_STAIRS = ItemsPart03.POLISHED_TUFF_STAIRS;
    public static final RegistryObject<Item> POLISHED_TUFF_WALL = ItemsPart03.POLISHED_TUFF_WALL;
    public static final RegistryObject<Item> CHISELED_TUFF = ItemsPart03.CHISELED_TUFF;
    public static final RegistryObject<Item> TUFF_BRICKS = ItemsPart03.TUFF_BRICKS;
    public static final RegistryObject<Item> TUFF_BRICK_SLAB = ItemsPart03.TUFF_BRICK_SLAB;
    public static final RegistryObject<Item> TUFF_BRICK_STAIRS = ItemsPart03.TUFF_BRICK_STAIRS;
    public static final RegistryObject<Item> TUFF_BRICK_WALL = ItemsPart03.TUFF_BRICK_WALL;
    public static final RegistryObject<Item> CHISELED_TUFF_BRICKS = ItemsPart03.CHISELED_TUFF_BRICKS;
    public static final RegistryObject<Item> WHITE_WOOL_SLAB = ItemsPart03.WHITE_WOOL_SLAB;
    public static final RegistryObject<Item> WHITE_WOOL_STAIRS = ItemsPart03.WHITE_WOOL_STAIRS;
    public static final RegistryObject<Item> WHITE_WOOL_WALL = ItemsPart03.WHITE_WOOL_WALL;
    public static final RegistryObject<Item> ORANGE_WOOL_SLAB = ItemsPart03.ORANGE_WOOL_SLAB;
    public static final RegistryObject<Item> ORANGE_WOOL_STAIRS = ItemsPart03.ORANGE_WOOL_STAIRS;
    public static final RegistryObject<Item> ORANGE_WOOL_WALL = ItemsPart03.ORANGE_WOOL_WALL;
    public static final RegistryObject<Item> MAGENTA_WOOL_SLAB = ItemsPart03.MAGENTA_WOOL_SLAB;
    public static final RegistryObject<Item> MAGENTA_WOOL_STAIRS = ItemsPart03.MAGENTA_WOOL_STAIRS;
    public static final RegistryObject<Item> MAGENTA_WOOL_WALL = ItemsPart03.MAGENTA_WOOL_WALL;
    public static final RegistryObject<Item> LIGHT_BLUE_WOOL_SLAB = ItemsPart03.LIGHT_BLUE_WOOL_SLAB;
    public static final RegistryObject<Item> LIGHT_BLUE_WOOL_STAIRS = ItemsPart03.LIGHT_BLUE_WOOL_STAIRS;
    public static final RegistryObject<Item> LIGHT_BLUE_WOOL_WALL = ItemsPart03.LIGHT_BLUE_WOOL_WALL;
    public static final RegistryObject<Item> YELLOW_WOOL_SLAB = ItemsPart03.YELLOW_WOOL_SLAB;
    public static final RegistryObject<Item> YELLOW_WOOL_STAIRS = ItemsPart03.YELLOW_WOOL_STAIRS;
    public static final RegistryObject<Item> YELLOW_WOOL_WALL = ItemsPart03.YELLOW_WOOL_WALL;
    public static final RegistryObject<Item> LIME_WOOL_SLAB = ItemsPart03.LIME_WOOL_SLAB;
    public static final RegistryObject<Item> LIME_WOOL_STAIRS = ItemsPart03.LIME_WOOL_STAIRS;
    public static final RegistryObject<Item> LIME_WOOL_WALL = ItemsPart03.LIME_WOOL_WALL;
    public static final RegistryObject<Item> PINK_WOOL_SLAB = ItemsPart03.PINK_WOOL_SLAB;
    public static final RegistryObject<Item> PINK_WOOL_STAIRS = ItemsPart03.PINK_WOOL_STAIRS;
    public static final RegistryObject<Item> PINK_WOOL_WALL = ItemsPart03.PINK_WOOL_WALL;
    public static final RegistryObject<Item> GRAY_WOOL_SLAB = ItemsPart03.GRAY_WOOL_SLAB;
    public static final RegistryObject<Item> GRAY_WOOL_STAIRS = ItemsPart03.GRAY_WOOL_STAIRS;
    public static final RegistryObject<Item> GRAY_WOOL_WALL = ItemsPart03.GRAY_WOOL_WALL;
    public static final RegistryObject<Item> LIGHT_GRAY_WOOL_SLAB = ItemsPart03.LIGHT_GRAY_WOOL_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_WOOL_STAIRS = ItemsPart03.LIGHT_GRAY_WOOL_STAIRS;
    public static final RegistryObject<Item> LIGHT_GRAY_WOOL_WALL = ItemsPart03.LIGHT_GRAY_WOOL_WALL;
    public static final RegistryObject<Item> CYAN_WOOL_SLAB = ItemsPart03.CYAN_WOOL_SLAB;
    public static final RegistryObject<Item> CYAN_WOOL_STAIRS = ItemsPart03.CYAN_WOOL_STAIRS;
    public static final RegistryObject<Item> CYAN_WOOL_WALL = ItemsPart03.CYAN_WOOL_WALL;
    public static final RegistryObject<Item> PURPLE_WOOL_SLAB = ItemsPart03.PURPLE_WOOL_SLAB;
    public static final RegistryObject<Item> PURPLE_WOOL_STAIRS = ItemsPart03.PURPLE_WOOL_STAIRS;
    public static final RegistryObject<Item> PURPLE_WOOL_WALL = ItemsPart03.PURPLE_WOOL_WALL;
    public static final RegistryObject<Item> BLUE_WOOL_SLAB = ItemsPart03.BLUE_WOOL_SLAB;
    public static final RegistryObject<Item> BLUE_WOOL_STAIRS = ItemsPart03.BLUE_WOOL_STAIRS;
    public static final RegistryObject<Item> BLUE_WOOL_WALL = ItemsPart03.BLUE_WOOL_WALL;
    public static final RegistryObject<Item> BROWN_WOOL_SLAB = ItemsPart03.BROWN_WOOL_SLAB;
    public static final RegistryObject<Item> BROWN_WOOL_STAIRS = ItemsPart03.BROWN_WOOL_STAIRS;
    public static final RegistryObject<Item> BROWN_WOOL_WALL = ItemsPart03.BROWN_WOOL_WALL;
    public static final RegistryObject<Item> GREEN_WOOL_SLAB = ItemsPart03.GREEN_WOOL_SLAB;
    public static final RegistryObject<Item> GREEN_WOOL_STAIRS = ItemsPart03.GREEN_WOOL_STAIRS;
    public static final RegistryObject<Item> GREEN_WOOL_WALL = ItemsPart03.GREEN_WOOL_WALL;
    public static final RegistryObject<Item> RED_WOOL_SLAB = ItemsPart03.RED_WOOL_SLAB;
    public static final RegistryObject<Item> RED_WOOL_STAIRS = ItemsPart03.RED_WOOL_STAIRS;
    public static final RegistryObject<Item> RED_WOOL_WALL = ItemsPart03.RED_WOOL_WALL;
    public static final RegistryObject<Item> BLACK_WOOL_SLAB = ItemsPart03.BLACK_WOOL_SLAB;
    public static final RegistryObject<Item> BLACK_WOOL_STAIRS = ItemsPart03.BLACK_WOOL_STAIRS;
    public static final RegistryObject<Item> BLACK_WOOL_WALL = ItemsPart03.BLACK_WOOL_WALL;
    public static final RegistryObject<Item> POPLAR_LOG = ItemsPart03.POPLAR_LOG;
    public static final RegistryObject<Item> POPLAR_PLANKS = ItemsPart03.POPLAR_PLANKS;
    public static final RegistryObject<Item> POPLAR_SAPLING = ItemsPart03.POPLAR_SAPLING;
    public static final RegistryObject<Item> POPLAR_BUTTON = ItemsPart03.POPLAR_BUTTON;
    public static final RegistryObject<Item> POPLAR_DOOR = ItemsPart03.POPLAR_DOOR;
    public static final RegistryObject<Item> POPLAR_FENCE = ItemsPart03.POPLAR_FENCE;
    public static final RegistryObject<Item> POPLAR_FENCE_GATE = ItemsPart03.POPLAR_FENCE_GATE;
    public static final RegistryObject<Item> POPLAR_LOG_SLAB = ItemsPart03.POPLAR_LOG_SLAB;
    public static final RegistryObject<Item> POPLAR_LOG_STAIRS = ItemsPart03.POPLAR_LOG_STAIRS;
    public static final RegistryObject<Item> POPLAR_LOG_WALL = ItemsPart03.POPLAR_LOG_WALL;
    public static final RegistryObject<Item> POPLAR_PRESSURE_PLATE = ItemsPart03.POPLAR_PRESSURE_PLATE;
    public static final RegistryObject<Item> POPLAR_SLAB = ItemsPart03.POPLAR_SLAB;
    public static final RegistryObject<Item> POPLAR_STAIRS = ItemsPart03.POPLAR_STAIRS;
    public static final RegistryObject<Item> POPLAR_TRAPDOOR = ItemsPart03.POPLAR_TRAPDOOR;
    public static final RegistryObject<Item> POPLAR_WOOD = ItemsPart03.POPLAR_WOOD;
    public static final RegistryObject<Item> POPLAR_WOOD_SLAB = ItemsPart03.POPLAR_WOOD_SLAB;
    public static final RegistryObject<Item> POPLAR_WOOD_STAIRS = ItemsPart03.POPLAR_WOOD_STAIRS;
    public static final RegistryObject<Item> STRIPPED_POPLAR_LOG = ItemsPart03.STRIPPED_POPLAR_LOG;
    public static final RegistryObject<Item> STRIPPED_POPLAR_LOG_SLAB = ItemsPart03.STRIPPED_POPLAR_LOG_SLAB;
    public static final RegistryObject<Item> STRIPPED_POPLAR_LOG_STAIRS = ItemsPart03.STRIPPED_POPLAR_LOG_STAIRS;
    public static final RegistryObject<Item> STRIPPED_POPLAR_LOG_WALL = ItemsPart03.STRIPPED_POPLAR_LOG_WALL;
    public static final RegistryObject<Item> STRIPPED_POPLAR_WOOD = ItemsPart03.STRIPPED_POPLAR_WOOD;
    public static final RegistryObject<Item> STRIPPED_POPLAR_WOOD_SLAB = ItemsPart03.STRIPPED_POPLAR_WOOD_SLAB;
    public static final RegistryObject<Item> STRIPPED_POPLAR_WOOD_STAIRS = ItemsPart03.STRIPPED_POPLAR_WOOD_STAIRS;
    public static final RegistryObject<Item> POPLAR_BOAT = ItemsPart03.POPLAR_BOAT;
    public static final RegistryObject<Item> ORANGE_POPLAR_LEAVES = ItemsPart03.ORANGE_POPLAR_LEAVES;
    public static final RegistryObject<Item> RED_POPLAR_LEAVES = ItemsPart03.RED_POPLAR_LEAVES;
    public static final RegistryObject<Item> YELLOW_POPLAR_LEAVES = ItemsPart03.YELLOW_POPLAR_LEAVES;
    public static final RegistryObject<Item> CINNABAR = ItemsPart03.CINNABAR;
    public static final RegistryObject<Item> POLISHED_CINNABAR = ItemsPart03.POLISHED_CINNABAR;
    public static final RegistryObject<Item> CINNABAR_SLAB = ItemsPart03.CINNABAR_SLAB;
    public static final RegistryObject<Item> CINNABAR_STAIRS = ItemsPart03.CINNABAR_STAIRS;
    public static final RegistryObject<Item> CINNABAR_WALL = ItemsPart03.CINNABAR_WALL;
    public static final RegistryObject<Item> POLISHED_CINNABAR_SLAB = ItemsPart03.POLISHED_CINNABAR_SLAB;
    public static final RegistryObject<Item> POLISHED_CINNABAR_STAIRS = ItemsPart03.POLISHED_CINNABAR_STAIRS;
    public static final RegistryObject<Item> POLISHED_CINNABAR_WALL = ItemsPart03.POLISHED_CINNABAR_WALL;
    public static final RegistryObject<Item> CINNABAR_BRICKS = ItemsPart03.CINNABAR_BRICKS;
    public static final RegistryObject<Item> CINNABAR_BRICKS_SLAB = ItemsPart03.CINNABAR_BRICKS_SLAB;
    public static final RegistryObject<Item> CINNABAR_BRICKS_STAIRS = ItemsPart03.CINNABAR_BRICKS_STAIRS;
    public static final RegistryObject<Item> CINNABAR_BRICKS_WALL = ItemsPart03.CINNABAR_BRICKS_WALL;
    public static final RegistryObject<Item> CHISELED_CINNABAR = ItemsPart03.CHISELED_CINNABAR;
    public static final RegistryObject<Item> SULFUR = ItemsPart03.SULFUR;
    public static final RegistryObject<Item> POLISHED_SULFUR = ItemsPart03.POLISHED_SULFUR;
    public static final RegistryObject<Item> SULFUR_SLAB = ItemsPart03.SULFUR_SLAB;
    public static final RegistryObject<Item> SULFUR_STAIRS = ItemsPart03.SULFUR_STAIRS;
    public static final RegistryObject<Item> SULFUR_WALL = ItemsPart03.SULFUR_WALL;
    public static final RegistryObject<Item> POLISHED_SULFUR_SLAB = ItemsPart03.POLISHED_SULFUR_SLAB;
    public static final RegistryObject<Item> POLISHED_SULFUR_STAIRS = ItemsPart03.POLISHED_SULFUR_STAIRS;
    public static final RegistryObject<Item> POLISHED_SULFUR_WALL = ItemsPart03.POLISHED_SULFUR_WALL;
    public static final RegistryObject<Item> SULFUR_BRICKS = ItemsPart03.SULFUR_BRICKS;
    public static final RegistryObject<Item> SULFUR_BRICKS_SLAB = ItemsPart03.SULFUR_BRICKS_SLAB;
    public static final RegistryObject<Item> SULFUR_BRICKS_STAIRS = ItemsPart03.SULFUR_BRICKS_STAIRS;
    public static final RegistryObject<Item> SULFUR_BRICKS_WALL = ItemsPart03.SULFUR_BRICKS_WALL;
    public static final RegistryObject<Item> CHISELED_SULFUR = ItemsPart03.CHISELED_SULFUR;
    public static final RegistryObject<Item> POTENT_SULFUR = ItemsPart03.POTENT_SULFUR;
    public static final RegistryObject<Item> SULFUR_SPIKE = ItemsPart03.SULFUR_SPIKE;
    public static final RegistryObject<Item> WHITE_WOOL_VERTICAL_SLAB = ItemsPart03.WHITE_WOOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> ORANGE_WOOL_VERTICAL_SLAB = ItemsPart03.ORANGE_WOOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> MAGENTA_WOOL_VERTICAL_SLAB = ItemsPart03.MAGENTA_WOOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIGHT_BLUE_WOOL_VERTICAL_SLAB = ItemsPart03.LIGHT_BLUE_WOOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> YELLOW_WOOL_VERTICAL_SLAB = ItemsPart03.YELLOW_WOOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIME_WOOL_VERTICAL_SLAB = ItemsPart03.LIME_WOOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> PINK_WOOL_VERTICAL_SLAB = ItemsPart03.PINK_WOOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> GRAY_WOOL_VERTICAL_SLAB = ItemsPart03.GRAY_WOOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_WOOL_VERTICAL_SLAB = ItemsPart03.LIGHT_GRAY_WOOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> CYAN_WOOL_VERTICAL_SLAB = ItemsPart03.CYAN_WOOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> PURPLE_WOOL_VERTICAL_SLAB = ItemsPart03.PURPLE_WOOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLUE_WOOL_VERTICAL_SLAB = ItemsPart03.BLUE_WOOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> BROWN_WOOL_VERTICAL_SLAB = ItemsPart03.BROWN_WOOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> GREEN_WOOL_VERTICAL_SLAB = ItemsPart03.GREEN_WOOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> RED_WOOL_VERTICAL_SLAB = ItemsPart03.RED_WOOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLACK_WOOL_VERTICAL_SLAB = ItemsPart03.BLACK_WOOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> RED_MOSS_BLOCK_VERTICAL_SLAB = ItemsPart03.RED_MOSS_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> ORANGE_MOSS_BLOCK_VERTICAL_SLAB =
            ItemsPart03.ORANGE_MOSS_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> YELLOW_MOSS_BLOCK_VERTICAL_SLAB =
            ItemsPart03.YELLOW_MOSS_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> TUFF_VERTICAL_SLAB = ItemsPart03.TUFF_VERTICAL_SLAB;
    public static final RegistryObject<Item> POLISHED_TUFF_VERTICAL_SLAB = ItemsPart03.POLISHED_TUFF_VERTICAL_SLAB;
    public static final RegistryObject<Item> TUFF_BRICK_VERTICAL_SLAB = ItemsPart03.TUFF_BRICK_VERTICAL_SLAB;
    public static final RegistryObject<Item> SULFUR_VERTICAL_SLAB = ItemsPart03.SULFUR_VERTICAL_SLAB;
    public static final RegistryObject<Item> POLISHED_SULFUR_VERTICAL_SLAB = ItemsPart03.POLISHED_SULFUR_VERTICAL_SLAB;
    public static final RegistryObject<Item> SULFUR_BRICKS_VERTICAL_SLAB = ItemsPart03.SULFUR_BRICKS_VERTICAL_SLAB;
    public static final RegistryObject<Item> CINNABAR_VERTICAL_SLAB = ItemsPart03.CINNABAR_VERTICAL_SLAB;
    public static final RegistryObject<Item> POLISHED_CINNABAR_VERTICAL_SLAB =
            ItemsPart03.POLISHED_CINNABAR_VERTICAL_SLAB;
    public static final RegistryObject<Item> CINNABAR_BRICKS_VERTICAL_SLAB = ItemsPart03.CINNABAR_BRICKS_VERTICAL_SLAB;
    public static final RegistryObject<Item> POPLAR_VERTICAL_SLAB = ItemsPart03.POPLAR_VERTICAL_SLAB;
    public static final RegistryObject<Item> POPLAR_LOG_VERTICAL_SLAB = ItemsPart03.POPLAR_LOG_VERTICAL_SLAB;
    public static final RegistryObject<Item> STRIPPED_POPLAR_LOG_VERTICAL_SLAB =
            ItemsPart03.STRIPPED_POPLAR_LOG_VERTICAL_SLAB;
    public static final RegistryObject<Item> POPLAR_WOOD_VERTICAL_SLAB = ItemsPart03.POPLAR_WOOD_VERTICAL_SLAB;
    public static final RegistryObject<Item> STRIPPED_POPLAR_WOOD_VERTICAL_SLAB =
            ItemsPart03.STRIPPED_POPLAR_WOOD_VERTICAL_SLAB;
    public static final RegistryObject<Item> OAK_VERTICAL_SLAB = ItemsPart03.OAK_VERTICAL_SLAB;
    public static final RegistryObject<Item> SPRUCE_VERTICAL_SLAB = ItemsPart03.SPRUCE_VERTICAL_SLAB;
    public static final RegistryObject<Item> BIRCH_VERTICAL_SLAB = ItemsPart03.BIRCH_VERTICAL_SLAB;
    public static final RegistryObject<Item> JUNGLE_VERTICAL_SLAB = ItemsPart03.JUNGLE_VERTICAL_SLAB;
    public static final RegistryObject<Item> ACACIA_VERTICAL_SLAB = ItemsPart03.ACACIA_VERTICAL_SLAB;
    public static final RegistryObject<Item> DARK_OAK_VERTICAL_SLAB = ItemsPart03.DARK_OAK_VERTICAL_SLAB;
    public static final RegistryObject<Item> CRIMSON_VERTICAL_SLAB = ItemsPart03.CRIMSON_VERTICAL_SLAB;
    public static final RegistryObject<Item> WARPED_VERTICAL_SLAB = ItemsPart03.WARPED_VERTICAL_SLAB;
    public static final RegistryObject<Item> STONE_VERTICAL_SLAB = ItemsPart03.STONE_VERTICAL_SLAB;
    public static final RegistryObject<Item> SMOOTH_STONE_VERTICAL_SLAB = ItemsPart03.SMOOTH_STONE_VERTICAL_SLAB;
    public static final RegistryObject<Item> GRANITE_VERTICAL_SLAB = ItemsPart03.GRANITE_VERTICAL_SLAB;
    public static final RegistryObject<Item> POLISHED_GRANITE_VERTICAL_SLAB =
            ItemsPart03.POLISHED_GRANITE_VERTICAL_SLAB;
    public static final RegistryObject<Item> DIORITE_VERTICAL_SLAB = ItemsPart03.DIORITE_VERTICAL_SLAB;
    public static final RegistryObject<Item> POLISHED_DIORITE_VERTICAL_SLAB =
            ItemsPart03.POLISHED_DIORITE_VERTICAL_SLAB;
    public static final RegistryObject<Item> ANDESITE_VERTICAL_SLAB = ItemsPart03.ANDESITE_VERTICAL_SLAB;
    public static final RegistryObject<Item> POLISHED_ANDESITE_VERTICAL_SLAB =
            ItemsPart03.POLISHED_ANDESITE_VERTICAL_SLAB;
    public static final RegistryObject<Item> COBBLESTONE_VERTICAL_SLAB = ItemsPart03.COBBLESTONE_VERTICAL_SLAB;
    public static final RegistryObject<Item> MOSSY_COBBLESTONE_VERTICAL_SLAB =
            ItemsPart03.MOSSY_COBBLESTONE_VERTICAL_SLAB;
    public static final RegistryObject<Item> STONE_BRICK_VERTICAL_SLAB = ItemsPart03.STONE_BRICK_VERTICAL_SLAB;
    public static final RegistryObject<Item> MOSSY_STONE_BRICK_VERTICAL_SLAB =
            ItemsPart03.MOSSY_STONE_BRICK_VERTICAL_SLAB;
    public static final RegistryObject<Item> NETHER_BRICK_VERTICAL_SLAB = ItemsPart03.NETHER_BRICK_VERTICAL_SLAB;
    public static final RegistryObject<Item> RED_NETHER_BRICK_VERTICAL_SLAB =
            ItemsPart03.RED_NETHER_BRICK_VERTICAL_SLAB;
    public static final RegistryObject<Item> OAK_LOG_STAIRS = ItemsPart03.OAK_LOG_STAIRS;
    public static final RegistryObject<Item> CUT_COPPER_VERTICAL_SLAB = ItemsPart03.CUT_COPPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> EXPOSED_CUT_COPPER_VERTICAL_SLAB =
            ItemsPart03.EXPOSED_CUT_COPPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> WEATHERED_CUT_COPPER_VERTICAL_SLAB =
            ItemsPart03.WEATHERED_CUT_COPPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> OXIDIZED_CUT_COPPER_VERTICAL_SLAB =
            ItemsPart03.OXIDIZED_CUT_COPPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> WAXED_CUT_COPPER_VERTICAL_SLAB =
            ItemsPart03.WAXED_CUT_COPPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> WAXED_EXPOSED_CUT_COPPER_VERTICAL_SLAB =
            ItemsPart03.WAXED_EXPOSED_CUT_COPPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> WAXED_WEATHERED_CUT_COPPER_VERTICAL_SLAB =
            ItemsPart03.WAXED_WEATHERED_CUT_COPPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> WAXED_OXIDIZED_CUT_COPPER_VERTICAL_SLAB =
            ItemsPart03.WAXED_OXIDIZED_CUT_COPPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> COBBLED_DEEPSLATE_VERTICAL_SLAB =
            ItemsPart03.COBBLED_DEEPSLATE_VERTICAL_SLAB;
    public static final RegistryObject<Item> POLISHED_DEEPSLATE_VERTICAL_SLAB =
            ItemsPart03.POLISHED_DEEPSLATE_VERTICAL_SLAB;
    public static final RegistryObject<Item> DEEPSLATE_BRICK_VERTICAL_SLAB = ItemsPart03.DEEPSLATE_BRICK_VERTICAL_SLAB;
    public static final RegistryObject<Item> DEEPSLATE_TILE_VERTICAL_SLAB = ItemsPart03.DEEPSLATE_TILE_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLACKSTONE_VERTICAL_SLAB = ItemsPart03.BLACKSTONE_VERTICAL_SLAB;
    public static final RegistryObject<Item> POLISHED_BLACKSTONE_VERTICAL_SLAB =
            ItemsPart03.POLISHED_BLACKSTONE_VERTICAL_SLAB;
    public static final RegistryObject<Item> POLISHED_BLACKSTONE_BRICK_VERTICAL_SLAB =
            ItemsPart03.POLISHED_BLACKSTONE_BRICK_VERTICAL_SLAB;
    public static final RegistryObject<Item> PRISMARINE_VERTICAL_SLAB = ItemsPart03.PRISMARINE_VERTICAL_SLAB;
    public static final RegistryObject<Item> PRISMARINE_BRICK_VERTICAL_SLAB =
            ItemsPart03.PRISMARINE_BRICK_VERTICAL_SLAB;
    public static final RegistryObject<Item> DARK_PRISMARINE_VERTICAL_SLAB = ItemsPart03.DARK_PRISMARINE_VERTICAL_SLAB;
    public static final RegistryObject<Item> BRICK_VERTICAL_SLAB = ItemsPart03.BRICK_VERTICAL_SLAB;
    public static final RegistryObject<Item> END_STONE_BRICK_VERTICAL_SLAB = ItemsPart03.END_STONE_BRICK_VERTICAL_SLAB;
    public static final RegistryObject<Item> PURPUR_VERTICAL_SLAB = ItemsPart03.PURPUR_VERTICAL_SLAB;
    public static final RegistryObject<Item> QUARTZ_VERTICAL_SLAB = ItemsPart04.QUARTZ_VERTICAL_SLAB;
    public static final RegistryObject<Item> SMOOTH_QUARTZ_VERTICAL_SLAB = ItemsPart04.SMOOTH_QUARTZ_VERTICAL_SLAB;
    public static final RegistryObject<Item> TERRACOTTA_SLAB = ItemsPart04.TERRACOTTA_SLAB;
    public static final RegistryObject<Item> TERRACOTTA_STAIRS = ItemsPart04.TERRACOTTA_STAIRS;
    public static final RegistryObject<Item> TERRACOTTA_VERTICAL_SLAB = ItemsPart04.TERRACOTTA_VERTICAL_SLAB;
    public static final RegistryObject<Item> WHITE_TERRACOTTA_SLAB = ItemsPart04.WHITE_TERRACOTTA_SLAB;
    public static final RegistryObject<Item> WHITE_TERRACOTTA_STAIRS = ItemsPart04.WHITE_TERRACOTTA_STAIRS;
    public static final RegistryObject<Item> WHITE_TERRACOTTA_VERTICAL_SLAB =
            ItemsPart04.WHITE_TERRACOTTA_VERTICAL_SLAB;
    public static final RegistryObject<Item> ORANGE_TERRACOTTA_SLAB = ItemsPart04.ORANGE_TERRACOTTA_SLAB;
    public static final RegistryObject<Item> ORANGE_TERRACOTTA_STAIRS = ItemsPart04.ORANGE_TERRACOTTA_STAIRS;
    public static final RegistryObject<Item> ORANGE_TERRACOTTA_VERTICAL_SLAB =
            ItemsPart04.ORANGE_TERRACOTTA_VERTICAL_SLAB;
    public static final RegistryObject<Item> MAGENTA_TERRACOTTA_SLAB = ItemsPart04.MAGENTA_TERRACOTTA_SLAB;
    public static final RegistryObject<Item> MAGENTA_TERRACOTTA_STAIRS = ItemsPart04.MAGENTA_TERRACOTTA_STAIRS;
    public static final RegistryObject<Item> MAGENTA_TERRACOTTA_VERTICAL_SLAB =
            ItemsPart04.MAGENTA_TERRACOTTA_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIGHT_BLUE_TERRACOTTA_SLAB = ItemsPart04.LIGHT_BLUE_TERRACOTTA_SLAB;
    public static final RegistryObject<Item> LIGHT_BLUE_TERRACOTTA_STAIRS = ItemsPart04.LIGHT_BLUE_TERRACOTTA_STAIRS;
    public static final RegistryObject<Item> LIGHT_BLUE_TERRACOTTA_VERTICAL_SLAB =
            ItemsPart04.LIGHT_BLUE_TERRACOTTA_VERTICAL_SLAB;
    public static final RegistryObject<Item> YELLOW_TERRACOTTA_SLAB = ItemsPart04.YELLOW_TERRACOTTA_SLAB;
    public static final RegistryObject<Item> YELLOW_TERRACOTTA_STAIRS = ItemsPart04.YELLOW_TERRACOTTA_STAIRS;
    public static final RegistryObject<Item> YELLOW_TERRACOTTA_VERTICAL_SLAB =
            ItemsPart04.YELLOW_TERRACOTTA_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIME_TERRACOTTA_SLAB = ItemsPart04.LIME_TERRACOTTA_SLAB;
    public static final RegistryObject<Item> LIME_TERRACOTTA_STAIRS = ItemsPart04.LIME_TERRACOTTA_STAIRS;
    public static final RegistryObject<Item> LIME_TERRACOTTA_VERTICAL_SLAB = ItemsPart04.LIME_TERRACOTTA_VERTICAL_SLAB;
    public static final RegistryObject<Item> PINK_TERRACOTTA_SLAB = ItemsPart04.PINK_TERRACOTTA_SLAB;
    public static final RegistryObject<Item> PINK_TERRACOTTA_STAIRS = ItemsPart04.PINK_TERRACOTTA_STAIRS;
    public static final RegistryObject<Item> PINK_TERRACOTTA_VERTICAL_SLAB = ItemsPart04.PINK_TERRACOTTA_VERTICAL_SLAB;
    public static final RegistryObject<Item> GRAY_TERRACOTTA_SLAB = ItemsPart04.GRAY_TERRACOTTA_SLAB;
    public static final RegistryObject<Item> GRAY_TERRACOTTA_STAIRS = ItemsPart04.GRAY_TERRACOTTA_STAIRS;
    public static final RegistryObject<Item> GRAY_TERRACOTTA_VERTICAL_SLAB = ItemsPart04.GRAY_TERRACOTTA_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_TERRACOTTA_SLAB = ItemsPart04.LIGHT_GRAY_TERRACOTTA_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_TERRACOTTA_STAIRS = ItemsPart04.LIGHT_GRAY_TERRACOTTA_STAIRS;
    public static final RegistryObject<Item> LIGHT_GRAY_TERRACOTTA_VERTICAL_SLAB =
            ItemsPart04.LIGHT_GRAY_TERRACOTTA_VERTICAL_SLAB;
    public static final RegistryObject<Item> CYAN_TERRACOTTA_SLAB = ItemsPart04.CYAN_TERRACOTTA_SLAB;
    public static final RegistryObject<Item> CYAN_TERRACOTTA_STAIRS = ItemsPart04.CYAN_TERRACOTTA_STAIRS;
    public static final RegistryObject<Item> CYAN_TERRACOTTA_VERTICAL_SLAB = ItemsPart04.CYAN_TERRACOTTA_VERTICAL_SLAB;
    public static final RegistryObject<Item> PURPLE_TERRACOTTA_SLAB = ItemsPart04.PURPLE_TERRACOTTA_SLAB;
    public static final RegistryObject<Item> PURPLE_TERRACOTTA_STAIRS = ItemsPart04.PURPLE_TERRACOTTA_STAIRS;
    public static final RegistryObject<Item> PURPLE_TERRACOTTA_VERTICAL_SLAB =
            ItemsPart04.PURPLE_TERRACOTTA_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLUE_TERRACOTTA_SLAB = ItemsPart04.BLUE_TERRACOTTA_SLAB;
    public static final RegistryObject<Item> BLUE_TERRACOTTA_STAIRS = ItemsPart04.BLUE_TERRACOTTA_STAIRS;
    public static final RegistryObject<Item> BLUE_TERRACOTTA_VERTICAL_SLAB = ItemsPart04.BLUE_TERRACOTTA_VERTICAL_SLAB;
    public static final RegistryObject<Item> BROWN_TERRACOTTA_SLAB = ItemsPart04.BROWN_TERRACOTTA_SLAB;
    public static final RegistryObject<Item> BROWN_TERRACOTTA_STAIRS = ItemsPart04.BROWN_TERRACOTTA_STAIRS;
    public static final RegistryObject<Item> BROWN_TERRACOTTA_VERTICAL_SLAB =
            ItemsPart04.BROWN_TERRACOTTA_VERTICAL_SLAB;
    public static final RegistryObject<Item> GREEN_TERRACOTTA_SLAB = ItemsPart04.GREEN_TERRACOTTA_SLAB;
    public static final RegistryObject<Item> GREEN_TERRACOTTA_STAIRS = ItemsPart04.GREEN_TERRACOTTA_STAIRS;
    public static final RegistryObject<Item> GREEN_TERRACOTTA_VERTICAL_SLAB =
            ItemsPart04.GREEN_TERRACOTTA_VERTICAL_SLAB;
    public static final RegistryObject<Item> RED_TERRACOTTA_SLAB = ItemsPart04.RED_TERRACOTTA_SLAB;
    public static final RegistryObject<Item> RED_TERRACOTTA_STAIRS = ItemsPart04.RED_TERRACOTTA_STAIRS;
    public static final RegistryObject<Item> RED_TERRACOTTA_VERTICAL_SLAB = ItemsPart04.RED_TERRACOTTA_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLACK_TERRACOTTA_SLAB = ItemsPart04.BLACK_TERRACOTTA_SLAB;
    public static final RegistryObject<Item> BLACK_TERRACOTTA_STAIRS = ItemsPart04.BLACK_TERRACOTTA_STAIRS;
    public static final RegistryObject<Item> BLACK_TERRACOTTA_VERTICAL_SLAB =
            ItemsPart04.BLACK_TERRACOTTA_VERTICAL_SLAB;
    public static final RegistryObject<Item> STRIPPED_OAK_LOG_STAIRS = ItemsPart04.STRIPPED_OAK_LOG_STAIRS;
    public static final RegistryObject<Item> SPRUCE_LOG_STAIRS = ItemsPart04.SPRUCE_LOG_STAIRS;
    public static final RegistryObject<Item> STRIPPED_SPRUCE_LOG_STAIRS = ItemsPart04.STRIPPED_SPRUCE_LOG_STAIRS;
    public static final RegistryObject<Item> BIRCH_LOG_STAIRS = ItemsPart04.BIRCH_LOG_STAIRS;
    public static final RegistryObject<Item> STRIPPED_BIRCH_LOG_STAIRS = ItemsPart04.STRIPPED_BIRCH_LOG_STAIRS;
    public static final RegistryObject<Item> JUNGLE_LOG_STAIRS = ItemsPart04.JUNGLE_LOG_STAIRS;
    public static final RegistryObject<Item> STRIPPED_JUNGLE_LOG_STAIRS = ItemsPart04.STRIPPED_JUNGLE_LOG_STAIRS;
    public static final RegistryObject<Item> ACACIA_LOG_STAIRS = ItemsPart04.ACACIA_LOG_STAIRS;
    public static final RegistryObject<Item> STRIPPED_ACACIA_LOG_STAIRS = ItemsPart04.STRIPPED_ACACIA_LOG_STAIRS;
    public static final RegistryObject<Item> DARK_OAK_LOG_STAIRS = ItemsPart04.DARK_OAK_LOG_STAIRS;
    public static final RegistryObject<Item> STRIPPED_DARK_OAK_LOG_STAIRS = ItemsPart04.STRIPPED_DARK_OAK_LOG_STAIRS;
    public static final RegistryObject<Item> CRIMSON_STEM_STAIRS = ItemsPart04.CRIMSON_STEM_STAIRS;
    public static final RegistryObject<Item> STRIPPED_CRIMSON_STEM_STAIRS = ItemsPart04.STRIPPED_CRIMSON_STEM_STAIRS;
    public static final RegistryObject<Item> WARPED_STEM_STAIRS = ItemsPart04.WARPED_STEM_STAIRS;
    public static final RegistryObject<Item> STRIPPED_WARPED_STEM_STAIRS = ItemsPart04.STRIPPED_WARPED_STEM_STAIRS;
    public static final RegistryObject<Item> ACACIA_WOOD_SLAB = ItemsPart04.ACACIA_WOOD_SLAB;
    public static final RegistryObject<Item> ACACIA_WOOD_STAIRS = ItemsPart04.ACACIA_WOOD_STAIRS;
    public static final RegistryObject<Item> ACACIA_WOOD_VERTICAL_SLAB = ItemsPart04.ACACIA_WOOD_VERTICAL_SLAB;
    public static final RegistryObject<Item> AMETHYST_BLOCK_STAIRS = ItemsPart04.AMETHYST_BLOCK_STAIRS;
    public static final RegistryObject<Item> BIRCH_WOOD_SLAB = ItemsPart04.BIRCH_WOOD_SLAB;
    public static final RegistryObject<Item> BIRCH_WOOD_STAIRS = ItemsPart04.BIRCH_WOOD_STAIRS;
    public static final RegistryObject<Item> BIRCH_WOOD_VERTICAL_SLAB = ItemsPart04.BIRCH_WOOD_VERTICAL_SLAB;
    public static final RegistryObject<Item> CRIMSON_HYPHAE_SLAB = ItemsPart04.CRIMSON_HYPHAE_SLAB;
    public static final RegistryObject<Item> CRIMSON_HYPHAE_STAIRS = ItemsPart04.CRIMSON_HYPHAE_STAIRS;
    public static final RegistryObject<Item> CRIMSON_HYPHAE_VERTICAL_SLAB = ItemsPart04.CRIMSON_HYPHAE_VERTICAL_SLAB;
    public static final RegistryObject<Item> CRYING_OBSIDIAN_SLAB = ItemsPart04.CRYING_OBSIDIAN_SLAB;
    public static final RegistryObject<Item> CRYING_OBSIDIAN_STAIRS = ItemsPart04.CRYING_OBSIDIAN_STAIRS;
    public static final RegistryObject<Item> CRYING_OBSIDIAN_VERTICAL_SLAB = ItemsPart04.CRYING_OBSIDIAN_VERTICAL_SLAB;
    public static final RegistryObject<Item> CUT_RED_SANDSTONE_STAIRS = ItemsPart04.CUT_RED_SANDSTONE_STAIRS;
    public static final RegistryObject<Item> CUT_RED_SANDSTONE_VERTICAL_SLAB =
            ItemsPart04.CUT_RED_SANDSTONE_VERTICAL_SLAB;
    public static final RegistryObject<Item> CUT_SANDSTONE_STAIRS = ItemsPart04.CUT_SANDSTONE_STAIRS;
    public static final RegistryObject<Item> CUT_SANDSTONE_VERTICAL_SLAB = ItemsPart04.CUT_SANDSTONE_VERTICAL_SLAB;
    public static final RegistryObject<Item> DARK_OAK_WOOD_SLAB = ItemsPart04.DARK_OAK_WOOD_SLAB;
    public static final RegistryObject<Item> DARK_OAK_WOOD_STAIRS = ItemsPart04.DARK_OAK_WOOD_STAIRS;
    public static final RegistryObject<Item> DARK_OAK_WOOD_VERTICAL_SLAB = ItemsPart04.DARK_OAK_WOOD_VERTICAL_SLAB;
    public static final RegistryObject<Item> DIRT_STAIRS = ItemsPart04.DIRT_STAIRS;
    public static final RegistryObject<Item> JUNGLE_WOOD_SLAB = ItemsPart04.JUNGLE_WOOD_SLAB;
    public static final RegistryObject<Item> JUNGLE_WOOD_STAIRS = ItemsPart04.JUNGLE_WOOD_STAIRS;
    public static final RegistryObject<Item> JUNGLE_WOOD_VERTICAL_SLAB = ItemsPart04.JUNGLE_WOOD_VERTICAL_SLAB;
    public static final RegistryObject<Item> MOSS_BLOCK_STAIRS = ItemsPart04.MOSS_BLOCK_STAIRS;
    public static final RegistryObject<Item> MYCELIUM_STAIRS = ItemsPart04.MYCELIUM_STAIRS;
    public static final RegistryObject<Item> OAK_WOOD_SLAB = ItemsPart04.OAK_WOOD_SLAB;
    public static final RegistryObject<Item> OAK_WOOD_STAIRS = ItemsPart04.OAK_WOOD_STAIRS;
    public static final RegistryObject<Item> OAK_WOOD_VERTICAL_SLAB = ItemsPart04.OAK_WOOD_VERTICAL_SLAB;
    public static final RegistryObject<Item> PODZOL_STAIRS = ItemsPart04.PODZOL_STAIRS;
    public static final RegistryObject<Item> SANDSTONE_VERTICAL_SLAB = ItemsPart04.SANDSTONE_VERTICAL_SLAB;
    public static final RegistryObject<Item> SMOOTH_RED_SANDSTONE_VERTICAL_SLAB =
            ItemsPart04.SMOOTH_RED_SANDSTONE_VERTICAL_SLAB;
    public static final RegistryObject<Item> SMOOTH_SANDSTONE_VERTICAL_SLAB =
            ItemsPart04.SMOOTH_SANDSTONE_VERTICAL_SLAB;
    public static final RegistryObject<Item> SPRUCE_WOOD_SLAB = ItemsPart04.SPRUCE_WOOD_SLAB;
    public static final RegistryObject<Item> SPRUCE_WOOD_STAIRS = ItemsPart04.SPRUCE_WOOD_STAIRS;
    public static final RegistryObject<Item> SPRUCE_WOOD_VERTICAL_SLAB = ItemsPart04.SPRUCE_WOOD_VERTICAL_SLAB;
    public static final RegistryObject<Item> STRIPPED_ACACIA_WOOD_SLAB = ItemsPart04.STRIPPED_ACACIA_WOOD_SLAB;
    public static final RegistryObject<Item> STRIPPED_ACACIA_WOOD_STAIRS = ItemsPart04.STRIPPED_ACACIA_WOOD_STAIRS;
    public static final RegistryObject<Item> STRIPPED_ACACIA_WOOD_VERTICAL_SLAB =
            ItemsPart04.STRIPPED_ACACIA_WOOD_VERTICAL_SLAB;
    public static final RegistryObject<Item> STRIPPED_BIRCH_WOOD_SLAB = ItemsPart04.STRIPPED_BIRCH_WOOD_SLAB;
    public static final RegistryObject<Item> STRIPPED_BIRCH_WOOD_STAIRS = ItemsPart04.STRIPPED_BIRCH_WOOD_STAIRS;
    public static final RegistryObject<Item> STRIPPED_BIRCH_WOOD_VERTICAL_SLAB =
            ItemsPart04.STRIPPED_BIRCH_WOOD_VERTICAL_SLAB;
    public static final RegistryObject<Item> STRIPPED_CRIMSON_HYPHAE_SLAB = ItemsPart04.STRIPPED_CRIMSON_HYPHAE_SLAB;
    public static final RegistryObject<Item> STRIPPED_CRIMSON_HYPHAE_STAIRS =
            ItemsPart04.STRIPPED_CRIMSON_HYPHAE_STAIRS;
    public static final RegistryObject<Item> STRIPPED_CRIMSON_HYPHAE_VERTICAL_SLAB =
            ItemsPart04.STRIPPED_CRIMSON_HYPHAE_VERTICAL_SLAB;
    public static final RegistryObject<Item> STRIPPED_DARK_OAK_WOOD_SLAB = ItemsPart04.STRIPPED_DARK_OAK_WOOD_SLAB;
    public static final RegistryObject<Item> STRIPPED_DARK_OAK_WOOD_STAIRS = ItemsPart04.STRIPPED_DARK_OAK_WOOD_STAIRS;
    public static final RegistryObject<Item> STRIPPED_DARK_OAK_WOOD_VERTICAL_SLAB =
            ItemsPart04.STRIPPED_DARK_OAK_WOOD_VERTICAL_SLAB;
    public static final RegistryObject<Item> STRIPPED_JUNGLE_WOOD_SLAB = ItemsPart04.STRIPPED_JUNGLE_WOOD_SLAB;
    public static final RegistryObject<Item> STRIPPED_JUNGLE_WOOD_STAIRS = ItemsPart04.STRIPPED_JUNGLE_WOOD_STAIRS;
    public static final RegistryObject<Item> STRIPPED_JUNGLE_WOOD_VERTICAL_SLAB =
            ItemsPart04.STRIPPED_JUNGLE_WOOD_VERTICAL_SLAB;
    public static final RegistryObject<Item> STRIPPED_OAK_WOOD_SLAB = ItemsPart04.STRIPPED_OAK_WOOD_SLAB;
    public static final RegistryObject<Item> STRIPPED_OAK_WOOD_STAIRS = ItemsPart04.STRIPPED_OAK_WOOD_STAIRS;
    public static final RegistryObject<Item> STRIPPED_OAK_WOOD_VERTICAL_SLAB =
            ItemsPart04.STRIPPED_OAK_WOOD_VERTICAL_SLAB;
    public static final RegistryObject<Item> STRIPPED_SPRUCE_WOOD_SLAB = ItemsPart04.STRIPPED_SPRUCE_WOOD_SLAB;
    public static final RegistryObject<Item> STRIPPED_SPRUCE_WOOD_STAIRS = ItemsPart04.STRIPPED_SPRUCE_WOOD_STAIRS;
    public static final RegistryObject<Item> STRIPPED_SPRUCE_WOOD_VERTICAL_SLAB =
            ItemsPart04.STRIPPED_SPRUCE_WOOD_VERTICAL_SLAB;
    public static final RegistryObject<Item> STRIPPED_WARPED_HYPHAE_SLAB = ItemsPart04.STRIPPED_WARPED_HYPHAE_SLAB;
    public static final RegistryObject<Item> STRIPPED_WARPED_HYPHAE_STAIRS = ItemsPart04.STRIPPED_WARPED_HYPHAE_STAIRS;
    public static final RegistryObject<Item> STRIPPED_WARPED_HYPHAE_VERTICAL_SLAB =
            ItemsPart04.STRIPPED_WARPED_HYPHAE_VERTICAL_SLAB;
    public static final RegistryObject<Item> TINTED_GLASS_SLAB = ItemsPart04.TINTED_GLASS_SLAB;
    public static final RegistryObject<Item> TINTED_GLASS_STAIRS = ItemsPart04.TINTED_GLASS_STAIRS;
    public static final RegistryObject<Item> TINTED_GLASS_VERTICAL_SLAB = ItemsPart04.TINTED_GLASS_VERTICAL_SLAB;
    public static final RegistryObject<Item> WARPED_HYPHAE_SLAB = ItemsPart04.WARPED_HYPHAE_SLAB;
    public static final RegistryObject<Item> WARPED_HYPHAE_STAIRS = ItemsPart04.WARPED_HYPHAE_STAIRS;
    public static final RegistryObject<Item> WARPED_HYPHAE_VERTICAL_SLAB = ItemsPart04.WARPED_HYPHAE_VERTICAL_SLAB;
    public static final RegistryObject<Item> MUFF_BLOCK = ItemsPart04.MUFF_BLOCK;
    public static final RegistryObject<Item> WHITE_WALLPAPER = ItemsPart04.WHITE_WALLPAPER;
    public static final RegistryObject<Item> ORANGE_WALLPAPER = ItemsPart04.ORANGE_WALLPAPER;
    public static final RegistryObject<Item> MAGENTA_WALLPAPER = ItemsPart04.MAGENTA_WALLPAPER;
    public static final RegistryObject<Item> LIGHT_BLUE_WALLPAPER = ItemsPart04.LIGHT_BLUE_WALLPAPER;
    public static final RegistryObject<Item> YELLOW_WALLPAPER = ItemsPart04.YELLOW_WALLPAPER;
    public static final RegistryObject<Item> LIME_WALLPAPER = ItemsPart04.LIME_WALLPAPER;
    public static final RegistryObject<Item> PINK_WALLPAPER = ItemsPart04.PINK_WALLPAPER;
    public static final RegistryObject<Item> GRAY_WALLPAPER = ItemsPart04.GRAY_WALLPAPER;
    public static final RegistryObject<Item> LIGHT_GRAY_WALLPAPER = ItemsPart04.LIGHT_GRAY_WALLPAPER;
    public static final RegistryObject<Item> CYAN_WALLPAPER = ItemsPart04.CYAN_WALLPAPER;
    public static final RegistryObject<Item> PURPLE_WALLPAPER = ItemsPart04.PURPLE_WALLPAPER;
    public static final RegistryObject<Item> BLUE_WALLPAPER = ItemsPart04.BLUE_WALLPAPER;
    public static final RegistryObject<Item> BROWN_WALLPAPER = ItemsPart04.BROWN_WALLPAPER;
    public static final RegistryObject<Item> GREEN_WALLPAPER = ItemsPart04.GREEN_WALLPAPER;
    public static final RegistryObject<Item> RED_WALLPAPER = ItemsPart04.RED_WALLPAPER;
    public static final RegistryObject<Item> BLACK_WALLPAPER = ItemsPart04.BLACK_WALLPAPER;
    public static final RegistryObject<Item> WHITE_WALLPAPER_SLAB = ItemsPart04.WHITE_WALLPAPER_SLAB;
    public static final RegistryObject<Item> WHITE_WALLPAPER_VERTICAL_SLAB = ItemsPart04.WHITE_WALLPAPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> ORANGE_WALLPAPER_SLAB = ItemsPart04.ORANGE_WALLPAPER_SLAB;
    public static final RegistryObject<Item> ORANGE_WALLPAPER_VERTICAL_SLAB =
            ItemsPart04.ORANGE_WALLPAPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> MAGENTA_WALLPAPER_SLAB = ItemsPart04.MAGENTA_WALLPAPER_SLAB;
    public static final RegistryObject<Item> MAGENTA_WALLPAPER_VERTICAL_SLAB =
            ItemsPart04.MAGENTA_WALLPAPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIGHT_BLUE_WALLPAPER_SLAB = ItemsPart04.LIGHT_BLUE_WALLPAPER_SLAB;
    public static final RegistryObject<Item> LIGHT_BLUE_WALLPAPER_VERTICAL_SLAB =
            ItemsPart04.LIGHT_BLUE_WALLPAPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> YELLOW_WALLPAPER_SLAB = ItemsPart04.YELLOW_WALLPAPER_SLAB;
    public static final RegistryObject<Item> YELLOW_WALLPAPER_VERTICAL_SLAB =
            ItemsPart04.YELLOW_WALLPAPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIME_WALLPAPER_SLAB = ItemsPart04.LIME_WALLPAPER_SLAB;
    public static final RegistryObject<Item> LIME_WALLPAPER_VERTICAL_SLAB = ItemsPart04.LIME_WALLPAPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> PINK_WALLPAPER_SLAB = ItemsPart04.PINK_WALLPAPER_SLAB;
    public static final RegistryObject<Item> PINK_WALLPAPER_VERTICAL_SLAB = ItemsPart04.PINK_WALLPAPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> GRAY_WALLPAPER_SLAB = ItemsPart04.GRAY_WALLPAPER_SLAB;
    public static final RegistryObject<Item> GRAY_WALLPAPER_VERTICAL_SLAB = ItemsPart04.GRAY_WALLPAPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_WALLPAPER_SLAB = ItemsPart04.LIGHT_GRAY_WALLPAPER_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_WALLPAPER_VERTICAL_SLAB =
            ItemsPart04.LIGHT_GRAY_WALLPAPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> CYAN_WALLPAPER_SLAB = ItemsPart04.CYAN_WALLPAPER_SLAB;
    public static final RegistryObject<Item> CYAN_WALLPAPER_VERTICAL_SLAB = ItemsPart04.CYAN_WALLPAPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> PURPLE_WALLPAPER_SLAB = ItemsPart04.PURPLE_WALLPAPER_SLAB;
    public static final RegistryObject<Item> PURPLE_WALLPAPER_VERTICAL_SLAB =
            ItemsPart04.PURPLE_WALLPAPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLUE_WALLPAPER_SLAB = ItemsPart04.BLUE_WALLPAPER_SLAB;
    public static final RegistryObject<Item> BLUE_WALLPAPER_VERTICAL_SLAB = ItemsPart04.BLUE_WALLPAPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> BROWN_WALLPAPER_SLAB = ItemsPart04.BROWN_WALLPAPER_SLAB;
    public static final RegistryObject<Item> BROWN_WALLPAPER_VERTICAL_SLAB = ItemsPart04.BROWN_WALLPAPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> GREEN_WALLPAPER_SLAB = ItemsPart04.GREEN_WALLPAPER_SLAB;
    public static final RegistryObject<Item> GREEN_WALLPAPER_VERTICAL_SLAB = ItemsPart04.GREEN_WALLPAPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> RED_WALLPAPER_SLAB = ItemsPart04.RED_WALLPAPER_SLAB;
    public static final RegistryObject<Item> RED_WALLPAPER_VERTICAL_SLAB = ItemsPart04.RED_WALLPAPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLACK_WALLPAPER_SLAB = ItemsPart04.BLACK_WALLPAPER_SLAB;
    public static final RegistryObject<Item> BLACK_WALLPAPER_VERTICAL_SLAB = ItemsPart04.BLACK_WALLPAPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> WHITE_WALLPAPER_FLAT = ItemsPart04.WHITE_WALLPAPER_FLAT;
    public static final RegistryObject<Item> ORANGE_WALLPAPER_FLAT = ItemsPart04.ORANGE_WALLPAPER_FLAT;
    public static final RegistryObject<Item> MAGENTA_WALLPAPER_FLAT = ItemsPart04.MAGENTA_WALLPAPER_FLAT;
    public static final RegistryObject<Item> LIGHT_BLUE_WALLPAPER_FLAT = ItemsPart04.LIGHT_BLUE_WALLPAPER_FLAT;
    public static final RegistryObject<Item> YELLOW_WALLPAPER_FLAT = ItemsPart04.YELLOW_WALLPAPER_FLAT;
    public static final RegistryObject<Item> LIME_WALLPAPER_FLAT = ItemsPart04.LIME_WALLPAPER_FLAT;
    public static final RegistryObject<Item> PINK_WALLPAPER_FLAT = ItemsPart04.PINK_WALLPAPER_FLAT;
    public static final RegistryObject<Item> GRAY_WALLPAPER_FLAT = ItemsPart04.GRAY_WALLPAPER_FLAT;
    public static final RegistryObject<Item> LIGHT_GRAY_WALLPAPER_FLAT = ItemsPart04.LIGHT_GRAY_WALLPAPER_FLAT;
    public static final RegistryObject<Item> CYAN_WALLPAPER_FLAT = ItemsPart04.CYAN_WALLPAPER_FLAT;
    public static final RegistryObject<Item> PURPLE_WALLPAPER_FLAT = ItemsPart04.PURPLE_WALLPAPER_FLAT;
    public static final RegistryObject<Item> BLUE_WALLPAPER_FLAT = ItemsPart04.BLUE_WALLPAPER_FLAT;
    public static final RegistryObject<Item> BROWN_WALLPAPER_FLAT = ItemsPart04.BROWN_WALLPAPER_FLAT;
    public static final RegistryObject<Item> GREEN_WALLPAPER_FLAT = ItemsPart04.GREEN_WALLPAPER_FLAT;
    public static final RegistryObject<Item> RED_WALLPAPER_FLAT = ItemsPart04.RED_WALLPAPER_FLAT;
    public static final RegistryObject<Item> BLACK_WALLPAPER_FLAT = ItemsPart04.BLACK_WALLPAPER_FLAT;
    public static final RegistryObject<Item> WHITE_CUSHION = ItemsPart04.WHITE_CUSHION;
    public static final RegistryObject<Item> ORANGE_CUSHION = ItemsPart04.ORANGE_CUSHION;
    public static final RegistryObject<Item> MAGENTA_CUSHION = ItemsPart04.MAGENTA_CUSHION;
    public static final RegistryObject<Item> LIGHT_BLUE_CUSHION = ItemsPart04.LIGHT_BLUE_CUSHION;
    public static final RegistryObject<Item> YELLOW_CUSHION = ItemsPart04.YELLOW_CUSHION;
    public static final RegistryObject<Item> LIME_CUSHION = ItemsPart04.LIME_CUSHION;
    public static final RegistryObject<Item> PINK_CUSHION = ItemsPart04.PINK_CUSHION;
    public static final RegistryObject<Item> GRAY_CUSHION = ItemsPart04.GRAY_CUSHION;
    public static final RegistryObject<Item> LIGHT_GRAY_CUSHION = ItemsPart04.LIGHT_GRAY_CUSHION;
    public static final RegistryObject<Item> CYAN_CUSHION = ItemsPart04.CYAN_CUSHION;
    public static final RegistryObject<Item> PURPLE_CUSHION = ItemsPart04.PURPLE_CUSHION;
    public static final RegistryObject<Item> BLUE_CUSHION = ItemsPart04.BLUE_CUSHION;
    public static final RegistryObject<Item> BROWN_CUSHION = ItemsPart04.BROWN_CUSHION;
    public static final RegistryObject<Item> GREEN_CUSHION = ItemsPart04.GREEN_CUSHION;
    public static final RegistryObject<Item> RED_CUSHION = ItemsPart04.RED_CUSHION;
    public static final RegistryObject<Item> BLACK_CUSHION = ItemsPart04.BLACK_CUSHION;
    public static final RegistryObject<Item> STRAW_BED = ItemsPart04.STRAW_BED;
    public static final RegistryObject<Item> BIG_BOOK = ItemsPart04.BIG_BOOK;
    public static final RegistryObject<Item> GLOWING_BIG_BOOK = ItemsPart04.GLOWING_BIG_BOOK;
    public static final RegistryObject<Item> WHITE_BIG_BOOK = ItemsPart04.WHITE_BIG_BOOK;
    public static final RegistryObject<Item> ORANGE_BIG_BOOK = ItemsPart04.ORANGE_BIG_BOOK;
    public static final RegistryObject<Item> MAGENTA_BIG_BOOK = ItemsPart04.MAGENTA_BIG_BOOK;
    public static final RegistryObject<Item> LIGHT_BLUE_BIG_BOOK = ItemsPart04.LIGHT_BLUE_BIG_BOOK;
    public static final RegistryObject<Item> YELLOW_BIG_BOOK = ItemsPart04.YELLOW_BIG_BOOK;
    public static final RegistryObject<Item> LIME_BIG_BOOK = ItemsPart04.LIME_BIG_BOOK;
    public static final RegistryObject<Item> PINK_BIG_BOOK = ItemsPart04.PINK_BIG_BOOK;
    public static final RegistryObject<Item> GRAY_BIG_BOOK = ItemsPart04.GRAY_BIG_BOOK;
    public static final RegistryObject<Item> LIGHT_GRAY_BIG_BOOK = ItemsPart04.LIGHT_GRAY_BIG_BOOK;
    public static final RegistryObject<Item> CYAN_BIG_BOOK = ItemsPart04.CYAN_BIG_BOOK;
    public static final RegistryObject<Item> PURPLE_BIG_BOOK = ItemsPart04.PURPLE_BIG_BOOK;
    public static final RegistryObject<Item> BLUE_BIG_BOOK = ItemsPart04.BLUE_BIG_BOOK;
    public static final RegistryObject<Item> BROWN_BIG_BOOK = ItemsPart04.BROWN_BIG_BOOK;
    public static final RegistryObject<Item> GREEN_BIG_BOOK = ItemsPart04.GREEN_BIG_BOOK;
    public static final RegistryObject<Item> RED_BIG_BOOK = ItemsPart04.RED_BIG_BOOK;
    public static final RegistryObject<Item> BLACK_BIG_BOOK = ItemsPart04.BLACK_BIG_BOOK;
    public static final RegistryObject<Item> PALE_OAK_LOG = ItemsPart04.PALE_OAK_LOG;
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_LOG = ItemsPart04.STRIPPED_PALE_OAK_LOG;
    public static final RegistryObject<Item> PALE_OAK_WOOD = ItemsPart04.PALE_OAK_WOOD;
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_WOOD = ItemsPart04.STRIPPED_PALE_OAK_WOOD;
    public static final RegistryObject<Item> PALE_OAK_PLANKS = ItemsPart04.PALE_OAK_PLANKS;
    public static final RegistryObject<Item> PALE_OAK_STAIRS = ItemsPart04.PALE_OAK_STAIRS;
    public static final RegistryObject<Item> PALE_OAK_SLAB = ItemsPart04.PALE_OAK_SLAB;
    public static final RegistryObject<Item> PALE_OAK_VERTICAL_SLAB = ItemsPart04.PALE_OAK_VERTICAL_SLAB;
    public static final RegistryObject<Item> PALE_OAK_FENCE = ItemsPart04.PALE_OAK_FENCE;
    public static final RegistryObject<Item> PALE_OAK_FENCE_GATE = ItemsPart04.PALE_OAK_FENCE_GATE;
    public static final RegistryObject<Item> PALE_OAK_DOOR = ItemsPart04.PALE_OAK_DOOR;
    public static final RegistryObject<Item> PALE_OAK_TRAPDOOR = ItemsPart04.PALE_OAK_TRAPDOOR;
    public static final RegistryObject<Item> PALE_OAK_BUTTON = ItemsPart04.PALE_OAK_BUTTON;
    public static final RegistryObject<Item> PALE_OAK_PRESSURE_PLATE = ItemsPart04.PALE_OAK_PRESSURE_PLATE;
    public static final RegistryObject<Item> PALE_OAK_LEAVES = ItemsPart04.PALE_OAK_LEAVES;
    public static final RegistryObject<Item> PALE_OAK_SAPLING = ItemsPart04.PALE_OAK_SAPLING;
    public static final RegistryObject<Item> PALE_OAK_LOG_SLAB = ItemsPart04.PALE_OAK_LOG_SLAB;
    public static final RegistryObject<Item> PALE_OAK_LOG_VERTICAL_SLAB = ItemsPart04.PALE_OAK_LOG_VERTICAL_SLAB;
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_LOG_SLAB = ItemsPart04.STRIPPED_PALE_OAK_LOG_SLAB;
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_LOG_VERTICAL_SLAB =
            ItemsPart04.STRIPPED_PALE_OAK_LOG_VERTICAL_SLAB;
    public static final RegistryObject<Item> PALE_OAK_WOOD_SLAB = ItemsPart04.PALE_OAK_WOOD_SLAB;
    public static final RegistryObject<Item> PALE_OAK_WOOD_VERTICAL_SLAB = ItemsPart04.PALE_OAK_WOOD_VERTICAL_SLAB;
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_WOOD_SLAB = ItemsPart04.STRIPPED_PALE_OAK_WOOD_SLAB;
    public static final RegistryObject<Item> STRIPPED_PALE_OAK_WOOD_VERTICAL_SLAB =
            ItemsPart04.STRIPPED_PALE_OAK_WOOD_VERTICAL_SLAB;
    public static final RegistryObject<Item> CHERRY_LOG = ItemsPart04.CHERRY_LOG;
    public static final RegistryObject<Item> STRIPPED_CHERRY_LOG = ItemsPart04.STRIPPED_CHERRY_LOG;
    public static final RegistryObject<Item> CHERRY_WOOD = ItemsPart04.CHERRY_WOOD;
    public static final RegistryObject<Item> STRIPPED_CHERRY_WOOD = ItemsPart04.STRIPPED_CHERRY_WOOD;
    public static final RegistryObject<Item> CHERRY_PLANKS = ItemsPart04.CHERRY_PLANKS;
    public static final RegistryObject<Item> CHERRY_STAIRS = ItemsPart04.CHERRY_STAIRS;
    public static final RegistryObject<Item> CHERRY_SLAB = ItemsPart04.CHERRY_SLAB;
    public static final RegistryObject<Item> CHERRY_VERTICAL_SLAB = ItemsPart04.CHERRY_VERTICAL_SLAB;
    public static final RegistryObject<Item> CHERRY_FENCE = ItemsPart04.CHERRY_FENCE;
    public static final RegistryObject<Item> CHERRY_FENCE_GATE = ItemsPart04.CHERRY_FENCE_GATE;
    public static final RegistryObject<Item> CHERRY_DOOR = ItemsPart04.CHERRY_DOOR;
    public static final RegistryObject<Item> CHERRY_TRAPDOOR = ItemsPart04.CHERRY_TRAPDOOR;
    public static final RegistryObject<Item> CHERRY_BUTTON = ItemsPart04.CHERRY_BUTTON;
    public static final RegistryObject<Item> CHERRY_PRESSURE_PLATE = ItemsPart04.CHERRY_PRESSURE_PLATE;
    public static final RegistryObject<Item> CHERRY_LEAVES = ItemsPart04.CHERRY_LEAVES;
    public static final RegistryObject<Item> CHERRY_SAPLING = ItemsPart04.CHERRY_SAPLING;
    public static final RegistryObject<Item> CHERRY_LOG_SLAB = ItemsPart04.CHERRY_LOG_SLAB;
    public static final RegistryObject<Item> CHERRY_LOG_VERTICAL_SLAB = ItemsPart04.CHERRY_LOG_VERTICAL_SLAB;
    public static final RegistryObject<Item> STRIPPED_CHERRY_LOG_SLAB = ItemsPart04.STRIPPED_CHERRY_LOG_SLAB;
    public static final RegistryObject<Item> STRIPPED_CHERRY_LOG_VERTICAL_SLAB =
            ItemsPart04.STRIPPED_CHERRY_LOG_VERTICAL_SLAB;
    public static final RegistryObject<Item> CHERRY_WOOD_SLAB = ItemsPart04.CHERRY_WOOD_SLAB;
    public static final RegistryObject<Item> CHERRY_WOOD_VERTICAL_SLAB = ItemsPart04.CHERRY_WOOD_VERTICAL_SLAB;
    public static final RegistryObject<Item> STRIPPED_CHERRY_WOOD_SLAB = ItemsPart04.STRIPPED_CHERRY_WOOD_SLAB;
    public static final RegistryObject<Item> STRIPPED_CHERRY_WOOD_VERTICAL_SLAB =
            ItemsPart04.STRIPPED_CHERRY_WOOD_VERTICAL_SLAB;
    public static final RegistryObject<Item> PALE_MOSS_BLOCK = ItemsPart04.PALE_MOSS_BLOCK;
    public static final RegistryObject<Item> PALE_MOSS_BLOCK_SLAB = ItemsPart04.PALE_MOSS_BLOCK_SLAB;
    public static final RegistryObject<Item> PALE_MOSS_BLOCK_STAIRS = ItemsPart04.PALE_MOSS_BLOCK_STAIRS;
    public static final RegistryObject<Item> PALE_MOSS_BLOCK_VERTICAL_SLAB = ItemsPart04.PALE_MOSS_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> PALE_MOSS_CARPET = ItemsPart04.PALE_MOSS_CARPET;
    public static final RegistryObject<Item> PALE_MOSS_LAYERS = ItemsPart04.PALE_MOSS_LAYERS;
    public static final RegistryObject<Item> PALE_MOSS_OVERLAY = ItemsPart04.PALE_MOSS_OVERLAY;
    public static final RegistryObject<Item> PALE_HANGING_MOSS = ItemsPart04.PALE_HANGING_MOSS;
    public static final RegistryObject<Item> CREAKING_HEART = ItemsPart04.CREAKING_HEART;
    public static final RegistryObject<Item> RESIN_CLUMP = ItemsPart04.RESIN_CLUMP;
    public static final RegistryObject<Item> RESIN_BLOCK = ItemsPart04.RESIN_BLOCK;
    public static final RegistryObject<Item> RESIN_BLOCK_SLAB = ItemsPart04.RESIN_BLOCK_SLAB;
    public static final RegistryObject<Item> RESIN_BLOCK_STAIRS = ItemsPart04.RESIN_BLOCK_STAIRS;
    public static final RegistryObject<Item> RESIN_BLOCK_VERTICAL_SLAB = ItemsPart04.RESIN_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> RESIN_BRICKS = ItemsPart04.RESIN_BRICKS;
    public static final RegistryObject<Item> RESIN_BRICK_SLAB = ItemsPart04.RESIN_BRICK_SLAB;
    public static final RegistryObject<Item> RESIN_BRICK_STAIRS = ItemsPart04.RESIN_BRICK_STAIRS;
    public static final RegistryObject<Item> RESIN_BRICK_WALL = ItemsPart04.RESIN_BRICK_WALL;
    public static final RegistryObject<Item> RESIN_BRICK_VERTICAL_SLAB = ItemsPart04.RESIN_BRICK_VERTICAL_SLAB;
    public static final RegistryObject<Item> CHISELED_RESIN_BRICKS = ItemsPart04.CHISELED_RESIN_BRICKS;
    public static final RegistryObject<Item> CLOSED_EYEBLOSSOM = ItemsPart04.CLOSED_EYEBLOSSOM;
    public static final RegistryObject<Item> OPEN_EYEBLOSSOM = ItemsPart04.OPEN_EYEBLOSSOM;
    public static final RegistryObject<Item> WILDFLOWERS = ItemsPart04.WILDFLOWERS;
    public static final RegistryObject<Item> LEAF_LITTER = ItemsPart04.LEAF_LITTER;
    public static final RegistryObject<Item> CACTUS_FLOWER = ItemsPart04.CACTUS_FLOWER;
    public static final RegistryObject<Item> BUSH = ItemsPart04.BUSH;
    public static final RegistryObject<Item> RED_BUSH = ItemsPart04.RED_BUSH;
    public static final RegistryObject<Item> WHITE_HAZE_BUSH = ItemsPart04.WHITE_HAZE_BUSH;
    public static final RegistryObject<Item> ORANGE_HAZE_BUSH = ItemsPart04.ORANGE_HAZE_BUSH;
    public static final RegistryObject<Item> MAGENTA_HAZE_BUSH = ItemsPart04.MAGENTA_HAZE_BUSH;
    public static final RegistryObject<Item> LIGHT_BLUE_HAZE_BUSH = ItemsPart04.LIGHT_BLUE_HAZE_BUSH;
    public static final RegistryObject<Item> YELLOW_HAZE_BUSH = ItemsPart04.YELLOW_HAZE_BUSH;
    public static final RegistryObject<Item> LIME_HAZE_BUSH = ItemsPart04.LIME_HAZE_BUSH;
    public static final RegistryObject<Item> PINK_HAZE_BUSH = ItemsPart04.PINK_HAZE_BUSH;
    public static final RegistryObject<Item> GRAY_HAZE_BUSH = ItemsPart04.GRAY_HAZE_BUSH;
    public static final RegistryObject<Item> LIGHT_GRAY_HAZE_BUSH = ItemsPart04.LIGHT_GRAY_HAZE_BUSH;
    public static final RegistryObject<Item> CYAN_HAZE_BUSH = ItemsPart04.CYAN_HAZE_BUSH;
    public static final RegistryObject<Item> PURPLE_HAZE_BUSH = ItemsPart04.PURPLE_HAZE_BUSH;
    public static final RegistryObject<Item> BLUE_HAZE_BUSH = ItemsPart04.BLUE_HAZE_BUSH;
    public static final RegistryObject<Item> BROWN_HAZE_BUSH = ItemsPart04.BROWN_HAZE_BUSH;
    public static final RegistryObject<Item> GREEN_HAZE_BUSH = ItemsPart04.GREEN_HAZE_BUSH;
    public static final RegistryObject<Item> RED_HAZE_BUSH = ItemsPart04.RED_HAZE_BUSH;
    public static final RegistryObject<Item> BLACK_HAZE_BUSH = ItemsPart04.BLACK_HAZE_BUSH;
    public static final RegistryObject<Item> FIREFLY_BUSH = ItemsPart04.FIREFLY_BUSH;
    public static final RegistryObject<Item> DRY_GRASS = ItemsPart04.DRY_GRASS;
    public static final RegistryObject<Item> TALL_DRY_GRASS = ItemsPart04.TALL_DRY_GRASS;
    public static final RegistryObject<Item> GOLDEN_DANDELION = ItemsPart04.GOLDEN_DANDELION;
    public static final RegistryObject<Item> OCHRE_FROGLIGHT = ItemsPart04.OCHRE_FROGLIGHT;
    public static final RegistryObject<Item> PEARLESCENT_FROGLIGHT = ItemsPart04.PEARLESCENT_FROGLIGHT;
    public static final RegistryObject<Item> VERDANT_FROGLIGHT = ItemsPart04.VERDANT_FROGLIGHT;
    public static final RegistryObject<Item> GLEAMING_FROGLIGHT = ItemsPart04.GLEAMING_FROGLIGHT;
    public static final RegistryObject<Item> IVORY_FROGLIGHT = ItemsPart04.IVORY_FROGLIGHT;
    public static final RegistryObject<Item> ASHEN_FROGLIGHT = ItemsPart04.ASHEN_FROGLIGHT;
    public static final RegistryObject<Item> ONYX_FROGLIGHT = ItemsPart04.ONYX_FROGLIGHT;
    public static final RegistryObject<Item> DUSKY_FROGLIGHT = ItemsPart04.DUSKY_FROGLIGHT;
    public static final RegistryObject<Item> SCARLET_FROGLIGHT = ItemsPart04.SCARLET_FROGLIGHT;
    public static final RegistryObject<Item> RUSSET_FROGLIGHT = ItemsPart04.RUSSET_FROGLIGHT;
    public static final RegistryObject<Item> VERIDIAN_FROGLIGHT = ItemsPart04.VERIDIAN_FROGLIGHT;
    public static final RegistryObject<Item> TIDAL_FROGLIGHT = ItemsPart04.TIDAL_FROGLIGHT;
    public static final RegistryObject<Item> CERULEAN_FROGLIGHT = ItemsPart04.CERULEAN_FROGLIGHT;
    public static final RegistryObject<Item> AZURE_FROGLIGHT = ItemsPart04.AZURE_FROGLIGHT;
    public static final RegistryObject<Item> IRIDESCENT_FROGLIGHT = ItemsPart04.IRIDESCENT_FROGLIGHT;
    public static final RegistryObject<Item> CORALINE_FROGLIGHT = ItemsPart04.CORALINE_FROGLIGHT;
    public static final RegistryObject<Item> SCULK = ItemsPart04.SCULK;
    public static final RegistryObject<Item> SCULK_SLAB = ItemsPart04.SCULK_SLAB;
    public static final RegistryObject<Item> SCULK_STAIRS = ItemsPart04.SCULK_STAIRS;
    public static final RegistryObject<Item> SCULK_WALL = ItemsPart04.SCULK_WALL;
    public static final RegistryObject<Item> SCULK_VERTICAL_SLAB = ItemsPart04.SCULK_VERTICAL_SLAB;
    public static final RegistryObject<Item> SCULK_VEIN = ItemsPart04.SCULK_VEIN;
    public static final RegistryObject<Item> SCULK_CATALYST = ItemsPart04.SCULK_CATALYST;
    public static final RegistryObject<Item> SCULK_SHRIEKER = ItemsPart04.SCULK_SHRIEKER;
    public static final RegistryObject<Item> SCULK_SENSOR = ItemsPart04.SCULK_SENSOR;
    public static final RegistryObject<Item> COPPER_TORCH = ItemsPart04.COPPER_TORCH;
    public static final RegistryObject<Item> COPPER_ROD = ItemsPart04.COPPER_ROD;
    public static final RegistryObject<Item> WAXED_COPPER_ROD = ItemsPart04.WAXED_COPPER_ROD;
    public static final RegistryObject<Item> EXPOSED_COPPER_ROD = ItemsPart04.EXPOSED_COPPER_ROD;
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_ROD = ItemsPart04.WAXED_EXPOSED_COPPER_ROD;
    public static final RegistryObject<Item> WEATHERED_COPPER_ROD = ItemsPart04.WEATHERED_COPPER_ROD;
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_ROD = ItemsPart04.WAXED_WEATHERED_COPPER_ROD;
    public static final RegistryObject<Item> OXIDIZED_COPPER_ROD = ItemsPart04.OXIDIZED_COPPER_ROD;
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_ROD = ItemsPart04.WAXED_OXIDIZED_COPPER_ROD;
    public static final RegistryObject<Item> COPPER_LANTERN = ItemsPart04.COPPER_LANTERN;
    public static final RegistryObject<Item> WAXED_COPPER_LANTERN = ItemsPart04.WAXED_COPPER_LANTERN;
    public static final RegistryObject<Item> EXPOSED_COPPER_LANTERN = ItemsPart04.EXPOSED_COPPER_LANTERN;
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_LANTERN = ItemsPart04.WAXED_EXPOSED_COPPER_LANTERN;
    public static final RegistryObject<Item> WEATHERED_COPPER_LANTERN = ItemsPart04.WEATHERED_COPPER_LANTERN;
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_LANTERN =
            ItemsPart04.WAXED_WEATHERED_COPPER_LANTERN;
    public static final RegistryObject<Item> OXIDIZED_COPPER_LANTERN = ItemsPart04.OXIDIZED_COPPER_LANTERN;
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_LANTERN = ItemsPart04.WAXED_OXIDIZED_COPPER_LANTERN;
    public static final RegistryObject<Item> SLIT_COPPER = ItemsPart04.SLIT_COPPER;
    public static final RegistryObject<Item> WAXED_SLIT_COPPER = ItemsPart04.WAXED_SLIT_COPPER;
    public static final RegistryObject<Item> EXPOSED_SLIT_COPPER = ItemsPart04.EXPOSED_SLIT_COPPER;
    public static final RegistryObject<Item> WAXED_EXPOSED_SLIT_COPPER = ItemsPart04.WAXED_EXPOSED_SLIT_COPPER;
    public static final RegistryObject<Item> WEATHERED_SLIT_COPPER = ItemsPart04.WEATHERED_SLIT_COPPER;
    public static final RegistryObject<Item> WAXED_WEATHERED_SLIT_COPPER = ItemsPart04.WAXED_WEATHERED_SLIT_COPPER;
    public static final RegistryObject<Item> OXIDIZED_SLIT_COPPER = ItemsPart04.OXIDIZED_SLIT_COPPER;
    public static final RegistryObject<Item> WAXED_OXIDIZED_SLIT_COPPER = ItemsPart04.WAXED_OXIDIZED_SLIT_COPPER;
    public static final RegistryObject<Item> SLIT_COPPER_STAIRS = ItemsPart04.SLIT_COPPER_STAIRS;
    public static final RegistryObject<Item> WAXED_SLIT_COPPER_STAIRS = ItemsPart04.WAXED_SLIT_COPPER_STAIRS;
    public static final RegistryObject<Item> EXPOSED_SLIT_COPPER_STAIRS = ItemsPart04.EXPOSED_SLIT_COPPER_STAIRS;
    public static final RegistryObject<Item> WAXED_EXPOSED_SLIT_COPPER_STAIRS =
            ItemsPart04.WAXED_EXPOSED_SLIT_COPPER_STAIRS;
    public static final RegistryObject<Item> WEATHERED_SLIT_COPPER_STAIRS = ItemsPart04.WEATHERED_SLIT_COPPER_STAIRS;
    public static final RegistryObject<Item> WAXED_WEATHERED_SLIT_COPPER_STAIRS =
            ItemsPart04.WAXED_WEATHERED_SLIT_COPPER_STAIRS;
    public static final RegistryObject<Item> OXIDIZED_SLIT_COPPER_STAIRS = ItemsPart04.OXIDIZED_SLIT_COPPER_STAIRS;
    public static final RegistryObject<Item> WAXED_OXIDIZED_SLIT_COPPER_STAIRS =
            ItemsPart04.WAXED_OXIDIZED_SLIT_COPPER_STAIRS;
    public static final RegistryObject<Item> SLIT_COPPER_SLAB = ItemsPart04.SLIT_COPPER_SLAB;
    public static final RegistryObject<Item> WAXED_SLIT_COPPER_SLAB = ItemsPart04.WAXED_SLIT_COPPER_SLAB;
    public static final RegistryObject<Item> EXPOSED_SLIT_COPPER_SLAB = ItemsPart04.EXPOSED_SLIT_COPPER_SLAB;
    public static final RegistryObject<Item> WAXED_EXPOSED_SLIT_COPPER_SLAB =
            ItemsPart04.WAXED_EXPOSED_SLIT_COPPER_SLAB;
    public static final RegistryObject<Item> WEATHERED_SLIT_COPPER_SLAB = ItemsPart04.WEATHERED_SLIT_COPPER_SLAB;
    public static final RegistryObject<Item> WAXED_WEATHERED_SLIT_COPPER_SLAB =
            ItemsPart04.WAXED_WEATHERED_SLIT_COPPER_SLAB;
    public static final RegistryObject<Item> OXIDIZED_SLIT_COPPER_SLAB = ItemsPart04.OXIDIZED_SLIT_COPPER_SLAB;
    public static final RegistryObject<Item> WAXED_OXIDIZED_SLIT_COPPER_SLAB =
            ItemsPart04.WAXED_OXIDIZED_SLIT_COPPER_SLAB;
    public static final RegistryObject<Item> SLIT_COPPER_VERTICAL_SLAB = ItemsPart04.SLIT_COPPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> WAXED_SLIT_COPPER_VERTICAL_SLAB =
            ItemsPart04.WAXED_SLIT_COPPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> EXPOSED_SLIT_COPPER_VERTICAL_SLAB =
            ItemsPart04.EXPOSED_SLIT_COPPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> WAXED_EXPOSED_SLIT_COPPER_VERTICAL_SLAB =
            ItemsPart04.WAXED_EXPOSED_SLIT_COPPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> WEATHERED_SLIT_COPPER_VERTICAL_SLAB =
            ItemsPart04.WEATHERED_SLIT_COPPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> WAXED_WEATHERED_SLIT_COPPER_VERTICAL_SLAB =
            ItemsPart04.WAXED_WEATHERED_SLIT_COPPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> OXIDIZED_SLIT_COPPER_VERTICAL_SLAB =
            ItemsPart04.OXIDIZED_SLIT_COPPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> WAXED_OXIDIZED_SLIT_COPPER_VERTICAL_SLAB =
            ItemsPart04.WAXED_OXIDIZED_SLIT_COPPER_VERTICAL_SLAB;
    public static final RegistryObject<Item> CHISELED_COPPER = ItemsPart04.CHISELED_COPPER;
    public static final RegistryObject<Item> WAXED_CHISELED_COPPER = ItemsPart04.WAXED_CHISELED_COPPER;
    public static final RegistryObject<Item> EXPOSED_CHISELED_COPPER = ItemsPart04.EXPOSED_CHISELED_COPPER;
    public static final RegistryObject<Item> WAXED_EXPOSED_CHISELED_COPPER = ItemsPart04.WAXED_EXPOSED_CHISELED_COPPER;
    public static final RegistryObject<Item> WEATHERED_CHISELED_COPPER = ItemsPart04.WEATHERED_CHISELED_COPPER;
    public static final RegistryObject<Item> WAXED_WEATHERED_CHISELED_COPPER =
            ItemsPart04.WAXED_WEATHERED_CHISELED_COPPER;
    public static final RegistryObject<Item> OXIDIZED_CHISELED_COPPER = ItemsPart04.OXIDIZED_CHISELED_COPPER;
    public static final RegistryObject<Item> WAXED_OXIDIZED_CHISELED_COPPER =
            ItemsPart04.WAXED_OXIDIZED_CHISELED_COPPER;
    public static final RegistryObject<Item> COPPER_GRATE = ItemsPart04.COPPER_GRATE;
    public static final RegistryObject<Item> WAXED_COPPER_GRATE = ItemsPart04.WAXED_COPPER_GRATE;
    public static final RegistryObject<Item> EXPOSED_COPPER_GRATE = ItemsPart04.EXPOSED_COPPER_GRATE;
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_GRATE = ItemsPart04.WAXED_EXPOSED_COPPER_GRATE;
    public static final RegistryObject<Item> WEATHERED_COPPER_GRATE = ItemsPart04.WEATHERED_COPPER_GRATE;
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_GRATE = ItemsPart04.WAXED_WEATHERED_COPPER_GRATE;
    public static final RegistryObject<Item> OXIDIZED_COPPER_GRATE = ItemsPart04.OXIDIZED_COPPER_GRATE;
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_GRATE = ItemsPart04.WAXED_OXIDIZED_COPPER_GRATE;
    public static final RegistryObject<Item> COPPER_BULB = ItemsPart04.COPPER_BULB;
    public static final RegistryObject<Item> WAXED_COPPER_BULB = ItemsPart04.WAXED_COPPER_BULB;
    public static final RegistryObject<Item> EXPOSED_COPPER_BULB = ItemsPart04.EXPOSED_COPPER_BULB;
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_BULB = ItemsPart04.WAXED_EXPOSED_COPPER_BULB;
    public static final RegistryObject<Item> WEATHERED_COPPER_BULB = ItemsPart04.WEATHERED_COPPER_BULB;
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_BULB = ItemsPart04.WAXED_WEATHERED_COPPER_BULB;
    public static final RegistryObject<Item> OXIDIZED_COPPER_BULB = ItemsPart04.OXIDIZED_COPPER_BULB;
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_BULB = ItemsPart04.WAXED_OXIDIZED_COPPER_BULB;
    public static final RegistryObject<Item> COPPER_DOOR = ItemsPart04.COPPER_DOOR;
    public static final RegistryObject<Item> WAXED_COPPER_DOOR = ItemsPart04.WAXED_COPPER_DOOR;
    public static final RegistryObject<Item> EXPOSED_COPPER_DOOR = ItemsPart04.EXPOSED_COPPER_DOOR;
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_DOOR = ItemsPart04.WAXED_EXPOSED_COPPER_DOOR;
    public static final RegistryObject<Item> WEATHERED_COPPER_DOOR = ItemsPart04.WEATHERED_COPPER_DOOR;
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_DOOR = ItemsPart04.WAXED_WEATHERED_COPPER_DOOR;
    public static final RegistryObject<Item> OXIDIZED_COPPER_DOOR = ItemsPart04.OXIDIZED_COPPER_DOOR;
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_DOOR = ItemsPart04.WAXED_OXIDIZED_COPPER_DOOR;
    public static final RegistryObject<Item> COPPER_TRAPDOOR = ItemsPart04.COPPER_TRAPDOOR;
    public static final RegistryObject<Item> WAXED_COPPER_TRAPDOOR = ItemsPart04.WAXED_COPPER_TRAPDOOR;
    public static final RegistryObject<Item> EXPOSED_COPPER_TRAPDOOR = ItemsPart04.EXPOSED_COPPER_TRAPDOOR;
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_TRAPDOOR = ItemsPart04.WAXED_EXPOSED_COPPER_TRAPDOOR;
    public static final RegistryObject<Item> WEATHERED_COPPER_TRAPDOOR = ItemsPart04.WEATHERED_COPPER_TRAPDOOR;
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_TRAPDOOR =
            ItemsPart04.WAXED_WEATHERED_COPPER_TRAPDOOR;
    public static final RegistryObject<Item> OXIDIZED_COPPER_TRAPDOOR = ItemsPart04.OXIDIZED_COPPER_TRAPDOOR;
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_TRAPDOOR =
            ItemsPart04.WAXED_OXIDIZED_COPPER_TRAPDOOR;
    public static final RegistryObject<Item> COPPER_BARS = ItemsPart04.COPPER_BARS;
    public static final RegistryObject<Item> WAXED_COPPER_BARS = ItemsPart04.WAXED_COPPER_BARS;
    public static final RegistryObject<Item> EXPOSED_COPPER_BARS = ItemsPart04.EXPOSED_COPPER_BARS;
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_BARS = ItemsPart04.WAXED_EXPOSED_COPPER_BARS;
    public static final RegistryObject<Item> WEATHERED_COPPER_BARS = ItemsPart04.WEATHERED_COPPER_BARS;
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_BARS = ItemsPart04.WAXED_WEATHERED_COPPER_BARS;
    public static final RegistryObject<Item> OXIDIZED_COPPER_BARS = ItemsPart04.OXIDIZED_COPPER_BARS;
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_BARS = ItemsPart04.WAXED_OXIDIZED_COPPER_BARS;
    public static final RegistryObject<Item> GOLD_BARS = ItemsPart04.GOLD_BARS;
    public static final RegistryObject<Item> STEEL_BARS = ItemsPart04.STEEL_BARS;
    public static final RegistryObject<Item> IRON_MESH = ItemsPart04.IRON_MESH;
    public static final RegistryObject<Item> GOLD_MESH = ItemsPart04.GOLD_MESH;
    public static final RegistryObject<Item> STEEL_MESH = ItemsPart04.STEEL_MESH;
    public static final RegistryObject<Item> COPPER_MESH = ItemsPart04.COPPER_MESH;
    public static final RegistryObject<Item> WAXED_COPPER_MESH = ItemsPart04.WAXED_COPPER_MESH;
    public static final RegistryObject<Item> EXPOSED_COPPER_MESH = ItemsPart04.EXPOSED_COPPER_MESH;
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_MESH = ItemsPart04.WAXED_EXPOSED_COPPER_MESH;
    public static final RegistryObject<Item> WEATHERED_COPPER_MESH = ItemsPart04.WEATHERED_COPPER_MESH;
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_MESH = ItemsPart04.WAXED_WEATHERED_COPPER_MESH;
    public static final RegistryObject<Item> OXIDIZED_COPPER_MESH = ItemsPart04.OXIDIZED_COPPER_MESH;
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_MESH = ItemsPart04.WAXED_OXIDIZED_COPPER_MESH;
    public static final RegistryObject<Item> COPPER_BUTTON = ItemsPart04.COPPER_BUTTON;
    public static final RegistryObject<Item> WAXED_COPPER_BUTTON = ItemsPart04.WAXED_COPPER_BUTTON;
    public static final RegistryObject<Item> EXPOSED_COPPER_BUTTON = ItemsPart04.EXPOSED_COPPER_BUTTON;
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_BUTTON = ItemsPart04.WAXED_EXPOSED_COPPER_BUTTON;
    public static final RegistryObject<Item> WEATHERED_COPPER_BUTTON = ItemsPart04.WEATHERED_COPPER_BUTTON;
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_BUTTON = ItemsPart04.WAXED_WEATHERED_COPPER_BUTTON;
    public static final RegistryObject<Item> OXIDIZED_COPPER_BUTTON = ItemsPart04.OXIDIZED_COPPER_BUTTON;
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_BUTTON = ItemsPart04.WAXED_OXIDIZED_COPPER_BUTTON;
    public static final RegistryObject<Item> COPPER_BOLTS = ItemsPart04.COPPER_BOLTS;
    public static final RegistryObject<Item> WAXED_COPPER_BOLTS = ItemsPart04.WAXED_COPPER_BOLTS;
    public static final RegistryObject<Item> EXPOSED_COPPER_BOLTS = ItemsPart04.EXPOSED_COPPER_BOLTS;
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_BOLTS = ItemsPart04.WAXED_EXPOSED_COPPER_BOLTS;
    public static final RegistryObject<Item> WEATHERED_COPPER_BOLTS = ItemsPart04.WEATHERED_COPPER_BOLTS;
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_BOLTS = ItemsPart04.WAXED_WEATHERED_COPPER_BOLTS;
    public static final RegistryObject<Item> OXIDIZED_COPPER_BOLTS = ItemsPart04.OXIDIZED_COPPER_BOLTS;
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_BOLTS = ItemsPart04.WAXED_OXIDIZED_COPPER_BOLTS;
    public static final RegistryObject<Item> COPPER_PRESSURE_PLATE = ItemsPart04.COPPER_PRESSURE_PLATE;
    public static final RegistryObject<Item> WAXED_COPPER_PRESSURE_PLATE = ItemsPart04.WAXED_COPPER_PRESSURE_PLATE;
    public static final RegistryObject<Item> EXPOSED_COPPER_PRESSURE_PLATE = ItemsPart04.EXPOSED_COPPER_PRESSURE_PLATE;
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_PRESSURE_PLATE =
            ItemsPart04.WAXED_EXPOSED_COPPER_PRESSURE_PLATE;
    public static final RegistryObject<Item> WEATHERED_COPPER_PRESSURE_PLATE =
            ItemsPart04.WEATHERED_COPPER_PRESSURE_PLATE;
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_PRESSURE_PLATE =
            ItemsPart04.WAXED_WEATHERED_COPPER_PRESSURE_PLATE;
    public static final RegistryObject<Item> OXIDIZED_COPPER_PRESSURE_PLATE =
            ItemsPart04.OXIDIZED_COPPER_PRESSURE_PLATE;
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_PRESSURE_PLATE =
            ItemsPart04.WAXED_OXIDIZED_COPPER_PRESSURE_PLATE;
    public static final RegistryObject<Item> WANDERING_HOMEMAKER_SPAWN_EGG = ItemsPart04.WANDERING_HOMEMAKER_SPAWN_EGG;
    public static final RegistryObject<Item> FESTIVE_WANDERING_HOMEMAKER_SPAWN_EGG =
            ItemsPart04.FESTIVE_WANDERING_HOMEMAKER_SPAWN_EGG;
    public static final RegistryObject<Item> GLASS_JAR = ItemsPart04.GLASS_JAR;
    public static final RegistryObject<Item> WHITE_GLASS_JAR = ItemsPart04.WHITE_GLASS_JAR;
    public static final RegistryObject<Item> ORANGE_GLASS_JAR = ItemsPart04.ORANGE_GLASS_JAR;
    public static final RegistryObject<Item> MAGENTA_GLASS_JAR = ItemsPart04.MAGENTA_GLASS_JAR;
    public static final RegistryObject<Item> LIGHT_BLUE_GLASS_JAR = ItemsPart04.LIGHT_BLUE_GLASS_JAR;
    public static final RegistryObject<Item> YELLOW_GLASS_JAR = ItemsPart04.YELLOW_GLASS_JAR;
    public static final RegistryObject<Item> LIME_GLASS_JAR = ItemsPart04.LIME_GLASS_JAR;
    public static final RegistryObject<Item> PINK_GLASS_JAR = ItemsPart04.PINK_GLASS_JAR;
    public static final RegistryObject<Item> GRAY_GLASS_JAR = ItemsPart04.GRAY_GLASS_JAR;
    public static final RegistryObject<Item> LIGHT_GRAY_GLASS_JAR = ItemsPart04.LIGHT_GRAY_GLASS_JAR;
    public static final RegistryObject<Item> CYAN_GLASS_JAR = ItemsPart04.CYAN_GLASS_JAR;
    public static final RegistryObject<Item> PURPLE_GLASS_JAR = ItemsPart04.PURPLE_GLASS_JAR;
    public static final RegistryObject<Item> BLUE_GLASS_JAR = ItemsPart04.BLUE_GLASS_JAR;
    public static final RegistryObject<Item> BROWN_GLASS_JAR = ItemsPart04.BROWN_GLASS_JAR;
    public static final RegistryObject<Item> GREEN_GLASS_JAR = ItemsPart04.GREEN_GLASS_JAR;
    public static final RegistryObject<Item> RED_GLASS_JAR = ItemsPart04.RED_GLASS_JAR;
    public static final RegistryObject<Item> BLACK_GLASS_JAR = ItemsPart04.BLACK_GLASS_JAR;
    public static final RegistryObject<Item> TINTED_GLASS_JAR = ItemsPart04.TINTED_GLASS_JAR;
    public static final RegistryObject<Item> ACACIA_LADDER = ItemsPart04.ACACIA_LADDER;
    public static final RegistryObject<Item> ASHPEN_BLACK_LADDER = ItemsPart04.ASHPEN_BLACK_LADDER;
    public static final RegistryObject<Item> ASHPEN_BLUE_LADDER = ItemsPart04.ASHPEN_BLUE_LADDER;
    public static final RegistryObject<Item> ASHPEN_BROWN_LADDER = ItemsPart04.ASHPEN_BROWN_LADDER;
    public static final RegistryObject<Item> ASHPEN_CYAN_LADDER = ItemsPart04.ASHPEN_CYAN_LADDER;
    public static final RegistryObject<Item> ASHPEN_GRAY_LADDER = ItemsPart04.ASHPEN_GRAY_LADDER;
    public static final RegistryObject<Item> ASHPEN_GREEN_LADDER = ItemsPart04.ASHPEN_GREEN_LADDER;
    public static final RegistryObject<Item> ASHPEN_LIGHT_BLUE_LADDER = ItemsPart04.ASHPEN_LIGHT_BLUE_LADDER;
    public static final RegistryObject<Item> ASHPEN_LIGHT_GRAY_LADDER = ItemsPart04.ASHPEN_LIGHT_GRAY_LADDER;
    public static final RegistryObject<Item> ASHPEN_LIME_LADDER = ItemsPart04.ASHPEN_LIME_LADDER;
    public static final RegistryObject<Item> ASHPEN_MAGENTA_LADDER = ItemsPart04.ASHPEN_MAGENTA_LADDER;
    public static final RegistryObject<Item> ASHPEN_ORANGE_LADDER = ItemsPart04.ASHPEN_ORANGE_LADDER;
    public static final RegistryObject<Item> ASHPEN_PINK_LADDER = ItemsPart04.ASHPEN_PINK_LADDER;
    public static final RegistryObject<Item> ASHPEN_PURPLE_LADDER = ItemsPart04.ASHPEN_PURPLE_LADDER;
    public static final RegistryObject<Item> ASHPEN_RED_LADDER = ItemsPart04.ASHPEN_RED_LADDER;
    public static final RegistryObject<Item> ASHPEN_WHITE_LADDER = ItemsPart04.ASHPEN_WHITE_LADDER;
    public static final RegistryObject<Item> ASHPEN_YELLOW_LADDER = ItemsPart04.ASHPEN_YELLOW_LADDER;
    public static final RegistryObject<Item> BAMBOO_LADDER = ItemsPart04.BAMBOO_LADDER;
    public static final RegistryObject<Item> BIRCH_LADDER = ItemsPart04.BIRCH_LADDER;
    public static final RegistryObject<Item> CHERRY_LADDER = ItemsPart04.CHERRY_LADDER;
    public static final RegistryObject<Item> CRIMSON_LADDER = ItemsPart04.CRIMSON_LADDER;
    public static final RegistryObject<Item> DARK_OAK_LADDER = ItemsPart04.DARK_OAK_LADDER;
    public static final RegistryObject<Item> JUNGLE_LADDER = ItemsPart04.JUNGLE_LADDER;
    public static final RegistryObject<Item> MANGROVE_LADDER = ItemsPart04.MANGROVE_LADDER;
    public static final RegistryObject<Item> OAK_LADDER = ItemsPart04.OAK_LADDER;
    public static final RegistryObject<Item> PALE_OAK_LADDER = ItemsPart04.PALE_OAK_LADDER;
    public static final RegistryObject<Item> POPLAR_LADDER = ItemsPart04.POPLAR_LADDER;
    public static final RegistryObject<Item> SPRUCE_LADDER = ItemsPart04.SPRUCE_LADDER;
    public static final RegistryObject<Item> STRIPPED_BAMBOO_LADDER = ItemsPart04.STRIPPED_BAMBOO_LADDER;
    public static final RegistryObject<Item> WARPED_LADDER = ItemsPart04.WARPED_LADDER;
    public static final RegistryObject<Item> COPPER_CHEST = ItemsPart04.COPPER_CHEST;
    public static final RegistryObject<Item> EXPOSED_COPPER_CHEST = ItemsPart04.EXPOSED_COPPER_CHEST;
    public static final RegistryObject<Item> WEATHERED_COPPER_CHEST = ItemsPart04.WEATHERED_COPPER_CHEST;
    public static final RegistryObject<Item> OXIDIZED_COPPER_CHEST = ItemsPart04.OXIDIZED_COPPER_CHEST;
    public static final RegistryObject<Item> WAXED_COPPER_CHEST = ItemsPart04.WAXED_COPPER_CHEST;
    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_CHEST = ItemsPart04.WAXED_EXPOSED_COPPER_CHEST;
    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_CHEST = ItemsPart04.WAXED_WEATHERED_COPPER_CHEST;
    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_CHEST = ItemsPart04.WAXED_OXIDIZED_COPPER_CHEST;
    public static final RegistryObject<Item> WHITE_LAYERED_WOOL = ItemsPart04.WHITE_LAYERED_WOOL;
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL = ItemsPart04.ORANGE_LAYERED_WOOL;
    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL = ItemsPart04.MAGENTA_LAYERED_WOOL;
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL = ItemsPart04.LIGHT_BLUE_LAYERED_WOOL;
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL = ItemsPart04.YELLOW_LAYERED_WOOL;
    public static final RegistryObject<Item> LIME_LAYERED_WOOL = ItemsPart04.LIME_LAYERED_WOOL;
    public static final RegistryObject<Item> PINK_LAYERED_WOOL = ItemsPart04.PINK_LAYERED_WOOL;
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL = ItemsPart04.GRAY_LAYERED_WOOL;
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL = ItemsPart04.LIGHT_GRAY_LAYERED_WOOL;
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL = ItemsPart04.CYAN_LAYERED_WOOL;
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL = ItemsPart04.PURPLE_LAYERED_WOOL;
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL = ItemsPart04.BLUE_LAYERED_WOOL;
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL = ItemsPart04.BROWN_LAYERED_WOOL;
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL = ItemsPart04.GREEN_LAYERED_WOOL;
    public static final RegistryObject<Item> RED_LAYERED_WOOL = ItemsPart04.RED_LAYERED_WOOL;
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL = ItemsPart04.BLACK_LAYERED_WOOL;
    public static final RegistryObject<Item> WHITE_LAYERED_WOOL_SLAB = ItemsPart04.WHITE_LAYERED_WOOL_SLAB;
    public static final RegistryObject<Item> WHITE_LAYERED_WOOL_STAIRS = ItemsPart04.WHITE_LAYERED_WOOL_STAIRS;
    public static final RegistryObject<Item> WHITE_LAYERED_WOOL_WALL = ItemsPart04.WHITE_LAYERED_WOOL_WALL;
    public static final RegistryObject<Item> WHITE_LAYERED_WOOL_CARPET = ItemsPart04.WHITE_LAYERED_WOOL_CARPET;
    public static final RegistryObject<Item> WHITE_LAYERED_WOOL_LAYERS = ItemsPart04.WHITE_LAYERED_WOOL_LAYERS;
    public static final RegistryObject<Item> WHITE_LAYERED_WOOL_VERTICAL_SLAB =
            ItemsPart04.WHITE_LAYERED_WOOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL_SLAB = ItemsPart04.ORANGE_LAYERED_WOOL_SLAB;
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL_STAIRS = ItemsPart04.ORANGE_LAYERED_WOOL_STAIRS;
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL_WALL = ItemsPart04.ORANGE_LAYERED_WOOL_WALL;
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL_CARPET = ItemsPart04.ORANGE_LAYERED_WOOL_CARPET;
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL_LAYERS = ItemsPart04.ORANGE_LAYERED_WOOL_LAYERS;
    public static final RegistryObject<Item> ORANGE_LAYERED_WOOL_VERTICAL_SLAB =
            ItemsPart04.ORANGE_LAYERED_WOOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL_SLAB = ItemsPart04.MAGENTA_LAYERED_WOOL_SLAB;
    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL_STAIRS = ItemsPart04.MAGENTA_LAYERED_WOOL_STAIRS;
    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL_WALL = ItemsPart04.MAGENTA_LAYERED_WOOL_WALL;
    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL_CARPET = ItemsPart04.MAGENTA_LAYERED_WOOL_CARPET;
    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL_LAYERS = ItemsPart04.MAGENTA_LAYERED_WOOL_LAYERS;
    public static final RegistryObject<Item> MAGENTA_LAYERED_WOOL_VERTICAL_SLAB =
            ItemsPart04.MAGENTA_LAYERED_WOOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL_SLAB = ItemsPart04.LIGHT_BLUE_LAYERED_WOOL_SLAB;
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL_STAIRS =
            ItemsPart04.LIGHT_BLUE_LAYERED_WOOL_STAIRS;
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL_WALL = ItemsPart04.LIGHT_BLUE_LAYERED_WOOL_WALL;
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL_CARPET =
            ItemsPart04.LIGHT_BLUE_LAYERED_WOOL_CARPET;
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL_LAYERS =
            ItemsPart04.LIGHT_BLUE_LAYERED_WOOL_LAYERS;
    public static final RegistryObject<Item> LIGHT_BLUE_LAYERED_WOOL_VERTICAL_SLAB =
            ItemsPart04.LIGHT_BLUE_LAYERED_WOOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL_SLAB = ItemsPart04.YELLOW_LAYERED_WOOL_SLAB;
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL_STAIRS = ItemsPart04.YELLOW_LAYERED_WOOL_STAIRS;
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL_WALL = ItemsPart04.YELLOW_LAYERED_WOOL_WALL;
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL_CARPET = ItemsPart04.YELLOW_LAYERED_WOOL_CARPET;
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL_LAYERS = ItemsPart04.YELLOW_LAYERED_WOOL_LAYERS;
    public static final RegistryObject<Item> YELLOW_LAYERED_WOOL_VERTICAL_SLAB =
            ItemsPart04.YELLOW_LAYERED_WOOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIME_LAYERED_WOOL_SLAB = ItemsPart04.LIME_LAYERED_WOOL_SLAB;
    public static final RegistryObject<Item> LIME_LAYERED_WOOL_STAIRS = ItemsPart04.LIME_LAYERED_WOOL_STAIRS;
    public static final RegistryObject<Item> LIME_LAYERED_WOOL_WALL = ItemsPart04.LIME_LAYERED_WOOL_WALL;
    public static final RegistryObject<Item> LIME_LAYERED_WOOL_CARPET = ItemsPart04.LIME_LAYERED_WOOL_CARPET;
    public static final RegistryObject<Item> LIME_LAYERED_WOOL_LAYERS = ItemsPart04.LIME_LAYERED_WOOL_LAYERS;
    public static final RegistryObject<Item> LIME_LAYERED_WOOL_VERTICAL_SLAB =
            ItemsPart04.LIME_LAYERED_WOOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> PINK_LAYERED_WOOL_SLAB = ItemsPart04.PINK_LAYERED_WOOL_SLAB;
    public static final RegistryObject<Item> PINK_LAYERED_WOOL_STAIRS = ItemsPart04.PINK_LAYERED_WOOL_STAIRS;
    public static final RegistryObject<Item> PINK_LAYERED_WOOL_WALL = ItemsPart04.PINK_LAYERED_WOOL_WALL;
    public static final RegistryObject<Item> PINK_LAYERED_WOOL_CARPET = ItemsPart04.PINK_LAYERED_WOOL_CARPET;
    public static final RegistryObject<Item> PINK_LAYERED_WOOL_LAYERS = ItemsPart04.PINK_LAYERED_WOOL_LAYERS;
    public static final RegistryObject<Item> PINK_LAYERED_WOOL_VERTICAL_SLAB =
            ItemsPart04.PINK_LAYERED_WOOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL_SLAB = ItemsPart04.GRAY_LAYERED_WOOL_SLAB;
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL_STAIRS = ItemsPart04.GRAY_LAYERED_WOOL_STAIRS;
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL_WALL = ItemsPart04.GRAY_LAYERED_WOOL_WALL;
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL_CARPET = ItemsPart04.GRAY_LAYERED_WOOL_CARPET;
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL_LAYERS = ItemsPart04.GRAY_LAYERED_WOOL_LAYERS;
    public static final RegistryObject<Item> GRAY_LAYERED_WOOL_VERTICAL_SLAB =
            ItemsPart04.GRAY_LAYERED_WOOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL_SLAB = ItemsPart04.LIGHT_GRAY_LAYERED_WOOL_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL_STAIRS =
            ItemsPart04.LIGHT_GRAY_LAYERED_WOOL_STAIRS;
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL_WALL = ItemsPart04.LIGHT_GRAY_LAYERED_WOOL_WALL;
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL_CARPET =
            ItemsPart04.LIGHT_GRAY_LAYERED_WOOL_CARPET;
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL_LAYERS =
            ItemsPart04.LIGHT_GRAY_LAYERED_WOOL_LAYERS;
    public static final RegistryObject<Item> LIGHT_GRAY_LAYERED_WOOL_VERTICAL_SLAB =
            ItemsPart04.LIGHT_GRAY_LAYERED_WOOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL_SLAB = ItemsPart04.CYAN_LAYERED_WOOL_SLAB;
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL_STAIRS = ItemsPart04.CYAN_LAYERED_WOOL_STAIRS;
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL_WALL = ItemsPart04.CYAN_LAYERED_WOOL_WALL;
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL_CARPET = ItemsPart04.CYAN_LAYERED_WOOL_CARPET;
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL_LAYERS = ItemsPart04.CYAN_LAYERED_WOOL_LAYERS;
    public static final RegistryObject<Item> CYAN_LAYERED_WOOL_VERTICAL_SLAB =
            ItemsPart04.CYAN_LAYERED_WOOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL_SLAB = ItemsPart04.PURPLE_LAYERED_WOOL_SLAB;
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL_STAIRS = ItemsPart04.PURPLE_LAYERED_WOOL_STAIRS;
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL_WALL = ItemsPart04.PURPLE_LAYERED_WOOL_WALL;
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL_CARPET = ItemsPart04.PURPLE_LAYERED_WOOL_CARPET;
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL_LAYERS = ItemsPart04.PURPLE_LAYERED_WOOL_LAYERS;
    public static final RegistryObject<Item> PURPLE_LAYERED_WOOL_VERTICAL_SLAB =
            ItemsPart04.PURPLE_LAYERED_WOOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL_SLAB = ItemsPart04.BLUE_LAYERED_WOOL_SLAB;
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL_STAIRS = ItemsPart04.BLUE_LAYERED_WOOL_STAIRS;
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL_WALL = ItemsPart04.BLUE_LAYERED_WOOL_WALL;
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL_CARPET = ItemsPart04.BLUE_LAYERED_WOOL_CARPET;
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL_LAYERS = ItemsPart04.BLUE_LAYERED_WOOL_LAYERS;
    public static final RegistryObject<Item> BLUE_LAYERED_WOOL_VERTICAL_SLAB =
            ItemsPart04.BLUE_LAYERED_WOOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL_SLAB = ItemsPart04.BROWN_LAYERED_WOOL_SLAB;
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL_STAIRS = ItemsPart04.BROWN_LAYERED_WOOL_STAIRS;
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL_WALL = ItemsPart04.BROWN_LAYERED_WOOL_WALL;
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL_CARPET = ItemsPart04.BROWN_LAYERED_WOOL_CARPET;
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL_LAYERS = ItemsPart04.BROWN_LAYERED_WOOL_LAYERS;
    public static final RegistryObject<Item> BROWN_LAYERED_WOOL_VERTICAL_SLAB =
            ItemsPart04.BROWN_LAYERED_WOOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL_SLAB = ItemsPart04.GREEN_LAYERED_WOOL_SLAB;
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL_STAIRS = ItemsPart04.GREEN_LAYERED_WOOL_STAIRS;
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL_WALL = ItemsPart04.GREEN_LAYERED_WOOL_WALL;
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL_CARPET = ItemsPart04.GREEN_LAYERED_WOOL_CARPET;
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL_LAYERS = ItemsPart04.GREEN_LAYERED_WOOL_LAYERS;
    public static final RegistryObject<Item> GREEN_LAYERED_WOOL_VERTICAL_SLAB =
            ItemsPart04.GREEN_LAYERED_WOOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> RED_LAYERED_WOOL_SLAB = ItemsPart04.RED_LAYERED_WOOL_SLAB;
    public static final RegistryObject<Item> RED_LAYERED_WOOL_STAIRS = ItemsPart04.RED_LAYERED_WOOL_STAIRS;
    public static final RegistryObject<Item> RED_LAYERED_WOOL_WALL = ItemsPart04.RED_LAYERED_WOOL_WALL;
    public static final RegistryObject<Item> RED_LAYERED_WOOL_CARPET = ItemsPart04.RED_LAYERED_WOOL_CARPET;
    public static final RegistryObject<Item> RED_LAYERED_WOOL_LAYERS = ItemsPart04.RED_LAYERED_WOOL_LAYERS;
    public static final RegistryObject<Item> RED_LAYERED_WOOL_VERTICAL_SLAB =
            ItemsPart04.RED_LAYERED_WOOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL_SLAB = ItemsPart04.BLACK_LAYERED_WOOL_SLAB;
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL_STAIRS = ItemsPart04.BLACK_LAYERED_WOOL_STAIRS;
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL_WALL = ItemsPart04.BLACK_LAYERED_WOOL_WALL;
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL_CARPET = ItemsPart04.BLACK_LAYERED_WOOL_CARPET;
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL_LAYERS = ItemsPart04.BLACK_LAYERED_WOOL_LAYERS;
    public static final RegistryObject<Item> BLACK_LAYERED_WOOL_VERTICAL_SLAB =
            ItemsPart04.BLACK_LAYERED_WOOL_VERTICAL_SLAB;
    public static final RegistryObject<Item> FLAMING_STEEL_INGOT = ItemsPart04.FLAMING_STEEL_INGOT;
    public static final RegistryObject<Item> FLAMING_STEEL_NUGGET = ItemsPart04.FLAMING_STEEL_NUGGET;
    public static final RegistryObject<Item> FLAMING_STEEL_BLOCK = ItemsPart04.FLAMING_STEEL_BLOCK;
    public static final RegistryObject<Item> FLAMING_STEEL_BLOCK_SLAB = ItemsPart04.FLAMING_STEEL_BLOCK_SLAB;
    public static final RegistryObject<Item> FLAMING_STEEL_BLOCK_VERTICAL_SLAB =
            ItemsPart04.FLAMING_STEEL_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> FLAMING_STEEL_BLOCK_STAIRS = ItemsPart04.FLAMING_STEEL_BLOCK_STAIRS;
    public static final RegistryObject<Item> FLAMING_STEEL_BLOCK_WALL = ItemsPart04.FLAMING_STEEL_BLOCK_WALL;
    public static final RegistryObject<Item> FLAMING_CUT_STEEL = ItemsPart04.FLAMING_CUT_STEEL;
    public static final RegistryObject<Item> FLAMING_CUT_STEEL_SLAB = ItemsPart04.FLAMING_CUT_STEEL_SLAB;
    public static final RegistryObject<Item> FLAMING_CUT_STEEL_VERTICAL_SLAB =
            ItemsPart04.FLAMING_CUT_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> FLAMING_CUT_STEEL_STAIRS = ItemsPart04.FLAMING_CUT_STEEL_STAIRS;
    public static final RegistryObject<Item> FLAMING_CUT_STEEL_WALL = ItemsPart04.FLAMING_CUT_STEEL_WALL;
    public static final RegistryObject<Item> FLAMING_POLISHED_STEEL = ItemsPart04.FLAMING_POLISHED_STEEL;
    public static final RegistryObject<Item> FLAMING_POLISHED_STEEL_SLAB = ItemsPart04.FLAMING_POLISHED_STEEL_SLAB;
    public static final RegistryObject<Item> FLAMING_POLISHED_STEEL_VERTICAL_SLAB =
            ItemsPart04.FLAMING_POLISHED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> FLAMING_POLISHED_STEEL_STAIRS = ItemsPart04.FLAMING_POLISHED_STEEL_STAIRS;
    public static final RegistryObject<Item> FLAMING_POLISHED_STEEL_WALL = ItemsPart04.FLAMING_POLISHED_STEEL_WALL;
    public static final RegistryObject<Item> FLAMING_PRESSED_STEEL = ItemsPart04.FLAMING_PRESSED_STEEL;
    public static final RegistryObject<Item> FLAMING_PRESSED_STEEL_SLAB = ItemsPart04.FLAMING_PRESSED_STEEL_SLAB;
    public static final RegistryObject<Item> FLAMING_PRESSED_STEEL_VERTICAL_SLAB =
            ItemsPart04.FLAMING_PRESSED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> FLAMING_PRESSED_STEEL_STAIRS = ItemsPart04.FLAMING_PRESSED_STEEL_STAIRS;
    public static final RegistryObject<Item> FLAMING_PRESSED_STEEL_WALL = ItemsPart04.FLAMING_PRESSED_STEEL_WALL;
    public static final RegistryObject<Item> FLAMING_STEEL_CASING = ItemsPart04.FLAMING_STEEL_CASING;
    public static final RegistryObject<Item> FLAMING_STEEL_GRATE = ItemsPart04.FLAMING_STEEL_GRATE;
    public static final RegistryObject<Item> FLAMING_STEEL_PILLAR = ItemsPart04.FLAMING_STEEL_PILLAR;
    public static final RegistryObject<Item> FLAMING_BOLTED_STEEL_PILLAR = ItemsPart04.FLAMING_BOLTED_STEEL_PILLAR;
    public static final RegistryObject<Item> FLAMING_STEEL_BOLTS = ItemsPart04.FLAMING_STEEL_BOLTS;
    public static final RegistryObject<Item> FLAMING_STEEL_DOOR = ItemsPart04.FLAMING_STEEL_DOOR;
    public static final RegistryObject<Item> FLAMING_STEEL_TRAPDOOR = ItemsPart04.FLAMING_STEEL_TRAPDOOR;
    public static final RegistryObject<Item> FLAMING_STEEL_BUTTON = ItemsPart04.FLAMING_STEEL_BUTTON;
    public static final RegistryObject<Item> FLAMING_STEEL_PRESSURE_PLATE = ItemsPart04.FLAMING_STEEL_PRESSURE_PLATE;
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_BLOCK = ItemsPart04.MAGENTA_FLAMING_STEEL_BLOCK;
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_BLOCK_SLAB =
            ItemsPart04.MAGENTA_FLAMING_STEEL_BLOCK_SLAB;
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_BLOCK_VERTICAL_SLAB =
            ItemsPart04.MAGENTA_FLAMING_STEEL_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_BLOCK_STAIRS =
            ItemsPart04.MAGENTA_FLAMING_STEEL_BLOCK_STAIRS;
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_BLOCK_WALL =
            ItemsPart04.MAGENTA_FLAMING_STEEL_BLOCK_WALL;
    public static final RegistryObject<Item> MAGENTA_FLAMING_CUT_STEEL = ItemsPart04.MAGENTA_FLAMING_CUT_STEEL;
    public static final RegistryObject<Item> MAGENTA_FLAMING_CUT_STEEL_SLAB =
            ItemsPart04.MAGENTA_FLAMING_CUT_STEEL_SLAB;
    public static final RegistryObject<Item> MAGENTA_FLAMING_CUT_STEEL_VERTICAL_SLAB =
            ItemsPart04.MAGENTA_FLAMING_CUT_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> MAGENTA_FLAMING_CUT_STEEL_STAIRS =
            ItemsPart04.MAGENTA_FLAMING_CUT_STEEL_STAIRS;
    public static final RegistryObject<Item> MAGENTA_FLAMING_CUT_STEEL_WALL =
            ItemsPart04.MAGENTA_FLAMING_CUT_STEEL_WALL;
    public static final RegistryObject<Item> MAGENTA_FLAMING_POLISHED_STEEL =
            ItemsPart04.MAGENTA_FLAMING_POLISHED_STEEL;
    public static final RegistryObject<Item> MAGENTA_FLAMING_POLISHED_STEEL_SLAB =
            ItemsPart04.MAGENTA_FLAMING_POLISHED_STEEL_SLAB;
    public static final RegistryObject<Item> MAGENTA_FLAMING_POLISHED_STEEL_VERTICAL_SLAB =
            ItemsPart04.MAGENTA_FLAMING_POLISHED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> MAGENTA_FLAMING_POLISHED_STEEL_STAIRS =
            ItemsPart04.MAGENTA_FLAMING_POLISHED_STEEL_STAIRS;
    public static final RegistryObject<Item> MAGENTA_FLAMING_POLISHED_STEEL_WALL =
            ItemsPart04.MAGENTA_FLAMING_POLISHED_STEEL_WALL;
    public static final RegistryObject<Item> MAGENTA_FLAMING_PRESSED_STEEL = ItemsPart05.MAGENTA_FLAMING_PRESSED_STEEL;
    public static final RegistryObject<Item> MAGENTA_FLAMING_PRESSED_STEEL_SLAB =
            ItemsPart05.MAGENTA_FLAMING_PRESSED_STEEL_SLAB;
    public static final RegistryObject<Item> MAGENTA_FLAMING_PRESSED_STEEL_VERTICAL_SLAB =
            ItemsPart05.MAGENTA_FLAMING_PRESSED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> MAGENTA_FLAMING_PRESSED_STEEL_STAIRS =
            ItemsPart05.MAGENTA_FLAMING_PRESSED_STEEL_STAIRS;
    public static final RegistryObject<Item> MAGENTA_FLAMING_PRESSED_STEEL_WALL =
            ItemsPart05.MAGENTA_FLAMING_PRESSED_STEEL_WALL;
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_CASING = ItemsPart05.MAGENTA_FLAMING_STEEL_CASING;
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_GRATE = ItemsPart05.MAGENTA_FLAMING_STEEL_GRATE;
    public static final RegistryObject<Item> MAGENTA_FLAMING_STEEL_PILLAR = ItemsPart05.MAGENTA_FLAMING_STEEL_PILLAR;
    public static final RegistryObject<Item> MAGENTA_FLAMING_BOLTED_STEEL_PILLAR =
            ItemsPart05.MAGENTA_FLAMING_BOLTED_STEEL_PILLAR;
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_STEEL_BLOCK =
            ItemsPart05.LIGHT_BLUE_FLAMING_STEEL_BLOCK;
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_STEEL_BLOCK_SLAB =
            ItemsPart05.LIGHT_BLUE_FLAMING_STEEL_BLOCK_SLAB;
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_STEEL_BLOCK_VERTICAL_SLAB =
            ItemsPart05.LIGHT_BLUE_FLAMING_STEEL_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_STEEL_BLOCK_STAIRS =
            ItemsPart05.LIGHT_BLUE_FLAMING_STEEL_BLOCK_STAIRS;
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_STEEL_BLOCK_WALL =
            ItemsPart05.LIGHT_BLUE_FLAMING_STEEL_BLOCK_WALL;
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_CUT_STEEL = ItemsPart05.LIGHT_BLUE_FLAMING_CUT_STEEL;
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_CUT_STEEL_SLAB =
            ItemsPart05.LIGHT_BLUE_FLAMING_CUT_STEEL_SLAB;
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_CUT_STEEL_VERTICAL_SLAB =
            ItemsPart05.LIGHT_BLUE_FLAMING_CUT_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_CUT_STEEL_STAIRS =
            ItemsPart05.LIGHT_BLUE_FLAMING_CUT_STEEL_STAIRS;
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_CUT_STEEL_WALL =
            ItemsPart05.LIGHT_BLUE_FLAMING_CUT_STEEL_WALL;
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_POLISHED_STEEL =
            ItemsPart05.LIGHT_BLUE_FLAMING_POLISHED_STEEL;
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_POLISHED_STEEL_SLAB =
            ItemsPart05.LIGHT_BLUE_FLAMING_POLISHED_STEEL_SLAB;
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_POLISHED_STEEL_VERTICAL_SLAB =
            ItemsPart05.LIGHT_BLUE_FLAMING_POLISHED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_POLISHED_STEEL_STAIRS =
            ItemsPart05.LIGHT_BLUE_FLAMING_POLISHED_STEEL_STAIRS;
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_POLISHED_STEEL_WALL =
            ItemsPart05.LIGHT_BLUE_FLAMING_POLISHED_STEEL_WALL;
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_PRESSED_STEEL =
            ItemsPart05.LIGHT_BLUE_FLAMING_PRESSED_STEEL;
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_PRESSED_STEEL_SLAB =
            ItemsPart05.LIGHT_BLUE_FLAMING_PRESSED_STEEL_SLAB;
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_PRESSED_STEEL_VERTICAL_SLAB =
            ItemsPart05.LIGHT_BLUE_FLAMING_PRESSED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_PRESSED_STEEL_STAIRS =
            ItemsPart05.LIGHT_BLUE_FLAMING_PRESSED_STEEL_STAIRS;
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_PRESSED_STEEL_WALL =
            ItemsPart05.LIGHT_BLUE_FLAMING_PRESSED_STEEL_WALL;
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_STEEL_CASING =
            ItemsPart05.LIGHT_BLUE_FLAMING_STEEL_CASING;
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_STEEL_GRATE =
            ItemsPart05.LIGHT_BLUE_FLAMING_STEEL_GRATE;
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_STEEL_PILLAR =
            ItemsPart05.LIGHT_BLUE_FLAMING_STEEL_PILLAR;
    public static final RegistryObject<Item> LIGHT_BLUE_FLAMING_BOLTED_STEEL_PILLAR =
            ItemsPart05.LIGHT_BLUE_FLAMING_BOLTED_STEEL_PILLAR;
    public static final RegistryObject<Item> YELLOW_FLAMING_STEEL_BLOCK = ItemsPart05.YELLOW_FLAMING_STEEL_BLOCK;
    public static final RegistryObject<Item> YELLOW_FLAMING_STEEL_BLOCK_SLAB =
            ItemsPart05.YELLOW_FLAMING_STEEL_BLOCK_SLAB;
    public static final RegistryObject<Item> YELLOW_FLAMING_STEEL_BLOCK_VERTICAL_SLAB =
            ItemsPart05.YELLOW_FLAMING_STEEL_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> YELLOW_FLAMING_STEEL_BLOCK_STAIRS =
            ItemsPart05.YELLOW_FLAMING_STEEL_BLOCK_STAIRS;
    public static final RegistryObject<Item> YELLOW_FLAMING_STEEL_BLOCK_WALL =
            ItemsPart05.YELLOW_FLAMING_STEEL_BLOCK_WALL;
    public static final RegistryObject<Item> YELLOW_FLAMING_CUT_STEEL = ItemsPart05.YELLOW_FLAMING_CUT_STEEL;
    public static final RegistryObject<Item> YELLOW_FLAMING_CUT_STEEL_SLAB = ItemsPart05.YELLOW_FLAMING_CUT_STEEL_SLAB;
    public static final RegistryObject<Item> YELLOW_FLAMING_CUT_STEEL_VERTICAL_SLAB =
            ItemsPart05.YELLOW_FLAMING_CUT_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> YELLOW_FLAMING_CUT_STEEL_STAIRS =
            ItemsPart05.YELLOW_FLAMING_CUT_STEEL_STAIRS;
    public static final RegistryObject<Item> YELLOW_FLAMING_CUT_STEEL_WALL = ItemsPart05.YELLOW_FLAMING_CUT_STEEL_WALL;
    public static final RegistryObject<Item> YELLOW_FLAMING_POLISHED_STEEL = ItemsPart05.YELLOW_FLAMING_POLISHED_STEEL;
    public static final RegistryObject<Item> YELLOW_FLAMING_POLISHED_STEEL_SLAB =
            ItemsPart05.YELLOW_FLAMING_POLISHED_STEEL_SLAB;
    public static final RegistryObject<Item> YELLOW_FLAMING_POLISHED_STEEL_VERTICAL_SLAB =
            ItemsPart05.YELLOW_FLAMING_POLISHED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> YELLOW_FLAMING_POLISHED_STEEL_STAIRS =
            ItemsPart05.YELLOW_FLAMING_POLISHED_STEEL_STAIRS;
    public static final RegistryObject<Item> YELLOW_FLAMING_POLISHED_STEEL_WALL =
            ItemsPart05.YELLOW_FLAMING_POLISHED_STEEL_WALL;
    public static final RegistryObject<Item> YELLOW_FLAMING_PRESSED_STEEL = ItemsPart05.YELLOW_FLAMING_PRESSED_STEEL;
    public static final RegistryObject<Item> YELLOW_FLAMING_PRESSED_STEEL_SLAB =
            ItemsPart05.YELLOW_FLAMING_PRESSED_STEEL_SLAB;
    public static final RegistryObject<Item> YELLOW_FLAMING_PRESSED_STEEL_VERTICAL_SLAB =
            ItemsPart05.YELLOW_FLAMING_PRESSED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> YELLOW_FLAMING_PRESSED_STEEL_STAIRS =
            ItemsPart05.YELLOW_FLAMING_PRESSED_STEEL_STAIRS;
    public static final RegistryObject<Item> YELLOW_FLAMING_PRESSED_STEEL_WALL =
            ItemsPart05.YELLOW_FLAMING_PRESSED_STEEL_WALL;
    public static final RegistryObject<Item> YELLOW_FLAMING_STEEL_CASING = ItemsPart05.YELLOW_FLAMING_STEEL_CASING;
    public static final RegistryObject<Item> YELLOW_FLAMING_STEEL_GRATE = ItemsPart05.YELLOW_FLAMING_STEEL_GRATE;
    public static final RegistryObject<Item> YELLOW_FLAMING_STEEL_PILLAR = ItemsPart05.YELLOW_FLAMING_STEEL_PILLAR;
    public static final RegistryObject<Item> YELLOW_FLAMING_BOLTED_STEEL_PILLAR =
            ItemsPart05.YELLOW_FLAMING_BOLTED_STEEL_PILLAR;
    public static final RegistryObject<Item> LIME_FLAMING_STEEL_BLOCK = ItemsPart05.LIME_FLAMING_STEEL_BLOCK;
    public static final RegistryObject<Item> LIME_FLAMING_STEEL_BLOCK_SLAB = ItemsPart05.LIME_FLAMING_STEEL_BLOCK_SLAB;
    public static final RegistryObject<Item> LIME_FLAMING_STEEL_BLOCK_VERTICAL_SLAB =
            ItemsPart05.LIME_FLAMING_STEEL_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIME_FLAMING_STEEL_BLOCK_STAIRS =
            ItemsPart05.LIME_FLAMING_STEEL_BLOCK_STAIRS;
    public static final RegistryObject<Item> LIME_FLAMING_STEEL_BLOCK_WALL = ItemsPart05.LIME_FLAMING_STEEL_BLOCK_WALL;
    public static final RegistryObject<Item> LIME_FLAMING_CUT_STEEL = ItemsPart05.LIME_FLAMING_CUT_STEEL;
    public static final RegistryObject<Item> LIME_FLAMING_CUT_STEEL_SLAB = ItemsPart05.LIME_FLAMING_CUT_STEEL_SLAB;
    public static final RegistryObject<Item> LIME_FLAMING_CUT_STEEL_VERTICAL_SLAB =
            ItemsPart05.LIME_FLAMING_CUT_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIME_FLAMING_CUT_STEEL_STAIRS = ItemsPart05.LIME_FLAMING_CUT_STEEL_STAIRS;
    public static final RegistryObject<Item> LIME_FLAMING_CUT_STEEL_WALL = ItemsPart05.LIME_FLAMING_CUT_STEEL_WALL;
    public static final RegistryObject<Item> LIME_FLAMING_POLISHED_STEEL = ItemsPart05.LIME_FLAMING_POLISHED_STEEL;
    public static final RegistryObject<Item> LIME_FLAMING_POLISHED_STEEL_SLAB =
            ItemsPart05.LIME_FLAMING_POLISHED_STEEL_SLAB;
    public static final RegistryObject<Item> LIME_FLAMING_POLISHED_STEEL_VERTICAL_SLAB =
            ItemsPart05.LIME_FLAMING_POLISHED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIME_FLAMING_POLISHED_STEEL_STAIRS =
            ItemsPart05.LIME_FLAMING_POLISHED_STEEL_STAIRS;
    public static final RegistryObject<Item> LIME_FLAMING_POLISHED_STEEL_WALL =
            ItemsPart05.LIME_FLAMING_POLISHED_STEEL_WALL;
    public static final RegistryObject<Item> LIME_FLAMING_PRESSED_STEEL = ItemsPart05.LIME_FLAMING_PRESSED_STEEL;
    public static final RegistryObject<Item> LIME_FLAMING_PRESSED_STEEL_SLAB =
            ItemsPart05.LIME_FLAMING_PRESSED_STEEL_SLAB;
    public static final RegistryObject<Item> LIME_FLAMING_PRESSED_STEEL_VERTICAL_SLAB =
            ItemsPart05.LIME_FLAMING_PRESSED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIME_FLAMING_PRESSED_STEEL_STAIRS =
            ItemsPart05.LIME_FLAMING_PRESSED_STEEL_STAIRS;
    public static final RegistryObject<Item> LIME_FLAMING_PRESSED_STEEL_WALL =
            ItemsPart05.LIME_FLAMING_PRESSED_STEEL_WALL;
    public static final RegistryObject<Item> LIME_FLAMING_STEEL_CASING = ItemsPart05.LIME_FLAMING_STEEL_CASING;
    public static final RegistryObject<Item> LIME_FLAMING_STEEL_GRATE = ItemsPart05.LIME_FLAMING_STEEL_GRATE;
    public static final RegistryObject<Item> LIME_FLAMING_STEEL_PILLAR = ItemsPart05.LIME_FLAMING_STEEL_PILLAR;
    public static final RegistryObject<Item> LIME_FLAMING_BOLTED_STEEL_PILLAR =
            ItemsPart05.LIME_FLAMING_BOLTED_STEEL_PILLAR;
    public static final RegistryObject<Item> PINK_FLAMING_STEEL_BLOCK = ItemsPart05.PINK_FLAMING_STEEL_BLOCK;
    public static final RegistryObject<Item> PINK_FLAMING_STEEL_BLOCK_SLAB = ItemsPart05.PINK_FLAMING_STEEL_BLOCK_SLAB;
    public static final RegistryObject<Item> PINK_FLAMING_STEEL_BLOCK_VERTICAL_SLAB =
            ItemsPart05.PINK_FLAMING_STEEL_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> PINK_FLAMING_STEEL_BLOCK_STAIRS =
            ItemsPart05.PINK_FLAMING_STEEL_BLOCK_STAIRS;
    public static final RegistryObject<Item> PINK_FLAMING_STEEL_BLOCK_WALL = ItemsPart05.PINK_FLAMING_STEEL_BLOCK_WALL;
    public static final RegistryObject<Item> PINK_FLAMING_CUT_STEEL = ItemsPart05.PINK_FLAMING_CUT_STEEL;
    public static final RegistryObject<Item> PINK_FLAMING_CUT_STEEL_SLAB = ItemsPart05.PINK_FLAMING_CUT_STEEL_SLAB;
    public static final RegistryObject<Item> PINK_FLAMING_CUT_STEEL_VERTICAL_SLAB =
            ItemsPart05.PINK_FLAMING_CUT_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> PINK_FLAMING_CUT_STEEL_STAIRS = ItemsPart05.PINK_FLAMING_CUT_STEEL_STAIRS;
    public static final RegistryObject<Item> PINK_FLAMING_CUT_STEEL_WALL = ItemsPart05.PINK_FLAMING_CUT_STEEL_WALL;
    public static final RegistryObject<Item> PINK_FLAMING_POLISHED_STEEL = ItemsPart05.PINK_FLAMING_POLISHED_STEEL;
    public static final RegistryObject<Item> PINK_FLAMING_POLISHED_STEEL_SLAB =
            ItemsPart05.PINK_FLAMING_POLISHED_STEEL_SLAB;
    public static final RegistryObject<Item> PINK_FLAMING_POLISHED_STEEL_VERTICAL_SLAB =
            ItemsPart05.PINK_FLAMING_POLISHED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> PINK_FLAMING_POLISHED_STEEL_STAIRS =
            ItemsPart05.PINK_FLAMING_POLISHED_STEEL_STAIRS;
    public static final RegistryObject<Item> PINK_FLAMING_POLISHED_STEEL_WALL =
            ItemsPart05.PINK_FLAMING_POLISHED_STEEL_WALL;
    public static final RegistryObject<Item> PINK_FLAMING_PRESSED_STEEL = ItemsPart05.PINK_FLAMING_PRESSED_STEEL;
    public static final RegistryObject<Item> PINK_FLAMING_PRESSED_STEEL_SLAB =
            ItemsPart05.PINK_FLAMING_PRESSED_STEEL_SLAB;
    public static final RegistryObject<Item> PINK_FLAMING_PRESSED_STEEL_VERTICAL_SLAB =
            ItemsPart05.PINK_FLAMING_PRESSED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> PINK_FLAMING_PRESSED_STEEL_STAIRS =
            ItemsPart05.PINK_FLAMING_PRESSED_STEEL_STAIRS;
    public static final RegistryObject<Item> PINK_FLAMING_PRESSED_STEEL_WALL =
            ItemsPart05.PINK_FLAMING_PRESSED_STEEL_WALL;
    public static final RegistryObject<Item> PINK_FLAMING_STEEL_CASING = ItemsPart05.PINK_FLAMING_STEEL_CASING;
    public static final RegistryObject<Item> PINK_FLAMING_STEEL_GRATE = ItemsPart05.PINK_FLAMING_STEEL_GRATE;
    public static final RegistryObject<Item> PINK_FLAMING_STEEL_PILLAR = ItemsPart05.PINK_FLAMING_STEEL_PILLAR;
    public static final RegistryObject<Item> PINK_FLAMING_BOLTED_STEEL_PILLAR =
            ItemsPart05.PINK_FLAMING_BOLTED_STEEL_PILLAR;
    public static final RegistryObject<Item> GRAY_FLAMING_STEEL_BLOCK = ItemsPart05.GRAY_FLAMING_STEEL_BLOCK;
    public static final RegistryObject<Item> GRAY_FLAMING_STEEL_BLOCK_SLAB = ItemsPart05.GRAY_FLAMING_STEEL_BLOCK_SLAB;
    public static final RegistryObject<Item> GRAY_FLAMING_STEEL_BLOCK_VERTICAL_SLAB =
            ItemsPart05.GRAY_FLAMING_STEEL_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> GRAY_FLAMING_STEEL_BLOCK_STAIRS =
            ItemsPart05.GRAY_FLAMING_STEEL_BLOCK_STAIRS;
    public static final RegistryObject<Item> GRAY_FLAMING_STEEL_BLOCK_WALL = ItemsPart05.GRAY_FLAMING_STEEL_BLOCK_WALL;
    public static final RegistryObject<Item> GRAY_FLAMING_CUT_STEEL = ItemsPart05.GRAY_FLAMING_CUT_STEEL;
    public static final RegistryObject<Item> GRAY_FLAMING_CUT_STEEL_SLAB = ItemsPart05.GRAY_FLAMING_CUT_STEEL_SLAB;
    public static final RegistryObject<Item> GRAY_FLAMING_CUT_STEEL_VERTICAL_SLAB =
            ItemsPart05.GRAY_FLAMING_CUT_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> GRAY_FLAMING_CUT_STEEL_STAIRS = ItemsPart05.GRAY_FLAMING_CUT_STEEL_STAIRS;
    public static final RegistryObject<Item> GRAY_FLAMING_CUT_STEEL_WALL = ItemsPart05.GRAY_FLAMING_CUT_STEEL_WALL;
    public static final RegistryObject<Item> GRAY_FLAMING_POLISHED_STEEL = ItemsPart05.GRAY_FLAMING_POLISHED_STEEL;
    public static final RegistryObject<Item> GRAY_FLAMING_POLISHED_STEEL_SLAB =
            ItemsPart05.GRAY_FLAMING_POLISHED_STEEL_SLAB;
    public static final RegistryObject<Item> GRAY_FLAMING_POLISHED_STEEL_VERTICAL_SLAB =
            ItemsPart05.GRAY_FLAMING_POLISHED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> GRAY_FLAMING_POLISHED_STEEL_STAIRS =
            ItemsPart05.GRAY_FLAMING_POLISHED_STEEL_STAIRS;
    public static final RegistryObject<Item> GRAY_FLAMING_POLISHED_STEEL_WALL =
            ItemsPart05.GRAY_FLAMING_POLISHED_STEEL_WALL;
    public static final RegistryObject<Item> GRAY_FLAMING_PRESSED_STEEL = ItemsPart05.GRAY_FLAMING_PRESSED_STEEL;
    public static final RegistryObject<Item> GRAY_FLAMING_PRESSED_STEEL_SLAB =
            ItemsPart05.GRAY_FLAMING_PRESSED_STEEL_SLAB;
    public static final RegistryObject<Item> GRAY_FLAMING_PRESSED_STEEL_VERTICAL_SLAB =
            ItemsPart05.GRAY_FLAMING_PRESSED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> GRAY_FLAMING_PRESSED_STEEL_STAIRS =
            ItemsPart05.GRAY_FLAMING_PRESSED_STEEL_STAIRS;
    public static final RegistryObject<Item> GRAY_FLAMING_PRESSED_STEEL_WALL =
            ItemsPart05.GRAY_FLAMING_PRESSED_STEEL_WALL;
    public static final RegistryObject<Item> GRAY_FLAMING_STEEL_CASING = ItemsPart05.GRAY_FLAMING_STEEL_CASING;
    public static final RegistryObject<Item> GRAY_FLAMING_STEEL_GRATE = ItemsPart05.GRAY_FLAMING_STEEL_GRATE;
    public static final RegistryObject<Item> GRAY_FLAMING_STEEL_PILLAR = ItemsPart05.GRAY_FLAMING_STEEL_PILLAR;
    public static final RegistryObject<Item> GRAY_FLAMING_BOLTED_STEEL_PILLAR =
            ItemsPart05.GRAY_FLAMING_BOLTED_STEEL_PILLAR;
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_STEEL_BLOCK =
            ItemsPart05.LIGHT_GRAY_FLAMING_STEEL_BLOCK;
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_STEEL_BLOCK_SLAB =
            ItemsPart05.LIGHT_GRAY_FLAMING_STEEL_BLOCK_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_STEEL_BLOCK_VERTICAL_SLAB =
            ItemsPart05.LIGHT_GRAY_FLAMING_STEEL_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_STEEL_BLOCK_STAIRS =
            ItemsPart05.LIGHT_GRAY_FLAMING_STEEL_BLOCK_STAIRS;
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_STEEL_BLOCK_WALL =
            ItemsPart05.LIGHT_GRAY_FLAMING_STEEL_BLOCK_WALL;
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_CUT_STEEL = ItemsPart05.LIGHT_GRAY_FLAMING_CUT_STEEL;
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_CUT_STEEL_SLAB =
            ItemsPart05.LIGHT_GRAY_FLAMING_CUT_STEEL_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_CUT_STEEL_VERTICAL_SLAB =
            ItemsPart05.LIGHT_GRAY_FLAMING_CUT_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_CUT_STEEL_STAIRS =
            ItemsPart05.LIGHT_GRAY_FLAMING_CUT_STEEL_STAIRS;
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_CUT_STEEL_WALL =
            ItemsPart05.LIGHT_GRAY_FLAMING_CUT_STEEL_WALL;
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_POLISHED_STEEL =
            ItemsPart05.LIGHT_GRAY_FLAMING_POLISHED_STEEL;
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_POLISHED_STEEL_SLAB =
            ItemsPart05.LIGHT_GRAY_FLAMING_POLISHED_STEEL_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_POLISHED_STEEL_VERTICAL_SLAB =
            ItemsPart05.LIGHT_GRAY_FLAMING_POLISHED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_POLISHED_STEEL_STAIRS =
            ItemsPart05.LIGHT_GRAY_FLAMING_POLISHED_STEEL_STAIRS;
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_POLISHED_STEEL_WALL =
            ItemsPart05.LIGHT_GRAY_FLAMING_POLISHED_STEEL_WALL;
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_PRESSED_STEEL =
            ItemsPart05.LIGHT_GRAY_FLAMING_PRESSED_STEEL;
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_PRESSED_STEEL_SLAB =
            ItemsPart05.LIGHT_GRAY_FLAMING_PRESSED_STEEL_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_PRESSED_STEEL_VERTICAL_SLAB =
            ItemsPart05.LIGHT_GRAY_FLAMING_PRESSED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_PRESSED_STEEL_STAIRS =
            ItemsPart05.LIGHT_GRAY_FLAMING_PRESSED_STEEL_STAIRS;
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_PRESSED_STEEL_WALL =
            ItemsPart05.LIGHT_GRAY_FLAMING_PRESSED_STEEL_WALL;
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_STEEL_CASING =
            ItemsPart05.LIGHT_GRAY_FLAMING_STEEL_CASING;
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_STEEL_GRATE =
            ItemsPart05.LIGHT_GRAY_FLAMING_STEEL_GRATE;
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_STEEL_PILLAR =
            ItemsPart05.LIGHT_GRAY_FLAMING_STEEL_PILLAR;
    public static final RegistryObject<Item> LIGHT_GRAY_FLAMING_BOLTED_STEEL_PILLAR =
            ItemsPart05.LIGHT_GRAY_FLAMING_BOLTED_STEEL_PILLAR;
    public static final RegistryObject<Item> CYAN_FLAMING_STEEL_BLOCK = ItemsPart05.CYAN_FLAMING_STEEL_BLOCK;
    public static final RegistryObject<Item> CYAN_FLAMING_STEEL_BLOCK_SLAB = ItemsPart05.CYAN_FLAMING_STEEL_BLOCK_SLAB;
    public static final RegistryObject<Item> CYAN_FLAMING_STEEL_BLOCK_VERTICAL_SLAB =
            ItemsPart05.CYAN_FLAMING_STEEL_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> CYAN_FLAMING_STEEL_BLOCK_STAIRS =
            ItemsPart05.CYAN_FLAMING_STEEL_BLOCK_STAIRS;
    public static final RegistryObject<Item> CYAN_FLAMING_STEEL_BLOCK_WALL = ItemsPart05.CYAN_FLAMING_STEEL_BLOCK_WALL;
    public static final RegistryObject<Item> CYAN_FLAMING_CUT_STEEL = ItemsPart05.CYAN_FLAMING_CUT_STEEL;
    public static final RegistryObject<Item> CYAN_FLAMING_CUT_STEEL_SLAB = ItemsPart05.CYAN_FLAMING_CUT_STEEL_SLAB;
    public static final RegistryObject<Item> CYAN_FLAMING_CUT_STEEL_VERTICAL_SLAB =
            ItemsPart05.CYAN_FLAMING_CUT_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> CYAN_FLAMING_CUT_STEEL_STAIRS = ItemsPart05.CYAN_FLAMING_CUT_STEEL_STAIRS;
    public static final RegistryObject<Item> CYAN_FLAMING_CUT_STEEL_WALL = ItemsPart05.CYAN_FLAMING_CUT_STEEL_WALL;
    public static final RegistryObject<Item> CYAN_FLAMING_POLISHED_STEEL = ItemsPart05.CYAN_FLAMING_POLISHED_STEEL;
    public static final RegistryObject<Item> CYAN_FLAMING_POLISHED_STEEL_SLAB =
            ItemsPart05.CYAN_FLAMING_POLISHED_STEEL_SLAB;
    public static final RegistryObject<Item> CYAN_FLAMING_POLISHED_STEEL_VERTICAL_SLAB =
            ItemsPart05.CYAN_FLAMING_POLISHED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> CYAN_FLAMING_POLISHED_STEEL_STAIRS =
            ItemsPart05.CYAN_FLAMING_POLISHED_STEEL_STAIRS;
    public static final RegistryObject<Item> CYAN_FLAMING_POLISHED_STEEL_WALL =
            ItemsPart05.CYAN_FLAMING_POLISHED_STEEL_WALL;
    public static final RegistryObject<Item> CYAN_FLAMING_PRESSED_STEEL = ItemsPart05.CYAN_FLAMING_PRESSED_STEEL;
    public static final RegistryObject<Item> CYAN_FLAMING_PRESSED_STEEL_SLAB =
            ItemsPart05.CYAN_FLAMING_PRESSED_STEEL_SLAB;
    public static final RegistryObject<Item> CYAN_FLAMING_PRESSED_STEEL_VERTICAL_SLAB =
            ItemsPart05.CYAN_FLAMING_PRESSED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> CYAN_FLAMING_PRESSED_STEEL_STAIRS =
            ItemsPart05.CYAN_FLAMING_PRESSED_STEEL_STAIRS;
    public static final RegistryObject<Item> CYAN_FLAMING_PRESSED_STEEL_WALL =
            ItemsPart05.CYAN_FLAMING_PRESSED_STEEL_WALL;
    public static final RegistryObject<Item> CYAN_FLAMING_STEEL_CASING = ItemsPart05.CYAN_FLAMING_STEEL_CASING;
    public static final RegistryObject<Item> CYAN_FLAMING_STEEL_GRATE = ItemsPart05.CYAN_FLAMING_STEEL_GRATE;
    public static final RegistryObject<Item> CYAN_FLAMING_STEEL_PILLAR = ItemsPart05.CYAN_FLAMING_STEEL_PILLAR;
    public static final RegistryObject<Item> CYAN_FLAMING_BOLTED_STEEL_PILLAR =
            ItemsPart05.CYAN_FLAMING_BOLTED_STEEL_PILLAR;
    public static final RegistryObject<Item> PURPLE_FLAMING_STEEL_BLOCK = ItemsPart05.PURPLE_FLAMING_STEEL_BLOCK;
    public static final RegistryObject<Item> PURPLE_FLAMING_STEEL_BLOCK_SLAB =
            ItemsPart05.PURPLE_FLAMING_STEEL_BLOCK_SLAB;
    public static final RegistryObject<Item> PURPLE_FLAMING_STEEL_BLOCK_VERTICAL_SLAB =
            ItemsPart05.PURPLE_FLAMING_STEEL_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> PURPLE_FLAMING_STEEL_BLOCK_STAIRS =
            ItemsPart05.PURPLE_FLAMING_STEEL_BLOCK_STAIRS;
    public static final RegistryObject<Item> PURPLE_FLAMING_STEEL_BLOCK_WALL =
            ItemsPart05.PURPLE_FLAMING_STEEL_BLOCK_WALL;
    public static final RegistryObject<Item> PURPLE_FLAMING_CUT_STEEL = ItemsPart05.PURPLE_FLAMING_CUT_STEEL;
    public static final RegistryObject<Item> PURPLE_FLAMING_CUT_STEEL_SLAB = ItemsPart05.PURPLE_FLAMING_CUT_STEEL_SLAB;
    public static final RegistryObject<Item> PURPLE_FLAMING_CUT_STEEL_VERTICAL_SLAB =
            ItemsPart05.PURPLE_FLAMING_CUT_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> PURPLE_FLAMING_CUT_STEEL_STAIRS =
            ItemsPart05.PURPLE_FLAMING_CUT_STEEL_STAIRS;
    public static final RegistryObject<Item> PURPLE_FLAMING_CUT_STEEL_WALL = ItemsPart05.PURPLE_FLAMING_CUT_STEEL_WALL;
    public static final RegistryObject<Item> PURPLE_FLAMING_POLISHED_STEEL = ItemsPart05.PURPLE_FLAMING_POLISHED_STEEL;
    public static final RegistryObject<Item> PURPLE_FLAMING_POLISHED_STEEL_SLAB =
            ItemsPart05.PURPLE_FLAMING_POLISHED_STEEL_SLAB;
    public static final RegistryObject<Item> PURPLE_FLAMING_POLISHED_STEEL_VERTICAL_SLAB =
            ItemsPart05.PURPLE_FLAMING_POLISHED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> PURPLE_FLAMING_POLISHED_STEEL_STAIRS =
            ItemsPart05.PURPLE_FLAMING_POLISHED_STEEL_STAIRS;
    public static final RegistryObject<Item> PURPLE_FLAMING_POLISHED_STEEL_WALL =
            ItemsPart05.PURPLE_FLAMING_POLISHED_STEEL_WALL;
    public static final RegistryObject<Item> PURPLE_FLAMING_PRESSED_STEEL = ItemsPart05.PURPLE_FLAMING_PRESSED_STEEL;
    public static final RegistryObject<Item> PURPLE_FLAMING_PRESSED_STEEL_SLAB =
            ItemsPart05.PURPLE_FLAMING_PRESSED_STEEL_SLAB;
    public static final RegistryObject<Item> PURPLE_FLAMING_PRESSED_STEEL_VERTICAL_SLAB =
            ItemsPart05.PURPLE_FLAMING_PRESSED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> PURPLE_FLAMING_PRESSED_STEEL_STAIRS =
            ItemsPart05.PURPLE_FLAMING_PRESSED_STEEL_STAIRS;
    public static final RegistryObject<Item> PURPLE_FLAMING_PRESSED_STEEL_WALL =
            ItemsPart05.PURPLE_FLAMING_PRESSED_STEEL_WALL;
    public static final RegistryObject<Item> PURPLE_FLAMING_STEEL_CASING = ItemsPart05.PURPLE_FLAMING_STEEL_CASING;
    public static final RegistryObject<Item> PURPLE_FLAMING_STEEL_GRATE = ItemsPart05.PURPLE_FLAMING_STEEL_GRATE;
    public static final RegistryObject<Item> PURPLE_FLAMING_STEEL_PILLAR = ItemsPart05.PURPLE_FLAMING_STEEL_PILLAR;
    public static final RegistryObject<Item> PURPLE_FLAMING_BOLTED_STEEL_PILLAR =
            ItemsPart05.PURPLE_FLAMING_BOLTED_STEEL_PILLAR;
    public static final RegistryObject<Item> BLUE_FLAMING_STEEL_BLOCK = ItemsPart05.BLUE_FLAMING_STEEL_BLOCK;
    public static final RegistryObject<Item> BLUE_FLAMING_STEEL_BLOCK_SLAB = ItemsPart05.BLUE_FLAMING_STEEL_BLOCK_SLAB;
    public static final RegistryObject<Item> BLUE_FLAMING_STEEL_BLOCK_VERTICAL_SLAB =
            ItemsPart05.BLUE_FLAMING_STEEL_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLUE_FLAMING_STEEL_BLOCK_STAIRS =
            ItemsPart05.BLUE_FLAMING_STEEL_BLOCK_STAIRS;
    public static final RegistryObject<Item> BLUE_FLAMING_STEEL_BLOCK_WALL = ItemsPart05.BLUE_FLAMING_STEEL_BLOCK_WALL;
    public static final RegistryObject<Item> BLUE_FLAMING_CUT_STEEL = ItemsPart05.BLUE_FLAMING_CUT_STEEL;
    public static final RegistryObject<Item> BLUE_FLAMING_CUT_STEEL_SLAB = ItemsPart05.BLUE_FLAMING_CUT_STEEL_SLAB;
    public static final RegistryObject<Item> BLUE_FLAMING_CUT_STEEL_VERTICAL_SLAB =
            ItemsPart05.BLUE_FLAMING_CUT_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLUE_FLAMING_CUT_STEEL_STAIRS = ItemsPart05.BLUE_FLAMING_CUT_STEEL_STAIRS;
    public static final RegistryObject<Item> BLUE_FLAMING_CUT_STEEL_WALL = ItemsPart05.BLUE_FLAMING_CUT_STEEL_WALL;
    public static final RegistryObject<Item> BLUE_FLAMING_POLISHED_STEEL = ItemsPart05.BLUE_FLAMING_POLISHED_STEEL;
    public static final RegistryObject<Item> BLUE_FLAMING_POLISHED_STEEL_SLAB =
            ItemsPart05.BLUE_FLAMING_POLISHED_STEEL_SLAB;
    public static final RegistryObject<Item> BLUE_FLAMING_POLISHED_STEEL_VERTICAL_SLAB =
            ItemsPart05.BLUE_FLAMING_POLISHED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLUE_FLAMING_POLISHED_STEEL_STAIRS =
            ItemsPart05.BLUE_FLAMING_POLISHED_STEEL_STAIRS;
    public static final RegistryObject<Item> BLUE_FLAMING_POLISHED_STEEL_WALL =
            ItemsPart05.BLUE_FLAMING_POLISHED_STEEL_WALL;
    public static final RegistryObject<Item> BLUE_FLAMING_PRESSED_STEEL = ItemsPart05.BLUE_FLAMING_PRESSED_STEEL;
    public static final RegistryObject<Item> BLUE_FLAMING_PRESSED_STEEL_SLAB =
            ItemsPart05.BLUE_FLAMING_PRESSED_STEEL_SLAB;
    public static final RegistryObject<Item> BLUE_FLAMING_PRESSED_STEEL_VERTICAL_SLAB =
            ItemsPart05.BLUE_FLAMING_PRESSED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLUE_FLAMING_PRESSED_STEEL_STAIRS =
            ItemsPart05.BLUE_FLAMING_PRESSED_STEEL_STAIRS;
    public static final RegistryObject<Item> BLUE_FLAMING_PRESSED_STEEL_WALL =
            ItemsPart05.BLUE_FLAMING_PRESSED_STEEL_WALL;
    public static final RegistryObject<Item> BLUE_FLAMING_STEEL_CASING = ItemsPart05.BLUE_FLAMING_STEEL_CASING;
    public static final RegistryObject<Item> BLUE_FLAMING_STEEL_GRATE = ItemsPart05.BLUE_FLAMING_STEEL_GRATE;
    public static final RegistryObject<Item> BLUE_FLAMING_STEEL_PILLAR = ItemsPart05.BLUE_FLAMING_STEEL_PILLAR;
    public static final RegistryObject<Item> BLUE_FLAMING_BOLTED_STEEL_PILLAR =
            ItemsPart05.BLUE_FLAMING_BOLTED_STEEL_PILLAR;
    public static final RegistryObject<Item> BROWN_FLAMING_STEEL_BLOCK = ItemsPart05.BROWN_FLAMING_STEEL_BLOCK;
    public static final RegistryObject<Item> BROWN_FLAMING_STEEL_BLOCK_SLAB =
            ItemsPart05.BROWN_FLAMING_STEEL_BLOCK_SLAB;
    public static final RegistryObject<Item> BROWN_FLAMING_STEEL_BLOCK_VERTICAL_SLAB =
            ItemsPart05.BROWN_FLAMING_STEEL_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> BROWN_FLAMING_STEEL_BLOCK_STAIRS =
            ItemsPart05.BROWN_FLAMING_STEEL_BLOCK_STAIRS;
    public static final RegistryObject<Item> BROWN_FLAMING_STEEL_BLOCK_WALL =
            ItemsPart05.BROWN_FLAMING_STEEL_BLOCK_WALL;
    public static final RegistryObject<Item> BROWN_FLAMING_CUT_STEEL = ItemsPart05.BROWN_FLAMING_CUT_STEEL;
    public static final RegistryObject<Item> BROWN_FLAMING_CUT_STEEL_SLAB = ItemsPart05.BROWN_FLAMING_CUT_STEEL_SLAB;
    public static final RegistryObject<Item> BROWN_FLAMING_CUT_STEEL_VERTICAL_SLAB =
            ItemsPart05.BROWN_FLAMING_CUT_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> BROWN_FLAMING_CUT_STEEL_STAIRS =
            ItemsPart05.BROWN_FLAMING_CUT_STEEL_STAIRS;
    public static final RegistryObject<Item> BROWN_FLAMING_CUT_STEEL_WALL = ItemsPart05.BROWN_FLAMING_CUT_STEEL_WALL;
    public static final RegistryObject<Item> BROWN_FLAMING_POLISHED_STEEL = ItemsPart05.BROWN_FLAMING_POLISHED_STEEL;
    public static final RegistryObject<Item> BROWN_FLAMING_POLISHED_STEEL_SLAB =
            ItemsPart05.BROWN_FLAMING_POLISHED_STEEL_SLAB;
    public static final RegistryObject<Item> BROWN_FLAMING_POLISHED_STEEL_VERTICAL_SLAB =
            ItemsPart05.BROWN_FLAMING_POLISHED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> BROWN_FLAMING_POLISHED_STEEL_STAIRS =
            ItemsPart05.BROWN_FLAMING_POLISHED_STEEL_STAIRS;
    public static final RegistryObject<Item> BROWN_FLAMING_POLISHED_STEEL_WALL =
            ItemsPart05.BROWN_FLAMING_POLISHED_STEEL_WALL;
    public static final RegistryObject<Item> BROWN_FLAMING_PRESSED_STEEL = ItemsPart05.BROWN_FLAMING_PRESSED_STEEL;
    public static final RegistryObject<Item> BROWN_FLAMING_PRESSED_STEEL_SLAB =
            ItemsPart05.BROWN_FLAMING_PRESSED_STEEL_SLAB;
    public static final RegistryObject<Item> BROWN_FLAMING_PRESSED_STEEL_VERTICAL_SLAB =
            ItemsPart05.BROWN_FLAMING_PRESSED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> BROWN_FLAMING_PRESSED_STEEL_STAIRS =
            ItemsPart05.BROWN_FLAMING_PRESSED_STEEL_STAIRS;
    public static final RegistryObject<Item> BROWN_FLAMING_PRESSED_STEEL_WALL =
            ItemsPart05.BROWN_FLAMING_PRESSED_STEEL_WALL;
    public static final RegistryObject<Item> BROWN_FLAMING_STEEL_CASING = ItemsPart05.BROWN_FLAMING_STEEL_CASING;
    public static final RegistryObject<Item> BROWN_FLAMING_STEEL_GRATE = ItemsPart05.BROWN_FLAMING_STEEL_GRATE;
    public static final RegistryObject<Item> BROWN_FLAMING_STEEL_PILLAR = ItemsPart05.BROWN_FLAMING_STEEL_PILLAR;
    public static final RegistryObject<Item> BROWN_FLAMING_BOLTED_STEEL_PILLAR =
            ItemsPart05.BROWN_FLAMING_BOLTED_STEEL_PILLAR;
    public static final RegistryObject<Item> GREEN_FLAMING_STEEL_BLOCK = ItemsPart05.GREEN_FLAMING_STEEL_BLOCK;
    public static final RegistryObject<Item> GREEN_FLAMING_STEEL_BLOCK_SLAB =
            ItemsPart05.GREEN_FLAMING_STEEL_BLOCK_SLAB;
    public static final RegistryObject<Item> GREEN_FLAMING_STEEL_BLOCK_VERTICAL_SLAB =
            ItemsPart05.GREEN_FLAMING_STEEL_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> GREEN_FLAMING_STEEL_BLOCK_STAIRS =
            ItemsPart05.GREEN_FLAMING_STEEL_BLOCK_STAIRS;
    public static final RegistryObject<Item> GREEN_FLAMING_STEEL_BLOCK_WALL =
            ItemsPart05.GREEN_FLAMING_STEEL_BLOCK_WALL;
    public static final RegistryObject<Item> GREEN_FLAMING_CUT_STEEL = ItemsPart05.GREEN_FLAMING_CUT_STEEL;
    public static final RegistryObject<Item> GREEN_FLAMING_CUT_STEEL_SLAB = ItemsPart05.GREEN_FLAMING_CUT_STEEL_SLAB;
    public static final RegistryObject<Item> GREEN_FLAMING_CUT_STEEL_VERTICAL_SLAB =
            ItemsPart05.GREEN_FLAMING_CUT_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> GREEN_FLAMING_CUT_STEEL_STAIRS =
            ItemsPart05.GREEN_FLAMING_CUT_STEEL_STAIRS;
    public static final RegistryObject<Item> GREEN_FLAMING_CUT_STEEL_WALL = ItemsPart05.GREEN_FLAMING_CUT_STEEL_WALL;
    public static final RegistryObject<Item> GREEN_FLAMING_POLISHED_STEEL = ItemsPart05.GREEN_FLAMING_POLISHED_STEEL;
    public static final RegistryObject<Item> GREEN_FLAMING_POLISHED_STEEL_SLAB =
            ItemsPart05.GREEN_FLAMING_POLISHED_STEEL_SLAB;
    public static final RegistryObject<Item> GREEN_FLAMING_POLISHED_STEEL_VERTICAL_SLAB =
            ItemsPart05.GREEN_FLAMING_POLISHED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> GREEN_FLAMING_POLISHED_STEEL_STAIRS =
            ItemsPart05.GREEN_FLAMING_POLISHED_STEEL_STAIRS;
    public static final RegistryObject<Item> GREEN_FLAMING_POLISHED_STEEL_WALL =
            ItemsPart05.GREEN_FLAMING_POLISHED_STEEL_WALL;
    public static final RegistryObject<Item> GREEN_FLAMING_PRESSED_STEEL = ItemsPart05.GREEN_FLAMING_PRESSED_STEEL;
    public static final RegistryObject<Item> GREEN_FLAMING_PRESSED_STEEL_SLAB =
            ItemsPart05.GREEN_FLAMING_PRESSED_STEEL_SLAB;
    public static final RegistryObject<Item> GREEN_FLAMING_PRESSED_STEEL_VERTICAL_SLAB =
            ItemsPart05.GREEN_FLAMING_PRESSED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> GREEN_FLAMING_PRESSED_STEEL_STAIRS =
            ItemsPart05.GREEN_FLAMING_PRESSED_STEEL_STAIRS;
    public static final RegistryObject<Item> GREEN_FLAMING_PRESSED_STEEL_WALL =
            ItemsPart05.GREEN_FLAMING_PRESSED_STEEL_WALL;
    public static final RegistryObject<Item> GREEN_FLAMING_STEEL_CASING = ItemsPart05.GREEN_FLAMING_STEEL_CASING;
    public static final RegistryObject<Item> GREEN_FLAMING_STEEL_GRATE = ItemsPart05.GREEN_FLAMING_STEEL_GRATE;
    public static final RegistryObject<Item> GREEN_FLAMING_STEEL_PILLAR = ItemsPart05.GREEN_FLAMING_STEEL_PILLAR;
    public static final RegistryObject<Item> GREEN_FLAMING_BOLTED_STEEL_PILLAR =
            ItemsPart05.GREEN_FLAMING_BOLTED_STEEL_PILLAR;
    public static final RegistryObject<Item> RED_FLAMING_STEEL_BLOCK = ItemsPart05.RED_FLAMING_STEEL_BLOCK;
    public static final RegistryObject<Item> RED_FLAMING_STEEL_BLOCK_SLAB = ItemsPart05.RED_FLAMING_STEEL_BLOCK_SLAB;
    public static final RegistryObject<Item> RED_FLAMING_STEEL_BLOCK_VERTICAL_SLAB =
            ItemsPart05.RED_FLAMING_STEEL_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> RED_FLAMING_STEEL_BLOCK_STAIRS =
            ItemsPart05.RED_FLAMING_STEEL_BLOCK_STAIRS;
    public static final RegistryObject<Item> RED_FLAMING_STEEL_BLOCK_WALL = ItemsPart05.RED_FLAMING_STEEL_BLOCK_WALL;
    public static final RegistryObject<Item> RED_FLAMING_CUT_STEEL = ItemsPart05.RED_FLAMING_CUT_STEEL;
    public static final RegistryObject<Item> RED_FLAMING_CUT_STEEL_SLAB = ItemsPart05.RED_FLAMING_CUT_STEEL_SLAB;
    public static final RegistryObject<Item> RED_FLAMING_CUT_STEEL_VERTICAL_SLAB =
            ItemsPart05.RED_FLAMING_CUT_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> RED_FLAMING_CUT_STEEL_STAIRS = ItemsPart05.RED_FLAMING_CUT_STEEL_STAIRS;
    public static final RegistryObject<Item> RED_FLAMING_CUT_STEEL_WALL = ItemsPart05.RED_FLAMING_CUT_STEEL_WALL;
    public static final RegistryObject<Item> RED_FLAMING_POLISHED_STEEL = ItemsPart05.RED_FLAMING_POLISHED_STEEL;
    public static final RegistryObject<Item> RED_FLAMING_POLISHED_STEEL_SLAB =
            ItemsPart05.RED_FLAMING_POLISHED_STEEL_SLAB;
    public static final RegistryObject<Item> RED_FLAMING_POLISHED_STEEL_VERTICAL_SLAB =
            ItemsPart05.RED_FLAMING_POLISHED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> RED_FLAMING_POLISHED_STEEL_STAIRS =
            ItemsPart05.RED_FLAMING_POLISHED_STEEL_STAIRS;
    public static final RegistryObject<Item> RED_FLAMING_POLISHED_STEEL_WALL =
            ItemsPart05.RED_FLAMING_POLISHED_STEEL_WALL;
    public static final RegistryObject<Item> RED_FLAMING_PRESSED_STEEL = ItemsPart05.RED_FLAMING_PRESSED_STEEL;
    public static final RegistryObject<Item> RED_FLAMING_PRESSED_STEEL_SLAB =
            ItemsPart05.RED_FLAMING_PRESSED_STEEL_SLAB;
    public static final RegistryObject<Item> RED_FLAMING_PRESSED_STEEL_VERTICAL_SLAB =
            ItemsPart05.RED_FLAMING_PRESSED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> RED_FLAMING_PRESSED_STEEL_STAIRS =
            ItemsPart05.RED_FLAMING_PRESSED_STEEL_STAIRS;
    public static final RegistryObject<Item> RED_FLAMING_PRESSED_STEEL_WALL =
            ItemsPart05.RED_FLAMING_PRESSED_STEEL_WALL;
    public static final RegistryObject<Item> RED_FLAMING_STEEL_CASING = ItemsPart05.RED_FLAMING_STEEL_CASING;
    public static final RegistryObject<Item> RED_FLAMING_STEEL_GRATE = ItemsPart05.RED_FLAMING_STEEL_GRATE;
    public static final RegistryObject<Item> RED_FLAMING_STEEL_PILLAR = ItemsPart05.RED_FLAMING_STEEL_PILLAR;
    public static final RegistryObject<Item> RED_FLAMING_BOLTED_STEEL_PILLAR =
            ItemsPart05.RED_FLAMING_BOLTED_STEEL_PILLAR;
    public static final RegistryObject<Item> BLACK_FLAMING_STEEL_BLOCK = ItemsPart05.BLACK_FLAMING_STEEL_BLOCK;
    public static final RegistryObject<Item> BLACK_FLAMING_STEEL_BLOCK_SLAB =
            ItemsPart05.BLACK_FLAMING_STEEL_BLOCK_SLAB;
    public static final RegistryObject<Item> BLACK_FLAMING_STEEL_BLOCK_VERTICAL_SLAB =
            ItemsPart05.BLACK_FLAMING_STEEL_BLOCK_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLACK_FLAMING_STEEL_BLOCK_STAIRS =
            ItemsPart05.BLACK_FLAMING_STEEL_BLOCK_STAIRS;
    public static final RegistryObject<Item> BLACK_FLAMING_STEEL_BLOCK_WALL =
            ItemsPart05.BLACK_FLAMING_STEEL_BLOCK_WALL;
    public static final RegistryObject<Item> BLACK_FLAMING_CUT_STEEL = ItemsPart05.BLACK_FLAMING_CUT_STEEL;
    public static final RegistryObject<Item> BLACK_FLAMING_CUT_STEEL_SLAB = ItemsPart05.BLACK_FLAMING_CUT_STEEL_SLAB;
    public static final RegistryObject<Item> BLACK_FLAMING_CUT_STEEL_VERTICAL_SLAB =
            ItemsPart05.BLACK_FLAMING_CUT_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLACK_FLAMING_CUT_STEEL_STAIRS =
            ItemsPart05.BLACK_FLAMING_CUT_STEEL_STAIRS;
    public static final RegistryObject<Item> BLACK_FLAMING_CUT_STEEL_WALL = ItemsPart05.BLACK_FLAMING_CUT_STEEL_WALL;
    public static final RegistryObject<Item> BLACK_FLAMING_POLISHED_STEEL = ItemsPart05.BLACK_FLAMING_POLISHED_STEEL;
    public static final RegistryObject<Item> BLACK_FLAMING_POLISHED_STEEL_SLAB =
            ItemsPart05.BLACK_FLAMING_POLISHED_STEEL_SLAB;
    public static final RegistryObject<Item> BLACK_FLAMING_POLISHED_STEEL_VERTICAL_SLAB =
            ItemsPart05.BLACK_FLAMING_POLISHED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLACK_FLAMING_POLISHED_STEEL_STAIRS =
            ItemsPart05.BLACK_FLAMING_POLISHED_STEEL_STAIRS;
    public static final RegistryObject<Item> BLACK_FLAMING_POLISHED_STEEL_WALL =
            ItemsPart05.BLACK_FLAMING_POLISHED_STEEL_WALL;
    public static final RegistryObject<Item> BLACK_FLAMING_PRESSED_STEEL = ItemsPart05.BLACK_FLAMING_PRESSED_STEEL;
    public static final RegistryObject<Item> BLACK_FLAMING_PRESSED_STEEL_SLAB =
            ItemsPart05.BLACK_FLAMING_PRESSED_STEEL_SLAB;
    public static final RegistryObject<Item> BLACK_FLAMING_PRESSED_STEEL_VERTICAL_SLAB =
            ItemsPart05.BLACK_FLAMING_PRESSED_STEEL_VERTICAL_SLAB;
    public static final RegistryObject<Item> BLACK_FLAMING_PRESSED_STEEL_STAIRS =
            ItemsPart05.BLACK_FLAMING_PRESSED_STEEL_STAIRS;
    public static final RegistryObject<Item> BLACK_FLAMING_PRESSED_STEEL_WALL =
            ItemsPart05.BLACK_FLAMING_PRESSED_STEEL_WALL;
    public static final RegistryObject<Item> BLACK_FLAMING_STEEL_CASING = ItemsPart05.BLACK_FLAMING_STEEL_CASING;
    public static final RegistryObject<Item> BLACK_FLAMING_STEEL_GRATE = ItemsPart05.BLACK_FLAMING_STEEL_GRATE;
    public static final RegistryObject<Item> BLACK_FLAMING_STEEL_PILLAR = ItemsPart05.BLACK_FLAMING_STEEL_PILLAR;
    public static final RegistryObject<Item> BLACK_FLAMING_BOLTED_STEEL_PILLAR =
            ItemsPart05.BLACK_FLAMING_BOLTED_STEEL_PILLAR;
    public static final RegistryObject<Item> HOLLOW_OAK_LOG = ItemsPart05.HOLLOW_OAK_LOG;
    public static final RegistryObject<Item> STRIPPED_HOLLOW_OAK_LOG = ItemsPart05.STRIPPED_HOLLOW_OAK_LOG;
    public static final RegistryObject<Item> HOLLOW_SPRUCE_LOG = ItemsPart05.HOLLOW_SPRUCE_LOG;
    public static final RegistryObject<Item> STRIPPED_HOLLOW_SPRUCE_LOG = ItemsPart05.STRIPPED_HOLLOW_SPRUCE_LOG;
    public static final RegistryObject<Item> HOLLOW_BIRCH_LOG = ItemsPart05.HOLLOW_BIRCH_LOG;
    public static final RegistryObject<Item> STRIPPED_HOLLOW_BIRCH_LOG = ItemsPart05.STRIPPED_HOLLOW_BIRCH_LOG;
    public static final RegistryObject<Item> HOLLOW_JUNGLE_LOG = ItemsPart05.HOLLOW_JUNGLE_LOG;
    public static final RegistryObject<Item> STRIPPED_HOLLOW_JUNGLE_LOG = ItemsPart05.STRIPPED_HOLLOW_JUNGLE_LOG;
    public static final RegistryObject<Item> HOLLOW_ACACIA_LOG = ItemsPart05.HOLLOW_ACACIA_LOG;
    public static final RegistryObject<Item> STRIPPED_HOLLOW_ACACIA_LOG = ItemsPart05.STRIPPED_HOLLOW_ACACIA_LOG;
    public static final RegistryObject<Item> HOLLOW_DARK_OAK_LOG = ItemsPart05.HOLLOW_DARK_OAK_LOG;
    public static final RegistryObject<Item> STRIPPED_HOLLOW_DARK_OAK_LOG = ItemsPart05.STRIPPED_HOLLOW_DARK_OAK_LOG;
    public static final RegistryObject<Item> HOLLOW_CRIMSON_STEM = ItemsPart05.HOLLOW_CRIMSON_STEM;
    public static final RegistryObject<Item> STRIPPED_HOLLOW_CRIMSON_STEM = ItemsPart05.STRIPPED_HOLLOW_CRIMSON_STEM;
    public static final RegistryObject<Item> HOLLOW_WARPED_STEM = ItemsPart05.HOLLOW_WARPED_STEM;
    public static final RegistryObject<Item> STRIPPED_HOLLOW_WARPED_STEM = ItemsPart05.STRIPPED_HOLLOW_WARPED_STEM;
    public static final RegistryObject<Item> HOLLOW_MANGROVE_LOG = ItemsPart05.HOLLOW_MANGROVE_LOG;
    public static final RegistryObject<Item> STRIPPED_HOLLOW_MANGROVE_LOG = ItemsPart05.STRIPPED_HOLLOW_MANGROVE_LOG;
    public static final RegistryObject<Item> HOLLOW_ASHPEN_LOG = ItemsPart05.HOLLOW_ASHPEN_LOG;
    public static final RegistryObject<Item> STRIPPED_HOLLOW_ASHPEN_LOG = ItemsPart05.STRIPPED_HOLLOW_ASHPEN_LOG;
    public static final RegistryObject<Item> HOLLOW_POPLAR_LOG = ItemsPart05.HOLLOW_POPLAR_LOG;
    public static final RegistryObject<Item> STRIPPED_HOLLOW_POPLAR_LOG = ItemsPart05.STRIPPED_HOLLOW_POPLAR_LOG;
    public static final RegistryObject<Item> HOLLOW_PALE_OAK_LOG = ItemsPart05.HOLLOW_PALE_OAK_LOG;
    public static final RegistryObject<Item> STRIPPED_HOLLOW_PALE_OAK_LOG = ItemsPart05.STRIPPED_HOLLOW_PALE_OAK_LOG;
    public static final RegistryObject<Item> HOLLOW_CHERRY_LOG = ItemsPart05.HOLLOW_CHERRY_LOG;
    public static final RegistryObject<Item> STRIPPED_HOLLOW_CHERRY_LOG = ItemsPart05.STRIPPED_HOLLOW_CHERRY_LOG;
    public static final RegistryObject<Item> CARDBOARD_BLOCK = ItemsPart05.CARDBOARD_BLOCK;
    public static final RegistryObject<Item> CARDBOARD_SLAB = ItemsPart05.CARDBOARD_SLAB;
    public static final RegistryObject<Item> CARDBOARD_STAIRS = ItemsPart05.CARDBOARD_STAIRS;
    public static final RegistryObject<Item> CARDBOARD_WALL = ItemsPart05.CARDBOARD_WALL;
    public static final RegistryObject<Item> CARDBOARD_VERTICAL_SLAB = ItemsPart05.CARDBOARD_VERTICAL_SLAB;
    public static final RegistryObject<Item> CARDBOARD_TRAPDOOR = ItemsPart05.CARDBOARD_TRAPDOOR;
    public static final RegistryObject<Item> CARDBOARD_BUTTON = ItemsPart05.CARDBOARD_BUTTON;
    public static final RegistryObject<Item> CARDBOARD_PRESSURE_PLATE = ItemsPart05.CARDBOARD_PRESSURE_PLATE;
    public static final RegistryObject<Item> SMOOTH_CARDBOARD_BLOCK = ItemsPart05.SMOOTH_CARDBOARD_BLOCK;
    public static final RegistryObject<Item> SMOOTH_CARDBOARD_SLAB = ItemsPart05.SMOOTH_CARDBOARD_SLAB;
    public static final RegistryObject<Item> SMOOTH_CARDBOARD_STAIRS = ItemsPart05.SMOOTH_CARDBOARD_STAIRS;
    public static final RegistryObject<Item> SMOOTH_CARDBOARD_WALL = ItemsPart05.SMOOTH_CARDBOARD_WALL;
    public static final RegistryObject<Item> SMOOTH_CARDBOARD_VERTICAL_SLAB =
            ItemsPart05.SMOOTH_CARDBOARD_VERTICAL_SLAB;
    public static final RegistryObject<Item> BUNDLED_CARDBOARD = ItemsPart05.BUNDLED_CARDBOARD;
    public static final RegistryObject<Item> BUNDLED_CARDBOARD_SLAB = ItemsPart05.BUNDLED_CARDBOARD_SLAB;
    public static final RegistryObject<Item> BUNDLED_CARDBOARD_STAIRS = ItemsPart05.BUNDLED_CARDBOARD_STAIRS;
    public static final RegistryObject<Item> BUNDLED_CARDBOARD_WALL = ItemsPart05.BUNDLED_CARDBOARD_WALL;
    public static final RegistryObject<Item> BUNDLED_CARDBOARD_VERTICAL_SLAB =
            ItemsPart05.BUNDLED_CARDBOARD_VERTICAL_SLAB;
    public static final RegistryObject<Item> PRESSED_CARDBOARD = ItemsPart05.PRESSED_CARDBOARD;
    public static final RegistryObject<Item> PRESSED_CARDBOARD_SLAB = ItemsPart05.PRESSED_CARDBOARD_SLAB;
    public static final RegistryObject<Item> PRESSED_CARDBOARD_STAIRS = ItemsPart05.PRESSED_CARDBOARD_STAIRS;
    public static final RegistryObject<Item> PRESSED_CARDBOARD_WALL = ItemsPart05.PRESSED_CARDBOARD_WALL;
    public static final RegistryObject<Item> PRESSED_CARDBOARD_VERTICAL_SLAB =
            ItemsPart05.PRESSED_CARDBOARD_VERTICAL_SLAB;
    public static final RegistryObject<Item> THICK_CARDBOARD_BLOCK = ItemsPart05.THICK_CARDBOARD_BLOCK;
    public static final RegistryObject<Item> THICK_CARDBOARD_SLAB = ItemsPart05.THICK_CARDBOARD_SLAB;
    public static final RegistryObject<Item> THICK_CARDBOARD_STAIRS = ItemsPart05.THICK_CARDBOARD_STAIRS;
    public static final RegistryObject<Item> THICK_CARDBOARD_WALL = ItemsPart05.THICK_CARDBOARD_WALL;
    public static final RegistryObject<Item> THICK_CARDBOARD_VERTICAL_SLAB = ItemsPart05.THICK_CARDBOARD_VERTICAL_SLAB;
    public static final RegistryObject<Item> STRIPPED_CARDBOARD_BLOCK = ItemsPart05.STRIPPED_CARDBOARD_BLOCK;
    public static final RegistryObject<Item> STRIPPED_CARDBOARD_SLAB = ItemsPart05.STRIPPED_CARDBOARD_SLAB;
    public static final RegistryObject<Item> STRIPPED_CARDBOARD_STAIRS = ItemsPart05.STRIPPED_CARDBOARD_STAIRS;
    public static final RegistryObject<Item> STRIPPED_CARDBOARD_WALL = ItemsPart05.STRIPPED_CARDBOARD_WALL;
    public static final RegistryObject<Item> STRIPPED_CARDBOARD_VERTICAL_SLAB =
            ItemsPart05.STRIPPED_CARDBOARD_VERTICAL_SLAB;
    public static final RegistryObject<Item> STRIPPED_CARDBOARD_TRAPDOOR = ItemsPart05.STRIPPED_CARDBOARD_TRAPDOOR;
    public static final RegistryObject<Item> STRIPPED_CARDBOARD_BUTTON = ItemsPart05.STRIPPED_CARDBOARD_BUTTON;
    public static final RegistryObject<Item> STRIPPED_CARDBOARD_PRESSURE_PLATE =
            ItemsPart05.STRIPPED_CARDBOARD_PRESSURE_PLATE;
    public static final RegistryObject<Item> STRIPPED_SMOOTH_CARDBOARD_BLOCK =
            ItemsPart05.STRIPPED_SMOOTH_CARDBOARD_BLOCK;
    public static final RegistryObject<Item> STRIPPED_SMOOTH_CARDBOARD_SLAB =
            ItemsPart05.STRIPPED_SMOOTH_CARDBOARD_SLAB;
    public static final RegistryObject<Item> STRIPPED_SMOOTH_CARDBOARD_STAIRS =
            ItemsPart05.STRIPPED_SMOOTH_CARDBOARD_STAIRS;
    public static final RegistryObject<Item> STRIPPED_SMOOTH_CARDBOARD_WALL =
            ItemsPart05.STRIPPED_SMOOTH_CARDBOARD_WALL;
    public static final RegistryObject<Item> STRIPPED_SMOOTH_CARDBOARD_VERTICAL_SLAB =
            ItemsPart05.STRIPPED_SMOOTH_CARDBOARD_VERTICAL_SLAB;
    public static final RegistryObject<Item> STRIPPED_BUNDLED_CARDBOARD = ItemsPart05.STRIPPED_BUNDLED_CARDBOARD;
    public static final RegistryObject<Item> STRIPPED_BUNDLED_CARDBOARD_SLAB =
            ItemsPart05.STRIPPED_BUNDLED_CARDBOARD_SLAB;
    public static final RegistryObject<Item> STRIPPED_BUNDLED_CARDBOARD_STAIRS =
            ItemsPart05.STRIPPED_BUNDLED_CARDBOARD_STAIRS;
    public static final RegistryObject<Item> STRIPPED_BUNDLED_CARDBOARD_WALL =
            ItemsPart05.STRIPPED_BUNDLED_CARDBOARD_WALL;
    public static final RegistryObject<Item> STRIPPED_BUNDLED_CARDBOARD_VERTICAL_SLAB =
            ItemsPart05.STRIPPED_BUNDLED_CARDBOARD_VERTICAL_SLAB;
    public static final RegistryObject<Item> STRIPPED_PRESSED_CARDBOARD = ItemsPart05.STRIPPED_PRESSED_CARDBOARD;
    public static final RegistryObject<Item> STRIPPED_PRESSED_CARDBOARD_SLAB =
            ItemsPart05.STRIPPED_PRESSED_CARDBOARD_SLAB;
    public static final RegistryObject<Item> STRIPPED_PRESSED_CARDBOARD_STAIRS =
            ItemsPart05.STRIPPED_PRESSED_CARDBOARD_STAIRS;
    public static final RegistryObject<Item> STRIPPED_PRESSED_CARDBOARD_WALL =
            ItemsPart05.STRIPPED_PRESSED_CARDBOARD_WALL;
    public static final RegistryObject<Item> STRIPPED_PRESSED_CARDBOARD_VERTICAL_SLAB =
            ItemsPart05.STRIPPED_PRESSED_CARDBOARD_VERTICAL_SLAB;
    public static final RegistryObject<Item> STRIPPED_THICK_CARDBOARD_BLOCK =
            ItemsPart05.STRIPPED_THICK_CARDBOARD_BLOCK;
    public static final RegistryObject<Item> STRIPPED_THICK_CARDBOARD_SLAB = ItemsPart05.STRIPPED_THICK_CARDBOARD_SLAB;
    public static final RegistryObject<Item> STRIPPED_THICK_CARDBOARD_STAIRS =
            ItemsPart05.STRIPPED_THICK_CARDBOARD_STAIRS;
    public static final RegistryObject<Item> STRIPPED_THICK_CARDBOARD_WALL = ItemsPart05.STRIPPED_THICK_CARDBOARD_WALL;
    public static final RegistryObject<Item> STRIPPED_THICK_CARDBOARD_VERTICAL_SLAB =
            ItemsPart05.STRIPPED_THICK_CARDBOARD_VERTICAL_SLAB;
    public static final RegistryObject<Item> TINTED_CARDBOARD_BLOCK = ItemsPart05.TINTED_CARDBOARD_BLOCK;
    public static final RegistryObject<Item> TINTED_CARDBOARD_SLAB = ItemsPart05.TINTED_CARDBOARD_SLAB;
    public static final RegistryObject<Item> TINTED_CARDBOARD_STAIRS = ItemsPart05.TINTED_CARDBOARD_STAIRS;
    public static final RegistryObject<Item> TINTED_CARDBOARD_WALL = ItemsPart05.TINTED_CARDBOARD_WALL;
    public static final RegistryObject<Item> TINTED_CARDBOARD_VERTICAL_SLAB =
            ItemsPart05.TINTED_CARDBOARD_VERTICAL_SLAB;
    public static final RegistryObject<Item> TINTED_CARDBOARD_TRAPDOOR = ItemsPart05.TINTED_CARDBOARD_TRAPDOOR;
    public static final RegistryObject<Item> TINTED_CARDBOARD_BUTTON = ItemsPart05.TINTED_CARDBOARD_BUTTON;
    public static final RegistryObject<Item> TINTED_CARDBOARD_PRESSURE_PLATE =
            ItemsPart05.TINTED_CARDBOARD_PRESSURE_PLATE;
    public static final RegistryObject<Item> TINTED_SMOOTH_CARDBOARD_BLOCK = ItemsPart05.TINTED_SMOOTH_CARDBOARD_BLOCK;
    public static final RegistryObject<Item> TINTED_SMOOTH_CARDBOARD_SLAB = ItemsPart05.TINTED_SMOOTH_CARDBOARD_SLAB;
    public static final RegistryObject<Item> TINTED_SMOOTH_CARDBOARD_STAIRS =
            ItemsPart05.TINTED_SMOOTH_CARDBOARD_STAIRS;
    public static final RegistryObject<Item> TINTED_SMOOTH_CARDBOARD_WALL = ItemsPart05.TINTED_SMOOTH_CARDBOARD_WALL;
    public static final RegistryObject<Item> TINTED_SMOOTH_CARDBOARD_VERTICAL_SLAB =
            ItemsPart05.TINTED_SMOOTH_CARDBOARD_VERTICAL_SLAB;
    public static final RegistryObject<Item> TINTED_BUNDLED_CARDBOARD = ItemsPart05.TINTED_BUNDLED_CARDBOARD;
    public static final RegistryObject<Item> TINTED_BUNDLED_CARDBOARD_SLAB = ItemsPart05.TINTED_BUNDLED_CARDBOARD_SLAB;
    public static final RegistryObject<Item> TINTED_BUNDLED_CARDBOARD_STAIRS =
            ItemsPart05.TINTED_BUNDLED_CARDBOARD_STAIRS;
    public static final RegistryObject<Item> TINTED_BUNDLED_CARDBOARD_WALL = ItemsPart05.TINTED_BUNDLED_CARDBOARD_WALL;
    public static final RegistryObject<Item> TINTED_BUNDLED_CARDBOARD_VERTICAL_SLAB =
            ItemsPart05.TINTED_BUNDLED_CARDBOARD_VERTICAL_SLAB;
    public static final RegistryObject<Item> TINTED_PRESSED_CARDBOARD = ItemsPart05.TINTED_PRESSED_CARDBOARD;
    public static final RegistryObject<Item> TINTED_PRESSED_CARDBOARD_SLAB = ItemsPart05.TINTED_PRESSED_CARDBOARD_SLAB;
    public static final RegistryObject<Item> TINTED_PRESSED_CARDBOARD_STAIRS =
            ItemsPart05.TINTED_PRESSED_CARDBOARD_STAIRS;
    public static final RegistryObject<Item> TINTED_PRESSED_CARDBOARD_WALL = ItemsPart05.TINTED_PRESSED_CARDBOARD_WALL;
    public static final RegistryObject<Item> TINTED_PRESSED_CARDBOARD_VERTICAL_SLAB =
            ItemsPart05.TINTED_PRESSED_CARDBOARD_VERTICAL_SLAB;
    public static final RegistryObject<Item> TINTED_THICK_CARDBOARD_BLOCK = ItemsPart05.TINTED_THICK_CARDBOARD_BLOCK;
    public static final RegistryObject<Item> TINTED_THICK_CARDBOARD_SLAB = ItemsPart05.TINTED_THICK_CARDBOARD_SLAB;
    public static final RegistryObject<Item> TINTED_THICK_CARDBOARD_STAIRS = ItemsPart05.TINTED_THICK_CARDBOARD_STAIRS;
    public static final RegistryObject<Item> TINTED_THICK_CARDBOARD_WALL = ItemsPart05.TINTED_THICK_CARDBOARD_WALL;
    public static final RegistryObject<Item> TINTED_THICK_CARDBOARD_VERTICAL_SLAB =
            ItemsPart05.TINTED_THICK_CARDBOARD_VERTICAL_SLAB;
    public static final RegistryObject<Item> WASHED_CARDBOARD_BLOCK = ItemsPart05.WASHED_CARDBOARD_BLOCK;
    public static final RegistryObject<Item> WASHED_CARDBOARD_SLAB = ItemsPart05.WASHED_CARDBOARD_SLAB;
    public static final RegistryObject<Item> WASHED_CARDBOARD_STAIRS = ItemsPart05.WASHED_CARDBOARD_STAIRS;
    public static final RegistryObject<Item> WASHED_CARDBOARD_WALL = ItemsPart05.WASHED_CARDBOARD_WALL;
    public static final RegistryObject<Item> WASHED_CARDBOARD_VERTICAL_SLAB =
            ItemsPart05.WASHED_CARDBOARD_VERTICAL_SLAB;
    public static final RegistryObject<Item> WASHED_CARDBOARD_TRAPDOOR = ItemsPart05.WASHED_CARDBOARD_TRAPDOOR;
    public static final RegistryObject<Item> WASHED_CARDBOARD_BUTTON = ItemsPart05.WASHED_CARDBOARD_BUTTON;
    public static final RegistryObject<Item> WASHED_CARDBOARD_PRESSURE_PLATE =
            ItemsPart05.WASHED_CARDBOARD_PRESSURE_PLATE;
    public static final RegistryObject<Item> WASHED_SMOOTH_CARDBOARD_BLOCK = ItemsPart05.WASHED_SMOOTH_CARDBOARD_BLOCK;
    public static final RegistryObject<Item> WASHED_SMOOTH_CARDBOARD_SLAB = ItemsPart05.WASHED_SMOOTH_CARDBOARD_SLAB;
    public static final RegistryObject<Item> WASHED_SMOOTH_CARDBOARD_STAIRS =
            ItemsPart05.WASHED_SMOOTH_CARDBOARD_STAIRS;
    public static final RegistryObject<Item> WASHED_SMOOTH_CARDBOARD_WALL = ItemsPart05.WASHED_SMOOTH_CARDBOARD_WALL;
    public static final RegistryObject<Item> WASHED_SMOOTH_CARDBOARD_VERTICAL_SLAB =
            ItemsPart05.WASHED_SMOOTH_CARDBOARD_VERTICAL_SLAB;
    public static final RegistryObject<Item> WASHED_BUNDLED_CARDBOARD = ItemsPart05.WASHED_BUNDLED_CARDBOARD;
    public static final RegistryObject<Item> WASHED_BUNDLED_CARDBOARD_SLAB = ItemsPart05.WASHED_BUNDLED_CARDBOARD_SLAB;
    public static final RegistryObject<Item> WASHED_BUNDLED_CARDBOARD_STAIRS =
            ItemsPart05.WASHED_BUNDLED_CARDBOARD_STAIRS;
    public static final RegistryObject<Item> WASHED_BUNDLED_CARDBOARD_WALL = ItemsPart05.WASHED_BUNDLED_CARDBOARD_WALL;
    public static final RegistryObject<Item> WASHED_BUNDLED_CARDBOARD_VERTICAL_SLAB =
            ItemsPart05.WASHED_BUNDLED_CARDBOARD_VERTICAL_SLAB;
    public static final RegistryObject<Item> WASHED_PRESSED_CARDBOARD = ItemsPart05.WASHED_PRESSED_CARDBOARD;
    public static final RegistryObject<Item> WASHED_PRESSED_CARDBOARD_SLAB = ItemsPart05.WASHED_PRESSED_CARDBOARD_SLAB;
    public static final RegistryObject<Item> WASHED_PRESSED_CARDBOARD_STAIRS =
            ItemsPart05.WASHED_PRESSED_CARDBOARD_STAIRS;
    public static final RegistryObject<Item> WASHED_PRESSED_CARDBOARD_WALL = ItemsPart05.WASHED_PRESSED_CARDBOARD_WALL;
    public static final RegistryObject<Item> WASHED_PRESSED_CARDBOARD_VERTICAL_SLAB =
            ItemsPart05.WASHED_PRESSED_CARDBOARD_VERTICAL_SLAB;
    public static final RegistryObject<Item> WASHED_THICK_CARDBOARD_BLOCK = ItemsPart05.WASHED_THICK_CARDBOARD_BLOCK;
    public static final RegistryObject<Item> WASHED_THICK_CARDBOARD_SLAB = ItemsPart05.WASHED_THICK_CARDBOARD_SLAB;
    public static final RegistryObject<Item> WASHED_THICK_CARDBOARD_STAIRS = ItemsPart05.WASHED_THICK_CARDBOARD_STAIRS;
    public static final RegistryObject<Item> WASHED_THICK_CARDBOARD_WALL = ItemsPart05.WASHED_THICK_CARDBOARD_WALL;
    public static final RegistryObject<Item> WASHED_THICK_CARDBOARD_VERTICAL_SLAB =
            ItemsPart05.WASHED_THICK_CARDBOARD_VERTICAL_SLAB;
    public static final RegistryObject<Item> BURNT_CARDBOARD_BLOCK = ItemsPart05.BURNT_CARDBOARD_BLOCK;
    public static final RegistryObject<Item> BURNT_CARDBOARD_SLAB = ItemsPart05.BURNT_CARDBOARD_SLAB;
    public static final RegistryObject<Item> BURNT_CARDBOARD_STAIRS = ItemsPart05.BURNT_CARDBOARD_STAIRS;
    public static final RegistryObject<Item> BURNT_CARDBOARD_WALL = ItemsPart05.BURNT_CARDBOARD_WALL;
    public static final RegistryObject<Item> BURNT_CARDBOARD_VERTICAL_SLAB = ItemsPart05.BURNT_CARDBOARD_VERTICAL_SLAB;
    public static final RegistryObject<Item> BURNT_CARDBOARD_TRAPDOOR = ItemsPart05.BURNT_CARDBOARD_TRAPDOOR;
    public static final RegistryObject<Item> BURNT_CARDBOARD_BUTTON = ItemsPart05.BURNT_CARDBOARD_BUTTON;
    public static final RegistryObject<Item> BURNT_CARDBOARD_PRESSURE_PLATE =
            ItemsPart05.BURNT_CARDBOARD_PRESSURE_PLATE;
    public static final RegistryObject<Item> BURNT_SMOOTH_CARDBOARD_BLOCK = ItemsPart05.BURNT_SMOOTH_CARDBOARD_BLOCK;
    public static final RegistryObject<Item> BURNT_SMOOTH_CARDBOARD_SLAB = ItemsPart05.BURNT_SMOOTH_CARDBOARD_SLAB;
    public static final RegistryObject<Item> BURNT_SMOOTH_CARDBOARD_STAIRS = ItemsPart05.BURNT_SMOOTH_CARDBOARD_STAIRS;
    public static final RegistryObject<Item> BURNT_SMOOTH_CARDBOARD_WALL = ItemsPart05.BURNT_SMOOTH_CARDBOARD_WALL;
    public static final RegistryObject<Item> BURNT_SMOOTH_CARDBOARD_VERTICAL_SLAB =
            ItemsPart05.BURNT_SMOOTH_CARDBOARD_VERTICAL_SLAB;
    public static final RegistryObject<Item> BURNT_BUNDLED_CARDBOARD = ItemsPart05.BURNT_BUNDLED_CARDBOARD;
    public static final RegistryObject<Item> BURNT_BUNDLED_CARDBOARD_SLAB = ItemsPart05.BURNT_BUNDLED_CARDBOARD_SLAB;
    public static final RegistryObject<Item> BURNT_BUNDLED_CARDBOARD_STAIRS =
            ItemsPart05.BURNT_BUNDLED_CARDBOARD_STAIRS;
    public static final RegistryObject<Item> BURNT_BUNDLED_CARDBOARD_WALL = ItemsPart05.BURNT_BUNDLED_CARDBOARD_WALL;
    public static final RegistryObject<Item> BURNT_BUNDLED_CARDBOARD_VERTICAL_SLAB =
            ItemsPart05.BURNT_BUNDLED_CARDBOARD_VERTICAL_SLAB;
    public static final RegistryObject<Item> BURNT_PRESSED_CARDBOARD = ItemsPart05.BURNT_PRESSED_CARDBOARD;
    public static final RegistryObject<Item> BURNT_PRESSED_CARDBOARD_SLAB = ItemsPart05.BURNT_PRESSED_CARDBOARD_SLAB;
    public static final RegistryObject<Item> BURNT_PRESSED_CARDBOARD_STAIRS =
            ItemsPart05.BURNT_PRESSED_CARDBOARD_STAIRS;
    public static final RegistryObject<Item> BURNT_PRESSED_CARDBOARD_WALL = ItemsPart05.BURNT_PRESSED_CARDBOARD_WALL;
    public static final RegistryObject<Item> BURNT_PRESSED_CARDBOARD_VERTICAL_SLAB =
            ItemsPart05.BURNT_PRESSED_CARDBOARD_VERTICAL_SLAB;
    public static final RegistryObject<Item> BURNT_THICK_CARDBOARD_BLOCK = ItemsPart05.BURNT_THICK_CARDBOARD_BLOCK;
    public static final RegistryObject<Item> BURNT_THICK_CARDBOARD_SLAB = ItemsPart05.BURNT_THICK_CARDBOARD_SLAB;
    public static final RegistryObject<Item> BURNT_THICK_CARDBOARD_STAIRS = ItemsPart05.BURNT_THICK_CARDBOARD_STAIRS;
    public static final RegistryObject<Item> BURNT_THICK_CARDBOARD_WALL = ItemsPart05.BURNT_THICK_CARDBOARD_WALL;
    public static final RegistryObject<Item> BURNT_THICK_CARDBOARD_VERTICAL_SLAB =
            ItemsPart05.BURNT_THICK_CARDBOARD_VERTICAL_SLAB;
    public static final RegistryObject<Item> OAK_SHELF = ItemsPart05.OAK_SHELF;
    public static final RegistryObject<Item> SPRUCE_SHELF = ItemsPart05.SPRUCE_SHELF;
    public static final RegistryObject<Item> BIRCH_SHELF = ItemsPart05.BIRCH_SHELF;
    public static final RegistryObject<Item> JUNGLE_SHELF = ItemsPart05.JUNGLE_SHELF;
    public static final RegistryObject<Item> ACACIA_SHELF = ItemsPart05.ACACIA_SHELF;
    public static final RegistryObject<Item> DARK_OAK_SHELF = ItemsPart05.DARK_OAK_SHELF;
    public static final RegistryObject<Item> MANGROVE_SHELF = ItemsPart05.MANGROVE_SHELF;
    public static final RegistryObject<Item> CHERRY_SHELF = ItemsPart05.CHERRY_SHELF;
    public static final RegistryObject<Item> CRIMSON_SHELF = ItemsPart05.CRIMSON_SHELF;
    public static final RegistryObject<Item> WARPED_SHELF = ItemsPart05.WARPED_SHELF;
    public static final RegistryObject<Item> BAMBOO_SHELF = ItemsPart05.BAMBOO_SHELF;
    public static final RegistryObject<Item> STRIPPED_BAMBOO_SHELF = ItemsPart05.STRIPPED_BAMBOO_SHELF;
    public static final RegistryObject<Item> PALE_OAK_SHELF = ItemsPart05.PALE_OAK_SHELF;
    public static final RegistryObject<Item> POPLAR_SHELF = ItemsPart05.POPLAR_SHELF;
    public static final RegistryObject<Item> GOLDEN_JAR = ItemsPart05.GOLDEN_JAR;
    public static final RegistryObject<Item> FESTIVE_STAR = ItemsPart05.FESTIVE_STAR;
    public static final RegistryObject<Item> EXPERIENCE_BUCKET = ItemsPart05.EXPERIENCE_BUCKET;
    public static final RegistryObject<Item> FESTIVE_GLINT_SHARD = ItemsPart05.FESTIVE_GLINT_SHARD;
    public static final RegistryObject<Item> GOLDEN_JAR_PATTERN = ItemsPart05.GOLDEN_JAR_PATTERN;
    public static final RegistryObject<Item> FESTIVE_STAR_PATTERN = ItemsPart05.FESTIVE_STAR_PATTERN;
    public static final RegistryObject<Item> BIG_WHITE_ORNAMENT = ItemsPart05.BIG_WHITE_ORNAMENT;
    public static final RegistryObject<Item> BIG_ORANGE_ORNAMENT = ItemsPart05.BIG_ORANGE_ORNAMENT;
    public static final RegistryObject<Item> BIG_MAGENTA_ORNAMENT = ItemsPart05.BIG_MAGENTA_ORNAMENT;
    public static final RegistryObject<Item> BIG_LIGHT_BLUE_ORNAMENT = ItemsPart05.BIG_LIGHT_BLUE_ORNAMENT;
    public static final RegistryObject<Item> BIG_YELLOW_ORNAMENT = ItemsPart05.BIG_YELLOW_ORNAMENT;
    public static final RegistryObject<Item> BIG_LIME_ORNAMENT = ItemsPart05.BIG_LIME_ORNAMENT;
    public static final RegistryObject<Item> BIG_PINK_ORNAMENT = ItemsPart05.BIG_PINK_ORNAMENT;
    public static final RegistryObject<Item> BIG_GRAY_ORNAMENT = ItemsPart05.BIG_GRAY_ORNAMENT;
    public static final RegistryObject<Item> BIG_LIGHT_GRAY_ORNAMENT = ItemsPart05.BIG_LIGHT_GRAY_ORNAMENT;
    public static final RegistryObject<Item> BIG_CYAN_ORNAMENT = ItemsPart05.BIG_CYAN_ORNAMENT;
    public static final RegistryObject<Item> BIG_PURPLE_ORNAMENT = ItemsPart05.BIG_PURPLE_ORNAMENT;
    public static final RegistryObject<Item> BIG_BLUE_ORNAMENT = ItemsPart05.BIG_BLUE_ORNAMENT;
    public static final RegistryObject<Item> BIG_BROWN_ORNAMENT = ItemsPart05.BIG_BROWN_ORNAMENT;
    public static final RegistryObject<Item> BIG_GREEN_ORNAMENT = ItemsPart05.BIG_GREEN_ORNAMENT;
    public static final RegistryObject<Item> BIG_RED_ORNAMENT = ItemsPart05.BIG_RED_ORNAMENT;
    public static final RegistryObject<Item> BIG_BLACK_ORNAMENT = ItemsPart05.BIG_BLACK_ORNAMENT;
    public static final RegistryObject<Item> BIG_GLASS_ORNAMENT = ItemsPart05.BIG_GLASS_ORNAMENT;
    public static final RegistryObject<Item> BIG_TINTED_GLASS_ORNAMENT = ItemsPart05.BIG_TINTED_GLASS_ORNAMENT;
    public static final RegistryObject<Item> BIG_ORNAMENT_TEMPLATE = ItemsPart05.BIG_ORNAMENT_TEMPLATE;
    public static final RegistryObject<Item> STRINGLIGHT_FRAME = ItemsPart05.STRINGLIGHT_FRAME;
    public static final RegistryObject<Item> STRINGLIGHT_FRAME_PATTERN = ItemsPart05.STRINGLIGHT_FRAME_PATTERN;
    public static final RegistryObject<Item> MUSIC_DISC_CELEBRATION = ItemsPart05.MUSIC_DISC_CELEBRATION;
    public static final RegistryObject<Item> MUSIC_DISC_SNOWFALL = ItemsPart05.MUSIC_DISC_SNOWFALL;
    public static final RegistryObject<Item> MUSIC_DISC_BUILDER = ItemsPart05.MUSIC_DISC_BUILDER;
}
