package com.kingodogo.buildscape.client;

import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

/** Vanilla adapter quads retain the original model's geometry and gain foliage tint. */
public final class TintedLeafHedgeModel implements BakedModel {
    private final BakedModel original;
    public TintedLeafHedgeModel(BakedModel original) { this.original = original; }
    @Override public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random) {
        return original.getQuads(state, side, random).stream().map(quad -> quad.getTintIndex() == -1
                ? new BakedQuad(quad.getVertices().clone(), 0, quad.getDirection(), quad.getSprite(), quad.isShade())
                : quad).toList();
    }
    @Override public boolean useAmbientOcclusion() { return original.useAmbientOcclusion(); }
    @Override public boolean isGui3d() { return original.isGui3d(); }
    @Override public boolean usesBlockLight() { return original.usesBlockLight(); }
    @Override public boolean isCustomRenderer() { return original.isCustomRenderer(); }
    @Override public TextureAtlasSprite getParticleIcon() { return original.getParticleIcon(); }
    @Override public ItemTransforms getTransforms() { return original.getTransforms(); }
    @Override public ItemOverrides getOverrides() { return original.getOverrides(); }
}
