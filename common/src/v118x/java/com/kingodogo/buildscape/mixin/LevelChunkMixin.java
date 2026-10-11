package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.cosmetic.sign.SignFrameAttachment;
import com.kingodogo.buildscape.cosmetic.sign.SignFrameType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin {
    @Dynamic
    @Inject(method = "setBlockState", at = @At("HEAD"), require = 0)
    private void buildscape$preserveRemovalData(BlockPos pos, BlockState state, boolean moving,
                                               CallbackInfoReturnable<BlockState> cir) {
        LevelChunk chunk = (LevelChunk) (Object) this;
        if (chunk.getLevel().isClientSide() || chunk.getBlockState(pos).getBlock() == state.getBlock()) return;
        if (chunk.getBlockEntity(pos) instanceof SignBlockEntity sign) {
            SignFrameType frame = SignFrameAttachment.getFrame(sign);
            if (frame != SignFrameType.NONE && frame.getItem() != null) {
                Containers.dropItemStack(chunk.getLevel(), pos.getX() + 0.5D, pos.getY() + 0.5D,
                        pos.getZ() + 0.5D, new ItemStack(frame.getItem()));
                SignFrameAttachment.setFrame(sign, SignFrameType.NONE);
            }
        }
    }
}