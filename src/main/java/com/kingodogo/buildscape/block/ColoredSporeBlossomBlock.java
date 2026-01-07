package com.kingodogo.buildscape.block;

import com.mojang.math.Vector3f;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SporeBlossomBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ColoredSporeBlossomBlock
        extends net.minecraft.world.level.block.SporeBlossomBlock {

    private final Vector3f particleColor;
    private final int particleColorRGB;

    public ColoredSporeBlossomBlock(
            BlockBehaviour.Properties properties,
            Vector3f particleColor,
            int particleColorRGB
    ) {
        super(properties);
        this.particleColor = particleColor;
        this.particleColorRGB = particleColorRGB;
    }

    @Override
    public void animateTick(
            BlockState state,
            Level level,
            BlockPos pos,
            Random random
    ) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();

        String colorHex = String.format("#%06x", particleColorRGB);

        if (level.isClientSide) {
            double centerX = (double) i + 0.5D;
            double centerY = (double) j + 0.5D;
            double centerZ = (double) k + 0.5D;

            if (random.nextFloat() < 0.50F) {
                int centerParticleCount = 1 + random.nextInt(3);
                for (int c = 0; c < centerParticleCount; c++) {
                    double offsetX = (random.nextDouble() - 0.5D) * 1.6D;
                    double offsetZ = (random.nextDouble() - 0.5D) * 1.6D;
                    double spawnX = centerX + offsetX;
                    double spawnZ = centerZ + offsetZ;

                    double driftX = (random.nextDouble() - 0.5D) * 0.02D;
                    double driftZ = (random.nextDouble() - 0.5D) * 0.02D;
                    double fallSpeed = -0.007D - random.nextDouble() * 0.006D;

                    if (level.isClientSide) {
                        com.kingodogo.buildscape.particle.TintedDripParticle.registerColorForPosition(
                                spawnX,
                                centerY,
                                spawnZ,
                                colorHex
                        );
                    }
                    level.addParticle(
                            com.kingodogo.buildscape.particle.ModParticles.TINTED_DRIP_FALL.get(),
                            spawnX,
                            centerY,
                            spawnZ,
                            driftX,
                            fallSpeed,
                            driftZ
                    );
                }
            }

            if (random.nextFloat() < 0.30F) {
                int spreadParticleCount = 3 + random.nextInt(18);
                for (int p = 0; p < spreadParticleCount; p++) {
                    double distance = 3.0D + random.nextDouble() * 12.0D;
                    double angle = random.nextDouble() * 2.0D * Math.PI;

                    double spreadX = centerX + Math.cos(angle) * distance;
                    double spreadZ = centerZ + Math.sin(angle) * distance;
                    double spreadY = centerY + (random.nextDouble() * 9.0D - 3.0D);

                    double velocityX = (random.nextDouble() - 0.5D) * 0.04D;
                    double velocityY = (random.nextDouble() - 0.125D) * 0.04D;
                    double velocityZ = (random.nextDouble() - 0.5D) * 0.04D;

                    if (level.isClientSide) {
                        com.kingodogo.buildscape.particle.TintedDripParticle.registerColorForPosition(
                                spreadX,
                                spreadY,
                                spreadZ,
                                colorHex
                        );
                    }
                    level.addParticle(
                            com.kingodogo.buildscape.particle.ModParticles.TINTED_DRIP_FALL.get(),
                            spreadX,
                            spreadY,
                            spreadZ,
                            velocityX,
                            velocityY,
                            velocityZ
                    );
                }
            }
        }
    }

    public int getParticleColorRGB() {
        return particleColorRGB;
    }

    public Vector3f getParticleColor() {
        return particleColor;
    }

    public static Vector3f hexToVector3f(String hex) {
        hex = hex.replace("#", "");

        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);

        return new Vector3f(r / 255.0f, g / 255.0f, b / 255.0f);
    }

    public static int hexToRGB(String hex) {
        hex = hex.replace("#", "");

        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);

        return (r << 16) | (g << 8) | b;
    }
}
