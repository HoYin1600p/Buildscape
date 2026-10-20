package com.kingodogo.buildscape.feature.pillar;

import com.kingodogo.buildscape.network.EditPillarRowsPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/** Detail-editor changes to item frames must survive the frame registering again. */
public class FrameEditPersistenceTest {

    private static final String[] FRAME_TYPES = {"minecraft:item_frame", "buildscape:white_item_frame"};

    @Rule
    public TemporaryFolder temporary = new TemporaryFolder();

    @Test
    public void editedFrameKeepsEditedValuesWhenItRegistersAgain() throws Exception {
        for (String type : FRAME_TYPES) {
            PillarIdManager manager = manager();
            load(manager, Map.of("FrameA", frame("FrameA", type)));
            CompoundTag entity = entityTag("FrameA");

            edit(manager, "FrameA", "ring", "#445566");
            assertTrue(type, apply(manager, "FrameA", entity, false));

            assertEquals(type, "ring", entity.getString("BuildScapeParticlePattern"));
            assertEquals(type, List.of("#445566"), colors(entity));
            assertEquals(type, "ring", manager.getPillarData("FrameA").pattern);
            assertEquals(type, List.of("#445566"), manager.getPillarData("FrameA").dyeColors);
            assertNull(type, manager.getPillarData("FrameA").frameEditPending);
            assertEquals(type, "retained", entity.getString("OtherModData"));
        }
    }

    @Test
    public void editKeepsEntityDataOfSeveralColoursAndClearedColours() throws Exception {
        PillarIdManager manager = manager();
        load(manager, Map.of("FrameA", frame("FrameA", FRAME_TYPES[1])));
        CompoundTag entity = entityTag("FrameA");
        edit(manager, "FrameA", "none", "#AA0000", "#00BB00", "#0000CC");
        assertTrue(apply(manager, "FrameA", entity, false));
        assertEquals(List.of("#AA0000", "#00BB00", "#0000CC"), colors(entity));

        CompoundTag second = entityTag("FrameA");
        edit(manager, "FrameA", "beam");
        assertTrue(apply(manager, "FrameA", second, false));
        assertEquals("beam", second.getString("BuildScapeParticlePattern"));
        assertTrue(colors(second).isEmpty());
    }

    @Test
    public void unloadedFrameEditSurvivesSaveAndRestart() throws Exception {
        for (String type : FRAME_TYPES) {
            PillarIdManager manager = manager();
            load(manager, Map.of("FrameA", frame("FrameA", type)));
            CompoundTag unloaded = entityTag("FrameA");

            edit(manager, "FrameA", "ring", "#445566");
            manager = restart(manager);
            assertEquals(type, Boolean.TRUE, manager.getPillarData("FrameA").frameEditPending);
            assertEquals(type, "ring", manager.getPillarData("FrameA").pattern);

            assertTrue(apply(manager, "FrameA", unloaded, false));
            assertEquals(type, "ring", unloaded.getString("BuildScapeParticlePattern"));
            assertEquals(type, List.of("#445566"), colors(unloaded));
            assertEquals(type, "ring", manager.getPillarData("FrameA").pattern);

            manager = restart(manager);
            assertNull(type, manager.getPillarData("FrameA").frameEditPending);
            assertEquals(type, "ring", manager.getPillarData("FrameA").pattern);
            assertEquals(type, List.of("#445566"), manager.getPillarData("FrameA").dyeColors);
        }
    }

    @Test
    public void physicalDyeAfterAnEditWins() throws Exception {
        for (String type : FRAME_TYPES) {
            PillarIdManager manager = manager();
            load(manager, Map.of("FrameA", frame("FrameA", type)));
            CompoundTag entity = entityTag("FrameA");
            edit(manager, "FrameA", "ring", "#445566");

            // The player dyes the frame: the entity changes and registers straight away.
            entity.putString("BuildScapeParticlePattern", "pulse");
            ListTag dyed = new ListTag();
            dyed.add(StringTag.valueOf("#FF0000"));
            entity.put("BuildScapeParticleColors", dyed);
            assertFalse(type, apply(manager, "FrameA", entity, true));

            assertNull(type, manager.getPillarData("FrameA").frameEditPending);
            assertEquals(type, "pulse", entity.getString("BuildScapeParticlePattern"));
            assertEquals(type, List.of("#FF0000"), colors(entity));
            // A later load must not bring the old edit back.
            assertFalse(type, apply(manager, "FrameA", entity, false));
            assertEquals(type, List.of("#FF0000"), colors(entity));
        }
    }

    @Test
    public void editAfterPhysicalChangeWinsToo() throws Exception {
        PillarIdManager manager = manager();
        load(manager, Map.of("FrameA", frame("FrameA", FRAME_TYPES[0])));
        CompoundTag entity = entityTag("FrameA");
        edit(manager, "FrameA", "ring", "#445566");
        assertFalse(apply(manager, "FrameA", entity, true));
        edit(manager, "FrameA", "burst", "#778899");
        assertTrue(apply(manager, "FrameA", entity, false));
        assertEquals("burst", entity.getString("BuildScapeParticlePattern"));
        assertEquals(List.of("#778899"), colors(entity));
    }

    @Test
    public void frameWithoutAnEditRegistersFromItsEntityAsBefore() throws Exception {
        PillarIdManager manager = manager();
        load(manager, Map.of("FrameA", frame("FrameA", FRAME_TYPES[0])));
        CompoundTag entity = entityTag("FrameA");
        CompoundTag expected = entity.copy();
        assertFalse(apply(manager, "FrameA", entity, false));
        assertEquals(expected, entity);
        assertFalse(apply(manager, "Unknown", entity, false));
    }

    @Test
    public void pillarBlocksAreNotMarkedPending() throws Exception {
        PillarIdManager manager = manager();
        PillarIdManager.PillarData pillar = new PillarIdManager.PillarData();
        pillar.id = "S-P1";
        pillar.dimension = "minecraft:overworld";
        pillar.pillarType = "buildscape:stone_pillar";
        pillar.pattern = "ring";
        load(manager, Map.of("S-P1", pillar));
        manager.applyFrameEdit(manager.getPillarData("S-P1"));
        assertNull(manager.getPillarData("S-P1").frameEditPending);
        manager.applyFrameEdit(null);
    }

    @Test
    public void pendingFrameWithoutParticleDataIsNotPrunedUntilItIsApplied() throws Exception {
        PillarIdManager manager = manager();
        PillarIdManager.PillarData cleared = frame("FrameA", FRAME_TYPES[0]);
        cleared.pattern = "none";
        cleared.dyeColors = new ArrayList<>();
        cleared.frameEditPending = Boolean.TRUE;
        PillarIdManager.PillarData stale = frame("FrameB", FRAME_TYPES[0]);
        stale.facing = "south";
        stale.pattern = "none";
        stale.dyeColors = new ArrayList<>();
        load(manager, Map.of("FrameA", cleared, "FrameB", stale));
        assertEquals(java.util.Set.of("FrameA"), manager.getAllPillarIds());
    }

    @Test
    public void pendingEditSurvivesAFrameRename() throws Exception {
        PillarIdManager manager = manager();
        load(manager, Map.of("FrameA", frame("FrameA", FRAME_TYPES[0])));
        edit(manager, "FrameA", "ring", "#445566");
        assertEquals("", manager.editPillarRows(List.of(
                new EditPillarRowsPacket.Row("FrameA", "FrameB", "minecraft:overworld", 0, 0, 0))));
        PillarIdManager.PillarData renamed = manager.getPillarData("FrameB");
        assertEquals(Boolean.TRUE, renamed.frameEditPending);
        CompoundTag entity = entityTag("FrameB");
        assertTrue(apply(manager, "FrameB", entity, false));
        assertEquals("ring", entity.getString("BuildScapeParticlePattern"));
    }

    @Test
    public void resetAndRemovalStillClearEditedFramesAndAllowNewFramesAtTheFace() throws Exception {
        for (boolean reset : new boolean[] {true, false}) {
            PillarIdManager manager = manager();
            load(manager, Map.of("FrameA", frame("FrameA", FRAME_TYPES[0])));
            edit(manager, "FrameA", "ring", "#445566");
            assertTrue(reset ? manager.resetPillars(List.of("FrameA")) : manager.removePillars(List.of("FrameA")));
            manager = restart(manager);
            assertTrue(manager.getAllPillarIds().isEmpty());
            CompoundTag stale = entityTag("FrameA");
            assertFalse(prepare(manager, stale));
            assertEquals(java.util.Set.of("OtherModData"), stale.getAllKeys());
            assertTrue(prepare(manager, entityTag("NewFrame")));
        }
    }

    private static void edit(PillarIdManager manager, String id, String pattern, String... newColors) {
        PillarIdManager.PillarData data = manager.getPillarData(id);
        data.pattern = pattern;
        data.dyeColors = new ArrayList<>(List.of(newColors));
        data.modifiedTime = System.currentTimeMillis();
        manager.applyFrameEdit(data);
        assertEquals("The frame is not loaded in a unit test", Boolean.TRUE, data.frameEditPending);
    }

    private static boolean apply(PillarIdManager manager, String id, CompoundTag tag, boolean immediate) throws Exception {
        Method apply = PillarIdManager.class.getDeclaredMethod("applyPendingFrameEdit",
                PillarIdManager.PillarData.class, CompoundTag.class, boolean.class);
        apply.setAccessible(true);
        return (Boolean) apply.invoke(manager, manager.getPillarData(id), tag, immediate);
    }

    private static boolean prepare(PillarIdManager manager, CompoundTag tag) throws Exception {
        Method prepare = PillarIdManager.class.getDeclaredMethod("prepareFrameRegistration", CompoundTag.class,
                String.class, net.minecraft.core.BlockPos.class, net.minecraft.core.Direction.class);
        prepare.setAccessible(true);
        return (Boolean) prepare.invoke(manager, tag, "minecraft:overworld", net.minecraft.core.BlockPos.ZERO,
                net.minecraft.core.Direction.NORTH);
    }

    private static List<String> colors(CompoundTag tag) {
        List<String> result = new ArrayList<>();
        ListTag list = tag.getList("BuildScapeParticleColors", 8);
        for (int i = 0; i < list.size(); i++) {
            result.add(list.getString(i));
        }
        return result;
    }

    private static CompoundTag entityTag(String id) {
        CompoundTag tag = new CompoundTag();
        tag.putString("BuildScapeFrameId", id);
        tag.putString("BuildScapeParticlePattern", "beam");
        ListTag list = new ListTag();
        list.add(StringTag.valueOf("#112233"));
        tag.put("BuildScapeParticleColors", list);
        tag.putString("OtherModData", "retained");
        return tag;
    }

    private static PillarIdManager.PillarData frame(String id, String type) {
        PillarIdManager.PillarData data = new PillarIdManager.PillarData();
        data.id = id;
        data.dimension = "minecraft:overworld";
        data.pillarType = type;
        data.pattern = "beam";
        data.dyeColors = new ArrayList<>(List.of("#112233"));
        data.facing = "north";
        data.modifiedTime = 10;
        return data;
    }

    private PillarIdManager restart(PillarIdManager manager) throws Exception {
        File saved = temporary.newFile();
        Method save = PillarIdManager.class.getDeclaredMethod("saveToFile", File.class, String.class);
        save.setAccessible(true);
        assertEquals(Boolean.TRUE, save.invoke(manager, saved, saved.getName()));
        PillarIdManager restarted = manager();
        Method read = PillarIdManager.class.getDeclaredMethod("readSavedData", File.class, File.class);
        read.setAccessible(true);
        Object savedData = read.invoke(restarted, saved, new File(temporary.getRoot(), "missing"));
        java.lang.reflect.Field field = savedData.getClass().getDeclaredField("data");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, PillarIdManager.PillarData> data = (Map<String, PillarIdManager.PillarData>) field.get(savedData);
        load(restarted, data);
        return restarted;
    }

    private static void load(PillarIdManager manager, Map<String, PillarIdManager.PillarData> data) throws Exception {
        Method process = PillarIdManager.class.getDeclaredMethod("processLoadedData",
                Map.class, net.minecraft.server.MinecraftServer.class, File.class);
        process.setAccessible(true);
        process.invoke(manager, data, null, null);
    }

    private static PillarIdManager manager() throws Exception {
        Constructor<PillarIdManager> constructor = PillarIdManager.class.getDeclaredConstructor(boolean.class);
        constructor.setAccessible(true);
        return constructor.newInstance(true);
    }
}
