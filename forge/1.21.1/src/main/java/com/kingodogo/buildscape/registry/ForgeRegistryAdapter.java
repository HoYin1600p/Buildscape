package com.kingodogo.buildscape.registry;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.item.ModCreativeTabs;
import net.minecraft.core.registries.BuiltInRegistries;

public class ForgeRegistryAdapter extends BaseRegistryAdapter {
    public ForgeRegistryAdapter() {
        super(BuildscapeCommon.MOD_ID);
    }

    @Override
    public void init() {
        com.kingodogo.buildscape.platform.Services.PLATFORM.wrapRegistryAction(() -> {
            super.init();
            registerTo(BuiltInRegistries.BLOCK, modBlocks);
            registerTo(BuiltInRegistries.ITEM, modItems);
            registerTo(BuiltInRegistries.BLOCK_ENTITY_TYPE, modBlockEntities);
            registerTo(BuiltInRegistries.ENTITY_TYPE, modEntities);
            registerTo(BuiltInRegistries.SOUND_EVENT, modSounds);
            registerTo(BuiltInRegistries.RECIPE_SERIALIZER, (java.util.Map) modRecipeSerializers);
            registerTo(BuiltInRegistries.MENU, modMenus);
            if (creativeTab != null) {
                com.kingodogo.buildscape.platform.Services.PLATFORM.register(BuiltInRegistries.CREATIVE_MODE_TAB, ModCreativeTabs.TAB_ID, creativeTab);
            }
        });
        System.out.println("[Buildscape Forge 1.21.1] Successfully registered " + modBlocks.size() + " blocks, " + modItems.size() + " items, " + modBlockEntities.size() + " block entities, " + modEntities.size() + " entities, " + modSounds.size() + " sounds, and " + modMenus.size() + " menus.");
    }
}
