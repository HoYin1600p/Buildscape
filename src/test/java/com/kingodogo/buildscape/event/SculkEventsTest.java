package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.feature.sculk.SculkBreakExperience;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * SculkEvents is registered by instance ({@code MinecraftForge.EVENT_BUS.register(new SculkEvents())}), so its
 * handlers are instance methods. The break handler needs a live player and block state, which a plain unit test
 * cannot build; these tests pin the handler wiring and the experience decision the break handler composes.
 */
public class SculkEventsTest {

    private static Map<String, Method> handlers() {
        Map<String, Method> found = new TreeMap<>();
        for (Method method : SculkEvents.class.getDeclaredMethods()) {
            if (method.isAnnotationPresent(SubscribeEvent.class)) {
                found.put(method.getName(), method);
            }
        }
        return found;
    }

    @Test
    public void handlersAreWiredToTheExpectedEvents() {
        Map<String, Method> handlers = handlers();
        assertEquals(3, handlers.size());
        assertEquals(LivingDeathEvent.class, handlers.get("onSculkDeath").getParameterTypes()[0]);
        assertEquals(LivingExperienceDropEvent.class, handlers.get("onSculkExperienceDrop").getParameterTypes()[0]);
        assertEquals(BlockEvent.BreakEvent.class, handlers.get("onSculkBlockBreak").getParameterTypes()[0]);
        for (Method method : handlers.values()) {
            assertEquals(method.getName(), 1, method.getParameterCount());
            assertTrue(method.getName(), Modifier.isPublic(method.getModifiers()));
        }
    }

    @Test
    public void deathAndExperienceHandlersRunLastBreakHandlerKeepsNormalPriority() {
        Map<String, Method> handlers = handlers();
        assertEquals(EventPriority.LOWEST, handlers.get("onSculkDeath").getAnnotation(SubscribeEvent.class).priority());
        assertEquals(EventPriority.LOWEST,
                handlers.get("onSculkExperienceDrop").getAnnotation(SubscribeEvent.class).priority());
        assertEquals(EventPriority.NORMAL,
                handlers.get("onSculkBlockBreak").getAnnotation(SubscribeEvent.class).priority());
    }

    @Test
    public void classIsRegisteredByInstanceNotByAnnotation() {
        // Handlers are instance methods; an @EventBusSubscriber would try to register them statically and fail.
        assertNull(SculkEvents.class.getAnnotation(Mod.EventBusSubscriber.class));
        for (Method method : handlers().values()) {
            assertFalse(method.getName(), Modifier.isStatic(method.getModifiers()));
        }
    }

    /** Mirrors the decision in onSculkBlockBreak: forBlock, skip when negative, then withTool. */
    private static int dropFor(String path, int silkTouch) {
        int experience = SculkBreakExperience.forBlock(path);
        return experience < 0 ? -1 : SculkBreakExperience.withTool(experience, silkTouch);
    }

    @Test
    public void breakDecisionTable() {
        assertEquals(1, dropFor("sculk", 0));
        assertEquals(0, dropFor("sculk", 1));
        assertEquals(0, dropFor("sculk_vein", 0));
        assertEquals(5, dropFor("sculk_catalyst", 0));
        assertEquals(0, dropFor("sculk_catalyst", 3));
        assertEquals(-1, dropFor("stone", 0));
        assertEquals(-1, dropFor(null, 1));
    }
}
