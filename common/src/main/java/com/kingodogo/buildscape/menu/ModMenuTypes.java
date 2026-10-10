package com.kingodogo.buildscape.menu;

import net.minecraft.world.inventory.MenuType;
public final class ModMenuTypes {
    private ModMenuTypes() {}
    public static final MenuType<BuildersWorkbenchMenu> BUILDERS_WORKBENCH_MENU =
            MenuTypeBridge.create(BuildersWorkbenchMenu::new);
    public static final MenuType<BuildersPouchMenu> BUILDERS_POUCH_MENU =
            MenuTypeBridge.create(BuildersPouchMenu::new);
}
