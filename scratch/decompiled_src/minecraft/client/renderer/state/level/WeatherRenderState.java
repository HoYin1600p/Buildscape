package net.minecraft.client.renderer.state.level;

import java.util.ArrayList;
import java.util.List;

public class WeatherRenderState {
   public final List rainColumns = new ArrayList();
   public final List snowColumns = new ArrayList();
   public float intensity;
   public int radius;

   public void reset() {
      this.rainColumns.clear();
      this.snowColumns.clear();
      this.intensity = 0.0F;
      this.radius = 0;
   }
}
