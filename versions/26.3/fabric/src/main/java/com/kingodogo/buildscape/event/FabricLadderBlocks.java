package com.kingodogo.buildscape.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Method;

/**
 * Buildscape blocks (and the vanilla chain through ChainBlockMixin) declare Forge's
 * isLadder(state, level, pos, entity); Fabric never calls it, so it is looked up by name here.
 */
public final class FabricLadderBlocks {
    private static final Method NONE;

    static {
        try {
            NONE = FabricLadderBlocks.class.getDeclaredMethod("none");
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static final ClassValue<Method> LADDER_METHODS = new ClassValue<>() {
        @Override
        protected Method computeValue(Class<?> blockClass) {
            try {
                Method method = blockClass.getMethod("isLadder",
                        BlockState.class, LevelReader.class, BlockPos.class, LivingEntity.class);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException e) {
                return NONE;
            }
        }
    };

    private FabricLadderBlocks() {}

    private static void none() {}

    public static boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
        Method method = LADDER_METHODS.get(state.getBlock().getClass());
        if (method == NONE) return false;
        try {
            return (boolean) method.invoke(state.getBlock(), state, level, pos, entity);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("isLadder failed on " + state.getBlock().getClass().getName(), e);
        }
    }
}
