package com.kingodogo.buildscape.client;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.level.ColorResolver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class DryFoliageColor {
    private static final int[] pixels = new int[65536];
    private static boolean loaded = false;

    public static final ColorResolver DRY_FOLIAGE_RESOLVER = (biome, x, z) -> get(biome.getBaseTemperature(), biome.getDownfall());

    public static void init() {
        try {
            ResourceLocation loc = new ResourceLocation("minecraft", "textures/colormap/dry_foliage.png");
            Resource resource = Minecraft.getInstance().getResourceManager().getResource(loc);
            try (InputStream stream = resource.getInputStream()) {
                BufferedImage image = ImageIO.read(stream);
                if (image != null) {
                    image.getRGB(0, 0, 256, 256, pixels, 0, 256);
                    loaded = true;
                }
            }
        } catch (Exception e) {
            java.util.Arrays.fill(pixels, 0x867E36);
        }
    }

    public static int get(double temperature, double humidity) {
        if (!loaded) {
            init();
        }
        humidity *= temperature;
        int i = (int) ((1.0D - temperature) * 255.0D);
        int j = (int) ((1.0D - humidity) * 255.0D);
        i = Math.max(0, Math.min(255, i));
        j = Math.max(0, Math.min(255, j));
        int index = j << 8 | i;
        return (index < 0 || index >= pixels.length) ? 0x867E36 : (pixels[index] & 0xFFFFFF);
    }

    public static int getDefaultColor() {
        return get(0.5D, 1.0D);
    }

    public static int getDryFoliageColor(net.minecraft.world.level.BlockAndTintGetter level, net.minecraft.core.BlockPos pos) {
        if (level == null || pos == null || !(level instanceof net.minecraft.world.level.LevelReader levelReader)) {
            return getDefaultColor();
        }
        int r = 0, g = 0, b = 0;
        int count = 0;
        int xMin = pos.getX() - 1;
        int xMax = pos.getX() + 1;
        int zMin = pos.getZ() - 1;
        int zMax = pos.getZ() + 1;
        for (int x = xMin; x <= xMax; x++) {
            for (int z = zMin; z <= zMax; z++) {
                net.minecraft.core.BlockPos curPos = new net.minecraft.core.BlockPos(x, pos.getY(), z);
                net.minecraft.world.level.biome.Biome biome = levelReader.getBiome(curPos).value();
                int color = get(biome.getBaseTemperature(), biome.getDownfall());
                r += (color >> 16) & 0xFF;
                g += (color >> 8) & 0xFF;
                b += color & 0xFF;
                count++;
            }
        }
        return ((r / count) << 16) | ((g / count) << 8) | (b / count);
    }
}
