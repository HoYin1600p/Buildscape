package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.core.ClientHooks;
import com.kingodogo.buildscape.registry.ModParticles;
import com.mojang.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SporeBlossomBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Locale;
import java.util.Random;

public class ColoredSporeBlossomBlock
        extends
            SporeBlossomBlock {

    private final Vector3f particleColor;
    private final int particleColorRGB;

    public ColoredSporeBlossomBlock(
            BlockBehaviour.Properties properties,
            Vector3f particleColor,
            int particleColorRGB) {
        super(properties);
        this.particleColor = particleColor;
        this.particleColorRGB = particleColorRGB;
    }

    @Override
    public void animateTick(
            BlockState state,
            Level level,
            BlockPos pos,
            Random random) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();

        String colorHex = String.format(Locale.ROOT, "#%06x", particleColorRGB);

        if (level.isClientSide) {
            double centerX = (double) i + 0.5;
            double centerY = (double) j + 0.5;
            double centerZ = (double) k + 0.5;

            if (random.nextFloat() < 0.50F) {
                int centerParticleCount = 1 + random.nextInt(3);
                for (int c = 0; c < centerParticleCount; c++) {
                    double offsetX = (random.nextDouble() - 0.5) * 1.6;
                    double offsetZ = (random.nextDouble() - 0.5) * 1.6;
                    double spawnX = centerX + offsetX;
                    double spawnZ = centerZ + offsetZ;

                    double driftX = (random.nextDouble() - 0.5) * 0.02;
                    double driftZ = (random.nextDouble() - 0.5) * 0.02;
                    double fallSpeed = -0.007 - random.nextDouble() * 0.006;

                    if (level.isClientSide) {
                        ClientHooks.get().registerTintedDripColor(
                                spawnX,
                                centerY,
                                spawnZ,
                                colorHex);
                    }
                    level.addParticle(
                            ModParticles.TINTED_DRIP_FALL.get(),
                            spawnX,
                            centerY,
                            spawnZ,
                            driftX,
                            fallSpeed,
                            driftZ);
                }
            }

            if (random.nextFloat() < 0.60F) {
                int spreadParticleCount = 10 + random.nextInt(25);
                for (int p = 0; p < spreadParticleCount; p++) {
                    double distance = 3.0 + random.nextDouble() * 16.0;
                    double angle = random.nextDouble() * 2.0 * Math.PI;

                    double spreadX = centerX + Math.cos(angle) * distance;
                    double spreadZ = centerZ + Math.sin(angle) * distance;
                    double spreadY = centerY + (random.nextDouble() * 9.0 - 3.0);

                    double velocityX = (random.nextDouble() - 0.5) * 0.04;
                    double velocityY = (random.nextDouble() - 0.125) * 0.04;
                    double velocityZ = (random.nextDouble() - 0.5) * 0.04;

                    if (level.isClientSide) {
                        ClientHooks.get().registerTintedDripColor(
                                spreadX,
                                spreadY,
                                spreadZ,
                                colorHex);
                    }
                    level.addParticle(
                            ModParticles.TINTED_DRIP_FALL.get(),
                            spreadX,
                            spreadY,
                            spreadZ,
                            velocityX,
                            velocityY,
                            velocityZ);
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
