package com.kingodogo.buildscape.client;

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
@SuppressWarnings("PMD.AvoidAccessibilityAlteration")
public final class FoliageColors {
    private FoliageColors() {}

    private static BlockTintSource source(String path) {
        if (path.equals("glow_lights")) return new BlockTintSource() {
            public int color(BlockState state) { return -1; }
            public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                if (level == null || pos == null
                        || !(level.getBlockEntity(pos) instanceof com.kingodogo.buildscape.block.GlowLightsBlockEntity lights)) return -1;
                return GlowLightTint.color(pos.hashCode() ^ state.hashCode(), lights.getDyeColors());
            }
        };
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

    private static BlockTintSource petalGrass() {
        return new BlockTintSource() {
            public int color(BlockState state) { return 0xff7fa832; }
            public int colorInWorld(BlockState state, BlockAndTintGetter view, BlockPos pos) {
                var level = net.minecraft.client.Minecraft.getInstance().level;
                return level == null || view == null || pos == null ? color(state)
                        : 0xff000000 | level.getBiome(pos).value().getGrassColor(pos.getX(), pos.getZ());
            }
        };
    }

    private static BlockTintSource stringColor(net.minecraft.world.level.block.state.properties.EnumProperty<
            com.kingodogo.buildscape.block.OrnamentBlock.StringColor> property) {
        return new BlockTintSource() {
            public int color(BlockState state) {
                return state != null && state.hasProperty(property)
                        ? 0xff000000 | state.getValue(property).getColor() : -1;
            }
            public java.util.Set<net.minecraft.world.level.block.state.properties.Property<?>> relevantProperties() {
                return java.util.Set.of(property);
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
            if (block instanceof com.kingodogo.buildscape.block.OrnamentBlock) {
                sink.register(List.of(stringColor(com.kingodogo.buildscape.block.OrnamentBlock.STRING_COLOR)), block);
                continue;
            }
            if (block instanceof com.kingodogo.buildscape.block.StringLightBlock) {
                sink.register(List.of(stringColor(com.kingodogo.buildscape.block.StringLightBlock.STRING_COLOR)), block);
                continue;
            }
            // Match vanilla 26.2 BlockColors, including wildflowers' untinted flower layer.
            // Short/tall dry grass, firefly bush and cactus flower have no vanilla tint.
            List<BlockTintSource> potSources = switch (id.getPath()) {
                case "clover", "red_petal", "blue_petal", "orange_petal", "pink_petal", "purple_petal" ->
                        List.of(BlockTintSources.constant(-1), petalGrass());
                case "potted_bush" -> List.of(BlockTintSources.grass());
                case "potted_leaf_litter" -> List.of(BlockTintSources.dryFoliage());
                case "potted_wildflowers" -> List.of(BlockTintSources.constant(-1), BlockTintSources.grass());
                default -> null;
            };
            if (potSources != null) {
                sink.register(potSources, block);
                continue;
            }
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
        if (id.getPath().equals("glow_lights")) return original;
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
