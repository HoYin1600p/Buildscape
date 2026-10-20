package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.math.Vector3f;

import java.util.Locale;
import java.util.Random;

public class SoftFabricBlock extends Block {
    public SoftFabricBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);

        if (entity instanceof Player && !entity.isSteppingCarefully() && entity.tickCount % 4 == 0
                && level instanceof ServerLevel serverLevel) {
            Vector3f color = this.getDyeColor(this.getRegistryName());
            if (color != null) {
                DustParticleOptions particleOptions = new DustParticleOptions(color, 0.5F);
                serverLevel.sendParticles(particleOptions, pos.getX() + 0.5, pos.getY() + 1.05,
                        pos.getZ() + 0.5, 4, 0.2, 0.025, 0.2, 0.0);
            }
        }
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        return FabricMiningSpeed.getDestroyProgress(state, player, level, pos);
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        super.attack(state, level, pos, player);

        Vector3f color = this.getDyeColor(this.getRegistryName());
        if (color != null) {
            if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
                DustParticleOptions particleOptions = new DustParticleOptions(color, 0.5F);
                Random random = level.random;
                for (int i = 0; i < 8; i++) {
                    double px = pos.getX() + 0.1 + random.nextDouble() * 0.8;
                    double py = pos.getY() + 0.1 + random.nextDouble() * 0.8;
                    double pz = pos.getZ() + 0.1 + random.nextDouble() * 0.8;

                    serverLevel.sendParticles(particleOptions, px, py, pz, 1, 0.0, 0.0, 0.0, 0.0);
                }
            }
        }
    }

    private Vector3f getDyeColor(ResourceLocation registryName) {
        if (registryName == null) {
            return new Vector3f(1.0F, 1.0F, 1.0F);
        }
        String path = registryName.getPath();
        if (path.equals("glow_ink_sack")) {
            return new Vector3f(0.1F, 0.9F, 0.9F);
        }
        if (path.endsWith("_dye_sack")) {
            String colorName = path.substring(0, path.length() - "_dye_sack".length());
            try {
                DyeColor dyeColor = DyeColor.valueOf(colorName.toUpperCase(Locale.ROOT));
                float[] rgb = dyeColor.getTextureDiffuseColors();
                return new Vector3f(rgb[0], rgb[1], rgb[2]);
            } catch (IllegalArgumentException e) {
                // not a dye colour name; fall through to the default colour
            }
        }
        return null;
    }
}
