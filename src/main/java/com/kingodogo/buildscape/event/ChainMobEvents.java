package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BuildScape.MODID)
public class ChainMobEvents {
    private static final TagKey<Block> MOB_CHAINS = TagKey.create(Registry.BLOCK_REGISTRY,
            new ResourceLocation(BuildScape.MODID, "mob_chains"));

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();

        if (entity instanceof Player) {
            return;
        }

        BlockPos pos = entity.blockPosition();
        boolean inChain = entity.level.getBlockState(pos).is(MOB_CHAINS);

        if (inChain) {
            entity.setOnGround(false);
        }
    }
}
