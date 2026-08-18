package net.minecraft.client.renderer.blockentity.state;

import java.util.ArrayList;
import java.util.List;

public class TestInstanceRenderState extends BlockEntityRenderState {
   public BeaconRenderState beaconRenderState;
   public BlockEntityWithBoundingBoxRenderState blockEntityWithBoundingBoxRenderState;
   public final List errorMarkers = new ArrayList();
}
