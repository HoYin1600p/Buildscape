package com.kingodogo.buildscape.biome;

import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;

/** Persistent biome colour overrides, using the 1.18.2 release's saved-data format. */
public final class BiomeTintData extends SavedData {
    private final BiomeTintStore store = new BiomeTintStore();

    public static BiomeTintData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new SavedData.Factory<>(BiomeTintData::new,
                (tag, registries) -> load(tag), null),
                "buildscape_biome_tints");
    }

    public boolean putColumn(int x, int z, CommonId biome) {
        if (!store.putColumn(x, z, biome)) return false;
        setDirty();
        return true;
    }

    public boolean hasChunk(long chunk) { return store.hasChunk(chunk); }
    public Map<Integer, CommonId> getChunk(long chunk) { return store.getChunk(chunk); }

    public static BiomeTintData load(CompoundTag tag) {
        BiomeTintData data = new BiomeTintData();
        ListTag chunks = tag.getList("Chunks", Tag.TAG_COMPOUND);
        for (int i = 0; i < chunks.size(); i++) {
            CompoundTag chunk = chunks.getCompound(i);
            ListTag columns = chunk.getList("Columns", Tag.TAG_COMPOUND);
            Map<Integer, CommonId> entries = new HashMap<>();
            for (int j = 0; j < columns.size(); j++) {
                CompoundTag column = columns.getCompound(j);
                int index = column.getInt("Index");
                CommonId biome = CommonId.tryParse(column.getString("Biome"));
                if (index >= 0 && index < 256 && biome != null) entries.put(index, biome);
            }
            if (!entries.isEmpty()) data.store.restoreChunk(chunk.getLong("Chunk"), entries);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag chunks = new ListTag();
        store.snapshot().forEach((key, columns) -> {
            CompoundTag chunk = new CompoundTag();
            chunk.putLong("Chunk", key);
            ListTag entries = new ListTag();
            columns.forEach((index, biome) -> {
                CompoundTag column = new CompoundTag();
                column.putInt("Index", index);
                column.putString("Biome", biome.toString());
                entries.add(column);
            });
            chunk.put("Columns", entries);
            chunks.add(chunk);
        });
        tag.put("Chunks", chunks);
        return tag;
    }
}
