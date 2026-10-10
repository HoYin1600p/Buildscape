package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class MuffBlockEntity extends BlockEntity {

    public MuffBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MUFF_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.level != null && this.level.isClientSide) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.register(this.worldPosition));
        }
    }

    @Override
    public void setRemoved() {
        if (this.level != null && this.level.isClientSide) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.unregister(this.worldPosition));
        }
        super.setRemoved();
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if (this.level != null && this.level.isClientSide) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.unregister(this.worldPosition));
        }
    }

    private static class ClientHandler {
        private static void register(BlockPos pos) {
            com.kingodogo.buildscape.client.MuffBlockManager.register(pos);
        }

        private static void unregister(BlockPos pos) {
            com.kingodogo.buildscape.client.MuffBlockManager.unregister(pos);
        }
    }
}
