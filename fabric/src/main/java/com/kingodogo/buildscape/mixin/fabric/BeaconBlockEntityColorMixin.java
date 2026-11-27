package com.kingodogo.buildscape.mixin.fabric;

import com.kingodogo.buildscape.util.BeaconScanContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Forge and NeoForge ask each scanned block for its beam colour; Fabric has no such hook, so the scan's dye lookup is
 * redirected here. BuildScape's colouring blocks implement BeaconBeamBlock so the scan reaches that lookup, and
 * {@link BeaconScanContext#beamColor} supplies their real colour (and tinted blocking).
 */
@Mixin(BeaconBlockEntity.class)
public abstract class BeaconBlockEntityColorMixin {
    private static final ThreadLocal<Level> buildscape$scanLevel = new ThreadLocal<>();
    private static final ThreadLocal<BlockPos> buildscape$scanPos = new ThreadLocal<>();

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private static BlockState buildscape$trackScannedBlock(Level level, BlockPos pos) {
        buildscape$scanLevel.set(level);
        buildscape$scanPos.set(pos);
        return level.getBlockState(pos);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/DyeColor;getTextureDiffuseColor()I"))
    private static int buildscape$beamColor(DyeColor dye) {
        Level level = buildscape$scanLevel.get();
        BlockPos pos = buildscape$scanPos.get();
        BeaconBlockEntity beacon = BeaconScanContext.activeBeacon();
        if (level != null && pos != null && beacon != null) {
            Integer rgb = BeaconScanContext.beamColor(level.getBlockState(pos), level, pos, beacon.getBlockPos());
            if (rgb != null) return 0xFF000000 | rgb;
        }
        return dye.getTextureDiffuseColor();
    }
}
