package com.kingodogo.buildscape.biome;

import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BiomeTintDataTest {
    @Test
    void readsReleaseDataAndRoundTripsValidColumns() {
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
        CompoundTag malformed = new CompoundTag();
        malformed.putInt("Index", 0);
        malformed.putString("Biome", "not valid!");
        columns.add(malformed);
        CompoundTag chunk = new CompoundTag();
        chunk.putLong("Chunk", chunkKey);
        chunk.put("Columns", columns);
        ListTag chunks = new ListTag();
        chunks.add(chunk);
        CompoundTag saved = new CompoundTag();
        saved.put("Chunks", chunks);

        BiomeTintData data = BiomeTintData.load(saved);
        assertEquals(CommonId.of("minecraft", "swamp"), data.getChunk(chunkKey).get(15));
        assertEquals(1, data.getChunk(chunkKey).size());
        assertFalse(data.isDirty());
        assertFalse(data.putColumn(-1, 32, CommonId.of("minecraft", "swamp")));
        assertFalse(data.isDirty());
        assertTrue(data.putColumn(-1, 32, CommonId.of("minecraft", "plains")));
        assertTrue(data.isDirty());
        assertEquals(data.getChunk(chunkKey), BiomeTintData.load(data.save(new CompoundTag())).getChunk(chunkKey));
    }
}
