package com.kingodogo.buildscape.menu;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public final class MenuTypeBridge {
    private MenuTypeBridge() {}

    @FunctionalInterface
    public interface MenuFactory<T extends AbstractContainerMenu> {
        T create(int windowId, Inventory playerInv);
    }

    public static <T extends AbstractContainerMenu> MenuType<T> create(MenuFactory<T> factory) {
        var result = new java.util.concurrent.atomic.AtomicReference<MenuType<T>>();
        Services.PLATFORM.wrapRegistryAction(() -> result.set(Services.PLATFORM.createMenuType(factory)));
        return result.get();
    }
}
