package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.math.Vector3f;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Locale;
import java.util.Random;

public class SpoolBlock extends RotatedPillarBlock {
    public SpoolBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);

        if (!entity.isSteppingCarefully() && level instanceof ServerLevel serverLevel) {
            ResourceLocation registryName = ForgeRegistries.BLOCKS.getKey(this);
            Vector3f color = this.getDyeColor(registryName);
            if (color != null) {
                DustParticleOptions particleOptions = new DustParticleOptions(color, 0.5F);
                Random random = level.random;
                for (int i = 0; i < 4; i++) {
                    double px = pos.getX() + 0.1D + random.nextDouble() * 0.8D;
                    double py = pos.getY() + 1.0D + random.nextDouble() * 0.1D;
                    double pz = pos.getZ() + 0.1D + random.nextDouble() * 0.8D;

                    serverLevel.sendParticles(particleOptions, px, py, pz, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        float destroySpeed = state.getDestroySpeed(level, pos);
        if (destroySpeed == -1.0F) {
            return 0.0F;
        }

        ItemStack tool = player.getMainHandItem();
        float speedMultiplier = 1.0F;

        if (tool.getItem() instanceof ShearsItem || tool.getItem() instanceof HoeItem) {
            if (tool.getItem() instanceof ShearsItem) {
                speedMultiplier = 5.0F;
            } else if (tool.getItem() instanceof net.minecraft.world.item.DiggerItem digger) {
                speedMultiplier = digger.getTier().getSpeed();
            } else {
                speedMultiplier = 2.0F;
            }

            int efficiencyLevel = net.minecraft.world.item.enchantment.EnchantmentHelper.getBlockEfficiency(player);
            if (efficiencyLevel > 0) {
                speedMultiplier += (float) (efficiencyLevel * efficiencyLevel + 1);
            }

            return speedMultiplier / destroySpeed / 30.0F;
        } else {
            // Hand/other tools: mine fast as well
            speedMultiplier = 2.5F;
            return speedMultiplier / destroySpeed / 100.0F;
        }
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        super.attack(state, level, pos, player);

        ResourceLocation registryName = ForgeRegistries.BLOCKS.getKey(this);
        Vector3f color = this.getDyeColor(registryName);
        if (color != null) {
            if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
                DustParticleOptions particleOptions = new DustParticleOptions(color, 0.5F);
                Random random = level.random;
                for (int i = 0; i < 8; i++) {
                    double px = pos.getX() + 0.1D + random.nextDouble() * 0.8D;
                    double py = pos.getY() + 0.1D + random.nextDouble() * 0.8D;
                    double pz = pos.getZ() + 0.1D + random.nextDouble() * 0.8D;

                    serverLevel.sendParticles(particleOptions, px, py, pz, 1, 0.0D, 0.0D, 0.0D, 0.0D);
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
                // fallback if color not found in enum
            }
        }
        if (path.endsWith("_spool")) {
            String colorName = path.substring(0, path.length() - "_spool".length());
            if (colorName.equals("glowing")) {
                return new Vector3f(0.1F, 0.9F, 0.9F);
            }
            if (colorName.isEmpty()) {
                return new Vector3f(1.0F, 1.0F, 1.0F);
            }
            try {
                DyeColor dyeColor = DyeColor.valueOf(colorName.toUpperCase(Locale.ROOT));
                float[] rgb = dyeColor.getTextureDiffuseColors();
                return new Vector3f(rgb[0], rgb[1], rgb[2]);
            } catch (IllegalArgumentException e) {
                // fallback if color not found in enum
            }
        }
        if (path.equals("spool")) {
            return new Vector3f(1.0F, 1.0F, 1.0F);
        }
        return null;
    }
}
