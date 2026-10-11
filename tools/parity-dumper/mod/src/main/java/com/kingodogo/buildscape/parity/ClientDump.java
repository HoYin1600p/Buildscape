package com.kingodogo.buildscape.parity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.TreeMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.IRegistryDelegate;

@Mod.EventBusSubscriber(modid = ParityDumper.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientDump {
    private static volatile boolean pending;

    private ClientDump() {}

    @Mod.EventBusSubscriber(modid = ParityDumper.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class ReloadRegistration {
        private ReloadRegistration() {}

        @SubscribeEvent
        public static void register(RegisterClientReloadListenersEvent event) {
            event.registerReloadListener(new SimplePreparableReloadListener<Boolean>() {
                @Override
                protected Boolean prepare(ResourceManager manager, ProfilerFiller profiler) { return Boolean.TRUE; }

                @Override
                protected void apply(Boolean prepared, ResourceManager manager, ProfilerFiller profiler) { pending = true; }
            });
        }
    }

    @SubscribeEvent
    public static void tags(TagsUpdatedEvent event) {
        if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED) pending = true;
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (event.phase != TickEvent.Phase.END || !pending || minecraft.getOverlay() != null) return;
        pending = false;
        DumpIO.capture(FMLPaths.GAMEDIR.get().resolve("parity-dump/client"), "client", ClientDump::snapshot);
    }

    private static ResourceLocation id(Object key) {
        if (key instanceof IRegistryDelegate<?> delegate) key = delegate.get();
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

    private static Map<?, ?> readMap(Class<?> owner, Object value, String mojmap, String searge) {
        return (Map<?, ?>) DumpIO.field(owner, value, mojmap, searge);
    }

    private static Map<String, JsonElement> snapshot() {
        Minecraft minecraft = Minecraft.getInstance();
        Map<String, JsonElement> files = new TreeMap<>(RegistryDump.common());
        JsonObject layers = RegistryDump.entries(Registry.BLOCK, block -> {
            JsonObject states = new JsonObject();
            block.getStateDefinition().getPossibleStates().forEach(state -> {
                JsonArray allowed = new JsonArray();
                RenderType.chunkBufferLayers().stream().filter(layer -> ItemBlockRenderTypes.canRenderInLayer(state, layer))
                        .map(layer -> layer.toString().split(":", 2)[0]).sorted().forEach(allowed::add);
                states.add(RegistryDump.stateId(state), allowed);
            });
            return states;
        });
        files.put("render-layers.json", layers);
        Map<?, ?> blockColors = readMap(BlockColors.class, minecraft.getBlockColors(), "blockColors", "f_92571_");
        Map<?, ?> itemColors = readMap(ItemColors.class, minecraft.getItemColors(), "itemColors", "f_92674_");
        files.put("block-colors.json", blockColors(blockColors));
        files.put("item-colors.json", itemColors(itemColors));
        files.put("block-entity-renderers.json", registrations(readMap(BlockEntityRenderDispatcher.class,
                minecraft.getBlockEntityRenderDispatcher(), "renderers", "f_112251_")));
        files.put("entity-renderers.json", registrations(readMap(EntityRenderDispatcher.class,
                minecraft.getEntityRenderDispatcher(), "renderers", "f_114362_")));
        files.put("screens.json", registrations(readMap(MenuScreens.class, null, "SCREENS", "f_96196_")));
        files.put("particle-providers.json", registrations(readMap(ParticleEngine.class, minecraft.particleEngine,
                "providers", "f_107293_")));
        return files;
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
