package net.minecraft.network.syncher;

import com.mojang.logging.LogUtils;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.ClassTreeIdRegistry;
import org.apache.commons.lang3.ObjectUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class SynchedEntityData {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_ID_VALUE = 254;
   private static final ClassTreeIdRegistry ID_REGISTRY = new ClassTreeIdRegistry();
   private final SyncedDataHolder entity;
   private final SynchedEntityData.DataItem[] itemsById;
   private boolean isDirty;

   private SynchedEntityData(final SyncedDataHolder entity, final SynchedEntityData.DataItem[] itemsById) {
      this.entity = entity;
      this.itemsById = itemsById;
   }

   public static EntityDataAccessor defineId(final Class clazz, final EntityDataSerializer type) {
      if (LOGGER.isDebugEnabled()) {
         try {
            Class aClass = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
            if (!aClass.equals(clazz)) {
               LOGGER.debug("defineId called for: {} from {}", new Object[]{clazz, aClass, new RuntimeException()});
            }
         } catch (ClassNotFoundException var3) {
         }
      }

      int id = ID_REGISTRY.define(clazz);
      if (id > 254) {
         throw new IllegalArgumentException("Data value id is too big with " + id + "! (Max is 254)");
      } else {
         return type.createAccessor(id);
      }
   }

   private SynchedEntityData.DataItem getItem(final EntityDataAccessor accessor) {
      return this.itemsById[accessor.id()];
   }

   public Object get(final EntityDataAccessor accessor) {
      return this.getItem(accessor).getValue();
   }

   public void set(final EntityDataAccessor accessor, final Object value) {
      this.set(accessor, value, false);
   }

   public void set(final EntityDataAccessor accessor, final Object value, final boolean forceDirty) {
      SynchedEntityData.DataItem dataItem = this.getItem(accessor);
      if (forceDirty || ObjectUtils.notEqual(value, dataItem.getValue())) {
         dataItem.setValue(value);
         this.entity.onSyncedDataUpdated(accessor);
         dataItem.setDirty(true);
         this.isDirty = true;
      }

   }

   public boolean isDirty() {
      return this.isDirty;
   }

   public @Nullable List packDirty() {
      if (!this.isDirty) {
         return null;
      } else {
         this.isDirty = false;
         List result = new ArrayList();

         for(SynchedEntityData.DataItem dataItem : this.itemsById) {
            if (dataItem.isDirty()) {
               dataItem.setDirty(false);
               result.add(dataItem.value());
            }
         }

         return result;
      }
   }

   public @Nullable List getNonDefaultValues() {
      List result = null;

      for(SynchedEntityData.DataItem dataItem : this.itemsById) {
         if (!dataItem.isSetToDefault()) {
            if (result == null) {
               result = new ArrayList();
            }

            result.add(dataItem.value());
         }
      }

      return result;
   }

   public void assignValues(final List items) {
      for(SynchedEntityData.DataValue item : items) {
         SynchedEntityData.DataItem dataItem = this.itemsById[item.id];
         this.assignValue(dataItem, item);
         this.entity.onSyncedDataUpdated(dataItem.getAccessor());
      }

      this.entity.onSyncedDataUpdated(items);
   }

   private void assignValue(final SynchedEntityData.DataItem dataItem, final SynchedEntityData.DataValue item) {
      if (!Objects.equals(item.serializer(), dataItem.accessor.serializer())) {
         throw new IllegalStateException(String.format(Locale.ROOT, "Invalid entity data item type for field %d on entity %s: old=%s(%s), new=%s(%s)", dataItem.accessor.id(), this.entity, dataItem.value, dataItem.value.getClass(), item.value, item.value.getClass()));
      } else {
         dataItem.setValue(item.value);
      }
   }

   public static class Builder {
      private final SyncedDataHolder entity;
      private final @Nullable SynchedEntityData.DataItem[] itemsById;

      public Builder(final SyncedDataHolder entity) {
         this.entity = entity;
         this.itemsById = new SynchedEntityData.DataItem[SynchedEntityData.ID_REGISTRY.getCount(entity.getClass())];
      }

      public SynchedEntityData.Builder define(final EntityDataAccessor accessor, final Object value) {
         int id = accessor.id();
         if (id >= this.itemsById.length) {
            throw new IllegalArgumentException("Data value id is too big with " + id + "! (Max is " + (this.itemsById.length - 1) + ")");
         } else if (this.itemsById[id] != null) {
            throw new IllegalArgumentException("Duplicate id value for " + id + "!");
         } else if (EntityDataSerializers.getSerializedId(accessor.serializer()) < 0) {
            throw new IllegalArgumentException("Unregistered serializer " + String.valueOf(accessor.serializer()) + " for " + id + "!");
         } else {
            this.itemsById[accessor.id()] = new SynchedEntityData.DataItem(accessor, value);
            return this;
         }
      }

      public SynchedEntityData build() {
         for(int i = 0; i < this.itemsById.length; ++i) {
            if (this.itemsById[i] == null) {
               throw new IllegalStateException("Entity " + String.valueOf(this.entity.getClass()) + " has not defined synched data value " + i);
            }
         }

         return new SynchedEntityData(this.entity, this.itemsById);
      }
   }

   public static class DataItem {
      private final EntityDataAccessor accessor;
      private Object value;
      private final Object initialValue;
      private boolean dirty;

      public DataItem(final EntityDataAccessor accessor, final Object initialValue) {
         this.accessor = accessor;
         this.initialValue = initialValue;
         this.value = initialValue;
      }

      public EntityDataAccessor getAccessor() {
         return this.accessor;
      }

      public void setValue(final Object value) {
         this.value = value;
      }

      public Object getValue() {
         return this.value;
      }

      public boolean isDirty() {
         return this.dirty;
      }

      public void setDirty(final boolean dirty) {
         this.dirty = dirty;
      }

      public boolean isSetToDefault() {
         return this.initialValue.equals(this.value);
      }

      public SynchedEntityData.DataValue value() {
         return SynchedEntityData.DataValue.create(this.accessor, this.value);
      }
   }

   public static record DataValue(int id, EntityDataSerializer serializer, Object value) {
      public static SynchedEntityData.DataValue create(final EntityDataAccessor accessor, final Object value) {
         EntityDataSerializer serializer = accessor.serializer();
         return new SynchedEntityData.DataValue(accessor.id(), serializer, serializer.copy(value));
      }

      public void write(final RegistryFriendlyByteBuf output) {
         int serializerId = EntityDataSerializers.getSerializedId(this.serializer);
         if (serializerId < 0) {
            throw new EncoderException("Unknown serializer type " + String.valueOf(this.serializer));
         } else {
            output.writeByte(this.id);
            output.writeVarInt(serializerId);
            this.serializer.codec().encode(output, this.value);
         }
      }

      public static SynchedEntityData.DataValue read(final RegistryFriendlyByteBuf input, final int id) {
         int type = input.readVarInt();
         EntityDataSerializer serializer = EntityDataSerializers.getSerializer(type);
         if (serializer == null) {
            throw new DecoderException("Unknown serializer type " + type);
         } else {
            return read(input, id, serializer);
         }
      }

      private static SynchedEntityData.DataValue read(final RegistryFriendlyByteBuf input, final int id, final EntityDataSerializer serializer) {
         return new SynchedEntityData.DataValue(id, serializer, serializer.codec().decode(input));
      }
   }
}
