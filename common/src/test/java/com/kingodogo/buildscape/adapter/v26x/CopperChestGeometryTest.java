package com.kingodogo.buildscape.adapter.v26x;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CopperChestGeometryTest {
    @Test void vanillaHalfMeshesMeetAtTheSeamForEveryFacing() {
        ModelPart left = ChestModel.createDoubleBodyLeftLayer().bakeRoot();
        ModelPart right = ChestModel.createDoubleBodyRightLayer().bakeRoot();
        for (Direction facing : new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
            // ChestType.LEFT's partner is clockwise from its facing, as in ChestBlock.
            Direction partner = facing.getClockWise();
            for (String part : new String[] {"bottom", "lid"}) {
                double[] leftBounds = bounds(left.getChild(part), facing, 0, 0, partner.getAxis());
                double[] rightBounds = bounds(right.getChild(part), facing,
                        partner.getStepX(), partner.getStepZ(), partner.getAxis());
                if (partner.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                    assertEquals(leftBounds[1], rightBounds[0], 0.00001, facing + " " + part);
                } else {
                    assertEquals(rightBounds[1], leftBounds[0], 0.00001, facing + " " + part);
                }
            }
        }
        assertEquals(ModelLayers.DOUBLE_CHEST_LEFT, ChestRenderer.LAYERS.select(ChestType.LEFT));
        assertEquals(ModelLayers.DOUBLE_CHEST_RIGHT, ChestRenderer.LAYERS.select(ChestType.RIGHT));
    }

    private static double[] bounds(ModelPart part, Direction facing, int x, int z, Direction.Axis axis) {
        PoseStack pose = new PoseStack();
        pose.translate(x, 0, z);
        pose.mulPose(ChestRenderer.modelTransformation(facing));
        double[] bounds = {Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
        part.getExtentsForGui(pose, vertex -> {
            double value = axis == Direction.Axis.X ? vertex.x() : vertex.z();
            bounds[0] = Math.min(bounds[0], value);
            bounds[1] = Math.max(bounds[1], value);
        });
        return bounds;
    }
}
