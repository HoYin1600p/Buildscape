package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class CascadeBlockEntity extends BlockEntity {

    private static final Random RANDOM = new Random();
    private int particleLevel = 5; // 1 = 20%, 2 = 40%, 3 = 60%, 4 = 80%, 5 = 100%

    public CascadeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CASCADE_BLOCK_ENTITY.get(), pos, state);
    }

    public int getParticleLevel() {
        if (particleLevel < 1 || particleLevel > 5) {
            particleLevel = 5;
        }
        return particleLevel;
    }

    public int cycleParticleLevel() {
        particleLevel = (getParticleLevel() % 5) + 1;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
        return particleLevel;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("ParticleLevel", getParticleLevel());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("ParticleLevel")) {
            this.particleLevel = tag.getInt("ParticleLevel");
        } else {
            this.particleLevel = 5;
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.level != null && !this.level.isClientSide) {
            CascadeWaterManager.registerWaterTicket(this.level, this.worldPosition);
        }
    }

    @Override
    public void setRemoved() {
        if (this.level != null && !this.level.isClientSide) {
            CascadeWaterManager.removeWaterTicket(this.level, this.worldPosition);
        }
        super.setRemoved();
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, CascadeBlockEntity be) {
        if (level != null && level.isClientSide) {
            ClientHandler.tick(level, pos, state, be);
        }
    }

    private static class ClientHandler {
        private static void tick(Level level, BlockPos pos, BlockState state, CascadeBlockEntity be) {
            // Respect Minecraft's particle settings for performance
            net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
            net.minecraft.client.ParticleStatus particleSetting = minecraft.options.particles;
            if (particleSetting == net.minecraft.client.ParticleStatus.MINIMAL) {
                if (level.getGameTime() % 10 != 0) return;
            } else if (particleSetting == net.minecraft.client.ParticleStatus.DECREASED) {
                if (level.getGameTime() % 2 != 0) return;
            }

            // If local player holds cascade block or bottle of mist in OFF-HAND, suppress particles in their chunk
            Player player = minecraft.player;
            if (player != null) {
                if (isMistSuppressor(player.getOffhandItem())) {
                    int playerChunkX = player.blockPosition().getX() >> 4;
                    int playerChunkZ = player.blockPosition().getZ() >> 4;
                    int blockChunkX = pos.getX() >> 4;
                    int blockChunkZ = pos.getZ() >> 4;
                    if (playerChunkX == blockChunkX && playerChunkZ == blockChunkZ) {
                        return;
                    }
                }
            }

            double levelFactor = be.getParticleLevel() * 0.2;
            int rawBase = 5 + RANDOM.nextInt(3);
            int count;
            if (particleSetting == net.minecraft.client.ParticleStatus.MINIMAL) {
                count = 1;
            } else if (particleSetting == net.minecraft.client.ParticleStatus.DECREASED) {
                count = Math.max(1, (int) Math.round(rawBase * 0.5 * levelFactor));
            } else {
                count = Math.max(1, (int) Math.round(rawBase * levelFactor));
            }

            for (int i = 0; i < count; i++) {
                double x = (double) pos.getX() + 0.5 + (RANDOM.nextDouble() - 0.5) * 2.0;
                double y = (double) pos.getY() + 1.75 + (RANDOM.nextDouble() - 0.5) * 0.1;
                double z = (double) pos.getZ() + 0.5 + (RANDOM.nextDouble() - 0.5) * 2.0;

                double xSpeed = (RANDOM.nextDouble() - 0.5) * 0.15;
                double ySpeed = RANDOM.nextDouble() * 0.01;
                double zSpeed = (RANDOM.nextDouble() - 0.5) * 0.15;

                level.addAlwaysVisibleParticle(ModParticles.CASCADE.get(), true, x, y, z, xSpeed, ySpeed, zSpeed);
            }
        }

        private static boolean isMistSuppressor(ItemStack stack) {
            if (stack.isEmpty()) return false;
            net.minecraft.world.item.Item item = stack.getItem();
            if (item instanceof com.kingodogo.buildscape.item.BottleOfMistItem) return true;
            if (item instanceof BlockItem blockItem) {
                return blockItem.getBlock() instanceof CascadeBlock;
            }
            return false;
        }
    }
}
