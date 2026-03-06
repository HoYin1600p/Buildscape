package com.kingodogo.buildscape.compat.vertical;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class ParticleDelegatingBakedModel implements BakedModel {
    private final BakedModel wrapped;
    private final TextureAtlasSprite particleTexture;

    public ParticleDelegatingBakedModel(BakedModel wrapped, TextureAtlasSprite particleTexture) {
        this.wrapped = wrapped;
        this.particleTexture = particleTexture;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, Random random) {
        return wrapped.getQuads(state, direction, random);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return wrapped.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return wrapped.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return wrapped.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return wrapped.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return particleTexture != null ? particleTexture : wrapped.getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
        return wrapped.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides() {
        return wrapped.getOverrides();
    }
}
