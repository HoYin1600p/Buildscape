package com.kingodogo.buildscape.pipe.transport;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
public class PipeFlowState {
    public static final String WATER_FLUID_ID = "minecraft:water";

    private String fluidId = "";
    private boolean isSource;
    private final Set<Direction> flowDirections = EnumSet.noneOf(Direction.class);
    private Direction inflowDirection = null;
    private BubbleColumnState bubbleColumn = BubbleColumnState.NONE;
    private int distance = 0;
    private int maxDistance = 0;
    private boolean isOpenEndpoint = false;

    public PipeFlowState() {
    }

    public PipeFlowState(boolean hasWater, boolean isSource, Collection<Direction> flowDirections, Direction inflowDirection, BubbleColumnState bubbleColumn, int distance, int maxDistance, boolean isOpenEndpoint) {
        this.fluidId = hasWater ? WATER_FLUID_ID : "";
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

    public PipeFlowState(boolean hasWater, boolean isSource, Collection<Direction> flowDirections, BubbleColumnState bubbleColumn, int distance) {
        this(hasWater, isSource, flowDirections, null, bubbleColumn, distance, 0, false);
    }

    public boolean hasWater() {
        return WATER_FLUID_ID.equals(fluidId);
    }

    public void setHasWater(boolean hasWater) {
        this.fluidId = hasWater ? WATER_FLUID_ID : "";
    }

    public boolean hasFluid() {
        return !fluidId.isEmpty();
    }

    public String getFluidId() {
        return fluidId;
    }

    public void setFluidId(String fluidId) {
        this.fluidId = fluidId == null ? "" : fluidId;
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

    public Direction getInflowDirection() {
        return inflowDirection;
    }

    public void setInflowDirection(Direction inflowDirection) {
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

    public int getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = Math.max(0, maxDistance);
    }

    public boolean isOpenEndpoint() {
        return isOpenEndpoint;
    }

    public void setOpenEndpoint(boolean openEndpoint) {
        isOpenEndpoint = openEndpoint;
    }

    public boolean isEmpty() {
        return !hasFluid() && !isSource && flowDirections.isEmpty() && inflowDirection == null
                && bubbleColumn == BubbleColumnState.NONE && distance == 0 && maxDistance == 0 && !isOpenEndpoint;
    }

    public void clear() {
        this.fluidId = "";
        this.isSource = false;
        this.flowDirections.clear();
        this.inflowDirection = null;
        this.bubbleColumn = BubbleColumnState.NONE;
        this.distance = 0;
        this.maxDistance = 0;
        this.isOpenEndpoint = false;
    }

    public PipeFlowState copy() {
        PipeFlowState copy = new PipeFlowState(false, this.isSource, this.flowDirections, this.inflowDirection,
                this.bubbleColumn, this.distance, this.maxDistance, this.isOpenEndpoint);
        copy.fluidId = this.fluidId;
        return copy;
    }

    public static PipeFlowState readData(com.kingodogo.buildscape.block.entity.IBlockEntityReadData data) {
        PipeFlowState state = new PipeFlowState();
        if (data == null) return state;
        state.fluidId = data.contains("FluidId")
                ? data.getStringOr("FluidId", "")
                : (data.getBooleanOr("HasWater", false) ? WATER_FLUID_ID : "");
        state.isSource = data.getBooleanOr("IsSource", false);
        state.bubbleColumn = BubbleColumnState.byName(data.getStringOr("BubbleColumn", "none"));
        state.distance = data.getIntOr("Distance", 0);
        state.maxDistance = data.getIntOr("MaxDistance", 0);
        state.isOpenEndpoint = data.getBooleanOr("IsOpenEndpoint", false);
        if (data.contains("InflowDir")) {
            state.inflowDirection = Direction.byName(data.getStringOr("InflowDir", ""));
        }
        java.util.List<String> dirs = data.getStringListOrEmpty("FlowDirs");
        for (String s : dirs) {
            Direction d = Direction.byName(s);
            if (d != null) {
                state.flowDirections.add(d);
            }
        }
        return state;
    }

    public void writeData(com.kingodogo.buildscape.block.entity.IBlockEntityWriteData data) {
        data.putBoolean("HasWater", hasWater());
        if (hasFluid()) {
            data.putString("FluidId", fluidId);
        }
        data.putBoolean("IsSource", isSource);
        data.putString("BubbleColumn", bubbleColumn.getSerializedName());
        data.putInt("Distance", distance);
        data.putInt("MaxDistance", maxDistance);
        data.putBoolean("IsOpenEndpoint", isOpenEndpoint);
        if (inflowDirection != null) {
            data.putString("InflowDir", inflowDirection.getName());
        }
        java.util.List<String> list = new java.util.ArrayList<>();
        for (Direction dir : flowDirections) {
            list.add(dir.getName());
        }
        data.putStringList("FlowDirs", list);
    }

    public CompoundTag writeToNbt(CompoundTag tag) {
        tag.putBoolean("HasWater", hasWater());
        if (hasFluid()) {
            tag.putString("FluidId", fluidId);
        }
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

        state.fluidId = tag.contains("FluidId")
                ? Services.PLATFORM.getTagString(tag, "FluidId", "")
                : (Services.PLATFORM.getTagBoolean(tag, "HasWater", false) ? WATER_FLUID_ID : "");
        state.isSource = Services.PLATFORM.getTagBoolean(tag, "IsSource", false);
        state.bubbleColumn = BubbleColumnState.byName(Services.PLATFORM.getTagString(tag, "BubbleColumn", "none"));
        state.distance = Services.PLATFORM.getTagInt(tag, "Distance", 0);
        state.maxDistance = Services.PLATFORM.getTagInt(tag, "MaxDistance", 0);
        state.isOpenEndpoint = Services.PLATFORM.getTagBoolean(tag, "IsOpenEndpoint", false);

        if (tag.contains("InflowDir")) {
            state.inflowDirection = Direction.byName(Services.PLATFORM.getTagString(tag, "InflowDir", ""));
        }

        java.util.List<String> dirs = Services.PLATFORM.getTagStringList(tag, "FlowDirs");
        for (String dirName : dirs) {
            Direction d = Direction.byName(dirName);
            if (d != null) {
                state.flowDirections.add(d);
            }
        }
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PipeFlowState that)) return false;
        return Objects.equals(fluidId, that.fluidId) &&
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
        return Objects.hash(fluidId, isSource, flowDirections, inflowDirection, bubbleColumn, distance, maxDistance, isOpenEndpoint);
    }

    @Override
    public String toString() {
        return "PipeFlowState{" +
                "fluidId='" + fluidId + '\'' +
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
