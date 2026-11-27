package com.kingodogo.buildscape.biome;

import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BiomeTintDataTest {
    @Test
    void importsReleaseDataAndRoundTripsThroughModernCodec() {
        long chunkKey = BiomeTintStore.chunkKey(-1, 2);
        CompoundTag valid = new CompoundTag();
        valid.putInt("Index", 15);
        valid.putString("Biome", "minecraft:swamp");
        CompoundTag invalid = new CompoundTag();
        invalid.putInt("Index", 300);
        invalid.putString("Biome", "minecraft:plains");
        ListTag columns = new ListTag();
        columns.add(valid);
        columns.add(invalid);
        CompoundTag chunk = new CompoundTag();
        chunk.putLong("Chunk", chunkKey);
        chunk.put("Columns", columns);
        ListTag chunks = new ListTag();
        chunks.add(chunk);
        CompoundTag payload = new CompoundTag();
        payload.put("Chunks", chunks);
        CompoundTag legacy = new CompoundTag();
        legacy.put("data", payload);

        BiomeTintData data = BiomeTintData.fromLegacy(legacy);
        assertEquals(CommonId.of("minecraft", "swamp"), data.getChunk(chunkKey).get(15));
        assertEquals(1, data.getChunk(chunkKey).size());
        assertFalse(data.putColumn(-1, 32, CommonId.of("minecraft", "swamp")));
        assertTrue(data.putColumn(-1, 32, CommonId.of("minecraft", "plains")));
        var encoded = BiomeTintData.CODEC.encodeStart(NbtOps.INSTANCE, data).result().orElseThrow();
        BiomeTintData decoded = BiomeTintData.CODEC.parse(NbtOps.INSTANCE, encoded).result().orElseThrow();
        assertEquals(data.getChunk(chunkKey), decoded.getChunk(chunkKey));
        assertThrows(IllegalArgumentException.class, () -> BiomeTintData.fromLegacy(new CompoundTag()));
        CompoundTag malformed = new CompoundTag();
        malformed.putString("data", "not a compound");
        assertThrows(IllegalArgumentException.class, () -> BiomeTintData.fromLegacy(malformed));
        assertTrue(BiomeTintData.CODEC.parse(NbtOps.INSTANCE, new CompoundTag()).result().isEmpty());
        var empty = BiomeTintData.CODEC.encodeStart(NbtOps.INSTANCE, new BiomeTintData()).result().orElseThrow();
        assertTrue(BiomeTintData.CODEC.parse(NbtOps.INSTANCE, empty).result().isPresent());
    }
}
