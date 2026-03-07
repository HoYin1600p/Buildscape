package com.kingodogo.buildscape.variantengine.client;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.variantengine.family.BlockFamily;
import com.kingodogo.buildscape.variantengine.registry.BlockRegistryScanner;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class VariantModelBakingManager {

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event) {
        BuildScape.LOGGER.info("VariantEngine: Dynamically baking models for variants...");
        Map<ResourceLocation, BakedModel> models = event.getModelRegistry();

        for (BlockFamily family : BlockRegistryScanner.getDetectedFamilies()) {
            Block base = family.getBaseBlock();
            ResourceLocation baseId = base.getRegistryName();
            if (baseId == null) continue;

            // Get the parent model (from which we extract textures)
            BakedModel parentModel = models.get(base.defaultBlockState().getBlock().getRegistryName());
            if (parentModel == null) continue;

            // Extract main texture (particle or first quad)
            TextureAtlasSprite sprite = extractMainSprite(parentModel);

            // Generate models for our registered variants
            family.getVariants().forEach((shape, variant) -> {
                ResourceLocation variantId = variant.getRegistryName();
                if (variantId != null && variantId.getNamespace().equals(BuildScape.MODID)) {
                    // It's a BuildScape generated block, bake its model
                    BakedModel variantModel = bakeModelForShape(shape, sprite, parentModel);
                    if (variantModel != null) {
                        models.put(variantId, variantModel);
                    }
                }
            });
        }
    }

    private static TextureAtlasSprite extractMainSprite(BakedModel model) {
        // Try particle first
        TextureAtlasSprite particle = model.getParticleIcon();
        if (particle != null && !particle.getName().getPath().contains("missingno")) return particle;

        // Otherwise, peek at quads
        for (Direction dir : Direction.values()) {
            List<BakedQuad> quads = model.getQuads(null, dir, new Random());
            if (!quads.isEmpty()) return quads.get(0).getSprite();
        }
        return particle;
    }

    private static BakedModel bakeModelForShape(com.kingodogo.buildscape.variantengine.builder.BlockShape shape, TextureAtlasSprite sprite, BakedModel parent) {
        // Here we would use a Template model or procedural quad generation
        // For the example, we return a delegating model that uses the extracted sprite
        return new VariantBakedModel(shape, sprite, parent);
    }

    // Internal class for the dynamic baked model
    private static class VariantBakedModel implements BakedModel {
        private final TextureAtlasSprite sprite;

        public VariantBakedModel(Object shape, TextureAtlasSprite sprite, BakedModel parent) {
            this.sprite = sprite;
        }

        // Custom implementation that renders the shape using the provided sprite
        // ... (Omitting full quad generation logic for brevity in example)
        @Override
        public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
            return List.of();
        }

        @Override
        public boolean useAmbientOcclusion() {
            return true;
        }

        @Override
        public boolean isGui3d() {
            return true;
        }

        @Override
        public boolean usesBlockLight() {
            return true;
        }

        @Override
        public boolean isCustomRenderer() {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleIcon() {
            return sprite;
        }

        @Override
        public net.minecraft.client.renderer.block.model.ItemOverrides getOverrides() {
            return net.minecraft.client.renderer.block.model.ItemOverrides.EMPTY;
        }

        @Override
        public net.minecraft.client.renderer.block.model.ItemTransforms getTransforms() {
            return net.minecraft.client.renderer.block.model.ItemTransforms.NO_TRANSFORMS;
        }
    }
}
