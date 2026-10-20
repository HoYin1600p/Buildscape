package com.kingodogo.buildscape.config;

import com.kingodogo.buildscape.testutil.BootstrappedGameTest;
import com.kingodogo.buildscape.world.ModGameRules;
import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.GameRules;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class GameRuleDefaultsRegressionTest extends BootstrappedGameTest {
    @Rule
    public TemporaryFolder temporary = new TemporaryFolder();

    @Test
    @SuppressWarnings("unchecked")
    public void configuredDefaultsApplyToNewWorldsAndSavedWorldRulesWin() throws Exception {
        Field instance = BuildscapeClientConfig.class.getDeclaredField("INSTANCE");
        instance.setAccessible(true);
        Object previousConfig = instance.get(null);
        Map<Field, Object> previousFields = new LinkedHashMap<>();
        for (Field field : ModGameRules.class.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
                field.setAccessible(true);
                previousFields.put(field, field.get(null));
            }
        }
        Field registry = null;
        for (Field field : GameRules.class.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && Map.class.isAssignableFrom(field.getType())) {
                registry = field;
            }
        }
        assertNotNull(registry);
        registry.setAccessible(true);
        Map<Object, Object> types = (Map<Object, Object>) registry.get(null);
        Map<Object, Object> previousTypes = new LinkedHashMap<>(types);
        Method factory = null;
        for (Method method : GameRules.BooleanValue.class.getDeclaredMethods()) {
            if (method.getReturnType() == GameRules.Type.class && method.getParameterCount() == 2
                    && method.getParameterTypes()[0] == boolean.class
                    && method.getParameterTypes()[1] == BiConsumer.class) {
                factory = method;
            }
        }
        assertNotNull(factory);
        factory.setAccessible(true);
        Method register = ModGameRules.class.getDeclaredMethod("register", Method.class);
        register.setAccessible(true);
        try {
            for (boolean cake : new boolean[]{false, true}) {
                for (boolean water : new boolean[]{false, true}) {
                    types.clear();
                    types.putAll(previousTypes);
                    Path root = temporary.newFolder().toPath();
                    Path file = root.resolve("buildscape/buildscape.cfg");
                    Files.createDirectories(file.getParent());
                    Files.writeString(file, "isCakeStack=" + cake + "\nisWaterbottleStack=" + water + "\n");
                    instance.set(null, new BuildscapeClientConfig(root));
                    register.invoke(null, factory);
                    GameRules newWorld = new GameRules();
                    assertEquals(cake, newWorld.getBoolean(ModGameRules.IS_CAKE_STACK));
                    assertEquals(water, newWorld.getBoolean(ModGameRules.IS_WATER_BOTTLE_STACK));
                    assertFalse(newWorld.getBoolean(ModGameRules.FAST_LEAF_DECAY));
                    assertFalse(newWorld.getBoolean(ModGameRules.DISABLE_ENDERMAN_GRIEFING));
                    assertFalse(newWorld.getBoolean(ModGameRules.DISABLE_CREEPER_GRIEFING));
                    assertFalse(newWorld.getBoolean(ModGameRules.DISABLE_GHAST_GRIEFING));
                    CompoundTag saved = new CompoundTag();
                    saved.putString("isCakeStack", Boolean.toString(!cake));
                    saved.putString("isWaterbottleStack", Boolean.toString(!water));
                    GameRules existingWorld = new GameRules(new Dynamic<>(NbtOps.INSTANCE, saved));
                    assertEquals(!cake, existingWorld.getBoolean(ModGameRules.IS_CAKE_STACK));
                    assertEquals(!water, existingWorld.getBoolean(ModGameRules.IS_WATER_BOTTLE_STACK));
                }
            }
        } finally {
            types.clear();
            types.putAll(previousTypes);
            instance.set(null, previousConfig);
            for (Map.Entry<Field, Object> entry : previousFields.entrySet()) {
                entry.getKey().set(null, entry.getValue());
            }
        }
    }
}
