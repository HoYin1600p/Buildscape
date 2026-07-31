package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.particle.ModParticles;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ConfettiItem extends Item {
    
    public ConfettiItem(Item.Properties properties) {
        super(properties);
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        
        // Play sounds and spawn particles on server-side
        if (!level.isClientSide) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), 
                SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 0.8F, 1.4F);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), 
                SoundEvents.FIREWORK_ROCKET_TWINKLE, SoundSource.PLAYERS, 0.6F, 1.6F);
            
            spawnConfettiParticles((ServerLevel) level, player);

            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
        }
        
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }
    
    private void spawnConfettiParticles(ServerLevel level, Player player) {
        net.minecraft.world.phys.Vec3 look = player.getLookAngle();
        double startX = player.getX() + look.x * 0.9D;
        double startY = player.getEyeY() + look.y * 0.9D;
        double startZ = player.getZ() + look.z * 0.9D;
        
        // Explosive burst of 75-120 confetti particles across a wide cone
        int particleCount = 75 + level.random.nextInt(46);
        
        for (int i = 0; i < particleCount; i++) {
            double speed = 0.15D + level.random.nextDouble() * 0.25D;
            double spread = 0.30D + level.random.nextDouble() * 0.35D;
            
            double vx = look.x * speed + (level.random.nextDouble() - 0.5D) * spread;
            double vy = look.y * speed + (level.random.nextDouble() - 0.5D) * spread + 0.12D;
            double vz = look.z * speed + (level.random.nextDouble() - 0.5D) * spread;
            
            double px = startX + (level.random.nextDouble() - 0.5D) * 0.4D;
            double py = startY + (level.random.nextDouble() - 0.5D) * 0.4D;
            double pz = startZ + (level.random.nextDouble() - 0.5D) * 0.4D;
            
            level.sendParticles((SimpleParticleType) ModParticles.CONFETTI.get(), 
                px, py, pz, 0, vx, vy, vz, 1.0D);
        }
    }
}

