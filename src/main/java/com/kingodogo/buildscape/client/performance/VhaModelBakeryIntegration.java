package com.kingodogo.buildscape.client.performance;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.resources.bundle.adapter.BlockStateAdapter;
import com.kingodogo.buildscape.resources.bundle.adapter.ModelAdapter;
import com.kingodogo.buildscape.resources.bundle.adapter.ResourcePackPrecedenceTracker;
import com.kingodogo.buildscape.resources.bundle.runtime.BundledResourceRegistry;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class VhaModelBakeryIntegration {
    private static final String API = "dev.hoyin1600p.vhaccelerator.api.client.ModelBakeryEvents";
    private static final AtomicBoolean REPORTED_FAILURE = new AtomicBoolean();
    private static volatile Method resourceLookup;
    private static volatile Method modelLookup;
    private static volatile Method blockStateLookup;

    private VhaModelBakeryIntegration() {}

    public static void register() {
        try {
            ClassLoader loader = VhaModelBakeryIntegration.class.getClassLoader();
            Class<?> api = Class.forName(API, false, loader);
            if (api.getField("API_VERSION").getInt(null) != 1) {
                BuildScape.LOGGER.warn("Unsupported VHA model API; retaining Buildscape's native loader");
                return;
            }
            Class<?> listenerType = Class.forName(API + "$Listener", false, loader);
            Constructor<?> model = Class.forName(API + "$Model", false, loader)
                    .getConstructor(String.class, BlockModel.class);
            Constructor<?> blockState = Class.forName(API + "$BlockStateDefinition", false, loader)
                    .getConstructor(String.class, BlockModelDefinition.class);
            Method resources = api.getMethod("resources", Object.class, ResourceManager.class, ResourceLocation.class);
            Method models = api.getMethod("model", Object.class, ResourceLocation.class);
            Method states = api.getMethod("hasBlockState", Object.class, ResourceLocation.class);
            Object listener = Proxy.newProxyInstance(loader, new Class<?>[]{listenerType},
                    (proxy, method, arguments) -> {
                        if (method.getDeclaringClass() == Object.class) {
                            return switch (method.getName()) {
                                case "equals" -> proxy == arguments[0];
                                case "hashCode" -> System.identityHashCode(proxy);
                                case "toString" -> "Buildscape native model provider";
                                default -> null;
                            };
                        }
                        ResourceManager manager = (ResourceManager) arguments[0];
                        if (method.getName().equals("beforePreparation")) {
                            BundledResourceRegistry.getInstance().ensureInitialized();
                            ResourcePackPrecedenceTracker.ensureInitialized(manager);
                            return null;
                        }
                        String pack = bundlePack(manager);
                        if (pack == null) {
                            return null;
                        }
                        BundledResourceRegistry registry = BundledResourceRegistry.getInstance();
                        if (method.getName().equals("model")) {
                            ResourceLocation id = (ResourceLocation) arguments[1];
                            var ir = registry.getModels().get(id.toString());
                            if (ir == null) {
                                return null;
                            }
                            long started = System.nanoTime();
                            BlockModel nativeModel = ModelAdapter.toBlockModel(ir);
                            registry.recordNativeConversionNanos(System.nanoTime() - started);
                            return nativeModel == null ? null : model.newInstance(pack, nativeModel);
                        }
                        if (method.getName().equals("blockState")) {
                            ResourceLocation id = (ResourceLocation) arguments[1];
                            var ir = registry.getBlockStates().get(id.toString());
                            if (ir == null) {
                                return null;
                            }
                            @SuppressWarnings("unchecked")
                            StateDefinition<Block, BlockState> definition = (StateDefinition<Block, BlockState>) arguments[2];
                            long started = System.nanoTime();
                            BlockModelDefinition nativeState = BlockStateAdapter.toBlockModelDefinition(ir, definition);
                            registry.recordNativeConversionNanos(System.nanoTime() - started);
                            return nativeState == null ? null : blockState.newInstance(pack, nativeState);
                        }
                        return null;
                    });
            Object registration = api.getMethod("register", String.class, int.class, listenerType)
                    .invoke(null, BuildScape.MODID, 1, listener);
            if (registration instanceof Enum<?> result && result.name().equals("REGISTERED")) {
                resourceLookup = resources;
                modelLookup = models;
                blockStateLookup = states;
                BuildScape.LOGGER.info("Registered Buildscape binary bundles with VHA model API v1");
            } else {
                BuildScape.LOGGER.warn("VHA model registration returned {}; retaining native fallback", registration);
            }
        } catch (ClassNotFoundException absent) {
            // VHA is optional; no compile or runtime dependency is required.
        } catch (ReflectiveOperationException | RuntimeException | LinkageError failure) {
            report(failure);
        }
    }

    private static String bundlePack(ResourceManager resources) {
        List<PackResources> packs = resources.listPacks().toList();
        int index = ResourcePackPrecedenceTracker.findClientBundlePackIndex(packs);
        return index < 0 ? null : packs.get(index).getName();
    }

    public static BlockModel model(Object bakery, ResourceLocation location) {
        Object result = invoke(modelLookup, bakery, location);
        return result instanceof BlockModel model ? model : null;
    }

    @SuppressWarnings("unchecked")
    public static List<Resource> resources(Object bakery, ResourceManager manager, ResourceLocation location) {
        Object result = invoke(resourceLookup, bakery, manager, location);
        return result instanceof List<?> list ? (List<Resource>) list : null;
    }

    public static boolean hasBlockState(Object bakery, ResourceLocation location) {
        return Boolean.TRUE.equals(invoke(blockStateLookup, bakery, location));
    }

    private static Object invoke(Method method, Object... arguments) {
        if (method == null) {
            return null;
        }
        try {
            return method.invoke(null, arguments);
        } catch (ReflectiveOperationException | RuntimeException | LinkageError failure) {
            Throwable cause = failure instanceof InvocationTargetException invocation ? invocation.getCause() : failure;
            if (cause instanceof VirtualMachineError fatal) {
                throw fatal;
            }
            if (cause instanceof ThreadDeath fatal) {
                throw fatal;
            }
            if (!(cause instanceof java.io.IOException)) {
                report(cause);
            }
            return null;
        }
    }

    private static void report(Throwable failure) {
        if (REPORTED_FAILURE.compareAndSet(false, true)) {
            BuildScape.LOGGER.warn("VHA integration failed; retaining Buildscape's native fallback", failure);
        }
    }
}
