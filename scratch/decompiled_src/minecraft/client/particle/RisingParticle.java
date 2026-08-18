package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public abstract class RisingParticle extends SingleQuadParticle {
   protected RisingParticle(final ClientLevel level, final double x, final double y, final double z, final double xd, final double yd, final double zd, final TextureAtlasSprite sprite) {
      super(level, x, y, z, xd, yd, zd, sprite);
      this.friction = 0.96F;
      this.xd = this.xd * (double)0.01F + xd;
      this.yd = this.yd * (double)0.01F + yd;
      this.zd = this.zd * (double)0.01F + zd;
      this.x += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
      this.y += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
      this.z += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
      this.lifetime = (int)(8.0D / ((double)this.random.nextFloat() * 0.8D + 0.2D)) + 4;
   }
}
