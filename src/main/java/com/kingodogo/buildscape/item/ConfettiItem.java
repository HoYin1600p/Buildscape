package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.particle.ModParticles;
import net.minecraft.core.particles.SimpleParticleType;
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
        
        // Server-side: consume item and play sound
        if (!level.isClientSide) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), 
                SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.PLAYERS, 0.5F, 1.2F);
            
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
        }
        
        // Spawn particles on client-side (particles are client-side only)
        if (level.isClientSide) {
            spawnConfettiParticles(level, player);
        }
        
        player.getCooldowns().addCooldown(this, 5); // 5 tick cooldown
        
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }
    
    private void spawnConfettiParticles(Level level, Player player) {
        // Spawn confetti particles in a burst pattern
        double x = player.getX();
        double y = player.getY() + player.getEyeHeight();
        double z = player.getZ();
        
        // Get player's look direction for directional burst
        float yaw = player.getYRot() * (float)(Math.PI / 180.0);
        float pitch = player.getXRot() * (float)(Math.PI / 180.0);
        
        // Spawn 30-50 confetti particles in a burst
        int particleCount = 30 + level.random.nextInt(21);
        
        for (int i = 0; i < particleCount; i++) {
            // Random direction in a cone shape
            double spread = level.random.nextDouble() * 0.5 + 0.3; // Spread angle
            double speed = 0.3 + level.random.nextDouble() * 0.4; // Random speed
            
            // Calculate velocity based on player's look direction with spread
            double vx = Math.sin(pitch) * Math.cos(yaw) * speed + 
                       (level.random.nextDouble() - 0.5) * spread;
            double vy = -Math.cos(pitch) * speed + 
                       (level.random.nextDouble() - 0.5) * spread + 0.2; // Slight upward bias
            double vz = Math.sin(pitch) * Math.sin(yaw) * speed + 
                       (level.random.nextDouble() - 0.5) * spread;
            
            // Add some randomness to position
            double px = x + (level.random.nextDouble() - 0.5) * 0.3;
            double py = y + (level.random.nextDouble() - 0.5) * 0.3;
            double pz = z + (level.random.nextDouble() - 0.5) * 0.3;
            
            // Spawn the particle
            level.addParticle(ModParticles.CONFETTI.get(),
                px, py, pz, vx, vy, vz);
        }
    }
}

