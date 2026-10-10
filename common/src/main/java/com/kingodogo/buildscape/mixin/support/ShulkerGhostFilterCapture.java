package com.kingodogo.buildscape.mixin.support;

import com.kingodogo.buildscape.mixin.MixinFactory;
import com.kingodogo.buildscape.util.GhostFilterable;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;

import java.util.List;
public final class ShulkerGhostFilterCapture {
    private static final ThreadLocal<GhostFilterable> CAPTURED = new ThreadLocal<>();

    private ShulkerGhostFilterCapture() {
    }

    public static void capture(GhostFilterable filterable) {
        CAPTURED.set(filterable);
    }

    public static void preserveInDrops(List<ItemStack> drops) {
        GhostFilterable filterable = CAPTURED.get();
        CAPTURED.remove();
        if (filterable == null || drops == null) {
            return;
        }

        String[] ghostFilters = filterable.buildscape$getGhostFilters();
        if (ghostFilters == null) {
            return;
        }

        boolean hasFilter = false;
        ListTag filterList = new ListTag();
        for (int i = 0; i < 27; i++) {
            String filter = i < ghostFilters.length && ghostFilters[i] != null ? ghostFilters[i] : "";
            hasFilter |= !filter.isEmpty();
            filterList.add(StringTag.valueOf(filter));
        }
        if (!hasFilter) {
            return;
        }

        for (ItemStack stack : drops) {
            if (stack.getItem() instanceof BlockItem blockItem
                    && blockItem.getBlock() instanceof ShulkerBoxBlock) {
                MixinFactory.preserveGhostFilters(stack, filterList);
            }
        }
    }
}
