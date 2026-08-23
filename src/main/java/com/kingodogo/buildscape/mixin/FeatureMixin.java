package com.kingodogo.buildscape.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;
import java.util.function.BiConsumer;

@Mixin(TrunkPlacer.class)
public class FeatureMixin {
    /**
     * Prevents tree features from replacing Composter planters with dirt
     * when a tree grows on top of or near a planter block.
     */
    @Inject(method = "setDirtAt", at = @At("HEAD"), cancellable = true)
    private static void onSetDirtAt(
            LevelSimulatedReader level,
            BiConsumer<BlockPos, BlockState> blockSetter,
            Random random,
            BlockPos pos,
            TreeConfiguration treeConfig,
            CallbackInfo ci
    ) {
        if (level.isStateAtPosition(pos, state -> state.is(Blocks.COMPOSTER))) {
            ci.cancel();
        }
    }
}
