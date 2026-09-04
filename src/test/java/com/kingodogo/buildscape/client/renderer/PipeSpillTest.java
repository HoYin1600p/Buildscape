package com.kingodogo.buildscape.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.List;

public final class PipeSpillTest {
    private static int checks;

    public static void main(String[] args) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            for (BlockPos pos : List.of(BlockPos.ZERO, new BlockPos(15, -60, 15), new BlockPos(-1, 70, -17))) {
                for (boolean reverse : new boolean[]{false, true}) {
                    verifySpill(pos, direction, reverse, 0.4, 0.7);
                    verifySpill(pos, direction, reverse, 0.1, 0.13);
                    verifySpill(pos, direction, reverse, 0.1, 0.82);
                }
            }
        }
        verifyPassThrough(0.001, 0.7);
        verifyPassThrough(0.8, 0.7);
        verifySide();
        verifySlopedSurface();
        verifyMultipleOutlets();
        for (BlockPos pos : List.of(BlockPos.ZERO, new BlockPos(15, 15, 15), new BlockPos(-1, -17, -17))) {
            for (boolean reverse : new boolean[]{false, true}) {
                verifyDownwardOutlet(pos, reverse);
            }
        }
        System.out.println("Pipe spill geometry: " + checks + " checks passed.");
    }

    private static void verifySpill(BlockPos pos, Direction direction, boolean reverse, double base, double lip) {
        Capture output = new Capture();
        VertexConsumer consumer = new PipeSpillVertexConsumer(output, pos,
                List.of(new PipeSpillVertexConsumer.Outlet(direction, lip)));
        top(consumer, pos, reverse, base, base, base, base);
        require(output.vertices.size() > 4, "surface subdivided");
        double area = 0;
        boolean lipSeen = false, taperSeen = false;
        double length = Math.min(0.875, Math.max(0.25, (lip - base) * 2));
        for (int i = 0; i < output.vertices.size(); i += 4) {
            double signedArea = 0;
            for (int j = 0; j < 4; j++) {
                V a = output.vertices.get(i + j), b = output.vertices.get(i + (j + 1) % 4);
                signedArea += a.x * b.z - b.x * a.z;
            }
            require(reverse ? signedArea > 0 : signedArea < 0, "winding preserved");
            area += Math.abs(signedArea) * 0.5;
        }
        near(area, 1, "exactly one top surface, without an underlying duplicate");
        for (V v : output.vertices) {
            double x = v.x - (pos.getX() & 15), z = v.z - (pos.getZ() & 15), y = v.y - (pos.getY() & 15);
            double distance = switch (direction) {
                case WEST -> x;
                case EAST -> 1 - x;
                case NORTH -> z;
                case SOUTH -> 1 - z;
                default -> throw new AssertionError();
            };
            double across = direction.getAxis() == Direction.Axis.X ? z : x;
            require(y >= base - 1e-6 && y <= lip + 1e-6, "bounded wedge height");
            if (distance == 0 && Math.abs(across - 0.5) < 1e-6) {
                near(y, lip, "pipe lip matches"); lipSeen = true;
            }
            if (distance >= length - 1e-6) {
                near(y, base, "taper joins original surface"); taperSeen = true;
            }
            if (across == 0 || across == 1) near(y, base, "no side-edge cracks");
            near(v.u, x, "vanilla U retained"); near(v.v, z, "vanilla V retained");
            require(v.red == 47 && v.green == 105 && v.blue == 191 && v.alpha == 213,
                    "biome tint and alpha retained");
            require(v.lightU == 160 && v.lightV == 240, "lighting retained");
        }
        require(lipSeen && taperSeen, "lip and taper were both tested");
    }

    private static void verifyPassThrough(double height, double lip) {
        Capture output = new Capture();
        top(new PipeSpillVertexConsumer(output, BlockPos.ZERO,
                List.of(new PipeSpillVertexConsumer.Outlet(Direction.WEST, lip))),
                BlockPos.ZERO, false, height, height, height, height);
        require(output.vertices.size() == 4, "unaffected surface passed through");
        for (V v : output.vertices) near(v.y, height, "unaffected height preserved");
    }

    private static void verifySide() {
        Capture output = new Capture();
        VertexConsumer c = new PipeSpillVertexConsumer(output, BlockPos.ZERO,
                List.of(new PipeSpillVertexConsumer.Outlet(Direction.WEST, 0.7)));
        point(c, 0, 0.4, 0, 0, 0); point(c, 0, 0.001, 0, 0, 1);
        point(c, 1, 0.001, 0, 1, 1); point(c, 1, 0.4, 0, 1, 0);
        require(output.vertices.size() == 4, "side face preserved");
        near(output.vertices.get(1).y, 0.001, "side bottom unchanged");
    }

    private static void verifySlopedSurface() {
        Capture output = new Capture();
        top(new PipeSpillVertexConsumer(output, BlockPos.ZERO,
                List.of(new PipeSpillVertexConsumer.Outlet(Direction.WEST, 0.7))),
                BlockPos.ZERO, false, 0.3, 0.4, 0.2, 0.1);
        for (V v : output.vertices) {
            if (v.x == 1) near(v.y, 0.1 + v.z * 0.1, "sloping outside boundary unchanged");
            if (v.x == 0 && v.z >= 0.127 && v.z <= 0.873) near(v.y, 0.7, "sloping receiver meets level lip");
        }
    }

    private static void verifyMultipleOutlets() {
        Capture output = new Capture();
        top(new PipeSpillVertexConsumer(output, BlockPos.ZERO, List.of(
                new PipeSpillVertexConsumer.Outlet(Direction.WEST, 0.7),
                new PipeSpillVertexConsumer.Outlet(Direction.EAST, 0.6))),
                BlockPos.ZERO, false, 0.4, 0.4, 0.4, 0.4);
        for (V v : output.vertices) {
            require(v.y <= 0.7 + 1e-6, "intersecting spills do not stack heights");
            if (v.z == 0.5 && v.x == 0) near(v.y, 0.7, "west outlet retained");
            if (v.z == 0.5 && v.x == 1) near(v.y, 0.6, "east outlet retained");
        }
    }

    private static void verifyDownwardOutlet(BlockPos pos, boolean reverse) {
        Capture output = new Capture();
        VertexConsumer c = new PipeSpillVertexConsumer(output, pos,
                List.of(new PipeSpillVertexConsumer.Outlet(Direction.UP, 1)));
        double x = pos.getX() & 15, y = pos.getY() & 15, z = pos.getZ() & 15;
        top(c, pos, reverse, 0.8, 0.85, 0.8, 0.75);
        require(output.vertices.size() == 16, "four skirts replace the water top without a cap");
        double area = 0;
        int neckVertices = 0, perimeterVertices = 0;
        for (int start = 0; start < 16; start += 4) {
            double signedArea = 0;
            for (int i = 0; i < 4; i++) {
                V a = output.vertices.get(start + i), b = output.vertices.get(start + (i + 1) % 4);
                signedArea += (a.x - x) * (b.z - z) - (b.x - x) * (a.z - z);
            }
            require(reverse ? signedArea > 0 : signedArea < 0, "downward skirt winding preserved");
            area += Math.abs(signedArea) / 2;
        }
        near(area, 1 - 0.746 * 0.746, "skirts cover exactly the ring around the open neck");
        for (V vertex : output.vertices) {
            double px = vertex.x - x, pz = vertex.z - z;
            if (px > 0 && px < 1) {
                require(Math.abs(px - 0.127) < 1e-6 || Math.abs(px - 0.873) < 1e-6, "neck width matches bore");
                require(Math.abs(pz - 0.127) < 1e-6 || Math.abs(pz - 0.873) < 1e-6, "neck depth matches bore");
                near(vertex.y, y + 1, "neck reaches the bottom of the pipe");
                neckVertices++;
            } else {
                near(vertex.y, y + 0.8 - px * 0.05 + pz * 0.05, "perimeter joins surrounding water unchanged");
                perimeterVertices++;
            }
            near(vertex.u, px, "falling water U preserved");
            near(vertex.v, pz, "falling water V preserved");
            require(vertex.red == 47 && vertex.green == 105 && vertex.blue == 191 && vertex.alpha == 213,
                    "falling water biome tint and alpha preserved");
            require(vertex.lightU == 160 && vertex.lightV == 240, "falling water light preserved");
        }
        require(neckVertices == 8 && perimeterVertices == 8, "each skirt connects both boundaries");
        int beforeSide = output.vertices.size();
        point(c, x, y + 0.8, z + 0.001, 0, 0); point(c, x, y, z + 0.001, 0, 1);
        point(c, x + 1, y, z + 0.001, 1, 1); point(c, x + 1, y + 0.75, z + 0.001, 1, 0);
        require(output.vertices.size() == beforeSide + 4, "vanilla side retained without duplicate geometry");
        near(output.vertices.get(beforeSide).y, y + 0.8, "vanilla side height preserved");
        near(output.vertices.get(beforeSide).z, z + 0.001, "vanilla side inset preserved");
        int beforeBottom = output.vertices.size();
        top(c, pos, reverse, 0.001, 0.001, 0.001, 0.001);
        require(output.vertices.size() == beforeBottom + 4, "bottom face retained");
        for (V vertex : output.vertices.subList(beforeBottom, output.vertices.size())) {
            near(vertex.y, y + 0.001, "bottom face unchanged");
        }
    }

    private static void top(VertexConsumer c, BlockPos pos, boolean reverse, double nw, double sw, double se, double ne) {
        double x = pos.getX() & 15, y = pos.getY() & 15, z = pos.getZ() & 15;
        point(c, x, y + nw, z, 0, 0);
        if (reverse) point(c, x + 1, y + ne, z, 1, 0); else point(c, x, y + sw, z + 1, 0, 1);
        point(c, x + 1, y + se, z + 1, 1, 1);
        if (reverse) point(c, x, y + sw, z + 1, 0, 1); else point(c, x + 1, y + ne, z, 1, 0);
    }

    private static void point(VertexConsumer c, double x, double y, double z, float u, float v) {
        c.vertex(x, y, z).color(47, 105, 191, 213).uv(u, v).uv2(160, 240).normal(0, 1, 0).endVertex();
    }

    private static void near(double actual, double expected, String message) {
        require(Math.abs(actual - expected) < 1e-6, message + ": " + actual + " != " + expected);
    }

    private static void require(boolean value, String message) {
        checks++;
        if (!value) throw new AssertionError(message);
    }

    private record V(double x, double y, double z, float u, float v, int red, int green, int blue,
                     int alpha, int lightU, int lightV) {}

    private static final class Capture implements VertexConsumer {
        final List<V> vertices = new ArrayList<>();
        double x, y, z;
        float u, v;
        int r, g, b, a, lu, lv;
        public VertexConsumer vertex(double x, double y, double z) { this.x=x; this.y=y; this.z=z; return this; }
        public VertexConsumer color(int r, int g, int b, int a) { this.r=r; this.g=g; this.b=b; this.a=a; return this; }
        public VertexConsumer uv(float u, float v) { this.u=u; this.v=v; return this; }
        public VertexConsumer overlayCoords(int u, int v) { return this; }
        public VertexConsumer uv2(int u, int v) { lu=u; lv=v; return this; }
        public VertexConsumer normal(float x, float y, float z) { return this; }
        public void defaultColor(int r, int g, int b, int a) {}
        public void unsetDefaultColor() {}
        public void endVertex() { vertices.add(new V(x,y,z,u,v,r,g,b,a,lu,lv)); }
    }
}
