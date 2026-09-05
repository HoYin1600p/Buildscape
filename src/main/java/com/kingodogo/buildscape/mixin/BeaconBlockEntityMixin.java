package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.util.BeaconBeamHeightAccessor;
import com.kingodogo.buildscape.util.BeaconScanContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BeaconBlockEntity.BeaconBeamSection;
import net.minecraft.world.level.block.state.BlockState;
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
    private static final int BUILDSCAPE_UNLIMITED = 1024;

    @Unique
    private int buildscape$beamHeight = BUILDSCAPE_UNLIMITED;

    @Override
    public int buildscape$getBeamHeight() {
        return this.buildscape$beamHeight;
    }

    @Override
    public void buildscape$setBeamHeight(int height) {
        this.buildscape$beamHeight = height;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private static void buildscape$onTick(Level level, BlockPos pos, BlockState state, BeaconBlockEntity blockEntity, CallbackInfo ci) {
        BeaconScanContext.begin(blockEntity);
        if (((BeaconBlockEntityAccessor) blockEntity).getLastCheckY() < pos.getY()) {
            ((BeaconBeamHeightAccessor) blockEntity).buildscape$setBeamHeight(BUILDSCAPE_UNLIMITED);
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private static void buildscape$afterTick(Level level, BlockPos pos, BlockState state, BeaconBlockEntity blockEntity, CallbackInfo ci) {
        BeaconScanContext.end();
    }

    @Redirect(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;clear()V"
        )
    )
    private static void buildscape$interceptClear(List<?> list, Level level, BlockPos pos, BlockState state, BeaconBlockEntity blockEntity) {
        int height = ((BeaconBeamHeightAccessor) blockEntity).buildscape$getBeamHeight();
        if (height >= BUILDSCAPE_UNLIMITED) {
            list.clear();
            return;
        }

        if (!level.isClientSide) {
            list.clear();
            return;
        }

        if (list.isEmpty()) {
            @SuppressWarnings("unchecked")
            List<BeaconBeamSection> beamList = (List<BeaconBeamSection>) list;
            BeaconBeamSection section = new BeaconBeamSection(new float[]{1.0F, 1.0F, 1.0F});
            ((BeaconBeamSectionAccessor) (Object) section).setHeight(height);
            beamList.add(section);
        }
    }
}
