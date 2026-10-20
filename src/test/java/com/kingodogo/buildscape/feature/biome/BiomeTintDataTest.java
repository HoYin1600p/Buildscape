package com.kingodogo.buildscape.feature.biome;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class BiomeTintDataTest {
    private static final ResourceLocation SWAMP = new ResourceLocation("minecraft", "swamp");
    private static final ResourceLocation DESERT = new ResourceLocation("minecraft", "desert");

    @Test
    public void columnsRoundTripAcrossNegativeChunkBoundaries() {
        BiomeTintData data = new BiomeTintData();
        data.putColumn(-1, -17, SWAMP);
        data.putColumn(-16, -32, DESERT);
        data.putColumn(0, 0, SWAMP);
        BiomeTintData loaded = BiomeTintData.load(data.save(new CompoundTag()));
        assertEquals(Map.of(255, SWAMP, 0, DESERT), loaded.getChunk(new ChunkPos(-1, -2)));
        assertEquals(Map.of(0, SWAMP), loaded.getChunk(new ChunkPos(0, 0)));
        assertTrue(loaded.getChunk(new ChunkPos(-1, -1)).isEmpty());
        assertFalse(loaded.isDirty());
    }

    @Test
    public void repaintChangesOnlyOneColumnAndUnchangedPaintDoesNotDirtyData() {
        BiomeTintData data = new BiomeTintData();
        assertTrue(data.putColumn(0, 0, SWAMP));
        assertTrue(data.putColumn(1, 0, SWAMP));
        Map<Integer, ResourceLocation> snapshot = data.getChunk(new ChunkPos(0, 0));
        data.setDirty(false);
        assertFalse(data.putColumn(0, 0, SWAMP));
        assertFalse(data.isDirty());
        assertTrue(data.putColumn(0, 0, DESERT));
        assertTrue(data.isDirty());
        assertEquals(Map.of(0, DESERT, 1, SWAMP), data.getChunk(new ChunkPos(0, 0)));
        assertEquals(Map.of(0, SWAMP, 1, SWAMP), snapshot);
        assertThrows(UnsupportedOperationException.class, () -> snapshot.put(2, SWAMP));
    }

    @Test
    public void existingWorldWithoutTintDataIsEmptyAndInvalidEntriesAreSkipped() {
        assertTrue(BiomeTintData.load(new CompoundTag()).getChunk(new ChunkPos(0, 0)).isEmpty());
        CompoundTag root = new CompoundTag();
        CompoundTag chunk = new CompoundTag();
        chunk.putLong("Chunk", 0L);
        ListTag columns = new ListTag();
        CompoundTag badIndex = new CompoundTag();
        badIndex.putInt("Index", 256);
        badIndex.putString("Biome", SWAMP.toString());
        columns.add(badIndex);
        CompoundTag badBiome = new CompoundTag();
        badBiome.putInt("Index", 0);
        badBiome.putString("Biome", "invalid biome");
        columns.add(badBiome);
        chunk.put("Columns", columns);
        ListTag chunks = new ListTag();
        chunks.add(chunk);
        root.put("Chunks", chunks);
        assertTrue(BiomeTintData.load(root).getChunk(new ChunkPos(0, 0)).isEmpty());
    }
}
