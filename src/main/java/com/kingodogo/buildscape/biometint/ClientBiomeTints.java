package com.kingodogo.buildscape.biometint;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, value = Dist.CLIENT)
public class ClientBiomeTints {
    private record State(@Nullable ClientLevel level, Map<Long, Map<Integer, Biome>> chunks) {}
    private static volatile State state = new State(null, Map.of());

    public static void apply(SyncBiomeTintPacket packet) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null || !level.dimension().location().equals(packet.dimension)) return;
        State previous = state;
        Map<Long, Map<Integer, Biome>> chunks = new HashMap<>(previous.level == level ? previous.chunks : Map.of());
        Registry<Biome> registry = level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
        Map<Integer, Biome> columns = new HashMap<>();
        packet.columns.forEach((index, id) -> {
            Biome biome = registry.get(id);
            if (biome != null) columns.put(index, biome);
        });
        Map<Integer, Biome> old = chunks.remove(packet.chunk.toLong());
        if (!columns.isEmpty()) chunks.put(packet.chunk.toLong(), Map.copyOf(columns));
        state = new State(level, Map.copyOf(chunks));
        if (!Objects.equals(old == null ? Map.of() : old, columns)) rebuild(packet.chunk, level);
    }

    private static void rebuild(ChunkPos chunk, ClientLevel level) {
        Minecraft minecraft = Minecraft.getInstance();
        int radius = minecraft.options.biomeBlendRadius;
        for (int x = (chunk.getMinBlockX() - radius) >> 4; x <= (chunk.getMaxBlockX() + radius) >> 4; x++) {
            for (int z = (chunk.getMinBlockZ() - radius) >> 4; z <= (chunk.getMaxBlockZ() + radius) >> 4; z++) {
                for (int y = level.getMinSection(); y < level.getMaxSection(); y++) {
                    minecraft.levelRenderer.setSectionDirty(x, y, z);
                }
            }
        }
    }

    public static int resolveColor(ColorResolver resolver, Biome original, double x, double z) {
        if (resolver != BiomeColors.GRASS_COLOR_RESOLVER && resolver != BiomeColors.FOLIAGE_COLOR_RESOLVER
                && resolver != BiomeColors.WATER_COLOR_RESOLVER) return resolver.getColor(original, x, z);
        State current = state;
        if (current.level == Minecraft.getInstance().level && !current.chunks.isEmpty()) {
            int blockX = net.minecraft.util.Mth.floor(x);
            int blockZ = net.minecraft.util.Mth.floor(z);
            Map<Integer, Biome> columns = current.chunks.get(ChunkPos.asLong(blockX >> 4, blockZ >> 4));
            if (columns != null) original = columns.getOrDefault(BiomeTintData.columnIndex(blockX, blockZ), original);
        }
        return resolver.getColor(original, x, z);
    }

    @Nullable
    public static Integer color(BlockAndTintGetter level, BlockPos pos, ColorResolver resolver) {
        if (resolver != BiomeColors.GRASS_COLOR_RESOLVER && resolver != BiomeColors.FOLIAGE_COLOR_RESOLVER
                && resolver != BiomeColors.WATER_COLOR_RESOLVER) return null;
        State current = state;
        if (current.chunks.isEmpty() || current.level != Minecraft.getInstance().level) return null;
        int radius = Minecraft.getInstance().options.biomeBlendRadius;
        boolean nearby = false;
        for (int x = (pos.getX() - radius) >> 4; x <= (pos.getX() + radius) >> 4; x++) {
            for (int z = (pos.getZ() - radius) >> 4; z <= (pos.getZ() + radius) >> 4; z++) {
                if (current.chunks.containsKey(ChunkPos.asLong(x, z))) nearby = true;
            }
        }
        if (!nearby) return null;
        int red = 0;
        int green = 0;
        int blue = 0;
        boolean painted = false;
        BlockPos.MutableBlockPos sample = new BlockPos.MutableBlockPos();
        for (int x = pos.getX() - radius; x <= pos.getX() + radius; x++) {
            for (int z = pos.getZ() - radius; z <= pos.getZ() + radius; z++) {
                Map<Integer, Biome> columns = current.chunks.get(ChunkPos.asLong(x >> 4, z >> 4));
                Biome biome = columns == null ? null : columns.get(BiomeTintData.columnIndex(x, z));
                if (biome == null) {
                    biome = current.level.getBiome(sample.set(x, pos.getY(), z)).value();
                } else {
                    painted = true;
                }
                int color = resolver.getColor(biome, x, z);
                red += (color >> 16) & 255;
                green += (color >> 8) & 255;
                blue += color & 255;
            }
        }
        if (!painted) return null;
        int samples = (radius * 2 + 1) * (radius * 2 + 1);
        return ((red / samples) << 16) | ((green / samples) << 8) | (blue / samples);
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        State current = state;
        if (event.getWorld() != current.level) return;
        ChunkPos chunk = event.getChunk().getPos();
        if (!current.chunks.containsKey(chunk.toLong())) return;
        Map<Long, Map<Integer, Biome>> chunks = new HashMap<>(current.chunks);
        chunks.remove(chunk.toLong());
        state = new State(current.level, Map.copyOf(chunks));
        rebuild(chunk, current.level);
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld() == state.level) state = new State(null, Map.of());
    }
}
