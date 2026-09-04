package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.block.ModBlocks;
import com.kingodogo.buildscape.item.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(AdvancementWidget.class)
public abstract class AdvancementWidgetMixin {

    @Shadow
    @Final
    private Advancement advancement;

    @Shadow
    @Final
    private DisplayInfo display;

    @Unique
    private static final List<ItemStack> buildscape$STEEL_BLOCK_ICONS = new ArrayList<>();
    @Unique
    private static final List<ItemStack> buildscape$TILE_BLOCK_ICONS = new ArrayList<>();
    @Unique
    private static final List<ItemStack> buildscape$ASHPEN_BLOCK_ICONS = new ArrayList<>();
    @Unique
    private static final List<ItemStack> buildscape$BIT_COPPER_BLOCK_ICONS = new ArrayList<>();
    @Unique
    private static final List<ItemStack> buildscape$SPOOL_ICONS = new ArrayList<>();
    @Unique
    private static final List<ItemStack> buildscape$WALLPAPER_ICONS = new ArrayList<>();
    @Unique
    private static final List<ItemStack> buildscape$FROGLIGHT_ICONS = new ArrayList<>();
    @Unique
    private static final List<ItemStack> buildscape$CHRISTMAS_ICONS = new ArrayList<>();

    @Unique
    private static void buildscape$initIcons() {
        if (!buildscape$STEEL_BLOCK_ICONS.isEmpty()) return;

        // Steel blocks cycle (One More Block - 100 blocks, non-flaming full cubes)
        buildscape$STEEL_BLOCK_ICONS.add(new ItemStack(ModItems.STEEL_BLOCK.get()));
        buildscape$STEEL_BLOCK_ICONS.add(new ItemStack(ModItems.SCRAPED_STEEL.get()));
        buildscape$STEEL_BLOCK_ICONS.add(new ItemStack(ModItems.RUSTIC_SCRAPED_STEEL.get()));
        buildscape$STEEL_BLOCK_ICONS.add(new ItemStack(ModItems.STACKED_STEEL.get()));
        buildscape$STEEL_BLOCK_ICONS.add(new ItemStack(ModItems.STEEL_PANELS.get()));
        buildscape$STEEL_BLOCK_ICONS.add(new ItemStack(ModItems.CROSSED_STEEL_PANELS.get()));
        buildscape$STEEL_BLOCK_ICONS.add(new ItemStack(ModItems.STEEL_MESH_BLOCK.get()));
        buildscape$STEEL_BLOCK_ICONS.add(new ItemStack(ModItems.PRESSED_STEEL.get()));
        buildscape$STEEL_BLOCK_ICONS.add(new ItemStack(ModItems.CUT_STEEL.get()));
        buildscape$STEEL_BLOCK_ICONS.add(new ItemStack(ModItems.POLISHED_STEEL.get()));
        buildscape$STEEL_BLOCK_ICONS.add(new ItemStack(ModItems.FACTORY_STEEL_PANEL.get()));
        buildscape$STEEL_BLOCK_ICONS.add(new ItemStack(ModItems.STEEL_CASING.get()));
        buildscape$STEEL_BLOCK_ICONS.add(new ItemStack(ModItems.STEEL_TRIM.get()));
        buildscape$STEEL_BLOCK_ICONS.add(new ItemStack(ModItems.STEEL_PILLAR.get()));
        buildscape$STEEL_BLOCK_ICONS.add(new ItemStack(ModItems.BOLTED_STEEL_PILLAR.get()));
        buildscape$STEEL_BLOCK_ICONS.add(new ItemStack(ModItems.STEEL_FAN.get()));

        // Tile blocks cycle (Okay, One More - 1,000 blocks, full cubes)
        buildscape$TILE_BLOCK_ICONS.add(new ItemStack(ModItems.WHITE_TILES.get()));
        buildscape$TILE_BLOCK_ICONS.add(new ItemStack(ModItems.ORANGE_TILES.get()));
        buildscape$TILE_BLOCK_ICONS.add(new ItemStack(ModItems.MAGENTA_TILES.get()));
        buildscape$TILE_BLOCK_ICONS.add(new ItemStack(ModItems.LIGHT_BLUE_TILES.get()));
        buildscape$TILE_BLOCK_ICONS.add(new ItemStack(ModItems.YELLOW_TILES.get()));
        buildscape$TILE_BLOCK_ICONS.add(new ItemStack(ModItems.LIME_TILES.get()));
        buildscape$TILE_BLOCK_ICONS.add(new ItemStack(ModItems.PINK_TILES.get()));
        buildscape$TILE_BLOCK_ICONS.add(new ItemStack(ModItems.GRAY_TILES.get()));
        buildscape$TILE_BLOCK_ICONS.add(new ItemStack(ModItems.LIGHT_GRAY_TILES.get()));
        buildscape$TILE_BLOCK_ICONS.add(new ItemStack(ModItems.CYAN_TILES.get()));
        buildscape$TILE_BLOCK_ICONS.add(new ItemStack(ModItems.PURPLE_TILES.get()));
        buildscape$TILE_BLOCK_ICONS.add(new ItemStack(ModItems.BLUE_TILES.get()));
        buildscape$TILE_BLOCK_ICONS.add(new ItemStack(ModItems.BROWN_TILES.get()));
        buildscape$TILE_BLOCK_ICONS.add(new ItemStack(ModItems.GREEN_TILES.get()));
        buildscape$TILE_BLOCK_ICONS.add(new ItemStack(ModItems.RED_TILES.get()));
        buildscape$TILE_BLOCK_ICONS.add(new ItemStack(ModItems.BLACK_TILES.get()));

        // Ashpen blocks cycle (Actually, One Last - 10,000 blocks, full cubes)
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(ModItems.ASHPEN_WHITE_PLANKS.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(ModItems.ORANGE_ASHPEN_PLANKS.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(ModItems.MAGENTA_ASHPEN_PLANKS.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(ModItems.LIGHT_BLUE_ASHPEN_PLANKS.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(ModItems.YELLOW_ASHPEN_PLANKS.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(ModItems.LIME_ASHPEN_PLANKS.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(ModItems.PINK_ASHPEN_PLANKS.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(ModItems.GRAY_ASHPEN_PLANKS.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(ModItems.LIGHT_GRAY_ASHPEN_PLANKS.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(ModItems.CYAN_ASHPEN_PLANKS.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(ModItems.PURPLE_ASHPEN_PLANKS.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(ModItems.BLUE_ASHPEN_PLANKS.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(ModItems.BROWN_ASHPEN_PLANKS.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(ModItems.GREEN_ASHPEN_PLANKS.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(ModItems.RED_ASHPEN_PLANKS.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(ModItems.BLACK_ASHPEN_PLANKS.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(ModItems.ASHPEN_LOG.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(ModItems.STRIPPED_ASHPEN_LOG.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(ModItems.ASHPEN_WOOD.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(ModItems.STRIPPED_ASHPEN_WOOD.get()));

        // Bit Copper blocks cycle (One Last one, i promise - 100,000 blocks, full cubes)
        buildscape$BIT_COPPER_BLOCK_ICONS.add(new ItemStack(ModItems.BIT_COPPER_BLOCK.get()));
        buildscape$BIT_COPPER_BLOCK_ICONS.add(new ItemStack(ModItems.BIT_CUT_COPPER.get()));
        buildscape$BIT_COPPER_BLOCK_ICONS.add(new ItemStack(ModItems.BIT_CHISELED_COPPER.get()));
        buildscape$BIT_COPPER_BLOCK_ICONS.add(new ItemStack(ModItems.BIT_EXPOSED_COPPER_BLOCK.get()));
        buildscape$BIT_COPPER_BLOCK_ICONS.add(new ItemStack(ModItems.BIT_EXPOSED_CUT_COPPER.get()));
        buildscape$BIT_COPPER_BLOCK_ICONS.add(new ItemStack(ModItems.BIT_EXPOSED_CHISELED_COPPER.get()));
        buildscape$BIT_COPPER_BLOCK_ICONS.add(new ItemStack(ModItems.BIT_WEATHERED_COPPER_BLOCK.get()));
        buildscape$BIT_COPPER_BLOCK_ICONS.add(new ItemStack(ModItems.BIT_WEATHERED_CUT_COPPER.get()));
        buildscape$BIT_COPPER_BLOCK_ICONS.add(new ItemStack(ModItems.BIT_WEATHERED_CHISELED_COPPER.get()));
        buildscape$BIT_COPPER_BLOCK_ICONS.add(new ItemStack(ModItems.BIT_OXIDIZED_COPPER_BLOCK.get()));
        buildscape$BIT_COPPER_BLOCK_ICONS.add(new ItemStack(ModItems.BIT_OXIDIZED_CUT_COPPER.get()));
        buildscape$BIT_COPPER_BLOCK_ICONS.add(new ItemStack(ModItems.BIT_OXIDIZED_CHISELED_COPPER.get()));

        // Spool cycle
        buildscape$SPOOL_ICONS.add(new ItemStack(ModItems.WHITE_SPOOL.get()));
        buildscape$SPOOL_ICONS.add(new ItemStack(ModItems.ORANGE_SPOOL.get()));
        buildscape$SPOOL_ICONS.add(new ItemStack(ModItems.MAGENTA_SPOOL.get()));
        buildscape$SPOOL_ICONS.add(new ItemStack(ModItems.LIGHT_BLUE_SPOOL.get()));
        buildscape$SPOOL_ICONS.add(new ItemStack(ModItems.YELLOW_SPOOL.get()));
        buildscape$SPOOL_ICONS.add(new ItemStack(ModItems.LIME_SPOOL.get()));
        buildscape$SPOOL_ICONS.add(new ItemStack(ModItems.PINK_SPOOL.get()));
        buildscape$SPOOL_ICONS.add(new ItemStack(ModItems.CYAN_SPOOL.get()));
        buildscape$SPOOL_ICONS.add(new ItemStack(ModItems.PURPLE_SPOOL.get()));
        buildscape$SPOOL_ICONS.add(new ItemStack(ModItems.BLUE_SPOOL.get()));
        buildscape$SPOOL_ICONS.add(new ItemStack(ModItems.RED_SPOOL.get()));

        // Wallpaper cycle
        buildscape$WALLPAPER_ICONS.add(new ItemStack(ModItems.WHITE_WALLPAPER.get()));
        buildscape$WALLPAPER_ICONS.add(new ItemStack(ModItems.ORANGE_WALLPAPER.get()));
        buildscape$WALLPAPER_ICONS.add(new ItemStack(ModItems.MAGENTA_WALLPAPER.get()));
        buildscape$WALLPAPER_ICONS.add(new ItemStack(ModItems.LIGHT_BLUE_WALLPAPER.get()));
        buildscape$WALLPAPER_ICONS.add(new ItemStack(ModItems.YELLOW_WALLPAPER.get()));
        buildscape$WALLPAPER_ICONS.add(new ItemStack(ModItems.LIME_WALLPAPER.get()));
        buildscape$WALLPAPER_ICONS.add(new ItemStack(ModItems.PINK_WALLPAPER.get()));
        buildscape$WALLPAPER_ICONS.add(new ItemStack(ModItems.CYAN_WALLPAPER.get()));
        buildscape$WALLPAPER_ICONS.add(new ItemStack(ModItems.PURPLE_WALLPAPER.get()));
        buildscape$WALLPAPER_ICONS.add(new ItemStack(ModItems.RED_WALLPAPER.get()));

        // Froglight cycle
        buildscape$FROGLIGHT_ICONS.add(new ItemStack(ModBlocks.RUSSET_FROGLIGHT.get()));
        buildscape$FROGLIGHT_ICONS.add(new ItemStack(ModBlocks.TIDAL_FROGLIGHT.get()));
        buildscape$FROGLIGHT_ICONS.add(new ItemStack(ModBlocks.SCARLET_FROGLIGHT.get()));
        buildscape$FROGLIGHT_ICONS.add(new ItemStack(ModBlocks.CERULEAN_FROGLIGHT.get()));
        buildscape$FROGLIGHT_ICONS.add(new ItemStack(ModBlocks.GLEAMING_FROGLIGHT.get()));
        buildscape$FROGLIGHT_ICONS.add(new ItemStack(ModBlocks.AZURE_FROGLIGHT.get()));

        // Christmas cycle
        buildscape$CHRISTMAS_ICONS.add(new ItemStack(ModItems.FESTIVE_STOCKING.get()));
        buildscape$CHRISTMAS_ICONS.add(new ItemStack(ModItems.RED_ORNAMENT.get()));
        buildscape$CHRISTMAS_ICONS.add(new ItemStack(ModItems.MULTICOLOR_STRING_LIGHT.get()));
        buildscape$CHRISTMAS_ICONS.add(new ItemStack(ModItems.GLOW_STAR.get()));
        buildscape$CHRISTMAS_ICONS.add(new ItemStack(ModItems.SNOWY_SPRUCE_LEAVES.get()));
    }

    @Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/DisplayInfo;getIcon()Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack cycleIconIfApplicable(DisplayInfo displayInfo) {
        if (advancement == null || advancement.getId() == null) {
            return displayInfo.getIcon();
        }

        buildscape$initIcons();
        String path = advancement.getId().getPath();

        long timeIndex = System.currentTimeMillis() / 1200L;

        if ("one_more_block".equals(path)) {
            int idx = (int) (timeIndex % buildscape$STEEL_BLOCK_ICONS.size());
            return buildscape$STEEL_BLOCK_ICONS.get(idx);
        } else if ("okay_one_more".equals(path)) {
            int idx = (int) (timeIndex % buildscape$TILE_BLOCK_ICONS.size());
            return buildscape$TILE_BLOCK_ICONS.get(idx);
        } else if ("actually_one_last".equals(path)) {
            int idx = (int) (timeIndex % buildscape$ASHPEN_BLOCK_ICONS.size());
            return buildscape$ASHPEN_BLOCK_ICONS.get(idx);
        } else if ("one_last_one_i_promise".equals(path)) {
            int idx = (int) (timeIndex % buildscape$BIT_COPPER_BLOCK_ICONS.size());
            return buildscape$BIT_COPPER_BLOCK_ICONS.get(idx);
        } else if ("string_me_along".equals(path)) {
            int idx = (int) (timeIndex % buildscape$SPOOL_ICONS.size());
            return buildscape$SPOOL_ICONS.get(idx);
        } else if ("the_entire_catalogue".equals(path)) {
            int idx = (int) (timeIndex % buildscape$WALLPAPER_ICONS.size());
            return buildscape$WALLPAPER_ICONS.get(idx);
        } else if ("rainbow_mood_light".equals(path)) {
            int idx = (int) (timeIndex % buildscape$FROGLIGHT_ICONS.size());
            return buildscape$FROGLIGHT_ICONS.get(idx);
        } else if ("a_very_buildscape_christmas".equals(path)) {
            int idx = (int) (timeIndex % buildscape$CHRISTMAS_ICONS.size());
            return buildscape$CHRISTMAS_ICONS.get(idx);
        }

        return displayInfo.getIcon();
    }
}
