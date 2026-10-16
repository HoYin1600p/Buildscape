package com.kingodogo.buildscape.registry;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.item.ModCreativeTabs;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.registries.BuiltInRegistries;

public class FabricRegistryAdapter extends BaseRegistryAdapter {
    public FabricRegistryAdapter() {
        super(BuildscapeCommon.MOD_ID);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public synchronized void init() {
        if (initialized) return;
        Services.PLATFORM.wrapRegistryAction(() -> {
            Services.PLATFORM.register(BuiltInRegistries.FLUID, new CommonId(namespace, "experience_still"),
                    com.kingodogo.buildscape.adapter.v121x.ExperienceFluids.still());
            Services.PLATFORM.register(BuiltInRegistries.FLUID, new CommonId(namespace, "experience_flowing"),
                    com.kingodogo.buildscape.adapter.v121x.ExperienceFluids.flowing());
            initBlocks();
            registerTo(BuiltInRegistries.BLOCK, modBlocks);
            initItems();
            registerTo(BuiltInRegistries.ITEM, modItems);
            initBlockEntities();
            registerTo(BuiltInRegistries.BLOCK_ENTITY_TYPE, modBlockEntities);
            initEntities();
            registerTo(BuiltInRegistries.ENTITY_TYPE, modEntities);
            initSounds();
            registerTo(BuiltInRegistries.SOUND_EVENT, modSounds);
            initParticles();
            registerTo(BuiltInRegistries.PARTICLE_TYPE, modParticles);
            initRecipeSerializers();
            registerTo(BuiltInRegistries.RECIPE_SERIALIZER, (java.util.Map) modRecipeSerializers);
            initMenus();
            registerTo(BuiltInRegistries.MENU, modMenus);
            creativeTab = ModCreativeTabs.createTab(modItems);
            Services.PLATFORM.register(BuiltInRegistries.CREATIVE_MODE_TAB, ModCreativeTabs.TAB_ID, creativeTab);
            for (var entry : creativeTabs.entrySet()) {
                Services.PLATFORM.register(BuiltInRegistries.CREATIVE_MODE_TAB,
                        new CommonId(namespace, entry.getKey()),
                        (net.minecraft.world.item.CreativeModeTab) entry.getValue().get());
            }
            initialized = true;
        });
        BuildscapeCommon.LOGGER.info("Registered {} blocks and {} items through Fabric", modBlocks.size(), modItems.size());
    }
}
