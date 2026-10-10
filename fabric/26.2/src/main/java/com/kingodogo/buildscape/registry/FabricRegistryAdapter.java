package com.kingodogo.buildscape.registry;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.item.ModCreativeTabs;
import net.minecraft.core.registries.BuiltInRegistries;

public class FabricRegistryAdapter extends BaseRegistryAdapter {
    public FabricRegistryAdapter() {
        super(BuildscapeCommon.MOD_ID);
    }

    @Override
    public synchronized void init() {
        if (isInitialized()) return;
        com.kingodogo.buildscape.adapter.v26x.fluid.ExperienceFluids.register();
        super.init();
        registerTo(BuiltInRegistries.BLOCK, modBlocks);
        registerTo(BuiltInRegistries.ITEM, modItems);
        com.kingodogo.buildscape.adapter.v26x.RemovedIdAliases.register(BuiltInRegistries.BLOCK,
                ((net.fabricmc.fabric.api.event.registry.FabricRegistry) BuiltInRegistries.BLOCK)::addAlias);
        com.kingodogo.buildscape.adapter.v26x.RemovedIdAliases.register(BuiltInRegistries.ITEM,
                ((net.fabricmc.fabric.api.event.registry.FabricRegistry) BuiltInRegistries.ITEM)::addAlias);
        registerTo(BuiltInRegistries.BLOCK_ENTITY_TYPE, modBlockEntities);
        registerTo(BuiltInRegistries.ENTITY_TYPE, modEntities);
        registerTo(BuiltInRegistries.SOUND_EVENT, modSounds);
        registerTo(BuiltInRegistries.RECIPE_SERIALIZER, (java.util.Map) modRecipeSerializers);
        registerTo(BuiltInRegistries.MENU, modMenus);
        if (creativeTab != null) {
            com.kingodogo.buildscape.platform.Services.PLATFORM.register(BuiltInRegistries.CREATIVE_MODE_TAB, ModCreativeTabs.TAB_ID, creativeTab);
        }
        // Fabric registers custom stats during onInitialize, while the registries are writable.
        com.kingodogo.buildscape.stat.ModStats.registerStats();
        System.out.println("[Buildscape Fabric 26.2] Successfully registered " + modBlocks.size() + " blocks, " + modItems.size() + " items, " + modBlockEntities.size() + " block entities, " + modEntities.size() + " entities, " + modSounds.size() + " sounds, and " + modMenus.size() + " menus.");
    }
}
