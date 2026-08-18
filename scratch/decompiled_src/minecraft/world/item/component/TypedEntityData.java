package net.minecraft.world.item.component;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import org.slf4j.Logger;

public final class TypedEntityData implements TooltipProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String TYPE_TAG = "id";
   private final Object type;
   private final CompoundTag tag;

   public static Codec codec(final Codec typeCodec) {
      return new Codec() {
         public DataResult decode(final DynamicOps ops, final Object input) {
            return CustomData.COMPOUND_TAG_CODEC.decode(ops, input).flatMap((pair) -> {
               CompoundTag tagWithoutType = ((CompoundTag)pair.getFirst()).copy();
               Tag typeTag = tagWithoutType.remove("id");
               return typeTag == null ? DataResult.error(() -> "Expected 'id' field in " + String.valueOf(input)) : typeCodec.parse(asNbtOps(ops), typeTag).map((type) -> Pair.of(new TypedEntityData(type, tagWithoutType), pair.getSecond()));
            });
         }

         public DataResult encode(final TypedEntityData input, final DynamicOps ops, final Object prefix) {
            return typeCodec.encodeStart(asNbtOps(ops), input.type).flatMap((typeTag) -> {
               CompoundTag tag = input.tag.copy();
               tag.put("id", typeTag);
               return CustomData.COMPOUND_TAG_CODEC.encode(tag, ops, prefix);
            });
         }

         private static DynamicOps asNbtOps(final DynamicOps ops) {
            if (ops instanceof RegistryOps registryOps) {
               return registryOps.withParent(NbtOps.INSTANCE);
            } else {
               return NbtOps.INSTANCE;
            }
         }
      };
   }

   public static StreamCodec streamCodec(final StreamCodec typeCodec) {
      return StreamCodec.composite(typeCodec, TypedEntityData::type, ByteBufCodecs.COMPOUND_TAG, TypedEntityData::tag, TypedEntityData::new);
   }

   private TypedEntityData(final Object type, final CompoundTag data) {
      this.type = type;
      this.tag = stripId(data);
   }

   public static TypedEntityData of(final Object type, final CompoundTag data) {
      return new TypedEntityData(type, data);
   }

   private static CompoundTag stripId(final CompoundTag tag) {
      if (tag.contains("id")) {
         CompoundTag copy = tag.copy();
         copy.remove("id");
         return copy;
      } else {
         return tag;
      }
   }

   public Object type() {
      return this.type;
   }

   public boolean contains(final String name) {
      return this.tag.contains(name);
   }

   public boolean equals(final Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof TypedEntityData)) {
         return false;
      } else {
         TypedEntityData customData = (TypedEntityData)obj;
         return this.type == customData.type && this.tag.equals(customData.tag);
      }
   }

   public int hashCode() {
      return 31 * this.type.hashCode() + this.tag.hashCode();
   }

   public String toString() {
      return String.valueOf(this.type) + " " + String.valueOf(this.tag);
   }

   public void loadInto(final Entity entity) {
      try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(entity.problemPath(), LOGGER)) {
         TagValueOutput output = TagValueOutput.createWithContext(reporter, entity.registryAccess());
         entity.saveWithoutId(output);
         CompoundTag entityData = output.buildResult();
         UUID uuid = entity.getUUID();
         entityData.merge(this.getUnsafe());
         entity.load(TagValueInput.create(reporter, entity.registryAccess(), entityData));
         entity.setUUID(uuid);
      }

   }

   public boolean loadInto(final SpawnData spawnData, final DefaultedRegistry registry) {
      CompoundTag entityTag = spawnData.getEntityToSpawn();
      CompoundTag oldTag = entityTag.copy();
      spawnData.getEntityToSpawn().putString("id", registry.getKey(this.type()).toString());
      entityTag.merge(this.getUnsafe());
      return !entityTag.equals(oldTag);
   }

   public boolean loadInto(final BlockEntity blockEntity, final HolderLookup.Provider registries) {
      try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(blockEntity.problemPath(), LOGGER)) {
         TagValueOutput output = TagValueOutput.createWithContext(reporter, registries);
         blockEntity.saveCustomOnly(output);
         CompoundTag entityTag = output.buildResult();
         CompoundTag oldTag = entityTag.copy();
         entityTag.merge(this.getUnsafe());
         if (!entityTag.equals(oldTag)) {
            try {
               blockEntity.loadCustomOnly(TagValueInput.create(reporter, registries, entityTag));
               blockEntity.setChanged();
               return true;
            } catch (Exception var11) {
               LOGGER.warn("Failed to apply custom data to block entity at {}", blockEntity.getBlockPos(), var11);

               try {
                  blockEntity.loadCustomOnly(TagValueInput.create(reporter.forChild(() -> "(rollback)"), registries, oldTag));
               } catch (Exception var10) {
                  LOGGER.warn("Failed to rollback block entity at {} after failure", blockEntity.getBlockPos(), var10);
               }
            }
         }

         return false;
      }
   }

   private CompoundTag tag() {
      return this.tag;
   }

   /** @deprecated */
   @Deprecated
   public CompoundTag getUnsafe() {
      return this.tag;
   }

   public CompoundTag copyTagWithoutId() {
      return this.tag.copy();
   }

   public void addToTooltip(final Item.TooltipContext context, final Consumer consumer, final TooltipFlag flag, final DataComponentGetter components) {
      if (this.type.getClass() == EntityType.class) {
         EntityType type = (EntityType)this.type;
         if (context.isPeaceful() && !type.isAllowedInPeaceful()) {
            consumer.accept(Component.translatable("item.spawn_egg.peaceful").withStyle(ChatFormatting.RED));
         }
      }

   }
}
