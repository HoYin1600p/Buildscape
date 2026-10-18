package com.kingodogo.buildscape.pillar;

import com.kingodogo.buildscape.config.PillarIdManager;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class PillarRegistryPersistenceTest {

    @Rule
    public TemporaryFolder temporary = new TemporaryFolder();

    @Test
    public void validMainWithoutColorsWinsOverColoredBackup() throws Exception {
        PillarIdManager manager = manager();
        File main = temporary.newFile("pillar-ids.dat");
        File backup = temporary.newFile("pillar-ids.bak.dat");
        String mainJson = "{\"S-P1\":{\"id\":\"S-P1\",\"dimension\":\"minecraft:overworld\",\"dyeColors\":[],\"modifiedTime\":200}}";
        Files.writeString(main.toPath(), mainJson);
        Files.writeString(backup.toPath(), "{\"S-P2\":{\"id\":\"S-P2\",\"dyeColors\":[\"#123456\"],\"modifiedTime\":100}}");
        Map<String, PillarIdManager.PillarData> selected = read(manager, main, backup);
        assertEquals(Set.of("S-P1"), selected.keySet());
        assertTrue(selected.get("S-P1").dyeColors.isEmpty());
        assertEquals(mainJson, Files.readString(main.toPath()));
    }

    @Test
    public void missingOrEmptyMainUsesUndyedBackup() throws Exception {
        PillarIdManager manager = manager();
        File main = new File(temporary.getRoot(), "pillar-ids.dat");
        File backup = temporary.newFile("pillar-ids.bak.dat");
        Files.writeString(backup.toPath(), "{\"S-P1\":{\"id\":\"S-P1\",\"dyeColors\":[]}}");
        assertEquals(Set.of("S-P1"), read(manager, main, backup).keySet());
        Files.writeString(main.toPath(), "{}");
        assertEquals(Set.of("S-P1"), read(manager, main, backup).keySet());
    }

    @Test
    public void corruptMainIsPreservedBeforeBackupRecovery() throws Exception {
        PillarIdManager manager = manager();
        File main = temporary.newFile("pillar-ids.dat");
        File backup = temporary.newFile("pillar-ids.bak.dat");
        String damaged = "{unreadable";
        Files.writeString(main.toPath(), damaged);
        Files.writeString(backup.toPath(), "{\"S-P1\":{\"id\":\"S-P1\",\"dyeColors\":[]}}");
        assertEquals(Set.of("S-P1"), read(manager, main, backup).keySet());
        File[] preserved = temporary.getRoot().listFiles((dir, name) -> name.startsWith("pillar-ids.dat.corrupted."));
        assertNotNull(preserved);
        assertEquals(1, preserved.length);
        assertEquals(damaged, Files.readString(preserved[0].toPath()));
        assertEquals(damaged, Files.readString(main.toPath()));
    }

    @Test
    public void corruptMainWithoutBackupRetainsSizeGuardAndOriginal() throws Exception {
        PillarIdManager manager = manager();
        File main = temporary.newFile("pillar-ids.dat");
        Files.writeString(main.toPath(), "{broken");
        assertTrue(read(manager, main, new File(temporary.getRoot(), "missing-backup")).isEmpty());
        Field size = PillarIdManager.class.getDeclaredField("lastFileSize");
        size.setAccessible(true);
        assertEquals(main.length(), size.getLong(manager));
        assertEquals("{broken", Files.readString(main.toPath()));
    }

    @Test
    public void pruningKeepsNewestFramePerFacingAndPreservesPillars() throws Exception {
        PillarIdManager manager = manager();
        PillarIdManager.PillarData older = frame("I-F1", "north", 10);
        PillarIdManager.PillarData newer = frame("I-F2", "north", 20);
        PillarIdManager.PillarData otherFacing = frame("I-F3", "south", 5);
        PillarIdManager.PillarData empty = frame("I-F4", "east", 1);
        empty.pattern = "none";
        PillarIdManager.PillarData removedDimension = frame("I-F5", "west", 1);
        removedDimension.dimension = "test:deleted";
        PillarIdManager.PillarData undyedPillar = frame("S-P1", null, 1);
        undyedPillar.pillarType = "buildscape:stone_pillar";
        undyedPillar.pattern = null;
        for (PillarIdManager.PillarData data : List.of(older, newer, otherFacing, empty, removedDimension, undyedPillar)) {
            manager.addPillarDataFromSync(data);
        }
        Method prune = PillarIdManager.class.getDeclaredMethod("pruneRegistry");
        prune.setAccessible(true);
        assertEquals(Boolean.TRUE, prune.invoke(manager));
        assertEquals(Set.of("I-F2", "I-F3", "I-F5", "S-P1"), manager.getAllPillarIds());
        assertEquals("I-F2", manager.getIdForPosition("minecraft:overworld:0:0:0:north"));
        assertEquals(Boolean.FALSE, prune.invoke(manager));
    }

    @Test
    public void pillarInUnknownDimensionSurvivesLoadAndSave() throws Exception {
        PillarIdManager manager = manager();
        PillarIdManager.PillarData pillar = frame("S-P9", null, 1);
        pillar.pillarType = "buildscape:stone_pillar";
        pillar.pattern = null;
        pillar.dimension = "the_vault:vault";
        load(manager, Map.of("S-P9", pillar));
        assertEquals(Set.of("S-P9"), manager.getAllPillarIds());
        assertEquals("the_vault:vault", manager.getPillarData("S-P9").dimension);

        File saved = new File(temporary.getRoot(), "saved-pillars.dat");
        Method save = PillarIdManager.class.getDeclaredMethod("saveToFile", File.class, String.class);
        save.setAccessible(true);
        assertEquals(Boolean.TRUE, save.invoke(manager, saved, "saved-pillars"));
        Map<String, PillarIdManager.PillarData> reread = read(manager(), saved, new File(temporary.getRoot(), "none"));
        assertEquals(Set.of("S-P9"), reread.keySet());
        assertEquals("the_vault:vault", reread.get("S-P9").dimension);
    }

    @Test
    public void itemFrameWithDataInUnknownDimensionSurvivesLoad() throws Exception {
        PillarIdManager manager = manager();
        PillarIdManager.PillarData withData = frame("I-F9", "north", 1);
        withData.dimension = "the_vault:vault";
        PillarIdManager.PillarData noData = frame("I-F8", "south", 1);
        noData.dimension = "the_vault:vault";
        noData.pattern = "none";
        load(manager, Map.of("I-F9", withData, "I-F8", noData));
        assertEquals(Set.of("I-F9"), manager.getAllPillarIds());
        assertEquals("spiral", manager.getPillarData("I-F9").pattern);
    }

    private static void load(PillarIdManager manager, Map<String, PillarIdManager.PillarData> data) throws Exception {
        Method process = PillarIdManager.class.getDeclaredMethod("processLoadedData",
                Map.class, net.minecraft.server.MinecraftServer.class, File.class);
        process.setAccessible(true);
        process.invoke(manager, data, null, null);
    }

    private static PillarIdManager.PillarData frame(String id, String facing, long modifiedTime) {
        PillarIdManager.PillarData data = new PillarIdManager.PillarData();
        data.id = id;
        data.dimension = "minecraft:overworld";
        data.pillarType = "minecraft:item_frame";
        data.pattern = "spiral";
        data.facing = facing;
        data.modifiedTime = modifiedTime;
        return data;
    }

    @Test
    public void frameRegistrationRequiresPatternOrColors() throws Exception {
        Method hasData = PillarIdManager.class.getDeclaredMethod("hasFrameParticleData", String.class, List.class);
        hasData.setAccessible(true);
        for (String pattern : new String[] {null, "", "none", "__not_set__"}) {
            assertEquals(Boolean.FALSE, hasData.invoke(null, pattern, List.of()));
            assertEquals(Boolean.TRUE, hasData.invoke(null, pattern, List.of("#123456")));
        }
        assertEquals(Boolean.TRUE, hasData.invoke(null, "spiral", List.of()));
    }

    @Test
    public void replacementRemovesPreviousFrameAtSamePosition() throws Exception {
        PillarIdManager manager = manager();
        manager.addPillarDataFromSync(frame("I-F1", "north", 10));
        Method remove = PillarIdManager.class.getDeclaredMethod("removeOtherFrameAtPosition", String.class, String.class);
        remove.setAccessible(true);
        assertEquals(Boolean.TRUE, remove.invoke(manager, "minecraft:overworld:0:0:0:north", "I-F2"));
        assertNull(manager.getPillarData("I-F1"));
        assertNull(manager.getIdForPosition("minecraft:overworld:0:0:0:north"));
    }

    private static PillarIdManager manager() throws Exception {
        Constructor<PillarIdManager> constructor = PillarIdManager.class.getDeclaredConstructor(boolean.class);
        constructor.setAccessible(true);
        return constructor.newInstance(true);
    }

    @Test
    public void framesOnDifferentFacesKeepSeparatePatternsWithoutPositionFallback() throws Exception {
        PillarIdManager manager = manager();
        PillarIdManager.PillarData north = frame("F-N1", "north", 10);
        PillarIdManager.PillarData south = frame("F-S1", "south", 10);
        south.pattern = "beam";
        PillarIdManager.PillarData stale = frame("F-OLD", null, 1);
        stale.pattern = "fountain";
        for (PillarIdManager.PillarData data : List.of(north, south, stale)) {
            manager.addPillarDataFromSync(data);
        }
        String dimension = "minecraft:overworld";
        net.minecraft.core.BlockPos pos = net.minecraft.core.BlockPos.ZERO;
        assertEquals("spiral", manager.getPillarData(manager.getFrameIdForPosition(
                dimension, pos, net.minecraft.core.Direction.NORTH)).pattern);
        assertEquals("beam", manager.getPillarData(manager.getFrameIdForPosition(
                dimension, pos, net.minecraft.core.Direction.SOUTH)).pattern);
        assertNull(manager.getFrameIdForPosition(dimension, pos, net.minecraft.core.Direction.EAST));
        assertNull(manager.getFrameIdForPosition(dimension, pos, null));

        Method remove = PillarIdManager.class.getDeclaredMethod("removeOtherFrameAtPosition", String.class, String.class);
        remove.setAccessible(true);
        assertEquals(Boolean.TRUE, remove.invoke(manager, manager.positionKey(
                dimension, pos, net.minecraft.core.Direction.NORTH), "F-N2"));
        PillarIdManager.PillarData replacement = frame("F-N2", "north", 20);
        replacement.pattern = "ring";
        manager.addPillarDataFromSync(replacement);
        assertNull(manager.getPillarData("F-N1"));
        assertEquals("ring", manager.getPillarData(manager.getFrameIdForPosition(
                dimension, pos, net.minecraft.core.Direction.NORTH)).pattern);
        assertEquals("beam", manager.getPillarData(manager.getFrameIdForPosition(
                dimension, pos, net.minecraft.core.Direction.SOUTH)).pattern);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, PillarIdManager.PillarData> read(PillarIdManager manager, File main, File backup) throws Exception {
        Method read = PillarIdManager.class.getDeclaredMethod("readSavedData", File.class, File.class);
        read.setAccessible(true);
        Object saved = read.invoke(manager, main, backup);
        Field data = saved.getClass().getDeclaredField("data");
        data.setAccessible(true);
        return (Map<String, PillarIdManager.PillarData>) data.get(saved);
    }
}
