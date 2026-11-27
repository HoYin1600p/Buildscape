package com.kingodogo.buildscape.client.biome;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.biome.Biome;

public final class BiomeTintRendering {
    private BiomeTintRendering() { }

    public static int resolveColor(ColorResolver resolver, Biome original, double x, double z) {
        if (resolver != BiomeColors.GRASS_COLOR_RESOLVER && resolver != BiomeColors.FOLIAGE_COLOR_RESOLVER
                && resolver != BiomeColors.WATER_COLOR_RESOLVER) return resolver.getColor(original, x, z);
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return resolver.getColor(original, x, z);
        var id = ClientBiomeTints.cache().get(level, Mth.floor(x), Mth.floor(z));
        if (id != null) {
            Biome painted = level.registryAccess().lookupOrThrow(Registries.BIOME)
                    .getValue(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath()));
            if (painted != null) original = painted;
        }
        return resolver.getColor(original, x, z);
    }

    public static Integer color(BlockAndTintGetter view, BlockPos pos, ColorResolver resolver) {
        if (resolver != BiomeColors.GRASS_COLOR_RESOLVER && resolver != BiomeColors.FOLIAGE_COLOR_RESOLVER
                && resolver != BiomeColors.WATER_COLOR_RESOLVER) return null;
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) return null;
        Registry<Biome> biomes = level.registryAccess().lookupOrThrow(Registries.BIOME);
        return ClientBiomeTints.cache().blendedColor(level, pos.getX(), pos.getZ(),
                minecraft.options.biomeBlendRadius().get(), (x, z) -> null, (id, x, z) -> {
                    Biome biome = id == null ? null : biomes.getValue(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath()));
                    if (biome == null) biome = level.getBiome(new BlockPos(x, pos.getY(), z)).value();
                    return resolver.getColor(biome, x, z);
                });
    }

    public static void rebuild(ClientLevel level, int chunkX, int chunkZ) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != level) return;
        level.clearTintCaches();
        int radius = minecraft.options.biomeBlendRadius().get();
        for (int x = ((chunkX << 4) - radius) >> 4; x <= ((chunkX << 4) + 15 + radius) >> 4; x++) {
            for (int z = ((chunkZ << 4) - radius) >> 4; z <= ((chunkZ << 4) + 15 + radius) >> 4; z++) {
                for (int y = level.getMinSectionY(); y < (level.getMaxSectionY() + 1); y++) {
                    minecraft.levelRenderer.setSectionDirty(x, y, z);
                }
            }
        }
    }
}
