package com.kingodogo.buildscape.variantengine.item;

import com.kingodogo.buildscape.variantengine.block.ExtShapeBlockInterface;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VariantBlockItem extends BlockItem {
    public VariantBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Block block = this.getBlock();
        if (block instanceof ExtShapeBlockInterface ext) {
            Block base = ext.getBaseBlock();
            if (base != null && base.getRegistryName() != null) {
                String namespace = base.getRegistryName().getNamespace();
                // Format: Added from %namespace% by Buildscape!
                tooltip.add(new TextComponent("Added from ")
                        .append(new TextComponent(namespace).withStyle(ChatFormatting.GOLD))
                        .append(new TextComponent(" by Buildscape!"))
                        .withStyle(ChatFormatting.GRAY));
            }
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
