package com.kingodogo.buildscape.pipe.transport;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.List;

public class PipeItemTransit {

    private final ItemStack stack;
    private final List<BlockPos> path;
    private final long startTick;
    private final long totalDurationTicks;
    private final Direction exitDirection;

    public PipeItemTransit(ItemStack stack, List<BlockPos> path, long startTick, long totalDurationTicks, Direction exitDirection) {
        this.stack = stack.copy();
        this.path = path != null ? Collections.unmodifiableList(path) : Collections.emptyList();
        this.startTick = startTick;
        this.totalDurationTicks = Math.max(1, totalDurationTicks);
        this.exitDirection = exitDirection != null ? exitDirection : Direction.DOWN;
    }

    public ItemStack getStack() {
        return stack;
    }

    public List<BlockPos> getPath() {
        return path;
    }

    public long getStartTick() {
        return startTick;
    }

    public long getTotalDurationTicks() {
        return totalDurationTicks;
    }

    public Direction getExitDirection() {
        return exitDirection;
    }

    public boolean isComplete(long currentTick) {
        return currentTick >= startTick + totalDurationTicks;
    }

    public BlockPos getDestination() {
        if (path.isEmpty()) return null;
        return path.get(path.size() - 1);
    }

    public BlockPos getCurrentBlockPos(long currentTick) {
        if (path.isEmpty()) return null;
        if (path.size() == 1) return path.get(0);

        float progress = Math.min(1.0F, Math.max(0.0F, (float) (currentTick - startTick) / totalDurationTicks));
        int index = Math.min(path.size() - 1, (int) (progress * path.size()));
        return path.get(index);
    }

    public Vec3 getInterpolatedPosition(long currentTick, float partialTicks) {
        if (path.isEmpty()) return Vec3.ZERO;
        if (path.size() == 1) {
            BlockPos p = path.get(0);
            return new Vec3(p.getX() + 0.5, p.getY() + 0.35, p.getZ() + 0.5);
        }

        float exactProgress = (float) (currentTick - startTick + partialTicks) / totalDurationTicks;
        exactProgress = Math.min(1.0F, Math.max(0.0F, exactProgress));

        float pathProgress = exactProgress * (path.size() - 1);
        int segmentIndex = (int) pathProgress;
        float segmentFraction = pathProgress - segmentIndex;

        if (segmentIndex >= path.size() - 1) {
            BlockPos last = path.get(path.size() - 1);
            return new Vec3(last.getX() + 0.5, last.getY() + 0.35, last.getZ() + 0.5);
        }

        BlockPos from = path.get(segmentIndex);
        BlockPos to = path.get(segmentIndex + 1);

        double x = from.getX() + 0.5 + (to.getX() - from.getX()) * segmentFraction;
        double y = from.getY() + 0.35 + (to.getY() - from.getY()) * segmentFraction;
        double z = from.getZ() + 0.5 + (to.getZ() - from.getZ()) * segmentFraction;

        return new Vec3(x, y, z);
    }
}
