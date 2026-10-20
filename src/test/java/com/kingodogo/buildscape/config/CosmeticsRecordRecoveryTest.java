package com.kingodogo.buildscape.config;

import com.kingodogo.buildscape.testutil.BootstrappedGameTest;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

public class CosmeticsRecordRecoveryTest extends BootstrappedGameTest {
    @Rule
    public TemporaryFolder temporary = new TemporaryFolder();

    @Test
    public void unreadableRecordIsPreservedBeforePreferenceAndCosmeticEdits() throws Exception {
        Path data = temporary.newFolder().toPath();
        Path legacy = temporary.newFolder().toPath();
        UUID player = UUID.randomUUID();
        Path file = data.resolve(player + "-cosmetic.dat");
        byte[] damaged = {1, 2, 3, 4};
        Files.write(file, damaged);
        CosmeticsConfig config = new CosmeticsConfig(data, legacy);
        assertFalse(config.getCreativeTreeBreaker(player));
        config.setCreativeTreeBreaker(player, true);
        config.equipCosmetic(player, 0, "test:hat");
        config.setCosmeticColor(player, "test:hat", "#123456");
        config.setShulkerPreview(player, false);
        assertArrayEquals(damaged, Files.readAllBytes(file.resolveSibling(file.getFileName() + ".bak")));
        CosmeticsConfig reloaded = new CosmeticsConfig(data, legacy);
        assertEquals(Map.of(0, "test:hat"), reloaded.getEquippedCosmetics(player));
        assertEquals("#123456", reloaded.getCosmeticColor(player, "test:hat"));
        assertTrue(reloaded.getCreativeTreeBreaker(player));
        assertFalse(reloaded.getShulkerPreview(player));
        try (var files = Files.list(data)) {
            assertFalse(files.anyMatch(path -> path.getFileName().toString().endsWith(".tmp")));
        }
    }

    @Test
    public void mismatchedUuidIsBackedUpBeforeReplacement() throws Exception {
        Path data = temporary.newFolder().toPath();
        UUID player = UUID.randomUUID();
        Path file = data.resolve(player + "-cosmetic.dat");
        CompoundTag record = record(UUID.randomUUID(), "test:other_hat");
        NbtIo.writeCompressed(record, file.toFile());
        byte[] original = Files.readAllBytes(file);
        CosmeticsConfig config = new CosmeticsConfig(data, temporary.newFolder().toPath());
        assertTrue(config.getEquippedCosmetics(player).isEmpty());
        config.equipCosmetic(player, 1, "test:new_hat");
        assertArrayEquals(original, Files.readAllBytes(file.resolveSibling(file.getFileName() + ".bak")));
        assertEquals(player, NbtIo.readCompressed(file.toFile()).getUUID("player_uuid"));
    }

    @Test
    public void currentDatWinsOverLegacyJsonAndCollidingDatIsRetained() throws Exception {
        Path data = temporary.newFolder().toPath();
        Path legacy = temporary.newFolder().toPath();
        UUID player = UUID.randomUUID();
        String name = player + "-cosmetic.dat";
        NbtIo.writeCompressed(record(player, "test:current_hat"), data.resolve(name).toFile());
        byte[] current = Files.readAllBytes(data.resolve(name));
        NbtIo.writeCompressed(record(player, "test:legacy_hat"), legacy.resolve(name).toFile());
        byte[] old = Files.readAllBytes(legacy.resolve(name));
        Files.writeString(legacy.resolve("equipped-cosmetics.json"),
                "{\"" + player + "\":{\"cosmetics\":{\"0\":\"test:json_hat\"},\"colors\":{}}}");
        CosmeticsConfig config = new CosmeticsConfig(data, legacy);
        assertArrayEquals(current, Files.readAllBytes(data.resolve(name)));
        assertArrayEquals(old, Files.readAllBytes(legacy.resolve(name + ".migrated")));
        assertFalse(Files.exists(legacy.resolve(name)));
        assertEquals(Map.of(0, "test:current_hat"), config.getEquippedCosmetics(player));
        assertTrue(config.getCreativeTreeBreaker(player));
        assertFalse(config.getShulkerPreview(player));
    }

    @Test
    public void legacyJsonUsesTheFirstFreeBackupWithoutDeletingEarlierCopies() throws Exception {
        Path legacy = temporary.newFolder().toPath();
        Path file = legacy.resolve("equipped-cosmetics.json");
        String original = "{\"global_settings\":{\"colorPickerX\":17}}";
        Files.writeString(file, original);
        Files.writeString(legacy.resolve("equipped-cosmetics.json.bak"), "first");
        Files.writeString(legacy.resolve("equipped-cosmetics.json.bak.1"), "second");
        new CosmeticsConfig(temporary.newFolder().toPath(), legacy);
        assertFalse(Files.exists(file));
        assertEquals("first", Files.readString(legacy.resolve("equipped-cosmetics.json.bak")));
        assertEquals("second", Files.readString(legacy.resolve("equipped-cosmetics.json.bak.1")));
        assertEquals(original, Files.readString(legacy.resolve("equipped-cosmetics.json.bak.2")));
    }

    private static CompoundTag record(UUID player, String cosmetic) {
        CompoundTag record = new CompoundTag();
        record.putUUID("player_uuid", player);
        CompoundTag equipped = new CompoundTag();
        equipped.putString("0", cosmetic);
        record.put("equipped_cosmetics", equipped);
        record.putBoolean("creative_tree_breaker", true);
        record.putBoolean("shulker_preview", false);
        return record;
    }
}
