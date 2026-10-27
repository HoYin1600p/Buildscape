package com.kingodogo.buildscape.client.particle;

import com.google.common.hash.Hashing;
import com.kingodogo.buildscape.BuildScape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;
import java.io.InputStream;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class HazeShaderSupport {
    private static volatile boolean standardShader;

    private HazeShaderSupport() {}

    @SubscribeEvent
    public static void onShadersReload(RegisterShadersEvent event) {
        // The alpha cutoff is valid only for the unmodified 1.18.2 particle programs.
        standardShader = !ModList.get().isLoaded("oculus") && !ModList.get().isLoaded("iris")
                && matches(event.getResourceManager(), "particle.fsh",
                        "7d07dcf396ae055be1c70bc7fdbfd12d2ac22bf145ce9dca40cb3f1038258b59")
                && matches(event.getResourceManager(), "particle.vsh",
                        "01d17c5af9afc75c4211338f08dafc87086226fd03967cd0a173753b7e9b1731");
        BuildScape.LOGGER.info("Haze alpha coverage culling: {}",
                standardShader ? "standard particle program" : "conservative fallback");
    }

    static int alphaCutoff(float alpha) {
        ShaderInstance shader = RenderSystem.getShader();
        if (!standardShader || shader == null || shader.getClass() != ShaderInstance.class
                || !"particle".equals(shader.getName())
                || !"particle".equals(shader.getVertexProgram().getName())
                || !"particle".equals(shader.getFragmentProgram().getName())) {
            return 0;
        }
        return cutoffForAlpha(alpha * RenderSystem.getShaderColor()[3]);
    }

    static int cutoffForAlpha(float alpha) {
        // Keep a whole alpha step below the cutoff for rounding and normalized vertex colours.
        if (!(alpha > 0)) {
            return 255;
        }
        return Math.max(0, Math.min(255, (int) Math.floor(25.5 / alpha) - 1));
    }

    private static boolean matches(ResourceManager resources, String file, String hash) {
        try (InputStream stream = resources.getResource(new ResourceLocation("minecraft", "shaders/core/" + file))
                .getInputStream()) {
            return Hashing.sha256().hashBytes(stream.readAllBytes()).toString().equals(hash);
        } catch (IOException exception) {
            return false;
        }
    }
}
