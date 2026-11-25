package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.block.entity.IBlockEntityReadData;
import com.kingodogo.buildscape.block.entity.IBlockEntityWriteData;
import com.kingodogo.buildscape.block.entity.IDataSerializable;
import com.kingodogo.buildscape.block.entity.DataBlockEntity;
import com.kingodogo.buildscape.particle.ModParticles;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Random;
public class CascadeBlockEntity extends DataBlockEntity implements IDataSerializable {

    private static final Random RANDOM = new Random();
    private int particleLevel = 5;

    public CascadeBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
        super(ModBlockEntities.CASCADE_TYPE, worldPosition, blockState);
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
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
        return particleLevel;
    }

    @Override
    public void readData(IBlockEntityReadData data) {
        this.particleLevel = data.getIntOr("ParticleLevel", 5);
    }

    @Override
    public void writeData(IBlockEntityWriteData data) {
        data.putInt("ParticleLevel", getParticleLevel());
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if (level != null && !level.isClientSide()) {
            CascadeWaterManager.registerWaterTicket(level, this.worldPosition);
        }
    }

    @Override
    public void setRemoved() {
        if (this.level != null && !this.level.isClientSide()) {
            CascadeWaterManager.removeWaterTicket(this.level, this.worldPosition);
        }
        super.setRemoved();
    }
    public static void clientTick(Level level, BlockPos pos, BlockState state, CascadeBlockEntity be) {
        if (level == null || !level.isClientSide()) return;

        int particleSetting = Services.PLATFORM.getClientParticleSetting();
        if (particleSetting == 2) {
            if (level.getGameTime() % 10 != 0) return;
        } else if (particleSetting == 1 && level.getGameTime() % 2 != 0) {
            return;
        }

        Player player = Services.PLATFORM.getClientPlayer();
        if (player != null && isMistSuppressor(player.getOffhandItem())) {
            int playerChunkX = player.blockPosition().getX() >> 4;
            int playerChunkZ = player.blockPosition().getZ() >> 4;
            int blockChunkX = pos.getX() >> 4;
            int blockChunkZ = pos.getZ() >> 4;
            if (playerChunkX == blockChunkX && playerChunkZ == blockChunkZ) return;
        }

        double levelFactor = be.getParticleLevel() * 0.2;
        int rawBase = 5 + RANDOM.nextInt(3);
        int count;
        if (particleSetting == 2) count = 1;
        else if (particleSetting == 1) count = Math.max(1, (int) Math.round(rawBase * 0.5 * levelFactor));
        else count = Math.max(1, (int) Math.round(rawBase * levelFactor));

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
        if (stack.getItem() instanceof com.kingodogo.buildscape.item.BottleOfMistItem) return true;
        return stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof CascadeBlock;
    }
}
