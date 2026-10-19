package com.kingodogo.buildscape.feature.pipe;

import com.kingodogo.buildscape.core.NbtKeys;
import com.kingodogo.buildscape.feature.pipe.transport.HollowPipeTransportManager;
import com.kingodogo.buildscape.feature.pipe.transport.PipeFlowState;
import com.kingodogo.buildscape.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;
import javax.annotation.Nullable;

public class HollowLogBlockEntity extends BlockEntity {
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
        super(ModBlockEntities.HOLLOW_LOG_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide
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
            if (level != null && !level.isClientSide) {
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
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, HollowLogBlockEntity blockEntity) {
        if (level == null || level.isClientSide) {
            return;
        }

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
                if (blockEntity.lavaTicks > 0 && blockEntity.lavaTicks <= 100 && blockEntity.lavaTicks % 20 == 0) {
                    level.playSound(null, pos, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 0.8F, 1.0F);
                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, 4, 0.3, 0.2, 0.3, 0.02);
                    }
                }
                if (blockEntity.lavaTicks <= 0) {
                    level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.LAVA, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 10, 0.3, 0.3, 0.3, 0.1);
                        serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 5, 0.2, 0.2, 0.2, 0.05);
                    }

                    boolean hadGlass = false;
                    if (!blockEntity.glassCoverNeg.isAir()) {
                        Block.popResource(level, pos, new ItemStack(blockEntity.glassCoverNeg.getBlock()));
                        hadGlass = true;
                    }
                    if (!blockEntity.glassCoverPos.isAir()) {
                        Block.popResource(level, pos, new ItemStack(blockEntity.glassCoverPos.getBlock()));
                        hadGlass = true;
                    }
                    blockEntity.glassCoverNeg = Blocks.AIR.defaultBlockState();
                    blockEntity.glassCoverPos = Blocks.AIR.defaultBlockState();

                    level.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
                }
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!decorationState.isAir()) {
            tag.put(NbtKeys.DECORATION_STATE, NbtUtils.writeBlockState(decorationState));
        }
        if (!glassCoverNeg.isAir()) {
            tag.put(NbtKeys.GLASS_COVER_NEG, NbtUtils.writeBlockState(glassCoverNeg));
        }
        if (!glassCoverPos.isAir()) {
            tag.put(NbtKeys.GLASS_COVER_POS, NbtUtils.writeBlockState(glassCoverPos));
        }
        tag.putString("FluidType", fluidType);
        tag.putInt("LavaTicks", lavaTicks);
        if (lavaPlacedByPlayer != null) {
            tag.putUUID(NbtKeys.LAVA_PLACED_BY_PLAYER, lavaPlacedByPlayer);
        }
        if (glassPlacedByPlayer != null) {
            tag.putUUID(NbtKeys.GLASS_PLACED_BY_PLAYER, glassPlacedByPlayer);
        }
        if (pipeFlowState != null && !pipeFlowState.isEmpty()) {
            tag.put(NbtKeys.PIPE_FLOW_STATE, pipeFlowState.writeToNbt(new CompoundTag()));
        }
    }

    @Override
    public void load(CompoundTag tag) {
        PipeFlowState previousFlow = pipeFlowState;
        super.load(tag);
        if (tag.contains(NbtKeys.DECORATION_STATE)) {
            decorationState = NbtUtils.readBlockState(tag.getCompound(NbtKeys.DECORATION_STATE));
        } else {
            decorationState = Blocks.AIR.defaultBlockState();
        }
        if (tag.contains(NbtKeys.GLASS_COVER_NEG)) {
            glassCoverNeg = NbtUtils.readBlockState(tag.getCompound(NbtKeys.GLASS_COVER_NEG));
        } else {
            glassCoverNeg = Blocks.AIR.defaultBlockState();
        }
        if (tag.contains(NbtKeys.GLASS_COVER_POS)) {
            glassCoverPos = NbtUtils.readBlockState(tag.getCompound(NbtKeys.GLASS_COVER_POS));
        } else {
            glassCoverPos = Blocks.AIR.defaultBlockState();
        }
        fluidType = tag.getString("FluidType");
        lavaTicks = tag.getInt("LavaTicks");
        if (tag.hasUUID(NbtKeys.LAVA_PLACED_BY_PLAYER)) {
            lavaPlacedByPlayer = tag.getUUID(NbtKeys.LAVA_PLACED_BY_PLAYER);
        }
        if (tag.hasUUID(NbtKeys.GLASS_PLACED_BY_PLAYER)) {
            glassPlacedByPlayer = tag.getUUID(NbtKeys.GLASS_PLACED_BY_PLAYER);
        }
        if (getBlockState().getBlock() instanceof HollowPipeBlock && tag.contains(NbtKeys.PIPE_FLOW_STATE)) {
            pipeFlowState = PipeFlowState.readFromNbt(tag.getCompound(NbtKeys.PIPE_FLOW_STATE));
        } else {
            pipeFlowState = new PipeFlowState();
        }
        if (level != null && level.isClientSide
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

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
