package com.kingodogo.buildscape.registry;

import com.kingodogo.buildscape.block.BlockDefinition;
import com.kingodogo.buildscape.block.BlockFactory;
import com.kingodogo.buildscape.item.ItemDefinition;
import com.kingodogo.buildscape.item.ItemFactory;
import com.kingodogo.buildscape.item.ModCreativeTabs;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import com.kingodogo.buildscape.block.ModBlockEntities;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
public abstract class BaseRegistryAdapter implements IRegistryAdapter {
    protected final String namespace;
    protected final Map<String, RegistrySupplier<?>> blocks = new LinkedHashMap<>();
    protected final Map<String, RegistrySupplier<?>> items = new LinkedHashMap<>();
    protected final Map<String, RegistrySupplier<?>> blockEntities = new LinkedHashMap<>();
    protected final Map<String, RegistrySupplier<?>> entities = new LinkedHashMap<>();
    protected final Map<String, RegistrySupplier<?>> sounds = new LinkedHashMap<>();
    protected final Map<String, RegistrySupplier<?>> creativeTabs = new LinkedHashMap<>();
    protected final Map<String, RegistrySupplier<?>> particles = new LinkedHashMap<>();
    protected final Map<String, RegistrySupplier<?>> recipeSerializers = new LinkedHashMap<>();
    protected final Map<String, Block> modBlocks = new LinkedHashMap<>();
    protected final Map<String, Item> modItems = new LinkedHashMap<>();
    protected final Map<String, BlockEntityType<?>> modBlockEntities = new LinkedHashMap<>();
    protected final Map<String, EntityType<?>> modEntities = new LinkedHashMap<>();
    protected final Map<String, SoundEvent> modSounds = new LinkedHashMap<>();
    protected final Map<String, net.minecraft.core.particles.ParticleType<?>> modParticles = new LinkedHashMap<>();
    protected final Map<String, Object> modRecipeSerializers = new LinkedHashMap<>();
    protected final Map<String, net.minecraft.world.inventory.MenuType<?>> modMenus = new LinkedHashMap<>();
    protected CreativeModeTab creativeTab;
    protected volatile boolean initialized = false;
    public BaseRegistryAdapter(String namespace) {
        this.namespace = Objects.requireNonNull(namespace, "namespace cannot be null");
    }

    @Override
    public <T> RegistrySupplier<T> registerBlock(String name, Supplier<T> blockSupplier) {
        RegistrySupplier<T> supplier = createSupplier(name, blockSupplier);
        blocks.put(name, supplier);
        return supplier;
    }

    @Override
    public <T> RegistrySupplier<T> registerItem(String name, Supplier<T> itemSupplier) {
        RegistrySupplier<T> supplier = createSupplier(name, itemSupplier);
        items.put(name, supplier);
        return supplier;
    }

    @Override
    public <T> RegistrySupplier<T> registerBlockEntity(String name, Supplier<T> blockEntitySupplier) {
        RegistrySupplier<T> supplier = createSupplier(name, blockEntitySupplier);
        blockEntities.put(name, supplier);
        return supplier;
    }

    @Override
    public <T> RegistrySupplier<T> registerEntity(String name, Supplier<T> entitySupplier) {
        RegistrySupplier<T> supplier = createSupplier(name, entitySupplier);
        entities.put(name, supplier);
        return supplier;
    }

    @Override
    public <T> RegistrySupplier<T> registerSound(String name, Supplier<T> soundSupplier) {
        RegistrySupplier<T> supplier = createSupplier(name, soundSupplier);
        sounds.put(name, supplier);
        return supplier;
    }

    @Override
    public <T> RegistrySupplier<T> registerParticle(String name, Supplier<T> particleSupplier) {
        RegistrySupplier<T> supplier = createSupplier(name, particleSupplier);
        particles.put(name, supplier);
        return supplier;
    }

    @Override
    public <T> RegistrySupplier<T> registerCreativeTab(String name, Supplier<T> tabSupplier) {
        RegistrySupplier<T> supplier = createSupplier(name, tabSupplier);
        creativeTabs.put(name, supplier);
        return supplier;
    }

    @Override
    public <T> RegistrySupplier<T> registerRecipeSerializer(String name, Supplier<T> serializerSupplier) {
        RegistrySupplier<T> supplier = createSupplier(name, serializerSupplier);
        recipeSerializers.put(name, supplier);
        return supplier;
    }
    protected <T> RegistrySupplier<T> createSupplier(String name, Supplier<T> supplier) {
        return new SimpleRegistrySupplier<>(namespace, name, supplier);
    }

    @Override
    public Collection<RegistrySupplier<?>> getRegisteredBlocks() {
        return Collections.unmodifiableCollection(blocks.values());
    }

    @Override
    public Collection<RegistrySupplier<?>> getRegisteredItems() {
        return Collections.unmodifiableCollection(items.values());
    }

    @Override
    public Collection<RegistrySupplier<?>> getRegisteredBlockEntities() {
        return Collections.unmodifiableCollection(blockEntities.values());
    }

    @Override
    public Collection<RegistrySupplier<?>> getRegisteredEntities() {
        return Collections.unmodifiableCollection(entities.values());
    }

    @Override
    public Collection<RegistrySupplier<?>> getRegisteredParticles() {
        return Collections.unmodifiableCollection(particles.values());
    }

    @Override
    public Collection<RegistrySupplier<?>> getRegisteredRecipeSerializers() {
        return Collections.unmodifiableCollection(recipeSerializers.values());
    }
    public Map<String, Block> getModBlocks() {
        return Collections.unmodifiableMap(modBlocks);
    }
    public Map<String, Item> getModItems() {
        return Collections.unmodifiableMap(modItems);
    }

    public Map<String, BlockEntityType<?>> getModBlockEntities() {
        return Collections.unmodifiableMap(modBlockEntities);
    }

    public Map<String, EntityType<?>> getModEntities() {
        return Collections.unmodifiableMap(modEntities);
    }

    public Map<String, SoundEvent> getModSounds() {
        return Collections.unmodifiableMap(modSounds);
    }

    public Map<String, Object> getModRecipeSerializers() {
        return Collections.unmodifiableMap(modRecipeSerializers);
    }

    public Map<String, net.minecraft.world.inventory.MenuType<?>> getModMenus() {
        return Collections.unmodifiableMap(modMenus);
    }
    public CreativeModeTab getCreativeTab() {
        return creativeTab;
    }
    public void initModEntries() {
        initBlocks();
        initItems();
        initBlockEntities();
        initEntities();
        initSounds();
        initParticles();
        initRecipeSerializers();
        initMenus();
    }

    protected void initBlocks() {
        for (Map.Entry<String, RegistrySupplier<?>> entry : blocks.entrySet()) {
            Object obj = entry.getValue().get();
            if (obj instanceof Block block) {
                modBlocks.put(entry.getKey(), block);
            } else if (obj instanceof BlockDefinition def) {
                Block block = def.getBlock();
                if (block == null) block = BlockFactory.createBlock(def);
                def.setBlock(block);
                modBlocks.put(entry.getKey(), block);
            }
        }

    }

    protected void initItems() {
        for (Map.Entry<String, RegistrySupplier<?>> entry : items.entrySet()) {
            String id = entry.getKey();
            Object obj = entry.getValue().get();
            if (obj instanceof Item item) {
                modItems.put(id, item);
            } else if (obj instanceof ItemDefinition def) {
                Block matchingBlock = modBlocks.get(id);
                if (matchingBlock != null) {
                    modItems.put(id, ItemFactory.createBlockItem(id, matchingBlock, def.getProperties()));
                } else {
                    modItems.put(id, ItemFactory.createItem(def));
                }
            }
        }
    }

    protected void initBlockEntities() {
        modBlockEntities.put("mangrove_sign_block_entity", ModBlockEntities.MANGROVE_SIGN_BLOCK_ENTITY_TYPE);
        modBlockEntities.put("bamboo_sign_block_entity", ModBlockEntities.BAMBOO_SIGN_BLOCK_ENTITY_TYPE);
        modBlockEntities.put("pillar_block_entity", ModBlockEntities.PILLAR_TYPE);
        modBlockEntities.put("decorated_pot_block_entity", ModBlockEntities.DECORATED_POT_TYPE);
        modBlockEntities.put("trapped_decorated_pot_block_entity", ModBlockEntities.TRAPPED_DECORATED_POT_TYPE);
        modBlockEntities.put("icicle_cauldron_block_entity", ModBlockEntities.ICICLE_CAULDRON_TYPE);
        modBlockEntities.put("festive_stocking_block_entity", ModBlockEntities.FESTIVE_STOCKING_TYPE);
        modBlockEntities.put("glow_lights_block_entity", ModBlockEntities.GLOW_LIGHTS_TYPE);
        modBlockEntities.put("smoke_vent_block_entity", ModBlockEntities.SMOKE_VENT_TYPE);
        modBlockEntities.put("cascade_block_entity", ModBlockEntities.CASCADE_TYPE);
        modBlockEntities.put("muff_block_entity", ModBlockEntities.MUFF_TYPE);
        modBlockEntities.put("glass_jar_block_entity", ModBlockEntities.GLASS_JAR_TYPE);
        modBlockEntities.put("builders_workbench", ModBlockEntities.BUILDERS_WORKBENCH_TYPE);
        modBlockEntities.put("copper_chest", ModBlockEntities.COPPER_CHEST_TYPE);
        modBlockEntities.put("potent_sulfur", ModBlockEntities.POTENT_SULFUR_TYPE);
        modBlockEntities.put("shelf", ModBlockEntities.SHELF_TYPE);
        modBlockEntities.put("trophy_block_entity", ModBlockEntities.TROPHY_TYPE);
        modBlockEntities.put("hollow_log", ModBlockEntities.HOLLOW_LOG_TYPE);
        for (Map.Entry<String, RegistrySupplier<?>> entry : blockEntities.entrySet()) {
            Object obj = entry.getValue().get();
            if (obj instanceof BlockEntityType<?> beType) {
                modBlockEntities.put(entry.getKey(), beType);
            }
        }
    }

    protected void initEntities() {
        modEntities.put("falling_icicle", Services.PLATFORM.getFallingIcicleEntityType());
        modEntities.put("festive_stocking", Services.PLATFORM.getFestiveStockingEntityType());
        modEntities.put("mangrove_boat", Services.PLATFORM.getMangroveBoatEntityType());
        modEntities.put("colored_item_frame", Services.PLATFORM.getColoredItemFrameEntityType());
        modEntities.put("seat", Services.PLATFORM.getSeatEntityType());
        modEntities.put("poplar_boat", Services.PLATFORM.getPoplarBoatEntityType());
        modEntities.put("wandering_homemaker", Services.PLATFORM.getWanderingHomemakerEntityType());
        modEntities.put("festive_wandering_homemaker", Services.PLATFORM.getFestiveWanderingHomemakerEntityType());
        for (Map.Entry<String, RegistrySupplier<?>> entry : entities.entrySet()) {
            Object obj = entry.getValue().get();
            if (obj instanceof EntityType<?> eType) {
                modEntities.put(entry.getKey(), eType);
            }
        }
    }

    protected void initSounds() {
        for (Map.Entry<String, RegistrySupplier<?>> entry : sounds.entrySet()) {
            Object obj = entry.getValue().get();
            if (obj instanceof SoundEvent soundEvent) {
                modSounds.put(entry.getKey(), soundEvent);
            }
        }

    }

    protected void initParticles() {
        for (Map.Entry<String, RegistrySupplier<?>> entry : particles.entrySet()) {
            Object obj = entry.getValue().get();
            if (obj instanceof net.minecraft.core.particles.ParticleType<?> p) {
                modParticles.put(entry.getKey(), p);
            }
        }

    }

    protected void initRecipeSerializers() {
        for (Map.Entry<String, RegistrySupplier<?>> entry : recipeSerializers.entrySet()) {
            Object obj = entry.getValue().get();
            if (obj != null) {
                modRecipeSerializers.put(entry.getKey(), obj);
            }
        }
    }

    protected void initMenus() {
        modMenus.put("builders_workbench", com.kingodogo.buildscape.menu.ModMenuTypes.BUILDERS_WORKBENCH_MENU);
        modMenus.put("builders_pouch", com.kingodogo.buildscape.menu.ModMenuTypes.BUILDERS_POUCH_MENU);
    }
    public <V> void registerTo(Registry<V> registry, Map<String, V> map) {
        for (Map.Entry<String, V> entry : map.entrySet()) {
            Services.PLATFORM.register(registry, new CommonId(namespace, entry.getKey()), entry.getValue());
        }
    }
    public boolean isInitialized() {
        return initialized;
    }
    @Override
    public void init() {
        if (!initialized) {
            Services.PLATFORM.wrapRegistryAction(() -> {
                initModEntries();
                for (Map.Entry<String, net.minecraft.core.particles.ParticleType<?>> entry : modParticles.entrySet()) {
                    Services.PLATFORM.registerParticleType(new CommonId(namespace, entry.getKey()), entry.getValue());
                }
                this.creativeTab = ModCreativeTabs.createTab(modItems);
                this.initialized = true;
            });
        }
    }
}
