package com.kingodogo.buildscape.adapter.v26x.client;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ColorResolver;

/** Uses vanilla's resource-reloaded dry colormap with the reference 3x3 biome blend. */
public final class DryFoliageColor {
    public static final ColorResolver DRY_FOLIAGE_RESOLVER = (biome, x, z) -> biome.getDryFoliageColor();
    private DryFoliageColor() {}

    public static int get(double temperature, double humidity) {
        return net.minecraft.world.level.DryFoliageColor.get(
                Math.max(0, Math.min(1, temperature)), Math.max(0, Math.min(1, humidity)));
    }

    public static int getDefaultColor() { return get(0.5, 1.0); }

    public static int getDryFoliageColor(BlockAndTintGetter level, BlockPos pos) {
        var reader = net.minecraft.client.Minecraft.getInstance().level;
        if (level == null || pos == null || reader == null) return getDefaultColor();
        int red = 0, green = 0, blue = 0;
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                int color = reader.getBiome(pos.offset(x, 0, z)).value().getDryFoliageColor();
                red += (color >>> 16) & 255;
                green += (color >>> 8) & 255;
                blue += color & 255;
            }
        }
        return ((red / 9) << 16) | ((green / 9) << 8) | (blue / 9);
    }
}
