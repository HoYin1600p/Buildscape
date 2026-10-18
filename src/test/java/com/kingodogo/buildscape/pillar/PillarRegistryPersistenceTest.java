package com.kingodogo.buildscape.pillar;

import com.kingodogo.buildscape.config.PillarIdManager;
import com.kingodogo.buildscape.network.EditPillarRowsPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
    public void missingOrTruncatedMainUsesUndyedBackupButValidEmptyMainWins() throws Exception {
        PillarIdManager manager = manager();
        File main = new File(temporary.getRoot(), "pillar-ids.dat");
        File backup = temporary.newFile("pillar-ids.bak.dat");
        Files.writeString(backup.toPath(), "{\"S-P1\":{\"id\":\"S-P1\",\"dyeColors\":[]}}");
        assertEquals(Set.of("S-P1"), read(manager, main, backup).keySet());
        Files.writeString(main.toPath(), "");
        assertEquals(Set.of("S-P1"), read(manager, main, backup).keySet());
        Files.writeString(main.toPath(), "{}");
        assertTrue(read(manager, main, backup).isEmpty());
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

    @Test
    public void unloadedSwapAndRotationReconcileAfterSaveAndRestart() throws Exception {
        for (int count : new int[] {2, 3}) {
            PillarIdManager manager = manager();
            java.util.ArrayList<EditPillarRowsPacket.Row> rows = new java.util.ArrayList<>();
            for (int i = 0; i < count; i++) {
                String id = "Pillar" + (char) ('A' + i);
                PillarIdManager.PillarData data = frame(id, null, 1);
                data.pillarType = "buildscape:stone_pillar";
                data.x = i;
                data.y = 64;
                manager.addPillarDataFromSync(data);
                rows.add(new EditPillarRowsPacket.Row(id, "Pillar" + (char) ('A' + (i + 1) % count),
                        "minecraft:overworld", i, 64, 0));
            }
            load(manager, manager.copyDataSnapshot());
            assertEquals("", manager.editPillarRows(rows));
            File saved = new File(temporary.getRoot(), "rotated-" + count + ".dat");
            Method save = PillarIdManager.class.getDeclaredMethod("saveToFile", File.class, String.class);
            save.setAccessible(true);
            assertEquals(Boolean.TRUE, save.invoke(manager, saved, "rotated-" + count));
            PillarIdManager restarted = manager();
            load(restarted, read(restarted, saved, new File(temporary.getRoot(), "missing")));
            Method reconcile = PillarIdManager.class.getDeclaredMethod("reconcilePillarId",
                    String.class, String.class, BlockPos.class);
            reconcile.setAccessible(true);
            for (int i = 0; i < count; i++) {
                assertEquals("Pillar" + (char) ('A' + (i + 1) % count), reconcile.invoke(restarted,
                        "Pillar" + (char) ('A' + i), "minecraft:overworld", new BlockPos(i, 64, 0)));
            }
        }
    }

    @Test
    public void joinedStacksKeepSavedIdsAcrossRestartsWithDeterministicPositionOwner() throws Exception {
        Method rebuild = PillarIdManager.class.getDeclaredMethod("rebuildPositionIndex");
        rebuild.setAccessible(true);
        Method save = PillarIdManager.class.getDeclaredMethod("saveToFile", File.class, String.class);
        save.setAccessible(true);
        Method reconcile = PillarIdManager.class.getDeclaredMethod("reconcilePillarId",
                String.class, String.class, BlockPos.class, BlockPos.class);
        reconcile.setAccessible(true);
        for (long lowerTime : new long[] {10, 20, 30}) {
            PillarIdManager manager = manager();
            PillarIdManager.PillarData lower = frame("PillarP", null, lowerTime);
            lower.pillarType = "buildscape:stone_pillar";
            lower.y = 64;
            lower.dyeColors.add("#123456");
            PillarIdManager.PillarData upper = frame("PillarQ", null, 20);
            upper.pillarType = "buildscape:stone_pillar";
            upper.y = 66;
            upper.dyeColors.add("#654321");
            load(manager, Map.of(lower.id, lower, upper.id, upper));
            upper.y = 64;
            rebuild.invoke(manager);
            String expectedOwner = lowerTime < 20 ? "PillarQ" : "PillarP";
            for (int restart = 0; restart < 3; restart++) {
                assertEquals(expectedOwner, manager.getIdForPosition("minecraft:overworld:0:64:0"));
                assertEquals(Set.of("PillarP", "PillarQ"), manager.getAllPillarIds());
                BlockPos bottom = new BlockPos(0, 64, 0);
                assertEquals("PillarP", reconcile.invoke(manager, "PillarP", "minecraft:overworld", bottom, bottom));
                for (int y = 65; y <= 67; y++) {
                    assertEquals("PillarQ", reconcile.invoke(manager, "PillarQ", "minecraft:overworld",
                            new BlockPos(0, y, 0), bottom));
                }
                assertEquals(List.of("#123456"), manager.getPillarData("PillarP").dyeColors);
                assertEquals(List.of("#654321"), manager.getPillarData("PillarQ").dyeColors);
                File saved = temporary.newFile();
                assertEquals(Boolean.TRUE, save.invoke(manager, saved, saved.getName()));
                PillarIdManager restarted = manager();
                load(restarted, read(restarted, saved, new File(temporary.getRoot(), "missing")));
                manager = restarted;
            }
        }
    }

    @Test
    public void renamedFramesReconcileByPositionAndFacingAfterRestart() throws Exception {
        for (String type : List.of("minecraft:item_frame", "buildscape:blue_item_frame")) {
            PillarIdManager manager = manager();
            PillarIdManager.PillarData north = frame("FrameA", "north", 1);
            north.pillarType = type;
            PillarIdManager.PillarData south = frame("FrameB", "south", 1);
            south.pillarType = type;
            load(manager, Map.of("FrameA", north, "FrameB", south));
            assertEquals("", manager.editPillarRows(List.of(
                    new EditPillarRowsPacket.Row("FrameA", "FrameB", "minecraft:overworld", 0, 0, 0),
                    new EditPillarRowsPacket.Row("FrameB", "FrameA", "minecraft:overworld", 0, 0, 0))));
            File saved = temporary.newFile();
            Method save = PillarIdManager.class.getDeclaredMethod("saveToFile", File.class, String.class);
            save.setAccessible(true);
            assertEquals(Boolean.TRUE, save.invoke(manager, saved, saved.getName()));
            PillarIdManager restarted = manager();
            load(restarted, read(restarted, saved, new File(temporary.getRoot(), "missing")));
            Method reconcile = PillarIdManager.class.getDeclaredMethod("reconcileFrameId",
                    String.class, String.class, BlockPos.class, Direction.class);
            reconcile.setAccessible(true);
            assertEquals("FrameB", reconcile.invoke(restarted, "FrameA", "minecraft:overworld", BlockPos.ZERO, Direction.NORTH));
            assertEquals("FrameA", reconcile.invoke(restarted, "FrameB", "minecraft:overworld", BlockPos.ZERO, Direction.SOUTH));
            assertEquals("SavedId", reconcile.invoke(restarted, "SavedId", "minecraft:overworld", BlockPos.ZERO, Direction.EAST));
            assertEquals("SavedId", reconcile.invoke(restarted, "SavedId", "test:other", BlockPos.ZERO, Direction.NORTH));
            assertEquals("FrameB", reconcile.invoke(restarted, "FrameB", "minecraft:overworld", BlockPos.ZERO, Direction.NORTH));
            assertEquals(java.util.Set.of("FrameA", "FrameB"), restarted.getAllPillarIds());
        }
    }
}
