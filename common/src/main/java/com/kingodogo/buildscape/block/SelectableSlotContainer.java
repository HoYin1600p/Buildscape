package com.kingodogo.buildscape.block;

import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
public interface SelectableSlotContainer {
    int getRows();
    int getColumns();
    default OptionalInt getHitSlot(final BlockHitResult hitResult, final Direction blockFacing) {
        return getRelativeHitCoordinatesForBlockFace(hitResult, blockFacing).map(hitCoords -> {
            int row = getSection(1.0F - hitCoords.y, this.getRows());
            int column = getSection(hitCoords.x, this.getColumns());
            return OptionalInt.of(column + row * this.getColumns());
        }).orElseGet(OptionalInt::empty);
    }
    private static Optional<Vec2> getRelativeHitCoordinatesForBlockFace(final BlockHitResult hitResult, final Direction blockFacing) {
        Direction hitDirection = hitResult.getDirection();
        if (blockFacing != hitDirection) {
            return Optional.empty();
        } else {
            BlockPos hitBlockPos = hitResult.getBlockPos().relative(hitDirection);
            Vec3 relativeHit = hitResult.getLocation().subtract(hitBlockPos.getX(), hitBlockPos.getY(), hitBlockPos.getZ());
            double rx = relativeHit.x();
            double ry = relativeHit.y();
            double rz = relativeHit.z();
            return switch (hitDirection) {
                case NORTH -> Optional.of(new Vec2((float) (1.0D - rx), (float) ry));
                case SOUTH -> Optional.of(new Vec2((float) rx, (float) ry));
                case WEST -> Optional.of(new Vec2((float) rz, (float) ry));
                case EAST -> Optional.of(new Vec2((float) (1.0D - rz), (float) ry));
                default -> Optional.empty();
            };
        }
    }
    private static int getSection(final float relativeCoordinate, final int maxSections) {
        float targetedPixel = relativeCoordinate * 16.0F;
        float sectionSize = 16.0F / (float) maxSections;
        return Mth.clamp(Mth.floor(targetedPixel / sectionSize), 0, maxSections - 1);
    }
}
