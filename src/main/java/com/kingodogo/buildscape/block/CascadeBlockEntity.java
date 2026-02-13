package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class CascadeBlockEntity extends BlockEntity {

    private static final Random RANDOM = new Random();

    public CascadeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CASCADE_BLOCK_ENTITY.get(), pos, state);
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

    @OnlyIn(Dist.CLIENT)
    public static void clientTick(Level level, BlockPos pos, BlockState state, CascadeBlockEntity be) {
        // Respect Minecraft's particle settings for performance
        net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
        net.minecraft.client.ParticleStatus particleSetting = minecraft.options.particles;
        if (particleSetting == net.minecraft.client.ParticleStatus.MINIMAL) {
            // Minimal: only spawn 1 particle every 10 ticks
            if (level.getGameTime() % 10 != 0) return;
        } else if (particleSetting == net.minecraft.client.ParticleStatus.DECREASED) {
            // Decreased: skip every other tick
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

        int baseCount = 5 + RANDOM.nextInt(3);
        int count;
        if (particleSetting == net.minecraft.client.ParticleStatus.MINIMAL) {
            count = 1;
        } else if (particleSetting == net.minecraft.client.ParticleStatus.DECREASED) {
            count = Math.max(1, baseCount / 2);
        } else {
            count = baseCount;
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

    @OnlyIn(Dist.CLIENT)
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
