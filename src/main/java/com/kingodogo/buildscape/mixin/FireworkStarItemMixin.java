package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.firework.CustomFireworkShapeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.FireworkStarItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(FireworkStarItem.class)
public abstract class FireworkStarItemMixin {

    @Inject(method = "appendHoverText(Lnet/minecraft/nbt/CompoundTag;Ljava/util/List;)V", at = @At("HEAD"), cancellable = true)
    private static void buildscape$appendCustomShapeHoverText(CompoundTag tag, List<Component> tooltip, CallbackInfo ci) {
        byte type = tag.getByte("Type");
        if (CustomFireworkShapeRegistry.isCustomShape(type)) {
            String shapeTranslationKey = switch (type) {
                case CustomFireworkShapeRegistry.CAKE_ID -> "item.minecraft.firework_star.shape.cake";
                case CustomFireworkShapeRegistry.CROWN_ID -> "item.minecraft.firework_star.shape.crown";
                case CustomFireworkShapeRegistry.TROPHY_ID -> "item.minecraft.firework_star.shape.trophy";
                case CustomFireworkShapeRegistry.CHRISTMAS_TREE_ID -> "item.minecraft.firework_star.shape.christmas_tree";
                case CustomFireworkShapeRegistry.PRESENTS_ID -> "item.minecraft.firework_star.shape.presents";
                case CustomFireworkShapeRegistry.CANDY_CANE_ID -> "item.minecraft.firework_star.shape.candy_cane";
                case CustomFireworkShapeRegistry.PHOENIX_ID -> "item.minecraft.firework_star.shape.phoenix";
                case CustomFireworkShapeRegistry.SNOWFLAKE_ID -> "item.minecraft.firework_star.shape.snowflake";
                default -> "item.minecraft.firework_star.shape.custom";
            };

            tooltip.add(new TranslatableComponent(shapeTranslationKey).withStyle(ChatFormatting.GRAY));

            int[] colors = tag.getIntArray("Colors");
            if (colors.length > 0) {
                tooltip.add(appendColors(new TextComponent("").withStyle(ChatFormatting.GRAY), colors));
            }

            int[] fadeColors = tag.getIntArray("FadeColors");
            if (fadeColors.length > 0) {
                tooltip.add(appendColors((new TranslatableComponent("item.minecraft.firework_star.fade_to")).append(" ").withStyle(ChatFormatting.GRAY), fadeColors));
            }

            if (tag.getBoolean("Flicker")) {
                tooltip.add((new TranslatableComponent("item.minecraft.firework_star.flicker")).withStyle(ChatFormatting.GRAY));
            }

            if (tag.getBoolean("Trail")) {
                tooltip.add((new TranslatableComponent("item.minecraft.firework_star.trail")).withStyle(ChatFormatting.GRAY));
            }

            ci.cancel();
        }
    }

    private static Component appendColors(Component component, int[] colors) {
        TextComponent textcomponent = new TextComponent("");
        for (int i = 0; i < colors.length; ++i) {
            if (i > 0) {
                textcomponent.append(", ");
            }
            textcomponent.append(getItemColorName(colors[i]));
        }
        return component.copy().append(textcomponent);
    }

    private static Component getItemColorName(int color) {
        DyeColor dyecolor = DyeColor.byFireworkColor(color);
        if (dyecolor == null) {
            return new TranslatableComponent("item.minecraft.firework_star.custom_color");
        } else {
            return new TranslatableComponent("item.minecraft.firework_star." + dyecolor.getName());
        }
    }
}
