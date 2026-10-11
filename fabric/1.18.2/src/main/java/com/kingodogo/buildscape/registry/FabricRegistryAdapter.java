package com.kingodogo.buildscape.registry;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.adapter.v118x.RegistrationOrder118x;
import com.kingodogo.buildscape.item.ModCreativeTabs;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.Registry;

public final class FabricRegistryAdapter extends BaseRegistryAdapter {
    public FabricRegistryAdapter() { super(BuildscapeCommon.MOD_ID); }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public synchronized void init() {
        if (initialized) return;
        Services.PLATFORM.wrapRegistryAction(() -> {
            initBlocks();
            registerTo(Registry.BLOCK, modBlocks);
            initItems();
            registerTo(Registry.ITEM, RegistrationOrder118x.orderedItems(modItems));
            initBlockEntities();
            registerTo(Registry.BLOCK_ENTITY_TYPE, modBlockEntities);
            initEntities();
            registerTo(Registry.ENTITY_TYPE, modEntities);
            initSounds();
            registerTo(Registry.SOUND_EVENT, modSounds);
            initParticles();
            registerTo(Registry.PARTICLE_TYPE, modParticles);
            initRecipeSerializers();
            registerTo(Registry.RECIPE_SERIALIZER, (java.util.Map) modRecipeSerializers);
            initMenus();
            registerTo(Registry.MENU, modMenus);
            creativeTab = ModCreativeTabs.createTab(modItems);
            initialized = true;
        });
        BuildscapeCommon.LOGGER.info("Registered {} blocks and {} items through Fabric", modBlocks.size(), modItems.size());
    }
}
