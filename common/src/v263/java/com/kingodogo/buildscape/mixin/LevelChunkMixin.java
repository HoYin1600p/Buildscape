package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.block.HollowLogBlock;
import com.kingodogo.buildscape.block.HollowPipeBlock;
import com.kingodogo.buildscape.block.PillarBlock;
import com.kingodogo.buildscape.block.TrappedDecoratedPotBlockEntity;
import com.kingodogo.buildscape.cosmetic.sign.SignFrameAttachment;
import com.kingodogo.buildscape.cosmetic.sign.SignFrameType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin {
    // The old block entity is removed before affectNeighborsAfterRemoval in 26.2.
    @Redirect(method = "setBlockState", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/entity/BlockEntity;preRemoveSideEffects(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"))
    private void buildscape$preserveRemovalData(BlockEntity entity, BlockPos pos, BlockState state) {
        if (entity instanceof SignBlockEntity sign) {
            SignFrameType frame = SignFrameAttachment.getFrame(sign);
            if (frame != SignFrameType.NONE && frame.getItem() != null && entity.getLevel() != null) {
                Containers.dropItemStack(entity.getLevel(), pos.getX() + 0.5D, pos.getY() + 0.5D,
                        pos.getZ() + 0.5D, new ItemStack(frame.getItem()));
                SignFrameAttachment.setFrame(sign, SignFrameType.NONE);
            }
        }
        // Main's onRemove released a spawn egg from a trapped pot as its mob; the generic container drop would drop the egg item.
        if (entity instanceof TrappedDecoratedPotBlockEntity pot && entity.getLevel() != null && !pot.isEmpty()) {
            pot.dropContents(entity.getLevel(), pos);
            pot.setStoredItem(ItemStack.EMPTY, false);
        }
        entity.preRemoveSideEffects(pos, state);
    }

    // Main's onRemove ran for every replacement, including the setblock command. 26.3 only calls
    // preRemoveSideEffects/affectNeighborsAfterRemoval when side effects are allowed (the command passes
    // flag 256), and removes the block entity before affectNeighborsAfterRemoval, so hollow logs, pipes
    // and pillars run their removal here while their block entity data is still readable.
    @Inject(method = "setBlockState", at = @At(value = "INVOKE", ordinal = 0,
            target = "Lnet/minecraft/world/level/chunk/LevelChunk;removeBlockEntity(Lnet/minecraft/core/BlockPos;)V"))
    private void buildscape$hollowRemoved(BlockPos pos, BlockState newState, int flags, CallbackInfoReturnable<BlockState> cir) {
        LevelChunk chunk = (LevelChunk) (Object) this;
        if (chunk.getLevel().isClientSide()) {
            return;
        }
        BlockEntity entity = chunk.getBlockEntity(pos);
        if (entity == null) {
            return;
        }
        BlockState oldState = entity.getBlockState();
        if (oldState.getBlock() instanceof HollowLogBlock log) {
            log.onBlockRemoved(chunk.getLevel(), pos, oldState);
        } else if (oldState.getBlock() instanceof HollowPipeBlock pipe) {
            pipe.onBlockRemoved(chunk.getLevel(), pos, oldState);
        } else if (!oldState.is(newState.getBlock()) && oldState.getBlock() instanceof PillarBlock pillar) {
            pillar.onBlockRemoved(chunk.getLevel(), pos, oldState);
        }
    }
}
