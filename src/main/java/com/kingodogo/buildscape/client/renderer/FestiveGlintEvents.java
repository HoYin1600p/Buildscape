package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.util.FestiveGlintHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayDeque;
import java.util.Deque;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, value = Dist.CLIENT)
public final class FestiveGlintEvents {

    private static final ThreadLocal<Deque<ItemStack>> STACK_TRACKER = ThreadLocal.withInitial(ArrayDeque::new);

    private FestiveGlintEvents() {}

    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            STACK_TRACKER.get().clear();
        }
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
