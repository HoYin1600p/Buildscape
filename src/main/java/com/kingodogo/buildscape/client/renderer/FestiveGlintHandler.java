package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.util.FestiveGlintHelper;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayDeque;
import java.util.Deque;

public final class FestiveGlintHandler {

    private static final ThreadLocal<Deque<ItemStack>> STACK_TRACKER = ThreadLocal.withInitial(ArrayDeque::new);

    private FestiveGlintHandler() {
    }

    public static void push(ItemStack stack) {
        if (stack != null && !stack.isEmpty()) {
            STACK_TRACKER.get().push(stack);
        } else {
            STACK_TRACKER.get().push(ItemStack.EMPTY);
        }
    }

    public static void pop() {
        Deque<ItemStack> deque = STACK_TRACKER.get();
        if (!deque.isEmpty()) {
            deque.pop();
        }
    }

    public static ItemStack getCurrent() {
        Deque<ItemStack> deque = STACK_TRACKER.get();
        return deque.isEmpty() ? ItemStack.EMPTY : deque.peek();
    }

    public static boolean isCurrentFestive() {
        return FestiveGlintHelper.hasFestiveGlint(getCurrent());
    }
}
