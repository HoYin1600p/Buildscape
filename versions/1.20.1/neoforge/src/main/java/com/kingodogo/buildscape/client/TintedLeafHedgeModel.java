package com.kingodogo.buildscape.client;

import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;

/** Preserve loader model behaviour while supplying the foliage tint index. */
public final class TintedLeafHedgeModel extends BakedModelWrapper<BakedModel> {
    public TintedLeafHedgeModel(BakedModel original) { super(original); }

    private static List<BakedQuad> tint(List<BakedQuad> quads) {
        return quads.stream().map(quad -> quad.getTintIndex() == -1
                ? new BakedQuad(quad.getVertices().clone(), 0, quad.getDirection(), quad.getSprite(), quad.isShade())
                : quad).toList();
    }

    @Override public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random) {
        return tint(super.getQuads(state, side, random));
    }

    @Override public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random,
            ModelData data, RenderType renderType) {
        return tint(super.getQuads(state, side, random, data, renderType));
    }

    @Override public List<BakedModel> getRenderPasses(net.minecraft.world.item.ItemStack stack, boolean fabulous) {
        return super.getRenderPasses(stack, fabulous).stream()
                .map(model -> (BakedModel) new TintedLeafHedgeModel(model)).toList();
    }

    @Override public BakedModel applyTransform(net.minecraft.world.item.ItemDisplayContext context,
            com.mojang.blaze3d.vertex.PoseStack pose, boolean leftHand) {
        BakedModel transformed = super.applyTransform(context, pose, leftHand);
        return transformed == originalModel ? this : new TintedLeafHedgeModel(transformed);
    }
}

