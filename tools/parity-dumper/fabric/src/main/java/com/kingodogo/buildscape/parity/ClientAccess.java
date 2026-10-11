package com.kingodogo.buildscape.parity;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Item;

final class ClientAccess {
    private ClientAccess() {}

    static Object key(Object key) { return key; }

    static boolean canRenderInLayer(BlockState state, RenderType layer) {
        return ItemBlockRenderTypes.getChunkRenderType(state) == layer;
    }

    static Map<?, ?> blockColors(Minecraft minecraft) {
        Map<Block, BlockColor> handlers = new LinkedHashMap<>();
        Registry.BLOCK.forEach(block -> {
            BlockColor handler = ColorProviderRegistry.BLOCK.get(block);
            if (handler != null) handlers.put(block, handler);
        });
        return handlers;
    }

    static Map<?, ?> itemColors(Minecraft minecraft) {
        Map<Item, ItemColor> handlers = new LinkedHashMap<>();
        Registry.ITEM.forEach(item -> {
            ItemColor handler = ColorProviderRegistry.ITEM.get(item);
            if (handler != null) handlers.put(item, handler);
        });
        return handlers;
    }

    static Map<?, ?> particleProviders(Minecraft minecraft) {
        // Vanilla uses numeric registry IDs, unlike Forge's ResourceLocation map.
        // Match the unique generic field so dev and intermediary names both work.
        try {
            Field candidate = null;
            for (Field field : ParticleEngine.class.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || !Int2ObjectMap.class.isAssignableFrom(field.getType())
                        || !(field.getGenericType() instanceof ParameterizedType mapType)) continue;
                Type[] arguments = mapType.getActualTypeArguments();
                if (arguments.length != 1) continue;
                Type value = arguments[0] instanceof ParameterizedType type ? type.getRawType() : arguments[0];
                if (value != ParticleProvider.class) continue;
                if (candidate != null) throw new IllegalStateException("Ambiguous particle provider table");
                candidate = field;
            }
            if (candidate == null) throw new NoSuchFieldException("No particle provider table");
            candidate.setAccessible(true);
            Int2ObjectMap<?> providers = (Int2ObjectMap<?>) candidate.get(minecraft.particleEngine);
            Map<ResourceLocation, Object> handlers = new LinkedHashMap<>();
            providers.int2ObjectEntrySet().forEach(entry -> {
                ResourceLocation id = Registry.PARTICLE_TYPE.getKey(Registry.PARTICLE_TYPE.byId(entry.getIntKey()));
                if (id == null) throw new IllegalStateException("Unknown particle provider ID " + entry.getIntKey());
                handlers.put(id, entry.getValue());
            });
            return handlers;
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Cannot inspect particle providers", failure);
        }
    }
}
