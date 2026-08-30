package com.kingodogo.buildscape.pipe.transport;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * Encapsulates the active fluid transport state of an individual Hollow Steel Pipe.
 * Completely decoupled from the structural blockstate (such as WATERLOGGED).
 */
public class PipeFlowState {
    private boolean hasWater;
    private boolean isSource;
    private final Set<Direction> flowDirections = EnumSet.noneOf(Direction.class);
    @Nullable
    private Direction inflowDirection = null;
    private BubbleColumnState bubbleColumn = BubbleColumnState.NONE;
    private int distance = 0;
    /** Total distance from source to the furthest pipe in this network section. Used for slope computation. */
    private int maxDistance = 0;
    /** True when this pipe has an open endpoint (flow exits the network here). Used for slope rendering. */
    private boolean isOpenEndpoint = false;

    public PipeFlowState() {
    }

    public PipeFlowState(boolean hasWater, boolean isSource, Collection<Direction> flowDirections, @Nullable Direction inflowDirection, BubbleColumnState bubbleColumn, int distance, int maxDistance, boolean isOpenEndpoint) {
        this.hasWater = hasWater;
        this.isSource = isSource;
        if (flowDirections != null) {
            this.flowDirections.addAll(flowDirections);
        }
        this.inflowDirection = inflowDirection;
        this.bubbleColumn = bubbleColumn != null ? bubbleColumn : BubbleColumnState.NONE;
        this.distance = distance;
        this.maxDistance = maxDistance;
        this.isOpenEndpoint = isOpenEndpoint;
    }

    public PipeFlowState(boolean hasWater, boolean isSource, Collection<Direction> flowDirections, BubbleColumnState bubbleColumn, int distance, int maxDistance, boolean isOpenEndpoint) {
        this(hasWater, isSource, flowDirections, null, bubbleColumn, distance, maxDistance, isOpenEndpoint);
    }

    /** Convenience constructor (legacy - maxDistance=0, isOpenEndpoint=false) */
    public PipeFlowState(boolean hasWater, boolean isSource, Collection<Direction> flowDirections, BubbleColumnState bubbleColumn, int distance) {
        this(hasWater, isSource, flowDirections, null, bubbleColumn, distance, 0, false);
    }

    public boolean hasWater() {
        return hasWater;
    }

    public void setHasWater(boolean hasWater) {
        this.hasWater = hasWater;
    }

    public boolean isSource() {
        return isSource;
    }

    public void setSource(boolean source) {
        isSource = source;
    }

    public Set<Direction> getFlowDirections() {
        return flowDirections;
    }

    public void setFlowDirections(Collection<Direction> dirs) {
        this.flowDirections.clear();
        if (dirs != null) {
            this.flowDirections.addAll(dirs);
        }
    }

    public void addFlowDirection(Direction dir) {
        if (dir != null) {
            this.flowDirections.add(dir);
        }
    }

    public boolean hasFlowDirection(Direction dir) {
        return this.flowDirections.contains(dir);
    }

    public void clearFlowDirections() {
        this.flowDirections.clear();
    }

    @Nullable
    public Direction getInflowDirection() {
        return inflowDirection;
    }

    public void setInflowDirection(@Nullable Direction inflowDirection) {
        this.inflowDirection = inflowDirection;
    }

    public BubbleColumnState getBubbleColumn() {
        return bubbleColumn;
    }

    public void setBubbleColumn(BubbleColumnState bubbleColumn) {
        this.bubbleColumn = bubbleColumn != null ? bubbleColumn : BubbleColumnState.NONE;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getMaxDistance() { return maxDistance; }
    public void setMaxDistance(int maxDistance) { this.maxDistance = Math.max(0, maxDistance); }

    public boolean isOpenEndpoint() { return isOpenEndpoint; }
    public void setOpenEndpoint(boolean openEndpoint) { isOpenEndpoint = openEndpoint; }

    public boolean isEmpty() {
        return !hasWater && !isSource && flowDirections.isEmpty() && inflowDirection == null
                && bubbleColumn == BubbleColumnState.NONE && distance == 0 && maxDistance == 0 && !isOpenEndpoint;
    }

    public void clear() {
        this.hasWater = false;
        this.isSource = false;
        this.flowDirections.clear();
        this.inflowDirection = null;
        this.bubbleColumn = BubbleColumnState.NONE;
        this.distance = 0;
        this.maxDistance = 0;
        this.isOpenEndpoint = false;
    }

    public PipeFlowState copy() {
        return new PipeFlowState(this.hasWater, this.isSource, this.flowDirections, this.inflowDirection, this.bubbleColumn, this.distance, this.maxDistance, this.isOpenEndpoint);
    }

    public CompoundTag writeToNbt(CompoundTag tag) {
        tag.putBoolean("HasWater", hasWater);
        tag.putBoolean("IsSource", isSource);
        tag.putString("BubbleColumn", bubbleColumn.getSerializedName());
        tag.putInt("Distance", distance);
        tag.putInt("MaxDistance", maxDistance);
        tag.putBoolean("IsOpenEndpoint", isOpenEndpoint);

        if (inflowDirection != null) {
            tag.putString("InflowDir", inflowDirection.getName());
        }

        ListTag list = new ListTag();
        for (Direction dir : flowDirections) {
            list.add(StringTag.valueOf(dir.getName()));
        }
        tag.put("FlowDirs", list);
        return tag;
    }

    public static PipeFlowState readFromNbt(CompoundTag tag) {
        PipeFlowState state = new PipeFlowState();
        if (tag == null) return state;

        state.hasWater = tag.getBoolean("HasWater");
        state.isSource = tag.getBoolean("IsSource");
        state.bubbleColumn = BubbleColumnState.byName(tag.getString("BubbleColumn"));
        state.distance = tag.getInt("Distance");
        state.maxDistance = tag.contains("MaxDistance") ? tag.getInt("MaxDistance") : 0;
        state.isOpenEndpoint = tag.contains("IsOpenEndpoint") && tag.getBoolean("IsOpenEndpoint");

        if (tag.contains("InflowDir")) {
            state.inflowDirection = Direction.byName(tag.getString("InflowDir"));
        }

        if (tag.contains("FlowDirs", Tag.TAG_LIST)) {
            ListTag list = tag.getList("FlowDirs", Tag.TAG_STRING);
            for (int i = 0; i < list.size(); i++) {
                Direction d = Direction.byName(list.getString(i));
                if (d != null) {
                    state.flowDirections.add(d);
                }
            }
        }
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PipeFlowState that)) return false;
        return hasWater == that.hasWater &&
                isSource == that.isSource &&
                distance == that.distance &&
                maxDistance == that.maxDistance &&
                isOpenEndpoint == that.isOpenEndpoint &&
                bubbleColumn == that.bubbleColumn &&
                inflowDirection == that.inflowDirection &&
                Objects.equals(flowDirections, that.flowDirections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hasWater, isSource, flowDirections, inflowDirection, bubbleColumn, distance, maxDistance, isOpenEndpoint);
    }

    @Override
    public String toString() {
        return "PipeFlowState{" +
                "hasWater=" + hasWater +
                ", isSource=" + isSource +
                ", flowDirections=" + flowDirections +
                ", inflowDirection=" + inflowDirection +
                ", bubbleColumn=" + bubbleColumn +
                ", distance=" + distance +
                ", maxDistance=" + maxDistance +
                ", isOpenEndpoint=" + isOpenEndpoint +
                '}';
    }
}
