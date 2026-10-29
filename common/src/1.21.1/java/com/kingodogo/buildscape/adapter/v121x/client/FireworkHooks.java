package com.kingodogo.buildscape.adapter.v121x.client;

import com.kingodogo.buildscape.firework.CustomFireworkRenderer;
import com.kingodogo.buildscape.firework.CustomFireworkShapeRegistry;
import com.kingodogo.buildscape.mixinsupport.FireworkContext;
import com.kingodogo.buildscape.mixinsupport.FireworkSparkAccess;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Custom firework shapes on 1.21.1. The rocket's custom shape data lives in the custom_data component
 * (Fireworks.Explosions[].Type), next to the vanilla fireworks component that only carries the ball bursts.
 */
public final class FireworkHooks {
    private record FireworkMetadata(ListTag explosions, float yaw) {}

    private static final Map<Object, FireworkMetadata> METADATA = Collections.synchronizedMap(new WeakHashMap<>());

    private FireworkHooks() {}

    public static void handleFireworkStarterTick(Object starter) {
        ItemStack stack = FireworkContext.take(starter);
        if (stack.isEmpty() || !(starter instanceof FireworkStarterAccess access)) return;
        var custom = stack.get(DataComponents.CUSTOM_DATA);
        if (custom == null) return;
        CompoundTag fireworks = custom.copyTag().getCompound("Fireworks");
        ListTag explosions = fireworks.getList("Explosions", Tag.TAG_COMPOUND);
        boolean customShape = false;
        for (int i = 0; i < explosions.size(); i++) {
            if (CustomFireworkShapeRegistry.isCustomShape(explosions.getCompound(i).getByte("Type"))) {
                customShape = true;
                break;
            }
        }
        if (!customShape) return;
        var particleAccess = (com.kingodogo.buildscape.mixin.client.ParticleV121xAccessor) starter;
        float yaw;
        if (fireworks.contains("ShotYaw")) {
            yaw = fireworks.getFloat("ShotYaw");
        } else if (Math.hypot(particleAccess.buildscape$getXd(), particleAccess.buildscape$getZd()) > 0.05) {
            yaw = (float) Math.toDegrees(Math.atan2(particleAccess.buildscape$getXd(), particleAccess.buildscape$getZd()));
        } else {
            yaw = Minecraft.getInstance().player == null ? 0 : Minecraft.getInstance().player.getYRot();
        }
        METADATA.put(starter, new FireworkMetadata(explosions.copy(), yaw));
        Particle particle = (Particle) starter;
        particle.setBoundingBox(particle.getBoundingBox().inflate(120.0D));
    }

    public static boolean renderCustomFireworkExplosion(Object starter, Object colorsObject, Object fadesObject,
            boolean trail, boolean flicker) {
        FireworkMetadata metadata = METADATA.get(starter);
        if (metadata == null || !(starter instanceof FireworkSparkAccess sparks)
                || !(starter instanceof FireworkStarterAccess access)) return false;
        var particleAccess = (com.kingodogo.buildscape.mixin.client.ParticleV121xAccessor) starter;
        int index = access.buildscape$life() / 2;
        if (index < 0 || index >= metadata.explosions().size()) return false;
        CompoundTag tag = metadata.explosions().getCompound(index);
        var shape = CustomFireworkShapeRegistry.getByNumericId(tag.getByte("Type"));
        if (shape.isEmpty()) return false;
        int[] colors = ((IntList) colorsObject).toIntArray();
        int[] fades = ((IntList) fadesObject).toIntArray();
        float yaw = tag.contains("ShotYaw") ? tag.getFloat("ShotYaw") : metadata.yaw();
        CustomFireworkRenderer.renderExplosion(shape.get(),
                particleAccess.buildscape$getX(), particleAccess.buildscape$getY(), particleAccess.buildscape$getZ(),
                colors, fades, trail, flicker, yaw,
                (x, y, z, vx, vy, vz, pointColors, fadeColors, sparkTrail, sparkFlicker) ->
                        sparks.buildscape$spawnSpark(x, y, z, vx, vy, vz,
                                new IntArrayList(pointColors), new IntArrayList(fadeColors), sparkTrail, sparkFlicker));
        return true;
    }
}
