package net.minecraft.client.renderer.blockentity.state;

import java.util.ArrayList;
import java.util.List;

public class BeaconRenderState extends BlockEntityRenderState {
   public float animationTime;
   public float beamRadiusScale;
   public List sections = new ArrayList();

   public static record Section(int color, int height) {
   }
}
