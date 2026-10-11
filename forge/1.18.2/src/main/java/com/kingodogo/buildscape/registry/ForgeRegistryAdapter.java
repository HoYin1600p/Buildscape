package com.kingodogo.buildscape.registry;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.adapter.v118x.RegistrationOrder118x;
import com.kingodogo.buildscape.item.ModCreativeTabs;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/** Prepare each common batch before Forge consumes its deferred entries. */
public final class ForgeRegistryAdapter extends BaseRegistryAdapter {
    private final Map<Registry<?>, DeferredRegister<?>> deferred = new IdentityHashMap<>();
    private final Set<Registry<?>> prepared = Collections.newSetFromMap(new IdentityHashMap<>());
    private final Set<Registry<?>> completed = Collections.newSetFromMap(new IdentityHashMap<>());
    private boolean attached;

    public ForgeRegistryAdapter() {
        super(BuildscapeCommon.MOD_ID);
    }

    public synchronized void attach(IEventBus bus) {
        if (attached) throw new IllegalStateException("Forge registries already attached");
        attached = true;
        bind(bus, Block.class, Registry.BLOCK, () -> { initBlocks(); registerTo(Registry.BLOCK, modBlocks); });
        bind(bus, Item.class, Registry.ITEM, () -> {
            initItems();
            // DeferredRegister preserves enqueue order; vanilla search uses Registry.ITEM order.
            registerTo(Registry.ITEM, RegistrationOrder118x.orderedItems(modItems));
        });
        bind(bus, BlockEntityType.class, Registry.BLOCK_ENTITY_TYPE,
                () -> { initBlockEntities(); registerTo(Registry.BLOCK_ENTITY_TYPE, modBlockEntities); });
        bind(bus, EntityType.class, Registry.ENTITY_TYPE,
                () -> { initEntities(); registerTo(Registry.ENTITY_TYPE, modEntities); });
        bind(bus, SoundEvent.class, Registry.SOUND_EVENT,
                () -> { initSounds(); registerTo(Registry.SOUND_EVENT, modSounds); });
        bind(bus, ParticleType.class, Registry.PARTICLE_TYPE,
                () -> { initParticles(); registerTo(Registry.PARTICLE_TYPE, modParticles); });
        bind(bus, RecipeSerializer.class, Registry.RECIPE_SERIALIZER, this::prepareRecipeSerializers);
        bind(bus, MenuType.class, Registry.MENU, () -> { initMenus(); registerTo(Registry.MENU, modMenus); });
        // These entries are queued by cluster factories during prepareRegistration.
        com.kingodogo.buildscape.fluid.ForgeExperienceFluids.FLUIDS.register(bus);
        bind(bus, Feature.class, Registry.FEATURE, null);
        bind(bus, ConfiguredFeature.class, net.minecraft.data.BuiltinRegistries.CONFIGURED_FEATURE, null);
        bind(bus, BlockStateProviderType.class, Registry.BLOCKSTATE_PROVIDER_TYPES, null);
        bind(bus, PlacementModifierType.class, Registry.PLACEMENT_MODIFIERS, null);
        bind(bus, TreeDecoratorType.class, Registry.TREE_DECORATOR_TYPES, null);
        bind(bus, FoliagePlacerType.class, Registry.FOLIAGE_PLACER_TYPES, null);
        bind(bus, TrunkPlacerType.class, Registry.TRUNK_PLACER_TYPES, null);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void prepareRecipeSerializers() {
        initRecipeSerializers();
        registerTo(Registry.RECIPE_SERIALIZER, (Map) modRecipeSerializers);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void bind(IEventBus bus, Class<?> entryType, Registry<?> registry, Runnable initializer) {
        DeferredRegister register = DeferredRegister.create(registry.key(), namespace);
        deferred.put(registry, register);
        if (!IForgeRegistryEntry.class.isAssignableFrom(entryType)) {
            register.register(bus);
            return;
        }
        Consumer<RegistryEvent.Register<?>> prepare = event -> Services.PLATFORM.wrapRegistryAction(() -> {
            if (!prepared.add(registry)) throw new IllegalStateException("Duplicate registry event: " + registry.key());
            if (initializer != null) initializer.run();
        });
        Consumer<RegistryEvent.Register<?>> finish = event -> completed.add(registry);
        // DeferredRegister handles Register at NORMAL. Populate it first, then record completion.
        bus.addGenericListener((Class) entryType, EventPriority.HIGH, (Consumer) prepare);
        register.register(bus);
        bus.addGenericListener((Class) entryType, EventPriority.LOW, (Consumer) finish);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public <V> void enqueue(Registry<V> registry, CommonId id, V value) {
        if (!namespace.equals(id.getNamespace())) throw new IllegalArgumentException("Foreign registration: " + id);
        if (completed.contains(registry)) throw new IllegalStateException("Late registration: " + id);
        DeferredRegister register = deferred.get(registry);
        if (register == null) throw new IllegalStateException("No Forge binding for " + registry.key());
        Services.PLATFORM.wrapRegistryAction(() -> register.register(id.getPath(), () -> {
            final Object[] result = new Object[1];
            Services.PLATFORM.wrapRegistryAction(() -> result[0] = value);
            return result[0];
        }));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public <V> void queue(Registry<V> registry, String path, java.util.function.Supplier<? extends V> factory) {
        DeferredRegister register = deferred.get(registry);
        if (register == null) throw new IllegalStateException("No Forge binding for " + registry.key());
        register.register(path, () -> {
            var value = new java.util.concurrent.atomic.AtomicReference<V>();
            Services.PLATFORM.wrapRegistryAction(() -> value.set(factory.get()));
            return value.get();
        });
    }
    @Override
    public synchronized void init() {
        if (initialized) return;
        if (!completed.contains(Registry.BLOCK) || !completed.contains(Registry.ITEM)
                || !completed.contains(Registry.BLOCK_ENTITY_TYPE) || !completed.contains(Registry.ENTITY_TYPE)
                || !completed.contains(Registry.SOUND_EVENT) || !completed.contains(Registry.PARTICLE_TYPE)
                || !completed.contains(Registry.RECIPE_SERIALIZER) || !completed.contains(Registry.MENU)) {
            throw new IllegalStateException("Buildscape Forge registry events have not completed");
        }
        Services.PLATFORM.wrapRegistryAction(() -> creativeTab = ModCreativeTabs.createTab(modItems));
        initialized = true;
        BuildscapeCommon.LOGGER.info("Registered {} blocks and {} items through Forge events",
                modBlocks.size(), modItems.size());
    }
}
