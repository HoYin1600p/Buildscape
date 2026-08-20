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
    private static final List<ItemStack> buildscape$BUILDING_BLOCK_ICONS = new ArrayList<>();
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
        if (!buildscape$BUILDING_BLOCK_ICONS.isEmpty()) return;

        // Building blocks cycle
        buildscape$BUILDING_BLOCK_ICONS.add(new ItemStack(ModItems.CARDBOARD_BLOCK.get()));
        buildscape$BUILDING_BLOCK_ICONS.add(new ItemStack(ModItems.COPPER_PILLAR.get()));
        buildscape$BUILDING_BLOCK_ICONS.add(new ItemStack(ModItems.SLIT_COPPER.get()));
        buildscape$BUILDING_BLOCK_ICONS.add(new ItemStack(ModItems.FLAMING_STEEL_BLOCK.get()));
        buildscape$BUILDING_BLOCK_ICONS.add(new ItemStack(ModItems.HOLLOW_OAK_LOG.get()));
        buildscape$BUILDING_BLOCK_ICONS.add(new ItemStack(ModItems.WHITE_CUSHION.get()));
        buildscape$BUILDING_BLOCK_ICONS.add(new ItemStack(ModItems.WHITE_WALLPAPER.get()));
        buildscape$BUILDING_BLOCK_ICONS.add(new ItemStack(ModItems.STEEL_INGOT.get()));

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

        if ("one_more_block".equals(path) || "okay_one_more".equals(path) || "actually_one_last".equals(path)) {
            int idx = (int) (timeIndex % buildscape$BUILDING_BLOCK_ICONS.size());
            return buildscape$BUILDING_BLOCK_ICONS.get(idx);
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
