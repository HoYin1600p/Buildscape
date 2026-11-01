package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class ChainMobLogic {
    private static final CommonId MOB_CHAINS = new CommonId("buildscape", "mob_chains");

    private ChainMobLogic() {}

    public static void onLivingUpdate(LivingEntity entity) {
        if (entity instanceof Player) {
            return;
        }

        BlockPos pos = entity.blockPosition();
        Level level = Services.PLATFORM.getEntityLevel(entity);
        boolean inChain = isChainBlock(level.getBlockState(pos));

        if (inChain) {
            entity.setOnGround(false);
        }
    }

    private static boolean isChainBlock(BlockState state) {
        return Services.PLATFORM.isBlockInTag(state.getBlock(), MOB_CHAINS);
    }
}
