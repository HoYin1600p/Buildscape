package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Arrays;
import javax.imageio.ImageIO;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DryFoliageColor {
    private static final int[] pixels = new int[65536];
    private static volatile boolean loaded = false;

    public static final ColorResolver DRY_FOLIAGE_RESOLVER = (biome, x, z) -> get(biome.getBaseTemperature(),
            biome.getDownfall());

    public static synchronized void init() {
        if (loaded) {
            return;
        }
        reload(Minecraft.getInstance().getResourceManager());
    }

    @SubscribeEvent
    public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((ResourceManagerReloadListener) resourceManager -> {
            reload(resourceManager);
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                mc.levelRenderer.allChanged();
            }
        });
    }

    private static synchronized void reload(ResourceManager resourceManager) {
        Arrays.fill(pixels, 0x867E36);
        try {
            ResourceLocation loc = new ResourceLocation("minecraft", "textures/colormap/dry_foliage.png");
            Resource resource = resourceManager.getResource(loc);
            try (InputStream stream = resource.getInputStream()) {
                BufferedImage image = ImageIO.read(stream);
                if (image != null) {
                    image.getRGB(0, 0, 256, 256, pixels, 0, 256);
                }
            }
        } catch (Exception e) {
            // missing resource, undecodable image or one smaller than 256x256; use the default colour
            Arrays.fill(pixels, 0x867E36);
        } finally {
            loaded = true;
        }
    }

    public static int get(double temperature, double humidity) {
        if (!loaded) {
            init();
        }
        humidity *= temperature;
        int i = (int) ((1.0 - temperature) * 255.0);
        int j = (int) ((1.0 - humidity) * 255.0);
        i = Math.max(0, Math.min(255, i));
        j = Math.max(0, Math.min(255, j));
        int index = j << 8 | i;
        return (index < 0 || index >= pixels.length) ? 0x867E36 : (pixels[index] & 0xFFFFFF);
    }

    public static int getDefaultColor() {
        return get(0.5, 1.0);
    }

    public static int getDryFoliageColor(BlockAndTintGetter level, BlockPos pos) {
        if (level == null || pos == null || !(level instanceof LevelReader levelReader)) {
            return getDefaultColor();
        }
        int r = 0, g = 0, b = 0;
        int count = 0;
        int xMin = pos.getX() - 1;
        int xMax = pos.getX() + 1;
        int zMin = pos.getZ() - 1;
        int zMax = pos.getZ() + 1;
        for (int x = xMin; x <= xMax; x++) {
            for (int z = zMin; z <= zMax; z++) {
                BlockPos curPos = new BlockPos(x, pos.getY(), z);
                Biome biome = levelReader.getBiome(curPos).value();
                int color = get(biome.getBaseTemperature(), biome.getDownfall());
                r += (color >> 16) & 0xFF;
                g += (color >> 8) & 0xFF;
                b += color & 0xFF;
                count++;
            }
        }
        return ((r / count) << 16) | ((g / count) << 8) | (b / count);
    }
}
