package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.world.ModGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LeavesBlock.class)
public class LeavesBlockMixin {

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    private void onTick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            java.util.Random random,
            CallbackInfo ci
    ) {
        if (level.getGameRules().getBoolean(ModGameRules.FAST_LEAF_DECAY)) {
            BlockState currentState = level.getBlockState(pos);
            if (currentState.getBlock() instanceof LeavesBlock) {
                BooleanProperty PERSISTENT = LeavesBlock.PERSISTENT;
                if (currentState.hasProperty(PERSISTENT) && !currentState.getValue(PERSISTENT)) {
                    level.scheduleTick(pos, (LeavesBlock) (Object) this, 1);
                }
            }
        }
    }

    @Inject(
            method = "randomTick",
            at = @At("TAIL")
    )
    private void onRandomTick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            java.util.Random random,
            CallbackInfo ci
    ) {
        if (level.getGameRules().getBoolean(ModGameRules.FAST_LEAF_DECAY)) {
            BlockState currentState = level.getBlockState(pos);
            if (currentState.getBlock() instanceof LeavesBlock) {
                BooleanProperty PERSISTENT = LeavesBlock.PERSISTENT;
                if (currentState.hasProperty(PERSISTENT) && !currentState.getValue(PERSISTENT)) {
                    level.scheduleTick(pos, (LeavesBlock) (Object) this, 1);
                }
            }
        }
    }
}

