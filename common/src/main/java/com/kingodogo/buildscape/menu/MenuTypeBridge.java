package com.kingodogo.buildscape.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
public final class MenuTypeBridge {
    private static final Constructor<?> CONSTRUCTOR;
    private static final Class<?> SUPPLIER_CLASS;
    private static final Object FEATURE_FLAGS;

    static {
        try {
            Class<?> supplierClass = null;
            for (Class<?> inner : MenuType.class.getDeclaredClasses()) {
                if (inner.getSimpleName().equals("MenuSupplier")) {
                    supplierClass = inner;
                    break;
                }
            }
            SUPPLIER_CLASS = supplierClass;

            Constructor<?> ctor = null;
            Object flags = null;
            try {
                Class<?> flagsClass = Class.forName("net.minecraft.world.flag.FeatureFlagSet");
                Class<?> featuresClass = Class.forName("net.minecraft.world.flag.FeatureFlags");
                Field vanillaSetField = featuresClass.getField("VANILLA_SET");
                flags = vanillaSetField.get(null);
                ctor = MenuType.class.getDeclaredConstructor(SUPPLIER_CLASS, flagsClass);
            } catch (ClassNotFoundException ignored) {
                ctor = MenuType.class.getDeclaredConstructor(SUPPLIER_CLASS);
            }
            CONSTRUCTOR = ctor;
            CONSTRUCTOR.setAccessible(true);
            FEATURE_FLAGS = flags;
        } catch (Exception e) {
            throw new RuntimeException("Failed to access MenuType constructor", e);
        }
    }
    public interface MenuFactory<T extends AbstractContainerMenu> {
        T create(int windowId, Inventory playerInv);
    }
    @SuppressWarnings("unchecked")
    public static <T extends AbstractContainerMenu> MenuType<T> create(MenuFactory<T> factory) {
        try {
            Object supplierProxy = Proxy.newProxyInstance(
                    MenuType.class.getClassLoader(),
                    new Class<?>[]{SUPPLIER_CLASS},
                    (proxy, method, args) -> {
                        if ("create".equals(method.getName())) {
                            return factory.create((Integer) args[0], (Inventory) args[1]);
                        }
                        return null;
                    });
            if (FEATURE_FLAGS != null) {
                return (MenuType<T>) CONSTRUCTOR.newInstance(supplierProxy, FEATURE_FLAGS);
            } else {
                return (MenuType<T>) CONSTRUCTOR.newInstance(supplierProxy);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create MenuType", e);
        }
    }
}
