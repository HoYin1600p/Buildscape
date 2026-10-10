package com.kingodogo.buildscape.adapter.v26x;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Consumer;

public final class CreativeTabBridge {
    private CreativeTabBridge() {}

    public static CreativeModeTab.DisplayItemsGenerator createGenerator(Consumer<Consumer<ItemStack>> consumer) {
        return (CreativeModeTab.DisplayItemsGenerator) Proxy.newProxyInstance(
                CreativeModeTab.class.getClassLoader(),
                new Class<?>[]{CreativeModeTab.DisplayItemsGenerator.class},
                (proxy, method, args) -> {
                    if ("accept".equals(method.getName()) && args.length == 2) {
                        Object output = args[1];
                        Method acceptMethod = output.getClass().getMethod("accept", ItemStack.class);
                        consumer.accept(stack -> {
                            try {
                                acceptMethod.invoke(output, stack);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                    return null;
                }
        );
    }
}
