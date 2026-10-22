package com.kingodogo.buildscape.client.performance;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.resources.bundle.adapter.BlockStateAdapter;
import com.kingodogo.buildscape.resources.bundle.adapter.ModelAdapter;
import com.kingodogo.buildscape.resources.bundle.adapter.ResourcePackPrecedenceTracker;
import com.kingodogo.buildscape.resources.bundle.model.ModelIR;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

public final class VhaModelBakeryIntegration {
    public static final boolean ENABLE_VHA_NATIVE = Boolean.parseBoolean(
            System.getProperty("buildscape.vha.native", "false"));
    private static final String API = "dev.hoyin1600p.vhaccelerator.api.client.ModelBakeryEvents";
    private static final AtomicBoolean REPORTED_FAILURE = new AtomicBoolean();
    private static final ConcurrentMap<String, BlockModel> PREPARED_MODELS = new ConcurrentHashMap<>();
    private static volatile Method resourceLookup;
    private static volatile Method modelLookup;
    private static volatile Method blockStateLookup;
    private static volatile PackCache packCache;

    private VhaModelBakeryIntegration() {}

    public static BlockModel getPreparedModel(String id) {
        return PREPARED_MODELS.get(id);
    }

    public static void prepareModels(ResourceManager manager) {
        BundledResourceRegistry registry = BundledResourceRegistry.getInstance();
        registry.ensureInitialized();
        ResourcePackPrecedenceTracker.ensureInitialized(manager);
        bundlePack(manager);

        Map<String, ModelIR> models = registry.getModels();
        if (models.isEmpty()) {
            return;
        }

        List<Map.Entry<String, ModelIR>> entries = new ArrayList<>(models.entrySet());
        long start = System.nanoTime();
        BuildscapeStartupWork.forEachIndex(entries.size(), index -> {
            Map.Entry<String, ModelIR> entry = entries.get(index);
            String id = entry.getKey();
            if (!registry.isModelOverridden(id)) {
                BlockModel nativeModel = ModelAdapter.toBlockModel(entry.getValue());
                if (nativeModel != null) {
                    nativeModel.name = id;
                    PREPARED_MODELS.put(id, nativeModel);
                }
            }
        });
        long elapsed = (System.nanoTime() - start) / 1_000_000L;
        BuildScape.LOGGER.info("Prepared {} bundled models in parallel ({} ms)", PREPARED_MODELS.size(), elapsed);
    }

    public static void register() {
        if (!ENABLE_VHA_NATIVE) {
            BuildScape.LOGGER.info(
                    "Buildscape VHA native model provider is disabled (serving bundled resources via ResourceManager)");
            return;
        }
        if (BundledResourceRegistry.getInstance().getModels().isEmpty()) {
            return;
        }
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
                            packCache = null;
                            PREPARED_MODELS.clear();
                            prepareModels(manager);
                            return null;
                        }
                        BundledResourceRegistry registry = BundledResourceRegistry.getInstance();
                        if (method.getName().equals("model")) {
                            ResourceLocation id = (ResourceLocation) arguments[1];
                            String idStr = id.toString();
                            BlockModel nativeModel = PREPARED_MODELS.get(idStr);
                            if (nativeModel == null) {
                                var ir = registry.getModel(idStr);
                                if (ir == null) {
                                    return null;
                                }
                                long started = System.nanoTime();
                                nativeModel = ModelAdapter.toBlockModel(ir);
                                registry.recordNativeConversionNanos(System.nanoTime() - started);
                                if (nativeModel != null) {
                                    nativeModel.name = idStr;
                                    PREPARED_MODELS.put(idStr, nativeModel);
                                }
                            } else {
                                registry.recordServedModel(idStr);
                            }
                            if (nativeModel == null) {
                                return null;
                            }
                            String pack = bundlePack(manager);
                            if (pack == null) {
                                return null;
                            }
                            return model.newInstance(pack, nativeModel);
                        }
                        if (method.getName().equals("blockState")) {
                            ResourceLocation id = (ResourceLocation) arguments[1];
                            var ir = registry.getBlockStates().get(id.toString());
                            if (ir == null) {
                                return null;
                            }
                            String pack = bundlePack(manager);
                            if (pack == null) {
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
        // The pack list is fixed for one reload; scanning it on every model callback serialises behind VHA's lock.
        PackCache cached = packCache;
        if (cached != null && cached.manager() == resources) {
            return cached.pack();
        }
        List<PackResources> packs = resources.listPacks().toList();
        int index = ResourcePackPrecedenceTracker.findClientBundlePackIndex(packs);
        String pack = index < 0 ? null : packs.get(index).getName();
        packCache = new PackCache(resources, pack);
        return pack;
    }

    private record PackCache(ResourceManager manager, String pack) {
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
