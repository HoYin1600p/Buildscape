package com.kingodogo.buildscape.mixin.fabric;

import com.kingodogo.buildscape.util.BeaconBeamStoppers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BeaconBlockEntity.class)
public abstract class BeaconStopperFabricMixin {
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;"))
    private static Block buildscape$stopperBlock(BlockState state) {
        return BeaconBeamStoppers.isStopper(state) ? Blocks.AIR : state.getBlock();
    }
}
