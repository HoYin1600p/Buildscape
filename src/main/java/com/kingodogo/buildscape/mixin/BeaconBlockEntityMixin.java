package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.util.BeaconBeamHeightAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
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
    private int buildscape$beamHeight = 1024;

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
        int j = pos.getY();
        if (((BeaconBlockEntityAccessor) blockEntity).getLastCheckY() < j) {
            // New scan starting, reset the beam height
            ((BeaconBeamHeightAccessor) blockEntity).buildscape$setBeamHeight(1024);
        }
    }

    @Unique
    private static boolean buildscape$isTintedGlass(BlockState state) {
        if (state.is(Blocks.TINTED_GLASS)) {
            return true;
        }
        net.minecraft.resources.ResourceLocation key = state.getBlock().getRegistryName();
        return key != null && (key.getPath().contains("tinted_glass") || key.getPath().equals("tinted_glass"));
    }

    @Redirect(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;clear()V"
        )
    )
    private static void buildscape$interceptClear(List<?> list, Level level, BlockPos pos, BlockState state, BeaconBlockEntity blockEntity) {
        int x = pos.getX();
        int z = pos.getZ();
        int startY = pos.getY() + 1;
        
        BlockPos.MutableBlockPos scanPos = new BlockPos.MutableBlockPos(x, startY, z);
        BlockState blockingState = null;
        int blockedY = -1;
        
        for (int y = startY; y < level.getMaxBuildHeight(); y++) {
            scanPos.set(x, y, z);
            BlockState blockState = level.getBlockState(scanPos);
            float[] colors = blockState.getBeaconColorMultiplier(level, scanPos, pos);
            if (colors == null) {
                if (blockState.getLightBlock(level, scanPos) >= 15 && !blockState.is(Blocks.BEDROCK)) {
                    blockingState = blockState;
                    blockedY = y;
                    break;
                }
            }
        }
        
        if (blockingState != null && buildscape$isTintedGlass(blockingState)) {
            int height = blockedY - pos.getY() - 1;
            ((BeaconBeamHeightAccessor) blockEntity).buildscape$setBeamHeight(height);
            
            if (list.isEmpty()) {
                @SuppressWarnings("unchecked")
                List<BeaconBeamSection> beamList = (List<BeaconBeamSection>) list;
                try {
                    BeaconBeamSection section = new BeaconBeamSection(new float[]{1.0f, 1.0f, 1.0f});
                    java.lang.reflect.Field heightField = BeaconBeamSection.class.getDeclaredField("height");
                    heightField.setAccessible(true);
                    heightField.setInt(section, height);
                    beamList.add(section);
                } catch (Exception e) {
                    BuildScape.getLogger().error("Failed to set BeaconBeamSection height via reflection", e);
                }
            }
            return;
        }
        
        ((BeaconBeamHeightAccessor) blockEntity).buildscape$setBeamHeight(1024);
        list.clear();
    }
}
