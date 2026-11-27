package com.kingodogo.buildscape.biome;

import com.kingodogo.buildscape.util.CommonId;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/** Persistent biome tints, including import of the 1.18.2 release's saved data. */
public final class BiomeTintData extends SavedData {
    static final Codec<BiomeTintData> CODEC = Codec.PASSTHROUGH.flatXmap(BiomeTintData::decodeResult,
            data -> DataResult.success(new Dynamic<>(NbtOps.INSTANCE, data.encode())));
    private static final SavedDataType<BiomeTintData> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath("buildscape", "biome_tints"), BiomeTintData::new, CODEC, null);
    private final BiomeTintStore store = new BiomeTintStore();

    public static BiomeTintData get(ServerLevel level) {
        var storage = level.getDataStorage();
        BiomeTintData existing = storage.get(TYPE);
        if (existing != null) return existing;

        Path dimension = DimensionType.getStorageFolder(level.dimension(),
                level.getServer().getWorldPath(LevelResource.ROOT));
        Path modern = dimension.resolve("data/buildscape/biome_tints.dat");
        if (Files.exists(modern)) {
            throw new IllegalStateException("Unable to load existing biome tint data: " + modern);
        }
        Path legacy = dimension.resolve("data/buildscape_biome_tints.dat");
        BiomeTintData data = new BiomeTintData();
        if (Files.exists(legacy)) {
            try {
                if (!Files.isRegularFile(legacy)) throw new IOException("Not a regular file");
                CompoundTag root = NbtIo.readCompressed(legacy, NbtAccounter.create(256L * 1024 * 1024));
                data = fromLegacy(root);
                data.setDirty();
            } catch (IOException | RuntimeException error) {
                throw new IllegalStateException("Unable to import legacy biome tint data: " + legacy, error);
            }
        }
        storage.set(TYPE, data);
        return data;
    }

    static BiomeTintData fromLegacy(CompoundTag root) {
        CompoundTag payload = root.getCompound("data").orElseThrow(
                () -> new IllegalArgumentException("Missing saved-data payload"));
        if (payload.getList("Chunks").isEmpty()) {
            throw new IllegalArgumentException("Missing biome tint chunks");
        }
        return decode(new Dynamic<>(NbtOps.INSTANCE, payload));
    }

    private static DataResult<BiomeTintData> decodeResult(Dynamic<?> root) {
        if (root.get("Chunks").asStreamOpt().result().isEmpty()) {
            return DataResult.error(() -> "Missing biome tint chunks");
        }
        return DataResult.success(decode(root));
    }

    private static BiomeTintData decode(Dynamic<?> root) {
        BiomeTintData data = new BiomeTintData();
        root.get("Chunks").asStreamOpt().result().ifPresent(chunks -> chunks.forEach(chunk -> {
            long key = chunk.get("Chunk").asLong(0);
            Map<Integer, CommonId> columns = new HashMap<>();
            chunk.get("Columns").asStreamOpt().result().ifPresent(entries -> entries.forEach(entry -> {
                int index = entry.get("Index").asInt(-1);
                CommonId biome = CommonId.tryParse(entry.get("Biome").asString(""));
                if (index >= 0 && index < 256 && biome != null) columns.put(index, biome);
            }));
            if (!columns.isEmpty()) data.store.restoreChunk(key, columns);
        }));
        return data;
    }

    private CompoundTag encode() {
        CompoundTag tag = new CompoundTag();
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

    public boolean putColumn(int x, int z, CommonId biome) {
        if (!store.putColumn(x, z, biome)) return false;
        setDirty();
        return true;
    }

    public boolean hasChunk(long chunk) { return store.hasChunk(chunk); }
    public Map<Integer, CommonId> getChunk(long chunk) { return store.getChunk(chunk); }
}
