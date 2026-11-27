package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.particle.ModParticles;
import com.kingodogo.buildscape.particle.TintedParticleColorTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SporeBlossomBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.DoubleSupplier;
import java.util.function.IntUnaryOperator;
public class ColoredSporeBlossomBlock extends SporeBlossomBlock {

    private final int particleColorRGB;

    public ColoredSporeBlossomBlock(BlockBehaviour.Properties properties, int particleColorRGB) {
        super(properties);
        this.particleColorRGB = particleColorRGB;
    }

    public int getParticleColorRGB() {
        return particleColorRGB;
    }

    public float getRed() {
        return ((particleColorRGB >> 16) & 0xFF) / 255.0f;
    }

    public float getGreen() {
        return ((particleColorRGB >> 8) & 0xFF) / 255.0f;
    }

    public float getBlue() {
        return (particleColorRGB & 0xFF) / 255.0f;
    }

    public static int hexToRGB(String hex) {
        if (hex == null || hex.isEmpty()) {
            return 0xFFFFFF;
        }
        hex = hex.replace("#", "");
        try {
            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);
            return (r << 16) | (g << 8) | b;
        } catch (Exception e) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Unable to parse spore blossom particle color; using white", e);
            return 0xFFFFFF;
        }
    }

    public void onAnimateTick(BlockState state, Level level, BlockPos pos, DoubleSupplier nextDouble, IntUnaryOperator nextInt) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();

        String colorHex = String.format(java.util.Locale.ROOT, "#%06x", particleColorRGB);

        if (level.isClientSide()) {
            double centerX = (double) i + 0.5D;
            double centerY = (double) j + 0.5D;
            double centerZ = (double) k + 0.5D;

            if (nextDouble.getAsDouble() < 0.50D) {
                int centerParticleCount = 1 + nextInt.applyAsInt(3);
                for (int c = 0; c < centerParticleCount; c++) {
                    double offsetX = (nextDouble.getAsDouble() - 0.5D) * 1.6D;
                    double offsetZ = (nextDouble.getAsDouble() - 0.5D) * 1.6D;
                    double spawnX = centerX + offsetX;
                    double spawnZ = centerZ + offsetZ;

                    double driftX = (nextDouble.getAsDouble() - 0.5D) * 0.02D;
                    double driftZ = (nextDouble.getAsDouble() - 0.5D) * 0.02D;
                    double fallSpeed = -0.007D - nextDouble.getAsDouble() * 0.006D;

                    TintedParticleColorTracker.registerColorForPosition(spawnX, centerY, spawnZ, colorHex);
                    level.addParticle(
                            ModParticles.TINTED_DRIP_FALL.get(),
                            spawnX,
                            centerY,
                            spawnZ,
                            driftX,
                            fallSpeed,
                            driftZ
                    );
                }
            }

            if (nextDouble.getAsDouble() < 0.60D) {
                int spreadParticleCount = 10 + nextInt.applyAsInt(25);
                for (int p = 0; p < spreadParticleCount; p++) {
                    double distance = 3.0D + nextDouble.getAsDouble() * 16.0D;
                    double angle = nextDouble.getAsDouble() * 2.0D * Math.PI;

                    double spreadX = centerX + Math.cos(angle) * distance;
                    double spreadZ = centerZ + Math.sin(angle) * distance;
                    double spreadY = centerY + (nextDouble.getAsDouble() * 9.0D - 3.0D);

                    double velocityX = (nextDouble.getAsDouble() - 0.5D) * 0.04D;
                    double velocityY = (nextDouble.getAsDouble() - 0.125D) * 0.04D;
                    double velocityZ = (nextDouble.getAsDouble() - 0.5D) * 0.04D;

                    TintedParticleColorTracker.registerColorForPosition(spreadX, spreadY, spreadZ, colorHex);
                    level.addParticle(
                            ModParticles.TINTED_DRIP_FALL.get(),
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
}
