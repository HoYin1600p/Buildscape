package net.minecraft.client.renderer.item;

import java.util.ArrayList;
import java.util.List;

public class TrackingItemStackRenderState extends ItemStackRenderState {
   private final List modelIdentityElements = new ArrayList();

   public void appendModelIdentityElement(final Object element) {
      this.modelIdentityElements.add(element);
   }

   public Object getModelIdentity() {
      return this.modelIdentityElements;
   }
}
