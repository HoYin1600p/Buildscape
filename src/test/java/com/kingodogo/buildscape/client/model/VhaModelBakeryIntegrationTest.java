package com.kingodogo.buildscape.client.model;

import com.kingodogo.buildscape.client.performance.VhaModelBakeryIntegration;
import com.kingodogo.buildscape.resources.bundle.ResourceBundleContainer;
import com.kingodogo.buildscape.resources.bundle.adapter.ResourcePackPrecedenceTracker;
import com.kingodogo.buildscape.resources.bundle.compiler.BlockStateCompiler;
import com.kingodogo.buildscape.resources.bundle.compiler.ModelCompiler;
import com.kingodogo.buildscape.resources.bundle.runtime.BundledResourceRegistry;
import com.kingodogo.buildscape.testutil.RegistryTestSupport;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.state.StateDefinition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class VhaModelBakeryIntegrationTest {
    @Test
    void missingVhaKeepsTheStandaloneFallback() {
        try {
            Class.forName("dev.hoyin1600p.vhaccelerator.api.client.ModelBakeryEvents");
            Assumptions.abort("Standalone check requires VHA to be absent");
        } catch (ClassNotFoundException absent) {
            VhaModelBakeryIntegration.register();
            ResourceLocation id = new ResourceLocation("buildscape", "standalone_probe");
            assertNull(VhaModelBakeryIntegration.model(new Object(), id));
            assertNull(VhaModelBakeryIntegration.resources(new Object(), null, id));
            assertFalse(VhaModelBakeryIntegration.hasBlockState(new Object(), id));
        }
    }

    @Test
    void registersBinaryModelsAndBlockstatesAndForwardsTheExistingLookup() throws Exception {
        Class<?> api;
        try {
            api = Class.forName("dev.hoyin1600p.vhaccelerator.api.client.ModelBakeryEvents");
        } catch (ClassNotFoundException absent) {
            Assumptions.abort("Run with VHA's model API on the integration-test classpath");
            return;
        }
        RegistryTestSupport.bootstrap();
        var registry = BundledResourceRegistry.getInstance();
        registry.reset();
        try {
            var writer = new ResourceBundleContainer.Writer();
            writer.addModel(ModelCompiler.compile("buildscape:block/vha_probe",
                    "{\"parent\":\"minecraft:block/cube_all\",\"textures\":{\"all\":\"minecraft:block/stone\"}}"));
            writer.addBlockState(BlockStateCompiler.compile("buildscape:vha_probe",
                    "{\"variants\":{\"\":{\"model\":\"buildscape:block/vha_probe\"}}}"));
            registry.initializeFromStream(new ByteArrayInputStream(writer.write(new byte[32])));
            ClassLoader loader = getClass().getClassLoader();
            PackResources pack = (PackResources) Proxy.newProxyInstance(loader, new Class<?>[]{PackResources.class},
                    (proxy, method, args) -> switch (method.getName()) {
                        case "getName" -> "bundle";
                        case "hasResource" -> ResourcePackPrecedenceTracker.CLIENT_BUNDLE_LOCATION.equals(args[1]);
                        default -> null;
                    });
            ResourceManager resources = (ResourceManager) Proxy.newProxyInstance(loader,
                    new Class<?>[]{ResourceManager.class},
                    (proxy, method, args) -> switch (method.getName()) {
                        case "listPacks" -> List.of(pack).stream();
                        case "hasResource" -> false;
                        case "listResources" -> List.of();
                        case "getResources", "getResource" -> throw new FileNotFoundException(args[0].toString());
                        default -> null;
                    });
            VhaModelBakeryIntegration.register();
            assertEquals(Boolean.TRUE, api.getMethod("isRegistered", String.class).invoke(null, "buildscape"));
            Object session = api.getMethod("begin", ResourceManager.class).invoke(null, resources);
            Class<?> access = Class.forName(api.getName() + "$Access");
            AtomicInteger forwarded = new AtomicInteger();
            Object bakery = Proxy.newProxyInstance(loader, new Class<?>[]{access}, (proxy, method, args) -> {
                if (method.getName().equals("vhaccelerator$modelSession")) {
                    return session;
                }
                if (method.getName().equals("vhaccelerator$resources")) {
                    forwarded.incrementAndGet();
                    return List.of();
                }
                return null;
            });
            ResourceLocation model = new ResourceLocation("buildscape", "block/vha_probe");
            assertNotNull(VhaModelBakeryIntegration.model(bakery, model));
            ResourceLocation block = new ResourceLocation("buildscape", "vha_probe");
            registry.setOverrides(Set.of(), Set.of(block.toString()));
            assertEquals(Boolean.TRUE,
                    session.getClass().getMethod("prepareBlockState", ResourceLocation.class, StateDefinition.class)
                            .invoke(session, block, null));
            assertTrue(VhaModelBakeryIntegration.hasBlockState(bakery, block));
            assertEquals(List.of(), VhaModelBakeryIntegration.resources(bakery, resources,
                    new ResourceLocation("buildscape", "blockstates/vha_probe.json")));
            assertEquals(1, forwarded.get());
            session.getClass().getMethod("afterBaking").invoke(session);
            assertFalse(VhaModelBakeryIntegration.hasBlockState(bakery, block));
        } finally {
            registry.reset();
        }
    }
}
