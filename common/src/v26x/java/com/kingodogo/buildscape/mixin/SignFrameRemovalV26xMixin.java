package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.cosmetic.sign.SignFrameAttachment;
import com.kingodogo.buildscape.cosmetic.sign.SignFrameType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Drops the attached sign frame when its sign block is replaced. The reference hooks BlockStateBase.onRemove, which
 * runs for every block change. On 26.2 preRemoveSideEffects (hooked in LevelChunkMixin) is skipped when the change
 * carries the skip-side-effects flag (/setblock, structure placement), and affectNeighborsAfterRemoval runs after the
 * block entity is gone. LevelChunk.removeBlockEntity runs for every replacement while the sign still has its frame
 * attachment; the frame is cleared after the first drop, so the earlier hook and this one never drop it twice.
 */
@Mixin(LevelChunk.class)
public abstract class SignFrameRemovalV26xMixin {

    @Inject(method = "removeBlockEntity", at = @At("HEAD"))
    private void buildscape$dropSignFrame(BlockPos pos, CallbackInfo ci) {
        LevelChunk self = (LevelChunk) (Object) this;
        BlockEntity entity = self.getBlockEntities().get(pos);
        if (!(entity instanceof SignBlockEntity sign) || !(entity.getBlockState().getBlock() instanceof SignBlock)) {
            return;
        }
        Level level = self.getLevel();
        if (level == null || level.isClientSide() || self.getBlockState(pos).is(entity.getBlockState().getBlock())) {
            return;
        }
        SignFrameType frame = SignFrameAttachment.getFrame(sign);
        if (frame != SignFrameType.NONE && frame.getItem() != null) {
            Containers.dropItemStack(
                    level,
                    (double) pos.getX() + 0.5D,
                    (double) pos.getY() + 0.5D,
                    (double) pos.getZ() + 0.5D,
                    new ItemStack(frame.getItem())
            );
            SignFrameAttachment.setFrame(sign, SignFrameType.NONE);
        }
    }
}
