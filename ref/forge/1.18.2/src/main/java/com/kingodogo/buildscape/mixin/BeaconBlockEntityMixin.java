package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.util.BeaconBeamHeightAccessor;
import com.kingodogo.buildscape.util.BeaconBeamScanState;
import com.kingodogo.buildscape.util.BeaconScanContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeaconBlockEntity.class)
public class BeaconBlockEntityMixin implements BeaconBeamHeightAccessor {

    @Unique
    private final BeaconBeamScanState buildscape$beamScanState = new BeaconBeamScanState();

    @Override
    public int buildscape$getBeamHeight() {
        return this.buildscape$beamScanState.confirmedHeight();
    }

    @Override
    public void buildscape$beginBeamScan() {
        this.buildscape$beamScanState.beginScan();
    }

    @Override
    public void buildscape$markBeamBlocked(int height) {
        this.buildscape$beamScanState.markBlocked(height);
    }

    @Override
    public void buildscape$completeBeamScan() {
        this.buildscape$beamScanState.completeScan();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private static void buildscape$onTick(Level level, BlockPos pos, BlockState state, BeaconBlockEntity blockEntity, CallbackInfo ci) {
        BeaconScanContext.begin(blockEntity);
        if (((BeaconBlockEntityAccessor) blockEntity).getLastCheckY() < pos.getY()) {
            ((BeaconBeamHeightAccessor) blockEntity).buildscape$beginBeamScan();
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private static void buildscape$afterTick(Level level, BlockPos pos, BlockState state, BeaconBlockEntity blockEntity, CallbackInfo ci) {
        if (((BeaconBlockEntityAccessor) blockEntity).getLastCheckY() < pos.getY()) {
            BeaconBeamHeightAccessor heightAccessor = (BeaconBeamHeightAccessor) blockEntity;
            heightAccessor.buildscape$completeBeamScan();
            BeaconScanContext.confirm(level, pos, heightAccessor.buildscape$getBeamHeight());
        }
        BeaconScanContext.end();
    }

}
