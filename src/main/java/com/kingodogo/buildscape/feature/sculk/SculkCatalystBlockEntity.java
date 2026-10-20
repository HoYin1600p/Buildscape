package com.kingodogo.buildscape.feature.sculk;

import com.kingodogo.buildscape.registry.ModBlockEntities;
import com.kingodogo.buildscape.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
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
    private boolean loggedServerTick;

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
        if (level.isClientSide || event != GameEvent.ENTITY_KILLED) {
            return false;
        }
        SculkCatalystHandler.debug("listener received death at {} catalyst={} in {}", pos, worldPosition,
                level.dimension().location());
        return SculkCatalystHandler.offerDeath(this, entity, pos);
    }

    public void absorbExperience(BlockPos deathPos, int experience) {
        if (!(level instanceof ServerLevel serverLevel) || experience <= 0) {
            return;
        }
        spreader.addCursors(deathPos, experience);
        BlockState state = getBlockState();
        serverLevel.setBlock(worldPosition, state.setValue(SculkCatalystBlock.BLOOM, true), 3);
        serverLevel.scheduleTick(worldPosition, state.getBlock(), 8);
        serverLevel.sendParticles(ParticleTypes.SOUL, worldPosition.getX() + 0.5,
                worldPosition.getY() + 1.15, worldPosition.getZ() + 0.5, 2, 0.2, 0.0, 0.2, 0.0);
        serverLevel.playSound(null, worldPosition, ModSounds.SCULK_CATALYST_BLOOM.get(), SoundSource.BLOCKS,
                2.0F, 0.6F + serverLevel.random.nextFloat() * 0.4F);
        setChanged();
    }

    public void serverTick(ServerLevel level) {
        if (!loggedServerTick) {
            SculkCatalystHandler.debug("catalyst ticking at {} in {}", worldPosition, level.dimension().location());
            loggedServerTick = true;
        }
        if (spreader.updateCursors(level, worldPosition)) {
            setChanged();
        }
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
