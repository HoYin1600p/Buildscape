package com.kingodogo.buildscape.mixin.forge;

import com.kingodogo.buildscape.util.BeaconBeamStoppers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BeaconBlockEntity.class)
abstract class BeaconStopperColorMixin {
    @Redirect(method = "tick", at = @At(value = "INVOKE", remap = false,
            target = "Lnet/minecraft/world/level/block/state/BlockState;getBeaconColorMultiplier(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)I"))
    private static int buildscape$stopperColor(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
        return BeaconBeamStoppers.isStopper(state) ? -1 : state.getBeaconColorMultiplier(level, pos, beaconPos);
    }
}
