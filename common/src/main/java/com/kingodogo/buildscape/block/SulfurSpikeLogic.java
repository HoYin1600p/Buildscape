package com.kingodogo.buildscape.block;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
public final class SulfurSpikeLogic {
    public enum Thickness { TIP, TIP_MERGE, FRUSTUM, MIDDLE, BASE }
    public enum VerticalDirection { UP, DOWN }
    public enum ClickFace { UP, DOWN, SIDE }
    public enum FluidKind { EMPTY, WATER, LAVA }

    @FunctionalInterface
    public interface ConnectedAtOffset {
        boolean test(int verticalOffset);
    }

    public interface NativeStateAccess {
        BlockState state(BlockPos pos);
        boolean isSulfur(BlockState state);
        boolean isPointed(BlockState state);
        VerticalDirection direction(BlockState state);
        Thickness thickness(BlockState state);
        BlockState withThickness(BlockState state, Thickness thickness);
    }

    public interface NativeMutationAccess extends NativeStateAccess {
        void setState(BlockPos pos, BlockState state, int flags);
        void scheduleTick(BlockPos pos, int delay);
        void notifyVanillaPointedNeighbor(BlockPos target, BlockPos source);
        void destroy(BlockPos pos, boolean drop);
        boolean isAir(BlockState state);
        boolean isSulfurSource(BlockState state);
        FluidKind fluidAt(BlockPos pos);
        BlockState newState(VerticalDirection direction, Thickness thickness);
    }

    private SulfurSpikeLogic() {
    }

    public static Thickness thicknessForPosition(int stackHeight, int position) {
        if (stackHeight == 1) {
            return Thickness.TIP;
        }
        if (stackHeight == 2) {
            return position == 0 ? Thickness.FRUSTUM : Thickness.TIP;
        }
        if (stackHeight == 3) {
            if (position == 0) return Thickness.BASE;
            if (position == 1) return Thickness.FRUSTUM;
            return Thickness.TIP;
        }
        if (position == 0) return Thickness.BASE;
        if (position == stackHeight - 1) return Thickness.TIP;
        if (position == stackHeight - 2) return Thickness.FRUSTUM;
        return Thickness.MIDDLE;
    }

    public static Thickness mergedTip(Thickness current, boolean adjacentIsSulfurSpike,
                                      boolean adjacentHasSameDirection, Thickness adjacent) {
        if (current != Thickness.TIP && current != Thickness.TIP_MERGE) return current;
        if (!adjacentIsSulfurSpike || adjacentHasSameDirection) return Thickness.TIP;
        if (adjacent != Thickness.TIP && adjacent != Thickness.TIP_MERGE) return Thickness.TIP;
        return Thickness.TIP_MERGE;
    }

    public static int stackHeight(ConnectedAtOffset connected) {
        int height = 1;
        for (int offset = 1; connected.test(offset); offset++) height++;
        for (int offset = -1; connected.test(offset); offset--) height++;
        return height;
    }

    public static int positionInStack(boolean pointsDown, ConnectedAtOffset connected) {
        int position = 0;
        int step = pointsDown ? 1 : -1;
        for (int offset = step; connected.test(offset); offset += step) position++;
        return position;
    }

    public static VerticalDirection placementDirection(
            VerticalDirection clickedSulfurDirection,
            ClickFace clickedFace,
            VerticalDirection sulfurBelowDirection,
            VerticalDirection sulfurAboveDirection,
            boolean sturdyAbove,
            boolean sturdyBelow) {
        if (clickedSulfurDirection != null) return clickedSulfurDirection;
        if (clickedFace == ClickFace.UP) return VerticalDirection.UP;
        if (clickedFace == ClickFace.DOWN) return VerticalDirection.DOWN;
        if (sulfurBelowDirection == VerticalDirection.UP) return VerticalDirection.UP;
        if (sulfurAboveDirection == VerticalDirection.DOWN) return VerticalDirection.DOWN;
        if (sturdyAbove && !sturdyBelow) return VerticalDirection.DOWN;
        if (sturdyBelow && !sturdyAbove) return VerticalDirection.UP;
        return VerticalDirection.DOWN;
    }

    public static boolean canSurvive(boolean supportedBySlab, boolean sturdySupport,
                                     boolean pointedSupport, boolean supportHasSameDirection) {
        return supportedBySlab || sturdySupport || (pointedSupport && supportHasSameDirection);
    }

    public static boolean supportGone(boolean sturdySupport, boolean pointedSupport,
                                      boolean supportedBySlab) {
        return !sturdySupport && !pointedSupport && !supportedBySlab;
    }

    public static boolean shouldGrowStalactite(float randomValue) {
        return randomValue < 0.06F;
    }

    public static boolean shouldGrowStalagmite(float randomValue) {
        return randomValue < 0.12F;
    }

    public static int farthestConnectedOffset(int step, int maximumSteps, ConnectedAtOffset connected) {
        int offset = 0;
        for (int count = 0; count < maximumSteps; count++) {
            int next = offset + step;
            if (!connected.test(next)) break;
            offset = next;
        }
        return offset;
    }

    public static List<Integer> connectedOffsets(int step, ConnectedAtOffset connected) {
        List<Integer> offsets = new ArrayList<>();
        for (int offset = step; connected.test(offset); offset += step) offsets.add(offset);
        return offsets;
    }

    public static Integer firstBlockingOffsetAbove(int maximumDistance, ConnectedAtOffset isAir,
                                                   ConnectedAtOffset isDownwardSulfur) {
        for (int offset = 1; offset <= maximumDistance; offset++) {
            if (isAir.test(offset)) continue;
            return isDownwardSulfur.test(offset) ? offset : null;
        }
        return null;
    }

    public static BlockState applyThickness(NativeStateAccess access, BlockPos pos, BlockState state) {
        VerticalDirection direction = access.direction(state);
        ConnectedAtOffset connected = offset -> {
            BlockState candidate = access.state(pos.offset(0, offset, 0));
            return access.isPointed(candidate) && access.direction(candidate) == direction;
        };
        int height = stackHeight(connected);
        int position = positionInStack(direction == VerticalDirection.DOWN, connected);
        Thickness thickness = thicknessForPosition(height, position);
        BlockPos adjacentPos = direction == VerticalDirection.DOWN ? pos.below() : pos.above();
        BlockState adjacent = access.state(adjacentPos);
        boolean sameBlock = access.isSulfur(adjacent);
        thickness = mergedTip(thickness, sameBlock,
                sameBlock && access.direction(adjacent) == direction,
                sameBlock ? access.thickness(adjacent) : null);
        return access.withThickness(state, thickness);
    }

    public static void onPlaced(NativeMutationAccess access, BlockPos pos, BlockState state) {
        if (access.thickness(state) == Thickness.TIP_MERGE) {
            BlockState corrected = applyThickness(access, pos, state);
            if (access.thickness(corrected) != Thickness.TIP_MERGE) {
                access.setState(pos, corrected, 2);
            }
        }
        access.scheduleTick(pos, 2);
        notifyVanillaPointedNeighbors(access, pos);
    }

    public static BlockState updateVerticalShape(NativeMutationAccess access, BlockPos pos,
                                                 BlockState state) {
        BlockState updated = applyThickness(access, pos, state);
        updateSulfurNeighbor(access, pos.above());
        updateSulfurNeighbor(access, pos.below());
        notifyVanillaPointedNeighbors(access, pos);
        return updated;
    }

    private static void updateSulfurNeighbor(NativeMutationAccess access, BlockPos pos) {
        BlockState neighbor = access.state(pos);
        if (!access.isSulfur(neighbor)) return;
        BlockState updated = applyThickness(access, pos, neighbor);
        if (updated != neighbor) access.setState(pos, updated, 2);
    }

    private static void notifyVanillaPointedNeighbors(NativeMutationAccess access, BlockPos pos) {
        access.notifyVanillaPointedNeighbor(pos.above(), pos);
        access.notifyVanillaPointedNeighbor(pos.below(), pos);
    }

    public static void scheduledSurvivalTick(NativeMutationAccess access, BlockPos pos, BlockState state,
                                             boolean survives, boolean supportGone, boolean freeBelow) {
        if (survives) return;
        VerticalDirection direction = access.direction(state);
        if (direction == VerticalDirection.DOWN && (supportGone || freeBelow)) {
            access.destroy(pos, true);
            return;
        }
        breakConnectedTowardTip(access, pos, direction);
    }

    public static void removed(NativeMutationAccess access, BlockPos pos, BlockState state) {
        VerticalDirection direction = access.direction(state);
        int step = direction == VerticalDirection.DOWN ? -1 : 1;
        for (int offset : connectedOffsets(step, value -> {
            BlockState candidate = access.state(pos.offset(0, value, 0));
            return access.isSulfur(candidate) && access.direction(candidate) == direction;
        })) {
            access.destroy(pos.offset(0, offset, 0), true);
        }
        updateRemainingStack(access,
                direction == VerticalDirection.DOWN ? pos.above() : pos.below(), direction);
    }

    private static void breakConnectedTowardTip(NativeMutationAccess access, BlockPos pos,
                                                VerticalDirection direction) {
        int step = direction == VerticalDirection.DOWN ? -1 : 1;
        for (int offset : connectedOffsets(step, value -> {
            BlockState candidate = access.state(pos.offset(0, value, 0));
            return access.isSulfur(candidate) && access.direction(candidate) == direction;
        })) {
            access.destroy(pos.offset(0, offset, 0), true);
        }
        updateRemainingStack(access,
                direction == VerticalDirection.DOWN ? pos.above() : pos.below(), direction);
    }

    private static void updateRemainingStack(NativeMutationAccess access, BlockPos start,
                                             VerticalDirection direction) {
        BlockState startState = access.state(start);
        if (!access.isSulfur(startState)) return;
        int towardBase = direction == VerticalDirection.DOWN ? 1 : -1;
        int baseOffset = farthestConnectedOffset(towardBase, Integer.MAX_VALUE, offset -> {
            BlockState candidate = access.state(start.offset(0, offset, 0));
            return access.isSulfur(candidate) && access.direction(candidate) == direction;
        });
        BlockPos current = start.offset(0, baseOffset, 0);
        int towardTip = -towardBase;
        while (true) {
            BlockState currentState = access.state(current);
            if (!access.isSulfur(currentState) || access.direction(currentState) != direction) return;
            BlockState updated = applyThickness(access, current, currentState);
            if (updated != currentState) access.setState(current, updated, 2);
            current = current.offset(0, towardTip, 0);
        }
    }

    public static void randomGrowthTick(NativeMutationAccess access, BlockPos pos, BlockState state,
                                        float randomValue) {
        if (access.direction(state) == VerticalDirection.DOWN) {
            growStalactite(access, pos, randomValue);
        } else {
            growStalagmite(access, pos, randomValue);
        }
    }

    private static void growStalactite(NativeMutationAccess access, BlockPos pos, float randomValue) {
        int rootOffset = farthestSulfur(access, pos, 1, 11, VerticalDirection.DOWN);
        BlockPos root = pos.offset(0, rootOffset, 0);
        if (!access.isSulfurSource(access.state(root.above()))) return;
        if (fluidAboveChain(access, pos) == FluidKind.EMPTY || !shouldGrowStalactite(randomValue)) return;
        int tipOffset = farthestSulfur(access, root, -1, 11, VerticalDirection.DOWN);
        BlockPos tip = root.offset(0, tipOffset, 0);
        BlockState tipState = access.state(tip);
        if (access.thickness(tipState) == Thickness.TIP_MERGE) return;
        BlockPos grow = tip.below();
        if (!access.isAir(access.state(grow))) return;
        access.setState(grow, access.newState(VerticalDirection.DOWN, Thickness.TIP), 3);
        access.setState(tip, access.withThickness(tipState, Thickness.FRUSTUM), 2);
    }

    private static void growStalagmite(NativeMutationAccess access, BlockPos pos, float randomValue) {
        int tipOffset = farthestSulfur(access, pos, 1, 11, VerticalDirection.UP);
        BlockPos tip = pos.offset(0, tipOffset, 0);
        BlockState tipState = access.state(tip);
        if (access.thickness(tipState) == Thickness.TIP_MERGE) return;
        Integer stalactiteOffset = firstBlockingOffsetAbove(10,
                offset -> access.isAir(access.state(tip.offset(0, offset, 0))),
                offset -> {
                    BlockState candidate = access.state(tip.offset(0, offset, 0));
                    return access.isSulfur(candidate)
                            && access.direction(candidate) == VerticalDirection.DOWN;
                });
        if (stalactiteOffset == null) return;
        BlockPos stalactiteTip = tip.offset(0, stalactiteOffset, 0);
        int rootOffset = farthestSulfur(access, stalactiteTip, 1, 11, VerticalDirection.DOWN);
        BlockPos root = stalactiteTip.offset(0, rootOffset, 0);
        if (!access.isSulfurSource(access.state(root.above()))) return;
        if (fluidAboveChain(access, stalactiteTip) == FluidKind.EMPTY
                || !shouldGrowStalagmite(randomValue)) return;
        BlockPos grow = tip.above();
        if (!access.isAir(access.state(grow))) return;
        access.setState(grow, access.newState(VerticalDirection.UP, Thickness.TIP), 3);
        access.setState(tip, access.withThickness(tipState, Thickness.FRUSTUM), 2);
    }

    private static int farthestSulfur(NativeStateAccess access, BlockPos origin, int step, int maximum,
                                      VerticalDirection direction) {
        return farthestConnectedOffset(step, maximum, offset -> {
            BlockState candidate = access.state(origin.offset(0, offset, 0));
            return access.isSulfur(candidate) && access.direction(candidate) == direction;
        });
    }

    public static FluidKind fluidAboveChain(NativeMutationAccess access, BlockPos tip) {
        BlockPos check = tip.above();
        int remaining = 20;
        while (remaining > 0 && access.isSulfur(access.state(check))) {
            check = check.above();
            remaining--;
        }
        if (!access.isSulfurSource(access.state(check))) return FluidKind.EMPTY;
        return access.fluidAt(check.above());
    }

    public static FluidKind dripParticleFluid(NativeMutationAccess access, BlockPos pos,
                                              BlockState state, int oneInThreeRoll) {
        if (access.direction(state) != VerticalDirection.DOWN) return FluidKind.EMPTY;
        Thickness thickness = access.thickness(state);
        if (thickness != Thickness.TIP && thickness != Thickness.TIP_MERGE) return FluidKind.EMPTY;
        FluidKind fluid = fluidAboveChain(access, pos);
        return fluid != FluidKind.EMPTY && oneInThreeRoll == 0 ? fluid : FluidKind.EMPTY;
    }

    public static void neighborChanged(NativeMutationAccess access, BlockPos pos, BlockState state,
                                       boolean verticalNeighborChanged) {
        access.scheduleTick(pos, 2);
        if (!verticalNeighborChanged) return;
        BlockState current = access.state(pos);
        if (!access.isSulfur(current)) return;
        BlockState updated = updateVerticalShape(access, pos, current);
        if (updated != current) access.setState(pos, updated, 2);
    }
}
