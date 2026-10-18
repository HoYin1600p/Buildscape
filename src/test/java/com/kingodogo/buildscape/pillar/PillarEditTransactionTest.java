package com.kingodogo.buildscape.pillar;

import com.kingodogo.buildscape.config.PillarIdManager;
import com.kingodogo.buildscape.network.EditPillarRowsPacket;
import net.minecraft.core.BlockPos;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class PillarEditTransactionTest {
    @Test
    public void idSwapPreservesAppearanceAndRebuildsPositionIndex() throws Exception {
        PillarIdManager manager = manager();
        add(manager, "S-P1", 1);
        add(manager, "S-P2", 2);
        manager.getPillarData("S-P1").pattern = "spiral";
        manager.getPillarData("S-P1").displayedItem = "minecraft:diamond";
        manager.getPillarData("S-P1").dyeColors.add("#123456");
        assertEquals("", manager.editPillarRows(List.of(row("S-P1", "S-P2", 1), row("S-P2", "S-P1", 2))));
        assertEquals("spiral", manager.getPillarData("S-P2").pattern);
        assertEquals("minecraft:diamond", manager.getPillarData("S-P2").displayedItem);
        assertEquals(List.of("#123456"), manager.getPillarData("S-P2").dyeColors);
        assertEquals("S-P2", manager.getIdForPosition("minecraft:overworld:1:64:0"));
        assertEquals("S-P1", manager.getIdForPosition("minecraft:overworld:2:64:0"));
    }

    @Test
    public void rotationAcrossFormerChunkBoundaryIsOneBatch() throws Exception {
        PillarIdManager manager = manager();
        List<EditPillarRowsPacket.Row> edits = new ArrayList<>();
        for (int i = 0; i < 65; i++) {
            add(manager, "S-P" + i, i);
            edits.add(row("S-P" + i, "S-P" + ((i + 1) % 65), i));
        }
        assertEquals("", manager.editPillarRows(edits));
        assertEquals(64, manager.getPillarData("S-P0").x);
        assertEquals(65, manager.getPillarCount());
    }

    @Test
    public void missingOriginalRejectsWholeBatchWithoutMutatingLiveData() throws Exception {
        PillarIdManager manager = manager();
        add(manager, "S-P1", 1);
        PillarIdManager.PillarData original = manager.getPillarData("S-P1");
        assertEquals("missing", manager.editPillarRows(List.of(row("S-P1", "NewId", 3), row("Absent", "OtherId", 4))));
        assertSame(original, manager.getPillarData("S-P1"));
        assertNull(manager.getPillarData("NewId"));
        assertEquals(1, original.x);
    }

    @Test
    public void conflictsAreCheckedAgainstFinalIdsAndPositions() throws Exception {
        PillarIdManager manager = manager();
        add(manager, "S-P1", 1);
        add(manager, "S-P2", 2);
        assertEquals("id_conflict:S-P1:S-P2", manager.editPillarRows(List.of(row("S-P1", "S-P2", 3))));
        assertEquals("position_conflict:S-P1:NewId", manager.editPillarRows(List.of(row("S-P1", "NewId", 2))));
        assertEquals("", manager.editPillarRows(List.of(row("S-P1", "S-P1", 2), row("S-P2", "S-P2", 1))));
        assertEquals(2, manager.getPillarData("S-P1").x);
    }

    @Test
    public void interactiveDisplayedItemChangeRequestsSave() throws Exception {
        PillarIdManager manager = manager();
        add(manager, "S-P1", 1);
        manager.updateDisplayedItem("S-P1", "minecraft:diamond");
        Field requested = PillarIdManager.class.getDeclaredField("saveRequested");
        requested.setAccessible(true);
        assertTrue(requested.getBoolean(manager));
    }

    @Test
    public void nullIdUpperBlockKeepsNullAtRegisteredStackBottom() throws Exception {
        PillarIdManager manager = manager();
        add(manager, "NewId", 1);
        assertNull(reconcile(manager, null, new BlockPos(1, 64, 0)));
    }

    @Test
    public void renamedSavedIdAdoptsRegistryIdAtStackBottom() throws Exception {
        PillarIdManager manager = manager();
        add(manager, "NewId", 1);
        assertEquals("NewId", reconcile(manager, "OldId", new BlockPos(1, 64, 0)));
        assertEquals("OldId", reconcile(manager, "OldId", new BlockPos(2, 64, 0)));
    }

    @Test
    public void existingSavedIdIsNotReplacedByAnotherStacksId() throws Exception {
        PillarIdManager manager = manager();
        add(manager, "ExistingId", 2);
        add(manager, "NewId", 1);
        assertEquals("ExistingId", reconcile(manager, "ExistingId", new BlockPos(1, 64, 0)));
    }

    private static String reconcile(PillarIdManager manager, String savedId, BlockPos bottom) throws Exception {
        Method method = PillarIdManager.class.getDeclaredMethod("reconcilePillarId", String.class, String.class, BlockPos.class);
        method.setAccessible(true);
        return (String) method.invoke(manager, savedId, "minecraft:overworld", bottom);
    }

    private static PillarIdManager manager() throws Exception {
        Constructor<PillarIdManager> constructor = PillarIdManager.class.getDeclaredConstructor(boolean.class);
        constructor.setAccessible(true);
        return constructor.newInstance(true);
    }

    private static void add(PillarIdManager manager, String id, int x) {
        PillarIdManager.PillarData data = new PillarIdManager.PillarData();
        data.id = id;
        data.dimension = "minecraft:overworld";
        data.x = x;
        data.y = 64;
        manager.addPillarDataFromSync(data);
    }

    private static EditPillarRowsPacket.Row row(String oldId, String newId, int x) {
        return new EditPillarRowsPacket.Row(oldId, newId, "minecraft:overworld", x, 64, 0);
    }
}
