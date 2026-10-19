package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mod.EventBusSubscriber(modid = BuildScape.MODID)
public class FrostRoseDropHandler {

    private static final List<TrackedDeath> trackedDeaths = new ArrayList<>();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntityLiving() instanceof SnowGolem)) return;
        if (event.getEntityLiving().level.isClientSide) return;
        if (!(event.getSource().getEntity() instanceof WitherBoss)) return;

        ServerLevel level = (ServerLevel) event.getEntityLiving().level;
        BlockPos deathPos = event.getEntityLiving().blockPosition();
        double deathX = event.getEntityLiving().getX();
        double deathY = event.getEntityLiving().getY();
        double deathZ = event.getEntityLiving().getZ();

        trackedDeaths.add(new TrackedDeath(level.dimension(), deathPos, deathX, deathY, deathZ,
                (SnowGolem) event.getEntityLiving(), 20));
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getWorld().isClientSide()) return;
        if (!(event.getEntity() instanceof ItemEntity itemEntity)) return;

        ItemStack stack = itemEntity.getItem();
        if (!stack.is(Items.WITHER_ROSE)) return;

        for (TrackedDeath death : trackedDeaths) {
            if (death.dimension != itemEntity.level.dimension()) continue;
            double dx = itemEntity.getX() - death.x;
            double dy = itemEntity.getY() - death.y;
            double dz = itemEntity.getZ() - death.z;
            if (dx * dx + dy * dy + dz * dz < 4.0) {
                event.setCanceled(true);
                return;
            }
        }
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        trackedDeaths.clear();
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (trackedDeaths.isEmpty()) return;

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            trackedDeaths.clear();
            return;
        }
        Iterator<TrackedDeath> it = trackedDeaths.iterator();
        while (it.hasNext()) {
            TrackedDeath death = it.next();
            ServerLevel level = server.getLevel(death.dimension);
            if (level == null || !level.isLoaded(death.pos)) {
                it.remove();
                continue;
            }

            if (death.dropPending) {
                // A skull applies damage before exploding. Wait until all entity ticks have finished.
                // A golem revived before the end of the tick must not create a rose.
                if (!death.golem.isDeadOrDying()) {
                    it.remove();
                    continue;
                }
                level.addFreshEntity(new ItemEntity(level,
                        death.pos.getX() + 0.5, death.pos.getY() + 0.5, death.pos.getZ() + 0.5,
                        new ItemStack(ModBlocks.FROST_ROSE.get())));
                death.dropPending = false;
            }

            if (level.getBlockState(death.pos).is(Blocks.WITHER_ROSE)) {
                level.removeBlock(death.pos, false);
                it.remove();
            } else if (level.getBlockState(death.pos.above()).is(Blocks.WITHER_ROSE)) {
                level.removeBlock(death.pos.above(), false);
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
        final ResourceKey<Level> dimension;
        final BlockPos pos;
        final double x, y, z;
        final SnowGolem golem;
        boolean dropPending = true;
        int ticksRemaining;

        TrackedDeath(ResourceKey<Level> dimension, BlockPos pos, double x, double y, double z, SnowGolem golem, int ticks) {
            this.dimension = dimension;
            this.pos = pos;
            this.x = x;
            this.y = y;
            this.z = z;
            this.golem = golem;
            this.ticksRemaining = ticks;
        }
    }
}
