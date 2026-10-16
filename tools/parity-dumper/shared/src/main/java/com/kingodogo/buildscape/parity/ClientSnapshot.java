package com.kingodogo.buildscape.parity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

final class ClientSnapshot {
    private ClientSnapshot() {}

    private static ResourceLocation id(Object key) {
        key = ClientAccess.key(key);
        if (key instanceof ResourceLocation location) return location;
        if (key instanceof Block block) return Registry.BLOCK.getKey(block);
        if (key instanceof Item item) return Registry.ITEM.getKey(item);
        if (key instanceof BlockEntityType<?> type) return Registry.BLOCK_ENTITY_TYPE.getKey(type);
        if (key instanceof EntityType<?> type) return Registry.ENTITY_TYPE.getKey(type);
        if (key instanceof MenuType<?> type) return Registry.MENU.getKey(type);
        throw new IllegalStateException("Unknown client registration key " + key.getClass().getName());
    }

    private static JsonObject registrations(Map<?, ?> registrations) {
        JsonObject result = new JsonObject();
        registrations.forEach((key, value) -> {
            ResourceLocation id = id(key);
            if (DumpIO.included(id)) result.add(id.toString(), DumpIO.object("implementation", DumpIO.implementation(value)));
        });
        return result;
    }

    static void snapshot(DumpIO.TableWriter writer) {
        Minecraft minecraft = Minecraft.getInstance();
        RegistryDump.commonTables().forEach(writer::table);
        writer.table("render-layers.json", () -> RegistryDump.entries(Registry.BLOCK, block -> {
            JsonObject states = new JsonObject();
            block.getStateDefinition().getPossibleStates().forEach(state -> {
                JsonArray allowed = new JsonArray();
                RenderType.chunkBufferLayers().stream().filter(layer -> ClientAccess.canRenderInLayer(state, layer))
                        .map(layer -> layer.toString().split(":", 2)[0]).sorted().forEach(allowed::add);
                states.add(RegistryDump.stateId(state), allowed);
            });
            return states;
        }));
        writer.table("block-colors.json", () -> blockColors(ClientAccess.blockColors(minecraft)));
        writer.table("item-colors.json", () -> itemColors(ClientAccess.itemColors(minecraft)));
        writer.table("block-entity-renderers.json", () -> registrations(DumpIO.table(BlockEntityRenderDispatcher.class,
                minecraft.getBlockEntityRenderDispatcher(), BlockEntityType.class, BlockEntityRenderer.class, "renderers", "f_112251_")));
        writer.table("entity-renderers.json", () -> registrations(DumpIO.table(EntityRenderDispatcher.class,
                minecraft.getEntityRenderDispatcher(), EntityType.class, EntityRenderer.class, "renderers", "f_114362_")));
        // ScreenConstructor is private in vanilla; the sole static Map is sufficient
        // to identify this table without a compile-time reference to that type.
        writer.table("screens.json", () -> registrations(DumpIO.table(MenuScreens.class,
                null, MenuType.class, null, "SCREENS", "f_96196_")));
        writer.table("particle-providers.json", () -> registrations(ClientAccess.particleProviders(minecraft)));
    }

    private static JsonElement blockColors(Map<?, ?> handlers) {
        JsonObject result = registrations(handlers);
        handlers.forEach((key, value) -> {
            ResourceLocation id = id(key);
            if (!DumpIO.included(id)) return;
            BlockColor handler = (BlockColor) value;
            JsonObject samples = new JsonObject();
            Registry.BLOCK.get(id).getStateDefinition().getPossibleStates().forEach(state -> {
                JsonArray colors = new JsonArray();
                for (int tint = 0; tint < 8; tint++) {
                    try { colors.add(handler.getColor(state, null, BlockPos.ZERO, tint)); }
                    catch (RuntimeException contextual) { colors.add(DumpIO.object("requiresContext", contextual.getClass().getName())); }
                }
                samples.add(RegistryDump.stateId(state), colors);
            });
            result.getAsJsonObject(id.toString()).add("samples", samples);
        });
        return result;
    }

    private static JsonElement itemColors(Map<?, ?> handlers) {
        JsonObject result = registrations(handlers);
        handlers.forEach((key, value) -> {
            ResourceLocation id = id(key);
            if (!DumpIO.included(id)) return;
            ItemColor handler = (ItemColor) value;
            ItemStack stack = new ItemStack(Registry.ITEM.get(id));
            JsonArray colors = new JsonArray();
            for (int tint = 0; tint < 8; tint++) {
                try { colors.add(handler.getColor(stack, tint)); }
                catch (RuntimeException contextual) { colors.add(DumpIO.object("requiresContext", contextual.getClass().getName())); }
            }
            result.getAsJsonObject(id.toString()).add("samples", colors);
        });
        return result;
    }
}
