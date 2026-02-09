package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mod.EventBusSubscriber(modid = BuildScape.MODID)
public class FrostRoseDropHandler {

    private static final List<TrackedDeath> trackedDeaths = new ArrayList<>();

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntityLiving() instanceof SnowGolem)) return;
        if (event.getEntityLiving().level.isClientSide) return;
        if (!(event.getSource().getEntity() instanceof WitherBoss)) return;

        ServerLevel level = (ServerLevel) event.getEntityLiving().level;
        BlockPos deathPos = event.getEntityLiving().blockPosition();
        double deathX = event.getEntityLiving().getX();
        double deathY = event.getEntityLiving().getY();
        double deathZ = event.getEntityLiving().getZ();

        // Drop Frost Rose item at the Snow Golem's death position
        ItemStack frostRose = new ItemStack(ModBlocks.FROST_ROSE.get());
        ItemEntity itemEntity = new ItemEntity(
                level,
                deathPos.getX() + 0.5,
                deathPos.getY() + 0.5,
                deathPos.getZ() + 0.5,
                frostRose
        );
        level.addFreshEntity(itemEntity);

        // Track this death position to intercept Wither Rose (both block and item)
        trackedDeaths.add(new TrackedDeath(level, deathPos, deathX, deathY, deathZ, 20));
    }

    // Intercept Wither Rose ITEM ENTITY spawning near a Snow Golem death position
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getWorld().isClientSide()) return;
        if (!(event.getEntity() instanceof ItemEntity itemEntity)) return;

        ItemStack stack = itemEntity.getItem();
        if (!stack.is(Items.WITHER_ROSE)) return;

        for (TrackedDeath death : trackedDeaths) {
            double dx = itemEntity.getX() - death.x;
            double dy = itemEntity.getY() - death.y;
            double dz = itemEntity.getZ() - death.z;
            if (dx * dx + dy * dy + dz * dz < 4.0) {
                event.setCanceled(true);
                return;
            }
        }
    }

    // Intercept Wither Rose BLOCK placed at a Snow Golem death position
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (trackedDeaths.isEmpty()) return;

        Iterator<TrackedDeath> it = trackedDeaths.iterator();
        while (it.hasNext()) {
            TrackedDeath death = it.next();

            // Check death position and one above for Wither Rose block
            if (death.level.getBlockState(death.pos).is(Blocks.WITHER_ROSE)) {
                death.level.removeBlock(death.pos, false);
                it.remove();
            } else if (death.level.getBlockState(death.pos.above()).is(Blocks.WITHER_ROSE)) {
                death.level.removeBlock(death.pos.above(), false);
                it.remove();
            } else {
                death.ticksRemaining--;
                if (death.ticksRemaining <= 0) {
                    it.remove();
                }
            }
        }
    }

    private static class TrackedDeath {
        final ServerLevel level;
        final BlockPos pos;
        final double x, y, z;
        int ticksRemaining;

        TrackedDeath(ServerLevel level, BlockPos pos, double x, double y, double z, int ticks) {
            this.level = level;
            this.pos = pos;
            this.x = x;
            this.y = y;
            this.z = z;
            this.ticksRemaining = ticks;
        }
    }
}
