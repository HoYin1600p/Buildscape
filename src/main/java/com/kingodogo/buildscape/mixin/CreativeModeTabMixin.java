package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.variantengine.builder.BlockShape;
import com.kingodogo.buildscape.variantengine.util.BlockBiMaps;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ListIterator;

@Mixin(CreativeModeTab.class)
public abstract class CreativeModeTabMixin {

    @Inject(method = "fillItemList", at = @At("TAIL"))
    private void buildscape$injectVerticalVariants(NonNullList<ItemStack> items, CallbackInfo ci) {
        // Inject vertical variant right after its parent in EVERY creative tab.
        // For the Buildscape tab, appendVerticalItems() additionally appends any verticals
        // whose parent is from another mod (not already in our list).

        ListIterator<ItemStack> it = items.listIterator();
        while (it.hasNext()) {
            ItemStack stack = it.next();
            if (stack.getItem() instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();
                Block verticalSlab = BlockBiMaps.getBlockOf(BlockShape.VERTICAL_SLAB, block);
                if (verticalSlab != null) {
                    it.add(new ItemStack(verticalSlab));
                }
                Block verticalStairs = BlockBiMaps.getBlockOf(BlockShape.VERTICAL_STAIRS, block);
                if (verticalStairs != null) {
                    it.add(new ItemStack(verticalStairs));
                }
            }
        }
    }
}
