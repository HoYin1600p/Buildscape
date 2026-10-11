package com.kingodogo.buildscape.adapter.v26x.client;

import com.kingodogo.buildscape.BuildscapeCommon;
import java.lang.reflect.Field;
import java.util.List;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;

/** Reference foliage and water colours for resource-defined tinted block and item models. */
public final class FoliageColors {
    private FoliageColors() {}

    private static BlockTintSource source(String path) {
        if (path.equals("cascade_block") || path.equals("cascade_block_no_mist")) return new BlockTintSource() {
            public int color(BlockState state) { return 0xFF3F76E4; }
            public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                return level == null || pos == null ? color(state)
                        : 0xFF000000 | BiomeColors.getAverageWaterColor(level, pos);
            }
        };
        String wood;
        if (path.endsWith("_leaf_hedge")) wood = path.substring(0, path.length() - "_leaf_hedge".length());
        else if (path.endsWith("_leaf_layers")) wood = path.substring(0, path.length() - "_leaf_layers".length());
        else return null;
        return switch (wood) {
            case "oak", "jungle", "acacia", "dark_oak" -> foliage(0x48B518);
            case "mangrove" -> foliage(0x92C648);
            case "spruce" -> BlockTintSources.constant(0xFF619961);
            case "birch" -> BlockTintSources.constant(0xFF80A755);
            default -> BlockTintSources.constant(-1);
        };
    }

    private static BlockTintSource foliage(int fallback) {
        return new BlockTintSource() {
            public int color(BlockState state) { return 0xFF000000 | fallback; }
            public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                return level == null || pos == null ? color(state)
                        : 0xFF000000 | BiomeColors.getAverageFoliageColor(level, pos);
            }
        };
    }

    public static void register(BlockColors colors) {
        register(colors::register);
    }

    /** Loader-neutral form: the sink may be vanilla BlockColors or a loader registry (Fabric API defers it). */
    public static void register(BlockTintSink sink) {
        for (var block : BuiltInRegistries.BLOCK) {
            Identifier id = BuiltInRegistries.BLOCK.getKey(block);
            if (!id.getNamespace().equals("buildscape")) continue;
            BlockTintSource tint = source(id.getPath());
            if (tint != null) sink.register(List.of(tint), block);
        }
    }

    @FunctionalInterface
    public interface BlockTintSink {
        void register(List<BlockTintSource> sources, net.minecraft.world.level.block.Block... blocks);
    }

    /** Model-bake callbacks supply item colours without changing packaged JSON resources. */
    public static ItemModel tintItem(Identifier id, ItemModel original) {
        if (!id.getNamespace().equals("buildscape")) return original;
        BlockTintSource tint = source(id.getPath());
        if (tint == null) return original;
        return (state, stack, resolver, display, level, owner, seed) -> {
            if (ItemLayers.COUNT == null || ItemLayers.LAYERS == null) {
                original.update(state, stack, resolver, display, level, owner, seed);
                return;
            }
            int first;
            try { first = ItemLayers.COUNT.getInt(state); }
            catch (ReflectiveOperationException | RuntimeException exception) {
                BuildscapeCommon.LOGGER.warn("Cannot inspect foliage item render layers", exception);
                original.update(state, stack, resolver, display, level, owner, seed);
                return;
            }
            original.update(state, stack, resolver, display, level, owner, seed);
            try {
                int color = tint.color(null);
                var layers = (ItemStackRenderState.LayerRenderState[]) ItemLayers.LAYERS.get(state);
                for (int index = first, count = ItemLayers.COUNT.getInt(state); index < count; index++) {
                    var tints = layers[index].tintLayers();
                    if (tints.isEmpty()) tints.add(color); else tints.set(0, color);
                }
                state.appendModelIdentityElement(color);
            } catch (ReflectiveOperationException | RuntimeException exception) {
                BuildscapeCommon.LOGGER.warn("Cannot apply foliage item tint", exception);
            }
        };
    }

    // 26.2 exposes tintLayers on each layer, but no public iterator over an extracted item.
    private static final class ItemLayers {
        static final Field COUNT = field("activeLayerCount");
        static final Field LAYERS = field("layers");
        private static Field field(String name) {
            try {
                Field field = ItemStackRenderState.class.getDeclaredField(name);
                field.setAccessible(true);
                return field;
            } catch (ReflectiveOperationException | RuntimeException exception) {
                BuildscapeCommon.LOGGER.error("Cannot access foliage item render state", exception);
                return null;
            }
        }
    }
}
