package com.kingodogo.buildscape.mixin.fabric;

import com.kingodogo.buildscape.event.ModCommonEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockItem.class)
public abstract class BlockPlacementMixin {
    @Redirect(method = "place", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/level/block/Block;setPlacedBy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V"))
    private void buildscape$placed(Block block, Level level, BlockPos pos, BlockState state,
                                  LivingEntity placer, ItemStack stack) {
        block.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide()) {
            ModCommonEvents.onBlockPlaced(level, pos, level.getBlockState(pos),
                    placer instanceof Player player ? player : null);
        }
    }
}
