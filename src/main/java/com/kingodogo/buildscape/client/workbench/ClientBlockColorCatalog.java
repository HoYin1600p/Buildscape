package com.kingodogo.buildscape.client.workbench;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.util.ColorGradientSolver;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Lazily samples the baked block models that Minecraft is already using. The
 * catalog is rebuilt only after a resource reload and never exists on a
 * dedicated server.
 */
@OnlyIn(Dist.CLIENT)
public final class ClientBlockColorCatalog {
    private static final int MAX_SAMPLES_PER_AXIS = 32;
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
            Minecraft minecraft = Minecraft.getInstance();
            BlockColors blockColors = minecraft.getBlockColors();
            Map<SpriteKey, PixelSample> spriteCache = new HashMap<>();
            List<ColorGradientSolver.BlockColor> colors = new ArrayList<>();

            for (ResourceLocation id : Registry.ITEM.keySet()) {
                Item item = Registry.ITEM.get(id);
                if (!ColorGradientSolver.isCandidateBlock(item)) continue;
                try {
                    Block block = ((BlockItem) item).getBlock();
                    BlockState state = block.defaultBlockState();
                    BlockSample sample = sampleBlock(minecraft, blockColors, state, spriteCache);
                    int rgb;
                    int categories;
                    if (sample == null) {
                        rgb = fallbackColor(state);
                        categories = ColorGradientSolver.categoriesFor(item);
                    } else {
                        rgb = sample.rgb;
                        categories = categories(item, state, sample.transparent);
                    }
                    colors.add(new ColorGradientSolver.BlockColor(item,
                            rgb >> 16 & 255, rgb >> 8 & 255, rgb & 255, categories));
                } catch (RuntimeException error) {
                    BuildScape.LOGGER.debug("Builder's Workbench skipped color sampling for {}", id, error);
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
            BuildScape.LOGGER.info("Builder's Workbench sampled {} block colors from {} unique sprites in {} ms",
                    colors.size(), spriteCache.size(), elapsedMs);
            return true;
        } catch (Throwable error) {
            BuildScape.LOGGER.error("Builder's Workbench could not build its texture color catalog", error);
            return false;
        } finally {
            synchronized (ClientBlockColorCatalog.class) {
                building = false;
            }
        }
    }

    private static BlockSample sampleBlock(Minecraft minecraft, BlockColors blockColors, BlockState state,
                                           Map<SpriteKey, PixelSample> spriteCache) {
        BakedModel model;
        try {
            model = minecraft.getBlockRenderer().getBlockModel(state);
        } catch (RuntimeException ignored) {
            return null;
        }

        BlockAccumulator accumulator = new BlockAccumulator();
        for (Direction side : Direction.values()) {
            sampleQuads(model, state, side, blockColors, spriteCache, accumulator);
        }
        sampleQuads(model, state, null, blockColors, spriteCache, accumulator);

        if (accumulator.weight == 0) {
            TextureAtlasSprite particle = model.getParticleIcon(EmptyModelData.INSTANCE);
            if (particle != null && !MissingTextureAtlasSprite.getLocation().equals(particle.getName())) {
                PixelSample sample = spriteCache.computeIfAbsent(new SpriteKey(particle.getName(), 0xFFFFFF),
                        ignored -> sampleSprite(particle, 0xFFFFFF));
                accumulator.add(sample);
            }
        }
        return accumulator.finish();
    }

    private static void sampleQuads(BakedModel model, BlockState state, Direction side, BlockColors blockColors,
                                    Map<SpriteKey, PixelSample> spriteCache, BlockAccumulator accumulator) {
        List<BakedQuad> quads;
        try {
            quads = model.getQuads(state, side, new Random(42L), EmptyModelData.INSTANCE);
        } catch (RuntimeException ignored) {
            return;
        }

        Set<SpriteKey> seenOnFace = new HashSet<>();
        for (BakedQuad quad : quads) {
            TextureAtlasSprite sprite = quad.getSprite();
            if (sprite == null || MissingTextureAtlasSprite.getLocation().equals(sprite.getName())) continue;
            int tint = 0xFFFFFF;
            if (quad.isTinted()) {
                try {
                    int resolved = blockColors.getColor(state, null, null, quad.getTintIndex());
                    if (resolved != -1) tint = resolved & 0xFFFFFF;
                } catch (RuntimeException ignored) {
                    // Some modded tint handlers require a live level and position.
                }
            }
            SpriteKey key = new SpriteKey(sprite.getName(), tint);
            if (!seenOnFace.add(key)) continue;
            int tintColor = tint;
            PixelSample sample = spriteCache.computeIfAbsent(key, ignored -> sampleSprite(sprite, tintColor));
            accumulator.add(sample);
        }
    }

    private static PixelSample sampleSprite(TextureAtlasSprite sprite, int tint) {
        int width = sprite.getWidth();
        int height = sprite.getHeight();
        int stepX = Math.max(1, (width + MAX_SAMPLES_PER_AXIS - 1) / MAX_SAMPLES_PER_AXIS);
        int stepY = Math.max(1, (height + MAX_SAMPLES_PER_AXIS - 1) / MAX_SAMPLES_PER_AXIS);
        int[] frames = sprite.getUniqueFrames().toArray();
        if (frames.length == 0) frames = new int[]{0};

        int tintRed = tint >> 16 & 255;
        int tintGreen = tint >> 8 & 255;
        int tintBlue = tint & 255;
        double red = 0;
        double green = 0;
        double blue = 0;
        double weight = 0;
        boolean transparent = false;

        for (int frame : frames) {
            for (int y = 0; y < height; y += stepY) {
                for (int x = 0; x < width; x += stepX) {
                    int pixel = sprite.getPixelRGBA(frame, x, y);
                    int alpha = NativeImage.getA(pixel);
                    if (alpha < 250) transparent = true;
                    if (alpha < 16) continue;
                    double alphaWeight = alpha / 255.0;
                    int pixelRed = NativeImage.getR(pixel) * tintRed / 255;
                    int pixelGreen = NativeImage.getG(pixel) * tintGreen / 255;
                    int pixelBlue = NativeImage.getB(pixel) * tintBlue / 255;
                    red += srgbToLinear(pixelRed / 255.0) * alphaWeight;
                    green += srgbToLinear(pixelGreen / 255.0) * alphaWeight;
                    blue += srgbToLinear(pixelBlue / 255.0) * alphaWeight;
                    weight += alphaWeight;
                }
            }
        }
        if (weight == 0) return PixelSample.EMPTY;
        return new PixelSample(red / weight, green / weight, blue / weight, transparent, true);
    }

    private static int categories(Item item, BlockState state, boolean sampledTransparency) {
        boolean full;
        try {
            full = Block.isShapeFullBlock(state.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO));
        } catch (RuntimeException ignored) {
            full = false;
        }
        boolean transparent = sampledTransparency
                || ItemBlockRenderTypes.canRenderInLayer(state, RenderType.translucent());
        return ColorGradientSolver.categoriesFor(item, full, transparent);
    }

    private static int fallbackColor(BlockState state) {
        try {
            int color = state.getMapColor(null, null).col;
            return color == 0 ? 0x808080 : color;
        } catch (RuntimeException ignored) {
            return 0x808080;
        }
    }

    private static double srgbToLinear(double value) {
        return value <= 0.04045 ? value / 12.92 : Math.pow((value + 0.055) / 1.055, 2.4);
    }

    private static int linearToSrgb(double value) {
        double clamped = Math.max(0.0, Math.min(1.0, value));
        double srgb = clamped <= 0.0031308 ? clamped * 12.92 : 1.055 * Math.pow(clamped, 1.0 / 2.4) - 0.055;
        return Math.max(0, Math.min(255, (int) Math.round(srgb * 255.0)));
    }

    private record SpriteKey(ResourceLocation texture, int tint) {
    }

    private record PixelSample(double red, double green, double blue, boolean transparent, boolean valid) {
        private static final PixelSample EMPTY = new PixelSample(0, 0, 0, true, false);
    }

    private record BlockSample(int rgb, boolean transparent) {
    }

    private static final class BlockAccumulator {
        private double red;
        private double green;
        private double blue;
        private int weight;
        private boolean transparent;

        private void add(PixelSample sample) {
            if (sample == null || !sample.valid) return;
            red += sample.red;
            green += sample.green;
            blue += sample.blue;
            transparent |= sample.transparent;
            weight++;
        }

        private BlockSample finish() {
            if (weight == 0) return null;
            int r = linearToSrgb(red / weight);
            int g = linearToSrgb(green / weight);
            int b = linearToSrgb(blue / weight);
            return new BlockSample(r << 16 | g << 8 | b, transparent);
        }
    }
}
