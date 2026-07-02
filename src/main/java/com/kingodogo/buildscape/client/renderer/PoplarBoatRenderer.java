package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.entity.PoplarBoatEntity;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;

public class PoplarBoatRenderer extends BoatRenderer {

    private static final ResourceLocation POPLAR_BOAT_TEXTURE =
            new ResourceLocation(BuildScape.MODID, "textures/entity/boat/poplar.png");

    private final BoatModel boatModel;

    public PoplarBoatRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.boatModel = new BoatModel(
                context.bakeLayer(ModelLayers.createBoatModelName(Boat.Type.OAK))
        );
    }

    @Override
    public Pair<ResourceLocation, BoatModel> getModelWithLocation(Boat boat) {
        return Pair.of(POPLAR_BOAT_TEXTURE, this.boatModel);
    }

    @Override
    @Deprecated
    public ResourceLocation getTextureLocation(Boat boat) {
        return POPLAR_BOAT_TEXTURE;
    }
}
