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
        // Check if fast leaf decay is enabled and leaf is not persistent
        // IMPORTANT: Only non-persistent leaves (naturally generated) are processed.
        // Player-placed leaves have PERSISTENT=true and are automatically excluded.
        if (level.getGameRules().getBoolean(ModGameRules.FAST_LEAF_DECAY)) {
            BlockState currentState = level.getBlockState(pos);
            // Only process if the leaf still exists and is not persistent
            // Persistent leaves (placed by players) will never decay, even with fast decay enabled
            if (currentState.getBlock() instanceof LeavesBlock) {
                BooleanProperty PERSISTENT = LeavesBlock.PERSISTENT;
                if (currentState.hasProperty(PERSISTENT) && !currentState.getValue(PERSISTENT)) {
                    LeavesBlock leavesBlock = (LeavesBlock) (Object) this;
                    // Call randomTick to trigger decay check - this will check distance and decay if needed
                    leavesBlock.randomTick(currentState, level, pos, random);
                    // Schedule another tick with delay 4 (slower, more gradual decay animation)
                    // (only if leaf still exists and is still non-persistent)
                    BlockState afterTick = level.getBlockState(pos);
                    if (afterTick.getBlock() instanceof LeavesBlock && 
                        afterTick.hasProperty(PERSISTENT) && 
                        !afterTick.getValue(PERSISTENT)) {
                        level.scheduleTick(pos, leavesBlock, 6);
                    }
                }
            }
        }
    }
}

