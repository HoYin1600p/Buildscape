package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.block.sculk.SculkSpreader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;

import javax.annotation.Nullable;

public class SculkCatalystBlockEntity extends BlockEntity implements GameEventListener {
    private final PositionSource listenerSource;
    private final SculkSpreader spreader = new SculkSpreader();

    public SculkCatalystBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SCULK_CATALYST_BLOCK_ENTITY.get(), pos, state);
        listenerSource = new BlockPositionSource(pos);
    }

    @Override
    public PositionSource getListenerSource() {
        return listenerSource;
    }

    @Override
    public int getListenerRadius() {
        return 8;
    }

    @Override
    public boolean handleGameEvent(Level level, GameEvent event, @Nullable Entity entity, BlockPos pos) {
        return !level.isClientSide && event == GameEvent.ENTITY_KILLED
                && SculkCatalystHandler.offerDeath(this, entity, pos);
    }

    public void absorbExperience(BlockPos deathPos, int experience) {
        if (!(level instanceof ServerLevel serverLevel) || experience <= 0) return;
        spreader.addCursors(deathPos, experience);
        BlockState state = getBlockState();
        serverLevel.setBlock(worldPosition, state.setValue(SculkCatalystBlock.BLOOM, true), 3);
        serverLevel.scheduleTick(worldPosition, state.getBlock(), 8);
        serverLevel.sendParticles(ParticleTypes.SOUL, worldPosition.getX() + 0.5D,
                worldPosition.getY() + 1.15D, worldPosition.getZ() + 0.5D, 2, 0.2D, 0.0D, 0.2D, 0.0D);
        serverLevel.playSound(null, worldPosition, SoundEvents.SCULK_CLICKING, SoundSource.BLOCKS,
                2.0F, 0.6F + serverLevel.random.nextFloat() * 0.4F);
        setChanged();
    }

    public void serverTick(ServerLevel level) {
        if (spreader.updateCursors(level, worldPosition)) setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        spreader.save(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        spreader.load(tag);
    }
}
