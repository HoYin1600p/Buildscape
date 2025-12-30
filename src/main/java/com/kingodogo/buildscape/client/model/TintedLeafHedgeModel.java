package com.kingodogo.buildscape.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TintedLeafHedgeModel implements BakedModel {

    private final BakedModel originalModel;

    public TintedLeafHedgeModel(BakedModel originalModel) {
        this.originalModel = originalModel;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(
            @Nullable BlockState state,
            @Nullable Direction side,
            @NotNull Random rand
    ) {
        List<BakedQuad> originalQuads = originalModel.getQuads(state, side, rand);

        boolean needsModification = false;
        for (BakedQuad quad : originalQuads) {
            if (quad.getTintIndex() == -1) {
                needsModification = true;
                break;
            }
        }

        if (!needsModification) {
            return originalQuads;
        }

        List<BakedQuad> tintedQuads = new ArrayList<>();

        for (BakedQuad quad : originalQuads) {
            if (quad.getTintIndex() == -1) {
                net.minecraft.client.renderer.texture.TextureAtlasSprite sprite =
                        quad.getSprite();

                int[] vertexData = quad.getVertices().clone();

                BakedQuad tintedQuad = new BakedQuad(
                        vertexData,
                        0,
                        quad.getDirection(),
                        sprite,
                        quad.isShade()
                );
                tintedQuads.add(tintedQuad);
            } else {
                tintedQuads.add(quad);
            }
        }

        return tintedQuads;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return originalModel.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return originalModel.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return originalModel.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return originalModel.isCustomRenderer();
    }

    @Override
    public net.minecraft.client.renderer.texture.TextureAtlasSprite getParticleIcon() {
        return originalModel.getParticleIcon();
    }

    @Override
    public net.minecraft.client.renderer.block.model.ItemTransforms getTransforms() {
        return originalModel.getTransforms();
    }

    @Override
    public net.minecraft.client.renderer.block.model.ItemOverrides getOverrides() {
        return originalModel.getOverrides();
    }
}
