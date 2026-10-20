package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.registry.blocks.CopperBlocks;
import com.kingodogo.buildscape.registry.items.GlassItems;
import com.kingodogo.buildscape.registry.items.MasonryItems;
import com.kingodogo.buildscape.registry.items.ShapedWoodItems;
import com.kingodogo.buildscape.registry.items.StoneItems;
import com.kingodogo.buildscape.registry.items.WoodItems;
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
        if (!buildscape$STEEL_BLOCK_ICONS.isEmpty()) {
            return;
        }

        buildscape$STEEL_BLOCK_ICONS.add(new ItemStack(MasonryItems.STEEL_BLOCK.get()));
        buildscape$STEEL_BLOCK_ICONS.add(new ItemStack(MasonryItems.SCRAPED_STEEL.get()));
        buildscape$STEEL_BLOCK_ICONS.add(new ItemStack(MasonryItems.RUSTIC_SCRAPED_STEEL.get()));
        buildscape$STEEL_BLOCK_ICONS.add(new ItemStack(MasonryItems.STACKED_STEEL.get()));
        buildscape$STEEL_BLOCK_ICONS.add(new ItemStack(MasonryItems.STEEL_PANELS.get()));
        buildscape$STEEL_BLOCK_ICONS.add(new ItemStack(MasonryItems.CROSSED_STEEL_PANELS.get()));
        buildscape$STEEL_BLOCK_ICONS.add(new ItemStack(MasonryItems.STEEL_MESH_BLOCK.get()));
        buildscape$STEEL_BLOCK_ICONS.add(new ItemStack(MasonryItems.PRESSED_STEEL.get()));
        buildscape$STEEL_BLOCK_ICONS.add(new ItemStack(MasonryItems.CUT_STEEL.get()));
        buildscape$STEEL_BLOCK_ICONS.add(new ItemStack(MasonryItems.POLISHED_STEEL.get()));
        buildscape$STEEL_BLOCK_ICONS.add(new ItemStack(MasonryItems.FACTORY_STEEL_PANEL.get()));
        buildscape$STEEL_BLOCK_ICONS.add(new ItemStack(MasonryItems.STEEL_CASING.get()));
        buildscape$STEEL_BLOCK_ICONS.add(new ItemStack(MasonryItems.STEEL_TRIM.get()));
        buildscape$STEEL_BLOCK_ICONS.add(new ItemStack(MasonryItems.STEEL_PILLAR.get()));
        buildscape$STEEL_BLOCK_ICONS.add(new ItemStack(MasonryItems.BOLTED_STEEL_PILLAR.get()));
        buildscape$STEEL_BLOCK_ICONS.add(new ItemStack(MasonryItems.STEEL_FAN.get()));

        buildscape$TILE_BLOCK_ICONS.add(new ItemStack(StoneItems.WHITE_TILES.get()));
        buildscape$TILE_BLOCK_ICONS.add(new ItemStack(StoneItems.ORANGE_TILES.get()));
        buildscape$TILE_BLOCK_ICONS.add(new ItemStack(StoneItems.MAGENTA_TILES.get()));
        buildscape$TILE_BLOCK_ICONS.add(new ItemStack(StoneItems.LIGHT_BLUE_TILES.get()));
        buildscape$TILE_BLOCK_ICONS.add(new ItemStack(StoneItems.YELLOW_TILES.get()));
        buildscape$TILE_BLOCK_ICONS.add(new ItemStack(StoneItems.LIME_TILES.get()));
        buildscape$TILE_BLOCK_ICONS.add(new ItemStack(StoneItems.PINK_TILES.get()));
        buildscape$TILE_BLOCK_ICONS.add(new ItemStack(StoneItems.GRAY_TILES.get()));
        buildscape$TILE_BLOCK_ICONS.add(new ItemStack(StoneItems.LIGHT_GRAY_TILES.get()));
        buildscape$TILE_BLOCK_ICONS.add(new ItemStack(StoneItems.CYAN_TILES.get()));
        buildscape$TILE_BLOCK_ICONS.add(new ItemStack(StoneItems.PURPLE_TILES.get()));
        buildscape$TILE_BLOCK_ICONS.add(new ItemStack(StoneItems.BLUE_TILES.get()));
        buildscape$TILE_BLOCK_ICONS.add(new ItemStack(StoneItems.BROWN_TILES.get()));
        buildscape$TILE_BLOCK_ICONS.add(new ItemStack(StoneItems.GREEN_TILES.get()));
        buildscape$TILE_BLOCK_ICONS.add(new ItemStack(StoneItems.RED_TILES.get()));
        buildscape$TILE_BLOCK_ICONS.add(new ItemStack(StoneItems.BLACK_TILES.get()));

        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(WoodItems.ASHPEN_WHITE_PLANKS.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(WoodItems.ORANGE_ASHPEN_PLANKS.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(WoodItems.MAGENTA_ASHPEN_PLANKS.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(WoodItems.LIGHT_BLUE_ASHPEN_PLANKS.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(WoodItems.YELLOW_ASHPEN_PLANKS.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(WoodItems.LIME_ASHPEN_PLANKS.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(WoodItems.PINK_ASHPEN_PLANKS.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(WoodItems.GRAY_ASHPEN_PLANKS.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(WoodItems.LIGHT_GRAY_ASHPEN_PLANKS.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(WoodItems.CYAN_ASHPEN_PLANKS.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(WoodItems.PURPLE_ASHPEN_PLANKS.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(WoodItems.BLUE_ASHPEN_PLANKS.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(WoodItems.BROWN_ASHPEN_PLANKS.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(WoodItems.GREEN_ASHPEN_PLANKS.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(WoodItems.RED_ASHPEN_PLANKS.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(WoodItems.BLACK_ASHPEN_PLANKS.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(GlassItems.ASHPEN_LOG.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(GlassItems.STRIPPED_ASHPEN_LOG.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(GlassItems.ASHPEN_WOOD.get()));
        buildscape$ASHPEN_BLOCK_ICONS.add(new ItemStack(GlassItems.STRIPPED_ASHPEN_WOOD.get()));

        buildscape$BIT_COPPER_BLOCK_ICONS.add(new ItemStack(StoneItems.BIT_COPPER_BLOCK.get()));
        buildscape$BIT_COPPER_BLOCK_ICONS.add(new ItemStack(StoneItems.BIT_CUT_COPPER.get()));
        buildscape$BIT_COPPER_BLOCK_ICONS.add(new ItemStack(StoneItems.BIT_CHISELED_COPPER.get()));
        buildscape$BIT_COPPER_BLOCK_ICONS.add(new ItemStack(StoneItems.BIT_EXPOSED_COPPER_BLOCK.get()));
        buildscape$BIT_COPPER_BLOCK_ICONS.add(new ItemStack(StoneItems.BIT_EXPOSED_CUT_COPPER.get()));
        buildscape$BIT_COPPER_BLOCK_ICONS.add(new ItemStack(StoneItems.BIT_EXPOSED_CHISELED_COPPER.get()));
        buildscape$BIT_COPPER_BLOCK_ICONS.add(new ItemStack(StoneItems.BIT_WEATHERED_COPPER_BLOCK.get()));
        buildscape$BIT_COPPER_BLOCK_ICONS.add(new ItemStack(StoneItems.BIT_WEATHERED_CUT_COPPER.get()));
        buildscape$BIT_COPPER_BLOCK_ICONS.add(new ItemStack(StoneItems.BIT_WEATHERED_CHISELED_COPPER.get()));
        buildscape$BIT_COPPER_BLOCK_ICONS.add(new ItemStack(StoneItems.BIT_OXIDIZED_COPPER_BLOCK.get()));
        buildscape$BIT_COPPER_BLOCK_ICONS.add(new ItemStack(StoneItems.BIT_OXIDIZED_CUT_COPPER.get()));
        buildscape$BIT_COPPER_BLOCK_ICONS.add(new ItemStack(StoneItems.BIT_OXIDIZED_CHISELED_COPPER.get()));

        buildscape$SPOOL_ICONS.add(new ItemStack(ShapedWoodItems.WHITE_SPOOL.get()));
        buildscape$SPOOL_ICONS.add(new ItemStack(ShapedWoodItems.ORANGE_SPOOL.get()));
        buildscape$SPOOL_ICONS.add(new ItemStack(ShapedWoodItems.MAGENTA_SPOOL.get()));
        buildscape$SPOOL_ICONS.add(new ItemStack(ShapedWoodItems.LIGHT_BLUE_SPOOL.get()));
        buildscape$SPOOL_ICONS.add(new ItemStack(ShapedWoodItems.YELLOW_SPOOL.get()));
        buildscape$SPOOL_ICONS.add(new ItemStack(ShapedWoodItems.LIME_SPOOL.get()));
        buildscape$SPOOL_ICONS.add(new ItemStack(ShapedWoodItems.PINK_SPOOL.get()));
        buildscape$SPOOL_ICONS.add(new ItemStack(ShapedWoodItems.CYAN_SPOOL.get()));
        buildscape$SPOOL_ICONS.add(new ItemStack(ShapedWoodItems.PURPLE_SPOOL.get()));
        buildscape$SPOOL_ICONS.add(new ItemStack(ShapedWoodItems.BLUE_SPOOL.get()));
        buildscape$SPOOL_ICONS.add(new ItemStack(ShapedWoodItems.RED_SPOOL.get()));

        buildscape$WALLPAPER_ICONS.add(new ItemStack(ShapedWoodItems.WHITE_WALLPAPER.get()));
        buildscape$WALLPAPER_ICONS.add(new ItemStack(ShapedWoodItems.ORANGE_WALLPAPER.get()));
        buildscape$WALLPAPER_ICONS.add(new ItemStack(ShapedWoodItems.MAGENTA_WALLPAPER.get()));
        buildscape$WALLPAPER_ICONS.add(new ItemStack(ShapedWoodItems.LIGHT_BLUE_WALLPAPER.get()));
        buildscape$WALLPAPER_ICONS.add(new ItemStack(ShapedWoodItems.YELLOW_WALLPAPER.get()));
        buildscape$WALLPAPER_ICONS.add(new ItemStack(ShapedWoodItems.LIME_WALLPAPER.get()));
        buildscape$WALLPAPER_ICONS.add(new ItemStack(ShapedWoodItems.PINK_WALLPAPER.get()));
        buildscape$WALLPAPER_ICONS.add(new ItemStack(ShapedWoodItems.CYAN_WALLPAPER.get()));
        buildscape$WALLPAPER_ICONS.add(new ItemStack(ShapedWoodItems.PURPLE_WALLPAPER.get()));
        buildscape$WALLPAPER_ICONS.add(new ItemStack(ShapedWoodItems.RED_WALLPAPER.get()));

        buildscape$FROGLIGHT_ICONS.add(new ItemStack(CopperBlocks.RUSSET_FROGLIGHT.get()));
        buildscape$FROGLIGHT_ICONS.add(new ItemStack(CopperBlocks.TIDAL_FROGLIGHT.get()));
        buildscape$FROGLIGHT_ICONS.add(new ItemStack(CopperBlocks.SCARLET_FROGLIGHT.get()));
        buildscape$FROGLIGHT_ICONS.add(new ItemStack(CopperBlocks.CERULEAN_FROGLIGHT.get()));
        buildscape$FROGLIGHT_ICONS.add(new ItemStack(CopperBlocks.GLEAMING_FROGLIGHT.get()));
        buildscape$FROGLIGHT_ICONS.add(new ItemStack(CopperBlocks.AZURE_FROGLIGHT.get()));

        buildscape$CHRISTMAS_ICONS.add(new ItemStack(WoodItems.FESTIVE_STOCKING.get()));
        buildscape$CHRISTMAS_ICONS.add(new ItemStack(WoodItems.RED_ORNAMENT.get()));
        buildscape$CHRISTMAS_ICONS.add(new ItemStack(WoodItems.MULTICOLOR_STRING_LIGHT.get()));
        buildscape$CHRISTMAS_ICONS.add(new ItemStack(WoodItems.GLOW_STAR.get()));
        buildscape$CHRISTMAS_ICONS.add(new ItemStack(WoodItems.SNOWY_SPRUCE_LEAVES.get()));
    }

    @Redirect(method = "draw", require = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/DisplayInfo;getIcon()Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack cycleIconIfApplicable(DisplayInfo displayInfo) {
        if (advancement == null || advancement.getId() == null
                || !"buildscape".equals(advancement.getId().getNamespace())) {
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
