package com.kingodogo.buildscape.mixinsupport;

import net.minecraft.world.item.ItemStack;
import java.util.Map;
import java.util.WeakHashMap;

/** Carries the rocket item through synchronous client particle creation. */
public final class FireworkContext {
    private static final ThreadLocal<ItemStack> CURRENT = new ThreadLocal<>();
    private static final Map<Object, ItemStack> STARTERS = new WeakHashMap<>();
    private FireworkContext() {}

    public static void begin(ItemStack stack) { CURRENT.set(stack); }
    public static void end() { CURRENT.remove(); }
    public static synchronized void remember(Object starter) {
        ItemStack stack = CURRENT.get();
        if (stack != null && !stack.isEmpty()) STARTERS.put(starter, stack.copy());
    }
    public static synchronized ItemStack take(Object starter) {
        ItemStack stack = STARTERS.remove(starter);
        return stack == null ? ItemStack.EMPTY : stack;
    }
}
