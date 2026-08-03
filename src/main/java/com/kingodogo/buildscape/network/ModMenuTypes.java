package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.CONTAINERS, BuildScape.MODID);

    public static final RegistryObject<MenuType<BuildersWorkbenchMenu>> BUILDERS_WORKBENCH_MENU =
            MENUS.register("builders_workbench", () -> IForgeMenuType.create(BuildersWorkbenchMenu::new));

    public static void register(net.minecraftforge.eventbus.api.IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
