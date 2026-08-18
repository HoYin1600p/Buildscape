package net.minecraft.network.syncher;

import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Rotations;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.VarInt;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.animal.chicken.ChickenSoundVariant;
import net.minecraft.world.entity.animal.chicken.ChickenVariant;
import net.minecraft.world.entity.animal.cow.CowSoundVariant;
import net.minecraft.world.entity.animal.cow.CowVariant;
import net.minecraft.world.entity.animal.feline.CatSoundVariant;
import net.minecraft.world.entity.animal.feline.CatVariant;
import net.minecraft.world.entity.animal.frog.FrogVariant;
import net.minecraft.world.entity.animal.golem.CopperGolemState;
import net.minecraft.world.entity.animal.nautilus.ZombieNautilusVariant;
import net.minecraft.world.entity.animal.pig.PigSoundVariant;
import net.minecraft.world.entity.animal.pig.PigVariant;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.animal.wolf.WolfSoundVariant;
import net.minecraft.world.entity.animal.wolf.WolfVariant;
import net.minecraft.world.entity.decoration.painting.PaintingVariant;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class EntityDataSerializers {
   private static final CrudeIncrementalIntIdentityHashBiMap SERIALIZERS = CrudeIncrementalIntIdentityHashBiMap.create(16);
   public static final EntityDataSerializer BYTE = EntityDataSerializer.forValueType(ByteBufCodecs.BYTE);
   public static final EntityDataSerializer INT = EntityDataSerializer.forValueType(ByteBufCodecs.VAR_INT);
   public static final EntityDataSerializer LONG = EntityDataSerializer.forValueType(ByteBufCodecs.VAR_LONG);
   public static final EntityDataSerializer FLOAT = EntityDataSerializer.forValueType(ByteBufCodecs.FLOAT);
   public static final EntityDataSerializer STRING = EntityDataSerializer.forValueType(ByteBufCodecs.STRING_UTF8);
   public static final EntityDataSerializer COMPONENT = EntityDataSerializer.forValueType(ComponentSerialization.TRUSTED_STREAM_CODEC);
   public static final EntityDataSerializer OPTIONAL_COMPONENT = EntityDataSerializer.forValueType(ComponentSerialization.TRUSTED_OPTIONAL_STREAM_CODEC);
   public static final EntityDataSerializer ITEM_STACK = new EntityDataSerializer() {
      public StreamCodec codec() {
         return ItemStack.OPTIONAL_STREAM_CODEC;
      }

      public ItemStack copy(final ItemStack value) {
         return value.copy();
      }
   };
   public static final EntityDataSerializer BLOCK_STATE = EntityDataSerializer.forValueType(ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY));
   private static final StreamCodec OPTIONAL_BLOCK_STATE_CODEC = new StreamCodec() {
      public void encode(final ByteBuf output, final Optional value) {
         if (value.isPresent()) {
            VarInt.write(output, Block.getId((BlockState)value.get()));
         } else {
            VarInt.write(output, 0);
         }

      }

      public Optional decode(final ByteBuf input) {
         int id = VarInt.read(input);
         return id == 0 ? Optional.empty() : Optional.of(Block.stateById(id));
      }
   };
   public static final EntityDataSerializer OPTIONAL_BLOCK_STATE = EntityDataSerializer.forValueType(OPTIONAL_BLOCK_STATE_CODEC);
   public static final EntityDataSerializer BOOLEAN = EntityDataSerializer.forValueType(ByteBufCodecs.BOOL);
   public static final EntityDataSerializer PARTICLE = EntityDataSerializer.forValueType(ParticleTypes.STREAM_CODEC);
   public static final EntityDataSerializer PARTICLES = EntityDataSerializer.forValueType(ParticleTypes.STREAM_CODEC.apply(ByteBufCodecs.list()));
   public static final EntityDataSerializer ROTATIONS = EntityDataSerializer.forValueType(Rotations.STREAM_CODEC);
   public static final EntityDataSerializer BLOCK_POS = EntityDataSerializer.forValueType(BlockPos.STREAM_CODEC);
   public static final EntityDataSerializer OPTIONAL_BLOCK_POS = EntityDataSerializer.forValueType(BlockPos.STREAM_CODEC.apply(ByteBufCodecs::optional));
   public static final EntityDataSerializer DIRECTION = EntityDataSerializer.forValueType(Direction.STREAM_CODEC);
   public static final EntityDataSerializer OPTIONAL_LIVING_ENTITY_REFERENCE = EntityDataSerializer.forValueType(EntityReference.streamCodec().apply(ByteBufCodecs::optional));
   public static final EntityDataSerializer OPTIONAL_GLOBAL_POS = EntityDataSerializer.forValueType(GlobalPos.STREAM_CODEC.apply(ByteBufCodecs::optional));
   public static final EntityDataSerializer VILLAGER_DATA = EntityDataSerializer.forValueType(VillagerData.STREAM_CODEC);
   private static final StreamCodec OPTIONAL_UNSIGNED_INT_CODEC = new StreamCodec() {
      public OptionalInt decode(final ByteBuf input) {
         int v = VarInt.read(input);
         return v == 0 ? OptionalInt.empty() : OptionalInt.of(v - 1);
      }

      public void encode(final ByteBuf output, final OptionalInt value) {
         VarInt.write(output, value.orElse(-1) + 1);
      }
   };
   public static final EntityDataSerializer OPTIONAL_UNSIGNED_INT = EntityDataSerializer.forValueType(OPTIONAL_UNSIGNED_INT_CODEC);
   public static final EntityDataSerializer POSE = EntityDataSerializer.forValueType(Pose.STREAM_CODEC);
   public static final EntityDataSerializer CAT_VARIANT = EntityDataSerializer.forValueType(CatVariant.STREAM_CODEC);
   public static final EntityDataSerializer CAT_SOUND_VARIANT = EntityDataSerializer.forValueType(CatSoundVariant.STREAM_CODEC);
   public static final EntityDataSerializer CHICKEN_VARIANT = EntityDataSerializer.forValueType(ChickenVariant.STREAM_CODEC);
   public static final EntityDataSerializer CHICKEN_SOUND_VARIANT = EntityDataSerializer.forValueType(ChickenSoundVariant.STREAM_CODEC);
   public static final EntityDataSerializer COW_VARIANT = EntityDataSerializer.forValueType(CowVariant.STREAM_CODEC);
   public static final EntityDataSerializer COW_SOUND_VARIANT = EntityDataSerializer.forValueType(CowSoundVariant.STREAM_CODEC);
   public static final EntityDataSerializer WOLF_VARIANT = EntityDataSerializer.forValueType(WolfVariant.STREAM_CODEC);
   public static final EntityDataSerializer WOLF_SOUND_VARIANT = EntityDataSerializer.forValueType(WolfSoundVariant.STREAM_CODEC);
   public static final EntityDataSerializer FROG_VARIANT = EntityDataSerializer.forValueType(FrogVariant.STREAM_CODEC);
   public static final EntityDataSerializer PIG_VARIANT = EntityDataSerializer.forValueType(PigVariant.STREAM_CODEC);
   public static final EntityDataSerializer PIG_SOUND_VARIANT = EntityDataSerializer.forValueType(PigSoundVariant.STREAM_CODEC);
   public static final EntityDataSerializer ZOMBIE_NAUTILUS_VARIANT = EntityDataSerializer.forValueType(ZombieNautilusVariant.STREAM_CODEC);
   public static final EntityDataSerializer PAINTING_VARIANT = EntityDataSerializer.forValueType(PaintingVariant.STREAM_CODEC);
   public static final EntityDataSerializer ARMADILLO_STATE = EntityDataSerializer.forValueType(Armadillo.ArmadilloState.STREAM_CODEC);
   public static final EntityDataSerializer SNIFFER_STATE = EntityDataSerializer.forValueType(Sniffer.State.STREAM_CODEC);
   public static final EntityDataSerializer WEATHERING_COPPER_STATE = EntityDataSerializer.forValueType(WeatheringCopper.WeatherState.STREAM_CODEC);
   public static final EntityDataSerializer COPPER_GOLEM_STATE = EntityDataSerializer.forValueType(CopperGolemState.STREAM_CODEC);
   public static final EntityDataSerializer VECTOR3 = EntityDataSerializer.forValueType(ByteBufCodecs.VECTOR3F);
   public static final EntityDataSerializer QUATERNION = EntityDataSerializer.forValueType(ByteBufCodecs.QUATERNIONF);
   public static final EntityDataSerializer RESOLVABLE_PROFILE = EntityDataSerializer.forValueType(ResolvableProfile.STREAM_CODEC);
   public static final EntityDataSerializer HUMANOID_ARM = EntityDataSerializer.forValueType(HumanoidArm.STREAM_CODEC);
   public static final EntityDataSerializer DYE_COLOR = EntityDataSerializer.forValueType(DyeColor.STREAM_CODEC);

   public static void registerSerializer(final EntityDataSerializer serializer) {
      SERIALIZERS.add(serializer);
   }

   public static @Nullable EntityDataSerializer getSerializer(final int id) {
      return (EntityDataSerializer)SERIALIZERS.byId(id);
   }

   public static int getSerializedId(final EntityDataSerializer serializer) {
      return SERIALIZERS.getId(serializer);
   }

   private EntityDataSerializers() {
   }

   static {
      registerSerializer(BYTE);
      registerSerializer(INT);
      registerSerializer(LONG);
      registerSerializer(FLOAT);
      registerSerializer(STRING);
      registerSerializer(COMPONENT);
      registerSerializer(OPTIONAL_COMPONENT);
      registerSerializer(ITEM_STACK);
      registerSerializer(BOOLEAN);
      registerSerializer(ROTATIONS);
      registerSerializer(BLOCK_POS);
      registerSerializer(OPTIONAL_BLOCK_POS);
      registerSerializer(DIRECTION);
      registerSerializer(OPTIONAL_LIVING_ENTITY_REFERENCE);
      registerSerializer(BLOCK_STATE);
      registerSerializer(OPTIONAL_BLOCK_STATE);
      registerSerializer(PARTICLE);
      registerSerializer(PARTICLES);
      registerSerializer(VILLAGER_DATA);
      registerSerializer(OPTIONAL_UNSIGNED_INT);
      registerSerializer(POSE);
      registerSerializer(CAT_VARIANT);
      registerSerializer(CAT_SOUND_VARIANT);
      registerSerializer(COW_VARIANT);
      registerSerializer(COW_SOUND_VARIANT);
      registerSerializer(WOLF_VARIANT);
      registerSerializer(WOLF_SOUND_VARIANT);
      registerSerializer(FROG_VARIANT);
      registerSerializer(PIG_VARIANT);
      registerSerializer(PIG_SOUND_VARIANT);
      registerSerializer(CHICKEN_VARIANT);
      registerSerializer(CHICKEN_SOUND_VARIANT);
      registerSerializer(ZOMBIE_NAUTILUS_VARIANT);
      registerSerializer(OPTIONAL_GLOBAL_POS);
      registerSerializer(PAINTING_VARIANT);
      registerSerializer(SNIFFER_STATE);
      registerSerializer(ARMADILLO_STATE);
      registerSerializer(COPPER_GOLEM_STATE);
      registerSerializer(WEATHERING_COPPER_STATE);
      registerSerializer(VECTOR3);
      registerSerializer(QUATERNION);
      registerSerializer(RESOLVABLE_PROFILE);
      registerSerializer(HUMANOID_ARM);
      registerSerializer(DYE_COLOR);
   }
}
