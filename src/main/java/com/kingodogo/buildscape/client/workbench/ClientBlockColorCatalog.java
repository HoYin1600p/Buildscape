package com.kingodogo.buildscape.client.workbench;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.ColorGradientSolver;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public final class ClientBlockColorCatalog {
    private static boolean ready;
    private static boolean building;
    private static int generation;

    private ClientBlockColorCatalog() {
    }

    public static synchronized void invalidate() {
        ready = false;
        generation++;
    }

    public static synchronized int generation() {
        return generation;
    }

    public static boolean ensureReady() {
        synchronized (ClientBlockColorCatalog.class) {
            if (ready) return true;
            if (building) return false;
            building = true;
        }

        long started = System.nanoTime();
        try {
            List<ColorGradientSolver.BlockColor> colors = new ArrayList<>();

            for (Item item : Services.PLATFORM.getAllItems()) {
                if (!ColorGradientSolver.isCandidateBlock(item)) continue;
                try {
                    Block block = ((BlockItem) item).getBlock();
                    BlockState state = block.defaultBlockState();
                    com.kingodogo.buildscape.platform.IPlatformAdapter.BlockColorSample sample = Services.PLATFORM.sampleBlockColor(state);
                    int rgb;
                    int categories;
                    if (sample == null) {
                        rgb = fallbackColor(state);
                        categories = ColorGradientSolver.categoriesFor(item);
                    } else {
                        rgb = sample.rgb();
                        categories = categories(item, state, sample.transparent());
                    }
                    colors.add(new ColorGradientSolver.BlockColor(item,
                            rgb >> 16 & 255, rgb >> 8 & 255, rgb & 255, categories,
                            sample != null && sample.singleTexture()));
                } catch (RuntimeException error) {
                    BlockState state = ((BlockItem) item).getBlock().defaultBlockState();
                    int rgb = fallbackColor(state);
                    colors.add(new ColorGradientSolver.BlockColor(item,
                            rgb >> 16 & 255, rgb >> 8 & 255, rgb & 255,
                            ColorGradientSolver.categoriesFor(item)));
                }
            }

            ColorGradientSolver.replaceDynamicColors(colors);
            synchronized (ClientBlockColorCatalog.class) {
                ready = true;
                generation++;
            }
            long elapsedMs = (System.nanoTime() - started) / 1_000_000L;
            BuildscapeCommon.LOGGER.info("Builder's Workbench sampled {} block colors in {} ms",
                    colors.size(), elapsedMs);
            return true;
        } catch (Throwable error) {
            BuildscapeCommon.LOGGER.error("Builder's Workbench could not build its texture color catalog: {}", error.getMessage());
            return false;
        } finally {
            synchronized (ClientBlockColorCatalog.class) {
                building = false;
            }
        }
    }

    private static int fallbackColor(BlockState state) {
        try {
            int color = state.getMapColor(null, null).col;
            return color == 0 ? 0x808080 : color;
        } catch (Throwable ignored) {
            return 0x808080;
        }
    }

    private static int categories(Item item, BlockState state, boolean sampledTransparency) {
        boolean full;
        try {
            full = Block.isShapeFullBlock(state.getShape(net.minecraft.world.level.EmptyBlockGetter.INSTANCE, net.minecraft.core.BlockPos.ZERO));
        } catch (RuntimeException ignored) {
            full = false;
        }
        boolean transparent = sampledTransparency || Services.PLATFORM.isTranslucent(state);
        return ColorGradientSolver.categoriesFor(item, full, transparent);
    }
}
