package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.ClimbableChainBlock;
import com.kingodogo.buildscape.block.LargeChainBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BuildScape.MODID)
public class ChainMobHandler {

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();

        if (entity instanceof Player) {
            return;
        }

        BlockPos pos = entity.blockPosition();
        boolean inChain = isChainBlock(entity.level.getBlockState(pos));

        if (inChain) {
            entity.setOnGround(false);
        }
    }

    private static boolean isChainBlock(BlockState state) {
        return (
                state.getBlock() instanceof ChainBlock ||
                        state.getBlock() instanceof ClimbableChainBlock ||
                        state.getBlock() instanceof LargeChainBlock
        );
    }
}
