package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class TrackingEmitter extends NoRenderParticle {
   private final Entity entity;
   private int life;
   private final int lifeTime;
   private final ParticleOptions particleType;

   public TrackingEmitter(final ClientLevel level, final Entity entity, final ParticleOptions particleType) {
      this(level, entity, particleType, 3);
   }

   public TrackingEmitter(final ClientLevel level, final Entity entity, final ParticleOptions particleType, final int lifeTime) {
      this(level, entity, particleType, lifeTime, entity.getDeltaMovement());
   }

   private TrackingEmitter(final ClientLevel level, final Entity entity, final ParticleOptions particleType, final int lifeTime, final Vec3 movement) {
      super(level, entity.getX(), entity.getY(0.5D), entity.getZ(), movement.x, movement.y, movement.z);
      this.entity = entity;
      this.lifeTime = lifeTime;
      this.particleType = particleType;
      this.tick();
   }

   public void tick() {
      for(int i = 0; i < 16; ++i) {
         double xa = (double)(this.random.nextFloat() * 2.0F - 1.0F);
         double ya = (double)(this.random.nextFloat() * 2.0F - 1.0F);
         double za = (double)(this.random.nextFloat() * 2.0F - 1.0F);
         if (!(xa * xa + ya * ya + za * za > 1.0D)) {
            double x = this.entity.getX(xa / 4.0D);
            double y = this.entity.getY(0.5D + ya / 4.0D);
            double z = this.entity.getZ(za / 4.0D);
            this.level.addParticle(this.particleType, x, y, z, xa, ya + 0.2D, za);
         }
      }

      ++this.life;
      if (this.life >= this.lifeTime) {
         this.remove();
      }

   }
}
