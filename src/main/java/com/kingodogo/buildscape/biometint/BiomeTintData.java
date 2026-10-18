package com.kingodogo.buildscape.biometint;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;

public class BiomeTintData extends SavedData {
    private final Map<Long, Map<Integer, ResourceLocation>> chunks = new HashMap<>();

    public static BiomeTintData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(BiomeTintData::load, BiomeTintData::new,
                "buildscape_biome_tints");
    }

    public static int columnIndex(int x, int z) {
        return (x & 15) | ((z & 15) << 4);
    }

    public boolean putColumn(int x, int z, ResourceLocation biome) {
        Map<Integer, ResourceLocation> columns = chunks.computeIfAbsent(ChunkPos.asLong(x >> 4, z >> 4),
                ignored -> new HashMap<>());
        if (biome.equals(columns.put(columnIndex(x, z), biome))) return false;
        setDirty();
        return true;
    }

    public Map<Integer, ResourceLocation> getChunk(ChunkPos chunk) {
        return Map.copyOf(chunks.getOrDefault(chunk.toLong(), Map.of()));
    }

    public static BiomeTintData load(CompoundTag tag) {
        BiomeTintData data = new BiomeTintData();
        ListTag chunkTags = tag.getList("Chunks", 10);
        for (int i = 0; i < chunkTags.size(); i++) {
            CompoundTag chunkTag = chunkTags.getCompound(i);
            Map<Integer, ResourceLocation> columns = new HashMap<>();
            ListTag columnTags = chunkTag.getList("Columns", 10);
            for (int j = 0; j < columnTags.size(); j++) {
                CompoundTag column = columnTags.getCompound(j);
                int index = column.getInt("Index");
                ResourceLocation biome = ResourceLocation.tryParse(column.getString("Biome"));
                if (index >= 0 && index < 256 && biome != null) columns.put(index, biome);
            }
            if (!columns.isEmpty()) data.chunks.put(chunkTag.getLong("Chunk"), columns);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag chunkTags = new ListTag();
        chunks.forEach((chunk, columns) -> {
            CompoundTag chunkTag = new CompoundTag();
            chunkTag.putLong("Chunk", chunk);
            ListTag columnTags = new ListTag();
            columns.forEach((index, biome) -> {
                CompoundTag column = new CompoundTag();
                column.putInt("Index", index);
                column.putString("Biome", biome.toString());
                columnTags.add(column);
            });
            chunkTag.put("Columns", columnTags);
            chunkTags.add(chunkTag);
        });
        tag.put("Chunks", chunkTags);
        return tag;
    }
}
