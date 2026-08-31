package com.kingodogo.buildscape.firework;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PhoenixFireworkDistanceHandler {

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getWorld() instanceof ServerLevel serverLevel && event.getEntity() instanceof FireworkRocketEntity rocket) {
            ItemStack stack = rocket.getItem();
            if (stack.is(Items.FIREWORK_ROCKET) && stack.hasTag()) {
                CompoundTag tag = stack.getTagElement("Fireworks");
                if (tag != null && tag.contains("Explosions", 9)) {
                    ListTag explosions = tag.getList("Explosions", 10);
                    for (int i = 0; i < explosions.size(); ++i) {
                        CompoundTag expTag = explosions.getCompound(i);
                        if (expTag.getByte("Type") == CustomFireworkShapeRegistry.PHOENIX_ID) {
                            // Ensure explosion packet is broadcast to all players within view distance
                            broadcastPhoenixExplosionToFarPlayers(serverLevel, rocket);
                            break;
                        }
                    }
                }
            }
        }
    }

    public static void broadcastPhoenixExplosionToFarPlayers(ServerLevel level, FireworkRocketEntity rocket) {
        double x = rocket.getX();
        double y = rocket.getY();
        double z = rocket.getZ();

        // High view distance limit (512 blocks = 32 chunks)
        double maxDistSq = 512.0 * 512.0;

        for (ServerPlayer player : level.players()) {
            double distSq = player.distanceToSqr(x, y, z);
            if (distSq <= maxDistSq) {
                player.connection.send(new ClientboundEntityEventPacket(rocket, (byte) 17));
            }
        }
    }
}
