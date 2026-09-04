package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.block.HollowLogBlockEntity;
import com.kingodogo.buildscape.block.HollowPipeBlock;
import com.kingodogo.buildscape.pipe.transport.BubbleColumnState;
import com.kingodogo.buildscape.pipe.transport.PipeFlowState;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/** Replaces, rather than overlays, the world-water surface beside an outlet. Author: HoYin1600p. */
public final class PipeSpillVertexConsumer implements VertexConsumer {
    private static final double OPEN_MIN = 0.127;
    private static final double OPEN_MAX = 1.0 - OPEN_MIN;

    /** Direction points from the world-water block toward the pipe. */
    public record Outlet(Direction direction, double height) {}

    private final VertexConsumer delegate;
    private final List<Outlet> outlets;
    private final int baseX, baseY, baseZ;
    private final Vertex[] quad = new Vertex[4];
    private int count;
    private double x, y, z;
    private int red = 255, green = 255, blue = 255, alpha = 255, overlayU, overlayV, lightU, lightV;
    private float u, v, nx, ny, nz;

    public PipeSpillVertexConsumer(VertexConsumer delegate, BlockPos pos, List<Outlet> outlets) {
        this.delegate = delegate;
        this.outlets = List.copyOf(outlets);
        baseX = pos.getX() & 15;
        baseY = pos.getY() & 15;
        baseZ = pos.getZ() & 15;
    }

    public static VertexConsumer wrap(VertexConsumer delegate, BlockAndTintGetter level, BlockPos pos,
                                      BlockState state, FluidState fluid) {
        List<Outlet> outlets = findOutlets(level, pos, state, fluid);
        return outlets.isEmpty() ? delegate : new PipeSpillVertexConsumer(delegate, pos, outlets);
    }

    public static List<Outlet> findOutlets(BlockAndTintGetter level, BlockPos pos, BlockState state, FluidState fluid) {
        // Leave sources, waterfalls, submerged water, logs and non-water fluids unchanged.
        if (!(state.getBlock() instanceof LiquidBlock) || !fluid.getType().isSame(Fluids.WATER)
                || fluid.isSource() || fluid.getValue(net.minecraft.world.level.material.FlowingFluid.FALLING)
                || level.getFluidState(pos.above()).getType().isSame(Fluids.WATER)) {
            return List.of();
        }
        List<Outlet> outlets = null;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos pipePos = pos.relative(direction);
            BlockState pipe = level.getBlockState(pipePos);
            Direction exit = direction.getOpposite();
            if (!(pipe.getBlock() instanceof HollowPipeBlock) || !HollowPipeBlock.isOpenEndpoint(pipe, exit)
                    || !(level.getBlockEntity(pipePos) instanceof HollowLogBlockEntity entity)) {
                continue;
            }
            PipeFlowState flow = entity.getPipeFlowState();
            if (flow == null || !flow.hasWater() || !flow.hasFlowDirection(exit) || flow.getDistance() >= 7
                    || flow.getInflowDirection() == Direction.UP
                    || (flow.hasFlowDirection(Direction.UP) && flow.getBubbleColumn() == BubbleColumnState.UP)) {
                continue;
            }
            PipeWaterSurface.Heights heights = PipeWaterSurface.flowing(pipe, flow);
            Direction.Axis axis = pipe.getValue(HollowPipeBlock.AXIS);
            boolean straightX = (axis == Direction.Axis.X || pipe.getValue(HollowPipeBlock.WEST)
                    || pipe.getValue(HollowPipeBlock.EAST))
                    && !pipe.getValue(HollowPipeBlock.NORTH) && !pipe.getValue(HollowPipeBlock.SOUTH);
            boolean straightZ = (axis == Direction.Axis.Z || pipe.getValue(HollowPipeBlock.NORTH)
                    || pipe.getValue(HollowPipeBlock.SOUTH))
                    && !pipe.getValue(HollowPipeBlock.WEST) && !pipe.getValue(HollowPipeBlock.EAST);
            double height = straightX || straightZ ? heights.outlet() : heights.center();
            // A source can flow in both directions. Match the existing renderer's chosen inlet edge too.
            if (straightX && exit == Direction.WEST && (flow.getInflowDirection() == Direction.WEST
                    || flow.hasFlowDirection(Direction.EAST))) height = heights.inlet();
            if (straightZ && exit == Direction.NORTH && (flow.getInflowDirection() == Direction.NORTH
                    || flow.hasFlowDirection(Direction.SOUTH))) height = heights.inlet();
            if (outlets == null) outlets = new ArrayList<>(2);
            outlets.add(new Outlet(direction, height));
        }
        return outlets == null ? List.of() : outlets;
    }

    @Override public VertexConsumer vertex(double x, double y, double z) {
        this.x = x; this.y = y; this.z = z; return this;
    }
    @Override public VertexConsumer color(int r, int g, int b, int a) {
        red = r; green = g; blue = b; alpha = a; return this;
    }
    @Override public VertexConsumer uv(float u, float v) { this.u = u; this.v = v; return this; }
    @Override public VertexConsumer overlayCoords(int u, int v) { overlayU = u; overlayV = v; return this; }
    @Override public VertexConsumer uv2(int u, int v) { lightU = u; lightV = v; return this; }
    @Override public VertexConsumer normal(float x, float y, float z) { nx = x; ny = y; nz = z; return this; }
    @Override public void defaultColor(int r, int g, int b, int a) { color(r, g, b, a); }
    @Override public void unsetDefaultColor() {}

    @Override public void endVertex() {
        quad[count++] = new Vertex(x, y, z, u, v, red, green, blue, alpha,
                overlayU, overlayV, lightU, lightV, nx, ny, nz);
        if (count == 4) {
            renderQuad();
            count = 0;
        }
    }

    private void renderQuad() {
        Vertex[] corners = new Vertex[4]; // NW, SW, SE, NE, independent of winding.
        for (Vertex vertex : quad) {
            double px = vertex.x - baseX, pz = vertex.z - baseZ;
            if (vertex.y <= baseY + 0.01 || vertex.y > baseY + 1.0
                    || (px != 0 && px != 1) || (pz != 0 && pz != 1)) {
                emitOriginal();
                return;
            }
            int index = px == 0 ? (pz == 0 ? 0 : 1) : (pz == 0 ? 3 : 2);
            if (corners[index] != null) { emitOriginal(); return; }
            corners[index] = vertex;
        }
        for (Vertex corner : corners) {
            if (corner == null) { emitOriginal(); return; }
        }
        List<Spill> spills = new ArrayList<>(outlets.size());
        TreeSet<Double> cuts = new TreeSet<>(List.of(0.0, OPEN_MIN, 0.25, 0.5, 0.75, OPEN_MAX, 1.0));
        for (Outlet outlet : outlets) {
            double edgeX = outlet.direction == Direction.WEST ? 0 : outlet.direction == Direction.EAST ? 1 : 0.5;
            double edgeZ = outlet.direction == Direction.NORTH ? 0 : outlet.direction == Direction.SOUTH ? 1 : 0.5;
            double gap = outlet.height - (sample(corners, edgeX, edgeZ).y - baseY);
            for (double across : new double[]{OPEN_MIN, OPEN_MAX}) {
                double sideX = outlet.direction.getAxis() == Direction.Axis.X ? edgeX : across;
                double sideZ = outlet.direction.getAxis() == Direction.Axis.Z ? edgeZ : across;
                gap = Math.max(gap, outlet.height - (sample(corners, sideX, sideZ).y - baseY));
            }
            if (gap <= 0.001) continue;
            double length = Math.min(0.875, Math.max(0.25, gap * 2.0));
            spills.add(new Spill(outlet, length));
            cuts.add(length);
            cuts.add(1.0 - length);
        }
        if (spills.isEmpty()) { emitOriginal(); return; }
        Double[] grid = cuts.toArray(Double[]::new);
        boolean forward = quad[1].z > quad[0].z;
        for (int ix = 0; ix < grid.length - 1; ix++) {
            for (int iz = 0; iz < grid.length - 1; iz++) {
                Vertex nw = raised(corners, spills, grid[ix], grid[iz]);
                Vertex sw = raised(corners, spills, grid[ix], grid[iz + 1]);
                Vertex se = raised(corners, spills, grid[ix + 1], grid[iz + 1]);
                Vertex ne = raised(corners, spills, grid[ix + 1], grid[iz]);
                emit(nw); emit(forward ? sw : ne); emit(se); emit(forward ? ne : sw);
            }
        }
    }

    private Vertex raised(Vertex[] corners, List<Spill> spills, double x, double z) {
        Vertex original = sample(corners, x, z);
        double height = original.y;
        for (Spill spill : spills) {
            Direction direction = spill.outlet.direction;
            double distance = switch (direction) {
                case WEST -> x;
                case EAST -> 1 - x;
                case NORTH -> z;
                case SOUTH -> 1 - z;
                default -> 1;
            };
            double across = direction.getAxis() == Direction.Axis.X ? z : x;
            double widthWeight = Math.min(1, Math.min(across / OPEN_MIN, (1 - across) / OPEN_MIN));
            double alongWeight = Math.max(0, 1 - distance / spill.length);
            double edgeX = direction == Direction.WEST ? 0 : direction == Direction.EAST ? 1 : x;
            double edgeZ = direction == Direction.NORTH ? 0 : direction == Direction.SOUTH ? 1 : z;
            double edgeHeight = sample(corners, edgeX, edgeZ).y;
            double lift = Math.max(0, baseY + spill.outlet.height - edgeHeight);
            height = Math.max(height, original.y + lift * widthWeight * alongWeight);
        }
        return original.at(original.x, height, original.z, original.u, original.v);
    }

    private Vertex sample(Vertex[] c, double x, double z) {
        // Preserve vanilla's two triangles, including UVs; bilinear interpolation would reshape the water.
        Vertex a = c[0], b = x <= z ? c[1] : c[3], d = c[2];
        double wb = Math.abs(z - x), wd = Math.min(x, z), wa = 1 - wb - wd;
        return a.at(baseX + x, a.y * wa + b.y * wb + d.y * wd, baseZ + z,
                (float)(a.u * wa + b.u * wb + d.u * wd), (float)(a.v * wa + b.v * wb + d.v * wd));
    }

    private void emitOriginal() { for (Vertex vertex : quad) emit(vertex); }

    private void emit(Vertex v) {
        delegate.vertex(v.x, v.y, v.z).color(v.red, v.green, v.blue, v.alpha).uv(v.u, v.v)
                .overlayCoords(v.overlayU, v.overlayV).uv2(v.lightU, v.lightV).normal(v.nx, v.ny, v.nz).endVertex();
    }

    private record Spill(Outlet outlet, double length) {}

    private record Vertex(double x, double y, double z, float u, float v,
                          int red, int green, int blue, int alpha, int overlayU, int overlayV,
                          int lightU, int lightV, float nx, float ny, float nz) {
        Vertex at(double x, double y, double z, float u, float v) {
            return new Vertex(x, y, z, u, v, red, green, blue, alpha,
                    overlayU, overlayV, lightU, lightV, nx, ny, nz);
        }
    }
}
