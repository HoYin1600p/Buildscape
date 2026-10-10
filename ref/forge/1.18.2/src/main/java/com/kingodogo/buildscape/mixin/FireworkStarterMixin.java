package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.firework.CustomFireworkRenderer;
import com.kingodogo.buildscape.firework.CustomFireworkShape;
import com.kingodogo.buildscape.firework.CustomFireworkShapeRegistry;
import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(FireworkParticles.Starter.class)
public abstract class FireworkStarterMixin extends Particle {

    @Shadow
    private ListTag explosions;

    protected FireworkStarterMixin(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
    }

    @Shadow
    private void createParticle(double x, double y, double z, double vx, double vy, double vz, int[] colors, int[] fadeColors, boolean trail, boolean flicker) {
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void buildscape$handleCustomFireworkShapes(CallbackInfo ci) {
        if (this.age == 0 && this.explosions != null && !this.explosions.isEmpty()) {
            ListTag vanillaExplosions = new ListTag();

            for (int i = 0; i < this.explosions.size(); ++i) {
                CompoundTag tag = this.explosions.getCompound(i);
                byte type = tag.getByte("Type");

                if (CustomFireworkShapeRegistry.isCustomShape(type)) {
                    Optional<CustomFireworkShape> shapeOpt = CustomFireworkShapeRegistry.getByNumericId(type);
                    if (shapeOpt.isPresent()) {
                        CustomFireworkShape shape = shapeOpt.get();
                        boolean trail = tag.getBoolean("Trail");
                        boolean flicker = tag.getBoolean("Flicker");
                        int[] colors = tag.getIntArray("Colors");
                        int[] fadeColors = tag.getIntArray("FadeColors");

                        float yaw = 0.0F;
                        if (tag.contains("ShotYaw")) {
                            yaw = tag.getFloat("ShotYaw");
                        } else if (Math.hypot(this.xd, this.zd) > 0.05) {
                            yaw = (float) (net.minecraft.util.Mth.atan2(this.xd, this.zd) * (180.0F / (float) Math.PI));
                        } else if (net.minecraft.client.Minecraft.getInstance().player != null) {
                            yaw = net.minecraft.client.Minecraft.getInstance().player.getYRot();
                        }

                        this.setBoundingBox(this.getBoundingBox().inflate(120.0D, 120.0D, 120.0D));

                        CustomFireworkRenderer.renderExplosion(
                                shape,
                                this.x, this.y, this.z,
                                colors, fadeColors,
                                trail, flicker,
                                yaw,
                                this::createParticle
                        );
                    }
                } else {
                    vanillaExplosions.add(tag);
                }
            }

            this.explosions = vanillaExplosions;
        }
    }
}
