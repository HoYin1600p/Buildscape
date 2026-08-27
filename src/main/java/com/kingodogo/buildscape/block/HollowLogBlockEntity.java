package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.event.AdvancementEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.UUID;

public class HollowLogBlockEntity extends BlockEntity {
    private BlockState decorationState = Blocks.AIR.defaultBlockState();
    private BlockState glassCoverNeg = Blocks.AIR.defaultBlockState();
    private BlockState glassCoverPos = Blocks.AIR.defaultBlockState();
    private String fluidType = "none";
    private int lavaTicks = 0;
    private UUID lavaPlacedByPlayer = null;
    private UUID glassPlacedByPlayer = null;

    public HollowLogBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HOLLOW_LOG_BLOCK_ENTITY.get(), pos, state);
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

        if (state.hasProperty(HollowLogBlock.LAVA_LOGGED) && state.getValue(HollowLogBlock.LAVA_LOGGED)) {
            if (blockEntity.lavaTicks > 0) {
                blockEntity.lavaTicks--;
                if (blockEntity.lavaTicks <= 0) {
                    // Burn away log when timer expires!
                    level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.LAVA, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 10, 0.3, 0.3, 0.3, 0.1);
                        serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 5, 0.2, 0.2, 0.2, 0.05);
                    }

                    // Drop glass cover items if present
                    boolean hadGlass = false;
                    if (!blockEntity.glassCoverNeg.isAir()) {
                        Block.popResource(level, pos, new ItemStack(blockEntity.glassCoverNeg.getBlock()));
                        hadGlass = true;
                    }
                    if (!blockEntity.glassCoverPos.isAir()) {
                        Block.popResource(level, pos, new ItemStack(blockEntity.glassCoverPos.getBlock()));
                        hadGlass = true;
                    }

                    // Trigger achievement "No More Walking on Lava" strictly to the player who placed BOTH lava and glass
                    if (hadGlass && blockEntity.lavaPlacedByPlayer != null && blockEntity.lavaPlacedByPlayer.equals(blockEntity.glassPlacedByPlayer)) {
                        if (level.getServer() != null) {
                            ServerPlayer player = level.getServer().getPlayerList().getPlayer(blockEntity.lavaPlacedByPlayer);
                            if (player != null) {
                                AdvancementEvents.grant(player, "no_more_walking_on_lava");
                            }
                        }
                    }

                    // Replace with lava or fire
                    level.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
                }
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!decorationState.isAir()) {
            tag.put("DecorationState", NbtUtils.writeBlockState(decorationState));
        }
        if (!glassCoverNeg.isAir()) {
            tag.put("GlassCoverNeg", NbtUtils.writeBlockState(glassCoverNeg));
        }
        if (!glassCoverPos.isAir()) {
            tag.put("GlassCoverPos", NbtUtils.writeBlockState(glassCoverPos));
        }
        tag.putString("FluidType", fluidType);
        tag.putInt("LavaTicks", lavaTicks);
        if (lavaPlacedByPlayer != null) {
            tag.putUUID("LavaPlacedByPlayer", lavaPlacedByPlayer);
        }
        if (glassPlacedByPlayer != null) {
            tag.putUUID("GlassPlacedByPlayer", glassPlacedByPlayer);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("DecorationState")) {
            decorationState = NbtUtils.readBlockState(tag.getCompound("DecorationState"));
        } else {
            decorationState = Blocks.AIR.defaultBlockState();
        }
        if (tag.contains("GlassCoverNeg")) {
            glassCoverNeg = NbtUtils.readBlockState(tag.getCompound("GlassCoverNeg"));
        } else {
            glassCoverNeg = Blocks.AIR.defaultBlockState();
        }
        if (tag.contains("GlassCoverPos")) {
            glassCoverPos = NbtUtils.readBlockState(tag.getCompound("GlassCoverPos"));
        } else {
            glassCoverPos = Blocks.AIR.defaultBlockState();
        }
        fluidType = tag.getString("FluidType");
        lavaTicks = tag.getInt("LavaTicks");
        if (tag.hasUUID("LavaPlacedByPlayer")) {
            lavaPlacedByPlayer = tag.getUUID("LavaPlacedByPlayer");
        }
        if (tag.hasUUID("GlassPlacedByPlayer")) {
            glassPlacedByPlayer = tag.getUUID("GlassPlacedByPlayer");
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
