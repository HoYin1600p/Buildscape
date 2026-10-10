package com.kingodogo.buildscape.mixin;

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
import org.spongepowered.asm.mixin.injection.Redirect;

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
        entity.preRemoveSideEffects(pos, state);
    }
}
