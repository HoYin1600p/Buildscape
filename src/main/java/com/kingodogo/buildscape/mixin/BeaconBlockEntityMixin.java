package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.util.BeaconBeamHeightAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BeaconBlockEntity.BeaconBeamSection;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
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
        if (((BeaconBlockEntityAccessor) blockEntity).getLastCheckY() < pos.getY()) {
            int height = buildscape$scanForTintedGlass(level, pos);
            ((BeaconBeamHeightAccessor) blockEntity).buildscape$setBeamHeight(height);
        }
    }

    @Unique
    private static int buildscape$scanForTintedGlass(Level level, BlockPos pos) {
        int x = pos.getX();
        int z = pos.getZ();
        int top = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
        BlockPos.MutableBlockPos scanPos = new BlockPos.MutableBlockPos();

        for (int y = pos.getY() + 1; y <= top; y++) {
            scanPos.set(x, y, z);
            BlockState blockState = level.getBlockState(scanPos);
            if (blockState.getBeaconColorMultiplier(level, scanPos, pos) != null) {
                continue;
            }
            if (blockState.getLightBlock(level, scanPos) >= 15 && !blockState.is(Blocks.BEDROCK)) {
                return buildscape$isTintedGlass(blockState) ? y - pos.getY() : BUILDSCAPE_UNLIMITED;
            }
        }
        return BUILDSCAPE_UNLIMITED;
    }

    @Unique
    private static boolean buildscape$isTintedGlass(BlockState state) {
        if (state.is(Blocks.TINTED_GLASS)) {
            return true;
        }
        ResourceLocation key = state.getBlock().getRegistryName();
        return key != null && key.getPath().contains("tinted_glass");
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

        if (list.isEmpty()) {
            @SuppressWarnings("unchecked")
            List<BeaconBeamSection> beamList = (List<BeaconBeamSection>) list;
            BeaconBeamSection section = new BeaconBeamSection(new float[]{1.0F, 1.0F, 1.0F});
            ((BeaconBeamSectionAccessor) (Object) section).setHeight(height);
            beamList.add(section);
        }
    }
}
