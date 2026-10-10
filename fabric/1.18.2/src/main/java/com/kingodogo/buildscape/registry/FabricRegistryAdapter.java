package com.kingodogo.buildscape.registry;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.item.ModCreativeTabs;
import net.minecraft.core.Registry;

public class FabricRegistryAdapter extends BaseRegistryAdapter {
    public FabricRegistryAdapter() {
        super(BuildscapeCommon.MOD_ID);
    }

    @Override
    public void init() {
        super.init();
        registerTo(Registry.BLOCK, modBlocks);
        registerTo(Registry.ITEM, modItems);
        registerTo(Registry.BLOCK_ENTITY_TYPE, modBlockEntities);
        registerTo(Registry.ENTITY_TYPE, modEntities);
        registerTo(Registry.SOUND_EVENT, modSounds);
        registerTo(Registry.RECIPE_SERIALIZER, (java.util.Map) modRecipeSerializers);
        registerTo(Registry.MENU, modMenus);
        System.out.println("[Buildscape Fabric 1.18.2] Successfully registered " + modBlocks.size() + " blocks, " + modItems.size() + " items, " + modBlockEntities.size() + " block entities, " + modEntities.size() + " entities, " + modSounds.size() + " sounds, " + modRecipeSerializers.size() + " recipe serializers, and " + modMenus.size() + " menus.");
    }
}
