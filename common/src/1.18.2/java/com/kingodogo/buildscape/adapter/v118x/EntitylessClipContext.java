package com.kingodogo.buildscape.adapter.v118x;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Entity-free raycasts matching vanilla's COLLIDER block and NONE fluid modes. */
public final class EntitylessClipContext {
    private EntitylessClipContext() {
    }

    public static BlockHitResult clipCollider(BlockGetter level, Vec3 from, Vec3 to) {
        // Traverse directly: 1.18.2 ClipContext requires an entity on vanilla/Fabric.
        CollisionContext collision = CollisionContext.empty();
        return BlockGetter.traverseBlocks(from, to, level, (getter, pos) -> {
            BlockState state = getter.getBlockState(pos);
            VoxelShape shape = state.getCollisionShape(getter, pos, collision);
            return getter.clipWithInteractionOverride(from, to, pos, shape, state);
        }, getter -> {
            Vec3 direction = from.subtract(to);
            return BlockHitResult.miss(to,
                    Direction.getNearest((float) direction.x, (float) direction.y, (float) direction.z),
                    new BlockPos(to));
        });
    }
}
