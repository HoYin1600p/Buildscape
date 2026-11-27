package com.kingodogo.buildscape.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class PersistentDataTest {
    @Test
    void playerCountersCooldownAndHomemakerIdSurviveDiskFormatRoundTrip() throws Exception {
        CompoundTag data = new CompoundTag();
        data.putInt("BS_Stat_PillarsInteracted", 69);
        data.putLong("WanderingHomemakerCooldownRealTime", 1_800_000_000_000L);
        data.putString("WanderingHomemakerUUID", "12f08421-241d-4500-808a-71fce7f9573e");
        data.putBoolean("HasEnteredBuildscape", true);
        CompoundTag restored = roundTrip(data);
        assertEquals(data, restored);
        assertNotSame(data, restored);
        restored.putInt("BS_Stat_PillarsInteracted", 70);
        assertEquals(69, data.getInt("BS_Stat_PillarsInteracted").orElseThrow());
    }

    @Test
    void frameAndSignTagsIncludingArbitraryNestedDataSurviveRoundTrip() throws Exception {
        CompoundTag data = new CompoundTag();
        data.putString("BuildScapeFrameId", "mangrove_42");
        data.putString("BuildScapeParticlePattern", "spiral");
        data.putString("BuildscapeSignFrame", "copper");
        ListTag colors = new ListTag();
        colors.add(StringTag.valueOf("#ABCDEF"));
        CompoundTag nested = new CompoundTag();
        nested.put("colors", colors);
        nested.putIntArray("positions", new int[] {1, 2, 3});
        data.put("extension", nested);
        CompoundTag restored = roundTrip(data);
        assertEquals(data, restored);
        assertNotSame(nested, restored.getCompound("extension").orElseThrow());
        restored.getCompound("extension").orElseThrow().getList("colors").orElseThrow().clear();
        assertEquals(1, colors.size());
    }

    @Test
    void deletedKeysStayDeletedAndEmptyOwnersRemainEmpty() throws Exception {
        CompoundTag data = new CompoundTag();
        data.putString("BuildscapeSignFrame", "copper");
        data.remove("BuildscapeSignFrame");
        assertTrue(roundTrip(data).isEmpty());
    }

    @Test
    void fabricCodecRestoresIndependentMutableOwners() {
        CompoundTag source = new CompoundTag();
        CompoundTag child = new CompoundTag();
        child.putInt("count", 1);
        source.put("milestones", child);
        var encoded = PersistentData.CODEC.encodeStart(NbtOps.INSTANCE, source).getOrThrow();
        CompoundTag first = PersistentData.CODEC.parse(NbtOps.INSTANCE, encoded).getOrThrow();
        CompoundTag second = PersistentData.CODEC.parse(NbtOps.INSTANCE, encoded).getOrThrow();
        first.getCompound("milestones").orElseThrow().putInt("count", 2);
        assertEquals(1, second.getCompound("milestones").orElseThrow().getInt("count").orElseThrow());
        assertEquals(1, child.getInt("count").orElseThrow());
    }

    private static CompoundTag roundTrip(CompoundTag data) throws Exception {
        CompoundTag encoded = (CompoundTag) PersistentData.MAP_CODEC.codec().encodeStart(NbtOps.INSTANCE, data).getOrThrow();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        NbtIo.write(encoded, new DataOutputStream(bytes));
        CompoundTag saved = NbtIo.read(new DataInputStream(new ByteArrayInputStream(bytes.toByteArray())));
        assertNotNull(saved);
        return PersistentData.MAP_CODEC.codec().parse(NbtOps.INSTANCE, saved).getOrThrow();
    }
}
