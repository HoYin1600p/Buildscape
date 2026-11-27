package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.client.particle.HazeMeshSink;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(QuadParticleRenderState.class)
abstract class HazeParticleRenderStateMixin implements HazeMeshSink {
    @Unique private final List<Mesh> buildscape$hazeMeshes = new ArrayList<>();

    @Override public void buildscape$addHazeMesh(float[] vertices, int color, int light) {
        buildscape$hazeMeshes.add(new Mesh(vertices, color, light));
    }

    @Inject(method = "clear", at = @At("TAIL"))
    private void buildscape$clearHaze(CallbackInfo ci) { buildscape$hazeMeshes.clear(); }

    @Inject(method = "buildLayer", at = @At("TAIL"))
    private void buildscape$renderHaze(SingleQuadParticle.Layer layer, VertexConsumer consumer, CallbackInfo ci) {
        if (layer != SingleQuadParticle.Layer.TRANSLUCENT) return;
        for (Mesh mesh : buildscape$hazeMeshes) {
            float[] vertices = mesh.vertices();
            for (int i = 0; i < vertices.length; i += 5) {
                consumer.addVertex(vertices[i], vertices[i + 1], vertices[i + 2])
                        .setUv(vertices[i + 3], vertices[i + 4]).setColor(mesh.color()).setLight(mesh.light());
            }
        }
    }
}
