package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixin.support.ShulkerGhostFilterCapture;
import com.kingodogo.buildscape.util.GhostFilterable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ShulkerBoxBlock.class)
public class ShulkerBoxBlockMixin {

    @Inject(method = "onRemove", at = @At("HEAD"))
    private void captureFilterOnRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving, CallbackInfo ci) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof GhostFilterable filterable) {
                ShulkerGhostFilterCapture.capture(filterable);
            }
        }
    }

    @Dynamic
    @Inject(method = "playerWillDestroy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/player/Player;)V", at = @At("HEAD"), require = 0)
    private void captureFilterOnDestroy118(Level level, BlockPos pos, BlockState state, Player player, CallbackInfo ci) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof GhostFilterable filterable) {
            ShulkerGhostFilterCapture.capture(filterable);
        }
    }

    @Dynamic
    @Inject(method = "playerWillDestroy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/level/block/state/BlockState;", at = @At("HEAD"), require = 0)
    private void captureFilterOnDestroy121(Level level, BlockPos pos, BlockState state, Player player, CallbackInfoReturnable<?> cir) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof GhostFilterable filterable) {
            ShulkerGhostFilterCapture.capture(filterable);
        }
    }

    @Dynamic
    @Inject(method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/List;", at = @At("RETURN"), require = 0)
    private void preserveGhostFilters118(BlockState state, CallbackInfoReturnable<List<ItemStack>> cir) {
        ShulkerGhostFilterCapture.preserveInDrops(cir.getReturnValue());
    }

    @Dynamic
    @Inject(method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/storage/loot/LootParams$Builder;)Ljava/util/List;", at = @At("RETURN"), require = 0)
    private void preserveGhostFilters121(BlockState state, @Coerce Object lootParams, CallbackInfoReturnable<List<ItemStack>> cir) {
        ShulkerGhostFilterCapture.preserveInDrops(cir.getReturnValue());
    }
}
