package com.kingodogo.buildscape.registry;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.item.ModCreativeTabs;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegisterEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class NeoForgeRegistryAdapter extends BaseRegistryAdapter {
    private record Pending(ResourceKey<?> key, Consumer<RegisterEvent> register) {}
    private final ArrayList<Pending> pending = new ArrayList<>();
    private final Set<ResourceKey<?>> completed = new HashSet<>();
    private RegisterEvent activeEvent;

    public NeoForgeRegistryAdapter() { super(BuildscapeCommon.MOD_ID); }

    public <V> void enqueue(Registry<V> registry, CommonId id, V value) {
        ResourceKey<?> key = registry.key();
        Consumer<RegisterEvent> registration = event -> event.register(registry.key(),
                Identifier.fromNamespaceAndPath(id.getNamespace(), id.getPath()), () -> value);
        if (activeEvent != null && activeEvent.getRegistryKey().equals(key)) {
            registration.accept(activeEvent);
        } else {
            if (completed.contains(key)) throw new IllegalStateException("Late registration: " + id);
            pending.add(new Pending(key, registration));
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void onRegister(RegisterEvent event) {
        activeEvent = event;
        try {
            var key = event.getRegistryKey();
            if (key.equals(Registries.BLOCK)) {
                initBlocks();
                registerTo(BuiltInRegistries.BLOCK, modBlocks);
            } else if (key.equals(Registries.ITEM)) {
                initItems();
                registerTo(BuiltInRegistries.ITEM, modItems);
            } else if (key.equals(Registries.BLOCK_ENTITY_TYPE)) {
                initBlockEntities();
                registerTo(BuiltInRegistries.BLOCK_ENTITY_TYPE, modBlockEntities);
            } else if (key.equals(Registries.ENTITY_TYPE)) {
                initEntities();
                registerTo(BuiltInRegistries.ENTITY_TYPE, modEntities);
            } else if (key.equals(Registries.SOUND_EVENT)) {
                initSounds();
                registerTo(BuiltInRegistries.SOUND_EVENT, modSounds);
            } else if (key.equals(Registries.PARTICLE_TYPE)) {
                initParticles();
                registerTo(BuiltInRegistries.PARTICLE_TYPE, modParticles);
            } else if (key.equals(Registries.RECIPE_SERIALIZER)) {
                initRecipeSerializers();
                registerTo(BuiltInRegistries.RECIPE_SERIALIZER, (java.util.Map) modRecipeSerializers);
            } else if (key.equals(Registries.MENU)) {
                initMenus();
                registerTo(BuiltInRegistries.MENU, modMenus);
            } else if (key.equals(Registries.CREATIVE_MODE_TAB)) {
                creativeTab = ModCreativeTabs.createTab(modItems);
                Services.PLATFORM.register(BuiltInRegistries.CREATIVE_MODE_TAB, ModCreativeTabs.TAB_ID, creativeTab);
                for (var entry : creativeTabs.entrySet()) {
                    Services.PLATFORM.register(BuiltInRegistries.CREATIVE_MODE_TAB,
                            new CommonId(namespace, entry.getKey()),
                            (net.minecraft.world.item.CreativeModeTab) entry.getValue().get());
                }
            } else if (key.equals(Registries.CUSTOM_STAT)) {
                com.kingodogo.buildscape.stat.ModStats.registerStats();
            }
            var iterator = pending.iterator();
            while (iterator.hasNext()) {
                Pending entry = iterator.next();
                if (entry.key().equals(key)) {
                    entry.register().accept(event);
                    iterator.remove();
                }
            }
            completed.add(key);
        } finally {
            activeEvent = null;
        }
    }

    @Override
    public synchronized void init() {
        if (initialized) return;
        if (!pending.isEmpty() || modBlocks.isEmpty() || modItems.isEmpty()) {
            throw new IllegalStateException("Buildscape registry events have not completed");
        }
        initialized = true;
        BuildscapeCommon.LOGGER.info("Registered {} blocks and {} items through NeoForge events",
                modBlocks.size(), modItems.size());
    }
}
