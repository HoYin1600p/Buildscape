package com.kingodogo.buildscape.adapter.v26x.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.resources.Identifier;
import org.joml.Vector3fc;

import java.util.function.Consumer;

/** Uses the same single chest mesh and standalone texture as the placed chest. */
public final class CopperChestSpecialRenderer implements NoDataSpecialModelRenderer {
    private final ChestModel model;
    private final Identifier texture;

    private CopperChestSpecialRenderer(ChestModel model, Identifier texture) {
        this.model = model;
        this.texture = texture;
    }

    @Override public void submit(PoseStack pose, SubmitNodeCollector collector, int light,
            int overlay, boolean foil, int outlineColor) {
        collector.submitModel(model, 0.0F, pose, texture, light, overlay, outlineColor, null);
    }

    @Override public void getExtents(Consumer<Vector3fc> output) {
        model.setupAnim(0.0F);
        model.root().getExtentsForGui(new PoseStack(), output);
    }

    public record Unbaked(Identifier texture) implements NoDataSpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = Identifier.CODEC.fieldOf("texture")
                .xmap(Unbaked::new, Unbaked::texture);

        @Override public MapCodec<Unbaked> type() { return MAP_CODEC; }

        @Override public CopperChestSpecialRenderer bake(BakingContext context) {
            return new CopperChestSpecialRenderer(
                    new ChestModel(context.entityModelSet().bakeLayer(ModelLayers.CHEST)), texture);
        }
    }
}
