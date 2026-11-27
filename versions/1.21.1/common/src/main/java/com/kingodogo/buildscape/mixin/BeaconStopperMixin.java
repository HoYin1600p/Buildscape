package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.util.BeaconBeamHeightAccessor;
import com.kingodogo.buildscape.util.BeaconBeamScanState;
import com.kingodogo.buildscape.util.BeaconBeamStoppers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.List;

@Mixin(BeaconBlockEntity.class)
abstract class BeaconStopperMixin {
    @Redirect(method = "tick", require = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;"))
    private static Block buildscape$stopperIsNotColor(BlockState state) {
        return BeaconBeamStoppers.isStopper(state) ? Blocks.AIR : state.getBlock();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getLightBlock(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)I"))
    private static int buildscape$stopperLight(BlockState state, BlockGetter world, BlockPos scanPos,
            Level level, BlockPos beaconPos, BlockState beaconState, BeaconBlockEntity beacon) {
        if (BeaconBeamStoppers.isStopper(state)) {
            ((BeaconBeamHeightAccessor) beacon).buildscape$markBeamBlocked(scanPos.getY() - beaconPos.getY());
            return 15;
        }
        return state.getLightBlock(world, scanPos);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
    private static boolean buildscape$stopperOverridesBedrock(BlockState state, Block block) {
        return !BeaconBeamStoppers.isStopper(state) && state.is(block);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/List;clear()V", remap = false))
    private static void buildscape$keepStoppedSections(List<?> sections,
            Level level, BlockPos pos, BlockState state, BeaconBlockEntity beacon) {
        if (((BeaconBeamHeightAccessor) beacon).buildscape$getPendingBeamHeight() >= BeaconBeamScanState.UNLIMITED) sections.clear();
    }
}
