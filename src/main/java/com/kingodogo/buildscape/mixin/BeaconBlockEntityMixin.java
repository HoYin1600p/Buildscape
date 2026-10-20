package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixin.accessor.BeaconBlockEntityAccessor;
import com.kingodogo.buildscape.util.BeaconBeamHeightAccessor;
import com.kingodogo.buildscape.util.BeaconBeamScanState;
import com.kingodogo.buildscape.util.BeaconBeamStoppers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BeaconBlockEntity.class)
public class BeaconBlockEntityMixin implements BeaconBeamHeightAccessor {

    @Unique
    private final BeaconBeamScanState buildscape$beamScanState = new BeaconBeamScanState();

    @Override
    public BeaconBeamScanState buildscape$getBeamScanState() {
        return this.buildscape$beamScanState;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private static void buildscape$onTick(Level level, BlockPos pos, BlockState state, BeaconBlockEntity blockEntity,
            CallbackInfo ci) {
        if (((BeaconBlockEntityAccessor) blockEntity).getLastCheckY() < pos.getY()) {
            ((BeaconBeamHeightAccessor) blockEntity).buildscape$getBeamScanState().beginScan();
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getBeaconColorMultiplier(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)[F", remap = false))
    private static float[] buildscape$stopperColor(BlockState state, LevelReader level, BlockPos pos,
            BlockPos beaconPos) {
        return BeaconBeamStoppers.isStopper(state) ? null : state.getBeaconColorMultiplier(level, pos, beaconPos);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getLightBlock(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)I"))
    private static int buildscape$stopperLightBlock(BlockState state, BlockGetter world, BlockPos scanPos,
            Level level, BlockPos pos, BlockState beaconState, BeaconBlockEntity blockEntity) {
        if (BeaconBeamStoppers.isStopper(state)) {
            ((BeaconBeamHeightAccessor) blockEntity).buildscape$getBeamScanState().markStopped();
            return 15;
        }
        return state.getLightBlock(world, scanPos);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
    private static boolean buildscape$bedrockExemption(BlockState state, Block block,
            Level level, BlockPos pos, BlockState beaconState, BeaconBlockEntity blockEntity) {
        // A pack may opt bedrock into the stopper tag too.
        return !((BeaconBeamHeightAccessor) blockEntity).buildscape$getBeamScanState().pendingStopped()
                && state.is(block);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/List;clear()V"))
    private static void buildscape$keepStoppedSections(List<?> sections,
            Level level, BlockPos pos, BlockState state, BeaconBlockEntity blockEntity) {
        if (!((BeaconBeamHeightAccessor) blockEntity).buildscape$getBeamScanState().pendingStopped()) {
            sections.clear();
        }
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;beamSections:Ljava/util/List;", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private static void buildscape$confirmStoppedSections(Level level, BlockPos pos, BlockState state,
            BeaconBlockEntity blockEntity,
            CallbackInfo ci) {
        ((BeaconBeamHeightAccessor) blockEntity).buildscape$getBeamScanState().completeScan();
    }

}
