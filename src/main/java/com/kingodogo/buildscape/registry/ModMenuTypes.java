package com.kingodogo.buildscape.registry;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.menu.BuildersPouchMenu;
import com.kingodogo.buildscape.menu.BuildersWorkbenchMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.CONTAINERS, BuildScape.MODID);

    public static final RegistryObject<MenuType<BuildersWorkbenchMenu>> BUILDERS_WORKBENCH_MENU =
            MENUS.register("builders_workbench", () -> IForgeMenuType.create(BuildersWorkbenchMenu::new));

    public static final RegistryObject<MenuType<BuildersPouchMenu>> BUILDERS_POUCH_MENU =
            MENUS.register("builders_pouch", () -> IForgeMenuType.create(BuildersPouchMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
