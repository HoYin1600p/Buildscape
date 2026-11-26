package com.kingodogo.buildscape.client;

import com.google.common.hash.Hashing;
import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.adapter.client.HazeRenderSupport;
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

/** Enables alpha-coverage trimming only for the unmodified 1.18.2 particle shader. */
@Mod.EventBusSubscriber(modid = BuildscapeCommon.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class HazeShaderSupport {
    private static volatile boolean standardShader;
    private HazeShaderSupport() { }

    public static void install() { HazeRenderSupport.installAlphaCutoffProvider(HazeShaderSupport::alphaCutoff); }

    @SubscribeEvent
    public static void onShadersReload(RegisterShadersEvent event) {
        standardShader = !ModList.get().isLoaded("oculus") && !ModList.get().isLoaded("iris")
                && matches(event.getResourceManager(), "particle.fsh", "7d07dcf396ae055be1c70bc7fdbfd12d2ac22bf145ce9dca40cb3f1038258b59")
                && matches(event.getResourceManager(), "particle.vsh", "01d17c5af9afc75c4211338f08dafc87086226fd03967cd0a173753b7e9b1731");
    }

    private static int alphaCutoff(double alpha) {
        ShaderInstance shader = RenderSystem.getShader();
        if (!standardShader || shader == null || shader.getClass() != ShaderInstance.class || !"particle".equals(shader.getName())
                || !"particle".equals(shader.getVertexProgram().getName()) || !"particle".equals(shader.getFragmentProgram().getName())) return 0;
        if (!(alpha > 0)) return 255;
        return Math.max(0, Math.min(255, (int) Math.floor(25.5 / alpha) - 1));
    }

    private static boolean matches(ResourceManager resources, String file, String hash) {
        var resOpt = resources.getResource(new ResourceLocation("minecraft", "shaders/core/" + file));
        if (resOpt.isEmpty()) return false;
        try (InputStream stream = resOpt.get().open()) {
            return Hashing.sha256().hashBytes(stream.readAllBytes()).toString().equals(hash);
        } catch (IOException exception) { return false; }
    }
}
