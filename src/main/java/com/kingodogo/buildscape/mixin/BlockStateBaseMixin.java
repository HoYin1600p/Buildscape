package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.block.PlanterHelper;
import com.kingodogo.buildscape.block.PlanterHelper.PlanterType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.kingodogo.buildscape.cosmetic.sign.SignFrameAttachment;
import com.kingodogo.buildscape.cosmetic.sign.SignFrameType;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class BlockStateBaseMixin {
    @Inject(method = "canSurvive", at = @At("HEAD"), cancellable = true)
    private void onCanSurvive(LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        if (belowState.is(Blocks.COMPOSTER)) {
            if (belowState.hasProperty(PlanterHelper.PLANTER) && belowState.getValue(PlanterHelper.PLANTER) != PlanterType.NONE) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "onRemove", at = @At("HEAD"))
    private void buildscape$onSignRemove(Level level, BlockPos pos, BlockState newState, boolean isMoving, CallbackInfo ci) {
        BlockState state = (BlockState) (Object) this;
        if (!state.is(newState.getBlock()) && !level.isClientSide()) {
            if (state.getBlock() instanceof SignBlock) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof SignBlockEntity sign) {
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
        }
    }
}
