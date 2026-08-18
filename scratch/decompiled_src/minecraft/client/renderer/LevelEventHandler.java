package net.minecraft.client.renderer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.PowerParticleOption;
import net.minecraft.core.particles.SculkChargeParticleOptions;
import net.minecraft.core.particles.ShriekParticleOption;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.SpellParticleOption;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class LevelEventHandler {
   private final Minecraft minecraft;
   private final ClientLevel level;
   private final Map playingJukeboxSongs = new HashMap();

   public LevelEventHandler(final Minecraft minecraft, final ClientLevel level) {
      this.minecraft = minecraft;
      this.level = level;
   }

   public void globalLevelEvent(final @LevelEvent.Value int type, final BlockPos pos, final int data) {
      switch (type) {
         case 1023:
         case 1028:
         case 1038:
            Camera camera = this.minecraft.gameRenderer.mainCamera();
            if (camera.isInitialized()) {
               Vec3 directionToEvent = Vec3.atCenterOf(pos).subtract(camera.position()).normalize();
               Vec3 soundPos = camera.position().add(directionToEvent.scale(2.0D));
               if (type == 1023) {
                  this.level.playLocalSound(soundPos.x, soundPos.y, soundPos.z, SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.0F, 1.0F, false);
               } else if (type == 1038) {
                  this.level.playLocalSound(soundPos.x, soundPos.y, soundPos.z, SoundEvents.END_PORTAL_SPAWN, SoundSource.HOSTILE, 1.0F, 1.0F, false);
               } else {
                  this.level.playLocalSound(soundPos.x, soundPos.y, soundPos.z, SoundEvents.ENDER_DRAGON_DEATH, SoundSource.HOSTILE, 5.0F, 1.0F, false);
               }
            }
         default:
      }
   }

   public void levelEvent(final @LevelEvent.Value int eventType, final BlockPos pos, final int data) {
      RandomSource random = this.level.getRandom();
      switch (eventType) {
         case 1000:
            this.level.playLocalSound(pos, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            break;
         case 1001:
            this.level.playLocalSound(pos, SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 1.0F, 1.2F, false);
            break;
         case 1002:
            this.level.playLocalSound(pos, SoundEvents.DISPENSER_LAUNCH, SoundSource.BLOCKS, 1.0F, 1.2F, false);
            break;
         case 1004:
            this.level.playLocalSound(pos, SoundEvents.FIREWORK_ROCKET_SHOOT, SoundSource.NEUTRAL, 1.0F, 1.2F, false);
            break;
         case 1009:
            if (data == 0) {
               this.level.playLocalSound(pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F, false);
            } else if (data == 1) {
               this.level.playLocalSound(pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 0.7F, 1.6F + (random.nextFloat() - random.nextFloat()) * 0.4F, false);
            }
            break;
         case 1010:
            this.level.registryAccess().lookupOrThrow(Registries.JUKEBOX_SONG).get(data).ifPresent((song) -> this.playJukeboxSong(song, pos));
            break;
         case 1011:
            this.stopJukeboxSongAndNotifyNearby(pos);
            break;
         case 1015:
            this.level.playLocalSound(pos, SoundEvents.GHAST_WARN, SoundSource.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1016:
            this.level.playLocalSound(pos, SoundEvents.GHAST_SHOOT, SoundSource.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1017:
            this.level.playLocalSound(pos, SoundEvents.ENDER_DRAGON_SHOOT, SoundSource.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1018:
            this.level.playLocalSound(pos, SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1019:
            this.level.playLocalSound(pos, SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR, SoundSource.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1020:
            this.level.playLocalSound(pos, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, SoundSource.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1021:
            this.level.playLocalSound(pos, SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, SoundSource.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1022:
            this.level.playLocalSound(pos, SoundEvents.WITHER_BREAK_BLOCK, SoundSource.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1024:
            this.level.playLocalSound(pos, SoundEvents.WITHER_SHOOT, SoundSource.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1025:
            this.level.playLocalSound(pos, SoundEvents.BAT_TAKEOFF, SoundSource.NEUTRAL, 0.05F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1026:
            this.level.playLocalSound(pos, SoundEvents.ZOMBIE_INFECT, SoundSource.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1027:
            this.level.playLocalSound(pos, SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1029:
            this.level.playLocalSound(pos, SoundEvents.ANVIL_DESTROY, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1030:
            this.level.playLocalSound(pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1031:
            this.level.playLocalSound(pos, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.3F, random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1032:
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forLocalAmbience(SoundEvents.PORTAL_TRAVEL, random.nextFloat() * 0.4F + 0.8F, 0.25F));
            break;
         case 1033:
            this.level.playLocalSound(pos, SoundEvents.CHORUS_FLOWER_GROW, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            break;
         case 1034:
            this.level.playLocalSound(pos, SoundEvents.CHORUS_FLOWER_DEATH, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            break;
         case 1035:
            this.level.playLocalSound(pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            break;
         case 1039:
            this.level.playLocalSound(pos, SoundEvents.PHANTOM_BITE, SoundSource.HOSTILE, 0.3F, random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1040:
            this.level.playLocalSound(pos, SoundEvents.ZOMBIE_CONVERTED_TO_DROWNED, SoundSource.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1041:
            this.level.playLocalSound(pos, SoundEvents.HUSK_CONVERTED_TO_ZOMBIE, SoundSource.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1042:
            this.level.playLocalSound(pos, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1043:
            this.level.playLocalSound(pos, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1044:
            this.level.playLocalSound(pos, SoundEvents.SMITHING_TABLE_USE, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1045:
            this.level.playLocalSound(pos, SoundEvents.POINTED_DRIPSTONE_LAND, SoundSource.BLOCKS, 2.0F, random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1046:
            this.level.playLocalSound(pos, SoundEvents.POINTED_DRIPSTONE_DRIP_LAVA_INTO_CAULDRON, SoundSource.BLOCKS, 2.0F, random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1047:
            this.level.playLocalSound(pos, SoundEvents.POINTED_DRIPSTONE_DRIP_WATER_INTO_CAULDRON, SoundSource.BLOCKS, 2.0F, random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1048:
            this.level.playLocalSound(pos, SoundEvents.SKELETON_CONVERTED_TO_STRAY, SoundSource.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1049:
            this.level.playLocalSound(pos, SoundEvents.CRAFTER_CRAFT, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            break;
         case 1050:
            this.level.playLocalSound(pos, SoundEvents.CRAFTER_FAIL, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            break;
         case 1051:
            this.level.playLocalSound(pos, SoundEvents.WIND_CHARGE_THROW, SoundSource.BLOCKS, 0.5F, 0.4F / (this.level.getRandom().nextFloat() * 0.4F + 0.8F), false);
            break;
         case 1052:
            this.level.playLocalSound(pos, SoundEvents.SULFUR_SPIKE_LAND, SoundSource.BLOCKS, 2.0F, random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1053:
         case 1054:
            this.level.playLocalSound(pos, SoundEvents.SPLASH_POTION_BREAK, SoundSource.NEUTRAL, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1500:
            ComposterBlock.handleFill(this.level, pos, data > 0);
            break;
         case 1501:
            this.level.playLocalSound(pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F, false);

            for(int i = 0; i < 8; ++i) {
               this.level.addParticle(ParticleTypes.LARGE_SMOKE, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + 1.2D, (double)pos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
            }
            break;
         case 1502:
            this.level.playLocalSound(pos, SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.BLOCKS, 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F, false);

            for(int i = 0; i < 5; ++i) {
               double x = (double)pos.getX() + random.nextDouble() * 0.6D + 0.2D;
               double y = (double)pos.getY() + random.nextDouble() * 0.6D + 0.2D;
               double z = (double)pos.getZ() + random.nextDouble() * 0.6D + 0.2D;
               this.level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
            }
            break;
         case 1503:
            this.level.playLocalSound(pos, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.0F, 1.0F, false);

            for(int i = 0; i < 16; ++i) {
               double x = (double)pos.getX() + (5.0D + random.nextDouble() * 6.0D) / 16.0D;
               double y = (double)pos.getY() + 0.8125D;
               double z = (double)pos.getZ() + (5.0D + random.nextDouble() * 6.0D) / 16.0D;
               this.level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
            }
            break;
         case 1504:
            PointedDripstoneBlock.spawnDripParticle(this.level, pos, this.level.getBlockState(pos));
            break;
         case 1505:
            BoneMealItem.addGrowthParticles(this.level, pos, data);
            this.level.playLocalSound(pos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            break;
         case 2000:
            this.shootParticles(data, pos, random, ParticleTypes.SMOKE);
            break;
         case 2001:
            BlockState blockState = Block.stateById(data);
            if (!blockState.isAir()) {
               SoundType soundType = blockState.getSoundType();
               this.level.playLocalSound(pos, soundType.getBreakSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F, false);
            }

            this.level.addDestroyBlockEffect(pos, blockState);
            break;
         case 2002:
            this.potionSplashParticles(ParticleTypes.EFFECT, pos, data, random);
            break;
         case 2003:
            double x = (double)pos.getX() + 0.5D;
            double y = (double)pos.getY();
            double z = (double)pos.getZ() + 0.5D;
            ItemParticleOption breakParticle = new ItemParticleOption(ParticleTypes.ITEM, Items.ENDER_EYE);

            for(int i = 0; i < 8; ++i) {
               this.level.addParticle(breakParticle, x, y, z, random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D);
            }

            for(double angle = 0.0D; angle < (Math.PI * 2D); angle += 0.15707963267948966D) {
               this.level.addParticle(ParticleTypes.PORTAL, x + Math.cos(angle) * 5.0D, y - 0.4D, z + Math.sin(angle) * 5.0D, Math.cos(angle) * -5.0D, 0.0D, Math.sin(angle) * -5.0D);
               this.level.addParticle(ParticleTypes.PORTAL, x + Math.cos(angle) * 5.0D, y - 0.4D, z + Math.sin(angle) * 5.0D, Math.cos(angle) * -7.0D, 0.0D, Math.sin(angle) * -7.0D);
            }
            break;
         case 2004:
            for(int i = 0; i < 20; ++i) {
               double x = (double)pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 2.0D;
               double y = (double)pos.getY() + 0.5D + (random.nextDouble() - 0.5D) * 2.0D;
               double z = (double)pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 2.0D;
               this.level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
               this.level.addParticle(ParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
            }
            break;
         case 2006:
            for(int i = 0; i < 200; ++i) {
               float dist = random.nextFloat() * 4.0F;
               float angle = random.nextFloat() * ((float)Math.PI * 2F);
               double velocityX = (double)(Mth.cos((double)angle) * dist);
               double velocityY = 0.01D + random.nextDouble() * 0.5D;
               double velocityZ = (double)(Mth.sin((double)angle) * dist);
               this.level.addParticle(PowerParticleOption.create(ParticleTypes.DRAGON_BREATH, dist), (double)pos.getX() + velocityX * 0.1D, (double)pos.getY() + 0.3D, (double)pos.getZ() + velocityZ * 0.1D, velocityX, velocityY, velocityZ);
            }

            if (data == 1) {
               this.level.playLocalSound(pos, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.HOSTILE, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
            }
            break;
         case 2007:
            this.potionSplashParticles(ParticleTypes.INSTANT_EFFECT, pos, data, random);
            break;
         case 2008:
            this.level.addParticle(ParticleTypes.EXPLOSION, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
            break;
         case 2009:
            for(int i = 0; i < 8; ++i) {
               this.level.addParticle(ParticleTypes.CLOUD, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + 1.2D, (double)pos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
            }
            break;
         case 2010:
            this.shootParticles(data, pos, random, ParticleTypes.WHITE_SMOKE);
            break;
         case 2011:
            ParticleUtils.spawnParticleInBlock(this.level, pos, data, ParticleTypes.HAPPY_VILLAGER);
            break;
         case 2012:
            ParticleUtils.spawnParticleInBlock(this.level, pos, data, ParticleTypes.HAPPY_VILLAGER);
            break;
         case 2013:
            ParticleUtils.spawnSmashAttackParticles(this.level, pos, data);
            break;
         case 2014:
            this.level.addDestroyBlockEffect(pos, Block.stateById(data));
            break;
         case 2015:
            int xDiff = (data >> 16 & 255) - 16;
            int yDiff = (data >> 8 & 255) - 8;
            int zDiff = (data & 255) - 16;
            BlockPos toPos = pos.offset(xDiff, yDiff, zDiff);

            for(int j = 0; j < 128; ++j) {
               double d = random.nextDouble();
               float xa = (random.nextFloat() - 0.5F) * 0.2F;
               float ya = (random.nextFloat() - 0.5F) * 0.2F;
               float za = (random.nextFloat() - 0.5F) * 0.2F;
               double x = Mth.lerp(d, (double)toPos.getX(), (double)pos.getX()) + (random.nextDouble() - 0.5D) + 0.5D;
               double y = Mth.lerp(d, (double)toPos.getY(), (double)pos.getY()) + random.nextDouble() - 0.5D;
               double z = Mth.lerp(d, (double)toPos.getZ(), (double)pos.getZ()) + (random.nextDouble() - 0.5D) + 0.5D;
               this.level.addParticle(ParticleTypes.PORTAL, x, y, z, (double)xa, (double)ya, (double)za);
            }
            break;
         case 3000:
            this.level.addAlwaysVisibleParticle(ParticleTypes.EXPLOSION_EMITTER, true, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
            this.level.playLocalSound(pos, SoundEvents.END_GATEWAY_SPAWN, SoundSource.BLOCKS, 10.0F, (1.0F + (random.nextFloat() - random.nextFloat()) * 0.2F) * 0.7F, false);
            break;
         case 3001:
            this.level.playLocalSound(pos, SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 64.0F, 0.8F + random.nextFloat() * 0.3F, false);
            break;
         case 3002:
            if (data >= 0 && data < Direction.Axis.VALUES.length) {
               ParticleUtils.spawnParticlesAlongAxis(Direction.Axis.VALUES[data], this.level, pos, 0.125D, ParticleTypes.ELECTRIC_SPARK, UniformInt.of(10, 19));
            } else {
               ParticleUtils.spawnParticlesOnBlockFaces(this.level, pos, ParticleTypes.ELECTRIC_SPARK, UniformInt.of(3, 5));
            }
            break;
         case 3003:
            ParticleUtils.spawnParticlesOnBlockFaces(this.level, pos, ParticleTypes.WAX_ON, UniformInt.of(3, 5));
            break;
         case 3004:
            ParticleUtils.spawnParticlesOnBlockFaces(this.level, pos, ParticleTypes.WAX_OFF, UniformInt.of(3, 5));
            break;
         case 3005:
            ParticleUtils.spawnParticlesOnBlockFaces(this.level, pos, ParticleTypes.SCRAPE, UniformInt.of(3, 5));
            break;
         case 3006:
            int count = data >> 6;
            if (count > 0) {
               if (random.nextFloat() < 0.3F + (float)count * 0.1F) {
                  float volume = 0.15F + 0.02F * (float)count * (float)count * random.nextFloat();
                  float pitch = 0.4F + 0.3F * (float)count * random.nextFloat();
                  this.level.playLocalSound(pos, SoundEvents.SCULK_BLOCK_CHARGE, SoundSource.BLOCKS, volume, pitch, false);
               }

               byte particleData = (byte)(data & 63);
               IntProvider repetition = UniformInt.of(0, count);
               float speedVar = 0.005F;
               Supplier speedSupplier = () -> new Vec3(Mth.nextDouble(random, (double)-0.005F, (double)0.005F), Mth.nextDouble(random, (double)-0.005F, (double)0.005F), Mth.nextDouble(random, (double)-0.005F, (double)0.005F));
               if (particleData == 0) {
                  for(Direction direction : Direction.values()) {
                     float fullBlockRotation = direction == Direction.DOWN ? (float)Math.PI : 0.0F;
                     double fullBlockFactor = direction.getAxis() == Direction.Axis.Y ? 0.65D : 0.57D;
                     ParticleUtils.spawnParticlesOnBlockFace(this.level, pos, new SculkChargeParticleOptions(fullBlockRotation), repetition, direction, speedSupplier, fullBlockFactor);
                  }
               } else {
                  for(Direction direction : MultifaceBlock.unpack(particleData)) {
                     float facesBlockRotation = direction == Direction.UP ? (float)Math.PI : 0.0F;
                     double facesBlockFactor = 0.35D;
                     ParticleUtils.spawnParticlesOnBlockFace(this.level, pos, new SculkChargeParticleOptions(facesBlockRotation), repetition, direction, speedSupplier, 0.35D);
                  }
               }
            } else {
               this.level.playLocalSound(pos, SoundEvents.SCULK_BLOCK_CHARGE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
               boolean isSolid = this.level.getBlockState(pos).isCollisionShapeFullBlock(this.level, pos);
               int particleCount = isSolid ? 40 : 20;
               float spread = isSolid ? 0.45F : 0.25F;
               float speed = 0.07F;

               for(int i = 0; i < particleCount; ++i) {
                  float velocityX = 2.0F * random.nextFloat() - 1.0F;
                  float velocityY = 2.0F * random.nextFloat() - 1.0F;
                  float velocityZ = 2.0F * random.nextFloat() - 1.0F;
                  this.level.addParticle(ParticleTypes.SCULK_CHARGE_POP, (double)pos.getX() + 0.5D + (double)(velocityX * spread), (double)pos.getY() + 0.5D + (double)(velocityY * spread), (double)pos.getZ() + 0.5D + (double)(velocityZ * spread), (double)(velocityX * 0.07F), (double)(velocityY * 0.07F), (double)(velocityZ * 0.07F));
               }
            }
            break;
         case 3007:
            for(int i = 0; i < 10; ++i) {
               this.level.addParticle(new ShriekParticleOption(i * 5), (double)pos.getX() + 0.5D, (double)pos.getY() + SculkShriekerBlock.TOP_Y, (double)pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
            }

            BlockState state = this.level.getBlockState(pos);
            boolean isWaterlogged = state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED);
            if (!isWaterlogged) {
               this.level.playLocalSound((double)pos.getX() + 0.5D, (double)pos.getY() + SculkShriekerBlock.TOP_Y, (double)pos.getZ() + 0.5D, SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.BLOCKS, 2.0F, 0.6F + random.nextFloat() * 0.4F, false);
            }
            break;
         case 3008:
            BlockState blockStateForBrushing = Block.stateById(data);
            Block angle = blockStateForBrushing.getBlock();
            if (angle instanceof BrushableBlock brushableBlock) {
               this.level.playLocalSound(pos, brushableBlock.getBrushCompletedSound(), SoundSource.PLAYERS, 1.0F, 1.0F, false);
            }

            this.level.addDestroyBlockEffect(pos, blockStateForBrushing);
            break;
         case 3009:
            ParticleUtils.spawnParticlesOnBlockFaces(this.level, pos, ParticleTypes.EGG_CRACK, UniformInt.of(3, 6));
            break;
         case 3011:
            TrialSpawner.addSpawnParticles(this.level, pos, random, TrialSpawner.FlameParticle.decode(data).particleType);
            break;
         case 3012:
            this.level.playLocalSound(pos, SoundEvents.TRIAL_SPAWNER_SPAWN_MOB, SoundSource.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, true);
            TrialSpawner.addSpawnParticles(this.level, pos, random, TrialSpawner.FlameParticle.decode(data).particleType);
            break;
         case 3013:
            this.level.playLocalSound(pos, SoundEvents.TRIAL_SPAWNER_DETECT_PLAYER, SoundSource.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, true);
            TrialSpawner.addDetectPlayerParticles(this.level, pos, random, data, ParticleTypes.TRIAL_SPAWNER_DETECTED_PLAYER);
            break;
         case 3014:
            this.level.playLocalSound(pos, SoundEvents.TRIAL_SPAWNER_EJECT_ITEM, SoundSource.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, true);
            TrialSpawner.addEjectItemParticles(this.level, pos, random);
            break;
         case 3015:
            BlockEntity var35 = this.level.getBlockEntity(pos);
            if (var35 instanceof VaultBlockEntity entity) {
               VaultBlockEntity.Client.emitActivationParticles(this.level, entity.getBlockPos(), entity.getBlockState(), entity.getSharedData(), data == 0 ? ParticleTypes.SMALL_FLAME : ParticleTypes.SOUL_FIRE_FLAME);
               this.level.playLocalSound(pos, SoundEvents.VAULT_ACTIVATE, SoundSource.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, true);
            }
            break;
         case 3016:
            VaultBlockEntity.Client.emitDeactivationParticles(this.level, pos, data == 0 ? ParticleTypes.SMALL_FLAME : ParticleTypes.SOUL_FIRE_FLAME);
            this.level.playLocalSound(pos, SoundEvents.VAULT_DEACTIVATE, SoundSource.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, true);
            break;
         case 3017:
            TrialSpawner.addEjectItemParticles(this.level, pos, random);
            break;
         case 3018:
            for(int i = 0; i < 10; ++i) {
               double velocityX = random.nextGaussian() * 0.02D;
               double velocityY = random.nextGaussian() * 0.02D;
               double velocityZ = random.nextGaussian() * 0.02D;
               this.level.addParticle(ParticleTypes.POOF, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + random.nextDouble(), (double)pos.getZ() + random.nextDouble(), velocityX, velocityY, velocityZ);
            }

            this.level.playLocalSound(pos, SoundEvents.COBWEB_PLACE, SoundSource.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, true);
            break;
         case 3019:
            this.level.playLocalSound(pos, SoundEvents.TRIAL_SPAWNER_DETECT_PLAYER, SoundSource.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, true);
            TrialSpawner.addDetectPlayerParticles(this.level, pos, random, data, ParticleTypes.TRIAL_SPAWNER_DETECTED_PLAYER_OMINOUS);
            break;
         case 3020:
            this.level.playLocalSound(pos, SoundEvents.TRIAL_SPAWNER_OMINOUS_ACTIVATE, SoundSource.BLOCKS, data == 0 ? 0.3F : 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, true);
            TrialSpawner.addDetectPlayerParticles(this.level, pos, random, 0, ParticleTypes.TRIAL_SPAWNER_DETECTED_PLAYER_OMINOUS);
            TrialSpawner.addBecomeOminousParticles(this.level, pos, random);
            break;
         case 3021:
            this.level.playLocalSound(pos, SoundEvents.TRIAL_SPAWNER_SPAWN_ITEM, SoundSource.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, true);
            TrialSpawner.addSpawnParticles(this.level, pos, random, TrialSpawner.FlameParticle.decode(data).particleType);
      }

   }

   private void shootParticles(final int data, final BlockPos pos, final RandomSource random, final SimpleParticleType particle) {
      Direction direction = Direction.from3DDataValue(data);
      int normalX = direction.getStepX();
      int normalY = direction.getStepY();
      int normalZ = direction.getStepZ();

      for(int i = 0; i < 10; ++i) {
         double pow = random.nextDouble() * 0.2D + 0.01D;
         double x = (double)pos.getX() + (double)normalX * 0.6D + 0.5D + (double)normalX * 0.01D + (random.nextDouble() - 0.5D) * (double)normalZ * 0.5D;
         double y = (double)pos.getY() + (double)normalY * 0.6D + 0.5D + (double)normalY * 0.01D + (random.nextDouble() - 0.5D) * (double)normalY * 0.5D;
         double z = (double)pos.getZ() + (double)normalZ * 0.6D + 0.5D + (double)normalZ * 0.01D + (random.nextDouble() - 0.5D) * (double)normalX * 0.5D;
         double velocityX = (double)normalX * pow + random.nextGaussian() * 0.01D;
         double velocityY = (double)normalY * pow + random.nextGaussian() * 0.01D;
         double velocityZ = (double)normalZ * pow + random.nextGaussian() * 0.01D;
         this.level.addParticle(particle, x, y, z, velocityX, velocityY, velocityZ);
      }

   }

   private void playJukeboxSong(final Holder songHolder, final BlockPos pos) {
      this.stopJukeboxSong(pos);
      JukeboxSong song = (JukeboxSong)songHolder.value();
      SoundEvent sound = (SoundEvent)song.soundEvent().value();
      SoundInstance instance = SimpleSoundInstance.forJukeboxSong(sound, Vec3.atCenterOf(pos));
      this.playingJukeboxSongs.put(pos, instance);
      this.minecraft.getSoundManager().play(instance);
      this.minecraft.gui.hud.setNowPlaying(song.description());
      this.notifyNearbyEntities(this.level, pos, true);
   }

   private void stopJukeboxSong(final BlockPos pos) {
      SoundInstance removedInstance = (SoundInstance)this.playingJukeboxSongs.remove(pos);
      if (removedInstance != null) {
         this.minecraft.getSoundManager().stop(removedInstance);
      }

   }

   private void stopJukeboxSongAndNotifyNearby(final BlockPos pos) {
      this.stopJukeboxSong(pos);
      this.notifyNearbyEntities(this.level, pos, false);
   }

   private void notifyNearbyEntities(final Level level, final BlockPos pos, final boolean isPlaying) {
      for(LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, (new AABB(pos)).inflate(3.0D))) {
         entity.setRecordPlayingNearby(pos, isPlaying);
      }

   }

   private void potionSplashParticles(final ParticleType particleType, final BlockPos pos, final int data, final RandomSource random) {
      Vec3 particlePos = Vec3.atBottomCenterOf(pos);
      ItemParticleOption breakParticle = new ItemParticleOption(ParticleTypes.ITEM, Items.SPLASH_POTION);

      for(int i = 0; i < 8; ++i) {
         this.level.addParticle(breakParticle, particlePos.x, particlePos.y, particlePos.z, random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D);
      }

      float red = (float)(data >> 16 & 255) / 255.0F;
      float green = (float)(data >> 8 & 255) / 255.0F;
      float blue = (float)(data >> 0 & 255) / 255.0F;

      for(int i = 0; i < 100; ++i) {
         double dist = random.nextDouble() * 4.0D;
         double angle = random.nextDouble() * Math.PI * 2.0D;
         double velocityX = Math.cos(angle) * dist;
         double velocityY = 0.01D + random.nextDouble() * 0.5D;
         double velocityZ = Math.sin(angle) * dist;
         float randomBrightness = 0.75F + random.nextFloat() * 0.25F;
         SpellParticleOption particle = SpellParticleOption.create(particleType, red * randomBrightness, green * randomBrightness, blue * randomBrightness, (float)dist);
         this.level.addParticle(particle, particlePos.x + velocityX * 0.1D, particlePos.y + 0.3D, particlePos.z + velocityZ * 0.1D, velocityX, velocityY, velocityZ);
      }

   }
}
