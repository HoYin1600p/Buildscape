package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.pipe.transport.HollowPipeTransportManager;
import com.kingodogo.buildscape.pipe.transport.PipeFlowState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import com.kingodogo.buildscape.block.entity.IBlockEntityReadData;
import com.kingodogo.buildscape.block.entity.IBlockEntityWriteData;
import com.kingodogo.buildscape.block.entity.IDataSerializable;
import com.kingodogo.buildscape.block.entity.DataBlockEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.UUID;
public class HollowLogBlockEntity extends DataBlockEntity implements IDataSerializable {
    private BlockState decorationState = Blocks.AIR.defaultBlockState();
    private BlockState glassCoverNeg = Blocks.AIR.defaultBlockState();
    private BlockState glassCoverPos = Blocks.AIR.defaultBlockState();
    private String fluidType = "none";
    private int lavaTicks = 0;
    private UUID lavaPlacedByPlayer = null;
    private UUID glassPlacedByPlayer = null;
    private PipeFlowState pipeFlowState = new PipeFlowState();
    private PipeFlowState pendingTargetState = null;
    private int flowDelayTicks = 0;

    public HollowLogBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HOLLOW_LOG_TYPE, pos, state);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if (level != null && !level.isClientSide()
                && (getBlockState().getBlock() instanceof HollowPipeBlock
                || getBlockState().getBlock() instanceof HollowLogBlock)) {
            HollowPipeTransportManager.markDirty(level, worldPosition);
        }
    }

    public PipeFlowState getPipeFlowState() {
        return pipeFlowState;
    }

    public void setPipeFlowState(PipeFlowState pipeFlowState) {
        this.pipeFlowState = pipeFlowState != null ? pipeFlowState : new PipeFlowState();
        this.pendingTargetState = null;
        this.flowDelayTicks = 0;
        setChanged();
        syncToClient();
    }

    public void setPendingFlowState(PipeFlowState target, int delayTicks) {
        PipeFlowState next = target != null ? target : new PipeFlowState();
        if (delayTicks > 0 && pendingTargetState != null && pendingTargetState.equals(next)) {
            return;
        }
        if (delayTicks <= 0) {
            this.pipeFlowState = next;
            this.pendingTargetState = null;
            this.flowDelayTicks = 0;
            setChanged();
            syncToClient();
        } else {
            this.pendingTargetState = next;
            this.flowDelayTicks = delayTicks;
            if (level != null && !level.isClientSide()) {
                level.scheduleTick(worldPosition, getBlockState().getBlock(), delayTicks);
            }
        }
    }

    public void applyPendingFlowState(Level level, BlockPos pos, BlockState state) {
        if (this.pendingTargetState != null) {
            this.pipeFlowState = this.pendingTargetState;
            this.pendingTargetState = null;
            this.flowDelayTicks = 0;
            setChanged();
            syncToClient();
        }
    }

    public BlockState getDecorationState() {
        return decorationState;
    }

    public void setDecorationState(BlockState state) {
        this.decorationState = state != null ? state : Blocks.AIR.defaultBlockState();
        setChanged();
        syncToClient();
    }

    public BlockState getGlassCoverNeg() {
        return glassCoverNeg;
    }

    public void setGlassCoverNeg(BlockState state) {
        this.glassCoverNeg = state != null ? state : Blocks.AIR.defaultBlockState();
        setChanged();
        syncToClient();
    }

    public BlockState getGlassCoverPos() {
        return glassCoverPos;
    }

    public void setGlassCoverPos(BlockState state) {
        this.glassCoverPos = state != null ? state : Blocks.AIR.defaultBlockState();
        setChanged();
        syncToClient();
    }

    public String getFluidType() {
        return fluidType;
    }

    public void setFluidType(String fluidType) {
        this.fluidType = fluidType != null ? fluidType : "none";
        setChanged();
        syncToClient();
    }

    public int getLavaTicks() {
        return lavaTicks;
    }

    public void setLavaTicks(int lavaTicks) {
        this.lavaTicks = lavaTicks;
        setChanged();
        syncToClient();
    }

    public UUID getLavaPlacedByPlayer() {
        return lavaPlacedByPlayer;
    }

    public void setLavaPlacedByPlayer(UUID uuid) {
        this.lavaPlacedByPlayer = uuid;
        setChanged();
    }

    public UUID getGlassPlacedByPlayer() {
        return glassPlacedByPlayer;
    }

    public void setGlassPlacedByPlayer(UUID uuid) {
        this.glassPlacedByPlayer = uuid;
        setChanged();
    }

    public void syncToClient() {
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, HollowLogBlockEntity blockEntity) {
        if (level == null || level.isClientSide()) {
            return;
        }

        HollowPipeTransportManager.tickLevel(level);

        if (blockEntity.pendingTargetState != null) {
            blockEntity.flowDelayTicks--;
            if (blockEntity.flowDelayTicks <= 0) {
                blockEntity.pipeFlowState = blockEntity.pendingTargetState;
                blockEntity.pendingTargetState = null;
                blockEntity.setChanged();
                blockEntity.syncToClient();
            }
        }

        if (state.hasProperty(HollowLogBlock.LAVA_LOGGED) && state.getValue(HollowLogBlock.LAVA_LOGGED)) {
            if (blockEntity.lavaTicks > 0) {
                blockEntity.lavaTicks--;
                if (blockEntity.lavaTicks <= 0) {
                    Services.PLATFORM.playFireExtinguish(level, pos);
                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.LAVA, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 10, 0.3, 0.3, 0.3, 0.1);
                        serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 5, 0.2, 0.2, 0.2, 0.05);
                    }

                    if (!blockEntity.glassCoverNeg.isAir()) {
                        Block.popResource(level, pos, new ItemStack(blockEntity.glassCoverNeg.getBlock()));
                    }
                    if (!blockEntity.glassCoverPos.isAir()) {
                        Block.popResource(level, pos, new ItemStack(blockEntity.glassCoverPos.getBlock()));
                    }
                    blockEntity.glassCoverNeg = Blocks.AIR.defaultBlockState();
                    blockEntity.glassCoverPos = Blocks.AIR.defaultBlockState();

                    level.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
                }
            }
        }
    }

    @Override
    public void writeData(IBlockEntityWriteData data) {
        if (!decorationState.isAir()) {
            data.putBlockState("DecorationState", decorationState);
        }
        if (!glassCoverNeg.isAir()) {
            data.putBlockState("GlassCoverNeg", glassCoverNeg);
        }
        if (!glassCoverPos.isAir()) {
            data.putBlockState("GlassCoverPos", glassCoverPos);
        }
        data.putString("FluidType", fluidType);
        data.putInt("LavaTicks", lavaTicks);
        if (lavaPlacedByPlayer != null) {
            data.putUUID("LavaPlacedByPlayer", lavaPlacedByPlayer);
        }
        if (glassPlacedByPlayer != null) {
            data.putUUID("GlassPlacedByPlayer", glassPlacedByPlayer);
        }
        if (pipeFlowState != null && !pipeFlowState.isEmpty()) {
            IBlockEntityWriteData child = data.createChild();
            pipeFlowState.writeData(child);
            data.putSubData("PipeFlowState", child);
        }
    }

    @Override
    public void readData(IBlockEntityReadData data) {
        PipeFlowState previousFlow = pipeFlowState;
        decorationState = data.getBlockStateOrAir("DecorationState");
        glassCoverNeg = data.getBlockStateOrAir("GlassCoverNeg");
        glassCoverPos = data.getBlockStateOrAir("GlassCoverPos");
        fluidType = data.getStringOr("FluidType", "");
        lavaTicks = data.getIntOr("LavaTicks", 0);
        lavaPlacedByPlayer = data.getUUIDOrNull("LavaPlacedByPlayer");
        glassPlacedByPlayer = data.getUUIDOrNull("GlassPlacedByPlayer");
        if (getBlockState().getBlock() instanceof HollowPipeBlock) {
            pipeFlowState = data.getSubData("PipeFlowState")
                    .map(PipeFlowState::readData)
                    .orElseGet(PipeFlowState::new);
        } else {
            pipeFlowState = new PipeFlowState();
        }
        if (level != null && level.isClientSide()
                && (getBlockState().getBlock() instanceof HollowPipeBlock
                || getBlockState().getBlock() instanceof HollowLogBlock)
                && !previousFlow.equals(pipeFlowState)) {
            for (Direction direction : Direction.values()) {
                if (direction == Direction.UP) continue;
                BlockPos neighbor = worldPosition.relative(direction);
                BlockState neighborState = level.getBlockState(neighbor);
                level.sendBlockUpdated(neighbor, neighborState, neighborState, Block.UPDATE_CLIENTS);
            }
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
