package com.kingodogo.buildscape.registry;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.item.ModCreativeTabs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Map;

public class NeoForgeRegistryAdapter extends BaseRegistryAdapter {
    private static IEventBus savedEventBus;

    public static void setEventBus(IEventBus eventBus) {
        savedEventBus = eventBus;
    }

    public NeoForgeRegistryAdapter() {
        super(BuildscapeCommon.MOD_ID);
    }

    @Override
    public void init() {
        if (savedEventBus != null) {
            savedEventBus.addListener(RegisterEvent.class, this::onRegister);
        } else {
            super.init();
        }
    }

    @SuppressWarnings("unchecked")
    private void onRegister(RegisterEvent event) {
        super.init();

        event.register(Registries.BLOCK, helper -> {
            for (Map.Entry<String, net.minecraft.world.level.block.Block> entry : modBlocks.entrySet()) {
                helper.register(ResourceLocation.fromNamespaceAndPath(BuildscapeCommon.MOD_ID, entry.getKey()), entry.getValue());
            }
        });

        event.register(Registries.ITEM, helper -> {
            for (Map.Entry<String, net.minecraft.world.item.Item> entry : modItems.entrySet()) {
                helper.register(ResourceLocation.fromNamespaceAndPath(BuildscapeCommon.MOD_ID, entry.getKey()), entry.getValue());
            }
        });

        event.register(Registries.BLOCK_ENTITY_TYPE, helper -> {
            for (Map.Entry<String, net.minecraft.world.level.block.entity.BlockEntityType<?>> entry : modBlockEntities.entrySet()) {
                helper.register(ResourceLocation.fromNamespaceAndPath(BuildscapeCommon.MOD_ID, entry.getKey()), entry.getValue());
            }
        });

        event.register(Registries.ENTITY_TYPE, helper -> {
            for (Map.Entry<String, net.minecraft.world.entity.EntityType<?>> entry : modEntities.entrySet()) {
                helper.register(ResourceLocation.fromNamespaceAndPath(BuildscapeCommon.MOD_ID, entry.getKey()), entry.getValue());
            }
        });

        event.register(Registries.SOUND_EVENT, helper -> {
            for (Map.Entry<String, net.minecraft.sounds.SoundEvent> entry : modSounds.entrySet()) {
                helper.register(ResourceLocation.fromNamespaceAndPath(BuildscapeCommon.MOD_ID, entry.getKey()), entry.getValue());
            }
        });

        event.register(Registries.RECIPE_SERIALIZER, helper -> {
            for (Map.Entry<String, Object> entry : modRecipeSerializers.entrySet()) {
                helper.register(ResourceLocation.fromNamespaceAndPath(BuildscapeCommon.MOD_ID, entry.getKey()), (net.minecraft.world.item.crafting.RecipeSerializer<?>) entry.getValue());
            }
        });

        event.register(Registries.MENU, helper -> {
            for (Map.Entry<String, net.minecraft.world.inventory.MenuType<?>> entry : modMenus.entrySet()) {
                helper.register(ResourceLocation.fromNamespaceAndPath(BuildscapeCommon.MOD_ID, entry.getKey()), entry.getValue());
            }
        });

        event.register(Registries.CREATIVE_MODE_TAB, helper -> {
            if (creativeTab != null) {
                helper.register(ResourceLocation.fromNamespaceAndPath(BuildscapeCommon.MOD_ID, ModCreativeTabs.TAB_ID.getPath()), creativeTab);
            }
        });
    }
}
