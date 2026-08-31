package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.util.GhostFilterable;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ShulkerBoxBlock.class)
public class ShulkerBoxBlockMixin {
    @Inject(method = "getDrops", at = @At("RETURN"))
    private void preserveGhostFiltersInDrops(BlockState state, LootContext.Builder builder, CallbackInfoReturnable<List<ItemStack>> cir) {
        BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof GhostFilterable filterable) {
            String[] ghostFilters = filterable.buildscape$getGhostFilters();
            if (ghostFilters != null) {
                boolean hasFilter = false;
                ListTag filterList = new ListTag();
                for (int i = 0; i < 27; i++) {
                    String f = i < ghostFilters.length && ghostFilters[i] != null ? ghostFilters[i] : "";
                    if (!f.isEmpty()) hasFilter = true;
                    filterList.add(StringTag.valueOf(f));
                }
                if (hasFilter) {
                    List<ItemStack> drops = cir.getReturnValue();
                    for (ItemStack stack : drops) {
                        if (stack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof ShulkerBoxBlock) {
                            stack.getOrCreateTagElement("BlockEntityTag").put("GhostFilters", filterList.copy());
                        }
                    }
                }
            }
        }
    }
}
