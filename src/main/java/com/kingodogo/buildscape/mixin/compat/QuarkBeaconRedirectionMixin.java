package com.kingodogo.buildscape.mixin.compat;

import com.kingodogo.buildscape.util.BeaconBeamScanState;
import com.kingodogo.buildscape.util.BeaconBeamStoppers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "vazkii.quark.content.tools.module.BeaconRedirectionModule", remap = false)
public class QuarkBeaconRedirectionMixin {

    @Unique
    private static final ThreadLocal<BeaconBeamScanState> buildscape$quarkScan = ThreadLocal
            .withInitial(BeaconBeamScanState::new);

    @Inject(method = "tickBeacon(Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;)I", at = @At("HEAD"), remap = false, require = 0)
    private static void buildscape$beginScan(BeaconBlockEntity beacon, CallbackInfoReturnable<Integer> callback) {
        buildscape$quarkScan.get().beginScan();
    }

    @Redirect(method = "tickBeacon(Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;)I", remap = false, require = 0, at = @At(value = "INVOKE", remap = true, target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;"))
    private static Block buildscape$stopperBlock(BlockState state) {
        // Stoppers also take precedence over Quark's redirection and bedrock exemptions.
        return BeaconBeamStoppers.isStopper(state) ? Blocks.AIR : state.getBlock();
    }

    @Redirect(method = "tickBeacon(Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;)I", remap = false, require = 0, at = @At(value = "INVOKE", remap = false, target = "Lnet/minecraft/world/level/block/state/BlockState;getBeaconColorMultiplier(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)[F"))
    private static float[] buildscape$stopperColor(BlockState state, LevelReader level, BlockPos pos,
            BlockPos beaconPos) {
        return BeaconBeamStoppers.isStopper(state) ? null : state.getBeaconColorMultiplier(level, pos, beaconPos);
    }

    @Redirect(method = "tickBeacon(Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;)I", remap = false, require = 0, at = @At(value = "INVOKE", remap = true, target = "Lnet/minecraft/world/level/block/state/BlockState;getLightBlock(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)I"))
    private static int buildscape$stopperLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        if (BeaconBeamStoppers.isStopper(state)) {
            buildscape$quarkScan.get().markStopped();
            return 15;
        }
        return state.getLightBlock(level, pos);
    }

    @ModifyVariable(method = "tickBeacon(Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;)I", at = @At("LOAD"), index = 6, remap = false, require = 0)
    private static boolean buildscape$keepStoppedSections(boolean broke) {
        // Quark 3.2 stores 'broke' in slot 6 and reads it at the final append-or-clear decision.
        // Keeping that append includes the current segment, whose height excludes the stopper.
        return buildscape$quarkScan.get().shouldClearBeam(broke);
    }
}
