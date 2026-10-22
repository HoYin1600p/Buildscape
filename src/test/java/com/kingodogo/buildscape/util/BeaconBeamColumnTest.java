package com.kingodogo.buildscape.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public final class BeaconBeamColumnTest {
    private static final float[] WHITE = {1.0F, 1.0F, 1.0F};
    private static final float[] RED = {1.0F, 0.0F, 0.0F};
    private static final float[] BLUE = {0.0F, 0.0F, 1.0F};
    private static final Cell GLASS = new Cell("minecraft:glass", 0, false, null);
    private static final Cell TINTED = new Cell("minecraft:tinted_glass", 15, false, null);
    private static final Cell STONE = new Cell("minecraft:stone", 15, false, null);
    private static final Cell JAR = new Cell("buildscape:tinted_glass_jar", 1, false, null);

    @Test
    public void clearGlassThenTintedKeepsTwoWhiteBlocks() {
        Column column = new Column(GLASS, TINTED);
        column.tick();
        assertStopped(column, 2);
        assertEquals(1, column.sections.size());
        assertArrayEquals(WHITE, column.sections.get(0).color, 0.0F);
    }

    @Test
    public void stainedGlassKeepsSectionsAndBlendedColorsBelowTheStopper() {
        Column column = new Column(colored(RED), GLASS, colored(BLUE), TINTED);
        column.tick();
        assertStopped(column, 4);
        assertEquals(3, column.sections.size());
        assertArrayEquals(WHITE, column.sections.get(0).color, 0.0F);
        assertArrayEquals(RED, column.sections.get(1).color, 0.0F);
        assertEquals(2, column.sections.get(1).height);
        assertArrayEquals(new float[]{0.5F, 0.0F, 0.5F}, column.sections.get(2).color, 0.0F);
    }

    @Test
    public void directlyAdjacentStopperKeepsTheBeaconActive() {
        Column column = new Column(TINTED);
        column.tick();
        assertStopped(column, 1);
    }

    @Test
    public void ordinaryStoneClearsTheBeamAndDisablesEffects() {
        Column column = new Column(GLASS, STONE, TINTED);
        column.tick();
        assertTrue(column.sections.isEmpty());
        assertFalse(column.active());
        assertFalse(column.state.confirmedStopped());
        assertEquals(3, column.reads);
    }

    @Test
    public void ignoresStoneAndOtherStoppersAboveTheFirstStopper() {
        for (Cell above : new Cell[]{STONE, TINTED}) {
            Column column = new Column(GLASS, TINTED, above);
            column.tick();
            assertStopped(column, 2);
            assertEquals(3, column.reads);
        }
    }

    @Test
    public void jarPassesThroughEvenWhenTagged() {
        Column column = new Column(JAR, new Cell(JAR.id, 1, true, null), GLASS);
        column.tick();
        assertTrue(column.active());
        assertFalse(column.state.confirmedStopped());
        assertEquals(4, column.height());

        Column stopped = new Column(JAR, TINTED);
        stopped.tick();
        assertStopped(stopped, 2);
    }

    @Test
    public void transparentAndColoredTaggedBlocksStopBeforeTheirOwnSection() {
        for (Cell stopper : new Cell[]{
                new Cell("buildscape:tinted_glass_ornament", 0, false, null),
                new Cell("other:tinted_glass_panel", 0, false, BLUE),
                new Cell("other:beam_cap", 0, true, RED),
                new Cell("minecraft:bedrock", 15, true, null)}) {
            Column column = new Column(GLASS, stopper, STONE);
            column.tick();
            assertStopped(column, 2);
            assertEquals(1, column.sections.size());
        }
    }

    @Test
    public void untaggedBedrockKeepsVanillaBehavior() {
        Column column = new Column(new Cell("minecraft:bedrock", 15, false, null), GLASS);
        column.tick();
        assertTrue(column.active());
        assertFalse(column.state.confirmedStopped());
        assertEquals(3, column.height());
    }

    @Test
    public void removingStopperPublishesNewSectionsAndStateTogetherAfterSeveralTicks() {
        Cell[] blocks = new Cell[25];
        Arrays.fill(blocks, GLASS);
        blocks[0] = TINTED;
        Column column = new Column(blocks);
        column.tick();
        assertStopped(column, 1);
        List<Section> previous = column.sections;

        column.cells[1] = GLASS;
        column.tick();
        assertSame(previous, column.sections);
        assertTrue(column.state.confirmedStopped());
        column.tick();
        assertSame(previous, column.sections);
        assertTrue(column.state.confirmedStopped());
        column.tick();
        assertFalse(column.state.confirmedStopped());
        assertTrue(column.active());
        assertEquals(26, column.height());
    }

    @Test
    public void incrementalScanUsesTheBlockPresentWhenItReachesThatHeight() {
        Cell[] blocks = new Cell[25];
        Arrays.fill(blocks, GLASS);
        Column column = new Column(blocks);
        column.tick();
        assertEquals(10, column.reads);
        assertTrue(column.sections.isEmpty());
        assertFalse(column.state.confirmedStopped());

        column.cells[22] = TINTED;
        column.cells[23] = STONE;
        column.tick();
        assertEquals(20, column.reads);
        assertTrue(column.sections.isEmpty());
        assertFalse(column.state.confirmedStopped());
        column.tick();
        assertStopped(column, 22);
        assertEquals(23, column.reads);
    }

    private static Cell colored(float[] color) {
        return new Cell("minecraft:stained_glass", 0, false, color);
    }

    private static void assertStopped(Column column, int height) {
        assertTrue(column.state.confirmedStopped());
        assertTrue(column.active());
        assertEquals(height, column.height());
    }

    private record Cell(String id, int lightBlock, boolean tagged, float[] color) {
    }

    private static final class Section {
        private final float[] color;
        private int height = 1;

        private Section(float[] color) {
            this.color = color;
        }
    }

    /** Mirrors vanilla's ten-block scan, section construction and final publication with a valid base. */
    private static final class Column {
        private final Cell[] cells;
        private final BeaconBeamScanState state = new BeaconBeamScanState();
        private List<Section> sections = new ArrayList<>();
        private List<Section> checking = new ArrayList<>();
        private int lastCheckY = -1;
        private int reads;

        private Column(Cell... blocks) {
            this.cells = new Cell[blocks.length + 1];
            this.cells[0] = new Cell("minecraft:beacon", 0, false, WHITE);
            System.arraycopy(blocks, 0, this.cells, 1, blocks.length);
        }

        private void tick() {
            if (this.lastCheckY < 0) {
                this.state.beginScan();
                this.checking = new ArrayList<>();
            }
            int top = this.cells.length - 1;
            Section section = this.checking.isEmpty() ? null : this.checking.get(this.checking.size() - 1);
            for (int count = 0; count < 10 && this.lastCheckY < top; count++) {
                Cell cell = this.cells[this.lastCheckY + 1];
                this.reads++;
                boolean stopper = BeaconBeamStoppers.isStopper(cell.id, cell.tagged);
                float[] color = stopper ? null : cell.color;
                if (color != null) {
                    if (this.checking.size() <= 1) {
                        section = new Section(color);
                        this.checking.add(section);
                    } else if (section != null) {
                        if (Arrays.equals(color, section.color)) {
                            section.height++;
                        } else {
                            section = new Section(new float[]{
                                    (section.color[0] + color[0]) / 2.0F,
                                    (section.color[1] + color[1]) / 2.0F,
                                    (section.color[2] + color[2]) / 2.0F
                            });
                            this.checking.add(section);
                        }
                    }
                } else {
                    if (section != null && stopper) {
                        this.state.markStopped();
                    }
                    int lightBlock = stopper ? 15 : cell.lightBlock;
                    boolean bedrock = !this.state.pendingStopped() && cell.id.equals("minecraft:bedrock");
                    if (section == null || lightBlock >= 15 && !bedrock) {
                        if (!this.state.pendingStopped()) {
                            this.checking.clear();
                        }
                        this.lastCheckY = top;
                        break;
                    }
                    section.height++;
                }
                this.lastCheckY++;
            }
            if (this.lastCheckY >= top) {
                this.lastCheckY = -1;
                this.sections = this.checking;
                this.state.completeScan();
            }
        }

        private boolean active() {
            return !this.sections.isEmpty();
        }

        private int height() {
            return this.sections.stream().mapToInt(section -> section.height).sum();
        }
    }
}
