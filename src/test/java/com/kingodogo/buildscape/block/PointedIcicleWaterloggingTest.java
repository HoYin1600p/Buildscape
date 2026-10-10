package com.kingodogo.buildscape.block;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;

/** Headless checks using the repository's standalone test-runner convention. */
public final class PointedIcicleWaterloggingTest {
    private PointedIcicleWaterloggingTest() {
    }

    public static void main(String[] args) {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();

        PointedIcicleBlock block = new PointedIcicleBlock(BlockBehaviour.Properties.of(Material.ICE));
        BlockState dry = block.defaultBlockState();
        BlockState wet = dry.setValue(PointedIcicleBlock.WATERLOGGED, true);
        require(block.getFluidState(dry).isEmpty(), "dry icicles must not contain fluid");
        require(block.getFluidState(wet).getType() == Fluids.WATER,
                "waterlogged icicles must report water");
        require(block.getFluidState(wet).isSource(), "contained water must remain a source");

        List<Object[]> scheduledTicks = new ArrayList<>();
        LevelAccessor level = (LevelAccessor) Proxy.newProxyInstance(
                LevelAccessor.class.getClassLoader(),
                new Class<?>[] { LevelAccessor.class },
                (proxy, method, arguments) -> {
                    if (method.getName().equals("getBlockState")) {
                        return Blocks.AIR.defaultBlockState();
                    }
                    if (method.getName().equals("scheduleTick")) {
                        scheduledTicks.add(arguments);
                        return null;
                    }
                    throw new AssertionError("Unexpected world access: " + method.getName());
                });
        BlockPos pos = new BlockPos(0, 64, 0);

        for (Direction direction : Direction.values()) {
            scheduledTicks.clear();
            BlockState updated = block.updateShape(wet, direction, Blocks.AIR.defaultBlockState(),
                    level, pos, pos.relative(direction));
            require(updated.getValue(PointedIcicleBlock.WATERLOGGED),
                    "neighbor updates must preserve contained water: " + direction);
            require(scheduledTicks.size() == 1,
                    "wet icicles must schedule exactly one fluid tick: " + direction);
            Object[] tick = scheduledTicks.get(0);
            require(tick.length == 3 && pos.equals(tick[0]) && tick[1] == Fluids.WATER
                            && Integer.valueOf(Fluids.WATER.getTickDelay(level)).equals(tick[2]),
                    "fluid tick must use the icicle position and vanilla water delay: " + direction);

            scheduledTicks.clear();
            BlockState updatedDry = block.updateShape(dry, direction, Blocks.AIR.defaultBlockState(),
                    level, pos, pos.relative(direction));
            require(!updatedDry.getValue(PointedIcicleBlock.WATERLOGGED),
                    "dry icicles must stay dry: " + direction);
            require(scheduledTicks.isEmpty(), "dry icicles must not schedule fluid ticks: " + direction);
        }

        System.out.println("Pointed icicle waterlogging tests passed");
    }

    private static void require(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError(description);
        }
    }
}
